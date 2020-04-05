// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Scaling;

public class ScalingViewport extends Viewport
{
    private Scaling scaling;
    
    public ScalingViewport(final Scaling scaling, final float worldWidth, final float worldHeight) {
        this(scaling, worldWidth, worldHeight, new OrthographicCamera());
    }
    
    public ScalingViewport(final Scaling scaling, final float worldWidth, final float worldHeight, final Camera camera) {
        this.scaling = scaling;
        this.setWorldSize(worldWidth, worldHeight);
        this.setCamera(camera);
    }
    
    @Override
    public void update(final int screenWidth, final int screenHeight, final boolean centerCamera) {
        final Vector2 scaled = this.scaling.apply(this.getWorldWidth(), this.getWorldHeight(), (float)screenWidth, (float)screenHeight);
        final int viewportWidth = Math.round(scaled.x);
        final int viewportHeight = Math.round(scaled.y);
        this.setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
        this.apply(centerCamera);
    }
    
    public Scaling getScaling() {
        return this.scaling;
    }
    
    public void setScaling(final Scaling scaling) {
        this.scaling = scaling;
    }
}
