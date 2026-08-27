package net.mcreator.lifemod.client;

import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;

public class PlayerCustomModel
        extends GeoModel<PlayerModelAnimatable> {

    private final boolean male;

    public PlayerCustomModel(boolean male) {
        this.male = male;
    }

    @Override
    public ResourceLocation getModelResource(
            PlayerModelAnimatable animatable
    ) {

        if (male) {

            return new ResourceLocation(
                    "life_mod",
                    "geo/custom_male.geo.json"
            );

        }

        return new ResourceLocation(
                "life_mod",
                "geo/custom_female.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(
            PlayerModelAnimatable animatable
    ) {

        if (male) {

            return new ResourceLocation(
                    "life_mod",
                    "textures/entity/custom_male.png"
            );

        }

        return new ResourceLocation(
                "life_mod",
                "textures/entity/custom_female.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(
            PlayerModelAnimatable animatable
    ) {

        if (male) {

            return new ResourceLocation(
                    "life_mod",
                    "animations/male.animation.json"
            );

        }

        return new ResourceLocation(
                "life_mod",
                "animations/female.animation.json"
        );
    }
}