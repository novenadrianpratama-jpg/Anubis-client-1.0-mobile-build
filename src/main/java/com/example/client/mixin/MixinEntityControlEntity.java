/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1313
 *  net.minecraft.class_243
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.movement.entitycontrol.EntityControlVehicleHook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1297.class})
public abstract class MixinEntityControlEntity {
    @ModifyVariable(method={"method_5784"}, at=@At(value="HEAD"), argsOnly=true)
    private class_243 anubis$entityControlVehicleMove(class_243 movement, class_1313 type) {
        if (!EntityControlVehicleHook.anyActive) {
            return movement;
        }
        return EntityControlVehicleHook.onEntityMove((class_1297)this, type, movement);
    }
}

