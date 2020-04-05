// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class DragListener extends InputListener
{
    private float tapSquareSize;
    private float touchDownX;
    private float touchDownY;
    private float stageTouchDownX;
    private float stageTouchDownY;
    private float dragStartX;
    private float dragStartY;
    private float dragLastX;
    private float dragLastY;
    private float dragX;
    private float dragY;
    private int pressedPointer;
    private int button;
    private boolean dragging;
    
    public DragListener() {
        this.tapSquareSize = 14.0f;
        this.touchDownX = -1.0f;
        this.touchDownY = -1.0f;
        this.stageTouchDownX = -1.0f;
        this.stageTouchDownY = -1.0f;
        this.pressedPointer = -1;
    }
    
    @Override
    public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        if (this.pressedPointer != -1) {
            return false;
        }
        if (pointer == 0 && this.button != -1 && button != this.button) {
            return false;
        }
        this.pressedPointer = pointer;
        this.touchDownX = x;
        this.touchDownY = y;
        this.stageTouchDownX = event.getStageX();
        this.stageTouchDownY = event.getStageY();
        return true;
    }
    
    @Override
    public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
        if (pointer != this.pressedPointer) {
            return;
        }
        if (!this.dragging && (Math.abs(this.touchDownX - x) > this.tapSquareSize || Math.abs(this.touchDownY - y) > this.tapSquareSize)) {
            this.dragging = true;
            this.dragStart(event, this.dragStartX = x, this.dragStartY = y, pointer);
            this.dragX = x;
            this.dragY = y;
        }
        if (this.dragging) {
            this.dragLastX = this.dragX;
            this.dragLastY = this.dragY;
            this.drag(event, this.dragX = x, this.dragY = y, pointer);
        }
    }
    
    @Override
    public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        if (pointer == this.pressedPointer) {
            if (this.dragging) {
                this.dragStop(event, x, y, pointer);
            }
            this.cancel();
        }
    }
    
    public void dragStart(final InputEvent event, final float x, final float y, final int pointer) {
    }
    
    public void drag(final InputEvent event, final float x, final float y, final int pointer) {
    }
    
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer) {
    }
    
    public void cancel() {
        this.dragging = false;
        this.pressedPointer = -1;
    }
    
    public boolean isDragging() {
        return this.dragging;
    }
    
    public void setTapSquareSize(final float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }
    
    public float getTapSquareSize() {
        return this.tapSquareSize;
    }
    
    public float getTouchDownX() {
        return this.touchDownX;
    }
    
    public float getTouchDownY() {
        return this.touchDownY;
    }
    
    public float getStageTouchDownX() {
        return this.stageTouchDownX;
    }
    
    public float getStageTouchDownY() {
        return this.stageTouchDownY;
    }
    
    public float getDragStartX() {
        return this.dragStartX;
    }
    
    public void setDragStartX(final float dragStartX) {
        this.dragStartX = dragStartX;
    }
    
    public float getDragStartY() {
        return this.dragStartY;
    }
    
    public void setDragStartY(final float dragStartY) {
        this.dragStartY = dragStartY;
    }
    
    public float getDragX() {
        return this.dragX;
    }
    
    public float getDragY() {
        return this.dragY;
    }
    
    public float getDragDistance() {
        return Vector2.len(this.dragX - this.dragStartX, this.dragY - this.dragStartY);
    }
    
    public float getDeltaX() {
        return this.dragX - this.dragLastX;
    }
    
    public float getDeltaY() {
        return this.dragY - this.dragLastY;
    }
    
    public int getButton() {
        return this.button;
    }
    
    public void setButton(final int button) {
        this.button = button;
    }
}
