/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.anubis.client.config.HudTheme;
import net.anubis.client.config.Surface;
import net.anubis.client.config.ThemeStore;
import net.anubis.client.gui.GifAnimation;
import net.anubis.client.module.impl.client.PerformanceModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class ThemeImages {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final ThemeImages INSTANCE = new ThemeImages();
    private static final int MAX_SIZE = 8192;
    private static final long RESCAN_MS = 2000L;
    private static final int SWEEP_TICKS = 20;
    private static final int OPTIMIZED_SIDE = 2048;
    private static final int PREVIEW_SIDE = 384;
    private static final long CACHE_BYTES = 0x4000000L;
    private final Map<String, Texture> textures = new HashMap<String, Texture>();
    private final Map<String, Texture> previews = new HashMap<String, Texture>();
    private final Map<String, GifAnimation> animations = new HashMap<String, GifAnimation>();
    private final Map<String, GifAnimation> posters = new HashMap<String, GifAnimation>();
    private final Set<String> failed = new HashSet<String>();
    private final List<Runnable> pendingFrees = new ArrayList<Runnable>();
    private final List<Runnable> profileListeners = new ArrayList<Runnable>();
    private final Map<String, Long> lastUsed = new HashMap<String, Long>();
    private long frame;
    private boolean optimized;
    private List<String> files = List.of();
    private long lastScan;
    private boolean listingChanged;
    private int ticks;
    private volatile Path pendingImport;
    private volatile boolean dialogOpen;

    private ThemeImages() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (++this.ticks % 20 == 0) {
                this.sweep();
            }
        });
    }

    public static ThemeImages get() {
        return INSTANCE;
    }

    public void beginFrame() {
        this.flushFrees();
        boolean next = PerformanceModule.optimizationsEnabled();
        if (this.optimized != next) {
            this.optimized = next;
            this.retireAll();
            this.profileListeners.forEach(Runnable::run);
            this.flushFrees();
            this.failed.clear();
        }
        ++this.frame;
        GifAnimation.beginFrame(this.optimized);
    }

    public void endFrame() {
        if (!this.optimized) {
            return;
        }
        long bytes = this.cacheBytes();
        if (bytes <= 0x4000000L) {
            return;
        }
        ArrayList<Map.Entry<String, Long>> oldest = new ArrayList<Map.Entry<String, Long>>(this.lastUsed.entrySet());
        oldest.sort(Comparator.comparingLong(Map.Entry::getValue));
        for (Map.Entry entry : oldest) {
            if (bytes <= 0x4000000L) break;
            if ((Long)entry.getValue() == this.frame) continue;
            bytes -= this.retire((String)entry.getKey());
        }
    }

    public boolean optimizedImages() {
        return this.optimized;
    }

    public void onImageProfileChange(Runnable listener) {
        this.profileListeners.add(listener);
    }

    private void markUsed(String prefix, String fileName) {
        if (this.optimized) {
            this.lastUsed.put(prefix + fileName, this.frame);
        }
    }

    public void retireTexture(Texture texture) {
        if (texture != null) {
            this.pendingFrees.add(() -> GL11.glDeleteTextures((int)texture.id()));
        }
    }

    private void flushFrees() {
        if (this.pendingFrees.isEmpty()) {
            return;
        }
        ArrayList<Runnable> frees = new ArrayList<Runnable>(this.pendingFrees);
        this.pendingFrees.clear();
        frees.forEach(Runnable::run);
    }

    private void retireAll() {
        this.textures.values().forEach(this::retireTexture);
        this.previews.values().forEach(this::retireTexture);
        this.animations.values().forEach(a -> this.pendingFrees.add(a::free));
        this.posters.values().forEach(a -> this.pendingFrees.add(a::free));
        this.textures.clear();
        this.previews.clear();
        this.animations.clear();
        this.posters.clear();
        this.lastUsed.clear();
    }

    private static long textureBytes(Texture texture) {
        return texture == null ? 0L : (long)texture.width() * (long)texture.height() * 16L / 3L;
    }

    private long cacheBytes() {
        long total = 0L;
        for (Texture texture : this.textures.values()) {
            total += ThemeImages.textureBytes(texture);
        }
        for (Texture texture : this.previews.values()) {
            total += ThemeImages.textureBytes(texture);
        }
        for (GifAnimation animation : this.animations.values()) {
            total += animation.estimatedBytes();
        }
        for (GifAnimation animation : this.posters.values()) {
            total += animation.estimatedBytes();
        }
        return total;
    }

    private long retire(String key) {
        String name = key.substring(2);
        this.lastUsed.remove(key);
        if (key.startsWith("t:") || key.startsWith("p:")) {
            Texture texture = (key.startsWith("t:") ? this.textures : this.previews).remove(name);
            this.retireTexture(texture);
            return ThemeImages.textureBytes(texture);
        }
        GifAnimation animation = (key.startsWith("a:") ? this.animations : this.posters).remove(name);
        if (animation == null) {
            return 0L;
        }
        long bytes = animation.estimatedBytes();
        this.pendingFrees.add(animation::free);
        return bytes;
    }

    public static boolean isImage(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || ThemeImages.isGif(name);
    }

    public static boolean isGif(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).endsWith(".gif");
    }

    public List<String> files() {
        long now = System.currentTimeMillis();
        if (now - this.lastScan > 2000L) {
            this.lastScan = now;
            List<String> scanned = ThemeImages.scan();
            if (!scanned.equals(this.files)) {
                this.files = scanned;
                this.listingChanged = true;
            }
        }
        return this.files;
    }

    public Texture texture(String fileName) {
        if (fileName == null || fileName.isBlank() || this.failed.contains(fileName)) {
            return null;
        }
        if (ThemeImages.isGif(fileName)) {
            return this.frame(this.animations, fileName, false);
        }
        this.markUsed("t:", fileName);
        Texture texture = this.textures.get(fileName);
        if (texture == null) {
            texture = ThemeImages.upload(ThemeStore.backgroundsDir().resolve(fileName), this.optimized ? 2048 : 8192);
            if (texture == null) {
                this.failed.add(fileName);
            } else {
                this.textures.put(fileName, texture);
            }
        }
        return texture;
    }

    public Texture preview(String fileName) {
        if (fileName == null || fileName.isBlank() || this.failed.contains(fileName)) {
            return null;
        }
        if (!ThemeImages.isGif(fileName)) {
            if (!this.optimized) {
                return this.texture(fileName);
            }
            this.markUsed("p:", fileName);
            Texture texture = this.previews.get(fileName);
            if (texture == null) {
                texture = ThemeImages.upload(ThemeStore.backgroundsDir().resolve(fileName), 384);
                if (texture == null) {
                    this.failed.add(fileName);
                } else {
                    this.previews.put(fileName, texture);
                }
            }
            return texture;
        }
        GifAnimation playing = this.animations.get(fileName);
        if (playing != null) {
            this.markUsed("a:", fileName);
        }
        Texture frame = playing != null ? playing.current() : null;
        return frame != null ? frame : this.frame(this.posters, fileName, true);
    }

    public boolean isBroken(String fileName) {
        return this.failed.contains(fileName);
    }

    public String importFile(Path source) {
        if (source == null || !Files.isRegularFile(source, new LinkOption[0]) || !ThemeImages.isImage(source)) {
            return null;
        }
        try {
            Path dir = ThemeStore.backgroundsDir();
            Files.createDirectories(dir, new FileAttribute[0]);
            if (dir.toAbsolutePath().normalize().equals(source.toAbsolutePath().normalize().getParent())) {
                return source.getFileName().toString();
            }
            String name = ThemeImages.uniqueName(dir, source.getFileName().toString());
            Files.copy(source, dir.resolve(name), new CopyOption[0]);
            this.lastScan = 0L;
            return name;
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to import header image {}", (Object)source, (Object)e);
            return null;
        }
    }

    public boolean delete(String fileName) {
        GifAnimation poster;
        if (fileName == null || !this.files.contains(fileName)) {
            return false;
        }
        try {
            Files.deleteIfExists(ThemeStore.backgroundsDir().resolve(fileName));
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to delete header image {}", (Object)fileName, (Object)e);
            return false;
        }
        Texture texture = this.textures.remove(fileName);
        this.retireTexture(texture);
        this.retireTexture(this.previews.remove(fileName));
        GifAnimation playing = this.animations.remove(fileName);
        if (playing != null) {
            this.pendingFrees.add(playing::free);
        }
        if ((poster = this.posters.remove(fileName)) != null) {
            this.pendingFrees.add(poster::free);
        }
        for (String prefix : List.of("t:", "p:", "a:", "g:")) {
            this.lastUsed.remove(prefix + fileName);
        }
        this.failed.remove(fileName);
        ArrayList<Surface> surfaces = new ArrayList<Surface>(ThemeStore.get().surfaces());
        surfaces.addAll(HudTheme.get().surfaces());
        for (Surface surface : surfaces) {
            if (!fileName.equals(surface.image.get())) continue;
            surface.image.set("");
            if (!surface.isImage()) continue;
            surface.background.set("None");
        }
        this.lastScan = 0L;
        return true;
    }

    public void browseAsync() {
        if (this.dialogOpen) {
            return;
        }
        this.dialogOpen = true;
        Thread thread = new Thread(() -> {
            try (MemoryStack stack = MemoryStack.stackPush();){
                PointerBuffer filters = stack.mallocPointer(4);
                filters.put(stack.UTF8((CharSequence)"*.png")).put(stack.UTF8((CharSequence)"*.jpg")).put(stack.UTF8((CharSequence)"*.jpeg")).put(stack.UTF8((CharSequence)"*.gif")).flip();
                String result = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Choose a header image", null, (PointerBuffer)filters, (CharSequence)"Images (png, jpg, gif)", (boolean)false);
                if (result != null) {
                    this.pendingImport = Path.of(result, new String[0]);
                }
            }
            catch (Throwable t) {
                LOGGER.warn("[Anubis] File dialog failed", t);
            }
            finally {
                this.dialogOpen = false;
            }
        }, "Anubis theme file dialog");
        thread.setDaemon(true);
        thread.start();
    }

    public Path takePendingImport() {
        Path path = this.pendingImport;
        this.pendingImport = null;
        return path;
    }

    public void openFolder() {
        this.browseAsync();
    }

    private Texture frame(Map<String, GifAnimation> cache, String fileName, boolean poster) {
        this.markUsed(poster ? "g:" : "a:", fileName);
        GifAnimation animation = cache.computeIfAbsent(fileName, name -> GifAnimation.load(ThemeStore.backgroundsDir().resolve((String)name), poster, this.optimized));
        Texture texture = animation.current();
        if (texture == null && animation.failed()) {
            cache.remove(fileName);
            this.lastUsed.remove((poster ? "g:" : "a:") + fileName);
            this.failed.add(fileName);
        }
        return texture;
    }

    private void sweep() {
        HashSet<String> shown = new HashSet<String>();
        for (Surface surface : ThemeStore.get().surfaces()) {
            if (!surface.isImage()) continue;
            shown.add(surface.image.get());
        }
        for (Surface surface : HudTheme.get().surfaces()) {
            if (!surface.isImage()) continue;
            shown.add(surface.image.get());
        }
        this.freeAllBut(this.animations, shown, "a:");
        if (!this.listingChanged) {
            return;
        }
        this.listingChanged = false;
        HashSet<String> present = new HashSet<String>(this.files);
        this.freeAllBut(this.animations, present, "a:");
        this.freeAllBut(this.posters, present, "g:");
        for (String name : new ArrayList<String>(this.textures.keySet())) {
            if (present.contains(name)) continue;
            this.retire("t:" + name);
        }
        for (String name : new ArrayList<String>(this.previews.keySet())) {
            if (present.contains(name)) continue;
            this.retire("p:" + name);
        }
        this.failed.retainAll(present);
    }

    private void freeAllBut(Map<String, GifAnimation> cache, Set<String> keep, String prefix) {
        cache.entrySet().removeIf(entry -> {
            if (keep.contains(entry.getKey())) {
                return false;
            }
            this.pendingFrees.add(((GifAnimation)entry.getValue())::free);
            this.lastUsed.remove(prefix + (String)entry.getKey());
            return true;
        });
    }

    private static List<String> scan() {
        List<String> list;
        block9: {
            Path dir = ThemeStore.backgroundsDir();
            if (!Files.isDirectory(dir, new LinkOption[0])) {
                return List.of();
            }
            Stream<Path> stream = Files.list(dir);
            try {
                list = stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(ThemeImages::isImage).map(path -> path.getFileName().toString()).sorted(String.CASE_INSENSITIVE_ORDER).toList();
                if (stream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return List.of();
                }
            }
            stream.close();
        }
        return list;
    }

    private static String uniqueName(Path dir, String original) {
        String name = original.replaceAll("[^A-Za-z0-9._-]", "_");
        int dot = name.lastIndexOf(46);
        String base = dot > 0 ? name.substring(0, dot) : name;
        String ext = dot > 0 ? name.substring(dot) : "";
        Object candidate = name;
        int i = 2;
        while (Files.exists(dir.resolve((String)candidate), new LinkOption[0])) {
            candidate = base + "-" + i + ext;
            ++i;
        }
        return candidate;
    }

    private static Texture upload(Path path, int maxSide) {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return null;
        }
        try {
            return ThemeImages.decode(Files.readAllBytes(path), path.getFileName().toString(), false, maxSide);
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis] Failed to load {}", (Object)path.getFileName(), (Object)e);
            return null;
        }
    }

    static Texture decode(byte[] bytes, String name, boolean pixelated) {
        return ThemeImages.decode(bytes, name, pixelated, 8192);
    }

    /*
     * Exception decompiling
     */
    private static Texture decode(byte[] bytes, String name, boolean pixelated, int maxSide) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [6[TRYBLOCK]], but top level block is 17[DOLOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
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

    public static Texture fromPixels(ByteBuffer rgba, int width, int height, boolean pixelated) {
        return new Texture(ThemeImages.createTexture(rgba, width, height, pixelated), width, height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int createTexture(ByteBuffer pixels, int width, int height, boolean pixelated) {
        int prevTexture = GL11.glGetInteger((int)32873);
        int prevUnpackBuffer = GL11.glGetInteger((int)35055);
        int prevAlignment = GL11.glGetInteger((int)3317);
        int prevRowLength = GL11.glGetInteger((int)3314);
        int prevSkipPixels = GL11.glGetInteger((int)3316);
        int prevSkipRows = GL11.glGetInteger((int)3315);
        try {
            GL15.glBindBuffer((int)35052, (int)0);
            GL11.glPixelStorei((int)3317, (int)4);
            GL11.glPixelStorei((int)3314, (int)0);
            GL11.glPixelStorei((int)3316, (int)0);
            GL11.glPixelStorei((int)3315, (int)0);
            int id = GL11.glGenTextures();
            GL11.glBindTexture((int)3553, (int)id);
            GL11.glTexParameteri((int)3553, (int)10241, (int)(pixelated ? 9728 : 9987));
            GL11.glTexParameteri((int)3553, (int)10240, (int)(pixelated ? 9728 : 9729));
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)width, (int)height, (int)0, (int)6408, (int)5121, (ByteBuffer)pixels);
            if (!pixelated) {
                GL30.glGenerateMipmap((int)3553);
            }
            int n = id;
            return n;
        }
        finally {
            GL11.glBindTexture((int)3553, (int)prevTexture);
            GL15.glBindBuffer((int)35052, (int)prevUnpackBuffer);
            GL11.glPixelStorei((int)3317, (int)prevAlignment);
            GL11.glPixelStorei((int)3314, (int)prevRowLength);
            GL11.glPixelStorei((int)3316, (int)prevSkipPixels);
            GL11.glPixelStorei((int)3315, (int)prevSkipRows);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Texture(int id, int width, int height) {
    }
}

