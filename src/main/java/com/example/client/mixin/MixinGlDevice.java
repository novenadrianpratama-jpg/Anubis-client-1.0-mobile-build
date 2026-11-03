/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10874
 *  org.lwjgl.opengl.GL11
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.example.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10874;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10874.class})
public class MixinGlDevice {
    @Redirect(method={"method_68813"}, at=@At(value="FIELD", target="Lnet/minecraft/class_10865;field_58039:Z", opcode=178))
    private static boolean anubis$blockDsaForAmd() {
        String renderer = GL11.glGetString((int)7937);
        return renderer == null || !renderer.contains("AMD");
    }
}

