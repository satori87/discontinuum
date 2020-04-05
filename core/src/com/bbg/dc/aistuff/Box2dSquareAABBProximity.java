// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.steer.Proximity;

public class Box2dSquareAABBProximity implements Proximity<Vector2>, QueryCallback
{
    protected Steerable<Vector2> owner;
    protected World world;
    protected Proximity.ProximityCallback<Vector2> behaviorCallback;
    protected float detectionRadius;
    private int neighborCount;
    private static final AABB aabb;
    
    static {
        aabb = new AABB();
    }
    
    public Box2dSquareAABBProximity(final Steerable<Vector2> owner, final World world, final float detectionRadius) {
        this.owner = owner;
        this.world = world;
        this.detectionRadius = detectionRadius;
        this.behaviorCallback = null;
        this.neighborCount = 0;
    }
    
    public Steerable<Vector2> getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Steerable<Vector2> owner) {
        this.owner = owner;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public void setWorld(final World world) {
        this.world = world;
    }
    
    public float getDetectionRadius() {
        return this.detectionRadius;
    }
    
    public void setDetectionRadius(final float detectionRadius) {
        this.detectionRadius = detectionRadius;
    }
    
    public int findNeighbors(final Proximity.ProximityCallback<Vector2> behaviorCallback) {
        this.behaviorCallback = behaviorCallback;
        this.neighborCount = 0;
        this.prepareAABB(Box2dSquareAABBProximity.aabb);
        this.world.QueryAABB((QueryCallback)this, Box2dSquareAABBProximity.aabb.lowerX, Box2dSquareAABBProximity.aabb.lowerY, Box2dSquareAABBProximity.aabb.upperX, Box2dSquareAABBProximity.aabb.upperY);
        this.behaviorCallback = null;
        return this.neighborCount;
    }
    
    protected void prepareAABB(final AABB aabb) {
        final Vector2 position = (Vector2)this.owner.getPosition();
        aabb.lowerX = position.x - this.detectionRadius;
        aabb.lowerY = position.y - this.detectionRadius;
        aabb.upperX = position.x + this.detectionRadius;
        aabb.upperY = position.y + this.detectionRadius;
    }
    
    protected Steerable<Vector2> getSteerable(final Fixture fixture) {
        return (Steerable<Vector2>)fixture.getBody().getUserData();
    }
    
    protected boolean accept(final Steerable<Vector2> steerable) {
        return true;
    }
    
    public boolean reportFixture(final Fixture fixture) {
        final Steerable<Vector2> steerable = this.getSteerable(fixture);
        if (steerable != this.owner && this.accept(steerable) && this.behaviorCallback.reportNeighbor((Steerable)steerable)) {
            ++this.neighborCount;
        }
        return true;
    }
    
    public static class AABB
    {
        float lowerX;
        float lowerY;
        float upperX;
        float upperY;
    }
}
