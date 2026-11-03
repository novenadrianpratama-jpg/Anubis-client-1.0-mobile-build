/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10255
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.module.impl.movement.entitycontrol.EntityControlVehicleHook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10255;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10255.class})
public abstract class MixinEntityControlAbstractBoat {
    @ModifyExpressionValue(method={"method_64482"}, at={@At(value="FIELD", target="Lnet/minecraft/class_10255;field_54444:Z", opcode=180)})
    private boolean anubis$entityControlInputLeft(boolean original) {
        return original && !EntityControlVehicleHook.locksBoatTurning((class_10255)this);
    }

    @ModifyExpressionValue(method={"method_64482"}, at={@At(value="FIELD", target="Lnet/minecraft/class_10255;field_54428:Z", opcode=180)})
    private boolean anubis$entityControlInputRight(boolean original) {
        return original && !EntityControlVehicleHook.locksBoatTurning((class_10255)this);
    }
}

