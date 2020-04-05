// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.util.Random;

public class RandomXS128 extends Random
{
    private static final double NORM_DOUBLE = 1.1102230246251565E-16;
    private static final double NORM_FLOAT = 5.9604644775390625E-8;
    private long seed0;
    private long seed1;
    
    public RandomXS128() {
        this.setSeed(new Random().nextLong());
    }
    
    public RandomXS128(final long seed) {
        this.setSeed(seed);
    }
    
    public RandomXS128(final long seed0, final long seed1) {
        this.setState(seed0, seed1);
    }
    
    @Override
    public long nextLong() {
        long s1 = this.seed0;
        final long s2 = this.seed1;
        this.seed0 = s2;
        s1 ^= s1 << 23;
        return (this.seed1 = (s1 ^ s2 ^ s1 >>> 17 ^ s2 >>> 26)) + s2;
    }
    
    @Override
    protected final int next(final int bits) {
        return (int)(this.nextLong() & (1L << bits) - 1L);
    }
    
    @Override
    public int nextInt() {
        return (int)this.nextLong();
    }
    
    @Override
    public int nextInt(final int n) {
        return (int)this.nextLong(n);
    }
    
    public long nextLong(final long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("n must be positive");
        }
        long bits;
        long value;
        do {
            bits = this.nextLong() >>> 1;
            value = bits % n;
        } while (bits - value + (n - 1L) < 0L);
        return value;
    }
    
    @Override
    public double nextDouble() {
        return (this.nextLong() >>> 11) * 1.1102230246251565E-16;
    }
    
    @Override
    public float nextFloat() {
        return (float)((this.nextLong() >>> 40) * 5.9604644775390625E-8);
    }
    
    @Override
    public boolean nextBoolean() {
        return (this.nextLong() & 0x1L) != 0x0L;
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        int n = 0;
        int i = bytes.length;
        while (i != 0) {
            n = ((i < 8) ? i : 8);
            long bits = this.nextLong();
            while (n-- != 0) {
                bytes[--i] = (byte)bits;
                bits >>= 8;
            }
        }
    }
    
    @Override
    public void setSeed(final long seed) {
        final long seed2 = murmurHash3((seed == 0L) ? Long.MIN_VALUE : seed);
        this.setState(seed2, murmurHash3(seed2));
    }
    
    public void setState(final long seed0, final long seed1) {
        this.seed0 = seed0;
        this.seed1 = seed1;
    }
    
    public long getState(final int seed) {
        return (seed == 0) ? this.seed0 : this.seed1;
    }
    
    private static final long murmurHash3(long x) {
        x ^= x >>> 33;
        x *= -49064778989728563L;
        x ^= x >>> 33;
        x *= -4265267296055464877L;
        x ^= x >>> 33;
        return x;
    }
}
