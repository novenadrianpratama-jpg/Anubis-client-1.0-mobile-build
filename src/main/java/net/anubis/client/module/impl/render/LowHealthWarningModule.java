/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImGui
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import imgui.ImDrawList;
import imgui.ImGui;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.GuiPalette;
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
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class LowHealthWarningModule
extends ClientModule
implements HudOverlayProvider {
    private static final String PULSE_HEARTBEAT = "Heartbeat";
    private static final String PULSE_BREATH = "Breath";
    private static final String PULSE_STEADY = "Steady";
    private static final String COLOR_THEME = "Theme";
    private static final String COLOR_MANUAL = "Manual";
    private static final int OPAQUE = -16777216;
    private static final int WHITE = -1;
    private static final float EDGE_LIFT = 0.5f;
    private static final float RISE_RATE = 5.0f;
    private static final float FALL_RATE = 2.6f;
    private static final float DEATH_RATE = 12.0f;
    private static final float CALM_BPM = 64.0f;
    private static final float PANIC_BPM = 150.0f;
    private static final float SECOND_BEAT = 0.2f;
    private static final float BEAT_WIDTH = 0.065f;
    private static final long GAP_NANOS = 250000000L;
    private static final float TAU = (float)Math.PI * 2;
    private static final float[] BAND_REACH = new float[]{1.0f, 0.55f, 0.22f};
    private static final float[] BAND_WEIGHT = new float[]{0.5f, 0.34f, 0.3f};
    private final NumberSetting warnBelow = this.addSetting("warn-below", new NumberSetting("Warn Below", 7.0f, 2.0f, 20.0f).step(1.0f));
    private final BooleanSetting countAbsorption = this.addSetting("count-absorption", new BooleanSetting("Count Absorption", true));
    private final ModeSetting pulse = this.addSetting("pulse", new ModeSetting("Pulse", "Heartbeat", "Heartbeat", "Breath", "Steady"));
    private final NumberSetting intensity = this.addSetting("intensity", new NumberSetting("Intensity", 0.7f, 0.2f, 1.0f).step(0.05f));
    private final NumberSetting reach = this.addSetting("reach", new NumberSetting("Reach", 0.16f, 0.06f, 0.35f).step(0.01f));
    private final BooleanSetting edgeLine = this.addSetting("edge-line", new BooleanSetting("Edge Line", true));
    private final ModeSetting colorMode = this.addSetting("color", new ModeSetting("Color", "Theme", "Theme", "Manual"));
    private final ColorSetting glowColor = this.addSetting("glow-color", new ColorSetting("Glow Color", -13338378));
    private final ColorSetting edgeColor = this.addSetting("edge-color", new ColorSetting("Edge Color", -5650177));
    private float level;
    private float danger;
    private float phase;
    private long lastFrame;

    public LowHealthWarningModule() {
        super("low-health-warning", "Low Health Warning", "Flashes the screen edges when your health is low.", ModuleCategory.RENDER);
        this.glowColor.visibleWhen(this::manual);
        this.edgeColor.visibleWhen(() -> this.edgeLine.get() && this.manual());
    }

    @Override
    protected void onEnable() {
        this.level = 0.0f;
        this.danger = 0.0f;
        this.phase = 0.0f;
        this.lastFrame = 0L;
    }

    @Override
    protected void onDisable() {
        this.level = 0.0f;
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        float alpha;
        float limit;
        float health;
        class_310 mc = class_310.method_1551();
        float dt = Math.min(Math.max(ImGui.getIO().getDeltaTime(), 0.0f), 0.1f);
        long now = System.nanoTime();
        if (now - this.lastFrame > 250000000L) {
            this.level = 0.0f;
            this.phase = 0.0f;
        }
        this.lastFrame = now;
        class_746 player = mc.field_1724;
        boolean dead = player == null || player.method_29504();
        float want = 0.0f;
        if (!(dead || mc.field_1690.field_1842 || player.method_7325() || player.method_31549().field_7480 || !((health = player.method_6032() + (this.countAbsorption.get() ? player.method_6067() : 0.0f)) < (limit = this.warnBelow.get())))) {
            want = 1.0f;
            this.danger = Math.max(0.0f, Math.min(1.0f, (limit - health) / limit));
        }
        float rate = dead ? 12.0f : (want > this.level ? 5.0f : 2.6f);
        this.level += (want - this.level) * (1.0f - (float)Math.exp(-rate * dt));
        if (this.level < 0.003f && want == 0.0f) {
            this.level = 0.0f;
            this.phase = 0.0f;
            return;
        }
        float bpm = 64.0f + 86.0f * this.danger;
        if (this.pulse.is(PULSE_BREATH)) {
            bpm *= 0.5f;
        }
        this.phase = (this.phase + dt * bpm / 60.0f) % 1.0f;
        float beat = this.beat();
        float shown = this.level * this.level * (3.0f - 2.0f * this.level);
        float strength = this.intensity.get() * shown * (0.45f + 0.55f * this.danger);
        float depth = this.reach.get() * Math.min(screenW, screenH) * (1.0f + 0.12f * beat);
        boolean manual = this.manual();
        int accent = manual ? 0 : CosmeticAccent.argb();
        int base = GuiPalette.abgr(manual ? this.glowColor.argb() | 0xFF000000 : accent);
        for (int i = 0; i < BAND_REACH.length; ++i) {
            alpha = strength * BAND_WEIGHT[i] * (0.6f + 0.4f * beat);
            LowHealthWarningModule.frame(dl, screenW, screenH, depth * BAND_REACH[i], LowHealthWarningModule.withAlpha(base, alpha));
        }
        if (this.edgeLine.get()) {
            float line = Math.max(2.0f, depth * 0.07f);
            alpha = this.intensity.get() * shown * (0.2f + 0.55f * beat);
            int edge = manual ? this.edgeColor.argb() | 0xFF000000 : class_9848.method_61319((float)0.5f, (int)accent, (int)-1);
            LowHealthWarningModule.frame(dl, screenW, screenH, line, EgyptDraw.col(edge, alpha));
        }
    }

    private float beat() {
        if (this.pulse.is(PULSE_STEADY)) {
            return 0.35f;
        }
        if (this.pulse.is(PULSE_BREATH)) {
            return 0.5f - 0.5f * (float)Math.cos((float)Math.PI * 2 * this.phase);
        }
        return Math.min(1.0f, LowHealthWarningModule.bump(this.phase, 0.0f) + 0.62f * LowHealthWarningModule.bump(this.phase, 0.2f));
    }

    private static float bump(float at, float center) {
        float d = Math.abs(at - center);
        d = Math.min(d, 1.0f - d) / 0.065f;
        return (float)Math.exp(-d * d);
    }

    private boolean manual() {
        return this.colorMode.is(COLOR_MANUAL);
    }

    private static int withAlpha(int abgr, float alpha) {
        int a = Math.round(255.0f * Math.max(0.0f, Math.min(1.0f, alpha)));
        return abgr & 0xFFFFFF | a << 24;
    }

    private static void frame(ImDrawList dl, float w, float h, float depth, int edge) {
        if (depth < 1.0f || edge >>> 24 == 0) {
            return;
        }
        int clear = edge & 0xFFFFFF;
        dl.addRectFilledMultiColor(0.0f, 0.0f, w, depth, edge, edge, clear, clear);
        dl.addRectFilledMultiColor(0.0f, h - depth, w, h, clear, clear, edge, edge);
        dl.addRectFilledMultiColor(0.0f, 0.0f, depth, h, edge, clear, clear, edge);
        dl.addRectFilledMultiColor(w - depth, 0.0f, w, h, clear, edge, edge, clear);
    }
}

