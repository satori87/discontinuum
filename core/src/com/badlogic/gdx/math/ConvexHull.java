// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;

public class ConvexHull
{
    private final IntArray quicksortStack;
    private float[] sortedPoints;
    private final FloatArray hull;
    private final IntArray indices;
    private final ShortArray originalIndices;
    
    public ConvexHull() {
        this.quicksortStack = new IntArray();
        this.hull = new FloatArray();
        this.indices = new IntArray();
        this.originalIndices = new ShortArray(false, 0);
    }
    
    public FloatArray computePolygon(final FloatArray points, final boolean sorted) {
        return this.computePolygon(points.items, 0, points.size, sorted);
    }
    
    public FloatArray computePolygon(final float[] polygon, final boolean sorted) {
        return this.computePolygon(polygon, 0, polygon.length, sorted);
    }
    
    public FloatArray computePolygon(float[] points, int offset, final int count, final boolean sorted) {
        final int end = offset + count;
        if (!sorted) {
            if (this.sortedPoints == null || this.sortedPoints.length < count) {
                this.sortedPoints = new float[count];
            }
            System.arraycopy(points, offset, this.sortedPoints, 0, count);
            points = this.sortedPoints;
            offset = 0;
            this.sort(points, count);
        }
        final FloatArray hull = this.hull;
        hull.clear();
        for (int i = offset; i < end; i += 2) {
            final float x = points[i];
            final float y = points[i + 1];
            while (hull.size >= 4 && this.ccw(x, y) <= 0.0f) {
                final FloatArray floatArray = hull;
                floatArray.size -= 2;
            }
            hull.add(x);
            hull.add(y);
        }
        int i = end - 4;
        final int t = hull.size + 2;
        while (i >= offset) {
            final float x2 = points[i];
            final float y2 = points[i + 1];
            while (hull.size >= t && this.ccw(x2, y2) <= 0.0f) {
                final FloatArray floatArray2 = hull;
                floatArray2.size -= 2;
            }
            hull.add(x2);
            hull.add(y2);
            i -= 2;
        }
        return hull;
    }
    
    public IntArray computeIndices(final FloatArray points, final boolean sorted, final boolean yDown) {
        return this.computeIndices(points.items, 0, points.size, sorted, yDown);
    }
    
    public IntArray computeIndices(final float[] polygon, final boolean sorted, final boolean yDown) {
        return this.computeIndices(polygon, 0, polygon.length, sorted, yDown);
    }
    
    public IntArray computeIndices(float[] points, int offset, final int count, final boolean sorted, final boolean yDown) {
        final int end = offset + count;
        if (!sorted) {
            if (this.sortedPoints == null || this.sortedPoints.length < count) {
                this.sortedPoints = new float[count];
            }
            System.arraycopy(points, offset, this.sortedPoints, 0, count);
            points = this.sortedPoints;
            offset = 0;
            this.sortWithIndices(points, count, yDown);
        }
        final IntArray indices = this.indices;
        indices.clear();
        final FloatArray hull = this.hull;
        hull.clear();
        for (int i = offset, index = i / 2; i < end; i += 2, ++index) {
            final float x = points[i];
            final float y = points[i + 1];
            while (hull.size >= 4 && this.ccw(x, y) <= 0.0f) {
                final FloatArray floatArray = hull;
                floatArray.size -= 2;
                final IntArray intArray = indices;
                --intArray.size;
            }
            hull.add(x);
            hull.add(y);
            indices.add(index);
        }
        int i = end - 4;
        int index = i / 2;
        final int t = hull.size + 2;
        while (i >= offset) {
            final float x2 = points[i];
            final float y2 = points[i + 1];
            while (hull.size >= t && this.ccw(x2, y2) <= 0.0f) {
                final FloatArray floatArray2 = hull;
                floatArray2.size -= 2;
                final IntArray intArray2 = indices;
                --intArray2.size;
            }
            hull.add(x2);
            hull.add(y2);
            indices.add(index);
            i -= 2;
            --index;
        }
        if (!sorted) {
            final short[] originalIndicesArray = this.originalIndices.items;
            final int[] indicesArray = indices.items;
            for (int j = 0, n = indices.size; j < n; ++j) {
                indicesArray[j] = originalIndicesArray[indicesArray[j]];
            }
        }
        return indices;
    }
    
    private float ccw(final float p3x, final float p3y) {
        final FloatArray hull = this.hull;
        final int size = hull.size;
        final float p1x = hull.get(size - 4);
        final float p1y = hull.get(size - 3);
        final float p2x = hull.get(size - 2);
        final float p2y = hull.peek();
        return (p2x - p1x) * (p3y - p1y) - (p2y - p1y) * (p3x - p1x);
    }
    
    private void sort(final float[] values, final int count) {
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
            final int i = this.quicksortPartition(values, lower, upper);
            if (i - lower > upper - i) {
                stack.add(lower);
                stack.add(i - 2);
            }
            stack.add(i + 2);
            stack.add(upper);
            if (upper - i < i - lower) {
                continue;
            }
            stack.add(lower);
            stack.add(i - 2);
        }
    }
    
    private int quicksortPartition(final float[] values, final int lower, final int upper) {
        final float x = values[lower];
        final float y = values[lower + 1];
        int up = upper;
        int down = lower;
        while (down < up) {
            while (down < up) {
                if (values[down] > x) {
                    break;
                }
                down += 2;
            }
            while (values[up] > x || (values[up] == x && values[up + 1] < y)) {
                up -= 2;
            }
            if (down < up) {
                float temp = values[down];
                values[down] = values[up];
                values[up] = temp;
                temp = values[down + 1];
                values[down + 1] = values[up + 1];
                values[up + 1] = temp;
            }
        }
        values[lower] = values[up];
        values[up] = x;
        values[lower + 1] = values[up + 1];
        values[up + 1] = y;
        return up;
    }
    
    private void sortWithIndices(final float[] values, final int count, final boolean yDown) {
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
            final int j = this.quicksortPartitionWithIndices(values, lower, upper, yDown, originalIndicesArray);
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
    
    private int quicksortPartitionWithIndices(final float[] values, final int lower, final int upper, final boolean yDown, final short[] originalIndices) {
        final float x = values[lower];
        final float y = values[lower + 1];
        int up = upper;
        int down = lower;
        while (down < up) {
            while (down < up && values[down] <= x) {
                down += 2;
            }
            if (yDown) {
                while (true) {
                    if (values[up] <= x) {
                        if (values[up] != x) {
                            break;
                        }
                        if (values[up + 1] >= y) {
                            break;
                        }
                    }
                    up -= 2;
                }
            }
            else {
                while (values[up] > x || (values[up] == x && values[up + 1] > y)) {
                    up -= 2;
                }
            }
            if (down < up) {
                float temp = values[down];
                values[down] = values[up];
                values[up] = temp;
                temp = values[down + 1];
                values[down + 1] = values[up + 1];
                values[up + 1] = temp;
                final short tempIndex = originalIndices[down / 2];
                originalIndices[down / 2] = originalIndices[up / 2];
                originalIndices[up / 2] = tempIndex;
            }
        }
        values[lower] = values[up];
        values[up] = x;
        values[lower + 1] = values[up + 1];
        values[up + 1] = y;
        final short tempIndex = originalIndices[lower / 2];
        originalIndices[lower / 2] = originalIndices[up / 2];
        originalIndices[up / 2] = tempIndex;
        return up;
    }
}
