/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_6606
 */
package net.anubis.client.util.playerbypass;

import java.util.BitSet;
import java.util.List;
import net.anubis.client.util.playerbypass.ChunkLightSnapshot;
import net.anubis.client.util.playerbypass.LightSectionStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_6606;

@Environment(value=EnvType.CLIENT)
public final class LightDataClassifier {
    private static final int NIBBLES_PER_SECTION = 2048;

    private LightDataClassifier() {
    }

    public static LightSectionStatus classifyPayload(byte[] payload) {
        if (payload == null || payload.length != 2048) {
            return LightSectionStatus.UNKNOWN;
        }
        boolean allZero = true;
        boolean allMax = true;
        for (byte b : payload) {
            if (b != 0) {
                allZero = false;
            }
            if (b != -1) {
                allMax = false;
            }
            if (allZero || allMax) continue;
            return LightSectionStatus.NORMAL;
        }
        if (allZero) {
            return LightSectionStatus.ZEROED_PRESENT;
        }
        if (allMax) {
            return LightSectionStatus.UNIFORM_MAX;
        }
        return LightSectionStatus.NORMAL;
    }

    public static void apply(class_6606 data, ChunkLightSnapshot snapshot) {
        LightDataClassifier.applyTrack(data.method_38601(), data.method_38604(), data.method_38606(), snapshot.sky);
    }

    static int sentNotDark(class_6606 data, int sectionCount, int bottomSectionY) {
        BitSet mask = data.method_38601();
        BitSet empty = data.method_38604();
        List updates = data.method_38606();
        int bits = 0;
        int next = 0;
        for (int i = 0; i < sectionCount + 2; ++i) {
            byte[] payload;
            boolean sent = mask.get(i);
            byte[] byArray = payload = sent && next < updates.size() ? (byte[])updates.get(next) : null;
            if (sent) {
                ++next;
            }
            if (i < 1 || i > sectionCount || i >= 32 || !ChunkLightSnapshot.candidate(i, bottomSectionY)) continue;
            if (sent) {
                LightSectionStatus status = LightDataClassifier.classifyPayload(payload);
                if (status != LightSectionStatus.NORMAL && status != LightSectionStatus.UNIFORM_MAX) continue;
                bits |= 1 << i;
                continue;
            }
            if (!empty.get(i)) continue;
            bits |= 1 << i;
        }
        return bits;
    }

    static void applyTrack(BitSet mask, BitSet emptyMask, List<byte[]> updates, LightSectionStatus[] out) {
        int next = 0;
        for (int i = 0; i < out.length; ++i) {
            if (mask.get(i)) {
                byte[] payload = next < updates.size() ? updates.get(next) : null;
                ++next;
                out[i] = LightDataClassifier.classifyPayload(payload);
                continue;
            }
            if (!emptyMask.get(i)) continue;
            out[i] = LightSectionStatus.EMPTY_MASK;
        }
    }
}

