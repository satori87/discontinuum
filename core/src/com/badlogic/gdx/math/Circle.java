// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Circle implements Serializable, Shape2D
{
    public float x;
    public float y;
    public float radius;
    
    public Circle() {
    }
    
    public Circle(final float x, final float y, final float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    
    public Circle(final Vector2 position, final float radius) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius;
    }
    
    public Circle(final Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }
    
    public Circle(final Vector2 center, final Vector2 edge) {
        this.x = center.x;
        this.y = center.y;
        this.radius = Vector2.len(center.x - edge.x, center.y - edge.y);
    }
    
    public void set(final float x, final float y, final float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    
    public void set(final Vector2 position, final float radius) {
        this.x = position.x;
        this.y = position.y;
        this.radius = radius;
    }
    
    public void set(final Circle circle) {
        this.x = circle.x;
        this.y = circle.y;
        this.radius = circle.radius;
    }
    
    public void set(final Vector2 center, final Vector2 edge) {
        this.x = center.x;
        this.y = center.y;
        this.radius = Vector2.len(center.x - edge.x, center.y - edge.y);
    }
    
    public void setPosition(final Vector2 position) {
        this.x = position.x;
        this.y = position.y;
    }
    
    public void setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public void setRadius(final float radius) {
        this.radius = radius;
    }
    
    @Override
    public boolean contains(float x, float y) {
        x = this.x - x;
        y = this.y - y;
        return x * x + y * y <= this.radius * this.radius;
    }
    
    @Override
    public boolean contains(final Vector2 point) {
        final float dx = this.x - point.x;
        final float dy = this.y - point.y;
        return dx * dx + dy * dy <= this.radius * this.radius;
    }
    
    public boolean contains(final Circle c) {
        final float radiusDiff = this.radius - c.radius;
        if (radiusDiff < 0.0f) {
            return false;
        }
        final float dx = this.x - c.x;
        final float dy = this.y - c.y;
        final float dst = dx * dx + dy * dy;
        final float radiusSum = this.radius + c.radius;
        return radiusDiff * radiusDiff >= dst && dst < radiusSum * radiusSum;
    }
    
    public boolean overlaps(final Circle c) {
        final float dx = this.x - c.x;
        final float dy = this.y - c.y;
        final float distance = dx * dx + dy * dy;
        final float radiusSum = this.radius + c.radius;
        return distance < radiusSum * radiusSum;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.x) + "," + this.y + "," + this.radius;
    }
    
    public float circumference() {
        return this.radius * 6.2831855f;
    }
    
    public float area() {
        return this.radius * this.radius * 3.1415927f;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Circle c = (Circle)o;
        return this.x == c.x && this.y == c.y && this.radius == c.radius;
    }
    
    @Override
    public int hashCode() {
        final int prime = 41;
        int result = 1;
        result = 41 * result + NumberUtils.floatToRawIntBits(this.radius);
        result = 41 * result + NumberUtils.floatToRawIntBits(this.x);
        result = 41 * result + NumberUtils.floatToRawIntBits(this.y);
        return result;
    }
}
