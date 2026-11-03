/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.RegionMapRenderer;
import net.anubis.client.gui.SurfaceBackground;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.util.donut.DonutGoliaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

@Environment(value=EnvType.CLIENT)
public final class GoliathPickerScreen
extends class_437 {
    private static final float SQUARE = 7.0f;
    private static final float PAD = 14.0f;
    private static final float HEADER_H = 42.0f;
    private static final float INFO_H = 28.0f;
    private static final float BUTTON_W = 96.0f;
    private static final float BUTTON_H = 26.0f;
    private static final int HEADER_BAND = 0xEFFFFFF;
    private static final int WINDOW_FLAGS = 367;
    private final Set<DonutGoliaths.Goliath> picked;
    private final Consumer<Set<DonutGoliaths.Goliath>> onPick;
    private boolean confirmed;
    private boolean dragging;
    private boolean dragAdds;
    private DonutGoliaths.Goliath dragLast;

    public GoliathPickerScreen(Set<DonutGoliaths.Goliath> preselected, Consumer<Set<DonutGoliaths.Goliath>> onPick) {
        super((class_2561)class_2561.method_43470((String)"Goliath RTP"));
        this.picked = new LinkedHashSet<DonutGoliaths.Goliath>(preselected);
        this.onPick = onPick;
    }

    protected void method_25426() {
        super.method_25426();
        ImGuiManager.getInstance().activate(this::draw);
    }

    public void method_25432() {
        super.method_25432();
        ImGuiManager.getInstance().deactivate();
        this.onPick.accept(this.confirmed ? Set.copyOf(this.picked) : null);
    }

    public void method_25420(class_332 graphics, int mouseX, int mouseY, float partialTick) {
        if (ThemeStore.get().menuBlur.get()) {
            super.method_25420(graphics, mouseX, mouseY, partialTick);
        }
    }

    public void method_25394(class_332 graphics, int mouseX, int mouseY, float partialTick) {
        ImGuiManager.getInstance().requestCursor(graphics);
    }

    public boolean method_25404(class_11908 event) {
        if (event.comp_4795() != 257 && event.comp_4795() != 335) {
            return super.method_25404(event);
        }
        this.confirm();
        return true;
    }

    public boolean method_25421() {
        return false;
    }

    private void confirm() {
        if (this.picked.isEmpty()) {
            return;
        }
        this.confirmed = true;
        this.field_22787.execute(() -> ((GoliathPickerScreen)this).method_25419());
    }

    private void draw() {
        float side = AnubisWidgets.s(252.0f);
        float pad = AnubisWidgets.s(14.0f);
        float headerH = AnubisWidgets.s(42.0f);
        float infoH = AnubisWidgets.s(28.0f);
        float rounding = AnubisWidgets.s(GuiPalette.rounding());
        ImGui.setNextWindowPos((float)(ImGui.getIO().getDisplaySizeX() * 0.5f), (float)(ImGui.getIO().getDisplaySizeY() * 0.5f), (int)1, (float)0.5f, (float)0.5f);
        ImGui.pushStyleColor((int)2, (int)GuiPalette.panelBg());
        ImGui.pushStyleVar((int)2, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)14, (float)0.0f, (float)0.0f);
        ImGui.pushStyleVar((int)3, (float)rounding);
        ImGui.pushStyleVar((int)4, (float)0.0f);
        if (ImGui.begin((String)"##goliath_rtp", (int)367)) {
            float wx = ImGui.getWindowPosX();
            float wy = ImGui.getWindowPosY();
            float ww = side + pad * 2.0f;
            float wh = ImGui.getWindowHeight();
            ClickGuiRenderer.drawShadow(wx, wy, ww, wh, rounding, 4);
            ImDrawList dl = ImGui.getWindowDrawList();
            SurfaceBackground.draw(dl, ThemeStore.get().panels, wx, wy, ww, wh, rounding);
            boolean guard = HudChrome.needsTextGuard(ThemeStore.get().panels);
            GoliathPickerScreen.drawHeader(dl, wx, wy, ww, headerH, rounding, guard);
            ImGui.dummy((float)ww, (float)headerH);
            ImGui.setCursorPos((float)pad, (float)headerH);
            DonutGoliaths.Goliath hover = this.drawMap(dl, side);
            ImFont small = ImGuiManager.getInstance().smallFont();
            String info = hover != null ? hover.label() + (hover.region() == 2 ? "  \u00b7  no /rtp" : "") : (this.picked.isEmpty() ? "Click or drag to pick goliaths" : this.picked.size() + (this.picked.size() == 1 ? " goliath picked" : " goliaths picked"));
            float infoY = wy + headerH + side;
            AnubisWidgets.text(dl, small, wx + pad, infoY + (infoH - AnubisWidgets.lineHeight(small)) * 0.5f, -4542808, info, guard);
            float buttonW = AnubisWidgets.s(96.0f);
            float buttonH = AnubisWidgets.s(26.0f);
            float buttonsY = headerH + side + infoH;
            boolean pushed = ClickGuiRenderer.pushFont(small);
            ImGui.setCursorPos((float)pad, (float)buttonsY);
            if (AnubisWidgets.chipButton("##goliath_clear", "Clear", buttonW, buttonH, false)) {
                this.picked.clear();
            }
            ImGui.setCursorPos((float)(pad + side - buttonW), (float)buttonsY);
            String go = this.picked.isEmpty() ? "Go" : "Go (" + this.picked.size() + ")";
            if (AnubisWidgets.chipButton("##goliath_go", go, buttonW, buttonH, !this.picked.isEmpty())) {
                this.confirm();
            }
            if (pushed) {
                ImGui.popFont();
            }
            ImGui.setCursorPos((float)0.0f, (float)(buttonsY + buttonH));
            ImGui.dummy((float)ww, (float)pad);
        }
        ImGui.end();
        ImGui.popStyleVar((int)4);
        ImGui.popStyleColor();
    }

    private static void drawHeader(ImDrawList dl, float x, float y, float w, float h, float rounding, boolean guard) {
        float cap = Math.min(h, rounding + AnubisWidgets.s(2.0f));
        int top = AnubisWidgets.c(AnubisWidgets.withAlpha(0xEFFFFFF, 1.0f - cap / (2.0f * h)));
        int below = AnubisWidgets.c(AnubisWidgets.withAlpha(0xEFFFFFF, 1.0f - cap / h));
        int clear = AnubisWidgets.c(0xFFFFFF);
        dl.addRectFilled(x, y, x + w, y + cap, top, rounding, 48);
        dl.addRectFilledMultiColor(x, y + cap, x + w, y + h, below, below, clear, clear);
        ImFont font = ImGuiManager.getInstance().categoryFont();
        float r = AnubisWidgets.s(7.0f);
        float cx = x + AnubisWidgets.s(14.0f) + r;
        float cy = y + h * 0.5f;
        AnubisWidgets.grid(dl, cx, cy, r, -4542808);
        AnubisWidgets.text(dl, font, cx + r + AnubisWidgets.s(9.0f), y + (h - AnubisWidgets.lineHeight(font)) * 0.5f, -461582, "Goliath RTP", guard);
    }

    private DonutGoliaths.Goliath drawMap(ImDrawList dl, float side) {
        int region;
        DonutGoliaths.Goliath under;
        boolean pickable;
        float mapX = ImGui.getCursorScreenPosX();
        float mapY = ImGui.getCursorScreenPosY();
        ImGui.invisibleButton((String)"##goliath_map", (float)side, (float)side);
        float square = side / 36.0f;
        DonutGoliaths.Goliath hover = ImGui.isItemHovered() ? GoliathPickerScreen.goliathAt(ImGui.getMousePosX() - mapX, ImGui.getMousePosY() - mapY, square) : null;
        boolean bl = pickable = hover != null && hover.region() != 2;
        if (ImGui.isItemClicked((int)0) && pickable) {
            this.dragAdds = !this.picked.contains(hover);
            this.toggle(hover);
            this.dragging = true;
            this.dragLast = hover;
        } else if (this.dragging && ImGui.isMouseDown((int)0) && (under = GoliathPickerScreen.goliathAt(ImGui.getMousePosX() - mapX, ImGui.getMousePosY() - mapY, square)) != null && under.region() != 2 && !under.equals(this.dragLast)) {
            this.toggle(under);
            this.dragLast = under;
        }
        if (!ImGui.isMouseDown((int)0)) {
            this.dragging = false;
            this.dragLast = null;
        }
        float scale = ImGuiManager.getInstance().uiScale();
        float mapR = Math.min(AnubisWidgets.s(4.0f), square);
        boolean any = !this.picked.isEmpty();
        for (region = 0; region < DonutGoliaths.CELLS.length; ++region) {
            int color = RegionMapRenderer.regionColor(region);
            boolean europe = region == 2;
            int[][] goliaths = DonutGoliaths.CELLS[region];
            for (int index = 0; index < goliaths.length; ++index) {
                boolean bright;
                DonutGoliaths.Goliath goliath = new DonutGoliaths.Goliath(region, index);
                boolean bl2 = bright = !europe && (this.picked.contains(goliath) || goliath.equals(hover));
                float alpha = europe ? 0.3f : (bright ? 1.0f : (any ? 0.45f : 0.85f));
                RegionMapRenderer.tile(dl, mapX, mapY, square, goliaths[index], HudChrome.scaleAlpha(color, alpha), mapR);
            }
        }
        RegionMapRenderer.drawDividers(dl, mapX, mapY, square, 1.0f, scale);
        for (region = 0; region < DonutGoliaths.CELLS.length; ++region) {
            boolean europe = region == 2;
            int[][] goliaths = DonutGoliaths.CELLS[region];
            for (int index = 0; index < goliaths.length; ++index) {
                int[] cell = goliaths[index];
                float x1 = mapX + (float)cell[0] * square;
                float y1 = mapY + (float)cell[1] * square;
                float x2 = mapX + (float)cell[2] * square;
                float y2 = mapY + (float)cell[3] * square;
                DonutGoliaths.Goliath goliath = new DonutGoliaths.Goliath(region, index);
                float inset = AnubisWidgets.s(0.75f);
                if (this.picked.contains(goliath)) {
                    dl.addRect(x1 + inset, y1 + inset, x2 - inset, y2 - inset, -1, 0.0f, 0, AnubisWidgets.s(1.5f));
                } else if (!europe && goliath.equals(hover)) {
                    dl.addRect(x1 + inset, y1 + inset, x2 - inset, y2 - inset, -1711276033, 0.0f, 0, AnubisWidgets.s(1.0f));
                }
                RegionMapRenderer.drawCenteredTileText(dl, index + 1, x1, y1, x2 - x1, y2 - y1, 1.0f, europe ? 0.5f : 1.0f, scale);
            }
        }
        if (this.field_22787.field_1724 != null && this.field_22787.field_1687 != null && this.field_22787.field_1687.method_27983() != class_1937.field_25181) {
            double factor = this.field_22787.field_1687.method_27983() == class_1937.field_25180 ? 8.0 : 1.0;
            double span = 450000.0;
            double fx = (this.field_22787.field_1724.method_23317() * factor + 225000.0) / span;
            double fz = (this.field_22787.field_1724.method_23321() * factor + 225000.0) / span;
            if (fx >= 0.0 && fx < 1.0 && fz >= 0.0 && fz < 1.0) {
                float px = mapX + (float)(fx * (double)side);
                float pz = mapY + (float)(fz * (double)side);
                dl.addCircleFilled(px, pz, AnubisWidgets.s(3.0f), -13487361);
                dl.addCircle(px, pz, AnubisWidgets.s(3.0f), -1728053248, 16, AnubisWidgets.s(1.0f));
            }
        }
        return hover;
    }

    private void toggle(DonutGoliaths.Goliath goliath) {
        if (this.dragAdds) {
            this.picked.add(goliath);
        } else {
            this.picked.remove(goliath);
        }
    }

    private static DonutGoliaths.Goliath goliathAt(float u, float v, float square) {
        if (u < 0.0f || v < 0.0f) {
            return null;
        }
        return DonutGoliaths.at(u / square * 12500.0f - 225000.0f, v / square * 12500.0f - 225000.0f);
    }
}

