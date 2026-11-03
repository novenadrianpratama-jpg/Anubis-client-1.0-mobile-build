/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import imgui.ImGui;
import java.util.Random;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.ThemeImages;
import net.anubis.client.gui.mainmenu.MenuTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class DuskScene {
    public static final float HORIZON = 0.72f;
    private static final String SKY_IMAGE = "/assets/anubis/textures/gui/menu/bg_sky.jpg";
    private static final String SUN_IMAGE = "/assets/anubis/textures/gui/menu/bg_sun.png";
    private static final String MID_IMAGE = "/assets/anubis/textures/gui/menu/bg_mid.png";
    private static final String NEAR_IMAGE = "/assets/anubis/textures/gui/menu/bg_near.png";
    private static final float CANVAS_W = 2880.0f;
    private static final float CANVAS_H = 1620.0f;
    private static final float SAFE_W = 2560.0f;
    private static final float SAFE_H = 1440.0f;
    private static final float CANVAS_HORIZON = 1127.0f;
    private static final float SUN_DISK = 0.3115f;
    private static final float SKY_DRIFT = 4.0f;
    private static final float MID_DRIFT = 14.0f;
    private static final float NEAR_DRIFT = 32.0f;
    private static final int SKY_NIGHT = -16251124;
    private static final int SKY_DUSK = -15069431;
    private static final int SKY_AMBER = -11194353;
    private static final float SKY_TURN = 0.45f;
    private static final int HORIZON_GLOW = -2062038;
    private static final float HORIZON_GLOW_PEAK = 0.2f;
    private static final float HORIZON_GLOW_REACH = 0.55f;
    private static final int GLOW_RINGS = 12;
    private static final int GLOW_SEGMENTS = 72;
    private static final float[] GLOW_COS;
    private static final float[] GLOW_SIN;
    private static final float SUN_SPIN = 0.03f;
    private static final float SUN_BREATH = 0.8f;
    private static final int PYRAMID_LIT = -12966896;
    private static final int PYRAMID_SHADE = -15397625;
    private static final int FAR_DUNE = -14412277;
    private static final int NEAR_DUNE = -15594746;
    private static final int DUNE_COLUMNS = 160;
    private static final int GRAIN_COUNT = 70;
    private static final float[][] GRAINS;
    private static final float VIGNETTE = 0.55f;

    private DuskScene() {
    }

    public static float horizon(float height) {
        return height * 0.72f;
    }

    public static void draw(ImDrawList dl, float width, float height, float scale, float time, float horizon, float sunX, float sunY, float sunRadius, float lookX, float lookY) {
        float y0;
        float s = Math.max(width / 2560.0f, height / 1440.0f);
        float x0 = (width - 2880.0f * s) * 0.5f;
        if (!DuskScene.layer(dl, SKY_IMAGE, x0, y0 = horizon - 1127.0f * s, s, 4.0f, lookX, lookY)) {
            DuskScene.sky(dl, width, height, horizon);
        }
        DuskScene.sun(dl, sunX, sunY, sunRadius, scale, time);
        if (!DuskScene.layer(dl, MID_IMAGE, x0, y0, s, 14.0f, lookX, lookY)) {
            DuskScene.pyramids(dl, width, height, horizon, scale);
        }
        if (!DuskScene.layer(dl, NEAR_IMAGE, x0, y0, s, 32.0f, lookX, lookY)) {
            DuskScene.dunes(dl, width, height, horizon, scale, time);
        }
        DuskScene.sand(dl, width, height, horizon, scale, time);
        DuskScene.vignette(dl, width, height);
    }

    private static boolean layer(ImDrawList dl, String image, float x0, float y0, float s, float drift, float lookX, float lookY) {
        ThemeImages.Texture texture = MenuTextures.original(image);
        if (texture == null) {
            return false;
        }
        float x = x0 - lookX * drift * s;
        float y = y0 - lookY * drift * 0.5f * s;
        dl.addImage((long)texture.id(), x, y, x + 2880.0f * s, y + 1620.0f * s);
        return true;
    }

    private static void sky(ImDrawList dl, float width, float height, float horizon) {
        int night = EgyptDraw.col(-16251124, 1.0f);
        int dusk = EgyptDraw.col(-15069431, 1.0f);
        int amber = EgyptDraw.col(-11194353, 1.0f);
        float turn = Math.min(height * 0.45f, horizon);
        dl.addRectFilledMultiColor(0.0f, 0.0f, width, turn, night, night, dusk, dusk);
        dl.addRectFilledMultiColor(0.0f, turn, width, horizon, dusk, dusk, amber, amber);
        dl.addRectFilled(0.0f, horizon, width, height, EgyptDraw.col(-16777216, 1.0f));
        DuskScene.horizonGlow(dl, width * 0.5f, horizon, width * 0.55f);
    }

    private static void sun(ImDrawList dl, float cx, float cy, float radius, float scale, float time) {
        ThemeImages.Texture texture = MenuTextures.original(SUN_IMAGE);
        if (texture == null) {
            EgyptDraw.sunDisk(dl, cx, cy, radius, time * 0.03f, scale, 1.0f);
            return;
        }
        float half = radius / 0.3115f * 0.5f;
        float glow = 0.965f + 0.035f * (float)Math.sin(time * 0.8f);
        dl.addImage((long)texture.id(), cx - half, cy - half, cx + half, cy + half, 0.0f, 0.0f, 1.0f, 1.0f, EgyptDraw.col(-1, glow));
    }

    private static void pyramids(ImDrawList dl, float width, float height, float horizon, float scale) {
        DuskScene.pyramid(dl, width * 0.17f, horizon, width * 0.34f, height * 0.25f, true, scale);
        DuskScene.pyramid(dl, width * 0.35f, horizon, width * 0.13f, height * 0.09f, true, scale);
        DuskScene.pyramid(dl, width * 0.83f, horizon, width * 0.26f, height * 0.19f, false, scale);
        DuskScene.pyramid(dl, width * 0.96f, horizon, width * 0.14f, height * 0.1f, false, scale);
    }

    private static void dunes(ImDrawList dl, float width, float height, float horizon, float scale, float time) {
        DuskScene.dune(dl, width, height, horizon + height * 0.015f, 9.0f * scale, 0.0021f / scale, 0.7f, time * 0.02f, -14412277);
        DuskScene.dune(dl, width, height, horizon + height * 0.09f, 15.0f * scale, 0.0014f / scale, 2.1f, time * 0.03f, -15594746);
    }

    private static void sand(ImDrawList dl, float width, float height, float horizon, float scale, float time) {
        float band = height - horizon + height * 0.08f;
        for (float[] grain : GRAINS) {
            float x = width * (1.0f - (float)((double)(grain[1] + time * grain[2]) % 1.0)) * 1.1f - width * 0.05f;
            float y = height - band * grain[0] + (float)Math.sin(time * 1.3f + grain[1] * 20.0f) * 3.0f * scale;
            float len = grain[3] * scale;
            dl.addLine(x, y, x + len, y - len * 0.08f, EgyptDraw.col(-2513573, grain[4]), Math.max(1.0f, scale));
        }
    }

    private static void vignette(ImDrawList dl, float width, float height) {
        float edge = Math.min(width, height) * 0.3f;
        int dark = EgyptDraw.col(-16777216, 0.55f);
        int clear = EgyptDraw.col(-16777216, 0.0f);
        dl.addRectFilledMultiColor(0.0f, 0.0f, edge, height, dark, clear, clear, dark);
        dl.addRectFilledMultiColor(width - edge, 0.0f, width, height, clear, dark, dark, clear);
    }

    public static void veil(ImDrawList dl, float width, float height, float amount) {
        dl.addRectFilled(0.0f, 0.0f, width, height, EgyptDraw.col(-16120059, amount));
    }

    private static void pyramid(ImDrawList dl, float cx, float base, float w, float h, boolean litRight, float s) {
        float left = cx - w * 0.5f;
        float right = cx + w * 0.5f;
        float apexX = cx + w * 0.06f * (float)(litRight ? 1 : -1);
        float apexY = base - h;
        int lit = EgyptDraw.col(-12966896, 1.0f);
        int shade = EgyptDraw.col(-15397625, 1.0f);
        dl.addTriangleFilled(left, base, apexX, apexY, cx, base, litRight ? shade : lit);
        dl.addTriangleFilled(cx, base, apexX, apexY, right, base, litRight ? lit : shade);
        float edgeX = litRight ? right : left;
        dl.addLine(apexX, apexY, edgeX, base, EgyptDraw.col(-871878, 0.35f), Math.max(1.0f, 1.2f * s));
        dl.addLine(apexX, apexY, cx, base, EgyptDraw.col(-871878, 0.12f), Math.max(1.0f, s));
    }

    private static void horizonGlow(ImDrawList dl, float cx, float cy, float radius) {
        float u = ImGui.getFontTexUvWhitePixelX();
        float v = ImGui.getFontTexUvWhitePixelY();
        int n = 72;
        dl.primReserve(n * 3 + 11 * n * 6, 1 + 12 * n);
        int centre = dl.getVtxCurrentIdx();
        dl.primWriteVtx(cx, cy, u, v, EgyptDraw.col(-2062038, 0.2f));
        for (int ring = 1; ring <= 12; ++ring) {
            float f = (float)ring / 12.0f;
            float left = 1.0f - f;
            int color = EgyptDraw.col(-2062038, 0.2f * left * left * (3.0f - 2.0f * left));
            for (int i = 0; i < n; ++i) {
                dl.primWriteVtx(cx + GLOW_COS[i] * radius * f, cy + GLOW_SIN[i] * radius * f, u, v, color);
            }
        }
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            dl.primWriteIdx(centre);
            dl.primWriteIdx(centre + 1 + i);
            dl.primWriteIdx(centre + 1 + j);
            for (int ring = 1; ring < 12; ++ring) {
                int inner = centre + 1 + (ring - 1) * n;
                int outer = inner + n;
                DuskScene.quad(dl, inner + i, outer + i, outer + j, inner + j);
            }
        }
    }

    private static void dune(ImDrawList dl, float width, float height, float y, float amp, float freq, float phase, float drift, int argb) {
        int i;
        float u = ImGui.getFontTexUvWhitePixelX();
        float v = ImGui.getFontTexUvWhitePixelY();
        int body = EgyptDraw.col(argb, 1.0f);
        int clear = EgyptDraw.col(argb, 0.0f);
        dl.primReserve(1920, 483);
        int first = dl.getVtxCurrentIdx();
        for (i = 0; i <= 160; ++i) {
            float x = width * (float)i / 160.0f;
            double w = x * freq + phase + drift;
            float ridge = y - (float)(Math.sin(w) * (double)amp + Math.sin(w * 2.3 + 1.7) * (double)amp * 0.45);
            dl.primWriteVtx(x, ridge - 0.5f, u, v, clear);
            dl.primWriteVtx(x, ridge + 0.5f, u, v, body);
            dl.primWriteVtx(x, height, u, v, body);
        }
        for (i = 0; i < 160; ++i) {
            int a = first + i * 3;
            int b = a + 3;
            DuskScene.quad(dl, a, b, b + 1, a + 1);
            DuskScene.quad(dl, a + 1, b + 1, b + 2, a + 2);
        }
    }

    private static void quad(ImDrawList dl, int a, int b, int c, int d) {
        dl.primWriteIdx(a);
        dl.primWriteIdx(b);
        dl.primWriteIdx(c);
        dl.primWriteIdx(a);
        dl.primWriteIdx(c);
        dl.primWriteIdx(d);
    }

    static {
        int i;
        GLOW_COS = new float[72];
        GLOW_SIN = new float[72];
        GRAINS = new float[70][];
        Random random = new Random(686869L);
        for (i = 0; i < 70; ++i) {
            DuskScene.GRAINS[i] = new float[]{random.nextFloat(), random.nextFloat(), 0.04f + random.nextFloat() * 0.09f, 4.0f + random.nextFloat() * 14.0f, 0.15f + random.nextFloat() * 0.35f};
        }
        for (i = 0; i < 72; ++i) {
            double angle = (double)i * Math.PI * 2.0 / 72.0;
            DuskScene.GLOW_COS[i] = (float)Math.cos(angle);
            DuskScene.GLOW_SIN[i] = (float)Math.sin(angle);
        }
    }
}

