/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1922
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2346
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import net.anubis.client.easyplace.builder.Walker;
import net.anubis.client.easyplace.util.StateRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
final class StandPlanner {
    private static final double PILLAR_COST = 8.0;
    private static final double BRIDGE_COST = 5.0;
    private static final double BUILD_BRIDGE_COST = 2.0;
    private static final double PROP_PENALTY = 8.0;
    private static final double LEAP_COST = 2.5;
    private static final double WADE_COST = 2.5;
    private static final double ICE_COST = 2.0;
    private static final int MAX_GAP = 1;
    private static final double FALL_COST = 40.0;
    private static final double DROP_COST = 2.0;
    private static final int BRIDGE_LIMIT = 10;
    private static final double EYE = 1.62;
    private static final double CROUCH_EYE = 1.27;
    private static final int MAX_SPOTS = 12000;
    private static final double SLACK = 10.0;
    private static final double GAIN = 2.0;
    private static final int MAX_CANDIDATES = 16;
    private static final int COUNT_CAP = 24;
    private static final int RANGE = 40;
    private static final int AROUND = 8;
    private static final double[][] FACE_POINTS = new double[][]{{0.5, 0.5}, {0.2, 0.5}, {0.8, 0.5}, {0.5, 0.2}, {0.5, 0.8}};
    private static final double[][] EDGE_POINTS = new double[][]{{0.5, 0.5}, {0.03, 0.5}, {0.97, 0.5}, {0.5, 0.03}, {0.5, 0.97}};
    private static final int MAX_LEAVES = 10;
    private static final long[] NO_LEAVES = new long[0];
    private final class_638 level;
    private final class_746 player;
    private final List<Target> targets;
    private final Predicate<class_2338> avoid;
    private final double reach;
    private final Pillar canPillar;
    private final Predicate<class_2338> canBridge;
    private final Exact exact;
    private final Support support;
    private double maxFall;
    private final int maxFeetY;
    private final class_238 around;
    private final class_238 targetBox;
    private final class_2338 start;
    private final PriorityQueue<Node> open = new PriorityQueue<Node>(Comparator.comparingDouble(Node::rank));
    private final Map<Long, Double> best = new HashMap<Long, Double>();
    private final Map<Long, Long> parent = new HashMap<Long, Long>();
    private final Map<Long, Move> moves = new HashMap<Long, Move>();
    private final Map<Long, Integer> bridges = new HashMap<Long, Integer>();
    private final Map<Long, long[]> leaves = new HashMap<Long, long[]>();
    private final Map<Long, Target> bridgeTargets = new HashMap<Long, Target>();
    private int visited;
    private List<Step> route;
    private Target found;
    private class_2338 bestSpot;
    private double bestValue = -1.7976931348623157E308;
    private double firstCost = -1.0;
    private int candidates;
    private boolean propped;
    private class_2338 closest;
    private double closestDistance = Double.MAX_VALUE;
    private final double startDistance;

    StandPlanner(class_638 level, class_746 player, List<Target> targets, Predicate<class_2338> avoid, Pillar canPillar, Predicate<class_2338> canBridge, Exact exact, Support support) {
        this.level = level;
        this.player = player;
        this.targets = targets;
        this.avoid = avoid;
        this.canPillar = canPillar;
        this.canBridge = canBridge;
        this.exact = exact;
        this.support = support;
        this.reach = player.method_55754() - 0.25;
        this.start = player.method_24515();
        for (Target target : targets) {
            class_2680 expected = target.expected();
            if (target.use() != Use.PLACE || StateRules.lookFor(expected) != null || StateRules.flatLookFor(expected) != null || expected.method_26204() instanceof class_2346 || !expected.method_26234((class_1922)level, target.pos())) continue;
            this.bridgeTargets.put(target.pos().method_10063(), target);
        }
        class_238 box = null;
        for (Target target : targets) {
            box = box == null ? new class_238(target.pos()) : box.method_991(new class_238(target.pos()));
        }
        this.maxFeetY = box == null ? this.start.method_10264() : (int)box.field_1325 + 1;
        this.around = box == null ? new class_238(this.start) : box.method_991(new class_238(this.start)).method_1014(8.0);
        this.targetBox = box == null ? new class_238(this.start) : box.method_1014(8.0);
        this.startDistance = this.workDistance(this.start);
        this.open.add(new Node(this.start.method_10063(), 0.0, 0.0));
        this.best.put(this.start.method_10063(), 0.0);
        this.parent.put(this.start.method_10063(), Long.MIN_VALUE);
        this.leaves.put(this.start.method_10063(), NO_LEAVES);
    }

    int visited() {
        return this.visited;
    }

    int lowestTargetY() {
        return (int)Math.round(this.targetBox.field_1322) + 8;
    }

    String extent() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int pillars = 0;
        int bridgesSeen = 0;
        for (Map.Entry<Long, Move> entry : this.moves.entrySet()) {
            class_2338 pos = class_2338.method_10092((long)entry.getKey());
            minX = Math.min(minX, pos.method_10263());
            maxX = Math.max(maxX, pos.method_10263());
            minY = Math.min(minY, pos.method_10264());
            maxY = Math.max(maxY, pos.method_10264());
            minZ = Math.min(minZ, pos.method_10260());
            maxZ = Math.max(maxZ, pos.method_10260());
            if (entry.getValue() == Move.PILLAR) {
                ++pillars;
            }
            if (entry.getValue() != Move.BRIDGE) continue;
            ++bridgesSeen;
        }
        return "x " + minX + ".." + maxX + " y " + minY + ".." + maxY + " z " + minZ + ".." + maxZ + " pillars " + pillars + " bridges " + bridgesSeen + " targetBox " + String.valueOf(this.targetBox) + " maxFeetY " + this.maxFeetY + " | " + this.whyStuck();
    }

    String whyStuck() {
        boolean near = this.around.method_1006(this.start.method_46558());
        StringBuilder out = new StringBuilder("near=" + near + " around=" + String.valueOf(this.around) + " maxFeetY=" + this.maxFeetY + " avoidUp=" + this.avoid.test(this.start.method_10086(2)));
        if (this.canPillar != null) {
            out.append(" pillar=").append(this.canPillar.possible(this.start, false));
        }
        out.append(" floor=").append(Walker.floor(this.level, this.start));
        if (this.canBridge != null) {
            for (class_2350 way : class_2350.class_2353.field_11062) {
                class_2338 next = this.start.method_10093(way);
                out.append(' ').append(way).append(":side=").append(class_2248.method_9501((class_265)this.level.method_8320(this.start.method_10074()).method_26218((class_1922)this.level, this.start.method_10074()), (class_2350)way)).append(",gap=").append(Double.isNaN(Walker.floor(this.level, next))).append(",room=").append(Walker.roomAt(this.level, next)).append(",fits=").append(this.canBridge.test(next.method_10074())).append(",avoid=").append(this.avoid.test(next));
            }
        }
        return out.toString();
    }

    StandPlanner falls(double blocks) {
        this.maxFall = blocks;
        return this;
    }

    List<Step> route() {
        return this.route;
    }

    Target target() {
        return this.found;
    }

    Status step(long deadline) {
        while (!this.open.isEmpty()) {
            boolean onLeaf;
            if (System.nanoTime() > deadline) {
                return Status.RUNNING;
            }
            Node node = this.open.poll();
            if (node.cost() > this.best.getOrDefault(node.pos(), (Double)Double.MAX_VALUE)) continue;
            if (this.firstCost >= 0.0 && (node.cost() > this.firstCost + 10.0 || this.candidates >= 16)) {
                return this.finish();
            }
            if (++this.visited > 12000) {
                return this.finish();
            }
            class_2338 spot = class_2338.method_10092((long)node.pos());
            if (!spot.equals((Object)this.start)) {
                double distance = this.workDistance(spot);
                if (distance < this.closestDistance) {
                    this.closestDistance = distance;
                    this.closest = spot;
                }
                long[] planned = this.leaves.getOrDefault(node.pos(), NO_LEAVES);
                Target target = this.moves.get(node.pos()) == Move.BRIDGE ? this.bridgeTargets.get(spot.method_10074().method_10063()) : null;
                this.propped = false;
                if (target == null) {
                    target = this.workFrom(spot, planned);
                }
                boolean needsProp = this.propped;
                if (target != null) {
                    double value = (double)this.countFrom(spot, planned) * 2.0 - node.cost() - (needsProp && this.bridgeTargets.containsKey(target.pos().method_10063()) ? 8.0 : 0.0);
                    if (this.firstCost < 0.0) {
                        this.firstCost = node.cost();
                    }
                    ++this.candidates;
                    if (value > this.bestValue) {
                        this.bestValue = value;
                        this.bestSpot = spot;
                        this.found = target;
                    }
                }
            }
            for (class_2338 next : Walker.steps(this.level, spot)) {
                int rise = next.method_10264() - spot.method_10264();
                double wade = (this.level.method_8316(next).method_15769() ? 0.0 : 2.5) + (Walker.icy(this.level, next) ? 2.0 : 0.0);
                this.relax(spot, next, node.cost() + 1.0 + (rise > 0 ? 0.6 : (double)(-rise) * 2.0) + wade, Move.WALK);
            }
            if (this.maxFall > 0.0) {
                for (class_2350 way : class_2350.class_2353.field_11062) {
                    Iterator landing = Walker.fall(this.level, spot, way, this.maxFall);
                    if (landing == null) continue;
                    this.relax(spot, (class_2338)landing, node.cost() + 40.0 + (double)(spot.method_10264() - landing.method_10264()) * 2.0, Move.WALK);
                }
            }
            double fromY = Walker.floorOr(this.level, spot);
            for (class_2350 way : class_2350.class_2353.field_11062) {
                for (int gap = 1; gap <= 1; ++gap) {
                    if (!Walker.leap(this.level, spot, fromY, way, gap)) continue;
                    this.relax(spot, spot.method_10079(way, gap + 1), node.cost() + 2.5 + (double)gap, Move.LEAP);
                }
            }
            boolean near = this.around.method_1006(spot.method_46558()) || spot.method_19455((class_2382)this.start) <= 4;
            boolean under = near || (double)spot.method_10264() < this.targetBox.field_1322 && (double)spot.method_10263() + 0.5 >= this.targetBox.field_1323 && (double)spot.method_10263() + 0.5 <= this.targetBox.field_1320 && (double)spot.method_10260() + 0.5 >= this.targetBox.field_1321 && (double)spot.method_10260() + 0.5 <= this.targetBox.field_1324;
            boolean bl = onLeaf = this.moves.get(node.pos()) == Move.PILLAR || this.moves.get(node.pos()) == Move.BRIDGE;
            if (under && this.canPillar != null && spot.method_10264() + 1 <= this.maxFeetY && this.canPillar.possible(spot, onLeaf)) {
                if (spot.method_10264() + 2 <= this.maxFeetY) {
                    this.relax(spot, spot.method_10086(2), node.cost() + 8.0, Move.PILLAR);
                }
                if (spot.method_10264() + 1 >= this.lowestTargetY()) {
                    this.relax(spot, spot.method_10086(1), node.cost() + 4.8, Move.PILLAR);
                }
            }
            if (!near || !((double)spot.method_10264() >= this.targetBox.field_1322) || this.canBridge == null || this.bridges.getOrDefault(node.pos(), 0) >= 10) continue;
            this.bridgeFrom(spot, node.cost());
        }
        return this.finish();
    }

    private Status finish() {
        if (this.bestSpot == null) {
            if (this.closest == null || this.closestDistance > this.startDistance - 2.0) {
                return Status.NONE;
            }
            this.route = this.path(this.closest);
            return Status.APPROACH;
        }
        this.route = this.path(this.bestSpot);
        return Status.FOUND;
    }

    private int countFrom(class_2338 feet, long[] planned) {
        Target target;
        class_2338 pos;
        double y = Walker.floorOr(this.level, feet);
        class_243 eye = new class_243((double)feet.method_10263() + 0.5, y + 1.62, (double)feet.method_10260() + 0.5);
        class_238 body = new class_238((double)feet.method_10263() + 0.2, y, (double)feet.method_10260() + 0.2, (double)feet.method_10263() + 0.8, y + 1.8, (double)feet.method_10260() + 0.8);
        int count = 0;
        Iterator<Target> iterator = this.targets.iterator();
        while (iterator.hasNext() && ((pos = (target = iterator.next()).pos()).method_46558().method_1022(eye) > Math.min(this.reach, target.within()) || body.method_994(new class_238(pos)) || !(target.use() == Use.PLACE ? this.seenFrom(eye, target) || this.onPlanned(eye, target, planned) : this.placeableFrom(eye, target, planned)) || ++count < 24)) {
        }
        return count;
    }

    private boolean seenFrom(class_243 eye, Target target) {
        class_2350 look = StateRules.lookFor(target.expected());
        class_2350 flat = StateRules.flatLookFor(target.expected());
        for (class_2350 side : class_2350.values()) {
            class_265 shape;
            class_2338 support = target.pos().method_10093(side);
            class_2680 state = this.level.method_8320(support);
            if (state.method_45474() || (shape = state.method_26218((class_1922)this.level, support)).method_1110()) continue;
            class_2350 face = side.method_10153();
            class_238 box = shape.method_1107().method_996(support);
            if (!this.seen(eye, support, box, face, look, flat)) continue;
            return true;
        }
        return false;
    }

    private void bridgeFrom(class_2338 spot, double cost) {
        boolean onLeaf;
        Move how = this.moves.get(spot.method_10063());
        boolean bl = onLeaf = how == Move.BRIDGE || how == Move.PILLAR;
        if (!onLeaf && Walker.floor(this.level, spot) != (double)spot.method_10264()) {
            return;
        }
        class_2338 under = spot.method_10074();
        for (class_2350 way : class_2350.class_2353.field_11062) {
            boolean build;
            class_2338 next;
            if (!onLeaf && !class_2248.method_9501((class_265)this.level.method_8320(under).method_26218((class_1922)this.level, under), (class_2350)way) || !Double.isNaN(Walker.floor(this.level, next = spot.method_10093(way))) || !Walker.roomAt(this.level, next) || !(build = this.bridgeTargets.containsKey(next.method_10074().method_10063())) && !this.canBridge.test(next.method_10074())) continue;
            this.relax(spot, next, cost + (build ? 2.0 : 5.0), Move.BRIDGE);
        }
    }

    private void relax(class_2338 from, class_2338 next, double cost, Move move) {
        if (this.avoid.test(next)) {
            return;
        }
        if (Math.abs(next.method_10263() - this.start.method_10263()) > 40 || Math.abs(next.method_10260() - this.start.method_10260()) > 40 || Math.abs(next.method_10264() - this.start.method_10264()) > 40 || next.method_10264() > this.maxFeetY + 1) {
            return;
        }
        long key = next.method_10063();
        if (cost >= this.best.getOrDefault(key, (Double)Double.MAX_VALUE)) {
            return;
        }
        this.best.put(key, cost);
        this.parent.put(key, from.method_10063());
        this.moves.put(key, move);
        this.bridges.put(key, this.bridges.getOrDefault(from.method_10063(), 0) + (move == Move.BRIDGE ? 1 : 0));
        long[] before = this.leaves.getOrDefault(from.method_10063(), NO_LEAVES);
        this.leaves.put(key, move == Move.BRIDGE || move == Move.PILLAR ? StandPlanner.withLeaf(before, next.method_10074(), move == Move.PILLAR) : before);
        this.open.add(new Node(key, cost, cost + this.guess(next)));
    }

    private double workDistance(class_2338 spot) {
        class_243 eye = new class_243((double)spot.method_10263() + 0.5, (double)spot.method_10264() + 1.62, (double)spot.method_10260() + 0.5);
        double best = Double.MAX_VALUE;
        for (int i = 0; i < Math.min(32, this.targets.size()); ++i) {
            best = Math.min(best, this.targets.get(i).pos().method_46558().method_1022(eye));
        }
        return best;
    }

    private double guess(class_2338 spot) {
        class_243 eye = new class_243((double)spot.method_10263() + 0.5, (double)spot.method_10264() + 1.62, (double)spot.method_10260() + 0.5);
        double dx = Math.max(0.0, Math.max(this.targetBox.field_1323 + 8.0 - eye.field_1352, eye.field_1352 - (this.targetBox.field_1320 - 8.0)));
        double dy = Math.max(0.0, Math.max(this.targetBox.field_1322 + 8.0 - eye.field_1351, eye.field_1351 - (this.targetBox.field_1325 - 8.0))) * 6.5;
        double dz = Math.max(0.0, Math.max(this.targetBox.field_1321 + 8.0 - eye.field_1350, eye.field_1350 - (this.targetBox.field_1324 - 8.0)));
        return Math.max(0.0, Math.sqrt(dx * dx + dy * dy + dz * dz) - this.reach) * 0.6;
    }

    private List<Step> path(class_2338 end) {
        ArrayList<Step> out = new ArrayList<Step>();
        long at = end.method_10063();
        while (at != this.start.method_10063() && at != Long.MIN_VALUE) {
            out.add(new Step(class_2338.method_10092((long)at), this.moves.getOrDefault(at, Move.WALK)));
            at = this.parent.get(at);
        }
        Collections.reverse(out);
        return out;
    }

    private Target workFrom(class_2338 feet, long[] planned) {
        if (!this.level.method_8316(feet).method_15769() && !this.level.method_8316(feet).method_15771() || Walker.nearLava(this.level, feet)) {
            return null;
        }
        double y = Walker.floorOr(this.level, feet);
        class_243 eye = new class_243((double)feet.method_10263() + 0.5, y + 1.62, (double)feet.method_10260() + 0.5);
        class_238 body = new class_238((double)feet.method_10263() + 0.2, y, (double)feet.method_10260() + 0.2, (double)feet.method_10263() + 0.8, y + 1.8, (double)feet.method_10260() + 0.8);
        for (Target target : this.targets) {
            class_2338 pos = target.pos();
            if (pos.method_46558().method_1025(eye) > (this.reach + 0.9) * (this.reach + 0.9) || pos.method_46558().method_1022(eye) > target.within() || body.method_994(new class_238(pos)) && (target.use() != Use.PLACE || !target.expected().method_26220((class_1922)this.level, pos).method_1110()) || !this.placeableFrom(eye, target, planned)) continue;
            return target;
        }
        return null;
    }

    private boolean placeableFrom(class_243 eye, Target target, long[] planned) {
        class_2338 pos = target.pos();
        switch (target.use().ordinal()) {
            case 3: {
                return pos.method_46558().method_1022(eye) <= this.reach - 0.5 && this.support != null && this.support.possible(pos, eye);
            }
            case 2: 
            case 4: 
            case 5: {
                if (pos.method_46558().method_1022(eye) > this.reach) {
                    return false;
                }
                class_265 shape = this.level.method_8320(pos).method_26218((class_1922)this.level, pos);
                if (shape.method_1110()) {
                    return false;
                }
                class_238 box = shape.method_1107().method_996(pos);
                for (class_2350 face : class_2350.values()) {
                    if (!StandPlanner.facesEye(box, face, eye) || !this.seen(eye, pos, box, face, null, null)) continue;
                    return true;
                }
                return false;
            }
        }
        class_2350 look = target.use() == Use.PLACE ? StateRules.lookFor(target.expected()) : null;
        class_2350 flat = target.use() == Use.PLACE ? StateRules.flatLookFor(target.expected()) : null;
        for (class_2350 side : class_2350.values()) {
            class_265 shape;
            class_2338 support = pos.method_10093(side);
            class_2680 state = this.level.method_8320(support);
            if (state.method_45474() || (shape = state.method_26218((class_1922)this.level, support)).method_1110()) continue;
            class_2350 face = side.method_10153();
            class_238 box = shape.method_1107().method_996(support);
            if (!this.seen(eye, support, box, face, look, flat) || target.use() == Use.PLACE && this.exact != null && !this.exact.placeable(target.expected(), pos, eye)) continue;
            return true;
        }
        if (this.onPlanned(eye, target, planned)) {
            return true;
        }
        if (target.use() != Use.PLACE) {
            return false;
        }
        if (StateRules.slabHalf(this.level.method_8320(pos), target.expected())) {
            return this.exact != null && this.exact.placeable(target.expected(), pos, eye);
        }
        this.propped = this.support != null && this.support.behind(target.expected(), pos, eye);
        return this.propped;
    }

    private boolean onPlanned(class_243 eye, Target target, long[] planned) {
        if (planned.length == 0) {
            return false;
        }
        if (target.use() == Use.PLACE && (StateRules.lookFor(target.expected()) != null || StateRules.flatLookFor(target.expected()) != null)) {
            return false;
        }
        class_2338 pos = target.pos();
        for (long key : planned) {
            class_2350 face;
            class_238 box;
            class_2338 leaf = class_2338.method_10092((long)key);
            if (leaf.method_19455((class_2382)pos) != 1 || !this.level.method_8320(leaf).method_45474() || !StandPlanner.facesEye(box = new class_238(leaf), face = class_2350.method_10147((float)(pos.method_10263() - leaf.method_10263()), (float)(pos.method_10264() - leaf.method_10264()), (float)(pos.method_10260() - leaf.method_10260())), eye)) continue;
            for (double[] point : FACE_POINTS) {
                class_243 shy;
                class_243 aim = StandPlanner.facePoint(box, face, point[0], point[1]);
                if (aim.method_1022(eye) > this.reach || this.level.method_17742(new class_3959(eye, shy = aim.method_1020(aim.method_1020(eye).method_1029().method_1021(0.05)), class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)this.player)).method_17783() != class_239.class_240.field_1333) continue;
                return true;
            }
        }
        return false;
    }

    private static long[] withLeaf(long[] before, class_2338 leaf, boolean pillar) {
        long[] out = new long[Math.min(before.length + 1, 10)];
        int n = 0;
        for (int i = Math.max(0, before.length + 1 - 10); i < before.length; ++i) {
            class_2338 old = class_2338.method_10092((long)before[i]);
            if (pillar && old.method_10263() == leaf.method_10263() && old.method_10260() == leaf.method_10260() && old.method_10264() < leaf.method_10264()) continue;
            out[n++] = before[i];
        }
        out[n++] = leaf.method_10063();
        return n == out.length ? out : Arrays.copyOf(out, n);
    }

    private boolean seen(class_243 eye, class_2338 block, class_238 box, class_2350 face, class_2350 look, class_2350 flat) {
        if (this.seenFrom(eye, block, box, face, look, flat, FACE_POINTS, 0.2)) {
            return true;
        }
        return look == class_2350.field_11036 && this.seenFrom(eye.method_1023(0.0, 0.3500000000000001, 0.0), block, box, face, look, flat, EDGE_POINTS, 0.05);
    }

    private boolean seenFrom(class_243 eye, class_2338 block, class_238 box, class_2350 face, class_2350 look, class_2350 flat, double[][] points, double margin) {
        if (!StandPlanner.facesEye(box, face, eye)) {
            return false;
        }
        for (double[] point : points) {
            class_3965 hit;
            class_243 aim = StandPlanner.facePoint(box, face, point[0], point[1]);
            if (aim.method_1022(eye) > this.reach || look != null && !Walker.dominant(aim.method_1020(eye), look, margin) || flat != null && !Walker.facingFlat(aim.method_1020(eye), flat) || (hit = this.level.method_17742(new class_3959(eye, aim.method_1019(aim.method_1020(eye).method_1029().method_1021(0.05)), class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)this.player))).method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)block) || hit.method_17780() != face) continue;
            return true;
        }
        return false;
    }

    private static boolean facesEye(class_238 box, class_2350 face, class_243 eye) {
        double gap = 0.05;
        return switch (face) {
            default -> throw new MatchException(null, null);
            case class_2350.field_11036 -> {
                if (eye.field_1351 > box.field_1325 + gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11033 -> {
                if (eye.field_1351 < box.field_1322 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11043 -> {
                if (eye.field_1350 < box.field_1321 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11035 -> {
                if (eye.field_1350 > box.field_1324 + gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11039 -> {
                if (eye.field_1352 < box.field_1323 - gap) {
                    yield true;
                }
                yield false;
            }
            case class_2350.field_11034 -> eye.field_1352 > box.field_1320 + gap;
        };
    }

    private static class_243 facePoint(class_238 box, class_2350 face, double u, double v) {
        double x = box.field_1323 + (box.field_1320 - box.field_1323) * u;
        double y = box.field_1322 + (box.field_1325 - box.field_1322) * v;
        return switch (face) {
            default -> throw new MatchException(null, null);
            case class_2350.field_11036 -> new class_243(x, box.field_1325, box.field_1321 + (box.field_1324 - box.field_1321) * v);
            case class_2350.field_11033 -> new class_243(x, box.field_1322, box.field_1321 + (box.field_1324 - box.field_1321) * v);
            case class_2350.field_11043 -> new class_243(x, y, box.field_1321);
            case class_2350.field_11035 -> new class_243(x, y, box.field_1324);
            case class_2350.field_11039 -> new class_243(box.field_1323, y, box.field_1321 + (box.field_1324 - box.field_1321) * u);
            case class_2350.field_11034 -> new class_243(box.field_1320, y, box.field_1321 + (box.field_1324 - box.field_1321) * u);
        };
    }

    @Environment(value=EnvType.CLIENT)
    static interface Pillar {
        public boolean possible(class_2338 var1, boolean var2);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Exact {
        public boolean placeable(class_2680 var1, class_2338 var2, class_243 var3);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Support {
        public boolean possible(class_2338 var1, class_243 var2);

        public boolean behind(class_2680 var1, class_2338 var2, class_243 var3);
    }

    @Environment(value=EnvType.CLIENT)
    record Target(class_2338 pos, class_2680 expected, Use use, double within) {
        Target(class_2338 pos, class_2680 expected, Use use) {
            this(pos, expected, use, Double.MAX_VALUE);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Use {
        PLACE,
        POUR,
        WATERLOG,
        NEAR,
        BREAK,
        CLICK;

    }

    @Environment(value=EnvType.CLIENT)
    private record Node(long pos, double cost, double rank) {
    }

    @Environment(value=EnvType.CLIENT)
    static enum Move {
        WALK,
        PILLAR,
        BRIDGE,
        LEAP;

    }

    @Environment(value=EnvType.CLIENT)
    static enum Status {
        RUNNING,
        FOUND,
        NONE,
        APPROACH;

    }

    @Environment(value=EnvType.CLIENT)
    record Step(class_2338 pos, Move move) {
    }
}

