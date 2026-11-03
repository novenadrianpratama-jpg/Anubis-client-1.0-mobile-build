/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_124
 *  net.minecraft.class_156
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10608
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_2583
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3532
 *  net.minecraft.class_368
 *  net.minecraft.class_5250
 *  net.minecraft.class_5251
 *  net.minecraft.class_9334
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.donutbets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_368;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class DonutBetsFeed {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final URI FEED = URI.create("https://donutbets.net/api/client/feed");
    private static final String SITE = "donutbets.net";
    static final int MAX_BYTES = 65536;
    private static final int MAX_EVENTS = 20;
    private static final long POLL_MILLIS = 55000L;
    private static final long POLL_JITTER_MILLIS = 10000L;
    private static final long FIRST_POLL_MILLIS = 15000L;
    private static final long READ_DEADLINE_SECONDS = 12L;
    private static final int HURT_QUIET_TICKS = 200;
    private static final int ID_MAX = 80;
    private static final int TITLE_MAX = 100;
    private static final int TEXT_MAX = 300;
    private static final int SEEN_MAX = 300;
    private static final String TOP_CENTER = "Top Center";
    private static final String TOP_LEFT = "Top Left";
    private static final String TOP_RIGHT = "Top Right";
    private static final long SOUND_GAP_MILLIS = 10000L;
    private static final String FANFARE = "Fanfare";
    private static final String LEVEL_UP = "Level Up";
    private static final String BELL = "Bell";
    private static final String AMETHYST = "Amethyst";
    private static final String SILENT = "Off";
    private static final String CARD_AT = "Top Center";
    private static final String SOUND = "Level Up";
    private static final int PER_HOUR = 3;
    private static final String NAME = "DonutBets";
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).followRedirects(HttpClient.Redirect.NEVER).build();
    private final Gson gson = new Gson();
    private volatile Thread worker;
    private volatile long nextPoll;
    private String etag;
    private final Deque<Event> queue = new ArrayDeque<Event>();
    private final Set<String> seen = new LinkedHashSet<String>();
    private final Deque<Long> shownAt = new ArrayDeque<Long>();
    private int ticksSinceHurt = 200;
    private long lastSoundAt;
    private static final DonutBetsFeed INSTANCE = new DonutBetsFeed();

    private DonutBetsFeed() {
    }

    public static void start() {
        INSTANCE.begin();
    }

    static DonutBetsFeed get() {
        return INSTANCE;
    }

    private synchronized void begin() {
        if (this.worker != null) {
            return;
        }
        this.loadSeen();
        this.nextPoll = System.currentTimeMillis() + 15000L;
        LOGGER.info("[DonutBets] Reading DonutBets event texts from {} about once a minute. Text only: nothing about the player is sent, nothing is downloaded or run. Part of the client, always on.", (Object)FEED);
        Thread thread = new Thread(this::run, "Anubis DonutBets event feed (text only)");
        thread.setDaemon(true);
        this.worker = thread;
        thread.start();
        TickEvent.TYPE.listen(this, Stage.POST, EventPriority.NORMAL, event -> this.tick());
    }

    private void run() {
        while (this.worker == Thread.currentThread()) {
            if (System.currentTimeMillis() >= this.nextPoll) {
                this.poll();
                this.nextPoll = System.currentTimeMillis() + 55000L + ThreadLocalRandom.current().nextLong(10000L);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }

    private void poll() {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder(FEED).timeout(Duration.ofSeconds(8L)).header("Accept", "application/json").header("User-Agent", "Anubis-Client/" + DonutBetsFeed.version() + " (DonutBets event feed, text only)").GET();
            if (this.etag != null) {
                request.header("If-None-Match", this.etag);
            }
            HttpResponse<InputStream> response = this.http.send(request.build(), HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream body = response.body();){
                CompletableFuture.runAsync(() -> DonutBetsFeed.closeQuietly(body), CompletableFuture.delayedExecutor(12L, TimeUnit.SECONDS));
                if (response.statusCode() == 304) {
                    return;
                }
                String type = response.headers().firstValue("Content-Type").orElse("").toLowerCase(Locale.ROOT);
                if (response.statusCode() != 200 || !type.contains("json")) {
                    LOGGER.debug("[DonutBets] feed answered {} {}", (Object)response.statusCode(), (Object)type);
                    return;
                }
                byte[] bytes = body.readNBytes(65537);
                if (bytes.length > 65536) {
                    LOGGER.debug("[DonutBets] feed is bigger than {} bytes, not read", (Object)65536);
                    return;
                }
                this.etag = response.headers().firstValue("ETag").filter(DonutBetsFeed::plainHeader).orElse(null);
                this.accept(new String(bytes, StandardCharsets.UTF_8));
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            LOGGER.debug("[DonutBets] feed did not answer: {}", (Object)e.toString());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void accept(String body) {
        List<Event> fresh;
        try {
            fresh = DonutBetsFeed.parse(body, Instant.now());
        }
        catch (RuntimeException | StackOverflowError e) {
            LOGGER.debug("[DonutBets] feed is not valid JSON: {}", (Object)e.toString());
            return;
        }
        Deque<Event> deque = this.queue;
        synchronized (deque) {
            for (Event event : fresh) {
                if (this.queue.size() >= 20) break;
                if (this.seen.contains(event.id()) || this.queue.stream().anyMatch(waiting -> waiting.id().equals(event.id()))) continue;
                this.queue.add(event);
            }
        }
    }

    static List<Event> parse(String body, Instant now) {
        ArrayList<Event> out = new ArrayList<Event>();
        JsonElement root = JsonParser.parseString((String)body);
        if (!(root.isJsonObject() && root.getAsJsonObject().has("events") && root.getAsJsonObject().get("events").isJsonArray())) {
            return out;
        }
        JsonArray events = root.getAsJsonObject().getAsJsonArray("events");
        for (JsonElement element : events) {
            if (out.size() >= 20) break;
            if (!element.isJsonObject()) continue;
            JsonObject event = element.getAsJsonObject();
            String id = DonutBetsFeed.clean(DonutBetsFeed.text(event, "id"), 80);
            String title = DonutBetsFeed.clean(DonutBetsFeed.text(event, "title"), 100);
            String text = DonutBetsFeed.clean(DonutBetsFeed.text(event, "text"), 300);
            if (id.isEmpty() || title.isEmpty() && text.isEmpty()) continue;
            Instant starts = DonutBetsFeed.time(event, "startsAt");
            Instant ends = DonutBetsFeed.time(event, "endsAt");
            if (starts != null && now.isBefore(starts) || ends != null && !now.isBefore(ends)) continue;
            out.add(new Event(id, title, text, DonutBetsFeed.safeLink(DonutBetsFeed.text(event, "url"))));
        }
        return out;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tick() {
        Event event;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        int n = this.ticksSinceHurt = mc.field_1724.field_6235 > 0 ? 0 : this.ticksSinceHurt + 1;
        if (mc.field_1755 != null || this.ticksSinceHurt < 200) {
            return;
        }
        long now = System.currentTimeMillis();
        while (!this.shownAt.isEmpty() && now - this.shownAt.peekFirst() > 3600000L) {
            this.shownAt.pollFirst();
        }
        if (this.shownAt.size() >= 3) {
            return;
        }
        Deque<Event> deque = this.queue;
        synchronized (deque) {
            event = this.queue.pollFirst();
        }
        if (event == null) {
            return;
        }
        this.shownAt.addLast(now);
        this.seen.add(event.id());
        while (this.seen.size() > 300) {
            this.seen.remove(this.seen.iterator().next());
        }
        this.saveSeen();
        this.show(mc, event, now);
    }

    private void show(class_310 mc, Event event, long now) {
        boolean quiet;
        String link;
        ModuleChat.Body body = ModuleChat.body();
        if (!event.title().isEmpty()) {
            body.append((class_2561)class_2561.method_43473().method_54663(0xFFFFFF).method_10852((class_2561)DonutBetsFeed.styled(event.title(), false)));
        }
        if (!event.text().isEmpty()) {
            if (!event.title().isEmpty()) {
                body.text(": ");
            }
            body.append((class_2561)DonutBetsFeed.styled(event.text(), false));
        }
        if ((link = DonutBetsFeed.safeLink(event.url())) != null) {
            body.text(" ").append((class_2561)class_2561.method_43470((String)"[open]").method_27694(style -> style.method_10977(class_124.field_1065).method_30938(Boolean.valueOf(true)).method_10958((class_2558)new class_2558.class_10608(URI.create(link))).method_10949((class_2568)new class_2568.class_10613((class_2561)class_2561.method_43470((String)link)))));
        }
        ModuleChat.sendAs(NAME, body);
        class_5250 title = event.title().isEmpty() ? class_2561.method_43470((String)NAME) : DonutBetsFeed.styled(event.title(), true);
        class_5250 message = event.text().isEmpty() ? class_2561.method_43470((String)SITE) : DonutBetsFeed.styled(event.text(), true);
        boolean bl = quiet = now - this.lastSoundAt < 10000L;
        if (!quiet) {
            this.lastSoundAt = now;
        }
        mc.method_1566().method_1999((class_368)new BetsToast((class_2561)title, (class_2561)message, quiet ? SILENT : "Level Up", "Top Center"));
        LOGGER.info("[DonutBets] showed event {}", (Object)event.id());
    }

    static String safeLink(String url) {
        if (url == null || url.isBlank() || url.length() > 300) {
            return null;
        }
        try {
            String host;
            URI uri = new URI(url.trim());
            String string = host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getRawUserInfo() != null || uri.getPort() != -1) {
                return null;
            }
            if (!host.equals(SITE) && !host.endsWith(".donutbets.net")) {
                return null;
            }
            return uri.toASCIIString();
        }
        catch (Exception e) {
            return null;
        }
    }

    static class_5250 styled(String text, boolean liveRainbow) {
        class_5250 out = class_2561.method_43473();
        StringBuilder run = new StringBuilder();
        class_2583 style = class_2583.field_24360;
        boolean rainbow = false;
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if ((c == '&' || c == '\u00a7') && i + 1 < text.length()) {
                char next = Character.toLowerCase(text.charAt(i + 1));
                if (next == '&') {
                    run.append('&');
                    i += 2;
                    continue;
                }
                if (text.regionMatches(true, i + 1, "rainbow", 0, 7)) {
                    DonutBetsFeed.flush(out, run, style, rainbow, liveRainbow);
                    style = class_2583.field_24360;
                    rainbow = true;
                    i += 8;
                    continue;
                }
                if (next == '#' && i + 8 <= text.length() && text.substring(i + 2, i + 8).matches("[0-9a-fA-F]{6}")) {
                    DonutBetsFeed.flush(out, run, style, rainbow, liveRainbow);
                    style = class_2583.field_24360.method_27703(class_5251.method_27717((int)Integer.parseInt(text.substring(i + 2, i + 8), 16)));
                    rainbow = false;
                    i += 8;
                    continue;
                }
                class_124 format = class_124.method_544((char)next);
                if (format != null) {
                    DonutBetsFeed.flush(out, run, style, rainbow, liveRainbow);
                    if (format == class_124.field_1070) {
                        style = class_2583.field_24360;
                        rainbow = false;
                    } else if (format.method_543()) {
                        style = class_2583.field_24360.method_10977(format);
                        rainbow = false;
                    } else {
                        style = style.method_27706(format);
                    }
                    i += 2;
                    continue;
                }
            }
            run.append(c);
            ++i;
        }
        DonutBetsFeed.flush(out, run, style, rainbow, liveRainbow);
        return out;
    }

    private static void flush(class_5250 out, StringBuilder run, class_2583 style, boolean rainbow, boolean liveRainbow) {
        if (run.isEmpty()) {
            return;
        }
        String text = run.toString();
        run.setLength(0);
        if (!rainbow) {
            out.method_10852((class_2561)class_2561.method_43470((String)text).method_10862(style));
        } else if (liveRainbow) {
            out.method_10852((class_2561)class_2561.method_43470((String)text).method_10862(style.method_10975("anubis:rainbow")));
        } else {
            int c;
            int count = text.codePointCount(0, text.length());
            int at = 0;
            for (int i = 0; i < text.length(); i += Character.charCount(c)) {
                c = text.codePointAt(i);
                float hue = (float)at++ / (float)Math.max(count, 7) * 0.85f;
                out.method_10852((class_2561)class_2561.method_43470((String)Character.toString(c)).method_10862(style.method_27703(class_5251.method_27717((int)class_3532.method_15369((float)hue, (float)0.75f, (float)1.0f)))));
            }
        }
    }

    static String clean(String text, int max) {
        if (text == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            if (c == 167) {
                c = 38;
            }
            int type = Character.getType(c);
            if (Character.isISOControl(c) || type == 16 || type == 13 || type == 14 || type == 18 || type == 19 || type == 0) continue;
            if (out.length() + Character.charCount(c) > max) break;
            out.appendCodePoint(c);
        }
        return out.toString().trim();
    }

    private static boolean plainHeader(String value) {
        if (value.isEmpty() || value.length() > 200) {
            return false;
        }
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c >= ' ' && c <= '~') continue;
            return false;
        }
        return true;
    }

    private static void closeQuietly(InputStream stream) {
        try {
            stream.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static String text(JsonObject object, String key) {
        try {
            return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : null;
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    private static Instant time(JsonObject object, String key) {
        String value = DonutBetsFeed.text(object, key);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String version() {
        return FabricLoader.getInstance().getModContainer("anubisplus").map(container -> container.getMetadata().getVersion().getFriendlyString()).orElse("dev");
    }

    private static Path seenFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("donutbets_seen.json");
    }

    private void loadSeen() {
        this.seen.clear();
        try {
            Path file = DonutBetsFeed.seenFile();
            if (!Files.exists(file, new LinkOption[0]) || Files.size(file) > 65536L) {
                return;
            }
            String[] ids = (String[])this.gson.fromJson(Files.readString(file, StandardCharsets.UTF_8), String[].class);
            if (ids == null) {
                return;
            }
            for (String id : ids) {
                String clean = DonutBetsFeed.clean(id, 80);
                if (clean.isEmpty() || this.seen.size() >= 300) continue;
                this.seen.add(clean);
            }
        }
        catch (IOException | RuntimeException e) {
            LOGGER.debug("[DonutBets] could not read the shown events: {}", (Object)e.toString());
        }
    }

    private void saveSeen() {
        try {
            Path file = DonutBetsFeed.seenFile();
            Files.createDirectories(file.getParent(), new FileAttribute[0]);
            Files.writeString(file, (CharSequence)this.gson.toJson((Object)this.seen.toArray(new String[0])), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException e) {
            LOGGER.debug("[DonutBets] could not save the shown events: {}", (Object)e.toString());
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Event(String id, String title, String text, String url) {
    }

    @Environment(value=EnvType.CLIENT)
    static final class BetsToast
    extends ChallengeToast {
        private static final long SHOWN_MILLIS = 30000L;
        private static final float RAINBOW_MILLIS = 3000.0f;
        private static final int BACK_SHIFT = 6;
        private static final float BACK_LEAN = (float)Math.toRadians(22.0);
        private static final float BACK_SCALE = 0.85f;
        private static final class_1799 BACK = new class_1799((class_1935)class_1802.field_8477);
        private final String position;
        private final String sound;

        BetsToast(class_2561 title, class_2561 message, String sound, String position) {
            super(BetsToast.glinting(new class_1799((class_1935)class_1802.field_8477)), title, message, !DonutBetsFeed.SILENT.equals(sound));
            this.sound = sound;
            this.position = position;
        }

        @Override
        public class_3414 method_66530() {
            return switch (this.sound) {
                case DonutBetsFeed.FANFARE -> class_3417.field_15195;
                case DonutBetsFeed.BELL -> (class_3414)class_3417.field_14793.comp_349();
                case DonutBetsFeed.AMETHYST -> class_3417.field_26980;
                case DonutBetsFeed.SILENT -> null;
                default -> class_3417.field_14709;
            };
        }

        private static class_1799 glinting(class_1799 stack) {
            stack.method_57379(class_9334.field_49641, (Object)true);
            return stack;
        }

        public float method_71809(int screenWidth, float visiblePortion) {
            if (DonutBetsFeed.TOP_LEFT.equals(this.position)) {
                return (float)(-this.method_29049()) * (1.0f - visiblePortion);
            }
            if (DonutBetsFeed.TOP_RIGHT.equals(this.position)) {
                return super.method_71809(screenWidth, visiblePortion);
            }
            return (float)(screenWidth - this.method_29049()) / 2.0f;
        }

        @Override
        protected void renderIcon(class_332 graphics, class_1799 icon) {
            BetsToast.leaning(graphics, 2, -BACK_LEAN);
            BetsToast.leaning(graphics, 14, BACK_LEAN);
            graphics.method_51445(icon, 8, 8);
        }

        private static void leaning(class_332 graphics, int x, float angle) {
            graphics.method_51448().pushMatrix();
            graphics.method_51448().translate((float)(x + 8), 16.0f);
            graphics.method_51448().rotate(angle);
            graphics.method_51448().scale(0.85f, 0.85f);
            graphics.method_51448().translate(-8.0f, -8.0f);
            graphics.method_51445(BACK, 0, 0);
            graphics.method_51448().popMatrix();
        }

        @Override
        protected long displayMillis() {
            return 30000L;
        }

        @Override
        public void method_1986(class_332 graphics, class_327 font, long visibleFor) {
            super.method_1986(graphics, font, visibleFor);
            BetsToast.frame(graphics, this.cardRight(), this.cardBottom(), (float)(class_156.method_658() % 3000L) / 3000.0f);
        }

        private static void frame(class_332 graphics, int right, int bottom, float turn) {
            int y;
            int x;
            int top = 1;
            int across = right - 4;
            int down = bottom - top - 4;
            float perimeter = 2.0f * (float)(across + down) + 8.0f;
            float at = 0.0f;
            for (x = 2; x < right - 2; ++x) {
                float f = at;
                at = f + 1.0f;
                BetsToast.dot(graphics, x, top, f / perimeter + turn);
            }
            float f = at;
            at = f + 1.0f;
            BetsToast.dot(graphics, right - 2, top + 1, f / perimeter + turn);
            for (y = top + 2; y < bottom - 2; ++y) {
                float f2 = at;
                at = f2 + 1.0f;
                BetsToast.dot(graphics, right - 1, y, f2 / perimeter + turn);
            }
            float f3 = at;
            at = f3 + 1.0f;
            BetsToast.dot(graphics, right - 2, bottom - 2, f3 / perimeter + turn);
            for (x = right - 3; x >= 2; --x) {
                float f4 = at;
                at = f4 + 1.0f;
                BetsToast.dot(graphics, x, bottom - 1, f4 / perimeter + turn);
            }
            float f5 = at;
            at = f5 + 1.0f;
            BetsToast.dot(graphics, 1, bottom - 2, f5 / perimeter + turn);
            for (y = bottom - 3; y >= top + 2; --y) {
                float f6 = at;
                at = f6 + 1.0f;
                BetsToast.dot(graphics, 0, y, f6 / perimeter + turn);
            }
            float f7 = at;
            at = f7 + 1.0f;
            BetsToast.dot(graphics, 1, top + 1, f7 / perimeter + turn);
        }

        private static void dot(class_332 graphics, int x, int y, float hue) {
            graphics.method_25294(x, y, x + 1, y + 1, 0xFF000000 | class_3532.method_15369((float)(hue - (float)Math.floor(hue)), (float)0.8f, (float)1.0f));
        }
    }
}

