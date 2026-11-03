/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10707
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3966
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.combat;

import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.impl.combat.melee.SlotReturn;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10707;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_640;
import net.minecraft.class_746;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class ShieldBreakerModule
extends ClientModule {
    private static final String MODE_PASSIVE = "Passive";
    private static final String MODE_AUTO = "Auto";
    private static final int REARM_TICKS = 10;
    private static final int MAX_LEAD_TICKS = 4;
    private static final int HOLD_TICKS = 2;
    private final ModeSetting mode = this.addSetting("mode", new ModeSetting("Mode", "Passive", "Passive", "Auto"));
    private final BooleanSetting switchBack = this.addSetting("switch-back", new BooleanSetting("Switch Back", true));
    private final SlotReturn slots = new SlotReturn(this);
    private int ticks;
    private int rearmTick;
    private int takenTick;

    public ShieldBreakerModule() {
        super("shield-breaker", "Shield Breaker", "Swaps to an axe for any hit into a raised shield. Auto also makes the hit.", ModuleCategory.COMBAT);
    }

    @Override
    protected void onEnable() {
        this.ticks = 0;
        this.rearmTick = 0;
    }

    @Override
    protected void onDisable() {
        this.slots.drop(class_310.method_1551(), this.switchBack.get());
    }

    public void beforeAttack(class_746 player, class_1297 target) {
        class_1657 other;
        block7: {
            block6: {
                if (!(target instanceof class_1657)) break block6;
                other = (class_1657)target;
                if (!MeleeAttacks.disablesShields(player.method_6047())) break block7;
            }
            return;
        }
        if (!ShieldBreakerModule.fightingHand(player)) {
            return;
        }
        if (MeleeAttacks.shieldBroken((class_1309)other) || !ShieldBreakerModule.blocksSoon(player, other)) {
            return;
        }
        int slot = ShieldBreakerModule.bestAxe(player);
        if (slot >= 0 && this.slots.take(player, slot)) {
            this.takenTick = this.ticks;
            MeleeAttacks.noteShieldBroken((class_1309)other);
        }
    }

    private static boolean fightingHand(class_746 player) {
        class_1799 main = player.method_6047();
        return main.method_7960() || MeleeAttacks.isMeleeWeapon(main);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onTick(TickEvent event) {
        ++this.ticks;
        if (this.ticks - this.takenTick >= 2) {
            this.slots.tick(class_310.method_1551(), this.switchBack.get());
        }
    }

    @EventHandler
    public void onAutoTick(TickEvent event) {
        if (this.mode.is(MODE_AUTO)) {
            this.tryHit(class_310.method_1551());
        }
    }

    private void tryHit(class_310 mc) {
        class_1657 target;
        class_1297 class_12972;
        if (this.ticks < this.rearmTick || !MeleeAttacks.canAct(mc)) {
            return;
        }
        if (CombatMacroLock.heldByOther(this) || AutoInvTotemModule.wantsHotbar()) {
            return;
        }
        class_746 player = mc.field_1724;
        if (MeleeAttacks.actedThisTick((class_1657)player) || ServerRotation.pending() || MeleeAttacks.clickPending(mc)) {
            return;
        }
        class_3966 hit = MeleeAttacks.crosshairHit(mc);
        if (hit == null || !((class_12972 = hit.method_17782()) instanceof class_1657) || !MeleeAttacks.isEnemy(player, (class_1297)(target = (class_1657)class_12972))) {
            return;
        }
        if (MeleeAttacks.shieldBroken((class_1309)target) || !ShieldBreakerModule.blocksSoon(player, target)) {
            return;
        }
        if (!(MeleeAttacks.disablesShields(player.method_6047()) || ShieldBreakerModule.bestAxe(player) >= 0 && ShieldBreakerModule.fightingHand(player))) {
            return;
        }
        if (MeleeAttacks.attack(mc, hit)) {
            this.rearmTick = this.ticks + 10;
            MeleeAttacks.claimTick(mc);
        }
    }

    private static boolean blocksSoon(class_746 player, class_1657 target) {
        if (!target.method_6115()) {
            return false;
        }
        class_1799 shield = target.method_6030();
        class_10707 blocks = (class_10707)shield.method_58694(class_9334.field_56396);
        if (blocks == null || target.method_6048() + ShieldBreakerModule.leadTicks(player) < blocks.method_67197()) {
            return false;
        }
        return MeleeAttacks.shieldBlocks((class_1309)target, (class_1657)player, shield);
    }

    private static int leadTicks(class_746 player) {
        class_640 info = player.field_3944.method_2871(player.method_5667());
        return info == null ? 0 : class_3532.method_15340((int)Math.round((float)info.method_2959() / 50.0f), (int)0, (int)4);
    }

    static int bestAxe(class_746 player) {
        class_1661 inventory = player.method_31548();
        int best = -1;
        double bestDamage = Double.NEGATIVE_INFINITY;
        for (int slot = 0; slot < class_1661.method_7368(); ++slot) {
            double damage;
            class_1799 stack = inventory.method_5438(slot);
            if (!MeleeAttacks.disablesShields(stack) || stack.method_63692() || !((damage = MeleeAttacks.attackDamage(stack)) > bestDamage)) continue;
            best = slot;
            bestDamage = damage;
        }
        return best;
    }
}

