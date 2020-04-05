// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public interface Vector<T extends Vector<T>>
{
    T cpy();
    
    float len();
    
    float len2();
    
    T limit(final float p0);
    
    T limit2(final float p0);
    
    T setLength(final float p0);
    
    T setLength2(final float p0);
    
    T clamp(final float p0, final float p1);
    
    T set(final T p0);
    
    T sub(final T p0);
    
    T nor();
    
    T add(final T p0);
    
    float dot(final T p0);
    
    T scl(final float p0);
    
    T scl(final T p0);
    
    float dst(final T p0);
    
    float dst2(final T p0);
    
    T lerp(final T p0, final float p1);
    
    T interpolate(final T p0, final float p1, final Interpolation p2);
    
    T setToRandomDirection();
    
    boolean isUnit();
    
    boolean isUnit(final float p0);
    
    boolean isZero();
    
    boolean isZero(final float p0);
    
    boolean isOnLine(final T p0, final float p1);
    
    boolean isOnLine(final T p0);
    
    boolean isCollinear(final T p0, final float p1);
    
    boolean isCollinear(final T p0);
    
    boolean isCollinearOpposite(final T p0, final float p1);
    
    boolean isCollinearOpposite(final T p0);
    
    boolean isPerpendicular(final T p0);
    
    boolean isPerpendicular(final T p0, final float p1);
    
    boolean hasSameDirection(final T p0);
    
    boolean hasOppositeDirection(final T p0);
    
    boolean epsilonEquals(final T p0, final float p1);
    
    T mulAdd(final T p0, final float p1);
    
    T mulAdd(final T p0, final T p1);
    
    T setZero();
}
