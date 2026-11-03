/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1540
 *  net.minecraft.class_4604
 *  net.minecraft.class_901
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.render.NoRenderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1540;
import net.minecraft.class_4604;
import net.minecraft.class_901;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_901.class})
public abstract class MixinFallingBlockRenderer {
    @Inject(method={"method_62436"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$cancelFallingBlock(class_1540 entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (NoRenderModule.hideFallingBlocks()) {
            cir.setReturnValue((Object)false);
        }
    }
}

