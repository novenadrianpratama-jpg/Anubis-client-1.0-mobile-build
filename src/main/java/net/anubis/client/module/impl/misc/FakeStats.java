/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_274
 *  net.minecraft.class_274$class_275
 *  net.minecraft.class_310
 *  net.minecraft.class_5223
 *  net.minecraft.class_5250
 *  net.minecraft.class_5348
 *  net.minecraft.class_746
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  net.minecraft.class_9013
 *  net.minecraft.class_9014
 *  net.minecraft.class_9015
 *  net.minecraft.class_9021
 *  net.minecraft.class_9022
 *  net.minecraft.class_9025
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.StringSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_274;
import net.minecraft.class_310;
import net.minecraft.class_5223;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import net.minecraft.class_9013;
import net.minecraft.class_9014;
import net.minecraft.class_9015;
import net.minecraft.class_9021;
import net.minecraft.class_9022;
import net.minecraft.class_9025;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class FakeStats
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String OBJECTIVE = "anubis_fs";
    private static final String SHOWN_BY_DEFAULT = ".gg/anubis";
    private static final int LINES = 15;
    private static final Comparator<class_9011> ORDER = Comparator.comparingInt(class_9011::comp_2128).reversed().thenComparing(class_9011::comp_2127, String.CASE_INSENSITIVE_ORDER);
    private static final Pattern MONEY = Pattern.compile("\\$\\s?(\\d[\\d.,]*[KkMmBbTtQq]?)");
    private static final String SMALL_CAPS = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21x\u028f\u1d22";
    private static volatile FakeStats active;
    private final StringSetting playerName = this.addSetting("name", new StringSetting("Name", ".gg/anubis"));
    private final Stat money = this.stat("Money", "money", "balance", "$");
    private final List<Stat> stats = List.of(this.money, this.stat("Shards", "shards", "\u2605"), this.stat("Kills", "kills", "\ud83d\udde1", "\u2694"), this.stat("Deaths", "deaths", "\u2620"), this.stat("Playtime", "playtime", "\u231a"), this.stat("Team", "team"));
    private long seed;
    private boolean applying;
    private class_269 board;
    private class_266 fake;
    private class_266 serverSidebar;
    private String sidebarMoney;
    private boolean nameReported;

    public FakeStats() {
        super("fake-stats", "Fake Stats", "Shows your own name and stats on the sidebar and tab list. Blank stats get scrambled.", ModuleCategory.DONUT);
    }

    private Stat stat(String name, String ... markers) {
        return new Stat(this.addSetting(name.toLowerCase(Locale.ROOT), new StringSetting(name, SHOWN_BY_DEFAULT)), markers);
    }

    @Override
    public boolean enabledByDefault() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean redirect(class_269 board, class_8646 slot, class_266 objective) {
        FakeStats module = active;
        if (module == null || module.applying || slot != class_8646.field_45157) {
            return false;
        }
        if (objective != null && OBJECTIVE.equals(objective.method_1113())) {
            return false;
        }
        module.serverSidebar = objective;
        if (objective == null) {
            return false;
        }
        class_266 shown = module.install(board);
        if (shown == null) {
            return false;
        }
        module.applying = true;
        try {
            board.method_1158(class_8646.field_45157, shown);
        }
        finally {
            module.applying = false;
        }
        return true;
    }

    public static class_2561 tabName(UUID player, class_2561 name) {
        FakeStats module = active;
        if (module == null || name == null || !FakeStats.isSelf(player)) {
            return name;
        }
        return module.fakeTabMoney(name);
    }

    public static class_5348 tabText(class_5348 text) {
        class_5348 class_53482;
        FakeStats module = active;
        if (module != null && text instanceof class_2561) {
            class_2561 line = (class_2561)text;
            class_53482 = module.fakeTabMoney(line);
        } else {
            class_53482 = text;
        }
        return class_53482;
    }

    public static class_9013 tabScore(class_9015 holder, final class_9013 real) {
        final FakeStats module = active;
        class_746 player = class_310.method_1551().field_1724;
        if (module == null || real == null || player == null || !player.method_5820().equals(holder.method_5820())) {
            return real;
        }
        return new class_9013(){

            public int method_55397() {
                return real.method_55397();
            }

            public boolean method_1131() {
                return real.method_1131();
            }

            public class_9022 method_55400() {
                return real.method_55400();
            }

            public class_5250 method_55399(class_9022 fallback) {
                return class_2561.method_43473().method_10852(module.fakeTabMoney((class_2561)real.method_55399(fallback)));
            }
        };
    }

    @Override
    protected void onEnable() {
        this.seed = ThreadLocalRandom.current().nextLong();
        active = this;
        this.on(TickEvent.TYPE, Stage.POST, event -> this.refresh());
        this.refresh();
    }

    @Override
    protected void onDisable() {
        active = null;
        this.restore();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void refresh() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            this.board = null;
            this.fake = null;
            this.serverSidebar = null;
            this.sidebarMoney = null;
            return;
        }
        class_269 current = mc.field_1687.method_8428();
        if (current != this.board) {
            this.board = current;
            this.fake = null;
            this.serverSidebar = null;
        }
        class_266 shown = current.method_1189(class_8646.field_45157);
        if (this.serverSidebar == null || this.serverSidebar.method_1117() != current) {
            class_266 class_2662 = this.serverSidebar = shown != null && !OBJECTIVE.equals(shown.method_1113()) ? shown : null;
        }
        if (this.serverSidebar == null) {
            this.sidebarMoney = null;
            if (this.fake != null && shown == this.fake) {
                this.applying = true;
                try {
                    current.method_1158(class_8646.field_45157, null);
                }
                finally {
                    this.applying = false;
                }
            }
            return;
        }
        class_266 installed = this.install(current);
        if (installed == null) {
            return;
        }
        if (current.method_1189(class_8646.field_45157) != installed) {
            this.applying = true;
            try {
                current.method_1158(class_8646.field_45157, installed);
            }
            finally {
                this.applying = false;
            }
        }
    }

    private class_266 install(class_269 current) {
        class_266 server = this.serverSidebar;
        if (server == null || server.method_1117() != current) {
            return this.fake;
        }
        class_266 objective = current.method_1170(OBJECTIVE);
        if (objective == null) {
            objective = current.method_1168(OBJECTIVE, class_274.field_1468, (class_2561)class_2561.method_43473(), class_274.class_275.field_1472, false, server.method_55384());
        }
        this.fake = objective;
        this.board = current;
        objective.method_55382(server.method_55384());
        this.writeRows(current, objective, server);
        return objective;
    }

    private void writeRows(class_269 current, class_266 objective, class_266 server) {
        int i;
        class_2561 title;
        class_2561 renamedTitle;
        ArrayList<class_9011> rows = new ArrayList<class_9011>();
        for (class_9011 entry : current.method_1184(server)) {
            if (entry.method_55385()) continue;
            rows.add(entry);
        }
        rows.sort(ORDER);
        if (rows.size() > 15) {
            rows.subList(15, rows.size()).clear();
        }
        objective.method_1121((renamedTitle = this.renamed(title = FakeStats.visible(server.method_1114()))) != null ? renamedTitle : this.mask(title, 0));
        boolean nameShown = renamedTitle != null;
        class_9022 format = server.method_55380((class_9022)class_9025.field_47567);
        this.sidebarMoney = null;
        for (i = 0; i < rows.size(); ++i) {
            class_9011 row = (class_9011)rows.get(i);
            class_2561 name = FakeStats.visible((class_2561)class_268.method_1142((class_270)current.method_1164(row.comp_2127()), (class_2561)row.method_55387()));
            class_2561 number = FakeStats.visible((class_2561)row.method_55386(format));
            class_2561 renamed = this.renamed(name);
            Hit hit = renamed == null ? this.find(name.getString(), number.getString()) : null;
            String custom = hit != null ? hit.stat().value().get().strip() : "";
            String shown = null;
            if (renamed != null) {
                name = renamed;
                nameShown = true;
            } else if (custom.isEmpty()) {
                name = this.mask(name, i + 1);
                number = this.mask(number, i + 1);
                if (hit != null) {
                    shown = (hit.inNumber() ? number : name).getString().substring(hit.start(), hit.end());
                }
            } else {
                class_2561 target = hit.inNumber() ? number : name;
                shown = FakeStats.fit(target.getString().substring(hit.start(), hit.end()), custom);
                class_5250 swapped = FakeStats.replace(target, List.of(new int[]{hit.start(), hit.end()}), shown);
                name = hit.inNumber() ? this.mask(name, i + 1) : swapped;
                Object object = number = hit.inNumber() ? swapped : this.mask(number, i + 1);
            }
            if (hit != null && hit.stat() == this.money) {
                this.sidebarMoney = shown;
            }
            class_9015 holder = class_9015.method_55422((String)FakeStats.holderName(i));
            current.method_1195(holder.method_5820());
            class_9014 access = current.method_1180(holder, objective);
            access.method_55410(row.comp_2128());
            access.method_55411(name);
            access.method_55412((class_9022)new class_9021(number));
        }
        for (i = rows.size(); i < 15; ++i) {
            class_9015 holder = class_9015.method_55422((String)FakeStats.holderName(i));
            if (current.method_55430(holder, objective) == null) continue;
            current.method_1155(holder, objective);
        }
        if (!(nameShown || this.playerName.get().isBlank() || this.nameReported)) {
            this.nameReported = true;
            this.reportMissingName(current, rows, format, title);
        }
    }

    private void reportMissingName(class_269 current, List<class_9011> rows, class_9022 format, class_2561 title) {
        StringBuilder lines = new StringBuilder("title: ").append(FakeStats.escaped(title.getString()));
        for (class_9011 row : rows) {
            class_2561 name = FakeStats.visible((class_2561)class_268.method_1142((class_270)current.method_1164(row.comp_2127()), (class_2561)row.method_55387()));
            lines.append("\n  ").append(FakeStats.escaped(name.getString())).append(" | ").append(FakeStats.escaped(FakeStats.visible((class_2561)row.method_55386(format)).getString()));
        }
        LOGGER.info("Fake Stats: no sidebar line shows the name {}\n{}", (Object)FakeStats.selfName(), (Object)lines);
    }

    private static String escaped(String text) {
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c >= ' ' && c < '\u007f') {
                out.append(c);
                continue;
            }
            out.append(String.format("\\u%04X", c));
        }
        return out.toString();
    }

    private void restore() {
        class_266 leftover;
        class_269 current = this.board;
        class_266 server = this.serverSidebar;
        class_266 shown = this.fake;
        this.board = null;
        this.fake = null;
        this.serverSidebar = null;
        this.sidebarMoney = null;
        if (current == null) {
            return;
        }
        if (current.method_1189(class_8646.field_45157) == shown) {
            class_266 back = server != null && server.method_1117() == current ? server : null;
            current.method_1158(class_8646.field_45157, back);
        }
        if ((leftover = current.method_1170(OBJECTIVE)) != null) {
            current.method_1194(leftover);
        }
    }

    private Hit find(String name, String number) {
        for (Stat stat : this.stats) {
            int[] value = FakeStats.valueAfter(name, stat);
            if (value != null && value[0] < value[1]) {
                return new Hit(stat, false, value[0], value[1]);
            }
            if (value != null) {
                return new Hit(stat, true, 0, number.length());
            }
            value = FakeStats.valueAfter(number, stat);
            if (value == null) continue;
            return new Hit(stat, true, value[0], value[1]);
        }
        return null;
    }

    private static int[] valueAfter(String line, Stat stat) {
        for (String marker : stat.markers()) {
            int start;
            int at = FakeStats.markerAt(line, marker);
            if (at < 0) continue;
            int end = line.length();
            for (start = at + marker.length(); start < end && (Character.isWhitespace(line.charAt(start)) || line.charAt(start) == ':'); ++start) {
            }
            while (end > start && Character.isWhitespace(line.charAt(end - 1))) {
                --end;
            }
            return new int[]{start, end};
        }
        return null;
    }

    private static int markerAt(String line, String marker) {
        String text = FakeStats.folded(line);
        boolean wordStart = FakeStats.wordChar(marker.charAt(0));
        boolean wordEnd = FakeStats.wordChar(marker.charAt(marker.length() - 1));
        int at = 0;
        while (at + marker.length() <= text.length()) {
            int end = at + marker.length();
            if (!(!text.regionMatches(true, at, marker, 0, marker.length()) || wordStart && at > 0 && FakeStats.wordChar(text.charAt(at - 1)) || wordEnd && end < text.length() && FakeStats.wordChar(text.charAt(end)))) {
                return at;
            }
            ++at;
        }
        return -1;
    }

    private static String folded(String line) {
        char[] out = line.toCharArray();
        for (int i = 0; i < out.length; ++i) {
            int letter = SMALL_CAPS.indexOf(out[i]);
            if (letter < 0) continue;
            out[i] = (char)(97 + letter);
        }
        return new String(out);
    }

    private static boolean wordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static String selfName() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null ? player.method_7334().name() : null;
    }

    private class_2561 renamed(class_2561 line) {
        int at;
        String self = FakeStats.selfName();
        int n = at = self != null ? FakeStats.markerAt(line.getString(), self) : -1;
        if (at < 0) {
            return null;
        }
        String custom = this.playerName.get().strip();
        return custom.isEmpty() ? line : FakeStats.replace(line, List.of(new int[]{at, at + self.length()}), custom);
    }

    private static String fit(String real, String custom) {
        String bare = custom.startsWith("$") ? custom.substring(1).strip() : custom;
        return real.startsWith("$") ? "$" + bare : bare;
    }

    private String tabMoney() {
        String shown;
        String custom = this.money.value().get().strip();
        String string = shown = custom.isEmpty() ? this.sidebarMoney : custom;
        if (shown == null) {
            return null;
        }
        shown = shown.startsWith("$") ? shown.substring(1).strip() : shown.strip();
        return shown.isEmpty() ? null : shown;
    }

    private class_2561 fakeTabMoney(class_2561 line) {
        String shown = this.tabMoney();
        if (shown == null || line.getString().indexOf(36) < 0) {
            return line;
        }
        class_2561 clean = FakeStats.visible(line);
        Matcher matcher = MONEY.matcher(clean.getString());
        ArrayList<int[]> ranges = new ArrayList<int[]>();
        while (matcher.find()) {
            ranges.add(new int[]{matcher.start(1), matcher.end(1)});
        }
        return ranges.isEmpty() ? line : FakeStats.replace(clean, ranges, shown);
    }

    private static boolean isSelf(UUID player) {
        class_746 self = class_310.method_1551().field_1724;
        return self != null && self.method_5667().equals(player);
    }

    private static class_2561 visible(class_2561 line) {
        if (line.getString().indexOf(167) < 0) {
            return line;
        }
        class_5250 out = class_2561.method_43473();
        StringBuilder run = new StringBuilder();
        class_2583[] runStyle = new class_2583[]{null};
        class_5223.method_27476((class_5348)line, (class_2583)class_2583.field_24360, (index, style, codePoint) -> {
            if (runStyle[0] != null && !style.equals((Object)runStyle[0])) {
                out.method_10852((class_2561)class_2561.method_43470((String)run.toString()).method_10862(runStyle[0]));
                run.setLength(0);
            }
            runStyle[0] = style;
            run.appendCodePoint(codePoint);
            return true;
        });
        if (!run.isEmpty()) {
            out.method_10852((class_2561)class_2561.method_43470((String)run.toString()).method_10862(runStyle[0]));
        }
        return out;
    }

    private static class_5250 replace(class_2561 line, List<int[]> ranges, String text) {
        if (line.getString().isEmpty()) {
            return class_2561.method_43470((String)text);
        }
        class_5250 out = class_2561.method_43473();
        int[] pos = new int[]{0};
        int[] next = new int[]{0};
        line.method_27658((style, piece) -> {
            int start = pos[0];
            int end = start + piece.length();
            int cursor = start;
            while (cursor < end) {
                int[] range;
                int[] nArray = range = next[0] < ranges.size() ? (int[])ranges.get(next[0]) : null;
                if (range == null || range[0] >= end) {
                    out.method_10852((class_2561)class_2561.method_43470((String)piece.substring(cursor - start)).method_10862(style));
                    cursor = end;
                    continue;
                }
                if (cursor < range[0]) {
                    out.method_10852((class_2561)class_2561.method_43470((String)piece.substring(cursor - start, range[0] - start)).method_10862(style));
                    cursor = range[0];
                    continue;
                }
                if (cursor == range[0]) {
                    out.method_10852((class_2561)class_2561.method_43470((String)text).method_10862(style));
                }
                if ((cursor = Math.min(end, range[1])) != range[1]) continue;
                next[0] = next[0] + 1;
            }
            pos[0] = end;
            return Optional.empty();
        }, class_2583.field_24360);
        return out;
    }

    private static String holderName(int row) {
        return new String(new char[]{'\ue000', (char)(57360 + row)});
    }

    private class_2561 mask(class_2561 line, int row) {
        if (line == null) {
            return class_2561.method_43473();
        }
        class_5250 out = class_2561.method_43473();
        boolean[] changed = new boolean[]{false};
        int[] number = new int[]{0};
        line.method_27658((style, text) -> {
            String shown = this.maskText(text, row, number);
            if (!shown.equals(text)) {
                changed[0] = true;
            }
            out.method_10852((class_2561)class_2561.method_43470((String)shown).method_10862(style));
            return Optional.empty();
        }, class_2583.field_24360);
        return changed[0] ? out : line;
    }

    private String maskText(String text, int row, int[] number) {
        StringBuilder out = null;
        int i = 0;
        while (i < text.length()) {
            int cp = text.codePointAt(i);
            if (!Character.isDigit(cp)) {
                if (out != null) {
                    out.appendCodePoint(cp);
                }
                i += Character.charCount(cp);
                continue;
            }
            int start = i;
            int end = i;
            while (end < text.length()) {
                int c = text.codePointAt(end);
                int width = Character.charCount(c);
                if (Character.isDigit(c)) {
                    end += width;
                    continue;
                }
                if (c != 44 && c != 46 || end + width >= text.length() || !Character.isDigit(text.codePointAt(end + width))) break;
                end += width;
            }
            String raw = text.substring(start, end);
            int n = number[0];
            number[0] = n + 1;
            String fake = this.fakeNumber(row, n, raw);
            if (out == null) {
                if (!fake.equals(raw)) {
                    out = new StringBuilder(text.length()).append(text, 0, start).append(fake);
                }
            } else {
                out.append(fake);
            }
            i = end;
        }
        return out == null ? text : out.toString();
    }

    private String fakeNumber(int row, int index, String raw) {
        Object rendered;
        boolean leadingZero;
        long fake;
        int digits = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c < '0' || c > '9') continue;
            ++digits;
        }
        if (digits == 0) {
            return raw;
        }
        if (digits > 15) {
            return this.shiftWide(row, index, raw);
        }
        long value = 0L;
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c < '0' || c > '9') continue;
            value = value * 10L + (long)(c - 48);
        }
        long span = FakeStats.pow10(digits);
        long offset = Long.remainderUnsigned(FakeStats.mix(this.seed ^ (long)row * 1315423911L ^ (long)index * 2654435761L ^ (long)digits), span);
        if (offset == 0L) {
            offset = 1L;
        }
        if ((fake = FakeStats.shifted(value, offset, span, digits, leadingZero = raw.charAt(0) == '0' && digits > 1)) == value) {
            fake = FakeStats.shifted(value, offset == span - 1L ? 1L : offset + 1L, span, digits, leadingZero);
        }
        if (((String)(rendered = Long.toString(fake))).length() < digits) {
            rendered = "0".repeat(digits - ((String)rendered).length()) + (String)rendered;
        }
        StringBuilder result = new StringBuilder(raw.length());
        int cursor = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c >= '0' && c <= '9') {
                result.append(((String)rendered).charAt(cursor++));
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    private static long shifted(long value, long offset, long span, int digits, boolean leadingZero) {
        long fake = Math.floorMod(value + offset, span);
        if (!leadingZero && digits > 1 && fake < span / 10L) {
            fake += span / 10L;
        }
        return fake;
    }

    private String shiftWide(int row, int index, String raw) {
        long salt = FakeStats.mix(this.seed ^ (long)row * 1315423911L ^ (long)index);
        StringBuilder result = new StringBuilder(raw.length());
        int pos = 0;
        boolean first = true;
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c >= '0' && c <= '9') {
                int shift = (int)Long.remainderUnsigned(FakeStats.mix(salt + (long)pos), 9L) + 1;
                int shown = (c - 48 + shift) % 10;
                if (first && shown == 0) {
                    shown = shift;
                }
                first = false;
                result.append((char)(48 + shown));
                ++pos;
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    private static long pow10(int digits) {
        long value = 1L;
        for (int i = 0; i < digits; ++i) {
            value *= 10L;
        }
        return value;
    }

    private static long mix(long z) {
        z = (z ^ z >>> 30) * -4658895280553007687L;
        z = (z ^ z >>> 27) * -7723592293110705685L;
        return z ^ z >>> 31;
    }

    @Environment(value=EnvType.CLIENT)
    private record Stat(StringSetting value, String[] markers) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Hit(Stat stat, boolean inNumber, int start, int end) {
    }
}

