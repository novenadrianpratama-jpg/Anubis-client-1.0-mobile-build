/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.spotify;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AlbumArt {
    private static final int MAX_SOURCE_SIDE = 4096;
    private final int size;
    private final byte[] rgba;

    private AlbumArt(int size, byte[] rgba) {
        this.size = size;
        this.rgba = rgba;
    }

    /*
     * Exception decompiling
     */
    public static AlbumArt decode(byte[] encoded, int maxSide) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 4 blocks at once
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

    static AlbumArt crop(ByteBuffer pixels, int width, int height, int maxSide) {
        int side = Math.min(width, height);
        int left = (width - side) / 2;
        int top = (height - side) / 2;
        int size = Math.min(side, maxSide);
        byte[] out = new byte[size * size * 4];
        int o = 0;
        for (int oy = 0; oy < size; ++oy) {
            int y0 = top + (int)((long)oy * (long)side / (long)size);
            int y1 = Math.max(y0 + 1, top + (int)((long)(oy + 1) * (long)side / (long)size));
            for (int ox = 0; ox < size; ++ox) {
                int x0 = left + (int)((long)ox * (long)side / (long)size);
                int x1 = Math.max(x0 + 1, left + (int)((long)(ox + 1) * (long)side / (long)size));
                long r = 0L;
                long g = 0L;
                long b = 0L;
                long a = 0L;
                for (int y = y0; y < y1; ++y) {
                    int row = (y * width + x0) * 4;
                    int x = x0;
                    while (x < x1) {
                        r += (long)(pixels.get(row) & 0xFF);
                        g += (long)(pixels.get(row + 1) & 0xFF);
                        b += (long)(pixels.get(row + 2) & 0xFF);
                        a += (long)(pixels.get(row + 3) & 0xFF);
                        ++x;
                        row += 4;
                    }
                }
                long count = (long)(y1 - y0) * (long)(x1 - x0);
                out[o++] = (byte)((r + count / 2L) / count);
                out[o++] = (byte)((g + count / 2L) / count);
                out[o++] = (byte)((b + count / 2L) / count);
                out[o++] = (byte)((a + count / 2L) / count);
            }
        }
        return new AlbumArt(size, out);
    }

    public int size() {
        return this.size;
    }

    public int byteSize() {
        return this.rgba.length;
    }

    public void copyTo(ByteBuffer target) {
        target.put(this.rgba);
    }
}

