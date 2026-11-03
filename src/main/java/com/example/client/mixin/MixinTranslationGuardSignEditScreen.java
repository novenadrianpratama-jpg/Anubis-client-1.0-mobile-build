/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2625
 *  net.minecraft.class_7743
 *  net.minecraft.class_8242
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.util.TranslationGuard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2625;
import net.minecraft.class_7743;
import net.minecraft.class_8242;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_7743.class})
public abstract class MixinTranslationGuardSignEditScreen {
    @Shadow
    private class_8242 field_43362;
    @Shadow
    @Final
    private String[] field_40425;

    @Inject(method={"<init>(Lnet/minecraft/class_2625;ZZLnet/minecraft/class_2561;)V"}, at={@At(value="TAIL")})
    private void anubis$guardMessages(class_2625 sign, boolean front, boolean filtered, class_2561 title, CallbackInfo ci) {
        for (int line = 0; line < this.field_40425.length; ++line) {
            this.field_40425[line] = TranslationGuard.plain(this.field_43362.method_49859(line, filtered));
        }
    }
}

