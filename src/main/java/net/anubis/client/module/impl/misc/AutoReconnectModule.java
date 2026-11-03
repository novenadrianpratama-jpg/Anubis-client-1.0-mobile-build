/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.minecraft.class_156
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_412
 *  net.minecraft.class_4185
 *  net.minecraft.class_419
 *  net.minecraft.class_437
 *  net.minecraft.class_442
 *  net.minecraft.class_500
 *  net.minecraft.class_639
 *  net.minecraft.class_642
 */
package net.anubis.client.module.impl.misc;

import java.util.Locale;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_4185;
import net.minecraft.class_419;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_500;
import net.minecraft.class_639;
import net.minecraft.class_642;

@Environment(value=EnvType.CLIENT)
public final class AutoReconnectModule
extends ClientModule {
    private static final long HOLD_MS = 15000L;
    private static volatile long holdUntil;
    private final NumberSetting delay = this.addSetting("delay", new NumberSetting("Delay", 5.0f, 3.0f, 60.0f).step(1.0f));
    private class_642 lastServer;
    private class_437 activeScreen;
    private class_4185 reconnectButton;
    private int ticksLeft;
    private boolean heldScreen;

    public static void holdOff() {
        holdUntil = class_156.method_658() + 15000L;
    }

    public AutoReconnectModule() {
        super("auto-reconnect", "Auto Reconnect", "Reconnects to the last server after an unexpected disconnect.", ModuleCategory.MISC);
        TickEvent.TYPE.listen(this, this::trackServer);
        ScreenEvents.AFTER_INIT.register(this::onScreenInit);
    }

    private void trackServer(TickEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        class_642 server = mc.method_1542() ? null : mc.method_1558();
        this.lastServer = server == null || server.method_2994() || server.method_52811() ? null : server;
    }

    private void onScreenInit(class_310 client, class_437 screen, int scaledWidth, int scaledHeight) {
        class_4185 button;
        if (screen instanceof class_442 || screen instanceof class_500) {
            this.lastServer = null;
            return;
        }
        if (!(screen instanceof class_419) || this.lastServer == null) {
            return;
        }
        class_642 server = this.lastServer;
        if (screen == this.activeScreen) {
            Screens.getButtons((class_437)screen).remove(this.reconnectButton);
        } else {
            this.activeScreen = screen;
            this.ticksLeft = Math.round(this.delay.get() * 20.0f);
            this.heldScreen = class_156.method_658() < holdUntil || AutoReconnectModule.isBan(screen);
            holdUntil = 0L;
        }
        boolean auto = this.isEnabled() && !this.heldScreen;
        this.reconnectButton = button = class_4185.method_46430((class_2561)AutoReconnectModule.reconnectLabel(auto, this.ticksLeft), btn -> {
            btn.field_22763 = false;
            this.reconnect(client, server);
        }).method_46434(scaledWidth - 150 - 10, scaledHeight - 20 - 10, 150, 20).method_46431();
        Screens.getButtons((class_437)screen).add(button);
        if (auto) {
            ScreenEvents.afterTick((class_437)screen).register(s -> {
                --this.ticksLeft;
                if (this.ticksLeft <= 0) {
                    button.field_22763 = false;
                    this.reconnect(client, server);
                    return;
                }
                button.method_25355(AutoReconnectModule.reconnectLabel(true, this.ticksLeft));
            });
        }
    }

    private static boolean isBan(class_437 screen) {
        String reason = screen.method_25435().getString().toLowerCase(Locale.ROOT);
        return reason.contains("banned") || reason.contains("blacklisted");
    }

    private void reconnect(class_310 client, class_642 server) {
        class_639 address = class_639.method_2950((String)server.field_3761);
        class_412.method_36877((class_437)new class_500((class_437)new class_442()), (class_310)client, (class_639)address, (class_642)server, (boolean)false, null);
    }

    private static class_2561 reconnectLabel(boolean auto, int ticksLeft) {
        return auto ? class_2561.method_43470((String)String.format(Locale.ROOT, "Reconnect (%.1f)", (double)ticksLeft / 20.0)) : class_2561.method_43470((String)"Reconnect");
    }
}

