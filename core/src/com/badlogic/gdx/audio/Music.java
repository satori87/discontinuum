// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface Music extends Disposable
{
    void play();
    
    void pause();
    
    void stop();
    
    boolean isPlaying();
    
    void setLooping(final boolean p0);
    
    boolean isLooping();
    
    void setVolume(final float p0);
    
    float getVolume();
    
    void setPan(final float p0, final float p1);
    
    void setPosition(final float p0);
    
    float getPosition();
    
    void dispose();
    
    void setOnCompletionListener(final OnCompletionListener p0);
    
    public interface OnCompletionListener
    {
        void onCompletion(final Music p0);
    }
}
