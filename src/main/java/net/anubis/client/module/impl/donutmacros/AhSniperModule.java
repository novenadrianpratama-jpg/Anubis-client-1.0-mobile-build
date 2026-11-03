/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.donutmacros;

import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.impl.donutmacros.SnipeTarget;
import net.anubis.client.module.impl.donutmacros.SniperBot;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AhSniperModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String EXACT = "Exactly these";
    private static final String AT_LEAST = "At least these";
    private final StringSetting item;
    private final StringSetting maxPrice;
    private final StringSetting enchants;
    private final ModeSetting enchantMatch;
    private final NumberSetting minHours;
    private final NumberSetting refresh;
    private final NumberSetting settle;
    private final StringSetting budget;
    private final BooleanSetting requireConfirm;
    private final NumberSetting maxBuys;
    private final BooleanSetting autoSell;
    private final StringSetting sellPrice;
    private final MacroWorker worker;
    private volatile SniperBot bot;

    public AhSniperModule() {
        super("ah-sniper", "AH Sniper", "Refreshes the AH and instantly buys your item below your price, with optional auto selling.", ModuleCategory.DONUT_MACROS);
        this.item = this.addSetting("item", new StringSetting("Item", SnipeTarget.Preset.SHARD_AXE.value).presets(SnipeTarget.Preset.choices()));
        this.maxPrice = this.addSetting("max-price", new StringSetting("Buy Below (each)", "").hint("e.g. 50m"));
        this.enchants = this.addSetting("enchants", new StringSetting("Enchantments", "").hint("e.g. protection 2, unbreaking 3, mending"));
        this.enchantMatch = this.addSetting("enchant-match", new ModeSetting("Enchant Match", EXACT, EXACT, AT_LEAST));
        this.minHours = this.addSetting("min-hours", new NumberSetting("Min Time Left (h)", 0.0f, 0.0f, 48.0f).step(0.5f));
        this.refresh = this.addSetting("refresh", new NumberSetting("Refresh Every (ms)", 300.0f, 100.0f, 2000.0f).step(50.0f));
        this.settle = this.addSetting("settle", new NumberSetting("Wait After Page Change (ms)", 200.0f, 50.0f, 1000.0f).step(25.0f));
        this.budget = this.addSetting("budget", new StringSetting("Max Total Spend", "").hint("e.g. 500m, empty = no limit"));
        this.requireConfirm = this.addSetting("require-confirm", new BooleanSetting("Only Buy With Confirm", false));
        this.maxBuys = this.addSetting("max-buys", new NumberSetting("Stop After (buys, 0 = never)", 0.0f, 0.0f, 100.0f).step(1.0f));
        this.autoSell = this.addSetting("auto-sell", new BooleanSetting("Auto Sell", false));
        this.sellPrice = this.addSetting("sell-price", new StringSetting("Sell Price (each)", "").hint("e.g. 60m"));
        this.worker = new MacroWorker("Anubis AH Sniper");
        this.usesNotifications();
        this.enchantMatch.visibleWhen(() -> !this.enchants.get().isBlank());
        this.sellPrice.visibleWhen(this.autoSell::get);
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        SniperBot created;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.method_1562() == null || mc.method_1548() == null) {
            this.say(ModuleChat.body().warn("Join a server first."));
            this.requestDisable();
            return;
        }
        long max = ServerText.parseMoney(this.maxPrice.get());
        if (max <= 0L) {
            this.say(ModuleChat.body().warn("Set Buy Below to a price like 50m."));
            this.requestDisable();
            return;
        }
        SnipeTarget target = SnipeTarget.parse(this.item.get(), this.enchants.get(), this.enchantMatch.is(EXACT), max, Math.round(this.minHours.get() * 60.0f));
        if (target == null) {
            this.say(ModuleChat.body().warn("Pick an item to snipe first."));
            this.requestDisable();
            return;
        }
        long spendLimit = 0L;
        if (!this.budget.get().isBlank() && (spendLimit = ServerText.parseMoney(this.budget.get())) <= 0L) {
            this.say(ModuleChat.body().warn("Set Max Total Spend to a price like 500m, or leave it empty."));
            this.requestDisable();
            return;
        }
        long sell = 0L;
        if (this.autoSell.get()) {
            sell = ServerText.parseMoney(this.sellPrice.get());
            if (sell <= 0L) {
                this.say(ModuleChat.body().warn("Set Sell Price to a price like 60m, or turn Auto Sell off."));
                this.requestDisable();
                return;
            }
            if (sell <= max) {
                this.say(ModuleChat.body().warn("Sell Price must be higher than Buy Below").text(", or the sniper would buy your own listing back."));
                this.requestDisable();
                return;
            }
        }
        this.bot = created = new SniperBot(this, this::stopFromBot, this.worker, target, this.refresh.getInt(), this.settle.getInt(), spendLimit, this.requireConfirm.get(), this.maxBuys.getInt(), sell, mc.method_1548().method_1676());
        this.worker.start(created::run, failure -> {
            LOGGER.error("[Anubis] AH Sniper stopped", failure);
            this.say(ModuleChat.body().danger("Stopped").text(": " + String.valueOf(failure)));
            this.stopFromBot(created);
        });
    }

    void stopFromBot(SniperBot from) {
        class_310.method_1551().execute(() -> {
            if (this.bot == from && this.isEnabled()) {
                this.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDisable() {
        SniperBot stopped = this.bot;
        this.bot = null;
        this.worker.stop();
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (stopped != null && player != null) {
            if (player.field_7512 != player.field_7498) {
                player.method_7346();
            } else {
                Dialogs.close(mc);
            }
            this.say(ModuleChat.body().text(stopped.summary()));
        }
    }

    @EventHandler
    public void onPacket(PacketReceiveNettyEvent event) {
        class_7439 chat;
        class_2596<?> class_25962;
        SniperBot current = this.bot;
        if (current != null && (class_25962 = event.packet) instanceof class_7439 && (chat = (class_7439)class_25962).comp_763() != null) {
            current.onChat(chat.comp_763().getString());
        }
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }
}

