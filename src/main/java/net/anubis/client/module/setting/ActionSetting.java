/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ActionSetting
extends Setting {
    private final Runnable action;

    public ActionSetting(String name, Runnable action) {
        super(name);
        this.action = action;
    }

    public void run() {
        this.action.run();
    }

    @Override
    public JsonElement serialize() {
        return JsonNull.INSTANCE;
    }

    @Override
    protected void deserializeValue(JsonElement element) {
    }
}

