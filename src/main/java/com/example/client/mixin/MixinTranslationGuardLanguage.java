/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2477
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.util.TranslationGuard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2477;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_2477.class})
public abstract class MixinTranslationGuardLanguage {
    @ModifyReturnValue(method={"method_10517"}, at={@At(value="RETURN")})
    private static class_2477 anubis$guardLanguage(class_2477 original) {
        return TranslationGuard.active() ? TranslationGuard.language(original) : original;
    }
}

