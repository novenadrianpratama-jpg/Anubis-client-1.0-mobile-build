/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1959
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2794
 *  net.minecraft.class_2893$class_2895
 *  net.minecraft.class_2975
 *  net.minecraft.class_3031
 *  net.minecraft.class_3124
 *  net.minecraft.class_3124$class_5876
 *  net.minecraft.class_3481
 *  net.minecraft.class_3532
 *  net.minecraft.class_3825
 *  net.minecraft.class_6796
 *  net.minecraft.class_6797
 *  net.minecraft.class_6862
 *  net.minecraft.class_6880
 *  net.minecraft.class_7510$class_6827
 *  net.minecraft.class_7923
 */
package net.anubis.client.util.seedscan;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.anubis.client.util.seedscan.VBlock;
import net.anubis.client.util.seedscan.WgRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2794;
import net.minecraft.class_2893;
import net.minecraft.class_2975;
import net.minecraft.class_3031;
import net.minecraft.class_3124;
import net.minecraft.class_3481;
import net.minecraft.class_3532;
import net.minecraft.class_3825;
import net.minecraft.class_6796;
import net.minecraft.class_6797;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7510;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public record OreSpec(String id, int step, int index, int order, int countMin, int countMax, int rarity, boolean trapezoid, int yMin, int yMax, int plateau, int size, float discard, byte kind, Set<class_6880<class_1959>> biomes) {
    private static final int[] STEPS = new int[]{class_2893.class_2895.field_13176.ordinal(), class_2893.class_2895.field_13177.ordinal()};

    public int reach() {
        return class_3532.method_15386((float)(((float)this.size / 16.0f * 2.0f + 1.0f) / 2.0f));
    }

    public int halfWidth() {
        return class_3532.method_15386((float)((float)this.size / 8.0f)) + this.reach();
    }

    public boolean drawsOnTerrain() {
        return this.discard > 0.0f && this.discard < 1.0f;
    }

    int sampleCount(WgRandom random) {
        if (this.rarity > 0) {
            return random.nextFloat() < 1.0f / (float)this.rarity ? 1 : 0;
        }
        return this.countMin == this.countMax ? this.countMin : random.between(this.countMin, this.countMax);
    }

    int sampleY(WgRandom random) {
        if (this.yMin > this.yMax) {
            return this.yMin;
        }
        if (!this.trapezoid) {
            return random.between(this.yMin, this.yMax);
        }
        int k = this.yMax - this.yMin;
        if (this.plateau >= k) {
            return random.between(this.yMin, this.yMax);
        }
        int l = (k - this.plateau) / 2;
        int m = k - l;
        return this.yMin + random.between(0, m) + random.between(0, l);
    }

    boolean inBiome(class_6880<class_1959> biome) {
        return this.biomes == null || this.biomes.contains(biome);
    }

    @Override
    public String toString() {
        return this.id + "(" + this.step + ":" + this.index + ")";
    }

    static List<OreSpec> derive(List<class_7510.class_6827> steps, List<class_6880<class_1959>> biomes, class_2794 generator, Function<class_6796, String> names, int minY, int maxY) {
        OreSpec.checkTags();
        ArrayList<OreSpec> specs = new ArrayList<OreSpec>();
        for (int step : STEPS) {
            if (step >= steps.size()) continue;
            List features = steps.get(step).comp_303();
            for (int index = 0; index < features.size(); ++index) {
                class_6796 placed = (class_6796)features.get(index);
                class_2975 configured = (class_2975)placed.comp_334().comp_349();
                if (configured.comp_332() != class_3031.field_13517) continue;
                class_3124 config = (class_3124)configured.comp_333();
                String id = names.apply(placed);
                Set carriers = Collections.newSetFromMap(new IdentityHashMap());
                for (class_6880<class_1959> biome : biomes) {
                    if (!generator.method_44216(biome).method_38105(placed)) continue;
                    carriers.add(biome);
                }
                specs.add(OreSpec.read(id, step, index, specs.size(), placed, config, carriers.size() == biomes.size() ? null : Set.copyOf(carriers), minY, maxY));
            }
        }
        return List.copyOf(specs);
    }

    private static OreSpec read(String id, int step, int index, int order, class_6796 placed, class_3124 config, Set<class_6880<class_1959>> biomes, int minY, int maxY) {
        int countMin;
        int countMax;
        List mods = placed.comp_335();
        if (mods.size() != 4) {
            throw OreSpec.unsupported(id, "placement " + mods.size() + " modifiers");
        }
        JsonObject first = OreSpec.json(id, (class_6797)mods.get(0));
        int rarity = 0;
        switch (OreSpec.type(first)) {
            case "minecraft:count": {
                JsonElement count = first.get("count");
                if (count.isJsonPrimitive()) {
                    countMin = countMax = count.getAsInt();
                    break;
                }
                Object provider = count.getAsJsonObject();
                if (!OreSpec.type((JsonObject)provider).equals("minecraft:uniform")) {
                    throw OreSpec.unsupported(id, "count " + String.valueOf(provider));
                }
                countMin = provider.get("min_inclusive").getAsInt();
                countMax = provider.get("max_inclusive").getAsInt();
                break;
            }
            case "minecraft:rarity_filter": {
                countMax = 1;
                countMin = 1;
                rarity = first.get("chance").getAsInt();
                break;
            }
            default: {
                throw OreSpec.unsupported(id, "first modifier " + String.valueOf(first));
            }
        }
        if (!OreSpec.type(OreSpec.json(id, (class_6797)mods.get(1))).equals("minecraft:in_square")) {
            throw OreSpec.unsupported(id, "second modifier");
        }
        JsonObject range = OreSpec.json(id, (class_6797)mods.get(2));
        if (!OreSpec.type(range).equals("minecraft:height_range")) {
            throw OreSpec.unsupported(id, "third modifier");
        }
        if (!OreSpec.type(OreSpec.json(id, (class_6797)mods.get(3))).equals("minecraft:biome")) {
            throw OreSpec.unsupported(id, "fourth modifier");
        }
        JsonObject height = range.getAsJsonObject("height");
        boolean trapezoid = switch (OreSpec.type(height)) {
            case "minecraft:uniform" -> false;
            case "minecraft:trapezoid" -> true;
            default -> throw OreSpec.unsupported(id, "height " + String.valueOf(height));
        };
        int plateau = height.has("plateau") ? height.get("plateau").getAsInt() : 0;
        int yMin = OreSpec.anchor(id, height.getAsJsonObject("min_inclusive"), minY, maxY);
        int yMax = OreSpec.anchor(id, height.getAsJsonObject("max_inclusive"), minY, maxY);
        return new OreSpec(id, step, index, order, countMin, countMax, rarity, trapezoid, yMin, yMax, plateau, config.field_13723, config.field_29064, OreSpec.kindOf(id, config), biomes);
    }

    private static int anchor(String id, JsonObject anchor, int minY, int maxY) {
        if (anchor.has("absolute")) {
            return anchor.get("absolute").getAsInt();
        }
        if (anchor.has("above_bottom")) {
            return minY + anchor.get("above_bottom").getAsInt();
        }
        if (anchor.has("below_top")) {
            return maxY - anchor.get("below_top").getAsInt();
        }
        throw OreSpec.unsupported(id, "anchor " + String.valueOf(anchor));
    }

    private static byte kindOf(String id, class_3124 config) {
        boolean covered;
        HashSet<String> tags = new HashSet<String>();
        byte kind = -1;
        for (class_3124.class_5876 target : config.field_29063) {
            JsonObject test = ((JsonElement)class_3825.field_25012.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)target.field_29068).getOrThrow()).getAsJsonObject();
            if (!test.get("predicate_type").getAsString().equals("minecraft:tag_match")) {
                throw OreSpec.unsupported(id, "target " + String.valueOf(test));
            }
            tags.add(test.get("tag").getAsString());
            byte written = VBlock.of(target.field_29069);
            if (written == 12 || written == 0 || kind != -1 && kind != written) {
                throw OreSpec.unsupported(id, "writes " + String.valueOf(target.field_29069));
            }
            kind = written;
        }
        boolean bl = covered = tags.equals(Set.of("minecraft:base_stone_overworld")) || tags.equals(Set.of("minecraft:stone_ore_replaceables", "minecraft:deepslate_ore_replaceables"));
        if (!covered || kind == -1) {
            throw OreSpec.unsupported(id, "targets " + String.valueOf(tags));
        }
        return kind;
    }

    static void checkTags() {
        OreSpec.expect((class_6862<class_2248>)class_3481.field_25806, class_2246.field_10340, class_2246.field_10474, class_2246.field_10508, class_2246.field_10115, class_2246.field_27165, class_2246.field_28888);
        OreSpec.expect((class_6862<class_2248>)class_3481.field_28992, class_2246.field_10340, class_2246.field_10474, class_2246.field_10508, class_2246.field_10115);
        OreSpec.expect((class_6862<class_2248>)class_3481.field_28993, class_2246.field_28888, class_2246.field_27165);
    }

    private static void expect(class_6862<class_2248> tag, class_2248 ... blocks) {
        Set want = Collections.newSetFromMap(new IdentityHashMap());
        Collections.addAll(want, blocks);
        Set have = Collections.newSetFromMap(new IdentityHashMap());
        for (class_6880 holder : class_7923.field_41175.method_40286(tag)) {
            have.add((class_2248)holder.comp_349());
        }
        if (!have.equals(want)) {
            throw new IllegalStateException("block tag " + String.valueOf(tag) + " is not vanilla's: " + String.valueOf(have));
        }
    }

    private static JsonObject json(String id, class_6797 modifier) {
        try {
            return ((JsonElement)class_6797.field_35736.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)modifier).getOrThrow()).getAsJsonObject();
        }
        catch (RuntimeException e) {
            throw OreSpec.unsupported(id, "modifier " + String.valueOf(modifier) + ": " + e.getMessage());
        }
    }

    private static String type(JsonObject object) {
        JsonElement type = object.get("type");
        return type == null ? "" : type.getAsString();
    }

    private static IllegalStateException unsupported(String id, String what) {
        return new IllegalStateException("feature " + id + " is not placed like a vanilla ore: " + what);
    }
}

