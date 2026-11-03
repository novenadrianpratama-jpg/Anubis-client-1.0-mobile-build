/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1299
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1922
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2350$class_2353
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2604
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_8042
 */
package net.anubis.client.module.impl.combat;

import imgui.ImDrawList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.PlayerAttackEntityEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.AnchorAuraModule;
import net.anubis.client.module.impl.combat.AutoCityModule;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.crystal.Combo;
import net.anubis.client.module.impl.combat.crystal.CrystalDamage;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.crystal.HurtClock;
import net.anubis.client.module.impl.combat.crystal.LootGuard;
import net.anubis.client.module.impl.combat.crystal.ShotView;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.impl.misc.AutoEatModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.KeySetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.ServerRotation;
import net.anubis.client.util.pvp.TargetingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2604;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_8042;

@Environment(value=EnvType.CLIENT)
public final class CrystalAuraModule
extends ClientModule
implements HudOverlayProvider {
    private static final int COUNTER_CAP = 1000;
    private static final int IDLE_TICKS = 10;
    private static final float FACE_PLACE_HEALTH = 10.0f;
    private static final float FACE_PLACE_DAMAGE = 2.0f;
    private static final float ARMOR_LOW = 0.1f;
    private static final float BREAK_FACTOR = 0.6f;
    private static final double AIM_INSET = 0.1;
    private static final int MAX_BREAK_ATTEMPTS = 3;
    private static final double CHAIN_RANGE = 12.0;
    private static final int WEAPON_SETTLE_TICKS = 2;
    private static final float LOOK_BONUS = 2.0f;
    private static final int MAX_LEAD_TICKS = 5;
    private static final float LOOK_MATCH_DEGREES = 2.0f;
    private static final double PLACE_BREAK_MARGIN = 0.1;
    private static final float ONE_LOOK_BONUS = 2.0f;
    private static final float OBSIDIAN_UPGRADE_BAR = 8.0f;
    private static final float OBSIDIAN_MIN_GAIN = 5.0f;
    private static final float OBSIDIAN_MIN_RATIO = 2.0f;
    private static final float EAGER_UPGRADE_BAR = 11.0f;
    private static final float EAGER_MIN_GAIN = 3.0f;
    private static final float EAGER_MIN_RATIO = 1.4f;
    private static final double PLACE_REACH_MARGIN = 0.1;
    private static final float HELD_VIEW_BONUS = 20.0f;
    private final BooleanSetting requireHold = this.addSetting("require-hold", new BooleanSetting("Require Hold", false));
    private final KeySetting holdKey = this.addSetting("hold-key", new KeySetting("Hold Key", 1003));
    private final NumberSetting placeDelay = this.addSetting("place-delay", new NumberSetting("Place Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting breakDelay = this.addSetting("break-delay", new NumberSetting("Break Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting minDamage = this.addSetting("min-damage", new NumberSetting("Min Damage", 4.0f, 0.0f, 20.0f).step(0.5f));
    private final NumberSetting maxSelfDamage = this.addSetting("max-self-damage", new NumberSetting("Max Self Damage", 8.0f, 0.0f, 20.0f).step(0.5f));
    private final BooleanSetting facePlace = this.addSetting("face-place", new BooleanSetting("Face Place", true));
    private final KeySetting forceFacePlace = this.addSetting("force-face-place", new KeySetting("Force Face Place", -1));
    private final BooleanSetting autoObsidian = this.addSetting("auto-obsidian", new BooleanSetting("Auto Obsidian", true));
    private final BooleanSetting switchBack = this.addSetting("switch-back", new BooleanSetting("Switch Back", true));
    private final NumberSetting turnSpeed = this.addSetting("turn-speed", new NumberSetting("Turn Speed", 4.5f, 1.0f, 10.0f).step(0.5f));
    private final NumberSetting viewAngle = this.addSetting("view-angle", new NumberSetting("View Angle", 90.0f, 30.0f, 180.0f).step(5.0f));
    private final BooleanSetting humanize = this.addSetting("humanize", new BooleanSetting("Human Pace", true));
    private final BooleanSetting protectLoot = this.addSetting("protect-loot", new BooleanSetting("Protect Loot", true));
    private final BooleanSetting render = this.addSetting("render", new BooleanSetting("Render", true));
    private final ColorSetting renderColor = this.addSetting("render-color", new ColorSetting("Render Color", -2728705));
    private final ShotView view = new ShotView();
    private boolean engaged;
    private int idleTicks;
    private int savedSlot = -1;
    private int ownSlot = -1;
    private int ticksSinceBreak = 1000;
    private int ticksSincePlace = 1000;
    private int breakGap;
    private int placeGap;
    private class_1799 lastMain;
    private int mainAge = 1000;
    private int pendingBreakId = -1;
    private int pendingBreakAge = 1000;
    private class_2338 pendingObsidian;
    private int pendingObsidianAge = 1000;
    private final Int2IntOpenHashMap breakAttempts = new Int2IntOpenHashMap();
    private final Int2IntOpenHashMap doomed = new Int2IntOpenHashMap();
    private float steerMin;
    private float steerSelf;
    private int steerUntil = -1;
    private float tickMin;
    private float tickSelf;
    private int eagerUntil = -1;
    private int floorY;
    private int floorUntil = -1;
    private int tickFloor = Integer.MIN_VALUE;
    private int fastUntil = -1;
    private boolean fullSpeed;

    public CrystalAuraModule() {
        super("crystal-aura", "Crystal Aura", "Places and breaks end crystals for the most damage.", ModuleCategory.COMBAT);
        this.holdKey.visibleWhen(this.requireHold::get);
        this.renderColor.visibleWhen(this.render::get);
    }

    @Override
    protected void onEnable() {
        this.resetState();
    }

    @Override
    protected void onDisable() {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && !AutoEatModule.isEating() && !AutoInvTotemModule.wantsHotbar()) {
            this.restoreSlot(player);
        }
        this.resetState();
        this.view.clear();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_1799 offhand;
        boolean offhandUse;
        boolean anchors;
        class_310 mc = class_310.method_1551();
        this.ageCounters();
        class_746 player = mc.field_1724;
        if (player != null && mc.field_1687 != null) {
            LootGuard.update(mc.field_1687, player);
        }
        if (player == null || mc.field_1687 == null || mc.field_1761 == null || player.method_29504()) {
            this.disengage(mc, null);
            Crystals.forgetRtt();
            return;
        }
        HurtClock.tick(player);
        this.forgetGone(mc.field_1687);
        boolean steered = player.field_6012 <= this.steerUntil;
        this.tickMin = steered ? this.steerMin : this.minDamage.get();
        this.tickSelf = steered ? this.steerSelf : this.maxSelfDamage.get();
        this.tickFloor = player.field_6012 <= this.floorUntil ? this.floorY : Integer.MIN_VALUE;
        this.fullSpeed = player.field_6012 <= this.fastUntil;
        class_1799 main = player.method_6047();
        if (main != this.lastMain) {
            this.lastMain = main;
            this.mainAge = 0;
        }
        Look look = ServerRotation.canRotate(player) ? Look.SILENT : Look.CROSSHAIR;
        boolean crystals = !this.requireHold.get() || Crystals.isKeyDown(mc, this.holdKey.value());
        AnchorAuraModule aura = ModuleRegistry.get(AnchorAuraModule.class);
        boolean bl = anchors = aura != null && aura.isEnabled() && look == Look.SILENT;
        if (!MeleeAttacks.canAct(mc) || player.method_7325() || !crystals && !anchors) {
            this.disengage(mc, player);
            return;
        }
        if (AutoInvTotemModule.wantsHotbar() || AutoCityModule.mining() || CombatMacroLock.heldByOther(this)) {
            CombatMacroLock.release(this);
            return;
        }
        if (this.ownSlot != -1 && !this.ownsSelection(player)) {
            this.forgetSlots();
        }
        boolean bl2 = offhandUse = !Crystals.isCombatItem(offhand = player.method_6079()) && !offhand.method_31574(class_1802.field_8301) && !offhand.method_31574(class_1802.field_8281);
        if (MeleeAttacks.rightClickComing(mc) && (offhandUse || main.method_31574(class_1802.field_23141) || main.method_31574(class_1802.field_8801))) {
            this.disengage(mc, player);
            return;
        }
        if (MeleeAttacks.actedThisTick((class_1657)player) || !MeleeAttacks.handsFree(mc, player) || !this.ownsSelection(player) && !CrystalAuraModule.mayTakeHand(player, anchors)) {
            this.idle(mc, player);
            return;
        }
        List<class_1309> enemies = Combo.enemies(mc, player);
        if (enemies.isEmpty()) {
            this.disengage(mc, player);
            return;
        }
        Rotation fixedLook = null;
        if (look == Look.CROSSHAIR) {
            fixedLook = RotationUtils.entityRotation((class_1297)player);
            if (ServerRotation.pending() || !CrystalAuraModule.sameLook(ServerRotation.sent(), fixedLook)) {
                this.idle(mc, player);
                return;
            }
        }
        int window = Crystals.confirmWindow(mc, player);
        class_1511 lastHit = this.lastHit(mc.field_1687, window);
        Fight fight = new Fight(player, mc.field_1687, enemies, Combo.friends(mc, player), window, Crystals.rttTicks(mc, player), fixedLook, look == Look.SILENT ? 2.0f : 0.0f, Crystals.isKeyDown(mc, this.forceFacePlace.value()));
        Combo.Shot shot = this.choose(mc, fight, lastHit, crystals, anchors ? aura : null);
        if (shot == null) {
            this.idle(mc, player);
            return;
        }
        this.engage();
        Object object = shot.plan();
        if (object instanceof Plan) {
            Plan plan = (Plan)object;
            if (this.render.get()) {
                this.show(fight, plan);
            }
            if (!this.perform(mc, player, fight, plan, look) || look != Look.SILENT) {
                return;
            }
            Plan following = this.planCrystal(fight, this.lastHit(mc.field_1687, window), 1, this.stagedBreak(fight));
            Rotation sent = ServerRotation.sent();
            this.turn(following == null || fight.holds(following) ? sent : fight.lookAt(following.aim()));
        } else {
            aura.show(shot);
            if (this.fire(mc, player, fight, aura, shot)) {
                ServerRotation.request(ServerRotation.sent());
            }
        }
    }

    public void steer(class_746 player, float minDamage, float maxSelf) {
        this.steerMin = minDamage;
        this.steerSelf = maxSelf;
        this.steerUntil = player.field_6012 + 2;
    }

    public void buildEagerly(class_746 player) {
        this.eagerUntil = player.field_6012 + 2;
    }

    public void crystalsFrom(class_746 player, int lowest) {
        this.floorY = lowest;
        this.floorUntil = player.field_6012 + 2;
    }

    public void fullSpeed(class_746 player) {
        this.fastUntil = player.field_6012 + 2;
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (this.render.get()) {
            this.view.draw();
        }
    }

    @Override
    public boolean isHudOverlayEnabled() {
        return this.isEnabled() && this.render.get() && this.view.showing();
    }

    @Override
    public void renderHudOverlay(ImDrawList dl, float screenW, float screenH) {
        this.view.label(dl, screenW, screenH);
    }

    @EventHandler
    public void onAttack(PlayerAttackEntityEvent event) {
        class_1511 crystal;
        class_1297 class_12972 = event.target;
        if (class_12972 instanceof class_1511 && (crystal = (class_1511)class_12972).method_73183().method_8608()) {
            this.markBreak(crystal);
        }
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_8042) {
            class_8042 bundle = (class_8042)class_25962;
            for (class_2596 packet : bundle.method_48324()) {
                this.note(player, packet);
            }
        } else {
            this.note(player, event.packet);
        }
        HurtClock.note(player, event.packet);
    }

    private void note(class_746 player, class_2596<?> packet) {
        class_2604 spawn;
        if (packet instanceof class_2604 && (spawn = (class_2604)packet).method_11169() == class_1299.field_6110) {
            Crystals.noteSpawn(player, class_2338.method_49637((double)spawn.method_11175(), (double)(spawn.method_11174() - 1.0), (double)spawn.method_11176()));
        }
    }

    private static boolean mayTakeHand(class_746 player, boolean anchors) {
        class_1799 main = player.method_6047();
        return main.method_31574(class_1802.field_8301) || main.method_31574(class_1802.field_8281) || Crystals.isCombatItem(main) || anchors && (main.method_31574(class_1802.field_23141) || main.method_31574(class_1802.field_8801));
    }

    private boolean ownsSelection(class_746 player) {
        return this.ownSlot != -1 && player.method_31548().method_67532() == this.ownSlot;
    }

    private void engage() {
        this.engaged = true;
        this.idleTicks = 0;
        CombatMacroLock.tryAcquire(this);
    }

    private void idle(class_310 mc, class_746 player) {
        if (!this.engaged || ++this.idleTicks >= 10) {
            this.disengage(mc, player);
        }
    }

    private void disengage(class_310 mc, class_746 player) {
        this.engaged = false;
        this.idleTicks = 0;
        CombatMacroLock.release(this);
        if (player == null) {
            this.forgetSlots();
        } else if (!(mc.field_1755 != null || AutoEatModule.isEating() || AutoInvTotemModule.wantsHotbar() || MeleeAttacks.actedThisTick((class_1657)player))) {
            this.restoreSlot(player);
        }
    }

    private void restoreSlot(class_746 player) {
        if (this.ownSlot == -1) {
            return;
        }
        class_1661 inventory = player.method_31548();
        if (this.switchBack.get() && class_1661.method_7380((int)this.savedSlot) && this.savedSlot != this.ownSlot && inventory.method_67532() == this.ownSlot) {
            inventory.method_61496(this.savedSlot);
        }
        this.forgetSlots();
    }

    private void forgetSlots() {
        this.savedSlot = -1;
        this.ownSlot = -1;
    }

    private void resetState() {
        this.engaged = false;
        this.idleTicks = 0;
        this.forgetSlots();
        this.ticksSinceBreak = 1000;
        this.ticksSincePlace = 1000;
        this.breakGap = 0;
        this.placeGap = 0;
        this.lastMain = null;
        this.mainAge = 1000;
        this.pendingBreakId = -1;
        this.pendingBreakAge = 1000;
        this.pendingObsidian = null;
        this.pendingObsidianAge = 1000;
        this.breakAttempts.clear();
        this.doomed.clear();
        Crystals.forgetRtt();
        CombatMacroLock.release(this);
    }

    private void forgetGone(class_638 level) {
        CrystalAuraModule.forgetGone(level, this.breakAttempts.keySet());
        CrystalAuraModule.forgetGone(level, this.doomed.keySet());
    }

    private static void forgetGone(class_638 level, IntSet ids) {
        IntIterator iterator = ids.iterator();
        while (iterator.hasNext()) {
            if (level.method_8469(iterator.nextInt()) != null) continue;
            iterator.remove();
        }
    }

    private static boolean oneLook(class_243 eye, class_238 crystal, class_243 placeAim, double reach) {
        class_243 direction = placeAim.method_1020(eye).method_1029();
        return crystal.method_992(eye, eye.method_1019(direction.method_1021(reach))).isPresent();
    }

    private boolean canHostSoon(Fight fight, class_2338 base, class_1297 breaking) {
        class_2338 above = base.method_10084();
        if (!fight.level.method_22347(above)) {
            return false;
        }
        class_238 space = new class_238((double)above.method_10263(), (double)above.method_10264(), (double)above.method_10260(), (double)above.method_10263() + 1.0, (double)above.method_10264() + 2.0, (double)above.method_10260() + 1.0);
        for (class_1297 entity : fight.level.method_8335(breaking, space)) {
            boolean aside;
            class_1309 living;
            if (!entity.method_5805()) continue;
            if (!(entity instanceof class_1309) || !fight.enemies.contains(living = (class_1309)entity)) {
                return false;
            }
            class_238 then = living.method_5829().method_997(Combo.lead(living, fight.arriveLead));
            boolean over = then.field_1322 >= space.field_1325;
            boolean bl = aside = then.field_1320 <= space.field_1323 || then.field_1323 >= space.field_1320 || then.field_1324 <= space.field_1321 || then.field_1321 >= space.field_1324;
            if (over || aside) continue;
            return false;
        }
        return true;
    }

    private class_1511 lastHit(class_638 level, int window) {
        class_1511 crystal;
        if (this.pendingBreakId < 0 || this.pendingBreakAge >= window) {
            return null;
        }
        class_1297 class_12972 = level.method_8469(this.pendingBreakId);
        return class_12972 instanceof class_1511 && (crystal = (class_1511)class_12972).method_5805() ? crystal : null;
    }

    private Combo.Shot choose(class_310 mc, Fight fight, class_1511 lastHit, boolean crystals, AnchorAuraModule aura) {
        Combo.Shot picked;
        ArrayList<Combo.Shot> shots = new ArrayList<Combo.Shot>();
        Combo.Shot aimed = null;
        float rival = 0.0f;
        if (crystals) {
            Plan opening = this.stagedBreak(fight);
            Plan plan = this.planCrystal(fight, lastHit, 0, opening);
            if (plan != null) {
                aimed = this.shot(fight, plan, false);
                if (this.due(plan, 0)) {
                    shots.add(aimed);
                }
            }
            if (aura != null && opening != null) {
                rival = this.shot(fight, opening, true).value();
            }
        }
        if (aura != null) {
            shots.addAll(aura.shots(mc, fight.player, fight.enemies, fight.friends, fight.rtt, rival));
        }
        return (picked = Combo.pick(shots)) != null ? picked : aimed;
    }

    private void show(Fight fight, Plan plan) {
        Side side = fight.side(plan.step());
        boolean baseToCome = plan.step() == Step.OBSIDIAN;
        class_243 center = plan.step() == Step.BREAK ? plan.crystal().method_73189() : Crystals.explosionAt(plan.target());
        float most = 0.0f;
        for (CrystalDamage.Victim enemy : side.enemies) {
            most = Math.max(most, enemy.damage(center, baseToCome));
        }
        class_2338 base = plan.step() == Step.BREAK ? plan.crystal().method_24515().method_10074() : plan.target();
        this.view.show(new class_238(base), this.renderColor.argb(), most, side.self.damage(center, baseToCome));
    }

    private Plan planCrystal(Fight fight, class_1511 lastHit, int ahead, Plan opening) {
        boolean staged;
        Plan breakPlan = this.planBreak(fight, false);
        if (breakPlan != null && this.due(breakPlan, ahead)) {
            return breakPlan;
        }
        boolean bl = staged = breakPlan != null || opening != null || Crystals.anyPlacing(fight.player, fight.window);
        Plan placePlan = staged ? null : this.planPlacement(fight, lastHit, lastHit == null);
        return placePlan != null ? placePlan : breakPlan;
    }

    private Plan stagedBreak(Fight fight) {
        return this.planBreak(fight, true);
    }

    private Combo.Shot shot(Fight fight, Plan plan, boolean opening) {
        class_243 center;
        Step step = plan.step();
        class_243 class_2432 = center = step == Step.BREAK ? plan.crystal().method_73189() : Crystals.explosionAt(plan.target());
        int readyIn = step == Step.BREAK ? 0 : (step == Step.CRYSTAL ? fight.rtt : fight.rtt + 1);
        return new Combo.Shot(Combo.Kind.valueOf(step.name()), plan.aim(), plan.route(), this.score(fight, step, center, Float.NEGATIVE_INFINITY, opening), CrystalAuraModule.kills(fight, step, center, opening), readyIn, plan);
    }

    private Plan planBreak(Fight fight, boolean opening) {
        double reach = fight.player.method_55755();
        Plan best = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        class_238 around = fight.player.method_5829().method_1014(reach + 2.0);
        for (class_1511 crystal : fight.level.method_18467(class_1511.class, around)) {
            float score;
            Plan plan;
            class_243 eye;
            if (!crystal.method_5805() || this.breakAttempts.get(crystal.method_5628()) >= 3 || crystal.method_5628() == this.pendingBreakId && this.pendingBreakAge < fight.window || this.doomed.containsKey(crystal.method_5628()) && fight.player.field_6012 - this.doomed.get(crystal.method_5628()) < fight.window || (eye = fight.eye).method_1025(TargetingUtils.closestPoint(eye, crystal.method_5829())) > reach * reach || crystal.method_23318() < (double)this.tickFloor - 0.01) continue;
            class_243 center = crystal.method_73189();
            if (this.protectLoot.get() && LootGuard.near(center) || this.bound(fight, Step.BREAK, center) + fight.lookBonus <= bestScore || (plan = this.breakLook(fight, crystal)) == null || !fight.inView(plan) || (score = this.score(fight, Step.BREAK, center, bestScore - fight.lookBonus, opening)) == Float.NEGATIVE_INFINITY || (score += fight.lookCredit(plan)) <= bestScore) continue;
            best = plan;
            bestScore = score;
        }
        if (best == null) {
            return null;
        }
        int weapon = Crystals.weaponRoute(fight.player);
        return weapon == -1 ? null : best.withRoute(weapon);
    }

    private Plan breakLook(Fight fight, class_1511 crystal) {
        Plan throughBase;
        if (fight.fixedLook != null) {
            Plan plan = new Plan(Step.BREAK, crystal, null, null, crystal.method_5829().method_1005(), -2);
            return CrystalAuraModule.hitAlong(fight.player, plan, fight.eye, fight.fixedLook) != null ? plan : null;
        }
        class_2338 base = crystal.method_24515().method_10074();
        if (Crystals.isBase(fight.level.method_8320(base)) && CrystalAuraModule.hitAlong(fight.player, throughBase = new Plan(Step.BREAK, crystal, base, base, CrystalAuraModule.aimOnTop(fight.level, base), -2), fight.aimEye, fight.lookAt(throughBase.aim())) != null) {
            return throughBase;
        }
        class_243 toward = fight.towardLook(crystal.method_5829().method_1011(0.1));
        Plan turned = new Plan(Step.BREAK, crystal, null, null, toward, -2);
        if (CrystalAuraModule.hitAlong(fight.player, turned, fight.aimEye, fight.lookAt(toward)) != null) {
            return turned;
        }
        class_243 nearest = TargetingUtils.closestPoint(fight.aimEye, crystal.method_5829().method_1011(0.1));
        Plan plan = new Plan(Step.BREAK, crystal, null, null, nearest, -2);
        return CrystalAuraModule.hitAlong(fight.player, plan, fight.aimEye, fight.lookAt(nearest)) != null ? plan : null;
    }

    /*
     * Unable to fully structure code
     */
    private Plan planPlacement(Fight fight, class_1511 breaking, boolean mayLayBase) {
        crystal = this.planBase(fight, Step.CRYSTAL, breaking);
        if (!this.autoObsidian.get() || !mayLayBase) {
            return crystal;
        }
        eager = fight.player.field_6012 <= this.eagerUntil;
        v0 = crystalDamage = crystal == null ? 0.0f : this.enemyDamage(fight, crystal);
        if (crystal != null) {
            v1 = eager != false ? 11.0f : 8.0f;
            if (crystalDamage >= v1) {
                return crystal;
            }
        }
        if ((obsidian = this.planBase(fight, Step.OBSIDIAN, null)) == null || crystal == null) {
            return obsidian == null ? crystal : obsidian;
        }
        obsidianDamage = this.enemyDamage(fight, obsidian);
        v2 = eager != false ? 3.0f : 5.0f;
        if (!(obsidianDamage >= crystalDamage + v2)) {
            v4 = false;
        } else {
            v3 = eager != false ? 1.4f : 2.0f;
            v4 = obsidianDamage >= crystalDamage * v3;
        }
        upgrade = v4;
        return upgrade != false ? obsidian : crystal;
    }

    private float enemyDamage(Fight fight, Plan plan) {
        class_243 center = Crystals.explosionAt(plan.target());
        float most = 0.0f;
        for (CrystalDamage.Victim enemy : fight.side((Step)plan.step()).enemies) {
            most = Math.max(most, enemy.damage(center, plan.step() == Step.OBSIDIAN));
        }
        return most;
    }

    private Plan planBase(Fight fight, Step step, class_1511 breaking) {
        Candidate candidate;
        class_2680 anyBase;
        class_746 player = fight.player;
        class_638 level = fight.level;
        class_1792 item = step == Step.CRYSTAL ? class_1802.field_8301 : class_1802.field_8281;
        if (Crystals.route(player, item, anyBase = class_2246.field_10540.method_9564(), this.savedSlot) == -1) {
            return null;
        }
        if (step == Step.OBSIDIAN && Crystals.route(player, class_1802.field_8301, anyBase, this.savedSlot) == -1) {
            return null;
        }
        double placeReach = fight.placeReach;
        double breakable = player.method_55755() - 0.1;
        class_243 eye = fight.eye;
        class_2338 origin = class_2338.method_49638((class_2374)eye);
        int radius = class_3532.method_15384((double)placeReach);
        float cycle = step == Step.CRYSTAL && fight.lookBonus > 0.0f ? 2.0f : 0.0f;
        ArrayList<Candidate> candidates = new ArrayList<Candidate>();
        class_2338.class_2339 cell = new class_2338.class_2339();
        class_2338.class_2339 below = new class_2338.class_2339();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    float bound;
                    class_238 crystal;
                    cell.method_25504((class_2382)origin, dx, dy, dz);
                    if (cell.method_10264() + 1 < this.tickFloor) continue;
                    class_2680 state = level.method_8320((class_2338)cell);
                    if (step == Step.CRYSTAL ? !Crystals.isBase(state) : !state.method_45474()) continue;
                    below.method_25505((class_2382)cell, class_2350.field_11033);
                    if (step == Step.OBSIDIAN && !level.method_8320((class_2338)below).method_26206((class_1922)level, (class_2338)below, class_2350.field_11036) || (step == Step.CRYSTAL ? Crystals.placing(player, (class_2338)cell, fight.window) : cell.equals((Object)this.pendingObsidian) && this.pendingObsidianAge < fight.window)) continue;
                    class_2338 base = cell.method_10062();
                    class_2338 clicked = step == Step.CRYSTAL ? base : below.method_10062();
                    class_243 aim = CrystalAuraModule.aimOnTop(level, clicked);
                    if (eye.method_1025(aim) > placeReach * placeReach || eye.method_1025(TargetingUtils.closestPoint(eye, crystal = Crystals.boxOn(base))) > breakable * breakable || this.protectLoot.get() && LootGuard.near(Crystals.explosionAt(base)) || (bound = this.bound(fight, step, Crystals.explosionAt(base))) == Float.NEGATIVE_INFINITY) continue;
                    candidates.add(new Candidate(new Plan(step, null, clicked, base, aim, -2), bound));
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(Candidate::bound).reversed());
        Plan best = null;
        float bestScore = Float.NEGATIVE_INFINITY;
        Iterator dz = candidates.iterator();
        while (dz.hasNext() && !((candidate = (Candidate)dz.next()).bound() + fight.lookBonus + cycle <= bestScore)) {
            Plan plan;
            Plan found = candidate.plan();
            float score = this.score(fight, step, Crystals.explosionAt(found.target()), bestScore - fight.lookBonus - cycle, false);
            if (score == Float.NEGATIVE_INFINITY || !level.method_8621().method_11952(found.clicked()) || !this.canHostSoon(fight, found.target(), (class_1297)(step == Step.CRYSTAL ? breaking : null)) || (plan = this.reachable(fight, found)) == null || !fight.inView(plan)) continue;
            score += fight.lookCredit(plan);
            if (cycle > 0.0f && CrystalAuraModule.oneLook(fight.aimEye, Crystals.boxOn(plan.target()), plan.aim(), breakable)) {
                score += cycle;
            }
            if (score <= bestScore) continue;
            best = plan;
            bestScore = score;
        }
        if (best == null) {
            return null;
        }
        int route = Crystals.route(player, item, level.method_8320(best.clicked()), this.savedSlot);
        return route == -1 ? null : best.withRoute(route);
    }

    private Plan reachable(Fight fight, Plan plan) {
        if (fight.fixedLook == null) {
            class_2338 clicked = plan.clicked();
            double top = plan.aim().field_1351;
            class_243 near = fight.towardLook(new class_238((double)clicked.method_10263() + 0.1, top, (double)clicked.method_10260() + 0.1, (double)clicked.method_10263() + 1.0 - 0.1, top, (double)clicked.method_10260() + 1.0 - 0.1));
            Plan turned = new Plan(plan.step(), null, clicked, plan.target(), near, plan.route());
            if (fight.eye.method_1025(near) <= fight.placeReach * fight.placeReach && CrystalAuraModule.hitAlong(fight.player, turned, fight.aimEye, fight.lookAt(near)) != null) {
                return turned;
            }
        }
        if (CrystalAuraModule.hitAlong(fight.player, plan, fight.aimEye, fight.lookAt(plan.aim())) != null) {
            return plan;
        }
        if (plan.step() != Step.CRYSTAL || fight.fixedLook != null) {
            return null;
        }
        class_243 middle = class_243.method_24953((class_2382)plan.clicked());
        for (class_2350 side : class_2350.class_2353.field_11062) {
            Plan aimed;
            class_243 face = middle.method_1031((double)side.method_10148() * 0.4, 0.0, (double)side.method_10165() * 0.4);
            if ((fight.aimEye.field_1352 - face.field_1352) * (double)side.method_10148() + (fight.aimEye.field_1350 - face.field_1350) * (double)side.method_10165() <= 0.0 || CrystalAuraModule.hitAlong(fight.player, aimed = new Plan(plan.step(), null, plan.clicked(), plan.target(), face, plan.route()), fight.aimEye, fight.lookAt(face)) == null) continue;
            return aimed;
        }
        return null;
    }

    private static class_243 aimOnTop(class_638 level, class_2338 pos) {
        double top = level.method_8320(pos).method_26218((class_1922)level, pos).method_1105(class_2350.class_2351.field_11052);
        return new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + top - 0.1, (double)pos.method_10260() + 0.5);
    }

    private float need(class_1309 target, boolean breaking, boolean forced) {
        float damage = forced || this.facePlace.get() && CrystalAuraModule.facePlaced(target) ? Math.min(2.0f, this.tickMin) : this.tickMin;
        return breaking ? damage * 0.6f : damage;
    }

    private static boolean facePlaced(class_1309 target) {
        return CrystalDamage.health(target) <= 10.0f || Combo.inHole(target) || CrystalAuraModule.armorLow(target);
    }

    private static boolean armorLow(class_1309 target) {
        for (class_1304 slot : class_1304.field_54086) {
            class_1799 stack;
            if (!slot.method_46643() || !(stack = target.method_6118(slot)).method_7963() || !((float)(stack.method_7936() - stack.method_7919()) <= (float)stack.method_7936() * 0.1f)) continue;
            return true;
        }
        return false;
    }

    private float bound(Fight fight, Step step, class_243 center) {
        float best = Float.NEGATIVE_INFINITY;
        for (CrystalDamage.Victim enemy : fight.side((Step)step).enemies) {
            class_1309 entity = enemy.entity();
            float upper = enemy.upperBound(center);
            if (!Combo.kills(upper, entity)) {
                if (upper < this.need(entity, step == Step.BREAK, fight.forced)) continue;
            }
            best = Math.max(best, Combo.value(upper, entity));
        }
        return best;
    }

    private float score(Fight fight, Step step, class_243 center, float toBeat, boolean opening) {
        Side side = fight.side(step);
        boolean baseToCome = step == Step.OBSIDIAN;
        float selfDamage = side.self.damage(center, baseToCome);
        if (selfDamage > this.tickSelf || CrystalDamage.endangers((class_1657)fight.player, selfDamage)) {
            return Float.NEGATIVE_INFINITY;
        }
        for (CrystalDamage.Victim friend : side.friends) {
            float damage = friend.damage(center, baseToCome);
            if (!(damage > this.tickSelf) && !CrystalDamage.endangers((class_1657)friend.entity(), damage)) continue;
            return Float.NEGATIVE_INFINITY;
        }
        float best = Float.NEGATIVE_INFINITY;
        for (CrystalDamage.Victim enemy : side.enemies) {
            class_1309 entity = enemy.entity();
            float effective = CrystalAuraModule.effective(fight, enemy, step, center, opening);
            if (!Combo.kills(effective, entity)) {
                if (effective < this.need(entity, step == Step.BREAK, fight.forced)) continue;
            }
            best = Math.max(best, Combo.value(effective, entity) - 0.5f * selfDamage);
        }
        return best > toBeat ? best : Float.NEGATIVE_INFINITY;
    }

    private static boolean kills(Fight fight, Step step, class_243 center, boolean opening) {
        for (CrystalDamage.Victim enemy : fight.side((Step)step).enemies) {
            if (!Combo.kills(CrystalAuraModule.effective(fight, enemy, step, center, opening), enemy.entity())) continue;
            return true;
        }
        return false;
    }

    private static float effective(Fight fight, CrystalDamage.Victim enemy, Step step, class_243 center, boolean opening) {
        int ahead = step == Step.BREAK ? (opening ? HurtClock.opensIn(enemy.entity(), fight.rtt) : 0) : fight.rtt;
        return HurtClock.effective(enemy, enemy.raw(center, step == Step.OBSIDIAN), ahead, fight.rtt);
    }

    private static class_239 hitAlong(class_746 player, Plan plan, class_243 eye, Rotation look) {
        if (look == null) {
            return null;
        }
        class_243 direction = class_243.method_1030((float)look.pitch(), (float)look.yaw());
        if (plan.step() == Step.BREAK) {
            class_238 box = plan.crystal().method_5829();
            if (box.method_1006(eye)) {
                return new class_3966((class_1297)plan.crystal(), eye);
            }
            class_243 at = box.method_992(eye, eye.method_1019(direction.method_1021(player.method_55755()))).orElse(null);
            return at != null && MeleeAttacks.lineIsClear(player, eye, at) ? new class_3966((class_1297)plan.crystal(), at) : null;
        }
        class_638 level = (class_638)player.method_73183();
        class_2680 clicked = level.method_8320(plan.clicked());
        class_3965 hit = clicked.method_26218((class_1922)level, plan.clicked()).method_1092(eye, eye.method_1019(direction.method_1021(player.method_55754())), plan.clicked());
        if (hit == null || !MeleeAttacks.lineIsClear(player, eye, hit.method_17784())) {
            return null;
        }
        if (plan.step() == Step.OBSIDIAN && !plan.target().equals((Object)Crystals.placedObsidian(level, player, hit, clicked))) {
            return null;
        }
        return hit;
    }

    private boolean perform(class_310 mc, class_746 player, Fight fight, Plan plan, Look look) {
        boolean acted;
        class_239 hit;
        class_243 eye = player.method_33571();
        Rotation sent = ServerRotation.sent();
        Rotation next = look == Look.SILENT ? sent : RotationUtils.entityRotation((class_1297)player);
        class_239 class_2392 = hit = sent == null ? null : CrystalAuraModule.hitAlong(player, plan, eye, sent);
        if (hit != null && next != sent && CrystalAuraModule.hitAlong(player, plan, eye, next) == null) {
            hit = null;
        }
        if (plan.route() != -2) {
            this.select(player, plan.route());
        }
        boolean bl = hit != null && this.due(plan, 0) && (plan.step() == Step.BREAK ? this.strike(mc, player, fight, plan.crystal()) : this.place(mc, player, plan, (class_3965)hit)) ? true : (acted = false);
        if (acted) {
            MeleeAttacks.claimTick(mc);
        } else if (look == Look.SILENT) {
            this.turn(hit != null ? sent : fight.lookAt(plan.aim()));
        }
        return acted;
    }

    private boolean fire(class_310 mc, class_746 player, Fight fight, AnchorAuraModule aura, Combo.Shot shot) {
        if (shot.route() != -2) {
            this.select(player, shot.route());
        }
        if (!aura.ready(mc, player, shot)) {
            this.turn(fight.lookAt(shot.aim()));
            return false;
        }
        aura.fire(mc, player, shot);
        return true;
    }

    private boolean strike(class_310 mc, class_746 player, Fight fight, class_1511 crystal) {
        if (this.mainAge < 2 && !Crystals.enchantsAddDamage(player.method_6047()) && !Crystals.dealsDamage((class_1309)player, class_1799.field_8037)) {
            return false;
        }
        if (!Crystals.strike(mc, player, (class_1297)crystal)) {
            return false;
        }
        this.markBreak(crystal);
        this.breakAttempts.addTo(crystal.method_5628(), 1);
        this.ticksSinceBreak = 0;
        this.breakGap = this.humanize.get() && !this.fullSpeed ? Crystals.humanGap() : 0;
        for (CrystalDamage.Victim enemy : fight.side((Step)Step.BREAK).enemies) {
            float raw = enemy.raw(crystal.method_73189(), false);
            if (!(raw > 0.0f)) continue;
            HurtClock.shot(player, enemy.entity(), raw, crystal.method_5628(), fight.rtt);
        }
        class_238 blast = crystal.method_5829().method_1014(12.0);
        for (class_1511 other : fight.level.method_18467(class_1511.class, blast)) {
            if (other == crystal || !(other.method_5858((class_1297)crystal) <= 144.0)) continue;
            this.doomed.put(other.method_5628(), player.field_6012);
        }
        return true;
    }

    private boolean place(class_310 mc, class_746 player, Plan plan, class_3965 hit) {
        if (!Crystals.rightClick(mc, player, hit)) {
            return false;
        }
        if (plan.step() == Step.CRYSTAL) {
            Crystals.notePlace(player, plan.target());
        } else {
            this.pendingObsidian = plan.target();
            this.pendingObsidianAge = 0;
        }
        this.ticksSincePlace = 0;
        this.placeGap = this.humanize.get() && !this.fullSpeed ? Crystals.humanGap() : 0;
        return true;
    }

    private void turn(Rotation look) {
        ServerRotation.request(look, this.fullSpeed ? this.turnSpeed.max() : this.turnSpeed.get());
    }

    private static boolean sameLook(Rotation a, Rotation b) {
        return a != null && Math.abs(RotationUtils.yawDelta(a.yaw(), b.yaw())) <= 2.0f && Math.abs(a.pitch() - b.pitch()) <= 2.0f;
    }

    private boolean due(Plan plan, int ahead) {
        return plan.step() == Step.BREAK ? this.ticksSinceBreak + ahead > Math.max(this.breakDelay.getInt(), this.breakGap - 1) : this.ticksSincePlace + ahead > Math.max(this.placeDelay.getInt(), this.placeGap - 1);
    }

    private void select(class_746 player, int slot) {
        class_1661 inventory = player.method_31548();
        if (this.savedSlot == -1) {
            this.savedSlot = inventory.method_67532();
        }
        inventory.method_61496(slot);
        this.ownSlot = slot;
        this.lastMain = player.method_6047();
        this.mainAge = 0;
    }

    private void markBreak(class_1511 crystal) {
        this.pendingBreakId = crystal.method_5628();
        this.pendingBreakAge = 0;
    }

    private void ageCounters() {
        this.ticksSinceBreak = Math.min(this.ticksSinceBreak + 1, 1000);
        this.ticksSincePlace = Math.min(this.ticksSincePlace + 1, 1000);
        this.mainAge = Math.min(this.mainAge + 1, 1000);
        this.pendingBreakAge = Math.min(this.pendingBreakAge + 1, 1000);
        this.pendingObsidianAge = Math.min(this.pendingObsidianAge + 1, 1000);
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Look {
        SILENT,
        CROSSHAIR;

    }

    @Environment(value=EnvType.CLIENT)
    private final class Fight {
        final class_746 player;
        final class_638 level;
        final class_243 eye;
        final class_243 aimEye;
        final List<class_1309> enemies;
        final List<class_1309> friends;
        final int window;
        final int rtt;
        final Rotation fixedLook;
        final Rotation serverLook;
        final float lookBonus;
        final int arriveLead;
        private final int placeLead;
        private final int selfPlaceLead;
        final boolean forced;
        final double placeReach;
        private Side breakSide;
        private Side placeSide;

        Fight(class_746 player, class_638 level, List<class_1309> enemies, List<class_1309> friends, int window, int rtt, Rotation fixedLook, float lookBonus, boolean forced) {
            this.player = player;
            this.level = level;
            this.eye = player.method_33571();
            this.aimEye = fixedLook != null ? this.eye : this.eye.method_1019(player.method_18798());
            this.enemies = enemies;
            this.friends = friends;
            this.window = window;
            this.rtt = rtt;
            this.fixedLook = fixedLook;
            this.serverLook = ServerRotation.sent();
            this.lookBonus = lookBonus;
            this.arriveLead = Math.min(rtt, 5);
            this.placeLead = Math.min(2 * rtt, 5);
            this.selfPlaceLead = Math.min(rtt, 5);
            this.forced = forced;
            this.placeReach = player.method_55754() - 0.1;
        }

        Side side(Step step) {
            if (step == Step.BREAK) {
                if (this.breakSide == null) {
                    this.breakSide = new Side(this.player, this.enemies, this.friends, this.arriveLead, 0);
                }
                return this.breakSide;
            }
            if (this.placeSide == null) {
                this.placeSide = new Side(this.player, this.enemies, this.friends, this.placeLead, this.selfPlaceLead);
            }
            return this.placeSide;
        }

        Rotation lookAt(class_243 aim) {
            return this.fixedLook != null ? this.fixedLook : RotationUtils.fromTo(this.aimEye, aim);
        }

        boolean holds(Plan plan) {
            return CrystalAuraModule.hitAlong(this.player, plan, this.eye, this.serverLook) != null;
        }

        float lookCredit(Plan plan) {
            return this.lookBonus > 0.0f && this.holds(plan) ? this.lookBonus : 0.0f;
        }

        boolean inView(Plan plan) {
            if (this.fixedLook != null || CrystalAuraModule.this.viewAngle.get() >= 180.0f) {
                return true;
            }
            float off = Math.abs(RotationUtils.yawDelta(this.player.method_36454(), RotationUtils.fromTo(this.aimEye, plan.aim()).yaw()));
            return off <= CrystalAuraModule.this.viewAngle.get() || off <= CrystalAuraModule.this.viewAngle.get() + 20.0f && this.holds(plan);
        }

        class_243 towardLook(class_238 box) {
            double along;
            Rotation from = this.serverLook != null ? this.serverLook : RotationUtils.entityRotation((class_1297)this.player);
            class_243 direction = class_243.method_1030((float)from.pitch(), (float)from.yaw());
            class_243 center = box.method_1005();
            boolean flat = box.method_17940() < 1.0E-6 && direction.field_1351 < -1.0E-6 && this.aimEye.field_1351 > box.field_1322;
            double d = along = flat ? (box.field_1322 - this.aimEye.field_1351) / direction.field_1351 : center.method_1020(this.aimEye).method_1026(direction);
            if (along <= 0.0) {
                return center;
            }
            class_243 on = this.aimEye.method_1019(direction.method_1021(along));
            return new class_243(class_3532.method_15350((double)on.field_1352, (double)box.field_1323, (double)box.field_1320), class_3532.method_15350((double)on.field_1351, (double)box.field_1322, (double)box.field_1325), class_3532.method_15350((double)on.field_1350, (double)box.field_1321, (double)box.field_1324));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Plan(Step step, class_1511 crystal, class_2338 clicked, class_2338 target, class_243 aim, int route) {
        Plan withRoute(int newRoute) {
            return new Plan(this.step, this.crystal, this.clicked, this.target, this.aim, newRoute);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Step {
        BREAK,
        CRYSTAL,
        OBSIDIAN;

    }

    @Environment(value=EnvType.CLIENT)
    private static final class Side {
        final List<CrystalDamage.Victim> enemies = new ArrayList<CrystalDamage.Victim>();
        final List<CrystalDamage.Victim> friends = new ArrayList<CrystalDamage.Victim>();
        final CrystalDamage.Victim self;

        Side(class_746 player, List<class_1309> targets, List<class_1309> friends, int lead, int selfLead) {
            for (class_1309 target : targets) {
                this.enemies.add(new CrystalDamage.Victim(target, Combo.lead(target, lead)));
            }
            for (class_1309 friend : friends) {
                this.friends.add(new CrystalDamage.Victim(friend, Combo.lead(friend, lead)));
            }
            this.self = new CrystalDamage.Victim((class_1309)player, Combo.lead((class_1309)player, selfLead));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Candidate(Plan plan, float bound) {
    }
}

