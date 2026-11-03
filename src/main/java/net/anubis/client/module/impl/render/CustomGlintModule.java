/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1044
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_918
 *  net.minecraft.class_9848
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.impl.render.CosmeticAccent;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_918;
import net.minecraft.class_9848;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class CustomGlintModule
extends ClientModule {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    public static volatile CustomGlintModule active;
    private static final String COLOR_CUSTOM = "Custom";
    private static final String COLOR_ACCENT = "Accent";
    private final ModeSetting colorMode = this.addSetting("color-mode", new ModeSetting("Color", "Custom", "Custom", "Accent"));
    private final ColorSetting itemColor = this.addSetting("item-color", new ColorSetting("Item Color", -9524994));
    private final BooleanSetting separateArmor = this.addSetting("separate-armor", new BooleanSetting("Separate Armor", false));
    private final ColorSetting armorColor = this.addSetting("armor-color", new ColorSetting("Armor Color", -3697942));
    private final NumberSetting intensity = this.addSetting("intensity", new NumberSetting("Intensity", 1.0f, 0.05f, 1.0f).step(0.05f));
    private final NumberSetting speed = this.addSetting("speed", new NumberSetting("Speed", 1.0f, 0.1f, 4.0f).step(0.1f));
    private final NumberSetting patternSize = this.addSetting("pattern-size", new NumberSetting("Pattern Size", 1.0f, 0.25f, 4.0f).step(0.25f));
    private final GlintTexture items = new GlintTexture(class_918.field_43087);
    private final GlintTexture armor = new GlintTexture(class_918.field_43086);
    private double glintClock = Double.NaN;
    private long glintClockReal;

    public CustomGlintModule() {
        super("custom-glint", "Custom Glint", "Recolours the enchantment glint and changes its speed and pattern size.", ModuleCategory.VISUALS);
        this.itemColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.separateArmor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM));
        this.armorColor.visibleWhen(() -> this.colorMode.is(COLOR_CUSTOM) && this.separateArmor.get());
    }

    @Override
    protected void onEnable() {
        this.glintClock = Double.NaN;
        active = this;
        this.on(TickEvent.TYPE, event -> this.refresh());
    }

    @Override
    protected void onDisable() {
        active = null;
        class_310 mc = class_310.method_1551();
        if (mc.method_18854()) {
            this.restore();
        } else {
            mc.execute(() -> {
                if (active == null) {
                    this.restore();
                }
            });
        }
    }

    public long glintMillis(long realMillis) {
        this.glintClock = Double.isNaN(this.glintClock) ? (double)realMillis : (this.glintClock += (double)Math.max(0L, realMillis - this.glintClockReal) * (double)this.speed.get());
        this.glintClockReal = realMillis;
        return (long)this.glintClock;
    }

    public float uvScaleMultiplier() {
        return 1.0f / this.patternSize.get();
    }

    private void refresh() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.method_18506() != null || RenderSystem.tryGetDevice() == null) {
            return;
        }
        int itemRgb = this.colorMode.is(COLOR_ACCENT) ? CosmeticAccent.argb() & 0xFFFFFF : this.itemColor.rgb();
        int armorRgb = this.colorMode.is(COLOR_CUSTOM) && this.separateArmor.get() ? this.armorColor.rgb() : itemRgb;
        float strength = this.intensity.get();
        this.items.tint(mc, itemRgb, strength);
        this.armor.tint(mc, armorRgb, strength);
    }

    private void restore() {
        class_310 mc = class_310.method_1551();
        this.items.restore(mc);
        this.armor.restore(mc);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class GlintTexture {
        private final class_2960 id;
        private int width;
        private int height;
        private int[] original;
        private int[] pattern;
        private GpuTexture written;
        private long writtenKey;
        private GpuTexture unusable;

        GlintTexture(class_2960 id) {
            this.id = id;
        }

        void tint(class_310 mc, int rgb, float intensity) {
            GpuTexture gpu = this.gpuTexture(mc);
            if (gpu == null || gpu.isClosed() || gpu == this.unusable) {
                return;
            }
            long key = (long)(rgb & 0xFFFFFF) << 8 | (long)Math.round(intensity * 255.0f);
            if (gpu == this.written && key == this.writtenKey) {
                return;
            }
            if (gpu != this.written) {
                this.written = null;
                if ((gpu.usage() & 1) == 0 || !this.readShipped(mc, gpu)) {
                    this.unusable = gpu;
                    return;
                }
            }
            if (!this.upload(gpu, this.tinted(rgb, intensity))) {
                this.unusable = gpu;
                return;
            }
            this.written = gpu;
            this.writtenKey = key;
        }

        void restore(class_310 mc) {
            GpuTexture gpu;
            GpuTexture gpuTexture = gpu = this.written != null ? this.gpuTexture(mc) : null;
            if (gpu != null && gpu == this.written && !gpu.isClosed() && this.original != null) {
                this.upload(gpu, this.original);
            }
            this.written = null;
            this.unusable = null;
            this.original = null;
            this.pattern = null;
        }

        private GpuTexture gpuTexture(class_310 mc) {
            try {
                class_1044 texture = mc.method_1531().method_4619(this.id);
                return texture == null ? null : texture.method_68004();
            }
            catch (IllegalStateException notLoaded) {
                return null;
            }
        }

        /*
         * Enabled aggressive exception aggregation
         */
        private boolean readShipped(class_310 mc, GpuTexture gpu) {
            Optional resource = mc.method_1478().method_14486(this.id);
            if (resource.isEmpty()) {
                return false;
            }
            try (InputStream in = ((class_3298)resource.get()).method_14482();){
                boolean bl;
                block21: {
                    int h;
                    int w;
                    class_1011 image;
                    block19: {
                        boolean bl2;
                        block20: {
                            image = class_1011.method_4309((InputStream)in);
                            w = image.method_4307();
                            h = image.method_4323();
                            if (w == gpu.getWidth(0) && h == gpu.getHeight(0)) break block19;
                            bl2 = false;
                            if (image == null) break block20;
                            image.close();
                        }
                        return bl2;
                    }
                    try {
                        int[] pixels = new int[w * h];
                        int[] luma = new int[w * h];
                        int i = 0;
                        for (int y = 0; y < h; ++y) {
                            int x = 0;
                            while (x < w) {
                                int argb;
                                pixels[i] = argb = image.method_61940(x, y);
                                luma[i] = Math.max(class_9848.method_61327((int)argb), Math.max(class_9848.method_61329((int)argb), class_9848.method_61331((int)argb)));
                                ++x;
                                ++i;
                            }
                        }
                        this.width = w;
                        this.height = h;
                        this.original = pixels;
                        this.pattern = luma;
                        bl = true;
                        if (image == null) break block21;
                    }
                    catch (Throwable throwable) {
                        if (image != null) {
                            try {
                                image.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    image.close();
                }
                return bl;
            }
            catch (IOException | RuntimeException e) {
                LOGGER.warn("[Anubis] Custom Glint could not read {}", (Object)this.id, (Object)e);
                return false;
            }
        }

        private int[] tinted(int rgb, float intensity) {
            float r = (float)class_9848.method_61327((int)rgb) * intensity / 255.0f;
            float g = (float)class_9848.method_61329((int)rgb) * intensity / 255.0f;
            float b = (float)class_9848.method_61331((int)rgb) * intensity / 255.0f;
            int[] out = new int[this.pattern.length];
            for (int i = 0; i < out.length; ++i) {
                int l = this.pattern[i];
                out[i] = class_9848.method_61324((int)class_9848.method_61320((int)this.original[i]), (int)Math.round((float)l * r), (int)Math.round((float)l * g), (int)Math.round((float)l * b));
            }
            return out;
        }

        private boolean upload(GpuTexture gpu, int[] argb) {
            boolean bl;
            class_1011 image = new class_1011(this.width, this.height, false);
            try {
                int i = 0;
                for (int y = 0; y < this.height; ++y) {
                    int x = 0;
                    while (x < this.width) {
                        image.method_61941(x, y, argb[i]);
                        ++x;
                        ++i;
                    }
                }
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpu, image);
                bl = true;
            }
            catch (Throwable throwable) {
                try {
                    try {
                        image.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (RuntimeException e) {
                    LOGGER.warn("[Anubis] Custom Glint could not write {}", (Object)this.id, (Object)e);
                    return false;
                }
            }
            image.close();
            return bl;
        }
    }
}

