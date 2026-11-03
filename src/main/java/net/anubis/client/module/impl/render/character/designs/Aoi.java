/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.designs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.anubis.client.module.impl.render.character.kit.Bone;
import net.anubis.client.module.impl.render.character.kit.Canvas;
import net.anubis.client.module.impl.render.character.kit.CharacterDesign;
import net.anubis.client.module.impl.render.character.kit.Colors;
import net.anubis.client.module.impl.render.character.kit.Decal;
import net.anubis.client.module.impl.render.character.kit.Motion;
import net.anubis.client.module.impl.render.character.kit.Paint;
import net.anubis.client.module.impl.render.character.kit.Paint2D;
import net.anubis.client.module.impl.render.character.kit.Part;
import net.anubis.client.module.impl.render.character.kit.Rig;
import net.anubis.client.module.impl.render.character.kit.Shape;
import net.anubis.client.module.impl.render.character.kit.Shape2D;
import net.anubis.client.module.impl.render.character.kit.Shapes;
import net.anubis.client.module.impl.render.character.kit.Strand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Aoi
extends CharacterDesign {
    private static final float TAU = (float)Math.PI * 2;
    private static final float NECK = 24.4f;
    private static final float HEAD_K = 1.16f;
    private static final float HEAD_DY = -0.25f;
    private static final float FACE_X = 2.25f;
    private static final float FACE_TOP = 28.6f;
    private static final float FACE_LOW = 26.08f;
    private static final float FACE_PX = 80.0f;
    private static final float YOKE = 21.75f;
    private static final float BEND = 13.0f;
    private static final int SKIN = -9274;
    private static final int SKIN_SHADE = -1331558;
    private static final int SKIN_LINE = -6657438;
    private static final int HAIR = -14140794;
    private static final int HAIR_DEEP = -15063975;
    private static final int HAIR_TOP = -12756032;
    private static final int HAIR_BLUE = -12948262;
    private static final int HAIR_TIP = -13246209;
    private static final int HAIR_INNER = -13646610;
    private static final int HAIR_SHINE = -7363350;
    private static final int HAIR_LINE = -15327158;
    private static final int CYAN = -13703425;
    private static final int NAVY = -14932394;
    private static final int JACKET = -12959662;
    private static final int JACKET_DARK = -14210250;
    private static final int JACKET_WHITE = -1446155;
    private static final int JACKET_SHADE = -3945254;
    private static final int LINE = -15065558;
    private static final int TOP = -525828;
    private static final int TOP_SHADE = -2498584;
    private static final int SKIRT = -12892555;
    private static final int SKIRT_DARK = -14472632;
    private static final int BLACK = -15263457;
    private static final int SILVER = -3550757;
    private static final int SOCK = -14012606;
    private static final int SOLE = -920586;
    private static final float SKIRT_ZC = -0.05f;
    private static final float SKIRT_TOP = 17.3f;
    private static final float SKIRT_HEM = 11.25f;
    private static final int PLEATS = 18;
    private static final float[][] MAIN_LOCKS = new float[][]{{0.95f, -1.55f, 29.85f, -2.0f, 29.5f, -2.3f, 28.45f, -2.24f, 27.65f, -2.0f, 27.42f}, {0.95f, -1.15f, 29.95f, -1.38f, 29.5f, -1.6f, 28.6f, -1.55f, 28.12f, -1.3f, 27.93f}, {0.85f, -0.64f, 29.95f, -0.45f, 29.4f, -0.08f, 28.25f, 0.16f, 27.4f, 0.04f, 27.02f}, {1.0f, -0.08f, 30.0f, 0.35f, 29.6f, 0.9f, 28.7f, 1.2f, 28.22f, 0.98f, 28.0f}, {1.05f, 0.56f, 30.0f, 1.15f, 29.7f, 1.8f, 28.75f, 2.08f, 27.8f, 1.85f, 27.45f}};
    private static final float[][] ACCENT_LOCKS = new float[][]{{0.55f, -0.95f, 29.85f, -1.0f, 29.3f, -0.9f, 28.5f, -0.76f, 28.3f}, {0.55f, -0.22f, 29.85f, 0.14f, 29.35f, 0.52f, 28.72f, 0.44f, 28.55f}, {0.5f, 0.5f, 29.85f, 1.02f, 29.4f, 1.5f, 28.5f, 1.44f, 28.2f}};

    public Aoi() {
        super("aoi", "Aoi");
    }

    @Override
    protected void define(Rig rig) {
        rig.scale(1.0f).outline(-15065558, 0.13f).occlusion(0.35f);
        Canvas stripe = Aoi.stripe();
        Shape head = Aoi.headShape();
        List<float[]> main = Aoi.fringeLocks(head, MAIN_LOCKS, 0.2f, 0.17f);
        List<float[]> accent = Aoi.fringeLocks(head, ACCENT_LOCKS, 0.2f, 0.17f);
        rig.part(Bone.HEAD).pivot(0.0f, 24.4f, 0.0f).shape(Aoi.grow(head.color(-9274))).detail(1.1f).resolution(0.85f).outline(-6657438, 0.09f).flatLight(1.0f).occlusion(0.2f).decal(Aoi.face(main));
        Aoi.hair(rig, main, accent);
        Aoi.ponytail(rig);
        Shape jacket = Aoi.body(rig, stripe);
        Aoi.skirt(rig, stripe);
        Aoi.arms(rig, stripe);
        Aoi.legs(rig);
        Aoi.gear(rig, jacket);
    }

    private static Shape grow(Shape s) {
        return s.scale(1.16f, 0.0f, 24.4f, 0.0f).move(0.0f, -0.25f, 0.0f);
    }

    private static float hy(float y) {
        return 24.4f + (y - 24.4f) * 1.16f + -0.25f;
    }

    private static Shape headShape() {
        Shape skull = Shapes.ellipsoid(0.0f, 28.52f, -0.15f, 2.28f, 2.24f, 2.32f);
        Shape cheek = Shapes.ellipsoid(0.0f, 27.4f, 0.7f, 1.95f, 1.1f, 1.45f);
        Shape jaw = Shapes.cone(0.0f, 27.25f, 0.42f, 1.68f, 0.0f, 26.15f, 1.18f, 0.55f);
        Shape jawline = Shapes.extrude(Shape2D.polygon(-2.6f, 31.5f, -2.6f, 29.0f, -2.24f, 27.6f, -2.11f, 27.2f, -1.9f, 26.8f, -1.62f, 26.44f, -1.28f, 26.12f, -0.92f, 25.91f, -0.54f, 25.79f, -0.25f, 25.75f, 0.0f, 25.74f, 0.25f, 25.75f, 0.54f, 25.79f, 0.92f, 25.91f, 1.28f, 26.12f, 1.62f, 26.44f, 1.9f, 26.8f, 2.11f, 27.2f, 2.24f, 27.6f, 2.6f, 29.0f, 2.6f, 31.5f), 4.0f, 0.0f);
        Shape face = Shapes.blend(0.9f, skull, cheek, jaw);
        Shape cut = face.intersect(0.3f, jawline);
        return Shapes.field((x, y, z) -> {
            float d = y > 28.8f ? face.distance(x, y, z) : cut.distance(x, y, z);
            return Math.max(d, Math.max(-1.0f - z, y - 30.1f));
        }, face.minX(), face.minY(), -1.1f, face.maxX(), 30.2f, face.maxZ());
    }

    private static List<float[]> fringeLocks(Shape head, float[][] data, float mid, float tip) {
        ArrayList<float[]> out = new ArrayList<float[]>();
        for (float[] k : data) {
            int n = (k.length - 1) / 2;
            float[] lock = new float[n * 4];
            for (int i = 0; i < n; ++i) {
                float x = k[1 + i * 2];
                float y = k[2 + i * 2];
                float u = 1.0f - x * x / 6.76f - (y - 28.62f) * (y - 28.62f) / 7.08f;
                float z = Math.max(Aoi.frontZ(head, x, y, 5.0f), -0.5f + 2.64f * (float)Math.sqrt(Math.max(0.0f, u)));
                float dx = x;
                float dy = y - 28.3f;
                float dz = z + 0.4f;
                float l = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                float t = (float)i / ((float)n - 1.0f);
                float lift = (i == 0 ? 0.1f : (i == n - 1 ? tip : mid)) + 0.22f * Aoi.smooth(1.3f, 2.2f, Math.abs(x));
                lock[i * 4] = x + dx / l * lift;
                lock[1 + i * 4] = y + dy / l * lift;
                lock[2 + i * 4] = z + dz / l * lift;
                lock[3 + i * 4] = k[0] * (float)Math.pow(1.0f - t, 1.2f) + 0.05f * t;
            }
            out.add(lock);
        }
        return out;
    }

    private static void hair(Rig rig, List<float[]> main, List<float[]> accent) {
        ArrayList<Shape> parts = new ArrayList<Shape>();
        parts.add(Shapes.subtract(0.35f, Shapes.ellipsoid(0.0f, 28.62f, -0.2f, 2.6f, 2.66f, 2.64f), Shapes.ellipsoid(0.0f, 26.75f, 2.75f, 2.45f, 2.85f, 2.72f)).intersect(Shapes.above(26.0f)));
        for (float a : new float[]{-155.0f, -118.0f, -80.0f, 80.0f, 118.0f, 155.0f}) {
            parts.add(Aoi.flowLock(a));
        }
        rig.extra("hair", Bone.HEAD).shape(Aoi.grow(Shapes.union(parts.toArray(new Shape[0])).paint(Aoi.capPaint()))).detail(0.8f).resolution(0.95f).outline(-15327158, 0.1f).flatLight(0.5f).occlusion(0.4f);
        Part bangs = rig.extra("fringe", Bone.HEAD).shape(Aoi.grow(Aoi.fringe(main, accent))).detail(0.4f).resolution(0.55f).outline(-15327158, 0.09f).flatLight(0.5f).occlusion(0.2f);
        Aoi.shine(bangs, main);
        Motion lockMotion = Motion.hair().idle(1.5f, 0.0f, 1.5f, 0.35f).walk(3.0f, 0.0f, 3.0f, 2.0f).drag(8.0f).hang(0.7f).turn(10.0f, 0.0f).spring(60.0f, 7.0f).bend(1.3f);
        Paint lockPaint = Paint.field((x, y, z) -> Colors.mix(Colors.mix(-14140794, -12948262, Aoi.smooth(28.2f, 25.8f, y)), -13246209, Aoi.smooth(26.2f, 24.4f, y)));
        Paint innerPaint = Paint.field((x, y, z) -> Colors.mix(-13646610, -13246209, Aoi.smooth(27.0f, 25.0f, y)));
        Shape lockR = Shapes.union(Shapes.tube(-1.55f, 29.95f, 1.0f, 0.4f, -2.32f, 28.5f, 1.4f, 0.55f, -2.55f, 27.1f, 1.3f, 0.52f, -2.48f, 25.9f, 1.15f, 0.42f, -2.3f, 24.9f, 1.2f, 0.28f, -2.1f, 24.3f, 1.34f, 0.04f).flattenFrom(2.0f, 0.0f, 27.5f, -0.5f).paint(lockPaint), Shapes.tube(-1.7f, 29.7f, 0.7f, 0.36f, -2.28f, 28.2f, 1.05f, 0.44f, -2.36f, 26.8f, 0.95f, 0.42f, -2.22f, 25.6f, 0.9f, 0.3f, -2.05f, 24.55f, 1.0f, 0.04f).flattenFrom(2.0f, 0.0f, 27.5f, -0.5f).paint(innerPaint));
        Part right = rig.extra("lock_r", Bone.HEAD).pivot(-2.32f, Aoi.hy(29.2f), 1.16f).shape(Aoi.grow(lockR)).motion(lockMotion).detail(0.7f).resolution(0.7f).outline(-15327158, 0.1f).flatLight(0.5f).occlusion(0.35f);
        rig.extra("lock_l", Bone.HEAD).mirrorOf(right);
    }

    private static Shape flowLock(float angleDeg) {
        float cx = 0.0f;
        float cy = 28.62f;
        float cz = -0.2f;
        float rx = 2.6f;
        float ry = 2.66f;
        float rz = 2.64f;
        float a = (float)Math.toRadians(angleDeg);
        float abs = Math.abs(angleDeg);
        float y0 = 29.1f - 1.7f * Aoi.smooth(20.0f, 90.0f, abs) - 1.2f * Aoi.smooth(95.0f, 160.0f, abs);
        float e = (float)Math.asin(Math.max(-0.95f, Math.min(0.95f, (y0 - cy) / ry)));
        float ux = (float)(Math.sin(a) * Math.cos(e));
        float uy = (float)Math.sin(e);
        float uz = (float)(Math.cos(a) * Math.cos(e));
        float tx = 0.0f;
        float ty = 30.25f - cy;
        float tz = -1.95f - cz;
        float tl = (float)Math.sqrt(tx * tx + ty * ty + tz * tz);
        tx /= tl;
        ty /= tl;
        tz /= tl;
        float[] f = new float[]{0.0f, 0.35f, 0.7f, 1.0f};
        float[] radius = new float[]{0.5f, 0.56f, 0.5f, 0.32f};
        float[] pts = new float[16];
        for (int i = 0; i < 4; ++i) {
            float dx = ux + (tx - ux) * f[i];
            float dy = uy + (ty - uy) * f[i];
            float dz = uz + (tz - uz) * f[i];
            float l = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
            float r = 1.0f / (float)Math.sqrt((dx /= l) * dx / (rx * rx) + (dy /= l) * dy / (ry * ry) + (dz /= l) * dz / (rz * rz));
            float lift = i == 3 ? -0.1f : 0.04f;
            pts[i * 4] = cx + dx * (r + lift);
            pts[i * 4 + 1] = cy + dy * (r + lift);
            pts[i * 4 + 2] = cz + dz * (r + lift);
            pts[i * 4 + 3] = radius[i];
        }
        return Shapes.tube(pts).flattenFrom(2.4f, cx, cy, cz);
    }

    private static Paint capPaint() {
        return Paint.field((x, y, z) -> Colors.mix(Colors.mix(-15063975, -14140794, Aoi.smooth(26.2f, 28.4f, y)), -12756032, Aoi.smooth(29.4f, 31.2f, y) * 0.8f));
    }

    private static Shape fringe(List<float[]> main, List<float[]> accent) {
        Shape[] parts = new Shape[main.size() + accent.size()];
        for (int i = 0; i < parts.length; ++i) {
            boolean top = i >= main.size();
            float[] k = top ? accent.get(i - main.size()) : main.get(i);
            int n = k.length / 4;
            float[] xy = new float[n * 2];
            for (int j = 0; j < n; ++j) {
                xy[j * 2] = k[j * 4];
                xy[j * 2 + 1] = k[j * 4 + 1];
            }
            float tint = top ? 1.12f : ((i & 1) == 0 ? 0.93f : 1.04f);
            parts[i] = Shapes.tube(k).flattenFrom(2.6f, 0.0f, 28.4f, -0.3f).intersect(Shapes.extrude(Shape2D.path(k[3] * 2.6f, 0.02f, xy), 4.0f, 0.0f)).paint(Paint.field((x, y, z) -> Colors.shade(Colors.mix(Colors.mix(-14140794, -12756032, Aoi.smooth(29.6f, 30.6f, y) * 0.5f), -12948262, Aoi.smooth(28.9f, 27.4f, y) * 0.85f), tint)));
        }
        return Shapes.union(parts);
    }

    private static void shine(Part bangs, List<float[]> locks) {
        for (int i = 0; i < locks.size(); ++i) {
            float[] k = locks.get(i);
            int n = k.length / 4;
            for (int j = 1; j < n; ++j) {
                float y0 = k[j * 4 - 3];
                float y1 = k[j * 4 + 1];
                if (y0 < 29.15f || y1 > 29.15f) continue;
                float t = (y0 - 29.15f) / (y0 - y1);
                float x = k[j * 4 - 4] + (k[j * 4] - k[j * 4 - 4]) * t;
                float z = k[j * 4 - 2] + (k[j * 4 + 2] - k[j * 4 - 2]) * t;
                float r = k[j * 4 - 1] + (k[j * 4 + 3] - k[j * 4 - 1]) * t;
                float mid = (float)Math.atan2(x, z + 0.2f);
                if (Math.abs(mid) > 0.96f) continue;
                float span = r * 1.1f / (float)Math.sqrt(x * x + (z + 0.2f) * (z + 0.2f));
                float w = 7.0f + (float)Math.toDegrees(span) * 0.25f;
                float px = 64.0f / (span * 1.5f);
                float[] a = new float[6];
                float[] b = new float[6];
                for (int q = 0; q < 3; ++q) {
                    float da = span * 0.6f * ((float)q * 0.5f - 1.0f);
                    float db = span * 0.6f * (float)q * 0.5f;
                    a[q * 2] = 32.0f + da * px;
                    a[q * 2 + 1] = 13.0f + 25.0f * ((float)Math.cos(mid) - (float)Math.cos(mid + da));
                    b[q * 2] = 32.0f + db * px;
                    b[q * 2 + 1] = 13.0f + 25.0f * ((float)Math.cos(mid) - (float)Math.cos(mid + db));
                }
                Canvas c = new Canvas(64, 26);
                c.fill(Shape2D.path(w * 0.2f, w, a).union(Shape2D.path(w, w * 0.2f, b)), Colors.withAlpha(-7363350, 0.5f), 5.0f);
                float cy = Aoi.hy(29.1f) + 0.081f - 0.29f * (1.0f - (float)Math.cos(mid)) + (float)((i & 1) * 2 - 1) * 0.03f;
                bangs.decal(Decal.wrap(c, 0.0f, cy, -0.232f, 0.0f, 1.0f, 0.0f, (float)Math.sin(mid), 0.0f, (float)Math.cos(mid), (float)Math.toDegrees(span * 1.5f), 0.30159998f).radius(1.8f, 4.2f).flatLight(0.8f));
            }
        }
    }

    private static void ponytail(Rig rig) {
        float[][] spray;
        float[][] spine = new float[][]{{30.4f, -2.0f}, {31.1f, -2.5f}, {31.35f, -3.25f}, {30.95f, -3.95f}, {29.7f, -4.3f}, {27.6f, -4.38f}, {25.1f, -4.22f}, {22.5f, -4.0f}, {19.9f, -3.8f}, {17.5f, -3.68f}, {15.3f, -3.74f}};
        int n = spine.length;
        float[] spread = new float[]{0.0f, 0.55f, 1.0f, 1.18f, 1.22f, 1.18f, 1.08f, 0.96f, 0.86f, 0.98f, 1.35f};
        float[] profile = new float[]{0.45f, 0.88f, 1.05f, 1.1f, 1.1f, 1.05f, 0.98f, 0.86f, 0.7f, 0.52f, 0.34f};
        float[] ny = new float[n];
        float[] nz = new float[n];
        for (int j = 0; j < n; ++j) {
            int a = Math.max(0, j - 1);
            int b = Math.min(n - 1, j + 1);
            float ty = spine[b][0] - spine[a][0];
            float tz = spine[b][1] - spine[a][1];
            float tl = (float)Math.sqrt(ty * ty + tz * tz);
            ny[j] = tz / tl;
            nz[j] = -ty / tl;
        }
        float[][] locks = new float[][]{{-1.02f, 0.25f, 10.1f, 0.68f, 0.0f, -1.0f}, {-0.5f, -0.3f, 10.25f, 0.72f, 1.1f, -1.0f}, {0.02f, 0.3f, 10.8f, 0.74f, 2.3f, 0.0f}, {0.54f, -0.28f, 9.7f, 0.72f, 0.6f, 1.0f}, {1.03f, 0.22f, 9.55f, 0.66f, 1.7f, 1.0f}, {-0.72f, 0.62f, 9.4f, 0.62f, 2.9f, -1.0f}, {0.74f, 0.6f, 9.5f, 0.62f, 0.3f, 1.0f}, {-0.05f, -0.62f, 10.6f, 0.64f, 2.0f, 0.0f}};
        ArrayList<Shape> strands = new ArrayList<Shape>();
        for (float[] k : locks) {
            int whole = Math.min((int)Math.floor(k[2]), n - 1);
            float frac = k[2] - (float)whole;
            float[] pts = new float[(whole + 2) * 4];
            for (int j = 0; j <= whole; ++j) {
                float s = spread[j];
                float g = Aoi.smooth(6.5f, 8.6f, j);
                float wave = 0.14f * (float)Math.sin((float)j * 0.9f + k[4]) * Aoi.smooth(1.0f, 4.0f, j) * (1.0f - g);
                float x2 = k[0] * s + wave;
                float off = k[1] * s * 0.75f * (1.0f - 0.6f * g);
                pts[j * 4] = x2 + (k[5] * 1.15f * s + k[0] * 0.2f * s - x2) * g;
                pts[j * 4 + 1] = spine[j][0] + ny[j] * off;
                pts[j * 4 + 2] = spine[j][1] + nz[j] * off;
                pts[j * 4 + 3] = k[3] * 1.08f * profile[j];
            }
            int o = (whole + 1) * 4;
            int next = Math.min(whole + 1, n - 1);
            float f = Math.max(frac, 0.45f);
            pts[o] = pts[whole * 4] * 1.12f + k[5] * 0.2f;
            pts[o + 1] = spine[whole][0] + (spine[next][0] - spine[whole][0]) * f - 1.1f;
            pts[o + 2] = pts[whole * 4 + 2] + (spine[next][1] - spine[whole][1]) * f - 0.1f;
            pts[o + 3] = 0.04f;
            Aoi.split(strands, pts, 6, 1.6f, true);
        }
        float[] core = new float[36];
        for (int j = 0; j < 9; ++j) {
            core[j * 4 + 1] = spine[j][0];
            core[j * 4 + 2] = spine[j][1];
            core[j * 4 + 3] = 0.64f * profile[j];
        }
        Aoi.split(strands, core, 5, 1.4f, false);
        for (float[] k : spray = new float[][]{{0.05f, 30.55f, -2.15f, 0.1f, 31.45f, -2.6f, 0.15f, 32.05f, -3.35f, 0.1f, 31.85f, -4.15f}, {0.3f, 30.5f, -2.1f, 0.72f, 31.25f, -2.7f, 0.98f, 31.72f, -3.45f, 1.08f, 31.42f, -4.05f}, {-0.35f, 30.5f, -2.1f, -0.76f, 31.2f, -2.75f, -1.02f, 31.62f, -3.4f, -1.12f, 31.32f, -4.0f}}) {
            strands.add(Shapes.tube(k[0], k[1], k[2], 0.36f, k[3], k[4], k[5], 0.44f, k[6], k[7], k[8], 0.3f, k[9], k[10], k[11], 0.04f).flattenFrom(1.8f, 0.0f, 26.0f, 0.5f));
        }
        Paint paint = Paint.field((x, y, z) -> {
            int c = Colors.mix(-14140794, -12948262, Aoi.smooth(27.5f, 21.5f, y));
            c = Colors.mix(c, -13246209, Aoi.smooth(21.5f, 16.0f, y));
            float s = Aoi.smooth(30.9f, 31.5f, y) * Aoi.smooth(-2.4f, -3.2f, z);
            return Colors.mix(c, -7363350, s * 0.5f);
        });
        Shape tail = Shapes.union(strands.toArray(new Shape[0])).paint(paint);
        Shape tie = Aoi.ring(0.0f, 30.38f, -2.05f, 0.0f, 0.55f, -0.83f, 0.52f, 0.38f).roughen(0.05f, 0.3f).color(-13703425);
        rig.extra("ponytail", Bone.HEAD).pivot(0.0f, Aoi.hy(30.35f), -2.3779998f).shape(Aoi.grow(Shapes.union(tail, tie))).detail(1.0f).resolution(1.3f).outline(-15327158, 0.12f).flatLight(0.4f).occlusion(0.4f).motion(Motion.hair().idle(3.0f, 0.0f, 2.5f, 0.3f).walk(7.0f, 0.0f, 5.0f, 2.0f).drag(22.0f).hang(0.3f).follow(Bone.BODY, 0.6f).turn(18.0f, 4.0f).spring(36.0f, 4.2f).bend(1.5f).limit(70.0f));
    }

    private static void split(List<Shape> out, float[] pts, int at, float flat, boolean sharp) {
        int n = pts.length / 4;
        if (n < at + 3) {
            out.add(Shapes.tube(pts).flattenFrom(flat, 0.0f, 26.0f, 0.5f));
            return;
        }
        out.add(Shapes.tube(Arrays.copyOfRange(pts, 0, (at + 2) * 4)).flattenFrom(flat, 0.0f, 26.0f, 0.5f));
        Shape end = Shapes.tube(Arrays.copyOfRange(pts, (at - 1) * 4, n * 4)).flattenFrom(flat, 0.0f, 26.0f, 0.5f);
        if (sharp) {
            int a = (n - 3) * 4;
            int b = (n - 2) * 4;
            int c = (n - 1) * 4;
            Shape2D tip = Shape2D.path(2.4f, 0.02f, pts[a], pts[a + 1], pts[b], pts[b + 1], pts[c], pts[c + 1]);
            end = end.intersect(Shapes.union(Shapes.above(pts[b + 1]), Shapes.extrude(tip, 8.0f, 0.0f)));
        }
        out.add(end);
    }

    private static Shape body(Rig rig, Canvas stripe) {
        Paint skin = Paint.field((x, y, z) -> Colors.mix(-9274, -1331558, Aoi.smooth(24.9f, 25.5f, y))).flatLight(1.0f).occlusion(0.1f);
        Shape neck = Shapes.cone(0.0f, 23.2f, -0.22f, 0.66f, 0.0f, 26.3f, 0.0f, 0.58f).paint(skin);
        Shape top = Shapes.blend(1.0f, Shapes.ellipsoid(0.0f, 21.15f, 0.0f, 2.25f, 2.05f, 1.45f), Shapes.ellipsoid(0.0f, 18.7f, 0.0f, 1.78f, 1.75f, 1.22f), Shapes.ellipsoid(0.0f, 16.6f, -0.05f, 2.0f, 1.5f, 1.4f), Shapes.capsule(-2.2f, 22.3f, -0.18f, 0.0f, 22.62f, -0.18f, 0.88f), Shapes.capsule(0.0f, 22.62f, -0.18f, 2.2f, 22.3f, -0.18f, 0.88f)).blend(0.6f, Shapes.ellipsoid(0.82f, 20.85f, 0.55f, 0.88f, 0.82f, 0.85f).mirrorX());
        top.paint(Paint.vertical(-2498584, 17.2f, -525828, 19.4f).flatLight(0.4f));
        Shape mock = Shapes.cone(0.0f, 23.0f, -0.2f, 0.9f, 0.0f, 25.3f, -0.12f, 0.74f).intersect(0.06f, Shapes.below(25.1f)).color(-15263457);
        Shape pelvis = Shapes.ellipsoid(0.0f, 14.95f, -0.05f, 1.95f, 1.2f, 1.35f).color(-15263457);
        Shape jacketVol = Shapes.blend(1.1f, Shapes.ellipsoid(0.0f, 21.0f, -0.1f, 2.7f, 2.4f, 1.98f), Shapes.capsule(-2.62f, 22.25f, -0.22f, 0.0f, 22.6f, -0.22f, 1.04f), Shapes.capsule(0.0f, 22.6f, -0.22f, 2.62f, 22.25f, -0.22f, 1.04f), Aoi.oval(-0.1f, 18.3f, 20.3f, 2.45f, 1.97f, 2.55f, 1.97f)).paint(Paint.field((x, y, z) -> Colors.mix(-12959662, -1446155, Aoi.smooth(21.72f, 21.78f, y))).flatLight(0.3f).occlusion(0.18f));
        Shape hem = Aoi.oval(-0.1f, 17.42f, 18.45f, 2.45f, 2.03f, 2.47f, 2.02f).color(-14210250);
        Shape collar = Shapes.torus(0.0f, 23.52f, -0.28f, 1.28f, 0.38f).rotate(16.0f, 0.0f, 0.0f, 0.0f, 23.52f, -0.28f).color(-14210250);
        Shape jacketSolid = Shapes.union(jacketVol, hem, collar);
        Shape jacket = Shapes.subtract(0.06f, jacketSolid, Aoi.opening(0.0f));
        float kz = Aoi.frontZ(jacketSolid, -1.85f, 18.95f, 6.0f);
        float by = 16.95f;
        float e = Aoi.flare(Aoi.skirtT(by));
        float brx = 1.85f + 1.6f * e + 0.22f;
        float brz = 1.5f + 1.4f * e + 0.22f;
        Shape belt = Aoi.ovalRing(-0.05f, by, 0.42f, brx, brz, 0.26f).color(-15263457);
        float bz = -0.05f + brz * (float)Math.sqrt(1.0f - 0.3025f / (brx * brx)) + 0.26f;
        Shape buckle = Shapes.box(0.55f, by, bz + 0.05f, 0.46f, 0.36f, 0.14f, 0.1f).color(-13749702);
        Part body = rig.part(Bone.BODY).pivot(0.0f, 24.4f, 0.0f).shape(Shapes.union(neck, mock, top, pelvis, jacket, belt, buckle)).detail(1.0f).outline(-15065558, 0.13f).occlusion(0.3f);
        Canvas back = Aoi.backStripe();
        Canvas band = Aoi.band();
        body.decal(Aoi.topGraphic()).decal(Decal.back(back, 2.02f, 19.6f, -2.0f, 0.6f, 4.0f).depth(1.4f, 1.4f)).decal(Decal.back(back, -2.02f, 19.6f, -2.0f, 0.6f, 4.0f).depth(1.4f, 1.4f)).decal(Decal.wrap(stripe, 0.0f, 18.12f, -0.1f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.3f).radius(1.95f, 3.2f)).decal(Decal.front(Aoi.pocket(), -1.85f, 18.95f, kz, 0.95f, 0.36f).depth(0.5f, 0.5f).maxAngle(70.0f)).decal(Decal.front(Aoi.pocket().mirrored(), 1.85f, 18.95f, kz, 0.95f, 0.36f).depth(0.5f, 0.5f).maxAngle(70.0f)).decal(Decal.front(band, 0.0f, 17.62f, 1.9f, 2.0f, 0.28f).depth(1.2f, 0.4f).maxAngle(70.0f)).decal(Decal.wrap(band, 0.0f, 24.8f, -0.14f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 240.0f, 0.26f).radius(0.4f, 1.05f)).decal(Decal.front(Aoi.buckle(), 0.55f, by, bz + 0.19f, 0.92f, 0.72f).depth(0.4f, 0.3f).maxAngle(60.0f));
        Shape strap = Shapes.union(Shapes.tube(0.98f, 16.7f, bz + 0.02f, 0.28f, 1.02f, 15.8f, bz + 0.26f, 0.28f, 1.05f, 14.9f, bz + 0.45f, 0.27f, 1.07f, 14.3f, bz + 0.55f, 0.26f).flatten(2.2f, 0.0f, 0.0f, 1.0f).color(-15263457), Shapes.box(1.07f, 14.15f, bz + 0.56f, 0.28f, 0.2f, 0.13f, 0.08f).color(-3550757));
        rig.extra("strap", Bone.BODY).pivot(0.98f, 16.7f, bz).shape(strap).resolution(0.7f).detail(0.3f).noOutline().motion(Motion.cloth().walk(8.0f, 0.0f, 5.0f, 2.0f).drag(6.0f).spring(40.0f, 5.0f).bend(1.2f));
        return jacketSolid;
    }

    private static float openingWidth(float y) {
        return 0.85f + 0.65f * (24.9f - y) / 10.1f;
    }

    private static Shape opening(float grow) {
        return Shapes.extrude(Shape2D.polygon(-0.85f - grow, 24.9f, 0.85f + grow, 24.9f, 1.5f + grow, 14.8f, -1.5f - grow, 14.8f), 2.6f, 0.0f).move(0.0f, 0.0f, 3.15f);
    }

    private static Shape piping(Shape jacket, float side, float y0, float y1) {
        float[] pts = new float[24];
        for (int i = 0; i < 8; ++i) {
            float y = y0 + (y1 - y0) * (float)i / 7.0f;
            float w = Aoi.openingWidth(y);
            pts[i * 3] = side * (w + 0.09f);
            pts[i * 3 + 1] = y;
            pts[i * 3 + 2] = Aoi.frontZ(jacket, side * (w + 0.1f), y, 6.0f) - 0.06f;
        }
        return Shapes.strand(0.13f, 0.13f, pts);
    }

    private static void skirt(Rig rig, Canvas stripe) {
        Paint paint = Paint.field((x, y, z) -> {
            float theta = (float)Math.atan2(x, z - -0.05f);
            float p = Aoi.pleat(theta * 18.0f / ((float)Math.PI * 2));
            return Colors.mix(-14472632, -12892555, 0.4f + 0.6f * (0.5f + 0.5f * p));
        }).flatLight(0.15f);
        Decal hem = Decal.wrap(stripe, 0.0f, 11.77f, -0.05f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.27f).radius(2.4f, 4.6f);
        float half = (float)Math.toRadians(30.0);
        float tuck = (float)Math.toRadians(14.0);
        String[] names = new String[]{"skirt_f", "skirt_s", "skirt_b"};
        float[] angle = new float[]{-30.0f, -90.0f, -150.0f};
        float[] follow = new float[]{0.5f, 0.42f, 0.5f};
        float[] limit = new float[]{16.0f, 13.0f, 15.0f};
        for (int i = 0; i < 3; ++i) {
            float a = (float)Math.toRadians(angle[i]);
            float px = 1.8f * (float)Math.sin(a);
            float pz = -0.05f + 1.45f * (float)Math.cos(a);
            Motion m = Motion.skirt(Bone.RIGHT_LEG).follow(Bone.RIGHT_LEG, follow[i], limit[i]).drag(i == 0 ? 0.0f : 3.0f);
            Part right = rig.extra(names[i] + "_r", Bone.BODY).pivot(px, 16.9f, pz).shape(Aoi.skirtPanel(a, half, tuck).paint(paint)).motion(m).detail(0.32f).resolution(1.0f).occlusion(0.3f).noOutline().decal(hem);
            rig.extra(names[i] + "_l", Bone.BODY).mirrorOf(right);
        }
    }

    private static float skirtT(float y) {
        return Aoi.clamp01((17.3f - y) / 6.049999f);
    }

    private static float flare(float t) {
        return t * (1.25f - 0.25f * t);
    }

    private static float skirtRadius(float x, float dz, float rho, float theta, float y) {
        float rx;
        float t = Aoi.skirtT(y);
        float e = Aoi.flare(t);
        float rz = 1.5f + 1.4f * e;
        float q = (float)Math.sqrt(rz * rz * x * x + (rx = 1.85f + 1.6f * e) * rx * dz * dz);
        float re = q < 1.0E-5f ? rx : rx * rz * rho / q;
        return re + (0.05f + 0.24f * t) * Aoi.pleat(theta * 18.0f / ((float)Math.PI * 2));
    }

    private static Shape skirtPanel(float centre, float half, float tuck) {
        float x0 = 0.0f;
        float x1 = 0.0f;
        float z0 = -0.05f;
        float z1 = -0.05f;
        for (int i = 0; i <= 16; ++i) {
            float a = centre - half - tuck + (half + tuck) * 2.0f * (float)i / 16.0f;
            for (float r : new float[]{1.4f, 4.05f}) {
                x0 = Math.min(x0, r * (float)Math.sin(a));
                x1 = Math.max(x1, r * (float)Math.sin(a));
                z0 = Math.min(z0, -0.05f + r * (float)Math.cos(a));
                z1 = Math.max(z1, -0.05f + r * (float)Math.cos(a));
            }
        }
        return Shapes.field((x, y, z) -> {
            float dz = z - -0.05f;
            float theta = (float)Math.atan2(x, dz);
            float rel = theta - centre;
            if ((double)rel > Math.PI) {
                rel -= (float)Math.PI * 2;
            }
            if ((double)rel < -Math.PI) {
                rel += (float)Math.PI * 2;
            }
            float out = Math.abs(rel) - half;
            float rho = (float)Math.sqrt(x * x + dz * dz);
            float r = Aoi.skirtRadius(x, dz, rho, theta, y) - 0.32f * Aoi.smooth(0.0f, 0.05f, out);
            float shell = Math.abs(rho - r) - 0.42f;
            float cap = Math.max(y - 17.3f, 11.25f - y);
            float a = Math.max(-1.2f, Math.min(1.2f, out - tuck));
            float edge = rho * a * (1.0f - a * a / 6.0f);
            return Math.max(shell, Math.max(cap, edge)) * 0.8f;
        }, x0 - 0.2f, 11.05f, z0 - 0.2f, x1 + 0.2f, 17.5f, z1 + 0.2f);
    }

    private static void arms(Rig rig, Canvas stripe) {
        float ex = -3.68f;
        float ey = 18.4f;
        float ez = -0.1f;
        float ax = ex + 3.3f;
        float ay = ey - 21.8f;
        float az = ez + 0.12f;
        float al = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        Shape sleeveBody = Shapes.blend(0.45f, Shapes.sphere(-3.22f, 22.2f, -0.15f, 1.1f), Shapes.cone(-3.3f, 21.8f, -0.12f, 1.07f, ex, ey, ez, 0.96f));
        Shape sleeve = sleeveBody.intersect(Shapes.plane(ex, ey, ez, ax /= al, ay /= al, az /= al).color(-14210250)).paint(Paint.field((x, y, z) -> Colors.mix(-1446155, -3945254, Aoi.smooth(-3.2f, -2.6f, x) * 0.7f)).flatLight(0.3f));
        float rx = ex - ax * 0.05f;
        float ry = ey - ay * 0.05f;
        float rz = ez - az * 0.05f;
        Shape roll = Aoi.ring(rx, ry, rz, ax, ay, az, 0.86f, 0.34f).color(-14210250);
        Paint skin = Paint.of(-9274).flatLight(1.0f).occlusion(0.25f);
        Shape forearm = Shapes.cone(ex, ey - 0.1f, ez + 0.05f, 0.55f, -3.92f, 14.66f, 0.15f, 0.39f).paint(skin).rotate(-13.0f, 0.0f, 0.0f, ex, ey, ez);
        float tilt = -1.5f;
        float c = (float)Math.cos(Math.toRadians(tilt));
        float s = (float)Math.sin(Math.toRadians(tilt));
        float tx = -3.3f + (rx + 3.3f) * c - (ry - 22.4f) * s;
        float ty = 22.4f + (rx + 3.3f) * s + (ry - 22.4f) * c;
        float bx = ax * c - ay * s;
        float by = ax * s + ay * c;
        rx = tx;
        ry = ty;
        ax = bx;
        ay = by;
        float[] grip = Aoi.bent(-4.15f, 12.75f, 0.45f, ex, ey, ez);
        float gx = -3.3f + (grip[0] + 3.3f) * c - (grip[1] - 22.4f) * s;
        float gy = 22.4f + (grip[0] + 3.3f) * s + (grip[1] - 22.4f) * c;
        Part right = rig.part(Bone.RIGHT_ARM).pivot(-3.3f, 22.4f, -0.1f).grip(gx, gy, grip[2]).shape(Shapes.union(sleeve, roll, forearm).rotate(0.0f, 0.0f, tilt, -3.3f, 22.4f, -0.1f)).outline(-12959918, 0.12f).occlusion(0.3f).swing(0.8f).joint(-3.78f, 18.41f, -0.1f, 0.55f);
        Part left = rig.part(Bone.LEFT_ARM).mirrorOf(right);
        right.decal(Decal.wrap(stripe, rx, ry, rz, -ax, -ay, -az, -1.0f, 0.0f, 0.3f, 250.0f, 0.4f).radius(0.95f, 1.6f));
        left.decal(Decal.wrap(stripe, -rx, ry, rz, ax, -ay, -az, 1.0f, 0.0f, 0.3f, 250.0f, 0.4f).radius(0.95f, 1.6f));
        left.decal(Decal.left(Aoi.sleevePatch(), 4.45f, 20.3f, -0.1f, 1.05f, 1.05f).depth(0.8f, 0.8f).maxAngle(65.0f));
        right.decal(Decal.right(Aoi.sleeveText(), -4.5f, 20.2f, -0.1f, 0.62f, 1.75f).depth(0.8f, 0.8f).maxAngle(65.0f));
        Part hand = rig.extra("hand_r", Bone.RIGHT_ARM).shape(Aoi.hand().rotate(-13.0f, 0.0f, 0.0f, ex, ey, ez).rotate(0.0f, 0.0f, tilt, -3.3f, 22.4f, -0.1f)).resolution(0.45f).detail(0.5f).outline(-6657438, 0.1f).flatLight(1.0f).occlusion(0.25f);
        rig.extra("hand_l", Bone.LEFT_ARM).mirrorOf(hand);
        Part wrist = rig.extra("wrist_r", Bone.RIGHT_ARM).shape(Shapes.ellipsoid(-3.97f, 14.12f, 0.16f, 0.31f, 0.5f, 0.37f).color(-9274).rotate(-13.0f, 0.0f, 0.0f, ex, ey, ez).rotate(0.0f, 0.0f, tilt, -3.3f, 22.4f, -0.1f)).resolution(0.6f).detail(1.4f).outline(-6657438, 0.1f).flatLight(1.0f).occlusion(0.25f);
        rig.extra("wrist_l", Bone.LEFT_ARM).mirrorOf(wrist);
    }

    private static float[] bent(float x, float y, float z, float ex, float ey, float ez) {
        double a = Math.toRadians(-13.0);
        float c = (float)Math.cos(a);
        float s = (float)Math.sin(a);
        float py = y - ey;
        float pz = z - ez;
        return new float[]{x, ey + py * c - pz * s, ez + py * s + pz * c};
    }

    private static Shape hand() {
        Shape palm = Shapes.ellipsoid(-4.0f, 13.2f, 0.14f, 0.3f, 0.68f, 0.54f);
        Strand thumb = Shapes.strand(0.2f, 0.15f, -3.86f, 13.35f, 0.6f, -3.72f, 12.9f, 0.92f, -3.62f, 12.5f, 0.98f);
        Shape fingers = Shapes.union(Shapes.strand(0.17f, 0.15f, -3.98f, 12.72f, 0.56f, -3.92f, 12.1f, 0.72f, -3.8f, 11.66f, 0.8f), Shapes.strand(0.17f, 0.15f, -4.0f, 12.74f, 0.24f, -3.88f, 12.12f, 0.28f, -3.66f, 11.8f, 0.3f), Shapes.strand(0.165f, 0.145f, -4.0f, 12.72f, -0.08f, -3.88f, 12.18f, -0.08f, -3.66f, 11.88f, -0.06f), Shapes.strand(0.15f, 0.13f, -3.99f, 12.66f, -0.36f, -3.88f, 12.3f, -0.36f, -3.7f, 12.06f, -0.33f));
        return Shapes.union(Shapes.blend(0.12f, palm, thumb, fingers).color(-9274), Shapes.cylinder(-3.95f, 14.22f, 0.18f, -3.93f, 14.9f, 0.15f, 0.5f, 0.1f).color(-13703425));
    }

    private static void legs(Rig rig) {
        Shape thigh = Shapes.cone(-1.24f, 14.1f, 0.0f, 1.3f, -1.1f, 8.3f, 0.1f, 0.84f);
        Shape knee = Shapes.sphere(-1.07f, 7.68f, 0.18f, 0.75f);
        Shape kneecap = Shapes.sphere(-1.06f, 7.8f, 0.5f, 0.52f);
        Shape calf = Shapes.cone(-1.07f, 7.3f, 0.06f, 0.78f, -1.12f, 2.6f, -0.1f, 0.5f);
        Shape muscle = Shapes.ellipsoid(-1.12f, 5.8f, -0.2f, 0.72f, 1.5f, 0.74f);
        Shape hip = Shapes.sphere(-1.1f, 14.4f, -0.05f, 1.32f);
        Paint legPaint = Paint.field((x, y, z) -> Colors.mix(Colors.mix(-14012606, -9274, Aoi.smooth(6.7f, 6.95f, y)), -15263457, Aoi.smooth(12.2f, 12.4f, y)));
        Shape leg = Shapes.union(Shapes.blend(0.5f, thigh, knee, calf, muscle).blend(0.3f, kneecap), hip).paint(legPaint);
        Shape sock = Shapes.union(Shapes.blend(0.5f, calf, muscle).inflate(0.1f).intersect(Shapes.below(6.95f)), Shapes.torus(-1.075f, 6.9f, 0.0f, 0.86f, 0.15f)).color(-14012606);
        Part right = rig.part(Bone.RIGHT_LEG).pivot(-1.2f, 14.3f, 0.0f).shape(leg).outline(-11914188, 0.12f).flatLight(0.7f).occlusion(0.3f).swing(0.55f, 30.0f).joint(-1.07f, 7.68f, 0.18f, 0.75f);
        rig.part(Bone.LEFT_LEG).mirrorOf(right);
        Part sockR = rig.extra("sock_r", Bone.RIGHT_LEG).shape(sock).resolution(0.7f).detail(0.45f).outline(-15460836, 0.12f);
        Part sockL = rig.extra("sock_l", Bone.LEFT_LEG).mirrorOf(sockR);
        Canvas socks = Aoi.socks();
        sockR.decal(Decal.wrap(socks, -1.1f, 6.42f, -0.05f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.3f).radius(0.3f, 1.4f));
        sockL.decal(Decal.wrap(socks, 1.1f, 6.42f, -0.05f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.3f).radius(0.3f, 1.4f));
        float fx = -1.14f;
        float turn = -7.0f;
        Part shoeR = rig.extra("shoe_r", Bone.RIGHT_LEG).shape(Aoi.shoe(fx).rotate(0.0f, turn, 0.0f, fx, 1.0f, 0.3f)).resolution(0.8f).detail(0.55f).outline(-12959664, 0.12f);
        Part shoeL = rig.extra("shoe_l", Bone.LEFT_LEG).mirrorOf(shoeR);
        Canvas laces = Aoi.laces();
        double a = Math.toRadians(turn);
        float c = (float)Math.cos(a);
        float s = (float)Math.sin(a);
        float lx = fx + 0.65f * s;
        float lz = 0.3f + 0.65f * c;
        shoeR.decal(Decal.project(laces, lx, 2.1f, lz, 0.87f * s, 0.5f, 0.87f * c, -0.5f * s, 0.87f, -0.5f * c, 0.8f, 1.28f).depth(0.7f, 0.7f).maxAngle(75.0f));
        shoeL.decal(Decal.project(laces, -lx, 2.1f, lz, -0.87f * s, 0.5f, 0.87f * c, 0.5f * s, 0.87f, -0.5f * c, 0.8f, 1.28f).depth(0.7f, 0.7f).maxAngle(75.0f));
    }

    private static Shape shoe(float fx) {
        Shape sole = Shapes.blend(0.35f, Shapes.box(fx, 0.42f, 0.4f, 0.88f, 0.42f, 1.45f, 0.36f), Shapes.ellipsoid(fx, 0.44f, 1.62f, 0.88f, 0.44f, 0.95f), Shapes.ellipsoid(fx, 0.44f, -0.85f, 0.84f, 0.44f, 0.7f));
        Shape toe = Shapes.ellipsoid(fx, 1.12f, 1.35f, 0.76f, 0.58f, 1.1f);
        Shape instep = Shapes.capsule(fx, 1.3f, 0.85f, fx, 2.05f, 0.1f, 0.7f);
        Shape heel = Shapes.ellipsoid(fx, 1.45f, -0.62f, 0.8f, 0.82f, 0.82f);
        Shape collar = Shapes.union(Shapes.cone(fx, 1.8f, -0.3f, 0.84f, fx, 3.5f, -0.17f, 0.74f).intersect(Shapes.below(3.2f)), Shapes.torus(fx, 3.18f, -0.19f, 0.66f, 0.15f));
        Shape tongue = Shapes.ellipsoid(fx, 3.05f, 0.42f, 0.46f, 0.72f, 0.3f);
        Shape upper = Shapes.blend(0.4f, toe, instep, heel, collar).blend(0.15f, tongue);
        Paint paint = Paint.field((x, y, z) -> {
            if (y < 0.9f) {
                return Colors.mix(-14012872, -920586, Aoi.smooth(0.05f, 0.24f, y));
            }
            return Colors.mix(-591621, -14210250, Aoi.smooth(2.8f, 3.05f, y));
        });
        return Shapes.union(sole, upper).paint(paint);
    }

    private static void gear(Rig rig, Shape jacket) {
        ArrayList<Shape> parts = new ArrayList<Shape>();
        Canvas glow = Aoi.phoneRing();
        Part part = rig.extra("gear", Bone.BODY);
        for (int s = -1; s <= 1; s += 2) {
            float ax = (float)s * 0.35f;
            float ay = 0.45f;
            float az = 0.82f;
            float l = (float)Math.sqrt(ax * ax + ay * ay + az * az);
            ax /= l;
            ay /= l;
            az /= l;
            float cx = (float)s * 1.55f;
            float cy = 23.5f;
            float cz = 1.12f;
            float px = ax;
            float py = ay;
            float pz = az;
            float qx = cx;
            float qy = cy;
            float qz = cz;
            Shape shell = Shapes.cylinder(cx - ax * 0.1f, cy - ay * 0.1f, cz - az * 0.1f, cx + ax * 0.16f, cy + ay * 0.16f, cz + az * 0.16f, 0.86f, 0.12f).paint(Paint.field((x, y, z) -> {
                float t = (x - qx) * px + (y - qy) * py + (z - qz) * pz;
                return Colors.mix(-14210250, -723206, Aoi.smooth(0.03f, 0.1f, t));
            }));
            Shape cushion = Shapes.cylinder(cx - ax * 0.36f, cy - ay * 0.36f, cz - az * 0.36f, cx - ax * 0.08f, cy - ay * 0.08f, cz - az * 0.08f, 0.78f, 0.1f).color(-14210250);
            Shape arm = Shapes.capsule(cx - ax * 0.1f - (float)s * 0.1f, cy + 0.8f, cz - az * 0.1f - 0.2f, (float)s * 1.3f, 24.4f, 0.5f, 0.2f).color(-14210250);
            parts.add(Shapes.union(shell, cushion, arm));
            part.decal(Decal.project(glow, cx + ax * 0.17f, cy + ay * 0.17f, cz + az * 0.17f, ax, ay, az, 0.0f, 1.0f, 0.0f, 1.3f, 1.3f).depth(0.4f, 0.4f).maxAngle(70.0f).glow());
        }
        parts.add(Shapes.tube(1.3f, 24.4f, 0.5f, 0.24f, 1.22f, 24.85f, -0.35f, 0.26f, 0.72f, 25.02f, -1.2f, 0.26f, 0.0f, 25.08f, -1.42f, 0.26f, -0.72f, 25.02f, -1.2f, 0.26f, -1.22f, 24.85f, -0.35f, 0.26f, -1.3f, 24.4f, 0.5f, 0.24f).flattenFrom(1.8f, 0.0f, 24.9f, -0.2f).color(-14210250));
        parts.add(Shapes.union(Aoi.piping(jacket, 1.0f, 23.3f, 17.5f), Aoi.piping(jacket, -1.0f, 23.3f, 17.5f)).color(-13703425));
        part.shape(Shapes.union(parts.toArray(new Shape[0]))).resolution(0.7f).detail(0.7f).outline(-15065820, 0.1f).flatLight(0.7f).occlusion(0.15f);
        float[] path = new float[]{1.75f, 22.65f, 1.85f, 21.8f, 1.92f, 20.9f, 1.95f, 20.15f};
        float[] cable = new float[12];
        for (int i = 0; i < 4; ++i) {
            cable[i * 3] = path[i * 2];
            cable[i * 3 + 1] = path[i * 2 + 1];
            cable[i * 3 + 2] = i == 0 ? 1.3f : Aoi.frontZ(jacket, path[i * 2], path[i * 2 + 1], 6.0f) + 0.16f;
        }
        float jz = cable[11];
        Shape plug = Shapes.union(Shapes.strand(0.13f, 0.13f, cable).color(-7563613), Shapes.capsule(1.95f, 20.2f, jz, 1.96f, 19.65f, jz + 0.02f, 0.19f).color(-12959662), Shapes.capsule(1.96f, 19.6f, jz + 0.02f, 1.96f, 19.3f, jz + 0.03f, 0.12f).color(-3550757));
        rig.extra("cable", Bone.BODY).pivot(1.75f, 22.65f, 1.3f).shape(plug).resolution(0.5f).detail(0.25f).noOutline().motion(Motion.cloth().idle(1.0f, 0.0f, 1.5f, 0.35f).walk(4.0f, 0.0f, 5.0f, 2.0f).drag(-2.0f).hang(0.4f).spring(45.0f, 5.0f).bend(1.1f));
    }

    private static float fu(float x) {
        return (x + 2.25f) * 80.0f;
    }

    private static float fv(float y) {
        return (28.6f - y) * 80.0f;
    }

    private static Decal face(List<float[]> locks) {
        int side;
        Canvas c = new Canvas(Math.round(360.0f), Math.round(201.60004f));
        for (float[] k : locks) {
            Shape2D shadow = null;
            int i = 0;
            while (i + 7 < k.length) {
                if (!(k[i + 1] > 29.2f) || !(k[i + 5] > 29.2f)) {
                    Shape2D seg = Shape2D.taper(Aoi.fu(k[i]), Aoi.fv(k[i + 1] - 0.22f), k[i + 3] * 80.0f * 1.7f, Aoi.fu(k[i + 4]), Aoi.fv(k[i + 5] - 0.22f), Math.max(2.0f, k[i + 7] * 80.0f * 1.5f));
                    shadow = shadow == null ? seg : shadow.union(seg);
                }
                i += 4;
            }
            if (shadow == null) continue;
            c.fill(shadow, 2028380826, 3.0f);
        }
        for (side = -1; side <= 1; side += 2) {
            Aoi.eye(c, side);
        }
        c.fill(Shape2D.taper(Aoi.fu(0.06f), Aoi.fv(26.8f), 4.0f, Aoi.fu(-0.02f), Aoi.fv(26.64f), 2.0f), -1714123650, 0.0f);
        c.fill(Shape2D.curve(Aoi.fu(-0.22f), Aoi.fv(26.2f), Aoi.fu(0.0f), Aoi.fv(26.1f), Aoi.fu(0.2f), Aoi.fv(26.23f), 3.4f, 2.2f), -7455931, 0.0f);
        for (side = -1; side <= 1; side += 2) {
            c.fill(Shape2D.ellipse(Aoi.fu((float)side * 1.25f), Aoi.fv(26.42f), 32.0f, 11.0f), 1442807955, 10.0f);
        }
        float cy = 27.34f;
        return Decal.front(c, 0.0f, Aoi.hy(cy), 3.132f, 5.22f, 2.9232004f).depth(3.0159998f, 0.8f).maxAngle(72.0f).flatLight(1.0f).occlusion(0.3f);
    }

    private static void eye(Canvas c, int side) {
        float ex = Aoi.fu((float)side * 0.98f);
        float ey = Aoi.fv(27.2f);
        float ix = ex - (float)side * 2.0f;
        float iy = ey + 4.0f;
        float inX = ex - (float)side * 46.0f;
        float outX = ex + (float)side * 47.0f;
        float inY = ey - 40.0f;
        float outY = ey - 50.0f;
        float midX = ex - (float)side * 2.0f;
        float midY = ey - 49.0f;
        Shape2D lid = Shape2D.polygon(inX - (float)side * 30.0f, inY + 2.0f, inX, inY, midX, midY, outX, outY, outX + (float)side * 30.0f, outY, outX + (float)side * 30.0f, ey + 90.0f, inX - (float)side * 30.0f, ey + 90.0f);
        Shape2D white = Shape2D.ellipse(ex, ey - 2.0f, 47.0f, 53.0f).intersect(lid);
        c.fill(white, Paint2D.linear(ex, ey - 50.0f, ex, ey + 50.0f, -2498062, -1, -1));
        Shape2D iris = Shape2D.ellipse(ix, iy, 32.0f, 43.0f);
        Shape2D inIris = iris.intersect(white);
        c.fill(inIris, Paint2D.linear(ix, iy - 43.0f, ix, iy + 43.0f, -16050107, -15449954, -14711060, -10687489));
        c.fill(Shape2D.ellipse(ix, iy + 25.0f, 22.0f, 14.0f).intersect(inIris), -1871645953, 6.0f);
        c.fill(Shape2D.ellipse(ix, iy + 3.0f, 12.0f, 19.0f), -16315098, 0.0f);
        c.fill(iris.outline(4.5f).intersect(white), -536210893, 0.0f);
        c.fill(Shape2D.ellipse(ex, ey - 44.0f, 62.0f, 20.0f).intersect(white), 1714438256, 8.0f);
        c.fill(Shape2D.box(ix - 12.0f, iy - 15.0f, 6.5f, 10.5f, 3.5f).rotate(-14.0f, ix - 12.0f, iy - 15.0f), -1, 0.0f);
        c.fill(Shape2D.box(ix - 2.0f, iy - 20.0f, 2.2f, 5.0f, 1.5f).rotate(-14.0f, ix - 2.0f, iy - 20.0f), -855638017, 0.0f);
        c.fill(Shape2D.circle(ix + 13.0f, iy + 21.0f, 4.2f), -570425345, 0.0f);
        int ink = -15724002;
        c.fill(Shape2D.path(5.0f, 9.0f, inX, inY + 1.0f, midX, midY - 1.0f, outX, outY), ink, 0.0f);
        c.fill(Shape2D.taper(outX - (float)side * 6.0f, outY + 1.0f, 7.5f, outX + (float)side * 12.0f, outY - 7.0f, 1.5f), ink, 0.0f);
        c.fill(Shape2D.curve(ex + (float)side * 6.0f, ey + 51.0f, ex + (float)side * 32.0f, ey + 46.0f, outX - (float)side * 3.0f, ey + 22.0f, 1.2f, 3.0f), -1072030672, 0.0f);
        c.fill(Shape2D.curve(ex - (float)side * 40.0f, ey - 72.0f, ex + (float)side * 2.0f, ey - 80.0f, ex + (float)side * 42.0f, ey - 72.0f, 3.6f, 2.0f), -14011280, 0.0f);
    }

    private static Canvas stripe() {
        Canvas c = new Canvas(64, 10);
        c.fill(Shape2D.box(32.0f, 3.3f, 40.0f, 2.8f, 0.0f), -13703425);
        c.fill(Shape2D.box(32.0f, 8.2f, 40.0f, 0.8f, 0.0f), -1);
        return c;
    }

    private static Canvas band() {
        Canvas c = new Canvas(64, 8);
        c.fill(Shape2D.box(32.0f, 4.0f, 40.0f, 3.0f, 0.0f), -13703425);
        return c;
    }

    private static Canvas socks() {
        Canvas c = new Canvas(64, 12);
        c.fill(Shape2D.box(32.0f, 2.5f, 40.0f, 2.0f, 0.0f), -13703425);
        c.fill(Shape2D.box(32.0f, 9.5f, 40.0f, 2.0f, 0.0f), -13703425);
        return c;
    }

    private static Canvas phoneRing() {
        Canvas c = new Canvas(96, 96);
        c.fill(Shape2D.circle(48.0f, 48.0f, 33.0f).outline(5.0f), -13703425);
        return c;
    }

    private static Decal topGraphic() {
        Canvas c = new Canvas(152, 120);
        c.fill(Shape2D.circle(76.0f, 60.0f, 42.0f).outline(10.0f), -13703425);
        float[] h = new float[]{16.0f, 30.0f, 50.0f, 72.0f, 94.0f, 72.0f, 50.0f, 30.0f, 16.0f};
        for (int i = 0; i < h.length; ++i) {
            c.fill(Shape2D.box(76.0f + (float)(i - 4) * 15.0f, 60.0f, 4.6f, h[i] * 0.5f, 3.0f), -14932394);
        }
        return Decal.front(c, 0.0f, 20.3f, 1.9f, 1.7f, 1.34f).depth(0.75f, 0.45f).maxAngle(62.0f);
    }

    private static Canvas backStripe() {
        Canvas c = new Canvas(12, 80);
        c.fill(Shape2D.box(6.0f, 40.0f, 3.1f, 39.0f, 0.9f), -13703425);
        c.fill(Shape2D.box(0.9f, 40.0f, 0.7f, 39.0f, 0.35f), -1512206);
        c.fill(Shape2D.box(11.1f, 40.0f, 0.7f, 39.0f, 0.35f), -1512206);
        return c;
    }

    private static Canvas buckle() {
        Canvas c = new Canvas(64, 48);
        c.fill(Shape2D.box(32.0f, 24.0f, 31.0f, 23.0f, 7.0f), Paint2D.linear(0.0f, 2.0f, 0.0f, 46.0f, -1643535, -7563613));
        c.fill(Shape2D.box(32.0f, 24.0f, 21.0f, 12.0f, 4.0f), -14276045);
        c.fill(Shape2D.box(32.0f, 24.0f, 14.0f, 3.5f, 1.5f), -13703425);
        c.fill(Shape2D.box(8.5f, 24.0f, 2.0f, 9.0f, 1.0f), -14276045);
        c.fill(Shape2D.box(55.5f, 24.0f, 2.0f, 9.0f, 1.0f), -14276045);
        return c;
    }

    private static Canvas pocket() {
        Canvas c = new Canvas(64, 24);
        c.fill(Shape2D.box(32.0f, 12.0f, 28.0f, 3.4f, 3.0f), -15460837);
        c.fill(Shape2D.box(32.0f, 12.0f, 25.0f, 0.8f, 0.4f), -13703425);
        c.fill(Shape2D.box(54.0f, 15.5f, 2.2f, 5.0f, 1.2f), -3550757);
        return c;
    }

    private static Canvas sleeveText() {
        float k = 0.43f;
        Canvas c = new Canvas(41, 117);
        Shape2D a = Shape2D.union(Shape2D.line(26.0f, 86.0f, 48.0f, 14.0f, 13.0f), Shape2D.line(48.0f, 14.0f, 70.0f, 86.0f, 13.0f), Shape2D.line(36.0f, 62.0f, 60.0f, 62.0f, 10.0f));
        Shape2D o = Shape2D.ellipse(48.0f, 136.0f, 22.0f, 34.0f).outline(13.0f);
        Shape2D i = Shape2D.union(Shape2D.line(48.0f, 190.0f, 48.0f, 258.0f, 13.0f), Shape2D.line(32.0f, 190.0f, 64.0f, 190.0f, 9.0f), Shape2D.line(32.0f, 258.0f, 64.0f, 258.0f, 9.0f));
        Shape2D text = Shape2D.union(a, o, i);
        Aoi.ink(c, k, text.move(3.0f, 3.0f), -13703425, 0.0f);
        Aoi.ink(c, k, text, -14932394, 0.0f);
        return c;
    }

    private static Canvas sleevePatch() {
        float k = 0.625f;
        Canvas c = new Canvas(80, 80);
        Aoi.ink(c, k, Shape2D.circle(64.0f, 64.0f, 60.0f), -14210250, 0.0f);
        Aoi.ink(c, k, Shape2D.circle(64.0f, 64.0f, 52.0f).outline(6.0f), -13703425, 0.0f);
        Aoi.ink(c, k, Shape2D.arc(64.0f, 70.0f, 30.0f, 180.0f, 360.0f, 8.0f), -591621, 0.0f);
        Aoi.ink(c, k, Shape2D.box(36.0f, 78.0f, 8.0f, 14.0f, 5.0f), -591621, 0.0f);
        Aoi.ink(c, k, Shape2D.box(92.0f, 78.0f, 8.0f, 14.0f, 5.0f), -591621, 0.0f);
        return c;
    }

    private static Canvas laces() {
        float k = 0.5f;
        Canvas c = new Canvas(48, 80);
        for (int i = 0; i < 4; ++i) {
            float y = 30.0f + (float)i * 32.0f;
            Aoi.ink(c, k, Shape2D.line(20.0f, y, 76.0f, y + 16.0f, 9.0f), -13703425, 0.0f);
            Aoi.ink(c, k, Shape2D.line(76.0f, y, 20.0f, y + 16.0f, 9.0f), -13703425, 0.0f);
            Aoi.ink(c, k, Shape2D.circle(14.0f, y, 5.0f), -3550757, 0.0f);
            Aoi.ink(c, k, Shape2D.circle(82.0f, y, 5.0f), -3550757, 0.0f);
        }
        return c;
    }

    private static void ink(Canvas c, float k, Shape2D s, int argb, float feather) {
        c.fill(s.scale(k, 0.0f, 0.0f), argb, feather * k);
    }

    private static float frontZ(Shape s, float x, float y, float start) {
        float d;
        float z = start;
        for (int i = 0; i < 400 && z > -8.0f; z -= Math.max(0.005f, d * 0.7f), ++i) {
            d = s.distance(x, y, z);
            if (!(d < 0.003f)) continue;
            return z;
        }
        return z;
    }

    private static Shape ring(float cx, float cy, float cz, float ax, float ay, float az, float major, float minor) {
        float l = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        float nx = ax / l;
        float ny = ay / l;
        float nz = az / l;
        Shape t = Shapes.torus(cx, cy, cz, major, minor);
        if (nx * nx + nz * nz < 1.0E-8f) {
            return t;
        }
        float angle = (float)Math.toDegrees(Math.acos(Math.max(-1.0f, Math.min(1.0f, ny))));
        return t.rotateAxis(nz, 0.0f, -nx, angle, cx, cy, cz);
    }

    private static Shape oval(float zc, float y0, float y1, float rx0, float rz0, float rx1, float rz1) {
        float mx = Math.max(rx0, rx1);
        float mz = Math.max(rz0, rz1);
        return Shapes.field((x, y, z) -> {
            float t = Aoi.clamp01((y - y0) / (y1 - y0));
            float d = Aoi.ellipse(x, z - zc, rx0 + (rx1 - rx0) * t, rz0 + (rz1 - rz0) * t);
            return Math.max(d * 0.95f, Math.max(y0 - y, y - y1));
        }, -mx, y0, zc - mz, mx, y1, zc + mz);
    }

    private static Shape ovalRing(float zc, float yc, float halfHeight, float rx, float rz, float thick) {
        float round = Math.min(thick, halfHeight) * 0.5f;
        return Shapes.field((x, y, z) -> {
            float a = Math.abs(Aoi.ellipse(x, z - zc, rx, rz)) - thick + round;
            float b = Math.abs(y - yc) - halfHeight + round;
            float oa = Math.max(a, 0.0f);
            float ob = Math.max(b, 0.0f);
            return ((float)Math.sqrt(oa * oa + ob * ob) + Math.min(Math.max(a, b), 0.0f) - round) * 0.95f;
        }, -rx - thick, yc - halfHeight, zc - rz - thick, rx + thick, yc + halfHeight, zc + rz + thick);
    }

    private static float ellipse(float x, float z, float rx, float rz) {
        float k0 = (float)Math.sqrt(x * x / (rx * rx) + z * z / (rz * rz));
        float k1 = (float)Math.sqrt(x * x / (rx * rx * rx * rx) + z * z / (rz * rz * rz * rz));
        if (k1 < 1.0E-6f) {
            return -Math.min(rx, rz);
        }
        return k0 * (k0 - 1.0f) / k1;
    }

    private static float pleat(float v) {
        float w = Aoi.tri(v);
        return w * (1.5f - 0.5f * w * w);
    }

    private static float tri(float v) {
        float u = v - (float)Math.floor(v);
        return 4.0f * Math.min(u, 1.0f - u) - 1.0f;
    }

    private static float clamp01(float v) {
        return v < 0.0f ? 0.0f : Math.min(v, 1.0f);
    }

    private static float smooth(float e0, float e1, float x) {
        float t = Aoi.clamp01((x - e0) / (e1 - e0));
        return t * t * (3.0f - 2.0f * t);
    }
}

