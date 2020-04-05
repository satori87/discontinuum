// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.CumulativeDistribution;

public final class WeightMeshSpawnShapeValue extends MeshSpawnShapeValue
{
    private CumulativeDistribution<Triangle> distribution;
    
    public WeightMeshSpawnShapeValue(final WeightMeshSpawnShapeValue value) {
        super(value);
        this.distribution = new CumulativeDistribution<Triangle>();
        this.load(value);
    }
    
    public WeightMeshSpawnShapeValue() {
        this.distribution = new CumulativeDistribution<Triangle>();
    }
    
    @Override
    public void init() {
        this.calculateWeights();
    }
    
    public void calculateWeights() {
        this.distribution.clear();
        final VertexAttributes attributes = this.mesh.getVertexAttributes();
        final int indicesCount = this.mesh.getNumIndices();
        final int vertexCount = this.mesh.getNumVertices();
        final int vertexSize = (short)(attributes.vertexSize / 4);
        final int positionOffset = (short)(attributes.findByUsage(1).offset / 4);
        final float[] vertices = new float[vertexCount * vertexSize];
        this.mesh.getVertices(vertices);
        if (indicesCount > 0) {
            final short[] indices = new short[indicesCount];
            this.mesh.getIndices(indices);
            for (int i = 0; i < indicesCount; i += 3) {
                final int p1Offset = indices[i] * vertexSize + positionOffset;
                final int p2Offset = indices[i + 1] * vertexSize + positionOffset;
                final int p3Offset = indices[i + 2] * vertexSize + positionOffset;
                final float x1 = vertices[p1Offset];
                final float y1 = vertices[p1Offset + 1];
                final float z1 = vertices[p1Offset + 2];
                final float x2 = vertices[p2Offset];
                final float y2 = vertices[p2Offset + 1];
                final float z2 = vertices[p2Offset + 2];
                final float x3 = vertices[p3Offset];
                final float y3 = vertices[p3Offset + 1];
                final float z3 = vertices[p3Offset + 2];
                final float area = Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0f);
                this.distribution.add(new Triangle(x1, y1, z1, x2, y2, z2, x3, y3, z3), area);
            }
        }
        else {
            for (int j = 0; j < vertexCount; j += vertexSize) {
                final int p1Offset2 = j + positionOffset;
                final int p2Offset2 = p1Offset2 + vertexSize;
                final int p3Offset2 = p2Offset2 + vertexSize;
                final float x4 = vertices[p1Offset2];
                final float y4 = vertices[p1Offset2 + 1];
                final float z4 = vertices[p1Offset2 + 2];
                final float x5 = vertices[p2Offset2];
                final float y5 = vertices[p2Offset2 + 1];
                final float z5 = vertices[p2Offset2 + 2];
                final float x6 = vertices[p3Offset2];
                final float y6 = vertices[p3Offset2 + 1];
                final float z6 = vertices[p3Offset2 + 2];
                final float area2 = Math.abs((x4 * (y5 - y6) + x5 * (y6 - y4) + x6 * (y4 - y5)) / 2.0f);
                this.distribution.add(new Triangle(x4, y4, z4, x5, y5, z5, x6, y6, z6), area2);
            }
        }
        this.distribution.generateNormalized();
    }
    
    @Override
    public void spawnAux(final Vector3 vector, final float percent) {
        final Triangle t = this.distribution.value();
        final float a = MathUtils.random();
        final float b = MathUtils.random();
        vector.set(t.x1 + a * (t.x2 - t.x1) + b * (t.x3 - t.x1), t.y1 + a * (t.y2 - t.y1) + b * (t.y3 - t.y1), t.z1 + a * (t.z2 - t.z1) + b * (t.z3 - t.z1));
    }
    
    @Override
    public SpawnShapeValue copy() {
        return new WeightMeshSpawnShapeValue(this);
    }
}
