/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10185
 *  net.minecraft.class_1263
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1802
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2596
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_437
 *  net.minecraft.class_465
 *  net.minecraft.class_490
 *  net.minecraft.class_634
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.combat;

import com.example.client.mixin.AccessorElytraFlyMultiPlayerGameMode;
import com.example.client.mixin.AccessorLocalPlayer;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.CombatMacroLock;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.combat.AutoTotemModule;
import net.anubis.client.module.impl.combat.totem.DoubleHand;
import net.anubis.client.module.impl.movement.SneakModule;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10185;
import net.minecraft.class_1263;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_490;
import net.minecraft.class_634;
import net.minecraft.class_640;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class AutoInvTotemModule
extends ClientModule {
    private static final String INVENTORY = "Inventory";
    private static final String HOTBAR_SWAP = "Hotbar Swap";
    private static final int MIN_CONFIRM_TICKS = 10;
    private static final int MAX_CONFIRM_TICKS = 40;
    private static final int PASS_COOLDOWN_TICKS = 4;
    private static final int GIVE_UP_COOLDOWN_TICKS = 10;
    private static final int MAX_PASS_SWAPS = 2;
    private static final int MAX_RESELECTS = 2;
    private static final int MAX_STEPS_PER_TICK = 3;
    private static final int MAX_UNSAFE_TICKS = 10;
    private static AutoInvTotemModule instance;
    private final ModeSetting mode = this.addSetting("mode", new ModeSetting("Mode", "Inventory", "Inventory", "Hotbar Swap"));
    private final BooleanSetting hotbarRefill = this.addSetting("hotbar-refill", new BooleanSetting("Hotbar Refill", false));
    private final NumberSetting hotbarSlot = this.addSetting("hotbar-slot", new NumberSetting("Hotbar Slot", 1.0f, 1.0f, 9.0f).step(1.0f));
    private final NumberSetting reactionDelay = this.addSetting("reaction-delay", new NumberSetting("Reaction Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting swapDelay = this.addSetting("swap-delay", new NumberSetting("Swap Delay", 1.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting closeDelay = this.addSetting("close-delay", new NumberSetting("Close Delay", 0.0f, 0.0f, 10.0f).step(1.0f));
    private final NumberSetting switchBackDelay = this.addSetting("switch-back-delay", new NumberSetting("Switch Back Delay", 2.0f, 0.0f, 10.0f).step(1.0f));
    private final BooleanSetting doubleHand = this.addSetting("double-hand", new BooleanSetting("Double Hand", false));
    private final DoubleHand totemHand = new DoubleHand();
    private Step step = Step.IDLE;
    private int tick;
    private int readyAt;
    private int reactionAt = -1;
    private int idleUntil;
    private int confirmDeadline = -1;
    private int clickedAt = -1;
    private int swappedAt = -1;
    private boolean openedInventory;
    private int passSwaps;
    private int unsafeTicks;
    private int totemSlot = -1;
    private int previousSlot = -1;
    private int reselects;

    public AutoInvTotemModule() {
        super("autoinvtotem", "Auto Inventory Totem", "Refills the offhand totem after a pop through the inventory or a hotbar swap; Double Hand also keeps one in the main hand while in danger.", ModuleCategory.COMBAT);
        instance = this;
        this.hotbarSlot.visibleWhen(this.hotbarRefill::get);
        this.switchBackDelay.visibleWhen(() -> this.mode.is(HOTBAR_SWAP));
    }

    public static boolean wantsHotbar() {
        AutoInvTotemModule module = instance;
        if (module == null || !module.isEnabled()) {
            return AutoTotemModule.wantsHotbar();
        }
        return module.step == Step.HOTBAR_SWAP || module.step == Step.HOTBAR_RETURN || module.totemHand.holding();
    }

    public static boolean busy() {
        AutoInvTotemModule module = instance;
        if (module == null || !module.isEnabled()) {
            return AutoTotemModule.busy();
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || player.method_7325() || player.method_56992()) {
            return false;
        }
        class_1661 inventory = player.method_31548();
        return module.step != Step.IDLE || module.totemHand.holding() || module.awaitingServer(player) || module.clickedAt == module.tick || player.method_6079().method_7960() && (AutoInvTotemModule.findStoredTotem(inventory) != -1 || module.hotbarSource(inventory) != -1);
    }

    public static boolean clickSafe(class_746 player) {
        AccessorLocalPlayer sent = (AccessorLocalPlayer)player;
        class_10185 input = sent.anubis$lastSentInput();
        return !sent.anubis$wasSprinting() && !input.comp_3159() && !input.comp_3160() && !input.comp_3161() && !input.comp_3162() && !input.comp_3163() && !input.comp_3164();
    }

    @Override
    protected void onEnable() {
        AutoTotemModule other = ModuleRegistry.get(AutoTotemModule.class);
        if (other != null) {
            other.setEnabled(false);
        }
        this.idleUntil = 0;
    }

    @Override
    protected void onDisable() {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player != null) {
            class_1661 inventory = player.method_31548();
            if (this.openedInventory && mc.field_1755 instanceof class_490) {
                player.method_7346();
            }
            if ((this.step == Step.HOTBAR_SWAP || this.step == Step.HOTBAR_RETURN) && mc.field_1755 == null) {
                this.restoreSlot(inventory);
            }
            this.totemHand.putBack(inventory);
        }
        this.reset();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onTick(TickEvent event) {
        ++this.tick;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null || mc.field_1761 == null || mc.method_1562() == null || player.method_29504() || player.method_7325() || player.method_56992()) {
            if (player != null && this.openedInventory && mc.field_1755 instanceof class_490) {
                player.method_7346();
            }
            this.reset();
            return;
        }
        if (this.step == Step.IDLE) {
            this.tryStart(mc, player);
        }
        if (this.step == Step.IDLE) {
            this.totemHand.tick(mc, player, this.doubleHand.get());
        }
        block6: for (int i = 0; i < 3 && this.step != Step.IDLE && this.tick >= this.readyAt; ++i) {
            switch (this.step.ordinal()) {
                case 1: {
                    this.inventorySwap(mc, player);
                    continue block6;
                }
                case 2: {
                    this.closeInventory(mc, player);
                    continue block6;
                }
                case 3: {
                    this.hotbarSwap(mc, player);
                    continue block6;
                }
                case 4: {
                    this.hotbarReturn(mc, player);
                }
            }
        }
    }

    private void tryStart(class_310 mc, class_746 player) {
        boolean offhandJob;
        class_1661 inventory = player.method_31548();
        if (!player.method_6079().method_7960()) {
            this.confirmDeadline = -1;
        }
        boolean bl = offhandJob = player.method_6079().method_7960() && !this.awaitingServer(player) && (AutoInvTotemModule.findStoredTotem(inventory) != -1 || this.hotbarSource(inventory) != -1);
        if (!offhandJob && !this.refillDue(mc, player)) {
            this.reactionAt = -1;
            return;
        }
        if (this.reactionAt < 0) {
            this.reactionAt = this.tick + this.reactionDelay.getInt();
        }
        if (this.tick < this.reactionAt || this.tick < this.idleUntil) {
            return;
        }
        if (mc.field_1755 instanceof class_465) {
            this.startPass(false);
        } else if (mc.field_1755 == null) {
            boolean canOpen;
            boolean bl2 = canOpen = !mc.field_1761.method_2895() && !AutoInvTotemModule.sneakForced();
            if (offhandJob && this.hotbarSource(inventory) != -1 && (this.mode.is(HOTBAR_SWAP) || !canOpen)) {
                this.startHotbarSwap(inventory);
            } else if (canOpen) {
                mc.method_1507((class_437)new class_490((class_1657)player));
                this.startPass(true);
            } else {
                this.idleUntil = this.tick + 10;
            }
        }
    }

    private boolean refillDue(class_310 mc, class_746 player) {
        class_1661 inventory = player.method_31548();
        if (player.method_6079().method_7960() || !this.refillWanted(inventory) || AutoInvTotemModule.findStoredTotem(inventory) == -1) {
            return false;
        }
        return mc.field_1755 instanceof class_465 || mc.field_1755 == null && AutoInvTotemModule.clickSafe(player) && !this.totemHand.holding() && !CombatMacroLock.heldByOther(this);
    }

    private boolean refillWanted(class_1661 inventory) {
        return this.hotbarRefill.get() && !inventory.method_5438(this.hotbarSlot.getInt() - 1).method_31574(class_1802.field_8288);
    }

    private static boolean sneakForced() {
        SneakModule sneak = SneakModule.active;
        return sneak != null && (sneak.doVanilla() || sneak.doPacket());
    }

    private void startPass(boolean opened) {
        this.openedInventory = opened;
        this.passSwaps = 0;
        this.unsafeTicks = 0;
        this.schedule(Step.INVENTORY_SWAP, opened ? Math.max(1, this.swapDelay.getInt()) : this.swapDelay.getInt());
    }

    private void inventorySwap(class_310 mc, class_746 player) {
        class_465 screen;
        block10: {
            block9: {
                class_437 class_4372 = mc.field_1755;
                if (!(class_4372 instanceof class_465)) break block9;
                screen = (class_465)class_4372;
                if (!this.openedInventory || mc.field_1755 instanceof class_490) break block10;
            }
            this.finishPass();
            return;
        }
        class_1703 menu = screen.method_17577();
        if (!AutoInvTotemModule.clickSafe(player) || !menu.method_34255().method_7960()) {
            if (++this.unsafeTicks > 10) {
                this.schedule(Step.INVENTORY_CLOSE, 0);
            } else {
                this.readyAt = this.tick + 1;
            }
            return;
        }
        int source = this.passSource(player);
        if (source == -1 || this.passSwaps >= 2) {
            this.schedule(Step.INVENTORY_CLOSE, this.closeDelay.getInt());
            return;
        }
        int button = player.method_6079().method_7960() ? 40 : this.hotbarSlot.getInt() - 1;
        menu.method_37418((class_1263)player.method_31548(), source).ifPresent(index -> mc.field_1761.method_2906(menu.field_7763, index, button, class_1713.field_7791, (class_1657)player));
        this.clickedAt = this.tick;
        ++this.passSwaps;
        if (this.passSource(player) != -1 && this.passSwaps < 2) {
            this.schedule(Step.INVENTORY_SWAP, Math.max(1, this.swapDelay.getInt()));
        } else {
            this.schedule(Step.INVENTORY_CLOSE, this.closeDelay.getInt());
        }
    }

    private void closeInventory(class_310 mc, class_746 player) {
        if (this.passSource(player) != -1 && this.passSwaps < 2 && this.unsafeTicks <= 10 && mc.field_1755 instanceof class_465) {
            this.schedule(Step.INVENTORY_SWAP, Math.max(1, this.swapDelay.getInt()));
            return;
        }
        if (this.openedInventory && mc.field_1755 instanceof class_490) {
            if (!AutoInvTotemModule.clickSafe(player) && ++this.unsafeTicks <= 10) {
                this.readyAt = this.tick + 1;
                return;
            }
            player.method_7346();
        }
        this.finishPass();
    }

    private void finishPass() {
        this.idleUntil = this.tick + 4;
        this.endSequence();
    }

    private int passSource(class_746 player) {
        class_1661 inventory = player.method_31548();
        int stored = AutoInvTotemModule.findStoredTotem(inventory);
        if (player.method_6079().method_7960()) {
            return stored != -1 ? stored : this.hotbarSource(inventory);
        }
        return this.refillWanted(inventory) ? stored : -1;
    }

    private void startHotbarSwap(class_1661 inventory) {
        this.totemSlot = this.hotbarSource(inventory);
        this.previousSlot = inventory.method_67532();
        this.reselects = 0;
        inventory.method_61496(this.totemSlot);
        this.schedule(Step.HOTBAR_SWAP, this.swapDelay.getInt());
    }

    private void hotbarSwap(class_310 mc, class_746 player) {
        if (mc.field_1755 != null) {
            this.readyAt = this.tick + 1;
            return;
        }
        if (!player.method_6079().method_7960()) {
            this.schedule(Step.HOTBAR_RETURN, 0);
            return;
        }
        class_1661 inventory = player.method_31548();
        if (inventory.method_67532() != this.totemSlot || !player.method_6047().method_31574(class_1802.field_8288)) {
            int slot = this.hotbarSource(inventory);
            if (slot == -1 || this.reselects++ >= 2) {
                this.idleUntil = this.tick + 10;
                this.schedule(Step.HOTBAR_RETURN, 0);
                return;
            }
            this.totemSlot = slot;
            inventory.method_61496(slot);
            this.schedule(Step.HOTBAR_SWAP, this.swapDelay.getInt());
            return;
        }
        class_634 connection = mc.method_1562();
        ((AccessorElytraFlyMultiPlayerGameMode)mc.field_1761).anubis$elytraFlySyncSelected();
        while (mc.field_1690.field_1831.method_1436()) {
        }
        connection.method_52787((class_2596)new class_2846(class_2846.class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
        this.confirmDeadline = this.tick + AutoInvTotemModule.confirmWindow(connection, player);
        this.swappedAt = this.tick;
        this.schedule(Step.HOTBAR_RETURN, this.switchBackDelay.getInt());
    }

    private void hotbarReturn(class_310 mc, class_746 player) {
        if (mc.field_1755 != null || this.swappedAt == this.tick && player.method_6115() && player.method_6058() == class_1268.field_5808) {
            this.readyAt = this.tick + 1;
            return;
        }
        this.restoreSlot(player.method_31548());
        this.endSequence();
    }

    private void restoreSlot(class_1661 inventory) {
        if (class_1661.method_7380((int)this.previousSlot) && this.previousSlot != this.totemSlot && inventory.method_67532() == this.totemSlot) {
            inventory.method_61496(this.previousSlot);
        }
    }

    private int hotbarSource(class_1661 inventory) {
        int selected = inventory.method_67532();
        int reserved = this.hotbarRefill.get() ? this.hotbarSlot.getInt() - 1 : -1;
        int fallback = -1;
        for (int i = 0; i < 9; ++i) {
            if (i == selected || !inventory.method_5438(i).method_31574(class_1802.field_8288)) continue;
            if (i != reserved) {
                return i;
            }
            fallback = i;
        }
        if (fallback != -1) {
            return fallback;
        }
        return !this.totemHand.holding() && inventory.method_5438(selected).method_31574(class_1802.field_8288) ? selected : -1;
    }

    private static int findStoredTotem(class_1661 inventory) {
        for (int i = 9; i < 36; ++i) {
            if (!inventory.method_5438(i).method_31574(class_1802.field_8288)) continue;
            return i;
        }
        return -1;
    }

    private static int confirmWindow(class_634 connection, class_746 player) {
        class_640 info = connection.method_2871(player.method_5667());
        int latencyMs = info != null ? info.method_2959() : 0;
        return class_3532.method_15340((int)(latencyMs / 25 + 4), (int)10, (int)40);
    }

    private boolean awaitingServer(class_746 player) {
        return this.confirmDeadline >= 0 && this.tick < this.confirmDeadline && !player.method_6079().method_31574(class_1802.field_8288);
    }

    private void schedule(Step next, int ticks) {
        this.step = next;
        this.readyAt = this.tick + ticks;
    }

    private void endSequence() {
        this.step = Step.IDLE;
        this.reactionAt = -1;
        this.openedInventory = false;
        this.passSwaps = 0;
        this.unsafeTicks = 0;
        this.totemSlot = -1;
        this.previousSlot = -1;
        this.reselects = 0;
    }

    private void reset() {
        this.endSequence();
        this.confirmDeadline = -1;
        this.totemHand.reset();
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Step {
        IDLE,
        INVENTORY_SWAP,
        INVENTORY_CLOSE,
        HOTBAR_SWAP,
        HOTBAR_RETURN;

    }
}

