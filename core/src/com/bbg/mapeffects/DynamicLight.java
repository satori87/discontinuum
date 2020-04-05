// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.mapeffects;

import com.badlogic.gdx.graphics.Color;
import box2dLight.RayHandler;
import com.bbg.dc.DCGame;
import box2dLight.PointLight;

public class DynamicLight extends PointLight
{
    DCGame game;
    LightManager lightMan;
    long stamp;
    boolean flickers;
    boolean flicker;
    float distance;
    long flickerAt;
    
    public DynamicLight(final DCGame game, final LightManager lightMan, final boolean flickers, final RayHandler rayHandler, final int rays, final Color color, final float distance, final float x, final float y) {
        super(rayHandler, rays, color, distance, x, y);
        this.stamp = 0L;
        this.flickers = false;
        this.flicker = false;
        this.distance = 0.0f;
        this.flickerAt = 0L;
        this.game = game;
        this.lightMan = lightMan;
        this.flickers = flickers;
        this.distance = distance;
    }
    
    public void update() {
        float newDist = 0.0f;
        if (this.game.tick > this.stamp) {
            this.stamp = this.game.tick + 200L;
            if (this.flickers && this.game.tick > this.flickerAt) {
                this.flicker = !this.flicker;
                if (this.flicker) {
                    newDist = this.distance * (0.3f + (float)(Math.random() / 10.0));
                    this.setDistance(this.distance + newDist);
                }
                this.flickerAt = this.game.tick + 200L + (int)(Math.random() * 150.0);
            }
        }
        super.update();
    }
}
