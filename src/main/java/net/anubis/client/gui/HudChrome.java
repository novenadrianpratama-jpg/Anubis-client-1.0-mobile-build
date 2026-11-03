/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import net.anubis.client.config.Surface;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.SurfaceBackground;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class HudChrome {
    static final int TEXT_VALUE = GuiPalette.abgr(-657929);
    static final int TEXT_NAME = GuiPalette.abgr(-1447442);
    static final int TEXT_LABEL = GuiPalette.abgr(-6052949);
    static final int WARN = GuiPalette.abgr(-47814);
    static final int BORDER = 0x17FFFFFF;
    static final int DIVIDER = 0x1FFFFFFF;
    static final int TRACK = 0x1AFFFFFF;
    static final int CHIP_FILL = GuiPalette.abgr(-350873571);
    private static final int TEXT_GUARD = -1946157056;
    private static final Long2FloatOpenHashMap DIGIT_CELLS = new Long2FloatOpenHashMap();
    private static final String[] ASCII = new String[128];

    private HudChrome() {
    }

    static void card(ImDrawList dl, Surface surface, float x, float y, float w, float h, float r, float alpha, boolean shadow, float scale) {
        if (alpha <= 0.001f) {
            return;
        }
        if (shadow) {
            HudChrome.shadow(dl, x, y, w, h, r, alpha, scale);
        }
        dl.addRectFilled(x, y, x + w, y + h, HudChrome.scaleAlpha(GuiPalette.fill(surface), alpha), r);
        SurfaceBackground.draw(dl, surface, x, y, w, h, r, alpha);
        dl.addRect(x, y, x + w, y + h, HudChrome.scaleAlpha(0x17FFFFFF, alpha), r, 0, 1.0f);
    }

    static void shadow(ImDrawList dl, float x, float y, float w, float h, float r, float alpha, float scale) {
        float s = Math.max(1.0f, scale);
        dl.addRectFilled(x - s, y - 0.5f * s, x + w + s, y + h + 1.5f * s, HudChrome.scaleAlpha(0x2E000000, alpha), r + s);
        dl.addRectFilled(x - 2.0f * s, y + 0.5f * s, x + w + 2.0f * s, y + h + 3.5f * s, HudChrome.scaleAlpha(0x17000000, alpha), r + 2.0f * s);
        dl.addRectFilled(x - 3.0f * s, y + 1.5f * s, x + w + 3.0f * s, y + h + 6.0f * s, HudChrome.scaleAlpha(0xC000000, alpha), r + 3.0f * s);
    }

    static void divider(ImDrawList dl, float x, float centerY, float height, float alpha) {
        float px = Math.round(x);
        float top = Math.round(centerY - height * 0.5f);
        dl.addRectFilled(px, top, px + 1.0f, top + (float)Math.round(height), HudChrome.scaleAlpha(0x1FFFFFFF, alpha));
    }

    static boolean needsTextGuard(Surface surface) {
        return surface != null && (surface.opacity.get() < 0.55f || surface.isImage());
    }

    static int px(float base, float scale) {
        return Math.max(6, Math.round(base * scale));
    }

    static float width(ImFont font, int size, String text) {
        if (font == null || text.isEmpty()) {
            return (float)(text.length() * size) * 0.55f;
        }
        return font.calcTextSizeA((float)((float)size), (float)Float.MAX_VALUE, (float)0.0f, (String)text).x;
    }

    static float ascent(ImFont font, int size) {
        if (font == null || font.getFontSize() <= 0.0f) {
            return (float)size * 0.77f;
        }
        return font.getAscent() * (float)size / font.getFontSize();
    }

    static float baselineOffset(ImFont big, int bigSize, ImFont small, int smallSize) {
        return Math.round(HudChrome.ascent(big, bigSize) - HudChrome.ascent(small, smallSize));
    }

    static void text(ImDrawList dl, ImFont font, int size, float x, float y, int color, String text, boolean guard) {
        if (text.isEmpty() || (color >>> 24 & 0xFF) == 0) {
            return;
        }
        if (guard) {
            HudChrome.raw(dl, font, size, x + 1.0f, y + 1.0f, HudChrome.scaleAlpha(-1946157056, (float)(color >>> 24 & 0xFF) / 255.0f), text);
        }
        HudChrome.raw(dl, font, size, x, y, color, text);
    }

    private static void raw(ImDrawList dl, ImFont font, int size, float x, float y, int color, String text) {
        if (font != null) {
            dl.addText(font, size, x, y, color, text);
        } else {
            dl.addText(x, y, color, text);
        }
    }

    private static float digitCell(ImFont font, int size) {
        if (font == null) {
            return (float)size * 0.6f;
        }
        long key = (long)System.identityHashCode(font) << 16 ^ (long)size;
        float cached = DIGIT_CELLS.get(key);
        if (!Float.isNaN(cached)) {
            return cached;
        }
        float cell = 0.0f;
        for (char c = '0'; c <= '9'; c = (char)(c + '\u0001')) {
            cell = Math.max(cell, HudChrome.advance(font, size, c));
        }
        if (DIGIT_CELLS.size() > 64) {
            DIGIT_CELLS.clear();
        }
        DIGIT_CELLS.put(key, cell);
        return cell;
    }

    private static float advance(ImFont font, int size, char c) {
        return font.getCharAdvance((int)c) * (float)size / font.getFontSize();
    }

    static float tabularWidth(ImFont font, int size, String text) {
        if (font == null) {
            return HudChrome.width(null, size, text);
        }
        float cell = HudChrome.digitCell(font, size);
        float w = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            w += Character.isDigit(c) ? cell : HudChrome.advance(font, size, c);
        }
        return w;
    }

    static void tabular(ImDrawList dl, ImFont font, int size, float x, float y, int color, String text, boolean guard) {
        if (font == null) {
            HudChrome.text(dl, null, size, x, y, color, text, guard);
            return;
        }
        float cell = HudChrome.digitCell(font, size);
        float cx = x;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            float adv = HudChrome.advance(font, size, c);
            if (Character.isDigit(c)) {
                HudChrome.text(dl, font, size, cx + (cell - adv) * 0.5f, y, color, HudChrome.glyph(c), guard);
                cx += cell;
                continue;
            }
            if (c != ' ') {
                HudChrome.text(dl, font, size, cx, y, color, HudChrome.glyph(c), guard);
            }
            cx += adv;
        }
    }

    private static String glyph(char c) {
        return c < ASCII.length ? ASCII[c] : String.valueOf(c);
    }

    static int scaleAlpha(int color, float alphaScale) {
        int alpha = color >>> 24 & 0xFF;
        int scaled = Math.round((float)alpha * Math.max(0.0f, Math.min(1.0f, alphaScale)));
        return color & 0xFFFFFF | scaled << 24;
    }

    static {
        DIGIT_CELLS.defaultReturnValue(Float.NaN);
        for (char c = '\u0000'; c < ASCII.length; c = (char)(c + '\u0001')) {
            HudChrome.ASCII[c] = String.valueOf(c);
        }
    }
}

