/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_636
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.misc.AntiGhostBlockModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_636.class})
public abstract class MixinAntiGhostBlockMultiPlayerGameMode {
    @Shadow
    private int field_3716;

    @Shadow
    public abstract void method_2925();

    @Inject(method={"method_2899"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$holdBreak(class_2338 pos, CallbackInfoReturnable<Boolean> cir) {
        if (AntiGhostBlockModule.holdBreak(pos)) {
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"method_2910"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$waitBeforeStart(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
        if (!AntiGhostBlockModule.waiting(pos)) {
            return;
        }
        this.method_2925();
        cir.setReturnValue((Object)false);
    }

    @Inject(method={"method_2902"}, at={@At(value="HEAD")}, cancellable=true, order=1100)
    private void anubis$waitBeforeContinue(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
        if (!AntiGhostBlockModule.waiting(pos)) {
            return;
        }
        if (this.field_3716 > 0) {
            --this.field_3716;
        }
        this.method_2925();
        cir.setReturnValue((Object)false);
    }
}

