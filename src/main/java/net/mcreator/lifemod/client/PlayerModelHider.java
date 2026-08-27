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

    private static final PlayerCustomRenderer MALE_RENDERER =
            new PlayerCustomRenderer(true);

    private static final PlayerCustomRenderer FEMALE_RENDERER =
            new PlayerCustomRenderer(false);

    private static final Map<UUID, PlayerModelAnimatable> ANIMATABLES =
            new ConcurrentHashMap<>();

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

        PlayerRenderer renderer =
                (PlayerRenderer) event.getRenderer();

        PlayerModel<AbstractClientPlayer> model =
                renderer.getModel();

        UUID uuid = clientPlayer.getUUID();

        /*
         * Mevcut vanilla model görünürlüklerini sakla.
         */
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

        /*
         * =====================================================
         * KAFA
         * =====================================================
         *
         * Vanilla kafa kullanılacak.
         *
         * Custom GeoModel'in kendi kafa parçaları varsa onların
         * görünürlüğü model/geo tarafından kontrol edilir.
         */
        model.head.visible = true;
        model.hat.visible = true;

        /*
         * =====================================================
         * VANILLA GÖVDEYİ GİZLE
         * =====================================================
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
         * =====================================================
         * GECKOLIB ANIMATABLE
         * =====================================================
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

        float partialTick =
                event.getPartialTick();

        animatable.updateMovement(partialTick);

        PoseStack poseStack =
                event.getPoseStack();

        poseStack.pushPose();

        /*
         * =====================================================
         * MODEL ROOT HİZALAMA
         * =====================================================
         *
         * Minecraft:
         *
         * 1 blok = 16 pixel
         *
         * ERKEK ROOT:
         *
         * Blockbench:
         * X = -4
         * Y =  0
         * Z = +1.7
         *
         * Root'u Minecraft oyuncu merkezine getirmek için
         * tersine çeviriyoruz:
         *
         * X = +4 px
         * Y =  0 px
         * Z = -1.7 px
         *
         * =====================================================
         */

        if (male) {

            poseStack.translate(
                    4.0D / 16.0D,
                    0.0D,
                    -1.7D / 16.0D
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

            /*
             * Kadın modelinin root'u:
             *
             * [0, 0, 0]
             *
             * Bu yüzden ekstra translation yok.
             */

            FEMALE_RENDERER.renderPlayerModel(
                    animatable,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight(),
                    0,
                    partialTick
            );
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {

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

        /*
         * Vanilla model görünürlüklerini geri yükle.
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