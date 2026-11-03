/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.function.Predicate;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.Walker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class Hop {
    private static final int TIME_LIMIT = 60;
    private static final double CENTERED = 0.15;
    private static final double STEP = 0.55;
    private static final double LINED_UP = 0.25;
    private static final double LEAP_AT = 0.3;
    private static final double LANDED = 0.55;
    private static final int SAFE_DROP = 3;
    private static final float TURN = 22.0f;
    private static final float WALK_OFF = 30.0f;
    private static final double JUMP_AT = 0.15;
    private final class_2338 spot;
    private final boolean through;
    private final boolean leap;
    private boolean sprint;
    private boolean careful;
    private boolean slow;
    private class_2338 from;
    private double floor = Double.NaN;
    private boolean centered;
    private int ticks;

    Hop(class_2338 spot) {
        this(spot, false, false);
    }

    Hop(class_2338 spot, boolean through) {
        this(spot, through, false);
    }

    Hop(class_2338 spot, boolean through, boolean leap) {
        this.spot = spot;
        this.through = through;
        this.leap = leap;
    }

    class_2338 spot() {
        return this.spot;
    }

    Hop sprinting() {
        this.sprint = true;
        return this;
    }

    Hop careful() {
        this.careful = true;
        this.sprint = false;
        return this;
    }

    Hop slow() {
        this.slow = true;
        this.sprint = false;
        return this;
    }

    static class_2338 spot(class_638 level, class_746 player, Predicate<class_2338> avoid) {
        if (!player.method_24828()) {
            return null;
        }
        class_2338 feet = player.method_24515();
        double from = Walker.floorOr(level, feet);
        class_2338 best = null;
        double bestRise = 0.45;
        for (class_2338 up : Walker.steps(level, feet)) {
            double rise = Walker.floor(level, up) - from;
            if (!(rise > bestRise) || avoid.test(up)) continue;
            bestRise = rise;
            best = up;
        }
        return best;
    }

    /*
     * Unable to fully structure code
     */
    Result tick(class_310 mc) {
        block12: {
            player = mc.field_1724;
            if (player == null) {
                return Result.FAILED;
            }
            if (this.from == null) {
                this.from = player.method_24515();
            }
            if (Double.isNaN(this.floor) && mc.field_1687 != null) {
                this.floor = Walker.floorOr(mc.field_1687, this.spot);
            }
            pos = player.method_73189();
            mc.field_1690.field_1832.method_23481(this.careful);
            dx = (double)this.spot.method_10263() + 0.5 - pos.field_1352;
            dz = (double)this.spot.method_10260() + 0.5 - pos.field_1350;
            near = this.through != false ? 0.35 : 0.12;
            over = Math.abs(dx) < near && Math.abs(dz) < near;
            settled = this.through != false || player.method_18798().method_37268() < 0.0025;
            v0 = landed = pos.field_1351 < this.floor + 0.55;
            if (over && settled && landed && player.method_24828() && pos.field_1351 >= this.floor - 0.05) {
                if (!this.through) {
                    this.release(mc);
                }
                return Result.DONE;
            }
            if (++this.ticks > 60) {
                this.release(mc);
                return Result.FAILED;
            }
            if (mc.field_1687 != null && player.method_24828() && this.intoHole(mc.field_1687, player, pos)) {
                mc.field_1690.field_1894.method_23481(false);
                mc.field_1690.field_1867.method_23481(false);
                mc.field_1690.field_1903.method_23481(false);
                mc.field_1690.field_1832.method_23481(true);
                return Result.RUNNING;
            }
            if (!this.centered) {
                cx = (double)this.from.method_10263() + 0.5 - pos.field_1352;
                cz = (double)this.from.method_10260() + 0.5 - pos.field_1350;
                aside = this.spot.method_10263() != this.from.method_10263() ? Math.abs(cz) : Math.abs(cx);
                v1 = level = this.floor - pos.field_1351 <= 0.55 && aside < 0.25;
                if (level != false || Math.abs(cx) < 0.15 && Math.abs(cz) < 0.15 || !player.method_24828() && this.ticks > 10) {
                    this.centered = true;
                } else {
                    off = Aim.turn(mc, (float)Math.toDegrees(Math.atan2(-cx, cz)), player.method_36455(), 22.0f);
                    mc.field_1690.field_1894.method_23481(off < 30.0f);
                    mc.field_1690.field_1903.method_23481(false);
                    return Result.RUNNING;
                }
            }
            off = Aim.turn(mc, (float)Math.toDegrees(Math.atan2(-dx, dz)), player.method_36455(), 22.0f);
            moving = player.method_18798().method_37268() > 4.0E-4;
            mc.field_1690.field_1867.method_23481(this.sprint != false && over == false && off < 10.0f);
            v2 = push = !(over != false && landed != false || this.through == false && !(Math.hypot(dx, dz) > 0.3) && moving != false && landed != false);
            if (!this.slow) break block12;
            v3 = down = player.method_24828() != false && pos.field_1351 < this.floor + 0.05 && Math.hypot(dx, dz) < 0.45;
            if (!push) {
                v6 = false;
            } else {
                v4 = player.method_18798().method_37267();
                v5 = down != false ? 0.03 : 0.08;
                v6 = v4 < v5;
            }
            push = v6;
        }
        mc.field_1690.field_1894.method_23481(push != false && off < 30.0f);
        ahead = (pos.field_1352 - ((double)this.from.method_10263() + 0.5)) * (double)Integer.signum(this.spot.method_10263() - this.from.method_10263()) + (pos.field_1350 - ((double)this.from.method_10260() + 0.5)) * (double)Integer.signum(this.spot.method_10260() - this.from.method_10260());
        atStep = player.field_5976 != false || ahead > 0.15 || this.spot.method_10263() == this.from.method_10263() && this.spot.method_10260() == this.from.method_10260();
        mc.field_1690.field_1903.method_23481(player.method_24828() != false && this.floor - pos.field_1351 > 0.55 && atStep != false || this.leap != false && player.method_24828() != false && ahead > 0.3 && ahead < 1.0);
        return Result.RUNNING;
    }

    private boolean intoHole(class_638 level, class_746 player, class_243 pos) {
        class_243 motion = player.method_18798();
        double speed = motion.method_37267();
        if (speed < 0.02) {
            return false;
        }
        double ahead = speed * 1.3 + 0.35;
        class_2338 cell = class_2338.method_49637((double)(pos.field_1352 + motion.field_1352 / speed * ahead), (double)(pos.field_1351 + 0.01), (double)(pos.field_1350 + motion.field_1350 / speed * ahead));
        class_2338 feet = player.method_24515();
        if (cell.method_10263() == this.spot.method_10263() && cell.method_10260() == this.spot.method_10260() || cell.method_10263() == feet.method_10263() && cell.method_10260() == feet.method_10260()) {
            return false;
        }
        return Walker.dropAt(level, cell) > 3;
    }

    void release(class_310 mc) {
        mc.field_1690.field_1894.method_23481(false);
        mc.field_1690.field_1903.method_23481(false);
        mc.field_1690.field_1867.method_23481(false);
        if (this.careful) {
            mc.field_1690.field_1832.method_23481(false);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        RUNNING,
        DONE,
        FAILED;

    }
}

