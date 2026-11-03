/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 */
package net.anubis.client.easyplace.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.anubis.client.easyplace.EasyPlace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2338;
import net.minecraft.class_310;

@Environment(value=EnvType.CLIENT)
public final class BaritoneCompat {
    private static final String[] CANDIDATES = new String[]{"baritone.api.BaritoneAPI", "baritone.BaritoneAPI"};
    private static final List<String> BUILD_SETTINGS = List.of("allowBreak", "allowPlace");
    private static Boolean available;
    private static String resolvedClass;
    private static Object cachedBaritone;
    private static boolean commanded;
    private static Map<String, String> restore;

    private BaritoneCompat() {
    }

    public static boolean isAvailable() {
        if (available == null) {
            available = false;
            for (String name : CANDIDATES) {
                try {
                    Class.forName(name);
                    resolvedClass = name;
                    available = true;
                    break;
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
        }
        return available;
    }

    public static boolean canWalk() {
        return BaritoneCompat.isAvailable() || BaritoneCompat.chatPrefix() != null;
    }

    public static void goTo(class_2338 feet) {
        if (BaritoneCompat.isAvailable()) {
            BaritoneCompat.setGoal("baritone.api.pathing.goals.GoalBlock", feet, -1);
            return;
        }
        BaritoneCompat.command("goto " + feet.method_10263() + " " + feet.method_10264() + " " + feet.method_10260());
    }

    public static void goNear(class_2338 pos, int range) {
        if (!BaritoneCompat.isAvailable()) {
            BaritoneCompat.goTo(pos);
            return;
        }
        BaritoneCompat.setGoal("baritone.api.pathing.goals.GoalNear", pos, range);
    }

    public static void cancel() {
        if (!BaritoneCompat.isAvailable()) {
            if (commanded) {
                BaritoneCompat.command("stop");
            }
            commanded = false;
            return;
        }
        try {
            Object baritone = BaritoneCompat.getBaritone();
            if (baritone == null) {
                return;
            }
            Method getPathing = BaritoneCompat.findMethod(baritone, "getPathingBehavior");
            if (getPathing == null) {
                return;
            }
            Object pathingBehavior = getPathing.invoke(baritone, new Object[0]);
            if (pathingBehavior == null) {
                return;
            }
            Method cancelMethod = BaritoneCompat.findMethodByName(pathingBehavior, "cancelEverything");
            if (cancelMethod != null) {
                cancelMethod.invoke(pathingBehavior, new Object[0]);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void lockForBuild() {
        if (!restore.isEmpty()) {
            return;
        }
        HashMap<String, String> saved = new HashMap<String, String>();
        Map<String, String> file = BaritoneCompat.readSettingsFile();
        for (String name : BUILD_SETTINGS) {
            saved.put(name, file.getOrDefault(name.toLowerCase(), "true"));
            BaritoneCompat.setSetting(name, false);
        }
        restore = saved;
    }

    public static void restoreSettings() {
        Map<String, String> saved = restore;
        restore = Map.of();
        saved.forEach((name, value) -> BaritoneCompat.setSetting(name, Boolean.parseBoolean(value)));
    }

    private static void setSetting(String name, boolean value) {
        if (BaritoneCompat.isAvailable()) {
            try {
                Class<?> apiClass = Class.forName(resolvedClass);
                Object settings = apiClass.getMethod("getSettings", new Class[0]).invoke(null, new Object[0]);
                Object setting = settings.getClass().getField(name).get(settings);
                Field field = setting.getClass().getField("value");
                field.set(setting, value);
            }
            catch (Exception e) {
                BaritoneCompat.log("setting " + name + " failed: " + e.getClass().getSimpleName());
            }
            return;
        }
        BaritoneCompat.command("set " + name + " " + value);
    }

    private static void setGoal(String goalClassName, class_2338 pos, int range) {
        try {
            Object goal;
            Object baritone = BaritoneCompat.getBaritone();
            if (baritone == null) {
                BaritoneCompat.log("getBaritone returned null");
                return;
            }
            Class<?> goalClass = Class.forName(goalClassName);
            if (range >= 0) {
                try {
                    goal = goalClass.getConstructor(class_2338.class, Integer.TYPE).newInstance(pos, range);
                }
                catch (NoSuchMethodException e) {
                    goal = goalClass.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(pos.method_10263(), pos.method_10264(), pos.method_10260(), range);
                }
            } else {
                try {
                    goal = goalClass.getConstructor(class_2338.class).newInstance(pos);
                }
                catch (NoSuchMethodException e) {
                    goal = goalClass.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(pos.method_10263(), pos.method_10264(), pos.method_10260());
                }
            }
            Method getGoalProcess = BaritoneCompat.findMethod(baritone, "getCustomGoalProcess");
            if (getGoalProcess == null) {
                BaritoneCompat.log("getCustomGoalProcess method not found");
                return;
            }
            Object goalProcess = getGoalProcess.invoke(baritone, new Object[0]);
            if (goalProcess == null) {
                BaritoneCompat.log("getCustomGoalProcess returned null");
                return;
            }
            Method setGoal = BaritoneCompat.findMethodByName(goalProcess, "setGoalAndPath");
            if (setGoal == null) {
                BaritoneCompat.log("setGoalAndPath method not found");
                return;
            }
            setGoal.invoke(goalProcess, goal);
        }
        catch (Exception e) {
            BaritoneCompat.log("goal failed: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void command(String text) {
        String prefix = BaritoneCompat.chatPrefix();
        class_310 mc = class_310.method_1551();
        if (prefix == null || mc.method_1562() == null) {
            return;
        }
        mc.method_1562().method_45729(prefix + text);
        commanded = !text.equals("stop");
    }

    private static String chatPrefix() {
        if (!FabricLoader.getInstance().isModLoaded("baritone")) {
            return null;
        }
        Map<String, String> settings = BaritoneCompat.readSettingsFile();
        if ("false".equals(settings.get("prefixcontrol"))) {
            return null;
        }
        String prefix = settings.getOrDefault("prefix", "#");
        return prefix.isBlank() || prefix.startsWith("/") ? null : prefix;
    }

    private static Map<String, String> readSettingsFile() {
        HashMap<String, String> out = new HashMap<String, String>();
        Path file = class_310.method_1551().field_1697.toPath().resolve("baritone").resolve("settings.txt");
        try {
            if (!Files.isRegularFile(file, new LinkOption[0])) {
                return out;
            }
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                int space = trimmed.indexOf(32);
                if (trimmed.isEmpty() || trimmed.startsWith("#") || space <= 0) continue;
                out.put(trimmed.substring(0, space).toLowerCase(), trimmed.substring(space + 1).trim());
            }
        }
        catch (Exception e) {
            BaritoneCompat.log("could not read settings.txt: " + e.getClass().getSimpleName());
        }
        return out;
    }

    private static void log(String message) {
        EasyPlace.LOG.warn("[Baritone] {}", (Object)message);
    }

    private static Object getBaritone() throws Exception {
        if (cachedBaritone != null) {
            return cachedBaritone;
        }
        Class<?> apiClass = Class.forName(resolvedClass);
        Object provider = apiClass.getMethod("getProvider", new Class[0]).invoke(null, new Object[0]);
        if (provider == null) {
            return null;
        }
        Method getPrimary = BaritoneCompat.findMethodByName(provider, "getPrimaryBaritone");
        if (getPrimary == null) {
            return null;
        }
        cachedBaritone = getPrimary.invoke(provider, new Object[0]);
        return cachedBaritone;
    }

    private static Method findMethod(Object obj, String name) {
        try {
            return obj.getClass().getMethod(name, new Class[0]);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static Method findMethodByName(Object obj, String name) {
        for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Method m : c.getMethods()) {
                if (!m.getName().equals(name)) continue;
                return m;
            }
        }
        return null;
    }

    static {
        restore = Map.of();
    }
}

