package net.mcreator.fatalscraftingqol.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.fatalscraftingqol.network.FatalsQolCraftingModVariables;

import javax.annotation.Nullable;

import java.util.Collections;

@Mod.EventBusSubscriber
public class RecipeRemoverProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (FatalsQolCraftingModVariables.MapVariables.get(world).ChainmailArmor == false) {
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.server.getRecipeManager().byKey(new ResourceLocation("fatals_qol_crafting:chainmail_helmet")).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.server.getRecipeManager().byKey(new ResourceLocation("fatals_qol_crafting:chainmail_chestplate")).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.server.getRecipeManager().byKey(new ResourceLocation("fatals_qol_crafting:chainmail_leggings")).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.server.getRecipeManager().byKey(new ResourceLocation("fatals_qol_crafting:chainmail_boots")).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
		}
	}
}
