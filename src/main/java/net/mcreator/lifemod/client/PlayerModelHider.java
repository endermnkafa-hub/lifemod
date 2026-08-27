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

    // =========================================================
    // MODEL AYARLARI
    // =========================================================

    /*
     * SAĞ / SOL
     *
     * Pozitif  = sağ
     * Negatif  = sol
     */
    private static final double MODEL_X = 0.0D;


    /*
     * YÜKSEKLİK
     *
     * Pozitif  = yukarı
     * Negatif  = aşağı
     */
    private static final double MODEL_Y = 0.0D;


    /*
     * ÖN / ARKA
     *
     * Pozitif  = öne
     * Negatif  = arkaya
     */
    private static final double MODEL_Z = 0.0D;


    /*
     * MODELİN EKSTRA YATAY DÖNÜŞÜ
     *
     * 0    = normal
     * 90   = 90 derece
     * 180  = tamamen ters
     * 270  = 270 derece
     *
     * Eğer model karakterin baktığı yönün tam tersine bakıyorsa
     * bunu 180.0F yap.
     */
    private static final float MODEL_YAW_OFFSET = 0.0F;


    /*
     * MODELİN DİKEY DÖNÜŞÜ
     *
     * Normalde 0 bırak.
     */
    private static final float MODEL_PITCH_OFFSET = 0.0F;


    /*
     * MODEL BOYUTU
     *
     * 1.0 = normal
     * 1.1 = %10 büyük
     * 0.9 = %10 küçük
     * 2.0 = 2 kat büyük
     */
    private static final float MODEL_SCALE = 1.0F;


    // =========================================================
    // RENDERERLAR
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
    // PLAYER RENDER
    // =========================================================

    @SubscribeEvent
    public static void onRenderPlayerPre(
            RenderPlayerEvent.Pre event
    ) {

        Player player = event.getEntity();

        /*
         * Sadece gerçek client player renderlarını işliyoruz.
         */
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }


        // =====================================================
        // GENDER
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


        /*
         * Gender seçilmemişse vanilla modeli kullan.
         */
        if (gender == 0) {
            return;
        }


        boolean male = gender == 1;


        // =====================================================
        // VANILLA PLAYER MODELİNİ TAMAMEN KAPAT
        // =====================================================

        event.setCanceled(true);


        // =====================================================
        // ANIMATABLE
        // =====================================================

        UUID uuid = clientPlayer.getUUID();

        PlayerModelAnimatable animatable =
                ANIMATABLES.compute(
                        uuid,
                        (id, old) -> {

                            /*
                             * İlk defa render ediliyorsa oluştur.
                             */
                            if (old == null) {

                                return new PlayerModelAnimatable(
                                        clientPlayer,
                                        male
                                );
                            }


                            /*
                             * Gender değişmişse yeni model oluştur.
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


        // =====================================================
        // ANİMASYON HAREKET BİLGİSİ
        // =====================================================

        animatable.updateMovement(
                event.getPartialTick()
        );


        // =====================================================
        // POSE STACK
        // =====================================================

        PoseStack poseStack =
                event.getPoseStack();

        poseStack.pushPose();


        // =====================================================
        // OYUNCUNUN GERÇEK YÖNÜ
        // =====================================================

        float bodyYaw = Mth.rotLerp(
                event.getPartialTick(),
                clientPlayer.yBodyRotO,
                clientPlayer.yBodyRot
        );


        /*
         * Minecraft oyuncusunun baktığı yön.
         */
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        bodyYaw
                )
        );


        // =====================================================
        // MODELİN KENDİ DÖNÜŞÜ
        // =====================================================

        /*
         * Eğer model ters duruyorsa:
         *
         * MODEL_YAW_OFFSET = 180.0F
         *
         * yapabilirsin.
         */
        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        MODEL_YAW_OFFSET
                )
        );


        // =====================================================
        // MODEL POZİSYONU
        // =====================================================

        /*
         * X = sağ / sol
         * Y = yukarı / aşağı
         * Z = ön / arka
         */
        poseStack.translate(
                MODEL_X,
                MODEL_Y,
                MODEL_Z
        );


        // =====================================================
        // MODEL DİKEY DÖNÜŞÜ
        // =====================================================

        if (MODEL_PITCH_OFFSET != 0.0F) {

            poseStack.mulPose(
                    Axis.XP.rotationDegrees(
                            MODEL_PITCH_OFFSET
                    )
            );
        }


        // =====================================================
        // MODEL BOYUTU
        // =====================================================

        poseStack.scale(
                MODEL_SCALE,
                MODEL_SCALE,
                MODEL_SCALE
        );


        // =====================================================
        // MODELİ ÇİZ
        // =====================================================

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


        // =====================================================
        // POSE STACK GERİ AL
        // =====================================================

        poseStack.popPose();
    }
}