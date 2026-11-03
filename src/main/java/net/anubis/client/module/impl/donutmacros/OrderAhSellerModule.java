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

import java.util.List;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.donutmacros.AuctionHouse;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.OrderAhBot;
import net.anubis.client.module.impl.donutmacros.Product;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.Setting;
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
public final class OrderAhSellerModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int PRODUCTS = 5;
    private static final String PRICE_BEAT = "Beat Top Order";
    private static final String PRICE_FIXED = "Fixed";
    private final ModeSetting productSlot = this.addSetting("product", new ModeSetting("Product", "1", "1", "2", "3", "4", "5"));
    private final StringSetting[] items = new StringSetting[5];
    private final StringSetting[] minPrices = new StringSetting[5];
    private final StringSetting[] enchants = new StringSetting[5];
    private final BooleanSetting[] restock = new BooleanSetting[5];
    private final StringSetting[] orderAmounts = new StringSetting[5];
    private final ModeSetting[] priceModes = new ModeSetting[5];
    private final StringSetting[] fixedPrices = new StringSetting[5];
    private final StringSetting[] bidIncrements = new StringSetting[5];
    private final StringSetting[] maxPrices = new StringSetting[5];
    private final StringSetting undercut;
    private final NumberSetting priceCheck;
    private final NumberSetting salesCheck;
    private final NumberSetting perListing;
    private final NumberSetting maxListings;
    private final NumberSetting listingDelay;
    private final NumberSetting relistGap;
    private final NumberSetting ignoreDrops;
    private final NumberSetting collectSlots;
    private final NumberSetting orderCheck;
    private final MacroWorker worker = new MacroWorker("Anubis Order AH Trader");
    private volatile OrderAhBot bot;

    public OrderAhSellerModule() {
        super("order-ah-seller", "Order AH Trader", "Sells an item on the Auction House, undercuts other sellers and restocks through orders.", ModuleCategory.DONUT_MACROS);
        this.usesNotifications();
        for (int i = 0; i < 5; ++i) {
            Setting[] all;
            int slot = i + 1;
            String id = "product-" + slot + "-";
            this.items[i] = this.addSetting(id + "item", new StringSetting("Item", slot == 1 ? "barrel" : "").hint("e.g. barrel or diamond_chestplate").itemPicker());
            this.minPrices[i] = this.addSetting(id + "min-price", new StringSetting("Min Price (each)", slot == 1 ? "3k" : "1k").hint("e.g. 3k"));
            this.enchants[i] = this.addSetting(id + "enchants", new StringSetting("Enchantments", "").enchantsFor(this.items[i]));
            this.restock[i] = this.addSetting(id + "restock", new BooleanSetting("Restock With Orders", false));
            this.orderAmounts[i] = this.addSetting(id + "order-amount", new StringSetting("Order Amount", "64").hint("e.g. 64"));
            this.priceModes[i] = this.addSetting(id + "order-price", new ModeSetting("Order Price", PRICE_BEAT, PRICE_BEAT, PRICE_FIXED));
            this.fixedPrices[i] = this.addSetting(id + "fixed-price", new StringSetting("Fixed Order Price (each)", "100").hint("e.g. 150k"));
            this.bidIncrements[i] = this.addSetting(id + "bid-increment", new StringSetting("Beat By", "1").hint("e.g. 1 or 100"));
            this.maxPrices[i] = this.addSetting(id + "max-price", new StringSetting("Max Order Price (each)", "").hint("Empty = no limit"));
            int index = i;
            for (Setting setting : all = new Setting[]{this.items[i], this.minPrices[i], this.enchants[i], this.restock[i], this.orderAmounts[i], this.priceModes[i], this.fixedPrices[i], this.bidIncrements[i], this.maxPrices[i]}) {
                setting.visibleWhen(() -> this.selected() == index);
            }
            this.enchants[i].visibleWhen(() -> this.selected() == index && ItemEnchants.supports(Product.resolveItem(this.items[index].get())));
            this.orderAmounts[i].visibleWhen(() -> this.selected() == index && this.restock[index].get());
            this.priceModes[i].visibleWhen(() -> this.selected() == index && this.restock[index].get());
            this.fixedPrices[i].visibleWhen(() -> this.selected() == index && this.restock[index].get() && this.priceModes[index].is(PRICE_FIXED));
            this.bidIncrements[i].visibleWhen(() -> this.selected() == index && this.restock[index].get() && this.priceModes[index].is(PRICE_BEAT));
            this.maxPrices[i].visibleWhen(() -> this.selected() == index && this.restock[index].get());
        }
        this.undercut = this.addSetting("undercut", new StringSetting("Undercut By", "100").hint("e.g. 100"));
        this.priceCheck = this.addSetting("price-check", new NumberSetting("Price Check (s)", 120.0f, 10.0f, 600.0f).step(10.0f));
        this.salesCheck = this.addSetting("sales-check", new NumberSetting("Sales Check (s)", 20.0f, 5.0f, 300.0f).step(5.0f));
        this.perListing = this.addSetting("per-listing", new NumberSetting("Items Per Listing", 1.0f, 1.0f, 64.0f).step(1.0f));
        this.maxListings = this.addSetting("max-listings", new NumberSetting("Max Listings", 25.0f, 1.0f, 50.0f).step(1.0f));
        this.listingDelay = this.addSetting("listing-delay", new NumberSetting("Listing Delay (s)", 0.0f, 0.0f, 5.0f).step(0.25f));
        this.relistGap = this.addSetting("relist-gap", new NumberSetting("Relist Higher At (x undercut)", 3.0f, 1.0f, 20.0f).step(0.5f));
        this.ignoreDrops = this.addSetting("ignore-drops", new NumberSetting("Ignore Drops Over (x undercut)", 20.0f, 1.0f, 100.0f).step(1.0f));
        this.collectSlots = this.addSetting("collect-slots", new NumberSetting("Collect Up To (slots)", 18.0f, 1.0f, 36.0f).step(1.0f));
        this.orderCheck = this.addSetting("order-check", new NumberSetting("Order Check (s)", 15.0f, 5.0f, 300.0f).step(5.0f));
        this.collectSlots.visibleWhen(this::anyRestock);
        this.orderCheck.visibleWhen(this::anyRestock);
    }

    private int selected() {
        return Integer.parseInt(this.productSlot.get()) - 1;
    }

    private boolean anyRestock() {
        return this.restock[this.selected()].get();
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        OrderAhBot created;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.method_1548() == null) {
            this.info(ModuleChat.body().warn("Join a server first."));
            this.requestDisable();
            return;
        }
        String problem = this.validate();
        if (problem != null) {
            this.info(ModuleChat.body().warn(problem));
            this.requestDisable();
            return;
        }
        this.bot = created = new OrderAhBot(this, this.worker, this.buildProduct(), this.buildConfig(), mc.method_1548().method_1676());
        this.worker.start(created::run, failure -> {
            LOGGER.error("[Anubis] Order AH Trader stopped", failure);
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
        OrderAhBot stopped = this.bot;
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
        OrderAhBot current = this.bot;
        if (current != null && (class_25962 = event.packet) instanceof class_7439 && (chat = (class_7439)class_25962).comp_763() != null) {
            current.onChat(chat.comp_763().getString());
        }
    }

    private String validate() {
        int i = this.selected();
        class_1792 item = Product.resolveItem(this.items[i].get());
        if (item == null) {
            return "Product " + (i + 1) + ": unknown item '" + this.items[i].get() + "'.";
        }
        if (ServerText.parseMoney(this.minPrices[i].get()) <= 0L) {
            return "Product " + (i + 1) + ": Min Price must be a price like 3k.";
        }
        if (ServerText.parseMoney(this.undercut.get()) < 0L) {
            return "Undercut By must be a price like 100.";
        }
        if (!this.restock[i].get()) {
            return null;
        }
        if (OrderAhSellerModule.parseAmount(this.orderAmounts[i].get()) <= 0) {
            return "Product " + (i + 1) + ": Order Amount must be a whole number.";
        }
        if (this.priceModes[i].is(PRICE_FIXED) && ServerText.parseMoney(this.fixedPrices[i].get()) <= 0L) {
            return "Product " + (i + 1) + ": Fixed Order Price must be a price like 150k.";
        }
        if (this.priceModes[i].is(PRICE_BEAT) && ServerText.parseMoney(this.bidIncrements[i].get()) <= 0L) {
            return "Product " + (i + 1) + ": Beat By must be a price like 1.";
        }
        if (!this.maxPrices[i].get().isBlank() && ServerText.parseMoney(this.maxPrices[i].get()) <= 0L) {
            return "Product " + (i + 1) + ": Max Order Price must be empty or a price like 200k.";
        }
        return null;
    }

    private Product buildProduct() {
        int i = this.selected();
        class_1792 item = Product.resolveItem(this.items[i].get());
        long max = this.maxPrices[i].get().isBlank() ? 0L : ServerText.parseMoney(this.maxPrices[i].get());
        return new Product(item, this.items[i].get().trim(), Product.searchText(item), new class_1799((class_1935)item).method_7964().getString(), ServerText.parseMoney(this.minPrices[i].get()), ItemEnchants.supports(item) ? Product.parseEnchants(this.enchants[i].get()) : List.of(), this.restock[i].get(), OrderAhSellerModule.parseAmount(this.orderAmounts[i].get()), this.priceModes[i].is(PRICE_BEAT), Math.max(1L, ServerText.parseMoney(this.fixedPrices[i].get())), Math.max(1L, ServerText.parseMoney(this.bidIncrements[i].get())), Math.max(0L, max));
    }

    private AuctionHouse.Config buildConfig() {
        return new AuctionHouse.Config(Math.max(0L, ServerText.parseMoney(this.undercut.get())), (long)(this.priceCheck.get() * 1000.0f), (long)(this.salesCheck.get() * 1000.0f), this.perListing.getInt(), this.maxListings.getInt(), (long)(this.listingDelay.get() * 1000.0f), this.relistGap.get(), this.ignoreDrops.get(), this.collectSlots.getInt(), (long)(this.orderCheck.get() * 1000.0f));
    }

    private static int parseAmount(String raw) {
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : -1;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    private void info(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }
}

