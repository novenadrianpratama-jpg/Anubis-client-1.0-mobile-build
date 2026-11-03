/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ThemeTint {
    private static final int PINK = 16732080;
    private static final int DUSK = 2754594;
    private static final int WARM_ROSE = 16751309;
    private static final float SURFACE_SHARE = 0.45f;
    private static final float WORLD_SHARE = 0.3f;
    private static volatile float pink;

    private ThemeTint() {
    }

    public static void pink(float level) {
        pink = Math.max(0.0f, Math.min(1.0f, level));
    }

    public static float pink() {
        return pink;
    }

    public static int accent(int argb) {
        return ThemeTint.mix(argb, 16732080, pink);
    }

    public static int surface(int argb) {
        return ThemeTint.mix(argb, 2754594, pink * 0.45f);
    }

    public static float world() {
        float t = pink;
        return t * t * (3.0f - 2.0f * t) * 0.3f;
    }

    public static int world(int argb) {
        float t = ThemeTint.world();
        if (t <= 0.0f) {
            return argb;
        }
        float warm = ThemeTint.warmth(argb);
        int target = warm > 0.0f ? ThemeTint.mix(16732080, 16751309, warm) & 0xFFFFFF : 16732080;
        return ThemeTint.mix(argb, target, t * (1.0f - 0.4f * warm));
    }

    private static float warmth(int argb) {
        float hue;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        if (max == 0 || (float)(max - min) < 0.25f * (float)max || max == b) {
            return 0.0f;
        }
        float f = hue = max == r ? 60.0f * (float)(g - b) / (float)(max - min) : 60.0f * ((float)(b - r) / (float)(max - min) + 2.0f);
        if (hue < 0.0f) {
            hue += 360.0f;
        }
        return Math.min(ThemeTint.ramp(hue, 10.0f, 20.0f), 1.0f - ThemeTint.ramp(hue, 60.0f, 70.0f));
    }

    private static float ramp(float x, float from, float to) {
        return Math.max(0.0f, Math.min(1.0f, (x - from) / (to - from)));
    }

    private static int mix(int argb, int rgb, float t) {
        if (t <= 0.0f) {
            return argb;
        }
        int r = ThemeTint.lerp(argb >> 16 & 0xFF, rgb >> 16 & 0xFF, t);
        int g = ThemeTint.lerp(argb >> 8 & 0xFF, rgb >> 8 & 0xFF, t);
        int b = ThemeTint.lerp(argb & 0xFF, rgb & 0xFF, t);
        return argb & 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static int lerp(int a, int b, float t) {
        return Math.round((float)a + (float)(b - a) * t);
    }
}

