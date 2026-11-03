/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_9848
 */
package net.anubis.client.util;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.anubis.client.util.FoglessLines;
import net.anubis.client.util.WorldQuads;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class ChunkOverlay {
    public static final double MARKER_Y = 63.0;
    private static final float EDGE_SHADE = 0.77f;
    private static final float LINE_WIDTH = 1.5f;
    private static final double CORNER_RADIUS = 2.0;
    private static final int ARC_SEGMENTS = 4;
    private static final float GROW_SECONDS = 0.3f;
    private static final float SHRINK_SECONDS = 0.25f;
    private static final float SETTLE_SECONDS = 0.25f;
    private static final float MAX_WAIT_SECONDS = 1.0f;
    private static final float RESET_GAP_SECONDS = 0.5f;
    private final Long2FloatOpenHashMap shown = new Long2FloatOpenHashMap();
    private final LongOpenHashSet pending = new LongOpenHashSet();
    private final LongOpenHashSet done = new LongOpenHashSet();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final LongArrayList group = new LongArrayList();
    private long lastNanos;
    private long pendingSince;
    private long lastAdded;
    private static final double[] OUTLINE = new double[40];
    private static int points;

    public void draw(LongSet chunks, int fill, float partialTick) {
        long now = System.nanoTime();
        float dt = this.lastNanos == 0L ? 0.0f : (float)(now - this.lastNanos) / 1.0E9f;
        this.lastNanos = now;
        if (dt > 0.5f) {
            this.shown.clear();
            this.pending.clear();
        }
        this.collect(chunks, now);
        this.step(chunks, dt);
        if (this.shown.isEmpty()) {
            return;
        }
        int edge = class_9848.method_61330((int)Math.min(255, class_9848.method_61320((int)fill) * 3 / 2), (int)class_9848.method_61321((int)fill, (float)0.77f));
        FoglessLines lines = FoglessLines.forFrame(partialTick);
        this.done.clear();
        LongIterator keys = this.shown.keySet().iterator();
        while (keys.hasNext()) {
            long start = keys.nextLong();
            if (!this.done.add(start)) continue;
            this.drawGroup(lines, chunks, start, fill, edge);
        }
    }

    public void drawTiles(LongSet chunks, int fill, int edge, float partialTick) {
        long now = System.nanoTime();
        float dt = this.lastNanos == 0L ? 0.0f : (float)(now - this.lastNanos) / 1.0E9f;
        this.lastNanos = now;
        if (dt > 0.5f) {
            this.shown.clear();
        }
        LongIterator keys = chunks.iterator();
        while (keys.hasNext()) {
            this.shown.putIfAbsent(keys.nextLong(), 0.0f);
        }
        this.step(chunks, dt);
        if (this.shown.isEmpty()) {
            return;
        }
        FoglessLines lines = FoglessLines.forFrame(partialTick);
        ObjectIterator entries = this.shown.long2FloatEntrySet().fastIterator();
        while (entries.hasNext()) {
            Long2FloatMap.Entry entry = (Long2FloatMap.Entry)entries.next();
            float size = ChunkOverlay.easeOut(entry.getFloatValue());
            if (size <= 0.001f) continue;
            long key = entry.getLongKey();
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            if (!this.settled(chunks, key)) {
                double half = 8.0 * (double)size;
                double mx = (double)(cx << 4) + 8.0;
                double mz = (double)(cz << 4) + 8.0;
                ChunkOverlay.tile(lines, mx - half, mz - half, mx + half, mz + half, 2.0 * (double)size, true, true, true, true, ChunkOverlay.fade(fill, size), ChunkOverlay.fade(edge, size));
                continue;
            }
            double x0 = cx << 4;
            double z0 = cz << 4;
            double x1 = x0 + 16.0;
            double z1 = z0 + 16.0;
            boolean north = !this.settled(chunks, class_1923.method_8331((int)cx, (int)(cz - 1)));
            boolean south = !this.settled(chunks, class_1923.method_8331((int)cx, (int)(cz + 1)));
            boolean west = !this.settled(chunks, class_1923.method_8331((int)(cx - 1), (int)cz));
            boolean east = !this.settled(chunks, class_1923.method_8331((int)(cx + 1), (int)cz));
            ChunkOverlay.tile(lines, x0, z0, x1, z1, 2.0, north, south, west, east, fill, edge);
        }
    }

    private boolean settled(LongSet chunks, long key) {
        return this.shown.get(key) >= 1.0f && chunks.contains(key);
    }

    private void collect(LongSet chunks, long now) {
        LongIterator keys = chunks.iterator();
        while (keys.hasNext()) {
            long key = keys.nextLong();
            if (this.shown.containsKey(key) || this.pending.contains(key)) continue;
            if (this.pending.isEmpty()) {
                this.pendingSince = now;
            }
            this.pending.add(key);
            this.lastAdded = now;
        }
        keys = this.pending.iterator();
        while (keys.hasNext()) {
            if (chunks.contains(keys.nextLong())) continue;
            keys.remove();
        }
        if (this.pending.isEmpty()) {
            return;
        }
        if ((float)(now - this.lastAdded) / 1.0E9f < 0.25f && (float)(now - this.pendingSince) / 1.0E9f < 1.0f) {
            return;
        }
        keys = this.pending.iterator();
        while (keys.hasNext()) {
            this.shown.put(keys.nextLong(), 0.0f);
        }
        this.pending.clear();
    }

    private void step(LongSet chunks, float dt) {
        ObjectIterator entries = this.shown.long2FloatEntrySet().fastIterator();
        while (entries.hasNext()) {
            Long2FloatMap.Entry entry = (Long2FloatMap.Entry)entries.next();
            if (chunks.contains(entry.getLongKey())) {
                entry.setValue(Math.min(1.0f, entry.getFloatValue() + dt / 0.3f));
                continue;
            }
            float left = entry.getFloatValue() - dt / 0.25f;
            if (left <= 0.0f) {
                entries.remove();
                continue;
            }
            entry.setValue(left);
        }
    }

    private void drawGroup(FoglessLines lines, LongSet chunks, long start, int fill, int edge) {
        float progress = this.shown.get(start);
        boolean present = chunks.contains(start);
        this.group.clear();
        this.queue.enqueue(start);
        double sumX = 0.0;
        double sumZ = 0.0;
        while (!this.queue.isEmpty()) {
            long key = this.queue.dequeueLong();
            this.group.add(key);
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            sumX += (double)(cx << 4) + 8.0;
            sumZ += (double)(cz << 4) + 8.0;
            this.visit(chunks, class_1923.method_8331((int)cx, (int)(cz - 1)), progress, present);
            this.visit(chunks, class_1923.method_8331((int)cx, (int)(cz + 1)), progress, present);
            this.visit(chunks, class_1923.method_8331((int)(cx - 1), (int)cz), progress, present);
            this.visit(chunks, class_1923.method_8331((int)(cx + 1), (int)cz), progress, present);
        }
        float size = ChunkOverlay.easeOut(progress);
        if (size <= 0.001f) {
            return;
        }
        double mx = sumX / (double)this.group.size();
        double mz = sumZ / (double)this.group.size();
        int groupFill = size >= 1.0f ? fill : ChunkOverlay.fade(fill, size);
        int groupEdge = size >= 1.0f ? edge : ChunkOverlay.fade(edge, size);
        for (int i = 0; i < this.group.size(); ++i) {
            long key = this.group.getLong(i);
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            double x0 = mx + ((double)(cx << 4) - mx) * (double)size;
            double z0 = mz + ((double)(cz << 4) - mz) * (double)size;
            double x1 = x0 + 16.0 * (double)size;
            double z1 = z0 + 16.0 * (double)size;
            boolean north = !this.joined(chunks, class_1923.method_8331((int)cx, (int)(cz - 1)), progress, present);
            boolean south = !this.joined(chunks, class_1923.method_8331((int)cx, (int)(cz + 1)), progress, present);
            boolean west = !this.joined(chunks, class_1923.method_8331((int)(cx - 1), (int)cz), progress, present);
            boolean east = !this.joined(chunks, class_1923.method_8331((int)(cx + 1), (int)cz), progress, present);
            ChunkOverlay.tile(lines, x0, z0, x1, z1, 2.0 * (double)size, north, south, west, east, groupFill, groupEdge);
        }
    }

    private void visit(LongSet chunks, long key, float progress, boolean present) {
        if (this.joined(chunks, key, progress, present) && this.done.add(key)) {
            this.queue.enqueue(key);
        }
    }

    private boolean joined(LongSet chunks, long key, float progress, boolean present) {
        return this.shown.containsKey(key) && this.shown.get(key) == progress && chunks.contains(key) == present;
    }

    private static void tile(FoglessLines lines, double x0, double z0, double x1, double z1, double radius, boolean north, boolean south, boolean west, boolean east, int fill, int edge) {
        double nw = north && west ? radius : 0.0;
        double ne = north && east ? radius : 0.0;
        double se = south && east ? radius : 0.0;
        double sw = south && west ? radius : 0.0;
        points = 0;
        ChunkOverlay.corner(lines, x0 + nw, z0 + nw, nw, 180.0, edge);
        ChunkOverlay.corner(lines, x1 - ne, z0 + ne, ne, 270.0, edge);
        ChunkOverlay.corner(lines, x1 - se, z1 - se, se, 0.0, edge);
        ChunkOverlay.corner(lines, x0 + sw, z1 - sw, sw, 90.0, edge);
        WorldQuads top = WorldQuads.layer(true);
        double y = 63.0;
        int i = 1;
        while (i + 1 < points) {
            top.quad(OUTLINE[0], y, OUTLINE[1], OUTLINE[i * 2], y, OUTLINE[i * 2 + 1], OUTLINE[i * 2 + 2], y, OUTLINE[i * 2 + 3], OUTLINE[i * 2 + 2], y, OUTLINE[i * 2 + 3], fill);
            ++i;
        }
        if (north) {
            ChunkOverlay.edge(lines, x0 + nw, z0, x1 - ne, z0, edge);
        }
        if (south) {
            ChunkOverlay.edge(lines, x0 + sw, z1, x1 - se, z1, edge);
        }
        if (west) {
            ChunkOverlay.edge(lines, x0, z0 + nw, x0, z1 - sw, edge);
        }
        if (east) {
            ChunkOverlay.edge(lines, x1, z0 + ne, x1, z1 - se, edge);
        }
    }

    private static void corner(FoglessLines lines, double cx, double cz, double radius, double startDegrees, int edge) {
        if (radius <= 0.0) {
            ChunkOverlay.point(cx, cz);
            return;
        }
        double previousX = 0.0;
        double previousZ = 0.0;
        for (int i = 0; i <= 4; ++i) {
            double angle = Math.toRadians(startDegrees + 90.0 * (double)i / 4.0);
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            ChunkOverlay.point(x, z);
            if (i > 0) {
                lines.line(previousX, 63.0, previousZ, x, 63.0, z, edge, 1.5f);
            }
            previousX = x;
            previousZ = z;
        }
    }

    private static void point(double x, double z) {
        ChunkOverlay.OUTLINE[ChunkOverlay.points * 2] = x;
        ChunkOverlay.OUTLINE[ChunkOverlay.points * 2 + 1] = z;
        ++points;
    }

    private static float easeOut(float t) {
        float inv = 1.0f - t;
        return 1.0f - inv * inv * inv;
    }

    private static int fade(int color, float amount) {
        return class_9848.method_61330((int)Math.round((float)class_9848.method_61320((int)color) * amount), (int)color);
    }

    private static void edge(FoglessLines lines, double ax, double az, double bx, double bz, int color) {
        lines.line(ax, 63.0, az, bx, 63.0, bz, color, 1.5f);
    }
}

