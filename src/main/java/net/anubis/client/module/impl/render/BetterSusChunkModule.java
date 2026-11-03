/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1922
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_1944
 *  net.minecraft.class_1959
 *  net.minecraft.class_1959$class_1963
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2302
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2397
 *  net.minecraft.class_2404
 *  net.minecraft.class_2488
 *  net.minecraft.class_2596
 *  net.minecraft.class_265
 *  net.minecraft.class_2672
 *  net.minecraft.class_2678
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_2804
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2902
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3558
 *  net.minecraft.class_3612
 *  net.minecraft.class_4076
 *  net.minecraft.class_5321
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_7115
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.BlockUpdateEvent;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.ChunkOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1922;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2302;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2397;
import net.minecraft.class_2404;
import net.minecraft.class_2488;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_2804;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2902;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3558;
import net.minecraft.class_3612;
import net.minecraft.class_4076;
import net.minecraft.class_5321;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_7115;

@Environment(value=EnvType.CLIENT)
public final class BetterSusChunkModule
extends ClientModule {
    private static final String DONUT_BRAND = "DonutFolia";
    private static final int KELP = 0;
    private static final int CAVE_VINES = 1;
    private static final int WEEPING_VINES = 2;
    private static final int BAMBOO = 3;
    private static final int SUGAR_CANE = 4;
    private static final int CACTUS = 5;
    private static final int COCOA = 6;
    private static final int BERRY = 7;
    private static final int NETHER_WART = 8;
    private static final int CROP = 9;
    private static final int STEM = 10;
    private static final int AMETHYST = 11;
    private static final int VINE = 12;
    private static final int LEAVES = 13;
    private static final int SOIL = 14;
    private static final int PROPAGULE = 15;
    private static final int EYEBLOSSOM = 16;
    private static final int SNOW = 17;
    private static final int ICE = 18;
    private static final int EVIDENCE_KINDS = 19;
    private static final int GROWTH_LIGHT = 9;
    private static final int FULL_LIGHT = 15;
    private static final int MELT_LIGHT = 10;
    private static final int BAMBOO_LIMIT = 16;
    private static final int SHORT_COLUMN = 3;
    private static final int KELP_MAX_AGE = 25;
    private static final int DAY_TICKS = 24000;
    private static final int EYEBLOSSOM_OPENS = 12600;
    private static final int EYEBLOSSOM_CLOSES = 23401;
    private static final int EYEBLOSSOM_SETTLE = 4000;
    private static final int EYEBLOSSOM_MIN = 3;
    private static final int HOLE_DEPTH = 2;
    private static final int ENCLOSED_SIDES = 3;
    private static final int REFRESH_TICKS = 20;
    private static final int LOADED_MARGIN = 3;
    private static final class_2350[] DIRECTIONS = class_2350.values();
    private static final int[][] SIDES = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int FILL_ALPHA = 100;
    private final NumberSetting simDistance = this.addSetting("sim-distance", new NumberSetting("Simulation Distance", 4.0f, 2.0f, 16.0f).step(1.0f));
    private final ColorSetting chunkColor = this.addSetting("chunk-color", new ColorSetting("Chunk Color", -65536));
    private final NumberSetting sensitivity = this.addSetting("min-flag", new NumberSetting("Sensitivity", 40.0f, 1.0f, 500.0f).step(1.0f));
    private final BooleanSetting holeFilter = this.addSetting("hole-filter", new BooleanSetting("Hole Filter", false));
    private final NumberSetting holeDistance = this.addSetting("hole-distance", new NumberSetting("Hole Distance", 2.0f, 0.0f, 8.0f).step(1.0f));
    private final BooleanSetting customEvidence = this.addSetting("custom-evidence", new BooleanSetting("Custom Flags", false));
    private final BooleanSetting[] evidence = new BooleanSetting[]{this.evidence("count-kelp", "Kelp"), this.evidence("count-cave-vines", "Cave Vines"), this.evidence("count-weeping-vines", "Weeping Vines"), this.evidence("count-bamboo", "Bamboo"), this.evidence("count-sugar-cane", "Sugar Cane"), this.evidence("count-cactus", "Cactus"), this.evidence("count-cocoa", "Cocoa"), this.evidence("count-sweet-berries", "Sweet Berries"), this.evidence("count-nether-wart", "Nether Wart"), this.evidence("count-crops", "Crops"), this.evidence("count-stems", "Stems"), this.evidence("count-amethyst", "Amethyst"), this.evidence("count-vines", "Vines"), this.evidence("count-leaves", "Leaves"), this.evidence("count-grass-dirt", "Grass & Dirt"), this.evidence("count-propagules", "Mangrove Propagules"), this.evidence("count-eyeblossoms", "Eyeblossoms"), this.evidence("count-snow", "Snow"), this.evidence("count-ice", "Ice")};
    private final ChunkOverlay primaryOverlay = new ChunkOverlay();
    private final Object lock = new Object();
    private final Long2ObjectOpenHashMap<Record> records = new Long2ObjectOpenHashMap();
    private final Long2IntOpenHashMap clearCoverage = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap support = new Long2IntOpenHashMap();
    private final LongOpenHashSet scanned = new LongOpenHashSet();
    private final LongArrayList distant = new LongArrayList();
    private final Set<Long> pending = ConcurrentHashMap.newKeySet();
    private final Set<Long> kelpChunks = ConcurrentHashMap.newKeySet();
    private final Set<Long> seenKelp = ConcurrentHashMap.newKeySet();
    private volatile LongSet primary = LongSets.EMPTY_SET;
    private volatile int generation;
    private volatile boolean dirty;
    private volatile boolean[] counted = new boolean[19];
    private ExecutorService executor;
    private class_5321<class_1937> worldKey;
    private int tickCounter;

    public BetterSusChunkModule() {
        super("better-sus-chunk", "Better Sus Chunk", "Marks chunks no player has been near: areas with overdue natural growth rule themselves out.", ModuleCategory.BASE_RAID);
        this.holeDistance.visibleWhen(this.holeFilter::get);
        this.simDistance.onChange(() -> {
            this.dirty = true;
        });
        this.holeFilter.onChange(() -> {
            if (this.holeFilter.get()) {
                this.dirty = true;
            }
        });
        this.customEvidence.onChange(this::refreshEvidence);
        for (BooleanSetting kind : this.evidence) {
            kind.visibleWhen(this.customEvidence::get);
            kind.onChange(this::refreshEvidence);
        }
        this.refreshEvidence();
    }

    private BooleanSetting evidence(String id, String name) {
        return this.addSetting(id, new BooleanSetting(name, true));
    }

    private void refreshEvidence() {
        boolean custom = this.customEvidence.get();
        boolean[] next = new boolean[19];
        for (int i = 0; i < next.length; ++i) {
            next[i] = !custom || this.evidence[i].get();
        }
        this.counted = next;
        this.dirty = true;
    }

    @Override
    protected void onEnable() {
        this.resetState();
        this.executor = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "Anubis Better Sus Chunk");
            thread.setDaemon(true);
            return thread;
        });
        class_638 level = class_310.method_1551().field_1687;
        this.worldKey = level == null ? null : level.method_27983();
        this.dirty = false;
        this.rescanLoadedChunks();
    }

    @Override
    protected void onDisable() {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
        this.resetState();
        this.worldKey = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resetState() {
        Object object = this.lock;
        synchronized (object) {
            ++this.generation;
            this.clearCoverage.clear();
            this.support.clear();
            this.records.clear();
            this.scanned.clear();
            this.pending.clear();
            this.kelpChunks.clear();
            this.seenKelp.clear();
        }
        this.primary = LongSets.EMPTY_SET;
        this.tickCounter = 0;
    }

    private void rescanLoadedChunks() {
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        if (!this.isEnabled() || mc.field_1724 == null || level == null) {
            return;
        }
        this.resetState();
        int radius = mc.field_1690.method_38521() + 3;
        class_1923 center = mc.field_1724.method_31476();
        for (int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for (int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                if (!level.method_8393(x, z)) continue;
                this.scheduleScan(class_1923.method_8331((int)x, (int)z));
            }
        }
    }

    private void scheduleScan(long key) {
        ExecutorService worker = this.executor;
        if (worker == null || worker.isShutdown()) {
            return;
        }
        if (!this.pending.add(key)) {
            return;
        }
        int expected = this.generation;
        worker.submit(() -> this.runScan(key, expected));
    }

    private void runScan(long key, int expected) {
        int cz;
        this.pending.remove(key);
        if (expected != this.generation || !this.isEnabled()) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        int cx = class_1923.method_8325((long)key);
        if (!level.method_8393(cx, cz = class_1923.method_8332((long)key))) {
            return;
        }
        class_2818 chunk = level.method_8497(cx, cz);
        if (chunk == null) {
            return;
        }
        this.rescan(level, key, chunk, expected);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rescan(class_638 level, long key, class_2818 chunk, int expected) {
        Record old = this.removeRecord(key);
        Source fresh = this.scanChunk(level, key, chunk);
        Object object = this.lock;
        synchronized (object) {
            int dz;
            int dx;
            if (expected != this.generation) {
                return;
            }
            Record merged = new Record(fresh.clears() || old != null && old.clears(), this.simDistance.getInt(), fresh.hole() || old != null && old.hole(), Math.max(fresh.weight(), old == null ? 0 : old.weight()));
            this.scanned.add(key);
            this.records.put(key, (Object)merged);
            if (!merged.clears()) {
                return;
            }
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            int reach = 2 * merged.radius() + 2;
            for (dx = -merged.radius(); dx <= merged.radius(); ++dx) {
                for (dz = -merged.radius(); dz <= merged.radius(); ++dz) {
                    this.clearCoverage.addTo(class_1923.method_8331((int)(cx + dx), (int)(cz + dz)), 1);
                }
            }
            for (dx = -reach; dx <= reach; ++dx) {
                for (dz = -reach; dz <= reach; ++dz) {
                    this.support.addTo(class_1923.method_8331((int)(cx + dx), (int)(cz + dz)), merged.weight());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Record removeRecord(long key) {
        Object object = this.lock;
        synchronized (object) {
            int dz;
            int dx;
            Record old = (Record)this.records.remove(key);
            this.scanned.remove(key);
            if (old == null || !old.clears()) {
                return old;
            }
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            int reach = 2 * old.radius() + 2;
            for (dx = -old.radius(); dx <= old.radius(); ++dx) {
                for (dz = -old.radius(); dz <= old.radius(); ++dz) {
                    BetterSusChunkModule.subtract(this.clearCoverage, class_1923.method_8331((int)(cx + dx), (int)(cz + dz)), 1);
                }
            }
            for (dx = -reach; dx <= reach; ++dx) {
                for (dz = -reach; dz <= reach; ++dz) {
                    BetterSusChunkModule.subtract(this.support, class_1923.method_8331((int)(cx + dx), (int)(cz + dz)), old.weight());
                }
            }
            return old;
        }
    }

    private static void subtract(Long2IntOpenHashMap map, long key, int amount) {
        if (!map.containsKey(key)) {
            return;
        }
        int value = map.get(key);
        if (value <= amount) {
            map.remove(key);
        } else {
            map.put(key, value - amount);
        }
    }

    private Source scanChunk(class_638 level, long key, class_2818 chunk) {
        boolean donut = BetterSusChunkModule.donutServer();
        boolean[] counts = this.counted;
        int count = donut && counts[0] && this.kelpChunks.contains(key) ? 1 : 0;
        BlockReader blocks = new BlockReader(level);
        class_1923 pos = chunk.method_12004();
        int minX = pos.method_8326();
        int minZ = pos.method_8328();
        Boolean bloomsOpen = BetterSusChunkModule.eyeblossomsOpen(level);
        int lateBlooms = 0;
        int settledBlooms = 0;
        class_2826[] sections = chunk.method_12006();
        for (int i = 0; i < sections.length; ++i) {
            class_2826 section = sections[i];
            if (section == null || section.method_38292() || !section.method_19523(state -> BetterSusChunkModule.kindOf(state, counts) >= 0)) continue;
            int baseY = chunk.method_31607() + (i << 4);
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        class_2680 state2 = section.method_12254(x, y, z);
                        int kind = BetterSusChunkModule.kindOf(state2, counts);
                        if (kind < 0) continue;
                        if (kind == 15) {
                            if ((Integer)state2.method_11654((class_2769)class_7115.field_37588) >= 4) continue;
                            ++count;
                            continue;
                        }
                        if (kind == 16) {
                            if (bloomsOpen == null) continue;
                            if (state2.method_27852(class_2246.field_55060) == bloomsOpen.booleanValue()) {
                                ++settledBlooms;
                                continue;
                            }
                            ++lateBlooms;
                            continue;
                        }
                        if (!BetterSusChunkModule.owed(blocks, minX + x, baseY + y, minZ + z, state2, kind, donut)) continue;
                        ++count;
                    }
                }
            }
        }
        if (settledBlooms == 0 && lateBlooms >= 3) {
            count += lateBlooms;
        }
        if ((counts[17] || counts[18]) && chunk.method_39295(class_2902.class_2903.field_13197)) {
            class_2902 heightmap = chunk.method_12032(class_2902.class_2903.field_13197);
            int seaLevel = level.method_8615();
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    int y = heightmap.method_12603(x, z);
                    if (!BetterSusChunkModule.surfaceOwed(level, blocks, minX + x, y, minZ + z, seaLevel, donut, counts)) continue;
                    ++count;
                }
            }
        }
        boolean hole = this.holeFilter.get() && BetterSusChunkModule.hasHole(level, pos);
        return new Source(count > 0, hole, count);
    }

    private static int kindOf(class_2680 state, boolean[] counts) {
        int kind = BetterSusChunkModule.classify(state);
        return kind >= 0 && counts[kind] ? kind : -1;
    }

    private static int classify(class_2680 state) {
        class_2248 block = state.method_26204();
        if (block == class_2246.field_9993) {
            return 0;
        }
        if (block == class_2246.field_28675) {
            return 1;
        }
        if (block == class_2246.field_22123) {
            return 2;
        }
        if (block == class_2246.field_10211) {
            return 3;
        }
        if (block == class_2246.field_10424) {
            return 4;
        }
        if (block == class_2246.field_10029) {
            return 5;
        }
        if (block == class_2246.field_10302) {
            return 6;
        }
        if (block == class_2246.field_16999) {
            return 7;
        }
        if (block == class_2246.field_9974) {
            return 8;
        }
        if (block == class_2246.field_46286 || block == class_2246.field_46287) {
            return 10;
        }
        if (block == class_2246.field_10597) {
            return 12;
        }
        if (block == class_2246.field_27160 || block == class_2246.field_27161 || BetterSusChunkModule.isBud(state)) {
            return 11;
        }
        if (block == class_2246.field_37544) {
            return (Boolean)state.method_11654((class_2769)class_7115.field_37591) != false ? 15 : -1;
        }
        if (block == class_2246.field_55060 || block == class_2246.field_55061) {
            return 16;
        }
        if (block instanceof class_2302) {
            return 9;
        }
        if (block instanceof class_2397) {
            return 13;
        }
        if (BetterSusChunkModule.isGrass(state) || state.method_27852(class_2246.field_10566)) {
            return 14;
        }
        return -1;
    }

    private static Boolean eyeblossomsOpen(class_638 level) {
        if (level.method_27983() != class_1937.field_25179 || level.method_8597().comp_5219()) {
            return null;
        }
        int time = Math.floorMod(level.method_8532(), 24000);
        boolean open = time >= 12600 && time < 23401;
        int since = open ? time - 12600 : Math.floorMod(time - 23401, 24000);
        return since >= 4000 ? Boolean.valueOf(open) : null;
    }

    private static boolean owed(BlockReader blocks, int x, int y, int z, class_2680 state, int kind, boolean donut) {
        if (kind == 14) {
            return BetterSusChunkModule.soilOwed(blocks, x, y, z, state);
        }
        if (donut) {
            if (kind == 0) {
                return blocks.state(x, y + 1, z).method_27852(class_2246.field_10382) && BetterSusChunkModule.column(blocks, x, y, z, class_2246.field_10463, 3) < 3;
            }
            if (kind == 11) {
                return BetterSusChunkModule.isBud(state);
            }
            if (kind == 1) {
                return false;
            }
        }
        return BetterSusChunkModule.growthOwed(blocks, x, y, z, state);
    }

    private static boolean growthOwed(BlockReader blocks, int x, int y, int z, class_2680 state) {
        class_2248 block = state.method_26204();
        if (block == class_2246.field_9993) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12517) < 25 && blocks.state(x, y + 1, z).method_27852(class_2246.field_10382);
        }
        if (block == class_2246.field_28675 || block == class_2246.field_22123) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12517) < 25 && blocks.state(x, y - 1, z).method_26215();
        }
        if (block == class_2246.field_10211) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12549) == 0 && blocks.state(x, y + 1, z).method_26215() && BetterSusChunkModule.bambooBelow(blocks, x, y, z) + 1 < 16 && blocks.light(x, y + 1, z) >= 9;
        }
        if (block == class_2246.field_10424) {
            return blocks.state(x, y + 1, z).method_26215() && BetterSusChunkModule.column(blocks, x, y, z, class_2246.field_10424, 3) < 3;
        }
        if (block == class_2246.field_10029) {
            return blocks.state(x, y + 1, z).method_26215() && ((Integer)state.method_11654((class_2769)class_2741.field_12498) < 15 || BetterSusChunkModule.column(blocks, x, y, z, class_2246.field_10029, 3) < 3);
        }
        if (block == class_2246.field_10302) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12556) < 2;
        }
        if (block == class_2246.field_9974) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12497) < 3;
        }
        if (block == class_2246.field_16999) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12497) < 3 && blocks.light(x, y + 1, z) >= 9;
        }
        if (block == class_2246.field_46286 || block == class_2246.field_46287) {
            return (Integer)state.method_11654((class_2769)class_2741.field_12550) < 7 && blocks.light(x, y, z) >= 9;
        }
        if (block instanceof class_2302) {
            class_2302 crop = (class_2302)block;
            return !crop.method_9825(state) && blocks.light(x, y, z) >= 9;
        }
        if (block == class_2246.field_10597) {
            return blocks.state(x, y - 1, z).method_26215() && ((Boolean)state.method_11654((class_2769)class_2741.field_12489) != false || (Boolean)state.method_11654((class_2769)class_2741.field_12487) != false || (Boolean)state.method_11654((class_2769)class_2741.field_12540) != false || (Boolean)state.method_11654((class_2769)class_2741.field_12527) != false);
        }
        if (block instanceof class_2397) {
            return state.method_28498((class_2769)class_2741.field_12541) && state.method_28498((class_2769)class_2741.field_12514) && (Integer)state.method_11654((class_2769)class_2741.field_12541) == 7 && (Boolean)state.method_11654((class_2769)class_2741.field_12514) == false;
        }
        return BetterSusChunkModule.amethystOwed(blocks, x, y, z, state);
    }

    private static boolean amethystOwed(BlockReader blocks, int x, int y, int z, class_2680 state) {
        if (state.method_27852(class_2246.field_27160)) {
            for (class_2350 direction : DIRECTIONS) {
                class_2680 near = blocks.state(x + direction.method_10148(), y + direction.method_10164(), z + direction.method_10165());
                if (near.method_26215()) {
                    return true;
                }
                if (!near.method_27852(class_2246.field_10382) || near.method_26227().method_15761() != 8) continue;
                return true;
            }
            return false;
        }
        if (!BetterSusChunkModule.isBud(state) || !state.method_28498((class_2769)class_2741.field_12525)) {
            return false;
        }
        class_2350 facing = (class_2350)state.method_11654((class_2769)class_2741.field_12525);
        return blocks.state(x - facing.method_10148(), y - facing.method_10164(), z - facing.method_10165()).method_27852(class_2246.field_27160);
    }

    private static int column(BlockReader blocks, int x, int y, int z, class_2248 target, int limit) {
        int length;
        for (length = 1; length < limit && blocks.state(x, y - length, z).method_27852(target); ++length) {
        }
        return length;
    }

    private static int bambooBelow(BlockReader blocks, int x, int y, int z) {
        int below;
        for (below = 0; below < 16 && blocks.state(x, y - below - 1, z).method_27852(class_2246.field_10211); ++below) {
        }
        return below;
    }

    private static boolean soilOwed(BlockReader blocks, int x, int y, int z, class_2680 state) {
        class_2680 upper = blocks.state(x, y + 1, z);
        if (BetterSusChunkModule.isGrass(state)) {
            return BetterSusChunkModule.smothers(upper);
        }
        if (!state.method_27852(class_2246.field_10566) || BetterSusChunkModule.smothers(upper) || upper.method_26227().method_15767(class_3486.field_15517)) {
            return false;
        }
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                for (int ny = y - 1; ny <= y + 3; ++ny) {
                    if (dx == 0 && dz == 0 && ny == y || !BetterSusChunkModule.isGrass(blocks.state(x + dx, ny, z + dz)) || BetterSusChunkModule.smothers(blocks.state(x + dx, ny + 1, z + dz)) || blocks.light(x + dx, ny + 1, z + dz) < 9) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean smothers(class_2680 upper) {
        if (upper.method_27852(class_2246.field_10477) && (Integer)upper.method_11654((class_2769)class_2488.field_11518) == 1) {
            return false;
        }
        if (upper.method_26227().method_15761() == 8) {
            return true;
        }
        return class_3558.method_20049((class_2680)class_2246.field_10219.method_9564(), (class_2680)upper, (class_2350)class_2350.field_11036, (int)upper.method_26193()) >= 15;
    }

    private static boolean surfaceOwed(class_638 level, BlockReader blocks, int x, int y, int z, int seaLevel, boolean donut, boolean[] counts) {
        boolean snow = counts[17] && (!donut || !blocks.state(x, y - 1, z).method_27852(class_2246.field_28888));
        return snow && BetterSusChunkModule.snowOwed(level, blocks, x, y, z, seaLevel) || counts[18] && BetterSusChunkModule.iceOwed(level, blocks, x, y, z, seaLevel);
    }

    private static boolean snowOwed(class_638 level, BlockReader blocks, int x, int y, int z, int seaLevel) {
        if (level.method_31601(y) || !blocks.state(x, y, z).method_26215()) {
            return false;
        }
        if (!BetterSusChunkModule.holdsSnow(level, blocks.state(x, y - 1, z), new class_2338(x, y - 1, z))) {
            return false;
        }
        class_2338 pos = new class_2338(x, y, z);
        if (((class_1959)level.method_23753(pos).comp_349()).method_48162(pos, seaLevel) != class_1959.class_1963.field_9383) {
            return false;
        }
        return BetterSusChunkModule.calmLight(level, blocks, x, y, z);
    }

    private static boolean holdsSnow(class_638 level, class_2680 below, class_2338 pos) {
        if (below.method_26164(class_3481.field_39104)) {
            return false;
        }
        if (below.method_26164(class_3481.field_39105)) {
            return true;
        }
        if (below.method_27852(class_2246.field_10477) && (Integer)below.method_11654((class_2769)class_2488.field_11518) == 8) {
            return true;
        }
        return class_2248.method_9501((class_265)below.method_26220((class_1922)level, pos), (class_2350)class_2350.field_11036);
    }

    private static boolean iceOwed(class_638 level, BlockReader blocks, int x, int y, int z, int seaLevel) {
        int water = y - 1;
        if (level.method_31601(water)) {
            return false;
        }
        class_2680 state = blocks.state(x, water, z);
        if (!(state.method_26204() instanceof class_2404) || state.method_26227().method_15772() != class_3612.field_15910) {
            return false;
        }
        if (!(level.method_8393(x - 1 >> 4, z >> 4) && level.method_8393(x + 1 >> 4, z >> 4) && level.method_8393(x >> 4, z - 1 >> 4) && level.method_8393(x >> 4, z + 1 >> 4))) {
            return false;
        }
        if (BetterSusChunkModule.isWater(blocks, x - 1, water, z) && BetterSusChunkModule.isWater(blocks, x + 1, water, z) && BetterSusChunkModule.isWater(blocks, x, water, z - 1) && BetterSusChunkModule.isWater(blocks, x, water, z + 1)) {
            return false;
        }
        if (((class_1959)level.method_23753(new class_2338(x, y, z)).comp_349()).method_39927(new class_2338(x, water, z), seaLevel)) {
            return false;
        }
        return BetterSusChunkModule.calmLight(level, blocks, x, water, z);
    }

    private static boolean isWater(BlockReader blocks, int x, int y, int z) {
        return blocks.state(x, y, z).method_26227().method_15767(class_3486.field_15517);
    }

    private static boolean calmLight(class_638 level, BlockReader blocks, int x, int y, int z) {
        int radius = 5;
        for (int cx = x - radius >> 4; cx <= x + radius >> 4; ++cx) {
            for (int cz = z - radius >> 4; cz <= z + radius >> 4; ++cz) {
                if (level.method_8393(cx, cz)) continue;
                return false;
            }
        }
        for (int dx = -radius; dx <= radius; ++dx) {
            int restY = radius - Math.abs(dx);
            for (int dy = -restY; dy <= restY; ++dy) {
                int restZ = restY - Math.abs(dy);
                for (int dz = -restZ; dz <= restZ; ++dz) {
                    int emission = blocks.state(x + dx, y + dy, z + dz).method_26213();
                    if (emission == 0 || emission - Math.abs(dx) - Math.abs(dy) - Math.abs(dz) < 10) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasHole(class_638 level, class_1923 pos) {
        BlockReader blocks = new BlockReader(level);
        int bottom = level.method_31607();
        int top = bottom + level.method_31605();
        int minX = pos.method_8326();
        int minZ = pos.method_8328();
        for (int x = minX; x < minX + 16; ++x) {
            for (int z = minZ; z < minZ + 16; ++z) {
                int single = 0;
                int alongX = 0;
                int alongZ = 0;
                for (int y = bottom; y < top; ++y) {
                    if (BetterSusChunkModule.singleShaft(level, blocks, x, y, z)) {
                        ++single;
                    } else {
                        if (BetterSusChunkModule.shaftEnds(level, blocks, x, y, z, single)) {
                            return true;
                        }
                        single = 0;
                    }
                    if (BetterSusChunkModule.wideShaft(level, blocks, x, y, z, 1, 0)) {
                        ++alongX;
                    } else {
                        if (BetterSusChunkModule.shaftEnds(level, blocks, x, y, z, alongX)) {
                            return true;
                        }
                        alongX = 0;
                    }
                    if (BetterSusChunkModule.wideShaft(level, blocks, x, y, z, 0, 1)) {
                        ++alongZ;
                        continue;
                    }
                    if (BetterSusChunkModule.shaftEnds(level, blocks, x, y, z, alongZ)) {
                        return true;
                    }
                    alongZ = 0;
                }
            }
        }
        return false;
    }

    private static boolean singleShaft(class_638 level, BlockReader blocks, int x, int y, int z) {
        return BetterSusChunkModule.open(level, blocks, x, y, z) && !BetterSusChunkModule.open(level, blocks, x - 1, y, z) && !BetterSusChunkModule.open(level, blocks, x + 1, y, z) && !BetterSusChunkModule.open(level, blocks, x, y, z - 1) && !BetterSusChunkModule.open(level, blocks, x, y, z + 1);
    }

    private static boolean wideShaft(class_638 level, BlockReader blocks, int x, int y, int z, int stepX, int stepZ) {
        int i;
        for (i = 0; i < 3; ++i) {
            if (BetterSusChunkModule.open(level, blocks, x + stepX * i, y, z + stepZ * i)) continue;
            return false;
        }
        if (BetterSusChunkModule.open(level, blocks, x - stepX, y, z - stepZ) || BetterSusChunkModule.open(level, blocks, x + stepX * 3, y, z + stepZ * 3)) {
            return false;
        }
        for (i = 0; i < 3; ++i) {
            int wx = x + stepX * i;
            int wz = z + stepZ * i;
            if (!BetterSusChunkModule.open(level, blocks, wx - stepZ, y, wz - stepX) && !BetterSusChunkModule.open(level, blocks, wx + stepZ, y, wz + stepX)) continue;
            return false;
        }
        return true;
    }

    private static boolean shaftEnds(class_638 level, BlockReader blocks, int x, int y, int z, int run) {
        return run >= 2 && BetterSusChunkModule.open(level, blocks, x, y, z) && !BetterSusChunkModule.open(level, blocks, x, y - run - 1, z);
    }

    private static boolean open(class_638 level, BlockReader blocks, int x, int y, int z) {
        return blocks.state(x, y, z).method_26220((class_1922)level, class_2338.field_10980).method_1110();
    }

    private static boolean isBud(class_2680 state) {
        class_2248 block = state.method_26204();
        return block == class_2246.field_27164 || block == class_2246.field_27163 || block == class_2246.field_27162;
    }

    private static boolean isGrass(class_2680 state) {
        return state.method_27852(class_2246.field_10219) || state.method_27852(class_2246.field_10402);
    }

    private static boolean donutServer() {
        class_634 connection = class_310.method_1551().method_1562();
        if (connection == null) {
            return false;
        }
        String brand = connection.method_52790();
        return brand != null && brand.contains(DONUT_BRAND);
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_2672) {
            class_2672 chunk = (class_2672)class_25962;
            this.scheduleScan(class_1923.method_8331((int)chunk.method_11523(), (int)chunk.method_11524()));
        } else if (event.packet instanceof class_2678) {
            this.resetState();
            this.worldKey = null;
        }
    }

    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent event) {
        class_638 level = class_310.method_1551().field_1687;
        if (!(level != null && this.counted[0] && event.newState.method_27852(class_2246.field_9993) && BetterSusChunkModule.donutServer())) {
            return;
        }
        int age = (Integer)event.newState.method_11654((class_2769)class_2741.field_12517);
        if (!this.seenKelp.add(event.pos.method_10063())) {
            return;
        }
        if (age <= 0 || age >= 25) {
            return;
        }
        if (!level.method_8320(event.pos.method_10084()).method_27852(class_2246.field_10382)) {
            return;
        }
        long key = class_1923.method_8331((int)(event.pos.method_10263() >> 4), (int)(event.pos.method_10260() >> 4));
        if (!this.kelpChunks.add(key)) {
            return;
        }
        this.scheduleScan(key);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        if (level == null || mc.field_1724 == null) {
            return;
        }
        class_5321 dimension = level.method_27983();
        if (dimension != this.worldKey) {
            this.resetState();
            this.worldKey = dimension;
        }
        if (this.dirty) {
            this.dirty = false;
            this.rescanLoadedChunks();
        }
        this.pruneDistant(mc);
        if (++this.tickCounter < 20) {
            return;
        }
        this.tickCounter = 0;
        this.rebuildDisplaySets(level);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void pruneDistant(class_310 mc) {
        int reach = mc.field_1690.method_38521() + 2 * this.simDistance.getInt() + 2;
        int px = mc.field_1724.method_31477() >> 4;
        int pz = mc.field_1724.method_31479() >> 4;
        Object object = this.lock;
        synchronized (object) {
            this.distant.clear();
            LongIterator keys = this.records.keySet().iterator();
            while (keys.hasNext()) {
                long key2 = keys.nextLong();
                if (Math.abs(class_1923.method_8325((long)key2) - px) <= reach && Math.abs(class_1923.method_8332((long)key2) - pz) <= reach) continue;
                this.distant.add(key2);
            }
            for (int i = 0; i < this.distant.size(); ++i) {
                this.removeRecord(this.distant.getLong(i));
            }
        }
        this.kelpChunks.removeIf(key -> Math.abs(class_1923.method_8325((long)key) - px) > reach || Math.abs(class_1923.method_8332((long)key) - pz) > reach);
        this.seenKelp.removeIf(packed -> Math.abs((class_2338.method_10061((long)packed) >> 4) - px) > reach || Math.abs((class_2338.method_10083((long)packed) >> 4) - pz) > reach);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rebuildDisplaySets(class_638 level) {
        boolean holes = this.holeFilter.get();
        int threshold = this.sensitivity.getInt();
        int holeReach = this.holeDistance.getInt();
        int reach = 2 * this.simDistance.getInt() + 2;
        LongOpenHashSet nextPrimary = new LongOpenHashSet();
        Object object = this.lock;
        synchronized (object) {
            LongIterator keys = this.scanned.iterator();
            while (keys.hasNext()) {
                int cz;
                long key = keys.nextLong();
                int cx = class_1923.method_8325((long)key);
                if (!level.method_8393(cx, cz = class_1923.method_8332((long)key)) || this.clearCoverage.containsKey(key) || holes && this.holeNearby(cx, cz, holeReach) || this.support.get(key) < threshold || this.coveredSides(cx, cz, reach) < 3) continue;
                nextPrimary.add(key);
            }
        }
        this.primary = nextPrimary;
    }

    private boolean holeNearby(int cx, int cz, int distance) {
        for (int dx = -distance; dx <= distance; ++dx) {
            for (int dz = -distance; dz <= distance; ++dz) {
                Record record = (Record)this.records.get(class_1923.method_8331((int)(cx + dx), (int)(cz + dz)));
                if (record == null || !record.hole()) continue;
                return true;
            }
        }
        return false;
    }

    private int coveredSides(int cx, int cz, int reach) {
        int sides = 0;
        for (int[] side : SIDES) {
            if (!this.stripCleared(cx, cz, side[0], side[1], reach)) continue;
            ++sides;
        }
        return sides;
    }

    private boolean stripCleared(int cx, int cz, int dx, int dz, int reach) {
        for (int depth = 1; depth <= reach; ++depth) {
            for (int lateral = -reach; lateral <= reach; ++lateral) {
                long key = class_1923.method_8331((int)(cx + dx * depth + dz * lateral), (int)(cz + dz * depth + dx * lateral));
                if (!this.clearCoverage.containsKey(key)) continue;
                return true;
            }
        }
        return false;
    }

    public LongSet flaggedChunks() {
        return LongSets.unmodifiable((LongSet)this.primary);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (class_310.method_1551().field_1687 == null) {
            return;
        }
        this.primaryOverlay.drawTiles(this.primary, this.chunkColor.argbWithAlpha(100), this.chunkColor.argbWithAlpha(255), event.partialTick);
    }

    @Environment(value=EnvType.CLIENT)
    private record Record(boolean clears, int radius, boolean hole, int weight) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Source(boolean clears, boolean hole, int weight) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class BlockReader {
        private final class_638 level;
        private class_2818 chunk;

        private BlockReader(class_638 level) {
            this.level = level;
        }

        private class_2680 state(int x, int y, int z) {
            if (this.level.method_31601(y)) {
                return class_2246.field_10243.method_9564();
            }
            class_2818 at = this.chunkAt(x >> 4, z >> 4);
            if (at == null) {
                return class_2246.field_10243.method_9564();
            }
            class_2826 section = at.method_12006()[at.method_31602(y)];
            if (section == null) {
                return class_2246.field_10243.method_9564();
            }
            return section.method_12254(x & 0xF, y & 0xF, z & 0xF);
        }

        private boolean emptySection(int x, int y, int z) {
            class_2818 at = this.chunkAt(x >> 4, z >> 4);
            if (at == null) {
                return true;
            }
            class_2826 section = at.method_12006()[at.method_31602(y)];
            return section == null || section.method_38292();
        }

        private class_2818 chunkAt(int cx, int cz) {
            if (this.chunk != null && this.chunk.method_12004().field_9181 == cx && this.chunk.method_12004().field_9180 == cz) {
                return this.chunk;
            }
            class_2818 loaded = this.level.method_2935().method_21730(cx, cz);
            if (loaded != null) {
                this.chunk = loaded;
            }
            return loaded;
        }

        private int light(int x, int y, int z) {
            int value = Math.max(this.rawLight(class_1944.field_9282, x, y, z), this.rawLight(class_1944.field_9284, x, y, z));
            if (value >= 15 || !this.level.method_8597().comp_642()) {
                return value;
            }
            return this.openSky(x, y, z) ? 15 : value;
        }

        private int rawLight(class_1944 layer, int x, int y, int z) {
            class_2804 data = this.level.method_22336().method_15562(layer).method_15544(class_4076.method_18676((int)(x >> 4), (int)(y >> 4), (int)(z >> 4)));
            if (data == null || data.method_51380()) {
                return 0;
            }
            return data.method_12139(x & 0xF, y & 0xF, z & 0xF);
        }

        private boolean openSky(int x, int y, int z) {
            int top = this.level.method_31600();
            for (int ny = y + 1; ny <= top; ++ny) {
                if (this.emptySection(x, ny, z)) {
                    ny |= 0xF;
                    continue;
                }
                if (this.state(x, ny, z).method_26193() <= 0) continue;
                return false;
            }
            return true;
        }
    }
}

