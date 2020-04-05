// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputEvent extends Event
{
    private Type type;
    private float stageX;
    private float stageY;
    private int pointer;
    private int button;
    private int keyCode;
    private int scrollAmount;
    private char character;
    private Actor relatedActor;
    
    @Override
    public void reset() {
        super.reset();
        this.relatedActor = null;
        this.button = -1;
    }
    
    public float getStageX() {
        return this.stageX;
    }
    
    public void setStageX(final float stageX) {
        this.stageX = stageX;
    }
    
    public float getStageY() {
        return this.stageY;
    }
    
    public void setStageY(final float stageY) {
        this.stageY = stageY;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public void setType(final Type type) {
        this.type = type;
    }
    
    public int getPointer() {
        return this.pointer;
    }
    
    public void setPointer(final int pointer) {
        this.pointer = pointer;
    }
    
    public int getButton() {
        return this.button;
    }
    
    public void setButton(final int button) {
        this.button = button;
    }
    
    public int getKeyCode() {
        return this.keyCode;
    }
    
    public void setKeyCode(final int keyCode) {
        this.keyCode = keyCode;
    }
    
    public char getCharacter() {
        return this.character;
    }
    
    public void setCharacter(final char character) {
        this.character = character;
    }
    
    public int getScrollAmount() {
        return this.scrollAmount;
    }
    
    public void setScrollAmount(final int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }
    
    public Actor getRelatedActor() {
        return this.relatedActor;
    }
    
    public void setRelatedActor(final Actor relatedActor) {
        this.relatedActor = relatedActor;
    }
    
    public Vector2 toCoordinates(final Actor actor, final Vector2 actorCoords) {
        actorCoords.set(this.stageX, this.stageY);
        actor.stageToLocalCoordinates(actorCoords);
        return actorCoords;
    }
    
    public boolean isTouchFocusCancel() {
        return this.stageX == -2.14748365E9f || this.stageY == -2.14748365E9f;
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
    
    public enum Type
    {
        touchDown("touchDown", 0), 
        touchUp("touchUp", 1), 
        touchDragged("touchDragged", 2), 
        mouseMoved("mouseMoved", 3), 
        enter("enter", 4), 
        exit("exit", 5), 
        scrolled("scrolled", 6), 
        keyDown("keyDown", 7), 
        keyUp("keyUp", 8), 
        keyTyped("keyTyped", 9);
        
        private Type(final String name, final int ordinal) {
        }
    }
}
