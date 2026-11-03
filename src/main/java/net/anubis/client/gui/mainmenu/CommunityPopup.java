/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImDrawList
 *  imgui.ImFont
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 */
package net.anubis.client.gui.mainmenu;

import imgui.ImDrawList;
import imgui.ImFont;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.EgyptDraw;
import net.anubis.client.gui.mainmenu.DuskWidgets;
import net.anubis.client.gui.mainmenu.MenuFrame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;

@Environment(value=EnvType.CLIENT)
public final class CommunityPopup {
    public static final String INVITE = "discord.gg/anubis";
    public static final String INVITE_URL = "https://discord.gg/anubis";
    public static final String BETS = "donutbets.net";
    public static final String BETS_URL = "https://donutbets.net";
    private static final float OPEN_DELAY = 0.6f;
    private static final float NEXT_DELAY = 0.35f;
    private static final float MARK_RING = 54.0f;
    private static final float MARK_GAP = 14.0f;
    private static final float LINE_GAP = 3.0f;
    private static final float CAPTION_TRACK = 4.0f;
    private static final float CHECK_BOX = 16.0f;
    private static final float CHECK_LABEL_GAP = 9.0f;
    private static final float CHECK_REACH = 5.0f;
    private static final float ROW_GAP = 18.0f;
    private static final float CONTENT_H = 88.0f;
    private static final float[] CLYDE = CommunityPopup.clydeOutline();
    private static final float CLYDE_EYE_X = 0.332f;
    private static final float CLYDE_EYE_Y = 0.076f;
    private static final float CLYDE_EYE_RX = 0.18f;
    private static final float CLYDE_EYE_RY = 0.2f;
    private static boolean offered;
    private static boolean betsOffered;
    private final DuskWidgets ui = new DuskWidgets();
    private final MenuFrame frame = new MenuFrame();
    private DuskWidgets.Dialog dialog;
    private Offer offer = Offer.DISCORD;
    private boolean dontShowAgain;
    private long openedAt;
    private float closedAt;

    public void update(float menuTime, boolean live) {
        if (this.dialog != null && this.dialog.isClosed()) {
            this.dialog = null;
            this.closedAt = menuTime;
        }
        if (this.dialog != null || !live) {
            return;
        }
        if (!offered) {
            if (menuTime < 0.6f) {
                return;
            }
            offered = true;
            if (ThemeStore.get().communityPopup.get()) {
                this.open(Offer.DISCORD);
                return;
            }
        }
        if (!betsOffered && menuTime >= Math.max(0.6f, this.closedAt + 0.35f)) {
            betsOffered = true;
            this.open(Offer.DONUTBETS);
        }
    }

    public boolean isOpen() {
        return this.dialog != null && !this.dialog.isClosed();
    }

    public void render(ImDrawList dl, float width, float height, float scale, float dt, float mouseX, float mouseY, boolean clicked) {
        if (!this.isOpen()) {
            return;
        }
        float time = (float)(System.nanoTime() - this.openedAt) / 1.0E9f;
        this.frame.begin(dl, dl, width, height, scale, time, dt, mouseX, mouseY, clicked);
        this.ui.begin(this.frame);
        this.ui.dialog(this.dialog);
    }

    public boolean keyPressed(int key) {
        if (!this.isOpen()) {
            return false;
        }
        if (key == 256) {
            this.dialog.dismiss();
        }
        return true;
    }

    private void open(Offer next) {
        this.offer = next;
        this.dontShowAgain = false;
        this.openedAt = System.nanoTime();
        this.dialog = next == Offer.DISCORD ? new DuskWidgets.Dialog("Join Anubis Community", "Updates, configs and support. Come say hi.").content(88.0f, this::drawContent).action("Join", this::join).cancel("Later", this::answered) : new DuskWidgets.Dialog("Check out DonutBets", "").content(54.0f, this::drawContent).action("Visit", this::join).cancel("Later");
    }

    private void join() {
        class_156.method_668().method_670(this.offer.url);
        this.answered();
    }

    private void answered() {
        if (!this.dontShowAgain || this.offer != Offer.DISCORD) {
            return;
        }
        ThemeStore theme = ThemeStore.get();
        theme.communityPopup.set(false);
        theme.save();
    }

    private void drawContent(DuskWidgets ui, DuskWidgets.Dialog dialog, float x, float y, float w, float h) {
        MenuFrame f = ui.frame();
        ImDrawList dl = f.bg();
        float s = f.scale();
        float a = ui.alpha();
        ImFont captionFont = f.tagFont();
        ImFont inviteFont = f.menuFont();
        String caption = this.offer.caption;
        String invite = this.offer.link;
        float ring = 54.0f * s;
        float track = 4.0f * s;
        float captionW = f.spacedWidth(captionFont, caption, track);
        float inviteW = f.textWidth(inviteFont, invite);
        float groupW = ring + 14.0f * s + Math.max(captionW, inviteW);
        float gx = x + (w - groupW) * 0.5f;
        boolean over = ui.hovered(gx, y, groupW, ring);
        if (ui.pressed(gx, y, groupW, ring)) {
            dialog.confirm();
        }
        float k = ui.hover("invite", over);
        float cx = gx + ring * 0.5f;
        float cy = y + ring * 0.5f - 2.0f * s;
        float r = ring * 0.44f;
        DuskWidgets.shenRing(dl, cx, cy, r, k, s, a);
        int ink = EgyptDraw.col(EgyptDraw.mix(-1326998, -16777216, k), a);
        int hole = EgyptDraw.col(EgyptDraw.mix(-16120059, -871878, k), a);
        if (this.offer == Offer.DISCORD) {
            CommunityPopup.discordMark(dl, cx, cy, r * 1.1f, ink, hole);
        } else {
            CommunityPopup.donutMark(dl, cx, cy, r, ink, hole);
        }
        float captionH = f.textHeight(captionFont);
        float inviteH = f.textHeight(inviteFont);
        float tx = gx + ring + 14.0f * s;
        float ty = cy - (captionH + 3.0f * s + inviteH) * 0.5f;
        f.spacedText(dl, captionFont, tx, ty, track, -7372954, a, caption);
        float inviteY = ty + captionH + 3.0f * s;
        float underY = inviteY + inviteH + s;
        f.text(dl, inviteFont, tx, inviteY, EgyptDraw.mix(-871878, -11654, k), a, invite);
        if (k > 0.01f) {
            dl.addLine(tx, underY, tx + inviteW * k, underY, EgyptDraw.col(-11654, k * a), Math.max(1.0f, s));
        }
        if (this.offer != Offer.DISCORD) {
            return;
        }
        ImFont labelFont = f.tagFont();
        String label = "Don't show again";
        float box = 16.0f * s;
        float reach = 5.0f * s;
        float rowW = box + 9.0f * s + f.textWidth(labelFont, label);
        float bx = x + (w - rowW) * 0.5f;
        float by = y + h - box;
        boolean overCheck = ui.hovered(bx, by - reach, rowW, box + 2.0f * reach);
        if (ui.pressed(bx, by - reach, rowW, box + 2.0f * reach)) {
            this.dontShowAgain = !this.dontShowAgain;
        }
        float kc = ui.hover("dont-show-again", overCheck);
        CommunityPopup.checkBox(dl, bx, by, box, this.dontShowAgain, kc, s, a);
        int labelColor = EgyptDraw.mix(this.dontShowAgain ? -1326998 : -7372954, -11654, kc);
        f.text(dl, labelFont, bx + box + 9.0f * s, by + (box - f.textHeight(labelFont)) * 0.5f + s, labelColor, a, label);
    }

    private static void checkBox(ImDrawList dl, float x, float y, float size, boolean checked, float k, float s, float a) {
        float r = 3.0f * s;
        if (!checked) {
            dl.addRectFilled(x, y, x + size, y + size, EgyptDraw.col(-16777216, 0.55f * a), r);
            dl.addRect(x, y, x + size, y + size, EgyptDraw.col(-871878, (0.45f + 0.4f * k) * a), r, 0, Math.max(1.0f, 1.2f * s));
            return;
        }
        dl.addRectFilled(x, y, x + size, y + size, EgyptDraw.col(EgyptDraw.mix(-871878, -11654, k), a), r);
        dl.pathClear();
        dl.pathLineTo(x + size * 0.24f, y + size * 0.52f);
        dl.pathLineTo(x + size * 0.43f, y + size * 0.71f);
        dl.pathLineTo(x + size * 0.77f, y + size * 0.31f);
        dl.pathStroke(EgyptDraw.col(-16777216, a), 0, Math.max(1.4f, 2.0f * s));
    }

    private static void discordMark(ImDrawList dl, float cx, float cy, float w, int ink, int hole) {
        float u = w * 0.5f;
        dl.pathClear();
        for (int i = 0; i < CLYDE.length; i += 2) {
            dl.pathLineTo(cx + CLYDE[i] * u, cy + CLYDE[i + 1] * u);
        }
        dl.pathFillConcave(ink);
        int segments = 16;
        for (int side = -1; side <= 1; side += 2) {
            float ex = cx + (float)side * 0.332f * u;
            float ey = cy + 0.076f * u;
            dl.pathClear();
            for (int i = 0; i < segments; ++i) {
                double angle = (double)i * Math.PI * 2.0 / (double)segments;
                dl.pathLineTo(ex + (float)Math.cos(angle) * 0.18f * u, ey + (float)Math.sin(angle) * 0.2f * u);
            }
            dl.pathFillConvex(hole);
        }
    }

    private static void donutMark(ImDrawList dl, float cx, float cy, float r, int ink, int hole) {
        dl.addCircleFilled(cx, cy, r * 0.74f, ink, 40);
        dl.addCircleFilled(cx, cy, r * 0.27f, hole, 28);
        float thick = Math.max(1.2f, r * 0.075f);
        for (int i = 0; i < 7; ++i) {
            double angle = (double)i * Math.PI * 2.0 / 7.0 + 0.35;
            float sx = cx + (float)Math.cos(angle) * r * 0.505f;
            float sy = cy + (float)Math.sin(angle) * r * 0.505f;
            float dx = (float)Math.cos(angle + 1.1 + (double)(i % 3) * 0.6) * r * 0.075f;
            float dy = (float)Math.sin(angle + 1.1 + (double)(i % 3) * 0.6) * r * 0.075f;
            dl.addLine(sx - dx, sy - dy, sx + dx, sy + dy, hole, thick);
        }
    }

    private static float[] clydeOutline() {
        int i;
        int cheekSteps = 6;
        int chinSteps = 4;
        float[] half = new float[2 * (4 + cheekSteps + 3 + chinSteps)];
        int n = 0;
        n = CommunityPopup.put(half, n, 0.0f, -0.667f);
        n = CommunityPopup.put(half, n, 0.229f, -0.65f);
        n = CommunityPopup.put(half, n, 0.282f, -0.758f);
        n = CommunityPopup.put(half, n, 0.694f, -0.631f);
        for (i = 1; i <= cheekSteps; ++i) {
            float t = (float)i / (float)cheekSteps;
            float v = 1.0f - t;
            float b0 = v * v * v;
            float b1 = 3.0f * v * v * t;
            float b2 = 3.0f * v * t * t;
            float b3 = t * t * t;
            n = CommunityPopup.put(half, n, b0 * 0.694f + b1 * 0.938f + b2 * 1.03f + b3 * 0.992f, b0 * -0.631f + b1 * -0.272f + b2 * 0.103f + b3 * 0.504f);
        }
        n = CommunityPopup.put(half, n, 0.749f, 0.653f);
        n = CommunityPopup.put(half, n, 0.486f, 0.758f);
        n = CommunityPopup.put(half, n, 0.344f, 0.531f);
        for (i = 1; i <= chinSteps; ++i) {
            float cx = 0.344f * (1.0f - (float)i / (float)chinSteps);
            n = CommunityPopup.put(half, n, cx, -0.606f + (float)Math.sqrt(1.413721f - cx * cx));
        }
        int points = n / 2;
        float[] outline = new float[2 * (2 * points - 2)];
        System.arraycopy(half, 0, outline, 0, n);
        for (int i2 = points - 2; i2 >= 1; --i2) {
            n = CommunityPopup.put(outline, n, -half[2 * i2], half[2 * i2 + 1]);
        }
        return outline;
    }

    private static int put(float[] points, int n, float x, float y) {
        points[n] = x;
        points[n + 1] = y;
        return n + 2;
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Offer {
        DISCORD("DISCORD", "discord.gg/anubis", "https://discord.gg/anubis"),
        DONUTBETS("DONUTBETS", "donutbets.net", "https://donutbets.net");

        final String caption;
        final String link;
        final String url;

        private Offer(String caption, String link, String url) {
            this.caption = caption;
            this.link = link;
            this.url = url;
        }
    }
}

