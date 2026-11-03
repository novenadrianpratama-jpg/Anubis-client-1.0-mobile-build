/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_7923
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.function.Consumer;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.module.impl.donutmacros.Gui;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
final class OrdersNav {
    static final int YOUR_ORDERS_BUTTON = 51;
    static final int ORDER_SLOTS = 45;
    private static final long OPEN_TIMEOUT_MS = 10000L;

    private OrdersNav() {
    }

    static boolean openYourOrders(MacroWorker worker, Gui gui, Consumer<ModuleChat.Body> say) {
        gui.closeAll();
        worker.sleep(150L);
        gui.command("orders");
        if (!worker.waitFor(OrdersNav::isOrdersRootNow, 10000L)) {
            say.accept(ModuleChat.body().warn("Could not open /orders."));
            gui.closeAll();
            return false;
        }
        String before = gui.fingerprint();
        gui.click(51, 0, class_1713.field_7790);
        if (worker.waitFor(OrdersNav::isYourOrdersNow, 2500L)) {
            return true;
        }
        if (gui.waitChange(before, 1000L)) {
            worker.sleep(150L);
            if (worker.call(Gui::containerOpenNow).booleanValue() && !worker.call(OrdersNav::isOrdersRootNow).booleanValue()) {
                return true;
            }
        }
        say.accept(ModuleChat.body().warn("Could not open Your Orders."));
        gui.closeAll();
        return false;
    }

    static boolean isOrdersRootNow() {
        if (!Gui.containerOpenNow()) {
            return false;
        }
        String title = ServerText.normalize(Gui.mc().field_1755.method_25440());
        if (title.startsWith("orders (page") || title.equals("orders")) {
            return true;
        }
        if (title.contains("your orders") || title.contains("auction")) {
            return false;
        }
        if (title.contains("orders")) {
            return true;
        }
        return Gui.nameOf(Gui.slotNow(51)).contains("your order");
    }

    static boolean isYourOrdersNow() {
        if (!Gui.containerOpenNow()) {
            return false;
        }
        String title = ServerText.normalize(Gui.mc().field_1755.method_25440());
        return title.contains("your orders") || Gui.findNow(OrdersNav::isNewOrderPane, 45) >= 0;
    }

    static boolean isNewOrderPane(class_1799 stack) {
        return stack.method_31574(class_1802.field_8871) && Gui.nameOf(stack).contains("new order");
    }

    static boolean isPane(class_1799 stack) {
        return stack.method_7960() || class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832().contains("stained_glass_pane");
    }
}

