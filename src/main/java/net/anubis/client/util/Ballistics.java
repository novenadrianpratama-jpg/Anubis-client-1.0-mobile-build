/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1301
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_3486
 *  net.minecraft.class_3532
 *  net.minecraft.class_3610
 *  net.minecraft.class_3726
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 */
package net.anubis.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1301;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;

@Environment(value=EnvType.CLIENT)
public final class Ballistics {
    public static final int DEFAULT_LIFETIME = 240;
    private static final double VOID_DEPTH = 64.0;
    private static final double FULL_MARGIN = 0.3;
    private static final double SEARCH_PAD = 1.3;
    private static final double FLUID_SKIN = 0.001;
    private static final double SHOOTER_CLEARANCE = 1.0;

    private Ballistics() {
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Flight {
        private double[] xs = new double[96];
        private double[] ys = new double[96];
        private double[] zs = new double[96];
        private int size;
        private int ticks;
        private double length;
        private Outcome outcome = Outcome.OPEN;
        private class_2350 face = class_2350.field_11036;
        private class_1297 struck;
        private Body body = Body.ARROW;
        private double px;
        private double py;
        private double pz;
        private double vx;
        private double vy;
        private double vz;
        private double gravity;
        private double width;
        private double height;
        private boolean wet;
        private int age;
        private int lifetime = 240;
        private class_1297 shooter;
        private class_1297 self;
        private boolean leftShooter;
        private class_1297 shooterRoot;
        private final List<class_238> shooterBoxes = new ArrayList<class_238>();
        private final class_2338.class_2339 cursor = new class_2338.class_2339();
        private double near;
        private double far;

        public Flight launch(Body body, class_243 from, class_243 motion) {
            this.body = body;
            this.px = from.field_1352;
            this.py = from.field_1351;
            this.pz = from.field_1350;
            this.vx = motion.field_1352;
            this.vy = motion.field_1351;
            this.vz = motion.field_1350;
            this.gravity = body.gravity;
            this.width = body.size;
            this.height = body.size;
            this.wet = false;
            this.age = 0;
            this.lifetime = 240;
            this.shooter = null;
            this.self = null;
            this.leftShooter = false;
            return this;
        }

        public Flight gravity(double value) {
            this.gravity = value;
            return this;
        }

        public Flight bounds(double w, double h) {
            this.width = w;
            this.height = h;
            return this;
        }

        public Flight wet(boolean value) {
            this.wet = value;
            return this;
        }

        public Flight age(int ticksAlive) {
            this.age = ticksAlive;
            return this;
        }

        public Flight lifetime(int maxTicks) {
            this.lifetime = Math.max(1, maxTicks);
            return this;
        }

        public Flight ignoring(class_1297 shooter, class_1297 self) {
            this.shooter = shooter;
            this.self = self;
            return this;
        }

        public Flight leftShooter(boolean value) {
            this.leftShooter = value;
            return this;
        }

        public Flight solve(class_1937 level) {
            this.size = 0;
            this.struck = null;
            this.outcome = Outcome.OPEN;
            this.face = class_2350.field_11036;
            this.trace(level);
            this.strike(level);
            this.measure();
            this.release();
            return this;
        }

        public void clear() {
            this.size = 0;
            this.length = 0.0;
            this.struck = null;
            this.leftShooter = false;
            this.outcome = Outcome.OPEN;
            this.release();
        }

        private void release() {
            this.shooter = null;
            this.self = null;
            this.shooterRoot = null;
            this.shooterBoxes.clear();
        }

        public int size() {
            return this.size;
        }

        public double x(int i) {
            return this.xs[i];
        }

        public double y(int i) {
            return this.ys[i];
        }

        public double z(int i) {
            return this.zs[i];
        }

        public class_243 point(int i) {
            return new class_243(this.xs[i], this.ys[i], this.zs[i]);
        }

        public class_243 impact() {
            return this.point(this.size - 1);
        }

        public class_243 heading() {
            if (this.size < 2) {
                return class_243.field_1353;
            }
            int last = this.size - 1;
            return new class_243(this.xs[last] - this.xs[last - 1], this.ys[last] - this.ys[last - 1], this.zs[last] - this.zs[last - 1]);
        }

        public int ticks() {
            return this.ticks;
        }

        public double length() {
            return this.length;
        }

        public Outcome outcome() {
            return this.outcome;
        }

        public class_2350 face() {
            return this.face;
        }

        public class_1297 struck() {
            return this.struck;
        }

        public boolean hasLeftShooter() {
            return this.leftShooter;
        }

        private void trace(class_1937 level) {
            class_3726 context = this.self != null ? class_3726.method_16195((class_1297)this.self) : class_3726.method_16194();
            double floor = (double)level.method_31607() - 64.0;
            double x = this.px;
            double y = this.py;
            double z = this.pz;
            double mx = this.vx;
            double my = this.vy;
            double mz = this.vz;
            boolean inWater = this.wet;
            this.push(x, y, z);
            int tick = 0;
            while (tick < this.lifetime) {
                double nz;
                double ny;
                double nx;
                class_3965 hit;
                ++tick;
                if (!this.body.movesFirst) {
                    my -= this.gravity;
                    double keep = inWater ? this.body.waterKeep : this.body.airKeep;
                    mx *= keep;
                    my *= keep;
                    mz *= keep;
                }
                if ((hit = level.method_61717(new class_3959(new class_243(x, y, z), new class_243(nx = x + mx, ny = y + my, nz = z + mz), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, context))).method_17783() != class_239.class_240.field_1333) {
                    class_243 at = hit.method_17784();
                    this.push(at.field_1352, at.field_1351, at.field_1350);
                    this.face = hit.method_17780();
                    this.outcome = Outcome.BLOCK;
                    this.ticks = tick;
                    return;
                }
                x = nx;
                y = ny;
                z = nz;
                this.push(x, y, z);
                if (this.body.movesFirst) {
                    double keep = inWater ? this.body.waterKeep : this.body.airKeep;
                    mx *= keep;
                    my = my * keep - this.gravity;
                    mz *= keep;
                }
                inWater = this.submerged(level, x, y, z);
                if (!(y < floor)) continue;
                break;
            }
            this.ticks = tick;
            if (this.body == Body.ROCKET && tick >= this.lifetime) {
                this.outcome = Outcome.BURST;
            }
        }

        private void strike(class_1937 level) {
            boolean clear;
            if (this.size < 2) {
                return;
            }
            this.leftShooter = clear = this.leftShooter || this.departs();
            double minX = this.xs[0];
            double minY = this.ys[0];
            double minZ = this.zs[0];
            double maxX = minX;
            double maxY = minY;
            double maxZ = minZ;
            for (int i = 1; i < this.size; ++i) {
                minX = Math.min(minX, this.xs[i]);
                minY = Math.min(minY, this.ys[i]);
                minZ = Math.min(minZ, this.zs[i]);
                maxX = Math.max(maxX, this.xs[i]);
                maxY = Math.max(maxY, this.ys[i]);
                maxZ = Math.max(maxZ, this.zs[i]);
            }
            class_238 span = new class_238(minX, minY, minZ, maxX, maxY, maxZ).method_1014(1.3);
            List candidates = level.method_8333(this.self, span, this::canStrike);
            if (candidates.isEmpty()) {
                return;
            }
            for (int i = 1; i < this.size; ++i) {
                if (!clear && i > 1) {
                    clear = this.outside(i - 1);
                }
                double ax = this.xs[i - 1];
                double ay = this.ys[i - 1];
                double az = this.zs[i - 1];
                double dx = this.xs[i] - ax;
                double dy = this.ys[i] - ay;
                double dz = this.zs[i] - az;
                double margin = class_3532.method_15350((double)((double)(this.age + i - 2) / 20.0), (double)0.0, (double)0.3);
                double best = Double.MAX_VALUE;
                class_1297 victim = null;
                for (int c = 0; c < candidates.size(); ++c) {
                    class_238 box;
                    double t;
                    class_1297 entity = (class_1297)candidates.get(c);
                    if (!clear && entity.method_5668() == this.shooterRoot || !((t = this.entry(ax, ay, az, dx, dy, dz, box = entity.method_5829(), margin)) < best)) continue;
                    best = t;
                    victim = entity;
                }
                if (victim == null) continue;
                this.xs[i] = ax + dx * best;
                this.ys[i] = ay + dy * best;
                this.zs[i] = az + dz * best;
                this.size = i + 1;
                this.ticks = i;
                this.struck = victim;
                this.outcome = Outcome.ENTITY;
                return;
            }
        }

        private boolean canStrike(class_1297 entity) {
            return !entity.method_7325() && entity.method_49108();
        }

        private boolean departs() {
            class_1297 class_12972 = this.shooterRoot = this.shooter != null ? this.shooter.method_5668() : null;
            if (this.shooterRoot == null) {
                return true;
            }
            this.shooterBoxes.clear();
            if (class_1301.field_52443.test(this.shooterRoot)) {
                this.shooterBoxes.add(this.shooterRoot.method_5829());
            }
            for (class_1297 rider : this.shooterRoot.method_5736()) {
                if (!class_1301.field_52443.test(rider)) continue;
                this.shooterBoxes.add(rider.method_5829());
            }
            return this.outside(0);
        }

        private boolean outside(int i) {
            double x = this.xs[i];
            double y = this.ys[i];
            double z = this.zs[i];
            double dx = this.xs[i + 1] - x;
            double dy = this.ys[i + 1] - y;
            double dz = this.zs[i + 1] - z;
            double half = this.width * 0.5;
            double minX = x - half + Math.min(dx, 0.0) - 1.0;
            double maxX = x + half + Math.max(dx, 0.0) + 1.0;
            double minY = y + Math.min(dy, 0.0) - 1.0;
            double maxY = y + this.height + Math.max(dy, 0.0) + 1.0;
            double minZ = z - half + Math.min(dz, 0.0) - 1.0;
            double maxZ = z + half + Math.max(dz, 0.0) + 1.0;
            for (int b = 0; b < this.shooterBoxes.size(); ++b) {
                if (!this.shooterBoxes.get(b).method_1003(minX, minY, minZ, maxX, maxY, maxZ)) continue;
                return false;
            }
            return true;
        }

        private double entry(double ax, double ay, double az, double dx, double dy, double dz, class_238 box, double margin) {
            this.near = 0.0;
            this.far = 1.0;
            if (!this.slab(ax, dx, box.field_1323 - margin, box.field_1320 + margin)) {
                return Double.MAX_VALUE;
            }
            if (!this.slab(ay, dy, box.field_1322 - margin, box.field_1325 + margin)) {
                return Double.MAX_VALUE;
            }
            if (!this.slab(az, dz, box.field_1321 - margin, box.field_1324 + margin)) {
                return Double.MAX_VALUE;
            }
            return this.near > 0.0 ? this.near : Double.MAX_VALUE;
        }

        private boolean slab(double origin, double delta, double lo, double hi) {
            if (Math.abs(delta) < 1.0E-12) {
                return origin >= lo && origin <= hi;
            }
            double enter = (lo - origin) / delta;
            double exit = (hi - origin) / delta;
            if (enter > exit) {
                double swap = enter;
                enter = exit;
                exit = swap;
            }
            if (enter > this.near) {
                this.near = enter;
            }
            if (exit < this.far) {
                this.far = exit;
            }
            return this.near <= this.far;
        }

        private boolean submerged(class_1937 level, double x, double y, double z) {
            double half = this.width * 0.5 - 0.001;
            double bottom = y + 0.001;
            int x0 = class_3532.method_15357((double)(x - half));
            int x1 = class_3532.method_15357((double)(x + half));
            int y0 = class_3532.method_15357((double)bottom);
            int y1 = class_3532.method_15357((double)(y + this.height - 0.001));
            int z0 = class_3532.method_15357((double)(z - half));
            int z1 = class_3532.method_15357((double)(z + half));
            for (int bx = x0; bx <= x1; ++bx) {
                for (int by = y0; by <= y1; ++by) {
                    for (int bz = z0; bz <= z1; ++bz) {
                        class_3610 fluid = level.method_8316((class_2338)this.cursor.method_10103(bx, by, bz));
                        if (!fluid.method_15767(class_3486.field_15517) || !((double)((float)by + fluid.method_15763((class_1922)level, (class_2338)this.cursor)) >= bottom)) continue;
                        return true;
                    }
                }
            }
            return false;
        }

        private void measure() {
            double total = 0.0;
            for (int i = 1; i < this.size; ++i) {
                double dx = this.xs[i] - this.xs[i - 1];
                double dy = this.ys[i] - this.ys[i - 1];
                double dz = this.zs[i] - this.zs[i - 1];
                total += Math.sqrt(dx * dx + dy * dy + dz * dz);
            }
            this.length = total;
        }

        private void push(double x, double y, double z) {
            if (this.size == this.xs.length) {
                int grown = this.size * 2;
                this.xs = Arrays.copyOf(this.xs, grown);
                this.ys = Arrays.copyOf(this.ys, grown);
                this.zs = Arrays.copyOf(this.zs, grown);
            }
            this.xs[this.size] = x;
            this.ys[this.size] = y;
            this.zs[this.size] = z;
            ++this.size;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Outcome {
        OPEN,
        BLOCK,
        ENTITY,
        BURST;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum Body {
        ARROW(0.05, 0.99, 0.6, true, 0.5),
        TRIDENT(0.05, 0.99, 0.99, true, 0.5),
        ORB(0.03, 0.99, 0.8, false, 0.25),
        FLASK(0.05, 0.99, 0.8, false, 0.25),
        BOTTLE(0.07, 0.99, 0.8, false, 0.25),
        ROCKET(0.0, 1.0, 1.0, true, 0.25);

        private final double gravity;
        private final double airKeep;
        private final double waterKeep;
        private final boolean movesFirst;
        private final double size;

        private Body(double gravity, double airKeep, double waterKeep, boolean movesFirst, double size) {
            this.gravity = gravity;
            this.airKeep = airKeep;
            this.waterKeep = waterKeep;
            this.movesFirst = movesFirst;
            this.size = size;
        }

        public double gravity() {
            return this.gravity;
        }
    }
}

