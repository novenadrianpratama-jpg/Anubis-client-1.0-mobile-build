/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10611
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_2568$class_5248
 *  net.minecraft.class_2583
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 *  net.minecraft.class_5348$class_5246
 */
package net.anubis.client.module.impl.misc.nameprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import net.anubis.client.module.impl.misc.nameprotect.NameMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5348;

@Environment(value=EnvType.CLIENT)
public final class ProtectedText {
    private ProtectedText() {
    }

    public static class_2561 rewrite(NameMatcher matcher, class_2561 text) {
        if (text == null || matcher.isEmpty()) {
            return text;
        }
        class_2561 rebuilt = ProtectedText.rebuild(matcher, (class_5348)text, true);
        return rebuilt != null ? rebuilt : text;
    }

    public static class_5348 rewrite(NameMatcher matcher, class_5348 text) {
        if (text == null || matcher.isEmpty()) {
            return text;
        }
        class_2561 rebuilt = ProtectedText.rebuild(matcher, text, true);
        return rebuilt != null ? rebuilt : text;
    }

    private static class_2561 rebuild(NameMatcher matcher, class_5348 text, boolean withHovers) {
        class_2583[] hoverStyles;
        Pieces pieces = new Pieces();
        text.method_27658((class_5348.class_5246)pieces, class_2583.field_24360);
        if (pieces.count == 0) {
            return null;
        }
        pieces.seal();
        String joined = pieces.text.toString();
        List<NameMatcher.Match> matches = matcher.scan(joined, pieces.starts, pieces.count);
        class_2583[] class_2583Array = hoverStyles = withHovers ? ProtectedText.rewriteHovers(matcher, pieces) : null;
        if (matches.isEmpty() && hoverStyles == null) {
            return null;
        }
        class_2583[] styles = hoverStyles != null ? hoverStyles : pieces.styles;
        Runs runs = new Runs(pieces);
        int position = 0;
        for (NameMatcher.Match match : matches) {
            runs.copy(joined, position, match.start());
            runs.spread(match.alias(), match.start(), match.end());
            position = match.end();
        }
        runs.copy(joined, position, joined.length());
        return runs.build(styles);
    }

    private static class_2583[] rewriteHovers(NameMatcher matcher, Pieces pieces) {
        class_2583[] out = null;
        IdentityHashMap<class_2568, class_2568> done = null;
        for (int i = 0; i < pieces.count; ++i) {
            class_2568 rewritten;
            class_2583 style = pieces.styles[i];
            class_2568 hover = style.method_10969();
            if (hover == null) continue;
            if (done == null) {
                done = new IdentityHashMap<class_2568, class_2568>();
            }
            if ((rewritten = (class_2568)done.get(hover)) == null) {
                rewritten = ProtectedText.rewriteHover(matcher, hover);
                done.put(hover, rewritten);
            }
            if (rewritten == hover) continue;
            if (out == null) {
                out = Arrays.copyOf(pieces.styles, pieces.count);
            }
            out[i] = style.method_10949(rewritten);
        }
        return out;
    }

    private static class_2568 rewriteHover(NameMatcher matcher, class_2568 hover) {
        if (hover instanceof class_2568.class_10613) {
            class_2568.class_10613 showText = (class_2568.class_10613)hover;
            class_2561 value = showText.comp_3510();
            class_2561 rewritten = value != null ? ProtectedText.rebuild(matcher, (class_5348)value, false) : null;
            return rewritten != null ? new class_2568.class_10613(rewritten) : hover;
        }
        if (hover instanceof class_2568.class_10611) {
            class_2568.class_10611 showEntity = (class_2568.class_10611)hover;
            class_2568.class_5248 info = showEntity.comp_3508();
            if (info == null || info.field_24353.isEmpty()) {
                return hover;
            }
            class_2561 rewritten = ProtectedText.rebuild(matcher, (class_5348)info.field_24353.get(), false);
            return rewritten != null ? new class_2568.class_10611(new class_2568.class_5248(info.field_24351, info.field_24352, rewritten)) : hover;
        }
        return hover;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Pieces
    implements class_5348.class_5246<Object> {
        final StringBuilder text = new StringBuilder();
        class_2583[] styles = new class_2583[8];
        int[] starts = new int[9];
        int count;

        private Pieces() {
        }

        public Optional<Object> accept(class_2583 style, String part) {
            if (part.isEmpty()) {
                return Optional.empty();
            }
            if (this.count == this.styles.length) {
                this.styles = Arrays.copyOf(this.styles, this.count * 2);
                this.starts = Arrays.copyOf(this.starts, this.count * 2 + 1);
            }
            this.styles[this.count] = style;
            this.starts[this.count] = this.text.length();
            ++this.count;
            this.text.append(part);
            return Optional.empty();
        }

        void seal() {
            this.starts[this.count] = this.text.length();
        }

        int pieceAt(int offset) {
            int found = Arrays.binarySearch(this.starts, 0, this.count, offset);
            return found >= 0 ? found : -found - 2;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Runs {
        private final Pieces pieces;
        private final List<Integer> runPieces = new ArrayList<Integer>();
        private final List<StringBuilder> runTexts = new ArrayList<StringBuilder>();

        Runs(Pieces pieces) {
            this.pieces = pieces;
        }

        void copy(String joined, int from, int to) {
            int piece;
            int n = piece = from < to ? this.pieces.pieceAt(from) : 0;
            while (from < to) {
                int pieceEnd = Math.min(this.pieces.starts[piece + 1], to);
                this.run(piece).append(joined, from, pieceEnd);
                from = pieceEnd;
                ++piece;
            }
        }

        void spread(String alias, int start, int end) {
            int span = end - start;
            int total = alias.codePointCount(0, alias.length());
            int index = 0;
            int offset = 0;
            while (offset < alias.length()) {
                int codePoint = alias.codePointAt(offset);
                int source = start + (int)((long)index * (long)span / (long)total);
                this.run(this.pieces.pieceAt(source)).appendCodePoint(codePoint);
                offset += Character.charCount(codePoint);
                ++index;
            }
        }

        private StringBuilder run(int piece) {
            int last = this.runPieces.size() - 1;
            if (last >= 0 && this.runPieces.get(last) == piece) {
                return this.runTexts.get(last);
            }
            StringBuilder text = new StringBuilder();
            this.runPieces.add(piece);
            this.runTexts.add(text);
            return text;
        }

        class_2561 build(class_2583[] styles) {
            class_5250 root = class_2561.method_43473();
            for (int i = 0; i < this.runTexts.size(); ++i) {
                root.method_10852((class_2561)class_2561.method_43470((String)this.runTexts.get(i).toString()).method_10862(styles[this.runPieces.get(i)]));
            }
            return root;
        }
    }
}

