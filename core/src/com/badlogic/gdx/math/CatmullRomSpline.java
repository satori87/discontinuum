// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public class CatmullRomSpline<T extends Vector<T>> implements Path<T>
{
    public T[] controlPoints;
    public boolean continuous;
    public int spanCount;
    private T tmp;
    private T tmp2;
    private T tmp3;
    
    public static <T extends Vector<T>> T calculate(final T out, final float t, final T[] points, final boolean continuous, final T tmp) {
        final int n = continuous ? points.length : (points.length - 3);
        float u = t * n;
        final int i = (t >= 1.0f) ? (n - 1) : ((int)u);
        u -= i;
        return calculate(out, i, u, points, continuous, tmp);
    }
    
    public static <T extends Vector<T>> T calculate(final T out, final int i, final float u, final T[] points, final boolean continuous, final T tmp) {
        final int n = points.length;
        final float u2 = u * u;
        final float u3 = u2 * u;
        out.set(points[i]).scl(1.5f * u3 - 2.5f * u2 + 1.0f);
        if (continuous || i > 0) {
            out.add(tmp.set(points[(n + i - 1) % n]).scl(-0.5f * u3 + u2 - 0.5f * u));
        }
        if (continuous || i < n - 1) {
            out.add(tmp.set(points[(i + 1) % n]).scl(-1.5f * u3 + 2.0f * u2 + 0.5f * u));
        }
        if (continuous || i < n - 2) {
            out.add(tmp.set(points[(i + 2) % n]).scl(0.5f * u3 - 0.5f * u2));
        }
        return out;
    }
    
    public static <T extends Vector<T>> T derivative(final T out, final float t, final T[] points, final boolean continuous, final T tmp) {
        final int n = continuous ? points.length : (points.length - 3);
        float u = t * n;
        final int i = (t >= 1.0f) ? (n - 1) : ((int)u);
        u -= i;
        return derivative(out, i, u, points, continuous, tmp);
    }
    
    public static <T extends Vector<T>> T derivative(final T out, final int i, final float u, final T[] points, final boolean continuous, final T tmp) {
        final int n = points.length;
        final float u2 = u * u;
        out.set(points[i]).scl(-u * 5.0f + u2 * 4.5f);
        if (continuous || i > 0) {
            out.add(tmp.set(points[(n + i - 1) % n]).scl(-0.5f + u * 2.0f - u2 * 1.5f));
        }
        if (continuous || i < n - 1) {
            out.add(tmp.set(points[(i + 1) % n]).scl(0.5f + u * 4.0f - u2 * 4.5f));
        }
        if (continuous || i < n - 2) {
            out.add(tmp.set(points[(i + 2) % n]).scl(-u + u2 * 1.5f));
        }
        return out;
    }
    
    public CatmullRomSpline() {
    }
    
    public CatmullRomSpline(final T[] controlPoints, final boolean continuous) {
        this.set(controlPoints, continuous);
    }
    
    public CatmullRomSpline set(final T[] controlPoints, final boolean continuous) {
        if (this.tmp == null) {
            this.tmp = controlPoints[0].cpy();
        }
        if (this.tmp2 == null) {
            this.tmp2 = controlPoints[0].cpy();
        }
        if (this.tmp3 == null) {
            this.tmp3 = controlPoints[0].cpy();
        }
        this.controlPoints = controlPoints;
        this.continuous = continuous;
        this.spanCount = (continuous ? controlPoints.length : (controlPoints.length - 3));
        return this;
    }
    
    @Override
    public T valueAt(final T out, final float t) {
        final int n = this.spanCount;
        float u = t * n;
        final int i = (t >= 1.0f) ? (n - 1) : ((int)u);
        u -= i;
        return this.valueAt(out, i, u);
    }
    
    public T valueAt(final T out, final int span, final float u) {
        return calculate(out, this.continuous ? span : (span + 1), u, this.controlPoints, this.continuous, this.tmp);
    }
    
    @Override
    public T derivativeAt(final T out, final float t) {
        final int n = this.spanCount;
        float u = t * n;
        final int i = (t >= 1.0f) ? (n - 1) : ((int)u);
        u -= i;
        return this.derivativeAt(out, i, u);
    }
    
    public T derivativeAt(final T out, final int span, final float u) {
        return derivative(out, this.continuous ? span : (span + 1), u, this.controlPoints, this.continuous, this.tmp);
    }
    
    public int nearest(final T in) {
        return this.nearest(in, 0, this.spanCount);
    }
    
    public int nearest(final T in, int start, final int count) {
        while (start < 0) {
            start += this.spanCount;
        }
        int result = start % this.spanCount;
        float dst = in.dst2(this.controlPoints[result]);
        for (int i = 1; i < count; ++i) {
            final int idx = (start + i) % this.spanCount;
            final float d = in.dst2(this.controlPoints[idx]);
            if (d < dst) {
                dst = d;
                result = idx;
            }
        }
        return result;
    }
    
    @Override
    public float approximate(final T v) {
        return this.approximate(v, this.nearest(v));
    }
    
    public float approximate(final T in, final int start, final int count) {
        return this.approximate(in, this.nearest(in, start, count));
    }
    
    public float approximate(final T in, final int near) {
        int n = near;
        final T nearest = this.controlPoints[n];
        final T previous = this.controlPoints[(n > 0) ? (n - 1) : (this.spanCount - 1)];
        final T next = this.controlPoints[(n + 1) % this.spanCount];
        final float dstPrev2 = in.dst2(previous);
        final float dstNext2 = in.dst2(next);
        T P1;
        T P2;
        T P3;
        if (dstNext2 < dstPrev2) {
            P1 = nearest;
            P2 = next;
            P3 = in;
        }
        else {
            P1 = previous;
            P2 = nearest;
            P3 = in;
            n = ((n > 0) ? (n - 1) : (this.spanCount - 1));
        }
        final float L1Sqr = P1.dst2(P2);
        final float L2Sqr = P3.dst2(P2);
        final float L3Sqr = P3.dst2(P1);
        final float L1 = (float)Math.sqrt(L1Sqr);
        final float s = (L2Sqr + L1Sqr - L3Sqr) / (2.0f * L1);
        final float u = MathUtils.clamp((L1 - s) / L1, 0.0f, 1.0f);
        return (n + u) / this.spanCount;
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
