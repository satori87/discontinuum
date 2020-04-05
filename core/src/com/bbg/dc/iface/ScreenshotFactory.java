// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.iface;

import java.nio.ByteBuffer;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class ScreenshotFactory
{
    public static void saveTexture(final String s, final int x, final int y, final int w, final int h, final Texture tex) {
        try {
            final FileHandle fh = new FileHandle(String.valueOf(s) + ".png");
            final Pixmap pixmap = getPixmapRegion(x, y, w, h, false, tex);
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    private static Pixmap getPixmapRegion(final int x, final int y, final int w, final int h, final boolean yDown, final Texture tex) {
        final TextureData td = tex.getTextureData();
        if (!td.isPrepared()) {
            td.prepare();
        }
        final Pixmap pm = td.consumePixmap();
        final Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(pm, x, y, w, h, 0, 0, w, h);
        if (yDown) {
            final ByteBuffer pixels = pixmap.getPixels();
            final int numBytes = w * h * 4;
            final byte[] lines = new byte[numBytes];
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; ++i) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        }
        return pixmap;
    }
}
