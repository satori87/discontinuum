// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.io.Serializable;

public class GridPoint3 implements Serializable
{
    private static final long serialVersionUID = 5922187982746752830L;
    public int x;
    public int y;
    public int z;
    
    public GridPoint3() {
    }
    
    public GridPoint3(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public GridPoint3(final GridPoint3 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }
    
    public GridPoint3 set(final GridPoint3 point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        return this;
    }
    
    public GridPoint3 set(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public float dst2(final GridPoint3 other) {
        final int xd = other.x - this.x;
        final int yd = other.y - this.y;
        final int zd = other.z - this.z;
        return (float)(xd * xd + yd * yd + zd * zd);
    }
    
    public float dst2(final int x, final int y, final int z) {
        final int xd = x - this.x;
        final int yd = y - this.y;
        final int zd = z - this.z;
        return (float)(xd * xd + yd * yd + zd * zd);
    }
    
    public float dst(final GridPoint3 other) {
        final int xd = other.x - this.x;
        final int yd = other.y - this.y;
        final int zd = other.z - this.z;
        return (float)Math.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    public float dst(final int x, final int y, final int z) {
        final int xd = x - this.x;
        final int yd = y - this.y;
        final int zd = z - this.z;
        return (float)Math.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    public GridPoint3 add(final GridPoint3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
    
    public GridPoint3 add(final int x, final int y, final int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    
    public GridPoint3 sub(final GridPoint3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
    
    public GridPoint3 sub(final int x, final int y, final int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    
    public GridPoint3 cpy() {
        return new GridPoint3(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final GridPoint3 g = (GridPoint3)o;
        return this.x == g.x && this.y == g.y && this.z == g.z;
    }
    
    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = 17 * result + this.x;
        result = 17 * result + this.y;
        result = 17 * result + this.z;
        return result;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
