/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  fi.dy.masa.litematica.world.WorldSchematic
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2377
 *  net.minecraft.class_2401
 *  net.minecraft.class_2404
 *  net.minecraft.class_2530
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_3486
 *  net.minecraft.class_3610
 *  net.minecraft.class_638
 *  net.minecraft.class_8886
 *  net.minecraft.class_8887
 */
package net.anubis.client.easyplace.builder;

import fi.dy.masa.litematica.world.WorldSchematic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.anubis.client.easyplace.builder.BuildOrder;
import net.anubis.client.easyplace.builder.CellFiller;
import net.anubis.client.easyplace.schematic.SchematicManager;
import net.anubis.client.easyplace.schematic.TargetStatus;
import net.anubis.client.easyplace.util.BlockStateUtil;
import net.anubis.client.easyplace.util.StateRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2377;
import net.minecraft.class_2401;
import net.minecraft.class_2404;
import net.minecraft.class_2530;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_638;
import net.minecraft.class_8886;
import net.minecraft.class_8887;

@Environment(value=EnvType.CLIENT)
final class BuildScan {
    static final int NONE = Integer.MAX_VALUE;
    final List<Cell> missing = new ArrayList<Cell>();
    final List<Cell> adjust = new ArrayList<Cell>();
    final List<Liquid> liquids = new ArrayList<Liquid>();
    final Set<Long> waiting = new HashSet<Long>();
    final List<class_2338> poweredTnt = new ArrayList<class_2338>();
    final List<class_2338> tntForPlayer = new ArrayList<class_2338>();
    final List<class_2338> afterTntForPlayer = new ArrayList<class_2338>();
    private final List<Cell> tntCells = new ArrayList<Cell>();
    private boolean powerLeft;
    private final Set<Long> waterBelow = new HashSet<Long>();
    private final Map<Long, Integer> staged = new HashMap<Long, Integer>();
    private final List<Cell> lateCells = new ArrayList<Cell>();
    private boolean machineLast;
    int late;
    int leversOff;
    final List<class_2338> levers = new ArrayList<class_2338>();
    int railsOff;
    final List<class_2338> crafters = new ArrayList<class_2338>();
    final Map<class_2338, boolean[]> crafterSlots = new HashMap<class_2338, boolean[]>();
    int layer = Integer.MAX_VALUE;
    int wrong;
    int noItem;
    int unloaded;
    boolean wholeSchematic = true;
    private boolean placeTnt;

    BuildScan() {
    }

    static BuildScan run(class_638 level, WorldSchematic schematic, List<SchematicManager.ActiveBox> boxes, Predicate<class_2338> deferred, Predicate<class_2338> gaveUp, Predicate<class_2338> waterPending, boolean lateNow, Set<Long> scaffold, Set<Long> craftersDone, boolean wantCrafters, boolean placeTnt, boolean machineLast) {
        BuildScan scan = new BuildScan();
        scan.placeTnt = placeTnt;
        scan.machineLast = machineLast;
        if (boxes.isEmpty()) {
            return scan;
        }
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (SchematicManager.ActiveBox box : boxes) {
            minY = Math.min(minY, box.minY());
            maxY = Math.max(maxY, box.maxY());
        }
        class_2338.class_2339 cursor = new class_2338.class_2339();
        int y = minY;
        do {
            scan.wholeSchematic = true;
            while (y <= maxY) {
                if (scan.layer != Integer.MAX_VALUE && y > scan.layer + 1) {
                    scan.wholeSchematic = false;
                    break;
                }
                for (SchematicManager.ActiveBox box : boxes) {
                    if (y < box.minY() || y > box.maxY()) continue;
                    for (int x = box.minX(); x <= box.maxX(); ++x) {
                        for (int z = box.minZ(); z <= box.maxZ(); ++z) {
                            cursor.method_10103(x, y, z);
                            scan.visit(level, schematic, cursor, deferred, gaveUp, waterPending, lateNow, scaffold, craftersDone, wantCrafters);
                        }
                    }
                }
                ++y;
            }
            if (scan.wholeSchematic) break;
            scan.keepApproachesOpen(level, schematic, gaveUp, deferred);
        } while (scan.layer == Integer.MAX_VALUE);
        scan.releaseStages(gaveUp);
        scan.releaseTnt(level, schematic, deferred, gaveUp, scaffold);
        scan.keepApproachesOpen(level, schematic, gaveUp, deferred);
        return scan;
    }

    private void releaseStages(Predicate<class_2338> gaveUp) {
        int lowest = Integer.MAX_VALUE;
        for (Map.Entry<Long, Integer> entry : this.staged.entrySet()) {
            if (gaveUp.test(class_2338.method_10092((long)entry.getKey()))) continue;
            lowest = Math.min(lowest, entry.getValue());
        }
        for (Map.Entry<Long, Integer> entry : this.staged.entrySet()) {
            if (entry.getValue() <= lowest) continue;
            this.waiting.add(entry.getKey());
        }
    }

    private void keepApproachesOpen(class_638 level, WorldSchematic schematic, Predicate<class_2338> gaveUp, Predicate<class_2338> deferred) {
        HashSet<Long> open = new HashSet<Long>();
        for (Cell cell : this.missing) {
            open.add(cell.pos().method_10063());
        }
        HashSet<Long> held = new HashSet<Long>();
        for (Cell cell : this.lateCells) {
            open.add(cell.pos().method_10063());
            held.add(cell.pos().method_10063());
        }
        held.addAll(this.staged.keySet());
        ArrayList<Cell> arrayList = new ArrayList<Cell>(this.missing);
        arrayList.addAll(this.lateCells);
        for (Cell cell : arrayList) {
            class_2338 approach;
            if (this.waiting.contains(cell.pos().method_10063()) && !held.contains(cell.pos().method_10063())) continue;
            if (StateRules.lookFor(cell.expected()) == class_2350.field_11036) {
                class_2338 low = BuildScan.lowStand(level, schematic, cell.pos(), open);
                if (low != null) {
                    if (open.contains(low.method_10063())) {
                        this.waiting.add(low.method_10063());
                    }
                    if (!open.contains(low.method_10084().method_10063())) continue;
                    this.waiting.add(low.method_10084().method_10063());
                    continue;
                }
                this.waiting.add(cell.pos().method_10063());
                continue;
            }
            if (gaveUp.test(cell.pos()) || (approach = BuildScan.approach(cell.expected(), cell.pos())) == null || !open.contains(approach.method_10063())) continue;
            class_2338 back = BuildScan.approach(schematic.method_8320(approach), approach);
            if (cell.pos().equals((Object)back)) continue;
            this.waiting.add(approach.method_10063());
        }
        this.layer = Integer.MAX_VALUE;
        for (Cell cell : this.missing) {
            if (this.waiting.contains(cell.pos().method_10063()) || deferred.test(cell.pos())) continue;
            this.layer = Math.min(this.layer, cell.pos().method_10264());
        }
    }

    private static class_2338 lowStand(class_638 level, WorldSchematic schematic, class_2338 pos, Set<Long> open) {
        class_2338 best = null;
        int bestScore = -1;
        for (class_2350 side : class_2350.class_2353.field_11062) {
            int score;
            class_2338 feet = pos.method_10074().method_10093(side);
            class_2338 floor = feet.method_10074();
            boolean floorNow = !level.method_8320(floor).method_26220((class_1922)level, floor).method_1110();
            class_2680 floorPlanned = schematic.method_8320(floor);
            if (!floorNow && (floorPlanned.method_26215() || !floorPlanned.method_26227().method_15769()) || !BuildScan.freeCell(level, schematic, feet, open) || !BuildScan.freeCell(level, schematic, feet.method_10084(), open) || StateRules.lookFor(schematic.method_8320(feet.method_10084())) == class_2350.field_11036 || (score = (floorNow ? 2 : 0) + (open.contains(feet.method_10063()) ? 0 : 1)) <= bestScore) continue;
            bestScore = score;
            best = feet;
        }
        return best;
    }

    private static boolean freeCell(class_638 level, WorldSchematic schematic, class_2338 cell, Set<Long> open) {
        class_2680 now = level.method_8320(cell);
        if (!now.method_45474() || !now.method_26227().method_15769()) {
            return false;
        }
        class_2680 planned = schematic.method_8320(cell);
        if (planned.method_26204() instanceof class_2404) {
            return false;
        }
        return planned.method_26215() || open.contains(cell.method_10063());
    }

    private static class_2338 approach(class_2680 expected, class_2338 pos) {
        class_2350 spout;
        class_2350 look = StateRules.lookFor(expected);
        if (look == null) {
            look = StateRules.flatLookFor(expected);
        }
        if (look == null && expected.method_26204() instanceof class_2377 && (spout = (class_2350)expected.method_11654((class_2769)class_2741.field_12545)) != class_2350.field_11033) {
            look = spout;
        }
        return look == null ? null : pos.method_10093(look.method_10153());
    }

    static Map<class_1792, Integer> remainingItems(class_638 level, WorldSchematic schematic, List<SchematicManager.ActiveBox> boxes) {
        HashMap<class_1792, Integer> out = new HashMap<class_1792, Integer>();
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (SchematicManager.ActiveBox box : boxes) {
            for (int y = box.minY(); y <= box.maxY(); ++y) {
                for (int x = box.minX(); x <= box.maxX(); ++x) {
                    for (int z = box.minZ(); z <= box.maxZ(); ++z) {
                        class_1792 item;
                        cursor.method_10103(x, y, z);
                        class_2680 expected = schematic.method_8320((class_2338)cursor);
                        if (expected.method_26215() || !level.method_22340((class_2338)cursor)) continue;
                        class_2680 actual = level.method_8320((class_2338)cursor);
                        CellFiller.Kind liquid = BuildScan.needsLiquid(expected, actual);
                        if (liquid != null) {
                            out.merge(liquid.item, 1, Integer::sum);
                        }
                        if (!BlockStateUtil.isPrimaryPlacementState(expected) || (item = BlockStateUtil.requiredItem(expected)) == null || BlockStateUtil.classify(expected, actual) != TargetStatus.MISSING) continue;
                        out.merge(item, StateRules.itemsNeeded(actual, expected), Integer::sum);
                    }
                }
            }
        }
        return out;
    }

    private void releaseTnt(class_638 level, WorldSchematic schematic, Predicate<class_2338> deferred, Predicate<class_2338> gaveUp, Set<Long> scaffold) {
        if (this.powerLeft || !scaffold.isEmpty()) {
            return;
        }
        for (Cell cell : this.tntCells) {
            if (BuildOrder.waits(level, schematic, cell.pos(), cell.expected(), gaveUp, this.machineLast)) {
                this.poweredTnt.add(cell.pos());
                continue;
            }
            this.waiting.remove(cell.pos().method_10063());
            if (deferred.test(cell.pos()) || this.layer != Integer.MAX_VALUE && cell.pos().method_10264() >= this.layer) continue;
            this.layer = cell.pos().method_10264();
        }
    }

    private void visit(class_638 level, WorldSchematic schematic, class_2338.class_2339 cursor, Predicate<class_2338> deferred, Predicate<class_2338> gaveUp, Predicate<class_2338> waterPending, boolean lateNow, Set<Long> scaffold, Set<Long> craftersDone, boolean wantCrafters) {
        class_2680 expected = schematic.method_8320((class_2338)cursor);
        if (expected.method_26215()) {
            return;
        }
        if (!level.method_22340((class_2338)cursor)) {
            ++this.unloaded;
            return;
        }
        class_2680 actual = level.method_8320((class_2338)cursor);
        CellFiller.Kind liquid = BuildScan.needsLiquid(expected, actual);
        if (liquid != null) {
            class_2338 pos = cursor.method_10062();
            boolean capped = liquid == CellFiller.Kind.LAVA_SOURCE && BuildScan.capped(schematic, pos);
            this.liquids.add(new Liquid(pos, liquid, capped));
            if ((liquid != CellFiller.Kind.LAVA_SOURCE || capped) && waterPending.test(pos)) {
                this.waterBelow.add(pos.method_10063());
            }
        }
        if (!expected.method_26227().method_15769() && expected.method_26204().method_8389() == class_1802.field_8162) {
            return;
        }
        if (!BlockStateUtil.isPrimaryPlacementState(expected)) {
            return;
        }
        if (BlockStateUtil.requiredItem(expected) == null) {
            ++this.noItem;
            return;
        }
        TargetStatus status = BlockStateUtil.classify(expected, actual);
        switch (status) {
            case MISSING: {
                boolean intoTnt;
                class_2338 pos = cursor.method_10062();
                boolean tnt = expected.method_26204() instanceof class_2530;
                boolean bl = intoTnt = !tnt && BuildOrder.pushedIntoTnt(schematic, pos, expected) != null;
                if (!this.placeTnt && (tnt || intoTnt)) {
                    (tnt ? this.tntForPlayer : this.afterTntForPlayer).add(pos);
                    this.waiting.add(pos.method_10063());
                    return;
                }
                int stage = BuildOrder.stage(schematic, pos, expected, this.machineLast);
                if (lateNow && !tnt && !intoTnt && stage > 0) {
                    this.powerLeft = true;
                    this.staged.put(pos.method_10063(), stage);
                }
                if (lateNow && tnt) {
                    this.missing.add(new Cell(pos, expected, actual));
                    this.tntCells.add(new Cell(pos, expected, actual));
                    this.waiting.add(pos.method_10063());
                    return;
                }
                if (!lateNow && stage > 0) {
                    ++this.late;
                    this.lateCells.add(new Cell(pos, expected, actual));
                    this.waiting.add(pos.method_10063());
                    return;
                }
                this.missing.add(new Cell(pos, expected, actual));
                if (this.waterBelow.contains(pos.method_10074().method_10063()) || BuildOrder.waits(level, schematic, pos, expected, gaveUp, this.machineLast)) {
                    this.waiting.add(pos.method_10063());
                    return;
                }
                if (this.layer != Integer.MAX_VALUE || deferred.test(pos)) break;
                this.layer = pos.method_10264();
                break;
            }
            case ADJUST: {
                this.adjust.add(new Cell(cursor.method_10062(), expected, actual));
                break;
            }
            case CORRECT: {
                boolean[] disabled;
                if (expected.method_26204() instanceof class_2401 && ((Boolean)expected.method_11654((class_2769)class_2741.field_12484)).booleanValue() && !((Boolean)actual.method_11654((class_2769)class_2741.field_12484)).booleanValue()) {
                    ++this.leversOff;
                    this.levers.add(cursor.method_10062());
                }
                if (BuildOrder.railShapeOff(actual, expected)) {
                    ++this.railsOff;
                }
                if (!wantCrafters || !(expected.method_26204() instanceof class_8886) || craftersDone.contains(cursor.method_10063()) || (disabled = BuildScan.disabledSlots(schematic, (class_2338)cursor)) == null) break;
                class_2338 pos = cursor.method_10062();
                this.crafters.add(pos);
                this.crafterSlots.put(pos, disabled);
                break;
            }
            case WRONG: {
                if (scaffold.contains(cursor.method_10063())) break;
                ++this.wrong;
                break;
            }
        }
    }

    static CellFiller.Kind needsLiquid(class_2680 expected, class_2680 actual) {
        class_3610 fluid = expected.method_26227();
        if (fluid.method_15769() || !fluid.method_15771()) {
            return null;
        }
        class_3610 there = actual.method_26227();
        if (fluid.method_15767(class_3486.field_15518)) {
            if (there.method_15771() && there.method_15767(class_3486.field_15518)) {
                return null;
            }
            return actual.method_26215() || actual.method_45474() && (there.method_15769() || there.method_15767(class_3486.field_15518)) ? CellFiller.Kind.LAVA_SOURCE : null;
        }
        if (!fluid.method_15767(class_3486.field_15517)) {
            return null;
        }
        if (expected.method_28498((class_2769)class_2741.field_12508)) {
            if (!StateRules.placed(actual, expected) || ((Boolean)actual.method_11654((class_2769)class_2741.field_12508)).booleanValue()) {
                return null;
            }
            return CellFiller.Kind.WATERLOG;
        }
        if (there.method_15771() && there.method_15767(class_3486.field_15517)) {
            return null;
        }
        if (!actual.method_26215() && !actual.method_45474()) {
            return null;
        }
        return CellFiller.Kind.WATER_SOURCE;
    }

    private static boolean capped(WorldSchematic schematic, class_2338 pos) {
        class_2338.class_2339 up = pos.method_25503();
        for (int i = 0; i < 64; ++i) {
            up.method_10098(class_2350.field_11036);
            class_2680 state = schematic.method_8320((class_2338)up);
            if (state.method_26227().method_15767(class_3486.field_15518)) continue;
            return !state.method_26215() && state.method_26227().method_15769();
        }
        return false;
    }

    private static boolean[] disabledSlots(WorldSchematic schematic, class_2338 pos) {
        class_2586 class_25862 = schematic.method_8321(pos);
        if (!(class_25862 instanceof class_8887)) {
            return null;
        }
        class_8887 crafter = (class_8887)class_25862;
        boolean[] disabled = new boolean[9];
        boolean any = false;
        for (int i = 0; i < 9; ++i) {
            disabled[i] = crafter.method_54483(i);
            any |= disabled[i];
        }
        return (boolean[])(any ? disabled : null);
    }

    Map<class_1792, Integer> needs(int top) {
        HashMap<class_1792, Integer> out = new HashMap<class_1792, Integer>();
        for (Cell cell : this.missing) {
            class_1792 item;
            if (cell.pos().method_10264() > top || (item = BlockStateUtil.requiredItem(cell.expected())) == null) continue;
            out.merge(item, StateRules.itemsNeeded(cell.actual(), cell.expected()), Integer::sum);
        }
        return out;
    }

    @Environment(value=EnvType.CLIENT)
    record Cell(class_2338 pos, class_2680 expected, class_2680 actual) {
    }

    @Environment(value=EnvType.CLIENT)
    record Liquid(class_2338 pos, CellFiller.Kind kind, boolean capped) {
    }
}

