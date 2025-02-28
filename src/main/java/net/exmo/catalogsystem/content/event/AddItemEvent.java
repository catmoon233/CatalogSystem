package net.exmo.catalogsystem.content.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

public class AddItemEvent extends LivingEvent {
    public ItemStack item;

    public AddItemEvent(LivingEntity entity) {
        super(entity);
    }

    public AddItemEvent(LivingEntity entity, ItemStack item) {
        super(entity);
        this.item = item;
    }
}