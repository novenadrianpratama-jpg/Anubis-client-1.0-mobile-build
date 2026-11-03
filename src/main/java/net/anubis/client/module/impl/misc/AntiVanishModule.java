/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1657
 *  net.minecraft.class_1676
 *  net.minecraft.class_1934
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2588
 *  net.minecraft.class_2596
 *  net.minecraft.class_2664
 *  net.minecraft.class_2675
 *  net.minecraft.class_268
 *  net.minecraft.class_2680
 *  net.minecraft.class_2703
 *  net.minecraft.class_2703$class_2705
 *  net.minecraft.class_2703$class_5893
 *  net.minecraft.class_2739
 *  net.minecraft.class_2741
 *  net.minecraft.class_2765
 *  net.minecraft.class_2767
 *  net.minecraft.class_2769
 *  net.minecraft.class_2824
 *  net.minecraft.class_2846
 *  net.minecraft.class_2879
 *  net.minecraft.class_2885
 *  net.minecraft.class_2886
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_5900
 *  net.minecraft.class_5900$class_5901
 *  net.minecraft.class_640
 *  net.minecraft.class_7417
 *  net.minecraft.class_7439
 *  net.minecraft.class_7828
 *  net.minecraft.class_7923
 *  net.minecraft.class_8042
 */
package net.anubis.client.module.impl.misc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.StaffGhost;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.misc.StaffListModule;
import net.anubis.client.util.NameFilter;
import net.anubis.client.util.antivanish.AntiVanishHeuristics;
import net.anubis.client.util.antivanish.AntiVanishText;
import net.anubis.client.util.staff.ShardTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1934;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2675;
import net.minecraft.class_268;
import net.minecraft.class_2680;
import net.minecraft.class_2703;
import net.minecraft.class_2739;
import net.minecraft.class_2741;
import net.minecraft.class_2765;
import net.minecraft.class_2767;
import net.minecraft.class_2769;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_5900;
import net.minecraft.class_640;
import net.minecraft.class_7417;
import net.minecraft.class_7439;
import net.minecraft.class_7828;
import net.minecraft.class_7923;
import net.minecraft.class_8042;

@Environment(value=EnvType.CLIENT)
public final class AntiVanishModule
extends ClientModule {
    private static final class_310 MC = class_310.method_1551();
    private static final int SENSOR_RANGE = 64;
    private static final long SIGNAL_WINDOW_MS = 15000L;
    private static final long DETECTION_TTL_MS = 20000L;
    private static final long CRITICAL_COOLDOWN_MS = 12000L;
    private static final long ANNOUNCE_COOLDOWN_MS = 1500L;
    private static final long ANNOUNCE_WINDOW_MS = 4000L;
    private static final int MAX_ANNOUNCE_PER_WINDOW = 6;
    private static final int MAX_OBSERVATIONS_PER_TICK = 512;
    private static final int CRITICAL_SCORE = 35;
    private static final long SELF_PLACE_TTL_MS = 6000L;
    private static final long CONTAINER_SELF_GRACE_MS = 2500L;
    private static final long SPELL_SETTLE_MS = 1000L;
    private static final long GOLIATH_QUIET_MS = 4000L;
    private static final long HINT_HOLD_MS = 1000L;
    private static final long TAB_PAIR_MS = 1000L;
    private static final long TEAM_BATCH_MS = 50L;
    private static final int TEAM_BATCH = 5;
    private static final long HINT_MEMORY_MS = 5000L;
    private static final long ARRIVAL_REPEAT_MS = 60000L;
    private static final int STAFF_RANK_MAX = 8;
    private static final Pattern RANK_TEAM = Pattern.compile("(\\d{2})-(.+)-(?:true|false)");
    private static final Set<class_2703.class_5893> ORDER_ONLY = EnumSet.of(class_2703.class_5893.field_29139, class_2703.class_5893.field_52324);
    private final ConcurrentLinkedQueue<Hint> hints = new ConcurrentLinkedQueue();
    private final List<Hint> heldHints = new ArrayList<Hint>();
    private final Deque<Hint> tabMoves = new ArrayDeque<Hint>();
    private final Deque<Long> teamChanges = new ArrayDeque<Long>();
    private final Map<UUID, String> tabNames = new HashMap<UUID, String>();
    private final Map<String, Long> arrivals = new HashMap<String, Long>();
    private volatile long quietUntilMs;
    private final ConcurrentLinkedQueue<Observation> observations = new ConcurrentLinkedQueue();
    private final Map<UUID, KnownPlayer> knownPlayers = new HashMap<UUID, KnownPlayer>();
    private final Map<String, Detection> detections = new LinkedHashMap<String, Detection>();
    private final Map<String, Long> signalCooldowns = new HashMap<String, Long>();
    private final Map<String, Long> announceCooldowns = new HashMap<String, Long>();
    private final Deque<Long> announceTimes = new ArrayDeque<Long>();
    private final Map<Long, Long> selfPlacedBlocks = new ConcurrentHashMap<Long, Long>();
    private final Map<UUID, Long> confirmedDepartures = new HashMap<UUID, Long>();
    private final Map<Long, Long> automatedMechanisms = new HashMap<Long, Long>();
    private final Map<String, Deque<Long>> weakParticleBursts = new HashMap<String, Deque<Long>>();
    private final Deque<Signal> signals = new ArrayDeque<Signal>();
    private final Deque<ExplosionEvent> recentExplosions = new ArrayDeque<ExplosionEvent>();
    private final Deque<PendingVanish> pendingVanishes = new ArrayDeque<PendingVanish>();
    private final Map<String, HiddenSpell> hiddenSpells = new LinkedHashMap<String, HiddenSpell>();
    private final Deque<RecentMessage> recentMessages = new ArrayDeque<RecentMessage>();
    private boolean serverSendsLeaveMessages;
    private static final long RECENT_MESSAGE_TTL_MS = 8000L;
    private static final long TRUSTED_LISTED_MS = 1500L;
    private final Map<UUID, Long> listedSinceMs = new HashMap<UUID, Long>();
    private final ShardTracker shard = new ShardTracker();
    private int tickCounter;
    private volatile long lastLocalActionMs;
    private long lastContainerActivityMs;
    private long lastCriticalMs;
    private long criticalUntilMs;
    private String criticalWatcher = "";
    private final Set<UUID> pendingChimes = new HashSet<UUID>();
    private UUID ghostId;
    private String ghostName;

    public AntiVanishModule() {
        super("anti-vanish", "Anti Vanish", "Detects vanished players.", ModuleCategory.DONUT);
        this.usesNotifications();
    }

    @Override
    protected void onEnable() {
        this.resetRuntime();
        if (AntiVanishModule.MC.field_1724 != null && AntiVanishModule.MC.field_1687 != null) {
            this.trackListedPlayers();
        }
    }

    @Override
    protected void onDisable() {
        this.resetRuntime();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (AntiVanishModule.MC.field_1724 == null || AntiVanishModule.MC.field_1687 == null || MC.method_1562() == null) {
            this.resetRuntime();
            return;
        }
        if (this.shard.moved(MC)) {
            this.resetRuntime();
        }
        this.playPendingChime();
        ++this.tickCounter;
        if (AntiVanishModule.MC.field_1724.field_7512 != AntiVanishModule.MC.field_1724.field_7498) {
            this.lastContainerActivityMs = System.currentTimeMillis();
        }
        this.drainObservations();
        this.processPendingVanishes();
        this.judgeHints();
        if (this.tickCounter % 10 == 0) {
            this.trackListedPlayers();
            this.reviewSpells();
        }
        if (this.tickCounter % 5 == 0) {
            this.scanInvisiblePlayers();
        }
        this.pruneState();
    }

    @EventHandler
    public void onPacketSend(PacketSendEvent event) {
        this.handleOutbound(event.packet);
    }

    private void handleOutbound(class_2596<?> packet) {
        if (packet == null) {
            return;
        }
        if (packet instanceof class_2885) {
            class_2885 useOn = (class_2885)packet;
            this.lastLocalActionMs = System.currentTimeMillis();
            if (useOn.method_12543() != null && useOn.method_12543().method_17777() != null) {
                long until = System.currentTimeMillis() + 6000L;
                class_2338 hit = useOn.method_12543().method_17777();
                class_2338 adjacent = hit.method_10093(useOn.method_12543().method_17780());
                this.selfPlacedBlocks.put(hit.method_10063(), until);
                this.selfPlacedBlocks.put(adjacent.method_10063(), until);
                String itemPath = "";
                if (AntiVanishModule.MC.field_1724 != null && useOn.method_12546() != null) {
                    class_2960 itemId = class_7923.field_41178.method_10221((Object)AntiVanishModule.MC.field_1724.method_5998(useOn.method_12546()).method_7909());
                    itemPath = AntiVanishHeuristics.path(itemId == null ? "" : itemId.toString());
                }
                AntiVanishModule.markSelfMultiBlockFootprint(hit, itemPath, this.selfPlacedBlocks, until);
                AntiVanishModule.markSelfMultiBlockFootprint(adjacent, itemPath, this.selfPlacedBlocks, until);
            }
            return;
        }
        if (packet instanceof class_2846 || packet instanceof class_2886 || packet instanceof class_2824 || packet instanceof class_2879) {
            this.lastLocalActionMs = System.currentTimeMillis();
        }
    }

    static void markSelfMultiBlockFootprint(class_2338 pos, String path, Map<Long, Long> targets, long until) {
        boolean vertical;
        if (pos == null || targets == null) {
            return;
        }
        targets.put(pos.method_10063(), until);
        String id = path == null ? "" : path;
        boolean bl = vertical = id.endsWith("_door") && !id.endsWith("trapdoor") || id.contains("sunflower") || id.contains("lilac") || id.contains("rose_bush") || id.contains("peony") || id.contains("tall_grass") || id.contains("large_fern") || id.contains("pitcher_plant");
        if (vertical) {
            targets.put(pos.method_10084().method_10063(), until);
            targets.put(pos.method_10074().method_10063(), until);
        }
        if (id.endsWith("_bed")) {
            targets.put(pos.method_10069(1, 0, 0).method_10063(), until);
            targets.put(pos.method_10069(-1, 0, 0).method_10063(), until);
            targets.put(pos.method_10069(0, 0, 1).method_10063(), until);
            targets.put(pos.method_10069(0, 0, -1).method_10063(), until);
        }
    }

    @EventHandler
    public void onPacketReceiveNetty(PacketReceiveNettyEvent event) {
        if (this.handleInbound(event.packet)) {
            event.setCancelled(true);
        }
    }

    private boolean handleInbound(class_2596<?> packet) {
        if (packet instanceof class_8042) {
            class_8042 bundle = (class_8042)packet;
            for (class_2596 part : bundle.method_48324()) {
                this.handleInbound(part);
            }
            return false;
        }
        if (packet instanceof class_7828) {
            class_7828 remove = (class_7828)packet;
            if (remove.comp_1105().size() < 4) {
                for (UUID id : remove.comp_1105()) {
                    this.observations.offer(Observation.tabRemove(id));
                }
            }
            long now = System.currentTimeMillis();
            for (UUID id : remove.comp_1105()) {
                this.hints.offer(new Hint(HintKind.TAB_OUT, id, null, -1, now));
            }
        } else if (packet instanceof class_2703) {
            class_2703 info = (class_2703)packet;
            this.queueTabHints(info);
            if (info.method_46327().contains(class_2703.class_5893.field_40700)) {
                int unlisted = 0;
                for (class_2703.class_2705 entry : info.method_46329()) {
                    if (entry == null || entry.comp_1108()) continue;
                    ++unlisted;
                }
                if (unlisted > 0 && unlisted < 4) {
                    for (class_2703.class_2705 entry : info.method_46329()) {
                        if (entry == null || entry.comp_1108()) continue;
                        this.observations.offer(Observation.tabHide(entry.comp_1106()));
                    }
                }
            }
            if (info.method_46327().contains(class_2703.class_5893.field_29137)) {
                this.queueGamemodeUpdates(info);
            }
        } else if (packet instanceof class_7439) {
            String departedName;
            String text;
            class_7439 chat = (class_7439)packet;
            String string = text = chat.comp_763() == null ? "" : chat.comp_763().getString();
            if (!text.isBlank()) {
                this.observations.offer(Observation.systemChat(text));
            }
            if (!(departedName = AntiVanishModule.departedPlayerName(chat.comp_763())).isBlank()) {
                this.observations.offer(Observation.playerLeft(departedName));
            }
        } else if (packet instanceof class_2739) {
            class_2739 metadata = (class_2739)packet;
            this.observations.offer(Observation.entityMetadata(metadata.comp_1127()));
        } else if (packet instanceof class_2664) {
            class_2664 explosion = (class_2664)packet;
            this.observations.offer(Observation.explosion(explosion.comp_2883(), explosion.comp_4594()));
        } else if (packet instanceof class_2765) {
            class_2765 sound = (class_2765)packet;
            this.observations.offer(Observation.entitySound(sound.method_11883(), AntiVanishModule.soundId(((class_3414)sound.method_11882().comp_349()).comp_3319())));
        } else if (packet instanceof class_2675) {
            class_2675 particles = (class_2675)packet;
            class_2960 id = class_7923.field_41180.method_10221((Object)particles.method_11551().method_10295());
            this.observations.offer(Observation.position(ObservationType.PARTICLE, particles.method_11544(), particles.method_11547(), particles.method_11546(), id == null ? "" : id.toString()));
        } else if (packet instanceof class_2767) {
            class_2767 sound = (class_2767)packet;
            this.onSoundPacket(sound);
        } else if (packet instanceof class_5900) {
            class_5900 team = (class_5900)packet;
            this.queueTeamHint(team);
        }
        return false;
    }

    private void queueTabHints(class_2703 info) {
        block3: {
            long now;
            block2: {
                now = System.currentTimeMillis();
                if (!info.method_46327().contains(class_2703.class_5893.field_29136)) break block2;
                for (class_2703.class_2705 entry : info.method_46329()) {
                    if (entry == null || entry.comp_1107() == null || entry.comp_1107().name() == null) continue;
                    this.hints.offer(new Hint(HintKind.TAB_IN, entry.comp_1106(), entry.comp_1107().name(), -1, now));
                }
                break block3;
            }
            if (!ORDER_ONLY.equals(info.method_46327()) || info.method_46329().size() >= 4) break block3;
            for (class_2703.class_2705 entry : info.method_46329()) {
                if (entry == null || entry.comp_1106() == null) continue;
                this.hints.offer(new Hint(HintKind.ORDER, entry.comp_1106(), null, -1, now));
            }
        }
    }

    private void queueTeamHint(class_5900 team) {
        boolean created;
        Matcher rank;
        class_5900.class_5901 action = team.method_34176();
        Matcher matcher = rank = action == null ? null : RANK_TEAM.matcher(team.method_34177());
        if (rank == null || !rank.matches()) {
            return;
        }
        String name = rank.group(2);
        boolean bl = created = action == class_5900.class_5901.field_29155;
        if (created && !team.method_34178().isEmpty()) {
            if (team.method_34178().stream().noneMatch(name::equalsIgnoreCase)) {
                return;
            }
        }
        this.hints.offer(new Hint(created ? HintKind.TEAM_UP : HintKind.TEAM_DOWN, null, name, Integer.parseInt(rank.group(1)), System.currentTimeMillis()));
    }

    private void judgeHints() {
        Hint hint;
        block6: while ((hint = this.hints.poll()) != null) {
            switch (hint.kind().ordinal()) {
                case 3: {
                    this.tabMoves.addLast(hint);
                    break;
                }
                case 4: {
                    String name = this.tabNames.get(hint.id());
                    if (name == null) continue block6;
                    this.tabMoves.addLast(new Hint(HintKind.TAB_OUT, hint.id(), name, -1, hint.atMs()));
                    break;
                }
                case 1: 
                case 2: {
                    this.teamChanges.addLast(hint.atMs());
                    this.heldHints.add(hint);
                    break;
                }
                case 0: {
                    this.heldHints.add(hint);
                }
            }
        }
        long now = System.currentTimeMillis();
        Iterator<Hint> it = this.heldHints.iterator();
        while (it.hasNext()) {
            Hint hint2 = it.next();
            if (now - hint2.atMs() < 1000L) continue;
            it.remove();
            this.judge(hint2);
        }
        while (!this.tabMoves.isEmpty() && now - this.tabMoves.peekFirst().atMs() > 5000L) {
            this.tabMoves.removeFirst();
        }
        while (!this.teamChanges.isEmpty() && now - this.teamChanges.peekFirst() > 5000L) {
            this.teamChanges.removeFirst();
        }
        if (this.tabNames.size() > 4096) {
            this.tabNames.clear();
        }
        for (class_640 info : MC.method_1562().method_2880()) {
            if (info.method_2966() == null || info.method_2966().name() == null) continue;
            this.tabNames.put(info.method_2966().id(), info.method_2966().name());
        }
    }

    private void judge(Hint hint) {
        int rank;
        String name;
        class_640 info;
        if (hint.atMs() < this.quietUntilMs) {
            return;
        }
        class_640 class_6402 = info = hint.id() != null ? MC.method_1562().method_2871(hint.id()) : MC.method_1562().method_73471(hint.name());
        String string = hint.name() != null ? hint.name() : (name = info == null || info.method_2966() == null ? null : info.method_2966().name());
        if (name == null || name.equalsIgnoreCase(AntiVanishModule.MC.field_1724.method_7334().name())) {
            return;
        }
        int n = rank = hint.rank() >= 0 ? hint.rank() : AntiVanishModule.teamRank(info);
        if (!(StaffListModule.isStaffName(name) || rank >= 0 && rank <= 8)) {
            return;
        }
        UUID uuid = info != null && info.method_2966() != null ? info.method_2966().id() : hint.id();
        switch (hint.kind().ordinal()) {
            case 0: {
                this.arrived(uuid, name, "tab re-sort");
                break;
            }
            case 1: {
                if (!this.teamOnly(hint)) break;
                this.arrived(uuid, name, "team created");
                break;
            }
            case 2: {
                if (!this.teamOnly(hint)) break;
                this.departed(name, "team removed");
                break;
            }
        }
    }

    private boolean teamOnly(Hint hint) {
        int batch = 0;
        Iterator<Object> iterator = this.teamChanges.iterator();
        while (iterator.hasNext()) {
            long at = iterator.next();
            if (Math.abs(at - hint.atMs()) > 50L || ++batch < 5) continue;
            return false;
        }
        for (Hint move : this.tabMoves) {
            if (!move.name().equalsIgnoreCase(hint.name()) || Math.abs(move.atMs() - hint.atMs()) > 1000L) continue;
            return false;
        }
        return true;
    }

    private static int teamRank(class_640 info) {
        class_268 team = info == null ? null : info.method_2955();
        Matcher rank = team == null ? null : RANK_TEAM.matcher(team.method_1197());
        return rank != null && rank.matches() ? Integer.parseInt(rank.group(1)) : -1;
    }

    private void arrived(UUID uuid, String name, String how) {
        long now = System.currentTimeMillis();
        Long last = this.arrivals.put(AntiVanishModule.spellKey(name), now);
        if (uuid != null) {
            StaffListModule.provenHere(uuid, name);
        }
        if (last != null && now - last < 60000L) {
            return;
        }
        this.news(name, ModuleChat.body().text("is ").danger("on your goliath").build(), how);
        this.watchingYou(name, new RegionProof(uuid, -1));
    }

    private void departed(String name, String how) {
        if (this.arrivals.remove(AntiVanishModule.spellKey(name)) == null) {
            return;
        }
        this.news(name, ModuleChat.body().good("left your goliath").build(), how);
    }

    private void onSoundPacket(class_2767 packet) {
        if (packet == null || packet.method_11894() == null || packet.method_11894().comp_349() == null) {
            return;
        }
        String id = AntiVanishModule.soundId(((class_3414)packet.method_11894().comp_349()).comp_3319());
        this.observations.offer(Observation.position(ObservationType.POSITIONAL_SOUND, packet.method_11890(), packet.method_11889(), packet.method_11893(), id));
    }

    public static boolean shouldShowHud() {
        AntiVanishModule module = AntiVanishModule.instance();
        return module != null && module.isEnabled() && module.hasHudContent();
    }

    private boolean hasHudContent() {
        long now = System.currentTimeMillis();
        if (now < this.criticalUntilMs) {
            return true;
        }
        for (Detection detection : this.detections.values()) {
            if (detection.expiresAt <= now || !AntiVanishModule.detectionWorthShowing(detection)) continue;
            return true;
        }
        return false;
    }

    private static boolean detectionWorthShowing(Detection detection) {
        if (detection == null) {
            return false;
        }
        String name = detection.name == null ? "" : detection.name.trim();
        return !name.isBlank() && !"Unknown".equalsIgnoreCase(name) && !"You".equalsIgnoreCase(name) && !"CRITICAL".equalsIgnoreCase(name);
    }

    public static String hudTag(HudEntry entry) {
        String lower;
        if (entry == null) {
            return "WATCH";
        }
        if ("CRITICAL".equalsIgnoreCase(entry.name())) {
            return "ALERT";
        }
        String string = lower = entry.reason() == null ? "" : entry.reason().toLowerCase(Locale.ROOT);
        if (lower.startsWith("vanish event")) {
            return "VANISH";
        }
        if (lower.startsWith("invisible entity")) {
            return "INVIS";
        }
        if (lower.startsWith("suspicious sound")) {
            return "SOUND";
        }
        if (lower.startsWith("ghost particle")) {
            return "PARTICLE";
        }
        return "WATCH";
    }

    public static String hudValue(HudEntry entry) {
        String name;
        if (entry == null) {
            return "Staff";
        }
        if ("CRITICAL".equalsIgnoreCase(entry.name())) {
            return AntiVanishModule.shortName(entry.reason());
        }
        String string = name = entry.name() == null ? "" : entry.name().trim();
        if (name.isBlank() || "Unknown".equalsIgnoreCase(name) || "You".equalsIgnoreCase(name)) {
            return "Staff";
        }
        return AntiVanishModule.shortName(name);
    }

    private static String shortName(String name) {
        String shown = name == null ? "" : NameFilter.apply(name.trim());
        return shown.length() <= 16 ? shown : shown.substring(0, 16);
    }

    public static List<HudEntry> hudEntries() {
        AntiVanishModule module = AntiVanishModule.instance();
        return module == null || !AntiVanishModule.shouldShowHud() ? List.of() : module.hudSnapshot();
    }

    private static AntiVanishModule instance() {
        return ModuleRegistry.get(AntiVanishModule.class);
    }

    private void drainObservations() {
        Observation observation;
        for (int i = 0; i < 512 && (observation = this.observations.poll()) != null; ++i) {
            this.processObservation(observation);
        }
        while (this.observations.size() > 4096) {
            this.observations.poll();
        }
    }

    private void processObservation(Observation observation) {
        switch (observation.type.ordinal()) {
            case 0: {
                this.handleTabRemoval(observation.profileId);
                break;
            }
            case 1: {
                this.handleTabHidden(observation.profileId);
                break;
            }
            case 2: {
                this.serverSendsLeaveMessages = true;
                this.confirmDeparture(observation.detail);
                break;
            }
            case 3: {
                this.cacheRecentMessage(observation.detail);
                break;
            }
            case 4: {
                this.inspectInvisibleEntity(observation.entityId);
                break;
            }
            case 5: {
                this.inspectPositionalSound(observation);
                break;
            }
            case 6: {
                this.inspectEntitySound(observation);
                break;
            }
            case 7: {
                this.inspectParticle(observation);
                break;
            }
            case 8: {
                this.rememberExplosion(observation);
                break;
            }
            case 9: {
                this.announceGamemode(observation.profileId, observation.detail);
            }
        }
    }

    private void trackListedPlayers() {
        if (MC.method_1562() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        for (class_640 info : MC.method_1562().method_45732()) {
            if (info == null || info.method_2966() == null || info.method_2966().id() == null) continue;
            UUID uuid = info.method_2966().id();
            if (AntiVanishModule.MC.field_1724 != null && AntiVanishModule.MC.field_1724.method_5667().equals(uuid)) continue;
            this.listedSinceMs.putIfAbsent(uuid, now);
            String name = info.method_2966().name();
            if (name != null && !this.knownPlayers.containsKey(uuid)) {
                this.knownPlayers.put(uuid, new KnownPlayer(uuid, name));
            }
            if (name == null || this.hiddenSpells.isEmpty()) continue;
            this.endSpell(name, false, 1000L);
        }
        if (this.listedSinceMs.size() > 1024 || this.knownPlayers.size() > 1024) {
            HashSet<UUID> connected = new HashSet<UUID>();
            for (class_640 info : MC.method_1562().method_2880()) {
                if (info == null || info.method_2966() == null) continue;
                connected.add(info.method_2966().id());
            }
            this.listedSinceMs.keySet().retainAll(connected);
            this.knownPlayers.keySet().retainAll(connected);
        }
    }

    private boolean trustedListed(UUID uuid) {
        Long since = uuid == null ? null : this.listedSinceMs.get(uuid);
        return since != null && System.currentTimeMillis() - since >= 1500L;
    }

    private boolean credibleSubject(UUID uuid, String name) {
        return uuid != null && AntiVanishText.isUsername(name) && StaffListModule.isStaffName(name) && this.trustedListed(uuid);
    }

    private void handleTabRemoval(UUID uuid) {
        if (uuid == null || AntiVanishModule.MC.field_1724.method_5667().equals(uuid)) {
            return;
        }
        String name = this.knownName(uuid);
        if (!this.credibleSubject(uuid, name) || !AntiVanishText.isPlausiblePlayerName(name)) {
            return;
        }
        this.pendingVanishes.removeIf(pending -> pending.uuid.equals(uuid));
        this.pendingVanishes.addLast(new PendingVanish(uuid, name, this.tickCounter + 20));
    }

    private void handleTabHidden(UUID uuid) {
        String name;
        if (uuid == null || AntiVanishModule.MC.field_1724.method_5667().equals(uuid)) {
            return;
        }
        KnownPlayer known = this.knownPlayers.get(uuid);
        String string = name = known != null && known.name != null ? known.name : this.knownName(uuid);
        if (!this.credibleSubject(uuid, name) || !AntiVanishText.isPlausiblePlayerName(name)) {
            return;
        }
        if (this.recentMessageNames(name)) {
            return;
        }
        this.reportHidden(uuid, name, false, "Vanish Event: hidden from TAB", "hidden from TAB", 100);
    }

    private String knownName(UUID uuid) {
        class_640 info;
        KnownPlayer known = this.knownPlayers.get(uuid);
        if (known != null && known.name != null && !known.name.isBlank()) {
            return known.name;
        }
        if (MC.method_1562() != null && (info = MC.method_1562().method_2871(uuid)) != null && info.method_2966() != null) {
            return info.method_2966().name();
        }
        return null;
    }

    private void confirmDeparture(String displayedName) {
        long now = System.currentTimeMillis();
        for (KnownPlayer known : this.knownPlayers.values()) {
            if (!AntiVanishText.containsPlayerName(displayedName, known.name)) continue;
            this.confirmedDepartures.put(known.uuid, now + 5000L);
            this.pendingVanishes.removeIf(pending -> pending.uuid.equals(known.uuid));
            this.hiddenSpells.remove(AntiVanishModule.spellKey(known.name));
            Detection detection = this.detections.get(known.uuid.toString());
            if (detection != null && AntiVanishModule.tabDepartureReason(detection.reason)) {
                this.detections.remove(known.uuid.toString());
            }
            this.signals.removeIf(signal -> signal.type == SignalType.VANISH && signal.subject.equalsIgnoreCase(known.name) && AntiVanishModule.tabDepartureReason(signal.reason));
        }
    }

    private void processPendingVanishes() {
        while (!this.pendingVanishes.isEmpty() && this.pendingVanishes.peekFirst().dueTick <= this.tickCounter) {
            PendingVanish pending = this.pendingVanishes.removeFirst();
            if (MC.method_1562().method_2871(pending.uuid) != null) continue;
            long now = System.currentTimeMillis();
            if (this.confirmedDepartures.getOrDefault(pending.uuid, 0L) > now) continue;
            if (this.recentMessageNames(pending.name)) {
                this.serverSendsLeaveMessages = true;
                continue;
            }
            class_1657 remaining = AntiVanishModule.MC.field_1687.method_18470(pending.uuid);
            if (remaining != null && !remaining.method_31481()) {
                this.reportHidden(pending.uuid, pending.name, false, "Vanish Event: entity remained", "off TAB, still nearby", 100);
                continue;
            }
            String reason = "Vanish Event: silent TAB disappearance";
            int score = AntiVanishModule.silentTabRemovalScore(this.serverSendsLeaveMessages);
            if (this.serverSendsLeaveMessages) {
                this.reportHidden(pending.uuid, pending.name, false, reason, "left TAB silently", score);
                continue;
            }
            RegionProof proof = this.regionProof(pending.uuid);
            if (proof == null) continue;
            this.signals.addLast(new Signal(SignalType.VANISH, pending.name, proof, reason, score, now));
            this.evaluateCritical();
        }
    }

    static int silentTabRemovalScore(boolean serverSendsLeaveMessages) {
        return serverSendsLeaveMessages ? 100 : 30;
    }

    static boolean tabDepartureReason(String reason) {
        if (reason == null) {
            return false;
        }
        String lower = reason.toLowerCase(Locale.ROOT);
        return lower.startsWith("vanish event:") && (lower.contains("tab") || lower.contains("entity remained") || lower.contains("no leave packet"));
    }

    private void cacheRecentMessage(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        long now = System.currentTimeMillis();
        this.recentMessages.addLast(new RecentMessage(text, now));
        while (!(this.recentMessages.isEmpty() || now - this.recentMessages.peekFirst().atMs <= 8000L && this.recentMessages.size() <= 64)) {
            this.recentMessages.removeFirst();
        }
    }

    private boolean recentMessageNames(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        long now = System.currentTimeMillis();
        for (RecentMessage message : this.recentMessages) {
            if (now - message.atMs > 8000L || !AntiVanishText.looksLikeLeaveMessage(message.text, name)) continue;
            return true;
        }
        return false;
    }

    private static String departedPlayerName(class_2561 component) {
        String string;
        class_2588 translated;
        class_7417 class_74172;
        if (component == null || !((class_74172 = component.method_10851()) instanceof class_2588) || !"multiplayer.player.left".equals((translated = (class_2588)class_74172).method_11022())) {
            return "";
        }
        Object[] args = translated.method_11023();
        if (args.length == 0 || args[0] == null) {
            return "";
        }
        Object object = args[0];
        if (object instanceof class_2561) {
            class_2561 name = (class_2561)object;
            string = name.getString();
        } else {
            string = String.valueOf(args[0]);
        }
        return string;
    }

    private void reportHidden(UUID uuid, String name, boolean spectator, String reason, String detail, int score) {
        this.upsertDetection(AntiVanishModule.detectionKey(uuid, name), name, reason, score, System.currentTimeMillis() + 20000L);
        HiddenSpell spell = this.hiddenSpells.get(AntiVanishModule.spellKey(name));
        if (spell != null && spell.spectator == spectator) {
            return;
        }
        this.hiddenSpells.put(AntiVanishModule.spellKey(name), new HiddenSpell(uuid, name, spectator, detail, this.regionProof(uuid), this.tickCounter));
    }

    private void reviewSpells() {
        Iterator<HiddenSpell> it = this.hiddenSpells.values().iterator();
        while (it.hasNext()) {
            HiddenSpell spell = it.next();
            if (spell.spectator && MC.method_1562().method_2871(spell.uuid) == null) {
                it.remove();
                continue;
            }
            if (spell.watched || spell.sinceTick >= this.tickCounter) continue;
            RegionProof proof = this.regionProof(spell.uuid);
            if (proof == null && !spell.told) {
                proof = spell.proof;
            }
            if (proof != null) {
                spell.told = true;
                spell.watched = true;
                this.watchingYou(spell.name, proof);
                continue;
            }
            if (spell.told) continue;
            spell.told = true;
            this.news(spell.name, spell.how(), spell.detail);
        }
    }

    private boolean endSpell(String name, boolean spectator, long settleMs) {
        HiddenSpell spell = this.hiddenSpells.get(AntiVanishModule.spellKey(name));
        if (spell == null || spell.spectator != spectator || System.currentTimeMillis() - spell.sinceMs < settleMs) {
            return false;
        }
        this.hiddenSpells.remove(AntiVanishModule.spellKey(name));
        this.detections.remove(AntiVanishModule.detectionKey(spell.uuid, spell.name));
        if (spell.told) {
            this.news(spell.name, ModuleChat.body().text("is ").good("visible again").build(), "");
        }
        return true;
    }

    private RegionProof regionProof(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        class_1657 body = AntiVanishModule.MC.field_1687.method_18470(uuid);
        if (body != null && body != AntiVanishModule.MC.field_1724) {
            return new RegionProof(uuid, Math.round(body.method_5739((class_1297)AntiVanishModule.MC.field_1724)));
        }
        return StaffListModule.isInYourRegion(uuid) ? new RegionProof(uuid, -1) : null;
    }

    private static String spellKey(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private static String detectionKey(UUID uuid, String name) {
        return uuid != null ? uuid.toString() : name;
    }

    private void scanInvisiblePlayers() {
        double rangeSq = this.sensorRangeSq();
        for (class_1657 player : AntiVanishModule.MC.field_1687.method_18456()) {
            if (player == null || player == AntiVanishModule.MC.field_1724 || !player.method_5767() || player.method_5858((class_1297)AntiVanishModule.MC.field_1724) > rangeSq || !this.realPlayer(player)) continue;
            this.triggerSensor(SignalType.INVISIBLE, player, "Invisible Entity: metadata flag", 35, 5000L);
        }
    }

    private void inspectInvisibleEntity(int entityId) {
        class_1657 player;
        class_1297 entity = AntiVanishModule.MC.field_1687.method_8469(entityId);
        if (!(entity instanceof class_1657) || (player = (class_1657)entity) == AntiVanishModule.MC.field_1724 || !player.method_5767()) {
            return;
        }
        if (player.method_5858((class_1297)AntiVanishModule.MC.field_1724) > this.sensorRangeSq()) {
            return;
        }
        if (!this.realPlayer(player)) {
            return;
        }
        this.triggerSensor(SignalType.INVISIBLE, player, "Invisible Entity: metadata flag", 35, 5000L);
    }

    private boolean realPlayer(class_1657 player) {
        return this.credibleSubject(player.method_5667(), AntiVanishModule.profileName(player));
    }

    private static String profileName(class_1657 player) {
        return player.method_7334() == null ? player.method_5477().getString() : player.method_7334().name();
    }

    private void inspectPositionalSound(Observation observation) {
        if (!AntiVanishHeuristics.suspiciousSound(observation.detail)) {
            return;
        }
        class_243 source = observation.position();
        long now = System.currentTimeMillis();
        if (!this.nearPlayer(source) || this.hasVisibleCause(source) || this.isExplosionRelated(source) || this.isPoweredMechanism(source, observation.detail) || now - this.lastLocalActionMs < 1000L || this.recentlySelfPlaced(source) || this.villagerToggledDoor(observation.detail, source) || this.selfContainerActive() && AntiVanishModule.isContainerSignal(observation.detail)) {
            return;
        }
        this.triggerSensor(SignalType.SOUND, this.locatedSubject(source), "Suspicious Sound: " + AntiVanishModule.shortId(observation.detail), 14, 3000L);
    }

    private void inspectEntitySound(Observation observation) {
        class_1657 player;
        if (!AntiVanishHeuristics.suspiciousSound(observation.detail)) {
            return;
        }
        class_1297 entity = AntiVanishModule.MC.field_1687.method_8469(observation.entityId);
        if (entity instanceof class_1657 && (player = (class_1657)entity) != AntiVanishModule.MC.field_1724 && player.method_5767() && player.method_5858((class_1297)AntiVanishModule.MC.field_1724) <= this.sensorRangeSq() && this.realPlayer(player)) {
            this.triggerSensor(SignalType.SOUND, player, "Suspicious Sound: invisible source", 16, 3000L);
        }
    }

    private void inspectParticle(Observation observation) {
        if (!AntiVanishHeuristics.suspiciousParticle(observation.detail)) {
            return;
        }
        class_243 source = observation.position();
        long now = System.currentTimeMillis();
        if (!this.nearPlayer(source) || this.hasVisibleCause(source) || this.isExplosionRelated(source) || now - this.lastLocalActionMs < 900L || this.hasAmbientParticleSource(source, observation.detail)) {
            return;
        }
        String particle = AntiVanishModule.shortId(observation.detail);
        if ((particle.equals("block") || particle.contains("smoke")) && this.nearSelf(source, 6.25)) {
            return;
        }
        if ((particle.equals("block") || particle.contains("smoke")) && !this.particleBurstReady(particle, now)) {
            return;
        }
        this.triggerSensor(SignalType.PARTICLE, this.locatedSubject(source), "Ghost Particle: " + AntiVanishModule.shortId(observation.detail), 16, 3000L);
    }

    private boolean villagerToggledDoor(String blockId, class_243 source) {
        boolean doorLike;
        String path = AntiVanishHeuristics.path(blockId);
        boolean bl = doorLike = path.contains("door") && !path.contains("trapdoor") || path.contains("fence_gate");
        if (!doorLike || source == null || AntiVanishModule.MC.field_1687 == null) {
            return false;
        }
        for (class_1297 entity : AntiVanishModule.MC.field_1687.method_18112()) {
            if (entity.method_5864() != class_1299.field_6077 || !(entity.method_73189().method_1025(source) <= 9.0)) continue;
            return true;
        }
        return false;
    }

    private boolean recentlySelfPlaced(class_243 source) {
        if (source == null) {
            return false;
        }
        Long until = this.selfPlacedBlocks.get(class_2338.method_49638((class_2374)source).method_10063());
        return until != null && until > System.currentTimeMillis();
    }

    private void rememberExplosion(Observation observation) {
        long now = System.currentTimeMillis();
        this.recentExplosions.addLast(new ExplosionEvent(observation.position(), Math.max(2.0, AntiVanishModule.parseDouble(observation.detail, 4.0) + 4.0), now));
        while (this.recentExplosions.size() > 8) {
            this.recentExplosions.removeFirst();
        }
    }

    private boolean particleBurstReady(String particle, long now) {
        Deque burst = this.weakParticleBursts.computeIfAbsent(particle, ignored -> new ArrayDeque());
        burst.addLast(now);
        while (!burst.isEmpty() && now - (Long)burst.peekFirst() > 2000L) {
            burst.removeFirst();
        }
        if (burst.size() < 2) {
            return false;
        }
        burst.clear();
        return true;
    }

    private void triggerSensor(SignalType type, String where, String reason, int weight, long cooldownMs) {
        this.addSignal(type, where, null, reason, weight, cooldownMs);
    }

    private void triggerSensor(SignalType type, class_1657 player, String reason, int weight, long cooldownMs) {
        this.addSignal(type, AntiVanishModule.profileName(player), this.regionProof(player.method_5667()), reason, weight, cooldownMs);
    }

    private void addSignal(SignalType type, String subject, RegionProof proof, String reason, int weight, long cooldownMs) {
        String cooldownKey;
        long last;
        long now = System.currentTimeMillis();
        if (now - (last = this.signalCooldowns.getOrDefault(cooldownKey = type.name() + "|" + subject.toLowerCase(Locale.ROOT), 0L).longValue()) < cooldownMs) {
            return;
        }
        this.signalCooldowns.put(cooldownKey, now);
        this.signals.addLast(new Signal(type, subject, proof, reason, weight, now));
        this.upsertDetection("signal:" + cooldownKey, subject, reason, weight, now + 20000L);
        this.announceTrigger(cooldownKey, subject, proof != null, reason);
        this.evaluateCritical();
    }

    private void evaluateCritical() {
        long now = System.currentTimeMillis();
        this.pruneSignals(now);
        EnumMap<SignalType, Integer> strongest = new EnumMap<SignalType, Integer>(SignalType.class);
        Signal watcher = null;
        for (Signal signal : this.signals) {
            strongest.merge(signal.type, signal.weight, Math::max);
            if (signal.proof == null) continue;
            watcher = signal;
        }
        int score = strongest.values().stream().mapToInt(Integer::intValue).sum();
        if (watcher == null || strongest.size() < 2 || score < 35) {
            return;
        }
        if (now - this.lastCriticalMs < 12000L) {
            return;
        }
        this.watchingYou(watcher.subject, watcher.proof);
    }

    private void watchingYou(String name, RegionProof proof) {
        long now;
        this.lastCriticalMs = now = System.currentTimeMillis();
        this.criticalUntilMs = now + 7000L;
        this.criticalWatcher = name;
        this.pendingChimes.add(proof.id());
        this.ghostId = proof.id();
        this.ghostName = name;
    }

    private void playPendingChime() {
        if (this.pendingChimes.isEmpty()) {
            return;
        }
        boolean sirenCovers = this.pendingChimes.stream().allMatch(StaffListModule::alarmedRecently);
        this.pendingChimes.clear();
        if (sirenCovers) {
            return;
        }
        if (MC.method_1483() != null) {
            MC.method_1483().method_4873((class_1113)class_1109.method_4757((class_3414)class_3417.field_15203, (float)1.0f, (float)1.0f));
        }
        StaffGhost.watching(this.ghostId, this.ghostName, 0);
    }

    private void news(String name, class_2561 what, String detail) {
        ModuleChat.Body line = ModuleChat.body().name(NameFilter.apply(name)).text(" ").append(what);
        if (!detail.isEmpty()) {
            line.text(" (" + detail + ")");
        }
        ModuleChat.send((ClientModule)this, line);
    }

    private void queueGamemodeUpdates(class_2703 info) {
        if (info.method_46327().contains(class_2703.class_5893.field_29136)) {
            return;
        }
        for (class_2703.class_2705 entry : info.method_46329()) {
            if (entry == null || entry.comp_1110() == null || entry.comp_1106() == null) continue;
            this.observations.offer(Observation.gamemode(entry.comp_1106(), entry.comp_1110().method_8381()));
        }
    }

    private void announceGamemode(UUID uuid, String gameMode) {
        if (uuid == null || AntiVanishModule.MC.field_1724 != null && AntiVanishModule.MC.field_1724.method_5667().equals(uuid)) {
            return;
        }
        KnownPlayer known = this.knownPlayers.get(uuid);
        if (known == null || !StaffListModule.isStaffName(known.name)) {
            return;
        }
        this.arrivals.put(AntiVanishModule.spellKey(known.name), System.currentTimeMillis());
        if (class_1934.field_9219.method_8381().equals(gameMode)) {
            if (this.credibleSubject(uuid, known.name)) {
                this.reportHidden(uuid, known.name, true, "Vanish Event: switched to spectator", "", 100);
                return;
            }
        } else if (this.endSpell(known.name, true, 0L)) {
            return;
        }
        ModuleChat.send((ClientModule)this, StaffListModule.switchedTo(ModuleChat.body().name(NameFilter.apply(known.name)).text(" "), gameMode));
    }

    private void announceTrigger(String eventKey, String subject, boolean named, String reason) {
        boolean located;
        long now = System.currentTimeMillis();
        Long last = this.announceCooldowns.get(eventKey);
        if (last != null && now - last < 1500L) {
            return;
        }
        while (!this.announceTimes.isEmpty() && now - this.announceTimes.peekFirst() > 4000L) {
            this.announceTimes.removeFirst();
        }
        if (this.announceTimes.size() >= 6) {
            return;
        }
        this.announceCooldowns.put(eventKey, now);
        this.announceTimes.addLast(now);
        int colon = reason.indexOf(": ");
        ModuleChat.Body line = colon < 0 ? ModuleChat.body().warn(reason) : ModuleChat.body().warn(reason.substring(0, colon)).text(reason.substring(colon));
        boolean bl = located = subject != null && !subject.isBlank() && !"Unknown".equalsIgnoreCase(subject) && !"CRITICAL".equalsIgnoreCase(subject);
        if (located) {
            line.text(" (");
            if (named) {
                line.name(NameFilter.apply(subject));
            } else {
                line.value(subject);
            }
            line.text(")");
        }
        ModuleChat.send((ClientModule)this, line);
    }

    private void upsertDetection(String key, String name, String reason, int score, long expiresAt) {
        Detection existing = this.detections.get(key);
        if (existing == null) {
            this.detections.put(key, new Detection(name, reason, score, expiresAt));
            return;
        }
        existing.name = name;
        existing.expiresAt = Math.max(existing.expiresAt, expiresAt);
        if (score >= existing.score) {
            existing.reason = reason;
            existing.score = score;
        }
    }

    private List<HudEntry> hudSnapshot() {
        long now = System.currentTimeMillis();
        ArrayList<HudEntry> out = new ArrayList<HudEntry>();
        if (now < this.criticalUntilMs) {
            out.add(new HudEntry("CRITICAL", this.criticalWatcher));
        }
        HashSet seenRows = new HashSet();
        this.detections.values().stream().filter(detection -> detection.expiresAt > now).filter(AntiVanishModule::detectionWorthShowing).sorted(Comparator.comparingInt(detection -> detection.score).reversed().thenComparing(detection -> detection.name, String.CASE_INSENSITIVE_ORDER)).map(detection -> new HudEntry(detection.name, detection.reason)).filter(entry -> seenRows.add(AntiVanishModule.hudTag(entry) + "\u0000" + AntiVanishModule.hudValue(entry))).limit(4L).forEach(out::add);
        return List.copyOf(out);
    }

    private void pruneState() {
        long now = System.currentTimeMillis();
        this.detections.entrySet().removeIf(entry -> ((Detection)entry.getValue()).expiresAt <= now);
        this.signalCooldowns.entrySet().removeIf(entry -> now - (Long)entry.getValue() > 60000L);
        this.announceCooldowns.entrySet().removeIf(entry -> now - (Long)entry.getValue() > 60000L);
        this.selfPlacedBlocks.entrySet().removeIf(entry -> (Long)entry.getValue() <= now);
        this.confirmedDepartures.entrySet().removeIf(entry -> (Long)entry.getValue() <= now);
        this.automatedMechanisms.entrySet().removeIf(entry -> (Long)entry.getValue() <= now);
        this.weakParticleBursts.values().removeIf(burst -> {
            while (!burst.isEmpty() && now - (Long)burst.peekFirst() > 2000L) {
                burst.removeFirst();
            }
            return burst.isEmpty();
        });
        while (!this.recentExplosions.isEmpty() && now - this.recentExplosions.peekFirst().timeMs > 3000L) {
            this.recentExplosions.removeFirst();
        }
        this.pruneSignals(now);
    }

    private void pruneSignals(long now) {
        while (!this.signals.isEmpty() && now - this.signals.peekFirst().timeMs > 15000L) {
            this.signals.removeFirst();
        }
    }

    private boolean nearPlayer(class_243 source) {
        return source != null && source.method_1025(AntiVanishModule.MC.field_1724.method_73189()) <= this.sensorRangeSq();
    }

    private boolean nearSelf(class_243 source, double maxDistSq) {
        return source != null && AntiVanishModule.MC.field_1724 != null && source.method_1025(AntiVanishModule.MC.field_1724.method_73189()) < maxDistSq;
    }

    private boolean selfContainerActive() {
        return System.currentTimeMillis() - this.lastContainerActivityMs < 2500L;
    }

    private static boolean isContainerSignal(String id) {
        String path = AntiVanishHeuristics.path(id);
        return path.contains("chest") || path.contains("barrel") || path.contains("shulker");
    }

    private String locatedSubject(class_243 source) {
        if (source == null || AntiVanishModule.MC.field_1724 == null) {
            return "nearby";
        }
        class_243 me = AntiVanishModule.MC.field_1724.method_73189();
        double dx = source.field_1352 - me.field_1352;
        double dy = source.field_1351 - me.field_1351;
        double dz = source.field_1350 - me.field_1350;
        long dist = Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
        String dir = AntiVanishModule.compass(dx, dz);
        return dir.isEmpty() ? dist + "m" : dist + "m " + dir;
    }

    private static String compass(double dx, double dz) {
        String ns;
        String string = dz < -1.0 ? "N" : (ns = dz > 1.0 ? "S" : "");
        String ew = dx > 1.0 ? "E" : (dx < -1.0 ? "W" : "");
        return ns + ew;
    }

    private boolean hasVisibleCause(class_243 source) {
        if (source == null) {
            return true;
        }
        for (class_1297 entity : AntiVanishModule.MC.field_1687.method_18112()) {
            class_1657 player;
            if (entity == AntiVanishModule.MC.field_1724 || !(entity instanceof class_1657 ? !(player = (class_1657)entity).method_5767() && entity.method_73189().method_1025(source) <= 16.0 : entity instanceof class_1676 && entity.method_73189().method_1025(source) <= 16.0)) continue;
            return true;
        }
        return false;
    }

    private boolean isExplosionRelated(class_243 source) {
        if (source == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        while (!this.recentExplosions.isEmpty() && now - this.recentExplosions.peekFirst().timeMs > 3000L) {
            this.recentExplosions.removeFirst();
        }
        for (ExplosionEvent explosion : this.recentExplosions) {
            if (!(explosion.center.method_1025(source) <= explosion.radius * explosion.radius)) continue;
            return true;
        }
        return false;
    }

    private boolean hasAmbientParticleSource(class_243 source, String particleId) {
        if (source == null || !AntiVanishModule.shortId(particleId).contains("smoke")) {
            return false;
        }
        class_2338 center = class_2338.method_49638((class_2374)source);
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                for (int dz = -2; dz <= 2; ++dz) {
                    class_2338 pos = center.method_10069(dx, dy, dz);
                    class_2960 id = class_7923.field_41175.method_10221((Object)AntiVanishModule.MC.field_1687.method_8320(pos).method_26204());
                    String path = AntiVanishModule.shortId(id == null ? "" : id.toString());
                    if (!path.contains("campfire") && !path.contains("furnace") && !path.contains("smoker") && !path.contains("torch") && !path.contains("fire") && !path.contains("candle") && !path.contains("respawn_anchor")) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPoweredMechanism(class_243 source, String soundId) {
        String path = AntiVanishModule.shortId(soundId);
        if (!path.contains("door") && !path.contains("trapdoor")) {
            return false;
        }
        class_2338 center = class_2338.method_49638((class_2374)source);
        long now = System.currentTimeMillis();
        for (int dy = -1; dy <= 1; ++dy) {
            boolean powered;
            class_2338 pos = center.method_10069(0, dy, 0);
            class_2680 state = AntiVanishModule.MC.field_1687.method_8320(pos);
            long key = pos.method_10063();
            boolean bl = powered = state.method_28498((class_2769)class_2741.field_12484) && (Boolean)state.method_11654((class_2769)class_2741.field_12484) != false;
            if (powered || AntiVanishModule.MC.field_1687.method_49803(pos)) {
                this.automatedMechanisms.put(key, now + 5000L);
                return true;
            }
            if (this.automatedMechanisms.getOrDefault(key, 0L) <= now) continue;
            return true;
        }
        return false;
    }

    private double sensorRangeSq() {
        double r = 64.0;
        return r * r;
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private static String soundId(class_2960 id) {
        return id == null ? "" : id.toString();
    }

    private static String shortId(String id) {
        return AntiVanishHeuristics.path(id);
    }

    private void resetRuntime() {
        this.observations.clear();
        this.knownPlayers.clear();
        this.detections.clear();
        this.signalCooldowns.clear();
        this.announceCooldowns.clear();
        this.announceTimes.clear();
        this.selfPlacedBlocks.clear();
        this.confirmedDepartures.clear();
        this.automatedMechanisms.clear();
        this.weakParticleBursts.clear();
        this.signals.clear();
        this.recentExplosions.clear();
        this.pendingVanishes.clear();
        this.hiddenSpells.clear();
        this.shard.clear();
        this.tickCounter = 0;
        this.lastLocalActionMs = 0L;
        this.lastContainerActivityMs = 0L;
        this.lastCriticalMs = 0L;
        this.criticalUntilMs = 0L;
        this.criticalWatcher = "";
        this.pendingChimes.clear();
        this.ghostId = null;
        this.ghostName = null;
        this.recentMessages.clear();
        this.listedSinceMs.clear();
        this.serverSendsLeaveMessages = false;
        this.hints.clear();
        this.heldHints.clear();
        this.tabMoves.clear();
        this.teamChanges.clear();
        this.tabNames.clear();
        this.arrivals.clear();
        this.quietUntilMs = System.currentTimeMillis() + 4000L;
    }

    @Environment(value=EnvType.CLIENT)
    private record Observation(ObservationType type, UUID profileId, int entityId, double x, double y, double z, String detail) {
        static Observation tabRemove(UUID id) {
            return new Observation(ObservationType.TAB_REMOVE, id, -1, 0.0, 0.0, 0.0, "");
        }

        static Observation tabHide(UUID id) {
            return new Observation(ObservationType.TAB_HIDE, id, -1, 0.0, 0.0, 0.0, "");
        }

        static Observation gamemode(UUID id, String mode) {
            return new Observation(ObservationType.GAMEMODE, id, -1, 0.0, 0.0, 0.0, mode);
        }

        static Observation playerLeft(String name) {
            return new Observation(ObservationType.PLAYER_LEFT, null, -1, 0.0, 0.0, 0.0, name);
        }

        static Observation systemChat(String text) {
            return new Observation(ObservationType.SYSTEM_CHAT, null, -1, 0.0, 0.0, 0.0, text);
        }

        static Observation entityMetadata(int id) {
            return new Observation(ObservationType.ENTITY_METADATA, null, id, 0.0, 0.0, 0.0, "");
        }

        static Observation explosion(class_243 center, float radius) {
            return new Observation(ObservationType.EXPLOSION, null, -1, center.field_1352, center.field_1351, center.field_1350, Float.toString(radius));
        }

        static Observation entitySound(int id, String sound) {
            return new Observation(ObservationType.ENTITY_SOUND, null, id, 0.0, 0.0, 0.0, sound);
        }

        static Observation position(ObservationType type, double x, double y, double z, String detail) {
            return new Observation(type, null, -1, x, y, z, detail);
        }

        class_243 position() {
            return new class_243(this.x, this.y, this.z);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Hint(HintKind kind, UUID id, String name, int rank, long atMs) {
    }

    @Environment(value=EnvType.CLIENT)
    private static enum HintKind {
        ORDER,
        TEAM_UP,
        TEAM_DOWN,
        TAB_IN,
        TAB_OUT;

    }

    @Environment(value=EnvType.CLIENT)
    private static enum ObservationType {
        TAB_REMOVE,
        TAB_HIDE,
        PLAYER_LEFT,
        SYSTEM_CHAT,
        ENTITY_METADATA,
        POSITIONAL_SOUND,
        ENTITY_SOUND,
        PARTICLE,
        EXPLOSION,
        GAMEMODE;

    }

    @Environment(value=EnvType.CLIENT)
    private record RegionProof(UUID id, int distance) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Detection {
        String name;
        String reason;
        int score;
        long expiresAt;

        Detection(String name, String reason, int score, long expiresAt) {
            this.name = name;
            this.reason = reason;
            this.score = score;
            this.expiresAt = expiresAt;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record HudEntry(String name, String reason) {
    }

    @Environment(value=EnvType.CLIENT)
    private record KnownPlayer(UUID uuid, String name) {
    }

    @Environment(value=EnvType.CLIENT)
    private record PendingVanish(UUID uuid, String name, int dueTick) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Signal(SignalType type, String subject, RegionProof proof, String reason, int weight, long timeMs) {
    }

    @Environment(value=EnvType.CLIENT)
    private static enum SignalType {
        VANISH,
        INVISIBLE,
        PARTICLE,
        SOUND;

    }

    @Environment(value=EnvType.CLIENT)
    private record RecentMessage(String text, long atMs) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class HiddenSpell {
        final UUID uuid;
        final String name;
        final boolean spectator;
        final String detail;
        final RegionProof proof;
        final int sinceTick;
        final long sinceMs = System.currentTimeMillis();
        boolean told;
        boolean watched;

        HiddenSpell(UUID uuid, String name, boolean spectator, String detail, RegionProof proof, int sinceTick) {
            this.uuid = uuid;
            this.name = name;
            this.spectator = spectator;
            this.detail = detail;
            this.proof = proof;
            this.sinceTick = sinceTick;
        }

        class_2561 how() {
            return this.spectator ? StaffListModule.switchedTo(ModuleChat.body(), class_1934.field_9219.method_8381()).build() : ModuleChat.body().danger("vanished").build();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record ExplosionEvent(class_243 center, double radius, long timeMs) {
    }
}

