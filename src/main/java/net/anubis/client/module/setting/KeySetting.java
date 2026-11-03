/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.anubis.client.input.ModuleKeybinds;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class KeySetting
extends Setting {
    private int value;

    public KeySetting(String name, int defaultKey) {
        super(name);
        this.value = defaultKey;
    }

    public int value() {
        return this.value;
    }

    public void setValue(int glfwKey) {
        if (this.value == glfwKey) {
            return;
        }
        this.value = glfwKey;
        this.notifyChanged();
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive((Number)this.value);
    }

    @Override
    protected void deserializeValue(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        try {
            int key = element.getAsInt();
            if (key == -1 || ModuleKeybinds.isBindable(key)) {
                this.value = key;
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }
}

