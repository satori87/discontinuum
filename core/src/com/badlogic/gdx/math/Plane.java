// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.io.Serializable;

public class Plane implements Serializable
{
    private static final long serialVersionUID = -1240652082930747866L;
    public final Vector3 normal;
    public float d;
    
    public Plane() {
        this.normal = new Vector3();
        this.d = 0.0f;
    }
    
    public Plane(final Vector3 normal, final float d) {
        this.normal = new Vector3();
        this.d = 0.0f;
        this.normal.set(normal).nor();
        this.d = d;
    }
    
    public Plane(final Vector3 normal, final Vector3 point) {
        this.normal = new Vector3();
        this.d = 0.0f;
        this.normal.set(normal).nor();
        this.d = -this.normal.dot(point);
    }
    
    public Plane(final Vector3 point1, final Vector3 point2, final Vector3 point3) {
        this.normal = new Vector3();
        this.d = 0.0f;
        this.set(point1, point2, point3);
    }
    
    public void set(final Vector3 point1, final Vector3 point2, final Vector3 point3) {
        this.normal.set(point1).sub(point2).crs(point2.x - point3.x, point2.y - point3.y, point2.z - point3.z).nor();
        this.d = -point1.dot(this.normal);
    }
    
    public void set(final float nx, final float ny, final float nz, final float d) {
        this.normal.set(nx, ny, nz);
        this.d = d;
    }
    
    public float distance(final Vector3 point) {
        return this.normal.dot(point) + this.d;
    }
    
    public PlaneSide testPoint(final Vector3 point) {
        final float dist = this.normal.dot(point) + this.d;
        if (dist == 0.0f) {
            return PlaneSide.OnPlane;
        }
        if (dist < 0.0f) {
            return PlaneSide.Back;
        }
        return PlaneSide.Front;
    }
    
    public PlaneSide testPoint(final float x, final float y, final float z) {
        final float dist = this.normal.dot(x, y, z) + this.d;
        if (dist == 0.0f) {
            return PlaneSide.OnPlane;
        }
        if (dist < 0.0f) {
            return PlaneSide.Back;
        }
        return PlaneSide.Front;
    }
    
    public boolean isFrontFacing(final Vector3 direction) {
        final float dot = this.normal.dot(direction);
        return dot <= 0.0f;
    }
    
    public Vector3 getNormal() {
        return this.normal;
    }
    
    public float getD() {
        return this.d;
    }
    
    public void set(final Vector3 point, final Vector3 normal) {
        this.normal.set(normal);
        this.d = -point.dot(normal);
    }
    
    public void set(final float pointX, final float pointY, final float pointZ, final float norX, final float norY, final float norZ) {
        this.normal.set(norX, norY, norZ);
        this.d = -(pointX * norX + pointY * norY + pointZ * norZ);
    }
    
    public void set(final Plane plane) {
        this.normal.set(plane.normal);
        this.d = plane.d;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.normal.toString()) + ", " + this.d;
    }
    
    public enum PlaneSide
    {
        OnPlane("OnPlane", 0), 
        Back("Back", 1), 
        Front("Front", 2);
        
        private PlaneSide(final String name, final int ordinal) {
        }
    }
}
