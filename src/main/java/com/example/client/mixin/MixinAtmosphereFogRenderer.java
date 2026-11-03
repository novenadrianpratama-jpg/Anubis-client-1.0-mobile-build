/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_4184
 *  net.minecraft.class_5636
 *  net.minecraft.class_638
 *  net.minecraft.class_758
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.AtmosphereModule;
import net.anubis.client.module.impl.render.PeepWorld;
import net.anubis.client.module.impl.render.PinkWater;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import net.minecraft.class_638;
import net.minecraft.class_758;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_758.class})
public abstract class MixinAtmosphereFogRenderer {
    @Inject(method={"method_62185"}, at={@At(value="RETURN")})
    private void anubis$fogColour(class_4184 camera, float partialTick, class_638 level, int renderDistance, float darkness, CallbackInfoReturnable<Vector4f> cir) {
        class_1309 living;
        class_5636 fluid = camera.method_19334();
        if (fluid == class_5636.field_27886) {
            PinkWater.underwater((Vector4f)cir.getReturnValue());
        }
        if (fluid != class_5636.field_27888 || darkness > 0.0f) {
            return;
        }
        class_1297 class_12972 = camera.method_19331();
        if (class_12972 instanceof class_1309 && ((living = (class_1309)class_12972).method_6059(class_1294.field_5919) || living.method_6059(class_1294.field_38092))) {
            return;
        }
        AtmosphereModule atmosphere = AtmosphereModule.get();
        if (atmosphere != null) {
            atmosphere.fog(camera, level, (Vector4f)cir.getReturnValue());
        }
        PeepWorld.fog((Vector4f)cir.getReturnValue());
    }
}

