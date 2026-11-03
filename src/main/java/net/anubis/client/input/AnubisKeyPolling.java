/*
 * Patched for Zalith/Android mobile support.
 * GLFW key polling is wrapped so an UnsatisfiedLinkError on Android
 * (where GLFW is a stub or absent) returns false instead of crashing.
 */
package net.anubis.client.input;

import net.anubis.client.mobile.ZalithCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_3675;

@Environment(value = EnvType.CLIENT)
public final class AnubisKeyPolling {
    private AnubisKeyPolling() {}

    public static boolean isKeyDown(class_1041 window, int key) {
        if (ZalithCompat.isMobile()) {
            // On Android GLFW is either absent or a no-op stub.
            // Key states come from Minecraft's own input pipeline instead.
            return false;
        }
        try {
            return class_3675.method_15987(window, key);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            // GLFW native library not loaded in this runtime — fail safe.
            return false;
        }
    }
}
