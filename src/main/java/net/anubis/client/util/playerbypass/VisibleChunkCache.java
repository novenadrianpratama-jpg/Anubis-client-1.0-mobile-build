/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.playerbypass;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.function.LongPredicate;
import net.anubis.client.util.playerbypass.ChunkLightSnapshot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class VisibleChunkCache {
    private final int radius;
    private final LongOpenHashSet bases = new LongOpenHashSet();
    private final LongOpenHashSet suspects = new LongOpenHashSet();
    private final LongOpenHashSet nextBases = new LongOpenHashSet();
    private final LongOpenHashSet nextSuspects = new LongOpenHashSet();
    private boolean valid;
    private long revision;
    private int centerX;
    private int centerZ;

    public VisibleChunkCache(int radius) {
        if (radius < 0 || radius > 512) {
            throw new IllegalArgumentException("Invalid chunk radius");
        }
        this.radius = radius;
    }

    public boolean update(long revision, int centerX, int centerZ, Long2ObjectMap<ChunkLightSnapshot> snapshots, Flagged flagged, LongPredicate suspect) {
        boolean changed;
        if (this.valid && this.revision == revision && this.centerX == centerX && this.centerZ == centerZ) {
            return false;
        }
        this.nextBases.clear();
        this.nextSuspects.clear();
        int side = this.radius * 2 + 1;
        if (snapshots.size() <= side * side) {
            for (Long2ObjectMap.Entry entry : snapshots.long2ObjectEntrySet()) {
                long key = entry.getLongKey();
                if (Math.abs((long)((int)key) - (long)centerX) > (long)this.radius || Math.abs((long)((int)(key >>> 32)) - (long)centerZ) > (long)this.radius) continue;
                this.accept(key, (ChunkLightSnapshot)entry.getValue(), flagged, suspect);
            }
        } else {
            for (int dx = -this.radius; dx <= this.radius; ++dx) {
                for (int dz = -this.radius; dz <= this.radius; ++dz) {
                    long key = (long)(centerX + dx) & 0xFFFFFFFFL | (long)(centerZ + dz) << 32;
                    ChunkLightSnapshot snapshot = (ChunkLightSnapshot)snapshots.get(key);
                    if (snapshot == null) continue;
                    this.accept(key, snapshot, flagged, suspect);
                }
            }
        }
        boolean bl = changed = !this.valid || !this.bases.equals((Object)this.nextBases) || !this.suspects.equals((Object)this.nextSuspects);
        if (changed) {
            this.bases.clear();
            this.bases.addAll((LongCollection)this.nextBases);
            this.suspects.clear();
            this.suspects.addAll((LongCollection)this.nextSuspects);
        }
        this.revision = revision;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.valid = true;
        return changed;
    }

    private void accept(long key, ChunkLightSnapshot snapshot, Flagged flagged, LongPredicate suspect) {
        if (flagged.test(key, snapshot)) {
            (suspect.test(key) ? this.nextSuspects : this.nextBases).add(key);
        }
    }

    public LongOpenHashSet bases() {
        return this.bases;
    }

    public LongOpenHashSet suspects() {
        return this.suspects;
    }

    public void invalidate() {
        this.valid = false;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Flagged {
        public boolean test(long var1, ChunkLightSnapshot var3);
    }
}

