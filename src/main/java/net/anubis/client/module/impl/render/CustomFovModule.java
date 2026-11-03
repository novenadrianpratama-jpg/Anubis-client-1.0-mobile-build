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
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class CustomFovModule
extends ClientModule {
    public static volatile CustomFovModule active;
    private static float screenEffectScale;
    private final NumberSetting fov = this.addSetting("fov", new NumberSetting("FOV", 110.0f, 30.0f, 160.0f).step(1.0f));
    private int culledFov = Integer.MIN_VALUE;

    public CustomFovModule() {
        super("custom-fov", "Custom Fov", "Changes your field of view (FOV).", ModuleCategory.VISUALS);
        this.fov.onChange(this::onFovChanged);
    }

    @Override
    protected void onEnable() {
        active = this;
        this.culledFov = this.fov.getInt();
        CustomFovModule.scheduleTerrainUpdate();
    }

    @Override
    protected void onDisable() {
        active = null;
        this.culledFov = Integer.MIN_VALUE;
        CustomFovModule.scheduleTerrainUpdate();
    }

    public float fov() {
        return this.fov.getInt();
    }

    public static void hudFovApplied(float vanilla, float applied) {
        screenEffectScale = vanilla == applied ? 1.0f : (float)(Math.tan(Math.toRadians(applied) * 0.5) / Math.tan(Math.toRadians(vanilla) * 0.5));
    }

    public static float screenEffectScale() {
        return screenEffectScale;
    }

    private void onFovChanged() {
        if (active != this) {
            return;
        }
        int value = this.fov.getInt();
        if (value == this.culledFov) {
            return;
        }
        this.culledFov = value;
        CustomFovModule.scheduleTerrainUpdate();
    }

    private static void scheduleTerrainUpdate() {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        if (!mc.method_18854()) {
            mc.execute(CustomFovModule::scheduleTerrainUpdate);
            return;
        }
        if (mc.field_1687 != null && mc.field_1769 != null) {
            mc.field_1769.method_3292();
        }
    }

    static {
        screenEffectScale = 1.0f;
    }
}

