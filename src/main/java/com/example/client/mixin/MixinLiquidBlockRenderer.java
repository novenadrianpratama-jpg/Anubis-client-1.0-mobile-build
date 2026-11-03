/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_775
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.render.PinkWater;
import net.anubis.client.module.impl.render.XrayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_775;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_775.class})
public abstract class MixinLiquidBlockRenderer {
    @ModifyArg(method={"method_23072"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_4588;method_22915(FFFF)Lnet/minecraft/class_4588;"), index=3)
    private float anubis$xrayFluidAlpha(float alpha) {
        if (XrayModule.active == null) {
            return alpha;
        }
        return alpha * XrayModule.CURRENT_ALPHA.get()[0];
    }

    @ModifyReturnValue(method={"method_3343"}, at={@At(value="RETURN")})
    private int anubis$pinkWaterGlow(int light) {
        return PinkWater.light(light);
    }
}

