/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_155
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.anubis.client.event.EventBus;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.AnubisTitleScreen;
import net.anubis.client.module.impl.misc.AutoEatModule;
import net.anubis.client.util.PerformanceBudget;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_155;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public abstract class MixinMinecraft {
    @Shadow
    public class_638 field_1687;

    @Shadow
    public abstract void method_1507(class_437 var1);

    @Inject(method={"method_1507"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$replaceTitleScreen(class_437 screen, CallbackInfo ci) {
        boolean title;
        if (screen instanceof AnubisTitleScreen) {
            return;
        }
        boolean bl = title = screen instanceof class_442 || screen == null && this.field_1687 == null;
        if (!title || !AnubisTitleScreen.enabled()) {
            return;
        }
        ci.cancel();
        this.method_1507((class_437)new AnubisTitleScreen());
    }

    @Inject(method={"method_24287"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$windowTitle(CallbackInfoReturnable<String> cir) {
        if (!AnubisTitleScreen.enabled()) {
            return;
        }
        cir.setReturnValue((Object)("Minecraft " + class_155.method_16673().comp_4025() + " - Anubis Client"));
    }

    @Inject(method={"method_1574"}, at={@At(value="HEAD")})
    private void anubis$onTickStart(CallbackInfo ci) {
        PerformanceBudget.beginTick();
        EventBus.INSTANCE.drainAsync();
        ServerRotation.startTick();
        TickEvent.dispatch();
    }

    @Inject(method={"method_1574"}, at={@At(value="RETURN")})
    private void anubis$onTickEnd(CallbackInfo ci) {
        TickEvent.dispatchPost();
    }

    @ModifyExpressionValue(method={"method_1583"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_239;method_17783()Lnet/minecraft/class_239$class_240;")})
    private class_239.class_240 anubis$autoEatIgnoreCrosshair(class_239.class_240 type) {
        return AutoEatModule.isEating() ? class_239.class_240.field_1333 : type;
    }
}

