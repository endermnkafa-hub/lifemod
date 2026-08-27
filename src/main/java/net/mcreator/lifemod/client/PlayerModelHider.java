```java
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

    /*
     * ============================================================
     * MODEL POZİSYON AYARLARI
     * ============================================================
     *
     * 1 Minecraft blok = 1.0
     * 1 Blockbench pixel = 0.0625
     *
     * X:
     *  + sağ
     *  - sol
     *
     * Y:
     *  + yukarı
     *  - aşağı
     *
     * Z:
     *  + öne
     *  - arkaya
     *
     * ============================================================
     */

    // ERKEK MODEL
    private static final double MALE_X = 0.0D;
    private static final double MALE_Y = 0.0D;
    private static final double MALE_Z = 0.0D;

    // KADIN MODEL
    private static final double FEMALE_X = 0.0D;
    private static final double FEMALE_Y = 0.0D;
    private static final double FEMALE_Z = 0.0D;


    /*
     * ============================================================
     * RENDERERLAR
     * ============================================================
     */

    private static final PlayerCustomRenderer MALE_RENDERER =
            new PlayerCustomRenderer(true);

    private static final PlayerCustomRenderer FEMALE_RENDERER =
            new PlayerCustomRenderer(false);


    /*
     * ============================================================
     * ANIMATABLE'LAR
     * ============================================================
     */

    private static final Map<UUID, PlayerModelAnimatable> ANIMATABLES =
            new ConcurrentHashMap<>();


    /*
     * ============================================================
     * VANILLA MODEL GÖRÜNÜRLÜK DURUMU
     * ============================================================
     */

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


    /*
     * ============================================================
     * PLAYER RENDER PRE
     * ============================================================
     */

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {

        Player player = event.getEntity();

        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }


        /*
         * ========================================================
         * GENDER
         * ========================================================
         *
         * 0 = vanilla
         * 1 = erkek
         * 2 = kadın
         */

        LifeModModVariables.PlayerVariables variables =
                clientPlayer.getCapability(
                        LifeModModVariables.PLAYER_VARIABLES_CAPABILITY,
                        null
                ).orElse(null);

        if (variables == null) {
            return;
        }

        int gender = (int) variables.gender;

        if (gender == 0) {
            return;
        }

        boolean male = gender == 1;


        /*
         * ========================================================
         * VANILLA PLAYER MODEL
         * ========================================================
         */

        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();

        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();


        UUID uuid =
                clientPlayer.getUUID();


        /*
         * ========================================================
         * ESKİ GÖRÜNÜRLÜKLERİ SAKLA
         * ========================================================
         */

        ModelVisibilityState oldState =
                new ModelVisibilityState();

        oldState.head =
                model.head.visible;

        oldState.hat =
                model.hat.visible;

        oldState.body =
                model.body.visible;

        oldState.rightArm =
                model.rightArm.visible;

        oldState.leftArm =
                model.leftArm.visible;

        oldState.rightLeg =
                model.rightLeg.visible;

        oldState.leftLeg =
                model.leftLeg.visible;

        oldState.jacket =
                model.jacket.visible;

        oldState.rightSleeve =
                model.rightSleeve.visible;

        oldState.leftSleeve =
                model.leftSleeve.visible;

        oldState.rightPants =
                model.rightPants.visible;

        oldState.leftPants =
                model.leftPants.visible;


        VISIBILITY_STATES.put(
                uuid,
                oldState
        );


        /*
         * ========================================================
         * KAFA
         * ========================================================
         *
         * Vanilla Minecraft kafası kullanılacak.
         */

        model.head.visible = true;
        model.hat.visible = true;


        /*
         * ========================================================
         * VANILLA VÜCUT
         * ========================================================
         *
         * Custom GeoModel vücudu çizeceği için vanilla vücut
         * parçalarını gizliyoruz.
         */

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
         * ========================================================
         * GECKOLIB ANIMATABLE
         * ========================================================
         */

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


        /*
         * ========================================================
         * ANİMASYON GÜNCELLE
         * ========================================================
         */

        float partialTick =
                event.getPartialTick();

        animatable.updateMovement(
                partialTick
        );


        /*
         * ========================================================
         * POSE STACK
         * ========================================================
         */

        PoseStack poseStack =
                event.getPoseStack();

        poseStack.pushPose();


        /*
         * ========================================================
         * MODEL POZİSYONU
         * ========================================================
         *
         * Buradaki değerler modeli sağa/sola/yukarı/aşağı/
         * öne/arkaya hareket ettirir.
         */

        if (male) {

            poseStack.translate(
                    MALE_X,
                    MALE_Y,
                    MALE_Z
            );

            MALE_RENDERER.renderPlayerModel(
                    animatable,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    0,
                    partialTick
            );

        } else {

            poseStack.translate(
                    FEMALE_X,
                    FEMALE_Y,
                    FEMALE_Z
            );

            FEMALE_RENDERER.renderPlayerModel(
                    animatable,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    0,
                    partialTick
            );
        }


        /*
         * ========================================================
         * POSE STACK GERİ AL
         * ========================================================
         */

        poseStack.popPose();
    }


    /*
     * ============================================================
     * PLAYER RENDER POST
     * ============================================================
     */

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {

        Player player =
                event.getEntity();

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


        /*
         * ========================================================
         * VANILLA MODELİ GERİ YÜKLE
         * ========================================================
         */

        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();

        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();


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
```
