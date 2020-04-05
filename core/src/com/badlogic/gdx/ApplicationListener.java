// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public interface ApplicationListener
{
    void create();
    
    void resize(final int p0, final int p1);
    
    void render();
    
    void pause();
    
    void resume();
    
    void dispose();
}
