package net.mcreator.fatalscraftingqol.procedures;

import net.minecraft.world.level.LevelAccessor;

import net.mcreator.fatalscraftingqol.network.FatalsQolCraftingModVariables;

public class ChainmailEnabledProcedure {
	public static boolean execute(LevelAccessor world) {
		return FatalsQolCraftingModVariables.MapVariables.get(world).ChainmailArmor;
	}
}
