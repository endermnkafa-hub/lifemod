package net.mcreator.lifemod.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.lifemod.network.LifeModModVariables;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerModelHider {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player instanceof AbstractClientPlayer clientPlayer) {
            double gender = player.getCapability(LifeModModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                    .orElse(new LifeModModVariables.PlayerVariables()).gender;

            if (gender != 0) {
                PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
                
                model.body.visible = false;
                model.rightArm.visible = false;
                model.leftArm.visible = false;
                model.rightLeg.visible = false;
                model.leftLeg.visible = false;

                model.jacket.visible = false;
                model.rightSleeve.visible = false;
                model.leftSleeve.visible = false;
                model.rightPants.visible = false;
                model.leftPants.visible = false;

                model.head.visible = true;
                model.hat.visible = true;
                
                renderCustomModel(clientPlayer, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), gender == 1);
            }
        }
    }

    private static void renderCustomModel(AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean isMale) {
        // GeckoLib render tetikleyicisi
    }
}
