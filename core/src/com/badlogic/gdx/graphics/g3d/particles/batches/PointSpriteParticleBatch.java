// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.batches;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.particles.renderers.PointSpriteControllerRenderData;

public class PointSpriteParticleBatch extends BufferedParticleBatch<PointSpriteControllerRenderData>
{
    private static boolean pointSpritesEnabled;
    protected static final Vector3 TMP_V1;
    protected static final int sizeAndRotationUsage = 512;
    protected static final VertexAttributes CPU_ATTRIBUTES;
    protected static final int CPU_VERTEX_SIZE;
    protected static final int CPU_POSITION_OFFSET;
    protected static final int CPU_COLOR_OFFSET;
    protected static final int CPU_REGION_OFFSET;
    protected static final int CPU_SIZE_AND_ROTATION_OFFSET;
    private float[] vertices;
    Renderable renderable;
    
    static {
        PointSpriteParticleBatch.pointSpritesEnabled = false;
        TMP_V1 = new Vector3();
        CPU_ATTRIBUTES = new VertexAttributes(new VertexAttribute[] { new VertexAttribute(1, 3, "a_position"), new VertexAttribute(2, 4, "a_color"), new VertexAttribute(16, 4, "a_region"), new VertexAttribute(512, 3, "a_sizeAndRotation") });
        CPU_VERTEX_SIZE = (short)(PointSpriteParticleBatch.CPU_ATTRIBUTES.vertexSize / 4);
        CPU_POSITION_OFFSET = (short)(PointSpriteParticleBatch.CPU_ATTRIBUTES.findByUsage(1).offset / 4);
        CPU_COLOR_OFFSET = (short)(PointSpriteParticleBatch.CPU_ATTRIBUTES.findByUsage(2).offset / 4);
        CPU_REGION_OFFSET = (short)(PointSpriteParticleBatch.CPU_ATTRIBUTES.findByUsage(16).offset / 4);
        CPU_SIZE_AND_ROTATION_OFFSET = (short)(PointSpriteParticleBatch.CPU_ATTRIBUTES.findByUsage(512).offset / 4);
    }
    
    private static void enablePointSprites() {
        Gdx.gl.glEnable(34370);
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.gl.glEnable(34913);
        }
        PointSpriteParticleBatch.pointSpritesEnabled = true;
    }
    
    public PointSpriteParticleBatch() {
        this(1000);
    }
    
    public PointSpriteParticleBatch(final int capacity) {
        this(capacity, new ParticleShader.Config(ParticleShader.ParticleType.Point));
    }
    
    public PointSpriteParticleBatch(final int capacity, final ParticleShader.Config shaderConfig) {
        super(PointSpriteControllerRenderData.class);
        if (!PointSpriteParticleBatch.pointSpritesEnabled) {
            enablePointSprites();
        }
        this.allocRenderable();
        this.ensureCapacity(capacity);
        (this.renderable.shader = new ParticleShader(this.renderable, shaderConfig)).init();
    }
    
    @Override
    protected void allocParticlesData(final int capacity) {
        this.vertices = new float[capacity * PointSpriteParticleBatch.CPU_VERTEX_SIZE];
        if (this.renderable.meshPart.mesh != null) {
            this.renderable.meshPart.mesh.dispose();
        }
        this.renderable.meshPart.mesh = new Mesh(false, capacity, 0, PointSpriteParticleBatch.CPU_ATTRIBUTES);
    }
    
    protected void allocRenderable() {
        this.renderable = new Renderable();
        this.renderable.meshPart.primitiveType = 0;
        this.renderable.meshPart.offset = 0;
        this.renderable.material = new Material(new Attribute[] { new BlendingAttribute(1, 771, 1.0f), new DepthTestAttribute(515, false), TextureAttribute.createDiffuse((Texture)null) });
    }
    
    public void setTexture(final Texture texture) {
        final TextureAttribute attribute = (TextureAttribute)this.renderable.material.get(TextureAttribute.Diffuse);
        attribute.textureDescription.texture = texture;
    }
    
    public Texture getTexture() {
        final TextureAttribute attribute = (TextureAttribute)this.renderable.material.get(TextureAttribute.Diffuse);
        return attribute.textureDescription.texture;
    }
    
    @Override
    protected void flush(final int[] offsets) {
        int tp = 0;
        for (final PointSpriteControllerRenderData data : this.renderData) {
            final ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            final ParallelArray.FloatChannel regionChannel = data.regionChannel;
            final ParallelArray.FloatChannel positionChannel = data.positionChannel;
            final ParallelArray.FloatChannel colorChannel = data.colorChannel;
            final ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            for (int p = 0; p < data.controller.particles.size; ++p, ++tp) {
                final int offset = offsets[tp] * PointSpriteParticleBatch.CPU_VERTEX_SIZE;
                final int regionOffset = p * regionChannel.strideSize;
                final int positionOffset = p * positionChannel.strideSize;
                final int colorOffset = p * colorChannel.strideSize;
                final int rotationOffset = p * rotationChannel.strideSize;
                this.vertices[offset + PointSpriteParticleBatch.CPU_POSITION_OFFSET] = positionChannel.data[positionOffset + 0];
                this.vertices[offset + PointSpriteParticleBatch.CPU_POSITION_OFFSET + 1] = positionChannel.data[positionOffset + 1];
                this.vertices[offset + PointSpriteParticleBatch.CPU_POSITION_OFFSET + 2] = positionChannel.data[positionOffset + 2];
                this.vertices[offset + PointSpriteParticleBatch.CPU_COLOR_OFFSET] = colorChannel.data[colorOffset + 0];
                this.vertices[offset + PointSpriteParticleBatch.CPU_COLOR_OFFSET + 1] = colorChannel.data[colorOffset + 1];
                this.vertices[offset + PointSpriteParticleBatch.CPU_COLOR_OFFSET + 2] = colorChannel.data[colorOffset + 2];
                this.vertices[offset + PointSpriteParticleBatch.CPU_COLOR_OFFSET + 3] = colorChannel.data[colorOffset + 3];
                this.vertices[offset + PointSpriteParticleBatch.CPU_SIZE_AND_ROTATION_OFFSET] = scaleChannel.data[p * scaleChannel.strideSize];
                this.vertices[offset + PointSpriteParticleBatch.CPU_SIZE_AND_ROTATION_OFFSET + 1] = rotationChannel.data[rotationOffset + 0];
                this.vertices[offset + PointSpriteParticleBatch.CPU_SIZE_AND_ROTATION_OFFSET + 2] = rotationChannel.data[rotationOffset + 1];
                this.vertices[offset + PointSpriteParticleBatch.CPU_REGION_OFFSET] = regionChannel.data[regionOffset + 0];
                this.vertices[offset + PointSpriteParticleBatch.CPU_REGION_OFFSET + 1] = regionChannel.data[regionOffset + 1];
                this.vertices[offset + PointSpriteParticleBatch.CPU_REGION_OFFSET + 2] = regionChannel.data[regionOffset + 2];
                this.vertices[offset + PointSpriteParticleBatch.CPU_REGION_OFFSET + 3] = regionChannel.data[regionOffset + 3];
            }
        }
        this.renderable.meshPart.size = this.bufferedParticlesCount;
        this.renderable.meshPart.mesh.setVertices(this.vertices, 0, this.bufferedParticlesCount * PointSpriteParticleBatch.CPU_VERTEX_SIZE);
        this.renderable.meshPart.update();
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        if (this.bufferedParticlesCount > 0) {
            renderables.add(pool.obtain().set(this.renderable));
        }
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.createSaveData("pointSpriteBatch");
        data.saveAsset(manager.getAssetFileName(this.getTexture()), Texture.class);
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.getSaveData("pointSpriteBatch");
        if (data != null) {
            this.setTexture(manager.get((AssetDescriptor<Texture>)data.loadAsset()));
        }
    }
}
