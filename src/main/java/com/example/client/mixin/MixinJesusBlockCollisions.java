/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1941
 *  net.minecraft.class_2338
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_3726
 *  net.minecraft.class_5329
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anubis.client.module.impl.movement.JesusModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1941;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3726;
import net.minecraft.class_5329;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_5329.class})
public abstract class MixinJesusBlockCollisions {
    @WrapOperation(method={"computeNext"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_3726;method_62878(Lnet/minecraft/class_2680;Lnet/minecraft/class_1941;Lnet/minecraft/class_2338;)Lnet/minecraft/class_265;")})
    private class_265 anubis$jesusCollisionShape(class_3726 context, class_2680 state, class_1941 level, class_2338 pos, Operation<class_265> original) {
        class_265 shape = (class_265)original.call(new Object[]{context, state, level, pos});
        JesusModule jesus = JesusModule.active;
        return jesus == null ? shape : jesus.collisionShape(level, state, pos, shape);
    }
}

