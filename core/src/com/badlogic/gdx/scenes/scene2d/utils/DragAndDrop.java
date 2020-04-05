// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import java.util.Iterator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;

public class DragAndDrop
{
    static final Vector2 tmpVector;
    Payload payload;
    Actor dragActor;
    Target target;
    boolean isValidTarget;
    Array<Target> targets;
    ObjectMap<Source, DragListener> sourceListeners;
    private float tapSquareSize;
    private int button;
    float dragActorX;
    float dragActorY;
    float touchOffsetX;
    float touchOffsetY;
    long dragStartTime;
    int dragTime;
    int activePointer;
    boolean cancelTouchFocus;
    boolean keepWithinStage;
    
    static {
        tmpVector = new Vector2();
    }
    
    public DragAndDrop() {
        this.targets = new Array<Target>();
        this.sourceListeners = new ObjectMap<Source, DragListener>();
        this.tapSquareSize = 8.0f;
        this.dragActorX = 0.0f;
        this.dragActorY = 0.0f;
        this.dragTime = 250;
        this.activePointer = -1;
        this.cancelTouchFocus = true;
        this.keepWithinStage = true;
    }
    
    public void addSource(final Source source) {
        final DragListener listener = new DragListener() {
            @Override
            public void dragStart(final InputEvent event, final float x, final float y, final int pointer) {
                if (DragAndDrop.this.activePointer != -1) {
                    event.stop();
                    return;
                }
                DragAndDrop.this.activePointer = pointer;
                DragAndDrop.this.dragStartTime = System.currentTimeMillis();
                DragAndDrop.this.payload = source.dragStart(event, this.getTouchDownX(), this.getTouchDownY(), pointer);
                event.stop();
                if (DragAndDrop.this.cancelTouchFocus && DragAndDrop.this.payload != null) {
                    source.getActor().getStage().cancelTouchFocusExcept(this, source.getActor());
                }
            }
            
            @Override
            public void drag(final InputEvent event, final float x, final float y, final int pointer) {
                if (DragAndDrop.this.payload == null) {
                    return;
                }
                if (pointer != DragAndDrop.this.activePointer) {
                    return;
                }
                final Stage stage = event.getStage();
                Touchable dragActorTouchable = null;
                if (DragAndDrop.this.dragActor != null) {
                    dragActorTouchable = DragAndDrop.this.dragActor.getTouchable();
                    DragAndDrop.this.dragActor.setTouchable(Touchable.disabled);
                }
                Target newTarget = null;
                DragAndDrop.this.isValidTarget = false;
                final float stageX = event.getStageX() + DragAndDrop.this.touchOffsetX;
                final float stageY = event.getStageY() + DragAndDrop.this.touchOffsetY;
                Actor hit = event.getStage().hit(stageX, stageY, true);
                if (hit == null) {
                    hit = event.getStage().hit(stageX, stageY, false);
                }
                if (hit != null) {
                    for (int i = 0, n = DragAndDrop.this.targets.size; i < n; ++i) {
                        final Target target = DragAndDrop.this.targets.get(i);
                        if (target.actor.isAscendantOf(hit)) {
                            newTarget = target;
                            target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(stageX, stageY));
                            break;
                        }
                    }
                }
                if (newTarget != DragAndDrop.this.target) {
                    if (DragAndDrop.this.target != null) {
                        DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                    }
                    DragAndDrop.this.target = newTarget;
                }
                if (newTarget != null) {
                    DragAndDrop.this.isValidTarget = newTarget.drag(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                }
                if (DragAndDrop.this.dragActor != null) {
                    DragAndDrop.this.dragActor.setTouchable(dragActorTouchable);
                }
                Actor actor = null;
                if (DragAndDrop.this.target != null) {
                    actor = (DragAndDrop.this.isValidTarget ? DragAndDrop.this.payload.validDragActor : DragAndDrop.this.payload.invalidDragActor);
                }
                if (actor == null) {
                    actor = DragAndDrop.this.payload.dragActor;
                }
                if (actor == null) {
                    return;
                }
                if (DragAndDrop.this.dragActor != actor) {
                    if (DragAndDrop.this.dragActor != null) {
                        DragAndDrop.this.dragActor.remove();
                    }
                    stage.addActor(DragAndDrop.this.dragActor = actor);
                }
                float actorX = event.getStageX() - actor.getWidth() + DragAndDrop.this.dragActorX;
                float actorY = event.getStageY() + DragAndDrop.this.dragActorY;
                if (DragAndDrop.this.keepWithinStage) {
                    if (actorX < 0.0f) {
                        actorX = 0.0f;
                    }
                    if (actorY < 0.0f) {
                        actorY = 0.0f;
                    }
                    if (actorX + actor.getWidth() > stage.getWidth()) {
                        actorX = stage.getWidth() - actor.getWidth();
                    }
                    if (actorY + actor.getHeight() > stage.getHeight()) {
                        actorY = stage.getHeight() - actor.getHeight();
                    }
                }
                actor.setPosition(actorX, actorY);
            }
            
            @Override
            public void dragStop(final InputEvent event, final float x, final float y, final int pointer) {
                if (pointer != DragAndDrop.this.activePointer) {
                    return;
                }
                DragAndDrop.this.activePointer = -1;
                if (DragAndDrop.this.payload == null) {
                    return;
                }
                if (System.currentTimeMillis() - DragAndDrop.this.dragStartTime < DragAndDrop.this.dragTime) {
                    DragAndDrop.this.isValidTarget = false;
                }
                if (DragAndDrop.this.dragActor != null) {
                    DragAndDrop.this.dragActor.remove();
                }
                if (DragAndDrop.this.isValidTarget) {
                    final float stageX = event.getStageX() + DragAndDrop.this.touchOffsetX;
                    final float stageY = event.getStageY() + DragAndDrop.this.touchOffsetY;
                    DragAndDrop.this.target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(stageX, stageY));
                    DragAndDrop.this.target.drop(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                }
                source.dragStop(event, x, y, pointer, DragAndDrop.this.payload, DragAndDrop.this.isValidTarget ? DragAndDrop.this.target : null);
                if (DragAndDrop.this.target != null) {
                    DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                }
                DragAndDrop.this.payload = null;
                DragAndDrop.this.target = null;
                DragAndDrop.this.isValidTarget = false;
                DragAndDrop.this.dragActor = null;
            }
        };
        listener.setTapSquareSize(this.tapSquareSize);
        listener.setButton(this.button);
        source.actor.addCaptureListener(listener);
        this.sourceListeners.put(source, listener);
    }
    
    public void removeSource(final Source source) {
        final DragListener dragListener = this.sourceListeners.remove(source);
        source.actor.removeCaptureListener(dragListener);
    }
    
    public void addTarget(final Target target) {
        this.targets.add(target);
    }
    
    public void removeTarget(final Target target) {
        this.targets.removeValue(target, true);
    }
    
    public void clear() {
        this.targets.clear();
        for (final ObjectMap.Entry<Source, DragListener> entry : this.sourceListeners.entries()) {
            entry.key.actor.removeCaptureListener(entry.value);
        }
        this.sourceListeners.clear();
    }
    
    public void setTapSquareSize(final float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }
    
    public void setButton(final int button) {
        this.button = button;
    }
    
    public void setDragActorPosition(final float dragActorX, final float dragActorY) {
        this.dragActorX = dragActorX;
        this.dragActorY = dragActorY;
    }
    
    public void setTouchOffset(final float touchOffsetX, final float touchOffsetY) {
        this.touchOffsetX = touchOffsetX;
        this.touchOffsetY = touchOffsetY;
    }
    
    public boolean isDragging() {
        return this.payload != null;
    }
    
    public Actor getDragActor() {
        return this.dragActor;
    }
    
    public Payload getDragPayload() {
        return this.payload;
    }
    
    public void setDragTime(final int dragMillis) {
        this.dragTime = dragMillis;
    }
    
    public void setCancelTouchFocus(final boolean cancelTouchFocus) {
        this.cancelTouchFocus = cancelTouchFocus;
    }
    
    public void setKeepWithinStage(final boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }
    
    public abstract static class Source
    {
        final Actor actor;
        
        public Source(final Actor actor) {
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
        }
        
        public abstract Payload dragStart(final InputEvent p0, final float p1, final float p2, final int p3);
        
        public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final Payload payload, final Target target) {
        }
        
        public Actor getActor() {
            return this.actor;
        }
    }
    
    public abstract static class Target
    {
        final Actor actor;
        
        public Target(final Actor actor) {
            if (actor == null) {
                throw new IllegalArgumentException("actor cannot be null.");
            }
            this.actor = actor;
            final Stage stage = actor.getStage();
            if (stage != null && actor == stage.getRoot()) {
                throw new IllegalArgumentException("The stage root cannot be a drag and drop target.");
            }
        }
        
        public abstract boolean drag(final Source p0, final Payload p1, final float p2, final float p3, final int p4);
        
        public void reset(final Source source, final Payload payload) {
        }
        
        public abstract void drop(final Source p0, final Payload p1, final float p2, final float p3, final int p4);
        
        public Actor getActor() {
            return this.actor;
        }
    }
    
    public static class Payload
    {
        Actor dragActor;
        Actor validDragActor;
        Actor invalidDragActor;
        Object object;
        
        public void setDragActor(final Actor dragActor) {
            this.dragActor = dragActor;
        }
        
        public Actor getDragActor() {
            return this.dragActor;
        }
        
        public void setValidDragActor(final Actor validDragActor) {
            this.validDragActor = validDragActor;
        }
        
        public Actor getValidDragActor() {
            return this.validDragActor;
        }
        
        public void setInvalidDragActor(final Actor invalidDragActor) {
            this.invalidDragActor = invalidDragActor;
        }
        
        public Actor getInvalidDragActor() {
            return this.invalidDragActor;
        }
        
        public Object getObject() {
            return this.object;
        }
        
        public void setObject(final Object object) {
            this.object = object;
        }
    }
}
