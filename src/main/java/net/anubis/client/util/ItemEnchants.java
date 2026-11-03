/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1887
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_5455
 *  net.minecraft.class_6880
 *  net.minecraft.class_7923
 *  net.minecraft.class_7924
 *  net.minecraft.class_9334
 */
package net.anubis.client.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5455;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class ItemEnchants {
    private static final String[] ROMAN = new String[]{"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};
    private static class_5455 cachedFor;
    private static List<class_6880<class_1887>> cached;

    private ItemEnchants() {
    }

    public static class_1792 resolve(String raw) {
        class_2960 id;
        Object text;
        Object object = text = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (((String)text).isEmpty()) {
            return null;
        }
        if (((String)text).indexOf(58) < 0) {
            text = "minecraft:" + (String)text;
        }
        if ((id = class_2960.method_12829((String)text)) == null || !class_7923.field_41178.method_10250(id)) {
            return null;
        }
        class_1792 item = (class_1792)class_7923.field_41178.method_63535(id);
        return item == class_1802.field_8162 ? null : item;
    }

    public static String stored(class_1792 item) {
        class_2960 key = class_7923.field_41178.method_10221((Object)item);
        return "minecraft".equals(key.method_12836()) ? key.method_12832() : key.toString();
    }

    public static boolean supports(class_1792 item) {
        if (item == null) {
            return false;
        }
        if (item == class_1802.field_8598) {
            return true;
        }
        List<class_6880<class_1887>> all = ItemEnchants.all();
        class_1799 stack = item.method_7854();
        if (all.isEmpty()) {
            return stack.method_57826(class_9334.field_53695);
        }
        for (class_6880<class_1887> holder : all) {
            if (!((class_1887)holder.comp_349()).method_60046(stack)) continue;
            return true;
        }
        return false;
    }

    public static List<class_6880<class_1887>> applicable(class_1792 item) {
        ArrayList<class_6880<class_1887>> out = new ArrayList<class_6880<class_1887>>();
        if (item == null) {
            return out;
        }
        class_1799 stack = item.method_7854();
        boolean book = item == class_1802.field_8598;
        for (class_6880<class_1887> holder2 : ItemEnchants.all()) {
            if (!book && !((class_1887)holder2.comp_349()).method_60046(stack)) continue;
            out.add(holder2);
        }
        out.sort(Comparator.comparing(holder -> ((class_1887)holder.comp_349()).comp_2686().getString(), String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    public static List<class_6880<class_1887>> all() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            return List.of();
        }
        class_5455 access = mc.field_1687.method_30349();
        if (access != cachedFor) {
            ArrayList found = new ArrayList();
            access.method_30530(class_7924.field_41265).method_42017().forEach(found::add);
            cached = List.copyOf(found);
            cachedFor = access;
        }
        return cached;
    }

    public static String path(class_6880<class_1887> holder) {
        return holder.method_40230().map(key -> key.method_29177().method_12832()).orElse("");
    }

    public static String key(String raw) {
        String text = raw == null ? "" : raw.toLowerCase(Locale.ROOT);
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) continue;
            out.append(c);
        }
        return out.toString();
    }

    public static Map<String, Integer> parse(String raw) {
        LinkedHashMap<String, Integer> out = new LinkedHashMap<String, Integer>();
        if (raw == null) {
            return out;
        }
        for (String part : raw.split("[,;]")) {
            String key;
            int parsed;
            String text = part.trim();
            if (text.isEmpty()) continue;
            int level = 1;
            int space = text.lastIndexOf(32);
            if (space > 0 && (parsed = ItemEnchants.level(text.substring(space + 1).trim())) > 0) {
                level = parsed;
                text = text.substring(0, space).trim();
            }
            if ((key = ItemEnchants.key(text)).isEmpty()) continue;
            out.put(key, level);
        }
        return out;
    }

    private static int level(String raw) {
        try {
            int value = Integer.parseInt(raw);
            return value >= 1 && value <= 10 ? value : 0;
        }
        catch (NumberFormatException e) {
            String text = raw.toLowerCase(Locale.ROOT);
            for (int i = 0; i < ROMAN.length; ++i) {
                if (!ROMAN[i].equals(text)) continue;
                return i + 1;
            }
            return 0;
        }
    }

    public static String roman(int level) {
        return level >= 1 && level <= ROMAN.length ? ROMAN[level - 1].toUpperCase(Locale.ROOT) : Integer.toString(level);
    }

    static {
        cached = List.of();
    }
}

