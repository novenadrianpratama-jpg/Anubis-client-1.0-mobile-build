/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.util.TracerEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;

@Environment(value=EnvType.CLIENT)
public final class WorldLabel {
    private static final int PRIMARY = -1;
    private static final int SECONDARY = -1315861;

    private WorldLabel() {
    }

    public static float[] project(class_310 mc, class_243 world, float screenW, float screenH) {
        class_4184 camera = mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332() || !TracerEngine.isInFront(camera, world)) {
            return null;
        }
        class_243 ndc = mc.field_1773.method_70778(world);
        if (!Double.isFinite(ndc.field_1352) || !Double.isFinite(ndc.field_1351)) {
            return null;
        }
        float x = (float)(ndc.field_1352 + 1.0) * 0.5f * screenW;
        float y = (float)(1.0 - ndc.field_1351) * 0.5f * screenH;
        if (x < -200.0f || x > screenW + 200.0f || y < -100.0f || y > screenH + 100.0f) {
            return null;
        }
        return new float[]{x, y};
    }

    public static void drawText(ImDrawList dl, float cx, float bottom, String primary, String secondary, float alpha) {
        ImGuiManager fonts = ImGuiManager.getInstance();
        ImFont main = fonts.hudTextFont();
        ImFont small = fonts.hudLabelFont();
        int mainPx = WorldLabel.fontPx(main);
        int smallPx = WorldLabel.fontPx(small);
        float padY = 5.0f;
        float gap = 6.0f;
        float w1 = HudChrome.width(main, mainPx, primary);
        float w2 = secondary == null ? 0.0f : HudChrome.width(small, smallPx, secondary);
        float x = Math.round(cx - (w1 + (secondary == null ? 0.0f : gap + w2)) * 0.5f);
        float y = Math.round(bottom - padY - (float)mainPx);
        HudChrome.text(dl, main, mainPx, x, y, WorldLabel.fade(-1, alpha), primary, true);
        if (secondary != null) {
            HudChrome.text(dl, small, smallPx, Math.round(x + w1 + gap), y + HudChrome.baselineOffset(main, mainPx, small, smallPx), WorldLabel.fade(-1315861, alpha), secondary, true);
        }
    }

    private static int fontPx(ImFont font) {
        return font != null ? Math.round(font.getFontSize()) : 13;
    }

    private static int fade(int abgr, float alpha) {
        int a = Math.round((float)(abgr >>> 24) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return a << 24 | abgr & 0xFFFFFF;
    }
}

