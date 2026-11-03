/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10185
 *  net.minecraft.class_10255
 *  net.minecraft.class_10590
 *  net.minecraft.class_10707
 *  net.minecraft.class_12125
 *  net.minecraft.class_12392
 *  net.minecraft.class_1268
 *  net.minecraft.class_1282
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1308
 *  net.minecraft.class_1309
 *  net.minecraft.class_1322$class_1323
 *  net.minecraft.class_1531
 *  net.minecraft.class_1569
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_3966
 *  net.minecraft.class_5134
 *  net.minecraft.class_5354
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 */
package net.anubis.client.module.impl.combat.melee;

import com.example.client.mixin.AccessorCombatMinecraft;
import com.example.client.mixin.AccessorLocalPlayer;
import com.example.client.mixin.AccessorTriggerbotKeyMapping;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.anubis.client.config.FriendStore;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.ShieldBreakerModule;
import net.anubis.client.module.impl.misc.AutoEatModule;
import net.anubis.client.module.impl.misc.FreecamModule;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10185;
import net.minecraft.class_10255;
import net.minecraft.class_10590;
import net.minecraft.class_10707;
import net.minecraft.class_12125;
import net.minecraft.class_12392;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1531;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_5134;
import net.minecraft.class_5354;
import net.minecraft.class_746;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public final class MeleeAttacks {
    public static final float CRIT_CHARGE = 0.9f;
    private static final double LINE_EPSILON_SQ = 0.001;
    private static final int SHIELD_BROKEN_TICKS = 20;
    private static int actorId;
    private static int actionTick;
    private static int shieldBrokenId;
    private static int shieldBrokenUntil;

    private MeleeAttacks() {
    }

    public static void noteAction(class_1657 player) {
        actorId = player.method_5628();
        actionTick = player.field_6012;
    }

    public static boolean actedThisTick(class_1657 player) {
        return actionTick == player.field_6012 && actorId == player.method_5628();
    }

    public static boolean handsFree(class_310 mc, class_746 player) {
        return !player.method_6115() && !player.method_3144() && !mc.field_1761.method_2923() && !MeleeAttacks.rowing(player) && !MeleeAttacks.clickPending(mc.field_1690.field_1831) && !MeleeAttacks.clickPending(mc.field_1690.field_1869);
    }

    private static boolean rowing(class_746 player) {
        class_10185 sent = ((AccessorLocalPlayer)player).anubis$lastSentInput();
        return player.method_5854() instanceof class_10255 && (sent.comp_3159() != sent.comp_3160() || sent.comp_3161() != sent.comp_3162());
    }

    public static boolean clickPending(class_310 mc) {
        return MeleeAttacks.clickPending(mc.field_1690.field_1886);
    }

    public static boolean clickPending(class_304 key) {
        return ((AccessorTriggerbotKeyMapping)key).anubis$triggerbotClickCount() > 0;
    }

    public static boolean rightClickComing(class_310 mc) {
        return mc.field_1690.field_1904.method_1434() || MeleeAttacks.clickPending(mc.field_1690.field_1904);
    }

    public static void takeAttackClicks(class_310 mc) {
        class_304 attack = mc.field_1690.field_1886;
        while (attack.method_1436()) {
        }
    }

    public static void claimTick(class_310 mc) {
        MeleeAttacks.takeAttackClicks(mc);
        class_304 use = mc.field_1690.field_1904;
        while (use.method_1436()) {
        }
        mc.field_1771 = Math.max(mc.field_1771, 1);
        AccessorCombatMinecraft access = (AccessorCombatMinecraft)mc;
        access.anubis$setRightClickDelay(Math.max(access.anubis$rightClickDelay(), 2));
    }

    public static boolean canAct(class_310 mc) {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null || mc.field_1755 != null) {
            return false;
        }
        if (AutoEatModule.isEating()) {
            return false;
        }
        FreecamModule freecam = ModuleRegistry.get(FreecamModule.class);
        return freecam == null || !freecam.isEnabled();
    }

    public static class_3966 crosshairHit(class_310 mc) {
        class_3966 hit;
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return null;
        }
        class_239 result = player.method_76762(1.0f, (class_1297)player);
        if (!(result instanceof class_3966) || (hit = (class_3966)result).method_17783() != class_239.class_240.field_1331) {
            return null;
        }
        return MeleeAttacks.lineIsClear(player, player.method_33571(), hit.method_17784()) ? hit : null;
    }

    public static boolean lineIsClear(class_746 player, class_243 from, class_243 to) {
        class_3965 block = player.method_73183().method_17742(new class_3959(from, to, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player));
        return block.method_17783() == class_239.class_240.field_1333 || block.method_17784().method_1025(from) >= from.method_1025(to) - 0.001;
    }

    public static boolean attack(class_310 mc, class_3966 hit) {
        return MeleeAttacks.attack(mc, hit, 0.0);
    }

    public static boolean attack(class_310 mc, class_3966 hit, double extraRange) {
        boolean inReach;
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return false;
        }
        if (mc.field_1771 > 0 || MeleeAttacks.actedThisTick((class_1657)player)) {
            return false;
        }
        if (!MeleeAttacks.handsFree(mc, player) || player.method_7325()) {
            return false;
        }
        if (player.method_5624() != ((AccessorLocalPlayer)player).anubis$wasSprinting()) {
            return false;
        }
        class_1799 held = player.method_6047();
        if (held.method_57826(class_9334.field_63631)) {
            return false;
        }
        if (!held.method_45435(mc.field_1687.method_45162()) || player.method_75202(held, 0)) {
            return false;
        }
        class_1297 target = hit.method_17782();
        if (target.method_31481()) {
            return false;
        }
        class_12392 range = (class_12392)held.method_58694(class_9334.field_64680);
        boolean bl = inReach = range != null ? range.method_76736((class_1309)player, hit.method_17784()) : player.method_56092(target.method_5829(), extraRange);
        if (!inReach) {
            return false;
        }
        mc.field_1761.method_2918((class_1657)player, target);
        if (!MeleeAttacks.actedThisTick((class_1657)player)) {
            return false;
        }
        player.method_6104(class_1268.field_5808);
        return true;
    }

    public static boolean stab(class_310 mc, class_3966 hit) {
        boolean inReach;
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            return false;
        }
        if (mc.field_1771 > 0 || MeleeAttacks.actedThisTick((class_1657)player)) {
            return false;
        }
        if (!MeleeAttacks.handsFree(mc, player) || player.method_7325()) {
            return false;
        }
        if (player.method_5624() != ((AccessorLocalPlayer)player).anubis$wasSprinting()) {
            return false;
        }
        class_1799 held = player.method_6047();
        class_12125 piercing = (class_12125)held.method_58694(class_9334.field_63631);
        if (piercing == null) {
            return false;
        }
        if (!held.method_45435(mc.field_1687.method_45162()) || player.method_75202(held, 0)) {
            return false;
        }
        class_1297 target = hit.method_17782();
        if (target.method_31481()) {
            return false;
        }
        class_12392 range = (class_12392)held.method_58694(class_9334.field_64680);
        boolean bl = inReach = range != null ? range.method_76736((class_1309)player, hit.method_17784()) : player.method_56092(target.method_5829(), 0.0);
        if (!inReach) {
            return false;
        }
        mc.field_1761.method_75407(piercing);
        if (!MeleeAttacks.actedThisTick((class_1657)player)) {
            return false;
        }
        player.method_6104(class_1268.field_5808);
        return true;
    }

    public static class_1297 stabTarget(class_746 player, Predicate<class_1297> wanted) {
        class_12392 range = player.method_76693();
        Rotation sent = ServerRotation.sent();
        class_243 look = sent != null ? class_243.method_1030((float)sent.pitch(), (float)sent.yaw()) : player.method_5828(1.0f);
        class_243 eye = player.method_33571();
        class_243 start = eye.method_1019(look.method_1021((double)range.method_76732((class_1297)player)));
        class_243 end = eye.method_1019(look.method_1021((double)range.method_76739((class_1297)player) + Math.max(0.0, player.method_60478().method_1026(look))));
        class_3965 wall = player.method_73183().method_17742(new class_3959(eye, end, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)player));
        if (wall.method_17783() != class_239.class_240.field_1333) {
            end = wall.method_17784();
        }
        float margin = range.comp_5262();
        for (class_1297 entity : player.method_73183().method_8333((class_1297)player, new class_238(eye, end).method_1014((double)margin + 1.0), wanted)) {
            class_238 box = entity.method_5829().method_1014((double)margin);
            if (!box.method_1006(start) && !box.method_992(start, end).isPresent()) continue;
            return entity;
        }
        return null;
    }

    private static boolean isHittable(class_746 self, class_1297 entity) {
        class_1657 player;
        class_1309 living;
        block5: {
            block4: {
                if (!(entity instanceof class_1309)) break block4;
                living = (class_1309)entity;
                if (entity != self && !(entity instanceof class_1531)) break block5;
            }
            return false;
        }
        if (!living.method_5805() || !entity.method_5732() || self.method_5854() == entity) {
            return false;
        }
        return !(entity instanceof class_1657) || !(player = (class_1657)entity).method_7325() && !player.method_68878();
    }

    public static boolean isEnemy(class_746 self, class_1297 entity) {
        class_1657 player;
        if (!MeleeAttacks.isHittable(self, entity) || MeleeAttacks.isFriend(entity)) {
            return false;
        }
        return !(entity instanceof class_1657) || self.method_7256(player = (class_1657)entity) && self.field_3944.method_2871(player.method_5667()) != null;
    }

    public static boolean isHostile(class_1309 entity) {
        class_1308 mob;
        return entity instanceof class_1569 && (!(entity instanceof class_5354) || entity instanceof class_1308 && (mob = (class_1308)entity).method_6510());
    }

    public static float charge(class_1657 player) {
        return player.method_7261(0.5f);
    }

    public static int ticksToFull(class_1657 player) {
        return Math.max(0, class_3532.method_15386((float)((1.0f - MeleeAttacks.charge(player)) * player.method_7279())));
    }

    public static boolean airborne(class_1657 player) {
        return !player.method_24828() && !player.method_6101() && !player.method_5799() && !player.method_74025() && !player.method_5765() && !player.method_31549().field_7479 && !player.method_6128();
    }

    public static boolean wouldCrit(class_1657 player, class_1297 target) {
        return MeleeAttacks.charge(player) > 0.9f && player.field_6017 > 0.0 && MeleeAttacks.critStance(player, target);
    }

    public static boolean critStance(class_1657 player, class_1297 target) {
        return !player.method_24828() && !player.method_6101() && !player.method_5799() && !player.method_74025() && !player.method_5765() && !player.method_5624() && target instanceof class_1309;
    }

    public static boolean shieldBlocks(class_1309 target, class_1657 attacker) {
        return MeleeAttacks.shieldBlocks(target, attacker, target.method_62821());
    }

    public static boolean shieldBlocks(class_1309 target, class_1657 attacker, class_1799 shield) {
        if (shield == null) {
            return false;
        }
        class_10707 blocks = (class_10707)shield.method_58694(class_9334.field_56396);
        if (blocks == null) {
            return false;
        }
        class_1282 source = attacker.method_73183().method_48963().method_48802(attacker);
        if (blocks.comp_3637().map(tag -> source.method_48789(tag)).orElse(false).booleanValue()) {
            return false;
        }
        class_243 facing = target.method_5631(0.0f, target.method_5791());
        class_243 toAttacker = attacker.method_73189().method_1020(target.method_73189());
        class_243 flat = new class_243(toAttacker.field_1352, 0.0, toAttacker.field_1350).method_1029();
        double angle = Math.acos(class_3532.method_15350((double)flat.method_1026(facing), (double)-1.0, (double)1.0));
        return blocks.method_67202(source, 1.0f, angle) > 0.0f;
    }

    public static boolean wastedOnShield(class_746 self, class_1309 target) {
        if (!MeleeAttacks.shieldBlocks(target, (class_1657)self) || MeleeAttacks.shieldBroken(target) || MeleeAttacks.disablesShields(self.method_6047())) {
            return false;
        }
        ShieldBreakerModule breaker = ModuleRegistry.get(ShieldBreakerModule.class);
        if (breaker == null || !breaker.isEnabled()) {
            return true;
        }
        class_1661 inventory = self.method_31548();
        for (int slot = 0; slot < class_1661.method_7368(); ++slot) {
            if (!MeleeAttacks.disablesShields(inventory.method_5438(slot))) continue;
            return false;
        }
        return true;
    }

    public static void noteShieldBroken(class_1309 target) {
        shieldBrokenId = target.method_5628();
        shieldBrokenUntil = target.field_6012 + 20;
    }

    public static boolean shieldBroken(class_1309 target) {
        return target.method_5628() == shieldBrokenId && target.field_6012 < shieldBrokenUntil;
    }

    public static boolean disablesShields(class_1799 stack) {
        class_10590 weapon = (class_10590)stack.method_58694(class_9334.field_55878);
        return weapon != null && weapon.comp_3602() > 0.0f;
    }

    public static boolean isMeleeWeapon(class_1799 stack) {
        return stack.method_31573(class_3489.field_42611) || stack.method_31573(class_3489.field_42612) || stack.method_31573(class_3489.field_63257) || stack.method_31574(class_1802.field_49814) || stack.method_31574(class_1802.field_8547);
    }

    public static double attackDamage(class_1799 stack) {
        double[] total = new double[]{0.0};
        stack.method_57354(class_1304.field_6173, (attribute, modifier) -> {
            if (class_5134.field_23721.equals(attribute) && modifier.comp_2450() == class_1322.class_1323.field_6328) {
                total[0] = total[0] + modifier.comp_2449();
            }
        });
        return total[0];
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean isFriend(class_1297 entity) {
        if (!(entity instanceof class_1657)) return false;
        class_1657 player = (class_1657)entity;
        if (!FriendStore.getInstance().isFriend(player.method_7334().name())) return false;
        return true;
    }

    public static int spread(int maxTicks) {
        return maxTicks > 0 ? ThreadLocalRandom.current().nextInt(maxTicks + 1) : 0;
    }

    static {
        actionTick = -1;
    }
}

