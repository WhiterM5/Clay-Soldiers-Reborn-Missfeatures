package net.whiterm.claysoldiersrebornmissfeatures.block.entity.client;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ClayNexusRenderer extends GeoBlockRenderer<ClayNexusBlockEntity> {
    public ClayNexusRenderer(BlockEntityRendererFactory.Context context) {
        super(new ClayNexusModel());
        addRenderLayer(new ClayNexusRenderLayer(this));
    }
}
