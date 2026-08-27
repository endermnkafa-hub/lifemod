package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class PlayerCustomRenderer extends GeoObjectRenderer<PlayerModelAnimatable> {

    public PlayerCustomRenderer(boolean isMale) {
        super(new PlayerCustomModel(isMale));
    }

    public void renderModel(
            PlayerModelAnimatable animatable,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            float partialTick
    ) {
        animatable.updateMovement(partialTick);

        super.render(
                poseStack,
                animatable,
                bufferSource,
                RenderTypeFor(animatable),
                packedLight,
                packedOverlay
        );
    }

    private net.minecraft.client.renderer.RenderType RenderTypeFor(
            PlayerModelAnimatable animatable
    ) {
        ResourceLocation texture =
                animatable.isMale()
                        ? new ResourceLocation(
                                "life_mod",
                                "textures/entity/custom_male.png"
                        )
                        : new ResourceLocation(
                                "life_mod",
                                "textures/entity/custom_female.png"
                        );

        return net.minecraft.client.renderer.RenderType.entityTranslucent(texture);
    }
}