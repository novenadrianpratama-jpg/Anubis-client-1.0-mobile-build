/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1304
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.config.Surface;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.HudGlyphs;
import net.anubis.client.gui.HudLayoutStore;
import net.anubis.client.gui.HudRenderer;
import net.anubis.client.gui.ItemIcons;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.impl.client.HudModule;
import net.anubis.client.module.impl.misc.NoInvLeakModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1304;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class ArmorStatus {
    private static final class_1304[] ARMOR = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final class_1304[] ARMOR_AND_HANDS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166, class_1304.field_6173, class_1304.field_6171};
    private static final float PAD = 6.0f;
    private static final float ICON = 18.0f;
    private static final float TILE_GAP = 5.0f;
    private static final float HANDS_GAP = 11.0f;
    private static final float GAUGE_H = 3.0f;
    private static final float GAUGE_GAP = 3.0f;
    private static final float TEXT_GAP = 2.0f;
    private static final float ROW_GAP = 4.0f;
    private static final float SIDE_GAP = 7.0f;
    private static final float SIDE_GAUGE_W = 44.0f;
    private static final float CRITICAL = 0.12f;
    private static final float GAUGE_RATE = 10.0f;
    private static final float SHOW_TIME = 0.25f;
    private static final String WIDEST = "100%";
    private static final String[] PERCENT = new String[101];
    private static final String[] COUNT = new String[100];
    private final float[] wear = new float[ARMOR_AND_HANDS.length];
    private final class_1792[] seen = new class_1792[ARMOR_AND_HANDS.length];
    private float presence;
    private float clock;
    private boolean column;
    private boolean hands;
    private boolean gauge;
    private boolean percent;

    ArmorStatus() {
    }

    void reset() {
        this.presence = 0.0f;
        for (int i = 0; i < this.seen.length; ++i) {
            this.seen[i] = null;
        }
    }

    boolean visible() {
        return this.presence > 0.0f;
    }

    void update(class_310 mc, HudModule hud, boolean active, boolean editMode, float dt) {
        boolean wanted;
        this.clock += dt;
        this.column = hud.armorColumn();
        this.hands = hud.armorHands();
        HudModule.ArmorWear mode = hud.armorWear();
        this.gauge = mode != HudModule.ArmorWear.PERCENT;
        this.percent = mode != HudModule.ArmorWear.GAUGE;
        class_746 player = mc.field_1724;
        boolean worn = false;
        class_1304[] order = this.order();
        for (int i = 0; i < order.length; ++i) {
            class_1799 stack;
            class_1799 class_17992 = stack = player == null ? class_1799.field_8037 : player.method_6118(order[i]);
            if (!stack.method_7960()) {
                worn = true;
            }
            float target = ArmorStatus.remaining(stack);
            if (stack.method_7909() != this.seen[i]) {
                this.seen[i] = stack.method_7909();
                this.wear[i] = target;
                continue;
            }
            this.wear[i] = target < 0.0f ? target : HudGlyphs.approach(Math.max(0.0f, this.wear[i]), target, 10.0f, dt);
        }
        boolean bl = wanted = editMode || active && worn;
        this.presence = editMode ? 1.0f : Math.max(0.0f, Math.min(1.0f, this.presence + (wanted ? dt : -dt) / 0.25f));
    }

    private class_1304[] order() {
        return this.hands ? ARMOR_AND_HANDS : ARMOR;
    }

    private static float remaining(class_1799 stack) {
        if (stack.method_7960() || !stack.method_7963() || stack.method_7936() <= 0) {
            return -1.0f;
        }
        return Math.max(0.0f, 1.0f - (float)stack.method_7919() / (float)stack.method_7936());
    }

    float planWidth(float s) {
        float pctW;
        if (!this.visible()) {
            return 1.0f;
        }
        int n = this.order().length;
        float icon = ItemIcons.pixels(18.0f * s);
        float f = pctW = this.percent ? (float)Math.ceil(ArmorStatus.percentWidth(s)) : 0.0f;
        if (this.column) {
            float side = (this.percent ? pctW : 0.0f) + (this.gauge ? 44.0f * s : 0.0f) + (this.percent && this.gauge ? 7.0f * s : 0.0f);
            return Math.round(6.0f * s * 2.0f + icon + 7.0f * s + side);
        }
        float tile = Math.max(icon, pctW);
        return Math.round(6.0f * s * 2.0f + (float)n * tile + (float)(n - 1) * 5.0f * s + (this.hands ? 11.0f * s - 5.0f * s : 0.0f));
    }

    float planHeight(float s) {
        int n = this.order().length;
        float icon = ItemIcons.pixels(18.0f * s);
        if (this.column) {
            return Math.round(6.0f * s * 2.0f + (float)n * icon + (float)(n - 1) * 4.0f * s + (this.hands ? 11.0f * s - 4.0f * s : 0.0f));
        }
        float below = (this.gauge ? 3.0f * s + ArmorStatus.gaugeHeight(s) : 0.0f) + (this.percent ? 2.0f * s + (float)HudChrome.px(14.0f, s) : 0.0f);
        return Math.round(6.0f * s * 2.0f + icon + below);
    }

    private static float percentWidth(float s) {
        return HudChrome.tabularWidth(ImGuiManager.getInstance().hudLabelFont(), HudChrome.px(14.0f, s), WIDEST);
    }

    private static float gaugeHeight(float s) {
        return Math.max(2.0f, (float)Math.round(3.0f * s));
    }

    void draw(ImDrawList dl, HudLayoutStore.HudElementState state, float x, float y, float alpha, boolean shadow, class_310 mc) {
        float a = alpha * HudGlyphs.easeOutQuint(this.presence);
        if (a <= 0.004f || mc.field_1724 == null) {
            return;
        }
        float s = HudRenderer.scaleOf(state);
        float w = state.width;
        float h = state.height;
        Surface surface = HudRenderer.hudSurface(state);
        boolean guard = HudChrome.needsTextGuard(surface);
        HudChrome.card(dl, surface, x, y, w, h, HudRenderer.hudRounding() * s, a, shadow, s);
        ImFont label = ImGuiManager.getInstance().hudLabelFont();
        int labelPx = HudChrome.px(14.0f, s);
        int icon = (int)ItemIcons.pixels(18.0f * s);
        float pad = Math.round(6.0f * s);
        float pctW = this.percent ? (float)Math.ceil(ArmorStatus.percentWidth(s)) : 0.0f;
        float tile = Math.max((float)icon, pctW);
        class_1304[] order = this.order();
        float cx = x + pad;
        float cy = y + pad;
        for (int i = 0; i < order.length; ++i) {
            class_1799 worn;
            class_1799 stack;
            float left;
            if (i == ARMOR.length) {
                this.drawHandsRule(dl, x, y, w, h, cx, cy, s, a);
                if (this.column) {
                    cy += 7.0f * s;
                } else {
                    cx += 6.0f * s;
                }
            }
            float f = left = (stack = NoInvLeakModule.shownInHud(worn = mc.field_1724.method_6118(order[i]), order[i])) == worn ? this.wear[i] : -1.0f;
            if (this.column) {
                this.drawSideways(dl, stack, left, cx, Math.round(cy), icon, pctW, label, labelPx, s, a, guard);
                cy += (float)icon + 4.0f * s;
                continue;
            }
            this.drawStacked(dl, stack, left, Math.round(cx), cy, icon, tile, label, labelPx, s, a, guard);
            cx += tile + 5.0f * s;
        }
    }

    private void drawHandsRule(ImDrawList dl, float x, float y, float w, float h, float cx, float cy, float s, float a) {
        int rule = HudGlyphs.ink(-871878, a * 0.45f);
        float t = Math.max(1.0f, (float)Math.round(s));
        if (this.column) {
            float ry = Math.round(cy + (11.0f * s - 4.0f * s) * 0.5f - 4.0f * s * 0.5f);
            HudGlyphs.register(dl, x + w * 0.15f, x + w * 0.85f, ry, t, rule);
        } else {
            float rx = Math.round(cx + (11.0f * s - 5.0f * s) * 0.5f - 5.0f * s * 0.5f);
            int clear = rule & 0xFFFFFF;
            dl.addRectFilledMultiColor(rx, y + h * 0.18f, rx + t, y + h * 0.5f, clear, clear, rule, rule);
            dl.addRectFilledMultiColor(rx, y + h * 0.5f, rx + t, y + h * 0.82f, rule, rule, clear, clear);
        }
    }

    private void drawStacked(ImDrawList dl, class_1799 stack, float left, float tx, float ty, int icon, float tile, ImFont label, int labelPx, float s, float a, boolean guard) {
        float ix = Math.round(tx + (tile - (float)icon) * 0.5f);
        this.drawIcon(dl, stack, left, ix, ty, icon, label, labelPx, s, a);
        float below = ty + (float)icon;
        if (this.gauge) {
            this.drawGauge(dl, stack, left, tx, below += 3.0f * s, tile, s, a);
            below += ArmorStatus.gaugeHeight(s);
        }
        if (this.percent && left >= 0.0f) {
            String value = ArmorStatus.percentText(left);
            float vw = HudChrome.tabularWidth(label, labelPx, value);
            HudChrome.tabular(dl, label, labelPx, Math.round(tx + (tile - vw) * 0.5f), Math.round(below + 2.0f * s), HudGlyphs.ink(HudGlyphs.vitality(left), a * this.criticalPulse(left)), value, guard);
        }
    }

    private void drawSideways(ImDrawList dl, class_1799 stack, float left, float tx, float ty, int icon, float pctW, ImFont label, int labelPx, float s, float a, boolean guard) {
        this.drawIcon(dl, stack, left, tx, ty, icon, label, labelPx, s, a);
        float cx = tx + (float)icon + 7.0f * s;
        float mid = ty + (float)icon * 0.5f;
        if (this.percent) {
            if (left >= 0.0f) {
                String value = ArmorStatus.percentText(left);
                float vw = HudChrome.tabularWidth(label, labelPx, value);
                HudChrome.tabular(dl, label, labelPx, Math.round(cx + pctW - vw), Math.round(mid - (float)labelPx * 0.5f), HudGlyphs.ink(HudGlyphs.vitality(left), a * this.criticalPulse(left)), value, guard);
            }
            cx += pctW + 7.0f * s;
        }
        if (this.gauge) {
            this.drawGauge(dl, stack, left, cx, Math.round(mid - ArmorStatus.gaugeHeight(s) * 0.5f), 44.0f * s, s, a);
        }
    }

    private void drawIcon(ImDrawList dl, class_1799 stack, float left, float ix, float iy, int icon, ImFont label, int labelPx, float s, float a) {
        int count;
        if (stack.method_7960()) {
            float inset = Math.max(1.0f, (float)Math.round(2.0f * s));
            dl.addRect(ix + inset, iy + inset, ix + (float)icon - inset, iy + (float)icon - inset, HudChrome.scaleAlpha(0x1FFFFFFF, a), Math.max(1.0f, 3.0f * s), 0, 1.0f);
            dl.addCircleFilled(ix + (float)icon * 0.5f, iy + (float)icon * 0.5f, Math.max(1.0f, 1.2f * s), HudChrome.scaleAlpha(0x1FFFFFFF, a), 8);
            return;
        }
        if (left >= 0.0f && left < 0.12f) {
            float glow = 0.5f + 0.5f * (float)Math.sin(this.clock * 6.5f);
            dl.addCircleFilled(ix + (float)icon * 0.5f, iy + (float)icon * 0.5f, (float)icon * 0.62f, HudGlyphs.ink(-2537681, a * (0.18f + 0.22f * glow)), 20);
        }
        ItemIcons.draw(dl, stack, ix, iy, (float)icon, a);
        if (stack.method_7958()) {
            HudGlyphs.sparkle(dl, ix + (float)icon - 1.5f * s, iy + 1.5f * s, 2.8f * s, HudGlyphs.ink(-11654, a * 0.9f));
        }
        if ((count = stack.method_7947()) > 1) {
            String value = ArmorStatus.countText(count);
            float vw = HudChrome.width(label, labelPx, value);
            HudChrome.text(dl, label, labelPx, Math.round(ix + (float)icon - vw + 2.0f * s), Math.round(iy + (float)icon - (float)labelPx + 3.0f * s), HudChrome.scaleAlpha(-1, a), value, true);
        }
    }

    private void drawGauge(ImDrawList dl, class_1799 stack, float left, float gx, float gy, float gw, float s, float a) {
        float gh = ArmorStatus.gaugeHeight(s);
        float r = gh * 0.5f;
        dl.addRectFilled(gx, gy, gx + gw, gy + gh, HudChrome.scaleAlpha(0x1AFFFFFF, a), r);
        if (stack.method_7960() || left < 0.0f) {
            return;
        }
        float fill = gw * Math.max(0.0f, Math.min(1.0f, left));
        if (fill < 0.5f) {
            return;
        }
        dl.addRectFilled(gx, gy, gx + fill, gy + gh, HudGlyphs.ink(HudGlyphs.vitality(left), a * this.criticalPulse(left)), Math.min(r, fill * 0.5f));
    }

    private float criticalPulse(float left) {
        if (left >= 0.12f) {
            return 1.0f;
        }
        return 0.6f + 0.4f * (0.5f + 0.5f * (float)Math.sin(this.clock * 6.5f));
    }

    private static String percentText(float left) {
        int value = Math.max(0, Math.min(100, Math.round(left * 100.0f)));
        Object cached = PERCENT[value];
        if (cached == null) {
            ArmorStatus.PERCENT[value] = cached = value + "%";
        }
        return cached;
    }

    private static String countText(int count) {
        if (count >= COUNT.length) {
            return Integer.toString(count);
        }
        String cached = COUNT[count];
        if (cached == null) {
            ArmorStatus.COUNT[count] = cached = Integer.toString(count);
        }
        return cached;
    }
}

