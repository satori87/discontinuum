// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Action;

public class LayoutAction extends Action
{
    private boolean enabled;
    
    @Override
    public void setTarget(final Actor actor) {
        if (actor != null && !(actor instanceof Layout)) {
            throw new GdxRuntimeException("Actor must implement layout: " + actor);
        }
        super.setTarget(actor);
    }
    
    @Override
    public boolean act(final float delta) {
        ((Layout)this.target).setLayoutEnabled(this.enabled);
        return true;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setLayoutEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
