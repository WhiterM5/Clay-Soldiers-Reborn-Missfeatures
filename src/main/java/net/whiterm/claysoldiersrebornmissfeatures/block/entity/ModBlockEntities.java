package net.whiterm.claysoldiersrebornmissfeatures.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.ModBlocks;

public class ModBlockEntities {
    //"clay_nexus_be" - clay nexus block entity
    public static final BlockEntityType<ClayNexusBlockEntity> CLAY_NEXUS_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "clay_nexus_be"),
                    FabricBlockEntityTypeBuilder.create(ClayNexusBlockEntity::new,
                            ModBlocks.CLAY_NEXUS).build());

    public static void registerBlockEntities(){
        ClaySoldiersRebornMissfeatures.LOGGER.info("Registering block entites for " + ClaySoldiersRebornMissfeatures.MOD_ID);
    }
}
