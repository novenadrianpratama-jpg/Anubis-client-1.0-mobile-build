/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2DoubleMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10264
 *  net.minecraft.class_10583
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1309
 *  net.minecraft.class_1311
 *  net.minecraft.class_1314
 *  net.minecraft.class_1540
 *  net.minecraft.class_1542
 *  net.minecraft.class_1606
 *  net.minecraft.class_1657
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2604
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2663
 *  net.minecraft.class_2680
 *  net.minecraft.class_2684
 *  net.minecraft.class_2726
 *  net.minecraft.class_2739
 *  net.minecraft.class_2741
 *  net.minecraft.class_2744
 *  net.minecraft.class_2769
 *  net.minecraft.class_2775
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_5321
 *  net.minecraft.class_5955
 *  net.minecraft.class_6025
 *  net.minecraft.class_638
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_8111
 *  net.minecraft.class_8143
 */
package net.anubis.client.util.playerbypass2;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10264;
import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1311;
import net.minecraft.class_1314;
import net.minecraft.class_1540;
import net.minecraft.class_1542;
import net.minecraft.class_1606;
import net.minecraft.class_1657;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2604;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2663;
import net.minecraft.class_2680;
import net.minecraft.class_2684;
import net.minecraft.class_2726;
import net.minecraft.class_2739;
import net.minecraft.class_2741;
import net.minecraft.class_2744;
import net.minecraft.class_2769;
import net.minecraft.class_2775;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_5321;
import net.minecraft.class_5955;
import net.minecraft.class_6025;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_8111;
import net.minecraft.class_8143;

@Environment(value=EnvType.CLIENT)
public final class SimulationSignals {
    private static final int RECENT = 6;
    private static final long SECOND = 1000000000L;
    private static final long CHUNK_SETTLE = 3000000000L;
    private static final long FRESH_ENTITY = 2000000000L;
    private static final long WANDER_MEMORY = 60000000000L;
    private static final long TELEPORT_QUIET = 2000000000L;
    private static final long LIGHTNING_LIFE = 3000000000L;
    private static final long UNDERGROUND_HOLD = 3000000000L;
    private static final long FROZEN_AFTER = 40000000000L;
    private static final int MAX_PINS = 32;
    private static final double MIN_MOVE_SQR = 1.0E-4;
    private static final double TELEPORT_JUMP_SQR = 64.0;
    private static final double SAMPLE_JUMP_SQR = 900.0;
    private static final double TRACKING_INSET = 24.0;
    private static final double TRACKED_DECAY = 0.5;
    private static final float FORGET_SCORE = 0.05f;
    private static final float WEAK_CAP = 4.0f;
    private static final float STRONG_CAP = 10.0f;
    private static final int MIN_Y = 0;
    private static final int BLOCK_SPILL = 1;
    private static final Set<class_2248> GROWS_FROM_AIR = Set.of(class_2246.field_10424, class_2246.field_10029, class_2246.field_10211, class_2246.field_10108, class_2246.field_46283, class_2246.field_46282, class_2246.field_10477, class_2246.field_10036, class_2246.field_22089, class_2246.field_10251, class_2246.field_10559, class_2246.field_10021, class_2246.field_10528);
    private static final Set<class_2248> GONE_TO_AIR = Set.of(class_2246.field_10477, class_2246.field_10036, class_2246.field_22089, class_2246.field_10195, class_2246.field_43230, class_2246.field_37575);
    private static boolean active;
    private static int serverSim;
    private static int serverView;
    private static int simOverride;
    private static int margin;
    private static boolean ignoreVisiblePlayers;
    private static long memoryNanos;
    private static int ticks;
    private static int session;
    private static class_5321<class_1937> dimension;
    private static long quietUntil;
    private static long undergroundUntil;
    private static double lastX;
    private static double lastZ;
    private static double sampleX;
    private static double sampleZ;
    private static boolean hasLast;
    private static boolean hasSample;
    private static long passedCenter;
    private static int passedRadius;
    private static final Long2ObjectOpenHashMap<ChunkSignal> CHUNKS;
    private static final Int2ObjectOpenHashMap<Share> ENTITY_SHARES;
    private static final Long2ObjectOpenHashMap<Share> BLOCK_SHARES;
    private static final Long2LongOpenHashMap CHUNK_LOADED;
    private static final Int2LongOpenHashMap ENTITY_ADDED;
    private static final Int2LongOpenHashMap ENTITY_INSIDE;
    private static final Map<class_5321<class_1937>, LongOpenHashSet> PASSED_BY_DIMENSION;
    private static LongOpenHashSet passed;
    private static final Int2DoubleOpenHashMap TRACKED_RANGE;
    private static final Int2ObjectOpenHashMap<long[]> LIGHTNING_PENDING;
    private static final Int2LongOpenHashMap LAST_ACTIVE;
    private static final LongOpenHashSet FROZEN;
    private static final ArrayDeque<Pin> PINS;

    private SimulationSignals() {
    }

    public static void configure(boolean on, int simulationOverride, int ringMargin, boolean ignorePlayers, int memorySeconds) {
        if (active && !on) {
            SimulationSignals.clear();
        }
        active = on;
        simOverride = simulationOverride;
        margin = ringMargin;
        ignoreVisiblePlayers = ignorePlayers;
        memoryNanos = (long)Math.max(1, memorySeconds) * 1000000000L;
    }

    public static void clear() {
        SimulationSignals.resetLevel();
        PASSED_BY_DIMENSION.clear();
        passed = new LongOpenHashSet();
        if (dimension != null) {
            PASSED_BY_DIMENSION.put(dimension, passed);
        }
    }

    private static void resetLevel() {
        CHUNKS.clear();
        ENTITY_SHARES.clear();
        BLOCK_SHARES.clear();
        ENTITY_ADDED.clear();
        ENTITY_INSIDE.clear();
        TRACKED_RANGE.clear();
        LIGHTNING_PENDING.clear();
        LAST_ACTIVE.clear();
        FROZEN.clear();
        PINS.clear();
        hasLast = false;
        hasSample = false;
        passedCenter = Long.MIN_VALUE;
        passedRadius = -1;
    }

    public static int session() {
        return session;
    }

    public static int serverView() {
        return serverView;
    }

    public static int simulation() {
        int sim = simOverride > 0 ? simOverride : (serverSim > 0 ? serverSim : 5);
        return Math.max(1, Math.min(sim, SimulationSignals.view() - 3 - margin));
    }

    public static int view() {
        int option = class_310.method_1551().field_1690.method_38521();
        return serverView > 0 ? Math.min(serverView, option) : option;
    }

    public static int ringStart() {
        return SimulationSignals.simulation() + 2 + margin;
    }

    public static Long2ObjectMap<ChunkSignal> chunks() {
        return CHUNKS;
    }

    public static LongOpenHashSet frozen() {
        return FROZEN;
    }

    public static Pin pollPin() {
        return PINS.poll();
    }

    public static void dropPins() {
        PINS.clear();
    }

    public static boolean holding() {
        class_746 self = class_310.method_1551().field_1724;
        return SimulationSignals.now() < undergroundUntil || self != null && self.method_23318() < 0.0;
    }

    public static long now() {
        return System.nanoTime();
    }

    static float decay(long elapsed) {
        return (float)Math.exp(-((double)elapsed) / (double)memoryNanos);
    }

    public static void onLogin(int simulationDistance, int chunkRadius, class_5321<class_1937> level) {
        serverSim = simulationDistance;
        serverView = chunkRadius;
        ++session;
        dimension = level;
        CHUNK_LOADED.clear();
        SimulationSignals.clear();
        SimulationSignals.startQuiet();
    }

    public static void onRespawn(class_5321<class_1937> level) {
        SimulationSignals.startQuiet();
        if (level.equals(dimension)) {
            return;
        }
        dimension = level;
        CHUNK_LOADED.clear();
        SimulationSignals.resetLevel();
        passed = PASSED_BY_DIMENSION.computeIfAbsent(level, key -> new LongOpenHashSet());
    }

    public static void onTeleport() {
        SimulationSignals.startQuiet();
    }

    public static void onSimulationDistance(int distance) {
        serverSim = distance;
    }

    public static void onViewDistance(int distance) {
        serverView = distance;
    }

    public static void onChunk(int x, int z) {
        if (!active) {
            return;
        }
        CHUNK_LOADED.put(class_1923.method_8331((int)x, (int)z), SimulationSignals.now());
    }

    public static void onMove(class_2684 packet) {
        boolean moved;
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_1297 entity = packet.method_11645((class_1937)level);
        if (entity == null) {
            return;
        }
        boolean bl = moved = packet.method_22826() && (packet.method_36150() != 0 || packet.method_36151() != 0 || packet.method_36152() != 0);
        if (moved || packet.method_11652()) {
            LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        }
        if (moved) {
            SimulationSignals.entitySignal(entity, Kind.MOVE, 0);
        } else if (packet.method_11652()) {
            SimulationSignals.entitySignal(entity, Kind.HEAD, 0);
        }
    }

    public static void onPositionSync(class_10264 packet) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_1297 entity = level.method_8469(packet.comp_3223());
        if (!(entity instanceof class_1309)) {
            return;
        }
        if (entity.method_73189().method_1025(packet.comp_3224().comp_3148()) < 1.0E-4) {
            return;
        }
        LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(entity, Kind.MOVE, 0);
    }

    public static void onHeadRotation(class_2726 packet) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_1297 entity = packet.method_11786((class_1937)level);
        if (entity == null) {
            return;
        }
        LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(entity, Kind.HEAD, 0);
    }

    public static void onEntityData(class_2739 packet) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        if (SimulationSignals.now() - ENTITY_ADDED.getOrDefault(packet.comp_1127(), -4611686018427387904L) < 2000000000L) {
            return;
        }
        class_1297 entity = level.method_8469(packet.comp_1127());
        if (entity == null) {
            return;
        }
        LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(entity, Kind.STATE, 0);
    }

    public static void onEntityEvent(class_2663 packet) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_1297 entity = packet.method_11469((class_1937)level);
        if (entity == null) {
            return;
        }
        LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(entity, Kind.EVENT, 0);
    }

    public static void onEquipment(class_2744 packet) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        if (SimulationSignals.now() - ENTITY_ADDED.getOrDefault(packet.method_11820(), -4611686018427387904L) < 2000000000L) {
            return;
        }
        class_1297 entity = level.method_8469(packet.method_11820());
        if (entity == null) {
            return;
        }
        LAST_ACTIVE.put(entity.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(entity, Kind.EQUIP, 0);
    }

    public static void onDamage(class_8143 packet) {
        if (!active) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        class_746 self = mc.field_1724;
        if (level == null || self == null) {
            return;
        }
        class_6880 type = packet.comp_1268();
        if (type.method_40225(class_8111.field_42353) || type.method_40225(class_8111.field_42355) || type.method_40225(class_8111.field_42358)) {
            return;
        }
        class_1297 victim = level.method_8469(packet.comp_1267());
        if (victim == null) {
            return;
        }
        int cause = packet.comp_1269();
        if (type.method_40225(class_8111.field_42320) && cause >= 0 && cause != self.method_5628() && level.method_8469(cause) == null) {
            SimulationSignals.pin(victim.method_24515(), "Hidden Attacker hit " + SimulationSignals.name(victim.method_5864()));
        }
        LAST_ACTIVE.put(victim.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(victim, Kind.HURT, 0);
    }

    public static void onTakeItem(class_2775 packet) {
        if (!active) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        class_746 self = mc.field_1724;
        if (level == null || self == null) {
            return;
        }
        int collector = packet.method_11912();
        if (collector == self.method_5628()) {
            return;
        }
        class_1297 item = level.method_8469(packet.method_11915());
        if (item == null) {
            return;
        }
        class_1297 taker = level.method_8469(collector);
        if (taker == null) {
            String string;
            if (item instanceof class_1542) {
                class_1542 stack = (class_1542)item;
                string = class_7923.field_41178.method_10221((Object)stack.method_6983().method_7909()).method_12832();
            } else {
                string = SimulationSignals.name(item.method_5864());
            }
            String what = string;
            SimulationSignals.pin(item.method_24515(), "Hidden Pickup " + what);
            return;
        }
        if (taker instanceof class_1657) {
            return;
        }
        LAST_ACTIVE.put(taker.method_5628(), SimulationSignals.now());
        SimulationSignals.entitySignal(taker, Kind.EVENT, 0);
    }

    public static void onAddEntity(class_2604 packet) {
        if (!active) {
            return;
        }
        long now = SimulationSignals.now();
        ENTITY_ADDED.put(packet.method_11167(), now);
        LAST_ACTIVE.put(packet.method_11167(), now);
        class_1299 type = packet.method_11169();
        if (type == class_1299.field_6097) {
            return;
        }
        class_2338 pos = class_2338.method_49637((double)packet.method_11175(), (double)packet.method_11174(), (double)packet.method_11176());
        if (type == class_1299.field_6112) {
            LIGHTNING_PENDING.put(packet.method_11167(), (Object)new long[]{pos.method_10063(), now});
            return;
        }
        if (!SimulationSignals.spawnable(type) || SimulationSignals.isQuiet()) {
            return;
        }
        if (!SimulationSignals.wellInsideTracking(packet.method_11175(), packet.method_11176(), type)) {
            return;
        }
        if (!SimulationSignals.settled(pos)) {
            return;
        }
        int id = packet.method_11167();
        SimulationSignals.signal(pos, Kind.SPAWN, SimulationSignals.spill(type), () -> SimulationSignals.entityShare(id), () -> SimulationSignals.name(type));
    }

    public static void onRemoveEntities(IntList ids) {
        if (!active) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        long now = SimulationSignals.now();
        boolean quiet = SimulationSignals.isQuiet();
        for (int i = 0; i < ids.size(); ++i) {
            class_1297 entity;
            int id = ids.getInt(i);
            long[] bolt = (long[])LIGHTNING_PENDING.remove(id);
            if (bolt != null) {
                if (quiet || now - bolt[1] >= 3000000000L) continue;
                SimulationSignals.signal(class_2338.method_10092((long)bolt[0]), Kind.LIGHTNING, 0, () -> SimulationSignals.entityShare(id), () -> "lightning_bolt");
                continue;
            }
            if (quiet || (entity = level.method_8469(id)) == null || entity instanceof class_1657 || now - ENTITY_ADDED.getOrDefault(id, -4611686018427387904L) < 2000000000L || !SimulationSignals.spawnable(entity.method_5864()) || !SimulationSignals.wellInsideTracking(entity.method_23317(), entity.method_23321(), entity.method_5864()) || !SimulationSignals.settled(entity.method_24515())) continue;
            SimulationSignals.entitySignal(entity, Kind.REMOVE, SimulationSignals.spill(entity.method_5864()));
        }
    }

    public static void onBlockUpdate(class_2626 packet) {
        if (!active) {
            return;
        }
        SimulationSignals.block(packet.method_11309(), packet.method_11308());
    }

    public static void onSectionUpdate(class_2637 packet) {
        if (!active) {
            return;
        }
        packet.method_30621(SimulationSignals::block);
    }

    private static void block(class_2338 pos, class_2680 state) {
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        class_2680 previous = level.method_8320(pos);
        if (previous == state) {
            return;
        }
        if (SimulationSignals.ignored(previous) || SimulationSignals.ignored(state)) {
            return;
        }
        if (!SimulationSignals.settled(pos)) {
            return;
        }
        Kind kind = SimulationSignals.ticked(previous, state) ? Kind.TICK : Kind.BLOCK;
        SimulationSignals.signal(pos, kind, 1, () -> SimulationSignals.blockShare(pos), () -> SimulationSignals.describe(previous) + " -> " + SimulationSignals.describe(state));
    }

    static boolean ticked(class_2680 previous, class_2680 state) {
        class_2248 to;
        class_2248 from = previous.method_26204();
        if (from == (to = state.method_26204())) {
            return true;
        }
        if (!previous.method_26227().method_15769() || !state.method_26227().method_15769()) {
            return true;
        }
        if (previous.method_26164(class_3481.field_15503) && state.method_26215()) {
            return true;
        }
        if (previous.method_26215() && state.method_26164(class_3481.field_15503) && !((Boolean)state.method_61767((class_2769)class_2741.field_12514, (Comparable)Boolean.valueOf(true))).booleanValue()) {
            return true;
        }
        if (previous.method_26164(class_3481.field_15462) && (state.method_26164(class_3481.field_15475) || state.method_26164(class_3481.field_15503))) {
            return true;
        }
        if (previous.method_26164(class_3481.field_20341) && state.method_26215()) {
            return true;
        }
        if (from == class_2246.field_10566 && (to == class_2246.field_10219 || to == class_2246.field_10402)) {
            return true;
        }
        if (from == class_2246.field_10362 && to == class_2246.field_10566) {
            return true;
        }
        if (from == class_2246.field_46287 && to == class_2246.field_46285) {
            return true;
        }
        if (from == class_2246.field_46286 && to == class_2246.field_46284) {
            return true;
        }
        if (previous.method_26215() && GROWS_FROM_AIR.contains(to)) {
            return true;
        }
        if (state.method_26215() && GONE_TO_AIR.contains(from)) {
            return true;
        }
        return class_5955.method_34737((class_2248)from).map(next -> next == to).orElse(false);
    }

    private static boolean ignored(class_2680 state) {
        return state.method_27852(class_2246.field_28048) || state.method_27852(class_2246.field_28675) || state.method_27852(class_2246.field_28676) || state.method_27852(class_2246.field_16999) || state.method_27852(class_2246.field_20422) || state.method_27852(class_2246.field_20421) || state.method_27852(class_2246.field_10597) || state.method_27852(class_2246.field_22123) || state.method_27852(class_2246.field_22124) || state.method_27852(class_2246.field_23078) || state.method_27852(class_2246.field_23079);
    }

    public static void tick() {
        if (!active) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_746 self = mc.field_1724;
        if (mc.field_1687 == null || self == null) {
            return;
        }
        long now = SimulationSignals.now();
        if (self.method_23318() < 0.0) {
            undergroundUntil = now + 3000000000L;
        }
        double x = self.method_23317();
        double z = self.method_23321();
        if (hasLast && SimulationSignals.square(x - lastX, z - lastZ) > 64.0) {
            SimulationSignals.startQuiet();
        }
        lastX = x;
        lastZ = z;
        hasLast = true;
        SimulationSignals.refreshPassed(self);
        if (++ticks % 10 != 0) {
            return;
        }
        int pcx = self.method_24515().method_10263() >> 4;
        int pcz = self.method_24515().method_10260() >> 4;
        int start = SimulationSignals.ringStart();
        boolean sample = !SimulationSignals.isQuiet() && (!hasSample || SimulationSignals.square(x - sampleX, z - sampleZ) <= 900.0);
        sampleX = x;
        sampleZ = z;
        hasSample = true;
        Int2DoubleOpenHashMap reach = new Int2DoubleOpenHashMap();
        int view = SimulationSignals.view();
        FROZEN.clear();
        for (class_1297 entity : mc.field_1687.method_18112()) {
            int ez;
            int ex;
            int d;
            if (entity instanceof class_1657) continue;
            long seen = LAST_ACTIVE.get(entity.method_5628());
            if (seen == 0L) {
                LAST_ACTIVE.put(entity.method_5628(), now);
            } else if (now - seen > 40000000000L && SimulationSignals.witness(entity) && (d = SimulationSignals.distance(ex = entity.method_24515().method_10263() >> 4, ez = entity.method_24515().method_10260() >> 4, pcx, pcz)) >= start && d <= view && !SimulationSignals.nearVisiblePlayer(ex, ez, self, 0)) {
                FROZEN.add(class_1923.method_8331((int)ex, (int)ez));
            }
            if (sample && SimulationSignals.spawnable(entity.method_5864()) && !entity.method_5782() && !entity.method_5765()) {
                int key = entity.method_5864().method_18387();
                double distance = Math.hypot(entity.method_23317() - x, entity.method_23321() - z);
                if (distance > reach.get(key)) {
                    reach.put(key, distance);
                }
            }
            if (SimulationSignals.distance(entity.method_24515().method_10263() >> 4, entity.method_24515().method_10260() >> 4, pcx, pcz) >= start) continue;
            ENTITY_INSIDE.put(entity.method_5628(), now);
        }
        if (sample) {
            for (Object key : (Object)TRACKED_RANGE.keySet().toIntArray()) {
                TRACKED_RANGE.put((int)key, Math.max(reach.get((int)key), TRACKED_RANGE.get((int)key) - 0.5));
            }
            for (Int2DoubleMap.Entry entry : reach.int2DoubleEntrySet()) {
                if (!(entry.getDoubleValue() > TRACKED_RANGE.get(entry.getIntKey()))) continue;
                TRACKED_RANGE.put(entry.getIntKey(), entry.getDoubleValue());
            }
        }
        if (ticks % 200 != 0) {
            return;
        }
        ObjectIterator chunks = CHUNKS.long2ObjectEntrySet().iterator();
        while (chunks.hasNext()) {
            if (!(((ChunkSignal)((Long2ObjectMap.Entry)chunks.next()).getValue()).score(now) < 0.05f)) continue;
            chunks.remove();
        }
        ENTITY_SHARES.values().removeIf(share -> share.amount(now) < 0.05f);
        BLOCK_SHARES.values().removeIf(share -> share.amount(now) < 0.05f);
        int keep = SimulationSignals.view() + 4;
        CHUNK_LOADED.long2LongEntrySet().removeIf(e -> SimulationSignals.distance(class_1923.method_8325((long)e.getLongKey()), class_1923.method_8332((long)e.getLongKey()), pcx, pcz) > keep);
        ENTITY_ADDED.int2LongEntrySet().removeIf(e -> now - e.getLongValue() > 60000000000L);
        ENTITY_INSIDE.int2LongEntrySet().removeIf(e -> now - e.getLongValue() > 60000000000L);
        LIGHTNING_PENDING.values().removeIf(bolt -> now - bolt[1] > 3000000000L);
        LAST_ACTIVE.keySet().removeIf(id -> mc.field_1687.method_8469(id) == null);
    }

    private static boolean witness(class_1297 entity) {
        class_1314 mob;
        return entity instanceof class_1314 && !((mob = (class_1314)entity) instanceof class_1606) && !mob.method_5987() && !mob.method_5765() && !mob.method_5782() && !mob.method_6113();
    }

    private static void pin(class_2338 pos, String detail) {
        class_746 self = class_310.method_1551().field_1724;
        if (self == null || SimulationSignals.holding() || pos.method_10264() < 0) {
            return;
        }
        if (PINS.size() >= 32) {
            PINS.poll();
        }
        PINS.add(new Pin(pos.method_10062(), detail + " @ " + pos.method_10263() + " " + pos.method_10264() + " " + pos.method_10260()));
    }

    private static void refreshPassed(class_746 self) {
        int pcx = self.method_24515().method_10263() >> 4;
        int pcz = self.method_24515().method_10260() >> 4;
        long center = class_1923.method_8331((int)pcx, (int)pcz);
        int radius = SimulationSignals.ringStart() - 1 + 1;
        if (center == passedCenter && radius == passedRadius) {
            return;
        }
        passedCenter = center;
        passedRadius = radius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                long key = class_1923.method_8331((int)(pcx + dx), (int)(pcz + dz));
                if (!passed.add(key)) continue;
                CHUNKS.remove(key);
            }
        }
    }

    private static void entitySignal(class_1297 entity, Kind kind, int spill) {
        class_6025 ownable;
        class_10583 owner;
        class_1540 falling;
        if (entity instanceof class_1657) {
            return;
        }
        if (entity.method_31483() instanceof class_1657) {
            return;
        }
        if (entity instanceof class_1540 && (falling = (class_1540)entity).method_6962().method_27852(class_2246.field_28048)) {
            return;
        }
        class_746 self = class_310.method_1551().field_1724;
        if (self == null) {
            return;
        }
        if (entity instanceof class_6025 && (owner = (ownable = (class_6025)entity).method_66287()) != null && self.method_5667().equals(owner.method_66263())) {
            return;
        }
        if (SimulationSignals.now() - ENTITY_INSIDE.getOrDefault(entity.method_5628(), -4611686018427387904L) < 60000000000L) {
            return;
        }
        SimulationSignals.signal(entity.method_24515(), kind, spill, () -> SimulationSignals.entityShare(entity.method_5628()), () -> SimulationSignals.name(entity.method_5864()));
    }

    private static Share entityShare(int id) {
        return (Share)ENTITY_SHARES.computeIfAbsent(id, key -> new Share());
    }

    private static Share blockShare(class_2338 pos) {
        return (Share)BLOCK_SHARES.computeIfAbsent(pos.method_10063(), key -> new Share());
    }

    private static String name(class_1299<?> type) {
        return class_7923.field_41177.method_10221(type).method_12832();
    }

    private static String describe(class_2680 state) {
        return state.toString().replace("Block{minecraft:", "").replace("}", "");
    }

    private static void signal(class_2338 pos, Kind kind, int spill, Supplier<Share> source, Supplier<String> what) {
        int pcz;
        int pcx;
        int cz;
        class_746 self = class_310.method_1551().field_1724;
        if (self == null) {
            return;
        }
        if (pos.method_10264() < 0 || SimulationSignals.holding()) {
            return;
        }
        int cx = pos.method_10263() >> 4;
        int d = SimulationSignals.distance(cx, cz = pos.method_10260() >> 4, pcx = self.method_24515().method_10263() >> 4, pcz = self.method_24515().method_10260() >> 4);
        if (d < SimulationSignals.ringStart() + spill || d > SimulationSignals.view()) {
            return;
        }
        long key = class_1923.method_8331((int)cx, (int)cz);
        if (passed.contains(key)) {
            return;
        }
        if (ignoreVisiblePlayers && SimulationSignals.nearVisiblePlayer(cx, cz, self, spill)) {
            return;
        }
        long now = SimulationSignals.now();
        float weight = source.get().take(kind.weight, kind.cap, now);
        if (weight <= 0.0f) {
            return;
        }
        String detail = kind.title + " " + what.get() + " @ " + pos.method_10263() + " " + pos.method_10264() + " " + pos.method_10260();
        ((ChunkSignal)CHUNKS.computeIfAbsent(key, k -> new ChunkSignal())).add(kind, now, weight, detail);
    }

    private static boolean nearVisiblePlayer(int cx, int cz, class_746 self, int spill) {
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return false;
        }
        int reach = SimulationSignals.simulation() + 1 + spill;
        for (class_1657 player : level.method_18456()) {
            if (player == self || SimulationSignals.distance(cx, cz, player.method_24515().method_10263() >> 4, player.method_24515().method_10260() >> 4) > reach) continue;
            return true;
        }
        return false;
    }

    private static boolean settled(class_2338 pos) {
        long loaded = CHUNK_LOADED.getOrDefault(class_1923.method_8331((int)(pos.method_10263() >> 4), (int)(pos.method_10260() >> 4)), Long.MIN_VALUE);
        return loaded == Long.MIN_VALUE || SimulationSignals.now() - loaded > 3000000000L;
    }

    private static boolean wellInsideTracking(double x, double z, class_1299<?> type) {
        class_746 self = class_310.method_1551().field_1724;
        if (self == null) {
            return false;
        }
        double range = Math.min(TRACKED_RANGE.get(type.method_18387()), (double)SimulationSignals.view() * 16.0);
        return Math.hypot(x - self.method_23317(), z - self.method_23321()) < range - 24.0;
    }

    private static boolean spawnable(class_1299<?> type) {
        return type.method_5891() == class_1311.field_6302 || type == class_1299.field_6052 || type == class_1299.field_6044;
    }

    private static int spill(class_1299<?> type) {
        return type == class_1299.field_6052 || type == class_1299.field_6044 ? 1 : 0;
    }

    private static void startQuiet() {
        quietUntil = SimulationSignals.now() + 2000000000L;
    }

    private static boolean isQuiet() {
        return SimulationSignals.now() < quietUntil;
    }

    private static double square(double dx, double dz) {
        return dx * dx + dz * dz;
    }

    public static int distance(int ax, int az, int bx, int bz) {
        return Math.max(Math.abs(ax - bx), Math.abs(az - bz));
    }

    static {
        serverSim = -1;
        serverView = -1;
        ignoreVisiblePlayers = true;
        memoryNanos = 30000000000L;
        passedCenter = Long.MIN_VALUE;
        passedRadius = -1;
        CHUNKS = new Long2ObjectOpenHashMap();
        ENTITY_SHARES = new Int2ObjectOpenHashMap();
        BLOCK_SHARES = new Long2ObjectOpenHashMap();
        CHUNK_LOADED = new Long2LongOpenHashMap();
        ENTITY_ADDED = new Int2LongOpenHashMap();
        ENTITY_INSIDE = new Int2LongOpenHashMap();
        PASSED_BY_DIMENSION = new HashMap<class_5321<class_1937>, LongOpenHashSet>();
        passed = new LongOpenHashSet();
        TRACKED_RANGE = new Int2DoubleOpenHashMap();
        LIGHTNING_PENDING = new Int2ObjectOpenHashMap();
        LAST_ACTIVE = new Int2LongOpenHashMap();
        FROZEN = new LongOpenHashSet();
        PINS = new ArrayDeque();
    }

    @Environment(value=EnvType.CLIENT)
    public record Pin(class_2338 pos, String detail) {
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Kind {
        MOVE("Mob Move", 2.5f, true),
        HEAD("Head Turn", 2.5f, true),
        SPAWN("Spawn / Drop", 2.0f, false),
        REMOVE("Despawn / Pickup", 1.5f, false),
        STATE("Entity State", 0.5f, false),
        EVENT("Entity Event", 5.0f, true),
        BLOCK("Block Change", 1.6f, false),
        TICK("Tick Change", 10.0f, true),
        LIGHTNING("Lightning", 10.0f, true),
        HURT("Hurt", 5.0f, true),
        EQUIP("Equipment", 5.0f, true);

        public final String title;
        final float weight;
        final float cap;

        private Kind(String title, float weight, boolean ticked) {
            this.title = title;
            this.weight = weight;
            this.cap = ticked ? 10.0f : 4.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ChunkSignal {
        private float score;
        private long updated;
        private int kinds;
        private final int[] counts = new int[Kind.values().length];
        private final ArrayDeque<String> recent = new ArrayDeque();

        public float score(long now) {
            return this.score * SimulationSignals.decay(now - this.updated);
        }

        public boolean has(Kind kind) {
            return (this.kinds & 1 << kind.ordinal()) != 0;
        }

        public int count(Kind kind) {
            return this.counts[kind.ordinal()];
        }

        public List<String> recent() {
            return List.copyOf(this.recent);
        }

        void add(Kind kind, long now, float weight, String detail) {
            this.recent.addLast(detail);
            if (this.recent.size() > 6) {
                this.recent.removeFirst();
            }
            this.score = this.score(now) + weight;
            this.updated = now;
            this.kinds |= 1 << kind.ordinal();
            int n = kind.ordinal();
            this.counts[n] = this.counts[n] + 1;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Share {
        private float amount;
        private long updated;

        private Share() {
        }

        float amount(long now) {
            return this.amount * SimulationSignals.decay(now - this.updated);
        }

        float take(float weight, float cap, long now) {
            float held = this.amount(now);
            float granted = Math.min(weight, Math.max(cap, weight) - held);
            if (granted <= 0.0f) {
                return 0.0f;
            }
            this.amount = held + granted;
            this.updated = now;
            return granted;
        }
    }
}

