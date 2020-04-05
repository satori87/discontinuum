// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.assets.AssetDescriptor;
import java.util.Iterator;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.utils.Array;

public abstract class ParticleControllerInfluencer extends Influencer
{
    public Array<ParticleController> templates;
    ParallelArray.ObjectChannel<ParticleController> particleControllerChannel;
    
    public ParticleControllerInfluencer() {
        this.templates = new Array<ParticleController>(true, 1, ParticleController.class);
    }
    
    public ParticleControllerInfluencer(final ParticleController... templates) {
        this.templates = new Array<ParticleController>(templates);
    }
    
    public ParticleControllerInfluencer(final ParticleControllerInfluencer influencer) {
        this((ParticleController[])influencer.templates.items);
    }
    
    @Override
    public void allocateChannels() {
        this.particleControllerChannel = this.controller.particles.addChannel(ParticleChannels.ParticleController);
    }
    
    @Override
    public void end() {
        for (int i = 0; i < this.controller.particles.size; ++i) {
            this.particleControllerChannel.data[i].end();
        }
    }
    
    @Override
    public void dispose() {
        if (this.controller != null) {
            for (int i = 0; i < this.controller.particles.size; ++i) {
                final ParticleController controller = this.particleControllerChannel.data[i];
                if (controller != null) {
                    controller.dispose();
                    this.particleControllerChannel.data[i] = null;
                }
            }
        }
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.createSaveData();
        final Array<ParticleEffect> effects = manager.getAll(ParticleEffect.class, new Array<ParticleEffect>());
        final Array<ParticleController> controllers = new Array<ParticleController>(this.templates);
        final Array<IntArray> effectsIndices = new Array<IntArray>();
        for (int i = 0; i < effects.size && controllers.size > 0; ++i) {
            final ParticleEffect effect = effects.get(i);
            final Array<ParticleController> effectControllers = effect.getControllers();
            final Iterator<ParticleController> iterator = controllers.iterator();
            IntArray indices = null;
            while (iterator.hasNext()) {
                final ParticleController controller = iterator.next();
                int index = -1;
                if ((index = effectControllers.indexOf(controller, true)) > -1) {
                    if (indices == null) {
                        indices = new IntArray();
                    }
                    iterator.remove();
                    indices.add(index);
                }
            }
            if (indices != null) {
                data.saveAsset(manager.getAssetFileName(effect), ParticleEffect.class);
                effectsIndices.add(indices);
            }
        }
        data.save("indices", effectsIndices);
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.getSaveData();
        final Array<IntArray> effectsIndices = data.load("indices");
        final Iterator<IntArray> iterator = effectsIndices.iterator();
        AssetDescriptor descriptor;
        while ((descriptor = data.loadAsset()) != null) {
            final ParticleEffect effect = manager.get((AssetDescriptor<ParticleEffect>)descriptor);
            if (effect == null) {
                throw new RuntimeException("Template is null");
            }
            final Array<ParticleController> effectControllers = effect.getControllers();
            final IntArray effectIndices = iterator.next();
            for (int i = 0, n = effectIndices.size; i < n; ++i) {
                this.templates.add(effectControllers.get(effectIndices.get(i)));
            }
        }
    }
    
    public static class Random extends ParticleControllerInfluencer
    {
        ParticleControllerPool pool;
        
        public Random() {
            this.pool = new ParticleControllerPool();
        }
        
        public Random(final ParticleController... templates) {
            super(templates);
            this.pool = new ParticleControllerPool();
        }
        
        public Random(final Random particleControllerRandom) {
            super(particleControllerRandom);
            this.pool = new ParticleControllerPool();
        }
        
        @Override
        public void init() {
            this.pool.clear();
            for (int i = 0; i < this.controller.emitter.maxParticleCount; ++i) {
                this.pool.free(this.pool.newObject());
            }
        }
        
        @Override
        public void dispose() {
            this.pool.clear();
            super.dispose();
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                final ParticleController controller = this.pool.obtain();
                controller.start();
                this.particleControllerChannel.data[i] = controller;
            }
        }
        
        @Override
        public void killParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                final ParticleController controller = this.particleControllerChannel.data[i];
                controller.end();
                this.pool.free(controller);
                this.particleControllerChannel.data[i] = null;
            }
        }
        
        @Override
        public Random copy() {
            return new Random(this);
        }
        
        private class ParticleControllerPool extends Pool<ParticleController>
        {
            public ParticleControllerPool() {
            }
            
            public ParticleController newObject() {
                final ParticleController controller = Random.this.templates.random().copy();
                controller.init();
                return controller;
            }
            
            @Override
            public void clear() {
                for (int i = 0, free = Random.this.pool.getFree(); i < free; ++i) {
                    Random.this.pool.obtain().dispose();
                }
                super.clear();
            }
        }
    }
    
    public static class Single extends ParticleControllerInfluencer
    {
        public Single(final ParticleController... templates) {
            super(templates);
        }
        
        public Single() {
        }
        
        public Single(final Single particleControllerSingle) {
            super(particleControllerSingle);
        }
        
        @Override
        public void init() {
            final ParticleController first = this.templates.first();
            for (int i = 0, c = this.controller.particles.capacity; i < c; ++i) {
                final ParticleController copy = first.copy();
                copy.init();
                this.particleControllerChannel.data[i] = copy;
            }
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                this.particleControllerChannel.data[i].start();
            }
        }
        
        @Override
        public void killParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                this.particleControllerChannel.data[i].end();
            }
        }
        
        @Override
        public Single copy() {
            return new Single(this);
        }
    }
}
