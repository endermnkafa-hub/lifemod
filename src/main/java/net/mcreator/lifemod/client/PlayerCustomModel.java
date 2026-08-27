package net.mcreator.lifemod.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlayerCustomModel extends GeoModel<PlayerModelAnimatable> {
    private final ResourceLocation modelResource;
    private final ResourceLocation textureResource;

    public PlayerCustomModel(boolean isMale) {
        this.modelResource = new ResourceLocation("life_mod", isMale ? "geo/male.geo.json" : "geo/female.geo.json");
        this.textureResource = new ResourceLocation("life_mod", "textures/entity/" + (isMale ? "male.png" : "female.png"));
    }

    @Override
    public ResourceLocation getModelResource(PlayerModelAnimatable animatable) { return modelResource; }
    @Override
    public ResourceLocation getTextureResource(PlayerModelAnimatable animatable) { return textureResource; }
    @Override
    public ResourceLocation getAnimationResource(PlayerModelAnimatable animatable) { return new ResourceLocation("life_mod", "animations/" + (animatable.isMale() ? "male.animation.json" : "female.animation.json")); }
}
