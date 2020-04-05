// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public abstract class RelativeTemporalAction extends TemporalAction
{
    private float lastPercent;
    
    @Override
    protected void begin() {
        this.lastPercent = 0.0f;
    }
    
    @Override
    protected void update(final float percent) {
        this.updateRelative(percent - this.lastPercent);
        this.lastPercent = percent;
    }
    
    protected abstract void updateRelative(final float p0);
}
