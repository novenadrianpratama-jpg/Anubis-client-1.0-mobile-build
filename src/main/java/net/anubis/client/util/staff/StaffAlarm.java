/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1113$class_1114
 *  net.minecraft.class_1144
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 */
package net.anubis.client.util.staff;

import java.util.ArrayDeque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

@Environment(value=EnvType.CLIENT)
public final class StaffAlarm {
    public static final String BEEP = "Beep";
    public static final String BELL = "Bell";
    public static final String GUARDIAN = "Guardian";
    public static final String OFF = "Off";
    private static final int SIREN_INTERVAL = 4;
    private static final float SIREN_HIGH = 1.5f;
    private static final float SIREN_LOW = 1.0f;
    private static final int BEEP_INTERVAL = 3;
    private static final float BEEP_PITCH = 1.6f;
    private static final int BELL_INTERVAL = 10;
    private static final int GUARDIAN_INTERVAL = 60;
    private static final int MAX_RINGING = 8;
    private String mode = "Off";
    private float volume;
    private int ticksLeft;
    private int step;
    private final ArrayDeque<class_1113> ringing = new ArrayDeque(8);

    public void start(String mode, float seconds, float volume) {
        if (mode == null || OFF.equals(mode) || volume <= 0.0f || seconds <= 0.0f) {
            return;
        }
        int ticks = Math.max(1, Math.round(seconds * 20.0f));
        this.volume = Math.min(1.0f, volume);
        if (this.isPlaying() && mode.equals(this.mode)) {
            this.ticksLeft = Math.max(this.ticksLeft, ticks);
            return;
        }
        this.stop();
        this.mode = mode;
        this.ticksLeft = ticks;
        this.step = 0;
    }

    public void tick() {
        if (this.ticksLeft <= 0) {
            return;
        }
        int interval = StaffAlarm.interval(this.mode);
        if (this.step % interval == 0) {
            this.play(this.step / interval);
        }
        ++this.step;
        if (--this.ticksLeft == 0) {
            this.step = 0;
        }
    }

    public void stop() {
        this.ticksLeft = 0;
        this.step = 0;
        if (this.ringing.isEmpty()) {
            return;
        }
        class_1144 sounds = class_310.method_1551().method_1483();
        if (sounds != null) {
            this.ringing.forEach(arg_0 -> ((class_1144)sounds).method_4870(arg_0));
        }
        this.ringing.clear();
    }

    public boolean isPlaying() {
        return this.ticksLeft > 0;
    }

    private void play(int note) {
        class_3414 sound;
        class_1144 sounds = class_310.method_1551().method_1483();
        if (sounds == null) {
            return;
        }
        class_1109 instance = new class_1109(sound.comp_3319(), class_3419.field_15250, this.volume, switch (this.mode) {
            case BEEP -> {
                sound = (class_3414)class_3417.field_18311.comp_349();
                yield 1.6f;
            }
            case BELL -> {
                sound = class_3417.field_17265;
                yield 1.0f;
            }
            case GUARDIAN -> {
                sound = class_3417.field_15203;
                yield 1.0f;
            }
            default -> {
                sound = (class_3414)class_3417.field_14622.comp_349();
                yield note % 2 == 0 ? 1.5f : 1.0f;
            }
        }, class_1113.method_43221(), false, 0, class_1113.class_1114.field_5478, 0.0, 0.0, 0.0, true);
        if (this.ringing.size() >= 8) {
            this.ringing.removeFirst();
        }
        this.ringing.addLast((class_1113)instance);
        sounds.method_4873((class_1113)instance);
    }

    private static int interval(String mode) {
        return switch (mode) {
            case BEEP -> 3;
            case BELL -> 10;
            case GUARDIAN -> 60;
            default -> 4;
        };
    }
}

