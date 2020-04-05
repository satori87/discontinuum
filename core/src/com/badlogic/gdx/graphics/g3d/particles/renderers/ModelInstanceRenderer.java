// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.renderers;

import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ModelInstanceParticleBatch;

public class ModelInstanceRenderer extends ParticleControllerRenderer<ModelInstanceControllerRenderData, ModelInstanceParticleBatch>
{
    private boolean hasColor;
    private boolean hasScale;
    private boolean hasRotation;
    
    public ModelInstanceRenderer() {
        super(new ModelInstanceControllerRenderData());
    }
    
    public ModelInstanceRenderer(final ModelInstanceParticleBatch batch) {
        this();
        this.setBatch(batch);
    }
    
    @Override
    public void allocateChannels() {
        ((ModelInstanceControllerRenderData)this.renderData).positionChannel = this.controller.particles.addChannel(ParticleChannels.Position);
    }
    
    @Override
    public void init() {
        ((ModelInstanceControllerRenderData)this.renderData).modelInstanceChannel = this.controller.particles.getChannel(ParticleChannels.ModelInstance);
        ((ModelInstanceControllerRenderData)this.renderData).colorChannel = this.controller.particles.getChannel(ParticleChannels.Color);
        ((ModelInstanceControllerRenderData)this.renderData).scaleChannel = this.controller.particles.getChannel(ParticleChannels.Scale);
        ((ModelInstanceControllerRenderData)this.renderData).rotationChannel = this.controller.particles.getChannel(ParticleChannels.Rotation3D);
        this.hasColor = (((ModelInstanceControllerRenderData)this.renderData).colorChannel != null);
        this.hasScale = (((ModelInstanceControllerRenderData)this.renderData).scaleChannel != null);
        this.hasRotation = (((ModelInstanceControllerRenderData)this.renderData).rotationChannel != null);
    }
    
    @Override
    public void update() {
        for (int i = 0, positionOffset = 0, c = this.controller.particles.size; i < c; ++i, positionOffset += ((ModelInstanceControllerRenderData)this.renderData).positionChannel.strideSize) {
            final ModelInstance instance = ((ModelInstanceControllerRenderData)this.renderData).modelInstanceChannel.data[i];
            final float scale = this.hasScale ? ((ModelInstanceControllerRenderData)this.renderData).scaleChannel.data[i] : 1.0f;
            float qx = 0.0f;
            float qy = 0.0f;
            float qz = 0.0f;
            float qw = 1.0f;
            if (this.hasRotation) {
                final int rotationOffset = i * ((ModelInstanceControllerRenderData)this.renderData).rotationChannel.strideSize;
                qx = ((ModelInstanceControllerRenderData)this.renderData).rotationChannel.data[rotationOffset + 0];
                qy = ((ModelInstanceControllerRenderData)this.renderData).rotationChannel.data[rotationOffset + 1];
                qz = ((ModelInstanceControllerRenderData)this.renderData).rotationChannel.data[rotationOffset + 2];
                qw = ((ModelInstanceControllerRenderData)this.renderData).rotationChannel.data[rotationOffset + 3];
            }
            instance.transform.set(((ModelInstanceControllerRenderData)this.renderData).positionChannel.data[positionOffset + 0], ((ModelInstanceControllerRenderData)this.renderData).positionChannel.data[positionOffset + 1], ((ModelInstanceControllerRenderData)this.renderData).positionChannel.data[positionOffset + 2], qx, qy, qz, qw, scale, scale, scale);
            if (this.hasColor) {
                final int colorOffset = i * ((ModelInstanceControllerRenderData)this.renderData).colorChannel.strideSize;
                final ColorAttribute colorAttribute = (ColorAttribute)instance.materials.get(0).get(ColorAttribute.Diffuse);
                final BlendingAttribute blendingAttribute = (BlendingAttribute)instance.materials.get(0).get(BlendingAttribute.Type);
                colorAttribute.color.r = ((ModelInstanceControllerRenderData)this.renderData).colorChannel.data[colorOffset + 0];
                colorAttribute.color.g = ((ModelInstanceControllerRenderData)this.renderData).colorChannel.data[colorOffset + 1];
                colorAttribute.color.b = ((ModelInstanceControllerRenderData)this.renderData).colorChannel.data[colorOffset + 2];
                if (blendingAttribute != null) {
                    blendingAttribute.opacity = ((ModelInstanceControllerRenderData)this.renderData).colorChannel.data[colorOffset + 3];
                }
            }
        }
        super.update();
    }
    
    @Override
    public ParticleControllerComponent copy() {
        return new ModelInstanceRenderer((ModelInstanceParticleBatch)this.batch);
    }
    
    @Override
    public boolean isCompatible(final ParticleBatch<?> batch) {
        return batch instanceof ModelInstanceParticleBatch;
    }
}
