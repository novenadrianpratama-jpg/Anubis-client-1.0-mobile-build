/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2535
 *  net.minecraft.class_2561
 *  net.minecraft.class_2678
 *  net.minecraft.class_310
 *  net.minecraft.class_3928
 *  net.minecraft.class_634
 *  net.minecraft.class_642
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.config;

import java.lang.ref.WeakReference;
import java.util.List;
import net.anubis.client.config.ConfigProfiles;
import net.anubis.client.event.impl.PacketAppliedEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.ChallengeToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_3928;
import net.minecraft.class_634;
import net.minecraft.class_642;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ProfileAutoLoad {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final Object OWNER = new Object();
    private static final long TOAST_WAIT_NANOS = 15000000000L;
    private static WeakReference<class_2535> handled = new WeakReference<Object>(null);
    private static String pendingTitle;
    private static String pendingMessage;
    private static long pendingSince;

    private ProfileAutoLoad() {
    }

    public static void install() {
        PacketAppliedEvent.TYPE.listen(OWNER, event -> {
            if (event.packet instanceof class_2678) {
                ProfileAutoLoad.onJoin();
            }
        });
        TickEvent.TYPE.listen(OWNER, event -> ProfileAutoLoad.showPendingToast());
    }

    private static void onJoin() {
        class_642 server;
        class_310 mc = class_310.method_1551();
        if (mc.method_1542()) {
            return;
        }
        class_634 listener = mc.method_1562();
        class_642 class_6422 = server = listener == null ? null : listener.method_45734();
        if (server == null || server.field_3761 == null) {
            return;
        }
        class_2535 connection = listener.method_48296();
        if (connection == handled.get()) {
            return;
        }
        handled = new WeakReference<class_2535>(connection);
        List<String> loaded = ConfigProfiles.getInstance().loadForServer(server.field_3761);
        if (loaded.isEmpty()) {
            return;
        }
        LOGGER.info("[Anubis] Joined {} - loaded profile {}", (Object)server.field_3761, (Object)String.join((CharSequence)", ", loaded));
        pendingTitle = loaded.size() == 1 ? "Profile loaded" : "Profiles loaded";
        pendingMessage = String.join((CharSequence)", ", loaded);
        pendingSince = System.nanoTime();
    }

    private static void showPendingToast() {
        boolean expired;
        if (pendingTitle == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        boolean bl = expired = System.nanoTime() - pendingSince > 15000000000L;
        if (!expired && (mc.field_1724 == null || mc.field_1755 instanceof class_3928)) {
            return;
        }
        String title = pendingTitle;
        String message = pendingMessage;
        pendingTitle = null;
        pendingMessage = null;
        if (mc.field_1687 == null) {
            return;
        }
        ChallengeToast.addOrUpdate(new class_1799((class_1935)class_1802.field_8674), title, (class_2561)class_2561.method_43470((String)message), false);
    }
}

