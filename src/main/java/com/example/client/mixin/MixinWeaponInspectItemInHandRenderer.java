/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11659
 *  net.minecraft.class_1268
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_4587
 *  net.minecraft.class_746
 *  net.minecraft.class_759
 *  net.minecraft.class_811
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.anubis.client.module.impl.render.HandModelModule;
import net.anubis.client.module.impl.render.WeaponInspectModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_759.class})
public abstract class MixinWeaponInspectItemInHandRenderer {
    @Inject(method={"method_22976"}, at={@At(value="HEAD")})
    private void anubis$inspectHands(float partialTick, class_4587 poseStack, class_11659 collector, class_746 player, int light, CallbackInfo ci) {
        if (WeaponInspectModule.active != null) {
            WeaponInspectModule.handsBegin(poseStack);
        }
    }

    @WrapOperation(method={"method_3228"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V")})
    private void anubis$inspectItem(class_759 renderer, class_1309 entity, class_1799 stack, class_811 context, class_4587 poseStack, class_11659 collector, int light, Operation<Void> original, @Local(argsOnly=true) class_1268 hand) {
        boolean mainHand;
        boolean bl = mainHand = hand == class_1268.field_5808;
        if (WeaponInspectModule.active != null) {
            if (!WeaponInspectModule.renderItem(poseStack, entity, stack, context, collector, light, mainHand)) {
                original.call(new Object[]{renderer, entity, stack, context, poseStack, collector, light});
            }
            return;
        }
        if (HandModelModule.active != null) {
            HandModelModule.renderArm(poseStack, entity, stack, context, collector, light, mainHand);
        }
        original.call(new Object[]{renderer, entity, stack, context, poseStack, collector, light});
    }
}

