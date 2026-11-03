/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_761
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.example.client.mixin;

import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.render.BlockOverlayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
public abstract class MixinBlockOverlayLevelRenderer {
    @Inject(method={"method_62210"}, at={@At(value="HEAD")}, cancellable=true)
    private void anubis$hideVanillaOutline(CallbackInfo ci) {
        BlockOverlayModule module = ModuleRegistry.get(BlockOverlayModule.class);
        if (module != null && module.isEnabled()) {
            ci.cancel();
        }
    }
}

