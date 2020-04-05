// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL20;

public interface Graphics
{
    boolean isGL30Available();
    
    GL20 getGL20();
    
    GL30 getGL30();
    
    void setGL20(final GL20 p0);
    
    void setGL30(final GL30 p0);
    
    int getWidth();
    
    int getHeight();
    
    int getBackBufferWidth();
    
    int getBackBufferHeight();
    
    long getFrameId();
    
    float getDeltaTime();
    
    float getRawDeltaTime();
    
    int getFramesPerSecond();
    
    GraphicsType getType();
    
    GLVersion getGLVersion();
    
    float getPpiX();
    
    float getPpiY();
    
    float getPpcX();
    
    float getPpcY();
    
    float getDensity();
    
    boolean supportsDisplayModeChange();
    
    Monitor getPrimaryMonitor();
    
    Monitor getMonitor();
    
    Monitor[] getMonitors();
    
    DisplayMode[] getDisplayModes();
    
    DisplayMode[] getDisplayModes(final Monitor p0);
    
    DisplayMode getDisplayMode();
    
    DisplayMode getDisplayMode(final Monitor p0);
    
    boolean setFullscreenMode(final DisplayMode p0);
    
    boolean setWindowedMode(final int p0, final int p1);
    
    void setTitle(final String p0);
    
    void setUndecorated(final boolean p0);
    
    void setResizable(final boolean p0);
    
    void setVSync(final boolean p0);
    
    BufferFormat getBufferFormat();
    
    boolean supportsExtension(final String p0);
    
    void setContinuousRendering(final boolean p0);
    
    boolean isContinuousRendering();
    
    void requestRendering();
    
    boolean isFullscreen();
    
    Cursor newCursor(final Pixmap p0, final int p1, final int p2);
    
    void setCursor(final Cursor p0);
    
    void setSystemCursor(final Cursor.SystemCursor p0);
    
    public enum GraphicsType
    {
        AndroidGL("AndroidGL", 0), 
        LWJGL("LWJGL", 1), 
        WebGL("WebGL", 2), 
        iOSGL("iOSGL", 3), 
        JGLFW("JGLFW", 4), 
        Mock("Mock", 5), 
        LWJGL3("LWJGL3", 6);
        
        private GraphicsType(final String name, final int ordinal) {
        }
    }
    
    public static class DisplayMode
    {
        public final int width;
        public final int height;
        public final int refreshRate;
        public final int bitsPerPixel;
        
        protected DisplayMode(final int width, final int height, final int refreshRate, final int bitsPerPixel) {
            this.width = width;
            this.height = height;
            this.refreshRate = refreshRate;
            this.bitsPerPixel = bitsPerPixel;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.width) + "x" + this.height + ", bpp: " + this.bitsPerPixel + ", hz: " + this.refreshRate;
        }
    }
    
    public static class Monitor
    {
        public final int virtualX;
        public final int virtualY;
        public final String name;
        
        protected Monitor(final int virtualX, final int virtualY, final String name) {
            this.virtualX = virtualX;
            this.virtualY = virtualY;
            this.name = name;
        }
    }
    
    public static class BufferFormat
    {
        public final int r;
        public final int g;
        public final int b;
        public final int a;
        public final int depth;
        public final int stencil;
        public final int samples;
        public final boolean coverageSampling;
        
        public BufferFormat(final int r, final int g, final int b, final int a, final int depth, final int stencil, final int samples, final boolean coverageSampling) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.depth = depth;
            this.stencil = stencil;
            this.samples = samples;
            this.coverageSampling = coverageSampling;
        }
        
        @Override
        public String toString() {
            return "r: " + this.r + ", g: " + this.g + ", b: " + this.b + ", a: " + this.a + ", depth: " + this.depth + ", stencil: " + this.stencil + ", num samples: " + this.samples + ", coverage sampling: " + this.coverageSampling;
        }
    }
}
