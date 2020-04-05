// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class SizeByAction extends RelativeTemporalAction
{
    private float amountWidth;
    private float amountHeight;
    
    @Override
    protected void updateRelative(final float percentDelta) {
        this.target.sizeBy(this.amountWidth * percentDelta, this.amountHeight * percentDelta);
    }
    
    public void setAmount(final float width, final float height) {
        this.amountWidth = width;
        this.amountHeight = height;
    }
    
    public float getAmountWidth() {
        return this.amountWidth;
    }
    
    public void setAmountWidth(final float width) {
        this.amountWidth = width;
    }
    
    public float getAmountHeight() {
        return this.amountHeight;
    }
    
    public void setAmountHeight(final float height) {
        this.amountHeight = height;
    }
}
