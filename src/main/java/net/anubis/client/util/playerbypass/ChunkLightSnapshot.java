/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util.playerbypass;

import java.util.Arrays;
import net.anubis.client.util.playerbypass.LightSectionStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ChunkLightSnapshot {
    public static final int BASE_DETECT_CEILING_Y = 62;
    public final int sectionCount;
    public final int bottomSectionY;
    public final LightSectionStatus[] sky;

    public ChunkLightSnapshot(int sectionCount, int bottomSectionY) {
        this.sectionCount = sectionCount;
        this.bottomSectionY = bottomSectionY;
        this.sky = new LightSectionStatus[sectionCount + 2];
        Arrays.fill((Object[])this.sky, (Object)LightSectionStatus.UNKNOWN);
    }

    public boolean hasBaseSignal() {
        for (int i = 1; i < this.sectionCount + 1; ++i) {
            if (!ChunkLightSnapshot.candidate(i, this.bottomSectionY) || this.sky[i] != LightSectionStatus.ZEROED_PRESENT) continue;
            return true;
        }
        return false;
    }

    static boolean candidate(int index, int bottomSectionY) {
        return (bottomSectionY + index - 1) * 16 + 15 < 62;
    }
}

