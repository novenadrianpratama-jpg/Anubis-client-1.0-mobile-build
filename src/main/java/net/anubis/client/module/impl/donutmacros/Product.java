/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1890
 *  net.minecraft.class_2960
 *  net.minecraft.class_6880
 *  net.minecraft.class_7923
 *  net.minecraft.class_9304
 */
package net.anubis.client.module.impl.donutmacros;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_2960;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9304;

@Environment(value=EnvType.CLIENT)
record Product(class_1792 item, String id, String search, String displayName, long minPrice, List<Enchant> enchants, boolean restock, int orderAmount, boolean beatTopOrder, long fixedPrice, long bidIncrement, long maxPrice, Predicate<class_1799> filter) {
    Product(class_1792 item, String id, String search, String displayName, long minPrice, List<Enchant> enchants, boolean restock, int orderAmount, boolean beatTopOrder, long fixedPrice, long bidIncrement, long maxPrice) {
        this(item, id, search, displayName, minPrice, enchants, restock, orderAmount, beatTopOrder, fixedPrice, bidIncrement, maxPrice, null);
    }

    static class_1792 resolveItem(String raw) {
        class_2960 id;
        Object text;
        Object object = text = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (((String)text).isEmpty()) {
            return null;
        }
        if (!((String)text).contains(":")) {
            text = "minecraft:" + (String)text;
        }
        if ((id = class_2960.method_12829((String)text)) == null || !class_7923.field_41178.method_10250(id)) {
            return null;
        }
        class_1792 item = (class_1792)class_7923.field_41178.method_63535(id);
        return item == class_1802.field_8162 ? null : item;
    }

    static String searchText(class_1792 item) {
        return class_7923.field_41178.method_10221((Object)item).method_12832().replace('_', ' ');
    }

    static List<Enchant> parseEnchants(String raw) {
        ArrayList<Enchant> out = new ArrayList<Enchant>();
        if (raw == null) {
            return out;
        }
        for (String part : raw.split("[,;]")) {
            String key;
            String last;
            int parsed;
            String text = part.trim();
            if (text.isEmpty()) continue;
            int level = 1;
            int space = text.lastIndexOf(32);
            if (space > 0 && (parsed = Product.parseLevel(last = text.substring(space + 1).trim())) > 0) {
                level = parsed;
                text = text.substring(0, space).trim();
            }
            if ((key = ServerText.key(text)).isEmpty()) continue;
            out.add(new Enchant(key, text, level));
        }
        return out;
    }

    private static int parseLevel(String raw) {
        try {
            int value = Integer.parseInt(raw);
            return value >= 1 && value <= 10 ? value : 0;
        }
        catch (NumberFormatException e) {
            return ServerText.roman(raw);
        }
    }

    boolean matches(class_1799 stack) {
        if (stack.method_7960() || !stack.method_31574(this.item)) {
            return false;
        }
        if (this.filter != null) {
            return this.filter.test(stack);
        }
        if (this.enchants.isEmpty()) {
            return true;
        }
        class_9304 present = class_1890.method_57532((class_1799)stack);
        for (Enchant wanted : this.enchants) {
            if (Product.levelOf(present, wanted.key()) >= wanted.level()) continue;
            return false;
        }
        return true;
    }

    static boolean enchantsMatch(class_1799 stack, List<Enchant> wanted, boolean exact) {
        class_9304 present = class_1890.method_57532((class_1799)stack);
        if (exact && present.method_57541() != wanted.size()) {
            return false;
        }
        for (Enchant enchant : wanted) {
            int level = Product.levelOf(present, enchant.key());
            if (!(exact ? level != enchant.level() : level < enchant.level())) continue;
            return false;
        }
        return true;
    }

    private static int levelOf(class_9304 present, String key) {
        for (Object2IntMap.Entry entry : present.method_57539()) {
            String path = ((class_6880)entry.getKey()).method_40230().map(k -> k.method_29177().method_12832()).orElse("");
            if (!ServerText.key(path).equals(key)) continue;
            return entry.getIntValue();
        }
        return 0;
    }

    boolean nameMatches(String label, boolean loose) {
        String key = ServerText.key(label);
        String byId = ServerText.key(class_7923.field_41178.method_10221((Object)this.item).method_12832());
        String byName = ServerText.key(this.displayName);
        if (key.equals(byId) || key.equals(byName)) {
            return true;
        }
        return loose && (key.endsWith(byId) || !byName.isEmpty() && key.endsWith(byName));
    }

    String describe() {
        StringBuilder text = new StringBuilder(this.displayName);
        if (!this.enchants.isEmpty()) {
            ArrayList<String> names = new ArrayList<String>();
            for (Enchant enchant : this.enchants) {
                names.add(enchant.label());
            }
            text.append(" (").append(String.join((CharSequence)", ", names)).append(')');
        }
        return text.toString();
    }

    @Environment(value=EnvType.CLIENT)
    record Enchant(String key, String name, int level) {
        String label() {
            return this.name + (String)(this.level > 1 ? " " + this.level : "");
        }
    }
}

