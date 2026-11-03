/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  imgui.type.ImBoolean
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imgui.type.ImBoolean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.anubis.client.module.setting.BlockColorListSetting;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class ItemColorListSetting
extends Setting {
    private List<ItemEntry> entries;
    private Supplier<List<ItemEntry>> entriesSupplier;
    private final float[] defaultColor;
    private Map<String, Integer> enabledLookup;

    public ItemColorListSetting(String name, List<ItemEntry> entries, float[] defaultColor) {
        super(name);
        this.entries = new ArrayList<ItemEntry>(entries);
        this.defaultColor = defaultColor;
        this.onChange(this::invalidateLookup);
    }

    public ItemColorListSetting(String name, Supplier<List<ItemEntry>> entriesSupplier, float[] defaultColor) {
        super(name);
        this.entriesSupplier = entriesSupplier;
        this.defaultColor = defaultColor;
        this.onChange(this::invalidateLookup);
    }

    private List<ItemEntry> entriesInternal() {
        if (this.entries == null) {
            this.entries = new ArrayList<ItemEntry>((Collection)this.entriesSupplier.get());
            this.entriesSupplier = null;
        }
        return this.entries;
    }

    public List<ItemEntry> entries() {
        return Collections.unmodifiableList(this.entriesInternal());
    }

    public float[] defaultColor() {
        return this.defaultColor;
    }

    public void invalidateLookup() {
        this.enabledLookup = null;
    }

    private Map<String, Integer> lookup() {
        if (this.enabledLookup == null) {
            this.rebuildLookup();
        }
        return this.enabledLookup;
    }

    private void rebuildLookup() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (ItemEntry entry : this.entriesInternal()) {
            if (!entry.enabled().get()) continue;
            map.put(entry.id(), ItemColorListSetting.rgbaToArgb(entry.color()));
        }
        this.enabledLookup = map;
    }

    public boolean isEnabled(class_1792 item) {
        class_2960 key = class_7923.field_41178.method_10221((Object)item);
        if (key == null) {
            return false;
        }
        return this.lookup().containsKey(key.toString());
    }

    public int colorArgb(class_1792 item) {
        class_2960 key = class_7923.field_41178.method_10221((Object)item);
        if (key == null) {
            return ItemColorListSetting.rgbaToArgb(this.defaultColor);
        }
        Integer color = this.lookup().get(key.toString());
        return color != null ? color : ItemColorListSetting.rgbaToArgb(this.defaultColor);
    }

    public boolean hasAnyEnabled() {
        return !this.lookup().isEmpty();
    }

    @Override
    public JsonElement serialize() {
        JsonObject root = new JsonObject();
        JsonArray defCol = new JsonArray();
        for (float v : this.defaultColor) {
            defCol.add((Number)Float.valueOf(ItemColorListSetting.clamp01(v)));
        }
        root.add("defaultColor", (JsonElement)defCol);
        JsonArray arr = new JsonArray();
        for (ItemEntry e : this.entriesInternal()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", e.id());
            obj.addProperty("enabled", Boolean.valueOf(e.enabled().get()));
            JsonArray col = new JsonArray();
            for (float v : e.color()) {
                col.add((Number)Float.valueOf(ItemColorListSetting.clamp01(v)));
            }
            obj.add("color", (JsonElement)col);
            arr.add((JsonElement)obj);
        }
        root.add("items", (JsonElement)arr);
        return root;
    }

    @Override
    public JsonElement compact(JsonElement value) {
        JsonElement defaults = this.defaultJson();
        if (value == null || !value.isJsonObject() || defaults == null || !defaults.isJsonObject()) {
            return value;
        }
        JsonObject root = value.getAsJsonObject();
        JsonObject shipped = defaults.getAsJsonObject();
        if (root.has("changes") || !root.has("items") || !shipped.has("items")) {
            return value;
        }
        try {
            HashMap<String, String> before = new HashMap<String, String>();
            for (Object el : shipped.getAsJsonArray("items")) {
                JsonObject obj = el.getAsJsonObject();
                before.put(obj.get("id").getAsString(), ItemColorListSetting.packItem(obj));
            }
            JsonObject changes = new JsonObject();
            for (JsonElement el : root.getAsJsonArray("items")) {
                String was;
                if (!el.isJsonObject() || !el.getAsJsonObject().has("id")) continue;
                JsonObject obj = el.getAsJsonObject();
                String id = obj.get("id").getAsString();
                String now = ItemColorListSetting.packItem(obj);
                if (now.equals(was = (String)before.get(id))) continue;
                boolean sameColor = was != null && now.substring(2).equals(was.substring(2));
                changes.addProperty(BlockColorListSetting.shortId(id), sameColor ? now.substring(0, 1) : now);
            }
            JsonObject out = new JsonObject();
            String color = BlockColorListSetting.hexColor(root.get("defaultColor"));
            if (color != null && !color.equals(BlockColorListSetting.hexColor(shipped.get("defaultColor")))) {
                out.addProperty("defaultColor", color);
            }
            out.add("changes", (JsonElement)changes);
            return out;
        }
        catch (RuntimeException malformed) {
            return value;
        }
    }

    private static String packItem(JsonObject obj) {
        boolean on = obj.has("enabled") && obj.get("enabled").getAsBoolean();
        return (on ? "1" : "0") + " " + BlockColorListSetting.hexColor(obj.get("color"));
    }

    @Override
    protected void deserializeValue(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        try {
            JsonObject root = element.getAsJsonObject();
            if (root.has("changes") && root.get("changes").isJsonObject()) {
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
            JsonArray defCol = root.getAsJsonArray("defaultColor");
            for (int i = 0; i < Math.min(4, defCol.size()); ++i) {
                this.defaultColor[i] = ItemColorListSetting.clamp01(defCol.get(i).getAsFloat());
            }
        }
        if (root.has("items")) {
            Map<String, ItemEntry> byId = this.byId();
            for (JsonElement el : root.getAsJsonArray("items")) {
                ItemEntry entry;
                JsonObject obj;
                if (!el.isJsonObject() || !(obj = el.getAsJsonObject()).has("id") || (entry = byId.get(obj.get("id").getAsString())) == null) continue;
                if (obj.has("enabled")) {
                    entry.enabled().set(obj.get("enabled").getAsBoolean());
                }
                if (!obj.has("color")) continue;
                JsonArray col = obj.getAsJsonArray("color");
                float[] c = entry.color();
                for (int i = 0; i < Math.min(4, col.size()); ++i) {
                    c[i] = ItemColorListSetting.clamp01(col.get(i).getAsFloat());
                }
            }
        }
    }

    private void applyChanges(JsonObject root) {
        JsonElement defaults = this.defaultJson();
        if (defaults != null && defaults.isJsonObject()) {
            this.applyFull(defaults.getAsJsonObject());
        }
        if (root.has("defaultColor")) {
            BlockColorListSetting.readHex(root.get("defaultColor").getAsString(), this.defaultColor);
        }
        Map<String, ItemEntry> byId = this.byId();
        for (Map.Entry change : root.getAsJsonObject("changes").entrySet()) {
            float[] color;
            ItemEntry entry = byId.get(BlockColorListSetting.fullId((String)change.getKey()));
            if (entry == null || !((JsonElement)change.getValue()).isJsonPrimitive()) continue;
            String[] parts = ((JsonElement)change.getValue()).getAsString().split(" ");
            try {
                color = parts.length >= 2 ? BlockColorListSetting.parseHex(parts[1]) : null;
            }
            catch (NumberFormatException damaged) {
                continue;
            }
            entry.enabled().set("1".equals(parts[0]));
            if (color == null) continue;
            System.arraycopy(color, 0, entry.color(), 0, 4);
        }
    }

    private Map<String, ItemEntry> byId() {
        HashMap<String, ItemEntry> map = new HashMap<String, ItemEntry>();
        for (ItemEntry entry : this.entriesInternal()) {
            map.putIfAbsent(entry.id(), entry);
        }
        return map;
    }

    public static ItemColorListSetting createDefault() {
        return new ItemColorListSetting("Item Colors", ItemColorListSetting::buildDefaultEntries, new float[]{1.0f, 1.0f, 1.0f, 0.8f});
    }

    private static List<ItemEntry> buildDefaultEntries() {
        ArrayList<ItemEntry> e = new ArrayList<ItemEntry>();
        for (class_1792 item : class_7923.field_41178) {
            class_2960 key = class_7923.field_41178.method_10221((Object)item);
            if (key == null) continue;
            String id = key.toString();
            String path = key.method_12832();
            String displayName = ItemColorListSetting.prettify(path);
            float[] color = ItemColorListSetting.defaultColorFor(path);
            boolean enabled = ItemColorListSetting.isPreEnabled(path);
            e.add(ItemColorListSetting.entry(id, displayName, enabled, color));
        }
        e.sort((a, b) -> a.displayName().compareToIgnoreCase(b.displayName()));
        return e;
    }

    private static String prettify(String path) {
        String[] words = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; ++i) {
            if (words[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(words[i].charAt(0)));
            sb.append(words[i].substring(1));
            if (i >= words.length - 1) continue;
            sb.append(" ");
        }
        return sb.toString();
    }

    private static boolean isPreEnabled(String path) {
        return switch (path) {
            case "diamond", "emerald", "netherite_ingot", "ancient_debris", "elytra", "totem_of_undying", "trident", "enchanted_golden_apple", "golden_apple", "nether_star", "heart_of_the_sea", "end_crystal", "shulker_box" -> true;
            case "gold_ingot", "gold_block", "gold_nugget" -> true;
            case "iron_ingot" -> true;
            default -> false;
        };
    }

    private static float[] defaultColorFor(String path) {
        return switch (path) {
            case "diamond", "diamond_sword", "diamond_pickaxe", "diamond_axe", "diamond_shovel", "diamond_hoe", "diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots" -> ItemColorListSetting.rgba(0, 255, 255);
            case "emerald" -> ItemColorListSetting.rgba(0, 255, 68);
            case "netherite_ingot", "ancient_debris", "netherite_sword", "netherite_pickaxe", "netherite_axe", "netherite_shovel", "netherite_hoe", "netherite_helmet", "netherite_chestplate", "netherite_leggings", "netherite_boots" -> ItemColorListSetting.rgba(128, 96, 144);
            case "gold_ingot", "gold_block", "gold_nugget", "golden_sword", "golden_pickaxe", "golden_axe", "golden_shovel", "golden_hoe", "golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots", "golden_apple", "enchanted_golden_apple" -> ItemColorListSetting.rgba(255, 215, 0);
            case "iron_ingot" -> ItemColorListSetting.rgba(204, 204, 204);
            case "elytra" -> ItemColorListSetting.rgba(85, 85, 255);
            case "totem_of_undying" -> ItemColorListSetting.rgba(255, 170, 0);
            case "trident" -> ItemColorListSetting.rgba(0, 136, 255);
            case "nether_star" -> ItemColorListSetting.rgba(255, 255, 0);
            case "heart_of_the_sea" -> ItemColorListSetting.rgba(0, 255, 221);
            case "end_crystal" -> ItemColorListSetting.rgba(255, 0, 255);
            case "shulker_box" -> ItemColorListSetting.rgba(204, 68, 204);
            default -> ItemColorListSetting.rgba(255, 255, 255);
        };
    }

    private static float[] rgba(int r, int g, int b) {
        return new float[]{(float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f, 1.0f};
    }

    private static ItemEntry entry(String id, String display, boolean enabled, float[] color) {
        return new ItemEntry(id, display, new ImBoolean(enabled), color);
    }

    public static int rgbaToArgb(float[] rgba) {
        int r = Math.round(ItemColorListSetting.clamp01(rgba[0]) * 255.0f);
        int g = Math.round(ItemColorListSetting.clamp01(rgba[1]) * 255.0f);
        int b = Math.round(ItemColorListSetting.clamp01(rgba[2]) * 255.0f);
        int a = Math.round(ItemColorListSetting.clamp01(rgba[3]) * 255.0f);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static float clamp01(float v) {
        if (Float.isNaN(v) || Float.isInfinite(v)) {
            return 1.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    @Environment(value=EnvType.CLIENT)
    public record ItemEntry(String id, String displayName, ImBoolean enabled, float[] color) {
    }
}

