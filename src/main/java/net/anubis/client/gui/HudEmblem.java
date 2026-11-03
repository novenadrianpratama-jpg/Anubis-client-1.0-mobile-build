/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.anubis.client.gui.ThemeImages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class HudEmblem {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String SOURCE = "/assets/anubis/textures/gui/hud_emblem.png";
    private static final float INK_GAMMA = 0.65f;
    private static final float INK_GAIN = 1.35f;
    private static float[] ink;
    private static int inkW;
    private static int inkH;
    private static boolean missing;
    private static ThemeImages.Texture sized;
    private static int sizedHeight;

    private HudEmblem() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ThemeImages.Texture forHeight(int height) {
        if (height < 4 || missing) {
            return null;
        }
        if (height == sizedHeight && sized != null) {
            return sized;
        }
        if (ink == null && !HudEmblem.load()) {
            missing = true;
            return null;
        }
        int width = Math.max(1, Math.round((float)inkW * (float)height / (float)inkH));
        ByteBuffer rgba = MemoryUtil.memAlloc((int)(width * height * 4));
        try {
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    float a = HudEmblem.coverage(x, y, width, height);
                    a = Math.min(1.0f, (float)Math.pow(a, 0.65f) * 1.35f);
                    rgba.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)Math.round(a * 255.0f));
                }
            }
            rgba.flip();
            if (sized != null) {
                GL11.glDeleteTextures((int)sized.id());
            }
            sized = ThemeImages.fromPixels(rgba, width, height, false);
            sizedHeight = height;
            ThemeImages.Texture texture = sized;
            return texture;
        }
        finally {
            MemoryUtil.memFree((Buffer)rgba);
        }
    }

    private static float coverage(int x, int y, int width, int height) {
        float x0 = (float)x * (float)inkW / (float)width;
        float x1 = (float)(x + 1) * (float)inkW / (float)width;
        float y0 = (float)y * (float)inkH / (float)height;
        float y1 = (float)(y + 1) * (float)inkH / (float)height;
        float sum = 0.0f;
        float area = 0.0f;
        for (int sy = (int)y0; sy < Math.min(inkH, (int)Math.ceil(y1)); ++sy) {
            float wy = Math.min(y1, (float)(sy + 1)) - Math.max(y0, (float)sy);
            for (int sx = (int)x0; sx < Math.min(inkW, (int)Math.ceil(x1)); ++sx) {
                float wx = Math.min(x1, (float)(sx + 1)) - Math.max(x0, (float)sx);
                float weight = wx * wy;
                sum += ink[sy * inkW + sx] * weight;
                area += weight;
            }
        }
        return area > 0.0f ? sum / area : 0.0f;
    }

    /*
     * Exception decompiling
     */
    private static boolean load() {
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

    static {
        sizedHeight = -1;
    }
}

