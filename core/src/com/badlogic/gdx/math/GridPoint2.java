// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.io.Serializable;

public class GridPoint2 implements Serializable
{
    private static final long serialVersionUID = -4019969926331717380L;
    public int x;
    public int y;
    
    public GridPoint2() {
    }
    
    public GridPoint2(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public GridPoint2(final GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
    }
    
    public GridPoint2 set(final GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
        return this;
    }
    
    public GridPoint2 set(final int x, final int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public float dst2(final GridPoint2 other) {
        final int xd = other.x - this.x;
        final int yd = other.y - this.y;
        return (float)(xd * xd + yd * yd);
    }
    
    public float dst2(final int x, final int y) {
        final int xd = x - this.x;
        final int yd = y - this.y;
        return (float)(xd * xd + yd * yd);
    }
    
    public float dst(final GridPoint2 other) {
        final int xd = other.x - this.x;
        final int yd = other.y - this.y;
        return (float)Math.sqrt(xd * xd + yd * yd);
    }
    
    public float dst(final int x, final int y) {
        final int xd = x - this.x;
        final int yd = y - this.y;
        return (float)Math.sqrt(xd * xd + yd * yd);
    }
    
    public GridPoint2 add(final GridPoint2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    public GridPoint2 add(final int x, final int y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public GridPoint2 sub(final GridPoint2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    
    public GridPoint2 sub(final int x, final int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    
    public GridPoint2 cpy() {
        return new GridPoint2(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final GridPoint2 g = (GridPoint2)o;
        return this.x == g.x && this.y == g.y;
    }
    
    @Override
    public int hashCode() {
        final int prime = 53;
        int result = 1;
        result = 53 * result + this.x;
        result = 53 * result + this.y;
        return result;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
