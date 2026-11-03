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
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_10055
 *  net.minecraft.class_10366
 *  net.minecraft.class_10789
 *  net.minecraft.class_11286
 *  net.minecraft.class_11659
 *  net.minecraft.class_11661
 *  net.minecraft.class_11684
 *  net.minecraft.class_12075
 *  net.minecraft.class_1297
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4618
 *  net.minecraft.class_6367
 *  net.minecraft.class_777
 *  net.minecraft.class_8921
 *  net.minecraft.class_898
 *  net.minecraft.class_9779
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
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
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.anubis.client.module.impl.render.AuraBlur;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10055;
import net.minecraft.class_10366;
import net.minecraft.class_10789;
import net.minecraft.class_11286;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4618;
import net.minecraft.class_6367;
import net.minecraft.class_777;
import net.minecraft.class_8921;
import net.minecraft.class_898;
import net.minecraft.class_9779;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class ShaderEspPass {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"anubis", (String)"pipeline/glow_esp")).withVertexShader("core/screenquad").withFragmentShader(class_2960.method_60655((String)"anubis", (String)"core/glow_esp")).withSampler("SilhouetteSampler").withSampler("HaloSampler").withUniform("GlowEspConfig", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA).withColorWrite(true, false).withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).build();
    private static final Supplier<String> PASS_LABEL = () -> "Anubis glow esp";
    private static final Supplier<String> CONFIG_LABEL = () -> "Anubis glow esp config";
    private static final int CONFIG_SIZE = 32;
    private static final class_12075 CAMERA = new class_12075();
    private static final class_4587 POSE = new class_4587();
    private static final Map<class_1297, class_10017> KEPT = new IdentityHashMap<class_1297, class_10017>();
    private static Rig rig;
    private static class_11286 projectionBuffer;
    private static class_6367 mask;
    private static AuraBlur blur;
    private static GpuBuffer config;
    private static GpuBufferSlice configSlice;
    private static ByteBuffer configData;
    private static long keptFrame;
    private static boolean invalidShaderLogged;

    private ShaderEspPass() {
    }

    static void keep(class_1297 entity, class_10017 state, long frame) {
        if (keptFrame != frame) {
            KEPT.clear();
            keptFrame = frame;
        }
        KEPT.put(entity, state);
    }

    static void dropKept() {
        KEPT.clear();
        keptFrame = Long.MIN_VALUE;
    }

    static boolean paint(class_4184 camera, Matrix4f viewRotation, Matrix4f projection, class_1297[] entities, int[] tints, int count, long frame) {
        class_310 mc = class_310.method_1551();
        class_276 main = mc.method_1522();
        if (count <= 0 || mc.field_1687 == null || !mc.method_53466() || mc.field_1773.method_35765() || main.method_30277() == null || main.field_1482 <= 0 || main.field_1481 <= 0) {
            return false;
        }
        GpuDevice device = RenderSystem.getDevice();
        if (!ShaderEspPass.shaderReady(device)) {
            return false;
        }
        if (mask == null) {
            ShaderEspPass.allocate(main.field_1482, main.field_1481);
        } else if (ShaderEspPass.mask.field_1482 != main.field_1482 || ShaderEspPass.mask.field_1481 != main.field_1481) {
            mask.method_1234(main.field_1482, main.field_1481);
        }
        if (rig == null) {
            rig = new Rig(mc);
        }
        if (projectionBuffer == null) {
            projectionBuffer = new class_11286("anubis glow esp");
        }
        device.createCommandEncoder().clearColorTexture(mask.method_30277(), 0);
        ShaderEspPass.submit(mc, camera, entities, tints, count, frame);
        ShaderEspPass.flush(viewRotation, projection);
        return true;
    }

    private static void submit(class_310 mc, class_4184 camera, class_1297[] entities, int[] tints, int count, long frame) {
        class_898 dispatcher = mc.method_1561();
        class_9779 delta = mc.method_61966();
        class_8921 ticks = mc.field_1687.method_54719();
        class_243 eye = camera.method_71156();
        class_1297 viewer = camera.method_19331();
        boolean reuse = keptFrame == frame;
        ShaderEspPass.CAMERA.field_63079 = camera.method_19332();
        ShaderEspPass.CAMERA.field_63078 = eye;
        ShaderEspPass.CAMERA.field_63077 = camera.method_19328();
        ShaderEspPass.CAMERA.field_63080 = viewer == null ? eye : viewer.method_30950(delta.method_60637(true));
        ShaderEspPass.CAMERA.field_63081.set((Quaternionfc)camera.method_23767());
        for (int i = 0; i < count; ++i) {
            class_10017 state;
            class_1297 entity = entities[i];
            if (entity == null || entity.method_31481()) continue;
            class_10017 class_100172 = state = reuse ? KEPT.get(entity) : null;
            if (state == null) {
                state = dispatcher.method_72977(entity, delta.method_60637(!ticks.method_54746(entity)));
            }
            ShaderEspPass.silhouetteOnly(state);
            state.field_61821 = tints[i] | 0xFF000000;
            dispatcher.method_72976(state, CAMERA, state.field_53325 - eye.field_1352, state.field_53326 - eye.field_1351, state.field_53327 - eye.field_1350, POSE, (class_11659)ShaderEspPass.rig.storage);
        }
    }

    private static void silhouetteOnly(class_10017 state) {
        state.field_53337 = null;
        state.field_60160 = null;
        state.field_53335 = false;
        state.field_61823.clear();
        if (state instanceof class_10055) {
            class_10055 avatar = (class_10055)state;
            avatar.field_53525 = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void flush(Matrix4f viewRotation, Matrix4f projection) {
        GpuTextureView previousColor = RenderSystem.outputColorTextureOverride;
        GpuTextureView previousDepth = RenderSystem.outputDepthTextureOverride;
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionBuffer.method_71123(projection), (class_10366)class_10366.field_54953);
        modelView.pushMatrix();
        modelView.mul((Matrix4fc)viewRotation);
        RenderSystem.outputColorTextureOverride = mask.method_71639();
        RenderSystem.outputDepthTextureOverride = null;
        try {
            ShaderEspPass.rig.features.method_73002();
            ShaderEspPass.rig.outlines.method_23285();
        }
        finally {
            RenderSystem.outputColorTextureOverride = previousColor;
            RenderSystem.outputDepthTextureOverride = previousDepth;
            modelView.popMatrix();
            RenderSystem.restoreProjectionMatrix();
            ShaderEspPass.rig.storage.method_72953();
        }
        ShaderEspPass.rig.storage.method_72954();
    }

    static void compose(float strokePx, float aura, float reachPx, float fill, float phase, int sheenArgb, float sheen) {
        class_276 main = class_310.method_1551().method_1522();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        GpuTextureView halo = blur.spread(encoder, mask.method_71639(), ShaderEspPass.mask.field_1482, ShaderEspPass.mask.field_1481, Math.max(reachPx, strokePx + 2.0f));
        configData.putFloat(0, strokePx);
        configData.putFloat(4, aura);
        configData.putFloat(8, fill);
        configData.putFloat(12, phase);
        configData.putFloat(16, (float)(sheenArgb >> 16 & 0xFF) / 255.0f);
        configData.putFloat(20, (float)(sheenArgb >> 8 & 0xFF) / 255.0f);
        configData.putFloat(24, (float)(sheenArgb & 0xFF) / 255.0f);
        configData.putFloat(28, sheen);
        encoder.writeToBuffer(configSlice, configData);
        try (RenderPass pass = encoder.createRenderPass(PASS_LABEL, main.method_71639(), OptionalInt.empty());){
            pass.setPipeline(PIPELINE);
            pass.setUniform("GlowEspConfig", configSlice);
            pass.bindTexture("SilhouetteSampler", mask.method_71639(), RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            pass.bindTexture("HaloSampler", halo, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            pass.draw(0, 3);
        }
    }

    private static boolean shaderReady(GpuDevice device) {
        if (device.precompilePipeline(PIPELINE).isValid() && AuraBlur.compiled(device)) {
            invalidShaderLogged = false;
            return true;
        }
        if (!invalidShaderLogged) {
            invalidShaderLogged = true;
            LOGGER.warn("Glow ESP shaders did not compile; entities are left without outlines");
        }
        return false;
    }

    private static void allocate(int width, int height) {
        mask = new class_6367("Anubis glow esp mask", width, height, false);
        blur = new AuraBlur("Anubis glow esp aura");
        config = RenderSystem.getDevice().createBuffer(CONFIG_LABEL, 136, 32L);
        configSlice = config.slice();
        configData = BufferUtils.createByteBuffer((int)32);
    }

    static void release() {
        ShaderEspPass.dropKept();
        if (rig != null) {
            rig.close();
            rig = null;
        }
        if (mask != null) {
            mask.method_1238();
            mask = null;
        }
        if (blur != null) {
            blur.release();
            blur = null;
        }
        if (config != null) {
            config.close();
            config = null;
        }
        if (projectionBuffer != null) {
            projectionBuffer.close();
            projectionBuffer = null;
        }
        configSlice = null;
        configData = null;
    }

    static {
        keptFrame = Long.MIN_VALUE;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Rig {
        final class_11661 storage = new class_11661();
        final Drain drain = new Drain();
        final class_4618 outlines;
        final class_11684 features;

        Rig(class_310 mc) {
            this.outlines = mc.method_22940().method_23003();
            this.features = new class_11684(this.storage, mc.method_1541(), (class_4597.class_4598)this.drain, mc.method_72703(), this.outlines, (class_4597.class_4598)this.drain, mc.field_1772);
        }

        void close() {
            this.features.close();
            this.drain.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Nowhere
    implements class_4588 {
        private Nowhere() {
        }

        public class_4588 method_22912(float x, float y, float z) {
            return this;
        }

        public void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        }

        public void method_22919(class_4587.class_4665 pose, class_777 quad, float red, float green, float blue, float alpha, int light, int overlay) {
        }

        public void method_22920(class_4587.class_4665 pose, class_777 quad, float[] brightness, float red, float green, float blue, float alpha, int[] lights, int overlay) {
        }

        public class_4588 method_1336(int red, int green, int blue, int alpha) {
            return this;
        }

        public class_4588 method_39415(int argb) {
            return this;
        }

        public class_4588 method_22913(float u, float v) {
            return this;
        }

        public class_4588 method_60796(int u, int v) {
            return this;
        }

        public class_4588 method_22921(int u, int v) {
            return this;
        }

        public class_4588 method_22914(float x, float y, float z) {
            return this;
        }

        public class_4588 method_75298(float width) {
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Drain
    extends class_4597.class_4598 {
        private final Map<class_1921, class_4588> sinks = new IdentityHashMap<class_1921, class_4588>();

        Drain() {
            super(new class_9799(64), new LinkedHashMap());
        }

        void close() {
            this.field_52156.close();
        }

        public class_4588 method_73477(class_1921 renderType) {
            return this.sinks.computeIfAbsent(renderType, type -> new Nowhere());
        }

        public void method_37104() {
        }

        public void method_22993() {
        }

        public void method_22994(class_1921 renderType) {
        }
    }
}

