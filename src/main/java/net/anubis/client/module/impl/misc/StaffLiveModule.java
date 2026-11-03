/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_2558
 *  net.minecraft.class_2558$class_10608
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_6880
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import imgui.ImDrawList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.gui.HudPanel;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringListSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class StaffLiveModule
extends ClientModule
implements HudOverlayProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String BROWSER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36";
    private static final Pattern CHANNEL = Pattern.compile("^(yt|tt):@?([A-Za-z0-9._-]+)$", 2);
    private static final String CANONICAL = "<link rel=\"canonical\" href=\"";
    private static final String LIVE_NOW = "\"isLiveNow\":true";
    private static final String META_TITLE = "<meta name=\"title\" content=\"";
    private static final String START = "\"startTimestamp\":\"";
    private static final int MAX_TITLE_CHARS = 48;
    private final StringListSetting channels = this.addSetting("channels", new StringListSetting("Channels", "yt:wizoxlol", "yt:pastagamer08", "tt:pastagamer08", "tt:theauzzi", "tt:u_vv7_", "yt:DrDonutt"));
    private final NumberSetting interval = this.addSetting("check-every", new NumberSetting("Check Every (s)", 60.0f, 15.0f, 600.0f).step(5.0f));
    private final NumberSetting scale = this.addSetting("scale", new NumberSetting("Scale", 1.0f, 0.5f, 3.0f).step(0.05f));
    private final NumberSetting opacity = this.addSetting("opacity", HudPanel.opacitySetting());
    private final HudPanel panel = new HudPanel(this.addSetting("pos-x", HudPanel.coordinate("X")), this.addSetting("pos-y", HudPanel.coordinate("Y")), this.scale, this.opacity);
    private final HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(10L)).build();
    private final Map<String, Boolean> live = new ConcurrentHashMap<String, Boolean>();
    private final Map<String, Live> shown = new ConcurrentHashMap<String, Live>();
    private volatile Thread worker;

    public StaffLiveModule() {
        super("staff-live", "Staff Live", "Tells you when DonutSMP staff go live on YouTube or TikTok: a chat line with a watch link, a chime, and a panel with the stream title while they are live. Channels are yt:handle or tt:handle.", ModuleCategory.DONUT);
        this.usesNotifications();
    }

    @Override
    protected void onEnable() {
        this.live.clear();
        this.shown.clear();
        Thread thread = new Thread(this::run, "Anubis Staff Live");
        thread.setDaemon(true);
        this.worker = thread;
        thread.start();
    }

    @Override
    protected void onDisable() {
        Thread thread = this.worker;
        this.worker = null;
        if (thread != null) {
            thread.interrupt();
        }
        this.shown.clear();
    }

    private void run() {
        long next = 0L;
        while (this.worker == Thread.currentThread()) {
            if (System.currentTimeMillis() >= next) {
                HashSet<String> keys = new HashSet<String>();
                for (Channel channel : this.channels()) {
                    if (this.worker != Thread.currentThread()) {
                        return;
                    }
                    keys.add(channel.key());
                    this.check(channel);
                }
                this.live.keySet().retainAll(keys);
                this.shown.keySet().retainAll(keys);
                next = System.currentTimeMillis() + (long)(this.interval.get() * 1000.0f);
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                return;
            }
        }
    }

    private List<Channel> channels() {
        ArrayList<Channel> out = new ArrayList<Channel>();
        for (String entry : this.channels.get()) {
            Matcher m = CHANNEL.matcher(entry.trim());
            if (!m.find()) continue;
            out.add(new Channel(m.group(1).equalsIgnoreCase("yt"), m.group(2)));
        }
        return out;
    }

    private void check(Channel channel) {
        Stream stream;
        try {
            stream = channel.youtube() ? this.youtube(channel.handle()) : this.tiktok(channel.handle());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        catch (Exception e) {
            LOGGER.debug("[Staff Live] check of {} failed: {}", (Object)channel.key(), (Object)e.toString());
            return;
        }
        if (stream == null) {
            return;
        }
        Boolean was = this.live.put(channel.key(), stream.live());
        if (stream.live() && !Boolean.TRUE.equals(was)) {
            this.announce(channel, stream);
        }
        if (!stream.live()) {
            this.shown.remove(channel.key());
        }
    }

    private Stream youtube(String handle) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.youtube.com/@" + handle + "/live")).timeout(Duration.ofSeconds(20L)).header("User-Agent", BROWSER).header("Accept-Language", "en-US,en;q=0.9").header("Accept-Encoding", "gzip").header("Cookie", "SOCS=CAI; CONSENT=YES+").build();
        HttpResponse<InputStream> response = this.http.send(request, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream raw = response.body();){
            int n;
            if (response.statusCode() != 200) {
                Stream stream = null;
                return stream;
            }
            boolean gzip = response.headers().firstValue("Content-Encoding").map(v -> v.equalsIgnoreCase("gzip")).orElse(false);
            InputStreamReader in = new InputStreamReader(gzip ? new GZIPInputStream(raw) : raw, StandardCharsets.UTF_8);
            char[] buffer = new char[16384];
            StringBuilder window = new StringBuilder();
            String watch = null;
            String title = null;
            boolean liveNow = false;
            while ((n = in.read(buffer)) > 0) {
                String href;
                window.append(buffer, 0, n);
                if (watch == null && (href = StaffLiveModule.after(window, CANONICAL)) != null) {
                    if (!href.contains("/watch?v=")) {
                        Stream stream = Stream.OFFLINE;
                        return stream;
                    }
                    watch = href;
                }
                if (watch != null && title == null) {
                    title = StaffLiveModule.after(window, META_TITLE);
                }
                if (watch != null && !liveNow) {
                    boolean bl = liveNow = window.indexOf(LIVE_NOW) >= 0;
                }
                if (liveNow) {
                    String start = StaffLiveModule.after(window, START);
                    if (start == null && window.length() <= 4096) continue;
                    Stream stream = new Stream(true, StaffLiveModule.clean(title), watch, null, StaffLiveModule.parseStart(start));
                    return stream;
                }
                if (window.length() <= 4096) continue;
                window.delete(0, window.length() - 512);
            }
            Stream stream = liveNow ? new Stream(true, StaffLiveModule.clean(title), watch, null, 0L) : Stream.OFFLINE;
            return stream;
        }
    }

    private Stream tiktok(String user) throws Exception {
        String url = "https://www.tiktok.com/api-live/user/room/?aid=1988&app_language=en&app_name=tiktok_web&sourceType=54&uniqueId=" + URLEncoder.encode(user, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(20L)).header("User-Agent", BROWSER).header("Accept", "application/json").build();
        HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        JsonElement body = JsonParser.parseString((String)response.body());
        if (!body.isJsonObject()) {
            return null;
        }
        JsonObject data = StaffLiveModule.object(body.getAsJsonObject(), "data");
        JsonObject room = StaffLiveModule.object(data, "liveRoom");
        if (room == null || !room.has("status")) {
            return null;
        }
        boolean on = room.get("status").getAsInt() == 2;
        JsonObject owner = StaffLiveModule.object(data, "user");
        String nick = owner != null && owner.has("nickname") ? owner.get("nickname").getAsString() : null;
        String title = room.has("title") ? room.get("title").getAsString() : null;
        long started = room.has("startTime") ? room.get("startTime").getAsLong() * 1000L : 0L;
        return new Stream(on, StaffLiveModule.clean(title), "https://www.tiktok.com/@" + user + "/live", nick, started);
    }

    private void announce(Channel channel, Stream stream) {
        Object url = stream.link() != null ? stream.link() : "https://www.youtube.com/@" + channel.handle() + "/live";
        String who = stream.nick() != null && !stream.nick().equalsIgnoreCase(channel.handle()) ? channel.handle() + " (" + stream.nick() + ")" : channel.handle();
        String platform = channel.youtube() ? "YouTube" : "TikTok";
        long now = System.currentTimeMillis();
        long since = stream.started() > 0L && stream.started() <= now ? stream.started() : now;
        this.shown.putIfAbsent(channel.key(), new Live(who, platform, stream.title(), since, now));
        LOGGER.info("[Staff Live] {} is live on {}", (Object)who, (Object)platform);
        class_310 mc = class_310.method_1551();
        mc.execute(() -> this.lambda$announce$2(mc, who, platform, stream, (String)url));
    }

    private List<Live> rows() {
        if (!this.isEnabled()) {
            return List.of();
        }
        return this.shown.values().stream().sorted(Comparator.comparingLong(Live::shownAt)).toList();
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled() && class_310.method_1551().field_1724 != null && !this.shown.isEmpty();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        List<Live> live = this.rows();
        if (live.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        ArrayList<HudPanel.Line> lines = new ArrayList<HudPanel.Line>();
        for (Live row : live) {
            lines.add(new HudPanel.Line(0, row.who(), -1, row.platform() + "  \u00b7  " + StaffLiveModule.ago(now - row.since()), -47814));
            if (row.title() == null || row.title().isBlank()) continue;
            lines.add(new HudPanel.Line(StaffLiveModule.shorten(row.title()), -6052949));
        }
        this.panel.card(dl, "LIVE", String.valueOf(live.size()), -47814, lines, "staff_list", screenW, screenH, 0.5f, 10.0f);
    }

    private static String shorten(String title) {
        return title.length() <= 48 ? title : title.substring(0, 47).stripTrailing() + "\u2026";
    }

    private static String ago(long ms) {
        long m = ms / 60000L;
        return m < 1L ? "now" : (m < 60L ? m + "m" : m / 60L + "h " + m % 60L + "m");
    }

    private static String after(CharSequence s, String marker) {
        String text = s.toString();
        int at = text.indexOf(marker);
        int end = at < 0 ? -1 : text.indexOf(34, at + marker.length());
        return end < 0 ? null : text.substring(at + marker.length(), end);
    }

    private static String clean(String s) {
        if (s == null) {
            return null;
        }
        s = s.replace("&quot;", "\"").replace("&#39;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
        StringBuilder out = new StringBuilder(s.length());
        s.codePoints().filter(cp -> cp >= 32 && cp < 8192 || cp >= 8208 && cp < 9728).forEach(out::appendCodePoint);
        return out.toString().replaceAll("\\s+", " ").trim();
    }

    private static long parseStart(String iso) {
        try {
            return iso == null ? 0L : OffsetDateTime.parse(iso).toInstant().toEpochMilli();
        }
        catch (RuntimeException e) {
            return 0L;
        }
    }

    private static JsonObject object(JsonObject parent, String key) {
        if (parent == null) {
            return null;
        }
        JsonElement element = parent.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    private /* synthetic */ void lambda$announce$2(class_310 mc, String who, String platform, Stream stream, String url) {
        if (!this.isEnabled() || mc.field_1724 == null) {
            return;
        }
        ModuleChat.Body body = ModuleChat.body().name(who).text(" is ").danger("LIVE").text(" on " + platform);
        if (stream.title() != null && !stream.title().isBlank()) {
            body.text(": " + stream.title());
        }
        body.text(" ").append((class_2561)class_2561.method_43470((String)"[watch]").method_27694(style -> style.method_10958((class_2558)new class_2558.class_10608(URI.create(url))).method_30938(Boolean.valueOf(true))));
        ModuleChat.send((ClientModule)this, body);
        if (this.notificationsOn()) {
            mc.method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_14725, (float)1.25f));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Channel(boolean youtube, String handle) {
        String key() {
            return (this.youtube ? "yt:" : "tt:") + this.handle.toLowerCase(Locale.ROOT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Stream(boolean live, String title, String link, String nick, long started) {
        static final Stream OFFLINE = new Stream(false, null, null, null, 0L);
    }

    @Environment(value=EnvType.CLIENT)
    private record Live(String who, String platform, String title, long since, long shownAt) {
    }
}

