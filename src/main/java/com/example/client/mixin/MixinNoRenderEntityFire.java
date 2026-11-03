/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_1297
 *  net.minecraft.class_761
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.NoRenderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public abstract class MixinNoRenderEntityFire {
    @Inject(method={"method_72914"}, at={@At(value="RETURN")})
    private void anubis$hideEntityFire(class_1297 entity, float partialTick, CallbackInfoReturnable<class_10017> cir) {
        class_10017 state = (class_10017)cir.getReturnValue();
        if (state != null && NoRenderModule.hideEntityFire()) {
            state.field_53335 = false;
        }
    }
}

