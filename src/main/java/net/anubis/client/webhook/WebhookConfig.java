/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.webhook;

import java.net.URI;
import java.net.URISyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record WebhookConfig(String url, String username, String avatarUrl) {
    public static final String DEFAULT_USERNAME = "Anubis";
    public static final String DEFAULT_AVATAR_URL = "https://i.imgur.com/W3cTqvc.png";

    public WebhookConfig {
        url = WebhookConfig.normalizeRequired(url, "url");
        username = WebhookConfig.normalizeOptional(username, DEFAULT_USERNAME);
        avatarUrl = WebhookConfig.normalizeOptional(avatarUrl, DEFAULT_AVATAR_URL);
    }

    public WebhookConfig(String url) {
        this(url, DEFAULT_USERNAME, DEFAULT_AVATAR_URL);
    }

    public static boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return uri.getHost() != null && ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme));
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

    private static String normalizeRequired(String value, String fieldName) {
        String normalized = WebhookConfig.normalizeOptional(value, null);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? fallback : normalized;
    }
}

