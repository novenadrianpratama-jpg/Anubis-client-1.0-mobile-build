/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.playerbypass;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record RtpReply(Kind kind, long millis, long nextUseMs) {
    public static final RtpReply NONE = new RtpReply(Kind.NONE, 0L);
    private static final Pattern FORMATTING = Pattern.compile("\u00a7.");
    private static final Pattern DURATION = Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*(hours?|hrs?|saat|h|minutes?|mins?|dakika|dk|m|seconds?|secs?|saniye|sn|s)(?![a-z])");
    private static final Pattern CLOCK = Pattern.compile("(?<![\\d:])(\\d{1,2}):(\\d{2})(?![\\d:])");
    private static final Pattern BRACKET_TIME = Pattern.compile("[\\[(<]\\s*\\d{1,2}:\\d{2}(?::\\d{2})?\\s*(?:[ap]m)?\\s*[\\])>]");
    private static final Pattern LEADING_TIME = Pattern.compile("^\\d{1,2}:\\d{2}(?::\\d{2})?(?:\\s?[ap]m)?(?![\\d:])(?!\\s*(?:left|remaining|kaldi|kalan))");
    private static final Pattern SENTENCE = Pattern.compile("\\.(?!\\d)|[!?|;]");
    private static final String SMALL_CAPS = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\u0455\ua731\u1d1b\u1d1c\u1d20\u1d21\u028f\u1d22";
    private static final String SMALL_CAPS_PLAIN = "abcdefghijklmnopqrsstuvwyz";
    private static final String TURKISH = "\u0131\u0130\u015f\u015e\u011f\u011e\u00fc\u00dc\u00f6\u00d6\u00e7\u00c7";
    private static final String TURKISH_PLAIN = "iissgguuoocc";
    private static final String CHAT_SEPARATORS = ":>\u00bb\u203a\u226b\u27a4\u279c\u2192\u21d2|";
    private static final String[] UNKNOWN = new String[]{"unknown command", "unknown or incomplete command", "bilinmeyen komut", "komut bulunamadi", "boyle bir komut yok"};
    private static final String[] NO_PERMISSION = new String[]{"no permission", "don't have permission", "dont have permission", "do not have permission", "not have permission", "insufficient permission", "not allowed to use", "yetkin yok", "yetkiniz yok", "iznin yok", "izniniz yok", "yetkiniz bulunmuyor", "izniniz bulunmuyor"};
    private static final String[] COMBAT = new String[]{"combat", "savasta", "savas modunda", "pvp modunda", "pvp'de", "pvpde", "kombat"};
    private static final String[] COMBAT_STRONG = new String[]{"in combat", "combat tag", "combat-tag", "combattag", "savasta", "savas modunda", "pvp modunda", "pvp'de", "pvpde"};
    private static final String[] COMBAT_OVER = new String[]{"no longer in combat", "out of combat", "left combat", "not in combat", "savastan ciktin", "artik savasta degil"};
    private static final String[] COOLDOWN_STRONG = new String[]{"cooldown", "cool down", "again", "another", "next teleport", "next rtp", "next use", "tekrar", "yeniden", "bekleme suresi"};
    private static final String[] COOLDOWN_WEAK = new String[]{"wait", "bekle", "before", "until", "remaining", "kalan", "sonra"};
    private static final String[] WARMUP = new String[]{"teleporting in", "teleporting you in", "teleport in", "teleported in", "teleportation in", "teleportation will", "teleport will", "will be teleported", "will teleport", "before teleporting", "before you teleport", "before being teleported", "don't move", "dont move", "do not move", "stand still", "isinlaniyor", "isinlanacak", "isinlanmaya", "hareket etme"};
    private static final String[] PROGRESS = new String[]{"teleporting", "searching", "looking for a", "finding a", "araniyor", "konum bul"};
    private static final String[] CANCEL = new String[]{"cancel", "iptal", "you moved", "hareket ettin", "aborted"};

    public RtpReply(Kind kind, long millis) {
        this(kind, millis, -1L);
    }

    public static RtpReply parse(String raw) {
        String text = RtpReply.normalize(raw);
        if (text.isEmpty()) {
            return NONE;
        }
        if (RtpReply.containsAny(text, UNKNOWN)) {
            return new RtpReply(Kind.UNKNOWN_COMMAND, 0L);
        }
        if (RtpReply.containsAny(text, NO_PERMISSION)) {
            return new RtpReply(Kind.NO_PERMISSION, 0L);
        }
        text = LEADING_TIME.matcher(BRACKET_TIME.matcher(text).replaceAll(" ").trim()).replaceFirst("").trim();
        String[] sentences = SENTENCE.split(text);
        long any = -1L;
        long strong = -1L;
        long warm = -1L;
        long weak = -1L;
        for (String sentence : sentences) {
            long ms = RtpReply.durationMs(sentence);
            if (ms < 0L) continue;
            if (any < 0L) {
                any = ms;
            }
            if (RtpReply.containsAny(sentence, COOLDOWN_STRONG)) {
                if (strong >= 0L) continue;
                strong = ms;
                continue;
            }
            if (RtpReply.containsAny(sentence, WARMUP)) {
                if (warm >= 0L) continue;
                warm = ms;
                continue;
            }
            if (!RtpReply.containsAny(sentence, COOLDOWN_WEAK) || weak >= 0L) continue;
            weak = ms;
        }
        boolean warmupWords = RtpReply.containsAny(text, WARMUP);
        if (RtpReply.containsAny(text, COMBAT) && !RtpReply.containsAny(text, COMBAT_OVER) && (!warmupWords || warm < 0L && RtpReply.containsAny(text, COMBAT_STRONG))) {
            return new RtpReply(Kind.COMBAT, 0L);
        }
        boolean cancel = RtpReply.containsAny(text, CANCEL);
        if (any < 0L) {
            if (cancel) {
                return new RtpReply(Kind.CANCELLED, 0L);
            }
            if (warmupWords || RtpReply.containsAny(text, PROGRESS)) {
                return new RtpReply(Kind.WARMUP, 0L);
            }
            return NONE;
        }
        if (warm >= 0L && strong >= 0L) {
            return new RtpReply(Kind.WARMUP, warm, strong);
        }
        if (RtpReply.containsAny(text, COOLDOWN_STRONG)) {
            return new RtpReply(Kind.COOLDOWN, strong >= 0L ? strong : any);
        }
        if (warmupWords) {
            return new RtpReply(Kind.WARMUP, warm >= 0L ? warm : any);
        }
        if (cancel) {
            return new RtpReply(Kind.CANCELLED, 0L);
        }
        if (RtpReply.containsAny(text, COOLDOWN_WEAK)) {
            return new RtpReply(Kind.COOLDOWN, weak >= 0L ? weak : any);
        }
        return NONE;
    }

    public static long durationMs(String normalized) {
        Matcher matcher = DURATION.matcher(normalized);
        double total = 0.0;
        boolean found = false;
        while (matcher.find()) {
            double value;
            try {
                value = Double.parseDouble(matcher.group(1).replace(',', '.'));
            }
            catch (NumberFormatException e) {
                continue;
            }
            total += value * (double)RtpReply.unitMs(matcher.group(2));
            found = true;
        }
        if (found) {
            return Math.round(total);
        }
        Matcher clock = CLOCK.matcher(normalized);
        if (clock.find()) {
            return ((long)Integer.parseInt(clock.group(1)) * 60L + (long)Integer.parseInt(clock.group(2))) * 1000L;
        }
        return -1L;
    }

    private static long unitMs(String unit) {
        return switch (unit) {
            case "h", "hr", "hrs", "hour", "hours", "saat" -> 3600000L;
            case "m", "min", "mins", "minute", "minutes", "dakika", "dk" -> 60000L;
            default -> 1000L;
        };
    }

    public static boolean looksLikePlayerChat(String raw, Iterable<String> names) {
        String text = RtpReply.normalize(raw);
        if (text.isEmpty()) {
            return false;
        }
        for (String name : names) {
            if (name == null || name.isBlank()) continue;
            String needle = name.toLowerCase(Locale.ROOT);
            int at = text.indexOf(needle);
            while (at >= 0) {
                int end = at + needle.length();
                if (!(at > 0 && RtpReply.nameChar(text.charAt(at - 1)) || end < text.length() && RtpReply.nameChar(text.charAt(end)) || !RtpReply.separatorAfter(text, end))) {
                    return true;
                }
                at = text.indexOf(needle, at + 1);
            }
        }
        return false;
    }

    private static boolean nameChar(char c) {
        return c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_';
    }

    private static boolean separatorAfter(String text, int from) {
        int i;
        for (i = from; i < text.length() && i - from < 3 && " ])}".indexOf(text.charAt(i)) >= 0; ++i) {
        }
        if (i >= text.length()) {
            return false;
        }
        char c = text.charAt(i);
        if (CHAT_SEPARATORS.indexOf(c) >= 0) {
            return true;
        }
        return (c == '-' || c == '=') && i + 1 < text.length() && text.charAt(i + 1) == '>';
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String text = FORMATTING.matcher(raw).replaceAll("");
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            int small = SMALL_CAPS.indexOf(c);
            int turkish = TURKISH.indexOf(c);
            if (small >= 0) {
                out.append(SMALL_CAPS_PLAIN.charAt(small));
                continue;
            }
            if (turkish >= 0) {
                out.append(TURKISH_PLAIN.charAt(turkish));
                continue;
            }
            if (c == '\u2019' || c == '\u2018') {
                out.append('\'');
                continue;
            }
            if (c == '\u0307') continue;
            out.append(c);
        }
        return out.toString().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static boolean containsAny(String text, String[] words) {
        for (String word : words) {
            if (!text.contains(word)) continue;
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Kind {
        NONE,
        COOLDOWN,
        WARMUP,
        CANCELLED,
        UNKNOWN_COMMAND,
        NO_PERMISSION,
        COMBAT;

    }
}

