// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputListener implements EventListener
{
    private static final Vector2 tmpCoords;
    
    static {
        tmpCoords = new Vector2();
    }
    
    @Override
    public boolean handle(final Event e) {
        if (!(e instanceof InputEvent)) {
            return false;
        }
        final InputEvent event = (InputEvent)e;
        switch (event.getType()) {
            case keyDown: {
                return this.keyDown(event, event.getKeyCode());
            }
            case keyUp: {
                return this.keyUp(event, event.getKeyCode());
            }
            case keyTyped: {
                return this.keyTyped(event, event.getCharacter());
            }
            default: {
                event.toCoordinates(event.getListenerActor(), InputListener.tmpCoords);
                switch (event.getType()) {
                    case touchDown: {
                        return this.touchDown(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getPointer(), event.getButton());
                    }
                    case touchUp: {
                        this.touchUp(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getPointer(), event.getButton());
                        return true;
                    }
                    case touchDragged: {
                        this.touchDragged(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getPointer());
                        return true;
                    }
                    case mouseMoved: {
                        return this.mouseMoved(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y);
                    }
                    case scrolled: {
                        return this.scrolled(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getScrollAmount());
                    }
                    case enter: {
                        this.enter(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getPointer(), event.getRelatedActor());
                        return false;
                    }
                    case exit: {
                        this.exit(event, InputListener.tmpCoords.x, InputListener.tmpCoords.y, event.getPointer(), event.getRelatedActor());
                        return false;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
        }
    }
    
    public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        return false;
    }
    
    public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
    }
    
    public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
    }
    
    public boolean mouseMoved(final InputEvent event, final float x, final float y) {
        return false;
    }
    
    public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
    }
    
    public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
    }
    
    public boolean scrolled(final InputEvent event, final float x, final float y, final int amount) {
        return false;
    }
    
    public boolean keyDown(final InputEvent event, final int keycode) {
        return false;
    }
    
    public boolean keyUp(final InputEvent event, final int keycode) {
        return false;
    }
    
    public boolean keyTyped(final InputEvent event, final char character) {
        return false;
    }
}
