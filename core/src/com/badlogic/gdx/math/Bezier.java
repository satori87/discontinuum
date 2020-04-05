// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Array;

public class Bezier<T extends Vector<T>> implements Path<T>
{
    public Array<T> points;
    private T tmp;
    private T tmp2;
    private T tmp3;
    
    public static <T extends Vector<T>> T linear(final T out, final float t, final T p0, final T p1, final T tmp) {
        return ((Vector<Vector<T>>)out.set(p0)).scl(1.0f - t).add(tmp.set(p1).scl(t));
    }
    
    public static <T extends Vector<T>> T linear_derivative(final T out, final float t, final T p0, final T p1, final T tmp) {
        return out.set(p1).sub(p0);
    }
    
    public static <T extends Vector<T>> T quadratic(final T out, final float t, final T p0, final T p1, final T p2, final T tmp) {
        final float dt = 1.0f - t;
        return ((Vector<Vector<T>>)out.set(p0)).scl(dt * dt).add(tmp.set(p1).scl(2.0f * dt * t)).add(tmp.set(p2).scl(t * t));
    }
    
    public static <T extends Vector<T>> T quadratic_derivative(final T out, final float t, final T p0, final T p1, final T p2, final T tmp) {
        final float dt = 1.0f - t;
        return ((Vector<Vector<T>>)out.set(p1).sub(p0)).scl(2.0f).scl(1.0f - t).add(((Vector<Vector<T>>)tmp.set(p2).sub(p1)).scl(t).scl(2.0f));
    }
    
    public static <T extends Vector<T>> T cubic(final T out, final float t, final T p0, final T p1, final T p2, final T p3, final T tmp) {
        final float dt = 1.0f - t;
        final float dt2 = dt * dt;
        final float t2 = t * t;
        return ((Vector<Vector<T>>)out.set(p0)).scl(dt2 * dt).add(tmp.set(p1).scl(3.0f * dt2 * t)).add(tmp.set(p2).scl(3.0f * dt * t2)).add(tmp.set(p3).scl(t2 * t));
    }
    
    public static <T extends Vector<T>> T cubic_derivative(final T out, final float t, final T p0, final T p1, final T p2, final T p3, final T tmp) {
        final float dt = 1.0f - t;
        final float dt2 = dt * dt;
        final float t2 = t * t;
        return ((Vector<Vector<T>>)out.set(p1).sub(p0)).scl(dt2 * 3.0f).add(tmp.set(p2).sub(p1).scl(dt * t * 6.0f)).add(tmp.set(p3).sub(p2).scl(t2 * 3.0f));
    }
    
    public Bezier() {
        this.points = new Array<T>();
    }
    
    public Bezier(final T... points) {
        this.points = new Array<T>();
        this.set(points);
    }
    
    public Bezier(final T[] points, final int offset, final int length) {
        this.points = new Array<T>();
        this.set(points, offset, length);
    }
    
    public Bezier(final Array<T> points, final int offset, final int length) {
        this.points = new Array<T>();
        this.set(points, offset, length);
    }
    
    public Bezier set(final T... points) {
        return this.set(points, 0, points.length);
    }
    
    public Bezier set(final T[] points, final int offset, final int length) {
        if (length < 2 || length > 4) {
            throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
        }
        if (this.tmp == null) {
            this.tmp = points[0].cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = points[0].cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = points[0].cpy();
        }
        this.points.clear();
        this.points.addAll(points, offset, length);
        return this;
    }
    
    public Bezier set(final Array<T> points, final int offset, final int length) {
        if (length < 2 || length > 4) {
            throw new GdxRuntimeException("Only first, second and third degree Bezier curves are supported.");
        }
        if (this.tmp == null) {
            this.tmp = points.get(0).cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = points.get(0).cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = points.get(0).cpy();
        }
        this.points.clear();
        this.points.addAll((Array<? extends T>)points, offset, length);
        return this;
    }
    
    @Override
    public T valueAt(final T out, final float t) {
        final int n = this.points.size;
        if (n == 2) {
            linear(out, t, this.points.get(0), this.points.get(1), this.tmp);
        }
        else if (n == 3) {
            quadratic(out, t, this.points.get(0), this.points.get(1), this.points.get(2), this.tmp);
        }
        else if (n == 4) {
            cubic(out, t, this.points.get(0), this.points.get(1), this.points.get(2), this.points.get(3), this.tmp);
        }
        return out;
    }
    
    @Override
    public T derivativeAt(final T out, final float t) {
        final int n = this.points.size;
        if (n == 2) {
            linear_derivative(out, t, this.points.get(0), this.points.get(1), this.tmp);
        }
        else if (n == 3) {
            quadratic_derivative(out, t, this.points.get(0), this.points.get(1), this.points.get(2), this.tmp);
        }
        else if (n == 4) {
            cubic_derivative(out, t, this.points.get(0), this.points.get(1), this.points.get(2), this.points.get(3), this.tmp);
        }
        return out;
    }
    
    @Override
    public float approximate(final T v) {
        final T p1 = this.points.get(0);
        final T p2 = this.points.get(this.points.size - 1);
        final T p3 = v;
        final float l1Sqr = p1.dst2(p2);
        final float l2Sqr = p3.dst2(p2);
        final float l3Sqr = p3.dst2(p1);
        final float l1 = (float)Math.sqrt(l1Sqr);
        final float s = (l2Sqr + l1Sqr - l3Sqr) / (2.0f * l1);
        return MathUtils.clamp((l1 - s) / l1, 0.0f, 1.0f);
    }
    
    @Override
    public float locate(final T v) {
        return this.approximate(v);
    }
    
    @Override
    public float approxLength(final int samples) {
        float tempLength = 0.0f;
        for (int i = 0; i < samples; ++i) {
            this.tmp2.set(this.tmp3);
            this.valueAt(this.tmp3, i / (samples - 1.0f));
            if (i > 0) {
                tempLength += this.tmp2.dst(this.tmp3);
            }
        }
        return tempLength;
    }
}
