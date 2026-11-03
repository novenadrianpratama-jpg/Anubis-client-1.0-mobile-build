/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 */
package net.anubis.client.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_243;

@Environment(value=EnvType.CLIENT)
public final class BlockClusterGeometry {
    private static final int[][] AXES = new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};

    private BlockClusterGeometry() {
    }

    public static Mesh build(Collection<class_2338> blocks, Membership same) {
        ArrayList<int[]> unitEdges = new ArrayList<int[]>(blocks.size() * 4);
        ArrayList<Face> faces = new ArrayList<Face>(blocks.size() * 3);
        for (class_2338 pos : blocks) {
            int[] p = new int[]{pos.method_10263(), pos.method_10264(), pos.method_10260()};
            BlockClusterGeometry.addFaces(p, same, faces);
            BlockClusterGeometry.addEdges(p, same, unitEdges);
        }
        return new Mesh(BlockClusterGeometry.mergeCollinear(unitEdges), faces);
    }

    private static List<Segment> mergeCollinear(List<int[]> unitEdges) {
        HashMap<List, List> lines = new HashMap<List, List>();
        for (int[] e : unitEdges) {
            int axis = e[0];
            int u = (axis + 1) % 3;
            int v = (axis + 2) % 3;
            lines.computeIfAbsent(List.of(Integer.valueOf(axis), Integer.valueOf(e[1 + u]), Integer.valueOf(e[1 + v])), k -> new ArrayList()).add(e[1 + axis]);
        }
        ArrayList<Segment> merged = new ArrayList<Segment>(unitEdges.size());
        for (Map.Entry line : lines.entrySet()) {
            int axis = (Integer)((List)line.getKey()).get(0);
            int u = (axis + 1) % 3;
            int v = (axis + 2) % 3;
            List starts = (List)line.getValue();
            starts.sort(null);
            int runStart = (Integer)starts.get(0);
            int runEnd = runStart + 1;
            for (int i = 1; i <= starts.size(); ++i) {
                if (i < starts.size() && (Integer)starts.get(i) == runEnd) {
                    ++runEnd;
                    continue;
                }
                int[] a = new int[3];
                int[] b = new int[3];
                a[u] = b[u] = ((Integer)((List)line.getKey()).get(1)).intValue();
                a[v] = b[v] = ((Integer)((List)line.getKey()).get(2)).intValue();
                a[axis] = runStart;
                b[axis] = runEnd;
                merged.add(new Segment(BlockClusterGeometry.vec(a), BlockClusterGeometry.vec(b)));
                if (i >= starts.size()) continue;
                runStart = (Integer)starts.get(i);
                runEnd = runStart + 1;
            }
        }
        return merged;
    }

    public static List<List<class_2338>> connectedComponents(Collection<class_2338> blocks) {
        HashSet<class_2338> remaining = new HashSet<class_2338>(blocks);
        ArrayList<List<class_2338>> components = new ArrayList<List<class_2338>>();
        ArrayDeque<class_2338> queue = new ArrayDeque<class_2338>();
        while (!remaining.isEmpty()) {
            class_2338 seed = (class_2338)remaining.iterator().next();
            remaining.remove(seed);
            ArrayList<class_2338> component = new ArrayList<class_2338>();
            queue.add(seed);
            while (!queue.isEmpty()) {
                class_2338 current = (class_2338)queue.poll();
                component.add(current);
                for (int[] axis : AXES) {
                    for (int sign = -1; sign <= 1; sign += 2) {
                        class_2338 next = current.method_10069(axis[0] * sign, axis[1] * sign, axis[2] * sign);
                        if (!remaining.remove(next)) continue;
                        queue.add(next);
                    }
                }
            }
            components.add(component);
        }
        return components;
    }

    private static void addFaces(int[] p, Membership same, List<Face> out) {
        for (int axis = 0; axis < 3; ++axis) {
            int u = (axis + 1) % 3;
            int v = (axis + 2) % 3;
            for (int sign = -1; sign <= 1; sign += 2) {
                if (same.contains(p[0] + AXES[axis][0] * sign, p[1] + AXES[axis][1] * sign, p[2] + AXES[axis][2] * sign)) continue;
                int[] base = (int[])p.clone();
                if (sign > 0) {
                    int n = axis;
                    base[n] = base[n] + 1;
                }
                out.add(new Face(BlockClusterGeometry.corner(base, u, 0, v, 0), BlockClusterGeometry.corner(base, u, 1, v, 0), BlockClusterGeometry.corner(base, u, 1, v, 1), BlockClusterGeometry.corner(base, u, 0, v, 1)));
            }
        }
    }

    private static void addEdges(int[] p, Membership same, List<int[]> out) {
        for (int axis = 0; axis < 3; ++axis) {
            int u = (axis + 1) % 3;
            int v = (axis + 2) % 3;
            for (int su = -1; su <= 1; su += 2) {
                for (int sv = -1; sv <= 1; sv += 2) {
                    boolean concave;
                    boolean nu = BlockClusterGeometry.contains(same, p, u, su, -1, 0);
                    boolean nv = BlockClusterGeometry.contains(same, p, v, sv, -1, 0);
                    boolean diagonal = BlockClusterGeometry.contains(same, p, u, su, v, sv);
                    boolean convex = !nu && !nv;
                    boolean bl = concave = nu && nv && !diagonal;
                    if (!convex && !concave) continue;
                    int[] start = (int[])p.clone();
                    if (su > 0) {
                        int n = u;
                        start[n] = start[n] + 1;
                    }
                    if (sv > 0) {
                        int n = v;
                        start[n] = start[n] + 1;
                    }
                    out.add(new int[]{axis, start[0], start[1], start[2]});
                }
            }
        }
    }

    private static boolean contains(Membership same, int[] p, int axisA, int signA, int axisB, int signB) {
        int[] q = (int[])p.clone();
        int n = axisA;
        q[n] = q[n] + signA;
        if (axisB >= 0) {
            int n2 = axisB;
            q[n2] = q[n2] + signB;
        }
        return same.contains(q[0], q[1], q[2]);
    }

    private static class_243 corner(int[] base, int u, int du, int v, int dv) {
        int[] q = (int[])base.clone();
        int n = u;
        q[n] = q[n] + du;
        int n2 = v;
        q[n2] = q[n2] + dv;
        return BlockClusterGeometry.vec(q);
    }

    private static class_243 vec(int[] p) {
        return new class_243((double)p[0], (double)p[1], (double)p[2]);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Membership {
        public boolean contains(int var1, int var2, int var3);
    }

    @Environment(value=EnvType.CLIENT)
    public record Mesh(List<Segment> edges, List<Face> faces) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Segment(class_243 a, class_243 b) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Face(class_243 a, class_243 b, class_243 c, class_243 d) {
    }
}

