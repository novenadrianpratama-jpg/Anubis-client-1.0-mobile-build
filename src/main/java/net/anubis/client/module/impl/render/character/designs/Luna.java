/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render.character.designs;

import java.util.ArrayList;
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
import net.anubis.client.module.impl.render.character.kit.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Luna
extends CharacterDesign {
    private static final int SKIN = -6958;
    private static final int SKIN_SHADE = -1331558;
    private static final int SKIN_LINE = -5737368;
    private static final int HAIR = -1912065;
    private static final int HAIR_MID = -4940554;
    private static final int HAIR_TIP = -7706904;
    private static final int HAIR_ROOT = -6519080;
    private static final int HAIR_LIGHT = -1647873;
    private static final int HAIR_LINE = -10794346;
    private static final int NAVY = -14603682;
    private static final int NAVY_DARK = -15460295;
    private static final int NAVY_LINE = -15921370;
    private static final int BODICE = -13945223;
    private static final int TIER = -12693598;
    private static final int LINING = -7571752;
    private static final int RIBBON = -4544530;
    private static final int PETTICOAT = -4611086;
    private static final int LACE = -1036;
    private static final int GOLD = -1326758;
    private static final int GOLD_DARK = -5011921;
    private static final int GOLD_PALE = -2868;
    private static final int GOLD_LINE = -8760296;
    private static final int TIGHTS = -12959880;
    private static final int TIGHTS_SHEEN = -10656082;
    private static final int BOOT = -7710146;
    private static final int BOOT_DARK = -9550799;
    private static final int BOOT_CUFF = -6395064;
    private static final int LEATHER = -8762574;
    private static final int LEATHER_DARK = -11653344;
    private static final int MOON = -4688;
    private static final float HEAD_K = 1.05f;
    private static final float NECK_Y = 24.7f;
    private static final float NECK_Z = -0.1f;
    private static final float AXIS_Z = -0.3f;
    private static final float HAT_TILT_X = -8.0f;
    private static final float HAT_TILT_Z = 4.0f;
    private static final Vec3 HAT_CENTRE = new Vec3(0.0f, 29.15f, -0.25f);
    private static final Shape2D BRIM = Shape2D.taper(2.9f, 29.25f, 0.82f, 5.72f, 29.0f, 0.5f);
    private static final float BRIM_WAVE = 0.3f;
    private static final float RIM_R = 5.86f;
    private static final Vec3 ELBOW = new Vec3(-3.72f, 18.4f, -0.05f);
    private static final float ELBOW_PITCH = -12.0f;
    private static final float ELBOW_ROLL = 3.0f;
    private static final float FACE_PPU = 41.48148f;
    private static final float FACE_LEFT = -2.3142858f;
    private static final float FACE_TOP = 28.810715f;
    private static final float[] CAPE_Y = new float[]{7.0f, 10.0f, 13.5f, 17.0f, 20.5f, 23.4f};
    private static final float[] CAPE_RX = new float[]{5.15f, 4.85f, 4.15f, 3.45f, 3.2f, 3.15f};
    private static final float[] CAPE_RZ = new float[]{4.45f, 4.2f, 3.5f, 2.3f, 1.62f, 1.3f};
    private static final float[] CAPE_CZ = new float[]{-0.8f, -0.85f, -0.85f, -0.6f, -0.45f, -0.35f};
    private static final float[] CAPE_WRAP = new float[]{104.0f, 102.0f, 100.0f, 105.0f, 118.0f, 132.0f};
    private static final int CAPE_STEPS = 96;
    private static final float[] CAPE_LUT = Luna.capeTable();
    private static final float[] CURTAIN_Y = new float[]{17.0f, 20.0f, 22.5f, 24.5f, 27.6f, 29.3f};
    private static final float[] CURTAIN_RX = new float[]{3.2f, 3.05f, 2.95f, 2.95f, 3.12f, 2.35f};
    private static final float[] CURTAIN_RZ = new float[]{3.0f, 2.65f, 2.25f, 2.55f, 3.05f, 2.35f};
    private static final float[] CURTAIN_CZ = new float[]{-0.95f, -0.95f, -0.9f, -0.65f, -0.3f, -0.3f};

    public Luna() {
        super("luna", "Luna");
    }

    @Override
    protected void define(Rig rig) {
        rig.scale(1.0f).outline(-15396047, 0.15f).occlusion(0.35f);
        Canvas band = new Canvas(128, 12);
        Luna.paintBand(band);
        Shape face = Luna.faceShape();
        Shape cap = Shapes.ellipsoid(0.0f, 27.55f, -0.35f, 2.82f, 2.94f, 2.88f).intersect(Shapes.plane(0.0f, 24.6f, -2.7f, 0.0f, -1.0f, 0.86f));
        List<Lock> bangs = Luna.bangs(face);
        Luna.head(rig, face, bangs);
        Luna.hair(rig, cap, bangs);
        Luna.hat(rig);
        Luna.body(rig);
        Luna.cape(rig);
        Luna.skirt(rig, band);
        Luna.arms(rig, band);
        Luna.legs(rig);
    }

    private static Shape faceShape() {
        Shape skull = Shapes.ellipsoid(0.0f, 27.5f, -0.3f, 2.38f, 2.5f, 2.42f);
        Shape cheeks = Shapes.ellipsoid(0.0f, 26.3f, 0.3f, 1.84f, 1.32f, 1.84f);
        Shape jaw = Shapes.cone(0.0f, 25.75f, 0.4f, 1.28f, 0.0f, 24.74f, 1.02f, 0.34f);
        return Shapes.blend(1.0f, skull, cheeks, jaw);
    }

    private static void head(Rig rig, Shape face, List<Lock> bangs) {
        rig.part(Bone.HEAD).pivot(0.0f, 24.7f, -0.1f).shape(Luna.hs(face.paint(Paint.of(-6958)))).resolution(0.9f).detail(1.5f).flatLight(1.0f).occlusion(0.2f).outline(-5737368, 0.08f).decal(Luna.faceDecal(bangs));
    }

    private static Shape hs(Shape s) {
        return s.scale(1.05f, 0.0f, 24.7f, -0.1f);
    }

    private static Vec3 hp(float x, float y, float z) {
        return new Vec3(x * 1.05f, 24.7f + (y - 24.7f) * 1.05f, -0.1f + (z - -0.1f) * 1.05f);
    }

    private static Part at(Part part, Vec3 p) {
        return part.pivot(p.x(), p.y(), p.z());
    }

    private static List<Lock> bangs(Shape face) {
        float[][] spec = new float[][]{{-50.0f, 26.5f, 0.75f, 0.3f, 0.0f}, {-24.0f, 27.6f, 0.72f, 0.25f, 0.0f}, {4.0f, 26.9f, 0.8f, -0.2f, 0.0f}, {27.0f, 27.8f, 0.7f, -0.25f, 0.0f}, {52.0f, 26.62f, 0.75f, -0.3f, 0.0f}, {-37.0f, 27.2f, 0.36f, 0.2f, 1.0f}, {40.0f, 27.02f, 0.36f, -0.2f, 1.0f}};
        ArrayList<Lock> locks = new ArrayList<Lock>();
        for (float[] s : spec) {
            float[] ys = new float[10];
            float[] rs = new float[10];
            for (int i = 0; i < 10; ++i) {
                ys[i] = s[1] - 0.3f + (29.6f - s[1] + 0.3f) * (float)i / 9.0f;
                rs[i] = Luna.surfaceR(face, s[0], ys[i]);
            }
            locks.add(s[4] > 0.0f ? new Lock(s[0], 29.4f, s[1], s[2], s[3], 0.7f, 0.75f, 0.1f, 0.3f, 0.2f, ys, rs) : new Lock(s[0], 29.4f, s[1], s[2], s[3], 0.5f, 0.45f, 0.05f, 0.4f, 0.22f, ys, rs));
        }
        return locks;
    }

    private static float interp(float[] xs, float[] vs, float x) {
        int n = xs.length;
        if (x <= xs[0]) {
            return vs[0];
        }
        if (x >= xs[n - 1]) {
            return vs[n - 1];
        }
        int i = 0;
        while (x > xs[i + 1]) {
            ++i;
        }
        return vs[i] + (vs[i + 1] - vs[i]) * (x - xs[i]) / (xs[i + 1] - xs[i]);
    }

    private static float surfaceR(Shape s, float deg, float y) {
        float d;
        double t = Math.toRadians(deg);
        float dx = (float)Math.sin(t);
        float dz = (float)Math.cos(t);
        float r = 4.5f;
        for (int i = 0; i < 80 && !(Math.abs(d = s.distance(dx * r, y, -0.3f + dz * r)) < 0.001f); ++i) {
            r -= d;
        }
        return r;
    }

    private static void hair(Rig rig, Shape cap, List<Lock> bangs) {
        int i;
        ArrayList<Shape> side = new ArrayList<Shape>();
        side.add(cap);
        for (int s = -1; s <= 1; s += 2) {
            side.add(Shapes.strand(0.74f, 0.24f, (float)s * 2.35f, 29.2f, -0.9f, (float)s * 3.04f, 27.6f, -0.72f, (float)s * 3.02f, 26.0f, -0.5f, (float)s * 2.78f, 24.5f, -0.35f).flattenFrom(1.5f, 0.0f, 26.8f, -0.6f));
        }
        Paint sidePaint = Paint.field((x, y, z) -> Luna.shine(Colors.mix(-4940554, -1912065, Luna.clamp01((y - 24.6f) / 3.6f)), x, y, z, 28.1f, 0.26f, 7, 0.3f));
        rig.extra("hair", Bone.HEAD).shape(Luna.hs(Shapes.union(side.toArray(new Shape[0])).paint(sidePaint))).resolution(1.3f).detail(0.6f).flatLight(0.4f).noOutline();
        ArrayList<Shape> fringe = new ArrayList<Shape>();
        for (Lock l : bangs) {
            fringe.add(l.shape());
        }
        Paint fringePaint = Paint.field((x, y, z) -> Luna.shine(Colors.mix(-1647873, -6519080, Luna.clamp01((y - 27.1f) / 1.9f)), x, y, z, 28.45f, 0.14f, 11, 0.9f));
        rig.extra("bangs", Bone.HEAD).shape(Luna.hs(Shapes.union(fringe.toArray(new Shape[0])).paint(fringePaint))).resolution(0.45f).detail(1.3f).flatLight(0.7f).occlusion(0.15f).outline(-10794346, 0.08f);
        Lock frame = new Lock(-72.0f, 29.4f, 24.3f, 0.62f, 0.9f, 0.0f, 0.0f, 0.0f, 0.45f, 0.3f, new float[]{24.0f, 25.0f, 26.2f, 27.6f, 28.8f, 29.6f}, new float[]{2.26f, 2.54f, 2.72f, 2.7f, 2.42f, 2.0f});
        Paint framePaint = Paint.field((x, y, z) -> y > 27.2f ? Colors.mix(-1647873, -6519080, Luna.clamp01((y - 27.2f) / 1.8f)) : Colors.mix(-4940554, -1647873, Luna.clamp01((y - 24.3f) / 2.9f)));
        rig.extra("locks", Bone.HEAD).shape(Luna.hs(frame.shape().mirrorX().paint(framePaint))).resolution(0.45f).detail(0.7f).flatLight(0.5f).occlusion(0.3f).outline(-10794346, 0.08f);
        ArrayList<Shape> under = new ArrayList<Shape>();
        ArrayList<Shape> top = new ArrayList<Shape>();
        for (i = 0; i < 5; ++i) {
            under.add(Luna.backLock(118.0f + 31.0f * (float)i, -0.22f, 18.2f + (float)(i % 2) * 0.35f, 1.25f, 0.5f, 2.6f, (float)i * 1.7f));
        }
        for (i = 0; i < 7; ++i) {
            float f = (float)i / 6.0f;
            float tipY = 16.9f + 1.4f * (float)Math.pow(Math.abs(f - 0.5f) * 2.0f, 1.5) + (float)(i % 3) * 0.3f;
            top.add(Luna.backLock(116.0f + 128.0f * f, i % 2 == 0 ? 0.14f : 0.06f, tipY, 0.66f, 0.12f, 1.7f, (float)i * 2.3f));
        }
        Shape underHair = Shapes.union(under.toArray(new Shape[0])).paint(Paint.vertical(-9152312, 18.0f, -5993238, 26.0f));
        Shape topHair = Shapes.union(top.toArray(new Shape[0])).paint(Paint.field((x, y, z) -> Luna.shine(Luna.backHair(y), x, y, z, 27.25f, 0.3f, 6, 1.1f)));
        Luna.at(rig.extra("hair_back", Bone.HEAD), Luna.hp(0.0f, 28.2f, -1.6f)).shape(Luna.hs(Shapes.union(underHair, topHair))).motion(Motion.hair().hang(0.8f).drag(18.0f)).resolution(1.15f).detail(0.85f).flatLight(0.35f).outline(-10794346, 0.1f);
        Strand ribbon = Shapes.tube(-2.3f, 29.0f, -0.2f, 0.52f, -2.8f, 27.4f, -0.05f, 0.58f, -2.95f, 25.6f, 0.25f, 0.56f, -2.8f, 23.8f, 1.0f, 0.5f, -2.66f, 22.2f, 1.7f, 0.46f, -2.62f, 20.8f, 2.02f, 0.42f).flattenFrom(1.45f, 0.0f, 22.0f, -0.5f);
        int n = 20;
        float[] helix = new float[n * 4];
        for (int k = 0; k < n; ++k) {
            float t = (float)k / ((float)n - 1.0f);
            double a = Math.PI + (double)t * Math.PI * 5.0;
            float r = 0.8f - 0.22f * t;
            helix[k * 4] = -1.82f + r * (float)Math.cos(a);
            helix[k * 4 + 1] = 20.8f - 3.3f * t;
            helix[k * 4 + 2] = 2.02f + r * (float)Math.sin(a);
            helix[k * 4 + 3] = 0.42f - 0.18f * t;
        }
        Shape lock = Shapes.union(ribbon, Shapes.tube(helix)).paint(Paint.vertical(-7706904, 17.4f, -1912065, 25.0f));
        Part curlR = Luna.at(rig.extra("curl_r", Bone.HEAD), Luna.hp(-2.35f, 28.7f, -0.15f)).shape(Luna.hs(lock)).resolution(0.9f).motion(Motion.hair().drag(4.0f).walk(3.0f, 0.0f, 3.5f, 2.0f).hang(0.7f).turn(10.0f, 0.0f).bend(1.4f)).detail(0.9f).flatLight(0.4f).outline(-10794346, 0.1f);
        rig.extra("curl_l", Bone.HEAD).mirrorOf(curlR);
    }

    private static int backHair(float y) {
        if (y > 26.2f) {
            return Colors.mix(-1912065, -6519080, Luna.clamp01((y - 26.2f) / 1.9f));
        }
        if (y > 22.0f) {
            return Colors.mix(-4940554, -1912065, Luna.clamp01((y - 22.0f) / 4.2f));
        }
        return Colors.mix(-7706904, -4940554, Luna.clamp01((y - 17.2f) / 4.8f));
    }

    private static int shine(int c, float x, float y, float z, float ringY, float width, int dashes, float phase) {
        float phi = (float)Math.atan2(x, z - -0.3f);
        float d = Math.abs(y - ringY + 0.25f * (1.0f - (float)Math.cos(phi)));
        if (d > width) {
            return c;
        }
        float gap = (float)Math.sin(phi * (float)dashes + phase) + 0.2f;
        if (gap <= 0.0f) {
            return c;
        }
        float k = (1.0f - d / width) * Math.min(1.0f, gap * 3.0f) * 0.8f;
        return Colors.mix(c, Colors.mix(c, -1, 0.5f), Math.min(1.0f, k * 1.6f));
    }

    private static Shape backLock(float deg, float lift, float tipY, float r0, float r1, float flat, float phase) {
        double a = Math.toRadians(deg);
        float sa = (float)Math.sin(a);
        float ca = (float)Math.cos(a);
        float[] ys = new float[]{29.3f, 27.6f, 25.3f, 23.0f, 20.8f, 18.9f, tipY};
        float[] pts = new float[ys.length * 3];
        for (int k = 0; k < ys.length; ++k) {
            float y = ys[k];
            float rx = Luna.spline(CURTAIN_Y, CURTAIN_RX, y) + lift;
            float rz = Luna.spline(CURTAIN_Y, CURTAIN_RZ, y) + lift;
            float cz = Luna.spline(CURTAIN_Y, CURTAIN_CZ, y);
            float wave = 0.32f * Luna.clamp01((26.5f - y) / 5.0f) * (float)Math.sin(y * 0.95f + phase);
            float flick = k == ys.length - 1 ? 0.16f : 0.0f;
            pts[k * 3] = (rx + flick) * sa + wave * ca;
            pts[k * 3 + 1] = y;
            pts[k * 3 + 2] = cz + (rz + flick) * ca - wave * sa;
        }
        return Shapes.strand(r0, r1, pts).flatten(flat, sa, 0.0f, ca);
    }

    private static void hat(Rig rig) {
        Paint felt = Paint.vertical(-15460295, 28.6f, -14603682, 31.0f);
        Shape brim = Shapes.field(Luna::brimDistance, -6.1f, 28.1f, -6.35f, 6.1f, 30.1f, 5.85f).paint(felt);
        rig.extra("brim", Bone.HEAD).shape(Luna.worn(brim)).resolution(1.2f).detail(0.65f).outline(-15921370, 0.12f);
        Shape rim = Shapes.field(Luna::rimDistance, -6.2f, 28.35f, -6.45f, 6.2f, 29.65f, 5.95f).paint(Paint.of(-1326758).grain(0.6f, 0.08f));
        Shape band = Shapes.cone(0.0f, 28.7f, -0.25f, 3.43f, 0.0f, 31.2f, -0.3f, 3.08f).intersect(Shapes.box(0.0f, 29.95f, -0.27f, 3.6f, 0.5f, 3.6f)).subtract(Shapes.cone(0.0f, 28.7f, -0.25f, 2.75f, 0.0f, 31.2f, -0.3f, 2.4f)).paint(Paint.vertical(-6650664, 29.45f, -4544530, 30.3f));
        rig.extra("hat_trim", Bone.HEAD).shape(Luna.worn(Shapes.union(rim, band))).resolution(0.95f).detail(0.35f).noOutline();
        Shape base = Shapes.cone(0.0f, 28.7f, -0.25f, 3.2f, 0.0f, 31.2f, -0.3f, 2.85f).intersect(Shapes.box(0.0f, 29.6f, -0.27f, 3.5f, 0.75f, 3.5f));
        rig.extra("hat", Bone.HEAD).shape(Luna.worn(base.paint(felt))).resolution(1.2f).detail(0.3f).outline(-15921370, 0.15f);
        rig.extra("hat_pin", Bone.HEAD).shape(Luna.worn(Luna.pin())).resolution(0.4f).detail(0.9f).outline(-8760296, 0.06f);
        Shape tip = Shapes.tube(0.0f, 30.2f, -0.29f, 2.8f, 0.0f, 32.2f, -0.42f, 2.24f, 0.0f, 34.1f, -0.74f, 1.56f, 0.04f, 35.3f, -1.42f, 1.02f, 0.14f, 36.0f, -2.45f, 0.64f, 0.3f, 36.15f, -3.65f, 0.36f).intersect(Shapes.above(30.15f)).subtract(0.25f, Shapes.ellipsoid(0.0f, 34.45f, -2.3f, 0.9f, 0.35f, 0.45f)).paint(Paint.vertical(-14603682, 30.0f, -14011792, 37.0f));
        Vec3 joint = Luna.hatPoint(0.0f, 30.4f, -0.3f);
        Part hatTip = Luna.at(rig.extra("hat_tip", rig.part(Bone.HEAD)), Luna.hp(joint.x(), joint.y(), joint.z())).shape(Luna.worn(tip)).resolution(1.25f).motion(new Motion().idle(2.0f, 0.0f, 2.0f, 0.3f).walk(6.0f, 0.0f, 4.0f, 2.0f).drag(7.0f).hang(0.25f).turn(10.0f, 0.0f).spring(38.0f, 4.5f).bend(1.7f).limit(35.0f)).detail(0.75f).outline(-15921370, 0.15f);
        Vec3 end = Luna.hatPoint(0.28f, 35.9f, -3.5f);
        Shape cord = Shapes.strand(0.08f, 0.08f, end.x(), end.y(), end.z(), end.x(), end.y() - 0.8f, end.z()).color(-5011921);
        Shape charm = Shapes.extrude(Shape2D.star(0.0f, 0.0f, 0.62f, 0.27f, 5).rotate(180.0f, 0.0f, 0.0f), 0.14f, 0.05f).rotate(0.0f, 90.0f, 0.0f).move(end.x(), end.y() - 1.3f, end.z()).paint(Paint.of(-9872).glow());
        Luna.at(rig.extra("hat_charm", hatTip), Luna.hp(end.x(), end.y(), end.z())).shape(Luna.hs(Shapes.union(cord, charm))).motion(Motion.hair().hang(0.9f).spring(60.0f, 6.0f)).resolution(0.45f).detail(1.2f).outline(-8760296, 0.05f);
    }

    private static float brimDistance(float x, float y, float z) {
        float dz = z + 0.25f;
        float rho = (float)Math.sqrt(x * x + dz * dz);
        return BRIM.distance(rho, y - Luna.wave(x, dz, rho, Luna.clamp01((rho - 2.9f) / 2.9f)));
    }

    private static float rimDistance(float x, float y, float z) {
        float dz = z + 0.25f;
        float rho = (float)Math.sqrt(x * x + dz * dz);
        float a = rho - 5.86f;
        float b = y - 29.0f - Luna.wave(x, dz, rho, 1.0f);
        return (float)Math.sqrt(a * a + b * b) - 0.31f;
    }

    private static float wave(float x, float dz, float rho, float s) {
        if (rho < 0.001f) {
            return 0.0f;
        }
        return 0.3f * s * s * (x * x - dz * dz) / (rho * rho);
    }

    private static Shape pin() {
        Shape2D moon = Shape2D.circle(0.0f, 0.0f, 0.68f).subtract(Shape2D.circle(0.34f, 0.18f, 0.52f));
        Shape2D star = Shape2D.star(0.46f, 0.14f, 0.38f, 0.17f, 5).rotate(180.0f, 0.46f, 0.14f);
        Shape badge = Shapes.union(Shapes.extrude(moon, 0.14f, 0.06f).paint(Paint.radial(-4688, -0.3f, -0.2f, 0.0f, 0.8f, -1326758).glow()), Shapes.extrude(star, 0.12f, 0.05f).move(0.0f, 0.0f, 0.04f).paint(Paint.of(-9872).glow()));
        double a = Math.toRadians(38.0);
        float r = 3.38f;
        return badge.rotate(-8.0f, 38.0f, 0.0f).move(r * (float)Math.sin(a), 29.95f, -0.25f + r * (float)Math.cos(a));
    }

    private static Shape worn(Shape s) {
        return Luna.hs(s.rotate(-8.0f, 0.0f, 4.0f, HAT_CENTRE.x(), HAT_CENTRE.y(), HAT_CENTRE.z()));
    }

    private static Vec3 hatPoint(float x, float y, float z) {
        return new Vec3(x, y, z).rotate(-8.0f, 0.0f, 4.0f, HAT_CENTRE);
    }

    private static void body(Rig rig) {
        Shape chest = Shapes.ellipsoid(0.0f, 21.35f, 0.05f, 2.2f, 1.75f, 1.4f);
        Shape shoulders = Shapes.capsule(0.0f, 22.95f, -0.15f, -2.45f, 22.4f, -0.15f, 0.72f).mirrorX();
        Shape waist = Shapes.ellipsoid(0.0f, 19.0f, 0.0f, 1.62f, 1.5f, 1.15f);
        Shape hips = Shapes.ellipsoid(0.0f, 16.8f, -0.05f, 2.2f, 1.75f, 1.5f);
        Shape torso = Shapes.blend(1.1f, chest, shoulders, waist, hips);
        Shape bodice = torso.paint(Paint.vertical(-14933168, 16.5f, -13945223, 22.5f));
        Shape neck = Shapes.capsule(0.0f, 22.9f, -0.15f, 0.0f, 25.1f, -0.05f, 0.6f).paint(Paint.vertical(-6958, 23.7f, -1331558, 24.2f).flatLight(1.0f).occlusion(0.25f));
        Shape collar = Shapes.cone(0.0f, 22.5f, -0.12f, 1.02f, 0.0f, 24.3f, -0.1f, 0.78f).intersect(Shapes.box(0.0f, 23.45f, -0.1f, 1.3f, 0.63f, 1.3f));
        Shape belt = torso.inflate(0.22f).intersect(Shapes.box(0.0f, 18.45f, 0.0f, 3.0f, 0.6f, 3.0f)).paint(Paint.vertical(-11653344, 17.9f, -8762574, 18.9f));
        rig.part(Bone.BODY).pivot(0.0f, 24.5f, 0.0f).shape(Shapes.union(bodice, neck, belt)).resolution(1.2f).detail(0.9f).occlusion(0.3f).outline(-15921370, 0.14f).decal(Luna.bodiceTrim());
        rig.extra("collar", Bone.BODY).shape(collar.paint(Paint.vertical(-15460295, 22.9f, -13945223, 24.05f))).resolution(0.6f).detail(0.35f).occlusion(0.3f).outline(-15921370, 0.08f);
        ArrayList<Shape> trim = new ArrayList<Shape>();
        float zb = Luna.surfaceZ(torso, 0.0f, 23.1f, 4.0f);
        float zc = Math.max(zb, Luna.surfaceZ(collar, 0.0f, 23.1f, 4.0f));
        trim.add(Shapes.ellipsoid(0.0f, 23.08f, zc + 0.12f, 0.3f, 0.26f, 0.2f).color(-4544530));
        trim.add(Shapes.ellipsoid(0.6f, 23.14f, zc + 0.02f, 0.5f, 0.3f, 0.18f).rotate(0.0f, -18.0f, -14.0f, 0.6f, 23.14f, zc).mirrorX().color(-4544530));
        for (int s = -1; s <= 1; s += 2) {
            float z1 = Luna.surfaceZ(torso, (float)s * 0.35f, 22.3f, 4.0f);
            float z2 = Luna.surfaceZ(torso, (float)s * 0.55f, 21.6f, 4.0f);
            trim.add(Shapes.strand(0.22f, 0.16f, (float)s * 0.12f, 22.95f, zc + 0.08f, (float)s * 0.35f, 22.3f, z1 + 0.14f, (float)s * 0.55f, 21.6f, z2 + 0.12f).flatten(1.7f, 0.0f, 0.0f, 1.0f).color(-4544530));
        }
        trim.add(Shapes.sphere(0.0f, 23.08f, zc + 0.3f, 0.17f).paint(Paint.of(-4678401).glow()));
        float zs = Luna.surfaceZ(belt, 0.0f, 18.45f, 4.0f);
        trim.add(Shapes.extrude(Shape2D.star(0.0f, 0.0f, 0.56f, 0.25f, 5).rotate(180.0f, 0.0f, 0.0f), 0.17f, 0.05f).move(0.0f, 18.45f, zs + 0.08f).color(-1326758));
        rig.extra("trim", Bone.BODY).shape(Shapes.union(trim.toArray(new Shape[0]))).resolution(0.5f).detail(0.55f).noOutline();
    }

    private static void cape(Rig rig) {
        Shape cape = Shapes.field(Luna::capeDistance, -6.0f, 6.3f, -6.2f, 6.0f, 25.4f, 1.3f).paint(Paint.field(Luna::capeColor));
        rig.extra("cape", Bone.BODY).pivot(0.0f, 23.0f, -1.0f).shape(cape).resolution(1.35f).motion(Motion.cloth().drag(12.0f).hang(0.6f)).detail(0.45f).occlusion(0.3f).noOutline().decal(Luna.capeStars()).decal(Luna.capeHem());
    }

    private static float capeAt(int k, float y) {
        float f = (y - CAPE_Y[0]) / (CAPE_Y[CAPE_Y.length - 1] - CAPE_Y[0]) * 96.0f;
        f = f < 0.0f ? 0.0f : Math.min(f, 96.0f);
        int i = Math.min((int)f, 95);
        float a = CAPE_LUT[i * 4 + k];
        return a + (CAPE_LUT[i * 4 + 4 + k] - a) * (f - (float)i);
    }

    private static float[] capeTable() {
        float[] t = new float[388];
        for (int i = 0; i <= 96; ++i) {
            float y = CAPE_Y[0] + (CAPE_Y[CAPE_Y.length - 1] - CAPE_Y[0]) * (float)i / 96.0f;
            t[i * 4] = Luna.spline(CAPE_Y, CAPE_RX, y);
            t[i * 4 + 1] = Luna.spline(CAPE_Y, CAPE_RZ, y);
            t[i * 4 + 2] = Luna.spline(CAPE_Y, CAPE_CZ, y);
            t[i * 4 + 3] = Luna.spline(CAPE_Y, CAPE_WRAP, y);
        }
        return t;
    }

    private static float capeDistance(float x, float y, float z) {
        float crz;
        float rx = Luna.capeAt(0, y);
        float rz = Luna.capeAt(1, y);
        float cz = Luna.capeAt(2, y);
        float d = Math.abs(Luna.ellipse(x, z - cz, rx, rz)) - 0.43f;
        if (d < 0.6f) {
            float ang = (float)Math.toDegrees(Math.atan2(Math.abs(x) / rx, -(z - cz) / rz));
            d = Math.max(d, (ang - Luna.capeAt(3, y)) * 0.0174533f * (rx + rz) * 0.5f);
        }
        d = Math.max(d, Math.max(y - 23.4f, 7.0f - y));
        if (y < 22.5f) {
            return d;
        }
        float cu = Luna.clamp01((y - 22.9f) / 2.3f);
        float crx = 1.45f + 0.35f * cu;
        float cd = Math.abs(Luna.ellipse(x, z + 0.3f, crx, crz = 1.3f + 0.3f * cu)) - 0.38f;
        if (cd < 0.6f) {
            float cang = (float)Math.toDegrees(Math.atan2(Math.abs(x) / crx, -(z + 0.3f) / crz));
            cd = Math.max(cd, (cang - 118.0f) * 0.0174533f * 1.5f);
        }
        cd = Math.max(cd, Math.max(y - 25.2f, 22.8f - y));
        return Math.min(d, cd);
    }

    private static int capeColor(float x, float y, float z) {
        float cu;
        float ce;
        float rx = Luna.capeAt(0, y);
        float rz = Luna.capeAt(1, y);
        float cz = Luna.capeAt(2, y);
        if (y > 22.9f && Math.abs(ce = Luna.ellipse(x, z + 0.3f, 1.45f + 0.35f * (cu = Luna.clamp01((y - 22.9f) / 2.3f)), 1.3f + 0.3f * cu)) < Math.abs(Luna.ellipse(x, z - cz, rx, rz))) {
            return ce > 0.0f ? -14603682 : -7571752;
        }
        float e = Luna.ellipse(x, z - cz, rx, rz);
        if (e < 0.0f) {
            return -7571752;
        }
        return Colors.mix(-15460295, -14603682, Luna.clamp01((y - 7.0f) / 14.0f));
    }

    private static Decal capeStars() {
        Canvas c = new Canvas(176, 176);
        long seed = 7L;
        for (int i = 0; i < 36; ++i) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float x = 7.0f + (float)((seed >>> 33) % 162L);
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float y = 8.0f + (float)((seed >>> 33) % 160L);
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float r = 2.8f + (float)((seed >>> 33) % 3L);
            boolean sparkle = (seed >>> 40) % 3L == 0L;
            Shape2D s = sparkle ? Shape2D.star(x, y, r * 1.3f, r * 0.3f, 4) : Shape2D.star(x, y, r, r * 0.46f, 5);
            c.fill(s, sparkle ? -598638 : -1326758);
        }
        c.fill(Shape2D.circle(88.0f, 92.0f, 19.0f).subtract(Shape2D.circle(97.0f, 86.0f, 16.0f)), -1326758);
        c.fill(Shape2D.star(97.0f, 88.0f, 7.5f, 3.2f, 5), -598638);
        return Decal.back(c, 0.0f, 12.5f, -3.0f, 11.0f, 11.0f).depth(4.0f, 4.0f).maxAngle(62.0f);
    }

    private static Decal capeHem() {
        Canvas c = new Canvas(192, 12);
        Luna.paintBand(c);
        return Decal.wrap(c, 0.0f, 7.27f, -0.8f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 230.0f, 0.48f).offset(0.03f);
    }

    private static void paintBand(Canvas c) {
        float h = c.height();
        float step = h * 2.0f;
        c.fillAll((x, y) -> {
            float v = y / h;
            if (v < 0.1f || v > 0.9f) {
                return 0;
            }
            if (v < 0.24f || v > 0.76f) {
                return -5011921;
            }
            float cx = ((float)Math.floor(x / step) + 0.5f) * step;
            return Math.abs(x - cx) + Math.abs(y - h * 0.5f) < h * 0.22f ? -2868 : -1326758;
        });
    }

    private static Canvas lace(int width, int scallops) {
        Canvas c = new Canvas(width, 16);
        float step = (float)width / (float)scallops;
        c.fill(Shape2D.box((float)width * 0.5f, 3.0f, (float)width * 0.5f, 3.0f, 0.0f), -1036);
        for (int i = 0; i < scallops; ++i) {
            float x = ((float)i + 0.5f) * step;
            c.fill(Shape2D.circle(x, 6.5f, step * 0.52f), -1036);
            c.erase(Shape2D.circle(x, 6.8f, step * 0.16f));
        }
        return c;
    }

    private static void skirt(Rig rig, Canvas band) {
        Canvas lace = Luna.lace(448, 34);
        for (int s = -1; s <= 1; s += 2) {
            Bone leg = s < 0 ? Bone.RIGHT_LEG : Bone.LEFT_LEG;
            Shape half = Shapes.union(Luna.tier(s, 18.5f, 12.4f, 1.75f, 3.45f, 2.5f, 11.0f, 0.24f, 0.78f, 6.0f, 16.0f).shape(-13945223, -15460295), Luna.tier(s, 13.4f, 9.1f, 3.1f, 3.95f, 1.3f, 14.0f, 0.28f, 0.74f, 14.0f, 22.0f).shape(-12693598, -14603682), Luna.tier(s, 9.55f, 8.6f, 3.7f, 4.05f, 1.0f, 20.0f, 0.16f, 0.74f, 20.0f, 24.0f).shape(-4611086, -7571752));
            rig.extra(s < 0 ? "skirt_r" : "skirt_l", Bone.BODY).pivot(0.0f, 18.2f, 0.0f).shape(half).resolution(1.15f).motion(Motion.skirt(leg).follow(leg, 0.3f, 12.0f)).detail(0.4f).occlusion(0.3f).noOutline().decal(Luna.hemBand(band, 12.58f, (float)s * 76.0f, 140.0f, 0.36f)).decal(Luna.hemBand(band, 9.28f, (float)s * 79.0f, 134.0f, 0.36f)).decal(Luna.hemBand(lace, 8.8f, (float)s * 80.0f, 132.0f, 0.34f));
        }
        Tier under = new Tier(18.2f, 8.7f, 1.6f, 2.9f, 1.6f, 16.0f, 0.12f, 0.66f, -42.0f, -42.0f, 42.0f, 42.0f);
        rig.extra("underskirt", Bone.BODY).pivot(0.0f, 18.2f, 0.2f).shape(under.shape(-4611086, -7571752)).resolution(1.1f).motion(new Motion().drag(-8.0f).walk(-5.0f, 0.0f, 0.0f, 2.0f).walkPhase(90.0f).spring(120.0f, 14.0f).bend(1.0f)).detail(0.4f).occlusion(0.3f).noOutline().decal(Luna.underskirtStars());
    }

    private static Tier tier(int s, float yTop, float yBot, float rTop, float rBot, float flare, float folds, float amp, float thick, float edgeTop, float edgeBot) {
        if (s > 0) {
            return new Tier(yTop, yBot, rTop, rBot, flare, folds, amp, thick, edgeTop, edgeBot, 188.0f, 188.0f);
        }
        return new Tier(yTop, yBot, rTop, rBot, flare, folds, amp, thick, -188.0f, -188.0f, -edgeTop, -edgeBot);
    }

    private static Decal hemBand(Canvas c, float y, float centreDeg, float degrees, float height) {
        double a = Math.toRadians(centreDeg);
        return Decal.wrap(c, 0.0f, y, 0.0f, 0.0f, 1.0f, 0.0f, (float)Math.sin(a), 0.0f, (float)Math.cos(a), degrees, height).offset(0.03f);
    }

    private static Decal underskirtStars() {
        float x;
        int i;
        Canvas c = new Canvas(192, 288);
        long seed = 3L;
        for (i = 0; i < 20; ++i) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            x = 12.0f + (float)((seed >>> 33) % 168L);
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float y = 88.0f + (float)((seed >>> 33) % 170L);
            c.fill(Shape2D.star(x, y, 5.4f, 1.7f, 4), -1326758);
        }
        for (i = 0; i < 18; ++i) {
            x = ((float)i + 0.5f) * 192.0f / 18.0f;
            c.fill(Shape2D.circle(x, 278.0f, 6.2f), -1036);
            c.erase(Shape2D.circle(x, 278.5f, 1.9f));
        }
        c.fill(Shape2D.box(96.0f, 272.0f, 96.0f, 4.0f, 0.0f), -1036);
        return Decal.front(c, 0.0f, 13.5f, 3.0f, 6.4f, 9.6f).depth(3.5f, 3.0f).maxAngle(66.0f);
    }

    private static Decal bodiceTrim() {
        Canvas c = new Canvas(64, 96);
        for (int s = -1; s <= 1; s += 2) {
            c.fill(Shape2D.path(2.0f, 2.0f, 32.0f + (float)s * 10.5f, 2.0f, 32.0f + (float)s * 9.0f, 48.0f, 32.0f + (float)s * 9.8f, 94.0f), -1326758);
        }
        for (int i = 0; i < 4; ++i) {
            float y = 13.0f + (float)i * 21.0f;
            c.fill(Shape2D.circle(32.0f, y, 5.0f), -5011921);
            c.fill(Shape2D.circle(32.0f, y, 4.0f), -1326758);
            c.fill(Shape2D.circle(30.8f, y - 1.3f, 1.5f), -2868);
        }
        return Decal.front(c, 0.0f, 20.6f, 3.0f, 2.0f, 3.0f).depth(2.5f, 2.0f).maxAngle(70.0f);
    }

    private static Shape fore(Shape s, int side) {
        return s.rotate(-12.0f, 0.0f, (float)side * 3.0f, (float)side * ELBOW.x(), ELBOW.y(), ELBOW.z());
    }

    private static Vec3 fore(float x, float y, float z) {
        return new Vec3(x, y, z).rotate(-12.0f, 0.0f, 3.0f, ELBOW);
    }

    private static void arms(Rig rig, Canvas band) {
        Paint skin = Paint.of(-6958).flatLight(1.0f).occlusion(0.25f);
        Shape puff = Shapes.ellipsoid(-3.5f, 22.0f, -0.1f, 0.92f, 1.0f, 0.9f);
        Shape upper = Shapes.cone(-3.55f, 21.5f, -0.1f, 0.66f, -3.72f, 18.45f, -0.05f, 0.55f);
        Shape elbow = Shapes.sphere(-3.72f, 18.4f, -0.05f, 0.55f);
        Shape lower = Luna.fore(Shapes.cone(-3.72f, 18.3f, -0.05f, 0.54f, -3.84f, 15.6f, 0.12f, 0.5f), 1);
        Shape sleeve = Shapes.blend(0.35f, puff, upper, elbow, lower).paint(Paint.vertical(-14867372, 15.0f, -13945223, 22.0f));
        Shape bell = Luna.fore(Shapes.cone(-3.84f, 16.1f, 0.12f, 0.52f, -3.92f, 14.3f, 0.2f, 1.02f).intersect(Shapes.above(14.3f)), 1).color(-13945223);
        Shape hollow = Luna.fore(Shapes.cone(-3.84f, 15.7f, 0.12f, 0.2f, -3.93f, 14.0f, 0.2f, 0.5f), 1).color(-7571752);
        Shape arm = Shapes.union(sleeve, Shapes.subtract(bell, hollow));
        Vec3 grip = Luna.fore(-4.0f, 13.3f, 0.42f);
        Vec3 cuff = Luna.fore(-3.91f, 14.52f, 0.2f);
        Vec3 axis = new Vec3(-0.04f, 1.0f, 0.05f).rotate(-12.0f, 0.0f, 3.0f);
        Vec3 front = new Vec3(0.0f, 0.0f, 1.0f).rotate(-12.0f, 0.0f, 3.0f);
        Part right = rig.part(Bone.RIGHT_ARM).pivot(-3.5f, 22.3f, -0.1f).grip(grip.x(), grip.y(), grip.z()).shape(arm).resolution(0.95f).detail(0.75f).occlusion(0.3f).outline(-15921370, 0.13f).swing(0.8f).joint(ELBOW.x(), ELBOW.y(), ELBOW.z(), 0.55f).decal(Decal.wrap(band, cuff.x(), cuff.y(), cuff.z(), axis.x(), axis.y(), axis.z(), front.x(), front.y(), front.z(), 360.0f, 0.36f).offset(0.03f));
        rig.part(Bone.LEFT_ARM).mirrorOf(right);
        ArrayList<Shape> hand = new ArrayList<Shape>();
        hand.add(Shapes.cone(3.92f, 14.6f, 0.22f, 0.33f, 3.97f, 13.8f, 0.28f, 0.33f));
        hand.add(Shapes.ellipsoid(4.0f, 13.25f, 0.32f, 0.27f, 0.6f, 0.52f));
        float[] curl = new float[]{-24.0f, -19.0f, -14.0f, -8.0f};
        for (int f = 0; f < 4; ++f) {
            float z = 0.02f + (float)f * 0.21f;
            float len = f == 0 || f == 3 ? 0.62f : 0.8f;
            hand.add(Shapes.capsule(4.02f, 12.8f, z, 3.94f, 12.8f - len, z + 0.1f, 0.12f).rotate(0.0f, 0.0f, curl[f], 4.02f, 12.85f, z));
        }
        hand.add(Shapes.capsule(3.86f, 13.35f, 0.76f, 3.74f, 12.75f, 0.96f, 0.14f));
        Part left = rig.extra("hand_l", Bone.LEFT_ARM).shape(Luna.fore(Shapes.blend(0.18f, hand.toArray(new Shape[0])).paint(skin), -1)).resolution(0.5f).detail(0.6f).outline(-5737368, 0.08f);
        rig.extra("hand_r", Bone.RIGHT_ARM).mirrorOf(left);
    }

    private static void legs(Rig rig) {
        Shape thigh = Shapes.cone(-1.3f, 15.3f, 0.0f, 1.18f, -1.1f, 8.6f, 0.08f, 0.82f);
        Shape knee = Shapes.sphere(-1.08f, 8.25f, 0.14f, 0.78f);
        Shape kneecap = Shapes.sphere(-1.08f, 8.3f, 0.45f, 0.55f);
        Shape shin = Shapes.cone(-1.08f, 8.0f, 0.1f, 0.8f, -1.14f, 2.1f, -0.1f, 0.5f);
        Shape calf = Shapes.ellipsoid(-1.12f, 5.9f, -0.3f, 0.85f, 1.5f, 0.9f);
        Shape leg = Shapes.blend(0.45f, thigh, knee, kneecap, shin, calf).paint(Paint.field(Luna::tights));
        Shape shaft = Shapes.cone(-1.12f, 4.9f, -0.08f, 0.8f, -1.1f, 1.5f, -0.1f, 0.62f).intersect(Shapes.below(5.0f));
        Shape toe = Shapes.ellipsoid(-1.1f, 0.48f, 1.0f, 0.6f, 0.48f, 0.82f);
        Shape instep = Shapes.ellipsoid(-1.1f, 1.0f, 0.25f, 0.64f, 0.62f, 0.9f);
        Shape boot = Shapes.blend(0.35f, shaft, toe, instep).paint(Paint.vertical(-9550799, 0.5f, -7710146, 4.6f));
        Shape heel = Shapes.box(-1.1f, 0.42f, -0.5f, 0.4f, 0.42f, 0.36f, 0.14f).color(-12704232);
        Shape sole = Shapes.box(-1.1f, 0.12f, 0.97f, 0.64f, 0.12f, 0.8f, 0.1f).color(-12966888);
        Shape strap = Shapes.torus(-1.1f, 1.75f, -0.05f, 0.62f, 0.26f).color(-11653344);
        Shape buckle = Shapes.ellipsoid(-1.72f, 1.75f, 0.57f, 0.24f, 0.3f, 0.14f).rotate(0.0f, -45.0f, 0.0f, -1.72f, 1.75f, 0.57f).color(-1326758);
        Shape foot = Shapes.union(boot, heel, sole, strap, buckle).rotate(0.0f, -7.0f, 0.0f, -1.12f, 0.0f, -0.1f);
        Shape all = Shapes.union(leg, foot).intersect(Shapes.above(0.0f));
        Part right = rig.part(Bone.RIGHT_LEG).pivot(-1.3f, 15.3f, 0.0f).shape(all).resolution(1.2f).detail(0.75f).occlusion(0.3f).outline(-14411226, 0.13f).swing(0.55f, 30.0f).joint(-1.08f, 8.25f, 0.14f, 0.8f);
        rig.part(Bone.LEFT_LEG).mirrorOf(right);
        Shape cuff = Shapes.cylinder(-1.12f, 4.45f, -0.08f, -1.12f, 5.18f, -0.08f, 0.94f, 0.18f).paint(Paint.vertical(-7710146, 4.5f, -6395064, 5.1f));
        Part cuffR = rig.extra("cuff_r", Bone.RIGHT_LEG).shape(cuff).resolution(0.7f).detail(0.9f).occlusion(0.3f).outline(-12966888, 0.08f);
        rig.extra("cuff_l", Bone.LEFT_LEG).mirrorOf(cuffR);
    }

    private static int tights(float x, float y, float z) {
        float cx = -1.1f - 0.2f * Luna.clamp01((y - 8.0f) / 7.0f);
        float cz = 0.05f;
        float a = (float)Math.atan2(x - cx, z - cz);
        float sheen = (float)Math.exp(-(a + 0.35f) * (a + 0.35f) / 0.16f);
        int base = Colors.mix(Colors.shade(-12959880, 0.82f), -12959880, Luna.clamp01((y - 3.0f) / 6.0f));
        return Colors.mix(base, -10656082, 0.55f * sheen);
    }

    private static Decal faceDecal(List<Lock> bangs) {
        Canvas canvas = new Canvas(192, 158);
        Shape2D shadow = null;
        for (Lock l : bangs) {
            Shape2D s = l.shadow(0.28f).inflate(3.7333333f);
            shadow = shadow == null ? s : shadow.union(s);
        }
        canvas.fill(shadow, Colors.withAlpha(-1199194, 0.55f), 4.0f);
        canvas.draw(Luna.features(), 12, 44);
        Vec3 c = Luna.hp(0.0f, 26.90625f, 3.0f);
        return Decal.front(canvas, c.x(), c.y(), c.z(), 4.8599997f, 3.9993749f).depth(3.0f, 3.0f).maxAngle(74.0f).occlusion(0.3f).flatLight(1.0f);
    }

    private static Canvas features() {
        float k = 0.5833333f;
        Canvas face = new Canvas(168, 114);
        Canvas eye = new Canvas(64, 80);
        Luna.eye(new Pen(eye, k, 60.0f, 96.0f), 122.0f, 181.0f, -1);
        face.draw(eye, 7, 0);
        face.draw(eye.mirrored(), 97, 0);
        Pen c = new Pen(face, k, 48.0f, 96.0f);
        for (int s = -1; s <= 1; s += 2) {
            float ex = 192.0f + (float)s * 70.0f;
            float ey = 181.0f;
            c.fill(Shape2D.star(ex - 10.0f, ey - 13.0f, 14.0f, 3.8f, 4), -1);
            c.fill(Shape2D.circle(ex - 10.0f, ey - 13.0f, 4.8f), -1);
            c.fill(Shape2D.star(ex + 12.0f, ey + 17.0f, 7.0f, 2.1f, 4), -285212673);
            c.fill(Shape2D.circle(ex + 10.0f, ey - 2.0f, 2.3f), -1191182337);
            c.fill(Shape2D.ellipse(192.0f + (float)s * 96.0f, 226.0f, 27.0f, 12.0f), 1895793815, 12.0f);
        }
        c.fill(Shape2D.curve(191.0f, 232.0f, 195.0f, 238.0f, 190.0f, 242.0f, 2.4f, 1.6f), -2135459206);
        c.fill(Shape2D.curve(180.0f, 275.0f, 192.0f, 284.0f, 204.0f, 275.0f, 3.2f, 3.2f), -7060390);
        c.fill(Shape2D.curve(188.0f, 285.0f, 192.0f, 287.0f, 196.0f, 285.0f, 1.8f, 1.8f), 1438670960);
        return face;
    }

    private static void eye(Pen c, float ex, float ey, int s) {
        Shape2D white = Shape2D.ellipse(ex, ey + 2.0f, 37.0f, 46.0f);
        c.fill(white, Paint2D.linear(ex, ey - 44.0f, ex, ey - 8.0f, -3160852, -1));
        Paint2D base = Paint2D.linear(ex, ey - 36.0f, ex, ey + 44.0f, -14282676, -11194192, -6656782, -2044673);
        c.fill(Shape2D.ellipse(ex, ey + 4.0f, 29.0f, 41.0f).intersect(white), (x, y) -> {
            float lx = (x - ex) / 32.0f;
            float ly = (y - ey + 28.0f) / 19.0f;
            float gx = (x - ex) / 18.0f;
            float gy = (y - ey - 27.0f) / 10.0f;
            float rx = (x - ex) / 29.0f;
            float ry = (y - ey - 4.0f) / 41.0f;
            float lid = Luna.clamp01((1.0f - (float)Math.sqrt(lx * lx + ly * ly)) * 8.0f) * 0.47f;
            float glow = Luna.clamp01((1.0f - (float)Math.sqrt(gx * gx + gy * gy)) * 5.0f) * 0.5f;
            float rim = Luna.clamp01(((float)Math.sqrt(rx * rx + ry * ry) - 0.88f) / 0.06f) * 0.82f;
            int col = Colors.mix(Colors.mix(base.argb(x, y), -14676416, lid), -726785, glow);
            return Colors.mix(col, -14479041, rim);
        });
        c.fill(Shape2D.ellipse(ex, ey + 5.0f, 12.0f, 17.0f), -15136210);
        c.fill(Shape2D.path(6.0f, 12.0f, ex - (float)s * 37.0f, ey - 6.0f, ex - (float)s * 24.0f, ey - 36.0f, ex + (float)s * 2.0f, ey - 48.0f, ex + (float)s * 25.0f, ey - 42.0f, ex + (float)s * 39.0f, ey - 26.0f, ex + (float)s * 50.0f, ey - 32.0f), -14938064);
        c.fill(Shape2D.curve(ex + (float)s * 40.0f, ey - 28.0f, ex + (float)s * 52.0f, ey - 40.0f, ex + (float)s * 58.0f, ey - 50.0f, 5.0f, 1.0f), -14938064);
        c.fill(Shape2D.curve(ex + (float)s * 30.0f, ey - 38.0f, ex + (float)s * 38.0f, ey - 50.0f, ex + (float)s * 40.0f, ey - 58.0f, 3.5f, 1.0f), -14938064);
        c.fill(Shape2D.curve(ex + (float)s * 6.0f, ey + 48.0f, ex + (float)s * 26.0f, ey + 44.0f, ex + (float)s * 35.0f, ey + 28.0f, 1.5f, 4.0f), -1069931696);
        c.fill(Shape2D.path(2.0f, 2.0f, ex - (float)s * 18.0f, ey - 55.0f, ex + (float)s * 4.0f, ey - 60.0f, ex + (float)s * 27.0f, ey - 53.0f), 1883909461);
        c.fill(Shape2D.path(5.5f, 2.0f, ex - (float)s * 28.0f, ey - 70.0f, ex + (float)s * 2.0f, ey - 82.0f, ex + (float)s * 36.0f, ey - 76.0f), -7703368);
    }

    private static float ellipse(float x, float z, float rx, float rz) {
        float px = x / rx;
        float pz = z / rz;
        float k0 = (float)Math.sqrt(px * px + pz * pz);
        float k1 = (float)Math.sqrt(px * px / (rx * rx) + pz * pz / (rz * rz));
        if (k1 < 1.0E-6f) {
            return -Math.min(rx, rz);
        }
        return k0 * (k0 - 1.0f) / k1;
    }

    private static float spline(float[] ks, float[] vs, float y) {
        int i;
        int n = ks.length;
        if (y <= ks[0]) {
            return vs[0];
        }
        if (y >= ks[n - 1]) {
            return vs[n - 1];
        }
        for (i = 0; i < n - 2 && y > ks[i + 1]; ++i) {
        }
        float t = (y - ks[i]) / (ks[i + 1] - ks[i]);
        float h = ks[i + 1] - ks[i];
        float m0 = i == 0 ? (vs[1] - vs[0]) / (ks[1] - ks[0]) : (vs[i + 1] - vs[i - 1]) / (ks[i + 1] - ks[i - 1]);
        float m1 = i + 2 >= n ? (vs[n - 1] - vs[n - 2]) / (ks[n - 1] - ks[n - 2]) : (vs[i + 2] - vs[i]) / (ks[i + 2] - ks[i]);
        float t2 = t * t;
        float t3 = t2 * t;
        return (2.0f * t3 - 3.0f * t2 + 1.0f) * vs[i] + (t3 - 2.0f * t2 + t) * h * m0 + (-2.0f * t3 + 3.0f * t2) * vs[i + 1] + (t3 - t2) * h * m1;
    }

    private static float surfaceZ(Shape s, float x, float y, float start) {
        float d;
        float z = start;
        for (int i = 0; i < 80 && !(Math.abs(d = s.distance(x, y, z)) < 0.001f); ++i) {
            z -= d;
        }
        return z;
    }

    private static float clamp01(float v) {
        return v < 0.0f ? 0.0f : Math.min(v, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Lock {
        private static final float REF = 2.6f;
        private final float rad;
        private final float top;
        private final float tip;
        private final float width;
        private final float hook;
        private final float g0;
        private final float lift;
        private final float g1;
        private final float t0;
        private final float t1;
        private final float[] ys;
        private final float[] rs;
        private final float[] poly;
        private final Shape2D outline;

        Lock(float deg, float top, float tip, float width, float hook, float g0, float lift, float g1, float t0, float t1, float[] ys, float[] rs) {
            float s;
            int i;
            this.rad = (float)Math.toRadians(deg);
            this.top = top;
            this.tip = tip;
            this.width = width;
            this.hook = hook;
            this.g0 = g0;
            this.lift = lift;
            this.g1 = g1;
            this.t0 = t0;
            this.t1 = t1;
            this.ys = ys;
            this.rs = rs;
            int n = 7;
            float so = hook > 0.0f ? -1.0f : 1.0f;
            this.poly = new float[(2 * n + 3) * 2];
            int k = 0;
            this.poly[k++] = so * width;
            this.poly[k++] = top + 0.5f;
            for (i = 0; i <= n; ++i) {
                s = 1.0f - (float)Math.pow(1.0f - (float)i / (float)n, 1.4f);
                this.poly[k++] = hook * s * s + so * width * (float)Math.pow(1.0f - s, 0.8f);
                this.poly[k++] = top + (tip - top) * s;
            }
            for (i = n - 1; i >= 0; --i) {
                s = 1.0f - (float)Math.pow(1.0f - (float)i / (float)n, 1.4f);
                this.poly[k++] = hook * s * s - so * width * (float)Math.pow(1.0f - s, 1.25);
                this.poly[k++] = top + (tip - top) * s;
            }
            this.poly[k++] = -so * width;
            this.poly[k] = top + 0.5f;
            this.outline = Shape2D.polygon(this.poly);
        }

        private float gap(float s) {
            float r = 1.0f - s;
            return this.g0 * r * r + 2.0f * this.lift * s * r + this.g1 * s * s;
        }

        float distance(float x, float y, float z) {
            float t;
            float dz = z - -0.3f;
            float rho = (float)Math.sqrt(x * x + dz * dz);
            float s = Luna.clamp01((this.top - y) / (this.top - this.tip));
            float in = Luna.interp(this.ys, this.rs, y) + this.gap(s);
            float dw = Math.max(in - rho, rho - in - (t = this.t0 + (this.t1 - this.t0) * s));
            if (dw > 0.8f) {
                return dw;
            }
            float u = 2.6f * ((float)Math.atan2(x, dz) - this.rad);
            float d2 = this.outline.distance(u, y);
            if (d2 > 0.8f) {
                return d2;
            }
            float q = Math.min(1.0f, Math.abs(u - this.hook * s * s) / (this.width * (1.0f - s) + 0.05f));
            dw = Math.max(in - rho, rho - in - t * (1.0f - 0.35f * q * q));
            float h = Luna.clamp01(0.5f - 0.5f * (dw - d2) / 0.07f);
            return dw + (d2 - dw) * h + 0.07f * h * (1.0f - h);
        }

        Shape shape() {
            float u0 = Float.MAX_VALUE;
            float u1 = -3.4028235E38f;
            float r0 = Float.MAX_VALUE;
            float r1 = 0.0f;
            for (int i = 0; i < this.poly.length; i += 2) {
                u0 = Math.min(u0, this.poly[i]);
                u1 = Math.max(u1, this.poly[i]);
            }
            for (float r : this.rs) {
                r0 = Math.min(r0, r);
                r1 = Math.max(r1, r);
            }
            r0 = Math.max(0.2f, r0 + Math.min(this.g0, this.g1) - 0.15f);
            r1 += Math.max(this.g0, Math.max(this.lift, this.g1)) + this.t0 + 0.15f;
            float x0 = Float.MAX_VALUE;
            float x1 = -3.4028235E38f;
            float z0 = Float.MAX_VALUE;
            float z1 = -3.4028235E38f;
            for (int i = 0; i <= 12; ++i) {
                double a = this.rad + (u0 + (u1 - u0) * (float)i / 12.0f) / 2.6f;
                for (float r : new float[]{r0, r1}) {
                    x0 = Math.min(x0, r * (float)Math.sin(a));
                    x1 = Math.max(x1, r * (float)Math.sin(a));
                    z0 = Math.min(z0, -0.3f + r * (float)Math.cos(a));
                    z1 = Math.max(z1, -0.3f + r * (float)Math.cos(a));
                }
            }
            return Shapes.field(this::distance, x0 - 0.1f, this.tip - 0.15f, z0 - 0.1f, x1 + 0.1f, this.top + 0.6f, z1 + 0.1f);
        }

        Shape2D shadow(float drop) {
            float[] p = (float[])this.poly.clone();
            for (int i = 0; i < p.length; i += 2) {
                float y = p[i + 1];
                float r = Luna.interp(this.ys, this.rs, y) + this.gap(Luna.clamp01((this.top - y) / (this.top - this.tip))) + this.t0 * 0.5f;
                p[i] = (r * (float)Math.sin(this.rad + p[i] / 2.6f) - -2.3142858f) * 41.48148f;
                p[i + 1] = (28.810715f - y + drop) * 41.48148f;
            }
            return Shape2D.polygon(p);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Tier {
        private static final int STEPS = 48;
        private final float yTop;
        private final float yBot;
        private final float rTop;
        private final float rBot;
        private final float folds;
        private final float amp;
        private final float half;
        private final float a0Top;
        private final float a0Bot;
        private final float a1Top;
        private final float a1Bot;
        private final float[] shape = new float[98];

        Tier(float yTop, float yBot, float rTop, float rBot, float flare, float folds, float amp, float thick, float a0Top, float a0Bot, float a1Top, float a1Bot) {
            this.yTop = yTop;
            this.yBot = yBot;
            this.rTop = rTop;
            this.rBot = rBot;
            this.folds = folds;
            this.amp = amp;
            this.half = thick * 0.5f;
            this.a0Top = a0Top;
            this.a0Bot = a0Bot;
            this.a1Top = a1Top;
            this.a1Bot = a1Bot;
            for (int i = 0; i <= 48; ++i) {
                float u = Math.min((float)i / 48.0f, 0.999f);
                this.shape[i * 2] = 1.0f - (float)Math.pow(1.0f - u, flare);
                this.shape[i * 2 + 1] = flare * (float)Math.pow(1.0f - u, flare - 1.0f);
            }
        }

        private float u(float y) {
            return Luna.clamp01((this.yTop - y) / (this.yTop - this.yBot));
        }

        private float table(int k, float u) {
            float f = u * 48.0f;
            int i = Math.min((int)f, 47);
            float a = this.shape[i * 2 + k];
            return a + (this.shape[i * 2 + 2 + k] - a) * (f - (float)i);
        }

        float radius(float x, float y, float z) {
            float u = this.u(y);
            return this.rTop + (this.rBot - this.rTop) * this.table(0, u) + this.amp * u * (float)Math.cos((double)this.folds * Math.atan2(x, z));
        }

        float distance(float x, float y, float z) {
            double dt;
            float base;
            float u = this.u(y);
            float h = this.yTop - this.yBot;
            float rho = (float)Math.sqrt(x * x + z * z);
            float far = Math.abs(rho - (base = this.rTop + (this.rBot - this.rTop) * this.table(0, u))) - this.half - this.amp;
            if (far > 0.6f) {
                return Math.max(far * 0.7f, Math.max(y - this.yTop, this.yBot - y));
            }
            double th = Math.atan2(x, z);
            float c = (float)Math.cos((double)this.folds * th);
            float s = (float)Math.sin((double)this.folds * th);
            float r = base + this.amp * u * c;
            float dry = ((this.rBot - this.rTop) * this.table(1, u) + this.amp * c) / h;
            float drt = this.amp * u * this.folds * s / Math.max(rho, 0.5f);
            float d = (Math.abs(rho - r) - this.half) / (float)Math.sqrt(1.0f + dry * dry + drt * drt);
            d = Math.max(d, Math.max(y - this.yTop, this.yBot - y));
            double a0 = Math.toRadians(this.a0Top + (this.a0Bot - this.a0Top) * u);
            double a1 = Math.toRadians(this.a1Top + (this.a1Bot - this.a1Top) * u);
            for (dt = th - (a0 + a1) * 0.5; dt > Math.PI; dt -= Math.PI * 2) {
            }
            while (dt < -Math.PI) {
                dt += Math.PI * 2;
            }
            return Math.max(d, (float)(Math.abs(dt) - (a1 - a0) * 0.5) * rho);
        }

        Shape shape(int outer, int inner) {
            float reach = Math.max(this.rTop, this.rBot) + this.amp + this.half + 0.2f;
            float lo = Math.min(this.a0Top, this.a0Bot);
            float hi = Math.max(this.a1Top, this.a1Bot);
            float x0 = 0.0f;
            float x1 = 0.0f;
            float z0 = 0.0f;
            float z1 = 0.0f;
            for (int i = 0; i <= 64; ++i) {
                double a = Math.toRadians(lo + (hi - lo) * (float)i / 64.0f);
                x0 = Math.min(x0, (float)Math.sin(a) * reach);
                x1 = Math.max(x1, (float)Math.sin(a) * reach);
                z0 = Math.min(z0, (float)Math.cos(a) * reach);
                z1 = Math.max(z1, (float)Math.cos(a) * reach);
            }
            Shape s = Shapes.field(this::distance, x0 - 0.3f, this.yBot - 0.2f, z0 - 0.3f, x1 + 0.3f, this.yTop + 0.2f, z1 + 0.3f);
            return s.paint(Paint.field((x, y, z) -> {
                float rho = (float)Math.sqrt(x * x + z * z);
                if (rho < this.radius(x, y, z)) {
                    return inner;
                }
                return Colors.mix(Colors.shade(outer, 0.8f), outer, Luna.clamp01((y - this.yBot) / (this.yTop - this.yBot) * 1.6f));
            }));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Pen {
        private final Canvas canvas;
        private final float k;
        private final float ox;
        private final float oy;

        Pen(Canvas canvas, float k, float ox, float oy) {
            this.canvas = canvas;
            this.k = k;
            this.ox = ox;
            this.oy = oy;
        }

        private Shape2D map(Shape2D shape) {
            float f = this.k / (this.k - 1.0f);
            return shape.scale(this.k, this.ox * f, this.oy * f);
        }

        void fill(Shape2D shape, int argb) {
            this.canvas.fill(this.map(shape), argb);
        }

        void fill(Shape2D shape, int argb, float feather) {
            this.canvas.fill(this.map(shape), argb, feather * this.k);
        }

        void fill(Shape2D shape, Paint2D paint) {
            this.canvas.fill(this.map(shape), (x, y) -> paint.argb(x / this.k + this.ox, y / this.k + this.oy));
        }
    }
}

