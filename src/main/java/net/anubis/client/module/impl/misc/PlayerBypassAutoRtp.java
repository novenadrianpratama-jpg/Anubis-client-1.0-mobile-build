/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1923
 *  net.minecraft.class_1935
 *  net.minecraft.class_243
 *  net.minecraft.class_2535
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2824
 *  net.minecraft.class_2824$class_5908
 *  net.minecraft.class_2885
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_465
 *  net.minecraft.class_5903
 *  net.minecraft.class_5904
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 *  net.minecraft.class_7472
 *  net.minecraft.class_7923
 *  net.minecraft.class_8111
 *  net.minecraft.class_8143
 *  net.minecraft.class_9449
 */
package net.anubis.client.module.impl.misc;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.ListenerHandle;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.anubis.client.module.impl.misc.PlayerBypassModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.playerbypass.AutoRtpLoop;
import net.anubis.client.util.playerbypass.ChunkLightSnapshot;
import net.anubis.client.util.playerbypass.PlayerBypassLightTracker;
import net.anubis.client.util.playerbypass.RtpReply;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_1935;
import net.minecraft.class_243;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2885;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_465;
import net.minecraft.class_5903;
import net.minecraft.class_5904;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import net.minecraft.class_7472;
import net.minecraft.class_7923;
import net.minecraft.class_8111;
import net.minecraft.class_8143;
import net.minecraft.class_9449;

@Environment(value=EnvType.CLIENT)
final class PlayerBypassAutoRtp {
    private static final double TELEPORT_DISTANCE_SQ = 256.0;
    private static final long REPLY_WINDOW_MS = 5000L;
    private static final long STOP_REPLY_WINDOW_MS = 2500L;
    private static final long RESUME_GRACE_MS = 1000L;
    private static final long RESPAWN_GRACE_MS = 3000L;
    private static final long OFFLINE_LIMIT_MS = 30000L;
    private static final long MENU_CLICK_DELAY_MS = 300L;
    private static final long MENU_FILL_MS = 2500L;
    private static final long MENU_CLAIM_MS = 4000L;
    private static final int MAX_MENU_CLICKS = 3;
    private static final int MENU_NAMES_SHOWN = 10;
    private static final long LONG_COOLDOWN_MS = 60000L;
    private static final long BAR_REFRESH_MS = 2000L;
    private static final long NO_CHUNK = Long.MIN_VALUE;
    private static final long NEVER = Long.MIN_VALUE;
    private final PlayerBypassModule module;
    private final BooleanSetting enabled;
    private final StringSetting command;
    private final NumberSetting scanTime;
    private final NumberSetting delay;
    private final NumberSetting timeout;
    private final NumberSetting maxRtps;
    private final BooleanSetting stopOnSuspect;
    private final StringSetting menuItem;
    private final BooleanSetting sound;
    private final BooleanSetting showCoordinates;
    private final AutoRtpLoop loop = new AutoRtpLoop();
    private final LongOpenHashSet knownSuspects = new LongOpenHashSet();
    private ListenerHandle<TickEvent> tickHandle;
    private ListenerHandle<PacketReceiveEvent> packetHandle;
    private ListenerHandle<PacketSendEvent> sendHandle;
    private class_2535 link;
    private class_638 level;
    private class_243 anchor;
    private int startChunkX;
    private int startChunkZ;
    private boolean wasDead;
    private long offlineSince;
    private long lastRevision;
    private boolean lastTryLanded;
    private long commandSentAt = Long.MIN_VALUE;
    private boolean sendingCommand;
    private volatile long userActionAt = Long.MIN_VALUE;
    private int menuId = -1;
    private int ignoredMenuId = -1;
    private long menuSeenAt;
    private boolean menuClicked;
    private int menuClicks;
    private long lastMenuClickAt;
    private long noticedCooldownUntil;
    private String lastBar = "";
    private long lastBarAt;

    PlayerBypassAutoRtp(PlayerBypassModule module, BooleanSetting enabled, StringSetting command, NumberSetting scanTime, NumberSetting delay, NumberSetting timeout, NumberSetting maxRtps, BooleanSetting stopOnSuspect, StringSetting menuItem, BooleanSetting sound, BooleanSetting showCoordinates) {
        this.module = module;
        this.enabled = enabled;
        this.command = command;
        this.scanTime = scanTime;
        this.delay = delay;
        this.timeout = timeout;
        this.maxRtps = maxRtps;
        this.stopOnSuspect = stopOnSuspect;
        this.menuItem = menuItem;
        this.sound = sound;
        this.showCoordinates = showCoordinates;
    }

    void start() {
        long cooldown;
        if (this.loop.running()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        class_634 connection = mc.method_1562();
        if (player == null || mc.field_1687 == null || connection == null || !connection.method_48296().method_10758()) {
            this.say(ModuleChat.body().warn("Join a server first").text(", Auto RTP is off."));
            this.enabled.set(false);
            return;
        }
        long now = System.currentTimeMillis();
        AutoRtpLoop.Config config = this.config();
        this.loop.start(now, config);
        this.link = connection.method_48296();
        this.level = mc.field_1687;
        this.anchor = player.method_73189();
        this.startChunkX = player.method_24515().method_10263() >> 4;
        this.startChunkZ = player.method_24515().method_10260() >> 4;
        this.wasDead = player.method_29504();
        this.offlineSince = 0L;
        this.lastRevision = PlayerBypassLightTracker.visibilityRevision();
        this.lastTryLanded = false;
        this.noticedCooldownUntil = 0L;
        this.lastBar = "";
        this.resetMenu();
        this.rememberSuspects();
        if (this.tickHandle == null) {
            this.tickHandle = TickEvent.TYPE.listen(this, event -> this.tick());
        }
        if (this.packetHandle == null) {
            this.packetHandle = PacketReceiveEvent.TYPE.listen(this, event -> this.onPacket(event.packet));
        }
        if (this.sendHandle == null) {
            this.sendHandle = PacketSendEvent.TYPE.listen(this, event -> this.onPacketSent(event.packet));
        }
        ModuleChat.Body body = ModuleChat.body().good("Auto RTP started").text(": ").value("/" + this.command()).text(", ").value(PlayerBypassAutoRtp.seconds(config.scanMs())).text(" scan per spot, at least ").value(PlayerBypassAutoRtp.seconds(config.delayMs())).text(" between RTPs");
        if (config.maxRtps() > 0) {
            body.text(", max ").value(config.maxRtps());
        }
        if ((cooldown = this.loop.cooldownLeft(now)) > 0L) {
            this.noticedCooldownUntil = now + cooldown;
            body.text(", server cooldown ").value(PlayerBypassAutoRtp.seconds(cooldown)).text(" left");
            if (cooldown >= 60000L) {
                body.text(" (turn Player Bypass off and on to clear it)");
            }
        }
        this.say(body.text("."));
    }

    void stopByUser() {
        if (!this.loop.running()) {
            this.unlisten();
            return;
        }
        this.stop(ModuleChat.body().text("Auto RTP stopped after ").value(this.loop.landings()).text(PlayerBypassAutoRtp.rtpWord(this.loop.landings())).text("."));
    }

    void forgetCooldown() {
        this.loop.forgetCooldown();
    }

    private void stop(ModuleChat.Body why) {
        boolean was = this.loop.running() || this.tickHandle != null;
        this.loop.stop();
        this.unlisten();
        this.resetMenu();
        this.link = null;
        this.level = null;
        this.anchor = null;
        if (was && why != null) {
            this.say(why);
        }
    }

    private void stopWithRestartHint(ModuleChat.Body why) {
        this.stop(why.text(" Turn ").value("Auto RTP").text(" off and on to start again."));
    }

    private void unlisten() {
        if (this.tickHandle != null) {
            this.tickHandle.unregister();
        }
        if (this.packetHandle != null) {
            this.packetHandle.unregister();
        }
        if (this.sendHandle != null) {
            this.sendHandle.unregister();
        }
        this.tickHandle = null;
        this.packetHandle = null;
        this.sendHandle = null;
    }

    boolean baseSpotted(int blockX, int blockZ, boolean disconnecting) {
        if (!this.loop.running() || this.atStartSpot(blockX >> 4, blockZ >> 4)) {
            return false;
        }
        this.found("Base", blockX, blockZ, disconnecting);
        return true;
    }

    private void found(String what, int blockX, int blockZ, boolean disconnecting) {
        class_746 player = class_310.method_1551().field_1724;
        boolean teleporting = this.loop.phase() == AutoRtpLoop.Phase.TELEPORTING;
        boolean moved = player != null && this.anchor != null && (player.method_73183() != this.level || player.method_73189().method_1025(this.anchor) > 256.0);
        boolean pending = this.loop.awaitingTeleport() && player != null && !moved;
        int rtps = this.loop.landings() + (teleporting && moved ? 1 : 0);
        boolean showPos = this.showCoordinates.get();
        ModuleChat.Body body = ModuleChat.body().good(what + " found");
        if (showPos) {
            body.text(" at ").value(blockX + ", " + blockZ);
        }
        this.stopWithRestartHint(body.text(" after ").value(rtps).text(PlayerBypassAutoRtp.rtpWord(rtps) + ". Auto RTP stopped."));
        if (pending && !disconnecting) {
            this.say(ModuleChat.body().warn("Your /" + this.command() + " is still pending").text(": move a little to cancel it (most servers cancel a teleport when you move),").text(" or the server may still teleport you away."));
        }
        ChallengeToast.addOrUpdate(new class_1799((class_1935)(what.equals("Base") ? class_1802.field_8575 : class_1802.field_27070)), what + " Found - Auto RTP", (class_2561)class_2561.method_43470((String)(showPos ? "at " + blockX + ", " + blockZ : "after " + rtps + PlayerBypassAutoRtp.rtpWord(rtps))), this.sound.get());
        if (this.sound.get()) {
            class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4758((class_3414)class_3417.field_14709, (float)1.0f));
        }
    }

    private void tick() {
        String pause;
        long suspect;
        boolean levelChanged;
        if (!this.loop.running()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        class_634 connection = mc.method_1562();
        long now = System.currentTimeMillis();
        if (this.link == null || !this.link.method_10758() || connection != null && connection.method_48296() != this.link) {
            this.stopWithRestartHint(ModuleChat.body().warn("Disconnected").text(", Auto RTP stopped."));
            return;
        }
        if (player == null || mc.field_1687 == null || connection == null || mc.field_1761 == null) {
            if (this.offlineSince == 0L) {
                this.offlineSince = now;
            }
            if (now - this.offlineSince > 30000L) {
                this.stopWithRestartHint(ModuleChat.body().warn("No world for 30s").text(", Auto RTP stopped."));
            }
            return;
        }
        this.offlineSince = 0L;
        AutoRtpLoop.Config config = this.config();
        long revision = PlayerBypassLightTracker.visibilityRevision();
        if (revision != this.lastRevision) {
            this.lastRevision = revision;
            this.loop.dataArrived(now);
        }
        if (player.method_29504()) {
            if (!this.wasDead) {
                this.wasDead = true;
                this.loop.interrupt(now, config);
                this.resetMenu();
            }
            this.loop.holdUntil(now + 3000L);
            this.bar(now, this.label(config) + " - paused, you died", now - this.lastBarAt >= 2000L);
            return;
        }
        if (this.wasDead) {
            this.wasDead = false;
            this.level = mc.field_1687;
            this.anchor = player.method_73189();
            this.loop.holdUntil(now + 3000L);
        }
        boolean bl = levelChanged = mc.field_1687 != this.level;
        if (levelChanged) {
            this.level = mc.field_1687;
            this.knownSuspects.clear();
        }
        if (levelChanged || this.anchor != null && player.method_73189().method_1025(this.anchor) > 256.0) {
            switch (this.loop.phase()) {
                case TELEPORTING: {
                    this.land(player, now, config);
                    break;
                }
                case SCANNING: {
                    this.anchor = player.method_73189();
                    this.loop.relanded(now, config);
                    break;
                }
                default: {
                    if (this.loop.landedLate(now, config)) {
                        this.anchor = player.method_73189();
                        this.closeMenu(player);
                        this.resetMenu();
                        break;
                    }
                    if (!levelChanged) break;
                    this.stopWithRestartHint(ModuleChat.body().warn("World changed").text(", Auto RTP stopped."));
                    return;
                }
            }
        }
        if (this.stopOnSuspect.get() && !levelChanged && (suspect = this.newSuspectNear(mc, player)) != Long.MIN_VALUE) {
            this.found("Suspect", (class_1923.method_8325((long)suspect) << 4) + 8, (class_1923.method_8332((long)suspect) << 4) + 8, false);
            return;
        }
        if (this.handleMenu(mc, player, now, config)) {
            return;
        }
        String string = pause = mc.field_1755 != null && !this.ourMenuOpen(mc, player) ? "a menu is open" : null;
        if (pause != null) {
            this.loop.holdUntil(now + 1000L);
        }
        switch (this.loop.tick(now, config)) {
            case SEND: {
                this.send(player, connection, now);
                break;
            }
            case TIMED_OUT: {
                this.lastTryLanded = false;
                this.closeMenu(player);
                break;
            }
            case SCAN_CLEAR: {
                this.lastTryLanded = true;
                break;
            }
            case LIMIT: {
                this.stopWithRestartHint(ModuleChat.body().text("Max RTPs reached (").value(config.maxRtps()).text("), no base found. Auto RTP stopped."));
                return;
            }
            case GAVE_UP: {
                this.closeMenu(player);
                this.stopWithRestartHint(ModuleChat.body().warn("No teleport after 5 tries").text(", check ").value("RTP Command").text(" and ").value("Menu Item").text(". Auto RTP stopped."));
                return;
            }
        }
        this.progress(now, pause, config);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void send(class_746 player, class_634 connection, long now) {
        this.anchor = player.method_73189();
        this.resetMenu();
        this.rememberSuspects();
        this.commandSentAt = now;
        this.sendingCommand = true;
        try {
            connection.method_45730(this.command());
        }
        finally {
            this.sendingCommand = false;
        }
    }

    private void land(class_746 player, long now, AutoRtpLoop.Config config) {
        this.anchor = player.method_73189();
        this.closeMenu(player);
        this.resetMenu();
        this.loop.landed(now, config);
    }

    private void onPacket(class_2596<?> packet) {
        if (!this.loop.running()) {
            return;
        }
        if (packet instanceof class_8143) {
            class_8143 damage = (class_8143)packet;
            this.onDamage(damage);
        } else if (packet instanceof class_7439) {
            class_7439 chat = (class_7439)packet;
            this.onServerText(chat.comp_763().getString(), !chat.comp_906());
        } else if (packet instanceof class_5904) {
            class_5904 title = (class_5904)packet;
            this.onServerText(title.comp_2281().getString(), false);
        } else if (packet instanceof class_5903) {
            class_5903 subtitle = (class_5903)packet;
            this.onServerText(subtitle.comp_2280().getString(), false);
        }
    }

    private void onPacketSent(class_2596<?> packet) {
        class_2824 interact;
        if (this.sendingCommand) {
            return;
        }
        if (packet instanceof class_2885 || packet instanceof class_7472 || packet instanceof class_9449 || packet instanceof class_2824 && PlayerBypassAutoRtp.isInteraction(interact = (class_2824)packet)) {
            this.userActionAt = System.currentTimeMillis();
        }
    }

    private static boolean isInteraction(class_2824 packet) {
        final boolean[] interaction = new boolean[]{false};
        packet.method_34209(new class_2824.class_5908(){

            public void method_34219(class_1268 hand) {
                interaction[0] = true;
            }

            public void method_34220(class_1268 hand, class_243 location) {
                interaction[0] = true;
            }

            public void method_34218() {
            }
        });
        return interaction[0];
    }

    private void onServerText(String raw, boolean chatLine) {
        boolean teleporting;
        long now = System.currentTimeMillis();
        boolean bl = teleporting = this.loop.phase() == AutoRtpLoop.Phase.TELEPORTING;
        if (!teleporting && !this.loop.sentWithin(now, 5000L)) {
            return;
        }
        if (chatLine && RtpReply.looksLikePlayerChat(raw, PlayerBypassAutoRtp.otherPlayerNames())) {
            return;
        }
        RtpReply reply = RtpReply.parse(raw);
        boolean fresh = this.loop.sentWithin(now, 2500L);
        switch (reply.kind()) {
            case COOLDOWN: {
                this.loop.cooldown(now, reply.millis());
                this.noticeCooldown(now, reply.millis());
                break;
            }
            case WARMUP: {
                this.loop.warmup(now, reply.millis(), this.config());
                if (reply.nextUseMs() < 0L) break;
                this.loop.nextUseIn(now, reply.nextUseMs());
                this.noticeCooldown(now, reply.nextUseMs());
                break;
            }
            case CANCELLED: {
                this.loop.cancelled(now);
                break;
            }
            case UNKNOWN_COMMAND: {
                if (!fresh) break;
                this.stopWithRestartHint(ModuleChat.body().warn("The server does not know ").value("/" + this.command()).text(", check ").value("RTP Command").text(". Auto RTP stopped."));
                break;
            }
            case NO_PERMISSION: {
                if (!fresh) break;
                this.stopWithRestartHint(ModuleChat.body().warn("No permission for ").value("/" + this.command()).text(". Auto RTP stopped."));
                break;
            }
            case COMBAT: {
                if (!teleporting || !fresh) break;
                this.stopWithRestartHint(ModuleChat.body().danger("In combat").text(", Auto RTP stopped."));
                break;
            }
        }
    }

    private void noticeCooldown(long now, long ms) {
        long until = now + ms;
        if (ms >= 60000L && until > this.noticedCooldownUntil + 5000L) {
            this.noticedCooldownUntil = until;
            this.say(ModuleChat.body().text("Server cooldown ").value(PlayerBypassAutoRtp.seconds(ms)).text(", Auto RTP waits."));
        }
    }

    private static List<String> otherPlayerNames() {
        class_310 mc = class_310.method_1551();
        class_634 connection = mc.method_1562();
        if (connection == null) {
            return List.of();
        }
        class_746 self = mc.field_1724;
        UUID selfId = self == null ? null : self.method_5667();
        String selfName = self == null ? null : self.method_7334().name();
        ArrayList<String> names = new ArrayList<String>();
        for (class_640 info : connection.method_2880()) {
            GameProfile profile = info.method_2966();
            if (profile == null || profile.name() == null || Objects.equals(profile.id(), selfId) || profile.name().equalsIgnoreCase(selfName)) continue;
            names.add(profile.name());
        }
        return names;
    }

    private void onDamage(class_8143 packet) {
        boolean byPlayer;
        class_1297 cause;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || packet.comp_1267() != player.method_5628()) {
            return;
        }
        class_1297 class_12972 = cause = packet.comp_1269() >= 0 ? mc.field_1687.method_8469(packet.comp_1269()) : null;
        if (cause == player) {
            return;
        }
        boolean bl = byPlayer = cause instanceof class_1657 || packet.comp_1268().method_40225(class_8111.field_42320) || packet.comp_1268().method_40225(class_8111.field_42332);
        if (!byPlayer) {
            return;
        }
        ModuleChat.Body body = ModuleChat.body().danger("Hit by ");
        if (cause instanceof class_1657) {
            class_1657 attacker = (class_1657)cause;
            body.name(attacker.method_7334().name());
        } else {
            body.text("a player");
        }
        this.stopWithRestartHint(body.text(", Auto RTP stopped (combat)."));
    }

    private boolean handleMenu(class_310 mc, class_746 player, long now, AutoRtpLoop.Config config) {
        boolean loading;
        if (this.loop.phase() != AutoRtpLoop.Phase.TELEPORTING || !PlayerBypassAutoRtp.containerOpen(mc, player)) {
            return false;
        }
        class_1703 menu = player.field_7512;
        if (menu.field_7763 != this.menuId) {
            if (menu.field_7763 == this.ignoredMenuId) {
                return false;
            }
            if (!this.menuMayBeOurs(now)) {
                this.ignoredMenuId = menu.field_7763;
                return false;
            }
            if (this.menuClicks >= 3) {
                this.stopWithRestartHint(ModuleChat.body().warn("The RTP menu keeps opening").text(", Auto RTP stopped."));
                return true;
            }
            this.menuId = menu.field_7763;
            this.menuSeenAt = now;
            this.menuClicked = false;
            this.loop.extend(now, config);
        }
        if (this.menuClicked || now - this.menuSeenAt < 300L) {
            return false;
        }
        ArrayList<String> names = new ArrayList<String>();
        String wanted = this.menuItem.get().trim();
        int slot = PlayerBypassAutoRtp.findSlot(menu, wanted, names);
        boolean bl = loading = now - this.menuSeenAt < 2500L;
        if (wanted.isEmpty()) {
            if (names.isEmpty() && loading) {
                return false;
            }
            this.stopWithRestartHint(ModuleChat.body().warn("The RTP command opened a menu").text(", set ").value("Menu Item").text(" to the item to click").text(PlayerBypassAutoRtp.nameList(names)).text(". Auto RTP stopped."));
            return true;
        }
        if (slot < 0) {
            if (loading) {
                return false;
            }
            this.stopWithRestartHint(ModuleChat.body().warn("No \"" + wanted + "\" in the RTP menu").text(PlayerBypassAutoRtp.nameList(names)).text(". Auto RTP stopped."));
            return true;
        }
        mc.field_1761.method_2906(menu.field_7763, slot, 0, class_1713.field_7790, (class_1657)player);
        this.menuClicked = true;
        ++this.menuClicks;
        this.lastMenuClickAt = now;
        this.loop.extend(now, config);
        return false;
    }

    private boolean menuMayBeOurs(long now) {
        if (this.commandSentAt == Long.MIN_VALUE || this.userActionAt >= this.commandSentAt) {
            return false;
        }
        if (now - this.commandSentAt <= 4000L) {
            return true;
        }
        return this.menuClicks > 0 && now - this.lastMenuClickAt <= 4000L;
    }

    private static int findSlot(class_1703 menu, String wanted, List<String> names) {
        ArrayList<class_1735> slots = new ArrayList<class_1735>();
        ArrayList<String> slotNames = new ArrayList<String>();
        ArrayList<String> slotIds = new ArrayList<String>();
        for (class_1735 slot : menu.field_7761) {
            if (slot.field_7871 instanceof class_1661) break;
            class_1799 stack = slot.method_7677();
            if (stack.method_7960()) continue;
            String name = RtpReply.normalize(stack.method_7964().getString());
            slots.add(slot);
            slotNames.add(name);
            slotIds.add(class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832());
            if (name.isEmpty() || names.contains(name)) continue;
            names.add(name);
        }
        for (String part : wanted.split(",")) {
            String text = RtpReply.normalize(part);
            if (text.isEmpty()) continue;
            String id = text.replace(' ', '_');
            for (int i = 0; i < slots.size(); ++i) {
                if (!((String)slotNames.get(i)).contains(text) && !((String)slotIds.get(i)).equals(id)) continue;
                return ((class_1735)slots.get((int)i)).field_7874;
            }
        }
        return -1;
    }

    private static String nameList(List<String> names) {
        if (names.isEmpty()) {
            return "";
        }
        List<String> shown = names.size() > 10 ? names.subList(0, 10) : names;
        return " (items: " + String.join((CharSequence)", ", shown) + (names.size() > shown.size() ? ", ..." : "") + ")";
    }

    private static boolean containerOpen(class_310 mc, class_746 player) {
        return player.field_7512 != player.field_7498 && mc.field_1755 instanceof class_465;
    }

    private boolean ourMenuOpen(class_310 mc, class_746 player) {
        return this.menuId >= 0 && PlayerBypassAutoRtp.containerOpen(mc, player) && player.field_7512.field_7763 == this.menuId;
    }

    private void closeMenu(class_746 player) {
        if (this.menuId >= 0 && player.field_7512 != player.field_7498 && player.field_7512.field_7763 == this.menuId) {
            player.method_7346();
        }
    }

    private void resetMenu() {
        this.menuId = -1;
        this.ignoredMenuId = -1;
        this.menuClicked = false;
        this.menuClicks = 0;
    }

    private void rememberSuspects() {
        this.knownSuspects.clear();
        LongIterator longIterator = PlayerBypassLightTracker.snapshots().keySet().iterator();
        while (longIterator.hasNext()) {
            long key = (Long)longIterator.next();
            if (!PlayerBypassLightTracker.isSuspect(key)) continue;
            this.knownSuspects.add(key);
        }
    }

    private long newSuspectNear(class_310 mc, class_746 player) {
        int radius = mc.field_1690.method_38521() + 1;
        int centerX = player.method_24515().method_10263() >> 4;
        int centerZ = player.method_24515().method_10260() >> 4;
        for (Long2ObjectMap.Entry entry : PlayerBypassLightTracker.snapshots().long2ObjectEntrySet()) {
            long key = entry.getLongKey();
            if (this.knownSuspects.contains(key) || !PlayerBypassLightTracker.isSuspect(key) || !PlayerBypassLightTracker.isFlagged(key, (ChunkLightSnapshot)entry.getValue())) continue;
            int chunkX = class_1923.method_8325((long)key);
            int chunkZ = class_1923.method_8332((long)key);
            if (Math.abs(chunkX - centerX) > radius || Math.abs(chunkZ - centerZ) > radius || this.atStartSpot(chunkX, chunkZ)) continue;
            return key;
        }
        return Long.MIN_VALUE;
    }

    private boolean atStartSpot(int chunkX, int chunkZ) {
        if (this.loop.landings() > 0) {
            return false;
        }
        int radius = class_310.method_1551().field_1690.method_38521() + 1;
        return Math.abs(chunkX - this.startChunkX) <= radius && Math.abs(chunkZ - this.startChunkZ) <= radius;
    }

    private void progress(long now, String pause, AutoRtpLoop.Config config) {
        Object text;
        String label = this.label(config);
        if (pause != null) {
            text = label + " - paused, " + pause;
        } else {
            long next = this.loop.nextSendIn(now);
            text = switch (this.loop.phase()) {
                case AutoRtpLoop.Phase.WAITING -> {
                    if (this.loop.cooldownLeft(now) > 0L) {
                        yield label + " - server cooldown, next in " + PlayerBypassAutoRtp.seconds(next);
                    }
                    if (this.loop.landings() == 0 && this.loop.sent() == 0) {
                        yield label + " - starting" + (String)(next >= 1000L ? " in " + PlayerBypassAutoRtp.seconds(next) : "");
                    }
                    if (!this.lastTryLanded) {
                        yield label + " - no teleport, retry in " + PlayerBypassAutoRtp.seconds(next);
                    }
                    if (this.loop.settling(now)) {
                        yield label + " - no base yet, chunks still loading";
                    }
                    yield label + " - no base, next in " + PlayerBypassAutoRtp.seconds(next);
                }
                case AutoRtpLoop.Phase.TELEPORTING -> label + " - waiting for the teleport";
                case AutoRtpLoop.Phase.SCANNING -> label + " - scanning, " + PlayerBypassAutoRtp.seconds(this.loop.scanLeft(now)) + " left";
                default -> "";
            };
        }
        boolean refresh = this.loop.phase() != AutoRtpLoop.Phase.TELEPORTING && now - this.lastBarAt >= 2000L;
        this.bar(now, (String)text, refresh);
    }

    private String label(AutoRtpLoop.Config config) {
        return "Auto RTP #" + this.loop.sent() + (String)(config.maxRtps() > 0 ? "/" + config.maxRtps() : "");
    }

    private void bar(long now, String text, boolean refresh) {
        if (text.isEmpty() || text.equals(this.lastBar) && !refresh) {
            return;
        }
        this.lastBar = text;
        this.lastBarAt = now;
        ModuleChat.actionBarAlways(this.module, ModuleChat.body().text(text));
    }

    private AutoRtpLoop.Config config() {
        return new AutoRtpLoop.Config(PlayerBypassAutoRtp.millis(this.scanTime), PlayerBypassAutoRtp.millis(this.delay), PlayerBypassAutoRtp.millis(this.timeout), this.maxRtps.getInt());
    }

    private String command() {
        String text = this.command.get().trim();
        while (text.startsWith("/")) {
            text = text.substring(1).trim();
        }
        return text.isEmpty() ? "rtp" : text;
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.sendAlways(this.module, body);
    }

    private static long millis(NumberSetting setting) {
        return Math.round((double)setting.get() * 1000.0);
    }

    private static String seconds(long ms) {
        long s = (ms + 999L) / 1000L;
        return s >= 120L ? String.format(Locale.ROOT, "%dm %ds", s / 60L, s % 60L) : s + "s";
    }

    private static String rtpWord(int count) {
        return count == 1 ? " RTP" : " RTPs";
    }
}

