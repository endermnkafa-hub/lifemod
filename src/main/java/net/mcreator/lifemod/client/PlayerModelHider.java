package net.mcreator.lifemod.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

import com.mojang.blaze3d.vertex.PoseStack;

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

        LifeModModVariables.PlayerVariables variables =
                clientPlayer.getCapability(
                        LifeModModVariables.PLAYER_VARIABLES_CAPABILITY,
                        null
                ).orElse(null);

        if (variables == null) {
            return;
        }

        double gender = variables.gender;

        /*
         * gender 0 = vanilla oyuncu modeli
         */
        if (gender == 0) {
            return;
        }

        /*
         * Vanilla oyuncu modelini gizle.
         *
         * Kafa ve şapka BURADA gizlenmiyor.
         * Custom model kendi kafasını çiziyorsa zaten
         * vanilla modelin tamamını gizlememiz gerekiyor.
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
         * Kafa da custom model tarafından çizileceği için
         * vanilla kafayı gizle.
         *
         * Önceki kodda kafa görünmüyordu çünkü custom modelin
         * kendisi doğru pozisyona oturmamıştı.
         */
        model.head.visible = false;
        model.hat.visible = false;

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

                            if (old.isMale() != isMale) {
                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        isMale
                                );
                            }

                            return old;
                        }
                );

        animatable.updateMovement(event.getPartialTick());

        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();

        /*
         * RenderPlayerEvent.Pre zaten oyuncunun dünya konumunda
         * ve oyuncuya göre doğru dönüşte çalışır.
         *
         * Bu yüzden önceki:
         *
         * 180 - player.getYRot()
         *
         * dönüşünü kullanmıyoruz.
         */

        /*
         * GeckoLib modeli Minecraft oyuncu modelinin merkezine
         * göre çizilir.
         *
         * Geo modelin pivotu oyuncunun ayak merkezindeyse
         * Y = 0 doğru konumdur.
         */
        poseStack.translate(
                0.0D,
                0.0D,
                0.0D
        );

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

        /*
         * Vanilla model görünürlüğünü sonraki frame için
         * tekrar aç.
         */
        model.body.visible = true;

        model.rightArm.visible = true;
        model.leftArm.visible = true;

        model.rightLeg.visible = true;
        model.leftLeg.visible = true;

        model.jacket.visible = true;

        model.rightSleeve.visible = true;
        model.leftSleeve.visible = true;

        model.rightPants.visible = true;
        model.leftPants.visible = true;

        model.head.visible = true;
        model.hat.visible = true;
    }
}