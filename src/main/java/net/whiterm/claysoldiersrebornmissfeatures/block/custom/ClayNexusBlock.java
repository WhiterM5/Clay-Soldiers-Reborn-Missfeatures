package net.whiterm.claysoldiersrebornmissfeatures.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ClayNexusBlock extends BlockWithEntity implements BlockEntityProvider {
    public ClayNexusBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClayNexusBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ClayNexusBlockEntity blockEntity1) {
                ItemScatterer.spawn(world, pos, blockEntity1);
                world.updateComparators(pos,this);
                blockEntity1.killHealthBar(world);
                blockEntity1.dropDroppedSoldiers(world, pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient()) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ClayNexusBlockEntity blockEntity1) {
            //------Create Health Bar------
            blockEntity1.createHealthBar((ServerWorld) world, pos, world.getServer());
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((ClayNexusBlockEntity) world.getBlockEntity(pos));
            ClayNexusBlockEntity blockEntity = (ClayNexusBlockEntity) world.getBlockEntity(pos);

            if (screenHandlerFactory != null && !player.isSneaking()) {
                player.openHandledScreen(screenHandlerFactory);
            }

            if (player.isSneaking()) {
                assert blockEntity != null;
                if (blockEntity.getNexusHealth() > 0) {
                    blockEntity.setActiveState();
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) return;

        boolean isPowered = world.getReceivedRedstonePower(pos) > 0; //world.isReceivingRedstonePower(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof ClayNexusBlockEntity blockEntity1) {
            boolean blockEntityActive = blockEntity1.getRedstoneActive();
            if (isPowered && !blockEntityActive) {
                blockEntity1.setActiveState();
                blockEntity1.setRedstoneActive(true);
            } else if (!isPowered) {
                blockEntity1.setRedstoneActive(false);
            }
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CLAY_NEXUS_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
