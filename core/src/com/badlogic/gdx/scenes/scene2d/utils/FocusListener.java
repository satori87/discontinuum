// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class FocusListener implements EventListener
{
    @Override
    public boolean handle(final Event event) {
        if (!(event instanceof FocusEvent)) {
            return false;
        }
        final FocusEvent focusEvent = (FocusEvent)event;
        switch (focusEvent.getType()) {
            case keyboard: {
                this.keyboardFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                break;
            }
            case scroll: {
                this.scrollFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                break;
            }
        }
        return false;
    }
    
    public void keyboardFocusChanged(final FocusEvent event, final Actor actor, final boolean focused) {
    }
    
    public void scrollFocusChanged(final FocusEvent event, final Actor actor, final boolean focused) {
    }
    
    public static class FocusEvent extends Event
    {
        private boolean focused;
        private Type type;
        private Actor relatedActor;
        
        @Override
        public void reset() {
            super.reset();
            this.relatedActor = null;
        }
        
        public boolean isFocused() {
            return this.focused;
        }
        
        public void setFocused(final boolean focused) {
            this.focused = focused;
        }
        
        public Type getType() {
            return this.type;
        }
        
        public void setType(final Type focusType) {
            this.type = focusType;
        }
        
        public Actor getRelatedActor() {
            return this.relatedActor;
        }
        
        public void setRelatedActor(final Actor relatedActor) {
            this.relatedActor = relatedActor;
        }
        
        public enum Type
        {
            keyboard("keyboard", 0), 
            scroll("scroll", 1);
            
            private Type(final String name, final int ordinal) {
            }
        }
    }
}
