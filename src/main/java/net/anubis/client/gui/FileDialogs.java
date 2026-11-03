/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
final class FileDialogs {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final String PATTERN = "*.json";
    private static final String DESCRIPTION = "Anubis profile (*.json)";
    private static volatile boolean open;
    private static volatile Path lastFolder;

    private FileDialogs() {
    }

    static boolean busy() {
        return open;
    }

    static void openProfile(String title, Consumer<Path> picked) {
        FileDialogs.show(stack -> TinyFileDialogs.tinyfd_openFileDialog((CharSequence)title, (CharSequence)FileDialogs.startIn(null), (PointerBuffer)FileDialogs.filters(stack), (CharSequence)DESCRIPTION, (boolean)false), picked);
    }

    static void saveProfile(String title, String fileName, Consumer<Path> picked) {
        FileDialogs.show(stack -> TinyFileDialogs.tinyfd_saveFileDialog((CharSequence)title, (CharSequence)FileDialogs.startIn(fileName), (PointerBuffer)FileDialogs.filters(stack), (CharSequence)DESCRIPTION), picked);
    }

    private static PointerBuffer filters(MemoryStack stack) {
        return (PointerBuffer)stack.mallocPointer(1).put(stack.UTF8((CharSequence)PATTERN)).flip();
    }

    private static String startIn(String fileName) {
        Path folder = lastFolder;
        if (folder == null || !Files.isDirectory(folder, new LinkOption[0])) {
            folder = FileDialogs.defaultFolder();
        }
        return fileName == null ? String.valueOf(folder) + File.separator : folder.resolve(fileName).toString();
    }

    private static Path defaultFolder() {
        try {
            Path home = Path.of(System.getProperty("user.home", "."), new String[0]);
            Path downloads = home.resolve("Downloads");
            return Files.isDirectory(downloads, new LinkOption[0]) ? downloads : home;
        }
        catch (InvalidPathException e) {
            return Path.of(".", new String[0]).toAbsolutePath();
        }
    }

    private static void show(Function<MemoryStack, String> dialog, Consumer<Path> picked) {
        if (open) {
            return;
        }
        open = true;
        Thread thread = new Thread(() -> {
            Path path;
            String result = null;
            try (MemoryStack stack = MemoryStack.stackPush();){
                result = (String)dialog.apply(stack);
            }
            catch (Throwable t) {
                LOGGER.warn("[Anubis] File dialog failed", t);
            }
            finally {
                open = false;
            }
            if (result == null || result.isBlank()) {
                return;
            }
            try {
                path = Path.of(result, new String[0]).toAbsolutePath();
            }
            catch (InvalidPathException e) {
                return;
            }
            if (path.getParent() != null) {
                lastFolder = path.getParent();
            }
            class_310.method_1551().execute(() -> picked.accept(path));
        }, "Anubis file dialog");
        thread.setDaemon(true);
        thread.start();
    }
}

