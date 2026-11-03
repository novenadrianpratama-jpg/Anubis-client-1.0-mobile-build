/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_368
 *  net.minecraft.class_368$class_369
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.example.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_368;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(value=EnvType.CLIENT)
@Mixin(targets={"net/minecraft/class_374$class_375"})
public interface AccessorHudToastInstance {
    @Accessor(value="field_2241")
    public class_368 anubis$hudToast();

    @Accessor(value="field_52787")
    public int anubis$hudFirstSlot();

    @Accessor(value="field_2244")
    public class_368.class_369 anubis$hudVisibility();

    @Accessor(value="field_52789")
    public float anubis$hudVisiblePortion();
}

