/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.client.HudModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class WorldText {
    public static final int NAME = HudChrome.TEXT_NAME;
    public static final int LABEL = HudChrome.TEXT_LABEL;
    private static final float LINE_GAP = 1.0f;
    private static final float ANCHOR_GAP = 2.0f;

    private WorldText() {
    }

    public static int hudAccent() {
        HudModule hud = ModuleRegistry.get(HudModule.class);
        return hud != null && hud.customAccent() ? GuiPalette.abgr(hud.accentArgb()) : GuiPalette.accent();
    }

    public static void draw(ImDrawList dl, float cx, float bottom, String name, String[] runs, int[] colors, float alpha) {
        if (alpha <= 0.001f) {
            return;
        }
        ImGuiManager fonts = ImGuiManager.getInstance();
        float u = fonts.uiScale();
        ImFont nameFont = fonts.hudTextFont();
        ImFont detailFont = fonts.hudLabelFont();
        int nameSize = HudChrome.px(17.0f, u);
        int detailSize = HudChrome.px(14.0f, u);
        boolean detail = runs.length > 0;
        float y = Math.round(bottom - 2.0f * u - (detail ? (float)detailSize + 1.0f * u : 0.0f) - (float)nameSize);
        float nameW = HudChrome.width(nameFont, nameSize, name);
        HudChrome.text(dl, nameFont, nameSize, Math.round(cx - nameW * 0.5f), y, HudChrome.scaleAlpha(NAME, alpha), name, true);
        if (!detail) {
            return;
        }
        float detailW = 0.0f;
        for (String run : runs) {
            detailW += HudChrome.width(detailFont, detailSize, run);
        }
        float x = Math.round(cx - detailW * 0.5f);
        float detailY = Math.round(y + (float)nameSize + 1.0f * u);
        for (int i = 0; i < runs.length; ++i) {
            HudChrome.text(dl, detailFont, detailSize, Math.round(x), detailY, HudChrome.scaleAlpha(colors[i], alpha), runs[i], true);
            x += HudChrome.width(detailFont, detailSize, runs[i]);
        }
    }
}

