// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.math.Vector2;

public final class Box2dSteeringUtils
{
    private Box2dSteeringUtils() {
    }
    
    public static float vectorToAngle(final Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }
    
    public static Vector2 angleToVector(final Vector2 outVector, final float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }
}
