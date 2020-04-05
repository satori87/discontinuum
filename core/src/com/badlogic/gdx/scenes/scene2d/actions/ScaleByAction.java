// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.actions;

public class ScaleByAction extends RelativeTemporalAction
{
    private float amountX;
    private float amountY;
    
    @Override
    protected void updateRelative(final float percentDelta) {
        this.target.scaleBy(this.amountX * percentDelta, this.amountY * percentDelta);
    }
    
    public void setAmount(final float x, final float y) {
        this.amountX = x;
        this.amountY = y;
    }
    
    public void setAmount(final float scale) {
        this.amountX = scale;
        this.amountY = scale;
    }
    
    public float getAmountX() {
        return this.amountX;
    }
    
    public void setAmountX(final float x) {
        this.amountX = x;
    }
    
    public float getAmountY() {
        return this.amountY;
    }
    
    public void setAmountY(final float y) {
        this.amountY = y;
    }
}
