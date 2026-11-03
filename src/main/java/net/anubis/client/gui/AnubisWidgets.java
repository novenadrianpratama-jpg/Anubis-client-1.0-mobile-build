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
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.type.ImString;
import java.util.List;
import java.util.Locale;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.Motion;
import net.anubis.client.imgui.ImGuiManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AnubisWidgets {
    private static final float POPUP_MAX_H = 280.0f;
    private static final Motion.Store ANIM = new Motion.Store();

    private AnubisWidgets() {
    }

    public static float s(float v) {
        return v * ImGuiManager.getInstance().uiScale();
    }

    public static float animate(int key, boolean on, float speed) {
        return AnubisWidgets.ease(key, on ? 1.0f : 0.0f, speed);
    }

    private static float ease(int key, float target, float speed) {
        return ANIM.ease(key, target, ClickGuiRenderer.animationDelta(ImGuiManager.getInstance().frameDelta()), speed);
    }

    public static void text(ImDrawList dl, ImFont font, float x, float y, int color, String text) {
        AnubisWidgets.text(dl, font, x, y, color, text, false);
    }

    public static void text(ImDrawList dl, ImFont font, float x, float y, int color, String text, boolean guard) {
        int size = font != null ? Math.round(font.getFontSize()) : 0;
        HudChrome.text(dl, font, size, x, y, AnubisWidgets.c(color), text, guard);
    }

    public static float textWidth(ImFont font, String text) {
        return font != null ? font.calcTextSizeAX(font.getFontSize(), Float.MAX_VALUE, 0.0f, text) : ImGui.calcTextSize((String)text).x;
    }

    public static float lineHeight(ImFont font) {
        return font != null ? font.getFontSize() : ImGui.getTextLineHeight();
    }

    public static String ellipsize(ImFont font, String text, float maxW) {
        int end;
        if (AnubisWidgets.textWidth(font, text) <= maxW) {
            return text;
        }
        for (end = text.length(); end > 0 && AnubisWidgets.textWidth(font, text.substring(0, end) + "...") > maxW; --end) {
        }
        return text.substring(0, end).stripTrailing() + "...";
    }

    public static int c(int color) {
        return ImGui.getColorU32i((int)color);
    }

    public static int withAlpha(int color, float alpha) {
        int a = Math.round((float)(color >>> 24 & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return a << 24 | color & 0xFFFFFF;
    }

    public static void drawSwitch(ImDrawList dl, float x, float y, float w, float h, float t) {
        float r = h * 0.5f;
        dl.addRectFilled(x, y, x + w, y + h, AnubisWidgets.c(AnubisTheme.lerpColor(-11911366, GuiPalette.accent(), t)), r);
        float knobR = r - AnubisWidgets.s(2.5f);
        float cx = x + r + (w - 2.0f * r) * t;
        dl.addCircleFilled(cx, y + r + AnubisWidgets.s(1.0f), knobR, AnubisWidgets.c(0x33000000), 24);
        dl.addCircleFilled(cx, y + r, knobR, AnubisWidgets.c(-461067), 24);
    }

    public static boolean sliderFloat(String id, float[] value, float min, float max, float width) {
        return AnubisWidgets.sliderFloat(id, value, min, max, width, null);
    }

    public static boolean sliderFloat(String id, float[] value, float min, float max, float width, Snap snap) {
        float t;
        float hitH = AnubisWidgets.s(18.0f);
        float sx = ImGui.getCursorScreenPosX();
        float cy = ImGui.getCursorScreenPosY() + hitH * 0.5f;
        ImGui.invisibleButton((String)id, (float)width, (float)hitH);
        boolean hovered = ImGui.isItemHovered();
        boolean held = ImGui.isItemActive();
        float focus = AnubisWidgets.animate(ImGui.getItemID(), hovered || held, 16.0f);
        boolean changed = false;
        if (held) {
            t = Math.max(0.0f, Math.min(1.0f, (ImGui.getIO().getMousePosX() - sx) / Math.max(1.0f, width)));
            float nv = min + t * (max - min);
            if (snap != null) {
                nv = snap.apply(nv);
            }
            if (nv != value[0]) {
                value[0] = nv;
                changed = true;
            }
        }
        t = AnubisWidgets.frac(value[0], min, max);
        ImDrawList dl = ImGui.getWindowDrawList();
        float trackH = AnubisWidgets.s(4.0f);
        float knobR = AnubisWidgets.s(6.5f) + AnubisWidgets.s(1.5f) * focus;
        float inset = AnubisWidgets.s(7.0f);
        float x0 = sx + inset;
        float x1 = sx + width - inset;
        float fillX = x0 + t * (x1 - x0);
        dl.addRectFilled(x0, cy - trackH * 0.5f, x1, cy + trackH * 0.5f, AnubisWidgets.c(-12240588), trackH);
        if (fillX > x0 + 0.5f) {
            dl.addRectFilled(x0, cy - trackH * 0.5f, fillX, cy + trackH * 0.5f, AnubisWidgets.c(GuiPalette.accent()), trackH);
        }
        if (focus > 0.01f) {
            dl.addCircleFilled(fillX, cy, knobR + AnubisWidgets.s(4.0f), AnubisWidgets.c(AnubisWidgets.withAlpha(GuiPalette.accent(), 0.25f * focus)), 24);
        }
        dl.addCircleFilled(fillX, cy + AnubisWidgets.s(1.0f), knobR, AnubisWidgets.c(0x40000000), 24);
        dl.addCircleFilled(fillX, cy, knobR, AnubisWidgets.c(-461067), 24);
        return changed;
    }

    public static boolean chipButton(String id, String label, float w, float h, boolean active) {
        int bg;
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        boolean clicked = ImGui.invisibleButton((String)id, (float)w, (float)h);
        boolean hovered = ImGui.isItemHovered();
        boolean held = ImGui.isItemActive();
        float hover = AnubisWidgets.animate(ImGui.getItemID(), hovered, 18.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        int n = bg = active ? AnubisTheme.lerpColor(GuiPalette.accent(), GuiPalette.accentHover(), hover * 0.35f) : AnubisTheme.lerpColor(-13226454, -12240588, hover);
        if (held) {
            bg = AnubisTheme.lerpColor(bg, -16777216, 0.12f);
        }
        dl.addRectFilled(sx, sy, sx + w, sy + h, AnubisWidgets.c(bg), AnubisWidgets.s(7.0f));
        ImFont font = ImGui.getFont();
        float tw = AnubisWidgets.textWidth(font, label);
        AnubisWidgets.text(dl, font, sx + (w - tw) * 0.5f, sy + (h - AnubisWidgets.lineHeight(font)) * 0.5f, active ? -1 : -461582, label);
        return clicked;
    }

    public static boolean iconButton(String id, float size) {
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        boolean clicked = ImGui.invisibleButton((String)id, (float)size, (float)size);
        float hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
        ImGui.getWindowDrawList().addCircleFilled(sx + size * 0.5f, sy + size * 0.5f, size * 0.5f, AnubisWidgets.c(AnubisTheme.lerpColor(-13226454, -12240588, hover)), 32);
        return clicked;
    }

    public static int segmented(String id, String[] labels, int selected, float w, float h) {
        float localX = ImGui.getCursorPosX();
        float localY = ImGui.getCursorPosY();
        float bx = ImGui.getCursorScreenPosX();
        float by = ImGui.getCursorScreenPosY();
        float inner = AnubisWidgets.s(3.0f);
        float r = AnubisWidgets.s(8.0f);
        float segW = (w - inner * 2.0f) / (float)labels.length;
        float pillX = AnubisWidgets.ease(ImGui.getID((String)id), inner + (float)selected * segW, 18.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        dl.addRectFilled(bx, by, bx + w, by + h, AnubisWidgets.c(-13226454), r);
        dl.addRectFilled(bx + pillX, by + inner, bx + pillX + segW, by + h - inner, AnubisWidgets.c(GuiPalette.accent()), r - inner);
        ImFont font = ImGui.getFont();
        int clicked = -1;
        for (int i = 0; i < labels.length; ++i) {
            ImGui.setCursorPos((float)(localX + inner + (float)i * segW), (float)(localY + inner));
            if (ImGui.invisibleButton((String)(id + i), (float)segW, (float)(h - inner * 2.0f))) {
                clicked = i;
            }
            float hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 18.0f);
            float centre = bx + inner + (float)i * segW + segW * 0.5f;
            float cover = Math.max(0.0f, 1.0f - Math.abs(centre - (bx + pillX + segW * 0.5f)) / (segW * 0.5f));
            int color = AnubisTheme.lerpColor(AnubisTheme.lerpColor(-4542808, -461582, hover), -1, cover);
            float tw = AnubisWidgets.textWidth(font, labels[i]);
            AnubisWidgets.text(dl, font, centre - tw * 0.5f, by + (h - AnubisWidgets.lineHeight(font)) * 0.5f, color, labels[i]);
        }
        return clicked;
    }

    public static int dropdown(String id, String current, List<String> options, float w, float h) {
        return AnubisWidgets.dropdown(id, current, current, options, w, h, null);
    }

    public static int dropdown(String id, String current, String label, List<String> options, float w, float h, ImString query) {
        String popupId = id + "_popup";
        float sx = ImGui.getCursorScreenPosX();
        float sy = ImGui.getCursorScreenPosY();
        if (ImGui.invisibleButton((String)id, (float)w, (float)h)) {
            ImGui.openPopup((String)popupId);
            if (query != null) {
                query.set("");
            }
        }
        boolean open = ImGui.isPopupOpen((String)popupId);
        float hover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered() || open, 18.0f);
        ImDrawList dl = ImGui.getWindowDrawList();
        float r = AnubisWidgets.s(7.0f);
        dl.addRectFilled(sx, sy, sx + w, sy + h, AnubisWidgets.c(AnubisTheme.lerpColor(-13226454, -12240588, hover)), r);
        if (open) {
            dl.addRect(sx, sy, sx + w, sy + h, AnubisWidgets.c(AnubisWidgets.withAlpha(GuiPalette.accent(), 0.8f)), r, 0, AnubisWidgets.s(1.2f));
        }
        ImFont font = ImGui.getFont();
        AnubisWidgets.text(dl, font, sx + AnubisWidgets.s(10.0f), sy + (h - AnubisWidgets.lineHeight(font)) * 0.5f, current.isEmpty() ? -4542808 : -461582, AnubisWidgets.ellipsize(font, label, w - AnubisWidgets.s(34.0f)));
        AnubisWidgets.chevron(dl, sx + w - AnubisWidgets.s(14.0f), sy + h * 0.5f, AnubisWidgets.s(4.0f), -4542808, open);
        int picked = -1;
        float popupAnim = AnubisWidgets.animate(ImGui.getID((String)popupId), open, 22.0f);
        float itemH = AnubisWidgets.s(30.0f);
        float roomBelow = ImGui.getIO().getDisplaySizeY() - (sy + h);
        float slide = AnubisWidgets.s(6.0f) * (1.0f - popupAnim);
        if (roomBelow < AnubisWidgets.s(290.0f) && sy > roomBelow) {
            ImGui.setNextWindowPos((float)sx, (float)(sy - AnubisWidgets.s(4.0f) + slide), (int)1, (float)0.0f, (float)1.0f);
        } else {
            ImGui.setNextWindowPos((float)sx, (float)(sy + h + AnubisWidgets.s(4.0f) - slide));
        }
        ImGui.setNextWindowSizeConstraints((float)w, (float)0.0f, (float)w, (float)AnubisWidgets.s(280.0f));
        ImGui.pushStyleVar((int)0, (float)Math.max(0.05f, popupAnim));
        ImGui.pushStyleVar((int)2, (float)AnubisWidgets.s(4.0f), (float)AnubisWidgets.s(4.0f));
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)AnubisWidgets.s(2.0f));
        if (ImGui.beginPopup((String)popupId, (int)8)) {
            float rowW = w - AnubisWidgets.s(8.0f);
            String needle = "";
            if (query != null) {
                if (ImGui.isWindowAppearing()) {
                    ImGui.setKeyboardFocusHere();
                }
                ImGui.pushStyleVar((int)11, (float)AnubisWidgets.s(8.0f), (float)AnubisWidgets.s(6.0f));
                ImGui.setNextItemWidth((float)rowW);
                ImGui.inputTextWithHint((String)"##search", (String)"Search...", (ImString)query);
                boolean enter = AnubisWidgets.submittedWithEnter();
                ImGui.popStyleVar();
                needle = query.get().trim().toLowerCase(Locale.ROOT);
                if (enter) {
                    for (int i = 0; i < options.size() && picked < 0; ++i) {
                        if (!AnubisWidgets.matches(options.get(i), needle)) continue;
                        picked = i;
                    }
                    if (picked >= 0) {
                        ImGui.closeCurrentPopup();
                    }
                }
            }
            ImDrawList pdl = ImGui.getWindowDrawList();
            boolean any = false;
            for (int i = 0; i < options.size(); ++i) {
                String option = options.get(i);
                if (!AnubisWidgets.matches(option, needle)) continue;
                any = true;
                float rx = ImGui.getCursorScreenPosX();
                float ry = ImGui.getCursorScreenPosY();
                if (ImGui.invisibleButton((String)("##opt" + i), (float)rowW, (float)itemH)) {
                    picked = i;
                    ImGui.closeCurrentPopup();
                }
                boolean selected = option.equals(current);
                float rowHover = AnubisWidgets.animate(ImGui.getItemID(), ImGui.isItemHovered(), 20.0f);
                if (rowHover > 0.01f) {
                    pdl.addRectFilled(rx, ry, rx + rowW, ry + itemH, AnubisWidgets.c(AnubisWidgets.withAlpha(-12240588, rowHover)), AnubisWidgets.s(6.0f));
                }
                AnubisWidgets.text(pdl, font, rx + AnubisWidgets.s(10.0f), ry + (itemH - AnubisWidgets.lineHeight(font)) * 0.5f, selected ? GuiPalette.accent() : -461582, AnubisWidgets.ellipsize(font, option, rowW - AnubisWidgets.s(34.0f)));
                if (!selected) continue;
                AnubisWidgets.check(pdl, rx + rowW - AnubisWidgets.s(14.0f), ry + itemH * 0.5f, AnubisWidgets.s(5.0f), GuiPalette.accent());
            }
            if (!any) {
                float rx = ImGui.getCursorScreenPosX();
                float ry = ImGui.getCursorScreenPosY();
                ImGui.dummy((float)rowW, (float)itemH);
                AnubisWidgets.text(pdl, font, rx + AnubisWidgets.s(10.0f), ry + (itemH - AnubisWidgets.lineHeight(font)) * 0.5f, -4542808, options.isEmpty() ? "Nothing to pick yet" : "No matches");
            }
            ImGui.endPopup();
        }
        ImGui.popStyleVar((int)3);
        return picked;
    }

    private static boolean matches(String option, String needle) {
        return needle.isEmpty() || option.toLowerCase(Locale.ROOT).contains(needle);
    }

    public static boolean submittedWithEnter() {
        return ImGui.isItemDeactivated() && (ImGui.isKeyPressed((int)525, (boolean)false) || ImGui.isKeyPressed((int)627, (boolean)false));
    }

    public static void chevron(ImDrawList dl, float cx, float cy, float r, int color, boolean up) {
        float dy = up ? -r * 0.5f : r * 0.5f;
        dl.addLine(cx - r, cy - dy, cx, cy + dy, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
        dl.addLine(cx, cy + dy, cx + r, cy - dy, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
    }

    public static void chevronLeft(ImDrawList dl, float cx, float cy, float r, int color) {
        dl.addLine(cx + r * 0.5f, cy - r, cx - r * 0.5f, cy, AnubisWidgets.c(color), AnubisWidgets.s(2.0f));
        dl.addLine(cx - r * 0.5f, cy, cx + r * 0.5f, cy + r, AnubisWidgets.c(color), AnubisWidgets.s(2.0f));
    }

    public static void chevronRight(ImDrawList dl, float cx, float cy, float r, int color) {
        dl.addLine(cx - r * 0.5f, cy - r, cx + r * 0.5f, cy, AnubisWidgets.c(color), AnubisWidgets.s(2.0f));
        dl.addLine(cx + r * 0.5f, cy, cx - r * 0.5f, cy + r, AnubisWidgets.c(color), AnubisWidgets.s(2.0f));
    }

    public static void chevronRotated(ImDrawList dl, float cx, float cy, float r, int color, float angle) {
        float dx = (float)Math.cos(angle);
        float dy = (float)Math.sin(angle);
        float tipX = cx + dx * r * 0.5f;
        float tipY = cy + dy * r * 0.5f;
        float backX = cx - dx * r * 0.5f;
        float backY = cy - dy * r * 0.5f;
        dl.addLine(backX - dy * r, backY + dx * r, tipX, tipY, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
        dl.addLine(tipX, tipY, backX + dy * r, backY - dx * r, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
    }

    public static void check(ImDrawList dl, float cx, float cy, float r, int color) {
        dl.addLine(cx - r, cy, cx - r * 0.3f, cy + r * 0.7f, AnubisWidgets.c(color), AnubisWidgets.s(1.8f));
        dl.addLine(cx - r * 0.3f, cy + r * 0.7f, cx + r, cy - r * 0.7f, AnubisWidgets.c(color), AnubisWidgets.s(1.8f));
    }

    public static void cross(ImDrawList dl, float cx, float cy, float r, int color) {
        dl.addLine(cx - r, cy - r, cx + r, cy + r, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
        dl.addLine(cx - r, cy + r, cx + r, cy - r, AnubisWidgets.c(color), AnubisWidgets.s(1.6f));
    }

    public static void magnifier(ImDrawList dl, float cx, float cy, float r, int color) {
        float cr = r * 0.62f;
        dl.addCircle(cx - r * 0.15f, cy - r * 0.15f, cr, AnubisWidgets.c(color), 20, AnubisWidgets.s(1.6f));
        dl.addLine(cx + r * 0.3f, cy + r * 0.3f, cx + r, cy + r, AnubisWidgets.c(color), AnubisWidgets.s(1.8f));
    }

    public static void crosshair(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        dl.addCircle(cx, cy, r * 0.62f, AnubisWidgets.c(color), 20, t);
        dl.addLine(cx, cy - r, cx, cy - r * 0.35f, AnubisWidgets.c(color), t);
        dl.addLine(cx, cy + r * 0.35f, cx, cy + r, AnubisWidgets.c(color), t);
        dl.addLine(cx - r, cy, cx - r * 0.35f, cy, AnubisWidgets.c(color), t);
        dl.addLine(cx + r * 0.35f, cy, cx + r, cy, AnubisWidgets.c(color), t);
    }

    public static void move(ImDrawList dl, float cx, float cy, float r, int color) {
        int col = AnubisWidgets.c(color);
        float t = AnubisWidgets.s(1.5f);
        float head = r * 0.42f;
        float shaft = r - head * 0.8f;
        dl.addLine(cx - shaft, cy, cx + shaft, cy, col, t);
        dl.addLine(cx, cy - shaft, cx, cy + shaft, col, t);
        dl.addTriangleFilled(cx - r, cy, cx - r + head, cy - head, cx - r + head, cy + head, col);
        dl.addTriangleFilled(cx + r, cy, cx + r - head, cy + head, cx + r - head, cy - head, col);
        dl.addTriangleFilled(cx, cy - r, cx + head, cy - r + head, cx - head, cy - r + head, col);
        dl.addTriangleFilled(cx, cy + r, cx - head, cy + r - head, cx + head, cy + r - head, col);
    }

    public static void grid(ImDrawList dl, float cx, float cy, float r, int color) {
        float g = r * 0.18f;
        float c = r * 0.8f;
        dl.addRectFilled(cx - c, cy - c, cx - g, cy - g, AnubisWidgets.c(color), AnubisWidgets.s(1.5f));
        dl.addRectFilled(cx + g, cy - c, cx + c, cy - g, AnubisWidgets.c(color), AnubisWidgets.s(1.5f));
        dl.addRectFilled(cx - c, cy + g, cx - g, cy + c, AnubisWidgets.c(color), AnubisWidgets.s(1.5f));
        dl.addRectFilled(cx + g, cy + g, cx + c, cy + c, AnubisWidgets.c(color), AnubisWidgets.s(1.5f));
    }

    public static void ring(ImDrawList dl, float cx, float cy, float r, int color) {
        dl.addCircle(cx, cy, r * 0.62f, AnubisWidgets.c(color), 24, r * 0.42f);
    }

    public static void eye(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        dl.addBezierQuadratic(cx - r, cy, cx, cy - r * 1.15f, cx + r, cy, AnubisWidgets.c(color), t, 16);
        dl.addBezierQuadratic(cx - r, cy, cx, cy + r * 1.15f, cx + r, cy, AnubisWidgets.c(color), t, 16);
        dl.addCircleFilled(cx, cy, r * 0.3f, AnubisWidgets.c(color), 16);
    }

    public static void monitor(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        dl.addRect(cx - r, cy - r * 0.8f, cx + r, cy + r * 0.4f, AnubisWidgets.c(color), AnubisWidgets.s(2.0f), 0, t);
        dl.addLine(cx, cy + r * 0.4f, cx, cy + r * 0.8f, AnubisWidgets.c(color), t);
        dl.addLine(cx - r * 0.5f, cy + r * 0.85f, cx + r * 0.5f, cy + r * 0.85f, AnubisWidgets.c(color), t);
    }

    public static void papyrus(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        float rollW = r * 0.36f;
        float rollH = r * 0.85f;
        float sheetH = r * 0.62f;
        dl.addRect(cx - r, cy - rollH, cx - r + rollW, cy + rollH, AnubisWidgets.c(color), rollW * 0.5f, 0, t);
        dl.addRect(cx + r - rollW, cy - rollH, cx + r, cy + rollH, AnubisWidgets.c(color), rollW * 0.5f, 0, t);
        dl.addLine(cx - r + rollW, cy - sheetH, cx + r - rollW, cy - sheetH, AnubisWidgets.c(color), t);
        dl.addLine(cx - r + rollW, cy + sheetH, cx + r - rollW, cy + sheetH, AnubisWidgets.c(color), t);
        dl.addLine(cx - r * 0.3f, cy - r * 0.15f, cx + r * 0.3f, cy - r * 0.15f, AnubisWidgets.c(color), t);
        dl.addLine(cx - r * 0.3f, cy + r * 0.22f, cx + r * 0.12f, cy + r * 0.22f, AnubisWidgets.c(color), t);
    }

    public static void house(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        dl.addLine(cx - r, cy - r * 0.05f, cx, cy - r * 0.95f, AnubisWidgets.c(color), t);
        dl.addLine(cx, cy - r * 0.95f, cx + r, cy - r * 0.05f, AnubisWidgets.c(color), t);
        dl.addRect(cx - r * 0.7f, cy - r * 0.3f, cx + r * 0.7f, cy + r * 0.85f, AnubisWidgets.c(color), 0.0f, 0, t);
        dl.addRectFilled(cx - r * 0.2f, cy + r * 0.25f, cx + r * 0.2f, cy + r * 0.85f, AnubisWidgets.c(color));
    }

    public static void ankh(ImDrawList dl, float cx, float cy, float r, int color) {
        float t = AnubisWidgets.s(1.5f);
        float barY = cy - r * 0.15f;
        float top = cy - r;
        dl.addBezierCubic(cx, barY, cx - r * 0.62f, barY - r * 0.3f, cx - r * 0.5f, top, cx, top, AnubisWidgets.c(color), t, 12);
        dl.addBezierCubic(cx, barY, cx + r * 0.62f, barY - r * 0.3f, cx + r * 0.5f, top, cx, top, AnubisWidgets.c(color), t, 12);
        dl.addLine(cx - r * 0.72f, barY, cx + r * 0.72f, barY, AnubisWidgets.c(color), t);
        dl.addLine(cx, barY, cx, cy + r, AnubisWidgets.c(color), t);
    }

    private static float frac(float v, float min, float max) {
        float r = max - min;
        return r < 1.0E-6f ? 0.0f : Math.max(0.0f, Math.min(1.0f, (v - min) / r));
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Snap {
        public float apply(float var1);
    }
}

