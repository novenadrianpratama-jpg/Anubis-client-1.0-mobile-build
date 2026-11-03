/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11516
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_1972
 *  net.minecraft.class_2246
 *  net.minecraft.class_2374
 *  net.minecraft.class_2378
 *  net.minecraft.class_2680
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_310
 *  net.minecraft.class_3612
 *  net.minecraft.class_4076
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 *  net.minecraft.class_6850
 *  net.minecraft.class_761
 *  net.minecraft.class_769
 *  net.minecraft.class_7924
 *  net.minecraft.class_846
 *  net.minecraft.class_846$class_851
 *  org.joml.Vector4f
 */
package net.anubis.client.module.impl.render;

import com.example.client.mixin.AccessorPinkWaterLevelRenderer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLongArray;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.ListenerHandle;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.TickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11516;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2246;
import net.minecraft.class_2374;
import net.minecraft.class_2378;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_3612;
import net.minecraft.class_4076;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_6850;
import net.minecraft.class_761;
import net.minecraft.class_769;
import net.minecraft.class_7924;
import net.minecraft.class_846;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public final class PinkWater {
    public static final String OFF = "Off";
    static final int WARM = 4445678;
    static final int LUKEWARM = 4566514;
    static final int CHERRY_GROVE = 6141935;
    static final int OCEAN = 4159204;
    static final int PALE_GARDEN = 7768221;
    static final int SWAMP = 6388580;
    static final int MANGROVE = 3832426;
    static final int COLD = 4020182;
    static final int MEADOW = 937679;
    static final int FROZEN = 3750089;
    static final int[] BIOMES = new int[]{4445678, 4566514, 6141935, 4159204, 7768221, 6388580, 3832426, 4020182, 937679, 3750089};
    private static final double[][] BIOME_LAB = new double[BIOMES.length][];
    private static final double FOG_HUE = 240.0;
    private static final double FOG_SATURATION = 0.902;
    private static final int SWITCH_ON_TICKS = 60;
    private static final int SWITCH_OFF_TICKS = 40;
    private static final int CHANGE_TICKS = 15;
    private static final float FOG_STEP = 0.05f;
    private static final int UNSEEN_BATCH = 64;
    private static final int UNSEEN_WAIT = 2;
    private static final int UNSEEN_PATIENCE = 20;
    private static final int UNSEEN_TRICKLE = 8;
    private static final double BLEND_POWER = 3.0;
    private static final double LIGHT_CARRY = 0.8;
    private static volatile Design shown;
    private static volatile Design fogDesign;
    private static volatile float fogShare;
    private static Design chosen;
    private static Design pending;
    private static int pendingTicks;
    private static float fogLevel;
    private static final List<Unseen> UNSEEN;
    private static int unseenNext;
    private static class_638 unseenLevel;
    private static class_769 unseenArea;
    private static int unseenWait;
    private static int unseenStalled;
    private static final Object UNSEEN_OWNER;
    private static ListenerHandle<TickEvent> unseenHook;
    private static final int CACHE_SIZE = 64;
    private static final AtomicLongArray CACHE;

    private PinkWater() {
    }

    public static String[] modes() {
        Design[] designs = Design.values();
        String[] modes = new String[designs.length + 1];
        modes[0] = OFF;
        for (int i = 0; i < designs.length; ++i) {
            modes[i + 1] = designs[i].label;
        }
        return modes;
    }

    public static boolean shows() {
        return shown != null;
    }

    public static int water(int rgb, boolean cave) {
        Design design = shown;
        return design == null ? rgb : PinkWater.cached(rgb, cave, design);
    }

    public static byte caveBiome(class_1959 biome) {
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return 0;
        }
        Optional registry = level.method_30349().method_46759(class_7924.field_41236);
        if (registry.isEmpty()) {
            return 0;
        }
        Optional key = ((class_2378)registry.get()).method_29113((Object)biome);
        if (key.isEmpty()) {
            return 1;
        }
        class_5321 k = (class_5321)key.get();
        return k.equals(class_1972.field_29218) || k.equals(class_1972.field_28107) || k.equals(class_1972.field_37543) ? (byte)2 : 1;
    }

    public static int waterFog(int argb) {
        float share = fogShare;
        Design design = fogDesign;
        if (share <= 0.0f || design == null) {
            return argb;
        }
        return PinkWater.mix(argb, PinkWater.waterFog(argb, design), share);
    }

    public static void underwater(Vector4f color) {
        float share = fogShare;
        Design design = fogDesign;
        if (share <= 0.0f || design == null) {
            return;
        }
        float k = 1.0f + ((float)design.fog.shade - 1.0f) * share;
        color.x *= k;
        color.y *= k;
        color.z *= k;
    }

    public static int light(int packed) {
        Design design = shown;
        return design == null ? packed : PinkWater.light(packed, design);
    }

    public static void tick(class_310 mc, boolean peep, Design design) {
        Design wanted;
        Design design2 = wanted = peep ? design : null;
        if (wanted == chosen) {
            pending = null;
            pendingTicks = 0;
        } else {
            int needed;
            if (wanted != pending) {
                pending = wanted;
                pendingTicks = 0;
            }
            int n = chosen == null ? 60 : (needed = design == null || wanted != null ? 15 : 40);
            if (++pendingTicks >= needed) {
                chosen = wanted;
                pending = null;
                pendingTicks = 0;
            }
        }
        class_638 level = mc.field_1687;
        boolean allowed = level != null && level.method_27983() != class_1937.field_25180 && level.method_27983() != class_1937.field_25181;
        PinkWater.show(mc, allowed ? chosen : null);
        fogLevel = Math.max(0.0f, Math.min(1.0f, fogLevel + (shown != null ? 0.05f : -0.05f)));
        fogShare = fogLevel * fogLevel * (3.0f - 2.0f * fogLevel);
    }

    public static void reset(class_310 mc) {
        chosen = null;
        pending = null;
        pendingTicks = 0;
        fogLevel = 0.0f;
        fogShare = 0.0f;
        PinkWater.show(mc, null);
    }

    private static void show(class_310 mc, Design next) {
        class_638 level;
        if (next == shown) {
            return;
        }
        shown = next;
        if (next != null) {
            fogDesign = next;
        }
        if ((level = mc.field_1687) == null || mc.field_1769 == null) {
            PinkWater.clearUnseen();
            return;
        }
        level.method_23784();
        PinkWater.refresh(mc, level);
    }

    private static void refresh(class_310 mc, class_638 level) {
        class_761 renderer = mc.field_1769;
        class_4076 at = class_4076.method_18680((class_2374)mc.field_1773.method_19418().method_71156());
        int radius = mc.field_1690.method_38521() + 1;
        LongOpenHashSet water = new LongOpenHashSet();
        for (int x = at.method_18674() - radius; x <= at.method_18674() + radius; ++x) {
            for (int z = at.method_18687() - radius; z <= at.method_18687() + radius; ++z) {
                class_2818 chunk = level.method_2935().method_21730(x, z);
                if (chunk == null) continue;
                class_2826[] sections = chunk.method_12006();
                for (int i = 0; i < sections.length; ++i) {
                    class_2826 section = sections[i];
                    if (section.method_38292() || !section.method_19523(PinkWater::tinted)) continue;
                    water.add(class_4076.method_18685((int)x, (int)chunk.method_31604(i), (int)z));
                }
            }
        }
        PinkWater.queueUnseen(renderer, level, at, water);
        LongIterator it = water.iterator();
        while (it.hasNext()) {
            long node = it.nextLong();
            renderer.method_8571(class_4076.method_18686((long)node), class_4076.method_18689((long)node), class_4076.method_18690((long)node));
        }
    }

    private static boolean tinted(class_2680 state) {
        return class_3612.field_15910.method_15780(state.method_26227().method_15772()) || state.method_27852(class_2246.field_27097);
    }

    private static void queueUnseen(class_761 renderer, class_638 level, class_4076 at, LongOpenHashSet water) {
        PinkWater.clearUnseen();
        class_769 area = ((AccessorPinkWaterLevelRenderer)renderer).anubis$pinkWaterViewArea();
        if (area == null || area.field_4150 == null || water.isEmpty()) {
            return;
        }
        for (class_846.class_851 section : area.field_4150) {
            long node;
            if (section == null || !water.contains(node = section.method_62975())) continue;
            boolean firstBuild = section.method_72052() == class_11516.field_60935 && !section.method_3672();
            UNSEEN.add(new Unseen(section, node, firstBuild));
        }
        if (UNSEEN.isEmpty()) {
            return;
        }
        UNSEEN.sort(Comparator.comparingLong(unseen -> PinkWater.distance(unseen.node, at)));
        unseenLevel = level;
        unseenArea = area;
        unseenWait = 2;
        unseenStalled = 0;
        if (unseenHook == null) {
            unseenHook = TickEvent.TYPE.listen(UNSEEN_OWNER, Stage.POST, EventPriority.NORMAL, event -> PinkWater.rebuildUnseen(class_310.method_1551()));
        }
    }

    private static void rebuildUnseen(class_310 mc) {
        int budget;
        class_846 dispatcher;
        if (unseenNext >= UNSEEN.size()) {
            PinkWater.clearUnseen();
            PinkWater.unhookUnseen();
            return;
        }
        class_761 renderer = mc.field_1769;
        class_846 class_8462 = dispatcher = renderer == null ? null : renderer.method_34810();
        if (mc.field_1687 != unseenLevel || dispatcher == null || ((AccessorPinkWaterLevelRenderer)renderer).anubis$pinkWaterViewArea() != unseenArea) {
            PinkWater.clearUnseen();
            PinkWater.unhookUnseen();
            return;
        }
        if (unseenWait > 0) {
            --unseenWait;
            return;
        }
        if (dispatcher.method_72046() == 0) {
            budget = 64;
            unseenStalled = 0;
        } else if (++unseenStalled >= 20) {
            budget = 8;
            unseenStalled = 0;
        } else {
            return;
        }
        class_6850 cache = null;
        while (budget > 0 && unseenNext < UNSEEN.size()) {
            Unseen next = UNSEEN.get(unseenNext++);
            class_846.class_851 section = next.section;
            if (section.method_62975() != next.node || !section.method_3672() || section.method_72052() == class_11516.field_60935 && (!next.firstBuild || !section.method_3673())) continue;
            if (cache == null) {
                cache = new class_6850();
            }
            section.method_22777(cache);
            section.method_3662();
            --budget;
        }
    }

    private static void clearUnseen() {
        UNSEEN.clear();
        unseenNext = 0;
        unseenLevel = null;
        unseenArea = null;
    }

    private static void unhookUnseen() {
        if (unseenHook == null) {
            return;
        }
        unseenHook.unregister();
        unseenHook = null;
    }

    private static long distance(long node, class_4076 at) {
        long dx = class_4076.method_18686((long)node) - at.method_18674();
        long dy = class_4076.method_18689((long)node) - at.method_18683();
        long dz = class_4076.method_18690((long)node) - at.method_18687();
        return dx * dx + dy * dy + dz * dz;
    }

    public static int water(int rgb, Design design) {
        return PinkWater.water(rgb, false, design);
    }

    public static int water(int rgb, boolean cave, Design design) {
        double[] lab;
        int known = PinkWater.index(rgb & 0xFFFFFF);
        double[] dArray = lab = known >= 0 ? (double[])design.water[known].clone() : PinkWater.between(PinkWater.oklab(rgb), design);
        if (cave) {
            for (int i = 0; i < 3; ++i) {
                int n = i;
                lab[n] = lab[n] + design.cave[i];
            }
        }
        return PinkWater.fromOklab(lab[0], lab[1], lab[2]);
    }

    private static double[] between(double[] in, Design design) {
        double weights = 0.0;
        double l = 0.0;
        double a = 0.0;
        double b = 0.0;
        double inL = 0.0;
        double inC = 0.0;
        for (int i = 0; i < BIOMES.length; ++i) {
            double[] at = BIOME_LAB[i];
            double[] out = design.water[i];
            double dl = in[0] - at[0];
            double da = in[1] - at[1];
            double db = in[2] - at[2];
            double w = 1.0 / Math.pow(Math.max(dl * dl + da * da + db * db, 1.0E-12), 1.5);
            weights += w;
            l += w * out[0];
            a += w * out[1];
            b += w * out[2];
            inL += w * at[0];
            inC += w * Math.hypot(at[1], at[2]);
        }
        l /= weights;
        l = PinkWater.clamp(l + (in[0] - inL / weights) * 0.8, 0.3, 0.92);
        double vivid = Math.sqrt(PinkWater.clamp(Math.hypot(in[1], in[2]) / Math.max(inC / weights, 1.0E-6), 0.4, 1.6));
        return new double[]{l, (a /= weights) * vivid, (b /= weights) * vivid};
    }

    static int index(int rgb) {
        for (int i = 0; i < BIOMES.length; ++i) {
            if (BIOMES[i] != rgb) continue;
            return i;
        }
        return -1;
    }

    public static int waterFog(int argb, Design design) {
        Fog fog = design.fog;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        double value = (double)max / 255.0;
        double saturation = max == 0 ? 0.0 : (double)(max - min) / (double)max;
        double hue = 0.0;
        if (max != min) {
            double span = max - min;
            hue = max == r ? 60.0 * ((double)(g - b) / span) : (max == g ? 60.0 * ((double)(b - r) / span + 2.0) : 60.0 * ((double)(r - g) / span + 4.0));
        }
        double follow = fog.hueFollow * PinkWater.smooth(0.1, 0.3, saturation);
        double outHue = fog.hue + PinkWater.clamp(-follow * PinkWater.wrap(hue - 240.0), -fog.maxShift, fog.maxShift);
        double outSaturation = PinkWater.clamp(fog.saturation * Math.pow(PinkWater.clamp(saturation / 0.902, 0.25, 1.1), 0.7), 0.0, 1.0);
        double outValue = PinkWater.clamp(value * fog.value, 0.0, 1.0);
        return argb & 0xFF000000 | PinkWater.hsv(outHue, outSaturation, outValue);
    }

    public static int underwater(int rgb, Design design) {
        double k = design.fog.shade;
        return PinkWater.srgbChannel((double)(rgb >> 16 & 0xFF) / 255.0 * k) << 16 | PinkWater.srgbChannel((double)(rgb >> 8 & 0xFF) / 255.0 * k) << 8 | PinkWater.srgbChannel((double)(rgb & 0xFF) / 255.0 * k);
    }

    public static int light(int packed, Design design) {
        int sky = packed >>> 20 & 0xF;
        int glow = (design.glow * sky + 7) / 15 << 4;
        if (glow <= 0 || (packed & 0xFF) >= glow) {
            return packed;
        }
        return packed & 0xFFFFFF00 | glow;
    }

    private static int cached(int rgb, boolean cave, Design design) {
        int key = rgb & 0xFFFFFF | design.ordinal() + 1 << 24 | (cave ? 0x40000000 : 0);
        int slot = key * -1640531535 >>> 26 & 0x3F;
        long entry = CACHE.getOpaque(slot);
        if ((int)(entry >>> 32) == key) {
            return (int)entry;
        }
        int out = PinkWater.water(rgb, cave, design);
        CACHE.setOpaque(slot, (long)key << 32 | (long)out & 0xFFFFFFFFL);
        return out;
    }

    static double[] oklab(int rgb) {
        double r = PinkWater.linear((double)(rgb >> 16 & 0xFF) / 255.0);
        double g = PinkWater.linear((double)(rgb >> 8 & 0xFF) / 255.0);
        double b = PinkWater.linear((double)(rgb & 0xFF) / 255.0);
        double l = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b);
        double m = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b);
        double s = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b);
        return new double[]{0.2104542553 * l + 0.793617785 * m - 0.0040720468 * s, 1.9779984951 * l - 2.428592205 * m + 0.4505937099 * s, 0.0259040371 * l + 0.7827717662 * m - 0.808675766 * s};
    }

    private static int fromOklab(double l, double a, double b) {
        double[] out = new double[3];
        if (!PinkWater.fromOklab(l, a, b, out)) {
            double low = 0.0;
            double high = 1.0;
            for (int i = 0; i < 14; ++i) {
                double mid = (low + high) * 0.5;
                if (PinkWater.fromOklab(l, a * mid, b * mid, out)) {
                    low = mid;
                    continue;
                }
                high = mid;
            }
            PinkWater.fromOklab(l, a * low, b * low, out);
        }
        return PinkWater.channel(out[0]) << 16 | PinkWater.channel(out[1]) << 8 | PinkWater.channel(out[2]);
    }

    private static boolean fromOklab(double l, double a, double b, double[] out) {
        double l1 = l + 0.3963377774 * a + 0.2158037573 * b;
        double m1 = l - 0.1055613458 * a - 0.0638541728 * b;
        double s1 = l - 0.0894841775 * a - 1.291485548 * b;
        l1 = l1 * l1 * l1;
        m1 = m1 * m1 * m1;
        s1 = s1 * s1 * s1;
        out[0] = 4.0767416621 * l1 - 3.3077115913 * m1 + 0.2309699292 * s1;
        out[1] = -1.2684380046 * l1 + 2.6097574011 * m1 - 0.3413193965 * s1;
        out[2] = -0.0041960863 * l1 - 0.7034186147 * m1 + 1.707614701 * s1;
        double e = 1.0E-4;
        return out[0] >= -e && out[0] <= 1.0 + e && out[1] >= -e && out[1] <= 1.0 + e && out[2] >= -e && out[2] <= 1.0 + e;
    }

    private static int hsv(double hueDegrees, double saturation, double value) {
        double g;
        double r;
        double h = (hueDegrees % 360.0 + 360.0) % 360.0 / 60.0;
        int sector = (int)Math.floor(h) % 6;
        double f = h - Math.floor(h);
        double p = value * (1.0 - saturation);
        double q = value * (1.0 - saturation * f);
        double t = value * (1.0 - saturation * (1.0 - f));
        return PinkWater.srgbChannel(r) << 16 | PinkWater.srgbChannel(g) << 8 | PinkWater.srgbChannel(switch (sector) {
            case 0 -> {
                r = value;
                g = t;
                yield p;
            }
            case 1 -> {
                r = q;
                g = value;
                yield p;
            }
            case 2 -> {
                r = p;
                g = value;
                yield t;
            }
            case 3 -> {
                r = p;
                g = q;
                yield value;
            }
            case 4 -> {
                r = t;
                g = p;
                yield value;
            }
            default -> {
                r = value;
                g = p;
                yield q;
            }
        });
    }

    private static double linear(double c) {
        return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }

    private static int channel(double linear) {
        double c = PinkWater.clamp(linear, 0.0, 1.0);
        double s = c <= 0.0031308 ? c * 12.92 : 1.055 * Math.pow(c, 0.4166666666666667) - 0.055;
        return PinkWater.srgbChannel(s);
    }

    private static int srgbChannel(double c) {
        return (int)Math.round(PinkWater.clamp(c, 0.0, 1.0) * 255.0);
    }

    private static int mix(int from, int to, float t) {
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * t);
        return from & 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static double wrap(double degrees) {
        double d = degrees % 360.0;
        if (d > 180.0) {
            d -= 360.0;
        }
        if (d <= -180.0) {
            d += 360.0;
        }
        return d;
    }

    private static double smooth(double from, double to, double x) {
        double t = PinkWater.clamp((x - from) / (to - from), 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);
    }

    private static double clamp(double v, double min, double max) {
        return v < min ? min : (v > max ? max : v);
    }

    static {
        for (int i = 0; i < BIOMES.length; ++i) {
            PinkWater.BIOME_LAB[i] = PinkWater.oklab(BIOMES[i]);
        }
        UNSEEN = new ArrayList<Unseen>();
        UNSEEN_OWNER = new Object();
        CACHE = new AtomicLongArray(64);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Design {
        ROSE_QUARTZ("Rose Quartz", new int[]{16742585, 15429040, 15626161, 13392801, 12357803, 11165580, 10508930, 12476315, 12146838, 11817105}, 11862992, new Fog(338.0, 0.25, 15.0, 0.47, 1.0, 0.84), 6),
        PINK_SALT_LAKE("Pink Salt Lake", new int[]{16759265, 16361428, 16427734, 15633086, 14923972, 14781610, 14452650, 15762095, 14908074, 14119845}, 12279295, new Fog(340.0, 0.15, 10.0, 0.36, 1.6, 0.77), 7),
        SUNSET_LAGOON("Sunset Lagoon", new int[]{16751278, 16746659, 16747428, 16737936, 14130594, 14444925, 13789560, 15753090, 14900600, 14113390}, 0xFF51FF, new Fog(352.0, 0.3, 15.0, 0.46, 1.15, 0.81), 2),
        NEON_NIGHT("Neon Night", new int[]{16740085, 15753959, 15885545, 14175952, 13076403, 13459385, 12802735, 12466090, 11808155, 11150220}, 15466751, new Fog(322.0, 0.2, 12.0, 0.5, 1.0, 0.82), 8),
        ORCHID_DEPTHS("Orchid Depths", new int[]{13459425, 12538831, 12670418, 11026355, 10317461, 9193110, 9518220, 9515665, 8531337, 7547010}, 6815936, new Fog(300.0, 0.3, 15.0, 0.47, 0.9, 0.76), 1),
        COTTON_CANDY("Cotton Candy", new int[]{0xFAA0FF, 15569654, 15701240, 14253288, 14595804, 12157650, 12156370, 12485350, 11959777, 11499740}, 8402175, new Fog(300.0, 0.3, 20.0, 0.38, 1.5, 0.77), 3);

        public static final Design DEFAULT;
        private final String label;
        final double[][] water;
        final double[] cave;
        final Fog fog;
        final int glow;

        private Design(String label, int[] water, int cave, Fog fog, int glow) {
            if (water.length != BIOMES.length) {
                throw new IllegalArgumentException(label + ": one colour per biome");
            }
            this.label = label;
            this.water = new double[water.length][];
            for (int i = 0; i < water.length; ++i) {
                this.water[i] = PinkWater.oklab(water[i]);
            }
            double[] ocean = this.water[PinkWater.index(4159204)];
            double[] lab = PinkWater.oklab(cave);
            this.cave = new double[]{lab[0] - ocean[0], lab[1] - ocean[1], lab[2] - ocean[2]};
            this.fog = fog;
            this.glow = glow;
        }

        public String label() {
            return this.label;
        }

        public static Design byLabel(String label) {
            for (Design design : Design.values()) {
                if (!design.label.equals(label)) continue;
                return design;
            }
            return null;
        }

        static {
            DEFAULT = ROSE_QUARTZ;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Fog(double hue, double hueFollow, double maxShift, double saturation, double value, double shade) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Unseen(class_846.class_851 section, long node, boolean firstBuild) {
    }
}

