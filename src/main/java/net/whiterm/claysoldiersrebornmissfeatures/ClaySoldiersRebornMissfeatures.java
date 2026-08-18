package net.whiterm.claysoldiersrebornmissfeatures;

import net.fabricmc.api.ModInitializer;

import net.whiterm.claysoldiersrebornmissfeatures.block.ModBlocks;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ModBlockEntities;
import net.whiterm.claysoldiersrebornmissfeatures.config.ModConfig;
import net.whiterm.claysoldiersrebornmissfeatures.event.ModEvents;
import net.whiterm.claysoldiersrebornmissfeatures.item.ModItems;
import net.whiterm.claysoldiersrebornmissfeatures.network.ModPackets;
import net.whiterm.claysoldiersrebornmissfeatures.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class ClaySoldiersRebornMissfeatures implements ModInitializer {
	public static final String MOD_ID = "claysoldiersmissf";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModConfig.registerConfigs();

        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
        ModEvents.registerEvents();

        GeckoLib.initialize();

        ModPackets.registerC2SPackets();
	}
}