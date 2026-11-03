/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  imgui.ImGuiInputTextCallbackData
 *  imgui.callback.ImGuiInputTextCallback
 *  imgui.type.ImString
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.type.ImString;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.mainmenu.DuskScene;
import net.anubis.client.gui.mainmenu.MenuFrame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class DuskWidgets {
    public static final float BUTTON_H = 42.0f;
    public static final float RING = 42.0f;
    public static final float GAP = 11.0f;
    public static final float TAG_H = 24.0f;
    public static final float FIELD_H = 40.0f;
    public static final float PANEL_PAD = 22.0f;
    public static final float DIALOG_W = 560.0f;
    private static final float PANEL_ROUNDING = 8.0f;
    private static final float ROW_ROUNDING = 6.0f;
    private static final float FIELD_ROUNDING = 0.0f;
    private static final float FIELD_PAD = 14.0f;
    private static final float SCROLL_STEP = 64.0f;
    private static final float SCROLLBAR_W = 3.0f;
    private static final float SCROLLBAR_INSET = 5.0f;
    private static final float SCROLL_THUMB_MIN = 28.0f;
    private static final float SCROLLBAR_GRAB = 14.0f;
    private static final float BUTTON_TRACK = 3.0f;
    private static final float TAG_TRACK = 4.0f;
    private static final float DIALOG_FADE = 0.18f;
    private static final float DIALOG_VEIL = 0.62f;
    private static final float LINE_SPACING = 1.35f;
    private static final float EASE = 12.0f;
    private final Map<Object, Anim> anims = new HashMap<Object, Anim>();
    private final Map<Object, Scroll> scrolls = new HashMap<Object, Scroll>();
    private final ArrayDeque<Scroll> openAreas = new ArrayDeque();
    private final ArrayDeque<float[]> clips = new ArrayDeque();
    private MenuFrame f;
    private int frame;
    private boolean click;
    private boolean doubleClick;
    private boolean mouseDown;
    private float wheel;
    private boolean blocked;
    private float alpha = 1.0f;

    public void begin(MenuFrame current) {
        this.f = current;
        ++this.frame;
        this.anims.values().removeIf(anim -> anim.frame < this.frame - 1);
        this.click = current.clicked();
        this.doubleClick = this.click && ImGui.isMouseDoubleClicked((int)0);
        this.mouseDown = ImGui.isMouseDown((int)0);
        this.wheel = ImGui.getIO().getMouseWheel();
        this.blocked = false;
        this.alpha = 1.0f;
        this.openAreas.clear();
        this.clips.clear();
    }

    public void end() {
        while (!this.openAreas.isEmpty()) {
            this.openAreas.pop();
            this.popClip();
        }
    }

    public MenuFrame frame() {
        return this.f;
    }

    public void alpha(float alpha) {
        this.alpha = alpha;
    }

    public float alpha() {
        return this.alpha;
    }

    public boolean hovered(float x, float y, float w, float h) {
        if (this.blocked) {
            return false;
        }
        float mx = this.f.mouseX();
        float my = this.f.mouseY();
        if (mx < x || mx > x + w || my < y || my > y + h) {
            return false;
        }
        float[] clip = this.clips.peek();
        return clip == null || mx >= clip[0] && mx <= clip[2] && my >= clip[1] && my <= clip[3];
    }

    public boolean pressed(float x, float y, float w, float h) {
        if (!this.click || !this.hovered(x, y, w, h)) {
            return false;
        }
        this.click = false;
        return true;
    }

    public float hover(Object key, boolean target) {
        Anim anim = this.anims.computeIfAbsent(key, k -> new Anim());
        anim.value = anim.value + ((target ? 1.0f : 0.0f) - anim.value) * Math.min(1.0f, this.f.dt() * 12.0f);
        anim.frame = this.frame;
        return anim.value;
    }

    public float hoverOf(Object key) {
        Anim anim = this.anims.get(key);
        return anim != null ? anim.value : 0.0f;
    }

    public void tooltip(String text, float centerX, float y) {
        this.f.tooltip(text, centerX, y);
    }

    public boolean button(Object key, String label, float x, float y, float w, float h) {
        return this.button(key, label, x, y, w, h, true);
    }

    public boolean button(Object key, String label, float x, float y, float w, float h, boolean enabled) {
        boolean over = enabled && this.hovered(x, y, w, h);
        boolean clicked = over && this.pressed(x, y, w, h);
        float k = this.hover(key, over);
        DuskWidgets.cartoucheButton(this.f.bg(), this.f.menuFont(), x, y, w, h, label.toUpperCase(Locale.ROOT), enabled, k, this.f.scale(), this.alpha);
        return clicked;
    }

    public boolean iconButton(Object key, Icon icon, String tooltip, float x, float y, float size) {
        boolean over = this.hovered(x, y, size, size);
        boolean clicked = over && this.pressed(x, y, size, size);
        float k = this.hover(key, over);
        float s = this.f.scale();
        DuskWidgets.ringButton(this.f.bg(), icon, x, y, size, k, s, this.alpha);
        if (tooltip != null && k > 0.5f) {
            this.f.tooltip(tooltip, x + size * 0.5f, y + size + 8.0f * s);
        }
        return clicked;
    }

    public void panel(float x, float y, float w, float h, String title) {
        float s = this.f.scale();
        DuskWidgets.panel(this.f.bg(), x, y, w, h, s, this.alpha);
        if (title != null) {
            float plate = 24.0f * s;
            DuskWidgets.tag(this.f.bg(), this.f.tagFont(), x + w * 0.5f, y - plate * 0.5f, plate, title.toUpperCase(Locale.ROOT), s, this.alpha);
        }
    }

    public int row(Object key, float x, float y, float w, float h, boolean selected) {
        boolean over = this.hovered(x, y, w, h);
        int clicks = 0;
        if (over && this.pressed(x, y, w, h)) {
            clicks = this.doubleClick ? 2 : 1;
        }
        float k = this.hover(key, over);
        DuskWidgets.row(this.f.bg(), x, y, w, h, k, selected, this.f.scale(), this.alpha);
        return clicks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean textField(TextField field, float x, float y, float w, float h) {
        ImDrawList dl = this.f.bg();
        float s = this.f.scale();
        boolean over = this.hovered(x, y, w, h);
        if (over && this.click) {
            this.click = false;
        }
        float k = this.hover(field, over);
        field.focus = field.focus + ((field.active ? 1.0f : 0.0f) - field.focus) * Math.min(1.0f, this.f.dt() * 12.0f);
        DuskWidgets.field(dl, x, y, w, h, k, field.focus, s, this.alpha);
        ImFont font = this.f.bodyFont();
        float padX = 14.0f * s;
        float padY = Math.max(0.0f, (h - this.f.textHeight(font)) * 0.5f);
        if (this.blocked) {
            field.active = false;
            String text = field.text();
            boolean empty = text.isEmpty();
            String shown = empty ? field.placeholder : (field.password ? "*".repeat(text.codePointCount(0, text.length())) : text);
            dl.pushClipRect(x + padX, y, x + w - padX, y + h, true);
            this.f.text(dl, font, x + padX, y + padY, empty ? -7372954 : -923950, this.alpha, shown);
            dl.popClipRect();
            return false;
        }
        ImGui.setCursorScreenPos((float)x, (float)y);
        if (font != null) {
            ImGui.pushFont((ImFont)font);
        }
        ImGui.pushStyleVar((int)11, (float)padX, (float)padY);
        ImGui.pushStyleVar((int)13, (float)0.0f);
        ImGui.pushStyleVar((int)12, (float)0.0f);
        ImGui.pushStyleColor((int)7, (int)0);
        ImGui.pushStyleColor((int)8, (int)0);
        ImGui.pushStyleColor((int)9, (int)0);
        ImGui.pushStyleColor((int)53, (int)0);
        ImGui.pushStyleColor((int)0, (int)EgyptDraw.col(-923950, this.alpha));
        ImGui.pushStyleColor((int)1, (int)EgyptDraw.col(-7372954, this.alpha));
        ImGui.pushStyleColor((int)51, (int)EgyptDraw.col(-871878, 0.35f * this.alpha));
        try {
            ImGui.setNextItemWidth((float)w);
            if (field.focusRequested) {
                ImGui.setKeyboardFocusHere();
                field.focusRequested = false;
            }
            boolean submitted = ImGui.inputTextWithHint((String)field.id, (String)field.placeholder, (ImString)field.buffer, (int)field.flags(), (ImGuiInputTextCallback)field.limiter);
            field.active = ImGui.isItemActive();
            boolean bl = submitted;
            return bl;
        }
        finally {
            ImGui.popStyleColor((int)7);
            ImGui.popStyleVar((int)3);
            if (font != null) {
                ImGui.popFont();
            }
        }
    }

    public float beginScroll(Object key, float x, float y, float w, float h, float contentHeight) {
        Scroll area = this.scrolls.computeIfAbsent(key, k -> new Scroll());
        float s = this.f.scale();
        area.x = x;
        area.y = y;
        area.w = w;
        area.h = h;
        area.content = contentHeight;
        float max = Math.max(0.0f, contentHeight - h);
        if (this.wheel != 0.0f && this.hovered(x, y, w, h)) {
            area.target -= this.wheel * 64.0f * s;
            this.wheel = 0.0f;
        }
        if (max > 0.0f) {
            float[] thumb = this.thumb(area, s);
            if (Float.isNaN(area.grab)) {
                float grabX = thumb[0] + thumb[2] * 0.5f - 14.0f * s * 0.5f;
                if (this.pressed(grabX, thumb[1], 14.0f * s, thumb[3])) {
                    area.grab = this.f.mouseY() - thumb[1];
                }
            } else if (!this.mouseDown || this.blocked) {
                area.grab = Float.NaN;
            } else {
                float travel = h - 10.0f * s - thumb[3];
                float along = (this.f.mouseY() - area.grab - (y + 5.0f * s)) / Math.max(1.0f, travel);
                area.target = area.offset = along * max;
            }
        } else {
            area.grab = Float.NaN;
        }
        area.target = Math.max(0.0f, Math.min(max, area.target));
        area.offset += (area.target - area.offset) * Math.min(1.0f, this.f.dt() * 16.0f);
        if (Math.abs(area.target - area.offset) < 0.5f) {
            area.offset = area.target;
        }
        area.offset = Math.max(0.0f, Math.min(max, area.offset));
        this.openAreas.push(area);
        this.pushClip(x, y, x + w, y + h);
        return Math.round(area.offset);
    }

    public void endScroll() {
        Scroll area = this.openAreas.pop();
        this.popClip();
        float max = area.content - area.h;
        if (max <= 0.0f) {
            return;
        }
        float s = this.f.scale();
        float[] thumb = this.thumb(area, s);
        float trackY = area.y + 5.0f * s;
        float trackH = area.h - 10.0f * s;
        ImDrawList dl = this.f.bg();
        boolean over = this.hovered(thumb[0] - 14.0f * s * 0.5f, thumb[1], 14.0f * s, thumb[3]);
        float k = this.hover(area, over || !Float.isNaN(area.grab));
        dl.addRectFilled(thumb[0], trackY, thumb[0] + thumb[2], trackY + trackH, EgyptDraw.col(-871878, 0.12f * this.alpha), thumb[2]);
        dl.addRectFilled(thumb[0], thumb[1], thumb[0] + thumb[2], thumb[1] + thumb[3], EgyptDraw.col(EgyptDraw.mix(-871878, -11654, k), (0.55f + 0.35f * k) * this.alpha), thumb[2]);
    }

    public void pushClip(float x0, float y0, float x1, float y1) {
        float[] outer = this.clips.peek();
        if (outer != null) {
            x0 = Math.max(x0, outer[0]);
            y0 = Math.max(y0, outer[1]);
            x1 = Math.min(x1, outer[2]);
            y1 = Math.min(y1, outer[3]);
        }
        this.clips.push(new float[]{x0, y0, x1, y1});
        ImGui.pushClipRect((float)x0, (float)y0, (float)x1, (float)y1, (boolean)true);
    }

    public void popClip() {
        this.clips.pop();
        ImGui.popClipRect();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dialog(Dialog dialog) {
        ImDrawList dl = this.f.bg();
        float s = this.f.scale();
        float sw = this.f.width();
        float sh = this.f.height();
        if (Float.isNaN(dialog.openedAt)) {
            dialog.openedAt = this.f.time();
        }
        float fade = Math.min(1.0f, (this.f.time() - dialog.openedAt) / 0.18f);
        float a = 1.0f - (1.0f - fade) * (1.0f - fade) * (1.0f - fade);
        float saved = this.alpha;
        this.alpha = a;
        try {
            DuskScene.veil(dl, sw, sh, 0.62f * a);
            ImFont font = this.f.bodyFont();
            float pad = 22.0f * s;
            float gap = 11.0f * s;
            float bh = 42.0f * s;
            float w = Math.min(560.0f * s, sw - 48.0f * s);
            float inner = w - pad * 2.0f;
            List<String> lines = dialog.body.isEmpty() ? List.of() : DuskWidgets.wrap(font, dialog.body, inner);
            float lineH = this.f.textHeight(font) * 1.35f;
            float bodyH = (float)lines.size() * lineH;
            float contentH = dialog.content != null ? dialog.contentHeight * s : 0.0f;
            float top = pad + 24.0f * s * 0.5f;
            float h = top + bodyH + (contentH > 0.0f ? (lines.isEmpty() ? 0.0f : gap) + contentH : 0.0f) + gap * 2.0f + bh + pad;
            float x = (sw - w) * 0.5f;
            float y = (sh - h) * 0.5f + (1.0f - a) * 8.0f * s;
            this.panel(x, y, w, h, dialog.title);
            float cy = y + top;
            for (String line : lines) {
                this.f.text(dl, font, x + (w - this.f.textWidth(font, line)) * 0.5f, cy, -923950, 0.9f * a, line);
                cy += lineH;
            }
            if (dialog.content != null) {
                if (!lines.isEmpty()) {
                    cy += gap;
                }
                dialog.content.draw(this, dialog, x + pad, cy, inner, contentH);
            }
            List<Dialog.Choice> choices = dialog.choices();
            int n = choices.size();
            float bw = (inner - gap * (float)(n - 1)) / (float)Math.max(1, n);
            float by = y + h - pad - bh;
            for (int i = 0; i < n; ++i) {
                Dialog.Choice choice = choices.get(i);
                if (!this.button(choice, choice.label, x + pad + (float)i * (bw + gap), by, bw, bh)) continue;
                dialog.press(choice);
            }
        }
        finally {
            this.alpha = saved;
        }
    }

    public static void cartouche(ImDrawList dl, float x, float y, float w, float h, float k, float s, float alpha) {
        float r = h * 0.5f;
        float outer = Math.max(1.2f, 1.8f * s);
        float inner = Math.max(1.0f, s);
        float inset = 4.0f * s;
        dl.addRectFilled(x + 2.0f * s, y + 3.0f * s, x + w + 2.0f * s, y + h + 3.0f * s, EgyptDraw.col(-16777216, 0.5f * alpha), r);
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(EgyptDraw.mix(-16120059, -871878, k), (0.9f + 0.1f * k) * alpha), r);
        dl.addRect(x, y, x + w, y + h, EgyptDraw.col(-871878, alpha), r, 0, outer);
        dl.addRect(x + inset, y + inset, x + w - inset, y + h - inset, EgyptDraw.col(EgyptDraw.mix(-871878, -16777216, k), (0.55f + 0.25f * k) * alpha), r - inset, 0, inner);
    }

    public static void cartoucheButton(ImDrawList dl, ImFont font, float x, float y, float w, float h, String label, boolean enabled, float k, float s, float a) {
        float alpha = a * (enabled ? 1.0f : 0.45f);
        DuskWidgets.cartouche(dl, x, y, w, h, k, s, alpha);
        float size = font != null ? font.getFontSize() : 15.0f * s;
        int textColor = enabled ? EgyptDraw.mix(-1326998, -16777216, k) : -7372954;
        EgyptDraw.drawSpaced(dl, font, x + w * 0.5f, y + (h - size) * 0.5f + s, label, 3.0f * s, textColor, alpha, k < 0.5f);
    }

    public static void shenRing(ImDrawList dl, float cx, float cy, float r, float k, float s, float a) {
        float outer = Math.max(1.2f, 1.8f * s);
        float inner = Math.max(1.0f, s);
        dl.addCircleFilled(cx + 2.0f * s, cy + 3.0f * s, r, EgyptDraw.col(-16777216, 0.5f * a), 32);
        dl.addCircleFilled(cx, cy, r, EgyptDraw.col(EgyptDraw.mix(-16120059, -871878, k), (0.9f + 0.1f * k) * a), 32);
        dl.addCircle(cx, cy, r, EgyptDraw.col(-871878, a), 32, outer);
        dl.addCircle(cx, cy, r - 3.5f * s, EgyptDraw.col(EgyptDraw.mix(-871878, -16777216, k), (0.5f + 0.3f * k) * a), 32, inner);
        dl.addLine(cx - r * 0.75f, cy + r + 2.5f * s, cx + r * 0.75f, cy + r + 2.5f * s, EgyptDraw.col(-871878, a), outer);
    }

    public static void ringButton(ImDrawList dl, Icon icon, float x, float y, float size, float k, float s, float a) {
        float cx = x + size * 0.5f;
        float cy = y + size * 0.5f - 2.0f * s;
        float r = size * 0.44f;
        DuskWidgets.shenRing(dl, cx, cy, r, k, s, a);
        icon.draw(dl, cx, cy, r * 0.5f, EgyptDraw.col(EgyptDraw.mix(-1326998, -16777216, k), a), Math.max(1.2f, 1.6f * s));
    }

    public static float tag(ImDrawList dl, ImFont font, float cx, float y, float h, String text, float s, float a) {
        float spacing = 4.0f * s;
        float w = EgyptDraw.spacedWidth(font, text, spacing) + 28.0f * s;
        float x = cx - w * 0.5f;
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-871878, a), 2.0f * s);
        dl.addRect(x + 2.5f * s, y + 2.5f * s, x + w - 2.5f * s, y + h - 2.5f * s, EgyptDraw.col(-16777216, 0.45f * a), s, 0, Math.max(1.0f, s));
        float size = font != null ? font.getFontSize() : 15.0f * s;
        EgyptDraw.drawSpaced(dl, font, cx, y + (h - size) * 0.5f + s, text, spacing, -16777216, a, false);
        return w;
    }

    public static void panel(ImDrawList dl, float x, float y, float w, float h, float s, float a) {
        float r = 8.0f * s;
        float inset = 4.0f * s;
        dl.addRectFilled(x + 3.0f * s, y + 4.0f * s, x + w + 3.0f * s, y + h + 4.0f * s, EgyptDraw.col(-16777216, 0.45f * a), r);
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-16120059, a), r);
        dl.addRect(x, y, x + w, y + h, EgyptDraw.col(-871878, 0.9f * a), r, 0, Math.max(1.0f, 1.4f * s));
        dl.addRect(x + inset, y + inset, x + w - inset, y + h - inset, EgyptDraw.col(-871878, 0.3f * a), r - inset, 0, Math.max(1.0f, s));
    }

    public static void row(ImDrawList dl, float x, float y, float w, float h, float k, boolean selected, float s, float a) {
        float r = 6.0f * s;
        float line = Math.max(1.0f, s);
        if (selected) {
            float inset = 3.0f * s;
            dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-871878, (0.11f + 0.03f * k) * a), r);
            dl.addRect(x, y, x + w, y + h, EgyptDraw.col(-871878, 0.85f * a), r, 0, Math.max(1.0f, 1.2f * s));
            dl.addRect(x + inset, y + inset, x + w - inset, y + h - inset, EgyptDraw.col(-871878, 0.25f * a), r - inset, 0, line);
            return;
        }
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-16777216, 0.22f * a), r);
        if (k > 0.01f) {
            dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-871878, 0.06f * k * a), r);
            dl.addRect(x, y, x + w, y + h, EgyptDraw.col(-871878, 0.45f * k * a), r, 0, line);
        }
    }

    public static void field(ImDrawList dl, float x, float y, float w, float h, float k, float focus, float s, float a) {
        float r = 0.0f * s;
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-16777216, 0.35f * a), r);
        dl.addRect(x, y, x + w, y + h, EgyptDraw.col(EgyptDraw.mix(-923950, -871878, focus), (0.12f + 0.1f * k + 0.5f * focus) * a), r, 0, Math.max(1.0f, s));
    }

    public static void drawTooltip(ImDrawList dl, ImFont font, String text, float cx, float y, float screenW, float screenH, float s) {
        float size = font != null ? font.getFontSize() : 15.0f * s;
        float w = EgyptDraw.textWidth(font, text) + 18.0f * s;
        float h = size + 10.0f * s;
        float margin = 4.0f * s;
        float x = Math.max(margin, Math.min(screenW - w - margin, cx - w * 0.5f));
        y = Math.min(y, screenH - h - margin);
        dl.addRectFilled(x, y, x + w, y + h, EgyptDraw.col(-16120059, 0.96f), 3.0f * s);
        dl.addRect(x, y, x + w, y + h, EgyptDraw.col(-871878, 0.7f), 3.0f * s, 0, Math.max(1.0f, s));
        EgyptDraw.text(dl, font, x + 9.0f * s, y + 5.0f * s, text, EgyptDraw.col(-11654, 1.0f));
    }

    public static List<String> wrap(ImFont font, String text, float width) {
        ArrayList<String> lines = new ArrayList<String>();
        for (String paragraph : text.split("\n", -1)) {
            String line = "";
            for (String word : paragraph.split(" +")) {
                String candidate;
                if (word.isEmpty()) continue;
                String string = candidate = line.isEmpty() ? word : line + " " + word;
                if (EgyptDraw.textWidth(font, candidate) <= width) {
                    line = candidate;
                    continue;
                }
                if (!line.isEmpty()) {
                    lines.add(line);
                }
                while (word.length() > 1 && EgyptDraw.textWidth(font, word) > width) {
                    int cut = DuskWidgets.fitCount(font, word, width);
                    lines.add(word.substring(0, cut));
                    word = word.substring(cut);
                }
                line = word;
            }
            lines.add(line);
        }
        return lines;
    }

    public static String fit(ImFont font, String text, float width) {
        if (EgyptDraw.textWidth(font, text) <= width) {
            return text;
        }
        String dots = "...";
        int n = DuskWidgets.fitCount(font, text, width - EgyptDraw.textWidth(font, dots));
        return text.substring(0, n).stripTrailing() + dots;
    }

    private static int fitCount(ImFont font, String text, float width) {
        int lo = 1;
        int hi = text.length();
        while (lo < hi) {
            int mid = lo + hi + 1 >>> 1;
            if (EgyptDraw.textWidth(font, text.substring(0, mid)) <= width) {
                lo = mid;
                continue;
            }
            hi = mid - 1;
        }
        return Math.min(lo, text.length());
    }

    private float[] thumb(Scroll area, float s) {
        float inset = 5.0f * s;
        float barW = 3.0f * s;
        float trackH = area.h - 2.0f * inset;
        float thumbH = Math.min(trackH, Math.max(28.0f * s, trackH * area.h / area.content));
        float max = Math.max(1.0f, area.content - area.h);
        float y = area.y + inset + (trackH - thumbH) * Math.max(0.0f, Math.min(1.0f, area.offset / max));
        return new float[]{area.x + area.w - inset - barW, y, barW, thumbH};
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Anim {
        float value;
        int frame;

        private Anim() {
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Icon {
        public void draw(ImDrawList var1, float var2, float var3, float var4, int var5, float var6);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class TextField {
        private static int nextId;
        private final String id = "##dusk_field_" + nextId++;
        private final int maxLength;
        private final ImString buffer;
        private final ImGuiInputTextCallback limiter;
        private String placeholder = "";
        private boolean password;
        private boolean focusRequested;
        private boolean active;
        private float focus;

        public TextField(int maxLength) {
            this.maxLength = maxLength;
            this.buffer = new ImString(maxLength * 3);
            this.limiter = new ImGuiInputTextCallback(){

                public void accept(ImGuiInputTextCallbackData data) {
                    this.limit(data);
                }
            };
        }

        public TextField placeholder(String text) {
            this.placeholder = text;
            return this;
        }

        public TextField password(boolean masked) {
            this.password = masked;
            return this;
        }

        public TextField allowedChars(String chars) {
            this.buffer.inputData.allowedChars = chars;
            return this;
        }

        public String text() {
            return this.buffer.get();
        }

        public void setText(String text) {
            if (text.codePointCount(0, text.length()) > this.maxLength) {
                text = text.substring(0, text.offsetByCodePoints(0, this.maxLength));
            }
            this.buffer.set(text);
        }

        public void focus() {
            this.focusRequested = true;
        }

        public boolean focused() {
            return this.active;
        }

        public int maxLength() {
            return this.maxLength;
        }

        private int flags() {
            return 0x400040 | (this.password ? 1024 : 0);
        }

        private void limit(ImGuiInputTextCallbackData data) {
            int caret;
            String text = data.getBuf();
            int over = text.codePointCount(0, text.length()) - this.maxLength;
            if (over <= 0) {
                return;
            }
            byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
            String before = new String(utf8, 0, caret = Math.min(Math.max(0, data.getCursorPos()), utf8.length), StandardCharsets.UTF_8);
            int cut = Math.min(over, before.codePointCount(0, before.length()));
            if (cut > 0) {
                int start = before.substring(0, before.offsetByCodePoints(before.length(), -cut)).getBytes(StandardCharsets.UTF_8).length;
                data.deleteChars(start, caret - start);
                over -= cut;
            }
            if (over > 0) {
                String rest = data.getBuf();
                int keep = rest.substring(0, rest.offsetByCodePoints(0, this.maxLength)).getBytes(StandardCharsets.UTF_8).length;
                data.deleteChars(keep, data.getBufTextLen() - keep);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Scroll {
        float offset;
        float target;
        float grab = Float.NaN;
        float x;
        float y;
        float w;
        float h;
        float content;

        private Scroll() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Dialog {
        private final String title;
        private final String body;
        private final List<Choice> actions = new ArrayList<Choice>();
        private Choice cancel;
        private Content content;
        private float contentHeight;
        private boolean closed;
        private float openedAt = Float.NaN;

        public Dialog(String title, String body) {
            this.title = title;
            this.body = body;
        }

        public Dialog action(String label, Runnable run) {
            this.actions.add(new Choice(label, run, null));
            return this;
        }

        public Dialog submit(String label, BooleanSupplier run) {
            this.actions.add(new Choice(label, null, run));
            return this;
        }

        public Dialog cancel(String label) {
            return this.cancel(label, null);
        }

        public Dialog cancel(String label, Runnable run) {
            this.cancel = new Choice(label, run, null);
            return this;
        }

        public Dialog content(float height, Content content) {
            this.contentHeight = height;
            this.content = content;
            return this;
        }

        public void confirm() {
            if (!this.actions.isEmpty()) {
                this.press(this.actions.get(0));
            }
        }

        public void dismiss() {
            if (this.cancel != null) {
                this.press(this.cancel);
            } else {
                this.close();
            }
        }

        public void close() {
            this.closed = true;
        }

        public boolean isClosed() {
            return this.closed;
        }

        private List<Choice> choices() {
            if (this.cancel == null) {
                return this.actions;
            }
            ArrayList<Choice> all = new ArrayList<Choice>(this.actions);
            all.add(this.cancel);
            return all;
        }

        private void press(Choice choice) {
            if (this.closed) {
                return;
            }
            if (choice.submit != null) {
                if (choice.submit.getAsBoolean()) {
                    this.closed = true;
                }
                return;
            }
            if (choice.run != null) {
                choice.run.run();
            }
            this.closed = true;
        }

        @Environment(value=EnvType.CLIENT)
        private record Choice(String label, Runnable run, BooleanSupplier submit) {
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        public static interface Content {
            public void draw(DuskWidgets var1, Dialog var2, float var3, float var4, float var5, float var6);
        }
    }
}

