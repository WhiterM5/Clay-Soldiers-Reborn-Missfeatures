package net.whiterm.claysoldiersrebornmissfeatures.block.entity.client;

import com.matthewperiut.clay.util.ClientInfoStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ClayNexusRenderLayer extends GeoRenderLayer<ClayNexusBlockEntity> {
    public ClayNexusRenderLayer(GeoRenderer<ClayNexusBlockEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack poseStack, ClayNexusBlockEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        //super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        Identifier overlay = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "textures/block/clay_nexus_team_color_layer.png");
        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(overlay);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderLayer);
        int[] rgb = getTeamColor(animatable);

        poseStack.push(); //writes current "pos etc" and after following line if you add another layer everithing will be set to 0 0 0 again
        poseStack.translate(0.5f, 0.0001f, 0.5f);
        getRenderer().reRender(
                getDefaultBakedModel(animatable),
                poseStack,
                bufferSource,
                animatable,
                renderLayer,
                vertexConsumer,
                partialTick,
                packedLight,
                OverlayTexture.DEFAULT_UV,
                rgb[0] / 255f,
                rgb[1] / 255f,
                rgb[2] / 255f,
                1f
        );
        poseStack.pop(); //sets poseStack to the saved thing (push())
    }

    public int[] getTeamColor(ClayNexusBlockEntity animatable) {
        Item soldierItem = animatable.getSoldierItem();
        if (soldierItem == Items.AIR || soldierItem == null) return new int[] {255, 255, 255};
        int[] rgb = new int[3];
        int color = 00000000;

        Text soldierItemName = soldierItem.getName();
        for (ClientInfoStorage.ColoredItemDataBundle coloredItemBundle : ClientInfoStorage.coloredItemDataBundleList) {
            Text coloredItemName = coloredItemBundle.item.get().getName();
            if (soldierItemName.equals(coloredItemName)) {
                color = coloredItemBundle.color;
            }
        }
        rgb[0] = ColorHelper.Argb.getRed(color);
        rgb[1] = ColorHelper.Argb.getGreen(color);
        rgb[2] = ColorHelper.Argb.getBlue(color);

        return rgb;
    }
}
