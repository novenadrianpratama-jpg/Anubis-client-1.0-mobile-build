/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_746
 */
package net.anubis.client.module.impl.misc;

import java.util.Set;
import java.util.TreeMap;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.GoliathPickerScreen;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.util.donut.DonutGoliaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

@Environment(value=EnvType.CLIENT)
public final class GoliathRtpModule
extends ClientModule {
    private static final int[] HOP = new int[]{1, 0, 0, 4, 3, 6, 5};
    private static final int SETTLE_TICKS = 15;
    private static final int RESEND_TICKS = 100;
    private static final double LANDED_SQ = 10000.0;
    private static Set<DonutGoliaths.Goliath> lastPick = Set.of();
    private Set<DonutGoliaths.Goliath> targets;
    private String pending;
    private int sinceSent;
    private int settle;
    private int sent;
    private double lastX;
    private double lastZ;

    public GoliathRtpModule() {
        super("goliath-rtp", "Goliath RTP", "Opens a map of every goliath: pick as many as you like and it uses /rtp until you land on one of them.", ModuleCategory.DONUT);
    }

    @Override
    public boolean persistEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        this.targets = null;
        this.pending = null;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.method_1562() == null) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Join the server first."));
            this.requestDisable();
            return;
        }
        mc.execute(() -> {
            if (this.isEnabled()) {
                mc.method_1507((class_437)new GoliathPickerScreen(lastPick, this::picked));
            }
        });
    }

    @Override
    protected void onDisable() {
        this.targets = null;
        this.pending = null;
    }

    private void picked(Set<DonutGoliaths.Goliath> chosen) {
        if (!this.isEnabled()) {
            return;
        }
        if (chosen == null || chosen.isEmpty()) {
            this.requestDisable();
            return;
        }
        this.targets = chosen;
        lastPick = chosen;
        this.pending = null;
        this.settle = 0;
        this.sent = 0;
        class_746 player = class_310.method_1551().field_1724;
        if (player != null) {
            this.lastX = player.method_23317();
            this.lastZ = player.method_23321();
        }
        if (chosen.size() == 1) {
            DonutGoliaths.Goliath only = chosen.iterator().next();
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Going to ").name(only.label()).text(" with /rtp " + DonutGoliaths.RTP[only.region()] + "."));
            return;
        }
        TreeMap<String, Integer> perRegion = new TreeMap<String, Integer>();
        for (DonutGoliaths.Goliath goliath : chosen) {
            perRegion.merge(DonutGoliaths.REGIONS[goliath.region()], 1, Integer::sum);
        }
        StringBuilder list = new StringBuilder();
        perRegion.forEach((region, n) -> list.append(list.isEmpty() ? "" : ", ").append((String)region).append(' ').append(n));
        ModuleChat.send((ClientModule)this, ModuleChat.body().text("Going to any of ").name(chosen.size() + " goliaths").text(" (" + String.valueOf(list) + ")."));
    }

    @EventHandler
    public void onTick(TickEvent event) {
        DonutGoliaths.Goliath here;
        double dz;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (this.targets == null || player == null || mc.field_1687 == null || mc.method_1562() == null) {
            return;
        }
        double dx = player.method_23317() - this.lastX;
        boolean teleported = dx * dx + (dz = player.method_23321() - this.lastZ) * dz > 10000.0;
        this.lastX = player.method_23317();
        this.lastZ = player.method_23321();
        if (this.pending != null) {
            if (!teleported) {
                if (++this.sinceSent >= 100) {
                    this.send(mc, this.pending);
                }
                return;
            }
            this.pending = null;
            this.settle = 15;
        }
        if ((here = GoliathRtpModule.here(mc)) != null && this.targets.contains(here)) {
            ModuleChat.send((ClientModule)this, ModuleChat.body().text("Landed on ").name(here.label()).text(" after " + this.sent + " /rtp."));
            this.requestDisable();
            return;
        }
        if (this.settle > 0) {
            --this.settle;
            return;
        }
        int current = here == null ? -1 : here.region();
        int next = this.bestRegion(current);
        if (next < 0) {
            next = current >= 0 ? HOP[current] : HOP[0];
        }
        this.send(mc, DonutGoliaths.RTP[next]);
    }

    private int bestRegion(int current) {
        int[] count = new int[DonutGoliaths.REGIONS.length];
        for (DonutGoliaths.Goliath goliath : this.targets) {
            int n = goliath.region();
            count[n] = count[n] + 1;
        }
        int best = -1;
        double bestShare = 0.0;
        for (int region = 0; region < count.length; ++region) {
            double share;
            if (region == current || count[region] == 0 || DonutGoliaths.RTP[region] == null || !((share = (double)count[region] / (double)DonutGoliaths.CELLS[region].length) > bestShare)) continue;
            best = region;
            bestShare = share;
        }
        return best;
    }

    private void send(class_310 mc, String region) {
        mc.method_1562().method_45730("rtp " + region);
        this.pending = region;
        this.sinceSent = 0;
        ++this.sent;
    }

    private static DonutGoliaths.Goliath here(class_310 mc) {
        if (mc.field_1687.method_27983() == class_1937.field_25181) {
            return null;
        }
        double scale = mc.field_1687.method_27983() == class_1937.field_25180 ? 8.0 : 1.0;
        return DonutGoliaths.at(mc.field_1724.method_23317() * scale, mc.field_1724.method_23321() * scale);
    }
}

