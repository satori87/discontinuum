// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Scaling;

public class FitViewport extends ScalingViewport
{
    public FitViewport(final float worldWidth, final float worldHeight) {
        super(Scaling.fit, worldWidth, worldHeight);
    }
    
    public FitViewport(final float worldWidth, final float worldHeight, final Camera camera) {
        super(Scaling.fit, worldWidth, worldHeight, camera);
    }
}
