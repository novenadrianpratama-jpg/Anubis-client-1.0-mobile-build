/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_4184
 *  net.minecraft.class_5321
 *  net.minecraft.class_746
 *  org.joml.Matrix3x2fStack
 */
package net.anubis.client.module.impl.render;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import net.anubis.client.chat.ModuleChat;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.EventPriority;
import net.anubis.client.event.Stage;
import net.anubis.client.event.impl.KeyInputEvent;
import net.anubis.client.event.impl.MouseInputEvent;
import net.anubis.client.event.impl.Render2DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.WorldLabel;
import net.anubis.client.input.ModuleKeybinds;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.setting.ActionSetting;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.ColorSetting;
import net.anubis.client.module.setting.KeySetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.module.setting.StringListSetting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_5321;
import net.minecraft.class_746;
import org.joml.Matrix3x2fStack;

@Environment(value=EnvType.CLIENT)
public final class WaypointsModule
extends ClientModule {
    private static final Pattern SPLIT = Pattern.compile("[\\s,]+");
    private static final Pattern DEATH_NAME = Pattern.compile("Death \\d{2}:\\d{2}");
    private static final DateTimeFormatter DEATH_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final double PROJECT_DISTANCE = 64.0;
    private static final double NETHER_SCALE = 8.0;
    private static final float FOCUS_RADIUS = 40.0f;
    private static final float FADE_BLOCKS = 4.0f;
    private static final int PADDING = 3;
    private static final int MARKER = 5;
    private static final int PANEL_RGB = 0x101014;
    private static final int TEXT_LABEL = -4934466;
    private final StringListSetting points = this.addSetting("points", new StringListSetting("Waypoints", new String[0]));
    private final KeySetting addKey = this.addSetting("add-key", new KeySetting("Add Here Key", -1));
    private final ActionSetting addHere = this.addSetting("add-here", new ActionSetting("Add Here", this::addHere));
    private final BooleanSetting deaths = this.addSetting("deaths", new BooleanSetting("Death Waypoints", true));
    private final NumberSetting deathMemory = this.addSetting("death-memory", new NumberSetting("Death Memory", 3.0f, 1.0f, 10.0f).step(1.0f));
    private final BooleanSetting otherDimension = this.addSetting("other-dimension", new BooleanSetting("Show In Other Dimension", true));
    private final BooleanSetting showDistance = this.addSetting("show-distance", new BooleanSetting("Show Distance", true));
    private final NumberSetting maxDistance = this.addSetting("max-distance", new NumberSetting("Max Distance", 0.0f, 0.0f, 30000.0f).step(100.0f));
    private final NumberSetting hideWithin = this.addSetting("hide-within", new NumberSetting("Hide Within", 3.0f, 0.0f, 32.0f).step(1.0f));
    private final NumberSetting scale = this.addSetting("scale", new NumberSetting("Scale", 1.0f, 0.5f, 2.0f).step(0.1f));
    private final ColorSetting color = this.addSetting("color", new ColorSetting("Color", -14006));
    private final ColorSetting deathColor = this.addSetting("death-color", new ColorSetting("Death Color", -47814));
    private List<Point> parsed;
    private boolean wasDead;

    public WaypointsModule() {
        super("waypoints", "Waypoints", "Saved places shown on screen with distance. Entries are \"Name x y z\", optionally ending in nether or end.", ModuleCategory.RENDER);
        this.points.onChange(() -> {
            this.parsed = null;
        });
        this.deathMemory.visibleWhen(this.deaths::get);
        this.deathColor.visibleWhen(this.deaths::get);
    }

    @Override
    protected void onEnable() {
        this.wasDead = false;
        this.on(KeyInputEvent.TYPE, Stage.PRE, EventPriority.NORMAL, event -> {
            if (event.action == 1) {
                this.pressed(event.key);
            }
        });
        this.on(MouseInputEvent.TYPE, Stage.PRE, EventPriority.NORMAL, event -> {
            if (event.action == 1) {
                this.pressed(1000 + event.button);
            }
        });
    }

    private void pressed(int key) {
        if (!ModuleKeybinds.isBindable(key) || key != this.addKey.value() || class_310.method_1551().field_1755 != null) {
            return;
        }
        this.addHere();
    }

    private void addHere() {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            this.say(ModuleChat.body().warn("Join a world first."));
            return;
        }
        String name = this.freeName();
        this.save(name, player.method_31477(), player.method_31478(), player.method_31479(), (class_5321<class_1937>)mc.field_1687.method_27983());
    }

    private void save(String name, int x, int y, int z, class_5321<class_1937> dimension) {
        String entry = name + " " + x + " " + y + " " + z + WaypointsModule.suffix(dimension);
        if (!this.points.addValue(entry)) {
            this.say(ModuleChat.body().warn("The waypoint list is full").text(" (64), remove one first."));
            return;
        }
        this.say(ModuleChat.body().good("Saved ").name(name).text(" at ").coords(x, y, z).text("."));
    }

    private String freeName() {
        List<Point> all = this.parsed();
        int n = all.size() + 1;
        while (true) {
            String name = "Waypoint " + n;
            if (all.stream().noneMatch(point -> point.name().equalsIgnoreCase(name))) {
                return name;
            }
            ++n;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            this.wasDead = false;
            return;
        }
        boolean dead = player.method_29504();
        if (dead && !this.wasDead && this.deaths.get()) {
            this.forgetOldDeaths(Math.max(0, this.deathMemory.getInt() - 1));
            this.save("Death " + LocalTime.now().format(DEATH_TIME), player.method_31477(), player.method_31478(), player.method_31479(), (class_5321<class_1937>)mc.field_1687.method_27983());
        }
        this.wasDead = dead;
    }

    private void forgetOldDeaths(int keep) {
        int i;
        ArrayList<Integer> deathRows = new ArrayList<Integer>();
        for (i = 0; i < this.points.size(); ++i) {
            Point point = WaypointsModule.parse(this.points.entry(i).get());
            if (point == null || !point.death()) continue;
            deathRows.add(i);
        }
        for (i = deathRows.size() - keep - 1; i >= 0; --i) {
            this.points.remove((Integer)deathRows.get(i));
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null || mc.field_1690.field_1842) {
            return;
        }
        class_4184 camera = mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332()) {
            return;
        }
        class_243 eye = camera.method_71156();
        class_5321 here = mc.field_1687.method_27983();
        double limit = this.maxDistance.get();
        ArrayList<Shown> visible = new ArrayList<Shown>();
        for (Point point : this.parsed()) {
            Shown shown = this.place(point, (class_5321<class_1937>)here, eye);
            if (shown == null || limit > 0.0 && shown.distance() > limit) continue;
            visible.add(shown);
        }
        visible.sort(Comparator.comparingDouble(Shown::distance).reversed());
        class_332 graphics = event.graphics;
        for (Shown shown : visible) {
            this.draw(graphics, mc, eye, shown);
        }
    }

    private Shown place(Point point, class_5321<class_1937> here, class_243 eye) {
        double x = (double)point.x() + 0.5;
        double z = (double)point.z() + 0.5;
        boolean converted = false;
        if (point.dimension() != here) {
            if (!this.otherDimension.get()) {
                return null;
            }
            if (point.dimension() == class_1937.field_25179 && here == class_1937.field_25180) {
                x /= 8.0;
                z /= 8.0;
            } else if (point.dimension() == class_1937.field_25180 && here == class_1937.field_25179) {
                x *= 8.0;
                z *= 8.0;
            } else {
                return null;
            }
            converted = true;
        }
        double y = point.y() == null || converted ? eye.field_1351 : (double)point.y().intValue() + 0.5;
        class_243 at = new class_243(x, y, z);
        return new Shown(point, at, at.method_1022(eye), converted);
    }

    private void draw(class_332 graphics, class_310 mc, class_243 eye, Shown shown) {
        int n;
        float dy;
        float dx;
        Object detail;
        float alpha = Math.min(1.0f, Math.max(0.0f, (float)(shown.distance() - (double)this.hideWithin.get()) / 4.0f));
        if (alpha <= 0.02f) {
            return;
        }
        class_243 toward = shown.at().method_1020(eye);
        class_243 aim = shown.distance() > 64.0 ? eye.method_1019(toward.method_1021(64.0 / shown.distance())) : shown.at();
        float[] screen = WorldLabel.project(mc, aim, graphics.method_51421(), graphics.method_51443());
        if (screen == null) {
            return;
        }
        Point point = shown.point();
        class_327 font = mc.field_1772;
        int accent = point.death() ? this.deathColor.argb() : this.color.argb();
        String title = point.name();
        Object object = detail = this.showDistance.get() ? WaypointsModule.distance(shown.distance()) : null;
        if (shown.converted()) {
            detail = (String)(detail == null ? "" : (String)detail + " ") + "(" + WaypointsModule.dimensionName(point.dimension()) + ")";
        }
        String coords = (dx = screen[0] - (float)graphics.method_51421() * 0.5f) * dx + (dy = screen[1] - (float)graphics.method_51443() * 0.5f) * dy <= 1600.0f ? point.x() + ", " + String.valueOf(point.y() == null ? "~" : point.y()) + ", " + point.z() : null;
        int titleW = font.method_1727(title) + (detail != null ? 4 + font.method_1727((String)detail) : 0);
        int width = Math.max(titleW, coords != null ? font.method_1727(coords) : 0) + 6;
        Objects.requireNonNull(font);
        if (coords != null) {
            Objects.requireNonNull(font);
            n = 9 + 1;
        } else {
            n = 0;
        }
        int height = 6 + 9 + n;
        Matrix3x2fStack pose = graphics.method_51448();
        pose.pushMatrix();
        pose.translate(screen[0], screen[1]);
        float size = this.scale.get();
        pose.scale(size, size);
        int markerColor = WaypointsModule.fade(accent, alpha);
        graphics.method_25294(-2, -2, 3, 3, WaypointsModule.fade(-16777216, alpha * 0.6f));
        graphics.method_25294(-1, -1, 2, 2, markerColor);
        int left = -width / 2;
        int top = -7 - height;
        graphics.method_25294(left, top, left + width, top + height, WaypointsModule.fade(-15724524, alpha * 0.55f));
        graphics.method_25294(left, top + height - 1, left + width, top + height, markerColor);
        int textX = left + 3;
        int textY = top + 3;
        graphics.method_51433(font, title, textX, textY, WaypointsModule.fade(accent, alpha), true);
        if (detail != null) {
            graphics.method_51433(font, (String)detail, textX + font.method_1727(title) + 4, textY, WaypointsModule.fade(-4934466, alpha), true);
        }
        if (coords != null) {
            Objects.requireNonNull(font);
            graphics.method_51433(font, coords, textX, textY + 9 + 1, WaypointsModule.fade(-1, alpha), true);
        }
        pose.popMatrix();
    }

    private List<Point> parsed() {
        if (this.parsed == null) {
            ArrayList<Point> out = new ArrayList<Point>();
            for (String entry : this.points.get()) {
                Point point = WaypointsModule.parse(entry);
                if (point == null) continue;
                out.add(point);
            }
            this.parsed = List.copyOf(out);
        }
        return this.parsed;
    }

    private static Point parse(String entry) {
        Integer value;
        class_5321<class_1937> named;
        ArrayList<String> tokens = new ArrayList<String>(List.of(SPLIT.split(entry.trim())));
        tokens.removeIf(String::isEmpty);
        class_5321<class_1937> dimension = class_1937.field_25179;
        if (!tokens.isEmpty() && (named = WaypointsModule.dimension((String)tokens.get(tokens.size() - 1))) != null) {
            dimension = named;
            tokens.remove(tokens.size() - 1);
        }
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        while (!tokens.isEmpty() && numbers.size() < 3 && (value = WaypointsModule.number((String)tokens.get(tokens.size() - 1))) != null) {
            numbers.add(0, value);
            tokens.remove(tokens.size() - 1);
        }
        if (numbers.size() < 2) {
            return null;
        }
        String name = String.join((CharSequence)" ", tokens).trim();
        if (name.isEmpty()) {
            name = "Waypoint";
        }
        boolean death = DEATH_NAME.matcher(name).matches();
        return numbers.size() == 3 ? new Point(name, (Integer)numbers.get(0), (Integer)numbers.get(1), (Integer)numbers.get(2), dimension, death) : new Point(name, (Integer)numbers.get(0), null, (Integer)numbers.get(1), dimension, death);
    }

    private static Integer number(String token) {
        try {
            return (int)Math.floor(Double.parseDouble(token));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static class_5321<class_1937> dimension(String token) {
        return switch (token.toLowerCase(Locale.ROOT)) {
            case "nether", "the_nether", "minecraft:the_nether" -> class_1937.field_25180;
            case "end", "the_end", "minecraft:the_end" -> class_1937.field_25181;
            case "overworld", "minecraft:overworld" -> class_1937.field_25179;
            default -> null;
        };
    }

    private static String suffix(class_5321<class_1937> dimension) {
        return dimension == class_1937.field_25180 ? " nether" : (dimension == class_1937.field_25181 ? " end" : "");
    }

    private static String dimensionName(class_5321<class_1937> dimension) {
        return dimension == class_1937.field_25180 ? "Nether" : (dimension == class_1937.field_25181 ? "End" : "Overworld");
    }

    private static String distance(double blocks) {
        if (blocks >= 10000.0) {
            return String.format(Locale.ROOT, "%.1fk m", blocks / 1000.0);
        }
        return Math.round(blocks) + "m";
    }

    private static int fade(int argb, float alpha) {
        int a = Math.round((float)(argb >>> 24 & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return a << 24 | argb & 0xFFFFFF;
    }

    private void say(ModuleChat.Body body) {
        ModuleChat.send((ClientModule)this, body);
    }

    @Environment(value=EnvType.CLIENT)
    private record Point(String name, int x, Integer y, int z, class_5321<class_1937> dimension, boolean death) {
    }

    @Environment(value=EnvType.CLIENT)
    private record Shown(Point point, class_243 at, double distance, boolean converted) {
    }
}

