// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.MapObject;

public class RectangleMapObject extends MapObject
{
    private Rectangle rectangle;
    
    public Rectangle getRectangle() {
        return this.rectangle;
    }
    
    public RectangleMapObject() {
        this(0.0f, 0.0f, 1.0f, 1.0f);
    }
    
    public RectangleMapObject(final float x, final float y, final float width, final float height) {
        this.rectangle = new Rectangle(x, y, width, height);
    }
}
