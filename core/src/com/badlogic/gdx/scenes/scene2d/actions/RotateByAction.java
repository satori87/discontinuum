// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class RotateByAction extends RelativeTemporalAction
{
    private float amount;
    
    @Override
    protected void updateRelative(final float percentDelta) {
        this.target.rotateBy(this.amount * percentDelta);
    }
    
    public float getAmount() {
        return this.amount;
    }
    
    public void setAmount(final float rotationAmount) {
        this.amount = rotationAmount;
    }
}
