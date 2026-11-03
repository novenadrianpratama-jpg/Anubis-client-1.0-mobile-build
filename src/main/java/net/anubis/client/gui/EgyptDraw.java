/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.ThemeImages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class EgyptDraw {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    public static final int GOLD = -871878;
    public static final int GOLD_LIGHT = -11654;
    public static final int GOLD_TEXT = -1326998;
    public static final int LACQUER = -16120059;
    public static final int SAND = -2513573;
    public static final int LAPIS = -13738055;
    public static final int LAPIS_LIGHT = -12750874;
    public static final int MALACHITE = -12600956;
    public static final int CARNELIAN = -2537681;
    public static final int TEXT = -923950;
    public static final int MUTED = -7372954;
    public static final int BLACK = -16777216;
    private static final Map<String, ThemeImages.Texture> TEXTURES = new HashMap<String, ThemeImages.Texture>();

    private EgyptDraw() {
    }

    public static int col(int argb, float alpha) {
        int a = Math.round((float)(argb >>> 24 & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return GuiPalette.abgr(a << 24 | argb & 0xFFFFFF);
    }

    public static int mix(int from, int to, float k) {
        k = Math.max(0.0f, Math.min(1.0f, k));
        int a = Math.round((float)(from >>> 24 & 0xFF) + (float)((to >>> 24 & 0xFF) - (from >>> 24 & 0xFF)) * k);
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * k);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * k);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * k);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static void glow(ImDrawList dl, float cx, float cy, float radius, int argb, float alpha) {
        int layers = 18;
        for (int i = 0; i < layers; ++i) {
            float f = 1.0f - (float)i / (float)layers;
            dl.addCircleFilled(cx, cy, radius * f, EgyptDraw.col(argb, alpha / (float)layers * 2.2f), 48);
        }
    }

    public static void sunDisk(ImDrawList dl, float cx, float cy, float r, float spin, float s, float a) {
        EgyptDraw.glow(dl, cx, cy, r * 1.9f, -1466054, 0.16f * a);
        dl.addCircleFilled(cx, cy, r, EgyptDraw.col(-14018806, 0.55f * a), 96);
        dl.addCircle(cx, cy, r, EgyptDraw.col(-871878, 0.55f * a), 96, Math.max(1.0f, 1.4f * s));
        dl.addCircle(cx, cy, r * 0.93f, EgyptDraw.col(-871878, 0.22f * a), 96, Math.max(1.0f, s));
        int rays = 48;
        for (int i = 0; i < rays; ++i) {
            float ang = spin + (float)((double)i * Math.PI * 2.0 / (double)rays);
            float inner = r * 1.07f;
            float outer = r * (i % 2 == 0 ? 1.34f : 1.2f);
            float cos = (float)Math.cos(ang);
            float sin = (float)Math.sin(ang);
            dl.addLine(cx + cos * inner, cy + sin * inner, cx + cos * outer, cy + sin * outer, EgyptDraw.col(-871878, (i % 2 == 0 ? 0.28f : 0.16f) * a), Math.max(1.0f, 1.3f * s));
        }
    }

    public static void text(ImDrawList dl, ImFont font, float x, float y, String text, int abgr) {
        if (font != null) {
            dl.addText(font, Math.round(font.getFontSize()), x, y, abgr, text);
        } else {
            dl.addText(x, y, abgr, text);
        }
    }

    public static float textWidth(ImFont font, String text) {
        if (font == null) {
            return (float)text.length() * 7.0f;
        }
        return font.calcTextSizeAX(font.getFontSize(), Float.MAX_VALUE, 0.0f, text);
    }

    public static float spacedWidth(ImFont font, String text, float spacing) {
        float w = 0.0f;
        for (int i = 0; i < text.length(); ++i) {
            w += EgyptDraw.textWidth(font, String.valueOf(text.charAt(i)));
        }
        return w + spacing * (float)Math.max(0, text.length() - 1);
    }

    public static void drawSpaced(ImDrawList dl, ImFont font, float cx, float y, String text, float spacing, int argb, float a, boolean shadow) {
        float x = cx - EgyptDraw.spacedWidth(font, text, spacing) * 0.5f;
        for (int i = 0; i < text.length(); ++i) {
            String glyph = String.valueOf(text.charAt(i));
            if (shadow) {
                EgyptDraw.text(dl, font, x + 1.0f, y + 1.0f, glyph, EgyptDraw.col(-16777216, 0.7f * a));
            }
            EgyptDraw.text(dl, font, x, y, glyph, EgyptDraw.col(argb, a));
            x += EgyptDraw.textWidth(font, glyph) + spacing;
        }
    }

    public static ThemeImages.Texture texture(String resource) {
        if (TEXTURES.containsKey(resource)) {
            return TEXTURES.get(resource);
        }
        ThemeImages.Texture texture = null;
        try (InputStream in = EgyptDraw.class.getResourceAsStream(resource);){
            if (in != null) {
                texture = ThemeImages.decode(in.readAllBytes(), resource, false);
            } else {
                LOGGER.warn("[Anubis+] Missing texture {}", (Object)resource);
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis+] Failed to load {}", (Object)resource, (Object)e);
        }
        TEXTURES.put(resource, texture);
        return texture;
    }

    public static float intro(float t, float delay) {
        float x = Math.max(0.0f, Math.min(1.0f, (t - delay) / 0.6f));
        float inv = 1.0f - x;
        return 1.0f - inv * inv * inv;
    }
}

