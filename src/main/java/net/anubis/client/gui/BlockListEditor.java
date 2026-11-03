/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  imgui.internal.ImGui
 *  imgui.internal.ImGuiWindow
 *  imgui.type.ImBoolean
 *  imgui.type.ImString
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2248
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_7923
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.internal.ImGuiWindow;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.BlockIcons;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.setting.BlockColorListSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
final class BlockListEditor {
    private static final int INLINE_MAX_ROWS = 8;
    private static final float HEADER_H = 40.0f;
    private static final float INLINE_ROW_H = 38.0f;
    private static final float LIST_PAD = 4.0f;
    private static final float EMPTY_H = 44.0f;
    private static final float PICKER_W = 820.0f;
    private static final float PICKER_MAX_H = 760.0f;
    private static final float PICKER_ROW_H = 42.0f;
    private static final float COLUMN_GAP = 28.0f;
    private static final float TAB_BAR_ROOM = 86.0f;
    private static final float ICON_SLOT = 32.0f;
    private static final float COLOR_CONTROLS_W = 88.0f;
    private static final float COLOR_COLUMN_EXTRA = 98.0f;
    private static final float COLOR_WINDOW_GAP = 12.0f;
    private static final float COLOR_WINDOW_W = 290.0f;
    private static final float COLOR_WINDOW_H = 360.0f;
    private static final int ADD_ALL_CONFIRM_OVER = 64;
    private static final long ARM_NANOS = 3000000000L;
    private static final int ARM_RESET = 1;
    private static final int ARM_CLEAR = 2;
    private static final int ARM_ADD = 3;
    private static final int LIST_BG = AnubisWidgets.withAlpha(-13226454, 0.5f);
    private static final int ROW_HOVER = 0x12FFFFFF;
    private static final int SCRIM = 0x73000000;
    private static final int TRACER_ACCENT = -12276993;
    private static final String SELECT_LABEL = "Select";
    private static final String EMPTY_TEXT = "No blocks yet";
    private static final String PICKER_TITLE = "Select Blocks";
    private static final String AVAILABLE_TITLE = "All blocks";
    private static final String CHOSEN_TITLE = "Selected";
    private static final String NO_MATCHES = "No blocks match";
    private static final String ALL_CHOSEN = "Every block is selected";
    private static final String NONE_CHOSEN = "Nothing selected yet";
    private static final String SEARCH_HINT = "Search blocks...";
    private static final String RESET_LABEL = "Reset";
    private static final String ADD_SHOWN_LABEL = "Add all shown";
    private static final String CLEAR_LABEL = "Clear all";
    private static final String CONFIRM_LABEL = "Confirm";
    private static final int PICKER_FLAGS = 8639;
    private static final int LEV_INSERT = 1;
    private static final int LEV_SUBSTITUTE = 8;
    private static final int LEV_DELETE = 8;
    private static final Reference2ObjectOpenHashMap<class_2248, BlockInfo> INFOS = new Reference2ObjectOpenHashMap();
    private static final Map<BlockColorListSetting, ListState> LISTS = new WeakHashMap<BlockColorListSetting, ListState>();
    private static String language;
    private static int iconsFrame;
    private static int namesGeneration;
    private static int[] levPrev;
    private static int[] levCur;
    private final Reference2ObjectOpenHashMap<BlockColorListSetting, float[]> inlineScroll = new Reference2ObjectOpenHashMap();
    private BlockColorListSetting picker;
    private ListState pickerList;
    private String pickerSubtitle;
    private final ImString search = new ImString(64);
    private boolean focusSearch;
    private boolean searchActive;
    private long pickerOpenedAt;
    private boolean pickerJustOpened;
    private final float[] availableScroll = new float[2];
    private final float[] chosenScroll = new float[2];
    private int armedAction;
    private BlockColorListSetting armedFor;
    private long armedAt;
    private BlockColorListSetting colorList;
    private BlockColorListSetting.BlockEntry colorEntry;
    private boolean colorTracer;
    private String colorTitle;
    private final ImBoolean colorOpen = new ImBoolean(false);
    private float colorAtX;
    private float colorAtY;
    private boolean colorFocus;

    BlockListEditor() {
    }

    float render(ClientModule module, BlockColorListSetting setting, float pad, float contentW, float startY) {
        BlockListEditor.beginFrame();
        ListState list = BlockListEditor.list(setting);
        BlockListEditor.refreshChosen(setting, list);
        boolean colors = !setting.colorsHidden();
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGuiManager.getInstance().moduleFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        float cardX0 = ImGui.getWindowPosX();
        float originY = ImGui.getWindowPosY() - ImGui.getScrollY();
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)setting.name());
        float labelCy = originY + startY + AnubisWidgets.s(10.0f) + ImGui.getTextLineHeight() * 0.5f;
        BlockListEditor.countPill(dl, small, ImGui.getItemRectMaxX() + AnubisWidgets.s(8.0f), labelCy, list.countPill);
        float chipW = AnubisWidgets.textWidth(font, SELECT_LABEL) + AnubisWidgets.s(28.0f);
        float chipH = AnubisWidgets.s(28.0f);
        float chipX = cardX0 + pad + contentW - chipW;
        ImGui.setCursorScreenPos((float)chipX, (float)(labelCy - chipH * 0.5f));
        if (AnubisWidgets.chipButton("##add_blocks", SELECT_LABEL, chipW, chipH, false)) {
            this.openPicker(module, setting, list);
        }
        if (colors) {
            float swW = AnubisWidgets.s(30.0f);
            float swH = AnubisWidgets.s(20.0f);
            if (BlockListEditor.swatch(dl, "##default_color", setting.defaultColor(), chipX - AnubisWidgets.s(10.0f) - swW, labelCy - swH * 0.5f, swW, swH)) {
                this.openColor(setting, null, false);
            }
            if (ImGui.isItemHovered((int)32768) && !this.editing(setting, null, false)) {
                ClickGuiRenderer.showTooltip("Default colour, for blocks not on the list");
            }
        }
        float listY = startY + AnubisWidgets.s(40.0f);
        float step = AnubisWidgets.s(38.0f);
        int n = list.chosenCount;
        float boxH = n == 0 ? AnubisWidgets.s(44.0f) : (float)Math.min(n, 8) * step + AnubisWidgets.s(4.0f) * 2.0f;
        float bx = cardX0 + pad;
        float by = originY + listY;
        float r = AnubisWidgets.s(10.0f);
        dl.addRectFilled(bx, by, bx + contentW, by + boxH, AnubisWidgets.c(LIST_BG), r);
        if (n == 0) {
            AnubisWidgets.text(dl, font, bx + AnubisWidgets.s(14.0f), by + (boxH - AnubisWidgets.lineHeight(font)) * 0.5f, -4542808, EMPTY_TEXT);
        } else if (n <= 8) {
            this.drawChosenRows(dl, setting, list, pad, listY + AnubisWidgets.s(4.0f), contentW, step, 0, n, colors, font, small);
        } else {
            ImGui.setCursorPos((float)pad, (float)(listY + AnubisWidgets.s(4.0f)));
            if (ImGui.beginChild((String)"##chosen", (float)contentW, (float)(boxH - AnubisWidgets.s(4.0f) * 2.0f), (boolean)false, (int)24)) {
                ImGui.setCursorPos((float)0.0f, (float)0.0f);
                ImGui.dummy((float)1.0f, (float)((float)n * step));
                float scroll = ImGui.getScrollY();
                int first = Math.max(0, (int)(scroll / step));
                int last = Math.min(n, (int)Math.ceil((scroll + ImGui.getWindowHeight()) / step));
                this.drawChosenRows(ImGui.getWindowDrawList(), setting, list, 0.0f, 0.0f, contentW, step, first, last, colors, font, small);
                ClickGuiRenderer.smoothScroll((float[])this.inlineScroll.computeIfAbsent((Object)setting, k -> new float[2]), BlockListEditor.frameDelta());
            }
            ImGui.endChild();
        }
        return AnubisWidgets.s(40.0f) + boxH + AnubisWidgets.s(12.0f);
    }

    private void drawChosenRows(ImDrawList dl, BlockColorListSetting setting, ListState list, float x, float y, float w, float step, int first, int last, boolean colors, ImFont font, ImFont small) {
        boolean windowHovered = ImGui.isWindowHovered();
        float icon = BlockIcons.size(AnubisWidgets.s(32.0f));
        float btn = AnubisWidgets.s(22.0f);
        float lineH = AnubisWidgets.lineHeight(font);
        float smallH = AnubisWidgets.lineHeight(small);
        Row removed = null;
        for (int i = first; i < last; ++i) {
            boolean hovered;
            Row row = list.chosen[i];
            ImGui.setCursorPos((float)x, (float)(y + (float)i * step));
            float x0 = ImGui.getCursorScreenPosX();
            float y0 = ImGui.getCursorScreenPosY();
            float cy = y0 + step * 0.5f;
            if (windowHovered && ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + w), (float)(y0 + step))) {
                dl.addRectFilled(x0 + AnubisWidgets.s(4.0f), y0 + AnubisWidgets.s(1.0f), x0 + w - AnubisWidgets.s(4.0f), y0 + step - AnubisWidgets.s(1.0f), AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(7.0f));
            }
            ImGui.pushID((int)row.info.rawId);
            float right = x0 + w - AnubisWidgets.s(10.0f);
            ImGui.setCursorScreenPos((float)(right - btn), (float)(cy - btn * 0.5f));
            if (ImGui.invisibleButton((String)"##remove", (float)btn, (float)btn)) {
                removed = row;
            }
            if (hovered = ImGui.isItemHovered()) {
                dl.addCircleFilled(right - btn * 0.5f, cy, btn * 0.5f, AnubisWidgets.c(-12240588), 20);
            }
            AnubisWidgets.cross(dl, right - btn * 0.5f, cy, AnubisWidgets.s(3.5f), hovered ? -10856974 : -4542808);
            right -= btn + AnubisWidgets.s(8.0f);
            if (colors) {
                right = this.colorControls(dl, setting, row, right, cy);
            }
            ImGui.popID();
            float ix = x0 + AnubisWidgets.s(8.0f);
            BlockIcons.draw(dl, row.info.block, ix, cy - icon * 0.5f, icon);
            float tx = ix + icon + AnubisWidgets.s(10.0f);
            Fit fit = BlockListEditor.fit(row.inlineFit, row.info, font, small, right - tx);
            AnubisWidgets.text(dl, font, tx, cy - lineH * 0.5f, -461582, fit.text);
            if (fit.hint == null) continue;
            AnubisWidgets.text(dl, small, tx + fit.textW + AnubisWidgets.s(6.0f), cy - smallH * 0.5f, -4542808, fit.hint);
        }
        if (removed != null) {
            BlockListEditor.deselect(setting, removed);
        }
    }

    private float colorControls(ImDrawList dl, BlockColorListSetting setting, Row row, float right, float cy) {
        float swW = AnubisWidgets.s(30.0f);
        float swH = AnubisWidgets.s(18.0f);
        float box = AnubisWidgets.s(16.0f);
        float gap = AnubisWidgets.s(6.0f);
        float x = right - swW;
        if (BlockListEditor.swatch(dl, "##tracer_color", row.entry.tracerColor(), x, cy - swH * 0.5f, swW, swH)) {
            this.openColor(setting, row, true);
        }
        if (ImGui.isItemHovered((int)32768) && !this.editing(setting, row.entry, true)) {
            ClickGuiRenderer.showTooltip("Tracer colour");
        }
        if (BlockListEditor.checkbox(dl, "##tracer", row.entry.tracerEnabled().get(), x -= gap + box, cy - box * 0.5f, box)) {
            row.entry.tracerEnabled().set(!row.entry.tracerEnabled().get());
            setting.notifyChanged();
        }
        if (ImGui.isItemHovered((int)32768)) {
            ClickGuiRenderer.showTooltip("Tracer");
        }
        if (BlockListEditor.swatch(dl, "##box_color", row.entry.color(), x -= gap + swW, cy - swH * 0.5f, swW, swH)) {
            this.openColor(setting, row, false);
        }
        if (ImGui.isItemHovered((int)32768) && !this.editing(setting, row.entry, false)) {
            ClickGuiRenderer.showTooltip("Box colour");
        }
        return x - AnubisWidgets.s(10.0f);
    }

    void renderWindows() {
        if (this.picker != null) {
            this.renderPicker();
        }
        if (this.colorList != null) {
            this.renderColorWindow();
        }
    }

    void close() {
        this.closePicker();
        this.closeColor();
    }

    boolean closePicker() {
        if (this.picker == null) {
            return false;
        }
        this.picker = null;
        this.pickerList = null;
        this.disarm();
        return true;
    }

    private void openPicker(ClientModule module, BlockColorListSetting setting, ListState list) {
        this.closeColor();
        this.picker = setting;
        this.pickerList = list;
        this.pickerSubtitle = module.name() + " / " + setting.name();
        this.search.set("");
        this.focusSearch = true;
        this.searchActive = false;
        this.pickerOpenedAt = System.nanoTime();
        this.pickerJustOpened = true;
        this.chosenScroll[1] = 0.0f;
        this.chosenScroll[0] = 0.0f;
        this.availableScroll[1] = 0.0f;
        this.availableScroll[0] = 0.0f;
        this.disarm();
    }

    private void renderPicker() {
        BlockListEditor.beginFrame();
        BlockColorListSetting setting = this.picker;
        ListState list = this.pickerList;
        BlockListEditor.refreshChosen(setting, list);
        boolean colors = !setting.colorsHidden();
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float margin = AnubisWidgets.s(16.0f);
        float extra = colors ? AnubisWidgets.s(98.0f) : 0.0f;
        float w = Math.min(sw - margin * 2.0f, AnubisWidgets.s(820.0f) + extra);
        float h = Math.min(sh - AnubisWidgets.s(86.0f) - AnubisWidgets.s(32.0f), AnubisWidgets.s(760.0f));
        float open = ClickGuiRenderer.easeOut(ClickGuiRenderer.elapsed(this.pickerOpenedAt, 200.0f));
        float x = Math.round((sw - w) * 0.5f);
        float y = Math.round(Math.max(margin, (sh - AnubisWidgets.s(86.0f) - h) * 0.5f + AnubisWidgets.s(8.0f)) + AnubisWidgets.s(14.0f) * (1.0f - open));
        float rounding = ClickGuiRenderer.cardRounding();
        ImGui.setNextWindowPos((float)0.0f, (float)0.0f, (int)1);
        ImGui.setNextWindowSize((float)sw, (float)sh, (int)1);
        if (this.pickerJustOpened) {
            ImGui.setNextWindowFocus();
        }
        ImGui.pushStyleVar((int)0, (float)Math.max(0.01f, open));
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)4, (float)0.0f);
        boolean closeClicked = false;
        boolean visible = ImGui.begin((String)"##block_picker", (int)8639);
        if (this.pickerJustOpened) {
            imgui.internal.ImGui.bringWindowToDisplayFront((ImGuiWindow)imgui.internal.ImGui.getCurrentWindow());
        }
        if (visible) {
            ImDrawList dl = ImGui.getWindowDrawList();
            dl.addRectFilled(0.0f, 0.0f, sw, sh, AnubisWidgets.c(0x73000000));
            BlockListEditor.shadow(dl, x, y, w, h, rounding);
            dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(GuiPalette.abgr(ThemeStore.get().cards.color.argbWithAlpha(255))), rounding);
            dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), rounding, 0, 1.0f);
            ImFont font = ImGuiManager.getInstance().moduleFont();
            ImFont small = ImGuiManager.getInstance().smallFont();
            float pad = AnubisWidgets.s(24.0f);
            float top = y + AnubisWidgets.s(22.0f);
            float btn = AnubisWidgets.s(28.0f);
            ClickGuiRenderer.drawCardTitle(PICKER_TITLE, x + pad, top + btn * 0.5f);
            ImGui.setCursorPos((float)(x + w - pad - btn), (float)top);
            if (AnubisWidgets.iconButton("##close", btn)) {
                closeClicked = true;
            }
            AnubisWidgets.cross(dl, x + w - pad - btn * 0.5f, top + btn * 0.5f, AnubisWidgets.s(4.5f), -461582);
            if (BlockListEditor.chip("##reset_blocks", RESET_LABEL, this.armed(1, setting), setting.isDefault(), x + w - pad - btn - AnubisWidgets.s(10.0f), top + btn * 0.5f, small, "Back to the default blocks") && this.confirm(1, setting)) {
                setting.resetToDefault();
            }
            float subY = top + btn + AnubisWidgets.s(2.0f);
            AnubisWidgets.text(dl, small, x + pad, subY, -4542808, this.pickerSubtitle);
            ImGui.setCursorPos((float)(x + pad), (float)(subY + AnubisWidgets.lineHeight(small) + AnubisWidgets.s(14.0f)));
            ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(12.0f), (float)AnubisWidgets.s(8.0f));
            ImGui.setNextItemWidth((float)(w - pad * 2.0f));
            if (this.focusSearch) {
                ImGui.setKeyboardFocusHere();
                this.focusSearch = false;
            }
            boolean enter = this.searchActive && (ImGui.isKeyPressed((int)525, (boolean)false) || ImGui.isKeyPressed((int)627, (boolean)false));
            ImGui.inputTextWithHint((String)"##block_search", (String)SEARCH_HINT, (ImString)this.search);
            this.searchActive = ImGui.isItemActive();
            ImGui.popStyleVar();
            float headerY = ImGui.getItemRectMaxY() + AnubisWidgets.s(18.0f);
            String ranked = list.searchKey;
            BlockListEditor.refreshSearch(list, this.search.get());
            if (list.searchKey != ranked) {
                this.chosenScroll[1] = 0.0f;
                this.chosenScroll[0] = 0.0f;
                this.availableScroll[1] = 0.0f;
                this.availableScroll[0] = 0.0f;
                if (this.armedAction == 3) {
                    this.disarm();
                }
            }
            BlockListEditor.refreshColumns(setting, list);
            if (enter && !list.searchKey.isBlank()) {
                if (list.availableCount > 0) {
                    BlockListEditor.select(setting, list.available[0]);
                }
                this.focusSearch = true;
            }
            float colW = (w - pad * 2.0f - AnubisWidgets.s(28.0f) - extra) * 0.5f;
            float rightW = colW + extra;
            float leftX = x + pad;
            float rightX = leftX + colW + AnubisWidgets.s(28.0f);
            float headerH = AnubisWidgets.s(28.0f);
            float listTop = headerY + headerH + AnubisWidgets.s(10.0f);
            float listBottom = y + h - AnubisWidgets.s(18.0f);
            if (list.availableLabelFor != list.availableCount) {
                list.availableLabelFor = list.availableCount;
                list.availableLabel = Integer.toString(list.availableCount);
            }
            if (list.shownLabelFor != list.shownCount) {
                list.shownLabelFor = list.shownCount;
                list.shownLabel = Integer.toString(list.shownCount);
            }
            BlockListEditor.columnHeader(dl, font, small, leftX, headerY, headerH, colW, AVAILABLE_TITLE, list.availableLabel);
            BlockListEditor.columnHeader(dl, font, small, rightX, headerY, headerH, rightW, CHOSEN_TITLE, list.shownLabel);
            float headerCy = headerY + headerH * 0.5f;
            boolean addShown = BlockListEditor.chip("##add_shown", ADD_SHOWN_LABEL, this.armed(3, setting), list.availableCount == 0, leftX + colW - AnubisWidgets.s(6.0f), headerCy, small, "Adds every block this column lists");
            boolean clearChosen = BlockListEditor.chip("##clear_all", CLEAR_LABEL, this.armed(2, setting), list.chosenCount == 0, rightX + rightW - AnubisWidgets.s(6.0f), headerCy, small, "Takes every block off the list");
            float dividerX = Math.round(leftX + colW + AnubisWidgets.s(28.0f) * 0.5f);
            dl.addLine(dividerX, headerY, dividerX, listBottom, AnubisWidgets.c(0xFFFFFFF), 1.0f);
            float listH = Math.max(1.0f, listBottom - listTop);
            String emptyLeft = list.searchKey.isBlank() ? ALL_CHOSEN : NO_MATCHES;
            String emptyRight = list.searchKey.isBlank() ? NONE_CHOSEN : NO_MATCHES;
            Row picked = this.column("##available", list.available, list.availableCount, true, null, leftX, listTop, colW, listH, this.availableScroll, emptyLeft, font, small);
            Row dropped = this.column("##chosen", list.shown, list.shownCount, false, colors ? setting : null, rightX, listTop, rightW, listH, this.chosenScroll, emptyRight, font, small);
            if (picked != null) {
                BlockListEditor.select(setting, picked);
            }
            if (dropped != null) {
                BlockListEditor.deselect(setting, dropped);
            }
            if (addShown) {
                this.addAllShown(setting, list);
            }
            if (clearChosen) {
                this.clearAll(setting);
            }
            if (!this.pickerJustOpened && ImGui.isMouseClicked((int)0) && ImGui.isWindowHovered() && !ImGui.isMouseHoveringRect((float)x, (float)y, (float)(x + w), (float)(y + h))) {
                closeClicked = true;
            }
        }
        ImGui.end();
        ImGui.popStyleVar((int)4);
        this.pickerJustOpened = false;
        if (closeClicked) {
            this.closePicker();
        }
    }

    private static boolean chip(String id, String label, boolean armed, boolean disabled, float right, float cy, ImFont small, String tooltip) {
        boolean pushed = ClickGuiRenderer.pushFont(small);
        float chipW = Math.max(AnubisWidgets.textWidth(small, label), AnubisWidgets.textWidth(small, CONFIRM_LABEL)) + AnubisWidgets.s(20.0f);
        float chipH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(12.0f);
        ImGui.setCursorPos((float)(right - chipW), (float)(cy - chipH * 0.5f));
        ImGui.beginDisabled((boolean)disabled);
        boolean clicked = AnubisWidgets.chipButton(id, armed ? CONFIRM_LABEL : label, chipW, chipH, armed);
        ImGui.endDisabled();
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip(tooltip);
        }
        if (pushed) {
            ImGui.popFont();
        }
        return clicked;
    }

    private boolean confirm(int action, BlockColorListSetting setting) {
        if (this.armed(action, setting)) {
            this.disarm();
            return true;
        }
        this.armedAction = action;
        this.armedFor = setting;
        this.armedAt = System.nanoTime();
        return false;
    }

    private boolean armed(int action, BlockColorListSetting setting) {
        return this.armedAction == action && this.armedFor == setting && System.nanoTime() - this.armedAt < 3000000000L;
    }

    private void disarm() {
        this.armedAction = 0;
        this.armedFor = null;
    }

    private static void columnHeader(ImDrawList dl, ImFont font, ImFont small, float x, float y, float h, float w, String title, String count) {
        float cy = y + h * 0.5f;
        AnubisWidgets.text(dl, font, x + AnubisWidgets.s(4.0f), cy - AnubisWidgets.lineHeight(font) * 0.5f, -461582, title);
        BlockListEditor.countPill(dl, small, x + AnubisWidgets.s(4.0f) + AnubisWidgets.textWidth(font, title) + AnubisWidgets.s(8.0f), cy, count);
        dl.addLine(x, y + h + AnubisWidgets.s(4.0f), x + w, y + h + AnubisWidgets.s(4.0f), AnubisWidgets.c(0xFFFFFFF), 1.0f);
    }

    private Row column(String id, Row[] rows, int count, boolean adding, BlockColorListSetting colors, float x, float y, float w, float h, float[] scroll, String emptyText, ImFont font, ImFont small) {
        Row clicked = null;
        ImGui.setCursorPos((float)x, (float)y);
        if (ImGui.beginChild((String)id, (float)w, (float)h, (boolean)false, (int)24)) {
            float step = AnubisWidgets.s(42.0f);
            float rowW = w - AnubisWidgets.s(6.0f);
            float rowH = step - AnubisWidgets.s(2.0f);
            ImGui.setCursorPos((float)0.0f, (float)0.0f);
            ImGui.dummy((float)1.0f, (float)((float)count * step + AnubisWidgets.s(6.0f)));
            ImDrawList dl = ImGui.getWindowDrawList();
            if (count == 0) {
                AnubisWidgets.text(dl, font, ImGui.getWindowPosX() + AnubisWidgets.s(8.0f), ImGui.getWindowPosY() + AnubisWidgets.s(12.0f), -4542808, emptyText);
            } else {
                float lineH = AnubisWidgets.lineHeight(font);
                float smallH = AnubisWidgets.lineHeight(small);
                float icon = BlockIcons.size(AnubisWidgets.s(32.0f));
                float btnR = AnubisWidgets.s(11.0f);
                float scrollY = ImGui.getScrollY();
                int first = Math.max(0, (int)(scrollY / step));
                int last = Math.min(count, (int)Math.ceil((scrollY + ImGui.getWindowHeight()) / step));
                int accent = GuiPalette.accent();
                boolean windowHovered = ImGui.isWindowHovered();
                for (int i = first; i < last; ++i) {
                    float textRight;
                    boolean hovered;
                    Row row = rows[i];
                    ImGui.setCursorPos((float)0.0f, (float)((float)i * step));
                    float x0 = ImGui.getCursorScreenPosX();
                    float y0 = ImGui.getCursorScreenPosY();
                    float cy = y0 + rowH * 0.5f;
                    float bx = x0 + rowW - AnubisWidgets.s(8.0f) - btnR;
                    ImGui.pushID((int)row.info.rawId);
                    if (colors == null) {
                        if (ImGui.invisibleButton((String)"##row", (float)rowW, (float)rowH)) {
                            clicked = row;
                        }
                        if (hovered = ImGui.isItemHovered()) {
                            dl.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(8.0f));
                        }
                        textRight = bx - btnR - AnubisWidgets.s(10.0f);
                    } else {
                        if (windowHovered && ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + rowW), (float)(y0 + rowH))) {
                            dl.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(8.0f));
                        }
                        float minusX = bx - btnR - AnubisWidgets.s(6.0f);
                        textRight = this.colorControls(dl, colors, row, minusX - AnubisWidgets.s(4.0f), cy);
                        ImGui.setCursorScreenPos((float)minusX, (float)y0);
                        if (ImGui.invisibleButton((String)"##minus", (float)(x0 + rowW - minusX), (float)rowH)) {
                            clicked = row;
                        }
                        hovered = ImGui.isItemHovered();
                        ImGui.setCursorScreenPos((float)x0, (float)y0);
                        if (ImGui.invisibleButton((String)"##row", (float)Math.max(1.0f, textRight + AnubisWidgets.s(4.0f) - x0), (float)rowH)) {
                            clicked = row;
                        }
                        hovered |= ImGui.isItemHovered();
                    }
                    ImGui.popID();
                    float ix = x0 + AnubisWidgets.s(6.0f);
                    BlockIcons.draw(dl, row.info.block, ix, cy - icon * 0.5f, icon);
                    float tx = ix + icon + AnubisWidgets.s(12.0f);
                    Fit fit = BlockListEditor.fit(row.pickerFit, row.info, font, small, textRight - tx);
                    AnubisWidgets.text(dl, font, tx, cy - lineH * 0.5f, hovered ? -461582 : -2042416, fit.text);
                    if (fit.hint != null) {
                        AnubisWidgets.text(dl, small, tx + fit.textW + AnubisWidgets.s(6.0f), cy - smallH * 0.5f, -4542808, fit.hint);
                    }
                    int fill = !hovered ? -13226454 : (adding ? accent : -10856974);
                    int glyph = hovered ? -1 : -4542808;
                    dl.addCircleFilled(bx, cy, btnR, AnubisWidgets.c(fill), 24);
                    dl.addLine(bx - btnR * 0.45f, cy, bx + btnR * 0.45f, cy, AnubisWidgets.c(glyph), AnubisWidgets.s(1.6f));
                    if (!adding) continue;
                    dl.addLine(bx, cy - btnR * 0.45f, bx, cy + btnR * 0.45f, AnubisWidgets.c(glyph), AnubisWidgets.s(1.6f));
                }
            }
            ClickGuiRenderer.smoothScroll(scroll, BlockListEditor.frameDelta());
        }
        ImGui.endChild();
        return clicked;
    }

    private void openColor(BlockColorListSetting setting, Row row, boolean tracer) {
        this.closeColor();
        this.colorList = setting;
        this.colorEntry = row == null ? null : row.entry;
        this.colorTracer = tracer;
        String label = row == null ? "Default Color" : (tracer ? row.info.name + " Tracer" : row.info.name);
        this.colorTitle = "Block Color - " + label + "###block_color";
        this.colorOpen.set(true);
        if (ImGui.isMousePosValid()) {
            this.colorAtX = ImGui.getIO().getMousePosX();
            this.colorAtY = ImGui.getIO().getMousePosY();
        } else {
            this.colorAtX = ImGui.getIO().getDisplaySizeX() * 0.5f;
            this.colorAtY = ImGui.getIO().getDisplaySizeY() * 0.5f;
        }
        this.colorFocus = true;
    }

    private void renderColorWindow() {
        int flags;
        float[] target = this.colorEntry == null ? this.colorList.defaultColor() : (this.colorTracer ? this.colorEntry.tracerColor() : this.colorEntry.color());
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        boolean leftOf = this.colorAtX + 12.0f + 290.0f > sw;
        boolean above = this.colorAtY + 12.0f + 360.0f > sh;
        ImGui.setNextWindowPos((float)(this.colorAtX + (leftOf ? -12.0f : 12.0f)), (float)(this.colorAtY + (above ? -12.0f : 12.0f)), (int)8, (float)(leftOf ? 1.0f : 0.0f), (float)(above ? 1.0f : 0.0f));
        ImGui.setNextWindowSize((float)270.0f, (float)330.0f, (int)4);
        if (this.colorFocus) {
            ImGui.setNextWindowFocus();
            this.colorFocus = false;
        }
        if (ImGui.begin((String)this.colorTitle, (ImBoolean)this.colorOpen, (int)(flags = 352))) {
            int pickerFlags = 135593984;
            if (ImGui.colorPicker4((String)"##bcpicker", (float[])target, (int)pickerFlags)) {
                this.colorList.notifyChanged();
            }
            ImGui.setNextItemWidth((float)240.0f);
            if (ImGui.colorEdit4((String)"RGBA##bcce", (float[])target, (int)pickerFlags)) {
                this.colorList.notifyChanged();
            }
            if (ImGui.button((String)"Close##bccp", (float)80.0f, (float)22.0f)) {
                this.colorOpen.set(false);
            }
        }
        ImGui.end();
        if (!this.colorOpen.get()) {
            this.closeColor();
        }
    }

    private boolean editing(BlockColorListSetting setting, BlockColorListSetting.BlockEntry entry, boolean tracer) {
        return this.colorList == setting && this.colorEntry == entry && this.colorTracer == tracer;
    }

    private void closeColor() {
        if (this.colorList == null) {
            return;
        }
        this.colorList.notifyChanged();
        this.colorList = null;
        this.colorEntry = null;
        this.colorOpen.set(false);
    }

    private static void select(BlockColorListSetting setting, Row row) {
        setting.select(row.entry);
        if (row.follower != null) {
            setting.select(row.follower.entry);
        }
    }

    private static void deselect(BlockColorListSetting setting, Row row) {
        setting.deselect(row.entry);
        if (row.follower != null) {
            setting.deselect(row.follower.entry);
        }
    }

    private void addAllShown(BlockColorListSetting setting, ListState list) {
        int n = list.availableCount;
        if (n == 0 || n > 64 && !this.confirm(3, setting)) {
            return;
        }
        ArrayList<BlockColorListSetting.BlockEntry> entries = new ArrayList<BlockColorListSetting.BlockEntry>(n + 8);
        for (int i = 0; i < n; ++i) {
            Row row = list.available[i];
            entries.add(row.entry);
            if (row.follower == null) continue;
            entries.add(row.follower.entry);
        }
        setting.selectAll(entries);
    }

    private void clearAll(BlockColorListSetting setting) {
        if (this.confirm(2, setting)) {
            setting.deselectAll();
        }
    }

    private static void beginFrame() {
        BlockListEditor.refreshNames();
        int frame = ImGuiManager.getInstance().frameCount();
        if (frame > iconsFrame + 1) {
            BlockIcons.forget();
        }
        iconsFrame = frame;
        BlockIcons.validate();
    }

    private static void refreshNames() {
        String selected = class_310.method_1551().method_1526().method_4669();
        if (selected.equals(language)) {
            return;
        }
        language = selected;
        if (INFOS.isEmpty()) {
            for (class_2248 block : class_7923.field_41175) {
                INFOS.put((Object)block, (Object)new BlockInfo(block));
            }
        }
        Object2IntOpenHashMap uses = new Object2IntOpenHashMap();
        for (BlockInfo info : INFOS.values()) {
            info.name = info.block.method_9518().getString();
            info.nameLower = info.name.toLowerCase(Locale.ROOT);
            if (info.wallBanner) continue;
            uses.addTo((Object)info.name, 1);
        }
        for (BlockInfo info : INFOS.values()) {
            info.hint = info.wallBanner || uses.getInt((Object)info.name) > 1 ? info.path : null;
        }
        ++namesGeneration;
    }

    private static ListState list(BlockColorListSetting setting) {
        ListState list = LISTS.get(setting);
        if (list == null) {
            list = new ListState(setting);
            LISTS.put(setting, list);
        }
        return list;
    }

    private static void refreshChosen(BlockColorListSetting setting, ListState list) {
        if (list.chosenRevision == setting.revision()) {
            return;
        }
        list.chosenRevision = setting.revision();
        List<BlockColorListSetting.BlockEntry> selected = setting.selected();
        if (list.chosen.length < selected.size()) {
            list.chosen = new Row[selected.size()];
        }
        int n = 0;
        for (int i = 0; i < selected.size(); ++i) {
            Row row = (Row)list.byEntry.get((Object)selected.get(i));
            if (row == null || row.leader != null && row.leader.entry.enabled().get()) continue;
            list.chosen[n++] = row;
        }
        list.chosenCount = n;
        if (list.labelsFor != n) {
            list.labelsFor = n;
            list.countPill = Integer.toString(n);
        }
    }

    private static void refreshSearch(ListState list, String raw) {
        if (raw == list.searchKey && list.searchGeneration == namesGeneration) {
            return;
        }
        list.searchKey = raw;
        list.searchGeneration = namesGeneration;
        Row[] rows = list.pickable;
        String filter = raw.trim().toLowerCase(Locale.ROOT);
        if (filter.isEmpty()) {
            for (int i = 0; i < rows.length; ++i) {
                list.results[i] = i;
            }
            list.resultCount = rows.length;
            return;
        }
        String[] words = filter.split(" ");
        long[] keys = list.sortKeys;
        int count = 0;
        for (int i = 0; i < rows.length; ++i) {
            int distance = BlockListEditor.matchDistance(rows[i].info, filter, words);
            if (distance < 0) continue;
            keys[count++] = (long)distance << 32 | (long)i;
        }
        Arrays.sort(keys, 0, count);
        for (int k = 0; k < count; ++k) {
            list.results[k] = (int)keys[k];
        }
        list.resultCount = count;
    }

    private static void refreshColumns(BlockColorListSetting setting, ListState list) {
        String filter;
        if (list.columnsRevision == setting.revision() && list.columnsKey == list.searchKey && list.columnsGeneration == namesGeneration) {
            return;
        }
        list.columnsRevision = setting.revision();
        list.columnsKey = list.searchKey;
        list.columnsGeneration = namesGeneration;
        int n = 0;
        for (int k = 0; k < list.resultCount; ++k) {
            Row row = list.pickable[list.results[k]];
            if (row.entry.enabled().get()) continue;
            list.available[n++] = row;
        }
        list.availableCount = n;
        if (list.shown.length < list.chosen.length) {
            list.shown = new Row[list.chosen.length];
            list.shownKeys = new long[list.chosen.length];
        }
        if ((filter = list.searchKey.trim().toLowerCase(Locale.ROOT)).isEmpty()) {
            System.arraycopy(list.chosen, 0, list.shown, 0, list.chosenCount);
            list.shownCount = list.chosenCount;
            return;
        }
        String[] words = filter.split(" ");
        long[] keys = list.shownKeys;
        int count = 0;
        for (int i = 0; i < list.chosenCount; ++i) {
            int distance = BlockListEditor.matchDistance(list.chosen[i].info, filter, words);
            if (distance < 0) continue;
            keys[count++] = (long)distance << 32 | (long)i;
        }
        Arrays.sort(keys, 0, count);
        for (int k = 0; k < count; ++k) {
            list.shown[k] = list.chosen[(int)keys[k]];
        }
        list.shownCount = count;
    }

    private static int matchDistance(BlockInfo info, String filter, String[] words) {
        int found = Math.max(BlockListEditor.wordsFound(info.nameLower, words), BlockListEditor.wordsFound(info.idLower, words));
        int nameDistance = BlockListEditor.levenshtein(filter, info.nameLower);
        int idDistance = BlockListEditor.levenshtein(filter, info.idLower);
        float relevancy = Math.max(1.0f - (float)nameDistance / (float)info.nameLower.length(), 1.0f - (float)idDistance / (float)info.idLower.length());
        return found > 0 || relevancy >= 0.5f ? Math.min(nameDistance, idDistance) : -1;
    }

    private static int wordsFound(String text, String[] words) {
        int found = 0;
        for (String word : words) {
            if (!text.contains(word)) {
                return 0;
            }
            if (word.isEmpty()) continue;
            int at = text.indexOf(word);
            while (at >= 0) {
                ++found;
                at = text.indexOf(word, at + word.length());
            }
        }
        return found;
    }

    private static int levenshtein(String from, String to) {
        int n = from.length();
        int m = to.length();
        if (n == 0) {
            return m * 1;
        }
        if (m == 0) {
            return n * 8;
        }
        if (levPrev.length <= m) {
            levPrev = new int[m + 1];
            levCur = new int[m + 1];
        }
        int[] prev = levPrev;
        int[] cur = levCur;
        for (int j = 0; j <= m; ++j) {
            prev[j] = j * 1;
        }
        for (int i = 1; i <= n; ++i) {
            cur[0] = i * 8;
            char typed = from.charAt(i - 1);
            for (int j = 1; j <= m; ++j) {
                int substitute = prev[j - 1] + (typed == to.charAt(j - 1) ? 0 : 8);
                cur[j] = Math.min(Math.min(prev[j] + 8, cur[j - 1] + 1), substitute);
            }
            int[] swap = prev;
            prev = cur;
            cur = swap;
        }
        return prev[m];
    }

    private static Fit fit(Fit fit, BlockInfo info, ImFont font, ImFont small, float avail) {
        if (fit.width == avail && fit.generation == namesGeneration) {
            return fit;
        }
        fit.width = avail;
        fit.generation = namesGeneration;
        fit.text = AnubisWidgets.ellipsize(font, info.name, avail);
        fit.textW = AnubisWidgets.textWidth(font, fit.text);
        float room = avail - fit.textW - AnubisWidgets.s(6.0f);
        fit.hint = info.hint != null && room >= AnubisWidgets.s(40.0f) ? AnubisWidgets.ellipsize(small, info.hint, room) : null;
        return fit;
    }

    private static void countPill(ImDrawList dl, ImFont small, float x, float cy, String count) {
        float w = AnubisWidgets.textWidth(small, count) + AnubisWidgets.s(14.0f);
        float h = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(6.0f);
        float y = cy - h * 0.5f;
        dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(-13226454), h * 0.5f);
        AnubisWidgets.text(dl, small, x + AnubisWidgets.s(7.0f), y + AnubisWidgets.s(3.0f), -4542808, count);
    }

    private static void shadow(ImDrawList dl, float x, float y, float w, float h, float rounding) {
        for (int i = 1; i <= 6; ++i) {
            float e = AnubisWidgets.s(2.5f) * (float)i;
            dl.addRectFilled(x - e, y - e + AnubisWidgets.s(4.0f), x + w + e, y + h + e + AnubisWidgets.s(4.0f), AnubisWidgets.c(0x14000000), rounding + e);
        }
    }

    private static boolean swatch(ImDrawList dl, String id, float[] rgba, float x, float y, float w, float h) {
        ImGui.setCursorScreenPos((float)x, (float)y);
        boolean clicked = ImGui.invisibleButton((String)id, (float)w, (float)h);
        boolean hovered = ImGui.isItemHovered();
        float r = AnubisWidgets.s(5.0f);
        dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(-13226454), r);
        dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(BlockListEditor.abgr(rgba)), r);
        dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(hovered ? 0x66FFFFFF : 0x26FFFFFF), r, 0, 1.0f);
        return clicked;
    }

    private static boolean checkbox(ImDrawList dl, String id, boolean on, float x, float y, float size) {
        ImGui.setCursorScreenPos((float)x, (float)y);
        boolean clicked = ImGui.invisibleButton((String)id, (float)size, (float)size);
        int border = on ? -12276993 : (ImGui.isItemHovered() ? 0x40FFFFFF : 0x1AFFFFFF);
        float r = AnubisWidgets.s(4.0f);
        dl.addRectFilled(x, y, x + size, y + size, AnubisWidgets.c(-13226454), r);
        dl.addRect(x + 0.5f, y + 0.5f, x + size - 0.5f, y + size - 0.5f, AnubisWidgets.c(border), r, 0, 1.0f);
        if (on) {
            AnubisWidgets.check(dl, x + size * 0.5f, y + size * 0.5f, size * 0.28f, -12276993);
        }
        return clicked;
    }

    private static int abgr(float[] rgba) {
        return BlockListEditor.unit(rgba[3]) << 24 | BlockListEditor.unit(rgba[2]) << 16 | BlockListEditor.unit(rgba[1]) << 8 | BlockListEditor.unit(rgba[0]);
    }

    private static int unit(float v) {
        return Math.round(Math.max(0.0f, Math.min(1.0f, v)) * 255.0f);
    }

    private static float frameDelta() {
        return ClickGuiRenderer.animationDelta(Math.max(ImGui.getIO().getDeltaTime(), 0.001f));
    }

    static {
        iconsFrame = -2;
        levPrev = new int[64];
        levCur = new int[64];
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ListState {
        final Row[] pickable;
        final Reference2ObjectOpenHashMap<BlockColorListSetting.BlockEntry, Row> byEntry = new Reference2ObjectOpenHashMap();
        Row[] chosen = new Row[0];
        int chosenCount;
        long chosenRevision = -1L;
        int labelsFor = -1;
        String countPill;
        final int[] results;
        final long[] sortKeys;
        int resultCount;
        String searchKey;
        int searchGeneration = -1;
        final Row[] available;
        int availableCount;
        Row[] shown = new Row[0];
        long[] shownKeys = new long[0];
        int shownCount;
        long columnsRevision = -1L;
        String columnsKey;
        int columnsGeneration = -1;
        int availableLabelFor = -1;
        int shownLabelFor = -1;
        String availableLabel;
        String shownLabel;

        ListState(BlockColorListSetting setting) {
            List<BlockColorListSetting.BlockEntry> entries = setting.entries();
            ArrayList<Row> rows = new ArrayList<Row>(entries.size());
            for (BlockColorListSetting.BlockEntry entry : entries) {
                BlockInfo info;
                class_2960 key = class_2960.method_12829((String)entry.id());
                class_2248 block = key == null ? null : (class_2248)class_7923.field_41175.method_17966(key).orElse(null);
                BlockInfo blockInfo = info = block == null ? null : (BlockInfo)INFOS.get((Object)block);
                if (info == null) continue;
                Row row = new Row(entry, info);
                rows.add(row);
                this.byEntry.put((Object)entry, (Object)row);
            }
            ArrayList<Row> offered = new ArrayList<Row>(rows.size());
            for (Row row : rows) {
                if (row.info.wallBannerId != null) {
                    BlockColorListSetting.BlockEntry wall = setting.entry(row.info.wallBannerId);
                    Row row2 = row.follower = wall == null ? null : (Row)this.byEntry.get((Object)wall);
                    if (row.follower != null) {
                        row.follower.leader = row;
                    }
                }
                if (row.info.wallBanner) continue;
                offered.add(row);
            }
            this.pickable = offered.toArray(new Row[0]);
            this.results = new int[this.pickable.length];
            this.sortKeys = new long[this.pickable.length];
            this.available = new Row[this.pickable.length];
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Row {
        final BlockColorListSetting.BlockEntry entry;
        final BlockInfo info;
        Row follower;
        Row leader;
        final Fit inlineFit = new Fit();
        final Fit pickerFit = new Fit();

        Row(BlockColorListSetting.BlockEntry entry, BlockInfo info) {
            this.entry = entry;
            this.info = info;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class BlockInfo {
        final class_2248 block;
        final int rawId;
        final String id;
        final String idLower;
        final String path;
        final boolean wallBanner;
        final String wallBannerId;
        String name;
        String nameLower;
        String hint;

        BlockInfo(class_2248 block) {
            this.block = block;
            this.rawId = class_7923.field_41175.method_10206((Object)block);
            class_2960 key = class_7923.field_41175.method_10221((Object)block);
            this.id = key.toString();
            this.idLower = this.id.toLowerCase(Locale.ROOT);
            this.path = key.method_12832();
            this.wallBanner = this.path.endsWith("_wall_banner");
            this.wallBannerId = !this.wallBanner && this.path.endsWith("_banner") ? key.method_12836() + ":" + this.path.substring(0, this.path.length() - "banner".length()) + "wall_banner" : null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Fit {
        String text;
        float textW;
        String hint;
        float width = -1.0f;
        int generation = -1;

        private Fit() {
        }
    }
}

