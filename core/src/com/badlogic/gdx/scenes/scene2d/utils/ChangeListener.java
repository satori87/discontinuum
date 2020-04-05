// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class ChangeListener implements EventListener
{
    @Override
    public boolean handle(final Event event) {
        if (!(event instanceof ChangeEvent)) {
            return false;
        }
        this.changed((ChangeEvent)event, event.getTarget());
        return false;
    }
    
    public abstract void changed(final ChangeEvent p0, final Actor p1);
    
    public static class ChangeEvent extends Event
    {
    }
}
