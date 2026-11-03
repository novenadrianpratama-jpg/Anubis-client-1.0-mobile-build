/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11401
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_7285
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.NoRenderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11401;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11401.class})
public abstract class MixinNoRenderLavaFog {
    @Inject(method={"method_42591"}, at={@At(value="TAIL")})
    private void anubis$clearLavaFog(class_7285 fog, class_4184 camera, class_638 level, float renderDistance, class_9779 deltaTracker, CallbackInfo ci) {
        if (!NoRenderModule.hideLavaFog()) {
            return;
        }
        fog.field_60582 = -8.0f;
        fog.field_60584 = renderDistance;
        fog.field_60099 = renderDistance;
        fog.field_60100 = renderDistance;
    }
}

