/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package net.anubis.client.module.impl.client;

import imgui.ImDrawList;
import imgui.ImFont;
import java.util.Locale;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.gui.AnubisWidgets;
import net.anubis.client.imgui.ImGuiManager;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.PerformanceBudget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class PerformanceModule
extends ClientModule
implements HudOverlayProvider {
    private static volatile boolean optimizing;
    private final BooleanSetting optimize = this.addSetting("optimize-client", new BooleanSetting("Optimize Client", true));
    private final BooleanSetting showStats = this.addSetting("show-stats", new BooleanSetting("Show Stats", true));
    private final BooleanSetting frustumCulling = this.addSetting("frustum-culling", new BooleanSetting("Frustum Culling", true));
    private final NumberSetting scanBudget = this.addSetting("scan-budget-ms", new NumberSetting("Scan Budget ms", 2.0f, 0.5f, 8.0f).step(0.5f));
    private long smoothedNanos;
    private long nextStatsUpdate;
    private String statsText = "";

    public PerformanceModule() {
        super("performance", "Performance", "Reduces background work and image memory while keeping effects and features. Disable to compare FPS.", ModuleCategory.CLIENT);
        this.optimize.onChange(this::updateOptimizationState);
    }

    public static boolean optimizationsEnabled() {
        return optimizing;
    }

    @Override
    protected void onEnable() {
        this.updateOptimizationState();
        this.nextStatsUpdate = 0L;
    }

    @Override
    protected void onDisable() {
        optimizing = false;
    }

    private void updateOptimizationState() {
        optimizing = this.isEnabled() && this.optimize.get();
        this.nextStatsUpdate = 0L;
    }

    public static boolean frustumCulling() {
        PerformanceModule module = ModuleRegistry.get(PerformanceModule.class);
        return module == null || module.frustumCulling.get();
    }

    public static long scanBudgetNanos() {
        PerformanceModule module = ModuleRegistry.get(PerformanceModule.class);
        return module == null ? 2000000L : (long)(module.scanBudget.get() * 1000000.0f);
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled() && this.showStats.get();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        this.smoothedNanos += (Render3DEvent.lastDurationNanos() - this.smoothedNanos) / 8L;
        long now = System.nanoTime();
        if (now >= this.nextStatsUpdate) {
            String scans = PerformanceModule.optimizationsEnabled() ? String.format(Locale.ROOT, "  \u00b7  scans %.2f ms", (double)PerformanceBudget.lastTickNanos() / 1000000.0) : "";
            this.statsText = String.format(Locale.ROOT, "%s  \u00b7  %d FPS  \u00b7  3D CPU %.2f ms%s  \u00b7  queue %d", PerformanceModule.optimizationsEnabled() ? "Optimized" : "Standard", class_310.method_1551().method_47599(), (double)this.smoothedNanos / 1000000.0, scans, ChunkScanner.pending());
            this.nextStatsUpdate = now + 250000000L;
        }
        String text = this.statsText;
        ImFont font = ImGuiManager.getInstance().hudLabelFont();
        float x = screenW - AnubisWidgets.textWidth(font, text) - 8.0f;
        float y = screenH - AnubisWidgets.lineHeight(font) - 6.0f;
        AnubisWidgets.text(dl, font, x + 1.0f, y + 1.0f, -1610612736, text);
        AnubisWidgets.text(dl, font, x, y, -3618616, text);
    }
}

