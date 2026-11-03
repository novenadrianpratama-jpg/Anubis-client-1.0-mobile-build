/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.ThemeImages;
import net.anubis.client.gui.mainmenu.MenuContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class DuskEmblem {
    private static final String NAME = "ANUBIS CLIENT";
    private static final float NAME_TRACK = 6.0f;
    private static final float[] ANKH = new float[]{0.46f, 0.32285714f, 0.5371429f, 0.4457143f};
    private static final float[] LEFT_EYE = new float[]{0.40857142f, 0.43714285f, 0.4485714f, 0.47714287f};
    private static final float[] RIGHT_EYE = new float[]{0.55142856f, 0.4457143f, 0.5885714f, 0.48f};
    private static final int HALO_BLUR = 5;
    private static final int GLOW_BLUR = 3;
    private static final float CYCLE = 8.0f;
    private static final float FIRST_PASS = 1.1f;
    private static final float HEAD_PASS = 0.9f;
    private static final float NAME_START = 0.55f;
    private static final float NAME_PASS = 0.8f;
    private static final float BUTTON_START = 1.2f;
    private static final float BUTTON_STAGGER = 0.25f;
    private static final float BUTTON_PASS = 0.8f;
    private static final float EYES_AT = 4.2f;
    private static final float EYES_FOR = 0.7f;
    private static final float ANKH_BREATH = 3.5f;
    static final int LIGHT = -3633;

    private DuskEmblem() {
    }

    static void draw(MenuContext c, float cx, float cy, float size, float nameY) {
        float t = c.time();
        float cycle = DuskEmblem.cycle(t);
        DuskEmblem.head(c, cx, cy, size, t, cycle);
        DuskEmblem.name(c, cx, nameY, cycle);
    }

    private static float cycle(float t) {
        return t < 1.1f ? -1.0f : (t - 1.1f) % 8.0f;
    }

    static float buttonPass(MenuContext c, int n) {
        float cycle = DuskEmblem.cycle(c.time());
        if (cycle < 0.0f) {
            return -1.0f;
        }
        float p = (cycle - 1.2f - (float)n * 0.25f) / 0.8f;
        return p < 0.0f || p > 1.0f ? -1.0f : p;
    }

    static float breath(float t) {
        return 0.5f - 0.5f * (float)Math.cos((double)t * Math.PI * 2.0 / 3.5);
    }

    private static void head(MenuContext c, float cx, float cy, float size, float t, float cycle) {
        ThemeImages.Texture head = c.head();
        if (head == null) {
            return;
        }
        ImDrawList dl = c.bg();
        float in = c.intro(0.0f);
        float x0 = cx - (size *= 0.96f + 0.04f * in) * 0.5f;
        float y0 = cy - size * 0.5f;
        float px = size / (float)head.width();
        ThemeImages.Texture halo = c.headBlur(5);
        float pad = 10.0f * px;
        c.image(dl, halo, x0 - pad, y0 - pad, x0 + size + pad, y0 + size + pad, -16777216, 0.7f * in);
        c.image(dl, head, x0, y0, x0 + size, y0 + size, -1, in);
        DuskEmblem.lightUp(c, ANKH, x0, y0, size, px, in * (0.2f + 0.5f * DuskEmblem.breath(t)));
        float eyes = cycle < 0.0f ? 0.0f : DuskEmblem.flash((cycle - 4.2f) / 0.7f);
        DuskEmblem.lightUp(c, LEFT_EYE, x0, y0, size, px, in * eyes);
        DuskEmblem.lightUp(c, RIGHT_EYE, x0, y0, size, px, in * eyes);
        if (cycle >= 0.0f && cycle < 0.9f) {
            DuskEmblem.sweep(c, x0, y0, size, DuskEmblem.smooth(cycle / 0.9f), in);
        }
    }

    private static void lightUp(MenuContext c, float[] box, float x0, float y0, float size, float px, float amount) {
        if (amount <= 0.01f) {
            return;
        }
        ImDrawList dl = c.bg();
        float bx0 = x0 + box[0] * size;
        float by0 = y0 + box[1] * size;
        float bx1 = x0 + box[2] * size;
        float by1 = y0 + box[3] * size;
        ThemeImages.Texture glow = c.headBlur(3);
        float pad = 6.0f * px;
        dl.pushClipRect(bx0 - pad, by0 - pad, bx1 + pad, by1 + pad, true);
        c.image(dl, glow, x0 - pad, y0 - pad, x0 + size + pad, y0 + size + pad, -11654, 0.8f * amount);
        dl.popClipRect();
        dl.pushClipRect(bx0, by0, bx1, by1, true);
        c.image(dl, c.headMask(), x0, y0, x0 + size, y0 + size, -3633, 0.6f * amount);
        dl.popClipRect();
    }

    private static void sweep(MenuContext c, float x0, float y0, float size, float progress, float alpha) {
        ThemeImages.Texture mask = c.headMask();
        if (mask == null) {
            return;
        }
        ImDrawList dl = c.bg();
        float half = size * 0.14f;
        float lean = size * 0.35f;
        float top = x0 - half + progress * (size + half * 2.0f + lean);
        float y1 = y0 + size;
        float[] xs = new float[]{top - half, top, top + half, top - half - lean, top - lean, top + half - lean};
        int edge = EgyptDraw.col(-3633, 0.0f);
        int centre = EgyptDraw.col(-3633, 0.9f * alpha);
        dl.pushClipRect(x0, y0, x0 + size, y1, true);
        dl.pushTextureID((long)mask.id());
        dl.primReserve(12, 6);
        int first = dl.getVtxCurrentIdx();
        for (int i = 0; i < 6; ++i) {
            float y = i < 3 ? y0 : y1;
            dl.primWriteVtx(xs[i], y, (xs[i] - x0) / size, (y - y0) / size, i % 3 == 1 ? centre : edge);
        }
        DuskEmblem.writeQuad(dl, first, first + 1, first + 4, first + 3);
        DuskEmblem.writeQuad(dl, first + 1, first + 2, first + 5, first + 4);
        dl.popTextureID();
        dl.popClipRect();
    }

    private static void name(MenuContext c, float cx, float y, float cycle) {
        ImDrawList dl = c.bg();
        ImFont font = c.nameFont();
        float s = c.scale();
        float track = 6.0f * s;
        float width = c.spacedWidth(font, NAME, track);
        float left = cx - width * 0.5f;
        float light = cycle < 0.0f ? -1.0f : (cycle - 0.55f) / 0.8f;
        float x = left;
        for (int i = 0; i < NAME.length(); ++i) {
            String glyph = String.valueOf(NAME.charAt(i));
            float w = c.textWidth(font, glyph);
            float a = c.intro(0.3f + (float)i * 0.035f);
            float ly = y + (1.0f - a) * 6.0f * s;
            float at = (x + w * 0.5f - left) / width;
            float k = light < 0.0f || light > 1.0f ? 0.0f : DuskEmblem.smooth(Math.max(0.0f, 1.0f - Math.abs(light * 1.4f - 0.2f - at) / 0.16f));
            c.text(dl, font, x + s, ly + 1.5f * s, -16777216, 0.7f * a, glyph);
            c.text(dl, font, x, ly, EgyptDraw.mix(-871878, -3633, k), a, glyph);
            x += w + track;
        }
    }

    private static float flash(float x) {
        if (x <= 0.0f || x >= 1.0f) {
            return 0.0f;
        }
        return x < 0.15f ? DuskEmblem.smooth(x / 0.15f) : DuskEmblem.smooth(1.0f - (x - 0.15f) / 0.85f);
    }

    static float smooth(float x) {
        x = Math.max(0.0f, Math.min(1.0f, x));
        return x * x * (3.0f - 2.0f * x);
    }

    private static void writeQuad(ImDrawList dl, int a, int b, int c, int d) {
        dl.primWriteIdx(a);
        dl.primWriteIdx(b);
        dl.primWriteIdx(c);
        dl.primWriteIdx(a);
        dl.primWriteIdx(c);
        dl.primWriteIdx(d);
    }
}

