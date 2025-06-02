package net.exmo.catalogsystem.content.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.gui.GuiGuideButtonBase;
import net.exmo.catalogsystem.content.gui.GuiToggleButtonBase;
import net.exmo.catalogsystem.content.gui.menu.CatalogTotalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import se.mickelus.mutil.gui.*;

import java.util.HashMap;

import static net.exmo.catalogsystem.Catalogsystem.manager;

public class CatalogTotalScreen extends AbstractContainerScreen<CatalogTotalMenu> {
    // GuiButton extends GuiClickable extends GuiElement
	private final GuiElement guiGuideBase;
	private final GuiElement guiButtonBase;
	private final GuiElement guiDefault;

    public CatalogTotalScreen(CatalogTotalMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
        int x = container.x;
        int y = container.y;
        int z = container.z;
		int pageIndex;
        Player player = container.player;
		HashMap<String, String> textState = new HashMap<>();

		manager.registerCallbacks(textState, x, y, z, player);

		if (container.id.matches(String.valueOf(1))) {
			pageIndex = 1;
		} else {
			pageIndex = 0;
		}
        Catalogsystem.LOGGER.info("draw page: {} {}", pageIndex, container.id);
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.guiDefault = new GuiElement(0, 0, this.imageWidth, this.imageHeight);
		this.guiDefault.addChild(new GuiRect(0, 0, this.imageWidth, this.imageHeight, 0xff0000));//located pos
		this.guiButtonBase = new GuiToggleButtonBase(imageWidth, imageHeight, pageIndex);
		this.guiGuideBase = new GuiGuideButtonBase(imageWidth, imageHeight, pageIndex, player, textState, x, y, z);
		this.guiDefault.addChild(guiGuideBase);
		this.guiDefault.addChild(guiButtonBase);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (tootipl!=null){
			guiGraphics.renderTooltip(font, tootipl.stack, tootipl.x, tootipl.y);
			tootipl =null;
		}
	}

	@Override
	public boolean mouseClicked(double d, double b, int p_97750_) {
		for (GuiElement guiElement : this.guiButtonBase.getChildren()){
			if (guiElement instanceof GuiButton button && button.onMouseClick((int) d, (int) b, p_97750_)) return true;
		}
		for (GuiElement guiElement : this.guiGuideBase.getChildren()){
			if (guiElement instanceof GuiButton button && button.onMouseClick((int) d, (int) b, p_97750_)) return true;
		}
		return super.mouseClicked(d, b, p_97750_);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(new ResourceLocation("weapon_catalog:textures/screens/weapon_catalog.png"),
			this.leftPos - 39, this.topPos - 20, 0, 0, 248, 190, 248, 190);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;

		this.guiDefault.updateFocusState(x, y, gx, gy);
		this.guiDefault.draw(guiGraphics, x, y, this.width, this.height, gx, gy, 1.0F);

		RenderSystem.disableBlend();
	}
	public itemtooltip tootipl = null;
	public class itemtooltip{

		public  ItemStack stack;
		public int x, y;


		public itemtooltip(ItemStack stack, int x, int y) {
			this.stack = stack;
			this.x = x;
			this.y = y;
		}
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
	}

	@Override
	public void init() {
		super.init();
	}
}
