/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_638
 *  net.minecraft.class_638$class_5271
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.anubis.client.module.impl.render.TimeChangerModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_638.class})
public abstract class MixinTimeChangerClientLevel {
    @WrapOperation(method={"method_29089"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_638$class_5271;method_165(J)V")})
    private void anubis$timeChangerServerDayTime(class_638.class_5271 levelData, long dayTime, Operation<Void> original) {
        TimeChangerModule timeChanger = TimeChangerModule.active;
        if (timeChanger == null) {
            original.call(new Object[]{levelData, dayTime});
            return;
        }
        class_638 level = (class_638)this;
        long shown = levelData.method_217();
        long forced = timeChanger.interceptServerDayTime(level, dayTime);
        original.call(new Object[]{levelData, forced});
        if (forced != shown) {
            level.method_75728().method_76405();
        }
    }
}

