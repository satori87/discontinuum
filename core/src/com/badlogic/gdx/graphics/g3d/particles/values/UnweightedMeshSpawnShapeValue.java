// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.Mesh;

public final class UnweightedMeshSpawnShapeValue extends MeshSpawnShapeValue
{
    private float[] vertices;
    private short[] indices;
    private int positionOffset;
    private int vertexSize;
    private int vertexCount;
    private int triangleCount;
    
    public UnweightedMeshSpawnShapeValue(final UnweightedMeshSpawnShapeValue value) {
        super(value);
        this.load(value);
    }
    
    public UnweightedMeshSpawnShapeValue() {
    }
    
    @Override
    public void setMesh(final Mesh mesh, final Model model) {
        super.setMesh(mesh, model);
        this.vertexSize = mesh.getVertexSize() / 4;
        this.positionOffset = mesh.getVertexAttribute(1).offset / 4;
        final int indicesCount = mesh.getNumIndices();
        if (indicesCount > 0) {
            mesh.getIndices(this.indices = new short[indicesCount]);
            this.triangleCount = this.indices.length / 3;
        }
        else {
            this.indices = null;
        }
        this.vertexCount = mesh.getNumVertices();
        mesh.getVertices(this.vertices = new float[this.vertexCount * this.vertexSize]);
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        if (this.indices == null) {
            final int triangleIndex = MathUtils.random(this.vertexCount - 3) * this.vertexSize;
            final int p1Offset = triangleIndex + this.positionOffset;
            final int p2Offset = p1Offset + this.vertexSize;
            final int p3Offset = p2Offset + this.vertexSize;
            final float x1 = this.vertices[p1Offset];
            final float y1 = this.vertices[p1Offset + 1];
            final float z1 = this.vertices[p1Offset + 2];
            final float x2 = this.vertices[p2Offset];
            final float y2 = this.vertices[p2Offset + 1];
            final float z2 = this.vertices[p2Offset + 2];
            final float x3 = this.vertices[p3Offset];
            final float y3 = this.vertices[p3Offset + 1];
            final float z3 = this.vertices[p3Offset + 2];
            Triangle.pick(x1, y1, z1, x2, y2, z2, x3, y3, z3, vector);
        }
        else {
            final int triangleIndex = MathUtils.random(this.triangleCount - 1) * 3;
            final int p1Offset = this.indices[triangleIndex] * this.vertexSize + this.positionOffset;
            final int p2Offset = this.indices[triangleIndex + 1] * this.vertexSize + this.positionOffset;
            final int p3Offset = this.indices[triangleIndex + 2] * this.vertexSize + this.positionOffset;
            final float x1 = this.vertices[p1Offset];
            final float y1 = this.vertices[p1Offset + 1];
            final float z1 = this.vertices[p1Offset + 2];
            final float x2 = this.vertices[p2Offset];
            final float y2 = this.vertices[p2Offset + 1];
            final float z2 = this.vertices[p2Offset + 2];
            final float x3 = this.vertices[p3Offset];
            final float y3 = this.vertices[p3Offset + 1];
            final float z3 = this.vertices[p3Offset + 2];
            Triangle.pick(x1, y1, z1, x2, y2, z2, x3, y3, z3, vector);
        }
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new UnweightedMeshSpawnShapeValue(this);
    }
}
