/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.impl.gui.FabricGuiEntry
 *  net.fabricmc.loader.impl.gui.FabricStatusTree
 *  net.fabricmc.loader.impl.gui.FabricStatusTree$FabricBasicButtonType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.security;

import java.nio.file.Path;
import net.anubis.client.security.TamperWindow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.gui.FabricStatusTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class TamperStop {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");

    private TamperStop() {
    }

    public static void stopGame(String reason, Path jar) {
        LOGGER.error("[Anubis] Build check: the game was stopped because this Anubis jar was modified ({}). Get Anubis only from {}", (Object)reason, (Object)"discord.gg/anubis");
        if (!TamperStop.showWindow(jar)) {
            TamperStop.showFabricWindow(jar);
        }
        System.exit(1);
    }

    private static boolean showWindow(Path jar) {
        String java = ProcessHandle.current().info().command().orElse(null);
        if (java == null || jar == null) {
            return false;
        }
        try {
            Process window = new ProcessBuilder(java, "-cp", jar.toString(), TamperWindow.class.getName(), jar.toString()).redirectOutput(ProcessBuilder.Redirect.DISCARD).redirectError(ProcessBuilder.Redirect.DISCARD).start();
            return window.waitFor() == 0;
        }
        catch (Exception e) {
            LOGGER.warn("[Anubis] Build check: the warning window could not open ({})", (Object)e.toString());
            return false;
        }
    }

    private static void showFabricWindow(Path jar) {
        try {
            FabricStatusTree tree = new FabricStatusTree("Anubis", TamperWindow.plainText(jar == null ? "" : jar.toString()));
            tree.addButton("Copy official link", FabricStatusTree.FabricBasicButtonType.CLICK_MANY).withClipboard("https://discord.gg/anubis");
            tree.addButton("Close", FabricStatusTree.FabricBasicButtonType.CLICK_ONCE).makeClose();
            FabricGuiEntry.open((FabricStatusTree)tree);
        }
        catch (Throwable e) {
            LOGGER.warn("[Anubis] Build check: Fabric's error window could not open ({})", (Object)e.toString());
        }
    }
}

