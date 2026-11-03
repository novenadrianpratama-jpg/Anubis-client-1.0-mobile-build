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
 *  net.minecraft.class_5894
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.function.Predicate;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.ListenerHandle;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.Product;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.impl.donutmacros.SnipeTarget;
import net.anubis.client.module.impl.donutmacros.SniperBot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_5894;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AhPurchase {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private final ClientModule owner;
    private final MacroWorker worker = new MacroWorker("Anubis AH Purchase");
    private final SniperBot bot;
    private final ListenerHandle<PacketReceiveNettyEvent> chat;
    private volatile Status status = Status.RUNNING;

    private AhPurchase(ClientModule owner, SnipeTarget target, int amount, long refreshMs, long budget) {
        this.owner = owner;
        this.bot = SniperBot.quota(owner, this::stopped, this.worker, target, amount, refreshMs, budget);
        this.chat = PacketReceiveNettyEvent.TYPE.listen(this, Stage.PRE, EventPriority.NORMAL, event -> {
            class_7439 packet;
            if (this.status != Status.RUNNING) {
                return;
            }
            class_2596<?> patt0$temp = event.packet;
            if (patt0$temp instanceof class_7439 && (packet = (class_7439)patt0$temp).comp_763() != null) {
                if (packet.comp_906()) {
                    this.bot.onNote(packet.comp_763().getString());
                } else {
                    this.bot.onChat(packet.comp_763().getString());
                }
            } else {
                class_5894 bar;
                class_2596<?> patt1$temp = event.packet;
                if (patt1$temp instanceof class_5894 && (bar = (class_5894)patt1$temp).comp_2279() != null) {
                    this.bot.onNote(bar.comp_2279().getString());
                }
            }
        });
        this.worker.start(this.bot::run, failure -> {
            LOGGER.error("[Anubis] AH purchase stopped", failure);
            ModuleChat.send(owner, ModuleChat.body().danger("AH purchase stopped").text(": " + String.valueOf(failure)));
            this.stopped(this.bot);
        });
    }

    public static AhPurchase start(ClientModule owner, class_1792 item, int amount, long maxPerListing, long refreshMs, long budget) {
        SnipeTarget target = SnipeTarget.matching(item, new class_1799((class_1935)item).method_7964().getString(), Product.searchText(item), stack -> stack.method_31574(item) && !stack.method_57826(class_9334.field_49623) && !stack.method_57826(class_9334.field_49611), maxPerListing);
        return new AhPurchase(owner, target, amount, refreshMs, budget);
    }

    public static AhPurchase startAny(ClientModule owner, class_1792 shown, String name, String search, Predicate<class_1799> filter, int amount, long maxPerListing, long refreshMs, long budget) {
        return new AhPurchase(owner, SnipeTarget.matching(shown, name, search, filter, maxPerListing), amount, refreshMs, budget);
    }

    public static double unitPrice(class_1799 listing) {
        double total = ServerText.askingPrice(listing);
        return total <= 0.0 ? Double.MAX_VALUE : total / (double)Math.max(1, listing.method_7947());
    }

    public static long parseMoney(String raw) {
        return ServerText.parseMoney(raw);
    }

    public static String money(double value) {
        return ServerText.shortMoney(value);
    }

    public Status status() {
        return this.status;
    }

    public int boughtItems() {
        return this.bot.boughtItems();
    }

    public long spent() {
        return this.bot.spent();
    }

    public void cancel() {
        if (this.status == Status.RUNNING) {
            this.finish(Status.FAILED);
        }
    }

    private void stopped(SniperBot from) {
        this.finish(from.complete() ? Status.DONE : Status.FAILED);
    }

    private synchronized void finish(Status result) {
        if (this.status != Status.RUNNING) {
            return;
        }
        this.status = result;
        this.chat.unregister();
        this.worker.stop();
        class_310.method_1551().execute(() -> {
            class_310 mc = class_310.method_1551();
            class_746 player = mc.field_1724;
            if (player == null) {
                return;
            }
            if (player.field_7512 != player.field_7498) {
                player.method_7346();
            } else {
                Dialogs.close(mc);
            }
        });
        if (result == Status.FAILED) {
            LOGGER.info("[Anubis] AH purchase for {} ended without finishing", (Object)this.owner.name());
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Status {
        RUNNING,
        DONE,
        FAILED;

    }
}

