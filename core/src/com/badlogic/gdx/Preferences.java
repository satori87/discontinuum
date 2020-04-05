// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import java.util.Map;

public interface Preferences
{
    Preferences putBoolean(final String p0, final boolean p1);
    
    Preferences putInteger(final String p0, final int p1);
    
    Preferences putLong(final String p0, final long p1);
    
    Preferences putFloat(final String p0, final float p1);
    
    Preferences putString(final String p0, final String p1);
    
    Preferences put(final Map<String, ?> p0);
    
    boolean getBoolean(final String p0);
    
    int getInteger(final String p0);
    
    long getLong(final String p0);
    
    float getFloat(final String p0);
    
    String getString(final String p0);
    
    boolean getBoolean(final String p0, final boolean p1);
    
    int getInteger(final String p0, final int p1);
    
    long getLong(final String p0, final long p1);
    
    float getFloat(final String p0, final float p1);
    
    String getString(final String p0, final String p1);
    
    Map<String, ?> get();
    
    boolean contains(final String p0);
    
    void clear();
    
    void remove(final String p0);
    
    void flush();
}
