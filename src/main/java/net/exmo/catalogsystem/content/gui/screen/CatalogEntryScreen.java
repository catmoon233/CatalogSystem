package net.exmo.catalogsystem.content.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.exmo.catalogsystem.content.Catalog;
import net.exmo.catalogsystem.content.CatalogHandle;
import net.exmo.catalogsystem.content.gui.menu.CatalogEntryMenu;
import net.exmo.catalogsystem.content.gui.menu.TotalCatalogMenu;
import net.exmo.catalogsystem.network.CatalogModVariables;
import net.exmo.catalogsystem.util.ItemSelector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogEntryScreen extends AbstractContainerScreen<CatalogEntryMenu> {
	private final static HashMap<String, Object> guistate = TotalCatalogMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private final HashMap<String, String> textstate = new HashMap<>();

	private final Catalog catalog;

	private List<ItemSelector> finalList; // 新增字段，用于存储最终列表
	private List<ItemSelector> hadList_; // 新增字段，用于存储最终列表
	private int hasCount; // 新增字段，用于存储已拥有物品的数量

	public CatalogEntryScreen(CatalogEntryMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.catalog = CatalogHandle.getCatalogs().get(ResourceLocation.tryParse(container.id));
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;

		// 初始化时处理 finalList 和 hasCount
		entity.getCapability(CatalogModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			capability.syncPlayerVariables(entity);

			 hadList_ = new ArrayList<>(capability.HadWeaponGather);
			hadList_.removeIf(itemSelector -> !catalog.get().keySet().stream().filter(itemSelector1 -> !itemSelector.compareItemSelector(itemSelector1)).toList().isEmpty());
			List<ItemSelector> reList = new ArrayList<>(catalog.get().keySet());
			for (ItemSelector itemSelector : hadList_) {
				reList = reList.stream().filter(itemSelector1 -> !itemSelector1.compareItemSelector(itemSelector)).toList();
			}
			this.finalList = new ArrayList<>(hadList_);
			this.finalList.addAll(reList);
			this.hasCount = hadList_.size();
		});
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		this.renderLabels(guiGraphics, mouseX, mouseY);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		if (tootipl!=null){
			guiGraphics.renderTooltip(font, tootipl.stack, tootipl.x, tootipl.y);
			tootipl =null;

		}

	}




	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta > 0 && currentPage > 0) {
			currentPage--; // Scroll up
		} else if (delta < 0 && currentPage < pages.size() - 1) {
			currentPage++; // Scroll down
		}
		return true; // Pass the event to parent
	}

	// Add this at the top of your class
	private Map<Integer, List<ItemSelector>> pages = new HashMap<>(); // Class-level variable to hold pages
	public static Map<Integer, List<ItemSelector>> gatherToStingMapIndex(List<ItemSelector> list, int eachSize)
	{
		Map<Integer,List<ItemSelector>> map = new HashMap<>();
		int index = 0;
		int i = 0;
		List<ItemSelector> _list = new ArrayList<>();
		boolean fir = true;
		for(var	 s:list)
		{
			_list.add(s);
			//map.put(index,s);
			i++;
			if(i>=eachSize)
			{
				map.put(index,_list);
				i = 0;
				index++;
				_list = new ArrayList<>();
				fir = false;
			}
		}
		if (fir)
		{
			map.put(index,_list);
		}
		return map;
	}
	public static List<String> copy (List<String> target,List<String> source)
	{
		List<String> _list = new ArrayList<>();
		_list.addAll(target);
		_list.addAll(source);
		return _list;
	}
	private int currentPage = 0; // Track the current page

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(new ResourceLocation("weapon_catalog:textures/screens/weapon_catalog.png"), this.leftPos - 39, this.topPos - 20, 0, 0, 248, 190, 248, 190);

		entity.getCapability(CatalogModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			pages = gatherToStingMapIndex(finalList, 96); // 使用初始化时计算的 finalList

			List<ItemSelector> currentPageItems = pages.get(currentPage);

			if (currentPageItems != null) {
				int height = -1;
				int gap = 1; // Set the gap size here

				for (int j = 0; j < currentPageItems.size(); j++) {
					if (j % 12 == 0) height++;
					int xPos = this.leftPos - 29 + (18 + gap) * (j % 12); // Adjusted X position with gap
					int yPos = this.topPos + 2 + (18 + gap) * height; // Adjusted Y position with gap

					int finalJ = j;
					if (!hadList_.stream().filter(e -> e.compareItemSelector(currentPageItems.get(finalJ))).toList().isEmpty()) {
						guiGraphics.blit(new ResourceLocation("weapon_catalog:textures/screens/weapon_catalog_slot.png"), xPos, yPos, 0, 0, 18, 18, 18, 18);
					} else {
						guiGraphics.blit(new ResourceLocation("weapon_catalog:textures/screens/weapon_catalog_selected_slot.png"), xPos, yPos, 0, 0, 18, 18, 18, 18);
					}

					Item item = currentPageItems.get(j).item();
					if (item != null) {
						String color = (j + currentPage * 96 >= hasCount) ? "\u00a77" : "\u00a7l";

						ItemStack itemStack = item.getDefaultInstance();
						itemStack.getOrCreateTag().putBoolean("CateLogItem", true);

						// Render the item at the new position
						guiGraphics.renderItem(itemStack, xPos + 1, yPos + 1); // Adjust position slightly for appearance

						// Tooltip handling
						if (gx >= xPos && gx <= xPos + 18 && gy >= yPos && gy <= yPos + 18) {
							this.tootipl = new itemtooltip(itemStack, gx, gy);
						}
					}
				}
			}
		});
		guiGraphics.drawString(this.font, (currentPage+1)+"/"+(pages.size()), this.leftPos - 36, this.topPos - 17, -12829636);

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
