/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import java.util.ArrayList;
import java.util.List;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.HudLayoutStore;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.impl.client.RegionMapModule;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.util.donut.DonutGoliaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class RegionMapRenderer {
    private static final float PAD = 10.0f;
    private static final float INFO_GAP = 8.0f;
    private static final float INFO_LINE_H = 22.0f;
    private static final float LEGEND_GAP = 4.0f;
    private static final float LEGEND_ROW_H = 16.0f;
    private static final float LEGEND_SWATCH = 9.0f;
    private static final float LABEL_GAP = 8.0f;
    private static final int MIN_ID_PX = 6;
    private static final String MINUS = "\u2212";
    private static final String COORDINATE_LABEL = "Coordinate";
    private static final String REGION_LABEL = "Region";
    private static final String NO_REGION = "\u2014";
    private static final int[] REGION_COLORS = new int[]{-10236257, -10246656, -1012536, -4559313, -1397425, -12401931, -16545540};
    private static final int[] LEGEND_ORDER = new int[]{0, 1, 2, 4, 3, 5, 6};
    private static final float PIN_SIZE = 6.0f;
    private static final float MAP_ROUNDING = 4.0f;
    private static final int GOLIATH_DIVIDER = 0x2E000000;
    private static final int REGION_DIVIDER = 0x47000000;
    private static final List<int[]> GOLIATH_LINES = new ArrayList<int[]>();
    private static final List<int[]> REGION_LINES = new ArrayList<int[]>();

    private RegionMapRenderer() {
    }

    private static void dividerRuns(int line, boolean vertical) {
        int grid = 36;
        int start = 0;
        int kind = RegionMapRenderer.dividerKind(line, 0, vertical);
        for (int along = 1; along <= grid; ++along) {
            int next;
            int n = next = along < grid ? RegionMapRenderer.dividerKind(line, along, vertical) : -1;
            if (next == kind) continue;
            if (kind == 2) {
                REGION_LINES.add(new int[]{vertical ? 1 : 0, line, start, along});
            } else if (kind == 1) {
                GOLIATH_LINES.add(new int[]{vertical ? 1 : 0, line, start, along});
            }
            kind = next;
            start = along;
        }
    }

    private static int dividerKind(int line, int along, boolean vertical) {
        int b;
        int a = vertical ? DonutGoliaths.square(line - 1, along) : DonutGoliaths.square(along, line - 1);
        if (a < 0 != (b = vertical ? DonutGoliaths.square(line, along) : DonutGoliaths.square(along, line)) < 0 || a / 1000 != b / 1000) {
            return 2;
        }
        return a != b ? 1 : 0;
    }

    public static Size measureSize(RegionMapModule module, float scale) {
        float gridSize = RegionMapRenderer.gridSize(module, scale);
        float pad = 10.0f * scale;
        float contentW = Math.max(gridSize, RegionMapRenderer.measureInfoWidth(module, scale));
        return new Size(Math.round(contentW + pad * 2.0f), Math.round(gridSize + pad * 2.0f + 8.0f * scale + RegionMapRenderer.measureInfoHeight(module) * scale));
    }

    private static float gridSize(RegionMapModule module, float scale) {
        return 36.0f * module.squareSize() * scale;
    }

    private static float measureInfoWidth(RegionMapModule module, float scale) {
        ImGuiManager m = ImGuiManager.getInstance();
        int textPx = HudChrome.px(17.0f, scale);
        int labelPx = HudChrome.px(14.0f, scale);
        float labelCol = RegionMapRenderer.labelColumn(module, m.hudLabelFont(), labelPx) + 8.0f * scale;
        float values = module.showCoordinates() ? HudChrome.tabularWidth(m.hudTextFont(), textPx, "\u2212000000, \u2212000000") : 0.0f;
        float legend = 0.0f;
        for (String name : DonutGoliaths.REGIONS) {
            values = Math.max(values, HudChrome.width(m.hudTextFont(), textPx, "00  \u00b7  " + name));
            legend = Math.max(legend, 15.0f * scale + HudChrome.width(m.hudLabelFont(), labelPx, name));
        }
        return (float)Math.ceil(Math.max(labelCol + values, module.showLegend() ? legend : 0.0f));
    }

    public static void render(ImDrawList dl, RegionMapModule module, HudLayoutStore.HudElementState layout) {
        RegionMapRenderer.render(dl, module, layout, 1.0f);
    }

    public static void render(ImDrawList dl, RegionMapModule module, HudLayoutStore.HudElementState layout, float fade) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || dl == null || module == null || layout == null) {
            return;
        }
        float scale = HudRenderer.scaleOf(layout);
        float square = module.squareSize() * scale;
        float pad = 10.0f * scale;
        float gridSize = RegionMapRenderer.gridSize(module, scale);
        float x = Math.round(layout.x);
        float y = Math.round(layout.y);
        float r = HudRenderer.hudRounding() * scale;
        float alpha = module.opacity() * fade;
        HudChrome.card(dl, HudRenderer.hudSurface(layout), x, y, layout.width, layout.height, r, alpha, module.shadow(), scale);
        float gridX = Math.round(x + (layout.width - gridSize) * 0.5f);
        float gridY = y + pad;
        float mapR = Math.min(4.0f * scale, square);
        for (int region = 0; region < DonutGoliaths.CELLS.length; ++region) {
            int color = HudChrome.scaleAlpha(REGION_COLORS[region], alpha);
            for (int[] c : DonutGoliaths.CELLS[region]) {
                RegionMapRenderer.tile(dl, gridX, gridY, square, c, color, mapR);
            }
        }
        RegionMapRenderer.drawDividers(dl, gridX, gridY, square, alpha, scale);
        if (module.showRegionIds()) {
            for (int[][] goliaths : DonutGoliaths.CELLS) {
                for (int index = 0; index < goliaths.length; ++index) {
                    int[] c;
                    c = goliaths[index];
                    RegionMapRenderer.drawCenteredTileText(dl, index + 1, gridX + (float)c[0] * square, gridY + (float)c[1] * square, (float)(c[2] - c[0]) * square, (float)(c[3] - c[1]) * square, module.textScale(), alpha, scale);
                }
            }
        }
        RegionMapRenderer.renderPlayerPin(dl, module, mc, player, gridX, gridY, gridSize, alpha, scale);
        RegionMapRenderer.renderInfoPanel(dl, module, mc, player, x + pad, gridY + gridSize + 8.0f * scale, alpha, scale);
    }

    static int regionColor(int region) {
        return REGION_COLORS[region];
    }

    static void tile(ImDrawList dl, float gridX, float gridY, float square, int[] c, int color, float mapRounding) {
        int grid = 36;
        int corners = 0;
        if (c[0] == 0 && c[1] == 0) {
            corners |= 0x10;
        }
        if (c[2] == grid && c[1] == 0) {
            corners |= 0x20;
        }
        if (c[0] == 0 && c[3] == grid) {
            corners |= 0x40;
        }
        if (c[2] == grid && c[3] == grid) {
            corners |= 0x80;
        }
        float x1 = gridX + (float)c[0] * square;
        float y1 = gridY + (float)c[1] * square;
        float x2 = gridX + (float)c[2] * square;
        float y2 = gridY + (float)c[3] * square;
        if (corners == 0) {
            dl.addRectFilled(x1, y1, x2, y2, color);
        } else {
            dl.addRectFilled(x1, y1, x2, y2, color, mapRounding, corners);
        }
    }

    static void drawDividers(ImDrawList dl, float gridX, float gridY, float square, float alpha, float scale) {
        float width = Math.max(1.0f, scale);
        RegionMapRenderer.drawRuns(dl, GOLIATH_LINES, gridX, gridY, square, width, HudChrome.scaleAlpha(0x2E000000, alpha));
        RegionMapRenderer.drawRuns(dl, REGION_LINES, gridX, gridY, square, width, HudChrome.scaleAlpha(0x47000000, alpha));
    }

    private static void drawRuns(ImDrawList dl, List<int[]> runs, float gridX, float gridY, float square, float width, int color) {
        float half = width * 0.5f;
        for (int[] run : runs) {
            if (run[0] == 1) {
                float lx = gridX + (float)run[1] * square;
                dl.addRectFilled(lx - half, gridY + (float)run[2] * square, lx + half, gridY + (float)run[3] * square, color);
                continue;
            }
            float ly = gridY + (float)run[1] * square;
            dl.addRectFilled(gridX + (float)run[2] * square, ly - half, gridX + (float)run[3] * square, ly + half, color);
        }
    }

    static void drawCenteredTileText(ImDrawList dl, int number, float tx, float ty, float w, float h, float textScale, float alpha, float scale) {
        ImFont font = ImGuiManager.getInstance().hudLabelFont();
        String label = String.valueOf(number);
        int fontPx = Math.max(6, HudChrome.px(14.0f * textScale * 0.85f, scale));
        float roomW = w - 2.0f * scale;
        float textW = HudChrome.width(font, fontPx, "00");
        if (textW > roomW) {
            fontPx = (int)Math.floor((float)fontPx * roomW / textW);
        }
        if ((fontPx = Math.min(fontPx, (int)Math.floor(h - scale))) < 6) {
            return;
        }
        float lx = tx + (w - HudChrome.width(font, fontPx, label)) * 0.5f;
        float ly = ty + (h - (float)fontPx) * 0.5f;
        HudChrome.text(dl, font, fontPx, lx, ly, HudChrome.scaleAlpha(HudChrome.TEXT_VALUE, alpha), label, true);
    }

    private static void renderPlayerPin(ImDrawList dl, RegionMapModule module, class_310 mc, class_746 player, float gridX, float gridY, float gridSize, float alpha, float scale) {
        if (!RegionMapRenderer.inOverworld(mc)) {
            return;
        }
        double span = 450000.0;
        double fx = (player.method_23317() + 225000.0) / span;
        double fz = (player.method_23321() + 225000.0) / span;
        if (fx < 0.0 || fx >= 1.0 || fz < 0.0 || fz >= 1.0) {
            return;
        }
        float centerX = gridX + (float)(fx * (double)gridSize);
        float centerY = gridY + (float)(fz * (double)gridSize);
        double angle = Math.toRadians((double)(-player.method_36454()) - 90.0);
        float arrowSize = 6.0f * module.playerScale() * scale;
        int color = HudChrome.scaleAlpha(RegionMapRenderer.toAbgr(module.playerIndicatorColor()), alpha);
        float tipX = centerX + (float)(Math.cos(angle) * (double)arrowSize);
        float tipY = centerY - (float)(Math.sin(angle) * (double)arrowSize);
        float leftX = centerX + (float)(Math.cos(angle + Math.toRadians(135.0)) * (double)arrowSize);
        float leftY = centerY - (float)(Math.sin(angle + Math.toRadians(135.0)) * (double)arrowSize);
        float rightX = centerX + (float)(Math.cos(angle - Math.toRadians(135.0)) * (double)arrowSize);
        float rightY = centerY - (float)(Math.sin(angle - Math.toRadians(135.0)) * (double)arrowSize);
        dl.addTriangleFilled(tipX, tipY, leftX, leftY, rightX, rightY, color);
        dl.addTriangle(tipX, tipY, leftX, leftY, rightX, rightY, HudChrome.scaleAlpha(-1728053248, alpha), 1.0f);
    }

    private static void renderInfoPanel(ImDrawList dl, RegionMapModule module, class_310 mc, class_746 player, float x, float y, float alpha, float scale) {
        ImGuiManager m = ImGuiManager.getInstance();
        ImFont textFont = m.hudTextFont();
        ImFont labelFont = m.hudLabelFont();
        int textPx = HudChrome.px(17.0f, scale);
        int labelPx = HudChrome.px(14.0f, scale);
        float labelOffset = HudChrome.baselineOffset(textFont, textPx, labelFont, labelPx);
        float valueX = x + RegionMapRenderer.labelColumn(module, labelFont, labelPx) + 8.0f * scale;
        float lineH = 22.0f * scale;
        float textInset = (float)Math.floor((lineH - (float)textPx) * 0.5f);
        float lineTop = y + textInset;
        if (module.showCoordinates()) {
            HudChrome.text(dl, labelFont, labelPx, x, lineTop + labelOffset, HudChrome.scaleAlpha(HudChrome.TEXT_LABEL, alpha), COORDINATE_LABEL, false);
            HudChrome.tabular(dl, textFont, textPx, valueX, lineTop, HudChrome.scaleAlpha(HudChrome.TEXT_VALUE, alpha), RegionMapRenderer.number((long)Math.floor(player.method_23317())) + ", " + RegionMapRenderer.number((long)Math.floor(player.method_23321())), false);
            lineTop += lineH;
        }
        DonutGoliaths.Goliath here = RegionMapRenderer.inOverworld(mc) ? DonutGoliaths.at(player.method_23317(), player.method_23321()) : null;
        String region = here == null ? NO_REGION : here.index() + 1 + "  \u00b7  " + DonutGoliaths.REGIONS[here.region()];
        HudChrome.text(dl, labelFont, labelPx, x, lineTop + labelOffset, HudChrome.scaleAlpha(HudChrome.TEXT_LABEL, alpha), REGION_LABEL, false);
        HudChrome.text(dl, textFont, textPx, valueX, lineTop, HudChrome.scaleAlpha(HudChrome.TEXT_NAME, alpha), region, false);
        if (!module.showLegend()) {
            return;
        }
        float legendY = y + lineH * (float)RegionMapRenderer.infoLines(module) + 4.0f * scale;
        float rowH = 16.0f * scale;
        float swatch = 9.0f * scale;
        for (int i = 0; i < LEGEND_ORDER.length; ++i) {
            int regionIndex = LEGEND_ORDER[i];
            float rowY = legendY + (float)i * rowH;
            float swatchY = rowY + (rowH - swatch) * 0.5f;
            dl.addRectFilled(x, swatchY, x + swatch, swatchY + swatch, HudChrome.scaleAlpha(REGION_COLORS[regionIndex], alpha), 2.0f * scale);
            HudChrome.text(dl, labelFont, labelPx, x + swatch + 6.0f * scale, rowY + (float)Math.floor((rowH - (float)labelPx) * 0.5f), HudChrome.scaleAlpha(HudChrome.TEXT_NAME, alpha), DonutGoliaths.REGIONS[regionIndex], false);
        }
    }

    private static boolean inOverworld(class_310 mc) {
        return mc.field_1687 != null && mc.field_1687.method_27983() == class_1937.field_25179;
    }

    private static int infoLines(RegionMapModule module) {
        return module.showCoordinates() ? 2 : 1;
    }

    private static float labelColumn(RegionMapModule module, ImFont labelFont, int labelPx) {
        float region = HudChrome.width(labelFont, labelPx, REGION_LABEL);
        return module.showCoordinates() ? Math.max(HudChrome.width(labelFont, labelPx, COORDINATE_LABEL), region) : region;
    }

    private static float measureInfoHeight(RegionMapModule module) {
        float height = 22.0f * (float)RegionMapRenderer.infoLines(module);
        if (module.showLegend()) {
            height += 4.0f + (float)LEGEND_ORDER.length * 16.0f;
        }
        return height;
    }

    private static String number(long value) {
        return value < 0L ? MINUS + -value : Long.toString(value);
    }

    private static int toAbgr(ColorSetting color) {
        return GuiPalette.abgr(color.argb());
    }

    static {
        for (int line = 1; line < 36; ++line) {
            RegionMapRenderer.dividerRuns(line, true);
            RegionMapRenderer.dividerRuns(line, false);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Size(float width, float height) {
    }
}

