
package net.exmo.catalogsystem.network;


import io.netty.buffer.Unpooled;
import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.gui.menu.CatalogEntryMenu;
import net.exmo.catalogsystem.content.gui.menu.CatalogTotalMenu;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OpenCatalogEntryMenuButtonMessage {
	private final int x, y, z;
	private final int buttonID;
	private final HashMap<String, String> textState;

	public OpenCatalogEntryMenuButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.textState = readTextState(buffer);
	}

	public OpenCatalogEntryMenuButtonMessage(int buttonID, int x, int y, int z, HashMap<String, String> textState) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.textState = textState;
	}

	public static void buffer(OpenCatalogEntryMenuButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		writeTextState(message.textState, buffer);
	}

	public static void handler(OpenCatalogEntryMenuButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			HashMap<String, String> textState = message.textState;
			handleButtonAction(entity, buttonID, x, y, z, textState);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z, HashMap<String, String> textState) {
		Level world = entity.level();
		//TODO: what is guiState used for?
		HashMap<String, Object> guiState = CatalogTotalMenu.guiState;
		for (Map.Entry<String, String> entry : textState.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			guiState.put(key, value);
		}
		// security measure to prevent arbitrary chunk generation
		Catalogsystem.LOGGER.info("t2 xyz: {} {} {}", x, y, z);
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
//			CompoundTag tag = new CompoundTag();
			String string = textState.get("id");
//			tag.putString("id", id);
			if (entity instanceof ServerPlayer _ent) {
				NetworkHooks.openScreen(
					_ent,
					new MenuProvider() {
						@Override
						public @NotNull Component getDisplayName() {
							//total??
							return Component.translatable("message.catalog_system.gui.entry_catalog." + string);
						}
						@Override
						public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
								FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
								buf.writeUtf(string);
							Catalogsystem.LOGGER.info("2draw ???: {}", id);
								return new CatalogEntryMenu(id, inventory, buf);
						}
					},
					buf -> buf.writeUtf(string)
				);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		Catalogsystem.addNetworkMessage(OpenCatalogEntryMenuButtonMessage.class, OpenCatalogEntryMenuButtonMessage::buffer, OpenCatalogEntryMenuButtonMessage::new, OpenCatalogEntryMenuButtonMessage::handler);
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
