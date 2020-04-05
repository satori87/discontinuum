// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Disposable;

public class ShapeCache implements Disposable, RenderableProvider
{
    private final MeshBuilder builder;
    private final Mesh mesh;
    private boolean building;
    private final String id = "id";
    private final Renderable renderable;
    
    public ShapeCache() {
        this(5000, 5000, new VertexAttributes(new VertexAttribute[] { new VertexAttribute(1, 3, "a_position"), new VertexAttribute(4, 4, "a_color") }), 1);
    }
    
    public ShapeCache(final int maxVertices, final int maxIndices, final VertexAttributes attributes, final int primitiveType) {
        this.renderable = new Renderable();
        this.mesh = new Mesh(false, maxVertices, maxIndices, attributes);
        this.builder = new MeshBuilder();
        this.renderable.meshPart.mesh = this.mesh;
        this.renderable.meshPart.primitiveType = primitiveType;
        this.renderable.material = new Material();
    }
    
    public MeshPartBuilder begin() {
        return this.begin(1);
    }
    
    public MeshPartBuilder begin(final int primitiveType) {
        if (this.building) {
            throw new GdxRuntimeException("Call end() after calling begin()");
        }
        this.building = true;
        this.builder.begin(this.mesh.getVertexAttributes());
        this.builder.part("id", primitiveType, this.renderable.meshPart);
        return this.builder;
    }
    
    public void end() {
        if (!this.building) {
            throw new GdxRuntimeException("Call begin() prior to calling end()");
        }
        this.building = false;
        this.builder.end(this.mesh);
    }
    
    @Override
    public void getRenderables(final Array<Renderable> renderables, final Pool<Renderable> pool) {
        renderables.add(this.renderable);
    }
    
    public Material getMaterial() {
        return this.renderable.material;
    }
    
    public Matrix4 getWorldTransform() {
        return this.renderable.worldTransform;
    }
    
    @Override
    public void dispose() {
        this.mesh.dispose();
    }
}
