/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_757
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.anubis.client.module.impl.render.CustomFovModule;
import net.anubis.client.module.impl.render.ZoomModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class MixinCustomFovGameRenderer {
    @Shadow
    public abstract boolean method_35765();

    @ModifyReturnValue(method={"method_3196"}, at={@At(value="RETURN")})
    private float anubis$customFov(float original, @Local(argsOnly=true) boolean useFovSetting) {
        float fov;
        CustomFovModule module = CustomFovModule.active;
        float f = fov = module == null || this.method_35765() ? original : module.fov();
        if (!useFovSetting) {
            CustomFovModule.hudFovApplied(original, fov);
        }
        return useFovSetting && !this.method_35765() ? ZoomModule.fov(fov) : fov;
    }
}

