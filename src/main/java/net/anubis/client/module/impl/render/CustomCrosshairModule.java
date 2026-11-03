/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11631
 *  net.minecraft.class_12392
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1934
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_310
 *  net.minecraft.class_3908
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_4061
 *  net.minecraft.class_408
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.render;

import imgui.ImDrawList;
import imgui.ImGui;
import net.anubis.client.gui.AnubisTheme;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.gui.GuiPalette;
import net.anubis.client.gui.ThemeTint;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11631;
import net.minecraft.class_12392;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1934;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3908;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4061;
import net.minecraft.class_408;
import net.minecraft.class_746;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class CustomCrosshairModule
extends ClientModule
implements HudOverlayProvider {
    public static volatile CustomCrosshairModule active;
    private static final String STYLE_CROSS = "Cross";
    private static final String STYLE_T = "T";
    private static final String STYLE_X = "X";
    private static final String STYLE_DOT = "Dot";
    private static final String STYLE_CIRCLE = "Circle";
    private static final String COLOR_CUSTOM = "Custom";
    private static final String COLOR_ACCENT = "Accent";
    private static final int OUTLINE = -1946157056;
    private static final int TRACK_ARGB = -870967267;
    private static final float BAR_HALF_WIDTH = 10.0f;
    private static final float BAR_HEIGHT = 3.0f;
    private static final float DIAGONAL = 0.70710677f;
    private static final double FULL_SPREAD_SPEED = 0.28;
    private static final float TINT_RATE = 14.0f;
    private static final float SPREAD_RATE = 12.0f;
    private static final float FADE_RATE = 18.0f;
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Cross", "Cross", "T", "X", "Dot", "Circle"));
    private final NumberSetting size = this.addSetting("size", new NumberSetting("Size", 6.0f, 1.0f, 20.0f).step(1.0f));
    private final NumberSetting gap = this.addSetting("gap", new NumberSetting("Gap", 2.0f, 0.0f, 16.0f).step(1.0f));
    private final NumberSetting thickness = this.addSetting("thickness", new NumberSetting("Thickness", 2.0f, 1.0f, 5.0f).step(1.0f));
    private final BooleanSetting centerDot = this.addSetting("center-dot", new BooleanSetting("Center Dot", false));
    private final BooleanSetting outline = this.addSetting("outline", new BooleanSetting("Outline", true));
    private final ModeSetting colorMode = this.addSetting("color-mode", new ModeSetting("Color", "Custom", "Custom", "Accent"));
    private final ColorSetting color = this.addSetting("color", new ColorSetting("Custom Color", -657929));
    private final BooleanSetting targetTint = this.addSetting("target-tint", new BooleanSetting("Target Tint", false));
    private final ColorSetting targetColor = this.addSetting("target-color", new ColorSetting("Target Color", -47814));
    private final BooleanSetting dynamicGap = this.addSetting("dynamic-gap", new BooleanSetting("Dynamic Gap", false));
    private final NumberSetting spread = this.addSetting("spread", new NumberSetting("Spread", 4.0f, 1.0f, 12.0f).step(1.0f));
    private float tint;
    private float extraGap;
    private float barAlpha;
    private float barFill;

    public CustomCrosshairModule() {
        super("custom-crosshair", "Custom Crosshair", "Draws a configurable crosshair in place of the vanilla one.", ModuleCategory.VISUALS);
        this.gap.visibleWhen(this::hasArms);
        this.centerDot.visibleWhen(() -> !this.style.is(STYLE_DOT));
        this.color.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.targetColor.visibleWhen(this.targetTint::get);
        this.spread.visibleWhen(() -> this.dynamicGap.get() && !this.style.is(STYLE_DOT));
    }

    @Override
    protected void onEnable() {
        this.tint = 0.0f;
        this.extraGap = 0.0f;
        this.barAlpha = 0.0f;
        this.barFill = 1.0f;
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        float edge;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || !CustomCrosshairModule.isVisible(mc)) {
            return;
        }
        float dt = Math.min(Math.max(ImGui.getIO().getDeltaTime(), 0.0f), 0.1f);
        float scale = ImGuiManager.getInstance().uiScale();
        this.updateMotion(mc, player, dt);
        Geometry geometry = this.geometry(screenW * 0.5f, screenH * 0.5f, scale);
        int fill = GuiPalette.abgr(this.currentColor());
        float fillAlpha = (float)(fill >>> 24 & 0xFF) / 255.0f;
        float f = edge = this.outline.get() ? Math.max(1.0f, (float)Math.round(scale)) : 0.0f;
        if (edge > 0.0f) {
            this.drawShape(dl, geometry, AnubisWidgets.withAlpha(-1946157056, fillAlpha), edge);
        }
        this.drawShape(dl, geometry, fill, 0.0f);
        this.drawCooldown(dl, mc, player, geometry, fill, edge, scale, dt);
    }

    private static boolean isVisible(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1761 == null || mc.field_1690.field_1842) {
            return false;
        }
        if (mc.field_1755 != null && !(mc.field_1755 instanceof class_408)) {
            return false;
        }
        if (!mc.field_1690.method_31044().method_31034()) {
            return false;
        }
        if (mc.field_61504.method_72772(class_11631.field_61583)) {
            return false;
        }
        return mc.field_1761.method_2920() != class_1934.field_9219 || CustomCrosshairModule.spectatorCanInteract(mc);
    }

    private static boolean spectatorCanInteract(class_310 mc) {
        class_239 hit = mc.field_1765;
        if (hit instanceof class_3966) {
            class_3966 entityHit = (class_3966)hit;
            return entityHit.method_17782() instanceof class_3908;
        }
        if (hit instanceof class_3965) {
            class_3965 blockHit = (class_3965)hit;
            if (hit.method_17783() == class_239.class_240.field_1332) {
                class_2338 pos = blockHit.method_17777();
                return mc.field_1687.method_8320(pos).method_26196((class_1937)mc.field_1687, pos) != null;
            }
        }
        return false;
    }

    private void updateMotion(class_310 mc, class_746 player, float dt) {
        class_1309 living;
        class_1297 class_12972;
        boolean onTarget = this.targetTint.get() && (class_12972 = mc.field_1692) instanceof class_1309 && (living = (class_1309)class_12972).method_5805();
        this.tint = CustomCrosshairModule.approach(this.tint, onTarget ? 1.0f : 0.0f, 14.0f, dt);
        float wanted = 0.0f;
        if (this.dynamicGap.get()) {
            double moved = Math.hypot(player.method_23317() - player.field_6014, player.method_23321() - player.field_5969);
            float moving = (float)Math.min(1.0, moved / 0.28);
            float swing = 1.0f - player.method_7261(0.0f);
            wanted = this.spread.get() * Math.max(moving, swing);
        }
        this.extraGap = CustomCrosshairModule.approach(this.extraGap, wanted, 12.0f, dt);
    }

    private static float approach(float current, float target, float rate, float dt) {
        float next = current + (target - current) * (1.0f - (float)Math.exp(-rate * dt));
        return Math.abs(target - next) < 0.001f ? target : next;
    }

    private int currentColor() {
        int base = this.colorMode.is(COLOR_ACCENT) ? CosmeticAccent.argb() : ThemeTint.world(this.color.argb());
        return this.tint <= 0.0f ? base : AnubisTheme.lerpColor(base, this.targetColor.argb(), this.tint);
    }

    private boolean hasArms() {
        return this.style.is(STYLE_CROSS) || this.style.is(STYLE_T) || this.style.is(STYLE_X);
    }

    private Geometry geometry(float cx, float cy, float scale) {
        float thick = Math.max(1.0f, (float)Math.round(this.thickness.get() * scale));
        float length = Math.max(1.0f, (float)Math.round(this.size.get() * scale));
        float baseGap = this.hasArms() ? this.gap.get() : 0.0f;
        float open = Math.round((baseGap + this.extraGap) * scale);
        float maxExtra = this.dynamicGap.get() && !this.style.is(STYLE_DOT) ? this.spread.get() : 0.0f;
        float below = this.reachBelow(length, Math.round((baseGap + maxExtra) * scale), thick);
        return new Geometry(cx, cy, length, open, thick, below);
    }

    private float reachBelow(float length, float open, float thick) {
        float below = switch (this.style.get()) {
            case STYLE_DOT -> length * 0.5f;
            case STYLE_CIRCLE -> length + open + thick * 0.5f;
            case STYLE_X -> (open + length) * 0.70710677f + thick * 0.5f;
            default -> open + length + thick * 0.5f;
        };
        return this.centerDot.get() ? Math.max(below, thick * 0.5f) : below;
    }

    private boolean armsJoined(Geometry g) {
        return (this.style.is(STYLE_CROSS) || this.style.is(STYLE_T)) && g.open() < 1.0f;
    }

    private void drawShape(ImDrawList dl, Geometry g, int color, float grow) {
        switch (this.style.get()) {
            case "Dot": {
                dl.addCircleFilled(g.cx(), g.cy(), Math.max(1.0f, g.size() * 0.5f) + grow, color, 0);
                break;
            }
            case "Circle": {
                dl.addCircle(g.cx(), g.cy(), g.size() + g.open(), color, 0, g.thick() + grow * 2.0f);
                break;
            }
            case "X": {
                CustomCrosshairModule.drawDiagonals(dl, g, color, grow);
                break;
            }
            default: {
                CustomCrosshairModule.drawArms(dl, g, color, grow, !this.style.is(STYLE_T));
            }
        }
        if (this.centerDot.get() && !this.style.is(STYLE_DOT) && !this.armsJoined(g)) {
            float x0 = (float)Math.floor(g.cx() - g.thick() * 0.5f);
            float y0 = (float)Math.floor(g.cy() - g.thick() * 0.5f);
            CustomCrosshairModule.rect(dl, x0, y0, x0 + g.thick(), y0 + g.thick(), color, grow);
        }
    }

    private static void drawArms(ImDrawList dl, Geometry g, int color, float grow, boolean top) {
        float x0 = (float)Math.floor(g.cx() - g.thick() * 0.5f);
        float x1 = x0 + g.thick();
        float y0 = (float)Math.floor(g.cy() - g.thick() * 0.5f);
        float y1 = y0 + g.thick();
        float near = g.open();
        float far = g.open() + g.size();
        if (near < 1.0f) {
            CustomCrosshairModule.rect(dl, x0 - far, y0, x1 + far, y1, color, grow);
            dl.addRectFilled(x0 - grow, y1 + grow, x1 + grow, y1 + far + grow, color);
            if (top) {
                dl.addRectFilled(x0 - grow, y0 - far - grow, x1 + grow, y0 - grow, color);
            }
            return;
        }
        CustomCrosshairModule.rect(dl, x0 - far, y0, x0 - near, y1, color, grow);
        CustomCrosshairModule.rect(dl, x1 + near, y0, x1 + far, y1, color, grow);
        CustomCrosshairModule.rect(dl, x0, y1 + near, x1, y1 + far, color, grow);
        if (top) {
            CustomCrosshairModule.rect(dl, x0, y0 - far, x1, y0 - near, color, grow);
        }
    }

    private static void drawDiagonals(ImDrawList dl, Geometry g, int color, float grow) {
        float near = Math.max(0.0f, g.open() - grow) * 0.70710677f;
        float far = (g.open() + g.size() + grow) * 0.70710677f;
        float width = g.thick() + grow * 2.0f;
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sy = -1; sy <= 1; sy += 2) {
                dl.addLine(g.cx() + (float)sx * near, g.cy() + (float)sy * near, g.cx() + (float)sx * far, g.cy() + (float)sy * far, color, width);
            }
        }
    }

    private static void rect(ImDrawList dl, float x0, float y0, float x1, float y1, int color, float grow) {
        dl.addRectFilled(x0 - grow, y0 - grow, x1 + grow, y1 + grow, color);
    }

    private void drawCooldown(ImDrawList dl, class_310 mc, class_746 player, Geometry g, int fill, float edge, float scale, float dt) {
        boolean wanted = mc.field_1690.method_42565().method_41753() == class_4061.field_18152;
        float charge = player.method_7261(0.0f);
        boolean ready = wanted && CustomCrosshairModule.readyOnTarget(mc, player, charge);
        this.barFill = ready ? 1.0f : Math.min(1.0f, charge);
        this.barAlpha = CustomCrosshairModule.approach(this.barAlpha, wanted && (ready || charge < 1.0f) ? 1.0f : 0.0f, 18.0f, dt);
        if (this.barAlpha < 0.01f) {
            return;
        }
        float w = Math.max(12.0f, 2.0f * (float)Math.round(10.0f * scale));
        float h = Math.max(2.0f, (float)Math.round(3.0f * scale));
        float x0 = (float)Math.floor(g.cx() - w * 0.5f);
        float y0 = Math.round(g.cy() + g.below() + edge + 5.0f * scale);
        float alpha = this.barAlpha * (float)(fill >>> 24 & 0xFF) / 255.0f;
        if (edge > 0.0f) {
            dl.addRectFilled(x0 - edge, y0 - edge, x0 + w + edge, y0 + h + edge, AnubisWidgets.withAlpha(-1946157056, alpha), h * 0.5f + edge);
        }
        dl.addRectFilled(x0, y0, x0 + w, y0 + h, AnubisWidgets.withAlpha(GuiPalette.abgr(-870967267), alpha), h * 0.5f);
        float filled = Math.round(w * this.barFill);
        if (filled >= 1.0f) {
            dl.addRectFilled(x0, y0, x0 + filled, y0 + h, AnubisWidgets.withAlpha(fill | 0xFF000000, alpha), h * 0.5f);
        }
    }

    private static boolean readyOnTarget(class_310 mc, class_746 player, float charge) {
        class_1309 target;
        class_1297 class_12972;
        if (charge < 1.0f || !((class_12972 = mc.field_1692) instanceof class_1309) || !(target = (class_1309)class_12972).method_5805()) {
            return false;
        }
        if (player.method_7279() <= 5.0f) {
            return false;
        }
        class_12392 range = (class_12392)player.method_76694().method_58694(class_9334.field_64680);
        return range == null || mc.field_1765 == null || range.method_76736((class_1309)player, mc.field_1765.method_17784());
    }

    @Environment(value=EnvType.CLIENT)
    private record Geometry(float cx, float cy, float size, float open, float thick, float below) {
    }
}

