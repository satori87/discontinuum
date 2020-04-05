// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

public class InputAdapter implements InputProcessor
{
    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }
    
    @Override
    public boolean keyUp(final int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(final char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }
    
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }
    
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(final int amount) {
        return false;
    }
}
