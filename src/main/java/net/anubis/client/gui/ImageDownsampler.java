/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ImageDownsampler {
    private ImageDownsampler() {
    }

    public static int[] dimensions(int width, int height, int maxSide) {
        if (width <= 0 || height <= 0 || maxSide <= 0) {
            throw new IllegalArgumentException("Image dimensions");
        }
        double scale = Math.min(1.0, (double)maxSide / (double)Math.max(width, height));
        return new int[]{Math.max(1, (int)Math.round((double)width * scale)), Math.max(1, (int)Math.round((double)height * scale))};
    }

    public static void rgba(ByteBuffer source, int width, int height, ByteBuffer target, int outWidth, int outHeight) {
        if (outWidth <= 0 || outHeight <= 0 || outWidth > width || outHeight > height) {
            throw new IllegalArgumentException("Only image reduction is supported");
        }
        double stepX = (double)width / (double)outWidth;
        double stepY = (double)height / (double)outHeight;
        for (int y = 0; y < outHeight; ++y) {
            double top = (double)y * stepY;
            double bottom = (double)(y + 1) * stepY;
            for (int x = 0; x < outWidth; ++x) {
                double left = (double)x * stepX;
                double right = (double)(x + 1) * stepX;
                double a = 0.0;
                double r = 0.0;
                double g = 0.0;
                double b = 0.0;
                for (int sy = (int)top; sy < Math.min(height, (int)Math.ceil(bottom)); ++sy) {
                    double wy = Math.min(bottom, (double)sy + 1.0) - Math.max(top, (double)sy);
                    for (int sx = (int)left; sx < Math.min(width, (int)Math.ceil(right)); ++sx) {
                        double weight = wy * (Math.min(right, (double)sx + 1.0) - Math.max(left, (double)sx));
                        int i = (sy * width + sx) * 4;
                        double alpha = (double)(source.get(i + 3) & 0xFF) * weight;
                        a += alpha;
                        r += (double)(source.get(i) & 0xFF) * alpha;
                        g += (double)(source.get(i + 1) & 0xFF) * alpha;
                        b += (double)(source.get(i + 2) & 0xFF) * alpha;
                    }
                }
                target.put((byte)(a == 0.0 ? 0L : Math.round(r / a)));
                target.put((byte)(a == 0.0 ? 0L : Math.round(g / a)));
                target.put((byte)(a == 0.0 ? 0L : Math.round(b / a)));
                target.put((byte)Math.round(a / (stepX * stepY)));
            }
        }
        target.flip();
    }
}

