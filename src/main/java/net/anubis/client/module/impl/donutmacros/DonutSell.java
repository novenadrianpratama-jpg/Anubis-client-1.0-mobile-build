/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_7439
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.ListenerHandle;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.PacketReceiveNettyEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.impl.donutmacros.SellRoutine;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.anubis.client.module.setting.StringListSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_7439;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class DonutSell {
    private static final Pattern RATE_LIMIT = Pattern.compile("you need to wait another ([\\d.]+) seconds?");
    private static final long GIVE_UP_MS = 45000L;
    private final SellRoutine routine;
    private final ListenerHandle<PacketReceiveNettyEvent> chat;
    private final long started = System.currentTimeMillis();
    private boolean sent;
    private boolean done;
    private int ticks;

    private DonutSell(ClientModule owner, Map<Integer, class_1792> slots) {
        Map<Integer, class_1792> wanted = Map.copyOf(slots);
        this.routine = new SellRoutine(owner, new StringListSetting("Never Sell", new String[0]), (index, stack) -> wanted.get(index) == null || !stack.method_31574((class_1792)wanted.get(index)), () -> {
            this.sent = true;
        });
        this.chat = PacketReceiveNettyEvent.TYPE.listen(this, Stage.PRE, EventPriority.NORMAL, event -> {
            class_7439 packet;
            class_2596<?> patt0$temp = event.packet;
            if (!(patt0$temp instanceof class_7439) || (packet = (class_7439)patt0$temp).comp_763() == null) {
                return;
            }
            Matcher wait = RATE_LIMIT.matcher(ServerText.normalize(packet.comp_763().getString()));
            if (!wait.find()) {
                return;
            }
            try {
                long retry = System.currentTimeMillis() + (long)Math.ceil(Double.parseDouble(wait.group(1)) * 1000.0) + 100L;
                class_310.method_1551().execute(() -> this.routine.commandRejected(retry));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
    }

    public static DonutSell start(ClientModule owner, Map<Integer, class_1792> slots) {
        return new DonutSell(owner, slots);
    }

    public boolean tick() {
        if (this.done) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.method_1562() == null || mc.field_1761 == null) {
            this.cancel();
            return false;
        }
        long now = System.currentTimeMillis();
        boolean busy = this.routine.step(mc, player, mc.method_1562(), now, true);
        ++this.ticks;
        if (busy || this.routine.active()) {
            if (now - this.started > 45000L) {
                this.cancel();
            }
            return !this.done;
        }
        if (this.sent || this.ticks > 3 || now - this.started > 45000L) {
            this.finish();
        }
        return !this.done;
    }

    public void cancel() {
        if (this.done) {
            return;
        }
        this.routine.abort(class_310.method_1551());
        this.finish();
    }

    private void finish() {
        this.done = true;
        this.chat.unregister();
    }
}

