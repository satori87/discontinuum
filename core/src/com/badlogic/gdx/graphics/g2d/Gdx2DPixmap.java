// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import com.badlogic.gdx.utils.Disposable;

public class Gdx2DPixmap implements Disposable
{
    public static final int GDX2D_FORMAT_ALPHA = 1;
    public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
    public static final int GDX2D_FORMAT_RGB888 = 3;
    public static final int GDX2D_FORMAT_RGBA8888 = 4;
    public static final int GDX2D_FORMAT_RGB565 = 5;
    public static final int GDX2D_FORMAT_RGBA4444 = 6;
    public static final int GDX2D_SCALE_NEAREST = 0;
    public static final int GDX2D_SCALE_LINEAR = 1;
    public static final int GDX2D_BLEND_NONE = 0;
    public static final int GDX2D_BLEND_SRC_OVER = 1;
    long basePtr;
    int width;
    int height;
    int format;
    ByteBuffer pixelPtr;
    long[] nativeData;
    
    public static int toGlFormat(final int format) {
        switch (format) {
            case 1: {
                return 6406;
            }
            case 2: {
                return 6410;
            }
            case 3:
            case 5: {
                return 6407;
            }
            case 4:
            case 6: {
                return 6408;
            }
            default: {
                throw new GdxRuntimeException("unknown format: " + format);
            }
        }
    }
    
    public static int toGlType(final int format) {
        switch (format) {
            case 1:
            case 2:
            case 3:
            case 4: {
                return 5121;
            }
            case 5: {
                return 33635;
            }
            case 6: {
                return 32819;
            }
            default: {
                throw new GdxRuntimeException("unknown format: " + format);
            }
        }
    }
    
    public Gdx2DPixmap(final byte[] encodedData, final int offset, final int len, final int requestedFormat) throws IOException {
        this.nativeData = new long[4];
        this.pixelPtr = load(this.nativeData, encodedData, offset, len);
        if (this.pixelPtr == null) {
            throw new IOException("Error loading pixmap: " + getFailureReason());
        }
        this.basePtr = this.nativeData[0];
        this.width = (int)this.nativeData[1];
        this.height = (int)this.nativeData[2];
        this.format = (int)this.nativeData[3];
        if (requestedFormat != 0 && requestedFormat != this.format) {
            this.convert(requestedFormat);
        }
    }
    
    public Gdx2DPixmap(final InputStream in, final int requestedFormat) throws IOException {
        this.nativeData = new long[4];
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int readBytes = 0;
        while ((readBytes = in.read(buffer)) != -1) {
            bytes.write(buffer, 0, readBytes);
        }
        buffer = bytes.toByteArray();
        this.pixelPtr = load(this.nativeData, buffer, 0, buffer.length);
        if (this.pixelPtr == null) {
            throw new IOException("Error loading pixmap: " + getFailureReason());
        }
        this.basePtr = this.nativeData[0];
        this.width = (int)this.nativeData[1];
        this.height = (int)this.nativeData[2];
        this.format = (int)this.nativeData[3];
        if (requestedFormat != 0 && requestedFormat != this.format) {
            this.convert(requestedFormat);
        }
    }
    
    public Gdx2DPixmap(final int width, final int height, final int format) throws GdxRuntimeException {
        this.nativeData = new long[4];
        this.pixelPtr = newPixmap(this.nativeData, width, height, format);
        if (this.pixelPtr == null) {
            throw new GdxRuntimeException("Error loading pixmap.");
        }
        this.basePtr = this.nativeData[0];
        this.width = (int)this.nativeData[1];
        this.height = (int)this.nativeData[2];
        this.format = (int)this.nativeData[3];
    }
    
    public Gdx2DPixmap(final ByteBuffer pixelPtr, final long[] nativeData) {
        this.nativeData = new long[4];
        this.pixelPtr = pixelPtr;
        this.basePtr = nativeData[0];
        this.width = (int)nativeData[1];
        this.height = (int)nativeData[2];
        this.format = (int)nativeData[3];
    }
    
    private void convert(final int requestedFormat) {
        final Gdx2DPixmap pixmap = new Gdx2DPixmap(this.width, this.height, requestedFormat);
        pixmap.drawPixmap(this, 0, 0, 0, 0, this.width, this.height);
        this.dispose();
        this.basePtr = pixmap.basePtr;
        this.format = pixmap.format;
        this.height = pixmap.height;
        this.nativeData = pixmap.nativeData;
        this.pixelPtr = pixmap.pixelPtr;
        this.width = pixmap.width;
    }
    
    @Override
    public void dispose() {
        free(this.basePtr);
    }
    
    public void clear(final int color) {
        clear(this.basePtr, color);
    }
    
    public void setPixel(final int x, final int y, final int color) {
        setPixel(this.basePtr, x, y, color);
    }
    
    public int getPixel(final int x, final int y) {
        return getPixel(this.basePtr, x, y);
    }
    
    public void drawLine(final int x, final int y, final int x2, final int y2, final int color) {
        drawLine(this.basePtr, x, y, x2, y2, color);
    }
    
    public void drawRect(final int x, final int y, final int width, final int height, final int color) {
        drawRect(this.basePtr, x, y, width, height, color);
    }
    
    public void drawCircle(final int x, final int y, final int radius, final int color) {
        drawCircle(this.basePtr, x, y, radius, color);
    }
    
    public void fillRect(final int x, final int y, final int width, final int height, final int color) {
        fillRect(this.basePtr, x, y, width, height, color);
    }
    
    public void fillCircle(final int x, final int y, final int radius, final int color) {
        fillCircle(this.basePtr, x, y, radius, color);
    }
    
    public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3, final int color) {
        fillTriangle(this.basePtr, x1, y1, x2, y2, x3, y3, color);
    }
    
    public void drawPixmap(final Gdx2DPixmap src, final int srcX, final int srcY, final int dstX, final int dstY, final int width, final int height) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
    }
    
    public void drawPixmap(final Gdx2DPixmap src, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final int dstX, final int dstY, final int dstWidth, final int dstHeight) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }
    
    public void setBlend(final int blend) {
        setBlend(this.basePtr, blend);
    }
    
    public void setScale(final int scale) {
        setScale(this.basePtr, scale);
    }
    
    public static Gdx2DPixmap newPixmap(final InputStream in, final int requestedFormat) {
        try {
            return new Gdx2DPixmap(in, requestedFormat);
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public static Gdx2DPixmap newPixmap(final int width, final int height, final int format) {
        try {
            return new Gdx2DPixmap(width, height, format);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public ByteBuffer getPixels() {
        return this.pixelPtr;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getFormat() {
        return this.format;
    }
    
    public int getGLInternalFormat() {
        return toGlFormat(this.format);
    }
    
    public int getGLFormat() {
        return this.getGLInternalFormat();
    }
    
    public int getGLType() {
        return toGlType(this.format);
    }
    
    public String getFormatString() {
        switch (this.format) {
            case 1: {
                return "alpha";
            }
            case 2: {
                return "luminance alpha";
            }
            case 3: {
                return "rgb888";
            }
            case 4: {
                return "rgba8888";
            }
            case 5: {
                return "rgb565";
            }
            case 6: {
                return "rgba4444";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    private static native ByteBuffer load(final long[] p0, final byte[] p1, final int p2, final int p3);
    
    private static native ByteBuffer newPixmap(final long[] p0, final int p1, final int p2, final int p3);
    
    private static native void free(final long p0);
    
    private static native void clear(final long p0, final int p1);
    
    private static native void setPixel(final long p0, final int p1, final int p2, final int p3);
    
    private static native int getPixel(final long p0, final int p1, final int p2);
    
    private static native void drawLine(final long p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    private static native void drawRect(final long p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    private static native void drawCircle(final long p0, final int p1, final int p2, final int p3, final int p4);
    
    private static native void fillRect(final long p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    private static native void fillCircle(final long p0, final int p1, final int p2, final int p3, final int p4);
    
    private static native void fillTriangle(final long p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    private static native void drawPixmap(final long p0, final long p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    private static native void setBlend(final long p0, final int p1);
    
    private static native void setScale(final long p0, final int p1);
    
    public static native String getFailureReason();
}
