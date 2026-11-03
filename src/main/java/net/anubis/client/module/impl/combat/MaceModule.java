/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1887
 *  net.minecraft.class_1893
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3966
 *  net.minecraft.class_5134
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 *  net.minecraft.class_746
 *  net.minecraft.class_9362
 */
package net.anubis.client.module.impl.combat;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.ShieldBreakerModule;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.impl.combat.melee.SlotReturn;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.InputControl;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_5134;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9362;

@Environment(value=EnvType.CLIENT)
public final class MaceModule
extends ClientModule {
    private static final String PICK_DAMAGE = "Damage";
    private static final String PICK_WIND_BURST = "Wind Burst";
    private static final String SMASH_DIRECT = "Direct";
    private static final String SMASH_DELAYED = "Delayed";
    private static final double REACH_MARGIN = 0.35;
    private static final float BREACH_GAIN = 1.15f;
    private final BooleanSetting autoSwap = this.addSetting("auto-swap", new BooleanSetting("Auto Swap", true));
    private final ModeSetting pick = this.addSetting("pick", new ModeSetting("Pick", "Damage", "Damage", "Wind Burst"));
    private final BooleanSetting breachSwap = this.addSetting("breach-swap", new BooleanSetting("Breach Swap", false));
    private final BooleanSetting swapBack = this.addSetting("swap-back", new BooleanSetting("Swap Back", true));
    private final BooleanSetting smashAssist = this.addSetting("smash-assist", new BooleanSetting("Smash Assist", false));
    private final ModeSetting smashTiming = this.addSetting("smash-timing", new ModeSetting("Smash Timing", "Delayed", "Direct", "Delayed"));
    private final NumberSetting minFall = this.addSetting("min-fall", new NumberSetting("Min Fall", 3.0f, 1.5f, 30.0f).step(0.5f));
    private final BooleanSetting mobs = this.addSetting("mobs", new BooleanSetting("Mobs", false));
    private final SlotReturn slots = new SlotReturn(this);
    private boolean smashed;

    public MaceModule() {
        super("mace", "Mace", "Swaps to the strongest mace for smash hits, with an optional smash assist.", ModuleCategory.COMBAT);
        this.pick.visibleWhen(this.autoSwap::get);
        this.breachSwap.visibleWhen(this.autoSwap::get);
        this.swapBack.visibleWhen(() -> this.autoSwap.get() || this.smashAssist.get());
        this.smashTiming.visibleWhen(this.smashAssist::get);
        this.minFall.visibleWhen(this.smashAssist::get);
        this.mobs.visibleWhen(this.smashAssist::get);
    }

    @Override
    protected void onDisable() {
        this.smashed = false;
        this.slots.drop(class_310.method_1551(), this.swapBack.get());
    }

    public void beforeAttack(class_746 player, class_1297 target) {
        if (!this.autoSwap.get() || !(target instanceof class_1309)) {
            return;
        }
        class_1309 living = (class_1309)target;
        if (MeleeAttacks.shieldBlocks(living, (class_1657)player) && !MeleeAttacks.shieldBroken(living)) {
            return;
        }
        boolean smash = class_9362.method_58659((class_1309)player);
        if (!smash && !this.breachSwap.get()) {
            return;
        }
        int slot = this.bestMace(player, living, smash);
        if (slot < 0) {
            return;
        }
        if (!smash && MaceModule.expected(player, player.method_31548().method_5438(slot), living) < MaceModule.expected(player, player.method_6047(), living) * 1.15f) {
            return;
        }
        this.slots.take(player, slot);
    }

    private int bestMace(class_746 player, class_1309 target, boolean smash) {
        boolean windBurst = smash && this.pick.is(PICK_WIND_BURST);
        class_1661 inventory = player.method_31548();
        int best = -1;
        boolean bestBurst = false;
        float bestDamage = 0.0f;
        for (int slot = 0; slot < class_1661.method_7368(); ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (!stack.method_31574(class_1802.field_49814) || stack.method_63692() || !smash && MaceModule.level(stack, (class_5321<class_1887>)class_1893.field_50158) == 0) continue;
            boolean burst = windBurst && MaceModule.level(stack, (class_5321<class_1887>)class_1893.field_50159) > 0;
            float damage = MaceModule.expected(player, stack, target);
            if (best >= 0 && (bestBurst && !burst || bestBurst == burst && damage <= bestDamage)) continue;
            best = slot;
            bestBurst = burst;
            bestDamage = damage;
        }
        return best;
    }

    private static float expected(class_746 player, class_1799 stack, class_1309 target) {
        int sharpness;
        float charge = MeleeAttacks.charge((class_1657)player);
        double fall = player.field_6017;
        float damage = (float)(1.0 + MeleeAttacks.attackDamage(player.method_6047())) * (0.2f + 0.8f * charge * charge);
        if (stack.method_31574(class_1802.field_49814) && class_9362.method_58659((class_1309)player)) {
            damage += (float)(MaceModule.smashBonus(fall) + 0.5 * (double)MaceModule.level(stack, (class_5321<class_1887>)class_1893.field_50157) * fall);
        }
        if (MeleeAttacks.wouldCrit((class_1657)player, (class_1297)target)) {
            damage *= 1.5f;
        }
        if ((sharpness = MaceModule.level(stack, (class_5321<class_1887>)class_1893.field_9118)) > 0) {
            damage += charge * (0.5f * (float)sharpness + 0.5f);
        }
        return MaceModule.afterArmor(target, damage, MaceModule.level(stack, (class_5321<class_1887>)class_1893.field_50158));
    }

    private static double smashBonus(double fall) {
        if (fall <= 3.0) {
            return 4.0 * fall;
        }
        return fall <= 8.0 ? 12.0 + 2.0 * (fall - 3.0) : 22.0 + fall - 8.0;
    }

    private static float afterArmor(class_1309 target, float damage, int breach) {
        float armor = target.method_6096();
        float toughness = (float)target.method_45325(class_5134.field_23725);
        float cut = class_3532.method_15363((float)(armor - damage / (2.0f + toughness / 4.0f)), (float)(armor * 0.2f), (float)20.0f) / 25.0f;
        return damage * (1.0f - class_3532.method_15363((float)(cut - 0.15f * (float)breach), (float)0.0f, (float)1.0f));
    }

    private static int level(class_1799 stack, class_5321<class_1887> enchantment) {
        for (Object2IntMap.Entry entry : stack.method_58657().method_57539()) {
            if (!((class_6880)entry.getKey()).method_40225(enchantment)) continue;
            return entry.getIntValue();
        }
        return 0;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null && mc.field_1724.method_24828()) {
            this.smashed = false;
        }
        this.slots.tick(mc, this.swapBack.get());
    }

    @EventHandler
    public void onSmashTick(TickEvent event) {
        if (this.smashAssist.get()) {
            this.trySmash(class_310.method_1551());
        }
    }

    private void trySmash(class_310 mc) {
        class_1309 target;
        class_1297 class_12972;
        if (!MeleeAttacks.canAct(mc) || CombatMacroLock.heldByOther(this) || AutoInvTotemModule.wantsHotbar()) {
            return;
        }
        class_746 player = mc.field_1724;
        if (MeleeAttacks.actedThisTick((class_1657)player) || MeleeAttacks.clickPending(mc) || MeleeAttacks.rightClickComing(mc) || ServerRotation.pending() || mc.field_1771 > 0 || !MeleeAttacks.handsFree(mc, player)) {
            return;
        }
        if (player.method_18798().field_1351 >= 0.0) {
            return;
        }
        class_3966 hit = MeleeAttacks.crosshairHit(mc);
        if (hit == null || !((class_12972 = hit.method_17782()) instanceof class_1309) || !MeleeAttacks.isEnemy(player, (class_1297)(target = (class_1309)class_12972))) {
            return;
        }
        if (!(target instanceof class_1657) && !this.mobs.get()) {
            return;
        }
        if (!this.smashReady(player, target)) {
            return;
        }
        if (MeleeAttacks.shieldBlocks(target, (class_1657)player) && !MeleeAttacks.shieldBroken(target)) {
            int axe = ShieldBreakerModule.bestAxe(player);
            if (MaceModule.canHoldOneTick(player, target) && axe >= 0 && (this.autoSwap.get() || this.swapBack.get()) && this.slots.take(player, axe)) {
                if (MeleeAttacks.attack(mc, hit)) {
                    MeleeAttacks.noteShieldBroken(target);
                    MeleeAttacks.claimTick(mc);
                } else {
                    this.slots.drop(mc, true);
                }
            }
            return;
        }
        if (this.holdsSmash(player, target)) {
            InputControl.dropSprint();
            mc.field_1771 = Math.max(mc.field_1771, 1);
            return;
        }
        if (MeleeAttacks.attack(mc, hit)) {
            this.smashed = true;
            MeleeAttacks.claimTick(mc);
        }
    }

    public void noteSmash() {
        this.smashed = true;
    }

    public boolean smashReady(class_746 player, class_1309 target) {
        return this.isEnabled() && !this.smashed && class_9362.method_58659((class_1309)player) && player.field_6017 >= (double)this.minFall.get() && (player.method_6047().method_31574(class_1802.field_49814) || this.autoSwap.get() && this.bestMace(player, target, true) >= 0);
    }

    public boolean holdsSmash(class_746 player, class_1309 target) {
        boolean chargeSoon = MeleeAttacks.charge((class_1657)player) <= 0.9f && player.method_7261(1.5f) > 0.9f;
        return MaceModule.canHoldOneTick(player, target) && (this.smashTiming.is(SMASH_DELAYED) || chargeSoon || target.field_6235 > 0 || player.method_5624());
    }

    private static boolean canHoldOneTick(class_746 player, class_1309 target) {
        class_243 motion = player.method_18798();
        if (!player.method_73183().method_8587((class_1297)player, player.method_5829().method_997(motion))) {
            return false;
        }
        double reach = player.method_55755() - 0.35;
        if (reach <= 0.0) {
            return false;
        }
        class_243 nextEye = player.method_33571().method_1019(motion);
        class_243 targetStep = target.method_73189().method_1023(target.field_6014, target.field_6036, target.field_5969);
        class_238 nextBox = target.method_5829().method_997(targetStep).method_1014((double)target.method_5871());
        if (nextBox.method_1006(nextEye)) {
            return true;
        }
        Optional aim = nextBox.method_992(nextEye, nextEye.method_1019(player.method_5828(1.0f).method_1021(reach)));
        return aim.isPresent() && MeleeAttacks.lineIsClear(player, nextEye, (class_243)aim.get());
    }
}

