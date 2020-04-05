// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Scaling;

public class StretchViewport extends ScalingViewport
{
    public StretchViewport(final float worldWidth, final float worldHeight) {
        super(Scaling.stretch, worldWidth, worldHeight);
    }
    
    public StretchViewport(final float worldWidth, final float worldHeight, final Camera camera) {
        super(Scaling.stretch, worldWidth, worldHeight, camera);
    }
}
