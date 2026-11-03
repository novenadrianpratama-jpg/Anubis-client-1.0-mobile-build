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
import net.anubis.client.module.impl.render.MotionBlurPass;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class MotionBlurModule
extends ClientModule {
    public static volatile MotionBlurModule active;
    private final NumberSetting strength = this.addSetting("strength", new NumberSetting("Strength", 50.0f, 10.0f, 90.0f).step(5.0f));

    public MotionBlurModule() {
        super("motion-blur", "Motion Blur", "Adds motion blur when you turn.", ModuleCategory.VISUALS);
    }

    @Override
    protected void onEnable() {
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
        if (RenderSystem.isOnRenderThread()) {
            MotionBlurPass.release();
        } else {
            class_310.method_1551().execute(MotionBlurModule::releaseIfOff);
        }
    }

    private static void releaseIfOff() {
        if (active == null) {
            MotionBlurPass.release();
        }
    }

    public static void afterWorld(boolean worldRendered) {
        MotionBlurModule module = active;
        if (module != null) {
            MotionBlurPass.apply(worldRendered, module.strength.get() / 100.0f);
        }
    }
}

