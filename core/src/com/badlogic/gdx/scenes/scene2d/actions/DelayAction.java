// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class DelayAction extends DelegateAction
{
    private float duration;
    private float time;
    
    public DelayAction() {
    }
    
    public DelayAction(final float duration) {
        this.duration = duration;
    }
    
    @Override
    protected boolean delegate(float delta) {
        if (this.time < this.duration) {
            this.time += delta;
            if (this.time < this.duration) {
                return false;
            }
            delta = this.time - this.duration;
        }
        return this.action == null || this.action.act(delta);
    }
    
    public void finish() {
        this.time = this.duration;
    }
    
    @Override
    public void restart() {
        super.restart();
        this.time = 0.0f;
    }
    
    public float getTime() {
        return this.time;
    }
    
    public void setTime(final float time) {
        this.time = time;
    }
    
    public float getDuration() {
        return this.duration;
    }
    
    public void setDuration(final float duration) {
        this.duration = duration;
    }
}
