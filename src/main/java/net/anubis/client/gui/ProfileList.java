/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  imgui.type.ImString
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.type.ImString;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import net.anubis.client.config.ConfigProfiles;
import net.anubis.client.config.ProfileException;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.FileDialogs;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.imgui.ImGuiManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
final class ProfileList {
    private static final float ROW_H = 52.0f;
    private static final float CHIP_W = 58.0f;
    private static final float CHIP_H = 28.0f;
    private static final float GAP = 6.0f;
    private static final float LABEL_W = 96.0f;
    private static final float FIELD_H = 30.0f;
    private static final long ARM_NANOS = 3000000000L;
    private static final int DISCORD_LIMIT = 2000;
    private static final String PRIVATE_NOTE = "Webhooks, macros, seeds and other private settings are not included.";
    private final BiConsumer<String, Boolean> messages;
    private final ImString renameInput = new ImString(64);
    private final ImString serverInput = new ImString(256);
    private final Map<String, Float> editorHeights = new HashMap<String, Float>();
    private String expanded;
    private String armed;
    private long armedAt;
    private String scrollTo;
    private float scrollTarget = Float.NaN;
    private boolean focusServer;

    ProfileList(BiConsumer<String, Boolean> messages) {
        this.messages = messages;
    }

    void open(String name) {
        this.expanded = null;
        this.toggle(name);
        this.scrollTo = ProfileList.key(name);
    }

    void disarm() {
        this.armed = null;
    }

    float takeScrollTarget() {
        float target = this.scrollTarget;
        this.scrollTarget = Float.NaN;
        return target;
    }

    void render(ConfigProfiles profiles, List<ConfigProfiles.Profile> list, String active, float pad, float contentW) {
        for (ConfigProfiles.Profile profile : list) {
            this.row(profiles, profile, profile.name().equalsIgnoreCase(active), pad, contentW);
        }
    }

    private void row(ConfigProfiles profiles, ConfigProfiles.Profile profile, boolean active, float pad, float contentW) {
        float bx;
        int corners;
        String name = profile.name();
        String key = ProfileList.key(name);
        boolean open = key.equals(this.expanded);
        ImGui.pushID((String)("profile_" + key));
        float localY = ImGui.getCursorPosY();
        if (key.equals(this.scrollTo)) {
            this.scrollTarget = localY - AnubisWidgets.s(8.0f);
            this.scrollTo = null;
        }
        float rowH = AnubisWidgets.s(52.0f);
        float reveal = AnubisWidgets.animate(ImGui.getID((String)"##reveal"), open, 16.0f);
        float editorH = reveal > 0.001f ? this.editorHeights.getOrDefault(key, Float.valueOf(AnubisWidgets.s(240.0f))).floatValue() * reveal : 0.0f;
        ImGui.setCursorPos((float)pad, (float)localY);
        float x0 = ImGui.getCursorScreenPosX();
        float y0 = ImGui.getCursorScreenPosY();
        ImGui.setNextItemAllowOverlap();
        ImGui.invisibleButton((String)"##row", (float)contentW, (float)rowH);
        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked((int)0)) {
            this.load(profiles, profile);
        }
        boolean rowHovered = ImGui.isMouseHoveringRect((float)x0, (float)y0, (float)(x0 + contentW), (float)(y0 + rowH));
        float hover = AnubisWidgets.animate(ImGui.getItemID(), rowHovered, 18.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        float left = x0 - AnubisWidgets.s(8.0f);
        float right = x0 + contentW + AnubisWidgets.s(8.0f);
        float r = AnubisWidgets.s(10.0f);
        int n = corners = reveal > 0.001f ? 48 : 240;
        if (reveal > 0.001f) {
            dl.addRectFilled(left, y0, right, y0 + rowH + editorH, AnubisWidgets.c(AnubisWidgets.withAlpha(0xAFFFFFF, reveal)), r);
        }
        if (active) {
            dl.addRectFilled(left, y0, right, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(GuiPalette.accent(), 0.16f)), r, corners);
        }
        if (hover > 0.01f) {
            dl.addRectFilled(left, y0, right, y0 + rowH, AnubisWidgets.c(AnubisWidgets.withAlpha(0xCFFFFFF, hover)), r, corners);
        }
        ImFont font = ImGui.getFont();
        ImFont small = ImGuiManager.getInstance().smallFont();
        float chipW = AnubisWidgets.s(58.0f);
        float chipH = AnubisWidgets.s(28.0f);
        float editSize = AnubisWidgets.s(28.0f);
        float gap = AnubisWidgets.s(6.0f);
        float buttonsW = chipW * 2.0f + editSize + gap * 2.0f;
        float textRoom = contentW - buttonsW - AnubisWidgets.s(12.0f);
        float badgeW = active ? AnubisWidgets.textWidth(small, "Active") + AnubisWidgets.s(14.0f) : 0.0f;
        String shown = AnubisWidgets.ellipsize(font, name, textRoom - (active ? badgeW + AnubisWidgets.s(8.0f) : 0.0f));
        float lineH = AnubisWidgets.lineHeight(font);
        float smallH = AnubisWidgets.lineHeight(small);
        float ty = y0 + (rowH - lineH - AnubisWidgets.s(3.0f) - smallH) * 0.5f;
        AnubisWidgets.text(dl, font, x0, ty, -461582, shown);
        if (active) {
            bx = x0 + AnubisWidgets.textWidth(font, shown) + AnubisWidgets.s(8.0f);
            float bh = smallH + AnubisWidgets.s(6.0f);
            float by = ty + (lineH - bh) * 0.5f;
            dl.addRectFilled(bx, by, bx + badgeW, by + bh, AnubisWidgets.c(GuiPalette.accent()), bh * 0.5f);
            AnubisWidgets.text(dl, small, bx + AnubisWidgets.s(7.0f), by + AnubisWidgets.s(3.0f), -1, "Active");
        }
        AnubisWidgets.text(dl, small, x0, ty + lineH + AnubisWidgets.s(3.0f), profile.broken() ? -10856974 : -4542808, AnubisWidgets.ellipsize(small, ProfileList.summary(profile), textRoom));
        bx = pad + contentW - buttonsW;
        float by = localY + (rowH - chipH) * 0.5f;
        ImGui.setCursorPos((float)bx, (float)by);
        if (AnubisWidgets.chipButton("##load", "Load", chipW, chipH, false)) {
            this.load(profiles, profile);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Apply the saved " + ProfileList.partList(profile.parts()) + " of this profile");
        }
        String saveKey = "save:" + key;
        boolean saveArmed = this.isArmed(saveKey);
        ImGui.setCursorPos((float)(bx + chipW + gap), (float)by);
        if (AnubisWidgets.chipButton("##save", saveArmed ? "Sure?" : "Save", chipW, chipH, saveArmed) && this.confirm(saveKey)) {
            this.save(profiles, profile);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Overwrite this profile with your current " + ProfileList.partList(profile.parts()));
        }
        ImGui.setCursorPos((float)(bx + (chipW + gap) * 2.0f), (float)(localY + (rowH - editSize) * 0.5f));
        float ex = ImGui.getCursorScreenPosX() + editSize * 0.5f;
        float ey = ImGui.getCursorScreenPosY() + editSize * 0.5f;
        if (ImGui.invisibleButton((String)"##edit", (float)editSize, (float)editSize)) {
            this.toggle(name);
        }
        boolean editHovered = ImGui.isItemHovered();
        float editHover = Math.max(AnubisWidgets.animate(ImGui.getItemID(), editHovered, 18.0f), reveal);
        if (editHover > 0.01f) {
            dl.addCircleFilled(ex, ey, editSize * 0.5f, AnubisWidgets.c(AnubisWidgets.withAlpha(-12240588, editHover)), 32);
        }
        AnubisWidgets.chevronRotated(dl, ex, ey, AnubisWidgets.s(4.0f), AnubisTheme.lerpColor(-4542808, -461582, editHover), reveal * 1.5707964f);
        if (editHovered) {
            ClickGuiRenderer.showTooltip(open ? "Close" : "Edit, share or delete");
        }
        float bottom = localY + rowH;
        if (reveal > 0.001f) {
            ImGui.pushClipRect((float)left, (float)(y0 + rowH), (float)right, (float)(y0 + rowH + editorH), (boolean)true);
            ImGui.pushStyleVar((int)0, (float)(ImGui.getStyle().getAlpha() * reveal));
            this.editorHeights.put(key, Float.valueOf(this.editor(profiles, profile, key, pad, contentW, bottom)));
            ImGui.popStyleVar();
            ImGui.popClipRect();
        }
        ImGui.setCursorPos((float)0.0f, (float)(bottom + editorH + AnubisWidgets.s(4.0f)));
        ImGui.popID();
    }

    private float editor(ConfigProfiles profiles, ConfigProfiles.Profile profile, String key, float pad, float contentW, float top) {
        ImDrawList dl = ImGui.getWindowDrawList();
        ImFont font = ImGui.getFont();
        float h = AnubisWidgets.s(30.0f);
        float gap = AnubisWidgets.s(6.0f);
        float framePadY = (h - ImGui.getTextLineHeight()) * 0.5f;
        float fieldX = pad + AnubisWidgets.s(96.0f);
        float fieldW = contentW - AnubisWidgets.s(96.0f);
        float y = top + AnubisWidgets.s(6.0f);
        ProfileList.label("Name", pad, y, h);
        float renameW = AnubisWidgets.s(78.0f);
        ImGui.setCursorPos((float)fieldX, (float)y);
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(10.0f), (float)framePadY);
        ImGui.setNextItemWidth((float)(fieldW - renameW - gap));
        ImGui.inputTextWithHint((String)"##rename", (String)"Profile name", (ImString)this.renameInput);
        boolean rename = AnubisWidgets.submittedWithEnter();
        ImGui.popStyleVar();
        ImGui.setCursorPos((float)(pad + contentW - renameW), (float)y);
        if (AnubisWidgets.chipButton("##rename_go", "Rename", renameW, h, false)) {
            rename = true;
        }
        if (rename) {
            this.rename(profiles, profile);
        }
        ProfileList.label("Includes", pad, y += h + AnubisWidgets.s(10.0f), h);
        ConfigProfiles.Part[] parts = ConfigProfiles.Part.values();
        float partW = (fieldW - gap * (float)(parts.length - 1)) / (float)parts.length;
        for (int i = 0; i < parts.length; ++i) {
            ConfigProfiles.Part part = parts[i];
            boolean on = profile.parts().contains((Object)part);
            ImGui.setCursorPos((float)(fieldX + (float)i * (partW + gap)), (float)y);
            if (AnubisWidgets.chipButton("##part_" + part.key, part.label, partW, h, on)) {
                this.setPart(profiles, profile, part, !on);
            }
            if (!ImGui.isItemHovered((int)65536)) continue;
            ClickGuiRenderer.showTooltip(ProfileList.partTip(part, on));
        }
        ProfileList.label("Load on join", pad, y += h + AnubisWidgets.s(10.0f), h);
        for (String server : profile.servers()) {
            ImGui.pushID((String)("server_" + server));
            ImGui.setCursorPos((float)fieldX, (float)y);
            float sx = ImGui.getCursorScreenPosX();
            float sy = ImGui.getCursorScreenPosY();
            dl.addRectFilled(sx, sy, sx + fieldW, sy + h, AnubisWidgets.c(-13226454), AnubisWidgets.s(7.0f));
            AnubisWidgets.text(dl, font, sx + AnubisWidgets.s(10.0f), sy + (h - AnubisWidgets.lineHeight(font)) * 0.5f, -461582, AnubisWidgets.ellipsize(font, server, fieldW - h - AnubisWidgets.s(14.0f)));
            ImGui.setCursorPos((float)(fieldX + fieldW - h), (float)y);
            if (ImGui.invisibleButton((String)"##remove", (float)h, (float)h)) {
                this.removeServer(profiles, profile, server);
            }
            boolean removeHovered = ImGui.isItemHovered();
            float removeHover = AnubisWidgets.animate(ImGui.getItemID(), removeHovered, 18.0f);
            float rcx = sx + fieldW - h * 0.5f;
            float rcy = sy + h * 0.5f;
            if (removeHover > 0.01f) {
                dl.addCircleFilled(rcx, rcy, h * 0.38f, AnubisWidgets.c(AnubisWidgets.withAlpha(-10856974, 0.18f * removeHover)), 24);
            }
            AnubisWidgets.cross(dl, rcx, rcy, AnubisWidgets.s(3.5f), AnubisTheme.lerpColor(-4542808, -10856974, removeHover));
            if (removeHovered) {
                ClickGuiRenderer.showTooltip("Stop loading on " + server);
            }
            ImGui.popID();
            y += h + gap;
        }
        float addW = AnubisWidgets.s(60.0f);
        ImGui.setCursorPos((float)fieldX, (float)y);
        ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(10.0f), (float)framePadY);
        ImGui.setNextItemWidth((float)(fieldW - addW - gap));
        if (this.focusServer) {
            ImGui.setKeyboardFocusHere();
            this.focusServer = false;
        }
        ImGui.inputTextWithHint((String)"##server", (String)"Server address", (ImString)this.serverInput, (int)16);
        boolean add = AnubisWidgets.submittedWithEnter();
        ImGui.popStyleVar();
        ImGui.setCursorPos((float)(pad + contentW - addW), (float)y);
        if (AnubisWidgets.chipButton("##server_add", "Add", addW, h, false)) {
            add = true;
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Loads this profile when you join one of these servers.");
        }
        if (add) {
            this.addServer(profiles, profile);
        }
        float actionW = (contentW - gap * 2.0f) / 3.0f;
        ImGui.setCursorPos((float)pad, (float)(y += h + AnubisWidgets.s(12.0f)));
        if (AnubisWidgets.chipButton("##export", "Export file", actionW, h, false)) {
            this.export(profile);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Save this profile as a .json file.\nWebhooks, macros, seeds and other private settings are not included.");
        }
        ImGui.setCursorPos((float)(pad + actionW + gap), (float)y);
        if (AnubisWidgets.chipButton("##code", "Copy code", actionW, h, false)) {
            this.copyCode(profiles, profile);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip("Copy a share code. Others load it with Paste code.\nWebhooks, macros, seeds and other private settings are not included.");
        }
        String deleteKey = "delete:" + key;
        boolean deleteArmed = this.isArmed(deleteKey);
        ImGui.setCursorPos((float)(pad + (actionW + gap) * 2.0f), (float)y);
        if (ProfileList.dangerChip("##delete", deleteArmed ? "Sure?" : "Delete", actionW, h, deleteArmed) && this.confirm(deleteKey)) {
            this.delete(profiles, profile);
        }
        if (ImGui.isItemHovered((int)65536)) {
            ClickGuiRenderer.showTooltip(deleteArmed ? "Click again to delete" : "Delete this profile");
        }
        return (y += h + AnubisWidgets.s(12.0f)) - top;
    }

    private void load(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            Set<ConfigProfiles.Part> applied = profiles.load(profile.name());
            this.messages.accept("Loaded " + profile.name() + " (" + ProfileList.partList(applied) + ")", false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void save(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            Set<ConfigProfiles.Part> saved = profiles.save(profile.name());
            this.messages.accept("Saved your current " + ProfileList.partList(saved) + " to " + profile.name(), false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void rename(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            String renamed = profiles.rename(profile.name(), this.renameInput.get());
            if (renamed.equals(profile.name())) {
                return;
            }
            this.expanded = ProfileList.key(renamed);
            this.renameInput.set(renamed);
            this.messages.accept("Renamed " + profile.name() + " to " + renamed, false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void setPart(ConfigProfiles profiles, ConfigProfiles.Profile profile, ConfigProfiles.Part part, boolean included) {
        try {
            profiles.setPart(profile.name(), part, included);
            this.messages.accept(included ? "Saved your current " + part.label + " into " + profile.name() : part.label + " is no longer part of " + profile.name(), false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void addServer(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            String added = profiles.addServer(profile.name(), this.serverInput.get());
            this.serverInput.set("");
            this.messages.accept(profile.name() + " now loads when you join " + added, false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
        this.focusServer = true;
    }

    private void removeServer(ConfigProfiles profiles, ConfigProfiles.Profile profile, String server) {
        try {
            profiles.removeServer(profile.name(), server);
            this.messages.accept(profile.name() + " no longer loads on " + server, false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void export(ConfigProfiles.Profile profile) {
        if (FileDialogs.busy()) {
            this.messages.accept("A file window is already open", true);
            return;
        }
        String name = profile.name();
        FileDialogs.saveProfile("Export " + name, name + ".json", path -> {
            String fileName = path.getFileName().toString();
            Path target = fileName.toLowerCase(Locale.ROOT).endsWith(".json") ? path : path.resolveSibling(fileName + ".json");
            try {
                ConfigProfiles.getInstance().exportTo(name, target);
                this.messages.accept("Exported " + name + " to " + String.valueOf(target.getFileName()), false);
            }
            catch (ProfileException e) {
                this.messages.accept(e.getMessage(), true);
            }
        });
    }

    private void copyCode(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            String code = profiles.shareCode(profile.name());
            class_310.method_1551().field_1774.method_1455(code);
            this.messages.accept((String)(code.length() > 2000 ? "Copied, but it is too long for Discord. Send the file instead" : "Share code for " + profile.name() + " copied"), false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void delete(ConfigProfiles profiles, ConfigProfiles.Profile profile) {
        try {
            profiles.delete(profile.name());
            if (ProfileList.key(profile.name()).equals(this.expanded)) {
                this.expanded = null;
            }
            this.messages.accept("Deleted " + profile.name(), false);
        }
        catch (ProfileException e) {
            this.messages.accept(e.getMessage(), true);
        }
    }

    private void toggle(String name) {
        String key = ProfileList.key(name);
        if (key.equals(this.expanded)) {
            this.expanded = null;
            return;
        }
        this.expanded = key;
        this.renameInput.set(name);
        this.serverInput.set("");
    }

    private boolean isArmed(String key) {
        return key.equals(this.armed) && System.nanoTime() - this.armedAt < 3000000000L;
    }

    private boolean confirm(String key) {
        if (this.isArmed(key)) {
            this.armed = null;
            return true;
        }
        this.armed = key;
        this.armedAt = System.nanoTime();
        return false;
    }

    private static boolean dangerChip(String id, String label, float w, float h, boolean armed) {
        int bg;
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        boolean clicked = ImGui.invisibleButton((String)id, (float)w, (float)h);
        float hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
        int n = bg = armed ? -10856974 : AnubisTheme.lerpColor(-13226454, -12240588, hover);
        if (ImGui.isItemActive()) {
            bg = AnubisTheme.lerpColor(bg, -16777216, 0.12f);
        }
        ImDrawList dl = ImGui.getWindowDrawList();
        dl.addRectFilled(sx, sy, sx + w, sy + h, AnubisWidgets.c(bg), AnubisWidgets.s(7.0f));
        ImFont font = ImGui.getFont();
        int text = armed ? -1 : AnubisTheme.lerpColor(-461582, -10856974, hover);
        AnubisWidgets.text(dl, font, sx + (w - AnubisWidgets.textWidth(font, label)) * 0.5f, sy + (h - AnubisWidgets.lineHeight(font)) * 0.5f, text, label);
        return clicked;
    }

    private static void label(String text, float x, float y, float h) {
        ImFont small = ImGuiManager.getInstance().smallFont();
        float sx = ImGui.getWindowPosX() + x;
        float sy = ImGui.getWindowPosY() - ImGui.getScrollY() + y;
        AnubisWidgets.text(ImGui.getWindowDrawList(), small, sx, sy + (h - AnubisWidgets.lineHeight(small)) * 0.5f, -4542808, text);
    }

    private static String summary(ConfigProfiles.Profile profile) {
        if (profile.broken()) {
            return "Can't be read - fix the file or delete it";
        }
        String parts = ProfileList.partList(profile.parts());
        List<String> servers = profile.servers();
        if (servers.size() == 1) {
            return parts + "  \u00b7  loads on " + servers.get(0);
        }
        if (servers.size() > 1) {
            return parts + "  \u00b7  loads on " + servers.size() + " servers";
        }
        return parts;
    }

    private static String partList(Set<ConfigProfiles.Part> parts) {
        StringBuilder text = new StringBuilder();
        for (ConfigProfiles.Part part : parts) {
            if (!text.isEmpty()) {
                text.append(", ");
            }
            text.append(part.label);
        }
        return text.isEmpty() ? "nothing" : text.toString();
    }

    private static String partTip(ConfigProfiles.Part part, boolean included) {
        String what = switch (part) {
            default -> throw new MatchException(null, null);
            case ConfigProfiles.Part.MODULES -> "Module settings, keybinds and toggles.";
            case ConfigProfiles.Part.HUD -> "HUD layout and the HUD theme.";
            case ConfigProfiles.Part.THEME -> "GUI theme.";
            case ConfigProfiles.Part.FRIENDS -> "Your friends list.";
        };
        return what + (included ? "\nClick to remove it from this profile." : "\nClick to add it to this profile.");
    }

    private static String key(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}

