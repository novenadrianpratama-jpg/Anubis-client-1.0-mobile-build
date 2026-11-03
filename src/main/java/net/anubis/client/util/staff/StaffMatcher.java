/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.staff;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class StaffMatcher {
    public static final String DEFAULT_RANK_WORDS = "owner, co-owner, manager, admin, administrator, developer, dev, sr-admin, sr-mod, senior-mod, moderator, mod, trial-mod, sr-helper, senior-helper, helper, trial-helper, support, staff";
    public static final String DEFAULT_MARKERS = "U+2605 U+2606 U+2726 U+2727 U+272A U+272F U+2730 U+2B50";
    public static final String MODE_ALL = "Ranks + Markers + Team";
    public static final String MODE_RANKS = "Ranks";
    public static final String MODE_MARKERS = "Markers";
    public static final String MODE_TEAM = "Team Rank";
    public static final int DEFAULT_TEAM_RANK_MAX = 5;
    private static final String GENERIC_RANK = "Staff";
    private static final Pattern CODEPOINT = Pattern.compile("(?i)(?:u\\+|\\\\u)([0-9a-f]{4,6})");
    private static final Pattern TEAM_RANK = Pattern.compile("^(\\d{2})-");
    private static final int MAX_SPAN = 3;
    private static final int TEAM_RANK_SLOTS = 100;
    private static final Map<String, String> LABELS = Map.ofEntries(Map.entry("owner", "Owner"), Map.entry("coowner", "Co-Owner"), Map.entry("manager", "Manager"), Map.entry("admin", "Admin"), Map.entry("administrator", "Admin"), Map.entry("sradmin", "Sr. Admin"), Map.entry("developer", "Dev"), Map.entry("dev", "Dev"), Map.entry("srmod", "Sr. Mod"), Map.entry("seniormod", "Sr. Mod"), Map.entry("moderator", "Mod"), Map.entry("mod", "Mod"), Map.entry("trialmod", "Trial Mod"), Map.entry("srhelper", "Sr. Helper"), Map.entry("seniorhelper", "Sr. Helper"), Map.entry("helper", "Helper"), Map.entry("trialhelper", "Trial Helper"), Map.entry("support", "Support"), Map.entry("staff", "Staff"), Map.entry("builder", "Builder"));
    private static final Map<Integer, Character> SMALL_CAPS = StaffMatcher.smallCaps();
    private static final char SMALL_CAPS_Q = '\u01eb';
    private final boolean useRanks;
    private final boolean useMarkers;
    private final boolean useTeamRank;
    private final int teamRankMax;
    private final Map<String, Integer> wordIndex = new HashMap<String, Integer>();
    private final List<Word> words = new ArrayList<Word>();
    private final Set<Integer> markers;
    private final boolean iconGlyphs;
    private final Set<String> knownNames;
    private final Set<String> ignoredNames;

    public StaffMatcher(String mode, String rankWords, String markerGlyphs, boolean iconGlyphs, int teamRankMax, String knownNames, String ignoredNames) {
        this.useRanks = MODE_ALL.equals(mode) || MODE_RANKS.equals(mode);
        this.useMarkers = MODE_ALL.equals(mode) || MODE_MARKERS.equals(mode);
        this.useTeamRank = MODE_ALL.equals(mode) || MODE_TEAM.equals(mode);
        this.teamRankMax = teamRankMax;
        this.iconGlyphs = iconGlyphs;
        for (String raw : rankWords.split(",")) {
            String typed = raw.trim();
            String folded = StaffMatcher.fold(typed).replace(" ", "");
            if (folded.isEmpty() || this.wordIndex.containsKey(folded)) continue;
            this.wordIndex.put(folded, this.words.size());
            this.words.add(new Word(folded, LABELS.getOrDefault(folded, StaffMatcher.capitalize(typed))));
        }
        this.markers = StaffMatcher.parseMarkers(markerGlyphs);
        this.knownNames = StaffMatcher.parseNames(knownNames);
        this.ignoredNames = StaffMatcher.parseNames(ignoredNames);
    }

    public Match match(String name, String teamName, String ... tabTexts) {
        int teamRank;
        Word rank;
        if (name == null || name.isEmpty()) {
            return null;
        }
        String key = name.toLowerCase(Locale.ROOT);
        if (this.ignoredNames.contains(key)) {
            return null;
        }
        StringBuilder combined = new StringBuilder();
        for (String text : tabTexts) {
            if (text == null || text.isEmpty()) continue;
            combined.append(text).append(' ');
        }
        if (teamName != null && !teamName.isEmpty()) {
            combined.append(teamName).append(' ');
        }
        String around = StaffMatcher.withoutName(StaffMatcher.stripLegacyCodes(combined.toString()), name);
        Word word = rank = this.useRanks ? this.bestRank(around) : null;
        if (rank != null) {
            return new Match(rank.label(), this.wordIndex.get(rank.folded()));
        }
        int n = teamRank = this.useTeamRank ? StaffMatcher.teamRank(teamName) : -1;
        if (teamRank >= 0 && teamRank <= this.teamRankMax) {
            return new Match(StaffMatcher.teamRankLabel(teamRank), this.words.size() + teamRank);
        }
        if (this.useMarkers && this.hasMarker(around) || this.knownNames.contains(key)) {
            return new Match(GENERIC_RANK, this.words.size() + 100);
        }
        return null;
    }

    public static int teamRank(String teamName) {
        if (teamName == null || teamName.length() < 3) {
            return -1;
        }
        Matcher m = TEAM_RANK.matcher(teamName);
        return m.lookingAt() ? Integer.parseInt(m.group(1)) : -1;
    }

    public static String teamRankLabel(int teamRank) {
        return teamRank < 10 ? "0" + teamRank : Integer.toString(teamRank);
    }

    private Word bestRank(String text) {
        int matchedSpan;
        if (this.words.isEmpty()) {
            return null;
        }
        List<String> tokens = StaffMatcher.tokens(text);
        int best = Integer.MAX_VALUE;
        block0: for (int i = 0; i < tokens.size(); i += Math.max(1, matchedSpan)) {
            matchedSpan = 0;
            for (int span = Math.min(3, tokens.size() - i); span >= 1; --span) {
                Integer index = this.wordIndex.get(String.join((CharSequence)"", tokens.subList(i, i + span)));
                if (index == null) continue;
                best = Math.min(best, index);
                matchedSpan = span;
                continue block0;
            }
        }
        return best == Integer.MAX_VALUE ? null : this.words.get(best);
    }

    static List<String> tokens(String text) {
        String folded = StaffMatcher.fold(text);
        ArrayList<String> out = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < folded.length(); ++i) {
            char c = folded.charAt(i);
            if (c >= 'a' && c <= 'z') {
                current.append(c);
                continue;
            }
            if (current.isEmpty()) continue;
            out.add(current.toString());
            current.setLength(0);
        }
        if (!current.isEmpty()) {
            out.add(current.toString());
        }
        return out;
    }

    static String stripLegacyCodes(String text) {
        if (text.indexOf(167) < 0) {
            return text;
        }
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == '\u00a7') {
                ++i;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }

    static String fold(String text) {
        int cp;
        String decomposed = Normalizer.normalize(text.replace('\u01eb', 'q'), Normalizer.Form.NFKD);
        StringBuilder out = new StringBuilder(decomposed.length());
        for (int i = 0; i < decomposed.length(); i += Character.charCount(cp)) {
            cp = decomposed.codePointAt(i);
            if (Character.getType(cp) == 6) continue;
            Character small = SMALL_CAPS.get(cp);
            if (small != null) {
                out.append(small.charValue());
                continue;
            }
            int lower = Character.toLowerCase(cp);
            out.append(lower >= 97 && lower <= 122 ? (char)lower : (char)' ');
        }
        return out.toString();
    }

    private boolean hasMarker(String text) {
        int cp;
        for (int i = 0; i < text.length(); i += Character.charCount(cp)) {
            cp = text.codePointAt(i);
            if (!this.markers.contains(cp) && (!this.iconGlyphs || !StaffMatcher.isPrivateUse(cp))) continue;
            return true;
        }
        return false;
    }

    public static boolean isPrivateUse(int cp) {
        return cp >= 57344 && cp <= 63743 || cp >= 983040 && cp <= 1048573 || cp >= 0x100000 && cp <= 1114109;
    }

    static Set<Integer> parseMarkers(String raw) {
        HashSet<Integer> out = new HashSet<Integer>();
        for (String token : raw.split("[,\\s]+")) {
            if (token.isEmpty()) continue;
            Matcher m = CODEPOINT.matcher(token);
            if (m.matches()) {
                int cp = Integer.parseInt(m.group(1), 16);
                if (!Character.isValidCodePoint(cp)) continue;
                out.add(cp);
                continue;
            }
            token.codePoints().forEach(out::add);
        }
        return out;
    }

    private static Set<String> parseNames(String raw) {
        HashSet<String> out = new HashSet<String>();
        for (String part : raw.split("[,\\s]+")) {
            if (part.isEmpty()) continue;
            out.add(part.toLowerCase(Locale.ROOT));
        }
        return out;
    }

    private static String withoutName(String text, String name) {
        String lowerName;
        String lowerText = text.toLowerCase(Locale.ROOT);
        if (!lowerText.contains(lowerName = name.toLowerCase(Locale.ROOT))) {
            return text;
        }
        StringBuilder out = new StringBuilder(text.length());
        int from = 0;
        int at = lowerText.indexOf(lowerName);
        while (at >= 0) {
            out.append(text, from, at).append(' ');
            from = at + lowerName.length();
            at = lowerText.indexOf(lowerName, from);
        }
        return out.append(text, from, text.length()).toString();
    }

    private static String capitalize(String word) {
        String trimmed = word.trim();
        if (trimmed.isEmpty()) {
            return GENERIC_RANK;
        }
        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT) + trimmed.substring(1);
    }

    private static Map<Integer, Character> smallCaps() {
        int[] codepoints = new int[]{7424, 665, 7428, 7429, 7431, 42800, 610, 668, 618, 7434, 7435, 671, 7437, 628, 7439, 7448, 42927, 640, 42801, 7451, 7452, 7456, 7457, 655, 7458};
        String letters = "abcdefghijklmnopqrstuvwyz";
        HashMap<Integer, Character> map = new HashMap<Integer, Character>();
        for (int i = 0; i < codepoints.length; ++i) {
            map.put(codepoints[i], Character.valueOf(letters.charAt(i)));
        }
        return Map.copyOf(map);
    }

    @Environment(value=EnvType.CLIENT)
    private record Word(String folded, String label) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Match(String rank, int seniority) {
    }
}

