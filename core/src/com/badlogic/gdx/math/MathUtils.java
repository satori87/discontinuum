// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.util.Random;

public final class MathUtils
{
    public static final float nanoToSec = 1.0E-9f;
    public static final float FLOAT_ROUNDING_ERROR = 1.0E-6f;
    public static final float PI = 3.1415927f;
    public static final float PI2 = 6.2831855f;
    public static final float E = 2.7182817f;
    private static final int SIN_BITS = 14;
    private static final int SIN_MASK = 16383;
    private static final int SIN_COUNT = 16384;
    private static final float radFull = 6.2831855f;
    private static final float degFull = 360.0f;
    private static final float radToIndex = 2607.5945f;
    private static final float degToIndex = 45.511112f;
    public static final float radiansToDegrees = 57.295776f;
    public static final float radDeg = 57.295776f;
    public static final float degreesToRadians = 0.017453292f;
    public static final float degRad = 0.017453292f;
    public static Random random;
    private static final int BIG_ENOUGH_INT = 16384;
    private static final double BIG_ENOUGH_FLOOR = 16384.0;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = 16384.5;
    
    static {
        MathUtils.random = new RandomXS128();
    }
    
    public static float sin(final float radians) {
        return Sin.table[(int)(radians * 2607.5945f) & 0x3FFF];
    }
    
    public static float cos(final float radians) {
        return Sin.table[(int)((radians + 1.5707964f) * 2607.5945f) & 0x3FFF];
    }
    
    public static float sinDeg(final float degrees) {
        return Sin.table[(int)(degrees * 45.511112f) & 0x3FFF];
    }
    
    public static float cosDeg(final float degrees) {
        return Sin.table[(int)((degrees + 90.0f) * 45.511112f) & 0x3FFF];
    }
    
    public static float atan2(final float y, final float x) {
        if (x == 0.0f) {
            if (y > 0.0f) {
                return 1.5707964f;
            }
            if (y == 0.0f) {
                return 0.0f;
            }
            return -1.5707964f;
        }
        else {
            final float z = y / x;
            if (Math.abs(z) >= 1.0f) {
                final float atan = 1.5707964f - z / (z * z + 0.28f);
                return (y < 0.0f) ? (atan - 3.1415927f) : atan;
            }
            final float atan = z / (1.0f + 0.28f * z * z);
            if (x < 0.0f) {
                return atan + ((y < 0.0f) ? -3.1415927f : 3.1415927f);
            }
            return atan;
        }
    }
    
    public static int random(final int range) {
        return MathUtils.random.nextInt(range + 1);
    }
    
    public static int random(final int start, final int end) {
        return start + MathUtils.random.nextInt(end - start + 1);
    }
    
    public static long random(final long range) {
        return (long)(MathUtils.random.nextDouble() * range);
    }
    
    public static long random(final long start, final long end) {
        return start + (long)(MathUtils.random.nextDouble() * (end - start));
    }
    
    public static boolean randomBoolean() {
        return MathUtils.random.nextBoolean();
    }
    
    public static boolean randomBoolean(final float chance) {
        return random() < chance;
    }
    
    public static float random() {
        return MathUtils.random.nextFloat();
    }
    
    public static float random(final float range) {
        return MathUtils.random.nextFloat() * range;
    }
    
    public static float random(final float start, final float end) {
        return start + MathUtils.random.nextFloat() * (end - start);
    }
    
    public static int randomSign() {
        return 0x1 | MathUtils.random.nextInt() >> 31;
    }
    
    public static float randomTriangular() {
        return MathUtils.random.nextFloat() - MathUtils.random.nextFloat();
    }
    
    public static float randomTriangular(final float max) {
        return (MathUtils.random.nextFloat() - MathUtils.random.nextFloat()) * max;
    }
    
    public static float randomTriangular(final float min, final float max) {
        return randomTriangular(min, max, (min + max) * 0.5f);
    }
    
    public static float randomTriangular(final float min, final float max, final float mode) {
        final float u = MathUtils.random.nextFloat();
        final float d = max - min;
        if (u <= (mode - min) / d) {
            return min + (float)Math.sqrt(u * d * (mode - min));
        }
        return max - (float)Math.sqrt((1.0f - u) * d * (max - mode));
    }
    
    public static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        value = (--value | value >> 1);
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }
    
    public static boolean isPowerOfTwo(final int value) {
        return value != 0 && (value & value - 1) == 0x0;
    }
    
    public static short clamp(final short value, final short min, final short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static int clamp(final int value, final int min, final int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static long clamp(final long value, final long min, final long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static float clamp(final float value, final float min, final float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static double clamp(final double value, final double min, final double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
    
    public static float lerp(final float fromValue, final float toValue, final float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }
    
    public static float lerpAngle(final float fromRadians, final float toRadians, final float progress) {
        final float delta = (toRadians - fromRadians + 6.2831855f + 3.1415927f) % 6.2831855f - 3.1415927f;
        return (fromRadians + delta * progress + 6.2831855f) % 6.2831855f;
    }
    
    public static float lerpAngleDeg(final float fromDegrees, final float toDegrees, final float progress) {
        final float delta = (toDegrees - fromDegrees + 360.0f + 180.0f) % 360.0f - 180.0f;
        return (fromDegrees + delta * progress + 360.0f) % 360.0f;
    }
    
    public static int floor(final float value) {
        return (int)(value + 16384.0) - 16384;
    }
    
    public static int floorPositive(final float value) {
        return (int)value;
    }
    
    public static int ceil(final float value) {
        return 16384 - (int)(16384.0 - value);
    }
    
    public static int ceilPositive(final float value) {
        return (int)(value + 0.9999999);
    }
    
    public static int round(final float value) {
        return (int)(value + 16384.5) - 16384;
    }
    
    public static int roundPositive(final float value) {
        return (int)(value + 0.5f);
    }
    
    public static boolean isZero(final float value) {
        return Math.abs(value) <= 1.0E-6f;
    }
    
    public static boolean isZero(final float value, final float tolerance) {
        return Math.abs(value) <= tolerance;
    }
    
    public static boolean isEqual(final float a, final float b) {
        return Math.abs(a - b) <= 1.0E-6f;
    }
    
    public static boolean isEqual(final float a, final float b, final float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }
    
    public static float log(final float a, final float value) {
        return (float)(Math.log(value) / Math.log(a));
    }
    
    public static float log2(final float value) {
        return log(2.0f, value);
    }
    
    private static class Sin
    {
        static final float[] table;
        
        static {
            table = new float[16384];
            for (int i = 0; i < 16384; ++i) {
                Sin.table[i] = (float)Math.sin((i + 0.5f) / 16384.0f * 6.2831855f);
            }
            for (int i = 0; i < 360; i += 90) {
                Sin.table[(int)(i * 45.511112f) & 0x3FFF] = (float)Math.sin(i * 0.017453292f);
            }
        }
    }
}
