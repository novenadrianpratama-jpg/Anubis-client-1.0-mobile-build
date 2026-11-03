/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_897
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.misc.NameProtectModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_897.class})
public abstract class MixinNameProtectEntityRenderer {
    @ModifyExpressionValue(method={"method_62354"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_897;method_62426(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2561;")})
    private class_2561 anubis$nameProtectNameTag(class_2561 original) {
        return NameProtectModule.protect(original);
    }
}

