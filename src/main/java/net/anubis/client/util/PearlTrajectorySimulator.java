/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1301
 *  net.minecraft.class_1676
 *  net.minecraft.class_1776
 *  net.minecraft.class_1802
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_3486
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_746
 */
package net.anubis.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1301;
import net.minecraft.class_1676;
import net.minecraft.class_1776;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3486;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class PearlTrajectorySimulator {
    private static final double SPAWN_EYE_OFFSET = 0.1;
    private static final double GRAVITY = 0.03;
    private static final float AIR_DRAG = 0.99f;
    private static final float WATER_DRAG = 0.8f;
    private static final int MAX_TICKS = 300;
    private static final double ENTITY_MARGIN = 0.3;

    private PearlTrajectorySimulator() {
    }

    public static SimulationResult simulate(class_1937 level, class_746 player, float partialTick) {
        class_243 eye = player.method_5836(partialTick);
        class_243 pos = new class_243(eye.field_1352, eye.field_1351 - 0.1, eye.field_1350);
        class_243 velocity = player.method_5828(partialTick).method_1021((double)class_1776.field_55033);
        class_243 motion = player.method_60478();
        velocity = velocity.method_1031(motion.field_1352, player.method_24828() ? 0.0 : motion.field_1351, motion.field_1350);
        return PearlTrajectorySimulator.simulate(level, pos, velocity, (class_1297)player, null);
    }

    public static SimulationResult simulate(class_1937 level, class_1676 pearl) {
        return PearlTrajectorySimulator.simulate(level, pearl.method_73189(), pearl.method_18798(), (class_1297)pearl, pearl.method_24921());
    }

    public static SimulationResult simulate(class_1937 level, class_243 start, class_243 velocity, class_1297 ignore, class_1297 alsoIgnore) {
        ArrayList<class_243> points = new ArrayList<class_243>(64);
        points.add(start);
        class_243 pos = start;
        class_3965 blockHit = null;
        class_1297 entityHit = null;
        int ticks = 0;
        while (ticks < 300) {
            class_3965 hit;
            ++ticks;
            velocity = velocity.method_1031(0.0, -0.03, 0.0);
            class_243 next = pos.method_1019(velocity = velocity.method_1021(PearlTrajectorySimulator.isInWater(level, pos) ? (double)0.8f : (double)0.99f));
            class_3965 blockResult = level.method_17742(new class_3959(pos, next, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, ignore));
            class_243 end = blockResult.method_17783() != class_239.class_240.field_1333 ? blockResult.method_17784() : next;
            class_1297 entity = PearlTrajectorySimulator.firstEntityOnSegment(level, pos, end, ignore, alsoIgnore);
            if (entity != null) {
                class_243 at = entity.method_5829().method_1014(0.3).method_992(pos, end).orElse(end);
                points.add(at);
                entityHit = entity;
                break;
            }
            if (blockResult instanceof class_3965 && (hit = blockResult).method_17783() != class_239.class_240.field_1333) {
                points.add(end);
                blockHit = hit;
                break;
            }
            pos = next;
            points.add(pos);
            if (!(pos.field_1351 < (double)(level.method_31607() - 64))) continue;
            break;
        }
        return new SimulationResult(List.copyOf(points), (class_243)points.getLast(), blockHit, entityHit, ticks);
    }

    private static class_1297 firstEntityOnSegment(class_1937 level, class_243 from, class_243 to, class_1297 ignore, class_1297 alsoIgnore) {
        class_238 sweep = new class_238(from, to).method_1014(1.0);
        class_1297 closest = null;
        double closestSq = Double.MAX_VALUE;
        for (class_1297 entity : level.method_8333(ignore, sweep, PearlTrajectorySimulator::canHit)) {
            double distanceSq;
            Optional hit;
            if (entity == alsoIgnore || (hit = entity.method_5829().method_1014(0.3).method_992(from, to)).isEmpty() || !((distanceSq = from.method_1025((class_243)hit.get())) < closestSq)) continue;
            closestSq = distanceSq;
            closest = entity;
        }
        return closest;
    }

    private static boolean canHit(class_1297 entity) {
        return class_1301.field_6155.test(entity) && entity.method_5863() && !(entity instanceof class_746);
    }

    private static boolean isInWater(class_1937 level, class_243 pos) {
        return level.method_8316(class_2338.method_49638((class_2374)pos)).method_15767(class_3486.field_15517);
    }

    public static boolean isHoldingPearl(class_746 player) {
        return player.method_6047().method_31574(class_1802.field_8634) || player.method_6079().method_31574(class_1802.field_8634);
    }

    @Environment(value=EnvType.CLIENT)
    public record SimulationResult(List<class_243> points, class_243 landing, class_3965 blockHit, class_1297 entityHit, int ticks) {
        public boolean landed() {
            return this.blockHit != null || this.entityHit != null;
        }
    }
}

