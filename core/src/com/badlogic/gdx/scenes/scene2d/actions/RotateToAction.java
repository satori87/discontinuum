// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.math.MathUtils;

public class RotateToAction extends TemporalAction
{
    private float start;
    private float end;
    private boolean useShortestDirection;
    
    public RotateToAction() {
        this.useShortestDirection = false;
    }
    
    public RotateToAction(final boolean useShortestDirection) {
        this.useShortestDirection = false;
        this.useShortestDirection = useShortestDirection;
    }
    
    @Override
    protected void begin() {
        this.start = this.target.getRotation();
    }
    
    @Override
    protected void update(final float percent) {
        if (this.useShortestDirection) {
            this.target.setRotation(MathUtils.lerpAngleDeg(this.start, this.end, percent));
        }
        else {
            this.target.setRotation(this.start + (this.end - this.start) * percent);
        }
    }
    
    public float getRotation() {
        return this.end;
    }
    
    public void setRotation(final float rotation) {
        this.end = rotation;
    }
    
    public boolean isUseShortestDirection() {
        return this.useShortestDirection;
    }
    
    public void setUseShortestDirection(final boolean useShortestDirection) {
        this.useShortestDirection = useShortestDirection;
    }
}
