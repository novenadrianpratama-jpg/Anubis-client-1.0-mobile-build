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
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.Breaker;
import net.anubis.client.easyplace.builder.Scaffold;
import net.anubis.client.easyplace.builder.SchematicBuilderModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class Climber {
    static final int HEIGHT = 2;
    private static final int STEP_LIMIT = 40;
    private static final int TRIES = 3;
    private static final int DIG_DEPTH = 2;
    private static final double CENTERED = 0.12;
    private final Set<Long> scaffold;
    private final int x;
    private final int z;
    private final int baseY;
    private final int height;
    private int placed;
    private int phase;
    private int phaseTicks;
    private int tries;
    private final List<class_2338> toBreak = new ArrayList<class_2338>();
    private Breaker breaker;

    Climber(class_2338 column, Set<Long> scaffold) {
        this(column, scaffold, 2);
    }

    Climber(class_2338 column, Set<Long> scaffold, int height) {
        this.x = column.method_10263();
        this.z = column.method_10260();
        this.baseY = column.method_10264();
        this.scaffold = scaffold;
        this.height = height;
    }

    static class_2338 column(class_310 mc) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || !player.method_24828()) {
            return null;
        }
        int feetY = (int)Math.floor(player.method_23318() + 0.01);
        if (Math.abs(player.method_23318() - (double)feetY) > 0.01) {
            return null;
        }
        class_2338 center = class_2338.method_49637((double)player.method_23317(), (double)feetY, (double)player.method_23321());
        if (Climber.groundUnder(mc, center)) {
            return center;
        }
        double r = (double)player.method_17681() / 2.0 - 0.01;
        for (double[] corner : new double[][]{{-r, -r}, {r, -r}, {-r, r}, {r, r}}) {
            class_2338 column = class_2338.method_49637((double)(player.method_23317() + corner[0]), (double)feetY, (double)(player.method_23321() + corner[1]));
            if (!Climber.groundUnder(mc, column)) continue;
            return column;
        }
        return null;
    }

    static boolean hasRoom(class_310 mc, class_2338 column) {
        for (int dy = 0; dy <= 4; ++dy) {
            class_2338 cell = column.method_10086(dy);
            if (mc.field_1687.method_8320(cell).method_26220((class_1922)mc.field_1687, cell).method_1110()) continue;
            return false;
        }
        return true;
    }

    private static boolean groundUnder(class_310 mc, class_2338 column) {
        class_2338 below = column.method_10074();
        class_265 shape = mc.field_1687.method_8320(below).method_26220((class_1922)mc.field_1687, below);
        return !shape.method_1110() && shape.method_1105(class_2350.class_2351.field_11052) >= 0.99;
    }

    class_2338 column() {
        return new class_2338(this.x, this.baseY, this.z);
    }

    static int leavesCount(class_746 player) {
        return Scaffold.count(player);
    }

    Result tick(class_310 mc) {
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return Result.FAILED;
        }
        if (this.breaker != null || this.placed >= this.height) {
            return this.breakBelow(mc);
        }
        if (!Scaffold.has(player)) {
            return Result.FAILED;
        }
        if (!Scaffold.hold(mc, player)) {
            return Result.RUNNING;
        }
        ++this.phaseTicks;
        class_2338 leaf = new class_2338(this.x, this.baseY + this.placed, this.z);
        switch (this.phase) {
            case 0: {
                boolean centered;
                double dx = (double)this.x + 0.5 - player.method_23317();
                double dz = (double)this.z + 0.5 - player.method_23321();
                boolean bl = centered = Math.abs(dx) < 0.12 && Math.abs(dz) < 0.12;
                if (centered) {
                    mc.field_1690.field_1894.method_23481(false);
                    Aim.turn(mc, player.method_36454(), 90.0f, 22.0f);
                    class_243 motion = player.method_18798();
                    if (this.phaseTicks < 2 || !(player.method_36455() > 88.0f) || !(motion.field_1352 * motion.field_1352 + motion.field_1350 * motion.field_1350 < 4.0E-4)) break;
                    this.next(1);
                    break;
                }
                float off = Aim.turn(mc, (float)Math.toDegrees(Math.atan2(-dx, dz)), player.method_36455(), 22.0f);
                mc.field_1690.field_1894.method_23481(off < 30.0f);
                if (this.phaseTicks <= 40) break;
                mc.field_1690.field_1894.method_23481(false);
                return Result.FAILED;
            }
            case 1: {
                Aim.look(mc, player.method_36454(), 90.0f);
                if (player.method_24828()) {
                    mc.field_1690.field_1832.method_23481(Aim.clickable(mc, leaf.method_10074()));
                    mc.field_1690.field_1903.method_23481(true);
                    this.next(2);
                    break;
                }
                if (this.phaseTicks <= 40) break;
                return Result.FAILED;
            }
            case 2: {
                Aim.look(mc, player.method_36454(), 90.0f);
                if (!player.method_24828()) {
                    mc.field_1690.field_1903.method_23481(false);
                }
                if (player.method_23318() >= (double)leaf.method_10264() + 1.02) {
                    mc.field_1690.field_1903.method_23481(false);
                    class_3965 hit = new class_3965(new class_243((double)this.x + 0.5, (double)leaf.method_10264(), (double)this.z + 0.5), class_2350.field_11036, leaf.method_10074(), false);
                    if (mc.field_1761.method_2896(player, class_1268.field_5808, hit).method_23665()) {
                        player.method_6104(class_1268.field_5808);
                        this.scaffold.add(leaf.method_10063());
                    }
                    mc.field_1690.field_1832.method_23481(false);
                    this.next(3);
                    break;
                }
                if ((!player.method_24828() || this.phaseTicks <= 4) && this.phaseTicks <= 40) break;
                mc.field_1690.field_1903.method_23481(false);
                if (++this.tries > 3) {
                    return Result.FAILED;
                }
                this.next(0);
                break;
            }
            case 3: {
                boolean over;
                Aim.look(mc, player.method_36454(), 90.0f);
                boolean there = Scaffold.block(mc.field_1687.method_8320(leaf));
                boolean bl = over = Math.abs((double)this.x + 0.5 - player.method_23317()) < 0.5 && Math.abs((double)this.z + 0.5 - player.method_23321()) < 0.5;
                if (there && over && player.method_24828() && player.method_23318() >= (double)leaf.method_10264() + 0.99) {
                    ++this.placed;
                    SchematicBuilderModule.trace("climber base " + this.baseY + " placed " + this.placed + " at y " + player.method_23318());
                    this.tries = 0;
                    if (this.placed >= this.height) {
                        for (int y = this.baseY + this.height - 2; y >= this.baseY - 2; --y) {
                            class_2338 below = new class_2338(this.x, y, this.z);
                            if (this.scaffold.contains(below.method_10063()) && Scaffold.block(mc.field_1687.method_8320(below))) {
                                this.toBreak.add(below);
                                continue;
                            }
                            if (!mc.field_1687.method_8320(below).method_26215()) break;
                        }
                        return this.breakBelow(mc);
                    }
                    this.next(0);
                    break;
                }
                if (this.phaseTicks <= 40 && (!player.method_24828() || over || this.phaseTicks <= 4)) break;
                SchematicBuilderModule.trace("climber retry base " + this.baseY + " placed " + this.placed + " there " + there + " over " + over + " y " + player.method_23318());
                if (!there) {
                    this.scaffold.remove(leaf.method_10063());
                }
                if (!over) {
                    return Result.FAILED;
                }
                if (++this.tries > 3) {
                    return Result.FAILED;
                }
                this.next(1);
                break;
            }
            default: {
                return Result.FAILED;
            }
        }
        return Result.RUNNING;
    }

    void release(class_310 mc) {
        mc.field_1690.field_1832.method_23481(false);
        mc.field_1690.field_1903.method_23481(false);
        mc.field_1690.field_1894.method_23481(false);
    }

    private Result breakBelow(class_310 mc) {
        Breaker.Result result;
        if (this.breaker == null) {
            if (this.toBreak.isEmpty()) {
                return Result.DONE;
            }
            this.breaker = new Breaker(this.toBreak.removeFirst(), true);
        }
        if ((result = this.breaker.tick(mc)) == Breaker.Result.RUNNING) {
            return Result.RUNNING;
        }
        if (result == Breaker.Result.DONE) {
            this.scaffold.remove(this.breaker.pos().method_10063());
        }
        this.breaker = null;
        return this.toBreak.isEmpty() ? Result.DONE : Result.RUNNING;
    }

    private void next(int value) {
        this.phase = value;
        this.phaseTicks = 0;
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        RUNNING,
        DONE,
        FAILED;

    }
}

