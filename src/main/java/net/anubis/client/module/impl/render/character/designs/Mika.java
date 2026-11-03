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
import net.anubis.client.module.impl.render.character.kit.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Mika
extends CharacterDesign {
    private static final int SKIN = -9532;
    private static final int SKIN_SHADE = -1331558;
    private static final int SKIN_LINE = -5740966;
    private static final int HAIR = -681403;
    private static final int HAIR_DARK = -3647457;
    private static final int HAIR_TIP = -15750;
    private static final int HAIR_LINE = -6142180;
    private static final int EAR_TIP = -4697060;
    private static final int EAR_PINK = -22088;
    private static final int TUFT = -5162;
    private static final int HOODIE = -4200487;
    private static final int HOODIE_SHADE = -7092297;
    private static final int RIB = -5710645;
    private static final int CUFF_IN = -9461870;
    private static final int HOODIE_LINE = -10774650;
    private static final int LINING = -744532;
    private static final int SKIRT = -12892555;
    private static final int SKIRT_DARK = -14275250;
    private static final int SKIRT_LINE = -14999232;
    private static final int TIGHTS = -12965330;
    private static final int TIGHTS_SHEEN = -9808288;
    private static final int TIGHTS_LINE = -15068908;
    private static final int SHOE = -542005;
    private static final int SOLE = -264207;
    private static final int MINT = -5839921;
    private static final int FISH = -8398652;
    private static final int FISH_LINE = -12811148;
    private static final int SHOE_LINE = -6659470;
    private static final int GOLD = -867761;
    private static final int TAIL_TIP = -3618;
    private static final int LASH = -13887470;
    private static final int BROW = -4691398;
    private static final float HX = 0.0f;
    private static final float HY = 27.95f;
    private static final float HZ = -0.15f;
    private static final float RX = 2.3f;
    private static final float RY = 2.3f;
    private static final float RZ = 2.35f;
    private static final float DROP = 0.4f;
    private static final float HS = 1.12f;
    private static final float NY = 24.9f;
    private static final float NZ = -0.3f;
    private static final float PPU = 55.652176f;
    private static final float TOP = 29.0f;
    private static final int PLEATS = 16;
    private static final float[][] FRINGE = new float[][]{{1.0f, -1.0f, 30.15f, -1.5f, 29.6f, -2.0f, 28.3f, -1.8f, 27.0f, 0.3f}, {0.95f, -0.42f, 30.25f, -0.75f, 29.75f, -1.05f, 28.6f, -0.85f, 27.9f, -0.35f}, {1.05f, -0.28f, 30.3f, 0.0f, 29.8f, 0.5f, 28.4f, 0.42f, 27.2f, 0.4f}, {0.95f, 0.55f, 30.25f, 0.9f, 29.75f, 1.25f, 28.6f, 1.02f, 27.65f, -0.3f}, {1.0f, 1.1f, 30.15f, 1.6f, 29.6f, 2.05f, 28.3f, 1.85f, 26.75f, 0.35f}, {0.65f, -0.75f, 30.2f, -1.2f, 29.6f, -1.5f, 28.3f, -1.3f, 27.45f, 0.3f}, {0.5f, 0.28f, 30.25f, 0.6f, 29.7f, 0.88f, 28.5f, 0.75f, 27.5f, -0.3f}, {0.5f, -0.45f, 30.25f, -0.4f, 29.7f, -0.3f, 28.3f, -0.18f, 27.6f, 0.3f}};
    private static final float[][] CROWN = new float[][]{{-0.413f, 0.766f, 0.492f}, {-0.992f, 0.087f, -0.087f}, {-0.539f, -0.342f, -0.77f}, {0.464f, -0.375f, -0.803f}, {0.975f, 0.139f, -0.172f}, {0.353f, 0.788f, 0.504f}};

    public Mika() {
        super("mika", "Mika");
    }

    @Override
    protected void define(Rig rig) {
        rig.scale(1.0f).outline(-12967400, 0.12f).occlusion(0.3f);
        Mika.body(rig);
        Mika.head(rig);
        Mika.hair(rig);
        Mika.ears(rig);
        Mika.arms(rig);
        Mika.legs(rig);
        Mika.tail(rig);
    }

    private static Shape toHead(Shape s) {
        return s.move(0.0f, -0.4f, 0.0f).scale(1.12f, 0.0f, 24.9f, -0.3f);
    }

    private static float wx(float x) {
        return x * 1.12f;
    }

    private static float wy(float y) {
        return 24.9f + (y - 0.4f - 24.9f) * 1.12f;
    }

    private static float wz(float z) {
        return -0.3f + (z - -0.3f) * 1.12f;
    }

    private static float u(float x) {
        return 128.0f + x * 55.652176f;
    }

    private static float v(float y) {
        return (29.0f - y) * 55.652176f;
    }

    private static float capZ(float x, float y, float off) {
        float ax = x / (2.72f + off);
        float ay = (y - 28.25f) / (2.8f + off);
        return -0.28f + (2.85f + off) * (float)Math.sqrt(Math.max(0.02f, 1.0f - ax * ax - ay * ay));
    }

    private static float[] onCap(float dx, float dy, float dz, float off) {
        float l = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        float ax = (dx /= l) / (2.72f + off);
        float ay = (dy /= l) / (2.8f + off);
        float az = (dz /= l) / (2.85f + off);
        float k = 1.0f / (float)Math.sqrt(ax * ax + ay * ay + az * az);
        return new float[]{dx * k, 28.25f + dy * k, -0.28f + dz * k};
    }

    private static Shape crownClump(float[] t, float w) {
        float wx = 0.15f;
        float wy = 0.93f;
        float wz = -0.35f;
        float[] p = new float[16];
        float[] s = new float[]{0.0f, 0.38f, 0.72f, 1.0f};
        float[] r = new float[]{0.2f, 0.85f * w, 0.8f * w, 0.3f};
        float[] off = new float[]{-0.12f, -0.07f, -0.07f, -0.14f};
        for (int i = 0; i < 4; ++i) {
            float[] q = Mika.onCap(wx + (t[0] - wx) * s[i], wy + (t[1] - wy) * s[i], wz + (t[2] - wz) * s[i], off[i]);
            p[i * 4] = q[0];
            p[i * 4 + 1] = q[1];
            p[i * 4 + 2] = q[2];
            p[i * 4 + 3] = r[i];
        }
        return Shapes.tube(p).flattenFrom(3.2f, 0.0f, 28.25f, -0.28f);
    }

    private static float surf(float x, float y, float off) {
        float ax = x / (2.3f + off);
        float ay = (y - 27.95f) / (2.3f + off);
        return -0.15f + (2.35f + off) * (float)Math.sqrt(Math.max(0.02f, 1.0f - ax * ax - ay * ay));
    }

    private static void head(Rig rig) {
        Shape cranium = Shapes.ellipsoid(0.0f, 27.95f, -0.15f, 2.3f, 2.3f, 2.35f);
        Shape cheeks = Shapes.ellipsoid(0.0f, 26.72f, 0.35f, 1.93f, 1.3f, 1.7f);
        Shape jaw = Shapes.cone(0.0f, 26.2f, 0.45f, 1.2f, 0.0f, 25.25f, 0.98f, 0.3f);
        Shape hidden = Shapes.union(Shapes.box(0.0f, 29.0f, -1.8f, 3.0f, 2.3f, 1.5f), Shapes.box(0.0f, 30.3f, 0.5f, 3.0f, 1.3f, 3.0f));
        Shape skull = Shapes.subtract(Shapes.blend(0.7f, cranium, cheeks, jaw), hidden).color(-9532);
        rig.part(Bone.HEAD).pivot(0.0f, 24.9f, -0.3f).shape(Mika.toHead(skull)).detail(1.5f).flatLight(1.0f).occlusion(0.2f).outline(-5740966, 0.1f).decal(Mika.face());
        Paint neck = Paint.vertical(-9532, 23.7f, -1331558, 24.3f).flatLight(1.0f).occlusion(0.25f);
        rig.extra("neck", Bone.BODY).shape(Shapes.capsule(0.0f, 23.1f, -0.28f, 0.0f, 25.2f, -0.2f, 0.62f).paint(neck)).outline(-5740966, 0.08f);
    }

    private static void hair(Rig rig) {
        Paint paint = Paint.field(Mika::hairColor);
        Shape cap = Shapes.ellipsoid(0.0f, 28.25f, -0.28f, 2.72f, 2.8f, 2.85f).intersect(Shapes.plane(0.0f, 27.4f, 1.2f, 0.0f, -0.6f, 1.0f)).intersect(Shapes.above(26.4f));
        Shape[] clumps = new Shape[CROWN.length];
        for (int i = 0; i < CROWN.length; ++i) {
            clumps[i] = Mika.crownClump(CROWN[i], i % 2 == 0 ? 1.0f : 0.9f);
        }
        Shape crown = Shapes.blend(0.3f, cap, Shapes.union(clumps));
        Part top = rig.extra("hair", Bone.HEAD).shape(Mika.toHead(Shapes.union(crown, Mika.backLock(180.0f, 24.95f, 1.05f, 0.25f)).paint(paint))).detail(0.9f).resolution(0.95f).flatLight(0.5f).occlusion(0.3f).outline(-6142180, 0.09f);
        float[][] back = new float[][]{{-54.0f, 25.95f, 0.9f, 0.3f}, {-75.0f, 25.45f, 1.0f, -0.3f}, {-97.0f, 25.8f, 1.05f, 0.25f}, {-119.0f, 25.3f, 1.05f, -0.2f}, {-140.0f, 25.5f, 1.05f, 0.3f}, {-161.0f, 25.1f, 1.0f, -0.3f}};
        ArrayList<Shape> half = new ArrayList<Shape>();
        for (float[] b : back) {
            half.add(Mika.backLock(b[0], b[1], b[2], b[3]));
        }
        half.add(Mika.topLock(-84.0f, 26.25f));
        half.add(Mika.topLock(-128.0f, 26.5f));
        Part right = rig.extra("hair_r", Bone.HEAD).pivot(0.0f, Mika.wy(29.8f), Mika.wz(-0.4f)).shape(Mika.toHead(Shapes.union(half.toArray(new Shape[0])).paint(paint))).detail(1.0f).resolution(0.85f).flatLight(0.5f).occlusion(0.35f).outline(-6142180, 0.09f).motion(Motion.hair().idle(1.2f, 0.0f, 1.0f, 0.35f).walk(2.5f, 0.0f, 2.0f, 2.0f).drag(6.0f).hang(0.2f).turn(5.0f, 0.0f).spring(60.0f, 8.0f).bend(1.6f));
        rig.extra("hair_l", Bone.HEAD).mirrorOf(right);
        Shape[] locks = new Shape[FRINGE.length];
        for (int i = 0; i < FRINGE.length; ++i) {
            locks[i] = Mika.fringeLock(FRINGE[i]);
        }
        Shape inner = Shapes.ellipsoid(0.0f, 28.25f, -0.28f, 2.56f, 2.64f, 2.69f).intersect(Shapes.above(29.3f));
        Shape bangs = Shapes.subtract(Shapes.union(locks), inner);
        Part fringe = rig.extra("fringe", Bone.HEAD).shape(Mika.toHead(bangs.paint(Paint.vertical(-15750, 26.9f, -681403, 29.2f)))).detail(1.3f).resolution(0.5f).flatLight(0.5f).occlusion(0.25f).outline(-6142180, 0.08f);
        Mika.shine(top, fringe);
        Mika.clip(rig);
        Strand side = Shapes.tube(-1.95f, 29.5f, 0.85f, 0.42f, -2.36f, 28.4f, 1.05f, 0.56f, -2.42f, 27.1f, 1.08f, 0.6f, -2.3f, 25.9f, 1.02f, 0.5f, -2.26f, 25.0f, 0.98f, 0.34f, -2.5f, 24.45f, 0.95f, 0.05f).flatten(2.3f, -0.55f, 0.0f, 0.83f);
        Part lock = rig.extra("lock_r", Bone.HEAD).pivot(Mika.wx(-1.95f), Mika.wy(29.4f), Mika.wz(0.85f)).shape(Mika.toHead(Mika.pointed(side, -2.5f, 24.45f, 0.95f, 0.4f, 1.0f, 0.3f).paint(paint))).detail(0.8f).resolution(0.55f).flatLight(0.5f).occlusion(0.35f).outline(-6142180, 0.09f).motion(Motion.hair().drag(10.0f).hang(0.35f));
        rig.extra("lock_l", Bone.HEAD).mirrorOf(lock);
        Strand ahoge = Shapes.tube(0.05f, 30.8f, 0.1f, 0.24f, 0.03f, 31.45f, 0.2f, 0.26f, 0.3f, 32.05f, 0.32f, 0.22f, 0.8f, 32.38f, 0.42f, 0.17f, 1.34f, 32.35f, 0.5f, 0.11f, 1.72f, 32.02f, 0.55f, 0.05f).flatten(1.6f, 0.0f, 0.0f, 1.0f);
        rig.extra("ahoge", Bone.HEAD).pivot(Mika.wx(0.05f), Mika.wy(30.85f), Mika.wz(0.1f)).shape(Mika.toHead(ahoge.paint(Paint.vertical(-681403, 31.2f, -15750, 32.1f)))).resolution(0.5f).detail(0.9f).flatLight(0.5f).occlusion(0.3f).outline(-6142180, 0.08f).motion(Motion.hair().idle(4.0f, 0.0f, 6.0f, 0.5f).walk(8.0f, 0.0f, 8.0f, 2.0f).drag(6.0f).hang(0.3f).spring(70.0f, 5.0f).bend(1.2f));
    }

    private static int hairColor(float x, float y, float z) {
        int base = Colors.mix(-15750, -681403, Mika.smooth(25.0f, 27.6f, y));
        float rho = (float)Math.sqrt(x * x + (z + 0.25f) * (z + 0.25f));
        float inner = (1.0f - Mika.smooth(1.9f, 2.55f, rho)) * (1.0f - Mika.smooth(26.4f, 27.6f, y));
        return Colors.mix(base, -3647457, inner * 0.75f);
    }

    private static Shape fringeLock(float[] f) {
        float lift = f[0] < 0.7f ? 0.52f : (f[9] > 0.0f ? 0.4f : 0.28f);
        Strand lock = Shapes.strand(f[0], 0.05f, f[1], f[2], Mika.capZ(f[1], f[2], -0.3f), f[3], f[4], Mika.capZ(f[3], f[4], 0.1f), f[5], f[6], Mika.surf(f[5], f[6], lift), f[7], f[8], Mika.surf(f[7], f[8], 0.12f)).flattenFrom(2.8f, 0.0f, 27.95f, -0.15f);
        float dx = f[5] - f[7];
        float dy = f[6] - f[8];
        float l = (float)Math.sqrt(dx * dx + dy * dy);
        float ra = 0.9f * (1.0f + f[9]);
        float rb = 0.9f * (1.0f - f[9]);
        float s = 0.34f;
        Shape2D v = Shape2D.polygon(f[7], f[8], f[7] + (dx /= l) * ra - (dy /= l) * s, f[8] + dy * ra + dx * s, f[7] + dx * 8.0f - dy * 5.0f, f[8] + dy * 8.0f + dx * 5.0f, f[7] + dx * 8.0f + dy * 5.0f, f[8] + dy * 8.0f - dx * 5.0f, f[7] + dx * rb + dy * s, f[8] + dy * rb - dx * s);
        return lock.intersect(Shapes.extrude(v, 6.0f, 0.0f));
    }

    private static void clip(Rig rig) {
        Shape2D fish = Shape2D.blend(0.08f, Shape2D.ellipse(-0.1f, 0.0f, 0.5f, 0.32f), Shape2D.polygon(0.3f, 0.0f, 0.86f, 0.36f, 0.66f, 0.0f, 0.86f, -0.36f));
        Shape body = Shapes.extrude(fish, 0.15f, 0.11f).scale(1.3f).paint(Paint.gradient(-3869978, 0.0f, 0.35f, 0.0f, -8398652, 0.0f, -0.35f, 0.0f));
        float x = 1.62f;
        float y = 28.7f;
        float off = 0.75f;
        float z = Mika.surf(x, y, off);
        float ax = 2.3f + off;
        float ay = 2.3f + off;
        float az = 2.35f + off;
        float nx = x / (ax * ax);
        float ny = (y - 27.95f) / (ay * ay);
        float nz = (z - -0.15f) / (az * az);
        float l = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
        float yaw = (float)Math.toDegrees(Math.atan2(nx, nz));
        float pitch = (float)(-Math.toDegrees(Math.asin(ny / l)));
        Shape placed = body.rotate(0.0f, 0.0f, 35.0f).rotate(pitch, yaw, 0.0f).move(x, y, z);
        Vec3 eye = new Vec3(-0.5f, 0.08f, 0.2f).rotate(0.0f, 0.0f, 35.0f).rotate(pitch, yaw, 0.0f).add(x, y, z);
        Vec3 n = new Vec3(0.0f, 0.0f, 1.0f).rotate(0.0f, 0.0f, 35.0f).rotate(pitch, yaw, 0.0f);
        Canvas c = new Canvas(32, 32);
        c.fill(Shape2D.circle(16.0f, 16.0f, 7.0f), -14796236);
        c.fill(Shape2D.circle(13.5f, 13.5f, 2.2f), -855638017);
        Decal dot = Decal.project(c, Mika.wx(eye.x()), Mika.wy(eye.y()), Mika.wz(eye.z()), n.x(), n.y(), n.z(), 0.0f, 1.0f, 0.0f, 0.33600003f, 0.33600003f).depth(0.25f, 0.25f);
        rig.extra("clip", Bone.HEAD).shape(Mika.toHead(placed)).resolution(0.4f).detail(0.5f).flatLight(0.4f).outline(-12811148, 0.1f).decal(dot);
    }

    private static void shine(Part top, Part fringe) {
        float[][] dash;
        Canvas c = new Canvas(96, 24);
        c.fill(Shape2D.union(Shape2D.taper(3.0f, 12.0f, 1.0f, 48.0f, 12.0f, 17.0f), Shape2D.taper(48.0f, 12.0f, 17.0f, 93.0f, 12.0f, 1.0f)), -419442022, 1.5f);
        for (float[] d : dash = new float[][]{{-52.0f, 0.72f, 0.12f, 6.0f}, {-24.0f, 0.9f, -0.06f, -4.0f}, {4.0f, 0.66f, 0.08f, 5.0f}, {27.0f, 0.82f, -0.08f, -6.0f}, {57.0f, 0.7f, 0.1f, 3.0f}}) {
            double a = Math.toRadians(d[0]);
            float sx = (float)Math.sin(a);
            float cz = (float)Math.cos(a);
            float[] p = Mika.onCap(sx, 0.36f + 0.3f * (1.0f - cz) + d[2], cz, 0.28f);
            float nx = p[0];
            float ny = p[1] - 28.25f;
            float nz = p[2] + 0.28f;
            Vec3 up = new Vec3(0.0f, 1.0f, 0.0f).rotate(0.0f, 0.0f, d[3]);
            Decal glint = Decal.project(c, Mika.wx(p[0]), Mika.wy(p[1]), Mika.wz(p[2]), nx, ny * 0.6f, nz, up.x(), up.y(), up.z(), d[1] * 1.12f, 0.2464f).depth(0.896f, 0.896f).maxAngle(80.0f).flatLight(0.8f).occlusion(0.0f);
            top.decal(glint);
            fringe.decal(glint);
        }
    }

    private static Shape pointed(Strand lock, float tx, float ty, float tz, float spread, float rise, float skew) {
        float cz = -0.25f;
        float deg = (float)Math.toDegrees(Math.atan2(tx, tz - cz));
        float ra = rise * (1.0f + skew);
        float rb = rise * (1.0f - skew);
        Shape2D v = Shape2D.polygon(-5.0f, ty + 8.0f, 5.0f, ty + 8.0f, spread, ty + ra, 0.0f, ty, -spread, ty + rb);
        return lock.intersect(Shapes.extrude(v, 5.0f, 0.0f).rotate(0.0f, deg, 0.0f, 0.0f, 0.0f, cz));
    }

    private static Shape backLock(float deg, float endY, float width, float skew) {
        double a = Math.toRadians(deg);
        float sx = (float)Math.sin(a);
        float cz = (float)Math.cos(a);
        float back = Math.max(0.0f, Math.min(1.0f, (Math.abs(deg) - 100.0f) / 70.0f));
        float[] ys = new float[]{30.1f, 29.1f, 27.9f, 26.9f, (26.9f + endY) * 0.5f - 0.1f, endY};
        float[] rx = new float[]{2.04f, 2.59f, 2.7f, 2.52f, 2.42f, 2.32f};
        float[] rz = new float[]{2.14f, 2.72f, 2.83f, 2.62f, 2.48f, 2.35f};
        float[] out = new float[]{-0.34f, -0.06f, 0.04f, 0.04f, 0.02f, -0.06f - 0.24f * back};
        float[] rs = new float[]{0.7f, 1.05f, 1.05f, 0.95f, 0.64f, 0.06f};
        float[] p = new float[24];
        for (int i = 0; i < 6; ++i) {
            p[i * 4] = sx * (rx[i] + out[i]);
            p[i * 4 + 1] = ys[i];
            p[i * 4 + 2] = -0.28f + cz * (rz[i] + out[i]);
            p[i * 4 + 3] = rs[i] * (i == 5 ? 1.0f : width);
        }
        return Mika.pointed(Shapes.tube(p).flattenFrom(2.1f, 0.0f, 27.95f, -0.15f), p[20], endY, p[22], 0.62f, 1.25f, skew);
    }

    private static Shape topLock(float deg, float endY) {
        double a = Math.toRadians(deg);
        float sx = (float)Math.sin(a);
        float cz = (float)Math.cos(a);
        float[] ring = new float[]{2.2f, 2.8f, 2.98f, 3.0f};
        float[] ys = new float[]{29.4f, 28.2f, 27.2f, endY};
        float[] rs = new float[]{0.55f, 0.75f, 0.55f, 0.08f};
        float[] p = new float[16];
        for (int i = 0; i < 4; ++i) {
            p[i * 4] = sx * ring[i];
            p[i * 4 + 1] = ys[i];
            p[i * 4 + 2] = -0.25f + cz * ring[i] * 1.05f;
            p[i * 4 + 3] = rs[i];
        }
        return Mika.pointed(Shapes.tube(p).flattenFrom(2.2f, 0.0f, 27.95f, -0.15f), p[12], endY, p[14], 0.6f, 1.15f, 0.2f);
    }

    private static void ears(Rig rig) {
        Shape front = Shapes.extrude(Shape2D.polygon(-1.1f, 0.0f, 1.1f, 0.0f, 0.08f, 2.55f), 0.64f, 0.32f);
        Shape profile = Shapes.extrude(Shape2D.polygon(0.6f, -0.3f, -0.62f, -0.3f, -0.15f, 2.7f, 0.2f, 2.7f), 1.3f, 0.2f).rotate(0.0f, 90.0f, 0.0f);
        Shape outer = Shapes.intersect(front, profile).paint(Paint.vertical(-681403, 1.45f, -4697060, 2.25f));
        Shape inner = Shapes.extrude(Shape2D.polygon(-0.66f, 0.3f, 0.68f, 0.3f, 0.07f, 1.95f), 0.5f, 0.14f).move(0.0f, 0.0f, 0.62f).paint(Paint.of(-22088).flatLight(0.7f));
        Shape tuft = Shapes.union(Shapes.strand(0.14f, 0.05f, -0.34f, 0.28f, 0.25f, -0.46f, 0.6f, 0.44f, -0.38f, 0.9f, 0.5f), Shapes.strand(0.14f, 0.05f, 0.3f, 0.28f, 0.25f, 0.46f, 0.58f, 0.44f, 0.56f, 0.86f, 0.48f)).paint(Paint.of(-5162).flatLight(0.7f));
        Shape ear = Shapes.union(Shapes.subtract(0.1f, outer, inner), tuft).scale(1.18f).rotate(-6.0f, -18.0f, 24.0f).move(-1.6f, 29.75f, -0.15f);
        Part right = rig.extra("ear_r", Bone.HEAD).pivot(Mika.wx(-1.6f), Mika.wy(29.9f), Mika.wz(-0.15f)).shape(Mika.toHead(ear)).resolution(0.58f).detail(0.9f).flatLight(0.4f).outline(-6142180, 0.09f).motion(Mika.earMotion(0.0f, -1.0f));
        rig.extra("ear_l", Bone.HEAD).mirrorOf(right).motion(Mika.earMotion(140.0f, 1.0f));
    }

    private static Motion earMotion(float phase, float side) {
        return Motion.none().idle(2.5f, 0.0f, 5.0f * side, 0.55f).idlePhase(phase).walk(3.0f, 0.0f, 3.0f * side, 2.0f).spring(90.0f, 7.0f);
    }

    private static void body(Rig rig) {
        Shape yoke = Shapes.union(Shapes.cone(0.0f, 22.6f, -0.2f, 1.02f, -1.95f, 22.2f, -0.2f, 0.9f), Shapes.cone(0.0f, 22.6f, -0.2f, 1.02f, 1.95f, 22.2f, -0.2f, 0.9f));
        Shape chest = Shapes.ellipsoid(0.0f, 20.5f, -0.1f, 2.3f, 2.4f, 1.72f);
        Shape belly = Shapes.ellipsoid(0.0f, 17.8f, -0.02f, 2.25f, 2.9f, 1.7f);
        Shape blouse = Shapes.ellipsoid(0.0f, 15.25f, -0.05f, 2.45f, 1.25f, 1.86f);
        Shape torso = Shapes.blend(1.0f, yoke, chest, belly, blouse).paint(Paint.field(Mika::hoodie));
        Shape pocket = Shapes.intersect(Shapes.ellipsoid(0.0f, 16.3f, -0.02f, 2.6f, 2.9f, 2.02f), Shapes.extrude(Shape2D.polygon(-1.6f, 17.3f, 1.6f, 17.3f, 2.15f, 14.75f, -2.15f, 14.75f), 2.0f, 0.0f).move(0.0f, 0.0f, 2.0f)).color(-4200487);
        Shape hem = Shapes.extrude(Shape2D.ellipse(0.0f, 0.0f, 2.42f, 1.8f), 0.4f, 0.3f).rotate(90.0f, 0.0f, 0.0f).move(0.0f, 14.2f, -0.05f).color(-5710645);
        rig.part(Bone.BODY).pivot(0.0f, 23.4f, -0.1f).shape(Shapes.union(Shapes.blend(0.25f, torso, pocket), hem)).resolution(1.15f).detail(1.0f).outline(-10774650, 0.12f).occlusion(0.3f).flatLight(0.2f).decal(Mika.pocketDecal()).decal(Mika.pawDecal()).decal(Mika.hemRib());
        rig.extra("collar", Bone.BODY).shape(Mika.rim().paint(Paint.field(Mika::rimColor))).resolution(0.7f).detail(0.8f).occlusion(0.3f).flatLight(0.2f).outline(-10774650, 0.1f);
        Shape pouch = Shapes.blend(0.8f, Shapes.ellipsoid(0.0f, 21.7f, -1.85f, 2.2f, 2.2f, 0.85f), Shapes.cone(0.0f, 20.6f, -2.3f, 0.8f, 0.0f, 19.1f, -2.2f, 0.28f));
        Shape dip = Shapes.ellipsoid(0.0f, 0.0f, 0.0f, 1.55f, 0.35f, 0.85f).rotate(-26.6f, 0.0f, 0.0f).move(0.0f, 22.95f, -1.95f);
        Shape hood = Shapes.subtract(0.15f, pouch.intersect(Shapes.plane(0.0f, 22.7f, -2.2f, 0.0f, 1.0f, -0.5f)), dip).paint(Paint.field(Mika::hoodColor));
        rig.extra("hood", Bone.BODY).shape(hood).resolution(0.85f).detail(0.6f).occlusion(0.3f).flatLight(0.2f).outline(-10774650, 0.12f);
        rig.extra("skirt", Bone.BODY).pivot(0.0f, 14.6f, 0.0f).shape(Mika.skirt()).resolution(0.9f).detail(0.8f).occlusion(0.35f).outline(-14999232, 0.12f).decal(Mika.skirtStripe()).motion(Motion.none().walk(0.0f, 8.0f, 0.0f, 1.0f).walkPhase(-90.0f).drag(2.0f).spring(80.0f, 10.0f));
        Shape cord = Shapes.tube(-0.32f, 23.5f, 1.45f, 0.13f, -0.38f, 22.6f, 1.75f, 0.13f, -0.44f, 21.3f, 1.95f, 0.13f, -0.48f, 20.1f, 2.0f, 0.12f).color(-264984);
        Shape bell = Shapes.sphere(-0.48f, 19.6f, 2.05f, 0.45f).paint(Paint.radial(-3920, -0.62f, 19.8f, 2.35f, 0.6f, -867761));
        Part cordR = rig.extra("cord_r", Bone.BODY).pivot(-0.32f, 23.5f, 1.45f).shape(Shapes.union(cord, bell)).resolution(0.5f).detail(0.5f).outline(-10774650, 0.08f).decal(Mika.bellDecal(-0.48f)).motion(Motion.none().rest(-3.0f, 0.0f, 0.0f).idle(1.5f, 0.0f, 2.0f, 0.5f).walk(5.0f, 0.0f, 6.0f, 2.0f).drag(-4.0f).hang(0.9f).spring(60.0f, 6.0f).bend(0.8f));
        rig.extra("cord_l", Bone.BODY).mirrorOf(cordR);
    }

    private static Shape rim() {
        return Shapes.tube(0.02f, 23.64f, 1.44f, 0.36f, -0.5f, 23.84f, 1.3f, 0.41f, -1.12f, 24.08f, 0.8f, 0.43f, -1.42f, 24.2f, -0.18f, 0.43f, -1.1f, 24.3f, -1.14f, 0.43f, 0.1f, 24.34f, -1.68f, 0.43f).flattenFrom(1.25f, 0.0f, 24.1f, -0.25f).mirrorX();
    }

    private static int hoodie(float x, float y, float z) {
        float rho = (float)Math.sqrt(x * x + (z + 0.2f) * (z + 0.2f));
        float collar = Mika.smooth(22.4f, 23.3f, y) * (1.0f - Mika.smooth(1.7f, 2.3f, rho)) * 0.6f;
        float fold = (1.0f - Mika.smooth(14.6f, 15.3f, y)) * 0.55f;
        return Colors.mix(-4200487, -7092297, Math.max(collar, fold));
    }

    private static int rimColor(float x, float y, float z) {
        float rho = (float)Math.sqrt(x * x + (z + 0.2f) * (z + 0.2f));
        float top = Mika.smooth(0.28f, 0.36f, y - 24.0f + 0.22f * z);
        return Colors.mix(-4200487, -744532, Math.max(top, 1.0f - Mika.smooth(1.12f, 1.38f, rho)));
    }

    private static int hoodColor(float x, float y, float z) {
        float ex = x / 2.2f;
        float ey = (y - 21.7f) / 2.2f;
        float ez = (z + 1.85f) / 0.85f;
        float cut = (y - 22.7f - 0.5f * (z + 2.2f)) * 0.894f;
        return cut > -0.5f && ex * ex + ey * ey + ez * ez < 0.8f ? -744532 : -4200487;
    }

    private static Shape skirt() {
        return Shapes.field(Mika::skirtDistance, -3.3f, 11.8f, -2.8f, 3.3f, 14.4f, 2.8f).paint(Paint.field(Mika::skirtColor));
    }

    private static float flare(float y) {
        float t = Math.max(0.0f, Math.min(1.0f, (14.0f - y) / 2.1f));
        return t * (2.0f - t);
    }

    private static float pleat(float x, float z, float t) {
        float rx = 2.2f + 0.75f * t;
        float rz = 1.66f + 0.84f * t;
        double f = Math.atan2(x / rx, z / rz) * 16.0 / (Math.PI * 2);
        f -= Math.floor(f);
        return (float)Math.abs(2.0 * f - 1.0);
    }

    private static float skirtDistance(float x, float y, float z) {
        float t = Mika.flare(y);
        float rx = 2.2f + 0.75f * t;
        float rz = 1.66f + 0.84f * t;
        float a = x / rx;
        float b = z / rz;
        float k = (float)Math.sqrt(a * a + b * b);
        float side = (k - 1.0f) * Math.min(rx, rz) * 0.72f - 0.2f * t * (Mika.pleat(x, z, t) - 0.5f);
        return Math.max(side, Math.max(y - 14.3f, 11.9f - y));
    }

    private static int skirtColor(float x, float y, float z) {
        float t = Mika.flare(y);
        return Colors.mix(-14275250, -12892555, 0.45f + 0.55f * (1.0f - t + t * Mika.pleat(x, z, t)));
    }

    private static Decal skirtStripe() {
        Canvas c = new Canvas(512, 24);
        c.fill(Shape2D.box(256.0f, 12.0f, 256.0f, 7.0f, 0.0f), -5839921);
        return Decal.wrap(c, 0.0f, 12.35f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.16f).radius(2.2f, 3.2f);
    }

    private static void arms(Rig rig) {
        Shape shoulder = Shapes.sphere(-2.72f, 21.95f, -0.15f, 0.95f);
        Shape upper = Shapes.cone(-2.85f, 21.6f, -0.15f, 0.9f, -3.35f, 18.4f, -0.2f, 0.88f);
        Shape elbow = Shapes.sphere(-3.38f, 18.3f, -0.18f, 0.9f);
        Shape fore = Shapes.cone(-3.38f, 18.2f, -0.18f, 0.9f, -3.72f, 15.0f, 0.45f, 1.12f);
        Shape sleeve = Shapes.blend(0.4f, shoulder, upper, elbow, fore).paint(Paint.field(Mika::sleeve));
        Shape cuff = Shapes.cylinder(-3.74f, 15.05f, 0.47f, -3.8f, 13.4f, 0.69f, 0.9f, 0.28f).paint(Paint.field(Mika::cuff));
        Shape arm = Shapes.blend(0.15f, sleeve, cuff);
        Part right = rig.part(Bone.RIGHT_ARM).pivot(-2.72f, 21.95f, -0.15f).grip(-3.8f, 12.9f, 0.9f).shape(arm).detail(0.9f).resolution(1.05f).outline(-10774650, 0.12f).occlusion(0.3f).flatLight(0.2f).swing(0.65f, 40.0f).joint(-3.38f, 18.3f, -0.18f, 0.7f).decal(Mika.cuffRib());
        rig.part(Bone.LEFT_ARM).mirrorOf(right);
        Part hand = rig.extra("hand_r", Bone.RIGHT_ARM).shape(Mika.hand()).resolution(0.4f).detail(2.6f).flatLight(1.0f).occlusion(0.25f).outline(-5740966, 0.08f);
        rig.extra("hand_l", Bone.LEFT_ARM).mirrorOf(hand);
    }

    private static int sleeve(float x, float y, float z) {
        float t = Math.max(0.0f, Math.min(1.0f, (21.9f - y) / 6.9f));
        float inner = Mika.smooth(0.25f, 0.85f, x + 2.8f + 0.94f * t) * 0.5f;
        float crease = (1.0f - Mika.smooth(0.2f, 0.55f, Math.abs(y - 18.25f))) * Mika.smooth(0.2f, 0.7f, z) * 0.45f;
        return Colors.mix(-4200487, -7092297, Math.max(inner, crease));
    }

    private static int cuff(float x, float y, float z) {
        float ax = -0.06f;
        float ay = -1.65f;
        float az = 0.22f;
        float l = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        float px = x + 3.8f;
        float py = y - 13.4f;
        float pz = z - 0.69f;
        float along = -(px * ax + py * ay + pz * az) / l;
        float rx = px + along * ax / l;
        float ry = py + along * ay / l;
        float rz = pz + along * az / l;
        float radial = (float)Math.sqrt(rx * rx + ry * ry + rz * rz);
        return Colors.mix(-5710645, -9461870, (1.0f - Mika.smooth(0.04f, 0.12f, along)) * (1.0f - Mika.smooth(0.55f, 0.68f, radial)));
    }

    private static Shape hand() {
        float[][] f = new float[][]{{-3.47f, 0.86f, 13.06f}, {-3.79f, 0.95f, 12.97f}, {-4.1f, 0.86f, 13.04f}};
        Shape[] tips = new Shape[f.length];
        for (int i = 0; i < f.length; ++i) {
            tips[i] = Shapes.capsule(f[i][0], 13.5f, f[i][1] - 0.12f, f[i][0], f[i][2], f[i][1], 0.16f);
        }
        Shape thumb = Shapes.capsule(-3.36f, 13.48f, 0.64f, -3.3f, 13.2f, 0.76f, 0.15f);
        return Shapes.union(Shapes.union(tips), thumb).paint(Paint.of(-9532));
    }

    private static void legs(Rig rig) {
        Shape hip = Shapes.sphere(-1.12f, 13.2f, -0.05f, 1.0f);
        Shape thigh = Shapes.cone(-1.18f, 12.8f, 0.0f, 1.13f, -1.1f, 7.9f, 0.1f, 0.88f);
        Shape knee = Shapes.sphere(-1.08f, 7.45f, 0.12f, 0.82f);
        Shape kneecap = Shapes.sphere(-1.07f, 7.5f, 0.48f, 0.5f);
        Strand calf = Shapes.tube(-1.08f, 7.2f, 0.1f, 0.84f, -1.1f, 5.6f, -0.15f, 0.93f, -1.13f, 3.6f, -0.02f, 0.64f, -1.15f, 2.2f, 0.0f, 0.52f);
        Shape leg = Shapes.blend(0.45f, hip, thigh, knee, kneecap, calf).paint(Paint.field(Mika::tights));
        Part right = rig.part(Bone.RIGHT_LEG).pivot(-1.2f, 13.7f, 0.0f).shape(leg).detail(0.85f).resolution(1.1f).outline(-15068908, 0.12f).occlusion(0.35f).swing(0.55f, 30.0f).joint(-1.08f, 7.45f, 0.12f, 0.8f);
        rig.part(Bone.LEFT_LEG).mirrorOf(right);
        Part shoe = rig.extra("shoe_r", Bone.RIGHT_LEG).shape(Mika.sneaker().rotate(0.0f, -7.0f, 0.0f, -1.2f, 0.0f, 0.2f)).resolution(0.75f).detail(1.0f).outline(-6659470, 0.12f).decal(Mika.laces());
        rig.extra("shoe_l", Bone.LEFT_LEG).mirrorOf(shoe);
    }

    private static int tights(float x, float y, float z) {
        double a = Math.atan2(-(x + 1.1f), z - 0.02f) - 0.35;
        float sheen = Mika.smooth(0.6f, 0.95f, (float)Math.cos(a)) * Mika.smooth(2.4f, 4.5f, y) * 0.5f;
        float dk = (float)Math.sqrt((y - 7.5f) * (y - 7.5f) + (x + 1.07f) * (x + 1.07f) * 1.4f);
        float knee = (1.0f - Mika.smooth(0.3f, 0.8f, dk)) * Mika.smooth(0.2f, 0.7f, z) * 0.55f;
        return Colors.mix(-12965330, -9808288, Math.max(sheen, knee));
    }

    private static Shape sneaker() {
        Shape sole = Shapes.blend(0.25f, Shapes.box(-1.2f, 0.3f, 0.5f, 0.9f, 0.3f, 1.84f, 0.26f), Shapes.ellipsoid(-1.2f, 0.36f, 2.05f, 0.82f, 0.34f, 0.5f)).intersect(Shapes.above(0.0f)).paint(Paint.of(-264207).band(-1011800, 0.0f, 0.46f, 0.0f, 0.0f, 1.0f, 0.0f, 0.07f, 0.05f));
        Shape toe = Shapes.ellipsoid(-1.2f, 0.9f, 1.15f, 0.74f, 0.55f, 1.12f);
        Shape heel = Shapes.ellipsoid(-1.2f, 1.28f, -0.48f, 0.75f, 0.95f, 0.84f);
        Shape upper = Shapes.blend(0.45f, toe, heel).color(-542005);
        Shape cap = Shapes.ellipsoid(-1.2f, 0.72f, 1.86f, 0.68f, 0.36f, 0.5f).color(-264207);
        Shape collar = Shapes.torus(-1.2f, 2.1f, -0.4f, 0.6f, 0.26f).color(-5839921);
        Shape tongue = Shapes.box(-1.2f, 1.75f, 0.35f, 0.42f, 0.42f, 0.3f, 0.18f).rotate(-25.0f, 0.0f, 0.0f, -1.2f, 1.75f, 0.35f).color(-207907);
        Shape tab = Shapes.capsule(-1.2f, 1.8f, -1.2f, -1.2f, 2.45f, -1.02f, 0.17f).color(-5839921);
        return Shapes.union(sole, Shapes.blend(0.12f, upper, cap), collar, tongue, tab);
    }

    private static void tail(Rig rig) {
        float[] pts = new float[]{0.0f, 13.95f, -1.45f, 0.58f, 0.15f, 13.35f, -2.7f, 0.76f, 0.6f, 13.2f, -4.1f, 0.92f, 1.6f, 14.0f, -5.15f, 1.06f, 2.6f, 15.9f, -5.5f, 1.15f, 3.2f, 18.1f, -5.15f, 1.15f, 3.35f, 19.9f, -4.35f, 1.04f, 3.0f, 21.0f, -3.45f, 0.72f, 2.55f, 21.45f, -2.85f, 0.26f};
        Curve curve = new Curve(pts);
        Shape tufts = Shapes.union(Shapes.strand(0.24f, 0.04f, 2.68f, 21.32f, -3.02f, 2.36f, 21.62f, -2.68f, 1.96f, 21.62f, -2.62f), Shapes.strand(0.24f, 0.04f, 2.68f, 21.32f, -3.02f, 2.46f, 21.6f, -2.62f, 2.42f, 21.62f, -2.2f), Shapes.strand(0.24f, 0.04f, 2.68f, 21.32f, -3.02f, 2.44f, 21.7f, -2.72f, 2.4f, 22.12f, -2.62f));
        Part tail = rig.extra("tail", Bone.BODY).pivot(0.0f, 13.95f, -1.45f).shape(Shapes.union(Shapes.tube(pts), tufts).paint(Paint.field(curve::tailColor))).detail(1.0f).resolution(1.1f).flatLight(0.3f).outline(-6142180, 0.1f).motion(Motion.tail().idle(3.0f, 14.0f, 4.0f, 0.4f).walk(4.0f, 12.0f, 3.0f, 1.0f).spring(30.0f, 4.0f).bend(1.4f));
        Canvas chevron = new Canvas(128, 96);
        chevron.fill(Shape2D.union(Shape2D.line(10.0f, 80.0f, 64.0f, 26.0f, 24.0f), Shape2D.line(64.0f, 26.0f, 118.0f, 80.0f, 24.0f)), -1999082968, 6.0f);
        for (float t : new float[]{0.3f, 0.43f, 0.56f}) {
            tail.decal(curve.chevron(chevron, t));
        }
    }

    private static float smooth(float e0, float e1, float x) {
        float t = Math.max(0.0f, Math.min(1.0f, (x - e0) / (e1 - e0)));
        return t * t * (3.0f - 2.0f * t);
    }

    private static Decal face() {
        Canvas c = new Canvas(256, 200);
        float sh = 13.356522f;
        Shape2D[] shadow = new Shape2D[FRINGE.length];
        for (int i = 0; i < FRINGE.length; ++i) {
            float[] f = FRINGE[i];
            float ex = f[7] + (f[5] - f[7]) * 0.12f;
            float ey = f[8] + (f[6] - f[8]) * 0.12f;
            shadow[i] = Shape2D.path(f[0] * 1.7f * 55.652176f, 9.0f, Mika.u(f[3]), Mika.v(f[4]) + sh, Mika.u(f[5]), Mika.v(f[6]) + sh, Mika.u(ex), Mika.v(ey) + sh);
        }
        c.fill(Shape2D.union(shadow), -1713793398, 3.0f);
        for (int s = -1; s <= 1; s += 2) {
            Mika.eye(c, 128.0f + (float)s * 53.0f, 114.0f, s);
        }
        c.fill(Shape2D.curve(125.5f, 146.5f, 128.5f, 150.5f, 131.5f, 149.0f, 2.2f, 1.4f), -1713795968);
        c.fill(Shape2D.path(3.0f, 3.0f, 117.0f, 177.0f, 122.5f, 182.5f, 128.0f, 178.5f, 133.5f, 182.5f, 139.0f, 177.0f), -7455954);
        return Decal.front(c, 0.0f, Mika.wy(27.203125f), Mika.wz(2.1f), 5.152f, 4.0249996f).depth(1.4f, 1.4f).maxAngle(72.0f).flatLight(1.0f).occlusion(0.3f);
    }

    private static void eye(Canvas c, float ex, float ey, int s) {
        Shape2D lid = Shape2D.ellipse(ex, ey + 10.0f, 31.0f, 40.0f).rotate((float)s * 12.0f, ex, ey + 10.0f);
        Shape2D white = Shape2D.ellipse(ex, ey + 2.0f, 26.0f, 31.0f).intersect(lid);
        c.fill(white, Paint2D.linear(ex, ey - 28.0f, ex, ey + 4.0f, -2571046, -776));
        Shape2D ring = Shape2D.ellipse(ex + (float)s, ey + 5.0f, 21.0f, 27.0f);
        c.fill(ring.intersect(white), Paint2D.linear(ex, ey - 22.0f, ex, ey + 32.0f, -11920890, -5748206, -1535446, -11414));
        c.fill(Shape2D.ellipse(ex + (float)s, ey + 6.0f, 9.0f, 21.0f).intersect(white), -1711287446, 4.0f);
        c.fill(ring.outline(2.5f).intersect(white), -1338371580);
        float px = ex + (float)s;
        float py = ey + 6.0f;
        c.fill(Shape2D.circle(px - 42.2f, py, 46.7f).intersect(Shape2D.circle(px + 42.2f, py, 46.7f)), -14414846);
        c.fill(Shape2D.ellipse(ex - 8.0f, ey - 8.0f, 7.5f, 9.0f).rotate(-20.0f, ex - 8.0f, ey - 8.0f), -1);
        c.fill(Shape2D.circle(ex + 9.0f, ey + 17.0f, 3.5f), -419430401);
        c.fill(Shape2D.curve(ex - (float)s * 28.0f, ey - 8.0f, ex - (float)s * 2.0f, ey - 42.0f, ex + (float)s * 30.0f, ey - 24.0f, 4.0f, 9.5f), -13887470);
        c.fill(Shape2D.taper(ex + (float)s * 24.0f, ey - 24.0f, 7.0f, ex + (float)s * 40.0f, ey - 36.0f, 1.5f), -13887470);
        c.fill(Shape2D.curve(ex + (float)s * 8.0f, ey + 34.0f, ex + (float)s * 21.0f, ey + 31.0f, ex + (float)s * 26.0f, ey + 17.0f, 1.2f, 3.0f), -866506210);
        c.fill(Shape2D.curve(ex - (float)s * 17.0f, ey - 38.0f, ex + (float)s * 2.0f, ey - 48.0f, ex + (float)s * 24.0f, ey - 38.0f, 1.5f, 2.2f), 1721786442);
        c.fill(Shape2D.curve(ex - (float)s * 16.0f, ey - 50.0f, ex + (float)s * 2.0f, ey - 58.0f, ex + (float)s * 22.0f, ey - 53.0f, 3.0f, 2.0f), -4691398);
        float bx = ex + (float)s * 10.0f;
        float by = ey + 42.0f;
        c.fill(Shape2D.ellipse(bx, by, 20.0f, 9.0f), 1627357068, 9.0f);
        for (int i = 0; i < 3; ++i) {
            c.fill(Shape2D.line(bx - 8.0f + (float)i * 7.0f, by - 4.0f, bx - 12.0f + (float)i * 7.0f, by + 4.0f, 1.6f), 1894277230);
        }
    }

    private static Decal pocketDecal() {
        int i;
        Canvas c = new Canvas(240, 144);
        int stitch = -1722777742;
        for (i = 0; i < 18; ++i) {
            c.fill(Shape2D.box(43.5f + (float)i * 9.0f, 16.5f, 2.6f, 0.75f, 0.7f), stitch);
        }
        for (i = 0; i < 23; ++i) {
            c.fill(Shape2D.box(21.0f + (float)i * 9.0f, 129.0f, 2.6f, 0.75f, 0.7f), stitch);
        }
        for (int s = -1; s <= 1; s += 2) {
            float tx = 120.0f + (float)s * 78.0f;
            float bx = 120.0f + (float)s * 102.75f;
            c.fill(Shape2D.taper(tx, 13.5f, 6.0f, bx, 132.0f, 3.75f), -1872719998);
            c.fill(Shape2D.taper(tx + (float)s * 2.25f, 13.5f, 2.25f, bx + (float)s * 2.25f, 132.0f, 1.5f), -1605733274);
            c.fill(Shape2D.taper(tx - (float)s * 4.5f, 16.5f, 1.9f, bx - (float)s * 4.5f, 129.0f, 1.5f), 0x60FFFFFF);
        }
        return Decal.front(c, 0.0f, 16.03f, 2.3f, 5.0f, 3.0f).depth(1.2f, 1.2f).maxAngle(75.0f);
    }

    private static Decal pawDecal() {
        Canvas c = new Canvas(128, 128);
        int pink = -877910;
        Shape2D pad = Shape2D.blend(8.0f, Shape2D.ellipse(64.0f, 90.0f, 27.0f, 19.0f), Shape2D.circle(48.0f, 78.0f, 15.0f), Shape2D.circle(80.0f, 78.0f, 15.0f));
        c.fill(pad, pink);
        c.fill(Shape2D.ellipse(28.0f, 52.0f, 11.0f, 14.0f).rotate(-25.0f, 28.0f, 52.0f), pink);
        c.fill(Shape2D.ellipse(51.0f, 33.0f, 11.0f, 14.5f).rotate(-8.0f, 51.0f, 33.0f), pink);
        c.fill(Shape2D.ellipse(77.0f, 33.0f, 11.0f, 14.5f).rotate(8.0f, 77.0f, 33.0f), pink);
        c.fill(Shape2D.ellipse(100.0f, 52.0f, 11.0f, 14.0f).rotate(25.0f, 100.0f, 52.0f), pink);
        return Decal.front(c, 1.2f, 20.6f, 1.6f, 1.5f, 1.5f).depth(1.5f, 1.5f).maxAngle(70.0f);
    }

    private static Canvas ribs(int count, int width) {
        Canvas c = new Canvas(width, 16);
        float step = (float)width / (float)count;
        for (int i = 0; i < count; ++i) {
            float x = (float)i * step + step * 0.5f;
            c.fill(Shape2D.box(x, 8.0f, step * 0.18f, 8.0f, 0.0f), 1078491756);
        }
        return c;
    }

    private static Decal hemRib() {
        return Decal.wrap(Mika.ribs(64, 506), 0.0f, 14.2f, -0.05f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 360.0f, 0.62f).radius(1.2f, 3.3f);
    }

    private static Decal cuffRib() {
        return Decal.wrap(Mika.ribs(24, 256), -3.77f, 14.3f, 0.57f, 0.06f, 1.65f, -0.22f, 0.0f, 0.0f, 1.0f, 360.0f, 1.2f).radius(0.6f, 1.4f);
    }

    private static Decal bellDecal(float x) {
        Canvas c = new Canvas(64, 64);
        c.fill(Shape2D.box(32.0f, 30.0f, 30.0f, 2.0f, 1.0f), -5737444);
        c.fill(Shape2D.circle(32.0f, 40.0f, 4.5f), -11915254);
        c.fill(Shape2D.line(32.0f, 40.0f, 32.0f, 60.0f, 3.5f), -11915254);
        c.fill(Shape2D.ellipse(24.0f, 20.0f, 5.0f, 3.5f), -1426063361);
        return Decal.front(c, x, 19.6f, 2.5f, 1.0f, 1.0f).depth(0.6f, 0.6f).maxAngle(70.0f);
    }

    private static Decal laces() {
        Canvas c = new Canvas(96, 128);
        int eyelet = -7710102;
        for (int i = 0; i < 4; ++i) {
            float y = 30.0f + (float)i * 22.0f;
            c.fill(Shape2D.line(24.0f, y, 72.0f, y + 14.0f, 6.0f), -1);
            c.fill(Shape2D.line(72.0f, y, 24.0f, y + 14.0f, 6.0f), -1);
            c.fill(Shape2D.circle(20.0f, y, 3.5f), eyelet);
            c.fill(Shape2D.circle(76.0f, y, 3.5f), eyelet);
        }
        c.fill(Shape2D.ellipse(36.0f, 14.0f, 11.0f, 6.0f).outline(4.0f), -1);
        c.fill(Shape2D.ellipse(60.0f, 14.0f, 11.0f, 6.0f).outline(4.0f), -1);
        c.fill(Shape2D.circle(48.0f, 16.0f, 4.0f), -1);
        Vec3 pivot = new Vec3(-1.2f, 0.0f, 0.2f);
        Vec3 at = new Vec3(-1.2f, 1.62f, 0.72f).rotate(0.0f, -7.0f, 0.0f, pivot);
        Vec3 dir = new Vec3(0.0f, 0.8f, 0.6f).rotate(0.0f, -7.0f, 0.0f);
        Vec3 up = new Vec3(0.0f, 0.6f, -0.8f).rotate(0.0f, -7.0f, 0.0f);
        return Decal.project(c, at.x(), at.y(), at.z(), dir.x(), dir.y(), dir.z(), up.x(), up.y(), up.z(), 0.95f, 1.25f).depth(0.6f, 0.6f).maxAngle(65.0f);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Curve {
        private final float[] xs;
        private final float[] ys;
        private final float[] zs;
        private final float[] len;
        private final int n;

        Curve(float[] p) {
            int count = p.length / 4 - 2;
            int per = 12;
            this.n = count * per + 1;
            this.xs = new float[this.n];
            this.ys = new float[this.n];
            this.zs = new float[this.n];
            this.len = new float[this.n];
            int k = 0;
            for (int i = 1; i <= count; ++i) {
                int prev = (i - 1) * 4;
                int cur = i * 4;
                int next = (i + 1) * 4;
                float ax = i == 1 ? p[0] : (p[prev] + p[cur]) * 0.5f;
                float ay = i == 1 ? p[1] : (p[prev + 1] + p[cur + 1]) * 0.5f;
                float az = i == 1 ? p[2] : (p[prev + 2] + p[cur + 2]) * 0.5f;
                float cx = i == count ? p[next] : (p[cur] + p[next]) * 0.5f;
                float cy = i == count ? p[next + 1] : (p[cur + 1] + p[next + 1]) * 0.5f;
                float cz = i == count ? p[next + 2] : (p[cur + 2] + p[next + 2]) * 0.5f;
                for (int j = 0; j < per || i == count && j == per; ++j) {
                    float t = (float)j / (float)per;
                    float u = 1.0f - t;
                    this.xs[k] = u * u * ax + 2.0f * u * t * p[cur] + t * t * cx;
                    this.ys[k] = u * u * ay + 2.0f * u * t * p[cur + 1] + t * t * cy;
                    this.zs[k] = u * u * az + 2.0f * u * t * p[cur + 2] + t * t * cz;
                    if (k > 0) {
                        float dx = this.xs[k] - this.xs[k - 1];
                        float dy = this.ys[k] - this.ys[k - 1];
                        float dz = this.zs[k] - this.zs[k - 1];
                        this.len[k] = this.len[k - 1] + (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                    }
                    ++k;
                }
            }
        }

        float param(float x, float y, float z) {
            int best = 0;
            float bd = Float.MAX_VALUE;
            for (int i = 0; i < this.n; ++i) {
                float dx = x - this.xs[i];
                float dy = y - this.ys[i];
                float dz = z - this.zs[i];
                float d = dx * dx + dy * dy + dz * dz;
                if (!(d < bd)) continue;
                bd = d;
                best = i;
            }
            return this.len[best] / this.len[this.n - 1];
        }

        int tailColor(float x, float y, float z) {
            float t = this.param(x, y, z);
            return Colors.mix(Colors.mix(-3647457, -681403, Mika.smooth(0.04f, 0.22f, t)), -3618, Mika.smooth(0.72f, 0.8f, t));
        }

        Decal chevron(Canvas canvas, float t) {
            int i;
            float target = t * this.len[this.n - 1];
            for (i = 1; i < this.n - 2 && this.len[i] < target; ++i) {
            }
            float tx = this.xs[i + 1] - this.xs[i - 1];
            float ty = this.ys[i + 1] - this.ys[i - 1];
            float tz = this.zs[i + 1] - this.zs[i - 1];
            return Decal.wrap(canvas, this.xs[i], this.ys[i], this.zs[i], tx, ty, tz, this.xs[i] - 1.8f, this.ys[i] - 17.6f, this.zs[i] + 3.3f, 140.0f, 0.9f).radius(0.3f, 1.6f);
        }
    }
}

