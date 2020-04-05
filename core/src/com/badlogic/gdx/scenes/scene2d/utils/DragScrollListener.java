// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.math.Vector2;

public class DragScrollListener extends DragListener
{
    static final Vector2 tmpCoords;
    private ScrollPane scroll;
    private Timer.Task scrollUp;
    private Timer.Task scrollDown;
    Interpolation interpolation;
    float minSpeed;
    float maxSpeed;
    float tickSecs;
    long startTime;
    long rampTime;
    
    static {
        tmpCoords = new Vector2();
    }
    
    public DragScrollListener(final ScrollPane scroll) {
        this.interpolation = Interpolation.exp5In;
        this.minSpeed = 15.0f;
        this.maxSpeed = 75.0f;
        this.tickSecs = 0.05f;
        this.rampTime = 1750L;
        this.scroll = scroll;
        this.scrollUp = new Timer.Task() {
            @Override
            public void run() {
                DragScrollListener.this.scroll(scroll.getScrollY() - DragScrollListener.this.getScrollPixels());
            }
        };
        this.scrollDown = new Timer.Task() {
            @Override
            public void run() {
                DragScrollListener.this.scroll(scroll.getScrollY() + DragScrollListener.this.getScrollPixels());
            }
        };
    }
    
    public void setup(final float minSpeedPixels, final float maxSpeedPixels, final float tickSecs, final float rampSecs) {
        this.minSpeed = minSpeedPixels;
        this.maxSpeed = maxSpeedPixels;
        this.tickSecs = tickSecs;
        this.rampTime = (long)(rampSecs * 1000.0f);
    }
    
    float getScrollPixels() {
        return this.interpolation.apply(this.minSpeed, this.maxSpeed, Math.min(1.0f, (System.currentTimeMillis() - this.startTime) / (float)this.rampTime));
    }
    
    @Override
    public void drag(final InputEvent event, final float x, final float y, final int pointer) {
        event.getListenerActor().localToActorCoordinates(this.scroll, DragScrollListener.tmpCoords.set(x, y));
        if (DragScrollListener.tmpCoords.x >= 0.0f && DragScrollListener.tmpCoords.x < this.scroll.getWidth()) {
            if (DragScrollListener.tmpCoords.y >= this.scroll.getHeight()) {
                this.scrollDown.cancel();
                if (!this.scrollUp.isScheduled()) {
                    this.startTime = System.currentTimeMillis();
                    Timer.schedule(this.scrollUp, this.tickSecs, this.tickSecs);
                }
                return;
            }
            if (DragScrollListener.tmpCoords.y < 0.0f) {
                this.scrollUp.cancel();
                if (!this.scrollDown.isScheduled()) {
                    this.startTime = System.currentTimeMillis();
                    Timer.schedule(this.scrollDown, this.tickSecs, this.tickSecs);
                }
                return;
            }
        }
        this.scrollUp.cancel();
        this.scrollDown.cancel();
    }
    
    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer) {
        this.scrollUp.cancel();
        this.scrollDown.cancel();
    }
    
    protected void scroll(final float y) {
        this.scroll.setScrollY(y);
    }
}
