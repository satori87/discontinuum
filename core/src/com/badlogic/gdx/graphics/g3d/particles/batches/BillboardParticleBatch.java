// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.batches;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g3d.particles.ResourceData;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardControllerRenderData;

public class BillboardParticleBatch extends BufferedParticleBatch<BillboardControllerRenderData>
{
    protected static final Vector3 TMP_V1;
    protected static final Vector3 TMP_V2;
    protected static final Vector3 TMP_V3;
    protected static final Vector3 TMP_V4;
    protected static final Vector3 TMP_V5;
    protected static final Vector3 TMP_V6;
    protected static final Matrix3 TMP_M3;
    protected static final int sizeAndRotationUsage = 512;
    protected static final int directionUsage = 1024;
    private static final VertexAttributes GPU_ATTRIBUTES;
    private static final VertexAttributes CPU_ATTRIBUTES;
    private static final int GPU_POSITION_OFFSET;
    private static final int GPU_UV_OFFSET;
    private static final int GPU_SIZE_ROTATION_OFFSET;
    private static final int GPU_COLOR_OFFSET;
    private static final int GPU_VERTEX_SIZE;
    private static final int CPU_POSITION_OFFSET;
    private static final int CPU_UV_OFFSET;
    private static final int CPU_COLOR_OFFSET;
    private static final int CPU_VERTEX_SIZE;
    private static final int MAX_PARTICLES_PER_MESH = 8191;
    private static final int MAX_VERTICES_PER_MESH = 32764;
    private RenderablePool renderablePool;
    private Array<Renderable> renderables;
    private float[] vertices;
    private short[] indices;
    private int currentVertexSize;
    private VertexAttributes currentAttributes;
    protected boolean useGPU;
    protected ParticleShader.AlignMode mode;
    protected Texture texture;
    protected BlendingAttribute blendingAttribute;
    protected DepthTestAttribute depthTestAttribute;
    Shader shader;
    
    static {
        TMP_V1 = new Vector3();
        TMP_V2 = new Vector3();
        TMP_V3 = new Vector3();
        TMP_V4 = new Vector3();
        TMP_V5 = new Vector3();
        TMP_V6 = new Vector3();
        TMP_M3 = new Matrix3();
        GPU_ATTRIBUTES = new VertexAttributes(new VertexAttribute[] { new VertexAttribute(1, 3, "a_position"), new VertexAttribute(16, 2, "a_texCoord0"), new VertexAttribute(2, 4, "a_color"), new VertexAttribute(512, 4, "a_sizeAndRotation") });
        CPU_ATTRIBUTES = new VertexAttributes(new VertexAttribute[] { new VertexAttribute(1, 3, "a_position"), new VertexAttribute(16, 2, "a_texCoord0"), new VertexAttribute(2, 4, "a_color") });
        GPU_POSITION_OFFSET = (short)(BillboardParticleBatch.GPU_ATTRIBUTES.findByUsage(1).offset / 4);
        GPU_UV_OFFSET = (short)(BillboardParticleBatch.GPU_ATTRIBUTES.findByUsage(16).offset / 4);
        GPU_SIZE_ROTATION_OFFSET = (short)(BillboardParticleBatch.GPU_ATTRIBUTES.findByUsage(512).offset / 4);
        GPU_COLOR_OFFSET = (short)(BillboardParticleBatch.GPU_ATTRIBUTES.findByUsage(2).offset / 4);
        GPU_VERTEX_SIZE = BillboardParticleBatch.GPU_ATTRIBUTES.vertexSize / 4;
        CPU_POSITION_OFFSET = (short)(BillboardParticleBatch.CPU_ATTRIBUTES.findByUsage(1).offset / 4);
        CPU_UV_OFFSET = (short)(BillboardParticleBatch.CPU_ATTRIBUTES.findByUsage(16).offset / 4);
        CPU_COLOR_OFFSET = (short)(BillboardParticleBatch.CPU_ATTRIBUTES.findByUsage(2).offset / 4);
        CPU_VERTEX_SIZE = BillboardParticleBatch.CPU_ATTRIBUTES.vertexSize / 4;
    }
    
    public BillboardParticleBatch(final ParticleShader.AlignMode mode, final boolean useGPU, final int capacity, final BlendingAttribute blendingAttribute, final DepthTestAttribute depthTestAttribute) {
        super(BillboardControllerRenderData.class);
        this.currentVertexSize = 0;
        this.useGPU = false;
        this.mode = ParticleShader.AlignMode.Screen;
        this.renderables = new Array<Renderable>();
        this.renderablePool = new RenderablePool();
        this.blendingAttribute = blendingAttribute;
        this.depthTestAttribute = depthTestAttribute;
        if (this.blendingAttribute == null) {
            this.blendingAttribute = new BlendingAttribute(1, 771, 1.0f);
        }
        if (this.depthTestAttribute == null) {
            this.depthTestAttribute = new DepthTestAttribute(515, false);
        }
        this.allocIndices();
        this.initRenderData();
        this.ensureCapacity(capacity);
        this.setUseGpu(useGPU);
        this.setAlignMode(mode);
    }
    
    public BillboardParticleBatch(final ParticleShader.AlignMode mode, final boolean useGPU, final int capacity) {
        this(mode, useGPU, capacity, null, null);
    }
    
    public BillboardParticleBatch() {
        this(ParticleShader.AlignMode.Screen, false, 100);
    }
    
    public BillboardParticleBatch(final int capacity) {
        this(ParticleShader.AlignMode.Screen, false, capacity);
    }
    
    public void allocParticlesData(final int capacity) {
        this.vertices = new float[this.currentVertexSize * 4 * capacity];
        this.allocRenderables(capacity);
    }
    
    protected Renderable allocRenderable() {
        final Renderable renderable = new Renderable();
        renderable.meshPart.primitiveType = 4;
        renderable.meshPart.offset = 0;
        renderable.material = new Material(new Attribute[] { this.blendingAttribute, this.depthTestAttribute, TextureAttribute.createDiffuse(this.texture) });
        (renderable.meshPart.mesh = new Mesh(false, 32764, 49146, this.currentAttributes)).setIndices(this.indices);
        renderable.shader = this.shader;
        return renderable;
    }
    
    private void allocIndices() {
        final int indicesCount = 49146;
        this.indices = new short[indicesCount];
        for (int i = 0, vertex = 0; i < indicesCount; i += 6, vertex += 4) {
            this.indices[i] = (short)vertex;
            this.indices[i + 1] = (short)(vertex + 1);
            this.indices[i + 2] = (short)(vertex + 2);
            this.indices[i + 3] = (short)(vertex + 2);
            this.indices[i + 4] = (short)(vertex + 3);
            this.indices[i + 5] = (short)vertex;
        }
    }
    
    private void allocRenderables(final int capacity) {
        final int meshCount = MathUtils.ceil((float)(capacity / 8191));
        final int free = this.renderablePool.getFree();
        if (free < meshCount) {
            for (int i = 0, left = meshCount - free; i < left; ++i) {
                this.renderablePool.free(this.renderablePool.newObject());
            }
        }
    }
    
    protected Shader getShader(final Renderable renderable) {
        final Shader shader = this.useGPU ? new ParticleShader(renderable, new ParticleShader.Config(this.mode)) : new DefaultShader(renderable);
        shader.init();
        return shader;
    }
    
    private void allocShader() {
        final Renderable allocRenderable;
        final Renderable newRenderable = allocRenderable = this.allocRenderable();
        final Shader shader = this.getShader(newRenderable);
        allocRenderable.shader = shader;
        this.shader = shader;
        this.renderablePool.free(newRenderable);
    }
    
    private void clearRenderablesPool() {
        this.renderablePool.freeAll(this.renderables);
        for (int i = 0, free = this.renderablePool.getFree(); i < free; ++i) {
            final Renderable renderable = this.renderablePool.obtain();
            renderable.meshPart.mesh.dispose();
        }
        this.renderables.clear();
    }
    
    public void setVertexData() {
        if (this.useGPU) {
            this.currentAttributes = BillboardParticleBatch.GPU_ATTRIBUTES;
            this.currentVertexSize = BillboardParticleBatch.GPU_VERTEX_SIZE;
        }
        else {
            this.currentAttributes = BillboardParticleBatch.CPU_ATTRIBUTES;
            this.currentVertexSize = BillboardParticleBatch.CPU_VERTEX_SIZE;
        }
    }
    
    private void initRenderData() {
        this.setVertexData();
        this.clearRenderablesPool();
        this.allocShader();
        this.resetCapacity();
    }
    
    public void setAlignMode(final ParticleShader.AlignMode mode) {
        if (mode != this.mode) {
            this.mode = mode;
            if (this.useGPU) {
                this.initRenderData();
                this.allocRenderables(this.bufferedParticlesCount);
            }
        }
    }
    
    public ParticleShader.AlignMode getAlignMode() {
        return this.mode;
    }
    
    public void setUseGpu(final boolean useGPU) {
        if (this.useGPU != useGPU) {
            this.useGPU = useGPU;
            this.initRenderData();
            this.allocRenderables(this.bufferedParticlesCount);
        }
    }
    
    public boolean isUseGPU() {
        return this.useGPU;
    }
    
    public void setTexture(final Texture texture) {
        this.renderablePool.freeAll(this.renderables);
        this.renderables.clear();
        for (int i = 0, free = this.renderablePool.getFree(); i < free; ++i) {
            final Renderable renderable = this.renderablePool.obtain();
            final TextureAttribute attribute = (TextureAttribute)renderable.material.get(TextureAttribute.Diffuse);
            attribute.textureDescription.texture = texture;
        }
        this.texture = texture;
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    @Override
    public void begin() {
        super.begin();
        this.renderablePool.freeAll(this.renderables);
        this.renderables.clear();
    }
    
    private static void putVertex(final float[] vertices, final int offset, final float x, final float y, final float z, final float u, final float v, final float scaleX, final float scaleY, final float cosRotation, final float sinRotation, final float r, final float g, final float b, final float a) {
        vertices[offset + BillboardParticleBatch.GPU_POSITION_OFFSET] = x;
        vertices[offset + BillboardParticleBatch.GPU_POSITION_OFFSET + 1] = y;
        vertices[offset + BillboardParticleBatch.GPU_POSITION_OFFSET + 2] = z;
        vertices[offset + BillboardParticleBatch.GPU_UV_OFFSET] = u;
        vertices[offset + BillboardParticleBatch.GPU_UV_OFFSET + 1] = v;
        vertices[offset + BillboardParticleBatch.GPU_SIZE_ROTATION_OFFSET] = scaleX;
        vertices[offset + BillboardParticleBatch.GPU_SIZE_ROTATION_OFFSET + 1] = scaleY;
        vertices[offset + BillboardParticleBatch.GPU_SIZE_ROTATION_OFFSET + 2] = cosRotation;
        vertices[offset + BillboardParticleBatch.GPU_SIZE_ROTATION_OFFSET + 3] = sinRotation;
        vertices[offset + BillboardParticleBatch.GPU_COLOR_OFFSET] = r;
        vertices[offset + BillboardParticleBatch.GPU_COLOR_OFFSET + 1] = g;
        vertices[offset + BillboardParticleBatch.GPU_COLOR_OFFSET + 2] = b;
        vertices[offset + BillboardParticleBatch.GPU_COLOR_OFFSET + 3] = a;
    }
    
    private static void putVertex(final float[] vertices, final int offset, final Vector3 p, final float u, final float v, final float r, final float g, final float b, final float a) {
        vertices[offset + BillboardParticleBatch.CPU_POSITION_OFFSET] = p.x;
        vertices[offset + BillboardParticleBatch.CPU_POSITION_OFFSET + 1] = p.y;
        vertices[offset + BillboardParticleBatch.CPU_POSITION_OFFSET + 2] = p.z;
        vertices[offset + BillboardParticleBatch.CPU_UV_OFFSET] = u;
        vertices[offset + BillboardParticleBatch.CPU_UV_OFFSET + 1] = v;
        vertices[offset + BillboardParticleBatch.CPU_COLOR_OFFSET] = r;
        vertices[offset + BillboardParticleBatch.CPU_COLOR_OFFSET + 1] = g;
        vertices[offset + BillboardParticleBatch.CPU_COLOR_OFFSET + 2] = b;
        vertices[offset + BillboardParticleBatch.CPU_COLOR_OFFSET + 3] = a;
    }
    
    private void fillVerticesGPU(final int[] particlesOffset) {
        int tp = 0;
        for (final BillboardControllerRenderData data : this.renderData) {
            final ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            final ParallelArray.FloatChannel regionChannel = data.regionChannel;
            final ParallelArray.FloatChannel positionChannel = data.positionChannel;
            final ParallelArray.FloatChannel colorChannel = data.colorChannel;
            final ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            for (int p = 0, c = data.controller.particles.size; p < c; ++p, ++tp) {
                int baseOffset = particlesOffset[tp] * this.currentVertexSize * 4;
                final float scale = scaleChannel.data[p * scaleChannel.strideSize];
                final int regionOffset = p * regionChannel.strideSize;
                final int positionOffset = p * positionChannel.strideSize;
                final int colorOffset = p * colorChannel.strideSize;
                final int rotationOffset = p * rotationChannel.strideSize;
                final float px = positionChannel.data[positionOffset + 0];
                final float py = positionChannel.data[positionOffset + 1];
                final float pz = positionChannel.data[positionOffset + 2];
                final float u = regionChannel.data[regionOffset + 0];
                final float v = regionChannel.data[regionOffset + 1];
                final float u2 = regionChannel.data[regionOffset + 2];
                final float v2 = regionChannel.data[regionOffset + 3];
                final float sx = regionChannel.data[regionOffset + 4] * scale;
                final float sy = regionChannel.data[regionOffset + 5] * scale;
                final float r = colorChannel.data[colorOffset + 0];
                final float g = colorChannel.data[colorOffset + 1];
                final float b = colorChannel.data[colorOffset + 2];
                final float a = colorChannel.data[colorOffset + 3];
                final float cosRotation = rotationChannel.data[rotationOffset + 0];
                final float sinRotation = rotationChannel.data[rotationOffset + 1];
                putVertex(this.vertices, baseOffset, px, py, pz, u, v2, -sx, -sy, cosRotation, sinRotation, r, g, b, a);
                baseOffset += this.currentVertexSize;
                putVertex(this.vertices, baseOffset, px, py, pz, u2, v2, sx, -sy, cosRotation, sinRotation, r, g, b, a);
                baseOffset += this.currentVertexSize;
                putVertex(this.vertices, baseOffset, px, py, pz, u2, v, sx, sy, cosRotation, sinRotation, r, g, b, a);
                baseOffset += this.currentVertexSize;
                putVertex(this.vertices, baseOffset, px, py, pz, u, v, -sx, sy, cosRotation, sinRotation, r, g, b, a);
                baseOffset += this.currentVertexSize;
            }
        }
    }
    
    private void fillVerticesToViewPointCPU(final int[] particlesOffset) {
        int tp = 0;
        for (final BillboardControllerRenderData data : this.renderData) {
            final ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            final ParallelArray.FloatChannel regionChannel = data.regionChannel;
            final ParallelArray.FloatChannel positionChannel = data.positionChannel;
            final ParallelArray.FloatChannel colorChannel = data.colorChannel;
            final ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            for (int p = 0, c = data.controller.particles.size; p < c; ++p, ++tp) {
                int baseOffset = particlesOffset[tp] * this.currentVertexSize * 4;
                final float scale = scaleChannel.data[p * scaleChannel.strideSize];
                final int regionOffset = p * regionChannel.strideSize;
                final int positionOffset = p * positionChannel.strideSize;
                final int colorOffset = p * colorChannel.strideSize;
                final int rotationOffset = p * rotationChannel.strideSize;
                final float px = positionChannel.data[positionOffset + 0];
                final float py = positionChannel.data[positionOffset + 1];
                final float pz = positionChannel.data[positionOffset + 2];
                final float u = regionChannel.data[regionOffset + 0];
                final float v = regionChannel.data[regionOffset + 1];
                final float u2 = regionChannel.data[regionOffset + 2];
                final float v2 = regionChannel.data[regionOffset + 3];
                final float sx = regionChannel.data[regionOffset + 4] * scale;
                final float sy = regionChannel.data[regionOffset + 5] * scale;
                final float r = colorChannel.data[colorOffset + 0];
                final float g = colorChannel.data[colorOffset + 1];
                final float b = colorChannel.data[colorOffset + 2];
                final float a = colorChannel.data[colorOffset + 3];
                final float cosRotation = rotationChannel.data[rotationOffset + 0];
                final float sinRotation = rotationChannel.data[rotationOffset + 1];
                final Vector3 look = BillboardParticleBatch.TMP_V3.set(this.camera.position).sub(px, py, pz).nor();
                final Vector3 right = BillboardParticleBatch.TMP_V1.set(this.camera.up).crs(look).nor();
                final Vector3 up = BillboardParticleBatch.TMP_V2.set(look).crs(right);
                right.scl(sx);
                up.scl(sy);
                if (cosRotation != 1.0f) {
                    BillboardParticleBatch.TMP_M3.setToRotation(look, cosRotation, sinRotation);
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x, -BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y, -BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x, BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y, BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u2, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x, BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y, BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u2, v, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x, -BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y, -BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u, v, r, g, b, a);
                }
                else {
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x + px, -BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y + py, -BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z + pz), u, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x + px, BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y + py, BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z + pz), u2, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x + px, BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y + py, BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z + pz), u2, v, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x + px, -BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y + py, -BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z + pz), u, v, r, g, b, a);
                }
            }
        }
    }
    
    private void fillVerticesToScreenCPU(final int[] particlesOffset) {
        final Vector3 look = BillboardParticleBatch.TMP_V3.set(this.camera.direction).scl(-1.0f);
        final Vector3 right = BillboardParticleBatch.TMP_V4.set(this.camera.up).crs(look).nor();
        final Vector3 up = this.camera.up;
        int tp = 0;
        for (final BillboardControllerRenderData data : this.renderData) {
            final ParallelArray.FloatChannel scaleChannel = data.scaleChannel;
            final ParallelArray.FloatChannel regionChannel = data.regionChannel;
            final ParallelArray.FloatChannel positionChannel = data.positionChannel;
            final ParallelArray.FloatChannel colorChannel = data.colorChannel;
            final ParallelArray.FloatChannel rotationChannel = data.rotationChannel;
            for (int p = 0, c = data.controller.particles.size; p < c; ++p, ++tp) {
                int baseOffset = particlesOffset[tp] * this.currentVertexSize * 4;
                final float scale = scaleChannel.data[p * scaleChannel.strideSize];
                final int regionOffset = p * regionChannel.strideSize;
                final int positionOffset = p * positionChannel.strideSize;
                final int colorOffset = p * colorChannel.strideSize;
                final int rotationOffset = p * rotationChannel.strideSize;
                final float px = positionChannel.data[positionOffset + 0];
                final float py = positionChannel.data[positionOffset + 1];
                final float pz = positionChannel.data[positionOffset + 2];
                final float u = regionChannel.data[regionOffset + 0];
                final float v = regionChannel.data[regionOffset + 1];
                final float u2 = regionChannel.data[regionOffset + 2];
                final float v2 = regionChannel.data[regionOffset + 3];
                final float sx = regionChannel.data[regionOffset + 4] * scale;
                final float sy = regionChannel.data[regionOffset + 5] * scale;
                final float r = colorChannel.data[colorOffset + 0];
                final float g = colorChannel.data[colorOffset + 1];
                final float b = colorChannel.data[colorOffset + 2];
                final float a = colorChannel.data[colorOffset + 3];
                final float cosRotation = rotationChannel.data[rotationOffset + 0];
                final float sinRotation = rotationChannel.data[rotationOffset + 1];
                BillboardParticleBatch.TMP_V1.set(right).scl(sx);
                BillboardParticleBatch.TMP_V2.set(up).scl(sy);
                if (cosRotation != 1.0f) {
                    BillboardParticleBatch.TMP_M3.setToRotation(look, cosRotation, sinRotation);
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x, -BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y, -BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x, BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y, BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u2, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x, BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y, BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u2, v, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x, -BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y, -BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z).mul(BillboardParticleBatch.TMP_M3).add(px, py, pz), u, v, r, g, b, a);
                }
                else {
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x + px, -BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y + py, -BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z + pz), u, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x - BillboardParticleBatch.TMP_V2.x + px, BillboardParticleBatch.TMP_V1.y - BillboardParticleBatch.TMP_V2.y + py, BillboardParticleBatch.TMP_V1.z - BillboardParticleBatch.TMP_V2.z + pz), u2, v2, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x + px, BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y + py, BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z + pz), u2, v, r, g, b, a);
                    baseOffset += this.currentVertexSize;
                    putVertex(this.vertices, baseOffset, BillboardParticleBatch.TMP_V6.set(-BillboardParticleBatch.TMP_V1.x + BillboardParticleBatch.TMP_V2.x + px, -BillboardParticleBatch.TMP_V1.y + BillboardParticleBatch.TMP_V2.y + py, -BillboardParticleBatch.TMP_V1.z + BillboardParticleBatch.TMP_V2.z + pz), u, v, r, g, b, a);
                }
            }
        }
    }
    
    @Override
    protected void flush(final int[] offsets) {
        if (this.useGPU) {
            this.fillVerticesGPU(offsets);
        }
        else if (this.mode == ParticleShader.AlignMode.Screen) {
            this.fillVerticesToScreenCPU(offsets);
        }
        else if (this.mode == ParticleShader.AlignMode.ViewPoint) {
            this.fillVerticesToViewPointCPU(offsets);
        }
        for (int addedVertexCount = 0, vCount = this.bufferedParticlesCount * 4, v = 0; v < vCount; v += addedVertexCount) {
            addedVertexCount = Math.min(vCount - v, 32764);
            final Renderable renderable = this.renderablePool.obtain();
            renderable.meshPart.size = addedVertexCount / 4 * 6;
            renderable.meshPart.mesh.setVertices(this.vertices, this.currentVertexSize * v, this.currentVertexSize * addedVertexCount);
            renderable.meshPart.update();
            this.renderables.add(renderable);
        }
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        for (final Renderable renderable : this.renderables) {
            renderables.add(pool.obtain().set(renderable));
        }
    }
    
    @Override
    public void save(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.createSaveData("billboardBatch");
        data.save("cfg", new Config(this.useGPU, this.mode));
        data.saveAsset(manager.getAssetFileName(this.texture), Texture.class);
    }
    
    @Override
    public void load(final AssetManager manager, final ResourceData resources) {
        final ResourceData.SaveData data = resources.getSaveData("billboardBatch");
        if (data != null) {
            this.setTexture(manager.get((AssetDescriptor<Texture>)data.loadAsset()));
            final Config cfg = data.load("cfg");
            this.setUseGpu(cfg.useGPU);
            this.setAlignMode(cfg.mode);
        }
    }
    
    private class RenderablePool extends Pool<Renderable>
    {
        public RenderablePool() {
        }
        
        public Renderable newObject() {
            return BillboardParticleBatch.this.allocRenderable();
        }
    }
    
    public static class Config
    {
        boolean useGPU;
        ParticleShader.AlignMode mode;
        
        public Config() {
        }
        
        public Config(final boolean useGPU, final ParticleShader.AlignMode mode) {
            this.useGPU = useGPU;
            this.mode = mode;
        }
    }
}
