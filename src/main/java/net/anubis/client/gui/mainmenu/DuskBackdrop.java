/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 */
package net.anubis.client.gui.mainmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_332;

@Environment(value=EnvType.CLIENT)
public final class DuskBackdrop {
    public static final class_2960 TEXTURE = class_2960.method_60655((String)"anubis", (String)"textures/gui/dusk_backdrop.png");
    private static final float ASPECT = 1.7777778f;

    private DuskBackdrop() {
    }

    public static void draw(class_332 graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        float screen = (float)width / (float)height;
        float u = screen < 1.7777778f ? (1.0f - screen / 1.7777778f) * 0.5f : 0.0f;
        float v = screen > 1.7777778f ? (1.0f - 1.7777778f / screen) * 0.5f : 0.0f;
        graphics.method_70845(TEXTURE, 0, 0, width, height, u, 1.0f - u, v, 1.0f - v);
    }
}

