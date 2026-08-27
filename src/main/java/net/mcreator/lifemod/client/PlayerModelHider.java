```java
package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
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
     * DEFAULT MODEL POZİSYONLARI
     * ============================================================
     */

    private static final double DEFAULT_MALE_X = -0.75D;
    private static final double DEFAULT_MALE_Y = -0.60D;
    private static final double DEFAULT_MALE_Z = -0.60D;

    private static final double DEFAULT_FEMALE_X = -0.50D;
    private static final double DEFAULT_FEMALE_Y = -0.60D;
    private static final double DEFAULT_FEMALE_Z = -0.50D;


    /*
     * ============================================================
     * AKTİF MODEL POZİSYONLARI
     * ============================================================
     *
     * LifeModModelCommand.java bu değerleri değiştirebilir.
     */

    private static double maleX = DEFAULT_MALE_X;
    private static double maleY = DEFAULT_MALE_Y;
    private static double maleZ = DEFAULT_MALE_Z;

    private static double femaleX = DEFAULT_FEMALE_X;
    private static double femaleY = DEFAULT_FEMALE_Y;
    private static double femaleZ = DEFAULT_FEMALE_Z;


    /*
     * ============================================================
     * MODEL YÖNÜ
     * ============================================================
     *
     * Model Blockbench'te ters bakıyorsa:
     *
     * 0   = normal
     * 180 = ters
     */

    private static final float MODEL_FORWARD_ROTATION = 0.0F;


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
     * VANILLA MODEL GÖRÜNÜRLÜK DURUMLARI
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
     * COMMAND API
     * ============================================================
     *
     * LifeModModelCommand.java tarafından kullanılır.
     */


    public static void setMaleX(double value) {
        maleX = value;
    }


    public static void setMaleY(double value) {
        maleY = value;
    }


    public static void setMaleZ(double value) {
        maleZ = value;
    }


    public static void setFemaleX(double value) {
        femaleX = value;
    }


    public static void setFemaleY(double value) {
        femaleY = value;
    }


    public static void setFemaleZ(double value) {
        femaleZ = value;
    }


    public static double getMaleX() {
        return maleX;
    }


    public static double getMaleY() {
        return maleY;
    }


    public static double getMaleZ() {
        return maleZ;
    }


    public static double getFemaleX() {
        return femaleX;
    }


    public static double getFemaleY() {
        return femaleY;
    }


    public static double getFemaleZ() {
        return femaleZ;
    }


    public static void resetMalePosition() {

        maleX = DEFAULT_MALE_X;
        maleY = DEFAULT_MALE_Y;
        maleZ = DEFAULT_MALE_Z;
    }


    public static void resetFemalePosition() {

        femaleX = DEFAULT_FEMALE_X;
        femaleY = DEFAULT_FEMALE_Y;
        femaleZ = DEFAULT_FEMALE_Z;
    }


    /*
     * ============================================================
     * PLAYER PRE RENDER
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
         * 0 = Vanilla
         * 1 = Erkek
         * 2 = Kadın
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
         * PLAYER RENDERER
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
         * VANILLA MODEL DURUMUNU SAKLA
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


        VISIBILITY_STATES.put(uuid, oldState);


        /*
         * ========================================================
         * VANILLA KAFA
         * ========================================================
         *
         * KAFA GÖRÜNÜR.
         *
         * Gövde/kollar/bacaklar gizlenir.
         */

        model.head.visible = true;
        model.hat.visible = true;


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
         * ANIMATION UPDATE
         * ========================================================
         */

        float partialTick =
                event.getPartialTick();

        animatable.updateMovement(partialTick);


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
         * ÖNEMLİ:
         *
         * Burada artık sabit değer kullanmıyoruz.
         *
         * LifeModModelCommand ile değiştirdiğin değerler
         * doğrudan burada kullanılıyor.
         */

        if (male) {

            poseStack.translate(
                    maleX,
                    maleY,
                    maleZ
            );

        } else {

            poseStack.translate(
                    femaleX,
                    femaleY,
                    femaleZ
            );
        }


        /*
         * ========================================================
         * BAKIŞ YÖNÜ
         * ========================================================
         *
         * Minecraft'ın body rotation'ı zaten oyuncuyu döndürür.
         *
         * Burada yalnızca kafanın gövdeye göre dönüşünü ekliyoruz.
         */

        float bodyYaw =
                Mth.rotLerp(
                        partialTick,
                        clientPlayer.yBodyRotO,
                        clientPlayer.yBodyRot
                );


        float headYaw =
                Mth.rotLerp(
                        partialTick,
                        clientPlayer.yHeadRotO,
                        clientPlayer.yHeadRot
                );


        float relativeHeadYaw =
                Mth.wrapDegrees(
                        headYaw - bodyYaw
                );


        /*
         * ========================================================
         * MODEL YAW
         * ========================================================
         */

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        relativeHeadYaw
                                + MODEL_FORWARD_ROTATION
                )
        );


        /*
         * ========================================================
         * CUSTOM MODEL RENDER
         * ========================================================
         */

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


        /*
         * ========================================================
         * POSE STACK RESTORE
         * ========================================================
         */

        poseStack.popPose();
    }


    /*
     * ============================================================
     * PLAYER POST RENDER
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
         * VANILLA PLAYER RENDERER
         * ========================================================
         */

        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();


        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();


        /*
         * ========================================================
         * VANILLA MODEL DURUMUNU GERİ YÜKLE
         * ========================================================
         */

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
