/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1297
 *  net.minecraft.class_1934
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_268
 *  net.minecraft.class_2703
 *  net.minecraft.class_2703$class_2705
 *  net.minecraft.class_2703$class_5893
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_6880
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 *  net.minecraft.class_8042
 */
package net.anubis.client.module.impl.misc;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.StaffGhost;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.util.NameFilter;
import net.anubis.client.util.staff.ShardTracker;
import net.anubis.client.util.staff.StaffAlarm;
import net.anubis.client.util.staff.StaffMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1297;
import net.minecraft.class_1934;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_268;
import net.minecraft.class_2703;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_6880;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_8042;

@Environment(value=EnvType.CLIENT)
public final class StaffListModule
extends ClientModule {
    public static final List<String> DONUT_STAFF = List.of("FluffyMaster07", "archivePedro", "Munkerlich", "Frenk_Btw", "Napooo_", "auzzitech", "CryptoDaveYT", "W1zox_", "ZEEF69", "showered", "CaptainMoose35", "BobIsFound", "NoahvdAa", "0GSummer", "LzouzMP5", "Pastagamer08", "u_vv", "Owen1212055", "Splaterd", "Fallerfly", "Bloodspulse", "_chaon", "Dough4", "evify");
    private static final Set<String> STAFF_KEYS = StaffListModule.staffKeys();
    private static final StaffMatcher RANKS = new StaffMatcher("Ranks + Markers + Team", "owner, co-owner, manager, admin, administrator, developer, dev, sr-admin, sr-mod, senior-mod, moderator, mod, trial-mod, sr-helper, senior-helper, helper, trial-helper, support, staff", "U+2605 U+2606 U+2726 U+2727 U+272A U+272F U+2730 U+2B50", false, 5, String.join((CharSequence)",", DONUT_STAFF), "");
    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int SETTLE_TICKS = 60;
    private static final int VANISH_CONFIRM_SCANS = 2;
    private static final int GROUP_THRESHOLD = 3;
    private static final int ALERT_GATHER_TICKS = 5;
    private static final int SWITCH_ECHO_TICKS = 20;
    public static final long ALERT_HIGHLIGHT_MS = 4000L;
    private static final long ALERT_COOLDOWN_MS = 120000L;
    private static final long SERVER_MARK_MS = 600000L;
    private static final int MAX_ROWS = 8;
    private static final float ALARM_SECONDS = 3.0f;
    private static final long SHARED_ALARM_MS = 5000L;
    private static final long NEARBY_GRACE_MS = 10000L;
    private static final long SIGHTING_GRACE_MS = 10000L;
    private static final long WATCH_GRACE_MS = 10000L;
    private static final Pattern ACCOUNT_NAME = Pattern.compile("[.*]?[A-Za-z0-9_]{1,16}");
    private final BooleanSetting regionAlert = this.addSetting("server-alert", new BooleanSetting("Region Alert", true));
    private final Map<UUID, Staff> staff = new LinkedHashMap<UUID, Staff>();
    private List<Staff> sorted = List.of();
    private boolean resort;
    private final Map<UUID, Classified> classified = new HashMap<UUID, Classified>();
    private final Map<UUID, Sighting> sightings = new HashMap<UUID, Sighting>();
    private final Queue<GameModeSwitch> switches = new ConcurrentLinkedQueue<GameModeSwitch>();
    private final Map<UUID, String> pendingAlerts = new LinkedHashMap<UUID, String>();
    private final StaffAlarm alarm = new StaffAlarm();
    private class_634 connection;
    private final ShardTracker shard = new ShardTracker();
    private int ticksToScan;
    private int settleTicks;
    private boolean primed;
    private int alertGather;
    private int tickCount;

    public StaffListModule() {
        super("staff-list", "Staff List", "Lists online DonutSMP staff.", ModuleCategory.DONUT);
        this.usesNotifications();
        this.regionAlert.onChange(() -> {
            if (!this.regionAlert.get()) {
                this.forgetShard();
            }
        });
    }

    @Override
    protected void onEnable() {
        this.forget();
        class_310 mc = class_310.method_1551();
        this.connection = mc.method_1562();
        this.shard.clear();
        this.settleTicks = 0;
        this.ticksToScan = 0;
        this.primed = false;
        this.on(TickEvent.TYPE, Stage.POST, this::onTick);
        this.on(PacketReceiveNettyEvent.TYPE, this::onPacketNetty);
    }

    @Override
    protected void onDisable() {
        this.forget();
        this.connection = null;
        this.shard.clear();
    }

    private void forget() {
        this.staff.clear();
        this.sorted = List.of();
        this.classified.clear();
        this.forgetShard();
    }

    private void forgetShard() {
        this.switches.clear();
        this.sightings.clear();
        this.pendingAlerts.clear();
        this.alarm.stop();
        this.resort = true;
    }

    public static boolean shouldShowHud() {
        StaffListModule module = StaffListModule.instance();
        return module != null && module.isEnabled() && !module.sorted.isEmpty();
    }

    public static HudList hudRows() {
        StaffListModule module = StaffListModule.instance();
        if (module == null || !module.isEnabled()) {
            return HudList.EMPTY;
        }
        List<Staff> all = module.sorted;
        int shown = all.size() <= 8 ? all.size() : 7;
        long now = System.currentTimeMillis();
        ArrayList<HudRow> rows = new ArrayList<HudRow>(shown);
        for (int i = 0; i < shown; ++i) {
            Staff s = all.get(i);
            Sighting seen = module.sightings.get(s.id);
            boolean watching = seen != null && seen.watching(now);
            boolean nearby = seen != null && seen.nearby;
            float alert = seen == null ? 0.0f : Math.max(0.0f, Math.min(1.0f, (float)(seen.highlightUntil - now) / 4000.0f));
            String shownName = NameFilter.apply(s.name);
            rows.add(new HudRow(shownName, s.id, s.presence, watching, watching ? Math.max(0L, now - seen.watchingSince) : -1L, nearby, nearby ? seen.distance : -1.0f, alert, !shownName.equals(s.name)));
        }
        return new HudList(rows, all.size() - shown);
    }

    public static String formatAge(long ms) {
        long total = Math.max(0L, ms) / 1000L;
        long hours = total / 3600L;
        long minutes = total / 60L % 60L;
        long seconds = total % 60L;
        StringBuilder out = new StringBuilder(8);
        if (hours > 0L) {
            out.append(hours).append(minutes < 10L ? ":0" : ":");
        }
        return out.append(minutes).append(seconds < 10L ? ":0" : ":").append(seconds).toString();
    }

    public static boolean isStaffName(String name) {
        return name != null && STAFF_KEYS.contains(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isInYourRegion(UUID id) {
        StaffListModule module = StaffListModule.instance();
        if (module == null || !module.isEnabled() || id == null) {
            return false;
        }
        Sighting seen = module.sightings.get(id);
        return seen != null && seen.marked(System.currentTimeMillis());
    }

    public static void provenHere(UUID id, String name) {
        StaffListModule module = StaffListModule.instance();
        if (module == null || !module.isEnabled() || !module.regionAlert.get() || id == null || name == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Sighting seen = module.sightings.computeIfAbsent(id, key -> new Sighting());
        if (!seen.marked(now)) {
            module.resort = true;
        }
        seen.prove(now);
        if (!seen.coolingDown(now)) {
            module.alert(id, seen, now, name);
        }
    }

    public static boolean alarmedRecently(UUID id) {
        StaffListModule module = StaffListModule.instance();
        if (module == null || !module.isEnabled() || id == null) {
            return false;
        }
        Sighting seen = module.sightings.get(id);
        return seen != null && seen.alertedAt != 0L && System.currentTimeMillis() - seen.alertedAt < 5000L;
    }

    private static Set<String> staffKeys() {
        HashSet<String> keys = new HashSet<String>();
        for (String name : DONUT_STAFF) {
            keys.add(name.toLowerCase(Locale.ROOT));
        }
        return Set.copyOf(keys);
    }

    private static StaffListModule instance() {
        return ModuleRegistry.get(StaffListModule.class);
    }

    private void onPacketNetty(PacketReceiveNettyEvent event) {
        if (!this.regionAlert.get()) {
            return;
        }
        Object object = event.packet;
        if (object instanceof class_2703) {
            class_2703 info = (class_2703)object;
            this.queueSwitches(info);
        } else {
            object = event.packet;
            if (object instanceof class_8042) {
                class_8042 bundle = (class_8042)object;
                for (class_2596 packet : bundle.method_48324()) {
                    if (!(packet instanceof class_2703)) continue;
                    class_2703 info = (class_2703)packet;
                    this.queueSwitches(info);
                }
            }
        }
    }

    private void queueSwitches(class_2703 info) {
        EnumSet actions = info.method_46327();
        if (!actions.contains(class_2703.class_5893.field_29137) || actions.contains(class_2703.class_5893.field_29136)) {
            return;
        }
        for (class_2703.class_2705 entry : info.method_46329()) {
            if (entry == null || entry.comp_1106() == null) continue;
            this.switches.offer(new GameModeSwitch(entry.comp_1106(), entry.comp_1110()));
        }
    }

    private void onTick(TickEvent event) {
        this.alarm.tick();
        class_310 mc = class_310.method_1551();
        class_634 conn = mc.method_1562();
        if (conn == null || mc.field_1724 == null || mc.field_1687 == null) {
            if (!(this.connection == null && this.staff.isEmpty() && this.classified.isEmpty() && this.sightings.isEmpty())) {
                this.forget();
            }
            this.switches.clear();
            this.connection = null;
            this.shard.clear();
            this.primed = false;
            return;
        }
        boolean moved = this.shard.moved(mc);
        if (conn != this.connection) {
            this.connection = conn;
            this.forget();
            this.settleTicks = 60;
            this.primed = false;
        } else if (moved) {
            this.forgetShard();
        }
        if (this.settleTicks > 0) {
            --this.settleTicks;
        }
        ++this.tickCount;
        long now = System.currentTimeMillis();
        if (this.regionAlert.get()) {
            this.drainSwitches(conn, mc.field_1724.method_5667(), now);
            this.trackBodies(mc, now);
        }
        if (--this.ticksToScan <= 0) {
            this.ticksToScan = 10;
            Changes changes = this.scan(mc, conn, now);
            if (this.primed) {
                this.announce(mc, changes);
            } else if (this.settleTicks == 0) {
                this.primed = true;
            }
        }
        if (!this.pendingAlerts.isEmpty() && --this.alertGather <= 0) {
            this.raise();
        }
        if (this.resort) {
            this.sortView(now);
        }
    }

    private Changes scan(class_310 mc, class_634 conn, long now) {
        UUID self = mc.field_1724.method_5667();
        HashSet<UUID> listed = new HashSet<UUID>();
        for (class_640 info : conn.method_45732()) {
            listed.add(info.method_2966().id());
        }
        Changes changes = new Changes();
        HashSet<UUID> inTab = new HashSet<UUID>();
        for (class_640 info : conn.method_2880()) {
            GameProfile profile = info.method_2966();
            UUID id = profile.id();
            String name = profile.name();
            if (id == null || id.equals(self) || !StaffListModule.isStaffName(name) || !StaffListModule.isAccount(id, name)) continue;
            inTab.add(id);
            StaffMatcher.Match rank = this.rank(info, id, name);
            Presence presence = info.method_2958() == class_1934.field_9219 ? Presence.SPECTATOR : (listed.contains(id) ? Presence.ONLINE : Presence.VANISHED);
            Staff tracked = this.staff.get(id);
            if (tracked == null) {
                tracked = new Staff(id);
                this.staff.put(id, tracked);
                tracked.update(name, rank, presence);
                changes.joined.add(tracked);
                continue;
            }
            Presence before = tracked.presence;
            tracked.update(name, rank, presence);
            tracked.offTabScans = 0;
            if (before == presence) continue;
            changes.changed.add(tracked);
        }
        this.classified.keySet().retainAll(inTab);
        Iterator<Staff> it = this.staff.values().iterator();
        while (it.hasNext()) {
            Staff tracked = it.next();
            if (inTab.contains(tracked.id)) continue;
            if (mc.field_1687.method_18470(tracked.id) != null) {
                if (++tracked.offTabScans < 2 || tracked.presence == Presence.VANISHED) continue;
                tracked.presence = Presence.VANISHED;
                changes.changed.add(tracked);
                continue;
            }
            it.remove();
            changes.left.add(tracked);
        }
        this.pruneSightings(now);
        this.sortView(now);
        return changes;
    }

    private void sortView(long now) {
        ArrayList<Staff> view = new ArrayList<Staff>(this.staff.values());
        view.sort(Comparator.comparingInt(s -> this.group((Staff)s, now)).thenComparingInt(s -> s.seniority).thenComparing(s -> s.name, String.CASE_INSENSITIVE_ORDER));
        this.sorted = List.copyOf(view);
        this.resort = false;
    }

    private int group(Staff s, long now) {
        Sighting seen = this.sightings.get(s.id);
        if (seen != null && seen.nearby) {
            return 0;
        }
        if (seen != null && seen.marked(now)) {
            return 1;
        }
        return s.presence != Presence.ONLINE ? 2 : 3;
    }

    private static boolean isAccount(UUID id, String name) {
        return name != null && id.version() != 2 && ACCOUNT_NAME.matcher(name).matches();
    }

    private StaffMatcher.Match rank(class_640 info, UUID id, String name) {
        class_268 team = info.method_2955();
        class_2561 display = info.method_2971();
        class_2561 prefix = team == null ? null : team.method_1144();
        class_2561 suffix = team == null ? null : team.method_1136();
        Classified last = this.classified.get(id);
        if (last != null && last.isFor(name, display, team, prefix, suffix)) {
            return last.match();
        }
        StaffMatcher.Match match = RANKS.match(name, team == null ? null : team.method_1197(), StaffListModule.plain(display), StaffListModule.plain(prefix), StaffListModule.plain(suffix));
        this.classified.put(id, new Classified(name, display, team, prefix, suffix, match));
        return match;
    }

    private static String plain(class_2561 component) {
        return component == null ? null : component.getString();
    }

    private void drainSwitches(class_634 conn, UUID self, long now) {
        GameModeSwitch change;
        while ((change = this.switches.poll()) != null) {
            String name;
            UUID id = change.id();
            if (id.equals(self)) continue;
            Staff tracked = this.staff.get(id);
            if (tracked != null) {
                name = tracked.name;
            } else {
                class_640 info = conn.method_2871(id);
                name = info == null ? null : info.method_2966().name();
                if (!StaffListModule.isStaffName(name) || !StaffListModule.isAccount(id, name)) continue;
            }
            Sighting seen = this.sightings.computeIfAbsent(id, key -> new Sighting());
            if (!seen.marked(now)) {
                this.resort = true;
            }
            seen.prove(now);
            if (seen.coolingDown(now)) continue;
            class_1934 mode = change.mode();
            seen.switchAlertTick = this.tickCount;
            seen.switchedToSpectator = mode == class_1934.field_9219;
            this.alert(id, seen, now, name);
        }
    }

    private void trackBodies(class_310 mc, long now) {
        class_746 self = mc.field_1724;
        for (class_742 body : mc.field_1687.method_18456()) {
            Staff tracked;
            if (body == self || (tracked = this.staff.get(body.method_5667())) == null) continue;
            Sighting seen = this.sightings.computeIfAbsent(tracked.id, key -> new Sighting());
            seen.prove(now);
            seen.bodyTick = this.tickCount;
            seen.distance = self.method_5739((class_1297)body);
            if (seen.nearby) continue;
            seen.nearby = true;
            this.resort = true;
            boolean newAppearance = seen.bodyLostAt == 0L || now - seen.bodyLostAt > 10000L;
            if (!newAppearance) continue;
            this.alert(tracked.id, seen, now, tracked.name);
        }
        for (Sighting seen : this.sightings.values()) {
            if (!seen.nearby || seen.bodyTick == this.tickCount) continue;
            seen.nearby = false;
            seen.bodyLostAt = now;
            this.resort = true;
        }
    }

    private void pruneSightings(long now) {
        Iterator<Map.Entry<UUID, Sighting>> it = this.sightings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Sighting> entry = it.next();
            Sighting seen = entry.getValue();
            if (this.staff.containsKey(entry.getKey())) {
                seen.untrackedSince = 0L;
                continue;
            }
            if (seen.untrackedSince == 0L) {
                seen.untrackedSince = now;
                continue;
            }
            if (now - seen.untrackedSince <= 10000L) continue;
            it.remove();
            this.resort = true;
        }
    }

    private void alert(UUID id, Sighting seen, long now, String name) {
        seen.alertedAt = now;
        seen.highlightUntil = now + 4000L;
        if (this.pendingAlerts.isEmpty()) {
            this.alertGather = 5;
        }
        this.pendingAlerts.put(id, name);
    }

    private void raise() {
        Map.Entry<UUID, String> first = this.pendingAlerts.entrySet().iterator().next();
        UUID id = first.getKey();
        String name = first.getValue();
        int others = this.pendingAlerts.size() - 1;
        this.pendingAlerts.clear();
        this.alarm.start("Guardian", 3.0f, 1.0f);
        StaffGhost.watching(id, name, others);
    }

    private void announce(class_310 mc, Changes changes) {
        boolean urgent;
        changes.changed.removeIf(this::echoesSwitchAlert);
        if (changes.isEmpty()) {
            return;
        }
        ArrayList<ModuleChat.Body> lines = new ArrayList<ModuleChat.Body>();
        StaffListModule.describe(lines, changes.joined, ModuleChat.body().warn("joined").build(), (line, s) -> {
            line.warn("joined");
            String note = StaffListModule.note(s);
            if (note != null) {
                line.text(" ").danger(note);
            }
        });
        StaffListModule.describe(lines, changes.changed, ModuleChat.body().warn("changed visibility").build(), (line, s) -> {
            switch (s.presence.ordinal()) {
                case 2: {
                    line.danger("vanished").text(s.offTabScans > 0 ? " (off TAB, still nearby)" : " (hidden from TAB)");
                    break;
                }
                case 1: {
                    StaffListModule.switchedTo(line, class_1934.field_9219.method_8381());
                    break;
                }
                case 0: {
                    line.text("is ").good("visible again");
                }
            }
        });
        StaffListModule.describe(lines, changes.left, ModuleChat.body().good("left").build(), (line, s) -> {
            if (s.presence == Presence.VANISHED) {
                line.text("is ").warn("out of sight");
            } else {
                line.good("left");
            }
        });
        for (ModuleChat.Body line2 : lines) {
            ModuleChat.send((ClientModule)this, line2);
        }
        boolean bl = urgent = !changes.joined.isEmpty() || changes.changed.stream().anyMatch(s -> s.presence != Presence.ONLINE);
        if (urgent && mc.method_1483() != null) {
            mc.method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_14725, (float)1.25f));
        }
    }

    private boolean echoesSwitchAlert(Staff s) {
        Sighting seen = this.sightings.get(s.id);
        if (seen == null || seen.switchAlertTick < 0 || this.tickCount - seen.switchAlertTick > 20) {
            return false;
        }
        return s.presence == Presence.SPECTATOR ? seen.switchedToSpectator : s.presence == Presence.ONLINE && !seen.switchedToSpectator;
    }

    private static void describe(List<ModuleChat.Body> lines, List<Staff> group, class_2561 groupVerb, BiConsumer<ModuleChat.Body, Staff> verb) {
        if (group.isEmpty()) {
            return;
        }
        if (group.size() > 3) {
            ArrayList<class_2561> labels = new ArrayList<class_2561>(group.size());
            for (Staff s : group) {
                labels.add(StaffListModule.label(s));
            }
            lines.add(StaffListModule.list(ModuleChat.body().value(group.size()).text(" staff ").append(groupVerb).text(": "), labels));
            return;
        }
        for (Staff s : group) {
            ModuleChat.Body line = ModuleChat.body().append(StaffListModule.label(s)).text(" ");
            verb.accept(line, s);
            lines.add(line);
        }
    }

    private static class_2561 label(Staff s) {
        return StaffListModule.label(s.name, s.rank);
    }

    private static class_2561 label(String name, String rank) {
        return ModuleChat.body().name(NameFilter.apply(name)).text(" (" + rank + ")").build();
    }

    private static String note(Staff s) {
        return switch (s.presence.ordinal()) {
            default -> throw new MatchException(null, null);
            case 2 -> "vanished";
            case 1 -> "spectating";
            case 0 -> null;
        };
    }

    private static ModuleChat.Body list(ModuleChat.Body body, List<class_2561> parts) {
        for (int i = 0; i < parts.size(); ++i) {
            if (i > 0) {
                body.text(", ");
            }
            body.append(parts.get(i));
        }
        return body;
    }

    static ModuleChat.Body switchedTo(ModuleChat.Body body, String mode) {
        if (mode == null) {
            return body.text("switched game mode");
        }
        body.text("switched to ");
        if (class_1934.field_9219.method_8381().equals(mode)) {
            return body.danger(mode);
        }
        if (class_1934.field_9220.method_8381().equals(mode)) {
            return body.warn(mode);
        }
        return body.name(mode);
    }

    @Environment(value=EnvType.CLIENT)
    public record HudList(List<HudRow> rows, int more) {
        static final HudList EMPTY = new HudList(List.of(), 0);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Staff {
        final UUID id;
        String name = "";
        String rank = "";
        int seniority;
        Presence presence = Presence.ONLINE;
        int offTabScans;

        Staff(UUID id) {
            this.id = id;
        }

        void update(String name, StaffMatcher.Match rank, Presence presence) {
            this.name = name;
            this.rank = rank.rank();
            this.seniority = rank.seniority();
            this.presence = presence;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Sighting {
        long provenAt;
        long watchingSince;
        long alertedAt;
        long highlightUntil;
        boolean nearby;
        float distance;
        long bodyLostAt;
        int bodyTick;
        int switchAlertTick = -1;
        boolean switchedToSpectator;
        long untrackedSince;

        private Sighting() {
        }

        boolean marked(long now) {
            return this.nearby || this.provenAt != 0L && now - this.provenAt < 600000L;
        }

        void prove(long now) {
            if (!this.watching(now)) {
                this.watchingSince = now;
            }
            this.provenAt = now;
        }

        boolean watching(long now) {
            return this.nearby || this.provenAt != 0L && now - this.provenAt < 10000L;
        }

        boolean coolingDown(long now) {
            return this.alertedAt != 0L && now - this.alertedAt < 120000L;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record HudRow(String name, UUID id, Presence presence, boolean watching, long watchingForMs, boolean nearby, float distance, float alertLevel, boolean nameHidden) {
        public String watchingFor() {
            return this.watching ? StaffListModule.formatAge(this.watchingForMs) : "";
        }

        public String distanceLabel() {
            return this.nearby ? Math.round(this.distance) + "m" : "";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Presence {
        ONLINE,
        SPECTATOR,
        VANISHED;

    }

    @Environment(value=EnvType.CLIENT)
    private record GameModeSwitch(UUID id, class_1934 mode) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Changes {
        final List<Staff> joined = new ArrayList<Staff>();
        final List<Staff> changed = new ArrayList<Staff>();
        final List<Staff> left = new ArrayList<Staff>();

        private Changes() {
        }

        boolean isEmpty() {
            return this.joined.isEmpty() && this.changed.isEmpty() && this.left.isEmpty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Classified(String name, class_2561 display, class_268 team, class_2561 prefix, class_2561 suffix, StaffMatcher.Match match) {
        boolean isFor(String name, class_2561 display, class_268 team, class_2561 prefix, class_2561 suffix) {
            return this.display == display && this.team == team && this.prefix == prefix && this.suffix == suffix && this.name.equals(name);
        }
    }
}

