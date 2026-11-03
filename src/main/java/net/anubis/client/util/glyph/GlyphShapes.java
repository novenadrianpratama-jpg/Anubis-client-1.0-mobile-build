/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.glyph;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class GlyphShapes {
    public static final float[] ANKH = GlyphShapes.ankh();
    public static final float[] SCARAB = GlyphShapes.scarab();
    public static final float[] SPARK = GlyphShapes.spark();
    public static final float[] GRAIN = GlyphShapes.grain();
    public static final float[] DIAMOND = GlyphShapes.diamond();
    public static final float[] DISK = new Builder().disk(0.0f, 0.0f, 1.0f, 1.0f, 12).build();
    public static final float[] OCTAGON = new Builder().disk(0.0f, 0.0f, 1.0f, 1.0f, 8).build();
    public static final float[] HALO = new Builder().arc(0.0f, 0.0f, 0.84f, 0.84f, 1.0f, 1.0f, 0.0f, (float)Math.PI * 2, 20).build();
    public static final float[] EYE = GlyphShapes.horusEye();
    public static final float[] EYE_PUPIL = new Builder().offset(-0.14f, 0.04f).disk(-0.08f, 0.07f, 0.19f, 0.19f, 8).build();

    private GlyphShapes() {
    }

    private static float[] ankh() {
        Builder b = new Builder();
        b.quad(-0.17f, -1.0f, 0.17f, -1.0f, 0.09f, 0.06f, -0.09f, 0.06f);
        b.quad(-0.56f, -0.02f, -0.09f, 0.04f, -0.09f, 0.2f, -0.56f, 0.26f);
        b.quad(-0.09f, 0.04f, 0.09f, 0.04f, 0.09f, 0.2f, -0.09f, 0.2f);
        b.quad(0.09f, 0.04f, 0.56f, -0.02f, 0.56f, 0.26f, 0.09f, 0.2f);
        b.arc(0.0f, 0.6f, 0.19f, 0.28f, 0.3f, 0.42f, -1.5707964f, 4.712389f, 10);
        return b.build();
    }

    private static float[] scarab() {
        Builder b = new Builder();
        float gap = 0.035f;
        float cy = -0.12f;
        float rx = 0.56f;
        float ry = 0.78f;
        int steps = 5;
        for (int i = 0; i < steps; ++i) {
            double a0 = 1.5707963267948966 + Math.PI * (double)i / (double)steps;
            double a1 = 1.5707963267948966 + Math.PI * (double)(i + 1) / (double)steps;
            float y0 = cy + (float)Math.sin(a0) * ry;
            float y1 = cy + (float)Math.sin(a1) * ry;
            float x0 = (float)Math.cos(a0) * rx - gap;
            float x1 = (float)Math.cos(a1) * rx - gap;
            b.quad(-gap, y0, x0, y0, x1, y1, -gap, y1);
            b.quad(gap, -y0 + 2.0f * cy, -x0, -y0 + 2.0f * cy, -x1, -y1 + 2.0f * cy, gap, -y1 + 2.0f * cy);
        }
        b.quad(-0.44f, 0.62f, 0.44f, 0.62f, 0.33f, 0.86f, -0.33f, 0.86f);
        b.quad(-0.24f, 0.86f, 0.24f, 0.86f, 0.13f, 1.0f, -0.13f, 1.0f);
        b.stroke(0.055f, -0.34f, 0.7f, -0.62f, 0.8f, -0.74f, 0.98f);
        b.stroke(0.055f, 0.34f, 0.7f, 0.62f, 0.8f, 0.74f, 0.98f);
        b.stroke(0.055f, -0.5f, 0.12f, -0.82f, 0.2f, -0.92f, 0.04f);
        b.stroke(0.055f, 0.5f, 0.12f, 0.82f, 0.2f, 0.92f, 0.04f);
        b.stroke(0.055f, -0.44f, -0.52f, -0.74f, -0.72f, -0.8f, -0.96f);
        b.stroke(0.055f, 0.44f, -0.52f, 0.74f, -0.72f, 0.8f, -0.96f);
        return b.build();
    }

    private static float[] spark() {
        Builder b = new Builder();
        b.quad(0.0f, -1.0f, 0.15f, 0.0f, 0.0f, 1.0f, -0.15f, 0.0f);
        b.quad(-0.62f, 0.0f, 0.0f, -0.13f, 0.62f, 0.0f, 0.0f, 0.13f);
        return b.build();
    }

    private static float[] grain() {
        return new Builder().quad(-0.8f, -0.62f, 0.72f, -0.88f, 0.9f, 0.46f, -0.52f, 0.82f).build();
    }

    private static float[] diamond() {
        return new Builder().quad(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f).build();
    }

    private static float[] horusEye() {
        Builder b = new Builder().offset(-0.14f, 0.04f);
        float[] upper = GlyphShapes.curve(-0.96f, 0.0f, -0.08f, 0.64f, 0.76f, 0.02f, 9);
        b.stroke(0.1f, GlyphShapes.append(upper, 1.3f, 0.02f));
        b.stroke(0.08f, GlyphShapes.curve(-0.96f, 0.0f, -0.06f, -0.46f, 0.76f, 0.02f, 8));
        b.stroke(0.12f, GlyphShapes.curve(-1.0f, 0.46f, 0.02f, 0.98f, 1.28f, 0.52f, 9));
        b.stroke(0.09f, -0.36f, -0.24f, -0.38f, -0.6f, -0.44f, -0.96f);
        b.stroke(0.08f, 0.06f, -0.22f, 0.3f, -0.44f, 0.6f, -0.6f, 0.85f, -0.72f, 0.98f, -0.86f, 0.95f, -0.99f, 0.83f, -1.03f, 0.74f, -0.96f, 0.76f, -0.87f, 0.85f, -0.85f);
        return b.build();
    }

    private static float[] curve(float x0, float y0, float cx, float cy, float x1, float y1, int steps) {
        float[] out = new float[(steps + 1) * 2];
        for (int i = 0; i <= steps; ++i) {
            float t = (float)i / (float)steps;
            float s = 1.0f - t;
            out[i * 2] = s * s * x0 + 2.0f * s * t * cx + t * t * x1;
            out[i * 2 + 1] = s * s * y0 + 2.0f * s * t * cy + t * t * y1;
        }
        return out;
    }

    private static float[] append(float[] points, float x, float y) {
        float[] out = Arrays.copyOf(points, points.length + 2);
        out[points.length] = x;
        out[points.length + 1] = y;
        return out;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Builder {
        private float[] data = new float[64];
        private int size;
        private float offsetX;
        private float offsetY;

        private Builder() {
        }

        Builder offset(float x, float y) {
            this.offsetX = x;
            this.offsetY = y;
            return this;
        }

        Builder quad(float ax, float ay, float bx, float by, float cx, float cy, float dx, float dy) {
            if (this.size + 8 > this.data.length) {
                this.data = Arrays.copyOf(this.data, this.data.length * 2);
            }
            this.data[this.size++] = ax + this.offsetX;
            this.data[this.size++] = ay + this.offsetY;
            this.data[this.size++] = bx + this.offsetX;
            this.data[this.size++] = by + this.offsetY;
            this.data[this.size++] = cx + this.offsetX;
            this.data[this.size++] = cy + this.offsetY;
            this.data[this.size++] = dx + this.offsetX;
            this.data[this.size++] = dy + this.offsetY;
            return this;
        }

        Builder arc(float cx, float cy, float innerX, float innerY, float outerX, float outerY, float from, float to, int steps) {
            for (int i = 0; i < steps; ++i) {
                double a0 = (double)from + (double)((to - from) * (float)i) / (double)steps;
                double a1 = (double)from + (double)((to - from) * (float)(i + 1)) / (double)steps;
                float c0 = (float)Math.cos(a0);
                float s0 = (float)Math.sin(a0);
                float c1 = (float)Math.cos(a1);
                float s1 = (float)Math.sin(a1);
                this.quad(cx + c0 * innerX, cy + s0 * innerY, cx + c0 * outerX, cy + s0 * outerY, cx + c1 * outerX, cy + s1 * outerY, cx + c1 * innerX, cy + s1 * innerY);
            }
            return this;
        }

        Builder disk(float cx, float cy, float rx, float ry, int sides) {
            int i;
            float[] ring = new float[sides * 2];
            for (i = 0; i < sides; ++i) {
                double a = Math.PI * 2 * (double)i / (double)sides + Math.PI / (double)sides;
                ring[i * 2] = cx + (float)Math.cos(a) * rx;
                ring[i * 2 + 1] = cy + (float)Math.sin(a) * ry;
            }
            i = 1;
            while (i + 1 < sides) {
                int j = Math.min(i + 2, sides - 1);
                this.quad(ring[0], ring[1], ring[i * 2], ring[i * 2 + 1], ring[(i + 1) * 2], ring[(i + 1) * 2 + 1], ring[j * 2], ring[j * 2 + 1]);
                i += 2;
            }
            return this;
        }

        Builder stroke(float width, float ... points) {
            int i;
            int n = points.length / 2;
            if (n < 2) {
                return this;
            }
            float half = width * 0.5f;
            float[] nx = new float[n];
            float[] ny = new float[n];
            for (i = 0; i < n; ++i) {
                float len;
                float dy;
                float dx;
                float len2;
                float sx = 0.0f;
                float sy = 0.0f;
                float px = 0.0f;
                float py = 0.0f;
                if (i > 0 && (len2 = (float)Math.sqrt((dx = points[i * 2] - points[i * 2 - 2]) * dx + (dy = points[i * 2 + 1] - points[i * 2 - 1]) * dy)) > 1.0E-6f) {
                    px = -dy / len2;
                    py = dx / len2;
                    sx += px;
                    sy += py;
                }
                if (i < n - 1 && (len2 = (float)Math.sqrt((dx = points[i * 2 + 2] - points[i * 2]) * dx + (dy = points[i * 2 + 3] - points[i * 2 + 1]) * dy)) > 1.0E-6f) {
                    sx += -dy / len2;
                    sy += dx / len2;
                    if (i == 0) {
                        px = -dy / len2;
                        py = dx / len2;
                    }
                }
                if ((len = (float)Math.sqrt(sx * sx + sy * sy)) < 1.0E-6f) {
                    sx = px;
                    sy = py;
                    len = 1.0f;
                }
                float dot = Math.max(0.5f, (sx /= len) * px + (sy /= len) * py);
                nx[i] = sx * half / dot;
                ny[i] = sy * half / dot;
            }
            for (i = 0; i < n - 1; ++i) {
                float ax = points[i * 2];
                float ay = points[i * 2 + 1];
                float bx = points[i * 2 + 2];
                float by = points[i * 2 + 3];
                this.quad(ax - nx[i], ay - ny[i], bx - nx[i + 1], by - ny[i + 1], bx + nx[i + 1], by + ny[i + 1], ax + nx[i], ay + ny[i]);
            }
            return this;
        }

        float[] build() {
            return Arrays.copyOf(this.data, this.size);
        }
    }
}

