/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  imgui.type.ImBoolean
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2248
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imgui.type.ImBoolean;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class BlockColorListSetting
extends Setting {
    static final String CHANGES = "changes";
    private static final String VANILLA = "minecraft:";
    private List<BlockEntry> entries;
    private Supplier<List<BlockEntry>> entriesSupplier;
    private final Supplier<List<BlockEntry>> shipped;
    private final float[] defaultColor;
    private final float[] shippedDefaultColor;
    private final boolean colorsHidden;
    private final List<String> defaultOrder;
    private final Map<String, BlockEntry> byId = new HashMap<String, BlockEntry>();
    private final ArrayList<BlockEntry> selection = new ArrayList();
    private final ReferenceOpenHashSet<BlockEntry> selectionSet = new ReferenceOpenHashSet();
    private final List<BlockEntry> selectionView = Collections.unmodifiableList(this.selection);
    private long selectionVersion = -1L;
    private Map<String, Integer> enabledLookup;
    private Map<String, Integer> tracerLookup;
    private Reference2IntOpenHashMap<class_2248> enabledByBlock;
    private Reference2IntOpenHashMap<class_2248> tracerByBlock;
    private long lookupVersion = -1L;
    private long currentVersion = 0L;
    private static final String WALL_BANNER = "_wall_banner";

    public BlockColorListSetting(String name, List<BlockEntry> entries, float[] defaultColor) {
        super(name);
        this.entries = new ArrayList<BlockEntry>(entries);
        List<BlockEntry> shippedEntries = entries.stream().map(BlockColorListSetting::copyEntry).toList();
        this.shipped = () -> shippedEntries;
        this.defaultColor = defaultColor;
        this.shippedDefaultColor = BlockColorListSetting.copyColor(defaultColor);
        this.colorsHidden = false;
        this.defaultOrder = List.of();
        this.index();
        this.onChange(this::invalidateLookup);
    }

    public BlockColorListSetting(String name, Supplier<List<BlockEntry>> entriesSupplier, float[] defaultColor) {
        this(name, entriesSupplier, defaultColor, false, List.of());
    }

    private BlockColorListSetting(String name, Supplier<List<BlockEntry>> entriesSupplier, float[] defaultColor, boolean colorsHidden, List<String> defaultOrder) {
        super(name);
        this.entriesSupplier = entriesSupplier;
        this.shipped = entriesSupplier;
        this.defaultColor = defaultColor;
        this.shippedDefaultColor = BlockColorListSetting.copyColor(defaultColor);
        this.colorsHidden = colorsHidden;
        this.defaultOrder = defaultOrder;
        this.onChange(this::invalidateLookup);
    }

    public boolean colorsHidden() {
        return this.colorsHidden;
    }

    private List<BlockEntry> entriesInternal() {
        if (this.entries == null) {
            this.entries = new ArrayList<BlockEntry>((Collection)this.entriesSupplier.get());
            this.entriesSupplier = null;
            this.index();
        }
        return this.entries;
    }

    private void index() {
        for (BlockEntry entry : this.entries) {
            this.byId.putIfAbsent(entry.id(), entry);
        }
        for (String id : this.defaultOrder) {
            BlockEntry entry = this.byId.get(id);
            if (entry == null || !entry.enabled().get()) continue;
            this.addToSelection(entry);
        }
        for (BlockEntry entry : this.entries) {
            if (!entry.enabled().get()) continue;
            this.addToSelection(entry);
        }
        this.selectionVersion = this.currentVersion;
    }

    public List<BlockEntry> entries() {
        return Collections.unmodifiableList(this.entriesInternal());
    }

    public BlockEntry entry(String id) {
        this.entriesInternal();
        return this.byId.get(id);
    }

    public float[] defaultColor() {
        return this.defaultColor;
    }

    public void resetColors() {
        this.entriesInternal();
        for (BlockEntry original : this.shipped.get()) {
            BlockEntry entry = this.byId.get(original.id());
            if (entry == null) continue;
            System.arraycopy(original.color(), 0, entry.color(), 0, 4);
            entry.tracerEnabled().set(original.tracerEnabled().get());
            System.arraycopy(original.tracerColor(), 0, entry.tracerColor(), 0, 4);
        }
        System.arraycopy(this.shippedDefaultColor, 0, this.defaultColor, 0, 4);
        this.notifyChanged();
    }

    public List<BlockEntry> selected() {
        List<BlockEntry> all = this.entriesInternal();
        if (this.selectionVersion != this.currentVersion) {
            for (int i = this.selection.size() - 1; i >= 0; --i) {
                BlockEntry entry = this.selection.get(i);
                if (entry.enabled().get()) continue;
                this.selection.remove(i);
                this.selectionSet.remove((Object)entry);
            }
            for (BlockEntry entry : all) {
                if (!entry.enabled().get()) continue;
                this.addToSelection(entry);
            }
            this.selectionVersion = this.currentVersion;
        }
        return this.selectionView;
    }

    public boolean select(BlockEntry entry) {
        this.selected();
        if (!this.addToSelection(entry)) {
            return false;
        }
        entry.enabled().set(true);
        this.notifyChanged();
        return true;
    }

    public boolean deselect(BlockEntry entry) {
        this.selected();
        if (!this.selectionSet.remove((Object)entry)) {
            return false;
        }
        for (int i = 0; i < this.selection.size(); ++i) {
            if (this.selection.get(i) != entry) continue;
            this.selection.remove(i);
            break;
        }
        entry.enabled().set(false);
        this.notifyChanged();
        return true;
    }

    public int selectAll(Iterable<BlockEntry> toAdd) {
        this.selected();
        int added = 0;
        for (BlockEntry entry : toAdd) {
            if (!this.addToSelection(entry)) continue;
            entry.enabled().set(true);
            ++added;
        }
        if (added > 0) {
            this.notifyChanged();
        }
        return added;
    }

    public int deselectAll() {
        this.selected();
        int removed = this.selection.size();
        if (removed == 0) {
            return 0;
        }
        for (BlockEntry entry : this.selection) {
            entry.enabled().set(false);
        }
        this.selection.clear();
        this.selectionSet.clear();
        this.notifyChanged();
        return removed;
    }

    private boolean addToSelection(BlockEntry entry) {
        if (!this.selectionSet.add((Object)entry)) {
            return false;
        }
        this.selection.add(entry);
        return true;
    }

    public void invalidateLookup() {
        ++this.currentVersion;
    }

    private Map<String, Integer> lookup() {
        if (this.enabledLookup == null || this.lookupVersion != this.currentVersion) {
            this.rebuildLookup();
        }
        return this.enabledLookup;
    }

    private void rebuildLookup() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        HashMap<String, Integer> tracers = new HashMap<String, Integer>();
        Reference2IntOpenHashMap byBlock = new Reference2IntOpenHashMap();
        Reference2IntOpenHashMap tracerByBlockMap = new Reference2IntOpenHashMap();
        for (BlockEntry entry : this.entriesInternal()) {
            if (!entry.enabled().get()) continue;
            BlockEntry look = this.colorSource(entry);
            int argb = BlockColorListSetting.rgbaToArgb(look.color());
            map.put(entry.id(), argb);
            class_2248 block = BlockColorListSetting.resolveBlock(entry.id());
            if (block != null) {
                byBlock.put((Object)block, argb);
            }
            if (!look.tracerEnabled().get()) continue;
            int tracerArgb = BlockColorListSetting.rgbaToArgb(look.tracerColor());
            tracers.put(entry.id(), tracerArgb);
            if (block == null) continue;
            tracerByBlockMap.put((Object)block, tracerArgb);
        }
        this.enabledLookup = map;
        this.tracerLookup = tracers;
        this.enabledByBlock = byBlock;
        this.tracerByBlock = tracerByBlockMap;
        this.lookupVersion = this.currentVersion;
    }

    private BlockEntry colorSource(BlockEntry entry) {
        String id = entry.id();
        if (!id.endsWith(WALL_BANNER)) {
            return entry;
        }
        BlockEntry banner = this.byId.get(id.substring(0, id.length() - WALL_BANNER.length()) + "_banner");
        return banner != null && banner.enabled().get() ? banner : entry;
    }

    private static class_2248 resolveBlock(String id) {
        class_2960 key = class_2960.method_12829((String)id);
        return key == null ? null : (class_2248)class_7923.field_41175.method_17966(key).orElse(null);
    }

    private Map<String, Integer> tracerLookup() {
        if (this.tracerLookup == null || this.lookupVersion != this.currentVersion) {
            this.rebuildLookup();
        }
        return this.tracerLookup;
    }

    private Reference2IntOpenHashMap<class_2248> blockLookup() {
        if (this.enabledByBlock == null || this.lookupVersion != this.currentVersion) {
            this.rebuildLookup();
        }
        return this.enabledByBlock;
    }

    private Reference2IntOpenHashMap<class_2248> tracerBlockLookup() {
        if (this.tracerByBlock == null || this.lookupVersion != this.currentVersion) {
            this.rebuildLookup();
        }
        return this.tracerByBlock;
    }

    public boolean isEnabled(class_2248 block) {
        return this.blockLookup().containsKey((Object)block);
    }

    public Set<class_2248> enabledBlocks() {
        return Collections.unmodifiableSet(this.blockLookup().keySet());
    }

    public int colorArgb(class_2248 block) {
        Reference2IntOpenHashMap<class_2248> map = this.blockLookup();
        return map.containsKey((Object)block) ? map.getInt((Object)block) : BlockColorListSetting.rgbaToArgb(this.defaultColor);
    }

    public boolean hasAnyEnabled() {
        return !this.lookup().isEmpty();
    }

    public Set<String> enabledIds() {
        return this.lookup().keySet();
    }

    public int tracerColorArgb(class_2248 block) {
        Reference2IntOpenHashMap<class_2248> map = this.tracerBlockLookup();
        return map.containsKey((Object)block) ? map.getInt((Object)block) : 0;
    }

    public boolean hasAnyTracerEnabled() {
        return !this.tracerLookup().isEmpty();
    }

    @Override
    public JsonElement serialize() {
        JsonObject root = new JsonObject();
        root.add("defaultColor", (JsonElement)BlockColorListSetting.colorJson(this.defaultColor));
        JsonArray arr = new JsonArray();
        for (BlockEntry e : this.selected()) {
            arr.add((JsonElement)BlockColorListSetting.entryJson(e));
        }
        for (BlockEntry e : this.entriesInternal()) {
            if (this.selectionSet.contains((Object)e)) continue;
            arr.add((JsonElement)BlockColorListSetting.entryJson(e));
        }
        root.add("blocks", (JsonElement)arr);
        return root;
    }

    private static JsonObject entryJson(BlockEntry e) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", e.id());
        obj.addProperty("enabled", Boolean.valueOf(e.enabled().get()));
        obj.add("color", (JsonElement)BlockColorListSetting.colorJson(e.color()));
        obj.addProperty("tracerEnabled", Boolean.valueOf(e.tracerEnabled().get()));
        obj.add("tracerColor", (JsonElement)BlockColorListSetting.colorJson(e.tracerColor()));
        return obj;
    }

    private static JsonArray colorJson(float[] rgba) {
        JsonArray arr = new JsonArray();
        for (float v : rgba) {
            arr.add((Number)Float.valueOf(BlockColorListSetting.clamp01(v)));
        }
        return arr;
    }

    @Override
    public JsonElement compact(JsonElement value) {
        JsonElement defaults = this.defaultJson();
        if (value == null || !value.isJsonObject() || defaults == null || !defaults.isJsonObject()) {
            return value;
        }
        JsonObject root = value.getAsJsonObject();
        JsonObject shipped = defaults.getAsJsonObject();
        if (root.has(CHANGES) || !root.has("blocks") || !shipped.has("blocks")) {
            return value;
        }
        try {
            HashMap<String, String[]> before = new HashMap<String, String[]>();
            for (Object el : shipped.getAsJsonArray("blocks")) {
                JsonObject obj = el.getAsJsonObject();
                before.put(obj.get("id").getAsString(), BlockColorListSetting.fields(obj));
            }
            JsonObject changes = new JsonObject();
            for (JsonElement el : root.getAsJsonArray("blocks")) {
                if (!el.isJsonObject() || !el.getAsJsonObject().has("id")) continue;
                JsonObject obj = el.getAsJsonObject();
                String id = obj.get("id").getAsString();
                Object[] now = BlockColorListSetting.fields(obj);
                Object[] was = (String[])before.get(id);
                if (was != null && Arrays.equals(now, was)) continue;
                boolean tracer = was == null || !((String)now[2]).equals(was[2]) || !((String)now[3]).equals(was[3]);
                StringBuilder change = new StringBuilder((String)now[0]);
                if (tracer || !((String)now[1]).equals(was[1])) {
                    change.append(' ').append((String)now[1]);
                }
                if (tracer) {
                    change.append(' ').append((String)now[2]).append(' ').append((String)now[3]);
                }
                changes.addProperty(BlockColorListSetting.shortId(id), change.toString());
            }
            JsonObject out = new JsonObject();
            String color = BlockColorListSetting.hexColor(root.get("defaultColor"));
            if (color != null && !color.equals(BlockColorListSetting.hexColor(shipped.get("defaultColor")))) {
                out.addProperty("defaultColor", color);
            }
            out.add(CHANGES, (JsonElement)changes);
            return out;
        }
        catch (RuntimeException malformed) {
            return value;
        }
    }

    @Override
    protected void deserializeValue(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        try {
            JsonObject root = element.getAsJsonObject();
            if (root.has(CHANGES) && root.get(CHANGES).isJsonObject()) {
                this.applyChanges(root);
            } else {
                this.applyFull(root);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void applyFull(JsonObject root) {
        if (root.has("defaultColor")) {
            BlockColorListSetting.readColor(root.getAsJsonArray("defaultColor"), this.defaultColor);
        }
        if (root.has("blocks")) {
            JsonArray arr = root.getAsJsonArray("blocks");
            ArrayList<BlockEntry> previous = new ArrayList<BlockEntry>(this.selected());
            this.selection.clear();
            this.selectionSet.clear();
            for (JsonElement el : arr) {
                BlockEntry entry;
                JsonObject obj;
                if (!el.isJsonObject() || !(obj = el.getAsJsonObject()).has("id") || (entry = this.byId.get(obj.get("id").getAsString())) == null) continue;
                if (obj.has("enabled")) {
                    entry.enabled().set(obj.get("enabled").getAsBoolean());
                }
                if (obj.has("color")) {
                    BlockColorListSetting.readColor(obj.getAsJsonArray("color"), entry.color());
                }
                if (obj.has("tracerEnabled")) {
                    entry.tracerEnabled().set(obj.get("tracerEnabled").getAsBoolean());
                }
                if (obj.has("tracerColor")) {
                    BlockColorListSetting.readColor(obj.getAsJsonArray("tracerColor"), entry.tracerColor());
                }
                if (!entry.enabled().get()) continue;
                this.addToSelection(entry);
            }
            for (BlockEntry entry : previous) {
                if (!entry.enabled().get()) continue;
                this.addToSelection(entry);
            }
        }
    }

    private void applyChanges(JsonObject root) {
        this.entriesInternal();
        JsonElement defaults = this.defaultJson();
        if (defaults != null && defaults.isJsonObject()) {
            this.applyFull(defaults.getAsJsonObject());
        }
        if (root.has("defaultColor")) {
            BlockColorListSetting.readHex(root.get("defaultColor").getAsString(), this.defaultColor);
        }
        for (Map.Entry change : root.getAsJsonObject(CHANGES).entrySet()) {
            float[] tracerColor;
            float[] color;
            BlockEntry entry = this.byId.get(BlockColorListSetting.fullId((String)change.getKey()));
            if (entry == null || !((JsonElement)change.getValue()).isJsonPrimitive()) continue;
            String[] parts = ((JsonElement)change.getValue()).getAsString().split(" ");
            try {
                color = parts.length >= 2 ? BlockColorListSetting.parseHex(parts[1]) : null;
                tracerColor = parts.length >= 4 ? BlockColorListSetting.parseHex(parts[3]) : null;
            }
            catch (NumberFormatException damaged) {
                continue;
            }
            entry.enabled().set("1".equals(parts[0]));
            if (color != null) {
                System.arraycopy(color, 0, entry.color(), 0, 4);
            }
            if (tracerColor != null) {
                entry.tracerEnabled().set("1".equals(parts[2]));
                System.arraycopy(tracerColor, 0, entry.tracerColor(), 0, 4);
            }
            if (!entry.enabled().get()) continue;
            this.addToSelection(entry);
        }
    }

    private static String[] fields(JsonObject obj) {
        return new String[]{BlockColorListSetting.flag(obj, "enabled"), String.valueOf(BlockColorListSetting.hexColor(obj.get("color"))), BlockColorListSetting.flag(obj, "tracerEnabled"), String.valueOf(BlockColorListSetting.hexColor(obj.get("tracerColor")))};
    }

    private static String flag(JsonObject obj, String key) {
        return obj.has(key) && obj.get(key).getAsBoolean() ? "1" : "0";
    }

    static String hexColor(JsonElement color) {
        if (color == null || !color.isJsonArray()) {
            return null;
        }
        float[] rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        BlockColorListSetting.readColor(color.getAsJsonArray(), rgba);
        return ColorSetting.hex(BlockColorListSetting.rgbaToArgb(rgba));
    }

    static void readHex(String hex, float[] into) {
        System.arraycopy(BlockColorListSetting.parseHex(hex), 0, into, 0, 4);
    }

    static float[] parseHex(String hex) {
        int argb = (int)Long.parseLong(hex, 16);
        return new float[]{(float)(argb >> 16 & 0xFF) / 255.0f, (float)(argb >> 8 & 0xFF) / 255.0f, (float)(argb & 0xFF) / 255.0f, (float)(argb >>> 24) / 255.0f};
    }

    static String shortId(String id) {
        return id.startsWith(VANILLA) ? id.substring(VANILLA.length()) : id;
    }

    static String fullId(String id) {
        return id.indexOf(58) < 0 ? VANILLA + id : id;
    }

    private static void readColor(JsonArray from, float[] into) {
        for (int i = 0; i < Math.min(4, from.size()); ++i) {
            into[i] = BlockColorListSetting.clamp01(from.get(i).getAsFloat());
        }
    }

    public static BlockColorListSetting createDefault() {
        return new BlockColorListSetting("Block Colors", () -> BlockColorListSetting.buildEntries(id -> false), BlockColorListSetting.white());
    }

    public static BlockColorListSetting blockList(String name, List<String> defaults) {
        Set<String> enabled = Set.copyOf(defaults);
        return new BlockColorListSetting(name, () -> BlockColorListSetting.buildEntries(enabled::contains), BlockColorListSetting.white(), true, List.copyOf(defaults));
    }

    private static List<BlockEntry> buildEntries(Predicate<String> enabledByDefault) {
        ArrayList<BlockEntry> e = new ArrayList<BlockEntry>();
        for (class_2248 block : class_7923.field_41175) {
            class_2960 key;
            if (block.method_9564().method_26215() || (key = class_7923.field_41175.method_10221((Object)block)) == null) continue;
            String id = key.toString();
            e.add(BlockColorListSetting.entry(id, BlockColorListSetting.prettify(key.method_12832()), "Blocks", enabledByDefault.test(id), 1.0f, 1.0f, 1.0f, 1.0f));
        }
        return e;
    }

    public static BlockColorListSetting createForBlockEntityDebug() {
        return new BlockColorListSetting("Block Colors", BlockColorListSetting::buildBlockEntityDebugEntries, BlockColorListSetting.white());
    }

    private static List<BlockEntry> buildBlockEntityDebugEntries() {
        ArrayList<BlockEntry> e = new ArrayList<BlockEntry>();
        for (class_2248 block : class_7923.field_41175) {
            class_2960 key;
            if (block.method_9564().method_26215() || (key = class_7923.field_41175.method_10221((Object)block)) == null) continue;
            String path = key.method_12832();
            int rgb = BlockColorListSetting.blockEntityDebugRgb(path);
            boolean enabled = rgb != -1 || BlockColorListSetting.blockEntityDebugPicked(path);
            e.add(BlockColorListSetting.entry(key.toString(), BlockColorListSetting.prettify(path), "Blocks", enabled, rgb == -1 ? 0xFFFFFF : rgb, true));
        }
        return e;
    }

    private static int blockEntityDebugRgb(String path) {
        if (path.endsWith("copper_chest")) {
            return 10246912;
        }
        if (path.endsWith("shulker_box")) {
            return 8781982;
        }
        return switch (path) {
            case "chest" -> 10246912;
            case "trapped_chest" -> 0xD23232;
            case "ender_chest" -> 7667967;
            case "barrel" -> 6572850;
            case "hopper" -> 0x505050;
            case "dispenser", "dropper" -> 7887420;
            case "brewing_stand" -> 7874760;
            case "furnace", "blast_furnace", "smoker" -> 0x646464;
            case "spawner" -> 9076390;
            case "enchanting_table" -> 0xC80000;
            case "crafter" -> 3978400;
            case "moving_piston", "piston", "sticky_piston", "piston_head" -> 65280;
            case "lectern" -> 13806160;
            case "jukebox" -> 2017380;
            case "decorated_pot" -> 11822120;
            case "beehive", "bee_nest" -> 15779840;
            case "beacon" -> 61660;
            case "conduit" -> 46320;
            case "comparator" -> 13120050;
            default -> -1;
        };
    }

    private static boolean blockEntityDebugPicked(String path) {
        if (path.contains("sign") || path.endsWith("_bed") || path.endsWith("_banner") || path.endsWith("_skull") || path.endsWith("_head")) {
            return true;
        }
        return switch (path) {
            case "bell", "campfire", "soul_campfire", "jigsaw", "structure_block", "command_block", "chain_command_block", "repeating_command_block", "end_portal", "end_gateway", "sculk_sensor", "calibrated_sculk_sensor", "sculk_catalyst", "sculk_shrieker", "suspicious_sand", "suspicious_gravel", "trial_spawner", "vault", "chiseled_bookshelf", "daylight_detector" -> true;
            default -> false;
        };
    }

    private static String prettify(String path) {
        String[] words = path.split("_");
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < words.length; ++i) {
            if (words[i].isEmpty()) continue;
            display.append(Character.toUpperCase(words[i].charAt(0)));
            display.append(words[i].substring(1));
            if (i >= words.length - 1) continue;
            display.append(" ");
        }
        return display.toString();
    }

    private static float[] white() {
        return new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    }

    private static BlockEntry entry(String id, String display, String cat, boolean enabled, float r, float g, float b, float a) {
        float[] color = new float[]{r, g, b, a};
        return new BlockEntry(id, display, cat, new ImBoolean(enabled), color, new ImBoolean(false), BlockColorListSetting.copyColor(color));
    }

    private static BlockEntry entry(String id, String display, String cat, boolean enabled, int rgb, boolean tracer) {
        float[] color = new float[]{(float)(rgb >> 16 & 0xFF) / 255.0f, (float)(rgb >> 8 & 0xFF) / 255.0f, (float)(rgb & 0xFF) / 255.0f, 1.0f};
        return new BlockEntry(id, display, cat, new ImBoolean(enabled), color, new ImBoolean(tracer), BlockColorListSetting.copyColor(color));
    }

    private static BlockEntry copyEntry(BlockEntry e) {
        return new BlockEntry(e.id(), e.displayName(), e.category(), new ImBoolean(e.enabled().get()), BlockColorListSetting.copyColor(e.color()), new ImBoolean(e.tracerEnabled().get()), BlockColorListSetting.copyColor(e.tracerColor()));
    }

    private static float[] copyColor(float[] rgba) {
        return new float[]{rgba[0], rgba[1], rgba[2], rgba[3]};
    }

    public static int rgbaToArgb(float[] rgba) {
        int r = Math.round(BlockColorListSetting.clamp01(rgba[0]) * 255.0f);
        int g = Math.round(BlockColorListSetting.clamp01(rgba[1]) * 255.0f);
        int b = Math.round(BlockColorListSetting.clamp01(rgba[2]) * 255.0f);
        int a = Math.round(BlockColorListSetting.clamp01(rgba[3]) * 255.0f);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static float clamp01(float v) {
        if (Float.isNaN(v) || Float.isInfinite(v)) {
            return 1.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    @Environment(value=EnvType.CLIENT)
    public record BlockEntry(String id, String displayName, String category, ImBoolean enabled, float[] color, ImBoolean tracerEnabled, float[] tracerColor) {
    }
}

