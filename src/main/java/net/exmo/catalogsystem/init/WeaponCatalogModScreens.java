
package net.exmo.catalogsystem.init;

import net.exmo.catalogsystem.content.gui.screen.CatalogEntryScreen;
import net.exmo.catalogsystem.content.gui.screen.CatalogTotalScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WeaponCatalogModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(WeaponCatalogModMenus.WEAPON_CATALOG.get(), CatalogTotalScreen::new);
			MenuScreens.register(WeaponCatalogModMenus.CATALOG_ENTRY.get(), CatalogEntryScreen::new);
		});
	}
}
