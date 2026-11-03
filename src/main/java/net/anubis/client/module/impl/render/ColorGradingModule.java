/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.ColorGradingPass;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class ColorGradingModule
extends ClientModule {
    public static volatile ColorGradingModule active;
    private final NumberSetting vibrance = this.addSetting("vibrance", new NumberSetting("Vibrance", 35.0f, 0.0f, 100.0f).step(1.0f));
    private final NumberSetting contrast = this.addSetting("contrast", new NumberSetting("Contrast", 8.0f, -20.0f, 40.0f).step(1.0f));
    private final NumberSetting brightness = this.addSetting("brightness", new NumberSetting("Brightness", 3.0f, -20.0f, 30.0f).step(1.0f));
    private final NumberSetting warmth = this.addSetting("warmth", new NumberSetting("Warmth", 15.0f, -100.0f, 100.0f).step(1.0f));
    private final NumberSetting vignette = this.addSetting("vignette", new NumberSetting("Vignette", 30.0f, 0.0f, 100.0f).step(1.0f));

    public ColorGradingModule() {
        super("color-grading", "Color Grading", "Adds color, contrast and a vignette to the world.", ModuleCategory.VISUALS);
    }

    @Override
    protected void onEnable() {
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
        if (RenderSystem.isOnRenderThread()) {
            ColorGradingPass.release();
        } else {
            class_310.method_1551().execute(ColorGradingModule::releaseIfOff);
        }
    }

    private static void releaseIfOff() {
        if (active == null) {
            ColorGradingPass.release();
        }
    }

    public static void afterWorld(boolean worldRendered) {
        ColorGradingModule module = active;
        if (module == null) {
            return;
        }
        ColorGradingPass.apply(worldRendered, module.vibrance.get() / 100.0f, 1.0f + module.contrast.get() / 100.0f, 1.0f + module.brightness.get() / 100.0f, module.warmth.get() / 100.0f, module.vignette.get() / 100.0f);
    }
}

