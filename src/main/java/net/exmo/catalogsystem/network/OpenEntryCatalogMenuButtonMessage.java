
package net.exmo.catalogsystem.network;


import io.netty.buffer.Unpooled;
import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.gui.menu.CatalogEntryMenu;
import net.exmo.catalogsystem.content.gui.menu.TotalCatalogMenu;
import net.exmo.catalogsystem.util.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OpenEntryCatalogMenuButtonMessage {
	private final int buttonID, x, y, z;
	private HashMap<String, String> textstate;

	public OpenEntryCatalogMenuButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.textstate = readTextState(buffer);
	}

	public OpenEntryCatalogMenuButtonMessage(int buttonID, int x, int y, int z, HashMap<String, String> textstate) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.textstate = textstate;

	}

	public static void buffer(OpenEntryCatalogMenuButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		writeTextState(message.textstate, buffer);
	}

	public static void handler(OpenEntryCatalogMenuButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			HashMap<String, String> textstate = message.textstate;
			handleButtonAction(entity, buttonID, x, y, z, textstate);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z, HashMap<String, String> textstate) {
		Level world = entity.level();
		HashMap guistate = TotalCatalogMenu.guistate;
		for (Map.Entry<String, String> entry : textstate.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			guistate.put(key, value);
		}
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
	//		CompoundTag tag = new CompoundTag();
			String string = textstate.get("id");
//			tag.putString("id", id);
			if (entity instanceof ServerPlayer _ent) {
				NetworkHooks.openScreen(_ent, new MenuProvider() {

					@Override
					public Component getDisplayName() {
						return Component.translatable("message.catalog_system.gui.total_catalog."+string);
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
							FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
							buf.writeUtf(string);
							return new CatalogEntryMenu(id, inventory, buf);
					}
				}, buf -> {buf.writeUtf(string);});
			}		}

	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		Catalogsystem.addNetworkMessage(OpenEntryCatalogMenuButtonMessage.class, OpenEntryCatalogMenuButtonMessage::buffer, OpenEntryCatalogMenuButtonMessage::new, OpenEntryCatalogMenuButtonMessage::handler);
	}

	public static void writeTextState(HashMap<String, String> map, FriendlyByteBuf buffer) {
		buffer.writeInt(map.size());
		for (Map.Entry<String, String> entry : map.entrySet()) {
			buffer.writeUtf(entry.getKey());
			buffer.writeUtf(entry.getValue());
		}
	}

	public static HashMap<String, String> readTextState(FriendlyByteBuf buffer) {
		int size = buffer.readInt();
		HashMap<String, String> map = new HashMap<>();
		for (int i = 0; i < size; i++) {
			String key = buffer.readUtf();
			String value = buffer.readUtf();
			map.put(key, value);
		}
		return map;
	}
}
