/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.anubis.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class WindowIcon {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"Anubis");
    private static final int[] SIZES = new int[]{16, 32, 48, 64, 128, 256};

    private WindowIcon() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void apply(long window) {
        int platform = GLFW.glfwGetPlatform();
        if (platform != 393217 && platform != 393220) {
            return;
        }
        ArrayList<ByteBuffer> pixels = new ArrayList<ByteBuffer>(SIZES.length);
        try (MemoryStack stack = MemoryStack.stackPush();){
            GLFWImage.Buffer images = GLFWImage.malloc((int)SIZES.length, (MemoryStack)stack);
            int count = 0;
            for (int size : SIZES) {
                ByteBuffer image = WindowIcon.load("/assets/anubis/icons/icon_" + size + ".png", stack);
                if (image == null) continue;
                pixels.add(image);
                images.position(count++);
                images.width(size);
                images.height(size);
                images.pixels(image);
            }
            if (count == 0) {
                return;
            }
            images.position(0);
            images.limit(count);
            GLFW.glfwSetWindowIcon((long)window, (GLFWImage.Buffer)images);
        }
        finally {
            pixels.forEach(STBImage::stbi_image_free);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    private static ByteBuffer load(String resource, MemoryStack stack) {
        try (InputStream in = WindowIcon.class.getResourceAsStream(resource);){
            ByteBuffer byteBuffer;
            if (in == null) {
                ByteBuffer byteBuffer2 = null;
                return byteBuffer2;
            }
            byte[] bytes = in.readAllBytes();
            ByteBuffer file = MemoryUtil.memAlloc((int)bytes.length);
            try {
                file.put(bytes).flip();
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                byteBuffer = STBImage.stbi_load_from_memory((ByteBuffer)file, (IntBuffer)w, (IntBuffer)h, (IntBuffer)channels, (int)4);
            }
            catch (Throwable throwable) {
                MemoryUtil.memFree((Buffer)file);
                throw throwable;
            }
            MemoryUtil.memFree((Buffer)file);
            return byteBuffer;
        }
        catch (IOException | RuntimeException e) {
            LOGGER.warn("[Anubis+] Failed to load window icon {}", (Object)resource, (Object)e);
            return null;
        }
    }
}

