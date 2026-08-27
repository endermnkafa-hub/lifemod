
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.lifemod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.mcreator.lifemod.item.ReselectgenderItem;
import net.mcreator.lifemod.LifeModMod;

public class LifeModModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, LifeModMod.MODID);
	public static final RegistryObject<Item> RESELECTGENDER = REGISTRY.register("reselectgender", () -> new ReselectgenderItem());
}
