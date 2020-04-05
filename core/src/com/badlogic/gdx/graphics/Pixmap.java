// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.nio.ByteBuffer;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Disposable;

public class Pixmap implements Disposable
{
    private Blending blending;
    private Filter filter;
    final Gdx2DPixmap pixmap;
    int color;
    private boolean disposed;
    
    public void setBlending(final Blending blending) {
        this.blending = blending;
        this.pixmap.setBlend((blending != Blending.None) ? 1 : 0);
    }
    
    public void setFilter(final Filter filter) {
        this.filter = filter;
        this.pixmap.setScale((filter != Filter.NearestNeighbour) ? 1 : 0);
    }
    
    public Pixmap(final int width, final int height, final Format format) {
        this.blending = Blending.SourceOver;
        this.filter = Filter.BiLinear;
        this.color = 0;
        this.pixmap = new Gdx2DPixmap(width, height, Format.toGdx2DPixmapFormat(format));
        this.setColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.fill();
    }
    
    public Pixmap(final byte[] encodedData, final int offset, final int len) {
        this.blending = Blending.SourceOver;
        this.filter = Filter.BiLinear;
        this.color = 0;
        try {
            this.pixmap = new Gdx2DPixmap(encodedData, offset, len, 0);
        }
        catch (IOException e) {
            throw new GdxRuntimeException("Couldn't load pixmap from image data", e);
        }
    }
    
    public Pixmap(final FileHandle file) {
        this.blending = Blending.SourceOver;
        this.filter = Filter.BiLinear;
        this.color = 0;
        try {
            final byte[] bytes = file.readBytes();
            this.pixmap = new Gdx2DPixmap(bytes, 0, bytes.length, 0);
        }
        catch (Exception e) {
            throw new GdxRuntimeException("Couldn't load file: " + file, e);
        }
    }
    
    public Pixmap(final Gdx2DPixmap pixmap) {
        this.blending = Blending.SourceOver;
        this.filter = Filter.BiLinear;
        this.color = 0;
        this.pixmap = pixmap;
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color = Color.rgba8888(r, g, b, a);
    }
    
    public void setColor(final Color color) {
        this.color = Color.rgba8888(color.r, color.g, color.b, color.a);
    }
    
    public void fill() {
        this.pixmap.clear(this.color);
    }
    
    public void drawLine(final int x, final int y, final int x2, final int y2) {
        this.pixmap.drawLine(x, y, x2, y2, this.color);
    }
    
    public void drawRectangle(final int x, final int y, final int width, final int height) {
        this.pixmap.drawRect(x, y, width, height, this.color);
    }
    
    public void drawPixmap(final Pixmap pixmap, final int x, final int y) {
        this.drawPixmap(pixmap, x, y, 0, 0, pixmap.getWidth(), pixmap.getHeight());
    }
    
    public void drawPixmap(final Pixmap pixmap, final int x, final int y, final int srcx, final int srcy, final int srcWidth, final int srcHeight) {
        this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, x, y, srcWidth, srcHeight);
    }
    
    public void drawPixmap(final Pixmap pixmap, final int srcx, final int srcy, final int srcWidth, final int srcHeight, final int dstx, final int dsty, final int dstWidth, final int dstHeight) {
        this.pixmap.drawPixmap(pixmap.pixmap, srcx, srcy, srcWidth, srcHeight, dstx, dsty, dstWidth, dstHeight);
    }
    
    public void fillRectangle(final int x, final int y, final int width, final int height) {
        this.pixmap.fillRect(x, y, width, height, this.color);
    }
    
    public void drawCircle(final int x, final int y, final int radius) {
        this.pixmap.drawCircle(x, y, radius, this.color);
    }
    
    public void fillCircle(final int x, final int y, final int radius) {
        this.pixmap.fillCircle(x, y, radius, this.color);
    }
    
    public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
        this.pixmap.fillTriangle(x1, y1, x2, y2, x3, y3, this.color);
    }
    
    public int getPixel(final int x, final int y) {
        return this.pixmap.getPixel(x, y);
    }
    
    public int getWidth() {
        return this.pixmap.getWidth();
    }
    
    public int getHeight() {
        return this.pixmap.getHeight();
    }
    
    @Override
    public void dispose() {
        if (this.disposed) {
            throw new GdxRuntimeException("Pixmap already disposed!");
        }
        this.pixmap.dispose();
        this.disposed = true;
    }
    
    public boolean isDisposed() {
        return this.disposed;
    }
    
    public void drawPixel(final int x, final int y) {
        this.pixmap.setPixel(x, y, this.color);
    }
    
    public void drawPixel(final int x, final int y, final int color) {
        this.pixmap.setPixel(x, y, color);
    }
    
    public int getGLFormat() {
        return this.pixmap.getGLFormat();
    }
    
    public int getGLInternalFormat() {
        return this.pixmap.getGLInternalFormat();
    }
    
    public int getGLType() {
        return this.pixmap.getGLType();
    }
    
    public ByteBuffer getPixels() {
        if (this.disposed) {
            throw new GdxRuntimeException("Pixmap already disposed");
        }
        return this.pixmap.getPixels();
    }
    
    public Format getFormat() {
        return Format.fromGdx2DPixmapFormat(this.pixmap.getFormat());
    }
    
    public Blending getBlending() {
        return this.blending;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public enum Blending
    {
        None("None", 0), 
        SourceOver("SourceOver", 1);
        
        private Blending(final String name, final int ordinal) {
        }
    }
    
    public enum Filter
    {
        NearestNeighbour("NearestNeighbour", 0), 
        BiLinear("BiLinear", 1);
        
        private Filter(final String name, final int ordinal) {
        }
    }
    
    public enum Format
    {
        Alpha("Alpha", 0), 
        Intensity("Intensity", 1), 
        LuminanceAlpha("LuminanceAlpha", 2), 
        RGB565("RGB565", 3), 
        RGBA4444("RGBA4444", 4), 
        RGB888("RGB888", 5), 
        RGBA8888("RGBA8888", 6);
        
        private Format(final String name, final int ordinal) {
        }
        
        public static int toGdx2DPixmapFormat(final Format format) {
            if (format == Format.Alpha) {
                return 1;
            }
            if (format == Format.Intensity) {
                return 1;
            }
            if (format == Format.LuminanceAlpha) {
                return 2;
            }
            if (format == Format.RGB565) {
                return 5;
            }
            if (format == Format.RGBA4444) {
                return 6;
            }
            if (format == Format.RGB888) {
                return 3;
            }
            if (format == Format.RGBA8888) {
                return 4;
            }
            throw new GdxRuntimeException("Unknown Format: " + format);
        }
        
        public static Format fromGdx2DPixmapFormat(final int format) {
            if (format == 1) {
                return Format.Alpha;
            }
            if (format == 2) {
                return Format.LuminanceAlpha;
            }
            if (format == 5) {
                return Format.RGB565;
            }
            if (format == 6) {
                return Format.RGBA4444;
            }
            if (format == 3) {
                return Format.RGB888;
            }
            if (format == 4) {
                return Format.RGBA8888;
            }
            throw new GdxRuntimeException("Unknown Gdx2DPixmap Format: " + format);
        }
        
        public static int toGlFormat(final Format format) {
            return Gdx2DPixmap.toGlFormat(toGdx2DPixmapFormat(format));
        }
        
        public static int toGlType(final Format format) {
            return Gdx2DPixmap.toGlType(toGdx2DPixmapFormat(format));
        }
    }
}
