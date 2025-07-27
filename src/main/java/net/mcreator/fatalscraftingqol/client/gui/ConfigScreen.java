package net.mcreator.fatalscraftingqol.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.fatalscraftingqol.world.inventory.ConfigMenu;
import net.mcreator.fatalscraftingqol.network.ConfigButtonMessage;
import net.mcreator.fatalscraftingqol.FatalsQolCraftingMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class ConfigScreen extends AbstractContainerScreen<ConfigMenu> {
	private final static HashMap<String, Object> guistate = ConfigMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_chainmail_armor;

	public ConfigScreen(ConfigMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("fatals_qol_crafting:textures/screens/config.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.fatals_qol_crafting.config.label_enabled"), 115, 9, -12829636, false);
	}

	@Override
	public void init() {
		super.init();
		button_chainmail_armor = Button.builder(Component.translatable("gui.fatals_qol_crafting.config.button_chainmail_armor"), e -> {
			if (true) {
				FatalsQolCraftingMod.PACKET_HANDLER.sendToServer(new ConfigButtonMessage(0, x, y, z));
				ConfigButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 5, this.topPos + 6, 103, 20).build();
		guistate.put("button:button_chainmail_armor", button_chainmail_armor);
		this.addRenderableWidget(button_chainmail_armor);
	}
}
