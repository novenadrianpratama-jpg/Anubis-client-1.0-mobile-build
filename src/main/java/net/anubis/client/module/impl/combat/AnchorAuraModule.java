/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1937
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 *  net.minecraft.class_310
 *  net.minecraft.class_4969
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import imgui.ImDrawList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.Render3DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.HudOverlayProvider;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.AutoCityModule;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.combat.CrystalAuraModule;
import net.anubis.client.module.impl.combat.anchor.AnchorGuard;
import net.anubis.client.module.impl.combat.crystal.Combo;
import net.anubis.client.module.impl.combat.crystal.CrystalDamage;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.anubis.client.module.impl.combat.crystal.HurtClock;
import net.anubis.client.module.impl.combat.crystal.LootGuard;
import net.anubis.client.module.impl.combat.crystal.ShotView;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.impl.combat.melee.SlotReturn;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.Rotation;
import net.anubis.client.util.pvp.RotationUtils;
import net.anubis.client.util.pvp.ServerRotation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_4969;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class AnchorAuraModule
extends ClientModule
implements HudOverlayProvider {
    private static final double REACH_MARGIN = 0.01;
    private static final int ARM_TICKS = 2;
    private static final int HOLD_TICKS = 10;
    private static final int MAX_LEAD_TICKS = 5;
    private static final int AIM_TIMEOUT_TICKS = 12;
    private static final int SKIP_TICKS = 40;
    private final NumberSetting minDamage = this.addSetting("min-damage", new NumberSetting("Min Damage", 6.0f, 0.0f, 20.0f).step(0.5f));
    private final NumberSetting maxSelfDamage = this.addSetting("max-self-damage", new NumberSetting("Max Self Damage", 6.0f, 0.0f, 20.0f).step(0.5f));
    private final BooleanSetting render = this.addSetting("render", new BooleanSetting("Render", true));
    private final ColorSetting renderColor = this.addSetting("render-color", new ColorSetting("Render Color", -26052));
    private final NumberSetting turnSpeed = this.addSetting("turn-speed", new NumberSetting("Turn Speed", 4.5f, 1.0f, 10.0f).step(0.5f));
    private final BooleanSetting protectLoot = this.addSetting("protect-loot", new BooleanSetting("Protect Loot", true));
    private final SlotReturn slots = new SlotReturn(this);
    private final ShotView view = new ShotView();
    private int tick;
    private int holdUntil;
    private class_2338 spent;
    private int spentUntil;
    private float aimTurn;
    private float placedTurn;
    private class_2338 aimingAt;
    private int aimingTicks;
    private class_2338 skipped;
    private int skippedUntil;
    private List<CrystalDamage.Victim> lastTargets;
    private List<CrystalDamage.Victim> lastSpared;
    private int lastRtt;
    private float steerMin;
    private float steerSelf;
    private int steerUntil = -1;
    private float tickMin;
    private float tickSelf;

    public AnchorAuraModule() {
        super("anchor-aura", "Anchor Aura", "Places, charges and detonates respawn anchors on nearby enemies.", ModuleCategory.COMBAT);
        this.renderColor.visibleWhen(this.render::get);
    }

    @Override
    protected void onDisable() {
        this.slots.drop(class_310.method_1551(), true);
        this.spent = null;
        this.view.clear();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        CrystalAuraModule crystal;
        ++this.tick;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player != null && mc.field_1687 != null) {
            LootGuard.update(mc.field_1687, player);
        }
        if ((crystal = ModuleRegistry.get(CrystalAuraModule.class)) != null && crystal.isEnabled() || !MeleeAttacks.canAct(mc) || player.method_7325() || AutoInvTotemModule.wantsHotbar() || AutoCityModule.mining() || CombatMacroLock.heldByOther(this) || mc.field_1687.method_27983() == class_1937.field_25180) {
            this.slots.tick(mc, true);
            this.release();
            return;
        }
        HurtClock.tick(player);
        if (this.tick > this.holdUntil) {
            this.slots.tick(mc, true);
        }
        if (MeleeAttacks.actedThisTick((class_1657)player) || !MeleeAttacks.handsFree(mc, player) || player.method_21823() || MeleeAttacks.rightClickComing(mc)) {
            return;
        }
        Combo.Shot shot = Combo.pick(this.shots(mc, player, Combo.enemies(mc, player), Combo.friends(mc, player), Crystals.rttTicks(mc, player), 0.0f));
        if (shot == null) {
            if (this.spent != null && this.tick < this.spentUntil && mc.field_1687.method_8320(this.spent).method_27852(class_2246.field_23152)) {
                ServerRotation.request(ServerRotation.sent());
            }
            if (this.tick > this.holdUntil) {
                this.release();
            }
            return;
        }
        this.show(shot);
        if (!CombatMacroLock.tryAcquire(this)) {
            return;
        }
        if (!this.ready(mc, player, shot)) {
            class_2338 cell = AnchorAuraModule.cellOf(shot);
            this.aimingTicks = cell.equals((Object)this.aimingAt) ? this.aimingTicks + 1 : 0;
            this.aimingAt = cell;
            if (this.aimingTicks >= 12) {
                this.skipped = cell;
                this.skippedUntil = this.tick + 40;
                this.aimingAt = null;
                return;
            }
            ServerRotation.request(RotationUtils.fromTo(player.method_33571(), shot.aim()), this.turnSpeed.get());
            return;
        }
        this.aimingAt = null;
        if (shot.route() != -2 && !this.slots.take(player, shot.route())) {
            return;
        }
        this.fire(mc, player, shot);
        this.holdUntil = this.tick + 10;
        ServerRotation.request(ServerRotation.sent());
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            HurtClock.note(player, event.packet);
        }
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

    private static class_2338 cellOf(Combo.Shot shot) {
        class_2338 class_23382;
        Object object = shot.plan();
        if (object instanceof AnchorGuard.Spot) {
            AnchorGuard.Spot spot = (AnchorGuard.Spot)object;
            class_23382 = spot.pos();
        } else {
            class_23382 = (class_2338)shot.plan();
        }
        return class_23382;
    }

    private boolean ruledOut(class_2338 cell) {
        return cell.equals((Object)this.skipped) && this.tick < this.skippedUntil || this.protectLoot.get() && LootGuard.near(class_243.method_24953((class_2382)cell));
    }

    public void show(Combo.Shot shot) {
        if (!this.render.get()) {
            return;
        }
        class_2338 cell = AnchorAuraModule.cellOf(shot);
        class_243 center = class_243.method_24953((class_2382)cell);
        float most = 0.0f;
        for (CrystalDamage.Victim victim : this.lastTargets) {
            most = Math.max(most, victim.damage(center, 5.0f, cell));
        }
        float own = this.lastSpared.get(0).damage(center, 5.0f, cell);
        this.view.show(new class_238(cell), this.renderColor.argb(), most, own);
    }

    public void steer(class_746 player, float minDamage, float maxSelf) {
        this.steerMin = minDamage;
        this.steerSelf = maxSelf;
        this.steerUntil = player.field_6012 + 2;
    }

    public List<Combo.Shot> shots(class_310 mc, class_746 player, List<class_1309> enemies, List<class_1309> friends, int rtt, float rival) {
        List<class_238> reserved;
        Combo.Shot placement;
        class_638 level = mc.field_1687;
        boolean steered = player.field_6012 <= this.steerUntil;
        this.tickMin = steered ? this.steerMin : this.minDamage.get();
        this.tickSelf = steered ? this.steerSelf : this.maxSelfDamage.get();
        class_1661 inventory = player.method_31548();
        class_1799 main = player.method_6047();
        int detonator = AnchorGuard.detonatorRoute(inventory, main);
        if (level.method_27983() == class_1937.field_25180 || !ServerRotation.canRotate(player) || player.method_6079().method_31574(class_1802.field_8801) || player.method_21823() || enemies.isEmpty() || detonator == -1) {
            return List.of();
        }
        ArrayList<class_1309> spared = new ArrayList<class_1309>();
        spared.add((class_1309)player);
        spared.addAll(friends);
        this.lastRtt = rtt;
        this.lastTargets = AnchorAuraModule.victims(enemies, rtt);
        this.lastSpared = AnchorAuraModule.victims(spared, rtt);
        ArrayList<Combo.Shot> shots = new ArrayList<Combo.Shot>();
        int glowstone = AnchorAuraModule.routeOf(inventory, main, class_1802.field_8801);
        boolean existing = false;
        for (class_2338 cell : this.anchors(level, player, enemies)) {
            boolean charged;
            int ahead;
            Blast due;
            class_243 aim;
            if (this.ruledOut(cell) || (aim = AnchorGuard.anchorAim(level, player, cell)) == null || !this.safe(this.lastSpared, cell) || (due = this.blast(cell, ahead = (charged = (Integer)level.method_8320(cell).method_11654((class_2769)class_4969.field_23153) > 0) ? 0 : 1, false)) == null && this.blast(cell, ahead, true) == null) continue;
            existing = true;
            if (due == null) continue;
            if (charged) {
                shots.add(new Combo.Shot(Combo.Kind.DETONATE, aim, detonator, due.value(), due.kills(), 0, cell));
                continue;
            }
            if (glowstone == -1 || HurtClock.opensIn(due.source(), rtt) > 1 || !(rival <= 0.0f) && !(due.value() > rival + 1.0f)) continue;
            shots.add(new Combo.Shot(Combo.Kind.CHARGE, aim, glowstone, due.value(), due.kills(), 1, cell));
        }
        int anchor = AnchorAuraModule.routeOf(inventory, main, class_1802.field_23141);
        if (!existing && anchor != -1 && glowstone != -1 && (placement = this.place(level, player, anchor, reserved = Crystals.reservedBoxes(player, Crystals.confirmWindow(mc, player)))) != null) {
            shots.add(placement);
        }
        return shots;
    }

    public boolean ready(class_310 mc, class_746 player, Combo.Shot shot) {
        boolean charged;
        class_638 level = mc.field_1687;
        if (shot.kind() == Combo.Kind.ANCHOR) {
            AnchorGuard.Spot spot = (AnchorGuard.Spot)shot.plan();
            Rotation sent = ServerRotation.sent();
            if (sent != null && AnchorGuard.hitAlong(level, player, sent, spot) != null) {
                return true;
            }
            float turn = AnchorAuraModule.turnTo(player, spot);
            if (turn > 0.0f) {
                this.aimTurn = turn;
            }
            return false;
        }
        class_2338 cell = (class_2338)shot.plan();
        class_2680 state = level.method_8320(cell);
        if (!state.method_27852(class_2246.field_23152) || AnchorGuard.anchorHit(level, player, cell) == null) {
            return false;
        }
        boolean bl = charged = (Integer)state.method_11654((class_2769)class_4969.field_23153) > 0;
        if (shot.kind() == Combo.Kind.CHARGE) {
            return !charged;
        }
        return charged && this.safe(this.lastSpared, cell) && this.blast(cell, 0, false) != null;
    }

    public void fire(class_310 mc, class_746 player, Combo.Shot shot) {
        class_638 level = mc.field_1687;
        switch (shot.kind()) {
            case ANCHOR: {
                AnchorGuard.Spot spot = (AnchorGuard.Spot)shot.plan();
                AnchorGuard.click(mc, player, AnchorGuard.hitAlong(level, player, ServerRotation.sent(), spot));
                if (!(this.aimTurn > 0.0f)) break;
                this.placedTurn = this.aimTurn;
                break;
            }
            case CHARGE: {
                AnchorGuard.click(mc, player, AnchorGuard.anchorHit(level, player, (class_2338)shot.plan()));
                break;
            }
            case DETONATE: {
                class_2338 cell = (class_2338)shot.plan();
                AnchorGuard.click(mc, player, AnchorGuard.anchorHit(level, player, cell));
                this.spent = cell;
                this.spentUntil = this.tick + Crystals.confirmWindow(mc, player);
                class_243 center = class_243.method_24953((class_2382)cell);
                for (CrystalDamage.Victim victim : this.lastTargets) {
                    float raw = victim.raw(center, 5.0f, cell);
                    if (!(raw > 0.0f)) continue;
                    HurtClock.shot(player, victim.entity(), raw, center, this.lastRtt);
                }
                break;
            }
        }
    }

    private Combo.Shot place(class_638 level, class_746 player, int route, List<class_238> reserved) {
        LinkedHashMap<class_2338, Float> bounds = new LinkedHashMap<class_2338, Float>();
        for (CrystalDamage.Victim victim : this.lastTargets) {
            class_2338 feet = victim.entity().method_24515();
            for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-2, -1, -2), (class_2338)feet.method_10069(2, 2, 2))) {
                float bound;
                if (bounds.containsKey(pos) || !level.method_8320(pos).method_26215() || !player.method_56093(pos, -0.01) || AnchorAuraModule.reservedAt(reserved, pos) || this.ruledOut(pos) || this.walkedInto(level, pos) || !((bound = this.upperValue(class_243.method_24953((class_2382)pos))) >= 0.0f)) continue;
                bounds.put(pos.method_10062(), Float.valueOf(bound));
            }
        }
        ArrayList cells = new ArrayList(bounds.keySet());
        cells.sort((a, b) -> Float.compare(((Float)bounds.get(b)).floatValue(), ((Float)bounds.get(a)).floatValue()));
        class_243 eye = player.method_33571();
        Combo.Shot best = null;
        for (class_2338 pos : cells) {
            float turn;
            Rotation look;
            class_243 reach;
            AnchorGuard.Spot candidate;
            if (best != null && ((Float)bounds.get(pos)).floatValue() <= best.value()) break;
            Blast blast = this.blast(pos, 2, true);
            if (blast == null || best != null && blast.value() <= best.value() || !this.safe(this.lastSpared, pos) || (candidate = AnchorGuard.supportFor(level, player, eye, pos)) == null || new class_238(pos).method_992(eye, eye.method_1019(reach = class_243.method_1030((float)(look = ServerRotation.next(player, RotationUtils.fromTo(eye, candidate.aim()))).pitch(), (float)look.yaw()).method_1021(player.method_55754()))).isEmpty() || (turn = AnchorAuraModule.turnTo(player, candidate)) > 2.0f && Math.abs(turn - this.placedTurn) < 1.0E-4f) continue;
            best = new Combo.Shot(Combo.Kind.ANCHOR, candidate.aim(), route, blast.value(), blast.kills(), 2, candidate);
        }
        return best;
    }

    private Set<class_2338> anchors(class_638 level, class_746 player, List<class_1309> enemies) {
        LinkedHashSet<class_2338> anchors = new LinkedHashSet<class_2338>();
        for (class_1309 enemy : enemies) {
            class_2338 feet = enemy.method_24515();
            for (class_2338 pos : class_2338.method_10097((class_2338)feet.method_10069(-4, -3, -4), (class_2338)feet.method_10069(4, 4, 4))) {
                if (!level.method_8320(pos).method_27852(class_2246.field_23152) || !player.method_56093(pos, -0.01) || pos.equals((Object)this.spent) && this.tick < this.spentUntil) continue;
                anchors.add(pos.method_10062());
            }
        }
        return anchors;
    }

    private Blast blast(class_2338 cell, int ahead, boolean waiting) {
        class_243 center = class_243.method_24953((class_2382)cell);
        class_1309 source = null;
        float best = 0.0f;
        boolean kills = false;
        for (CrystalDamage.Victim victim : this.lastTargets) {
            int when;
            class_1309 enemy = victim.entity();
            int n = when = waiting ? Math.max(ahead, HurtClock.opensIn(enemy, this.lastRtt)) : ahead;
            float effective = HurtClock.effective(victim, victim.raw(center, 5.0f, cell), when, this.lastRtt);
            if (effective < this.needed(enemy)) continue;
            kills = kills || Combo.kills(effective, enemy);
            float value = Combo.value(effective, enemy);
            if (source != null && !(value > best)) continue;
            source = enemy;
            best = value;
        }
        if (source == null) {
            return null;
        }
        float own = this.lastSpared.get(0).damage(center, 5.0f, cell);
        return new Blast(best - 0.5f * own, kills, source);
    }

    private float upperValue(class_243 center) {
        float bound = -1.0f;
        for (CrystalDamage.Victim victim : this.lastTargets) {
            float upper = victim.upperBound(center, 5.0f);
            if (!(upper >= this.needed(victim.entity()))) continue;
            bound = Math.max(bound, Combo.value(upper, victim.entity()));
        }
        return bound;
    }

    private boolean safe(List<CrystalDamage.Victim> spared, class_2338 cell) {
        class_243 center = class_243.method_24953((class_2382)cell);
        for (CrystalDamage.Victim other : spared) {
            float damage = other.damage(center, 5.0f, cell);
            if (!(damage > this.tickSelf) && !CrystalDamage.endangers((class_1657)other.entity(), damage)) continue;
            return false;
        }
        return true;
    }

    private float needed(class_1309 enemy) {
        return Math.min(this.tickMin, CrystalDamage.health(enemy));
    }

    private static List<CrystalDamage.Victim> victims(List<class_1309> entities, int rtt) {
        ArrayList<CrystalDamage.Victim> victims = new ArrayList<CrystalDamage.Victim>(entities.size());
        for (class_1309 entity : entities) {
            int lead = entity instanceof class_746 ? 0 : Math.min(rtt, 5);
            victims.add(new CrystalDamage.Victim(entity, Combo.lead(entity, lead)));
        }
        return victims;
    }

    private boolean walkedInto(class_638 level, class_2338 pos) {
        class_238 cell = new class_238(pos);
        int lead = Math.min(this.lastRtt, 5);
        for (class_1657 other : level.method_18456()) {
            class_238 box;
            if (other.method_7325() || other instanceof class_746 || !(box = other.method_5829()).method_994(cell) && !box.method_997(Combo.lead((class_1309)other, lead)).method_994(cell)) continue;
            return true;
        }
        return false;
    }

    private static boolean reservedAt(List<class_238> reserved, class_2338 pos) {
        for (class_238 box : reserved) {
            if (!box.method_71152(pos)) continue;
            return true;
        }
        return false;
    }

    private static int routeOf(class_1661 inventory, class_1799 main, class_1792 item) {
        if (main.method_31574(item)) {
            return -2;
        }
        int slot = Crystals.hotbarSlotOf(inventory, item);
        return slot == -1 ? -1 : slot;
    }

    private static float turnTo(class_746 player, AnchorGuard.Spot spot) {
        Rotation sent = ServerRotation.sent();
        Rotation from = sent != null ? sent : RotationUtils.entityRotation((class_1297)player);
        Rotation aimed = ServerRotation.next(player, RotationUtils.fromTo(player.method_33571(), spot.aim()));
        return Math.abs(aimed.yaw() - from.yaw());
    }

    private void release() {
        if (!this.slots.holding()) {
            CombatMacroLock.release(this);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Blast(float value, boolean kills, class_1309 source) {
    }
}

