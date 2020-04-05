// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Action;

public class RemoveListenerAction extends Action
{
    private EventListener listener;
    private boolean capture;
    
    @Override
    public boolean act(final float delta) {
        if (this.capture) {
            this.target.removeCaptureListener(this.listener);
        }
        else {
            this.target.removeListener(this.listener);
        }
        return true;
    }
    
    public EventListener getListener() {
        return this.listener;
    }
    
    public void setListener(final EventListener listener) {
        this.listener = listener;
    }
    
    public boolean getCapture() {
        return this.capture;
    }
    
    public void setCapture(final boolean capture) {
        this.capture = capture;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.listener = null;
    }
}
