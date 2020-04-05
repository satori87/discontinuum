// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.scenes.scene2d.Action;

public abstract class DelegateAction extends Action
{
    protected Action action;
    
    public void setAction(final Action action) {
        this.action = action;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    protected abstract boolean delegate(final float p0);
    
    @Override
    public final boolean act(final float delta) {
        final Pool pool = this.getPool();
        this.setPool(null);
        try {
            return this.delegate(delta);
        }
        finally {
            this.setPool(pool);
        }
    }
    
    @Override
    public void restart() {
        if (this.action != null) {
            this.action.restart();
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.action = null;
    }
    
    @Override
    public void setActor(final Actor actor) {
        if (this.action != null) {
            this.action.setActor(actor);
        }
        super.setActor(actor);
    }
    
    @Override
    public void setTarget(final Actor target) {
        if (this.action != null) {
            this.action.setTarget(target);
        }
        super.setTarget(target);
    }
    
    @Override
    public String toString() {
        return String.valueOf(super.toString()) + ((this.action == null) ? "" : ("(" + this.action + ")"));
    }
}
