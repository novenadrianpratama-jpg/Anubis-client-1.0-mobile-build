/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_1297
 *  net.minecraft.class_4184
 *  net.minecraft.class_761
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.anubis.client.module.impl.render.ShaderEspModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public abstract class MixinShaderEspLevelRenderer {
    @Inject(method={"method_72914"}, at={@At(value="RETURN")})
    private void anubis$glowEspKeepState(class_1297 entity, float partialTick, CallbackInfoReturnable<class_10017> cir) {
        if (ShaderEspModule.active != null) {
            ShaderEspModule.extracted(entity, (class_10017)cir.getReturnValue());
        }
    }

    @Inject(method={"method_22710"}, at={@At(value="TAIL")})
    private void anubis$glowEsp(CallbackInfo ci, @Local(argsOnly=true) class_4184 camera, @Local(argsOnly=true, ordinal=0) Matrix4f viewRotation, @Local(argsOnly=true, ordinal=1) Matrix4f projection) {
        if (ShaderEspModule.active != null) {
            ShaderEspModule.afterLevel(camera, viewRotation, projection);
        }
    }
}

