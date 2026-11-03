/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
 *  com.google.gson.stream.JsonWriter
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.util.playerbypass;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.LongConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ProtectedChunkStore {
    public static final int RADIUS = 2;
    static final int CONFIG_VERSION = 1;
    static final long SAVE_DELAY_NANOS = 10000000000L;
    private static final long STOP_WAIT_SECONDS = 10L;
    private static final String DEFAULT_PORT_SUFFIX = ":25565";
    private static final int NAME_PART_LENGTH = 40;
    private static final long NOT_AN_INT = Long.MIN_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final ProtectedChunkStore INSTANCE = new ProtectedChunkStore();
    private final Map<String, Bucket> buckets = new HashMap<String, Bucket>();
    private final Executor writer = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "Anubis Player Bypass saves");
        thread.setDaemon(true);
        return thread;
    });
    private final ConcurrentHashMap<Path, long[]> queued = new ConcurrentHashMap();
    private CompletableFuture<Void> lastWrite = CompletableFuture.completedFuture(null);
    private volatile boolean writeFailed;
    private Bucket active;
    private boolean dirty;
    private long dirtySince;

    private ProtectedChunkStore() {
    }

    public static ProtectedChunkStore get() {
        return INSTANCE;
    }

    public static long key(int chunkX, int chunkZ) {
        return (long)chunkX & 0xFFFFFFFFL | ((long)chunkZ & 0xFFFFFFFFL) << 32;
    }

    static int chunkX(long key) {
        return (int)key;
    }

    static int chunkZ(long key) {
        return (int)(key >>> 32);
    }

    public static String serverKey(String address) {
        String key;
        String string = key = address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
        if (key.endsWith(DEFAULT_PORT_SUFFIX)) {
            key = key.substring(0, key.length() - DEFAULT_PORT_SUFFIX.length());
        }
        return key.isEmpty() ? "unknown" : key;
    }

    public static String singleplayerKey(String worldFolder) {
        return "singleplayer/" + worldFolder;
    }

    public static void forEachInArea(int centerX, int centerZ, LongConsumer action) {
        for (int x = centerX - 2; x <= centerX + 2; ++x) {
            for (int z = centerZ - 2; z <= centerZ + 2; ++z) {
                action.accept(ProtectedChunkStore.key(x, z));
            }
        }
    }

    static String fileName(String server, String dimension) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest((server + "\n" + dimension).getBytes(StandardCharsets.UTF_8));
            String hash = HexFormat.of().formatHex(digest, 0, 8);
            return ProtectedChunkStore.readable(server) + "_" + ProtectedChunkStore.readable(dimension) + "-" + hash + ".json";
        }
        catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is always available", impossible);
        }
    }

    public void select(String server, String dimension) {
        if (server == null || dimension == null) {
            this.active = null;
            return;
        }
        String name = ProtectedChunkStore.fileName(server, dimension);
        Bucket bucket = this.buckets.get(name);
        if (bucket == null) {
            bucket = new Bucket(this.directory().resolve(name), server, dimension);
            ProtectedChunkStore.load(bucket);
            this.buckets.put(name, bucket);
        }
        this.active = bucket;
    }

    public boolean isProtected(long chunk) {
        return this.active != null && this.active.chunks.contains(chunk);
    }

    public boolean protect(long chunk) {
        if (this.active == null || !this.active.chunks.add(chunk)) {
            return false;
        }
        this.changed(this.active);
        return true;
    }

    public boolean flagAllowed(long chunk, boolean alreadyFlagged) {
        return alreadyFlagged || !this.isProtected(chunk);
    }

    public void tickAutosave() {
        this.retryFailedWrites();
        if (!this.dirty || System.nanoTime() - this.dirtySince < 10000000000L) {
            return;
        }
        this.saveChanged();
    }

    public void flush() {
        this.retryFailedWrites();
        if (this.dirty) {
            this.saveChanged();
        }
    }

    public void flushAndWait() {
        this.flush();
        try {
            this.lastWrite.get(10L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException | TimeoutException e) {
            LOGGER.warn("[Anubis] Player Bypass protected chunks may not all be saved", (Throwable)e);
        }
    }

    private void changed(Bucket bucket) {
        if (bucket.readOnly) {
            return;
        }
        bucket.dirty = true;
        if (!this.dirty) {
            this.dirty = true;
            this.dirtySince = System.nanoTime();
        }
    }

    private void retryFailedWrites() {
        if (!this.writeFailed) {
            return;
        }
        this.writeFailed = false;
        for (Bucket bucket : this.buckets.values()) {
            if (!bucket.writeFailed) continue;
            bucket.writeFailed = false;
            this.changed(bucket);
        }
    }

    private void saveChanged() {
        this.dirty = false;
        for (Bucket bucket : this.buckets.values()) {
            if (!bucket.dirty) continue;
            bucket.dirty = false;
            this.save(bucket);
        }
    }

    private void save(Bucket bucket) {
        if (this.queued.put(bucket.file, bucket.chunks.toLongArray()) != null) {
            return;
        }
        this.lastWrite = CompletableFuture.runAsync(() -> this.writeQueued(bucket), this.writer);
    }

    private void writeQueued(Bucket bucket) {
        long[] chunks = this.queued.remove(bucket.file);
        if (chunks == null) {
            return;
        }
        try {
            ProtectedChunkStore.write(bucket, chunks);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to save Player Bypass protected chunks to {}", (Object)bucket.file, (Object)e);
            bucket.writeFailed = true;
            this.writeFailed = true;
        }
    }

    private static void write(Bucket bucket, long[] chunks) throws IOException {
        Files.createDirectories(bucket.file.getParent(), new FileAttribute[0]);
        Path tmp = bucket.file.resolveSibling(String.valueOf(bucket.file.getFileName()) + ".tmp");
        try (JsonWriter json = new JsonWriter((Writer)Files.newBufferedWriter(tmp, new OpenOption[0]));){
            json.beginObject();
            json.name("version").value(1L);
            json.name("server").value(bucket.server);
            json.name("dimension").value(bucket.dimension);
            json.name("count").value((long)chunks.length);
            json.name("chunks").beginArray();
            for (long chunk : chunks) {
                json.value((long)ProtectedChunkStore.chunkX(chunk)).value((long)ProtectedChunkStore.chunkZ(chunk));
            }
            json.endArray();
            json.endObject();
        }
        try {
            Files.move(tmp, bucket.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException atomicFail) {
            Files.move(tmp, bucket.file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void load(Bucket bucket) {
        if (!Files.isRegularFile(bucket.file, new LinkOption[0])) {
            return;
        }
        int version = 0;
        try (JsonReader json = new JsonReader((Reader)Files.newBufferedReader(bucket.file));){
            long maxChunks = Files.size(bucket.file) / 4L;
            json.beginObject();
            block17: while (json.hasNext()) {
                switch (json.nextName()) {
                    case "version": {
                        version = json.nextInt();
                        break;
                    }
                    case "count": {
                        long count = ProtectedChunkStore.wholeInt(json);
                        if (count <= 0L) continue block17;
                        bucket.chunks.ensureCapacity((int)Math.min(count, maxChunks));
                        break;
                    }
                    case "chunks": {
                        if (json.peek() == JsonToken.BEGIN_ARRAY) {
                            ProtectedChunkStore.readChunks(json, bucket.chunks);
                            break;
                        }
                        json.skipValue();
                        break;
                    }
                    default: {
                        json.skipValue();
                    }
                }
            }
            json.endObject();
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load Player Bypass protected chunks from {}; preserving the file", (Object)bucket.file, (Object)e);
            bucket.chunks.clear();
            bucket.readOnly = true;
            return;
        }
        if (version > 1) {
            LOGGER.warn("[Anubis] Player Bypass protected chunks file {} is version {}, newer than supported {}; skipping load", new Object[]{bucket.file, version, 1});
            bucket.chunks.clear();
            bucket.readOnly = true;
        }
    }

    private static void readChunks(JsonReader json, LongOpenHashSet into) throws IOException {
        json.beginArray();
        int index = 0;
        long x = Long.MIN_VALUE;
        while (json.hasNext()) {
            long value = ProtectedChunkStore.wholeInt(json);
            if ((index++ & 1) == 0) {
                x = value;
                continue;
            }
            if (x == Long.MIN_VALUE || value == Long.MIN_VALUE) continue;
            into.add(ProtectedChunkStore.key((int)x, (int)value));
        }
        json.endArray();
    }

    private static long wholeInt(JsonReader json) throws IOException {
        if (json.peek() != JsonToken.NUMBER) {
            json.skipValue();
            return Long.MIN_VALUE;
        }
        double value = json.nextDouble();
        if (value != Math.rint(value) || value < -2.147483648E9 || value > 2.147483647E9) {
            return Long.MIN_VALUE;
        }
        return (long)value;
    }

    private static String readable(String key) {
        StringBuilder out = new StringBuilder(Math.min(key.length(), 40));
        for (int i = 0; i < key.length() && out.length() < 40; ++i) {
            int c = Character.toLowerCase(key.charAt(i));
            out.append((char)(c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 46 || c == 45 ? c : 95));
        }
        return out.toString();
    }

    private Path directory() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("playerbypass-protected");
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Bucket {
        final Path file;
        final String server;
        final String dimension;
        final LongOpenHashSet chunks = new LongOpenHashSet();
        boolean readOnly;
        boolean dirty;
        volatile boolean writeFailed;

        Bucket(Path file, String server, String dimension) {
            this.file = file;
            this.server = server;
            this.dimension = dimension;
        }
    }
}

