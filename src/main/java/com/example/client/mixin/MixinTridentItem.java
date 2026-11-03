/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1835
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.misc.TridentUtilModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1835;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1835.class})
public abstract class MixinTridentItem {
    @ModifyExpressionValue(method={"method_7836", "method_7840"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_1657;method_5721()Z")}, require=0)
    private boolean anubis$allowOutOfWater(boolean original) {
        return original || TridentUtilModule.allowOutOfWater();
    }

    @ModifyExpressionValue(method={"method_7840"}, at={@At(value="CONSTANT", args={"intValue=10"})}, require=0)
    private int anubis$scaleMinCharge(int original) {
        return TridentUtilModule.minChargeTicks(original);
    }
}

