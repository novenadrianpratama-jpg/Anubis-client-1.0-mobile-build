/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import net.anubis.client.module.impl.render.PeepSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
final class PeepArt {
    static final int STREAK = 0;
    static final int DROP = 1;
    static final int PETAL = 2;
    static final int HEART_LEFT = 3;
    static final int HEART_RIGHT = 4;
    static final int FEATHER = 5;
    static final int WISP = 6;
    static final int BEAM = 7;
    static final int TWINKLE = 8;
    static final int ARC = 9;
    static final int MIST = 10;
    static final int BOLT = 11;
    static final int BOLT_FORK = 12;
    static final int HEART = 13;
    static final int CONE = 14;
    static final int PETAL_CURL = 15;
    static final int GLINT = 16;
    static final int COUNT = 17;
    static final float ARC_RADIUS = 0.421875f;
    static final float CONE_FOOT = 0.05f;
    static final float CONE_END = 0.9f;
    static final float FEATHER_ASPECT = 0.28846154f;

    private PeepArt() {
    }

    static Art[] drawAll() {
        Art[] out = new Art[17];
        out[0] = PeepArt.streak();
        out[1] = PeepArt.drop();
        out[2] = PeepArt.petal(false);
        out[15] = PeepArt.petal(true);
        Art[] heart = PeepArt.heart();
        out[13] = heart[0];
        out[3] = heart[1];
        out[4] = heart[2];
        out[5] = PeepArt.feather();
        out[6] = PeepArt.wisp();
        out[7] = PeepArt.beam();
        out[8] = PeepArt.twinkle();
        out[9] = PeepArt.arc();
        out[10] = PeepArt.mist();
        out[11] = PeepArt.bolt(7, false);
        out[12] = PeepArt.bolt(23, true);
        out[14] = PeepArt.cone();
        out[16] = PeepArt.glint();
        return out;
    }

    static int[] rim(int[] tattoo, int w, int h) {
        float[] core = new float[w * h];
        for (int i = 0; i < core.length; ++i) {
            core[i] = (float)(tattoo[i] >>> 24) / 255.0f;
        }
        float[] wide = PeepSky.blur(core, w, h, 3);
        float[] deep = PeepSky.blur(core, w, h, 7);
        float[] out = new float[core.length];
        for (int i = 0; i < out.length; ++i) {
            float ring = class_3532.method_15363((float)(wide[i] * 2.2f - PeepArt.smooth(0.55f, 0.95f, core[i]) * 1.2f), (float)0.0f, (float)1.0f) * (1.0f - PeepArt.smooth(0.6f, 0.8f, deep[i]));
            float band = core[i] * PeepArt.smooth(0.4f, 0.6f, deep[i]) * (1.0f - PeepArt.smooth(0.85f, 0.97f, deep[i]));
            float tip = core[i] * (1.0f - PeepArt.smooth(0.45f, 0.6f, deep[i]));
            out[i] = Math.max(ring, Math.max(band, tip));
        }
        return PeepArt.white(out);
    }

    static int[] gold(int[] tattoo, int w, int h) {
        float[] core = new float[w * h];
        for (int i = 0; i < core.length; ++i) {
            core[i] = PeepArt.smooth(0.3f, 0.65f, (float)(tattoo[i] >>> 24) / 255.0f);
        }
        float[] deep = PeepSky.blur(core, w, h, 7);
        float[] fill = PeepSky.blur(core, w, h, 12);
        float[] soft = PeepSky.blur(core, w, h, 2);
        int[] out = new int[w * h];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int i = y * w + x;
                float line = core[i] * (1.0f - PeepArt.smooth(0.88f, 0.98f, fill[i]));
                if ((line = Math.max(line, Math.min(1.0f, soft[i] * 1.8f) * (1.0f - PeepArt.smooth(0.25f, 0.45f, deep[i])))) <= 0.002f) continue;
                float bevel = soft[Math.min(h - 1, y + 2) * w + Math.min(w - 1, x + 2)] - soft[Math.max(0, y - 2) * w + Math.max(0, x - 2)];
                float lit = class_3532.method_15363((float)(0.5f + 1.1f * bevel), (float)0.0f, (float)1.0f);
                int r = Math.round(class_3532.method_16439((float)lit, (float)0.85f, (float)1.0f) * 255.0f);
                int g = Math.round(class_3532.method_16439((float)lit, (float)0.8f, (float)1.0f) * 255.0f);
                int b = Math.round(class_3532.method_16439((float)lit, (float)0.45f, (float)1.0f) * 255.0f);
                out[i] = Math.round(class_3532.method_15363((float)line, (float)0.0f, (float)1.0f) * 255.0f) << 24 | r << 16 | g << 8 | b;
            }
        }
        return out;
    }

    static int[] veil(int[] tattoo, int w, int h) {
        float[] core = new float[w * h];
        for (int i = 0; i < core.length; ++i) {
            core[i] = (float)(tattoo[i] >>> 24) / 255.0f;
        }
        float[] soft = PeepSky.blur(core, w, h, 3);
        float[] deep = PeepSky.blur(core, w, h, 7);
        float[] mist = PeepSky.blur(core, w, h, 11);
        float[] out = new float[core.length];
        for (int i = 0; i < out.length; ++i) {
            float hollow = 1.0f - 0.85f * PeepArt.smooth(0.6f, 0.9f, deep[i]);
            float body = Math.min(1.0f, soft[i] * 1.15f) * hollow;
            out[i] = Math.max(body, Math.min(1.0f, mist[i] * 2.2f) * 0.45f * (0.4f + 0.6f * hollow));
        }
        return PeepArt.white(out);
    }

    private static Art streak() {
        int w = 256;
        int h = 32;
        float[] a = new float[w * h];
        float head = 0.93f;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float t = ((float)x + 0.5f) / (float)w;
                float dy = (float)y + 0.5f - (float)h * 0.5f;
                float along = t < head ? (float)Math.pow(t / head, 2.6) : (float)Math.cos((t - head) / (1.0f - head) * 1.5707964f);
                float sigma = 0.7f + 2.6f * (float)Math.pow(t, 1.6);
                float body = along * PeepArt.gauss(dy, sigma);
                float bulb = 0.9f * PeepArt.gauss((float)Math.hypot((float)x + 0.5f - (float)w * 0.9f, dy), 3.4f);
                a[y * w + x] = Math.min(1.0f, body + bulb);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 2)));
    }

    private static Art drop() {
        int w = 12;
        int h = 96;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            float t = ((float)y + 0.5f) / (float)h;
            float along = (float)Math.pow(Math.sin(Math.PI * (double)t), 0.7) * (0.3f + 0.7f * t);
            for (int x = 0; x < w; ++x) {
                a[y * w + x] = along * PeepArt.gauss((float)x + 0.5f - (float)w * 0.5f, 1.15f);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 1)));
    }

    private static Art petal(boolean curled) {
        int w = 60;
        int h = 68;
        int pad = 6;
        Path2D.Float shape = new Path2D.Float();
        if (curled) {
            shape.moveTo(0.52, 0.99);
            shape.curveTo(0.2, 0.84, 0.03, 0.52, 0.12, 0.22);
            shape.curveTo(0.17, 0.06, 0.31, 0.02, 0.41, 0.13);
            shape.curveTo(0.56, 0.25, 0.76, 0.21, 0.93, 0.07);
            shape.curveTo(1.0, 0.42, 0.85, 0.8, 0.52, 0.99);
        } else {
            shape.moveTo(0.46, 0.99);
            shape.curveTo(0.3, 0.9, 0.06, 0.62, 0.05, 0.38);
            shape.curveTo(0.04, 0.14, 0.24, 0.02, 0.45, 0.04);
            shape.curveTo(0.55, 0.05, 0.6, 0.075, 0.64, 0.07);
            shape.curveTo(0.8, 0.05, 0.97, 0.18, 0.95, 0.4);
            shape.curveTo(0.93, 0.62, 0.7, 0.88, 0.54, 0.99);
        }
        shape.closePath();
        float[] hard = PeepArt.fill(shape, w, h, pad);
        float[] mask = PeepSky.blur(hard, w, h, 1);
        float[] inner = PeepSky.blur(hard, w, h, 4);
        int[] out = new int[w * h];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int i = y * w + x;
                float m = mask[i];
                if (m <= 0.002f) continue;
                float nx = ((float)x + 0.5f - (float)pad) / ((float)w - (float)pad * 2.0f);
                float ny = ((float)y + 0.5f - (float)pad) / ((float)h - (float)pad * 2.0f);
                float r = (float)Math.hypot((nx - 0.5f) * 1.2f, ny - 0.42f) / 0.6f;
                int col = PeepArt.mix(16763356, 16088488, PeepArt.smooth(0.08f, 0.75f, r));
                col = PeepArt.mix(col, 13582964, PeepArt.smooth(0.7f, 1.15f, r) * 0.65f);
                float base = PeepArt.smooth(0.64f, 1.0f, ny);
                col = PeepArt.mix(col, 11020378, base * 0.5f);
                float angle = (float)Math.atan2(nx - 0.5f, 1.0f - ny);
                float vein = (float)Math.pow(Math.abs(Math.cos(angle * 11.0f)), 24.0) * (1.0f - base) * PeepArt.smooth(0.1f, 0.5f, 1.0f - ny);
                col = PeepArt.mix(col, 16768232, vein * 0.2f);
                float rim = (1.0f - PeepArt.smooth(0.55f, 0.9f, inner[i])) * (1.0f - PeepArt.smooth(0.4f, 0.9f, ny));
                col = PeepArt.mix(col, 16758996, rim * 0.55f);
                if (curled) {
                    float lip = (1.0f - PeepArt.smooth(0.5f, 0.85f, inner[i])) * (1.0f - PeepArt.smooth(0.16f, 0.4f, ny));
                    float under = PeepArt.smooth(0.14f, 0.26f, ny) * (1.0f - PeepArt.smooth(0.26f, 0.46f, ny)) * PeepArt.smooth(0.3f, 0.8f, inner[i]);
                    col = PeepArt.mix(PeepArt.mix(col, 16767462, lip * 0.7f), 11546719, under * 0.35f);
                }
                out[i] = class_9848.method_71346((float)class_3532.method_15363((float)(m * 0.95f), (float)0.0f, (float)1.0f), (int)col);
            }
        }
        return new Art(w, h, out);
    }

    private static Art[] heart() {
        int w = 128;
        int h = 120;
        int pad = 16;
        Path2D.Float shape = new Path2D.Float();
        shape.moveTo(0.5, 0.95);
        shape.curveTo(0.13, 0.68, 0.0, 0.44, 0.08, 0.24);
        shape.curveTo(0.17, 0.04, 0.42, 0.03, 0.5, 0.24);
        shape.curveTo(0.58, 0.03, 0.83, 0.04, 0.92, 0.24);
        shape.curveTo(1.0, 0.44, 0.87, 0.68, 0.5, 0.95);
        shape.closePath();
        float[] mask = PeepArt.fill(shape, w, h, pad);
        float[][] part = new float[][]{mask, new float[w * h], new float[w * h]};
        for (int y = 0; y < h; ++y) {
            float split = (float)w * 0.5f + PeepArt.crack(((float)y + 0.5f - (float)pad) / ((float)h - (float)pad * 2.0f)) * ((float)w - (float)pad * 2.0f);
            for (int x = 0; x < w; ++x) {
                part[(float)x + 0.5f < split ? 1 : 2][y * w + x] = mask[y * w + x];
            }
        }
        Art[] out = new Art[part.length];
        for (int piece = 0; piece < part.length; ++piece) {
            float[] core = part[piece];
            float[] inner = PeepSky.blur(core, w, h, 6);
            float[] glow = PeepSky.blur(core, w, h, 7);
            int[] px = new int[w * h];
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    float haloAlpha;
                    int i = y * w + x;
                    float nx = ((float)x + 0.5f - (float)pad) / ((float)w - (float)pad * 2.0f);
                    float ny = ((float)y + 0.5f - (float)pad) / ((float)h - (float)pad * 2.0f);
                    float m = core[i];
                    float edge = class_3532.method_15363((float)((m - inner[i]) * 2.6f), (float)0.0f, (float)1.0f) * m;
                    float shine = PeepArt.gauss((float)Math.hypot(nx - 0.3f, ny - 0.27f), 0.09f) * m;
                    int body = PeepArt.mix(12720235, 16732080, 0.25f + shine * 0.5f);
                    float bodyAlpha = Math.max(m * (0.16f + 0.1f * (1.0f - ny)), edge);
                    float alpha = bodyAlpha + (haloAlpha = (1.0f - m) * Math.min(1.0f, glow[i] * 1.5f) * 0.65f);
                    if (alpha <= 0.002f) continue;
                    int line = PeepArt.mix(16751320, 0xFFE6F6, PeepArt.smooth(0.75f, 1.0f, edge) * 0.45f);
                    int col = PeepArt.mix(16732080, PeepArt.mix(body, line, edge / Math.max(bodyAlpha, 1.0E-4f)), bodyAlpha / alpha);
                    px[i] = class_9848.method_71346((float)class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f), (int)col);
                }
            }
            out[piece] = new Art(w, h, px);
        }
        return out;
    }

    static float crack(float ny) {
        float[] ys = new float[]{0.2f, 0.34f, 0.47f, 0.6f, 0.73f, 0.86f, 1.0f};
        float[] xs = new float[]{0.0f, -0.05f, 0.045f, -0.04f, 0.04f, -0.025f, 0.0f};
        if (ny <= ys[0]) {
            return 0.0f;
        }
        for (int i = 1; i < ys.length; ++i) {
            if (!(ny <= ys[i])) continue;
            return class_3532.method_16439((float)((ny - ys[i - 1]) / (ys[i] - ys[i - 1])), (float)xs[i - 1], (float)xs[i]);
        }
        return 0.0f;
    }

    private static Art feather() {
        int w = Math.round(60.0f);
        int h = 208;
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics2D g = PeepArt.smooth(image);
        QuadCurve2D.Float shaft = new QuadCurve2D.Float((float)w * 0.42f, (float)h * 0.97f, (float)w * 0.56f, (float)h * 0.5f, (float)w * 0.44f, (float)h * 0.035f);
        int barbs = 120;
        for (int i = 0; i < barbs; ++i) {
            float t = 0.06f + 0.92f * (float)i / ((float)barbs - 1.0f);
            float px = PeepArt.quad(shaft.x1, shaft.ctrlx, shaft.x2, t);
            float py = PeepArt.quad(shaft.y1, shaft.ctrly, shaft.y2, t);
            float tx = PeepArt.quadSlope(shaft.x1, shaft.ctrlx, shaft.x2, t);
            float ty = PeepArt.quadSlope(shaft.y1, shaft.ctrly, shaft.y2, t);
            float len = (float)Math.hypot(tx, ty);
            tx /= len;
            ty /= len;
            float profile = (float)Math.pow(Math.sin(Math.PI * (double)Math.min(1.0f, (t - 0.02f) / 0.97f)), 0.75);
            boolean fluff = t < 0.3f;
            for (int side = -1; side <= 1; side += 2) {
                float gap = PeepSky.hash01(i * 7 + side * 3 + 11);
                if (!fluff && gap < 0.15f || side == 1 && i >= 50 && i <= 53 || side == -1 && i >= 82 && i <= 85) continue;
                float reach = (float)w * (fluff ? 0.36f : 0.4f) * profile * (0.9f + 0.1f * gap) * (side < 0 ? 0.7f : 1.05f);
                float angle = (fluff ? 0.55f : 0.95f) * (float)side;
                float dx = tx * (float)Math.cos(angle) - ty * (float)Math.sin(angle);
                float dy = tx * (float)Math.sin(angle) + ty * (float)Math.cos(angle);
                float bend = fluff ? 0.35f : 0.12f;
                g.setColor(new Color(1.0f, 1.0f, 1.0f, fluff ? 0.32f : 0.72f));
                g.setStroke(new BasicStroke(fluff ? 0.9f : 1.05f, 1, 1));
                g.draw(new QuadCurve2D.Float(px, py, px + dx * reach * 0.55f, py + dy * reach * 0.55f, px + dx * reach - (float)side * ty * reach * bend * 0.0f + tx * reach * bend, py + dy * reach + ty * reach * bend));
            }
        }
        g.dispose();
        BufferedImage quill = new BufferedImage(w, h, 2);
        g = PeepArt.smooth(quill);
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        g.setStroke(new BasicStroke(1.7f, 1, 1));
        g.draw(shaft);
        g.dispose();
        float[] barb = PeepArt.alpha(image);
        float[] stem = PeepArt.alpha(quill);
        float[] a = new float[barb.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = stem[i] + barb[i] * (1.0f - stem[i]);
        }
        float[] soft = PeepSky.blur(a, w, h, 1);
        for (int i = 0; i < a.length; ++i) {
            a[i] = Math.min(1.0f, a[i] * 0.8f + soft[i] * 0.45f);
        }
        PeepArt.fadeBorder(a, w, h, 2);
        float[] bright = PeepSky.blur(stem, w, h, 1);
        int[] out = new int[w * h];
        for (int i = 0; i < out.length; ++i) {
            out[i] = class_9848.method_71346((float)class_3532.method_15363((float)a[i], (float)0.0f, (float)1.0f), (int)PeepArt.mix(16764134, 0xFFFFFF, Math.min(1.0f, bright[i] * 1.6f)));
        }
        return new Art(w, h, out);
    }

    private static Art wisp() {
        int w = 96;
        int h = 256;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            float t = ((float)y + 0.5f) / (float)h;
            float along = PeepArt.smooth(0.0f, 0.2f, t) * (float)Math.pow(1.0f - t, 1.5);
            float bend = (float)Math.pow(t, 1.2);
            float centre = 0.5f + 0.13f * (float)Math.sin(t * 6.0f + 0.4f) * bend;
            float second = 0.5f + 0.13f * (float)Math.sin(t * 6.0f + 2.3f) * bend;
            float sigma = (float)w * (0.12f * (float)Math.pow(1.0f - t, 0.8) + 0.018f);
            for (int x = 0; x < w; ++x) {
                float nx = ((float)x + 0.5f) / (float)w;
                float veil = PeepArt.gauss((nx - centre) * (float)w, sigma);
                float strand = 0.5f * PeepArt.gauss((nx - second) * (float)w, sigma * 0.6f) * PeepArt.smooth(0.22f, 0.5f, t);
                float core = 0.55f * PeepArt.gauss((nx - centre) * (float)w, sigma * 0.35f) * (1.0f - t);
                a[y * w + x] = Math.min(1.0f, along * (0.7f * veil + strand + core));
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(PeepSky.blur(a, w, h, 3), w, h, 3)));
    }

    private static Art beam() {
        int w = 128;
        int h = 512;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            float t = 1.0f - ((float)y + 0.5f) / (float)h;
            float along = PeepArt.smooth(0.0f, 0.09f, t) * (float)Math.pow(1.0f - t, 1.25);
            float sigma = 1.6f + 27.0f * t;
            float core = 0.7f + 3.2f * t;
            for (int x = 0; x < w; ++x) {
                float dx = (float)x + 0.5f - (float)w * 0.5f;
                a[y * w + x] = Math.min(1.0f, along * (0.7f * PeepArt.gauss(dx, sigma) + 0.45f * PeepArt.gauss(dx, core) * (1.0f - t)));
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 2)));
    }

    private static Art cone() {
        int w = 256;
        int h = 512;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            float t = 1.0f - ((float)y + 0.5f) / (float)h;
            float along = PeepArt.smooth(0.0f, 0.035f, t) * (1.0f - PeepArt.smooth(0.3f, 1.0f, t)) * (0.6f + 0.4f * (1.0f - t));
            float half = (0.05f + 0.84999996f * t) * (float)w * 0.5f;
            for (int x = 0; x < w; ++x) {
                float across = ((float)x + 0.5f - (float)w * 0.5f) / half;
                float r = Math.abs(across);
                if (r >= 1.2f) continue;
                float body = 1.0f - PeepArt.smooth(0.5f, 1.05f, r);
                float axis = 0.35f * PeepArt.gauss(r, 0.2f);
                float shafts = 0.88f + 0.12f * PeepArt.valueNoise((across + 1.2f) * 3.5f, t * 2.2f, 64, 1);
                a[y * w + x] = Math.min(1.0f, along * (0.7f * body + axis) * shafts);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 2)));
    }

    private static Art glint() {
        int w = 32;
        int h = 32;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float dx = (float)x + 0.5f - (float)w * 0.5f;
                float dy = (float)y + 0.5f - (float)h * 0.5f;
                float d = (float)Math.hypot(dx, dy);
                float rays = PeepArt.glintRay(dx, dy) + PeepArt.glintRay(dy, dx);
                float core = PeepArt.gauss(d, 1.6f) + 0.4f * PeepArt.gauss(d, 4.0f);
                a[y * w + x] = Math.min(1.0f, rays + core);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 1)));
    }

    private static float glintRay(float along, float across) {
        float reach = Math.max(0.0f, 1.0f - Math.abs(along) / 15.0f);
        return PeepArt.gauss(across, 0.7f + 0.9f * reach) * reach * reach * 0.9f;
    }

    private static Art twinkle() {
        int w = 64;
        int h = 64;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float dx = (float)x + 0.5f - (float)w * 0.5f;
                float dy = (float)y + 0.5f - (float)h * 0.5f;
                float ray = PeepArt.ray(dx, dy, 29.0f) + PeepArt.ray(dy, dx, 29.0f);
                float diag = 0.35f * (PeepArt.ray((dx + dy) * 0.7071f, (dx - dy) * 0.7071f, 14.0f) + PeepArt.ray((dx - dy) * 0.7071f, (dx + dy) * 0.7071f, 14.0f));
                float core = PeepArt.gauss((float)Math.hypot(dx, dy), 2.6f) + 0.35f * PeepArt.gauss((float)Math.hypot(dx, dy), 7.0f);
                a[y * w + x] = Math.min(1.0f, ray + diag + core);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 2)));
    }

    private static Art arc() {
        int w = 256;
        int h = 256;
        float radius = 0.421875f * (float)w;
        float sweep = 2.6f;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                float head;
                float dx = (float)x + 0.5f - (float)w * 0.5f;
                float dy = (float)y + 0.5f - (float)h * 0.5f;
                float r = (float)Math.hypot(dx, dy);
                float behind = (float)(-Math.atan2(dy, dx));
                if (behind < 0.0f) {
                    behind += (float)Math.PI * 2;
                }
                float f = behind < 0.06f ? 1.0f : (head = behind > sweep ? 0.0f : (float)Math.pow(1.0f - behind / sweep, 1.6));
                if (behind > 6.2031856f) {
                    head = (float)Math.cos(((float)Math.PI * 2 - behind) / 0.08f * 1.5707964f);
                }
                float line = PeepArt.gauss(r - radius, 1.3f) + 0.3f * PeepArt.gauss(r - radius, 4.5f);
                a[y * w + x] = Math.min(1.0f, line * head);
            }
        }
        return new Art(w, h, PeepArt.white(PeepArt.fadeBorder(a, w, h, 2)));
    }

    private static Art mist() {
        int x;
        int w = 512;
        int h = 96;
        float[] a = new float[w * h];
        for (int y = 0; y < h; ++y) {
            float t = ((float)y + 0.5f) / (float)h;
            float profile = (float)Math.pow(Math.sin(Math.PI * (double)t), 1.4) * (0.55f + 0.45f * t);
            for (x = 0; x < w; ++x) {
                float n = 0.0f;
                float amp = 0.55f;
                float total = 0.0f;
                for (int octave = 0; octave < 4; ++octave) {
                    int cells = 4 << octave;
                    n += amp * PeepArt.valueNoise((float)x / (float)w * (float)cells, t * (2.5f + (float)octave * 2.5f) + (float)octave * 7.3f, cells, octave);
                    total += amp;
                    amp *= 0.5f;
                }
                a[y * w + x] = profile * PeepArt.smooth(0.28f, 0.78f, n /= total);
            }
        }
        float[] out = PeepSky.blur(a, w, h, 1);
        for (int y = 0; y < h; ++y) {
            float edge = PeepArt.smooth(0.0f, 3.0f, y) * PeepArt.smooth(0.0f, 3.0f, h - 1 - y);
            for (x = 0; x < w; ++x) {
                int n = y * w + x;
                out[n] = out[n] * edge;
            }
        }
        return new Art(w, h, PeepArt.white(out));
    }

    private static Art bolt(int seed, boolean fork) {
        int w = 128;
        int h = 256;
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics2D g = PeepArt.smooth(image);
        g.setColor(Color.WHITE);
        float[] xs = new float[33];
        float[] ys = new float[33];
        PeepArt.jag(xs, ys, (float)w * 0.5f, 12.0f, (float)w * 0.5f + (PeepSky.hash01(seed) - 0.5f) * 30.0f, (float)h - 12.0f, seed);
        g.setStroke(new BasicStroke(2.6f, 1, 1));
        g.draw(PeepArt.polyline(xs, ys));
        float[] bx = new float[17];
        float[] by = new float[17];
        int branches = fork ? 2 : 1;
        for (int b = 0; b < branches; ++b) {
            int from = 9 + b * 8;
            float side = (b % 2 == 0 ? 1.0f : -1.0f) * (PeepSky.hash01(seed + b) < 0.5f ? 1.0f : -1.0f);
            PeepArt.jag(bx, by, xs[from], ys[from], xs[from] + side * (26.0f + 14.0f * (float)b), ys[from] + 58.0f - 10.0f * (float)b, seed * 3 + 1 + b);
            g.setStroke(new BasicStroke(1.5f - 0.3f * (float)b, 1, 1));
            g.draw(PeepArt.polyline(bx, by));
        }
        g.dispose();
        float[] core = PeepArt.alpha(image);
        for (int y = 0; y < h; ++y) {
            float out = PeepArt.smooth(12.0f, 44.0f, y);
            for (int x = 0; x < w; ++x) {
                int n = y * w + x;
                core[n] = core[n] * out;
            }
        }
        float[] glow = PeepSky.blur(core, w, h, 6);
        int[] out = new int[w * h];
        for (int i = 0; i < out.length; ++i) {
            float halo = Math.min(1.0f, glow[i] * 3.0f);
            out[i] = class_9848.method_71346((float)class_3532.method_15363((float)Math.max(core[i], halo * 0.7f), (float)0.0f, (float)1.0f), (int)PeepArt.mix(16732080, 0xFFF4FB, core[i]));
        }
        return new Art(w, h, out);
    }

    private static void jag(float[] xs, float[] ys, float x0, float y0, float x1, float y1, int seed) {
        int n = xs.length - 1;
        xs[0] = x0;
        ys[0] = y0;
        xs[n] = x1;
        ys[n] = y1;
        float spread = (float)Math.hypot(x1 - x0, y1 - y0) * 0.32f;
        for (int step = n; step > 1; step /= 2) {
            int i = 0;
            while (i + step <= n) {
                int mid = i + step / 2;
                xs[mid] = (xs[i] + xs[i + step]) * 0.5f + (PeepSky.hash01(seed * 131 + mid * 17 + step) - 0.5f) * spread;
                float y = (ys[i] + ys[i + step]) * 0.5f + (PeepSky.hash01(seed * 151 + mid * 13 + step) - 0.5f) * spread * 0.25f;
                float a = ys[i] + 0.3f * (ys[i + step] - ys[i]);
                float b = ys[i] + 0.7f * (ys[i + step] - ys[i]);
                ys[mid] = class_3532.method_15363((float)y, (float)Math.min(a, b), (float)Math.max(a, b));
                i += step;
            }
            spread *= 0.62f;
        }
    }

    private static Path2D.Float polyline(float[] xs, float[] ys) {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(xs[0], ys[0]);
        for (int i = 1; i < xs.length; ++i) {
            path.lineTo(xs[i], ys[i]);
        }
        return path;
    }

    private static float ray(float along, float across, float length) {
        float reach = Math.max(0.0f, 1.0f - Math.abs(along) / length);
        return PeepArt.gauss(across, 0.85f + 0.6f * reach) * reach * reach;
    }

    private static float valueNoise(float x, float y, int period, int octave) {
        int x0 = (int)Math.floor(x);
        int y0 = (int)Math.floor(y);
        float fx = x - (float)x0;
        float fy = y - (float)y0;
        fx = fx * fx * (3.0f - 2.0f * fx);
        fy = fy * fy * (3.0f - 2.0f * fy);
        float a = PeepArt.lattice(x0, y0, period, octave);
        float b = PeepArt.lattice(x0 + 1, y0, period, octave);
        float c = PeepArt.lattice(x0, y0 + 1, period, octave);
        float d = PeepArt.lattice(x0 + 1, y0 + 1, period, octave);
        return class_3532.method_16439((float)fy, (float)class_3532.method_16439((float)fx, (float)a, (float)b), (float)class_3532.method_16439((float)fx, (float)c, (float)d));
    }

    private static float lattice(int x, int y, int period, int octave) {
        return PeepSky.hash01(Math.floorMod(x, period) * 7919 + y * 104729 + octave * 15485863);
    }

    private static float[] fill(Path2D.Float unitShape, int w, int h, int pad) {
        BufferedImage image = new BufferedImage(w, h, 2);
        Graphics2D g = PeepArt.smooth(image);
        g.translate(pad, pad);
        g.scale((double)w - (double)pad * 2.0, (double)h - (double)pad * 2.0);
        g.setColor(Color.WHITE);
        g.fill(unitShape);
        g.dispose();
        return PeepArt.alpha(image);
    }

    private static Graphics2D smooth(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    private static float[] alpha(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] px = image.getRGB(0, 0, w, h, null, 0, w);
        float[] a = new float[px.length];
        for (int i = 0; i < a.length; ++i) {
            a[i] = (float)(px[i] >>> 24) / 255.0f;
        }
        return a;
    }

    private static float[] fadeBorder(float[] a, int w, int h, int border) {
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int d = Math.min(Math.min(x, w - 1 - x), Math.min(y, h - 1 - y));
                if (d >= border) continue;
                int n = y * w + x;
                a[n] = a[n] * ((float)d / (float)border);
            }
        }
        return a;
    }

    private static int[] white(float[] a) {
        int[] out = new int[a.length];
        for (int i = 0; i < a.length; ++i) {
            out[i] = Math.round(class_3532.method_15363((float)a[i], (float)0.0f, (float)1.0f) * 255.0f) << 24 | 0xFFFFFF;
        }
        return out;
    }

    private static float quad(float a, float b, float c, float t) {
        float s = 1.0f - t;
        return s * s * a + 2.0f * s * t * b + t * t * c;
    }

    private static float quadSlope(float a, float b, float c, float t) {
        return 2.0f * (1.0f - t) * (b - a) + 2.0f * t * (c - b);
    }

    private static float gauss(float d, float sigma) {
        return (float)Math.exp(-d * d / (2.0f * sigma * sigma));
    }

    private static float smooth(float from, float to, float x) {
        float t = class_3532.method_15363((float)((x - from) / (to - from)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static int mix(int from, int to, float t) {
        return class_9848.method_61319((float)class_3532.method_15363((float)t, (float)0.0f, (float)1.0f), (int)from, (int)to) & 0xFFFFFF;
    }

    @Environment(value=EnvType.CLIENT)
    record Art(int width, int height, int[] argb) {
    }
}

