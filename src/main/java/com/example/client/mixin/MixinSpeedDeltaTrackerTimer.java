/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_9779$class_9781
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.movement.SpeedModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9779.class_9781.class})
public abstract class MixinSpeedDeltaTrackerTimer {
    @Shadow
    private float field_51958;

    @Inject(method={"method_60639"}, at={@At(value="FIELD", target="Lnet/minecraft/class_9779$class_9781;field_51962:J", opcode=181)})
    private void anubis$speedTimer(long time, CallbackInfoReturnable<Integer> cir) {
        float multiplier = SpeedModule.timerMultiplier;
        if (multiplier != 1.0f) {
            this.field_51958 *= multiplier;
        }
    }
}

