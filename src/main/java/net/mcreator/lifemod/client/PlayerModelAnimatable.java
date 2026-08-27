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
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isSprinting() {
        return sprinting;
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

        if (male) {

            if (sprinting) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.sprint")
                );

                return PlayState.CONTINUE;
            }

            if (moving) {

                state.getController().setAnimation(
                        RawAnimation.begin()
                                .thenLoop("male.walk")
                );

                return PlayState.CONTINUE;
            }

            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop("male.idle")
            );

            return PlayState.CONTINUE;
        }

        /*
         * Kadın modelinin animation.json dosyasında bulunan
         * mevcut animasyon kullanılıyor.
         */
        state.getController().setAnimation(
                RawAnimation.begin()
                        .thenLoop("animation.jenny.fhappy")
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