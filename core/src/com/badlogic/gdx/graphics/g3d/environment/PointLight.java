// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PointLight extends BaseLight<PointLight>
{
    public final Vector3 position;
    public float intensity;
    
    public PointLight() {
        this.position = new Vector3();
    }
    
    public PointLight setPosition(final float positionX, final float positionY, final float positionZ) {
        this.position.set(positionX, positionY, positionZ);
        return this;
    }
    
    public PointLight setPosition(final Vector3 position) {
        this.position.set(position);
        return this;
    }
    
    public PointLight setIntensity(final float intensity) {
        this.intensity = intensity;
        return this;
    }
    
    public PointLight set(final PointLight copyFrom) {
        return this.set(copyFrom.color, copyFrom.position, copyFrom.intensity);
    }
    
    public PointLight set(final Color color, final Vector3 position, final float intensity) {
        if (color != null) {
            this.color.set(color);
        }
        if (position != null) {
            this.position.set(position);
        }
        this.intensity = intensity;
        return this;
    }
    
    public PointLight set(final float r, final float g, final float b, final Vector3 position, final float intensity) {
        this.color.set(r, g, b, 1.0f);
        if (position != null) {
            this.position.set(position);
        }
        this.intensity = intensity;
        return this;
    }
    
    public PointLight set(final Color color, final float x, final float y, final float z, final float intensity) {
        if (color != null) {
            this.color.set(color);
        }
        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }
    
    public PointLight set(final float r, final float g, final float b, final float x, final float y, final float z, final float intensity) {
        this.color.set(r, g, b, 1.0f);
        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof PointLight && this.equals((PointLight)obj);
    }
    
    public boolean equals(final PointLight other) {
        return other != null && (other == this || (this.color.equals(other.color) && this.position.equals(other.position) && this.intensity == other.intensity));
    }
}
