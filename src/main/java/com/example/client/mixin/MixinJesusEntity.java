/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.movement.JesusModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public abstract class MixinJesusEntity {
    @Inject(method={"method_5700"}, at={@At(value="HEAD")})
    private void anubis$jesusAboveBubbleColumn(CallbackInfo ci) {
        this.anubis$jesusMarkBubbleColumn();
    }

    @Inject(method={"method_5764"}, at={@At(value="HEAD")})
    private void anubis$jesusInsideBubbleColumn(CallbackInfo ci) {
        this.anubis$jesusMarkBubbleColumn();
    }

    @Unique
    private void anubis$jesusMarkBubbleColumn() {
        JesusModule jesus = JesusModule.active;
        if (jesus != null && this == class_310.method_1551().field_1724) {
            jesus.markInBubbleColumn();
        }
    }
}

