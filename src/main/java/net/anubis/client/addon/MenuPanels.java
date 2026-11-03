/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.addon;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class MenuPanels {
    private static volatile Supplier<? extends Panel> accounts;

    private MenuPanels() {
    }

    public static void registerAccounts(Supplier<? extends Panel> factory) {
        accounts = factory;
    }

    public static Panel newAccountsPanel() {
        Supplier<? extends Panel> factory = accounts;
        return factory == null ? null : factory.get();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Panel {
        public void open();

        public boolean isOpen();

        public boolean keyPressed(int var1);

        public void render(float var1, float var2, float var3, float var4);
    }
}

