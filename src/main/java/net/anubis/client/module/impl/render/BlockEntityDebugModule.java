/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12179
 *  net.minecraft.class_12180
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2487
 *  net.minecraft.class_2512
 *  net.minecraft.class_2520
 *  net.minecraft.class_2561
 *  net.minecraft.class_2568
 *  net.minecraft.class_2568$class_10613
 *  net.minecraft.class_2596
 *  net.minecraft.class_2626
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Map;
import java.util.Set;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.esp.BlockLayer;
import net.anubis.client.esp.EspStyle;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BlockColorListSetting;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.util.BlockEntityLeakTracker;
import net.anubis.client.util.LightLeakScanner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class BlockEntityDebugModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int SETTINGS_VERSION = 1;
    private static final int SUMMARY_TICKS = 1200;
    private static final float FILL_ALPHA = 0.8f;
    private static final int MAX_CHAT_PER_TICK = 5;
    private static final int MAX_NBT_CHARS = 200;
    private static final Set<String> NEW_IN_TABLE = Set.of("minecraft:jukebox", "minecraft:piston", "minecraft:sticky_piston");
    private final ModeSetting sDetection = this.addSetting("detection", new ModeSetting("Detection", "All", "All", "Below Zero", "Below -20", "Below -30"));
    private final BooleanSetting sBlockFilter = this.addSetting("block-filter", new BooleanSetting("Block Filter", false));
    private final BooleanSetting sChat = this.addSetting("chat", new BooleanSetting("Chat Notifications", false));
    private final BooleanSetting sShowNbt = this.addSetting("show-nbt", new BooleanSetting("Show NBT", true));
    private final BooleanSetting sHints = this.addSetting("hidden-hints", new BooleanSetting("Hidden Block Hints", true));
    private final BooleanSetting sLightLeaks = this.addSetting("light-leaks", new BooleanSetting("Light Leaks", true));
    private final EspStyle style = new EspStyle((x$0, x$1) -> this.addSetting(x$0, x$1), "Boxes", 0.8f, 256.0f);
    private final BooleanSetting sTracers = this.addSetting("tracers", new BooleanSetting("Tracers", true));
    private final BooleanSetting sCustomTracerColor = this.addSetting("custom-tracer-color", new BooleanSetting("Custom Tracer Color", false));
    private final ColorSetting sTracerColor = this.addSetting("tracer-color", new ColorSetting("Tracer Color", -1));
    private final BlockColorListSetting sBlockColors = this.addSetting(BlockColorListSetting.createForBlockEntityDebug());
    private final BlockLayer layer = new BlockLayer(new BlockLayer.Palette(){

        @Override
        public int color(Object kind) {
            return BlockEntityDebugModule.this.sBlockColors.colorArgb((class_2248)kind);
        }

        @Override
        public int tracerColor(Object kind) {
            return BlockEntityDebugModule.this.tracerColorOf((class_2248)kind);
        }
    }, () -> class_310.method_1551().field_1687 == null ? null : class_310.method_1551().field_1687.method_27983());
    private final LongOpenHashSet spawners = new LongOpenHashSet();
    private final LightLeakScanner lightScanner = new LightLeakScanner();
    private int ticks;
    private String lastSummary = "";
    private int seenGeneration = -1;
    private Set<String> scannedIds = Set.of();

    public BlockEntityDebugModule() {
        super("block-entity-debug", "Block Entity Debug", "Renders every leaked block entity as ESP (always-on Netty capture), plus blocks the server hides but still gives away through chest/crafter events, sounds and light.", ModuleCategory.BASE_RAID);
        this.sShowNbt.visibleWhen(this.sChat::get);
        this.sLightLeaks.visibleWhen(this.sHints::get);
        this.sHints.onChange(this::resync);
        this.sLightLeaks.onChange(this::restartLightScan);
        this.sCustomTracerColor.visibleWhen(this.sTracers::get);
        this.sTracerColor.visibleWhen(() -> this.sTracers.get() && this.sCustomTracerColor.get());
        this.sDetection.onChange(this::resync);
        this.sBlockFilter.onChange(this::resync);
        this.sBlockColors.onChange(this::onBlockColorsChanged);
    }

    @Override
    public int settingsVersion() {
        return 1;
    }

    @Override
    public void migrateSettings(int savedVersion) {
        this.style.shape.set("Boxes");
        this.style.boxSize.set(1.0f);
        this.style.fillAlpha.set(0.8f);
        this.sBlockColors.resetColors();
        int plain = BlockColorListSetting.rgbaToArgb(this.sBlockColors.defaultColor());
        boolean filtered = this.sBlockFilter.get();
        for (BlockColorListSetting.BlockEntry entry : this.sBlockColors.entries()) {
            if (BlockColorListSetting.rgbaToArgb(entry.color()) == plain || filtered && !NEW_IN_TABLE.contains(entry.id())) continue;
            this.sBlockColors.select(entry);
        }
    }

    private int tracerColorOf(class_2248 block) {
        int own;
        int n = own = this.sBlockColors.isEnabled(block) ? this.sBlockColors.tracerColorArgb(block) : this.sBlockColors.colorArgb(block);
        if (own == 0) {
            return 0;
        }
        return this.sCustomTracerColor.get() ? this.sTracerColor.argb() : own;
    }

    private void onBlockColorsChanged() {
        if (this.isEnabled() && this.sBlockFilter.get() && !this.sBlockColors.enabledIds().equals(this.scannedIds)) {
            this.resync();
        }
    }

    @Override
    protected void onEnable() {
        BlockEntityLeakTracker.recordAdditions(true);
        this.resync();
        this.restartLightScan();
    }

    @Override
    protected void onDisable() {
        BlockEntityLeakTracker.recordAdditions(false);
        this.lightScanner.clear();
        this.layer.clear();
        this.spawners.clear();
    }

    private void restartLightScan() {
        this.lightScanner.clear();
        class_310 mc = class_310.method_1551();
        if (!this.isEnabled() || !this.lightLeaksOn() || mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.lightScanner.queueLoaded(mc.field_1687, mc.field_1724.method_24515(), mc.field_1690.method_38521());
    }

    private boolean lightLeaksOn() {
        return this.sHints.get() && this.sLightLeaks.get();
    }

    private void resync() {
        if (!this.isEnabled()) {
            return;
        }
        this.layer.clear();
        this.spawners.clear();
        this.scannedIds = this.sBlockColors.enabledIds();
        this.seenGeneration = BlockEntityLeakTracker.generation();
        while (BlockEntityLeakTracker.pollAdded() != null) {
        }
        for (Map.Entry<class_2338, BlockEntityLeakTracker.Entry> entry : BlockEntityLeakTracker.view().entrySet()) {
            this.accept(entry.getKey(), entry.getValue());
        }
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        if (this.lightLeaksOn()) {
            this.lightScanner.onPacketApplied(event.packet);
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_2338 pos;
        class_310 mc = class_310.method_1551();
        if (this.lightLeaksOn() && mc.field_1687 != null) {
            this.lightScanner.tick(mc.field_1687);
        }
        if (++this.ticks % 1200 == 0) {
            this.logSources();
        }
        if (BlockEntityLeakTracker.generation() != this.seenGeneration) {
            this.resync();
            return;
        }
        int announced = 0;
        int more = 0;
        while ((pos = BlockEntityLeakTracker.pollAdded()) != null) {
            BlockEntityLeakTracker.Entry entry = BlockEntityLeakTracker.view().get(pos);
            if (entry == null || !this.accept(pos, entry) || !this.sChat.get()) continue;
            if (announced < 5) {
                this.announce(pos, entry);
                ++announced;
                continue;
            }
            ++more;
        }
        if (more > 0) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("+").value(more).text(" more"));
        }
    }

    private void logSources() {
        String summary = "chunk=" + BlockEntityLeakTracker.count(BlockEntityLeakTracker.Source.CHUNK) + " data=" + BlockEntityLeakTracker.count(BlockEntityLeakTracker.Source.DATA) + " event=" + BlockEntityLeakTracker.count(BlockEntityLeakTracker.Source.EVENT) + " sound=" + BlockEntityLeakTracker.count(BlockEntityLeakTracker.Source.SOUND) + " light=" + BlockEntityLeakTracker.count(BlockEntityLeakTracker.Source.LIGHT);
        if (summary.equals(this.lastSummary)) {
            return;
        }
        this.lastSummary = summary;
        LOGGER.info("[Anubis] Block Entity Debug sources: {}", (Object)summary);
    }

    private boolean accept(class_2338 pos, BlockEntityLeakTracker.Entry entry) {
        if (!(entry.real() || this.sHints.get() && (entry.source() != BlockEntityLeakTracker.Source.LIGHT || this.sLightLeaks.get()))) {
            return false;
        }
        class_2248 block = entry.block();
        if (!this.passesDetection(pos)) {
            return false;
        }
        if (this.sBlockFilter.get() && block != class_2246.field_10124 && !this.sBlockColors.isEnabled(block)) {
            return false;
        }
        this.layer.put(pos.method_10063(), block);
        if (block == class_2246.field_10260) {
            this.spawners.add(pos.method_10063());
        }
        return true;
    }

    private void announce(class_2338 pos, BlockEntityLeakTracker.Entry entry) {
        String mob;
        class_2487 tag = entry.tag();
        ModuleChat.Body body = ModuleChat.body().name(entry.block() == class_2246.field_10124 ? "Hidden block" : entry.block().method_9518().getString());
        if (!entry.real()) {
            body.text(" (from " + entry.source().name().toLowerCase() + ")");
        }
        String string = mob = tag == null ? null : (String)tag.method_10562("SpawnData").flatMap(data -> data.method_10562("entity")).flatMap(entity -> entity.method_10558("id")).orElse(null);
        if (mob != null) {
            body.text(" (").value(mob.replace("minecraft:", "")).text(")");
        }
        body.text(" at ").coords(pos.method_10263(), pos.method_10264(), pos.method_10260());
        if (this.sShowNbt.get() && tag != null) {
            Object snbt = tag.toString();
            if (((String)snbt).length() > 200) {
                snbt = ((String)snbt).substring(0, 200) + "...";
            }
            class_2561 full = class_2512.method_32270((class_2520)tag);
            body.append((class_2561)class_2561.method_43470((String)(" " + (String)snbt)).method_27694(style -> style.method_10949((class_2568)new class_2568.class_10613(full))));
        }
        ModuleChat.send((ClientModule)this, body);
    }

    private boolean passesDetection(class_2338 pos) {
        return switch (this.sDetection.get()) {
            case "Below Zero" -> {
                if (pos.method_10264() < 0) {
                    yield true;
                }
                yield false;
            }
            case "Below -20" -> {
                if (pos.method_10264() < -20) {
                    yield true;
                }
                yield false;
            }
            case "Below -30" -> {
                if (pos.method_10264() < -30) {
                    yield true;
                }
                yield false;
            }
            default -> true;
        };
    }

    @EventHandler
    public void onPacketReceiveNetty(PacketReceiveNettyEvent event) {
        class_2596<?> class_25962 = event.packet;
        if (!(class_25962 instanceof class_2626)) {
            return;
        }
        class_2626 packet = (class_2626)class_25962;
        if (!BlockEntityLeakTracker.containsReal(packet.method_11309())) {
            return;
        }
        class_2680 state = packet.method_11308();
        if (state.method_26215() || state.method_27852(class_2246.field_10340) || state.method_27852(class_2246.field_28888)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        this.layer.render(event, this.style);
        if (this.sTracers.get()) {
            this.layer.renderTracers(this.style);
        }
        if (this.spawners.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return;
        }
        class_243 eye = mc.field_1724.method_5836(event.partialTick);
        double rangeSq = (double)this.style.range.get() * (double)this.style.range.get();
        int color = this.sBlockColors.colorArgb(class_2246.field_10260);
        class_12179 beam = class_12179.method_75536((int)color, (float)this.style.lineWidth.get());
        LongIterator longIterator = this.spawners.iterator();
        while (longIterator.hasNext()) {
            long packed = (Long)longIterator.next();
            class_2338 pos = class_2338.method_10092((long)packed);
            if (eye.method_1028((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 0.5) > rangeSq) continue;
            class_12180.method_75541((class_238)new class_238((double)pos.method_10263() + 0.3, (double)pos.method_10264() + 1.0, (double)pos.method_10260() + 0.3, (double)pos.method_10263() + 0.7, (double)pos.method_10264() + 257.0, (double)pos.method_10260() + 0.7), (class_12179)beam).method_75533();
        }
    }
}

