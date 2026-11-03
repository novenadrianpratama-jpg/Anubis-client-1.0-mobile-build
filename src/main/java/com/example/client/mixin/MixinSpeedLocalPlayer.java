/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1313
 *  net.minecraft.class_243
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.anubis.client.module.impl.movement.SpeedModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1313;
import net.minecraft.class_243;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_746.class})
public abstract class MixinSpeedLocalPlayer {
    @ModifyVariable(method={"method_5784"}, at=@At(value="HEAD"), argsOnly=true)
    private class_243 anubis$speedMove(class_243 movement, @Local(argsOnly=true) class_1313 type) {
        SpeedModule speed = SpeedModule.active;
        if (speed == null || type != class_1313.field_6308) {
            return movement;
        }
        return speed.onSelfMove((class_746)this, movement);
    }
}

