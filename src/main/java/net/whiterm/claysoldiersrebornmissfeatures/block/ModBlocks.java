package net.whiterm.claysoldiersrebornmissfeatures.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.custom.ClayNexusBlock;

public class ModBlocks {
    //private static final Block NAME = registerBlock("name",
    //       new Block(FabricBlockSettings.copyOf(Blocks.BRAIN_CORAL_BLOCK)));

    public static final Block CLAY_NEXUS = registerBlock("clay_nexus",
                   new ClayNexusBlock(FabricBlockSettings.create()
                           .mapColor(MapColor.BLACK).instrument(Instrument.BASEDRUM)
                           .strength(1.0f, 1200.0f).breakInstantly()
                           .nonOpaque().noCollision()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, name),
                block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        ClaySoldiersRebornMissfeatures.LOGGER.info("Registering modded blocks for " + ClaySoldiersRebornMissfeatures.MOD_ID);
    }
}
