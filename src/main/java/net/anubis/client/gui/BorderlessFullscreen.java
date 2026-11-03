/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Platform
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 */
package net.anubis.client.gui;

import com.sun.jna.Platform;
import net.anubis.client.module.impl.misc.NoInvLeakModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class BorderlessFullscreen {
    private BorderlessFullscreen() {
    }

    public static boolean wanted() {
        return Platform.isWindows() && NoInvLeakModule.wantsBorderless();
    }

    public static void tick() {
        class_1041 window = class_310.method_1551().method_22683();
        if (!window.method_4498()) {
            return;
        }
        Switchable switchable = (Switchable)window;
        if (BorderlessFullscreen.wanted() != switchable.anubis$isBorderless()) {
            switchable.anubis$reapplyMode();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Switchable {
        public boolean anubis$isBorderless();

        public void anubis$reapplyMode();
    }
}

