// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public final class NumberUtils
{
    public static int floatToIntBits(final float value) {
        return Float.floatToIntBits(value);
    }
    
    public static int floatToRawIntBits(final float value) {
        return Float.floatToRawIntBits(value);
    }
    
    public static int floatToIntColor(final float value) {
        int intBits = Float.floatToRawIntBits(value);
        intBits |= (int)((intBits >>> 24) * 1.003937f) << 24;
        return intBits;
    }
    
    public static float intToFloatColor(final int value) {
        return Float.intBitsToFloat(value & 0xFEFFFFFF);
    }
    
    public static float intBitsToFloat(final int value) {
        return Float.intBitsToFloat(value);
    }
    
    public static long doubleToLongBits(final double value) {
        return Double.doubleToLongBits(value);
    }
    
    public static double longBitsToDouble(final long value) {
        return Double.longBitsToDouble(value);
    }
}
