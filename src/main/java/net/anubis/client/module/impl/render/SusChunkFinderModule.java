/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_1944
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2382
 *  net.minecraft.class_2596
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_310
 *  net.minecraft.class_3568
 *  net.minecraft.class_4076
 *  net.minecraft.class_4481
 *  net.minecraft.class_5543
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongConsumer;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.BlockUpdateEvent;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.ChunkAreas;
import net.anubis.client.util.ChunkOverlay;
import net.anubis.client.util.GeodeGlowScan;
import net.anubis.client.util.HiddenAmethyst;
import net.anubis.client.util.LevelUtil;
import net.anubis.client.util.PerformanceBudget;
import net.anubis.client.util.ServerBlockLight;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_2596;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_3568;
import net.minecraft.class_4076;
import net.minecraft.class_4481;
import net.minecraft.class_5543;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class SusChunkFinderModule
extends ClientModule {
    private static final long SCAN_BUDGET_NANOS = 2000000L;
    private static final int MIN_SCANNED_NEIGHBOURS = 3;
    private static final int NO_PACKET_SENSITIVITY = 1;
    private static final int VISIBLE_REFRESH_TICKS = 20;
    private static final int LIGHT_RESCAN_TICKS = 20;
    private static final int FULL_RESCAN_TICKS = 1200;
    private static final int VISIBLE_MIN_TICKS = 5;
    private static final int LIGHT_WAIT_LIMIT_TICKS = 200;
    private static final int PRUNE_TICKS = 200;
    private static final int PRUNE_MARGIN = 4;
    private static final int PACKET_MIN_Y = -16;
    private static final int PACKET_MAX_Y = 30;
    private static final String METHOD_CLASSIC = "Classic";
    private static final String METHOD_ANBS = "ANBS+ Scan";
    private static final int GLOW_CELLS_PER_POINT = 12;
    private static final int GEODE_TOP_Y = 47;
    private static final int MIN_EXPOSED_BUDDING = 10;
    private static final int MIN_EXPOSED_LINING = 160;
    private static final int GEODE_SETTLE_TICKS = 40;
    private static final int GEODE_CELL_SHIFT = 2;
    private static final int TORCH_LIGHT = 14;
    private static final class_2350[] SIDES = class_2350.values();
    private static final int AMETHYST = 0;
    private static final int KELP = 1;
    private static final int CAVE_VINES = 2;
    private static final int VINES = 3;
    private static final int BAMBOO = 4;
    private static final int COCOA = 5;
    private static final int BEE_NEST = 6;
    private static final int GROWTH_KINDS = 7;
    private static final int GROWN = 1;
    private static final int UNGROWN = -1;
    private static final int NO_SIGN = 0;
    private static final int BAMBOO_GROWTH_LIGHT = 9;
    private static final String DONUT_BRAND = "DonutFolia";
    private static final int STYLE_VOTES = 3;
    private final ModeSetting method = this.addSetting("method", new ModeSetting("Method", "Classic", "Classic", "ANBS+ Scan"));
    private final NumberSetting spreadRadius = this.addSetting("spread-radius", new NumberSetting("Simulation Distance", 5.0f, 2.0f, 16.0f).step(1.0f));
    private final NumberSetting sensitivity = this.addSetting("packet-sensitivity", new NumberSetting("Sensitivity", 6.0f, 1.0f, 20.0f).step(1.0f));
    private final BooleanSetting kelp = this.growthSign("growth-kelp", "Kelp");
    private final BooleanSetting caveVines = this.growthSign("growth-cave-vines", "Cave Vines");
    private final BooleanSetting vines = this.growthSign("growth-vines", "Vines");
    private final BooleanSetting bamboo = this.growthSign("growth-bamboo", "Bamboo");
    private final BooleanSetting cocoa = this.growthSign("growth-cocoa", "Cocoa");
    private final BooleanSetting beeNests = this.growthSign("growth-bee-nests", "Bee Nests");
    private final BooleanSetting[] growthSigns = new BooleanSetting[]{null, this.kelp, this.caveVines, this.vines, this.bamboo, this.cocoa, this.beeNests};
    private final ColorSetting chunkColor = this.addSetting("chunk-color", new ColorSetting("Chunk Color", 1627325444));
    private final BooleanSetting tracers = this.addSetting("tracers", new BooleanSetting("Tracers", true));
    private final BooleanSetting detectMined = this.addSetting("detect-mined-clusters", new BooleanSetting("Detect Mined Clusters", false));
    private final ColorSetting minedColor = this.addSetting("mined-color", new ColorSetting("Mined Color", 1621511679));
    private final ChunkAreas chunkAreas = new ChunkAreas();
    private final ChunkOverlay chunkOverlay = new ChunkOverlay();
    private final ChunkAreas minedAreas = new ChunkAreas();
    private final ChunkOverlay minedOverlay = new ChunkOverlay();
    private final GeodeGlowScan glow = new GeodeGlowScan();
    private final ServerBlockLight serverLight = new ServerBlockLight();
    private int darkVotes;
    private int glowVotes;
    private final Long2IntOpenHashMap[] heat = SusChunkFinderModule.perKind();
    private final Long2IntOpenHashMap[] vetoes = SusChunkFinderModule.perKind();
    private final Long2ObjectOpenHashMap<int[]> chunkGrown = new Long2ObjectOpenHashMap();
    private final Long2IntOpenHashMap chunkUngrown = new Long2IntOpenHashMap();
    private final Long2BooleanOpenHashMap chunkHasPacket = new Long2BooleanOpenHashMap();
    private final LongOpenHashSet scannedChunks = new LongOpenHashSet();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final LongOpenHashSet queued = new LongOpenHashSet();
    private final LongOpenHashSet lightDirty = new LongOpenHashSet();
    private final Long2IntOpenHashMap waitingSince = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap bestLightClusters = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap bestLightBuds = new Long2IntOpenHashMap();
    private final LongOpenHashSet geodeChunks = new LongOpenHashSet();
    private Long2IntOpenHashMap visible = new Long2IntOpenHashMap();
    private boolean visibleDirty = true;
    private int visibleAge;
    private class_638 lastLevel;
    private int appliedRadius;
    private final Long2ObjectOpenHashMap<GeodeSlice> geodeSlices = new Long2ObjectOpenHashMap();
    private final Long2IntOpenHashMap geodeWaiting = new Long2IntOpenHashMap();
    private final Long2IntOpenHashMap geodeScanTicks = new Long2IntOpenHashMap();
    private List<GeodeCluster> geodeClusters = List.of();
    private boolean geodeClustersDirty;
    private LongOpenHashSet minedChunks = new LongOpenHashSet();
    private int minedAge;
    private int ticks;

    public SusChunkFinderModule() {
        super("sus-chunk-finder", "Sus Chunk Finder", "Finds chunks with signs of player activity from grown amethyst, kelp, vines, bamboo, cocoa and bee nests.", ModuleCategory.BASE_RAID);
        this.method.onChange(this::rescan);
        this.sensitivity.onChange(() -> {
            this.visibleDirty = true;
        });
        for (BooleanSetting sign : this.growthSigns) {
            if (sign == null) continue;
            sign.visibleWhen(() -> this.method.is(METHOD_CLASSIC));
            sign.onChange(this::rescan);
        }
        this.detectMined.onChange(this::restartGeodes);
        this.minedColor.visibleWhen(this.detectMined::get);
    }

    private BooleanSetting growthSign(String id, String name) {
        return this.addSetting(id, new BooleanSetting(name, true));
    }

    private static Long2IntOpenHashMap[] perKind() {
        Long2IntOpenHashMap[] maps = new Long2IntOpenHashMap[7];
        for (int kind = 0; kind < 7; ++kind) {
            maps[kind] = new Long2IntOpenHashMap();
        }
        return maps;
    }

    private int countedKinds() {
        int kinds = 1;
        for (int kind = 0; kind < 7; ++kind) {
            if (this.growthSigns[kind] == null || !this.growthSigns[kind].get()) continue;
            kinds |= 1 << kind;
        }
        return kinds;
    }

    private void rescan() {
        this.clearAll();
        this.queueLoadedChunks();
    }

    private void restartGeodes() {
        this.clearGeodes();
        if (this.detectMined.get()) {
            this.queueLoadedChunks();
        }
    }

    @Override
    protected void onEnable() {
        this.clearAll();
        this.resetLight();
        this.lastLevel = class_310.method_1551().field_1687;
        this.queueLoadedChunks();
    }

    @Override
    protected void onDisable() {
        this.clearAll();
        this.resetLight();
        this.lastLevel = null;
    }

    private void resetLight() {
        this.serverLight.clear();
        this.darkVotes = 0;
        this.glowVotes = 0;
    }

    private void clearAll() {
        for (int kind = 0; kind < 7; ++kind) {
            this.heat[kind].clear();
            this.vetoes[kind].clear();
        }
        this.chunkGrown.clear();
        this.chunkUngrown.clear();
        this.chunkHasPacket.clear();
        this.scannedChunks.clear();
        this.queue.clear();
        this.queued.clear();
        this.lightDirty.clear();
        this.waitingSince.clear();
        this.bestLightClusters.clear();
        this.bestLightBuds.clear();
        this.geodeChunks.clear();
        this.visible = new Long2IntOpenHashMap();
        this.visibleDirty = true;
        this.appliedRadius = this.spreadRadius.getInt();
        this.clearGeodes();
    }

    private void clearGeodes() {
        this.geodeSlices.clear();
        this.geodeWaiting.clear();
        this.geodeScanTicks.clear();
        this.geodeClusters = List.of();
        this.geodeClustersDirty = false;
        this.minedChunks = new LongOpenHashSet();
        this.minedAge = 0;
    }

    private void queueLoadedChunks() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        int radius = (Integer)mc.field_1690.method_42503().method_41753() + 1;
        class_1923 center = mc.field_1724.method_31476();
        for (int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for (int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                if (!LevelUtil.isLoaded(mc.field_1687, x, z)) continue;
                this.enqueue(class_1923.method_8331((int)x, (int)z));
            }
        }
    }

    private void enqueue(long key) {
        if (this.queued.add(key)) {
            this.queue.enqueue(key);
        }
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        class_638 level = class_310.method_1551().field_1687;
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_2672) {
            class_2672 chunk = (class_2672)class_25962;
            if (level != null) {
                this.serverLight.onChunk(level, chunk.method_11523(), chunk.method_11524(), chunk.method_38599());
            }
            this.enqueue(class_1923.method_8331((int)chunk.method_11523(), (int)chunk.method_11524()));
            this.markLightDirty(chunk.method_11523(), chunk.method_11524());
        } else {
            class_25962 = event.packet;
            if (class_25962 instanceof class_2676) {
                class_2676 light = (class_2676)class_25962;
                if (level != null) {
                    this.serverLight.onLight(level, light.method_11558(), light.method_11554(), light.method_38600());
                }
                this.markLightDirty(light.method_11558(), light.method_11554());
            }
        }
    }

    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent event) {
        this.serverLight.onBlockChanged(event.pos);
    }

    private void markLightDirty(int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                long key = class_1923.method_8331((int)(chunkX + dx), (int)(chunkZ + dz));
                if (!this.geodeChunks.contains(key) && this.scannedChunks.contains(key)) continue;
                this.lightDirty.add(key);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        long deadline;
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        if (level != this.lastLevel) {
            this.clearAll();
            this.resetLight();
            this.lastLevel = level;
            this.queueLoadedChunks();
        }
        if (level == null) {
            return;
        }
        ++this.ticks;
        if (this.spreadRadius.getInt() != this.appliedRadius) {
            this.clearAll();
            this.queueLoadedChunks();
        }
        if (this.ticks % 1200 == 0) {
            this.lightDirty.clear();
            this.queueLoadedChunks();
        } else if (this.ticks % 20 == 0 && !this.lightDirty.isEmpty()) {
            LongIterator longIterator = this.lightDirty.iterator();
            while (longIterator.hasNext()) {
                long key = (Long)longIterator.next();
                if (!LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key))) continue;
                this.enqueue(key);
            }
            this.lightDirty.clear();
        }
        boolean optimized = PerformanceModule.optimizationsEnabled();
        long started = System.nanoTime();
        long l = deadline = optimized ? PerformanceBudget.acquire(2000000L) : started + 2000000L;
        while (!this.queue.isEmpty() && System.nanoTime() < deadline) {
            long key = this.queue.dequeueLong();
            this.queued.remove(key);
            int cx = class_1923.method_8325((long)key);
            int cz = class_1923.method_8332((long)key);
            if (!LevelUtil.isLoaded(level, cx, cz)) {
                this.waitingSince.remove(key);
                continue;
            }
            if (this.method.is(METHOD_ANBS) && !this.lightSettled(level, key)) {
                this.lightDirty.add(key);
                continue;
            }
            this.waitingSince.remove(key);
            this.updateChunk(key, level.method_8497(cx, cz), level);
            this.scannedChunks.add(key);
            this.visibleDirty = true;
        }
        if (optimized) {
            PerformanceBudget.record(started);
        }
        ++this.visibleAge;
        if (this.visibleDirty && this.visibleAge >= 5 || this.visibleAge >= 20) {
            this.visible = this.computeVisible(level);
            this.visibleDirty = false;
            this.visibleAge = 0;
        }
        if (this.ticks % 200 == 0) {
            this.pruneFar(class_310.method_1551());
        }
        if (this.detectMined.get() && ++this.minedAge >= 20) {
            this.minedChunks = this.computeMined(level);
            this.minedAge = 0;
        }
    }

    private boolean lightSettled(class_638 level, long key) {
        if (!this.waitingSince.containsKey(key)) {
            this.waitingSince.put(key, this.ticks);
        }
        if (this.ticks - this.waitingSince.get(key) >= 200) {
            return true;
        }
        class_3568 engine = level.method_22336();
        if (engine.method_15518()) {
            return false;
        }
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (!LevelUtil.isLoaded(level, cx + dx, cz + dz)) {
                    return false;
                }
                if (engine.method_62874(class_4076.method_51687((int)(cx + dx), (int)(cz + dz)))) continue;
                return false;
            }
        }
        return true;
    }

    private void pruneFar(class_310 mc) {
        if (mc.field_1724 == null) {
            return;
        }
        int reach = mc.field_1690.method_38521() + 2 * this.spreadRadius.getInt() + 4;
        int px = mc.field_1724.method_31477() >> 4;
        int pz = mc.field_1724.method_31479() >> 4;
        LongArrayList far = new LongArrayList();
        LongIterator longIterator = this.scannedChunks.iterator();
        while (longIterator.hasNext()) {
            long key = (Long)longIterator.next();
            if (Math.max(Math.abs(class_1923.method_8325((long)key) - px), Math.abs(class_1923.method_8332((long)key) - pz)) <= reach) continue;
            far.add(key);
        }
        for (int i = 0; i < far.size(); ++i) {
            this.forget(far.getLong(i));
        }
        if (!far.isEmpty()) {
            this.visibleDirty = true;
        }
        if (mc.field_1687 != null) {
            this.serverLight.forgetUnloaded(mc.field_1687);
        }
    }

    private void forget(long key) {
        int r = this.spreadRadius.getInt();
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        this.withdraw(cx, cz, r, (int[])this.chunkGrown.remove(key), this.chunkUngrown.remove(key));
        this.chunkHasPacket.remove(key);
        this.scannedChunks.remove(key);
        this.bestLightClusters.remove(key);
        this.bestLightBuds.remove(key);
        this.geodeChunks.remove(key);
        this.waitingSince.remove(key);
        this.lightDirty.remove(key);
    }

    private void updateChunk(long key, class_2818 chunk, class_638 level) {
        int r = this.spreadRadius.getInt();
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        this.withdraw(cx, cz, r, (int[])this.chunkGrown.remove(key), this.chunkUngrown.remove(key));
        this.chunkHasPacket.put(key, SusChunkFinderModule.hasBlocksInRange(chunk, -16, 30));
        if (SusChunkFinderModule.hasLining(chunk)) {
            this.geodeChunks.add(key);
        } else {
            this.geodeChunks.remove(key);
        }
        ChunkScore raw = this.scoreChunk(chunk, level);
        int bestClusters = this.bestLightClusters.get(key);
        int bestBuds = this.bestLightBuds.get(key);
        if (raw.lightClusters() > bestClusters || raw.lightClusters() == bestClusters && raw.lightBuds() > bestBuds) {
            bestClusters = raw.lightClusters();
            bestBuds = raw.lightBuds();
            this.bestLightClusters.put(key, bestClusters);
            this.bestLightBuds.put(key, bestBuds);
        }
        int[] grown = raw.grown();
        grown[0] = grown[0] + bestClusters;
        int ungrown = bestBuds > 0 ? raw.ungrown() | 1 : raw.ungrown();
        this.chunkGrown.put(key, (Object)grown);
        this.chunkUngrown.put(key, ungrown);
        this.spread(cx, cz, r, grown, ungrown);
        if (this.detectMined.get()) {
            this.collectGeode(key, chunk, level);
        }
    }

    private void spread(int cx, int cz, int r, int[] grown, int ungrown) {
        for (int kind = 0; kind < 7; ++kind) {
            Long2IntOpenHashMap kindHeat = this.heat[kind];
            Long2IntOpenHashMap kindVetoes = this.vetoes[kind];
            int amount = grown[kind];
            if (amount > 0) {
                SusChunkFinderModule.forArea(cx, cz, r, area -> kindHeat.addTo(area, amount));
            }
            if ((ungrown & 1 << kind) == 0) continue;
            SusChunkFinderModule.forArea(cx, cz, r, area -> kindVetoes.addTo(area, 1));
        }
    }

    private void withdraw(int cx, int cz, int r, int[] grown, int ungrown) {
        for (int kind = 0; kind < 7; ++kind) {
            int amount;
            Long2IntOpenHashMap kindHeat = this.heat[kind];
            Long2IntOpenHashMap kindVetoes = this.vetoes[kind];
            int n = amount = grown == null ? 0 : grown[kind];
            if (amount > 0) {
                SusChunkFinderModule.forArea(cx, cz, r, area -> SusChunkFinderModule.subtract(kindHeat, area, amount));
            }
            if ((ungrown & 1 << kind) == 0) continue;
            SusChunkFinderModule.forArea(cx, cz, r, area -> SusChunkFinderModule.subtract(kindVetoes, area, 1));
        }
    }

    private static void forArea(int cx, int cz, int r, LongConsumer action) {
        for (int dx = -r; dx <= r; ++dx) {
            for (int dz = -r; dz <= r; ++dz) {
                action.accept(class_1923.method_8331((int)(cx + dx), (int)(cz + dz)));
            }
        }
    }

    private static void subtract(Long2IntOpenHashMap map, long key, int amount) {
        int left = map.get(key) - amount;
        if (left > 0) {
            map.put(key, left);
        } else {
            map.remove(key);
        }
    }

    private static boolean hasLining(class_2818 chunk) {
        for (class_2826 section : chunk.method_12006()) {
            if (section == null || section.method_38292() || !section.method_19523(SusChunkFinderModule::isLining)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasBlocksInRange(class_2818 chunk, int minY, int maxY) {
        class_2826[] sections = chunk.method_12006();
        int minSectionY = chunk.method_32891();
        for (int i = 0; i < sections.length; ++i) {
            int baseY;
            class_2826 section = sections[i];
            if (section == null || section.method_38292() || (baseY = minSectionY + i << 4) + 15 < minY || baseY > maxY) continue;
            for (int y = 0; y < 16; ++y) {
                int worldY = baseY + y;
                if (worldY < minY || worldY > maxY) continue;
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        if (section.method_12254(x, y, z).method_26215()) continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChunkScore scoreChunk(class_2818 chunk, class_638 level) {
        int[] grown = new int[7];
        if (this.method.is(METHOD_ANBS)) {
            grown[0] = this.glow.scan(level, chunk, null) / 12;
            return new ChunkScore(grown, 0, 0, 0);
        }
        class_2826[] sections = chunk.method_12006();
        int minSectionY = chunk.method_32891();
        class_1923 pos = chunk.method_12004();
        int counted = this.countedKinds();
        int ungrown = 0;
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (int i = 0; i < sections.length; ++i) {
            class_2826 section = sections[i];
            if (section == null || section.method_38292() || !section.method_19523(state -> SusChunkFinderModule.isCounted(state, counted))) continue;
            int baseY = minSectionY + i << 4;
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < 16; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        class_2680 state2 = section.method_12254(x, y, z);
                        int kind = SusChunkFinderModule.growthKind(state2);
                        if (kind < 0 || (counted & 1 << kind) == 0) continue;
                        cursor.method_10103(pos.method_8326() + x, baseY + y, pos.method_8328() + z);
                        int sign = SusChunkFinderModule.readSign(level, cursor, state2, kind);
                        if (sign == 1) {
                            int n = kind;
                            grown[n] = grown[n] + 1;
                            continue;
                        }
                        if (sign != -1) continue;
                        ungrown |= 1 << kind;
                    }
                }
            }
        }
        HiddenAmethyst.Reading hidden = HiddenAmethyst.read(level, chunk, this.serverLight, this.hiddenSolids());
        this.darkVotes += hidden.darkVotes();
        this.glowVotes += hidden.glowVotes();
        return new ChunkScore(grown, ungrown, hidden.clusters(), hidden.buds());
    }

    private Boolean hiddenSolids() {
        if (SusChunkFinderModule.onDonut()) {
            return Boolean.TRUE;
        }
        if (this.darkVotes >= 3 && this.darkVotes > this.glowVotes) {
            return Boolean.TRUE;
        }
        if (this.glowVotes >= 3 && this.glowVotes > this.darkVotes) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static boolean onDonut() {
        class_634 connection = class_310.method_1551().method_1562();
        String brand = connection == null ? null : connection.method_52790();
        return brand != null && brand.contains(DONUT_BRAND);
    }

    private static boolean isCounted(class_2680 state, int counted) {
        int kind = SusChunkFinderModule.growthKind(state);
        return kind >= 0 && (counted & 1 << kind) != 0;
    }

    private static int growthKind(class_2680 state) {
        class_2248 block = state.method_26204();
        if (block == class_2246.field_27161 || SusChunkFinderModule.isBud(state)) {
            return 0;
        }
        if (block == class_2246.field_9993) {
            return 1;
        }
        if (block == class_2246.field_28675) {
            return 2;
        }
        if (block == class_2246.field_10597) {
            return 3;
        }
        if (block == class_2246.field_10211) {
            return 4;
        }
        if (block == class_2246.field_10302) {
            return 5;
        }
        if (block == class_2246.field_20421) {
            return 6;
        }
        return -1;
    }

    private static int readSign(class_638 level, class_2338.class_2339 at, class_2680 state, int kind) {
        return switch (kind) {
            case 0 -> {
                if (state.method_27852(class_2246.field_27161)) {
                    yield 1;
                }
                if (!state.method_28498((class_2769)class_2741.field_12525)) {
                    yield 0;
                }
                class_2350 facing = (class_2350)state.method_11654((class_2769)class_2741.field_12525);
                if (level.method_8320((class_2338)at.method_10098(facing.method_10153())).method_27852(class_2246.field_27160)) {
                    yield -1;
                }
                yield 1;
            }
            case 1 -> {
                if ((Integer)state.method_11654((class_2769)class_2741.field_12517) == 25 || !level.method_8320((class_2338)at.method_10098(class_2350.field_11036)).method_27852(class_2246.field_10382)) {
                    yield 1;
                }
                yield -1;
            }
            case 2 -> {
                if ((Integer)state.method_11654((class_2769)class_2741.field_12517) == 25 || !level.method_8320((class_2338)at.method_10098(class_2350.field_11033)).method_26215()) {
                    yield 1;
                }
                yield -1;
            }
            case 3 -> {
                class_2680 below = level.method_8320((class_2338)at.method_10098(class_2350.field_11033));
                if (below.method_27852(class_2246.field_10597)) {
                    yield 0;
                }
                if (!below.method_26215()) {
                    yield 1;
                }
                if (SusChunkFinderModule.hangsFromSide(state)) {
                    yield -1;
                }
                yield 1;
            }
            case 4 -> SusChunkFinderModule.bambooSign(level, at, state);
            case 5 -> {
                if ((Integer)state.method_11654((class_2769)class_2741.field_12556) == 2) {
                    yield 1;
                }
                yield -1;
            }
            case 6 -> {
                if ((Integer)state.method_11654((class_2769)class_4481.field_20420) == 5) {
                    yield 1;
                }
                yield -1;
            }
            default -> 0;
        };
    }

    private static boolean hangsFromSide(class_2680 vine) {
        return (Boolean)vine.method_11654((class_2769)class_2741.field_12489) != false || (Boolean)vine.method_11654((class_2769)class_2741.field_12487) != false || (Boolean)vine.method_11654((class_2769)class_2741.field_12540) != false || (Boolean)vine.method_11654((class_2769)class_2741.field_12527) != false;
    }

    private static int bambooSign(class_638 level, class_2338.class_2339 at, class_2680 state) {
        int height;
        int x = at.method_10263();
        int y = at.method_10264();
        int z = at.method_10260();
        class_2680 above = level.method_8320((class_2338)at.method_10098(class_2350.field_11036));
        if (above.method_27852(class_2246.field_10211)) {
            return 0;
        }
        if ((Integer)state.method_11654((class_2769)class_2741.field_12549) == 1) {
            return 1;
        }
        if (!above.method_26215() || level.method_22335((class_2338)at, 0) < 9) {
            return 0;
        }
        for (height = 1; height < 16 && level.method_8320((class_2338)at.method_10103(x, y - height, z)).method_27852(class_2246.field_10211); ++height) {
        }
        return height < 16 ? -1 : 0;
    }

    private void collectGeode(long key, class_2818 chunk, class_638 level) {
        int baseY;
        this.geodeScanTicks.put(key, this.ticks);
        if (this.geodeSlices.remove(key) != null) {
            this.geodeClustersDirty = true;
        }
        this.geodeWaiting.remove(key);
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                long near = class_1923.method_8331((int)(cx + dx), (int)(cz + dz));
                if (dx == 0 && dz == 0 || (this.geodeWaiting.get(near) & SusChunkFinderModule.waitBit(-dx, -dz)) == 0) continue;
                this.enqueue(near);
            }
        }
        if (!LevelUtil.isOverworld(level)) {
            return;
        }
        class_2826[] sections = chunk.method_12006();
        int minSectionY = chunk.method_32891();
        int minX = cx << 4;
        int minZ = cz << 4;
        LongArrayList lining = new LongArrayList();
        LongArrayList budding = new LongArrayList();
        class_2338.class_2339 cursor = new class_2338.class_2339();
        int waiting = 0;
        for (int i = 0; i < sections.length && (baseY = minSectionY + i << 4) <= 47; ++i) {
            class_2826 section = sections[i];
            if (section == null || section.method_38292() || !section.method_19523(SusChunkFinderModule::isLining)) continue;
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        class_2680 state = section.method_12254(x, y, z);
                        if (!SusChunkFinderModule.isLining(state)) continue;
                        int wx = minX + x;
                        int wy = baseY + y;
                        int wz = minZ + z;
                        boolean exposed = false;
                        int missing = 0;
                        for (class_2350 side : SIDES) {
                            boolean inside;
                            int nx = wx + side.method_10148();
                            int ny = wy + side.method_10164();
                            int nz = wz + side.method_10165();
                            if (level.method_31601(ny)) continue;
                            int ncx = nx >> 4;
                            int ncz = nz >> 4;
                            boolean bl = inside = ncx == cx && ncz == cz;
                            if (!inside && !LevelUtil.isLoaded(level, ncx, ncz)) {
                                missing |= SusChunkFinderModule.waitBit(ncx - cx, ncz - cz);
                                continue;
                            }
                            cursor.method_10103(nx, ny, nz);
                            if (!SusChunkFinderModule.isOpen(inside ? chunk.method_8320((class_2338)cursor) : level.method_8320((class_2338)cursor))) continue;
                            exposed = true;
                            break;
                        }
                        if (!exposed) {
                            waiting |= missing;
                            continue;
                        }
                        long packed = class_2338.method_10064((int)wx, (int)wy, (int)wz);
                        lining.add(packed);
                        if (!state.method_27852(class_2246.field_27160)) continue;
                        budding.add(packed);
                    }
                }
            }
        }
        if (waiting != 0) {
            this.geodeWaiting.put(key, waiting);
        }
        if (lining.isEmpty()) {
            return;
        }
        this.geodeSlices.put(key, (Object)new GeodeSlice(lining.toLongArray(), budding.toLongArray()));
        this.geodeClustersDirty = true;
    }

    private static int waitBit(int dx, int dz) {
        return 1 << (dx + 1) * 3 + dz + 1;
    }

    private LongOpenHashSet computeMined(class_638 level) {
        if (this.geodeSlices.keySet().removeIf(key -> !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)))) {
            this.geodeClustersDirty = true;
        }
        this.geodeWaiting.keySet().removeIf(key -> !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)));
        this.geodeScanTicks.keySet().removeIf(key -> !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)));
        if (this.geodeClustersDirty) {
            this.geodeClusters = this.clusterGeodes();
            this.geodeClustersDirty = false;
        }
        LongOpenHashSet mined = new LongOpenHashSet();
        for (GeodeCluster cluster : this.geodeClusters) {
            boolean bl = cluster.budding() > 0 ? cluster.budding() >= 10 : cluster.lining().length >= 160;
            boolean large = bl;
            if (!large || !this.settled(level, cluster) || !SusChunkFinderModule.unlit(level, cluster.lining())) continue;
            mined.add(cluster.chunk());
        }
        return mined;
    }

    private List<GeodeCluster> clusterGeodes() {
        long cell;
        Long2ObjectOpenHashMap cells = new Long2ObjectOpenHashMap();
        LongOpenHashSet budding = new LongOpenHashSet();
        for (GeodeSlice slice : this.geodeSlices.values()) {
            for (long packed : slice.lining()) {
                cell = class_2338.method_10064((int)(class_2338.method_10061((long)packed) >> 2), (int)(class_2338.method_10071((long)packed) >> 2), (int)(class_2338.method_10083((long)packed) >> 2));
                LongArrayList members = (LongArrayList)cells.get(cell);
                if (members == null) {
                    members = new LongArrayList();
                    cells.put(cell, (Object)members);
                }
                members.add(packed);
            }
            for (long packed : slice.budding()) {
                budding.add(packed);
            }
        }
        ArrayList<GeodeCluster> clusters = new ArrayList<GeodeCluster>();
        LongOpenHashSet seen = new LongOpenHashSet();
        LongArrayFIFOQueue frontier = new LongArrayFIFOQueue();
        LongIterator longIterator = cells.keySet().iterator();
        while (longIterator.hasNext()) {
            long start = (Long)longIterator.next();
            if (!seen.add(start)) continue;
            LongArrayList lining = new LongArrayList();
            frontier.enqueue(start);
            while (!frontier.isEmpty()) {
                cell = frontier.dequeueLong();
                lining.addAll((LongList)cells.get(cell));
                int x = class_2338.method_10061((long)cell);
                int y = class_2338.method_10071((long)cell);
                int z = class_2338.method_10083((long)cell);
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dy = -1; dy <= 1; ++dy) {
                        for (int dz = -1; dz <= 1; ++dz) {
                            long next = class_2338.method_10064((int)(x + dx), (int)(y + dy), (int)(z + dz));
                            if (!cells.containsKey(next) || !seen.add(next)) continue;
                            frontier.enqueue(next);
                        }
                    }
                }
            }
            clusters.add(SusChunkFinderModule.toCluster(lining.toLongArray(), budding));
        }
        return clusters;
    }

    private static GeodeCluster toCluster(long[] lining, LongOpenHashSet budding) {
        int count = 0;
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        long sumX = 0L;
        long sumZ = 0L;
        for (long packed : lining) {
            if (budding.contains(packed)) {
                ++count;
            }
            int x = class_2338.method_10061((long)packed);
            int z = class_2338.method_10083((long)packed);
            minX = Math.min(minX, x);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxZ = Math.max(maxZ, z);
            sumX += (long)x;
            sumZ += (long)z;
        }
        int centerX = (int)Math.floorDiv(sumX, lining.length);
        int centerZ = (int)Math.floorDiv(sumZ, lining.length);
        return new GeodeCluster(lining, count, minX, minZ, maxX, maxZ, class_1923.method_8331((int)(centerX >> 4), (int)(centerZ >> 4)));
    }

    private boolean settled(class_638 level, GeodeCluster cluster) {
        for (int cx = cluster.minX() - 1 >> 4; cx <= cluster.maxX() + 1 >> 4; ++cx) {
            for (int cz = cluster.minZ() - 1 >> 4; cz <= cluster.maxZ() + 1 >> 4; ++cz) {
                long key = class_1923.method_8331((int)cx, (int)cz);
                if (!LevelUtil.isLoaded(level, cx, cz) || !this.geodeScanTicks.containsKey(key)) {
                    return false;
                }
                if (this.ticks - this.geodeScanTicks.get(key) >= 40) continue;
                return false;
            }
        }
        return true;
    }

    private static boolean unlit(class_638 level, long[] lining) {
        class_2338.class_2339 cursor = new class_2338.class_2339();
        Long2IntOpenHashMap peaks = new Long2IntOpenHashMap();
        for (long packed : lining) {
            int x = class_2338.method_10061((long)packed);
            int y = class_2338.method_10071((long)packed);
            int z = class_2338.method_10083((long)packed);
            for (class_2350 side : SIDES) {
                cursor.method_10103(x + side.method_10148(), y + side.method_10164(), z + side.method_10165());
                if (SusChunkFinderModule.isAmethystGrowth(level.method_8320((class_2338)cursor))) {
                    return false;
                }
                int light = level.method_8314(class_1944.field_9282, (class_2338)cursor);
                if (light <= 0 || SusChunkFinderModule.sourceLight(level, (class_2338)cursor, light, peaks) == 14) continue;
                return false;
            }
        }
        return true;
    }

    private static int sourceLight(class_638 level, class_2338 start, int light, Long2IntOpenHashMap peaks) {
        int peak;
        LongArrayList path = new LongArrayList();
        class_2338.class_2339 at = start.method_25503();
        class_2338.class_2339 probe = new class_2338.class_2339();
        while (true) {
            long key;
            if (peaks.containsKey(key = at.method_10063())) {
                peak = peaks.get(key);
                break;
            }
            path.add(key);
            class_2350 uphill = null;
            int best = light;
            for (class_2350 side : SIDES) {
                int value = level.method_8314(class_1944.field_9282, (class_2338)probe.method_25505((class_2382)at, side));
                if (value <= best) continue;
                best = value;
                uphill = side;
            }
            if (uphill == null) {
                peak = light;
                break;
            }
            at.method_10098(uphill);
            light = best;
        }
        for (int i = 0; i < path.size(); ++i) {
            peaks.put(path.getLong(i), peak);
        }
        return peak;
    }

    private static boolean isLining(class_2680 state) {
        return state.method_27852(class_2246.field_27159) || state.method_27852(class_2246.field_27160);
    }

    private static boolean isOpen(class_2680 state) {
        return class_5543.method_31626((class_2680)state) || SusChunkFinderModule.isAmethystGrowth(state);
    }

    private static boolean isBud(class_2680 state) {
        class_2248 block = state.method_26204();
        return block == class_2246.field_27164 || block == class_2246.field_27163 || block == class_2246.field_27162;
    }

    private static boolean isAmethystGrowth(class_2680 state) {
        return state.method_27852(class_2246.field_27161) || SusChunkFinderModule.isBud(state);
    }

    private Long2IntOpenHashMap computeVisible(class_638 level) {
        Long2IntOpenHashMap total = new Long2IntOpenHashMap();
        for (int kind = 0; kind < 7; ++kind) {
            Long2IntOpenHashMap kindVetoes = this.vetoes[kind];
            for (Long2IntMap.Entry entry : this.heat[kind].long2IntEntrySet()) {
                if (kindVetoes.containsKey(entry.getLongKey())) continue;
                total.addTo(entry.getLongKey(), entry.getIntValue());
            }
        }
        Long2IntOpenHashMap eligible = new Long2IntOpenHashMap();
        for (Long2IntMap.Entry entry : total.long2IntEntrySet()) {
            int cz;
            long key = entry.getLongKey();
            int cx = class_1923.method_8325((long)key);
            if (!LevelUtil.isLoaded(level, cx, cz = class_1923.method_8332((long)key)) || this.scannedNeighbours(cx, cz) < 3) continue;
            eligible.put(key, entry.getIntValue());
        }
        int withPacket = this.sensitivity.getInt();
        int reach = this.appliedRadius;
        Long2IntOpenHashMap kept = new Long2IntOpenHashMap();
        for (Long2IntMap.Entry entry : eligible.long2IntEntrySet()) {
            long key = entry.getLongKey();
            int value = entry.getIntValue();
            boolean reached = value >= withPacket || !this.chunkHasPacket.getOrDefault(key, true) && this.amethystHeat(key) >= 1;
            if (!reached || !SusChunkFinderModule.isPeak(eligible, key, value, reach)) continue;
            kept.put(key, value);
        }
        return kept;
    }

    private int amethystHeat(long key) {
        return this.vetoes[0].containsKey(key) ? 0 : this.heat[0].get(key);
    }

    private static boolean isPeak(Long2IntOpenHashMap field, long key, int value, int reach) {
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        for (int dx = -reach; dx <= reach; ++dx) {
            for (int dz = -reach; dz <= reach; ++dz) {
                if (dx == 0 && dz == 0 || field.get(class_1923.method_8331((int)(cx + dx), (int)(cz + dz))) <= value) continue;
                return false;
            }
        }
        return true;
    }

    private int scannedNeighbours(int cx, int cz) {
        int count = 0;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0 || !this.scannedChunks.contains(class_1923.method_8331((int)(cx + dx), (int)(cz + dz)))) continue;
                ++count;
            }
        }
        return count;
    }

    public LongSet flaggedChunks() {
        return LongSets.unmodifiable((LongSet)this.visible.keySet());
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (class_310.method_1551().field_1687 == null) {
            return;
        }
        this.chunkOverlay.draw(this.visible.keySet(), this.chunkColor.argb(), event.partialTick);
        if (this.tracers.get()) {
            this.chunkAreas.drawTracers(this.visible.keySet(), 63.0, class_9848.method_61334((int)this.chunkColor.argb()));
        }
        this.minedOverlay.draw((LongSet)this.minedChunks, this.minedColor.argb(), event.partialTick);
        if (this.tracers.get()) {
            this.minedAreas.drawTracers((LongSet)this.minedChunks, 63.0, class_9848.method_61334((int)this.minedColor.argb()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record ChunkScore(int[] grown, int ungrown, int lightClusters, int lightBuds) {
    }

    @Environment(value=EnvType.CLIENT)
    private record GeodeSlice(long[] lining, long[] budding) {
    }

    @Environment(value=EnvType.CLIENT)
    private record GeodeCluster(long[] lining, int budding, int minX, int minZ, int maxX, int maxZ, long chunk) {
    }
}

