/*
 * Anubis Client — Zalith/PojavLauncher mobile compatibility layer.
 * Detects Android environment and routes input/GUI away from GLFW/ImGui.
 */
package net.anubis.client.mobile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public final class ZalithCompat {

    private static final boolean MOBILE = detectMobile();

    private ZalithCompat() {}

    /**
     * Returns true when running on Android under Zalith/PojavLauncher.
     * Detection priority:
     *   1. System property "os.name" contains "android"
     *   2. android.os.Build class is loadable (always present on ART)
     *   3. PojavLauncher bridge class present
     */
    private static boolean detectMobile() {
        // Check os.name — PojavLauncher / Zalith sets this to contain "Android"
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("android") || osName.contains("linux") && isAndroidRuntime()) {
            return true;
        }
        // Hard check: try to load android.os.Build — always present on ART, never on JVM
        try {
            Class.forName("android.os.Build");
            return true;
        } catch (ClassNotFoundException ignored) {}
        // PojavLauncher-specific bridge
        try {
            Class.forName("net.kdt.pojavlaunch.utils.MCOptionUtils");
            return true;
        } catch (ClassNotFoundException ignored) {}
        // Zalith Launcher 2 specific
        try {
            Class.forName("com.zalith.launcher.minecraft.MinecraftUtils");
            return true;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    private static boolean isAndroidRuntime() {
        // ART sets java.vm.name to "Dalvik"
        return System.getProperty("java.vm.name", "").contains("Dalvik");
    }

    /** Whether the current runtime is Zalith / PojavLauncher on Android. */
    public static boolean isMobile() {
        return MOBILE;
    }

    /** Safe GLFW window handle — returns 0L on mobile so callers can no-op. */
    public static long safeWindowHandle(long handle) {
        return MOBILE ? 0L : handle;
    }
}
