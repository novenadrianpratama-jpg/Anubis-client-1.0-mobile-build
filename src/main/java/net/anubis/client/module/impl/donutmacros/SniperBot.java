/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1890
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_5251
 *  net.minecraft.class_640
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 *  net.minecraft.class_9290
 *  net.minecraft.class_9334
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.impl.donutmacros.AuctionHouse;
import net.anubis.client.module.impl.donutmacros.Dialogs;
import net.anubis.client.module.impl.donutmacros.Gui;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.impl.donutmacros.SnipeTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5251;
import net.minecraft.class_640;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class SniperBot {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Pattern RATE_LIMIT = Pattern.compile("^you need to wait another ([\\d.]+) seconds?");
    private static final double MAX_WAIT_SECONDS = 30.0;
    private static final Pattern BOUGHT_COUNT = Pattern.compile("^(\\d{1,4})\\s*x?\\s+");
    private static final Pattern TIME = Pattern.compile("\\d+\\s*[dhms]\\b");
    private static final int LISTING_SLOTS = 45;
    private static final int DEFAULT_FILTER = 47;
    private static final int DEFAULT_REFRESH = 49;
    private static final long OPEN_TIMEOUT_MS = 10000L;
    private static final long CONTENT_TIMEOUT_MS = 3000L;
    private static final long SORT_TIMEOUT_MS = 3000L;
    private static final long SETTLE_MS = 150L;
    private static final long SETTLE_LIMIT_MS = 1500L;
    private static final int SETTLE_TICKS = 2;
    private static final long ANSWER_DEFAULT_MS = 1000L;
    private static final long ANSWER_MIN_MS = 700L;
    private static final long ANSWER_MAX_MS = 3000L;
    private static final long ANSWER_SLACK_MS = 300L;
    private static final double CONFIRM_PRICE_SLACK = 1.02;
    private static final long BUY_TIMEOUT_MS = 4000L;
    private static final long SCREEN_GRACE_MS = 400L;
    private static final long REOPEN_WAIT_MS = 1000L;
    private static final long LATE_PURCHASE_MS = 15000L;
    private static final long FAILED_SKIP_MS = 60000L;
    private static final long WRONG_SKIP_MS = 600000L;
    private static final long BOUGHT_SKIP_MS = 15000L;
    private static final long RECOVERY_MS = 2000L;
    private static final long POLL_MS = 2L;
    private static final int TIMER_SLACK_MINUTES = 2;
    private static final int STRIKES_TO_SKIP = 2;
    private static final int UNREADABLE_TO_STOP = 4;
    private static final int MAX_PAGES = 5;
    private static final long RESEARCH_MIN_MS = 2500L;
    private static final long MISS_NOTE_MS = 30000L;
    private static final Hit CLOSED = new Hit(-1, class_1799.field_8037, 0L, 0L, -1, "", "");
    private final ClientModule module;
    private final Consumer<SniperBot> stopper;
    private final MacroWorker worker;
    private final Gui gui;
    private final SnipeTarget target;
    private final long refreshMs;
    private final long settleMs;
    private final long budget;
    private final boolean requireConfirm;
    private final int maxBuys;
    private final int wantItems;
    private final Sort sort;
    private final AuctionHouse ah;
    private final AuctionHouse.Stock stock;
    private final List<AuctionHouse.Stock> stocks;
    private final Map<String, Long> skip = new HashMap<String, Long>();
    private final Map<String, Integer> strikes = new HashMap<String, Integer>();
    private volatile Purchase purchase;
    private volatile long goneAt;
    private volatile long poorAt;
    private volatile long ownAt;
    private volatile long holdUntil;
    private long handledAt;
    private Hit lastHit;
    private long lastClickAt;
    private long pageKey = Long.MIN_VALUE;
    private long pageChangedAt;
    private int pageChangedTick;
    private boolean refreshPending;
    private long refreshSentAt;
    private long answerMs;
    private long nextRefresh;
    private volatile boolean confirmPressed;
    private boolean instantWarned;
    private int unreadable;
    private int bought;
    private int unsold;
    private long spent;
    private int wrong;
    private long wrongSpent;
    private int boughtItems;
    private volatile boolean complete;
    private int pagesTurned;
    private int searches;
    private volatile String note = "";
    private volatile long noteAt;
    private long lastMissNote;
    private boolean sortWarned;

    SniperBot(ClientModule module, Consumer<SniperBot> stopper, MacroWorker worker, SnipeTarget target, long refreshMs, long settleMs, long budget, boolean requireConfirm, int maxBuys, long sellPrice, String ign) {
        this(module, stopper, worker, target, refreshMs, settleMs, budget, requireConfirm, maxBuys, 0, sellPrice, ign);
    }

    static SniperBot quota(ClientModule module, Consumer<SniperBot> stopper, MacroWorker worker, SnipeTarget target, int wantItems, long refreshMs, long budget) {
        return new SniperBot(module, stopper, worker, target, refreshMs, 200L, budget, false, 0, Math.max(1, wantItems), 0L, "");
    }

    private SniperBot(ClientModule module, Consumer<SniperBot> stopper, MacroWorker worker, SnipeTarget target, long refreshMs, long settleMs, long budget, boolean requireConfirm, int maxBuys, int wantItems, long sellPrice, String ign) {
        this.module = module;
        this.stopper = stopper;
        this.worker = worker;
        this.gui = new Gui(worker);
        this.target = target;
        this.refreshMs = refreshMs;
        this.settleMs = settleMs;
        this.budget = budget;
        this.requireConfirm = requireConfirm;
        this.maxBuys = maxBuys;
        this.wantItems = wantItems;
        Sort sort = this.sort = wantItems > 0 ? Sort.LOWEST : Sort.RECENT;
        if (sellPrice > 0L) {
            this.ah = new AuctionHouse(module, worker, new AuctionHouse.Config(0L, 60000L, 20000L, 1, 1000, 0L, 3.0, 20.0, 36, 15000L), ign);
            this.stock = new AuctionHouse.Stock(target.product(sellPrice), true);
            this.stocks = List.of(this.stock);
        } else {
            this.ah = null;
            this.stock = null;
            this.stocks = List.of();
        }
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.send(this.module, body);
    }

    boolean complete() {
        return this.complete;
    }

    int boughtItems() {
        return this.boughtItems;
    }

    long spent() {
        return this.spent + this.wrongSpent;
    }

    void run() {
        if (this.wantItems > 0) {
            this.say(ModuleChat.body().text("Buying ").value(this.wantItems).text("x ").name(this.target.describe()).text(", at most $").value(ServerText.shortMoney(this.target.maxPrice)).text(" per listing").text((String)(this.budget > 0L ? ", spending at most $" + ServerText.shortMoney(this.budget) : "")).text("."));
        } else {
            this.say(ModuleChat.body().text("Sniping ").name(this.target.describe()).text(" under $").value(ServerText.shortMoney(this.target.maxPrice)).text((String)(this.target.minMinutes > 0 ? " with " + SniperBot.formatMinutes(this.target.minMinutes) + "+ left" : "")).text((String)(this.stock != null ? ", selling at $" + ServerText.shortMoney(this.stock.price) : "")).text((String)(this.budget > 0L ? ", spending at most $" + ServerText.shortMoney(this.budget) : "")).text("."));
        }
        while (true) {
            this.worker.check();
            try {
                if (!this.worker.call(() -> Gui.mc().field_1724 != null && Gui.mc().method_1562() != null).booleanValue()) {
                    this.worker.sleep(2000L);
                    continue;
                }
                if (this.stock != null && this.unsold > 0) {
                    this.sellStock();
                }
                this.snipe();
                continue;
            }
            catch (MacroWorker.Cancelled cancelled) {
                throw cancelled;
            }
            catch (RuntimeException failure) {
                String reason = failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage();
                this.say(ModuleChat.body().warn(reason).text(", trying again."));
                this.worker.check();
                this.gui.closeAll();
                this.worker.sleep(2000L);
                continue;
            }
            break;
        }
    }

    private void snipe() {
        this.openAuction();
        this.ensureSort();
        this.pageKey = Long.MIN_VALUE;
        this.refreshPending = false;
        this.pagesTurned = 0;
        this.nextRefresh = System.currentTimeMillis() + (this.wantItems > 0 ? Math.max(this.refreshMs, 2500L) : this.refreshMs);
        while (true) {
            this.worker.check();
            Purchase late = this.purchase;
            if (late != null && late.at() > this.handledAt) {
                if (this.lastHit != null && late.at() - this.lastClickAt <= 15000L) {
                    if (!this.bought(this.lastHit, late)) continue;
                    return;
                }
                this.handledAt = late.at();
            }
            if (this.wantItems > 0 && this.worker.call(() -> SniperBot.freeSlots(Gui.player()) == 0).booleanValue()) {
                this.finish(ModuleChat.body().warn("Inventory full").text(" after buying ").value(this.boughtItems).text("x ").name(this.target.describe()).text("."));
            }
            long mark = System.currentTimeMillis();
            Hit hit = this.worker.call(this::pageTick);
            if (hit == CLOSED) {
                return;
            }
            if (hit != null) {
                this.lastHit = hit;
                this.lastClickAt = mark;
                if (this.handleBuy(hit, mark)) {
                    return;
                }
                this.pageKey = Long.MIN_VALUE;
                this.nextRefresh = System.currentTimeMillis() + this.refreshMs;
                continue;
            }
            this.worker.sleep(2L);
        }
    }

    private void openAuction() {
        this.gui.closeAll();
        long wait = this.holdUntil - System.currentTimeMillis();
        if (wait > 0L) {
            this.worker.sleep(wait);
        }
        long sentAt = System.currentTimeMillis();
        this.gui.command("ah " + this.target.search);
        if (!this.worker.waitFor(SniperBot::auctionOpenNow, 10000L)) {
            String screen = this.worker.call(() -> Gui.mc().field_1755 == null ? "no screen" : Gui.mc().field_1755.getClass().getSimpleName() + " '" + ServerText.normalize(Gui.mc().field_1755.method_25440()) + "'");
            String said = this.noteAt >= sentAt ? this.note : "";
            LOGGER.info("[Anubis] AH did not open for '/ah {}': {}, server said '{}'", new Object[]{this.target.search, screen, said});
            throw new AuctionHouse.Problem("The AH did not open" + (String)(said.isEmpty() ? "" : " (server: " + said + ")"));
        }
        this.worker.waitFor(() -> {
            if (!SniperBot.auctionOpenNow()) {
                return true;
            }
            List<class_1799> stacks = Gui.containerNow();
            return SniperBot.refreshSlot(stacks) >= 0 || SniperBot.filterSlot(stacks) >= 0;
        }, 3000L);
        this.settle();
    }

    private void settle() {
        long end = System.currentTimeMillis() + 1500L;
        int last = this.worker.call(SniperBot::stateNow);
        long stableSince = System.currentTimeMillis();
        while (System.currentTimeMillis() < end) {
            this.worker.sleep(25L);
            int state = this.worker.call(SniperBot::stateNow);
            long now = System.currentTimeMillis();
            if (state != last) {
                last = state;
                stableSince = now;
                continue;
            }
            if (now - stableSince < 150L) continue;
            return;
        }
    }

    private static int stateNow() {
        class_746 player = Gui.mc().field_1724;
        return player == null ? -1 : player.field_7512.method_37421();
    }

    private static boolean auctionOpenNow() {
        if (!Gui.containerOpenNow()) {
            return false;
        }
        String title = ServerText.normalize(Gui.mc().field_1755.method_25440());
        if (title.contains("order")) {
            return false;
        }
        return title.contains("auction") || SniperBot.refreshSlot(Gui.containerNow()) >= 0;
    }

    private void ensureSort() {
        for (int attempt = 0; attempt < 5; ++attempt) {
            int[] state = this.worker.call(() -> {
                List<class_1799> stacks = Gui.containerNow();
                int slot = SniperBot.filterSlot(stacks);
                return new int[]{slot, slot < 0 ? Sort.UNKNOWN.ordinal() : SniperBot.selectedSort(stacks.get(slot)).ordinal()};
            });
            if (state[0] < 0) {
                if (!this.sortWarned) {
                    this.say(ModuleChat.body().warn("No Filter button found").text(", sniping with the current sorting."));
                }
                this.sortWarned = true;
                return;
            }
            Sort sort = Sort.values()[state[1]];
            if (sort == this.sort) {
                return;
            }
            if (sort == Sort.UNKNOWN && attempt > 0) {
                if (!this.sortWarned) {
                    this.say(ModuleChat.body().warn("Could not read the Filter").text(", make sure it is on " + (this.sort == Sort.LOWEST ? "Lowest Price." : "Recently Listed.")));
                }
                this.sortWarned = true;
                return;
            }
            String before = this.gui.fingerprint();
            this.gui.click(state[0], 0, class_1713.field_7790);
            if (sort == Sort.UNKNOWN) {
                this.gui.waitChange(before, 3000L);
            } else {
                this.worker.waitFor(() -> {
                    Sort now = SniperBot.sortNow();
                    return now != Sort.UNKNOWN && now != sort;
                }, 3000L);
            }
            this.settle();
        }
    }

    private static Sort sortNow() {
        class_746 player = Gui.mc().field_1724;
        if (player == null || !SniperBot.auctionOpenNow() || !player.field_7512.method_34255().method_7960()) {
            return Sort.UNKNOWN;
        }
        List<class_1799> stacks = Gui.containerNow();
        int slot = SniperBot.filterSlot(stacks);
        return slot < 0 ? Sort.UNKNOWN : SniperBot.selectedSort(stacks.get(slot));
    }

    private static int filterSlot(List<class_1799> stacks) {
        for (int i = 45; i < stacks.size(); ++i) {
            class_1799 stack = stacks.get(i);
            if (stack.method_7960() || !stack.method_31574(class_1802.field_8239) && !Gui.nameOf(stack).contains("filter")) continue;
            return i;
        }
        return stacks.size() > 47 && !stacks.get(47).method_7960() ? 47 : -1;
    }

    private static int refreshSlot(List<class_1799> stacks) {
        for (int i = 45; i < stacks.size(); ++i) {
            class_1799 stack = stacks.get(i);
            if (stack.method_7960() || !stack.method_31574(class_1802.field_8782)) continue;
            return i;
        }
        return stacks.size() > 49 && stacks.get(49).method_31574(class_1802.field_8782) ? 49 : -1;
    }

    static Sort selectedSort(class_1799 filter) {
        ArrayList<Sort> options = new ArrayList<Sort>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (class_2561 line : ((class_9290)filter.method_58695(class_9334.field_49632, (Object)class_9290.field_49340)).comp_2400()) {
            Sort option;
            String text = ServerText.normalize(Dialogs.visibleText(line));
            Sort sort = text.contains("recent") ? Sort.RECENT : (text.contains("lowest") ? Sort.LOWEST : (option = text.contains("highest") ? Sort.HIGHEST : Sort.UNKNOWN));
            if (option == Sort.UNKNOWN) continue;
            options.add(option);
            colors.add(SniperBot.color(line));
        }
        if (options.size() < 2) {
            return Sort.UNKNOWN;
        }
        for (int i = 0; i < options.size(); ++i) {
            boolean unique = true;
            boolean othersAgree = true;
            int other = -2;
            for (int j = 0; j < options.size(); ++j) {
                if (j == i) continue;
                if (((Integer)colors.get(j)).equals(colors.get(i))) {
                    unique = false;
                }
                if (other == -2) {
                    other = (Integer)colors.get(j);
                    continue;
                }
                if (other == (Integer)colors.get(j)) continue;
                othersAgree = false;
            }
            if (!unique || !othersAgree) continue;
            return (Sort)((Object)options.get(i));
        }
        int best = -1;
        Sort pick = Sort.UNKNOWN;
        for (int i = 0; i < options.size(); ++i) {
            int rgb = (Integer)colors.get(i);
            int brightness = (rgb >> 16 & 0xFF) + (rgb >> 8 & 0xFF) + (rgb & 0xFF);
            if (brightness > best) {
                best = brightness;
                pick = (Sort)((Object)options.get(i));
                continue;
            }
            if (brightness != best) continue;
            pick = Sort.UNKNOWN;
        }
        return pick;
    }

    private static int color(class_2561 line) {
        int[] value = new int[]{-1};
        line.method_27658((style, text) -> {
            if (value[0] >= 0 || text.isBlank() || text.trim().equals("\u2022") || text.trim().equals("\u25aa")) {
                return Optional.empty();
            }
            class_5251 color = style.method_10973();
            value[0] = color == null ? 0xAA00AA : color.method_27716();
            return Optional.empty();
        }, class_2583.field_24360);
        return value[0];
    }

    private Hit pageTick() {
        class_746 player = Gui.mc().field_1724;
        if (player == null || !SniperBot.auctionOpenNow()) {
            return CLOSED;
        }
        long now = System.currentTimeMillis();
        long key = (long)player.field_7512.field_7763 << 32 | (long)player.field_7512.method_37421() & 0xFFFFFFFFL;
        if (key != this.pageKey) {
            this.pageKey = key;
            this.pageChangedAt = now;
            this.pageChangedTick = player.field_6012;
            if (this.refreshPending) {
                this.refreshPending = false;
                this.learnAnswer(now - this.refreshSentAt);
            }
            return null;
        }
        if (this.refreshPending) {
            if (now - this.refreshSentAt < this.answerTimeout(player)) {
                return null;
            }
            this.refreshPending = false;
        }
        if (now - this.pageChangedAt < this.settleMs || player.field_6012 - this.pageChangedTick < 2) {
            return null;
        }
        if (!player.field_7512.method_34255().method_7960()) {
            return null;
        }
        String title = ServerText.normalize(Gui.mc().field_1755.method_25440());
        if (this.wantItems <= 0 && title.contains("(page") && !title.contains("(page 1)")) {
            return null;
        }
        this.skip.values().removeIf(until -> until < now);
        List<class_1799> stacks = Gui.containerNow();
        int pick = -1;
        double pickUnit = Double.MAX_VALUE;
        for (int i = 0; i < 45 && i < stacks.size(); ++i) {
            String fingerprint;
            double total;
            class_1799 stack = stacks.get(i);
            if (stack.method_7960() || !this.target.matches(stack) || (total = ServerText.askingPrice(stack)) <= 0.0) continue;
            long unit = Math.round(total / (double)Math.max(1, stack.method_7947()));
            if ((this.wantItems > 0 ? Math.round(total) : unit) > this.target.maxPrice || this.budget > 0L && this.spent + this.wrongSpent + Math.round(total) > this.budget || this.skip.containsKey(fingerprint = SniperBot.fingerprint(stack, total))) continue;
            if (this.wantItems > 0) {
                double each = total / (double)Math.max(1, stack.method_7947());
                if (SniperBot.room(player, stack) < stack.method_7947() || !(each < pickUnit)) continue;
                pick = i;
                pickUnit = each;
                continue;
            }
            return this.click(player, i, stack, total, unit, fingerprint);
        }
        if (pick >= 0) {
            class_1799 stack = stacks.get(pick);
            double total = ServerText.askingPrice(stack);
            return this.click(player, pick, stack, total, Math.round(total / (double)Math.max(1, stack.method_7947())), SniperBot.fingerprint(stack, total));
        }
        if (this.wantItems > 0) {
            int next = SniperBot.nextPageSlot(stacks);
            if (next >= 0 && this.pagesTurned < 5) {
                ++this.pagesTurned;
                Gui.mc().field_1761.method_2906(player.field_7512.field_7763, next, 0, class_1713.field_7790, (class_1657)player);
                this.refreshPending = true;
                this.refreshSentAt = now;
                return null;
            }
            if (now < this.nextRefresh) {
                return null;
            }
            this.noteMiss(title, stacks, now);
            return CLOSED;
        }
        if (now >= this.nextRefresh) {
            int slot = SniperBot.refreshSlot(stacks);
            if (slot >= 0) {
                Gui.mc().field_1761.method_2906(player.field_7512.field_7763, slot, 0, class_1713.field_7790, (class_1657)player);
                this.refreshPending = true;
                this.refreshSentAt = now;
            }
            this.nextRefresh = now + this.refreshMs;
        }
        return null;
    }

    private static int nextPageSlot(List<class_1799> stacks) {
        for (int i = 45; i < stacks.size(); ++i) {
            String name;
            class_1799 stack = stacks.get(i);
            if (stack.method_7960() || (name = Gui.nameOf(stack)).contains("prev") || name.contains("back") || !name.contains("next") && (!stack.method_31574(class_1802.field_8107) || !name.contains("page"))) continue;
            return i;
        }
        return -1;
    }

    private void noteMiss(String title, List<class_1799> stacks, long now) {
        ++this.searches;
        if (now - this.lastMissNote < 30000L) {
            return;
        }
        this.lastMissNote = now;
        int listed = 0;
        for (int i = 0; i < 45 && i < stacks.size(); ++i) {
            if (stacks.get(i).method_7960()) continue;
            ++listed;
        }
        this.say(ModuleChat.body().text("No ").name(this.target.describe()).text(" for $").value(ServerText.shortMoney(this.target.maxPrice)).text(" or less on ").value("/ah " + this.target.search).text(" yet (" + listed + " other listings), searching again."));
        LOGGER.info("[Anubis] AH purchase found no {} on '{}' after {} searches:{}", new Object[]{this.target.describe(), title, this.searches, SniperBot.describe(stacks)});
    }

    private Hit click(class_746 player, int slot, class_1799 stack, double total, long unit, String fingerprint) {
        this.confirmPressed = false;
        Gui.mc().field_1761.method_2906(player.field_7512.field_7763, slot, 0, class_1713.field_7790, (class_1657)player);
        return new Hit(slot, stack, Math.round(total), unit, this.target.timed() ? SnipeTarget.minutesLeft(stack) : -1, fingerprint, stack.method_7964().getString());
    }

    private static int freeSlots(class_746 player) {
        int free = 0;
        for (int i = 0; i < 36; ++i) {
            if (!player.method_31548().method_5438(i).method_7960()) continue;
            ++free;
        }
        return free;
    }

    private static int room(class_746 player, class_1799 listing) {
        int room = 0;
        for (int i = 0; i < 36; ++i) {
            class_1799 held = player.method_31548().method_5438(i);
            if (held.method_7960()) {
                room += listing.method_7914();
                continue;
            }
            if (!class_1799.method_31577((class_1799)held, (class_1799)listing)) continue;
            room += Math.max(0, held.method_7914() - held.method_7947());
        }
        return room;
    }

    private void learnAnswer(long sample) {
        if (sample <= 0L || sample > 3000L) {
            return;
        }
        this.answerMs = this.answerMs <= 0L ? sample : (this.answerMs * 3L + sample) / 4L;
    }

    private long answerTimeout(class_746 player) {
        class_640 info;
        long base = this.answerMs > 0L ? this.answerMs * 3L : 1000L;
        class_640 class_6402 = info = Gui.mc().method_1562() == null ? null : Gui.mc().method_1562().method_2871(player.method_5667());
        if (info != null) {
            base = Math.max(base, (long)info.method_2959() * 2L);
        }
        return Math.clamp(base + 300L, 700L, 3000L);
    }

    private static String fingerprint(class_1799 stack, double total) {
        StringBuilder text = new StringBuilder();
        text.append(class_7923.field_41178.method_10221((Object)stack.method_7909())).append('|').append(stack.method_7947()).append('|').append(ServerText.key(stack.method_7964().getString())).append('|').append(Math.round(total)).append('|').append(stack.method_7919()).append('|').append(class_1890.method_57532((class_1799)stack));
        for (String line : ServerText.lore(stack)) {
            String clean = ServerText.normalize(line);
            if (TIME.matcher(clean).find()) continue;
            text.append('|').append(clean);
        }
        return text.toString();
    }

    private boolean handleBuy(Hit hit, long mark) {
        Object[] pressedDialog = new Object[]{null};
        int[] pressedChest = new int[]{-1};
        long[] seen = new long[]{0L, Long.MIN_VALUE};
        boolean[] verified = new boolean[]{false};
        Problem[] problem = new Problem[]{null};
        long end = System.currentTimeMillis() + 4000L;
        while (System.currentTimeMillis() < end) {
            Purchase done = this.purchase;
            if (done != null && done.at() >= mark && done.at() > this.handledAt) {
                return this.bought(hit, done);
            }
            if (this.poorAt >= mark) {
                this.say(ModuleChat.body().danger("Not enough money").text(" for ").name(hit.name()).text(" at $").value(ServerText.shortMoney(hit.total())).text(", stopping."));
                this.stopper.accept(this);
                throw new MacroWorker.Cancelled();
            }
            if (this.goneAt >= mark || this.ownAt >= mark) {
                this.skip.put(hit.fingerprint(), System.currentTimeMillis() + 60000L);
                return false;
            }
            Step step = this.worker.call(() -> this.confirmStep(hit, pressedDialog, pressedChest, seen, verified, problem));
            if (step == Step.WRONG) {
                if (this.strikes.merge(hit.fingerprint(), 1, Integer::sum) >= 2) {
                    this.skip.put(hit.fingerprint(), System.currentTimeMillis() + 600000L);
                }
                this.say(ModuleChat.body().warn("Did not buy").text(" ").name(hit.name()).text(": " + problem[0].reason() + "."));
                if (problem[0].fatal()) {
                    this.unreadable = 0;
                } else if (++this.unreadable >= 4) {
                    this.say(ModuleChat.body().danger("The purchase menu could not be checked").text(" " + this.unreadable + " times in a row, so AH Sniper stopped without buying. Send a screenshot of the menu to the Anubis team."));
                    this.stopper.accept(this);
                    throw new MacroWorker.Cancelled();
                }
                return !this.worker.waitFor(SniperBot::auctionOpenNow, 1000L);
            }
            if (step == Step.PRESSED) {
                end = Math.max(end, System.currentTimeMillis() + 4000L);
            }
            this.worker.sleep(2L);
        }
        this.skip.put(hit.fingerprint(), System.currentTimeMillis() + 60000L);
        return this.worker.call(SniperBot::auctionOpenNow) == false;
    }

    private Step confirmStep(Hit hit, Object[] pressedDialog, int[] pressedChest, long[] seen, boolean[] verified, Problem[] problem) {
        Problem wrong;
        class_746 player = Gui.mc().field_1724;
        if (player == null) {
            return Step.WAIT;
        }
        Dialogs.View view = Dialogs.current(Gui.mc());
        if (view != null) {
            if (view.dialog() == pressedDialog[0]) {
                return Step.WAIT;
            }
            if (!(view.titled("buy") || view.titled("sure") || view.titled("purchase") || view.titled("confirm"))) {
                return Step.WAIT;
            }
            Dialogs.Button yes = view.labelled("yes", "confirm", "buy");
            if (yes == null) {
                return Step.WAIT;
            }
            Problem wrong2 = this.dialogProblem(view, hit, verified[0]);
            if (wrong2 != null) {
                LOGGER.info("[Anubis] AH Sniper cancelled a purchase dialog ({}): {} body={} items={}", new Object[]{wrong2.reason(), view.describe(), view.body(), SniperBot.describe(view.items())});
                Dialogs.close(Gui.mc());
                problem[0] = wrong2;
                return Step.WRONG;
            }
            if (!Dialogs.press(Gui.mc(), view, yes, Map.of())) {
                return Step.WAIT;
            }
            pressedDialog[0] = view.dialog();
            verified[0] = true;
            this.confirmPressed = true;
            return Step.PRESSED;
        }
        if (!Gui.containerOpenNow() || SniperBot.auctionOpenNow()) {
            return Step.WAIT;
        }
        int sync = player.field_7512.field_7763;
        if (sync == pressedChest[0]) {
            return Step.WAIT;
        }
        List<class_1799> stacks = Gui.containerNow();
        int confirm = SniperBot.confirmSlot(stacks, hit);
        if (confirm < 0) {
            return Step.WAIT;
        }
        long now = System.currentTimeMillis();
        if (seen[1] != (long)sync) {
            seen[0] = now;
            seen[1] = sync;
        }
        if ((wrong = this.chestProblem(stacks, confirm, hit, verified[0])) != null) {
            if (!wrong.fatal() && now - seen[0] < 400L) {
                return Step.WAIT;
            }
            LOGGER.info("[Anubis] AH Sniper cancelled a purchase menu ({}): '{}' {}", new Object[]{wrong.reason(), ServerText.normalize(Gui.mc().field_1755.method_25440()), SniperBot.describe(stacks)});
            player.method_7346();
            problem[0] = wrong;
            return Step.WRONG;
        }
        Gui.mc().field_1761.method_2906(sync, confirm, 0, class_1713.field_7790, (class_1657)player);
        pressedChest[0] = sync;
        verified[0] = true;
        this.confirmPressed = true;
        return Step.PRESSED;
    }

    private static int confirmSlot(List<class_1799> stacks, Hit hit) {
        int fallback = -1;
        for (int i = 0; i < stacks.size(); ++i) {
            class_1799 stack = stacks.get(i);
            if (stack.method_7960() || stack.method_31574(hit.stack().method_7909()) || !class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832().contains("lime")) continue;
            String name = Gui.nameOf(stack);
            if (name.contains("confirm") || name.contains("buy") || name.contains("purchase") || name.contains("yes")) {
                return i;
            }
            if (fallback >= 0) continue;
            fallback = i;
        }
        return fallback;
    }

    private Problem chestProblem(List<class_1799> stacks, int confirm, Hit hit, boolean verified) {
        boolean shown = false;
        double price = -1.0;
        for (int i = 0; i < stacks.size(); ++i) {
            class_1799 stack = stacks.get(i);
            if (stack.method_7960()) continue;
            double asked = ServerText.askingPrice(SniperBot.textOf(stack));
            if (asked > price) {
                price = asked;
            }
            if (i == confirm) continue;
            if (this.sameListing(stack, hit)) {
                shown = true;
                continue;
            }
            if (SniperBot.decoration(stack) || !(asked > 0.0)) continue;
            return new Problem("the menu was for " + stack.method_7964().getString(), true);
        }
        return this.verdict(shown, price, hit, verified);
    }

    private Problem dialogProblem(Dialogs.View view, Hit hit, boolean verified) {
        ArrayList<String> text = new ArrayList<String>();
        text.add(view.title());
        text.addAll(view.body());
        for (Dialogs.Button button : view.buttons()) {
            text.add(button.label());
        }
        double price = ServerText.askingPrice(text);
        boolean shown = false;
        for (class_1799 item : view.items()) {
            if (item.method_7960()) continue;
            double asked = ServerText.askingPrice(SniperBot.textOf(item));
            if (asked > price) {
                price = asked;
            }
            if (this.sameListing(item, hit)) {
                shown = true;
                continue;
            }
            if (SniperBot.decoration(item)) continue;
            return new Problem("the menu was for " + item.method_7964().getString(), true);
        }
        if (!shown && view.items().isEmpty()) {
            String want = ServerText.key(hit.name());
            shown = !want.isEmpty() && ServerText.key(String.join((CharSequence)" ", text)).contains(want);
        }
        return this.verdict(shown, price, hit, verified);
    }

    private Problem verdict(boolean shown, double price, Hit hit, boolean verified) {
        if (price > this.limit(hit)) {
            return new Problem("the menu asked $" + ServerText.shortMoney(price), true);
        }
        if (verified) {
            return null;
        }
        if (!shown) {
            return new Problem("the menu did not show this listing", false);
        }
        if (price <= 0.0) {
            return new Problem("the menu did not show the price", false);
        }
        return null;
    }

    private double limit(Hit hit) {
        double max = this.wantItems > 0 ? (double)this.target.maxPrice : (double)this.target.maxPrice * (double)Math.max(1, hit.stack().method_7947());
        return Math.min(max, (double)hit.total() * 1.02 + 1.0);
    }

    private boolean sameListing(class_1799 shown, Hit hit) {
        class_1799 want = hit.stack();
        if (!shown.method_31574(want.method_7909()) || shown.method_7947() != want.method_7947() || shown.method_7919() != want.method_7919()) {
            return false;
        }
        String wantName = ServerText.key(want.method_7964().getString());
        if (!ServerText.key(shown.method_7964().getString()).contains(wantName)) {
            return false;
        }
        if (!class_1890.method_57532((class_1799)shown).equals((Object)class_1890.method_57532((class_1799)want))) {
            return false;
        }
        if (!this.target.sameKind(shown)) {
            return false;
        }
        if (hit.minutes() < 0) {
            return true;
        }
        int minutes = SnipeTarget.minutesLeft(shown);
        return minutes >= 0 && Math.abs(minutes - hit.minutes()) <= 2;
    }

    private static boolean decoration(class_1799 stack) {
        boolean colored;
        String path = class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832();
        if (path.contains("glass_pane") || path.endsWith("stained_glass") || path.equals("glass") || path.equals("barrier") || path.equals("structure_void") || path.equals("light") || path.endsWith("_head") || path.endsWith("_skull")) {
            return true;
        }
        boolean bl = colored = path.startsWith("lime_") || path.startsWith("green_") || path.startsWith("red_");
        if (colored && (path.endsWith("_dye") || path.endsWith("_wool") || path.endsWith("_concrete") || path.endsWith("_terracotta") || path.endsWith("_carpet"))) {
            return true;
        }
        String name = Gui.nameOf(stack);
        return name.contains("confirm") || name.contains("cancel") || name.contains("go back") || name.contains("close");
    }

    private static List<String> textOf(class_1799 stack) {
        ArrayList<String> out = new ArrayList<String>();
        out.add(stack.method_7964().getString());
        out.addAll(ServerText.lore(stack));
        return out;
    }

    private static String describe(List<class_1799> stacks) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < stacks.size() && text.length() < 2000; ++i) {
            class_1799 stack = stacks.get(i);
            if (stack.method_7960()) continue;
            text.append(" [").append(i).append(' ').append(class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832()).append(" x").append(stack.method_7947()).append(" '").append(stack.method_7964().getString()).append('\'');
            for (String line : ServerText.lore(stack)) {
                text.append(" / ").append(line);
            }
            text.append(']');
        }
        return text.toString();
    }

    private boolean bought(Hit hit, Purchase done) {
        boolean rightItem;
        this.handledAt = done.at();
        int count = Math.max(1, done.count());
        long total = done.total() > 0L ? done.total() : hit.total();
        boolean bl = rightItem = done.name().isEmpty() || SniperBot.namesMatch(done.name(), hit.stack());
        boolean rightPrice = this.wantItems > 0 ? total <= this.target.maxPrice : total <= this.target.maxPrice * (long)count;
        this.skip.put(hit.fingerprint(), System.currentTimeMillis() + 15000L);
        if (!rightItem || !rightPrice) {
            ++this.wrong;
            this.wrongSpent += total;
            LOGGER.warn("[Anubis] AH Sniper bought {}x '{}' for {} while sniping '{}' under {}", new Object[]{count, done.name(), total, hit.name(), this.target.maxPrice});
            this.say(ModuleChat.body().danger("Bought the wrong listing").text(": " + count + "x ").name(done.name().isEmpty() ? hit.name() : done.name()).text(" for $").value(ServerText.shortMoney(total)).text(". AH Sniper stopped to keep your money safe."));
            this.stopper.accept(this);
            throw new MacroWorker.Cancelled();
        }
        ++this.bought;
        this.boughtItems += count;
        this.spent += total;
        this.unreadable = 0;
        ++this.unsold;
        this.say(ModuleChat.body().good(this.wantItems > 0 ? "Bought" : "Sniped").text(" ").name(hit.name()).text(" for $").value(ServerText.shortMoney(total)).text(" (").value(this.bought).text(this.bought == 1 ? " buy" : " buys").text(", $").value(ServerText.shortMoney(this.spent)).text(" spent)."));
        if (!this.confirmPressed) {
            if (this.requireConfirm) {
                this.say(ModuleChat.body().danger("Your AH buy confirm menu is off").text(", so that click bought the listing right away. AH Sniper stopped because Only Buy With Confirm is on. Turn the buy confirm menu on in /settings."));
                this.stopper.accept(this);
                throw new MacroWorker.Cancelled();
            }
            if (!this.instantWarned) {
                this.instantWarned = true;
                this.say(ModuleChat.body().warn("Your AH buy confirm menu is off").text(", so a click buys right away. AH Sniper only clicks once the page has been still for " + this.settleMs + "ms. Turn the confirm menu on in /settings to have every purchase checked before you pay."));
            }
        }
        if (this.wantItems > 0 && this.boughtItems >= this.wantItems) {
            this.finish(ModuleChat.body().text("Got ").value(this.boughtItems).text("x ").name(this.target.describe()).text(" for $").value(ServerText.shortMoney(this.spent)).text("."));
        }
        if (this.budget > 0L && this.spent + this.wrongSpent >= this.budget) {
            if (this.stock != null) {
                this.worker.sleep(300L);
                this.sellStock();
            }
            this.say(ModuleChat.body().text("Spent $").value(ServerText.shortMoney(this.spent + this.wrongSpent)).text(" of your $").value(ServerText.shortMoney(this.budget)).text(" limit, stopping."));
            this.stopper.accept(this);
            throw new MacroWorker.Cancelled();
        }
        if (this.maxBuys > 0 && this.bought >= this.maxBuys) {
            if (this.stock != null) {
                this.worker.sleep(300L);
                this.sellStock();
            }
            this.say(ModuleChat.body().text("Reached ").value(this.maxBuys).text(this.maxBuys == 1 ? " buy" : " buys").text(", stopping."));
            this.stopper.accept(this);
            throw new MacroWorker.Cancelled();
        }
        if (this.stock == null) {
            if (this.wantItems <= 0) {
                return this.worker.call(SniperBot::auctionOpenNow) == false;
            }
            this.gui.closeAll();
            return true;
        }
        this.worker.sleep(300L);
        this.sellStock();
        return true;
    }

    private void finish(ModuleChat.Body body) {
        this.complete = true;
        this.say(body);
        this.stopper.accept(this);
        throw new MacroWorker.Cancelled();
    }

    private static boolean namesMatch(String said, class_1799 stack) {
        String text = ServerText.key(said);
        if (text.isEmpty()) {
            return true;
        }
        String shown = ServerText.key(stack.method_7964().getString());
        String plain = ServerText.key(new class_1799((class_1935)stack.method_7909()).method_7964().getString());
        String id = ServerText.key(class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832());
        return !shown.isEmpty() && (text.contains(shown) || shown.contains(text)) || !plain.isEmpty() && text.contains(plain) || !id.isEmpty() && text.contains(id);
    }

    private void sellStock() {
        int count = Math.min(this.unsold, this.ah.countInventory(this.stock.product));
        if (count <= 0) {
            return;
        }
        this.unsold -= this.ah.listSome(this.stock, Math.min(count, 9), new int[]{0});
    }

    void onNote(String raw) {
        String line = ServerText.normalize(raw);
        if (line.isEmpty()) {
            return;
        }
        this.note = line.length() > 120 ? line.substring(0, 120) : line;
        this.noteAt = System.currentTimeMillis();
    }

    void onChat(String raw) {
        String line = ServerText.normalize(raw);
        if (line.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        this.onNote(raw);
        Matcher wait = RATE_LIMIT.matcher(line);
        if (wait.find()) {
            try {
                this.holdUntil = now + (long)Math.ceil(Math.min(30.0, Double.parseDouble(wait.group(1))) * 1000.0) + 100L;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        if (line.startsWith("you bought")) {
            this.purchase = SniperBot.parsePurchase(line, now);
        }
        if (line.contains("already bought") || line.contains("no longer available") || line.contains("not available")) {
            this.goneAt = now;
        }
        if (line.contains("enough money")) {
            this.poorAt = now;
        }
        if (line.contains("your own")) {
            this.ownAt = now;
        }
        if (this.ah != null) {
            this.ah.onChat(line, this.stocks);
        }
    }

    private static Purchase parsePurchase(String line, long now) {
        List<Double> money;
        int forAt;
        String rest = line.substring("you bought".length()).trim();
        int count = 1;
        Matcher amount = BOUGHT_COUNT.matcher(rest);
        if (amount.find()) {
            count = Math.max(1, Integer.parseInt(amount.group(1)));
            rest = rest.substring(amount.end());
        }
        String name = ((forAt = rest.lastIndexOf(" for ")) >= 0 ? rest.substring(0, forAt) : rest).trim();
        long total = -1L;
        if (forAt >= 0 && !(money = ServerText.moneyAll(rest.substring(forAt + 5))).isEmpty()) {
            total = Math.round(money.get(0));
        }
        return new Purchase(now, count, name, total);
    }

    String summary() {
        return "Sniped " + this.bought + (this.bought == 1 ? " item" : " items") + " for $" + ServerText.shortMoney(this.spent) + (String)(this.wrong > 0 ? ", plus " + this.wrong + " wrong " + (this.wrong == 1 ? "listing" : "listings") + " for $" + ServerText.shortMoney(this.wrongSpent) : "") + (String)(this.stock != null ? ". " + AuctionHouse.summary(this.stocks) : ".");
    }

    private static String formatMinutes(int minutes) {
        return minutes % 60 == 0 ? minutes / 60 + "h" : minutes / 60 + "h " + minutes % 60 + "m";
    }

    @Environment(value=EnvType.CLIENT)
    static enum Sort {
        UNKNOWN,
        LOWEST,
        HIGHEST,
        RECENT;

    }

    @Environment(value=EnvType.CLIENT)
    private record Purchase(long at, int count, String name, long total) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Hit(int slot, class_1799 stack, long total, long unit, int minutes, String fingerprint, String name) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Problem(String reason, boolean fatal) {
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Step {
        WAIT,
        PRESSED,
        WRONG;

    }
}

