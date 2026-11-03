/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.misc.nameprotect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class NameMatcher {
    public static final NameMatcher EMPTY = new NameMatcher(Map.of());
    public static final int MAX_NAME_LENGTH = 32;
    private final Map<String, String> mapping;
    private final String[] names;
    private final String[] aliases;
    private final int mask;
    private final long lengths;

    public NameMatcher(Map<String, String> nameToAlias) {
        HashMap<String, String> unique = new HashMap<String, String>();
        ArrayList<String> keptNames = new ArrayList<String>(nameToAlias.size());
        ArrayList<String> keptAliases = new ArrayList<String>(nameToAlias.size());
        for (Map.Entry<String, String> entry : nameToAlias.entrySet()) {
            String name = entry.getKey();
            String alias = entry.getValue();
            if (name == null || name.isEmpty() || name.length() > 32 || alias == null || unique.putIfAbsent(name.toLowerCase(Locale.ROOT), alias) != null) continue;
            keptNames.add(name);
            keptAliases.add(alias);
        }
        int capacity = Integer.highestOneBit(Math.max(1, keptNames.size()) * 2) * 2;
        String[] tableNames = new String[capacity];
        String[] tableAliases = new String[capacity];
        long lengthBits = 0L;
        for (int i = 0; i < keptNames.size(); ++i) {
            String name = (String)keptNames.get(i);
            int slot = NameMatcher.hash(name, 0, name.length()) & capacity - 1;
            while (tableNames[slot] != null) {
                slot = slot + 1 & capacity - 1;
            }
            tableNames[slot] = name;
            tableAliases[slot] = (String)keptAliases.get(i);
            lengthBits |= 1L << name.length();
        }
        this.mapping = Map.copyOf(unique);
        this.names = tableNames;
        this.aliases = tableAliases;
        this.mask = capacity - 1;
        this.lengths = lengthBits;
    }

    public boolean isEmpty() {
        return this.mapping.isEmpty();
    }

    public boolean sameMapping(NameMatcher other) {
        return other != null && this.mapping.equals(other.mapping);
    }

    public static boolean isNameChar(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_';
    }

    public static String nameCore(String raw) {
        int start;
        if (raw == null) {
            return null;
        }
        int end = raw.length();
        for (start = 0; start < end && !NameMatcher.isNameChar(raw.charAt(start)); ++start) {
        }
        while (end > start && !NameMatcher.isNameChar(raw.charAt(end - 1))) {
            --end;
        }
        if (start == end || end - start > 32) {
            return null;
        }
        for (int i = start; i < end; ++i) {
            if (NameMatcher.isNameChar(raw.charAt(i))) continue;
            return null;
        }
        return raw.substring(start, end);
    }

    public String aliasAt(String text, int start, int end) {
        int length = end - start;
        if (length <= 0 || length > 32 || (this.lengths & 1L << length) == 0L) {
            return null;
        }
        int slot = NameMatcher.hash(text, start, end) & this.mask;
        String name;
        while ((name = this.names[slot]) != null) {
            if (name.length() == length && name.regionMatches(true, 0, text, start, length)) {
                return this.aliases[slot];
            }
            slot = slot + 1 & this.mask;
        }
        return null;
    }

    public String replace(String text) {
        if (text == null || text.isEmpty() || this.isEmpty()) {
            return text;
        }
        List<Match> matches = this.scan(text, new int[]{0, text.length()}, 1);
        if (matches.isEmpty()) {
            return text;
        }
        StringBuilder out = new StringBuilder(text.length() + 16);
        int position = 0;
        for (Match match : matches) {
            out.append(text, position, match.start()).append(match.alias());
            position = match.end();
        }
        return out.append(text, position, text.length()).toString();
    }

    public boolean mentionsAny(String text) {
        return text != null && !text.isEmpty() && !this.isEmpty() && !this.scan(text, new int[]{0, text.length()}, 1).isEmpty();
    }

    public NameMatcher newNamesSince(NameMatcher older) {
        HashMap<String, String> fresh = null;
        for (Map.Entry<String, String> entry : this.mapping.entrySet()) {
            if (older != null && entry.getValue().equals(older.mapping.get(entry.getKey()))) continue;
            if (fresh == null) {
                fresh = new HashMap<String, String>();
            }
            fresh.put(entry.getKey(), entry.getValue());
        }
        return fresh != null ? new NameMatcher(fresh) : EMPTY;
    }

    public List<Match> scan(String text, int[] segmentStarts, int segmentCount) {
        if (this.isEmpty() || text.isEmpty()) {
            return List.of();
        }
        List<Match> found = null;
        int length = text.length();
        int segment = 0;
        int i = 0;
        while (i < length) {
            int wordEnd;
            while (segment < segmentCount - 1 && segmentStarts[segment + 1] <= i) {
                ++segment;
            }
            int segmentEnd = segmentStarts[segment + 1];
            char c = text.charAt(i);
            if (c == '\u00a7') {
                i += i + 1 < segmentEnd ? 2 : 1;
                continue;
            }
            if (!NameMatcher.isNameChar(c)) {
                ++i;
                continue;
            }
            for (wordEnd = i + 1; wordEnd < length && NameMatcher.isNameChar(text.charAt(wordEnd)); ++wordEnd) {
            }
            String alias = this.aliasAt(text, i, wordEnd);
            if (alias != null) {
                int from;
                found = NameMatcher.add(found, from, wordEnd, (String)((from = NameMatcher.bedrockPrefix(text, i)) == i ? alias : text.substring(from, i).replace(".", "") + alias));
            } else if (segmentEnd < wordEnd) {
                for (int part = segment + 1; part < segmentCount && segmentStarts[part] < wordEnd; ++part) {
                    int from = segmentStarts[part];
                    String endingAlias = this.aliasAt(text, from, wordEnd);
                    if (endingAlias == null) continue;
                    found = NameMatcher.add(found, from, wordEnd, endingAlias);
                    break;
                }
            }
            i = wordEnd;
        }
        return found != null ? found : List.of();
    }

    private static int bedrockPrefix(String text, int start) {
        int at = start;
        int from = start;
        while (true) {
            if (at >= 2 && text.charAt(at - 2) == '\u00a7') {
                at -= 2;
                continue;
            }
            if (at < 1 || text.charAt(at - 1) != '.') break;
            from = --at;
        }
        if (from == start) {
            return start;
        }
        boolean glued = from >= 1 && NameMatcher.isNameChar(text.charAt(from - 1)) && (from < 2 || text.charAt(from - 2) != '\u00a7');
        return glued ? start : from;
    }

    private static List<Match> add(List<Match> found, int start, int end, String alias) {
        ArrayList<Match> list = found != null ? found : new ArrayList<Match>(4);
        list.add(new Match(start, end, alias));
        return list;
    }

    private static int hash(String text, int start, int end) {
        int h = 0;
        for (int i = start; i < end; ++i) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c = (char)(c + 32);
            }
            h = 31 * h + c;
        }
        return h ^ h >>> 16;
    }

    @Environment(value=EnvType.CLIENT)
    public record Match(int start, int end, String alias) {
    }
}

