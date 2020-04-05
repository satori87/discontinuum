// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AmbientCubemap
{
    private static final int NUM_VALUES = 18;
    public final float[] data;
    
    private static final float clamp(final float v) {
        return (v < 0.0f) ? 0.0f : ((v > 1.0f) ? 1.0f : v);
    }
    
    public AmbientCubemap() {
        this.data = new float[18];
    }
    
    public AmbientCubemap(final float[] copyFrom) {
        if (copyFrom.length != 18) {
            throw new GdxRuntimeException("Incorrect array size");
        }
        System.arraycopy(copyFrom, 0, this.data = new float[copyFrom.length], 0, this.data.length);
    }
    
    public AmbientCubemap(final AmbientCubemap copyFrom) {
        this(copyFrom.data);
    }
    
    public AmbientCubemap set(final float[] values) {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = values[i];
        }
        return this;
    }
    
    public AmbientCubemap set(final AmbientCubemap other) {
        return this.set(other.data);
    }
    
    public AmbientCubemap set(final Color color) {
        return this.set(color.r, color.g, color.b);
    }
    
    public AmbientCubemap set(final float r, final float g, final float b) {
        for (int idx = 0; idx < 18; idx += 3) {
            this.data[idx] = r;
            this.data[idx + 1] = g;
            this.data[idx + 2] = b;
        }
        return this;
    }
    
    public Color getColor(final Color out, int side) {
        side *= 3;
        return out.set(this.data[side], this.data[side + 1], this.data[side + 2], 1.0f);
    }
    
    public AmbientCubemap clear() {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = 0.0f;
        }
        return this;
    }
    
    public AmbientCubemap clamp() {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = clamp(this.data[i]);
        }
        return this;
    }
    
    public AmbientCubemap add(final float r, final float g, final float b) {
        float[] data;
        int n;
        float[] data2;
        int n2;
        float[] data3;
        int n3;
        for (int idx = 0; idx < this.data.length; n = idx++, data[n] += r, data2 = this.data, n2 = idx++, data2[n2] += g, data3 = this.data, n3 = idx++, data3[n3] += b) {
            data = this.data;
        }
        return this;
    }
    
    public AmbientCubemap add(final Color color) {
        return this.add(color.r, color.g, color.b);
    }
    
    public AmbientCubemap add(final float r, final float g, final float b, final float x, final float y, final float z) {
        final float x2 = x * x;
        final float y2 = y * y;
        final float z2 = z * z;
        float d = x2 + y2 + z2;
        if (d == 0.0f) {
            return this;
        }
        d = 1.0f / d * (d + 1.0f);
        final float rd = r * d;
        final float gd = g * d;
        final float bd = b * d;
        int idx = (x > 0.0f) ? 0 : 3;
        final float[] data = this.data;
        final int n = idx;
        data[n] += x2 * rd;
        final float[] data2 = this.data;
        final int n2 = idx + 1;
        data2[n2] += x2 * gd;
        final float[] data3 = this.data;
        final int n3 = idx + 2;
        data3[n3] += x2 * bd;
        idx = ((y > 0.0f) ? 6 : 9);
        final float[] data4 = this.data;
        final int n4 = idx;
        data4[n4] += y2 * rd;
        final float[] data5 = this.data;
        final int n5 = idx + 1;
        data5[n5] += y2 * gd;
        final float[] data6 = this.data;
        final int n6 = idx + 2;
        data6[n6] += y2 * bd;
        idx = ((z > 0.0f) ? 12 : 15);
        final float[] data7 = this.data;
        final int n7 = idx;
        data7[n7] += z2 * rd;
        final float[] data8 = this.data;
        final int n8 = idx + 1;
        data8[n8] += z2 * gd;
        final float[] data9 = this.data;
        final int n9 = idx + 2;
        data9[n9] += z2 * bd;
        return this;
    }
    
    public AmbientCubemap add(final Color color, final Vector3 direction) {
        return this.add(color.r, color.g, color.b, direction.x, direction.y, direction.z);
    }
    
    public AmbientCubemap add(final float r, final float g, final float b, final Vector3 direction) {
        return this.add(r, g, b, direction.x, direction.y, direction.z);
    }
    
    public AmbientCubemap add(final Color color, final float x, final float y, final float z) {
        return this.add(color.r, color.g, color.b, x, y, z);
    }
    
    public AmbientCubemap add(final Color color, final Vector3 point, final Vector3 target) {
        return this.add(color.r, color.g, color.b, target.x - point.x, target.y - point.y, target.z - point.z);
    }
    
    public AmbientCubemap add(final Color color, final Vector3 point, final Vector3 target, final float intensity) {
        final float t = intensity / (1.0f + target.dst(point));
        return this.add(color.r * t, color.g * t, color.b * t, target.x - point.x, target.y - point.y, target.z - point.z);
    }
    
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this.data.length; i += 3) {
            result = String.valueOf(result) + Float.toString(this.data[i]) + ", " + Float.toString(this.data[i + 1]) + ", " + Float.toString(this.data[i + 2]) + "\n";
        }
        return result;
    }
}
