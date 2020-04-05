// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.nio.ByteBuffer;
import java.nio.Buffer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class ScreenUtils
{
    public static TextureRegion getFrameBufferTexture() {
        final int w = Gdx.graphics.getBackBufferWidth();
        final int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferTexture(0, 0, w, h);
    }
    
    public static TextureRegion getFrameBufferTexture(final int x, final int y, final int w, final int h) {
        final int potW = MathUtils.nextPowerOfTwo(w);
        final int potH = MathUtils.nextPowerOfTwo(h);
        final Pixmap pixmap = getFrameBufferPixmap(x, y, w, h);
        final Pixmap potPixmap = new Pixmap(potW, potH, Pixmap.Format.RGBA8888);
        potPixmap.drawPixmap(pixmap, 0, 0);
        final Texture texture = new Texture(potPixmap);
        final TextureRegion textureRegion = new TextureRegion(texture, 0, h, w, -h);
        potPixmap.dispose();
        pixmap.dispose();
        return textureRegion;
    }
    
    public static Pixmap getFrameBufferPixmap(final int x, final int y, final int w, final int h) {
        Gdx.gl.glPixelStorei(3333, 1);
        final Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        final ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, 6408, 5121, pixels);
        return pixmap;
    }
    
    public static byte[] getFrameBufferPixels(final boolean flipY) {
        final int w = Gdx.graphics.getBackBufferWidth();
        final int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferPixels(0, 0, w, h, flipY);
    }
    
    public static byte[] getFrameBufferPixels(final int x, final int y, final int w, final int h, final boolean flipY) {
        Gdx.gl.glPixelStorei(3333, 1);
        final ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 4);
        Gdx.gl.glReadPixels(x, y, w, h, 6408, 5121, pixels);
        final int numBytes = w * h * 4;
        final byte[] lines = new byte[numBytes];
        if (flipY) {
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; ++i) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
        }
        else {
            pixels.clear();
            pixels.get(lines);
        }
        return lines;
    }
}
