/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.imgui;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum UiFont {
    GEIST("Geist", "geist-medium.ttf", "geist-semibold.ttf", 1.0f, 0, 0, false, 0.546f, 0.0f),
    MANROPE("Manrope", "manrope-medium.ttf", "manrope-bold.ttf", 1.05f, 0, 0, false, 0.527f, 0.0f),
    RAJDHANI("Rajdhani", "rajdhani-semibold.ttf", "rajdhani-bold.ttf", 1.12f, 0, 0, false, 0.504f, 0.0f),
    SILKSCREEN("Silkscreen", "silkscreen-regular.ttf", "silkscreen-bold.ttf", 0.874f, 8, 2, true, 0.7f, 0.18f),
    JERSEY("Jersey 10", "jersey10-regular.ttf", "jersey10-regular.ttf", 1.09f, 20, 1, false, 0.5f, 0.0f);

    private static final String DIR = "/assets/anubis/fonts/";
    public final String label;
    private final String regular;
    private final String strong;
    private final float scale;
    private final int step;
    private final int minSteps;
    private final boolean emSized;
    private final float capRatio;
    private final float lift;

    private UiFont(String label, String regular, String strong, float scale, int step, int minSteps, boolean emSized, float capRatio, float lift) {
        this.label = label;
        this.regular = regular;
        this.strong = strong;
        this.scale = scale;
        this.step = step;
        this.minSteps = minSteps;
        this.emSized = emSized;
        this.capRatio = capRatio;
        this.lift = lift;
    }

    public static String[] labels() {
        return (String[])Arrays.stream(UiFont.values()).map(font -> font.label).toArray(String[]::new);
    }

    public static UiFont of(String label) {
        for (UiFont font : UiFont.values()) {
            if (!font.label.equals(label)) continue;
            return font;
        }
        return GEIST;
    }

    String resource(boolean bold) {
        return DIR + (bold ? this.strong : this.regular);
    }

    boolean pixel() {
        return this.step > 0;
    }

    float size(float geistSize) {
        float size = geistSize * this.scale;
        if (!this.pixel()) {
            return Math.round(size);
        }
        return this.step * Math.max(this.minSteps, Math.round(size / (float)this.step));
    }

    int flags() {
        if (!this.pixel()) {
            return 8;
        }
        int flags = 129;
        return this.emSized ? flags | 0x200 : flags;
    }

    float offsetY(float size) {
        return -Math.round(this.lift * size);
    }

    float fallbackSize(float size) {
        return Math.round(size * this.capRatio / UiFont.GEIST.capRatio);
    }
}

