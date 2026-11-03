/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1058
 *  net.minecraft.class_1657
 *  net.minecraft.class_2680
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4603
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.movement.BoatNoclipModule;
import net.anubis.client.module.impl.render.CustomFovModule;
import net.anubis.client.module.impl.render.NoRenderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1657;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_4603.class})
public abstract class MixinScreenEffectRenderer {
    @ModifyExpressionValue(method={"method_23067"}, at={@At(value="NEW", target="()Lnet/minecraft/class_4587;")})
    private class_4587 anubis$undoCustomFov(class_4587 poseStack) {
        float scale = CustomFovModule.screenEffectScale();
        if (scale != 1.0f) {
            poseStack.method_23760().method_23761().scale(scale, scale, 1.0f);
        }
        return poseStack;
    }

    @Inject(method={"method_23070"}, at={@At(value="HEAD")}, cancellable=true)
    private static void anubis$cancelFireOverlay(class_4587 poseStack, class_4597 bufferSource, class_1058 sprite, CallbackInfo ci) {
        if (NoRenderModule.hideFireOverlay()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_24225"}, at={@At(value="HEAD")}, cancellable=true)
    private static void anubis$noWallOverlayInNoclipBoat(class_1657 player, CallbackInfoReturnable<class_2680> cir) {
        if (BoatNoclipModule.hidesWallOverlay(player)) {
            cir.setReturnValue(null);
        }
    }
}

