// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g3d.Renderable;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;

public final class ParticleSystem implements RenderableProvider
{
    private static ParticleSystem instance;
    private Array<ParticleBatch<?>> batches;
    private Array<ParticleEffect> effects;
    
    @Deprecated
    public static ParticleSystem get() {
        if (ParticleSystem.instance == null) {
            ParticleSystem.instance = new ParticleSystem();
        }
        return ParticleSystem.instance;
    }
    
    public ParticleSystem() {
        this.batches = new Array<ParticleBatch<?>>();
        this.effects = new Array<ParticleEffect>();
    }
    
    public void add(final ParticleBatch<?> batch) {
        this.batches.add(batch);
    }
    
    public void add(final ParticleEffect effect) {
        this.effects.add(effect);
    }
    
    public void remove(final ParticleEffect effect) {
        this.effects.removeValue(effect, true);
    }
    
    public void removeAll() {
        this.effects.clear();
    }
    
    public void update() {
        for (final ParticleEffect effect : this.effects) {
            effect.update();
        }
    }
    
    public void updateAndDraw() {
        for (final ParticleEffect effect : this.effects) {
            effect.update();
            effect.draw();
        }
    }
    
    public void update(final float deltaTime) {
        for (final ParticleEffect effect : this.effects) {
            effect.update(deltaTime);
        }
    }
    
    public void updateAndDraw(final float deltaTime) {
        for (final ParticleEffect effect : this.effects) {
            effect.update(deltaTime);
            effect.draw();
        }
    }
    
    public void begin() {
        for (final ParticleBatch<?> batch : this.batches) {
            batch.begin();
        }
    }
    
    public void draw() {
        for (final ParticleEffect effect : this.effects) {
            effect.draw();
        }
    }
    
    public void end() {
        for (final ParticleBatch<?> batch : this.batches) {
            batch.end();
        }
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (final ParticleBatch<?> batch : this.batches) {
            batch.getRenderables(renderables, pool);
        }
    }
    
    public Array<ParticleBatch<?>> getBatches() {
        return this.batches;
    }
}
