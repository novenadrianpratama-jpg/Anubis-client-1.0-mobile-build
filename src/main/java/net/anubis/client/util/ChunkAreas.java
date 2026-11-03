/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_243
 */
package net.anubis.client.util;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.anubis.client.util.TracerEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_243;

@Environment(value=EnvType.CLIENT)
public final class ChunkAreas {
    private final DoubleArrayList areaX = new DoubleArrayList();
    private final DoubleArrayList areaZ = new DoubleArrayList();
    private int areasSize = -1;
    private int areasHash;
    private final LongOpenHashSet seen = new LongOpenHashSet();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final LongArrayList area = new LongArrayList();

    public void drawTracers(LongSet chunks, double y, int color) {
        if (chunks.isEmpty()) {
            return;
        }
        this.find(chunks);
        for (int i = 0; i < this.areaX.size(); ++i) {
            TracerEngine.draw(new class_243(this.areaX.getDouble(i), y, this.areaZ.getDouble(i)), color);
        }
    }

    private void find(LongSet chunks) {
        int hash = chunks.hashCode();
        if (chunks.size() == this.areasSize && hash == this.areasHash) {
            return;
        }
        this.areasSize = chunks.size();
        this.areasHash = hash;
        this.areaX.clear();
        this.areaZ.clear();
        this.seen.clear();
        LongIterator longIterator = chunks.iterator();
        while (longIterator.hasNext()) {
            long start = (Long)longIterator.next();
            if (!this.seen.add(start)) continue;
            this.area.clear();
            this.queue.enqueue(start);
            double sumX = 0.0;
            double sumZ = 0.0;
            while (!this.queue.isEmpty()) {
                long key = this.queue.dequeueLong();
                this.area.add(key);
                int cx = class_1923.method_8325((long)key);
                int cz = class_1923.method_8332((long)key);
                sumX += (double)(cx << 4) + 8.0;
                sumZ += (double)(cz << 4) + 8.0;
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        long next = class_1923.method_8331((int)(cx + dx), (int)(cz + dz));
                        if (!chunks.contains(next) || !this.seen.add(next)) continue;
                        this.queue.enqueue(next);
                    }
                }
            }
            double mx = sumX / (double)this.area.size();
            double mz = sumZ / (double)this.area.size();
            if (!chunks.contains(class_1923.method_8331((int)((int)Math.floor(mx) >> 4), (int)((int)Math.floor(mz) >> 4)))) {
                double best = Double.MAX_VALUE;
                double bx = mx;
                double bz = mz;
                for (int i = 0; i < this.area.size(); ++i) {
                    double z;
                    long key = this.area.getLong(i);
                    double x = (double)(class_1923.method_8325((long)key) << 4) + 8.0;
                    double d = (x - mx) * (x - mx) + ((z = (double)(class_1923.method_8332((long)key) << 4) + 8.0) - mz) * (z - mz);
                    if (!(d < best)) continue;
                    best = d;
                    bx = x;
                    bz = z;
                }
                mx = bx;
                mz = bz;
            }
            this.areaX.add(mx);
            this.areaZ.add(mz);
        }
    }
}

