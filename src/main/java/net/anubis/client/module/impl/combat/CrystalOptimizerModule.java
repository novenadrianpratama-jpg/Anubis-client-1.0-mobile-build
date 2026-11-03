/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1309
 *  net.minecraft.class_1511
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2716
 *  net.minecraft.class_2824
 *  net.minecraft.class_2824$class_5908
 *  net.minecraft.class_2868
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.impl.PacketReceiveEvent;
import net.anubis.client.event.impl.PacketSendEvent;
import net.anubis.client.event.impl.PlayerAttackEntityEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.crystal.Crystals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2716;
import net.minecraft.class_2824;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class CrystalOptimizerModule
extends ClientModule {
    private static final int SAMPLE_CAPACITY = 64;
    private final class_1799[] heldSamples = new class_1799[64];
    private final int[] sampleTicks = new int[64];
    private int sampleCursor;
    private int sampleCount;
    private int ticks;
    private int trackedSince;
    private class_746 trackedPlayer;
    private class_638 trackedLevel;
    private boolean attackSent;
    private class_1511 pendingRemoval;
    private final AttackProbe attackProbe = new AttackProbe();
    private final Int2ObjectMap<Ghost> ghosts = new Int2ObjectOpenHashMap();
    private final IntSet stubborn = new IntOpenHashSet();
    private final IntSet serverRemoved = new IntOpenHashSet();

    public CrystalOptimizerModule() {
        super("crystal-optimizer", "Crystal Optimizer", "Removes crystals client-side as soon as your hit breaks them.", ModuleCategory.COMBAT);
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    protected void onDisable() {
        class_638 level = class_310.method_1551().field_1687;
        if (level != null && level == this.trackedLevel) {
            for (Ghost ghost : this.ghosts.values()) {
                CrystalOptimizerModule.revive(level, ghost.crystal());
            }
        }
        this.reset();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onTick(TickEvent event) {
        this.attackSent = false;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        class_638 level = mc.field_1687;
        if (player == null || level == null) {
            if (this.trackedPlayer != null || this.sampleCount > 0) {
                this.reset();
            }
            return;
        }
        if (player != this.trackedPlayer || level != this.trackedLevel) {
            this.reset();
            this.trackedPlayer = player;
            this.trackedLevel = level;
        }
        if (this.pendingRemoval != null && !this.pendingRemoval.method_31481()) {
            level.method_2945(this.pendingRemoval.method_5628(), class_1297.class_5529.field_26999);
            this.ghosts.put(this.pendingRemoval.method_5628(), (Object)new Ghost(this.pendingRemoval, this.ticks));
        }
        this.pendingRemoval = null;
        ++this.ticks;
        this.serverRemoved.removeIf(id -> level.method_8469(id) == null);
        this.sample(player.method_6047());
        this.restoreGhosts(mc, player, level);
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        IntListIterator intListIterator = event.packet;
        if (!(intListIterator instanceof class_2716)) {
            return;
        }
        class_2716 remove = (class_2716)intListIterator;
        intListIterator = remove.method_36548().iterator();
        while (intListIterator.hasNext()) {
            int id = (Integer)intListIterator.next();
            this.ghosts.remove(id);
            this.stubborn.remove(id);
            if (this.pendingRemoval != null && this.pendingRemoval.method_5628() == id) {
                this.pendingRemoval = null;
            }
            if (this.trackedLevel == null || !(this.trackedLevel.method_8469(id) instanceof class_1511)) continue;
            this.serverRemoved.add(id);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPacketSend(PacketSendEvent event) {
        class_310 mc = class_310.method_1551();
        if (!mc.method_18854()) {
            return;
        }
        class_2596<?> class_25962 = event.packet;
        if (class_25962 instanceof class_2824) {
            class_2824 interact = (class_2824)class_25962;
            this.attackSent = this.attackProbe.isAttack(interact);
        } else {
            class_25962 = event.packet;
            if (class_25962 instanceof class_2868) {
                class_2868 carried = (class_2868)class_25962;
                class_746 player = mc.field_1724;
                if (player != null && player == this.trackedPlayer && class_1661.method_7380((int)carried.method_12442())) {
                    this.sample(player.method_31548().method_5438(carried.method_12442()));
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onAttack(PlayerAttackEntityEvent event) {
        class_1297 class_12972;
        boolean sent = this.attackSent;
        this.attackSent = false;
        if (!sent || !((class_12972 = event.target) instanceof class_1511)) {
            return;
        }
        class_1511 crystal = (class_1511)class_12972;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        class_638 level = mc.field_1687;
        if (player == null || level == null || player != this.trackedPlayer || crystal.method_73183() != level) {
            return;
        }
        if (this.stubborn.contains(crystal.method_5628()) || this.serverRemoved.contains(crystal.method_5628()) || !this.breaksOnServer(mc, player, level, crystal)) {
            return;
        }
        this.pendingRemoval = crystal;
    }

    private void restoreGhosts(class_310 mc, class_746 player, class_638 level) {
        if (this.ghosts.isEmpty()) {
            return;
        }
        int window = Crystals.confirmWindow(mc, player);
        ObjectIterator iterator = this.ghosts.values().iterator();
        while (iterator.hasNext()) {
            Ghost ghost = (Ghost)iterator.next();
            if (this.ticks - ghost.removedAt() <= window) continue;
            iterator.remove();
            if (!CrystalOptimizerModule.revive(level, ghost.crystal())) continue;
            this.stubborn.add(ghost.crystal().method_5628());
        }
    }

    private static boolean revive(class_638 level, class_1511 old) {
        if (level.method_8469(old.method_5628()) != null) {
            return false;
        }
        class_1511 copy = new class_1511((class_1937)level, old.method_23317(), old.method_23318(), old.method_23321());
        copy.method_5838(old.method_5628());
        copy.method_5826(old.method_5667());
        copy.method_6839(old.method_6836());
        level.method_53875((class_1297)copy);
        return true;
    }

    private boolean breaksOnServer(class_310 mc, class_746 player, class_638 level, class_1511 crystal) {
        if (crystal.method_31481()) {
            return false;
        }
        if (player.method_7325() || player.method_29504() || player.method_6123()) {
            return false;
        }
        class_1799 weapon = player.method_6047();
        if (!weapon.method_45435(level.method_45162()) || player.method_75202(weapon, 0)) {
            return false;
        }
        if (!level.method_8621().method_11952(crystal.method_24515())) {
            return false;
        }
        if (!player.method_76729(crystal.method_5829(), 0.0)) {
            return false;
        }
        return Crystals.enchantsAddDamage(weapon) || this.recentHandsDealDamage(mc, player, weapon);
    }

    private boolean recentHandsDealDamage(class_310 mc, class_746 player, class_1799 held) {
        int index;
        int walked;
        if (!Crystals.dealsDamage((class_1309)player, held)) {
            return false;
        }
        int window = Crystals.confirmWindow(mc, player);
        boolean complete = this.ticks - this.trackedSince >= window;
        class_1799 checked = held;
        for (walked = 0; walked < this.sampleCount && this.ticks - this.sampleTicks[index = Math.floorMod(this.sampleCursor - 1 - walked, 64)] <= window; ++walked) {
            class_1799 sample = this.heldSamples[index];
            if (sample == checked) continue;
            if (!Crystals.dealsDamage((class_1309)player, sample)) {
                return false;
            }
            checked = sample;
        }
        if (walked == 64) {
            complete = false;
        }
        return complete || Crystals.dealsDamage((class_1309)player, class_1799.field_8037);
    }

    private void sample(class_1799 stack) {
        this.heldSamples[this.sampleCursor] = stack;
        this.sampleTicks[this.sampleCursor] = this.ticks;
        this.sampleCursor = (this.sampleCursor + 1) % 64;
        if (this.sampleCount < 64) {
            ++this.sampleCount;
        }
    }

    private void reset() {
        Arrays.fill(this.heldSamples, null);
        this.sampleCursor = 0;
        this.sampleCount = 0;
        this.trackedSince = this.ticks;
        this.trackedPlayer = null;
        this.trackedLevel = null;
        this.attackSent = false;
        this.pendingRemoval = null;
        this.ghosts.clear();
        this.stubborn.clear();
        this.serverRemoved.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class AttackProbe
    implements class_2824.class_5908 {
        private boolean attack;

        private AttackProbe() {
        }

        boolean isAttack(class_2824 packet) {
            this.attack = false;
            packet.method_34209((class_2824.class_5908)this);
            return this.attack;
        }

        public void method_34219(class_1268 hand) {
        }

        public void method_34220(class_1268 hand, class_243 location) {
        }

        public void method_34218() {
            this.attack = true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Ghost(class_1511 crystal, int removedAt) {
    }
}

