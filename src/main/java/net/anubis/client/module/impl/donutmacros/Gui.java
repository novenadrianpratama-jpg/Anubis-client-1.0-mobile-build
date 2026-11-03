/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1661
 *  net.minecraft.class_1703
 *  net.minecraft.class_1713
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 *  net.minecraft.class_2596
 *  net.minecraft.class_2815
 *  net.minecraft.class_310
 *  net.minecraft.class_418
 *  net.minecraft.class_465
 *  net.minecraft.class_746
 *  net.minecraft.class_7923
 */
package net.anubis.client.module.impl.donutmacros;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.anubis.client.module.impl.donutmacros.MacroWorker;
import net.anubis.client.module.impl.donutmacros.ServerText;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_418;
import net.minecraft.class_465;
import net.minecraft.class_746;
import net.minecraft.class_7923;

@Environment(value=EnvType.CLIENT)
final class Gui {
    private static final int COMMAND_GAP_MS = 200;
    final MacroWorker worker;

    Gui(MacroWorker worker) {
        this.worker = worker;
    }

    static class_310 mc() {
        return class_310.method_1551();
    }

    static class_746 player() {
        class_746 player = Gui.mc().field_1724;
        if (player == null) {
            throw new IllegalStateException("Not in a world");
        }
        return player;
    }

    String title() {
        return this.worker.call(() -> Gui.mc().field_1755 == null ? "" : ServerText.normalize(Gui.mc().field_1755.method_25440()));
    }

    static boolean containerOpenNow() {
        class_746 player = Gui.mc().field_1724;
        return player != null && player.field_7512 != player.field_7498 && Gui.mc().field_1755 instanceof class_465;
    }

    static List<class_1799> containerNow() {
        ArrayList<class_1799> out = new ArrayList<class_1799>();
        class_746 player = Gui.mc().field_1724;
        if (player == null || player.field_7512 == player.field_7498) {
            return out;
        }
        for (class_1735 slot : player.field_7512.field_7761) {
            if (slot.field_7871 instanceof class_1661) break;
            out.add(slot.method_7677().method_7972());
        }
        return out;
    }

    List<class_1799> container() {
        return this.worker.call(Gui::containerNow);
    }

    static String fingerprintNow() {
        StringBuilder text = new StringBuilder();
        class_746 player = Gui.mc().field_1724;
        if (player != null) {
            text.append(player.field_7512.field_7763).append('#');
        }
        for (class_1799 stack : Gui.containerNow()) {
            text.append(class_7923.field_41178.method_10221((Object)stack.method_7909())).append(':').append(stack.method_7947()).append(':').append(stack.method_7964().getString()).append('|');
        }
        return text.toString();
    }

    String fingerprint() {
        return this.worker.call(Gui::fingerprintNow);
    }

    static class_1799 slotNow(int index) {
        List<class_1799> stacks = Gui.containerNow();
        return index >= 0 && index < stacks.size() ? stacks.get(index) : class_1799.field_8037;
    }

    static int findNow(Predicate<class_1799> test, int limit) {
        List<class_1799> stacks = Gui.containerNow();
        for (int i = 0; i < stacks.size() && i < limit; ++i) {
            if (stacks.get(i).method_7960() || !test.test(stacks.get(i))) continue;
            return i;
        }
        return -1;
    }

    static String nameOf(class_1799 stack) {
        return ServerText.normalize(stack.method_7964());
    }

    void click(int slot, int button, class_1713 type) {
        this.worker.run(() -> {
            class_746 player = Gui.player();
            class_1703 menu = player.field_7512;
            Gui.mc().field_1761.method_2906(menu.field_7763, slot, button, type, (class_1657)player);
        });
    }

    void clickInventory(int slot, int button, class_1713 type) {
        this.worker.run(() -> {
            class_746 player = Gui.player();
            Gui.mc().field_1761.method_2906(player.field_7498.field_7763, slot, button, type, (class_1657)player);
        });
    }

    void closeInventory() {
        this.worker.run(() -> {
            class_746 player = Gui.player();
            if (player.field_7512 == player.field_7498) {
                player.field_3944.method_52787((class_2596)new class_2815(player.field_7498.field_7763));
            }
        });
    }

    void command(String command) {
        this.worker.run(() -> Gui.player().field_3944.method_45730(command));
        this.worker.sleep(200L);
    }

    void closeAll() {
        for (int i = 0; i < 4; ++i) {
            boolean open = this.worker.call(() -> {
                class_746 player = Gui.mc().field_1724;
                if (player == null) {
                    return false;
                }
                if (player.field_7512 != player.field_7498) {
                    player.method_7346();
                    return true;
                }
                if (Gui.mc().field_1755 != null && !(Gui.mc().field_1755 instanceof class_418)) {
                    Gui.mc().field_1755.method_25419();
                    return true;
                }
                return false;
            });
            if (!open) {
                return;
            }
            this.worker.ticks(2);
        }
    }

    boolean waitChange(String before, long timeoutMs) {
        return this.worker.waitFor(() -> !Gui.fingerprintNow().equals(before), timeoutMs);
    }
}

