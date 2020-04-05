// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Ray implements Serializable
{
    private static final long serialVersionUID = -620692054835390878L;
    public final Vector3 origin;
    public final Vector3 direction;
    static Vector3 tmp;
    
    static {
        Ray.tmp = new Vector3();
    }
    
    public Ray() {
        this.origin = new Vector3();
        this.direction = new Vector3();
    }
    
    public Ray(final Vector3 origin, final Vector3 direction) {
        this.origin = new Vector3();
        this.direction = new Vector3();
        this.origin.set(origin);
        this.direction.set(direction).nor();
    }
    
    public Ray cpy() {
        return new Ray(this.origin, this.direction);
    }
    
    public Vector3 getEndPoint(final Vector3 out, final float distance) {
        return out.set(this.direction).scl(distance).add(this.origin);
    }
    
    public Ray mul(final Matrix4 matrix) {
        Ray.tmp.set(this.origin).add(this.direction);
        Ray.tmp.mul(matrix);
        this.origin.mul(matrix);
        this.direction.set(Ray.tmp.sub(this.origin));
        return this;
    }
    
    @Override
    public String toString() {
        return "ray [" + this.origin + ":" + this.direction + "]";
    }
    
    public Ray set(final Vector3 origin, final Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction);
        return this;
    }
    
    public Ray set(final float x, final float y, final float z, final float dx, final float dy, final float dz) {
        this.origin.set(x, y, z);
        this.direction.set(dx, dy, dz);
        return this;
    }
    
    public Ray set(final Ray ray) {
        this.origin.set(ray.origin);
        this.direction.set(ray.direction);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        final Ray r = (Ray)o;
        return this.direction.equals(r.direction) && this.origin.equals(r.origin);
    }
    
    @Override
    public int hashCode() {
        final int prime = 73;
        int result = 1;
        result = 73 * result + this.direction.hashCode();
        result = 73 * result + this.origin.hashCode();
        return result;
    }
}
