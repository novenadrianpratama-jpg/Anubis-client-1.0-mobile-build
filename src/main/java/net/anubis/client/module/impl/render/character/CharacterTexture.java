/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1044
 */
package net.anubis.client.module.impl.render.character;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1044;

@Environment(value=EnvType.CLIENT)
final class CharacterTexture
extends class_1044 {
    CharacterTexture(String label, int width, int height, int[][] mips) {
        GpuDevice device = RenderSystem.getDevice();
        this.field_56974 = device.createTexture(() -> label, 5, TextureFormat.RGBA8, width, height, 1, mips.length);
        this.field_60597 = device.createTextureView(this.field_56974);
        CommandEncoder encoder = device.createCommandEncoder();
        int w = width;
        int h = height;
        for (int level = 0; level < mips.length; ++level) {
            int[] pixels = mips[level];
            try (class_1011 image = new class_1011(w, h, false);){
                for (int y = 0; y < h; ++y) {
                    for (int x = 0; x < w; ++x) {
                        image.method_61941(x, y, pixels[y * w + x]);
                    }
                }
                encoder.writeToTexture(this.field_56974, image, level, 0, 0, 0, w, h, 0, 0);
            }
            w = Math.max(1, w / 2);
            h = Math.max(1, h / 2);
        }
        this.field_63613 = RenderSystem.getSamplerCache().method_76520(FilterMode.LINEAR, true);
    }
}

