/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1937
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
 *  net.minecraft.class_746
 *  net.minecraft.class_7472
 *  net.minecraft.class_8042
 *  net.minecraft.class_9449
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.misc;

import imgui.ImDrawList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.Render2DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.HudPanel;
import net.anubis.client.gui.ScreenText;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.NameFilter;
import net.anubis.client.util.donut.DonutGoliaths;
import net.anubis.client.util.staff.ShardTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_268;
import net.minecraft.class_2703;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_7472;
import net.minecraft.class_8042;
import net.minecraft.class_9449;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class MediaBaseFinderModule
extends ClientModule
implements HudOverlayProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String MEDIA_PREFIX = "\ud83d\udcf9";
    private static final String MEDIA_TEAM = "09-";
    private static final int SETTLE_TICKS = 60;
    private static final int SCAN_TICKS = 10;
    private static final int MAX_LISTED = 8;
    private static final long RTP_WINDOW_NANOS = 60000000000L;
    private final BooleanSetting reportEmpty = this.addSetting("report-empty", new BooleanSetting("Report Empty Goliaths", true));
    private final BooleanSetting statusBox = this.addSetting("status-box", new BooleanSetting("Status Box", true));
    private final NumberSetting scale = this.addSetting("scale", new NumberSetting("Scale", 1.0f, 0.5f, 3.0f).step(0.05f));
    private final NumberSetting opacity = this.addSetting("opacity", HudPanel.opacitySetting());
    private final HudPanel panel = new HudPanel(this.addSetting("pos-x", HudPanel.coordinate("X")), this.addSetting("pos-y", HudPanel.coordinate("Y")), this.scale, this.opacity);
    private final ShardTracker shard = new ShardTracker();
    private final ScreenText screenText = new ScreenText(0xFF55FF, 3.0f);
    private final Queue<UUID> modeUpdates = new ConcurrentLinkedQueue<UUID>();
    private final Set<String> proven = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    private Set<String> here = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    private String goliath = "\u2014";
    private int settle;
    private int ticks;
    private boolean reported;
    private boolean afterRtp;
    private volatile long rtpAt;

    public MediaBaseFinderModule() {
        super("media-base-finder", "Media Base Finder", "Tells you which DonutSMP media are on the goliath you land on (after /rtp), where their base most likely is.", ModuleCategory.BASE_RAID);
        this.usesNotifications();
        this.scale.visibleWhen(this.statusBox::get);
        this.opacity.visibleWhen(this.statusBox::get);
    }

    @Override
    protected void onEnable() {
        this.arrive(class_310.method_1551());
    }

    @Override
    protected void onDisable() {
        this.shard.clear();
        this.modeUpdates.clear();
        this.proven.clear();
        this.here = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    }

    private void arrive(class_310 mc) {
        this.modeUpdates.clear();
        this.proven.clear();
        this.here = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.settle = 60;
        this.reported = false;
        this.goliath = MediaBaseFinderModule.label(mc);
        long sent = this.rtpAt;
        this.afterRtp = sent != 0L && System.nanoTime() - sent < 60000000000L;
    }

    @EventHandler
    public void onPacketSend(PacketSendEvent event) {
        String command;
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_7472) {
            class_7472 plain = (class_7472)class_25962;
            v0 = plain.comp_808();
        } else {
            class_25962 = event.packet;
            if (class_25962 instanceof class_9449) {
                class_9449 signed = (class_9449)class_25962;
                v0 = signed.comp_2532();
            } else {
                v0 = command = null;
            }
        }
        if (command == null) {
            return;
        }
        String text = command.trim().toLowerCase(Locale.ROOT);
        if (text.equals("rtp") || text.startsWith("rtp ")) {
            this.rtpAt = System.nanoTime();
        }
    }

    @EventHandler(stage=Stage.POST)
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_634 connection = mc.method_1562();
        class_746 player = mc.field_1724;
        if (connection == null || player == null || mc.field_1687 == null) {
            this.shard.clear();
            return;
        }
        if (this.shard.moved(mc)) {
            this.arrive(mc);
        }
        this.drainModeUpdates(connection);
        if (this.settle > 0) {
            --this.settle;
            return;
        }
        if (this.reported && ++this.ticks % 10 != 0) {
            return;
        }
        Set<String> found = this.scan(mc, connection, player);
        this.goliath = MediaBaseFinderModule.label(mc);
        if (!this.reported) {
            this.reported = true;
            this.here = found;
            this.report(found);
            return;
        }
        ArrayList<String> arrived = new ArrayList<String>();
        for (String name : found) {
            if (this.here.contains(name)) continue;
            arrived.add(name);
        }
        this.here = found;
        if (!arrived.isEmpty()) {
            this.announce(arrived, found.size(), false);
        }
    }

    private Set<String> scan(class_310 mc, class_634 connection, class_746 player) {
        TreeSet<String> found = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        String self = player.method_7334().name();
        for (class_268 team : mc.field_1687.method_8428().method_1159()) {
            if (!MediaBaseFinderModule.isMedia(team)) continue;
            for (String member : team.method_1204()) {
                if (member.equalsIgnoreCase(self) || connection.method_2874(member) != null && !this.proven.contains(member)) continue;
                found.add(member);
            }
        }
        return found;
    }

    private static boolean isMedia(class_268 team) {
        return team.method_1197().startsWith(MEDIA_TEAM) || team.method_1144().getString().contains(MEDIA_PREFIX);
    }

    @EventHandler
    public void onPacketReceiveNetty(PacketReceiveNettyEvent event) {
        this.collect(event.packet);
    }

    private void collect(class_2596<?> packet) {
        if (packet instanceof class_8042) {
            class_8042 bundle = (class_8042)packet;
            for (class_2596 inner : bundle.method_48324()) {
                this.collect(inner);
            }
            return;
        }
        if (!(packet instanceof class_2703)) {
            return;
        }
        class_2703 info = (class_2703)packet;
        if (!info.method_46327().contains(class_2703.class_5893.field_29137) || info.method_46327().contains(class_2703.class_5893.field_29136)) {
            return;
        }
        for (class_2703.class_2705 entry : info.method_46329()) {
            if (entry == null || entry.comp_1106() == null) continue;
            this.modeUpdates.offer(entry.comp_1106());
        }
    }

    private void drainModeUpdates(class_634 connection) {
        UUID id;
        while ((id = this.modeUpdates.poll()) != null) {
            String name;
            class_640 info = connection.method_2871(id);
            if (info == null || info.method_2955() == null || !MediaBaseFinderModule.isMedia(info.method_2955()) || !this.proven.add(name = info.method_2966().name())) continue;
            LOGGER.info("[Media Base Finder] game mode update places {} on {}", (Object)name, (Object)this.goliath);
        }
    }

    private void report(Set<String> found) {
        this.rtpAt = 0L;
        LOGGER.info("[Media Base Finder] {}: {} media {}", new Object[]{this.goliath, found.size(), found});
        if (found.isEmpty()) {
            if (this.reportEmpty.get()) {
                ModuleChat.send((ClientModule)this, ModuleChat.body().text("No media on ").name(this.goliath).text("."));
            }
            return;
        }
        this.announce(new ArrayList<String>(found), found.size(), true);
    }

    private void announce(List<String> names, int total, boolean summary) {
        class_310 mc = class_310.method_1551();
        String list = MediaBaseFinderModule.names(names);
        String area = MediaBaseFinderModule.area(mc);
        String bases = total + (total == 1 ? " media base" : " media bases") + (String)(area == null ? "" : " within " + area + " blocks");
        if (summary) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text(bases + " on ").name(this.goliath).text(": ").name(list));
        } else {
            ModuleChat.send((ClientModule)this, ModuleChat.body().name(list).text(" joined ").name(this.goliath).text(", now " + bases + "."));
            LOGGER.info("[Media Base Finder] {} joined {}, now {}", new Object[]{list, this.goliath, total});
        }
        if (summary) {
            LOGGER.info("[Media Base Finder] screen text: afterRtp={} notifications={}", (Object)this.afterRtp, (Object)this.notificationsOn());
        }
        if (!(summary && this.afterRtp && this.notificationsOn())) {
            return;
        }
        this.screenText.show(total + (total == 1 ? " Media Base" : " Media Bases"), (String)(area == null ? "on this goliath" : "within " + area + " blocks"));
        mc.method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_14622, (float)1.4f));
    }

    private static String area(class_310 mc) {
        String l;
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1687.method_27983() != class_1937.field_25179) {
            return null;
        }
        DonutGoliaths.Goliath at = DonutGoliaths.at(mc.field_1724.method_23317(), mc.field_1724.method_23321());
        if (at == null) {
            return null;
        }
        int[] c = DonutGoliaths.CELLS[at.region()][at.index()];
        String w = MediaBaseFinderModule.blocks(c[2] - c[0]);
        return w.equals(l = MediaBaseFinderModule.blocks(c[3] - c[1])) ? w : w + " \u00d7 " + l;
    }

    private static String blocks(int squares) {
        int value = squares * 12500;
        return value % 1000 == 0 ? value / 1000 + "k" : String.format(Locale.ROOT, "%.1fk", (double)value / 1000.0);
    }

    private static String names(List<String> names) {
        StringBuilder out = new StringBuilder();
        int shown = Math.min(names.size(), 8);
        for (int i = 0; i < shown; ++i) {
            out.append(i == 0 ? "" : ", ").append(NameFilter.apply(names.get(i)));
        }
        if (names.size() > shown) {
            out.append(" +").append(names.size() - shown);
        }
        return out.toString();
    }

    private static String label(class_310 mc) {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1687.method_27983() != class_1937.field_25179) {
            return "\u2014";
        }
        DonutGoliaths.Goliath at = DonutGoliaths.at(mc.field_1724.method_23317(), mc.field_1724.method_23321());
        return at == null ? "Off the map" : at.label();
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled() && this.statusBox.get() && class_310.method_1551().field_1724 != null;
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        ArrayList<HudPanel.Line> lines = new ArrayList<HudPanel.Line>();
        if (this.settle > 0) {
            lines.add(new HudPanel.Line("Checking\u2026", -19648));
        } else if (this.here.isEmpty()) {
            lines.add(new HudPanel.Line("None here", -6052949));
        } else {
            int shown = 0;
            for (String name : this.here) {
                if (shown++ == 8) {
                    lines.add(new HudPanel.Line("+" + (this.here.size() - 8) + " more", -6052949));
                    break;
                }
                lines.add(new HudPanel.Line(HudPanel.face(name), NameFilter.apply(name), -1, "", -6052949));
            }
        }
        String title = this.here.size() > 1 ? "MEDIA BASES ON YOUR GOLIATH" : "MEDIA BASE ON YOUR GOLIATH";
        this.panel.card(dl, title, "", -1, lines, "staff_list", screenW, screenH, 1.0f, 220.0f);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        this.screenText.render(event.graphics);
    }
}

