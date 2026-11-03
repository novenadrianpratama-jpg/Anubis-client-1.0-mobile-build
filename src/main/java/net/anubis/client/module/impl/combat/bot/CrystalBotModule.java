/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1269
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1684
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1890
 *  net.minecraft.class_1893
 *  net.minecraft.class_1922
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2378
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_2664
 *  net.minecraft.class_2680
 *  net.minecraft.class_2708
 *  net.minecraft.class_2709
 *  net.minecraft.class_2761
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_3481
 *  net.minecraft.class_3486
 *  net.minecraft.class_3489
 *  net.minecraft.class_3532
 *  net.minecraft.class_3726
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_4969
 *  net.minecraft.class_638
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_7924
 *  net.minecraft.class_8143
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.combat.bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.easyplace.builder.Aim;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.AnchorAuraModule;
import net.anubis.client.module.impl.combat.AutoCityModule;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.AutoTotemModule;
import net.anubis.client.module.impl.combat.AutoTrapModule;
import net.anubis.client.module.impl.combat.CrystalAuraModule;
import net.anubis.client.module.impl.combat.anchor.AnchorGuard;
import net.anubis.client.module.impl.combat.bot.AirTrack;
import net.anubis.client.module.impl.combat.bot.BotBody;
import net.anubis.client.module.impl.combat.bot.BotKit;
import net.anubis.client.module.impl.combat.bot.BotPause;
import net.anubis.client.module.impl.combat.bot.PearlFlight;
import net.anubis.client.module.impl.combat.bot.RivalBook;
import net.anubis.client.module.impl.combat.bot.StandPicker;
import net.anubis.client.module.impl.combat.crystal.Combo;
import net.anubis.client.module.impl.combat.crystal.CrystalDamage;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.ServerRotation;
import net.anubis.client.util.pvp.TargetingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1684;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2378;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2664;
import net.minecraft.class_2680;
import net.minecraft.class_2708;
import net.minecraft.class_2709;
import net.minecraft.class_2761;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4969;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_7924;
import net.minecraft.class_8143;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class CrystalBotModule
extends ClientModule {
    private static final double MIN = 2.4;
    private static final double IDEAL = 3.2;
    private static final double MAX = 4.0;
    private static final Predicate<class_1799> GAPPLE = stack -> stack.method_31574(class_1802.field_8463) || stack.method_31574(class_1802.field_8367);
    private static final Predicate<class_1799> XP = stack -> stack.method_31574(class_1802.field_8287);
    private static final Predicate<class_1799> PEARL = stack -> stack.method_31574(class_1802.field_8634);
    private static final Predicate<class_1799> OBSIDIAN = stack -> stack.method_31574(class_1802.field_8281);
    private static final Predicate<class_1799> ANCHOR = stack -> stack.method_31574(class_1802.field_23141);
    private static final Predicate<class_1799> GLOWSTONE = stack -> stack.method_31574(class_1802.field_8801);
    private static final float ARMOR_LOW = 0.3f;
    private static final int RUN_HOLD = 40;
    private static final int POP_COVER = 40;
    private static final int LAUNCH_GAP = 80;
    private static final int LAUNCH_STALL = 20;
    private static final int LAUNCH_TIMEOUT = 16;
    private static final double LAUNCH_START = 3.9;
    private static final double LAUNCH_GIVE_UP = 4.1;
    private static final double LAUNCH_STEP_IN = 2.9;
    private static final int LAUNCH_WATCH = 8;
    private static final double LAUNCH_LIFT = 0.3;
    private static final double HIT_RANGE = 2.95;
    private static final int THROW_TIMEOUT = 14;
    private static final int PEARL_GAP = 30;
    private static final int SHIELD_GAP = 20;
    private static final int BREACH_WAIT = 40;
    private static final int BREACH_WAIT_CAMPER = 15;
    private static final int BREACH_GAP = 100;
    private static final double ESCAPE_FAR = 20.0;
    private static final double ESCAPE_NEAR = 12.0;
    private static final int SEARCH_GAP = 10;
    private static final double EAT_CLEAR = 8.0;
    private static final int FOLLOW_TICKS = 12;
    private static final int OPENER_WINDOW = 40;
    private static final int CAMP_MAX = 2;
    private static final int SPOT_SCAN_GAP = 5;
    private static final float SPOT_DANGER = 8.0f;
    private static final int WORKING = 0;
    private static final int PLACED = 1;
    private static final int REGROUP_TICKS = 300;
    private static final double REGROUP_RANGE = 64.0;
    private static final int BOX_AIM_TIMEOUT = 10;
    private static final float RETREAT_HEALTH = 6.0f;
    private static final int POP_STREAK = 3;
    private static final int POP_STREAK_GAP = 600;
    private static final int POP_RUN = 200;
    private static final double BLAST_WATCH = 7.0;
    private static final double BELOW_DROP = 3.0;
    private static final double BELOW_REACH = 8.0;
    private static final double DIG_DEEPEST = 6.0;
    private static final int BELOW_SETTLE = 20;
    private static final int DIG_TIMEOUT = 80;
    private static final int DIG_WALK = 40;
    private static final int DIG_MAX = 2;
    private static final int DIG_GAP = 100;
    private static final int DIG_DOWN_TICKS = 50;
    private static final int DIG_BASE_TICKS = 25;
    private static final float LEVEL_MIN_DAMAGE = 2.5f;
    private final NumberSetting range = this.addSetting("range", new NumberSetting("Fight Range", 16.0f, 8.0f, 32.0f).step(1.0f));
    private final BooleanSetting anchors = this.addSetting("anchors", new BooleanSetting("Use Anchors", true));
    private final BooleanSetting launches = this.addSetting("knock-up", new BooleanSetting("Knock-Up Hits", true));
    private final BooleanSetting breach = this.addSetting("break-holes", new BooleanSetting("Break Holes", true));
    private final BooleanSetting guardAnchors = this.addSetting("guard-anchors", new BooleanSetting("Guard Anchors", true));
    private final BooleanSetting surround = this.addSetting("surround", new BooleanSetting("Surround Self", true));
    private final NumberSetting eatBelow = this.addSetting("eat-below", new NumberSetting("Gapple Below HP", 14.0f, 0.0f, 20.0f).step(1.0f));
    private final BooleanSetting mendArmor = this.addSetting("mend-armor", new BooleanSetting("Mend Armor", true));
    private final BooleanSetting escape = this.addSetting("escape-pearls", new BooleanSetting("Escape Pearl", true));
    private final BooleanSetting chase = this.addSetting("chase-pearls", new BooleanSetting("Chase Pearls", true));
    private final BooleanSetting refill = this.addSetting("refill", new BooleanSetting("Refill Hotbar", true));
    private final BooleanSetting learn = this.addSetting("learn", new BooleanSetting("Learn Rivals", true));
    private final BooleanSetting dance = this.addSetting("strafe", new BooleanSetting("Strafe", true));
    private final class_310 mc = class_310.method_1551();
    private final BotBody body = new BotBody();
    private final BotKit kit = new BotKit();
    private final RivalBook book = new RivalBook();
    private final BotPause pause = new BotPause();
    private final AirTrack air = new AirTrack();
    private class_1657 challenger;
    private int challengerSince;
    private int attackedBy = -1;
    private int attackedAt = -1000;
    private double closest;
    private int closerAt;
    private StandPicker.Spot standing;
    private int standScanAt;
    private int defuseAt = -1000;
    private int retreatAt = -1000;
    private Boolean crystalWasOn;
    private Boolean anchorWasOn;
    private Boolean totemWasOn;
    private int popStreak;
    private boolean poppedOut;
    private class_2338 digAnchor;
    private boolean digPlaced;
    private boolean digAnnounced;
    private int digGuards;
    private int digUntil;
    private int digWalkUntil;
    private int digBlasts;
    private int digAfter;
    private int belowSince = -1;
    private int stepDigAfter;
    private int baseDigAfter;
    private int diggingAt = -100;
    private boolean eating;
    private boolean needsGapple;
    private boolean mending;
    private class_2338 boxingAt;
    private int boxingUntil;
    private int tick;
    private class_1657 fought;
    private Stance stance = Stance.HOLD;
    private int runUntil;
    private int ownPopAt = -1000;
    private Throw throwing;
    private Rotation throwLook;
    private class_243 throwSpot;
    private double throwAway;
    private int throwUntil;
    private int pearlAt = -1000;
    private int searchAfter;
    private int theirPearl = -1;
    private int followedPearl = -1;
    private boolean launching;
    private int launchUntil;
    private int nextLaunch;
    private int launchedAt = -1;
    private double launchY;
    private double launchPeak;
    private int shieldAt = -1000;
    private class_2338 boxAiming;
    private int boxAimTicks;
    private class_2338 boxSkipped;
    private int boxSkippedUntil;
    private int followUntil;
    private float selfCapNow;
    private boolean followLaid = true;
    private class_2338 blocking;
    private int spotScanAt;
    private int cityFails;
    private int openerArm = -1;
    private double openerReach;
    private int openerAt;
    private float openerHealth;
    private int openerPops;
    private boolean cityStarted;
    private int breachAfter;
    private float lastTheirHealth;
    private int hurtAt;
    private int progressAt;
    private int theirPopAt = -1;
    private int holeSince = -1;
    private boolean lowSpell;
    private boolean pearledInSpell;

    public CrystalBotModule() {
        super("crystal-bot", "Crystal PvP Bot", "Crystal-fights the nearest enemy player: steers the Crystal and Anchor Aura, knocks them up, breaks their hole, guards, eats, mends, refills and pearls.", ModuleCategory.COMBAT);
    }

    @Override
    protected void onEnable() {
        this.body.forget();
        this.needsGapple = false;
        this.cityStarted = false;
        this.launching = false;
        this.mending = false;
        this.eating = false;
        this.boxingAt = null;
        this.fought = null;
        this.throwing = null;
        this.tick = 0;
        this.runUntil = 0;
        this.boxSkippedUntil = 0;
        this.followUntil = 0;
        this.nextLaunch = 0;
        this.launchedAt = -1;
        this.shieldAt = -1000;
        this.pearlAt = -1000;
        this.ownPopAt = -1000;
        this.baseDigAfter = 0;
        this.stepDigAfter = 0;
        this.standScanAt = 0;
        this.spotScanAt = 0;
        this.breachAfter = 0;
        this.searchAfter = 0;
        this.retreatAt = -1000;
        this.attackedAt = -1000;
        this.defuseAt = -1000;
        this.attackedBy = -1;
        this.challenger = null;
        this.standing = null;
        this.air.forget();
        this.pause.reset();
        this.totemWasOn = null;
        this.anchorWasOn = null;
        this.crystalWasOn = null;
        this.popStreak = 0;
        this.forgetDig();
        this.on(TickEvent.TYPE, Stage.PRE, event -> this.tick());
    }

    @Override
    protected void onDisable() {
        this.body.release(this.mc);
        this.body.forget();
        CombatMacroLock.release(this);
        this.restoreAuras();
        AutoCityModule city = ModuleRegistry.get(AutoCityModule.class);
        if (this.cityStarted && city != null && city.isEnabled()) {
            city.setEnabled(false);
        }
        this.cityStarted = false;
        this.book.save(true);
    }

    @EventHandler
    public void onPacketApplied(PacketAppliedEvent event) {
        class_2663 status;
        class_746 player = this.mc.field_1724;
        if (player == null || this.mc.field_1687 == null) {
            return;
        }
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_2663 && (status = (class_2663)class_25962).method_11470() == 35) {
            if (status.method_11469((class_1937)this.mc.field_1687) == player) {
                this.popStreak = this.tick - this.ownPopAt <= 600 ? this.popStreak + 1 : 1;
                this.ownPopAt = this.tick;
            } else if (this.fought != null && status.method_11469((class_1937)this.mc.field_1687) == this.fought) {
                this.theirPopAt = this.tick;
                ++this.openerPops;
            }
        }
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        class_746 player = this.mc.field_1724;
        if (player == null) {
            return;
        }
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_2708) {
            class_2708 moved = (class_2708)class_25962;
            class_243 to = moved.comp_3228().comp_3148();
            class_243 from = player.method_73189();
            Set relative = moved.comp_3229();
            this.pause.moved(new class_243(relative.contains(class_2709.field_12400) ? from.field_1352 + to.field_1352 : to.field_1352, relative.contains(class_2709.field_12398) ? from.field_1351 + to.field_1351 : to.field_1351, relative.contains(class_2709.field_12403) ? from.field_1350 + to.field_1350 : to.field_1350), this.tick - this.pearlAt);
        } else {
            class_25962 = event.packet;
            if (class_25962 instanceof class_2761) {
                class_2761 time = (class_2761)class_25962;
                this.pause.time(time.comp_3219());
            } else {
                class_25962 = event.packet;
                if (class_25962 instanceof class_2664) {
                    class_2664 blast = (class_2664)class_25962;
                    this.air.blast(blast.comp_2883());
                } else {
                    class_8143 hurt;
                    class_25962 = event.packet;
                    if (class_25962 instanceof class_8143 && (hurt = (class_8143)class_25962).comp_1267() == player.method_5628() && hurt.comp_1269() >= 0) {
                        this.attackedBy = hurt.comp_1269();
                        this.attackedAt = this.tick;
                    }
                }
            }
        }
    }

    private void tick() {
        boolean calm;
        boolean punish;
        boolean low;
        BotPause.State state;
        ++this.tick;
        class_746 player = this.mc.field_1724;
        class_638 level = this.mc.field_1687;
        this.body.release(this.mc);
        if (player == null || level == null || !MeleeAttacks.canAct(this.mc) || player.method_29504()) {
            this.body.forget();
            CombatMacroLock.release(this);
            this.throwing = null;
            this.launching = false;
            return;
        }
        CombatMacroLock.release(this);
        this.keepTotem();
        this.body.stayGrounded(CrystalBotModule.blastNear(level, player));
        if (this.learn.get()) {
            this.book.save(false);
        }
        if ((state = this.pause.tick(this.mc, player)) == BotPause.State.STOP) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().danger("The server keeps pulling us back").text(", stopping."));
            this.requestDisable();
            return;
        }
        if (state != BotPause.State.RUN) {
            this.throwing = null;
            this.eating = false;
            this.launching = false;
            if (state == BotPause.State.HOLD) {
                CombatMacroLock.take(this);
            }
            return;
        }
        boolean regrouping = this.needsGapple || this.eating || this.tick - this.retreatAt < 300;
        class_1657 target = this.choose(level, player, this.body.pick(this.mc, player, this.range.get(), regrouping ? 64.0 : (double)this.range.get() + 8.0));
        if (target != this.fought) {
            this.newFight(target);
        }
        boolean bl = low = player.method_6032() <= this.eatAt() && player.method_6067() < 4.0f;
        if (player.method_6032() > this.eatAt() + 4.0f || player.method_6067() >= 4.0f) {
            this.needsGapple = false;
        }
        if (target == null) {
            this.launching = false;
            this.throwing = null;
            CombatMacroLock.release(this);
            if ((low || this.needsGapple || this.eating && player.method_6115()) && this.hands() && this.body.use(this.mc, player, GAPPLE)) {
                this.eating = true;
                return;
            }
            this.eating = false;
            if (this.refill.get() && this.kit.refill(this.mc, player, this.anchors.get())) {
                return;
            }
            if (!this.mendIdle(player)) {
                this.mending = false;
            }
            return;
        }
        RivalBook.Rival rival = this.learn.get() ? this.book.of(target.method_5667()) : null;
        this.watch(level, target, rival);
        if (CrystalBotModule.feetY(player) < class_3532.method_15357((double)(target.method_23318() + 0.01))) {
            this.body.stayGrounded(true);
        }
        this.air.note(target, this.tick, rival);
        Combo.prefer(player, (class_1309)target);
        Combo.flight(player, (class_1309)target, ahead -> this.air.lead(ahead, this.tick));
        this.runAuras(level);
        Stance was = this.stance;
        this.stance = this.stance(level, player, target, rival);
        if (this.stance == Stance.RUN && was != Stance.RUN) {
            ModuleChat.send((ClientModule)this, this.poppedOut ? ModuleChat.body().warn("3 totems popped").text(", getting away.") : ModuleChat.body().warn("Losing").text(", getting away."));
        }
        this.poppedOut = false;
        this.steer(player, target, rival);
        double distance = player.method_5739((class_1297)target);
        if (distance < this.closest - 0.5) {
            this.closest = distance;
            this.closerAt = this.tick;
        }
        if (this.throwing != null && this.keepThrowing(player, target)) {
            return;
        }
        if (this.stance == Stance.RUN && this.getAway(level, player, target)) {
            return;
        }
        if (this.getDown(level, player, target)) {
            return;
        }
        if (this.guardAnchors.get() && (this.defuse(level, player) || this.guard(level, player, target))) {
            return;
        }
        if (this.launching && this.launch(player, target)) {
            return;
        }
        if (!this.followLaid && this.tick <= this.followUntil && this.followUp(level, player, target)) {
            return;
        }
        boolean bl2 = punish = target.method_6115() && target.method_6030().method_57826(class_9334.field_50075) && CrystalBotModule.totems(player) >= 1 && player.method_6032() > 8.0f;
        if (low && !punish) {
            this.needsGapple = true;
        }
        if (this.needsGapple || this.eating && player.method_6115()) {
            if (distance >= 8.0) {
                if (BotBody.hotbarSlot(player, GAPPLE) >= 0) {
                    if (this.hands() && this.body.use(this.mc, player, GAPPLE)) {
                        this.eating = true;
                    }
                    this.body.face(this.mc, player, target, 1.0f);
                    this.body.backOff(this.mc, player);
                    return;
                }
            } else if (this.critical(player) && BotBody.hotbarSlot(player, GAPPLE) >= 0) {
                this.eating = false;
                this.retreatAt = this.tick;
                if (this.pearlAway(level, player, target)) {
                    return;
                }
                this.body.flee(this.mc, player, target);
                return;
            }
        }
        this.eating = false;
        if (this.cover(level, player, target, distance)) {
            this.body.face(this.mc, player, target, 1.0f);
            return;
        }
        if (this.stance == Stance.GUARD && this.closeSpot(level, player, target)) {
            this.body.face(this.mc, player, target, 1.0f);
            return;
        }
        if (this.breach.get() && this.breakHole(level, player, target, rival)) {
            return;
        }
        if (this.mendArmor.get() && distance >= 12.0 && CrystalBotModule.armorLow((class_1309)player) && this.hands() && this.throwXp(player)) {
            return;
        }
        this.mending = false;
        if (this.chase.get() && this.chasePearl(level, player, target, rival)) {
            return;
        }
        boolean bl3 = calm = distance >= 8.0 || BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_8301)) < 0;
        if (this.refill.get() && calm && this.kit.needed(player, this.anchors.get()) && this.hands() && this.kit.refill(this.mc, player, this.anchors.get())) {
            this.body.face(this.mc, player, target, 1.0f);
            return;
        }
        if (this.finish(player, target)) {
            return;
        }
        if (this.launches.get() && this.startLaunch(player, target, rival)) {
            return;
        }
        if (this.digDown(level, player, target, distance) || this.digBase(level, player, target, distance)) {
            this.body.face(this.mc, player, target, 1.0f);
            return;
        }
        CombatMacroLock.release(this);
        CrystalBotModule.handBack(player);
        this.body.face(this.mc, player, target, 1.0f);
        if (CrystalBotModule.surrounded(level, player)) {
            return;
        }
        if (this.stayLow(level, player, target, distance)) {
            return;
        }
        if (this.tick >= this.standScanAt) {
            this.standScanAt = this.tick + 10;
            this.standing = StandPicker.pick((class_1937)level, (class_1657)player, target, 2.4, 3.2, 4.0, this.stance.cover);
        }
        if (this.standing != null && !this.body.toward(this.mc, player, this.standing.center())) {
            return;
        }
        this.standing = null;
        this.body.keepDistance(this.mc, player, target, 2.4, 3.2, 4.0, this.dance.get());
    }

    private class_1657 choose(class_638 level, class_746 player, class_1657 kept) {
        class_1657 best = null;
        double bestWorth = Double.NEGATIVE_INFINITY;
        for (class_1657 other : CrystalBotModule.enemies(level, player, this.range.get())) {
            double worth = this.worth(player, other);
            if (!(worth > bestWorth)) continue;
            best = other;
            bestWorth = worth;
        }
        if (kept == null || !kept.method_5805() || this.fought == null) {
            this.challenger = null;
            if (best != null) {
                this.body.hold(best);
            }
            return best;
        }
        if (best == null || best == kept) {
            this.challenger = null;
            return kept;
        }
        boolean close = (double)player.method_5739((class_1297)kept) <= 6.0;
        double d = this.worth(player, kept);
        double d2 = close ? 8.0 : 2.0;
        if (bestWorth < d + d2) {
            this.challenger = null;
            return kept;
        }
        if (best != this.challenger) {
            this.challenger = best;
            this.challengerSince = this.tick;
            return kept;
        }
        if (this.tick - this.challengerSince < (close ? 20 : 6)) {
            return kept;
        }
        this.challenger = null;
        this.body.hold(best);
        return best;
    }

    private double worth(class_746 player, class_1657 other) {
        double distance = player.method_5739((class_1297)other);
        double near = distance <= 6.0 ? 1.0 : (distance >= 10.0 ? 0.0 : (10.0 - distance) / 4.0);
        double worth = 4.0 * near - 0.2 * distance + (double)(20.0f - Math.min(20.0f, CrystalDamage.health((class_1309)other))) / 5.0;
        if (!other.method_6079().method_31574(class_1802.field_8288) && !other.method_6047().method_31574(class_1802.field_8288)) {
            worth += 1.5;
        }
        if (CrystalBotModule.armorLow((class_1309)other)) {
            worth += 1.0;
        }
        if (other.method_5628() == this.attackedBy && this.tick - this.attackedAt <= 60 && distance <= 4.5) {
            worth += 4.0;
        }
        return worth;
    }

    private boolean finish(class_746 player, class_1657 target) {
        if (this.pause.slowServer() || target.field_6235 > 0 || CrystalBotModule.reach(player, target) > 2.95 || CrystalDamage.health((class_1309)target) > 3.0f) {
            return false;
        }
        if (!MeleeAttacks.isMeleeWeapon(player.method_6047()) || MeleeAttacks.charge((class_1657)player) < 0.9f) {
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        this.body.face(this.mc, player, target, 1.0f);
        this.strike(player, target);
        return true;
    }

    private void newFight(class_1657 target) {
        if (this.fought != null && this.lowSpell && this.pearledInSpell && this.learn.get()) {
            this.book.of(this.fought.method_5667()).lowSpell(true);
        }
        this.fought = target;
        this.launching = false;
        this.holeSince = -1;
        this.theirPopAt = -1;
        this.launchedAt = -1;
        this.pearledInSpell = false;
        this.lowSpell = false;
        this.followedPearl = -1;
        this.theirPearl = -1;
        this.openerArm = -1;
        this.closest = Double.MAX_VALUE;
        this.closerAt = this.tick;
        this.standing = null;
        this.followLaid = true;
        this.blocking = null;
        this.cityFails = 0;
        this.air.forget();
        this.forgetDig();
        this.progressAt = this.hurtAt = this.tick;
        float f = this.lastTheirHealth = target == null ? 0.0f : CrystalDamage.health((class_1309)target);
        if (target != null) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Fighting ").name(target.method_5477().getString()).text("."));
            if (this.learn.get()) {
                this.book.of(target.method_5667()).fought();
            }
        } else {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("No enemy in range, waiting."));
        }
    }

    private Stance stance(class_638 level, class_746 player, class_1657 target, RivalBook.Rival rival) {
        boolean run;
        int totems = CrystalBotModule.totems(player);
        float health = player.method_6032() + player.method_6067();
        float risk = CrystalBotModule.worstThreat(level, player, this.digAnchor) / Math.max(1.0f, health);
        int near = CrystalBotModule.enemiesWithin(level, player, 8.0);
        boolean bl = run = totems == 0 && near >= 2;
        if (run) {
            this.runUntil = Math.max(this.runUntil, this.tick + 40);
        }
        if (this.popStreak >= 3) {
            this.popStreak = 0;
            this.poppedOut = true;
            this.runUntil = this.tick + 200;
        }
        if (this.tick < this.runUntil) {
            return Stance.RUN;
        }
        if (this.tick - this.ownPopAt <= 40) {
            return Stance.GUARD;
        }
        if (this.finishing(target, rival)) {
            return Stance.PRESS;
        }
        boolean guard = health <= this.retreatAt() + 2.0f || totems <= 1 || risk >= 0.35f;
        return guard && health <= 8.0f ? Stance.GUARD : Stance.PRESS;
    }

    private boolean finishing(class_1657 target, RivalBook.Rival rival) {
        if (this.theirPopAt < 0 || target.method_6079().method_31574(class_1802.field_8288)) {
            return false;
        }
        int window = rival == null ? 8 : class_3532.method_15340((int)rival.retotemWindow(8), (int)3, (int)30);
        return this.tick - this.theirPopAt <= window;
    }

    private void steer(class_746 player, class_1657 target, RivalBook.Rival rival) {
        AnchorAuraModule anchor;
        CrystalAuraModule crystal;
        float min;
        float selfCap;
        float shift = 2.0f;
        float health = player.method_6032() + player.method_6067();
        float room = health - (player.method_6079().method_31574(class_1802.field_8288) ? 1.0f : 4.0f);
        this.selfCapNow = selfCap = Math.max(0.5f, Math.min(this.stance.selfCap + shift, room));
        float f = min = this.finishing(target, rival) ? 2.0f : this.stance.minDamage;
        if (CrystalBotModule.feetY(player) >= class_3532.method_15357((double)(target.method_23318() + 0.01)) && !Combo.inHole((class_1309)target)) {
            min = Math.min(min, 2.5f);
        }
        if ((crystal = ModuleRegistry.get(CrystalAuraModule.class)) != null) {
            crystal.steer(player, min, selfCap);
            crystal.crystalsFrom(player, CrystalBotModule.feetY(player) + 1);
            crystal.fullSpeed(player);
            crystal.buildEagerly(player);
        }
        if ((anchor = ModuleRegistry.get(AnchorAuraModule.class)) != null) {
            anchor.steer(player, this.finishing(target, rival) ? 2.0f : this.stance.anchorMin, Math.min(selfCap, 11.0f));
        }
    }

    private float eatAt() {
        return this.eatBelow.get() - 2.0f;
    }

    private float retreatAt() {
        return 4.0f;
    }

    private boolean critical(class_746 player) {
        return player.method_6032() + player.method_6067() <= this.retreatAt() && !player.method_6079().method_31574(class_1802.field_8288);
    }

    private static int totems(class_746 player) {
        int count = player.method_6079().method_31574(class_1802.field_8288) ? 1 : 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!inventory.method_5438(i).method_31574(class_1802.field_8288)) continue;
            count += inventory.method_5438(i).method_7947();
        }
        return count;
    }

    private static float worstThreat(class_638 level, class_746 player, class_2338 ours) {
        List<class_1657> enemies = CrystalBotModule.enemies(level, player, 16.0);
        float worst = 0.0f;
        for (class_1511 crystal : level.method_18467(class_1511.class, player.method_5829().method_1014(12.0))) {
            class_243 center = crystal.method_73189();
            float us = CrystalDamage.estimate(center, (class_1309)player);
            if (!(us > worst) || !CrystalBotModule.worthIt(enemies, crystal.method_5829(), center, us, 4.0, 6.0f, null)) continue;
            worst = us;
        }
        class_2338 feet = player.method_24515();
        for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-5, -2, -5), (class_2338)feet.method_10069(5, 3, 5))) {
            class_2338 anchor;
            class_243 center;
            float us;
            class_2680 state = level.method_8320(pos);
            if (!state.method_27852(class_2246.field_23152) || (Integer)state.method_11654((class_2769)class_4969.field_23153) <= 0 || pos.equals((Object)ours) || !((us = CrystalDamage.estimate(center = class_243.method_24953((class_2382)(anchor = pos.method_10062())), (class_1309)player, 5.0f, anchor)) > worst) || !CrystalBotModule.worthIt(enemies, new class_238(anchor), center, us, 5.5, 5.0f, anchor)) continue;
            worst = us;
        }
        return worst;
    }

    private static boolean worthIt(List<class_1657> enemies, class_238 source, class_243 center, float us, double reach, float power, class_2338 ignored) {
        for (class_1657 enemy : enemies) {
            class_243 eye = enemy.method_33571();
            if (eye.method_1025(TargetingUtils.closestPoint(eye, source)) > reach * reach || !(CrystalDamage.estimate(center, (class_1309)enemy, power, ignored) < us)) continue;
            return true;
        }
        return false;
    }

    private static int enemiesWithin(class_638 level, class_746 player, double reach) {
        int count = 0;
        for (class_1657 other : level.method_18456()) {
            if (other == player || !other.method_5805() || !MeleeAttacks.isEnemy(player, (class_1297)other) || !((double)other.method_5739((class_1297)player) <= reach)) continue;
            ++count;
        }
        return count;
    }

    private static List<class_1657> enemies(class_638 level, class_746 player, double reach) {
        ArrayList<class_1657> enemies = new ArrayList<class_1657>();
        for (class_1657 other : level.method_18456()) {
            if (other == player || !other.method_5805() || !MeleeAttacks.isEnemy(player, (class_1297)other) || !((double)other.method_5739((class_1297)player) <= reach)) continue;
            enemies.add(other);
        }
        return enemies;
    }

    private void runAuras(class_638 level) {
        boolean wantAnchors;
        CrystalAuraModule crystal = ModuleRegistry.get(CrystalAuraModule.class);
        if (crystal != null && !crystal.isEnabled()) {
            if (this.crystalWasOn == null) {
                this.crystalWasOn = false;
            }
            crystal.setEnabled(true);
        }
        AnchorAuraModule anchor = ModuleRegistry.get(AnchorAuraModule.class);
        boolean bl = wantAnchors = this.anchors.get() && level.method_27983() != class_1937.field_25180;
        if (anchor != null && wantAnchors && !anchor.isEnabled()) {
            if (this.anchorWasOn == null) {
                this.anchorWasOn = false;
            }
            anchor.setEnabled(true);
        }
    }

    private void keepTotem() {
        AutoInvTotemModule inventory = ModuleRegistry.get(AutoInvTotemModule.class);
        AutoTotemModule plain = ModuleRegistry.get(AutoTotemModule.class);
        if (inventory == null || inventory.isEnabled() || plain != null && plain.isEnabled()) {
            return;
        }
        if (this.totemWasOn == null) {
            this.totemWasOn = false;
        }
        inventory.setEnabled(true);
    }

    private void restoreAuras() {
        AutoInvTotemModule totem;
        AnchorAuraModule anchor;
        CrystalAuraModule crystal = ModuleRegistry.get(CrystalAuraModule.class);
        if (crystal != null && Boolean.FALSE.equals(this.crystalWasOn)) {
            crystal.setEnabled(false);
        }
        if ((anchor = ModuleRegistry.get(AnchorAuraModule.class)) != null && Boolean.FALSE.equals(this.anchorWasOn)) {
            anchor.setEnabled(false);
        }
        if ((totem = ModuleRegistry.get(AutoInvTotemModule.class)) != null && Boolean.FALSE.equals(this.totemWasOn)) {
            totem.setEnabled(false);
        }
        this.totemWasOn = null;
        this.anchorWasOn = null;
        this.crystalWasOn = null;
    }

    private static boolean blastNear(class_638 level, class_746 player) {
        for (class_1511 crystal : level.method_18467(class_1511.class, player.method_5829().method_1014(7.0))) {
            if (!(CrystalDamage.estimate(crystal.method_73189(), (class_1309)player) >= 1.0f)) continue;
            return true;
        }
        class_2338 feet = player.method_24515();
        int reach = (int)Math.ceil(7.0);
        for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-reach, -2, -reach), (class_2338)feet.method_10069(reach, 3, reach))) {
            class_2680 state = level.method_8320(pos);
            if (!state.method_27852(class_2246.field_23152) || (Integer)state.method_11654((class_2769)class_4969.field_23153) <= 0 || !(class_243.method_24953((class_2382)pos).method_1022(player.method_73189()) <= 7.0)) continue;
            return true;
        }
        return false;
    }

    private static void handBack(class_746 player) {
        class_1799 main = player.method_6047();
        if (main.method_31574(class_1802.field_8301) || main.method_31574(class_1802.field_8281) || main.method_31574(class_1802.field_23141) || main.method_31574(class_1802.field_8801) || Crystals.isCombatItem(main)) {
            return;
        }
        int slot = BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_8301));
        if (slot < 0) {
            slot = BotBody.hotbarSlot(player, MeleeAttacks::isMeleeWeapon);
        }
        if (slot >= 0) {
            player.method_31548().method_61496(slot);
        }
    }

    private boolean hands() {
        CombatMacroLock.take(this);
        return !MeleeAttacks.actedThisTick((class_1657)this.mc.field_1724);
    }

    private void watch(class_638 level, class_1657 target, RivalBook.Rival rival) {
        float theirs = CrystalDamage.health((class_1309)target);
        if (theirs < this.lastTheirHealth - 0.5f) {
            this.hurtAt = this.tick;
        }
        if (theirs < this.lastTheirHealth - 0.5f || !Combo.inHole((class_1309)target)) {
            this.progressAt = this.tick;
        }
        this.lastTheirHealth = theirs;
        if (this.openerArm >= 0 && this.tick - this.openerAt >= 40) {
            float taken = this.openerHealth - theirs + 10.0f * (float)this.openerPops;
            if (rival != null) {
                rival.opened(this.openerArm, this.openerReach, class_3532.method_15363((float)(taken / 12.0f), (float)0.0f, (float)1.5f));
            }
            this.openerArm = -1;
        }
        if (this.launchedAt >= 0) {
            this.launchPeak = Math.max(this.launchPeak, target.method_23318() - this.launchY);
            if (this.tick - this.launchedAt >= 8) {
                if (rival != null) {
                    rival.launched(this.launchPeak >= 0.3);
                }
                this.launchedAt = -1;
            }
        }
        if (this.theirPopAt >= 0 && rival != null) {
            if (target.method_6079().method_31574(class_1802.field_8288)) {
                rival.retotemed(this.tick - this.theirPopAt);
                this.theirPopAt = -1;
            } else if (this.tick - this.theirPopAt > 100) {
                rival.retotemed(100);
                this.theirPopAt = -1;
            }
        } else if (this.theirPopAt >= 0 && this.tick - this.theirPopAt > 100) {
            this.theirPopAt = -1;
        }
        if (Combo.inHole((class_1309)target)) {
            if (this.holeSince < 0) {
                this.holeSince = this.tick;
            }
        } else if (this.holeSince >= 0) {
            if (rival != null) {
                rival.leftHole(Math.min(600, this.tick - this.holeSince));
            }
            this.holeSince = -1;
        }
        if (!this.lowSpell && theirs <= 8.0f) {
            this.lowSpell = true;
            this.pearledInSpell = false;
        } else if (this.lowSpell && theirs > 12.0f) {
            if (rival != null) {
                rival.lowSpell(this.pearledInSpell);
            }
            this.lowSpell = false;
        }
        for (class_1684 pearl : level.method_18467(class_1684.class, target.method_5829().method_1014(4.0))) {
            if (pearl.method_24921() != target || pearl.field_6012 > 2 || pearl.method_5628() == this.followedPearl) continue;
            if (this.lowSpell) {
                this.pearledInSpell = true;
            }
            this.theirPearl = pearl.method_5628();
        }
    }

    private boolean getAway(class_638 level, class_746 player, class_1657 target) {
        if ((double)player.method_5739((class_1297)target) < 12.0 && this.pearlAway(level, player, target)) {
            return true;
        }
        if ((double)player.method_5739((class_1297)target) >= 16.0 && player.method_6032() <= this.eatAt() + 4.0f && this.hands() && this.body.use(this.mc, player, GAPPLE)) {
            this.eating = true;
            return true;
        }
        this.body.flee(this.mc, player, target);
        return true;
    }

    private boolean pearlAway(class_638 level, class_746 player, class_1657 target) {
        if (!this.escape.get() || !this.pearlReady(player) || this.tick < this.searchAfter) {
            return false;
        }
        List<class_1657> enemies = CrystalBotModule.enemies(level, player, 48.0);
        Rotation look = PearlFlight.escape(player, enemies, 20.0);
        this.throwAway = 16.0;
        if (look == null) {
            look = PearlFlight.escape(player, enemies, 12.0);
            this.throwAway = 12.0;
        }
        if (look == null) {
            this.searchAfter = this.tick + 10;
            return false;
        }
        this.startThrow(Throw.ESCAPE, look, null);
        return this.keepThrowing(player, target);
    }

    private boolean pearlReady(class_746 player) {
        return this.tick - this.pearlAt >= 30 && BotBody.hotbarSlot(player, PEARL) >= 0 && !player.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8634)) && (player.method_6032() + player.method_6067() > 6.0f || player.method_6079().method_31574(class_1802.field_8288));
    }

    private void startThrow(Throw kind, Rotation look, class_243 spot) {
        this.throwing = kind;
        this.throwLook = look;
        this.throwSpot = spot;
        this.throwUntil = this.tick + 14;
    }

    private boolean keepThrowing(class_746 player, class_1657 target) {
        boolean good;
        if (this.tick > this.throwUntil || !this.pearlReady(player)) {
            this.throwing = null;
            this.pearlAt = this.tick - 15;
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        Aim.turn(this.mc, this.throwLook.yaw(), this.throwLook.pitch(), 27.0f);
        int slot = BotBody.hotbarSlot(player, PEARL);
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
            return true;
        }
        if (ServerRotation.desynced() || player.method_6115() || !MeleeAttacks.handsFree(this.mc, player)) {
            return true;
        }
        if (!player.method_24828() || player.method_60478().method_37268() > 1.0E-4) {
            return true;
        }
        float yaw = player.method_36454();
        float pitch = player.method_36455();
        Rotation sent = ServerRotation.commit(player);
        if (Math.abs(class_3532.method_15393((float)(sent.yaw() - yaw))) > 0.01f || Math.abs(sent.pitch() - pitch) > 0.01f) {
            return true;
        }
        boolean bl = good = this.throwing == Throw.ESCAPE ? PearlFlight.escapes(player, yaw, pitch, CrystalBotModule.enemies(this.mc.field_1687, player, 48.0), this.throwAway) : PearlFlight.reaches(player, yaw, pitch, this.throwSpot, 0.0, 3.0);
        if (!good) {
            return true;
        }
        if (this.mc.field_1761.method_2919((class_1657)player, class_1268.field_5808).method_23665()) {
            player.method_6104(class_1268.field_5808);
            MeleeAttacks.claimTick(this.mc);
            this.pearlAt = this.tick;
            if (this.throwing == Throw.ESCAPE) {
                ModuleChat.send((ClientModule)this, ModuleChat.body().warn("Pearled away").text("."));
            } else {
                ModuleChat.send((ClientModule)this, ModuleChat.body().text("Pearled after ").name(target.method_5477().getString()).text("."));
            }
        }
        this.throwing = null;
        return true;
    }

    private boolean chasePearl(class_638 level, class_746 player, class_1657 target, RivalBook.Rival rival) {
        if (this.stance != Stance.PRESS && this.stance != Stance.HOLD || !this.pearlReady(player)) {
            return false;
        }
        float health = player.method_6032() + player.method_6067();
        if (health < 6.0f) {
            return false;
        }
        int pearls = CrystalBotModule.count(player, PEARL);
        boolean following = this.theirPearl >= 0;
        boolean keepOne = CrystalBotModule.totems(player) == 0 && player.method_6032() <= 12.0f || !following && rival != null && rival.pearlsWhenLow() && CrystalDamage.health((class_1309)target) <= 8.0f;
        if (pearls <= (keepOne ? 1 : 0)) {
            return false;
        }
        class_243 spot = null;
        if (following) {
            class_1684 pearl;
            PearlFlight.Landing landing;
            this.followedPearl = this.theirPearl;
            this.theirPearl = -1;
            class_1297 class_12972 = level.method_8469(this.followedPearl);
            if (class_12972 instanceof class_1684 && (landing = PearlFlight.fly((class_1937)level, (class_1297)(pearl = (class_1684)class_12972), pearl.method_73189(), pearl.method_18798())) != null && landing.at().method_1022(player.method_73189()) > 8.0) {
                spot = landing.at();
            }
        }
        double distance = player.method_5739((class_1297)target);
        if (spot == null && distance <= (double)this.range.get() + 8.0) {
            AirTrack.Landing down;
            boolean stuck;
            class_243 away = target.method_73189().method_1020(player.method_73189()).method_18805(1.0, 0.0, 1.0).method_1029();
            class_243 motion = target.method_73189().method_1023(target.field_6014, target.field_6036, target.field_5969);
            boolean running = distance >= 12.0 && motion.field_1352 * away.field_1352 + motion.field_1350 * away.field_1350 > 0.12;
            class_2338 under = target.method_24515().method_10074();
            boolean standing = !level.method_8320(under).method_26220((class_1922)level, under).method_1110();
            boolean above = distance >= 3.0 && target.method_23318() - player.method_23318() >= 3.0 && standing;
            boolean below = distance >= 4.0 && player.method_23318() - target.method_23318() >= 4.0 && standing;
            boolean bl = stuck = distance > 6.0 && this.tick - this.closerAt > 60;
            if (running || above || below || stuck) {
                spot = target.method_73189().method_1019(motion.method_18805(1.0, 0.0, 1.0).method_1021((double)PearlFlight.flightTicks(distance)));
            }
            if (spot == null && !target.method_24828() && distance >= 6.0 && (down = this.air.landing(this.tick)) != null && target.method_23318() - down.at().field_1351 >= 3.0) {
                spot = down.at();
            }
        }
        if (spot == null || this.tick < this.searchAfter) {
            return false;
        }
        Rotation look = PearlFlight.toward(player, spot, 0.8, 3.0);
        if (look == null) {
            this.searchAfter = this.tick + 10;
            return false;
        }
        this.startThrow(Throw.CHASE, look, spot);
        return this.keepThrowing(player, target);
    }

    private boolean getDown(class_638 level, class_746 player, class_1657 target) {
        boolean blast;
        double drop = player.method_23318() - target.method_23318();
        double sideways = Math.hypot(target.method_23317() - player.method_23317(), target.method_23321() - player.method_23321());
        if (drop < 3.0 || sideways > 8.0 || this.stance == Stance.RUN || this.critical(player)) {
            this.forgetDig();
            return false;
        }
        if (this.belowSince < 0) {
            this.belowSince = this.tick;
        }
        if (this.tick - this.belowSince < 20 || !player.method_24828() || !target.method_24828()) {
            return false;
        }
        if (this.digAnchor != null && !level.method_8320(this.digAnchor).method_27852(class_2246.field_23152)) {
            if (this.digPlaced) {
                ++this.digBlasts;
                this.digWalkUntil = this.tick + 40;
            }
            this.digAnchor = null;
        }
        if (this.tick < this.digWalkUntil || CrystalBotModule.openDrop(level, player, target)) {
            this.body.face(this.mc, player, target, 1.0f);
            this.body.dropToward(this.mc, player, target.method_73189());
            return true;
        }
        boolean bl = blast = drop <= 6.0 && this.digBlasts < 2 && this.tick >= this.digAfter && this.anchors.get() && level.method_27983() != class_1937.field_25180 && BotBody.hotbarSlot(player, ANCHOR) >= 0 && BotBody.hotbarSlot(player, GLOWSTONE) >= 0;
        if (blast && this.blastDown(level, player, target)) {
            return true;
        }
        return this.pearlDown(player, target);
    }

    private boolean blastDown(class_638 level, class_746 player, class_1657 target) {
        AnchorGuard.Spot shield;
        if (this.digAnchor == null) {
            this.digAnchor = CrystalBotModule.digCell(level, player, target);
            if (this.digAnchor == null) {
                this.digAfter = this.tick + 100;
                return false;
            }
            this.digPlaced = false;
            this.digGuards = 0;
            this.digUntil = this.tick + 80;
            if (!this.digAnnounced) {
                ModuleChat.send((ClientModule)this, ModuleChat.body().text("Blasting down to ").name(target.method_5477().getString()).text("."));
            }
            this.digAnnounced = true;
        }
        if (this.tick > this.digUntil) {
            this.digBlasts = 2;
            this.digAnchor = null;
            return false;
        }
        this.body.face(this.mc, player, target, 1.0f);
        class_2680 state = level.method_8320(this.digAnchor);
        if (!state.method_27852(class_2246.field_23152)) {
            AnchorGuard.Spot spot;
            AnchorGuard.Spot spot2 = spot = state.method_45474() ? AnchorGuard.supportFor(level, player, player.method_33571(), this.digAnchor) : null;
            if (spot == null) {
                this.digAnchor = null;
                this.digAfter = this.tick + 100;
                return false;
            }
            if (this.placeAt(level, player, spot, BotBody.hotbarSlot(player, ANCHOR)) == 1) {
                this.digPlaced = true;
            }
            return true;
        }
        if ((Integer)state.method_11654((class_2769)class_4969.field_23153) <= 0) {
            return this.clickDigAnchor(level, player, BotBody.hotbarSlot(player, GLOWSTONE));
        }
        class_243 aim = AnchorGuard.anchorAim(level, player, this.digAnchor);
        if (aim == null) {
            this.digAnchor = null;
            return false;
        }
        int shieldSlot = CrystalBotModule.guardBlockSlot(player);
        if (this.digGuards < 2 && shieldSlot >= 0 && (shield = AnchorGuard.find(level, player, this.digAnchor, aim)) != null && !shield.standing()) {
            if (this.placeAt(level, player, shield, shieldSlot) == 1) {
                ++this.digGuards;
            }
            return true;
        }
        float own = CrystalDamage.estimate(class_243.method_24953((class_2382)this.digAnchor), (class_1309)player, 5.0f, this.digAnchor);
        int route = AnchorGuard.detonatorRoute(player.method_31548(), player.method_6047());
        if (own > Math.min(this.selfCapNow, 10.0f) || CrystalDamage.endangers((class_1657)player, own) || route == -1) {
            this.digBlasts = 2;
            this.digAnchor = null;
            return false;
        }
        return this.clickDigAnchor(level, player, route == -2 ? player.method_31548().method_67532() : route);
    }

    private static class_2338 digCell(class_638 level, class_746 player, class_1657 target) {
        class_243 eye = player.method_33571();
        double reach = player.method_55754() - 0.5;
        class_2338 feet = player.method_24515();
        ArrayList<class_2338> cells = new ArrayList<class_2338>();
        for (class_2338 cell2 : class_2338.method_10097((class_2338)feet.method_10069(-4, -2, -4), (class_2338)feet.method_10069(4, 0, 4))) {
            class_238 box;
            if (eye.method_1022(class_243.method_24953((class_2382)cell2)) > reach) continue;
            class_2338 under = cell2.method_10074();
            if (!level.method_8320(cell2).method_45474() || !level.method_8316(cell2).method_15769() || !level.method_8320(under).method_26206((class_1922)level, under, class_2350.field_11036) || (box = new class_238(cell2)).method_994(player.method_5829()) || box.method_994(target.method_5829())) continue;
            cells.add(cell2.method_10062());
        }
        cells.sort(Comparator.comparingDouble(cell -> class_243.method_24953((class_2382)cell).method_1025(target.method_73189())));
        class_2338 fallback = null;
        for (class_2338 cell3 : cells.subList(0, Math.min(cells.size(), 8))) {
            class_243 sight;
            if (AnchorGuard.supportFor(level, player, eye, cell3) == null) continue;
            if (fallback == null) {
                fallback = cell3;
            }
            if (AnchorGuard.find(level, player, cell3, sight = TargetingUtils.closestPoint(eye, new class_238(cell3).method_1011(0.1))) == null) continue;
            return cell3;
        }
        return fallback;
    }

    private static boolean openDrop(class_638 level, class_746 player, class_1657 target) {
        class_243 way = target.method_73189().method_1020(player.method_73189()).method_18805(1.0, 0.0, 1.0);
        if (way.method_1027() < 0.25) {
            return false;
        }
        class_243 step = player.method_73189().method_1019(way.method_1029().method_1021(0.8));
        class_2338 column = class_2338.method_49637((double)step.field_1352, (double)(player.method_23318() + 0.1), (double)step.field_1350);
        int floor = class_3532.method_15357((double)(target.method_23318() + 0.01));
        class_2338 pos = column;
        while (pos.method_10264() >= floor - 1) {
            class_2680 state = level.method_8320(pos);
            if (state.method_27852(class_2246.field_10164) || state.method_26164(class_3481.field_21952)) {
                return false;
            }
            if (!state.method_26220((class_1922)level, pos).method_1110()) {
                return (double)pos.method_10264() < player.method_23318() - 1.5 && pos.method_10264() <= floor + 1;
            }
            pos = pos.method_10074();
        }
        return false;
    }

    private boolean clickDigAnchor(class_638 level, class_746 player, int slot) {
        if (slot < 0) {
            this.digAnchor = null;
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        class_3965 hit = AnchorGuard.anchorHit(level, player, this.digAnchor);
        if (hit == null) {
            class_243 aim = AnchorGuard.anchorAim(level, player, this.digAnchor);
            if (aim == null) {
                this.digAnchor = null;
                return false;
            }
            ServerRotation.request(RotationUtils.fromTo(player.method_33571(), aim), 6.0f);
            return true;
        }
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
            return true;
        }
        if (MeleeAttacks.handsFree(this.mc, player)) {
            AnchorGuard.click(this.mc, player, hit);
        }
        return true;
    }

    private boolean pearlDown(class_746 player, class_1657 target) {
        if (!this.chase.get() || !this.pearlReady(player) || this.tick < this.searchAfter) {
            return false;
        }
        class_243 spot = target.method_73189();
        Rotation look = PearlFlight.toward(player, spot, 0.8, 3.0);
        if (look == null) {
            this.searchAfter = this.tick + 10;
            return false;
        }
        this.startThrow(Throw.CHASE, look, spot);
        return this.keepThrowing(player, target);
    }

    private static int feetY(class_746 player) {
        return class_3532.method_15357((double)(player.method_23318() + 0.01));
    }

    private boolean wantsLower(class_746 player, class_1657 target, double distance) {
        return this.stance != Stance.RUN && player.method_24828() && target.method_24828() && !Combo.inHole((class_1309)target) && distance <= 5.5 && CrystalBotModule.feetY(player) == class_3532.method_15357((double)(target.method_23318() + 0.01)) && !this.justLaunched();
    }

    private boolean justLaunched() {
        return this.launching || this.launchedAt >= 0 || this.tick <= this.followUntil + 20;
    }

    private boolean stayLow(class_638 level, class_746 player, class_1657 target, double distance) {
        if (this.stance == Stance.RUN || !player.method_24828() || !target.method_24828() || Combo.inHole((class_1309)target) || distance > 5.5) {
            return false;
        }
        if (CrystalBotModule.feetY(player) < class_3532.method_15357((double)(target.method_23318() + 0.01))) {
            return true;
        }
        if (!this.wantsLower(player, target, distance)) {
            return false;
        }
        class_2338 step = CrystalBotModule.lowerStep(level, player, target, CrystalBotModule.feetY(player) - 1);
        if (step == null) {
            return false;
        }
        this.body.toward(this.mc, player, class_243.method_24955((class_2382)step));
        return true;
    }

    private boolean digDown(class_638 level, class_746 player, class_1657 target, double distance) {
        boolean diggable;
        if (!this.wantsLower(player, target, distance) || this.tick < this.stepDigAfter) {
            return false;
        }
        if (CrystalBotModule.lowerStep(level, player, target, CrystalBotModule.feetY(player) - 1) != null) {
            return false;
        }
        class_2338 under = player.method_24515().method_10074();
        class_2680 state = level.method_8320(under);
        if (BotBody.hotbarSlot(player, OBSIDIAN) < 0 && !Crystals.isBase(state)) {
            return false;
        }
        class_2338 landing = under.method_10074();
        boolean bl = diggable = !state.method_31709() && CrystalBotModule.digTicks(level, player, under) <= 50 && !level.method_8320(landing).method_26220((class_1922)level, landing).method_1110() && !CrystalBotModule.nearLava(level, under);
        if (!diggable || !this.mine(level, player, under)) {
            this.stepDigAfter = this.tick + 100;
            return false;
        }
        return true;
    }

    private boolean digBase(class_638 level, class_746 player, class_1657 target, double distance) {
        int theirs = class_3532.method_15357((double)(target.method_23318() + 0.01));
        if (this.stance == Stance.RUN || !player.method_24828() || !target.method_24828() || Combo.inHole((class_1309)target) || distance > 5.5 || CrystalBotModule.feetY(player) != theirs - 1 || this.tick < this.baseDigAfter || BotBody.hotbarSlot(player, OBSIDIAN) < 0 || this.justLaunched()) {
            return false;
        }
        class_243 eye = player.method_33571();
        class_2338 theirFeet = target.method_24515();
        class_2338 best = null;
        double nearest = Double.MAX_VALUE;
        for (class_2338 pos : class_2338.method_10097((class_2338)theirFeet.method_10069(-2, -1, -2), (class_2338)theirFeet.method_10069(2, -1, 2))) {
            class_243 top;
            double reach;
            class_2680 state = level.method_8320(pos);
            if (Crystals.isBase(state) && Crystals.canHost((class_1937)level, pos, null)) {
                return false;
            }
            if (pos.method_10263() == theirFeet.method_10263() && pos.method_10260() == theirFeet.method_10260() || Math.max(Math.abs(pos.method_10263() - player.method_31477()), Math.abs(pos.method_10260() - player.method_31479())) < 2 || state.method_45474() || state.method_31709() || !level.method_22347(pos.method_10084()) || (reach = (top = class_243.method_24953((class_2382)pos).method_1031(0.0, 0.5, 0.0)).method_1022(target.method_73189())) > 2.5 || eye.method_1022(top) > player.method_55754() - 0.5 || CrystalBotModule.digTicks(level, player, pos) > 25 || reach >= nearest) continue;
            best = pos.method_10062();
            nearest = reach;
        }
        if (best == null || !this.mine(level, player, best)) {
            this.baseDigAfter = this.tick + 100;
            return false;
        }
        return true;
    }

    private static class_2338 lowerStep(class_638 level, class_746 player, class_1657 target, int feet) {
        class_2338 here = player.method_24515();
        class_2338 best = null;
        double nearest = Double.MAX_VALUE;
        for (class_2338 cell : class_2338.method_10097((class_2338)here.method_10069(-2, -1, -2), (class_2338)here.method_10069(2, -1, 2))) {
            double distance;
            class_2338 spot = StandPicker.standable((class_1937)level, cell);
            if (spot == null || spot.method_10264() != feet || !spot.equals((Object)cell)) continue;
            class_243 at = class_243.method_24955((class_2382)spot);
            double reach = Math.hypot(at.field_1352 - target.method_23317(), at.field_1350 - target.method_23321());
            if (reach < 2.4 || reach > 4.5 || !CrystalBotModule.walkable(level, player.method_73189(), at) || !((distance = spot.method_10262((class_2382)here)) < nearest)) continue;
            best = spot.method_10062();
            nearest = distance;
        }
        return best;
    }

    private static int digTicks(class_638 level, class_746 player, class_2338 pos) {
        class_2378 enchantments;
        int efficiency;
        class_2680 state = level.method_8320(pos);
        float hardness = state.method_26214((class_1922)level, pos);
        if (hardness < 0.0f) {
            return Integer.MAX_VALUE;
        }
        if (hardness == 0.0f) {
            return 1;
        }
        int slot = CrystalBotModule.toolSlot(player.method_31548(), state);
        class_1799 tool = slot < 0 ? class_1799.field_8037 : player.method_31548().method_5438(slot);
        float speed = tool.method_7924(state);
        if (speed > 1.0f && (efficiency = (enchantments = player.method_56673().method_30530(class_7924.field_41265)).method_46746(class_1893.field_9131).map(holder -> class_1890.method_8225((class_6880)holder, (class_1799)tool)).orElse(0).intValue()) > 0) {
            speed += (float)(efficiency * efficiency + 1);
        }
        boolean right = tool.method_7951(state) || !state.method_29291();
        return class_3532.method_15386((float)(hardness * (right ? 30.0f : 100.0f) / speed));
    }

    private static boolean nearLava(class_638 level, class_2338 pos) {
        for (class_2350 side : class_2350.values()) {
            if (!level.method_8316(pos.method_10093(side)).method_15767(class_3486.field_15518)) continue;
            return true;
        }
        return false;
    }

    private void forgetDig() {
        this.digAnchor = null;
        this.digAnnounced = false;
        this.digPlaced = false;
        this.digBlasts = 0;
        this.digGuards = 0;
        this.digAfter = 0;
        this.digWalkUntil = 0;
        this.digUntil = 0;
        this.belowSince = -1;
    }

    private boolean startLaunch(class_746 player, class_1657 target, RivalBook.Rival rival) {
        boolean armed;
        if (player.field_6012 - this.diggingAt <= 1) {
            return false;
        }
        if (this.tick < this.nextLaunch || this.pause.slowServer() || this.stance != Stance.PRESS && this.stance != Stance.HOLD || rival != null && rival.shrugsOffLaunches()) {
            return false;
        }
        if (!target.method_24828() || Combo.inHole((class_1309)target) || target.method_6039() && !CrystalBotModule.axeInHotbar(player)) {
            return false;
        }
        if (!this.mc.field_1687.method_18467(class_1511.class, target.method_5829().method_1014(3.0)).isEmpty()) {
            return false;
        }
        double reach = CrystalBotModule.reach(player, target);
        if (CrystalBotModule.weaponSlot(player, target.method_6039()) < 0 || reach > 3.9) {
            return false;
        }
        boolean bl = armed = MeleeAttacks.isMeleeWeapon(player.method_6047()) && MeleeAttacks.charge((class_1657)player) >= 0.5f;
        if (!armed) {
            int arm;
            int n = rival != null ? rival.opener(reach) : (arm = this.tick - this.hurtAt >= 20 ? 0 : 1);
            if (this.openerArm < 0) {
                this.judgeOpener(arm, reach, target);
            }
            if (arm == 1) {
                this.nextLaunch = this.tick + 40;
                return false;
            }
        }
        this.launching = true;
        this.launchUntil = this.tick + 16;
        return this.launch(player, target);
    }

    private void judgeOpener(int arm, double reach, class_1657 target) {
        this.openerArm = arm;
        this.openerReach = reach;
        this.openerAt = this.tick;
        this.openerHealth = CrystalDamage.health((class_1309)target);
        this.openerPops = 0;
    }

    private boolean followUp(class_638 level, class_746 player, class_1657 target) {
        class_243 landing;
        AirTrack.Landing down = this.air.landing(this.tick);
        class_243 class_2432 = landing = down != null ? down.at() : target.method_73189();
        if (this.anchorUnder(level, player, target, landing)) {
            return true;
        }
        int slot = BotBody.hotbarSlot(player, OBSIDIAN);
        if (slot < 0) {
            this.followLaid = true;
            return false;
        }
        class_2338 feet = class_2338.method_49637((double)landing.field_1352, (double)(landing.field_1351 + 0.2), (double)landing.field_1350);
        CrystalDamage.Victim them = new CrystalDamage.Victim((class_1309)target, landing.method_1020(target.method_73189()));
        CrystalDamage.Victim us = new CrystalDamage.Victim((class_1309)player, class_243.field_1353);
        class_243 eye = player.method_33571();
        AnchorGuard.Spot best = null;
        float most = 4.0f;
        for (class_2338 base : class_2338.method_10097((class_2338)feet.method_10069(-1, -1, -1), (class_2338)feet.method_10069(1, 0, 1))) {
            AnchorGuard.Spot spot;
            class_2338 below;
            if (base.method_10263() == feet.method_10263() && base.method_10260() == feet.method_10260()) continue;
            class_2680 state = level.method_8320(base);
            class_243 center = Crystals.explosionAt(base);
            if (Crystals.isBase(state) && Crystals.canHost((class_1937)level, base, null)) {
                if (!(them.damage(center, false) >= most)) continue;
                this.followLaid = true;
                return false;
            }
            if (!state.method_45474() || !level.method_22347(base.method_10084()) || !level.method_22347(base.method_10086(2)) || !level.method_8320(below = base.method_10074()).method_26206((class_1922)level, below, class_2350.field_11036) || !level.method_8628(class_2246.field_10540.method_9564(), base, class_3726.method_16194()) || eye.method_1022(class_243.method_24953((class_2382)base)) > player.method_55754()) continue;
            float hurt = them.damage(center, true);
            float own = us.damage(center, true);
            if (hurt <= most || own > this.selfCapNow || CrystalDamage.endangers((class_1657)player, own) || (spot = AnchorGuard.supportFor(level, player, eye, base.method_10062())) == null) continue;
            best = spot;
            most = hurt;
        }
        this.body.face(this.mc, player, target, 1.0f);
        if (best == null) {
            if (eye.method_1022(landing) > 3.5) {
                this.body.drive(this.mc, player, 1, 0.0, true, false);
            }
            return true;
        }
        if (this.placeAt(level, player, best, slot) == 1) {
            this.followLaid = true;
        }
        return true;
    }

    private boolean anchorUnder(class_638 level, class_746 player, class_1657 target, class_243 landing) {
        if (!this.anchors.get() || level.method_27983() == class_1937.field_25180 || target.method_24828()) {
            return false;
        }
        int slot = BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_23141));
        if (slot < 0 || BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_8801)) < 0) {
            return false;
        }
        class_2338 cell = class_2338.method_49637((double)landing.field_1352, (double)(landing.field_1351 + 0.2), (double)landing.field_1350);
        class_2338 below = cell.method_10074();
        if (!level.method_8320(cell).method_45474() || !level.method_8320(below).method_26206((class_1922)level, below, class_2350.field_11036)) {
            return false;
        }
        if (target.method_5829().method_994(new class_238(cell)) || player.method_5829().method_994(new class_238(cell))) {
            return false;
        }
        class_243 eye = player.method_33571();
        if (eye.method_1022(class_243.method_24953((class_2382)cell)) > player.method_55754()) {
            return false;
        }
        class_243 blast = class_243.method_24953((class_2382)cell);
        float own = CrystalDamage.estimate(blast, (class_1309)player, 5.0f, cell);
        class_243 onTop = new class_243(landing.field_1352, (double)cell.method_10264() + 1.0, landing.field_1350);
        float theirs = new CrystalDamage.Victim((class_1309)target, onTop.method_1020(target.method_73189())).damage(blast, 5.0f, cell);
        if (theirs < 6.0f || own > Math.min(this.selfCapNow, 11.0f) || CrystalDamage.endangers((class_1657)player, own)) {
            return false;
        }
        AnchorGuard.Spot spot = AnchorGuard.supportFor(level, player, eye, cell);
        if (spot == null) {
            return false;
        }
        this.body.face(this.mc, player, target, 1.0f);
        if (this.placeAt(level, player, spot, slot) == 1) {
            this.followLaid = true;
        }
        return true;
    }

    private boolean launch(class_746 player, class_1657 target) {
        if (this.tick > this.launchUntil || this.stance == Stance.RUN || this.stance == Stance.GUARD || CrystalBotModule.reach(player, target) > 4.1 || Combo.inHole((class_1309)target)) {
            this.launching = false;
            this.nextLaunch = this.tick + 40;
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        int slot = CrystalBotModule.weaponSlot(player, target.method_6039());
        if (slot < 0) {
            this.launching = false;
            return false;
        }
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
        }
        this.body.face(this.mc, player, target, 1.0f);
        float charge = MeleeAttacks.charge((class_1657)player);
        double reach = CrystalBotModule.reach(player, target);
        Rotation sent = ServerRotation.sent();
        boolean sprinting = player.method_5624();
        if (charge >= 0.95f && reach <= 2.95 && this.strike(player, target)) {
            this.launching = false;
            this.nextLaunch = this.tick + 80;
            this.launchedAt = this.tick;
            this.launchY = target.method_23318();
            this.launchPeak = 0.0;
            this.followLaid = false;
            this.followUntil = this.tick + 12;
            this.air.kicked(player, target, sent != null ? sent.yaw() : player.method_36454(), sprinting, this.tick);
            this.body.drive(this.mc, player, 0, 1.0, false, false);
            return true;
        }
        this.body.drive(this.mc, player, reach > 2.9 ? 1 : 0, 0.0, charge >= 0.85f, false);
        return true;
    }

    private boolean strike(class_746 player, class_1657 target) {
        class_243 at;
        if (this.pause.slowServer() || MeleeAttacks.actedThisTick((class_1657)player) || this.mc.field_1771 > 0 || !MeleeAttacks.isMeleeWeapon(player.method_6047())) {
            return false;
        }
        Rotation sent = ServerRotation.sent();
        if (sent == null) {
            return false;
        }
        class_243 eye = player.method_33571();
        double reachLimit = Math.min(2.95, player.method_55755());
        class_238 box = target.method_5829();
        class_243 class_2432 = at = box.method_1006(eye) ? eye : (class_243)box.method_992(eye, eye.method_1019(class_243.method_1030((float)sent.pitch(), (float)sent.yaw()).method_1021(reachLimit))).orElse(null);
        if (at == null || !MeleeAttacks.lineIsClear(player, eye, at)) {
            return false;
        }
        if (!MeleeAttacks.attack(this.mc, new class_3966((class_1297)target, at))) {
            return false;
        }
        MeleeAttacks.claimTick(this.mc);
        return true;
    }

    private static double reach(class_746 player, class_1657 target) {
        class_243 eye = player.method_33571();
        return eye.method_1022(TargetingUtils.closestPoint(eye, target.method_5829()));
    }

    private static boolean axeInHotbar(class_746 player) {
        return BotBody.hotbarSlot(player, stack -> stack.method_31573(class_3489.field_42612)) >= 0;
    }

    private static int weaponSlot(class_746 player, boolean shield) {
        class_1661 inventory = player.method_31548();
        int best = -1;
        double most = 0.0;
        for (int i = 0; i < 9; ++i) {
            class_1799 stack = inventory.method_5438(i);
            if (!MeleeAttacks.isMeleeWeapon(stack) || stack.method_31574(class_1802.field_49814) || shield && !stack.method_31573(class_3489.field_42612)) continue;
            double damage = MeleeAttacks.attackDamage(stack);
            if (best >= 0 && !(damage > most)) continue;
            best = i;
            most = damage;
        }
        return best;
    }

    private boolean breakHole(class_638 level, class_746 player, class_1657 target, RivalBook.Rival rival) {
        boolean pickaxe;
        int wait;
        AutoCityModule city = ModuleRegistry.get(AutoCityModule.class);
        if (city == null) {
            return false;
        }
        if (city.isEnabled()) {
            if (!this.cityStarted) {
                return false;
            }
            CombatMacroLock.release(this);
            this.body.face(this.mc, player, target, 1.0f);
            return true;
        }
        if (this.cityStarted) {
            this.cityStarted = false;
            this.breachAfter = this.tick + 100;
            this.progressAt = this.tick;
            if (Combo.inHole((class_1309)target)) {
                ++this.cityFails;
            }
        }
        if (this.stance == Stance.RUN || this.tick < this.breachAfter || !Combo.inHole((class_1309)target) || player.method_6032() + player.method_6067() < 8.0f) {
            return false;
        }
        int n = wait = rival != null && rival.campsInHoles() ? 15 : 40;
        if (this.tick - this.progressAt < wait) {
            return false;
        }
        boolean bl = pickaxe = BotBody.hotbarSlot(player, stack -> stack.method_31573(class_3489.field_42614)) >= 0;
        if (this.cityFails >= 2 && pickaxe && this.roofOff(level, player, target)) {
            return true;
        }
        if (this.cityFails >= 2 || !pickaxe) {
            return this.camp(level, player, target);
        }
        if ((double)player.method_5739((class_1297)target) > 3.6) {
            this.body.face(this.mc, player, target, 1.0f);
            this.body.keepDistance(this.mc, player, target, 1.6, 2.6, 3.4, false);
            return true;
        }
        city.setEnabled(true);
        if (!city.isEnabled()) {
            return false;
        }
        this.cityStarted = true;
        ModuleChat.send((ClientModule)this, ModuleChat.body().text("Breaking ").name(target.method_5477().getString()).text("'s hole open."));
        return true;
    }

    private boolean roofOff(class_638 level, class_746 player, class_1657 target) {
        if (!this.anchors.get() || level.method_27983() == class_1937.field_25180 || BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_23141)) < 0 || BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_8801)) < 0) {
            return false;
        }
        class_2338 roof = target.method_24515().method_10086(2);
        class_2680 state = level.method_8320(roof);
        if (state.method_45474() || state.method_26214((class_1922)level, roof) < 0.0f) {
            return false;
        }
        if (this.mine(level, player, roof)) {
            this.body.face(this.mc, player, target, 1.0f);
            return true;
        }
        class_2338 view = CrystalBotModule.roofView(level, player, roof);
        if (view == null) {
            return false;
        }
        this.body.face(this.mc, player, target, 1.0f);
        this.body.toward(this.mc, player, class_243.method_24955((class_2382)view));
        return true;
    }

    private static boolean walkable(class_638 level, class_243 from, class_243 to) {
        class_243 way = to.method_1020(from);
        int steps = Math.max(1, (int)Math.ceil(way.method_37267() / 0.5));
        int lastY = class_3532.method_15357((double)(from.field_1351 + 0.01));
        for (int i = 1; i <= steps; ++i) {
            class_243 at = from.method_1019(way.method_1021((double)i / (double)steps));
            class_2338 feet = StandPicker.standable((class_1937)level, class_2338.method_49637((double)at.field_1352, (double)lastY, (double)at.field_1350));
            if (feet == null || Math.abs(feet.method_10264() - lastY) > 1) {
                return false;
            }
            lastY = feet.method_10264();
        }
        return true;
    }

    private static class_2338 roofView(class_638 level, class_746 player, class_2338 roof) {
        class_2338 here = player.method_24515();
        class_2338 best = null;
        double nearest = Double.MAX_VALUE;
        double reach = player.method_55754() - 0.2;
        block0: for (class_2338 cell : class_2338.method_10097((class_2338)here.method_10069(-4, -1, -4), (class_2338)here.method_10069(4, 1, 4))) {
            class_2338 feet = StandPicker.standable((class_1937)level, cell);
            if (feet == null || !feet.equals((Object)cell) || !CrystalBotModule.walkable(level, player.method_73189(), class_243.method_24955((class_2382)feet))) continue;
            class_243 eye = class_243.method_24955((class_2382)feet).method_1031(0.0, (double)player.method_5751(), 0.0);
            double distance = feet.method_10262((class_2382)here);
            if (distance >= nearest) continue;
            for (class_2350 face : class_2350.values()) {
                class_3965 hit;
                class_243 point = class_243.method_24953((class_2382)roof).method_43206(face, 0.5);
                if (eye.method_1022(point) > reach || (hit = level.method_17742(new class_3959(eye, point.method_43206(face, -0.05), class_3959.class_3960.field_17559, class_3959.class_242.field_1348, (class_1297)player))).method_17783() != class_239.class_240.field_1332 || !hit.method_17777().equals((Object)roof) || hit.method_17780() != face) continue;
                best = feet.method_10062();
                nearest = distance;
                continue block0;
            }
        }
        return best;
    }

    private boolean mine(class_638 level, class_746 player, class_2338 pos) {
        class_3965 held;
        class_2680 state = level.method_8320(pos);
        int slot = CrystalBotModule.toolSlot(player.method_31548(), state);
        if (slot < 0) {
            return false;
        }
        class_3965 face = CrystalBotModule.visibleFace(level, player, pos);
        if (face == null) {
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        Rotation sent = ServerRotation.sent();
        class_3965 class_39652 = held = sent == null ? null : AnchorGuard.trace(level, player, sent);
        if (held == null || !held.method_17777().equals((Object)pos)) {
            ServerRotation.request(RotationUtils.fromTo(player.method_33571(), face.method_17784()), 6.0f);
            return true;
        }
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
            return true;
        }
        ServerRotation.request(sent);
        if (MeleeAttacks.actedThisTick((class_1657)player)) {
            return true;
        }
        if (this.mc.field_1761.method_2902(pos, held.method_17780())) {
            player.method_6104(class_1268.field_5808);
        }
        MeleeAttacks.noteAction((class_1657)player);
        this.diggingAt = player.field_6012;
        return true;
    }

    public static boolean digging() {
        CrystalBotModule module = ModuleRegistry.get(CrystalBotModule.class);
        class_746 player = class_310.method_1551().field_1724;
        return module != null && module.isEnabled() && player != null && player.field_6012 - module.diggingAt <= 1;
    }

    private static int toolSlot(class_1661 inventory, class_2680 state) {
        int best = -1;
        float fastest = 0.0f;
        for (int i = 0; i < 9; ++i) {
            boolean fits;
            class_1799 stack = inventory.method_5438(i);
            boolean bl = fits = stack.method_7951(state) || !state.method_29291();
            if (!fits || stack.method_7924(state) <= fastest) continue;
            best = i;
            fastest = stack.method_7924(state);
        }
        return best;
    }

    private static class_3965 visibleFace(class_638 level, class_746 player, class_2338 pos) {
        class_243 eye = player.method_33571();
        class_3965 best = null;
        double nearest = player.method_55754() * player.method_55754();
        for (class_2350 face : class_2350.values()) {
            class_3965 hit;
            class_243 point = class_243.method_24953((class_2382)pos).method_43206(face, 0.5);
            double distance = eye.method_1025(point);
            if (distance > nearest || (hit = AnchorGuard.trace(level, player, RotationUtils.fromTo(eye, point.method_43206(face, -0.05)))) == null || !hit.method_17777().equals((Object)pos) || hit.method_17780() != face) continue;
            best = hit;
            nearest = distance;
        }
        return best;
    }

    private boolean camp(class_638 level, class_746 player, class_1657 target) {
        class_2338 feet = target.method_24515();
        if (level.method_18467(class_1511.class, new class_238(feet).method_1009(3.0, 1.0, 3.0)).size() >= 2) {
            return false;
        }
        class_243 eye = player.method_33571();
        class_2338 best = null;
        float least = this.selfCapNow - 2.0f;
        double hit = player.method_55755() - 0.1;
        for (class_2338 cell : class_2338.method_10097((class_2338)feet.method_10069(-2, 0, -2), (class_2338)feet.method_10069(2, 0, 2))) {
            float own;
            class_2338 base;
            if (Math.max(Math.abs(cell.method_10263() - feet.method_10263()), Math.abs(cell.method_10260() - feet.method_10260())) != 2 || (base = cell.method_10074().method_10062()).method_10264() < CrystalBotModule.feetY(player) || !Crystals.isBase(level.method_8320(base)) || !Crystals.canHost((class_1937)level, base, null) || eye.method_1025(TargetingUtils.closestPoint(eye, Crystals.boxOn(base))) > hit * hit || (own = CrystalDamage.estimate(Crystals.explosionAt(base), (class_1309)player)) > least || CrystalDamage.endangers((class_1657)player, own) || CrystalBotModule.topAim(level, player, base) == null) continue;
            best = base;
            least = own;
        }
        if (best == null) {
            return false;
        }
        this.body.face(this.mc, player, target, 1.0f);
        this.placeCrystal(level, player, best);
        return true;
    }

    private static class_243 topAim(class_638 level, class_746 player, class_2338 base) {
        double[][] spots;
        class_243 eye = player.method_33571();
        for (double[] at : spots = new double[][]{{0.5, 0.5}, {0.2, 0.2}, {0.8, 0.2}, {0.2, 0.8}, {0.8, 0.8}}) {
            class_3965 hit;
            class_243 point = new class_243((double)base.method_10263() + at[0], (double)base.method_10264() + 1.0, (double)base.method_10260() + at[1]);
            if (eye.method_1022(point) > player.method_55754() - 0.1 || (hit = AnchorGuard.trace(level, player, RotationUtils.fromTo(eye, point))) == null || !hit.method_17777().equals((Object)base) || hit.method_17780() != class_2350.field_11036) continue;
            return point;
        }
        return null;
    }

    private void placeCrystal(class_638 level, class_746 player, class_2338 base) {
        class_3965 hit;
        int slot = BotBody.hotbarSlot(player, stack -> stack.method_31574(class_1802.field_8301));
        if (slot < 0 || !this.hands()) {
            return;
        }
        Rotation sent = ServerRotation.sent();
        class_3965 class_39652 = hit = sent == null ? null : AnchorGuard.trace(level, player, sent);
        if (hit == null || !hit.method_17777().equals((Object)base) || hit.method_17780() != class_2350.field_11036) {
            class_243 aim = CrystalBotModule.topAim(level, player, base);
            if (aim != null) {
                ServerRotation.request(RotationUtils.fromTo(player.method_33571(), aim), 6.0f);
            }
            return;
        }
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
            return;
        }
        if (!MeleeAttacks.handsFree(this.mc, player)) {
            return;
        }
        if (Crystals.rightClick(this.mc, player, hit)) {
            Crystals.notePlace(player, base);
            MeleeAttacks.claimTick(this.mc);
        }
        ServerRotation.request(sent);
    }

    private boolean closeSpot(class_638 level, class_746 player, class_1657 target) {
        int slot = CrystalBotModule.guardBlockSlot(player);
        if (slot < 0) {
            return false;
        }
        if (this.blocking == null && this.tick >= this.spotScanAt) {
            this.spotScanAt = this.tick + 5;
            this.blocking = CrystalBotModule.dangerSpot(level, player, target);
        }
        if (this.blocking == null) {
            return false;
        }
        if (!level.method_8320(this.blocking).method_45474() || !level.method_8628(class_2246.field_10540.method_9564(), this.blocking, class_3726.method_16194())) {
            this.blocking = null;
            return false;
        }
        AnchorGuard.Spot spot = AnchorGuard.supportFor(level, player, player.method_33571(), this.blocking);
        if (spot == null) {
            this.blocking = null;
            return false;
        }
        if (this.placeAt(level, player, spot, slot) == 1) {
            this.blocking = null;
        }
        return true;
    }

    private static class_2338 dangerSpot(class_638 level, class_746 player, class_1657 target) {
        class_2338 feet = player.method_24515();
        class_243 theirEye = target.method_33571();
        double reach = Math.max(4.6, player.method_55754() + 0.1);
        class_2338 worst = null;
        float first = 0.0f;
        float second = 0.0f;
        for (class_2338 base : class_2338.method_10097((class_2338)feet.method_10069(-4, -2, -4), (class_2338)feet.method_10069(4, 1, 4))) {
            class_243 center;
            float us;
            if (!Crystals.isBase(level.method_8320(base)) || !Crystals.canHost((class_1937)level, base, null) || theirEye.method_1022(new class_243((double)base.method_10263() + 0.5, (double)base.method_10264() + 1.0, (double)base.method_10260() + 0.5)) > reach || (us = CrystalDamage.estimate(center = Crystals.explosionAt(base), (class_1309)player)) <= second || CrystalDamage.estimate(center, (class_1309)target) >= us + 2.0f) continue;
            if (us > first) {
                second = first;
                first = us;
                worst = base.method_10084().method_10062();
                continue;
            }
            second = us;
        }
        return first >= 8.0f && second <= first / 2.0f ? worst : null;
    }

    private int placeAt(class_638 level, class_746 player, AnchorGuard.Spot spot, int slot) {
        class_3965 hit;
        if (!this.hands()) {
            return 0;
        }
        Rotation sent = ServerRotation.sent();
        class_3965 class_39652 = hit = sent == null ? null : AnchorGuard.hitAlong(level, player, sent, spot);
        if (hit == null) {
            ServerRotation.request(RotationUtils.fromTo(player.method_33571(), spot.aim()), 6.0f);
            return 0;
        }
        if (player.method_31548().method_67532() != slot) {
            player.method_31548().method_61496(slot);
            return 0;
        }
        if (!MeleeAttacks.handsFree(this.mc, player)) {
            return 0;
        }
        AnchorGuard.click(this.mc, player, hit);
        ServerRotation.request(sent);
        return 1;
    }

    private boolean defuse(class_638 level, class_746 player) {
        if (this.tick - this.defuseAt < 10 || level.method_27983() == class_1937.field_25180) {
            return false;
        }
        class_2338 feet = player.method_24515();
        for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-5, -2, -5), (class_2338)feet.method_10069(5, 3, 5))) {
            class_2338 anchor;
            class_243 center;
            class_2680 state = level.method_8320(pos);
            if (!state.method_27852(class_2246.field_23152) || (Integer)state.method_11654((class_2769)class_4969.field_23153) <= 0 || !player.method_56093(pos, -0.1) || pos.equals((Object)this.digAnchor) || CrystalDamage.estimate(center = class_243.method_24953((class_2382)(anchor = pos.method_10062())), (class_1309)player, 5.0f, anchor) > Math.max(1.0f, 0.15f * player.method_6032())) continue;
            boolean theirs = false;
            for (class_1657 enemy : CrystalBotModule.enemies(level, player, 16.0)) {
                theirs |= enemy.method_33571().method_1022(center) <= 5.5;
            }
            if (!theirs) continue;
            class_1661 inventory = player.method_31548();
            int route = AnchorGuard.detonatorRoute(inventory, player.method_6047());
            if (route == -1 || !this.hands()) {
                return false;
            }
            class_3965 hit = AnchorGuard.anchorHit(level, player, anchor);
            if (hit == null) {
                class_243 aim = AnchorGuard.anchorAim(level, player, anchor);
                if (aim == null) continue;
                ServerRotation.request(RotationUtils.fromTo(player.method_33571(), aim), 6.0f);
                return true;
            }
            if (route != -2) {
                inventory.method_61496(route);
                return true;
            }
            if (!MeleeAttacks.handsFree(this.mc, player)) {
                return true;
            }
            AnchorGuard.click(this.mc, player, hit);
            this.defuseAt = this.tick;
            return true;
        }
        return false;
    }

    private boolean guard(class_638 level, class_746 player, class_1657 target) {
        float health = player.method_6032() + player.method_6067();
        class_2338 danger = null;
        float worst = 0.0f;
        class_2338 feet = player.method_24515();
        for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-5, -2, -5), (class_2338)feet.method_10069(5, 3, 5))) {
            class_243 center;
            float hurt;
            if (!level.method_8320(pos).method_27852(class_2246.field_23152) || pos.equals((Object)this.digAnchor) || (hurt = CrystalDamage.estimate(center = class_243.method_24953((class_2382)pos), (class_1309)player, 5.0f, pos.method_10062())) < Math.max(4.0f, health * 0.3f) && hurt < health - 1.0f || target.method_33571().method_1022(center) > 5.5 || hurt <= worst) continue;
            worst = hurt;
            danger = pos.method_10062();
        }
        if (danger == null) {
            return false;
        }
        AnchorGuard.Spot spot = AnchorGuard.find(level, player, danger, player.method_33571());
        if (spot != null && spot.standing()) {
            return false;
        }
        int slot = CrystalBotModule.guardBlockSlot(player);
        if (spot == null || slot < 0 || this.tick - this.shieldAt < 20) {
            this.body.face(this.mc, player, target, 1.0f);
            this.body.away(this.mc, player, class_243.method_24953(danger));
            return true;
        }
        this.body.face(this.mc, player, target, 1.0f);
        if (this.placeAt(level, player, spot, slot) == 1) {
            this.shieldAt = this.tick;
        }
        return true;
    }

    private static int guardBlockSlot(class_746 player) {
        int obsidian = BotBody.hotbarSlot(player, OBSIDIAN);
        return obsidian >= 0 ? obsidian : BotBody.hotbarSlot(player, AnchorGuard::isGuardBlock);
    }

    private boolean cover(class_638 level, class_746 player, class_1657 target, double distance) {
        boolean wants;
        boolean boxing = this.boxingAt != null && this.boxingAt.equals((Object)player.method_24515()) && this.tick < this.boxingUntil;
        boolean bl = wants = player.method_6032() + player.method_6067() <= this.retreatAt() + 4.0f || this.stance == Stance.GUARD;
        if (this.surround.get() && !boxing && wants && player.method_24828()) {
            this.boxingAt = player.method_24515();
            this.boxingUntil = this.tick + 60;
            boxing = true;
        }
        if (!boxing) {
            this.boxingAt = null;
        }
        return this.surround.get() && boxing && player.method_24828() && this.boxIn(level, player);
    }

    private boolean boxIn(class_638 level, class_746 player) {
        List<class_2338> open = CrystalBotModule.openAroundFeet(level, player);
        if (open.isEmpty() || BotBody.hotbarSlot(player, OBSIDIAN) < 0) {
            return false;
        }
        if (!this.hands()) {
            return true;
        }
        for (class_2338 cell : open) {
            class_3965 support = AutoTrapModule.support(level, player, cell);
            if (support == null || cell.equals((Object)this.boxSkipped) && this.tick < this.boxSkippedUntil) continue;
            class_3965 seen = AutoTrapModule.seenByServer(level, player, support);
            if (seen == null) {
                this.boxAimTicks = cell.equals((Object)this.boxAiming) ? this.boxAimTicks + 1 : 0;
                this.boxAiming = cell;
                if (this.boxAimTicks >= 10) {
                    this.boxSkipped = cell;
                    this.boxSkippedUntil = this.tick + 40;
                    continue;
                }
                ServerRotation.request(RotationUtils.fromTo(player.method_33571(), support.method_17784()), 6.0f);
                return true;
            }
            int slot = BotBody.hotbarSlot(player, OBSIDIAN);
            if (player.method_31548().method_67532() != slot) {
                player.method_31548().method_61496(slot);
                return true;
            }
            if (!MeleeAttacks.handsFree(this.mc, player)) {
                return true;
            }
            class_1269 result = this.mc.field_1761.method_2896(player, class_1268.field_5808, seen);
            if (result.method_23665()) {
                player.method_6104(class_1268.field_5808);
                MeleeAttacks.noteAction((class_1657)player);
                ServerRotation.request(ServerRotation.sent());
            }
            return true;
        }
        return false;
    }

    private static List<class_2338> openAroundFeet(class_638 level, class_746 player) {
        ArrayList<class_2338> open = new ArrayList<class_2338>();
        for (class_2338 cell : CrystalBotModule.ring(player)) {
            class_2680 state = level.method_8320(cell);
            if (!state.method_45474() || cell.method_46558().method_1022(player.method_33571()) > player.method_55754() || !level.method_8628(class_2246.field_10540.method_9564(), cell, class_3726.method_16194())) continue;
            open.add(cell);
        }
        return open;
    }

    private static boolean surrounded(class_638 level, class_746 player) {
        for (class_2338 cell : CrystalBotModule.ring(player)) {
            if (!level.method_8320(cell).method_26220((class_1922)level, cell).method_1110()) continue;
            return false;
        }
        return true;
    }

    private static Set<class_2338> ring(class_746 player) {
        class_238 box = player.method_5829();
        int feet = class_3532.method_15357((double)(box.field_1322 + 0.01));
        LinkedHashSet<Long> columns = new LinkedHashSet<Long>();
        for (int x = class_3532.method_15357((double)box.field_1323); x <= class_3532.method_15357((double)(box.field_1320 - 1.0E-4)); ++x) {
            for (int z = class_3532.method_15357((double)box.field_1321); z <= class_3532.method_15357((double)(box.field_1324 - 1.0E-4)); ++z) {
                columns.add(class_2338.method_10064((int)x, (int)0, (int)z));
            }
        }
        LinkedHashSet<class_2338> ring = new LinkedHashSet<class_2338>();
        Iterator iterator = columns.iterator();
        while (iterator.hasNext()) {
            long key = (Long)iterator.next();
            class_2338 column = class_2338.method_10092((long)key).method_33096(feet);
            for (class_2350 side : class_2350.class_2353.field_11062) {
                class_2338 cell = column.method_10093(side);
                if (columns.contains(class_2338.method_10064((int)cell.method_10263(), (int)0, (int)cell.method_10260()))) continue;
                ring.add(cell);
            }
        }
        return ring;
    }

    private static boolean armorLow(class_1309 player) {
        for (class_1304 slot : class_1304.field_54086) {
            class_1799 stack;
            if (!slot.method_46643() || !(stack = player.method_6118(slot)).method_7963() || !((float)(stack.method_7936() - stack.method_7919()) < (float)stack.method_7936() * 0.3f)) continue;
            return true;
        }
        return false;
    }

    private boolean throwXp(class_746 player) {
        if (BotBody.hotbarSlot(player, XP) < 0) {
            return false;
        }
        this.mending = true;
        Aim.turn(this.mc, player.method_36454(), 86.0f, 22.0f);
        if (player.method_36455() > 70.0f) {
            this.body.use(this.mc, player, XP);
        }
        return true;
    }

    private boolean mendIdle(class_746 player) {
        if (!this.mendArmor.get() || !CrystalBotModule.armorLow((class_1309)player) && !this.mending || !CrystalBotModule.hasDamagedArmor(player)) {
            return false;
        }
        CombatMacroLock.take(this);
        return this.throwXp(player);
    }

    private static boolean hasDamagedArmor(class_746 player) {
        for (class_1304 slot : class_1304.field_54086) {
            if (!slot.method_46643() || !player.method_6118(slot).method_7986()) continue;
            return true;
        }
        return false;
    }

    private static int count(class_746 player, Predicate<class_1799> want) {
        int count = 0;
        class_1661 inventory = player.method_31548();
        for (int i = 0; i < 36; ++i) {
            if (!want.test(inventory.method_5438(i))) continue;
            count += inventory.method_5438(i).method_7947();
        }
        return count + (want.test(player.method_6079()) ? player.method_6079().method_7947() : 0);
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Stance {
        PRESS(3.5f, 10.0f, 5.0f, 0.7),
        HOLD(4.5f, 8.0f, 6.0f, 1.0),
        GUARD(6.0f, 5.0f, 7.0f, 1.6),
        RUN(99.0f, 3.0f, 99.0f, 2.0);

        final float minDamage;
        final float selfCap;
        final float anchorMin;
        final double cover;

        private Stance(float minDamage, float selfCap, float anchorMin, double cover) {
            this.minDamage = minDamage;
            this.selfCap = selfCap;
            this.anchorMin = anchorMin;
            this.cover = cover;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Throw {
        ESCAPE,
        CHASE;

    }
}

