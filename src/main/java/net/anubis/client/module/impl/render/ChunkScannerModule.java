/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2IntMaps
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1923
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2672
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_310
 *  net.minecraft.class_5285
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_9848
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.BlockUpdateEvent;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.ChunkAreas;
import net.anubis.client.util.ChunkOverlay;
import net.anubis.client.util.LevelUtil;
import net.anubis.client.util.PerformanceBudget;
import net.anubis.client.util.seedscan.AdvancedScanner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2672;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_5285;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_9848;

@Environment(value=EnvType.CLIENT)
public final class ChunkScannerModule
extends ClientModule {
    private static final int PROBE_CHUNKS = 48;
    private static final byte OTHER = 0;
    private static final byte STONE = 1;
    private static final byte BLOB = 2;
    private static final byte AIR = 3;
    private static final byte MISSING = 4;
    private static final int[] SIDE_X = new int[]{1, -1, 0, 0};
    private static final int[] SIDE_Z = new int[]{0, 0, 1, -1};
    private static final int[] CORNER_X = new int[]{1, 1, -1, -1};
    private static final int[] CORNER_Z = new int[]{1, -1, 1, -1};
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 32;
    private static final int CHUNK_COLOR = 0x60FFFFFF;
    private static final int OLD_CHUNK_COLOR = 1614011733;
    private static final int SEED_SETTLE_TICKS = 20;
    private final BooleanSetting advancedScanner = this.addSetting("advanced-scanner", new BooleanSetting("Advanced Scanner", false));
    private final StringSetting seed = this.addSetting("seed", new StringSetting("Seed", ""));
    private final NumberSetting stackHeight = this.addSetting("stack-height", new NumberSetting("Sensitivity", 2.0f, 2.0f, 5.0f).step(1.0f));
    private final NumberSetting spreadRadius = this.addSetting("spread-radius", new NumberSetting("Spread Radius", 1.0f, 0.0f, 16.0f).step(1.0f));
    private final ColorSetting chunkColor = this.addSetting("chunk-color", new ColorSetting("Chunk Color", 0x60FFFFFF));
    private final BooleanSetting tracers = this.addSetting("tracers", new BooleanSetting("Tracers", true));
    private final BooleanSetting sound = this.addSetting("sound", new BooleanSetting("Sound", true));
    private final ChunkAreas chunkAreas = new ChunkAreas();
    private final ChunkOverlay chunkOverlay = new ChunkOverlay();
    private static final Find UNDECIDED = new Find(0L, 0, new long[0]);
    private final Long2ObjectOpenHashMap<Find> flagged = new Long2ObjectOpenHashMap();
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final LongOpenHashSet queued = new LongOpenHashSet();
    private final List<Find> fresh = new ArrayList<Find>();
    private final Long2IntOpenHashMap heat = new Long2IntOpenHashMap();
    private int appliedRadius;
    private LongOpenHashSet shown = new LongOpenHashSet();
    private boolean shownDirty = true;
    private int shownRefreshTicks;
    private class_638 lastLevel;
    private boolean scanning;
    private AdvancedScanner advanced;
    private boolean appliedAdvanced;
    private String seedText;
    private int seedStill;
    private boolean seedAsked;
    private ScanConfig appliedConfig;
    private WeakReference<class_634> probeServer = new WeakReference<Object>(null);
    private boolean probing;
    private final LongOpenHashSet probed = new LongOpenHashSet();
    private final class_2818[] around = new class_2818[9];
    private final int[] groupX = new int[4];
    private final int[] groupZ = new int[4];
    private int groupSize;
    private boolean guessed;
    private class_2248 wall;
    private class_2248 lastBlock;
    private final int[] stackTop = new int[256];
    private final int[] stackShape = new int[256];
    private final int[] stackAreas = new int[256];
    private final boolean[] stackGuessed = new boolean[256];
    private final class_2248[] stackWall = new class_2248[256];
    private boolean sawBlob;
    private int scanX;
    private int scanZ;
    private int keepHeight;
    private long[] keptStacks = new long[16];
    private int keptStackCount;
    private int tallest;
    private long bottom;
    private boolean unsure;

    public ChunkScannerModule() {
        super("chunk-scanner", "Chunk Scanner", "Marks suspicious chunks with signs of player activity.", ModuleCategory.BASE_RAID);
        BooleanSetting notifications = this.usesNotifications();
        this.sound.visibleWhen(notifications::get);
        this.stackHeight.visibleWhen(() -> !this.advancedScanner.get());
        this.seed.visibleWhen(this.advancedScanner::get);
        this.seed.privateValue();
    }

    @Override
    public int settingsVersion() {
        return 1;
    }

    @Override
    public void migrateSettings(int savedVersion) {
        if (savedVersion < 1 && this.chunkColor.argb() == 1614011733) {
            this.chunkColor.setArgb(0x60FFFFFF);
        }
    }

    @Override
    protected void onEnable() {
        this.startLevel(class_310.method_1551().field_1687);
    }

    @Override
    protected void onDisable() {
        this.startLevel(null);
    }

    private void startLevel(class_638 level) {
        this.closeAdvanced();
        this.appliedAdvanced = this.advancedScanner.get();
        this.flagged.clear();
        this.heat.clear();
        this.appliedRadius = this.spreadRadius.getInt();
        this.queue.clear();
        this.queued.clear();
        this.fresh.clear();
        this.shown = new LongOpenHashSet();
        this.shownDirty = true;
        this.shownRefreshTicks = 0;
        this.lastLevel = level;
        boolean bl = this.scanning = level != null && level.method_27983() != class_1937.field_25180 && level.method_27983() != class_1937.field_25181;
        if (!this.scanning) {
            return;
        }
        class_634 connection = class_310.method_1551().method_1562();
        if (connection != this.probeServer.get()) {
            this.probeServer = new WeakReference<class_634>(connection);
            this.probing = true;
            this.probed.clear();
        }
        this.appliedConfig = this.currentConfig(level);
        this.queueLoadedChunks(level);
    }

    private ScanConfig currentConfig(class_638 level) {
        return new ScanConfig(Math.max(0, level.method_31607()), Math.min(32, level.method_31600()), this.stackHeight.getInt());
    }

    private void queueLoadedChunks(class_638 level) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        int radius = (Integer)mc.field_1690.method_42503().method_41753() + 1;
        class_1923 center = mc.field_1724.method_31476();
        for (int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for (int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                if (!LevelUtil.isLoaded(level, x, z)) continue;
                this.enqueue(class_1923.method_8331((int)x, (int)z));
            }
        }
    }

    private void enqueue(long key) {
        if (this.scanning && !this.advancedScanner.get() && this.queued.add(key)) {
            this.queue.enqueue(key);
        }
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        class_2596<?> class_25962;
        if (this.advanced != null && (class_25962 = event.packet) instanceof class_2672) {
            class_2672 arrived = (class_2672)class_25962;
            this.advanced.chunkArrived(arrived.method_11523(), arrived.method_11524());
        }
        if (!((class_25962 = event.packet) instanceof class_2672)) {
            return;
        }
        class_2672 chunk = (class_2672)class_25962;
        this.shownDirty = true;
        this.enqueue(class_1923.method_8331((int)chunk.method_11523(), (int)chunk.method_11524()));
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) continue;
                this.enqueue(class_1923.method_8331((int)(chunk.method_11523() + dx), (int)(chunk.method_11524() + dz)));
            }
        }
    }

    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent event) {
        int y;
        if (!this.scanning || event.pos == null) {
            return;
        }
        if (this.advanced != null) {
            this.advanced.blockChanged(event.pos);
        }
        if ((y = event.pos.method_10264()) < this.appliedConfig.minY() - 1 || y > this.appliedConfig.maxY() + 1) {
            return;
        }
        if (!ChunkScannerModule.matters(event.oldState) && !ChunkScannerModule.matters(event.newState)) {
            return;
        }
        int x = event.pos.method_10263();
        int z = event.pos.method_10260();
        for (int cx = x - 2 >> 4; cx <= x + 1 >> 4; ++cx) {
            for (int cz = z - 2 >> 4; cz <= z + 2 >> 4; ++cz) {
                this.enqueue(class_1923.method_8331((int)cx, (int)cz));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    public void onTick(TickEvent event) {
        boolean optimized;
        int radius;
        ScanConfig config;
        class_638 level = class_310.method_1551().field_1687;
        if (level != this.lastLevel) {
            this.startLevel(level);
        }
        if (!this.scanning) {
            return;
        }
        if (this.advancedScanner.get() != this.appliedAdvanced) {
            this.startLevel(level);
        }
        if (!(config = this.currentConfig(level)).equals(this.appliedConfig)) {
            this.appliedConfig = config;
            this.queueLoadedChunks(level);
        }
        if ((radius = this.spreadRadius.getInt()) != this.appliedRadius) {
            this.appliedRadius = radius;
            this.heat.clear();
            this.shownDirty = true;
            ObjectIterator it = this.flagged.long2ObjectEntrySet().fastIterator();
            while (it.hasNext()) {
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
                this.spread(entry.getLongKey(), (Find)entry.getValue(), 1);
            }
        }
        long deadline = (optimized = PerformanceModule.optimizationsEnabled()) ? PerformanceBudget.acquire(PerformanceModule.scanBudgetNanos()) : System.nanoTime() + PerformanceModule.scanBudgetNanos();
        long started = System.nanoTime();
        try {
            if (this.advancedScanner.get() && started < deadline) {
                this.tickAdvanced(level, deadline);
            }
            while (!this.queue.isEmpty() && System.nanoTime() < deadline) {
                long key = this.queue.dequeueLong();
                this.queued.remove(key);
                int cx = class_1923.method_8325((long)key);
                int cz = class_1923.method_8332((long)key);
                if (!LevelUtil.isLoaded(level, cx, cz)) continue;
                Find find = this.scan(level, cx, cz, config);
                if (this.sawBlob) {
                    this.probe(key);
                }
                if (find == UNDECIDED) continue;
                Find was = (Find)this.flagged.get(key);
                if (find == null ? was == null : was != null && was.sameAs(find)) continue;
                this.refile(key, was, find);
                if (was != null) continue;
                this.fresh.add(find);
            }
            this.announce(this.fresh);
            this.fresh.clear();
            if (!optimized || this.shownDirty || ++this.shownRefreshTicks >= 5) {
                this.shown = this.computeShown(level);
                this.shownDirty = false;
                this.shownRefreshTicks = 0;
            }
        }
        finally {
            if (optimized) {
                PerformanceBudget.record(started);
            }
        }
    }

    private void refile(long key, Find was, Find find) {
        this.shownDirty = true;
        if (was != null) {
            this.spread(key, was, -1);
        }
        if (find == null) {
            this.flagged.remove(key);
            return;
        }
        this.flagged.put(key, (Object)find);
        this.spread(key, find, 1);
    }

    private void spread(long key, Find find, int sign) {
        int amount = sign * find.stacks().length;
        int r = this.appliedRadius;
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        for (int x = cx - r; x <= cx + r; ++x) {
            for (int z = cz - r; z <= cz + r; ++z) {
                long area = class_1923.method_8331((int)x, (int)z);
                if (this.heat.addTo(area, amount) + amount != 0) continue;
                this.heat.remove(area);
            }
        }
    }

    private LongOpenHashSet computeShown(class_638 level) {
        Long2IntOpenHashMap candidates = new Long2IntOpenHashMap();
        for (Long2IntMap.Entry entry : Long2IntMaps.fastIterable((Long2IntMap)this.heat)) {
            long key = entry.getLongKey();
            if (!LevelUtil.isLoaded(level, class_1923.method_8325((long)key), class_1923.method_8332((long)key))) continue;
            candidates.put(key, entry.getIntValue());
        }
        return this.appliedRadius == 0 ? new LongOpenHashSet((LongCollection)candidates.keySet()) : ChunkScannerModule.hottestPerPatch(candidates);
    }

    private static LongOpenHashSet hottestPerPatch(Long2IntOpenHashMap candidates) {
        LongOpenHashSet kept = new LongOpenHashSet();
        LongOpenHashSet seen = new LongOpenHashSet();
        LongArrayList patch = new LongArrayList();
        LongArrayFIFOQueue open = new LongArrayFIFOQueue();
        LongIterator longIterator = candidates.keySet().iterator();
        while (longIterator.hasNext()) {
            long start = (Long)longIterator.next();
            if (!seen.add(start)) continue;
            patch.clear();
            open.enqueue(start);
            int max = 0;
            while (!open.isEmpty()) {
                long key = open.dequeueLong();
                patch.add(key);
                max = Math.max(max, candidates.get(key));
                int cx = class_1923.method_8325((long)key);
                int cz = class_1923.method_8332((long)key);
                for (int side = 0; side < 4; ++side) {
                    long next = class_1923.method_8331((int)(cx + SIDE_X[side]), (int)(cz + SIDE_Z[side]));
                    if (!candidates.containsKey(next) || !seen.add(next)) continue;
                    open.enqueue(next);
                }
            }
            for (int i = 0; i < patch.size(); ++i) {
                if (candidates.get(patch.getLong(i)) != max) continue;
                kept.add(patch.getLong(i));
            }
        }
        return kept;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Find scan(class_638 level, int chunkX, int chunkZ, ScanConfig config) {
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                this.around[(dx + 1) * 3 + dz + 1] = level.method_2935().method_21730(chunkX + dx, chunkZ + dz);
            }
        }
        this.sawBlob = false;
        this.scanX = chunkX << 4;
        this.scanZ = chunkZ << 4;
        this.keepHeight = config.stackHeight();
        this.keptStackCount = 0;
        this.tallest = 0;
        this.bottom = 0L;
        this.unsure = false;
        try {
            int i;
            class_2818 chunk = this.around[4];
            class_2826[] sections = chunk.method_12006();
            int minSectionY = chunk.method_32891();
            Arrays.fill(this.stackTop, Integer.MIN_VALUE);
            boolean blank = true;
            for (i = 0; i < sections.length; ++i) {
                int baseY;
                class_2826 section = sections[i];
                if (section == null || section.method_38292() || (baseY = minSectionY + i << 4) + 15 < config.minY() || baseY > config.maxY()) continue;
                int fromY = Math.max(config.minY() - baseY, 0);
                int toY = Math.min(config.maxY() - baseY, 15);
                if (blank) {
                    boolean bl = blank = !ChunkScannerModule.hasBlocks(section, fromY, toY);
                }
                if (!section.method_19523(state -> state.method_27852(class_2246.field_10340)) || !section.method_19523(state -> ChunkScannerModule.isBlob(state.method_26204()))) continue;
                for (int y = fromY; y <= toY; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        boolean afterStone = false;
                        for (int x = 0; x < 16; ++x) {
                            boolean stone;
                            class_2248 block = section.method_12254(x, y, z).method_26204();
                            boolean bl = stone = block == class_2246.field_10340;
                            if (stone) {
                                if (!afterStone && this.isArea(x, baseY + y, z)) {
                                    this.stack(x, baseY + y, z);
                                }
                            } else if (this.probing && ChunkScannerModule.isBlob(block)) {
                                this.sawBlob = true;
                                if (this.enclosed(x, baseY + y, z)) {
                                    this.probing = false;
                                    this.probed.clear();
                                }
                            }
                            afterStone = stone;
                        }
                    }
                }
            }
            for (i = 0; i < 256; ++i) {
                this.keep(i);
            }
            if (this.tallest >= config.stackHeight()) {
                Find find = new Find(this.bottom, this.tallest, Arrays.copyOf(this.keptStacks, this.keptStackCount));
                return find;
            }
            Find find = this.unsure || blank ? UNDECIDED : null;
            return find;
        }
        finally {
            Arrays.fill(this.around, null);
        }
    }

    private void stack(int x, int y, int z) {
        boolean onTop;
        int i = z << 4 | x;
        int shape = this.shape(x, z);
        boolean bl = onTop = this.stackTop[i] == y - 1 && this.stackShape[i] == shape && this.stackWall[i] == this.wall;
        if (!onTop) {
            this.keep(i);
        }
        this.stackTop[i] = y;
        this.stackShape[i] = shape;
        this.stackWall[i] = this.wall;
        this.stackAreas[i] = onTop ? this.stackAreas[i] + 1 : 1;
        this.stackGuessed[i] = this.guessed || onTop && this.stackGuessed[i];
    }

    private void keep(int i) {
        int height = this.stackAreas[i];
        int top = this.stackTop[i];
        if (top == Integer.MIN_VALUE || height < this.keepHeight) {
            return;
        }
        int shape = this.stackShape[i];
        if (!this.goesOn(i & 0xF, top + 1, i >> 4, shape) || !this.goesOn(i & 0xF, top - height, i >> 4, shape)) {
            return;
        }
        if (this.stackGuessed[i]) {
            this.unsure = true;
            return;
        }
        int x = this.scanX + (i & 0xF);
        int z = this.scanZ + (i >> 4);
        long lowest = class_2338.method_10064((int)x, (int)(top - height + 1), (int)z);
        if (height > this.tallest || height == this.tallest && ChunkScannerModule.before(lowest, this.bottom)) {
            this.tallest = height;
            this.bottom = lowest;
        }
        if (this.keptStackCount == this.keptStacks.length) {
            this.keptStacks = Arrays.copyOf(this.keptStacks, this.keptStacks.length * 2);
        }
        this.keptStacks[this.keptStackCount++] = lowest;
    }

    private boolean goesOn(int x0, int y, int z0, int shape) {
        for (int bit = 0; bit < 6; ++bit) {
            if ((shape >> bit & 1) == 0) continue;
            this.kind(x0 + bit / 3, y, z0 + bit % 3 - 1);
            if (ChunkScannerModule.carriesStone(this.lastBlock)) continue;
            return false;
        }
        return true;
    }

    private static boolean before(long a, long b) {
        if (class_2338.method_10071((long)a) != class_2338.method_10071((long)b)) {
            return class_2338.method_10071((long)a) < class_2338.method_10071((long)b);
        }
        if (class_2338.method_10083((long)a) != class_2338.method_10083((long)b)) {
            return class_2338.method_10083((long)a) < class_2338.method_10083((long)b);
        }
        return class_2338.method_10061((long)a) < class_2338.method_10061((long)b);
    }

    private int shape(int x0, int z0) {
        int shape = 0;
        for (int i = 0; i < this.groupSize; ++i) {
            shape |= 1 << (this.groupX[i] - x0) * 3 + this.groupZ[i] - z0 + 1;
        }
        return shape;
    }

    private boolean isArea(int x0, int y, int z0) {
        int z;
        int x;
        int i;
        this.guessed = false;
        this.wall = null;
        if (this.seen(x0 - 1, y, z0) != 2 || !this.sameWall() || this.seen(x0, y, z0 - 1) != 2 || !this.sameWall()) {
            return false;
        }
        this.groupX[0] = x0;
        this.groupZ[0] = z0;
        int size = 1;
        int maxX = x0;
        int minZ = z0;
        int maxZ = z0;
        for (i = 0; i < size; ++i) {
            for (int side = 0; side < 4; ++side) {
                x = this.groupX[i] + SIDE_X[side];
                z = this.groupZ[i] + SIDE_Z[side];
                if (this.inGroup(x, z, size)) continue;
                byte kind = this.seen(x, y, z);
                if (kind == 2) {
                    if (this.sameWall()) continue;
                    return false;
                }
                if (kind != 1) {
                    return false;
                }
                if (x < x0 || x == x0 && z < z0) {
                    return false;
                }
                maxX = Math.max(maxX, x);
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
                if (maxX - x0 > 1 || maxZ - minZ > 1) {
                    return false;
                }
                this.groupX[size] = x;
                this.groupZ[size] = z;
                ++size;
            }
        }
        for (i = 0; i < size; ++i) {
            for (int corner = 0; corner < 4; ++corner) {
                x = this.groupX[i] + CORNER_X[corner];
                z = this.groupZ[i] + CORNER_Z[corner];
                if (this.inGroup(x, z, size) || this.seen(x, y, z) == 2 && this.sameWall()) continue;
                return false;
            }
        }
        for (i = 0; i < size; ++i) {
            if (this.kind(this.groupX[i], y + 1, this.groupZ[i]) != 3 && this.kind(this.groupX[i], y - 1, this.groupZ[i]) != 3) continue;
            return false;
        }
        this.groupSize = size;
        return true;
    }

    private boolean inGroup(int x, int z, int size) {
        for (int i = 0; i < size; ++i) {
            if (this.groupX[i] != x || this.groupZ[i] != z) continue;
            return true;
        }
        return false;
    }

    private byte seen(int x, int y, int z) {
        byte kind = this.kind(x, y, z);
        if (kind != 4) {
            return kind;
        }
        this.guessed = true;
        return 2;
    }

    private boolean sameWall() {
        if (this.lastBlock == null) {
            return true;
        }
        if (this.wall == null) {
            this.wall = this.lastBlock;
        }
        return this.lastBlock == this.wall;
    }

    private byte kind(int x, int y, int z) {
        class_2818 chunk = this.around[((x >> 4) + 1) * 3 + (z >> 4) + 1];
        this.lastBlock = null;
        if (chunk == null) {
            return 4;
        }
        int index = chunk.method_31602(y);
        if (index < 0 || index >= chunk.method_32890()) {
            return 0;
        }
        class_2680 state = chunk.method_38259(index).method_12254(x & 0xF, y & 0xF, z & 0xF);
        this.lastBlock = state.method_26204();
        return ChunkScannerModule.kindOf(state);
    }

    private boolean enclosed(int x, int y, int z) {
        return this.full(x + 1, y, z) && this.full(x - 1, y, z) && this.full(x, y, z + 1) && this.full(x, y, z - 1) && this.full(x, y + 1, z) && this.full(x, y - 1, z);
    }

    private boolean full(int x, int y, int z) {
        class_2818 chunk = this.around[((x >> 4) + 1) * 3 + (z >> 4) + 1];
        if (chunk == null) {
            return false;
        }
        int index = chunk.method_31602(y);
        return index >= 0 && index < chunk.method_32890() && chunk.method_38259(index).method_12254(x & 0xF, y & 0xF, z & 0xF).method_26216();
    }

    private static boolean hasBlocks(class_2826 section, int fromY, int toY) {
        if (!section.method_19523(state -> !state.method_26215())) {
            return false;
        }
        for (int y = fromY; y <= toY; ++y) {
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) {
                    if (section.method_12254(x, y, z).method_26215()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static byte kindOf(class_2680 state) {
        class_2248 block = state.method_26204();
        if (block == class_2246.field_10340) {
            return 1;
        }
        if (ChunkScannerModule.isBlob(block)) {
            return 2;
        }
        return state.method_26215() ? (byte)3 : 0;
    }

    private static boolean isBlob(class_2248 block) {
        return block == class_2246.field_10508 || block == class_2246.field_10115 || block == class_2246.field_10474 || block == class_2246.field_10255;
    }

    private static boolean carriesStone(class_2248 block) {
        return block == class_2246.field_10340 || block == class_2246.field_28888;
    }

    private static boolean matters(class_2680 state) {
        return state != null && (ChunkScannerModule.kindOf(state) != 0 || ChunkScannerModule.carriesStone(state.method_26204()));
    }

    private void probe(long key) {
        if (!this.probing || !this.probed.add(key) || this.probed.size() < 48) {
            return;
        }
        this.probing = false;
        this.probed.clear();
        ModuleChat.send((ClientModule)this, ModuleChat.body().text("This server ").warn("hides").text(" ores with anti-xray, so patched shafts can't be seen"));
    }

    private void announce(List<Find> finds) {
        if (!this.notificationsOn()) {
            return;
        }
        if (finds.isEmpty()) {
            return;
        }
        class_1799 icon = new class_1799((class_1935)(this.advancedScanner.get() ? class_1802.field_20401 : class_1802.field_27070));
        ChallengeToast.addOrUpdate(icon, this.name(), (class_2561)class_2561.method_43470((String)"Suspect Chunk Scanned"), this.sound.get());
    }

    private void tickAdvanced(class_638 level, long deadline) {
        String text = this.seed.get();
        if (!text.equals(this.seedText)) {
            this.seedStill = this.seedText == null ? 20 : 0;
            this.seedText = text;
        } else if (this.seedStill < 20) {
            ++this.seedStill;
        }
        if (this.seedStill >= 20) {
            OptionalLong wanted = class_5285.method_46720((String)text);
            if (wanted.isEmpty()) {
                if (this.advanced != null) {
                    this.startLevel(level);
                }
                if (!this.seedAsked) {
                    this.seedAsked = true;
                    ModuleChat.send((ClientModule)this, ModuleChat.body().text("Advanced Scanner needs the world seed: ").warn("set Seed"));
                }
                return;
            }
            this.seedAsked = false;
            if (this.advanced == null || this.advanced.seed() != wanted.getAsLong()) {
                if (this.advanced != null) {
                    this.startLevel(level);
                }
                this.advanced = AdvancedScanner.start(wanted.getAsLong());
            }
        }
        if (this.advanced == null) {
            return;
        }
        for (AdvancedScanner.ChunkResult result : this.advanced.tick(level, deadline)) {
            Find find = result.flagged() ? new Find(result.anchors()[0], result.blocks(), result.anchors()) : null;
            Find was = (Find)this.flagged.get(result.chunk());
            if (find == null ? was == null : was != null && was.sameAs(find)) continue;
            this.refile(result.chunk(), was, find);
            if (was != null || find == null) continue;
            this.fresh.add(find);
        }
        for (String notice : this.advanced.drainNotices()) {
            ModuleChat.send((ClientModule)this, notice);
        }
    }

    private void closeAdvanced() {
        if (this.advanced != null) {
            this.advanced.close();
        }
        this.advanced = null;
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (class_310.method_1551().field_1687 == null) {
            return;
        }
        this.chunkOverlay.draw((LongSet)this.shown, this.chunkColor.argb(), event.partialTick);
        if (this.tracers.get()) {
            this.chunkAreas.drawTracers((LongSet)this.shown, 63.0, class_9848.method_61334((int)this.chunkColor.argb()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record ScanConfig(int minY, int maxY, int stackHeight) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Find(long pos, int height, long[] stacks) {
        boolean sameAs(Find other) {
            return this.pos == other.pos && this.height == other.height && Arrays.equals(this.stacks, other.stacks);
        }
    }
}

