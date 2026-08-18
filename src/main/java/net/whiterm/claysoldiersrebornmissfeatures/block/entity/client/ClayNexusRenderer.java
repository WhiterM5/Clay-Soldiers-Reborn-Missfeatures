package net.whiterm.claysoldiersrebornmissfeatures.block.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class ClayNexusRenderer extends GeoBlockRenderer<ClayNexusBlockEntity> {
    public ClayNexusRenderer(BlockEntityRendererFactory.Context context) {
        super(new ClayNexusModel());
        addRenderLayer(new ClayNexusRenderLayer(this));
    }
}
