/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10042
 *  net.minecraft.class_11659
 *  net.minecraft.class_1921
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_922
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_11659;
import net.minecraft.class_1921;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_922.class})
public abstract class MixinCharacterLivingEntityRenderer {
    @Inject(method={"method_24302"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$hideModelUnderCharacter(class_10042 state, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<class_1921> cir) {
        if (CharacterModule.hidesModel((class_10017)state)) {
            cir.setReturnValue(null);
        }
    }

    @WrapOperation(method={"method_4054"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_3887;method_4199(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10017;FF)V")})
    private void anubis$hideLayersUnderCharacter(class_3887<?, ?> layer, class_4587 poseStack, class_11659 collector, int light, class_10017 state, float yRot, float xRot, Operation<Void> original) {
        if (CharacterModule.hidesLayer(layer, state)) {
            return;
        }
        original.call(new Object[]{layer, poseStack, collector, light, state, Float.valueOf(yRot), Float.valueOf(xRot)});
    }
}

