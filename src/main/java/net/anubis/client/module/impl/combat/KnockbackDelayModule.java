/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_2547
 *  net.minecraft.class_2596
 *  net.minecraft.class_2664
 *  net.minecraft.class_2743
 *  net.minecraft.class_310
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.melee.MeleeAttacks;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2743;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class KnockbackDelayModule
extends ClientModule {
    private static final double FIGHT_RANGE = 6.0;
    private final NumberSetting chance = this.addSetting("chance", new NumberSetting("Chance (%)", 40.0f, 0.0f, 100.0f).step(5.0f));
    private final NumberSetting airMin = this.addSetting("air-min", new NumberSetting("Air Delay Min (ms)", 50.0f, 0.0f, 500.0f).step(10.0f));
    private final NumberSetting airMax = this.addSetting("air-max", new NumberSetting("Air Delay Max (ms)", 100.0f, 0.0f, 500.0f).step(10.0f));
    private final NumberSetting groundMin = this.addSetting("ground-min", new NumberSetting("Ground Delay Min (ms)", 200.0f, 0.0f, 1000.0f).step(10.0f));
    private final NumberSetting groundMax = this.addSetting("ground-max", new NumberSetting("Ground Delay Max (ms)", 250.0f, 0.0f, 1000.0f).step(10.0f));
    private final BooleanSetting waterCheck = this.addSetting("water-check", new BooleanSetting("Not In Water", false));
    private final BooleanSetting onlyFights = this.addSetting("only-fights", new BooleanSetting("Only Near Enemies", true));
    private final class_310 mc = class_310.method_1551();
    private final Deque<Held> held = new ArrayDeque<Held>();
    private class_634 heldFor;
    private volatile boolean fightNear;

    public KnockbackDelayModule() {
        super("knockback-delay", "Knockback Delay", "Takes some hits' knockback a random moment late: a little in the air, longer on the ground.", ModuleCategory.COMBAT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void onEnable() {
        Deque<Held> deque = this.held;
        synchronized (deque) {
            this.held.clear();
        }
        this.on(TickEvent.TYPE, Stage.PRE, event -> {
            this.fightNear = this.mc.field_1724 != null && this.enemyNear(this.mc.field_1724);
            this.applyDue(false);
        });
    }

    @Override
    protected void onDisable() {
        this.mc.execute(() -> this.applyDue(true));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    public void onPacketReceive(PacketReceiveNettyEvent event) {
        boolean waiting;
        class_746 player = this.mc.field_1724;
        if (player == null || !KnockbackDelayModule.pushesMe(event.packet, player)) {
            return;
        }
        Deque<Held> deque = this.held;
        synchronized (deque) {
            waiting = !this.held.isEmpty();
        }
        if (!waiting && !this.delays(player)) {
            return;
        }
        boolean ground = player.method_24828();
        long wait = waiting ? 0L : KnockbackDelayModule.randomBetween(ground ? this.groundMin.get() : this.airMin.get(), ground ? this.groundMax.get() : this.airMax.get());
        Deque<Held> deque2 = this.held;
        synchronized (deque2) {
            if (this.held.isEmpty()) {
                this.heldFor = player.field_3944;
            }
            long due = Math.max(System.currentTimeMillis() + wait, this.held.isEmpty() ? 0L : this.held.peekLast().due());
            this.held.addLast(new Held(event.packet, due));
        }
        event.setCancelled(true);
    }

    private static boolean pushesMe(class_2596<?> packet, class_746 player) {
        class_2664 explode;
        if (packet instanceof class_2743) {
            class_2743 motion = (class_2743)packet;
            return motion.method_11818() == player.method_5628();
        }
        return packet instanceof class_2664 && (explode = (class_2664)packet).comp_2884().isPresent();
    }

    private boolean delays(class_746 player) {
        if (this.waterCheck.get() && player.method_5799()) {
            return false;
        }
        if (this.onlyFights.get() && !this.fightNear) {
            return false;
        }
        return ThreadLocalRandom.current().nextDouble(100.0) < (double)this.chance.get();
    }

    private boolean enemyNear(class_746 player) {
        for (class_1657 other : player.method_73183().method_18456()) {
            if (other == player || !((double)other.method_5739((class_1297)player) <= 6.0) || !MeleeAttacks.isEnemy(player, (class_1297)other)) continue;
            return true;
        }
        return false;
    }

    private static long randomBetween(float a, float b) {
        long low = Math.round(Math.min(a, b));
        long high = Math.round(Math.max(a, b));
        return high <= low ? low : ThreadLocalRandom.current().nextLong(low, high + 1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void applyDue(boolean all) {
        class_634 connection = this.mc.method_1562();
        long now = System.currentTimeMillis();
        while (true) {
            Held next;
            Deque<Held> deque = this.held;
            synchronized (deque) {
                next = this.held.peekFirst();
                if (next == null || !all && next.due() > now) {
                    return;
                }
                this.held.pollFirst();
                if (connection == null || connection != this.heldFor) {
                    this.held.clear();
                    return;
                }
            }
            next.packet().method_65081((class_2547)connection);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Held(class_2596<?> packet, long due) {
    }
}

