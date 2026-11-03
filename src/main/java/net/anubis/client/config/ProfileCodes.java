/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import net.anubis.client.config.ProfileException;
import net.anubis.client.config.ShareDictionary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
final class ProfileCodes {
    static final String PREFIX = "AnubisProfile:";
    static final int MAX_JSON_BYTES = 0x400000;
    private static final int MAX_CODE_CHARS = 0x100000;
    private static final String DAMAGED = "That code is damaged or incomplete";
    private static final byte[] DICTIONARY = ShareDictionary.bytes();

    private ProfileCodes() {
    }

    static String encode(JsonObject profile) {
        return ProfileCodes.encode(profile, PREFIX);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String encode(JsonObject profile, String prefix) {
        byte[] json = ProfileCodes.tidyNumbers((JsonElement)profile.deepCopy()).toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream packed = new ByteArrayOutputStream(json.length / 3 + 64);
        Deflater deflater = new Deflater(9, true);
        try {
            deflater.setDictionary(DICTIONARY);
            deflater.setInput(json);
            deflater.finish();
            byte[] chunk = new byte[4096];
            while (!deflater.finished()) {
                packed.write(chunk, 0, deflater.deflate(chunk));
            }
        }
        finally {
            deflater.end();
        }
        return prefix + Base64.getEncoder().withoutPadding().encodeToString(packed.toByteArray());
    }

    static JsonObject decode(String text) throws ProfileException {
        return ProfileCodes.decode(text, PREFIX, "No Anubis profile code on the clipboard");
    }

    static JsonObject decode(String text, String prefix, String missing) throws ProfileException {
        byte[] json;
        String body = ProfileCodes.extract(text, prefix, missing);
        try (InputStream in = ProfileCodes.unpack(Base64.getDecoder().decode(body));){
            json = in.readNBytes(0x400001);
        }
        catch (IOException | IllegalArgumentException e) {
            throw new ProfileException(DAMAGED);
        }
        if (json.length > 0x400000) {
            throw new ProfileException("That code is too large");
        }
        return ProfileCodes.parseObject(new String(json, StandardCharsets.UTF_8), DAMAGED);
    }

    private static InputStream unpack(byte[] packed) throws IOException {
        if (packed.length > 1 && (packed[0] & 0xFF) == 31 && (packed[1] & 0xFF) == 139) {
            return new GZIPInputStream(new ByteArrayInputStream(packed));
        }
        Inflater inflater = new Inflater(true);
        inflater.setDictionary(DICTIONARY);
        return new InflaterInputStream(new ByteArrayInputStream(Arrays.copyOf(packed, packed.length + 1)), inflater);
    }

    private static JsonElement tidyNumbers(JsonElement element) {
        double value;
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : List.copyOf(object.keySet())) {
                object.add(key, ProfileCodes.tidyNumbers(object.get(key)));
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                array.set(i, ProfileCodes.tidyNumbers(array.get(i)));
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber() && (value = element.getAsDouble()) == Math.rint(value) && Math.abs(value) < 1.0E15) {
            return new JsonPrimitive((Number)((long)value));
        }
        return element;
    }

    static JsonObject parseObject(String text, String failure) throws ProfileException {
        String json = text.startsWith("\ufeff") ? text.substring(1) : text;
        try {
            JsonElement parsed = JsonParser.parseString((String)json);
            if (parsed.isJsonObject()) {
                return parsed.getAsJsonObject();
            }
        }
        catch (RuntimeException e) {
            throw new ProfileException(failure);
        }
        throw new ProfileException(failure);
    }

    static boolean contains(String text, String prefix) {
        if (text == null) {
            return false;
        }
        int i = 0;
        while (i + prefix.length() <= text.length()) {
            if (text.regionMatches(true, i, prefix, 0, prefix.length())) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private static String extract(String text, String prefix, String missing) throws ProfileException {
        int start;
        int end;
        if (text == null || text.isBlank()) {
            throw new ProfileException("The clipboard is empty");
        }
        if (text.length() > 0x200000) {
            throw new ProfileException("That code is too large");
        }
        int at = -1;
        int i = 0;
        while (i + prefix.length() <= text.length() && at < 0) {
            if (text.regionMatches(true, i, prefix, 0, prefix.length())) {
                at = i;
            }
            ++i;
        }
        if (at < 0) {
            throw new ProfileException(missing);
        }
        for (end = start = at + prefix.length(); end < text.length() && ProfileCodes.isCodeChar(text.charAt(end)); ++end) {
        }
        if (end == start) {
            throw new ProfileException(DAMAGED);
        }
        if (end - start > 0x100000) {
            throw new ProfileException("That code is too large");
        }
        return text.substring(start, end).replace('-', '+').replace('_', '/');
    }

    private static boolean isCodeChar(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9' || ch == '+' || ch == '/' || ch == '=' || ch == '-' || ch == '_';
    }
}

