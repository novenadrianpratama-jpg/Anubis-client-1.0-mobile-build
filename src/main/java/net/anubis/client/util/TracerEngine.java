/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_408
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.anubis.client.util;

import net.anubis.client.util.FoglessLines;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class TracerEngine {
    private static final float WIDTH = 1.5f;
    private static boolean active;
    private static class_243 cameraPos;
    private static class_243 origin;
    private static FoglessLines lines;
    private static final Matrix4f viewProjection;

    private TracerEngine() {
    }

    public static void beginFrame(Matrix4fc viewRotation, Matrix4fc projection) {
        active = false;
        lines = null;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        if (mc.field_1755 != null && !(mc.field_1755 instanceof class_408)) {
            return;
        }
        class_4184 camera = mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332()) {
            return;
        }
        viewProjection.set(projection).mul(viewRotation);
        Matrix4f inverse = viewProjection.invert(new Matrix4f());
        Vector4f centre = inverse.transform(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
        Vector4f eye = inverse.transform(new Vector4f(0.0f, 0.0f, -1.0f, 0.0f));
        cameraPos = camera.method_71156();
        origin = cameraPos.method_1031((double)(centre.x / centre.w), (double)(centre.y / centre.w), (double)(centre.z / centre.w));
        class_243 eyePos = cameraPos.method_1031((double)(eye.x / eye.w), (double)(eye.y / eye.w), (double)(eye.z / eye.w));
        Vector3f forward = new Vector3f().set((float)(TracerEngine.origin.field_1352 - eyePos.field_1352), (float)(TracerEngine.origin.field_1351 - eyePos.field_1351), (float)(TracerEngine.origin.field_1350 - eyePos.field_1350)).normalize();
        double pixel = 2.0 / (double)(projection.m11() * (float)Math.max(1, mc.method_22683().method_4506()));
        lines = FoglessLines.forView(eyePos, (Vector3fc)forward, pixel);
        active = true;
    }

    public static void draw(class_243 target, int argb) {
        if (active) {
            lines.line(origin, target, argb, 1.5f);
        }
    }

    public static void stem(class_243 feet, double height, int argb) {
        if (!active) {
            return;
        }
        lines.line(feet, feet.method_1031(0.0, height, 0.0), argb, 1.5f);
    }

    public static Vector4f clip(class_243 world) {
        if (!active) {
            return null;
        }
        return viewProjection.transform(new Vector4f((float)(world.field_1352 - TracerEngine.cameraPos.field_1352), (float)(world.field_1351 - TracerEngine.cameraPos.field_1351), (float)(world.field_1350 - TracerEngine.cameraPos.field_1350), 1.0f));
    }

    public static boolean isInFront(class_4184 camera, class_243 target) {
        class_243 delta = target.method_1020(camera.method_71156());
        Vector3fc fwd = camera.method_19335();
        return delta.field_1352 * (double)fwd.x() + delta.field_1351 * (double)fwd.y() + delta.field_1350 * (double)fwd.z() > 0.05;
    }

    static {
        cameraPos = class_243.field_1353;
        origin = class_243.field_1353;
        viewProjection = new Matrix4f();
    }
}

