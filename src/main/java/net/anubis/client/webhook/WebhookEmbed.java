/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.webhook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record WebhookEmbed(String title, String description, Integer color, String thumbnailUrl, String imageUrl, String footerText, String footerIconUrl) {
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        WebhookEmbed.add(json, "title", this.title);
        WebhookEmbed.add(json, "description", this.description);
        if (this.color != null) {
            json.addProperty("color", (Number)this.color);
        }
        WebhookEmbed.addNestedUrl(json, "thumbnail", this.thumbnailUrl);
        WebhookEmbed.addNestedUrl(json, "image", this.imageUrl);
        if (this.footerText != null || this.footerIconUrl != null) {
            JsonObject footer = new JsonObject();
            WebhookEmbed.add(footer, "text", this.footerText);
            WebhookEmbed.add(footer, "icon_url", this.footerIconUrl);
            json.add("footer", (JsonElement)footer);
        }
        return json;
    }

    private static void addNestedUrl(JsonObject root, String name, String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty("url", url);
        root.add(name, (JsonElement)object);
    }

    private static void add(JsonObject root, String name, String value) {
        if (value != null && !value.isBlank()) {
            root.addProperty(name, value);
        }
    }
}

