/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_2680
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PlayerAttackEntityEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.crystal.CrystalDamage;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.crystal.LootLatch;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.impl.misc.AutoEatModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class CrystalMacroModule
extends ClientModule {
    private static final int COUNTER_CAP = 1000;
    private static final int WEAPON_SETTLE_TICKS = 2;
    private static final int MAX_BREAK_ATTEMPTS = 3;
    private static final int GHOST_REST_TICKS = 100;
    private final NumberSetting breakDelay = this.addSetting("break-delay", new NumberSetting("Break Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting placeDelay = this.addSetting("place-delay", new NumberSetting("Place Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final BooleanSetting autoObsidian = this.addSetting("auto-obsidian", new BooleanSetting("Auto Obsidian", true));
    private final NumberSetting obsidianDelay = this.addSetting("obsidian-delay", new NumberSetting("Obsidian Delay", 2.0f, 0.0f, 10.0f).step(1.0f));
    private final BooleanSetting switchBack = this.addSetting("switch-back", new BooleanSetting("Switch Back", true));
    private final NumberSetting maxSelfDamage = this.addSetting("max-self-damage", new NumberSetting("Max Self Damage", 8.0f, 0.0f, 20.0f).step(0.5f));
    private final BooleanSetting humanize = this.addSetting("humanize", new BooleanSetting("Human Pace", true));
    private boolean engaged;
    private int savedSlot = -1;
    private int ownSlot = -1;
    private int ticksSinceAction = 1000;
    private int actionGap;
    private final Int2IntOpenHashMap breakAttempts = new Int2IntOpenHashMap();
    private final Int2IntOpenHashMap restUntil = new Int2IntOpenHashMap();
    private class_1799 lastMain;
    private int mainAge = 1000;
    private int pendingBreakId = -1;
    private int pendingBreakAge = 1000;
    private class_2338 pendingCrystalBase;
    private int pendingCrystalAge = 1000;
    private class_2338 pendingObsidian;
    private int pendingObsidianAge = 1000;

    public CrystalMacroModule() {
        super("crystal-macro", "Crystal Macro", "Breaks and places crystals while you hold right-click.", ModuleCategory.COMBAT);
        this.obsidianDelay.visibleWhen(this.autoObsidian::get);
    }

    @Override
    public int settingsVersion() {
        return 1;
    }

    @Override
    public void migrateSettings(int savedVersion) {
        if (savedVersion < 1) {
            this.breakDelay.set(this.breakDelay.get() - 1.0f);
            this.placeDelay.set(this.placeDelay.get() - 1.0f);
            this.obsidianDelay.set(this.obsidianDelay.get() - 1.0f);
        }
    }

    @Override
    protected void onEnable() {
        this.resetState();
    }

    @Override
    protected void onDisable() {
        class_310 mc = class_310.method_1551();
        this.endHold(mc);
        class_746 player = mc.field_1724;
        if (player != null && !CrystalMacroModule.hotbarTakenByOther()) {
            this.restoreSlot(player);
        }
        this.resetState();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        this.ageCounters();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null || player.method_29504()) {
            this.endHold(mc);
            this.forgetSlots();
            return;
        }
        class_1799 main = player.method_6047();
        if (main != this.lastMain) {
            this.lastMain = main;
            this.mainAge = 0;
        }
        this.forgetGone(mc.field_1687);
        LootLatch.update(mc, player);
        if (LootLatch.latched()) {
            this.endHold(mc);
            if (!player.method_6115() && !AutoEatModule.isEating() && (this.ownsSelection(player) || CrystalMacroModule.activationAllows(player))) {
                CrystalMacroModule.suppressVanillaUse(mc);
            }
            return;
        }
        if (!this.holdActive(mc, player)) {
            this.endHold(mc);
            if (mc.field_1755 == null && !CrystalMacroModule.hotbarTakenByOther() && !MeleeAttacks.actedThisTick((class_1657)player)) {
                this.restoreSlot(player);
            }
            return;
        }
        if (AutoInvTotemModule.wantsHotbar() || CombatMacroLock.heldByOther(this)) {
            CombatMacroLock.release(this);
            if (this.engaged) {
                CrystalMacroModule.suppressVanillaUse(mc);
            }
            return;
        }
        if (this.engaged) {
            if (!this.keepEngaged(player)) {
                this.endHold(mc);
                return;
            }
        } else if (player.method_6115() || !this.ownsSelection(player) && !CrystalMacroModule.activationAllows(player)) {
            return;
        }
        Plan plan = this.plan(mc, player);
        if (!this.engaged && plan == null && !this.ownsSelection(player)) {
            return;
        }
        this.engaged = true;
        CombatMacroLock.tryAcquire(this);
        CrystalMacroModule.suppressVanillaUse(mc);
        if (plan == null || !MeleeAttacks.handsFree(mc, player) || MeleeAttacks.actedThisTick((class_1657)player) || ServerRotation.pending() || ServerRotation.desynced()) {
            return;
        }
        switch (plan.step().ordinal()) {
            case 0: {
                this.breakCrystal(mc, player, plan);
                break;
            }
            case 1: {
                if (!this.useOnCrosshair(mc, player, this.placeDelay, plan)) break;
                this.pendingCrystalBase = plan.target();
                this.pendingCrystalAge = 0;
                break;
            }
            case 2: {
                if (!this.useOnCrosshair(mc, player, this.obsidianDelay, plan)) break;
                this.pendingObsidian = plan.target();
                this.pendingObsidianAge = 0;
            }
        }
    }

    @EventHandler
    public void onAttack(PlayerAttackEntityEvent event) {
        class_1511 crystal;
        class_1297 class_12972 = event.target;
        if (class_12972 instanceof class_1511 && (crystal = (class_1511)class_12972).method_73183().method_8608()) {
            this.markBreak(crystal);
        }
    }

    private boolean holdActive(class_310 mc, class_746 player) {
        return MeleeAttacks.canAct(mc) && mc.method_1569() && !player.method_7325() && Crystals.isPhysicallyDown(mc, mc.field_1690.field_1904);
    }

    private static boolean hotbarTakenByOther() {
        return AutoEatModule.isEating() || AutoInvTotemModule.wantsHotbar();
    }

    private boolean keepEngaged(class_746 player) {
        if (this.ownsSelection(player)) {
            return true;
        }
        this.forgetSlots();
        return CrystalMacroModule.activationAllows(player);
    }

    private boolean ownsSelection(class_746 player) {
        return this.ownSlot != -1 && player.method_31548().method_67532() == this.ownSlot;
    }

    private static boolean activationAllows(class_746 player) {
        class_1799 main = player.method_6047();
        class_1799 offhand = player.method_6079();
        if (main.method_31574(class_1802.field_8301) || offhand.method_31574(class_1802.field_8301) && Crystals.isCombatItem(main)) {
            return true;
        }
        return !(!main.method_31573(class_3489.field_42611) && !main.method_31573(class_3489.field_42612) && !main.method_31574(class_1802.field_49814) || !offhand.method_7960() && !offhand.method_31574(class_1802.field_8288) || Crystals.hotbarSlotOf(player.method_31548(), class_1802.field_8301) == -1);
    }

    private void endHold(class_310 mc) {
        CombatMacroLock.release(this);
        if (!this.engaged) {
            return;
        }
        this.engaged = false;
        mc.field_1690.field_1904.method_23481(mc.field_1755 == null && Crystals.isPhysicallyDown(mc, mc.field_1690.field_1904));
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
        this.forgetSlots();
        this.ticksSinceAction = 1000;
        this.actionGap = 0;
        this.breakAttempts.clear();
        this.restUntil.clear();
        this.lastMain = null;
        this.mainAge = 1000;
        this.pendingBreakId = -1;
        this.pendingBreakAge = 1000;
        this.pendingCrystalBase = null;
        this.pendingCrystalAge = 1000;
        this.pendingObsidian = null;
        this.pendingObsidianAge = 1000;
        CombatMacroLock.release(this);
    }

    private static void suppressVanillaUse(class_310 mc) {
        class_304 use = mc.field_1690.field_1904;
        use.method_23481(false);
        while (use.method_1436()) {
        }
    }

    private Plan plan(class_310 mc, class_746 player) {
        class_3965 blockHit;
        class_239 hit = player.method_76762(1.0f, (class_1297)player);
        int window = Crystals.confirmWindow(mc, player);
        if (this.pendingCrystalBase != null && Crystals.hasCrystal((class_1937)mc.field_1687, this.pendingCrystalBase, null)) {
            this.pendingCrystalBase = null;
        }
        if (hit instanceof class_3966) {
            class_1511 crystal;
            class_3966 entityHit = (class_3966)hit;
            class_1297 class_12972 = entityHit.method_17782();
            if (!(class_12972 instanceof class_1511) || !(crystal = (class_1511)class_12972).method_5805()) {
                return null;
            }
            if (crystal.method_5628() == this.pendingBreakId && this.pendingBreakAge < window) {
                return null;
            }
            if (this.restUntil.get(crystal.method_5628()) > player.field_6012) {
                return null;
            }
            float selfDamage = CrystalDamage.estimate(crystal.method_73189(), (class_1309)player);
            if (selfDamage > this.maxSelfDamage.get() || CrystalDamage.endangers((class_1657)player, selfDamage)) {
                return null;
            }
            int weapon = Crystals.weaponRoute(player);
            return weapon == -1 ? null : Plan.forBreak(crystal, weapon);
        }
        if (!(hit instanceof class_3965) || (blockHit = (class_3965)hit).method_17783() != class_239.class_240.field_1332) {
            return null;
        }
        class_638 level = mc.field_1687;
        class_2338 pos = blockHit.method_17777();
        if (!level.method_8621().method_11952(pos)) {
            return null;
        }
        class_2680 state = level.method_8320(pos);
        if (Crystals.isBase(state)) {
            if (pos.equals((Object)this.pendingCrystalBase) && this.pendingCrystalAge < window) {
                return null;
            }
            if (!Crystals.canHost((class_1937)level, pos, null)) {
                return null;
            }
            int route = Crystals.route(player, class_1802.field_8301, state, this.savedSlot);
            return route == -1 ? null : Plan.forPlace(Step.CRYSTAL, blockHit, pos, route);
        }
        if (!this.autoObsidian.get()) {
            return null;
        }
        if (Crystals.route(player, class_1802.field_8301, class_2246.field_10540.method_9564(), this.savedSlot) == -1) {
            return null;
        }
        int route = Crystals.route(player, class_1802.field_8281, state, this.savedSlot);
        if (route == -1) {
            return null;
        }
        class_2338 target = Crystals.obsidianTarget(level, player, blockHit, state);
        if (target == null || target.equals((Object)this.pendingObsidian) && this.pendingObsidianAge < window) {
            return null;
        }
        return Plan.forPlace(Step.OBSIDIAN, blockHit, target, route);
    }

    private void breakCrystal(class_310 mc, class_746 player, Plan plan) {
        if (!this.due(this.breakDelay)) {
            return;
        }
        if (plan.route() != -2) {
            this.select(player, plan.route());
        }
        if (this.mainAge < 2 && !Crystals.enchantsAddDamage(player.method_6047()) && !Crystals.dealsDamage((class_1309)player, class_1799.field_8037)) {
            return;
        }
        if (!Crystals.strike(mc, player, (class_1297)plan.crystal())) {
            return;
        }
        MeleeAttacks.claimTick(mc);
        this.markBreak(plan.crystal());
        int id = plan.crystal().method_5628();
        if (this.breakAttempts.addTo(id, 1) + 1 >= 3) {
            this.breakAttempts.remove(id);
            this.restUntil.put(id, player.field_6012 + 100);
        }
        this.acted();
    }

    private boolean useOnCrosshair(class_310 mc, class_746 player, NumberSetting delay, Plan plan) {
        if (!this.due(delay)) {
            return false;
        }
        if (plan.route() != -2) {
            this.select(player, plan.route());
        }
        if (!Crystals.rightClick(mc, player, plan.blockHit())) {
            return false;
        }
        MeleeAttacks.claimTick(mc);
        this.acted();
        return true;
    }

    private boolean due(NumberSetting delay) {
        return this.ticksSinceAction > Math.max(delay.getInt(), this.actionGap - 1);
    }

    private void acted() {
        this.ticksSinceAction = 0;
        this.actionGap = this.humanize.get() ? Crystals.humanGap() : 0;
    }

    private void forgetGone(class_638 level) {
        this.breakAttempts.keySet().removeIf(id -> level.method_8469(id) == null);
        this.restUntil.keySet().removeIf(id -> level.method_8469(id) == null);
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
        this.ticksSinceAction = Math.min(this.ticksSinceAction + 1, 1000);
        this.mainAge = Math.min(this.mainAge + 1, 1000);
        this.pendingBreakAge = Math.min(this.pendingBreakAge + 1, 1000);
        this.pendingCrystalAge = Math.min(this.pendingCrystalAge + 1, 1000);
        this.pendingObsidianAge = Math.min(this.pendingObsidianAge + 1, 1000);
    }

    @Environment(value=EnvType.CLIENT)
    private record Plan(Step step, class_1511 crystal, class_3965 blockHit, class_2338 target, int route) {
        static Plan forBreak(class_1511 crystal, int weapon) {
            return new Plan(Step.BREAK, crystal, null, null, weapon);
        }

        static Plan forPlace(Step step, class_3965 blockHit, class_2338 target, int route) {
            return new Plan(step, null, blockHit, target, route);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Step {
        BREAK,
        CRYSTAL,
        OBSIDIAN;

    }
}

