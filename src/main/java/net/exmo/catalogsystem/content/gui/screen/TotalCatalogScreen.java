package net.exmo.catalogsystem.content.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.CSRes;
import net.exmo.catalogsystem.content.Catalog;
import net.exmo.catalogsystem.content.CatalogHandle;
import net.exmo.catalogsystem.content.gui.menu.TotalCatalogMenu;
import net.exmo.catalogsystem.network.OpenEntryCatalogMenuButtonMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import se.mickelus.mutil.gui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotalCatalogScreen extends AbstractContainerScreen<TotalCatalogMenu> {
	private final static HashMap<String, Object> guistate = TotalCatalogMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final HashMap<String, String> textstate = new HashMap<>();

	private final static int maxButtonNum = 6;
	private final static int buttonGapWidth = 50;
	private final static int buttonGapHeight = 50;
	private final static int buttonWidth = 30;
	private final static int buttonHeight = 30;
	private int buttonNum;

	// GuiButton extends GuiClickable extends GuiElement
	private GuiElement guiButtonsBase; //Base for all buttons
	private GuiElement guiDefault;
	private final Map<ResourceLocation,Catalog> catalogs = CatalogHandle.getCatalogs();
	private Map<Integer,List<Map.Entry<ResourceLocation,Catalog>>> pages;

	private int page ;
	private void pagesSet() {
		pages = new HashMap<>();
		int index = 0;
		int page = 0;
		List<Map.Entry<ResourceLocation, Catalog>> currentPageEntries = new ArrayList<>();

		for (Map.Entry<ResourceLocation, Catalog> entry : catalogs.entrySet()) {
			if (index == 0) {
				// 新页面开始时初始化列表
				currentPageEntries = new ArrayList<>();
				pages.put(page, currentPageEntries);
			}

			currentPageEntries.add(entry);
			index++;

			if (index >= 6) {
				// 当前页面已满，准备下一页
				page++;
				index = 0;
			}
		}

		// 如果最后一个页面不满6个条目，仍然需要保存
		if (index > 0 && !pages.containsKey(page)) {
			pages.put(page, currentPageEntries);
		}
	}

	public TotalCatalogScreen(TotalCatalogMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.buttonNum = 0;
		this.page = 0;
		pagesSet();
		// 1 2 3
		// 4 5 6
		//add
		this.guiDefault = new GuiElement(0, 0, this.imageWidth, this.imageHeight);
		this.guiDefault.addChild(new GuiRect(0, 0, this.imageWidth, this.imageHeight, 0xff0000));
//		guiDefault.setAttachment(GuiAttachment.middleCenter);
		//TODO: Menu Texture

		int guiButtonBaseWidth = buttonWidth + 2 * buttonGapWidth;
		int guiButtonBaseHeight = buttonHeight + buttonGapHeight;
		int guiButtonBaseOffsetX = (this.imageWidth - buttonWidth - 2 * buttonGapWidth) / 2;
		int guiButtonBaseOffsetY = (this.imageHeight - buttonHeight - buttonGapHeight) / 2;
		this.guiButtonsBase = new GuiElement(guiButtonBaseOffsetX, guiButtonBaseOffsetY, guiButtonBaseWidth, guiButtonBaseHeight);
		this.guiButtonsBase.addChild(new GuiRect(0, 0, guiButtonBaseWidth, guiButtonBaseHeight, 0x0000ff));

//		guiButtonsBase.setAttachment(GuiAttachment.middleCenter);

		for(int i = 0; i < pages.get(page).size(); ++i) {
			// TODO: should text for button diff?
			Map.Entry<ResourceLocation, Catalog> resourceLocationCatalogEntry = pages.get(page).get(i);
			String buttonText = I18n.get("message.catalog_system.gui.total_catalog." + resourceLocationCatalogEntry.getKey().toString());

			GuiButton guiButton = new GuiButton(getButtonX(i) - guiButtonBaseWidth / 2, getButtonY(i) - guiButtonBaseHeight / 2,
					buttonWidth, buttonHeight, buttonText,
					() -> openCatalogEntry(resourceLocationCatalogEntry.getKey().toString()));
			guiButton.addChild(new GuiRect(0, 0, buttonWidth, buttonHeight, 0x00ff00));
//			guiButton.setAttachment(GuiAttachment.topLeft);
//			int offsetx = 0;
//			if (i >= 3){
//				offsetx += (i-3)*100;
//			}else offsetx += (i)*100;
//			int offsety = 0;
//			if (i>=3){
//				offsety = 100;
//			}

			ResourceLocation resourceLocation = CSRes.weaponCatalogMenu;
			ResourceLocation guiTexture = resourceLocationCatalogEntry.getValue().getGuiTexture();
			if (guiTexture != null) resourceLocation = guiTexture;
//			this.guiButtonsBase.addChild(new GuiTexture(getButtonX(i), getButtonY(i), buttonWidth, buttonHeight, resourceLocation));
			this.guiButtonsBase.addChild(guiButton);
		}
		this.guiDefault.addChild(this.guiButtonsBase);
	}
//	private int getW2(){
//		return this.width/2;
//	}
//	private int getH2(){
//		return this.height/2;
//	}

	private int getButtonX(int index){
		int columnIndex = index % 3;
		int buttonOffsetX = buttonGapWidth + buttonWidth/2;
		return buttonOffsetX + columnIndex * buttonGapWidth;
	}

	private int getButtonY(int index){
		int rowIndex = index / 3;
		int buttonOffsetY = buttonGapHeight/2 + buttonHeight/2;
		return buttonOffsetY + rowIndex * buttonGapHeight;
	}

	public void openCatalogEntry(String id){
		textstate.put("id", id);
		Catalogsystem.PACKET_HANDLER.sendToServer(new OpenEntryCatalogMenuButtonMessage(0, x, y, z, textstate));
		OpenEntryCatalogMenuButtonMessage.handleButtonAction(entity, 0, x, y, z, textstate);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		buttonNum = pages.get(0).size()+1;
		if (tootipl!=null){
			guiGraphics.renderTooltip(font, tootipl.stack, tootipl.x, tootipl.y);
			tootipl =null;

		}
	}

	//TODO: get Button Number
	public void setButtonNum(int num)
	{
		if (num > maxButtonNum)
		{
			this.buttonNum = maxButtonNum;
			//TODO: ERROR process, should we add more for next page?
		} else {
			this.buttonNum = Math.max(0, num);
		}
	}

	private int getButtonNum()
	{
		return this.buttonNum;
	}


	@Override
	public boolean mouseClicked(double d, double b, int p_97750_) {
		for (GuiElement guiElement : this.guiButtonsBase.getChildren()){
			if( ((GuiButton) guiElement).onMouseClick((int) d, (int) b, p_97750_)) return true;
//			if ( d >= guiElement.getX() && d <= guiElement.getX() + guiElement.getWidth() && b >= guiElement.getY() && b <= guiElement.getY() + guiElement.getHeight()){
//				if (guiElement instanceof GuiButton){
//
//				}
//			}
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

//		this.guiButtonsBase.updateFocusState(x, y, gx, gy);
//		this.guiButtonsBase.draw(guiGraphics, x, y, this.width, this.height, gx, gy, 1.0F);



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
