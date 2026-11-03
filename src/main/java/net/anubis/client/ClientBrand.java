/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ClientBrand {
    public static final String NAME = "Anubis";
    public static final String WINDOW_TITLE = "Anubis Client";
    public static final String LOGGER_NAME = "Anubis";

    private ClientBrand() {
    }
}

