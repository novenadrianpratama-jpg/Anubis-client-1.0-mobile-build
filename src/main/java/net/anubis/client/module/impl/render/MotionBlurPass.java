/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_6367
 *  org.lwjgl.BufferUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class MotionBlurPass {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"anubis", (String)"pipeline/motion_blur")).withVertexShader("core/screenquad").withFragmentShader(class_2960.method_60655((String)"anubis", (String)"core/motion_blur")).withSampler("InSampler").withUniform("MotionBlurConfig", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withColorWrite(true, false).withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).build();
    private static final Supplier<String> PASS_LABEL = () -> "Anubis motion blur";
    private static final Supplier<String> CONFIG_LABEL = () -> "Anubis motion blur config";
    private static final int CONFIG_SIZE = 16;
    private static final long STALE_NANOS = 1000000000L;
    private static final float MIN_RETENTION = 0.003921569f;
    private static class_6367 history;
    private static GpuBuffer config;
    private static GpuBufferSlice configSlice;
    private static ByteBuffer configData;
    private static boolean primed;
    private static long lastFrameNanos;
    private static int levelKey;
    private static int seed;
    private static boolean invalidShaderLogged;

    private MotionBlurPass() {
    }

    static void apply(boolean worldRendered, float retention60) {
        float retention;
        class_310 mc = class_310.method_1551();
        class_276 main = mc.method_1522();
        GpuTexture mainColor = main.method_30277();
        if (!worldRendered || mc.field_1687 == null || !mc.method_53466() || mc.field_1773.method_35765() || mainColor == null || main.field_1482 <= 0 || main.field_1481 <= 0) {
            primed = false;
            return;
        }
        long now = System.nanoTime();
        long gap = now - lastFrameNanos;
        lastFrameNanos = now;
        int level = System.identityHashCode(mc.field_1687);
        if (level != levelKey) {
            levelKey = level;
            primed = false;
        }
        if (history == null) {
            MotionBlurPass.allocate(main.field_1482, main.field_1481);
            primed = false;
        } else if (MotionBlurPass.history.field_1482 != main.field_1482 || MotionBlurPass.history.field_1481 != main.field_1481) {
            history.method_1234(main.field_1482, main.field_1481);
            primed = false;
        }
        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder encoder = device.createCommandEncoder();
        if (primed && gap > 0L && gap < 1000000000L && (retention = (float)Math.pow(retention60, (double)gap * 6.0E-8)) >= 0.003921569f && MotionBlurPass.shaderReady(device)) {
            MotionBlurPass.blend(encoder, main, retention);
        }
        encoder.copyTextureToTexture(mainColor, history.method_30277(), 0, 0, 0, 0, 0, main.field_1482, main.field_1481);
        primed = true;
    }

    private static void blend(CommandEncoder encoder, class_276 main, float retention) {
        seed = seed + 1 & 0x3F;
        configData.putFloat(0, retention);
        configData.putFloat(4, (float)seed * 7.0f);
        encoder.writeToBuffer(configSlice, configData);
        try (RenderPass pass = encoder.createRenderPass(PASS_LABEL, main.method_71639(), OptionalInt.empty());){
            pass.setPipeline(PIPELINE);
            pass.setUniform("MotionBlurConfig", configSlice);
            pass.bindTexture("InSampler", history.method_71639(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
            pass.draw(0, 3);
        }
    }

    private static boolean shaderReady(GpuDevice device) {
        if (device.precompilePipeline(PIPELINE).isValid()) {
            invalidShaderLogged = false;
            return true;
        }
        if (!invalidShaderLogged) {
            invalidShaderLogged = true;
            LOGGER.warn("Motion Blur shader {} did not compile; frames are passed through unblurred", (Object)PIPELINE.getFragmentShader());
        }
        return false;
    }

    private static void allocate(int width, int height) {
        history = new class_6367("Anubis motion blur history", width, height, false);
        config = RenderSystem.getDevice().createBuffer(CONFIG_LABEL, 136, 16L);
        configSlice = config.slice();
        configData = BufferUtils.createByteBuffer((int)16);
    }

    static void release() {
        if (history != null) {
            history.method_1238();
            history = null;
        }
        if (config != null) {
            config.close();
            config = null;
        }
        configSlice = null;
        configData = null;
        primed = false;
        lastFrameNanos = 0L;
        levelKey = 0;
    }
}

