package net.exmo.catalogsystem.content;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.init.SBItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.event.CatalogRegisterEvent;
import net.exmo.catalogsystem.util.ItemSelector;
import net.exmo.exmodifier.util.AttriGether;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static net.exmo.catalogsystem.Catalogsystem.manager;

public class CatalogHandle {
    private static Map<ResourceLocation, Catalog> catalogs = new HashMap<>();

    public static Map<ResourceLocation, Catalog> getCatalogs() {
        return catalogs;
    }

    public static void init() {

    }

    public static void registerCatalog(ResourceLocation id, Catalog catalog) {
        catalogs.put(id, catalog);
        Catalogsystem.LOGGER.debug("Registered Catalog: " + id);
    }

    @Mod.EventBusSubscriber
    public static class CommonEvent {


        @SubscribeEvent
        public static void onCatalogRegister(CatalogRegisterEvent event) {
            catalogs = new HashMap<>();
            AttributeCatalog catalog = new AttributeCatalog();
            for (Item item1 : ForgeRegistries.ITEMS.getValues()) {
                ItemStack item = item1.getDefaultInstance();
                double damage = item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream()
                        .mapToDouble(AttributeModifier::getAmount).sum();
                if (damage <= 0) continue;
                AttriGether[] attriGether = new AttriGether[]{new AttriGether(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.nameUUIDFromBytes((item.toString()).getBytes()), item.getDisplayName().toString(), damage * 0.05, AttributeModifier.Operation.ADDITION))};
                catalog.addEntry
                        (ItemSelector.fromItem(item.getItem()), attriGether);
            }
            event.registerCatalog(new ResourceLocation("catalogsystem", "weapon_catalog"), catalog);
            AttributeCatalog catalog1 = new AttributeCatalog();
            for (Item item1 : ForgeRegistries.ITEMS.getValues()) {
                ItemStack item = item1.getDefaultInstance();
                double armorValue = 0;
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    armorValue += item.getAttributeModifiers(slot).get(Attributes.ARMOR).stream()
                            .mapToDouble(AttributeModifier::getAmount).sum();
                }
                if (armorValue <= 0) continue;
                AttriGether[] attriGether = new AttriGether[]{new AttriGether(Attributes.ARMOR, new AttributeModifier(UUID.nameUUIDFromBytes((item.toString()).getBytes()), item.getDisplayName().toString(), armorValue * 0.03, AttributeModifier.Operation.ADDITION))};
                catalog1.addEntry
                        (ItemSelector.fromItem(item.getItem()), attriGether);
            }
            event.registerCatalog(new ResourceLocation("catalogsystem", "armor_catalog"), catalog1);
            if (ModList.get().isLoaded(SlashBlade.MODID) ) {
                AttributeCatalog catalog2 = new AttributeCatalog();
                List<ItemStack> list=  event.event.getRegistryAccess().registryOrThrow(SlashBladeDefinition.REGISTRY_KEY).asLookup().listElements().sorted(SlashBladeDefinition.COMPARATOR)
                        .map(e -> e.get().getBlade()).toList();

                for (ItemStack item : list) {

                    double attackDamage = 0;
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        attackDamage += item.getAttributeModifiers(slot).get(Attributes.ATTACK_DAMAGE).stream()
                                .mapToDouble(AttributeModifier::getAmount).sum();
                    }
                    if (attackDamage <= 0) continue;
                    AttriGether[] attriGether = new AttriGether[]{new AttriGether(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.nameUUIDFromBytes((item.toString()).getBytes()), item.getDisplayName().toString(), attackDamage * 0.03, AttributeModifier.Operation.ADDITION))};
                    CompoundTag e1 = new CompoundTag();
                    CompoundTag tag = new CompoundTag();
                    tag.putString("translationKey",item.getOrCreateTagElement("bladeState").getString("translationKey"));
                    e1.put("bladeState",tag );
                    catalog2.addEntry
                            (new ItemSelector(SBItems.slashblade,null,List.of(
                                    e1
                            ), ItemSelector.CompareType.NBT,null,item), attriGether);
                }
                event.registerCatalog(new ResourceLocation("catalogsystem", "slashblade_catalog"), catalog2);

            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void AtReload(AddReloadListenerEvent event) {
            CatalogRegisterEvent event1 = new CatalogRegisterEvent(catalogs);
            event1.event = event;
            MinecraftForge.EVENT_BUS.post(event1);
            catalogs.clear();
            event1.getCatalogs().forEach(CatalogHandle::registerCatalog);
            manager.init();
        }
    }

    public static List<ItemSelector> getItemSelector(ItemStack stack) {
        return catalogs.values().stream().flatMap(catalog -> catalog.get().entrySet().stream()).filter(entry -> entry.getKey().compare(stack)).map(Map.Entry::getKey).toList();
    }
    public static boolean hasItemSelector(ItemSelector itemSelector) {
        return catalogs.values().stream().anyMatch(catalog -> catalog.get().containsKey(itemSelector));
    }
    public static List<Catalog> getCatalogFromItemSelector(ItemSelector itemSelector) {
        return catalogs.values().stream().filter(catalog -> catalog.get().containsKey(itemSelector)).toList();
    }
    public static List<Catalog> getCatalogFromItemSelectors(List<ItemSelector> itemSelectors) {
        return catalogs.values().stream().filter(catalog -> itemSelectors.stream().anyMatch(itemSelector -> catalog.get().containsKey(itemSelector))).toList();
    }

}



