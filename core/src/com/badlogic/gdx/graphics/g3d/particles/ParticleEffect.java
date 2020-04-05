// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ParticleEffect implements Disposable, ResourceData.Configurable
{
    private Array<ParticleController> controllers;
    private BoundingBox bounds;
    
    public ParticleEffect() {
        this.controllers = new Array<ParticleController>(true, 3, ParticleController.class);
    }
    
    public ParticleEffect(final ParticleEffect effect) {
        this.controllers = new Array<ParticleController>(true, effect.controllers.size);
        for (int i = 0, n = effect.controllers.size; i < n; ++i) {
            this.controllers.add(effect.controllers.get(i).copy());
        }
    }
    
    public ParticleEffect(final ParticleController... emitters) {
        this.controllers = new Array<ParticleController>(emitters);
    }
    
    public void init() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).init();
        }
    }
    
    public void start() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).start();
        }
    }
    
    public void end() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).end();
        }
    }
    
    public void reset() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).reset();
        }
    }
    
    public void update() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).update();
        }
    }
    
    public void update(final float deltaTime) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).update(deltaTime);
        }
    }
    
    public void draw() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).draw();
        }
    }
    
    public boolean isComplete() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            if (!this.controllers.get(i).isComplete()) {
                return false;
            }
        }
        return true;
    }
    
    public void setTransform(final Matrix4 transform) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).setTransform(transform);
        }
    }
    
    public void rotate(final Quaternion rotation) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).rotate(rotation);
        }
    }
    
    public void rotate(final Vector3 axis, final float angle) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).rotate(axis, angle);
        }
    }
    
    public void translate(final Vector3 translation) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).translate(translation);
        }
    }
    
    public void scale(final float scaleX, final float scaleY, final float scaleZ) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).scale(scaleX, scaleY, scaleZ);
        }
    }
    
    public void scale(final Vector3 scale) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).scale(scale.x, scale.y, scale.z);
        }
    }
    
    public Array<ParticleController> getControllers() {
        return this.controllers;
    }
    
    public ParticleController findController(final String name) {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            final ParticleController emitter = this.controllers.get(i);
            if (emitter.name.equals(name)) {
                return emitter;
            }
        }
        return null;
    }
    
    @Override
    public void dispose() {
        for (int i = 0, n = this.controllers.size; i < n; ++i) {
            this.controllers.get(i).dispose();
        }
    }
    
    public BoundingBox getBoundingBox() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox();
        }
        final BoundingBox bounds = this.bounds;
        bounds.inf();
        for (final ParticleController emitter : this.controllers) {
            bounds.ext(emitter.getBoundingBox());
        }
        return bounds;
    }
    
    public void setBatch(final Array<ParticleBatch<?>> batches) {
        for (final ParticleController controller : this.controllers) {
            for (final ParticleBatch<?> batch : batches) {
                if (controller.renderer.setBatch(batch)) {
                    break;
                }
            }
        }
    }
    
    public ParticleEffect copy() {
        return new ParticleEffect(this);
    }
    
    @Override
    public void save(final AssetManager assetManager, final ResourceData data) {
        for (final ParticleController controller : this.controllers) {
            controller.save(assetManager, data);
        }
    }
    
    @Override
    public void load(final AssetManager assetManager, final ResourceData data) {
        final int i = 0;
        for (final ParticleController controller : this.controllers) {
            controller.load(assetManager, data);
        }
    }
}
