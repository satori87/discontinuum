// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionDrawable extends BaseDrawable implements TransformDrawable
{
    private TextureRegion region;
    
    public TextureRegionDrawable() {
    }
    
    public TextureRegionDrawable(final Texture texture) {
        this.setRegion(new TextureRegion(texture));
    }
    
    public TextureRegionDrawable(final TextureRegion region) {
        this.setRegion(region);
    }
    
    public TextureRegionDrawable(final TextureRegionDrawable drawable) {
        super(drawable);
        this.setRegion(drawable.region);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float width, final float height) {
        batch.draw(this.region, x, y, width, height);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        batch.draw(this.region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }
    
    public void setRegion(final TextureRegion region) {
        this.region = region;
        if (region != null) {
            this.setMinWidth((float)region.getRegionWidth());
            this.setMinHeight((float)region.getRegionHeight());
        }
    }
    
    public TextureRegion getRegion() {
        return this.region;
    }
    
    public Drawable tint(final Color tint) {
        Sprite sprite;
        if (this.region instanceof TextureAtlas.AtlasRegion) {
            sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion)this.region);
        }
        else {
            sprite = new Sprite(this.region);
        }
        sprite.setColor(tint);
        sprite.setSize(this.getMinWidth(), this.getMinHeight());
        final SpriteDrawable drawable = new SpriteDrawable(sprite);
        drawable.setLeftWidth(this.getLeftWidth());
        drawable.setRightWidth(this.getRightWidth());
        drawable.setTopHeight(this.getTopHeight());
        drawable.setBottomHeight(this.getBottomHeight());
        return drawable;
    }
}
