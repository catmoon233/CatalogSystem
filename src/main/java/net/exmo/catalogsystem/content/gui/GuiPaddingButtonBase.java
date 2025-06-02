package net.exmo.catalogsystem.content.gui;

import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiRect;

import static net.exmo.catalogsystem.Catalogsystem.manager;
import static net.exmo.catalogsystem.content.gui.CatalogManager.maxEntryNum;

public class GuiPaddingButtonBase extends GuiElement {
    public final static int paddingButtonGapWidth = 30;
    public final static int paddingButtonWidth = 20;
    public final static int paddingButtonHeight = 10;
    public final static int paddingsBaseWidth = paddingButtonWidth + 5 * paddingButtonGapWidth;
    public final static int paddingsBaseHeight = paddingButtonHeight;

    public GuiPaddingButtonBase(int imageWidth, int imageHeight, int pageIndex, String id) {
        super((imageWidth - paddingsBaseWidth) / 2, - paddingsBaseHeight, paddingsBaseWidth, paddingsBaseHeight);
        this.addChild(new GuiRect(0, 0, paddingsBaseWidth, paddingsBaseHeight, 0x0000ff));//located pos
        for (int i = 0; i < maxEntryNum; i++) {
            this.addChild(new GuiRect(i * paddingButtonGapWidth, 0, paddingButtonWidth, paddingButtonHeight, 0x770077));//located pos
            if (manager.hasEntry(pageIndex, i)) {
                this.addChild(new GuiRect(i * paddingButtonGapWidth, 0, paddingButtonWidth, paddingButtonHeight, 0xff00ff));//located pos
                if (!manager.getId(pageIndex, i).matches(id)){
                    this.addChild(manager.getEntryPaddingButton(pageIndex, i));
                }
            }
        }
    }
}
