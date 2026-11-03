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
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonToken
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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.config.BundledDefaults;
import net.anubis.client.config.FriendStore;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.ModuleConfigStore;
import net.anubis.client.config.ProfileCodes;
import net.anubis.client.config.ProfileException;
import net.anubis.client.config.ServerAddresses;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.HudLayoutStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ConfigProfiles {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final ConfigProfiles INSTANCE = new ConfigProfiles();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern VALID_NAME = Pattern.compile("[\\p{L}\\p{Nd} _()-]{1,32}");
    private static final Pattern NUMBERED = Pattern.compile("(.+) \\(\\d{1,3}\\)");
    private static final Set<String> RESERVED = Set.of("con", "prn", "aux", "nul", "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9", "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9");
    private static final String EXTENSION = ".json";
    private static final String FORMAT_KEY = "anubisProfile";
    private static final int FORMAT = 2;
    private static final int OLDEST_FORMAT = 2;
    public static final String TOO_OLD = "Made by an old Anubis - old configs no longer work";
    private static final String UPGRADED_MARKER = ".format2";
    private static final String PRESETS_MARKER = ".presets";
    private static final String PRIVATE_MARKER = ".private-values-removed";
    private static final int MAX_NAME = 32;
    private static final int MAX_FRIENDS = 1000;
    private static final long MAX_LOCAL_BYTES = 0x2000000L;
    public static final int MAX_SERVERS = 16;
    public static final String NAME_RULE = "Use 1-32 letters, digits, spaces, _ - ( )";
    public static final String BACKUP_NAME = "Auto backup";
    private final ModuleConfigStore store;
    private final Map<String, Header> headers = new HashMap<String, Header>();
    private List<Profile> cached;
    private boolean upgradeChecked;
    private boolean presetsChecked;
    private boolean privateChecked;

    private ConfigProfiles() {
        this(ModuleConfigStore.getInstance());
    }

    private ConfigProfiles(ModuleConfigStore store) {
        this.store = store;
    }

    public static ConfigProfiles getInstance() {
        return INSTANCE;
    }

    public static boolean isValidName(String name) {
        return name != null && name.equals(name.trim()) && VALID_NAME.matcher(name).matches() && !RESERVED.contains(name.toLowerCase(Locale.ROOT));
    }

    public Path directory() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("profiles").toAbsolutePath().normalize();
    }

    public synchronized List<Profile> list() {
        if (this.cached == null) {
            this.cached = this.scan();
        }
        return this.cached;
    }

    public synchronized void refresh() {
        this.cached = null;
    }

    public synchronized Profile find(String name) {
        if (name == null) {
            return null;
        }
        for (Profile profile : this.list()) {
            if (!profile.name().equalsIgnoreCase(name)) continue;
            return profile;
        }
        return null;
    }

    public synchronized String create(String name) throws ProfileException {
        String wanted;
        String string = wanted = name == null ? "" : name.trim();
        if (!ConfigProfiles.isValidName(wanted)) {
            throw new ProfileException(NAME_RULE);
        }
        Profile existing = this.find(wanted);
        if (existing != null) {
            throw new ProfileException(existing.name() + " already exists - use its Save button");
        }
        if (Files.exists(this.file(wanted), new LinkOption[0])) {
            this.cached = null;
            throw new ProfileException(wanted + " already exists - use its Save button");
        }
        Contents contents = new Contents();
        contents.data.put(Part.MODULES, this.capture(Part.MODULES));
        this.write(wanted, contents);
        this.activate(wanted);
        return wanted;
    }

    public synchronized Set<Part> save(String name) throws ProfileException {
        Profile profile = this.require(name);
        Contents contents = this.read(profile.name());
        EnumSet<Part> parts = contents.data.isEmpty() ? EnumSet.of(Part.MODULES) : EnumSet.copyOf(contents.data.keySet());
        for (Part part : parts) {
            contents.data.put(part, this.capture(part));
        }
        this.write(profile.name(), contents);
        this.activate(profile.name());
        return Collections.unmodifiableSet(parts);
    }

    public synchronized Set<Part> load(String name) throws ProfileException {
        Profile profile = this.require(name);
        Contents contents = this.read(profile.name());
        EnumSet<Part> applied = EnumSet.noneOf(Part.class);
        for (Map.Entry<Part, JsonElement> entry : contents.data.entrySet()) {
            if (!this.apply(entry.getKey(), entry.getValue())) continue;
            applied.add(entry.getKey());
        }
        if (applied.isEmpty()) {
            throw new ProfileException("Nothing in " + profile.name() + " could be applied");
        }
        this.activate(profile.name());
        return Collections.unmodifiableSet(applied);
    }

    public synchronized void delete(String name) throws ProfileException {
        Profile profile = this.require(name);
        try {
            Files.deleteIfExists(this.file(profile.name()));
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Failed to delete profile '{}'", (Object)profile.name(), (Object)e);
            throw new ProfileException("Could not delete " + profile.name());
        }
        this.cached = null;
        if (profile.name().equalsIgnoreCase(this.store.activeProfile())) {
            this.store.setActiveProfile(null);
            this.store.save();
        }
    }

    public synchronized String rename(String name, String newName) throws ProfileException {
        String wanted;
        Profile profile;
        block9: {
            boolean sameFile;
            profile = this.require(name);
            String string = wanted = newName == null ? "" : newName.trim();
            if (wanted.equals(profile.name())) {
                return wanted;
            }
            if (!ConfigProfiles.isValidName(wanted)) {
                throw new ProfileException(NAME_RULE);
            }
            Profile clash = this.find(wanted);
            boolean bl = sameFile = clash != null && clash.name().equals(profile.name());
            if (clash != null && !sameFile) {
                throw new ProfileException(clash.name() + " already exists");
            }
            Path from = this.file(profile.name());
            Path to = this.file(wanted);
            try {
                if (sameFile) {
                    Path step = from.resolveSibling(profile.name() + ".rename.tmp");
                    Files.move(from, step, StandardCopyOption.REPLACE_EXISTING);
                    try {
                        Files.move(step, to, new CopyOption[0]);
                        break block9;
                    }
                    catch (IOException e) {
                        Files.move(step, from, StandardCopyOption.REPLACE_EXISTING);
                        throw e;
                    }
                }
                Files.move(from, to, new CopyOption[0]);
            }
            catch (IOException e) {
                LOGGER.warn("[Anubis] Failed to rename profile '{}' to '{}'", new Object[]{profile.name(), wanted, e});
                throw new ProfileException("Could not rename " + profile.name());
            }
        }
        this.cached = null;
        if (profile.name().equalsIgnoreCase(this.store.activeProfile())) {
            this.store.setActiveProfile(wanted);
            this.store.save();
        }
        return wanted;
    }

    public synchronized void setPart(String name, Part part, boolean included) throws ProfileException {
        Profile profile = this.require(name);
        Contents contents = this.read(profile.name());
        if (included == contents.data.containsKey((Object)part)) {
            return;
        }
        if (included) {
            contents.data.put(part, this.capture(part));
        } else {
            if (contents.data.size() == 1) {
                throw new ProfileException("A profile needs at least one part");
            }
            contents.data.remove((Object)part);
        }
        this.write(profile.name(), contents);
    }

    public synchronized String addServer(String name, String address) throws ProfileException {
        ServerAddresses.Address parsed = ServerAddresses.parse(address);
        if (parsed == null) {
            throw new ProfileException("That isn't a server address");
        }
        Profile profile = this.require(name);
        Contents contents = this.read(profile.name());
        String shown = parsed.toString();
        if (contents.servers.contains(shown)) {
            throw new ProfileException(shown + " is already on the list");
        }
        if (contents.servers.size() >= 16) {
            throw new ProfileException("A profile can list up to 16 servers");
        }
        contents.servers.add(shown);
        this.write(profile.name(), contents);
        return shown;
    }

    public synchronized void removeServer(String name, String address) throws ProfileException {
        Profile profile = this.require(name);
        Contents contents = this.read(profile.name());
        if (contents.servers.remove(address)) {
            this.write(profile.name(), contents);
        }
    }

    public synchronized void exportTo(String name, Path target) throws ProfileException {
        JsonObject shared = this.exportJson(name);
        try {
            Files.writeString(target, (CharSequence)GSON.toJson((JsonElement)shared), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to export profile '{}' to {}", new Object[]{name, target, e});
            throw new ProfileException("Could not write " + String.valueOf(target.getFileName()));
        }
    }

    public synchronized String shareCode(String name) throws ProfileException {
        return ProfileCodes.encode(this.exportJson(name));
    }

    public synchronized String importFile(Path source) throws ProfileException {
        String text;
        try {
            if (!Files.isRegularFile(source, new LinkOption[0])) {
                throw new ProfileException("That file doesn't exist");
            }
            if (Files.size(source) > 0x400000L) {
                throw new ProfileException("That file is too large to be a profile");
            }
            text = Files.readString(source, StandardCharsets.UTF_8);
        }
        catch (IOException | RuntimeException e) {
            throw new ProfileException("Couldn't read " + String.valueOf(source.getFileName()));
        }
        String fileName = source.getFileName().toString();
        int dot = fileName.lastIndexOf(46);
        String fallback = dot > 0 ? fileName.substring(0, dot) : fileName;
        return this.importRoot(ProfileCodes.parseObject(text, "That file isn't an Anubis profile"), fallback);
    }

    public synchronized String importCode(String text) throws ProfileException {
        try {
            return this.importRoot(ProfileCodes.decode(text), "Shared profile");
        }
        catch (ProfileException e) {
            LOGGER.warn("[Anubis] Share code import failed ({} characters on the clipboard): {}", (Object)(text == null ? 0 : text.length()), (Object)e.getMessage());
            throw e;
        }
    }

    public synchronized void backup(Set<Part> parts) throws ProfileException {
        Contents contents = new Contents();
        for (Part part : parts) {
            contents.data.put(part, this.capture(part));
        }
        if (contents.data.isEmpty()) {
            contents.data.put(Part.MODULES, this.capture(Part.MODULES));
        }
        this.write(BACKUP_NAME, contents);
        this.cached = null;
    }

    public synchronized List<String> loadForServer(String address) {
        ServerAddresses.Address joined = ServerAddresses.parse(address);
        if (joined == null) {
            return List.of();
        }
        this.cached = null;
        ArrayList<Match> matches = new ArrayList<Match>();
        for (Profile profile : this.list()) {
            if (profile.broken()) continue;
            int best = 0;
            int length = 0;
            for (String server : profile.servers()) {
                ServerAddresses.Address entry = ServerAddresses.parse(server);
                int score = ServerAddresses.score(entry, joined);
                if (score <= best && (score != best || score <= 0 || entry.host().length() <= length)) continue;
                best = score;
                length = entry.host().length();
            }
            if (best <= 0) continue;
            matches.add(new Match(profile.name(), best, length));
        }
        matches.sort(Comparator.comparingInt(Match::score).thenComparingInt(Match::length).thenComparing(match -> match.name().toLowerCase(Locale.ROOT)));
        ArrayList<String> loaded = new ArrayList<String>();
        for (Match match2 : matches) {
            try {
                this.load(match2.name());
                loaded.add(match2.name());
            }
            catch (ProfileException e) {
                LOGGER.warn("[Anubis] Could not load profile '{}' for {}: {}", new Object[]{match2.name(), joined, e.getMessage()});
            }
        }
        return loaded;
    }

    private Profile require(String name) throws ProfileException {
        Profile profile = this.find(name);
        if (profile == null) {
            throw new ProfileException("That profile no longer exists");
        }
        return profile;
    }

    private void activate(String name) {
        this.store.setActiveProfile(name);
        this.store.save();
    }

    private JsonObject exportJson(String name) throws ProfileException {
        Profile profile = this.require(name);
        Contents contents = this.shareable(this.read(profile.name()), "Could not prepare " + profile.name() + " for sharing");
        if (contents.data.isEmpty()) {
            throw new ProfileException(profile.name() + " has nothing to share");
        }
        JsonObject root = new JsonObject();
        root.addProperty(FORMAT_KEY, (Number)2);
        root.addProperty("name", profile.name());
        root.add("data", (JsonElement)ConfigProfiles.data(contents));
        return root;
    }

    private String importRoot(JsonObject root, String fallbackName) throws ProfileException {
        Contents contents = this.shareable(ConfigProfiles.parse(root, false, false), "That profile is damaged");
        if (contents.data.isEmpty()) {
            throw new ProfileException("That profile has nothing Anubis can use");
        }
        JsonElement named = root.get("name");
        String wanted = named != null && named.isJsonPrimitive() ? named.getAsString() : fallbackName;
        String name = this.uniqueName(wanted);
        this.write(name, contents);
        return name;
    }

    private JsonElement capture(Part part) throws ProfileException {
        try {
            return switch (part.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> this.store.snapshot();
                case 1 -> {
                    JsonObject hud = new JsonObject();
                    hud.add("layout", (JsonElement)HudLayoutStore.getInstance().profileSnapshot());
                    hud.add("theme", (JsonElement)HudTheme.get().profileSnapshot());
                    yield hud;
                }
                case 2 -> ThemeStore.get().profileSnapshot();
                case 3 -> {
                    JsonArray friends = new JsonArray();
                    for (String friend : FriendStore.getInstance().all()) {
                        friends.add(friend);
                    }
                    yield friends;
                }
            };
        }
        catch (RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to capture the {} part of a profile", (Object)part.key, (Object)e);
            throw new ProfileException("Could not read your current " + part.label);
        }
    }

    private boolean apply(Part part, JsonElement data) {
        try {
            return switch (part.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    if (data.isJsonObject() && this.store.applyProfile(data.getAsJsonObject())) {
                        yield true;
                    }
                    yield false;
                }
                case 1 -> ConfigProfiles.applyHud(data);
                case 2 -> {
                    if (!data.isJsonObject()) {
                        yield false;
                    }
                    ThemeStore theme = ThemeStore.get();
                    theme.applyProfile(data.getAsJsonObject());
                    theme.save();
                    yield true;
                }
                case 3 -> {
                    if (!data.isJsonArray()) {
                        yield false;
                    }
                    FriendStore.getInstance().replaceAll(ConfigProfiles.friendNames(data.getAsJsonArray()));
                    yield true;
                }
            };
        }
        catch (RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to apply the {} part of a profile", (Object)part.key, (Object)e);
            return false;
        }
    }

    private static boolean applyHud(JsonElement data) {
        if (!data.isJsonObject()) {
            return false;
        }
        JsonObject hud = data.getAsJsonObject();
        boolean applied = false;
        if (hud.has("layout") && hud.get("layout").isJsonObject()) {
            applied = HudLayoutStore.getInstance().applyProfile(hud.getAsJsonObject("layout"));
        }
        if (hud.has("theme") && hud.get("theme").isJsonObject()) {
            HudTheme theme = HudTheme.get();
            theme.applyProfile(hud.getAsJsonObject("theme"));
            theme.save();
            applied = true;
        }
        return applied;
    }

    private Contents shareable(Contents contents, String failure) throws ProfileException {
        Contents kept = new Contents();
        try {
            for (Map.Entry<Part, JsonElement> entry : contents.data.entrySet()) {
                JsonElement value = this.shareable(entry.getKey(), entry.getValue());
                if (value == null) continue;
                kept.data.put(entry.getKey(), value);
            }
        }
        catch (RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to filter a profile for sharing", (Throwable)e);
            throw new ProfileException(failure);
        }
        return kept;
    }

    private JsonElement shareable(Part part, JsonElement data) {
        return switch (part.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if (data.isJsonObject()) {
                    yield this.store.shareable(data.getAsJsonObject());
                }
                yield null;
            }
            case 1 -> {
                JsonObject layout;
                if (!data.isJsonObject()) {
                    yield null;
                }
                JsonObject hud = data.getAsJsonObject();
                JsonObject kept = new JsonObject();
                if (hud.has("layout") && hud.get("layout").isJsonObject() && (layout = HudLayoutStore.shareable(hud.getAsJsonObject("layout"))) != null) {
                    kept.add("layout", (JsonElement)layout);
                }
                if (hud.has("theme") && hud.get("theme").isJsonObject()) {
                    kept.add("theme", (JsonElement)HudTheme.get().shareable(hud.getAsJsonObject("theme")));
                }
                if (kept.size() == 0) {
                    yield null;
                }
                yield kept;
            }
            case 2 -> {
                if (data.isJsonObject()) {
                    yield ThemeStore.get().shareable(data.getAsJsonObject());
                }
                yield null;
            }
            case 3 -> {
                if (!data.isJsonArray()) {
                    yield null;
                }
                JsonArray kept = new JsonArray();
                for (String name : ConfigProfiles.friendNames(data.getAsJsonArray())) {
                    kept.add(name);
                }
                yield kept;
            }
        };
    }

    private static List<String> friendNames(JsonArray array) {
        LinkedHashMap<String, String> names = new LinkedHashMap<String, String>();
        for (JsonElement element : array) {
            String name;
            if (names.size() >= 1000) break;
            if (!element.isJsonPrimitive() || !FriendStore.isValidName(name = element.getAsString().trim())) continue;
            names.putIfAbsent(name.toLowerCase(Locale.ROOT), name);
        }
        return List.copyOf(names.values());
    }

    private static Contents parse(JsonObject root, boolean withServers, boolean allowOld) throws ProfileException {
        Contents contents = new Contents();
        if (root.has(FORMAT_KEY)) {
            int format;
            try {
                format = root.get(FORMAT_KEY).getAsInt();
            }
            catch (RuntimeException e) {
                throw new ProfileException("That isn't an Anubis profile");
            }
            if (format > 2) {
                throw new ProfileException("Made by a newer Anubis - update to open it");
            }
            if (format < 1) {
                throw new ProfileException("That isn't an Anubis profile");
            }
            if (format < 2 && !allowOld) {
                throw new ProfileException(TOO_OLD);
            }
            JsonElement data = root.get("data");
            if (data != null && data.isJsonObject()) {
                for (Part part : Part.values()) {
                    JsonElement value = data.getAsJsonObject().get(part.key);
                    if (value == null || !(part == Part.FRIENDS ? value.isJsonArray() : value.isJsonObject())) continue;
                    contents.data.put(part, value);
                }
            }
            JsonElement servers = root.get("loadOnJoin");
            if (withServers && servers != null && servers.isJsonArray()) {
                for (JsonElement server : servers.getAsJsonArray()) {
                    if (!server.isJsonPrimitive()) continue;
                    ConfigProfiles.addServer(contents.servers, server.getAsString());
                }
            }
        } else if (root.has("modules") && root.get("modules").isJsonObject()) {
            if (!allowOld) {
                throw new ProfileException(TOO_OLD);
            }
            contents.data.put(Part.MODULES, (JsonElement)root);
        } else {
            throw new ProfileException("That isn't an Anubis profile");
        }
        return contents;
    }

    private static void addServer(List<String> servers, String text) {
        ServerAddresses.Address address = ServerAddresses.parse(text);
        if (address == null || servers.size() >= 16) {
            return;
        }
        String shown = address.toString();
        if (!servers.contains(shown)) {
            servers.add(shown);
        }
    }

    private static JsonObject data(Contents contents) {
        JsonObject data = new JsonObject();
        for (Map.Entry<Part, JsonElement> entry : contents.data.entrySet()) {
            data.add(entry.getKey().key, entry.getValue());
        }
        return data;
    }

    private Contents read(String name) throws ProfileException {
        Contents contents;
        block10: {
            Path path = this.file(name);
            if (Files.size(path) > 0x2000000L) {
                throw new ProfileException(name + " is too large to open");
            }
            BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            try {
                JsonElement parsed = JsonParser.parseReader((Reader)reader);
                if (!parsed.isJsonObject()) {
                    throw new ProfileException(name + " isn't a profile file");
                }
                contents = ConfigProfiles.parse(parsed.getAsJsonObject(), true, false);
                if (reader == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException | RuntimeException e) {
                    LOGGER.warn("[Anubis] Failed to read profile '{}'", (Object)name, (Object)e);
                    throw new ProfileException("Could not read " + name);
                }
            }
            ((Reader)reader).close();
        }
        return contents;
    }

    private void write(String name, Contents contents) throws ProfileException {
        JsonElement modules = contents.data.get((Object)Part.MODULES);
        if (modules != null && modules.isJsonObject()) {
            contents.data.put(Part.MODULES, (JsonElement)ModuleConfigStore.withoutPrivateValues(modules.getAsJsonObject()));
        }
        JsonObject root = new JsonObject();
        root.addProperty(FORMAT_KEY, (Number)2);
        JsonArray servers = new JsonArray();
        for (String server : contents.servers) {
            servers.add(server);
        }
        root.add("loadOnJoin", (JsonElement)servers);
        root.add("data", (JsonElement)ConfigProfiles.data(contents));
        Path path = this.file(name);
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Path tmp = path.resolveSibling(name + ".json.tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
            try {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException atomicFail) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to save profile '{}'", (Object)name, (Object)e);
            throw new ProfileException("Could not save " + name);
        }
        this.cached = null;
    }

    private Path file(String name) throws ProfileException {
        Path path;
        if (!ConfigProfiles.isValidName(name)) {
            throw new ProfileException(NAME_RULE);
        }
        Path dir = this.directory();
        if (!dir.equals((path = dir.resolve(name + EXTENSION).normalize()).getParent())) {
            throw new ProfileException(NAME_RULE);
        }
        return path;
    }

    private String uniqueName(String wanted) throws ProfileException {
        String base = ConfigProfiles.cleanName(wanted);
        if (!this.taken(base)) {
            return base;
        }
        Matcher numbered = NUMBERED.matcher(base);
        String root = numbered.matches() ? numbered.group(1) : base;
        for (int i = 2; i < 1000; ++i) {
            String suffix = " (" + i + ")";
            String candidate = ConfigProfiles.cut(root, 32 - suffix.length()).stripTrailing() + suffix;
            if (!ConfigProfiles.isValidName(candidate) || this.taken(candidate)) continue;
            return candidate;
        }
        throw new ProfileException("Too many profiles share that name");
    }

    private boolean taken(String name) {
        return this.find(name) != null || Files.exists(this.directory().resolve(name + EXTENSION), new LinkOption[0]);
    }

    private static String cleanName(String raw) {
        int cp;
        StringBuilder out = new StringBuilder();
        String text = raw == null ? "" : raw;
        for (int i = 0; i < text.length(); i += Character.charCount(cp)) {
            cp = text.codePointAt(i);
            if (!Character.isLetter(cp) && !Character.isDigit(cp) && "_-()".indexOf(cp) < 0) continue;
            out.appendCodePoint(cp);
        }
        String name = ConfigProfiles.cut(out.toString().replaceAll(" {2,}", " ").trim(), 32).trim();
        return ConfigProfiles.isValidName(name) ? name : "Imported";
    }

    private static String cut(String text, int codePoints) {
        if (text.codePointCount(0, text.length()) <= codePoints) {
            return text;
        }
        return text.substring(0, text.offsetByCodePoints(0, codePoints));
    }

    private List<Profile> scan() {
        Path dir = this.directory();
        this.upgradeLocalProfiles(dir);
        this.removePrivateValues(dir);
        this.installPresets(dir);
        if (!Files.isDirectory(dir, new LinkOption[0])) {
            this.headers.clear();
            return List.of();
        }
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        HashSet<String> seen = new HashSet<String>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*.json");){
            for (Path file : files) {
                String fileName = file.getFileName().toString();
                String name = fileName.substring(0, fileName.length() - EXTENSION.length());
                if (!ConfigProfiles.isValidName(name) || !Files.isRegularFile(file, new LinkOption[0]) || !seen.add(name.toLowerCase(Locale.ROOT))) continue;
                profiles.add(this.header(name, file));
            }
        }
        catch (IOException e) {
            LOGGER.warn("[Anubis] Failed to list profiles", (Throwable)e);
        }
        HashSet<String> names = new HashSet<String>();
        for (Profile profile2 : profiles) {
            names.add(profile2.name());
        }
        this.headers.keySet().retainAll(names);
        profiles.sort(Comparator.comparing(profile -> profile.name().toLowerCase(Locale.ROOT)));
        return List.copyOf(profiles);
    }

    private void upgradeLocalProfiles(Path dir) {
        if (this.upgradeChecked) {
            return;
        }
        this.upgradeChecked = true;
        Path marker = dir.resolve(UPGRADED_MARKER);
        try {
            if (Files.exists(marker, new LinkOption[0])) {
                return;
            }
            Files.createDirectories(dir, new FileAttribute[0]);
            try (DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*.json");){
                for (Path file : files) {
                    String fileName = file.getFileName().toString();
                    String name = fileName.substring(0, fileName.length() - EXTENSION.length());
                    if (!ConfigProfiles.isValidName(name) || !Files.isRegularFile(file, new LinkOption[0]) || Files.size(file) > 0x2000000L) continue;
                    this.upgrade(name, file);
                }
            }
            Files.writeString(marker, (CharSequence)"Profiles here were moved to format 2.\n", StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to move old profiles to the current format", (Throwable)e);
        }
    }

    private void removePrivateValues(Path dir) {
        if (this.privateChecked) {
            return;
        }
        this.privateChecked = true;
        Path marker = dir.resolve(PRIVATE_MARKER);
        try {
            if (Files.exists(marker, new LinkOption[0]) || !Files.isDirectory(dir, new LinkOption[0])) {
                return;
            }
            try (DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*.json");){
                for (Path file : files) {
                    String fileName = file.getFileName().toString();
                    String name = fileName.substring(0, fileName.length() - EXTENSION.length());
                    if (!ConfigProfiles.isValidName(name) || !Files.isRegularFile(file, new LinkOption[0]) || Files.size(file) > 0x2000000L) continue;
                    try {
                        Contents contents = this.read(name);
                        JsonElement modules = contents.data.get((Object)Part.MODULES);
                        if (modules == null || !modules.isJsonObject() || ModuleConfigStore.withoutPrivateValues(modules.getAsJsonObject()).equals((Object)modules)) continue;
                        this.write(name, contents);
                    }
                    catch (ProfileException e) {
                        LOGGER.warn("[Anubis] Couldn't clean private values out of profile '{}'", (Object)name);
                    }
                }
            }
            Files.writeString(marker, (CharSequence)"Private values were removed from the profiles here.\n", StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to clean private values out of the profiles", (Throwable)e);
        }
    }

    private void installPresets(Path dir) {
        if (this.presetsChecked) {
            return;
        }
        this.presetsChecked = true;
        List<JsonObject> presets = BundledDefaults.presets();
        if (presets.isEmpty()) {
            return;
        }
        Path marker = dir.resolve(PRESETS_MARKER);
        try {
            ArrayList<String> installed = new ArrayList<String>();
            if (Files.isRegularFile(marker, new LinkOption[0])) {
                for (String line : Files.readAllLines(marker, StandardCharsets.UTF_8)) {
                    if (line.isBlank()) continue;
                    installed.add(line.trim().toLowerCase(Locale.ROOT));
                }
            }
            int before = installed.size();
            for (JsonObject preset : presets) {
                String name;
                String key;
                JsonElement named = preset.get("name");
                if (named == null || !named.isJsonPrimitive() || installed.contains(key = (name = named.getAsString()).toLowerCase(Locale.ROOT))) continue;
                try {
                    Path path = this.file(name);
                    Contents contents = this.shareable(ConfigProfiles.parse(preset, false, false), "That profile is damaged");
                    if (!Files.exists(path, new LinkOption[0]) && !contents.data.isEmpty()) {
                        this.write(name, contents);
                    }
                    installed.add(key);
                }
                catch (ProfileException e) {
                    LOGGER.warn("[Anubis] Could not add the bundled profile '{}': {}", (Object)name, (Object)e.getMessage());
                }
            }
            if (installed.size() == before) {
                return;
            }
            Files.createDirectories(dir, new FileAttribute[0]);
            Files.write(marker, installed, StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to add the bundled profiles", (Throwable)e);
        }
    }

    private void upgrade(String name, Path file) {
        try {
            JsonElement parsed;
            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);){
                parsed = JsonParser.parseReader((Reader)reader);
            }
            if (!parsed.isJsonObject()) {
                return;
            }
            JsonObject root = parsed.getAsJsonObject();
            JsonElement format = root.get(FORMAT_KEY);
            if (format != null && format.isJsonPrimitive() && format.getAsInt() >= 2) {
                return;
            }
            this.write(name, ConfigProfiles.parse(root, true, true));
            LOGGER.info("[Anubis] Moved profile '{}' to the current format", (Object)name);
        }
        catch (IOException | RuntimeException | ProfileException e) {
            LOGGER.warn("[Anubis] Could not move profile '{}' to the current format", (Object)name, (Object)e);
        }
    }

    private Profile header(String name, Path file) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class, new LinkOption[0]);
            Header known = this.headers.get(name);
            if (known != null && known.modified().equals(attributes.lastModifiedTime()) && known.size() == attributes.size()) {
                return known.profile();
            }
            Profile profile = ConfigProfiles.readHeader(name, file);
            this.headers.put(name, new Header(attributes.lastModifiedTime(), attributes.size(), profile));
            return profile;
        }
        catch (IOException e) {
            return new Profile(name, Set.of(), List.of(), true);
        }
    }

    private static Profile readHeader(String name, Path file) {
        EnumSet<Part> parts = EnumSet.noneOf(Part.class);
        ArrayList<String> servers = new ArrayList<String>();
        int format = -1;
        boolean legacy = false;
        try (JsonReader reader = new JsonReader((Reader)Files.newBufferedReader(file, StandardCharsets.UTF_8));){
            reader.beginObject();
            block19: while (reader.hasNext()) {
                switch (reader.nextName()) {
                    case "anubisProfile": {
                        format = reader.nextInt();
                        continue block19;
                    }
                    case "loadOnJoin": {
                        ConfigProfiles.readServers(reader, servers);
                        continue block19;
                    }
                    case "data": {
                        ConfigProfiles.readParts(reader, parts);
                        continue block19;
                    }
                    case "modules": {
                        legacy = reader.peek() == JsonToken.BEGIN_OBJECT;
                        reader.skipValue();
                        continue block19;
                    }
                }
                reader.skipValue();
            }
            reader.endObject();
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to read profile '{}'", (Object)name, (Object)e);
            return new Profile(name, Set.of(), List.of(), true);
        }
        if (format < 0 && legacy) {
            return new Profile(name, Collections.unmodifiableSet(EnumSet.of(Part.MODULES)), List.of(), true);
        }
        boolean broken = format < 2 || format > 2 || parts.isEmpty();
        return new Profile(name, Collections.unmodifiableSet(parts), List.copyOf(servers), broken);
    }

    private static void readServers(JsonReader reader, List<String> servers) throws IOException {
        if (reader.peek() != JsonToken.BEGIN_ARRAY) {
            reader.skipValue();
            return;
        }
        reader.beginArray();
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.STRING) {
                ConfigProfiles.addServer(servers, reader.nextString());
                continue;
            }
            reader.skipValue();
        }
        reader.endArray();
    }

    private static void readParts(JsonReader reader, Set<Part> parts) throws IOException {
        if (reader.peek() != JsonToken.BEGIN_OBJECT) {
            reader.skipValue();
            return;
        }
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            JsonToken token = reader.peek();
            for (Part part : Part.values()) {
                if (!part.key.equals(key) || token != (part == Part.FRIENDS ? JsonToken.BEGIN_ARRAY : JsonToken.BEGIN_OBJECT)) continue;
                parts.add(part);
            }
            reader.skipValue();
        }
        reader.endObject();
    }

    @Environment(value=EnvType.CLIENT)
    public record Profile(String name, Set<Part> parts, List<String> servers, boolean broken) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Contents {
        final List<String> servers = new ArrayList<String>();
        final Map<Part, JsonElement> data = new EnumMap<Part, JsonElement>(Part.class);

        private Contents() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Part {
        MODULES("modules", "Modules"),
        HUD("hud", "HUD"),
        THEME("theme", "Theme"),
        FRIENDS("friends", "Friends");

        public final String key;
        public final String label;

        private Part(String key, String label) {
            this.key = key;
            this.label = label;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Match(String name, int score, int length) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Header(FileTime modified, long size, Profile profile) {
    }
}

