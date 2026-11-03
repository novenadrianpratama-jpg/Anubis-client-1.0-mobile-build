/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1132
 *  net.minecraft.class_1923
 *  net.minecraft.class_2596
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_310
 *  net.minecraft.class_5218
 *  net.minecraft.class_638
 *  net.minecraft.class_642
 *  net.minecraft.class_6606
 *  net.minecraft.class_746
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.util.playerbypass;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.nio.file.Path;
import net.anubis.client.util.playerbypass.ChunkEntryGuard;
import net.anubis.client.util.playerbypass.ChunkLightSnapshot;
import net.anubis.client.util.playerbypass.DarkSectionCheck;
import net.anubis.client.util.playerbypass.LightDataClassifier;
import net.anubis.client.util.playerbypass.LightSectionStatus;
import net.anubis.client.util.playerbypass.ProtectedChunkStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1132;
import net.minecraft.class_1923;
import net.minecraft.class_2596;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_310;
import net.minecraft.class_5218;
import net.minecraft.class_638;
import net.minecraft.class_642;
import net.minecraft.class_6606;
import net.minecraft.class_746;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class PlayerBypassLightTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Long2ObjectOpenHashMap<ChunkLightSnapshot> SNAPSHOTS = new Long2ObjectOpenHashMap();
    private static final LongOpenHashSet ALERTED = new LongOpenHashSet();
    private static final LongOpenHashSet SUSPECT = new LongOpenHashSet();
    static final int SUSPECT_TURNED_DARK = 1;
    static final int SUSPECT_AIRLESS = 2;
    private static final Long2IntOpenHashMap SENT_NOT_DARK = new Long2IntOpenHashMap();
    private static final int MAX_REMEMBERED_CHUNKS = 250000;
    private static String sentPlace;
    private static final Long2ObjectOpenHashMap<ChunkLightSnapshot> HELD;
    private static final ChunkEntryGuard ENTRY_GUARD;
    private static final ProtectedChunkStore PROTECTED_CHUNKS;
    private static class_638 lastLevel;
    private static SpottedListener spottedListener;
    private static long visibilityRevision;

    private PlayerBypassLightTracker() {
    }

    public static void onTick() {
        class_310 mc = class_310.method_1551();
        PlayerBypassLightTracker.syncLevel(mc);
        class_746 player = mc.field_1724;
        if (lastLevel != null && player != null) {
            ENTRY_GUARD.update(player.method_31476().method_8324(), PlayerBypassLightTracker::isLoaded, PlayerBypassLightTracker::isFlaggedOrHeld, PROTECTED_CHUNKS);
        }
        PROTECTED_CHUNKS.tickAutosave();
    }

    public static void onPacketApplied(class_2596<?> packet) {
        class_6606 data;
        int chunkZ;
        int chunkX;
        if (packet instanceof class_2672) {
            class_2672 chunk = (class_2672)packet;
            chunkX = chunk.method_11523();
            chunkZ = chunk.method_11524();
            data = chunk.method_38599();
        } else if (packet instanceof class_2676) {
            class_2676 light = (class_2676)packet;
            chunkX = light.method_11558();
            chunkZ = light.method_11554();
            data = light.method_38600();
        } else {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        PlayerBypassLightTracker.syncLevel(mc);
        if (level == null || data == null) {
            return;
        }
        if (mc.field_1724 != null && mc.field_1724.method_23318() < 0.0) {
            PlayerBypassLightTracker.hold(chunkX, chunkZ, data, level.method_32890(), level.method_32891());
        } else {
            PlayerBypassLightTracker.ingest(chunkX, chunkZ, data, level.method_32890(), level.method_32891());
        }
        PlayerBypassLightTracker.rememberNotDark(chunkX, chunkZ, data, level.method_32890(), level.method_32891());
        ++visibilityRevision;
    }

    private static void rememberNotDark(int chunkX, int chunkZ, class_6606 data, int sectionCount, int bottomSectionY) {
        int bits = LightDataClassifier.sentNotDark(data, sectionCount, bottomSectionY);
        if (bits == 0) {
            return;
        }
        if (SENT_NOT_DARK.size() >= 250000) {
            SENT_NOT_DARK.clear();
        }
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        SENT_NOT_DARK.put(key, SENT_NOT_DARK.get(key) | bits);
    }

    private static void syncLevel(class_310 mc) {
        String dimension;
        class_638 level = mc.field_1687;
        if (level == lastLevel) {
            return;
        }
        ++visibilityRevision;
        SNAPSHOTS.clear();
        ALERTED.clear();
        SUSPECT.clear();
        HELD.clear();
        ENTRY_GUARD.reset();
        lastLevel = level;
        PROTECTED_CHUNKS.flush();
        if (level == null) {
            PROTECTED_CHUNKS.select(null, null);
            return;
        }
        String world = PlayerBypassLightTracker.worldKey(mc);
        String place = world + "\n" + (dimension = level.method_27983().method_29177().toString());
        if (!place.equals(sentPlace)) {
            SENT_NOT_DARK.clear();
            sentPlace = place;
        }
        PROTECTED_CHUNKS.select(world, dimension);
    }

    private static String worldKey(class_310 mc) {
        class_1132 local = mc.method_1576();
        if (local != null) {
            Path folder = local.method_27050(class_5218.field_24188).normalize().getFileName();
            return ProtectedChunkStore.singleplayerKey(folder == null ? "world" : folder.toString());
        }
        class_642 server = mc.method_1558();
        return ProtectedChunkStore.serverKey(server == null ? null : server.field_3761);
    }

    private static void ingest(int chunkX, int chunkZ, class_6606 data, int sectionCount, int bottomSectionY) {
        int suspect;
        ChunkLightSnapshot snapshot;
        boolean wasFlagged;
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        if (HELD.containsKey(key)) {
            PlayerBypassLightTracker.hold(chunkX, chunkZ, data, sectionCount, bottomSectionY);
        }
        boolean bl = wasFlagged = (snapshot = (ChunkLightSnapshot)SNAPSHOTS.get(key)) != null && PlayerBypassLightTracker.isFlagged(key, snapshot);
        if (snapshot == null || snapshot.sectionCount != sectionCount || snapshot.bottomSectionY != bottomSectionY) {
            snapshot = new ChunkLightSnapshot(sectionCount, bottomSectionY);
            SNAPSHOTS.put(key, (Object)snapshot);
        }
        try {
            LightDataClassifier.apply(data, snapshot);
        }
        catch (Throwable t) {
            LOGGER.error("Player Bypass light ingest failed for chunk {}, {}", new Object[]{chunkX, chunkZ, t});
            return;
        }
        boolean flagged = snapshot.hasBaseSignal();
        if (flagged && !PROTECTED_CHUNKS.flagAllowed(key, wasFlagged)) {
            SNAPSHOTS.remove(key);
            return;
        }
        int n = suspect = flagged ? PlayerBypassLightTracker.suspicion(key, chunkX, chunkZ, snapshot) : 0;
        if (flagged && suspect != 0) {
            SUSPECT.add(key);
        } else if (flagged) {
            SUSPECT.remove(key);
            if (spottedListener != null && ALERTED.add(key)) {
                spottedListener.onChunkSpotted(chunkX, chunkZ);
            }
        } else if (!ALERTED.contains(key)) {
            SNAPSHOTS.remove(key);
            SUSPECT.remove(key);
        }
    }

    private static int suspicion(long key, int chunkX, int chunkZ, ChunkLightSnapshot snapshot) {
        int sentNotDark = SENT_NOT_DARK.get(key);
        int why = 0;
        for (int i = 1; i < snapshot.sectionCount + 1; ++i) {
            if (!ChunkLightSnapshot.candidate(i, snapshot.bottomSectionY) || snapshot.sky[i] != LightSectionStatus.ZEROED_PRESENT) continue;
            if (i < 32 && (sentNotDark >>> i & 1) != 0) {
                why |= 1;
                continue;
            }
            if (lastLevel != null && DarkSectionCheck.airlessAround(lastLevel, chunkX, snapshot.bottomSectionY + i - 1, chunkZ)) {
                why |= 2;
                continue;
            }
            return 0;
        }
        return why;
    }

    private static void hold(int chunkX, int chunkZ, class_6606 data, int sectionCount, int bottomSectionY) {
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        ChunkLightSnapshot snapshot = (ChunkLightSnapshot)HELD.get(key);
        if (snapshot == null || snapshot.sectionCount != sectionCount || snapshot.bottomSectionY != bottomSectionY) {
            snapshot = new ChunkLightSnapshot(sectionCount, bottomSectionY);
        }
        try {
            LightDataClassifier.apply(data, snapshot);
        }
        catch (Throwable t) {
            LOGGER.error("Player Bypass light hold failed for chunk {}, {}", new Object[]{chunkX, chunkZ, t});
            return;
        }
        if (snapshot.hasBaseSignal() && PROTECTED_CHUNKS.flagAllowed(key, false)) {
            HELD.put(key, (Object)snapshot);
        } else {
            HELD.remove(key);
        }
    }

    public static void setSpottedListener(SpottedListener listener) {
        spottedListener = listener;
    }

    public static Long2ObjectMap<ChunkLightSnapshot> snapshots() {
        return SNAPSHOTS;
    }

    public static long visibilityRevision() {
        return visibilityRevision;
    }

    public static boolean isSuspect(long chunkKey) {
        return SUSPECT.contains(chunkKey);
    }

    public static boolean isFlagged(long chunkKey, ChunkLightSnapshot snapshot) {
        return ALERTED.contains(chunkKey) || snapshot.hasBaseSignal();
    }

    public static boolean isFlagged(long chunkKey) {
        ChunkLightSnapshot snapshot = (ChunkLightSnapshot)SNAPSHOTS.get(chunkKey);
        return snapshot != null && PlayerBypassLightTracker.isFlagged(chunkKey, snapshot);
    }

    private static boolean isFlaggedOrHeld(long chunkKey) {
        return PlayerBypassLightTracker.isFlagged(chunkKey) || HELD.containsKey(chunkKey);
    }

    private static boolean isLoaded(long chunkKey) {
        return lastLevel != null && lastLevel.method_2935().method_12123(class_1923.method_8325((long)chunkKey), class_1923.method_8332((long)chunkKey));
    }

    static {
        HELD = new Long2ObjectOpenHashMap();
        ENTRY_GUARD = new ChunkEntryGuard();
        PROTECTED_CHUNKS = ProtectedChunkStore.get();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface SpottedListener {
        public void onChunkSpotted(int var1, int var2);
    }
}

