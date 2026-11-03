/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1893
 *  net.minecraft.class_1922
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2346
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_2384
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_2586
 *  net.minecraft.class_2596
 *  net.minecraft.class_2636
 *  net.minecraft.class_2663
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_3481
 *  net.minecraft.class_3489
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_4050
 *  net.minecraft.class_465
 *  net.minecraft.class_638
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 */
package net.anubis.client.easyplace.builder;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.easyplace.EasyPlace;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.Breaker;
import net.anubis.client.easyplace.builder.Eater;
import net.anubis.client.easyplace.builder.Hands;
import net.anubis.client.easyplace.builder.Hop;
import net.anubis.client.easyplace.builder.Shuffle;
import net.anubis.client.easyplace.inventory.InventoryCalm;
import net.anubis.client.easyplace.util.PlacementUtil;
import net.anubis.client.esp.ChunkScanner;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.donutmacros.AhPurchase;
import net.anubis.client.module.impl.donutmacros.DonutSell;
import net.anubis.client.module.impl.misc.AutoReconnectModule;
import net.anubis.client.module.impl.render.BetterSusChunkModule;
import net.anubis.client.module.impl.render.SusChunkFinderModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.ChunkOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_2384;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2596;
import net.minecraft.class_2636;
import net.minecraft.class_2663;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_3481;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_4050;
import net.minecraft.class_465;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class TunnelMinerModule
extends ClientModule
implements ChunkScanner.Consumer {
    private static final String SIZE_3X3 = "3x3 Shard Pickaxe";
    private static final String SIZE_1X2 = "1x2";
    private static final Predicate<class_1799> TOTEM = stack -> stack.method_31574(class_1802.field_8288);
    private static final Predicate<class_1799> XP = stack -> stack.method_31574(class_1802.field_8287);
    private static final Predicate<class_1799> APPLE = stack -> stack.method_31574(class_1802.field_8463);
    private static final Predicate<class_1799> PICKAXE = stack -> stack.method_31573(class_3489.field_42614);
    private static final Predicate<class_1799> SHARD = stack -> PICKAXE.test((class_1799)stack) && TunnelMinerModule.shardName(stack);
    private static final Set<class_2248> STORAGE = Set.of(class_2246.field_10034, class_2246.field_10380, class_2246.field_16328, class_2246.field_10443, class_2246.field_10312, class_2246.field_10181, class_2246.field_16333, class_2246.field_16334, class_2246.field_10228, class_2246.field_10200);
    private static final Set<class_1792> JUNK = Set.of(class_1802.field_20412, class_1802.field_29025, class_1802.field_28866, class_1802.field_20391, class_1802.field_27021, class_1802.field_20394, class_1802.field_20401, class_1802.field_20407, class_1802.field_8831, class_1802.field_8110, class_1802.field_8145, class_1802.field_8328, class_1802.field_27020, class_1802.field_29024, class_1802.field_22000, class_1802.field_23843);
    private static final int PLAN_AHEAD = 40;
    private static final int PLAN_SIDE = 15;
    private static final int PLAN_NODES = 6000;
    private static final int REPLAN_STEPS = 9;
    private static final double STAND_MIN = 1.0;
    private static final double STAND_MAX = 2.6;
    private static final double STEP_TO_MAX = 1.6;
    private static final double STAND_CLOSE = 0.3;
    private static final float VARIED_STAND = 0.2f;
    private static final double QUICK_STEP_SPREAD = 0.3;
    private static final double STREAM_ASIDE = 0.45;
    private static final double STREAM_GAP = 1.0;
    private static final int FEET_WAIT = 4;
    private static final float OLD_WAY_CHANCE = 0.06f;
    private static final int OLD_WAY_MIN = 2;
    private static final int OLD_WAY_SPREAD = 4;
    private static final float LINGER_CHANCE = 0.01f;
    private static final double FALL_BEHIND = 3.0;
    private static final int PERCEIVE_MIN = 3;
    private static final int PERCEIVE_MAX = 5;
    private static final int BUY_RETRY_TICKS = 1200;
    private static final int SELL_STACKS = 4;
    private static final int SELL_RETRY_TICKS = 1200;
    private static final int VERIFY_TICKS = 60;
    private static final int XP_STACK = 64;
    private static final int APPLE_STACK = 64;
    private static final int START_ZONE = 48;
    private static final int CLOSE_SCREEN_TICKS = 100;
    private static final String FLAGS_OFF = "Off";
    private static final String FLAGS_SUS = "Sus Chunk";
    private static final String FLAGS_BETTER = "Better Sus Chunk";
    private static final String FLAGS_BOTH = "Both";
    private static final int ARRIVE_BLOCKS = 3;
    private static final int TOTEM_POP = 35;
    private static final int STALL_PLANS = 3;
    private static final double VISITED_COST = 4.0;
    private static final int UNFLAGGED_CHECKS = 10;
    private static final int VISITED_COLOR = 1431357695;
    private final ModeSetting size = this.addSetting("size", new ModeSetting("Tunnel Size", "3x3 Shard Pickaxe", "3x3 Shard Pickaxe", "1x2"));
    private final BooleanSetting wander = this.addSetting("wander", new BooleanSetting("Wander", true));
    private final ModeSetting flags = this.addSetting("flags", new ModeSetting("Dig To Flagged Chunks", "Off", "Off", "Sus Chunk", "Better Sus Chunk", "Both"));
    private final BooleanSetting showVisited = this.addSetting("show-visited", new BooleanSetting("Show Visited Chunks", true));
    private final NumberSetting storageToStop = this.addSetting("storage-to-stop", new NumberSetting("Storage To Stop", 30.0f, 3.0f, 300.0f).step(1.0f));
    private final NumberSetting baseRadius = this.addSetting("base-radius", new NumberSetting("Base Radius", 80.0f, 16.0f, 192.0f).step(8.0f));
    private final BooleanSetting spawners = this.addSetting("spawners", new BooleanSetting("Stop At Spawners", true));
    private final BooleanSetting disconnect = this.addSetting("disconnect", new BooleanSetting("Disconnect On Find", true));
    private final BooleanSetting quitOnPop = this.addSetting("quit-on-pop", new BooleanSetting("Quit On Totem Pop", true));
    private final BooleanSetting keepTotem = this.addSetting("keep-totem", new BooleanSetting("Keep Totem In Offhand", true));
    private final BooleanSetting mend = this.addSetting("mend", new BooleanSetting("Mend Pickaxe", true));
    private final NumberSetting mendBelow = this.addSetting("mend-below", new NumberSetting("Mend Below Durability", 150.0f, 20.0f, 1000.0f).step(10.0f));
    private final BooleanSetting eat = this.addSetting("eat", new BooleanSetting("Eat Golden Apples", true));
    private final BooleanSetting sellJunk = this.addSetting("sell-junk", new BooleanSetting("Sell Junk For Room", true));
    private final BooleanSetting buyFromAh = this.addSetting("buy-from-ah", new BooleanSetting("Buy From AH", true));
    private final StringSetting maxPerListing = this.addSetting("max-per-listing", new StringSetting("Max Price Per Listing", "10m").hint("e.g. 10m, one whole listing"));
    private final NumberSetting refresh = this.addSetting("refresh", new NumberSetting("AH Refresh (ms)", 1000.0f, 300.0f, 3000.0f).step(100.0f));
    private final BooleanSetting chatFeedback = this.addSetting("chat-feedback", new BooleanSetting("Chat Feedback", true));
    private final class_310 mc = class_310.method_1551();
    private final Random random = new Random();
    private final Map<Long, Boolean> finds = new HashMap<Long, Boolean>();
    private final Map<Long, Integer> breakFails = new HashMap<Long, Integer>();
    private final Set<Long> blocked = new HashSet<Long>();
    private final Map<class_1792, Long> nextBuy = new HashMap<class_1792, Long>();
    private final Map<String, Long> lastFeedback = new HashMap<String, Long>();
    private final List<class_2338> steps = new ArrayList<class_2338>();
    private final LongOpenHashSet visitedChunks = new LongOpenHashSet();
    private final ChunkOverlay visitedOverlay = new ChunkOverlay();
    private boolean hasTarget;
    private long target;
    private int targetBest;
    private int targetNear;
    private int targetStalls;
    private int unflaggedChecks;
    private long tick;
    private int cellSize;
    private class_2350 heading;
    private class_2338 start;
    private class_2338 axis;
    private int drift;
    private long nextDrift;
    private int noiseSeed;
    private int stepIndex;
    private int stepsSincePlan;
    private int blockedPlans;
    private Breaker breaker;
    private Hop hop;
    private Shuffle shuffle;
    private Eater eater;
    private int originX;
    private int originZ;
    private class_2338 routeFrom;
    private class_2338 seenFrom;
    private int perceive;
    private class_2338 standTarget;
    private class_243 standSpot;
    private boolean standVaried;
    private boolean streaming;
    private class_2338 streamTarget;
    private long headBrokeAt = -100L;
    private class_2338 decidedSlab;
    private int oldWayLeft;
    private float[] idleLook;
    private boolean lingering;
    private long leaveAt;
    private String foundWhat;
    private class_2338 foundAt;
    private int settle;
    private long restUntil;
    private long nextRest;
    private long screenSince;
    private DonutSell sell;
    private long nextSell;
    private boolean stopped;
    private boolean popped;
    private AhPurchase purchase;
    private class_1792 purchaseItem;
    private String purchaseName;
    private int purchaseBefore;
    private long verifyUntil;
    private long maxListing;
    private boolean mending;
    private long mendStarted;

    public TunnelMinerModule() {
        super("tunnel-miner", "Tunnel Base Finder", "Digs a tunnel the way you face (3x3 with a Shard Pickaxe) like a player would, around lava, water and gravel, or from one Sus Chunk flagged chunk to the next, and stops at a base or a spawner. Buys totems, XP bottles and golden apples from the AH.", ModuleCategory.BASE_RAID);
        this.usesNotifications();
        this.showVisited.visibleWhen(() -> !this.flags.is(FLAGS_OFF));
        this.mendBelow.visibleWhen(this.mend::get);
        this.maxPerListing.visibleWhen(this.buyFromAh::get);
        this.refresh.visibleWhen(this.buyFromAh::get);
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        class_746 player = this.mc.field_1724;
        if (player == null || this.mc.field_1687 == null) {
            this.say(ModuleChat.body().warn("Join a world first."));
            this.requestDisable();
            return;
        }
        this.tick = 0L;
        this.finds.clear();
        this.breakFails.clear();
        this.blocked.clear();
        this.nextBuy.clear();
        this.steps.clear();
        this.settle = 0;
        this.blockedPlans = 0;
        this.stepsSincePlan = 0;
        this.stepIndex = 0;
        this.breaker = null;
        this.hop = null;
        this.eater = null;
        this.purchase = null;
        this.sell = null;
        this.nextSell = 0L;
        this.oldWayLeft = 0;
        this.decidedSlab = null;
        this.verifyUntil = 0L;
        this.stopped = false;
        this.mending = false;
        this.heading = player.method_5735();
        this.axis = this.start = player.method_24515();
        this.drift = 0;
        this.nextDrift = 0L;
        this.noiseSeed = this.random.nextInt();
        this.restUntil = 0L;
        this.nextRest = this.nextGap();
        this.seenFrom = this.routeFrom = player.method_24515();
        this.shuffle = null;
        this.standTarget = null;
        this.standSpot = null;
        this.idleLook = null;
        this.lingering = false;
        this.leaveAt = 0L;
        this.maxListing = AhPurchase.parseMoney(this.maxPerListing.get());
        this.cellSize = 1;
        if (this.size.is(SIZE_3X3)) {
            if (Hands.has(player, SHARD)) {
                this.cellSize = 3;
            } else {
                this.say(ModuleChat.body().warn("No Shard Pickaxe").text(", digging a 1x2 tunnel instead."));
            }
        }
        this.originX = player.method_31477() - this.cellSize / 2;
        this.originZ = player.method_31479() - this.cellSize / 2;
        this.visitedChunks.clear();
        this.hasTarget = false;
        if (!this.flags.is(FLAGS_OFF)) {
            this.enableSources();
        }
        ChunkScanner.register(this);
        this.on(TickEvent.TYPE, Stage.PRE, event -> this.step());
        this.on(Render3DEvent.TYPE, Stage.POST, this::render);
        this.popped = false;
        this.on(PacketAppliedEvent.TYPE, event -> {
            class_2663 status;
            class_2596<?> patt0$temp = event.packet;
            if (patt0$temp instanceof class_2663 && (status = (class_2663)patt0$temp).method_11470() == 35 && this.mc.field_1687 != null && this.mc.field_1724 != null && status.method_11469((class_1937)this.mc.field_1687) == this.mc.field_1724) {
                this.popped = true;
            }
        });
        String tunnel = "Digging a " + (this.cellSize == 3 ? "3x3" : SIZE_1X2) + " tunnel";
        this.say(ModuleChat.body().text(this.flags.is(FLAGS_OFF) ? tunnel + " " + this.heading.method_10151() + " at Y " + this.start.method_10264() + "." : tunnel + " at Y " + this.start.method_10264() + " to the chunks " + this.flags.get() + " flags, " + this.heading.method_10151() + " while there are none."));
    }

    @Override
    protected void onDisable() {
        ChunkScanner.unregister(this);
        if (this.purchase != null) {
            this.purchase.cancel();
        }
        this.purchase = null;
        if (this.sell != null) {
            this.sell.cancel();
        }
        this.sell = null;
        this.release();
        this.finds.clear();
    }

    private void step() {
        class_746 player = this.mc.field_1724;
        class_638 level = this.mc.field_1687;
        if (player == null || level == null || this.mc.field_1761 == null) {
            return;
        }
        ++this.tick;
        if (this.popped) {
            if (this.quitOnPop.get()) {
                this.quitOnPop(player);
                return;
            }
            this.popped = false;
        }
        if (this.stopped) {
            this.linger(player);
            return;
        }
        if (!this.flags.is(FLAGS_OFF)) {
            this.visitedChunks.add(class_1923.method_8331((int)(player.method_31477() >> 4), (int)(player.method_31479() >> 4)));
            if (this.hasTarget && this.tick % 20L == 0L) {
                this.watchTarget(player, level);
            }
        }
        if (player.method_29504()) {
            this.say(ModuleChat.body().danger("You died").text(", Tunnel Base Finder stopped."));
            this.requestDisable();
            return;
        }
        if (this.purchase != null) {
            this.followPurchase(player);
            return;
        }
        if (this.verifyUntil > 0L) {
            this.verifyPurchase(player);
            return;
        }
        if (this.sell != null) {
            if (!this.sell.tick()) {
                this.sell = null;
                if (TunnelMinerModule.freeSlots(player) == 0) {
                    this.nextSell = this.tick + 1200L;
                }
            }
            return;
        }
        if (this.mending) {
            this.mend(player);
            return;
        }
        if (this.mc.field_1755 instanceof class_465) {
            this.release();
            if (this.screenSince == 0L) {
                this.screenSince = this.tick;
            }
            if (this.tick - this.screenSince > 100L) {
                player.method_7346();
            }
            return;
        }
        this.screenSince = 0L;
        if (this.eater != null) {
            if (this.eater.tick(this.mc) != Eater.Result.RUNNING) {
                this.eater = null;
            }
            return;
        }
        if (this.breaker == null && this.hop == null && !this.supplies(player)) {
            return;
        }
        if (this.tick % 20L == 0L && this.checkFinds(player, level)) {
            return;
        }
        this.dig(player, level);
    }

    private boolean supplies(class_746 player) {
        class_1799 pickaxe;
        if (this.keepTotem.get() && !TOTEM.test(player.method_6079())) {
            this.release();
            if (Hands.has(player, TOTEM)) {
                if (this.tick % 5L == 0L && this.calm(player)) {
                    Hands.toOffhand(this.mc, player, TOTEM);
                }
                return false;
            }
            if (!this.startBuy(player, class_1802.field_8288, "Totem", "totem", 1)) {
                this.say(ModuleChat.body().danger("No totem").text(" and Buy From AH is off, Tunnel Base Finder stopped."));
                this.requestDisable();
            }
            return false;
        }
        if (this.eat.get() && Eater.hungry(player)) {
            if (Hands.has(player, APPLE)) {
                if (this.calm(player)) {
                    this.eater = new Eater(APPLE);
                }
                return false;
            }
            if (this.startBuy(player, class_1802.field_8463, "Golden Apples", "golden apple stack", 64)) {
                return false;
            }
        }
        if ((pickaxe = this.pickaxe(player)) == null) {
            this.say(ModuleChat.body().danger("No pickaxe").text(this.cellSize == 3 ? " (Shard Pickaxe)" : "").text(", Tunnel Base Finder stopped."));
            this.requestDisable();
            return false;
        }
        int left = pickaxe.method_7936() - pickaxe.method_7919();
        if (this.mend.get() && TunnelMinerModule.mendable(pickaxe) && left < this.mendBelow.getInt()) {
            if (Hands.has(player, XP)) {
                this.release();
                this.mending = true;
                this.mendStarted = this.tick;
                this.feedback("mend", "Mending the pickaxe with XP bottles", 200);
                return false;
            }
            if (this.startBuy(player, class_1802.field_8287, "XP Bottles", "xp bottle stack", 64)) {
                return false;
            }
        }
        if (!(left >= 10 || this.mend.get() && TunnelMinerModule.mendable(pickaxe))) {
            this.say(ModuleChat.body().danger("Pickaxe almost broken").text(", Tunnel Base Finder stopped before it breaks."));
            this.requestDisable();
            return false;
        }
        return true;
    }

    private void mend(class_746 player) {
        boolean repaired;
        class_1799 held = player.method_6047();
        boolean bl = repaired = this.tool().test(held) && held.method_7919() == 0;
        if (repaired || this.tick - this.mendStarted > 1200L || !XP.test(player.method_6079()) && !Hands.has(player, XP)) {
            this.mc.field_1690.field_1904.method_23481(false);
            this.mending = false;
            if (repaired) {
                this.feedback("mended", "Pickaxe mended", 200);
            }
            return;
        }
        if (!XP.test(player.method_6079())) {
            this.mc.field_1690.field_1904.method_23481(false);
            if (this.tick % 5L == 0L && this.calm(player)) {
                Hands.toOffhand(this.mc, player, XP);
            }
            return;
        }
        if (!Hands.hold(this.mc, player, this.tool())) {
            return;
        }
        Aim.turn(this.mc, player.method_36454(), 80.0f, 22.0f);
        this.mc.field_1690.field_1904.method_23481(player.method_36455() > 60.0f);
    }

    private boolean startBuy(class_746 player, class_1792 item, String name, String search, int amount) {
        if (!this.buyFromAh.get()) {
            return false;
        }
        if (this.tick < this.nextBuy.getOrDefault(item, 0L)) {
            this.feedback("wait-" + name, "Waiting to try buying " + name + " again", 600);
            return true;
        }
        if (TunnelMinerModule.freeSlots(player) == 0) {
            if (!this.sellJunk.get() || this.tick < this.nextSell || !this.startSelling(player)) {
                this.feedback("room-" + name, "No room in the inventory to buy " + name, 600);
                this.nextBuy.put(item, this.tick + 200L);
            }
            return true;
        }
        if (!this.calm(player)) {
            return true;
        }
        this.purchaseItem = item;
        this.purchaseName = name;
        this.purchaseBefore = TunnelMinerModule.count(player, item);
        this.feedback("buy-" + name, "Buying " + name + " (/ah " + search + ")", 1);
        this.purchase = AhPurchase.startAny(this, item, name, search, stack -> stack.method_31574(item), amount, this.maxListing, this.refresh.getInt(), 0L);
        return true;
    }

    private void followPurchase(class_746 player) {
        AhPurchase.Status status = this.purchase.status();
        if (status == AhPurchase.Status.RUNNING) {
            return;
        }
        this.purchase = null;
        if (status == AhPurchase.Status.FAILED) {
            this.say(ModuleChat.body().warn("Could not buy " + this.purchaseName).text(", trying again in a minute."));
            this.nextBuy.put(this.purchaseItem, this.tick + 1200L);
            this.purchaseItem = null;
            return;
        }
        this.verifyUntil = this.tick + 60L;
    }

    private void verifyPurchase(class_746 player) {
        int got = TunnelMinerModule.count(player, this.purchaseItem) - this.purchaseBefore;
        if (got > 0) {
            this.say(ModuleChat.body().good("Bought " + got + " " + this.purchaseName).text(", it is in the inventory."));
            EasyPlace.LOG.info("[Tunnel Base Finder] bought {} {} (verified)", (Object)got, (Object)this.purchaseName);
        } else {
            if (this.tick < this.verifyUntil) {
                return;
            }
            this.say(ModuleChat.body().warn("The AH sold " + this.purchaseName + " but it never showed up").text(", trying again later."));
            this.nextBuy.put(this.purchaseItem, this.tick + 1200L);
        }
        this.verifyUntil = 0L;
        this.purchaseItem = null;
    }

    private boolean startSelling(class_746 player) {
        class_1661 inventory = player.method_31548();
        HashMap<Integer, class_1792> slots = new HashMap<Integer, class_1792>();
        for (int i = 35; i >= 0 && slots.size() < 4; --i) {
            class_1792 item = inventory.method_5438(i).method_7909();
            if (i == inventory.method_67532() || !JUNK.contains(item)) continue;
            slots.put(i, item);
        }
        if (slots.isEmpty()) {
            return false;
        }
        if (!this.calm(player)) {
            return true;
        }
        this.feedback("selling", "Selling " + slots.size() + " stacks of junk stone with /sell to make room", 1);
        this.sell = DonutSell.start(this, slots);
        return true;
    }

    private void dig(class_746 player, class_638 level) {
        if (this.settle > 0) {
            --this.settle;
            if (this.lingering) {
                this.idle(player);
            }
            return;
        }
        this.lingering = false;
        if (this.breaker != null) {
            Breaker.Result result = this.breaker.tick(this.mc);
            if (result == Breaker.Result.RUNNING) {
                return;
            }
            class_2338 at = this.breaker.pos();
            this.breaker = null;
            int n = this.settle = this.cellSize == 3 ? this.lognormal(2.2, 0.3, 2, 6) : this.lognormal(1.0, 0.5, 0, 4);
            if (result == Breaker.Result.FAILED && this.breakFails.merge(at.method_10063(), 1, Integer::sum) >= 3) {
                this.blocked.add(this.cellKey(at));
                this.steps.clear();
            }
            return;
        }
        if (this.shuffle != null) {
            Shuffle.Result result = this.shuffle.tick(this.mc);
            if (result == Shuffle.Result.RUNNING) {
                return;
            }
            boolean quick = this.shuffle.quick();
            this.shuffle = null;
            if (result == Shuffle.Result.FAILED) {
                this.standSpot = null;
            } else if (!quick) {
                this.settle = this.lognormal(1.5, 0.5, 0, 6);
            }
            return;
        }
        if (this.hop != null) {
            Hop.Result result = this.hop.tick(this.mc);
            if (result == Hop.Result.RUNNING) {
                return;
            }
            this.hop = null;
            if (result == Hop.Result.FAILED) {
                this.steps.clear();
            }
            return;
        }
        if (this.rest(player)) {
            return;
        }
        if (!Hands.hold(this.mc, player, this.tool())) {
            return;
        }
        if (this.hasTarget && this.arrived(player)) {
            this.endTarget(player, null);
        }
        if (!(this.stepIndex < this.steps.size() && this.stepsSincePlan < 9 || this.plan(player, level))) {
            return;
        }
        class_2338 next = this.steps.get(this.stepIndex);
        class_2338 from = this.stepIndex > 0 ? this.steps.get(this.stepIndex - 1) : this.routeFrom;
        class_2350 way = TunnelMinerModule.beside(from, next);
        boolean newCell = this.cellKey(next) != this.cellKey(from);
        this.seenFrom = player.method_24515();
        if (way == null || newCell && !Boolean.TRUE.equals(this.safe(level, this.cellX(next.method_10263()), this.cellZ(next.method_10260()), next.method_10264()))) {
            this.endStream();
            this.steps.clear();
            return;
        }
        class_2338 target = this.toMine(level, next, way);
        if (this.stream(player, level, next, way, target)) {
            return;
        }
        if (target == null) {
            if (TunnelMinerModule.flat(player, next) <= 3.0) {
                ++this.stepIndex;
                ++this.stepsSincePlan;
            } else if (TunnelMinerModule.flat(player, from) > 1.2) {
                this.hop = new Hop(from, true);
            } else {
                boolean through = this.stepIndex + 1 < this.steps.size() && TunnelMinerModule.beside(next, this.steps.get(this.stepIndex + 1)) == way && this.toMine(level, this.steps.get(this.stepIndex + 1), way) == null;
                this.hop = new Hop(next, through);
                ++this.stepIndex;
                ++this.stepsSincePlan;
            }
            return;
        }
        class_243 spot = this.stand(level, player, target, way);
        if (spot == null) {
            if (TunnelMinerModule.flat(player, from) > 0.7) {
                this.hop = new Hop(from, false);
                return;
            }
        } else if (Math.hypot(spot.field_1352 - player.method_23317(), spot.field_1350 - player.method_23321()) > 0.3) {
            this.shuffle = new Shuffle(spot, target.method_46558(), !this.standVaried);
            return;
        }
        if (this.random.nextFloat() < 0.01f) {
            this.settle = this.lognormal(20.0, 0.5, 8, 100);
            this.idleLook = null;
            this.lingering = true;
            return;
        }
        this.breaker = new Breaker(target.method_10062(), false, this.face(next, target, way));
        this.standTarget = null;
    }

    private boolean stream(class_746 player, class_638 level, class_2338 next, class_2350 way, class_2338 target) {
        boolean feetPending;
        if (this.streamTarget != null && level.method_8320(this.streamTarget).method_26215()) {
            this.headBrokeAt = this.tick;
            this.streamTarget = null;
        }
        class_2338 head = next.method_10084();
        boolean bl = feetPending = target != null && target.equals((Object)next) && this.tick - this.headBrokeAt < 4L;
        if (this.cellSize != 3 || !this.straight(way) || Math.abs(TunnelMinerModule.aside(player, next, way)) > 0.45 || target != null && !target.equals((Object)head) && !feetPending) {
            this.endStream();
            return false;
        }
        if (target != null && target.equals((Object)head) && !head.equals((Object)this.decidedSlab)) {
            this.decidedSlab = head.method_10062();
            if (this.oldWayLeft > 0) {
                --this.oldWayLeft;
            }
            if (this.oldWayLeft == 0 && this.random.nextFloat() < 0.06f) {
                this.oldWayLeft = 2 + this.random.nextInt(4);
            }
        }
        if (this.oldWayLeft > 0) {
            this.endStream();
            return false;
        }
        this.streaming = true;
        class_243 face = class_243.method_24953((class_2382)head).method_1023((double)way.method_10148() * 0.5, 0.0, (double)way.method_10165() * 0.5);
        float[] rot = PlacementUtil.calcRotation(player.method_33571(), face);
        Aim.turn(this.mc, rot[0], rot[1], 22.0f);
        double gap = (face.field_1352 - player.method_23317()) * (double)way.method_10148() + (face.field_1350 - player.method_23321()) * (double)way.method_10165();
        this.mc.field_1690.field_1894.method_23481(target == null || gap > 1.0);
        if (target == null) {
            ++this.stepIndex;
            ++this.stepsSincePlan;
            return true;
        }
        if (feetPending) {
            return true;
        }
        class_3965 hit = Aim.trace(this.mc, player, player.method_36454(), player.method_36455(), player.method_55754());
        if (hit != null && hit.method_17783() == class_239.class_240.field_1332 && hit.method_17777().equals((Object)head)) {
            this.streamTarget = head.method_10062();
            if (this.mc.field_1761.method_2902(head, hit.method_17780())) {
                player.method_6104(class_1268.field_5808);
            }
        }
        return true;
    }

    private boolean straight(class_2350 way) {
        return this.stepIndex + 1 < this.steps.size() && TunnelMinerModule.beside(this.steps.get(this.stepIndex), this.steps.get(this.stepIndex + 1)) == way;
    }

    private static double aside(class_746 player, class_2338 next, class_2350 way) {
        class_2350 side = way.method_10170();
        return (player.method_23317() - (double)next.method_10263() - 0.5) * (double)side.method_10148() + (player.method_23321() - (double)next.method_10260() - 0.5) * (double)side.method_10165();
    }

    private void endStream() {
        if (!this.streaming) {
            return;
        }
        this.streaming = false;
        this.streamTarget = null;
        this.mc.field_1690.field_1894.method_23481(false);
        if (this.mc.field_1761 != null && this.mc.field_1761.method_2923()) {
            this.mc.field_1761.method_2925();
        }
    }

    private int lognormal(double median, double sigma, int min, int max) {
        return (int)class_3532.method_53062((long)Math.round(Math.exp(Math.log(median) + this.random.nextGaussian() * sigma)), (long)min, (long)max);
    }

    private static double flat(class_746 player, class_2338 pos) {
        return Math.hypot((double)pos.method_10263() + 0.5 - player.method_23317(), (double)pos.method_10260() + 0.5 - player.method_23321());
    }

    private class_243 stand(class_638 level, class_746 player, class_2338 target, class_2350 way) {
        double keepAside;
        if (target.equals((Object)this.standTarget)) {
            return this.standSpot;
        }
        this.standTarget = target.method_10062();
        this.standSpot = null;
        double tx = (double)target.method_10263() + 0.5;
        double tz = (double)target.method_10260() + 0.5;
        double y = player.method_23318();
        class_2350 side = way.method_10170();
        double room = this.cellSize == 3 ? 0.7 : 0.2;
        double back = (tx - player.method_23317()) * (double)way.method_10148() + (tz - player.method_23321()) * (double)way.method_10165();
        double aside = (player.method_23317() - tx) * (double)side.method_10148() + (player.method_23321() - tz) * (double)side.method_10165();
        double d = keepAside = this.cellSize == 3 && this.oldWayLeft == 0 ? Math.min(room, 0.45) : room;
        if (back >= 1.0 && back <= 2.6 && Math.abs(aside) <= keepAside && TunnelMinerModule.open(level, player, player.method_73189())) {
            this.standSpot = player.method_73189();
            return this.standSpot;
        }
        this.standVaried = this.random.nextFloat() < 0.2f;
        for (int i = 0; i < 16; ++i) {
            double b = 1.0 + this.random.nextDouble() * (this.standVaried ? 0.6000000000000001 : 0.3);
            double l = this.standVaried ? (this.random.nextDouble() * 2.0 - 1.0) * room : 0.0;
            class_243 spot = new class_243(tx - (double)way.method_10148() * b + (double)side.method_10148() * l, y, tz - (double)way.method_10165() * b + (double)side.method_10165() * l);
            if (!TunnelMinerModule.open(level, player, spot)) continue;
            this.standSpot = spot;
            return spot;
        }
        return null;
    }

    private static boolean open(class_638 level, class_746 player, class_243 spot) {
        class_238 box = player.method_18377(class_4050.field_18076).method_30757(spot).method_1011(0.02);
        if (!level.method_8587((class_1297)player, box)) {
            return false;
        }
        class_2338 floor = class_2338.method_49637((double)spot.field_1352, (double)(spot.field_1351 - 0.5), (double)spot.field_1350);
        return !level.method_8320(floor).method_26220((class_1922)level, floor).method_1110();
    }

    private class_2338 toMine(class_638 level, class_2338 next, class_2350 way) {
        class_2338 head = next.method_10084();
        if (TunnelMinerModule.solid(level, head)) {
            return head;
        }
        if (TunnelMinerModule.solid(level, next)) {
            return next;
        }
        return null;
    }

    private class_2350 face(class_2338 next, class_2338 target, class_2350 way) {
        if (this.cellSize == 3 && target.equals((Object)next)) {
            return class_2350.field_11036;
        }
        return way.method_10153();
    }

    private static boolean solid(class_638 level, class_2338 pos) {
        class_2680 state = level.method_8320(pos);
        return !state.method_26220((class_1922)level, pos).method_1110();
    }

    private boolean rest(class_746 player) {
        if (this.tick < this.restUntil) {
            this.idle(player);
            return true;
        }
        if (this.tick >= this.nextRest) {
            this.release();
            this.restUntil = this.tick + (long)this.lognormal(30.0, 0.6, 10, 200);
            this.nextRest = this.restUntil + this.nextGap();
            this.idleLook = null;
            return true;
        }
        return false;
    }

    private long nextGap() {
        return (long)class_3532.method_15350((double)(-Math.log(1.0 - this.random.nextDouble()) * 2400.0), (double)600.0, (double)12000.0);
    }

    private void idle(class_746 player) {
        if (this.idleLook == null || this.random.nextFloat() < 0.025f) {
            this.idleLook = new float[]{player.method_36454() + (float)(this.random.nextGaussian() * 40.0), class_3532.method_15363((float)((float)(10.0 + this.random.nextGaussian() * 22.0)), (float)-45.0f, (float)70.0f)};
        }
        Aim.turn(this.mc, this.idleLook[0], this.idleLook[1], 22.0f);
    }

    private boolean plan(class_746 player, class_638 level) {
        Node origin;
        int tz;
        class_2338 feet;
        this.steps.clear();
        this.stepIndex = 0;
        this.stepsSincePlan = 0;
        this.routeFrom = feet = player.method_24515();
        this.seenFrom = feet;
        this.perceive = 3 + this.random.nextInt(3);
        int y = feet.method_10264();
        int cx = this.cellX(feet.method_10263());
        int cz = this.cellZ(feet.method_10260());
        this.pickTarget(player);
        boolean toTarget = this.hasTarget;
        int tx = toTarget ? this.cellX(TunnelMinerModule.middle(class_1923.method_8325((long)this.target))) : 0;
        int n = tz = toTarget ? this.cellZ(TunnelMinerModule.middle(class_1923.method_8332((long)this.target))) : 0;
        if (!toTarget && this.wander.get() && this.tick >= this.nextDrift) {
            this.drift = class_3532.method_15340((int)(this.drift + this.random.nextInt(3) - 1), (int)-2, (int)2);
            this.nextDrift = this.tick + 20L * (long)(30 + this.random.nextInt(60));
        }
        int fx = this.heading.method_10148();
        int fz = this.heading.method_10165();
        class_2350 side = this.heading.method_10170();
        int lx = side.method_10148();
        int lz = side.method_10165();
        int ax = this.cellX(this.axis.method_10263());
        int az = this.cellZ(this.axis.method_10260());
        int ahead = Math.max(4, 40 / this.cellSize);
        int aside = Math.max(3, 15 / this.cellSize);
        int startLateral = (cx - ax) * lx + (cz - az) * lz;
        long here = this.cellChunk(cx, cz);
        long goal = toTarget ? this.target : Long.MIN_VALUE;
        HashMap<Long, Boolean> safeCache = new HashMap<Long, Boolean>();
        PriorityQueue<Node> open = new PriorityQueue<Node>((a, b) -> Double.compare(a.rank, b.rank));
        HashSet<Long> visited = new HashSet<Long>();
        Node best = origin = new Node(cx, cz, 0.0, 0.0, null);
        double bestScore = toTarget ? (double)(-TunnelMinerModule.apart(cx, cz, tx, tz)) : this.score(cx, cz, cx, cz, fx, fz, lx, lz, ax, az);
        open.add(origin);
        int expanded = 0;
        while (!open.isEmpty() && expanded++ < 6000) {
            double nodeScore;
            Node node = (Node)open.poll();
            if (!visited.add(TunnelMinerModule.key(node.x, node.z))) continue;
            double d = nodeScore = toTarget ? (double)(-TunnelMinerModule.apart(node.x, node.z, tx, tz)) : this.score(node.x, node.z, cx, cz, fx, fz, lx, lz, ax, az);
            if (nodeScore > bestScore || nodeScore == bestScore && node.cost < best.cost) {
                best = node;
                bestScore = nodeScore;
            }
            for (class_2350 way : class_2350.class_2353.field_11062) {
                Boolean safe;
                int z;
                int x = node.x + way.method_10148();
                long k = TunnelMinerModule.key(x, z = node.z + way.method_10165());
                if (visited.contains(k) || this.blocked.contains(k)) continue;
                if (toTarget) {
                    if (Math.abs(x - cx) > ahead || Math.abs(z - cz) > ahead) {
                        continue;
                    }
                } else {
                    int forward = (x - cx) * fx + (z - cz) * fz;
                    int lateral = (x - ax) * lx + (z - az) * lz;
                    if (forward < -2 || forward > ahead || Math.abs(lateral - startLateral) > aside) continue;
                }
                if (!Boolean.TRUE.equals(safe = safeCache.computeIfAbsent(k, unused -> this.safe(level, x, z, y)))) continue;
                double cost = node.cost + 1.0 + this.noise(x, z) + this.visitedCost(x, z, here, goal);
                double estimate = toTarget ? (double)TunnelMinerModule.apart(x, z, tx, tz) : -this.score(x, z, cx, cz, fx, fz, lx, lz, ax, az);
                open.add(new Node(x, z, cost, cost + estimate, node));
            }
        }
        if (toTarget) {
            int left = TunnelMinerModule.apart(best.x, best.z, tx, tz);
            int now = TunnelMinerModule.apart(cx, cz, tx, tz);
            if (best == origin) {
                this.endTarget(player, left == 0 ? null : "could not get any closer");
                return false;
            }
            if (left > 0) {
                if (left < this.targetBest || now < this.targetNear) {
                    this.targetBest = Math.min(this.targetBest, left);
                    this.targetNear = Math.min(this.targetNear, now);
                    this.targetStalls = 0;
                } else if (++this.targetStalls >= 3) {
                    this.endTarget(player, "could not get any closer");
                    return false;
                }
            }
        } else if (best == origin) {
            if (++this.blockedPlans > 4) {
                this.say(ModuleChat.body().danger("Nowhere safe to dig").text(", Tunnel Base Finder stopped."));
                this.requestDisable();
                return false;
            }
            this.heading = this.blockedPlans % 2 == 1 ? this.heading.method_10170() : this.heading.method_10160().method_10160();
            this.axis = feet;
            this.drift = 0;
            this.feedback("turn", "Blocked ahead, digging " + this.heading.method_10151() + " instead", 40);
            return false;
        }
        this.blockedPlans = 0;
        ArrayList<Node> cells = new ArrayList<Node>();
        Node node = best;
        while (node != null) {
            cells.add(0, node);
            node = node.parent;
        }
        class_2338 at = feet;
        class_2338 middle = this.center((Node)cells.get(0), y);
        at = this.walkTo(at, middle);
        for (int i = 1; i < cells.size(); ++i) {
            at = this.walkTo(at, this.center((Node)cells.get(i), y));
        }
        return !this.steps.isEmpty();
    }

    private class_2338 walkTo(class_2338 from, class_2338 to) {
        class_2338 at = from;
        while (at.method_10263() != to.method_10263()) {
            at = at.method_10069(Integer.signum(to.method_10263() - at.method_10263()), 0, 0);
            this.steps.add(at);
        }
        while (at.method_10260() != to.method_10260()) {
            at = at.method_10069(0, 0, Integer.signum(to.method_10260() - at.method_10260()));
            this.steps.add(at);
        }
        return at;
    }

    private class_2338 center(Node cell, int y) {
        int half = this.cellSize / 2;
        return new class_2338(cell.x * this.cellSize + this.originX + half, y, cell.z * this.cellSize + this.originZ + half);
    }

    private int cellX(int x) {
        return Math.floorDiv(x - this.originX, this.cellSize);
    }

    private int cellZ(int z) {
        return Math.floorDiv(z - this.originZ, this.cellSize);
    }

    private double score(int x, int z, int cx, int cz, int fx, int fz, int lx, int lz, int ax, int az) {
        int forward = (x - cx) * fx + (z - cz) * fz;
        int lateral = (x - ax) * lx + (z - az) * lz;
        return (double)forward - 2.0 * (double)Math.abs(lateral - this.drift);
    }

    private LongSet flagged() {
        BetterSusChunkModule better;
        SusChunkFinderModule sus;
        LongOpenHashSet all = new LongOpenHashSet();
        if (!this.flags.is(FLAGS_BETTER) && (sus = ModuleRegistry.get(SusChunkFinderModule.class)) != null && sus.isEnabled()) {
            all.addAll((LongCollection)sus.flaggedChunks());
        }
        if (!this.flags.is(FLAGS_SUS) && (better = ModuleRegistry.get(BetterSusChunkModule.class)) != null && better.isEnabled()) {
            all.addAll((LongCollection)better.flaggedChunks());
        }
        return all;
    }

    private void enableSources() {
        if (!this.flags.is(FLAGS_BETTER)) {
            this.turnOn(ModuleRegistry.get(SusChunkFinderModule.class), "Sus Chunk Finder");
        }
        if (!this.flags.is(FLAGS_SUS)) {
            this.turnOn(ModuleRegistry.get(BetterSusChunkModule.class), FLAGS_BETTER);
        }
    }

    private void turnOn(ClientModule module, String name) {
        if (module == null || module.isEnabled()) {
            return;
        }
        module.setEnabled(true);
        this.say(ModuleChat.body().text("Turned on " + name + " to find chunks to dig to."));
    }

    private void pickTarget(class_746 player) {
        if (this.flags.is(FLAGS_OFF) || this.hasTarget) {
            return;
        }
        double bestDistance = Double.MAX_VALUE;
        long best = 0L;
        LongIterator keys = this.flagged().iterator();
        while (keys.hasNext()) {
            double distance;
            long key = keys.nextLong();
            if (this.visitedChunks.contains(key)) continue;
            int mx = TunnelMinerModule.middle(class_1923.method_8325((long)key));
            int mz = TunnelMinerModule.middle(class_1923.method_8332((long)key));
            if (Math.abs(mx - this.start.method_10263()) <= 48 && Math.abs(mz - this.start.method_10260()) <= 48 || !((distance = Math.hypot((double)mx - player.method_23317(), (double)mz - player.method_23321())) < bestDistance)) continue;
            bestDistance = distance;
            best = key;
        }
        if (bestDistance == Double.MAX_VALUE) {
            return;
        }
        this.hasTarget = true;
        this.target = best;
        this.targetBest = Integer.MAX_VALUE;
        this.targetNear = Integer.MAX_VALUE;
        this.targetStalls = 0;
        this.unflaggedChecks = 0;
        int mx = TunnelMinerModule.middle(class_1923.method_8325((long)best));
        int mz = TunnelMinerModule.middle(class_1923.method_8332((long)best));
        this.feedback("target", "Digging to a flagged chunk at " + mx + " " + mz + " (" + (int)bestDistance + " blocks)", 1);
        EasyPlace.LOG.info("[Tunnel Base Finder] digging to flagged chunk at {} {} ({} blocks)", new Object[]{mx, mz, (int)bestDistance});
    }

    private void watchTarget(class_746 player, class_638 level) {
        if (this.flagged().contains(this.target) || !level.method_8393(class_1923.method_8325((long)this.target), class_1923.method_8332((long)this.target))) {
            this.unflaggedChecks = 0;
            return;
        }
        if (++this.unflaggedChecks >= 10) {
            this.endTarget(player, "it is not flagged any more");
        }
    }

    private boolean arrived(class_746 player) {
        return Math.abs(player.method_31477() - TunnelMinerModule.middle(class_1923.method_8325((long)this.target))) <= 3 && Math.abs(player.method_31479() - TunnelMinerModule.middle(class_1923.method_8332((long)this.target))) <= 3;
    }

    private void endTarget(class_746 player, String why) {
        int mx = TunnelMinerModule.middle(class_1923.method_8325((long)this.target));
        int mz = TunnelMinerModule.middle(class_1923.method_8332((long)this.target));
        this.visitedChunks.add(this.target);
        this.hasTarget = false;
        this.steps.clear();
        this.heading = player.method_5735();
        this.axis = player.method_24515();
        this.drift = 0;
        String what = why == null ? "Dug through the flagged chunk at " + mx + " " + mz : "Left the flagged chunk at " + mx + " " + mz + " (" + why + ")";
        this.feedback("target-end", what, 1);
        EasyPlace.LOG.info("[Tunnel Base Finder] {}", (Object)what);
    }

    private double visitedCost(int x, int z, long here, long goal) {
        if (this.flags.is(FLAGS_OFF)) {
            return 0.0;
        }
        long chunk = this.cellChunk(x, z);
        return chunk != here && chunk != goal && this.visitedChunks.contains(chunk) ? 4.0 : 0.0;
    }

    private long cellChunk(int x, int z) {
        int half = this.cellSize / 2;
        return class_1923.method_8331((int)(x * this.cellSize + this.originX + half >> 4), (int)(z * this.cellSize + this.originZ + half >> 4));
    }

    private static int middle(int chunk) {
        return chunk * 16 + 8;
    }

    private static int apart(int x, int z, int tx, int tz) {
        return Math.abs(x - tx) + Math.abs(z - tz);
    }

    private void render(Render3DEvent event) {
        if (!this.showVisited.get() || this.flags.is(FLAGS_OFF) || this.visitedChunks.isEmpty()) {
            return;
        }
        this.visitedOverlay.draw((LongSet)this.visitedChunks, 1431357695, event.partialTick);
    }

    private double noise(int x, int z) {
        if (!this.wander.get()) {
            return 0.0;
        }
        int h = x * 73856093 ^ z * 19349663 ^ this.noiseSeed;
        h ^= h >>> 13;
        h *= 1540483477;
        h ^= h >>> 15;
        return (double)(h & 0x3FF) / 1023.0 * 0.7;
    }

    private Boolean safe(class_638 level, int cx, int cz, int y) {
        int dz;
        int s = this.cellSize;
        int height = s == 3 ? 3 : 2;
        int x0 = cx * s + this.originX;
        int z0 = cz * s + this.originZ;
        int dx = Math.max(0, Math.max(x0 - this.seenFrom.method_10263(), this.seenFrom.method_10263() - (x0 + s - 1)));
        if (Math.max(dx, dz = Math.max(0, Math.max(z0 - this.seenFrom.method_10260(), this.seenFrom.method_10260() - (z0 + s - 1)))) > this.perceive) {
            return level.method_8477(new class_2338(x0, y, z0)) ? Boolean.TRUE : null;
        }
        class_2338.class_2339 pos = new class_2338.class_2339();
        class_2338.class_2339 around = new class_2338.class_2339();
        for (int x = x0; x < x0 + s; ++x) {
            for (int z = z0; z < z0 + s; ++z) {
                pos.method_10103(x, y - 1, z);
                if (!level.method_8477((class_2338)pos)) {
                    return null;
                }
                class_2680 floor = level.method_8320((class_2338)pos);
                if (!floor.method_26227().method_15769() || floor.method_26220((class_1922)level, (class_2338)pos).method_1110()) {
                    return false;
                }
                pos.method_10103(x, y + height, z);
                if (level.method_8320((class_2338)pos).method_26204() instanceof class_2346) {
                    return false;
                }
                for (int dy = 0; dy < height; ++dy) {
                    pos.method_10103(x, y + dy, z);
                    class_2680 state = level.method_8320((class_2338)pos);
                    if (!state.method_26227().method_15769() || state.method_26204() instanceof class_2384 || state.method_26214((class_1922)level, (class_2338)pos) < 0.0f) {
                        return false;
                    }
                    for (class_2350 direction : class_2350.values()) {
                        around.method_25505((class_2382)pos, direction);
                        if (level.method_8320((class_2338)around).method_26227().method_15769()) continue;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private long cellKey(class_2338 pos) {
        return TunnelMinerModule.key(this.cellX(pos.method_10263()), this.cellZ(pos.method_10260()));
    }

    private static long key(int x, int z) {
        return (long)x << 32 | (long)z & 0xFFFFFFFFL;
    }

    private static class_2350 beside(class_2338 from, class_2338 to) {
        if (from.method_10264() != to.method_10264()) {
            return null;
        }
        int dx = to.method_10263() - from.method_10263();
        int dz = to.method_10260() - from.method_10260();
        if (Math.abs(dx) + Math.abs(dz) != 1) {
            return null;
        }
        return dx > 0 ? class_2350.field_11034 : (dx < 0 ? class_2350.field_11039 : (dz > 0 ? class_2350.field_11035 : class_2350.field_11043));
    }

    @Override
    public boolean wants(class_2680 state) {
        return state.method_27852(class_2246.field_10260) || state.method_26164(class_3481.field_21490) || STORAGE.contains(state.method_26204());
    }

    @Override
    public void found(long pos, class_2680 state) {
        this.finds.put(pos, state.method_27852(class_2246.field_10260));
    }

    @Override
    public void removed(long pos) {
        this.finds.remove(pos);
    }

    @Override
    public void reset() {
        this.finds.clear();
    }

    private boolean checkFinds(class_746 player, class_638 level) {
        int radius = this.baseRadius.getInt();
        int storage = 0;
        long sumX = 0L;
        long sumZ = 0L;
        class_2338 spawner = null;
        for (Map.Entry<Long, Boolean> entry : this.finds.entrySet()) {
            class_2338 pos = class_2338.method_10092((long)entry.getKey());
            if (Math.abs(pos.method_10263() - this.start.method_10263()) <= 48 && Math.abs(pos.method_10260() - this.start.method_10260()) <= 48 || Math.abs(pos.method_10263() - player.method_31477()) > radius || Math.abs(pos.method_10260() - player.method_31479()) > radius) continue;
            if (entry.getValue().booleanValue()) {
                if (spawner != null || TunnelMinerModule.spiderSpawner(level, pos)) continue;
                spawner = pos;
                continue;
            }
            ++storage;
            sumX += (long)pos.method_10263();
            sumZ += (long)pos.method_10260();
        }
        if (this.spawners.get() && spawner != null) {
            this.found("a spawner", spawner);
            return true;
        }
        if (storage >= this.storageToStop.getInt()) {
            this.found("a base (" + storage + " storage blocks)", new class_2338((int)(sumX / (long)storage), player.method_31478(), (int)(sumZ / (long)storage)));
            return true;
        }
        return false;
    }

    private static boolean spiderSpawner(class_638 level, class_2338 pos) {
        class_2586 class_25862 = level.method_8321(pos);
        if (!(class_25862 instanceof class_2636)) {
            return false;
        }
        class_2636 spawner = (class_2636)class_25862;
        class_1297 shown = spawner.method_11390().method_8283((class_1937)level, pos);
        return shown != null && (shown.method_5864() == class_1299.field_6079 || shown.method_5864() == class_1299.field_6084);
    }

    private void found(String what, class_2338 at) {
        this.stopped = true;
        this.release();
        this.foundWhat = what;
        this.foundAt = at.method_10062();
        EasyPlace.LOG.info("[Tunnel Base Finder] found {} at {} {} {}", new Object[]{what, at.method_10263(), at.method_10264(), at.method_10260()});
        this.say(ModuleChat.body().danger("Found " + what).text(" at ").coords(at.method_10263(), at.method_10264(), at.method_10260()));
        if (this.notificationsOn()) {
            this.mc.method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_14622, (float)1.0f));
        }
        if (!this.disconnect.get()) {
            this.requestDisable();
            return;
        }
        this.leaveAt = this.tick + (long)this.lognormal(260.0, 0.6, 70, 1400);
        this.idleLook = null;
    }

    private void quitOnPop(class_746 player) {
        this.popped = false;
        this.release();
        this.stopped = true;
        class_2338 at = player.method_24515();
        EasyPlace.LOG.info("[Tunnel Base Finder] totem popped at {} {} {}, leaving the server", new Object[]{at.method_10263(), at.method_10264(), at.method_10260()});
        if (this.mc.method_1562() != null) {
            AutoReconnectModule.holdOff();
            this.mc.method_1562().method_48296().method_10747((class_2561)class_2561.method_43470((String)("[Tunnel Base Finder] Totem popped at " + at.method_10263() + " " + at.method_10264() + " " + at.method_10260() + ", left the server")));
        }
        this.requestDisable();
    }

    private void linger(class_746 player) {
        if (this.leaveAt == 0L) {
            return;
        }
        this.idle(player);
        if (this.tick < this.leaveAt) {
            return;
        }
        this.leaveAt = 0L;
        if (this.mc.method_1562() != null) {
            AutoReconnectModule.holdOff();
            this.mc.method_1562().method_48296().method_10747((class_2561)class_2561.method_43470((String)("[Tunnel Base Finder] Found " + this.foundWhat + " at " + this.foundAt.method_10263() + " " + this.foundAt.method_10264() + " " + this.foundAt.method_10260())));
        }
        this.requestDisable();
    }

    private Predicate<class_1799> tool() {
        return this.cellSize == 3 ? SHARD : PICKAXE;
    }

    private class_1799 pickaxe(class_746 player) {
        Predicate<class_1799> tool = this.tool();
        if (tool.test(player.method_6047())) {
            return player.method_6047();
        }
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!tool.test(inventory.method_5438(i))) continue;
            return inventory.method_5438(i);
        }
        return null;
    }

    static boolean shardName(class_1799 stack) {
        String name = stack.method_7964().getString().toLowerCase(Locale.ROOT);
        return name.contains("shard") || name.contains("amethyst");
    }

    private static boolean mendable(class_1799 stack) {
        return stack.method_58657().method_57534().stream().anyMatch(enchantment -> enchantment.method_40225(class_1893.field_9101));
    }

    private static int count(class_746 player, class_1792 item) {
        int total = player.method_6079().method_31574(item) ? player.method_6079().method_7947() : 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            class_1799 stack = inventory.method_5438(i);
            if (!stack.method_31574(item)) continue;
            total += stack.method_7947();
        }
        return total;
    }

    private static int freeSlots(class_746 player) {
        int free = 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!inventory.method_5438(i).method_7960()) continue;
            ++free;
        }
        return free;
    }

    private boolean calm(class_746 player) {
        this.release();
        return InventoryCalm.still(player);
    }

    private void release() {
        if (this.breaker != null) {
            this.breaker.release(this.mc);
        }
        this.breaker = null;
        if (this.hop != null) {
            this.hop.release(this.mc);
        }
        this.hop = null;
        if (this.eater != null) {
            this.eater.release(this.mc);
        }
        this.eater = null;
        if (this.shuffle != null) {
            this.shuffle.release(this.mc);
        }
        this.shuffle = null;
        this.standTarget = null;
        this.streaming = false;
        this.streamTarget = null;
        this.mc.field_1690.field_1904.method_23481(false);
        this.mc.field_1690.field_1894.method_23481(false);
        this.mc.field_1690.field_1881.method_23481(false);
        this.mc.field_1690.field_1913.method_23481(false);
        this.mc.field_1690.field_1849.method_23481(false);
        this.mc.field_1690.field_1903.method_23481(false);
        this.mc.field_1690.field_1867.method_23481(false);
        this.mc.field_1690.field_1832.method_23481(false);
        if (this.mc.field_1761 != null) {
            this.mc.field_1761.method_2925();
        }
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }

    private void feedback(String key, String message, int minimumIntervalTicks) {
        if (!this.chatFeedback.get()) {
            return;
        }
        long last = this.lastFeedback.getOrDefault(key, -4611686018427387904L);
        if (this.tick - last < (long)minimumIntervalTicks) {
            return;
        }
        this.lastFeedback.put(key, this.tick);
        ModuleChat.send((ClientModule)this, message);
    }

    @Environment(value=EnvType.CLIENT)
    private record Node(int x, int z, double cost, double rank, Node parent) {
    }
}

