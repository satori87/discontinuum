// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class NinePatchDrawable extends BaseDrawable implements TransformDrawable
{
    private NinePatch patch;
    
    public NinePatchDrawable() {
    }
    
    public NinePatchDrawable(final NinePatch patch) {
        this.setPatch(patch);
    }
    
    public NinePatchDrawable(final NinePatchDrawable drawable) {
        super(drawable);
        this.setPatch(drawable.patch);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float width, final float height) {
        this.patch.draw(batch, x, y, width, height);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        this.patch.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }
    
    public void setPatch(final NinePatch patch) {
        this.patch = patch;
        this.setMinWidth(patch.getTotalWidth());
        this.setMinHeight(patch.getTotalHeight());
        this.setTopHeight(patch.getPadTop());
        this.setRightWidth(patch.getPadRight());
        this.setBottomHeight(patch.getPadBottom());
        this.setLeftWidth(patch.getPadLeft());
    }
    
    public NinePatch getPatch() {
        return this.patch;
    }
    
    public NinePatchDrawable tint(final Color tint) {
        final NinePatchDrawable drawable = new NinePatchDrawable(this);
        drawable.setPatch(new NinePatch(drawable.getPatch(), tint));
        return drawable;
    }
}
