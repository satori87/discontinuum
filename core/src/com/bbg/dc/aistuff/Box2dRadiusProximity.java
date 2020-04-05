// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.steer.Steerable;

public class Box2dRadiusProximity extends Box2dSquareAABBProximity
{
    public Box2dRadiusProximity(final Steerable<Vector2> owner, final World world, final float detectionRadius) {
        super(owner, world, detectionRadius);
    }
    
    @Override
    protected Steerable<Vector2> getSteerable(final Fixture fixture) {
        return (Steerable<Vector2>)fixture.getBody().getUserData();
    }
    
    @Override
    protected boolean accept(final Steerable<Vector2> steerable) {
        final float range = this.detectionRadius + steerable.getBoundingRadius();
        final float distanceSquare = ((Vector2)steerable.getPosition()).dst2((Vector2)this.owner.getPosition());
        return distanceSquare <= range * range;
    }
}
