/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1959
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.example.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.anubis.client.module.impl.render.PinkWater;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1959;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1959.class})
public abstract class MixinPinkWaterBiome {
    @Unique
    private volatile byte anubis$cave;

    @ModifyReturnValue(method={"method_8687"}, at={@At(value="RETURN")})
    private int anubis$pinkWater(int color) {
        if (!PinkWater.shows()) {
            return color;
        }
        byte cave = this.anubis$cave;
        if (cave == 0 && (cave = PinkWater.caveBiome((class_1959)this)) != 0) {
            this.anubis$cave = cave;
        }
        return PinkWater.water(color, cave == 2);
    }
}

