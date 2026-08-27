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

    public void updateMovement(float partialTick) {

        double vx = player.getDeltaMovement().x;
        double vz = player.getDeltaMovement().z;

        double horizontalSpeed =
                vx * vx + vz * vz;

        this.moving =
                horizontalSpeed > 0.0001D
                        && !player.isPassenger();

        this.sprinting =
                moving && player.isSprinting();

        this.sneaking =
                player.isShiftKeyDown();
    }

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

    private PlayState movementPredicate(
            AnimationState<PlayerModelAnimatable> state
    ) {

        /*
         * ============================================================
         * ERKEK
         * ============================================================
         */

        if (male) {

            /*
             * Sneak + hareket
             */
            if (sneaking && moving) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sneakwalk")
                );

                return PlayState.CONTINUE;
            }

            /*
             * Sneak + durma
             */
            if (sneaking) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sneak")
                );

                return PlayState.CONTINUE;
            }

            /*
             * Sprint
             */
            if (sprinting) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sprint")
                );

                return PlayState.CONTINUE;
            }

            /*
             * Normal yürüme
             */
            if (moving) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.walk")
                );

                return PlayState.CONTINUE;
            }

            /*
             * Havada
             */
            if (!player.onGround()) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.jump")
                );

                return PlayState.CONTINUE;
            }

            /*
             * Idle
             */
            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("male.idle")
            );

            return PlayState.CONTINUE;
        }


        /*
         * ============================================================
         * KADIN
         * ============================================================
         *
         * Verdiğin animasyon isimleri:
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
         * Havada
         */
        if (!player.onGround()) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.fly")
            );

            return PlayState.CONTINUE;
        }


        /*
         * Binek üzerinde
         */
        if (player.isPassenger()) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.ride")
            );

            return PlayState.CONTINUE;
        }


        /*
         * Sprint / koşu
         */
        if (sprinting) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.run")
            );

            return PlayState.CONTINUE;
        }


        /*
         * Normal yürüme
         */
        if (moving) {

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("animation.jenny.walk")
            );

            return PlayState.CONTINUE;
        }


        /*
         * Idle
         */
        state.getController().setAnimation(
                RawAnimation.begin()
                        .thenLoop("animation.jenny.idle")
        );

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object relatedObject) {
        return player.tickCount;
    }
}

