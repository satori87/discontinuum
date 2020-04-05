// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.influencers;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.assets.AssetDescriptor;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;

public abstract class ModelInfluencer extends Influencer
{
    public Array<Model> models;
    ParallelArray.ObjectChannel<ModelInstance> modelChannel;
    
    public ModelInfluencer() {
        this.models = new Array<Model>(true, 1, Model.class);
    }
    
    public ModelInfluencer(final Model... models) {
        this.models = new Array<Model>(models);
    }
    
    public ModelInfluencer(final ModelInfluencer influencer) {
        this((Model[])influencer.models.toArray(Model.class));
    }
    
    @Override
    public void allocateChannels() {
        this.modelChannel = this.controller.particles.addChannel(ParticleChannels.ModelInstance);
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.createSaveData();
        for (final Model model : this.models) {
            data.saveAsset(manager.getAssetFileName(model), Model.class);
        }
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.getSaveData();
        AssetDescriptor descriptor;
        while ((descriptor = data.loadAsset()) != null) {
            final Model model = manager.get((AssetDescriptor<Model>)descriptor);
            if (model == null) {
                throw new RuntimeException("Model is null");
            }
            this.models.add(model);
        }
    }
    
    public static class Random extends ModelInfluencer
    {
        ModelInstancePool pool;
        
        public Random() {
            this.pool = new ModelInstancePool();
        }
        
        public Random(final Random influencer) {
            super(influencer);
            this.pool = new ModelInstancePool();
        }
        
        public Random(final Model... models) {
            super(models);
            this.pool = new ModelInstancePool();
        }
        
        @Override
        public void init() {
            this.pool.clear();
        }
        
        @Override
        public void activateParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                this.modelChannel.data[i] = this.pool.obtain();
            }
        }
        
        @Override
        public void killParticles(final int startIndex, final int count) {
            for (int i = startIndex, c = startIndex + count; i < c; ++i) {
                this.pool.free(this.modelChannel.data[i]);
                this.modelChannel.data[i] = null;
            }
        }
        
        @Override
        public Random copy() {
            return new Random(this);
        }
        
        private class ModelInstancePool extends Pool<ModelInstance>
        {
            public ModelInstancePool() {
            }
            
            public ModelInstance newObject() {
                return new ModelInstance(Random.this.models.random());
            }
        }
    }
    
    public static class Single extends ModelInfluencer
    {
        public Single() {
        }
        
        public Single(final Single influencer) {
            super(influencer);
        }
        
        public Single(final Model... models) {
            super(models);
        }
        
        @Override
        public void init() {
            final Model first = this.models.first();
            for (int i = 0, c = this.controller.emitter.maxParticleCount; i < c; ++i) {
                this.modelChannel.data[i] = new ModelInstance(first);
            }
        }
        
        @Override
        public Single copy() {
            return new Single(this);
        }
    }
}
