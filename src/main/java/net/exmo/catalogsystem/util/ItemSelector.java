package net.exmo.catalogsystem.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.security.PublicKey;
import java.util.List;
import java.util.Objects;

public record ItemSelector(Item item, String itemId, List<CompoundTag> containNBT,
                           net.exmo.catalogsystem.util.ItemSelector.CompareType type, List<TagKey<Item>> containTag,ItemStack renderStack) {


    public enum CompareType {
        TAG,
        ID,
        ITEM,
        NBT,
        NBT_AND_ITEM,
        NBT_AND_ID
    }

    /**
     * 构造一个新的ItemSelector。
     *
     * @param item       要比较的物品实例
     * @param itemId     物品ID字符串（如果适用）
     * @param containNBT 包含的NBT标签列表（如果适用）
     * @param type       比较类型
     * @param containTag 包含的标签列表（如果适用）
     */
    public ItemSelector(Item item, String itemId, List<CompoundTag> containNBT, CompareType type, List<TagKey<Item>> containTag, ItemStack renderStack) {
        this.item = item;
        this.itemId = itemId;
        this.containNBT = containNBT != null ? containNBT : List.of();
        this.type = type;
        this.containTag = containTag != null ? containTag : List.of();
        this.renderStack = renderStack;
    }

    //    public static ItemSelector formItemStack(ItemStack stack){
//
//    }
    public static ItemSelector fromItem(Item item) {
        return new ItemSelector(item, null, null, CompareType.ITEM, null,null);
    }

    /**
     * 比较给定的ItemStack是否符合选择器的标准。
     *
     * @param stack 要比较的ItemStack
     * @return 如果ItemStack符合选择器的标准，则返回true；否则返回false
     */
    public boolean compare(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return switch (type) {
            case TAG -> checkTags(stack);
            case ID -> Objects.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString(), itemId);

            case ITEM -> Objects.equals(stack.getItem(), item);
            case NBT -> checkNBT(stack);
            case NBT_AND_ITEM -> Objects.equals(stack.getItem(), item) && checkNBT(stack);
            case NBT_AND_ID ->
                    Objects.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString(), itemId) && checkNBT(stack);
            default -> false;
        };
    }
    public boolean compareItemSelector(ItemSelector itemSelector){
        if (itemSelector.item != null && item!=null&& itemSelector.item != item) return false;
        if (itemSelector.itemId !=null&&itemId!=null && !itemSelector.itemId.equals(itemId)) return false;
        if (itemSelector.containNBT !=null&&containNBT !=null && !itemSelector.containNBT.equals(containNBT)) return false;
        if (itemSelector.containTag !=null && containTag !=null &&!itemSelector.containTag.equals(containTag)) return false;

        return true;
    }

    private boolean checkTags(ItemStack stack) {
        for (TagKey<Item> tag : containTag) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNBT(ItemStack stack) {
        CompoundTag stackTag = stack.getTag();
        if (stackTag == null) return containNBT.isEmpty();

        for (CompoundTag conditionNBT : containNBT) {
            if (isSubset(conditionNBT, stackTag)) {
                return true;
            }
        }
        return false;
    }

    // 递归检查条件NBT是否为堆叠NBT的子集
    private boolean isSubset(CompoundTag condition, CompoundTag target) {
        for (String key : condition.getAllKeys()) {
            Tag conditionTag = condition.get(key);
            Tag targetTag = target.get(key);

            if (targetTag == null) return false;

            if (conditionTag instanceof CompoundTag conditionCompound) {
                if (!(targetTag instanceof CompoundTag targetCompound) ||
                        !isSubset(conditionCompound, targetCompound)) {
                    return false;
                }
            } else if (!conditionTag.equals(targetTag)) {
                return false;
            }
        }
        return true;
    }



    /**
     * 将ItemSelector对象序列化为NBT数据。
     *
     * @return 表示ItemSelector的CompoundTag。
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        // 序列化Item或ItemId
        if (item != null) {
            tag.putString("ItemId", ForgeRegistries.ITEMS.getKey(item).toString());
        } else if (itemId != null && !itemId.isEmpty()) {
            tag.putString("ItemId", itemId);
        }

        // 序列化CompareType
        tag.putString("CompareType", type.name());
        if (renderStack!=null) {
            tag.put("renderItem",renderStack.serializeNBT());
        }


        // 序列化ContainNBT
        ListTag nbtList = new ListTag();
        for (CompoundTag nbt : containNBT) {
            nbtList.add(nbt);
        }
        tag.put("ContainNBT", nbtList);

        // 序列化ContainTag
        ListTag tagList = new ListTag();
        for (TagKey<Item> tagKey : containTag) {
            tagList.add(StringTag.valueOf(tagKey.location().toString()));
        }
        tag.put("ContainTag", tagList);

        return tag;
    }

    /**
     * 从NBT数据反序列化出ItemSelector对象。
     *
     * @param tag 包含ItemSelector信息的CompoundTag。
     * @return 反序列化的ItemSelector对象。
     */
    public static ItemSelector fromNBT(CompoundTag tag) {
        Item item = null;
        String itemId = tag.getString("ItemId");
        if (!itemId.isEmpty()) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        }

        CompareType type = CompareType.valueOf(tag.getString("CompareType"));

        List<CompoundTag> containNBT = new java.util.ArrayList<>();
        if (tag.contains("ContainNBT", Tag.TAG_LIST)) {
            ListTag nbtList = tag.getList("ContainNBT", Tag.TAG_COMPOUND);
            for (Tag nbtTag : nbtList) {
                if (nbtTag instanceof CompoundTag compoundTag) {
                    containNBT.add(compoundTag);
                }
            }
        }

        List<TagKey<Item>> containTag = new java.util.ArrayList<>();
        if (tag.contains("ContainTag", Tag.TAG_LIST)) {
            ListTag tagList = tag.getList("ContainTag", Tag.TAG_STRING);
            for (Tag stringTag : tagList) {
                if (stringTag instanceof StringTag stringTagInstance) {
                    containTag.add(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(stringTagInstance.getAsString())));
                }
            }
        }
        ItemStack renderStack = null;
        if (tag.contains("renderItem"))renderStack = ItemStack.of(tag.getCompound("renderItem"));
        return new ItemSelector(item, itemId, containNBT, type, containTag,renderStack);
    }
}