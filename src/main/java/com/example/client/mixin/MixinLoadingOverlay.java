/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_425
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 */
package com.example.client.mixin;

import net.anubis.client.gui.LoadingScreens;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_425;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_425.class})
public abstract class MixinLoadingOverlay {
    @ModifyArg(method={"method_25394"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_332;method_25294(IIIII)V"), index=4)
    private int anubis$backgroundFill(int argb) {
        return LoadingScreens.backgroundColor(argb);
    }

    @ModifyArg(method={"method_25394"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorTexture(Lcom/mojang/blaze3d/textures/GpuTexture;I)V"), index=1)
    private int anubis$backgroundClear(int argb) {
        return LoadingScreens.backgroundColor(argb);
    }
}

