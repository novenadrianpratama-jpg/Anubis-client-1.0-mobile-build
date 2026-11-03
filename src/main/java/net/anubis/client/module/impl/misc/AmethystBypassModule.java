/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_1923
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2626
 *  net.minecraft.class_2637
 *  net.minecraft.class_2672
 *  net.minecraft.class_2676
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2841
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package net.anubis.client.module.impl.misc;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.Arrays;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.AmethystLight;
import net.anubis.client.util.GeodeGlowScan;
import net.anubis.client.util.LevelUtil;
import net.anubis.client.util.PerformanceBudget;
import net.anubis.client.util.TracerEngine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2841;
import net.minecraft.class_310;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class AmethystBypassModule
extends ClientModule {
    private static final int COLOR = -3636481;
    private static final float LINE_WIDTH = 1.5f;
    private static final int RESCAN_TICKS = 20;
    private static final long BUDGET_NANOS = 2000000L;
    private static final String ANBS_SCAN = "ANBS+ Scan";
    private static final int MAX_GLOW_MARKS = 200;
    private static final int FULL_REFRESH_TICKS = 600;
    private final ModeSetting method = this.addSetting("method", new ModeSetting("Method", "Bypass", "Bypass", "Light", "ANBS+ Scan"));
    private final NumberSetting minCells = this.addSetting("min-cells", new NumberSetting("Min Cells", 12.0f, 1.0f, 100.0f).step(1.0f));
    private final BooleanSetting tracer = this.addSetting("tracer", new BooleanSetting("Tracer", false));
    private final ArrayDeque<class_1923> queue = new ArrayDeque();
    private final LongOpenHashSet found = new LongOpenHashSet();
    private final LongOpenHashSet bypassScanned = new LongOpenHashSet();
    private final Long2ObjectOpenHashMap<class_2338> bypassMarkers = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<long[]> chunkHits = new Long2ObjectOpenHashMap();
    private final LongOpenHashSet lightScanned = new LongOpenHashSet();
    private final LongOpenHashSet chunkFound = new LongOpenHashSet();
    private boolean hitsChanged;
    private int sinceRefresh;
    private final GeodeGlowScan glow = new GeodeGlowScan();
    private final LongArrayList glowCells = new LongArrayList();
    private long[] hits = new long[0];
    private int tick;

    public AmethystBypassModule() {
        super("amethyst-bypass", "Amethyst Bypass", "Shows amethyst geodes hidden by anti-xray.", ModuleCategory.BASE_RAID);
        this.method.onChange(this::restart);
        this.minCells.visibleWhen(() -> this.method.is(ANBS_SCAN));
    }

    @Override
    protected void onEnable() {
        this.restart();
    }

    @Override
    protected void onDisable() {
        this.restart();
    }

    private void restart() {
        this.queue.clear();
        this.found.clear();
        this.bypassScanned.clear();
        this.bypassMarkers.clear();
        this.chunkHits.clear();
        this.lightScanned.clear();
        this.hitsChanged = false;
        this.sinceRefresh = 0;
        this.hits = new long[0];
        this.tick = 0;
    }

    @EventHandler(stage=Stage.POST)
    public void onTick(TickEvent event) {
        long deadline;
        class_310 mc = class_310.method_1551();
        class_638 level = mc.field_1687;
        if (level == null || mc.field_1724 == null) {
            return;
        }
        boolean bypass = this.method.is("Bypass");
        if (this.queue.isEmpty()) {
            if (++this.tick < 20) {
                return;
            }
            this.tick = 0;
            class_1923 center = mc.field_1724.method_31476();
            int radius = (Integer)mc.field_1690.method_42503().method_41753();
            if (bypass) {
                this.bypassScanned.removeIf(key -> !AmethystBypassModule.inRange(key, center, radius) || !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)));
                this.bypassMarkers.keySet().removeIf(key -> !this.bypassScanned.contains(key));
                for (x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
                    for (z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                        long key2 = class_1923.method_8331((int)x, (int)z);
                        if (!LevelUtil.isLoaded(level, x, z) || this.bypassScanned.contains(key2)) continue;
                        this.queue.add(new class_1923(x, z));
                    }
                }
            } else {
                if (++this.sinceRefresh * 20 >= 600) {
                    this.sinceRefresh = 0;
                    this.lightScanned.clear();
                }
                this.hitsChanged |= this.chunkHits.keySet().removeIf(key -> !AmethystBypassModule.inRange(key, center, radius) || !LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key)));
                this.lightScanned.removeIf(key -> !AmethystBypassModule.inRange(key, center, radius));
                for (x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
                    for (z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                        if (!LevelUtil.isLoaded(level, x, z) || this.lightScanned.contains(class_1923.method_8331((int)x, (int)z))) continue;
                        this.queue.add(new class_1923(x, z));
                    }
                }
            }
        }
        boolean optimized = PerformanceModule.optimizationsEnabled();
        long started = System.nanoTime();
        long l = deadline = optimized ? PerformanceBudget.acquire(2000000L) : started + 2000000L;
        while (!this.queue.isEmpty() && System.nanoTime() < deadline) {
            class_1923 pos = this.queue.poll();
            if (!LevelUtil.isLoaded(level, pos.field_9181, pos.field_9180)) continue;
            class_2818 chunk = level.method_8497(pos.field_9181, pos.field_9180);
            if (bypass) {
                AmethystBypassModule.collectPaletteLeak(chunk, this.bypassMarkers);
                this.bypassScanned.add(pos.method_8324());
                continue;
            }
            this.chunkFound.clear();
            if (this.method.is(ANBS_SCAN)) {
                this.collectGlow(level, chunk, this.chunkFound);
            } else {
                AmethystLight.scanSources(level, chunk, (packed, light) -> this.chunkFound.add(packed));
            }
            long key3 = pos.method_8324();
            long[] previous = (long[])this.chunkHits.get(key3);
            long[] now = this.chunkFound.toLongArray();
            if (previous != null && now.length < previous.length) {
                this.lightScanned.add(key3);
                continue;
            }
            if (now.length == 0) {
                if (this.chunkHits.remove(key3) != null) {
                    this.hitsChanged = true;
                }
            } else if (previous == null || !Arrays.equals(previous, now)) {
                this.chunkHits.put(key3, (Object)now);
                this.hitsChanged = true;
            }
            this.lightScanned.add(key3);
        }
        if (optimized) {
            PerformanceBudget.record(started);
        }
        if (bypass) {
            if (this.queue.isEmpty()) {
                this.hits = AmethystBypassModule.markersToLongArray(this.bypassMarkers);
            }
        } else if (this.hitsChanged) {
            this.hitsChanged = false;
            this.found.clear();
            ObjectIterator objectIterator = this.chunkHits.values().iterator();
            while (objectIterator.hasNext()) {
                long[] chunkMarks;
                for (long mark : chunkMarks = (long[])objectIterator.next()) {
                    this.found.add(mark);
                }
            }
            this.hits = this.found.toLongArray();
        }
    }

    private void collectGlow(class_638 level, class_2818 chunk, LongOpenHashSet out) {
        this.glowCells.clear();
        if (this.glow.scan(level, chunk, arg_0 -> ((LongArrayList)this.glowCells).add(arg_0)) < this.minCells.getInt()) {
            return;
        }
        int marks = Math.min(this.glowCells.size(), 200);
        for (int i = 0; i < marks; ++i) {
            out.add(this.glowCells.getLong(i));
        }
    }

    private static boolean inRange(long chunkKey, class_1923 center, int radius) {
        int x = class_1923.method_8325((long)chunkKey);
        int z = class_1923.method_8332((long)chunkKey);
        return Math.abs(x - center.field_9181) <= radius && Math.abs(z - center.field_9180) <= radius;
    }

    private static long[] markersToLongArray(Long2ObjectOpenHashMap<class_2338> markers) {
        long[] out = new long[markers.size()];
        int i = 0;
        for (class_2338 marker : markers.values()) {
            out[i++] = marker.method_10063();
        }
        return out;
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        if (!this.method.is("Bypass")) {
            class_2596<?> class_25962 = event.packet;
            if (class_25962 instanceof class_2672) {
                class_2672 chunk = (class_2672)class_25962;
                this.dirtyAround(chunk.method_11523(), chunk.method_11524());
            } else {
                class_25962 = event.packet;
                if (class_25962 instanceof class_2676) {
                    class_2676 light = (class_2676)class_25962;
                    this.dirtyAround(light.method_11558(), light.method_11554());
                } else {
                    class_25962 = event.packet;
                    if (class_25962 instanceof class_2626) {
                        class_2626 update = (class_2626)class_25962;
                        this.dirtyAround(update.method_11309().method_10263() >> 4, update.method_11309().method_10260() >> 4);
                    } else {
                        class_25962 = event.packet;
                        if (class_25962 instanceof class_2637) {
                            class_2637 sectionUpdate = (class_2637)class_25962;
                            sectionUpdate.method_30621((pos, state) -> this.dirtyAround(pos.method_10263() >> 4, pos.method_10260() >> 4));
                        }
                    }
                }
            }
            return;
        }
        class_2596<?> class_25963 = event.packet;
        if (class_25963 instanceof class_2672) {
            class_2672 chunk = (class_2672)class_25963;
            this.dirtyBypassChunk(chunk.method_11523(), chunk.method_11524());
        } else {
            class_25963 = event.packet;
            if (class_25963 instanceof class_2626) {
                class_2626 update = (class_2626)class_25963;
                this.dirtyBypassChunk(update.method_11309().method_10263() >> 4, update.method_11309().method_10260() >> 4);
            } else {
                class_25963 = event.packet;
                if (class_25963 instanceof class_2637) {
                    class_2637 sectionUpdate = (class_2637)class_25963;
                    sectionUpdate.method_30621((pos, state) -> this.dirtyBypassChunk(pos.method_10263() >> 4, pos.method_10260() >> 4));
                }
            }
        }
    }

    private void dirtyAround(int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                this.lightScanned.remove(class_1923.method_8331((int)(chunkX + dx), (int)(chunkZ + dz)));
            }
        }
    }

    private void dirtyBypassChunk(int chunkX, int chunkZ) {
        long key = class_1923.method_8331((int)chunkX, (int)chunkZ);
        if (this.bypassScanned.remove(key)) {
            this.bypassMarkers.remove(key);
        }
    }

    private static void collectPaletteLeak(class_2818 chunk, Long2ObjectOpenHashMap<class_2338> out) {
        class_2826[] sections = chunk.method_12006();
        int minSectionY = chunk.method_32891();
        class_1923 pos = chunk.method_12004();
        for (int i = 0; i < sections.length; ++i) {
            class_2841 states;
            class_2826 section = sections[i];
            if (section == null || (states = section.method_12265()).method_19526(state -> false) || !states.method_19526(AmethystBypassModule::isBud)) continue;
            int baseY = (minSectionY + i) * 16;
            class_2338 marker = AmethystBypassModule.pickMarker(chunk, section, baseY, pos);
            out.put(pos.method_8324(), (Object)(marker != null ? marker : new class_2338(pos.method_8326() + 8, baseY + 8, pos.method_8328() + 8)));
            return;
        }
    }

    private static class_2338 pickMarker(class_2818 chunk, class_2826 section, int baseY, class_1923 pos) {
        class_2338.class_2339 neighbour = new class_2338.class_2339();
        class_2338 best = null;
        int bestScore = Integer.MAX_VALUE;
        for (int y = 0; y < 16; ++y) {
            int worldY = baseY + y;
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    int score;
                    if (!section.method_12254(x, y, z).method_26215()) continue;
                    int worldX = pos.method_8326() + x;
                    int worldZ = pos.method_8328() + z;
                    boolean touchesGeode = false;
                    for (class_2350 direction : class_2350.values()) {
                        int nx = worldX + direction.method_10148();
                        int nz = worldZ + direction.method_10165();
                        if (nx >> 4 != pos.field_9181 || nz >> 4 != pos.field_9180) continue;
                        neighbour.method_10103(nx, worldY + direction.method_10164(), nz);
                        class_2248 block = chunk.method_8320((class_2338)neighbour).method_26204();
                        if (block != class_2246.field_27159 && block != class_2246.field_27160) continue;
                        touchesGeode = true;
                        break;
                    }
                    if (!touchesGeode || (score = AmethystBypassModule.scatter(worldX, worldY, worldZ)) >= bestScore) continue;
                    bestScore = score;
                    best = new class_2338(worldX, worldY, worldZ);
                }
            }
        }
        return best;
    }

    static int scatter(int x, int y, int z) {
        long h = (long)x * -7046029254386353131L ^ (long)y * -4417276706812531889L ^ (long)z * 1609587929392839161L;
        h ^= h >>> 29;
        h *= -4658895280553007687L;
        h ^= h >>> 32;
        return Math.floorMod(h, 100);
    }

    private static boolean isBud(class_2680 state) {
        class_2248 block = state.method_26204();
        return block == class_2246.field_27164 || block == class_2246.field_27163 || block == class_2246.field_27162;
    }

    private static boolean isGeode(class_2680 state) {
        return state.method_27852(class_2246.field_27159) || state.method_27852(class_2246.field_27160);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        class_638 level = class_310.method_1551().field_1687;
        long[] current = this.hits;
        if (level == null || current.length == 0) {
            return;
        }
        class_12179 style = class_12179.method_75537((int)-3636481, (float)1.5f, (int)-3636481);
        boolean drawTracers = this.tracer.get();
        for (long packed : current) {
            class_2338 pos = class_2338.method_10092((long)packed);
            if (!LevelUtil.isLoaded(level, pos.method_10263() >> 4, pos.method_10260() >> 4)) continue;
            class_238 box = new class_238(pos);
            if (event.frustum == null || event.frustum.method_23093(box)) {
                class_12180.method_75541((class_238)box, (class_12179)style).method_75533();
            }
            if (!drawTracers) continue;
            TracerEngine.draw(class_243.method_24953((class_2382)pos), -3636481);
        }
    }
}

