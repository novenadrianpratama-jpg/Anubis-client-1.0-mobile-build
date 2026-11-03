/*
 * Patched BuildConfig — SIGN_CERT_PARTS cleared so OfficialBuild.check()
 * returns State.NOT_CHECKED (unsigned local build) instead of State.MODIFIED
 * when the JAR is rebuilt from source without the original signing key.
 */
package net.anubis.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public final class BuildConfig {
    public static final boolean DEBUG = false;
    // Empty array → String.join("", parts) == "" → OfficialBuild treats as
    // unsigned dev build (NOT_CHECKED), skipping the tamper-detection path.
    public static final String[] SIGN_CERT_PARTS = new String[]{};

    private BuildConfig() {}
}
