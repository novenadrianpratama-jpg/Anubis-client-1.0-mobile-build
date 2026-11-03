/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11403
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.render.PinkWater;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11403;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11403.class})
public abstract class MixinPinkWaterFog {
    @ModifyReturnValue(method={"method_71654"}, at={@At(value="RETURN")})
    private int anubis$pinkWaterFog(int color) {
        return PinkWater.waterFog(color);
    }
}

