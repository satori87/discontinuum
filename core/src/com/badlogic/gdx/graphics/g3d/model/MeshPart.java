// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Mesh;

public class MeshPart
{
    public String id;
    public int primitiveType;
    public int offset;
    public int size;
    public Mesh mesh;
    public final Vector3 center;
    public final Vector3 halfExtents;
    public float radius;
    private static final BoundingBox bounds;
    
    static {
        bounds = new BoundingBox();
    }
    
    public MeshPart() {
        this.center = new Vector3();
        this.halfExtents = new Vector3();
        this.radius = -1.0f;
    }
    
    public MeshPart(final String id, final Mesh mesh, final int offset, final int size, final int type) {
        this.center = new Vector3();
        this.halfExtents = new Vector3();
        this.radius = -1.0f;
        this.set(id, mesh, offset, size, type);
    }
    
    public MeshPart(final MeshPart copyFrom) {
        this.center = new Vector3();
        this.halfExtents = new Vector3();
        this.radius = -1.0f;
        this.set(copyFrom);
    }
    
    public MeshPart set(final MeshPart other) {
        this.id = other.id;
        this.mesh = other.mesh;
        this.offset = other.offset;
        this.size = other.size;
        this.primitiveType = other.primitiveType;
        this.center.set(other.center);
        this.halfExtents.set(other.halfExtents);
        this.radius = other.radius;
        return this;
    }
    
    public MeshPart set(final String id, final Mesh mesh, final int offset, final int size, final int type) {
        this.id = id;
        this.mesh = mesh;
        this.offset = offset;
        this.size = size;
        this.primitiveType = type;
        this.center.set(0.0f, 0.0f, 0.0f);
        this.halfExtents.set(0.0f, 0.0f, 0.0f);
        this.radius = -1.0f;
        return this;
    }
    
    public void update() {
        this.mesh.calculateBoundingBox(MeshPart.bounds, this.offset, this.size);
        MeshPart.bounds.getCenter(this.center);
        MeshPart.bounds.getDimensions(this.halfExtents).scl(0.5f);
        this.radius = this.halfExtents.len();
    }
    
    public boolean equals(final MeshPart other) {
        return other == this || (other != null && other.mesh == this.mesh && other.primitiveType == this.primitiveType && other.offset == this.offset && other.size == this.size);
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return arg0 != null && (arg0 == this || (arg0 instanceof MeshPart && this.equals((MeshPart)arg0)));
    }
    
    public void render(final ShaderProgram shader, final boolean autoBind) {
        this.mesh.render(shader, this.primitiveType, this.offset, this.size, autoBind);
    }
    
    public void render(final ShaderProgram shader) {
        this.mesh.render(shader, this.primitiveType, this.offset, this.size);
    }
}
