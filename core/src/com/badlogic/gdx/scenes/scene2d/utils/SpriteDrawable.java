// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Color;

public class SpriteDrawable extends BaseDrawable implements TransformDrawable
{
    private static final Color temp;
    private Sprite sprite;
    
    static {
        temp = new Color();
    }
    
    public SpriteDrawable() {
    }
    
    public SpriteDrawable(final Sprite sprite) {
        this.setSprite(sprite);
    }
    
    public SpriteDrawable(final SpriteDrawable drawable) {
        super(drawable);
        this.setSprite(drawable.sprite);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float width, final float height) {
        final Color spriteColor = this.sprite.getColor();
        SpriteDrawable.temp.set(spriteColor);
        this.sprite.setColor(spriteColor.mul(batch.getColor()));
        this.sprite.setRotation(0.0f);
        this.sprite.setScale(1.0f, 1.0f);
        this.sprite.setBounds(x, y, width, height);
        this.sprite.draw(batch);
        this.sprite.setColor(SpriteDrawable.temp);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        final Color spriteColor = this.sprite.getColor();
        SpriteDrawable.temp.set(spriteColor);
        this.sprite.setColor(spriteColor.mul(batch.getColor()));
        this.sprite.setOrigin(originX, originY);
        this.sprite.setRotation(rotation);
        this.sprite.setScale(scaleX, scaleY);
        this.sprite.setBounds(x, y, width, height);
        this.sprite.draw(batch);
        this.sprite.setColor(SpriteDrawable.temp);
    }
    
    public void setSprite(final Sprite sprite) {
        this.sprite = sprite;
        this.setMinWidth(sprite.getWidth());
        this.setMinHeight(sprite.getHeight());
    }
    
    public Sprite getSprite() {
        return this.sprite;
    }
    
    public SpriteDrawable tint(final Color tint) {
        Sprite newSprite;
        if (this.sprite instanceof TextureAtlas.AtlasSprite) {
            newSprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasSprite)this.sprite);
        }
        else {
            newSprite = new Sprite(this.sprite);
        }
        newSprite.setColor(tint);
        newSprite.setSize(this.getMinWidth(), this.getMinHeight());
        final SpriteDrawable drawable = new SpriteDrawable(newSprite);
        drawable.setLeftWidth(this.getLeftWidth());
        drawable.setRightWidth(this.getRightWidth());
        drawable.setTopHeight(this.getTopHeight());
        drawable.setBottomHeight(this.getBottomHeight());
        return drawable;
    }
}
