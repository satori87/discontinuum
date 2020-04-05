// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.maps.MapObject;

public class PolygonMapObject extends MapObject
{
    private Polygon polygon;
    
    public Polygon getPolygon() {
        return this.polygon;
    }
    
    public void setPolygon(final Polygon polygon) {
        this.polygon = polygon;
    }
    
    public PolygonMapObject() {
        this(new float[0]);
    }
    
    public PolygonMapObject(final float[] vertices) {
        this.polygon = new Polygon(vertices);
    }
    
    public PolygonMapObject(final Polygon polygon) {
        this.polygon = polygon;
    }
}
