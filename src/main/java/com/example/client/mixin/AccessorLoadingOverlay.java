/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_425
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.example.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_425;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_425.class})
public interface AccessorLoadingOverlay {
    @Accessor(value="field_17770")
    public float anubis$getProgress();

    @Accessor(value="field_17771")
    public long anubis$getFadeOutStart();

    @Accessor(value="field_18220")
    public long anubis$getFadeInStart();

    @Accessor(value="field_18219")
    public boolean anubis$isFadeIn();
}

