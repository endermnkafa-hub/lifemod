package net.mcreator.lifemod.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.lifemod.world.inventory.GenderGuiMenu;
import net.mcreator.lifemod.network.GenderGuiButtonMessage;
import net.mcreator.lifemod.LifeModMod;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class GenderGuiScreen extends AbstractContainerScreen<GenderGuiMenu> {
	private final static HashMap<String, Object> guistate = GenderGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_male;
	Button button_female;

	public GenderGuiScreen(GenderGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("life_mod:textures/screens/gender_gui.png");

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
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.life_mod.gender_gui.label_what_is_yor_gender"), 38, 21, -12829636, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		button_male = Button.builder(Component.translatable("gui.life_mod.gender_gui.button_male"), e -> {
			if (true) {
				LifeModMod.PACKET_HANDLER.sendToServer(new GenderGuiButtonMessage(0, x, y, z));
				GenderGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 10, this.topPos + 96, 46, 20).build();
		guistate.put("button:button_male", button_male);
		this.addRenderableWidget(button_male);
		button_female = Button.builder(Component.translatable("gui.life_mod.gender_gui.button_female"), e -> {
			if (true) {
				LifeModMod.PACKET_HANDLER.sendToServer(new GenderGuiButtonMessage(1, x, y, z));
				GenderGuiButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 109, this.topPos + 96, 56, 20).build();
		guistate.put("button:button_female", button_female);
		this.addRenderableWidget(button_female);
	}
}
