/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 *  net.minecraft.class_3610
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.movement.JesusModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_3610;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1309.class})
public abstract class MixinJesusLivingEntity {
    @ModifyReturnValue(method={"method_26319"}, at={@At(value="RETURN")})
    private boolean anubis$jesusCanStandOnFluid(boolean original, class_3610 fluidState) {
        if (original) {
            return true;
        }
        JesusModule jesus = JesusModule.active;
        if (jesus == null || this != class_310.method_1551().field_1724) {
            return false;
        }
        return jesus.canWalkOnFluid(fluidState);
    }
}

