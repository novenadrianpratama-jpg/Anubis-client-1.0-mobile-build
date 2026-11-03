/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12180$class_12181
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4604
 *  net.minecraft.class_5636
 *  net.minecraft.class_758$class_4596
 *  net.minecraft.class_761
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.example.client.mixin.AccessorGameRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.module.impl.render.NoRenderModule;
import net.anubis.client.util.ShaderOverlay;
import net.anubis.client.util.TracerEngine;
import net.anubis.client.util.WorldQuads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12180;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_5636;
import net.minecraft.class_758;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public abstract class MixinLevelRenderer {
    @ModifyArg(method={"method_22710"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_863;method_23099(Lnet/minecraft/class_4604;DDDF)V"), index=0)
    private class_4604 anubis$onRenderLevel(class_4604 frustum, @Local(argsOnly=true, ordinal=0) Matrix4f viewRotation, @Local(argsOnly=true, ordinal=1) Matrix4f projection) {
        float partialTick = class_310.method_1551().method_61966().method_60637(false);
        TracerEngine.beginFrame((Matrix4fc)viewRotation, (Matrix4fc)projection);
        WorldQuads.beginFrame(class_310.method_1551().field_1773.method_19418().method_71156());
        try (class_12180.class_12181 capture = ShaderOverlay.capture();){
            Render3DEvent.dispatch(partialTick, frustum);
        }
        return frustum;
    }

    @ModifyArg(method={"method_22710"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_761;method_62199(Lnet/minecraft/class_9909;Lnet/minecraft/class_12075;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V"), index=2)
    private GpuBufferSlice anubis$noFluidFogOnTop(GpuBufferSlice fog, @Local(argsOnly=true) class_4184 camera) {
        return MixinLevelRenderer.anubis$onTopFog(fog, camera);
    }

    @Unique
    private static GpuBufferSlice anubis$onTopFog(GpuBufferSlice fog, class_4184 camera) {
        if (camera.method_19334() == class_5636.field_27888) {
            return fog;
        }
        return ((AccessorGameRenderer)class_310.method_1551().field_1773).anubis$getFogRenderer().method_71109(class_758.class_4596.field_60101);
    }

    @Inject(method={"method_22710"}, at={@At(value="TAIL")})
    private void anubis$overlayAfterShaderPack(CallbackInfo ci, @Local(argsOnly=true) GpuBufferSlice fog, @Local(argsOnly=true) class_4184 camera, @Local(argsOnly=true, ordinal=0) Matrix4f viewRotation, @Local(argsOnly=true, ordinal=1) Matrix4f projection) {
        WorldQuads.render(viewRotation, projection);
        if (ShaderOverlay.pending()) {
            ShaderOverlay.render(camera, viewRotation, projection, fog, MixinLevelRenderer.anubis$onTopFog(fog, camera));
        }
    }

    @Inject(method={"method_22710"}, at={@At(value="TAIL")})
    private void anubis$worldFogForHand(CallbackInfo ci, @Local(argsOnly=true) GpuBufferSlice fog, @Local(argsOnly=true) class_4184 camera) {
        if (camera.method_19334() != class_5636.field_27888) {
            RenderSystem.setShaderFog((GpuBufferSlice)fog);
        }
    }

    @Inject(method={"method_43788"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$keepSkyDuringBlindnessDarkness(class_4184 camera, CallbackInfoReturnable<Boolean> cir) {
        if (NoRenderModule.hideBlindnessDarkness()) {
            cir.setReturnValue((Object)false);
        }
    }
}

