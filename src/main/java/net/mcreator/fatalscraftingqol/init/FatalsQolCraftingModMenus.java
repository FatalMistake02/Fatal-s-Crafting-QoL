
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.fatalscraftingqol.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.fatalscraftingqol.world.inventory.ConfigMenu;
import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

public class FatalsQolCraftingModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FatalsQolCraftingMod.MODID);
	public static final RegistryObject<MenuType<ConfigMenu>> CONFIG = REGISTRY.register("config", () -> IForgeMenuType.create(ConfigMenu::new));
}
