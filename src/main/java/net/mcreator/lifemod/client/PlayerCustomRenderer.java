package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class PlayerCustomRenderer extends GeoObjectRenderer<PlayerModelAnimatable> {

    private final boolean male;

    public PlayerCustomRenderer(boolean male) {
        super(new PlayerCustomModel(male));
        this.male = male;
    }

    public void renderPlayerModel(
            PlayerModelAnimatable animatable,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            float partialTick
    ) {
        animatable.updateMovement(partialTick);

        ResourceLocation texture = male
                ? new ResourceLocation(
                        "life_mod",
                        "textures/entity/custom_male.png"
                )
                : new ResourceLocation(
                        "life_mod",
                        "textures/entity/custom_female.png"
                );

        RenderType renderType = RenderType.entityTranslucent(texture);

        VertexConsumer vertexConsumer =
                bufferSource.getBuffer(renderType);

        super.render(
                poseStack,
                animatable,
                bufferSource,
                renderType,
                vertexConsumer,
                packedLight
        );
    }
}