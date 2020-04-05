// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;

public class TiledDrawable extends TextureRegionDrawable
{
    private static final Color temp;
    private final Color color;
    
    static {
        temp = new Color();
    }
    
    public TiledDrawable() {
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public TiledDrawable(final TextureRegion region) {
        super(region);
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public TiledDrawable(final TextureRegionDrawable drawable) {
        super(drawable);
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override
    public void draw(final Batch batch, float x, float y, final float width, final float height) {
        final Color batchColor = batch.getColor();
        TiledDrawable.temp.set(batchColor);
        batch.setColor(batchColor.mul(this.color));
        final TextureRegion region = this.getRegion();
        final float regionWidth = (float)region.getRegionWidth();
        final float regionHeight = (float)region.getRegionHeight();
        final int fullX = (int)(width / regionWidth);
        final int fullY = (int)(height / regionHeight);
        final float remainingX = width - regionWidth * fullX;
        final float remainingY = height - regionHeight * fullY;
        final float startX = x;
        final float startY = y;
        final float endX = x + width - remainingX;
        final float endY = y + height - remainingY;
        for (int i = 0; i < fullX; ++i) {
            y = startY;
            for (int ii = 0; ii < fullY; ++ii) {
                batch.draw(region, x, y, regionWidth, regionHeight);
                y += regionHeight;
            }
            x += regionWidth;
        }
        final Texture texture = region.getTexture();
        final float u = region.getU();
        final float v2 = region.getV2();
        if (remainingX > 0.0f) {
            final float u2 = u + remainingX / texture.getWidth();
            float v3 = region.getV();
            y = startY;
            for (int ii2 = 0; ii2 < fullY; ++ii2) {
                batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v3);
                y += regionHeight;
            }
            if (remainingY > 0.0f) {
                v3 = v2 - remainingY / texture.getHeight();
                batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v3);
            }
        }
        if (remainingY > 0.0f) {
            final float u2 = region.getU2();
            final float v3 = v2 - remainingY / texture.getHeight();
            x = startX;
            for (int j = 0; j < fullX; ++j) {
                batch.draw(texture, x, y, regionWidth, remainingY, u, v2, u2, v3);
                x += regionWidth;
            }
        }
        batch.setColor(TiledDrawable.temp);
    }
    
    @Override
    public void draw(final Batch batch, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        throw new UnsupportedOperationException();
    }
    
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public TiledDrawable tint(final Color tint) {
        final TiledDrawable drawable = new TiledDrawable(this);
        drawable.color.set(tint);
        drawable.setLeftWidth(this.getLeftWidth());
        drawable.setRightWidth(this.getRightWidth());
        drawable.setTopHeight(this.getTopHeight());
        drawable.setBottomHeight(this.getBottomHeight());
        return drawable;
    }
}
