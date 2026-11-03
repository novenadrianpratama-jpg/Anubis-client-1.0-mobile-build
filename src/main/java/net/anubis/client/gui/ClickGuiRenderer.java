/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  imgui.ImVec4
 *  imgui.internal.ImGui
 *  imgui.type.ImBoolean
 *  imgui.type.ImString
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.lwjgl.glfw.GLFW
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.anubis.client.config.BundledDefaults;
import net.anubis.client.config.ConfigProfiles;
import net.anubis.client.config.FriendStore;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.ModuleCodes;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.config.ProfileException;
import net.anubis.client.config.Surface;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.BlockListEditor;
import net.anubis.client.gui.FileDialogs;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.HudEditorScreen;
import net.anubis.client.gui.HudLayoutStore;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.gui.ItemPickerEditor;
import net.anubis.client.gui.Motion;
import net.anubis.client.gui.PanelStateStore;
import net.anubis.client.gui.ProfileList;
import net.anubis.client.gui.SurfaceBackground;
import net.anubis.client.gui.ThemeImages;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.client.HotkeysModule;
import net.anubis.client.module.impl.client.HudModule;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.module.impl.client.RadarModule;
import net.anubis.client.module.impl.client.RegionMapModule;
import net.anubis.client.module.setting.ActionSetting;
import net.anubis.client.module.setting.BlockColorListSetting;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ItemColorListSetting;
import net.anubis.client.module.setting.KeySetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.anubis.client.module.setting.StringListSetting;
import net.anubis.client.module.setting.StringSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public final class ClickGuiRenderer {
    private static final float PANEL_W = 212.0f;
    private static final float PANEL_GAP = 14.0f;
    private static final float PANEL_TOP = 28.0f;
    private static final float HEADER_H = 42.0f;
    private static final float ROW_H = 30.0f;
    private static final float ROW_GAP = 2.0f;
    private static final float ROW_INSET = 6.0f;
    private static final int HEADER_BAND = 0xEFFFFFF;
    private static final int ROW_RULE = 0x24FFFFFF;
    private static final float CARD_W = 460.0f;
    private static final float CARD_MAX_H = 760.0f;
    private static final float CARD_PAD = 24.0f;
    private static final float SETTING_ROW_H = 46.0f;
    private static final float TAB_BAR_H = 44.0f;
    private static final float TAB_BAR_BOTTOM = 26.0f;
    private static final float INSPECTOR_W = 360.0f;
    private static final float SMALL_TOG_W = 16.0f;
    private static final float SMALL_TOG_H = 16.0f;
    public static final int ERROR_TEXT = -10856974;
    private static final int[] AVATAR_COLORS = new int[]{-624588, -10893516, -16080897, -2207057, -10536961, -3690448, -2730408, -16724737};
    private static final int PANEL_FLAGS = 123;
    private static final int CARD_FLAGS = 8511;
    public static final int CHILD_FLAGS = 24;
    private static final String PANEL_ID_PREFIX = "##panel_";
    private static final String SEARCH_PANEL_ID = "##panel_search";
    private static final int STYLE_VAR_COUNT = 11;
    private static final int DYNAMIC_COLOR_COUNT = 10;
    private static final int[] THEME_COLORS = new int[]{0, -461582, 1, -4542808, 3, 0, 4, -14015199, 5, 0x14FFFFFF, 6, 0, 7, -13226454, 8, -12240588, 9, -12240588, 21, -13226454, 22, -12240588, 24, -13226454, 25, -12240588, 27, 0xFFFFFFF, 14, 0, 15, 0x30FFFFFF, 16, 0x50FFFFFF, 17, 0x70FFFFFF, 30, 0, 31, 0, 32, 0, 53, 0};
    private Tab tab = Tab.MODULES;
    private final long guiOpenedAt;
    private long viewChangedAt = this.guiOpenedAt = System.nanoTime();
    private float tabIndicatorX = -1.0f;
    private float tabIndicatorW;
    private ClientModule settingsModule;
    private boolean cardJustOpened;
    private boolean dividerPending;
    private boolean panelGuard;
    private boolean tabBarGuard;
    private boolean cardGuard;
    private boolean cardTextLift;
    private final HashMap<String, float[]> scroll = new HashMap();
    private static float scrollDragFrom;
    private static final ImBoolean SCROLLBAR_HOVERED;
    private static final ImBoolean SCROLLBAR_HELD;
    private ClientModule listeningModule = null;
    private boolean listeningFirstFrame = false;
    private KeySetting listeningKeySetting = null;
    private boolean listeningKeyFirstFrame = false;
    private final ImBoolean colorPickerOpen = new ImBoolean(false);
    private ClientModule activeColorModule = null;
    private ColorSetting activeColorSetting = null;
    private final ImString globalSearch = new ImString(128);
    private boolean focusSearch;
    private float searchBodyH;
    private final Map<String, float[]> panelSpots = new HashMap<String, float[]>();
    private final Map<String, float[]> panelHomes = new HashMap<String, float[]>();
    private final Map<String, float[]> panelTargets = new HashMap<String, float[]>();
    private String panelLayoutKey = "";
    private boolean panelHomesDirty;
    private CategoryPanel[] panelCache = new CategoryPanel[0];
    private int panelCacheRevision = -1;
    private final Map<String, float[]> panelFold = new HashMap<String, float[]>();
    private final Map<String, Motion.Rows> rowMotion = new HashMap<String, Motion.Rows>();
    private final Int2ObjectOpenHashMap<String> keyLabels = new Int2ObjectOpenHashMap();
    private int cachedRegistryRevision = -1;
    private String cachedGlobalSearch = "";
    private List<ClientModule> cachedGlobalResults = List.of();
    private final ImString friendInput = new ImString(32);
    private boolean focusFriendInput;
    private String friendMessage;
    private long friendMessageAt;
    private final ImString profileInput = new ImString(64);
    private boolean focusProfileInput;
    private String configMessage;
    private boolean configMessageError;
    private long configMessageAt;
    private String moduleCodeFor;
    private String moduleCodeMessage;
    private boolean moduleCodeError;
    private long moduleCodeAt;
    private final ProfileList profileList = new ProfileList(this::setConfigMessage);
    private String pendingConfirm;
    private long pendingConfirmAt;
    private String themeMessage;
    private boolean themeMessageError;
    private long themeMessageAt;
    private Surface themeSurface;
    private boolean inspectMode;
    private final List<float[]> panelRects = new ArrayList<float[]>();
    private float[] cardRect;
    private float[] tabBarRect;
    private float[] inspectorRect;
    private Surface hoveredSurface;
    private boolean rowDividers;
    private float rowAboveEmphasis;
    private boolean focusInspector;
    private long previewChangedAt;
    private String hudElementId;
    private boolean focusHudInspector;
    private String hudMessage;
    private boolean hudMessageError;
    private long hudMessageAt;
    private final BlockListEditor blockLists = new BlockListEditor();
    private final ItemPickerEditor itemPickers = new ItemPickerEditor();
    private ItemColorListSetting activeItemColorPalette = null;
    private boolean itemColorWindowOpen = false;
    private final ImString itemSearchBuf = new ImString(256);
    private ItemColorListSetting.ItemEntry activeItemColorEntry = null;
    private boolean editingItemDefaultColor = false;
    private final HashMap<ItemColorListSetting.ItemEntry, Float> itemEntryTogAnim = new HashMap();
    private final ImBoolean itemColorPickerOpen = new ImBoolean(false);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render() {
        float dt = ClickGuiRenderer.animationDelta(Math.max(ImGui.getIO().getDeltaTime(), 0.001f));
        ThemeStore theme = ThemeStore.get();
        this.panelGuard = HudChrome.needsTextGuard(theme.panels);
        this.tabBarGuard = HudChrome.needsTextGuard(theme.tabBar);
        this.cardGuard = HudChrome.needsTextGuard(theme.cards);
        this.cardTextLift = theme.cards.isImage() && !theme.cards.isBanner();
        ClickGuiRenderer.applyTheme();
        boolean moduleFontPushed = ClickGuiRenderer.pushFont(ImGuiManager.getInstance().moduleFont());
        try {
            float sw = ImGui.getIO().getDisplaySizeX();
            float sh = ImGui.getIO().getDisplaySizeY();
            float open = ClickGuiRenderer.easeOut(ClickGuiRenderer.elapsed(this.guiOpenedAt, 240.0f));
            float view = ClickGuiRenderer.easeOut(ClickGuiRenderer.elapsed(this.viewChangedAt, 260.0f));
            float alpha = Math.max(0.01f, open * view);
            ImGui.getBackgroundDrawList().addRectFilled(0.0f, 0.0f, sw, sh, AnubisWidgets.withAlpha(-16777216, theme.backgroundDim.get() * open));
            Path picked = ThemeImages.get().takePendingImport();
            if (picked != null) {
                this.importHeaderImage(picked);
            }
            this.handleShortcuts();
            switch (this.tab.ordinal()) {
                case 0: {
                    if (this.settingsModule == null) {
                        this.renderModulePanels(dt, alpha);
                        break;
                    }
                    this.renderModuleCard(this.settingsModule, dt, alpha, view);
                    break;
                }
                case 1: {
                    this.renderHudStudio(dt, alpha, view);
                    break;
                }
                case 2: {
                    this.renderFriendsCard(dt, alpha, view);
                    break;
                }
                case 3: {
                    this.renderThemeStudio(dt, alpha, view);
                    break;
                }
                case 4: {
                    this.renderConfigCard(dt, alpha, view);
                }
            }
            this.renderTabBar(dt, open);
            this.savePanelHomes();
            this.renderColorPickerWindow();
            this.blockLists.renderWindows();
            this.itemPickers.renderWindows();
            this.renderItemColorPaletteWindow();
            this.closeCardOnOutsideClick();
        }
        finally {
            if (moduleFontPushed) {
                ImGui.popFont();
            }
            ClickGuiRenderer.removeTheme();
        }
    }

    private void handleShortcuts() {
        if (this.tab == Tab.MODULES && this.settingsModule == null && ImGui.getIO().getKeyCtrl() && ImGui.isKeyPressed((int)551)) {
            this.focusSearch = true;
        }
    }

    private void switchTab(Tab next) {
        if (next == this.tab) {
            return;
        }
        this.closeSettings();
        this.tab = next;
        this.viewChangedAt = System.nanoTime();
        this.pendingConfirm = null;
        this.profileList.disarm();
        if (next == Tab.CONFIG) {
            ConfigProfiles.getInstance().refresh();
        }
    }

    private void renderModulePanels(float dt, float alpha) {
        CategoryPanel[] panels = this.visiblePanels();
        boolean placed = this.panelHomes.containsKey(SEARCH_PANEL_ID);
        for (CategoryPanel panel : panels) {
            placed &= this.panelHomes.containsKey(panel.id());
        }
        if (!placed) {
            this.placeNewPanels(panels);
        }
        this.fitPanels(panels);
        for (CategoryPanel panel : panels) {
            this.renderCategoryPanel(panel, dt, alpha);
        }
        this.renderSearchPanel(dt, alpha);
    }

    private CategoryPanel[] visiblePanels() {
        int revision = ModuleRegistry.revision();
        if (revision != this.panelCacheRevision) {
            ArrayList<CategoryPanel> panels = new ArrayList<CategoryPanel>();
            for (ModuleCategory category : ModuleCategory.values()) {
                if (this.modulesFor(category).isEmpty()) continue;
                panels.add(new CategoryPanel(category, PANEL_ID_PREFIX + category.name(), "##rows_" + category.name()));
            }
            this.panelCache = (CategoryPanel[])panels.toArray(CategoryPanel[]::new);
            this.panelCacheRevision = revision;
        }
        return this.panelCache;
    }

    private void renderCategoryPanel(CategoryPanel panel, float dt, float alpha) {
        List<ClientModule> modules = this.modulesFor(panel.category());
        float listH = Math.min(ClickGuiRenderer.rowListHeight(modules.size()), ClickGuiRenderer.maxListHeight());
        float open = this.unfolded(panel, dt);
        if (open < 1.0f) {
            ImGui.setNextWindowSize((float)AnubisWidgets.s(212.0f), (float)(AnubisWidgets.s(42.0f) + listH * open), (int)1);
        }
        if (this.beginPanel(panel.id(), alpha)) {
            String title = panel.category().displayName();
            this.drawPanelChrome(title, panel.category());
            this.recordPanelRect();
            this.renderFoldControl(panel.category(), open);
            if (open > 0.0f) {
                this.renderRowList(panel.rowsId(), modules, listH, false, dt);
            }
        }
        ClickGuiRenderer.endPanel();
    }

    private float unfolded(CategoryPanel panel, float dt) {
        float target = PanelStateStore.getInstance().isCollapsed(panel.category()) ? 0.0f : 1.0f;
        float[] open = this.panelFold.get(panel.id());
        if (open == null) {
            open = new float[]{target};
            this.panelFold.put(panel.id(), open);
        }
        open[0] = ClickGuiRenderer.smooth(open[0], target, dt, 14.0f);
        if (Math.abs(open[0] - target) < 0.002f) {
            open[0] = target;
        }
        return open[0];
    }

    private void renderFoldControl(ModuleCategory category, float open) {
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY();
        float ww = ImGui.getWindowWidth();
        float headerH = AnubisWidgets.s(42.0f);
        float hit = AnubisWidgets.s(22.0f);
        float cx = wx + ww - AnubisWidgets.s(20.0f);
        float cy = wy + headerH * 0.5f;
        boolean overHeader = !this.inspectMode && ImGui.isWindowHovered() && ImGui.isMouseHoveringRect((float)wx, (float)wy, (float)(wx + ww), (float)(wy + headerH));
        ImGui.setCursorScreenPos((float)(cx - hit * 0.5f), (float)(cy - hit * 0.5f));
        boolean clicked = ImGui.invisibleButton((String)"##fold", (float)hit, (float)hit) && !this.inspectMode;
        float hover = AnubisWidgets.animate(ImGui.getItemID(), overHeader, 18.0f);
        ImGui.setCursorPos((float)0.0f, (float)headerH);
        if (clicked || overHeader && ImGui.isMouseClicked((int)1)) {
            PanelStateStore store;
            store.setCollapsed(category, !(store = PanelStateStore.getInstance()).isCollapsed(category));
        }
        AnubisWidgets.chevronRotated(ImGui.getWindowDrawList(), cx, cy, AnubisWidgets.s(4.0f), AnubisTheme.lerpColor(-4542808, -461582, hover), open * 1.5707964f);
    }

    private void renderSearchPanel(float dt, float alpha) {
        if (this.beginPanel(SEARCH_PANEL_ID, alpha)) {
            List<ClientModule> results;
            this.drawPanelChrome("Search", null);
            this.recordPanelRect();
            float inset = AnubisWidgets.s(6.0f) + AnubisWidgets.s(2.0f);
            ImGui.setCursorPos((float)inset, (float)(AnubisWidgets.s(42.0f) - AnubisWidgets.s(4.0f)));
            ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(10.0f), (float)AnubisWidgets.s(7.0f));
            ImGui.setNextItemWidth((float)(AnubisWidgets.s(212.0f) - inset * 2.0f));
            if (this.focusSearch) {
                ImGui.setKeyboardFocusHere();
                this.focusSearch = false;
            }
            ImGui.inputTextWithHint((String)"##globalsearch", (String)"Search modules...", (ImString)this.globalSearch);
            ImGui.popStyleVar();
            ImGui.dummy((float)0.0f, (float)AnubisWidgets.s(8.0f));
            String query = ClickGuiRenderer.normalizeSearch(this.globalSearch.get());
            List<ClientModule> list = results = query.isEmpty() ? List.of() : this.searchModules(query);
            float target = query.isEmpty() ? 0.0f : (results.isEmpty() ? AnubisWidgets.s(34.0f) : Math.min(ClickGuiRenderer.rowListHeight(results.size()), ClickGuiRenderer.maxListHeight()));
            this.searchBodyH = ClickGuiRenderer.smooth(this.searchBodyH, target, dt, 14.0f);
            if (Math.abs(this.searchBodyH - target) < 0.5f) {
                this.searchBodyH = target;
            }
            if (this.searchBodyH >= 1.0f) {
                if (results.isEmpty()) {
                    ImGui.beginChild((String)"##search_empty", (float)AnubisWidgets.s(212.0f), (float)this.searchBodyH, (boolean)false, (int)24);
                    String empty = "No results";
                    ImFont font = ImGui.getFont();
                    AnubisWidgets.text(ImGui.getWindowDrawList(), font, ImGui.getWindowPosX() + (AnubisWidgets.s(212.0f) - AnubisWidgets.textWidth(font, empty)) * 0.5f, ImGui.getWindowPosY() + AnubisWidgets.s(4.0f), -4542808, empty, this.panelGuard);
                    ImGui.endChild();
                } else {
                    this.renderRowList("##search_rows", results, this.searchBodyH, true, dt);
                }
            }
        }
        ClickGuiRenderer.endPanel();
    }

    private boolean beginPanel(String id, float alpha) {
        boolean moved;
        float[] target = this.panelTargets.remove(id);
        if (target != null) {
            ImGui.setNextWindowPos((float)target[0], (float)target[1], (int)1);
        }
        ImGui.setNextWindowSizeConstraints((float)AnubisWidgets.s(212.0f), (float)0.0f, (float)AnubisWidgets.s(212.0f), (float)Float.MAX_VALUE);
        ImGui.pushStyleVar((int)0, (float)alpha);
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)ClickGuiRenderer.panelRounding());
        boolean open = ImGui.begin((String)id, (int)123);
        float x = ImGui.getWindowPosX();
        float y = ImGui.getWindowPosY();
        float[] spot = this.panelSpots.computeIfAbsent(id, key -> new float[]{x, y});
        boolean bl = moved = Math.abs(x - spot[0]) > 0.5f || Math.abs(y - spot[1]) > 0.5f;
        if (target == null && moved && ImGui.isMouseDown((int)0)) {
            this.panelHomes.put(id, new float[]{x / AnubisWidgets.s(1.0f), y / AnubisWidgets.s(1.0f)});
            this.panelHomesDirty = true;
        }
        spot[0] = x;
        spot[1] = y;
        return open;
    }

    private void savePanelHomes() {
        if (!this.panelHomesDirty || ImGui.isMouseDown((int)0)) {
            return;
        }
        this.panelHomesDirty = false;
        PanelStateStore.getInstance().setPanelHomes(this.panelHomes);
    }

    private static void endPanel() {
        ImGui.end();
        ImGui.popStyleVar((int)4);
    }

    private void drawPanelChrome(String title, ModuleCategory category) {
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY();
        float ww = ImGui.getWindowWidth();
        float wh = ImGui.getWindowHeight();
        ClickGuiRenderer.drawShadow(wx, wy, ww, wh, ClickGuiRenderer.panelRounding(), 4);
        ImDrawList dl = ImGui.getWindowDrawList();
        SurfaceBackground.draw(dl, ThemeStore.get().panels, wx, wy, ww, wh, ClickGuiRenderer.panelRounding());
        ImFont font = ImGuiManager.getInstance().categoryFont();
        float headerH = AnubisWidgets.s(42.0f);
        ClickGuiRenderer.drawHeaderBand(dl, wx, wy, ww, headerH);
        float r = AnubisWidgets.s(7.0f);
        float cx = wx + AnubisWidgets.s(14.0f) + r;
        float cy = wy + headerH * 0.5f;
        if (category == null) {
            AnubisWidgets.magnifier(dl, cx, cy, r, -4542808);
        } else {
            ClickGuiRenderer.drawCategoryIcon(dl, category, cx, cy, r, -4542808);
        }
        float tx = cx + r + AnubisWidgets.s(9.0f);
        float ty = wy + (headerH - AnubisWidgets.lineHeight(font)) * 0.5f;
        AnubisWidgets.text(dl, font, tx, ty, -461582, title, this.panelGuard);
        ImGui.dummy((float)ww, (float)headerH);
    }

    private static void drawHeaderBand(ImDrawList dl, float x, float y, float w, float h) {
        float rounding = ClickGuiRenderer.panelRounding();
        float cap = Math.min(h, rounding + AnubisWidgets.s(2.0f));
        int top = AnubisWidgets.c(AnubisWidgets.withAlpha(0xEFFFFFF, 1.0f - cap / (2.0f * h)));
        int below = AnubisWidgets.c(AnubisWidgets.withAlpha(0xEFFFFFF, 1.0f - cap / h));
        int clear = AnubisWidgets.c(0xFFFFFF);
        dl.addRectFilled(x, y, x + w, y + cap, top, rounding, 48);
        dl.addRectFilledMultiColor(x, y + cap, x + w, y + h, below, below, clear, clear);
    }

    private static void softRule(ImDrawList dl, float x0, float x1, float y, int peak) {
        float mid = (x0 + x1) * 0.5f;
        int solid = AnubisWidgets.c(peak);
        int clear = AnubisWidgets.c(peak & 0xFFFFFF);
        dl.addRectFilledMultiColor(x0, y, mid, y + 1.0f, clear, solid, solid, clear);
        dl.addRectFilledMultiColor(mid, y, x1, y + 1.0f, solid, clear, clear, solid);
    }

    private static void drawCategoryIcon(ImDrawList dl, ModuleCategory category, float cx, float cy, float r, int color) {
        switch (category.icon()) {
            case CROSSHAIR: {
                AnubisWidgets.crosshair(dl, cx, cy, r, color);
                break;
            }
            case CHEVRON: {
                AnubisWidgets.chevron(dl, cx, cy, r, color, true);
                break;
            }
            case MOVE: {
                AnubisWidgets.move(dl, cx, cy, r, color);
                break;
            }
            case GRID: {
                AnubisWidgets.grid(dl, cx, cy, r, color);
                break;
            }
            case RING: {
                AnubisWidgets.ring(dl, cx, cy, r, color);
                break;
            }
            case EYE: {
                AnubisWidgets.eye(dl, cx, cy, r, color);
                break;
            }
            case MONITOR: {
                AnubisWidgets.monitor(dl, cx, cy, r, color);
                break;
            }
            case HOUSE: {
                AnubisWidgets.house(dl, cx, cy, r, color);
                break;
            }
            case PAPYRUS: {
                AnubisWidgets.papyrus(dl, cx, cy, r, color);
                break;
            }
            case ANKH: {
                AnubisWidgets.ankh(dl, cx, cy, r, color);
            }
        }
    }

    public static void drawShadow(float x, float y, float w, float h, float rounding, int layers) {
        ImDrawList bg = ImGui.getBackgroundDrawList();
        for (int i = 1; i <= layers; ++i) {
            float e = AnubisWidgets.s(2.5f) * (float)i;
            bg.addRectFilled(x - e, y - e + AnubisWidgets.s(4.0f), x + w + e, y + h + e + AnubisWidgets.s(4.0f), AnubisWidgets.c(0x14000000), rounding + e);
        }
    }

    private static float rowListHeight(int rows) {
        return (float)rows * AnubisWidgets.s(32.0f) - AnubisWidgets.s(2.0f) + AnubisWidgets.s(6.0f);
    }

    private static float maxListHeight() {
        return Math.max(AnubisWidgets.s(120.0f), ImGui.getIO().getDisplaySizeY() * 0.62f - AnubisWidgets.s(42.0f));
    }

    private void renderRowList(String id, List<ClientModule> modules, float height, boolean showCategory, float dt) {
        if (ImGui.beginChild((String)id, (float)AnubisWidgets.s(212.0f), (float)Math.max(1.0f, height), (boolean)false, (int)24)) {
            Motion.Rows motion = this.rowMotion.computeIfAbsent(id, key -> new Motion.Rows());
            motion.bind(modules);
            this.rowDividers = ThemeStore.get().moduleDividers.get();
            for (int i = 0; i < modules.size(); ++i) {
                this.renderModuleRow(modules.get(i), showCategory, motion, i, dt);
            }
            ImGui.dummy((float)1.0f, (float)(AnubisWidgets.s(6.0f) - AnubisWidgets.s(2.0f)));
            this.applySmoothScroll(id, dt);
        }
        ImGui.endChild();
    }

    private void renderModuleRow(ClientModule module, boolean showCategory, Motion.Rows motion, int row, float dt) {
        float fade;
        ImGui.pushID((String)module.id());
        float inset = AnubisWidgets.s(6.0f);
        float rowH = AnubisWidgets.s(30.0f);
        float localY = ImGui.getCursorPosY();
        ImGui.setCursorPosX((float)inset);
        float x0 = ImGui.getCursorScreenPosX();
        float y0 = ImGui.getCursorScreenPosY();
        float w = ImGui.getWindowWidth() - inset * 2.0f;
        if (ImGui.invisibleButton((String)"##row", (float)w, (float)rowH) && !this.inspectMode) {
            if (module.isAction()) {
                module.onClick();
            } else {
                module.toggle();
            }
        }
        boolean hovered = ImGui.isItemHovered();
        if (!this.inspectMode && ImGui.isItemClicked((int)1)) {
            this.openSettings(module);
        }
        if (!this.inspectMode && ImGui.isItemHovered((int)65536)) {
            String description = module.description();
            ClickGuiRenderer.showTooltip((String)(description == null || description.isBlank() ? "Right-click for settings" : description + "\nRight-click for settings"));
        }
        float hover = motion.hover(row, hovered, dt, 18.0f);
        float on = motion.on(row, module.isEnabled(), dt, 14.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        float emphasis = Math.max(hover, on);
        if (this.rowDividers && row > 0 && (fade = 1.0f - Math.max(emphasis, this.rowAboveEmphasis)) > 0.01f) {
            float y = (float)Math.floor(y0 - AnubisWidgets.s(2.0f) * 0.5f);
            ClickGuiRenderer.softRule(dl, x0 + AnubisWidgets.s(8.0f), x0 + w - AnubisWidgets.s(8.0f), y, AnubisWidgets.withAlpha(0x24FFFFFF, fade));
        }
        this.rowAboveEmphasis = emphasis;
        float r = AnubisWidgets.s(6.0f);
        if (hover > 0.01f) {
            dl.addRectFilled(x0, y0, x0 + w, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(0x12FFFFFF, hover)), r);
        }
        if (on > 0.01f) {
            dl.addRectFilled(x0, y0, x0 + w, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(AnubisTheme.lerpColor(GuiPalette.accent(), GuiPalette.accentHover(), hover * 0.3f), on)), r);
        }
        ImFont font = ImGui.getFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        String trailing = showCategory ? module.category().displayName() : (module.keybind() == -1 ? null : this.keyLabel(module.keybind()));
        float tw = trailing == null ? 0.0f : AnubisWidgets.textWidth(small, trailing);
        int textColor = AnubisTheme.lerpColor(AnubisTheme.lerpColor(-2042416, -461582, hover), -1, on);
        float room = w - AnubisWidgets.s(trailing != null || module.isAction() ? 28.0f : 20.0f) - tw;
        String name = AnubisWidgets.ellipsize(font, module.name(), room);
        AnubisWidgets.text(dl, font, x0 + AnubisWidgets.s(10.0f), y0 + (rowH - AnubisWidgets.lineHeight(font)) * 0.5f, textColor, name, this.panelGuard);
        if (trailing != null) {
            int trailColor = AnubisTheme.lerpColor(-4542808, -419430401, on);
            AnubisWidgets.text(dl, small, x0 + w - AnubisWidgets.s(10.0f) - tw, y0 + (rowH - AnubisWidgets.lineHeight(small)) * 0.5f, trailColor, trailing, this.panelGuard);
        } else if (module.isAction()) {
            AnubisWidgets.chevronRight(dl, x0 + w - AnubisWidgets.s(14.0f), y0 + rowH * 0.5f, AnubisWidgets.s(4.0f), AnubisTheme.lerpColor(-4542808, -461582, hover));
        }
        ImGui.setCursorPos((float)0.0f, (float)(localY + rowH + AnubisWidgets.s(2.0f)));
        ImGui.popID();
    }

    public static void showTooltip(String text) {
        ImGui.pushStyleVar((int)2, (float)AnubisWidgets.s(10.0f), (float)AnubisWidgets.s(8.0f));
        ImGui.beginTooltip();
        ImGui.pushTextWrapPos((float)AnubisWidgets.s(280.0f));
        ImGui.textUnformatted((String)text);
        ImGui.popTextWrapPos();
        ImGui.endTooltip();
        ImGui.popStyleVar();
    }

    private void applySmoothScroll(String key, float dt) {
        ClickGuiRenderer.smoothScroll(this.scroll.computeIfAbsent(key, k -> new float[2]), dt);
    }

    public static void smoothScroll(float[] state, float dt) {
        float wheel;
        float max = ImGui.getScrollMaxY();
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY();
        float ww = ImGui.getWindowWidth();
        float wh = ImGui.getWindowHeight();
        float barH = Math.max(AnubisWidgets.s(24.0f), wh * wh / (wh + max));
        float travel = Math.max(1.0f, wh - barH);
        boolean held = false;
        boolean hot = false;
        if (max > 0.5f) {
            int id = ImGui.getID((String)"##scrollbar");
            imgui.internal.ImGui.keepAliveID((int)id);
            imgui.internal.ImGui.buttonBehavior((float)(wx + ww - AnubisWidgets.s(8.0f)), (float)wy, (float)(wx + ww), (float)(wy + wh), (int)id, (ImBoolean)SCROLLBAR_HOVERED, (ImBoolean)SCROLLBAR_HELD);
            held = SCROLLBAR_HELD.get();
            boolean bl = hot = held || SCROLLBAR_HOVERED.get();
            if (held && ImGui.isMouseClicked((int)0)) {
                float barY = wy + travel * (state[0] / max);
                float mouseY = ImGui.getMousePosY();
                state[1] = mouseY >= barY && mouseY <= barY + barH ? state[0] : (mouseY - wy - barH * 0.5f) / travel * max;
                scrollDragFrom = state[1];
            } else if (held) {
                state[1] = scrollDragFrom + ImGui.getMouseDragDeltaY((int)0, (float)0.0f) * max / travel;
            }
        }
        if (!held && ImGui.isWindowHovered() && (wheel = ImGui.getIO().getMouseWheel()) != 0.0f) {
            state[1] = state[1] - wheel * AnubisWidgets.s(56.0f);
        }
        state[1] = Math.max(0.0f, Math.min(max, state[1]));
        float f = state[0] = held ? state[1] : ClickGuiRenderer.smooth(state[0], state[1], dt, 16.0f);
        if (Math.abs(state[0] - state[1]) < 0.3f) {
            state[0] = state[1];
        }
        ImGui.setScrollY((float)state[0]);
        if (max > 0.5f) {
            float barY = wy + travel * (state[0] / max);
            ImGui.getWindowDrawList().addRectFilled(wx + ww - (hot ? AnubisWidgets.s(6.0f) : AnubisWidgets.s(4.0f)), barY, wx + ww - AnubisWidgets.s(1.5f), barY + barH, AnubisWidgets.c(hot ? 0x66FFFFFF : 0x33FFFFFF), AnubisWidgets.s(2.0f));
        }
    }

    private PanelLayout panelLayout(CategoryPanel[] panels) {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<Float> heights = new ArrayList<Float>();
        PanelStateStore folds = PanelStateStore.getInstance();
        for (CategoryPanel panel : panels) {
            ids.add(panel.id());
            keys.add(panel.category().name());
            heights.add(Float.valueOf(folds.isCollapsed(panel.category()) ? AnubisWidgets.s(42.0f) : AnubisWidgets.s(42.0f) + Math.min(ClickGuiRenderer.rowListHeight(this.modulesFor(panel.category()).size()), ClickGuiRenderer.maxListHeight())));
        }
        ids.add(SEARCH_PANEL_ID);
        keys.add("search");
        heights.add(Float.valueOf(AnubisWidgets.s(42.0f) + (float)ImGui.getFontSize() + AnubisWidgets.s(18.0f) + ClickGuiRenderer.maxListHeight()));
        return new PanelLayout(ids, keys, heights);
    }

    private void placeNewPanels(CategoryPanel[] panels) {
        PanelLayout layout = this.panelLayout(panels);
        List<String> ids = layout.ids();
        List<String> keys = layout.keys();
        List<Float> heights = layout.heights();
        float scale = AnubisWidgets.s(1.0f);
        Map<String, float[]> stored = PanelStateStore.getInstance().panelHomes();
        Map<String, Object> legacy = stored.isEmpty() ? ClickGuiRenderer.savedPanelSpots() : Map.of();
        ArrayList<float[]> taken = new ArrayList<float[]>();
        for (int i = 0; i < ids.size(); ++i) {
            float[] home;
            String id = ids.get(i);
            if (!this.panelHomes.containsKey(id)) {
                home = stored.get(id);
                float[] old = (float[])legacy.get(id);
                if (home == null && old != null && ClickGuiRenderer.coveredArea(old[0], old[1], heights.get(i).floatValue(), taken) == 0.0f) {
                    home = new float[]{old[0] / scale, old[1] / scale};
                    this.panelHomesDirty = true;
                }
                if (home != null) {
                    this.panelHomes.put(id, home);
                }
            }
            if ((home = this.panelHomes.get(id)) == null) continue;
            taken.add(new float[]{home[0] * scale, home[1] * scale, AnubisWidgets.s(212.0f), heights.get(i).floatValue()});
        }
        boolean firstRun = taken.isEmpty();
        for (int i = 0; i < ids.size(); ++i) {
            if (this.panelHomes.containsKey(ids.get(i))) continue;
            float h = heights.get(i).floatValue();
            float[] spot = ClickGuiRenderer.bundledSpot(keys.get(i));
            if (spot == null && firstRun) {
                spot = ClickGuiRenderer.panelPosFor(i, ids.size());
            }
            if (spot == null || ClickGuiRenderer.coveredArea(spot[0], spot[1], h, taken) > 0.0f) {
                spot = ClickGuiRenderer.freeSpot(h, taken);
            }
            this.panelHomes.put(ids.get(i), new float[]{spot[0] / scale, spot[1] / scale});
            taken.add(new float[]{spot[0], spot[1], AnubisWidgets.s(212.0f), h});
            this.panelHomesDirty = true;
        }
        this.panelLayoutKey = "";
    }

    private void fitPanels(CategoryPanel[] panels) {
        float scale;
        float sh;
        float sw = ImGui.getIO().getDisplaySizeX();
        String key = sw + "x" + (sh = ImGui.getIO().getDisplaySizeY()) + "@" + (scale = AnubisWidgets.s(1.0f));
        if (key.equals(this.panelLayoutKey)) {
            return;
        }
        this.panelLayoutKey = key;
        PanelLayout layout = this.panelLayout(panels);
        float w = AnubisWidgets.s(212.0f);
        float lowest = sh - AnubisWidgets.s(70.0f) - AnubisWidgets.s(42.0f);
        ArrayList<float[]> taken = new ArrayList<float[]>();
        ArrayList<Integer> misfits = new ArrayList<Integer>();
        for (int i = 0; i < layout.ids().size(); ++i) {
            String id = layout.ids().get(i);
            float[] home = this.panelHomes.get(id);
            float x = home[0] * scale;
            float y = home[1] * scale;
            if (x >= 0.0f && y >= 0.0f && x + w <= sw && y <= lowest) {
                this.panelTargets.put(id, new float[]{x, y});
                taken.add(new float[]{x, y, w, layout.heights().get(i).floatValue()});
                continue;
            }
            misfits.add(i);
        }
        Iterator iterator = misfits.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            float h = layout.heights().get(i).floatValue();
            float[] spot = ClickGuiRenderer.freeSpot(h, taken);
            this.panelTargets.put(layout.ids().get(i), spot);
            taken.add(new float[]{spot[0], spot[1], w, h});
        }
    }

    private static float[] bundledSpot(String key) {
        float sh;
        float sw = ImGui.getIO().getDisplaySizeX();
        float[] spot = BundledDefaults.panelSpot(key, sw, sh = ImGui.getIO().getDisplaySizeY());
        if (spot == null) {
            return null;
        }
        spot[0] = Math.max(0.0f, Math.min(spot[0], sw - AnubisWidgets.s(212.0f)));
        spot[1] = Math.max(0.0f, Math.min(spot[1], sh - AnubisWidgets.s(42.0f)));
        return spot;
    }

    private static float[] panelPosFor(int index, int panelCount) {
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float w = AnubisWidgets.s(212.0f);
        float gap = AnubisWidgets.s(14.0f);
        int columns = Math.max(1, (int)((sw - AnubisWidgets.s(40.0f) + gap) / (w + gap)));
        int inRow = Math.min(columns, panelCount);
        float rowW = (float)inRow * (w + gap) - gap;
        float startX = Math.max(AnubisWidgets.s(20.0f), (sw - rowW) * 0.5f);
        return new float[]{startX + (float)(index % columns) * (w + gap), AnubisWidgets.s(28.0f) + (float)(index / columns) * sh * 0.45f};
    }

    private static float[] freeSpot(float h, List<float[]> taken) {
        float[] fArray;
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float w = AnubisWidgets.s(212.0f);
        float gap = AnubisWidgets.s(14.0f);
        float margin = AnubisWidgets.s(20.0f);
        float bottom = sh - AnubisWidgets.s(70.0f) - gap;
        float highest = Float.MAX_VALUE;
        for (float[] fArray2 : taken) {
            highest = Math.min(highest, fArray2[1]);
        }
        float[] rightMost = null;
        for (float[] rect3 : taken) {
            boolean topRow = rect3[1] < highest + AnubisWidgets.s(42.0f);
            if (!topRow || rightMost != null && !(rect3[0] + rect3[2] > rightMost[0] + rightMost[2])) continue;
            rightMost = rect3;
        }
        if (rightMost == null) {
            float[] fArray3 = new float[2];
            fArray3[0] = margin;
            fArray = fArray3;
            fArray3[1] = AnubisWidgets.s(28.0f);
        } else {
            float[] fArray4 = new float[2];
            fArray4[0] = rightMost[0] + rightMost[2] + gap;
            fArray = fArray4;
            fArray4[1] = rightMost[1];
        }
        float[] fArray5 = fArray;
        ArrayList<float[]> scan = new ArrayList<float[]>();
        scan.add(new float[]{margin, AnubisWidgets.s(28.0f)});
        for (float[] rect4 : taken) {
            scan.add(new float[]{rect4[0] + rect4[2] + gap, rect4[1]});
            scan.add(new float[]{rect4[0], rect4[1] + rect4[3] + gap});
        }
        scan.sort(Comparator.comparingDouble(spot -> spot[1]).thenComparingDouble(spot -> spot[0]));
        scan.addFirst(fArray5);
        for (float[] spot2 : scan) {
            boolean onScreen = spot2[0] >= 0.0f && spot2[1] >= 0.0f && spot2[0] + w <= sw - margin && spot2[1] + h <= bottom;
            if (!onScreen || ClickGuiRenderer.coveredArea(spot2[0], spot2[1], h, taken) != 0.0f) continue;
            return spot2;
        }
        float[] best = null;
        float bestArea = Float.MAX_VALUE;
        for (float[] spot3 : scan) {
            float y;
            float x = Math.max(0.0f, Math.min(spot3[0], sw - w - margin));
            float area = ClickGuiRenderer.coveredArea(x, y = Math.max(0.0f, Math.min(spot3[1], sh - h - margin)), h, taken);
            if (!(area < bestArea)) continue;
            best = new float[]{x, y};
            bestArea = area;
        }
        return best;
    }

    private static float coveredArea(float x, float y, float h, List<float[]> taken) {
        float w = AnubisWidgets.s(212.0f);
        float m = AnubisWidgets.s(14.0f) * 0.5f;
        float area = 0.0f;
        for (float[] r : taken) {
            float overlapW = Math.min(x + w, r[0] + r[2]) + m - Math.max(x, r[0]);
            float overlapH = Math.min(y + h, r[1] + r[3]) + m - Math.max(y, r[1]);
            if (!(overlapW > 0.0f) || !(overlapH > 0.0f)) continue;
            area += overlapW * overlapH;
        }
        return area;
    }

    private static Map<String, float[]> savedPanelSpots() {
        HashMap<String, float[]> spots = new HashMap<String, float[]>();
        String ini = ImGui.getIO().getIniFilename();
        if (ini == null || !Files.isRegularFile(Path.of(ini, new String[0]), new LinkOption[0])) {
            return spots;
        }
        String section = "[Window][";
        try {
            String window = null;
            for (String line : Files.readAllLines(Path.of(ini, new String[0]))) {
                if (line.startsWith("[")) {
                    boolean panel = line.startsWith(section + PANEL_ID_PREFIX) && line.endsWith("]");
                    window = panel ? line.substring(section.length(), line.length() - 1) : null;
                    continue;
                }
                if (window == null || !line.startsWith("Pos=")) continue;
                String[] xy = line.substring(4).split(",");
                spots.put(window, new float[]{Float.parseFloat(xy[0]), Float.parseFloat(xy[1])});
            }
        }
        catch (IOException | RuntimeException e) {
            spots.clear();
        }
        return spots;
    }

    private List<ClientModule> modulesFor(ModuleCategory category) {
        return ModuleRegistry.inCategory(category);
    }

    private List<ClientModule> searchModules(String query) {
        int revision = ModuleRegistry.revision();
        if (revision != this.cachedRegistryRevision) {
            this.cachedRegistryRevision = revision;
            this.cachedGlobalSearch = "";
            this.cachedGlobalResults = List.of();
        }
        if (query.equals(this.cachedGlobalSearch)) {
            return this.cachedGlobalResults;
        }
        ArrayList<ClientModule> matches = new ArrayList<ClientModule>();
        for (ClientModule module : ModuleRegistry.all()) {
            if (!module.name().toLowerCase(Locale.ROOT).contains(query)) continue;
            matches.add(module);
        }
        this.cachedGlobalSearch = query;
        this.cachedGlobalResults = List.copyOf(matches);
        return this.cachedGlobalResults;
    }

    private static String normalizeSearch(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private void renderTabBar(float dt, float open) {
        Tab[] tabs = Tab.values();
        ImFont font = ImGui.getFont();
        float h = AnubisWidgets.s(44.0f);
        float inner = AnubisWidgets.s(4.0f);
        float padX = AnubisWidgets.s(20.0f);
        float[] widths = new float[tabs.length];
        float total = inner * 2.0f;
        for (int i = 0; i < tabs.length; ++i) {
            widths[i] = AnubisWidgets.textWidth(font, tabs[i].label) + padX * 2.0f;
            total += widths[i];
        }
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float x = (sw - total) * 0.5f;
        float y = sh - h - AnubisWidgets.s(26.0f) + AnubisWidgets.s(16.0f) * (1.0f - open);
        ImGui.setNextWindowPos((float)x, (float)y, (int)1);
        ImGui.setNextWindowSize((float)total, (float)h, (int)1);
        ImGui.pushStyleVar((int)0, (float)Math.max(0.01f, open));
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)(h * 0.5f));
        ImGui.pushStyleColor((int)2, (int)GuiPalette.tabBarBg());
        if (ImGui.begin((String)"##tabbar", (int)319)) {
            this.tabBarRect = new float[]{x, y, total, h};
            if (this.tab == Tab.THEME) {
                this.outlineSurface(ThemeStore.get().tabBar, x, y, total, h, h * 0.5f);
            }
            ClickGuiRenderer.drawShadow(x, y, total, h, h * 0.5f, 4);
            ImDrawList dl = ImGui.getWindowDrawList();
            SurfaceBackground.draw(dl, ThemeStore.get().tabBar, x, y, total, h, h * 0.5f);
            dl.addRect(x, y, x + total, y + h, AnubisWidgets.c(0x12FFFFFF), h * 0.5f, 0, 1.0f);
            float targetX = inner;
            for (int i = 0; i < this.tab.ordinal(); ++i) {
                targetX += widths[i];
            }
            float targetW = widths[this.tab.ordinal()];
            if (this.tabIndicatorX < 0.0f) {
                this.tabIndicatorX = targetX;
                this.tabIndicatorW = targetW;
            }
            this.tabIndicatorX = ClickGuiRenderer.smooth(this.tabIndicatorX, targetX, dt, 18.0f);
            this.tabIndicatorW = ClickGuiRenderer.smooth(this.tabIndicatorW, targetW, dt, 18.0f);
            float pillH = h - inner * 2.0f;
            dl.addRectFilled(x + this.tabIndicatorX, y + inner, x + this.tabIndicatorX + this.tabIndicatorW, y + inner + pillH, AnubisWidgets.c(GuiPalette.accent()), pillH * 0.5f);
            float tx = inner;
            for (int i = 0; i < tabs.length; ++i) {
                ImGui.setCursorPos((float)tx, (float)inner);
                if (ImGui.invisibleButton((String)("##tab_" + tabs[i].name()), (float)widths[i], (float)pillH)) {
                    this.switchTab(tabs[i]);
                }
                float hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
                float centre = x + tx + widths[i] * 0.5f;
                float cover = Math.max(0.0f, 1.0f - Math.abs(centre - (x + this.tabIndicatorX + this.tabIndicatorW * 0.5f)) / Math.max(1.0f, widths[i] * 0.5f));
                int color = AnubisTheme.lerpColor(AnubisTheme.lerpColor(-4542808, -461582, hover), -1, cover);
                float tw = AnubisWidgets.textWidth(font, tabs[i].label);
                AnubisWidgets.text(dl, font, x + tx + (widths[i] - tw) * 0.5f, y + (h - AnubisWidgets.lineHeight(font)) * 0.5f, color, tabs[i].label, this.tabBarGuard);
                tx += widths[i];
            }
        }
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar((int)3);
    }

    private float[] beginCard(String id, float alpha, float view) {
        return this.beginCard(id, alpha, view, ImGui.getIO().getDisplaySizeX() * 0.5f);
    }

    private float[] beginCard(String id, float alpha, float view, float centreX) {
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float reserved = AnubisWidgets.s(86.0f);
        float w = Math.min(sw - AnubisWidgets.s(32.0f), AnubisWidgets.s(460.0f));
        float h = Math.min(sh - reserved - AnubisWidgets.s(32.0f), AnubisWidgets.s(760.0f));
        float x = Math.max(AnubisWidgets.s(16.0f), centreX - w * 0.5f);
        float y = Math.max(AnubisWidgets.s(16.0f), (sh - reserved - h) * 0.5f + AnubisWidgets.s(8.0f)) + AnubisWidgets.s(24.0f) * (1.0f - view);
        ImGui.setNextWindowPos((float)x, (float)y, (int)1);
        ImGui.setNextWindowSize((float)w, (float)h, (int)1);
        ImGui.pushStyleVar((int)0, (float)alpha);
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)ClickGuiRenderer.cardRounding());
        ImGui.pushStyleColor((int)2, (int)GuiPalette.cardBg());
        if (this.cardTextLift) {
            ImGui.pushStyleColor((int)0, (int)-1);
            ImGui.pushStyleColor((int)1, (int)-2042416);
        }
        ImGui.begin((String)id, (int)8511);
        ClickGuiRenderer.drawShadow(x, y, w, h, ClickGuiRenderer.cardRounding(), 6);
        ImDrawList dl = ImGui.getWindowDrawList();
        Surface cards = ThemeStore.get().cards;
        SurfaceBackground.draw(dl, cards, x, y, w, h, ClickGuiRenderer.cardRounding());
        dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), ClickGuiRenderer.cardRounding(), 0, 1.0f);
        float top = SurfaceBackground.hasImageBanner(cards) ? Math.max(AnubisWidgets.s(22.0f), SurfaceBackground.bannerHeight(cards) - AnubisWidgets.s(44.0f)) : AnubisWidgets.s(22.0f);
        return new float[]{w, h, top, x, y};
    }

    private static float panelRounding() {
        return AnubisWidgets.s(GuiPalette.rounding());
    }

    public static float cardRounding() {
        return AnubisWidgets.s(Math.min(28.0f, GuiPalette.rounding() * 2.2f));
    }

    private void endCard() {
        ImGui.end();
        ImGui.popStyleColor((int)(this.cardTextLift ? 3 : 1));
        ImGui.popStyleVar((int)4);
    }

    public static void drawCardTitle(String title, float x, float centreY) {
        ClickGuiRenderer.drawCardTitle(title, x, centreY, false);
    }

    private static void drawCardTitle(String title, float x, float centreY, boolean guard) {
        ImFont font = ImGuiManager.getInstance().titleFont();
        AnubisWidgets.text(ImGui.getWindowDrawList(), font, ImGui.getWindowPosX() + x, ImGui.getWindowPosY() + centreY - AnubisWidgets.lineHeight(font) * 0.5f, -461582, title, guard);
    }

    public static float mutedParagraph(String text, float x, float y, float wrapX) {
        ImGui.setCursorPos((float)x, (float)y);
        ImGui.pushTextWrapPos((float)wrapX);
        ImGui.pushStyleColor((int)0, (ImVec4)ImGui.getStyleColorVec4((int)1));
        ImGui.textUnformatted((String)text);
        ImGui.popStyleColor();
        ImGui.popTextWrapPos();
        return ImGui.getCursorPosY();
    }

    public static void cardDivider(float pad, float w, float localY) {
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY();
        ImGui.getWindowDrawList().addLine(wx + pad, wy + localY, wx + w - pad, wy + localY, AnubisWidgets.c(0xFFFFFFF), 1.0f);
    }

    private void renderModuleCard(ClientModule module, float dt, float alpha, float view) {
        this.renderModuleCard(module, dt, alpha, view, false);
    }

    private void renderModuleCard(ClientModule module, float dt, float alpha, float view, boolean preview) {
        this.renderModuleCard(module, dt, alpha, view, preview, !preview);
    }

    private void renderModuleCard(ClientModule module, float dt, float alpha, float view, boolean preview, boolean showBack) {
        float[] size;
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        if (preview) {
            size = this.beginCard("##preview_card", alpha, view, (sw - AnubisWidgets.s(408.0f)) * 0.5f);
            this.cardRect = new float[]{size[3], size[4], size[0], size[1]};
            this.outlineSurface(ThemeStore.get().cards, size[3], size[4], size[0], size[1], ClickGuiRenderer.cardRounding());
            ImGui.pushStyleVar((int)1, (float)1.0f);
            ImGui.beginDisabled();
        } else {
            ImGui.getBackgroundDrawList().addRectFilled(0.0f, 0.0f, sw, sh, AnubisWidgets.withAlpha(0x55000000, view));
            size = this.beginCard("##module_card", alpha, view);
        }
        float w = size[0];
        float h = size[1];
        float pad = AnubisWidgets.s(24.0f);
        float top = size[2];
        float btn = AnubisWidgets.s(32.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY();
        float titleX = pad;
        if (!preview && showBack) {
            ImGui.setCursorPos((float)(pad - AnubisWidgets.s(4.0f)), (float)top);
            if (AnubisWidgets.iconButton("##back", btn)) {
                this.closeSettings();
            }
            AnubisWidgets.chevronLeft(dl, wx + pad - AnubisWidgets.s(4.0f) + btn * 0.5f - AnubisWidgets.s(1.0f), wy + top + btn * 0.5f, AnubisWidgets.s(5.0f), -461582);
            titleX = pad + btn + AnubisWidgets.s(8.0f);
        }
        ClickGuiRenderer.drawCardTitle(module.name(), titleX, top + btn * 0.5f, this.cardGuard);
        if (module.isAction()) {
            float openW = AnubisWidgets.s(72.0f);
            float openH = AnubisWidgets.s(30.0f);
            ImGui.setCursorPos((float)(w - pad - openW), (float)(top + (btn - openH) * 0.5f));
            if (AnubisWidgets.chipButton("##open", "Open", openW, openH, true)) {
                module.onClick();
            }
        } else {
            float swW = AnubisWidgets.s(46.0f);
            float swH = AnubisWidgets.s(26.0f);
            ImGui.setCursorPos((float)(w - pad - swW), (float)(top + (btn - swH) * 0.5f));
            float swX = ImGui.getCursorScreenPosX();
            float swY = ImGui.getCursorScreenPosY();
            if (ImGui.invisibleButton((String)"##enabled", (float)swW, (float)swH)) {
                module.toggle();
            }
            AnubisWidgets.drawSwitch(dl, swX, swY, swW, swH, AnubisWidgets.animate(ImGui.getItemID(), module.isEnabled(), 14.0f));
        }
        float y = top + btn + AnubisWidgets.s(12.0f);
        String description = module.description();
        if (description != null && !description.isBlank()) {
            y = ClickGuiRenderer.mutedParagraph(description, pad, y, w - pad) + AnubisWidgets.s(4.0f);
        }
        ClickGuiRenderer.cardDivider(pad, w, y += AnubisWidgets.s(12.0f));
        ImGui.setCursorPos((float)0.0f, (float)(y + 1.0f));
        if (ImGui.beginChild((String)"##body", (float)w, (float)Math.max(1.0f, h - y - 1.0f - AnubisWidgets.s(10.0f)), (boolean)false, (int)24)) {
            float contentW = w - pad * 2.0f;
            float labelY = ImGui.getCursorPosY() + AnubisWidgets.s(16.0f);
            this.sectionLabel("Settings", pad);
            if (!preview && module.hasModifiedSettings()) {
                this.renderResetAllChip(module, pad, contentW, labelY);
            }
            boolean any = false;
            for (Setting setting : module.settings()) {
                if (!setting.isVisible() || module.isAction() && setting == module.showInHud()) continue;
                any = true;
                ImGui.pushID((String)setting.id());
                this.renderSetting(module, setting, pad, contentW);
                ImGui.popID();
            }
            if (!any) {
                ClickGuiRenderer.mutedParagraph("This module has no settings.", pad, ImGui.getCursorPosY() + AnubisWidgets.s(8.0f), w - pad);
            }
            this.sectionLabel("Keybind", pad);
            ImGui.pushID((String)"##keybind_row");
            this.renderKeybindRow(module, pad, contentW);
            ImGui.popID();
            if (ClickGuiRenderer.isHudModule(module)) {
                this.renderHudActions(pad, contentW);
            }
            if (module.inConfigs()) {
                this.sectionLabel("Config", pad);
                this.renderModuleConfigActions(module, pad, contentW);
            }
            ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(18.0f));
            this.applySmoothScroll("card_" + module.id(), dt);
        }
        ImGui.endChild();
        if (preview) {
            ImGui.endDisabled();
            ImGui.popStyleVar();
        }
        this.endCard();
    }

    private void renderResetAllChip(ClientModule module, float pad, float contentW, float labelY) {
        float afterY = ImGui.getCursorPosY();
        String key = "reset:" + module.id();
        String label = this.isConfirming(key) ? "Confirm reset" : "Reset all";
        ImFont small = ImGuiManager.getInstance().smallFont();
        boolean pushed = ClickGuiRenderer.pushFont(small);
        float chipW = AnubisWidgets.textWidth(small, label) + AnubisWidgets.s(18.0f);
        float chipH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(8.0f);
        ImGui.setCursorPos((float)(pad + contentW - chipW), (float)(labelY + (ImGui.getTextLineHeight() - chipH) * 0.5f));
        if (AnubisWidgets.chipButton("##reset_all", label, chipW, chipH, this.isConfirming(key)) && this.confirmClick(key)) {
            module.resetSettings();
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Restore every setting to its default");
        }
        if (pushed) {
            ImGui.popFont();
        }
        ImGui.setCursorPosY((float)afterY);
    }

    private boolean confirmClick(String key) {
        if (this.isConfirming(key)) {
            this.pendingConfirm = null;
            return true;
        }
        this.pendingConfirm = key;
        this.pendingConfirmAt = System.nanoTime();
        return false;
    }

    private boolean isConfirming(String key) {
        return key.equals(this.pendingConfirm) && System.nanoTime() - this.pendingConfirmAt < 3000000000L;
    }

    private void sectionLabel(String text, float pad) {
        ImGui.setCursorPos((float)pad, (float)(ImGui.getCursorPosY() + AnubisWidgets.s(16.0f)));
        boolean pushed = ClickGuiRenderer.pushFont(ImGuiManager.getInstance().smallFont());
        ImGui.pushStyleColor((int)0, (ImVec4)ImGui.getStyleColorVec4((int)1));
        ImGui.textUnformatted((String)text);
        ImGui.popStyleColor();
        if (pushed) {
            ImGui.popFont();
        }
        ImGui.setCursorPosY((float)(ImGui.getCursorPosY() + AnubisWidgets.s(4.0f)));
        this.dividerPending = false;
    }

    private float rowStart(float pad, float contentW) {
        float localY = ImGui.getCursorPosY();
        if (this.dividerPending) {
            float wx = ImGui.getWindowPosX();
            float sy = ImGui.getCursorScreenPosY();
            ImGui.getWindowDrawList().addLine(wx + pad, sy, wx + pad + contentW, sy, AnubisWidgets.c(0xFFFFFFF), 1.0f);
        }
        this.dividerPending = true;
        return localY;
    }

    public static void rowLabel(String label, float pad, float startY, float rowH) {
        ImGui.setCursorPos((float)pad, (float)(startY + (rowH - ImGui.getTextLineHeight()) * 0.5f));
        ImGui.textUnformatted((String)label);
    }

    private void openSettings(ClientModule module) {
        this.settingsModule = module;
        this.viewChangedAt = System.nanoTime();
        this.cardJustOpened = true;
    }

    private void closeSettings() {
        if (this.settingsModule != null) {
            this.viewChangedAt = System.nanoTime();
        }
        this.settingsModule = null;
        this.listeningModule = null;
        this.listeningKeySetting = null;
        this.activeColorModule = null;
        this.activeColorSetting = null;
        this.colorPickerOpen.set(false);
        this.blockLists.close();
        this.itemPickers.close();
        this.itemColorWindowOpen = false;
    }

    private void closeCardOnOutsideClick() {
        if (this.cardJustOpened) {
            this.cardJustOpened = false;
            return;
        }
        if (this.settingsModule == null || this.listeningModule != null || this.listeningKeySetting != null) {
            return;
        }
        if (ImGui.isMouseClicked((int)0) && !ImGui.isWindowHovered((int)4) && !ImGui.isAnyItemActive()) {
            this.closeSettings();
        }
    }

    private void renderFriendsCard(float dt, float alpha, float view) {
        float[] size = this.beginCard("##friends_card", alpha, view);
        float w = size[0];
        float h = size[1];
        float pad = AnubisWidgets.s(24.0f);
        float top = size[2];
        float btn = AnubisWidgets.s(32.0f);
        FriendStore store = FriendStore.getInstance();
        List<String> friends = store.all();
        ClickGuiRenderer.drawCardTitle("Friends", pad, top + btn * 0.5f, this.cardGuard);
        String count = friends.size() == 1 ? "1 player" : friends.size() + " players";
        ImFont small = ImGuiManager.getInstance().smallFont();
        AnubisWidgets.text(ImGui.getWindowDrawList(), small, ImGui.getWindowPosX() + w - pad - AnubisWidgets.textWidth(small, count), ImGui.getWindowPosY() + top + (btn - AnubisWidgets.lineHeight(small)) * 0.5f, -4542808, count, this.cardGuard);
        float y = ClickGuiRenderer.mutedParagraph("Add players by their exact username.", pad, top + btn + AnubisWidgets.s(12.0f), w - pad) + AnubisWidgets.s(14.0f);
        float addW = AnubisWidgets.s(84.0f);
        float inputH = ImGui.getTextLineHeight() + AnubisWidgets.s(16.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(12.0f), (float)AnubisWidgets.s(8.0f));
        ImGui.setNextItemWidth((float)(w - pad * 2.0f - addW - AnubisWidgets.s(8.0f)));
        if (this.focusFriendInput) {
            ImGui.setKeyboardFocusHere();
            this.focusFriendInput = false;
        }
        ImGui.inputTextWithHint((String)"##friend_input", (String)"Username", (ImString)this.friendInput, (int)16);
        boolean submitted = AnubisWidgets.submittedWithEnter();
        ImGui.popStyleVar();
        ImGui.setCursorPos((float)(w - pad - addW), (float)y);
        if (AnubisWidgets.chipButton("##friend_add", "Add", addW, inputH, true)) {
            submitted = true;
        }
        if (submitted) {
            this.tryAddFriend(store);
        }
        y += inputH + AnubisWidgets.s(8.0f);
        if (this.friendMessage != null && ClickGuiRenderer.elapsed(this.friendMessageAt, 2500.0f) < 1.0f) {
            ImGui.setCursorPos((float)pad, (float)y);
            boolean pushed = ClickGuiRenderer.pushFont(small);
            ImGui.pushStyleColor((int)0, (int)-10856974);
            ImGui.textUnformatted((String)this.friendMessage);
            ImGui.popStyleColor();
            if (pushed) {
                ImGui.popFont();
            }
        }
        ClickGuiRenderer.cardDivider(pad, w, y += AnubisWidgets.lineHeight(small) + AnubisWidgets.s(10.0f));
        ImGui.setCursorPos((float)0.0f, (float)(y + 1.0f));
        if (ImGui.beginChild((String)"##friend_list", (float)w, (float)Math.max(1.0f, h - y - 1.0f - AnubisWidgets.s(10.0f)), (boolean)false, (int)24)) {
            if (friends.isEmpty()) {
                String empty = "No friends yet";
                ImGui.setCursorPos((float)((w - ImGui.calcTextSize((String)empty).x) * 0.5f), (float)AnubisWidgets.s(40.0f));
                ImGui.textDisabled((String)empty);
            } else {
                ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(8.0f));
                for (String name : friends) {
                    this.renderFriendRow(store, name, pad, w - pad * 2.0f);
                }
            }
            ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(12.0f));
            this.applySmoothScroll("friends", dt);
        }
        ImGui.endChild();
        this.endCard();
    }

    private void renderFriendRow(FriendStore store, String name, float pad, float contentW) {
        ImGui.pushID((String)("friend_" + name));
        float rowH = AnubisWidgets.s(48.0f);
        float localY = ImGui.getCursorPosY();
        ImGui.setCursorPos((float)pad, (float)localY);
        float x0 = ImGui.getCursorScreenPosX();
        float y0 = ImGui.getCursorScreenPosY();
        ImGui.setNextItemAllowOverlap();
        ImGui.invisibleButton((String)"##row", (float)contentW, (float)rowH);
        boolean rowHovered = ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + contentW), (float)(y0 + rowH));
        float hover = AnubisWidgets.animate(ImGui.getItemID(), rowHovered, 18.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        if (hover > 0.01f) {
            dl.addRectFilled(x0 - AnubisWidgets.s(8.0f), y0, x0 + contentW + AnubisWidgets.s(8.0f), y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(0xCFFFFFF, hover)), AnubisWidgets.s(10.0f));
        }
        float avatarR = AnubisWidgets.s(15.0f);
        float acx = x0 + avatarR;
        float acy = y0 + rowH * 0.5f;
        int avatarColor = AVATAR_COLORS[Math.floorMod(name.toLowerCase(Locale.ROOT).hashCode(), AVATAR_COLORS.length)];
        dl.addCircleFilled(acx, acy, avatarR, AnubisWidgets.c(avatarColor), 32);
        ImFont category = ImGuiManager.getInstance().categoryFont();
        String initial = name.substring(0, 1).toUpperCase(Locale.ROOT);
        AnubisWidgets.text(dl, category, acx - AnubisWidgets.textWidth(category, initial) * 0.5f, acy - AnubisWidgets.lineHeight(category) * 0.5f, -1, initial);
        ImFont font = ImGui.getFont();
        AnubisWidgets.text(dl, font, x0 + avatarR * 2.0f + AnubisWidgets.s(12.0f), y0 + (rowH - AnubisWidgets.lineHeight(font)) * 0.5f, -461582, name);
        float removeSize = AnubisWidgets.s(28.0f);
        ImGui.setCursorPos((float)(pad + contentW - removeSize), (float)(localY + (rowH - removeSize) * 0.5f));
        float rx = ImGui.getCursorScreenPosX();
        float ry = ImGui.getCursorScreenPosY();
        if (ImGui.invisibleButton((String)"##remove", (float)removeSize, (float)removeSize)) {
            store.remove(name);
        }
        float removeHover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
        float rcx = rx + removeSize * 0.5f;
        float rcy = ry + removeSize * 0.5f;
        if (removeHover > 0.01f) {
            dl.addCircleFilled(rcx, rcy, removeSize * 0.5f, AnubisWidgets.c(AnubisWidgets.withAlpha(-10856974, 0.18f * removeHover)), 32);
        }
        AnubisWidgets.cross(dl, rcx, rcy, AnubisWidgets.s(4.5f), AnubisTheme.lerpColor(AnubisWidgets.withAlpha(-4542808, 0.25f + 0.75f * hover), -10856974, removeHover));
        ImGui.setCursorPos((float)0.0f, (float)(localY + rowH + AnubisWidgets.s(4.0f)));
        ImGui.popID();
    }

    private void tryAddFriend(FriendStore store) {
        String name = this.friendInput.get().trim();
        if (!FriendStore.isValidName(name)) {
            this.friendMessage = "Use 1-16 letters, digits or _ (Bedrock names start with .)";
        } else if (store.isFriend(name)) {
            this.friendMessage = name + " is already on your list";
        } else {
            store.add(name);
            this.friendInput.set("");
            this.friendMessage = null;
        }
        this.friendMessageAt = System.nanoTime();
        this.focusFriendInput = true;
    }

    private void renderConfigCard(float dt, float alpha, float view) {
        float[] size = this.beginCard("##config_card", alpha, view);
        float w = size[0];
        float h = size[1];
        float pad = AnubisWidgets.s(24.0f);
        float top = size[2];
        float btn = AnubisWidgets.s(32.0f);
        float contentW = w - pad * 2.0f;
        ModuleConfigStore store = ModuleConfigStore.getInstance();
        ConfigProfiles profiles = ConfigProfiles.getInstance();
        ImFont small = ImGuiManager.getInstance().smallFont();
        ClickGuiRenderer.drawCardTitle("Config", pad, top + btn * 0.5f, this.cardGuard);
        String status = store.isReadOnly() ? "Read-only" : (store.hasUnsavedChanges() ? "Saving..." : "Saved");
        int statusColor = store.isReadOnly() ? -10856974 : -4542808;
        AnubisWidgets.text(ImGui.getWindowDrawList(), small, ImGui.getWindowPosX() + w - pad - AnubisWidgets.textWidth(small, status), ImGui.getWindowPosY() + top + (btn - AnubisWidgets.lineHeight(small)) * 0.5f, statusColor, status, this.cardGuard);
        if (store.isReadOnly() && ImGui.isMouseHoveringRect((float)(ImGui.getWindowPosX() + w - pad - AnubisWidgets.s(80.0f)), (float)(ImGui.getWindowPosY() + top), (float)(ImGui.getWindowPosX() + w - pad), (float)(ImGui.getWindowPosY() + top + btn))) {
            ClickGuiRenderer.showTooltip("modules.json could not be read. Fix or delete it.");
        }
        float y = ClickGuiRenderer.mutedParagraph("Changes save automatically. Profiles can be shared with a code.", pad, top + btn + AnubisWidgets.s(12.0f), w - pad) + AnubisWidgets.s(14.0f);
        float gap = AnubisWidgets.s(8.0f);
        float chipH = ImGui.getTextLineHeight() + AnubisWidgets.s(16.0f);
        float chipW = (contentW - gap * 2.0f) / 3.0f;
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##cfg_save", "Save all", chipW, chipH, true)) {
            this.saveAllConfigs(store);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Save everything now");
        }
        ImGui.setCursorPos((float)(pad + chipW + gap), (float)y);
        if (AnubisWidgets.chipButton("##cfg_folder", "Open folder", chipW, chipH, false)) {
            this.openProfilesFolder(profiles);
        }
        ImGui.setCursorPos((float)(pad + (chipW + gap) * 2.0f), (float)y);
        boolean resetArmed = this.isConfirming("reset-all-modules");
        if (AnubisWidgets.chipButton("##cfg_reset", resetArmed ? "Confirm" : "Reset all", chipW, chipH, resetArmed) && this.confirmClick("reset-all-modules")) {
            for (ClientModule module : ModuleRegistry.all()) {
                module.resetSettings();
            }
            this.setConfigMessage("Every module setting is back to default", false);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Reset all module settings. Keybinds stay.");
        }
        ClickGuiRenderer.cardDivider(pad, w, y += chipH + AnubisWidgets.s(18.0f));
        float createW = AnubisWidgets.s(84.0f);
        ImGui.setCursorPos((float)pad, (float)(y += AnubisWidgets.s(14.0f)));
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(12.0f), (float)AnubisWidgets.s(8.0f));
        ImGui.setNextItemWidth((float)(contentW - createW - gap));
        if (this.focusProfileInput) {
            ImGui.setKeyboardFocusHere();
            this.focusProfileInput = false;
        }
        ImGui.inputTextWithHint((String)"##profile_input", (String)"New profile name", (ImString)this.profileInput);
        boolean submitted = AnubisWidgets.submittedWithEnter();
        ImGui.popStyleVar();
        ImGui.setCursorPos((float)(w - pad - createW), (float)y);
        if (AnubisWidgets.chipButton("##profile_create", "Create", createW, chipH, true)) {
            submitted = true;
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Save your current modules as a new profile");
        }
        if (submitted) {
            this.tryCreateProfile(profiles);
        }
        float halfW = (contentW - gap) * 0.5f;
        ImGui.setCursorPos((float)pad, (float)(y += chipH + AnubisWidgets.s(8.0f)));
        if (AnubisWidgets.chipButton("##profile_import", "Import file", halfW, chipH, false)) {
            this.importProfileFile();
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Import a profile .json file. You can also drop it here.");
        }
        ImGui.setCursorPos((float)(pad + halfW + gap), (float)y);
        if (AnubisWidgets.chipButton("##profile_paste", "Paste code", halfW, chipH, false)) {
            this.pasteProfileCode();
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Load a share code from your clipboard. Your current settings go to Auto backup.");
        }
        y += chipH + AnubisWidgets.s(8.0f);
        if (this.configMessage != null && ClickGuiRenderer.elapsed(this.configMessageAt, 2500.0f) < 1.0f) {
            ImGui.setCursorPos((float)pad, (float)y);
            boolean pushed = ClickGuiRenderer.pushFont(small);
            ImGui.pushStyleColor((int)0, (int)(this.configMessageError ? -10856974 : -4542808));
            ImGui.textUnformatted((String)this.configMessage);
            ImGui.popStyleColor();
            if (pushed) {
                ImGui.popFont();
            }
        }
        ClickGuiRenderer.cardDivider(pad, w, y += AnubisWidgets.lineHeight(small) + AnubisWidgets.s(10.0f));
        List<ConfigProfiles.Profile> list = profiles.list();
        ImGui.setCursorPos((float)0.0f, (float)(y + 1.0f));
        if (ImGui.beginChild((String)"##profile_list", (float)w, (float)Math.max(1.0f, h - y - 1.0f - AnubisWidgets.s(10.0f)), (boolean)false, (int)24)) {
            if (list.isEmpty()) {
                String empty = "No profiles yet";
                ImGui.setCursorPos((float)((w - ImGui.calcTextSize((String)empty).x) * 0.5f), (float)AnubisWidgets.s(40.0f));
                ImGui.textDisabled((String)empty);
            } else {
                ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(8.0f));
                this.profileList.render(profiles, list, store.activeProfile(), pad, contentW);
            }
            ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(12.0f));
            float target = this.profileList.takeScrollTarget();
            if (!Float.isNaN(target)) {
                this.scroll.computeIfAbsent("profiles", k -> new float[2])[1] = target;
            }
            this.applySmoothScroll("profiles", dt);
        }
        ImGui.endChild();
        this.endCard();
    }

    private void tryCreateProfile(ConfigProfiles profiles) {
        try {
            String name = profiles.create(this.profileInput.get());
            this.profileInput.set("");
            this.profileList.open(name);
            this.setConfigMessage("Created " + name + " from the current modules", false);
        }
        catch (ProfileException e) {
            this.setConfigMessage(e.getMessage(), true);
        }
        this.focusProfileInput = true;
    }

    private void importProfileFile() {
        if (FileDialogs.busy()) {
            this.setConfigMessage("A file window is already open", true);
            return;
        }
        FileDialogs.openProfile("Import an Anubis profile", this::importProfile);
    }

    private void importProfile(Path path) {
        try {
            String name = ConfigProfiles.getInstance().importFile(path);
            this.profileList.open(name);
            this.setConfigMessage("Imported " + name + " - press Load to use it", false);
        }
        catch (ProfileException e) {
            this.setConfigMessage(e.getMessage(), true);
        }
    }

    private void pasteProfileCode() {
        ConfigProfiles profiles = ConfigProfiles.getInstance();
        try {
            String name = profiles.importCode(class_310.method_1551().field_1774.method_1460());
            ConfigProfiles.Profile imported = profiles.find(name);
            profiles.backup(imported == null ? Set.of(ConfigProfiles.Part.MODULES) : imported.parts());
            profiles.load(name);
            this.profileList.open(name);
            this.setConfigMessage("Loaded " + name + " - your previous settings are in Auto backup", false);
        }
        catch (ProfileException e) {
            this.setConfigMessage(e.getMessage(), true);
        }
    }

    private void saveAllConfigs(ModuleConfigStore store) {
        store.save();
        ThemeStore.get().save();
        HudTheme.get().save();
        HudLayoutStore.getInstance().save();
        FriendStore.getInstance().save();
        if (store.isReadOnly()) {
            this.setConfigMessage("modules.json is unreadable, so it was left untouched", true);
        } else {
            this.setConfigMessage("Everything saved", false);
        }
    }

    private void openProfilesFolder(ConfigProfiles profiles) {
        try {
            Path dir = profiles.directory();
            Files.createDirectories(dir, new FileAttribute[0]);
            class_156.method_668().method_60932(dir);
        }
        catch (IOException | RuntimeException e) {
            this.setConfigMessage("Could not open the profiles folder", true);
        }
    }

    private void setConfigMessage(String message, boolean error) {
        this.configMessage = message;
        this.configMessageError = error;
        this.configMessageAt = System.nanoTime();
    }

    private void renderThemeStudio(float dt, float alpha, float view) {
        ThemeStore theme = ThemeStore.get();
        float preview = ClickGuiRenderer.easeOut(ClickGuiRenderer.elapsed(this.previewChangedAt, 260.0f));
        this.panelRects.clear();
        this.cardRect = null;
        this.inspectMode = true;
        if (this.themeSurface == theme.cards) {
            ClientModule sample = ClickGuiRenderer.previewModule();
            if (sample != null) {
                this.renderModuleCard(sample, dt, alpha * preview, Math.min(view, preview), true);
            }
        } else {
            this.renderModulePanels(dt, alpha * preview);
        }
        this.inspectMode = false;
        this.renderInspector(theme, dt, alpha);
        this.highlightSurfaces(theme);
    }

    private void renderInspector(ThemeStore theme, float dt, float alpha) {
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float w = AnubisWidgets.s(360.0f);
        float h = Math.min(sh - AnubisWidgets.s(86.0f) - AnubisWidgets.s(40.0f), AnubisWidgets.s(760.0f));
        ImGui.setNextWindowPos((float)(sw - w - AnubisWidgets.s(24.0f)), (float)AnubisWidgets.s(24.0f), (int)4);
        ImGui.setNextWindowSize((float)w, (float)h, (int)1);
        if (this.focusInspector) {
            ImGui.setNextWindowFocus();
            this.focusInspector = false;
        }
        ImGui.pushStyleVar((int)0, (float)alpha);
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)ClickGuiRenderer.cardRounding());
        ImGui.pushStyleColor((int)2, (int)GuiPalette.abgr(theme.cards.color.argbWithAlpha(255)));
        int flags = 59;
        if (ImGui.begin((String)"##theme_inspector", (int)flags)) {
            float x = ImGui.getWindowPosX();
            float y = ImGui.getWindowPosY();
            this.inspectorRect = new float[]{x, y, w, h};
            ClickGuiRenderer.drawShadow(x, y, w, h, ClickGuiRenderer.cardRounding(), 6);
            ImGui.getWindowDrawList().addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), ClickGuiRenderer.cardRounding(), 0, 1.0f);
            float pad = AnubisWidgets.s(20.0f);
            float top = AnubisWidgets.s(20.0f);
            ClickGuiRenderer.drawCardTitle("Theme", pad, top + AnubisWidgets.s(14.0f));
            float ty = top + AnubisWidgets.s(34.0f);
            if (this.themeMessage != null && System.nanoTime() - this.themeMessageAt < 3000000000L) {
                ImGui.setCursorPos((float)pad, (float)ty);
                ImGui.pushTextWrapPos((float)(w - pad));
                ImGui.pushStyleColor((int)0, (int)(this.themeMessageError ? -10856974 : -461582));
                ImGui.textUnformatted((String)this.themeMessage);
                ImGui.popStyleColor();
                ImGui.popTextWrapPos();
                ty = ImGui.getCursorPosY();
            } else {
                ty = ClickGuiRenderer.mutedParagraph("Click a panel or card on screen to edit it.", pad, ty, w - pad);
            }
            ty = this.renderSurfaceTabs(theme, pad, ty + AnubisWidgets.s(12.0f), w - pad * 2.0f) + AnubisWidgets.s(14.0f);
            ClickGuiRenderer.cardDivider(pad, w, ty);
            ImGui.setCursorPos((float)0.0f, (float)(ty + 1.0f));
            if (ImGui.beginChild((String)"##inspector_body", (float)w, (float)Math.max(1.0f, h - ty - 1.0f - AnubisWidgets.s(10.0f)), (boolean)false, (int)24)) {
                float contentW = w - pad * 2.0f;
                if (this.themeSurface == null) {
                    this.renderGlobalOptions(theme, pad, contentW);
                } else {
                    this.renderSurfaceOptions(this.themeSurface, pad, contentW);
                }
                ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(18.0f));
                this.applySmoothScroll("inspector", dt);
            }
            ImGui.endChild();
        }
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar((int)4);
    }

    private float renderSurfaceTabs(ThemeStore theme, float pad, float y, float contentW) {
        String[] labels = new String[]{"Global", "Panels", "Cards", "Tab bar"};
        Surface[] targets = new Surface[]{null, theme.panels, theme.cards, theme.tabBar};
        int selected = 0;
        for (int i = 1; i < targets.length; ++i) {
            if (targets[i] != this.themeSurface) continue;
            selected = i;
        }
        float h = AnubisWidgets.s(32.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        int clicked = AnubisWidgets.segmented("##surface", labels, selected, contentW, h);
        if (clicked >= 0) {
            this.selectSurface(targets[clicked]);
        }
        return y + h;
    }

    private void renderGlobalOptions(ThemeStore theme, float pad, float contentW) {
        this.sectionLabel("Presets", pad);
        this.renderPresetRow(theme, pad, contentW);
        this.sectionLabel("Colors", pad);
        this.settingRows(pad, contentW, theme.accent);
        this.sectionLabel("Layout", pad);
        this.settingRows(pad, contentW, theme.rounding, theme.moduleDividers, theme.backgroundDim, theme.menuBlur);
        this.sectionLabel("Interface", pad);
        this.settingRows(pad, contentW, theme.uiScale, theme.font, theme.animationSpeed, theme.animateLines, theme.customMainMenu, theme.communityPopup);
        this.sectionLabel("Share", pad);
        this.renderThemeActions(theme, pad, contentW);
    }

    private void renderSurfaceOptions(Surface surface, float pad, float contentW) {
        this.sectionLabel("Fill", pad);
        this.settingRows(pad, contentW, surface.color, surface.opacity);
        this.sectionLabel("Background", pad);
        this.settingRows(pad, contentW, surface.background);
        if (surface.isImage()) {
            this.renderImageGallery(surface, pad, contentW);
        }
        this.settingRows(pad, contentW, surface.mapping, surface.coverage, surface.headerHeight, surface.imageOpacity, surface.imageScale, surface.imageX, surface.imageY, surface.lineOpacity);
        if (surface.isScreenMapped()) {
            ClickGuiRenderer.mutedParagraph("Screen mapping pins the background to the screen, so each surface is a window onto it. Drag a panel to see another part.", pad, ImGui.getCursorPosY() + AnubisWidgets.s(12.0f), pad + contentW);
        }
    }

    private void selectSurface(Surface surface) {
        if (surface == this.themeSurface) {
            return;
        }
        ThemeStore theme = ThemeStore.get();
        if (surface == theme.cards != (this.themeSurface == theme.cards)) {
            this.previewChangedAt = System.nanoTime();
        }
        this.themeSurface = surface;
        this.focusInspector = true;
    }

    private void highlightSurfaces(ThemeStore theme) {
        boolean clicked;
        float my;
        float mx = ImGui.getIO().getMousePosX();
        boolean blocked = ClickGuiRenderer.contains(this.inspectorRect, mx, my = ImGui.getIO().getMousePosY()) || ImGui.isPopupOpen((String)"", (int)3072);
        Surface hovered = null;
        if (!blocked) {
            for (float[] rect : this.panelRects) {
                if (!ClickGuiRenderer.contains(rect, mx, my)) continue;
                hovered = theme.panels;
            }
            if (ClickGuiRenderer.contains(this.cardRect, mx, my)) {
                hovered = theme.cards;
            }
            if (ClickGuiRenderer.contains(this.tabBarRect, mx, my)) {
                hovered = theme.tabBar;
            }
        }
        this.hoveredSurface = hovered;
        if (hovered == null) {
            return;
        }
        ImDrawList fg = ImGui.getForegroundDrawList();
        boolean tabBar = hovered == theme.tabBar;
        String hint = hovered.label + (tabBar ? "  \u00b7  right-click to edit" : "  \u00b7  click to edit");
        ImFont small = ImGuiManager.getInstance().smallFont();
        float chipW = AnubisWidgets.textWidth(small, hint) + AnubisWidgets.s(18.0f);
        float chipH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(10.0f);
        float cx = Math.min(mx + AnubisWidgets.s(14.0f), ImGui.getIO().getDisplaySizeX() - chipW - AnubisWidgets.s(4.0f));
        float cy = Math.min(my + AnubisWidgets.s(18.0f), ImGui.getIO().getDisplaySizeY() - chipH - AnubisWidgets.s(4.0f));
        fg.addRectFilled(cx, cy, cx + chipW, cy + chipH, GuiPalette.accent(), chipH * 0.5f);
        AnubisWidgets.text(fg, small, cx + AnubisWidgets.s(9.0f), cy + AnubisWidgets.s(5.0f), -1, hint);
        boolean bl = tabBar ? ImGui.isMouseClicked((int)1) : (clicked = ImGui.isMouseClicked((int)0) || ImGui.isMouseClicked((int)1));
        if (clicked) {
            this.selectSurface(hovered);
        }
    }

    private void outlineSurface(Surface surface, float x, float y, float w, float h, float rounding) {
        int color;
        if (surface == this.themeSurface) {
            color = AnubisWidgets.withAlpha(GuiPalette.accent(), 0.8f);
        } else if (surface == this.hoveredSurface) {
            color = -2130706433;
        } else {
            return;
        }
        float e = AnubisWidgets.s(3.0f);
        float reach = e + AnubisWidgets.s(2.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        dl.pushClipRect(x - reach, y - reach, x + w + reach, y + h + reach, false);
        dl.addRect(x - e, y - e, x + w + e, y + h + e, color, rounding + e, 0, AnubisWidgets.s(1.5f));
        dl.popClipRect();
    }

    private void recordPanelRect() {
        if (!this.inspectMode) {
            return;
        }
        float x = ImGui.getWindowPosX();
        float y = ImGui.getWindowPosY();
        float w = ImGui.getWindowWidth();
        float h = ImGui.getWindowHeight();
        this.panelRects.add(new float[]{x, y, w, h});
        this.outlineSurface(ThemeStore.get().panels, x, y, w, h, ClickGuiRenderer.panelRounding());
    }

    private static boolean contains(float[] rect, float x, float y) {
        return rect != null && x >= rect[0] && x <= rect[0] + rect[2] && y >= rect[1] && y <= rect[1] + rect[3];
    }

    private static ClientModule previewModule() {
        ClientModule fallback = null;
        for (ClientModule module : ModuleRegistry.all()) {
            if (fallback == null) {
                fallback = module;
            }
            if (module.settings().size() < 3) continue;
            return module;
        }
        return fallback;
    }

    private void settingRows(float pad, float contentW, Setting ... settings) {
        for (Setting setting : settings) {
            if (!setting.isVisible()) continue;
            ImGui.pushID((String)setting.id());
            this.renderSetting(null, setting, pad, contentW);
            ImGui.popID();
        }
    }

    private void renderPresetRow(ThemeStore theme, float pad, float contentW) {
        float rowH = AnubisWidgets.s(46.0f) + AnubisWidgets.s(6.0f);
        float startY = this.rowStart(pad, contentW);
        float size = AnubisWidgets.s(28.0f);
        float gap = AnubisWidgets.s(12.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        for (int i = 0; i < ThemeStore.PRESETS.size(); ++i) {
            ThemeStore.Preset preset = ThemeStore.PRESETS.get(i);
            ImGui.setCursorPos((float)(pad + (float)i * (size + gap)), (float)(startY + (rowH - size) * 0.5f));
            float cx = ImGui.getCursorScreenPosX() + size * 0.5f;
            float cy = ImGui.getCursorScreenPosY() + size * 0.5f;
            if (ImGui.invisibleButton((String)("##preset" + i), (float)size, (float)size)) {
                theme.applyPreset(preset);
            }
            boolean hovered = ImGui.isItemHovered();
            float hover = AnubisWidgets.animate(ImGui.getItemID(), hovered, 18.0f);
            boolean selected = theme.matchesPreset(preset);
            dl.addCircleFilled(cx, cy, size * 0.5f - AnubisWidgets.s(3.0f), AnubisWidgets.c(GuiPalette.abgr(preset.accent())), 32);
            if (selected) {
                dl.addCircle(cx, cy, size * 0.5f, AnubisWidgets.c(-1), 32, AnubisWidgets.s(1.5f));
            } else if (hover > 0.01f) {
                dl.addCircle(cx, cy, size * 0.5f, AnubisWidgets.c(AnubisWidgets.withAlpha(-1, 0.35f * hover)), 32, AnubisWidgets.s(1.2f));
            }
            if (!hovered) continue;
            ClickGuiRenderer.showTooltip(preset.name());
        }
        ImGui.setCursorPos((float)0.0f, (float)(startY + rowH));
    }

    private void renderImageGallery(Surface surface, float pad, float contentW) {
        List<String> files = ThemeImages.get().files();
        int columns = 3;
        float gap = AnubisWidgets.s(8.0f);
        float r = AnubisWidgets.s(8.0f);
        float tileW = (contentW - gap * (float)(columns - 1)) / (float)columns;
        float tileH = tileW * 0.6f;
        float startY = ImGui.getCursorPosY() + AnubisWidgets.s(4.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont small = ImGuiManager.getInstance().smallFont();
        String deleted = null;
        int count = files.size() + 1;
        for (int i = 0; i < count; ++i) {
            ImGui.setCursorPos((float)(pad + (float)(i % columns) * (tileW + gap)), (float)(startY + (float)(i / columns) * (tileH + gap)));
            float sx = ImGui.getCursorScreenPosX();
            float sy = ImGui.getCursorScreenPosY();
            String name = i == 0 ? null : files.get(i - 1);
            String deleteKey = name == null ? null : "image-delete:" + name;
            boolean armed = deleteKey != null && this.isConfirming(deleteKey);
            float del = AnubisWidgets.s(20.0f);
            float dx = sx + tileW - del - AnubisWidgets.s(5.0f);
            float dy = sy + AnubisWidgets.s(5.0f);
            boolean overTile = ImGui.isWindowHovered((int)128) && ImGui.isMouseHoveringRect((float)sx, (float)sy, (float)(sx + tileW), (float)(sy + tileH));
            boolean deleteShown = deleteKey != null && (armed || overTile);
            boolean deleteHovered = false;
            float deleteHover = 0.0f;
            if (deleteShown) {
                ImGui.setCursorScreenPos((float)dx, (float)dy);
                if (ImGui.invisibleButton((String)("##tiledel" + i), (float)del, (float)del) && this.confirmClick(deleteKey)) {
                    deleted = name;
                }
                deleteHovered = ImGui.isItemHovered();
                deleteHover = AnubisWidgets.animate(ImGui.getItemID(), deleteHovered, 18.0f);
                armed = this.isConfirming(deleteKey);
                if (deleteHovered) {
                    ClickGuiRenderer.showTooltip(armed ? "Click again to delete" : "Delete image");
                }
                ImGui.setCursorScreenPos((float)sx, (float)sy);
            }
            boolean clicked = ImGui.invisibleButton((String)("##tile" + i), (float)tileW, (float)tileH);
            if (PerformanceModule.optimizationsEnabled() && !ImGui.isItemVisible()) continue;
            boolean hovered = ImGui.isItemHovered() || deleteHovered;
            float hover = AnubisWidgets.animate(ImGui.getItemID(), hovered, 18.0f);
            if (i == 0) {
                dl.addRectFilled(sx, sy, sx + tileW, sy + tileH, AnubisWidgets.c(AnubisTheme.lerpColor(-13226454, -12240588, hover)), r);
                int glyph = AnubisTheme.lerpColor(-4542808, -461582, hover);
                float cx = sx + tileW * 0.5f;
                float cy = sy + tileH * 0.5f - AnubisWidgets.s(8.0f);
                dl.addLine(cx - AnubisWidgets.s(7.0f), cy, cx + AnubisWidgets.s(7.0f), cy, AnubisWidgets.c(glyph), AnubisWidgets.s(1.8f));
                dl.addLine(cx, cy - AnubisWidgets.s(7.0f), cx, cy + AnubisWidgets.s(7.0f), AnubisWidgets.c(glyph), AnubisWidgets.s(1.8f));
                String label = "Add image";
                AnubisWidgets.text(dl, small, cx - AnubisWidgets.textWidth(small, label) * 0.5f, cy + AnubisWidgets.s(12.0f), glyph, label);
                if (!clicked) continue;
                ThemeImages.get().browseAsync();
                continue;
            }
            ThemeImages.Texture texture = ThemeImages.get().preview(name);
            boolean broken = texture == null && ThemeImages.get().isBroken(name);
            dl.addRectFilled(sx, sy, sx + tileW, sy + tileH, AnubisWidgets.c(-13226454), r);
            if (texture != null) {
                float[] uv = SurfaceBackground.coverUv(texture.width(), texture.height(), tileW, tileH, 0.5f);
                dl.addImageRounded((long)texture.id(), sx, sy, sx + tileW, sy + tileH, uv[0], uv[1], uv[2], uv[3], AnubisWidgets.c(-1), r, 240);
            } else if (broken && !armed) {
                float cx = sx + tileW * 0.5f;
                float cy = sy + tileH * 0.5f - AnubisWidgets.s(8.0f);
                AnubisWidgets.cross(dl, cx, cy, AnubisWidgets.s(5.0f), -4542808);
                String label = "Can't load";
                AnubisWidgets.text(dl, small, cx - AnubisWidgets.textWidth(small, label) * 0.5f, cy + AnubisWidgets.s(10.0f), -4542808, label);
            }
            if (ThemeImages.isGif(name)) {
                String badge = "GIF";
                float badgePad = AnubisWidgets.s(4.0f);
                float badgeH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(2.0f);
                float badgeW = AnubisWidgets.textWidth(small, badge) + badgePad * 2.0f;
                float bx = sx + AnubisWidgets.s(5.0f);
                float by = sy + tileH - badgeH - AnubisWidgets.s(5.0f);
                dl.addRectFilled(bx, by, bx + badgeW, by + badgeH, AnubisWidgets.c(AnubisWidgets.withAlpha(-16777216, 0.6f)), AnubisWidgets.s(4.0f));
                AnubisWidgets.text(dl, small, bx + badgePad, by + AnubisWidgets.s(1.0f), -1, badge);
            }
            if (armed) {
                String ask = "Delete?";
                dl.addRectFilled(sx, sy, sx + tileW, sy + tileH, AnubisWidgets.c(AnubisWidgets.withAlpha(-16777216, 0.6f)), r);
                AnubisWidgets.text(dl, small, sx + (tileW - AnubisWidgets.textWidth(small, ask)) * 0.5f, sy + (tileH - AnubisWidgets.lineHeight(small)) * 0.5f, -1, ask);
            }
            if (name.equals(surface.image.get())) {
                dl.addRect(sx - AnubisWidgets.s(1.0f), sy - AnubisWidgets.s(1.0f), sx + tileW + AnubisWidgets.s(1.0f), sy + tileH + AnubisWidgets.s(1.0f), AnubisWidgets.c(GuiPalette.accent()), r + AnubisWidgets.s(1.0f), 0, AnubisWidgets.s(2.0f));
            } else if (hover > 0.01f) {
                dl.addRect(sx, sy, sx + tileW, sy + tileH, AnubisWidgets.c(AnubisWidgets.withAlpha(-1, 0.4f * hover)), r, 0, 1.0f);
            }
            if (deleteShown) {
                float fade = armed ? 1.0f : hover;
                float dcx = dx + del * 0.5f;
                float dcy = dy + del * 0.5f;
                int disc = armed ? -10856974 : AnubisTheme.lerpColor(AnubisWidgets.withAlpha(-16777216, 0.55f), GuiPalette.accent(), deleteHover);
                dl.addCircleFilled(dcx, dcy, del * 0.5f, AnubisWidgets.c(AnubisWidgets.withAlpha(disc, fade)), 32);
                AnubisWidgets.cross(dl, dcx, dcy, AnubisWidgets.s(3.5f), AnubisWidgets.withAlpha(-1, fade));
            }
            if (clicked) {
                if (armed) {
                    this.pendingConfirm = null;
                } else {
                    surface.image.set(name);
                }
            }
            if (deleteHovered || !ImGui.isItemHovered((int)65536)) continue;
            ClickGuiRenderer.showTooltip((String)(broken ? name + "\nCouldn't load this image" : name));
        }
        if (deleted != null) {
            this.deleteImage(deleted);
        }
        int rows = (count + columns - 1) / columns;
        float hintY = startY + (float)rows * (tileH + gap) - gap + AnubisWidgets.s(10.0f);
        String hint = "Drop an image on the screen or ";
        String link = "choose an image";
        float hintW = AnubisWidgets.textWidth(small, hint);
        float linkW = AnubisWidgets.textWidth(small, link);
        float lineH = AnubisWidgets.lineHeight(small);
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY() - ImGui.getScrollY();
        AnubisWidgets.text(dl, small, wx + pad, wy + hintY, -4542808, hint);
        ImGui.setCursorPos((float)(pad + hintW), (float)hintY);
        if (ImGui.invisibleButton((String)"##openfolder", (float)linkW, (float)lineH)) {
            ThemeImages.get().openFolder();
        }
        float linkHover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
        AnubisWidgets.text(dl, small, wx + pad + hintW, wy + hintY, AnubisTheme.lerpColor(GuiPalette.accent(), GuiPalette.accentHover(), linkHover), link);
        ImGui.setCursorPos((float)0.0f, (float)(hintY + lineH + AnubisWidgets.s(8.0f)));
    }

    private void renderThemeActions(ThemeStore theme, float pad, float contentW) {
        float gap = AnubisWidgets.s(8.0f);
        float h = AnubisWidgets.s(34.0f);
        float buttonW = (contentW - gap * 2.0f) / 3.0f;
        float y = ImGui.getCursorPosY() + AnubisWidgets.s(6.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##copytheme", "Copy theme", buttonW, h, false)) {
            class_310.method_1551().field_1774.method_1455(theme.exportString());
            this.showThemeMessage("Theme copied to the clipboard", false);
        }
        ImGui.setCursorPos((float)(pad + buttonW + gap), (float)y);
        if (AnubisWidgets.chipButton("##pastetheme", "Paste theme", buttonW, h, false)) {
            boolean applied = theme.importString(class_310.method_1551().field_1774.method_1460());
            this.showThemeMessage(applied ? "Theme applied from the clipboard" : "The clipboard doesn't contain an Anubis theme", !applied);
        }
        ImGui.setCursorPos((float)(pad + (buttonW + gap) * 2.0f), (float)y);
        if (AnubisWidgets.chipButton("##resettheme", "Reset", buttonW, h, false)) {
            theme.resetToDefaults();
            this.showThemeMessage("Theme reset to the defaults", false);
        }
        ImGui.setCursorPos((float)0.0f, (float)(y + h));
    }

    private void showThemeMessage(String message, boolean error) {
        this.themeMessage = message;
        this.themeMessageError = error;
        this.themeMessageAt = System.nanoTime();
    }

    public void onFilesDropped(List<Path> paths) {
        if (paths.isEmpty()) {
            return;
        }
        if (this.tab == Tab.CONFIG) {
            for (Path path : paths) {
                if (!path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json")) continue;
                this.importProfile(path);
                return;
            }
        }
        for (Path path : paths) {
            if (!ThemeImages.isImage(path)) continue;
            if (this.tab == Tab.HUD) {
                this.importHudHeaderImage(path);
            } else {
                this.switchTab(Tab.THEME);
                this.importHeaderImage(path);
            }
            return;
        }
        if (this.tab == Tab.HUD) {
            this.showHudMessage("Only PNG, JPG and GIF images can be used", true);
        } else {
            this.switchTab(Tab.THEME);
            this.showThemeMessage("Only PNG, JPG and GIF images can be used", true);
        }
    }

    private void importHudHeaderImage(Path path) {
        String name = ThemeImages.get().importFile(path);
        if (name == null) {
            this.showHudMessage("Only PNG, JPG and GIF images can be used", true);
            return;
        }
        String targetId = this.hudElementId != null ? this.hudElementId : "info_bar";
        Surface target = HudTheme.get().forElementId(targetId);
        if (target == null) {
            return;
        }
        target.image.set(name);
        target.background.set("Image");
        this.selectHudElement(targetId);
        this.showHudMessage(name + " is now the " + target.label.toLowerCase(Locale.ROOT) + " background", false);
    }

    private void importHeaderImage(Path path) {
        String name = ThemeImages.get().importFile(path);
        if (name == null) {
            this.showThemeMessage("Only PNG, JPG and GIF images can be used", true);
            return;
        }
        ThemeStore theme = ThemeStore.get();
        Surface target = this.themeSurface != null ? this.themeSurface : theme.cards;
        target.image.set(name);
        target.background.set("Image");
        this.showThemeMessage(name + " is now the " + target.label.toLowerCase(Locale.ROOT) + " background", false);
    }

    private void deleteImage(String name) {
        String message;
        boolean deleted = ThemeImages.get().delete(name);
        String string = message = deleted ? "Deleted " + name : "Couldn't delete " + name;
        if (this.tab == Tab.HUD) {
            this.showHudMessage(message, !deleted);
        } else {
            this.showThemeMessage(message, !deleted);
        }
    }

    private static void drawCheck(float x, float y, float progress, int accent) {
        ImDrawList dl = ImGui.getWindowDrawList();
        dl.addRectFilled(x, y, x + 16.0f, y + 16.0f, -13226454, 4.0f);
        dl.addRect(x + 0.5f, y + 0.5f, x + 16.0f - 0.5f, y + 16.0f - 0.5f, AnubisTheme.lerpColor(0x1AFFFFFF, accent, progress), 4.0f);
        if (progress > 0.01f) {
            dl.addLine(x + 4.0f, y + 8.0f, x + 7.0f, y + 11.0f, accent, 2.0f * progress);
            dl.addLine(x + 7.0f, y + 11.0f, x + 12.0f, y + 5.0f, accent, 2.0f * progress);
        }
    }

    static float smooth(float from, float to, float dt, float speed) {
        return from + (to - from) * (1.0f - (float)Math.exp(-speed * Math.max(0.0f, dt)));
    }

    public static float animationDelta(float dt) {
        float speed = GuiPalette.animationSpeed();
        return speed <= 0.0f ? 1.0f : dt * speed;
    }

    public static float elapsed(long since, float durationMs) {
        float speed = GuiPalette.animationSpeed();
        if (speed <= 0.0f) {
            return 1.0f;
        }
        return Math.min(1.0f, (float)(System.nanoTime() - since) * speed / (durationMs * 1000000.0f));
    }

    public static float easeOut(float t) {
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv;
    }

    private void renderKeybindRow(ClientModule module, float pad, float contentW) {
        boolean isListening;
        float rowH = AnubisWidgets.s(46.0f);
        float startY = this.rowStart(pad, contentW);
        boolean bl = isListening = this.listeningModule == module;
        if (isListening) {
            if (this.listeningFirstFrame) {
                this.listeningFirstFrame = false;
            } else {
                int pressed = ClickGuiRenderer.scanPressedKey();
                if (pressed == 256) {
                    module.setKeybind(-1);
                    this.listeningModule = null;
                    isListening = false;
                } else if (pressed != -1) {
                    module.setKeybind(pressed);
                    this.listeningModule = null;
                    isListening = false;
                }
            }
        }
        ClickGuiRenderer.rowLabel("Key", pad, startY, rowH);
        float chipH = AnubisWidgets.s(30.0f);
        float keyW = AnubisWidgets.s(96.0f);
        float modeW = AnubisWidgets.s(76.0f);
        float gap = AnubisWidgets.s(8.0f);
        float chipY = startY + (rowH - chipH) * 0.5f;
        if (!module.isAction()) {
            ImGui.setCursorPos((float)(pad + contentW - keyW - gap - modeW), (float)chipY);
            if (AnubisWidgets.chipButton("##bindmode", module.isHoldMode() ? "Hold" : "Toggle", modeW, chipH, false)) {
                module.setHoldMode(!module.isHoldMode());
            }
        }
        String label = isListening ? "Press a key..." : (module.keybind() == -1 ? "None" : ClickGuiRenderer.keyName(module.keybind()));
        ImGui.setCursorPos((float)(pad + contentW - keyW), (float)chipY);
        if (AnubisWidgets.chipButton("##bind", label, keyW, chipH, isListening)) {
            if (isListening) {
                this.listeningModule = null;
            } else {
                this.listeningModule = module;
                this.listeningFirstFrame = true;
                this.listeningKeySetting = null;
                this.listeningKeyFirstFrame = false;
            }
        }
        ImGui.setCursorPos((float)0.0f, (float)(startY + rowH));
    }

    public boolean capturesKeys() {
        return this.listeningModule != null || this.listeningKeySetting != null || ImGuiManager.getInstance().typing();
    }

    public boolean handleEscape() {
        if (this.listeningKeySetting != null) {
            this.listeningKeySetting.setValue(-1);
            this.listeningKeySetting = null;
            this.listeningKeyFirstFrame = false;
            return true;
        }
        if (this.listeningModule != null) {
            this.listeningModule.setKeybind(-1);
            this.listeningModule = null;
            this.listeningFirstFrame = false;
            return true;
        }
        if (this.blockLists.closePicker()) {
            return true;
        }
        if (this.itemPickers.closeWindow()) {
            return true;
        }
        if (this.settingsModule != null) {
            this.closeSettings();
            return true;
        }
        return false;
    }

    public static int scanPressedKey() {
        long handle = class_310.method_1551().method_22683().method_4490();
        for (int key = 32; key <= 348; ++key) {
            if (GLFW.glfwGetKey((long)handle, (int)key) != 1) continue;
            return key;
        }
        for (int button = 0; button <= 7; ++button) {
            if (GLFW.glfwGetMouseButton((long)handle, (int)button) != 1) continue;
            return 1000 + button;
        }
        return -1;
    }

    private String keyLabel(int key) {
        String label = (String)this.keyLabels.get(key);
        if (label == null) {
            label = ClickGuiRenderer.keyName(key);
            this.keyLabels.put(key, (Object)label);
        }
        return label;
    }

    public static String keyName(int key) {
        String name;
        if (key >= 1000) {
            int button = key - 1000;
            return switch (button) {
                case 0 -> "LMB";
                case 1 -> "RMB";
                case 2 -> "MMB";
                default -> "MB" + (button + 1);
            };
        }
        if ((key >= 39 && key <= 96 || key >= 161 && key <= 162) && (name = GLFW.glfwGetKeyName((int)key, (int)-1)) != null && !name.isBlank()) {
            return name.toUpperCase(Locale.ROOT);
        }
        return switch (key) {
            case 256 -> "ESC";
            case 257 -> "ENTER";
            case 335 -> "NUM ENTER";
            case 32 -> "SPACE";
            case 259 -> "BKSP";
            case 258 -> "TAB";
            case 340 -> "L SHIFT";
            case 344 -> "R SHIFT";
            case 341 -> "L CTRL";
            case 345 -> "R CTRL";
            case 342 -> "L ALT";
            case 346 -> "R ALT";
            case 265 -> "UP";
            case 264 -> "DOWN";
            case 263 -> "LEFT";
            case 262 -> "RIGHT";
            case 260 -> "INS";
            case 261 -> "DEL";
            case 268 -> "HOME";
            case 269 -> "END";
            case 266 -> "PGUP";
            case 267 -> "PGDN";
            case 290 -> "F1";
            case 291 -> "F2";
            case 292 -> "F3";
            case 293 -> "F4";
            case 294 -> "F5";
            case 295 -> "F6";
            case 296 -> "F7";
            case 297 -> "F8";
            case 298 -> "F9";
            case 299 -> "F10";
            case 300 -> "F11";
            case 301 -> "F12";
            default -> "K" + key;
        };
    }

    private void renderModuleConfigActions(ClientModule module, float pad, float contentW) {
        float gap = AnubisWidgets.s(8.0f);
        float h = AnubisWidgets.s(36.0f);
        float buttonW = (contentW - gap) * 0.5f;
        float y = ImGui.getCursorPosY() + AnubisWidgets.s(6.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##copy_config", "Copy Config", buttonW, h, true)) {
            this.copyModuleConfig(module);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Copy this module's settings as a short code");
        }
        String key = "paste:" + module.id();
        boolean confirming = this.isConfirming(key);
        ImGui.setCursorPos((float)(pad + buttonW + gap), (float)y);
        if (AnubisWidgets.chipButton("##paste_config", confirming ? "Confirm paste" : "Paste Config", buttonW, h, confirming) && this.confirmClick(key)) {
            this.pasteModuleConfig(module);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Replace this module's settings with a code from your clipboard");
        }
        y += h + AnubisWidgets.s(8.0f);
        ImFont small = ImGuiManager.getInstance().smallFont();
        if (module.id().equals(this.moduleCodeFor) && this.moduleCodeMessage != null && ClickGuiRenderer.elapsed(this.moduleCodeAt, 2500.0f) < 1.0f) {
            ImGui.setCursorPos((float)pad, (float)y);
            boolean pushed = ClickGuiRenderer.pushFont(small);
            ImGui.pushStyleColor((int)0, (int)(this.moduleCodeError ? -10856974 : -4542808));
            ImGui.textUnformatted((String)this.moduleCodeMessage);
            ImGui.popStyleColor();
            if (pushed) {
                ImGui.popFont();
            }
        }
        ImGui.setCursorPos((float)0.0f, (float)(y + AnubisWidgets.lineHeight(small)));
    }

    private void copyModuleConfig(ClientModule module) {
        String code = ModuleCodes.copy(module);
        class_310.method_1551().field_1774.method_1455(code);
        this.setModuleCodeMessage(module, "Copied, " + code.length() + " characters", false);
    }

    private void pasteModuleConfig(ClientModule module) {
        try {
            ModuleCodes.paste(module, class_310.method_1551().field_1774.method_1460());
            this.setModuleCodeMessage(module, "Config pasted", false);
        }
        catch (ProfileException e) {
            this.setModuleCodeMessage(module, e.getMessage(), true);
        }
    }

    private void setModuleCodeMessage(ClientModule module, String message, boolean error) {
        this.moduleCodeFor = module.id();
        this.moduleCodeMessage = message;
        this.moduleCodeError = error;
        this.moduleCodeAt = System.nanoTime();
    }

    private void renderHudActions(float pad, float contentW) {
        float gap = AnubisWidgets.s(8.0f);
        float h = AnubisWidgets.s(36.0f);
        float buttonW = (contentW - gap) * 0.5f;
        float y = ImGui.getCursorPosY() + AnubisWidgets.s(14.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##editlayout", "Edit Layout", buttonW, h, true)) {
            class_310.method_1551().method_1507((class_437)new HudEditorScreen());
        }
        ImGui.setCursorPos((float)(pad + buttonW + gap), (float)y);
        if (AnubisWidgets.chipButton("##resetlayout", "Reset Layout", buttonW, h, false)) {
            HudLayoutStore.getInstance().resetToDefaults(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
        }
        ImGui.setCursorPos((float)0.0f, (float)(y + h));
    }

    private static boolean isHudModule(ClientModule module) {
        return module.category() == ModuleCategory.CLIENT && "HUD".equals(module.name());
    }

    private static ClientModule hudModule() {
        for (ClientModule module : ModuleRegistry.all()) {
            if (!ClickGuiRenderer.isHudModule(module)) continue;
            return module;
        }
        return null;
    }

    private void renderHudStudio(float dt, float alpha, float view) {
        this.renderHudInspector(dt, alpha);
        this.highlightHudElements();
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        HudRenderer.getInstance().studioInput(sw, sh, ClickGuiRenderer.hudStudioBlocked());
    }

    private static boolean hudStudioBlocked() {
        return ImGui.isWindowHovered((int)4) || ImGui.isAnyItemActive() || ImGui.isPopupOpen((String)"", (int)3072);
    }

    private void renderHudInspector(float dt, float alpha) {
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float w = AnubisWidgets.s(360.0f);
        float h = Math.min(sh - AnubisWidgets.s(86.0f) - AnubisWidgets.s(40.0f), AnubisWidgets.s(760.0f));
        ImGui.setNextWindowPos((float)(sw - w - AnubisWidgets.s(24.0f)), (float)AnubisWidgets.s(24.0f), (int)4);
        ImGui.setNextWindowSize((float)w, (float)h, (int)1);
        if (this.focusHudInspector) {
            ImGui.setNextWindowFocus();
            this.focusHudInspector = false;
        }
        ImGui.pushStyleVar((int)0, (float)alpha);
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)ClickGuiRenderer.cardRounding());
        ImGui.pushStyleColor((int)2, (int)GuiPalette.abgr(ThemeStore.get().cards.color.argbWithAlpha(255)));
        int flags = 59;
        if (ImGui.begin((String)"##hud_inspector", (int)flags)) {
            float x = ImGui.getWindowPosX();
            float y = ImGui.getWindowPosY();
            ClickGuiRenderer.drawShadow(x, y, w, h, ClickGuiRenderer.cardRounding(), 6);
            ImGui.getWindowDrawList().addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), ClickGuiRenderer.cardRounding(), 0, 1.0f);
            float pad = AnubisWidgets.s(20.0f);
            float top = AnubisWidgets.s(20.0f);
            ClickGuiRenderer.drawCardTitle("HUD", pad, top + AnubisWidgets.s(14.0f));
            float ty = top + AnubisWidgets.s(34.0f);
            if (this.hudMessage != null && System.nanoTime() - this.hudMessageAt < 3000000000L) {
                ImGui.setCursorPos((float)pad, (float)ty);
                ImGui.pushTextWrapPos((float)(w - pad));
                ImGui.pushStyleColor((int)0, (int)(this.hudMessageError ? -10856974 : -461582));
                ImGui.textUnformatted((String)this.hudMessage);
                ImGui.popStyleColor();
                ImGui.popTextWrapPos();
                ty = ImGui.getCursorPosY();
            } else {
                ty = ClickGuiRenderer.mutedParagraph("Click an element on screen to edit it.", pad, ty, w - pad);
            }
            ty = this.renderHudElementTabs(pad, ty + AnubisWidgets.s(12.0f), w - pad * 2.0f, dt) + AnubisWidgets.s(14.0f);
            ClickGuiRenderer.cardDivider(pad, w, ty);
            ImGui.setCursorPos((float)0.0f, (float)(ty + 1.0f));
            if (ImGui.beginChild((String)"##hud_inspector_body", (float)w, (float)Math.max(1.0f, h - ty - 1.0f - AnubisWidgets.s(10.0f)), (boolean)false, (int)24)) {
                float contentW = w - pad * 2.0f;
                this.dividerPending = false;
                if (this.hudElementId == null) {
                    this.renderHudGeneralOptions(pad, contentW);
                } else {
                    this.renderHudElementOptions(this.hudElementId, pad, contentW);
                }
                ImGui.dummy((float)1.0f, (float)AnubisWidgets.s(18.0f));
                this.applySmoothScroll("hud_inspector", dt);
            }
            ImGui.endChild();
        }
        ImGui.end();
        ImGui.popStyleColor();
        ImGui.popStyleVar((int)4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private float renderHudElementTabs(float pad, float y, float contentW, float dt) {
        @Environment(value=EnvType.CLIENT)
        record Item(String id, String label) {
        }
        List<Item> items = List.of(new Item(null, "General"), new Item("info_bar", HudTheme.get().infoBar.label), new Item("coordinates", HudTheme.get().coordinates.label), new Item("module_list", HudTheme.get().moduleList.label), new Item("potion_tracker", HudTheme.get().potions.label), new Item("region_map", HudTheme.get().regionMap.label), new Item("anti_vanish", HudTheme.get().antiVanish.label), new Item("staff_list", HudTheme.get().staffList.label), new Item("spotify", HudTheme.get().spotify.label), new Item("hotkeys", HudTheme.get().hotkeys.label), new Item("radar", HudRenderer.editorLabel("radar")), new Item("target_card", HudTheme.get().targetCard.label), new Item("armor_status", HudTheme.get().armorStatus.label), new Item("item_cooldowns", HudTheme.get().itemCooldowns.label), new Item("toggle_toasts", HudTheme.get().toasts.label));
        ImFont small = ImGuiManager.getInstance().smallFont();
        boolean pushed = ClickGuiRenderer.pushFont(small);
        float chipH = AnubisWidgets.s(24.0f);
        float gap = AnubisWidgets.s(6.0f);
        float rowGap = AnubisWidgets.s(6.0f);
        float curX = pad;
        float curY = y;
        try {
            for (Item item : items) {
                boolean selected = Objects.equals(item.id(), this.hudElementId);
                float chipW = AnubisWidgets.textWidth(small, item.label()) + AnubisWidgets.s(18.0f);
                if (curX + chipW > pad + contentW && curX > pad) {
                    curX = pad;
                    curY += chipH + rowGap;
                }
                ImGui.setCursorPos((float)curX, (float)curY);
                if (AnubisWidgets.chipButton("##hudel_" + (item.id() == null ? "general" : item.id()), item.label(), chipW, chipH, selected)) {
                    this.selectHudElement(item.id());
                }
                curX += chipW + gap;
            }
        }
        finally {
            if (pushed) {
                ImGui.popFont();
            }
        }
        return curY + chipH;
    }

    private void renderHudGeneralOptions(float pad, float contentW) {
        ClientModule hud = ClickGuiRenderer.hudModule();
        if (hud != null) {
            this.sectionLabel("Settings", pad);
            this.settingRows(pad, contentW, hud.settings().toArray(new Setting[0]));
        }
        this.sectionLabel("Layout", pad);
        this.settingRows(pad, contentW, HudTheme.get().font, HudTheme.get().rounding);
        float y = ImGui.getCursorPosY() + AnubisWidgets.s(6.0f);
        float h = AnubisWidgets.s(34.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##resethudlayout", "Reset positions & sizes", contentW, h, false)) {
            HudLayoutStore.getInstance().resetToDefaults(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
            this.showHudMessage("HUD layout reset", false);
        }
        ImGui.setCursorPos((float)0.0f, (float)(y + h));
        this.sectionLabel("Share", pad);
        this.renderHudThemeActions(pad, contentW);
    }

    private void renderHudElementOptions(String id, float pad, float contentW) {
        Surface surface;
        RadarModule owner;
        RegionMapModule regionMap;
        HudModule hud;
        List<Setting> own;
        ClientModule clientModule = ClickGuiRenderer.hudModule();
        if (clientModule instanceof HudModule && !(own = (hud = (HudModule)clientModule).elementSettings(id)).isEmpty()) {
            this.sectionLabel(ClickGuiRenderer.hudOptionsTitle(id), pad);
            this.settingRows(pad, contentW, own.toArray(new Setting[0]));
        }
        if ("region_map".equals(id) && (regionMap = ModuleRegistry.get(RegionMapModule.class)) != null) {
            this.renderInlineModuleToggle(regionMap, pad, contentW);
            this.sectionLabel("Settings", pad);
            this.settingRows(pad, contentW, regionMap.settings().toArray(new Setting[0]));
        }
        ClientModule clientModule2 = "radar".equals(id) ? ModuleRegistry.get(RadarModule.class) : (owner = "hotkeys".equals(id) ? ModuleRegistry.get(HotkeysModule.class) : null);
        if (owner != null) {
            this.renderInlineModuleToggle(owner, pad, contentW);
            if (!owner.settings().isEmpty()) {
                this.sectionLabel("Settings", pad);
                this.settingRows(pad, contentW, owner.settings().toArray(new Setting[0]));
            }
        }
        if ((surface = HudTheme.get().forElementId(id)) != null) {
            this.renderSurfaceOptions(surface, pad, contentW);
        }
    }

    private static String hudOptionsTitle(String id) {
        return switch (id) {
            case "toggle_toasts" -> "Style";
            case "target_card", "armor_status", "item_cooldowns" -> "Options";
            default -> "Show";
        };
    }

    private void renderInlineModuleToggle(ClientModule module, float pad, float contentW) {
        float rowH = AnubisWidgets.s(46.0f);
        float startY = this.rowStart(pad, contentW);
        ClickGuiRenderer.rowLabel("Active", pad, startY, rowH);
        float swW = AnubisWidgets.s(40.0f);
        float swH = AnubisWidgets.s(22.0f);
        ImGui.setCursorPos((float)(pad + contentW - swW), (float)(startY + (rowH - swH) * 0.5f));
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        if (ImGui.invisibleButton((String)"##active", (float)swW, (float)swH)) {
            module.toggle();
        }
        AnubisWidgets.drawSwitch(ImGui.getWindowDrawList(), sx, sy, swW, swH, AnubisWidgets.animate(ImGui.getItemID(), module.isEnabled(), 16.0f));
        ImGui.setCursorPos((float)0.0f, (float)(startY + rowH));
    }

    private void renderHudThemeActions(float pad, float contentW) {
        HudTheme theme = HudTheme.get();
        float gap = AnubisWidgets.s(8.0f);
        float h = AnubisWidgets.s(34.0f);
        float buttonW = (contentW - gap * 2.0f) / 3.0f;
        float y = ImGui.getCursorPosY() + AnubisWidgets.s(6.0f);
        ImGui.setCursorPos((float)pad, (float)y);
        if (AnubisWidgets.chipButton("##copyhudtheme", "Copy theme", buttonW, h, false)) {
            class_310.method_1551().field_1774.method_1455(theme.exportString());
            this.showHudMessage("HUD theme copied to the clipboard", false);
        }
        ImGui.setCursorPos((float)(pad + buttonW + gap), (float)y);
        if (AnubisWidgets.chipButton("##pastehudtheme", "Paste theme", buttonW, h, false)) {
            boolean applied = theme.importString(class_310.method_1551().field_1774.method_1460());
            this.showHudMessage(applied ? "HUD theme applied from the clipboard" : "The clipboard doesn't contain an Anubis HUD theme", !applied);
        }
        ImGui.setCursorPos((float)(pad + (buttonW + gap) * 2.0f), (float)y);
        if (AnubisWidgets.chipButton("##resethudtheme", "Reset", buttonW, h, false)) {
            theme.resetToDefaults();
            this.showHudMessage("HUD theme reset to the defaults", false);
        }
        ImGui.setCursorPos((float)0.0f, (float)(y + h));
    }

    private void showHudMessage(String message, boolean error) {
        this.hudMessage = message;
        this.hudMessageError = error;
        this.hudMessageAt = System.nanoTime();
    }

    private void selectHudElement(String id) {
        if (Objects.equals(id, this.hudElementId)) {
            return;
        }
        this.hudElementId = id;
        this.focusHudInspector = true;
    }

    private void highlightHudElements() {
        HudLayoutStore.HudElementState selected;
        float mx = ImGui.getIO().getMousePosX();
        float my = ImGui.getIO().getMousePosY();
        boolean dragging = HudRenderer.getInstance().isDragging();
        HudLayoutStore.HudElementState hovered = null;
        if (!dragging && !ClickGuiRenderer.hudStudioBlocked()) {
            for (HudLayoutStore.HudElementState state : HudLayoutStore.getInstance().orderedElements()) {
                if (!state.positioned || !ClickGuiRenderer.contains(new float[]{state.x, state.y, state.width, state.height}, mx, my)) continue;
                hovered = state;
            }
        }
        ImDrawList under = ImGui.getBackgroundDrawList();
        if (this.hudElementId != null && (selected = ClickGuiRenderer.findHudElementState(this.hudElementId)) != null) {
            this.outlineHudElement(under, selected, AnubisWidgets.withAlpha(GuiPalette.accent(), 0.8f));
        }
        if (hovered == null) {
            return;
        }
        if (!hovered.id.equals(this.hudElementId)) {
            this.outlineHudElement(under, hovered, -2130706433);
        }
        ImDrawList fg = ImGui.getForegroundDrawList();
        Surface surface = HudTheme.get().forElementId(hovered.id);
        String label = surface != null ? surface.label : HudRenderer.editorLabel(hovered.id);
        String hint = label + "  \u00b7  click to edit, drag to move, scroll to resize";
        ImFont small = ImGuiManager.getInstance().smallFont();
        float chipW = AnubisWidgets.textWidth(small, hint) + AnubisWidgets.s(18.0f);
        float chipH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(10.0f);
        float cx = Math.min(mx + AnubisWidgets.s(14.0f), ImGui.getIO().getDisplaySizeX() - chipW - AnubisWidgets.s(4.0f));
        float cy = Math.min(my + AnubisWidgets.s(18.0f), ImGui.getIO().getDisplaySizeY() - chipH - AnubisWidgets.s(4.0f));
        fg.addRectFilled(cx, cy, cx + chipW, cy + chipH, GuiPalette.accent(), chipH * 0.5f);
        AnubisWidgets.text(fg, small, cx + AnubisWidgets.s(9.0f), cy + AnubisWidgets.s(5.0f), -1, hint);
        if (ImGui.isMouseClicked((int)0) || ImGui.isMouseClicked((int)1)) {
            this.selectHudElement(hovered.id);
        }
    }

    private void outlineHudElement(ImDrawList dl, HudLayoutStore.HudElementState state, int color) {
        float e = AnubisWidgets.s(3.0f);
        float rounding = HudRenderer.hudRounding() * HudRenderer.scaleOf(state);
        dl.addRect(state.x - e, state.y - e, state.x + state.width + e, state.y + state.height + e, color, rounding + e, 0, AnubisWidgets.s(1.5f));
    }

    private static HudLayoutStore.HudElementState findHudElementState(String id) {
        for (HudLayoutStore.HudElementState state : HudLayoutStore.getInstance().orderedElements()) {
            if (!state.id.equals(id)) continue;
            return state;
        }
        return null;
    }

    private void openColorPicker(ClientModule module, ColorSetting setting) {
        this.activeColorModule = module;
        this.activeColorSetting = setting;
        this.colorPickerOpen.set(true);
    }

    private void renderColorPickerWindow() {
        String owner;
        if (this.activeColorSetting == null) {
            return;
        }
        if (!this.colorPickerOpen.get()) {
            this.activeColorSetting = null;
            this.activeColorModule = null;
            return;
        }
        ImGui.setNextWindowSize((float)270.0f, (float)330.0f, (int)4);
        ImGui.setNextWindowPos((float)260.0f, (float)90.0f, (int)4);
        int flags = 352;
        String string = owner = this.activeColorModule != null ? this.activeColorModule.name() + " / " : "";
        if (ImGui.begin((String)("Color - " + owner + this.activeColorSetting.name() + "##color_picker"), (ImBoolean)this.colorPickerOpen, (int)flags)) {
            int pickerFlags = 135593984;
            if (ImGui.colorPicker4((String)"##picker", (float[])this.activeColorSetting.value(), (int)pickerFlags)) {
                this.activeColorSetting.notifyChanged();
            }
            ImGui.setNextItemWidth((float)240.0f);
            if (ImGui.colorEdit4((String)"RGBA", (float[])this.activeColorSetting.value(), (int)pickerFlags)) {
                this.activeColorSetting.notifyChanged();
            }
            if (ImGui.button((String)"Close", (float)80.0f, (float)22.0f)) {
                this.activeColorSetting.notifyChanged();
                this.colorPickerOpen.set(false);
            }
        }
        ImGui.end();
        if (!this.colorPickerOpen.get()) {
            this.activeColorSetting = null;
            this.activeColorModule = null;
        }
    }

    private void renderItemColorPaletteWindow() {
        if (!this.itemColorWindowOpen || this.activeItemColorPalette == null) {
            return;
        }
        float dt = Math.max(ImGui.getIO().getDeltaTime(), 0.001f);
        ImGui.setNextWindowSize((float)340.0f, (float)500.0f, (int)4);
        ImGui.setNextWindowPos((float)340.0f, (float)100.0f, (int)4);
        int wFlags = 288;
        boolean open = ImGui.begin((String)"Item Colors##icpalette", (int)wFlags);
        if (!open) {
            ImGui.end();
            if (this.activeItemColorPalette != null) {
                this.activeItemColorPalette.notifyChanged();
            }
            this.itemColorWindowOpen = false;
            return;
        }
        ImGui.text((String)"Default:");
        ImGui.sameLine();
        int defFlags = 131648;
        if (ImGui.colorButton((String)"##icdefcol", (float[])this.activeItemColorPalette.defaultColor(), (int)defFlags, (float)42.0f, (float)16.0f)) {
            this.editingItemDefaultColor = true;
            this.activeItemColorEntry = null;
            this.itemColorPickerOpen.set(true);
        }
        ImGui.separator();
        ImGui.text((String)"Search:");
        ImGui.sameLine();
        ImGui.setNextItemWidth((float)200.0f);
        ImGui.inputText((String)"##icsearch", (ImString)this.itemSearchBuf);
        String search = this.itemSearchBuf.get().toLowerCase().trim();
        ImGui.separator();
        ImGui.beginChild((String)"##iclist", (float)0.0f, (float)360.0f, (boolean)false, (int)0);
        for (ItemColorListSetting.ItemEntry entry : this.activeItemColorPalette.entries()) {
            boolean matches;
            boolean bl = matches = search.isEmpty() || entry.displayName().toLowerCase().contains(search) || entry.id().toLowerCase().contains(search);
            if (!matches) continue;
            float rowY = ImGui.getCursorPosY();
            float entryH = 22.0f;
            float availW = ImGui.getContentRegionAvailX();
            ImGui.setCursorPos((float)4.0f, (float)(rowY + (entryH - 16.0f) * 0.5f));
            ImGui.pushID((String)(entry.id() + "_ictog"));
            this.renderItemEntryToggle(this.activeItemColorPalette, entry, dt);
            ImGui.popID();
            float textH2 = ImGui.getTextLineHeight();
            ImGui.setCursorPos((float)24.0f, (float)(rowY + (entryH - textH2) * 0.5f));
            ImGui.textUnformatted((String)entry.displayName());
            float swatchX = availW - 46.0f;
            if (swatchX < 96.0f) {
                swatchX = 96.0f;
            }
            ImGui.setCursorPos((float)swatchX, (float)(rowY + (entryH - 16.0f) * 0.5f));
            int csFlags = 131648;
            ImGui.pushID((String)(entry.id() + "_iccol"));
            if (ImGui.colorButton((String)"##ic", (float[])entry.color(), (int)csFlags, (float)42.0f, (float)16.0f)) {
                this.activeItemColorEntry = entry;
                this.editingItemDefaultColor = false;
                this.itemColorPickerOpen.set(true);
            }
            ImGui.popID();
            ImGui.setCursorPosY((float)(rowY + entryH + 2.0f));
        }
        ImGui.endChild();
        ImGui.separator();
        if (ImGui.button((String)"Close##icclose", (float)80.0f, (float)22.0f)) {
            this.activeItemColorPalette.notifyChanged();
            this.itemColorWindowOpen = false;
            this.activeItemColorPalette = null;
        }
        ImGui.end();
        this.renderItemColorPicker();
    }

    private void renderItemColorPicker() {
        String label;
        float[] target;
        if (!this.itemColorPickerOpen.get()) {
            this.activeItemColorEntry = null;
            this.editingItemDefaultColor = false;
            return;
        }
        if (this.activeItemColorEntry == null && !this.editingItemDefaultColor) {
            this.itemColorPickerOpen.set(false);
            return;
        }
        if (this.editingItemDefaultColor && this.activeItemColorPalette != null) {
            target = this.activeItemColorPalette.defaultColor();
            label = "Default Color";
        } else {
            target = this.activeItemColorEntry.color();
            label = this.activeItemColorEntry.displayName();
        }
        ImGui.setNextWindowSize((float)270.0f, (float)330.0f, (int)4);
        ImGui.setNextWindowPos((float)700.0f, (float)100.0f, (int)4);
        int wFlags = 352;
        if (ImGui.begin((String)("Item Color - " + label), (ImBoolean)this.itemColorPickerOpen, (int)wFlags)) {
            int pickerFlags = 135593984;
            ImGui.colorPicker4((String)"##icpicker", (float[])target, (int)pickerFlags);
            ImGui.setNextItemWidth((float)240.0f);
            if (ImGui.colorEdit4((String)"RGBA##icce", (float[])target, (int)pickerFlags) && this.activeItemColorPalette != null) {
                this.activeItemColorPalette.notifyChanged();
            }
            if (ImGui.button((String)"Close##iccp", (float)80.0f, (float)22.0f)) {
                if (this.activeItemColorPalette != null) {
                    this.activeItemColorPalette.notifyChanged();
                }
                this.itemColorPickerOpen.set(false);
                this.activeItemColorEntry = null;
                this.editingItemDefaultColor = false;
            }
        }
        ImGui.end();
        if (!this.itemColorPickerOpen.get()) {
            this.activeItemColorEntry = null;
            this.editingItemDefaultColor = false;
        }
    }

    private void renderItemEntryToggle(ItemColorListSetting palette, ItemColorListSetting.ItemEntry entry, float dt) {
        float tw = 16.0f;
        float th = 16.0f;
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        if (ImGui.invisibleButton((String)"##ietog", (float)16.0f, (float)16.0f)) {
            entry.enabled().set(!entry.enabled().get());
            palette.notifyChanged();
        }
        float target = entry.enabled().get() ? 1.0f : 0.0f;
        float current = this.itemEntryTogAnim.getOrDefault(entry, Float.valueOf(target)).floatValue();
        current = ClickGuiRenderer.smooth(current, target, dt, 14.0f);
        this.itemEntryTogAnim.put(entry, Float.valueOf(current));
        ClickGuiRenderer.drawCheck(sx, sy, current, GuiPalette.accent());
    }

    private void renderSetting(ClientModule module, Setting setting, float pad, float contentW) {
        StringListSetting sls;
        StringSetting ss;
        StringSetting ss2;
        StringSetting ss3;
        float rowH = AnubisWidgets.s(46.0f);
        float startY = this.rowStart(pad, contentW);
        ImDrawList dl = ImGui.getWindowDrawList();
        if (setting instanceof BooleanSetting) {
            float hover;
            BooleanSetting bs = (BooleanSetting)setting;
            ImGui.setCursorPos((float)pad, (float)startY);
            float sx = ImGui.getCursorScreenPosX();
            float sy = ImGui.getCursorScreenPosY();
            if (ImGui.invisibleButton((String)"##row", (float)contentW, (float)rowH)) {
                bs.set(!bs.get());
            }
            if ((hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f)) > 0.01f) {
                dl.addRectFilled(sx - AnubisWidgets.s(10.0f), sy + AnubisWidgets.s(3.0f), sx + contentW + AnubisWidgets.s(10.0f), sy + rowH - AnubisWidgets.s(3.0f), AnubisWidgets.c(AnubisWidgets.withAlpha(0xAFFFFFF, hover)), AnubisWidgets.s(10.0f));
            }
            ClickGuiRenderer.rowLabel(setting.name(), pad, startY, rowH);
            float swW = AnubisWidgets.s(40.0f);
            float swH = AnubisWidgets.s(22.0f);
            AnubisWidgets.drawSwitch(dl, sx + contentW - swW, sy + (rowH - swH) * 0.5f, swW, swH, AnubisWidgets.animate(ImGui.getID((String)"##switch"), bs.get(), 16.0f));
        } else if (setting instanceof NumberSetting) {
            NumberSetting ns = (NumberSetting)setting;
            rowH = AnubisWidgets.s(64.0f);
            ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
            ImGui.textUnformatted((String)setting.name());
            ImFont small = ImGuiManager.getInstance().smallFont();
            String value = ns.formatted();
            float tw = AnubisWidgets.textWidth(small, value);
            float pillW = tw + AnubisWidgets.s(16.0f);
            float pillH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(8.0f);
            float wx = ImGui.getWindowPosX();
            float wy = ImGui.getWindowPosY() - ImGui.getScrollY();
            float px = wx + pad + contentW - pillW;
            float py = wy + startY + AnubisWidgets.s(8.0f);
            dl.addRectFilled(px, py, px + pillW, py + pillH, AnubisWidgets.c(-13226454), pillH * 0.5f);
            AnubisWidgets.text(dl, small, px + AnubisWidgets.s(8.0f), py + AnubisWidgets.s(4.0f), -461582, value);
            ImGui.setCursorPos((float)(pad - AnubisWidgets.s(7.0f)), (float)(startY + AnubisWidgets.s(36.0f)));
            if (AnubisWidgets.sliderFloat("##v", ns.value(), ns.min(), ns.max(), contentW + AnubisWidgets.s(14.0f), ns::snap)) {
                ns.notifyChanged();
            }
        } else if (setting instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting)setting;
            ClickGuiRenderer.rowLabel(setting.name(), pad, startY, rowH);
            float widest = 0.0f;
            for (String mode : ms.modes()) {
                widest = Math.max(widest, ImGui.calcTextSize((String)mode).x);
            }
            float dropW = Math.min(contentW * 0.6f, Math.max(AnubisWidgets.s(120.0f), widest + AnubisWidgets.s(44.0f)));
            float dropH = AnubisWidgets.s(30.0f);
            ImGui.setCursorPos((float)(pad + contentW - dropW), (float)(startY + (rowH - dropH) * 0.5f));
            int picked = AnubisWidgets.dropdown("##mode", ms.get(), ms.modes(), dropW, dropH);
            if (picked >= 0) {
                ms.set(ms.modes().get(picked));
            }
        } else if (setting instanceof ColorSetting) {
            ColorSetting cs = (ColorSetting)setting;
            ClickGuiRenderer.rowLabel(setting.name(), pad, startY, rowH);
            float swW = AnubisWidgets.s(44.0f);
            float swH = AnubisWidgets.s(24.0f);
            ImGui.setCursorPos((float)(pad + contentW - swW), (float)(startY + (rowH - swH) * 0.5f));
            float sx = ImGui.getCursorScreenPosX();
            float sy = ImGui.getCursorScreenPosY();
            if (ImGui.invisibleButton((String)"##color", (float)swW, (float)swH)) {
                this.openColorPicker(module, cs);
            }
            boolean hovered = ImGui.isItemHovered();
            float[] val = cs.value();
            int r = (int)(val[0] * 255.0f) & 0xFF;
            int g = (int)(val[1] * 255.0f) & 0xFF;
            int b = (int)(val[2] * 255.0f) & 0xFF;
            int a = val.length > 3 ? (int)(val[3] * 255.0f) & 0xFF : 255;
            dl.addRectFilled(sx, sy, sx + swW, sy + swH, AnubisWidgets.c(-13226454), AnubisWidgets.s(7.0f));
            dl.addRectFilled(sx, sy, sx + swW, sy + swH, AnubisWidgets.c(a << 24 | b << 16 | g << 8 | r), AnubisWidgets.s(7.0f));
            dl.addRect(sx, sy, sx + swW, sy + swH, AnubisWidgets.c(hovered ? 0x66FFFFFF : 0x26FFFFFF), AnubisWidgets.s(7.0f), 0, 1.0f);
            if (hovered) {
                ClickGuiRenderer.showTooltip(String.format("#%02X%02X%02X", r, g, b));
            }
        } else if (setting instanceof KeySetting) {
            boolean isListening;
            KeySetting ks = (KeySetting)setting;
            boolean bl = isListening = this.listeningKeySetting == ks;
            if (isListening) {
                if (this.listeningKeyFirstFrame) {
                    this.listeningKeyFirstFrame = false;
                } else {
                    int pressed = ClickGuiRenderer.scanPressedKey();
                    if (pressed == 256) {
                        ks.setValue(-1);
                        this.listeningKeySetting = null;
                        isListening = false;
                    } else if (pressed != -1) {
                        ks.setValue(pressed);
                        this.listeningKeySetting = null;
                        isListening = false;
                    }
                }
            }
            ClickGuiRenderer.rowLabel(setting.name(), pad, startY, rowH);
            float keyW = AnubisWidgets.s(96.0f);
            float chipH = AnubisWidgets.s(30.0f);
            String keyLabel = isListening ? "Press a key..." : (ks.value() == -1 ? "None" : ClickGuiRenderer.keyName(ks.value()));
            ImGui.setCursorPos((float)(pad + contentW - keyW), (float)(startY + (rowH - chipH) * 0.5f));
            if (AnubisWidgets.chipButton("##keyval", keyLabel, keyW, chipH, isListening)) {
                if (isListening) {
                    this.listeningKeySetting = null;
                } else {
                    this.listeningKeySetting = ks;
                    this.listeningKeyFirstFrame = true;
                    this.listeningModule = null;
                    this.listeningFirstFrame = false;
                }
            }
        } else if (setting instanceof StringSetting && !(ss3 = (StringSetting)setting).presets().isEmpty()) {
            rowH = this.itemPickers.renderPresets(module, ss3, pad, contentW, startY);
        } else if (setting instanceof StringSetting && (ss2 = (StringSetting)setting).isItemPicker()) {
            rowH = this.itemPickers.renderItem(module, ss2, pad, contentW, startY);
        } else if (setting instanceof StringSetting && (ss = (StringSetting)setting).enchantItem() != null) {
            rowH = this.itemPickers.renderEnchants(module, ss, pad, contentW, startY);
        } else if (setting instanceof StringSetting) {
            boolean edited;
            StringSetting ss4 = (StringSetting)setting;
            ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
            ImGui.textUnformatted((String)setting.name());
            ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(34.0f)));
            ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(12.0f), (float)AnubisWidgets.s(8.0f));
            ImGui.setNextItemWidth((float)contentW);
            boolean bl = edited = ss4.hint() != null ? ImGui.inputTextWithHint((String)"##str", (String)ss4.hint(), (ImString)ss4.getImString()) : ImGui.inputText((String)"##str", (ImString)ss4.getImString());
            if (edited) {
                ss4.notifyChanged();
            }
            ImGui.popStyleVar();
            rowH = AnubisWidgets.s(34.0f) + ImGui.getTextLineHeight() + AnubisWidgets.s(16.0f) + AnubisWidgets.s(12.0f);
        } else if (setting instanceof StringListSetting && (sls = (StringListSetting)setting).isItemPicker()) {
            rowH = this.itemPickers.renderList(module, sls, pad, contentW, startY);
        } else if (setting instanceof StringListSetting) {
            StringListSetting sls2 = (StringListSetting)setting;
            rowH = ClickGuiRenderer.renderStringList(sls2, pad, contentW, startY);
        } else if (setting instanceof ActionSetting) {
            ActionSetting actionSetting = (ActionSetting)setting;
            float h = AnubisWidgets.s(36.0f);
            ImGui.setCursorPos((float)pad, (float)(startY + (rowH + AnubisWidgets.s(6.0f) - h) * 0.5f));
            if (AnubisWidgets.chipButton("##action", setting.name(), contentW, h, false)) {
                actionSetting.run();
            }
            rowH += AnubisWidgets.s(6.0f);
        } else if (setting instanceof BlockColorListSetting) {
            BlockColorListSetting bcls = (BlockColorListSetting)setting;
            rowH = this.blockLists.render(module, bcls, pad, contentW, startY);
        } else if (setting instanceof ItemColorListSetting) {
            ItemColorListSetting icls = (ItemColorListSetting)setting;
            long enabled = icls.entries().stream().filter(en -> en.enabled().get()).count();
            if (ClickGuiRenderer.listChip(setting.name(), "Edit (" + enabled + ")", pad, contentW, startY, rowH)) {
                this.activeItemColorPalette = icls;
                this.itemColorWindowOpen = true;
                this.activeItemColorEntry = null;
                this.editingItemDefaultColor = false;
            }
        }
        if (!(setting instanceof ActionSetting)) {
            this.renderResetAffordance(setting, pad, contentW, startY, rowH);
        }
        ImGui.setCursorPos((float)0.0f, (float)(startY + rowH));
    }

    private void renderResetAffordance(Setting setting, float pad, float contentW, float startY, float rowH) {
        float labelCy;
        if (this.tab == Tab.THEME) {
            return;
        }
        boolean modified = !setting.isDefault();
        float wx = ImGui.getWindowPosX();
        float wy = ImGui.getWindowPosY() - ImGui.getScrollY();
        boolean topLabel = setting instanceof NumberSetting || setting instanceof StringSetting || setting instanceof StringListSetting || setting instanceof BlockColorListSetting;
        float f = labelCy = topLabel ? startY + AnubisWidgets.s(10.0f) + ImGui.getTextLineHeight() * 0.5f : startY + rowH * 0.5f;
        if (modified) {
            float dx = wx + pad - AnubisWidgets.s(11.0f);
            float dy = wy + labelCy;
            float r = AnubisWidgets.s(3.0f);
            ImGui.getWindowDrawList().addCircleFilled(dx, dy, r, AnubisWidgets.c(GuiPalette.accent()), 12);
            if (ImGui.isMouseHoveringRect((float)(dx - r * 2.0f), (float)(dy - r * 2.0f), (float)(dx + r * 2.0f), (float)(dy + r * 2.0f))) {
                ClickGuiRenderer.showTooltip("Modified - right-click the row to reset");
            }
        }
        float x0 = wx + pad - AnubisWidgets.s(10.0f);
        float y0 = wy + startY;
        if (ImGui.isWindowHovered() && ImGui.isMouseClicked((int)1) && ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + contentW + AnubisWidgets.s(20.0f)), (float)(y0 + rowH))) {
            ImGui.openPopup((String)"##setting_ctx");
        }
        ImGui.pushStyleVar((int)2, (float)AnubisWidgets.s(6.0f), (float)AnubisWidgets.s(6.0f));
        if (ImGui.beginPopup((String)"##setting_ctx")) {
            ImGui.textDisabled((String)setting.name());
            ImGui.separator();
            if (modified) {
                if (ImGui.selectable((String)"Reset to default")) {
                    setting.resetToDefault();
                }
            } else {
                ImGui.textDisabled((String)"Already at default");
            }
            ImGui.endPopup();
        }
        ImGui.popStyleVar();
    }

    private static float renderStringList(StringListSetting list, float pad, float contentW, float startY) {
        float fieldH;
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)list.name());
        float y = startY + AnubisWidgets.s(34.0f);
        float gap = AnubisWidgets.s(6.0f);
        float removeW = fieldH = ImGui.getTextLineHeight() + AnubisWidgets.s(16.0f);
        int remove = -1;
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(12.0f), (float)AnubisWidgets.s(8.0f));
        for (int i = 0; i < list.size(); ++i) {
            ImGui.pushID((int)i);
            ImGui.setCursorPos((float)pad, (float)y);
            ImGui.setNextItemWidth((float)(contentW - removeW - gap));
            if (ImGui.inputText((String)"##entry", (ImString)list.entry(i))) {
                list.edited();
            }
            ImGui.setCursorPos((float)(pad + contentW - removeW), (float)y);
            if (AnubisWidgets.chipButton("##remove", "x", removeW, fieldH, false)) {
                remove = i;
            }
            ImGui.popID();
            y += fieldH + gap;
        }
        ImGui.popStyleVar();
        if (remove >= 0) {
            list.remove(remove);
        }
        if (list.canAdd()) {
            float addH = AnubisWidgets.s(30.0f);
            ImGui.setCursorPos((float)pad, (float)y);
            if (AnubisWidgets.chipButton("##add", "+ Add", contentW, addH, false)) {
                list.add();
            }
            y += addH;
        }
        return y - startY + AnubisWidgets.s(12.0f);
    }

    private static boolean listChip(String label, String chip, float pad, float contentW, float startY, float rowH) {
        ClickGuiRenderer.rowLabel(label, pad, startY, rowH);
        float chipW = Math.max(AnubisWidgets.s(96.0f), ImGui.calcTextSize((String)chip).x + AnubisWidgets.s(28.0f));
        float chipH = AnubisWidgets.s(30.0f);
        ImGui.setCursorPos((float)(pad + contentW - chipW), (float)(startY + (rowH - chipH) * 0.5f));
        return AnubisWidgets.chipButton("##listedit", chip, chipW, chipH, false);
    }

    public static void applyTheme() {
        ImGui.pushStyleVar((int)3, (float)AnubisWidgets.s(10.0f));
        ImGui.pushStyleVar((int)12, (float)AnubisWidgets.s(8.0f));
        ImGui.pushStyleVar((int)9, (float)AnubisWidgets.s(10.0f));
        ImGui.pushStyleVar((int)21, (float)AnubisWidgets.s(6.0f));
        ImGui.pushStyleVar((int)2, (float)AnubisWidgets.s(10.0f), (float)AnubisWidgets.s(10.0f));
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(10.0f), (float)AnubisWidgets.s(6.0f));
        ImGui.pushStyleVar((int)14, (float)AnubisWidgets.s(8.0f), (float)AnubisWidgets.s(6.0f));
        ImGui.pushStyleVar((int)4, (float)0.0f);
        ImGui.pushStyleVar((int)10, (float)1.0f);
        ImGui.pushStyleVar((int)18, (float)AnubisWidgets.s(6.0f));
        ImGui.pushStyleVar((int)19, (float)AnubisWidgets.s(3.0f));
        for (int i = 0; i < THEME_COLORS.length; i += 2) {
            ImGui.pushStyleColor((int)THEME_COLORS[i], (int)THEME_COLORS[i + 1]);
        }
        int accent = GuiPalette.accent();
        int card = GuiPalette.cardBg();
        ImGui.pushStyleColor((int)2, (int)GuiPalette.panelBg());
        ImGui.pushStyleColor((int)10, (int)card);
        ImGui.pushStyleColor((int)11, (int)card);
        ImGui.pushStyleColor((int)12, (int)card);
        ImGui.pushStyleColor((int)18, (int)accent);
        ImGui.pushStyleColor((int)19, (int)accent);
        ImGui.pushStyleColor((int)20, (int)accent);
        ImGui.pushStyleColor((int)23, (int)accent);
        ImGui.pushStyleColor((int)26, (int)accent);
        ImGui.pushStyleColor((int)51, (int)AnubisWidgets.withAlpha(accent, 0.4f));
    }

    public static void removeTheme() {
        ImGui.popStyleColor((int)(THEME_COLORS.length / 2 + 10));
        ImGui.popStyleVar((int)11);
    }

    public static boolean pushFont(ImFont font) {
        if (font == null) {
            return false;
        }
        ImGui.pushFont((ImFont)font);
        return true;
    }

    private static /* synthetic */ float[] lambda$renderConfigCard$6(String k) {
        return new float[2];
    }

    static {
        SCROLLBAR_HOVERED = new ImBoolean();
        SCROLLBAR_HELD = new ImBoolean();
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Tab {
        MODULES("Modules"),
        HUD("HUD"),
        FRIENDS("Friends"),
        THEME("Theme"),
        CONFIG("Config");

        final String label;

        private Tab(String label) {
            this.label = label;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record CategoryPanel(ModuleCategory category, String id, String rowsId) {
    }

    @Environment(value=EnvType.CLIENT)
    private record PanelLayout(List<String> ids, List<String> keys, List<Float> heights) {
    }
}

