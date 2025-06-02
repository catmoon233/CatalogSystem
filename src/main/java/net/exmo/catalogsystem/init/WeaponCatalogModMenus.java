
package net.exmo.catalogsystem.init;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.gui.menu.CatalogEntryMenu;
import net.exmo.catalogsystem.content.gui.menu.CatalogTotalMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WeaponCatalogModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Catalogsystem.MODID);
	public static final RegistryObject<MenuType<CatalogTotalMenu>> WEAPON_CATALOG = REGISTRY.register("catalog_total", () -> IForgeMenuType.create(CatalogTotalMenu::new));
	public static final RegistryObject<MenuType<CatalogEntryMenu>> CATALOG_ENTRY = REGISTRY.register("catalog_entry", () -> IForgeMenuType.create(CatalogEntryMenu::new));
}
