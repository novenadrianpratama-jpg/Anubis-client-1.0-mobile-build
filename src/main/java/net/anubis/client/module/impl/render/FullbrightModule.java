/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FullbrightModule
extends ClientModule {
    private final NumberSetting gamma = this.addSetting("gamma", new NumberSetting("Gamma", 8.0f, 1.0f, 16.0f).step(0.5f));

    public FullbrightModule() {
        super("fullbright", "Fullbright", "Brighten dark areas by overriding the client lightmap gamma.", ModuleCategory.RENDER);
    }

    public float gamma() {
        return Math.max(1.0f, this.gamma.get());
    }
}

