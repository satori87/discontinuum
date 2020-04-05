// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface AudioDevice extends Disposable
{
    boolean isMono();
    
    void writeSamples(final short[] p0, final int p1, final int p2);
    
    void writeSamples(final float[] p0, final int p1, final int p2);
    
    int getLatency();
    
    void dispose();
    
    void setVolume(final float p0);
}
