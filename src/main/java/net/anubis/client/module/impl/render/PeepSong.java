/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.module.impl.render;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
enum PeepSong {
    STAR_SHOPPING("starshopping"),
    CRYBABY("crybaby"),
    CRY_ALONE("cryalone"),
    HELLBOY("hellboy"),
    BEAMER_BOY("beamerboy"),
    SPOTLIGHT("spotlight"),
    THE_BRIGHTSIDE("thebrightside", "brightside"),
    SUNLIGHT_ON_YOUR_SKIN("sunlightonyourskin"),
    LIFE_IS_BEAUTIFUL("lifeisbeautiful"),
    FALLING_DOWN("fallingdown"),
    SAVE_THAT_SHIT("savethatshit", "savethatsht"),
    AWFUL_THINGS("awfulthings"),
    WITCHBLADES("witchblades"),
    FOUR_GOLD_CHAINS("4goldchains", "fourgoldchains"),
    GHOST_BOY("ghostboy"),
    GHOST_GIRL("ghostgirl");

    static final PeepSong[] ALL;
    private static final Pattern SPACES;
    private static final Pattern BRACKETS;
    private static final Pattern TAIL;
    private static final Pattern VERSION;
    private static final Pattern NOT_NAME;
    private final String[] names;

    private PeepSong(String ... names) {
        this.names = names;
    }

    static PeepSong of(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        String plain = Normalizer.normalize(title, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        plain = SPACES.matcher(plain).replaceAll(" ");
        plain = BRACKETS.matcher(plain).replaceAll(" ");
        plain = TAIL.matcher(plain.strip()).replaceAll("");
        plain = VERSION.matcher(plain).replaceAll("");
        plain = NOT_NAME.matcher(plain).replaceAll("");
        for (PeepSong song : ALL) {
            for (String name : song.names) {
                if (!name.equals(plain)) continue;
                return song;
            }
        }
        return null;
    }

    static {
        ALL = PeepSong.values();
        SPACES = Pattern.compile("[\\s\\p{Z}]+");
        BRACKETS = Pattern.compile("[(\\[{][^)\\]}]*[)\\]}]");
        TAIL = Pattern.compile("\\s+(?:[-\u2013\u2014|]|feat\\.?|ft\\.?|with)\\s.*$");
        VERSION = Pattern.compile("\\s*[-\u2013\u2014]\\s*(?:\\d{4}\\s*)?(?:remaster(?:ed)?|remix(?:ed)?|live)\\b.*$");
        NOT_NAME = Pattern.compile("[^a-z0-9]");
    }
}

