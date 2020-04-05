// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.utils.Pool;

public abstract class Action implements Pool.Poolable
{
    protected Actor actor;
    protected Actor target;
    private Pool pool;
    
    public abstract boolean act(final float p0);
    
    public void restart() {
    }
    
    public void setActor(final Actor actor) {
        this.actor = actor;
        if (this.target == null) {
            this.setTarget(actor);
        }
        if (actor == null && this.pool != null) {
            this.pool.free(this);
            this.pool = null;
        }
    }
    
    public Actor getActor() {
        return this.actor;
    }
    
    public void setTarget(final Actor target) {
        this.target = target;
    }
    
    public Actor getTarget() {
        return this.target;
    }
    
    @Override
    public void reset() {
        this.actor = null;
        this.target = null;
        this.pool = null;
        this.restart();
    }
    
    public Pool getPool() {
        return this.pool;
    }
    
    public void setPool(final Pool pool) {
        this.pool = pool;
    }
    
    @Override
    public String toString() {
        String name = this.getClass().getName();
        final int dotIndex = name.lastIndexOf(46);
        if (dotIndex != -1) {
            name = name.substring(dotIndex + 1);
        }
        if (name.endsWith("Action")) {
            name = name.substring(0, name.length() - 6);
        }
        return name;
    }
}
