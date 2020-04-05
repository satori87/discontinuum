// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public interface Path<T>
{
    T derivativeAt(final T p0, final float p1);
    
    T valueAt(final T p0, final float p1);
    
    float approximate(final T p0);
    
    float locate(final T p0);
    
    float approxLength(final int p0);
}
