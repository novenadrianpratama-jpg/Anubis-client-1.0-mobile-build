/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4604
 */
package net.anubis.client.esp;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongPredicate;
import java.util.function.Supplier;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.esp.EspStyle;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.util.BlockBoxHelper;
import net.anubis.client.util.BlockClusterGeometry;
import net.anubis.client.util.TracerEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4604;

@Environment(value=EnvType.CLIENT)
public final class BlockLayer {
    private static final long REVEAL_NANOS = 380000000L;
    private static final double FADE_START = 64.0;
    private static final double FADE_END = 256.0;
    private static final float FADE_FLOOR = 0.45f;
    private final Palette palette;
    private final Supplier<Object> dimension;
    private final Map<Object, World> worlds = new HashMap<Object, World>();

    public BlockLayer(Palette palette, Supplier<Object> dimension) {
        this.palette = palette;
        this.dimension = dimension;
    }

    public void put(long pos, Object kind) {
        World world = this.world(true);
        if (world == null) {
            return;
        }
        Found existing = (Found)world.positions.get(pos);
        if (existing != null && Objects.equals(existing.kind(), kind)) {
            return;
        }
        world.positions.put(pos, (Object)new Found(kind, System.nanoTime()));
        ((LongOpenHashSet)world.byChunk.computeIfAbsent(BlockLayer.chunkOf(pos), k -> new LongOpenHashSet())).add(pos);
        BlockLayer.markDirty(world, pos);
    }

    public void remove(long pos) {
        World world = this.world(false);
        if (world == null || world.positions.remove(pos) == null) {
            return;
        }
        long chunk = BlockLayer.chunkOf(pos);
        LongOpenHashSet set = (LongOpenHashSet)world.byChunk.get(chunk);
        if (set != null && set.remove(pos) && set.isEmpty()) {
            world.byChunk.remove(chunk);
        }
        BlockLayer.markDirty(world, pos);
    }

    public void clear() {
        this.worlds.clear();
    }

    public void retainChunk(long chunkKey, LongPredicate keep) {
        World world = this.world(false);
        if (world == null) {
            return;
        }
        LongOpenHashSet set = (LongOpenHashSet)world.byChunk.get(chunkKey);
        if (set == null) {
            return;
        }
        for (long pos : set.toLongArray()) {
            if (keep.test(pos)) continue;
            this.remove(pos);
        }
    }

    public List<Vein> veins() {
        World world = this.world(false);
        if (world == null) {
            return List.of();
        }
        this.rebuildDirty(world);
        if (world.veinsDirty) {
            world.veins = BlockLayer.joinVeins(world);
            world.veinsDirty = false;
        }
        return world.veins;
    }

    private World world(boolean create) {
        Object key = this.dimension.get();
        if (key == null) {
            return null;
        }
        return create ? this.worlds.computeIfAbsent(key, k -> new World()) : this.worlds.get(key);
    }

    private static long chunkKey(int cx, int cz) {
        return (long)cx & 0xFFFFFFFFL | (long)cz << 32;
    }

    private static long chunkOf(long pos) {
        return BlockLayer.chunkKey(class_2338.method_10061((long)pos) >> 4, class_2338.method_10083((long)pos) >> 4);
    }

    private static long regionOf(long chunk) {
        return BlockLayer.chunkKey((int)chunk >> 4, (int)(chunk >> 32) >> 4);
    }

    private static void markDirty(World world, long pos) {
        ChunkScanner.ScanBatch batch = ChunkScanner.deliveryBatch();
        LongOpenHashSet dirty = world.dirty;
        if (batch != null && !batch.finished) {
            if (world.scanBatch != batch) {
                BlockLayer.flushScanDirty(world);
            }
            world.scanBatch = batch;
            dirty = world.scanDirty;
        }
        int x = class_2338.method_10061((long)pos);
        int z = class_2338.method_10083((long)pos);
        int cx = x >> 4;
        int cz = z >> 4;
        dirty.add(BlockLayer.chunkKey(cx, cz));
        if ((x & 0xF) == 0) {
            dirty.add(BlockLayer.chunkKey(cx - 1, cz));
        }
        if ((x & 0xF) == 15) {
            dirty.add(BlockLayer.chunkKey(cx + 1, cz));
        }
        if ((z & 0xF) == 0) {
            dirty.add(BlockLayer.chunkKey(cx, cz - 1));
        }
        if ((z & 0xF) == 15) {
            dirty.add(BlockLayer.chunkKey(cx, cz + 1));
        }
    }

    private static void flushScanDirty(World world) {
        world.dirty.addAll((LongCollection)world.scanDirty);
        world.scanDirty.clear();
        world.scanBatch = null;
    }

    private void rebuildDirty(World world) {
        if (world.scanBatch != null && (world.scanBatch.finished || !PerformanceModule.optimizationsEnabled())) {
            BlockLayer.flushScanDirty(world);
        }
        if (world.dirty.isEmpty()) {
            return;
        }
        LongIterator longIterator = world.dirty.iterator();
        while (longIterator.hasNext()) {
            long chunk = (Long)longIterator.next();
            this.rebuildChunk(world, chunk);
        }
        world.dirty.clear();
        world.veinsDirty = true;
    }

    private void rebuildChunk(World world, long chunkKey) {
        LongOpenHashSet set = (LongOpenHashSet)world.byChunk.get(chunkKey);
        if (set == null) {
            if (world.shapes.remove(chunkKey) != null) {
                LongOpenHashSet region = (LongOpenHashSet)world.shapeRegions.get(BlockLayer.regionOf(chunkKey));
                if (region != null && region.remove(chunkKey) && region.isEmpty()) {
                    world.shapeRegions.remove(BlockLayer.regionOf(chunkKey));
                }
                ++world.shapeRevision;
            }
            return;
        }
        LinkedHashMap<Object, List> byKind = new LinkedHashMap<Object, List>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        LongIterator longIterator = set.iterator();
        while (longIterator.hasNext()) {
            long pos = (Long)longIterator.next();
            class_2338 block = class_2338.method_10092((long)pos);
            byKind.computeIfAbsent(((Found)world.positions.get(pos)).kind(), k -> new ArrayList()).add(block);
            minX = Math.min(minX, block.method_10263());
            maxX = Math.max(maxX, block.method_10263());
            minY = Math.min(minY, block.method_10264());
            maxY = Math.max(maxY, block.method_10264());
            minZ = Math.min(minZ, block.method_10260());
            maxZ = Math.max(maxZ, block.method_10260());
        }
        int cx = (int)chunkKey;
        int cz = (int)(chunkKey >> 32);
        ArrayList<Cluster> clusters = new ArrayList<Cluster>();
        for (Map.Entry entry : byKind.entrySet()) {
            Object kind = entry.getKey();
            BlockClusterGeometry.Membership same = (x, y, z) -> {
                Found found = (Found)world.positions.get(class_2338.method_10064((int)x, (int)y, (int)z));
                return found != null && Objects.equals(found.kind(), kind);
            };
            for (List<class_2338> component : BlockClusterGeometry.connectedComponents((Collection)entry.getValue())) {
                clusters.add(BlockLayer.buildCluster(world, kind, component, same, cx, cz));
            }
        }
        world.shapes.put(chunkKey, (Object)new Shapes(clusters, new class_238((double)(minX - 1), (double)(minY - 1), (double)(minZ - 1), (double)(maxX + 2), (double)(maxY + 2), (double)(maxZ + 2))));
        ((LongOpenHashSet)world.shapeRegions.computeIfAbsent(BlockLayer.regionOf(chunkKey), k -> new LongOpenHashSet())).add(chunkKey);
        ++world.shapeRevision;
    }

    private static Iterable<Shapes> nearbyShapes(World world, class_243 eye, double range) {
        if (!PerformanceModule.optimizationsEnabled()) {
            return world.shapes.values();
        }
        int cx = (int)Math.floor(eye.field_1352) >> 4;
        int cz = (int)Math.floor(eye.field_1350) >> 4;
        if (world.candidateRevision == world.shapeRevision && world.candidateX == cx && world.candidateZ == cz && world.candidateRange == range) {
            return world.candidates;
        }
        world.candidates.clear();
        int radius = (int)Math.ceil(range / 16.0) + 1;
        for (int rx = cx - radius >> 4; rx <= cx + radius >> 4; ++rx) {
            for (int rz = cz - radius >> 4; rz <= cz + radius >> 4; ++rz) {
                LongOpenHashSet region = (LongOpenHashSet)world.shapeRegions.get(BlockLayer.chunkKey(rx, rz));
                if (region == null) continue;
                LongIterator longIterator = region.iterator();
                while (longIterator.hasNext()) {
                    Shapes shape;
                    long key = (Long)longIterator.next();
                    if (Math.abs((long)((int)key) - (long)cx) > (long)radius || Math.abs((long)((int)(key >> 32)) - (long)cz) > (long)radius || (shape = (Shapes)world.shapes.get(key)) == null) continue;
                    world.candidates.add(shape);
                }
            }
        }
        world.candidateRevision = world.shapeRevision;
        world.candidateX = cx;
        world.candidateZ = cz;
        world.candidateRange = range;
        return world.candidates;
    }

    private static Cluster buildCluster(World world, Object kind, List<class_2338> component, BlockClusterGeometry.Membership same, int cx, int cz) {
        long[] positions = new long[component.size()];
        ArrayList<Long> links = new ArrayList<Long>();
        double sx = 0.0;
        double sy = 0.0;
        double sz = 0.0;
        long bornAt = Long.MAX_VALUE;
        for (int i = 0; i < positions.length; ++i) {
            class_2338 pos = component.get(i);
            positions[i] = pos.method_10063();
            sx += (double)pos.method_10263() + 0.5;
            sy += (double)pos.method_10264() + 0.5;
            sz += (double)pos.method_10260() + 0.5;
            bornAt = Math.min(bornAt, ((Found)world.positions.get(positions[i])).bornAt());
            for (class_2338 next : new class_2338[]{pos.method_10078(), pos.method_10067(), pos.method_10072(), pos.method_10095()}) {
                if (next.method_10263() >> 4 == cx && next.method_10260() >> 4 == cz || !same.contains(next.method_10263(), next.method_10264(), next.method_10260())) continue;
                links.add(next.method_10063());
            }
        }
        int n = positions.length;
        return new Cluster(kind, positions, new class_243(sx / (double)n, sy / (double)n, sz / (double)n), bornAt, BlockClusterGeometry.build(component, same), links.stream().mapToLong(Long::longValue).toArray());
    }

    private static List<Vein> joinVeins(World world) {
        ArrayList<Cluster> all = new ArrayList<Cluster>();
        Long2ObjectOpenHashMap owner = new Long2ObjectOpenHashMap();
        for (Iterator shapes : world.shapes.values()) {
            for (Cluster cluster : ((Shapes)((Object)shapes)).clusters()) {
                all.add(cluster);
                if (cluster.links().length == 0) continue;
                long[] lArray = cluster.positions();
                int n = lArray.length;
                for (int i = 0; i < n; ++i) {
                    long pos = lArray[i];
                    int lx = class_2338.method_10061((long)pos) & 0xF;
                    int lz = class_2338.method_10083((long)pos) & 0xF;
                    if (lx != 0 && lx != 15 && lz != 0 && lz != 15) continue;
                    owner.put(pos, (Object)cluster);
                }
            }
        }
        IdentityHashMap<Cluster, Cluster> parent = new IdentityHashMap<Cluster, Cluster>();
        for (Cluster cluster : all) {
            parent.put(cluster, cluster);
        }
        for (Cluster cluster : all) {
            for (long link : cluster.links()) {
                Cluster other = (Cluster)owner.get(link);
                if (other == null) continue;
                parent.put(BlockLayer.find(parent, cluster), BlockLayer.find(parent, other));
            }
        }
        IdentityHashMap<Cluster, double[]> sums = new IdentityHashMap<Cluster, double[]>();
        for (Cluster cluster : all) {
            double[] sum = sums.computeIfAbsent(BlockLayer.find(parent, cluster), r -> new double[4]);
            int n = cluster.positions().length;
            sum[0] = sum[0] + cluster.center().field_1352 * (double)n;
            sum[1] = sum[1] + cluster.center().field_1351 * (double)n;
            sum[2] = sum[2] + cluster.center().field_1350 * (double)n;
            sum[3] = sum[3] + (double)n;
        }
        ArrayList<Vein> arrayList = new ArrayList<Vein>(sums.size());
        for (Map.Entry entry : sums.entrySet()) {
            double[] s = (double[])entry.getValue();
            arrayList.add(new Vein(((Cluster)entry.getKey()).kind(), new class_243(s[0] / s[3], s[1] / s[3], s[2] / s[3])));
        }
        return arrayList;
    }

    private static Cluster find(Map<Cluster, Cluster> parent, Cluster cluster) {
        Cluster root = cluster;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        while (parent.get(cluster) != root) {
            Cluster next = parent.get(cluster);
            parent.put(cluster, root);
            cluster = next;
        }
        return root;
    }

    public void render(Render3DEvent event, EspStyle style) {
        class_310 mc = class_310.method_1551();
        World world = this.world(false);
        if (mc.field_1724 == null || world == null) {
            return;
        }
        this.rebuildDirty(world);
        class_4604 frustum = PerformanceModule.frustumCulling() ? event.frustum : null;
        long now = System.nanoTime();
        class_243 eye = mc.field_1724.method_5836(event.partialTick);
        boolean outline = style.outline();
        boolean glow = style.glow.get();
        boolean fade = style.fade.get();
        boolean reveal = style.reveal.get();
        float baseWidth = style.lineWidth.get();
        float fillAlpha = style.fillAlpha.get();
        float boxSize = style.boxSize.get();
        double range = style.range.get();
        for (Shapes shapes : BlockLayer.nearbyShapes(world, eye, range)) {
            if (frustum != null && !frustum.method_23093(shapes.bounds())) continue;
            for (Cluster cluster : shapes.clusters()) {
                int fill;
                double dist = eye.method_1022(cluster.center());
                if (dist > range) continue;
                float alpha = fade ? BlockLayer.distanceFade(dist) : 1.0f;
                float t = reveal ? Math.min(1.0f, (float)(now - cluster.bornAt()) / 3.8E8f) : 1.0f;
                if ((alpha *= BlockLayer.easeOutCubic(t)) < 0.02f) continue;
                float scale = t < 1.0f ? 0.55f + 0.45f * BlockLayer.easeOutBack(t) : 1.0f;
                float width = fade ? baseWidth * (1.0f - 0.3f * (float)Math.min(1.0, dist / 256.0)) : baseWidth;
                int color = this.palette.color(cluster.kind());
                int stroke = BlockLayer.scaleAlpha(color, alpha);
                int n = fill = fillAlpha > 0.0f ? BlockLayer.withAlpha(color, fillAlpha * alpha) : 0;
                if (outline) {
                    BlockLayer.drawOutline(cluster, stroke, fill, width, glow, scale);
                    continue;
                }
                BlockLayer.drawBoxes(cluster, stroke, fill, width, boxSize * scale);
            }
        }
    }

    private static void drawOutline(Cluster cluster, int stroke, int fill, float width, boolean glow, float scale) {
        class_243 c = cluster.center();
        BlockClusterGeometry.Mesh mesh = cluster.mesh();
        if (fill >>> 24 > 0) {
            class_12179 style = class_12179.method_75539((int)fill);
            for (BlockClusterGeometry.Face face : mesh.faces()) {
                class_12180.method_75547((class_243)BlockLayer.grow(face.a(), c, scale), (class_243)BlockLayer.grow(face.b(), c, scale), (class_243)BlockLayer.grow(face.c(), c, scale), (class_243)BlockLayer.grow(face.d(), c, scale), (class_12179)style).method_75533();
            }
        }
        int halo = glow ? BlockLayer.scaleAlpha(stroke, 0.18f) : 0;
        for (BlockClusterGeometry.Segment edge : mesh.edges()) {
            class_243 a = BlockLayer.grow(edge.a(), c, scale);
            class_243 b = BlockLayer.grow(edge.b(), c, scale);
            if (glow) {
                class_12180.method_75546((class_243)a, (class_243)b, (int)halo, (float)(width * 3.0f)).method_75533();
            }
            class_12180.method_75546((class_243)a, (class_243)b, (int)stroke, (float)width).method_75533();
        }
    }

    private static void drawBoxes(Cluster cluster, int stroke, int fill, float width, float size) {
        class_12179 style = fill >>> 24 > 0 ? class_12179.method_75537((int)stroke, (float)width, (int)fill) : class_12179.method_75536((int)stroke, (float)width);
        for (long pos : cluster.positions()) {
            class_238 bounds = BlockBoxHelper.scaledBlockBox(class_2338.method_10092((long)pos), size);
            class_12180.method_75541((class_238)bounds, (class_12179)style).method_75533();
        }
    }

    private static class_243 grow(class_243 p, class_243 c, float scale) {
        return scale == 1.0f ? p : c.method_1019(p.method_1020(c).method_1021((double)scale));
    }

    private static float distanceFade(double dist) {
        if (dist <= 64.0) {
            return 1.0f;
        }
        float t = (float)Math.min(1.0, (dist - 64.0) / 192.0);
        return 1.0f - 0.55f * t * t;
    }

    public void renderTracers(EspStyle style) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        class_243 eye = mc.field_1724.method_5836(1.0f);
        double rangeSq = (double)style.range.get() * (double)style.range.get();
        for (Vein vein : this.veins()) {
            int color;
            if (eye.method_1025(vein.center()) > rangeSq || (color = this.palette.tracerColor(vein.kind())) >>> 24 == 0) continue;
            TracerEngine.draw(vein.center(), color);
        }
    }

    private static int withAlpha(int argb, float alpha) {
        return Math.round(255.0f * Math.max(0.0f, Math.min(1.0f, alpha))) << 24 | argb & 0xFFFFFF;
    }

    private static int scaleAlpha(int argb, float mul) {
        int alpha = Math.round((float)(argb >>> 24) * Math.max(0.0f, Math.min(1.0f, mul)));
        return alpha << 24 | argb & 0xFFFFFF;
    }

    private static float easeOutCubic(float t) {
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv;
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float u = t - 1.0f;
        return 1.0f + c3 * u * u * u + c1 * u * u;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Palette {
        public int color(Object var1);

        public int tracerColor(Object var1);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class World {
        final Long2ObjectOpenHashMap<Found> positions = new Long2ObjectOpenHashMap();
        final Long2ObjectOpenHashMap<LongOpenHashSet> byChunk = new Long2ObjectOpenHashMap();
        final Long2ObjectOpenHashMap<Shapes> shapes = new Long2ObjectOpenHashMap();
        final Long2ObjectOpenHashMap<LongOpenHashSet> shapeRegions = new Long2ObjectOpenHashMap();
        final LongOpenHashSet dirty = new LongOpenHashSet();
        final LongOpenHashSet scanDirty = new LongOpenHashSet();
        ChunkScanner.ScanBatch scanBatch;
        List<Vein> veins = List.of();
        boolean veinsDirty;
        long shapeRevision;
        long candidateRevision = -1L;
        int candidateX;
        int candidateZ;
        double candidateRange;
        final List<Shapes> candidates = new ArrayList<Shapes>();

        private World() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Found(Object kind, long bornAt) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Cluster(Object kind, long[] positions, class_243 center, long bornAt, BlockClusterGeometry.Mesh mesh, long[] links) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Shapes(List<Cluster> clusters, class_238 bounds) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Vein(Object kind, class_243 center) {
    }
}

