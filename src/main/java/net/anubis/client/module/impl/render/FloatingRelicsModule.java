/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_5819
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.render;

import java.awt.Color;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.glyph.GlyphCanvas;
import net.anubis.client.util.glyph.GlyphShapes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class FloatingRelicsModule
extends ClientModule {
    private static final String COMET = "Comet";
    private static final String FIREBALL = "Fireball";
    private static final String RAINBOW = "Rainbow";
    private static final String NEON = "Neon";
    private static final String ELECTRIC = "Electric";
    private static final String SAKURA = "Sakura";
    private static final String VOID = "Void";
    private static final String AURORA = "Aurora";
    private static final int MAX_RELICS = 32;
    private static final int MAX_HISTORY = 62;
    private static final int SMOOTHING = 3;
    private static final int MAX_POINTS = 188;
    private static final float GRAVITY = 0.016f;
    private static final float BUOYANCY = 0.045f;
    private static final float AIR_DRAG = 0.982f;
    private static final float GROUND_GRIP = 0.86f;
    private static final float ORBIT_PULL = 0.012f;
    private static final float SWIRL = 0.0025f;
    private static final float FOLLOW = 0.035f;
    private static final float WANDER = 0.003f;
    private static final float MAX_SPEED = 0.9f;
    private static final int APPEAR_TICKS = 12;
    private static final int STAGGER_TICKS = 3;
    private static final float[] GLOW_SCALES = new float[]{2.6f, 2.0f, 1.55f, 1.2f, 0.85f, 0.5f};
    private static final float[] GLOW_ALPHAS = new float[]{0.035f, 0.05f, 0.08f, 0.13f, 0.3f, 0.95f};
    private static final float[] PETAL = FloatingRelicsModule.petal();
    private static final float[] ROUND = FloatingRelicsModule.round(24);
    private static final float[] SOFT = FloatingRelicsModule.round(16);
    private static final float SOFT_BELOW = 0.1f;
    private static final long EPOCH = System.nanoTime();
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Comet", "Comet", "Fireball", "Rainbow", "Neon", "Electric", "Sakura", "Void", "Aurora"));
    private final NumberSetting trailLength = this.addSetting("trail-length", new NumberSetting("Trail Length", 1.0f, 0.2f, 3.0f).step(0.1f));
    private final NumberSetting count = this.addSetting("count", new NumberSetting("Count", 6.0f, 1.0f, 32.0f).step(1.0f));
    private final NumberSetting size = this.addSetting("size", new NumberSetting("Size", 0.3f, 0.15f, 0.75f).step(0.05f));
    private final NumberSetting spread = this.addSetting("spread", new NumberSetting("Spread", 2.6f, 1.2f, 6.0f).step(0.1f));
    private final NumberSetting bounce = this.addSetting("bounce", new NumberSetting("Bounce", 0.6f, 0.1f, 0.95f).step(0.05f));
    private final NumberSetting drift = this.addSetting("drift", new NumberSetting("Drift", 0.5f, 0.0f, 2.5f).step(0.1f));
    private final BooleanSetting blocks = this.addSetting("block-bounce", new BooleanSetting("Block Bounce", true));
    private final Relic[] relics = new Relic[32];
    private final GlyphCanvas canvas = new GlyphCanvas();
    private final class_5819 random = class_5819.method_43047();
    private final class_2338.class_2339 cursor = new class_2338.class_2339();
    private final double[] points = new double[564];
    private final double[] raw = new double[186];
    private final double[] near = new double[3];
    private final double[] far = new double[3];
    private int live;
    private int age;
    private class_638 boundLevel;

    public FloatingRelicsModule() {
        super("floating-relics", "Floating Relics", "Glowing orbs float around you and leave a trail as they move.", ModuleCategory.VISUALS);
        for (int i = 0; i < 32; ++i) {
            this.relics[i] = new Relic();
        }
    }

    @Override
    protected void onEnable() {
        this.reset();
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
        this.on(Render3DEvent.TYPE, this::onRender3D);
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    private void reset() {
        this.live = 0;
        this.age = 0;
        this.boundLevel = null;
    }

    private void syncLevel(class_638 level) {
        if (level == this.boundLevel) {
            return;
        }
        this.boundLevel = level;
        this.live = 0;
    }

    private void onTick(TickEvent event) {
        int i;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        class_638 level = mc.field_1687;
        if (player == null || level == null) {
            this.reset();
            return;
        }
        this.syncLevel(level);
        ++this.age;
        int want = Math.min(32, this.count.getInt());
        for (int i2 = this.live; i2 < want; ++i2) {
            this.wake(this.relics[i2], (i2 - this.live) * 3);
        }
        this.live = want;
        double px = player.method_23317();
        double py = player.method_23318();
        double pz = player.method_23321();
        float height = player.method_17682();
        float moveX = (float)(px - player.field_6014);
        float moveZ = (float)(pz - player.field_5969);
        float restitution = this.bounce.get();
        for (i = 0; i < this.live; ++i) {
            this.step(this.relics[i], level, px, py, pz, height, moveX, moveZ, restitution);
        }
        for (i = 0; i < this.live; ++i) {
            for (int j = i + 1; j < this.live; ++j) {
                this.knock(this.relics[i], this.relics[j], restitution);
            }
        }
        for (i = 0; i < this.live; ++i) {
            if (!this.relics[i].placed) continue;
            this.relics[i].remember();
        }
    }

    private void wake(Relic r, int delay) {
        r.placed = false;
        r.wakeAt = this.age + delay;
        r.orbit = 0.55f + 0.45f * this.random.method_43057();
        r.lift = this.random.method_43057();
        r.grain = 0.82f + 0.3f * this.random.method_43057();
        r.phase = this.random.method_43057() * ((float)Math.PI * 2);
    }

    private void place(Relic r, double px, double py, double pz) {
        float angle = this.random.method_43057() * ((float)Math.PI * 2);
        double radius = this.spread.get() * r.orbit;
        r.x = px + (double)class_3532.method_15362((double)angle) * radius;
        r.y = py + 0.15;
        r.z = pz + (double)class_3532.method_15374((double)angle) * radius;
        r.lastX = r.x;
        r.lastY = r.y;
        r.lastZ = r.z;
        r.vx = -class_3532.method_15374((double)angle) * 0.03f;
        r.vy = 0.14f + 0.08f * this.random.method_43057();
        r.vz = class_3532.method_15362((double)angle) * 0.03f;
        r.placed = true;
        r.placedAt = this.age;
        r.head = 0;
        r.filled = 0;
    }

    private void step(Relic r, class_638 level, double px, double py, double pz, float height, float moveX, float moveZ, float restitution) {
        if (!r.placed) {
            if (this.age < r.wakeAt) {
                return;
            }
            this.place(r, px, py, pz);
        }
        r.lastX = r.x;
        r.lastY = r.y;
        r.lastZ = r.z;
        double dx = r.x - px;
        double dz = r.z - pz;
        double flat = Math.sqrt(dx * dx + dz * dz);
        double hover = py + 0.35 + (double)r.lift * ((double)height + 0.2) + 0.16 * (double)class_3532.method_15374((double)(((float)this.age + r.phase * 16.0f) * 0.045f));
        double leash = (double)this.spread.get() * 3.0 + 8.0;
        if (flat > leash || Math.abs(r.y - hover) > leash) {
            this.place(r, px, py, pz);
            return;
        }
        float nx = flat > 1.0E-4 ? (float)(dx / flat) : 1.0f;
        float nz = flat > 1.0E-4 ? (float)(dz / flat) : 0.0f;
        float pull = (float)((double)(this.spread.get() * r.orbit) - flat) * 0.012f;
        float swirl = this.drift.get() * 0.0025f;
        r.vx += nx * pull - nz * swirl + moveX * 0.035f;
        r.vz += nz * pull + nx * swirl + moveZ * 0.035f;
        float wander = ((float)this.age + r.phase * 20.0f) * 0.06f;
        r.vx += class_3532.method_15374((double)wander) * 0.003f;
        r.vz += class_3532.method_15362((double)(wander * 1.31f)) * 0.003f;
        r.vy -= 0.016f;
        double sink = hover - r.y;
        if (sink > 0.0) {
            r.vy += (float)Math.min(sink, 1.5) * 0.045f;
        }
        r.vx *= 0.982f;
        r.vy *= 0.982f;
        r.vz *= 0.982f;
        float speed = r.vx * r.vx + r.vy * r.vy + r.vz * r.vz;
        if (speed > 0.80999994f) {
            float k = 0.9f / (float)Math.sqrt(speed);
            r.vx *= k;
            r.vy *= k;
            r.vz *= k;
        }
        boolean collide = this.blocks.get();
        float half = this.size.get() * r.grain * 0.5f;
        double next = r.x + (double)r.vx;
        if (collide && this.solid(level, next + (double)Math.copySign(half, r.vx), r.y, r.z)) {
            r.vx = -r.vx * restitution;
        } else {
            r.x = next;
        }
        next = r.y + (double)r.vy;
        if (collide && this.solid(level, r.x, next + (double)Math.copySign(half, r.vy), r.z)) {
            if (r.vy < 0.0f) {
                r.vx *= 0.86f;
                r.vz *= 0.86f;
            }
            r.vy = -r.vy * restitution;
        } else {
            r.y = next;
        }
        next = r.z + (double)r.vz;
        if (collide && this.solid(level, r.x, r.y, next + (double)Math.copySign(half, r.vz))) {
            r.vz = -r.vz * restitution;
        } else {
            r.z = next;
        }
        if (collide && this.solid(level, r.x, r.y, r.z)) {
            r.y += 0.2;
            r.lastY = r.y;
            r.vy = Math.max(r.vy, 0.08f);
        }
    }

    private void knock(Relic a, Relic b, float restitution) {
        if (!a.placed || !b.placed) {
            return;
        }
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dz = b.z - a.z;
        double d2 = dx * dx + dy * dy + dz * dz;
        double reach = (double)(this.size.get() * (a.grain + b.grain)) * 0.5 * 0.95;
        if (d2 >= reach * reach || d2 < 1.0E-8) {
            return;
        }
        double d = Math.sqrt(d2);
        float nx = (float)(dx / d);
        float ny = (float)(dy / d);
        float nz = (float)(dz / d);
        double push = (reach - d) * 0.5;
        a.x -= (double)nx * push;
        a.y -= (double)ny * push;
        a.z -= (double)nz * push;
        b.x += (double)nx * push;
        b.y += (double)ny * push;
        b.z += (double)nz * push;
        float closing = (a.vx - b.vx) * nx + (a.vy - b.vy) * ny + (a.vz - b.vz) * nz;
        if (closing <= 0.0f) {
            return;
        }
        float impulse = closing * (1.0f + restitution) * 0.5f;
        a.vx -= impulse * nx;
        a.vy -= impulse * ny;
        a.vz -= impulse * nz;
        b.vx += impulse * nx;
        b.vy += impulse * ny;
        b.vz += impulse * nz;
    }

    private boolean solid(class_638 level, double x, double y, double z) {
        this.cursor.method_10103(class_3532.method_15357((double)x), class_3532.method_15357((double)y), class_3532.method_15357((double)z));
        class_2680 state = level.method_8320((class_2338)this.cursor);
        if (state.method_26215()) {
            return false;
        }
        class_265 shape = state.method_26220((class_1922)level, (class_2338)this.cursor);
        if (shape.method_1110()) {
            return false;
        }
        double lx = x - (double)this.cursor.method_10263();
        double ly = y - (double)this.cursor.method_10264();
        double lz = z - (double)this.cursor.method_10260();
        return lx >= shape.method_1091(class_2350.class_2351.field_11048) && lx <= shape.method_1105(class_2350.class_2351.field_11048) && ly >= shape.method_1091(class_2350.class_2351.field_11052) && ly <= shape.method_1105(class_2350.class_2351.field_11052) && lz >= shape.method_1091(class_2350.class_2351.field_11051) && lz <= shape.method_1105(class_2350.class_2351.field_11051);
    }

    private void onRender3D(Render3DEvent event) {
        if (this.live == 0) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.canvas.begin(mc.field_1773.method_19418());
        float pt = event.partialTick;
        float time = (float)(System.nanoTime() - EPOCH) / 1.0E9f;
        int samples = Math.max(2, Math.min(60, Math.round(this.trailLength.get() * 20.0f)));
        String look = this.style.get();
        for (int i = 0; i < this.live; ++i) {
            Relic r = this.relics[i];
            if (!r.placed) continue;
            float since = (float)(this.age - r.placedAt) + pt;
            float grow = FloatingRelicsModule.appear(since / 12.0f);
            float extent = this.size.get() * 0.5f * r.grain * grow;
            if (extent < 0.004f) continue;
            int n = this.trail(r, pt, samples);
            this.drawTrail(look, i, n, extent, time);
            int h = (n - 1) * 3;
            this.drawHead(look, this.points[h], this.points[h + 1], this.points[h + 2], extent, time, r.phase);
        }
        this.canvas.submit(false);
    }

    private int trail(Relic r, float pt, int samples) {
        int m = Math.min(r.filled, samples + 2);
        if (m < 2) {
            this.points[0] = class_3532.method_16436((double)pt, (double)r.lastX, (double)r.x);
            this.points[1] = class_3532.method_16436((double)pt, (double)r.lastY, (double)r.y);
            this.points[2] = class_3532.method_16436((double)pt, (double)r.lastZ, (double)r.z);
            return 1;
        }
        for (int k = 0; k < m; ++k) {
            int slot = Math.floorMod(r.head - m + k, 62) * 3;
            this.raw[k * 3] = r.history[slot];
            this.raw[k * 3 + 1] = r.history[slot + 1];
            this.raw[k * 3 + 2] = r.history[slot + 2];
        }
        double head = (float)(m - 2) + pt;
        double tail = Math.max(0.0, head - (double)samples);
        int n = Math.max(2, (int)Math.ceil((head - tail) * 3.0) + 1);
        for (int j = 0; j < n; ++j) {
            double u = j == n - 1 ? head : tail + (head - tail) * (double)j / (double)(n - 1);
            int k = Math.min(m - 2, (int)u);
            double t = u - (double)k;
            int k0 = Math.max(0, k - 1) * 3;
            int k1 = k * 3;
            int k2 = (k + 1) * 3;
            int k3 = Math.min(m - 1, k + 2) * 3;
            for (int axis = 0; axis < 3; ++axis) {
                this.points[j * 3 + axis] = FloatingRelicsModule.catmull(this.raw[k0 + axis], this.raw[k1 + axis], this.raw[k2 + axis], this.raw[k3 + axis], t);
            }
        }
        return n;
    }

    private static double catmull(double p0, double p1, double p2, double p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return 0.5 * (2.0 * p1 + (p2 - p0) * t + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t2 + (3.0 * p1 - p0 - 3.0 * p2 + p3) * t3);
    }

    private void drawTrail(String look, int relic, int n, float extent, float time) {
        if (n < 2) {
            return;
        }
        float width = extent * 0.7f;
        block18: for (int k = 1; k < n; ++k) {
            float t = (float)k / (float)(n - 1);
            float tb = (float)(k - 1) / (float)(n - 1);
            int a = (k - 1) * 3;
            int b = k * 3;
            int back = n - 1 - k;
            double ax = this.points[a];
            double ay = this.points[a + 1];
            double az = this.points[a + 2];
            double bx = this.points[b];
            double by = this.points[b + 1];
            double bz = this.points[b + 2];
            switch (look) {
                case "Fireball": {
                    if (back % 3 == 0) {
                        int col = FloatingRelicsModule.lerpRgb(13114880, 16760896, t);
                        this.glowDisk(bx, by, bz, extent * (0.35f + 0.8f * t), col, 0.55f * t * t);
                    }
                    if (back % 4 != 0) continue block18;
                    long seed = (long)relic * 7919L + (long)back * 104729L + (long)(time * 6.0f);
                    double sx = bx + FloatingRelicsModule.jitter(seed, 1) * (double)extent * 1.6;
                    double sy = by + FloatingRelicsModule.jitter(seed, 2) * (double)extent + (double)((1.0f - t) * extent) * 2.4;
                    double sz = bz + FloatingRelicsModule.jitter(seed, 3) * (double)extent * 1.6;
                    this.canvas.sprite(GlyphShapes.DISK, sx, sy, sz, extent * 0.09f, 0.0f, FloatingRelicsModule.withAlpha(16765562, 0.9f * t));
                    continue block18;
                }
                case "Rainbow": {
                    int col = Color.HSBtoRGB(t * 0.85f - time * 0.25f, 0.85f, 1.0f) & 0xFFFFFF;
                    this.strip(ax, ay, az, bx, by, bz, width * tb, width * t, FloatingRelicsModule.withAlpha(col, 0.92f * t));
                    continue block18;
                }
                case "Neon": {
                    int col = FloatingRelicsModule.lerpRgb(16728018, 0x3FF2FF, t);
                    this.strip(ax, ay, az, bx, by, bz, width * 1.5f * tb, width * 1.5f * t, FloatingRelicsModule.withAlpha(col, 0.22f * t));
                    this.strip(ax, ay, az, bx, by, bz, width * 0.6f * tb, width * 0.6f * t, FloatingRelicsModule.withAlpha(col, 0.8f * t));
                    this.strip(ax, ay, az, bx, by, bz, width * 0.18f, width * 0.18f, FloatingRelicsModule.withAlpha(0xFFFFFF, 0.9f * t));
                    continue block18;
                }
                case "Electric": {
                    long seed = (long)relic * 7919L + (long)back * 104729L + (long)(time * 14.0f);
                    FloatingRelicsModule.jag(this.far, ax, ay, az, seed + 104729L, (double)extent * 0.7 * (double)(1.0f - tb * 0.5f));
                    FloatingRelicsModule.jag(this.near, bx, by, bz, seed, (double)extent * 0.7 * (double)(1.0f - t * 0.5f));
                    this.strip(this.far[0], this.far[1], this.far[2], this.near[0], this.near[1], this.near[2], width * 0.6f, width * 0.6f, FloatingRelicsModule.withAlpha(6273023, 0.3f * t));
                    this.strip(this.far[0], this.far[1], this.far[2], this.near[0], this.near[1], this.near[2], width * 0.16f, width * 0.16f, FloatingRelicsModule.withAlpha(0xE6F6FF, 0.95f * t));
                    continue block18;
                }
                case "Sakura": {
                    if (back % 6 != 0) continue block18;
                    long seed = (long)relic * 7919L + (long)back * 104729L;
                    double fall = (double)((1.0f - t) * extent) * 3.0;
                    float spin = time * 2.1f + (float)back * 0.7f + (float)relic;
                    this.canvas.sprite(PETAL, bx += FloatingRelicsModule.jitter(seed, 1) * (double)extent * (double)(1.4f - t), by - fall, bz += FloatingRelicsModule.jitter(seed, 3) * (double)extent * (double)(1.4f - t), extent * (0.45f + 0.45f * t), spin, FloatingRelicsModule.withAlpha(FloatingRelicsModule.lerpRgb(0xFFE4EE, 0xFF8FB8, t), 0.95f * t));
                    continue block18;
                }
                case "Void": {
                    for (int strand = 0; strand < 2; ++strand) {
                        FloatingRelicsModule.helix(this.far, ax, ay, az, bx, by, bz, tb, time, strand, extent);
                        FloatingRelicsModule.helix(this.near, bx, by, bz, bx + (bx - ax), by + (by - ay), bz + (bz - az), t, time, strand, extent);
                        int col = strand == 0 ? 12472575 : 16729800;
                        this.strip(this.far[0], this.far[1], this.far[2], this.near[0], this.near[1], this.near[2], width * 0.32f, width * 0.32f, FloatingRelicsModule.withAlpha(col, 0.85f * t));
                    }
                    continue block18;
                }
                case "Aurora": {
                    int col = FloatingRelicsModule.lerpRgb(4194208, 11558911, 0.5f + 0.5f * class_3532.method_15374((double)(t * 6.0f + time * 1.5f)));
                    float shimmer = 0.45f + 0.55f * Math.abs(class_3532.method_15374((double)((float)k * 0.9f + time * 3.0f)));
                    double tall = (double)extent * 3.2 * (double)t * (double)(0.7f + 0.3f * shimmer);
                    this.curtain(ax, ay, az, bx, by, bz, (double)extent * 3.2 * (double)tb * 0.5, tall * 0.5, 0.0, FloatingRelicsModule.withAlpha(col, 0.5f * t * shimmer));
                    this.curtain(ax, ay, az, bx, by, bz, (double)extent * 3.2 * (double)tb, tall, (double)extent * 3.2 * (double)tb * 0.5, FloatingRelicsModule.withAlpha(col, 0.16f * t * shimmer), tall * 0.5);
                    continue block18;
                }
                default: {
                    int col = FloatingRelicsModule.lerpRgb(4160511, 13625599, t);
                    this.strip(ax, ay, az, bx, by, bz, width * 1.9f * tb, width * 1.9f * t, FloatingRelicsModule.withAlpha(col, 0.16f * t * t));
                    this.strip(ax, ay, az, bx, by, bz, width * tb, width * t, FloatingRelicsModule.withAlpha(col, 0.75f * t * t));
                }
            }
        }
    }

    private void drawHead(String look, double x, double y, double z, float extent, float time, float phase) {
        switch (look) {
            case "Fireball": {
                this.glow(x, y, z, extent * 1.1f, 16742938, 0.9f);
                this.glowDisk(x, y, z, extent * 0.45f, 16773808, 1.0f);
                break;
            }
            case "Rainbow": 
            case "Neon": {
                this.glow(x, y, z, extent * 0.9f, 0xFFFFFF, 1.0f);
                break;
            }
            case "Electric": {
                this.glow(x, y, z, extent * 1.1f, 6273023, 0.9f);
                this.glowDisk(x, y, z, extent * 0.4f, 0xFFFFFF, 1.0f);
                break;
            }
            case "Sakura": {
                this.glow(x, y, z, extent * 0.9f, 16757713, 0.8f);
                this.canvas.sprite(PETAL, x, y, z, extent * 0.85f, time * 1.6f + phase, FloatingRelicsModule.withAlpha(16751812, 1.0f));
                break;
            }
            case "Void": {
                this.glow(x, y, z, extent * 1.2f, 10174463, 0.85f);
                this.canvas.sprite(GlyphShapes.HALO, x, y, z, extent * 0.62f, time * 2.0f, FloatingRelicsModule.withAlpha(14456575, 0.95f));
                this.canvas.sprite(GlyphShapes.DISK, x, y, z, extent * 0.5f, 0.0f, FloatingRelicsModule.withAlpha(656400, 1.0f));
                break;
            }
            case "Aurora": {
                this.glow(x, y, z, extent * 0.8f, 13697000, 1.0f);
                break;
            }
            default: {
                this.glow(x, y, z, extent * 1.2f, 9422079, 0.85f);
                this.glowDisk(x, y, z, extent * 0.38f, 0xFFFFFF, 1.0f);
            }
        }
    }

    private void glow(double x, double y, double z, float radius, int rgb, float alpha) {
        for (int layer = 0; layer < GLOW_SCALES.length; ++layer) {
            int col = layer == GLOW_SCALES.length - 1 ? FloatingRelicsModule.lerpRgb(rgb, 0xFFFFFF, 0.6f) : rgb;
            float[] disk = GLOW_ALPHAS[layer] < 0.1f ? SOFT : ROUND;
            this.canvas.sprite(disk, x, y, z, radius * GLOW_SCALES[layer], 0.0f, FloatingRelicsModule.withAlpha(col, alpha * GLOW_ALPHAS[layer]));
        }
    }

    private void glowDisk(double x, double y, double z, float radius, int rgb, float alpha) {
        this.canvas.sprite(SOFT, x, y, z, radius * 1.6f, 0.0f, FloatingRelicsModule.withAlpha(rgb, alpha * 0.25f));
        this.canvas.sprite(ROUND, x, y, z, radius, 0.0f, FloatingRelicsModule.withAlpha(rgb, alpha));
    }

    private void strip(double ax, double ay, double az, double bx, double by, double bz, float wa, float wb, int argb) {
        double sz;
        double sy;
        if (wa <= 0.0f && wb <= 0.0f) {
            return;
        }
        double dx = bx - ax;
        double dy = by - ay;
        double dz = bz - az;
        double tx = this.canvas.camX() - (ax + bx) * 0.5;
        double ty = this.canvas.camY() - (ay + by) * 0.5;
        double tz = this.canvas.camZ() - (az + bz) * 0.5;
        double sx = dy * tz - dz * ty;
        double length = Math.sqrt(sx * sx + (sy = dz * tx - dx * tz) * sy + (sz = dx * ty - dy * tx) * sz);
        if (length < 1.0E-9) {
            return;
        }
        this.canvas.quad(ax + (sx /= length) * (double)wa, ay + (sy /= length) * (double)wa, az + (sz /= length) * (double)wa, bx + sx * (double)wb, by + sy * (double)wb, bz + sz * (double)wb, bx - sx * (double)wb, by - sy * (double)wb, bz - sz * (double)wb, ax - sx * (double)wa, ay - sy * (double)wa, az - sz * (double)wa, argb);
    }

    private void curtain(double ax, double ay, double az, double bx, double by, double bz, double topA, double topB, double base, int argb) {
        this.curtain(ax, ay, az, bx, by, bz, topA, topB, base, argb, base);
    }

    private void curtain(double ax, double ay, double az, double bx, double by, double bz, double topA, double topB, double baseA, int argb, double baseB) {
        if (topA <= baseA && topB <= baseB) {
            return;
        }
        this.canvas.quad(ax, ay + baseA, az, bx, by + baseB, bz, bx, by + topB, bz, ax, ay + topA, az, argb);
        this.canvas.quad(ax, ay + topA, az, bx, by + topB, bz, bx, by + baseB, bz, ax, ay + baseA, az, argb);
    }

    private static void jag(double[] out, double x, double y, double z, long seed, double reach) {
        out[0] = x + FloatingRelicsModule.jitter(seed, 1) * reach;
        out[1] = y + FloatingRelicsModule.jitter(seed, 2) * reach;
        out[2] = z + FloatingRelicsModule.jitter(seed, 3) * reach;
    }

    private static void helix(double[] out, double ax, double ay, double az, double bx, double by, double bz, float t, float time, int strand, float extent) {
        double dx = bx - ax;
        double dy = by - ay;
        double dz = bz - az;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-9) {
            out[0] = ax;
            out[1] = ay;
            out[2] = az;
            return;
        }
        dx /= length;
        dy /= length;
        double ux = -(dz /= length);
        double uy = 0.0;
        double uz = dx;
        double ul = Math.sqrt(ux * ux + uz * uz);
        if (ul < 1.0E-6) {
            ux = 1.0;
            uz = 0.0;
            ul = 1.0;
        }
        double vx = dy * (uz /= ul) - dz * uy;
        double vy = dz * (ux /= ul) - dx * uz;
        double vz = dx * uy - dy * ux;
        double angle = (double)t * 9.0 + (double)time * 4.0 + (double)strand * Math.PI;
        double radius = (double)extent * (0.9 - 0.5 * (double)t);
        double c = Math.cos(angle) * radius;
        double s = Math.sin(angle) * radius;
        out[0] = ax + ux * c + vx * s;
        out[1] = ay + uy * c + vy * s;
        out[2] = az + uz * c + vz * s;
    }

    private static double jitter(long seed, int axis) {
        long h = seed * -7046029254386353131L + (long)axis * -4417276706812531889L;
        h ^= h >>> 31;
        h *= -4658895280553007687L;
        h ^= h >>> 29;
        return (double)(h >>> 11) / 9.007199254740992E15 * 2.0 - 1.0;
    }

    private static float[] round(int segments) {
        float[] out = new float[segments / 2 * 8];
        for (int k = 0; k < segments; k += 2) {
            int o = k / 2 * 8;
            for (int v = 0; v < 3; ++v) {
                float angle = (float)(k + v) * ((float)Math.PI * 2) / (float)segments;
                out[o + 2 + v * 2] = class_3532.method_15362((double)angle);
                out[o + 3 + v * 2] = class_3532.method_15374((double)angle);
            }
        }
        return out;
    }

    private static float[] petal() {
        int n = 16;
        double[] px = new double[n];
        double[] py = new double[n];
        for (int k = 0; k < n; ++k) {
            double t = (double)k / (double)n * Math.PI * 2.0;
            double notch = 1.0 - 0.28 * Math.pow(Math.max(0.0, Math.cos(t - 1.5707963267948966)), 12.0);
            px[k] = Math.cos(t) * 0.62 * (0.75 + 0.25 * Math.sin(t)) * notch;
            py[k] = Math.sin(t) * notch;
        }
        float[] out = new float[n * 8];
        for (int k = 0; k < n; ++k) {
            int j = (k + 1) % n;
            int o = k * 8;
            out[o] = 0.0f;
            out[o + 1] = 0.0f;
            out[o + 2] = (float)px[k];
            out[o + 3] = (float)py[k];
            out[o + 4] = (float)px[j];
            out[o + 5] = (float)py[j];
            out[o + 6] = (float)px[j];
            out[o + 7] = (float)py[j];
        }
        return out;
    }

    private static float appear(float t) {
        if (t >= 1.0f) {
            return 1.0f;
        }
        if (t <= 0.0f) {
            return 0.0f;
        }
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv + 0.22f * class_3532.method_15374((double)(t * (float)Math.PI)) * inv;
    }

    private static int lerpRgb(int a, int b, float t) {
        t = class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        int r = (int)((float)(a >> 16 & 0xFF) + (float)((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * t);
        int g = (int)((float)(a >> 8 & 0xFF) + (float)((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * t);
        int bl = (int)((float)(a & 0xFF) + (float)((b & 0xFF) - (a & 0xFF)) * t);
        return r << 16 | g << 8 | bl;
    }

    private static int withAlpha(int rgb, float alpha) {
        int a = (int)(Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f + 0.5f);
        return a << 24 | rgb & 0xFFFFFF;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Relic {
        double x;
        double y;
        double z;
        double lastX;
        double lastY;
        double lastZ;
        float vx;
        float vy;
        float vz;
        float orbit;
        float lift;
        float grain;
        float phase;
        boolean placed;
        int wakeAt;
        int placedAt;
        final double[] history = new double[186];
        int head;
        int filled;

        private Relic() {
        }

        void remember() {
            this.history[this.head * 3] = this.x;
            this.history[this.head * 3 + 1] = this.y;
            this.history[this.head * 3 + 2] = this.z;
            this.head = (this.head + 1) % 62;
            this.filled = Math.min(62, this.filled + 1);
        }
    }
}

