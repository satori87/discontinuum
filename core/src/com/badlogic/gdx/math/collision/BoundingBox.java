// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Matrix4;
import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class BoundingBox implements Serializable
{
    private static final long serialVersionUID = -1286036817192127343L;
    private static final Vector3 tmpVector;
    public final Vector3 min;
    public final Vector3 max;
    private final Vector3 cnt;
    private final Vector3 dim;
    
    static {
        tmpVector = new Vector3();
    }
    
    public Vector3 getCenter(final Vector3 out) {
        return out.set(this.cnt);
    }
    
    public float getCenterX() {
        return this.cnt.x;
    }
    
    public float getCenterY() {
        return this.cnt.y;
    }
    
    public float getCenterZ() {
        return this.cnt.z;
    }
    
    public Vector3 getCorner000(final Vector3 out) {
        return out.set(this.min.x, this.min.y, this.min.z);
    }
    
    public Vector3 getCorner001(final Vector3 out) {
        return out.set(this.min.x, this.min.y, this.max.z);
    }
    
    public Vector3 getCorner010(final Vector3 out) {
        return out.set(this.min.x, this.max.y, this.min.z);
    }
    
    public Vector3 getCorner011(final Vector3 out) {
        return out.set(this.min.x, this.max.y, this.max.z);
    }
    
    public Vector3 getCorner100(final Vector3 out) {
        return out.set(this.max.x, this.min.y, this.min.z);
    }
    
    public Vector3 getCorner101(final Vector3 out) {
        return out.set(this.max.x, this.min.y, this.max.z);
    }
    
    public Vector3 getCorner110(final Vector3 out) {
        return out.set(this.max.x, this.max.y, this.min.z);
    }
    
    public Vector3 getCorner111(final Vector3 out) {
        return out.set(this.max.x, this.max.y, this.max.z);
    }
    
    public Vector3 getDimensions(final Vector3 out) {
        return out.set(this.dim);
    }
    
    public float getWidth() {
        return this.dim.x;
    }
    
    public float getHeight() {
        return this.dim.y;
    }
    
    public float getDepth() {
        return this.dim.z;
    }
    
    public Vector3 getMin(final Vector3 out) {
        return out.set(this.min);
    }
    
    public Vector3 getMax(final Vector3 out) {
        return out.set(this.max);
    }
    
    public BoundingBox() {
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.clr();
    }
    
    public BoundingBox(final BoundingBox bounds) {
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.set(bounds);
    }
    
    public BoundingBox(final Vector3 minimum, final Vector3 maximum) {
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.set(minimum, maximum);
    }
    
    public BoundingBox set(final BoundingBox bounds) {
        return this.set(bounds.min, bounds.max);
    }
    
    public BoundingBox set(final Vector3 minimum, final Vector3 maximum) {
        this.min.set((minimum.x < maximum.x) ? minimum.x : maximum.x, (minimum.y < maximum.y) ? minimum.y : maximum.y, (minimum.z < maximum.z) ? minimum.z : maximum.z);
        this.max.set((minimum.x > maximum.x) ? minimum.x : maximum.x, (minimum.y > maximum.y) ? minimum.y : maximum.y, (minimum.z > maximum.z) ? minimum.z : maximum.z);
        this.cnt.set(this.min).add(this.max).scl(0.5f);
        this.dim.set(this.max).sub(this.min);
        return this;
    }
    
    public BoundingBox set(final Vector3[] points) {
        this.inf();
        for (final Vector3 l_point : points) {
            this.ext(l_point);
        }
        return this;
    }
    
    public BoundingBox set(final List<Vector3> points) {
        this.inf();
        for (final Vector3 l_point : points) {
            this.ext(l_point);
        }
        return this;
    }
    
    public BoundingBox inf() {
        this.min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        this.max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        this.cnt.set(0.0f, 0.0f, 0.0f);
        this.dim.set(0.0f, 0.0f, 0.0f);
        return this;
    }
    
    public BoundingBox ext(final Vector3 point) {
        return this.set(this.min.set(min(this.min.x, point.x), min(this.min.y, point.y), min(this.min.z, point.z)), this.max.set(Math.max(this.max.x, point.x), Math.max(this.max.y, point.y), Math.max(this.max.z, point.z)));
    }
    
    public BoundingBox clr() {
        return this.set(this.min.set(0.0f, 0.0f, 0.0f), this.max.set(0.0f, 0.0f, 0.0f));
    }
    
    public boolean isValid() {
        return this.min.x <= this.max.x && this.min.y <= this.max.y && this.min.z <= this.max.z;
    }
    
    public BoundingBox ext(final BoundingBox a_bounds) {
        return this.set(this.min.set(min(this.min.x, a_bounds.min.x), min(this.min.y, a_bounds.min.y), min(this.min.z, a_bounds.min.z)), this.max.set(max(this.max.x, a_bounds.max.x), max(this.max.y, a_bounds.max.y), max(this.max.z, a_bounds.max.z)));
    }
    
    public BoundingBox ext(final Vector3 center, final float radius) {
        return this.set(this.min.set(min(this.min.x, center.x - radius), min(this.min.y, center.y - radius), min(this.min.z, center.z - radius)), this.max.set(max(this.max.x, center.x + radius), max(this.max.y, center.y + radius), max(this.max.z, center.z + radius)));
    }
    
    public BoundingBox ext(final BoundingBox bounds, final Matrix4 transform) {
        this.ext(BoundingBox.tmpVector.set(bounds.min.x, bounds.min.y, bounds.min.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.min.x, bounds.min.y, bounds.max.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.min.x, bounds.max.y, bounds.min.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.min.x, bounds.max.y, bounds.max.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.max.x, bounds.min.y, bounds.min.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.max.x, bounds.min.y, bounds.max.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.max.x, bounds.max.y, bounds.min.z).mul(transform));
        this.ext(BoundingBox.tmpVector.set(bounds.max.x, bounds.max.y, bounds.max.z).mul(transform));
        return this;
    }
    
    public BoundingBox mul(final Matrix4 transform) {
        final float x0 = this.min.x;
        final float y0 = this.min.y;
        final float z0 = this.min.z;
        final float x2 = this.max.x;
        final float y2 = this.max.y;
        final float z2 = this.max.z;
        this.inf();
        this.ext(BoundingBox.tmpVector.set(x0, y0, z0).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x0, y0, z2).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x0, y2, z0).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x0, y2, z2).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x2, y0, z0).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x2, y0, z2).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x2, y2, z0).mul(transform));
        this.ext(BoundingBox.tmpVector.set(x2, y2, z2).mul(transform));
        return this;
    }
    
    public boolean contains(final BoundingBox b) {
        return !this.isValid() || (this.min.x <= b.min.x && this.min.y <= b.min.y && this.min.z <= b.min.z && this.max.x >= b.max.x && this.max.y >= b.max.y && this.max.z >= b.max.z);
    }
    
    public boolean intersects(final BoundingBox b) {
        if (!this.isValid()) {
            return false;
        }
        final float lx = Math.abs(this.cnt.x - b.cnt.x);
        final float sumx = this.dim.x / 2.0f + b.dim.x / 2.0f;
        final float ly = Math.abs(this.cnt.y - b.cnt.y);
        final float sumy = this.dim.y / 2.0f + b.dim.y / 2.0f;
        final float lz = Math.abs(this.cnt.z - b.cnt.z);
        final float sumz = this.dim.z / 2.0f + b.dim.z / 2.0f;
        return lx <= sumx && ly <= sumy && lz <= sumz;
    }
    
    public boolean contains(final Vector3 v) {
        return this.min.x <= v.x && this.max.x >= v.x && this.min.y <= v.y && this.max.y >= v.y && this.min.z <= v.z && this.max.z >= v.z;
    }
    
    @Override
    public String toString() {
        return "[" + this.min + "|" + this.max + "]";
    }
    
    public BoundingBox ext(final float x, final float y, final float z) {
        return this.set(this.min.set(min(this.min.x, x), min(this.min.y, y), min(this.min.z, z)), this.max.set(max(this.max.x, x), max(this.max.y, y), max(this.max.z, z)));
    }
    
    static final float min(final float a, final float b) {
        return (a > b) ? b : a;
    }
    
    static final float max(final float a, final float b) {
        return (a > b) ? a : b;
    }
}
