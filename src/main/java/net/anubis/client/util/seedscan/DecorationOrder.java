/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_2338
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.anubis.client.util.seedscan.ChunkDecoration;
import net.anubis.client.util.seedscan.TerrainChunk;
import net.anubis.client.util.seedscan.VBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public final class DecorationOrder {
    public static final int MIN_VOTES = 3;
    private final Map<Pair, int[]> votes = new HashMap<Pair, int[]>();
    private final Long2ObjectOpenHashMap<Map<Pair, int[]>> bySource = new Long2ObjectOpenHashMap();

    public int compare(long a, long b) {
        boolean aIsFirst;
        if (a == b) {
            return 0;
        }
        int[] v = this.votes.get(Pair.of(a, b));
        if (v == null) {
            return 0;
        }
        boolean bl = aIsFirst = a < b;
        if (v[0] >= 3 && v[1] == 0) {
            return aIsFirst ? 1 : -1;
        }
        if (v[1] >= 3 && v[0] == 0) {
            return aIsFirst ? -1 : 1;
        }
        return 0;
    }

    public boolean after(long a, long b) {
        return this.compare(a, b) > 0;
    }

    public Set<long[]> collect(ChunkDecoration own, Long2ObjectMap<ChunkDecoration> decorations, TerrainLookup terrain, Shown shown) {
        long ownKey = class_1923.method_8331((int)own.chunkX, (int)own.chunkZ);
        HashMap<Pair, int[]> fresh = new HashMap<Pair, int[]>();
        for (int i = 0; i < own.size(); ++i) {
            TerrainChunk chunk;
            long pos = own.key(i);
            byte ownKind = DecorationOrder.lastStone(own, i);
            if (ownKind < 0) continue;
            int x = class_2338.method_10061((long)pos);
            int y = class_2338.method_10071((long)pos);
            int z = class_2338.method_10083((long)pos);
            byte otherKind = -1;
            long other = 0L;
            boolean usable = true;
            boolean found = false;
            for (int dx = -1; dx <= 1 && usable; ++dx) {
                for (int dz = -1; dz <= 1 && usable; ++dz) {
                    int index;
                    long key = class_1923.method_8331((int)((x >> 4) + dx), (int)((z >> 4) + dz));
                    ChunkDecoration d = (ChunkDecoration)decorations.get(key);
                    if (d == null) continue;
                    for (ChunkDecoration.Fog fog : d.fogs()) {
                        if (!fog.contains(x, y, z)) continue;
                        usable = false;
                    }
                    if (key == ownKey || (index = d.find(pos)) < 0) continue;
                    if (found) {
                        usable = false;
                        continue;
                    }
                    found = true;
                    otherKind = DecorationOrder.lastStone(d, index);
                    other = key;
                    if (otherKind >= 0) continue;
                    usable = false;
                }
            }
            if (!usable || !found || otherKind == ownKind || other < ownKey || (chunk = terrain.terrain(x >> 4, z >> 4)) == null || !VBlock.isBase(chunk.kind(x, y, z))) continue;
            byte kind = shown.kind(x, y, z);
            int[] v = fresh.computeIfAbsent(Pair.of(ownKey, other), k -> new int[2]);
            if (kind == ownKind) {
                v[0] = v[0] + 1;
                continue;
            }
            if (kind != otherKind) continue;
            v[1] = v[1] + 1;
        }
        HashSet touched = new HashSet(fresh.keySet());
        Map old = (Map)this.bySource.put(ownKey, fresh);
        if (old != null) {
            touched.addAll(old.keySet());
        }
        HashSet<long[]> changed = new HashSet<long[]>();
        for (Pair pair : touched) {
            int before = this.compare(pair.first(), pair.second());
            int[] total = this.votes.computeIfAbsent(pair, k -> new int[2]);
            int[] was = old == null ? null : (int[])old.get(pair);
            int[] now = (int[])fresh.get(pair);
            if (was != null) {
                total[0] = total[0] - was[0];
                total[1] = total[1] - was[1];
            }
            if (now != null) {
                total[0] = total[0] + now[0];
                total[1] = total[1] + now[1];
            }
            if (total[0] == 0 && total[1] == 0) {
                this.votes.remove(pair);
            }
            if (this.compare(pair.first(), pair.second()) == before) continue;
            changed.add(new long[]{pair.first(), pair.second()});
        }
        return changed;
    }

    static long evidenceSignature(ChunkDecoration d) {
        long hash = 1125899906842597L;
        for (int i = 0; i < d.size(); ++i) {
            byte kind = DecorationOrder.lastStone(d, i);
            if (kind < 0) continue;
            hash = 31L * hash + d.key(i);
            hash = 31L * hash + (long)kind;
        }
        return 31L * hash + (long)d.fogs().hashCode();
    }

    private static byte lastStone(ChunkDecoration d, int index) {
        byte last = -1;
        for (int n = 0; n < d.writeCount(index); ++n) {
            short write = d.write(index, n);
            byte kind = ChunkDecoration.kind(write);
            if (ChunkDecoration.possible(write) || !VBlock.isBase(kind)) {
                return -1;
            }
            last = kind;
        }
        return last;
    }

    @Environment(value=EnvType.CLIENT)
    private record Pair(long first, long second) {
        static Pair of(long a, long b) {
            return a < b ? new Pair(a, b) : new Pair(b, a);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface TerrainLookup {
        public TerrainChunk terrain(int var1, int var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Shown {
        public byte kind(int var1, int var2, int var3);
    }
}

