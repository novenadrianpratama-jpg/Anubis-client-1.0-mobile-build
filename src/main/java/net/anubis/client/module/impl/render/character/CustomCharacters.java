/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.module.impl.render.character;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.anubis.client.module.impl.render.character.gltf.CharacterBake;
import net.anubis.client.module.impl.render.character.gltf.ImageDecoder;
import net.anubis.client.module.impl.render.character.kit.BakedDesign;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class CustomCharacters {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final long MAX_FILE = 0x10000000L;
    private static final int MAX_TEXTURE = 8192;

    private CustomCharacters() {
    }

    static Path folder() {
        return FabricLoader.getInstance().getConfigDir().resolve("anubis").resolve("characters");
    }

    private static Path cache() {
        return CustomCharacters.folder().resolve(".baked");
    }

    static List<BakedDesign> scan() {
        ArrayList<Path> files = new ArrayList<Path>();
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(CustomCharacters.folder(), "*.{glb,GLB}");){
            for (Path file : dir) {
                if (!Files.isRegularFile(file, new LinkOption[0])) continue;
                files.add(file);
            }
        }
        catch (IOException ignored) {
            return List.of();
        }
        files.sort(null);
        ArrayList<BakedDesign> out = new ArrayList<BakedDesign>();
        HashSet<String> ids = new HashSet<String>();
        for (Path file : files) {
            String name = file.getFileName().toString();
            name = name.substring(0, name.length() - 4);
            try {
                String stamp = name + Files.size(file) + Files.getLastModifiedTime(file, new LinkOption[0]).toMillis() + "3";
                String id = "custom_" + CustomCharacters.slug(name) + "_" + Integer.toHexString(stamp.hashCode());
                ids.add(id);
                out.add(new BakedDesign(id, name, () -> CustomCharacters.open(file, id)));
            }
            catch (IOException e) {
                LOGGER.warn("[Anubis] Skipping custom character {}: {}", (Object)file, (Object)e.toString());
            }
        }
        CustomCharacters.prune(ids);
        return out;
    }

    private static String slug(String name) {
        String s = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
        return s.isEmpty() ? "model" : (s.length() > 32 ? s.substring(0, 32) : s);
    }

    private static void prune(Set<String> keep) {
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(CustomCharacters.cache(), "*.anc");){
            for (Path file : dir) {
                String id = file.getFileName().toString().replace(".anc", "");
                if (keep.contains(id)) continue;
                Files.deleteIfExists(file);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static synchronized InputStream open(Path file, String id) throws IOException {
        Path baked = CustomCharacters.cache().resolve(id + ".anc");
        if (Files.exists(baked, new LinkOption[0])) {
            return Files.newInputStream(baked, new OpenOption[0]);
        }
        if (Files.size(file) > 0x10000000L) {
            throw new IOException("the file is larger than 256 MB");
        }
        String name = file.getFileName().toString();
        CharacterModule.baking(name);
        long start = System.nanoTime();
        CharacterBake.Result result = CharacterBake.bake(Files.readAllBytes(file), new CharacterBake.Options(), CustomCharacters::decode);
        LOGGER.info("[Anubis] Baked custom character {} in {} ms, moving {}", new Object[]{name, (System.nanoTime() - start) / 1000000L, result.limbs()});
        if (result.limbs().size() < 3) {
            CharacterModule.notice(name, result.limbs());
        }
        Files.createDirectories(CustomCharacters.cache(), new FileAttribute[0]);
        Path temp = CustomCharacters.cache().resolve(id + ".tmp");
        Files.write(temp, result.data(), new OpenOption[0]);
        Files.move(temp, baked, StandardCopyOption.REPLACE_EXISTING);
        return new ByteArrayInputStream(result.data());
    }

    /*
     * Exception decompiling
     */
    private static ImageDecoder.Picture decode(byte[] data) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

