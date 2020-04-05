// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class PerformanceCounters
{
    private static final float nano2seconds = 1.0E-9f;
    private long lastTick;
    public final Array<PerformanceCounter> counters;
    
    public PerformanceCounters() {
        this.lastTick = 0L;
        this.counters = new Array<PerformanceCounter>();
    }
    
    public PerformanceCounter add(final String name, final int windowSize) {
        final PerformanceCounter result = new PerformanceCounter(name, windowSize);
        this.counters.add(result);
        return result;
    }
    
    public PerformanceCounter add(final String name) {
        final PerformanceCounter result = new PerformanceCounter(name);
        this.counters.add(result);
        return result;
    }
    
    public void tick() {
        final long t = TimeUtils.nanoTime();
        if (this.lastTick > 0L) {
            this.tick((t - this.lastTick) * 1.0E-9f);
        }
        this.lastTick = t;
    }
    
    public void tick(final float deltaTime) {
        for (int i = 0; i < this.counters.size; ++i) {
            this.counters.get(i).tick(deltaTime);
        }
    }
    
    public StringBuilder toString(final StringBuilder sb) {
        sb.setLength(0);
        for (int i = 0; i < this.counters.size; ++i) {
            if (i != 0) {
                sb.append("; ");
            }
            this.counters.get(i).toString(sb);
        }
        return sb;
    }
}
