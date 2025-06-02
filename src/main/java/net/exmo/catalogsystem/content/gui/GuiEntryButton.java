package net.exmo.catalogsystem.content.gui;

import net.exmo.catalogsystem.content.Catalog;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import se.mickelus.mutil.gui.GuiButton;
import se.mickelus.mutil.gui.GuiRect;

import java.util.Map;

import static net.exmo.catalogsystem.content.gui.GuiPaddingButtonBase.*;
import static net.exmo.catalogsystem.content.gui.GuiToggleButtonBase.*;

public class GuiEntryButton {
    String text;
    Runnable onClick;
    int index;
    Map.Entry<ResourceLocation, Catalog> entry;

    public GuiEntryButton(Map.Entry<ResourceLocation, Catalog> entry, int index) {
        this.entry = entry;
        this.index = index;
        this.text = I18n.get("message.catalog_system.gui.total_catalog." + entry.getKey().toString());
    }

    public void registerCallback(Runnable onClick) {
        this.onClick = onClick;
    }

    public Map.Entry<ResourceLocation, Catalog> getEntry() {
        return entry;
    }

    public GuiButton getEntryToggleButton() {
        int columnIndex = index % 3;
        int x = columnIndex * toggleButtonGapWidth;

        int rowIndex = index / 3;
        int y = rowIndex * toggleButtonGapHeight;

        GuiButton button = new GuiButton(x, y, toggleButtonWidth, toggleButtonHeight, text, onClick);
        button.addChild(new GuiRect(0, 0, toggleButtonWidth, toggleButtonHeight, 0x00ff00));//located pos
        return button;
    }

    public GuiButton getEntryPaddingButton() {
        int x = index * paddingButtonGapWidth;

        GuiButton button = new GuiButton(x, 0, paddingButtonWidth, paddingButtonHeight, text, onClick);
        button.addChild(new GuiRect(0, 0, paddingButtonWidth, paddingButtonHeight, 0x00ff00));//located pos
        return button;
    }
}
