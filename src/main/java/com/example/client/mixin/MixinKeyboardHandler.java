/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11908
 *  net.minecraft.class_309
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.anubis.client.AnubisClientMod;
import net.anubis.client.event.impl.KeyInputEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_309.class})
public abstract class MixinKeyboardHandler {
    @WrapMethod(method={"method_1466"})
    private void anubis$openGuiFirst(long window, int action, class_11908 event, Operation<Void> original) {
        boolean guiKey;
        boolean bl = guiKey = action != 0 && AnubisClientMod.isGuiKey(window, event);
        if (guiKey && action == 1 && AnubisClientMod.toggleGuiFromKey()) {
            return;
        }
        original.call(new Object[]{window, action, event});
        if (guiKey) {
            AnubisClientMod.dropGuiClicks();
        }
    }

    @Inject(method={"method_1466"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$onKeyPress(long window, int action, class_11908 event, CallbackInfo ci) {
        if (KeyInputEvent.dispatch(event.comp_4795(), event.comp_4796(), action, event.comp_4797())) {
            ci.cancel();
        }
    }
}

