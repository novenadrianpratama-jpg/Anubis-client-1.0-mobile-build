/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12250
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.render.CustomGlintModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12250;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_12250.class})
public abstract class MixinCustomGlintTextureTransform {
    @ModifyVariable(method={"method_76031"}, at=@At(value="HEAD"), argsOnly=true)
    private static float anubis$glintPatternScale(float scale) {
        CustomGlintModule module = CustomGlintModule.active;
        return module == null ? scale : scale * module.uvScaleMultiplier();
    }

    @ModifyExpressionValue(method={"method_76031"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_156;method_658()J")})
    private static long anubis$glintClock(long millis) {
        CustomGlintModule module = CustomGlintModule.active;
        return module == null ? millis : module.glintMillis(millis);
    }
}

