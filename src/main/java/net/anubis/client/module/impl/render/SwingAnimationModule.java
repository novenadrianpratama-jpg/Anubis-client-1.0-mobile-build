/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1569
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package net.anubis.client.module.impl.render;

import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ModeSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.combat.CombatTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public final class SwingAnimationModule
extends ClientModule {
    public static volatile SwingAnimationModule active;
    private static final String KHOPESH = "Slash";
    private static final String ANKH = "Chop";
    private static final String SICKLE = "Hook";
    private static final String SCARAB = "Spin";
    private static final String HORUS = "Double Tap";
    private static final String SPHINX = "Push";
    private static final long COMBAT_WINDOW_MS = 3000L;
    private static final float PI = (float)Math.PI;
    private static final float TAU = (float)Math.PI * 2;
    private final ModeSetting style = this.addSetting("style", new ModeSetting("Style", "Slash", "Slash", "Chop", "Hook", "Spin", "Double Tap", "Push"));
    private final NumberSetting tempo = this.addSetting("tempo", new NumberSetting("Tempo", 1.0f, 0.4f, 2.5f).step(0.05f));
    private final NumberSetting intensity = this.addSetting("intensity", new NumberSetting("Intensity", 1.0f, 0.2f, 2.0f).step(0.05f));
    private final BooleanSetting offhand = this.addSetting("offhand", new BooleanSetting("Offhand Too", true));
    private final BooleanSetting combatOnly = this.addSetting("combat-only", new BooleanSetting("Combat Only", false));
    private int styleIndex;
    private boolean styled;

    public SwingAnimationModule() {
        super("swing-animation", "Swing Animation", "Changes your first-person swing animation.", ModuleCategory.VISUALS);
        this.style.onChange(this::cacheStyle);
        this.cacheStyle();
    }

    @Override
    protected void onEnable() {
        this.latch();
        active = this;
    }

    @Override
    protected void onDisable() {
        active = null;
    }

    private void cacheStyle() {
        this.styleIndex = Math.max(0, this.style.modes().indexOf(this.style.get()));
    }

    public void latch() {
        this.styled = !this.combatOnly.get() || SwingAnimationModule.inCombat();
    }

    private static boolean inCombat() {
        if (CombatTarget.lastHit(3000L) != null) {
            return true;
        }
        class_1297 looked = class_310.method_1551().field_1692;
        return (looked instanceof class_1657 || looked instanceof class_1569) && looked.method_5805();
    }

    public boolean covers(boolean mainHand) {
        return (mainHand || this.offhand.get()) && this.styled;
    }

    public int swingTicks(int ticks) {
        if (!this.styled) {
            return ticks;
        }
        return Math.max(1, Math.round((float)ticks / this.tempo.get()));
    }

    public void pose(class_4587 pose, float progress, int side) {
        if (progress <= 0.0f || progress >= 1.0f) {
            return;
        }
        float k = this.intensity.get();
        switch (this.styleIndex) {
            case 1: {
                SwingAnimationModule.ankh(pose, progress, side, k);
                break;
            }
            case 2: {
                SwingAnimationModule.sickle(pose, progress, side, k);
                break;
            }
            case 3: {
                SwingAnimationModule.scarab(pose, progress, side, k);
                break;
            }
            case 4: {
                SwingAnimationModule.horus(pose, progress, side, k);
                break;
            }
            case 5: {
                SwingAnimationModule.sphinx(pose, progress, side, k);
                break;
            }
            default: {
                SwingAnimationModule.khopesh(pose, progress, side, k);
            }
        }
    }

    private static void khopesh(class_4587 pose, float t, int side, float k) {
        float a = SwingAnimationModule.crest(t, 0.3f) * k;
        pose.method_46416((float)side * -0.3f * a, 0.05f * class_3532.method_15374((double)((float)Math.PI * t)) * k, -0.1f * a);
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)side * 64.0f * a));
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * 48.0f * a));
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-14.0f * a));
    }

    private static void ankh(class_4587 pose, float t, int side, float k) {
        float raise = t < 0.28f ? class_3532.method_15374((double)((float)Math.PI * t / 0.28f)) * k : 0.0f;
        float strike = t <= 0.18f ? 0.0f : SwingAnimationModule.crest((t - 0.18f) / 0.82f, 0.28f) * k;
        pose.method_46416((float)side * -0.06f * strike, 0.16f * raise - 0.1f * strike, 0.06f * raise - 0.24f * strike);
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(30.0f * raise - 105.0f * strike));
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * 10.0f * strike));
    }

    private static void sickle(class_4587 pose, float t, int side, float k) {
        float a = SwingAnimationModule.crest(t, 0.42f);
        float hook = a * a * k;
        pose.method_46416((float)side * -0.1f * (a *= k), -0.24f * a, -0.06f * a);
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * -100.0f * a));
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)side * 30.0f * hook));
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-25.0f * a));
    }

    private static void scarab(class_4587 pose, float t, int side, float k) {
        float turn = (float)Math.PI * 2 * t;
        float cos = class_3532.method_15362((double)turn);
        float sin = class_3532.method_15374((double)turn);
        float body = class_3532.method_15374((double)((float)Math.PI * t)) * k;
        float r = 0.17f * k;
        pose.method_46416((float)side * r * 0.6f * (cos - 1.0f), r * sin, -0.12f * body);
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * 40.0f * sin * k));
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-40.0f * body));
    }

    private static void horus(class_4587 pose, float t, int side, float k) {
        float beat = class_3532.method_15374((double)((float)Math.PI * 2 * t));
        float w = beat * beat * (t < 0.5f ? 1.0f : 0.65f) * k;
        pose.method_46416((float)side * -0.08f * w, 0.06f * w, -0.1f * w);
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)side * 26.0f * w));
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-50.0f * w));
        pose.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)side * -22.0f * w));
    }

    private static void sphinx(class_4587 pose, float t, int side, float k) {
        float a = (0.5f - 0.5f * class_3532.method_15362((double)((float)Math.PI * 2 * t))) * k;
        pose.method_46416(0.0f, -0.06f * a, -0.1f * a);
        pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(-24.0f * a));
        pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)side * 10.0f * a));
    }

    private static float crest(float t, float peak) {
        if (t <= 0.0f || t >= 1.0f) {
            return 0.0f;
        }
        if (t < peak) {
            float u = 1.0f - t / peak;
            return 1.0f - u * u * u;
        }
        float u = (t - peak) / (1.0f - peak);
        return 1.0f - u * u * (3.0f - 2.0f * u);
    }
}

