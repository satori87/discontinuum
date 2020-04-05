// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ShortArray;

public class EarClippingTriangulator
{
    private static final int CONCAVE = -1;
    private static final int CONVEX = 1;
    private final ShortArray indicesArray;
    private short[] indices;
    private float[] vertices;
    private int vertexCount;
    private final IntArray vertexTypes;
    private final ShortArray triangles;
    
    public EarClippingTriangulator() {
        this.indicesArray = new ShortArray();
        this.vertexTypes = new IntArray();
        this.triangles = new ShortArray();
    }
    
    public ShortArray computeTriangles(final FloatArray vertices) {
        return this.computeTriangles(vertices.items, 0, vertices.size);
    }
    
    public ShortArray computeTriangles(final float[] vertices) {
        return this.computeTriangles(vertices, 0, vertices.length);
    }
    
    public ShortArray computeTriangles(final float[] vertices, final int offset, final int count) {
        this.vertices = vertices;
        final int vertexCount2 = count / 2;
        this.vertexCount = vertexCount2;
        final int vertexCount = vertexCount2;
        final int vertexOffset = offset / 2;
        final ShortArray indicesArray = this.indicesArray;
        indicesArray.clear();
        indicesArray.ensureCapacity(vertexCount);
        indicesArray.size = vertexCount;
        final short[] items = indicesArray.items;
        this.indices = items;
        final short[] indices = items;
        if (areVerticesClockwise(vertices, offset, count)) {
            for (short i = 0; i < vertexCount; ++i) {
                indices[i] = (short)(vertexOffset + i);
            }
        }
        else {
            int j = 0;
            final int n = vertexCount - 1;
            while (j < vertexCount) {
                indices[j] = (short)(vertexOffset + n - j);
                ++j;
            }
        }
        final IntArray vertexTypes = this.vertexTypes;
        vertexTypes.clear();
        vertexTypes.ensureCapacity(vertexCount);
        for (int k = 0, n2 = vertexCount; k < n2; ++k) {
            vertexTypes.add(this.classifyVertex(k));
        }
        final ShortArray triangles = this.triangles;
        triangles.clear();
        triangles.ensureCapacity(Math.max(0, vertexCount - 2) * 3);
        this.triangulate();
        return triangles;
    }
    
    private void triangulate() {
        final int[] vertexTypes = this.vertexTypes.items;
        while (this.vertexCount > 3) {
            final int earTipIndex = this.findEarTip();
            this.cutEarTip(earTipIndex);
            final int previousIndex = this.previousIndex(earTipIndex);
            final int nextIndex = (earTipIndex == this.vertexCount) ? 0 : earTipIndex;
            vertexTypes[previousIndex] = this.classifyVertex(previousIndex);
            vertexTypes[nextIndex] = this.classifyVertex(nextIndex);
        }
        if (this.vertexCount == 3) {
            final ShortArray triangles = this.triangles;
            final short[] indices = this.indices;
            triangles.add(indices[0]);
            triangles.add(indices[1]);
            triangles.add(indices[2]);
        }
    }
    
    private int classifyVertex(final int index) {
        final short[] indices = this.indices;
        final int previous = indices[this.previousIndex(index)] * 2;
        final int current = indices[index] * 2;
        final int next = indices[this.nextIndex(index)] * 2;
        final float[] vertices = this.vertices;
        return computeSpannedAreaSign(vertices[previous], vertices[previous + 1], vertices[current], vertices[current + 1], vertices[next], vertices[next + 1]);
    }
    
    private int findEarTip() {
        final int vertexCount = this.vertexCount;
        for (int i = 0; i < vertexCount; ++i) {
            if (this.isEarTip(i)) {
                return i;
            }
        }
        final int[] vertexTypes = this.vertexTypes.items;
        for (int j = 0; j < vertexCount; ++j) {
            if (vertexTypes[j] != -1) {
                return j;
            }
        }
        return 0;
    }
    
    private boolean isEarTip(final int earTipIndex) {
        final int[] vertexTypes = this.vertexTypes.items;
        if (vertexTypes[earTipIndex] == -1) {
            return false;
        }
        final int previousIndex = this.previousIndex(earTipIndex);
        final int nextIndex = this.nextIndex(earTipIndex);
        final short[] indices = this.indices;
        final int p1 = indices[previousIndex] * 2;
        final int p2 = indices[earTipIndex] * 2;
        final int p3 = indices[nextIndex] * 2;
        final float[] vertices = this.vertices;
        final float p1x = vertices[p1];
        final float p1y = vertices[p1 + 1];
        final float p2x = vertices[p2];
        final float p2y = vertices[p2 + 1];
        final float p3x = vertices[p3];
        final float p3y = vertices[p3 + 1];
        for (int i = this.nextIndex(nextIndex); i != previousIndex; i = this.nextIndex(i)) {
            if (vertexTypes[i] != 1) {
                final int v = indices[i] * 2;
                final float vx = vertices[v];
                final float vy = vertices[v + 1];
                if (computeSpannedAreaSign(p3x, p3y, p1x, p1y, vx, vy) >= 0 && computeSpannedAreaSign(p1x, p1y, p2x, p2y, vx, vy) >= 0 && computeSpannedAreaSign(p2x, p2y, p3x, p3y, vx, vy) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void cutEarTip(final int earTipIndex) {
        final short[] indices = this.indices;
        final ShortArray triangles = this.triangles;
        triangles.add(indices[this.previousIndex(earTipIndex)]);
        triangles.add(indices[earTipIndex]);
        triangles.add(indices[this.nextIndex(earTipIndex)]);
        this.indicesArray.removeIndex(earTipIndex);
        this.vertexTypes.removeIndex(earTipIndex);
        --this.vertexCount;
    }
    
    private int previousIndex(final int index) {
        return ((index == 0) ? this.vertexCount : index) - 1;
    }
    
    private int nextIndex(final int index) {
        return (index + 1) % this.vertexCount;
    }
    
    private static boolean areVerticesClockwise(final float[] vertices, final int offset, final int count) {
        if (count <= 2) {
            return false;
        }
        float area = 0.0f;
        for (int i = offset, n = offset + count - 3; i < n; i += 2) {
            final float p1x = vertices[i];
            final float p1y = vertices[i + 1];
            final float p2x = vertices[i + 2];
            final float p2y = vertices[i + 3];
            area += p1x * p2y - p2x * p1y;
        }
        final float p1x = vertices[offset + count - 2];
        final float p1y = vertices[offset + count - 1];
        final float p2x = vertices[offset];
        final float p2y = vertices[offset + 1];
        return area + p1x * p2y - p2x * p1y < 0.0f;
    }
    
    private static int computeSpannedAreaSign(final float p1x, final float p1y, final float p2x, final float p2y, final float p3x, final float p3y) {
        float area = p1x * (p3y - p2y);
        area += p2x * (p1y - p3y);
        area += p3x * (p2y - p1y);
        return (int)Math.signum(area);
    }
}
