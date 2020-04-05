// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class SpotLight extends BaseLight<SpotLight>
{
    public final Vector3 position;
    public final Vector3 direction;
    public float intensity;
    public float cutoffAngle;
    public float exponent;
    
    public SpotLight() {
        this.position = new Vector3();
        this.direction = new Vector3();
    }
    
    public SpotLight setPosition(final float positionX, final float positionY, final float positionZ) {
        this.position.set(positionX, positionY, positionZ);
        return this;
    }
    
    public SpotLight setPosition(final Vector3 position) {
        this.position.set(position);
        return this;
    }
    
    public SpotLight setDirection(final float directionX, final float directionY, final float directionZ) {
        this.direction.set(directionX, directionY, directionZ);
        return this;
    }
    
    public SpotLight setDirection(final Vector3 direction) {
        this.direction.set(direction);
        return this;
    }
    
    public SpotLight setIntensity(final float intensity) {
        this.intensity = intensity;
        return this;
    }
    
    public SpotLight setCutoffAngle(final float cutoffAngle) {
        this.cutoffAngle = cutoffAngle;
        return this;
    }
    
    public SpotLight setExponent(final float exponent) {
        this.exponent = exponent;
        return this;
    }
    
    public SpotLight set(final SpotLight copyFrom) {
        return this.set(copyFrom.color, copyFrom.position, copyFrom.direction, copyFrom.intensity, copyFrom.cutoffAngle, copyFrom.exponent);
    }
    
    public SpotLight set(final Color color, final Vector3 position, final Vector3 direction, final float intensity, final float cutoffAngle, final float exponent) {
        if (color != null) {
            this.color.set(color);
        }
        if (position != null) {
            this.position.set(position);
        }
        if (direction != null) {
            this.direction.set(direction).nor();
        }
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }
    
    public SpotLight set(final float r, final float g, final float b, final Vector3 position, final Vector3 direction, final float intensity, final float cutoffAngle, final float exponent) {
        this.color.set(r, g, b, 1.0f);
        if (position != null) {
            this.position.set(position);
        }
        if (direction != null) {
            this.direction.set(direction).nor();
        }
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }
    
    public SpotLight set(final Color color, final float posX, final float posY, final float posZ, final float dirX, final float dirY, final float dirZ, final float intensity, final float cutoffAngle, final float exponent) {
        if (color != null) {
            this.color.set(color);
        }
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }
    
    public SpotLight set(final float r, final float g, final float b, final float posX, final float posY, final float posZ, final float dirX, final float dirY, final float dirZ, final float intensity, final float cutoffAngle, final float exponent) {
        this.color.set(r, g, b, 1.0f);
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }
    
    public SpotLight setTarget(final Vector3 target) {
        this.direction.set(target).sub(this.position).nor();
        return this;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SpotLight && this.equals((SpotLight)obj);
    }
    
    public boolean equals(final SpotLight other) {
        return other != null && (other == this || (this.color.equals(other.color) && this.position.equals(other.position) && this.direction.equals(other.direction) && MathUtils.isEqual(this.intensity, other.intensity) && MathUtils.isEqual(this.cutoffAngle, other.cutoffAngle) && MathUtils.isEqual(this.exponent, other.exponent)));
    }
}
