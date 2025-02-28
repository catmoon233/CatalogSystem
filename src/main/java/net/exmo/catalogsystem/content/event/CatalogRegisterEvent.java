package net.exmo.catalogsystem.content.event;

import net.exmo.catalogsystem.content.Catalog;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;

public class CatalogRegisterEvent  extends Event {
    private Map<ResourceLocation, Catalog> catalogs;
    public Map<ResourceLocation, Catalog> getCatalogs()
    {
        return catalogs;
    }
    public void registerCatalog(String modid,String id,Catalog catalog)
    {
        this.catalogs.put(new ResourceLocation(modid,id),catalog);
    }
    public void registerCatalog(ResourceLocation id,Catalog catalog)
    {
        this.catalogs.put(id,catalog);
    }    public  CatalogRegisterEvent(Map<ResourceLocation, Catalog> catalogs)
    {
        this.catalogs = catalogs;
    }
}
