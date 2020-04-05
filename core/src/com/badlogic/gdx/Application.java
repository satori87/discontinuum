// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.utils.Clipboard;

public interface Application
{
    public static final int LOG_NONE = 0;
    public static final int LOG_DEBUG = 3;
    public static final int LOG_INFO = 2;
    public static final int LOG_ERROR = 1;
    
    ApplicationListener getApplicationListener();
    
    Graphics getGraphics();
    
    Audio getAudio();
    
    Input getInput();
    
    Files getFiles();
    
    Net getNet();
    
    void log(final String p0, final String p1);
    
    void log(final String p0, final String p1, final Throwable p2);
    
    void error(final String p0, final String p1);
    
    void error(final String p0, final String p1, final Throwable p2);
    
    void debug(final String p0, final String p1);
    
    void debug(final String p0, final String p1, final Throwable p2);
    
    void setLogLevel(final int p0);
    
    int getLogLevel();
    
    void setApplicationLogger(final ApplicationLogger p0);
    
    ApplicationLogger getApplicationLogger();
    
    ApplicationType getType();
    
    int getVersion();
    
    long getJavaHeap();
    
    long getNativeHeap();
    
    Preferences getPreferences(final String p0);
    
    Clipboard getClipboard();
    
    void postRunnable(final Runnable p0);
    
    void exit();
    
    void addLifecycleListener(final LifecycleListener p0);
    
    void removeLifecycleListener(final LifecycleListener p0);
    
    public enum ApplicationType
    {
        Android("Android", 0), 
        Desktop("Desktop", 1), 
        HeadlessDesktop("HeadlessDesktop", 2), 
        Applet("Applet", 3), 
        WebGL("WebGL", 4), 
        iOS("iOS", 5);
        
        private ApplicationType(final String name, final int ordinal) {
        }
    }
}
