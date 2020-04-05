// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public interface InputProcessor
{
    boolean keyDown(final int p0);
    
    boolean keyUp(final int p0);
    
    boolean keyTyped(final char p0);
    
    boolean touchDown(final int p0, final int p1, final int p2, final int p3);
    
    boolean touchUp(final int p0, final int p1, final int p2, final int p3);
    
    boolean touchDragged(final int p0, final int p1, final int p2);
    
    boolean mouseMoved(final int p0, final int p1);
    
    boolean scrolled(final int p0);
}
