package net.whiterm.claysoldiersrebornmissfeatures;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.whiterm.claysoldiersrebornmissfeatures.block.ModBlocks;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ModBlockEntities;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.client.ClayNexusRenderer;
import net.whiterm.claysoldiersrebornmissfeatures.screen.ClayNexusScreen;
import net.whiterm.claysoldiersrebornmissfeatures.screen.ModScreenHandlers;

public class ClaySoldiersRebornMissfeaturesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.CLAY_NEXUS_SCREEN_HANDLER, ClayNexusScreen::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CLAY_NEXUS_BLOCK_ENTITY,
                ClayNexusRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CLAY_NEXUS, RenderLayer.getCutoutMipped());
    }
}
