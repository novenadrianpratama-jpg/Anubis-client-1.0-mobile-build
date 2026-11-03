/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.metadata.ModOrigin$Kind
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import net.anubis.client.BuildConfig;
import net.anubis.client.security.TamperStop;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class OfficialBuild {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String MOD_ID = "anubisplus";
    private static final Pattern SIG_FILE = Pattern.compile("SIG-[^.]*(\\.[A-Z0-9]{1,3})?");
    private static volatile State state = State.PENDING;
    private static volatile String reason = "";
    private static final AtomicBoolean tickChecked = new AtomicBoolean();

    private OfficialBuild() {
    }

    public static State state() {
        return state;
    }

    public static void checkBeforeLaunch() {
        OfficialBuild.run();
        OfficialBuild.stopIfModified();
    }

    public static void enforceIfPending() {
        if (state != State.PENDING) {
            return;
        }
        OfficialBuild.run();
        OfficialBuild.stopIfModified();
    }

    public static void enforceOnFirstTick() {
        if (tickChecked.getAndSet(true)) {
            return;
        }
        OfficialBuild.enforceIfPending();
    }

    private static void stopIfModified() {
        if (state == State.MODIFIED) {
            TamperStop.stopGame(reason, OfficialBuild.ownJar());
        }
    }

    private static void run() {
        Result result;
        try {
            result = OfficialBuild.check();
        }
        catch (RuntimeException e) {
            result = new Result(State.NOT_CHECKED, "the check could not run (" + e.getClass().getSimpleName() + ")");
        }
        reason = result.reason();
        state = result.state();
        switch (result.state().ordinal()) {
            case 1: {
                LOGGER.info("[Anubis] Build check: this jar matches the signed release ({})", (Object)result.reason());
                break;
            }
            case 2: {
                LOGGER.warn("[Anubis] Build check: this jar differs from the signed Anubis release ({}). Download Anubis only from the official Discord.", (Object)result.reason());
                break;
            }
            default: {
                LOGGER.info("[Anubis] Build check skipped: {}", (Object)result.reason());
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Result check() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return new Result(State.NOT_CHECKED, "development environment");
        }
        String expected = String.join((CharSequence)"", BuildConfig.SIGN_CERT_PARTS);
        if (expected.isEmpty()) {
            return new Result(State.NOT_CHECKED, "unsigned local build");
        }
        Path jar = OfficialBuild.ownJar();
        if (jar == null) {
            return new Result(State.NOT_CHECKED, "the Anubis jar file could not be located");
        }
        try (JarFile file = new JarFile(jar.toFile(), true);){
            Result result;
            int signed = 0;
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (entry.isDirectory()) {
                    if (entry.getSize() <= 0L) continue;
                    Result result2 = OfficialBuild.modified("unexpected data in " + name);
                    return result2;
                }
                if (OfficialBuild.signingRelated(name)) continue;
                try (InputStream in = file.getInputStream(entry);){
                    in.transferTo(OutputStream.nullOutputStream());
                }
                CodeSigner[] signers = entry.getCodeSigners();
                if (signers == null) {
                    Result result3 = OfficialBuild.modified((String)(signed == 0 ? "the signature is missing" : "a file was added: " + name));
                    return result3;
                }
                if (!OfficialBuild.signedBy(signers, expected)) {
                    Result result4 = OfficialBuild.modified("signed with a different key");
                    return result4;
                }
                ++signed;
            }
            if (signed == 0) {
                result = OfficialBuild.modified("the signature is missing");
                return result;
            }
            result = new Result(State.VERIFIED, signed + " files checked");
            return result;
        }
        catch (SecurityException e) {
            return OfficialBuild.modified("a file does not match the signature: " + e.getMessage());
        }
        catch (IOException e) {
            return new Result(State.NOT_CHECKED, "the jar could not be read (" + e.getClass().getSimpleName() + ")");
        }
    }

    private static Result modified(String reason) {
        return new Result(State.MODIFIED, reason);
    }

    private static Path ownJar() {
        ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
        if (mod == null || mod.getOrigin().getKind() != ModOrigin.Kind.PATH) {
            return null;
        }
        List paths = mod.getOrigin().getPaths();
        if (paths.size() != 1) {
            return null;
        }
        Path path = (Path)paths.get(0);
        boolean jar = path.getFileName() != null && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar");
        return jar && Files.isRegularFile(path, new LinkOption[0]) ? path : null;
    }

    private static boolean signingRelated(String name) {
        String upper = name.toUpperCase(Locale.ROOT);
        if (!upper.startsWith("META-INF/") || upper.indexOf(47, 9) >= 0) {
            return false;
        }
        String file = upper.substring(9);
        return file.equals("MANIFEST.MF") || file.endsWith(".SF") || file.endsWith(".DSA") || file.endsWith(".RSA") || file.endsWith(".EC") || SIG_FILE.matcher(file).matches();
    }

    private static boolean signedBy(CodeSigner[] signers, String expected) {
        for (CodeSigner signer : signers) {
            List<? extends Certificate> chain = signer.getSignerCertPath().getCertificates();
            if (chain.isEmpty() || !expected.equalsIgnoreCase(OfficialBuild.fingerprint(chain.get(0)))) continue;
            return true;
        }
        return false;
    }

    private static String fingerprint(Certificate certificate) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()));
        }
        catch (GeneralSecurityException e) {
            return "";
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        PENDING,
        VERIFIED,
        MODIFIED,
        NOT_CHECKED;

    }

    @Environment(value=EnvType.CLIENT)
    private record Result(State state, String reason) {
    }
}

