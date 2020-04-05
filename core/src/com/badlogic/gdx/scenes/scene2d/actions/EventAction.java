// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Event;

public abstract class EventAction<T extends Event> extends Action
{
    final Class<? extends T> eventClass;
    boolean result;
    boolean active;
    private final EventListener listener;
    
    public EventAction(final Class<? extends T> eventClass) {
        this.listener = new EventListener() {
            @Override
            public boolean handle(final Event event) {
                return EventAction.this.active && ClassReflection.isInstance(EventAction.this.eventClass, event) && (EventAction.this.result = EventAction.this.handle(event));
            }
        };
        this.eventClass = eventClass;
    }
    
    @Override
    public void restart() {
        this.result = false;
        this.active = false;
    }
    
    @Override
    public void setTarget(final Actor newTarget) {
        if (this.target != null) {
            this.target.removeListener(this.listener);
        }
        super.setTarget(newTarget);
        if (newTarget != null) {
            newTarget.addListener(this.listener);
        }
    }
    
    public abstract boolean handle(final T p0);
    
    @Override
    public boolean act(final float delta) {
        this.active = true;
        return this.result;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
}
