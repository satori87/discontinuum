// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class Image extends Widget
{
    private Scaling scaling;
    private int align;
    private float imageX;
    private float imageY;
    private float imageWidth;
    private float imageHeight;
    private Drawable drawable;
    
    public Image() {
        this((Drawable)null);
    }
    
    public Image(final NinePatch patch) {
        this(new NinePatchDrawable(patch), Scaling.stretch, 1);
    }
    
    public Image(final TextureRegion region) {
        this(new TextureRegionDrawable(region), Scaling.stretch, 1);
    }
    
    public Image(final Texture texture) {
        this(new TextureRegionDrawable(new TextureRegion(texture)));
    }
    
    public Image(final Skin skin, final String drawableName) {
        this(skin.getDrawable(drawableName), Scaling.stretch, 1);
    }
    
    public Image(final Drawable drawable) {
        this(drawable, Scaling.stretch, 1);
    }
    
    public Image(final Drawable drawable, final Scaling scaling) {
        this(drawable, scaling, 1);
    }
    
    public Image(final Drawable drawable, final Scaling scaling, final int align) {
        this.align = 1;
        this.setDrawable(drawable);
        this.scaling = scaling;
        this.align = align;
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }
    
    @Override
    public void layout() {
        if (this.drawable == null) {
            return;
        }
        final float regionWidth = this.drawable.getMinWidth();
        final float regionHeight = this.drawable.getMinHeight();
        final float width = this.getWidth();
        final float height = this.getHeight();
        final Vector2 size = this.scaling.apply(regionWidth, regionHeight, width, height);
        this.imageWidth = size.x;
        this.imageHeight = size.y;
        if ((this.align & 0x8) != 0x0) {
            this.imageX = 0.0f;
        }
        else if ((this.align & 0x10) != 0x0) {
            this.imageX = (float)(int)(width - this.imageWidth);
        }
        else {
            this.imageX = (float)(int)(width / 2.0f - this.imageWidth / 2.0f);
        }
        if ((this.align & 0x2) != 0x0) {
            this.imageY = (float)(int)(height - this.imageHeight);
        }
        else if ((this.align & 0x4) != 0x0) {
            this.imageY = 0.0f;
        }
        else {
            this.imageY = (float)(int)(height / 2.0f - this.imageHeight / 2.0f);
        }
    }
    
    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        this.validate();
        final Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        final float x = this.getX();
        final float y = this.getY();
        final float scaleX = this.getScaleX();
        final float scaleY = this.getScaleY();
        if (this.drawable instanceof TransformDrawable) {
            final float rotation = this.getRotation();
            if (scaleX != 1.0f || scaleY != 1.0f || rotation != 0.0f) {
                ((TransformDrawable)this.drawable).draw(batch, x + this.imageX, y + this.imageY, this.getOriginX() - this.imageX, this.getOriginY() - this.imageY, this.imageWidth, this.imageHeight, scaleX, scaleY, rotation);
                return;
            }
        }
        if (this.drawable != null) {
            this.drawable.draw(batch, x + this.imageX, y + this.imageY, this.imageWidth * scaleX, this.imageHeight * scaleY);
        }
    }
    
    public void setDrawable(final Skin skin, final String drawableName) {
        this.setDrawable(skin.getDrawable(drawableName));
    }
    
    public void setDrawable(final Drawable drawable) {
        if (this.drawable == drawable) {
            return;
        }
        if (drawable != null) {
            if (this.getPrefWidth() != drawable.getMinWidth() || this.getPrefHeight() != drawable.getMinHeight()) {
                this.invalidateHierarchy();
            }
        }
        else {
            this.invalidateHierarchy();
        }
        this.drawable = drawable;
    }
    
    public Drawable getDrawable() {
        return this.drawable;
    }
    
    public void setScaling(final Scaling scaling) {
        if (scaling == null) {
            throw new IllegalArgumentException("scaling cannot be null.");
        }
        this.scaling = scaling;
        this.invalidate();
    }
    
    public void setAlign(final int align) {
        this.align = align;
        this.invalidate();
    }
    
    @Override
    public float getMinWidth() {
        return 0.0f;
    }
    
    @Override
    public float getMinHeight() {
        return 0.0f;
    }
    
    @Override
    public float getPrefWidth() {
        if (this.drawable != null) {
            return this.drawable.getMinWidth();
        }
        return 0.0f;
    }
    
    @Override
    public float getPrefHeight() {
        if (this.drawable != null) {
            return this.drawable.getMinHeight();
        }
        return 0.0f;
    }
    
    public float getImageX() {
        return this.imageX;
    }
    
    public float getImageY() {
        return this.imageY;
    }
    
    public float getImageWidth() {
        return this.imageWidth;
    }
    
    public float getImageHeight() {
        return this.imageHeight;
    }
}
