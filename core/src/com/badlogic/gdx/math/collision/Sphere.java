// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Sphere implements Serializable
{
    private static final long serialVersionUID = -6487336868908521596L;
    public float radius;
    public final Vector3 center;
    private static final float PI_4_3 = 4.1887903f;
    
    public Sphere(final Vector3 center, final float radius) {
        this.center = new Vector3(center);
        this.radius = radius;
    }
    
    public boolean overlaps(final Sphere sphere) {
        return this.center.dst2(sphere.center) < (this.radius + sphere.radius) * (this.radius + sphere.radius);
    }
    
    @Override
    public int hashCode() {
        final int prime = 71;
        int result = 1;
        result = 71 * result + this.center.hashCode();
        result = 71 * result + NumberUtils.floatToRawIntBits(this.radius);
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Sphere s = (Sphere)o;
        return this.radius == s.radius && this.center.equals(s.center);
    }
    
    public float volume() {
        return 4.1887903f * this.radius * this.radius * this.radius;
    }
    
    public float surfaceArea() {
        return 12.566371f * this.radius * this.radius;
    }
}
