/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.misc;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class SlowSwingModule
extends ClientModule {
    private final NumberSetting speed = this.addSetting("speed", new NumberSetting("Speed", 0.25f, 0.1f, 2.0f).step(0.05f));

    public SlowSwingModule() {
        super("slow-swing", "Slow Swing", "Control hand swing animation speed.", ModuleCategory.MISC);
    }

    public int getModifiedSwingDuration(int vanilla) {
        float s = this.speed.get();
        if (s <= 0.0f) {
            s = 0.1f;
        }
        int modified = Math.round((float)vanilla / s);
        return Math.max(1, modified);
    }
}

