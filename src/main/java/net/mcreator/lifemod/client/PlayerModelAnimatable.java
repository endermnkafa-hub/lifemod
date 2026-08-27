package net.mcreator.lifemod.client;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class PlayerModelAnimatable implements GeoAnimatable {
    private final boolean isMale;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlayerModelAnimatable(boolean isMale) { this.isMale = isMale; }
    public boolean isMale() { return isMale; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
