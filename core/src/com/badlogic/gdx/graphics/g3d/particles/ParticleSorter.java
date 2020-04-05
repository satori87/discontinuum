// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public abstract class ParticleSorter
{
    static final Vector3 TMP_V1;
    protected Camera camera;
    
    static {
        TMP_V1 = new Vector3();
    }
    
    public abstract <T extends ParticleControllerRenderData> int[] sort(final Array<T> p0);
    
    public void setCamera(final Camera camera) {
        this.camera = camera;
    }
    
    public void ensureCapacity(final int capacity) {
    }
    
    public static class None extends ParticleSorter
    {
        int currentCapacity;
        int[] indices;
        
        public None() {
            this.currentCapacity = 0;
        }
        
        @Override
        public void ensureCapacity(final int capacity) {
            if (this.currentCapacity < capacity) {
                this.indices = new int[capacity];
                for (int i = 0; i < capacity; ++i) {
                    this.indices[i] = i;
                }
                this.currentCapacity = capacity;
            }
        }
        
        @Override
        public <T extends ParticleControllerRenderData> int[] sort(final Array<T> renderData) {
            return this.indices;
        }
    }
    
    public static class Distance extends ParticleSorter
    {
        private float[] distances;
        private int[] particleIndices;
        private int[] particleOffsets;
        private int currentSize;
        
        public Distance() {
            this.currentSize = 0;
        }
        
        @Override
        public void ensureCapacity(final int capacity) {
            if (this.currentSize < capacity) {
                this.distances = new float[capacity];
                this.particleIndices = new int[capacity];
                this.particleOffsets = new int[capacity];
                this.currentSize = capacity;
            }
        }
        
        @Override
        public <T extends ParticleControllerRenderData> int[] sort(final Array<T> renderData) {
            final float[] val = this.camera.view.val;
            final float cx = val[2];
            final float cy = val[6];
            final float cz = val[10];
            int count = 0;
            int i = 0;
            for (final ParticleControllerRenderData data : renderData) {
                for (int k = 0, c = i + data.controller.particles.size; i < c; ++i, k += data.positionChannel.strideSize) {
                    this.distances[i] = cx * data.positionChannel.data[k + 0] + cy * data.positionChannel.data[k + 1] + cz * data.positionChannel.data[k + 2];
                    this.particleIndices[i] = i;
                }
                count += data.controller.particles.size;
            }
            this.qsort(0, count - 1);
            for (i = 0; i < count; ++i) {
                this.particleOffsets[this.particleIndices[i]] = i;
            }
            return this.particleOffsets;
        }
        
        public void qsort(final int si, final int ei) {
            if (si < ei) {
                if (ei - si <= 8) {
                    for (int i = si; i <= ei; ++i) {
                        for (int j = i; j > si && this.distances[j - 1] > this.distances[j]; --j) {
                            final float tmp = this.distances[j];
                            this.distances[j] = this.distances[j - 1];
                            this.distances[j - 1] = tmp;
                            final int tmpIndex = this.particleIndices[j];
                            this.particleIndices[j] = this.particleIndices[j - 1];
                            this.particleIndices[j - 1] = tmpIndex;
                        }
                    }
                    return;
                }
                final float pivot = this.distances[si];
                int k = si + 1;
                final int particlesPivotIndex = this.particleIndices[si];
                for (int l = si + 1; l <= ei; ++l) {
                    if (pivot > this.distances[l]) {
                        if (l > k) {
                            final float tmp = this.distances[l];
                            this.distances[l] = this.distances[k];
                            this.distances[k] = tmp;
                            final int tmpIndex = this.particleIndices[l];
                            this.particleIndices[l] = this.particleIndices[k];
                            this.particleIndices[k] = tmpIndex;
                        }
                        ++k;
                    }
                }
                this.distances[si] = this.distances[k - 1];
                this.distances[k - 1] = pivot;
                this.particleIndices[si] = this.particleIndices[k - 1];
                this.particleIndices[k - 1] = particlesPivotIndex;
                this.qsort(si, k - 2);
                this.qsort(k, ei);
            }
        }
    }
}
