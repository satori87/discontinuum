// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public interface Screen
{
    void show();
    
    void render(final float p0);
    
    void resize(final int p0, final int p1);
    
    void pause();
    
    void resume();
    
    void hide();
    
    void dispose();
}
