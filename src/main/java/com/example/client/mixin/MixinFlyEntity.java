/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.movement.FlyModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public abstract class MixinFlyEntity {
    @ModifyReturnValue(method={"method_5799"}, at={@At(value="RETURN")})
    private boolean anubis$flyIsInWater(boolean original) {
        return original && !this.anubis$flyAffects();
    }

    @ModifyReturnValue(method={"method_5771"}, at={@At(value="RETURN")})
    private boolean anubis$flyIsInLava(boolean original) {
        return original && !this.anubis$flyAffects();
    }

    @ModifyExpressionValue(method={"method_5790"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_1297;method_5869()Z")})
    private boolean anubis$flyIsUnderWater(boolean original) {
        return original && !this.anubis$flyAffects();
    }

    @Unique
    private boolean anubis$flyAffects() {
        return FlyModule.active != null && this == class_310.method_1551().field_1724;
    }
}

