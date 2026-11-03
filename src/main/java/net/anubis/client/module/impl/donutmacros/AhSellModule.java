/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.donutmacros.AhSellBot;
import net.anubis.client.module.impl.donutmacros.AuctionHouse;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.Product;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringSetting;
import net.anubis.client.util.ItemEnchants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AhSellModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int SLOTS = 9;
    private static final String FIXED = "Fixed";
    private static final String AUTO = "Auto (beat cheapest)";
    private final ModeSetting count = this.addSetting("items", new ModeSetting("Items", "1", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
    private final ModeSetting pricing = this.addSetting("pricing", new ModeSetting("Pricing", "Fixed", "Fixed", "Auto (beat cheapest)"));
    private final StringSetting[] items = new StringSetting[9];
    private final StringSetting[] enchants = new StringSetting[9];
    private final StringSetting[] prices = new StringSetting[9];
    private final StringSetting undercut;
    private final NumberSetting priceCheck;
    private final NumberSetting relistGap;
    private final NumberSetting ignoreDrops;
    private final NumberSetting salesCheck;
    private final NumberSetting perListing;
    private final NumberSetting maxListings;
    private final NumberSetting listingDelay;
    private final MacroWorker worker = new MacroWorker("Anubis AH Sell");
    private volatile AhSellBot bot;

    public AhSellModule() {
        super("ah-sell", "AH Sell", "Lists up to 9 items from your inventory on the Auction House at your price or the cheapest price.", ModuleCategory.DONUT_MACROS);
        this.usesNotifications();
        for (int i = 0; i < 9; ++i) {
            int slot = i + 1;
            int index = i;
            this.items[i] = this.addSetting("item-" + slot, new StringSetting("Item " + slot, "").hint("e.g. diamond_block").itemPicker());
            this.enchants[i] = this.addSetting("enchants-" + slot, new StringSetting("Enchantments " + slot, "").enchantsFor(this.items[i]));
            this.prices[i] = this.addSetting("price-" + slot, new StringSetting("Price " + slot + " (each)", "").hint("e.g. 5k (Auto: lowest price)"));
            this.items[i].visibleWhen(() -> slot <= this.slotCount());
            this.enchants[i].visibleWhen(() -> slot <= this.slotCount() && ItemEnchants.supports(Product.resolveItem(this.items[index].get())));
            this.prices[i].visibleWhen(() -> slot <= this.slotCount());
        }
        this.undercut = this.addSetting("undercut", new StringSetting("Undercut By", "100").hint("e.g. 100"));
        this.priceCheck = this.addSetting("price-check", new NumberSetting("Price Check (s)", 120.0f, 10.0f, 600.0f).step(10.0f));
        this.relistGap = this.addSetting("relist-gap", new NumberSetting("Relist Higher At (x undercut)", 3.0f, 1.0f, 20.0f).step(0.5f));
        this.ignoreDrops = this.addSetting("ignore-drops", new NumberSetting("Ignore Drops Over (x undercut)", 20.0f, 1.0f, 100.0f).step(1.0f));
        this.salesCheck = this.addSetting("sales-check", new NumberSetting("Sales Check (s)", 20.0f, 5.0f, 300.0f).step(5.0f));
        this.perListing = this.addSetting("per-listing", new NumberSetting("Items Per Listing", 1.0f, 1.0f, 64.0f).step(1.0f));
        this.maxListings = this.addSetting("max-listings", new NumberSetting("Max Listings", 25.0f, 1.0f, 50.0f).step(1.0f));
        this.listingDelay = this.addSetting("listing-delay", new NumberSetting("Listing Delay (s)", 0.0f, 0.0f, 5.0f).step(0.25f));
        this.undercut.visibleWhen(this::auto);
        this.priceCheck.visibleWhen(this::auto);
        this.relistGap.visibleWhen(this::auto);
        this.ignoreDrops.visibleWhen(this::auto);
    }

    private int slotCount() {
        return Integer.parseInt(this.count.get());
    }

    private boolean auto() {
        return this.pricing.is(AUTO);
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        AhSellBot created;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.method_1548() == null) {
            this.info(ModuleChat.body().warn("Join a server first."));
            this.requestDisable();
            return;
        }
        ArrayList<Product> products = new ArrayList<Product>();
        String problem = this.buildProducts(products);
        if (problem != null) {
            this.info(ModuleChat.body().warn(problem));
            this.requestDisable();
            return;
        }
        AuctionHouse.Config config = new AuctionHouse.Config(Math.max(0L, ServerText.parseMoney(this.undercut.get())), (long)(this.priceCheck.get() * 1000.0f), (long)(this.salesCheck.get() * 1000.0f), this.perListing.getInt(), this.maxListings.getInt(), (long)(this.listingDelay.get() * 1000.0f), this.relistGap.get(), this.ignoreDrops.get(), 36, 15000L);
        this.bot = created = new AhSellBot(this, this.worker, products, !this.auto(), config, mc.method_1548().method_1676());
        this.worker.start(created::run, failure -> {
            LOGGER.error("[Anubis] AH Sell stopped", failure);
            this.info(ModuleChat.body().danger("Stopped").text(": " + String.valueOf(failure)));
            mc.execute(() -> {
                if (this.bot == created) {
                    this.setEnabled(false);
                }
            });
        });
    }

    @Override
    protected void onDisable() {
        AhSellBot stopped = this.bot;
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
            this.info(ModuleChat.body().text(stopped.summary()));
        }
    }

    @EventHandler
    public void onPacket(PacketReceiveNettyEvent event) {
        class_7439 chat;
        class_2596<?> class_25962;
        AhSellBot current = this.bot;
        if (current != null && (class_25962 = event.packet) instanceof class_7439 && (chat = (class_7439)class_25962).comp_763() != null) {
            current.onChat(chat.comp_763().getString());
        }
    }

    private String buildProducts(List<Product> out) {
        if (!this.auto() || ServerText.parseMoney(this.undercut.get()) >= 0L) {
            HashSet<class_1792> seen = new HashSet<class_1792>();
            for (int i = 0; i < this.slotCount(); ++i) {
                String raw = this.items[i].get().trim();
                if (raw.isEmpty()) continue;
                class_1792 item = Product.resolveItem(raw);
                if (item == null) {
                    return "Item " + (i + 1) + ": unknown item '" + raw + "'.";
                }
                if (!seen.add(item)) {
                    return "Item " + (i + 1) + ": " + raw + " is listed twice.";
                }
                long price = ServerText.parseMoney(this.prices[i].get());
                if (price <= 0L) {
                    return "Item " + (i + 1) + ": set a price like 5k" + (this.auto() ? " (the lowest price Auto may use)." : ".");
                }
                List<Product.Enchant> wanted = ItemEnchants.supports(item) ? Product.parseEnchants(this.enchants[i].get()) : List.of();
                out.add(new Product(item, raw, Product.searchText(item), new class_1799((class_1935)item).method_7964().getString(), price, wanted, false, 1, true, 1L, 1L, 0L));
            }
            return out.isEmpty() ? "Set at least one item." : null;
        }
        return "Undercut By must be a price like 100.";
    }

    private void info(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }
}

