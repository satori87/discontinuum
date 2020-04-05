// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.steer.Steerable;

public class Wall implements Steerable<Vector2>
{
    public boolean boundary;
    public Body body;
    
    public Wall() {
        this.boundary = false;
    }
    
    public Wall(final Body body, final boolean boundary) {
        this.boundary = false;
        this.body = body;
        this.boundary = boundary;
    }
    
    public Vector2 angleToVector(final Vector2 outVector, final float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }
    
    public float getOrientation() {
        return this.body.getAngle();
    }
    
    public Vector2 getPosition() {
        return this.body.getPosition();
    }
    
    public Location<Vector2> newLocation() {
        return (Location<Vector2>)new Box2dLocation();
    }
    
    public void setOrientation(final float orientation) {
        this.body.setTransform(this.getPosition(), orientation);
    }
    
    public Vector2 getLinearVelocity() {
        return this.body.getLinearVelocity();
    }
    
    public float getAngularVelocity() {
        return this.body.getAngularVelocity();
    }
    
    public float getBoundingRadius() {
        return 0.02f;
    }
    
    public boolean isTagged() {
        return false;
    }
    
    public void setTagged(final boolean tagged) {
    }
    
    public float getMaxLinearSpeed() {
        return 1.0f;
    }
    
    public void setMaxLinearSpeed(final float maxLinearSpeed) {
    }
    
    public float getMaxLinearAcceleration() {
        return 0.0f;
    }
    
    public void setMaxLinearAcceleration(final float maxLinearAcceleration) {
    }
    
    public float getMaxAngularSpeed() {
        return 1.0f;
    }
    
    public void setMaxAngularSpeed(final float maxAngularSpeed) {
    }
    
    public float getMaxAngularAcceleration() {
        return 1.0f;
    }
    
    public void setMaxAngularAcceleration(final float maxAngularAcceleration) {
    }
    
    public float getZeroLinearSpeedThreshold() {
        return 1.0E-7f;
    }
    
    public void setZeroLinearSpeedThreshold(final float value) {
        throw new UnsupportedOperationException();
    }
    
    public float vectorToAngle(final Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }
}
