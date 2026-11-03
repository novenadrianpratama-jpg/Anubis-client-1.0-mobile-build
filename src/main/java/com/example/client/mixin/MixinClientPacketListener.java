/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2663
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2678
 *  net.minecraft.class_2743
 *  net.minecraft.class_634
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.event.impl.ChatSendEvent;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2663;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2678;
import net.minecraft.class_2743;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_634.class})
public abstract class MixinClientPacketListener {
    @Inject(method={"method_45729"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$onSendChat(String message, CallbackInfo ci) {
        if (ChatSendEvent.dispatch(message)) {
            ci.cancel();
        }
    }

    @Inject(method={"method_11148"}, at={@At(value="TAIL")})
    private void anubis$afterEntityEvent(class_2663 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11120"}, at={@At(value="TAIL")})
    private void anubis$afterLogin(class_2678 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11128"}, at={@At(value="TAIL")})
    private void anubis$afterChunkData(class_2672 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11136"}, at={@At(value="TAIL")})
    private void anubis$afterBlockUpdate(class_2626 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11100"}, at={@At(value="TAIL")})
    private void anubis$afterSectionUpdate(class_2637 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11143"}, at={@At(value="TAIL")})
    private void anubis$afterLightUpdate(class_2676 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }

    @Inject(method={"method_11132"}, at={@At(value="TAIL")})
    private void anubis$afterEntityMotion(class_2743 packet, CallbackInfo ci) {
        PacketAppliedEvent.dispatch(packet);
    }
}

