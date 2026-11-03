/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  imgui.type.ImBoolean
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import imgui.type.ImBoolean;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class BooleanSetting
extends Setting {
    private final ImBoolean value;

    public BooleanSetting(String name, boolean value) {
        super(name);
        this.value = new ImBoolean(value);
    }

    public boolean get() {
        return this.value.get();
    }

    public void set(boolean newValue) {
        if (this.value.get() == newValue) {
            return;
        }
        this.value.set(newValue);
        this.notifyChanged();
    }

    public ImBoolean value() {
        return this.value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(Boolean.valueOf(this.value.get()));
    }

    @Override
    protected void deserializeValue(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        try {
            this.value.set(element.getAsBoolean());
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }
}

