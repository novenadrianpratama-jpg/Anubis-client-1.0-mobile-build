/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
 */
package net.anubis.client.security;

import net.anubis.client.security.OfficialBuild;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

@Environment(value=EnvType.CLIENT)
public final class EarlyBuildCheck
implements PreLaunchEntrypoint {
    public void onPreLaunch() {
        OfficialBuild.checkBeforeLaunch();
    }
}

