// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class RemoveAction extends Action
{
    private Action action;
    
    @Override
    public boolean act(final float delta) {
        this.target.removeAction(this.action);
        return true;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public void setAction(final Action action) {
        this.action = action;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.action = null;
    }
}
