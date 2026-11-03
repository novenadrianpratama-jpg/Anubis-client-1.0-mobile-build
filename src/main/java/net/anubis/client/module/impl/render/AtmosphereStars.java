/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_287
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_5819
 *  net.minecraft.class_9848
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.impl.render.AtmosphereAurora;
import net.anubis.client.module.impl.render.SkyMesh;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_287;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_5819;
import net.minecraft.class_9848;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
final class AtmosphereStars {
    private static final float RADIUS = 100.0f;
    private static final int STARS = 2600;
    private static final float GLOW_ABOVE = 0.8f;
    private static final long TWINKLE_NANOS = 50000000L;
    private static final int BAND_COLUMNS = 180;
    private static final int BAND_ROWS = 12;
    private static final float BAND_HALF_WIDTH = 17.0f;
    private static final int NEBULA_COLUMNS = 72;
    private static final int NEBULA_ROWS = 36;
    private static final Vector3f BAND_NORMAL = new Vector3f(0.42f, 0.5f, 0.76f).normalize();
    private final SkyMesh starMesh = new SkyMesh("Anubis stars", SkyMesh.GLOW, 5200);
    private final SkyMesh bandMesh = new SkyMesh("Anubis milky way", SkyMesh.GLOW, 2160);
    private final SkyMesh nebulaMesh = new SkyMesh("Anubis end nebulae", SkyMesh.GLOW, 2592);
    private final float[] dir = new float[7800];
    private final float[] size = new float[2600];
    private final float[] light = new float[2600];
    private final float[] roll = new float[2600];
    private final float[] phase = new float[2600];
    private final float[] speed = new float[2600];
    private final int[] rgb = new int[2600];
    private boolean placed;
    private long lastTwinkle;
    private float builtBrightness = -1.0f;
    private int bandTint;

    AtmosphereStars() {
    }

    void renderStars(class_4587 pose, float alpha, float brightness) {
        long now = System.nanoTime();
        if (now - this.lastTwinkle > 50000000L || brightness != this.builtBrightness) {
            this.lastTwinkle = now;
            this.builtBrightness = brightness;
            this.buildStars((float)(now % 3600000000000L) / 1.0E9f, brightness);
        }
        this.starMesh.draw(pose, alpha);
    }

    void renderBand(class_4587 pose, float alpha, int tint) {
        if (!this.bandMesh.isBuilt() || tint != this.bandTint) {
            this.bandTint = tint;
            this.buildBand(tint);
        }
        this.bandMesh.draw(pose, alpha);
    }

    void renderNebulae(class_4587 pose, float alpha) {
        if (!this.nebulaMesh.isBuilt()) {
            this.buildNebulae();
        }
        this.nebulaMesh.draw(pose, alpha);
    }

    private void place() {
        class_5819 random = class_5819.method_43049((long)10842L);
        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        AtmosphereStars.bandAxes(a, b);
        for (int i = 0; i < 2600; ++i) {
            float z;
            float y;
            float x;
            if (random.method_43057() < 0.3f) {
                float along = random.method_43057() * ((float)Math.PI * 2);
                float across = (float)random.method_43059() * 9.0f * ((float)Math.PI / 180);
                float cosAcross = class_3532.method_15362((double)across);
                float sinAcross = class_3532.method_15374((double)across);
                x = cosAcross * (class_3532.method_15362((double)along) * a.x + class_3532.method_15374((double)along) * b.x) + sinAcross * AtmosphereStars.BAND_NORMAL.x;
                y = cosAcross * (class_3532.method_15362((double)along) * a.y + class_3532.method_15374((double)along) * b.y) + sinAcross * AtmosphereStars.BAND_NORMAL.y;
                z = cosAcross * (class_3532.method_15362((double)along) * a.z + class_3532.method_15374((double)along) * b.z) + sinAcross * AtmosphereStars.BAND_NORMAL.z;
            } else {
                y = random.method_43057() * 2.0f - 1.0f;
                float around = random.method_43057() * ((float)Math.PI * 2);
                float flat = class_3532.method_15355((float)(1.0f - y * y));
                x = flat * class_3532.method_15362((double)around);
                z = flat * class_3532.method_15374((double)around);
            }
            this.dir[i * 3] = x;
            this.dir[i * 3 + 1] = y;
            this.dir[i * 3 + 2] = z;
            float big = (float)Math.pow(random.method_43057(), 6.0);
            this.size[i] = 0.1f + 0.5f * big;
            this.light[i] = class_3532.method_15363((float)(0.3f + 0.7f * big + 0.25f * random.method_43057()), (float)0.0f, (float)1.0f);
            this.roll[i] = random.method_43057() * ((float)Math.PI * 2);
            this.phase[i] = random.method_43057() * ((float)Math.PI * 2);
            this.speed[i] = 1.5f + 3.5f * random.method_43057();
            this.rgb[i] = AtmosphereStars.starColour(random.method_43057());
        }
        this.placed = true;
    }

    private static int starColour(float t) {
        if (t < 0.18f) {
            return class_9848.method_61319((float)(t / 0.18f), (int)16757870, (int)16774116);
        }
        if (t < 0.75f) {
            return 0xFFF8F0;
        }
        return class_9848.method_61319((float)((t - 0.75f) / 0.25f), (int)0xFFF3F6, (int)11125503);
    }

    private void buildStars(float time, float brightness) {
        if (!this.placed) {
            this.place();
        }
        class_287 builder = this.starMesh.begin();
        for (int i = 0; i < 2600; ++i) {
            float twinkle = 0.62f + 0.38f * class_3532.method_15374((double)(time * this.speed[i] + this.phase[i]));
            float alpha = class_3532.method_15363((float)(this.light[i] * twinkle * brightness), (float)0.0f, (float)1.0f);
            this.star(builder, i, this.size[i], alpha);
            if (!(this.light[i] > 0.8f)) continue;
            this.star(builder, i, this.size[i] * 4.0f, alpha * 0.12f);
        }
        this.starMesh.upload(builder);
    }

    private void star(class_287 builder, int i, float half, float alpha) {
        float uz;
        float uy;
        float ux;
        float x = this.dir[i * 3];
        float y = this.dir[i * 3 + 1];
        float z = this.dir[i * 3 + 2];
        if (Math.abs(y) < 0.9f) {
            ux = z;
            uy = 0.0f;
            uz = -x;
        } else {
            ux = 0.0f;
            uy = -z;
            uz = y;
        }
        float ul = class_3532.method_15355((float)(ux * ux + uy * uy + uz * uz));
        float vx = y * (uz /= ul) - z * (uy /= ul);
        float vy = z * (ux /= ul) - x * uz;
        float vz = x * uy - y * ux;
        float c = class_3532.method_15362((double)this.roll[i]) * half;
        float s = class_3532.method_15374((double)this.roll[i]) * half;
        float ax = ux * c + vx * s;
        float ay = uy * c + vy * s;
        float az = uz * c + vz * s;
        float bx = vx * c - ux * s;
        float by = vy * c - uy * s;
        float bz = vz * c - uz * s;
        float cx = x * 100.0f;
        float cy = y * 100.0f;
        float cz = z * 100.0f;
        int argb = Math.round(alpha * 255.0f) << 24 | this.rgb[i];
        SkyMesh.vertex(builder, cx - ax - bx, cy - ay - by, cz - az - bz, argb);
        SkyMesh.vertex(builder, cx + ax - bx, cy + ay - by, cz + az - bz, argb);
        SkyMesh.vertex(builder, cx + ax + bx, cy + ay + by, cz + az + bz, argb);
        SkyMesh.vertex(builder, cx - ax + bx, cy - ay + by, cz - az + bz, argb);
    }

    private static void bandAxes(Vector3f a, Vector3f b) {
        new Vector3f(0.0f, 1.0f, 0.0f).cross((Vector3fc)BAND_NORMAL, a).normalize();
        new Vector3f((Vector3fc)BAND_NORMAL).cross((Vector3fc)a, b).normalize();
    }

    private void buildBand(int tint) {
        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        AtmosphereStars.bandAxes(a, b);
        float[][] px = new float[181][13];
        float[][] py = new float[181][13];
        float[][] pz = new float[181][13];
        int[][] colour = new int[181][13];
        for (int c = 0; c <= 180; ++c) {
            float along = (float)c / 180.0f * ((float)Math.PI * 2);
            float ax = class_3532.method_15362((double)along) * a.x + class_3532.method_15374((double)along) * b.x;
            float ay = class_3532.method_15362((double)along) * a.y + class_3532.method_15374((double)along) * b.y;
            float az = class_3532.method_15362((double)along) * a.z + class_3532.method_15374((double)along) * b.z;
            float width = 0.55f + 0.45f * AtmosphereStars.fbm(ax * 1.7f, ay * 1.7f, az * 1.7f);
            float rift = 3.0f * (AtmosphereStars.fbm(ax * 2.3f + 5.0f, ay * 2.3f, az * 2.3f) - 0.5f);
            float riftDepth = class_3532.method_15363((float)(AtmosphereStars.fbm(ax * 1.4f, ay * 1.4f + 9.0f, az * 1.4f) * 1.6f - 0.35f), (float)0.0f, (float)1.0f);
            for (int r = 0; r <= 12; ++r) {
                float acrossDeg = ((float)r / 12.0f * 2.0f - 1.0f) * 17.0f;
                float across = acrossDeg * ((float)Math.PI / 180);
                float cosAcross = class_3532.method_15362((double)across);
                float sinAcross = class_3532.method_15374((double)across);
                float x = cosAcross * ax + sinAcross * AtmosphereStars.BAND_NORMAL.x;
                float y = cosAcross * ay + sinAcross * AtmosphereStars.BAND_NORMAL.y;
                float z = cosAcross * az + sinAcross * AtmosphereStars.BAND_NORMAL.z;
                px[c][r] = x * 100.0f;
                py[c][r] = y * 100.0f;
                pz[c][r] = z * 100.0f;
                float edge = acrossDeg / (17.0f * width);
                float core = (float)Math.exp(-edge * edge * 2.2f);
                float dust = 0.35f + 0.65f * AtmosphereStars.fbm(x * 6.0f, y * 6.0f, z * 6.0f);
                float lane = 1.0f - 0.75f * riftDepth * (float)Math.exp(-AtmosphereStars.sq((acrossDeg - rift) / 2.6f));
                float glow = class_3532.method_15363((float)(core * dust * lane), (float)0.0f, (float)1.0f);
                int rgb = class_9848.method_61319((float)core, (int)7306440, (int)tint) & 0xFFFFFF;
                colour[c][r] = Math.round(glow * 0.42f * 255.0f) << 24 | rgb;
            }
        }
        class_287 builder = this.bandMesh.begin();
        for (int c = 0; c < 180; ++c) {
            for (int r = 0; r < 12; ++r) {
                SkyMesh.vertex(builder, px[c][r], py[c][r], pz[c][r], colour[c][r]);
                SkyMesh.vertex(builder, px[c + 1][r], py[c + 1][r], pz[c + 1][r], colour[c + 1][r]);
                SkyMesh.vertex(builder, px[c + 1][r + 1], py[c + 1][r + 1], pz[c + 1][r + 1], colour[c + 1][r + 1]);
                SkyMesh.vertex(builder, px[c][r + 1], py[c][r + 1], pz[c][r + 1], colour[c][r + 1]);
            }
        }
        this.bandMesh.upload(builder);
    }

    private void buildNebulae() {
        int r;
        float[][] px = new float[73][37];
        float[][] py = new float[73][37];
        float[][] pz = new float[73][37];
        int[][] colour = new int[73][37];
        for (int c = 0; c <= 72; ++c) {
            float around = (float)c / 72.0f * ((float)Math.PI * 2);
            for (r = 0; r <= 36; ++r) {
                float el = ((float)r / 36.0f - 0.5f) * (float)Math.PI;
                float x = class_3532.method_15362((double)el) * class_3532.method_15362((double)around);
                float y = class_3532.method_15374((double)el);
                float z = class_3532.method_15362((double)el) * class_3532.method_15374((double)around);
                px[c][r] = x * 100.0f;
                py[c][r] = y * 100.0f;
                pz[c][r] = z * 100.0f;
                float density = AtmosphereAurora.smoothstep((AtmosphereStars.fbm(x * 2.2f, y * 2.2f, z * 2.2f) - 0.42f) / 0.35f);
                float hue = AtmosphereStars.fbm(x * 1.3f + 11.0f, y * 1.3f, z * 1.3f);
                int rgb = hue < 0.5f ? class_9848.method_61319((float)(hue * 2.0f), (int)8011007, (int)16732104) : class_9848.method_61319((float)((hue - 0.5f) * 2.0f), (int)16732104, (int)3989712);
                colour[c][r] = Math.round(density * 0.55f * 255.0f) << 24 | rgb & 0xFFFFFF;
            }
        }
        class_287 builder = this.nebulaMesh.begin();
        for (int c = 0; c < 72; ++c) {
            for (r = 0; r < 36; ++r) {
                SkyMesh.vertex(builder, px[c][r], py[c][r], pz[c][r], colour[c][r]);
                SkyMesh.vertex(builder, px[c + 1][r], py[c + 1][r], pz[c + 1][r], colour[c + 1][r]);
                SkyMesh.vertex(builder, px[c + 1][r + 1], py[c + 1][r + 1], pz[c + 1][r + 1], colour[c + 1][r + 1]);
                SkyMesh.vertex(builder, px[c][r + 1], py[c][r + 1], pz[c][r + 1], colour[c][r + 1]);
            }
        }
        this.nebulaMesh.upload(builder);
    }

    static float fbm(float x, float y, float z) {
        float sum = 0.0f;
        float amplitude = 0.5f;
        float total = 0.0f;
        for (int octave = 0; octave < 4; ++octave) {
            sum += amplitude * AtmosphereStars.noise(x, y, z);
            total += amplitude;
            x *= 2.03f;
            y *= 2.03f;
            z *= 2.03f;
            amplitude *= 0.5f;
        }
        return sum / total;
    }

    private static float noise(float x, float y, float z) {
        int ix = class_3532.method_15375((float)x);
        int iy = class_3532.method_15375((float)y);
        int iz = class_3532.method_15375((float)z);
        float fx = x - (float)ix;
        float fy = y - (float)iy;
        float fz = z - (float)iz;
        fx = fx * fx * (3.0f - 2.0f * fx);
        fy = fy * fy * (3.0f - 2.0f * fy);
        fz = fz * fz * (3.0f - 2.0f * fz);
        float x00 = class_3532.method_16439((float)fx, (float)AtmosphereStars.hash(ix, iy, iz), (float)AtmosphereStars.hash(ix + 1, iy, iz));
        float x10 = class_3532.method_16439((float)fx, (float)AtmosphereStars.hash(ix, iy + 1, iz), (float)AtmosphereStars.hash(ix + 1, iy + 1, iz));
        float x01 = class_3532.method_16439((float)fx, (float)AtmosphereStars.hash(ix, iy, iz + 1), (float)AtmosphereStars.hash(ix + 1, iy, iz + 1));
        float x11 = class_3532.method_16439((float)fx, (float)AtmosphereStars.hash(ix, iy + 1, iz + 1), (float)AtmosphereStars.hash(ix + 1, iy + 1, iz + 1));
        return class_3532.method_16439((float)fz, (float)class_3532.method_16439((float)fy, (float)x00, (float)x10), (float)class_3532.method_16439((float)fy, (float)x01, (float)x11));
    }

    private static float hash(int x, int y, int z) {
        int h = x * 374761393 + y * 668265263 + z * 1274126177;
        h = (h ^ h >>> 13) * 1274126177;
        return (float)((h ^ h >>> 16) & 0xFFFFFF) / 1.6777215E7f;
    }

    private static float sq(float v) {
        return v * v;
    }

    void close() {
        this.starMesh.close();
        this.bandMesh.close();
        this.nebulaMesh.close();
        this.builtBrightness = -1.0f;
    }
}

