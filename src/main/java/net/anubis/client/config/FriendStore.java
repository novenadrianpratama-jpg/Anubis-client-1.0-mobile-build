/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class FriendStore {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final FriendStore INSTANCE = new FriendStore();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 1;
    private static final Pattern VALID_NAME = Pattern.compile("[.*]?[A-Za-z0-9_]{1,16}");
    private final Map<String, String> friends = new LinkedHashMap<String, String>();
    private boolean loaded;
    private boolean readOnly;

    private FriendStore() {
    }

    public static FriendStore getInstance() {
        return INSTANCE;
    }

    public static boolean isValidName(String name) {
        return name != null && VALID_NAME.matcher(name).matches();
    }

    public synchronized boolean isFriend(String name) {
        this.ensureLoaded();
        return name != null && this.friends.containsKey(name.toLowerCase(Locale.ROOT));
    }

    public synchronized List<String> all() {
        this.ensureLoaded();
        return List.copyOf(this.friends.values());
    }

    public synchronized boolean add(String name) {
        String trimmed;
        this.ensureLoaded();
        String string = trimmed = name == null ? "" : name.trim();
        if (!FriendStore.isValidName(trimmed)) {
            return false;
        }
        if (this.friends.putIfAbsent(trimmed.toLowerCase(Locale.ROOT), trimmed) != null) {
            return false;
        }
        this.save();
        return true;
    }

    public synchronized boolean remove(String name) {
        this.ensureLoaded();
        if (name == null || this.friends.remove(name.toLowerCase(Locale.ROOT)) == null) {
            return false;
        }
        this.save();
        return true;
    }

    public synchronized void replaceAll(Collection<String> names) {
        this.ensureLoaded();
        this.friends.clear();
        for (String name : names) {
            String trimmed = name == null ? "" : name.trim();
            if (!FriendStore.isValidName(trimmed)) continue;
            this.friends.putIfAbsent(trimmed.toLowerCase(Locale.ROOT), trimmed);
        }
        this.save();
    }

    public synchronized void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        this.friends.clear();
        Path path = this.configPath();
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            int version;
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            int n = version = root.has("version") ? root.get("version").getAsInt() : 0;
            if (version > 1) {
                this.readOnly = true;
                LOGGER.warn("[Anubis] Friend list version {} is newer than supported {}; skipping load", (Object)version, (Object)1);
                return;
            }
            if (!root.has("friends") || !root.get("friends").isJsonArray()) {
                return;
            }
            for (JsonElement element : root.getAsJsonArray("friends")) {
                String name;
                if (!element.isJsonPrimitive() || !FriendStore.isValidName(name = element.getAsString().trim())) continue;
                this.friends.putIfAbsent(name.toLowerCase(Locale.ROOT), name);
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load friend list; preserving the file", (Throwable)e);
            this.friends.clear();
            this.readOnly = true;
        }
    }

    public synchronized void save() {
        if (this.readOnly) {
            return;
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)1);
        JsonArray list = new JsonArray();
        for (String name : this.friends.values()) {
            list.add(name);
        }
        root.add("friends", (JsonElement)list);
        Path path = this.configPath();
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Path tmp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, new OpenOption[0]);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException atomicFail) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Failed to save friend list", (Throwable)e);
        }
    }

    private void ensureLoaded() {
        if (!this.loaded) {
            this.load();
        }
    }

    private Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("friends.json");
    }
}

