package net.whiterm.claysoldiersrebornmissfeatures.block.entity.client;

import net.minecraft.util.Identifier;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class ClayNexusModel extends GeoModel<ClayNexusBlockEntity> {
    @Override
    public Identifier getModelResource(ClayNexusBlockEntity animatable) {
        return new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "geo/clay_nexus.geo.json");
    }

    @Override
    public Identifier getTextureResource(ClayNexusBlockEntity animatable) {
        return new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "textures/block/clay_nexus.png");
    }

    @Override
    public Identifier getAnimationResource(ClayNexusBlockEntity animatable) {
        return new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "animations/clay_nexus.animation.json");
    }
}
