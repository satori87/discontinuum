// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ScreenViewport extends Viewport
{
    private float unitsPerPixel;
    
    public ScreenViewport() {
        this(new OrthographicCamera());
    }
    
    public ScreenViewport(final Camera camera) {
        this.unitsPerPixel = 1.0f;
        this.setCamera(camera);
    }
    
    @Override
    public void update(final int screenWidth, final int screenHeight, final boolean centerCamera) {
        this.setScreenBounds(0, 0, screenWidth, screenHeight);
        this.setWorldSize(screenWidth * this.unitsPerPixel, screenHeight * this.unitsPerPixel);
        this.apply(centerCamera);
    }
    
    public float getUnitsPerPixel() {
        return this.unitsPerPixel;
    }
    
    public void setUnitsPerPixel(final float unitsPerPixel) {
        this.unitsPerPixel = unitsPerPixel;
    }
}
