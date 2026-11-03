/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_124
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_5348
 *  net.minecraft.class_9848
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImGui;
import java.util.Objects;
import java.util.UUID;
import net.anubis.client.event.impl.Render2DEvent;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.util.NameFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5348;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class StaffGhost {
    private static final float LIFE_SECONDS = 1.5f;
    private static final float TITLE_SECONDS = 1.25f;
    private static final int TITLE_IN = 10;
    private static final int TITLE_STAY = 40;
    private static final int TITLE_OUT = 15;
    private static final float NOTE_SECONDS = 4.5f;
    private static final float NOTE_IN = 0.5f;
    private static final float NOTE_STAY = 2.5f;
    private static final float NOTE_OUT = 1.0f;
    private static final class_2561 NOTE = class_2561.method_43470((String)"Goodluck Anubis User, Be Careful");
    private static final int NOTE_COLOR = 0xFFFF55;
    private static final float DISTANCE = 1.49f;
    private static final float LIFT = 0.24f;
    private static final float HALF = 0.45f;
    private static final float HAT = 1.125f;
    private static final float HALF_FOV_TAN = (float)Math.tan(Math.toRadians(35.0));
    private static final float NEAR = -0.08f;
    private static final int CELLS = 8;
    private static final float[][] FACES = new float[][]{{8.0f, 8.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f}, {0.0f, 8.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 0.8f}, {16.0f, 8.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 0.8f}, {8.0f, 0.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 0.95f}, {16.0f, 0.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 0.6f}};
    private static UUID watcher;
    private static boolean hidden;
    private static String title;
    private static int stage;
    private static long startNanos;
    private static long noteNanos;
    private static boolean active;

    private StaffGhost() {
    }

    public static void watching(UUID id, String name, int others) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || name == null) {
            return;
        }
        String shown = NameFilter.apply(name);
        watcher = id;
        hidden = !shown.equals(name);
        title = others > 0 ? shown + " +" + others : shown;
        stage = 0;
        startNanos = System.nanoTime();
        noteNanos = 0L;
        active = true;
    }

    public static boolean hasWork() {
        if (active && class_310.method_1551().field_1687 == null) {
            active = false;
        }
        return active;
    }

    public static void renderNote(Render2DEvent event) {
        if (noteNanos == 0L) {
            return;
        }
        float t = (float)(System.nanoTime() - noteNanos) / 1.0E9f;
        if (t >= 4.0f) {
            noteNanos = 0L;
            return;
        }
        float fade = t < 0.5f ? t / 0.5f : Math.min(1.0f, (4.0f - t) / 1.0f);
        int alpha = Math.round(fade * 255.0f);
        if (alpha <= 8) {
            return;
        }
        class_332 g = event.graphics;
        class_327 font = class_310.method_1551().field_1772;
        g.method_51448().pushMatrix();
        g.method_51448().translate((float)g.method_51421() / 2.0f, (float)g.method_51443() / 2.0f);
        g.method_51448().scale(2.0f, 2.0f);
        int n = -font.method_27525((class_5348)NOTE) / 2;
        Objects.requireNonNull(font);
        g.method_51439(font, NOTE, n, -9 / 2, class_9848.method_61330((int)alpha, (int)0xFFFF55), true);
        g.method_51448().popMatrix();
    }

    public static void render() {
        if (!StaffGhost.hasWork()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        float seconds = StaffGhost.elapsed();
        if (stage == 0 && seconds >= 1.25f) {
            stage = 1;
            mc.field_1705.method_34001(10, 40, 15);
            mc.field_1705.method_34002((class_2561)class_2561.method_43470((String)"WATCHING YOU").method_27695(new class_124[]{class_124.field_1061, class_124.field_1067}));
            mc.field_1705.method_34004((class_2561)class_2561.method_43470((String)title).method_27695(new class_124[]{class_124.field_1061, class_124.field_1067}));
        } else if (stage == 1 && seconds >= 4.5f) {
            stage = 2;
            mc.field_1705.method_1742();
            noteNanos = System.nanoTime();
            active = false;
            return;
        }
        if (seconds >= 1.5f || watcher == null) {
            return;
        }
        int texture = HudRenderer.getInstance().staffFace(watcher, hidden);
        if (texture == 0) {
            return;
        }
        float f = seconds / 1.5f;
        float alpha = 0.05f + 0.6f * (float)Math.sin((double)f * Math.PI);
        double angle = Math.toRadians(60.0 - 150.0 * (double)f);
        float cos = (float)Math.cos(angle);
        float sin = (float)Math.sin(angle);
        float w = ImGui.getIO().getDisplaySizeX();
        float h = ImGui.getIO().getDisplaySizeY();
        float focal = h * 0.5f / HALF_FOV_TAN;
        ImDrawList dl = ImGui.getForegroundDrawList();
        StaffGhost.drawCube(dl, texture, 0.45f, 0.0f, cos, sin, w, h, focal, alpha);
        StaffGhost.drawCube(dl, texture, 0.50624996f, 32.0f, cos, sin, w, h, focal, alpha);
    }

    private static void drawCube(ImDrawList dl, int texture, float half, float uShift, float cos, float sin, float w, float h, float focal, float alpha) {
        float[] a = new float[3];
        float[] b = new float[3];
        float[] c = new float[3];
        float[] sx = new float[81];
        float[] sy = new float[sx.length];
        for (float[] face : FACES) {
            StaffGhost.corner(face, 2, half, cos, sin, a);
            StaffGhost.corner(face, 5, half, cos, sin, b);
            StaffGhost.corner(face, 8, half, cos, sin, c);
            float ux = b[0] - a[0];
            float uy = b[1] - a[1];
            float uz = b[2] - a[2];
            float vx = c[0] - a[0];
            float vy = c[1] - a[1];
            float vz = c[2] - a[2];
            float nx = uy * vz - uz * vy;
            float ny = uz * vx - ux * vz;
            float nz = ux * vy - uy * vx;
            float mx = a[0] + (ux + vx) * 0.5f;
            float my = a[1] + (uy + vy) * 0.5f;
            float mz = a[2] + (uz + vz) * 0.5f;
            if (nx * mx + ny * my + nz * mz <= 0.0f || a[2] > -0.08f || b[2] > -0.08f || c[2] > -0.08f || c[2] + uz > -0.08f) continue;
            for (int j = 0; j <= 8; ++j) {
                for (int i = 0; i <= 8; ++i) {
                    float s = (float)i / 8.0f;
                    float t = (float)j / 8.0f;
                    float x = a[0] + ux * s + vx * t;
                    float y = a[1] + uy * s + vy * t;
                    float z = a[2] + uz * s + vz * t;
                    sx[j * 9 + i] = w * 0.5f + x / -z * focal;
                    sy[j * 9 + i] = h * 0.5f - y / -z * focal;
                }
            }
            int shade = Math.round(face[11] * 255.0f);
            int col = Math.round(Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f) << 24 | shade << 16 | shade << 8 | shade;
            float u0 = (face[0] + uShift) / 64.0f;
            float v0 = face[1] / 64.0f;
            float texel = 0.015625f;
            for (int j = 0; j < 8; ++j) {
                for (int i = 0; i < 8; ++i) {
                    int p = j * 9 + i;
                    int q = p + 8 + 1;
                    float cu = u0 + (float)i * texel;
                    float cv = v0 + (float)j * texel;
                    dl.addImageQuad((long)texture, sx[p], sy[p], sx[p + 1], sy[p + 1], sx[q + 1], sy[q + 1], sx[q], sy[q], cu, cv, cu + texel, cv, cu + texel, cv + texel, cu, cv + texel, col);
                }
            }
        }
    }

    private static void corner(float[] face, int at, float half, float cos, float sin, float[] out) {
        float x = face[at] * half;
        float y = face[at + 1] * half + 0.24f;
        float z = face[at + 2] * half - 1.49f;
        out[0] = x;
        out[1] = y * cos - z * sin;
        out[2] = y * sin + z * cos;
    }

    private static float elapsed() {
        return (float)(System.nanoTime() - startNanos) / 1.0E9f;
    }
}

