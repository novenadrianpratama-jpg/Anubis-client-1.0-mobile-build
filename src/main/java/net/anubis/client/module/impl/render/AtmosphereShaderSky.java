/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_9799
 *  net.minecraft.class_9801
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.BufferUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_9799;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class AtmosphereShaderSky {
    static final String OFF = "Off";
    static final String SPACE = "Space";
    static final String NORTHERN_LIGHTS = "Northern Lights";
    static final String RINGED_PLANET = "Ringed Planet";
    static final String BLACK_HOLE = "Black Hole";
    private static final List<String> THEMES = List.of("Space", "Northern Lights", "Ringed Planet", "Black Hole");
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"anubis", (String)"pipeline/sky_theme")).withVertexShader(class_2960.method_60655((String)"anubis", (String)"core/sky_theme")).withFragmentShader(class_2960.method_60655((String)"anubis", (String)"core/sky_theme")).withUniform("AnubisSky", class_10789.field_60031).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27382).withBlend(BlendFunction.TRANSLUCENT).build();
    private static final int UNIFORM_SIZE = 112;
    private static final float CUBE = 10.0f;
    private static final int CUBE_INDICES = 36;
    private static final float MAX_STEP = 0.1f;
    private static final float TIME_WRAP = 4096.0f;
    private GpuBuffer cube;
    private GpuBuffer uniforms;
    private ByteBuffer uniformData;
    private boolean invalidLogged;
    private float time;
    private long lastNanos;

    AtmosphereShaderSky() {
    }

    boolean render(String theme, float visibility, float brightness, float speed, float stars, float effect, float band, int low, int high, int bandColor, int zenith, int horizon) {
        int mode = THEMES.indexOf(theme);
        long now = System.nanoTime();
        float dt = this.lastNanos == 0L ? 0.0f : Math.min((float)(now - this.lastNanos) / 1.0E9f, 0.1f);
        this.lastNanos = now;
        this.time = (this.time + dt * speed) % 4096.0f;
        if (mode < 0 || visibility <= 0.002f) {
            return false;
        }
        GpuDevice device = RenderSystem.getDevice();
        if (!device.precompilePipeline(PIPELINE).isValid()) {
            if (!this.invalidLogged) {
                this.invalidLogged = true;
                LOGGER.warn("[Anubis] Atmosphere sky shader {} did not compile; using the gradient sky", (Object)PIPELINE.getFragmentShader());
            }
            return false;
        }
        class_276 target = class_310.method_1551().method_1522();
        if (target.field_1482 <= 0 || target.field_1481 <= 0) {
            return false;
        }
        if (this.cube == null) {
            this.cube = AtmosphereShaderSky.buildCube(device);
        }
        if (this.uniforms == null) {
            this.uniforms = device.createBuffer(() -> "Anubis sky theme settings", 136, 112L);
            this.uniformData = BufferUtils.createByteBuffer((int)112);
        }
        this.putVec(0, mode, visibility, this.time, brightness);
        this.putVec(16, stars, effect, band, 1.0f);
        this.putColor(32, low);
        this.putColor(48, high);
        this.putColor(64, bandColor);
        this.putColor(80, zenith);
        this.putColor(96, horizon);
        CommandEncoder encoder = device.createCommandEncoder();
        encoder.writeToBuffer(this.uniforms.slice(), this.uniformData);
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewStack(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        RenderSystem.class_5590 indices = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
        try (RenderPass pass = encoder.createRenderPass(() -> "Anubis sky theme", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
            this.drawSky(pass, PIPELINE, transforms, indices);
        }
        return true;
    }

    private void drawSky(RenderPass pass, RenderPipeline pipeline, GpuBufferSlice transforms, RenderSystem.class_5590 indices) {
        pass.setPipeline(pipeline);
        RenderSystem.bindDefaultUniforms((RenderPass)pass);
        pass.setUniform("DynamicTransforms", transforms);
        pass.setUniform("AnubisSky", this.uniforms.slice());
        pass.setVertexBuffer(0, this.cube);
        pass.setIndexBuffer(indices.method_68274(36), indices.method_31924());
        pass.drawIndexed(0, 0, 36, 1);
    }

    void close() {
        if (this.cube != null) {
            this.cube.close();
        }
        if (this.uniforms != null) {
            this.uniforms.close();
        }
        this.cube = null;
        this.uniforms = null;
        this.uniformData = null;
        this.lastNanos = 0L;
        this.invalidLogged = false;
    }

    private void putVec(int offset, float x, float y, float z, float w) {
        this.uniformData.putFloat(offset, x);
        this.uniformData.putFloat(offset + 4, y);
        this.uniformData.putFloat(offset + 8, z);
        this.uniformData.putFloat(offset + 12, w);
    }

    private void putColor(int offset, int rgb) {
        this.putVec(offset, (float)(rgb >> 16 & 0xFF) / 255.0f, (float)(rgb >> 8 & 0xFF) / 255.0f, (float)(rgb & 0xFF) / 255.0f, 1.0f);
    }

    private static GpuBuffer buildCube(GpuDevice device) {
        try (class_9799 bytes = new class_9799(24 * class_290.field_1592.getVertexSize());){
            GpuBuffer gpuBuffer;
            block11: {
                class_287 builder = new class_287(bytes, VertexFormat.class_5596.field_27382, class_290.field_1592);
                float s = 10.0f;
                AtmosphereShaderSky.face(builder, s, -s, -s, s, s, -s, s, s, s, s, -s, s);
                AtmosphereShaderSky.face(builder, -s, -s, s, -s, s, s, -s, s, -s, -s, -s, -s);
                AtmosphereShaderSky.face(builder, -s, s, -s, -s, s, s, s, s, s, s, s, -s);
                AtmosphereShaderSky.face(builder, -s, -s, s, -s, -s, -s, s, -s, -s, s, -s, s);
                AtmosphereShaderSky.face(builder, s, -s, s, s, s, s, -s, s, s, -s, -s, s);
                AtmosphereShaderSky.face(builder, -s, -s, -s, -s, s, -s, s, s, -s, s, -s, -s);
                class_9801 mesh = builder.method_60800();
                try {
                    gpuBuffer = device.createBuffer(() -> "Anubis sky theme cube", 32, mesh.method_60818());
                    if (mesh == null) break block11;
                }
                catch (Throwable throwable) {
                    if (mesh != null) {
                        try {
                            mesh.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                mesh.close();
            }
            return gpuBuffer;
        }
    }

    private static void face(class_287 builder, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz) {
        builder.method_22912(ax, ay, az);
        builder.method_22912(bx, by, bz);
        builder.method_22912(cx, cy, cz);
        builder.method_22912(dx, dy, dz);
    }
}

