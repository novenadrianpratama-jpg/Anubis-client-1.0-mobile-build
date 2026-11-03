/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.Random;
import net.anubis.client.easyplace.builder.Aim;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class Breaker {
    private static final int TIME_LIMIT = 100;
    private static final int LEAN_TICKS = 40;
    private static final float TURN = 22.0f;
    private static final Random HAND = new Random();
    private static final float WOBBLE_CHANCE = 0.3f;
    private static final double WOBBLE_DEGREES = 0.35;
    private final class_2338 pos;
    private final boolean mayLean;
    private final class_2350 side;
    private static final float VARIED_CHANCE = 0.2f;
    private final boolean varied = HAND.nextFloat() < 0.2f;
    private final double u = this.varied ? class_3532.method_15350((double)(0.5 + HAND.nextGaussian() * 0.12), (double)0.22, (double)0.78) : 0.5;
    private final double v = this.varied ? class_3532.method_15350((double)(0.5 + HAND.nextGaussian() * 0.12), (double)0.22, (double)0.78) : 0.5;
    private class_2350 face;
    private class_2350 leanWay;
    private boolean leaning;
    private float yaw;
    private float pitch;
    private int phase;
    private int ticks;
    private int react = -1;

    Breaker(class_2338 pos, boolean mayLean) {
        this(pos, mayLean, null);
    }

    Breaker(class_2338 pos, boolean mayLean, class_2350 side) {
        this.pos = pos;
        this.mayLean = mayLean;
        this.side = side;
    }

    private Aim.Shot shot(class_310 mc) {
        return this.side == null ? Aim.at(mc, this.pos) : Aim.mine(mc, this.pos, this.side, this.u, this.v);
    }

    class_2338 pos() {
        return this.pos;
    }

    Result tick(class_310 mc) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return this.finish(mc, Result.FAILED);
        }
        if (mc.field_1687.method_8320(this.pos).method_26215()) {
            if (this.phase >= 2) {
                mc.field_1761.method_2925();
            }
            return this.finish(mc, Result.DONE);
        }
        if (++this.ticks > 100) {
            mc.field_1761.method_2925();
            return this.finish(mc, Result.FAILED);
        }
        switch (this.phase) {
            case 0: {
                Aim.Shot shot = this.shot(mc);
                if (shot == null) {
                    if (!this.mayLean || this.ticks > 40) {
                        return this.finish(mc, Result.FAILED);
                    }
                    if (this.leanWay == null) {
                        this.leanWay = this.leanWay(mc, player);
                    }
                    if (this.leanWay == null) {
                        return this.finish(mc, Result.FAILED);
                    }
                    this.leaning = true;
                    mc.field_1690.field_1832.method_23481(true);
                    float off = Aim.turn(mc, this.leanWay.method_10144(), 70.0f, 22.0f);
                    mc.field_1690.field_1894.method_23481(off < 20.0f);
                    return Result.RUNNING;
                }
                if (this.leaning) {
                    mc.field_1690.field_1894.method_23481(false);
                }
                this.face = shot.hit().method_17780();
                this.yaw = shot.yaw();
                this.pitch = shot.pitch();
                this.phase = 1;
                break;
            }
            case 1: {
                if (this.react < 0) {
                    if (Aim.turn(mc, this.yaw, this.pitch, 22.0f) > 1.0f || Math.abs(player.method_36455() - this.pitch) > 1.0f) {
                        return Result.RUNNING;
                    }
                    Aim.Shot shot = this.shot(mc);
                    if (shot == null) {
                        this.phase = 0;
                        return Result.RUNNING;
                    }
                    this.face = shot.hit().method_17780();
                    Aim.look(mc, shot.yaw(), shot.pitch());
                    this.yaw = player.method_36454();
                    this.pitch = player.method_36455();
                    this.react = this.varied ? (int)class_3532.method_53062((long)Math.round(Math.exp(Math.log(2.6) + HAND.nextGaussian() * 0.35)), (long)2L, (long)6L) : 2;
                    return Result.RUNNING;
                }
                if (--this.react > 0) {
                    return Result.RUNNING;
                }
                class_3965 hit = Aim.trace(mc, player, player.method_36454(), player.method_36455(), player.method_55754());
                if (hit == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)this.pos) || hit.method_17780() != this.face) {
                    this.react = -1;
                    return Result.RUNNING;
                }
                mc.field_1761.method_2910(this.pos, this.face);
                player.method_6104(class_1268.field_5808);
                this.phase = 2;
                break;
            }
            default: {
                if (this.side != null && this.varied && HAND.nextFloat() < 0.3f) {
                    this.wobble(mc, player);
                }
                Aim.look(mc, this.yaw, this.pitch);
                mc.field_1761.method_2902(this.pos, this.face);
                player.method_6104(class_1268.field_5808);
            }
        }
        return Result.RUNNING;
    }

    private void wobble(class_310 mc, class_746 player) {
        float nextPitch;
        float nextYaw = this.yaw + (float)(HAND.nextGaussian() * 0.35);
        class_3965 hit = Aim.trace(mc, player, nextYaw, nextPitch = this.pitch + (float)(HAND.nextGaussian() * 0.35 * 0.7), player.method_55754());
        if (hit == null || hit.method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)this.pos) || hit.method_17780() != this.face) {
            return;
        }
        Aim.look(mc, nextYaw, nextPitch);
        this.yaw = player.method_36454();
        this.pitch = player.method_36455();
    }

    private class_2350 leanWay(class_310 mc, class_746 player) {
        class_2338 feet = player.method_24515();
        class_2350 best = null;
        for (class_2350 way : class_2350.class_2353.field_11062) {
            class_2338 beside;
            if (!mc.field_1687.method_8320(this.pos.method_10093(way)).method_45474() || !mc.field_1687.method_8320(beside = feet.method_10093(way)).method_26220((class_1922)mc.field_1687, beside).method_1110() || !mc.field_1687.method_8320(beside.method_10084()).method_26220((class_1922)mc.field_1687, beside.method_10084()).method_1110() || best != null && !(Math.abs(class_3532.method_15393((float)(way.method_10144() - player.method_36454()))) < Math.abs(class_3532.method_15393((float)(best.method_10144() - player.method_36454()))))) continue;
            best = way;
        }
        return best;
    }

    void release(class_310 mc) {
        this.finish(mc, Result.FAILED);
    }

    private Result finish(class_310 mc, Result result) {
        if (this.leaning) {
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1832.method_23481(false);
            this.leaning = false;
        }
        return result;
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        RUNNING,
        DONE,
        FAILED;

    }
}

