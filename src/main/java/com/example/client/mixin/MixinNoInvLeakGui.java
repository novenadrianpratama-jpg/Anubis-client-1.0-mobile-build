/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.WrapWithCondition
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.anubis.client.module.impl.misc.NoInvLeakModule;
import net.anubis.client.module.impl.misc.noinvleak.StreamOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_329.class})
public abstract class MixinNoInvLeakGui {
    @Unique
    private static final class_2960 ANUBIS$SELECTION = class_2960.method_60656((String)"hud/hotbar_selection");
    @Shadow
    private int field_2040;
    @Shadow
    private class_1799 field_2031;

    @ModifyVariable(method={"method_1762"}, at=@At(value="HEAD"), argsOnly=true)
    private class_1799 anubis$noInvLeakHotbar(class_1799 stack, @Local(argsOnly=true) class_332 guiGraphics, @Local(argsOnly=true, ordinal=0) int x, @Local(argsOnly=true, ordinal=1) int y, @Local(argsOnly=true, ordinal=2) int seed) {
        return NoInvLeakModule.hotbar(stack, NoInvLeakModule.screenX(guiGraphics, x, y, x), NoInvLeakModule.screenY(guiGraphics, x, y, y), seed);
    }

    @Inject(method={"method_1749"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$noInvLeakItemName(class_332 guiGraphics, CallbackInfo ci) {
        if (!NoInvLeakModule.hidesItemName()) {
            return;
        }
        if (NoInvLeakModule.overlaying() && this.field_2040 > 0 && !this.field_2031.method_7960()) {
            int alpha = Math.min(255, (int)((float)this.field_2040 * 256.0f / 10.0f));
            int y = guiGraphics.method_51443() - 59;
            if (!class_310.method_1551().field_1761.method_2908()) {
                y += 14;
            }
            StreamOverlay.itemName(this.field_2031, alpha, y);
        }
        ci.cancel();
    }

    @WrapWithCondition(method={"method_1759"}, at={@At(value="INVOKE", target="Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V")})
    private boolean anubis$noInvLeakSelection(class_332 guiGraphics, RenderPipeline pipeline, class_2960 sprite, int x, int y, int width, int height) {
        if (!NoInvLeakModule.hidesSelectedSlot() || !ANUBIS$SELECTION.equals((Object)sprite)) {
            return true;
        }
        if (NoInvLeakModule.overlaying()) {
            StreamOverlay.selection(x, y);
        }
        return false;
    }
}

