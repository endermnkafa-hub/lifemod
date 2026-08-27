package net.mcreator.lifemod.client;

import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class PlayerModelAnimatable implements GeoAnimatable {

    private final AbstractClientPlayer player;
    private final boolean isMale;

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    private boolean moving;
    private boolean sprinting;

    public PlayerModelAnimatable(AbstractClientPlayer player, boolean isMale) {
        this.player = player;
        this.isMale = isMale;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public boolean isMale() {
        return isMale;
    }

    public void updateMovement(float partialTick) {
        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;

        double speed = dx * dx + dz * dz;

        this.moving = speed > 0.00005D;
        this.sprinting = this.moving && player.isSprinting();
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
                        "movement",
                        2,
                        this::movementPredicate
                )
        );
    }

    private PlayState movementPredicate(
            AnimationState<PlayerModelAnimatable> state
    ) {
        if (sprinting) {
            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop(
                                    isMale
                                            ? "male.sprint"
                                            : "female.sprint"
                            )
            );

            return PlayState.CONTINUE;
        }

        if (moving) {
            state.getController().setAnimation(
                    RawAnimation.begin()
                            .thenLoop(
                                    isMale
                                            ? "male.walk"
                                            : "female.walk"
                            )
            );

            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(
                RawAnimation.begin()
                        .thenLoop(
                                isMale
                                        ? "male.idle"
                                        : "female.idle"
                        )
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