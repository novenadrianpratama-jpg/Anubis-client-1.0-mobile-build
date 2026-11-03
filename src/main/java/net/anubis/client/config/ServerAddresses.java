/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.HostAndPort
 *  com.google.common.net.InetAddresses
 *  com.google.common.net.InternetDomainName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.config;

import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import java.net.IDN;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class ServerAddresses {
    public static final int DEFAULT_PORT = 25565;
    private static final int MAX_HOST = 253;
    private static final Pattern HOST = Pattern.compile("[a-z0-9_-]+(\\.[a-z0-9_-]+)*");
    private static final Set<String> FRONT_DOORS = Set.of("play", "mc", "www", "join", "hub", "lobby", "server", "connect", "go");

    private ServerAddresses() {
    }

    public static Address parse(String text) {
        if (text == null) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty() || trimmed.length() > 261) {
            return null;
        }
        try {
            HostAndPort parsed = HostAndPort.fromString((String)trimmed).withDefaultPort(25565);
            String host = parsed.getHost().toLowerCase(Locale.ROOT);
            while (host.endsWith(".")) {
                host = host.substring(0, host.length() - 1);
            }
            if (host.isEmpty()) {
                return null;
            }
            if (!(InetAddresses.isInetAddress((String)host) || (host = IDN.toASCII(host).toLowerCase(Locale.ROOT)).length() <= 253 && HOST.matcher(host).matches())) {
                return null;
            }
            return new Address(host, parsed.getPort());
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return null;
        }
    }

    public static int score(Address entry, Address joined) {
        String actual;
        if (entry == null || joined == null || entry.port() != joined.port()) {
            return 0;
        }
        String listed = entry.host();
        if (listed.equals(actual = joined.host())) {
            return 3;
        }
        if (InetAddresses.isInetAddress((String)listed) || InetAddresses.isInetAddress((String)actual)) {
            return 0;
        }
        String site = ServerAddresses.site(listed);
        if (site.equals(ServerAddresses.site(actual))) {
            return 2;
        }
        return actual.endsWith("." + site) ? 1 : 0;
    }

    private static String site(String host) {
        int dot = host.indexOf(46);
        if (dot <= 0 || !FRONT_DOORS.contains(host.substring(0, dot))) {
            return host;
        }
        String rest = host.substring(dot + 1);
        try {
            return InternetDomainName.from((String)rest).isUnderPublicSuffix() ? rest : host;
        }
        catch (IllegalArgumentException e) {
            return rest.indexOf(46) > 0 ? rest : host;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Address(String host, int port) {
        @Override
        public String toString() {
            Object shown = this.host.indexOf(58) >= 0 ? "[" + this.host + "]" : this.host;
            return this.port == 25565 ? shown : (String)shown + ":" + this.port;
        }
    }
}

