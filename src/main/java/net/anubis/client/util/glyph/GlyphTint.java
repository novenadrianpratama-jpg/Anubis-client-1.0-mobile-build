/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_9848
 */
package net.anubis.client.util.glyph;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class GlyphTint {
    public static final String THEME = "Theme";
    public static final String MANUAL = "Manual";
    private static final float WARM = 0.16666667f;
    private static final float COOL = 0.6666667f;
    private static final float COMPANION_TURN = -0.09f;
    private int primary;
    private int secondary;
    private int highlight;
    private int companion;
    private int dust;
    private int flash;
    private float hue;
    private float saturation;
    private float value;

    public GlyphTint resolve(String mode, int accent, int manualMain, int manualSecond) {
        boolean manual = MANUAL.equals(mode);
        this.primary = class_9848.method_61334((int)(manual ? manualMain : accent));
        this.split(this.primary);
        if (manual) {
            this.secondary = class_9848.method_61334((int)manualSecond);
            this.companion = GlyphTint.mix(this.primary, this.secondary, 0.5f);
        } else {
            this.secondary = GlyphTint.hsv(GlyphTint.toward(this.hue, 0.6666667f, 0.025f), this.saturation * 1.05f, this.value * 0.66f);
            this.companion = GlyphTint.hsv(this.hue + -0.09f, this.saturation * 0.9f, this.value * 0.94f);
        }
        this.highlight = GlyphTint.hsv(GlyphTint.toward(this.hue, 0.16666667f, 0.03f), this.saturation * 0.5f, this.value + (1.0f - this.value) * 0.85f);
        this.dust = GlyphTint.hsv(GlyphTint.toward(this.hue, 0.16666667f, 0.02f), this.saturation * 0.34f, 0.5f + this.value * 0.4f);
        this.flash = GlyphTint.mix(this.highlight, -1, 0.6f);
        return this;
    }

    private void split(int rgb) {
        int r = class_9848.method_61327((int)rgb);
        int g = class_9848.method_61329((int)rgb);
        int b = class_9848.method_61331((int)rgb);
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        float range = max - min;
        this.value = (float)max / 255.0f;
        float f = this.saturation = max == 0 ? 0.0f : range / (float)max;
        this.hue = range == 0.0f ? 0.0f : (max == r ? class_3532.method_22450((float)((float)(g - b) / range / 6.0f)) : (max == g ? (2.0f + (float)(b - r) / range) / 6.0f : (4.0f + (float)(r - g) / range) / 6.0f));
    }

    private static float toward(float from, float target, float step) {
        float delta = target - from;
        return Math.abs(delta -= (float)class_3532.method_15375((float)(delta + 0.5f))) <= step ? target : from + Math.copySign(step, delta);
    }

    private static int hsv(float h, float s, float v) {
        float wrapped = class_3532.method_22450((float)h);
        if (wrapped >= 1.0f) {
            wrapped = 0.0f;
        }
        return class_3532.method_60599((float)wrapped, (float)class_3532.method_15363((float)s, (float)0.0f, (float)1.0f), (float)class_3532.method_15363((float)v, (float)0.0f, (float)1.0f), (int)255);
    }

    public int primary() {
        return this.primary;
    }

    public int secondary() {
        return this.secondary;
    }

    public int highlight() {
        return this.highlight;
    }

    public int companion() {
        return this.companion;
    }

    public int dust() {
        return this.dust;
    }

    public int flash() {
        return this.flash;
    }

    public static int alpha(int rgb, float alpha) {
        float clamped = alpha <= 0.0f ? 0.0f : Math.min(1.0f, alpha);
        return (int)(clamped * 255.0f + 0.5f) << 24 | rgb & 0xFFFFFF;
    }

    public static int mix(int from, int to, float k) {
        return class_9848.method_61319((float)Math.max(0.0f, Math.min(1.0f, k)), (int)from, (int)to);
    }

    public static int lift(int rgb, float amount) {
        return GlyphTint.mix(class_9848.method_61334((int)rgb), -1, amount);
    }
}

