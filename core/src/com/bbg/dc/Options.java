// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc;

public class Options
{
    public String version;
    public boolean fullscreen;
    public int windowX;
    public int windowY;
    public boolean framelimit;
    
    public Options() {
        this.version = "d1";
        this.fullscreen = true;
        this.windowX = 1024;
        this.windowY = 768;
        this.framelimit = false;
    }
}
