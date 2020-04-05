// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.batches;

import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderData;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ModelInstanceControllerRenderData;

public class ModelInstanceParticleBatch implements ParticleBatch<ModelInstanceControllerRenderData>
{
    Array<ModelInstanceControllerRenderData> controllersRenderData;
    int bufferedParticlesCount;
    
    public ModelInstanceParticleBatch() {
        this.controllersRenderData = new Array<ModelInstanceControllerRenderData>(false, 5);
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (final ModelInstanceControllerRenderData data : this.controllersRenderData) {
            for (int i = 0, count = data.controller.particles.size; i < count; ++i) {
                data.modelInstanceChannel.data[i].getRenderables(renderables, pool);
            }
        }
    }
    
    public int getBufferedCount() {
        return this.bufferedParticlesCount;
    }
    
    @Override
    public void begin() {
        this.controllersRenderData.clear();
        this.bufferedParticlesCount = 0;
    }
    
    @Override
    public void end() {
    }
    
    @Override
    public void draw(final ModelInstanceControllerRenderData data) {
        this.controllersRenderData.add(data);
        this.bufferedParticlesCount += data.controller.particles.size;
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData assetDependencyData) {
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData assetDependencyData) {
    }
}
