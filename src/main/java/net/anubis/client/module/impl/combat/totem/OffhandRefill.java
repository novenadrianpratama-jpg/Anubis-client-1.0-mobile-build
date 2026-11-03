/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.combat.totem;

import java.util.random.RandomGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class OffhandRefill {
    private static final int MAX_ATTEMPTS = 3;
    private static final long MS = 1000000L;
    private final Driver driver;
    private final RandomGenerator random;
    private Phase phase = Phase.IDLE;
    private long due;
    private int source = -1;
    private double tempo = 1.0;
    private boolean occupiedSinceSwap;
    private Status status = Status.IDLE;
    private int attempts;
    private int sentContainer = -1;
    private int seenState = -1;
    private long startedAt;
    private long deadline;
    private long retryAt;
    private long retryPause;

    public OffhandRefill(Driver driver, RandomGenerator random) {
        this.driver = driver;
        this.random = random;
    }

    public void reset() {
        this.cancelSchedule();
        this.resetConfirmation();
        this.occupiedSinceSwap = false;
    }

    public boolean busy() {
        return this.phase != Phase.IDLE || this.status == Status.WAITING;
    }

    public void step(long now, int setting) {
        if (!this.driver.canInteract()) {
            this.reset();
            return;
        }
        this.expire(now);
        if (!this.driver.needsTotem()) {
            this.occupiedSinceSwap = true;
            this.cancelSchedule();
            return;
        }
        if (this.occupiedSinceSwap) {
            if (this.status != Status.EMPTY && this.status != Status.GAVE_UP) {
                this.resetConfirmation();
            }
            this.occupiedSinceSwap = false;
        }
        if (!this.canAttempt(now)) {
            this.cancelSchedule();
            return;
        }
        int speed = Math.max(1, Math.min(10, setting));
        if (speed == 1) {
            int instant = this.driver.findSource();
            if (instant >= 0 && this.driver.validSource(instant)) {
                this.equip(now, instant);
            }
            return;
        }
        if (now < this.due) {
            return;
        }
        switch (this.phase.ordinal()) {
            case 0: {
                if (this.driver.findSource() < 0) {
                    return;
                }
                this.tempo = 0.94 + this.random.nextDouble() * 0.12;
                this.phase = Phase.NOTICE;
                this.due = now + this.sample(10 + (speed - 1) * 8, 20 + (speed - 1) * 13);
                break;
            }
            case 1: {
                this.source = this.driver.findSource();
                if (this.source < 0) {
                    this.cancelSchedule();
                    return;
                }
                this.phase = Phase.TARGET;
                this.due = now + this.sample(6.0 + (double)(speed - 1) * 2.5, 12.0 + (double)(speed - 1) * 4.5);
                break;
            }
            case 2: {
                if (!this.driver.validSource(this.source)) {
                    this.phase = Phase.NOTICE;
                    this.due = now + this.sample(6.0, 14.0);
                    return;
                }
                this.equip(now, this.source);
            }
        }
    }

    public void observe(int containerId, int stateId, Offhand offhand, long receivedAt) {
        if (this.attempts == 0 || receivedAt < this.startedAt) {
            return;
        }
        if (containerId != -1 && containerId != this.sentContainer) {
            return;
        }
        if (stateId != -1 && stateId < this.seenState) {
            return;
        }
        if (this.status != Status.WAITING && this.status != Status.UNCONFIRMED && this.status != Status.EMPTY && this.status != Status.GAVE_UP) {
            return;
        }
        if (stateId != -1) {
            this.seenState = stateId;
        }
        this.status = switch (offhand.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if (this.attempts >= 3) {
                    yield Status.GAVE_UP;
                }
                yield Status.EMPTY;
            }
            case 1 -> Status.CONFIRMED;
            case 2 -> Status.OCCUPIED;
        };
        this.retryAt = receivedAt + this.retryPause;
    }

    private void equip(long now, int index) {
        if (!(this.driver.needsTotem() && this.driver.canInteract() && this.driver.validSource(index))) {
            this.cancelSchedule();
            return;
        }
        this.begin(now, this.driver.containerId(), this.driver.stateId(), this.driver.latencyMillis());
        this.cancelSchedule();
        this.driver.swap(index);
        this.occupiedSinceSwap = !this.driver.needsTotem();
    }

    private long sample(double low, double high) {
        return Math.round((low + (high - low) * (this.random.nextDouble() + this.random.nextDouble()) / 2.0) * this.tempo * 1000000.0);
    }

    private void cancelSchedule() {
        this.phase = Phase.IDLE;
        this.source = -1;
        this.due = 0L;
    }

    private void resetConfirmation() {
        this.status = Status.IDLE;
        this.attempts = 0;
        this.sentContainer = -1;
        this.seenState = -1;
        this.retryPause = 0L;
        this.retryAt = 0L;
        this.deadline = 0L;
        this.startedAt = 0L;
    }

    private void begin(long now, int containerId, int stateId, int latencyMillis) {
        ++this.attempts;
        this.sentContainer = containerId;
        this.seenState = stateId;
        this.startedAt = now;
        long latency = Math.max(0, latencyMillis);
        this.deadline = now + Math.min(5000L, Math.max(750L, latency * 3L + 250L)) * 1000000L;
        this.retryPause = Math.min(500L, Math.max(100L, latency)) * 1000000L;
        this.status = Status.WAITING;
    }

    private void expire(long now) {
        if (this.status == Status.WAITING && now >= this.deadline) {
            this.status = Status.UNCONFIRMED;
            this.retryAt = now + this.retryPause;
        }
    }

    private boolean canAttempt(long now) {
        this.expire(now);
        if (this.status == Status.IDLE) {
            return true;
        }
        if (this.status != Status.EMPTY && this.status != Status.UNCONFIRMED) {
            return false;
        }
        if (this.attempts >= 3) {
            this.status = Status.GAVE_UP;
            return false;
        }
        return now >= this.retryAt;
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Phase {
        IDLE,
        NOTICE,
        TARGET;

    }

    @Environment(value=EnvType.CLIENT)
    private static enum Status {
        IDLE,
        WAITING,
        CONFIRMED,
        EMPTY,
        OCCUPIED,
        UNCONFIRMED,
        GAVE_UP;

    }

    @Environment(value=EnvType.CLIENT)
    public static interface Driver {
        public boolean needsTotem();

        public boolean canInteract();

        public int findSource();

        public boolean validSource(int var1);

        public void swap(int var1);

        public int containerId();

        public int stateId();

        public int latencyMillis();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Offhand {
        EMPTY,
        TOTEM,
        OTHER;

    }
}

