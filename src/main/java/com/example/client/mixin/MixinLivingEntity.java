/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.example.client.mixin;

import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.SlowSwingModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1309.class})
public abstract class MixinLivingEntity {
    @Inject(method={"method_6028"}, at={@At(value="RETURN")}, cancellable=true)
    private void anubis$modifySwingDuration(CallbackInfoReturnable<Integer> cir) {
        SlowSwingModule module = ModuleRegistry.get(SlowSwingModule.class);
        if (module == null || !module.isEnabled()) {
            return;
        }
        if (this != class_310.method_1551().field_1724) {
            return;
        }
        cir.setReturnValue((Object)module.getModifiedSwingDuration((Integer)cir.getReturnValue()));
    }
}

