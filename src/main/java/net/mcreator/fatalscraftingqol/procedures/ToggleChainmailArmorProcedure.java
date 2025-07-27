package net.mcreator.fatalscraftingqol.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.fatalscraftingqol.network.FatalsQolCraftingModVariables;

public class ToggleChainmailArmorProcedure {
	public static void execute(LevelAccessor world) {
		if (FatalsQolCraftingModVariables.MapVariables.get(world).ChainmailArmor == true) {
			FatalsQolCraftingModVariables.MapVariables.get(world).ChainmailArmor = false;
			FatalsQolCraftingModVariables.MapVariables.get(world).syncData(world);
		} else {
			FatalsQolCraftingModVariables.MapVariables.get(world).ChainmailArmor = true;
			FatalsQolCraftingModVariables.MapVariables.get(world).syncData(world);
		}
	}
}
