// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class FPSLogger
{
    long startTime;
    
    public FPSLogger() {
        this.startTime = TimeUtils.nanoTime();
    }
    
    public void log() {
        if (TimeUtils.nanoTime() - this.startTime > 1000000000L) {
            Gdx.app.log("FPSLogger", "fps: " + Gdx.graphics.getFramesPerSecond());
            this.startTime = TimeUtils.nanoTime();
        }
    }
}
