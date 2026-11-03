/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1316
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.movement.BoatFlyModule;
import net.anubis.client.module.impl.movement.EntityControlModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1316;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_746.class})
public abstract class MixinEntityControlLocalPlayer {
    @ModifyReturnValue(method={"method_3151"}, at={@At(value="RETURN")})
    private float anubis$entityControlMaxJump(float original) {
        return EntityControlModule.maxJump() ? 1.0f : original;
    }

    @ModifyReturnValue(method={"method_45773"}, at={@At(value="RETURN")})
    private class_1316 anubis$boatFlyCancelJump(class_1316 original) {
        if (original == null || !BoatFlyModule.cancelsMountJump((class_746)this)) {
            return original;
        }
        return null;
    }
}

