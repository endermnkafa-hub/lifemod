package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.mcreator.lifemod.network.LifeModModVariables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerModelHider {

    // =========================================================
    // CUSTOM RENDERERLAR
    // =========================================================

    private static final PlayerCustomRenderer MALE_RENDERER =
            new PlayerCustomRenderer(true);

    private static final PlayerCustomRenderer FEMALE_RENDERER =
            new PlayerCustomRenderer(false);


    // =========================================================
    // ANIMATABLE CACHE
    // =========================================================

    private static final Map<UUID, PlayerModelAnimatable> ANIMATABLES =
            new ConcurrentHashMap<>();


    // =========================================================
    // PLAYER MODEL GÖRÜNÜRLÜK DURUMUNU SAKLA
    // =========================================================

    private static final Map<UUID, ModelVisibilityState> VISIBILITY_STATES =
            new ConcurrentHashMap<>();


    private static class ModelVisibilityState {

        boolean head;
        boolean hat;

        boolean body;

        boolean rightArm;
        boolean leftArm;

        boolean rightLeg;
        boolean leftLeg;

        boolean jacket;

        boolean rightSleeve;
        boolean leftSleeve;

        boolean rightPants;
        boolean leftPants;
    }


    // =========================================================
    // RENDER PRE
    // =========================================================

    @SubscribeEvent
    public static void onRenderPlayerPre(
            RenderPlayerEvent.Pre event
    ) {

        Player player = event.getEntity();

        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }


        // =====================================================
        // GENDER BİLGİSİ
        // =====================================================

        LifeModModVariables.PlayerVariables variables =
                clientPlayer.getCapability(
                        LifeModModVariables.PLAYER_VARIABLES_CAPABILITY,
                        null
                ).orElse(null);

        if (variables == null) {
            return;
        }


        /*
         * Gender:
         *
         * 0 = vanilla
         * 1 = erkek
         * 2 = kadın
         */

        int gender = (int) variables.gender;

        if (gender == 0) {
            return;
        }


        boolean male = gender == 1;


        // =====================================================
        // PLAYER RENDERER
        // =====================================================

        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();


        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();


        // =====================================================
        // MEVCUT MODEL GÖRÜNÜRLÜKLERİNİ SAKLA
        // =====================================================

        UUID uuid = clientPlayer.getUUID();

        ModelVisibilityState oldState =
                new ModelVisibilityState();

        oldState.head = model.head.visible;
        oldState.hat = model.hat.visible;

        oldState.body = model.body.visible;

        oldState.rightArm = model.rightArm.visible;
        oldState.leftArm = model.leftArm.visible;

        oldState.rightLeg = model.rightLeg.visible;
        oldState.leftLeg = model.leftLeg.visible;

        oldState.jacket = model.jacket.visible;

        oldState.rightSleeve = model.rightSleeve.visible;
        oldState.leftSleeve = model.leftSleeve.visible;

        oldState.rightPants = model.rightPants.visible;
        oldState.leftPants = model.leftPants.visible;

        VISIBILITY_STATES.put(uuid, oldState);


        // =====================================================
        // VANILLA MODELİ AYARLA
        // =====================================================

        /*
         * KAFA:
         *
         * Vanilla kafa kesinlikle görünür.
         */

        model.head.visible = true;
        model.hat.visible = true;


        /*
         * GÖVDE:
         *
         * Custom model gövdeyi çizdiği için vanilla gövdeyi
         * gizliyoruz.
         */

        model.body.visible = false;


        /*
         * KOLLAR
         */

        model.rightArm.visible = false;
        model.leftArm.visible = false;


        /*
         * BACAKLAR
         */

        model.rightLeg.visible = false;
        model.leftLeg.visible = false;


        /*
         * PLAYER CLOTHING / ARMOR-LIKE LAYERS
         */

        model.jacket.visible = false;

        model.rightSleeve.visible = false;
        model.leftSleeve.visible = false;

        model.rightPants.visible = false;
        model.leftPants.visible = false;


        // =====================================================
        // ANIMATABLE
        // =====================================================

        PlayerModelAnimatable animatable =
                ANIMATABLES.compute(
                        uuid,
                        (id, old) -> {

                            if (old == null) {

                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        male
                                );
                            }


                            if (old.isMale() != male) {

                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        male
                                );
                            }


                            return old;
                        }
                );


        // =====================================================
        // HAREKET BİLGİSİ
        // =====================================================

        float partialTick =
                event.getPartialTick();

        animatable.updateMovement(
                partialTick
        );


        // =====================================================
        // POSE STACK
        // =====================================================

        PoseStack poseStack =
                event.getPoseStack();


        poseStack.pushPose();


        // =====================================================
        // ÖNEMLİ:
        //
        // BURADA ARTIK bodyYaw UYGULAMIYORUZ.
        //
        // RenderPlayerEvent.Pre'nin PoseStack'i zaten
        // PlayerRenderer tarafından oyuncunun pozisyonu ve
        // yönü için hazırlanmış durumda.
        // =====================================================


        // =====================================================
        // CUSTOM MODELİ ÇİZ
        // =====================================================

        if (male) {

            MALE_RENDERER.renderPlayerModel(
                    animatable,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    0,
                    partialTick
            );

        } else {

            FEMALE_RENDERER.renderPlayerModel(
                    animatable,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    0,
                    partialTick
            );
        }


        // =====================================================
        // POSE STACK GERİ AL
        // =====================================================

        poseStack.popPose();
    }


    // =========================================================
    // RENDER POST
    // =========================================================

    @SubscribeEvent
    public static void onRenderPlayerPost(
            RenderPlayerEvent.Post event
    ) {

        Player player = event.getEntity();

        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }


        UUID uuid =
                clientPlayer.getUUID();


        ModelVisibilityState oldState =
                VISIBILITY_STATES.remove(uuid);


        if (oldState == null) {
            return;
        }


        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();


        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();


        // =====================================================
        // MODEL GÖRÜNÜRLÜKLERİNİ GERİ YÜKLE
        // =====================================================

        model.head.visible =
                oldState.head;

        model.hat.visible =
                oldState.hat;

        model.body.visible =
                oldState.body;

        model.rightArm.visible =
                oldState.rightArm;

        model.leftArm.visible =
                oldState.leftArm;

        model.rightLeg.visible =
                oldState.rightLeg;

        model.leftLeg.visible =
                oldState.leftLeg;

        model.jacket.visible =
                oldState.jacket;

        model.rightSleeve.visible =
                oldState.rightSleeve;

        model.leftSleeve.visible =
                oldState.leftSleeve;

        model.rightPants.visible =
                oldState.rightPants;

        model.leftPants.visible =
                oldState.leftPants;
    }
}