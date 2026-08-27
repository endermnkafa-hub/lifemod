
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.lifemod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.lifemod.world.inventory.GenderGuiMenu;
import net.mcreator.lifemod.LifeModMod;

public class LifeModModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LifeModMod.MODID);
	public static final RegistryObject<MenuType<GenderGuiMenu>> GENDER_GUI = REGISTRY.register("gender_gui", () -> IForgeMenuType.create(GenderGuiMenu::new));
}
