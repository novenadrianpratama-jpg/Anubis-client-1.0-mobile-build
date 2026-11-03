/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.event.impl.BlockUpdateEvent;
import net.anubis.client.event.impl.WorldEntityAddEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_638.class})
public abstract class MixinClientLevel {
    @Inject(method={"method_53875"}, at={@At(value="HEAD")})
    private void anubis$onEntityAdd(class_1297 entity, CallbackInfo ci) {
        WorldEntityAddEvent.dispatch(entity);
    }

    @Inject(method={"method_8413"}, at={@At(value="HEAD")})
    private void anubis$onBlockUpdated(class_2338 pos, class_2680 oldState, class_2680 newState, int flags, CallbackInfo ci) {
        BlockUpdateEvent.dispatch(pos, oldState, newState);
    }
}

