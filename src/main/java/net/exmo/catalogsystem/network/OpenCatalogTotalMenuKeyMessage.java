
package net.exmo.catalogsystem.network;


import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.gui.menu.CatalogTotalMenu;
import net.exmo.catalogsystem.util.PlayerUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.exmo.catalogsystem.Catalogsystem.manager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OpenCatalogTotalMenuKeyMessage {
    private static int pressedms1;
    int type, pressedms;

	public OpenCatalogTotalMenuKeyMessage(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public OpenCatalogTotalMenuKeyMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(OpenCatalogTotalMenuKeyMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(OpenCatalogTotalMenuKeyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		if (message.type == 0) {
			context.enqueueWork(() -> pressAction(context.getSender(), message.type, message.pressedms));
		}
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
        Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {
			manager.resetScreen();
			manager.openScreen(() -> new PlayerUtil.MenuGen<>(CatalogTotalMenu.class).OpenGui(entity, Component.literal("Catalog")));
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		Catalogsystem.addNetworkMessage(OpenCatalogTotalMenuKeyMessage.class, OpenCatalogTotalMenuKeyMessage::buffer, OpenCatalogTotalMenuKeyMessage::new, OpenCatalogTotalMenuKeyMessage::handler);
	}
}
