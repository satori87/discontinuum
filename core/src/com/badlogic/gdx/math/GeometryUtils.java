// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

public final class GeometryUtils
{
    private static final Vector2 tmp1;
    private static final Vector2 tmp2;
    private static final Vector2 tmp3;
    
    static {
        tmp1 = new Vector2();
        tmp2 = new Vector2();
        tmp3 = new Vector2();
    }
    
    public static Vector2 toBarycoord(final Vector2 p, final Vector2 a, final Vector2 b, final Vector2 c, final Vector2 barycentricOut) {
        final Vector2 v0 = GeometryUtils.tmp1.set(b).sub(a);
        final Vector2 v2 = GeometryUtils.tmp2.set(c).sub(a);
        final Vector2 v3 = GeometryUtils.tmp3.set(p).sub(a);
        final float d00 = v0.dot(v0);
        final float d2 = v0.dot(v2);
        final float d3 = v2.dot(v2);
        final float d4 = v3.dot(v0);
        final float d5 = v3.dot(v2);
        final float denom = d00 * d3 - d2 * d2;
        barycentricOut.x = (d3 * d4 - d2 * d5) / denom;
        barycentricOut.y = (d00 * d5 - d2 * d4) / denom;
        return barycentricOut;
    }
    
    public static boolean barycoordInsideTriangle(final Vector2 barycentric) {
        return barycentric.x >= 0.0f && barycentric.y >= 0.0f && barycentric.x + barycentric.y <= 1.0f;
    }
    
    public static Vector2 fromBarycoord(final Vector2 barycentric, final Vector2 a, final Vector2 b, final Vector2 c, final Vector2 interpolatedOut) {
        final float u = 1.0f - barycentric.x - barycentric.y;
        interpolatedOut.x = u * a.x + barycentric.x * b.x + barycentric.y * c.x;
        interpolatedOut.y = u * a.y + barycentric.x * b.y + barycentric.y * c.y;
        return interpolatedOut;
    }
    
    public static float fromBarycoord(final Vector2 barycentric, final float a, final float b, final float c) {
        final float u = 1.0f - barycentric.x - barycentric.y;
        return u * a + barycentric.x * b + barycentric.y * c;
    }
    
    public static float lowestPositiveRoot(final float a, final float b, final float c) {
        final float det = b * b - 4.0f * a * c;
        if (det < 0.0f) {
            return Float.NaN;
        }
        final float sqrtD = (float)Math.sqrt(det);
        final float invA = 1.0f / (2.0f * a);
        float r1 = (-b - sqrtD) * invA;
        float r2 = (-b + sqrtD) * invA;
        if (r1 > r2) {
            final float tmp = r2;
            r2 = r1;
            r1 = tmp;
        }
        if (r1 > 0.0f) {
            return r1;
        }
        if (r2 > 0.0f) {
            return r2;
        }
        return Float.NaN;
    }
    
    public static boolean colinear(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        final float dx21 = x2 - x1;
        final float dy21 = y2 - y1;
        final float dx22 = x3 - x2;
        final float dy22 = y3 - y2;
        final float det = dx22 * dy21 - dx21 * dy22;
        return Math.abs(det) < 1.0E-6f;
    }
    
    public static Vector2 triangleCentroid(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final Vector2 centroid) {
        centroid.x = (x1 + x2 + x3) / 3.0f;
        centroid.y = (y1 + y2 + y3) / 3.0f;
        return centroid;
    }
    
    public static Vector2 triangleCircumcenter(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final Vector2 circumcenter) {
        final float dx21 = x2 - x1;
        final float dy21 = y2 - y1;
        final float dx22 = x3 - x2;
        final float dy22 = y3 - y2;
        final float dx23 = x1 - x3;
        final float dy23 = y1 - y3;
        float det = dx22 * dy21 - dx21 * dy22;
        if (Math.abs(det) < 1.0E-6f) {
            throw new IllegalArgumentException("Triangle points must not be colinear.");
        }
        det *= 2.0f;
        final float sqr1 = x1 * x1 + y1 * y1;
        final float sqr2 = x2 * x2 + y2 * y2;
        final float sqr3 = x3 * x3 + y3 * y3;
        circumcenter.set((sqr1 * dy22 + sqr2 * dy23 + sqr3 * dy21) / det, -(sqr1 * dx22 + sqr2 * dx23 + sqr3 * dx21) / det);
        return circumcenter;
    }
    
    public static float triangleCircumradius(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        float x4;
        float y4;
        if (Math.abs(y2 - y1) < 1.0E-6f) {
            final float m2 = -(x3 - x2) / (y3 - y2);
            final float mx2 = (x2 + x3) / 2.0f;
            final float my2 = (y2 + y3) / 2.0f;
            x4 = (x2 + x1) / 2.0f;
            y4 = m2 * (x4 - mx2) + my2;
        }
        else if (Math.abs(y3 - y2) < 1.0E-6f) {
            final float m3 = -(x2 - x1) / (y2 - y1);
            final float mx3 = (x1 + x2) / 2.0f;
            final float my3 = (y1 + y2) / 2.0f;
            x4 = (x3 + x2) / 2.0f;
            y4 = m3 * (x4 - mx3) + my3;
        }
        else {
            final float m3 = -(x2 - x1) / (y2 - y1);
            final float m2 = -(x3 - x2) / (y3 - y2);
            final float mx3 = (x1 + x2) / 2.0f;
            final float mx2 = (x2 + x3) / 2.0f;
            final float my3 = (y1 + y2) / 2.0f;
            final float my2 = (y2 + y3) / 2.0f;
            x4 = (m3 * mx3 - m2 * mx2 + my2 - my3) / (m3 - m2);
            y4 = m3 * (x4 - mx3) + my3;
        }
        final float dx = x1 - x4;
        final float dy = y1 - y4;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
    
    public static float triangleQuality(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        final float length1 = (float)Math.sqrt(x1 * x1 + y1 * y1);
        final float length2 = (float)Math.sqrt(x2 * x2 + y2 * y2);
        final float length3 = (float)Math.sqrt(x3 * x3 + y3 * y3);
        return Math.min(length1, Math.min(length2, length3)) / triangleCircumradius(x1, y1, x2, y2, x3, y3);
    }
    
    public static float triangleArea(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        return Math.abs((x1 - x3) * (y2 - y1) - (x1 - x2) * (y3 - y1)) * 0.5f;
    }
    
    public static Vector2 quadrilateralCentroid(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4, final Vector2 centroid) {
        final float avgX1 = (x1 + x2 + x3) / 3.0f;
        final float avgY1 = (y1 + y2 + y3) / 3.0f;
        final float avgX2 = (x1 + x4 + x3) / 3.0f;
        final float avgY2 = (y1 + y4 + y3) / 3.0f;
        centroid.x = avgX1 - (avgX1 - avgX2) / 2.0f;
        centroid.y = avgY1 - (avgY1 - avgY2) / 2.0f;
        return centroid;
    }
    
    public static Vector2 polygonCentroid(final float[] polygon, final int offset, final int count, final Vector2 centroid) {
        if (count < 6) {
            throw new IllegalArgumentException("A polygon must have 3 or more coordinate pairs.");
        }
        float x = 0.0f;
        float y = 0.0f;
        float signedArea = 0.0f;
        int i = offset;
        for (int n = offset + count - 2; i < n; i += 2) {
            final float x2 = polygon[i];
            final float y2 = polygon[i + 1];
            final float x3 = polygon[i + 2];
            final float y3 = polygon[i + 3];
            final float a = x2 * y3 - x3 * y2;
            signedArea += a;
            x += (x2 + x3) * a;
            y += (y2 + y3) * a;
        }
        final float x4 = polygon[i];
        final float y4 = polygon[i + 1];
        final float x5 = polygon[offset];
        final float y5 = polygon[offset + 1];
        final float a2 = x4 * y5 - x5 * y4;
        signedArea += a2;
        x += (x4 + x5) * a2;
        y += (y4 + y5) * a2;
        if (signedArea == 0.0f) {
            centroid.x = 0.0f;
            centroid.y = 0.0f;
        }
        else {
            signedArea *= 0.5f;
            centroid.x = x / (6.0f * signedArea);
            centroid.y = y / (6.0f * signedArea);
        }
        return centroid;
    }
    
    public static float polygonArea(final float[] polygon, final int offset, final int count) {
        float area = 0.0f;
        for (int i = offset, n = offset + count; i < n; i += 2) {
            final int x1 = i;
            final int y1 = i + 1;
            int x2 = (i + 2) % n;
            if (x2 < offset) {
                x2 += offset;
            }
            int y2 = (i + 3) % n;
            if (y2 < offset) {
                y2 += offset;
            }
            area += polygon[x1] * polygon[y2];
            area -= polygon[x2] * polygon[y1];
        }
        area *= 0.5f;
        return area;
    }
    
    public static void ensureCCW(final float[] polygon) {
        ensureCCW(polygon, 0, polygon.length);
    }
    
    public static void ensureCCW(final float[] polygon, final int offset, final int count) {
        if (!isClockwise(polygon, offset, count)) {
            return;
        }
        final int lastX = offset + count - 2;
        for (int i = offset, n = offset + count / 2; i < n; i += 2) {
            final int other = lastX - i;
            final float x = polygon[i];
            final float y = polygon[i + 1];
            polygon[i] = polygon[other];
            polygon[i + 1] = polygon[other + 1];
            polygon[other] = x;
            polygon[other + 1] = y;
        }
    }
    
    public static boolean isClockwise(final float[] polygon, final int offset, final int count) {
        if (count <= 2) {
            return false;
        }
        float area = 0.0f;
        for (int i = offset, n = offset + count - 3; i < n; i += 2) {
            final float p1x = polygon[i];
            final float p1y = polygon[i + 1];
            final float p2x = polygon[i + 2];
            final float p2y = polygon[i + 3];
            area += p1x * p2y - p2x * p1y;
        }
        final float p1x = polygon[offset + count - 2];
        final float p1y = polygon[offset + count - 1];
        final float p2x = polygon[offset];
        final float p2y = polygon[offset + 1];
        return area + p1x * p2y - p2x * p1y < 0.0f;
    }
}
