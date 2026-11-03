/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1542
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_640
 *  net.minecraft.class_742
 *  org.joml.Matrix3x2fStack
 */
package net.anubis.client.module.impl.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.anubis.client.event.EventHandler;
import net.anubis.client.event.impl.Render2DEvent;
import net.anubis.client.event.impl.TickEvent;
import net.anubis.client.gui.WorldLabel;
import net.anubis.client.module.ClientModule;
import net.anubis.client.module.ModuleCategory;
import net.anubis.client.module.ModuleRegistry;
import net.anubis.client.module.setting.BooleanSetting;
import net.anubis.client.module.setting.NumberSetting;
import net.anubis.client.util.NameFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_640;
import net.minecraft.class_742;
import org.joml.Matrix3x2fStack;

@Environment(value=EnvType.CLIENT)
public class NametagsModule
extends ClientModule {
    private static final int PANEL_RGB = 1447965;
    private static final int BORDER = 0x17FFFFFF;
    private static final int SHADOW_NEAR = 0x2E000000;
    private static final int SHADOW_FAR = 0x17000000;
    private static final int DIVIDER = 0x1FFFFFFF;
    private static final int EMPTY_SLOT = 0x1AFFFFFF;
    private static final int TEXT_NAME = -1447442;
    private static final int TEXT_LABEL = -6052949;
    private static final int TEXT_VALUE = -657929;
    private static final int STATUS_GOOD = -13577896;
    private static final int STATUS_WARN = -10742;
    private static final int STATUS_BAD = -47814;
    private static final int ITEM_COUNT_COLOR = -10742;
    private static final float TEXT_GUARD_BELOW = 0.55f;
    private static final class_1304[] ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
    private static final int PADDING = 4;
    private static final int ROW_GAP = 3;
    private static final int ICON_SIZE = 10;
    private static final int ICON_GAP = 2;
    private static final int TEXT_GAP = 4;
    private static final int SECTION_GAP = 3;
    private static final float ICON_SCALE = 0.625f;
    private static final int TAG_CAP = 64;
    private static final double FULL_SIZE_DISTANCE = 8.0;
    private static final int SEEN_LIMIT = 9;
    private static final String SEEN_LABEL = "Seen";
    private static final int SEEN_PLAYERS = 128;
    private static final Comparator<Nearby> NEAREST = Comparator.comparingDouble(Nearby::distanceSq);
    private final BooleanSetting sPing = this.addSetting("ping", new BooleanSetting("Ping", true));
    private final BooleanSetting sHeldItems = this.addSetting("held-items", new BooleanSetting("Held Items", true));
    private final BooleanSetting sSeenItems = this.addSetting("seen-items", new BooleanSetting("Seen Items", true));
    private final BooleanSetting sArmor = this.addSetting("armor", new BooleanSetting("Armor", true));
    private final BooleanSetting sDurability = this.addSetting("durability", new BooleanSetting("Durability", true));
    private final BooleanSetting sHealth = this.addSetting("health", new BooleanSetting("Health", true));
    private final BooleanSetting sHideVanilla = this.addSetting("hide-vanilla", new BooleanSetting("Hide Vanilla", true));
    private final BooleanSetting sItems = this.addSetting("items", new BooleanSetting("Dropped Items", true));
    private final NumberSetting sRange = this.addSetting("range", new NumberSetting("Range", 128.0f, 8.0f, 512.0f).step(1.0f));
    private final NumberSetting sMaxTags = this.addSetting("max-tags", new NumberSetting("Max Tags", 64.0f, 1.0f, 64.0f).step(1.0f));
    private final NumberSetting sScale = this.addSetting("scale", new NumberSetting("Scale", 1.0f, 0.5f, 2.0f).step(0.1f));
    private final NumberSetting sMinScale = this.addSetting("min-scale", new NumberSetting("Min Scale", 0.55f, 0.3f, 1.0f).step(0.05f));
    private final NumberSetting sBackgroundAlpha = this.addSetting("background-alpha", new NumberSetting("Background Alpha", 0.45f, 0.0f, 1.0f).step(0.05f));
    private final Map<UUID, LinkedHashMap<class_1792, class_1799>> seen = new LinkedHashMap<UUID, LinkedHashMap<class_1792, class_1799>>(16, 0.75f, true);
    private class_638 seenLevel;

    public NametagsModule() {
        super("nametags", "Nametags", "Player nametags with held items, armour, health, ping and distance.", ModuleCategory.RENDER);
        this.sDurability.visibleWhen(() -> this.sHeldItems.get() || this.sArmor.get());
        this.sSeenItems.visibleWhen(this.sHeldItems::get);
    }

    public static boolean hideVanillaNametags() {
        NametagsModule module = ModuleRegistry.get(NametagsModule.class);
        return module != null && module.isEnabled() && module.sHideVanilla.get();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 != this.seenLevel) {
            this.seen.clear();
            this.seenLevel = mc.field_1687;
        }
        if (mc.field_1687 == null || !this.sHeldItems.get() || !this.sSeenItems.get()) {
            return;
        }
        for (class_742 player : mc.field_1687.method_18456()) {
            if (player == mc.field_1724) continue;
            this.remember(player.method_5667(), player.method_6047());
            this.remember(player.method_5667(), player.method_6079());
        }
    }

    private void remember(UUID id, class_1799 stack) {
        if (stack.method_7960()) {
            return;
        }
        LinkedHashMap items = this.seen.computeIfAbsent(id, key -> new LinkedHashMap());
        class_1799 known = (class_1799)items.remove(stack.method_7909());
        items.put(stack.method_7909(), known != null && class_1799.method_31577((class_1799)known, (class_1799)stack) ? known : stack.method_7972());
        while (items.size() > 9) {
            items.remove(items.keySet().iterator().next());
        }
        while (this.seen.size() > 128) {
            this.seen.remove(this.seen.keySet().iterator().next());
        }
    }

    private List<class_1799> seenItems(class_742 player) {
        LinkedHashMap<class_1792, class_1799> items = this.seen.get(player.method_5667());
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        class_1792 main = player.method_6047().method_7909();
        class_1792 off = player.method_6079().method_7909();
        ArrayList<class_1799> out = new ArrayList<class_1799>(items.size());
        for (class_1799 stack : items.values()) {
            if (stack.method_7909() == main || stack.method_7909() == off) continue;
            out.add(0, stack);
        }
        return out;
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        int i;
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null || mc.field_1690.field_1842) {
            return;
        }
        class_4184 camera = mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332()) {
            return;
        }
        float partialTick = event.partialTick;
        class_243 eye = mc.field_1724.method_5836(partialTick);
        double range = Math.max(1.0f, this.sRange.get());
        double rangeSq = range * range;
        int budget = Math.max(1, Math.min(64, this.sMaxTags.getInt()));
        List<Nearby> players = new ArrayList<Nearby>();
        for (class_742 player : mc.field_1687.method_18456()) {
            double distanceSq;
            if (player == mc.field_1724 || player.method_31481() || player.method_29504() || !((distanceSq = player.method_30950(partialTick).method_1025(eye)) <= rangeSq)) continue;
            players.add(new Nearby((class_1297)player, distanceSq));
        }
        players = NametagsModule.nearest(players, budget);
        int itemBudget = budget - players.size();
        List<Nearby> items = new ArrayList<Nearby>();
        if (this.sItems.get() && itemBudget > 0) {
            for (class_1297 entity : mc.field_1687.method_18112()) {
                double distanceSq;
                class_1542 item;
                if (!(entity instanceof class_1542) || (item = (class_1542)entity).method_31481() || item.method_6983().method_7960() || !((distanceSq = item.method_30950(partialTick).method_1025(eye)) <= rangeSq)) continue;
                items.add(new Nearby((class_1297)item, distanceSq));
            }
            items = NametagsModule.nearest(items, itemBudget);
        }
        for (i = items.size() - 1; i >= 0; --i) {
            this.renderItemTag(event.graphics, mc, camera, (class_1542)((Nearby)items.get(i)).entity(), partialTick);
        }
        for (i = players.size() - 1; i >= 0; --i) {
            this.renderTag(event.graphics, mc, camera, (class_742)players.get(i).entity(), partialTick);
        }
    }

    private static List<Nearby> nearest(List<Nearby> candidates, int limit) {
        candidates.sort(NEAREST);
        return candidates.size() <= limit ? candidates : candidates.subList(0, limit);
    }

    private void renderTag(class_332 graphics, class_310 mc, class_4184 camera, class_742 player, float partialTick) {
        class_640 info;
        class_243 anchor = player.method_30950(partialTick).method_1031(0.0, (double)player.method_17682() + 0.15, 0.0);
        float[] screen = WorldLabel.project(mc, anchor, graphics.method_51421(), graphics.method_51443());
        if (screen == null) {
            return;
        }
        class_327 font = mc.field_1772;
        String name = NameFilter.apply(player.method_7334().name());
        String pingText = null;
        int pingColor = -6052949;
        if (this.sPing.get() && mc.method_1562() != null && (info = mc.method_1562().method_2871(player.method_5667())) != null) {
            int latency = info.method_2959();
            pingText = latency + "ms";
            pingColor = latency < 60 ? -13577896 : (latency < 130 ? -10742 : -47814);
        }
        List<Object> hands = this.sHeldItems.get() ? NametagsModule.heldItems(player) : List.of();
        List<Object> armor = this.sArmor.get() ? NametagsModule.wornArmor(player) : List.of();
        List<class_1799> earlier = this.sHeldItems.get() && this.sSeenItems.get() ? this.seenItems(player) : List.of();
        String healthText = null;
        int healthColor = -13577896;
        float health = player.method_6032() + player.method_6067();
        if (this.sHealth.get() && Float.isFinite(health)) {
            float fraction = Math.min(1.0f, health / Math.max(1.0f, player.method_6063()));
            healthText = String.valueOf((int)Math.ceil(health));
            healthColor = fraction > 0.6f ? -13577896 : (fraction > 0.3f ? -10742 : -47814);
        }
        double dist = anchor.method_1022(camera.method_71156());
        String distText = Math.round((float)dist) + "m";
        int handsW = NametagsModule.iconsWidth(hands.size());
        int armorW = NametagsModule.iconsWidth(armor.size());
        int healthW = healthText != null ? font.method_1727(healthText) : 0;
        int distW = font.method_1727(distText);
        int row1W = font.method_1727(name) + (pingText != null ? 5 + font.method_1727(pingText) : 0);
        int row2W = NametagsModule.extend(0, handsW, 0);
        row2W = NametagsModule.extend(row2W, armorW, 7);
        row2W = NametagsModule.extend(row2W, healthW, 4);
        row2W = NametagsModule.extend(row2W, distW, 4);
        int row3W = earlier.isEmpty() ? 0 : font.method_1727(SEEN_LABEL) + 4 + NametagsModule.iconsWidth(earlier.size());
        int contentW = Math.max(Math.max(row1W, row2W), row3W);
        int panelW = contentW + 8;
        Objects.requireNonNull(font);
        int panelH = 8 + 9 + 3 + 10 + (earlier.isEmpty() ? 0 : 13);
        float baseScale = Math.max(0.5f, Math.min(2.0f, this.sScale.get()));
        double minScale = Math.max(0.3f, Math.min(1.0f, this.sMinScale.get()));
        float distScale = (float)Math.min(1.0, Math.max(minScale, 8.0 / Math.max(1.0, dist)));
        float scale = baseScale * distScale;
        float surface = Math.max(0.0f, Math.min(1.0f, this.sBackgroundAlpha.get()));
        boolean guard = surface < 0.55f;
        float chrome = Math.min(1.0f, surface * 2.0f);
        Matrix3x2fStack pose = graphics.method_51448();
        pose.pushMatrix();
        pose.translate(screen[0], screen[1]);
        pose.scale(scale, scale);
        int x = -panelW / 2;
        int y = -panelH - 1;
        NametagsModule.drawPanel(graphics, x, y, x + panelW, y + panelH, surface, chrome);
        int left = x + 4;
        int textY = y + 4;
        graphics.method_51433(font, name, left, textY, -1447442, guard);
        if (pingText != null) {
            graphics.method_51433(font, pingText, left + font.method_1727(name) + 5, textY, pingColor, guard);
        }
        Objects.requireNonNull(font);
        int rowY = y + 4 + 9 + 3;
        Objects.requireNonNull(font);
        int labelY = rowY + (10 - 9) / 2 + 1;
        int cursor = left;
        boolean placed = false;
        if (!hands.isEmpty()) {
            cursor = this.drawIcons(graphics, font, player, hands, 0, cursor, rowY, true, chrome);
            placed = true;
        }
        if (!armor.isEmpty()) {
            if (placed) {
                int lineX = cursor + 3;
                graphics.method_25294(lineX, rowY + 1, lineX + 1, rowY + 10 - 1, NametagsModule.scaleAlpha(0x1FFFFFFF, chrome));
                cursor += 7;
            }
            cursor = this.drawIcons(graphics, font, player, armor, hands.size(), cursor, rowY, false, chrome);
            placed = true;
        }
        if (healthText != null) {
            if (placed) {
                cursor += 4;
            }
            graphics.method_51433(font, healthText, cursor, labelY, healthColor, guard);
            cursor += healthW;
            placed = true;
        }
        if (placed) {
            cursor += 4;
        }
        graphics.method_51433(font, distText, cursor, labelY, -6052949, guard);
        if (!earlier.isEmpty()) {
            int seenY = rowY + 10 + 3;
            Objects.requireNonNull(font);
            graphics.method_51433(font, SEEN_LABEL, left, seenY + (10 - 9) / 2 + 1, -6052949, guard);
            this.drawIcons(graphics, font, player, earlier, hands.size() + armor.size(), left + font.method_1727(SEEN_LABEL) + 4, seenY, false, chrome);
        }
        pose.popMatrix();
    }

    private static List<class_1799> heldItems(class_742 player) {
        class_1799 main = player.method_6047();
        class_1799 off = player.method_6079();
        if (main.method_7960() && off.method_7960()) {
            return List.of();
        }
        return List.of(main, off);
    }

    private static List<class_1799> wornArmor(class_742 player) {
        ArrayList<class_1799> worn = new ArrayList<class_1799>(ARMOR_SLOTS.length);
        for (class_1304 slot : ARMOR_SLOTS) {
            class_1799 stack = player.method_6118(slot);
            if (stack.method_7960()) continue;
            worn.add(stack);
        }
        return worn;
    }

    private static int iconsWidth(int count) {
        return count == 0 ? 0 : count * 12 - 2;
    }

    private static int extend(int total, int width, int gap) {
        if (width <= 0) {
            return total;
        }
        return total == 0 ? width : total + gap + width;
    }

    private int drawIcons(class_332 graphics, class_327 font, class_742 holder, List<class_1799> stacks, int seedOffset, int x, int y, boolean counts, float chrome) {
        for (int i = 0; i < stacks.size(); ++i) {
            if (i > 0) {
                x += 2;
            }
            this.drawIcon(graphics, font, holder, stacks.get(i), holder.method_5628() * 8 + seedOffset + i, x, y, counts, chrome);
            x += 10;
        }
        return x;
    }

    private void drawIcon(class_332 graphics, class_327 font, class_742 holder, class_1799 stack, int seed, int x, int y, boolean count, float chrome) {
        if (stack.method_7960()) {
            graphics.method_25294(x + 2, y + 2, x + 10 - 2, y + 10 - 2, NametagsModule.scaleAlpha(0x1AFFFFFF, chrome));
            return;
        }
        Matrix3x2fStack pose = graphics.method_51448();
        pose.pushMatrix();
        pose.translate((float)x, (float)y);
        pose.scale(0.625f, 0.625f);
        graphics.method_51423((class_1309)holder, stack, 0, 0, seed);
        if (this.sDurability.get() && stack.method_31578()) {
            graphics.method_25294(2, 13, 15, 15, -16777216);
            graphics.method_25294(2, 13, 2 + stack.method_31579(), 14, 0xFF000000 | stack.method_31580());
        }
        if (count && stack.method_7947() > 1) {
            String text = String.valueOf(stack.method_7947());
            graphics.method_51433(font, text, 17 - font.method_1727(text), 9, -657929, true);
        }
        pose.popMatrix();
    }

    private void renderItemTag(class_332 graphics, class_310 mc, class_4184 camera, class_1542 item, float partialTick) {
        class_243 anchor = item.method_30950(partialTick).method_1031(0.0, 0.35, 0.0);
        float[] screen = WorldLabel.project(mc, anchor, graphics.method_51421(), graphics.method_51443());
        if (screen == null) {
            return;
        }
        class_1799 stack = item.method_6983();
        String itemName = stack.method_7964().getString();
        int count = stack.method_7947();
        String countText = count > 1 ? "x" + count : null;
        class_327 font = mc.field_1772;
        int nameW = font.method_1727(itemName);
        int countW = countText != null ? font.method_1727(countText) : 0;
        int totalW = nameW + (countText != null ? 3 + countW : 0);
        double dist = anchor.method_1022(camera.method_71156());
        float scale = (float)Math.min(1.0, Math.max(0.25, 6.0 / Math.max(1.0, dist)));
        Matrix3x2fStack pose = graphics.method_51448();
        pose.pushMatrix();
        pose.translate(screen[0], screen[1]);
        pose.scale(scale, scale);
        int tx = -totalW / 2;
        Objects.requireNonNull(font);
        int ty = -9 / 2;
        graphics.method_51433(font, itemName, tx, ty, -1447442, true);
        if (countText != null) {
            graphics.method_51433(font, countText, tx + nameW + 3, ty, -10742, true);
        }
        pose.popMatrix();
    }

    private static void drawPanel(class_332 g, int x1, int y1, int x2, int y2, float surface, float chrome) {
        NametagsModule.drawR4Rect(g, x1 - 1, y1 + 1, x2 + 1, y2 + 2, NametagsModule.scaleAlpha(0x17000000, chrome));
        NametagsModule.drawR4Rect(g, x1, y1 + 1, x2, y2 + 1, NametagsModule.scaleAlpha(0x2E000000, chrome));
        NametagsModule.drawR4Rect(g, x1, y1, x2, y2, Math.round(surface * 255.0f) << 24 | 0x16181D);
        NametagsModule.drawR4Border(g, x1, y1, x2, y2, NametagsModule.scaleAlpha(0x17FFFFFF, chrome));
    }

    private static int scaleAlpha(int argb, float factor) {
        int alpha = Math.round((float)(argb >>> 24 & 0xFF) * Math.max(0.0f, Math.min(1.0f, factor)));
        return alpha << 24 | argb & 0xFFFFFF;
    }

    private static void drawR4Rect(class_332 g, int x1, int y1, int x2, int y2, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        g.method_25294(x1 + 4, y1, x2 - 4, y1 + 1, color);
        g.method_25294(x1 + 2, y1 + 1, x2 - 2, y1 + 2, color);
        g.method_25294(x1 + 1, y1 + 2, x2 - 1, y1 + 4, color);
        g.method_25294(x1, y1 + 4, x2, y2 - 4, color);
        g.method_25294(x1 + 1, y2 - 4, x2 - 1, y2 - 2, color);
        g.method_25294(x1 + 2, y2 - 2, x2 - 2, y2 - 1, color);
        g.method_25294(x1 + 4, y2 - 1, x2 - 4, y2, color);
    }

    private static void drawR4Border(class_332 g, int x1, int y1, int x2, int y2, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        g.method_25294(x1 + 4, y1, x2 - 4, y1 + 1, color);
        g.method_25294(x1 + 4, y2 - 1, x2 - 4, y2, color);
        g.method_25294(x1, y1 + 4, x1 + 1, y2 - 4, color);
        g.method_25294(x2 - 1, y1 + 4, x2, y2 - 4, color);
        g.method_25294(x1 + 2, y1 + 1, x1 + 4, y1 + 2, color);
        g.method_25294(x1 + 1, y1 + 2, x1 + 2, y1 + 4, color);
        g.method_25294(x2 - 4, y1 + 1, x2 - 2, y1 + 2, color);
        g.method_25294(x2 - 2, y1 + 2, x2 - 1, y1 + 4, color);
        g.method_25294(x1 + 1, y2 - 4, x1 + 2, y2 - 2, color);
        g.method_25294(x1 + 2, y2 - 2, x1 + 4, y2 - 1, color);
        g.method_25294(x2 - 2, y2 - 4, x2 - 1, y2 - 2, color);
        g.method_25294(x2 - 4, y2 - 2, x2 - 2, y2 - 1, color);
    }

    @Environment(value=EnvType.CLIENT)
    private record Nearby(class_1297 entity, double distanceSq) {
    }
}

