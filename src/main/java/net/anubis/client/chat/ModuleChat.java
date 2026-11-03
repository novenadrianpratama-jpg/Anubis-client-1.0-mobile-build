/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_5250
 */
package net.anubis.client.chat;

import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.ThemeTint;
import net.anubis.client.module.ClientModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5250;

@Environment(value=EnvType.CLIENT)
public final class ModuleChat {
    private static final String HEADER = "Anubis Client";
    private static final int DARK_GREY = 0x555555;
    private static final int GREY = 0xAAAAAA;
    private static final int WHITE = 0xFFFFFF;
    private static final int DANGER = 0xFF5555;
    private static final int GOOD = 0x55FF55;
    private static final int WARN = 0xFFAA00;
    private static final int VALUE = 0x55FFFF;

    private ModuleChat() {
    }

    public static Body body() {
        return new Body();
    }

    public static void send(ClientModule module, class_2561 body) {
        ModuleChat.onClientThread(() -> {
            if (module.notificationsOn()) {
                ModuleChat.addLine(module, body);
            }
        });
    }

    public static void send(ClientModule module, Body body) {
        ModuleChat.send(module, body.build());
    }

    public static void send(ClientModule module, String text) {
        ModuleChat.send(module, (class_2561)class_2561.method_43470((String)text));
    }

    public static void actionBar(ClientModule module, Body body) {
        class_2561 built = body.build();
        ModuleChat.onClientThread(() -> {
            class_310 mc = class_310.method_1551();
            if (module.notificationsOn() && mc.field_1705 != null) {
                mc.field_1705.method_1758((class_2561)ModuleChat.line(module, built), false);
            }
        });
    }

    public static void sendAs(String name, Body body) {
        class_2561 built = body.build();
        ModuleChat.onClientThread(() -> {
            class_310 mc = class_310.method_1551();
            if (mc.field_1705 != null) {
                mc.field_1705.method_1743().method_1812((class_2561)ModuleChat.line(name, built));
            }
        });
    }

    public static void sendAlways(ClientModule module, Body body) {
        class_2561 built = body.build();
        ModuleChat.onClientThread(() -> ModuleChat.addLine(module, built));
    }

    public static void actionBarAlways(ClientModule module, Body body) {
        class_2561 built = body.build();
        ModuleChat.onClientThread(() -> {
            class_310 mc = class_310.method_1551();
            if (mc.field_1705 != null) {
                mc.field_1705.method_1758((class_2561)ModuleChat.line(module, built), false);
            }
        });
    }

    private static void addLine(ClientModule module, class_2561 body) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1705 != null) {
            mc.field_1705.method_1743().method_1812((class_2561)ModuleChat.line(module, body));
        }
    }

    private static class_5250 line(ClientModule module, class_2561 body) {
        return ModuleChat.line(module.name(), body);
    }

    private static class_5250 line(String name, class_2561 body) {
        return class_2561.method_43473().method_10852((class_2561)class_2561.method_43470((String)"[").method_54663(0x555555)).method_10852((class_2561)class_2561.method_43470((String)HEADER).method_54663(ThemeTint.accent(ThemeStore.get().accent.argb()) & 0xFFFFFF)).method_10852((class_2561)class_2561.method_43470((String)"] ").method_54663(0x555555)).method_10852((class_2561)class_2561.method_43470((String)name).method_54663(0xFFFFFF)).method_10852((class_2561)class_2561.method_43470((String)" : ").method_54663(0x555555)).method_10852((class_2561)class_2561.method_43473().method_54663(0xAAAAAA).method_10852(body));
    }

    private static void onClientThread(Runnable task) {
        class_310 mc = class_310.method_1551();
        if (mc.method_18854()) {
            task.run();
        } else {
            mc.execute(task);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Body {
        private final class_5250 root = class_2561.method_43473().method_54663(0xAAAAAA);

        private Body() {
        }

        public Body text(String text) {
            this.root.method_10852((class_2561)class_2561.method_43470((String)text));
            return this;
        }

        public Body name(String name) {
            return this.part(name, 0xFFFFFF);
        }

        public Body danger(String text) {
            return this.part(text, 0xFF5555);
        }

        public Body good(String text) {
            return this.part(text, 0x55FF55);
        }

        public Body warn(String text) {
            return this.part(text, 0xFFAA00);
        }

        public Body value(Object value) {
            return this.part(String.valueOf(value), 0x55FFFF);
        }

        public Body coords(int x, int y, int z) {
            return this.part(x + ", " + y + ", " + z, 0x55FFFF);
        }

        public Body append(class_2561 component) {
            this.root.method_10852(component);
            return this;
        }

        public class_2561 build() {
            return this.root.method_27661();
        }

        private Body part(String text, int rgb) {
            this.root.method_10852((class_2561)class_2561.method_43470((String)text).method_54663(rgb));
            return this;
        }
    }
}

