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
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10366
 *  net.minecraft.class_10799
 *  net.minecraft.class_11286
 *  net.minecraft.class_243
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
 */
package net.anubis.client.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10366;
import net.minecraft.class_10799;
import net.minecraft.class_11286;
import net.minecraft.class_243;
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

@Environment(value=EnvType.CLIENT)
public final class WorldQuads {
    private static final int MAX_QUADS = 64000;
    private static final int MIN_ALPHA = 3;
    private static final RenderPipeline DEPTH_TESTED = WorldQuads.pipeline("world_quads", DepthTestFunction.LEQUAL_DEPTH_TEST);
    private static final RenderPipeline ON_TOP = WorldQuads.pipeline("world_quads_on_top", DepthTestFunction.NO_DEPTH_TEST);
    private static final WorldQuads TESTED = new WorldQuads(DEPTH_TESTED);
    private static final WorldQuads TOP = new WorldQuads(ON_TOP);
    private static double originX;
    private static double originY;
    private static double originZ;
    private static class_9799 bytes;
    private static class_11286 projectionBuffer;
    private final RenderPipeline pipeline;
    private float[] corners = new float[3072];
    private int[] colors = new int[256];
    private int quads;

    private WorldQuads(RenderPipeline pipeline) {
        this.pipeline = pipeline;
    }

    private static RenderPipeline pipeline(String name, DepthTestFunction depth) {
        return RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"anubis", (String)("pipeline/" + name))).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).withDepthTestFunction(depth).withCull(false).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).build();
    }

    public static WorldQuads layer(boolean onTop) {
        return onTop ? TOP : TESTED;
    }

    public static void beginFrame(class_243 camera) {
        originX = camera.field_1352;
        originY = camera.field_1351;
        originZ = camera.field_1350;
        WorldQuads.TESTED.quads = 0;
        WorldQuads.TOP.quads = 0;
    }

    public boolean quad(double ax, double ay, double az, double bx, double by, double bz, double cx, double cy, double cz, double dx, double dy, double dz, int argb) {
        if (argb >>> 24 < 3 || !this.reserve(1)) {
            return false;
        }
        int p = this.quads * 12;
        float[] out = this.corners;
        out[p] = (float)(ax - originX);
        out[p + 1] = (float)(ay - originY);
        out[p + 2] = (float)(az - originZ);
        out[p + 3] = (float)(bx - originX);
        out[p + 4] = (float)(by - originY);
        out[p + 5] = (float)(bz - originZ);
        out[p + 6] = (float)(cx - originX);
        out[p + 7] = (float)(cy - originY);
        out[p + 8] = (float)(cz - originZ);
        out[p + 9] = (float)(dx - originX);
        out[p + 10] = (float)(dy - originY);
        out[p + 11] = (float)(dz - originZ);
        this.colors[this.quads++] = argb;
        return true;
    }

    public void quads(double[] positions, int[] argb, int count) {
        if (count <= 0) {
            return;
        }
        int room = Math.min(count, 64000 - this.quads);
        if (room <= 0 || !this.reserve(room)) {
            return;
        }
        float[] out = this.corners;
        int w = this.quads * 12;
        int kept = 0;
        for (int i = 0; i < room; ++i) {
            int color = argb[i];
            if (color >>> 24 < 3) continue;
            int r = i * 12;
            for (int k = 0; k < 12; k += 3) {
                out[w + k] = (float)(positions[r + k] - originX);
                out[w + k + 1] = (float)(positions[r + k + 1] - originY);
                out[w + k + 2] = (float)(positions[r + k + 2] - originZ);
            }
            this.colors[this.quads + kept] = color;
            ++kept;
            w += 12;
        }
        this.quads += kept;
    }

    private boolean reserve(int more) {
        if (this.quads + more <= this.colors.length) {
            return true;
        }
        if (this.quads + more > 64000) {
            return false;
        }
        int grown = Math.min(64000, Math.max(this.colors.length * 2, this.quads + more));
        this.colors = Arrays.copyOf(this.colors, grown);
        this.corners = Arrays.copyOf(this.corners, grown * 12);
        return true;
    }

    public static void render(Matrix4f viewRotation, Matrix4f projection) {
        if (WorldQuads.TESTED.quads == 0 && WorldQuads.TOP.quads == 0) {
            return;
        }
        if (projectionBuffer == null) {
            projectionBuffer = new class_11286("anubis world quads");
        }
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionBuffer.method_71123(projection), (class_10366)class_10366.field_54953);
        try {
            TESTED.draw(viewRotation);
            TOP.draw(viewRotation);
        }
        finally {
            RenderSystem.restoreProjectionMatrix();
            WorldQuads.TESTED.quads = 0;
            WorldQuads.TOP.quads = 0;
        }
    }

    private void draw(Matrix4f viewRotation) {
        if (this.quads == 0) {
            return;
        }
        if (bytes == null) {
            bytes = new class_9799(this.quads * 4 * class_290.field_1576.getVertexSize());
        }
        class_287 builder = new class_287(bytes, VertexFormat.class_5596.field_27382, class_290.field_1576);
        float[] in = this.corners;
        int i = 0;
        int p = 0;
        while (i < this.quads) {
            int color = this.colors[i];
            builder.method_22912(in[p], in[p + 1], in[p + 2]).method_39415(color);
            builder.method_22912(in[p + 3], in[p + 4], in[p + 5]).method_39415(color);
            builder.method_22912(in[p + 6], in[p + 7], in[p + 8]).method_39415(color);
            builder.method_22912(in[p + 9], in[p + 10], in[p + 11]).method_39415(color);
            ++i;
            p += 12;
        }
        try (class_9801 mesh = builder.method_60794();){
            if (mesh == null) {
                return;
            }
            GpuBuffer vertices = class_290.field_1576.uploadImmediateVertexBuffer(mesh.method_60818());
            RenderSystem.class_5590 indices = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
            int indexCount = mesh.method_60822().comp_751();
            GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)viewRotation, (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
            class_276 target = class_310.method_1551().method_1522();
            try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Anubis world quads", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());){
                pass.setPipeline(this.pipeline);
                RenderSystem.bindDefaultUniforms((RenderPass)pass);
                pass.setUniform("DynamicTransforms", transforms);
                pass.setVertexBuffer(0, vertices);
                pass.setIndexBuffer(indices.method_68274(indexCount), indices.method_31924());
                pass.drawIndexed(0, 0, indexCount, 1);
            }
        }
    }
}

