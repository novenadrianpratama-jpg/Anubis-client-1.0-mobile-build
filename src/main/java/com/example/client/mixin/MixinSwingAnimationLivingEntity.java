/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.render.SwingAnimationModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1309.class})
public abstract class MixinSwingAnimationLivingEntity {
    @ModifyExpressionValue(method={"method_6119", "method_23667"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_1309;method_6028()I")})
    private int anubis$swingTempo(int ticks) {
        SwingAnimationModule module = SwingAnimationModule.active;
        if (module == null || this != class_310.method_1551().field_1724) {
            return ticks;
        }
        return module.swingTicks(ticks);
    }

    @Inject(method={"method_23667"}, at={@At(value="FIELD", target="Lnet/minecraft/class_1309;field_6252:Z", opcode=181)})
    private void anubis$latchSwing(class_1268 hand, boolean broadcast, CallbackInfo ci) {
        SwingAnimationModule module = SwingAnimationModule.active;
        if (module != null && this == class_310.method_1551().field_1724) {
            module.latch();
        }
    }
}

