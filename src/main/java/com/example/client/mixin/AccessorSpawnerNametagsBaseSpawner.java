/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1917
 *  net.minecraft.class_1952
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.example.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1917;
import net.minecraft.class_1952;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1917.class})
public interface AccessorSpawnerNametagsBaseSpawner {
    @Accessor(value="field_9155")
    public class_1952 anubis$spawnerNametagsNextSpawnData();

    @Accessor(value="field_9158")
    public int anubis$spawnerNametagsRequiredPlayerRange();
}

