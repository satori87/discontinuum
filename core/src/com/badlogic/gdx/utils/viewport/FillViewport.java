// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Scaling;

public class FillViewport extends ScalingViewport
{
    public FillViewport(final float worldWidth, final float worldHeight) {
        super(Scaling.fill, worldWidth, worldHeight);
    }
    
    public FillViewport(final float worldWidth, final float worldHeight, final Camera camera) {
        super(Scaling.fill, worldWidth, worldHeight, camera);
    }
}
