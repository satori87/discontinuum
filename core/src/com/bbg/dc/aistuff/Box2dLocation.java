// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.utils.Location;

public class Box2dLocation implements Location<Vector2>
{
    public Vector2 position;
    float orientation;
    
    public Box2dLocation() {
        this.position = new Vector2();
        this.orientation = 0.0f;
    }
    
    public Box2dLocation(final float x, final float y) {
        this.position = new Vector2(x / 100.0f, y / 100.0f);
        this.orientation = 0.0f;
    }
    
    public Vector2 getPosition() {
        return this.position;
    }
    
    public float getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final float orientation) {
        this.orientation = orientation;
    }
    
    public Location<Vector2> newLocation() {
        return (Location<Vector2>)new Box2dLocation();
    }
    
    public float vectorToAngle(final Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }
    
    public Vector2 angleToVector(final Vector2 outVector, final float angle) {
        return Box2dSteeringUtils.angleToVector(outVector, angle);
    }
}
