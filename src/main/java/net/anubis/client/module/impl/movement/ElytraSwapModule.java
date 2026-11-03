/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10192
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1713
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1890
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  net.minecraft.class_5134
 *  net.minecraft.class_746
 *  net.minecraft.class_9285
 *  net.minecraft.class_9331
 *  net.minecraft.class_9334
 *  net.minecraft.class_9701
 */
package net.anubis.client.module.impl.movement;

import com.example.client.mixin.AccessorElytraFlyMultiPlayerGameMode;
import com.example.client.mixin.AccessorLocalPlayer;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.KeyInputEvent;
import net.anubis.client.event.impl.MouseInputEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.input.ModuleKeybinds;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.combat.AutoInvTotemModule;
import net.anubis.client.module.impl.movement.elytrafly.ElytraFlyInventory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.KeySetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.pvp.InputControl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_5134;
import net.minecraft.class_746;
import net.minecraft.class_9285;
import net.minecraft.class_9331;
import net.minecraft.class_9334;
import net.minecraft.class_9701;

@Environment(value=EnvType.CLIENT)
public final class ElytraSwapModule
extends ClientModule {
    private static final int[] SOURCE_INDICES = ElytraSwapModule.sourceIndices();
    private static final int MAX_WAIT_TICKS = 20;
    private static final int CHEST_SLOT_ID = 6;
    private static final class_2561 SWAP_FAILED = ModuleChat.body().danger("Swap failed").text(", the item didn't move into the chest slot").build();
    private final KeySetting key = this.addSetting("key", new KeySetting("Key", -1));
    private final BooleanSetting swapBackOnLanding = this.addSetting("swap-back-on-landing", new BooleanSetting("Swap Back on Landing", true));
    private final NumberSetting landingDelay = this.addSetting("landing-delay", new NumberSetting("Landing Delay", 4.0f, 0.0f, 20.0f).step(1.0f));
    private boolean pending;
    private int pendingTicks;
    private boolean armed;
    private boolean glided;
    private int groundTicks;
    private int settleTicks;
    private int storedIndex = -1;
    private class_1792 storedItem;

    public ElytraSwapModule() {
        super("elytra-swap", "Elytra Swap", "Swaps between your chestplate and elytra with one key.", ModuleCategory.MOVEMENT);
        this.usesNotifications();
        this.landingDelay.visibleWhen(this.swapBackOnLanding::get);
    }

    @Override
    protected void onEnable() {
        this.reset();
        this.on(KeyInputEvent.TYPE, Stage.PRE, EventPriority.HIGH, this::onKey);
        this.on(MouseInputEvent.TYPE, Stage.PRE, EventPriority.HIGH, this::onMouse);
        this.on(TickEvent.TYPE, Stage.PRE, EventPriority.LOWEST, this::onTick);
    }

    @Override
    protected void onDisable() {
        this.reset();
    }

    private void reset() {
        this.pending = false;
        this.pendingTicks = 0;
        this.disarm();
    }

    private void disarm() {
        this.armed = false;
        this.glided = false;
        this.groundTicks = 0;
        this.settleTicks = 0;
        this.storedIndex = -1;
        this.storedItem = null;
    }

    private void onKey(KeyInputEvent event) {
        if (event.action == 1) {
            this.onPress(event.key);
        }
    }

    private void onMouse(MouseInputEvent event) {
        if (event.action == 1) {
            this.onPress(1000 + event.button);
        }
    }

    private void onPress(int pressed) {
        int bound = this.key.value();
        if (!ModuleKeybinds.isBindable(bound) || pressed != bound) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1755 != null || mc.field_1724 == null) {
            return;
        }
        this.pending = true;
        this.pendingTicks = 0;
    }

    private void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null) {
            this.reset();
            return;
        }
        if (this.pending) {
            String busy = this.busyReason(player);
            if (busy == null) {
                if (mc.field_1755 != null || this.swap(mc, player)) {
                    this.pending = false;
                    return;
                }
                busy = "you kept moving";
            }
            if (++this.pendingTicks > 20) {
                this.pending = false;
                ModuleChat.send((ClientModule)this, ModuleChat.body().warn("Swap skipped").text(", " + busy));
            }
            return;
        }
        if (this.armed) {
            this.followFlight(mc, player);
        }
    }

    private String busyReason(class_746 player) {
        if (CombatMacroLock.heldByOther(this)) {
            return "another macro kept the inventory busy";
        }
        if (AutoInvTotemModule.busy()) {
            return "the totem refill kept the inventory busy";
        }
        if (player.method_6115()) {
            return "you kept using an item";
        }
        if (((AccessorLocalPlayer)player).anubis$lastSentInput().comp_3164()) {
            return "you kept sneaking";
        }
        return null;
    }

    private void followFlight(class_310 mc, class_746 player) {
        if (!this.swapBackOnLanding.get() || !player.method_6118(class_1304.field_6174).method_57826(class_9334.field_54197)) {
            this.disarm();
            return;
        }
        if (player.method_6128()) {
            this.glided = true;
            this.groundTicks = 0;
            return;
        }
        if (!this.glided) {
            return;
        }
        if (!player.method_24828() && !player.method_5799()) {
            this.groundTicks = 0;
            return;
        }
        if (this.groundTicks++ < this.landingDelay.getInt()) {
            return;
        }
        if (mc.field_1755 != null || !ElytraSwapModule.inventoryReady(player) || this.busyReason(player) != null) {
            return;
        }
        if (!ElytraSwapModule.isBound(player, player.method_6118(class_1304.field_6174))) {
            int chestplate = this.chestplateToEquip(player);
            if (chestplate == -1) {
                ModuleChat.send((ClientModule)this, ModuleChat.body().danger("Can't swap back").text(", no ").name("chestplate").text(" left in your inventory"));
            } else if (!AutoInvTotemModule.clickSafe(player)) {
                if (++this.settleTicks <= 20) {
                    InputControl.idle();
                    return;
                }
                ModuleChat.send((ClientModule)this, ModuleChat.body().warn("Swap back skipped").text(", you kept moving"));
            } else if (!ElytraSwapModule.equip(mc, player, chestplate)) {
                ModuleChat.send((ClientModule)this, SWAP_FAILED);
            }
        }
        this.disarm();
    }

    private boolean swap(class_310 mc, class_746 player) {
        class_1792 takenOff;
        if (player.method_7325()) {
            return true;
        }
        if (!ElytraSwapModule.inventoryReady(player)) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().warn("Swap skipped").text(", close the open container first"));
            return true;
        }
        class_1799 worn = player.method_6118(class_1304.field_6174);
        if (ElytraSwapModule.isBound(player, worn)) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().danger("Can't swap").text(", your chest piece has ").name("Curse of Binding"));
            return true;
        }
        if (worn.method_57826(class_9334.field_54197)) {
            int chestplate = this.chestplateToEquip(player);
            if (chestplate == -1) {
                ModuleChat.send((ClientModule)this, ModuleChat.body().danger("Can't swap").text(", no ").name("chestplate").text(" in your inventory"));
            } else {
                if (!AutoInvTotemModule.clickSafe(player)) {
                    InputControl.idle();
                    return false;
                }
                if (!ElytraSwapModule.equip(mc, player, chestplate)) {
                    ModuleChat.send((ClientModule)this, SWAP_FAILED);
                }
            }
            this.disarm();
            return true;
        }
        int elytra = ElytraSwapModule.findElytra(player);
        if (elytra == -1) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().danger("Can't swap").text(", no usable ").name("elytra").text(" in your inventory"));
            return true;
        }
        if (!AutoInvTotemModule.clickSafe(player)) {
            InputControl.idle();
            return false;
        }
        class_1792 class_17922 = takenOff = ElytraSwapModule.isChestplate(worn) ? worn.method_7909() : null;
        if (!ElytraSwapModule.equip(mc, player, elytra)) {
            ModuleChat.send((ClientModule)this, SWAP_FAILED);
            return true;
        }
        this.disarm();
        if (takenOff != null) {
            this.armed = this.swapBackOnLanding.get();
            this.storedIndex = elytra;
            this.storedItem = takenOff;
        }
        return true;
    }

    private int chestplateToEquip(class_746 player) {
        class_1799 stored;
        if (this.storedIndex >= 0 && this.storedItem != null && (stored = player.method_31548().method_5438(this.storedIndex)).method_31574(this.storedItem) && !ElytraSwapModule.isBound(player, stored)) {
            return this.storedIndex;
        }
        return ElytraSwapModule.findBestChestplate(player);
    }

    private static boolean equip(class_310 mc, class_746 player, int index) {
        class_1792 item = player.method_31548().method_5438(index).method_7909();
        ((AccessorElytraFlyMultiPlayerGameMode)mc.field_1761).anubis$elytraFlySyncSelected();
        if (class_1661.method_7380((int)index) || index == 40) {
            mc.field_1761.method_2906(player.field_7498.field_7763, 6, index, class_1713.field_7791, (class_1657)player);
        } else {
            ElytraFlyInventory.moveToChest(index);
        }
        player.field_3944.method_52787((class_2596)new class_2815(0));
        return player.method_6118(class_1304.field_6174).method_31574(item);
    }

    private static boolean inventoryReady(class_746 player) {
        return player.field_7512 == player.field_7498 && player.field_7512.method_34255().method_7960();
    }

    private static boolean isBound(class_746 player, class_1799 stack) {
        return !player.method_68878() && class_1890.method_60142((class_1799)stack, (class_9331)class_9701.field_51656);
    }

    private static int findElytra(class_746 player) {
        class_1661 inventory = player.method_31548();
        int best = -1;
        int bestLeft = -1;
        for (int index : SOURCE_INDICES) {
            int left;
            class_1799 stack = inventory.method_5438(index);
            if (!class_1309.method_63624((class_1799)stack, (class_1304)class_1304.field_6174) || ElytraSwapModule.isBound(player, stack) || (left = ElytraSwapModule.durabilityLeft(stack)) <= bestLeft) continue;
            best = index;
            bestLeft = left;
        }
        return best;
    }

    private static int findBestChestplate(class_746 player) {
        class_1661 inventory = player.method_31548();
        int best = -1;
        double bestScore = -1.0;
        int bestLeft = -1;
        for (int index : SOURCE_INDICES) {
            class_1799 stack = inventory.method_5438(index);
            if (!ElytraSwapModule.isChestplate(stack) || ElytraSwapModule.isBound(player, stack)) continue;
            double score = ElytraSwapModule.armorScore(stack);
            int left = ElytraSwapModule.durabilityLeft(stack);
            if (!(score > bestScore) && (score != bestScore || left <= bestLeft)) continue;
            best = index;
            bestScore = score;
            bestLeft = left;
        }
        return best;
    }

    private static boolean isChestplate(class_1799 stack) {
        if (stack.method_7960() || stack.method_57826(class_9334.field_54197)) {
            return false;
        }
        class_10192 equippable = (class_10192)stack.method_58694(class_9334.field_54196);
        return equippable != null && equippable.comp_3174() == class_1304.field_6174;
    }

    private static double armorScore(class_1799 stack) {
        class_9285 modifiers = (class_9285)stack.method_58695(class_9334.field_49636, (Object)class_9285.field_49326);
        return modifiers.method_57481(class_5134.field_23724, 0.0, class_1304.field_6174) + modifiers.method_57481(class_5134.field_23725, 0.0, class_1304.field_6174);
    }

    private static int durabilityLeft(class_1799 stack) {
        return stack.method_7963() ? stack.method_7936() - stack.method_7919() : Integer.MAX_VALUE;
    }

    private static int[] sourceIndices() {
        int[] indices = new int[37];
        for (int i = 0; i < 36; ++i) {
            indices[i] = i;
        }
        indices[36] = 40;
        return indices;
    }
}

