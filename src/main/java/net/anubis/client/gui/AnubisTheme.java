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
public final class AnubisTheme {
    public static final float CATEGORY_FONT_SIZE = 17.0f;
    public static final float MODULE_FONT_SIZE = 16.0f;
    public static final float TITLE_FONT_SIZE = 24.0f;
    public static final float SMALL_FONT_SIZE = 13.0f;
    public static final int GUI_SURFACE = -13226454;
    public static final int GUI_SURFACE_HOVER = -12240588;
    public static final int GUI_DIVIDER = 0xFFFFFFF;
    public static final int GUI_TEXT = -461582;
    public static final int GUI_TEXT_ROW = -2042416;
    public static final int GUI_TEXT_MUTED = -4542808;
    public static final int GUI_SWITCH_OFF = -11911366;
    public static final int GUI_KNOB = -461067;
    public static final int GUI_SHADOW = 0x14000000;
    public static final int HUD_LABEL = -7106930;

    private AnubisTheme() {
    }

    public static int lerpColor(int c1, int c2, float t) {
        int r1 = c1 & 0xFF;
        int r2 = c2 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b1 = c1 >> 16 & 0xFF;
        int b2 = c2 >> 16 & 0xFF;
        int a1 = c1 >>> 24;
        int a2 = c2 >>> 24;
        return (int)((float)a1 + (float)(a2 - a1) * t) << 24 | (int)((float)b1 + (float)(b2 - b1) * t) << 16 | (int)((float)g1 + (float)(g2 - g1) * t) << 8 | (int)((float)r1 + (float)(r2 - r1) * t);
    }
}

