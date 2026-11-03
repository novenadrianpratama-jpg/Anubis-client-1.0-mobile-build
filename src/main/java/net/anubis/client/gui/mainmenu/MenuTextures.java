/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui.mainmenu;

import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.anubis.client.gui.ThemeImages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class MenuTextures {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Map<String, ThemeImages.Texture> TEXTURES = new HashMap<String, ThemeImages.Texture>();
    private static boolean optimized;

    private static void changeProfile() {
        TEXTURES.values().forEach(ThemeImages.get()::retireTexture);
        TEXTURES.clear();
        optimized = ThemeImages.get().optimizedImages();
    }

    private MenuTextures() {
    }

    static ThemeImages.Texture original(String resource) {
        return MenuTextures.cached(resource, resource, UnaryOperator.identity());
    }

    static ThemeImages.Texture mask(String resource) {
        return MenuTextures.cached(resource + "#mask", resource, p -> {
            int[] out = new int[p.argb().length];
            for (int i = 0; i < out.length; ++i) {
                out[i] = p.argb()[i] & 0xFF000000 | 0xFFFFFF;
            }
            return new Pixels(p.width(), p.height(), out);
        });
    }

    static ThemeImages.Texture blurred(String resource, int radius) {
        return MenuTextures.cached(resource + "#blur" + radius, resource, p -> MenuTextures.blur(p, radius));
    }

    private static ThemeImages.Texture cached(String key, String resource, UnaryOperator<Pixels> variant) {
        boolean next = ThemeImages.get().optimizedImages();
        if (optimized != next) {
            MenuTextures.changeProfile();
        }
        if (TEXTURES.containsKey(key)) {
            return TEXTURES.get(key);
        }
        ThemeImages.Texture texture = null;
        try (InputStream in = MenuTextures.class.getResourceAsStream(resource);){
            if (in == null) {
                LOGGER.warn("[Anubis] Missing menu image {}", (Object)resource);
            } else {
                Pixels p = MenuTextures.decode(in.readAllBytes(), resource);
                if (p != null) {
                    p = (Pixels)variant.apply(p);
                    texture = MenuTextures.upload(p.argb(), p.width(), p.height());
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn("[Anubis] Failed to load menu image {}", (Object)key, (Object)e);
        }
        TEXTURES.put(key, texture);
        return texture;
    }

    private static Pixels blur(Pixels p, int radius) {
        int pad = radius * 2;
        int w = p.width() + pad * 2;
        int h = p.height() + pad * 2;
        float[] alpha = new float[w * h];
        for (int y = 0; y < p.height(); ++y) {
            for (int x = 0; x < p.width(); ++x) {
                alpha[(y + pad) * w + x + pad] = (float)(p.argb()[y * p.width() + x] >>> 24) / 255.0f;
            }
        }
        for (int pass = 0; pass < 3; ++pass) {
            alpha = MenuTextures.box(alpha, w, h, radius, true);
            alpha = MenuTextures.box(alpha, w, h, radius, false);
        }
        int[] out = new int[w * h];
        for (int i = 0; i < out.length; ++i) {
            out[i] = Math.round(Math.min(1.0f, alpha[i]) * 255.0f) << 24 | 0xFFFFFF;
        }
        return new Pixels(w, h, out);
    }

    private static float[] box(float[] in, int w, int h, int radius, boolean across) {
        if (optimized) {
            return MenuTextures.slidingBox(in, w, h, radius, across);
        }
        float[] out = new float[in.length];
        float norm = 1.0f / (float)(radius * 2 + 1);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float sum = 0.0f;
                for (int k = -radius; k <= radius; ++k) {
                    int sy;
                    int sx = across ? x + k : x;
                    int n = sy = across ? y : y + k;
                    if (sx < 0 || sy < 0 || sx >= w || sy >= h) continue;
                    sum += in[sy * w + sx];
                }
                out[y * w + x] = sum * norm;
            }
        }
        return out;
    }

    private static float[] slidingBox(float[] in, int w, int h, int radius, boolean across) {
        float[] out = new float[in.length];
        int length = across ? w : h;
        int rows = across ? h : w;
        float norm = 1.0f / (float)(radius * 2 + 1);
        for (int row = 0; row < rows; ++row) {
            double sum = 0.0;
            for (int k = 0; k <= Math.min(radius, length - 1); ++k) {
                sum += (double)in[across ? row * w + k : k * w + row];
            }
            for (int p = 0; p < length; ++p) {
                out[across ? row * w + p : p * w + row] = (float)sum * norm;
                int left = p - radius;
                int right = p + radius + 1;
                if (left >= 0) {
                    sum -= (double)in[across ? row * w + left : left * w + row];
                }
                if (right >= length) continue;
                sum += (double)in[across ? row * w + right : right * w + row];
            }
        }
        return out;
    }

    /*
     * Exception decompiling
     */
    private static Pixels decode(byte[] bytes, String name) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ThemeImages.Texture upload(int[] argb, int width, int height) {
        ByteBuffer buffer = MemoryUtil.memAlloc((int)(argb.length * 4));
        try {
            for (int c : argb) {
                buffer.put((byte)(c >> 16)).put((byte)(c >> 8)).put((byte)c).put((byte)(c >>> 24));
            }
            buffer.flip();
            ThemeImages.Texture texture = ThemeImages.fromPixels(buffer, width, height, false);
            return texture;
        }
        finally {
            MemoryUtil.memFree((Buffer)buffer);
        }
    }

    static {
        ThemeImages.get().onImageProfileChange(MenuTextures::changeProfile);
    }

    @Environment(value=EnvType.CLIENT)
    private record Pixels(int width, int height, int[] argb) {
    }
}

