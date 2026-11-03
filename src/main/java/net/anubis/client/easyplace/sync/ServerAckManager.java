/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2680
 *  net.minecraft.class_638
 */
package net.anubis.client.easyplace.sync;

import net.anubis.client.easyplace.schematic.SchematicTarget;
import net.anubis.client.easyplace.sync.BlockAcks;
import net.anubis.client.easyplace.sync.PlacementConfirmation;
import net.anubis.client.easyplace.util.BlockStateUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2680;
import net.minecraft.class_638;

@Environment(value=EnvType.CLIENT)
public final class ServerAckManager {
    private PlacementConfirmation pending;

    public void begin(SchematicTarget target, long tick, int timeoutTicks, int retry, int sequence, class_2680 predicted) {
        this.pending = new PlacementConfirmation(target.pos(), target.expected(), tick, tick + (long)timeoutTicks, retry, sequence, predicted);
    }

    public Result poll(class_638 level, long tick) {
        if (this.pending == null) {
            return Result.IDLE;
        }
        if (BlockAcks.answered(level, this.pending.sequence())) {
            class_2680 wanted = this.pending.predicted() == null || this.pending.predicted().method_26215() ? this.pending.expected().state() : this.pending.predicted();
            return BlockStateUtil.statesMatch(wanted, level.method_8320(this.pending.pos())) ? Result.CONFIRMED : Result.REFUSED;
        }
        return tick >= this.pending.deadlineTick() ? Result.TIMEOUT : Result.WAITING;
    }

    public PlacementConfirmation getPending() {
        return this.pending;
    }

    public void clear() {
        this.pending = null;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Result {
        IDLE,
        WAITING,
        CONFIRMED,
        REFUSED,
        TIMEOUT;

    }
}

