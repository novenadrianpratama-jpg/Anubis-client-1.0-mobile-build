/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.designs;

import java.util.ArrayList;
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
public final class Sakura
extends CharacterDesign {
    private static final int SKIN = -9016;
    private static final int SKIN_SHADE = -1331558;
    private static final int SKIN_LINE = -5740452;
    private static final int HAIR_ROOT = -2592100;
    private static final int HAIR = -677434;
    private static final int HAIR_TIP = -12321;
    private static final int HAIR_SHADE = -3908470;
    private static final int HAIR_LINE = -6864801;
    private static final int SHINE = -855643406;
    private static final int WHITE = -263689;
    private static final int WHITE_SHADE = -2305053;
    private static final int SLEEVE_IN = -2502430;
    private static final int CLOTH_LINE = -7568744;
    private static final int RED = -2674372;
    private static final int RED_DARK = -5955793;
    private static final int RED_IN = -10087904;
    private static final int RED_LINE = -8514270;
    private static final int CORD = -1816774;
    private static final int GOLD = -1459895;
    private static final int BEAD = -2608577;
    private static final int STRAW = -1320012;
    private static final int SOLE = -10600144;
    private static final int PETAL = -8984;
    private static final int PETAL_CORE = -2078596;
    private static final float HAKAMA_TOP = 20.0f;
    private static final float HAKAMA_HEM = 2.5f;
    private static final float HS = 1.12f;
    private static final float DROP = 0.4f;
    private static final float HX = 0.0f;
    private static final float HY = 28.9f;
    private static final float HZ = -0.3f;
    private static final float PPU = 56.88889f;
    private static final float FACE_TOP = 29.95f;
    private static final float[] PLEATS = new float[]{0.21f, 0.7f, 2.76f};
    private static final float[][] LOCKS = new float[][]{{-0.08f, 28.38f, -2.0f, 0.95f, 0.12f, 0.34f}, {-0.98f, 28.82f, -25.0f, 0.95f, 0.28f, 0.3f}, {-1.92f, 28.5f, -47.0f, 0.92f, 0.26f, 0.3f}, {0.92f, 28.9f, 24.0f, 0.95f, 0.28f, 0.3f}, {1.9f, 28.62f, 46.0f, 0.92f, 0.25f, 0.3f}, {-0.55f, 28.58f, -13.0f, 0.45f, 0.16f, 0.44f}, {0.46f, 28.7f, 11.0f, 0.45f, 0.18f, 0.44f}, {-1.46f, 28.72f, -36.0f, 0.42f, 0.2f, 0.42f}, {1.42f, 28.8f, 35.0f, 0.42f, 0.2f, 0.42f}};
    private static final Shape2D SLEEVE_SIDE = Shape2D.polygon(0.85f, 23.95f, 0.62f, 19.0f, 0.12f, 15.7f, -0.2f, 14.7f, -1.2f, 14.25f, -2.25f, 14.0f, -2.9f, 14.2f, -3.2f, 14.8f, -3.25f, 16.0f, -2.2f, 22.4f, -1.0f, 24.2f).inflate(0.3f);

    public Sakura() {
        super("sakura", "Sakura");
    }

    @Override
    protected void define(Rig rig) {
        rig.scale(1.0f).outline(-11914176, 0.12f).occlusion(0.3f).flatLight(0.55f);
        float[][] locks = Sakura.lockPaths();
        Paint hair = Sakura.hairPaint();
        Sakura.head(rig, locks);
        Sakura.hair(rig, hair);
        Sakura.fringe(rig, locks);
        Sakura.kanzashi(rig);
        Sakura.body(rig);
        Sakura.hakama(rig);
        Sakura.arms(rig);
        Sakura.legs(rig);
    }

    private static Shape up(Shape s) {
        return s.scale(1.12f, 0.0f, 24.6f, -0.2f).move(0.0f, -0.4f, 0.0f);
    }

    private static float tx(float x) {
        return x * 1.12f;
    }

    private static float ty(float y) {
        return 24.6f + (y - 24.6f) * 1.12f - 0.4f;
    }

    private static float tz(float z) {
        return -0.2f + (z + 0.2f) * 1.12f;
    }

    private static Shape headBase() {
        return Shapes.blend(0.85f, Shapes.ellipsoid(0.0f, 28.72f, -0.15f, 2.28f, 2.4f, 2.35f), Shapes.ellipsoid(0.0f, 27.75f, 0.55f, 2.05f, 1.3f, 1.75f), Shapes.cone(0.0f, 27.3f, 0.62f, 1.35f, 0.0f, 26.05f, 1.2f, 0.3f));
    }

    private static void head(Rig rig, float[][] locks) {
        Shape neck = Shapes.capsule(0.0f, 23.3f, -0.45f, 0.0f, 25.9f, -0.3f, 0.6f);
        float chinY = Sakura.ty(25.82f);
        float chinZ = Sakura.tz(1.2f);
        Paint skin = Paint.field((x, y, z) -> {
            float jaw = chinY + Math.max(0.0f, chinZ - z) * 0.62f;
            float t = Sakura.smooth(jaw + 0.05f, jaw - 0.2f, y) * (0.6f + 0.4f * Sakura.smooth(23.4f, 24.8f, y));
            return Colors.mix(-9016, -1331558, t);
        });
        Shape skull = Sakura.up(Sakura.headBase().intersect(Shapes.below(30.2f)));
        rig.part(Bone.HEAD).pivot(0.0f, 24.6f, -0.2f).shape(Shapes.blend(0.45f, skull, neck).paint(skin)).detail(2.0f).flatLight(1.0f).occlusion(0.22f).outline(-5740452, 0.1f).decal(Sakura.face(locks));
    }

    private static float[][] lockPaths() {
        Shape head = Sakura.headBase();
        float[][] out = new float[LOCKS.length][];
        for (int i = 0; i < LOCKS.length; ++i) {
            float[] l = LOCKS[i];
            float[] a = Sakura.onHead(36.0f, l[2], -0.15f);
            float[] b = Sakura.onHead(58.0f, l[2], 0.05f);
            float x2 = b[0] + (l[0] - b[0]) * 0.75f;
            float y2 = l[1] + 0.68f;
            float x3 = l[0] - Math.signum(l[0]) * l[4];
            out[i] = new float[]{a[0], a[1], a[2], b[0], b[1], b[2], x2, y2, Sakura.frontZ(head, x2, y2) + l[5], x3, l[1], Sakura.frontZ(head, x3, l[1]) + 0.22f, l[3]};
        }
        return out;
    }

    private static float frontZ(Shape head, float x, float y) {
        float z;
        for (z = 3.5f; z > -1.0f && head.distance(x, y, z) > 0.0f; z -= 0.02f) {
        }
        return z;
    }

    private static void fringe(Rig rig, float[][] locks) {
        Shape[] parts = new Shape[locks.length];
        Shape[] blades = new Shape[locks.length];
        for (int i = 0; i < locks.length; ++i) {
            float[] p = locks[i];
            float w = p[12];
            Strand s = Shapes.tube(p[0], p[1], p[2], 0.9f * w, p[3], p[4], p[5], 1.15f * w, p[6], p[7], p[8], 0.95f * w, p[9], p[10], p[11], 0.12f).flattenFrom(3.0f, 0.0f, 28.7f, -0.15f);
            blades[i] = Shapes.extrude(Shape2D.polygon(Sakura.blade(p)), 5.0f, 0.0f);
            parts[i] = Shapes.intersect(s, blades[i]);
        }
        Shape bangs = Shapes.intersect(Shapes.blend(0.15f, parts), Shapes.union(blades));
        Shape skin = Sakura.headBase();
        Paint paint = Paint.field((x, y, z) -> {
            int c = Colors.mix(Colors.mix(-2592100, -677434, 0.45f), -677434, Sakura.smooth(30.9f, 29.7f, y));
            c = Colors.mix(c, -12321, 0.5f * Sakura.smooth(29.5f, 28.5f, y));
            return Colors.mix(c, -3908470, 0.6f * (1.0f - Sakura.smooth(0.12f, 0.42f, skin.distance(x, y, z))));
        });
        rig.extra("fringe", Bone.HEAD).shape(Sakura.up(bangs.paint(paint))).resolution(0.45f).detail(1.05f).flatLight(0.7f).occlusion(0.1f).outline(-6864801, 0.09f).decal(Sakura.shine());
    }

    private static float[] blade(float[] p) {
        float w = p[12];
        boolean right = p[9] < 0.0f;
        float x0 = p[3];
        float y0 = 31.6f;
        float cx = p[6];
        float cy = p[7];
        float x1 = p[9];
        float y1 = p[10];
        int n = 12;
        float[] xy = new float[(2 * n + 1) * 2];
        for (int i = 0; i <= n; ++i) {
            float t = (float)i / (float)n;
            float u = 1.0f - t;
            float bx = u * u * x0 + 2.0f * u * t * cx + t * t * x1;
            float by = u * u * y0 + 2.0f * u * t * cy + t * t * y1;
            float tx = u * (cx - x0) + t * (x1 - cx);
            float ty = u * (cy - y0) + t * (y1 - cy);
            float tl = (float)Math.sqrt(tx * tx + ty * ty);
            float nx = -ty / tl;
            float ny = tx / tl;
            if (nx > 0.0f != right) {
                nx = -nx;
                ny = -ny;
            }
            float wi = w * (1.0f - Sakura.smooth(0.6f, 1.0f, t));
            float wo = w * (1.0f - (float)Math.pow(Sakura.smooth(0.3f, 1.0f, t), 0.75));
            xy[i * 2] = bx + nx * wi;
            xy[i * 2 + 1] = by + ny * wi;
            if (i >= n) continue;
            xy[(2 * n - i) * 2] = bx - nx * wo;
            xy[(2 * n - i) * 2 + 1] = by - ny * wo;
        }
        return xy;
    }

    private static Decal shine() {
        float[][] dashes;
        Canvas c = new Canvas(280, 22);
        for (float[] d : dashes = new float[][]{{-68.0f, -54.0f}, {-45.0f, -25.0f}, {-13.0f, 9.0f}, {20.0f, 38.0f}, {48.0f, 64.0f}}) {
            float a0 = d[0];
            float a1 = d[1];
            float am = (a0 + a1) * 0.5f;
            float w = 6.5f - Math.abs(am) * 0.025f;
            c.fill(Shape2D.curve(140.0f + a0 * 2.0f, Sakura.ringY(a0), 140.0f + a0 + am, Sakura.ringY((a0 + am) * 0.5f), 140.0f + am * 2.0f, Sakura.ringY(am), 1.0f, w), -855643406);
            c.fill(Shape2D.curve(140.0f + am * 2.0f, Sakura.ringY(am), 140.0f + am + a1, Sakura.ringY((am + a1) * 0.5f), 140.0f + a1 * 2.0f, Sakura.ringY(a1), w, 1.0f), -855643406);
        }
        return Decal.wrap(c, 0.0f, Sakura.ty(30.16f), Sakura.tz(-0.3f), 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 140.0f, 0.6272f).radius(1.2f, 3.8f).offset(0.03f);
    }

    private static float ringY(float deg) {
        float y = 30.4f - 0.36f * (1.0f - (float)Math.cos(Math.toRadians(deg)));
        return (30.44f - y) * 40.0f;
    }

    private static void hair(Rig rig, Paint paint) {
        ArrayList<Shape> cap = new ArrayList<Shape>();
        cap.add(Shapes.ellipsoid(0.0f, 28.9f, -0.3f, 2.58f, 2.62f, 2.7f));
        for (float phi : new float[]{64.0f, 88.0f, 272.0f, 296.0f}) {
            cap.add(Sakura.capLock(phi, 5.0f, 98.0f, 0.8f, 0.62f));
        }
        Shape faceCut = Shapes.box(0.0f, 26.3f, 2.3f, 2.95f, 3.3f, 2.8f, 0.9f);
        Shape sideFill = Shapes.blend(0.3f, Shapes.strand(0.62f, 0.44f, -2.3f, 29.6f, -0.05f, -2.48f, 28.1f, -0.12f, -2.45f, 26.5f, -0.22f).flatten(1.6f, 1.0f, 0.0f, 0.0f), Shapes.strand(0.68f, 0.44f, -2.25f, 29.5f, -0.95f, -2.46f, 27.9f, -1.05f, -2.45f, 26.3f, -1.12f).flatten(1.6f, 1.0f, 0.0f, 0.0f)).mirrorX();
        Shape capShape = Shapes.blend(0.25f, Shapes.subtract(Shapes.blend(0.3f, cap.toArray(new Shape[0])), faceCut).paint(paint), sideFill.paint(paint.occlusion(0.35f)));
        rig.extra("hair", Bone.HEAD).shape(Sakura.up(capShape)).detail(1.1f).flatLight(0.55f).occlusion(0.22f).outline(-6864801, 0.09f).decal(Sakura.shine());
        Strand front = Shapes.tube(-1.95f, 30.4f, 0.92f, 0.55f, -2.3f, 29.3f, 1.12f, 0.6f, -2.42f, 27.8f, 1.0f, 0.55f, -2.41f, 25.9f, 0.72f, 0.3f, -2.3f, 25.1f, 0.74f, 0.36f, -2.12f, 24.7f, 0.78f, 0.32f).flatten(1.8f, 1.0f, 0.0f, 0.3f);
        Strand back = Shapes.tube(-2.2f, 30.2f, 0.1f, 0.58f, -2.52f, 28.9f, 0.24f, 0.62f, -2.58f, 27.3f, 0.32f, 0.55f, -2.41f, 25.9f, 0.56f, 0.3f, -2.28f, 25.0f, 0.54f, 0.36f, -2.2f, 24.35f, 0.56f, 0.32f).flatten(1.8f, 1.0f, 0.0f, 0.15f);
        Shape tie = Shapes.cylinder(-2.41f, 25.38f, 0.64f, -2.41f, 25.94f, 0.64f, 0.5f, 0.12f).color(-263689);
        Shape lock = Shapes.union(Shapes.blend(0.25f, front, back).paint(paint), tie);
        Motion sway = new Motion().idle(1.5f, 0.0f, 0.6f, 0.35f).walk(3.0f, 0.0f, 1.0f, 2.0f).drag(8.0f).hang(0.6f).spring(60.0f, 8.0f).bend(1.4f);
        Part lockR = rig.extra("sidelock_r", Bone.HEAD).pivot(Sakura.tx(-2.3f), Sakura.ty(30.0f), Sakura.tz(0.6f)).shape(Sakura.up(lock)).motion(sway).resolution(0.7f).detail(0.6f).flatLight(0.5f).occlusion(0.35f).outline(-6864801, 0.09f).decal(Sakura.tieCord());
        rig.extra("sidelock_l", Bone.HEAD).mirrorOf(lockR);
        Sakura.backHair(rig, paint);
    }

    private static Decal tieCord() {
        Canvas c = new Canvas(40, 20);
        c.fill(Shape2D.box(20.0f, 10.0f, 20.0f, 2.8f, 0.0f), -1816774);
        c.fill(Shape2D.ellipse(20.0f, 10.0f, 4.0f, 5.0f), -1816774);
        return Decal.wrap(c, Sakura.tx(-2.41f), Sakura.ty(25.67f), Sakura.tz(0.63f), 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, 0.45f, 220.0f, 0.56f).radius(0.2f, 0.9f);
    }

    private static void backHair(Rig rig, Paint paint) {
        ArrayList<Strand> crown = new ArrayList<Strand>();
        ArrayList<Strand> fall = new ArrayList<Strand>();
        for (int k = 0; k < 7; ++k) {
            float s = ((float)k - 3.0f) / 3.0f;
            float phi = 180.0f - s * 72.0f;
            float lift = k % 2 == 0 ? 0.1f : 0.0f;
            float[] a = Sakura.onHead(26.0f, phi, -0.02f);
            float[] b = Sakura.onHead(60.0f, phi, 0.22f + lift);
            float[] c = Sakura.onHead(88.0f, phi, 0.34f + lift);
            float[] d = Sakura.onHead(114.0f, phi, 0.2f + lift);
            crown.add(Shapes.strand(0.78f, 0.72f, a[0], a[1], a[2], b[0], b[1], b[2], c[0], c[1], c[2], d[0], d[1], d[2]).flattenFrom(2.4f, 0.0f, 28.9f, -0.3f));
            float[] e = Sakura.onHead(104.0f, phi, 0.32f);
            fall.add(Shapes.strand(0.8f, 0.64f, e[0], e[1], e[2], s * 2.85f, 25.0f, -2.52f - 0.3f * (1.0f - s * s), s * 1.02f, 21.9f, -2.8f).flatten(2.0f, 0.0f, 0.0f, 1.0f));
        }
        ArrayList<Strand> tail = new ArrayList<Strand>();
        float[] tips = new float[]{14.1f, 13.72f, 13.58f, 13.78f, 14.16f};
        for (int m = 0; m < 5; ++m) {
            float s = ((float)m - 2.0f) / 2.0f;
            tail.add(Shapes.strand(0.8f, 0.14f, s * 0.72f, 22.3f, -2.85f, s * 1.02f, 18.9f, -3.08f, s * 0.84f, 16.0f, -3.25f, s * 0.2f, tips[m], -3.32f).flatten(1.6f, 0.0f, 0.0f, 1.0f));
        }
        Shape backShape = Shapes.union(Shapes.blend(0.1f, crown.toArray(new Shape[0])), Shapes.blend(0.2f, fall.toArray(new Shape[0])), Shapes.blend(0.15f, tail.toArray(new Shape[0]))).paint(paint);
        Shape wrap = Shapes.box(0.0f, 21.9f, -2.82f, 1.42f, 0.45f, 0.62f, 0.32f).color(-263689);
        Shape cord = Shapes.box(0.0f, 21.9f, -2.82f, 1.5f, 0.2f, 0.7f, 0.17f).color(-1816774);
        Shape loopR = Shapes.ellipsoid(-1.4f, 22.2f, -3.5f, 1.2f, 0.66f, 0.38f).paint(Paint.radial(-2305053, -0.3f, 22.0f, -3.6f, 1.6f, -263689)).rotate(0.0f, 0.0f, -16.0f, -1.4f, 22.2f, -3.5f);
        Shape knot = Shapes.ellipsoid(0.0f, 22.0f, -3.58f, 0.48f, 0.52f, 0.36f).color(-1816774);
        Shape tailR = Shapes.strand(0.44f, 0.38f, -0.3f, 21.6f, -3.5f, -0.8f, 20.5f, -3.45f, -1.2f, 19.4f, -3.4f).flatten(2.2f, 0.0f, 0.0f, 1.0f).color(-263689);
        Shape ribbon = Shapes.union(wrap, cord, loopR, loopR.mirrored(), knot, tailR, tailR.mirrored());
        rig.extra("hair_back", Bone.HEAD).pivot(0.0f, Sakura.ty(29.0f), Sakura.tz(-1.6f)).shape(Sakura.up(Shapes.union(backShape, ribbon))).motion(Motion.hair().hang(0.0f).follow(Bone.BODY, 0.85f).drag(12.0f)).detail(0.58f).flatLight(0.5f).occlusion(0.35f).outline(-6864801, 0.09f).decal(Sakura.tailLines());
    }

    private static Decal tailLines() {
        Canvas c = new Canvas(48, 108);
        float k = 12.0f;
        float top = 22.0f;
        int ink = 1890076012;
        for (int m = 0; m < 4; ++m) {
            float s = ((float)m - 1.5f) / 2.0f;
            float x0 = s * 0.72f;
            float x1 = s * 1.02f;
            float x2 = s * 0.84f;
            float x3 = s * 0.2f;
            float mx = (x1 + x2) * 0.5f;
            float end = 14.6f + Math.abs(s) * 0.4f;
            c.fill(Shape2D.curve(24.0f - k * x0, (top - 21.4f) * k, 24.0f - k * x1, (top - 18.9f) * k, 24.0f - k * mx, (top - 17.45f) * k, 0.4f, 1.5f), ink);
            c.fill(Shape2D.curve(24.0f - k * mx, (top - 17.45f) * k, 24.0f - k * x2, (top - 16.0f) * k, 24.0f - k * x3, (top - end) * k, 1.5f, 0.3f), ink);
        }
        return Decal.back(c, 0.0f, Sakura.ty(top - 54.0f / k), Sakura.tz(-3.4f), 4.48f, 10.08f).depth(0.9f, 1.0f).maxAngle(70.0f);
    }

    private static void kanzashi(Rig rig) {
        Shape pin = Shapes.union(Sakura.blossom(1.6f).rotate(-22.0f, 42.0f, 0.0f).move(2.08f, 30.9f, 1.18f), Sakura.blossom(0.95f).rotate(-12.0f, 70.0f, 0.0f).move(2.66f, 30.02f, 0.12f));
        rig.extra("kanzashi", Bone.HEAD).shape(Sakura.up(pin)).resolution(0.42f).detail(0.5f).flatLight(0.4f).outline(-6669732, 0.08f);
        ArrayList<Shape> bira = new ArrayList<Shape>();
        float[][] strings = new float[][]{{2.86f, 29.84f, 1.06f, 2.0f}, {3.2f, 29.74f, 0.52f, 3.0f}};
        for (int i = 0; i < strings.length; ++i) {
            float x = strings[i][0];
            float y = strings[i][1];
            float z = strings[i][2];
            int beads = (int)strings[i][3];
            float end = y - 0.55f * (float)beads;
            bira.add(Shapes.capsule(x, y + 0.05f, z, x + 0.03f, end, z + 0.02f, 0.07f).color(-1459895));
            for (int b = 0; b < beads; ++b) {
                float r = b == beads - 1 ? 0.23f : 0.17f;
                bira.add(Shapes.sphere(x + 0.01f * (float)b, y - 0.55f * (float)(b + 1), z, r).color((b + i) % 2 == 0 ? -2608577 : -1459895));
            }
        }
        rig.extra("bira", Bone.HEAD).pivot(Sakura.tx(3.08f), Sakura.ty(29.86f), Sakura.tz(0.8f)).shape(Sakura.up(Shapes.union(bira.toArray(new Shape[0])))).motion(Motion.hair().hang(1.0f).spring(60.0f, 6.0f)).resolution(0.4f).detail(1.5f).flatLight(0.5f).outline(-8767416, 0.05f);
    }

    private static Shape blossom(float s) {
        ArrayList<Shape> petals = new ArrayList<Shape>();
        for (int k = 0; k < 5; ++k) {
            float a = 90.0f + 72.0f * (float)k;
            double r = Math.toRadians(a);
            float c = (float)Math.cos(r);
            float n = (float)Math.sin(r);
            float px = c * 0.47f * s;
            float py = n * 0.47f * s;
            Shape petal = Shapes.ellipsoid(px, py, 0.0f, 0.3f * s, 0.44f * s, 0.2f).rotate(0.0f, 0.0f, a - 90.0f, px, py, 0.0f);
            petals.add(Shapes.subtract(petal, Shapes.sphere(c * 0.93f * s, n * 0.93f * s, 0.0f, 0.12f * s)));
        }
        Shape petalShape = Shapes.union(petals.toArray(new Shape[0])).paint(Paint.radial(-2078596, 0.0f, 0.0f, 0.1f, 0.4f * s, -8984));
        return Shapes.union(petalShape, Shapes.sphere(0.0f, 0.0f, 0.16f, 0.15f * s).color(-1459895));
    }

    private static Shape capLock(float phi, float from, float to, float r0, float r1) {
        float[] xyz = new float[15];
        for (int i = 0; i < 5; ++i) {
            float[] p = Sakura.onHead(from + (to - from) * (float)i / 4.0f, phi, 0.02f);
            System.arraycopy(p, 0, xyz, i * 3, 3);
        }
        return Shapes.strand(r0, r1, xyz).flattenFrom(2.8f, 0.0f, 28.9f, -0.3f);
    }

    private static float[] onHead(float alpha, float phi, float lift) {
        double a = Math.toRadians(alpha);
        double p = Math.toRadians(phi);
        float ca = (float)Math.cos(a);
        float sa = (float)Math.sin(a);
        float cp = (float)Math.cos(p);
        float sp = (float)Math.sin(p);
        float dx = sp * sa;
        float dy = 0.97f * ca + 0.24f * cp * sa;
        float dz = -0.24f * ca + 0.97f * cp * sa;
        float t = 1.0f / (float)Math.sqrt(dx * dx / 7.3984003f + dy * dy / 7.3984003f + dz * dz / 8.065599f) + lift;
        return new float[]{0.0f + dx * t, 28.9f + dy * t, -0.3f + dz * t};
    }

    private static Paint hairPaint() {
        return Paint.field((x, y, z) -> {
            float dy = y - 31.2f;
            float dz = z + 0.7f;
            float d = (float)Math.sqrt(x * x + dy * dy + dz * dz);
            int c = Colors.mix(-2592100, -677434, Sakura.smooth(0.8f, 2.9f, d));
            return Colors.mix(c, -12321, Sakura.smooth(5.5f, 17.5f, d) * 0.9f);
        });
    }

    private static void body(Rig rig) {
        Shape shoulders = Shapes.union(Shapes.capsule(0.0f, 23.95f, -0.15f, -2.55f, 23.3f, -0.15f, 0.95f), Shapes.capsule(0.0f, 23.95f, -0.15f, 2.55f, 23.3f, -0.15f, 0.95f));
        Shape chest = Shapes.ellipsoid(0.0f, 21.7f, 0.05f, 2.55f, 2.1f, 1.7f);
        Shape bust = Shapes.ellipsoid(0.86f, 21.6f, 0.6f, 0.92f, 0.82f, 0.98f).mirrorX();
        Shape waist = Shapes.ellipsoid(0.0f, 19.6f, 0.0f, 2.15f, 1.7f, 1.5f);
        Shape hips = Shapes.ellipsoid(0.0f, 17.8f, 0.0f, 2.2f, 1.6f, 1.55f);
        Shape torso = Shapes.blend(1.0f, shoulders, Shapes.blend(0.8f, chest, bust), waist, hips).intersect(Shapes.above(17.2f));
        Strand collarR = Shapes.strand(0.42f, 0.4f, 0.0f, 24.75f, -1.1f, -0.95f, 24.62f, -0.72f, -1.22f, 24.3f, 0.35f, -0.75f, 23.35f, 1.45f, 0.12f, 22.45f, 1.92f).flattenFrom(2.2f, 0.0f, 22.5f, -1.2f);
        Strand collarL = Shapes.strand(0.42f, 0.4f, 0.0f, 24.75f, -1.1f, 0.95f, 24.62f, -0.72f, 1.22f, 24.3f, 0.35f, 0.75f, 23.35f, 1.5f, -0.3f, 22.15f, 2.02f, -1.35f, 20.6f, 1.88f).flattenFrom(2.2f, 0.0f, 22.5f, -1.2f);
        Paint kosode = Paint.field((x, y, z) -> {
            float under = Sakura.smooth(0.5f, 1.3f, z) * Sakura.smooth(19.9f, 20.5f, y) * (1.0f - Sakura.smooth(20.6f, 21.1f, y));
            return Colors.mix(-263689, -2305053, under * 0.55f);
        });
        rig.part(Bone.BODY).shape(Shapes.blend(0.14f, torso, collarR, collarL).paint(kosode)).outline(-7568744, 0.1f).detail(0.85f).occlusion(0.2f).decal(Sakura.collar());
        Shape band = Shapes.subtract(Shapes.ellipsoid(0.0f, 19.2f, 0.0f, 2.82f, 2.6f, 2.32f).intersect(Shapes.box(0.0f, 19.2f, 0.0f, 3.2f, 0.3f, 2.7f, 0.12f)), Shapes.ellipsoid(0.0f, 19.2f, 0.0f, 2.4f, 2.6f, 1.9f));
        Shape knot = Shapes.ellipsoid(0.0f, 19.22f, 2.36f, 0.36f, 0.36f, 0.24f);
        Shape loopR = Shapes.ellipsoid(-1.0f, 19.4f, 2.28f, 1.0f, 0.44f, 0.2f).rotate(0.0f, -8.0f, -12.0f, -1.0f, 19.4f, 2.28f);
        Strand tailR = Shapes.strand(0.27f, 0.22f, -0.12f, 19.0f, 2.38f, -0.42f, 18.1f, 2.44f, -0.64f, 17.2f, 2.48f).flatten(2.2f, 0.0f, 0.0f, 1.0f);
        Shape bow = Shapes.union(knot, loopR, loopR.mirrored(), tailR, tailR.mirrored());
        rig.extra("himo", Bone.BODY).shape(Shapes.union(band, bow).color(-263689)).resolution(0.6f).detail(0.36f).outline(-7568744, 0.09f);
    }

    private static Decal collar() {
        Canvas c = new Canvas(92, 78);
        float[] top = Sakura.uv(0.0f, 25.1f);
        float[] low = Sakura.uv(0.0f, 23.2f);
        c.fill(Shape2D.polygon(Sakura.uv(-0.95f, 25.1f, -0.84f, 24.49f, -0.41f, 23.59f, 0.02f, 23.16f, 0.4f, 23.59f, 0.84f, 24.49f, 0.95f, 25.1f)), Paint2D.linear(0.0f, top[1], 0.0f, low[1], -9016, -9016, -1331558));
        Sakura.segments(c, 6.6f, -2674372, -0.9f, 25.1f, -0.84f, 24.49f, -0.41f, 23.59f, 0.05f, 23.12f);
        Sakura.segments(c, 6.6f, -2674372, 0.9f, 25.1f, 0.84f, 24.49f, 0.4f, 23.59f, -0.02f, 23.1f);
        float[] tip = Sakura.uv(0.02f, 23.12f);
        c.fill(Shape2D.circle(tip[0], tip[1], 4.2f), -2674372);
        int line = 1484418696;
        Sakura.segments(c, 1.8f, line, -1.65f, 24.25f, -1.6f, 24.11f, -1.13f, 23.16f, -0.6f, 22.6f);
        Sakura.segments(c, 1.8f, line, 1.65f, 24.25f, 1.6f, 24.11f, 1.09f, 23.12f, 0.55f, 22.45f);
        return Decal.front(c, 0.0f, 23.8f, 2.0f, 3.8333333f, 3.25f).depth(1.6f, 1.2f).maxAngle(86.0f).flatLight(0.9f).occlusion(0.4f);
    }

    private static float[] uv(float ... xy) {
        float[] p = new float[xy.length];
        for (int i = 0; i < xy.length; i += 2) {
            p[i] = (xy[i] + 1.9166666f) * 24.0f;
            p[i + 1] = (25.425f - xy[i + 1]) * 24.0f;
        }
        return p;
    }

    private static void segments(Canvas c, float width, int argb, float ... xy) {
        float[] p = Sakura.uv(xy);
        int i = 0;
        while (i + 3 < p.length) {
            c.fill(Shape2D.line(p[i], p[i + 1], p[i + 2], p[i + 3], width), argb);
            i += 2;
        }
    }

    private static void hakama(Rig rig) {
        Shape slit = Shapes.extrude(Shape2D.polygon(-0.3f, 20.4f, 0.3f, 20.4f, 0.0f, 16.3f), 0.7f, 0.04f).rotate(0.0f, 90.0f, 0.0f).move(-2.95f, 0.0f, 0.0f).color(-263689);
        Shape half = Shapes.subtract(Shapes.field(Sakura::hakamaField, -5.1f, 2.25f, -3.9f, 2.1f, 20.25f, 3.9f).paint(Paint.field(Sakura::hakamaColor)), slit);
        Motion motion = new Motion().follow(Bone.RIGHT_LEG, 0.62f, 13.0f).drag(3.0f).hang(0.5f).spring(80.0f, 11.0f).bend(1.6f).limit(24.0f);
        Part right = rig.extra("hakama_r", Bone.BODY).pivot(-1.0f, 19.6f, 0.0f).shape(half).motion(motion).resolution(1.1f).detail(0.42f).flatLight(0.35f).occlusion(0.2f).outline(-8514270, 0.12f);
        right.decal(Sakura.pleatLine(PLEATS[0])).decal(Sakura.pleatLine(PLEATS[1]));
        rig.extra("hakama_l", Bone.BODY).mirrorOf(right);
    }

    private static Decal pleatLine(float ang) {
        Canvas c = new Canvas(10, 96);
        c.fill(Shape2D.taper(5.0f, 1.0f, 1.2f, 5.0f, 95.0f, 6.0f), Paint2D.linear(0.0f, 0.0f, 0.0f, 40.0f, 8262946, -1719790302));
        float s = (float)Math.sin(ang);
        float k = (float)Math.cos(ang);
        return Decal.wrap(c, -1.14f, 8.0f, 0.0f, 1.15f, 17.5f, 0.0f, -s, 0.0f, k, 3.6f, 11.0f);
    }

    private static float hakamaField(float x, float y, float z) {
        float u = Sakura.clamp01((20.0f - y) / 17.5f);
        float f = (float)Math.pow(u, 0.8);
        float cx = -(0.35f + 1.15f * u);
        float rx = 2.05f + 1.15f * f;
        float rz = 1.9f + 1.5f * f;
        float px = x - cx;
        float d = Sakura.ellipse(px, z, rx, rz);
        float ang = (float)Math.atan2(-px, z);
        float depth = 0.05f + 0.17f * u;
        for (float a : PLEATS) {
            d -= depth * Sakura.pleat(ang - a);
        }
        float cap = Math.max(y - 20.0f, 2.5f - y);
        return Sakura.smax(d, cap, 0.2f) * 0.9f;
    }

    private static int hakamaColor(float x, float y, float z) {
        float u = Sakura.clamp01((20.0f - y) / 17.5f);
        float f = (float)Math.pow(u, 0.8);
        float px = x + 0.35f + 1.15f * u;
        if (y < 2.8f && Sakura.ellipse(px, z, 2.05f + 1.15f * f, 1.9f + 1.5f * f) < -0.2f) {
            return -10087904;
        }
        float ang = (float)Math.atan2(-px, z);
        float fold = 0.0f;
        for (float a : PLEATS) {
            float d = ang - a;
            if (!(d >= 0.0f) || !(d < 0.16f)) continue;
            fold = Math.max(fold, 1.0f - Sakura.smooth(0.02f, 0.16f, d));
        }
        int base = Colors.mix(-2674372, -5955793, Sakura.smooth(9.0f, 2.5f, y) * 0.45f);
        return Colors.mix(base, -5955793, fold * 0.35f * Sakura.smooth(0.02f, 0.2f, u));
    }

    private static float pleat(float d) {
        if (d < -0.4f || d > 0.07f) {
            return 0.0f;
        }
        return d < 0.0f ? Sakura.smooth(-0.4f, 0.0f, d) : 1.0f - Sakura.smooth(0.0f, 0.07f, d);
    }

    private static float ellipse(float px, float pz, float rx, float rz) {
        float ax = px / rx;
        float az = pz / rz;
        float k0 = (float)Math.sqrt(ax * ax + az * az);
        float bx = px / (rx * rx);
        float bz = pz / (rz * rz);
        float k1 = (float)Math.sqrt(bx * bx + bz * bz);
        return k1 < 1.0E-6f ? -Math.min(rx, rz) : k0 * (k0 - 1.0f) / k1;
    }

    private static void arms(Rig rig) {
        float sx = -3.62f;
        float sy = 23.0f;
        float sz = -0.12f;
        float ex = -4.05f;
        float ey = 19.45f;
        float ez = -0.3f;
        float wx = -4.28f;
        float wy = 15.95f;
        float wz = 0.45f;
        float fl = (float)Math.sqrt((wx - ex) * (wx - ex) + (wy - ey) * (wy - ey) + (wz - ez) * (wz - ez));
        float dx = (wx - ex) / fl;
        float dy = (wy - ey) / fl;
        float dz = (wz - ez) / fl;
        Shape tube = Shapes.blend(0.35f, Shapes.cone(sx, sy, sz, 0.66f, ex, ey, ez, 0.58f), Shapes.cone(ex, ey, ez, 0.58f, wx, wy, wz, 0.66f));
        Shape cavity = Shapes.sphere(wx + 0.3f * dx, wy + 0.3f * dy, wz + 0.3f * dz, 0.46f).color(-2502430);
        Part armR = rig.part(Bone.RIGHT_ARM).pivot(-3.5f, 23.25f, -0.1f).grip(-4.32f, 15.05f, 0.72f).shape(Shapes.subtract(0.08f, tube.color(-263689), cavity)).resolution(0.7f).detail(0.7f).outline(-7568744, 0.1f).swing(0.55f).joint(ex, ey, ez, 0.6f);
        rig.part(Bone.LEFT_ARM).mirrorOf(armR);
        Shape bag = Shapes.field((x, y, z) -> {
            float sd = SLEEVE_SIDE.distance(z, y);
            float xc = -4.12f - 0.085f * (23.8f - y);
            float h = 0.3f + 0.16f * Sakura.clamp01((22.0f - y) / 8.0f) + 0.26f * Sakura.smooth(0.0f, 1.3f, -sd) + 0.07f * (float)Math.sin(z * 2.3f + 0.6f) * Sakura.smooth(22.0f, 17.0f, y);
            return Sakura.smax(sd, Math.abs(x - xc) - h, 0.28f);
        }, -6.1f, 13.2f, -3.9f, -3.3f, 24.7f, 1.6f).paint(Paint.vertical(-1053966, 13.5f, -263689, 19.0f));
        Shape tassel = Shapes.union(Shapes.sphere(-5.0f, 14.14f, -3.42f, 0.23f).color(-1816774), Shapes.cone(-5.0f, 13.98f, -3.44f, 0.15f, -5.01f, 13.58f, -3.46f, 0.27f).paint(Paint.vertical(-4705748, 13.3f, -1816774, 13.9f)));
        Motion hang = Motion.cloth().hang(0.7f).drag(8.0f).spring(40.0f, 5.0f).bend(1.3f).limit(35.0f);
        Part sleeveR = rig.extra("sleeve_r", Bone.RIGHT_ARM).shape(Shapes.union(bag, tassel)).motion(hang).resolution(1.0f).detail(0.7f).outline(-7568744, 0.1f).decal(Sakura.sleeveTrim());
        rig.extra("sleeve_l", Bone.LEFT_ARM).mirrorOf(sleeveR);
        Sakura.hands(rig, wx, wy, wz);
    }

    private static void hands(Rig rig, float wx, float wy, float wz) {
        float hx = 4.3f;
        float hy = 15.25f;
        float hz = 0.64f;
        ArrayList<Shape> hand = new ArrayList<Shape>();
        hand.add(Shapes.ellipsoid(hx, hy, hz, 0.33f, 0.6f, 0.52f));
        hand.add(Shapes.capsule(hx - 0.02f, hy + 0.45f, hz - 0.12f, -wx, wy + 0.1f, wz, 0.32f));
        hand.add(Shapes.strand(0.15f, 0.13f, hx + 0.02f, hy - 0.4f, hz + 0.36f, hx - 0.02f, hy - 0.86f, hz + 0.44f, hx - 0.2f, hy - 1.1f, hz + 0.4f));
        hand.add(Shapes.strand(0.15f, 0.13f, hx + 0.03f, hy - 0.44f, hz + 0.12f, hx, hy - 0.98f, hz + 0.14f, hx - 0.22f, hy - 1.26f, hz + 0.11f));
        hand.add(Shapes.strand(0.14f, 0.12f, hx + 0.03f, hy - 0.42f, hz - 0.12f, hx, hy - 0.9f, hz - 0.13f, hx - 0.2f, hy - 1.16f, hz - 0.14f));
        hand.add(Shapes.strand(0.13f, 0.11f, hx + 0.02f, hy - 0.36f, hz - 0.34f, hx, hy - 0.74f, hz - 0.36f, hx - 0.16f, hy - 0.95f, hz - 0.35f));
        hand.add(Shapes.strand(0.17f, 0.14f, hx - 0.18f, hy + 0.1f, hz + 0.42f, hx - 0.3f, hy - 0.3f, hz + 0.58f, hx - 0.32f, hy - 0.54f, hz + 0.5f));
        Part left = rig.extra("hand_l", Bone.LEFT_ARM).shape(Shapes.blend(0.12f, hand.toArray(new Shape[0])).color(-9016)).resolution(0.45f).detail(0.35f).flatLight(1.0f).occlusion(0.25f).outline(-5740452, 0.1f);
        rig.extra("hand_r", Bone.RIGHT_ARM).mirrorOf(left);
    }

    private static Decal sleeveTrim() {
        Canvas c = new Canvas(64, 24);
        float k = 16.0f;
        float[] trim = new float[]{-0.3f, 14.75f, -1.2f, 14.3f, -2.25f, 14.05f, -2.85f, 14.25f, -3.12f, 14.8f};
        int i = 0;
        while (i + 3 < trim.length) {
            c.fill(Shape2D.line((trim[i] + 3.75f) * k, (15.3f - trim[i + 1]) * k, (trim[i + 2] + 3.75f) * k, (15.3f - trim[i + 3]) * k, 3.0f), -1816774);
            i += 2;
        }
        return Decal.right(c, -6.1f, 15.3f - 12.0f / k, -3.75f + 32.0f / k, 64.0f / k, 24.0f / k).depth(1.3f, 1.2f).maxAngle(48.0f);
    }

    private static void legs(Rig rig) {
        float lx = -1.3f;
        Shape tabi = Shapes.blend(0.35f, Shapes.sphere(lx, 1.62f, -0.12f, 0.52f), Shapes.ellipsoid(lx, 0.98f, -0.45f, 0.56f, 0.54f, 0.62f), Shapes.ellipsoid(lx, 0.84f, 0.55f, 0.6f, 0.43f, 1.22f), Shapes.ellipsoid(lx + 0.33f, 0.76f, 1.57f, 0.25f, 0.31f, 0.43f), Shapes.ellipsoid(lx - 0.18f, 0.73f, 1.47f, 0.37f, 0.29f, 0.46f));
        Shape split = Shapes.box(lx + 0.1f, 0.8f, 1.97f, 0.05f, 0.4f, 0.55f, 0.02f).color(-2305053);
        Shape shin = Shapes.cone(lx, 1.7f, -0.1f, 0.5f, lx + 0.02f, 3.3f, -0.08f, 0.54f);
        Shape sock = Shapes.subtract(Shapes.blend(0.3f, tabi, shin).color(-263689), split);
        Shape sole = Shapes.box(lx, 0.22f, 0.52f, 0.74f, 0.22f, 1.64f, 0.2f).paint(Paint.field((x, y, z) -> y > 0.36f ? -1320012 : -10600144));
        Shape straps = Shapes.union(Shapes.strand(0.2f, 0.2f, lx + 0.13f, 0.5f, 1.5f, lx + 0.13f, 0.95f, 1.3f), Shapes.strand(0.2f, 0.2f, lx + 0.13f, 0.95f, 1.3f, lx - 0.35f, 1.08f, 0.85f, lx - 0.62f, 0.55f, 0.35f), Shapes.strand(0.2f, 0.2f, lx + 0.13f, 0.95f, 1.3f, lx + 0.35f, 1.08f, 0.85f, lx + 0.6f, 0.55f, 0.35f)).color(-1816774);
        Shape foot = Shapes.union(sock, sole, straps).rotate(0.0f, -7.0f, 0.0f, lx, 0.0f, -0.1f);
        Part legR = rig.part(Bone.RIGHT_LEG).pivot(lx, 14.2f, 0.0f).shape(foot).resolution(0.6f).detail(0.5f).flatLight(0.4f).outline(-10860992, 0.1f).swing(0.38f, 18.0f);
        rig.part(Bone.LEFT_LEG).mirrorOf(legR);
    }

    private static float px(float x) {
        return 128.0f + x * 56.88889f;
    }

    private static float py(float y) {
        return (29.95f - y) * 56.88889f;
    }

    private static Decal face(float[][] locks) {
        Canvas eye = new Canvas(88, 136);
        float ex = 40.0f;
        float ey = 70.0f;
        Shape2D white = Shape2D.ellipse(ex, ey, 27.5f, 33.0f);
        eye.fill(white, Paint2D.linear(ex, ey - 33.0f, ex, ey + 10.0f, -923920, -1));
        eye.fill(Shape2D.ellipse(ex, ey - 34.0f, 34.0f, 22.0f).intersect(white), -1713846054, 2.0f);
        Shape2D irisShape = Shape2D.ellipse(ex, ey + 3.5f, 20.5f, 27.5f);
        Shape2D iris = irisShape.intersect(white);
        eye.fill(iris, Paint2D.linear(ex, ey - 24.0f, ex, ey + 30.0f, -11923926, -2996102, -19508));
        eye.fill(irisShape.outline(2.6f).intersect(white), -1337323990);
        eye.fill(Shape2D.ellipse(ex, ey + 1.5f, 9.0f, 12.5f), -14023146);
        eye.fill(Shape2D.ellipse(ex, ey + 17.0f, 12.0f, 6.5f).intersect(iris), -2130716956, 3.0f);
        eye.fill(Shape2D.curve(ex + 26.5f, ey - 13.0f, ex + 2.0f, ey - 46.0f, ex - 30.0f, ey - 6.0f, 4.2f, 8.4f), -13757408);
        eye.fill(Shape2D.taper(ex - 25.0f, ey - 9.0f, 6.0f, ex - 35.0f, ey - 6.0f, 1.4f), -13757408);
        eye.fill(Shape2D.curve(ex - 5.0f, ey + 32.0f, ex - 20.0f, ey + 31.0f, ex - 26.5f, ey + 16.0f, 1.6f, 2.8f), -864407480);
        eye.fill(Shape2D.curve(ex + 15.0f, ey - 38.0f, ex, ey - 48.0f, ex - 23.0f, ey - 33.0f, 1.5f, 1.5f), 2009102984);
        eye.fill(Shape2D.curve(ex + 20.0f, ey - 48.0f, ex + 2.0f, ey - 57.0f, ex - 22.0f, ey - 51.0f, 4.0f, 2.6f), -4892806);
        eye.fill(Shape2D.ellipse(ex - 4.0f, ey + 46.0f, 21.0f, 9.0f), 1728023196, 10.0f);
        Canvas c = new Canvas(256, 220);
        int oy = Math.round(Sakura.py(28.0f)) - 70;
        ArrayList<Shape2D> shade = new ArrayList<Shape2D>();
        for (float[] p : locks) {
            float[] xy = Sakura.blade(p);
            for (int i = 0; i < xy.length; i += 2) {
                xy[i] = Sakura.px(xy[i]);
                xy[i + 1] = Sakura.py(xy[i + 1]) + 14.0f;
            }
            shade.add(Shape2D.polygon(xy));
        }
        shade.add(Shape2D.box(128.0f, Sakura.py(29.75f), 130.0f, 14.222222f, 0.0f));
        c.fill(Shape2D.union(shade.toArray(new Shape2D[0])), -1930845035, 3.0f);
        c.draw(eye, 28, oy);
        c.draw(eye.mirrored(), 140, oy);
        for (int side = -1; side <= 1; side += 2) {
            float cx = 128.0f + (float)side * 60.0f;
            c.fill(Shape2D.ellipse(cx - 7.5f, (float)oy + 60.0f, 7.0f, 8.8f), -1);
            c.fill(Shape2D.circle(cx + 8.0f, (float)oy + 83.0f, 3.0f), -570425345);
            c.fill(Shape2D.circle(cx - 12.0f, (float)oy + 76.0f, 1.6f), -1426063361);
        }
        c.fill(Shape2D.line(130.0f, (float)oy + 119.0f, 127.0f, (float)oy + 124.5f, 2.4f), -1713795968);
        c.fill(Shape2D.curve(119.0f, (float)oy + 157.0f, 128.0f, (float)oy + 163.0f, 137.0f, (float)oy + 157.0f, 2.6f, 2.6f), -4896678);
        return Decal.front(c, 0.0f, Sakura.ty(28.016407f), Sakura.tz(2.1f), 5.04f, 4.33125f).depth(2.0f, 1.8f).maxAngle(72.0f).flatLight(1.0f).occlusion(0.3f);
    }

    private static float smax(float a, float b, float k) {
        float h = Sakura.clamp01(0.5f - 0.5f * (b - a) / k);
        return b + (a - b) * h + k * h * (1.0f - h);
    }

    private static float smooth(float e0, float e1, float x) {
        float t = Sakura.clamp01((x - e0) / (e1 - e0));
        return t * t * (3.0f - 2.0f * t);
    }

    private static float clamp01(float v) {
        return v < 0.0f ? 0.0f : Math.min(v, 1.0f);
    }
}

