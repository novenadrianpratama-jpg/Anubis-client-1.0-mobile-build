/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12125
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.combat.FriendGuardModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12125;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_636.class})
public abstract class MixinFriendGuardMultiPlayerGameMode {
    @Inject(method={"method_2918"}, at={@At(value="HEAD")}, cancellable=true, order=900)
    private void anubis$friendGuard(class_1657 player, class_1297 target, CallbackInfo ci) {
        if (FriendGuardModule.intercepts(player, target)) {
            player.method_7350();
            ci.cancel();
        }
    }

    @Inject(method={"method_75407"}, at={@At(value="HEAD")}, cancellable=true, order=900)
    private void anubis$friendGuardStab(class_12125 weapon, CallbackInfo ci) {
        if (FriendGuardModule.interceptsStab(class_310.method_1551().field_1724)) {
            ci.cancel();
        }
    }
}

