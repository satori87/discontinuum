// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;

public abstract class TemporalAction extends Action
{
    private float duration;
    private float time;
    private Interpolation interpolation;
    private boolean reverse;
    private boolean began;
    private boolean complete;
    
    public TemporalAction() {
    }
    
    public TemporalAction(final float duration) {
        this.duration = duration;
    }
    
    public TemporalAction(final float duration, final Interpolation interpolation) {
        this.duration = duration;
        this.interpolation = interpolation;
    }
    
    @Override
    public boolean act(final float delta) {
        if (this.complete) {
            return true;
        }
        final Pool pool = this.getPool();
        this.setPool(null);
        try {
            if (!this.began) {
                this.begin();
                this.began = true;
            }
            this.time += delta;
            this.complete = (this.time >= this.duration);
            float percent;
            if (this.complete) {
                percent = 1.0f;
            }
            else {
                percent = this.time / this.duration;
                if (this.interpolation != null) {
                    percent = this.interpolation.apply(percent);
                }
            }
            this.update(this.reverse ? (1.0f - percent) : percent);
            if (this.complete) {
                this.end();
            }
            return this.complete;
        }
        finally {
            this.setPool(pool);
        }
    }
    
    protected void begin() {
    }
    
    protected void end() {
    }
    
    protected abstract void update(final float p0);
    
    public void finish() {
        this.time = this.duration;
    }
    
    @Override
    public void restart() {
        this.time = 0.0f;
        this.began = false;
        this.complete = false;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.reverse = false;
        this.interpolation = null;
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
    
    public Interpolation getInterpolation() {
        return this.interpolation;
    }
    
    public void setInterpolation(final Interpolation interpolation) {
        this.interpolation = interpolation;
    }
    
    public boolean isReverse() {
        return this.reverse;
    }
    
    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }
}
