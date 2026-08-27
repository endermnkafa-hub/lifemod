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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerModelHider {

    private static final Map<UUID, PlayerModelAnimatable> ANIMATABLES =
            new ConcurrentHashMap<>();

    private static final PlayerCustomRenderer MALE_RENDERER =
            new PlayerCustomRenderer(true);

    private static final PlayerCustomRenderer FEMALE_RENDERER =
            new PlayerCustomRenderer(false);

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {

        Player player = event.getEntity();

        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }

        double gender = player
                .getCapability(
                        LifeModModVariables.PLAYER_VARIABLES_CAPABILITY,
                        null
                )
                .orElse(
                        new LifeModModVariables.PlayerVariables()
                )
                .gender;

        /*
         * gender = 0:
         * Oyuncunun normal Minecraft modeli kullanılmaya devam eder.
         */
        if (gender == 0) {
            return;
        }

        /*
         * Vanilla oyuncu modelini gizle.
         */
        PlayerModel<AbstractClientPlayer> model =
                event.getRenderer().getModel();

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

        /*
         * Custom model kendi kafasını/gövdesini çizdiği için
         * vanilla kafa da gizleniyor.
         */
        model.head.visible = false;
        model.hat.visible = false;

        /*
         * Oyuncuya ait animatable nesneyi al.
         */
        UUID uuid = clientPlayer.getUUID();

        boolean isMale = gender == 1;

        PlayerModelAnimatable animatable =
                ANIMATABLES.compute(
                        uuid,
                        (id, old) -> {

                            if (old == null) {
                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        isMale
                                );
                            }

                            /*
                             * Cinsiyet değiştiğinde yeni model oluştur.
                             */
                            if (old.isMale() != isMale) {
                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        isMale
                                );
                            }

                            return old;
                        }
                );

        /*
         * Animasyon hareket bilgisini güncelle.
         */
        animatable.updateMovement(event.getPartialTick());

        /*
         * Oyuncunun gerçek rotasyonunu custom modele aktar.
         */
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();

        poseStack.mulPose(
                com.mojang.math.Axis.YP.rotationDegrees(
                        180.0F - clientPlayer.getYRot()
                )
        );

        /*
         * Modelin Minecraft koordinat sistemine oturması için
         * başlangıç noktası.
         */
        poseStack.translate(
                0.0D,
                0.0D,
                0.0D
        );

        /*
         * Erkek / kadın renderer seç.
         */
        if (isMale) {

    MALE_RENDERER.renderPlayerModel(
            animatable,
            poseStack,
            event.getMultiBufferSource(),
            event.getPackedLight(),
            0,
            event.getPartialTick()
    );

} else {

    FEMALE_RENDERER.renderPlayerModel(
            animatable,
            poseStack,
            event.getMultiBufferSource(),
            event.getPackedLight(),
            0,
            event.getPartialTick()
    );
}

        poseStack.popPose();
    }
}