/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.movement.FlyModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_746.class})
public abstract class MixinFlyLocalPlayer {
    @ModifyReturnValue(method={"method_5715"}, at={@At(value="RETURN")})
    private boolean anubis$flyNoSneak(boolean original) {
        if (!original) {
            return false;
        }
        FlyModule fly = FlyModule.active;
        return fly == null || !fly.noSneak();
    }

    @ModifyExpressionValue(method={"method_6007"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_1656;method_7252()F")})
    private float anubis$flyFreecamVertical(float original) {
        FlyModule fly = FlyModule.active;
        if (fly == null || !fly.suppressFlightVerticalInput()) {
            return original;
        }
        return 0.0f;
    }
}

