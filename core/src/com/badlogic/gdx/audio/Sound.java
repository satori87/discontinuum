// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface Sound extends Disposable
{
    long play();
    
    long play(final float p0);
    
    long play(final float p0, final float p1, final float p2);
    
    long loop();
    
    long loop(final float p0);
    
    long loop(final float p0, final float p1, final float p2);
    
    void stop();
    
    void pause();
    
    void resume();
    
    void dispose();
    
    void stop(final long p0);
    
    void pause(final long p0);
    
    void resume(final long p0);
    
    void setLooping(final long p0, final boolean p1);
    
    void setPitch(final long p0, final float p1);
    
    void setVolume(final long p0, final float p1);
    
    void setPan(final long p0, final float p1, final float p2);
}
