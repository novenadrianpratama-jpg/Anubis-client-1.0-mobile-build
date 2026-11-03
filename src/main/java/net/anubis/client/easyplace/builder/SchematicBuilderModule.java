/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  fi.dy.masa.litematica.world.WorldSchematic
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1263
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1922
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2281
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2346
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239
 *  net.minecraft.class_2404
 *  net.minecraft.class_2426
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_265
 *  net.minecraft.class_2663
 *  net.minecraft.class_2680
 *  net.minecraft.class_2741
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_3486
 *  net.minecraft.class_3610
 *  net.minecraft.class_3965
 *  net.minecraft.class_465
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_7743
 *  net.minecraft.class_7923
 */
package net.anubis.client.easyplace.builder;

import fi.dy.masa.litematica.world.WorldSchematic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.easyplace.EasyPlace;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.easyplace.builder.BlockUser;
import net.anubis.client.easyplace.builder.Breaker;
import net.anubis.client.easyplace.builder.Bridger;
import net.anubis.client.easyplace.builder.BuildOrder;
import net.anubis.client.easyplace.builder.BuildScan;
import net.anubis.client.easyplace.builder.CellFiller;
import net.anubis.client.easyplace.builder.Climber;
import net.anubis.client.easyplace.builder.Eater;
import net.anubis.client.easyplace.builder.Hands;
import net.anubis.client.easyplace.builder.Hop;
import net.anubis.client.easyplace.builder.Relogger;
import net.anubis.client.easyplace.builder.Scaffold;
import net.anubis.client.easyplace.builder.ScaffoldStore;
import net.anubis.client.easyplace.builder.StandPlanner;
import net.anubis.client.easyplace.builder.Walker;
import net.anubis.client.easyplace.compat.BaritoneCompat;
import net.anubis.client.easyplace.inventory.InventoryPickSourceMode;
import net.anubis.client.easyplace.module.EasyPlaceModule;
import net.anubis.client.easyplace.placement.BuildScope;
import net.anubis.client.easyplace.placement.PlacementConfig;
import net.anubis.client.easyplace.placement.PlacementManager;
import net.anubis.client.easyplace.schematic.SchematicManager;
import net.anubis.client.easyplace.util.BlockStateUtil;
import net.anubis.client.easyplace.util.PlacementUtil;
import net.anubis.client.easyplace.util.StateRules;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.donutmacros.AhPurchase;
import net.anubis.client.module.impl.donutmacros.DonutSell;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1263;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2281;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_2404;
import net.minecraft.class_2426;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2663;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_3965;
import net.minecraft.class_465;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7743;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
public final class SchematicBuilderModule
extends ClientModule {
    private static final int SCAN_TICKS = 20;
    private static final int SCAN_AFTER_PLACE_TICKS = 4;
    private static final int NO_TARGET_TICKS = 4;
    private static final int WALK_FAILS = 2;
    private static final int LEAVES_TO_BUY = 16;
    private static final int SCAFFOLD_DEPTH = 4;
    private static final int SWEEP_TICKS = 1200;
    private static final int NOTHING_MOVED_TICKS = 1200;
    private static final String GLASS_SEARCH = "glass stack";
    private static final int SMALL_NEED = 16;
    private static final int POUR_REST_TICKS = 1200;
    private static final int RELOG_REFUSALS = 8;
    private static final int RELOG_WINDOW_TICKS = 1200;
    private static final int RELOG_GAP_TICKS = 6000;
    private static final int LAVA_FIRST_RETRY = 200;
    private static final double LOW_WEIGHT = 40.0;
    private static final int WALK_CANDIDATES = 16;
    private static final int STUBBORN_TRIES = 3;
    private static final int GIVE_UP_TRIES = 8;
    private static final int BAD_CLIMB_TICKS = 1200;
    private static final float SWIM_DEPTH = 0.4f;
    private static final double CLOSE_REACH = 2.8;
    private static final double CLOSE_STAND = 2.2;
    private static final int TOTEM_POP = 35;
    private static final long SELL_GAP_TICKS = 200L;
    private static final long BUY_SETTLE_TICKS = 40L;
    private static final int BUSY_LIMIT_TICKS = 100;
    private static final int STRAY_MENU_TICKS = 100;
    private static final int RELAX_LIMIT = 32;
    private static final int DEFER_BATCH = 4;
    private static final long PLAN_BUDGET_NANOS = 3000000L;
    private static final int PLAN_TARGETS = 600;
    private static final int BUCKET_BATCH = 8;
    private static final int SPARE_SLOTS = 4;
    private static final int TOTEM_RETRY_TICKS = 1200;
    private static final int SPRINT_RUN = 3;
    private static final int EAT_GAP_TICKS = 60;
    private static final float LOW_HEALTH = 8.0f;
    private static final float SAFE_FALL_HEALTH = 15.0f;
    private static final double MAX_FALL = 5.0;
    private static final float FULL_FALL_HEALTH = 18.0f;
    private static final double FULL_FALL = 8.0;
    private static final int STUCK_GAP_TICKS = 60;
    private static final int ARRIVE_GRACE_TICKS = 30;
    private static final Predicate<class_1799> TOTEM = stack -> stack.method_31574(class_1802.field_8288);
    private static final class_2350[] SUPPORT_SIDES = new class_2350[]{class_2350.field_11033, class_2350.field_11043, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11036};
    private final BooleanSetting buyFromAh = this.addSetting("buy-from-ah", new BooleanSetting("Buy From AH", true));
    private final StringSetting maxPerListing = this.addSetting("max-per-listing", new StringSetting("Max Price Per Listing", "10m").hint("e.g. 10m, one whole listing"));
    private final StringSetting budget = this.addSetting("budget", new StringSetting("Max Total Spend", "").hint("e.g. 500m, empty = no limit"));
    private final NumberSetting refresh = this.addSetting("refresh", new NumberSetting("AH Refresh (ms)", 1000.0f, 300.0f, 3000.0f).step(100.0f));
    private final BooleanSetting climb = this.addSetting("climb", new BooleanSetting("Climb With Leaves", true));
    private final BooleanSetting supportLeaves = this.addSetting("support-leaves", new BooleanSetting("Support Leaves", true));
    private final BooleanSetting walk = this.addSetting("walk", new BooleanSetting("Walk With Baritone", false));
    private final BooleanSetting crafterSlots = this.addSetting("crafter-slots", new BooleanSetting("Crafter Slots", true));
    private final BooleanSetting clickStates = this.addSetting("click-states", new BooleanSetting("Repeaters & Comparators", true));
    private final BooleanSetting placeWater = this.addSetting("place-water", new BooleanSetting("Place Water & Lava", true));
    private final BooleanSetting sellLeftovers = this.addSetting("sell-leftovers", new BooleanSetting("Sell Leftovers", true));
    private final BooleanSetting keepTotem = this.addSetting("keep-totem", new BooleanSetting("Keep Totem In Offhand", true));
    private final BooleanSetting relogOnGhosts = this.addSetting("relog-ghosts", new BooleanSetting("Relog On Ghost Blocks", true));
    private final BooleanSetting placeTnt = this.addSetting("place-tnt", new BooleanSetting("Place TNT", false));
    private final BooleanSetting machineLast = this.addSetting("machine-last", new BooleanSetting("Pistons & Observers Last", true));
    private final BooleanSetting flipLevers = this.addSetting("flip-levers", new BooleanSetting("Flip Levers On", true));
    private final BooleanSetting relaxFacing = this.addSetting("relax-facing", new BooleanSetting("Relax Facing When Stuck", true));
    private final BooleanSetting eat = this.addSetting("eat", new BooleanSetting("Eat When Hungry", true));
    private final NumberSetting tickDelay = this.addSetting("tick-delay", new NumberSetting("Tick Delay", 1.0f, 0.0f, 10.0f).step(1.0f));
    private final BooleanSetting chatFeedback = this.addSetting("chat-feedback", new BooleanSetting("Chat Feedback", true));
    private final class_310 mc = class_310.method_1551();
    private final PlacementManager placement = new PlacementManager(this.mc, (key, message, interval) -> {
        if (key.startsWith("missing-item-") || key.startsWith("unsupported-")) {
            this.feedback(key, message, interval);
        }
    });
    private final SchematicManager schematics = new SchematicManager(this.mc);
    private final Walker walker = new Walker();
    private final Set<Long> scaffold = new HashSet<Long>();
    private final Set<Long> spentSupport = new HashSet<Long>();
    private long proppedAt = Long.MIN_VALUE;
    private boolean leftoversAdopted;
    private final ScaffoldStore scaffoldStore = new ScaffoldStore();
    private String scaffoldWorld;
    private final Set<Long> stuckScaffold = new HashSet<Long>();
    private final Map<Long, Long> supportFor = new HashMap<Long, Long>();
    private final Set<Long> craftersDone = new HashSet<Long>();
    private final Set<Long> clickFailed = new HashSet<Long>();
    private final Set<Long> deferred = new HashSet<Long>();
    private final Set<Long> missingKeys = new HashSet<Long>();
    private final Set<Long> stubborn = new HashSet<Long>();
    private final Map<Long, Integer> tries = new HashMap<Long, Integer>();
    private final Map<Long, Integer> walkFails = new HashMap<Long, Integer>();
    private final Map<Long, Long> badStands = new HashMap<Long, Long>();
    private final Map<Long, Integer> waterFails = new HashMap<Long, Integer>();
    private final Map<Long, Integer> scaffoldFails = new HashMap<Long, Integer>();
    private final Set<Long> badScaffold = new HashSet<Long>();
    private final Set<Long> buildScaffold = new HashSet<Long>();
    private final Map<class_1792, Integer> startCounts = new HashMap<class_1792, Integer>();
    private final Map<class_1792, Integer> bought = new HashMap<class_1792, Integer>();
    private final Map<String, Long> lastFeedbackTick = new HashMap<String, Long>();
    private BuildScan scan;
    private long tick;
    private long nextScan;
    private long lastScan;
    private long placedAtScan;
    private int stuckWalks;
    private class_243 walkFrom;
    private class_2338 walkStand;
    private int climbFails;
    private long noClimbUntil;
    private final Map<Long, Long> badClimbs = new HashMap<Long, Long>();
    private long placingSince;
    private boolean placing;
    private AhPurchase purchase;
    private class_1792 purchaseItem;
    private int purchaseNeed;
    private long lastPlaceTick = -4611686018427387904L;
    private int busyTicks;
    private StandPlanner planner;
    private List<StandPlanner.Step> route;
    private int routeIndex;
    private class_2338 routeTarget;
    private class_2338 routeStand;
    private class_2338 routeStart;
    private StandPlanner.Use routeUse;
    private int sweptLayer = Integer.MAX_VALUE;
    private int refusedSeen;
    private long lastLookUpTrace;
    private final Relogger relogger = new Relogger();
    private final ArrayDeque<Long> refusalTicks = new ArrayDeque();
    private int refusedTracked;
    private class_2338 refusedStand;
    private int refusedHere;
    private long lastRelogTick = -6000L;
    private class_638 seenLevel;
    private boolean sweepDue;
    private boolean sweepPlanning;
    private long sweepSince;
    private int climbTo = Integer.MIN_VALUE;
    private boolean fallsNext;
    private long lavaFirstUntil;
    private boolean lavaPlanning;
    private boolean planWithFalls;
    private int routeItemTicks;
    private long placedAtArrival;
    private long arrivedAt = -4611686018427387904L;
    private long noPlanUntil;
    private long lastStuckTick = -4611686018427387904L;
    private int strayMenuTicks;
    private String purchaseName = "";
    private DonutSell sell;
    private long nextSellTick;
    private Climber climber;
    private Hop hop;
    private Bridger bridger;
    private Breaker breaker;
    private BlockUser user;
    private Eater eater;
    private long nextEatTick;
    private CellFiller filler;
    private class_2338 walkTarget;
    private long spent;
    private long spendLimit;
    private long maxListing;
    private int leavesBought;
    private int bucketsEmptied;
    private boolean retriedDeferred;
    private boolean waterGivenUp;
    private boolean lavaGivenUp;
    private boolean latePhase;
    private long stallSince;
    private long stallProgress = -1L;
    private int leastLeft = Integer.MAX_VALUE;
    private class_2338 stallAt;
    private final Set<Long> lackingKeys = new HashSet<Long>();
    private final Map<Long, Long> pouredAt = new HashMap<Long, Long>();
    private long nextRefillTick;
    private boolean totemPopped;
    private long totemSwapTick;
    private long nextTotemBuyTick;
    private int shownLayer = Integer.MAX_VALUE;
    private boolean walking;
    private boolean armed;
    private int leversFlipped;
    private boolean verified;
    private boolean relaxTried;
    private final List<class_2338> relaxed = new ArrayList<class_2338>();
    private final List<Long> plannedKeys = new ArrayList<Long>();

    public SchematicBuilderModule() {
        super("schematic-builder", "Schematic Builder", "Builds your active Litematica schematic layer by layer: buys missing blocks from the AH, walks with Baritone, climbs with leaves, sets crafter slots, puts the water and lava in and sells what is left over.", ModuleCategory.DONUT);
        this.usesNotifications();
        this.maxPerListing.visibleWhen(this.buyFromAh::get);
        this.budget.visibleWhen(this.buyFromAh::get);
        this.refresh.visibleWhen(this.buyFromAh::get);
        this.placement.setOutcome(new PlacementManager.Outcome(){

            @Override
            public void placed(class_2338 pos) {
                if (SchematicBuilderModule.this.mc.field_1687 != null && SchematicBuilderModule.this.mc.field_1687.method_8320(pos).method_26204() instanceof class_2281) {
                    SchematicBuilderModule.trace("chest confirmed at " + pos.method_23854() + " as " + String.valueOf(SchematicBuilderModule.this.mc.field_1687.method_8320(pos)));
                }
                SchematicBuilderModule.this.tries.merge(pos.method_10063(), 1, Integer::sum);
                SchematicBuilderModule.this.lastPlaceTick = SchematicBuilderModule.this.tick;
                SchematicBuilderModule.this.verified = false;
            }

            @Override
            public void failed(class_2338 pos) {
                SchematicBuilderModule.this.tries.merge(pos.method_10063(), 1, Integer::sum);
                SchematicBuilderModule.this.lastPlaceTick = SchematicBuilderModule.this.tick;
            }
        });
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            this.say(ModuleChat.body().warn("Join a world first."));
            this.requestDisable();
            return;
        }
        if (!this.schematics.hasActiveSchematic()) {
            this.say(ModuleChat.body().warn("No active Litematica placement").text(", load and place a schematic first."));
            this.requestDisable();
            return;
        }
        this.maxListing = AhPurchase.parseMoney(this.maxPerListing.get());
        if (this.buyFromAh.get() && this.maxListing <= 0L) {
            this.say(ModuleChat.body().warn("Set Max Price Per Listing to a price like 10m."));
            this.requestDisable();
            return;
        }
        long l = this.spendLimit = this.budget.get().isBlank() ? 0L : AhPurchase.parseMoney(this.budget.get());
        if (this.buyFromAh.get() && !this.budget.get().isBlank() && this.spendLimit <= 0L) {
            this.say(ModuleChat.body().warn("Set Max Total Spend to a price like 500m, or leave it empty."));
            this.requestDisable();
            return;
        }
        EasyPlaceModule easyPlace = ModuleRegistry.get(EasyPlaceModule.class);
        if (easyPlace != null && easyPlace.isEnabled()) {
            easyPlace.setEnabled(false);
        }
        this.resetState();
        this.snapshotInventory(this.mc.field_1724);
        this.placement.start();
        boolean bl = this.walking = this.walk.get() && BaritoneCompat.canWalk();
        if (this.walk.get() && !this.walking) {
            this.say(ModuleChat.body().warn("Baritone not found").text(", walking with the builder's own planner."));
        }
        if (this.walking) {
            BaritoneCompat.lockForBuild();
        }
        this.on(TickEvent.TYPE, Stage.PRE, event -> {
            this.relogger.tick(this.mc);
            if (!this.relogger.active()) {
                this.placement.tick(this.config());
                this.step();
            }
            if (this.placing && this.mc.field_1755 == null) {
                this.placement.tickPre(this.config());
            }
        });
        this.on(PacketAppliedEvent.TYPE, event -> {
            class_2663 status;
            class_2596<?> patt0$temp = event.packet;
            if (patt0$temp instanceof class_2663 && (status = (class_2663)patt0$temp).method_11470() == 35 && this.mc.field_1687 != null && this.mc.field_1724 != null && status.method_11469((class_1937)this.mc.field_1687) == this.mc.field_1724) {
                this.totemPopped = true;
            }
        });
        this.say(ModuleChat.body().text("Building the schematic layer by layer."));
    }

    @Override
    protected void onDisable() {
        if (this.purchase != null) {
            this.purchase.cancel();
        }
        if (this.sell != null) {
            this.sell.cancel();
        }
        if (this.climber != null) {
            this.climber.release(this.mc);
        }
        if (this.hop != null) {
            this.hop.release(this.mc);
        }
        if (this.bridger != null) {
            this.bridger.release(this.mc);
        }
        if (this.breaker != null && this.mc.field_1761 != null) {
            this.mc.field_1761.method_2925();
        }
        if (this.breaker != null) {
            this.breaker.release(this.mc);
        }
        if (this.user != null) {
            this.user.close(this.mc);
        }
        if (this.eater != null) {
            this.eater.release(this.mc);
        }
        this.walker.stop();
        this.placement.reset();
        if (this.walking) {
            BaritoneCompat.restoreSettings();
        }
        this.scaffoldStore.save(this.scaffoldWorld, this.scaffold);
        this.scaffoldWorld = null;
        this.resetState();
    }

    static boolean tracing() {
        return Boolean.getBoolean("easyplace.builder.debug");
    }

    static void trace(String message) {
        if (Boolean.getBoolean("easyplace.builder.debug")) {
            EasyPlace.LOG.info("[builder] " + message);
        }
    }

    private void resetState() {
        this.scan = null;
        this.tick = 0L;
        this.nextScan = 0L;
        this.lastScan = 0L;
        this.placedAtScan = 0L;
        this.stuckWalks = 0;
        this.placing = false;
        this.purchase = null;
        this.purchaseItem = null;
        this.sell = null;
        this.nextSellTick = 0L;
        this.climber = null;
        this.hop = null;
        this.bridger = null;
        this.breaker = null;
        this.user = null;
        this.eater = null;
        this.nextEatTick = 0L;
        this.filler = null;
        this.planner = null;
        this.route = null;
        this.routeTarget = null;
        this.noPlanUntil = 0L;
        this.lastStuckTick = -4611686018427387904L;
        this.walkTarget = null;
        this.spent = 0L;
        this.leavesBought = 0;
        this.bucketsEmptied = 0;
        this.retriedDeferred = false;
        this.armed = false;
        this.leversFlipped = 0;
        this.verified = false;
        this.relaxTried = false;
        this.relaxed.clear();
        this.waterGivenUp = false;
        this.lavaGivenUp = false;
        this.latePhase = false;
        this.nextRefillTick = 0L;
        this.totemPopped = false;
        this.totemSwapTick = 0L;
        this.nextTotemBuyTick = 0L;
        this.shownLayer = Integer.MAX_VALUE;
        this.sweptLayer = Integer.MAX_VALUE;
        this.refusedSeen = 0;
        this.refusalTicks.clear();
        this.refusedTracked = 0;
        this.refusedStand = null;
        this.refusedHere = 0;
        this.lastRelogTick = -6000L;
        this.seenLevel = null;
        this.climbTo = Integer.MIN_VALUE;
        this.fallsNext = false;
        this.planWithFalls = false;
        this.lavaFirstUntil = 0L;
        this.lavaPlanning = false;
        this.sweepDue = false;
        this.sweepPlanning = false;
        this.scaffold.clear();
        this.spentSupport.clear();
        this.proppedAt = Long.MIN_VALUE;
        this.leftoversAdopted = false;
        this.stuckScaffold.clear();
        this.supportFor.clear();
        this.craftersDone.clear();
        this.clickFailed.clear();
        this.deferred.clear();
        this.missingKeys.clear();
        this.stubborn.clear();
        this.tries.clear();
        this.walkFails.clear();
        this.badStands.clear();
        this.climbFails = 0;
        this.noClimbUntil = 0L;
        this.badClimbs.clear();
        this.pouredAt.clear();
        this.leastLeft = Integer.MAX_VALUE;
        this.stallAt = null;
        this.walkStand = null;
        this.waterFails.clear();
        this.scaffoldFails.clear();
        this.badScaffold.clear();
        this.buildScaffold.clear();
        this.startCounts.clear();
        this.bought.clear();
        this.lastFeedbackTick.clear();
        this.placement.setScope(null);
    }

    private boolean watchRefusals(class_746 player) {
        int refused = this.placement.refusedCount();
        if (refused < this.refusedTracked) {
            this.refusedTracked = refused;
        }
        if (refused > this.refusedTracked) {
            for (int i = this.refusedTracked; i < refused; ++i) {
                this.refusalTicks.addLast(this.tick);
            }
            this.refusedTracked = refused;
            class_2338 at = player.method_24515();
            if (at.equals((Object)this.refusedStand)) {
                if (++this.refusedHere >= 2) {
                    this.badStands.put(at.method_10063(), this.tick + 600L);
                    this.stopMoving();
                    this.route = null;
                    this.noPlanUntil = this.tick;
                    this.refusedHere = 0;
                    this.feedback("refused-move", "The server refuses blocks placed from this spot, trying from another", 100);
                }
            } else {
                this.refusedStand = at;
                this.refusedHere = 1;
            }
        }
        while (!this.refusalTicks.isEmpty() && this.tick - this.refusalTicks.peekFirst() > 1200L) {
            this.refusalTicks.pollFirst();
        }
        if (!this.relogOnGhosts.get() || this.refusalTicks.size() < 8 || this.tick - this.lastRelogTick < 6000L) {
            return false;
        }
        if (this.purchase != null || this.sell != null || this.user != null) {
            return false;
        }
        if (!this.relogger.start(this.mc)) {
            return false;
        }
        this.lastRelogTick = this.tick;
        this.refusalTicks.clear();
        this.say(ModuleChat.body().warn("The server keeps refusing blocks (ghost blocks)").text(": relogging to sync up, the build carries on after."));
        this.releaseMovement();
        return true;
    }

    private boolean watchStall(class_746 player) {
        int left;
        if (this.purchase != null || this.sell != null || this.relogger.active()) {
            this.stallSince = this.tick;
            return false;
        }
        int n = left = this.scan == null ? Integer.MAX_VALUE : this.missingKeys.size() + this.scan.liquids.size() + this.scaffold.size();
        if (this.placement.placedCount() != this.stallProgress || left < this.leastLeft || this.stallAt == null || !this.stallAt.method_19771((class_2382)player.method_24515(), 3.0)) {
            this.stallProgress = this.placement.placedCount();
            this.leastLeft = Math.min(this.leastLeft, left);
            this.stallAt = player.method_24515();
            this.stallSince = this.tick;
            return false;
        }
        if (this.tick - this.stallSince < 1200L) {
            return false;
        }
        this.stallSince = this.tick;
        SchematicBuilderModule.trace("nothing moved for 1200 ticks at " + player.method_24515().method_23854() + ": hop " + (this.hop != null) + " climber " + (this.climber != null) + " bridger " + (this.bridger != null) + " breaker " + (this.breaker == null ? "-" : this.breaker.pos().method_23854()) + " filler " + (String)(this.filler == null ? "-" : String.valueOf((Object)this.filler.kind()) + " " + this.filler.cell().method_23854()) + " user " + (this.user != null) + " eater " + (this.eater != null) + " planner " + (this.planner != null) + " route " + (String)(this.route == null ? "-" : this.routeIndex + "/" + this.route.size()) + " walker " + this.walker.active() + " placing " + this.placing + " noPlan " + (this.noPlanUntil - this.tick) + " layer " + String.valueOf(this.scan == null ? "?" : Integer.valueOf(this.scan.layer)) + " " + this.placement.debugState(player.method_24515()) + " | " + this.scanState());
        this.feedback("stall", "Nothing moved for a minute, starting over from here", 100);
        if (this.filler != null) {
            this.waterFails.put(this.filler.cell().method_10063(), 2);
        }
        if (this.routeTarget != null) {
            this.deferred.add(this.routeTarget.method_10063());
        }
        this.releaseMovement();
        if (this.user != null) {
            this.user.close(this.mc);
        }
        this.user = null;
        this.filler = null;
        this.planner = null;
        this.route = null;
        this.routeTarget = null;
        this.stopMoving();
        this.placement.abort();
        this.noPlanUntil = this.tick;
        this.rescanSoon();
        return true;
    }

    private String scanState() {
        if (this.scan == null) {
            return "no scan";
        }
        StringBuilder out = new StringBuilder("missing " + this.scan.missing.size() + " waiting " + this.scan.waiting.size() + " deferred " + this.deferred.size() + " liquids " + this.scan.liquids.size() + " late " + this.scan.late + " latePhase " + this.latePhase + " whole " + this.scan.wholeSchematic + " waterGivenUp " + this.waterGivenUp + " retriedDeferred " + this.retriedDeferred + " buckets " + String.valueOf(this.mc.field_1724 == null ? "?" : Integer.valueOf(SchematicBuilderModule.count(this.mc.field_1724, class_1802.field_8705))));
        int shown = 0;
        for (BuildScan.Cell cell : this.scan.missing) {
            if (shown++ >= 6) break;
            long key = cell.pos().method_10063();
            out.append(" [").append(cell.pos().method_23854()).append(' ').append(cell.expected().method_26204().method_9518().getString()).append(this.scan.waiting.contains(key) ? " waiting" : "").append(this.deferred.contains(key) ? " deferred" : "").append(this.lackingKeys.contains(key) ? " lacking" : "").append(']');
        }
        return out.toString();
    }

    private void releaseMovement() {
        if (this.climber != null) {
            this.climber.release(this.mc);
        }
        if (this.hop != null) {
            this.hop.release(this.mc);
        }
        if (this.bridger != null) {
            this.bridger.release(this.mc);
        }
        if (this.breaker != null && this.mc.field_1761 != null) {
            this.mc.field_1761.method_2925();
        }
        if (this.breaker != null) {
            this.breaker.release(this.mc);
        }
        if (this.eater != null) {
            this.eater.release(this.mc);
        }
        this.climber = null;
        this.hop = null;
        this.bridger = null;
        this.breaker = null;
        this.eater = null;
        this.filler = null;
        this.planner = null;
        this.route = null;
        this.routeTarget = null;
        this.walker.stop();
        this.placing = false;
    }

    private void restartAfterRejoin() {
        this.releaseMovement();
        if (this.user != null) {
            this.user.close(this.mc);
        }
        this.user = null;
        if (this.purchase != null) {
            this.purchase.cancel();
        }
        this.purchase = null;
        if (this.sell != null) {
            this.sell.cancel();
        }
        this.sell = null;
        this.placement.reset();
        this.refusedTracked = 0;
        this.scan = null;
        this.nextScan = 0L;
        this.noPlanUntil = 0L;
        SchematicBuilderModule.trace("back in the world, starting over from a fresh scan");
    }

    private void keepScaffoldList() {
        String world = ScaffoldStore.worldKey(this.mc);
        if (!Objects.equals(world, this.scaffoldWorld)) {
            this.scaffoldStore.save(this.scaffoldWorld, this.scaffold);
            this.scaffold.clear();
            this.scaffoldWorld = world;
            if (world == null) {
                return;
            }
            Set<Long> earlier = this.scaffoldStore.load(world);
            this.scaffold.addAll(earlier);
            if (!earlier.isEmpty()) {
                this.feedback("scaffold-kept", earlier.size() + " scaffold blocks from an earlier session are still there, they get mined too", 1);
            }
            return;
        }
        if (this.tick % 100L == 0L) {
            this.scaffoldStore.save(this.scaffoldWorld, this.scaffold);
        }
    }

    private PlacementConfig config() {
        return new PlacementConfig(true, true, 180.0, InventoryPickSourceMode.SERVER_RANGE, true, this.tickDelay.getInt(), true, 4.5, true, false, false, false, 360.0);
    }

    private void step() {
        class_746 player = this.mc.field_1724;
        class_638 level = this.mc.field_1687;
        if (player == null || level == null || this.mc.field_1761 == null) {
            this.placing = false;
            return;
        }
        ++this.tick;
        if (level != this.seenLevel) {
            if (this.seenLevel != null) {
                this.restartAfterRejoin();
            }
            this.seenLevel = level;
        }
        if (this.watchRefusals(player)) {
            return;
        }
        this.keepScaffoldList();
        if (this.watchStall(player)) {
            return;
        }
        if (this.mc.field_1755 instanceof class_7743) {
            this.mc.field_1755.method_25419();
            return;
        }
        if (this.mc.field_1755 instanceof class_465 && this.purchase == null && this.sell == null && this.user == null) {
            if (SchematicBuilderModule.tracing()) {
                Object object;
                String string = this.mc.field_1755.method_25440().getString();
                class_239 class_2392 = this.mc.field_1765;
                if (class_2392 instanceof class_3965) {
                    class_3965 hit = (class_3965)class_2392;
                    object = hit.method_17777().method_23854() + " " + String.valueOf(level.method_8320(hit.method_17777()).method_26204());
                } else {
                    object = "-";
                }
                SchematicBuilderModule.trace("menu " + string + " opened; aiming at " + (String)object + " shift " + player.method_5715() + " key " + this.mc.field_1690.field_1832.method_1434() + " hop " + (this.hop != null) + " climber " + (this.climber != null) + " bridger " + (this.bridger != null) + " filler " + (this.filler != null) + " user " + (this.user != null) + " breaker " + (this.breaker != null) + " " + this.placement.debugState(player.method_24515()));
            }
            player.method_7346();
            this.feedback("stray-menu", "Closed a menu that was opened by mistake", 100);
            return;
        }
        if (this.purchase != null) {
            this.placing = false;
            if (this.purchaseItem != null && this.purchase.status() == AhPurchase.Status.RUNNING && SchematicBuilderModule.count(player, this.purchaseItem) >= this.purchaseNeed) {
                this.purchase.cancel();
                this.spent += this.purchase.spent();
                this.bought.merge(this.purchaseItem, this.purchase.boughtItems(), Integer::sum);
                this.feedback("enough-" + this.purchaseName, "Already have enough " + this.purchaseName + ", stopped buying", 1);
                this.purchase = null;
                this.rescanSoon();
                return;
            }
            if (this.keepTotem.get() && this.totemPopped && this.purchaseItem != class_1802.field_8288 && !player.method_6079().method_31574(class_1802.field_8288) && !Hands.has(player, TOTEM)) {
                this.purchase.cancel();
                this.spent += this.purchase.spent();
                if (this.purchaseItem != null) {
                    this.bought.merge(this.purchaseItem, this.purchase.boughtItems(), Integer::sum);
                } else {
                    this.leavesBought += this.purchase.boughtItems();
                }
                this.purchase = null;
                return;
            }
            this.followPurchase();
            return;
        }
        if (this.sell != null) {
            this.placing = false;
            if (!this.sell.tick()) {
                this.sell = null;
                this.nextSellTick = this.tick + 200L;
                this.rescanSoon();
            }
            return;
        }
        if (this.user != null) {
            this.placing = false;
            BlockUser.Result result = this.user.tick(this.mc);
            if (result != BlockUser.Result.RUNNING) {
                if (this.user.crafter()) {
                    this.craftersDone.add(this.user.pos().method_10063());
                    if (result == BlockUser.Result.FAILED) {
                        this.feedback("crafter", "Could not set the crafter at " + this.user.pos().method_23854(), 1);
                    }
                } else if (result == BlockUser.Result.FAILED) {
                    this.clickFailed.add(this.user.pos().method_10063());
                }
                this.user.close(this.mc);
                this.user = null;
                this.rescanSoon();
            }
            return;
        }
        if (this.eater != null) {
            this.placing = false;
            Eater.Result result = this.eater.tick(this.mc);
            if (result != Eater.Result.RUNNING) {
                this.eater.release(this.mc);
                this.eater = null;
            }
            return;
        }
        if (this.climber != null) {
            this.placing = false;
            Climber.Result result = this.climber.tick(this.mc);
            if (result != Climber.Result.RUNNING) {
                this.climber.release(this.mc);
                this.climber = null;
                this.walkFails.clear();
                if (result == Climber.Result.FAILED) {
                    SchematicBuilderModule.trace("climb failed at " + player.method_24515().method_23854() + " in " + String.valueOf(level.method_8316(player.method_24515())));
                    this.badClimbs.put(this.climber.column().method_10063(), this.tick + 1200L);
                    this.route = null;
                    if (++this.climbFails >= 2) {
                        this.climbFails = 0;
                        this.noClimbUntil = this.tick + 600L;
                        this.feedback("climb-pause", "Pillaring keeps failing here, trying other spots for 30s", 1);
                    } else {
                        this.feedback("climb-failed", "Could not climb here", 60);
                    }
                } else {
                    this.climbFails = 0;
                }
                this.rescanSoon();
            }
            return;
        }
        if (this.bridger != null) {
            this.placing = false;
            Bridger.Result result = this.bridger.tick(this.mc);
            if (result != Bridger.Result.RUNNING) {
                this.bridger.release(this.mc);
                if (result == Bridger.Result.FAILED) {
                    SchematicBuilderModule.trace("bridge failed to " + this.bridger.spot().method_23854() + " from " + player.method_24515().method_23854());
                    this.badStands.put(this.bridger.spot().method_10063(), this.tick + 400L);
                    this.route = null;
                }
                this.bridger = null;
                this.rescanSoon();
            }
            return;
        }
        if (this.hop != null) {
            this.placing = false;
            Hop.Result result = this.hop.tick(this.mc);
            if (result != Hop.Result.RUNNING) {
                if (result == Hop.Result.FAILED) {
                    SchematicBuilderModule.trace("hop failed to " + this.hop.spot().method_23854() + " from " + player.method_24515().method_23854() + " y=" + player.method_23318());
                    this.badStands.put(this.hop.spot().method_10063(), this.tick + 400L);
                    this.route = null;
                }
                this.hop = null;
                if (result == Hop.Result.DONE) {
                    this.stuckWalks = 0;
                }
                this.rescanSoon();
            }
            return;
        }
        if (this.breaker != null) {
            this.placing = false;
            Breaker.Result result = this.breaker.tick(this.mc);
            if (result != Breaker.Result.RUNNING) {
                if (result == Breaker.Result.DONE) {
                    long key = this.breaker.pos().method_10063();
                    this.scaffold.remove(key);
                    this.spentSupport.remove(key);
                    this.supportFor.remove(key);
                } else {
                    this.stuckScaffold.add(this.breaker.pos().method_10063());
                }
                this.breaker = null;
                this.rescanSoon();
            }
            return;
        }
        if (this.filler != null) {
            this.placing = false;
            CellFiller.Result result = this.filler.tick(this.mc);
            if (result != CellFiller.Result.RUNNING) {
                this.fillerDone(result == CellFiller.Result.DONE);
                this.filler = null;
                this.rescanSoon();
            }
            return;
        }
        if (this.route != null) {
            this.placing = false;
            if (this.routeIndex < this.route.size()) {
                StandPlanner.Step next;
                class_2338 passing = this.cleanupLeaf(player, this.routeFloor());
                if (passing != null) {
                    this.mc.field_1690.field_1894.method_23481(false);
                    this.mc.field_1690.field_1867.method_23481(false);
                    this.mc.field_1690.field_1903.method_23481(false);
                    if (!player.method_24828() || player.method_18798().method_37268() > 4.0E-4) {
                        return;
                    }
                    SchematicBuilderModule.trace("mining " + passing.method_23854() + " in passing, standing at " + player.method_24515().method_23854());
                    this.stopMoving();
                    this.breaker = new Breaker(passing, false);
                    return;
                }
                class_2338 expected = this.routeIndex == 0 ? this.routeStart : this.route.get(this.routeIndex - 1).pos();
                class_2338 at = player.method_24515();
                if (!player.method_24828()) {
                    return;
                }
                if (expected != null && (at.method_10263() != expected.method_10263() || at.method_10260() != expected.method_10260() || Math.abs(at.method_10264() - expected.method_10264()) > 1)) {
                    this.mc.field_1690.field_1894.method_23481(false);
                    this.mc.field_1690.field_1867.method_23481(false);
                    this.mc.field_1690.field_1903.method_23481(false);
                    if (player.method_18798().method_37268() > 4.0E-4) {
                        this.mc.field_1690.field_1832.method_23481(true);
                        return;
                    }
                    this.mc.field_1690.field_1832.method_23481(false);
                    SchematicBuilderModule.trace("off the route at " + at.method_23854() + ", expected " + expected.method_23854() + ", planning again");
                    this.route = null;
                    this.routeTarget = null;
                    this.noPlanUntil = this.tick;
                    return;
                }
                if ((next = this.route.get(this.routeIndex++)).move() == StandPlanner.Move.WALK || next.move() == StandPlanner.Move.LEAP) {
                    boolean far;
                    boolean through = next.move() == StandPlanner.Move.WALK && this.routeIndex < this.route.size() && this.route.get(this.routeIndex).move() == StandPlanner.Move.WALK;
                    this.hop = new Hop(next.pos(), through, next.move() == StandPlanner.Move.LEAP);
                    boolean bl = far = next.pos().method_19455((class_2382)player.method_24515()) - Math.abs(next.pos().method_10264() - player.method_24515().method_10264()) >= 3;
                    if (this.flatRunAhead(next.pos()) >= 3 || next.move() == StandPlanner.Move.LEAP && far) {
                        this.hop.sprinting();
                    }
                    if (next.move() == StandPlanner.Move.WALK && (Walker.risky(level, player.method_24515()) || Walker.risky(level, next.pos()) || Walker.icy(level, player.method_24515()) || Walker.icy(level, next.pos()))) {
                        if (next.pos().method_10264() >= player.method_24515().method_10264()) {
                            this.hop.careful();
                        } else {
                            this.hop.slow();
                        }
                    }
                    return;
                }
                if (next.move() == StandPlanner.Move.BRIDGE) {
                    class_2338 under = next.pos().method_10074();
                    WorldSchematic schematic = this.schematics.getSchematicWorld();
                    class_1792 material = null;
                    if (!(schematic == null || schematic.method_8320(under).method_26215() || (material = BlockStateUtil.requiredItem(schematic.method_8320(under))) != null && SchematicBuilderModule.count(player, material) > 0)) {
                        SchematicBuilderModule.trace("route bridge into " + under.method_23854() + " without its block");
                        this.route = null;
                        this.noPlanUntil = this.tick + 40L;
                        return;
                    }
                    this.bridger = new Bridger(player.method_24515(), next.pos(), this.scaffold, material);
                    if (this.routeIndex < this.route.size() && this.route.get(this.routeIndex).move() == StandPlanner.Move.BRIDGE && this.route.get(this.routeIndex).pos().method_10059((class_2382)next.pos()).equals((Object)next.pos().method_10059((class_2382)player.method_24515()))) {
                        this.bridger.chained();
                    }
                    return;
                }
                int rise = next.pos().method_10264() - player.method_24515().method_10264();
                class_2338 column = next.pos().method_10087(Math.max(1, rise));
                if (rise < 1 || rise > 2 || !this.pillarFits(level, column) || Climber.leavesCount(player) < rise) {
                    SchematicBuilderModule.trace("route pillar refused at " + player.method_24515().method_23854() + " column=" + column.method_23854() + " y=" + player.method_23318() + " ground=" + player.method_24828());
                    this.route = null;
                    this.noPlanUntil = this.tick + 40L;
                    return;
                }
                this.climber = new Climber(column, this.scaffold, rise);
                return;
            }
            this.route = null;
            this.arrivedAt = this.tick;
            this.placedAtArrival = this.placement.placedCount();
            this.rescanSoon();
        }
        if (this.planner != null) {
            this.placing = false;
            this.followPlanner();
            return;
        }
        if (this.walker.active()) {
            this.placing = false;
            Walker.Result result = this.walker.tick(player, this.tick);
            if (result == Walker.Result.FAILED) {
                if (this.walkStand != null) {
                    this.badStands.put(this.walkStand.method_10063(), this.tick + 1200L);
                }
                if (this.walkTarget != null && this.walkFails.merge(this.walkTarget.method_10063(), 1, Integer::sum) >= 2) {
                    this.deferred.add(this.walkTarget.method_10063());
                }
                if (this.walkFrom != null && player.method_73189().method_1022(this.walkFrom) < 1.0) {
                    ++this.stuckWalks;
                }
            } else if (result == Walker.Result.ARRIVED) {
                this.stuckWalks = 0;
            }
            if (result != Walker.Result.RUNNING) {
                this.walkTarget = null;
                this.rescanSoon();
            }
            return;
        }
        if (this.mc.field_1755 != null) {
            this.placing = false;
            if (player.field_7512 != player.field_7498 && ++this.strayMenuTicks > 100) {
                this.strayMenuTicks = 0;
                player.method_7346();
                this.feedback("stray-menu", "Closed a menu that was left open", 100);
            }
            return;
        }
        this.strayMenuTicks = 0;
        if (this.placement.busy()) {
            this.placing = true;
            if (++this.busyTicks > 100) {
                this.busyTicks = 0;
                this.placement.abort();
                this.feedback("busy-reset", "A placement got stuck, skipping it", 100);
            }
            return;
        }
        this.busyTicks = 0;
        if (this.keepTotem.get() && this.mindTotem(player)) {
            return;
        }
        if (this.eat.get() && Eater.hungry(player) && this.tick >= this.nextEatTick) {
            this.nextEatTick = this.tick + 60L;
            if (Eater.hasFood(player)) {
                this.stopMoving();
                this.eater = new Eater();
                return;
            }
            if (player.method_6032() <= 8.0f) {
                this.feedback("no-food", "Low on health and nothing to eat: health won't come back", 600);
            }
        }
        if (this.scan == null || this.tick >= this.nextScan || this.placement.placedCount() != this.placedAtScan && this.tick - this.lastScan >= 4L) {
            this.rescan(level, player);
        }
        if (this.scan == null) {
            return;
        }
        this.decide(level, player);
    }

    private void fillerDone(boolean done) {
        long key = this.filler.cell().method_10063();
        switch (this.filler.kind()) {
            case SUPPORT_LEAF: {
                if (done) {
                    this.scaffold.add(key);
                    break;
                }
                this.supportFor.remove(key);
                if (this.scaffoldFails.merge(key, 1, Integer::sum) < 2) break;
                this.badScaffold.add(key);
                break;
            }
            case WATER_SOURCE: 
            case WATERLOG: 
            case LAVA_SOURCE: {
                if (done) {
                    ++this.bucketsEmptied;
                    this.pouredAt.put(key, this.tick);
                } else {
                    this.waterFails.merge(key, 1, Integer::sum);
                }
                SchematicBuilderModule.trace((done ? "poured into " : "could not pour into ") + this.filler.cell().method_23854() + " (" + String.valueOf((Object)this.filler.kind()) + ")");
                break;
            }
            case REFILL: {
                if (done) {
                    this.bucketsEmptied = Math.max(0, this.bucketsEmptied - 1);
                    break;
                }
                this.nextRefillTick = this.tick + 100L;
            }
        }
    }

    private boolean mindTotem(class_746 player) {
        if (player.method_6079().method_31574(class_1802.field_8288)) {
            if (this.totemSwapTick > 0L) {
                this.feedback("totem-ok", "Totem is in the offhand", 1);
            }
            this.totemPopped = false;
            this.totemSwapTick = 0L;
            return false;
        }
        if (Hands.has(player, TOTEM)) {
            if (this.totemSwapTick > 0L && this.tick - this.totemSwapTick < 5L) {
                return true;
            }
            if (!Hands.toOffhand(this.mc, player, TOTEM)) {
                return false;
            }
            this.totemSwapTick = this.tick;
            return true;
        }
        if (this.tick < this.nextTotemBuyTick) {
            return false;
        }
        this.nextTotemBuyTick = this.tick + 1200L;
        if (!this.buyFromAh.get() || SchematicBuilderModule.room(player, new class_1799((class_1935)class_1802.field_8288)) <= 0 && !this.startSelling(player, false)) {
            this.feedback("totem-none", "No totem for the offhand" + (this.buyFromAh.get() ? " and no room to buy one" : " (Buy From AH is off)"), 1200);
            this.totemPopped = false;
            return false;
        }
        if (this.sell != null) {
            return true;
        }
        this.feedback("totem-buy", this.totemPopped ? "Totem popped, buying a new one" : "No totem in the offhand, buying one", 1);
        this.totemPopped = false;
        return this.startPurchase(class_1802.field_8288, 1);
    }

    private void debugLookUp(class_638 level, WorldSchematic schematic) {
        if (!SchematicBuilderModule.tracing() || this.tick - this.lastLookUpTrace < 400L || this.scan == null) {
            return;
        }
        for (BuildScan.Cell cell : this.scan.missing) {
            class_2338 pos = cell.pos();
            if (StateRules.lookFor(cell.expected()) != class_2350.field_11036 || level.method_8320(pos.method_10084()).method_26215()) continue;
            StringBuilder out = new StringBuilder("lookup " + pos.method_23854() + " waiting=" + this.scan.waiting.contains(pos.method_10063()) + " deferred=" + this.deferred.contains(pos.method_10063()));
            for (class_2350 side : class_2350.class_2353.field_11062) {
                class_2338 feet = pos.method_10074().method_10093(side);
                double floor = Walker.floor(level, feet);
                class_243 eye = new class_243((double)feet.method_10263() + 0.5, (Double.isNaN(floor) ? (double)feet.method_10264() : floor) + 1.62, (double)feet.method_10260() + 0.5);
                out.append(" ").append(side).append("[feet=").append(level.method_8320(feet).method_26204().method_9518().getString()).append(" head=").append(level.method_8320(feet.method_10084()).method_26204().method_9518().getString()).append(" floor=").append(floor).append(" wait=").append(this.scan.waiting.contains(feet.method_10063())).append(" ok=").append(this.placement.placeableFrom(cell.expected(), pos, eye)).append("]");
            }
            SchematicBuilderModule.trace(out.toString());
            this.lastLookUpTrace = this.tick;
            return;
        }
    }

    private void rescan(class_638 level, class_746 player) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        List<SchematicManager.ActiveBox> boxes = this.schematics.activeBoxes();
        this.nextScan = this.tick + 20L;
        this.lastScan = this.tick;
        if (this.placement.refusedCount() > this.refusedSeen) {
            this.refusedSeen = this.placement.refusedCount();
            this.feedback("refused", "The server refused " + this.refusedSeen + " blocks so far (ghost blocks); placing slower and trying them again", 200);
        }
        if (this.placement.placedCount() != this.placedAtScan) {
            this.stuckWalks = 0;
        }
        this.placedAtScan = this.placement.placedCount();
        if (schematic == null || boxes.isEmpty()) {
            this.scan = null;
            this.placing = false;
            this.feedback("no-schematic", "No active Litematica placement", 100);
            return;
        }
        class_238 body = player.method_5829().method_1014(0.05);
        this.scan = BuildScan.run(level, schematic, boxes, pos -> this.deferred.contains(pos.method_10063()) || body.method_994(new class_238(pos)) || SchematicBuilderModule.enclosed(level, pos), pos -> this.deferred.contains(pos.method_10063()), this::liquidPending, this.latePhase, this.scaffold, this.craftersDone, this.crafterSlots.get(), this.placeTnt.get(), this.machineLast.get());
        this.debugLookUp(level, schematic);
        this.scaffold.removeIf(key -> {
            class_2338 pos = class_2338.method_10092((long)key);
            if (!level.method_22340(pos)) {
                return false;
            }
            class_2680 state = level.method_8320(pos);
            return !Scaffold.block(state) || !schematic.method_8320(pos).method_26215() && StateRules.done(state, schematic.method_8320(pos));
        });
        Iterator<Object> iterator = this.scaffold.iterator();
        while (iterator.hasNext()) {
            long key2 = iterator.next();
            if (schematic.method_8320(class_2338.method_10092((long)key2)).method_26215()) continue;
            this.buildScaffold.add(key2);
        }
        this.missingKeys.clear();
        for (BuildScan.Cell cell2 : this.scan.missing) {
            this.missingKeys.add(cell2.pos().method_10063());
        }
        this.lackingKeys.clear();
        if (!this.buyFromAh.get()) {
            HashMap<class_1792, Boolean> lacks = new HashMap<class_1792, Boolean>();
            for (BuildScan.Cell cell3 : this.scan.missing) {
                if (!SchematicBuilderModule.lacking(player, cell3.expected(), lacks)) continue;
                this.lackingKeys.add(cell3.pos().method_10063());
            }
        }
        this.trackStubborn();
        if (this.scan.layer != Integer.MAX_VALUE && this.scan.layer != this.shownLayer) {
            this.shownLayer = this.scan.layer;
            long left = this.scan.missing.stream().filter(cell -> cell.pos().method_10264() == this.scan.layer).count();
            this.feedback("layer", "Layer Y=" + this.scan.layer + ", " + left + (left == 1L ? " block" : " blocks") + " to place", 1);
        }
    }

    private void trackStubborn() {
        if (!this.scan.wholeSchematic) {
            int top = this.scan.layer + 1;
            this.tries.keySet().removeIf(key -> class_2338.method_10071((long)key) <= top && !this.missingKeys.contains(key));
        } else {
            this.tries.keySet().retainAll(this.missingKeys);
        }
        this.stubborn.clear();
        for (Map.Entry<Long, Integer> entry : this.tries.entrySet()) {
            if (entry.getValue() >= 8) {
                if (!this.deferred.add(entry.getKey())) continue;
                this.feedback("gave-up-" + String.valueOf(entry.getKey()), "The block at " + class_2338.method_10092((long)entry.getKey()).method_23854() + " keeps disappearing, skipping it", 1);
                continue;
            }
            if (entry.getValue() < 3) continue;
            this.stubborn.add(entry.getKey());
        }
    }

    private void rescanSoon() {
        this.nextScan = Math.min(this.nextScan, this.tick + 1L);
    }

    private void decide(class_638 level, class_746 player) {
        class_2338 leaf;
        int lava;
        if (this.armed) {
            this.endOfBuild(level, player);
            return;
        }
        int layer = this.scan.layer;
        WorldSchematic shown = this.schematics.getSchematicWorld();
        class_1792 routeItem = this.routeTarget == null || shown == null ? null : BlockStateUtil.requiredItem(shown.method_8320(this.routeTarget));
        this.routeItemTicks = routeItem != null && SchematicBuilderModule.count(player, routeItem) > 0 ? this.routeItemTicks + 1 : 0;
        int top = layer != Integer.MAX_VALUE ? layer : this.highestMissing();
        int water = top == Integer.MIN_VALUE ? 0 : this.waterWanted(top);
        int n = lava = top == Integer.MIN_VALUE ? 0 : this.cappedLavaWanted(top);
        if (water > 0 && this.refillWater(level, player, water)) {
            return;
        }
        if (this.buyFromAh.get() && top != Integer.MIN_VALUE) {
            Map<class_1792, Integer> needs = this.scan.needs(top);
            if (water > 0) {
                needs.put(class_1802.field_8705, water);
            }
            if (lava > 0) {
                needs.put(class_1802.field_8187, Math.min(lava, 8));
            }
            if (this.buyMissing(player, needs)) {
                return;
            }
        }
        if ((leaf = this.cleanupLeaf(player)) != null) {
            this.breaker = new Breaker(leaf, false);
            this.placing = false;
            return;
        }
        if (this.sweepFinishedLayers(level, player)) {
            return;
        }
        if (this.crafterSlots.get()) {
            for (class_2338 pos2 : this.scan.crafters) {
                if (!this.inReach(player, pos2) || Aim.at(this.mc, pos2) == null) continue;
                this.user = BlockUser.crafter(pos2, this.scan.crafterSlots.get(pos2));
                this.placing = false;
                return;
            }
        }
        if (this.clickStates.get()) {
            for (BuildScan.Cell cell : this.scan.adjust) {
                if (this.clickFailed.contains(cell.pos().method_10063()) || !this.inReach(player, cell.pos()) || Aim.at(this.mc, cell.pos()) == null) continue;
                this.user = BlockUser.adjust(cell.pos(), cell.expected());
                this.placing = false;
                return;
            }
        }
        if (this.placeWater.get() && (this.waterNow(player) || this.cappedLavaNow(level, player))) {
            return;
        }
        if (top == Integer.MIN_VALUE) {
            this.endOfBuild(level, player);
            return;
        }
        class_243 eye = player.method_33571();
        Set<Long> waiting = this.scan.waiting;
        Set<Long> exit = SchematicBuilderModule.lastExit(level, player);
        this.placement.setScope(new BuildScope(top, 40.0, pos -> waiting.contains(pos.method_10063()) || exit.contains(pos.method_10063()) || this.stubborn.contains(pos.method_10063()) && pos.method_46558().method_1022(eye) > 2.8 || this.deferred.contains(pos.method_10063()) && this.tries.getOrDefault(pos.method_10063(), 0) >= 8));
        if (!this.placing) {
            this.placing = true;
            this.placingSince = this.tick;
        }
        if (this.tick - this.placingSince < 4L || this.placement.ticksWithoutTarget() < 4L) {
            return;
        }
        if (this.supportLeaves.get() && this.placeSupport(level, player, top, true)) {
            return;
        }
        if (this.supportLeaves.get() && this.routeTarget != null && this.tick - this.arrivedAt < 34L && this.placeSupportFor(level, player, this.routeTarget)) {
            return;
        }
        if (!this.walking && this.tick >= this.noPlanUntil && this.plan(level, player, top)) {
            return;
        }
        int feet = player.method_24515().method_10264();
        if (this.walking && layer != Integer.MAX_VALUE && layer >= feet + 2 && this.goUp(level, player)) {
            return;
        }
        if (this.walking && layer != Integer.MAX_VALUE && layer >= feet && this.stuckWalks >= 2 && this.goUp(level, player)) {
            this.stuckWalks = 0;
            return;
        }
        if (this.walkTowardsWork(level, player, top)) {
            return;
        }
        if (this.supportLeaves.get() && this.placeSupport(level, player, top, false)) {
            return;
        }
        if (this.walking && layer != Integer.MAX_VALUE && layer >= feet && this.goUp(level, player)) {
            return;
        }
        if (layer == Integer.MAX_VALUE) {
            this.endOfBuild(level, player);
            return;
        }
        if (this.tick - this.lastStuckTick < 60L) {
            return;
        }
        this.lastStuckTick = this.tick;
        BuildScan.Cell stuck = this.nearestOpen(player, top);
        if (stuck != null) {
            SchematicBuilderModule.trace("stuck, skipping " + stuck.pos().method_23854() + " at " + player.method_24515().method_23854());
            this.deferred.add(stuck.pos().method_10063());
        }
        this.feedback("stuck", "Can't reach the rest of layer Y=" + layer + " from here, skipping it for now", 100);
    }

    private static Set<Long> lastExit(class_638 level, class_746 player) {
        class_2338 feet = player.method_24515();
        List<class_2338> exits = Walker.steps(level, feet);
        if (exits.size() != 1) {
            return Set.of();
        }
        class_2338 out = exits.get(0);
        HashSet<Long> keep = new HashSet<Long>();
        for (int dy = 0; dy <= 2; ++dy) {
            keep.add(out.method_10086(dy).method_10063());
        }
        if (out.method_10264() > feet.method_10264()) {
            keep.add(feet.method_10086(2).method_10063());
        }
        keep.add(feet.method_10093(class_2350.method_10147((float)(out.method_10263() - feet.method_10263()), (float)0.0f, (float)(out.method_10260() - feet.method_10260()))).method_10084().method_10063());
        return keep;
    }

    private int highestMissing() {
        int best = Integer.MIN_VALUE;
        for (BuildScan.Cell cell : this.scan.missing) {
            if (this.scan.waiting.contains(cell.pos().method_10063())) continue;
            best = Math.max(best, cell.pos().method_10264());
        }
        return best;
    }

    private boolean goUp(class_638 level, class_746 player) {
        class_2338 spot = Hop.spot(level, player, pos -> false);
        if (spot != null) {
            this.stopMoving();
            this.hop = new Hop(spot);
            return true;
        }
        return this.startClimb(player);
    }

    private boolean placeSupport(class_638 level, class_746 player, int top, boolean waitForNeighbours) {
        for (BuildScan.Cell cell : this.scan.missing) {
            class_2338 pos = cell.pos();
            if (pos.method_10264() > top || this.deferred.contains(pos.method_10063()) || this.scan.waiting.contains(pos.method_10063()) || !this.inReach(player, pos) || cell.expected().method_26204() instanceof class_2346 || StateRules.lookFor(cell.expected()) == class_2350.field_11036 && !pos.equals((Object)this.routeTarget) || waitForNeighbours && (this.missingNeighbour(pos) || SchematicBuilderModule.bridgeable(level, cell.expected(), pos)) || !this.placeSupportFor(level, player, pos)) continue;
            return true;
        }
        return false;
    }

    private static boolean bridgeable(class_638 level, class_2680 expected, class_2338 pos) {
        if (StateRules.lookFor(expected) != null || StateRules.flatLookFor(expected) != null || expected.method_26204() instanceof class_2346 || !expected.method_26234((class_1922)level, pos)) {
            return false;
        }
        for (class_2350 side : class_2350.class_2353.field_11062) {
            class_2338 next = pos.method_10093(side);
            if (!class_2248.method_9501((class_265)level.method_8320(next).method_26218((class_1922)level, next), (class_2350)side.method_10153()) || !Walker.roomAt(level, next.method_10084())) continue;
            return true;
        }
        return false;
    }

    private boolean placeSupportFor(class_638 level, class_746 player, class_2338 pos) {
        if (this.latePhase && this.placeTnt.get() || !this.missingKeys.contains(pos.method_10063()) || this.scan.waiting.contains(pos.method_10063())) {
            return false;
        }
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return false;
        }
        class_2680 expected = schematic.method_8320(pos);
        if (expected.method_26204() instanceof class_2346 || this.placement.placeableFrom(expected, pos, player.method_33571())) {
            return false;
        }
        List<class_2338> chain = this.scaffoldFor(level, expected, pos, player.method_33571());
        if (chain == null) {
            return false;
        }
        class_2338 next = chain.get(0);
        if (!CellFiller.reachable(this.mc, next, CellFiller.Kind.SUPPORT_LEAF)) {
            return false;
        }
        if (Scaffold.count(player) < 1) {
            if (this.buyFromAh.get()) {
                return this.buyLeaves();
            }
            this.feedback("no-scaffold", "Need glass (or leaves) to prop blocks up", 200);
            return false;
        }
        SchematicBuilderModule.trace("scaffold " + next.method_23854() + " (" + chain.size() + " to go) for " + pos.method_23854());
        this.stopMoving();
        this.supportFor.put(next.method_10063(), pos.method_10063());
        if (pos.equals((Object)this.routeTarget)) {
            this.proppedAt = this.tick;
        }
        if (!schematic.method_8320(next).method_26215()) {
            this.buildScaffold.add(next.method_10063());
        }
        this.filler = new CellFiller(next, CellFiller.Kind.SUPPORT_LEAF);
        return true;
    }

    private List<class_2338> scaffoldFor(class_638 level, class_2680 expected, class_2338 pos, class_243 eye) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return null;
        }
        double reach = (this.mc.field_1724 == null ? 4.5 : this.mc.field_1724.method_55754()) - 0.6;
        class_2350 look = StateRules.lookFor(expected);
        class_2350 flat = StateRules.flatLookFor(expected);
        class_238 body = new class_238(eye.field_1352 - 0.3, eye.field_1351 - 1.62, eye.field_1350 - 0.3, eye.field_1352 + 0.3, eye.field_1351 + 0.18, eye.field_1350 + 0.3);
        for (int pass = 0; pass < 2; ++pass) {
            for (class_2338 top : BuildOrder.supportCells(expected, pos)) {
                List<class_2338> beside;
                boolean fromBelowLater;
                boolean fits = pass == 0 ? SchematicBuilderModule.scaffoldFits(level, schematic, top) : this.buildCellFits(level, schematic, top);
                if (!fits || this.badScaffold.contains(top.method_10063()) || body.method_994(new class_238(top))) continue;
                class_2350 toward = class_2350.method_10147((float)(pos.method_10263() - top.method_10263()), (float)(pos.method_10264() - top.method_10264()), (float)(pos.method_10260() - top.method_10260()));
                class_243 face = top.method_46558().method_43206(toward, 0.5);
                class_243 to = face.method_1020(eye);
                boolean bl = fromBelowLater = look == class_2350.field_11036 && top.equals((Object)pos.method_10084());
                if (fromBelowLater ? eye.field_1351 <= (double)top.method_10264() + 0.05 || top.method_46558().method_1022(eye) > reach + 0.5 : to.method_1033() > reach || !PlacementUtil.isFaceVisibleToEye(top, toward, eye) || look != null && !Walker.dominant(to, look) || flat != null && !Walker.facingFlat(to, flat)) continue;
                ArrayList<class_2338> chain = new ArrayList<class_2338>();
                class_2338 cell = top;
                boolean grounded = false;
                for (int i = 0; i < 4 && (i <= 0 || SchematicBuilderModule.scaffoldFits(level, schematic, cell) && !body.method_994(new class_238(cell))) && !(cell.method_46558().method_1022(eye) > reach + 0.5); ++i) {
                    chain.add(cell);
                    if (i == 0 && SchematicBuilderModule.sideBase(level, cell, pos, eye) || SchematicBuilderModule.solidUnder(level, cell) && eye.field_1351 > (double)cell.method_10264() + 0.05) {
                        grounded = true;
                        break;
                    }
                    cell = cell.method_10074();
                }
                if (!grounded && chain.size() >= 1 && SchematicBuilderModule.scaffoldFits(level, schematic, top) && (beside = this.besideChain(level, schematic, top, pos, eye, body, reach)) != null) {
                    return beside;
                }
                if (!grounded || chain.size() > 1 && eye.field_1351 <= (double)top.method_10264() + 0.05) continue;
                Collections.reverse(chain);
                if (Aim.against(this.mc, (class_2338)chain.get(0), null, eye) == null) continue;
                return chain;
            }
        }
        return null;
    }

    private List<class_2338> besideChain(class_638 level, WorldSchematic schematic, class_2338 top, class_2338 forPos, class_243 eye, class_238 body, double reach) {
        if (eye.field_1351 <= (double)top.method_10264() + 0.05) {
            return null;
        }
        for (class_2350 side : class_2350.class_2353.field_11062) {
            class_2338 piece = top.method_10093(side);
            if (piece.equals((Object)forPos) || !SchematicBuilderModule.scaffoldFits(level, schematic, piece) || !SchematicBuilderModule.solidUnder(level, piece) || body.method_994(new class_238(piece)) || piece.method_46558().method_1022(eye) > reach + 0.5 || this.badScaffold.contains(piece.method_10063()) || Aim.against(this.mc, piece, null, eye) == null) continue;
            return new ArrayList<class_2338>(List.of(piece, top));
        }
        return null;
    }

    private boolean buildCellFits(class_638 level, WorldSchematic schematic, class_2338 cell) {
        if (!this.missingKeys.contains(cell.method_10063()) || !level.method_8320(cell).method_26215() || SchematicBuilderModule.waterCell(level, schematic, cell)) {
            return false;
        }
        for (class_2350 way : class_2350.values()) {
            class_2338 next = cell.method_10093(way);
            class_2680 watcher = schematic.method_8320(next);
            if (!(watcher.method_26204() instanceof class_2426) || !next.method_10093((class_2350)watcher.method_11654((class_2769)class_2741.field_12525)).equals((Object)cell)) continue;
            return false;
        }
        return true;
    }

    private static boolean sideBase(class_638 level, class_2338 cell, class_2338 forPos, class_243 eye) {
        for (class_2350 way : class_2350.values()) {
            class_2680 state;
            class_2338 base = cell.method_10093(way);
            if (base.equals((Object)forPos) || (state = level.method_8320(base)).method_45474() || state.method_26218((class_1922)level, base).method_1110() || !PlacementUtil.isFaceVisibleToEye(base, way.method_10153(), eye)) continue;
            return true;
        }
        return false;
    }

    private static boolean solidUnder(class_638 level, class_2338 cell) {
        class_2680 under = level.method_8320(cell.method_10074());
        return !under.method_45474() && !under.method_26218((class_1922)level, cell.method_10074()).method_1110();
    }

    private boolean neededNow(class_2338 pos) {
        int top = this.scan == null || this.scan.layer == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.scan.layer;
        class_2338.class_2339 cell = new class_2338.class_2339();
        for (int dy = -1; dy <= 1; ++dy) {
            if (pos.method_10264() + dy > top) continue;
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    long key = cell.method_10103(pos.method_10263() + dx, pos.method_10264() + dy, pos.method_10260() + dz).method_10063();
                    if (!this.missingKeys.contains(key) || this.deferred.contains(key) || this.scan.waiting.contains(key) || this.lackingKeys.contains(key)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean missingNeighbour(class_2338 pos) {
        for (class_2350 direction : class_2350.values()) {
            long key = pos.method_10093(direction).method_10063();
            if (!this.missingKeys.contains(key) || this.deferred.contains(key) || this.scan.waiting.contains(key) || this.lackingKeys.contains(key)) continue;
            return true;
        }
        return false;
    }

    private boolean pillarFits(class_638 level, class_2338 column) {
        Long bad = this.badClimbs.get(column.method_10063());
        if (bad != null && bad > this.tick) {
            return false;
        }
        if (level.method_8316(column).method_15763((class_1922)level, column) > 0.4f) {
            return false;
        }
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        for (int dy = 0; dy < 2; ++dy) {
            class_2338 cell = column.method_10086(dy);
            if (SchematicBuilderModule.scaffoldFits(level, schematic, cell) || SchematicBuilderModule.runningWaterFits(level, schematic, cell)) continue;
            return false;
        }
        return Climber.hasRoom(this.mc, column);
    }

    private boolean digOut(class_638 level, class_746 player) {
        class_2338 feet = player.method_24515();
        class_2680 here = level.method_8320(feet);
        if (here.method_26215() || here.method_45474() || here.method_26234((class_1922)level, feet) || level.method_8321(feet) instanceof class_1263 || here.method_26214((class_1922)level, feet) < 0.0f) {
            return false;
        }
        for (class_2350 side : class_2350.values()) {
            if (level.method_8316(feet.method_10093(side)).method_15769()) continue;
            return false;
        }
        SchematicBuilderModule.trace("shut in on " + String.valueOf(here) + " at " + feet.method_23854() + ", mining it to pillar out");
        this.feedback("dig-out", "Shut in at " + feet.method_23854() + ", mining the block underfoot to climb out (it goes back in later)", 100);
        this.stopMoving();
        this.breaker = new Breaker(feet, false);
        return true;
    }

    private boolean escapeClimb(class_638 level) {
        class_2338 cell;
        int dy;
        class_2338 column = Climber.column(this.mc);
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (column == null || schematic == null || !Climber.hasRoom(this.mc, column)) {
            return false;
        }
        for (dy = 0; dy < 2; ++dy) {
            cell = column.method_10086(dy);
            if (level.method_8320(cell).method_45474() && (level.method_8316(cell).method_15769() || SchematicBuilderModule.runningWaterFits(level, schematic, cell)) && !(schematic.method_8320(cell).method_26204() instanceof class_2404)) continue;
            return false;
        }
        for (dy = 0; dy < 2; ++dy) {
            cell = column.method_10086(dy);
            if (schematic.method_8320(cell).method_26215()) continue;
            this.buildScaffold.add(cell.method_10063());
        }
        SchematicBuilderModule.trace("shut in at " + column.method_23854() + ", pillaring out");
        this.stopMoving();
        this.climber = new Climber(column, this.scaffold);
        return true;
    }

    private static boolean runningWaterFits(class_638 level, WorldSchematic schematic, class_2338 cell) {
        class_3610 now = level.method_8316(cell);
        if (now.method_15769() || now.method_15771() || !level.method_8320(cell).method_45474()) {
            return false;
        }
        class_2680 planned = schematic == null ? class_2246.field_10124.method_9564() : schematic.method_8320(cell);
        return planned.method_26215() || planned.method_26204() instanceof class_2404 && !planned.method_26227().method_15771();
    }

    static boolean scaffoldFits(class_638 level, WorldSchematic schematic, class_2338 cell) {
        class_2680 there = level.method_8320(cell);
        if (!there.method_45474() || SchematicBuilderModule.waterCell(level, schematic, cell)) {
            return false;
        }
        if (schematic != null && !schematic.method_8320(cell).method_26215()) {
            return false;
        }
        for (class_2350 way : class_2350.values()) {
            class_2680 watcher;
            class_2338 next = cell.method_10093(way);
            class_2680 class_26802 = watcher = schematic != null ? schematic.method_8320(next) : level.method_8320(next);
            if (!(watcher.method_26204() instanceof class_2426) || !next.method_10093((class_2350)watcher.method_11654((class_2769)class_2741.field_12525)).equals((Object)cell)) continue;
            return false;
        }
        return true;
    }

    static boolean waterCell(class_638 level, WorldSchematic schematic, class_2338 pos) {
        return !level.method_8316(pos).method_15769() || schematic != null && !schematic.method_8320(pos).method_26227().method_15769();
    }

    private static boolean floating(class_638 level, class_2338 pos) {
        for (class_2350 direction : class_2350.values()) {
            if (level.method_8320(pos.method_10093(direction)).method_45474()) continue;
            return false;
        }
        return true;
    }

    private boolean buyMissing(class_746 player, Map<class_1792, Integer> needs) {
        if (this.tick - this.lastPlaceTick < 40L) {
            return false;
        }
        ArrayList<Map.Entry<class_1792, Integer>> wanted = new ArrayList<Map.Entry<class_1792, Integer>>(needs.entrySet());
        wanted.sort(Comparator.comparing(entry -> class_7923.field_41178.method_10221((Object)((class_1792)entry.getKey())).toString()));
        for (Map.Entry entry2 : wanted) {
            class_1792 item = (class_1792)entry2.getKey();
            int missing = (Integer)entry2.getValue() - SchematicBuilderModule.count(player, item);
            if (missing <= 0) continue;
            int room = SchematicBuilderModule.room(player, new class_1799((class_1935)item));
            if (item == class_1802.field_8705) {
                room = Math.min(room - 4, 8);
            }
            if (room <= 0) {
                if (!this.startSelling(player, false)) continue;
                return true;
            }
            return this.startPurchase(item, Math.min(SchematicBuilderModule.buyAmount(item, missing), room));
        }
        return false;
    }

    private static int buyAmount(class_1792 item, int missing) {
        int stack = new class_1799((class_1935)item).method_7914();
        if (missing <= 16 || stack <= 16) {
            return missing;
        }
        return (missing + stack - 1) / stack * stack;
    }

    private boolean startPurchase(class_1792 item, int amount) {
        long left = this.remainingBudget();
        if (left < 0L) {
            this.say(ModuleChat.body().warn("Max Total Spend reached").text(", stopping."));
            this.requestDisable();
            return true;
        }
        this.stopMoving();
        this.purchaseItem = item;
        this.purchaseNeed = this.mc.field_1724 == null ? amount : SchematicBuilderModule.count(this.mc.field_1724, item) + amount;
        this.purchaseName = new class_1799((class_1935)item).method_7964().getString();
        this.purchase = item == class_1802.field_8280 ? AhPurchase.startAny(this, class_1802.field_8280, "Glass", GLASS_SEARCH, stack -> stack.method_31574(class_1802.field_8280), amount, this.maxListing, this.refresh.getInt(), left) : AhPurchase.start(this, item, amount, this.maxListing, this.refresh.getInt(), left);
        return true;
    }

    private boolean buyLeaves() {
        class_746 player = this.mc.field_1724;
        if (player == null || SchematicBuilderModule.room(player, new class_1799((class_1935)class_1802.field_8280)) <= 0 && !this.startSelling(player, false)) {
            this.feedback("leaves-room", "No free inventory slot for leaves", 200);
            return false;
        }
        if (this.sell != null) {
            return true;
        }
        long left = this.remainingBudget();
        if (left < 0L) {
            this.say(ModuleChat.body().warn("Max Total Spend reached").text(", stopping."));
            this.requestDisable();
            return true;
        }
        this.stopMoving();
        this.purchaseItem = null;
        this.purchaseName = "Leaves";
        this.purchase = AhPurchase.startAny(this, class_1802.field_8280, "Glass", GLASS_SEARCH, Scaffold::glass, 16, this.maxListing, this.refresh.getInt(), left);
        return true;
    }

    private long remainingBudget() {
        if (this.spendLimit <= 0L) {
            return 0L;
        }
        long left = this.spendLimit - this.spent;
        return left > 0L ? left : -1L;
    }

    private void followPurchase() {
        AhPurchase.Status status = this.purchase.status();
        if (status == AhPurchase.Status.RUNNING) {
            return;
        }
        this.spent += this.purchase.spent();
        if (this.purchaseItem != null) {
            this.bought.merge(this.purchaseItem, this.purchase.boughtItems(), Integer::sum);
        } else {
            this.leavesBought += this.purchase.boughtItems();
        }
        this.purchase = null;
        this.rescanSoon();
        if (status == AhPurchase.Status.FAILED) {
            this.say(ModuleChat.body().danger("Could not buy " + this.purchaseName).text(", Schematic Builder stopped."));
            this.requestDisable();
        }
    }

    private boolean startClimb(class_746 player) {
        if (!this.climb.get()) {
            this.feedback("climb-off", "The next layer is out of reach; turn on Climb With Leaves to go up", 200);
            return false;
        }
        if (Climber.leavesCount(player) < 2) {
            if (this.buyFromAh.get()) {
                return this.buyLeaves();
            }
            this.feedback("no-leaves", "Need at least 2 leaves to climb", 200);
            return false;
        }
        if (this.tick < this.noClimbUntil) {
            return false;
        }
        class_2338 column = Climber.column(this.mc);
        if (column == null || !this.pillarFits(this.mc.field_1687, column)) {
            this.feedback("climb-room", "No room to pillar here (solid ground, no water, no placed block in the way)", 200);
            return false;
        }
        this.stopMoving();
        this.placing = false;
        this.climber = new Climber(column, this.scaffold);
        this.feedback("climbing", "Climbing 2 blocks with leaves", 20);
        return true;
    }

    private boolean walkTowardsWork(class_638 level, class_746 player, int top) {
        ArrayList<BuildScan.Cell> open = new ArrayList<BuildScan.Cell>();
        for (BuildScan.Cell cell2 : this.scan.missing) {
            long key = cell2.pos().method_10063();
            if (cell2.pos().method_10264() > top || this.deferred.contains(key) || this.scan.waiting.contains(key) || SchematicBuilderModule.enclosed(level, cell2.pos())) continue;
            open.add(cell2);
        }
        open.sort(Comparator.comparingDouble(cell -> cell.pos().method_19770((class_2374)player.method_73189())));
        class_2338 at = player.method_24515();
        for (int i = 0; i < open.size() && i < 16; ++i) {
            class_2338 stand;
            BuildScan.Cell cell3 = (BuildScan.Cell)open.get(i);
            if (!this.walking) {
                return false;
            }
            boolean close = this.stubborn.contains(cell3.pos().method_10063());
            class_2338 class_23382 = stand = close ? Walker.standFor(level, player, cell3.pos(), cell3.expected(), this::badStand, 2.2) : Walker.standFor(level, player, cell3.pos(), cell3.expected(), this::badStand);
            if (stand == null || stand.equals((Object)at)) {
                if (this.walkFails.merge(cell3.pos().method_10063(), 1, Integer::sum) < 2) continue;
                this.deferred.add(cell3.pos().method_10063());
                continue;
            }
            this.walkTarget = cell3.pos();
            this.walkTo(player, stand);
            return true;
        }
        return false;
    }

    private boolean badStand(class_2338 pos) {
        Long until = this.badStands.get(pos.method_10063());
        if (until == null) {
            return false;
        }
        if (until > this.tick) {
            return true;
        }
        this.badStands.remove(pos.method_10063());
        return false;
    }

    private boolean plan(class_638 level, class_746 player, int top) {
        this.missedRouteTarget(player);
        if (this.climbTo != Integer.MIN_VALUE) {
            class_2338 column;
            class_2338 class_23382 = column = player.method_24515().method_10264() < this.climbTo && Climber.leavesCount(player) >= 2 && this.tick >= this.noClimbUntil ? Climber.column(this.mc) : null;
            if (column != null && this.pillarFits(level, column)) {
                this.stopMoving();
                this.climber = new Climber(column, this.scaffold);
                return true;
            }
            this.climbTo = Integer.MIN_VALUE;
        }
        ArrayList<StandPlanner.Target> targets = new ArrayList<StandPlanner.Target>();
        HashMap<class_1792, Integer> lacking = new HashMap<class_1792, Integer>();
        for (BuildScan.Cell cell : this.scan.missing) {
            class_1792 item2;
            if (cell.pos().method_10264() > top || this.deferred.contains(cell.pos().method_10063()) || this.scan.waiting.contains(cell.pos().method_10063()) || (item2 = BlockStateUtil.requiredItem(cell.expected())) == null) continue;
            if (SchematicBuilderModule.count(player, item2) <= 0) {
                lacking.merge(item2, 1, Integer::sum);
                continue;
            }
            targets.add(new StandPlanner.Target(cell.pos(), cell.expected(), StandPlanner.Use.PLACE, this.stubborn.contains(cell.pos().method_10063()) ? 2.5999999999999996 : Double.MAX_VALUE));
        }
        if (Hands.has(player, stack -> stack.method_31574(class_1802.field_8705))) {
            for (BuildScan.Liquid liquid : this.scan.liquids) {
                if (liquid.kind() == CellFiller.Kind.LAVA_SOURCE || !this.dueNow(liquid) || !this.waterPending(liquid.pos())) continue;
                targets.add(SchematicBuilderModule.liquidTarget(level, liquid));
            }
        }
        if (Hands.has(player, stack -> stack.method_31574(class_1802.field_8187)) && !this.lavaGivenUp) {
            WorldSchematic schematic = this.schematics.getSchematicWorld();
            ArrayList<StandPlanner.Target> lava = new ArrayList<StandPlanner.Target>();
            for (BuildScan.Liquid liquid : this.scan.liquids) {
                if (!liquid.capped() || this.scan.layer != Integer.MAX_VALUE && liquid.pos().method_10264() >= this.scan.layer || !this.liquidPending(liquid.pos()) || schematic == null || !SchematicBuilderModule.walledIn(level, schematic, liquid.pos())) continue;
                lava.add(SchematicBuilderModule.liquidTarget(level, liquid));
            }
            if (!lava.isEmpty() && this.tick >= this.lavaFirstUntil) {
                this.lavaFirstUntil = this.tick + 200L;
                this.lavaPlanning = true;
                this.startPlanner(level, player, lava);
                return true;
            }
            targets.addAll(lava);
        }
        if (!lacking.isEmpty() && !this.buyFromAh.get()) {
            ArrayList names = new ArrayList();
            lacking.forEach((item, n) -> names.add(n + "x " + new class_1799((class_1935)item).method_7964().getString()));
            this.feedback("lacking", "Missing in inventory: " + String.join((CharSequence)", ", names.subList(0, Math.min(6, names.size()))) + (names.size() > 6 ? " and more" : "") + " (Buy From AH is off)", 400);
        }
        if (targets.isEmpty()) {
            return false;
        }
        this.startPlanner(level, player, targets);
        return true;
    }

    private void missedRouteTarget(class_746 player) {
        if (this.routeTarget != null && this.tick - this.arrivedAt < 34L) {
            long key = this.routeTarget.method_10063();
            if (this.missingKeys.contains(key) && this.placement.placedCount() > this.placedAtArrival) {
                SchematicBuilderModule.trace("placed others instead of " + this.routeTarget.method_23854());
            } else if (this.missingKeys.contains(key) && this.proppedAt >= this.arrivedAt) {
                SchematicBuilderModule.trace("propped " + this.routeTarget.method_23854() + ", placing it comes next");
            } else if (this.missingKeys.contains(key) && this.routeItemTicks < 4) {
                SchematicBuilderModule.trace("arrived without the item for " + this.routeTarget.method_23854());
            } else if (this.missingKeys.contains(key)) {
                boolean now;
                WorldSchematic schematic = this.schematics.getSchematicWorld();
                boolean bl = now = SchematicBuilderModule.tracing() && schematic != null && this.placement.placeableFrom(schematic.method_8320(this.routeTarget), this.routeTarget, player.method_33571());
                if (SchematicBuilderModule.tracing()) {
                    SchematicBuilderModule.trace("arrived but did not place " + this.routeTarget.method_23854() + " standing at " + player.method_24515().method_23854() + " planned " + (this.routeStand == null ? "?" : this.routeStand.method_23854()) + " pos=" + String.valueOf(player.method_73189()) + " exactNow=" + now + " waiting=" + this.scan.waiting.contains(key) + " stubborn=" + this.stubborn.contains(key) + " " + this.placement.debugState(this.routeTarget));
                }
                if (this.walkFails.merge(key, 1, Integer::sum) >= 2) {
                    this.deferred.add(key);
                }
            } else if (this.routeUse == StandPlanner.Use.BREAK && this.scaffold.contains(key)) {
                SchematicBuilderModule.trace("arrived but did not mine the leaf at " + this.routeTarget.method_23854());
                this.walkFails.merge(key, 1, Integer::sum);
            } else if ((this.routeUse == StandPlanner.Use.POUR || this.routeUse == StandPlanner.Use.WATERLOG) && this.scan.liquids.stream().anyMatch(liquid -> liquid.pos().method_10063() == key)) {
                SchematicBuilderModule.trace("arrived but could not pour into " + this.routeTarget.method_23854());
                this.waterFails.merge(key, 1, Integer::sum);
            }
        }
        this.routeTarget = null;
    }

    private static StandPlanner.Target liquidTarget(class_638 level, BuildScan.Liquid liquid) {
        return new StandPlanner.Target(liquid.pos(), level.method_8320(liquid.pos()), liquid.kind() == CellFiller.Kind.WATERLOG ? StandPlanner.Use.WATERLOG : StandPlanner.Use.POUR);
    }

    private void startPlanner(final class_638 level, class_746 player, List<StandPlanner.Target> targets) {
        class_243 at = player.method_73189();
        targets.sort(Comparator.comparingInt(target -> target.pos().method_10264()).thenComparingDouble(target -> target.pos().method_19770((class_2374)at)));
        if (targets.size() > 600) {
            targets = new ArrayList<StandPlanner.Target>(targets.subList(0, 600));
        }
        this.plannedKeys.clear();
        for (StandPlanner.Target target2 : targets) {
            this.plannedKeys.add(target2.pos().method_10063());
        }
        final WorldSchematic schematic = this.schematics.getSchematicWorld();
        boolean scaffoldOk = !this.latePhase || !this.placeTnt.get();
        boolean pillars = this.climb.get() && scaffoldOk && Climber.leavesCount(player) >= 2 && this.tick >= this.noClimbUntil;
        boolean bridges = this.climb.get() && scaffoldOk && !this.sweepPlanning && Climber.leavesCount(player) >= 1;
        SchematicBuilderModule.trace("plan from " + player.method_24515().method_23854() + " targets=" + targets.size() + " first=" + targets.get(0).pos().method_23854() + " pillars=" + pillars + " leaves=" + Climber.leavesCount(player));
        this.planner = new StandPlanner(level, player, targets, pos -> this.badStand((class_2338)pos) || this.scaffold.contains(pos.method_10074().method_10063()) && !SchematicBuilderModule.supports(player, pos.method_10074()) && !this.walkway(pos.method_10074()), pillars ? (pos, onLeaf) -> (onLeaf || Walker.floor(level, pos) == (double)pos.method_10264()) && this.pillarFits(level, pos) : null, bridges ? cell -> SchematicBuilderModule.scaffoldFits(level, schematic, cell) && !this.scan.waiting.contains(cell.method_10063()) : null, this.placement::placeableFrom, new StandPlanner.Support(){

            @Override
            public boolean possible(class_2338 pos, class_243 eye) {
                return schematic != null && SchematicBuilderModule.this.scaffoldFor(level, schematic.method_8320(pos), pos, eye) != null;
            }

            @Override
            public boolean behind(class_2680 expected, class_2338 pos, class_243 eye) {
                return SchematicBuilderModule.this.scaffoldFor(level, expected, pos, eye) != null;
            }
        }).falls(!this.fallsNext ? 0.0 : (player.method_6032() >= 18.0f ? 8.0 : (player.method_6032() >= 15.0f ? 5.0 : 0.0)));
        this.planWithFalls = this.fallsNext;
        this.fallsNext = false;
        this.placing = false;
        this.followPlanner();
    }

    private int flatRunAhead(class_2338 from) {
        StandPlanner.Step step;
        int run = 0;
        for (int i = this.routeIndex; i < this.route.size() && (step = this.route.get(i)).move() == StandPlanner.Move.WALK && step.pos().method_10264() == from.method_10264(); ++i) {
            ++run;
        }
        return run;
    }

    private void followPlanner() {
        class_2338 leaf;
        class_2338 column;
        StandPlanner.Status status = this.planner.step(System.nanoTime() + 3000000L);
        if (status == StandPlanner.Status.RUNNING) {
            return;
        }
        StandPlanner done = this.planner;
        this.planner = null;
        if (status == StandPlanner.Status.NONE && done.visited() <= 2 && this.mc.field_1687 != null && this.mc.field_1724 != null && SchematicBuilderModule.tracing()) {
            SchematicBuilderModule.trace("boxed in: " + Walker.explain(this.mc.field_1687, this.mc.field_1724.method_24515()) + " pos " + String.valueOf(this.mc.field_1724.method_73189()) + " why " + done.whyStuck());
        }
        if (status == StandPlanner.Status.NONE && done.visited() > 2 && SchematicBuilderModule.tracing()) {
            SchematicBuilderModule.trace("plan NONE extent " + done.extent());
        }
        SchematicBuilderModule.trace("plan " + String.valueOf((Object)status) + " visited=" + done.visited() + (String)(status == StandPlanner.Status.FOUND ? " target=" + done.target().pos().method_23854() + " route=" + String.valueOf(done.route()) : ""));
        if (status == StandPlanner.Status.APPROACH && !done.route().isEmpty()) {
            SchematicBuilderModule.trace("approach " + String.valueOf(done.route()));
            if (this.lavaPlanning) {
                this.lavaFirstUntil = 0L;
            }
            this.lavaPlanning = false;
            this.sweepPlanning = false;
            this.route = done.route();
            this.routeIndex = 0;
            this.routeStart = this.mc.field_1724 == null ? null : this.mc.field_1724.method_24515();
            this.routeTarget = null;
            this.routeUse = null;
            this.routeStand = this.route.get(this.route.size() - 1).pos();
            return;
        }
        if (this.lavaPlanning && status == StandPlanner.Status.FOUND) {
            this.lavaFirstUntil = 0L;
        }
        boolean wasLava = this.lavaPlanning;
        this.lavaPlanning = false;
        if (wasLava && status == StandPlanner.Status.NONE) {
            this.noPlanUntil = this.tick;
            return;
        }
        if (status == StandPlanner.Status.NONE && !this.planWithFalls && !this.sweepPlanning && this.mc.field_1724 != null && this.mc.field_1724.method_6032() >= 15.0f) {
            this.fallsNext = true;
            this.noPlanUntil = this.tick;
            return;
        }
        if (this.sweepPlanning && status == StandPlanner.Status.NONE) {
            this.sweepDue = false;
        }
        if (!this.sweepPlanning && status == StandPlanner.Status.NONE) {
            this.deferUnplanned();
        }
        this.sweepPlanning = false;
        if (status == StandPlanner.Status.FOUND && !done.route().isEmpty()) {
            this.route = done.route();
            this.routeIndex = 0;
            this.routeStart = this.mc.field_1724 == null ? null : this.mc.field_1724.method_24515();
            this.routeTarget = done.target().pos();
            this.routeUse = done.target().use();
            this.routeStand = this.route.get(this.route.size() - 1).pos();
            return;
        }
        this.noPlanUntil = this.tick + 40L;
        if (status == StandPlanner.Status.NONE && this.mc.field_1724 != null && this.mc.field_1687 != null && done.lowestTargetY() - this.mc.field_1724.method_24515().method_10264() > 4 && Climber.leavesCount(this.mc.field_1724) >= 2 && this.tick >= this.noClimbUntil && (column = Climber.column(this.mc)) != null && this.pillarFits(this.mc.field_1687, column)) {
            SchematicBuilderModule.trace("far below the work, climbing at " + column.method_23854());
            this.climbTo = done.lowestTargetY() - 4;
            this.stopMoving();
            this.climber = new Climber(column, this.scaffold);
            return;
        }
        if (status == StandPlanner.Status.NONE && done.visited() <= 3 && this.mc.field_1724 != null && this.mc.field_1687 != null && Climber.leavesCount(this.mc.field_1724) >= 2 && (this.escapeClimb(this.mc.field_1687) || this.digOut(this.mc.field_1687, this.mc.field_1724))) {
            return;
        }
        class_746 player = this.mc.field_1724;
        class_2338 class_23382 = leaf = player == null || done.visited() > 3 ? null : this.supportingLeaf(player);
        if (leaf != null && this.dropBelow(leaf) <= 4) {
            SchematicBuilderModule.trace("stranded on the leaf at " + leaf.method_23854() + ", mining it to get down");
            this.stopMoving();
            this.breaker = new Breaker(leaf, true);
        }
    }

    private void deferUnplanned() {
        int counted = 0;
        for (long key : this.plannedKeys) {
            if (counted++ >= 4) break;
            if (this.walkFails.merge(key, 1, Integer::sum) < 2 || !this.deferred.add(key)) continue;
            SchematicBuilderModule.trace("no plan reaches " + class_2338.method_10092((long)key).method_23854() + ", leaving it for later");
        }
    }

    private void walkTo(class_746 player, class_2338 stand) {
        this.placing = false;
        this.walkStand = stand;
        this.walkFrom = player.method_73189();
        this.walker.go(player, stand, this.tick);
    }

    private boolean walkToFix(class_638 level, class_746 player) {
        ArrayList<class_2338> targets = new ArrayList<class_2338>();
        if (this.crafterSlots.get()) {
            targets.addAll(this.scan.crafters);
        }
        if (this.clickStates.get()) {
            for (BuildScan.Cell cell : this.scan.adjust) {
                if (this.clickFailed.contains(cell.pos().method_10063())) continue;
                targets.add(cell.pos());
            }
        }
        targets.sort(Comparator.comparingDouble(pos -> pos.method_19770((class_2374)player.method_73189())));
        if (!this.walking) {
            return this.planToClick(level, player, targets, pos -> {
                if (this.scan.crafters.contains(pos)) {
                    this.craftersDone.add(pos.method_10063());
                } else {
                    this.clickFailed.add(pos.method_10063());
                }
            });
        }
        class_2338 at = player.method_24515();
        for (class_2338 pos2 : targets) {
            class_2338 stand = Walker.standFor(level, player, pos2, class_2246.field_10124.method_9564(), this::badStand);
            if (stand == null || stand.equals((Object)at) || this.walkFails.merge(pos2.method_10063(), 1, Integer::sum) > 2) {
                if (this.scan.crafters.contains(pos2)) {
                    this.craftersDone.add(pos2.method_10063());
                    continue;
                }
                this.clickFailed.add(pos2.method_10063());
                continue;
            }
            this.walkTarget = null;
            this.walkTo(player, stand);
            return true;
        }
        return false;
    }

    private boolean planToClick(class_638 level, class_746 player, List<class_2338> targets, Consumer<class_2338> giveUp) {
        ArrayList<StandPlanner.Target> wanted = new ArrayList<StandPlanner.Target>();
        for (class_2338 pos : targets) {
            if (this.walkFails.getOrDefault(pos.method_10063(), 0) >= 2) {
                giveUp.accept(pos);
                continue;
            }
            wanted.add(new StandPlanner.Target(pos, level.method_8320(pos), StandPlanner.Use.CLICK));
        }
        if (wanted.isEmpty()) {
            return false;
        }
        if (this.tick < this.noPlanUntil) {
            return true;
        }
        this.walkFails.merge(((StandPlanner.Target)wanted.get(0)).pos().method_10063(), 1, Integer::sum);
        this.startPlanner(level, player, wanted);
        return true;
    }

    private boolean flipLevers(class_638 level, class_746 player) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return false;
        }
        ArrayList<class_2338> far = new ArrayList<class_2338>();
        for (class_2338 pos2 : this.scan.levers) {
            if (this.clickFailed.contains(pos2.method_10063())) continue;
            if (this.inReach(player, pos2) && Aim.at(this.mc, pos2) != null) {
                if (!this.armed) {
                    this.feedback("levers", "Everything is in, flipping the levers on last", 1);
                }
                this.armed = true;
                ++this.leversFlipped;
                this.user = BlockUser.lever(pos2, schematic.method_8320(pos2));
                return true;
            }
            far.add(pos2);
        }
        far.sort(Comparator.comparingDouble(pos -> pos.method_19770((class_2374)player.method_73189())));
        return this.planToClick(level, player, far, pos -> this.clickFailed.add(pos.method_10063()));
    }

    private boolean relaxStuck(class_638 level, class_746 player) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return false;
        }
        int changed = 0;
        for (BuildScan.Cell cell : this.scan.missing) {
            if (changed >= 32) break;
            class_2338 pos = cell.pos();
            if (!this.deferred.contains(pos.method_10063()) || !BuildOrder.lookOnly(cell.expected())) continue;
            class_2680 best = null;
            int bestSpots = 0;
            for (class_2680 variant : BuildOrder.turnedVariants(cell.expected())) {
                int spots = this.placeSpots(level, player, pos, variant);
                if (spots <= bestSpots) continue;
                bestSpots = spots;
                best = variant;
            }
            if (best == null) continue;
            SchematicBuilderModule.trace("relaxing " + pos.method_23854() + " " + String.valueOf(cell.expected()) + " -> " + String.valueOf(best) + " (" + bestSpots + " spots)");
            schematic.method_8652(pos, best, 18);
            this.relaxed.add(pos);
            this.deferred.remove(pos.method_10063());
            this.tries.remove(pos.method_10063());
            this.walkFails.remove(pos.method_10063());
            this.stubborn.remove(pos.method_10063());
            ++changed;
        }
        if (changed == 0) {
            return false;
        }
        this.feedback("relax", changed + (changed == 1 ? " block" : " blocks") + " could not go in the schematic's way round, placing them another way", 1);
        this.rescanSoon();
        return true;
    }

    private boolean releaseProps() {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return false;
        }
        boolean released = false;
        Iterator<Map.Entry<Long, Long>> it = this.supportFor.entrySet().iterator();
        while (it.hasNext()) {
            long held = it.next().getValue();
            if (!this.deferred.contains(held) || !this.missingKeys.contains(held) || !BuildOrder.lookOnly(schematic.method_8320(class_2338.method_10092((long)held)))) continue;
            it.remove();
            released = true;
        }
        if (released) {
            this.rescanSoon();
        }
        return released;
    }

    private int placeSpots(class_638 level, class_746 player, class_2338 pos, class_2680 state) {
        double reach = player.method_55754() - 0.65;
        int spots = 0;
        for (int dy = -2; dy <= 2; ++dy) {
            for (int dx = -4; dx <= 4; ++dx) {
                for (int dz = -4; dz <= 4; ++dz) {
                    class_243 eye;
                    class_2338 feet = pos.method_10069(dx, dy, dz);
                    if (feet.equals((Object)pos) || feet.method_10084().equals((Object)pos) || !Walker.standable(level, feet) || (eye = new class_243((double)feet.method_10263() + 0.5, Walker.floor(level, feet) + (double)player.method_5751(), (double)feet.method_10260() + 0.5)).method_1022(pos.method_46558()) > reach || !this.placement.placeableFrom(state, pos, eye)) continue;
                    ++spots;
                }
            }
        }
        return spots;
    }

    private BuildScan.Cell nearestOpen(class_746 player, int top) {
        BuildScan.Cell best = null;
        double bestDist = Double.MAX_VALUE;
        for (BuildScan.Cell cell : this.scan.missing) {
            double dist;
            if (cell.pos().method_10264() > top || this.deferred.contains(cell.pos().method_10063()) || this.scan.waiting.contains(cell.pos().method_10063()) || !((dist = cell.pos().method_19770((class_2374)player.method_73189())) < bestDist)) continue;
            bestDist = dist;
            best = cell;
        }
        return best;
    }

    private void endOfBuild(class_638 level, class_746 player) {
        this.placing = false;
        if (this.armed) {
            this.finishBuild(level, player);
            return;
        }
        if (!this.scan.wholeSchematic) {
            return;
        }
        class_2338 support = this.supportingLeaf(player);
        if (support != null && this.walking) {
            class_2338 aside = Walker.stepAside(level, player, pos -> this.scaffold.contains(pos.method_10074().method_10063()));
            if (aside != null) {
                this.walkTarget = null;
                this.walkTo(player, aside);
                return;
            }
        } else if (support != null) {
            for (class_2338 step : Walker.steps(level, player.method_24515())) {
                if (this.scaffold.contains(step.method_10074().method_10063()) || this.badStand(step)) continue;
                this.badStands.put(step.method_10063(), this.tick + 200L);
                this.stopMoving();
                this.hop = new Hop(step);
                return;
            }
        }
        if (this.walkToFix(level, player)) {
            return;
        }
        if (!this.retriedDeferred && !this.deferred.isEmpty()) {
            this.retriedDeferred = true;
            this.deferred.clear();
            this.walkFails.clear();
            this.clickFailed.clear();
            this.tries.clear();
            this.rescanSoon();
            return;
        }
        if (this.relaxFacing.get() && !this.relaxTried && !this.deferred.isEmpty()) {
            if (this.releaseProps() || this.tick < this.noPlanUntil || this.planner != null || this.sweepScaffold(level, player)) {
                return;
            }
            this.relaxTried = true;
            if (this.relaxStuck(level, player)) {
                return;
            }
        }
        if (this.placeWater.get() && !this.waterGivenUp && this.liquids(level, player, false)) {
            return;
        }
        if (!this.latePhase && this.scan.late > 0) {
            if (!this.leftoversAdopted) {
                this.leftoversAdopted = true;
                int adopted = this.adoptLeftoverScaffold(level);
                if (adopted > 0) {
                    this.feedback("leftover", adopted + " stray glass or leaf blocks are in empty cells of the build, mining them first", 1);
                }
            }
            if (this.tick < this.noPlanUntil || this.planner != null) {
                return;
            }
            if (this.sweepScaffold(level, player)) {
                return;
            }
            if (!this.scaffold.isEmpty()) {
                ArrayList<String> where = new ArrayList<String>();
                for (long key : this.scaffold) {
                    if (where.size() >= 5) continue;
                    where.add(class_2338.method_10092((long)key).method_23854());
                }
                this.say(ModuleChat.body().warn(this.scaffold.size() + " scaffold blocks could not be mined").text(" (" + String.join((CharSequence)"; ", where) + (this.scaffold.size() > 5 ? "; ..." : "") + "). The redstone power and TNT are not placed: mine these, then turn the builder on again."));
                this.requestDisable();
                return;
            }
            this.latePhase = true;
            this.feedback("late", "Every other block is in and no scaffold is left, now the " + this.scan.late + (this.machineLast.get() ? " pistons, observers and redstone power sources, in that order" : " redstone power sources") + (this.placeTnt.get() ? " and TNT (TNT last)" : ""), 1);
            this.rescanSoon();
            return;
        }
        if (this.placeWater.get() && !this.lavaGivenUp && this.liquids(level, player, true)) {
            return;
        }
        if (this.sweepScaffold(level, player)) {
            return;
        }
        if (!this.verified) {
            this.verified = true;
            this.feedback("verify", "Scaffold is out, checking the whole build once more", 1);
            this.rescanSoon();
            return;
        }
        this.finishBuild(level, player);
    }

    private void finishBuild(class_638 level, class_746 player) {
        if (this.flipLevers.get() && this.flipLevers(level, player)) {
            return;
        }
        if (this.startSelling(player, true)) {
            return;
        }
        int held = (int)this.scan.missing.stream().filter(cell -> this.scan.waiting.contains(cell.pos().method_10063())).count();
        int left = this.scan.missing.size() - held;
        int dry = this.liquidsLeft(false);
        int lava = this.liquidsLeft(true);
        ModuleChat.Body body = ModuleChat.body().good("Build finished").text((String)(left == 0 ? "." : ", " + left + " blocks could not be reached."));
        if (this.spent > 0L) {
            body.text(" Spent $").value(AhPurchase.money(this.spent)).text(".");
        }
        if (this.scan.wrong > 0) {
            body.text(" ").warn(this.scan.wrong + " blocks differ from the schematic").text(" and were left alone.");
        }
        if (dry > 0) {
            body.text(" " + dry + " water cells are still dry.");
        }
        if (lava > 0) {
            body.text(" " + lava + " lava cells are still empty.");
        }
        if (!this.scan.poweredTnt.isEmpty()) {
            body.text(" ").warn(this.scan.poweredTnt.size() + " TNT left out").text(", power reaches those spots.");
        }
        if (!this.scan.tntForPlayer.isEmpty()) {
            body.text(" ").warn(this.scan.tntForPlayer.size() + " TNT left for you").text(" (" + SchematicBuilderModule.someOf(this.scan.tntForPlayer) + ")");
            if (!this.scan.afterTntForPlayer.isEmpty()) {
                body.text(", then " + this.scan.afterTntForPlayer.size() + " blocks in front of their pistons (" + SchematicBuilderModule.someOf(this.scan.afterTntForPlayer) + ")");
            }
            body.text(". Place them when you are ready for the machine to run (Litematica shows them as missing).");
        }
        if (held > this.scan.poweredTnt.size()) {
            body.text(" " + (held - this.scan.poweredTnt.size()) + " blocks were held back (sand over a gap, an observer before its block).");
        }
        if (this.scan.leversOff > 0 && !this.flipLevers.get()) {
            body.text(" " + this.scan.leversOff + (this.scan.leversOff == 1 ? " lever is" : " levers are") + " on in the schematic and left off, flip them when you are ready.");
        }
        if (this.scan.leversOff > 0 && this.flipLevers.get()) {
            body.text(" ").warn(this.scan.leversOff + (this.scan.leversOff == 1 ? " lever" : " levers") + " could not be flipped on").text(" (" + SchematicBuilderModule.someOf(this.scan.levers) + ").");
        }
        if (this.leversFlipped > 0) {
            body.text(" " + this.leversFlipped + (this.leversFlipped == 1 ? " lever was" : " levers were") + " flipped on last.");
        }
        if (!this.relaxed.isEmpty()) {
            body.text(" ").warn(this.relaxed.size() + (this.relaxed.size() == 1 ? " block" : " blocks") + " went in facing another way").text(", no spot could place them the schematic's way (" + SchematicBuilderModule.someOf(this.relaxed) + ").");
        }
        if (this.scan.railsOff > 0) {
            body.text(" " + this.scan.railsOff + (this.scan.railsOff == 1 ? " rail has" : " rails have") + " another shape than the schematic, vanilla picks it from the rails around.");
        }
        if (this.scan.noItem > 0) {
            body.text(" " + this.scan.noItem + " cells need blocks without an item.");
        }
        if (!this.scaffold.isEmpty()) {
            body.text(" " + this.scaffold.size() + " scaffold leaves are still there.");
        }
        this.say(body);
        this.requestDisable();
    }

    private static String someOf(List<class_2338> cells) {
        ArrayList<String> shown = new ArrayList<String>();
        for (class_2338 pos : cells) {
            if (shown.size() == 4) break;
            shown.add(pos.method_23854());
        }
        return String.join((CharSequence)"; ", shown) + (cells.size() > 4 ? "; ..." : "");
    }

    private boolean waterNow(class_746 player) {
        if (!Hands.has(player, stack -> stack.method_31574(class_1802.field_8705))) {
            return false;
        }
        for (BuildScan.Liquid target : this.scan.liquids) {
            if (target.kind() == CellFiller.Kind.LAVA_SOURCE || !this.dueNow(target) || !this.waterPending(target.pos()) || !CellFiller.reachable(this.mc, target.pos(), target.kind())) continue;
            this.stopMoving();
            this.filler = new CellFiller(target.pos(), target.kind());
            return true;
        }
        return false;
    }

    private boolean dueNow(BuildScan.Liquid liquid) {
        return liquid.kind() == CellFiller.Kind.WATERLOG || this.scan.layer == Integer.MAX_VALUE || liquid.pos().method_10264() < this.scan.layer;
    }

    private boolean waterPending(class_2338 pos) {
        return this.placeWater.get() && !this.waterGivenUp && this.waterFails.getOrDefault(pos.method_10063(), 0) < 2;
    }

    private boolean liquidPending(class_2338 pos) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null || !schematic.method_8320(pos).method_26227().method_15767(class_3486.field_15518)) {
            return this.waterPending(pos);
        }
        return this.placeWater.get() && !this.lavaGivenUp && this.waterFails.getOrDefault(pos.method_10063(), 0) < 2;
    }

    private boolean cappedLavaNow(class_638 level, class_746 player) {
        if (this.lavaGivenUp || !Hands.has(player, stack -> stack.method_31574(class_1802.field_8187))) {
            return false;
        }
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return false;
        }
        for (BuildScan.Liquid target : this.scan.liquids) {
            class_3610 below;
            class_2338 pos = target.pos();
            if (!target.capped() || this.scan.layer != Integer.MAX_VALUE && pos.method_10264() >= this.scan.layer || !this.liquidPending(pos) || (below = schematic.method_8320(pos.method_10074()).method_26227()).method_15771() && below.method_15767(class_3486.field_15518) && (!level.method_8316(pos.method_10074()).method_15771() || !level.method_8316(pos.method_10074()).method_15767(class_3486.field_15518)) || !SchematicBuilderModule.walledIn(level, schematic, pos) || !CellFiller.reachable(this.mc, pos, target.kind())) continue;
            SchematicBuilderModule.trace("capped lava " + pos.method_23854());
            this.stopMoving();
            this.filler = new CellFiller(pos, target.kind());
            return true;
        }
        return false;
    }

    private int cappedLavaWanted(int top) {
        int wanted = 0;
        for (BuildScan.Liquid liquid : this.scan.liquids) {
            if (!liquid.capped() || liquid.pos().method_10264() >= top || !this.liquidPending(liquid.pos())) continue;
            ++wanted;
        }
        return wanted;
    }

    private int waterWanted(int top) {
        int wanted = 0;
        for (BuildScan.Liquid liquid : this.scan.liquids) {
            if (liquid.kind() == CellFiller.Kind.LAVA_SOURCE || liquid.pos().method_10264() > top || !this.waterPending(liquid.pos())) continue;
            ++wanted;
        }
        return wanted;
    }

    private boolean refillWater(class_638 level, class_746 player, int wanted) {
        if (wanted <= SchematicBuilderModule.count(player, class_1802.field_8705) || SchematicBuilderModule.count(player, class_1802.field_8550) <= 0 || this.tick < this.nextRefillTick) {
            return false;
        }
        class_2338 center = class_2338.method_49638((class_2374)player.method_33571());
        for (class_2338 pos : class_2338.method_10097((class_2338)center.method_10069(-5, -5, -5), (class_2338)center.method_10069(5, 5, 5))) {
            Long poured = this.pouredAt.get(pos.method_10063());
            if (poured != null && this.tick - poured < 1200L || !CellFiller.endless(level, pos) || Aim.water(this.mc, pos) == null) continue;
            this.stopMoving();
            this.filler = new CellFiller(pos.method_10062(), CellFiller.Kind.REFILL);
            return true;
        }
        this.nextRefillTick = this.tick + 40L;
        return false;
    }

    private int liquidsLeft(boolean lava) {
        if (!this.placeWater.get()) {
            return 0;
        }
        return (int)this.scan.liquids.stream().filter(liquid -> liquid.kind() == CellFiller.Kind.LAVA_SOURCE == lava).count();
    }

    private boolean liquids(class_638 level, class_746 player, boolean lava) {
        String what;
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        ArrayList<BuildScan.Liquid> targets = new ArrayList<BuildScan.Liquid>();
        int open = 0;
        for (BuildScan.Liquid target2 : this.scan.liquids) {
            if (target2.kind() == CellFiller.Kind.LAVA_SOURCE != lava || this.waterFails.getOrDefault(target2.pos().method_10063(), 0) >= 2) continue;
            if (lava && !SchematicBuilderModule.walledIn(level, schematic, target2.pos())) {
                ++open;
                continue;
            }
            targets.add(target2);
        }
        if (open > 0) {
            this.feedback("lava-open", open + " lava cells are not walled in, leaving them empty", 400);
        }
        if (targets.isEmpty()) {
            return false;
        }
        targets.sort(Comparator.comparingInt(target -> target.pos().method_10264()).thenComparingDouble(target -> target.pos().method_19770((class_2374)player.method_73189())));
        class_1792 bucket = lava ? class_1802.field_8187 : class_1802.field_8705;
        String string = what = lava ? "lava" : "water";
        if (!Hands.has(player, stack -> stack.method_31574(bucket))) {
            if (!lava && this.refillWater(level, player, targets.size())) {
                return true;
            }
            if (!this.buyFromAh.get()) {
                this.feedback("no-" + what, "Need " + what + " buckets for " + targets.size() + " " + what + " cells", 200);
                this.giveUpLiquid(lava);
                return false;
            }
            int free = SchematicBuilderModule.freeSlots(player);
            if (free <= 0) {
                if (this.startSelling(player, false)) {
                    return true;
                }
                this.feedback(what + "-room", "No free inventory slot for " + what + " buckets", 200);
                this.giveUpLiquid(lava);
                return false;
            }
            this.feedback(what + "-buy", "Buying " + what + " buckets for " + targets.size() + " " + what + " cells", 1);
            return this.startPurchase(bucket, Math.min(targets.size(), Math.min(free, 8)));
        }
        for (BuildScan.Liquid target3 : targets) {
            if (!CellFiller.reachable(this.mc, target3.pos(), target3.kind())) continue;
            this.stopMoving();
            this.filler = new CellFiller(target3.pos(), target3.kind());
            return true;
        }
        if (!this.walking) {
            this.missedRouteTarget(player);
            if (this.tick < this.noPlanUntil) {
                this.giveUpLiquid(lava);
                return false;
            }
            ArrayList<StandPlanner.Target> spots = new ArrayList<StandPlanner.Target>();
            for (BuildScan.Liquid target4 : targets) {
                spots.add(SchematicBuilderModule.liquidTarget(level, target4));
            }
            this.startPlanner(level, player, spots);
            return true;
        }
        class_2338 at = player.method_24515();
        for (BuildScan.Liquid target5 : targets) {
            class_2338 stand = Walker.standFor(level, player, target5.pos(), class_2246.field_10124.method_9564(), this::badStand, 2.8);
            if (stand == null || stand.equals((Object)at)) {
                this.waterFails.merge(target5.pos().method_10063(), 1, Integer::sum);
                continue;
            }
            this.walkTarget = null;
            this.walkTo(player, stand);
            return true;
        }
        return false;
    }

    private boolean sweepFinishedLayers(class_638 level, class_746 player) {
        if (this.scan.layer == Integer.MAX_VALUE || this.walking) {
            return false;
        }
        if (this.sweptLayer == Integer.MAX_VALUE || this.scan.layer > this.sweptLayer) {
            this.sweptLayer = this.scan.layer;
            this.sweepDue = true;
            this.sweepSince = this.tick;
        }
        if (this.sweepDue && this.tick - this.sweepSince > 1200L) {
            this.sweepDue = false;
        }
        if (!this.sweepDue || this.tick < this.noPlanUntil) {
            return false;
        }
        ArrayList<StandPlanner.Target> targets = new ArrayList<StandPlanner.Target>();
        for (long key : this.scaffold) {
            class_2338 pos = class_2338.method_10092((long)key);
            if (pos.method_10264() >= this.scan.layer - 1 || !level.method_22340(pos) || this.walkFails.getOrDefault(key, 0) >= 2 || this.keepScaffold(player, key)) continue;
            targets.add(new StandPlanner.Target(pos, level.method_8320(pos), StandPlanner.Use.BREAK));
        }
        if (targets.isEmpty()) {
            this.sweepDue = false;
            return false;
        }
        this.feedback("layer-sweep", "Mining " + targets.size() + " scaffold blocks under the finished layers", 200);
        this.missedRouteTarget(player);
        this.sweepPlanning = true;
        this.startPlanner(level, player, targets);
        return true;
    }

    private int adoptLeftoverScaffold(class_638 level) {
        WorldSchematic schematic = this.schematics.getSchematicWorld();
        if (schematic == null) {
            return 0;
        }
        int added = 0;
        class_2338.class_2339 cursor = new class_2338.class_2339();
        for (SchematicManager.ActiveBox box : this.schematics.activeBoxes()) {
            for (int y = box.minY(); y <= box.maxY(); ++y) {
                for (int x = box.minX(); x <= box.maxX(); ++x) {
                    for (int z = box.minZ(); z <= box.maxZ(); ++z) {
                        cursor.method_10103(x, y, z);
                        if (!level.method_22340((class_2338)cursor) || !schematic.method_8320((class_2338)cursor).method_26215() || !Scaffold.block(level.method_8320((class_2338)cursor)) || !this.scaffold.add(cursor.method_10063())) continue;
                        ++added;
                    }
                }
            }
        }
        return added;
    }

    private boolean sweepScaffold(class_638 level, class_746 player) {
        if (this.walking || this.scaffold.isEmpty()) {
            return false;
        }
        this.missedRouteTarget(player);
        if (this.tick < this.noPlanUntil) {
            return false;
        }
        ArrayList<StandPlanner.Target> targets = new ArrayList<StandPlanner.Target>();
        for (long key : this.scaffold) {
            class_2338 pos = class_2338.method_10092((long)key);
            if (!level.method_22340(pos) || this.walkFails.getOrDefault(key, 0) >= 2 || this.keepScaffold(player, key)) continue;
            targets.add(new StandPlanner.Target(pos, level.method_8320(pos), StandPlanner.Use.BREAK));
        }
        if (targets.isEmpty()) {
            return false;
        }
        this.feedback("sweep", "Mining " + targets.size() + " scaffold leaves left behind", 200);
        this.startPlanner(level, player, targets);
        return true;
    }

    private void giveUpLiquid(boolean lava) {
        if (lava) {
            this.lavaGivenUp = true;
        } else {
            this.waterGivenUp = true;
        }
    }

    private static boolean walledIn(class_638 level, WorldSchematic schematic, class_2338 pos) {
        return SchematicBuilderModule.walledIn(level, schematic, pos, new HashSet<Long>());
    }

    private static boolean walledIn(class_638 level, WorldSchematic schematic, class_2338 pos, Set<Long> seen) {
        if (!seen.add(pos.method_10063()) || seen.size() > 64) {
            return seen.size() <= 64;
        }
        for (class_2350 direction : class_2350.values()) {
            class_2338 next;
            class_2680 there;
            if (direction == class_2350.field_11036 || !(there = level.method_8320(next = pos.method_10093(direction))).method_45474() || there.method_26227().method_15767(class_3486.field_15518) && there.method_26227().method_15771() || schematic != null && schematic.method_8320(next).method_26227().method_15767(class_3486.field_15518) && SchematicBuilderModule.walledIn(level, schematic, next, seen)) continue;
            return false;
        }
        return true;
    }

    private void snapshotInventory(class_746 player) {
        this.startCounts.clear();
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            class_1799 stack = inventory.method_5438(i);
            if (stack.method_7960()) continue;
            this.startCounts.merge(stack.method_7909(), stack.method_7947(), Integer::sum);
        }
    }

    private boolean startSelling(class_746 player, boolean finished) {
        Map<Integer, class_1792> slots;
        if (!this.sellLeftovers.get() || this.sell != null || this.tick < this.nextSellTick) {
            return false;
        }
        Map<class_1792, Integer> still = Map.of();
        if (!finished) {
            WorldSchematic schematic = this.schematics.getSchematicWorld();
            if (schematic == null || this.mc.field_1687 == null) {
                return false;
            }
            still = BuildScan.remainingItems(this.mc.field_1687, schematic, this.schematics.activeBoxes());
        }
        if ((slots = this.leftoverSlots(player, still)).isEmpty()) {
            this.nextSellTick = this.tick + 200L;
            return false;
        }
        this.stopMoving();
        this.feedback("selling", "Selling leftovers with /sell", 1);
        this.sell = DonutSell.start(this, slots);
        return true;
    }

    private Map<Integer, class_1792> leftoverSlots(class_746 player, Map<class_1792, Integer> still) {
        class_1661 inventory = player.method_31548();
        LinkedHashMap<class_1792, List> where = new LinkedHashMap<class_1792, List>();
        HashMap<class_1792, Integer> have = new HashMap<class_1792, Integer>();
        for (int i = 0; i < 36; ++i) {
            class_1799 stack = inventory.method_5438(i);
            if (stack.method_7960() || stack.method_31574(class_1802.field_8288) || stack.method_31574(class_1802.field_8705) || stack.method_31574(class_1802.field_8187)) continue;
            where.computeIfAbsent(stack.method_7909(), item -> new ArrayList()).add(i);
            have.merge(stack.method_7909(), stack.method_7947(), Integer::sum);
        }
        HashMap<Integer, class_1792> out = new HashMap<Integer, class_1792>();
        for (Map.Entry entry : where.entrySet()) {
            class_1792 item2 = (class_1792)entry.getKey();
            class_1799 sample = new class_1799((class_1935)item2);
            int made = this.bought.getOrDefault(item2, 0) + (item2 == class_1802.field_8550 ? this.bucketsEmptied : 0) + (Scaffold.item(sample) ? this.leavesBought : 0);
            int extra = Math.min(made, (Integer)have.get(item2) - this.startCounts.getOrDefault(item2, 0) - still.getOrDefault(item2, 0));
            if (extra <= 0) continue;
            ArrayList<Integer> slots = new ArrayList<Integer>((Collection)entry.getValue());
            slots.sort(Comparator.comparingInt(slot -> -inventory.method_5438(slot.intValue()).method_7947()));
            Iterator iterator = slots.iterator();
            while (iterator.hasNext()) {
                int slot2 = (Integer)iterator.next();
                int size = inventory.method_5438(slot2).method_7947();
                if (size > extra) continue;
                out.put(slot2, item2);
                extra -= size;
            }
        }
        return out;
    }

    private class_2338 cleanupLeaf(class_746 player) {
        return this.cleanupLeaf(player, Set.of());
    }

    private Set<Long> routeFloor() {
        HashSet<Long> floor = new HashSet<Long>();
        for (int i = Math.max(0, this.routeIndex - 1); i < this.route.size(); ++i) {
            floor.add(this.route.get(i).pos().method_10074().method_10063());
        }
        return floor;
    }

    private class_2338 cleanupLeaf(class_746 player, Set<Long> keep) {
        double reach = player.method_55754();
        Iterator<Long> it = this.scaffold.iterator();
        while (it.hasNext()) {
            long key = it.next();
            class_2338 pos = class_2338.method_10092((long)key);
            if (!this.mc.field_1687.method_22340(pos)) continue;
            if (!Scaffold.block(this.mc.field_1687.method_8320(pos))) {
                it.remove();
                this.spentSupport.remove(key);
                this.supportFor.remove(key);
                this.buildScaffold.remove(key);
                continue;
            }
            Long held = this.supportFor.get(key);
            if (held != null) {
                if (this.missingKeys.contains(held)) continue;
                this.supportFor.remove(key);
                this.spentSupport.add(key);
            }
            if (this.stuckScaffold.contains(key) || SchematicBuilderModule.supports(player, pos) || this.walkway(pos) || keep.contains(key) || !this.buildScaffold.contains(key) && !this.spentSupport.contains(key) && this.neededNow(pos) || pos.method_46558().method_1022(player.method_33571()) > reach || Aim.at(this.mc, pos) == null) continue;
            return pos;
        }
        return null;
    }

    private boolean keepScaffold(class_746 player, long key) {
        class_2338 pos = class_2338.method_10092((long)key);
        Long held = this.supportFor.get(key);
        if (held != null && this.missingKeys.contains(held) && !this.deferred.contains(held)) {
            return true;
        }
        return this.stuckScaffold.contains(key) || SchematicBuilderModule.supports(player, pos) || this.walkway(pos) || !this.buildScaffold.contains(key) && !this.spentSupport.contains(key) && held == null && this.neededNow(pos);
    }

    private boolean walkway(class_2338 pos) {
        return this.scan != null && this.scan.layer != Integer.MAX_VALUE && pos.method_10264() == this.scan.layer - 1 && !this.buildScaffold.contains(pos.method_10063());
    }

    private int dropBelow(class_2338 leaf) {
        for (int depth = 1; depth <= 8; ++depth) {
            class_2338 cell = leaf.method_10087(depth - 1);
            if (Double.isNaN(Walker.floor(this.mc.field_1687, cell))) continue;
            return depth;
        }
        return Integer.MAX_VALUE;
    }

    private class_2338 supportingLeaf(class_746 player) {
        for (long key : this.scaffold) {
            class_2338 pos = class_2338.method_10092((long)key);
            if (!SchematicBuilderModule.supports(player, pos)) continue;
            return pos;
        }
        return null;
    }

    private static boolean supports(class_746 player, class_2338 pos) {
        class_238 cell;
        class_238 body = player.method_5829();
        return body.method_994(cell = new class_238(pos)) || body.method_989(0.0, -0.25, 0.0).method_994(cell);
    }

    private boolean inReach(class_746 player, class_2338 pos) {
        return pos.method_46558().method_1022(player.method_33571()) <= player.method_55754() - 0.3;
    }

    private static boolean enclosed(class_638 level, class_2338 pos) {
        for (class_2350 direction : class_2350.values()) {
            class_2338 next = pos.method_10093(direction);
            if (!level.method_8320(next).method_45474()) continue;
            return false;
        }
        return true;
    }

    private void stopMoving() {
        this.walker.stop();
        this.walkTarget = null;
        this.placing = false;
    }

    private static boolean lacking(class_746 player, class_2680 expected, Map<class_1792, Boolean> seen) {
        class_1792 item = BlockStateUtil.requiredItem(expected);
        if (item == null || item == class_1802.field_8162) {
            return false;
        }
        return seen.computeIfAbsent(item, it -> SchematicBuilderModule.count(player, it) <= 0);
    }

    private static int count(class_746 player, class_1792 item) {
        int total = 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!inventory.method_5438(i).method_31574(item)) continue;
            total += inventory.method_5438(i).method_7947();
        }
        if (player.method_6079().method_31574(item)) {
            total += player.method_6079().method_7947();
        }
        return total;
    }

    private static int room(class_746 player, class_1799 wanted) {
        int room = 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            class_1799 held = inventory.method_5438(i);
            if (held.method_7960()) {
                room += wanted.method_7914();
                continue;
            }
            if (!held.method_31574(wanted.method_7909())) continue;
            room += Math.max(0, held.method_7914() - held.method_7947());
        }
        return room;
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

    private void say(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }

    private void feedback(String key, String message, int minimumIntervalTicks) {
        if (!this.chatFeedback.get()) {
            return;
        }
        long last = this.lastFeedbackTick.getOrDefault(key, -4611686018427387904L);
        if (this.tick - last < (long)minimumIntervalTicks) {
            return;
        }
        this.lastFeedbackTick.put(key, this.tick);
        ModuleChat.send((ClientModule)this, message);
    }
}

