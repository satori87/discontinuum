// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Ellipse implements Serializable, Shape2D
{
    public float x;
    public float y;
    public float width;
    public float height;
    private static final long serialVersionUID = 7381533206532032099L;
    
    public Ellipse() {
    }
    
    public Ellipse(final Ellipse ellipse) {
        this.x = ellipse.x;
        this.y = ellipse.y;
        this.width = ellipse.width;
        this.height = ellipse.height;
    }
    
    public Ellipse(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Ellipse(final Vector2 position, final float width, final float height) {
        this.x = position.x;
        this.y = position.y;
        this.width = width;
        this.height = height;
    }
    
    public Ellipse(final Vector2 position, final Vector2 size) {
        this.x = position.x;
        this.y = position.y;
        this.width = size.x;
        this.height = size.y;
    }
    
    public Ellipse(final Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.width = circle.radius * 2.0f;
        this.height = circle.radius * 2.0f;
    }
    
    @Override
    public boolean contains(float x, float y) {
        x -= this.x;
        y -= this.y;
        return x * x / (this.width * 0.5f * this.width * 0.5f) + y * y / (this.height * 0.5f * this.height * 0.5f) <= 1.0f;
    }
    
    @Override
    public boolean contains(final Vector2 point) {
        return this.contains(point.x, point.y);
    }
    
    public void set(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void set(final Ellipse ellipse) {
        this.x = ellipse.x;
        this.y = ellipse.y;
        this.width = ellipse.width;
        this.height = ellipse.height;
    }
    
    public void set(final Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.width = circle.radius * 2.0f;
        this.height = circle.radius * 2.0f;
    }
    
    public void set(final Vector2 position, final Vector2 size) {
        this.x = position.x;
        this.y = position.y;
        this.width = size.x;
        this.height = size.y;
    }
    
    public Ellipse setPosition(final Vector2 position) {
        this.x = position.x;
        this.y = position.y;
        return this;
    }
    
    public Ellipse setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Ellipse setSize(final float width, final float height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    public float area() {
        return 3.1415927f * (this.width * this.height) / 4.0f;
    }
    
    public float circumference() {
        final float a = this.width / 2.0f;
        final float b = this.height / 2.0f;
        if (a * 3.0f > b || b * 3.0f > a) {
            return (float)(3.1415927410125732 * (3.0f * (a + b) - Math.sqrt((3.0f * a + b) * (a + 3.0f * b))));
        }
        return (float)(6.2831854820251465 * Math.sqrt((a * a + b * b) / 2.0f));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Ellipse e = (Ellipse)o;
        return this.x == e.x && this.y == e.y && this.width == e.width && this.height == e.height;
    }
    
    @Override
    public int hashCode() {
        final int prime = 53;
        int result = 1;
        result = 53 * result + NumberUtils.floatToRawIntBits(this.height);
        result = 53 * result + NumberUtils.floatToRawIntBits(this.width);
        result = 53 * result + NumberUtils.floatToRawIntBits(this.x);
        result = 53 * result + NumberUtils.floatToRawIntBits(this.y);
        return result;
    }
}
