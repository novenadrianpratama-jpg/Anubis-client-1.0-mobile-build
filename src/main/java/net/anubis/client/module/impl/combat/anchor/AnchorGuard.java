/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1269$class_9860
 *  net.minecraft.class_1269$class_9861
 *  net.minecraft.class_1297
 *  net.minecraft.class_1661
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1922
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2343
 *  net.minecraft.class_2346
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2350$class_2352
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2682
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat.anchor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1661;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2682;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class AnchorGuard {
    public static final float BLAST_POWER = 5.0f;
    private static final double MIN_SHADE = 0.15;
    private static final double FACE_INSET = 0.3;
    private static final double AIM_INSET = 0.1;

    private AnchorGuard() {
    }

    public static Spot find(class_638 level, class_746 player, class_2338 anchor, class_243 sightline) {
        class_243 blast = class_243.method_24953((class_2382)anchor);
        class_238 body = player.method_5829();
        List<class_243> points = AnchorGuard.bodyPoints(body);
        ArrayList<class_243> open = new ArrayList<class_243>(points.size());
        for (class_243 point : points) {
            class_3965 hit = level.method_17742(new class_3959(point, blast, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player));
            if (hit.method_17783() != class_239.class_240.field_1333 && !hit.method_17777().equals((Object)anchor)) continue;
            open.add(point);
        }
        class_2338 feet = player.method_24515();
        if ((double)open.size() < Math.ceil((double)points.size() * 0.15)) {
            return new Spot(feet, null, null, null);
        }
        int needed = (int)Math.ceil((double)open.size() * 0.15);
        class_243 eye = player.method_33571();
        LinkedHashSet<class_2338> around = new LinkedHashSet<class_2338>();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) continue;
                around.add(feet.method_10069(dx, 0, dz));
            }
        }
        for (class_2350 side : class_2350.class_2353.field_11062) {
            around.add(anchor.method_10093(side));
        }
        ArrayList<Cell> cells = new ArrayList<Cell>(around.size());
        for (class_2338 pos : around) {
            int shade;
            class_238 box = new class_238(pos);
            if (pos.equals((Object)anchor) || box.method_994(body) || box.method_992(eye, sightline).isPresent() || (shade = AnchorGuard.shade(box, blast, open)) < needed) continue;
            cells.add(new Cell(pos, shade, box.method_1005().method_1025(blast)));
        }
        cells.sort(Comparator.comparingInt(Cell::shade).reversed().thenComparingDouble(Cell::distance));
        for (Cell cell : cells) {
            Spot spot;
            class_2680 state = level.method_8320(cell.pos());
            if (!state.method_45474() || (spot = state.method_26218((class_1922)level, cell.pos()).method_1110() ? AnchorGuard.supportFor(level, player, eye, cell.pos()) : AnchorGuard.direct(level, player, eye, cell.pos(), state)) == null) continue;
            return spot;
        }
        return null;
    }

    public static class_3965 hitAlong(class_638 level, class_746 player, Rotation look, Spot spot) {
        class_3965 hit = AnchorGuard.trace(level, player, look);
        if (hit == null || !hit.method_17777().equals((Object)spot.support())) {
            return null;
        }
        if (spot.face() != null && hit.method_17780() != spot.face()) {
            return null;
        }
        class_2338 placed = Crystals.placedObsidian(level, player, hit, level.method_8320(spot.support()));
        return spot.pos().equals((Object)placed) ? hit : null;
    }

    public static class_3965 anchorHit(class_638 level, class_746 player, class_2338 anchor) {
        Rotation sent = ServerRotation.sent();
        class_3965 hit = sent == null ? null : AnchorGuard.trace(level, player, sent);
        return hit != null && hit.method_17777().equals((Object)anchor) ? hit : null;
    }

    public static boolean isGuardBlock(class_1799 stack) {
        class_1792 class_17922 = stack.method_7909();
        if (!(class_17922 instanceof class_1747)) {
            return false;
        }
        class_1747 item = (class_1747)class_17922;
        class_2248 block = item.method_7711();
        if (block instanceof class_2343 || block instanceof class_2346 || block == class_2246.field_23152 || block == class_2246.field_10375) {
            return false;
        }
        return block.method_9564().method_26234((class_1922)class_2682.field_12294, class_2338.field_10980);
    }

    public static int detonatorSlot(class_1661 inventory) {
        int totem = Crystals.hotbarSlotOf(inventory, class_1802.field_8288);
        if (totem != -1) {
            return totem;
        }
        for (int slot = 0; slot < class_1661.method_7368(); ++slot) {
            if (!Crystals.isCombatItem(inventory.method_5438(slot))) continue;
            return slot;
        }
        return -1;
    }

    public static int detonatorRoute(class_1661 inventory, class_1799 main) {
        if (Crystals.isCombatItem(main)) {
            return -2;
        }
        int slot = AnchorGuard.detonatorSlot(inventory);
        return slot == -1 ? -1 : slot;
    }

    public static class_243 anchorAim(class_638 level, class_746 player, class_2338 anchor) {
        class_243 point;
        class_3965 hit;
        class_243 eye = player.method_33571();
        class_238 box = new class_238(anchor).method_1011(0.1);
        Rotation sent = ServerRotation.sent();
        if (sent != null) {
            class_243 direction = class_243.method_1030((float)sent.pitch(), (float)sent.yaw());
            double along = box.method_1005().method_1020(eye).method_1026(direction);
            if (along > 0.0) {
                class_243 on = eye.method_1019(direction.method_1021(along));
                class_243 toward = new class_243(class_3532.method_15350((double)on.field_1352, (double)box.field_1323, (double)box.field_1320), class_3532.method_15350((double)on.field_1351, (double)box.field_1322, (double)box.field_1325), class_3532.method_15350((double)on.field_1350, (double)box.field_1321, (double)box.field_1324));
                class_3965 hit2 = AnchorGuard.trace(level, player, ServerRotation.next(player, RotationUtils.fromTo(eye, toward)));
                if (hit2 != null && hit2.method_17777().equals((Object)anchor)) {
                    return toward;
                }
            }
        }
        return (hit = AnchorGuard.trace(level, player, ServerRotation.next(player, RotationUtils.fromTo(eye, point = new class_243(class_3532.method_15350((double)eye.field_1352, (double)box.field_1323, (double)box.field_1320), class_3532.method_15350((double)eye.field_1351, (double)box.field_1322, (double)box.field_1325), class_3532.method_15350((double)eye.field_1350, (double)box.field_1321, (double)box.field_1324)))))) != null && hit.method_17777().equals((Object)anchor) ? point : null;
    }

    public static void click(class_310 mc, class_746 player, class_3965 hit) {
        class_1269.class_9860 success;
        class_1799 held = player.method_6047();
        int countBefore = held.method_7947();
        class_1269 result = mc.field_1761.method_2896(player, class_1268.field_5808, hit);
        if (result instanceof class_1269.class_9860 && (success = (class_1269.class_9860)result).comp_2909() == class_1269.class_9861.field_52427) {
            player.method_6104(class_1268.field_5808);
            if (!held.method_7960() && (held.method_7947() != countBefore || player.method_56992())) {
                mc.field_1773.field_4012.method_3215(class_1268.field_5808);
            }
        }
        MeleeAttacks.claimTick(mc);
    }

    private static Spot direct(class_638 level, class_746 player, class_243 eye, class_2338 pos, class_2680 state) {
        Spot spot = new Spot(pos, pos, null, state.method_26218((class_1922)level, pos).method_1107().method_996(pos).method_1005());
        Rotation look = ServerRotation.next(player, RotationUtils.fromTo(eye, spot.aim()));
        return AnchorGuard.hitAlong(level, player, look, spot) != null ? spot : null;
    }

    public static Spot supportFor(class_638 level, class_746 player, class_243 eye, class_2338 cell) {
        Spot best = null;
        double bestDistance = Double.MAX_VALUE;
        for (class_2350 side : class_2350.values()) {
            double distance;
            class_265 shape;
            class_2338 support = cell.method_10093(side);
            class_2680 state = level.method_8320(support);
            if (state.method_45474() || (shape = state.method_26218((class_1922)level, support)).method_1110()) continue;
            class_2350 face = side.method_10153();
            Spot spot = null;
            for (class_243 aim : AnchorGuard.faceAims(shape.method_1107().method_996(support), face)) {
                Spot candidate = new Spot(cell, support, face, aim);
                Rotation look = ServerRotation.next(player, RotationUtils.fromTo(eye, aim));
                if (AnchorGuard.hitAlong(level, player, look, candidate) == null) continue;
                spot = candidate;
                break;
            }
            if (spot == null) continue;
            double d = distance = side == class_2350.field_11033 ? -1.0 : eye.method_1025(spot.aim());
            if (!(distance < bestDistance)) continue;
            best = spot;
            bestDistance = distance;
        }
        return best;
    }

    private static List<class_243> faceAims(class_238 box, class_2350 face) {
        class_2350.class_2351 axis = face.method_10166();
        double plane = face.method_10171() == class_2350.class_2352.field_11056 ? box.method_990(axis) : box.method_1001(axis);
        class_243 center = box.method_1005().method_38499(axis, plane);
        ArrayList<class_243> aims = new ArrayList<class_243>(5);
        aims.add(center);
        for (class_2350 toward : class_2350.values()) {
            class_2350.class_2351 along = toward.method_10166();
            if (along == axis) continue;
            aims.add(center.method_43206(toward, (box.method_990(along) - box.method_1001(along)) * 0.3));
        }
        return aims;
    }

    public static class_3965 trace(class_638 level, class_746 player, Rotation look) {
        class_243 reach;
        class_243 eye = player.method_33571();
        class_3965 hit = level.method_17742(new class_3959(eye, eye.method_1019(reach = class_243.method_1030((float)look.pitch(), (float)look.yaw()).method_1021(player.method_55754())), class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)player));
        return hit.method_17783() == class_239.class_240.field_1332 ? hit : null;
    }

    private static int shade(class_238 box, class_243 blast, List<class_243> points) {
        int blocked = 0;
        for (class_243 point : points) {
            if (!box.method_992(point, blast).isPresent()) continue;
            ++blocked;
        }
        return blocked;
    }

    private static List<class_243> bodyPoints(class_238 body) {
        double stepX = 1.0 / ((body.field_1320 - body.field_1323) * 2.0 + 1.0);
        double stepY = 1.0 / ((body.field_1325 - body.field_1322) * 2.0 + 1.0);
        double stepZ = 1.0 / ((body.field_1324 - body.field_1321) * 2.0 + 1.0);
        double offsetX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
        double offsetZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;
        ArrayList<class_243> points = new ArrayList<class_243>();
        for (double fx = 0.0; fx <= 1.0; fx += stepX) {
            for (double fy = 0.0; fy <= 1.0; fy += stepY) {
                for (double fz = 0.0; fz <= 1.0; fz += stepZ) {
                    points.add(new class_243(class_3532.method_16436((double)fx, (double)body.field_1323, (double)body.field_1320) + offsetX, class_3532.method_16436((double)fy, (double)body.field_1322, (double)body.field_1325), class_3532.method_16436((double)fz, (double)body.field_1321, (double)body.field_1324) + offsetZ));
                }
            }
        }
        return points;
    }

    @Environment(value=EnvType.CLIENT)
    public record Spot(class_2338 pos, class_2338 support, class_2350 face, class_243 aim) {
        public boolean standing() {
            return this.support == null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Cell(class_2338 pos, int shade, double distance) {
    }
}

