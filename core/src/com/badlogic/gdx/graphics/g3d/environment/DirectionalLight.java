// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class DirectionalLight extends BaseLight<DirectionalLight>
{
    public final Vector3 direction;
    
    public DirectionalLight() {
        this.direction = new Vector3();
    }
    
    public DirectionalLight setDirection(final float directionX, final float directionY, final float directionZ) {
        this.direction.set(directionX, directionY, directionZ);
        return this;
    }
    
    public DirectionalLight setDirection(final Vector3 direction) {
        this.direction.set(direction);
        return this;
    }
    
    public DirectionalLight set(final DirectionalLight copyFrom) {
        return this.set(copyFrom.color, copyFrom.direction);
    }
    
    public DirectionalLight set(final Color color, final Vector3 direction) {
        if (color != null) {
            this.color.set(color);
        }
        if (direction != null) {
            this.direction.set(direction).nor();
        }
        return this;
    }
    
    public DirectionalLight set(final float r, final float g, final float b, final Vector3 direction) {
        this.color.set(r, g, b, 1.0f);
        if (direction != null) {
            this.direction.set(direction).nor();
        }
        return this;
    }
    
    public DirectionalLight set(final Color color, final float dirX, final float dirY, final float dirZ) {
        if (color != null) {
            this.color.set(color);
        }
        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }
    
    public DirectionalLight set(final float r, final float g, final float b, final float dirX, final float dirY, final float dirZ) {
        this.color.set(r, g, b, 1.0f);
        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return arg0 instanceof DirectionalLight && this.equals((DirectionalLight)arg0);
    }
    
    public boolean equals(final DirectionalLight other) {
        return other != null && (other == this || (this.color.equals(other.color) && this.direction.equals(other.direction)));
    }
}
