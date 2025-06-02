
package net.exmo.catalogsystem.init;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.network.OpenCatalogTotalMenuKeyMessage;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class CatalogSystemModKeyMappings {
	public static final KeyMapping OPEN_CATALOG_KEY = new KeyMapping("key.catalog_system.open_catalog_system_key", GLFW.GLFW_KEY_MINUS, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				Catalogsystem.PACKET_HANDLER.sendToServer(new OpenCatalogTotalMenuKeyMessage(0, 0));
				OpenCatalogTotalMenuKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_CATALOG_KEY);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				OPEN_CATALOG_KEY.consumeClick();
			}
		}
	}
}
