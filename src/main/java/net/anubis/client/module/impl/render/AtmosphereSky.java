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

@Environment(value=EnvType.CLIENT)
final class AtmosphereSky {
    private static final float RADIUS = 100.0f;
    private static final float[] RINGS = new float[]{-30.0f, -12.0f, -4.0f, 0.0f, 4.0f, 9.0f, 16.0f, 26.0f, 40.0f, 58.0f, 76.0f, 90.0f};
    private static final int SEGMENTS = 48;
    private static final int HALO_RINGS = 4;
    private static final int HALO_SEGMENTS = 32;
    private static final int METEOR_SEGMENTS = 10;
    private static final float[] KEYS = new float[]{-0.35f, -0.08f, 0.06f, 0.3f};
    private static final int GOLDEN_ZENITH = 5210048;
    private static final int GOLDEN_HORIZON = 16032864;
    private static final int GOLDEN_GLOW = 16747069;
    private static final int DAY_ZENITH = 3108822;
    private static final int DAY_HORIZON = 11850996;
    private static final int DAY_GLOW = 16774362;
    private static final int BLUE_HOUR_ZENITH = 1319497;
    private final SkyMesh dome = new SkyMesh("Anubis sky dome", SkyMesh.PAINT, (RINGS.length - 1) * 48);
    private final SkyMesh endDome = new SkyMesh("Anubis end sky", SkyMesh.PAINT, (RINGS.length - 1) * 48);
    private final SkyMesh halo = new SkyMesh("Anubis sun and moon glow", SkyMesh.GLOW, 256);
    private final SkyMesh meteorMesh = new SkyMesh("Anubis shooting stars", SkyMesh.GLOW, 12);
    private final class_5819 random = class_5819.method_43047();
    private static final float DOME_SUN_STEP = 0.002f;
    private static final float DOME_STRENGTH_STEP = 0.004f;
    private int zenith;
    private int horizon;
    private int glow;
    private float glowStrength;
    private int builtZenith;
    private int builtHorizon;
    private int builtGlow;
    private float builtStrength;
    private float builtSunX = Float.NaN;
    private float builtSunY = Float.NaN;
    private float sunX;
    private float sunY;
    private float sunAngle;
    private float moonAngle;
    private boolean ready;
    private long meteorStart;
    private long meteorEnd;
    private long nextMeteor;
    private float mx;
    private float my;
    private float mz;
    private float tx;
    private float ty;
    private float tz;

    AtmosphereSky() {
    }

    void update(float sunAngle, float moonAngle, float rain, float thunder, float flash, int nightZenith, int nightHorizon, int twilight) {
        int key;
        this.sunAngle = sunAngle;
        this.moonAngle = moonAngle;
        this.sunX = -class_3532.method_15374((double)sunAngle);
        this.sunY = class_3532.method_15362((double)sunAngle);
        int blueHourZenith = class_9848.method_61319((float)0.3f, (int)1319497, (int)nightZenith);
        int[] zeniths = new int[]{nightZenith, blueHourZenith, 5210048, 3108822};
        int[] horizons = new int[]{nightHorizon, AtmosphereSky.scale(twilight, 0.75f), 16032864, 11850996};
        int[] glows = new int[]{nightHorizon, twilight, 16747069, 16774362};
        float[] strengths = new float[]{0.0f, 0.55f, 1.0f, 0.25f};
        for (key = 0; key < KEYS.length - 2 && this.sunY > KEYS[key + 1]; ++key) {
        }
        float t = AtmosphereAurora.smoothstep((this.sunY - KEYS[key]) / (KEYS[key + 1] - KEYS[key]));
        this.zenith = class_9848.method_61319((float)t, (int)zeniths[key], (int)zeniths[key + 1]);
        this.horizon = class_9848.method_61319((float)t, (int)horizons[key], (int)horizons[key + 1]);
        this.glow = class_9848.method_61319((float)t, (int)glows[key], (int)glows[key + 1]);
        this.glowStrength = class_3532.method_16439((float)t, (float)strengths[key], (float)strengths[key + 1]) * (1.0f - rain);
        this.zenith = AtmosphereSky.weather(this.zenith, rain, thunder, flash);
        this.horizon = AtmosphereSky.weather(this.horizon, rain, thunder, flash);
        this.ready = true;
    }

    private static int weather(int rgb, float rain, float thunder, float flash) {
        int grey = AtmosphereSky.scale(AtmosphereSky.luma(rgb), 0.85f);
        rgb = class_9848.method_61319((float)(rain * 0.65f), (int)rgb, (int)grey);
        rgb = AtmosphereSky.scale(rgb, 1.0f - 0.35f * rain - 0.25f * thunder);
        return flash > 0.0f ? class_9848.method_61319((float)(flash * 0.45f), (int)rgb, (int)15265023) : rgb;
    }

    boolean ready() {
        return this.ready;
    }

    int horizonToward(float x, float z) {
        float length = class_3532.method_15355((float)(x * x + z * z));
        return length < 1.0E-4f ? this.horizon : this.colourAt(x / length, 0.02f, z / length);
    }

    float golden() {
        return 1.0f - Math.min(1.0f, Math.abs(this.sunY - 0.06f) / 0.22f);
    }

    float nightness() {
        return AtmosphereAurora.smoothstep((0.05f - this.sunY) / 0.4f);
    }

    int twilightGlow() {
        return this.glow;
    }

    void renderDome(class_4587 pose) {
        if (!this.dome.isBuilt() || this.domeStale()) {
            class_287 builder = this.dome.begin();
            this.domeQuads(builder, false);
            this.dome.upload(builder);
            this.builtZenith = this.zenith;
            this.builtHorizon = this.horizon;
            this.builtGlow = this.glow;
            this.builtStrength = this.glowStrength;
            this.builtSunX = this.sunX;
            this.builtSunY = this.sunY;
        }
        this.dome.draw(pose, 1.0f);
    }

    private boolean domeStale() {
        return this.zenith != this.builtZenith || this.horizon != this.builtHorizon || this.glow != this.builtGlow || Math.abs(this.glowStrength - this.builtStrength) > 0.004f || Math.abs(this.sunX - this.builtSunX) > 0.002f || Math.abs(this.sunY - this.builtSunY) > 0.002f;
    }

    void renderEndDome(class_4587 pose) {
        if (!this.endDome.isBuilt()) {
            class_287 builder = this.endDome.begin();
            this.domeQuads(builder, true);
            this.endDome.upload(builder);
        }
        this.endDome.draw(pose, 1.0f);
    }

    private void domeQuads(class_287 builder, boolean end) {
        for (int ring = 0; ring < RINGS.length - 1; ++ring) {
            for (int s = 0; s < 48; ++s) {
                this.corner(builder, RINGS[ring], s, end);
                this.corner(builder, RINGS[ring], s + 1, end);
                this.corner(builder, RINGS[ring + 1], s + 1, end);
                this.corner(builder, RINGS[ring + 1], s, end);
            }
        }
    }

    private void corner(class_287 builder, float elevationDeg, int segment, boolean end) {
        float el = elevationDeg * ((float)Math.PI / 180);
        float az = (float)segment / 48.0f * ((float)Math.PI * 2);
        float x = class_3532.method_15362((double)el) * class_3532.method_15362((double)az);
        float y = class_3532.method_15374((double)el);
        float z = class_3532.method_15362((double)el) * class_3532.method_15374((double)az);
        int rgb = end ? AtmosphereSky.endColourAt(y) : this.colourAt(x, y, z);
        SkyMesh.vertex(builder, x * 100.0f, y * 100.0f, z * 100.0f, 0xFF000000 | rgb);
    }

    private int colourAt(float x, float y, float z) {
        int rgb = class_9848.method_61319((float)((float)Math.sqrt(Math.max(y, 0.0f))), (int)this.horizon, (int)this.zenith);
        if (y < 0.0f) {
            rgb = AtmosphereSky.scale(rgb, 1.0f - 0.35f * AtmosphereAurora.smoothstep(-y / 0.4f));
        }
        if (this.glowStrength > 0.001f) {
            float toSun = Math.max(0.0f, x * this.sunX + y * this.sunY);
            float horizontal = Math.max(0.0f, this.sunX < 0.0f ? -x : x);
            float near = (float)(Math.pow(toSun, 8.0) * 0.9 + Math.pow(toSun, 2.0) * 0.25);
            float band = (float)(Math.exp(-Math.abs(y) * 9.0f) * Math.pow(horizontal, 1.5));
            float amount = class_3532.method_15363((float)(this.glowStrength * (near + band * 0.6f)), (float)0.0f, (float)1.0f);
            rgb = class_9848.method_61319((float)amount, (int)rgb, (int)this.glow);
        }
        return rgb & 0xFFFFFF;
    }

    private static int endColourAt(float y) {
        return class_9848.method_61319((float)((float)Math.sqrt(Math.max(y, 0.0f))), (int)1839152, (int)459789) & 0xFFFFFF;
    }

    void renderHalos(class_4587 pose, float rainBrightness, float night) {
        class_287 builder = this.halo.begin();
        float low = 1.0f - AtmosphereAurora.smoothstep(this.sunY / 0.35f);
        float sunAlpha = AtmosphereAurora.smoothstep((this.sunY + 0.12f) / 0.12f) * rainBrightness * (0.45f + 0.35f * low);
        int sunColour = class_9848.method_61319((float)low, (int)16773584, (int)16742963);
        AtmosphereSky.haloQuads(builder, this.sunAngle, 28.0f * (1.0f + 0.6f * low), sunColour, sunAlpha);
        AtmosphereSky.haloQuads(builder, this.moonAngle, 34.0f, 11520255, 0.32f * night * rainBrightness);
        this.halo.upload(builder);
        this.halo.draw(pose, 1.0f);
    }

    private static void haloQuads(class_287 builder, float angle, float radius, int rgb, float alpha) {
        if (alpha <= 0.002f) {
            return;
        }
        float[] radii = new float[]{0.0f, radius * 0.16f, radius * 0.38f, radius * 0.66f, radius};
        float[] alphas = new float[]{1.0f, 0.55f, 0.22f, 0.07f, 0.0f};
        float cosA = class_3532.method_15362((double)angle);
        float sinA = class_3532.method_15374((double)angle);
        for (int ring = 0; ring < 4; ++ring) {
            for (int s = 0; s < 32; ++s) {
                AtmosphereSky.haloVertex(builder, radii[ring], s, cosA, sinA, rgb, alphas[ring] * alpha);
                AtmosphereSky.haloVertex(builder, radii[ring], s + 1, cosA, sinA, rgb, alphas[ring] * alpha);
                AtmosphereSky.haloVertex(builder, radii[ring + 1], s + 1, cosA, sinA, rgb, alphas[ring + 1] * alpha);
                AtmosphereSky.haloVertex(builder, radii[ring + 1], s, cosA, sinA, rgb, alphas[ring + 1] * alpha);
            }
        }
    }

    private static void haloVertex(class_287 builder, float r, int segment, float cosA, float sinA, int rgb, float alpha) {
        float around = (float)segment / 32.0f * ((float)Math.PI * 2);
        float lx = class_3532.method_15362((double)around) * r;
        float ly = 100.0f;
        float lz = class_3532.method_15374((double)around) * r;
        float y = ly * cosA - lz * sinA;
        float z = ly * sinA + lz * cosA;
        SkyMesh.vertex(builder, -z, y, lx, Math.round(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f) << 24 | rgb & 0xFFFFFF);
    }

    void renderMeteors(class_4587 pose, float night, float rainBrightness) {
        long now = System.nanoTime();
        if (now >= this.meteorEnd) {
            if (this.nextMeteor == 0L) {
                this.nextMeteor = now + AtmosphereSky.seconds(4.0f + 10.0f * this.random.method_43057());
            }
            if (now < this.nextMeteor || night < 0.6f || rainBrightness < 0.8f) {
                return;
            }
            this.launch(now);
        }
        float life = (float)(now - this.meteorStart) / (float)(this.meteorEnd - this.meteorStart);
        float fade = class_3532.method_15374((double)((float)Math.PI * class_3532.method_15363((float)life, (float)0.0f, (float)1.0f))) * night * rainBrightness;
        class_287 builder = this.meteorMesh.begin();
        float travel = life * 0.55f;
        float tail = 0.16f;
        float[] prev = null;
        for (int i = 0; i <= 10; ++i) {
            float along = travel - tail * (float)i / 10.0f;
            float[] p = new float[]{(this.mx + this.tx * along) * 100.0f, (this.my + this.ty * along) * 100.0f, (this.mz + this.tz * along) * 100.0f};
            if (prev != null) {
                AtmosphereSky.streak(builder, prev, p, fade * (1.0f - ((float)i - 1.0f) / 10.0f), fade * (1.0f - (float)i / 10.0f));
            }
            prev = p;
        }
        this.meteorMesh.upload(builder);
        this.meteorMesh.draw(pose, 1.0f);
    }

    private void launch(long now) {
        float el = (30.0f + 40.0f * this.random.method_43057()) * ((float)Math.PI / 180);
        float az = this.random.method_43057() * ((float)Math.PI * 2);
        this.mx = class_3532.method_15362((double)el) * class_3532.method_15362((double)az);
        this.my = class_3532.method_15374((double)el);
        this.mz = class_3532.method_15362((double)el) * class_3532.method_15374((double)az);
        float dx = -class_3532.method_15374((double)az);
        float dz = class_3532.method_15362((double)az);
        float dy = -0.6f - 0.4f * this.random.method_43057();
        float dot = dx * this.mx + dy * this.my + dz * this.mz;
        this.tx = dx - dot * this.mx;
        this.ty = dy - dot * this.my;
        this.tz = dz - dot * this.mz;
        float length = class_3532.method_15355((float)(this.tx * this.tx + this.ty * this.ty + this.tz * this.tz));
        this.tx /= length;
        this.ty /= length;
        this.tz /= length;
        this.meteorStart = now;
        this.meteorEnd = now + AtmosphereSky.seconds(0.7f + 0.5f * this.random.method_43057());
        this.nextMeteor = this.meteorEnd + AtmosphereSky.seconds(6.0f + 18.0f * this.random.method_43057());
    }

    private static void streak(class_287 builder, float[] a, float[] b, float alphaA, float alphaB) {
        float lz = b[2] - a[2];
        float ly = b[1] - a[1];
        float wx = a[1] * lz - a[2] * ly;
        float lx = b[0] - a[0];
        float wy = a[2] * lx - a[0] * lz;
        float wz = a[0] * ly - a[1] * lx;
        float wl = class_3532.method_15355((float)(wx * wx + wy * wy + wz * wz));
        if (wl < 1.0E-5f) {
            return;
        }
        float half = 0.22f / wl;
        int ca = Math.round(class_3532.method_15363((float)alphaA, (float)0.0f, (float)1.0f) * 255.0f) << 24 | 0xEAF2FF;
        int cb = Math.round(class_3532.method_15363((float)alphaB, (float)0.0f, (float)1.0f) * 255.0f) << 24 | 0xEAF2FF;
        SkyMesh.vertex(builder, a[0] - (wx *= half), a[1] - (wy *= half), a[2] - (wz *= half), ca);
        SkyMesh.vertex(builder, a[0] + wx, a[1] + wy, a[2] + wz, ca);
        SkyMesh.vertex(builder, b[0] + wx, b[1] + wy, b[2] + wz, cb);
        SkyMesh.vertex(builder, b[0] - wx, b[1] - wy, b[2] - wz, cb);
    }

    private static long seconds(float s) {
        return (long)(s * 1.0E9f);
    }

    static int scale(int rgb, float k) {
        int r = class_3532.method_15340((int)Math.round((float)(rgb >> 16 & 0xFF) * k), (int)0, (int)255);
        int g = class_3532.method_15340((int)Math.round((float)(rgb >> 8 & 0xFF) * k), (int)0, (int)255);
        int b = class_3532.method_15340((int)Math.round((float)(rgb & 0xFF) * k), (int)0, (int)255);
        return rgb & 0xFF000000 | r << 16 | g << 8 | b;
    }

    static int luma(int rgb) {
        int l = Math.round(0.2126f * (float)(rgb >> 16 & 0xFF) + 0.7152f * (float)(rgb >> 8 & 0xFF) + 0.0722f * (float)(rgb & 0xFF));
        return rgb & 0xFF000000 | l << 16 | l << 8 | l;
    }

    void close() {
        this.dome.close();
        this.endDome.close();
        this.halo.close();
        this.meteorMesh.close();
        this.ready = false;
    }
}

