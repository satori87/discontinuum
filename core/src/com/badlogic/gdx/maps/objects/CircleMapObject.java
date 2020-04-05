// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.maps.MapObject;

public class CircleMapObject extends MapObject
{
    private Circle circle;
    
    public Circle getCircle() {
        return this.circle;
    }
    
    public CircleMapObject() {
        this(0.0f, 0.0f, 1.0f);
    }
    
    public CircleMapObject(final float x, final float y, final float radius) {
        this.circle = new Circle(x, y, radius);
    }
}
