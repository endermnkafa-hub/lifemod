package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class PlayerCustomRenderer extends GeoObjectRenderer<PlayerModelAnimatable> {

    private final boolean isMale;

    public PlayerCustomRenderer(boolean isMale) {
        super(new PlayerCustomModel(isMale));
        this.isMale = isMale;
    }

    @Override
    public void render(
            PlayerModelAnimatable animatable,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {

        animatable.updateMovement(partialTick);

        poseStack.pushPose();

        /*
         * Minecraft oyuncu modeli yaklaşık 1.8 blok yüksekliğindedir.
         * Geo modelinin pivotuna göre küçük bir dikey düzeltme.
         */
        poseStack.translate(0.0D, 0.0D, 0.0D);

        super.render(
                animatable,
                partialTick,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }
}