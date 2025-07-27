
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.fatalscraftingqol.init;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.fatalscraftingqol.network.OpenConfigMessage;
import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class FatalsQolCraftingModKeyMappings {
	public static final KeyMapping OPEN_CONFIG = new KeyMapping("key.fatals_qol_crafting.open_config", GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				OPEN_CONFIG_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - OPEN_CONFIG_LASTPRESS);
				FatalsQolCraftingMod.PACKET_HANDLER.sendToServer(new OpenConfigMessage(1, dt));
				OpenConfigMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	private static long OPEN_CONFIG_LASTPRESS = 0;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_CONFIG);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				OPEN_CONFIG.consumeClick();
			}
		}
	}
}
