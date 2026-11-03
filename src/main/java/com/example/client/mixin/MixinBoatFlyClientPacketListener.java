/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2692
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.impl.movement.BoatFlyModule;
import net.anubis.client.module.impl.movement.BoatNoclipModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2692;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_634.class})
public abstract class MixinBoatFlyClientPacketListener {
    @Inject(method={"method_11134"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_2833;method_65307(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2833;")})
    private void anubis$boatFlyVehicleSetback(class_2692 packet, CallbackInfo ci) {
        BoatNoclipModule noclip;
        BoatFlyModule fly = BoatFlyModule.active;
        if (fly != null) {
            fly.onVehicleSetback();
        }
        if ((noclip = BoatNoclipModule.active) != null) {
            noclip.onVehicleSetback();
        }
    }
}

