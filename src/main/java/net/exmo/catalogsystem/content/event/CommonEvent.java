package net.exmo.catalogsystem.content.event;

import net.exmo.catalogsystem.content.Catalog;
import net.exmo.catalogsystem.content.CatalogHandle;
import net.exmo.catalogsystem.network.CatalogModVariables;
import net.exmo.catalogsystem.util.ItemSelector;
import net.exmo.exmodifier.util.AttriGether;
import net.exmo.exmodifier.util.EntityAttrUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.exmo.exmodifier.content.modifier.ModifierHandle.percentAtr;
import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mod.EventBusSubscriber
public class CommonEvent {
    @SubscribeEvent
    public static void UnLock(EntityItemPickupEvent event){
        UnLockFunction(event.getEntity(), event.getItem().getItem());
    }
    @SubscribeEvent
    public static void UnLock3(LivingEquipmentChangeEvent event){
        if (event.getEntity() instanceof Player player) {
            UnLockFunction(player, event.getTo());
            UnLockFunction(player, event.getFrom());
        }
    }
    @SubscribeEvent
    public static void UnLock2(ItemStackedOnOtherEvent event){
        UnLockFunction(event.getPlayer(), event.getCarriedItem());
        UnLockFunction(event.getPlayer(), event.getStackedOnItem());

    }

    public static void UnLockFunction(Player player , ItemStack stack){
        if (player.level().isClientSide)return;
        player.getCapability(CatalogModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
            var s = CatalogHandle.getItemSelector(stack);
            if (s.isEmpty())return;
            for (ItemSelector s1 : s) {
                if (!CatalogHandle.hasItemSelector(s1)) return;
                if (capability.HadWeaponGather.stream().filter(e -> e.compareItemSelector(s1)).toList().isEmpty()) {
                    List<ItemSelector> list2 = capability.HadWeaponGather;
                    list2.add(s1);
                    capability.HadWeaponGather = list2;
                    capability.syncPlayerVariables(player);
                    player.displayClientMessage(Component.translatable("message.catalogsystem.unlock_item").append(stack.getDisplayName()), false);
                    MinecraftForge.EVENT_BUS.post(new AddItemEvent(player, stack));
                }

            }
        });
    }
    @SubscribeEvent
    public static void ToolTipRender(ItemTooltipEvent event) {

        ItemStack itemStack = event.getItemStack();
        if (itemStack.getTag()==null)return;
        if (event.getEntity() ==null)return;
        if (!itemStack.getTag().getBoolean("CateLogItem"))return;
       // if (LoadCatalog.WeaponCatalog.containsKey(item)){
        Player player = event.getEntity();
        AtomicBoolean has = new AtomicBoolean(false);
        List<ItemSelector> itemSelector1 = CatalogHandle.getItemSelector(itemStack);
            player.getCapability(CatalogModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                List<ItemSelector> hadWeaponGather = new ArrayList<>(capability.HadWeaponGather);

                for (ItemSelector itemSelector : itemSelector1) {
                    hadWeaponGather=  hadWeaponGather.stream().filter(e -> e.compareItemSelector(itemSelector)).toList();
                }
                has.set(!hadWeaponGather.isEmpty());
            });


            MutableComponent state ;
            if (has.get()){
                state = Component.translatable("tooltip.catalog_system.unlock_item");
            }else{
                state = Component.translatable("tooltip.catalog_system.lock_item");
            }
        event.getToolTip().add(state.append(Component.translatable("tooltip.catalog_system.unlock_item_add_attribute")));
        List<Catalog> catalogs = CatalogHandle.getCatalogFromItemSelectors(itemSelector1);
        List<AttriGether> weaponAttributes = new ArrayList<>();
        for (Catalog catalog : catalogs) {
            for (ItemSelector itemSelector : itemSelector1) {
                weaponAttributes.addAll(List.of(catalog.get().get(itemSelector)));
            }
        }
        for (AttriGether weaponAttribute : weaponAttributes){
            event.getToolTip().add(weaponAttribute.generateTooltipBase());
//                AttributeModifier attributemodifier = weaponAttribute.getModifier();
//                Attribute attribute = weaponAttribute.getAttribute();
//                if (attribute == null)continue;
//                if (attributemodifier ==null)continue;
//                //    if (modifierAttriGether.slot==null)continue;
//                //  Exmodifier.LOGGER.info(modifierAttriGether.getAttribute().getDescriptionId());
//                //   if (!itemStack.getAttributeModifiers(modifierAttriGether.slot).containsEntry(attribute, attributemodifier))continue;
//                double d0 = attributemodifier.getAmount();
//                boolean flag = false;
//                String percent = "";
//                double d1;
//                if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL  &&!percentAtr.contains(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString())) {
//                    if ((attribute).equals(Attributes.KNOCKBACK_RESISTANCE)) {
//                        d1 = d0 * 10.0;
//                    } else {
//                        d1 = d0;
//                    }
//                } else {
//                    d1 = d0 * 100.0;
//                }
//                String amouta2 = "";
//                if (percentAtr.contains(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString())){
//                    percent = "%";
//                    DecimalFormat df = new DecimalFormat("#.####");
//                    amouta2 = df.format(attributemodifier.getAmount() * 100);
//                    if (weaponAttribute.attribute.getDescriptionId().length() >=4){
//                        if (ForgeRegistries.ATTRIBUTES.getKey(attribute).toString().startsWith("twtp") ||ForgeRegistries.ATTRIBUTES.getKey(attribute).toString().startsWith("isfix") ) {
//                            amouta2 = df.format(attributemodifier.getAmount()) ;
//                        }
//                    }
//                }
//
//                if (flag) {
//                    event.getToolTip().add((Component.literal(" ")).append(Component.translatable("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(attribute.getDescriptionId())})).withStyle(ChatFormatting.DARK_GREEN));
//                } else if (d0 > 0.0) {
//                    if (percent.equals("%")) event.getToolTip().add(Component.translatable("add").append(amouta2).append(percent).append(" ").append(Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.BLUE));
//                    else event.getToolTip().add((Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(attribute.getDescriptionId())})).withStyle(ChatFormatting.BLUE));
//                } else if (d0 < 0.0) {
//                    d1 *= -1.0;
//                    if (percent.equals("%")) event.getToolTip().add(Component.translatable("subtract").append(amouta2).append(percent).append(" ").append(Component.translatable(attribute.getDescriptionId())).withStyle(ChatFormatting.RED));
//                    else  event.getToolTip().add((Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(attribute.getDescriptionId())})).withStyle(ChatFormatting.RED));
//                }
      //       }
        }

    }
//    @SubscribeEvent
//    public static void OnPlayerReSpawn(PlayerEvent.PlayerRespawnEvent event) {
//        Player player = event.getEntity();
//        if (player.level().isClientSide)return;
//        player.getCapability(.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
//            for (String item : capability.HadWeaponGather) {
//                List<AttriGether> attrItem = LoadCatalog.WeaponCatalog.get(item);
//                if (attrItem==null)continue;
//                for (AttriGether attriGether : attrItem){
//                    if (attriGether.attribute==null)continue;
//                    EntityAttrUtil.entityAddAttrTF(attriGether.attribute ,attriGether.getModifier(),player, EntityAttrUtil.WearOrTake.WEAR);
//                }
//            }
//        });
//    };

}
