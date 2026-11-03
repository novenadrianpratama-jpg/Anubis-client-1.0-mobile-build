/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  imgui.internal.ImGui
 *  imgui.internal.ImGuiWindow
 *  imgui.type.ImString
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1887
 *  net.minecraft.class_1935
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_6880
 *  net.minecraft.class_7923
 *  net.minecraft.class_9334
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.internal.ImGuiWindow;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.ItemIcons;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.setting.Setting;
import net.anubis.client.module.setting.StringListSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.ItemEnchants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
final class ItemPickerEditor {
    private static final int INLINE_MAX_ROWS = 8;
    private static final float HEADER_H = 40.0f;
    private static final float INLINE_ROW_H = 38.0f;
    private static final float LIST_PAD = 4.0f;
    private static final float EMPTY_H = 44.0f;
    private static final float LIST_W = 820.0f;
    private static final float SINGLE_W = 560.0f;
    private static final float ENCHANT_W = 560.0f;
    private static final float PICKER_MAX_H = 760.0f;
    private static final float ENCHANT_MAX_H = 640.0f;
    private static final float PICKER_ROW_H = 42.0f;
    private static final float COLUMN_GAP = 28.0f;
    private static final float TAB_BAR_ROOM = 86.0f;
    private static final float ICON_SLOT = 32.0f;
    private static final float PRESET_ICON = 48.0f;
    private static final float PRESET_GAP = 8.0f;
    private static final int LIST_BG = AnubisWidgets.withAlpha(-13226454, 0.5f);
    private static final int ROW_HOVER = 0x12FFFFFF;
    private static final int SCRIM = 0x73000000;
    private static final String SELECT_LABEL = "Select";
    private static final String EDIT_LABEL = "Edit";
    private static final String NO_ITEMS = "No items yet";
    private static final String NO_ITEM = "No item selected";
    private static final String ANY_ENCHANTS = "Any enchantments";
    private static final String LIST_TITLE = "Select Items";
    private static final String SINGLE_TITLE = "Select Item";
    private static final String ENCHANT_TITLE = "Enchantments";
    private static final String AVAILABLE_TITLE = "All items";
    private static final String CHOSEN_TITLE = "Selected";
    private static final String NO_MATCHES = "No items match";
    private static final String NONE_CHOSEN = "Nothing selected yet";
    private static final String SEARCH_HINT = "Search items...";
    private static final String CLEAR_LABEL = "Clear";
    private static final String DONE_LABEL = "Done";
    private static final String TEXT_HINT = "text match";
    private static final String UNKNOWN_HINT = "unknown item";
    private static final String ENCHANT_HELP = "Pick what the item must have. Clashing enchantments turn off on their own.";
    private static final String NO_WORLD = "Join a world to load enchantments.";
    private static final String NO_ENCHANTS = "This item takes no enchantments.";
    private static final String FULL_LIST = "The list is full";
    private static final String OTHER_ITEMS = "Other items";
    private static final int PICKER_FLAGS = 8639;
    private static final int LEV_INSERT = 1;
    private static final int LEV_SUBSTITUTE = 8;
    private static final int LEV_DELETE = 8;
    private static final List<ItemInfo> ITEMS = new ArrayList<ItemInfo>();
    private static final Reference2ObjectOpenHashMap<class_1792, ItemInfo> BY_ITEM = new Reference2ObjectOpenHashMap();
    private static final Map<StringSetting.Preset, class_1799> PRESET_ICONS = new HashMap<StringSetting.Preset, class_1799>();
    private static String language;
    private static int namesGeneration;
    private static int[] levPrev;
    private static int[] levCur;
    private final Reference2ObjectOpenHashMap<StringListSetting, float[]> inlineScroll = new Reference2ObjectOpenHashMap();
    private final Reference2ObjectOpenHashMap<StringSetting, Summary> summaries = new Reference2ObjectOpenHashMap();
    private ClientModule pickerModule;
    private StringListSetting listTarget;
    private StringSetting itemTarget;
    private String pickerSubtitle;
    private final ImString search = new ImString(64);
    private boolean focusSearch;
    private boolean searchActive;
    private boolean pickerJustOpened;
    private long pickerOpenedAt;
    private final float[] availableScroll = new float[2];
    private final float[] chosenScroll = new float[2];
    private ItemInfo[] available = new ItemInfo[0];
    private ItemInfo[] chosen = new ItemInfo[0];
    private long[] sortKeys = new long[0];
    private int availableCount;
    private int chosenCount;
    private Object columnsTarget;
    private long columnsRevision = -1L;
    private String columnsKey;
    private int columnsGeneration = -1;
    private StringSetting enchantTarget;
    private class_1792 enchantItem;
    private String enchantSubtitle;
    private final List<EnchantRow> enchantRows = new ArrayList<EnchantRow>();
    private boolean enchantsLoaded;
    private long enchantOpenedAt;
    private boolean enchantJustOpened;
    private final float[] enchantScroll = new float[2];

    ItemPickerEditor() {
    }

    float renderList(ClientModule module, StringListSetting setting, float pad, float contentW, float startY) {
        ItemPickerEditor.beginFrame();
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGuiManager.getInstance().moduleFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        float cardX0 = ImGui.getWindowPosX();
        float originY = ImGui.getWindowPosY() - ImGui.getScrollY();
        List<String> values = setting.get();
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)setting.name());
        float labelCy = originY + startY + AnubisWidgets.s(10.0f) + ImGui.getTextLineHeight() * 0.5f;
        ItemPickerEditor.countPill(dl, small, ImGui.getItemRectMaxX() + AnubisWidgets.s(8.0f), labelCy, Integer.toString(values.size()));
        if (ItemPickerEditor.headerChip("##pick_items", SELECT_LABEL, cardX0 + pad + contentW, labelCy, font)) {
            this.openList(module, setting);
        }
        float listY = startY + AnubisWidgets.s(40.0f);
        float step = AnubisWidgets.s(38.0f);
        int n = values.size();
        float boxH = n == 0 ? AnubisWidgets.s(44.0f) : (float)Math.min(n, 8) * step + AnubisWidgets.s(4.0f) * 2.0f;
        float bx = cardX0 + pad;
        float by = originY + listY;
        dl.addRectFilled(bx, by, bx + contentW, by + boxH, AnubisWidgets.c(LIST_BG), AnubisWidgets.s(10.0f));
        String removed = null;
        if (n == 0) {
            AnubisWidgets.text(dl, font, bx + AnubisWidgets.s(14.0f), by + (boxH - AnubisWidgets.lineHeight(font)) * 0.5f, -4542808, NO_ITEMS);
        } else if (n <= 8) {
            removed = this.drawEntries(dl, values, pad, listY + AnubisWidgets.s(4.0f), contentW, step, 0, n, font, small);
        } else {
            ImGui.setCursorPos((float)pad, (float)(listY + AnubisWidgets.s(4.0f)));
            if (ImGui.beginChild((String)"##chosen_items", (float)contentW, (float)(boxH - AnubisWidgets.s(4.0f) * 2.0f), (boolean)false, (int)24)) {
                ImGui.setCursorPos((float)0.0f, (float)0.0f);
                ImGui.dummy((float)1.0f, (float)((float)n * step));
                float scroll = ImGui.getScrollY();
                int first = Math.max(0, (int)(scroll / step));
                int last = Math.min(n, (int)Math.ceil((scroll + ImGui.getWindowHeight()) / step));
                removed = this.drawEntries(ImGui.getWindowDrawList(), values, 0.0f, 0.0f, contentW, step, first, last, font, small);
                ClickGuiRenderer.smoothScroll((float[])this.inlineScroll.computeIfAbsent((Object)setting, k -> new float[2]), ItemPickerEditor.frameDelta());
            }
            ImGui.endChild();
        }
        if (removed != null) {
            setting.removeValue(removed);
        }
        return AnubisWidgets.s(40.0f) + boxH + AnubisWidgets.s(12.0f);
    }

    float renderItem(ClientModule module, StringSetting setting, float pad, float contentW, float startY) {
        boolean boxHovered;
        ItemPickerEditor.beginFrame();
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGuiManager.getInstance().moduleFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        float cardX0 = ImGui.getWindowPosX();
        float originY = ImGui.getWindowPosY() - ImGui.getScrollY();
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)setting.name());
        float labelCy = originY + startY + AnubisWidgets.s(10.0f) + ImGui.getTextLineHeight() * 0.5f;
        boolean open = ItemPickerEditor.headerChip("##pick_item", SELECT_LABEL, cardX0 + pad + contentW, labelCy, font);
        float boxY = startY + AnubisWidgets.s(40.0f);
        float boxH = AnubisWidgets.s(38.0f) + AnubisWidgets.s(4.0f) * 2.0f;
        float bx = cardX0 + pad;
        float by = originY + boxY;
        float cy = by + boxH * 0.5f;
        float lineH = AnubisWidgets.lineHeight(font);
        float smallH = AnubisWidgets.lineHeight(small);
        String raw = setting.get().trim();
        float btn = AnubisWidgets.s(22.0f);
        float right = bx + contentW - AnubisWidgets.s(10.0f);
        boolean clear = false;
        boolean clearHovered = false;
        if (!raw.isEmpty()) {
            ImGui.setCursorScreenPos((float)(right - btn), (float)(cy - btn * 0.5f));
            clear = ImGui.invisibleButton((String)"##clear_item", (float)btn, (float)btn);
            clearHovered = ImGui.isItemHovered();
        }
        float boxButtonW = raw.isEmpty() ? contentW : Math.max(1.0f, contentW - btn - AnubisWidgets.s(14.0f));
        ImGui.setCursorScreenPos((float)bx, (float)by);
        if (ImGui.invisibleButton((String)"##item_box", (float)boxButtonW, (float)boxH)) {
            open = true;
        }
        dl.addRectFilled(bx, by, bx + contentW, by + boxH, AnubisWidgets.c((boxHovered = ImGui.isItemHovered()) ? AnubisWidgets.withAlpha(-13226454, 0.8f) : LIST_BG), AnubisWidgets.s(10.0f));
        if (raw.isEmpty()) {
            AnubisWidgets.text(dl, font, bx + AnubisWidgets.s(14.0f), cy - lineH * 0.5f, -4542808, NO_ITEM);
        } else {
            if (clearHovered) {
                dl.addCircleFilled(right - btn * 0.5f, cy, btn * 0.5f, AnubisWidgets.c(-12240588), 20);
            }
            AnubisWidgets.cross(dl, right - btn * 0.5f, cy, AnubisWidgets.s(3.5f), clearHovered ? -10856974 : -4542808);
            float textRight = right - btn - AnubisWidgets.s(8.0f);
            class_1792 item = ItemEnchants.resolve(raw);
            float ix = bx + AnubisWidgets.s(8.0f);
            if (item != null) {
                ItemInfo info = ItemPickerEditor.info(item);
                float icon = ItemIcons.pixels(AnubisWidgets.s(32.0f));
                ItemIcons.draw(dl, item, ix, cy - icon * 0.5f, icon, 1.0f);
                float tx = ix + icon + AnubisWidgets.s(10.0f);
                ItemPickerEditor.drawLabel(dl, font, small, tx, cy, textRight - tx, info.name, info.stored, -461582, lineH, smallH);
            } else {
                ItemPickerEditor.drawLabel(dl, font, small, ix + AnubisWidgets.s(6.0f), cy, textRight - ix - AnubisWidgets.s(6.0f), raw, UNKNOWN_HINT, -10856974, lineH, smallH);
            }
        }
        if (clear) {
            setting.set("");
            if (setting.enchantSetting() != null) {
                setting.enchantSetting().set("");
            }
        } else if (open) {
            this.openItem(module, setting);
        }
        return AnubisWidgets.s(40.0f) + boxH + AnubisWidgets.s(12.0f);
    }

    float renderPresets(ClientModule module, StringSetting setting, float pad, float contentW, float startY) {
        ItemPickerEditor.beginFrame();
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGuiManager.getInstance().moduleFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        float cardX0 = ImGui.getWindowPosX();
        float originY = ImGui.getWindowPosY() - ImGui.getScrollY();
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)setting.name());
        List<StringSetting.Preset> presets = setting.presets();
        String value = setting.get().trim();
        String key = value.toLowerCase(Locale.ROOT).replace(' ', '_');
        StringSetting.Preset chosen = null;
        for (StringSetting.Preset preset : presets) {
            if (!preset.value().equalsIgnoreCase(key)) continue;
            chosen = preset;
        }
        int accent = GuiPalette.accent();
        float gap = AnubisWidgets.s(8.0f);
        float lineH = AnubisWidgets.lineHeight(font);
        float smallH = AnubisWidgets.lineHeight(small);
        float icon = ItemIcons.pixels(AnubisWidgets.s(48.0f));
        float tileW = (contentW - gap * (float)(presets.size() - 1)) / (float)presets.size();
        float tileH = icon + AnubisWidgets.s(30.0f) + smallH;
        float top = originY + startY + AnubisWidgets.s(40.0f);
        for (int i = 0; i < presets.size(); ++i) {
            StringSetting.Preset preset = presets.get(i);
            boolean selected = preset == chosen;
            float x0 = cardX0 + pad + (float)i * (tileW + gap);
            ImGui.setCursorScreenPos((float)x0, (float)top);
            ImGui.pushID((int)i);
            boolean clicked = ImGui.invisibleButton((String)"##preset", (float)tileW, (float)tileH);
            boolean hovered = ImGui.isItemHovered();
            ImGui.popID();
            ItemPickerEditor.presetTile(dl, x0, top, tileW, tileH, selected, hovered, accent);
            ItemIcons.draw(dl, ItemPickerEditor.presetIcon(preset), x0 + (tileW - icon) * 0.5f, top + AnubisWidgets.s(12.0f), icon, 1.0f);
            String shown = AnubisWidgets.ellipsize(small, preset.label(), tileW - AnubisWidgets.s(10.0f));
            float labelW = AnubisWidgets.textWidth(small, shown);
            AnubisWidgets.text(dl, small, x0 + (tileW - labelW) * 0.5f, top + tileH - AnubisWidgets.s(12.0f) - smallH, selected || hovered ? -461582 : -2042416, shown);
            if (hovered && !shown.equals(preset.label())) {
                ClickGuiRenderer.showTooltip(preset.label());
            }
            if (!clicked) continue;
            setting.set(preset.value());
        }
        boolean otherChosen = chosen == null && !value.isEmpty();
        class_1792 other = otherChosen ? ItemEnchants.resolve(value) : null;
        float rowY = top + tileH + gap;
        float rowH = AnubisWidgets.s(38.0f) + AnubisWidgets.s(4.0f) * 2.0f;
        float bx = cardX0 + pad;
        float cy = rowY + rowH * 0.5f;
        ImGui.setCursorScreenPos((float)bx, (float)rowY);
        boolean open = ImGui.invisibleButton((String)"##other_items", (float)contentW, (float)rowH);
        boolean hovered = ImGui.isItemHovered();
        ItemPickerEditor.presetTile(dl, bx, rowY, contentW, rowH, otherChosen, hovered, accent);
        float slot = ItemIcons.pixels(AnubisWidgets.s(32.0f));
        float ix = bx + AnubisWidgets.s(10.0f);
        float tx = ix + slot + AnubisWidgets.s(10.0f);
        float avail = bx + contentW - AnubisWidgets.s(14.0f) - tx;
        if (other != null) {
            ItemIcons.draw(dl, other, ix, cy - slot * 0.5f, slot, 1.0f);
            ItemPickerEditor.drawLabel(dl, font, small, tx, cy, avail, ItemPickerEditor.info((class_1792)other).name, OTHER_ITEMS, -461582, lineH, smallH);
        } else {
            float gx = ix + slot * 0.5f;
            float r = slot * 0.34f;
            int glyph = AnubisWidgets.c(hovered ? -461582 : -4542808);
            dl.addCircle(gx, cy, r, glyph, 24, AnubisWidgets.s(1.5f));
            dl.addLine(gx - r * 0.5f, cy, gx + r * 0.5f, cy, glyph, AnubisWidgets.s(1.5f));
            dl.addLine(gx, cy - r * 0.5f, gx, cy + r * 0.5f, glyph, AnubisWidgets.s(1.5f));
            ItemPickerEditor.drawLabel(dl, font, small, tx, cy, avail, otherChosen ? value : OTHER_ITEMS, otherChosen ? OTHER_ITEMS : null, hovered || otherChosen ? -461582 : -2042416, lineH, smallH);
        }
        if (open) {
            this.openItem(module, setting);
        }
        return AnubisWidgets.s(40.0f) + tileH + gap + rowH + AnubisWidgets.s(12.0f);
    }

    private static void presetTile(ImDrawList dl, float x, float y, float w, float h, boolean selected, boolean hovered, int accent) {
        int fill = selected ? AnubisWidgets.withAlpha(accent, 0.18f) : (hovered ? AnubisWidgets.withAlpha(-13226454, 0.8f) : LIST_BG);
        dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(fill), AnubisWidgets.s(10.0f));
        if (selected) {
            dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(AnubisWidgets.withAlpha(accent, 0.9f)), AnubisWidgets.s(10.0f), 0, AnubisWidgets.s(1.5f));
        }
    }

    private static class_1799 presetIcon(StringSetting.Preset preset) {
        class_1799 stack = PRESET_ICONS.get(preset);
        if (stack != null) {
            return stack;
        }
        class_1792 item = ItemEnchants.resolve(preset.icon());
        stack = new class_1799((class_1935)(item == null ? class_1802.field_8077 : item));
        if (preset.glint()) {
            stack.method_57379(class_9334.field_49641, (Object)true);
        }
        PRESET_ICONS.put(preset, stack);
        return stack;
    }

    float renderEnchants(ClientModule module, StringSetting setting, float pad, float contentW, float startY) {
        boolean hovered;
        ItemPickerEditor.beginFrame();
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGuiManager.getInstance().moduleFont();
        float cardX0 = ImGui.getWindowPosX();
        float originY = ImGui.getWindowPosY() - ImGui.getScrollY();
        ImGui.setCursorPos((float)pad, (float)(startY + AnubisWidgets.s(10.0f)));
        ImGui.textUnformatted((String)setting.name());
        float labelCy = originY + startY + AnubisWidgets.s(10.0f) + ImGui.getTextLineHeight() * 0.5f;
        boolean open = ItemPickerEditor.headerChip("##edit_enchants", EDIT_LABEL, cardX0 + pad + contentW, labelCy, font);
        float boxY = startY + AnubisWidgets.s(40.0f);
        float boxH = AnubisWidgets.s(38.0f) + AnubisWidgets.s(4.0f) * 2.0f;
        float bx = cardX0 + pad;
        float by = originY + boxY;
        ImGui.setCursorScreenPos((float)bx, (float)by);
        if (ImGui.invisibleButton((String)"##enchant_box", (float)contentW, (float)boxH)) {
            open = true;
        }
        dl.addRectFilled(bx, by, bx + contentW, by + boxH, AnubisWidgets.c((hovered = ImGui.isItemHovered()) ? AnubisWidgets.withAlpha(-13226454, 0.8f) : LIST_BG), AnubisWidgets.s(10.0f));
        String summary = this.summary(setting);
        float lineH = AnubisWidgets.lineHeight(font);
        float tx = bx + AnubisWidgets.s(14.0f);
        float avail = contentW - AnubisWidgets.s(28.0f);
        if (summary.isEmpty()) {
            AnubisWidgets.text(dl, font, tx, by + (boxH - lineH) * 0.5f, -4542808, ANY_ENCHANTS);
        } else {
            String shown = AnubisWidgets.ellipsize(font, summary, avail);
            AnubisWidgets.text(dl, font, tx, by + (boxH - lineH) * 0.5f, -461582, shown);
            if (hovered && !shown.equals(summary)) {
                ClickGuiRenderer.showTooltip(summary);
            }
        }
        if (open) {
            this.openEnchants(module, setting);
        }
        return AnubisWidgets.s(40.0f) + boxH + AnubisWidgets.s(12.0f);
    }

    void renderWindows() {
        if (this.listTarget != null || this.itemTarget != null) {
            this.renderPicker();
        } else if (this.enchantTarget != null) {
            this.renderEnchantWindow();
        }
    }

    void close() {
        this.closePicker();
        this.closeEnchants();
    }

    boolean closeWindow() {
        if (this.listTarget != null || this.itemTarget != null) {
            this.closePicker();
            return true;
        }
        if (this.enchantTarget != null) {
            this.closeEnchants();
            return true;
        }
        return false;
    }

    private String drawEntries(ImDrawList dl, List<String> values, float x, float y, float w, float step, int first, int last, ImFont font, ImFont small) {
        boolean windowHovered = ImGui.isWindowHovered();
        float icon = ItemIcons.pixels(AnubisWidgets.s(32.0f));
        float btn = AnubisWidgets.s(22.0f);
        float lineH = AnubisWidgets.lineHeight(font);
        float smallH = AnubisWidgets.lineHeight(small);
        String removed = null;
        for (int i = first; i < last; ++i) {
            boolean hovered;
            String value = values.get(i);
            ImGui.setCursorPos((float)x, (float)(y + (float)i * step));
            float x0 = ImGui.getCursorScreenPosX();
            float y0 = ImGui.getCursorScreenPosY();
            float cy = y0 + step * 0.5f;
            if (windowHovered && ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + w), (float)(y0 + step))) {
                dl.addRectFilled(x0 + AnubisWidgets.s(4.0f), y0 + AnubisWidgets.s(1.0f), x0 + w - AnubisWidgets.s(4.0f), y0 + step - AnubisWidgets.s(1.0f), AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(7.0f));
            }
            ImGui.pushID((int)i);
            float right = x0 + w - AnubisWidgets.s(10.0f);
            ImGui.setCursorScreenPos((float)(right - btn), (float)(cy - btn * 0.5f));
            if (ImGui.invisibleButton((String)"##remove", (float)btn, (float)btn)) {
                removed = value;
            }
            if (hovered = ImGui.isItemHovered()) {
                dl.addCircleFilled(right - btn * 0.5f, cy, btn * 0.5f, AnubisWidgets.c(-12240588), 20);
            }
            AnubisWidgets.cross(dl, right - btn * 0.5f, cy, AnubisWidgets.s(3.5f), hovered ? -10856974 : -4542808);
            ImGui.popID();
            right -= btn + AnubisWidgets.s(8.0f);
            float ix = x0 + AnubisWidgets.s(8.0f);
            class_1792 item = ItemEnchants.resolve(value);
            if (item != null) {
                ItemInfo info = ItemPickerEditor.info(item);
                ItemIcons.draw(dl, item, ix, cy - icon * 0.5f, icon, 1.0f);
                float tx = ix + icon + AnubisWidgets.s(10.0f);
                ItemPickerEditor.drawLabel(dl, font, small, tx, cy, right - tx, info.name, info.stored, -461582, lineH, smallH);
                continue;
            }
            float tx = ix + AnubisWidgets.s(6.0f);
            ItemPickerEditor.drawLabel(dl, font, small, tx, cy, right - tx, value, TEXT_HINT, -2042416, lineH, smallH);
        }
        return removed;
    }

    private static void drawLabel(ImDrawList dl, ImFont font, ImFont small, float x, float cy, float avail, String text, String hint, int color, float lineH, float smallH) {
        if (avail <= 0.0f) {
            return;
        }
        String shown = AnubisWidgets.ellipsize(font, text, avail);
        AnubisWidgets.text(dl, font, x, cy - lineH * 0.5f, color, shown);
        float used = AnubisWidgets.textWidth(font, shown);
        float room = avail - used - AnubisWidgets.s(6.0f);
        if (hint != null && room >= AnubisWidgets.s(40.0f)) {
            AnubisWidgets.text(dl, small, x + used + AnubisWidgets.s(6.0f), cy - smallH * 0.5f, -4542808, AnubisWidgets.ellipsize(small, hint, room));
        }
    }

    private void openList(ClientModule module, StringListSetting setting) {
        this.closeEnchants();
        this.listTarget = setting;
        this.itemTarget = null;
        this.startPicker(module, setting.name());
    }

    private void openItem(ClientModule module, StringSetting setting) {
        this.closeEnchants();
        this.itemTarget = setting;
        this.listTarget = null;
        this.startPicker(module, setting.name());
    }

    private void startPicker(ClientModule module, String settingName) {
        this.pickerModule = module;
        this.pickerSubtitle = module.name() + " / " + settingName;
        this.search.set("");
        this.focusSearch = true;
        this.searchActive = false;
        this.pickerOpenedAt = System.nanoTime();
        this.pickerJustOpened = true;
        this.chosenScroll[1] = 0.0f;
        this.chosenScroll[0] = 0.0f;
        this.availableScroll[1] = 0.0f;
        this.availableScroll[0] = 0.0f;
        this.columnsTarget = null;
    }

    private void closePicker() {
        this.listTarget = null;
        this.itemTarget = null;
        this.columnsTarget = null;
    }

    private void renderPicker() {
        ItemPickerEditor.beginFrame();
        boolean list = this.listTarget != null;
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float margin = AnubisWidgets.s(16.0f);
        float w = Math.min(sw - margin * 2.0f, AnubisWidgets.s(list ? 820.0f : 560.0f));
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
        ItemInfo picked = null;
        ItemInfo dropped = null;
        boolean visible = ImGui.begin((String)"##item_picker", (int)8639);
        if (this.pickerJustOpened) {
            imgui.internal.ImGui.bringWindowToDisplayFront((ImGuiWindow)imgui.internal.ImGui.getCurrentWindow());
        }
        if (visible) {
            String emptyLeft;
            ImDrawList dl = ImGui.getWindowDrawList();
            dl.addRectFilled(0.0f, 0.0f, sw, sh, AnubisWidgets.c(0x73000000));
            ItemPickerEditor.shadow(dl, x, y, w, h, rounding);
            dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(GuiPalette.abgr(ThemeStore.get().cards.color.argbWithAlpha(255))), rounding);
            dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), rounding, 0, 1.0f);
            ImFont font = ImGuiManager.getInstance().moduleFont();
            ImFont small = ImGuiManager.getInstance().smallFont();
            float pad = AnubisWidgets.s(24.0f);
            float top = y + AnubisWidgets.s(22.0f);
            float btn = AnubisWidgets.s(28.0f);
            ClickGuiRenderer.drawCardTitle(list ? LIST_TITLE : SINGLE_TITLE, x + pad, top + btn * 0.5f);
            ImGui.setCursorPos((float)(x + w - pad - btn), (float)top);
            if (AnubisWidgets.iconButton("##close", btn)) {
                closeClicked = true;
            }
            AnubisWidgets.cross(dl, x + w - pad - btn * 0.5f, top + btn * 0.5f, AnubisWidgets.s(4.5f), -461582);
            if (list && ItemPickerEditor.chip("##clear_items", CLEAR_LABEL, this.listTarget.get().isEmpty(), x + w - pad - btn - AnubisWidgets.s(10.0f), top + btn * 0.5f, small, "Takes every item off the list")) {
                this.listTarget.clear();
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
            ImGui.inputTextWithHint((String)"##item_search", (String)SEARCH_HINT, (ImString)this.search);
            this.searchActive = ImGui.isItemActive();
            ImGui.popStyleVar();
            float headerY = ImGui.getItemRectMaxY() + AnubisWidgets.s(18.0f);
            String before = this.columnsKey;
            this.refreshColumns();
            if (before != null && !before.equals(this.columnsKey)) {
                this.chosenScroll[1] = 0.0f;
                this.chosenScroll[0] = 0.0f;
                this.availableScroll[1] = 0.0f;
                this.availableScroll[0] = 0.0f;
            }
            if (enter && !this.search.get().isBlank() && this.availableCount > 0) {
                picked = this.available[0];
                this.focusSearch = list;
            }
            float headerH = AnubisWidgets.s(28.0f);
            float listTop = headerY + headerH + AnubisWidgets.s(10.0f);
            float listBottom = y + h - AnubisWidgets.s(18.0f);
            float listH = Math.max(1.0f, listBottom - listTop);
            String string = emptyLeft = this.search.get().isBlank() ? NONE_CHOSEN : NO_MATCHES;
            if (list) {
                float colW = (w - pad * 2.0f - AnubisWidgets.s(28.0f)) * 0.5f;
                float leftX = x + pad;
                float rightX = leftX + colW + AnubisWidgets.s(28.0f);
                ItemPickerEditor.columnHeader(dl, font, small, leftX, headerY, headerH, colW, AVAILABLE_TITLE, this.listTarget.canAdd() ? Integer.toString(this.availableCount) : FULL_LIST);
                ItemPickerEditor.columnHeader(dl, font, small, rightX, headerY, headerH, colW, CHOSEN_TITLE, Integer.toString(this.chosenCount));
                float dividerX = Math.round(leftX + colW + AnubisWidgets.s(28.0f) * 0.5f);
                dl.addLine(dividerX, headerY, dividerX, listBottom, AnubisWidgets.c(0xFFFFFFF), 1.0f);
                ItemInfo add = this.column("##available", this.available, this.availableCount, true, null, leftX, listTop, colW, listH, this.availableScroll, NO_MATCHES, font, small);
                ItemInfo remove = this.column("##chosen", this.chosen, this.chosenCount, false, null, rightX, listTop, colW, listH, this.chosenScroll, emptyLeft, font, small);
                if (add != null) {
                    picked = add;
                }
                if (remove != null) {
                    dropped = remove;
                }
            } else {
                float colW = w - pad * 2.0f;
                ItemPickerEditor.columnHeader(dl, font, small, x + pad, headerY, headerH, colW, AVAILABLE_TITLE, Integer.toString(this.availableCount));
                class_1792 current = ItemEnchants.resolve(this.itemTarget.get());
                ItemInfo choice = this.column("##single", this.available, this.availableCount, true, current, x + pad, listTop, colW, listH, this.availableScroll, NO_MATCHES, font, small);
                if (choice != null) {
                    picked = choice;
                }
            }
            if (!this.pickerJustOpened && ImGui.isMouseClicked((int)0) && ImGui.isWindowHovered() && !ImGui.isMouseHoveringRect((float)x, (float)y, (float)(x + w), (float)(y + h))) {
                closeClicked = true;
            }
        }
        ImGui.end();
        ImGui.popStyleVar((int)4);
        this.pickerJustOpened = false;
        if (dropped != null && this.listTarget != null) {
            ItemPickerEditor.removeFromList(this.listTarget, dropped.item);
        }
        if (picked != null) {
            if (this.listTarget != null) {
                this.listTarget.addValue(picked.stored);
            } else if (this.itemTarget != null) {
                this.pickItem(picked);
                return;
            }
        }
        if (closeClicked) {
            this.closePicker();
        }
    }

    private void pickItem(ItemInfo picked) {
        StringSetting setting = this.itemTarget;
        ClientModule module = this.pickerModule;
        class_1792 previous = ItemEnchants.resolve(setting.get());
        setting.set(picked.stored);
        this.closePicker();
        StringSetting enchants = setting.enchantSetting();
        if (enchants == null) {
            return;
        }
        if (previous != picked.item) {
            enchants.set("");
        }
        if (module != null && ItemEnchants.supports(picked.item)) {
            this.openEnchants(module, enchants);
        }
    }

    private static void removeFromList(StringListSetting setting, class_1792 item) {
        for (String value : setting.get()) {
            if (ItemEnchants.resolve(value) != item) continue;
            setting.removeValue(value);
            return;
        }
    }

    private ItemInfo column(String id, ItemInfo[] rows, int count, boolean adding, class_1792 current, float x, float y, float w, float h, float[] scroll, String emptyText, ImFont font, ImFont small) {
        ItemInfo clicked = null;
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
                float icon = ItemIcons.pixels(AnubisWidgets.s(32.0f));
                float btnR = AnubisWidgets.s(11.0f);
                float scrollY = ImGui.getScrollY();
                int first = Math.max(0, (int)(scrollY / step));
                int last = Math.min(count, (int)Math.ceil((scrollY + ImGui.getWindowHeight()) / step));
                int accent = GuiPalette.accent();
                for (int i = first; i < last; ++i) {
                    boolean selected;
                    ItemInfo row = rows[i];
                    ImGui.setCursorPos((float)0.0f, (float)((float)i * step));
                    float x0 = ImGui.getCursorScreenPosX();
                    float y0 = ImGui.getCursorScreenPosY();
                    float cy = y0 + rowH * 0.5f;
                    ImGui.pushID((int)row.rawId);
                    if (ImGui.invisibleButton((String)"##row", (float)rowW, (float)rowH)) {
                        clicked = row;
                    }
                    boolean hovered = ImGui.isItemHovered();
                    ImGui.popID();
                    boolean bl = selected = current != null && row.item == current;
                    if (selected) {
                        dl.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(accent, 0.18f)), AnubisWidgets.s(8.0f));
                    } else if (hovered) {
                        dl.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(8.0f));
                    }
                    float ix = x0 + AnubisWidgets.s(6.0f);
                    ItemIcons.draw(dl, row.item, ix, cy - icon * 0.5f, icon, 1.0f);
                    float bx = x0 + rowW - AnubisWidgets.s(8.0f) - btnR;
                    float tx = ix + icon + AnubisWidgets.s(12.0f);
                    ItemPickerEditor.drawLabel(dl, font, small, tx, cy, bx - btnR - AnubisWidgets.s(10.0f) - tx, row.name, row.stored, hovered || selected ? -461582 : -2042416, lineH, smallH);
                    if (selected) {
                        dl.addCircleFilled(bx, cy, btnR, AnubisWidgets.c(accent), 24);
                        AnubisWidgets.check(dl, bx, cy, btnR * 0.5f, -1);
                        continue;
                    }
                    int fill = !hovered ? -13226454 : (adding ? accent : -10856974);
                    int glyph = hovered ? -1 : -4542808;
                    dl.addCircleFilled(bx, cy, btnR, AnubisWidgets.c(fill), 24);
                    dl.addLine(bx - btnR * 0.45f, cy, bx + btnR * 0.45f, cy, AnubisWidgets.c(glyph), AnubisWidgets.s(1.6f));
                    if (!adding) continue;
                    dl.addLine(bx, cy - btnR * 0.45f, bx, cy + btnR * 0.45f, AnubisWidgets.c(glyph), AnubisWidgets.s(1.6f));
                }
            }
            ClickGuiRenderer.smoothScroll(scroll, ItemPickerEditor.frameDelta());
        }
        ImGui.endChild();
        return clicked;
    }

    private void refreshColumns() {
        Setting target = this.listTarget != null ? this.listTarget : this.itemTarget;
        long revision = this.listTarget != null ? this.listTarget.revision() : this.itemTarget.revision();
        String key = this.search.get();
        if (target == this.columnsTarget && revision == this.columnsRevision && key.equals(this.columnsKey) && this.columnsGeneration == namesGeneration) {
            return;
        }
        this.columnsTarget = target;
        this.columnsRevision = revision;
        this.columnsKey = key;
        this.columnsGeneration = namesGeneration;
        ReferenceOpenHashSet picked = new ReferenceOpenHashSet();
        if (this.listTarget != null) {
            for (String value : this.listTarget.get()) {
                class_1792 item = ItemEnchants.resolve(value);
                if (item == null) continue;
                picked.add((Object)item);
            }
        }
        String filter = key.trim().toLowerCase(Locale.ROOT);
        String[] words = filter.split(" ");
        int size = ITEMS.size();
        if (this.available.length < size) {
            this.available = new ItemInfo[size];
            this.chosen = new ItemInfo[size];
            this.sortKeys = new long[size];
        }
        int count = 0;
        for (int i = 0; i < size; ++i) {
            ItemInfo info = ITEMS.get(i);
            if (picked.contains((Object)info.item)) continue;
            if (filter.isEmpty()) {
                this.sortKeys[count++] = i;
                continue;
            }
            int distance = ItemPickerEditor.matchDistance(info, filter, words);
            if (distance < 0) continue;
            this.sortKeys[count++] = (long)distance << 32 | (long)i;
        }
        Arrays.sort(this.sortKeys, 0, count);
        for (int k = 0; k < count; ++k) {
            this.available[k] = ITEMS.get((int)this.sortKeys[k]);
        }
        this.availableCount = count;
        int shown = 0;
        if (this.listTarget != null) {
            for (String value : this.listTarget.get()) {
                class_1792 item = ItemEnchants.resolve(value);
                if (item == null) continue;
                ItemInfo info = ItemPickerEditor.info(item);
                if (!filter.isEmpty() && ItemPickerEditor.matchDistance(info, filter, words) < 0) continue;
                this.chosen[shown++] = info;
            }
        }
        this.chosenCount = shown;
    }

    private void openEnchants(ClientModule module, StringSetting setting) {
        this.closePicker();
        this.enchantTarget = setting;
        this.enchantItem = setting.enchantItem() == null ? null : ItemEnchants.resolve(setting.enchantItem().get());
        String itemName = this.enchantItem == null ? "No item" : ItemPickerEditor.info((class_1792)this.enchantItem).name;
        this.enchantSubtitle = module.name() + " / " + itemName;
        this.loadEnchantRows();
        this.enchantOpenedAt = System.nanoTime();
        this.enchantJustOpened = true;
        this.enchantScroll[1] = 0.0f;
        this.enchantScroll[0] = 0.0f;
    }

    private void closeEnchants() {
        this.enchantTarget = null;
        this.enchantItem = null;
        this.enchantRows.clear();
    }

    private void loadEnchantRows() {
        this.enchantRows.clear();
        boolean bl = this.enchantsLoaded = class_310.method_1551().field_1687 != null;
        if (!this.enchantsLoaded || this.enchantItem == null) {
            return;
        }
        Map<String, Integer> current = ItemEnchants.parse(this.enchantTarget.get());
        for (class_6880<class_1887> holder : ItemEnchants.applicable(this.enchantItem)) {
            EnchantRow row = new EnchantRow(holder);
            Integer level = current.get(row.key);
            if (level == null) {
                level = current.get(ItemEnchants.key(row.name));
            }
            if (level != null) {
                row.on = true;
                row.level = Math.max(1, Math.min(row.max, level));
            }
            this.enchantRows.add(row);
        }
    }

    private void writeEnchants() {
        ArrayList<Object> parts = new ArrayList<Object>();
        for (EnchantRow row : this.enchantRows) {
            if (!row.on) continue;
            parts.add(row.max > 1 ? row.path + " " + row.level : row.path);
        }
        this.enchantTarget.set(String.join((CharSequence)", ", parts));
    }

    private void toggle(EnchantRow row) {
        boolean bl = row.on = !row.on;
        if (row.on) {
            for (EnchantRow other : this.enchantRows) {
                if (other == row || !other.on || class_1887.method_60033(row.holder, other.holder)) continue;
                other.on = false;
            }
        }
        this.writeEnchants();
    }

    private void renderEnchantWindow() {
        ItemPickerEditor.beginFrame();
        float sw = ImGui.getIO().getDisplaySizeX();
        float sh = ImGui.getIO().getDisplaySizeY();
        float margin = AnubisWidgets.s(16.0f);
        float w = Math.min(sw - margin * 2.0f, AnubisWidgets.s(560.0f));
        float h = Math.min(sh - AnubisWidgets.s(86.0f) - AnubisWidgets.s(32.0f), AnubisWidgets.s(640.0f));
        float open = ClickGuiRenderer.easeOut(ClickGuiRenderer.elapsed(this.enchantOpenedAt, 200.0f));
        float x = Math.round((sw - w) * 0.5f);
        float y = Math.round(Math.max(margin, (sh - AnubisWidgets.s(86.0f) - h) * 0.5f + AnubisWidgets.s(8.0f)) + AnubisWidgets.s(14.0f) * (1.0f - open));
        float rounding = ClickGuiRenderer.cardRounding();
        ImGui.setNextWindowPos((float)0.0f, (float)0.0f, (int)1);
        ImGui.setNextWindowSize((float)sw, (float)sh, (int)1);
        if (this.enchantJustOpened) {
            ImGui.setNextWindowFocus();
        }
        ImGui.pushStyleVar((int)0, (float)Math.max(0.01f, open));
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)4, (float)0.0f);
        boolean closeClicked = false;
        EnchantRow toggled = null;
        boolean visible = ImGui.begin((String)"##enchant_picker", (int)8639);
        if (this.enchantJustOpened) {
            imgui.internal.ImGui.bringWindowToDisplayFront((ImGuiWindow)imgui.internal.ImGui.getCurrentWindow());
        }
        if (visible) {
            ImDrawList dl = ImGui.getWindowDrawList();
            dl.addRectFilled(0.0f, 0.0f, sw, sh, AnubisWidgets.c(0x73000000));
            ItemPickerEditor.shadow(dl, x, y, w, h, rounding);
            dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(GuiPalette.abgr(ThemeStore.get().cards.color.argbWithAlpha(255))), rounding);
            dl.addRect(x, y, x + w, y + h, AnubisWidgets.c(0x12FFFFFF), rounding, 0, 1.0f);
            ImFont font = ImGuiManager.getInstance().moduleFont();
            ImFont small = ImGuiManager.getInstance().smallFont();
            float pad = AnubisWidgets.s(24.0f);
            float top = y + AnubisWidgets.s(22.0f);
            float btn = AnubisWidgets.s(28.0f);
            ClickGuiRenderer.drawCardTitle(ENCHANT_TITLE, x + pad, top + btn * 0.5f);
            ImGui.setCursorPos((float)(x + w - pad - btn), (float)top);
            if (AnubisWidgets.iconButton("##close", btn)) {
                closeClicked = true;
            }
            AnubisWidgets.cross(dl, x + w - pad - btn * 0.5f, top + btn * 0.5f, AnubisWidgets.s(4.5f), -461582);
            float subY = top + btn + AnubisWidgets.s(2.0f);
            float smallH = AnubisWidgets.lineHeight(small);
            float lineH = AnubisWidgets.lineHeight(font);
            float subX = x + pad;
            if (this.enchantItem != null) {
                float icon = ItemIcons.pixels(AnubisWidgets.s(20.0f));
                ItemIcons.draw(dl, this.enchantItem, subX, subY + smallH * 0.5f - icon * 0.5f, icon, 1.0f);
                subX += icon + AnubisWidgets.s(8.0f);
            }
            AnubisWidgets.text(dl, small, subX, subY, -4542808, this.enchantSubtitle);
            float helpY = subY + smallH + AnubisWidgets.s(8.0f);
            AnubisWidgets.text(dl, small, x + pad, helpY, -4542808, AnubisWidgets.ellipsize(small, ENCHANT_HELP, w - pad * 2.0f));
            float footerH = AnubisWidgets.s(32.0f);
            float listTop = helpY + smallH + AnubisWidgets.s(14.0f);
            float listBottom = y + h - AnubisWidgets.s(20.0f) - footerH - AnubisWidgets.s(12.0f);
            float listH = Math.max(1.0f, listBottom - listTop);
            float colW = w - pad * 2.0f;
            dl.addLine(x + pad, listTop - AnubisWidgets.s(6.0f), x + pad + colW, listTop - AnubisWidgets.s(6.0f), AnubisWidgets.c(0xFFFFFFF), 1.0f);
            if (!this.enchantsLoaded || this.enchantRows.isEmpty()) {
                AnubisWidgets.text(dl, font, x + pad + AnubisWidgets.s(8.0f), listTop + AnubisWidgets.s(10.0f), -4542808, this.enchantsLoaded ? NO_ENCHANTS : NO_WORLD);
            } else {
                ImGui.setCursorPos((float)(x + pad), (float)listTop);
                if (ImGui.beginChild((String)"##enchant_rows", (float)colW, (float)listH, (boolean)false, (int)24)) {
                    float step = AnubisWidgets.s(42.0f);
                    float rowW = colW - AnubisWidgets.s(6.0f);
                    float rowH = step - AnubisWidgets.s(2.0f);
                    ImGui.setCursorPos((float)0.0f, (float)0.0f);
                    ImGui.dummy((float)1.0f, (float)((float)this.enchantRows.size() * step + AnubisWidgets.s(6.0f)));
                    ImDrawList rows = ImGui.getWindowDrawList();
                    int accent = GuiPalette.accent();
                    float scrollY = ImGui.getScrollY();
                    int first = Math.max(0, (int)(scrollY / step));
                    int last = Math.min(this.enchantRows.size(), (int)Math.ceil((scrollY + ImGui.getWindowHeight()) / step));
                    for (int i = first; i < last; ++i) {
                        EnchantRow row = this.enchantRows.get(i);
                        ImGui.setCursorPos((float)0.0f, (float)((float)i * step));
                        float x0 = ImGui.getCursorScreenPosX();
                        float y0 = ImGui.getCursorScreenPosY();
                        float cy = y0 + rowH * 0.5f;
                        float stepperW = row.max > 1 ? AnubisWidgets.s(112.0f) : 0.0f;
                        ImGui.pushID((int)i);
                        if (ImGui.invisibleButton((String)"##toggle", (float)Math.max(1.0f, rowW - stepperW), (float)rowH)) {
                            toggled = row;
                        }
                        boolean hovered = ImGui.isItemHovered();
                        if (row.on) {
                            rows.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(accent, 0.14f)), AnubisWidgets.s(8.0f));
                        } else if (hovered) {
                            rows.addRectFilled(x0, y0, x0 + rowW, y0 + rowH, AnubisWidgets.c(0x12FFFFFF), AnubisWidgets.s(8.0f));
                        }
                        float box = AnubisWidgets.s(18.0f);
                        float bx = x0 + AnubisWidgets.s(10.0f);
                        rows.addRectFilled(bx, cy - box * 0.5f, bx + box, cy + box * 0.5f, AnubisWidgets.c(-13226454), AnubisWidgets.s(4.0f));
                        rows.addRect(bx + 0.5f, cy - box * 0.5f + 0.5f, bx + box - 0.5f, cy + box * 0.5f - 0.5f, AnubisWidgets.c(row.on ? accent : (hovered ? 0x40FFFFFF : 0x1AFFFFFF)), AnubisWidgets.s(4.0f), 0, 1.0f);
                        if (row.on) {
                            AnubisWidgets.check(rows, bx + box * 0.5f, cy, box * 0.28f, accent);
                        }
                        float tx = bx + box + AnubisWidgets.s(12.0f);
                        String label = row.max > 1 ? row.name + "  (max " + ItemEnchants.roman(row.max) + ")" : row.name;
                        AnubisWidgets.text(rows, font, tx, cy - lineH * 0.5f, row.on || hovered ? -461582 : -2042416, AnubisWidgets.ellipsize(font, label, x0 + rowW - stepperW - AnubisWidgets.s(10.0f) - tx));
                        if (row.max > 1) {
                            float chipW = AnubisWidgets.s(28.0f);
                            float chipH = AnubisWidgets.s(26.0f);
                            float right = x0 + rowW - AnubisWidgets.s(8.0f);
                            ImGui.setCursorScreenPos((float)(right - chipW), (float)(cy - chipH * 0.5f));
                            if (AnubisWidgets.chipButton("##up", "+", chipW, chipH, false) && row.level < row.max) {
                                ++row.level;
                                if (!row.on) {
                                    toggled = row;
                                } else {
                                    this.writeEnchants();
                                }
                            }
                            String levelText = ItemEnchants.roman(row.level);
                            float levelW = AnubisWidgets.s(40.0f);
                            float lx = right - chipW - levelW;
                            AnubisWidgets.text(rows, font, lx + (levelW - AnubisWidgets.textWidth(font, levelText)) * 0.5f, cy - lineH * 0.5f, row.on ? -461582 : -4542808, levelText);
                            ImGui.setCursorScreenPos((float)(lx - chipW), (float)(cy - chipH * 0.5f));
                            if (AnubisWidgets.chipButton("##down", "-", chipW, chipH, false) && row.level > 1) {
                                --row.level;
                                if (!row.on) {
                                    toggled = row;
                                } else {
                                    this.writeEnchants();
                                }
                            }
                        }
                        ImGui.popID();
                    }
                    ClickGuiRenderer.smoothScroll(this.enchantScroll, ItemPickerEditor.frameDelta());
                }
                ImGui.endChild();
            }
            float footerY = y + h - AnubisWidgets.s(20.0f) - footerH;
            float doneW = AnubisWidgets.textWidth(font, DONE_LABEL) + AnubisWidgets.s(36.0f);
            ImGui.setCursorPos((float)(x + w - pad - doneW), (float)footerY);
            if (AnubisWidgets.chipButton("##done", DONE_LABEL, doneW, footerH, true)) {
                closeClicked = true;
            }
            float clearW = AnubisWidgets.textWidth(font, CLEAR_LABEL) + AnubisWidgets.s(36.0f);
            ImGui.setCursorPos((float)(x + pad), (float)footerY);
            boolean any = false;
            for (EnchantRow row : this.enchantRows) {
                any |= row.on;
            }
            ImGui.beginDisabled((!any ? 1 : 0) != 0);
            if (AnubisWidgets.chipButton("##clear_enchants", CLEAR_LABEL, clearW, footerH, false)) {
                for (EnchantRow row : this.enchantRows) {
                    row.on = false;
                }
                this.writeEnchants();
            }
            ImGui.endDisabled();
            if (!this.enchantJustOpened && ImGui.isMouseClicked((int)0) && ImGui.isWindowHovered() && !ImGui.isMouseHoveringRect((float)x, (float)y, (float)(x + w), (float)(y + h))) {
                closeClicked = true;
            }
        }
        ImGui.end();
        ImGui.popStyleVar((int)4);
        this.enchantJustOpened = false;
        if (toggled != null) {
            this.toggle(toggled);
        }
        if (closeClicked) {
            this.closeEnchants();
        }
    }

    private String summary(StringSetting setting) {
        Summary cached = (Summary)this.summaries.get((Object)setting);
        if (cached != null && cached.revision() == setting.revision() && cached.names() == namesGeneration) {
            return cached.text();
        }
        Map<String, Integer> parsed = ItemEnchants.parse(setting.get());
        ArrayList<Object> parts = new ArrayList<Object>();
        List<class_6880<class_1887>> all = ItemEnchants.all();
        for (Map.Entry<String, Integer> entry : parsed.entrySet()) {
            String name = null;
            int max = 0;
            for (class_6880<class_1887> holder : all) {
                if (!ItemEnchants.key(ItemEnchants.path(holder)).equals(entry.getKey())) continue;
                name = ((class_1887)holder.comp_349()).comp_2686().getString();
                max = ((class_1887)holder.comp_349()).method_8183();
                break;
            }
            if (name == null) {
                name = entry.getKey();
            }
            parts.add(max == 1 ? name : name + " " + ItemEnchants.roman(entry.getValue()));
        }
        String text = String.join((CharSequence)", ", parts);
        this.summaries.put((Object)setting, (Object)new Summary(setting.revision(), namesGeneration, text));
        return text;
    }

    private static boolean headerChip(String id, String label, float right, float cy, ImFont font) {
        float chipW = AnubisWidgets.textWidth(font, label) + AnubisWidgets.s(28.0f);
        float chipH = AnubisWidgets.s(28.0f);
        ImGui.setCursorScreenPos((float)(right - chipW), (float)(cy - chipH * 0.5f));
        return AnubisWidgets.chipButton(id, label, chipW, chipH, false);
    }

    private static boolean chip(String id, String label, boolean disabled, float right, float cy, ImFont small, String tooltip) {
        boolean pushed = ClickGuiRenderer.pushFont(small);
        float chipW = AnubisWidgets.textWidth(small, label) + AnubisWidgets.s(20.0f);
        float chipH = AnubisWidgets.lineHeight(small) + AnubisWidgets.s(12.0f);
        ImGui.setCursorPos((float)(right - chipW), (float)(cy - chipH * 0.5f));
        ImGui.beginDisabled((boolean)disabled);
        boolean clicked = AnubisWidgets.chipButton(id, label, chipW, chipH, false);
        ImGui.endDisabled();
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip(tooltip);
        }
        if (pushed) {
            ImGui.popFont();
        }
        return clicked;
    }

    private static void columnHeader(ImDrawList dl, ImFont font, ImFont small, float x, float y, float h, float w, String title, String count) {
        float cy = y + h * 0.5f;
        AnubisWidgets.text(dl, font, x + AnubisWidgets.s(4.0f), cy - AnubisWidgets.lineHeight(font) * 0.5f, -461582, title);
        ItemPickerEditor.countPill(dl, small, x + AnubisWidgets.s(4.0f) + AnubisWidgets.textWidth(font, title) + AnubisWidgets.s(8.0f), cy, count);
        dl.addLine(x, y + h + AnubisWidgets.s(4.0f), x + w, y + h + AnubisWidgets.s(4.0f), AnubisWidgets.c(0xFFFFFFF), 1.0f);
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

    private static void beginFrame() {
        ItemPickerEditor.refreshNames();
    }

    private static ItemInfo info(class_1792 item) {
        ItemPickerEditor.refreshNames();
        ItemInfo info = (ItemInfo)BY_ITEM.get((Object)item);
        if (info == null) {
            info = new ItemInfo(item);
            info.name = item.method_7854().method_7964().getString();
            info.nameLower = info.name.toLowerCase(Locale.ROOT);
            BY_ITEM.put((Object)item, (Object)info);
        }
        return info;
    }

    private static void refreshNames() {
        String selected = class_310.method_1551().method_1526().method_4669();
        if (selected.equals(language) && !ITEMS.isEmpty()) {
            return;
        }
        language = selected;
        if (ITEMS.isEmpty()) {
            for (class_1792 item : class_7923.field_41178) {
                if (item == class_1802.field_8162) continue;
                ItemInfo info2 = new ItemInfo(item);
                ITEMS.add(info2);
                BY_ITEM.put((Object)item, (Object)info2);
            }
        }
        for (ItemInfo info3 : ITEMS) {
            info3.name = info3.item.method_7854().method_7964().getString();
            info3.nameLower = info3.name.toLowerCase(Locale.ROOT);
        }
        ITEMS.sort(Comparator.comparing(info -> info.nameLower).thenComparing(info -> info.id));
        ++namesGeneration;
    }

    private static int matchDistance(ItemInfo info, String filter, String[] words) {
        int found = Math.max(ItemPickerEditor.wordsFound(info.nameLower, words), ItemPickerEditor.wordsFound(info.idLower, words));
        int nameDistance = ItemPickerEditor.levenshtein(filter, info.nameLower);
        int idDistance = ItemPickerEditor.levenshtein(filter, info.idLower);
        float relevancy = Math.max(1.0f - (float)nameDistance / (float)Math.max(1, info.nameLower.length()), 1.0f - (float)idDistance / (float)Math.max(1, info.idLower.length()));
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

    private static float frameDelta() {
        return ClickGuiRenderer.animationDelta(Math.max(ImGui.getIO().getDeltaTime(), 0.001f));
    }

    static {
        levPrev = new int[64];
        levCur = new int[64];
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ItemInfo {
        final class_1792 item;
        final int rawId;
        final String id;
        final String stored;
        final String idLower;
        String name;
        String nameLower;

        ItemInfo(class_1792 item) {
            this.item = item;
            this.rawId = class_7923.field_41178.method_10206((Object)item);
            class_2960 key = class_7923.field_41178.method_10221((Object)item);
            this.id = key.toString();
            this.stored = ItemEnchants.stored(item);
            this.idLower = this.id.toLowerCase(Locale.ROOT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class EnchantRow {
        final class_6880<class_1887> holder;
        final String path;
        final String key;
        final String name;
        final int max;
        int level;
        boolean on;

        EnchantRow(class_6880<class_1887> holder) {
            this.holder = holder;
            this.path = ItemEnchants.path(holder);
            this.key = ItemEnchants.key(this.path);
            this.name = ((class_1887)holder.comp_349()).comp_2686().getString();
            this.level = this.max = Math.max(1, ((class_1887)holder.comp_349()).method_8183());
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Summary(long revision, int names, String text) {
    }
}

