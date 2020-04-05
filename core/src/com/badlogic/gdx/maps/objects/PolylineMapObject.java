// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.maps.MapObject;

public class PolylineMapObject extends MapObject
{
    private Polyline polyline;
    
    public Polyline getPolyline() {
        return this.polyline;
    }
    
    public void setPolyline(final Polyline polyline) {
        this.polyline = polyline;
    }
    
    public PolylineMapObject() {
        this(new float[0]);
    }
    
    public PolylineMapObject(final float[] vertices) {
        this.polyline = new Polyline(vertices);
    }
    
    public PolylineMapObject(final Polyline polyline) {
        this.polyline = polyline;
    }
}
