// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public final class TimeUtils
{
    private static final long nanosPerMilli = 1000000L;
    
    public static long nanoTime() {
        return System.nanoTime();
    }
    
    public static long millis() {
        return System.currentTimeMillis();
    }
    
    public static long nanosToMillis(final long nanos) {
        return nanos / 1000000L;
    }
    
    public static long millisToNanos(final long millis) {
        return millis * 1000000L;
    }
    
    public static long timeSinceNanos(final long prevTime) {
        return nanoTime() - prevTime;
    }
    
    public static long timeSinceMillis(final long prevTime) {
        return millis() - prevTime;
    }
}
