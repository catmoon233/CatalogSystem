package net.exmo.catalogsystem.content;

import com.mojang.serialization.Codec;
import net.exmo.catalogsystem.util.ItemSelector;
import net.exmo.exmodifier.util.AttriGether;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeCatalog extends Catalog implements ItemGather<AttriGether>{
    Codec<AttributeCatalog> codec;

    private Map<ItemSelector,AttriGether[] > itemGather = new HashMap<>();

    public AttributeCatalog() {
    }

    @Override
    public Map<ItemSelector, AttriGether[]> get() {
        return itemGather;
    }
    public AttributeCatalog addEntry(ItemSelector itemSelector, AttriGether[] attriGether)
    {
        itemGather.put(itemSelector, attriGether);
        return  this;
    }


}