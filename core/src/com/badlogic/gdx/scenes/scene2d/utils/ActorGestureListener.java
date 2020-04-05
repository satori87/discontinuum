// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class ActorGestureListener implements EventListener
{
    static final Vector2 tmpCoords;
    static final Vector2 tmpCoords2;
    private final GestureDetector detector;
    InputEvent event;
    Actor actor;
    Actor touchDownTarget;
    
    static {
        tmpCoords = new Vector2();
        tmpCoords2 = new Vector2();
    }
    
    public ActorGestureListener() {
        this(20.0f, 0.4f, 1.1f, 0.15f);
    }
    
    public ActorGestureListener(final float halfTapSquareSize, final float tapCountInterval, final float longPressDuration, final float maxFlingDelay) {
        this.detector = new GestureDetector(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, new GestureDetector.GestureAdapter() {
            private final Vector2 initialPointer1 = new Vector2();
            private final Vector2 initialPointer2 = new Vector2();
            private final Vector2 pointer1 = new Vector2();
            private final Vector2 pointer2 = new Vector2();
            
            @Override
            public boolean tap(final float stageX, final float stageY, final int count, final int button) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords.set(stageX, stageY));
                ActorGestureListener.this.tap(ActorGestureListener.this.event, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y, count, button);
                return true;
            }
            
            @Override
            public boolean longPress(final float stageX, final float stageY) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords.set(stageX, stageY));
                return ActorGestureListener.this.longPress(ActorGestureListener.this.actor, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y);
            }
            
            @Override
            public boolean fling(final float velocityX, final float velocityY, final int button) {
                this.stageToLocalAmount(ActorGestureListener.tmpCoords.set(velocityX, velocityY));
                ActorGestureListener.this.fling(ActorGestureListener.this.event, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y, button);
                return true;
            }
            
            @Override
            public boolean pan(final float stageX, final float stageY, float deltaX, float deltaY) {
                this.stageToLocalAmount(ActorGestureListener.tmpCoords.set(deltaX, deltaY));
                deltaX = ActorGestureListener.tmpCoords.x;
                deltaY = ActorGestureListener.tmpCoords.y;
                ActorGestureListener.this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords.set(stageX, stageY));
                ActorGestureListener.this.pan(ActorGestureListener.this.event, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y, deltaX, deltaY);
                return true;
            }
            
            @Override
            public boolean zoom(final float initialDistance, final float distance) {
                ActorGestureListener.this.zoom(ActorGestureListener.this.event, initialDistance, distance);
                return true;
            }
            
            @Override
            public boolean pinch(final Vector2 stageInitialPointer1, final Vector2 stageInitialPointer2, final Vector2 stagePointer1, final Vector2 stagePointer2) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.initialPointer1.set(stageInitialPointer1));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.initialPointer2.set(stageInitialPointer2));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.pointer1.set(stagePointer1));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.pointer2.set(stagePointer2));
                ActorGestureListener.this.pinch(ActorGestureListener.this.event, this.initialPointer1, this.initialPointer2, this.pointer1, this.pointer2);
                return true;
            }
            
            private void stageToLocalAmount(final Vector2 amount) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(amount);
                amount.sub(ActorGestureListener.this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords2.set(0.0f, 0.0f)));
            }
        });
    }
    
    @Override
    public boolean handle(final Event e) {
        if (!(e instanceof InputEvent)) {
            return false;
        }
        final InputEvent event = (InputEvent)e;
        switch (event.getType()) {
            case touchDown: {
                this.actor = event.getListenerActor();
                this.touchDownTarget = event.getTarget();
                this.detector.touchDown(event.getStageX(), event.getStageY(), event.getPointer(), event.getButton());
                this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords.set(event.getStageX(), event.getStageY()));
                this.touchDown(event, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y, event.getPointer(), event.getButton());
                return true;
            }
            case touchUp: {
                if (event.isTouchFocusCancel()) {
                    this.detector.reset();
                    return false;
                }
                this.event = event;
                this.actor = event.getListenerActor();
                this.detector.touchUp(event.getStageX(), event.getStageY(), event.getPointer(), event.getButton());
                this.actor.stageToLocalCoordinates(ActorGestureListener.tmpCoords.set(event.getStageX(), event.getStageY()));
                this.touchUp(event, ActorGestureListener.tmpCoords.x, ActorGestureListener.tmpCoords.y, event.getPointer(), event.getButton());
                return true;
            }
            case touchDragged: {
                this.event = event;
                this.actor = event.getListenerActor();
                this.detector.touchDragged(event.getStageX(), event.getStageY(), event.getPointer());
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void touchDown(final InputEvent event, final float x, final float y, final int pointer, final int button) {
    }
    
    public void touchUp(final InputEvent event, final float x, final float y, final int pointer, final int button) {
    }
    
    public void tap(final InputEvent event, final float x, final float y, final int count, final int button) {
    }
    
    public boolean longPress(final Actor actor, final float x, final float y) {
        return false;
    }
    
    public void fling(final InputEvent event, final float velocityX, final float velocityY, final int button) {
    }
    
    public void pan(final InputEvent event, final float x, final float y, final float deltaX, final float deltaY) {
    }
    
    public void zoom(final InputEvent event, final float initialDistance, final float distance) {
    }
    
    public void pinch(final InputEvent event, final Vector2 initialPointer1, final Vector2 initialPointer2, final Vector2 pointer1, final Vector2 pointer2) {
    }
    
    public GestureDetector getGestureDetector() {
        return this.detector;
    }
    
    public Actor getTouchDownTarget() {
        return this.touchDownTarget;
    }
}
