/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_4050
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.movement.ElytraFlyModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_4050;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public abstract class MixinElytraFlyEntity {
    @ModifyReturnValue(method={"method_18376"}, at={@At(value="RETURN")})
    private class_4050 anubis$elytraFlyGetPose(class_4050 original) {
        ElytraFlyModule module = ElytraFlyModule.active;
        if (module == null) {
            return original;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (this != player || !mc.method_18854()) {
            return original;
        }
        return module.canPacketEfly(player) ? class_4050.field_18077 : original;
    }
}

