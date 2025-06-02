
package net.exmo.catalogsystem.content.gui.menu;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.init.WeaponCatalogModMenus;
import net.exmo.exmodifier.Exmodifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CatalogTotalMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guiState = new HashMap<>();
	public final Level world;
	public final Player player;
	public int x, y, z;
	public int pageIndex;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;
	public String id;

	public CatalogTotalMenu(int id, Inventory inv, FriendlyByteBuf extraData, int pageIndex) {
		super(WeaponCatalogModMenus.WEAPON_CATALOG.get(), id);
		this.pageIndex = pageIndex;
		this.player = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(0);
		Catalogsystem.LOGGER.info("1draw page: {}", pageIndex);
//		if (extraData != null) {
//			try {
//				BlockPos pos = extraData.readBlockPos();  // Ensure this does not throw an exception
//				this.x = pos.getX();
//				this.y = pos.getY();
//				this.z = pos.getZ();
//				access = ContainerLevelAccess.create(world, pos);
//			} catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
//				ex.printStackTrace();
//				throw new RuntimeException("Failed to read block position from buffer", ex);
//			}
//		}
		try {
			this.id = extraData.readUtf();
		}catch (IllegalArgumentException ex){
			Exmodifier.LOGGER.error("Failed to read block position from buffer",ex);
		}
	}

	public CatalogTotalMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		this(id, inv, extraData, 0);
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
			if (this.boundItemMatcher != null)
				return this.boundItemMatcher.get();
			else if (this.boundBlockEntity != null)
				return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
			else if (this.boundEntity != null)
				return this.boundEntity.isAlive();
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
