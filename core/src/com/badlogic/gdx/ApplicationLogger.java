// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public interface ApplicationLogger
{
    void log(final String p0, final String p1);
    
    void log(final String p0, final String p1, final Throwable p2);
    
    void error(final String p0, final String p1);
    
    void error(final String p0, final String p1, final Throwable p2);
    
    void debug(final String p0, final String p1);
    
    void debug(final String p0, final String p1, final Throwable p2);
}
