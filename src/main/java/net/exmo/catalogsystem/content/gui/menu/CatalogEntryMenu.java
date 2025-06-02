
package net.exmo.catalogsystem.content.gui.menu;

import net.exmo.catalogsystem.init.WeaponCatalogModMenus;
import net.exmo.exmodifier.Exmodifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class CatalogEntryMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guiState = new HashMap<>();
	public final Level world;
	public final Player player;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	private Entity boundEntity = null;
	private BlockEntity boundBlockEntity = null;
	public String id;
	public CatalogEntryMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(WeaponCatalogModMenus.CATALOG_ENTRY.get(), id);
		this.player = inv.player;
		this.world = inv.player.level();


//		if (extraData != null) {
//			try {
//				CompoundTag compoundTag = extraData.readNbt();
//			if (compoundTag != null) {
//				this.id = compoundTag.getString("id");
//				this.internal = new ItemStackHandler(0);
//				this.x = (int) compoundTag.getDouble("x");
//				this.y = (int) compoundTag.getDouble("y");
//				this.z = (int) compoundTag.getDouble("z");
//				access = ContainerLevelAccess.create(world, new BlockPos(x, y, z));
//			}
//			} catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
//			//	ex.printStackTrace();
//			//	throw new RuntimeException("Failed to read block position from buffer", ex);
//			}
//		}
		try {
			this.id = extraData.readUtf();

		}catch (IllegalArgumentException ex){
			Exmodifier.LOGGER.error("Failed to read block position from buffer",ex);
		}
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
