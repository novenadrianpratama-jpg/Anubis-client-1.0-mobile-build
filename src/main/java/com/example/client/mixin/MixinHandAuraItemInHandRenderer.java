/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11684
 *  net.minecraft.class_310
 *  net.minecraft.class_759
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anubis.client.module.impl.render.HandAuraModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11684;
import net.minecraft.class_310;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_759.class})
public abstract class MixinHandAuraItemInHandRenderer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @WrapOperation(method={"method_22976"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_11684;method_73002()V")})
    private void anubis$handAura(class_11684 features, Operation<Void> original) {
        if (HandAuraModule.active == null || !HandAuraModule.beginCapture()) {
            original.call(new Object[]{features});
            return;
        }
        boolean drawn = false;
        try {
            original.call(new Object[]{features});
            class_310.method_1551().method_22940().method_23000().method_22993();
            drawn = true;
        }
        finally {
            HandAuraModule.endCapture(drawn);
        }
    }
}

