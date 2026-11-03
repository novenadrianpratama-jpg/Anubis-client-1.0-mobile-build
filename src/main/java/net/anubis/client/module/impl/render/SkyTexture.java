/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_12137
 *  net.minecraft.class_9848
 *  org.lwjgl.system.MemoryUtil
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.IntBuffer;
import net.anubis.client.module.impl.render.SkyImage;
import net.anubis.client.module.impl.render.SkyMips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_12137;
import net.minecraft.class_9848;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
final class SkyTexture
implements SkyImage {
    private final int width;
    private final int height;
    private boolean mipmapped;
    private GpuTexture texture;
    private GpuTextureView view;

    private SkyTexture(int width, int height, GpuTexture texture, GpuTextureView view) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.view = view;
    }

    static SkyTexture upload(String name, int width, int height, int[][] levels) {
        if (levels.length <= 1) {
            return SkyTexture.upload(name, width, height, levels.length == 0 ? new int[]{} : levels[0]);
        }
        for (int level = 0; level < levels.length; ++level) {
            if (levels[level].length >= SkyMips.size(width, level) * SkyMips.size(height, level)) continue;
            throw new IllegalArgumentException("Sky texture " + name + " is missing pixels at mip level " + level);
        }
        String label = "anubis:sky/" + name;
        GpuDevice device = RenderSystem.getDevice();
        GpuTexture texture = device.createTexture(() -> label, 5, TextureFormat.RGBA8, width, height, 1, levels.length);
        try {
            for (int level = 0; level < levels.length; ++level) {
                int w = SkyMips.size(width, level);
                int h = SkyMips.size(height, level);
                int[] argb = levels[level];
                try (class_1011 image = new class_1011(w, h, false);){
                    IntBuffer pixels = MemoryUtil.memIntBuffer((long)image.method_67769(), (int)(w * h));
                    for (int i = 0; i < w * h; ++i) {
                        pixels.put(i, class_9848.method_61337((int)argb[i]));
                    }
                    device.createCommandEncoder().writeToTexture(texture, image, level, 0, 0, 0, w, h, 0, 0);
                    continue;
                }
            }
            SkyTexture made = new SkyTexture(width, height, texture, device.createTextureView(texture));
            made.mipmapped = true;
            return made;
        }
        catch (RuntimeException e) {
            texture.close();
            throw e;
        }
    }

    static SkyTexture upload(String name, int width, int height, int[] argb) {
        if (argb.length < width * height) {
            throw new IllegalArgumentException("Sky texture " + name + " is missing pixels");
        }
        String label = "anubis:sky/" + name;
        GpuDevice device = RenderSystem.getDevice();
        try (class_1011 image = new class_1011(width, height, false);){
            IntBuffer pixels = MemoryUtil.memIntBuffer((long)image.method_67769(), (int)(width * height));
            for (int i = 0; i < width * height; ++i) {
                pixels.put(i, class_9848.method_61337((int)argb[i]));
            }
            GpuTexture texture = device.createTexture(() -> label, 5, TextureFormat.RGBA8, width, height, 1, 1);
            try {
                device.createCommandEncoder().writeToTexture(texture, image);
                SkyTexture skyTexture = new SkyTexture(width, height, texture, device.createTextureView(texture));
                return skyTexture;
            }
            catch (RuntimeException e) {
                texture.close();
                throw e;
            }
        }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    GpuTextureView view() {
        return this.view;
    }

    class_12137 sampler() {
        return RenderSystem.getSamplerCache().method_76520(FilterMode.LINEAR, this.mipmapped);
    }

    @Override
    public void close() {
        if (this.view != null) {
            this.view.close();
        }
        if (this.texture != null) {
            this.texture.close();
        }
        this.view = null;
        this.texture = null;
    }
}

