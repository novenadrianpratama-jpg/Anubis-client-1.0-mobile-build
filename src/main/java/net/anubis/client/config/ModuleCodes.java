/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.anubis.client.config.ProfileCodes;
import net.anubis.client.config.ProfileException;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ModuleCodes {
    private static final String PREFIX = "AnubisModule:";
    private static final int FORMAT = 2;

    private ModuleCodes() {
    }

    public static String copy(ClientModule module) {
        JsonObject settings = new JsonObject();
        for (Setting setting : module.settings()) {
            if (setting.isPrivate() || setting.isDefault()) continue;
            settings.add(setting.id(), setting.compact(setting.serialize()));
        }
        JsonObject root = new JsonObject();
        root.addProperty("format", (Number)2);
        root.addProperty("module", module.id());
        if (module.settingsVersion() > 0) {
            root.addProperty("settingsVersion", (Number)module.settingsVersion());
        }
        root.add("settings", (JsonElement)settings);
        return ProfileCodes.encode(root, PREFIX);
    }

    public static void paste(ClientModule module, String text) throws ProfileException {
        String id;
        if (!module.inConfigs()) {
            throw new ProfileException(module.name() + " can't be set by configs");
        }
        if (!ProfileCodes.contains(text, PREFIX) && ProfileCodes.contains(text, "AnubisProfile:")) {
            throw new ProfileException("That's a whole profile, paste it in the Config tab");
        }
        JsonObject root = ProfileCodes.decode(text, PREFIX, "No module config on the clipboard");
        int format = 0;
        if (root.has("format") && root.get("format").isJsonPrimitive()) {
            try {
                format = root.get("format").getAsInt();
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        if (format < 2) {
            throw new ProfileException("Made by an old Anubis - old configs no longer work");
        }
        String string = id = root.has("module") && root.get("module").isJsonPrimitive() ? root.get("module").getAsString() : "";
        if (!module.id().equals(id)) {
            ClientModule owner = id.isEmpty() ? null : ModuleRegistry.resolve(id);
            throw new ProfileException("That config is for " + (owner != null ? owner.name() : "another module"));
        }
        JsonObject settings = root.has("settings") && root.get("settings").isJsonObject() ? root.getAsJsonObject("settings") : new JsonObject();
        for (Setting setting : module.settings()) {
            if (setting.isPrivate()) continue;
            JsonElement value = settings.get(setting.id());
            if (value != null) {
                setting.deserialize(value);
                continue;
            }
            setting.resetToDefault();
        }
        int current = module.settingsVersion();
        int saved = 0;
        if (root.has("settingsVersion") && root.get("settingsVersion").isJsonPrimitive()) {
            try {
                saved = root.get("settingsVersion").getAsInt();
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        if (current > 0 && saved < current) {
            module.migrateSettings(saved);
        }
    }
}

