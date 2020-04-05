// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.batches;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderData;

public abstract class BufferedParticleBatch<T extends ParticleControllerRenderData> implements ParticleBatch<T>
{
    protected Array<T> renderData;
    protected int bufferedParticlesCount;
    protected int currentCapacity;
    protected ParticleSorter sorter;
    protected Camera camera;
    
    protected BufferedParticleBatch(final Class<T> type) {
        this.currentCapacity = 0;
        this.sorter = new ParticleSorter.Distance();
        this.renderData = new Array<T>(false, 10, type);
    }
    
    @Override
    public void begin() {
        this.renderData.clear();
        this.bufferedParticlesCount = 0;
    }
    
    @Override
    public void draw(final T data) {
        if (data.controller.particles.size > 0) {
            this.renderData.add(data);
            this.bufferedParticlesCount += data.controller.particles.size;
        }
    }
    
    @Override
    public void end() {
        if (this.bufferedParticlesCount > 0) {
            this.ensureCapacity(this.bufferedParticlesCount);
            this.flush(this.sorter.sort(this.renderData));
        }
    }
    
    public void ensureCapacity(final int capacity) {
        if (this.currentCapacity >= capacity) {
            return;
        }
        this.sorter.ensureCapacity(capacity);
        this.allocParticlesData(capacity);
        this.currentCapacity = capacity;
    }
    
    public void resetCapacity() {
        final int n = 0;
        this.bufferedParticlesCount = n;
        this.currentCapacity = n;
    }
    
    protected abstract void allocParticlesData(final int p0);
    
    public void setCamera(final Camera camera) {
        this.camera = camera;
        this.sorter.setCamera(camera);
    }
    
    public ParticleSorter getSorter() {
        return this.sorter;
    }
    
    public void setSorter(final ParticleSorter sorter) {
        (this.sorter = sorter).setCamera(this.camera);
        sorter.ensureCapacity(this.currentCapacity);
    }
    
    protected abstract void flush(final int[] p0);
    
    public int getBufferedCount() {
        return this.bufferedParticlesCount;
    }
}
