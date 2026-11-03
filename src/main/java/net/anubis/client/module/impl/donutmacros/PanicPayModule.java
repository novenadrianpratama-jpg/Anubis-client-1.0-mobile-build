/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_465
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.donutmacros.AuctionHouse;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.Gui;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.security.PayGuard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_465;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class PanicPayModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Pattern VALID_NAME = Pattern.compile("\\.?[A-Za-z0-9_]{1,16}");
    private static final Pattern RATE_LIMIT = Pattern.compile("^you need to wait another ([\\d.]+) seconds?");
    private static final double MAX_WAIT_SECONDS = 30.0;
    private static final int INVENTORY_SLOTS = 36;
    private static final int CONFIRM_SLOT = 53;
    private static final int MAX_STRIKES = 2;
    private static final int MAX_SELL_ROUNDS = 6;
    private static final int[] ARMOR_MENU_SLOTS = new int[]{5, 6, 7, 8};
    private static final int OFFHAND_MENU_SLOT = 45;
    private static final long COMMAND_GAP_MS = 350L;
    private static final long OPEN_TIMEOUT_MS = 8000L;
    private static final long REPLY_TIMEOUT_MS = 6000L;
    private static final long MENU_SETTLE_MS = 50L;
    private static final long CONFIRM_WAIT_MS = 700L;
    private static final long RECONFIRM_WAIT_MS = 900L;
    private static final long RETURN_WAIT_MS = 600L;
    private final StringSetting payTo = this.addSetting("pay-to", new StringSetting("Pay To", "").hint("Player name"));
    private final BooleanSetting sellArmor = this.addSetting("sell-armor", new BooleanSetting("Sell Armor", true));
    private final BooleanSetting sellOffhand = this.addSetting("sell-offhand", new BooleanSetting("Sell Offhand", true));
    private final MacroWorker worker = new MacroWorker("Anubis Panic Pay");
    private volatile boolean running;
    private volatile long paidAt;
    private volatile String paidText = "";
    private volatile long poorAt;
    private volatile long errorAt;
    private volatile String errorText = "";
    private volatile long holdUntil;
    private volatile long limitedAt;
    private volatile long balanceAt;
    private volatile double balance;
    private volatile String balanceText = "";
    private long lastCommandAt;

    public PanicPayModule() {
        super("panic-pay", "Panic Pay", "Sells your whole inventory and armor with /sell, then pays all your money to one player.", ModuleCategory.DONUT_MACROS);
        this.usesNotifications();
        this.payTo.sessionOnly();
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    public boolean inConfigs() {
        return false;
    }

    @Override
    protected void onEnable() {
        class_310 mc = class_310.method_1551();
        String target = this.payTo.get().trim();
        if (mc.field_1724 == null || mc.method_1562() == null) {
            this.say(ModuleChat.body().warn("Join a server first."));
            this.requestDisable();
            return;
        }
        if (!VALID_NAME.matcher(target).matches()) {
            this.say(ModuleChat.body().warn("Set Pay To to a player name first."));
            this.requestDisable();
            return;
        }
        if (target.equalsIgnoreCase(mc.method_1548().method_1676())) {
            this.say(ModuleChat.body().warn("Pay To is your own name."));
            this.requestDisable();
            return;
        }
        boolean armor = this.sellArmor.get();
        boolean offhand = this.sellOffhand.get();
        this.running = true;
        this.lastCommandAt = 0L;
        this.say(ModuleChat.body().danger("Panic Pay started").text(": selling everything, then paying ").name(target).text(". Press the key again to cancel."));
        this.worker.start(() -> {
            try {
                this.run(new Gui(this.worker), target, armor, offhand);
            }
            catch (AuctionHouse.Problem problem) {
                this.say(ModuleChat.body().warn(problem.getMessage()).text(", stopped."));
            }
            finally {
                this.finish(mc);
            }
        }, failure -> {
            LOGGER.error("[Anubis] Panic Pay stopped", failure);
            this.say(ModuleChat.body().danger("Stopped").text(": " + String.valueOf(failure)));
            this.finish(mc);
        });
    }

    private void finish(class_310 mc) {
        mc.execute(() -> {
            if (this.running && this.isEnabled()) {
                this.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDisable() {
        boolean wasRunning = this.running;
        this.running = false;
        this.worker.stop();
        class_746 player = class_310.method_1551().field_1724;
        if (wasRunning && player != null && player.field_7512 != player.field_7498) {
            player.method_7346();
        }
    }

    @EventHandler
    public void onPacket(PacketReceiveNettyEvent event) {
        class_7439 chat;
        class_2596<?> class_25962;
        if (!this.running || !((class_25962 = event.packet) instanceof class_7439) || (chat = (class_7439)class_25962).comp_763() == null) {
            return;
        }
        String raw = chat.comp_763().getString();
        String line = ServerText.normalize(raw);
        long now = System.currentTimeMillis();
        Matcher wait = RATE_LIMIT.matcher(line);
        if (wait.find()) {
            try {
                this.holdUntil = now + (long)Math.ceil(Math.min(30.0, Double.parseDouble(wait.group(1))) * 1000.0) + 100L;
                this.limitedAt = now;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            return;
        }
        if (line.startsWith("you paid ")) {
            this.paidText = line;
            this.paidAt = now;
        } else if (line.contains("enough money")) {
            this.poorAt = now;
        } else if (line.startsWith("you have $") || line.startsWith("balance") && line.contains("$")) {
            String shown = line.substring(line.indexOf(36));
            List<Double> values = ServerText.moneyAll(shown);
            String token = ServerText.moneyToken(shown);
            if (!values.isEmpty() && token != null) {
                this.balance = values.get(0);
                this.balanceText = token;
                this.balanceAt = now;
            }
        } else if (line.contains("not online") || line.contains("not found") || line.contains("cannot pay") || line.contains("can't pay") || line.contains("not a valid") || line.contains("invalid") || line.contains("doesn't exist") || line.contains("does not exist")) {
            this.errorText = raw;
            this.errorAt = now;
        }
    }

    private void run(Gui gui, String target, boolean armor, boolean offhand) {
        gui.closeAll();
        HashMap<class_1792, Integer> strikes = new HashMap<class_1792, Integer>();
        int sold = 0;
        try {
            if (armor || offhand) {
                this.takeOff(gui, armor, offhand, true);
            }
            sold = this.sellRounds(gui, strikes);
            if ((armor || offhand) && this.takeOff(gui, armor, offhand, false)) {
                sold += this.sellRounds(gui, strikes);
            }
        }
        catch (AuctionHouse.Problem problem) {
            this.say(ModuleChat.body().warn(problem.getMessage()).text(", paying what you have."));
            gui.closeAll();
        }
        this.say(ModuleChat.body().text("Sold ").value(sold).text(sold == 1 ? " item" : " items").text(", paying ").name(target).text("."));
        this.payBalance(target);
    }

    private int sellRounds(Gui gui, Map<class_1792, Integer> strikes) {
        Map before;
        int total = 0;
        for (int round = 0; round < 6 && !(before = this.worker.call(() -> PanicPayModule.inventoryCounts(strikes))).isEmpty(); ++round) {
            this.openSell(gui);
            int moved = this.worker.call(() -> {
                class_746 player = Gui.player();
                ArrayList<Integer> slots = new ArrayList<Integer>();
                for (class_1735 slot : player.field_7512.field_7761) {
                    if (!(slot.field_7871 instanceof class_1661) || !slot.method_7681() || slot.method_34266() >= 36 || strikes.getOrDefault(slot.method_7677().method_7909(), 0) >= 2) continue;
                    slots.add(slot.field_7874);
                }
                Iterator iterator = slots.iterator();
                while (iterator.hasNext()) {
                    int index = (Integer)iterator.next();
                    Gui.mc().field_1761.method_2906(player.field_7512.field_7763, index, 0, class_1713.field_7794, (class_1657)player);
                }
                return slots.size();
            });
            this.worker.ticks(2);
            Map<class_1792, Integer> offered = moved > 0 ? this.worker.call(PanicPayModule::waitingItems) : Map.of();
            Map<class_1792, Integer> left = offered.isEmpty() ? offered : this.confirm(gui, offered);
            gui.closeAll();
            if (left == null) {
                this.worker.sleep(600L);
            } else if (!left.isEmpty()) {
                int expected = PanicPayModule.total(before) - PanicPayModule.total(offered) + PanicPayModule.total(left);
                this.worker.waitFor(() -> PanicPayModule.total(PanicPayModule.inventoryCounts(strikes)) >= expected, 600L);
            }
            Map after = this.worker.call(() -> PanicPayModule.inventoryCounts(strikes));
            int soldNow = 0;
            for (Map.Entry entry : before.entrySet()) {
                soldNow += Math.max(0, (Integer)entry.getValue() - after.getOrDefault(entry.getKey(), 0));
            }
            total += soldNow;
            for (Map.Entry entry : before.entrySet()) {
                if (after.getOrDefault(entry.getKey(), 0) < (Integer)entry.getValue()) continue;
                int strike = soldNow > 0 ? 2 : strikes.getOrDefault(entry.getKey(), 0) + 1;
                strikes.put((class_1792)entry.getKey(), strike);
                if (strike != 2) continue;
                this.say(ModuleChat.body().warn("Could not sell ").name(new class_1799((class_1935)entry.getKey()).method_7964().getString()).text(", skipping it."));
            }
        }
        return total;
    }

    private Map<class_1792, Integer> confirm(Gui gui, Map<class_1792, Integer> offered) {
        Map left = offered;
        for (int attempt = 0; attempt < 2 && left.equals(offered); ++attempt) {
            if (!this.worker.call(PanicPayModule::sellMenuOpen).booleanValue()) {
                return null;
            }
            gui.click(53, 0, class_1713.field_7790);
            Map shown = left;
            this.worker.waitFor(() -> !PanicPayModule.sellMenuOpen() || !PanicPayModule.waitingItems().equals(shown), attempt == 0 ? 700L : 900L);
            left = this.worker.call(() -> PanicPayModule.sellMenuOpen() ? PanicPayModule.waitingItems() : null);
            if (left != null) continue;
            return null;
        }
        return left;
    }

    private static Map<class_1792, Integer> waitingItems() {
        HashMap<class_1792, Integer> items = new HashMap<class_1792, Integer>();
        for (class_1735 slot : Gui.player().field_7512.field_7761) {
            if (slot.field_7871 instanceof class_1661 || slot.field_7874 == 53 || !slot.method_7681()) continue;
            items.merge(slot.method_7677().method_7909(), slot.method_7677().method_7947(), Integer::sum);
        }
        return items;
    }

    private static int total(Map<class_1792, Integer> counts) {
        int sum = 0;
        for (int count : counts.values()) {
            sum += count;
        }
        return sum;
    }

    private static Map<class_1792, Integer> inventoryCounts(Map<class_1792, Integer> strikes) {
        HashMap<class_1792, Integer> counts = new HashMap<class_1792, Integer>();
        class_1661 inventory = Gui.player().method_31548();
        for (int index = 0; index < 36; ++index) {
            class_1799 stack = inventory.method_5438(index);
            if (stack.method_7960() || strikes.getOrDefault(stack.method_7909(), 0) >= 2) continue;
            counts.merge(stack.method_7909(), stack.method_7947(), Integer::sum);
        }
        return counts;
    }

    private void openSell(Gui gui) {
        for (int attempt = 0; attempt < 3; ++attempt) {
            long sent = this.command("sell");
            this.worker.waitFor(() -> PanicPayModule.sellMenuOpen() || this.limitedAt >= sent, 8000L);
            if (this.worker.call(PanicPayModule::sellMenuOpen).booleanValue()) {
                this.worker.sleep(50L);
                return;
            }
            gui.closeAll();
        }
        throw new AuctionHouse.Problem("The sell menu did not open");
    }

    private static boolean sellMenuOpen() {
        class_746 player = Gui.mc().field_1724;
        if (player == null || player.field_7512 == player.field_7498 || !(Gui.mc().field_1755 instanceof class_465)) {
            return false;
        }
        int container = 0;
        for (class_1735 slot : player.field_7512.field_7761) {
            if (slot.field_7871 instanceof class_1661) continue;
            ++container;
        }
        return container > 53;
    }

    private boolean takeOff(Gui gui, boolean armor, boolean offhand, boolean onlyIntoFreeSlots) {
        gui.closeAll();
        boolean moved = this.worker.call(() -> {
            class_746 player = Gui.player();
            ArrayList<Integer> worn = new ArrayList<Integer>();
            if (armor) {
                for (int slot : ARMOR_MENU_SLOTS) {
                    if (!player.field_7498.method_7611(slot).method_7681()) continue;
                    worn.add(slot);
                }
            }
            if (offhand && player.field_7498.method_7611(45).method_7681()) {
                worn.add(45);
            }
            int free = 0;
            for (int index = 0; index < 36; ++index) {
                if (!player.method_31548().method_5438(index).method_7960()) continue;
                ++free;
            }
            int count = onlyIntoFreeSlots ? Math.min(free, worn.size()) : worn.size();
            for (int i = 0; i < count; ++i) {
                Gui.mc().field_1761.method_2906(player.field_7498.field_7763, ((Integer)worn.get(i)).intValue(), 0, class_1713.field_7794, (class_1657)player);
            }
            return count > 0;
        });
        if (!moved) {
            return false;
        }
        gui.closeInventory();
        this.worker.ticks(2);
        return true;
    }

    private void payBalance(String target) {
        String amount = this.readBalance();
        if (this.balance < 1.0) {
            this.say(ModuleChat.body().warn("You have no money to pay."));
            return;
        }
        double step = ServerText.displayStep(this.balanceText);
        int lowered = 0;
        for (int attempt = 0; attempt < 4; ++attempt) {
            Payment result = this.pay(target, amount);
            if (result == Payment.PAID) {
                this.say(ModuleChat.body().good("Paid $" + amount).text(" to ").name(target).text("."));
                if (!PanicPayModule.mentions(this.paidText, target)) {
                    this.say(ModuleChat.body().danger("Check the payment").text(": the server said \"" + this.paidText + "\"."));
                }
                return;
            }
            if (result == Payment.POOR) {
                long lower = (long)Math.floor(this.balance - step * (double)(++lowered));
                if (lowered > 2 || lower < 1L) {
                    throw new AuctionHouse.Problem("/pay " + target + " " + amount + " was refused: not enough money");
                }
                amount = Long.toString(lower);
                continue;
            }
            if (result != Payment.FAILED) continue;
            throw new AuctionHouse.Problem("/pay failed: " + this.errorText);
        }
        throw new AuctionHouse.Problem("No answer to /pay " + target + " " + amount);
    }

    private String readBalance() {
        for (int attempt = 0; attempt < 3; ++attempt) {
            long sent = this.command("bal");
            this.worker.waitFor(() -> this.balanceAt >= sent || this.limitedAt >= sent, 6000L);
            if (this.balanceAt < sent) continue;
            return this.balanceText;
        }
        throw new AuctionHouse.Problem("Could not read your balance from /bal");
    }

    private Payment pay(String target, String amount) {
        long mark = this.command("pay " + target + " " + amount);
        Object[] pressed = new Object[]{null};
        long end = mark + 6000L;
        while (System.currentTimeMillis() < end) {
            if (this.paidAt >= mark) {
                return Payment.PAID;
            }
            if (this.poorAt >= mark) {
                return Payment.POOR;
            }
            if (this.errorAt >= mark) {
                return Payment.FAILED;
            }
            if (this.limitedAt >= mark) {
                return Payment.RETRY;
            }
            boolean otherPlayer = this.worker.call(() -> {
                Dialogs.View view = Dialogs.current(Gui.mc());
                if (view == null || view.dialog() == pressed[0]) {
                    return false;
                }
                Dialogs.Button confirm = view.labelled("confirm", "yes", "pay");
                if (confirm == null) {
                    return false;
                }
                if (PanicPayModule.namesOtherPlayer(view, target)) {
                    Dialogs.close(Gui.mc());
                    pressed[0] = view.dialog();
                    return true;
                }
                if (Dialogs.press(Gui.mc(), view, confirm, Map.of())) {
                    pressed[0] = view.dialog();
                }
                return false;
            });
            if (otherPlayer) {
                this.errorText = "the confirmation named a different player than " + target;
                return Payment.FAILED;
            }
            this.worker.sleep(50L);
        }
        return Payment.RETRY;
    }

    private static boolean mentions(String line, String target) {
        String wanted = target.toLowerCase(Locale.ROOT);
        if (wanted.startsWith(".")) {
            wanted = wanted.substring(1);
        }
        for (String word : line.split("[^a-z0-9_]+")) {
            if (!word.equals(wanted)) continue;
            return true;
        }
        return false;
    }

    private static boolean namesOtherPlayer(Dialogs.View view, String target) {
        String wanted = target.toLowerCase(Locale.ROOT);
        if (wanted.startsWith(".")) {
            wanted = wanted.substring(1);
        }
        ArrayList<String> text = new ArrayList<String>(view.body());
        text.add(view.title());
        for (String line : text) {
            for (String word : line.split("[^a-z0-9_]+")) {
                if (word.length() <= wanted.length() || !word.startsWith(wanted)) continue;
                return true;
            }
        }
        return false;
    }

    private long command(String command) {
        long wait = Math.max(this.holdUntil, this.lastCommandAt + 350L) - System.currentTimeMillis();
        if (wait > 0L) {
            this.worker.sleep(wait);
        }
        long sent = System.currentTimeMillis();
        this.worker.run(() -> {
            PayGuard.allow(command);
            Gui.player().field_3944.method_45730(command);
        });
        this.lastCommandAt = sent;
        return sent;
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Payment {
        PAID,
        POOR,
        RETRY,
        FAILED;

    }
}

