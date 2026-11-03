/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  imgui.ImFontGlyph
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_3928
 *  net.minecraft.class_408
 *  net.minecraft.class_424
 *  net.minecraft.class_435
 */
package net.anubis.client.gui;

import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImFontGlyph;
import imgui.ImGui;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.Surface;
import net.anubis.client.event.impl.ModuleToggleEvent;
import net.anubis.client.gui.ClickGuiRenderer;
import net.anubis.client.gui.HudChrome;
import net.anubis.client.gui.Motion;
import net.anubis.client.gui.WorldText;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.client.HudModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_3928;
import net.minecraft.class_408;
import net.minecraft.class_424;
import net.minecraft.class_435;

@Environment(value=EnvType.CLIENT)
public final class ToggleToasts {
    public static final String ID = "toggle_toasts";
    private static final float MARGIN = 10.0f;
    private static final float GAP = 6.0f;
    private static final float CAP_H = 28.0f;
    private static final float PAD_X = 12.0f;
    private static final float PAD_Y = 8.0f;
    private static final float LINE_GAP = 2.0f;
    private static final float GAP_DIV = 10.0f;
    private static final float DIV_H = 12.0f;
    private static final float DOT = 3.0f;
    private static final float GAP_DOT = 8.0f;
    private static final float CHIP_H = 16.0f;
    private static final float CHIP_PAD = 6.0f;
    private static final float CHIP_RADIUS = 4.0f;
    private static final float GAP_CHIP = 10.0f;
    private static final float CHAT_INPUT = 14.0f;
    private static final int MAX_SHOWN = 4;
    private static final float IN_SECONDS = 0.16f;
    private static final float HOLD_SECONDS = 2.0f;
    private static final float OUT_SECONDS = 0.18f;
    private static final float MAX_STEP = 0.033333335f;
    private static final float LIFT_SPEED = 14.0f;
    private static final float OFF_OUTLINE = 0.55f;
    private static final int CHIP_TEXT = -1;
    private static final Object OWNER = new Object();
    private static final Queue<Toggle> PENDING = new ConcurrentLinkedQueue<Toggle>();
    private static final List<Toast> TOASTS = new ArrayList<Toast>();
    private static float lift = Float.NaN;

    private ToggleToasts() {
    }

    public static void install() {
        ModuleToggleEvent.TYPE.listen(OWNER, event -> ToggleToasts.show(event.module, event.enabled));
    }

    public static void show(ClientModule module, boolean enabled) {
        if (!module.toggleMessageOn()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null && mc.field_1687 != null) {
            PENDING.add(new Toggle(module, enabled));
        }
    }

    public static boolean hasWork() {
        return !TOASTS.isEmpty() || !PENDING.isEmpty();
    }

    public static void render() {
        Toggle toggle;
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            PENDING.clear();
            TOASTS.clear();
            lift = Float.NaN;
            return;
        }
        float dt = Math.min(ImGuiManager.getInstance().frameDelta(), 0.033333335f);
        ToggleToasts.step(dt);
        while ((toggle = PENDING.poll()) != null) {
            ToggleToasts.add(toggle);
        }
        if (TOASTS.isEmpty()) {
            lift = Float.NaN;
            return;
        }
        float s = ImGuiManager.getInstance().uiScale();
        lift = Motion.step(lift, ToggleToasts.liftTarget(mc, s), dt, 14.0f);
        if (!ToggleToasts.hidden(mc)) {
            ToggleToasts.draw(s);
        }
    }

    private static void add(Toggle toggle) {
        Toast toast = null;
        for (Toast existing : TOASTS) {
            if (existing.module != toggle.module()) continue;
            toast = existing;
            break;
        }
        if (toast == null) {
            toast = new Toast(toggle.module());
            TOASTS.add(toast);
        }
        toast.enabled = toggle.enabled();
        toast.leaving = false;
        toast.hold = 2.0f;
        int alive = 1;
        for (int i = TOASTS.size() - 1; i >= 0; --i) {
            Toast other = TOASTS.get(i);
            if (other == toast || other.leaving || ++alive <= 4) continue;
            other.leaving = true;
        }
    }

    private static void step(float dt) {
        for (int i = TOASTS.size() - 1; i >= 0; --i) {
            float f;
            Toast toast = TOASTS.get(i);
            if (toast.leaving) {
                toast.shown -= dt / 0.18f;
                if (!(toast.shown <= 0.0f)) continue;
                TOASTS.remove(i);
                continue;
            }
            if (toast.shown < 1.0f) {
                toast.shown = Math.min(1.0f, toast.shown + dt / 0.16f);
                continue;
            }
            toast.hold -= dt;
            if (!(f <= 0.0f)) continue;
            toast.leaving = true;
        }
    }

    private static float liftTarget(class_310 mc, float s) {
        if (mc.field_1755 instanceof class_408) {
            return 14.0f * (float)mc.method_22683().method_4495() + 6.0f * s;
        }
        return 10.0f * s;
    }

    private static boolean hidden(class_310 mc) {
        if (mc.field_1690.field_1842 && mc.field_1755 == null) {
            return true;
        }
        return mc.method_18506() != null || mc.field_1755 instanceof class_3928 || mc.field_1755 instanceof class_424 || mc.field_1755 instanceof class_435;
    }

    private static void draw(float s) {
        HudModule hud = ModuleRegistry.get(HudModule.class);
        HudModule.ToastStyle style = hud != null ? hud.toastStyle() : HudModule.ToastStyle.BAR;
        Surface surface = HudTheme.get().toasts;
        Look k = ToggleToasts.look(style, s, HudChrome.needsTextGuard(surface));
        float cursor = ImGui.getIO().getDisplaySizeY() - lift;
        for (int i = TOASTS.size() - 1; i >= 0; --i) {
            Toast toast = TOASTS.get(i);
            toast.open = ClickGuiRenderer.easeOut(Math.max(0.0f, Math.min(1.0f, toast.shown)));
            toast.bottom = Math.round(cursor);
            toast.top = Math.round(cursor - k.h() * toast.open);
            cursor -= (k.h() + 6.0f * s) * toast.open;
        }
        ImDrawList dl = ImGui.getForegroundDrawList();
        boolean shadow = hud == null || hud.shadow();
        float r = HudTheme.get().rounding.get() * s;
        float left = Math.round(10.0f * s);
        for (Toast toast : TOASTS) {
            boolean clip;
            float cardH = toast.bottom - toast.top;
            if (cardH < 1.0f) continue;
            String name = toast.module.name();
            float nameW = HudChrome.width(k.name(), k.namePx(), name);
            float w = ToggleToasts.measure(style, k, nameW);
            float x = Math.round(left - (w + left) * (1.0f - toast.open));
            float a = toast.open;
            HudChrome.card(dl, surface, x, toast.top, w, cardH, r, a, shadow, s);
            boolean bl = clip = cardH < k.h() - 0.5f;
            if (clip) {
                dl.pushClipRect(x, toast.top, x + w, toast.bottom, true);
            }
            float y = Math.round(toast.top - (k.h() - cardH) * 0.5f);
            switch (style) {
                case CARD: {
                    ToggleToasts.drawCard(dl, k, toast, x, y, a);
                    break;
                }
                case CHIP: {
                    ToggleToasts.drawChip(dl, k, toast, x, y, w, a);
                    break;
                }
                default: {
                    ToggleToasts.drawBar(dl, k, toast, x, y, nameW, a);
                }
            }
            if (!clip) continue;
            dl.popClipRect();
        }
    }

    private static Look look(HudModule.ToastStyle style, float s, boolean guard) {
        ImGuiManager gui = ImGuiManager.getInstance();
        ImFont name = gui.hudTextFont();
        ImFont label = gui.hudLabelFont();
        int namePx = HudChrome.px(17.0f, s);
        int labelPx = HudChrome.px(14.0f, s);
        float h = style == HudModule.ToastStyle.CARD ? (float)Math.round(8.0f * s * 2.0f + (float)namePx + 2.0f * s + (float)labelPx) : (float)Math.round(28.0f * s);
        float stateW = Math.max(HudChrome.width(label, labelPx, ToggleToasts.state(style, true)), HudChrome.width(label, labelPx, ToggleToasts.state(style, false)));
        if (style == HudModule.ToastStyle.CHIP) {
            stateW = (float)Math.ceil(stateW + 6.0f * s * 2.0f);
        }
        return new Look(name, namePx, label, labelPx, s, h, stateW, WorldText.hudAccent(), guard);
    }

    private static String state(HudModule.ToastStyle style, boolean enabled) {
        return switch (style) {
            default -> throw new MatchException(null, null);
            case HudModule.ToastStyle.BAR -> {
                if (enabled) {
                    yield "on";
                }
                yield "off";
            }
            case HudModule.ToastStyle.CARD -> {
                if (enabled) {
                    yield "Enabled";
                }
                yield "Disabled";
            }
            case HudModule.ToastStyle.CHIP -> enabled ? "ON" : "OFF";
        };
    }

    private static float measure(HudModule.ToastStyle style, Look k, float nameW) {
        float s = k.s();
        float w = switch (style) {
            default -> throw new MatchException(null, null);
            case HudModule.ToastStyle.BAR -> 12.0f * s * 2.0f + nameW + 10.0f * s * 2.0f + 1.0f + k.stateW();
            case HudModule.ToastStyle.CARD -> 12.0f * s * 2.0f + 14.0f * s + Math.max(nameW, k.stateW());
            case HudModule.ToastStyle.CHIP -> 12.0f * s + nameW + 10.0f * s + k.stateW() + ToggleToasts.chipInset(k);
        };
        return (float)Math.ceil(w);
    }

    private static float chipInset(Look k) {
        return (float)Math.floor((k.h() - (float)Math.round(16.0f * k.s())) * 0.5f);
    }

    private static void drawBar(ImDrawList dl, Look k, Toast toast, float x, float y, float nameW, float a) {
        float s = k.s();
        float nameTop = y + (float)Math.floor((k.h() - (float)k.namePx()) * 0.5f);
        float labelTop = nameTop + HudChrome.baselineOffset(k.name(), k.namePx(), k.label(), k.labelPx());
        float cx = Math.round(x + 12.0f * s);
        HudChrome.text(dl, k.name(), k.namePx(), cx, nameTop, HudChrome.scaleAlpha(HudChrome.TEXT_VALUE, a), toast.module.name(), k.guard());
        HudChrome.divider(dl, cx += nameW + 10.0f * s, y + k.h() * 0.5f, 12.0f * s, a);
        int color = HudChrome.scaleAlpha(toast.enabled ? k.accent() : HudChrome.TEXT_LABEL, a);
        HudChrome.text(dl, k.label(), k.labelPx(), Math.round(cx += 1.0f + 10.0f * s), labelTop, color, ToggleToasts.state(HudModule.ToastStyle.BAR, toast.enabled), k.guard());
    }

    private static void drawCard(ImDrawList dl, Look k, Toast toast, float x, float y, float a) {
        float s = k.s();
        dl.addCircleFilled(x + 15.0f * s, y + k.h() * 0.5f, 3.0f * s, HudChrome.scaleAlpha(toast.enabled ? k.accent() : HudChrome.TEXT_LABEL, a), 0);
        float tx = Math.round(x + 26.0f * s);
        float nameTop = Math.round(y + 8.0f * s);
        HudChrome.text(dl, k.name(), k.namePx(), tx, nameTop, HudChrome.scaleAlpha(HudChrome.TEXT_VALUE, a), toast.module.name(), k.guard());
        HudChrome.text(dl, k.label(), k.labelPx(), tx, Math.round(nameTop + (float)k.namePx() + 2.0f * s), HudChrome.scaleAlpha(HudChrome.TEXT_LABEL, a), ToggleToasts.state(HudModule.ToastStyle.CARD, toast.enabled), k.guard());
    }

    private static void drawChip(ImDrawList dl, Look k, Toast toast, float x, float y, float w, float a) {
        float s = k.s();
        float nameTop = y + (float)Math.floor((k.h() - (float)k.namePx()) * 0.5f);
        HudChrome.text(dl, k.name(), k.namePx(), Math.round(x + 12.0f * s), nameTop, HudChrome.scaleAlpha(HudChrome.TEXT_VALUE, a), toast.module.name(), k.guard());
        float inset = ToggleToasts.chipInset(k);
        float capH = Math.round(16.0f * s);
        float right = x + w - inset;
        float l = right - k.stateW();
        float top = y + inset;
        float r = Math.min(HudTheme.get().rounding.get(), 4.0f) * s;
        if (toast.enabled) {
            dl.addRectFilled(l, top, right, top + capH, HudChrome.scaleAlpha(k.accent(), a), r);
        } else {
            dl.addRect(l, top, right, top + capH, HudChrome.scaleAlpha(HudChrome.TEXT_LABEL, a * 0.55f), r, 0, 1.0f);
        }
        String word = ToggleToasts.state(HudModule.ToastStyle.CHIP, toast.enabled);
        float wordX = Math.round(l + (k.stateW() - HudChrome.width(k.label(), k.labelPx(), word)) * 0.5f);
        float wordY = Math.round(top + capH * 0.5f - ToggleToasts.capMiddle(k.label(), k.labelPx()));
        HudChrome.text(dl, k.label(), k.labelPx(), wordX, wordY, HudChrome.scaleAlpha(toast.enabled ? -1 : HudChrome.TEXT_LABEL, a), word, !toast.enabled && k.guard());
    }

    private static float capMiddle(ImFont font, int px) {
        ImFontGlyph glyph;
        float top = (float)px * 0.2f;
        if (font != null && font.getFontSize() > 0.0f && (glyph = font.findGlyphNoFallback(72)).isValidPtr()) {
            top = glyph.getY0() * (float)px / font.getFontSize();
        }
        return (top + HudChrome.ascent(font, px)) * 0.5f;
    }

    @Environment(value=EnvType.CLIENT)
    private record Toggle(ClientModule module, boolean enabled) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Toast {
        final ClientModule module;
        boolean enabled;
        boolean leaving;
        float shown;
        float hold;
        float open;
        float top;
        float bottom;

        Toast(ClientModule module) {
            this.module = module;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Look(ImFont name, int namePx, ImFont label, int labelPx, float s, float h, float stateW, int accent, boolean guard) {
    }
}

