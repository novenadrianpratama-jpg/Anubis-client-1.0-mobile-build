/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.anubis.client.webhook.WebhookConfig;
import net.anubis.client.webhook.WebhookEmbed;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class WebhookPayloadBuilder {
    private final List<WebhookEmbed> embeds = new ArrayList<WebhookEmbed>();
    private String content;

    public WebhookPayloadBuilder content(String content) {
        this.content = content;
        return this;
    }

    public WebhookPayloadBuilder addEmbed(WebhookEmbed embed) {
        if (embed != null) {
            this.embeds.add(embed);
        }
        return this;
    }

    public JsonObject build(WebhookConfig config) {
        if ((this.content == null || this.content.isBlank()) && this.embeds.isEmpty()) {
            throw new IllegalStateException("Webhook payload must include content or at least one embed");
        }
        JsonObject payload = new JsonObject();
        payload.addProperty("username", config.username());
        payload.addProperty("avatar_url", config.avatarUrl());
        if (this.content != null && !this.content.isBlank()) {
            payload.addProperty("content", this.content);
        }
        if (!this.embeds.isEmpty()) {
            JsonArray jsonEmbeds = new JsonArray();
            for (WebhookEmbed embed : this.embeds) {
                jsonEmbeds.add((JsonElement)embed.toJson());
            }
            payload.add("embeds", (JsonElement)jsonEmbeds);
        }
        return payload;
    }
}

