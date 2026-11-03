/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongListIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1923
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_634
 *  net.minecraft.class_7134
 *  net.minecraft.class_746
 *  net.minecraft.class_9848
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc;

import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.anubis.client.gui.GlintHeadRenderer;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.anubis.client.module.impl.misc.AutoReconnectModule;
import net.anubis.client.module.impl.misc.PlayerBypassAutoRtp;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.ChunkAreas;
import net.anubis.client.util.FoglessLines;
import net.anubis.client.util.playerbypass.ChunkLightSnapshot;
import net.anubis.client.util.playerbypass.PlayerBypassLightTracker;
import net.anubis.client.util.playerbypass.VisibleChunkCache;
import net.anubis.client.util.playerbypass2.SimulationSignals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_634;
import net.minecraft.class_7134;
import net.minecraft.class_746;
import net.minecraft.class_9848;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class PlayerBypassModule
extends ClientModule {
    private static final int RENDER_DISTANCE_CHUNKS = 16;
    private static final double MARKER_Y = 64.0;
    private static final float LINE_WIDTH = 2.0f;
    private static final int MARK_FILL_ALPHA = 34;
    private static final int BASE_COLOR = -2516480;
    private static final int SUSPECT_COLOR = -5766;
    private static final int ACTIVITY_COLOR = -11205;
    private static final int MEMORY_SECONDS = 45;
    private static final float FALLOFF = 0.3f;
    private static final float MIN_SOURCE = 0.5f;
    private static final int MAX_FOUND = 256;
    private static final int ALERT_SPACING = 4;
    private static final int PIN_SPACING = 1;
    private static final float FROZEN_NEIGHBOUR = 0.5f;
    private static final int REPORT_SOURCES = 4;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private final NumberSetting opacity = this.addSetting("opacity", new NumberSetting("Opacity", 140.0f, 10.0f, 255.0f).step(1.0f));
    private final BooleanSetting tracers = this.addSetting("tracers", new BooleanSetting("Tracers", false));
    private final NumberSetting sensitivity = this.addSetting("sens", new NumberSetting("Sensitivity", 5.0f, 1.0f, 10.0f).step(1.0f));
    private final ChunkAreas chunkAreas = new ChunkAreas();
    private final VisibleChunkCache visibleChunks = new VisibleChunkCache(16);
    private final BooleanSetting sound = this.addSetting("sound", new BooleanSetting("Sound", true));
    private final BooleanSetting showCoordinates = this.addSetting("show-coordinates", new BooleanSetting("Show Coordinates", true));
    private final BooleanSetting disconnect = this.addSetting("disconnect-on-spotted", new BooleanSetting("Auto Disconnect", false));
    private final BooleanSetting autoRtp = this.addSetting("auto-rtp", new BooleanSetting("Auto RTP", false));
    private final StringSetting rtpCommand = this.addSetting("rtp-command", new StringSetting("RTP Command", "rtp").hint("Without the slash, e.g. rtp"));
    private final NumberSetting rtpScanTime = this.addSetting("rtp-scan-time", new NumberSetting("Scan Time (s)", 5.0f, 2.0f, 30.0f).step(0.5f));
    private final NumberSetting rtpDelay = this.addSetting("rtp-delay", new NumberSetting("RTP Delay (s)", 6.0f, 3.0f, 120.0f).step(0.5f));
    private final NumberSetting rtpTimeout = this.addSetting("rtp-max-timeout", new NumberSetting("RTP Timeout (s)", 10.0f, 3.0f, 60.0f).step(1.0f));
    private final NumberSetting rtpMax = this.addSetting("rtp-max", new NumberSetting("Max RTPs (0 = unlimited)", 100.0f, 0.0f, 500.0f).step(1.0f));
    private final BooleanSetting rtpStopOnSuspect = this.addSetting("rtp-stop-on-suspect", new BooleanSetting("Stop On Suspect", false));
    private final StringSetting rtpMenuItem = this.addSetting("rtp-menu-item", new StringSetting("Menu Item", "").hint("Clicked if the command opens a menu, e.g. overworld"));
    private final PlayerBypassAutoRtp autoRtpRunner = new PlayerBypassAutoRtp(this, this.autoRtp, this.rtpCommand, this.rtpScanTime, this.rtpDelay, this.rtpTimeout, this.rtpMax, this.rtpStopOnSuspect, this.rtpMenuItem, this.sound, this.showCoordinates);
    private final LongOpenHashSet peaks = new LongOpenHashSet();
    private final Map<class_5321<class_1937>, LongLinkedOpenHashSet> foundByDimension = new HashMap<class_5321<class_1937>, LongLinkedOpenHashSet>();
    private LongLinkedOpenHashSet found = new LongLinkedOpenHashSet();
    private int session = -1;
    private final LongOpenHashSet shown = new LongOpenHashSet();
    private final Long2FloatOpenHashMap field = new Long2FloatOpenHashMap();
    private final Long2FloatOpenHashMap raw = new Long2FloatOpenHashMap();
    private int ticks;

    public PlayerBypassModule() {
        super("player-bypass", "Player Bypass", "Finds hidden players from light data and from chunks that keep ticking outside your simulation distance.", ModuleCategory.BASE_RAID);
        Setting[] rtpSettings;
        BooleanSetting notifications = this.usesNotifications();
        this.sound.visibleWhen(() -> notifications.get() || this.autoRtp.get());
        this.showCoordinates.visibleWhen(() -> notifications.get() || this.disconnect.get() || this.autoRtp.get());
        this.autoRtp.sessionOnly();
        for (Setting setting : rtpSettings = new Setting[]{this.rtpCommand, this.rtpScanTime, this.rtpDelay, this.rtpTimeout, this.rtpMax, this.rtpStopOnSuspect, this.rtpMenuItem}) {
            setting.visibleWhen(this.autoRtp::get);
        }
        this.autoRtp.onChange(() -> {
            if (!this.isEnabled()) {
                return;
            }
            if (this.autoRtp.get()) {
                this.autoRtpRunner.start();
            } else {
                this.autoRtpRunner.stopByUser();
            }
        });
    }

    @Override
    protected void onEnable() {
        this.visibleChunks.invalidate();
        PlayerBypassLightTracker.setSpottedListener(this::onChunkSpotted);
        if (this.autoRtp.get()) {
            this.autoRtpRunner.start();
        }
        this.foundByDimension.clear();
        this.found = new LongLinkedOpenHashSet();
        this.session = SimulationSignals.session();
        SimulationSignals.clear();
        this.configure();
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
    }

    @Override
    protected void onDisable() {
        this.visibleChunks.invalidate();
        PlayerBypassLightTracker.setSpottedListener(null);
        this.autoRtpRunner.stopByUser();
        this.autoRtpRunner.forgetCooldown();
        this.autoRtp.set(false);
        SimulationSignals.configure(false, 0, 0, true, 30);
        this.peaks.clear();
        this.foundByDimension.clear();
        this.found = new LongLinkedOpenHashSet();
        this.shown.clear();
        this.field.clear();
        this.raw.clear();
    }

    private void onChunkSpotted(int chunkX, int chunkZ) {
        this.spotted((chunkX << 4) + 8, (chunkZ << 4) + 8, new class_1799((class_1935)class_1802.field_8575));
    }

    private void spotted(int x, int z, class_1799 icon) {
        class_634 connection;
        class_310 mc = class_310.method_1551();
        boolean showPos = this.showCoordinates.get();
        if (!this.autoRtpRunner.baseSpotted(x, z, this.disconnect.get()) && this.notificationsOn()) {
            ChallengeToast.addOrUpdate(icon, "Player Bypass", (class_2561)(showPos ? class_2561.method_43470((String)("at " + x + ", " + z)) : class_2561.method_43473()), this.sound.get());
        }
        if (this.disconnect.get() && (connection = mc.method_1562()) != null && connection.method_48296().method_10758()) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Auto Disconnect ").warn("triggered"));
            AutoReconnectModule.holdOff();
            connection.method_48296().method_10747((class_2561)class_2561.method_43470((String)("[Player Bypass] Player detected" + (String)(showPos ? " at " + x + ", " + z : ""))));
        }
    }

    private void configure() {
        SimulationSignals.configure(true, 0, 0, true, 45);
    }

    private static boolean netherOrEnd(class_1937 level) {
        class_5321 dimension = level.method_27983();
        return dimension.equals(class_1937.field_25180) || dimension.equals(class_1937.field_25181) || level.method_40134().method_40225(class_7134.field_37667) || level.method_40134().method_40225(class_7134.field_37668) || !level.method_8597().comp_642();
    }

    private float threshold() {
        return 8.0f * (float)Math.pow(2.0, (5.0 - (double)this.sensitivity.get()) / 2.5);
    }

    private void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (this.session != SimulationSignals.session()) {
            this.session = SimulationSignals.session();
            this.foundByDimension.clear();
            this.peaks.clear();
            this.shown.clear();
        }
        this.found = this.foundByDimension.computeIfAbsent((class_5321<class_1937>)mc.field_1687.method_27983(), key -> new LongLinkedOpenHashSet());
        this.configure();
        SimulationSignals.tick();
        if (PlayerBypassModule.netherOrEnd((class_1937)mc.field_1687)) {
            SimulationSignals.dropPins();
            this.shown.clear();
            return;
        }
        this.drainPins();
        if (++this.ticks % 5 != 0) {
            return;
        }
        this.collect(mc.field_1724);
        this.alert();
        this.refreshShown(mc.field_1724);
    }

    private void collect(class_746 self) {
        this.peaks.clear();
        this.field.clear();
        this.raw.clear();
        long now = SimulationSignals.now();
        int sim = SimulationSignals.simulation();
        int pcx = self.method_24515().method_10263() >> 4;
        int pcz = self.method_24515().method_10260() >> 4;
        int start = SimulationSignals.ringStart();
        int view = SimulationSignals.view();
        int side = 2 * sim + 1;
        float[] weights = new float[side * side];
        float span = (float)sim * 1.4143f + 1.0f;
        for (int dx = -sim; dx <= sim; ++dx) {
            for (int dz = -sim; dz <= sim; ++dz) {
                weights[(dx + sim) * side + dz + sim] = 1.0f - 0.3f * (float)Math.sqrt(dx * dx + dz * dz) / span;
            }
        }
        for (Long2ObjectMap.Entry entry : SimulationSignals.chunks().long2ObjectEntrySet()) {
            float score;
            int cz;
            long key = entry.getLongKey();
            int cx = class_1923.method_8325((long)key);
            int d = SimulationSignals.distance(cx, cz = class_1923.method_8332((long)key), pcx, pcz);
            if (d < start || d > view || (score = ((SimulationSignals.ChunkSignal)entry.getValue()).score(now)) < 0.5f) continue;
            for (int dx = -sim; dx <= sim; ++dx) {
                for (int dz = -sim; dz <= sim; ++dz) {
                    long area = class_1923.method_8331((int)(cx + dx), (int)(cz + dz));
                    this.field.addTo(area, score * weights[(dx + sim) * side + dz + sim]);
                    this.raw.addTo(area, score);
                }
            }
        }
        ObjectIterator dx = SimulationSignals.frozen().iterator();
        while (dx.hasNext()) {
            long frozen = (Long)dx.next();
            this.field.remove(frozen);
            this.raw.remove(frozen);
            int fx = class_1923.method_8325((long)frozen);
            int fz = class_1923.method_8332((long)frozen);
            for (int dx2 = -1; dx2 <= 1; ++dx2) {
                for (int dz = -1; dz <= 1; ++dz) {
                    long near = class_1923.method_8331((int)(fx + dx2), (int)(fz + dz));
                    if (!this.field.containsKey(near)) continue;
                    this.field.put(near, this.field.get(near) * 0.5f);
                    this.raw.put(near, this.raw.get(near) * 0.5f);
                }
            }
        }
        float threshold = this.threshold();
        for (Long2FloatMap.Entry entry : this.field.long2FloatEntrySet()) {
            long key = entry.getLongKey();
            if (this.raw.get(key) < threshold || !this.isPeak(key, entry.getFloatValue(), sim)) continue;
            this.peaks.add(key);
        }
    }

    private boolean isPeak(long key, float value, int reach) {
        int cx = class_1923.method_8325((long)key);
        int cz = class_1923.method_8332((long)key);
        for (int dx = -reach; dx <= reach; ++dx) {
            for (int dz = -reach; dz <= reach; ++dz) {
                long other;
                float near;
                if (dx == 0 && dz == 0 || !((near = this.field.get(other = class_1923.method_8331((int)(cx + dx), (int)(cz + dz)))) > value) && (near != value || other >= key)) continue;
                return false;
            }
        }
        return true;
    }

    private void drainPins() {
        SimulationSignals.Pin pin;
        while ((pin = SimulationSignals.pollPin()) != null) {
            class_2338 pos = pin.pos();
            long key = class_1923.method_8331((int)(pos.method_10263() >> 4), (int)(pos.method_10260() >> 4));
            LOGGER.info("[Player Bypass] {}", (Object)pin.detail());
            if (this.near(key, 1)) continue;
            this.remember(key);
            ModuleChat.send((ClientModule)this, pin.detail());
            this.spotted(pos.method_10263(), pos.method_10260(), GlintHeadRenderer.stack());
        }
    }

    private void alert() {
        long now = SimulationSignals.now();
        int spacing = Math.max(4, 2 * SimulationSignals.simulation());
        LongIterator longIterator = this.peaks.iterator();
        while (longIterator.hasNext()) {
            long key = (Long)longIterator.next();
            if (this.near(key, spacing)) continue;
            this.remember(key);
            int x = (class_1923.method_8325((long)key) << 4) + 8;
            int z = (class_1923.method_8332((long)key) << 4) + 8;
            this.report(key, x, z, now);
            this.spotted(x, z, GlintHeadRenderer.stack());
        }
    }

    private boolean near(long key, int spacing) {
        LongListIterator longListIterator = this.found.iterator();
        while (longListIterator.hasNext()) {
            long old = (Long)longListIterator.next();
            if (SimulationSignals.distance(class_1923.method_8325((long)old), class_1923.method_8332((long)old), class_1923.method_8325((long)key), class_1923.method_8332((long)key)) > spacing) continue;
            return true;
        }
        return false;
    }

    private void remember(long key) {
        this.found.add(key);
        while (this.found.size() > 256) {
            this.found.removeFirstLong();
        }
    }

    private void refreshShown(class_746 self) {
        this.shown.clear();
        int pcx = self.method_24515().method_10263() >> 4;
        int pcz = self.method_24515().method_10260() >> 4;
        int reach = SimulationSignals.view() + SimulationSignals.simulation() + 2;
        LongListIterator longListIterator = this.found.iterator();
        while (longListIterator.hasNext()) {
            long key = (Long)longListIterator.next();
            if (SimulationSignals.distance(class_1923.method_8325((long)key), class_1923.method_8332((long)key), pcx, pcz) > reach) continue;
            this.shown.add(key);
        }
    }

    private void report(long found, int x, int z, long now) {
        int sim = SimulationSignals.simulation();
        int fx = class_1923.method_8325((long)found);
        int fz = class_1923.method_8332((long)found);
        ArrayList<Long2ObjectMap.Entry> sources = new ArrayList<Long2ObjectMap.Entry>();
        for (Long2ObjectMap.Entry entry : SimulationSignals.chunks().long2ObjectEntrySet()) {
            long l = entry.getLongKey();
            if (SimulationSignals.distance(class_1923.method_8325((long)l), class_1923.method_8332((long)l), fx, fz) > sim || ((SimulationSignals.ChunkSignal)entry.getValue()).score(now) < 0.5f) continue;
            sources.add(entry);
        }
        sources.sort((a, b) -> Float.compare(((SimulationSignals.ChunkSignal)b.getValue()).score(now), ((SimulationSignals.ChunkSignal)a.getValue()).score(now)));
        ArrayList<CallSite> lines = new ArrayList<CallSite>();
        lines.add((CallSite)((Object)("Found " + x + ", " + z + " from " + sources.size() + " chunks (sim " + sim + ", ring " + SimulationSignals.ringStart() + "-" + SimulationSignals.view() + ")")));
        for (int i = 0; i < Math.min(4, sources.size()); ++i) {
            SimulationSignals.ChunkSignal chunkSignal = (SimulationSignals.ChunkSignal)((Long2ObjectMap.Entry)sources.get(i)).getValue();
            long key = ((Long2ObjectMap.Entry)sources.get(i)).getLongKey();
            StringBuilder kinds = new StringBuilder();
            for (SimulationSignals.Kind kind : SimulationSignals.Kind.values()) {
                if (chunkSignal.count(kind) <= 0) continue;
                kinds.append(kinds.isEmpty() ? "" : ", ").append(kind.title).append(" x").append(chunkSignal.count(kind));
            }
            lines.add((CallSite)((Object)(" chunk " + ((class_1923.method_8325((long)key) << 4) + 8) + ", " + ((class_1923.method_8332((long)key) << 4) + 8) + String.format(Locale.ROOT, " score %.1f: ", Float.valueOf(chunkSignal.score(now))) + String.valueOf(kinds))));
            for (String recent : chunkSignal.recent()) {
                lines.add((CallSite)((Object)("   " + recent)));
            }
        }
        for (String string : lines) {
            LOGGER.info("[Player Bypass] {}", (Object)string);
            ModuleChat.send((ClientModule)this, string);
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        LongOpenHashSet bases = new LongOpenHashSet();
        LongOpenHashSet suspects = new LongOpenHashSet();
        Long2ObjectMap<ChunkLightSnapshot> snapshots = PlayerBypassLightTracker.snapshots();
        if (!snapshots.isEmpty()) {
            int centerX = mc.field_1724.method_24515().method_10263() >> 4;
            int centerZ = mc.field_1724.method_24515().method_10260() >> 4;
            if (PerformanceModule.optimizationsEnabled()) {
                this.visibleChunks.update(PlayerBypassLightTracker.visibilityRevision(), centerX, centerZ, snapshots, PlayerBypassLightTracker::isFlagged, PlayerBypassLightTracker::isSuspect);
                bases.addAll((LongCollection)this.visibleChunks.bases());
                suspects.addAll((LongCollection)this.visibleChunks.suspects());
            } else {
                this.visibleChunks.invalidate();
                for (Long2ObjectMap.Entry entry : snapshots.long2ObjectEntrySet()) {
                    long key = entry.getLongKey();
                    if (Math.abs(class_1923.method_8325((long)key) - centerX) > 16 || Math.abs(class_1923.method_8332((long)key) - centerZ) > 16 || !PlayerBypassLightTracker.isFlagged(key, (ChunkLightSnapshot)entry.getValue())) continue;
                    (PlayerBypassLightTracker.isSuspect(key) ? suspects : bases).add(key);
                }
            }
        }
        LongOpenHashSet activity = new LongOpenHashSet();
        if (!PlayerBypassModule.netherOrEnd((class_1937)mc.field_1687)) {
            LongIterator centerZ = this.shown.iterator();
            while (centerZ.hasNext()) {
                long key = (Long)centerZ.next();
                if (bases.contains(key) || suspects.contains(key)) continue;
                activity.add(key);
            }
        }
        if (bases.isEmpty() && suspects.isEmpty() && activity.isEmpty()) {
            return;
        }
        FoglessLines lines = FoglessLines.forFrame(event.partialTick);
        double bottom = mc.field_1687.method_31607();
        double top = mc.field_1687.method_31600() + 1;
        this.mark(lines, suspects, -5766, bottom, top);
        this.mark(lines, activity, -11205, bottom, top);
        this.mark(lines, bases, -2516480, bottom, top);
        if (this.tracers.get()) {
            this.chunkAreas.drawTracers((LongSet)bases, 64.0, class_9848.method_61334((int)-2516480));
            this.chunkAreas.drawTracers((LongSet)suspects, 64.0, class_9848.method_61334((int)-5766));
            this.chunkAreas.drawTracers((LongSet)activity, 64.0, class_9848.method_61334((int)-11205));
        }
    }

    private void mark(FoglessLines lines, LongOpenHashSet chunks, int color, double bottom, double top) {
        if (chunks.isEmpty()) {
            return;
        }
        float strength = (float)Math.max(10, Math.min(255, this.opacity.getInt())) / 255.0f;
        class_12179 fill = class_12179.method_75539((int)class_9848.method_61330((int)Math.round(34.0f * strength), (int)color));
        int edge = class_9848.method_61330((int)Math.round(255.0f * strength), (int)color);
        LongIterator longIterator = chunks.iterator();
        while (longIterator.hasNext()) {
            long key = (Long)longIterator.next();
            double x0 = class_1923.method_8325((long)key) << 4;
            double z0 = class_1923.method_8332((long)key) << 4;
            double x1 = x0 + 16.0;
            double z1 = z0 + 16.0;
            class_12180.method_75541((class_238)new class_238(x0, bottom, z0, x1, top, z1), (class_12179)fill).method_75533();
            PlayerBypassModule.column(lines, x0, z0, x1, z1, bottom, top, edge);
        }
    }

    private static void column(FoglessLines lines, double x0, double z0, double x1, double z1, double bottom, double top, int color) {
        double[][] corners = new double[][]{{x0, z0}, {x1, z0}, {x1, z1}, {x0, z1}};
        for (int i = 0; i < corners.length; ++i) {
            double[] a = corners[i];
            double[] b = corners[(i + 1) % corners.length];
            lines.line(new class_243(a[0], bottom, a[1]), new class_243(a[0], top, a[1]), color, 2.0f);
            lines.line(new class_243(a[0], bottom, a[1]), new class_243(b[0], bottom, b[1]), color, 2.0f);
            lines.line(new class_243(a[0], top, a[1]), new class_243(b[0], top, b[1]), color, 2.0f);
        }
    }
}

