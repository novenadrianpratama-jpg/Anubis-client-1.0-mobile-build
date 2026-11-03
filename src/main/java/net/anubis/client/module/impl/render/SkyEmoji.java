/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
enum SkyEmoji {
    BROKEN_HEART(Set.of("heartbreak", "heartbroken", "brokenhearted"), Set.of("k\u0131r\u0131k", "kirik", "y\u0131k\u0131l", "parampar\u00e7a")),
    HEART(Set.of("heart", "hearts"), Set.of("kalp", "kalb")),
    KISS(Set.of("kiss", "kisses", "kissing", "kissed"), Set.of("\u00f6p")),
    HUG(Set.of("hug", "hugs", "hold", "holding", "embrace", "cuddle"), Set.of("sar\u0131l", "kucak")),
    LOVE(Set.of("love", "loved", "lover", "lovers", "loving", "baby", "darling", "honey"), Set.of("a\u015fk", "sevgi", "seviyor", "sevdi")),
    SHY(Set.of("shy", "blush", "blushing", "crush", "cute"), Set.of("utan", "tatl\u0131")),
    CRY(Set.of("cry", "cried", "crying", "cries", "tears", "tear", "weep"), Set.of("a\u011fla", "g\u00f6zya\u015f")),
    SAD(Set.of("sad", "lonely", "alone", "pain", "hurt", "hurts", "sorrow", "goodbye"), Set.of("\u00fczg\u00fcn", "yaln\u0131z", "ac\u0131", "h\u00fcz\u00fcn", "keder", "elveda")),
    LAUGH(Set.of("laugh", "laughing", "haha", "funny", "lol", "smile", "smiling"), Set.of("g\u00fcl\u00fcyor", "g\u00fclerim", "kahkaha", "g\u00fcl\u00fcmse")),
    DEVIL(Set.of("sin", "sins", "sinner", "sinners", "devil", "hell", "evil", "demon", "danger", "dangerous", "wicked"), Set.of("\u015feytan", "g\u00fcnah", "cehennem", "tehlike")),
    FIRE(Set.of("fire", "burn", "burning", "flame", "flames", "lit", "blaze"), Set.of("ate\u015f", "alev", "yan\u0131yor", "yak\u0131yor")),
    ANGRY(Set.of("angry", "mad", "hate", "rage", "fury"), Set.of("k\u0131zg\u0131n", "nefret", "\u00f6fke", "sinir")),
    SHOCK(Set.of("wow", "omg", "shock", "shocked", "crazy", "insane"), Set.of("\u015fok", "inan\u0131lmaz", "\u00e7\u0131lg\u0131n")),
    COOL(Set.of("cool", "swag", "drip", "boss", "flex", "fresh"), Set.of("haval\u0131", "efsane")),
    MONEY(Set.of("money", "cash", "rich", "dollar", "dollars", "millionaire", "paid"), Set.of("zengin", "dolar", "paray\u0131", "paralar")),
    STAR(Set.of("star", "stars", "shine", "shining", "sparkle"), Set.of("y\u0131ld\u0131z", "parla")),
    MOON(Set.of("moon", "night", "midnight", "tonight", "dream", "dreams", "dreaming"), Set.of("gece", "r\u00fcya", "mehtap")),
    SLEEP(Set.of("sleep", "sleeping", "tired", "asleep"), Set.of("uyku", "uyuyor", "uyudu", "yorgun")),
    THINK(Set.of("think", "thinking", "thought", "wonder", "why", "maybe", "guess"), Set.of("d\u00fc\u015f\u00fcn", "neden", "belki", "acaba")),
    MUSIC(Set.of("dance", "dancing", "song", "songs", "music", "sing", "singing", "melody", "beat"), Set.of("dans", "\u015fark\u0131", "m\u00fczik", "melodi"));

    static final int SIZE = 256;
    private static final String IMAGES = "/assets/anubis/textures/sky_emoji/";
    private static final int CANVAS = 192;
    private static final Pattern WORD;
    private static final String[][] PHRASES;
    private static final Color INK;
    private static final Color TEAR;
    private static final Color RED;
    private final Set<String> words;
    private final Set<String> stems;

    private SkyEmoji(Set<String> words, Set<String> stems) {
        this.words = words;
        this.stems = stems;
    }

    static Match match(String line) {
        for (String[] phrase : PHRASES) {
            Matcher found = Pattern.compile(Pattern.quote(phrase[0]), 66).matcher(line);
            if (!found.find()) continue;
            int start = found.start() + phrase[0].indexOf(phrase[1]);
            return new Match(BROKEN_HEART, start, start + phrase[1].length());
        }
        ArrayList<int[]> spans = new ArrayList<int[]>();
        ArrayList<String> tokens = new ArrayList<String>();
        Matcher word = WORD.matcher(line);
        while (word.find()) {
            spans.add(new int[]{word.start(), word.end()});
            tokens.add(word.group().toLowerCase(Locale.ROOT).replace("i\u0307", "i"));
        }
        for (SkyEmoji emoji : SkyEmoji.values()) {
            for (int i = 0; i < tokens.size(); ++i) {
                String token = (String)tokens.get(i);
                boolean hit = emoji.words.contains(token);
                for (String stem : emoji.stems) {
                    hit |= token.startsWith(stem);
                }
                if (!hit) continue;
                return new Match(emoji, ((int[])spans.get(i))[0], ((int[])spans.get(i))[1]);
            }
        }
        return null;
    }

    int[] draw() {
        BufferedImage picture = this.picture();
        if (picture == null && (this == HUG || this == SHY || this == THINK)) {
            return null;
        }
        BufferedImage image = new BufferedImage(256, 256, 2);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (picture != null) {
            g.drawImage(picture, 0, 0, 256, 256, null);
            g.dispose();
            return image.getRGB(0, 0, 256, 256, null, 0, 256);
        }
        g.scale(1.3333333333333333, 1.3333333333333333);
        switch (this.ordinal()) {
            case 1: {
                SkyEmoji.heart(g, 96.0f, 100.0f, 64.0f, false);
                break;
            }
            case 0: {
                SkyEmoji.heart(g, 96.0f, 100.0f, 56.0f, true);
                break;
            }
            case 10: {
                SkyEmoji.flame(g);
                break;
            }
            case 15: {
                SkyEmoji.star(g);
                break;
            }
            case 16: {
                SkyEmoji.moon(g);
                break;
            }
            case 19: {
                SkyEmoji.notes(g);
                break;
            }
            default: {
                this.face(g);
            }
        }
        g.dispose();
        return image.getRGB(0, 0, 256, 256, null, 0, 256);
    }

    private BufferedImage picture() {
        BufferedImage bufferedImage;
        block8: {
            InputStream in = SkyEmoji.class.getResourceAsStream(IMAGES + this.name().toLowerCase(Locale.ROOT) + ".png");
            try {
                BufferedImage bufferedImage2 = bufferedImage = in == null ? null : ImageIO.read(in);
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return null;
                }
            }
            in.close();
        }
        return bufferedImage;
    }

    private void face(Graphics2D g) {
        if (this == DEVIL) {
            SkyEmoji.horns(g);
        }
        SkyEmoji.sphere(g, 96.0f, 100.0f, 72.0f);
        switch (this.ordinal()) {
            case 4: {
                SkyEmoji.heart(g, 70.0f, 90.0f, 20.0f, false);
                SkyEmoji.heart(g, 122.0f, 90.0f, 20.0f, false);
                SkyEmoji.openSmile(g, 96.0f, 122.0f, 30.0f, 18.0f, true);
                break;
            }
            case 2: {
                SkyEmoji.closedEye(g, 72.0f, 92.0f, 13.0f, true);
                SkyEmoji.eye(g, 120.0f, 90.0f, 11.0f, 14.0f, 0.0f, 0.0f);
                SkyEmoji.kissLips(g, 102.0f, 128.0f);
                SkyEmoji.heart(g, 150.0f, 62.0f, 12.0f, false);
                break;
            }
            case 6: {
                SkyEmoji.tears(g, 72.0f, 98.0f);
                SkyEmoji.tears(g, 120.0f, 98.0f);
                SkyEmoji.closedEye(g, 72.0f, 92.0f, 13.0f, false);
                SkyEmoji.closedEye(g, 120.0f, 92.0f, 13.0f, false);
                SkyEmoji.brows(g, 72.0f, 72.0f, 120.0f, 72.0f, -0.35f);
                SkyEmoji.openFrown(g, 96.0f, 134.0f, 22.0f, 13.0f);
                break;
            }
            case 7: {
                SkyEmoji.droopyEye(g, 72.0f, 94.0f);
                SkyEmoji.droopyEye(g, 120.0f, 94.0f);
                SkyEmoji.brows(g, 72.0f, 74.0f, 120.0f, 74.0f, -0.3f);
                SkyEmoji.frown(g, 96.0f, 134.0f, 18.0f);
                SkyEmoji.drop(g, 62.0f, 118.0f, 7.0f);
                break;
            }
            case 8: {
                SkyEmoji.happyEye(g, 72.0f, 90.0f);
                SkyEmoji.happyEye(g, 120.0f, 90.0f);
                SkyEmoji.laughMouth(g, 96.0f, 120.0f);
                SkyEmoji.drop(g, 44.0f, 98.0f, 8.0f);
                SkyEmoji.drop(g, 148.0f, 98.0f, 8.0f);
                break;
            }
            case 9: {
                SkyEmoji.eye(g, 72.0f, 94.0f, 11.0f, 9.0f, 0.2f, 0.0f);
                SkyEmoji.eye(g, 120.0f, 94.0f, 11.0f, 9.0f, -0.2f, 0.0f);
                SkyEmoji.brows(g, 72.0f, 78.0f, 120.0f, 78.0f, 0.45f);
                SkyEmoji.smirk(g, 96.0f, 126.0f, true);
                break;
            }
            case 11: {
                SkyEmoji.eye(g, 72.0f, 96.0f, 11.0f, 9.0f, 0.15f, 0.0f);
                SkyEmoji.eye(g, 120.0f, 96.0f, 11.0f, 9.0f, -0.15f, 0.0f);
                SkyEmoji.brows(g, 72.0f, 78.0f, 120.0f, 78.0f, 0.6f);
                SkyEmoji.teeth(g, 96.0f, 130.0f);
                SkyEmoji.vein(g, 146.0f, 56.0f);
                break;
            }
            case 12: {
                SkyEmoji.eye(g, 72.0f, 90.0f, 15.0f, 18.0f, 0.0f, 0.0f);
                SkyEmoji.eye(g, 120.0f, 90.0f, 15.0f, 18.0f, 0.0f, 0.0f);
                SkyEmoji.brows(g, 72.0f, 64.0f, 120.0f, 64.0f, -0.15f);
                SkyEmoji.ring(g, 96.0f, 134.0f, 13.0f, 16.0f);
                break;
            }
            case 13: {
                SkyEmoji.shades(g);
                SkyEmoji.smirk(g, 98.0f, 128.0f, false);
                break;
            }
            case 14: {
                SkyEmoji.dollarEyes(g);
                SkyEmoji.openSmile(g, 96.0f, 122.0f, 30.0f, 18.0f, false);
                SkyEmoji.banknoteTongue(g, 96.0f, 138.0f);
                break;
            }
            case 17: {
                SkyEmoji.closedEye(g, 72.0f, 94.0f, 13.0f, true);
                SkyEmoji.closedEye(g, 120.0f, 94.0f, 13.0f, true);
                SkyEmoji.ring(g, 96.0f, 130.0f, 6.0f, 7.0f);
                SkyEmoji.zz(g);
                break;
            }
            default: {
                SkyEmoji.eye(g, 72.0f, 92.0f, 11.0f, 14.0f, 0.0f, 0.0f);
                SkyEmoji.eye(g, 120.0f, 92.0f, 11.0f, 14.0f, 0.0f, 0.0f);
            }
        }
    }

    private static void sphere(Graphics2D g, float cx, float cy, float r) {
        for (int i = 6; i >= 1; --i) {
            float grow = (float)i * 2.2f;
            g.setColor(new Color(0, 10, 40, 12));
            g.fill(new Ellipse2D.Float(cx - r - grow * 0.5f, cy - r + 6.0f - grow * 0.3f, (r + grow * 0.5f) * 2.0f, (r + grow * 0.4f) * 2.0f));
        }
        Ellipse2D.Float ball = new Ellipse2D.Float(cx - r, cy - r, r * 2.0f, r * 2.0f);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx - r * 0.35f, cy - r * 0.45f), r * 1.55f, new float[]{0.0f, 0.35f, 0.75f, 1.0f}, new Color[]{new Color(11132159), new Color(4101621), new Color(1791689), new Color(798078)}));
        g.fill(ball);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy + r * 0.9f), r * 1.1f, new float[]{0.0f, 1.0f}, new Color[]{new Color(7327999), new Color(7327999, true)}));
        g.fill(ball);
        g.setStroke(new BasicStroke(2.5f));
        g.setColor(new Color(666222));
        g.draw(ball);
        AffineTransform keep = g.getTransform();
        g.rotate(-0.5, cx - r * 0.42f, cy - r * 0.55f);
        g.setPaint(new GradientPaint(0.0f, cy - r * 0.8f, new Color(255, 255, 255, 190), 0.0f, cy - r * 0.25f, new Color(255, 255, 255, 0)));
        g.fill(new Ellipse2D.Float(cx - r * 0.75f, cy - r * 0.8f, r * 0.7f, r * 0.42f));
        g.setTransform(keep);
    }

    private static void eye(Graphics2D g, float cx, float cy, float rx, float ry, float tilt, float look) {
        AffineTransform keep = g.getTransform();
        g.rotate(tilt, cx, cy);
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Float(cx - rx, cy - ry, rx * 2.0f, ry * 2.0f));
        g.setColor(INK);
        float pr = Math.min(rx, ry) * 0.62f;
        g.fill(new Ellipse2D.Float(cx - pr + look, cy - pr + ry * 0.15f, pr * 2.0f, pr * 2.0f));
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Float(cx - pr * 0.25f + look, cy - pr * 0.55f + ry * 0.15f, pr * 0.6f, pr * 0.6f));
        g.setTransform(keep);
    }

    private static void closedEye(Graphics2D g, float cx, float cy, float w, boolean smile) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(5.0f, 1, 1));
        float bend = smile ? 8.0f : -8.0f;
        g.draw(new QuadCurve2D.Float(cx - w, cy, cx, cy + bend, cx + w, cy));
    }

    private static void happyEye(Graphics2D g, float cx, float cy) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(6.0f, 1, 1));
        g.draw(new QuadCurve2D.Float(cx - 13.0f, cy + 6.0f, cx, cy - 14.0f, cx + 13.0f, cy + 6.0f));
    }

    private static void droopyEye(Graphics2D g, float cx, float cy) {
        Area eye = new Area(new Ellipse2D.Float(cx - 11.0f, cy - 13.0f, 22.0f, 26.0f));
        eye.subtract(new Area(new Ellipse2D.Float(cx - 16.0f, cy - 26.0f, 32.0f, 22.0f)));
        g.setColor(Color.WHITE);
        g.fill(eye);
        g.setColor(INK);
        g.fill(new Ellipse2D.Float(cx - 6.0f, cy - 2.0f, 12.0f, 12.0f));
        g.setStroke(new BasicStroke(3.5f, 1, 1));
        g.draw(new QuadCurve2D.Float(cx - 12.0f, cy - 5.0f, cx, cy - 1.0f, cx + 12.0f, cy - 5.0f));
    }

    private static void brows(Graphics2D g, float lx, float ly, float rx, float ry, float angle) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(6.0f, 1, 1));
        float dx = 12.0f;
        float dy = 12.0f * angle;
        g.draw(new QuadCurve2D.Float(lx - dx, ly - dy, lx, ly - 3.0f, lx + dx, ly + dy));
        g.draw(new QuadCurve2D.Float(rx - dx, ry + dy, rx, ry - 3.0f, rx + dx, ry - dy));
    }

    private static void openSmile(Graphics2D g, float cx, float cy, float w, float h, boolean tongue) {
        GeneralPath mouth = new GeneralPath();
        mouth.moveTo(cx - w, cy);
        mouth.quadTo(cx, cy + h * 2.2f, cx + w, cy);
        mouth.quadTo(cx, cy + h * 0.4f, cx - w, cy);
        mouth.closePath();
        g.setColor(new Color(2755102));
        g.fill(mouth);
        if (tongue) {
            Shape keep = g.getClip();
            g.clip(mouth);
            g.setColor(new Color(16740241));
            g.fill(new Ellipse2D.Float(cx - w * 0.5f, cy + h * 0.7f, w, h * 1.4f));
            g.setClip(keep);
        }
        g.setColor(INK);
        g.setStroke(new BasicStroke(3.0f, 1, 1));
        g.draw(mouth);
    }

    private static void openFrown(Graphics2D g, float cx, float cy, float w, float h) {
        GeneralPath mouth = new GeneralPath();
        mouth.moveTo(cx - w, cy + h * 0.6f);
        mouth.quadTo(cx, cy - h * 1.4f, cx + w, cy + h * 0.6f);
        mouth.quadTo(cx, cy + h * 0.2f, cx - w, cy + h * 0.6f);
        mouth.closePath();
        g.setColor(new Color(2755102));
        g.fill(mouth);
        g.setColor(INK);
        g.setStroke(new BasicStroke(3.0f, 1, 1));
        g.draw(mouth);
    }

    private static void frown(Graphics2D g, float cx, float cy, float w) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(6.0f, 1, 1));
        g.draw(new QuadCurve2D.Float(cx - w, cy + 4.0f, cx, cy - 10.0f, cx + w, cy + 4.0f));
    }

    private static void smirk(Graphics2D g, float cx, float cy, boolean wide) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(6.0f, 1, 1));
        float w = wide ? 30.0f : 22.0f;
        GeneralPath path = new GeneralPath();
        path.moveTo(cx - w, cy - 2.0f);
        path.curveTo(cx - w * 0.3f, cy + 10.0f, cx + w * 0.5f, cy + 8.0f, cx + w, cy - 10.0f);
        g.draw(path);
    }

    private static void laughMouth(Graphics2D g, float cx, float cy) {
        GeneralPath mouth = new GeneralPath();
        mouth.moveTo(cx - 34.0f, cy - 4.0f);
        mouth.lineTo(cx + 34.0f, cy - 4.0f);
        mouth.quadTo(cx + 30.0f, cy + 38.0f, cx, cy + 38.0f);
        mouth.quadTo(cx - 30.0f, cy + 38.0f, cx - 34.0f, cy - 4.0f);
        mouth.closePath();
        g.setColor(new Color(2755102));
        g.fill(mouth);
        Shape keep = g.getClip();
        g.clip(mouth);
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(cx - 34.0f, cy - 6.0f, 68.0f, 13.0f, 6.0f, 6.0f));
        g.setColor(new Color(16740241));
        g.fill(new Ellipse2D.Float(cx - 18.0f, cy + 18.0f, 36.0f, 26.0f));
        g.setClip(keep);
        g.setColor(INK);
        g.setStroke(new BasicStroke(3.0f, 1, 1));
        g.draw(mouth);
    }

    private static void teeth(Graphics2D g, float cx, float cy) {
        RoundRectangle2D.Float mouth = new RoundRectangle2D.Float(cx - 26.0f, cy - 10.0f, 52.0f, 20.0f, 12.0f, 12.0f);
        g.setColor(Color.WHITE);
        g.fill(mouth);
        g.setColor(INK);
        g.setStroke(new BasicStroke(2.5f));
        g.draw(mouth);
        g.drawLine((int)(cx - 26.0f), (int)cy, (int)(cx + 26.0f), (int)cy);
        for (int i = -2; i <= 2; ++i) {
            g.drawLine((int)(cx + (float)i * 9.0f), (int)(cy - 10.0f), (int)(cx + (float)i * 9.0f), (int)(cy + 10.0f));
        }
    }

    private static void ring(Graphics2D g, float cx, float cy, float rx, float ry) {
        g.setColor(new Color(2755102));
        g.fill(new Ellipse2D.Float(cx - rx, cy - ry, rx * 2.0f, ry * 2.0f));
        g.setColor(INK);
        g.setStroke(new BasicStroke(3.0f));
        g.draw(new Ellipse2D.Float(cx - rx, cy - ry, rx * 2.0f, ry * 2.0f));
    }

    private static void kissLips(Graphics2D g, float cx, float cy) {
        g.setColor(INK);
        g.setStroke(new BasicStroke(5.5f, 1, 1));
        GeneralPath lips = new GeneralPath();
        lips.moveTo(cx - 6.0f, cy - 12.0f);
        lips.quadTo(cx + 10.0f, cy - 10.0f, cx + 2.0f, cy - 2.0f);
        lips.quadTo(cx + 12.0f, cy + 4.0f, cx - 6.0f, cy + 10.0f);
        g.draw(lips);
    }

    private static void tears(Graphics2D g, float cx, float cy) {
        g.setPaint(new GradientPaint(0.0f, cy, new Color(13235199), 0.0f, cy + 70.0f, new Color(5227519)));
        g.fill(new RoundRectangle2D.Float(cx - 7.0f, cy, 14.0f, 66.0f, 14.0f, 14.0f));
    }

    private static void drop(Graphics2D g, float cx, float cy, float r) {
        GeneralPath drop = new GeneralPath();
        drop.moveTo(cx, cy - r * 2.2f);
        drop.curveTo(cx + r * 1.2f, cy - r * 0.6f, cx + r, cy + r, cx, cy + r);
        drop.curveTo(cx - r, cy + r, cx - r * 1.2f, cy - r * 0.6f, cx, cy - r * 2.2f);
        g.setColor(TEAR);
        g.fill(drop);
        g.setColor(new Color(255, 255, 255, 200));
        g.fill(new Ellipse2D.Float(cx - r * 0.45f, cy - r * 0.4f, r * 0.5f, r * 0.6f));
    }

    private static void shades(Graphics2D g) {
        GeneralPath lens = new GeneralPath();
        lens.moveTo(42.0f, 80.0f);
        lens.lineTo(90.0f, 80.0f);
        lens.quadTo(90.0f, 108.0f, 68.0f, 108.0f);
        lens.quadTo(44.0f, 108.0f, 42.0f, 80.0f);
        lens.closePath();
        GeneralPath right = (GeneralPath)lens.clone();
        right.transform(AffineTransform.getTranslateInstance(60.0, 0.0));
        g.setColor(new Color(724764));
        g.fill(lens);
        g.fill(right);
        g.setStroke(new BasicStroke(6.0f, 1, 1));
        g.draw(new Line2D.Float(88.0f, 84.0f, 104.0f, 84.0f));
        g.setColor(new Color(255, 255, 255, 120));
        g.setStroke(new BasicStroke(4.0f, 1, 1));
        g.drawLine(52, 86, 62, 96);
        g.drawLine(112, 86, 122, 96);
    }

    private static void dollarEyes(Graphics2D g) {
        g.setFont(new Font("SansSerif", 1, 40));
        g.setColor(new Color(2347360));
        g.drawString("$", 60, 106);
        g.drawString("$", 108, 106);
    }

    private static void banknoteTongue(Graphics2D g, float cx, float cy) {
        g.setColor(new Color(4054148));
        g.fill(new RoundRectangle2D.Float(cx - 14.0f, cy - 4.0f, 28.0f, 26.0f, 6.0f, 6.0f));
        g.setColor(new Color(2002510));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(new RoundRectangle2D.Float(cx - 14.0f, cy - 4.0f, 28.0f, 26.0f, 6.0f, 6.0f));
    }

    private static void zz(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", 1, 30));
        g.drawString("Z", 140, 52);
        g.setFont(new Font("SansSerif", 1, 20));
        g.drawString("z", 162, 30);
    }

    private static void vein(Graphics2D g, float cx, float cy) {
        g.setColor(RED);
        g.setStroke(new BasicStroke(5.0f, 1, 1));
        for (int i = 0; i < 4; ++i) {
            AffineTransform keep = g.getTransform();
            g.rotate((double)i * Math.PI / 2.0, cx, cy);
            g.draw(new QuadCurve2D.Float(cx - 4.0f, cy - 14.0f, cx - 4.0f, cy - 4.0f, cx - 14.0f, cy - 4.0f));
            g.setTransform(keep);
        }
    }

    private static void horns(Graphics2D g) {
        for (int side = -1; side <= 1; side += 2) {
            GeneralPath horn = new GeneralPath();
            float base = 96.0f + (float)side * 40.0f;
            horn.moveTo(base - (float)side * 16.0f, 48.0f);
            horn.quadTo(base + (float)side * 12.0f, 34.0f, base + (float)side * 18.0f, 10.0f);
            horn.quadTo(base + (float)side * 26.0f, 40.0f, base + (float)side * 14.0f, 62.0f);
            horn.closePath();
            g.setPaint(new GradientPaint(0.0f, 10.0f, new Color(14183423), 0.0f, 62.0f, new Color(6954936)));
            g.fill(horn);
            g.setColor(new Color(4001138));
            g.setStroke(new BasicStroke(2.5f));
            g.draw(horn);
        }
    }

    private static Shape heartShape(float cx, float cy, float r) {
        GeneralPath heart = new GeneralPath();
        heart.moveTo(cx, cy + r * 0.95f);
        heart.curveTo(cx - r * 1.35f, cy + r * 0.05f, cx - r * 1.1f, cy - r * 1.05f, cx, cy - r * 0.45f);
        heart.curveTo(cx + r * 1.1f, cy - r * 1.05f, cx + r * 1.35f, cy + r * 0.05f, cx, cy + r * 0.95f);
        heart.closePath();
        return heart;
    }

    private static void heart(Graphics2D g, float cx, float cy, float r, boolean broken) {
        Shape shape = SkyEmoji.heartShape(cx, cy, r);
        HeartPaint paint = new HeartPaint(cx, cy, r);
        if (!broken) {
            paint.fill(g, shape);
            return;
        }
        GeneralPath crack = new GeneralPath();
        crack.moveTo(cx, cy - r);
        crack.lineTo(cx - r * 0.18f, cy - r * 0.2f);
        crack.lineTo(cx + r * 0.15f, cy + r * 0.15f);
        crack.lineTo(cx - r * 0.1f, cy + r * 0.6f);
        crack.lineTo(cx, cy + r * 1.2f);
        crack.lineTo(cx - r * 2.0f, cy + r * 1.2f);
        crack.lineTo(cx - r * 2.0f, cy - r * 1.2f);
        crack.closePath();
        Area left = new Area(shape);
        left.intersect(new Area(crack));
        Area right = new Area(shape);
        right.subtract(new Area(crack));
        left.transform(AffineTransform.getRotateInstance(-0.1, cx, cy + r));
        left.transform(AffineTransform.getTranslateInstance(-5.0, 0.0));
        right.transform(AffineTransform.getRotateInstance(0.1, cx, cy + r));
        right.transform(AffineTransform.getTranslateInstance(5.0, 0.0));
        paint.fill(g, left);
        paint.fill(g, right);
    }

    private static void flame(Graphics2D g) {
        GeneralPath outer = new GeneralPath();
        outer.moveTo(96.0f, 176.0f);
        outer.curveTo(40.0f, 176.0f, 30.0f, 122.0f, 58.0f, 86.0f);
        outer.curveTo(66.0f, 110.0f, 76.0f, 112.0f, 80.0f, 108.0f);
        outer.curveTo(70.0f, 74.0f, 92.0f, 42.0f, 110.0f, 16.0f);
        outer.curveTo(112.0f, 58.0f, 150.0f, 76.0f, 152.0f, 120.0f);
        outer.curveTo(154.0f, 156.0f, 130.0f, 176.0f, 96.0f, 176.0f);
        outer.closePath();
        g.setPaint(new GradientPaint(0.0f, 16.0f, new Color(8382719), 0.0f, 176.0f, new Color(1791689)));
        g.fill(outer);
        g.setColor(new Color(666222));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(outer);
        GeneralPath inner = new GeneralPath();
        inner.moveTo(98.0f, 168.0f);
        inner.curveTo(70.0f, 168.0f, 66.0f, 136.0f, 82.0f, 116.0f);
        inner.curveTo(88.0f, 132.0f, 96.0f, 130.0f, 98.0f, 122.0f);
        inner.curveTo(96.0f, 102.0f, 108.0f, 86.0f, 116.0f, 76.0f);
        inner.curveTo(118.0f, 104.0f, 132.0f, 118.0f, 128.0f, 142.0f);
        inner.curveTo(126.0f, 160.0f, 114.0f, 168.0f, 98.0f, 168.0f);
        inner.closePath();
        g.setPaint(new GradientPaint(0.0f, 76.0f, Color.WHITE, 0.0f, 168.0f, new Color(11132159)));
        g.fill(inner);
    }

    private static void star(Graphics2D g) {
        GeneralPath star = new GeneralPath();
        for (int i = 0; i < 10; ++i) {
            double angle = -1.5707963267948966 + (double)i * Math.PI / 5.0;
            double radius = i % 2 == 0 ? 80.0 : 34.0;
            float x = (float)(96.0 + Math.cos(angle) * radius);
            float y = (float)(100.0 + Math.sin(angle) * radius);
            if (i == 0) {
                star.moveTo(x, y);
                continue;
            }
            star.lineTo(x, y);
        }
        star.closePath();
        g.setPaint(new RadialGradientPaint(new Point2D.Float(80.0f, 80.0f), 100.0f, new float[]{0.0f, 0.5f, 1.0f}, new Color[]{new Color(16774832), new Color(16762941), new Color(14255642)}, MultipleGradientPaint.CycleMethod.NO_CYCLE));
        g.fill(star);
        g.setColor(new Color(9064202));
        g.setStroke(new BasicStroke(2.5f, 1, 1));
        g.draw(star);
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Float(72.0f, 62.0f, 22.0f, 12.0f));
    }

    private static void moon(Graphics2D g) {
        Area crescent = new Area(new Ellipse2D.Float(30.0f, 26.0f, 140.0f, 140.0f));
        crescent.subtract(new Area(new Ellipse2D.Float(70.0f, 8.0f, 130.0f, 130.0f)));
        g.setPaint(new RadialGradientPaint(new Point2D.Float(60.0f, 70.0f), 120.0f, new float[]{0.0f, 1.0f}, new Color[]{new Color(16775112), new Color(15909198)}));
        g.fill(crescent);
        g.setColor(new Color(10119954));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(crescent);
        g.setColor(new Color(16775112));
        SkyEmoji.sparkle(g, 150.0f, 140.0f, 12.0f);
        SkyEmoji.sparkle(g, 168.0f, 104.0f, 7.0f);
    }

    private static void sparkle(Graphics2D g, float cx, float cy, float r) {
        GeneralPath spark = new GeneralPath();
        spark.moveTo(cx, cy - r);
        spark.quadTo(cx, cy, cx + r, cy);
        spark.quadTo(cx, cy, cx, cy + r);
        spark.quadTo(cx, cy, cx - r, cy);
        spark.quadTo(cx, cy, cx, cy - r);
        g.fill(spark);
    }

    private static void notes(Graphics2D g) {
        g.setPaint(new GradientPaint(0.0f, 30.0f, new Color(9425919), 0.0f, 170.0f, new Color(1791689)));
        g.fill(new Ellipse2D.Float(36.0f, 132.0f, 44.0f, 32.0f));
        g.fill(new Ellipse2D.Float(112.0f, 116.0f, 44.0f, 32.0f));
        g.fill(new Rectangle2D.Float(70.0f, 42.0f, 10.0f, 104.0f));
        g.fill(new Rectangle2D.Float(146.0f, 26.0f, 10.0f, 104.0f));
        GeneralPath beam = new GeneralPath();
        beam.moveTo(70.0f, 42.0f);
        beam.lineTo(156.0f, 26.0f);
        beam.lineTo(156.0f, 50.0f);
        beam.lineTo(70.0f, 66.0f);
        beam.closePath();
        g.fill(beam);
        g.setColor(new Color(666222));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(new Ellipse2D.Float(36.0f, 132.0f, 44.0f, 32.0f));
        g.draw(new Ellipse2D.Float(112.0f, 116.0f, 44.0f, 32.0f));
        g.setColor(new Color(255, 255, 255, 160));
        g.fill(new Ellipse2D.Float(46.0f, 138.0f, 14.0f, 8.0f));
        g.fill(new Ellipse2D.Float(122.0f, 122.0f, 14.0f, 8.0f));
    }

    static {
        WORD = Pattern.compile("\\p{L}+");
        PHRASES = new String[][]{{"broken heart", "broken heart"}, {"break my heart", "heart"}, {"kalbim k\u0131r\u0131k", "k\u0131r\u0131k"}};
        INK = new Color(662080);
        TEAR = new Color(10479359);
        RED = new Color(16726876);
    }

    @Environment(value=EnvType.CLIENT)
    record Match(SkyEmoji emoji, int start, int end) {
    }

    @Environment(value=EnvType.CLIENT)
    private record HeartPaint(float cx, float cy, float r) {
        void fill(Graphics2D g, Shape shape) {
            g.setPaint(new RadialGradientPaint(new Point2D.Float(this.cx - this.r * 0.4f, this.cy - this.r * 0.5f), this.r * 1.7f, new float[]{0.0f, 0.45f, 1.0f}, new Color[]{new Color(16752050), new Color(16726876), new Color(11014190)}));
            g.fill(shape);
            g.setColor(new Color(7211552));
            g.setStroke(new BasicStroke(Math.max(1.5f, this.r * 0.05f)));
            g.draw(shape);
            Shape keep = g.getClip();
            g.clip(shape);
            g.setColor(new Color(255, 255, 255, 150));
            g.fill(new Ellipse2D.Float(this.cx - this.r * 0.85f, this.cy - this.r * 0.75f, this.r * 0.55f, this.r * 0.35f));
            g.setClip(keep);
        }
    }
}

