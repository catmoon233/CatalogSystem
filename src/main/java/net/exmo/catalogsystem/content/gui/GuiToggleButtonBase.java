package net.exmo.catalogsystem.content.gui;

import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiRect;

import static net.exmo.catalogsystem.Catalogsystem.manager;
import static net.exmo.catalogsystem.content.gui.CatalogManager.maxEntryNum;

public class GuiToggleButtonBase extends GuiElement {
    public final static int toggleButtonGapWidth = 50;
    public final static int toggleButtonGapHeight = 50;
    public final static int toggleButtonWidth = 30;
    public final static int toggleButtonHeight = 30;
    public final static int togglesBaseWidth = toggleButtonWidth + 2 * toggleButtonGapWidth;
    public final static int togglesBaseHeight = toggleButtonHeight + toggleButtonGapHeight;

    public GuiToggleButtonBase(int imageWidth, int imageHeight, int pageIndex) {
        super((imageWidth - togglesBaseWidth) / 2, (imageHeight - togglesBaseHeight) / 2, togglesBaseWidth, togglesBaseHeight);
        this.addChild(new GuiRect(0, 0, togglesBaseWidth, togglesBaseHeight, 0x0000ff));//located pos
        for (int i = 0; i < maxEntryNum; i++) {
            if (manager.hasEntry(pageIndex, i)){
                this.addChild(manager.getEntryToggleButton(pageIndex, i));
            }
        }
    }
}
