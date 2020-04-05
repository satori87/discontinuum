// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SphericalHarmonics
{
    private static final float[] coeff;
    public final float[] data;
    
    static {
        coeff = new float[] { 0.282095f, 0.488603f, 0.488603f, 0.488603f, 1.092548f, 1.092548f, 1.092548f, 0.315392f, 0.546274f };
    }
    
    private static final float clamp(final float v) {
        return (v < 0.0f) ? 0.0f : ((v > 1.0f) ? 1.0f : v);
    }
    
    public SphericalHarmonics() {
        this.data = new float[27];
    }
    
    public SphericalHarmonics(final float[] copyFrom) {
        if (copyFrom.length != 27) {
            throw new GdxRuntimeException("Incorrect array size");
        }
        this.data = copyFrom.clone();
    }
    
    public SphericalHarmonics set(final float[] values) {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = values[i];
        }
        return this;
    }
    
    public SphericalHarmonics set(final AmbientCubemap other) {
        return this.set(other.data);
    }
    
    public SphericalHarmonics set(final Color color) {
        return this.set(color.r, color.g, color.b);
    }
    
    public SphericalHarmonics set(final float r, final float g, final float b) {
        for (int idx = 0; idx < this.data.length; this.data[idx++] = r, this.data[idx++] = g, this.data[idx++] = b) {}
        return this;
    }
}
