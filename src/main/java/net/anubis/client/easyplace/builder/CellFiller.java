/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_3486
 *  net.minecraft.class_3610
 *  net.minecraft.class_3611
 *  net.minecraft.class_3612
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.Hands;
import net.anubis.client.easyplace.builder.Scaffold;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_3612;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class CellFiller {
    private static final int ANSWER_TICKS = 12;
    private static final int HOLD_TICKS = 20;
    private static final int TURN_TICKS = 30;
    private static final int TRIES = 2;
    private final class_2338 cell;
    private final Kind kind;
    private Aim.Shot shot;
    private int phase;
    private int phaseTicks;
    private int onAim;
    private int tries;

    CellFiller(class_2338 cell, Kind kind) {
        this.cell = cell;
        this.kind = kind;
    }

    class_2338 cell() {
        return this.cell;
    }

    Kind kind() {
        return this.kind;
    }

    static boolean filled(class_310 mc, class_2338 cell, Kind kind) {
        class_2680 state = mc.field_1687.method_8320(cell);
        return switch (kind.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Scaffold.block(state);
            case 1 -> {
                if (state.method_26227().method_15771() && state.method_26227().method_15767(class_3486.field_15517)) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (state.method_28498((class_2769)class_2741.field_12508) && ((Boolean)state.method_11654((class_2769)class_2741.field_12508)).booleanValue()) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if (state.method_26227().method_15771() && state.method_26227().method_15767(class_3486.field_15518)) {
                    yield true;
                }
                yield false;
            }
            case 4 -> false;
        };
    }

    static boolean reachable(class_310 mc, class_2338 cell, Kind kind) {
        return CellFiller.aim(mc, cell, kind) != null;
    }

    Result tick(class_310 mc) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return Result.FAILED;
        }
        if (CellFiller.filled(mc, this.cell, this.kind)) {
            return Result.DONE;
        }
        ++this.phaseTicks;
        switch (this.phase) {
            case 0: {
                if (!(this.kind != Kind.SUPPORT_LEAF ? Hands.hold(mc, player, stack -> stack.method_31574(this.kind.item)) : Scaffold.hold(mc, player))) {
                    if (this.phaseTicks > 20) {
                        return Result.FAILED;
                    }
                    return Result.RUNNING;
                }
                this.shot = CellFiller.aim(mc, this.cell, this.kind);
                if (this.shot == null) {
                    return Result.FAILED;
                }
                this.next(1);
                break;
            }
            case 1: {
                boolean used;
                Aim.turn(mc, this.shot.yaw(), this.shot.pitch(), 22.0f);
                if (!Aim.looking(player, this.shot.yaw(), this.shot.pitch())) {
                    this.onAim = 0;
                    if (this.phaseTicks > 30) {
                        return Result.FAILED;
                    }
                    return Result.RUNNING;
                }
                if (++this.onAim < 2) {
                    return Result.RUNNING;
                }
                boolean bl = used = this.kind == Kind.SUPPORT_LEAF ? mc.field_1761.method_2896(player, class_1268.field_5808, this.shot.hit()).method_23665() : mc.field_1761.method_2919((class_1657)player, class_1268.field_5808).method_23665();
                if (used) {
                    player.method_6104(class_1268.field_5808);
                }
                this.next(2);
                break;
            }
            case 2: {
                if (this.kind == Kind.REFILL && player.method_6047().method_31574(class_1802.field_8705)) {
                    return Result.DONE;
                }
                if (this.phaseTicks <= 12) break;
                if (++this.tries >= 2) {
                    return Result.FAILED;
                }
                this.next(0);
                break;
            }
            default: {
                return Result.FAILED;
            }
        }
        return Result.RUNNING;
    }

    private void next(int value) {
        this.phase = value;
        this.phaseTicks = 0;
        this.onAim = 0;
    }

    private static Aim.Shot aim(class_310 mc, class_2338 cell, Kind kind) {
        return switch (kind.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Aim.against(mc, cell, null);
            case 1 -> Aim.against(mc, cell, (class_3611)class_3612.field_15910);
            case 3 -> Aim.against(mc, cell, (class_3611)class_3612.field_15908);
            case 2 -> Aim.at(mc, cell);
            case 4 -> Aim.water(mc, cell);
        };
    }

    static boolean endless(class_638 level, class_2338 pos) {
        if (!level.method_8320(pos).method_27852(class_2246.field_10382) || !level.method_8316(pos).method_15771()) {
            return false;
        }
        int sources = 0;
        for (class_2350 direction : class_2350.class_2353.field_11062) {
            class_3610 next = level.method_8316(pos.method_10093(direction));
            if (!next.method_15771() || !next.method_15767(class_3486.field_15517)) continue;
            ++sources;
        }
        if (sources < 2) {
            return false;
        }
        class_2680 below = level.method_8320(pos.method_10074());
        return below.method_51367() || below.method_26227().method_15771() && below.method_26227().method_15767(class_3486.field_15517);
    }

    @Environment(value=EnvType.CLIENT)
    static enum Kind {
        SUPPORT_LEAF(class_1802.field_17503),
        WATER_SOURCE(class_1802.field_8705),
        WATERLOG(class_1802.field_8705),
        LAVA_SOURCE(class_1802.field_8187),
        REFILL(class_1802.field_8550);

        final class_1792 item;

        private Kind(class_1792 item) {
            this.item = item;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        RUNNING,
        DONE,
        FAILED;

    }
}

