// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.graphics.Mesh;
import java.util.Iterator;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;

public class RenderableShapeBuilder extends BaseShapeBuilder
{
    private static short[] indices;
    private static float[] vertices;
    private static final RenderablePool renderablesPool;
    private static final Array<Renderable> renderables;
    private static final int FLOAT_BYTES = 4;
    
    static {
        renderablesPool = new RenderablePool();
        renderables = new Array<Renderable>();
    }
    
    public static void buildNormals(final MeshPartBuilder builder, final RenderableProvider renderableProvider, final float vectorSize) {
        buildNormals(builder, renderableProvider, vectorSize, RenderableShapeBuilder.tmpColor0.set(0.0f, 0.0f, 1.0f, 1.0f), RenderableShapeBuilder.tmpColor1.set(1.0f, 0.0f, 0.0f, 1.0f), RenderableShapeBuilder.tmpColor2.set(0.0f, 1.0f, 0.0f, 1.0f));
    }
    
    public static void buildNormals(final MeshPartBuilder builder, final RenderableProvider renderableProvider, final float vectorSize, final Color normalColor, final Color tangentColor, final Color binormalColor) {
        renderableProvider.getRenderables(RenderableShapeBuilder.renderables, RenderableShapeBuilder.renderablesPool);
        for (final Renderable renderable : RenderableShapeBuilder.renderables) {
            buildNormals(builder, renderable, vectorSize, normalColor, tangentColor, binormalColor);
        }
        RenderableShapeBuilder.renderablesPool.flush();
        RenderableShapeBuilder.renderables.clear();
    }
    
    public static void buildNormals(final MeshPartBuilder builder, final Renderable renderable, final float vectorSize, final Color normalColor, final Color tangentColor, final Color binormalColor) {
        final Mesh mesh = renderable.meshPart.mesh;
        int positionOffset = -1;
        if (mesh.getVertexAttribute(1) != null) {
            positionOffset = mesh.getVertexAttribute(1).offset / 4;
        }
        int normalOffset = -1;
        if (mesh.getVertexAttribute(8) != null) {
            normalOffset = mesh.getVertexAttribute(8).offset / 4;
        }
        int tangentOffset = -1;
        if (mesh.getVertexAttribute(128) != null) {
            tangentOffset = mesh.getVertexAttribute(128).offset / 4;
        }
        int binormalOffset = -1;
        if (mesh.getVertexAttribute(256) != null) {
            binormalOffset = mesh.getVertexAttribute(256).offset / 4;
        }
        final int attributesSize = mesh.getVertexSize() / 4;
        int verticesOffset = 0;
        int verticesQuantity = 0;
        if (mesh.getNumIndices() > 0) {
            ensureIndicesCapacity(mesh.getNumIndices());
            mesh.getIndices(renderable.meshPart.offset, renderable.meshPart.size, RenderableShapeBuilder.indices, 0);
            final short minVertice = minVerticeInIndices();
            final short maxVertice = maxVerticeInIndices();
            verticesOffset = minVertice;
            verticesQuantity = maxVertice - minVertice;
        }
        else {
            verticesOffset = renderable.meshPart.offset;
            verticesQuantity = renderable.meshPart.size;
        }
        ensureVerticesCapacity(verticesQuantity * attributesSize);
        mesh.getVertices(verticesOffset * attributesSize, verticesQuantity * attributesSize, RenderableShapeBuilder.vertices, 0);
        for (int i = verticesOffset; i < verticesQuantity; ++i) {
            final int id = i * attributesSize;
            RenderableShapeBuilder.tmpV0.set(RenderableShapeBuilder.vertices[id + positionOffset], RenderableShapeBuilder.vertices[id + positionOffset + 1], RenderableShapeBuilder.vertices[id + positionOffset + 2]);
            if (normalOffset != -1) {
                RenderableShapeBuilder.tmpV1.set(RenderableShapeBuilder.vertices[id + normalOffset], RenderableShapeBuilder.vertices[id + normalOffset + 1], RenderableShapeBuilder.vertices[id + normalOffset + 2]);
                RenderableShapeBuilder.tmpV2.set(RenderableShapeBuilder.tmpV0).add(RenderableShapeBuilder.tmpV1.scl(vectorSize));
            }
            if (tangentOffset != -1) {
                RenderableShapeBuilder.tmpV3.set(RenderableShapeBuilder.vertices[id + tangentOffset], RenderableShapeBuilder.vertices[id + tangentOffset + 1], RenderableShapeBuilder.vertices[id + tangentOffset + 2]);
                RenderableShapeBuilder.tmpV4.set(RenderableShapeBuilder.tmpV0).add(RenderableShapeBuilder.tmpV3.scl(vectorSize));
            }
            if (binormalOffset != -1) {
                RenderableShapeBuilder.tmpV5.set(RenderableShapeBuilder.vertices[id + binormalOffset], RenderableShapeBuilder.vertices[id + binormalOffset + 1], RenderableShapeBuilder.vertices[id + binormalOffset + 2]);
                RenderableShapeBuilder.tmpV6.set(RenderableShapeBuilder.tmpV0).add(RenderableShapeBuilder.tmpV5.scl(vectorSize));
            }
            RenderableShapeBuilder.tmpV0.mul(renderable.worldTransform);
            RenderableShapeBuilder.tmpV2.mul(renderable.worldTransform);
            RenderableShapeBuilder.tmpV4.mul(renderable.worldTransform);
            RenderableShapeBuilder.tmpV6.mul(renderable.worldTransform);
            if (normalOffset != -1) {
                builder.setColor(normalColor);
                builder.line(RenderableShapeBuilder.tmpV0, RenderableShapeBuilder.tmpV2);
            }
            if (tangentOffset != -1) {
                builder.setColor(tangentColor);
                builder.line(RenderableShapeBuilder.tmpV0, RenderableShapeBuilder.tmpV4);
            }
            if (binormalOffset != -1) {
                builder.setColor(binormalColor);
                builder.line(RenderableShapeBuilder.tmpV0, RenderableShapeBuilder.tmpV6);
            }
        }
    }
    
    private static void ensureVerticesCapacity(final int capacity) {
        if (RenderableShapeBuilder.vertices == null || RenderableShapeBuilder.vertices.length < capacity) {
            RenderableShapeBuilder.vertices = new float[capacity];
        }
    }
    
    private static void ensureIndicesCapacity(final int capacity) {
        if (RenderableShapeBuilder.indices == null || RenderableShapeBuilder.indices.length < capacity) {
            RenderableShapeBuilder.indices = new short[capacity];
        }
    }
    
    private static short minVerticeInIndices() {
        short min = 32767;
        for (int i = 0; i < RenderableShapeBuilder.indices.length; ++i) {
            if (RenderableShapeBuilder.indices[i] < min) {
                min = RenderableShapeBuilder.indices[i];
            }
        }
        return min;
    }
    
    private static short maxVerticeInIndices() {
        short max = -32768;
        for (int i = 0; i < RenderableShapeBuilder.indices.length; ++i) {
            if (RenderableShapeBuilder.indices[i] > max) {
                max = RenderableShapeBuilder.indices[i];
            }
        }
        return max;
    }
    
    private static class RenderablePool extends FlushablePool<Renderable>
    {
        public RenderablePool() {
        }
        
        @Override
        protected Renderable newObject() {
            return new Renderable();
        }
        
        @Override
        public Renderable obtain() {
            final Renderable renderable = super.obtain();
            renderable.environment = null;
            renderable.material = null;
            renderable.meshPart.set("", null, 0, 0, 0);
            renderable.shader = null;
            renderable.userData = null;
            return renderable;
        }
    }
}
