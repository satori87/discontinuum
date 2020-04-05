// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.maps.MapObject;

public class EllipseMapObject extends MapObject
{
    private Ellipse ellipse;
    
    public Ellipse getEllipse() {
        return this.ellipse;
    }
    
    public EllipseMapObject() {
        this(0.0f, 0.0f, 1.0f, 1.0f);
    }
    
    public EllipseMapObject(final float x, final float y, final float width, final float height) {
        this.ellipse = new Ellipse(x, y, width, height);
    }
}
