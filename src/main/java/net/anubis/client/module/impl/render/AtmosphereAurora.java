/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_287
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.impl.render.SkyMesh;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_287;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
final class AtmosphereAurora {
    private static final float RADIUS = 100.0f;
    private static final int COLUMNS = 64;
    private static final int ROWS = 6;
    private static final float[][] CURTAINS = new float[][]{{-40.0f, 22.0f, 78.0f, 18.0f}, {6.0f, 28.0f, 100.0f, 23.0f}, {54.0f, 20.0f, 70.0f, 16.0f}};
    private final SkyMesh mesh = new SkyMesh("Anubis aurora", SkyMesh.GLOW, CURTAINS.length * 64 * 6);
    private final float[] x = new float[455];
    private final float[] y = new float[this.x.length];
    private final float[] z = new float[this.x.length];
    private final int[] color = new int[this.x.length];

    AtmosphereAurora() {
    }

    void render(class_4587 pose, float intensity, float time, int low, int high) {
        class_287 builder = this.mesh.begin();
        for (int i = 0; i < CURTAINS.length; ++i) {
            this.curtain(builder, CURTAINS[i], i, time, low, high);
        }
        this.mesh.upload(builder);
        this.mesh.draw(pose, intensity);
    }

    private void curtain(class_287 builder, float[] shape, int seed, float time, int low, int high) {
        int c;
        float azimuth = shape[0];
        float elevation = shape[1];
        float width = shape[2];
        float height = shape[3];
        for (c = 0; c <= 64; ++c) {
            float t = (float)c / 64.0f;
            float az = azimuth + (t - 0.5f) * width + 3.0f * AtmosphereAurora.wave(t * 2.1f + time * 0.05f + (float)seed);
            float base = elevation + 5.0f * class_3532.method_15374((double)((float)Math.PI * t)) + 3.0f * AtmosphereAurora.wave(t * 3.7f - time * 0.07f + (float)seed * 2.0f);
            float ends = AtmosphereAurora.smoothstep(t / 0.25f) * AtmosphereAurora.smoothstep((1.0f - t) / 0.25f);
            float glow = ends * (0.55f + 0.45f * AtmosphereAurora.wave01(t * 5.3f + time * 0.11f + (float)seed * 1.3f));
            float rays = 0.72f + 0.28f * AtmosphereAurora.wave01(t * 23.0f + time * 0.35f + (float)seed);
            float top = height * (0.8f + 0.35f * AtmosphereAurora.wave01(t * 4.1f + time * 0.06f + (float)seed * 3.0f));
            float cosAz = class_3532.method_15362((double)(az * ((float)Math.PI / 180)));
            float sinAz = class_3532.method_15374((double)(az * ((float)Math.PI / 180)));
            for (int r = 0; r <= 6; ++r) {
                float v = (float)r / 6.0f;
                float el = (base + v * top) * ((float)Math.PI / 180);
                float cosEl = class_3532.method_15362((double)el);
                int i = c * 7 + r;
                this.x[i] = 100.0f * cosEl * sinAz;
                this.y[i] = 100.0f * class_3532.method_15374((double)el);
                this.z[i] = -100.0f * cosEl * cosAz;
                float profile = v < 0.12f ? AtmosphereAurora.smoothstep(v / 0.12f) : (float)Math.pow(1.0f - (v - 0.12f) / 0.88f, 1.5);
                float alpha = class_3532.method_15363((float)(glow * rays * profile * 0.8f), (float)0.0f, (float)1.0f);
                int rgb = class_9848.method_61319((float)((float)Math.pow(v, 0.7)), (int)low, (int)high) & 0xFFFFFF;
                this.color[i] = Math.round(alpha * 255.0f) << 24 | rgb;
            }
        }
        for (c = 0; c < 64; ++c) {
            for (int r = 0; r < 6; ++r) {
                int a = c * 7 + r;
                int b = (c + 1) * 7 + r;
                this.vertex(builder, a);
                this.vertex(builder, b);
                this.vertex(builder, b + 1);
                this.vertex(builder, a + 1);
            }
        }
    }

    private void vertex(class_287 builder, int i) {
        SkyMesh.vertex(builder, this.x[i], this.y[i], this.z[i], this.color[i]);
    }

    static float wave(float s) {
        return 0.5f * class_3532.method_15374((double)s) + 0.3f * class_3532.method_15374((double)(s * 2.13f + 1.3f)) + 0.2f * class_3532.method_15374((double)(s * 4.37f + 2.9f));
    }

    static float wave01(float s) {
        return 0.5f + 0.5f * AtmosphereAurora.wave(s);
    }

    static float smoothstep(float x) {
        x = class_3532.method_15363((float)x, (float)0.0f, (float)1.0f);
        return x * x * (3.0f - 2.0f * x);
    }

    void close() {
        this.mesh.close();
    }
}

