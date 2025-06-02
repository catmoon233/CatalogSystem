
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
public class OpenCatalogTotalMenuButtonMessage {
	private final int x, y, z;
	private final int pageIndex;
	private final HashMap<String, String> textState;

	public OpenCatalogTotalMenuButtonMessage(FriendlyByteBuf buffer) {
		this.pageIndex = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.textState = readTextState(buffer);
	}

	public OpenCatalogTotalMenuButtonMessage(int pageIndex, int x, int y, int z, HashMap<String, String> textState) {
		this.pageIndex = pageIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.textState = textState;
	}

	public static void buffer(OpenCatalogTotalMenuButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.pageIndex);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		writeTextState(message.textState, buffer);
	}

	public static void handler(OpenCatalogTotalMenuButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player player = context.getSender();
			int pageIndex = message.pageIndex;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			HashMap<String, String> textState = message.textState;
			handleButtonAction(player, x, y, z, pageIndex, textState);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player player, int x, int y, int z, int pageIndex, HashMap<String, String> textState) {
		Level world = player.level();
		//TODO: what is guiState used for?
		HashMap<String, Object> guiState = CatalogTotalMenu.guiState;
		for (Map.Entry<String, String> entry : textState.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			guiState.put(key, value);
		}
		// security measure to prevent arbitrary chunk generation
		//TODO: x y z is player location(lower)
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		Catalogsystem.LOGGER.info("t3 xyz: {} {} {}", x, y, z);
		String string = textState.get("id");
		if (player instanceof ServerPlayer _ent) {
			NetworkHooks.openScreen(
				_ent,
				new MenuProvider() {
					@Override
					public @NotNull Component getDisplayName() {
						return Component.translatable("message.catalog_system.gui.total_catalog." + string);
					}
					@Override
					public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
						FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
						buf.writeUtf(string);
						Catalogsystem.LOGGER.info("2draw page: {}", pageIndex);
						return new CatalogTotalMenu(id, inventory, buf, pageIndex);
					}
				},
				buf -> buf.writeUtf(string)
			);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		Catalogsystem.addNetworkMessage(OpenCatalogTotalMenuButtonMessage.class, OpenCatalogTotalMenuButtonMessage::buffer, OpenCatalogTotalMenuButtonMessage::new, OpenCatalogTotalMenuButtonMessage::handler);
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
