package net.exmo.catalogsystem.content;

import net.exmo.exmodifier.util.AttriGether;
import net.minecraft.resources.ResourceLocation;

public abstract class Catalog implements ItemGather<AttriGether> {
    private ResourceLocation guiTexture;

    public ResourceLocation getGuiTexture() {
        return guiTexture;
    }

    public Catalog setGuiTexture(ResourceLocation guiTexture) {
        this.guiTexture = guiTexture;
        return this;
    }
}