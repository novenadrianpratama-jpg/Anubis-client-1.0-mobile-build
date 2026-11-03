/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.anubis.client.module.setting.Setting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ColorSetting
extends Setting {
    private final float[] value;
    private int packed;
    private int packedR;
    private int packedG;
    private int packedB;
    private int packedA;

    public ColorSetting(String name, int argb) {
        super(name);
        this.value = new float[]{(float)(argb >> 16 & 0xFF) / 255.0f, (float)(argb >> 8 & 0xFF) / 255.0f, (float)(argb & 0xFF) / 255.0f, (float)(argb >>> 24 & 0xFF) / 255.0f};
    }

    public float[] value() {
        return this.value;
    }

    public void setArgb(int argb) {
        if (this.argb() == argb) {
            return;
        }
        this.setFromArgb(argb);
        this.notifyChanged();
    }

    public int argb() {
        int r = Float.floatToRawIntBits(this.value[0]);
        int g = Float.floatToRawIntBits(this.value[1]);
        int b = Float.floatToRawIntBits(this.value[2]);
        int a = Float.floatToRawIntBits(this.value[3]);
        if (r != this.packedR || g != this.packedG || b != this.packedB || a != this.packedA) {
            this.packed = this.pack();
            this.packedR = r;
            this.packedG = g;
            this.packedB = b;
            this.packedA = a;
        }
        return this.packed;
    }

    public int rgb() {
        return this.argb() & 0xFFFFFF;
    }

    public int argbWithAlpha(int alpha) {
        return ColorSetting.clamp255(alpha) << 24 | this.argb() & 0xFFFFFF;
    }

    private int pack() {
        return ColorSetting.pack(this.value[0], this.value[1], this.value[2], this.value[3]);
    }

    private static int pack(float red, float green, float blue, float alpha) {
        int a = Math.round(ColorSetting.clamp01(alpha) * 255.0f);
        int r = Math.round(ColorSetting.clamp01(red) * 255.0f);
        int g = Math.round(ColorSetting.clamp01(green) * 255.0f);
        int b = Math.round(ColorSetting.clamp01(blue) * 255.0f);
        return a << 24 | r << 16 | g << 8 | b;
    }

    @Override
    public JsonElement compact(JsonElement element) {
        try {
            if (element != null && element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                return new JsonPrimitive(ColorSetting.hex(ColorSetting.pack(ColorSetting.readFloat(object, "r", 0.0f), ColorSetting.readFloat(object, "g", 0.0f), ColorSetting.readFloat(object, "b", 0.0f), ColorSetting.readFloat(object, "a", 1.0f))));
            }
            if (element != null && element.isJsonArray() && element.getAsJsonArray().size() >= 3) {
                JsonArray array = element.getAsJsonArray();
                float alpha = array.size() > 3 ? array.get(3).getAsFloat() : 1.0f;
                return new JsonPrimitive(ColorSetting.hex(ColorSetting.pack(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), alpha)));
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return element;
    }

    public static String hex(int argb) {
        return String.format("%08X", argb);
    }

    @Override
    public JsonElement serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("r", (Number)Float.valueOf(ColorSetting.clamp01(this.value[0])));
        object.addProperty("g", (Number)Float.valueOf(ColorSetting.clamp01(this.value[1])));
        object.addProperty("b", (Number)Float.valueOf(ColorSetting.clamp01(this.value[2])));
        object.addProperty("a", (Number)Float.valueOf(ColorSetting.clamp01(this.value[3])));
        return object;
    }

    @Override
    protected void deserializeValue(JsonElement element) {
        if (element == null) {
            return;
        }
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                this.value[0] = ColorSetting.readFloat(object, "r", this.value[0]);
                this.value[1] = ColorSetting.readFloat(object, "g", this.value[1]);
                this.value[2] = ColorSetting.readFloat(object, "b", this.value[2]);
                this.value[3] = ColorSetting.readFloat(object, "a", this.value[3]);
                this.clampValues();
                return;
            }
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (int i = 0; i < Math.min(4, array.size()); ++i) {
                    this.value[i] = ColorSetting.clamp01(array.get(i).getAsFloat());
                }
                return;
            }
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isString()) {
                    this.setFromHex(primitive.getAsString());
                } else if (primitive.isNumber()) {
                    this.setFromArgb(primitive.getAsInt());
                }
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }

    private void setFromHex(String raw) {
        Object hex = raw.trim();
        if (((String)hex).startsWith("#")) {
            hex = ((String)hex).substring(1);
        }
        if (((String)hex).length() == 6) {
            hex = "FF" + (String)hex;
        }
        if (((String)hex).length() != 8) {
            return;
        }
        this.setFromArgb((int)Long.parseLong((String)hex, 16));
    }

    private void setFromArgb(int argb) {
        this.value[0] = (float)(argb >> 16 & 0xFF) / 255.0f;
        this.value[1] = (float)(argb >> 8 & 0xFF) / 255.0f;
        this.value[2] = (float)(argb & 0xFF) / 255.0f;
        this.value[3] = (float)(argb >>> 24 & 0xFF) / 255.0f;
    }

    private void clampValues() {
        for (int i = 0; i < this.value.length; ++i) {
            this.value[i] = ColorSetting.clamp01(this.value[i]);
        }
    }

    private static float readFloat(JsonObject object, String name, float fallback) {
        if (!object.has(name)) {
            return fallback;
        }
        return ColorSetting.clamp01(object.get(name).getAsFloat());
    }

    private static float clamp01(float v) {
        if (Float.isNaN(v) || Float.isInfinite(v)) {
            return 1.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }
}

