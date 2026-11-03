/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12076
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_638
 *  net.minecraft.class_7833
 *  net.minecraft.class_9848
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.anubis.client.module.impl.render;

import com.example.client.mixin.AccessorAtmosphereClientLevel;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.impl.render.AtmosphereAurora;
import net.anubis.client.module.impl.render.AtmosphereShaderSky;
import net.anubis.client.module.impl.render.AtmosphereSky;
import net.anubis.client.module.impl.render.AtmosphereStars;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12076;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_7833;
import net.minecraft.class_9848;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class AtmosphereModule
extends ClientModule {
    private static final String CUSTOM = "Custom";
    private static final String AURORA_ALWAYS = "Always";
    private static final String AURORA_COLD = "Cold Biomes";
    private static final String AURORA_OFF = "Off";
    private static final float MOON_SCALE = 1.4f;
    private static final Theme[] THEMES = new Theme[]{new Theme("Borealis", 198414, 729648, 13073302, 3080090, 9063935, 13163263), new Theme("Nebula", 459791, 2233916, 14053304, 16732120, 5991679, 15124735), new Theme("Anubis", 657157, 2890512, 15239498, 16757306, 16730670, 16769200), new Theme("Frost", 264724, 1454154, 11057384, 8382719, 15135999, 0xDDEEFF), new Theme("Crimson", 721926, 3018260, 15227490, 0xFF3355, 16751164, 16765640)};
    private final ModeSetting theme = this.addSetting("theme", new ModeSetting("Theme", "Borealis", "Borealis", "Nebula", "Anubis", "Frost", "Crimson", "Custom"));
    private final ColorSetting nightColor = this.addSetting("night-color", new ColorSetting("Night Sky Color", -16047568));
    private final ColorSetting sunsetColor = this.addSetting("sunset-color", new ColorSetting("Sunset Color", -3703914));
    private final ColorSetting auroraColor = this.addSetting("aurora-color", new ColorSetting("Aurora Color", -13697126));
    private final ColorSetting auroraTopColor = this.addSetting("aurora-top-color", new ColorSetting("Aurora Top Color", -7713281));
    private final ModeSetting skyShader = this.addSetting("sky-shader", new ModeSetting("Sky Shader", "Off", "Off", "Space", "Northern Lights", "Ringed Planet", "Black Hole"));
    private final NumberSetting shaderDaytime = this.addSetting("sky-shader-daytime", new NumberSetting("Daytime Strength", 1.0f, 0.0f, 1.0f).step(0.05f));
    private final NumberSetting shaderBrightness = this.addSetting("sky-shader-brightness", new NumberSetting("Shader Brightness", 1.0f, 0.2f, 2.0f).step(0.1f));
    private final NumberSetting shaderSpeed = this.addSetting("sky-shader-speed", new NumberSetting("Shader Speed", 1.0f, 0.0f, 3.0f).step(0.1f));
    private final NumberSetting shaderStars = this.addSetting("sky-shader-stars", new NumberSetting("Shader Stars", 1.0f, 0.0f, 2.0f).step(0.1f));
    private final NumberSetting shaderEffect = this.addSetting("sky-shader-effect", new NumberSetting("Nebula / Aurora Strength", 1.0f, 0.0f, 2.0f).step(0.1f));
    private final NumberSetting shaderBand = this.addSetting("sky-shader-galaxy", new NumberSetting("Galaxy Band", 1.0f, 0.0f, 2.0f).step(0.1f));
    private final BooleanSetting gradientSky = this.addSetting("gradient-sky", new BooleanSetting("Gradient Sky", true));
    private final BooleanSetting keepInRain = this.addSetting("keep-in-rain", new BooleanSetting("Keep In Rain", true));
    private final BooleanSetting celestialGlow = this.addSetting("sun-moon-glow", new BooleanSetting("Sun & Moon Glow", true));
    private final BooleanSetting stars = this.addSetting("stars", new BooleanSetting("Stars", true));
    private final NumberSetting starBrightness = this.addSetting("star-brightness", new NumberSetting("Star Brightness", 1.3f, 0.5f, 2.0f).step(0.1f));
    private final BooleanSetting milkyWay = this.addSetting("milky-way", new BooleanSetting("Milky Way", true));
    private final NumberSetting milkyWayBrightness = this.addSetting("milky-way-brightness", new NumberSetting("Milky Way Brightness", 1.0f, 0.2f, 2.0f).step(0.1f));
    private final BooleanSetting shootingStars = this.addSetting("shooting-stars", new BooleanSetting("Shooting Stars", true));
    private final ModeSetting aurora = this.addSetting("aurora", new ModeSetting("Aurora", "Always", "Always", "Cold Biomes", "Off"));
    private final NumberSetting auroraBrightness = this.addSetting("aurora-brightness", new NumberSetting("Aurora Brightness", 1.0f, 0.2f, 2.0f).step(0.1f));
    private final NumberSetting auroraSpeed = this.addSetting("aurora-speed", new NumberSetting("Aurora Speed", 1.0f, 0.2f, 3.0f).step(0.1f));
    private final BooleanSetting cloudColors = this.addSetting("cloud-colors", new BooleanSetting("Cloud Colors", true));
    private final BooleanSetting endSky = this.addSetting("end-sky", new BooleanSetting("End Sky", true));
    private final BooleanSetting netherColors = this.addSetting("nether-colors", new BooleanSetting("Nether Colors", true));
    private final AtmosphereSky sky = new AtmosphereSky();
    private final AtmosphereStars starField = new AtmosphereStars();
    private final AtmosphereAurora auroraMesh = new AtmosphereAurora();
    private final AtmosphereShaderSky shaderSky = new AtmosphereShaderSky();
    private final class_4587 world = new class_4587();
    private float night;
    private float rainBrightness = 1.0f;
    private float auroraHere;
    private long lastFrame;
    private boolean moonScaled;
    private boolean shaderRendered;

    public AtmosphereModule() {
        super("atmosphere", "Atmosphere", "A custom sky with stars and an aurora.", ModuleCategory.VISUALS);
        this.nightColor.visibleWhen(() -> this.theme.is(CUSTOM));
        this.sunsetColor.visibleWhen(() -> this.theme.is(CUSTOM));
        this.auroraColor.visibleWhen(() -> this.theme.is(CUSTOM));
        this.auroraTopColor.visibleWhen(() -> this.theme.is(CUSTOM));
        this.shaderDaytime.visibleWhen(this::shaderOn);
        this.shaderBrightness.visibleWhen(this::shaderOn);
        this.shaderSpeed.visibleWhen(this::shaderOn);
        this.shaderStars.visibleWhen(this::shaderOn);
        this.shaderEffect.visibleWhen(this::shaderOn);
        this.shaderBand.visibleWhen(this::shaderOn);
        this.stars.visibleWhen(this::ownSky);
        this.starBrightness.visibleWhen(() -> this.stars.get() && this.ownSky());
        this.milkyWay.visibleWhen(this::ownSky);
        this.milkyWayBrightness.visibleWhen(() -> this.milkyWay.get() && this.ownSky());
        this.aurora.visibleWhen(this::ownSky);
        this.auroraBrightness.visibleWhen(() -> !this.aurora.is(AURORA_OFF) && this.ownSky());
        this.auroraSpeed.visibleWhen(() -> !this.aurora.is(AURORA_OFF) && this.ownSky());
    }

    public static AtmosphereModule get() {
        AtmosphereModule module = ModuleRegistry.get(AtmosphereModule.class);
        return module != null && module.isEnabled() ? module : null;
    }

    @Override
    protected void onDisable() {
        this.sky.close();
        this.starField.close();
        this.auroraMesh.close();
        this.shaderSky.close();
        this.shaderRendered = false;
        this.lastFrame = 0L;
        this.auroraHere = 0.0f;
    }

    public void extract(class_638 level, float partialTick, class_12076 state) {
        this.shaderRendered = false;
        this.night = class_3532.method_15363((float)(state.field_63094 * 2.0f), (float)0.0f, (float)1.0f);
        float rain = 1.0f - state.field_63093;
        float thunder = level.method_8478(partialTick);
        if (this.keepInRain.get()) {
            this.night = Math.max(this.night, AtmosphereModule.clearNight(state.field_63091) * rain);
            state.field_63094 = Math.max(state.field_63094, this.night * 0.5f);
            state.field_63093 = 1.0f;
            rain = 0.0f;
            thunder = 0.0f;
        }
        this.rainBrightness = 1.0f - rain;
        Theme colours = this.currentTheme();
        int flashTicks = ((AccessorAtmosphereClientLevel)level).anubis$atmosphereSkyFlash();
        this.sky.update(state.field_63091, state.field_64465, rain, thunder, flashTicks > 0 ? Math.min(1.0f, ((float)flashTicks - partialTick) / 2.0f) : 0.0f, colours.nightZenith, colours.nightHorizon, colours.twilight);
    }

    public boolean keepsSkyInRain() {
        return this.keepInRain.get();
    }

    private static float clearNight(float sunAngle) {
        float dark = class_3532.method_15363((float)(0.75f - 2.0f * class_3532.method_15362((double)sunAngle)), (float)0.0f, (float)1.0f);
        return dark * dark;
    }

    public boolean skyDisc() {
        if (!this.gradientSky.get() || !this.sky.ready()) {
            return false;
        }
        this.sky.renderDome(this.world);
        this.renderShaderSky();
        return true;
    }

    public void afterSkyDisc() {
        this.renderShaderSky();
    }

    private boolean ownSky() {
        return !this.shaderOn();
    }

    public int lyricGlow() {
        return this.currentTheme().auroraHigh;
    }

    private void renderShaderSky() {
        if (!this.shaderOn()) {
            return;
        }
        Theme colours = this.currentTheme();
        this.shaderRendered = this.shaderSky.render(this.skyShader.get(), this.shaderVisibility(), this.shaderBrightness.get(), this.shaderSpeed.get(), this.shaderStars.get(), this.shaderEffect.get(), this.shaderBand.get(), colours.auroraLow, colours.auroraHigh, colours.milkyWay, colours.nightZenith, colours.nightHorizon);
    }

    private boolean shaderOn() {
        return !this.skyShader.is(AURORA_OFF);
    }

    private boolean shaderAurora() {
        return this.skyShader.is("Northern Lights");
    }

    private float shaderVisibility() {
        return class_3532.method_16439((float)this.night, (float)this.shaderDaytime.get(), (float)1.0f) * (0.4f + 0.6f * this.rainBrightness);
    }

    public boolean replacesSunrise() {
        return this.gradientSky.get() && this.sky.ready();
    }

    public boolean stars(class_4587 pose) {
        if (this.shaderRendered) {
            return true;
        }
        float clear = this.night * this.rainBrightness;
        if (this.milkyWay.get()) {
            this.starField.renderBand(pose, clear * this.milkyWayBrightness.get(), this.currentTheme().milkyWay);
        }
        if (!this.stars.get()) {
            return false;
        }
        this.starField.renderStars(pose, clear, this.starBrightness.get());
        return true;
    }

    public void beforeMoon(class_4587 pose) {
        this.moonScaled = this.celestialGlow.get();
        if (!this.moonScaled) {
            return;
        }
        pose.method_22903();
        pose.method_22905(1.4f, 1.0f, 1.4f);
    }

    public void afterMoon(class_4587 pose) {
        if (this.moonScaled) {
            pose.method_22909();
        }
        this.moonScaled = false;
    }

    public void afterCelestials() {
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.0f : Math.min((float)(now - this.lastFrame) / 1.0E9f, 0.1f);
        this.lastFrame = now;
        if (!this.sky.ready()) {
            return;
        }
        if (this.celestialGlow.get()) {
            this.sky.renderHalos(this.world, this.rainBrightness, this.night);
        }
        if (this.shootingStars.get()) {
            this.sky.renderMeteors(this.world, this.night, this.rainBrightness);
        }
        this.auroraHere += (this.auroraTarget() - this.auroraHere) * Math.min(1.0f, dt * 0.5f);
        if (this.auroraHere > 0.01f && this.night > 0.01f && !this.shaderRendered) {
            Theme colours = this.currentTheme();
            float time = (float)(now % 3600000000000L) / 1.0E9f * this.auroraSpeed.get();
            float intensity = this.auroraHere * this.night * (0.35f + 0.65f * this.rainBrightness) * this.auroraBrightness.get();
            this.auroraMesh.render(this.world, intensity, time, colours.auroraLow, colours.auroraHigh);
        }
    }

    private float auroraTarget() {
        if (this.aurora.is(AURORA_OFF)) {
            return 0.0f;
        }
        if (this.aurora.is(AURORA_ALWAYS)) {
            return 1.0f;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null) {
            return 0.0f;
        }
        class_2338 at = class_2338.method_49638((class_2374)mc.field_1773.method_19418().method_71156());
        return ((class_1959)mc.field_1687.method_23753(at).comp_349()).method_33599(at, mc.field_1687.method_8615()) ? 1.0f : 0.0f;
    }

    public void fog(class_4184 camera, class_638 level, Vector4f color) {
        if (level.method_27983() == class_1937.field_25179) {
            if (this.gradientSky.get() && this.sky.ready()) {
                Vector3fc look = camera.method_19335();
                int rgb = this.sky.horizonToward(look.x(), look.z());
                color.x = (float)(rgb >> 16 & 0xFF) / 255.0f;
                color.y = (float)(rgb >> 8 & 0xFF) / 255.0f;
                color.z = (float)(rgb & 0xFF) / 255.0f;
            }
            if (this.shaderOn()) {
                this.shaderFog(color);
            }
        } else if (level.method_27983() == class_1937.field_25180 && this.netherColors.get()) {
            float luma = 0.2126f * color.x + 0.7152f * color.y + 0.0722f * color.z;
            color.x = class_3532.method_15363((float)((luma + (color.x - luma) * 1.45f) * 1.12f), (float)0.0f, (float)1.0f);
            color.y = class_3532.method_15363((float)((luma + (color.y - luma) * 1.45f) * 1.12f), (float)0.0f, (float)1.0f);
            color.z = class_3532.method_15363((float)((luma + (color.z - luma) * 1.45f) * 1.12f), (float)0.0f, (float)1.0f);
        }
    }

    private void shaderFog(Vector4f color) {
        int horizon = this.currentTheme().nightHorizon;
        float scale = this.shaderAurora() ? 0.85f : 0.3f;
        float amount = this.shaderVisibility() * 0.85f;
        color.x = class_3532.method_16439((float)amount, (float)color.x, (float)((float)(horizon >> 16 & 0xFF) / 255.0f * scale));
        color.y = class_3532.method_16439((float)amount, (float)color.y, (float)((float)(horizon >> 8 & 0xFF) / 255.0f * scale));
        color.z = class_3532.method_16439((float)amount, (float)color.z, (float)((float)(horizon & 0xFF) / 255.0f * scale));
    }

    public int clouds(int argb) {
        class_310 mc = class_310.method_1551();
        if (!this.cloudColors.get() || !this.sky.ready() || mc.field_1687 == null || mc.field_1687.method_27983() != class_1937.field_25179) {
            return argb;
        }
        float golden = this.sky.golden();
        float dark = this.sky.nightness();
        int target = class_9848.method_61319((float)golden, (int)class_9848.method_61319((float)dark, (int)0xFFFFFF, (int)this.currentTheme().nightHorizon), (int)this.sky.twilightGlow());
        float amount = Math.max(golden * 0.75f, dark * 0.55f);
        return AtmosphereModule.keepBrightness(argb, target, amount);
    }

    public boolean endSky() {
        if (!this.endSky.get()) {
            return false;
        }
        long now = System.nanoTime();
        this.sky.renderEndDome(this.world);
        class_4587 turning = new class_4587();
        turning.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)(now % 1200000000000L) / 1.0E9f * 0.3f));
        this.starField.renderNebulae(turning, 1.0f);
        this.starField.renderStars(turning, 1.0f, 1.2f);
        return true;
    }

    private Theme currentTheme() {
        for (Theme each : THEMES) {
            if (!this.theme.is(each.name)) continue;
            return each;
        }
        int horizon = this.nightColor.rgb();
        return new Theme(CUSTOM, AtmosphereSky.scale(horizon, 0.3f), horizon, this.sunsetColor.rgb(), this.auroraColor.rgb(), this.auroraTopColor.rgb(), 0xE8E8FF);
    }

    private static int keepBrightness(int argb, int target, float amount) {
        if (amount <= 0.0f) {
            return argb;
        }
        float r = (float)(argb >> 16 & 0xFF) / 255.0f;
        float g = (float)(argb >> 8 & 0xFF) / 255.0f;
        float b = (float)(argb & 0xFF) / 255.0f;
        float tr = (float)(target >> 16 & 0xFF) / 255.0f;
        float tg = (float)(target >> 8 & 0xFF) / 255.0f;
        float tb = (float)(target & 0xFF) / 255.0f;
        float scale = AtmosphereModule.luminance(r, g, b) / Math.max(AtmosphereModule.luminance(tr, tg, tb), 0.001f);
        r = class_3532.method_16439((float)amount, (float)r, (float)Math.min(1.0f, tr * scale));
        g = class_3532.method_16439((float)amount, (float)g, (float)Math.min(1.0f, tg * scale));
        b = class_3532.method_16439((float)amount, (float)b, (float)Math.min(1.0f, tb * scale));
        return argb & 0xFF000000 | AtmosphereModule.channel(r) << 16 | AtmosphereModule.channel(g) << 8 | AtmosphereModule.channel(b);
    }

    private static float luminance(float r, float g, float b) {
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    private static int channel(float value) {
        return class_3532.method_15340((int)Math.round(value * 255.0f), (int)0, (int)255);
    }

    @Environment(value=EnvType.CLIENT)
    private record Theme(String name, int nightZenith, int nightHorizon, int twilight, int auroraLow, int auroraHigh, int milkyWay) {
    }
}

