/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_156
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.combat.bot;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_156;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class RivalBook {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Gson GSON = new Gson();
    private static final int VERSION = 1;
    private static final int MAX_FILES = 64;
    private static final long MAX_BYTES = 16384L;
    private static final float WEIGHT = 0.25f;
    static final int KNOCK_UP = 0;
    static final int CRYSTALS = 1;
    static final int OPENERS = 2;
    static final int BUCKETS = 3;
    private static final float[] PRIOR = new float[]{0.45f, 0.55f};
    private static final float FADE = 0.97f;
    private final Map<UUID, Rival> loaded = new HashMap<UUID, Rival>();
    private long lastSave;

    RivalBook() {
    }

    Rival of(UUID id) {
        return this.loaded.computeIfAbsent(id, RivalBook::read);
    }

    void save(boolean now) {
        long time = System.currentTimeMillis();
        if (!now && time - this.lastSave < 30000L) {
            return;
        }
        this.lastSave = time;
        ArrayList<Rival> changed = new ArrayList<Rival>();
        for (Rival rival : this.loaded.values()) {
            if (!rival.dirty) continue;
            rival.dirty = false;
            rival.seen = time;
            changed.add(rival.copy());
        }
        if (changed.isEmpty()) {
            return;
        }
        class_156.method_27958().execute(() -> {
            for (Rival rival : changed) {
                RivalBook.write(rival);
            }
            RivalBook.prune();
        });
    }

    private static Path folder() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("bots").resolve("rivals");
    }

    private static Rival read(UUID id) {
        Rival rival;
        Path file = RivalBook.folder().resolve(String.valueOf(id) + ".json");
        try {
            if (Files.isRegularFile(file, new LinkOption[0]) && Files.size(file) <= 16384L && (rival = (Rival)GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), Rival.class)) != null && rival.version == 1) {
                rival.id = id.toString();
                if (rival.openerReward == null || rival.openerReward.length != 6) {
                    rival.openerReward = new float[6];
                }
                if (rival.openerTries == null || rival.openerTries.length != 6) {
                    rival.openerTries = new float[6];
                }
                return rival;
            }
        }
        catch (JsonSyntaxException | IOException e) {
            LOGGER.warn("[Anubis] Could not read a crystal bot rival entry, starting it over", e);
        }
        rival = new Rival();
        rival.id = id.toString();
        return rival;
    }

    private static void write(Rival rival) {
        Path folder = RivalBook.folder();
        Path file = folder.resolve(String.valueOf(UUID.fromString(rival.id)) + ".json");
        try {
            Files.createDirectories(folder, new FileAttribute[0]);
            Path tmp = folder.resolve(String.valueOf(file.getFileName()) + ".tmp");
            Files.writeString(tmp, (CharSequence)GSON.toJson((Object)rival), StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException atomicFail) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("[Anubis] Could not save a crystal bot rival entry", (Throwable)e);
        }
    }

    private static void prune() {
        ArrayList<Path> files = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(RivalBook.folder(), "*.json");){
            for (Path file : stream) {
                files.add(file);
            }
        }
        catch (IOException e) {
            return;
        }
        if (files.size() <= 64) {
            return;
        }
        files.sort((a, b) -> Long.compare(RivalBook.modified(b), RivalBook.modified(a)));
        for (Path old : files.subList(64, files.size())) {
            try {
                Files.deleteIfExists(old);
            }
            catch (IOException iOException) {}
        }
    }

    private static long modified(Path file) {
        try {
            return Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
        }
        catch (IOException e) {
            return 0L;
        }
    }

    private static float average(float value, int samples, float sample) {
        return samples == 0 ? sample : value + (sample - value) * 0.25f;
    }

    @Environment(value=EnvType.CLIENT)
    static final class Rival {
        int version = 1;
        String id;
        long seen;
        int fights;
        float launchRate = 0.5f;
        int launches;
        float airAlong;
        float airAcross;
        int flights;
        float retotemTicks;
        int retotems;
        float holeTicks;
        int holes;
        float pearlLowRate;
        int lowSpells;
        float[] openerReward = new float[6];
        float[] openerTries = new float[6];
        transient boolean dirty;

        Rival() {
        }

        void fought() {
            ++this.fights;
            this.dirty = true;
        }

        void launched(boolean lifted) {
            this.launchRate = RivalBook.average(this.launchRate, this.launches++, lifted ? 1.0f : 0.0f);
            this.dirty = true;
        }

        void flew(double along, double across) {
            this.airAlong = RivalBook.average(this.airAlong, this.flights, (float)along);
            this.airAcross = RivalBook.average(this.airAcross, this.flights++, (float)across);
            this.dirty = true;
        }

        boolean knowsFlight() {
            return this.flights >= 2;
        }

        void retotemed(int ticks) {
            this.retotemTicks = RivalBook.average(this.retotemTicks, this.retotems++, ticks);
            this.dirty = true;
        }

        void leftHole(int ticks) {
            this.holeTicks = RivalBook.average(this.holeTicks, this.holes++, ticks);
            this.dirty = true;
        }

        void lowSpell(boolean pearled) {
            this.pearlLowRate = RivalBook.average(this.pearlLowRate, this.lowSpells++, pearled ? 1.0f : 0.0f);
            this.dirty = true;
        }

        boolean shrugsOffLaunches() {
            return this.launches >= 4 && this.launchRate < 0.35f;
        }

        int retotemWindow(int fallback) {
            return this.retotems >= 2 ? Math.round(this.retotemTicks) : fallback;
        }

        boolean campsInHoles() {
            return this.holes >= 2 && this.holeTicks >= 100.0f;
        }

        int opener(double reach) {
            int bucket = Rival.bucket(reach);
            float all = 0.0f;
            for (int arm = 0; arm < 2; ++arm) {
                all += this.openerTries[arm * 3 + bucket];
            }
            int best = 0;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int arm = 0; arm < 2; ++arm) {
                float tries = this.openerTries[arm * 3 + bucket];
                if (tries < 0.5f) {
                    return arm;
                }
                double mean = (this.openerReward[arm * 3 + bucket] + PRIOR[arm]) / (tries + 1.0f);
                double score = mean + 0.5 * Math.sqrt(Math.log((double)all + 1.0) / (double)tries) + ThreadLocalRandom.current().nextDouble(0.02);
                if (!(score > bestScore)) continue;
                bestScore = score;
                best = arm;
            }
            return best;
        }

        void opened(int arm, double reach, float reward) {
            int at = arm * 3 + Rival.bucket(reach);
            for (int i = 0; i < this.openerTries.length; ++i) {
                if (i % 3 != at % 3) continue;
                int n = i;
                this.openerReward[n] = this.openerReward[n] * 0.97f;
                int n2 = i;
                this.openerTries[n2] = this.openerTries[n2] * 0.97f;
            }
            int n = at;
            this.openerReward[n] = this.openerReward[n] + reward;
            int n3 = at;
            this.openerTries[n3] = this.openerTries[n3] + 1.0f;
            this.dirty = true;
        }

        private static int bucket(double reach) {
            return reach <= 2.6 ? 0 : (reach <= 3.0 ? 1 : 2);
        }

        boolean pearlsWhenLow() {
            return this.lowSpells >= 2 && this.pearlLowRate >= 0.5f;
        }

        private Rival copy() {
            return (Rival)GSON.fromJson(GSON.toJson((Object)this), Rival.class);
        }
    }
}

