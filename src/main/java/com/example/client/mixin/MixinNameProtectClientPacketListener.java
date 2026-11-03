/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2703
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.misc.NameProtectModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2703;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_634.class})
public abstract class MixinNameProtectClientPacketListener {
    @Inject(method={"method_11113"}, at={@At(value="TAIL")})
    private void anubis$nameProtectPlayerList(class_2703 packet, CallbackInfo ci) {
        NameProtectModule.onPlayerInfoApplied(packet);
    }

    @ModifyExpressionValue(method={"method_34075"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_5892;comp_2276()Lnet/minecraft/class_2561;")})
    private class_2561 anubis$nameProtectDeathMessage(class_2561 message) {
        return NameProtectModule.protect(message);
    }
}

