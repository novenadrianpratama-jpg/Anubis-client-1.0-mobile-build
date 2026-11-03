/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11973
 *  net.minecraft.class_1297
 *  net.minecraft.class_898
 *  net.minecraft.class_8984
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.NoRenderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11973;
import net.minecraft.class_1297;
import net.minecraft.class_898;
import net.minecraft.class_8984;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_8984.class})
public abstract class MixinTrialSpawnerRenderer {
    @Inject(method={"method_74396"}, at={@At(value="HEAD")}, cancellable=true)
    private static void anubis$cancelSpawnerMobExtract(class_11973 state, float partialTick, class_1297 displayEntity, class_898 dispatcher, double x, double y, CallbackInfo ci) {
        if (NoRenderModule.hideSpawnerMob()) {
            ci.cancel();
        }
    }
}

