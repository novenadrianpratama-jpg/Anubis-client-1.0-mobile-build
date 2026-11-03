/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4184
 *  net.minecraft.class_757
 *  net.minecraft.class_758
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.example.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.class_757;
import net.minecraft.class_758;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public interface AccessorGameRenderer {
    @Invoker(value="method_3196")
    public float anubis$getFov(class_4184 var1, float var2, boolean var3);

    @Accessor(value="field_60793")
    public class_758 anubis$getFogRenderer();

    @Invoker(value="method_3202")
    public boolean anubis$shouldRenderBlockOutline();
}

