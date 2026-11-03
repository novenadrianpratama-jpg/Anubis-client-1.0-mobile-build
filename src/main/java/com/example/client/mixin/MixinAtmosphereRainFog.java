/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11398
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.AtmosphereModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11398;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11398.class})
public abstract class MixinAtmosphereRainFog {
    @Shadow
    private float field_60589;

    @Inject(method={"method_76304"}, at={@At(value="TAIL")})
    private void anubis$keepClearInRain(class_4184 camera, class_638 level, class_9779 deltaTracker, CallbackInfo ci) {
        AtmosphereModule atmosphere = AtmosphereModule.get();
        if (atmosphere != null && atmosphere.keepsSkyInRain()) {
            this.field_60589 = 0.0f;
        }
    }
}

