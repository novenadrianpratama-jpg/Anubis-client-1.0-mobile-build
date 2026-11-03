/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package net.anubis.client.easyplace.builder;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.anubis.client.easyplace.EasyPlace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
final class ScaffoldStore {
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "EasyPlace scaffold list");
        thread.setDaemon(true);
        return thread;
    });
    private final Object lock = new Object();
    private int savedHash;
    private String savedWorld;

    ScaffoldStore() {
    }

    private static Path file() {
        return class_310.method_1551().field_1697.toPath().resolve("config").resolve("easyplace-builder-scaffold.json");
    }

    static String worldKey(class_310 mc) {
        if (mc.field_1687 == null) {
            return null;
        }
        Object place = mc.method_1576() != null ? "local:" + mc.method_1576().method_27728().method_150() : (mc.method_1558() != null ? mc.method_1558().field_3761.toLowerCase(Locale.ROOT) : "unknown");
        return (String)place + " " + String.valueOf(mc.field_1687.method_27983());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Set<Long> load(String world) {
        HashSet<Long> out = new HashSet<Long>();
        Object object = this.lock;
        synchronized (object) {
            JsonObject root = ScaffoldStore.read();
            JsonElement jsonElement = root.get(world);
            if (jsonElement instanceof JsonArray) {
                JsonArray list = (JsonArray)jsonElement;
                for (JsonElement element : list) {
                    out.add(element.getAsLong());
                }
            }
        }
        this.savedWorld = world;
        this.savedHash = out.hashCode();
        return out;
    }

    void save(String world, Set<Long> scaffold) {
        if (world == null) {
            return;
        }
        int hash = scaffold.hashCode();
        if (world.equals(this.savedWorld) && hash == this.savedHash) {
            return;
        }
        this.savedWorld = world;
        this.savedHash = hash;
        long[] copy = scaffold.stream().mapToLong(Long::longValue).toArray();
        WRITER.execute(() -> this.write(world, copy));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void write(String world, long[] positions) {
        Object object = this.lock;
        synchronized (object) {
            JsonObject root = ScaffoldStore.read();
            if (positions.length == 0) {
                root.remove(world);
            } else {
                JsonArray list = new JsonArray();
                for (long position : positions) {
                    list.add((Number)position);
                }
                root.add(world, (JsonElement)list);
            }
            try {
                Path file = ScaffoldStore.file();
                Files.createDirectories(file.getParent(), new FileAttribute[0]);
                Path temp = file.resolveSibling(String.valueOf(file.getFileName()) + ".tmp");
                Files.writeString(temp, (CharSequence)new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)root), StandardCharsets.UTF_8, new OpenOption[0]);
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException e) {
                EasyPlace.LOG.warn("Could not save the builder's scaffold list", (Throwable)e);
            }
        }
    }

    private static JsonObject read() {
        Path file = ScaffoldStore.file();
        if (!Files.isRegularFile(file, new LinkOption[0])) {
            return new JsonObject();
        }
        try {
            JsonElement parsed = JsonParser.parseString((String)Files.readString(file, StandardCharsets.UTF_8));
            return parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();
        }
        catch (IOException | RuntimeException e) {
            EasyPlace.LOG.warn("Could not read the builder's scaffold list", (Throwable)e);
            return new JsonObject();
        }
    }
}

