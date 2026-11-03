/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10868
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_408
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_9296
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL11
 */
package net.anubis.client.gui;

import com.mojang.blaze3d.textures.GpuTexture;
import imgui.ImDrawList;
import imgui.ImFont;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.Surface;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.SurfaceBackground;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.client.HudModule;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10868;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_9296;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
public final class HudPanel {
    public static final int LABEL = -6052949;
    public static final int VALUE = -1;
    public static final int WARN = -47814;
    public static final int GOOD = -13445301;
    public static final int CAUTION = -19648;
    private static final float GRIP = 14.0f;
    private static final float HEADER_H = 20.0f;
    private static final float ROW_H = 22.0f;
    private static final float PAD = 8.0f;
    private static final float FACE = 16.0f;
    private static final float GAP_FACE = 6.0f;
    private static final float GAP_COUNT = 5.0f;
    private static final float GAP_RIGHT = 14.0f;
    private static final float ROWS_RADIUS = 6.0f;
    private static final float SKIN_FACE_U = 0.125f;
    private static final float SKIN_HAT_U = 0.625f;
    private static final float SKIN_TOP_V = 0.125f;
    private static final float SKIN_PART = 0.125f;
    private static final Map<String, class_9296> PROFILES = new HashMap<String, class_9296>();
    private static final Set<GpuTexture> NEAREST = Collections.newSetFromMap(new WeakHashMap());
    private final NumberSetting x;
    private final NumberSetting y;
    private final NumberSetting scale;
    private final NumberSetting opacity;
    private boolean dragging;
    private boolean resizing;
    private boolean wasDown;
    private float grabX;
    private float grabY;
    private float scaleAtGrab;
    private float widthAtGrab;
    private float alpha = 1.0f;

    public HudPanel(NumberSetting x, NumberSetting y, NumberSetting scale, NumberSetting opacity) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.opacity = opacity;
        x.visibleWhen(() -> false);
        y.visibleWhen(() -> false);
    }

    public static NumberSetting coordinate(String name) {
        return new NumberSetting(name, -1.0f, -1.0f, 10000.0f).step(1.0f);
    }

    public static NumberSetting opacitySetting() {
        return new NumberSetting("Opacity", 100.0f, 10.0f, 100.0f).step(5.0f);
    }

    public float[] place(ImDrawList dl, float w, float h, float screenW, float screenH, float defaultX, float defaultY) {
        class_310 mc = class_310.method_1551();
        this.alpha = Math.max(0.0f, Math.min(1.0f, this.opacity.get() / 100.0f));
        float px = this.x.get() < 0.0f ? defaultX : this.x.get();
        float py = this.y.get() < 0.0f ? defaultY : this.y.get();
        boolean chat = mc.field_1755 instanceof class_408;
        boolean down = chat && GLFW.glfwGetMouseButton((long)mc.method_22683().method_4490(), (int)0) == 1;
        float mx = (float)(mc.field_1729.method_1603() * (double)screenW / (double)Math.max(1, mc.method_22683().method_4480()));
        float my = (float)(mc.field_1729.method_1604() * (double)screenH / (double)Math.max(1, mc.method_22683().method_4507()));
        if (down && !this.wasDown) {
            if (this.scale != null && mx >= px + w - 14.0f && mx <= px + w && my >= py + h - 14.0f && my <= py + h) {
                this.resizing = true;
                this.scaleAtGrab = this.scale.get();
                this.widthAtGrab = Math.max(1.0f, w);
                this.grabX = px + w - mx;
            } else if (mx >= px && mx <= px + w && my >= py && my <= py + h) {
                this.dragging = true;
                this.grabX = mx - px;
                this.grabY = my - py;
            }
        }
        if (this.resizing) {
            this.scale.set(Math.max(this.scale.min(), Math.min(this.scale.max(), this.scaleAtGrab * (mx + this.grabX - px) / this.widthAtGrab)));
        }
        if (this.dragging) {
            px = mx - this.grabX;
            py = my - this.grabY;
        }
        px = Math.max(0.0f, Math.min(px, screenW - w));
        py = Math.max(0.0f, Math.min(py, screenH - h));
        if (!down) {
            if (this.dragging) {
                this.x.set(Math.round(px));
                this.y.set(Math.round(py));
            }
            this.dragging = false;
            this.resizing = false;
        }
        this.wasDown = down;
        if (chat) {
            dl.addRect(px - 1.0f, py - 1.0f, px + w + 1.0f, py + h + 1.0f, GuiPalette.abgr(this.dragging || this.resizing ? -1056964609 : 0x60FFFFFF), 8.0f, 0, 1.0f);
            if (this.scale != null) {
                for (int i = 1; i <= 3; ++i) {
                    float o = (float)i * 4.0f;
                    dl.addLine(px + w - o, py + h - 2.0f, px + w - 2.0f, py + h - o, GuiPalette.abgr(-788529153), 1.5f);
                }
            }
        }
        return new float[]{px, py};
    }

    public void card(ImDrawList dl, String title, String value, int valueColor, List<Line> lines, String surfaceId, float screenW, float screenH, float across, float defaultY) {
        ImGuiManager m = ImGuiManager.getInstance();
        float s = (this.scale == null ? 1.0f : this.scale.get()) * m.uiScale();
        ImFont labelFont = m.hudLabelFont();
        ImFont textFont = m.hudTextFont();
        int labelPx = HudChrome.px(14.0f, s);
        int textPx = HudChrome.px(17.0f, s);
        float pad = 8.0f * s;
        float headerH = 20.0f * s;
        float rowH = 22.0f * s;
        float face = HudPanel.faceSize(s);
        float faceGap = 6.0f * s;
        boolean faces = false;
        for (Line line : lines) {
            faces |= line.face() != 0;
        }
        float textX = faces ? face + faceGap : 0.0f;
        float titleW = HudChrome.width(labelFont, labelPx, title);
        float inner = titleW + (value.isEmpty() ? 0.0f : 5.0f * s + HudChrome.width(textFont, textPx, value));
        for (Line line : lines) {
            float lineW = textX + HudChrome.width(textFont, textPx, line.text());
            if (!line.right().isEmpty()) {
                lineW += 14.0f * s + HudChrome.width(labelFont, labelPx, line.right());
            }
            inner = Math.max(inner, lineW);
        }
        float w = (float)Math.ceil(inner + pad * 2.0f);
        float h = Math.round(headerH + rowH * (float)lines.size());
        float margin = 10.0f * s;
        float[] at = this.place(dl, w, h, screenW, screenH, margin + (screenW - w - margin * 2.0f) * across, defaultY);
        float x = Math.round(at[0]);
        float y = Math.round(at[1]);
        Surface surface = HudTheme.get().forElementId(surfaceId);
        float r = Math.min(HudTheme.get().rounding.get(), 6.0f) * s;
        HudModule hud = ModuleRegistry.get(HudModule.class);
        if (hud == null || hud.shadow()) {
            HudChrome.shadow(dl, x, y, w, h, r, this.alpha, s);
        }
        if (surface != null) {
            dl.addRectFilled(x, y, x + w, y + h, HudChrome.scaleAlpha(GuiPalette.fill(surface), this.alpha), r);
            SurfaceBackground.draw(dl, surface, x, y, w, h, r, this.alpha);
        } else {
            dl.addRectFilled(x, y, x + w, y + h, HudChrome.scaleAlpha(HudChrome.CHIP_FILL, this.alpha), r);
        }
        float labelOffset = HudChrome.baselineOffset(textFont, textPx, labelFont, labelPx);
        float headTop = y + (float)Math.floor((headerH - (float)textPx) * 0.5f);
        HudChrome.text(dl, labelFont, labelPx, x + pad, headTop + labelOffset, this.abgr(-6052949), title, true);
        if (!value.isEmpty()) {
            HudChrome.text(dl, textFont, textPx, x + pad + titleW + 5.0f * s, headTop, this.abgr(valueColor), value, true);
        }
        float textInset = (float)Math.floor((rowH - (float)textPx) * 0.5f);
        for (int i = 0; i < lines.size(); ++i) {
            Line line = lines.get(i);
            float top = Math.round(y + headerH + rowH * (float)i);
            float ty = top + textInset;
            if (line.face() != 0) {
                this.drawFace(dl, line.face(), Math.round(x + pad), Math.round(top + (rowH - face) * 0.5f), face, s);
            }
            HudChrome.text(dl, textFont, textPx, x + pad + textX, ty, this.abgr(line.color()), line.text(), true);
            if (line.right().isEmpty()) continue;
            float rx = x + w - pad - HudChrome.width(labelFont, labelPx, line.right());
            HudChrome.text(dl, labelFont, labelPx, rx, ty + labelOffset, this.abgr(line.rightColor()), line.right(), true);
        }
    }

    private static float faceSize(float s) {
        int size = Math.round((float)Math.round(16.0f * s) / 8.0f) * 8;
        if ((float)size > 22.0f * s - 4.0f * s) {
            size -= 8;
        }
        return Math.max(8, size);
    }

    public void drawFace(ImDrawList dl, int texture, float fx, float fy, float size, float s) {
        float r = Math.max(1.0f, (float)Math.round(2.0f * s));
        int tint = this.abgr(-1);
        dl.addImageRounded((long)texture, fx, fy, fx + size, fy + size, 0.125f, 0.125f, 0.25f, 0.25f, tint, r, 240);
        dl.addImageRounded((long)texture, fx, fy, fx + size, fy + size, 0.625f, 0.125f, 0.75f, 0.25f, tint, r, 240);
        dl.addRect(fx, fy, fx + size, fy + size, HudChrome.scaleAlpha(0x17FFFFFF, this.alpha), r, 0, 1.0f);
    }

    public static int face(String name) {
        class_10868 gl;
        GpuTexture texture;
        class_310 mc = class_310.method_1551();
        class_634 connection = mc.method_1562();
        class_640 info = connection == null ? null : connection.method_2874(name);
        class_2960 skin = info != null ? info.method_52810().comp_1626().comp_3627() : mc.method_73362().method_73495(PROFILES.computeIfAbsent(name.toLowerCase(Locale.ROOT), key -> class_9296.method_74889((String)name))).method_73503().comp_1626().comp_3627();
        try {
            texture = mc.method_1531().method_4619(skin).method_68004();
        }
        catch (IllegalStateException notUploaded) {
            return 0;
        }
        if (!(texture instanceof class_10868) || (gl = (class_10868)texture).isClosed()) {
            return 0;
        }
        if (NEAREST.add(texture)) {
            int bound = GL11.glGetInteger((int)32873);
            GL11.glBindTexture((int)3553, (int)gl.method_68427());
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL11.glBindTexture((int)3553, (int)bound);
        }
        return gl.method_68427();
    }

    public int abgr(int argb) {
        int a = Math.round((float)(argb >>> 24 & 0xFF) * this.alpha);
        return GuiPalette.abgr(a << 24 | argb & 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    public record Line(int face, String text, int color, String right, int rightColor) {
        public Line(String text, int color) {
            this(0, text, color, "", -6052949);
        }
    }
}

