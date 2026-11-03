/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  imgui.ImFontAtlas
 *  imgui.ImGui
 *  imgui.gl3.ImGuiImplGl3
 *  imgui.type.ImInt
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL15
 */
package imgui.gl3;

import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.type.ImInt;
import java.nio.ByteBuffer;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

@Environment(value=EnvType.CLIENT)
public class AnubisGl3
extends ImGuiImplGl3 {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean createFontsTexture() {
        boolean success;
        int texture;
        int oldFont;
        int oldSkipPixels;
        int oldSkipRows;
        int oldRowLength;
        int oldAlignment;
        int oldUnpackBuffer;
        int oldTexture;
        int oldActive;
        block7: {
            ImFontAtlas atlas = ImGui.getIO().getFonts();
            ImInt width = new ImInt();
            ImInt height = new ImInt();
            ByteBuffer pixels = atlas.getTexDataAsRGBA32(width, height);
            int maximum = GL11.glGetInteger((int)3379);
            long byteCount = (long)width.get() * (long)height.get() * 4L;
            if (width.get() <= 0 || height.get() <= 0 || width.get() > maximum || height.get() > maximum || pixels == null || byteCount > (long)pixels.capacity() || byteCount > Integer.MAX_VALUE) {
                throw new IllegalStateException("[Anubis ImGui] Invalid font atlas " + width.get() + "x" + height.get() + "; GL_MAX_TEXTURE_SIZE=" + maximum);
            }
            ByteBuffer upload = pixels.duplicate();
            upload.position(0);
            upload.limit((int)byteCount);
            oldActive = GL11.glGetInteger((int)34016);
            GL13.glActiveTexture((int)33984);
            oldTexture = GL11.glGetInteger((int)32873);
            oldUnpackBuffer = GL11.glGetInteger((int)35055);
            oldAlignment = GL11.glGetInteger((int)3317);
            oldRowLength = GL11.glGetInteger((int)3314);
            oldSkipRows = GL11.glGetInteger((int)3315);
            oldSkipPixels = GL11.glGetInteger((int)3316);
            oldFont = this.data.fontTexture;
            texture = 0;
            success = false;
            try {
                AnubisGl3.reportExistingErrors();
                GL15.glBindBuffer((int)35052, (int)0);
                GL11.glPixelStorei((int)3317, (int)1);
                GL11.glPixelStorei((int)3314, (int)0);
                GL11.glPixelStorei((int)3315, (int)0);
                GL11.glPixelStorei((int)3316, (int)0);
                texture = GL11.glGenTextures();
                GL11.glBindTexture((int)3553, (int)texture);
                GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
                GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
                GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
                GL11.glTexParameteri((int)3553, (int)33084, (int)0);
                GL11.glTexParameteri((int)3553, (int)33085, (int)0);
                GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)width.get(), (int)height.get(), (int)0, (int)6408, (int)5121, (ByteBuffer)upload);
                int error = GL11.glGetError();
                if (texture == 0 || error != 0) {
                    throw new IllegalStateException("[Anubis ImGui] Font upload failed: " + width.get() + "x" + height.get() + ", GL error=0x" + Integer.toHexString(error) + ", renderer=" + GL11.glGetString((int)7937));
                }
                this.data.fontTexture = texture;
                atlas.setTexID((long)texture);
                success = true;
                System.err.printf(Locale.ROOT, "[Anubis ImGui] Font atlas uploaded: %dx%d RGBA8, max=%d, previousUnpackBuffer=%d, GL=%s, renderer=%s%n", width.get(), height.get(), maximum, oldUnpackBuffer, GL11.glGetString((int)7938), GL11.glGetString((int)7937));
                if (success || texture == 0) break block7;
            }
            catch (Throwable throwable) {
                if (!success && texture != 0) {
                    GL11.glDeleteTextures((int)texture);
                }
                int restoredTexture = success && oldFont != 0 && oldTexture == oldFont ? texture : oldTexture;
                GL11.glBindTexture((int)3553, (int)restoredTexture);
                GL15.glBindBuffer((int)35052, (int)oldUnpackBuffer);
                GL11.glPixelStorei((int)3317, (int)oldAlignment);
                GL11.glPixelStorei((int)3314, (int)oldRowLength);
                GL11.glPixelStorei((int)3315, (int)oldSkipRows);
                GL11.glPixelStorei((int)3316, (int)oldSkipPixels);
                GL13.glActiveTexture((int)oldActive);
                if (success && oldFont != 0 && oldFont != texture) {
                    GL11.glDeleteTextures((int)oldFont);
                }
                throw throwable;
            }
            GL11.glDeleteTextures((int)texture);
        }
        int restoredTexture = success && oldFont != 0 && oldTexture == oldFont ? texture : oldTexture;
        GL11.glBindTexture((int)3553, (int)restoredTexture);
        GL15.glBindBuffer((int)35052, (int)oldUnpackBuffer);
        GL11.glPixelStorei((int)3317, (int)oldAlignment);
        GL11.glPixelStorei((int)3314, (int)oldRowLength);
        GL11.glPixelStorei((int)3315, (int)oldSkipRows);
        GL11.glPixelStorei((int)3316, (int)oldSkipPixels);
        GL13.glActiveTexture((int)oldActive);
        if (success && oldFont != 0 && oldFont != texture) {
            GL11.glDeleteTextures((int)oldFont);
        }
        return true;
    }

    private static void reportExistingErrors() {
        for (int i = 0; i < 16; ++i) {
            int error = GL11.glGetError();
            if (error == 0) {
                return;
            }
            System.err.println("[Anubis ImGui] GL error predating font upload: 0x" + Integer.toHexString(error));
        }
    }
}

