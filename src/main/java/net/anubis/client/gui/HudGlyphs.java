/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import net.anubis.client.gui.EgyptDraw;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class HudGlyphs {
    private static final float TAU = (float)Math.PI * 2;

    private HudGlyphs() {
    }

    static int ink(int argb, float alpha) {
        return EgyptDraw.col(argb, alpha);
    }

    static int vitality(float fraction) {
        float f = Math.max(0.0f, Math.min(1.0f, fraction));
        if (f >= 0.55f) {
            return EgyptDraw.mix(-871878, -12600956, (f - 0.55f) / 0.45f);
        }
        return EgyptDraw.mix(-2537681, -871878, f / 0.55f);
    }

    static float approach(float current, float target, float rate, float dt) {
        float next = current + (target - current) * (1.0f - (float)Math.exp(-rate * dt));
        return Math.abs(next - target) < 5.0E-4f ? target : next;
    }

    static float easeOutQuint(float t) {
        float inv = 1.0f - Math.max(0.0f, Math.min(1.0f, t));
        return 1.0f - inv * inv * inv * inv * inv;
    }

    static void register(ImDrawList dl, float x0, float x1, float y, float thickness, int abgr) {
        if (x1 - x0 < 2.0f) {
            return;
        }
        float mid = (x0 + x1) * 0.5f;
        int clear = abgr & 0xFFFFFF;
        dl.addRectFilledMultiColor(x0, y, mid, y + thickness, clear, abgr, abgr, clear);
        dl.addRectFilledMultiColor(mid, y, x1, y + thickness, abgr, clear, clear, abgr);
    }

    static void arc(ImDrawList dl, float cx, float cy, float r, float from, float sweep, float thickness, int abgr) {
        if (Math.abs(sweep) < 0.002f || (abgr >>> 24 & 0xFF) == 0) {
            return;
        }
        int segments = Math.max(6, Math.round(Math.abs(sweep) / ((float)Math.PI * 2) * 40.0f));
        dl.pathClear();
        dl.pathArcTo(cx, cy, r, from, from + sweep, segments);
        dl.pathStroke(abgr, 0, thickness);
    }

    static void ankh(ImDrawList dl, float cx, float cy, float h, float thickness, int abgr) {
        float top = cy - h * 0.5f;
        float bottom = cy + h * 0.5f;
        float bar = top + h * 0.42f;
        float loopW = h * 0.24f;
        dl.addBezierCubic(cx, bar, cx - loopW * 1.25f, bar - h * 0.12f, cx - loopW, top, cx, top, abgr, thickness, 10);
        dl.addBezierCubic(cx, bar, cx + loopW * 1.25f, bar - h * 0.12f, cx + loopW, top, cx, top, abgr, thickness, 10);
        dl.addLine(cx - h * 0.3f, bar, cx + h * 0.3f, bar, abgr, thickness);
        dl.addQuadFilled(cx - thickness * 0.5f, bar, cx + thickness * 0.5f, bar, cx + thickness * 1.1f, bottom, cx - thickness * 1.1f, bottom, abgr);
    }

    static void sparkle(ImDrawList dl, float cx, float cy, float r, int abgr) {
        float w = r * 0.3f;
        dl.addQuadFilled(cx, cy - r, cx + w, cy, cx, cy + r, cx - w, cy, abgr);
        dl.addQuadFilled(cx - r, cy, cx, cy - w, cx + r, cy, cx, cy + w, abgr);
    }

    static void hourglass(ImDrawList dl, float cx, float cy, float h, float thickness, int abgr) {
        float half = h * 0.5f;
        float w = h * 0.36f;
        dl.addLine(cx - w, cy - half, cx + w, cy - half, abgr, thickness);
        dl.addLine(cx - w, cy + half, cx + w, cy + half, abgr, thickness);
        dl.addTriangleFilled(cx - w * 0.8f, cy - half, cx + w * 0.8f, cy - half, cx, cy, abgr);
        dl.addTriangle(cx - w * 0.8f, cy + half, cx + w * 0.8f, cy + half, cx, cy, abgr, thickness);
    }

    static void horusEye(ImDrawList dl, float cx, float cy, float r, float thickness, int abgr) {
        float l = cx - r;
        float rr = cx + r * 0.9f;
        dl.addBezierQuadratic(l, cy, cx, cy - r * 0.78f, rr, cy, abgr, thickness, 14);
        dl.addBezierQuadratic(l, cy, cx, cy + r * 0.5f, rr, cy, abgr, thickness, 14);
        dl.addCircleFilled(cx - r * 0.05f, cy - r * 0.04f, r * 0.24f, abgr, 12);
        dl.addBezierQuadratic(l - r * 0.05f, cy - r * 0.55f, cx, cy - r * 0.98f, rr + r * 0.15f, cy - r * 0.62f, abgr, thickness, 14);
        dl.addLine(rr, cy, rr + r * 0.34f, cy - r * 0.04f, abgr, thickness);
        dl.addLine(cx - r * 0.12f, cy + r * 0.24f, cx - r * 0.34f, cy + r * 0.95f, abgr, thickness);
        dl.addBezierCubic(cx + r * 0.2f, cy + r * 0.24f, cx + r * 0.3f, cy + r * 0.8f, cx + r * 0.78f, cy + r * 0.9f, cx + r * 0.72f, cy + r * 0.6f, abgr, thickness, 12);
    }
}

