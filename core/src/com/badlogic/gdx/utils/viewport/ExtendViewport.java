// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ExtendViewport extends Viewport
{
    private float minWorldWidth;
    private float minWorldHeight;
    private float maxWorldWidth;
    private float maxWorldHeight;
    
    public ExtendViewport(final float minWorldWidth, final float minWorldHeight) {
        this(minWorldWidth, minWorldHeight, 0.0f, 0.0f, new OrthographicCamera());
    }
    
    public ExtendViewport(final float minWorldWidth, final float minWorldHeight, final Camera camera) {
        this(minWorldWidth, minWorldHeight, 0.0f, 0.0f, camera);
    }
    
    public ExtendViewport(final float minWorldWidth, final float minWorldHeight, final float maxWorldWidth, final float maxWorldHeight) {
        this(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, new OrthographicCamera());
    }
    
    public ExtendViewport(final float minWorldWidth, final float minWorldHeight, final float maxWorldWidth, final float maxWorldHeight, final Camera camera) {
        this.minWorldWidth = minWorldWidth;
        this.minWorldHeight = minWorldHeight;
        this.maxWorldWidth = maxWorldWidth;
        this.maxWorldHeight = maxWorldHeight;
        this.setCamera(camera);
    }
    
    @Override
    public void update(final int screenWidth, final int screenHeight, final boolean centerCamera) {
        float worldWidth = this.minWorldWidth;
        float worldHeight = this.minWorldHeight;
        final Vector2 scaled = Scaling.fit.apply(worldWidth, worldHeight, (float)screenWidth, (float)screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        if (viewportWidth < screenWidth) {
            final float toViewportSpace = viewportHeight / worldHeight;
            final float toWorldSpace = worldHeight / viewportHeight;
            float lengthen = (screenWidth - viewportWidth) * toWorldSpace;
            if (this.maxWorldWidth > 0.0f) {
                lengthen = Math.min(lengthen, this.maxWorldWidth - this.minWorldWidth);
            }
            worldWidth += lengthen;
            viewportWidth += Math.round(lengthen * toViewportSpace);
        }
        else if (viewportHeight < screenHeight) {
            final float toViewportSpace = viewportWidth / worldWidth;
            final float toWorldSpace = worldWidth / viewportWidth;
            float lengthen = (screenHeight - viewportHeight) * toWorldSpace;
            if (this.maxWorldHeight > 0.0f) {
                lengthen = Math.min(lengthen, this.maxWorldHeight - this.minWorldHeight);
            }
            worldHeight += lengthen;
            viewportHeight += Math.round(lengthen * toViewportSpace);
        }
        this.setWorldSize(worldWidth, worldHeight);
        this.setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
        this.apply(centerCamera);
    }
    
    public float getMinWorldWidth() {
        return this.minWorldWidth;
    }
    
    public void setMinWorldWidth(final float minWorldWidth) {
        this.minWorldWidth = minWorldWidth;
    }
    
    public float getMinWorldHeight() {
        return this.minWorldHeight;
    }
    
    public void setMinWorldHeight(final float minWorldHeight) {
        this.minWorldHeight = minWorldHeight;
    }
    
    public float getMaxWorldWidth() {
        return this.maxWorldWidth;
    }
    
    public void setMaxWorldWidth(final float maxWorldWidth) {
        this.maxWorldWidth = maxWorldWidth;
    }
    
    public float getMaxWorldHeight() {
        return this.maxWorldHeight;
    }
    
    public void setMaxWorldHeight(final float maxWorldHeight) {
        this.maxWorldHeight = maxWorldHeight;
    }
}
