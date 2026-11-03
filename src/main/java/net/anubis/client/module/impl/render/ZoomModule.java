/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class ZoomModule
extends ClientModule {
    private static final float MIN_ZOOM = 1.1f;
    private static final float MAX_ZOOM = 50.0f;
    private static final float SCROLL_STEP = 1.2f;
    private static final float FAST_RATE = 30.0f;
    private static final float SLOW_RATE = 5.0f;
    private static final float MAX_FRAME_SECONDS = 0.1f;
    private static volatile ZoomModule instance;
    private static float target;
    private static float current;
    private static long lastNanos;
    private final NumberSetting zoom = this.addSetting("zoom", new NumberSetting("Zoom", 4.0f, 1.5f, 20.0f).step(0.5f));
    private final NumberSetting smoothness = this.addSetting("smoothness", new NumberSetting("Smoothness", 60.0f, 0.0f, 100.0f).step(5.0f));
    private final BooleanSetting scroll = this.addSetting("scroll", new BooleanSetting("Scroll", true));
    private final BooleanSetting lowerSensitivity = this.addSetting("lower-sensitivity", new BooleanSetting("Lower Sensitivity", true));
    private final BooleanSetting cinematic = this.addSetting("cinematic", new BooleanSetting("Cinematic Camera", false));
    private boolean smoothBefore;
    private boolean smoothPushed;

    public ZoomModule() {
        super("zoom", "Zoom", "Hold the key to zoom in.", ModuleCategory.VISUALS);
        this.setKeybind(67);
        this.setHoldMode(true);
        instance = this;
    }

    @Override
    protected void onEnable() {
        target = this.zoom.get();
        if (this.cinematic.get()) {
            class_310 mc = class_310.method_1551();
            this.smoothBefore = mc.field_1690.field_1914;
            mc.field_1690.field_1914 = true;
            this.smoothPushed = true;
        }
    }

    @Override
    protected void onDisable() {
        target = 1.0f;
        if (this.smoothPushed) {
            class_310.method_1551().field_1690.field_1914 = this.smoothBefore;
            this.smoothPushed = false;
        }
    }

    public static float fov(float fov) {
        float factor = ZoomModule.advance();
        return factor > 1.0001f ? fov / factor : fov;
    }

    public static double turnScale() {
        ZoomModule module = instance;
        if (module == null || !module.lowerSensitivity.get() || current <= 1.0001f) {
            return 1.0;
        }
        return 1.0 / (double)current;
    }

    public static boolean scroll(double amount) {
        ZoomModule module = instance;
        if (module == null || !module.isEnabled() || !module.scroll.get() || amount == 0.0) {
            return false;
        }
        float next = target * (amount > 0.0 ? 1.2f : 0.8333333f);
        target = Math.max(1.1f, Math.min(50.0f, next));
        return true;
    }

    private static float advance() {
        double to;
        float smooth;
        long now = System.nanoTime();
        float seconds = lastNanos == 0L ? 0.0f : Math.min(0.1f, (float)(now - lastNanos) / 1.0E9f);
        lastNanos = now;
        ZoomModule module = instance;
        float f = smooth = module == null ? 0.0f : module.smoothness.get() / 100.0f;
        if (smooth <= 0.0f || current == target) {
            current = target;
            return current;
        }
        float rate = 30.0f + -25.0f * smooth;
        float blend = 1.0f - (float)Math.exp(-seconds * rate);
        double from = Math.log(current);
        if (Math.abs((current = (float)Math.exp(from + ((to = Math.log(target)) - from) * (double)blend)) - target) < 0.002f) {
            current = target;
        }
        return current;
    }

    static {
        target = 1.0f;
        current = 1.0f;
    }
}

