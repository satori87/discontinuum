// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class MipMapGenerator
{
    private static boolean useHWMipMap;
    
    static {
        MipMapGenerator.useHWMipMap = true;
    }
    
    private MipMapGenerator() {
    }
    
    public static void setUseHardwareMipMap(final boolean useHWMipMap) {
        MipMapGenerator.useHWMipMap = useHWMipMap;
    }
    
    public static void generateMipMap(final Pixmap pixmap, final int textureWidth, final int textureHeight) {
        generateMipMap(3553, pixmap, textureWidth, textureHeight);
    }
    
    public static void generateMipMap(final int target, final Pixmap pixmap, final int textureWidth, final int textureHeight) {
        if (!MipMapGenerator.useHWMipMap) {
            generateMipMapCPU(target, pixmap, textureWidth, textureHeight);
            return;
        }
        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.WebGL || Gdx.app.getType() == Application.ApplicationType.iOS) {
            generateMipMapGLES20(target, pixmap);
        }
        else {
            generateMipMapDesktop(target, pixmap, textureWidth, textureHeight);
        }
    }
    
    private static void generateMipMapGLES20(final int target, final Pixmap pixmap) {
        Gdx.gl.glTexImage2D(target, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        Gdx.gl20.glGenerateMipmap(target);
    }
    
    private static void generateMipMapDesktop(final int target, final Pixmap pixmap, final int textureWidth, final int textureHeight) {
        if (Gdx.graphics.supportsExtension("GL_ARB_framebuffer_object") || Gdx.graphics.supportsExtension("GL_EXT_framebuffer_object") || Gdx.gl30 != null) {
            Gdx.gl.glTexImage2D(target, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
            Gdx.gl20.glGenerateMipmap(target);
        }
        else {
            generateMipMapCPU(target, pixmap, textureWidth, textureHeight);
        }
    }
    
    private static void generateMipMapCPU(final int target, Pixmap pixmap, final int textureWidth, final int textureHeight) {
        Gdx.gl.glTexImage2D(target, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        if (Gdx.gl20 == null && textureWidth != textureHeight) {
            throw new GdxRuntimeException("texture width and height must be square when using mipmapping.");
        }
        for (int width = pixmap.getWidth() / 2, height = pixmap.getHeight() / 2, level = 1; width > 0 && height > 0; width = pixmap.getWidth() / 2, height = pixmap.getHeight() / 2, ++level) {
            final Pixmap tmp = new Pixmap(width, height, pixmap.getFormat());
            tmp.setBlending(Pixmap.Blending.None);
            tmp.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, width, height);
            if (level > 1) {
                pixmap.dispose();
            }
            pixmap = tmp;
            Gdx.gl.glTexImage2D(target, level, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        }
    }
}
