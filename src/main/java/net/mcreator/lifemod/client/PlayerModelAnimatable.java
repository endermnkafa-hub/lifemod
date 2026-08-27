package net.mcreator.lifemod.client;

import net.minecraft.client.player.AbstractClientPlayer;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class PlayerModelAnimatable implements GeoAnimatable {

    private final AbstractClientPlayer player;
    private final boolean male;

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);


    private boolean moving;
    private boolean sprinting;
    private boolean sneaking;


    public PlayerModelAnimatable(
            AbstractClientPlayer player,
            boolean male
    ) {
        this.player = player;
        this.male = male;
    }


    public AbstractClientPlayer getPlayer() {
        return player;
    }


    public boolean isMale() {
        return male;
    }


    public boolean isMoving() {
        return moving;
    }


    public boolean isSprinting() {
        return sprinting;
    }


    public boolean isSneaking() {
        return sneaking;
    }


    /*
     * ============================================================
     * MOVEMENT UPDATE
     * ============================================================
     */

    public void updateMovement(float partialTick) {

        double vx =
                player.getDeltaMovement().x;

        double vz =
                player.getDeltaMovement().z;

        double horizontalSpeed =
                vx * vx + vz * vz;


        this.moving =
                horizontalSpeed > 0.0001D
                        && !player.isPassenger();


        this.sprinting =
                moving
                        && player.isSprinting();


        this.sneaking =
                player.isShiftKeyDown();
    }


    /*
     * ============================================================
     * CONTROLLER
     * ============================================================
     */

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {

        controllers.add(
                new AnimationController<>(
                        this,
                        "player_movement",
                        0,
                        this::movementPredicate
                )
        );
    }


    /*
     * ============================================================
     * ANIMATION PREDICATE
     * ============================================================
     */

    private PlayState movementPredicate(
            AnimationState<PlayerModelAnimatable> state
    ) {


        /*
         * ========================================================
         * ERKEK
         * ========================================================
         *
         * male.idle
         * male.walk
         * male.sprint
         * male.jump
         * male.sneak
         * male.sneakwalk
         */

        if (male) {


            /*
             * ZIPLIYOR / HAVADA
             *
             * Bu kontrol özellikle walk'tan ÖNCE.
             */

            if (!player.onGround()) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.jump")
                );

                return PlayState.CONTINUE;
            }


            /*
             * SNEAK + YÜRÜME
             */

            if (sneaking && moving) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sneakwalk")
                );

                return PlayState.CONTINUE;
            }


            /*
             * SNEAK + DURMA
             */

            if (sneaking) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sneak")
                );

                return PlayState.CONTINUE;
            }


            /*
             * SPRINT
             */

            if (sprinting) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sprint")
                );

                return PlayState.CONTINUE;
            }


            /*
             * NORMAL YÜRÜME
             */

            if (moving) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.walk")
                );

                return PlayState.CONTINUE;
            }


            /*
             * IDLE
             */

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("male.idle")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * KADIN
         * ========================================================
         *
         * animation.jenny.idle
         * animation.jenny.attack0
         * animation.jenny.walk
         * animation.jenny.run
         * animation.jenny.bowcharge
         * animation.jenny.ride
         * animation.jenny.fly
         */


        /*
         * ========================================================
         * BİNEK
         * ========================================================
         */

        if (player.isPassenger()) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.ride")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * UÇUŞ / HAVADA
         * ========================================================
         */

        if (!player.onGround()) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.fly")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * SALDIRI
         * ========================================================
         *
         * Minecraft'ın swingTime değeri saldırı sırasında > 0 olur.
         */

        if (player.swingTime > 0) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenPlay("animation.jenny.attack0")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * YAY / ITEM KULLANMA
         * ========================================================
         *
         * Oyuncu bir item kullanıyorsa bowcharge oynatılır.
         */

        if (player.isUsingItem()) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.bowcharge")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * KOŞU
         * ========================================================
         */

        if (sprinting) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.run")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * YÜRÜME
         * ========================================================
         */

        if (moving) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.walk")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ========================================================
         * IDLE
         * ========================================================
         */

        state.getController().setAnimation(
                RawAnimation.begin()
                        .thenLoop("animation.jenny.idle")
        );

        return PlayState.CONTINUE;
    }


    /*
     * ============================================================
     * GECKOLIB CACHE
     * ============================================================
     */

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    /*
     * ============================================================
     * GECKOLIB TICK
     * ============================================================
     */

    @Override
    public double getTick(Object relatedObject) {
        return player.tickCount;
    }
}
