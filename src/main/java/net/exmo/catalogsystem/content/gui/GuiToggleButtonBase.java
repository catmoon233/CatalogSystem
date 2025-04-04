package net.exmo.catalogsystem.content.gui;

import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiRect;

public class GuiToggleButtonBase extends GuiElement {
    private final static int maxButtonNum = 6;
    private final static int buttonGapWidth = 50;
    private final static int buttonGapHeight = 50;
    private final static int buttonWidth = 30;
    private final static int buttonHeight = 30;

    public GuiToggleButtonBase(int imageWidth, int imageHeight) {
        super((imageWidth - buttonWidth - 2 * buttonGapWidth) / 2,
                (imageHeight - buttonHeight - buttonGapHeight) / 2,
                buttonHeight + buttonGapHeight,
                buttonWidth + 2 * buttonGapWidth);
        int guiButtonBaseWidth = buttonWidth + 2 * buttonGapWidth;
        int guiButtonBaseHeight = buttonHeight + buttonGapHeight;
        this.addChild(new GuiRect(0, 0, guiButtonBaseWidth, guiButtonBaseHeight, 0x0000ff));
    }
}
