package net.exmo.catalogsystem.content;

import net.exmo.catalogsystem.util.ItemSelector;

import java.util.List;
import java.util.Map;

public interface ItemGather<T> {
     Map<ItemSelector, T[]> get();

}
