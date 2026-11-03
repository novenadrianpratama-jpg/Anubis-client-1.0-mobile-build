/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.impl.render.HandAuraPass;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class HandAuraModule
extends ClientModule {
    public static volatile HandAuraModule active;
    private static final String COLOR_THEME = "Theme";
    private static final String COLOR_MANUAL = "Manual";
    private static final int WHITE = -1;
    private static final float SHEEN_LIFT = 0.55f;
    private static final long BREATH_NANOS = 3200000000L;
    private static final long SHEEN_NANOS = 4400000000L;
    private final ModeSetting colorMode = this.addSetting("color", new ModeSetting("Color", "Theme", "Theme", "Manual"));
    private final ColorSetting glowColor = this.addSetting("glow-color", new ColorSetting("Glow Color", -13338378));
    private final NumberSetting aura = this.addSetting("aura", new NumberSetting("Aura", 65.0f, 0.0f, 100.0f).step(5.0f));
    private final NumberSetting reach = this.addSetting("reach", new NumberSetting("Reach", 8.0f, 2.0f, 20.0f).step(1.0f));
    private final NumberSetting rim = this.addSetting("rim", new NumberSetting("Rim", 40.0f, 0.0f, 100.0f).step(5.0f));
    private final NumberSetting sheen = this.addSetting("sheen", new NumberSetting("Sheen", 25.0f, 0.0f, 100.0f).step(5.0f));
    private final ColorSetting sheenColor = this.addSetting("sheen-color", new ColorSetting("Sheen Color", -3153665));
    private final BooleanSetting breathe = this.addSetting("breathe", new BooleanSetting("Breathe", true));

    public HandAuraModule() {
        super("hand-aura", "Hand Aura", "Adds a glowing outline to your hand and held item.", ModuleCategory.VISUALS);
        this.glowColor.visibleWhen(this::manual);
        this.reach.visibleWhen(() -> this.aura.get() > 0.0f);
        this.sheenColor.visibleWhen(() -> this.sheen.get() > 0.0f && this.manual());
    }

    @Override
    protected void onEnable() {
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
        if (RenderSystem.isOnRenderThread()) {
            HandAuraPass.release();
        } else {
            class_310.method_1551().execute(HandAuraModule::releaseIfOff);
        }
    }

    private static void releaseIfOff() {
        if (active == null) {
            HandAuraPass.release();
        }
    }

    public static boolean beginCapture() {
        HandAuraModule module = active;
        return module != null && module.shows() && HandAuraPass.begin();
    }

    public static void endCapture(boolean drawn) {
        if (!HandAuraPass.end() || !drawn) {
            return;
        }
        HandAuraModule module = active;
        float scale = Math.max(1.0f, (float)class_310.method_1551().method_1522().field_1481 / 1080.0f);
        if (module == null) {
            HandAuraPass.compose(-1, 0.0f, 2.0f, 0.0f, 2.0f * scale, -1, 0.0f, 0.0f);
            return;
        }
        long now = System.nanoTime();
        float phase = (float)((double)(now % 4400000000L) / 4.4E9);
        int glow = module.glowArgb();
        HandAuraPass.compose(glow, module.auraStrength(now), module.reach.get() * scale, module.rim.get() / 100.0f, 2.0f * scale, module.sheenArgb(glow), module.sheen.get() / 100.0f, phase);
    }

    private boolean manual() {
        return this.colorMode.is(COLOR_MANUAL);
    }

    private int glowArgb() {
        return this.manual() ? this.glowColor.argb() : CosmeticAccent.argb();
    }

    private int sheenArgb(int glow) {
        return this.manual() ? this.sheenColor.argb() : class_9848.method_61319((float)0.55f, (int)glow, (int)-1);
    }

    private boolean shows() {
        return this.aura.get() > 0.0f || this.rim.get() > 0.0f;
    }

    private float auraStrength(long now) {
        float strength = this.aura.get() / 100.0f;
        if (!this.breathe.get()) {
            return strength;
        }
        double cycle = (double)(now % 3200000000L) / 3.2E9;
        float swell = (float)Math.sin(cycle * Math.PI);
        return strength * (0.6f + 0.4f * swell * swell);
    }
}

