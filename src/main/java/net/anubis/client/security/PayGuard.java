/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_124
 *  net.minecraft.class_156
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_310
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 *  net.minecraft.class_7472
 *  net.minecraft.class_9449
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.security;

import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_7472;
import net.minecraft.class_9449;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class PayGuard {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final long ALLOW_MS = 5000L;
    private static final long SCREEN_GAP_MS = 30000L;
    private static final long CHAT_GAP_MS = 3000L;
    private static final int SHOWN_CHARS = 64;
    private static final class_2561 TITLE = class_2561.method_43470((String)"You are using a fake Anubis!").method_27695(new class_124[]{class_124.field_1061, class_124.field_1067});
    private static final ConcurrentLinkedQueue<Allowance> allowed = new ConcurrentLinkedQueue();
    private static volatile long lastScreenMs;
    private static volatile long lastChatMs;
    private static volatile String lastChatShown;

    private PayGuard() {
    }

    public static void allowTyped(String chatInput) {
        if (chatInput != null && chatInput.strip().startsWith("/")) {
            PayGuard.allow(chatInput);
        }
    }

    public static void allow(String command) {
        if (!PayGuard.isPay(command)) {
            return;
        }
        long now = System.currentTimeMillis();
        allowed.removeIf(entry -> now - entry.atMs() > 5000L);
        allowed.offer(new Allowance(PayGuard.normalize(command), now));
    }

    public static boolean blocks(class_2596<?> packet) {
        String command;
        if (packet instanceof class_7472) {
            class_7472 plain = (class_7472)packet;
            command = plain.comp_808();
        } else if (packet instanceof class_9449) {
            class_9449 signed = (class_9449)packet;
            command = signed.comp_2532();
        } else {
            return false;
        }
        if (!PayGuard.isPay(command) || PayGuard.consume(command)) {
            return false;
        }
        PayGuard.report(command);
        return true;
    }

    public static boolean isPay(String command) {
        String text = PayGuard.normalize(command);
        int space = text.indexOf(32);
        String name = space < 0 ? text : text.substring(0, space);
        name = name.substring(name.indexOf(58) + 1);
        return name.equals("pay") || name.equals("epay");
    }

    private static String normalize(String command) {
        String text;
        String string = text = command == null ? "" : command.strip();
        if (text.startsWith("/")) {
            text = text.substring(1).strip();
        }
        return text.replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private static boolean consume(String command) {
        String wanted = PayGuard.normalize(command);
        long now = System.currentTimeMillis();
        Iterator<Allowance> it = allowed.iterator();
        while (it.hasNext()) {
            Allowance entry = it.next();
            if (now - entry.atMs() > 5000L) {
                it.remove();
                continue;
            }
            if (!entry.command().equals(wanted)) continue;
            it.remove();
            return true;
        }
        return false;
    }

    private static void report(String command) {
        String text = command.strip();
        String shown = "/" + (String)(text.length() > 64 ? text.substring(0, 64) + "..." : text);
        LOGGER.warn("[Anubis] Pay Guard blocked \"{}\" sent by {}", (Object)shown, (Object)PayGuard.caller());
        class_310 mc = class_310.method_1551();
        mc.execute(() -> PayGuard.warn(mc, shown));
    }

    private static String caller() {
        return StackWalker.getInstance().walk(frames -> frames.map(StackWalker.StackFrame::getClassName).filter(name -> !name.startsWith("net.anubis.client.security.") && !name.startsWith("net.minecraft.") && !name.startsWith("com.mojang.") && !name.startsWith("io.netty.") && !name.startsWith("java.") && !name.startsWith("jdk.") && !name.startsWith("org.spongepowered.") && !name.startsWith("com.example.client.mixin.")).findFirst().orElse("unknown code"));
    }

    private static void warn(class_310 mc, String shown) {
        long now = System.currentTimeMillis();
        if (!(mc.field_1705 == null || now - lastChatMs < 3000L && shown.equals(lastChatShown))) {
            lastChatMs = now;
            lastChatShown = shown;
            mc.field_1705.method_1743().method_1812((class_2561)class_2561.method_43473().method_10852((class_2561)class_2561.method_43470((String)"[Anubis] ").method_27695(new class_124[]{class_124.field_1079, class_124.field_1067})).method_10852((class_2561)class_2561.method_43470((String)("Blocked " + shown + ". You are using a fake Anubis, this version is not trustworthy. The real one is at discord.gg/anubis. Don't tell whoever gave you this file that it was caught.")).method_27692(class_124.field_1061)));
        }
        if (mc.field_1755 instanceof WarningScreen || now - lastScreenMs < 30000L) {
            return;
        }
        lastScreenMs = now;
        mc.method_1507((class_437)new WarningScreen(mc.field_1755, shown));
    }

    static {
        lastChatShown = "";
    }

    @Environment(value=EnvType.CLIENT)
    private record Allowance(String command, long atMs) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class WarningScreen
    extends class_410 {
        WarningScreen(class_437 previous, String shown) {
            super(open -> {
                if (open) {
                    class_156.method_668().method_670("https://discord.gg/anubis");
                }
                class_310.method_1551().method_1507(previous);
            }, TITLE, (class_2561)class_2561.method_43470((String)("This copy of Anubis tried to send " + shown + " by itself. It was blocked.\n\nThis version is not trustworthy. Delete it and get the real Anubis from discord.gg/anubis.\n\nDon't tell the person or place you got this file from that it was caught, so they don't find out. Report it in discord.gg/anubis instead.")), (class_2561)class_2561.method_43470((String)"Open discord.gg/anubis"), (class_2561)class_2561.method_43470((String)"Close"));
        }
    }
}

