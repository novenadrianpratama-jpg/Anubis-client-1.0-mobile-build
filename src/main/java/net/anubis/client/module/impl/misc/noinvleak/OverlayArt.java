/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_5348
 */
package net.anubis.client.module.impl.misc.noinvleak;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Optional;
import net.anubis.client.module.impl.misc.noinvleak.Canvas;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_5348;

@Environment(value=EnvType.CLIENT)
final class OverlayArt {
    private static final int GLYPH = 8;
    private static final int SHADOW_MASK = 0xFCFCFC;
    private static Image selection;
    private static Slice tooltipBackground;
    private static Slice tooltipFrame;
    private static boolean[] glyphs;
    private static boolean loaded;

    private OverlayArt() {
    }

    static void reset() {
        selection = null;
        tooltipBackground = null;
        tooltipFrame = null;
        glyphs = null;
        loaded = false;
    }

    static Image selection() {
        OverlayArt.load();
        return selection;
    }

    static Slice tooltipBackground() {
        OverlayArt.load();
        return tooltipBackground;
    }

    static Slice tooltipFrame() {
        OverlayArt.load();
        return tooltipFrame;
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        selection = OverlayArt.image("textures/gui/sprites/hud/hotbar_selection.png");
        tooltipBackground = OverlayArt.slice("textures/gui/sprites/tooltip/background.png", 9, false);
        tooltipFrame = OverlayArt.slice("textures/gui/sprites/tooltip/frame.png", 10, true);
        Image font = OverlayArt.image("textures/font/ascii.png");
        if (font != null) {
            glyphs = new boolean[font.width() * font.height()];
            for (int i = 0; i < glyphs.length; ++i) {
                OverlayArt.glyphs[i] = font.pixels()[i] >>> 24 != 0;
            }
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static Image image(String path) {
        class_3298 resource = class_310.method_1551().method_1478().method_14486(class_2960.method_60656((String)path)).orElse(null);
        if (resource == null) {
            return null;
        }
        try (InputStream in = resource.method_14482();){
            Image image;
            block16: {
                class_1011 image2 = class_1011.method_4309((InputStream)in);
                try {
                    int[] abgr = image2.method_48463();
                    int[] out = new int[abgr.length];
                    for (int i = 0; i < abgr.length; ++i) {
                        int a = abgr[i] >>> 24;
                        out[i] = Canvas.premultiply(a << 24 | (abgr[i] & 0xFF) << 16 | (abgr[i] >>> 8 & 0xFF) << 8 | abgr[i] >>> 16 & 0xFF, a);
                    }
                    image = new Image(out, image2.method_4307(), image2.method_4323());
                    if (image2 == null) break block16;
                }
                catch (Throwable throwable) {
                    if (image2 != null) {
                        try {
                            image2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                image2.close();
            }
            return image;
        }
        catch (IOException | RuntimeException unreadable) {
            return null;
        }
    }

    private static Slice slice(String path, int border, boolean stretch) {
        Image image = OverlayArt.image(path);
        if (image == null) {
            return null;
        }
        class_3298 meta = class_310.method_1551().method_1478().method_14486(class_2960.method_60656((String)(path + ".mcmeta"))).orElse(null);
        if (meta != null) {
            try (BufferedReader reader = meta.method_43039();){
                JsonObject scaling = JsonParser.parseReader((Reader)reader).getAsJsonObject().getAsJsonObject("gui").getAsJsonObject("scaling");
                if (scaling.has("border")) {
                    border = scaling.get("border").getAsInt();
                }
                stretch = scaling.has("stretch_inner") && scaling.get("stretch_inner").getAsBoolean();
            }
            catch (IOException | RuntimeException exception) {
                // empty catch block
            }
        }
        return new Slice(image, border, stretch);
    }

    static void slice(Canvas canvas, Slice slice, int x, int y, int width, int height, int scale) {
        int dy;
        if (slice == null) {
            return;
        }
        Image img = slice.image();
        int b = slice.border();
        int innerW = img.width() - 2 * b;
        int innerH = img.height() - 2 * b;
        if (innerW <= 0 || innerH <= 0) {
            return;
        }
        int[] column = new int[width];
        int[] row = new int[height];
        for (int dx = 0; dx < width; ++dx) {
            column[dx] = OverlayArt.sourceAt(dx, width, b, innerW, slice.stretch());
        }
        for (dy = 0; dy < height; ++dy) {
            row[dy] = OverlayArt.sourceAt(dy, height, b, innerH, slice.stretch());
        }
        for (dy = 0; dy < height; ++dy) {
            for (int dx = 0; dx < width; ++dx) {
                int src = img.pixels()[row[dy] * img.width() + column[dx]];
                if (src >>> 24 == 0) continue;
                canvas.blendPixel(src, (x + dx) * scale, (y + dy) * scale, scale);
            }
        }
    }

    private static int sourceAt(int d, int size, int b, int inner, boolean stretch) {
        if (d < b) {
            return d;
        }
        if (d >= size - b) {
            return b + inner + (d - (size - b));
        }
        int into = d - b;
        int middle = size - 2 * b;
        return b + (stretch ? into * inner / Math.max(middle, 1) : into % inner);
    }

    static void text(Canvas canvas, class_2561 text, int x, int y, int scale, int argb, boolean shadow) {
        OverlayArt.load();
        if (glyphs == null) {
            return;
        }
        int[] cursor = new int[]{x};
        text.method_27658((style, piece) -> {
            int color = style.method_10973() != null ? argb & 0xFF000000 | style.method_10973().method_27716() : argb;
            int i = 0;
            while (i < piece.length()) {
                int cp = piece.codePointAt(i);
                i += Character.charCount(cp);
                int advance = OverlayArt.advance(cp, style);
                if (cp < 128 && cp > 32 && !style.method_10987()) {
                    if (shadow) {
                        OverlayArt.glyph(canvas, cp, cursor[0] + 1, y + 1, scale, color & 0xFF000000 | (color & 0xFCFCFC) >> 2, style);
                    }
                    OverlayArt.glyph(canvas, cp, cursor[0], y, scale, color, style);
                }
                if (style.method_10986()) {
                    canvas.rect(cursor[0] * scale, (y + 4) * scale, advance * scale, scale, color);
                }
                if (style.method_10965()) {
                    canvas.rect(cursor[0] * scale, (y + 8) * scale, advance * scale, scale, color);
                }
                cursor[0] = cursor[0] + advance;
            }
            return Optional.empty();
        }, class_2583.field_24360);
    }

    private static int advance(int cp, class_2583 style) {
        return class_310.method_1551().field_1772.method_27525(class_5348.method_29431((String)new String(Character.toChars(cp)), (class_2583)style));
    }

    private static void glyph(Canvas canvas, int cp, int x, int y, int scale, int argb, class_2583 style) {
        int cellX = cp % 16 * 8;
        int cellY = cp / 16 * 8;
        int stride = 128;
        for (int row = 0; row < 8; ++row) {
            int shear = style.method_10966() && row < 4 ? 1 : 0;
            int col = 0;
            while (col < 8) {
                if (!glyphs[(cellY + row) * stride + cellX + col]) {
                    ++col;
                    continue;
                }
                int start = col;
                while (col < 8 && glyphs[(cellY + row) * stride + cellX + col]) {
                    ++col;
                }
                canvas.rect((x + start + shear) * scale, (y + row) * scale, (col - start) * scale, scale, argb);
                if (!style.method_10984()) continue;
                canvas.rect((x + start + shear + 1) * scale, (y + row) * scale, (col - start) * scale, scale, argb);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Image(int[] pixels, int width, int height) {
    }

    @Environment(value=EnvType.CLIENT)
    record Slice(Image image, int border, boolean stretch) {
    }
}

