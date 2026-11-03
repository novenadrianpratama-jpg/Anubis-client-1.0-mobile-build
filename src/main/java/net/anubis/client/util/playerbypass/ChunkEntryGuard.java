/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.playerbypass;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.function.LongPredicate;
import net.anubis.client.util.playerbypass.ProtectedChunkStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class ChunkEntryGuard {
    private final LongOpenHashSet waiting = new LongOpenHashSet();
    private boolean inChunk;
    private long chunk;
    private boolean entryPending;

    ChunkEntryGuard() {
    }

    void reset() {
        this.inChunk = false;
        this.entryPending = false;
        this.waiting.clear();
    }

    void update(long playerChunk, LongPredicate arrived, LongPredicate flagged, ProtectedChunkStore store) {
        if (!this.inChunk || playerChunk != this.chunk) {
            this.inChunk = true;
            this.chunk = playerChunk;
            this.entryPending = true;
            this.waiting.clear();
        }
        if (this.entryPending) {
            if (!arrived.test(playerChunk)) {
                return;
            }
            this.entryPending = false;
            if (flagged.test(playerChunk)) {
                return;
            }
            ProtectedChunkStore.forEachInArea(ProtectedChunkStore.chunkX(playerChunk), ProtectedChunkStore.chunkZ(playerChunk), key -> {
                if (!arrived.test(key)) {
                    this.waiting.add(key);
                } else if (!flagged.test(key)) {
                    store.protect(key);
                }
            });
            return;
        }
        if (this.waiting.isEmpty()) {
            return;
        }
        LongIterator keys = this.waiting.iterator();
        while (keys.hasNext()) {
            long key2 = keys.nextLong();
            if (!arrived.test(key2)) continue;
            keys.remove();
            if (flagged.test(key2)) continue;
            store.protect(key2);
        }
    }
}

