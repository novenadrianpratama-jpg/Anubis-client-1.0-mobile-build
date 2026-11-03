/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11515
 *  net.minecraft.class_1920
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_3610
 *  net.minecraft.class_4076
 *  net.minecraft.class_750
 *  net.minecraft.class_8251
 *  net.minecraft.class_853
 *  net.minecraft.class_9810
 *  net.minecraft.class_9810$class_9811
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anubis.client.module.impl.render.XrayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11515;
import net.minecraft.class_1920;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import net.minecraft.class_4076;
import net.minecraft.class_750;
import net.minecraft.class_8251;
import net.minecraft.class_853;
import net.minecraft.class_9810;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9810.class})
public abstract class MixinSectionCompiler {
    @WrapOperation(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_853;method_8320(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2680;")})
    private class_2680 anubis$xrayBlockState(class_853 region, class_2338 pos, Operation<class_2680> original) {
        float alpha;
        class_2680 state = (class_2680)original.call(new Object[]{region, pos});
        XrayModule.Snapshot snapshot = XrayModule.active;
        if (snapshot == null || state.method_26215()) {
            return state;
        }
        float[] alphaSlot = XrayModule.CURRENT_ALPHA.get();
        alphaSlot[0] = alpha = snapshot.alphaFor((class_1920)region, state, pos);
        return alpha <= 0.0f ? class_2246.field_10124.method_9564() : state;
    }

    @WrapOperation(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4696;method_23679(Lnet/minecraft/class_2680;)Lnet/minecraft/class_11515;")})
    private class_11515 anubis$xrayBlockLayer(class_2680 state, Operation<class_11515> original) {
        if (XrayModule.active == null) {
            return (class_11515)original.call(new Object[]{state});
        }
        float alpha = XrayModule.CURRENT_ALPHA.get()[0];
        if (alpha > 0.0f && alpha < 1.0f) {
            return class_11515.field_60926;
        }
        return (class_11515)original.call(new Object[]{state});
    }

    @WrapOperation(method={"method_60904"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_4696;method_23680(Lnet/minecraft/class_3610;)Lnet/minecraft/class_11515;")})
    private class_11515 anubis$xrayFluidLayer(class_3610 fluidState, Operation<class_11515> original) {
        if (XrayModule.active == null) {
            return (class_11515)original.call(new Object[]{fluidState});
        }
        float alpha = XrayModule.CURRENT_ALPHA.get()[0];
        if (alpha > 0.0f && alpha < 1.0f) {
            return class_11515.field_60926;
        }
        return (class_11515)original.call(new Object[]{fluidState});
    }

    @Inject(method={"method_60904"}, at={@At(value="RETURN")})
    private void anubis$xrayResetAlpha(class_4076 sectionPos, class_853 region, class_8251 vertexSorting, class_750 bufferPack, CallbackInfoReturnable<class_9810.class_9811> cir) {
        XrayModule.CURRENT_ALPHA.get()[0] = 1.0f;
    }
}

