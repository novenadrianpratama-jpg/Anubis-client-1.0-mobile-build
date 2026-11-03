/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 */
package net.anubis.client.module.impl.misc;

import com.example.client.mixin.AccessorNetheriteClientLevel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_640;

@Environment(value=EnvType.CLIENT)
public final class AntiGhostBlockModule
extends ClientModule {
    private static final int MAX_PENDING = 256;
    private static AntiGhostBlockModule instance;
    private final Map<Long, Pending> pending = new LinkedHashMap<Long, Pending>();
    private class_638 world;

    public AntiGhostBlockModule() {
        super("anti-ghost-block", "Anti Ghost Block", "Stops invisible ghost blocks when mining: a broken block stays visible until the server confirms the break.", ModuleCategory.MISC);
        instance = this;
    }

    @Override
    protected void onEnable() {
        this.pending.clear();
        this.world = class_310.method_1551().field_1687;
    }

    @Override
    protected void onDisable() {
        this.pending.clear();
        this.world = null;
    }

    private static AntiGhostBlockModule active(class_638 level) {
        AntiGhostBlockModule module = instance;
        if (module == null || !module.isEnabled()) {
            return null;
        }
        class_310 mc = class_310.method_1551();
        if (module.world != mc.field_1687) {
            module.pending.clear();
            module.world = mc.field_1687;
        }
        return level != null && level == mc.field_1687 && mc.field_1724 != null && mc.method_1562() != null && !mc.method_1542() ? module : null;
    }

    private static long timeoutNanos(class_310 mc) {
        class_640 info = mc.method_1562() != null && mc.field_1724 != null ? mc.method_1562().method_2871(mc.field_1724.method_5667()) : null;
        long ping = info == null ? 0L : (long)Math.max(0, info.method_2959());
        return Math.max(2000L, ping * 3L + 500L) * 1000000L;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        AntiGhostBlockModule module = AntiGhostBlockModule.active(mc.field_1687);
        if (module == null) {
            return;
        }
        long now = System.nanoTime();
        long timeout = AntiGhostBlockModule.timeoutNanos(mc);
        module.pending.values().removeIf(entry -> now - entry.since >= timeout);
    }

    public static boolean holdBreak(class_2338 pos) {
        class_310 mc = class_310.method_1551();
        AntiGhostBlockModule module = AntiGhostBlockModule.active(mc.field_1687);
        if (module == null || mc.field_1687.method_8320(pos).method_26215()) {
            return false;
        }
        int sequence = ((AccessorNetheriteClientLevel)mc.field_1687).anubis$blockStatePredictionHandler().method_41942();
        long key = pos.method_10063();
        if (!module.pending.containsKey(key)) {
            if (module.pending.size() >= 256) {
                Iterator<Long> oldest = module.pending.keySet().iterator();
                oldest.next();
                oldest.remove();
            }
            module.pending.put(key, new Pending(sequence, System.nanoTime()));
        }
        return true;
    }

    public static boolean waiting(class_2338 pos) {
        class_310 mc = class_310.method_1551();
        AntiGhostBlockModule module = AntiGhostBlockModule.active(mc.field_1687);
        if (module == null) {
            return false;
        }
        Pending entry = module.pending.get(pos.method_10063());
        if (entry == null) {
            return false;
        }
        if (System.nanoTime() - entry.since < AntiGhostBlockModule.timeoutNanos(mc)) {
            return true;
        }
        module.pending.remove(pos.method_10063());
        return false;
    }

    public static boolean serverAnswered(class_638 level, class_2338 pos) {
        AntiGhostBlockModule module = AntiGhostBlockModule.active(level);
        return module != null && module.pending.remove(pos.method_10063()) != null;
    }

    public static void acknowledged(class_638 level, int sequence) {
        AntiGhostBlockModule module = AntiGhostBlockModule.active(level);
        if (module != null) {
            module.pending.values().removeIf(entry -> entry.sequence <= sequence);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Pending(int sequence, long since) {
    }
}

