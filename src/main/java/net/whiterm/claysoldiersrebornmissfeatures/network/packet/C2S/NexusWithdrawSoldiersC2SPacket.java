package net.whiterm.claysoldiersrebornmissfeatures.network.packet.C2S;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NexusWithdrawSoldiersC2SPacket {
    private static final Logger log = LogManager.getLogger(NexusWithdrawSoldiersC2SPacket.class);

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();

        server.execute(() -> {
            ServerWorld world = player.getServerWorld();

            ClayNexusBlockEntity blockEntity = ((ClayNexusBlockEntity) world.getBlockEntity(pos));
            assert blockEntity != null;
            try {
                blockEntity.withdrawDroppedSoldiers(player);
            } catch (CommandSyntaxException e) {
                log.error("CommandSyntaxException: ", e);
            }
        });
    }
}
