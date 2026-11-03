/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10515$class_10516
 *  net.minecraft.class_10517
 *  net.minecraft.class_2960
 *  net.minecraft.class_5699$class_10388
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.mojang.serialization.MapCodec;
import net.anubis.client.gui.GlintHeadRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10515;
import net.minecraft.class_10517;
import net.minecraft.class_2960;
import net.minecraft.class_5699;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10517.class})
public abstract class MixinGlintHeadSpecialModelRenderers {
    @Shadow
    @Final
    private static class_5699.class_10388<class_2960, MapCodec<? extends class_10515.class_10516>> field_55453;

    @Inject(method={"method_65712"}, at={@At(value="TAIL")})
    private static void anubis$registerGlintHead(CallbackInfo ci) {
        field_55453.method_65325((Object)GlintHeadRenderer.ID, GlintHeadRenderer.Unbaked.MAP_CODEC);
    }
}

