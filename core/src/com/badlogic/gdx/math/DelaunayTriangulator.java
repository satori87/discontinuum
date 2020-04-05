// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.IntArray;

public class DelaunayTriangulator
{
    private static final float EPSILON = 1.0E-6f;
    private static final int INSIDE = 0;
    private static final int COMPLETE = 1;
    private static final int INCOMPLETE = 2;
    private final IntArray quicksortStack;
    private float[] sortedPoints;
    private final ShortArray triangles;
    private final ShortArray originalIndices;
    private final IntArray edges;
    private final BooleanArray complete;
    private final float[] superTriangle;
    private final Vector2 centroid;
    
    public DelaunayTriangulator() {
        this.quicksortStack = new IntArray();
        this.triangles = new ShortArray(false, 16);
        this.originalIndices = new ShortArray(false, 0);
        this.edges = new IntArray();
        this.complete = new BooleanArray(false, 16);
        this.superTriangle = new float[6];
        this.centroid = new Vector2();
    }
    
    public ShortArray computeTriangles(final FloatArray points, final boolean sorted) {
        return this.computeTriangles(points.items, 0, points.size, sorted);
    }
    
    public ShortArray computeTriangles(final float[] polygon, final boolean sorted) {
        return this.computeTriangles(polygon, 0, polygon.length, sorted);
    }
    
    public ShortArray computeTriangles(float[] points, int offset, final int count, final boolean sorted) {
        final ShortArray triangles = this.triangles;
        triangles.clear();
        if (count < 6) {
            return triangles;
        }
        triangles.ensureCapacity(count);
        if (!sorted) {
            if (this.sortedPoints == null || this.sortedPoints.length < count) {
                this.sortedPoints = new float[count];
            }
            System.arraycopy(points, offset, this.sortedPoints, 0, count);
            points = this.sortedPoints;
            offset = 0;
            this.sort(points, count);
        }
        final int end = offset + count;
        float xmin = points[0];
        float ymin = points[1];
        float xmax = xmin;
        float ymax = ymin;
        for (int i = offset + 2; i < end; ++i) {
            float value = points[i];
            if (value < xmin) {
                xmin = value;
            }
            if (value > xmax) {
                xmax = value;
            }
            ++i;
            value = points[i];
            if (value < ymin) {
                ymin = value;
            }
            if (value > ymax) {
                ymax = value;
            }
        }
        final float dx = xmax - xmin;
        final float dy = ymax - ymin;
        final float dmax = ((dx > dy) ? dx : dy) * 20.0f;
        final float xmid = (xmax + xmin) / 2.0f;
        final float ymid = (ymax + ymin) / 2.0f;
        final float[] superTriangle = this.superTriangle;
        superTriangle[0] = xmid - dmax;
        superTriangle[1] = ymid - dmax;
        superTriangle[2] = xmid;
        superTriangle[3] = ymid + dmax;
        superTriangle[4] = xmid + dmax;
        superTriangle[5] = ymid - dmax;
        final IntArray edges = this.edges;
        edges.ensureCapacity(count / 2);
        final BooleanArray complete = this.complete;
        complete.clear();
        complete.ensureCapacity(count);
        triangles.add(end);
        triangles.add(end + 2);
        triangles.add(end + 4);
        complete.add(false);
        for (int pointIndex = offset; pointIndex < end; pointIndex += 2) {
            final float x = points[pointIndex];
            final float y = points[pointIndex + 1];
            final short[] trianglesArray = triangles.items;
            final boolean[] completeArray = complete.items;
            for (int triangleIndex = triangles.size - 1; triangleIndex >= 0; triangleIndex -= 3) {
                final int completeIndex = triangleIndex / 3;
                if (!completeArray[completeIndex]) {
                    final int p1 = trianglesArray[triangleIndex - 2];
                    final int p2 = trianglesArray[triangleIndex - 1];
                    final int p3 = trianglesArray[triangleIndex];
                    float x2;
                    float y2;
                    if (p1 >= end) {
                        final int j = p1 - end;
                        x2 = superTriangle[j];
                        y2 = superTriangle[j + 1];
                    }
                    else {
                        x2 = points[p1];
                        y2 = points[p1 + 1];
                    }
                    float x3;
                    float y3;
                    if (p2 >= end) {
                        final int j = p2 - end;
                        x3 = superTriangle[j];
                        y3 = superTriangle[j + 1];
                    }
                    else {
                        x3 = points[p2];
                        y3 = points[p2 + 1];
                    }
                    float x4;
                    float y4;
                    if (p3 >= end) {
                        final int j = p3 - end;
                        x4 = superTriangle[j];
                        y4 = superTriangle[j + 1];
                    }
                    else {
                        x4 = points[p3];
                        y4 = points[p3 + 1];
                    }
                    switch (this.circumCircle(x, y, x2, y2, x3, y3, x4, y4)) {
                        case 1: {
                            completeArray[completeIndex] = true;
                            break;
                        }
                        case 0: {
                            edges.add(p1);
                            edges.add(p2);
                            edges.add(p2);
                            edges.add(p3);
                            edges.add(p3);
                            edges.add(p1);
                            triangles.removeIndex(triangleIndex);
                            triangles.removeIndex(triangleIndex - 1);
                            triangles.removeIndex(triangleIndex - 2);
                            complete.removeIndex(completeIndex);
                            break;
                        }
                    }
                }
            }
            final int[] edgesArray = edges.items;
            for (int k = 0, n = edges.size; k < n; k += 2) {
                final int p4 = edgesArray[k];
                if (p4 != -1) {
                    final int p5 = edgesArray[k + 1];
                    boolean skip = false;
                    for (int ii = k + 2; ii < n; ii += 2) {
                        if (p4 == edgesArray[ii + 1] && p5 == edgesArray[ii]) {
                            skip = true;
                            edgesArray[ii] = -1;
                        }
                    }
                    if (!skip) {
                        triangles.add(p4);
                        triangles.add(edgesArray[k + 1]);
                        triangles.add(pointIndex);
                        complete.add(false);
                    }
                }
            }
            edges.clear();
        }
        final short[] trianglesArray2 = triangles.items;
        for (int l = triangles.size - 1; l >= 0; l -= 3) {
            if (trianglesArray2[l] >= end || trianglesArray2[l - 1] >= end || trianglesArray2[l - 2] >= end) {
                triangles.removeIndex(l);
                triangles.removeIndex(l - 1);
                triangles.removeIndex(l - 2);
            }
        }
        if (!sorted) {
            final short[] originalIndicesArray = this.originalIndices.items;
            for (int m = 0, n2 = triangles.size; m < n2; ++m) {
                trianglesArray2[m] = (short)(originalIndicesArray[trianglesArray2[m] / 2] * 2);
            }
        }
        if (offset == 0) {
            for (int l = 0, n3 = triangles.size; l < n3; ++l) {
                trianglesArray2[l] /= 2;
            }
        }
        else {
            for (int l = 0, n3 = triangles.size; l < n3; ++l) {
                trianglesArray2[l] = (short)((trianglesArray2[l] - offset) / 2);
            }
        }
        return triangles;
    }
    
    private int circumCircle(final float xp, final float yp, final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        final float y1y2 = Math.abs(y1 - y2);
        final float y2y3 = Math.abs(y2 - y3);
        float xc;
        float yc;
        if (y1y2 < 1.0E-6f) {
            if (y2y3 < 1.0E-6f) {
                return 2;
            }
            final float m2 = -(x3 - x2) / (y3 - y2);
            final float mx2 = (x2 + x3) / 2.0f;
            final float my2 = (y2 + y3) / 2.0f;
            xc = (x2 + x1) / 2.0f;
            yc = m2 * (xc - mx2) + my2;
        }
        else {
            final float m3 = -(x2 - x1) / (y2 - y1);
            final float mx3 = (x1 + x2) / 2.0f;
            final float my3 = (y1 + y2) / 2.0f;
            if (y2y3 < 1.0E-6f) {
                xc = (x3 + x2) / 2.0f;
                yc = m3 * (xc - mx3) + my3;
            }
            else {
                final float m4 = -(x3 - x2) / (y3 - y2);
                final float mx4 = (x2 + x3) / 2.0f;
                final float my4 = (y2 + y3) / 2.0f;
                xc = (m3 * mx3 - m4 * mx4 + my4 - my3) / (m3 - m4);
                yc = m3 * (xc - mx3) + my3;
            }
        }
        float dx = x2 - xc;
        float dy = y2 - yc;
        final float rsqr = dx * dx + dy * dy;
        dx = xp - xc;
        dx *= dx;
        dy = yp - yc;
        if (dx + dy * dy - rsqr <= 1.0E-6f) {
            return 0;
        }
        return (xp > xc && dx > rsqr) ? 1 : 2;
    }
    
    private void sort(final float[] values, final int count) {
        final int pointCount = count / 2;
        this.originalIndices.clear();
        this.originalIndices.ensureCapacity(pointCount);
        final short[] originalIndicesArray = this.originalIndices.items;
        for (short i = 0; i < pointCount; ++i) {
            originalIndicesArray[i] = i;
        }
        int lower = 0;
        int upper = count - 1;
        final IntArray stack = this.quicksortStack;
        stack.add(lower);
        stack.add(upper - 1);
        while (stack.size > 0) {
            upper = stack.pop();
            lower = stack.pop();
            if (upper <= lower) {
                continue;
            }
            final int j = this.quicksortPartition(values, lower, upper, originalIndicesArray);
            if (j - lower > upper - j) {
                stack.add(lower);
                stack.add(j - 2);
            }
            stack.add(j + 2);
            stack.add(upper);
            if (upper - j < j - lower) {
                continue;
            }
            stack.add(lower);
            stack.add(j - 2);
        }
    }
    
    private int quicksortPartition(final float[] values, final int lower, final int upper, final short[] originalIndices) {
        final float value = values[lower];
        int up = upper;
        int down = lower + 2;
        while (down < up) {
            while (down < up) {
                if (values[down] > value) {
                    break;
                }
                down += 2;
            }
            while (values[up] > value) {
                up -= 2;
            }
            if (down < up) {
                float tempValue = values[down];
                values[down] = values[up];
                values[up] = tempValue;
                tempValue = values[down + 1];
                values[down + 1] = values[up + 1];
                values[up + 1] = tempValue;
                final short tempIndex = originalIndices[down / 2];
                originalIndices[down / 2] = originalIndices[up / 2];
                originalIndices[up / 2] = tempIndex;
            }
        }
        values[lower] = values[up];
        values[up] = value;
        float tempValue = values[lower + 1];
        values[lower + 1] = values[up + 1];
        values[up + 1] = tempValue;
        final short tempIndex = originalIndices[lower / 2];
        originalIndices[lower / 2] = originalIndices[up / 2];
        originalIndices[up / 2] = tempIndex;
        return up;
    }
    
    public void trim(final ShortArray triangles, final float[] points, final float[] hull, final int offset, final int count) {
        final short[] trianglesArray = triangles.items;
        for (int i = triangles.size - 1; i >= 0; i -= 3) {
            final int p1 = trianglesArray[i - 2] * 2;
            final int p2 = trianglesArray[i - 1] * 2;
            final int p3 = trianglesArray[i] * 2;
            GeometryUtils.triangleCentroid(points[p1], points[p1 + 1], points[p2], points[p2 + 1], points[p3], points[p3 + 1], this.centroid);
            if (!Intersector.isPointInPolygon(hull, offset, count, this.centroid.x, this.centroid.y)) {
                triangles.removeIndex(i);
                triangles.removeIndex(i - 1);
                triangles.removeIndex(i - 2);
            }
        }
    }
}
