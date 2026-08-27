package net.mcreator.lifemod.client;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PlayerCustomModel extends GeoModel<PlayerModelAnimatable> {

    private final boolean isMale;

    public PlayerCustomModel(boolean isMale) {
        this.isMale = isMale;
    }

    @Override
    public ResourceLocation getModelResource(PlayerModelAnimatable animatable) {

        return new ResourceLocation(
                "life_mod",
                isMale
                        ? "geo/custom_male.geo.json"
                        : "geo/custom_female.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(PlayerModelAnimatable animatable) {

        return new ResourceLocation(
                "life_mod",
                isMale
                        ? "textures/entity/custom_male.png"
                        : "textures/entity/custom_female.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(PlayerModelAnimatable animatable) {

        return new ResourceLocation(
                "life_mod",
                isMale
                        ? "animations/male.animation.json"
                        : "animations/female.animation.json"
        );
    }
}