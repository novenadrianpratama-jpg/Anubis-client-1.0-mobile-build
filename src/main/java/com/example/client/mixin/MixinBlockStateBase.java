/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_4970$class_4971
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.XrayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_4970.class_4971.class})
public abstract class MixinBlockStateBase {
    @Inject(method={"method_26210"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$xrayNoAmbientOcclusion(class_1922 level, class_2338 pos, CallbackInfoReturnable<Float> cir) {
        if (XrayModule.active != null) {
            cir.setReturnValue((Object)Float.valueOf(1.0f));
        }
    }
}

