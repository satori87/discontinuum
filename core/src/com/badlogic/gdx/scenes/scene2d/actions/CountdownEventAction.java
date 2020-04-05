// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Event;

public class CountdownEventAction<T extends Event> extends EventAction<T>
{
    int count;
    int current;
    
    public CountdownEventAction(final Class<? extends T> eventClass, final int count) {
        super(eventClass);
        this.count = count;
    }
    
    @Override
    public boolean handle(final T event) {
        ++this.current;
        return this.current >= this.count;
    }
}
