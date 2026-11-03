/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_2338
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.anubis.client.util.seedscan.BlobJudge;
import net.anubis.client.util.seedscan.ChunkDecoration;
import net.anubis.client.util.seedscan.DecorationOrder;
import net.anubis.client.util.seedscan.GateHeights;
import net.anubis.client.util.seedscan.OrePredictor;
import net.anubis.client.util.seedscan.SeedWorldgen;
import net.anubis.client.util.seedscan.ShownChunk;
import net.anubis.client.util.seedscan.TerrainChunk;
import net.anubis.client.util.seedscan.VBlock;
import net.anubis.client.util.seedscan.VanillaBand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_2338;

@Environment(value=EnvType.CLIENT)
public final class PredictionArea {
    public static final int MARGIN = 1;
    public static final int DOUBT_MISMATCH = 8;
    public static final int MISMATCH_COLUMNS = 26;
    private final SeedWorldgen world;
    private final Long2ObjectOpenHashMap<TerrainChunk> terrains = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<ShownChunk> shown = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<ChunkDecoration> decorations = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<MayWrite> may = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<GateHeights> gateHeights = new Long2ObjectOpenHashMap();
    private final DecorationOrder order = new DecorationOrder();
    private final Long2IntOpenHashMap doubt = new Long2IntOpenHashMap();
    private LongOpenHashSet bandChunks = new LongOpenHashSet();
    private LongOpenHashSet decorationChunks = new LongOpenHashSet();
    private final LongOpenHashSet dirty = new LongOpenHashSet();
    private final LongOpenHashSet built = new LongOpenHashSet();
    private final Long2ObjectOpenHashMap<VanillaBand> bands = new Long2ObjectOpenHashMap();
    private final LongOpenHashSet clustersDue = new LongOpenHashSet();
    private final LongOpenHashSet clustersSent = new LongOpenHashSet();
    private final ArrayDeque<ChunkClusters> out = new ArrayDeque();
    private final LongOpenHashSet collected = new LongOpenHashSet();
    private final Long2LongOpenHashMap signatures = new Long2LongOpenHashMap();
    private int focusX;
    private int focusZ;
    private int compared;
    private int mismatched;

    public PredictionArea(SeedWorldgen world) {
        this.world = world;
        this.doubt.defaultReturnValue(-1);
    }

    public void want(LongCollection judged, int focusChunkX, int focusChunkZ) {
        long key;
        this.focusX = focusChunkX;
        this.focusZ = focusChunkZ;
        LongOpenHashSet wanted = new LongOpenHashSet(judged);
        LongOpenHashSet decos = new LongOpenHashSet();
        int reach = 2;
        LongIterator it = wanted.iterator();
        while (it.hasNext()) {
            key = it.nextLong();
            int x = class_1923.method_8325((long)key);
            int z = class_1923.method_8332((long)key);
            for (int dx = -reach; dx <= reach; ++dx) {
                for (int dz = -reach; dz <= reach; ++dz) {
                    decos.add(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
                }
            }
        }
        it = decos.iterator();
        while (it.hasNext()) {
            key = it.nextLong();
            if (this.decorations.containsKey(key)) continue;
            this.dirty.add(key);
        }
        this.bandChunks = wanted;
        this.decorationChunks = decos;
        this.built.retainAll((LongCollection)wanted);
        this.bands.keySet().retainAll((LongCollection)wanted);
        this.clustersDue.retainAll((LongCollection)wanted);
        this.clustersSent.retainAll((LongCollection)wanted);
        this.dirty.retainAll((LongCollection)decos);
        this.forgetOutside(decos);
    }

    private void forgetOutside(LongOpenHashSet decos) {
        LongOpenHashSet near = new LongOpenHashSet();
        LongIterator it = decos.iterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            int x = class_1923.method_8325((long)key);
            int z = class_1923.method_8332((long)key);
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    near.add(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
                }
            }
        }
        this.terrains.keySet().retainAll((LongCollection)near);
        this.shown.keySet().retainAll((LongCollection)near);
        this.gateHeights.keySet().retainAll((LongCollection)near);
        this.doubt.keySet().retainAll((LongCollection)near);
        this.decorations.keySet().retainAll((LongCollection)decos);
        this.may.keySet().retainAll((LongCollection)decos);
        this.collected.retainAll((LongCollection)decos);
        this.signatures.keySet().retainAll((LongCollection)decos);
    }

    public void offer(ShownChunk chunk) {
        int dz;
        int dx;
        long key = class_1923.method_8331((int)chunk.chunkX, (int)chunk.chunkZ);
        this.shown.put(key, (Object)chunk);
        this.doubt.remove(key);
        this.gateHeights.remove(key);
        for (dx = -3; dx <= 3; ++dx) {
            for (dz = -3; dz <= 3; ++dz) {
                this.collected.remove(class_1923.method_8331((int)(chunk.chunkX + dx), (int)(chunk.chunkZ + dz)));
            }
        }
        for (dx = -1; dx <= 1; ++dx) {
            for (dz = -1; dz <= 1; ++dz) {
                long near = class_1923.method_8331((int)(chunk.chunkX + dx), (int)(chunk.chunkZ + dz));
                if (!this.decorationChunks.contains(near)) continue;
                this.markDirty(near);
            }
        }
    }

    public boolean idle() {
        if (!(this.dirty.isEmpty() && this.out.isEmpty() && this.clustersDue.isEmpty())) {
            return false;
        }
        LongIterator it = this.bandChunks.iterator();
        while (it.hasNext()) {
            if (this.built.contains(it.nextLong())) continue;
            return false;
        }
        return true;
    }

    public int compared() {
        return this.compared;
    }

    public int mismatched() {
        return this.mismatched;
    }

    public ChunkClusters step() {
        long key;
        if (!this.out.isEmpty()) {
            return this.out.poll();
        }
        LongOpenHashSet firstTime = new LongOpenHashSet();
        LongIterator it = this.dirty.iterator();
        while (it.hasNext()) {
            long key2 = it.nextLong();
            if (this.decorations.containsKey(key2)) continue;
            firstTime.add(key2);
        }
        long next = this.nearest(firstTime.isEmpty() ? this.dirty : firstTime);
        if (next != Long.MIN_VALUE) {
            int x = class_1923.method_8325((long)next);
            int z = class_1923.method_8332((long)next);
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    long key3 = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                    if (this.terrains.containsKey(key3)) continue;
                    this.terrains.put(key3, (Object)this.world.terrain(x + dx, z + dz));
                    return null;
                }
            }
            this.predict(next);
            return null;
        }
        LongOpenHashSet waiting = new LongOpenHashSet();
        LongIterator it2 = this.bandChunks.iterator();
        while (it2.hasNext()) {
            key = it2.nextLong();
            if (this.built.contains(key)) continue;
            waiting.add(key);
        }
        while (!waiting.isEmpty()) {
            long key4 = this.nearest(waiting);
            waiting.remove(key4);
            if (!this.ready(key4)) continue;
            int x = class_1923.method_8325((long)key4);
            int z = class_1923.method_8332((long)key4);
            ChunkDecoration[] around = new ChunkDecoration[9];
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    around[(dx + 1) * 3 + dz + 1] = (ChunkDecoration)this.decorations.get(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
                }
            }
            VanillaBand band = VanillaBand.of((TerrainChunk)this.terrains.get(key4), around, this.order);
            this.built.add(key4);
            this.bands.put(key4, (Object)band);
            for (int dx = -2; dx <= 2; ++dx) {
                for (int dz = -2; dz <= 2; ++dz) {
                    long near = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                    if (!this.bandChunks.contains(near)) continue;
                    this.clustersDue.add(near);
                }
            }
            return null;
        }
        LongOpenHashSet due = new LongOpenHashSet((LongCollection)this.clustersDue);
        while (!due.isEmpty()) {
            key = this.nearest(due);
            due.remove(key);
            if (!this.clustersReady(key)) continue;
            this.clustersDue.remove(key);
            this.clustersSent.add(key);
            return new ChunkClusters(key, List.copyOf(BlobJudge.clusters(class_1923.method_8325((long)key), class_1923.method_8332((long)key), (cx, cz) -> (VanillaBand)this.bands.get(class_1923.method_8331((int)cx, (int)cz)))));
        }
        return null;
    }

    private boolean clustersReady(long key) {
        if (!this.bands.containsKey(key)) {
            return false;
        }
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                long near = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                if (!this.bandChunks.contains(near) || this.bands.containsKey(near)) continue;
                return false;
            }
        }
        return true;
    }

    private void unbuild(long key) {
        if (!this.built.remove(key)) {
            return;
        }
        this.bands.remove(key);
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                long near = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                if (this.clustersSent.remove(near)) {
                    this.out.add(new ChunkClusters(near, null));
                }
                if (!this.bandChunks.contains(near)) continue;
                this.clustersDue.add(near);
            }
        }
    }

    private boolean ready(long key) {
        int dz;
        int dx;
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        for (dx = -1; dx <= 1; ++dx) {
            for (dz = -1; dz <= 1; ++dz) {
                if (this.decorations.containsKey(class_1923.method_8331((int)(x + dx), (int)(z + dz)))) continue;
                return false;
            }
        }
        if (!this.terrains.containsKey(key)) {
            return false;
        }
        for (dx = -3; dx <= 3; ++dx) {
            for (dz = -3; dz <= 3; ++dz) {
                if (!this.dirty.contains(class_1923.method_8331((int)(x + dx), (int)(z + dz)))) continue;
                return false;
            }
        }
        return true;
    }

    private void markDirty(long key) {
        if (!this.decorationChunks.contains(key)) {
            return;
        }
        this.dirty.add(key);
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                this.unbuild(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
            }
        }
    }

    private void predict(long key) {
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        ChunkDecoration full = OrePredictor.predict(this.world, x, z, this.inputs(x, z));
        this.dirty.remove(key);
        ChunkDecoration decoration = full.trimmed(-16, 63, true);
        ChunkDecoration before = (ChunkDecoration)this.decorations.put(key, (Object)decoration);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                this.unbuild(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
            }
        }
        MayWrite own = (MayWrite)this.may.computeIfAbsent(key, k -> new MayWrite(x, z));
        if (own.absorb(full)) {
            for (int dx = -2; dx <= 2; ++dx) {
                for (int dz = -2; dz <= 2; ++dz) {
                    if (dx == 0 && dz == 0) continue;
                    this.markDirty(class_1923.method_8331((int)(x + dx), (int)(z + dz)));
                }
            }
        }
        long signature = DecorationOrder.evidenceSignature(decoration);
        boolean evidenceChanged = before == null || this.signatures.get(key) != signature || !this.collected.contains(key);
        this.signatures.put(key, signature);
        ArrayList<long[]> changed = new ArrayList<long[]>();
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                long near = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                ChunkDecoration d = (ChunkDecoration)this.decorations.get(near);
                if (d == null || !this.neighbourhoodGuessed(near) || !evidenceChanged && this.collected.contains(near)) continue;
                changed.addAll(this.order.collect(d, (Long2ObjectMap<ChunkDecoration>)this.decorations, this::terrainOrNull, this::shownKind));
                this.collected.add(near);
            }
        }
        Iterator iterator = changed.iterator();
        while (iterator.hasNext()) {
            long[] pair;
            for (long end : pair = (long[])iterator.next()) {
                int ex = class_1923.method_8325((long)end);
                int ez = class_1923.method_8332((long)end);
                for (int dx = -2; dx <= 2; ++dx) {
                    for (int dz = -2; dz <= 2; ++dz) {
                        this.markDirty(class_1923.method_8331((int)(ex + dx), (int)(ez + dz)));
                    }
                }
            }
        }
    }

    private boolean neighbourhoodGuessed(long key) {
        int x = class_1923.method_8325((long)key);
        int z = class_1923.method_8332((long)key);
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                long near = class_1923.method_8331((int)(x + dx), (int)(z + dz));
                if (!this.decorationChunks.contains(near) || this.decorations.containsKey(near)) continue;
                return false;
            }
        }
        return true;
    }

    private TerrainChunk terrainOrNull(int chunkX, int chunkZ) {
        return (TerrainChunk)this.terrains.get(class_1923.method_8331((int)chunkX, (int)chunkZ));
    }

    private byte shownKind(int x, int y, int z) {
        ShownChunk chunk = (ShownChunk)this.shown.get(class_1923.method_8331((int)(x >> 4), (int)(z >> 4)));
        return chunk == null ? (byte)15 : chunk.kind(x, y, z);
    }

    private int heightDoubt(long key, TerrainChunk model, ShownChunk seen) {
        int known = this.doubt.get(key);
        if (known != -1) {
            return known;
        }
        int off = 0;
        int columns = 0;
        int chunkX = class_1923.method_8325((long)key);
        int chunkZ = class_1923.method_8332((long)key);
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                int wx = (chunkX << 4) + x;
                int wz = (chunkZ << 4) + z;
                int top = seen.terrainTop(wx, wz);
                if (top == Integer.MIN_VALUE) continue;
                ++columns;
                if (Math.abs(top - model.oceanFloor(wx, wz)) <= 2) continue;
                ++off;
            }
        }
        int result = columns >= 64 && off * 256 > 26 * columns ? 8 : 0;
        this.doubt.put(key, result);
        if (columns >= 64) {
            ++this.compared;
            if (result == 8) {
                ++this.mismatched;
            }
        }
        return result;
    }

    private GateHeights gateHeights(int chunkX, int chunkZ) {
        ShownChunk seen;
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        GateHeights heights = (GateHeights)this.gateHeights.get(key);
        if (heights != null) {
            return heights;
        }
        TerrainChunk model = (TerrainChunk)this.terrains.get(key);
        heights = GateHeights.of(model, seen, (seen = (ShownChunk)this.shown.get(key)) == null ? 0 : this.heightDoubt(key, model, seen), this.world.treeAllowance(chunkX, chunkZ));
        this.gateHeights.put(key, (Object)heights);
        return heights;
    }

    private OrePredictor.Inputs inputs(int chunkX, int chunkZ) {
        final long self = class_1923.method_8331((int)chunkX, (int)chunkZ);
        return new OrePredictor.Inputs(){

            @Override
            public TerrainChunk terrain(int x, int z) {
                return (TerrainChunk)PredictionArea.this.terrains.get(class_1923.method_8331((int)x, (int)z));
            }

            @Override
            public GateHeights gateHeights(int x, int z) {
                return PredictionArea.this.gateHeights(x, z);
            }

            @Override
            public boolean othersMayWrite(int x, int y, int z) {
                int px = x >> 4;
                int pz = z >> 4;
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        long writer = class_1923.method_8331((int)(px + dx), (int)(pz + dz));
                        if (writer == self || PredictionArea.this.order.after(writer, self)) continue;
                        if (!PredictionArea.this.decorationChunks.contains(writer)) {
                            return true;
                        }
                        MayWrite w = (MayWrite)PredictionArea.this.may.get(writer);
                        if (w == null || !w.nonBase(x, y, z)) continue;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public byte shown(int x, int y, int z) {
                return PredictionArea.this.shownKind(x, y, z);
            }

            @Override
            public boolean predictedOre(int x, int y, int z) {
                int px = x >> 4;
                int pz = z >> 4;
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        MayWrite w = (MayWrite)PredictionArea.this.may.get(class_1923.method_8331((int)(px + dx), (int)(pz + dz)));
                        if (w == null || !w.ore(x, y, z)) continue;
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private long nearest(LongOpenHashSet keys) {
        long best = Long.MIN_VALUE;
        int bestDistance = Integer.MAX_VALUE;
        LongIterator it = keys.iterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            int distance = Math.max(Math.abs(class_1923.method_8325((long)key) - this.focusX), Math.abs(class_1923.method_8332((long)key) - this.focusZ));
            if (distance >= bestDistance) continue;
            bestDistance = distance;
            best = key;
        }
        return best;
    }

    @Environment(value=EnvType.CLIENT)
    public record ChunkClusters(long chunk, List<BlobJudge.Cluster> clusters) {
    }

    @Environment(value=EnvType.CLIENT)
    static final class MayWrite {
        static final int TOP = 200;
        private final int originX;
        private final int originZ;
        private int[] nonBase = new int[0];
        private int[] ores = new int[0];
        private final List<ChunkDecoration.Fog> fogs = new ArrayList<ChunkDecoration.Fog>();

        MayWrite(int chunkX, int chunkZ) {
            this.originX = chunkX - 1 << 4;
            this.originZ = chunkZ - 1 << 4;
        }

        private int index(int x, int y, int z) {
            int lx = x - this.originX;
            int lz = z - this.originZ;
            if (lx < 0 || lx >= 48 || lz < 0 || lz >= 48 || y < -64 || y > 200) {
                return -1;
            }
            return ((y - -64) * 48 + lz) * 48 + lx;
        }

        boolean absorb(ChunkDecoration d) {
            IntOpenHashSet addNonBase = new IntOpenHashSet();
            IntOpenHashSet addOres = new IntOpenHashSet();
            for (int i = 0; i < d.size(); ++i) {
                long pos = d.key(i);
                int index = this.index(class_2338.method_10061((long)pos), class_2338.method_10071((long)pos), class_2338.method_10083((long)pos));
                if (index < 0) continue;
                for (int n = 0; n < d.writeCount(i); ++n) {
                    byte kind = ChunkDecoration.kind(d.write(i, n));
                    if (VBlock.isBase(kind)) continue;
                    if (Arrays.binarySearch(this.nonBase, index) < 0) {
                        addNonBase.add(index);
                    }
                    if (kind != 11 || Arrays.binarySearch(this.ores, index) >= 0) continue;
                    addOres.add(index);
                }
            }
            boolean grew = !addNonBase.isEmpty() || !addOres.isEmpty();
            this.nonBase = MayWrite.merged(this.nonBase, addNonBase);
            this.ores = MayWrite.merged(this.ores, addOres);
            for (ChunkDecoration.Fog fog : d.fogs()) {
                if (VBlock.isBase(fog.kind()) || this.fogs.contains(fog)) continue;
                this.fogs.add(fog);
                grew = true;
            }
            return grew;
        }

        private static int[] merged(int[] sorted, IntOpenHashSet more) {
            if (more.isEmpty()) {
                return sorted;
            }
            int[] out = Arrays.copyOf(sorted, sorted.length + more.size());
            int at = sorted.length;
            IntIterator it = more.iterator();
            while (it.hasNext()) {
                out[at++] = it.nextInt();
            }
            Arrays.sort(out);
            return out;
        }

        boolean nonBase(int x, int y, int z) {
            int index = this.index(x, y, z);
            if (index >= 0 && Arrays.binarySearch(this.nonBase, index) >= 0) {
                return true;
            }
            for (ChunkDecoration.Fog fog : this.fogs) {
                if (!fog.contains(x, y, z)) continue;
                return true;
            }
            return false;
        }

        boolean ore(int x, int y, int z) {
            int index = this.index(x, y, z);
            if (index >= 0 && Arrays.binarySearch(this.ores, index) >= 0) {
                return true;
            }
            for (ChunkDecoration.Fog fog : this.fogs) {
                if (fog.kind() != 11 || !fog.contains(x, y, z)) continue;
                return true;
            }
            return false;
        }
    }
}

