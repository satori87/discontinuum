// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ClickListener extends InputListener
{
    public static float visualPressedDuration;
    private float tapSquareSize;
    private float touchDownX;
    private float touchDownY;
    private int pressedPointer;
    private int pressedButton;
    private int button;
    private boolean pressed;
    private boolean over;
    private boolean cancelled;
    private long visualPressedTime;
    private long tapCountInterval;
    private int tapCount;
    private long lastTapTime;
    
    static {
        ClickListener.visualPressedDuration = 0.1f;
    }
    
    public ClickListener() {
        this.tapSquareSize = 14.0f;
        this.touchDownX = -1.0f;
        this.touchDownY = -1.0f;
        this.pressedPointer = -1;
        this.pressedButton = -1;
        this.tapCountInterval = 400000000L;
    }
    
    public ClickListener(final int button) {
        this.tapSquareSize = 14.0f;
        this.touchDownX = -1.0f;
        this.touchDownY = -1.0f;
        this.pressedPointer = -1;
        this.pressedButton = -1;
        this.tapCountInterval = 400000000L;
        this.button = button;
    }
    
    @Override
    public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        if (this.pressed) {
            return false;
        }
        if (pointer == 0 && this.button != -1 && button != this.button) {
            return false;
        }
        this.pressed = true;
        this.pressedPointer = pointer;
        this.pressedButton = button;
        this.touchDownX = x;
        this.touchDownY = y;
        this.setVisualPressed(true);
        return true;
    }
    
    @Override
    public void touchDragged(final InputEvent event, final float x, final float y, final int pointer) {
        if (pointer != this.pressedPointer || this.cancelled) {
            return;
        }
        if (!(this.pressed = this.isOver(event.getListenerActor(), x, y))) {
            this.invalidateTapSquare();
        }
    }
    
    @Override
    public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
        if (pointer == this.pressedPointer) {
            if (!this.cancelled) {
                boolean touchUpOver = this.isOver(event.getListenerActor(), x, y);
                if (touchUpOver && pointer == 0 && this.button != -1 && button != this.button) {
                    touchUpOver = false;
                }
                if (touchUpOver) {
                    final long time = TimeUtils.nanoTime();
                    if (time - this.lastTapTime > this.tapCountInterval) {
                        this.tapCount = 0;
                    }
                    ++this.tapCount;
                    this.lastTapTime = time;
                    this.clicked(event, x, y);
                }
            }
            this.pressed = false;
            this.pressedPointer = -1;
            this.pressedButton = -1;
            this.cancelled = false;
        }
    }
    
    @Override
    public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
        if (pointer == -1 && !this.cancelled) {
            this.over = true;
        }
    }
    
    @Override
    public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
        if (pointer == -1 && !this.cancelled) {
            this.over = false;
        }
    }
    
    public void cancel() {
        if (this.pressedPointer == -1) {
            return;
        }
        this.cancelled = true;
        this.pressed = false;
    }
    
    public void clicked(final InputEvent event, final float x, final float y) {
    }
    
    public boolean isOver(final Actor actor, final float x, final float y) {
        final Actor hit = actor.hit(x, y, true);
        return (hit != null && hit.isDescendantOf(actor)) || this.inTapSquare(x, y);
    }
    
    public boolean inTapSquare(final float x, final float y) {
        return (this.touchDownX != -1.0f || this.touchDownY != -1.0f) && (Math.abs(x - this.touchDownX) < this.tapSquareSize && Math.abs(y - this.touchDownY) < this.tapSquareSize);
    }
    
    public boolean inTapSquare() {
        return this.touchDownX != -1.0f;
    }
    
    public void invalidateTapSquare() {
        this.touchDownX = -1.0f;
        this.touchDownY = -1.0f;
    }
    
    public boolean isPressed() {
        return this.pressed;
    }
    
    public boolean isVisualPressed() {
        if (this.pressed) {
            return true;
        }
        if (this.visualPressedTime <= 0L) {
            return false;
        }
        if (this.visualPressedTime > TimeUtils.millis()) {
            return true;
        }
        this.visualPressedTime = 0L;
        return false;
    }
    
    public void setVisualPressed(final boolean visualPressed) {
        if (visualPressed) {
            this.visualPressedTime = TimeUtils.millis() + (long)(ClickListener.visualPressedDuration * 1000.0f);
        }
        else {
            this.visualPressedTime = 0L;
        }
    }
    
    public boolean isOver() {
        return this.over || this.pressed;
    }
    
    public void setTapSquareSize(final float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }
    
    public float getTapSquareSize() {
        return this.tapSquareSize;
    }
    
    public void setTapCountInterval(final float tapCountInterval) {
        this.tapCountInterval = (long)(tapCountInterval * 1.0E9f);
    }
    
    public int getTapCount() {
        return this.tapCount;
    }
    
    public void setTapCount(final int tapCount) {
        this.tapCount = tapCount;
    }
    
    public float getTouchDownX() {
        return this.touchDownX;
    }
    
    public float getTouchDownY() {
        return this.touchDownY;
    }
    
    public int getPressedButton() {
        return this.pressedButton;
    }
    
    public int getPressedPointer() {
        return this.pressedPointer;
    }
    
    public int getButton() {
        return this.button;
    }
    
    public void setButton(final int button) {
        this.button = button;
    }
}
