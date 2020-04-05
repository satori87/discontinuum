// 
// Decompiled by Procyon v0.5.36
// 

package com.bbg.dc.aistuff;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.bbg.dc.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;

public class Box2dRaycastCollisionDetector implements RaycastCollisionDetector<Vector2>
{
    World world;
    Box2dRaycastCallback callback;
    
    public Box2dRaycastCollisionDetector(final World world, final Entity ignore) {
        this(world, new Box2dRaycastCallback(ignore));
    }
    
    public Box2dRaycastCollisionDetector(final World world, final Box2dRaycastCallback callback) {
        this.world = world;
        this.callback = callback;
    }
    
    public boolean collides(final Ray<Vector2> ray) {
        return this.findCollision(null, ray);
    }
    
    public boolean findCollision(final Collision<Vector2> outputCollision, final Ray<Vector2> inputRay) {
        this.callback.collided = false;
        if (!((Vector2)inputRay.start).epsilonEquals((Vector2)inputRay.end, 1.0E-6f)) {
            this.callback.outputCollision = outputCollision;
            this.world.rayCast((RayCastCallback)this.callback, (Vector2)inputRay.start, (Vector2)inputRay.end);
        }
        return this.callback.collided;
    }
    
    public static class Box2dRaycastCallback implements RayCastCallback
    {
        public Collision<Vector2> outputCollision;
        public boolean collided;
        public Entity ignore;
        
        public Box2dRaycastCallback(final Entity ignore) {
            this.ignore = ignore;
        }
        
        public float reportRayFixture(final Fixture fixture, final Vector2 point, final Vector2 normal, final float fraction) {
            if (this.outputCollision != null) {
                this.outputCollision.set((Vector)point, (Vector)normal);
            }
            if (fixture.getBody().getUserData() instanceof Entity) {
                final Entity e = (Entity)fixture.getBody().getUserData();
                if (e != this.ignore) {
                    this.collided = true;
                }
                else {
                    System.out.println("ignored");
                }
            }
            return fraction;
        }
    }
}
