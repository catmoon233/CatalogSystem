package net.exmo.catalogsystem.content.gui;

import net.exmo.catalogsystem.Catalogsystem;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.gui.GuiButton;
import se.mickelus.mutil.gui.GuiElement;
import se.mickelus.mutil.gui.GuiRect;
import se.mickelus.mutil.gui.GuiText;

import java.util.HashMap;

import static net.exmo.catalogsystem.Catalogsystem.manager;

public class GuiGuideButtonBase extends GuiElement {
    public final static int backButtonWidth = 20;
    public final static int backButtonHeight = 20;
    public final static int pageTextWidth = 10;
    public final static int pageTextHeight = 10;
    public final static int GuideButtonWidth = 20;
    public final static int GuideButtonHeight = 20;

    public GuiGuideButtonBase(int imageWidth, int imageHeight) {
        super(0, 0, imageWidth, imageHeight);
        this.addChild(new GuiRect(imageWidth, imageHeight - backButtonHeight, backButtonWidth, backButtonHeight, 0x0000ff));//located pos
        if (manager.hasBackScreen()) {
            this.addChild(new GuiButton(imageWidth, imageHeight - backButtonHeight, backButtonWidth, backButtonHeight, "back", manager.getBackScreen()));
        }
    }

    public GuiGuideButtonBase(int imageWidth, int imageHeight, int pageIndex, Player player, HashMap<String, String> textState, int x, int y, int z) {
        this(imageWidth, imageHeight);
        this.addChild(new GuiText(imageWidth - pageTextWidth, imageHeight - pageTextHeight, pageTextWidth, String.valueOf(pageIndex + 1)));
        if (pageIndex > 0) {
            this.addChild(new GuiButton(-GuideButtonWidth, imageHeight - backButtonHeight - GuideButtonHeight, GuideButtonWidth, GuideButtonHeight, "<-",
                    manager.getGuideCallback(textState, x, y, z, player, pageIndex - 1)));
        }
        if (pageIndex < manager.getPageCount()) {
            this.addChild(new GuiButton(imageWidth, imageHeight - backButtonHeight - GuideButtonHeight, GuideButtonWidth, GuideButtonHeight, "->",
                    manager.getGuideCallback(textState, x, y, z, player, pageIndex + 1)));
        }
    }
}
