/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1923
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2841
 *  net.minecraft.class_310
 *  net.minecraft.class_4543
 *  net.minecraft.class_638
 */
package net.anubis.client.util.seedscan;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;
import net.anubis.client.util.seedscan.BlobJudge;
import net.anubis.client.util.seedscan.PredictionArea;
import net.anubis.client.util.seedscan.SeedWorldgen;
import net.anubis.client.util.seedscan.ShownChunk;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2841;
import net.minecraft.class_310;
import net.minecraft.class_4543;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class AdvancedScanner
implements AutoCloseable {
    public static final int MAX_RADIUS = 8;
    public static final int WORLD_LIMIT = 250000;
    private static final int SETTLE_TICKS = 20;
    private static final int SEED_CHECK_AFTER = 40;
    private static final double SEED_CHECK_FAIL = 0.5;
    private static final int HIDDEN_AFTER = 10;
    private static final int GROUND_CHECK_AFTER = 20;
    private final long seed;
    private final Thread worker;
    private volatile boolean closed;
    private volatile boolean stopped;
    private final ConcurrentLinkedQueue<Object> inbox = new ConcurrentLinkedQueue();
    private final ConcurrentLinkedQueue<Object> outbox = new ConcurrentLinkedQueue();
    private final Long2ObjectOpenHashMap<List<BlobJudge.Cluster>> clusters = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<ChunkResult> results = new Long2ObjectOpenHashMap();
    private final LongOpenHashSet shownSent = new LongOpenHashSet();
    private final LongArrayFIFOQueue toJudge = new LongArrayFIFOQueue();
    private final LongOpenHashSet judgeQueued = new LongOpenHashSet();
    private final Long2LongOpenHashMap settling = new Long2LongOpenHashMap();
    private final LongOpenHashSet counted = new LongOpenHashSet();
    private final List<String> notices = new ArrayList<String>();
    private LongOpenHashSet area = new LongOpenHashSet();
    private int areaX = Integer.MIN_VALUE;
    private int areaZ = Integer.MIN_VALUE;
    private int areaRadius = -1;
    private boolean areaStale;
    private long ticks;
    private int trusted;
    private int mispredicted;
    private int hidden;
    private boolean hashChecked;
    private boolean seedWarned;
    private boolean hiddenWarned;
    private boolean distrusted;
    private boolean wrongHeight;
    private boolean released;

    private AdvancedScanner(long seed) {
        this.seed = seed;
        this.worker = new Thread(this::work, "Anubis Advanced Scanner");
        this.worker.setDaemon(true);
        this.worker.setPriority(2);
        this.worker.start();
    }

    public static AdvancedScanner start(long seed) {
        return new AdvancedScanner(seed);
    }

    public long seed() {
        return this.seed;
    }

    @Override
    public void close() {
        this.closed = true;
        LockSupport.unpark(this.worker);
    }

    public static boolean matchesHashedSeed(long seed, long hashedSeed) {
        return class_4543.method_27984((long)seed) == hashedSeed;
    }

    static OptionalLong hashedSeed(class_638 level) {
        try {
            Field found = null;
            for (Field field : class_4543.class.getDeclaredFields()) {
                if (field.getType() != Long.TYPE || Modifier.isStatic(field.getModifiers())) continue;
                if (found != null) {
                    return OptionalLong.empty();
                }
                found = field;
            }
            if (found == null) {
                return OptionalLong.empty();
            }
            found.setAccessible(true);
            return OptionalLong.of(found.getLong(level.method_22385()));
        }
        catch (ReflectiveOperationException | RuntimeException e) {
            return OptionalLong.empty();
        }
    }

    public List<String> drainNotices() {
        ArrayList<String> out = new ArrayList<String>(this.notices);
        this.notices.clear();
        return out;
    }

    public List<ChunkResult> tick(class_638 level, long deadline) {
        Object message;
        ++this.ticks;
        while ((message = this.outbox.poll()) != null) {
            if (message instanceof PredictionArea.ChunkClusters) {
                PredictionArea.ChunkClusters found = (PredictionArea.ChunkClusters)message;
                if (found.clusters() == null) {
                    this.clusters.remove(found.chunk());
                    continue;
                }
                this.clusters.put(found.chunk(), found.clusters());
                this.queueJudge(found.chunk());
                continue;
            }
            if (!(message instanceof String)) continue;
            String notice = (String)message;
            this.notices.add(notice);
        }
        if (this.stopped) {
            this.release();
            return List.of();
        }
        if (level.method_31607() != -64 || level.method_31605() != 384) {
            if (!this.wrongHeight) {
                this.wrongHeight = true;
                this.notices.add("Advanced Scanner only works with vanilla world height.");
            }
            return List.of();
        }
        if (!this.hashChecked) {
            this.hashChecked = true;
            OptionalLong hashed = AdvancedScanner.hashedSeed(level);
            if (hashed.isPresent() && !AdvancedScanner.matchesHashedSeed(this.seed, hashed.getAsLong())) {
                this.notices.add("Seed " + this.seed + " does not match this server. Check the seed.");
            }
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null) {
            this.updateArea(level, mc.field_1724.method_31476(), Math.min((Integer)mc.field_1690.method_42503().method_41753(), 8));
        }
        this.copyArrived(level, deadline);
        ArrayList<ChunkResult> judged = new ArrayList<ChunkResult>();
        BlobJudge.Client client = AdvancedScanner.clientView(level);
        int rounds = this.toJudge.size();
        while (rounds-- > 0 && !this.toJudge.isEmpty() && System.nanoTime() < deadline) {
            ChunkResult result;
            List found;
            long key = this.toJudge.dequeueLong();
            this.judgeQueued.remove(key);
            if (this.settling.containsKey(key)) {
                if (this.settling.get(key) > this.ticks) {
                    this.queueJudge(key);
                    continue;
                }
                this.settling.remove(key);
            }
            if ((found = (List)this.clusters.get(key)) == null || !this.area.contains(key) || (result = this.judge(key, found, client)) == null) continue;
            this.results.put(key, (Object)result);
            judged.add(result);
        }
        return judged;
    }

    private ChunkResult judge(long key, List<BlobJudge.Cluster> found, BlobJudge.Client client) {
        ArrayList<BlobJudge.Verdict> verdicts = new ArrayList<BlobJudge.Verdict>(found.size());
        for (BlobJudge.Cluster cluster : found) {
            BlobJudge.Verdict verdict = BlobJudge.judge(cluster, client, 1);
            verdicts.add(verdict);
            this.count(verdict);
        }
        if (this.distrusted) {
            return new ChunkResult(key, 0, new long[0], List.of());
        }
        return AdvancedScanner.decide(key, (ChunkResult)this.results.get(key), verdicts);
    }

    static ChunkResult decide(long chunk, ChunkResult before, List<BlobJudge.Verdict> now) {
        HashMap<Long, BlobJudge.Verdict> last = new HashMap<Long, BlobJudge.Verdict>();
        if (before != null) {
            for (BlobJudge.Verdict verdict : before.verdicts()) {
                if (verdict.status() == BlobJudge.Status.INCOMPLETE) continue;
                last.put(AdvancedScanner.clusterKey(verdict.owner(), verdict.kind()), verdict);
            }
        }
        boolean undecided = false;
        ArrayList<BlobJudge.Verdict> verdicts = new ArrayList<BlobJudge.Verdict>(now.size());
        LongArrayList blocks = new LongArrayList();
        LongArrayList anchors = new LongArrayList();
        for (BlobJudge.Verdict verdict : now) {
            if (verdict.status() == BlobJudge.Status.INCOMPLETE) {
                BlobJudge.Verdict earlier = (BlobJudge.Verdict)last.get(AdvancedScanner.clusterKey(verdict.owner(), verdict.kind()));
                if (earlier != null) {
                    verdict = earlier;
                } else {
                    undecided = true;
                }
            }
            verdicts.add(verdict);
            if (!verdict.alert()) continue;
            anchors.add(verdict.changes()[0]);
            blocks.addElements(blocks.size(), verdict.changes());
        }
        if (undecided && anchors.isEmpty()) {
            return null;
        }
        long[] sorted = blocks.toLongArray();
        Arrays.sort(sorted);
        int n = 0;
        for (long pos : sorted) {
            if (n != 0 && sorted[n - 1] == pos) continue;
            sorted[n++] = pos;
        }
        return new ChunkResult(chunk, n, anchors.toLongArray(), List.copyOf(verdicts));
    }

    static void dropOutside(Long2ObjectOpenHashMap<ChunkResult> results, LongOpenHashSet area) {
        ObjectIterator it = results.long2ObjectEntrySet().fastIterator();
        while (it.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
            if (area.contains(entry.getLongKey()) || ((ChunkResult)entry.getValue()).flagged()) continue;
            it.remove();
        }
    }

    private static long clusterKey(long owner, byte kind) {
        return owner * 31L + (long)kind;
    }

    public void blockChanged(class_2338 pos) {
        if (this.stopped) {
            return;
        }
        if (pos.method_10264() < -17 || pos.method_10264() > 64) {
            return;
        }
        int cx = pos.method_10263() >> 4;
        int cz = pos.method_10260() >> 4;
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                long key = class_1923.method_8331((int)(cx + dx), (int)(cz + dz));
                if (!this.clusters.containsKey(key)) continue;
                this.settling.put(key, this.ticks + 20L);
                this.queueJudge(key);
            }
        }
    }

    public void chunkArrived(int chunkX, int chunkZ) {
        if (this.stopped) {
            return;
        }
        this.areaStale = true;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                this.queueJudge(class_1923.method_8331((int)(chunkX + dx), (int)(chunkZ + dz)));
            }
        }
    }

    static boolean insideWorldLimit(int chunkX, int chunkZ) {
        int chunks = 15625;
        return chunkX >= -chunks && chunkX < chunks && chunkZ >= -chunks && chunkZ < chunks;
    }

    private void queueJudge(long key) {
        if (this.judgeQueued.add(key)) {
            this.toJudge.enqueue(key);
        }
    }

    private void updateArea(class_638 level, class_1923 center, int radius) {
        boolean moved;
        boolean bl = moved = center.field_9181 != this.areaX || center.field_9180 != this.areaZ || radius != this.areaRadius;
        if (!(moved || this.areaStale && this.ticks % 20L == 0L)) {
            return;
        }
        this.areaStale = false;
        this.areaX = center.field_9181;
        this.areaZ = center.field_9180;
        this.areaRadius = radius;
        LongOpenHashSet chunks = new LongOpenHashSet();
        for (int x = center.field_9181 - radius; x <= center.field_9181 + radius; ++x) {
            for (int z = center.field_9180 - radius; z <= center.field_9180 + radius; ++z) {
                if (!AdvancedScanner.insideWorldLimit(x, z) || level.method_2935().method_21730(x, z) == null) continue;
                chunks.add(class_1923.method_8331((int)x, (int)z));
            }
        }
        this.area = chunks;
        this.clusters.keySet().retainAll((LongCollection)chunks);
        AdvancedScanner.dropOutside(this.results, chunks);
        int reach = radius + 1 + 1 + 1;
        this.shownSent.removeIf(key -> Math.max(Math.abs(class_1923.method_8325((long)key) - center.field_9181), Math.abs(class_1923.method_8332((long)key) - center.field_9180)) > reach);
        this.send(new Want(chunks, center.field_9181, center.field_9180));
    }

    private void copyArrived(class_638 level, long deadline) {
        if (this.areaRadius < 0) {
            return;
        }
        int reach = this.areaRadius + 1 + 1 + 1;
        for (int ring = 0; ring <= reach; ++ring) {
            if (System.nanoTime() >= deadline) {
                return;
            }
            for (int x = this.areaX - ring; x <= this.areaX + ring; ++x) {
                for (int z = this.areaZ - ring; z <= this.areaZ + ring; ++z) {
                    class_2818 chunk;
                    long key;
                    if (Math.max(Math.abs(x - this.areaX), Math.abs(z - this.areaZ)) != ring || this.shownSent.contains(key = class_1923.method_8331((int)x, (int)z)) || (chunk = level.method_2935().method_21730(x, z)) == null) continue;
                    class_2826[] sections = chunk.method_12006();
                    class_2841[] copies = new class_2841[sections.length];
                    for (int i = 0; i < sections.length; ++i) {
                        if (sections[i] == null || sections[i].method_38292()) continue;
                        copies[i] = sections[i].method_12265().method_39957();
                    }
                    this.shownSent.add(key);
                    this.send(new Copied(x, z, copies));
                    if (System.nanoTime() < deadline) continue;
                    return;
                }
            }
        }
    }

    private static BlobJudge.Client clientView(final class_638 level) {
        return new BlobJudge.Client(){
            private int lastX = Integer.MIN_VALUE;
            private int lastZ = Integer.MIN_VALUE;
            private class_2818 last;

            @Override
            public class_2680 state(int x, int y, int z) {
                int cx = x >> 4;
                int cz = z >> 4;
                if (cx != this.lastX || cz != this.lastZ) {
                    this.lastX = cx;
                    this.lastZ = cz;
                    this.last = level.method_2935().method_21730(cx, cz);
                }
                if (this.last == null) {
                    return null;
                }
                int index = this.last.method_31602(y);
                if (index < 0 || index >= this.last.method_32890()) {
                    return class_2246.field_10243.method_9564();
                }
                return this.last.method_38259(index).method_12254(x & 0xF, y & 0xF, z & 0xF);
            }
        };
    }

    private void count(BlobJudge.Verdict verdict) {
        switch (verdict.status()) {
            case CLEAN: 
            case ALERT: 
            case MISPREDICTED: 
            case HIDDEN: {
                if (this.counted.add(AdvancedScanner.clusterKey(verdict.owner(), verdict.kind()))) break;
                return;
            }
            default: {
                return;
            }
        }
        switch (verdict.status()) {
            case CLEAN: 
            case ALERT: {
                ++this.trusted;
                break;
            }
            case MISPREDICTED: {
                ++this.mispredicted;
                break;
            }
            default: {
                ++this.hidden;
            }
        }
        int seen = this.trusted + this.mispredicted;
        if (!this.seedWarned && seen >= 40 && (double)this.mispredicted > 0.5 * (double)seen) {
            this.seedWarned = true;
            this.distrusted = true;
            this.notices.add("Ores do not match seed " + this.seed + ". Advanced alerts are off until you change the seed.");
        }
        if (!this.hiddenWarned && this.hidden >= 10 && this.hidden > seen) {
            this.hiddenWarned = true;
            this.notices.add("This server hides ores with anti-xray. Advanced Scanner can't check them.");
        }
    }

    private void send(Object message) {
        if (this.stopped || this.closed) {
            return;
        }
        this.inbox.add(message);
        LockSupport.unpark(this.worker);
    }

    private void release() {
        this.inbox.clear();
        if (this.released) {
            return;
        }
        this.released = true;
        this.clusters.clear();
        this.toJudge.clear();
        this.judgeQueued.clear();
        this.settling.clear();
        this.shownSent.clear();
        this.area = new LongOpenHashSet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void work() {
        SeedWorldgen world = null;
        Throwable failure = null;
        try {
            world = SeedWorldgen.create(this.seed);
            this.predict(world);
        }
        catch (Throwable e) {
            failure = e;
        }
        finally {
            this.stopped = true;
            this.inbox.clear();
        }
        if (failure == null) {
            return;
        }
        try {
            this.outbox.add((world == null ? "Advanced Scanner can't work out this world: " : "Advanced Scanner stopped: ") + String.valueOf(failure));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void predict(SeedWorldgen world) {
        PredictionArea prediction = new PredictionArea(world);
        boolean groundChecked = false;
        while (!this.closed) {
            Object message;
            while ((message = this.inbox.poll()) != null) {
                if (message instanceof Want) {
                    Want want = (Want)message;
                    prediction.want((LongCollection)want.chunks(), want.x(), want.z());
                    continue;
                }
                if (!(message instanceof Copied)) continue;
                Copied copied = (Copied)message;
                prediction.offer(ShownChunk.of(copied.x(), copied.z(), copied.sections()));
            }
            if (prediction.idle()) {
                LockSupport.parkNanos(50000000L);
                continue;
            }
            PredictionArea.ChunkClusters found = prediction.step();
            if (found != null) {
                this.outbox.add(found);
            }
            if (groundChecked || prediction.compared() < 20) continue;
            groundChecked = true;
            if (prediction.mismatched() * 2 <= prediction.compared()) continue;
            this.outbox.add("The terrain here does not match seed " + this.seed + ". Check the seed.");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ChunkResult(long chunk, int blocks, long[] anchors, List<BlobJudge.Verdict> verdicts) {
        public boolean flagged() {
            return this.anchors.length > 0;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Want(LongOpenHashSet chunks, int x, int z) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Copied(int x, int z, class_2841<class_2680>[] sections) {
    }
}

