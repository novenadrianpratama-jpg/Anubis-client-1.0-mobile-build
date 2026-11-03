/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10444$class_10446
 *  net.minecraft.class_804
 *  org.joml.Vector3fc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.example.client.mixin;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10444;
import net.minecraft.class_804;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10444.class_10446.class})
public interface AccessorHandItemLayer {
    @Accessor(value="field_56967")
    public class_804 anubis$handTransform();

    @Accessor(value="field_56968")
    public Supplier<Vector3fc[]> anubis$handExtents();
}

