package net.exmo.catalogsystem.content.gui;

import net.exmo.catalogsystem.Catalogsystem;
import net.exmo.catalogsystem.content.Catalog;
import net.exmo.catalogsystem.content.CatalogHandle;
import net.exmo.catalogsystem.network.OpenCatalogEntryMenuButtonMessage;
import net.exmo.catalogsystem.network.OpenCatalogTotalMenuButtonMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import se.mickelus.mutil.gui.GuiButton;

import java.util.*;

import static net.exmo.catalogsystem.Catalogsystem.manager;

public class CatalogManager {
    private record screenNode(Runnable open, int depth) {
        public void run() {
            open.run();
        }
    }

    private final Stack<screenNode> screens = new Stack<>();
    public final static int maxEntryNum = 6;
    private final Map<ResourceLocation, Catalog> catalogs = CatalogHandle.getCatalogs();
    private Map<Integer, List<GuiEntryButton>> pages;
    private boolean initialized = false;

    public void init() {
        pages = new HashMap<>();
        int index = 0;
        int page = 0;
        List<GuiEntryButton> tempPage = new ArrayList<>();

        for (Map.Entry<ResourceLocation, Catalog> entry : catalogs.entrySet()) {
            // 新页面开始时初始化列表
            if (index == 0) {
                tempPage = new ArrayList<>();
            }

            tempPage.add(new GuiEntryButton(entry, index));
            index++;
//            Catalogsystem.LOGGER.info("[CATALOG] Successfully initialize {}", entry.getKey().toString());

            // 当前页面已满，准备下一页
            if (index >= maxEntryNum) {
                page++;
                index = 0;
                pages.put(page, tempPage);
            }
        }

        // 如果最后一个页面不满6个条目，仍然需要保存
        if (index > 0) {
            pages.put(page, tempPage);
        }

        initialized = true;
    }

    private void openCatalogTotal(String id, HashMap<String, String> textState, int x, int y, int z, Player player, int pageIndex) {
        textState.put("id", id);
        Catalogsystem.PACKET_HANDLER.sendToServer(new OpenCatalogTotalMenuButtonMessage(pageIndex, x, y, z, textState));
        Catalogsystem.LOGGER.info("3draw page: {}", pageIndex);
        OpenCatalogTotalMenuButtonMessage.handleButtonAction(player, x, y, z, pageIndex, textState);
    }

    public Runnable getGuideCallback(HashMap<String, String> textState, int x, int y, int z, Player player, int pageIndex) {
        return () -> openCatalogTotal(String.valueOf(pageIndex), textState, x, y, z, player, pageIndex);
    }

    //TODO: what is xyz used for?
    private void openCatalogEntry(String id, HashMap<String, String> textState, int x, int y, int z, Player player) {
        textState.put("id", id);
        Catalogsystem.PACKET_HANDLER.sendToServer(new OpenCatalogEntryMenuButtonMessage(0, x, y, z, textState));
        OpenCatalogEntryMenuButtonMessage.handleButtonAction(player, 0, x, y, z, textState);
    }

    public void registerCallbacks(HashMap<String, String> textState, int x, int y, int z, Player player) {
        if (!initialized) {
            Catalogsystem.LOGGER.error("[CATALOG] Fail to register callbacks, CatalogManager not initialized!");
            return;
        }

        for (List<GuiEntryButton> tempPage : pages.values()) {
            for (GuiEntryButton entry : tempPage) {
                entry.registerCallback(() -> manager.openScreen(() -> openCatalogEntry(entry.getEntry().getKey().toString(), textState, x, y, z, player), 2));
//                Catalogsystem.LOGGER.info("[CATALOG] Successfully register callback {}", entry.getEntry().getKey().toString());
            }
        }
    }

    public int getPageCount() {
        return pages.size();
    }

    public void openScreen(Runnable open, int depth) {
        screens.push(new screenNode(open, depth)).run();
        Catalogsystem.LOGGER.error("[CATALOG] push screen! length:{}", screens.size());
    }

    private void popScreen() {
        int depth = screens.pop().depth();
        while(screens.peek().depth() == depth) {
            Catalogsystem.LOGGER.error("[CATALOG] popping up screen! length:{}", screens.size());
            screens.pop();
        }
        screens.peek().run();
    }

    public Runnable getBackScreen() {
        return this::popScreen;
    }

    public boolean hasBackScreen() {
        return screens.size() > 1 && screens.peek().depth() > 1;
    }

    public void resetScreen() {
        screens.clear();
    }

    public boolean hasEntry(int pageIndex, int entryIndex) {
        return initialized && pages.containsKey(pageIndex) && pages.get(pageIndex).size() > entryIndex;
    }

    public GuiButton getEntryToggleButton(int pageIndex, int entryIndex) {
        return pages.get(pageIndex).get(entryIndex).getEntryToggleButton();
    }

    public GuiButton getEntryPaddingButton(int pageIndex, int entryIndex) {
        return pages.get(pageIndex).get(entryIndex).getEntryPaddingButton();
    }

    public String getId(int pageIndex, int entryIndex) {
        return pages.get(pageIndex).get(entryIndex).getEntry().getKey().toString();
    }
}
