/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  fi.dy.masa.litematica.world.WorldSchematic
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2199
 *  net.minecraft.class_2231
 *  net.minecraft.class_2241
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2276
 *  net.minecraft.class_2309
 *  net.minecraft.class_2312
 *  net.minecraft.class_2338
 *  net.minecraft.class_2341
 *  net.minecraft.class_2346
 *  net.minecraft.class_2349
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2350$class_2352
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2363
 *  net.minecraft.class_2366
 *  net.minecraft.class_2377
 *  net.minecraft.class_2399
 *  net.minecraft.class_2406
 *  net.minecraft.class_2426
 *  net.minecraft.class_2457
 *  net.minecraft.class_2458
 *  net.minecraft.class_2459
 *  net.minecraft.class_2465
 *  net.minecraft.class_2510
 *  net.minecraft.class_2530
 *  net.minecraft.class_2551
 *  net.minecraft.class_2555
 *  net.minecraft.class_2577
 *  net.minecraft.class_2665
 *  net.minecraft.class_2680
 *  net.minecraft.class_2738
 *  net.minecraft.class_2741
 *  net.minecraft.class_2760
 *  net.minecraft.class_2768
 *  net.minecraft.class_2769
 *  net.minecraft.class_2771
 *  net.minecraft.class_3486
 *  net.minecraft.class_3708
 *  net.minecraft.class_3715
 *  net.minecraft.class_3718
 *  net.minecraft.class_4481
 *  net.minecraft.class_638
 *  net.minecraft.class_8810
 */
package net.anubis.client.easyplace.builder;

import fi.dy.masa.litematica.world.WorldSchematic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import net.anubis.client.easyplace.util.BlockStateUtil;
import net.anubis.client.easyplace.util.StateRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2199;
import net.minecraft.class_2231;
import net.minecraft.class_2241;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2276;
import net.minecraft.class_2309;
import net.minecraft.class_2312;
import net.minecraft.class_2338;
import net.minecraft.class_2341;
import net.minecraft.class_2346;
import net.minecraft.class_2349;
import net.minecraft.class_2350;
import net.minecraft.class_2363;
import net.minecraft.class_2366;
import net.minecraft.class_2377;
import net.minecraft.class_2399;
import net.minecraft.class_2406;
import net.minecraft.class_2426;
import net.minecraft.class_2457;
import net.minecraft.class_2458;
import net.minecraft.class_2459;
import net.minecraft.class_2465;
import net.minecraft.class_2510;
import net.minecraft.class_2530;
import net.minecraft.class_2551;
import net.minecraft.class_2555;
import net.minecraft.class_2577;
import net.minecraft.class_2665;
import net.minecraft.class_2680;
import net.minecraft.class_2738;
import net.minecraft.class_2741;
import net.minecraft.class_2760;
import net.minecraft.class_2768;
import net.minecraft.class_2769;
import net.minecraft.class_2771;
import net.minecraft.class_3486;
import net.minecraft.class_3708;
import net.minecraft.class_3715;
import net.minecraft.class_3718;
import net.minecraft.class_4481;
import net.minecraft.class_638;
import net.minecraft.class_8810;

@Environment(value=EnvType.CLIENT)
final class BuildOrder {
    static final int STAGE_NORMAL = 0;
    static final int STAGE_PISTON = 1;
    static final int STAGE_OBSERVER = 2;
    static final int STAGE_POWER = 3;
    private static final int MAX_TRACK = 512;

    private BuildOrder() {
    }

    static boolean powerOrTnt(class_2680 state) {
        class_2248 block = state.method_26204();
        return block == class_2246.field_10002 || block instanceof class_2459 || block instanceof class_2309 || block instanceof class_2530;
    }

    static boolean late(WorldSchematic schematic, class_2338 pos, class_2680 expected, boolean machineLast) {
        return BuildOrder.stage(schematic, pos, expected, machineLast) > 0;
    }

    static int stage(WorldSchematic schematic, class_2338 pos, class_2680 expected, boolean machineLast) {
        int own = BuildOrder.ownStage(schematic, pos, expected, machineLast);
        if (own != 0) {
            return own;
        }
        class_2338 support = BuildOrder.hangsOn(expected, pos);
        return support == null ? 0 : BuildOrder.ownStage(schematic, support, schematic.method_8320(support), machineLast);
    }

    private static int ownStage(WorldSchematic schematic, class_2338 pos, class_2680 expected, boolean machineLast) {
        if (BuildOrder.powerOrTnt(expected) || BuildOrder.pushedIntoTnt(schematic, pos, expected) != null) {
            return 3;
        }
        if (!machineLast) {
            return 0;
        }
        class_2248 block = expected.method_26204();
        if (block instanceof class_2665) {
            return 1;
        }
        if (block instanceof class_2426) {
            return 2;
        }
        return 0;
    }

    static boolean pistonWouldFire(class_638 level, class_2338 pos) {
        return level.method_49803(pos) || level.method_49803(pos.method_10084());
    }

    static class_2338 pushedIntoTnt(WorldSchematic schematic, class_2338 pos, class_2680 expected) {
        if (expected.method_26215() || expected.method_26204() instanceof class_2530) {
            return null;
        }
        for (class_2350 way : class_2350.values()) {
            class_2338 ahead;
            class_2680 behind = schematic.method_8320(pos.method_10093(way.method_10153()));
            if (!(behind.method_26204() instanceof class_2665) || behind.method_11654((class_2769)class_2741.field_12525) != way || !(schematic.method_8320(ahead = pos.method_10093(way)).method_26204() instanceof class_2530)) continue;
            return ahead;
        }
        return null;
    }

    static class_2338 railBefore(class_638 level, WorldSchematic schematic, class_2338 pos, class_2680 expected, Predicate<class_2338> gaveUp) {
        List<class_2338> track = BuildOrder.track(schematic, pos);
        int index = track.indexOf(pos);
        if (index <= 0) {
            return null;
        }
        class_2338 before = track.get(index - 1);
        if (gaveUp.test(before) || level.method_8320(before).method_26204() instanceof class_2241) {
            return null;
        }
        return before;
    }

    static boolean railShapeOff(class_2680 actual, class_2680 expected) {
        class_2768 want = BuildOrder.railShape(expected);
        class_2768 got = BuildOrder.railShape(actual);
        return want != null && got != null && want != got;
    }

    private static List<class_2338> track(WorldSchematic schematic, class_2338 pos) {
        HashSet<class_2338> seen = new HashSet<class_2338>();
        ArrayList<class_2338> stack = new ArrayList<class_2338>(List.of(pos));
        class_2338 start = null;
        while (!stack.isEmpty() && seen.size() < 512) {
            class_2338 at = (class_2338)stack.remove(stack.size() - 1);
            if (!seen.add(at)) continue;
            List<class_2338> links = BuildOrder.links(schematic, at);
            if (links.size() < 2 && (start == null || BuildOrder.before(at, start))) {
                start = at;
            }
            stack.addAll(links);
        }
        if (start == null) {
            for (class_2338 rail : seen) {
                if (start != null && !BuildOrder.before(rail, start)) continue;
                start = rail;
            }
        }
        ArrayList<class_2338> order = new ArrayList<class_2338>();
        HashSet<class_2338> laid = new HashSet<class_2338>();
        class_2338 at = start;
        while (at != null && laid.add(at)) {
            order.add(at);
            class_2338 next = null;
            for (class_2338 link : BuildOrder.links(schematic, at)) {
                if (laid.contains(link) || next != null && !BuildOrder.before(link, next)) continue;
                next = link;
            }
            at = next;
        }
        return order;
    }

    private static boolean before(class_2338 a, class_2338 b) {
        if (a.method_10264() != b.method_10264()) {
            return a.method_10264() < b.method_10264();
        }
        if (a.method_10263() != b.method_10263()) {
            return a.method_10263() < b.method_10263();
        }
        return a.method_10260() < b.method_10260();
    }

    private static List<class_2338> links(WorldSchematic schematic, class_2338 pos) {
        ArrayList<class_2338> out = new ArrayList<class_2338>(2);
        class_2768 shape = BuildOrder.railShape(schematic.method_8320(pos));
        if (shape == null) {
            return out;
        }
        block0: for (class_2338 end : BuildOrder.ends(pos, shape)) {
            class_2338 other = BuildOrder.railAt(schematic, end);
            if (other == null) continue;
            class_2768 otherShape = BuildOrder.railShape(schematic.method_8320(other));
            for (class_2338 back : BuildOrder.ends(other, otherShape)) {
                if (!pos.equals((Object)BuildOrder.railAt(schematic, back))) continue;
                out.add(other);
                continue block0;
            }
        }
        return out;
    }

    private static class_2338 railAt(WorldSchematic schematic, class_2338 cell) {
        if (BuildOrder.railShape(schematic.method_8320(cell)) != null) {
            return cell;
        }
        return BuildOrder.railShape(schematic.method_8320(cell.method_10074())) != null ? cell.method_10074() : null;
    }

    private static List<class_2338> ends(class_2338 pos, class_2768 shape) {
        return switch (shape) {
            default -> throw new MatchException(null, null);
            case class_2768.field_12665 -> List.of(pos.method_10095(), pos.method_10072());
            case class_2768.field_12674 -> List.of(pos.method_10078(), pos.method_10067());
            case class_2768.field_12667 -> List.of(pos.method_10078().method_10084(), pos.method_10067());
            case class_2768.field_12666 -> List.of(pos.method_10067().method_10084(), pos.method_10078());
            case class_2768.field_12670 -> List.of(pos.method_10095().method_10084(), pos.method_10072());
            case class_2768.field_12668 -> List.of(pos.method_10072().method_10084(), pos.method_10095());
            case class_2768.field_12664 -> List.of(pos.method_10072(), pos.method_10078());
            case class_2768.field_12671 -> List.of(pos.method_10072(), pos.method_10067());
            case class_2768.field_12672 -> List.of(pos.method_10095(), pos.method_10067());
            case class_2768.field_12663 -> List.of(pos.method_10095(), pos.method_10078());
        };
    }

    private static class_2768 railShape(class_2680 state) {
        class_2248 class_22482 = state.method_26204();
        if (!(class_22482 instanceof class_2241)) {
            return null;
        }
        class_2241 rail = (class_2241)class_22482;
        class_2769 property = rail.method_9474();
        return state.method_28498(property) ? (class_2768)state.method_11654(property) : null;
    }

    static boolean lookOnly(class_2680 state) {
        class_2248 block = state.method_26204();
        return block instanceof class_2510 || block instanceof class_2363 || block instanceof class_3708 || block instanceof class_2465 || block instanceof class_2276 || block instanceof class_2366 || block instanceof class_2349 || block instanceof class_2406 || block instanceof class_3718 || block instanceof class_3715 || block instanceof class_4481 || block instanceof class_2199;
    }

    static List<class_2680> turnedVariants(class_2680 state) {
        ArrayList<class_2680> out = new ArrayList<class_2680>();
        for (class_2769 class_27692 : List.of(class_2741.field_12525, class_2741.field_12481, class_2741.field_12496, class_2741.field_12529)) {
            if (!state.method_28498(class_27692)) continue;
            for (Object value : class_27692.method_11898()) {
                class_2680 turned = BuildOrder.with(state, class_27692, value);
                if (turned.equals((Object)state)) continue;
                out.add(turned);
            }
        }
        return out;
    }

    private static class_2680 with(class_2680 state, class_2769 property, Object value) {
        return (class_2680)state.method_11657(property, (Comparable)value);
    }

    static class_2338 hangsOn(class_2680 state, class_2338 pos) {
        class_2248 block = state.method_26204();
        if (block instanceof class_2555 || block instanceof class_2458 || block instanceof class_2551 || block instanceof class_2399) {
            return pos.method_10093(((class_2350)state.method_11654((class_2769)class_2741.field_12481)).method_10153());
        }
        if (block instanceof class_2341) {
            return switch ((class_2738)state.method_11654((class_2769)class_2741.field_12555)) {
                default -> throw new MatchException(null, null);
                case class_2738.field_12475 -> pos.method_10074();
                case class_2738.field_12473 -> pos.method_10084();
                case class_2738.field_12471 -> pos.method_10093(((class_2350)state.method_11654((class_2769)class_2741.field_12481)).method_10153());
            };
        }
        if (block instanceof class_2457 || block instanceof class_2312 || block instanceof class_2241 || block instanceof class_2577 || block instanceof class_2231 || block instanceof class_8810) {
            return pos.method_10074();
        }
        return null;
    }

    static List<class_2338> supportCells(class_2680 expected, class_2338 pos) {
        class_2350 look = StateRules.lookFor(expected);
        if (look == null) {
            look = StateRules.flatLookFor(expected);
        }
        if (look != null) {
            ArrayList<class_2338> cells = new ArrayList<class_2338>(List.of(pos.method_10093(look), pos.method_10074()));
            for (class_2350 side : class_2350.class_2353.field_11062) {
                if (side == look) continue;
                cells.add(pos.method_10093(side));
            }
            if (look != class_2350.field_11036) {
                cells.add(pos.method_10084());
            }
            return cells;
        }
        class_2248 block = expected.method_26204();
        if (block instanceof class_2377) {
            class_2350 spout = (class_2350)expected.method_11654((class_2769)class_2741.field_12545);
            return spout == class_2350.field_11033 ? List.of(pos.method_10074(), pos.method_10084()) : List.of(pos.method_10093(spout));
        }
        if (BuildOrder.hangsOn(expected, pos) != null) {
            return List.of();
        }
        if (expected.method_28498((class_2769)class_2741.field_12496)) {
            class_2350.class_2351 axis = (class_2350.class_2351)expected.method_11654((class_2769)class_2741.field_12496);
            return List.of(pos.method_10093(class_2350.method_10169((class_2350.class_2351)axis, (class_2350.class_2352)class_2350.class_2352.field_11060)), pos.method_10093(class_2350.method_10169((class_2350.class_2351)axis, (class_2350.class_2352)class_2350.class_2352.field_11056)));
        }
        if (expected.method_28498((class_2769)class_2741.field_12485)) {
            return expected.method_11654((class_2769)class_2741.field_12485) == class_2771.field_12679 ? List.of(pos.method_10084()) : List.of(pos.method_10074());
        }
        if (expected.method_28498((class_2769)class_2741.field_12518)) {
            return expected.method_11654((class_2769)class_2741.field_12518) == class_2760.field_12619 ? List.of(pos.method_10084()) : List.of(pos.method_10074());
        }
        return List.of(pos.method_10074(), pos.method_10095(), pos.method_10072(), pos.method_10078(), pos.method_10067(), pos.method_10084());
    }

    static boolean waits(class_638 level, WorldSchematic schematic, class_2338 pos, class_2680 expected, Predicate<class_2338> gaveUp, boolean machineLast) {
        class_2248 block = expected.method_26204();
        class_2338 tnt = BuildOrder.pushedIntoTnt(schematic, pos, expected);
        if (tnt != null && !(level.method_8320(tnt).method_26204() instanceof class_2530)) {
            return true;
        }
        if (block instanceof class_2665) {
            return BuildOrder.pistonWouldFire(level, pos);
        }
        if (block instanceof class_2241 && BuildOrder.railBefore(level, schematic, pos, expected, gaveUp) != null) {
            return true;
        }
        if (block instanceof class_2426) {
            class_2338 watched = pos.method_10093((class_2350)expected.method_11654((class_2769)class_2741.field_12525));
            class_2680 wanted = schematic.method_8320(watched);
            if (BlockStateUtil.requiredItem(wanted) == null || BuildOrder.stage(schematic, watched, wanted, machineLast) > 2 || gaveUp.test(watched)) {
                return false;
            }
            if (wanted.method_26204() instanceof class_2426 && watched.method_10093((class_2350)wanted.method_11654((class_2769)class_2741.field_12525)).equals((Object)pos)) {
                return false;
            }
            return !StateRules.placed(level.method_8320(watched), wanted);
        }
        if (block instanceof class_2346) {
            return class_2346.method_10128((class_2680)level.method_8320(pos.method_10074()));
        }
        if (!expected.method_28498((class_2769)class_2741.field_12508) && expected.method_26227().method_15771() && expected.method_26227().method_15767(class_3486.field_15517)) {
            return !level.method_8316(pos).method_15771() || !level.method_8316(pos).method_15767(class_3486.field_15517);
        }
        if (block instanceof class_2530) {
            return level.method_49803(pos);
        }
        return false;
    }
}

