/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2248
 *  net.minecraft.class_2350
 *  net.minecraft.class_2680
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.XrayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_2248.class})
public abstract class MixinBlock {
    @Inject(method={"method_9607"}, at={@At(value="HEAD")}, cancellable=true)
    private static void anubis$xrayForceFace(class_2680 state, class_2680 neighborState, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
        XrayModule.Snapshot snapshot = XrayModule.active;
        if (snapshot != null && snapshot.isWhitelisted(state.method_26204()) && XrayModule.CURRENT_ALPHA.get()[0] >= 1.0f) {
            cir.setReturnValue((Object)true);
        }
    }
}

