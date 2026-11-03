/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.spotify;

import java.text.Normalizer;
import java.util.function.IntPredicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class DisplayText {
    private DisplayText() {
    }

    public static String fold(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder out = null;
        boolean space = false;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            String replacement = DisplayText.replacement(c);
            if (!(replacement != null || c == ' ' && space)) {
                if (out != null) {
                    out.append(c);
                }
                space = c == ' ';
                continue;
            }
            if (out == null) {
                out = new StringBuilder(text.length()).append(text, 0, i);
            }
            if (replacement == null) continue;
            for (int k = 0; k < replacement.length(); ++k) {
                char r = replacement.charAt(k);
                if (r == ' ' && (space || out.isEmpty())) continue;
                out.append(r);
                space = r == ' ';
            }
        }
        if (out == null) {
            String stripped = text.strip();
            return stripped.length() == text.length() ? text : stripped;
        }
        return out.toString().strip();
    }

    public static String fold(String text, IntPredicate drawable) {
        String folded = DisplayText.fold(text);
        StringBuilder out = null;
        int i = 0;
        while (i < folded.length()) {
            String base;
            int cp = folded.codePointAt(i);
            int next = i + Character.charCount(cp);
            String string = base = cp < 128 || !Character.isLetter(cp) || drawable.test(cp) ? null : DisplayText.baseLetters(cp);
            if (base != null && !base.codePoints().allMatch(b -> b < 128 || drawable.test(b))) {
                base = null;
            }
            if (base != null && out == null) {
                out = new StringBuilder(folded.length()).append(folded, 0, i);
            }
            if (out != null) {
                if (base != null) {
                    out.append(base);
                } else {
                    out.append(folded, i, next);
                }
            }
            i = next;
        }
        return out == null ? folded : out.toString();
    }

    static String baseLetters(int cp) {
        String special;
        switch (cp) {
            case 321: {
                String string = "L";
                break;
            }
            case 322: {
                String string = "l";
                break;
            }
            case 272: {
                String string = "D";
                break;
            }
            case 273: {
                String string = "d";
                break;
            }
            case 294: {
                String string = "H";
                break;
            }
            case 295: {
                String string = "h";
                break;
            }
            case 358: {
                String string = "T";
                break;
            }
            case 359: {
                String string = "t";
                break;
            }
            case 319: {
                String string = "L";
                break;
            }
            case 320: {
                String string = "l";
                break;
            }
            case 338: {
                String string = "OE";
                break;
            }
            case 339: {
                String string = "oe";
                break;
            }
            case 306: {
                String string = "IJ";
                break;
            }
            case 307: {
                String string = "ij";
                break;
            }
            case 383: {
                String string = "s";
                break;
            }
            default: {
                String string = special = null;
            }
        }
        if (special != null) {
            return special;
        }
        String decomposed = Normalizer.normalize(new String(Character.toChars(cp)), Normalizer.Form.NFD);
        StringBuilder base = new StringBuilder(decomposed.length());
        decomposed.codePoints().filter(c -> Character.getType(c) != 6).forEach(base::appendCodePoint);
        if (base.isEmpty() || base.length() == decomposed.length()) {
            return null;
        }
        return base.toString();
    }

    private static String replacement(char c) {
        return switch (c) {
            case '\u2018', '\u2019', '\u201a', '\u201b', '\u2032' -> "'";
            case '\u201c', '\u201d', '\u201e', '\u201f', '\u2033' -> "\"";
            case '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2015', '\u2212' -> "-";
            case '\u2026' -> "...";
            case '\u2022', '\u2027', '\u2219' -> "\u00b7";
            case '\t', '\u00a0', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008', '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' -> " ";
            case '\u200b', '\u200c', '\u200d', '\u2060', '\u2669', '\u266a', '\u266b', '\u266c', '\ufeff' -> "";
            default -> null;
        };
    }
}

