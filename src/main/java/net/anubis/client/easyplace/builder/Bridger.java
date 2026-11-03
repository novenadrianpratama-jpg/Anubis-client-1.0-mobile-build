/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2350$class_2352
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.Set;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.Hands;
import net.anubis.client.easyplace.builder.Scaffold;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class Bridger {
    private static final int EDGE_TICKS = 40;
    private static final int HOLD_TICKS = 20;
    private static final int ANSWER_TICKS = 12;
    private static final int TURN_TICKS = 30;
    private static final int STEP_TICKS = 40;
    private static final int TRIES = 2;
    private static final double LEANING = 0.18;
    private final class_2338 from;
    private final class_2338 spot;
    private final class_2338 leaf;
    private final class_2350 way;
    private final Set<Long> scaffold;
    private final class_1792 material;
    private boolean chained;
    private int phase;
    private int phaseTicks;
    private int stillTicks;
    private int onAim;
    private int clickedAt = -1;
    private int tries;
    private class_243 lastPos;

    Bridger(class_2338 from, class_2338 spot, Set<Long> scaffold, class_1792 material) {
        this.from = from;
        this.spot = spot;
        this.leaf = spot.method_10074();
        this.way = spot.method_10263() > from.method_10263() ? class_2350.field_11034 : (spot.method_10263() < from.method_10263() ? class_2350.field_11039 : (spot.method_10260() > from.method_10260() ? class_2350.field_11035 : class_2350.field_11043));
        this.scaffold = scaffold;
        this.material = material;
    }

    class_2338 spot() {
        return this.spot;
    }

    Bridger chained() {
        this.chained = true;
        return this;
    }

    Result tick(class_310 mc) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return Result.FAILED;
        }
        mc.field_1690.field_1832.method_23481(true);
        ++this.phaseTicks;
        boolean leafThere = this.material == null ? Scaffold.block(mc.field_1687.method_8320(this.leaf)) : !mc.field_1687.method_8320(this.leaf).method_45474();
        switch (this.phase) {
            case 0: {
                boolean held;
                if (leafThere) {
                    this.next(3);
                    return Result.RUNNING;
                }
                if (this.material == null ? !Scaffold.has(player) : !Hands.has(player, stack -> stack.method_31574(this.material))) {
                    return Result.FAILED;
                }
                boolean bl = held = this.material == null ? Scaffold.hold(mc, player) : Hands.hold(mc, player, stack -> stack.method_31574(this.material));
                if (!held) {
                    return this.phaseTicks > 20 ? Result.FAILED : Result.RUNNING;
                }
                this.lastPos = player.method_73189();
                this.next(1);
                break;
            }
            case 1: {
                double out = this.past(player.method_73189());
                class_243 pos = player.method_73189();
                this.stillTicks = pos.method_1025(this.lastPos) < 1.0E-4 ? this.stillTicks + 1 : 0;
                this.lastPos = pos;
                if (out > 0.18 || out > 0.03 && this.stillTicks >= 3) {
                    mc.field_1690.field_1894.method_23481(false);
                    this.next(2);
                    return Result.RUNNING;
                }
                if (this.phaseTicks > 40) {
                    return Result.FAILED;
                }
                float off = Aim.turn(mc, this.way.method_10144(), 30.0f, 22.0f);
                mc.field_1690.field_1894.method_23481(off < 20.0f);
                break;
            }
            case 2: {
                if (leafThere) {
                    if (this.material == null) {
                        this.scaffold.add(this.leaf.method_10063());
                    }
                    this.next(3);
                    return Result.RUNNING;
                }
                if (!player.method_5715()) {
                    if (this.phaseTicks > 10) {
                        return this.retry(mc);
                    }
                    return Result.RUNNING;
                }
                if (this.clickedAt < 0) {
                    Aim.Shot shot = Aim.side(mc, this.from.method_10074(), this.way);
                    if (shot == null) {
                        return this.retry(mc);
                    }
                    Aim.turn(mc, shot.yaw(), shot.pitch(), 22.0f);
                    if (!Aim.looking(player, shot.yaw(), shot.pitch())) {
                        this.onAim = 0;
                        if (this.phaseTicks > 30) {
                            return this.retry(mc);
                        }
                        return Result.RUNNING;
                    }
                    if (++this.onAim < 2) {
                        return Result.RUNNING;
                    }
                    class_1799 hand = player.method_6047();
                    if (this.material == null ? !Scaffold.item(hand) : !hand.method_31574(this.material)) {
                        if (++this.tries >= 2) {
                            return Result.FAILED;
                        }
                        this.next(0);
                        return Result.RUNNING;
                    }
                    if (mc.field_1761.method_2896(player, class_1268.field_5808, shot.hit()).method_23665()) {
                        player.method_6104(class_1268.field_5808);
                    }
                    this.clickedAt = this.phaseTicks;
                    break;
                }
                if (this.phaseTicks - this.clickedAt <= 12) break;
                return this.retry(mc);
            }
            case 3: {
                boolean there;
                class_243 pos = player.method_73189();
                double dx = (double)this.spot.method_10263() + 0.5 - pos.field_1352;
                double dz = (double)this.spot.method_10260() + 0.5 - pos.field_1350;
                boolean bl = there = player.method_24828() && player.method_24515().equals((Object)this.spot);
                if (there && (this.chained || Math.abs(dx) < 0.25 && Math.abs(dz) < 0.25)) {
                    this.release(mc);
                    return Result.DONE;
                }
                if (this.phaseTicks > 40) {
                    return Result.FAILED;
                }
                float off = Aim.turn(mc, (float)Math.toDegrees(Math.atan2(-dx, dz)), 30.0f, 22.0f);
                mc.field_1690.field_1894.method_23481(off < 30.0f);
                break;
            }
            default: {
                return Result.FAILED;
            }
        }
        return Result.RUNNING;
    }

    private double past(class_243 pos) {
        double edge = this.way.method_10171() == class_2350.class_2352.field_11056 ? 1.0 : 0.0;
        return switch (this.way.method_10166()) {
            case class_2350.class_2351.field_11048 -> (pos.field_1352 - ((double)this.from.method_10263() + edge)) * (double)this.way.method_10148();
            case class_2350.class_2351.field_11051 -> (pos.field_1350 - ((double)this.from.method_10260() + edge)) * (double)this.way.method_10165();
            default -> 0.0;
        };
    }

    private Result retry(class_310 mc) {
        if (++this.tries >= 2) {
            return Result.FAILED;
        }
        this.lastPos = mc.field_1724 == null ? this.lastPos : mc.field_1724.method_73189();
        this.next(1);
        return Result.RUNNING;
    }

    private void next(int value) {
        this.phase = value;
        this.phaseTicks = 0;
        this.stillTicks = 0;
        this.onAim = 0;
        this.clickedAt = -1;
    }

    void release(class_310 mc) {
        mc.field_1690.field_1894.method_23481(false);
        mc.field_1690.field_1832.method_23481(false);
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        RUNNING,
        DONE,
        FAILED;

    }
}

