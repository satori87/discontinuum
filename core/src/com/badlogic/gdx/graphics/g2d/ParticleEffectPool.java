// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<PooledEffect>
{
    private final ParticleEffect effect;
    
    public ParticleEffectPool(final ParticleEffect effect, final int initialCapacity, final int max) {
        super(initialCapacity, max);
        this.effect = effect;
    }
    
    @Override
    protected PooledEffect newObject() {
        return new PooledEffect(this.effect);
    }
    
    @Override
    public void free(final PooledEffect effect) {
        super.free(effect);
        effect.reset(false);
        if (effect.xSizeScale != this.effect.xSizeScale || effect.ySizeScale != this.effect.ySizeScale || effect.motionScale != this.effect.motionScale) {
            final Array<ParticleEmitter> emitters = effect.getEmitters();
            final Array<ParticleEmitter> templateEmitters = this.effect.getEmitters();
            for (int i = 0; i < emitters.size; ++i) {
                final ParticleEmitter emitter = emitters.get(i);
                final ParticleEmitter templateEmitter = templateEmitters.get(i);
                emitter.matchSize(templateEmitter);
                emitter.matchMotion(templateEmitter);
            }
            effect.xSizeScale = this.effect.xSizeScale;
            effect.ySizeScale = this.effect.ySizeScale;
            effect.motionScale = this.effect.motionScale;
        }
    }
    
    public class PooledEffect extends ParticleEffect
    {
        PooledEffect(final ParticleEffect effect) {
            super(effect);
        }
        
        public void free() {
            ParticleEffectPool.this.free(this);
        }
    }
}
