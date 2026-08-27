package net.mcreator.lifemod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.AbstractClientPlayer;
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

    private static final Map<UUID, PlayerModelAnimatable> ANIMATABLES =
            new ConcurrentHashMap<>();

    private static final PlayerCustomRenderer MALE_RENDERER =
            new PlayerCustomRenderer(true);

    private static final PlayerCustomRenderer FEMALE_RENDERER =
            new PlayerCustomRenderer(false);

    @SubscribeEvent
    public static void onRenderPlayerPre(
            RenderPlayerEvent.Pre event
    ) {

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
         * Gender sistemi:
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

        /*
         * Vanilla PlayerRenderer'ın tamamını iptal ediyoruz.
         *
         * Böylece:
         *
         * - vanilla gövde
         * - vanilla kafa
         * - vanilla kollar
         * - vanilla bacaklar
         *
         * custom modelin altında görünmüyor.
         */
        event.setCanceled(true);

        UUID uuid = clientPlayer.getUUID();

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

                            /*
                             * Gender değiştiyse eski animatable'ı
                             * kullanma.
                             */
                            if (old.isMale() != male) {

                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        male
                                );
                            }

                            return old;
                        }
                );

        animatable.updateMovement(
                event.getPartialTick()
        );

        PoseStack poseStack =
                event.getPoseStack();

        poseStack.pushPose();

        /*
         * =====================================================
         * MINECRAFT PLAYER ROTATION
         * =====================================================
         *
         * Oyuncunun gövde yönünü kullanıyoruz.
         *
         * yBodyRotO = önceki tick
         * yBodyRot  = mevcut tick
         *
         * partialTick ile yumuşak interpolasyon yapıyoruz.
         */
        float bodyYaw = Mth.rotLerp(
                event.getPartialTick(),
                clientPlayer.yBodyRotO,
                clientPlayer.yBodyRot
        );

        /*
         * Minecraft player modelinin forward yönü ile
         * GeckoLib modelinin forward yönünü eşleştiriyoruz.
         */
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        
                )
        );

        /*
         * Minecraft HumanoidModel dönüşümü.
         *
         * Bu özellikle modelin:
         *
         * - yukarıda kalmasını
         * - aşağıda kalmasını
         * - ters eksende çizilmesini
         *
         * önlemek için önemli.
         */
        poseStack.scale(
                1.0F,
                1.0F,
                1.0F
        );

        /*
         * Minecraft player model origin'i
         * ayaklardan 1.501 blok yukarıdadır.
         *
         * GeckoLib modelimizi aynı koordinat sistemine
         * getiriyoruz.
         */
        poseStack.translate(
                0.0D,
                -1.501D,
                0.0D
        );

        /*
         * Modeli çiz.
         */
        if (male) {

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