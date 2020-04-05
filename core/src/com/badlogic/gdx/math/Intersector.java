// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public final class Intersector
{
    private static final Vector3 v0;
    private static final Vector3 v1;
    private static final Vector3 v2;
    private static final FloatArray floatArray;
    private static final FloatArray floatArray2;
    private static final Vector2 ip;
    private static final Vector2 ep1;
    private static final Vector2 ep2;
    private static final Vector2 s;
    private static final Vector2 e;
    private static final Plane p;
    private static final Vector3 i;
    private static final Vector3 dir;
    private static final Vector3 start;
    static Vector3 best;
    static Vector3 tmp;
    static Vector3 tmp1;
    static Vector3 tmp2;
    static Vector3 tmp3;
    static Vector2 v2tmp;
    static Vector3 intersection;
    
    static {
        v0 = new Vector3();
        v1 = new Vector3();
        v2 = new Vector3();
        floatArray = new FloatArray();
        floatArray2 = new FloatArray();
        ip = new Vector2();
        ep1 = new Vector2();
        ep2 = new Vector2();
        s = new Vector2();
        e = new Vector2();
        p = new Plane(new Vector3(), 0.0f);
        i = new Vector3();
        dir = new Vector3();
        start = new Vector3();
        Intersector.best = new Vector3();
        Intersector.tmp = new Vector3();
        Intersector.tmp1 = new Vector3();
        Intersector.tmp2 = new Vector3();
        Intersector.tmp3 = new Vector3();
        Intersector.v2tmp = new Vector2();
        Intersector.intersection = new Vector3();
    }
    
    public static boolean isPointInTriangle(final Vector3 point, final Vector3 t1, final Vector3 t2, final Vector3 t3) {
        Intersector.v0.set(t1).sub(point);
        Intersector.v1.set(t2).sub(point);
        Intersector.v2.set(t3).sub(point);
        final float ab = Intersector.v0.dot(Intersector.v1);
        final float ac = Intersector.v0.dot(Intersector.v2);
        final float bc = Intersector.v1.dot(Intersector.v2);
        final float cc = Intersector.v2.dot(Intersector.v2);
        if (bc * ac - cc * ab < 0.0f) {
            return false;
        }
        final float bb = Intersector.v1.dot(Intersector.v1);
        return ab * bc - ac * bb >= 0.0f;
    }
    
    public static boolean isPointInTriangle(final Vector2 p, final Vector2 a, final Vector2 b, final Vector2 c) {
        final float px1 = p.x - a.x;
        final float py1 = p.y - a.y;
        final boolean side12 = (b.x - a.x) * py1 - (b.y - a.y) * px1 > 0.0f;
        return (c.x - a.x) * py1 - (c.y - a.y) * px1 > 0.0f != side12 && (c.x - b.x) * (p.y - b.y) - (c.y - b.y) * (p.x - b.x) > 0.0f == side12;
    }
    
    public static boolean isPointInTriangle(final float px, final float py, final float ax, final float ay, final float bx, final float by, final float cx, final float cy) {
        final float px2 = px - ax;
        final float py2 = py - ay;
        final boolean side12 = (bx - ax) * py2 - (by - ay) * px2 > 0.0f;
        return (cx - ax) * py2 - (cy - ay) * px2 > 0.0f != side12 && (cx - bx) * (py - by) - (cy - by) * (px - bx) > 0.0f == side12;
    }
    
    public static boolean intersectSegmentPlane(final Vector3 start, final Vector3 end, final Plane plane, final Vector3 intersection) {
        final Vector3 dir = Intersector.v0.set(end).sub(start);
        final float denom = dir.dot(plane.getNormal());
        if (denom == 0.0f) {
            return false;
        }
        final float t = -(start.dot(plane.getNormal()) + plane.getD()) / denom;
        if (t < 0.0f || t > 1.0f) {
            return false;
        }
        intersection.set(start).add(dir.scl(t));
        return true;
    }
    
    public static int pointLineSide(final Vector2 linePoint1, final Vector2 linePoint2, final Vector2 point) {
        return (int)Math.signum((linePoint2.x - linePoint1.x) * (point.y - linePoint1.y) - (linePoint2.y - linePoint1.y) * (point.x - linePoint1.x));
    }
    
    public static int pointLineSide(final float linePoint1X, final float linePoint1Y, final float linePoint2X, final float linePoint2Y, final float pointX, final float pointY) {
        return (int)Math.signum((linePoint2X - linePoint1X) * (pointY - linePoint1Y) - (linePoint2Y - linePoint1Y) * (pointX - linePoint1X));
    }
    
    public static boolean isPointInPolygon(final Array<Vector2> polygon, final Vector2 point) {
        Vector2 lastVertice = polygon.peek();
        boolean oddNodes = false;
        for (int i = 0; i < polygon.size; ++i) {
            final Vector2 vertice = polygon.get(i);
            if (((vertice.y < point.y && lastVertice.y >= point.y) || (lastVertice.y < point.y && vertice.y >= point.y)) && vertice.x + (point.y - vertice.y) / (lastVertice.y - vertice.y) * (lastVertice.x - vertice.x) < point.x) {
                oddNodes = !oddNodes;
            }
            lastVertice = vertice;
        }
        return oddNodes;
    }
    
    public static boolean isPointInPolygon(final float[] polygon, final int offset, final int count, final float x, final float y) {
        boolean oddNodes = false;
        int j = offset + count - 2;
        for (int i = offset, n = j; i <= n; i += 2) {
            final float yi = polygon[i + 1];
            final float yj = polygon[j + 1];
            if ((yi < y && yj >= y) || (yj < y && yi >= y)) {
                final float xi = polygon[i];
                if (xi + (y - yi) / (yj - yi) * (polygon[j] - xi) < x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }
    
    public static boolean intersectPolygons(final Polygon p1, final Polygon p2, final Polygon overlap) {
        if (p1.getVertices().length == 0 || p2.getVertices().length == 0) {
            return false;
        }
        Intersector.floatArray2.clear();
        Intersector.floatArray.clear();
        Intersector.floatArray2.addAll(p1.getTransformedVertices());
        for (int i = 0; i < p2.getTransformedVertices().length; i += 2) {
            Intersector.ep1.set(p2.getTransformedVertices()[i], p2.getTransformedVertices()[i + 1]);
            if (i < p2.getTransformedVertices().length - 2) {
                Intersector.ep2.set(p2.getTransformedVertices()[i + 2], p2.getTransformedVertices()[i + 3]);
            }
            else {
                Intersector.ep2.set(p2.getTransformedVertices()[0], p2.getTransformedVertices()[1]);
            }
            if (Intersector.floatArray2.size == 0) {
                return false;
            }
            Intersector.s.set(Intersector.floatArray2.get(Intersector.floatArray2.size - 2), Intersector.floatArray2.get(Intersector.floatArray2.size - 1));
            for (int j = 0; j < Intersector.floatArray2.size; j += 2) {
                Intersector.e.set(Intersector.floatArray2.get(j), Intersector.floatArray2.get(j + 1));
                if (pointLineSide(Intersector.ep2, Intersector.ep1, Intersector.e) > 0) {
                    if (pointLineSide(Intersector.ep2, Intersector.ep1, Intersector.s) <= 0) {
                        intersectLines(Intersector.s, Intersector.e, Intersector.ep1, Intersector.ep2, Intersector.ip);
                        if (Intersector.floatArray.size < 2 || Intersector.floatArray.get(Intersector.floatArray.size - 2) != Intersector.ip.x || Intersector.floatArray.get(Intersector.floatArray.size - 1) != Intersector.ip.y) {
                            Intersector.floatArray.add(Intersector.ip.x);
                            Intersector.floatArray.add(Intersector.ip.y);
                        }
                    }
                    Intersector.floatArray.add(Intersector.e.x);
                    Intersector.floatArray.add(Intersector.e.y);
                }
                else if (pointLineSide(Intersector.ep2, Intersector.ep1, Intersector.s) > 0) {
                    intersectLines(Intersector.s, Intersector.e, Intersector.ep1, Intersector.ep2, Intersector.ip);
                    Intersector.floatArray.add(Intersector.ip.x);
                    Intersector.floatArray.add(Intersector.ip.y);
                }
                Intersector.s.set(Intersector.e.x, Intersector.e.y);
            }
            Intersector.floatArray2.clear();
            Intersector.floatArray2.addAll(Intersector.floatArray);
            Intersector.floatArray.clear();
        }
        if (Intersector.floatArray2.size != 0) {
            if (overlap != null) {
                if (overlap.getVertices().length == Intersector.floatArray2.size) {
                    System.arraycopy(Intersector.floatArray2.items, 0, overlap.getVertices(), 0, Intersector.floatArray2.size);
                }
                else {
                    overlap.setVertices(Intersector.floatArray2.toArray());
                }
            }
            return true;
        }
        return false;
    }
    
    public static float distanceLinePoint(final float startX, final float startY, final float endX, final float endY, final float pointX, final float pointY) {
        final float normalLength = (float)Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY));
        return Math.abs((pointX - startX) * (endY - startY) - (pointY - startY) * (endX - startX)) / normalLength;
    }
    
    public static float distanceSegmentPoint(final float startX, final float startY, final float endX, final float endY, final float pointX, final float pointY) {
        return nearestSegmentPoint(startX, startY, endX, endY, pointX, pointY, Intersector.v2tmp).dst(pointX, pointY);
    }
    
    public static float distanceSegmentPoint(final Vector2 start, final Vector2 end, final Vector2 point) {
        return nearestSegmentPoint(start, end, point, Intersector.v2tmp).dst(point);
    }
    
    public static Vector2 nearestSegmentPoint(final Vector2 start, final Vector2 end, final Vector2 point, final Vector2 nearest) {
        final float length2 = start.dst2(end);
        if (length2 == 0.0f) {
            return nearest.set(start);
        }
        final float t = ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) / length2;
        if (t < 0.0f) {
            return nearest.set(start);
        }
        if (t > 1.0f) {
            return nearest.set(end);
        }
        return nearest.set(start.x + t * (end.x - start.x), start.y + t * (end.y - start.y));
    }
    
    public static Vector2 nearestSegmentPoint(final float startX, final float startY, final float endX, final float endY, final float pointX, final float pointY, final Vector2 nearest) {
        final float xDiff = endX - startX;
        final float yDiff = endY - startY;
        final float length2 = xDiff * xDiff + yDiff * yDiff;
        if (length2 == 0.0f) {
            return nearest.set(startX, startY);
        }
        final float t = ((pointX - startX) * (endX - startX) + (pointY - startY) * (endY - startY)) / length2;
        if (t < 0.0f) {
            return nearest.set(startX, startY);
        }
        if (t > 1.0f) {
            return nearest.set(endX, endY);
        }
        return nearest.set(startX + t * (endX - startX), startY + t * (endY - startY));
    }
    
    public static boolean intersectSegmentCircle(final Vector2 start, final Vector2 end, final Vector2 center, final float squareRadius) {
        Intersector.tmp.set(end.x - start.x, end.y - start.y, 0.0f);
        Intersector.tmp1.set(center.x - start.x, center.y - start.y, 0.0f);
        final float l = Intersector.tmp.len();
        final float u = Intersector.tmp1.dot(Intersector.tmp.nor());
        if (u <= 0.0f) {
            Intersector.tmp2.set(start.x, start.y, 0.0f);
        }
        else if (u >= l) {
            Intersector.tmp2.set(end.x, end.y, 0.0f);
        }
        else {
            Intersector.tmp3.set(Intersector.tmp.scl(u));
            Intersector.tmp2.set(Intersector.tmp3.x + start.x, Intersector.tmp3.y + start.y, 0.0f);
        }
        final float x = center.x - Intersector.tmp2.x;
        final float y = center.y - Intersector.tmp2.y;
        return x * x + y * y <= squareRadius;
    }
    
    public static float intersectSegmentCircleDisplace(final Vector2 start, final Vector2 end, final Vector2 point, final float radius, final Vector2 displacement) {
        float u = (point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y);
        float d = start.dst(end);
        u /= d * d;
        if (u < 0.0f || u > 1.0f) {
            return Float.POSITIVE_INFINITY;
        }
        Intersector.tmp.set(end.x, end.y, 0.0f).sub(start.x, start.y, 0.0f);
        Intersector.tmp2.set(start.x, start.y, 0.0f).add(Intersector.tmp.scl(u));
        d = Intersector.tmp2.dst(point.x, point.y, 0.0f);
        if (d < radius) {
            displacement.set(point).sub(Intersector.tmp2.x, Intersector.tmp2.y).nor();
            return d;
        }
        return Float.POSITIVE_INFINITY;
    }
    
    public static float intersectRayRay(final Vector2 start1, final Vector2 direction1, final Vector2 start2, final Vector2 direction2) {
        final float difx = start2.x - start1.x;
        final float dify = start2.y - start1.y;
        final float d1xd2 = direction1.x * direction2.y - direction1.y * direction2.x;
        if (d1xd2 == 0.0f) {
            return Float.POSITIVE_INFINITY;
        }
        final float d2sx = direction2.x / d1xd2;
        final float d2sy = direction2.y / d1xd2;
        return difx * d2sy - dify * d2sx;
    }
    
    public static boolean intersectRayPlane(final Ray ray, final Plane plane, final Vector3 intersection) {
        final float denom = ray.direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            final float t = -(ray.origin.dot(plane.getNormal()) + plane.getD()) / denom;
            if (t < 0.0f) {
                return false;
            }
            if (intersection != null) {
                intersection.set(ray.origin).add(Intersector.v0.set(ray.direction).scl(t));
            }
            return true;
        }
        else {
            if (plane.testPoint(ray.origin) == Plane.PlaneSide.OnPlane) {
                if (intersection != null) {
                    intersection.set(ray.origin);
                }
                return true;
            }
            return false;
        }
    }
    
    public static float intersectLinePlane(final float x, final float y, final float z, final float x2, final float y2, final float z2, final Plane plane, final Vector3 intersection) {
        final Vector3 direction = Intersector.tmp.set(x2, y2, z2).sub(x, y, z);
        final Vector3 origin = Intersector.tmp2.set(x, y, z);
        final float denom = direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            final float t = -(origin.dot(plane.getNormal()) + plane.getD()) / denom;
            if (intersection != null) {
                intersection.set(origin).add(direction.scl(t));
            }
            return t;
        }
        if (plane.testPoint(origin) == Plane.PlaneSide.OnPlane) {
            if (intersection != null) {
                intersection.set(origin);
            }
            return 0.0f;
        }
        return -1.0f;
    }
    
    public static boolean intersectRayTriangle(final Ray ray, final Vector3 t1, final Vector3 t2, final Vector3 t3, final Vector3 intersection) {
        final Vector3 edge1 = Intersector.v0.set(t2).sub(t1);
        final Vector3 edge2 = Intersector.v1.set(t3).sub(t1);
        final Vector3 pvec = Intersector.v2.set(ray.direction).crs(edge2);
        float det = edge1.dot(pvec);
        if (MathUtils.isZero(det)) {
            Intersector.p.set(t1, t2, t3);
            if (Intersector.p.testPoint(ray.origin) == Plane.PlaneSide.OnPlane && isPointInTriangle(ray.origin, t1, t2, t3)) {
                if (intersection != null) {
                    intersection.set(ray.origin);
                }
                return true;
            }
            return false;
        }
        else {
            det = 1.0f / det;
            final Vector3 tvec = Intersector.i.set(ray.origin).sub(t1);
            final float u = tvec.dot(pvec) * det;
            if (u < 0.0f || u > 1.0f) {
                return false;
            }
            final Vector3 qvec = tvec.crs(edge1);
            final float v = ray.direction.dot(qvec) * det;
            if (v < 0.0f || u + v > 1.0f) {
                return false;
            }
            final float t4 = edge2.dot(qvec) * det;
            if (t4 < 0.0f) {
                return false;
            }
            if (intersection != null) {
                if (t4 <= 1.0E-6f) {
                    intersection.set(ray.origin);
                }
                else {
                    ray.getEndPoint(intersection, t4);
                }
            }
            return true;
        }
    }
    
    public static boolean intersectRaySphere(final Ray ray, final Vector3 center, final float radius, final Vector3 intersection) {
        final float len = ray.direction.dot(center.x - ray.origin.x, center.y - ray.origin.y, center.z - ray.origin.z);
        if (len < 0.0f) {
            return false;
        }
        final float dst2 = center.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
        final float r2 = radius * radius;
        if (dst2 > r2) {
            return false;
        }
        if (intersection != null) {
            intersection.set(ray.direction).scl(len - (float)Math.sqrt(r2 - dst2)).add(ray.origin);
        }
        return true;
    }
    
    public static boolean intersectRayBounds(final Ray ray, final BoundingBox box, final Vector3 intersection) {
        if (box.contains(ray.origin)) {
            if (intersection != null) {
                intersection.set(ray.origin);
            }
            return true;
        }
        float lowest = 0.0f;
        boolean hit = false;
        if (ray.origin.x <= box.min.x && ray.direction.x > 0.0f) {
            final float t = (box.min.x - ray.origin.x) / ray.direction.x;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.y >= box.min.y && Intersector.v2.y <= box.max.y && Intersector.v2.z >= box.min.z && Intersector.v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.x >= box.max.x && ray.direction.x < 0.0f) {
            final float t = (box.max.x - ray.origin.x) / ray.direction.x;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.y >= box.min.y && Intersector.v2.y <= box.max.y && Intersector.v2.z >= box.min.z && Intersector.v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.y <= box.min.y && ray.direction.y > 0.0f) {
            final float t = (box.min.y - ray.origin.y) / ray.direction.y;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.x >= box.min.x && Intersector.v2.x <= box.max.x && Intersector.v2.z >= box.min.z && Intersector.v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.y >= box.max.y && ray.direction.y < 0.0f) {
            final float t = (box.max.y - ray.origin.y) / ray.direction.y;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.x >= box.min.x && Intersector.v2.x <= box.max.x && Intersector.v2.z >= box.min.z && Intersector.v2.z <= box.max.z && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.z <= box.min.z && ray.direction.z > 0.0f) {
            final float t = (box.min.z - ray.origin.z) / ray.direction.z;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.x >= box.min.x && Intersector.v2.x <= box.max.x && Intersector.v2.y >= box.min.y && Intersector.v2.y <= box.max.y && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.z >= box.max.z && ray.direction.z < 0.0f) {
            final float t = (box.max.z - ray.origin.z) / ray.direction.z;
            if (t >= 0.0f) {
                Intersector.v2.set(ray.direction).scl(t).add(ray.origin);
                if (Intersector.v2.x >= box.min.x && Intersector.v2.x <= box.max.x && Intersector.v2.y >= box.min.y && Intersector.v2.y <= box.max.y && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (hit && intersection != null) {
            intersection.set(ray.direction).scl(lowest).add(ray.origin);
            if (intersection.x < box.min.x) {
                intersection.x = box.min.x;
            }
            else if (intersection.x > box.max.x) {
                intersection.x = box.max.x;
            }
            if (intersection.y < box.min.y) {
                intersection.y = box.min.y;
            }
            else if (intersection.y > box.max.y) {
                intersection.y = box.max.y;
            }
            if (intersection.z < box.min.z) {
                intersection.z = box.min.z;
            }
            else if (intersection.z > box.max.z) {
                intersection.z = box.max.z;
            }
        }
        return hit;
    }
    
    public static boolean intersectRayBoundsFast(final Ray ray, final BoundingBox box) {
        return intersectRayBoundsFast(ray, box.getCenter(Intersector.tmp1), box.getDimensions(Intersector.tmp2));
    }
    
    public static boolean intersectRayBoundsFast(final Ray ray, final Vector3 center, final Vector3 dimensions) {
        final float divX = 1.0f / ray.direction.x;
        final float divY = 1.0f / ray.direction.y;
        final float divZ = 1.0f / ray.direction.z;
        float minx = (center.x - dimensions.x * 0.5f - ray.origin.x) * divX;
        float maxx = (center.x + dimensions.x * 0.5f - ray.origin.x) * divX;
        if (minx > maxx) {
            final float t = minx;
            minx = maxx;
            maxx = t;
        }
        float miny = (center.y - dimensions.y * 0.5f - ray.origin.y) * divY;
        float maxy = (center.y + dimensions.y * 0.5f - ray.origin.y) * divY;
        if (miny > maxy) {
            final float t2 = miny;
            miny = maxy;
            maxy = t2;
        }
        float minz = (center.z - dimensions.z * 0.5f - ray.origin.z) * divZ;
        float maxz = (center.z + dimensions.z * 0.5f - ray.origin.z) * divZ;
        if (minz > maxz) {
            final float t3 = minz;
            minz = maxz;
            maxz = t3;
        }
        final float min = Math.max(Math.max(minx, miny), minz);
        final float max = Math.min(Math.min(maxx, maxy), maxz);
        return max >= 0.0f && max >= min;
    }
    
    public static boolean intersectRayTriangles(final Ray ray, final float[] triangles, final Vector3 intersection) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (triangles.length / 3 % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < triangles.length - 6; i += 9) {
            final boolean result = intersectRayTriangle(ray, Intersector.tmp1.set(triangles[i], triangles[i + 1], triangles[i + 2]), Intersector.tmp2.set(triangles[i + 3], triangles[i + 4], triangles[i + 5]), Intersector.tmp3.set(triangles[i + 6], triangles[i + 7], triangles[i + 8]), Intersector.tmp);
            if (result) {
                final float dist = ray.origin.dst2(Intersector.tmp);
                if (dist < min_dist) {
                    min_dist = dist;
                    Intersector.best.set(Intersector.tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection != null) {
            intersection.set(Intersector.best);
        }
        return true;
    }
    
    public static boolean intersectRayTriangles(final Ray ray, final float[] vertices, final short[] indices, final int vertexSize, final Vector3 intersection) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (indices.length % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < indices.length; i += 3) {
            final int i2 = indices[i] * vertexSize;
            final int i3 = indices[i + 1] * vertexSize;
            final int i4 = indices[i + 2] * vertexSize;
            final boolean result = intersectRayTriangle(ray, Intersector.tmp1.set(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]), Intersector.tmp2.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), Intersector.tmp3.set(vertices[i4], vertices[i4 + 1], vertices[i4 + 2]), Intersector.tmp);
            if (result) {
                final float dist = ray.origin.dst2(Intersector.tmp);
                if (dist < min_dist) {
                    min_dist = dist;
                    Intersector.best.set(Intersector.tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection != null) {
            intersection.set(Intersector.best);
        }
        return true;
    }
    
    public static boolean intersectRayTriangles(final Ray ray, final List<Vector3> triangles, final Vector3 intersection) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (triangles.size() % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < triangles.size() - 2; i += 3) {
            final boolean result = intersectRayTriangle(ray, triangles.get(i), triangles.get(i + 1), triangles.get(i + 2), Intersector.tmp);
            if (result) {
                final float dist = ray.origin.dst2(Intersector.tmp);
                if (dist < min_dist) {
                    min_dist = dist;
                    Intersector.best.set(Intersector.tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection != null) {
            intersection.set(Intersector.best);
        }
        return true;
    }
    
    public static boolean intersectLines(final Vector2 p1, final Vector2 p2, final Vector2 p3, final Vector2 p4, final Vector2 intersection) {
        final float x1 = p1.x;
        final float y1 = p1.y;
        final float x2 = p2.x;
        final float y2 = p2.y;
        final float x3 = p3.x;
        final float y3 = p3.y;
        final float x4 = p4.x;
        final float y4 = p4.y;
        final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (d == 0.0f) {
            return false;
        }
        if (intersection != null) {
            final float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / d;
            intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
        }
        return true;
    }
    
    public static boolean intersectLines(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4, final Vector2 intersection) {
        final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (d == 0.0f) {
            return false;
        }
        if (intersection != null) {
            final float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / d;
            intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
        }
        return true;
    }
    
    public static boolean intersectLinePolygon(final Vector2 p1, final Vector2 p2, final Polygon polygon) {
        final float[] vertices = polygon.getTransformedVertices();
        final float x1 = p1.x;
        final float y1 = p1.y;
        final float x2 = p2.x;
        final float y2 = p2.y;
        final int n = vertices.length;
        float x3 = vertices[n - 2];
        float y3 = vertices[n - 1];
        for (int i = 0; i < n; i += 2) {
            final float x4 = vertices[i];
            final float y4 = vertices[i + 1];
            final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
            if (d != 0.0f) {
                final float yd = y1 - y3;
                final float xd = x1 - x3;
                final float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
                if (ua >= 0.0f && ua <= 1.0f) {
                    return true;
                }
            }
            x3 = x4;
            y3 = y4;
        }
        return false;
    }
    
    public static boolean intersectRectangles(final Rectangle rectangle1, final Rectangle rectangle2, final Rectangle intersection) {
        if (rectangle1.overlaps(rectangle2)) {
            intersection.x = Math.max(rectangle1.x, rectangle2.x);
            intersection.width = Math.min(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width) - intersection.x;
            intersection.y = Math.max(rectangle1.y, rectangle2.y);
            intersection.height = Math.min(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height) - intersection.y;
            return true;
        }
        return false;
    }
    
    public static boolean intersectSegmentRectangle(final float startX, final float startY, final float endX, final float endY, final Rectangle rectangle) {
        final float rectangleEndX = rectangle.x + rectangle.width;
        final float rectangleEndY = rectangle.y + rectangle.height;
        return intersectSegments(startX, startY, endX, endY, rectangle.x, rectangle.y, rectangle.x, rectangleEndY, null) || intersectSegments(startX, startY, endX, endY, rectangle.x, rectangle.y, rectangleEndX, rectangle.y, null) || intersectSegments(startX, startY, endX, endY, rectangleEndX, rectangle.y, rectangleEndX, rectangleEndY, null) || intersectSegments(startX, startY, endX, endY, rectangle.x, rectangleEndY, rectangleEndX, rectangleEndY, null) || rectangle.contains(startX, startY);
    }
    
    public static boolean intersectSegmentRectangle(final Vector2 start, final Vector2 end, final Rectangle rectangle) {
        return intersectSegmentRectangle(start.x, start.y, end.x, end.y, rectangle);
    }
    
    public static boolean intersectSegmentPolygon(final Vector2 p1, final Vector2 p2, final Polygon polygon) {
        final float[] vertices = polygon.getTransformedVertices();
        final float x1 = p1.x;
        final float y1 = p1.y;
        final float x2 = p2.x;
        final float y2 = p2.y;
        final int n = vertices.length;
        float x3 = vertices[n - 2];
        float y3 = vertices[n - 1];
        for (int i = 0; i < n; i += 2) {
            final float x4 = vertices[i];
            final float y4 = vertices[i + 1];
            final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
            if (d != 0.0f) {
                final float yd = y1 - y3;
                final float xd = x1 - x3;
                final float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
                if (ua >= 0.0f && ua <= 1.0f) {
                    final float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
                    if (ub >= 0.0f && ub <= 1.0f) {
                        return true;
                    }
                }
            }
            x3 = x4;
            y3 = y4;
        }
        return false;
    }
    
    public static boolean intersectSegments(final Vector2 p1, final Vector2 p2, final Vector2 p3, final Vector2 p4, final Vector2 intersection) {
        final float x1 = p1.x;
        final float y1 = p1.y;
        final float x2 = p2.x;
        final float y2 = p2.y;
        final float x3 = p3.x;
        final float y3 = p3.y;
        final float x4 = p4.x;
        final float y4 = p4.y;
        final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (d == 0.0f) {
            return false;
        }
        final float yd = y1 - y3;
        final float xd = x1 - x3;
        final float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
        if (ua < 0.0f || ua > 1.0f) {
            return false;
        }
        final float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
        if (ub < 0.0f || ub > 1.0f) {
            return false;
        }
        if (intersection != null) {
            intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
        }
        return true;
    }
    
    public static boolean intersectSegments(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4, final Vector2 intersection) {
        final float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (d == 0.0f) {
            return false;
        }
        final float yd = y1 - y3;
        final float xd = x1 - x3;
        final float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
        if (ua < 0.0f || ua > 1.0f) {
            return false;
        }
        final float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
        if (ub < 0.0f || ub > 1.0f) {
            return false;
        }
        if (intersection != null) {
            intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
        }
        return true;
    }
    
    static float det(final float a, final float b, final float c, final float d) {
        return a * d - b * c;
    }
    
    static double detd(final double a, final double b, final double c, final double d) {
        return a * d - b * c;
    }
    
    public static boolean overlaps(final Circle c1, final Circle c2) {
        return c1.overlaps(c2);
    }
    
    public static boolean overlaps(final Rectangle r1, final Rectangle r2) {
        return r1.overlaps(r2);
    }
    
    public static boolean overlaps(final Circle c, final Rectangle r) {
        float closestX = c.x;
        float closestY = c.y;
        if (c.x < r.x) {
            closestX = r.x;
        }
        else if (c.x > r.x + r.width) {
            closestX = r.x + r.width;
        }
        if (c.y < r.y) {
            closestY = r.y;
        }
        else if (c.y > r.y + r.height) {
            closestY = r.y + r.height;
        }
        closestX -= c.x;
        closestX *= closestX;
        closestY -= c.y;
        closestY *= closestY;
        return closestX + closestY < c.radius * c.radius;
    }
    
    public static boolean overlapConvexPolygons(final Polygon p1, final Polygon p2) {
        return overlapConvexPolygons(p1, p2, null);
    }
    
    public static boolean overlapConvexPolygons(final Polygon p1, final Polygon p2, final MinimumTranslationVector mtv) {
        return overlapConvexPolygons(p1.getTransformedVertices(), p2.getTransformedVertices(), mtv);
    }
    
    public static boolean overlapConvexPolygons(final float[] verts1, final float[] verts2, final MinimumTranslationVector mtv) {
        return overlapConvexPolygons(verts1, 0, verts1.length, verts2, 0, verts2.length, mtv);
    }
    
    public static boolean overlapConvexPolygons(final float[] verts1, final int offset1, final int count1, final float[] verts2, final int offset2, final int count2, final MinimumTranslationVector mtv) {
        float overlap = Float.MAX_VALUE;
        float smallestAxisX = 0.0f;
        float smallestAxisY = 0.0f;
        final int end1 = offset1 + count1;
        final int end2 = offset2 + count2;
        for (int i = offset1; i < end1; i += 2) {
            final float x1 = verts1[i];
            final float y1 = verts1[i + 1];
            final float x2 = verts1[(i + 2) % count1];
            final float y2 = verts1[(i + 3) % count1];
            float axisX = y1 - y2;
            float axisY = -(x1 - x2);
            final float length = (float)Math.sqrt(axisX * axisX + axisY * axisY);
            axisX /= length;
            axisY /= length;
            float max1;
            float min1 = max1 = axisX * verts1[0] + axisY * verts1[1];
            for (int j = offset1; j < end1; j += 2) {
                final float p = axisX * verts1[j] + axisY * verts1[j + 1];
                if (p < min1) {
                    min1 = p;
                }
                else if (p > max1) {
                    max1 = p;
                }
            }
            int numInNormalDir = 0;
            float max2;
            float min2 = max2 = axisX * verts2[0] + axisY * verts2[1];
            for (int k = offset2; k < end2; k += 2) {
                numInNormalDir -= pointLineSide(x1, y1, x2, y2, verts2[k], verts2[k + 1]);
                final float p2 = axisX * verts2[k] + axisY * verts2[k + 1];
                if (p2 < min2) {
                    min2 = p2;
                }
                else if (p2 > max2) {
                    max2 = p2;
                }
            }
            if ((min1 > min2 || max1 < min2) && (min2 > min1 || max2 < min1)) {
                return false;
            }
            float o = Math.min(max1, max2) - Math.max(min1, min2);
            if ((min1 < min2 && max1 > max2) || (min2 < min1 && max2 > max1)) {
                final float mins = Math.abs(min1 - min2);
                final float maxs = Math.abs(max1 - max2);
                if (mins < maxs) {
                    o += mins;
                }
                else {
                    o += maxs;
                }
            }
            if (o < overlap) {
                overlap = o;
                smallestAxisX = ((numInNormalDir >= 0) ? axisX : (-axisX));
                smallestAxisY = ((numInNormalDir >= 0) ? axisY : (-axisY));
            }
        }
        for (int i = offset2; i < end2; i += 2) {
            final float x1 = verts2[i];
            final float y1 = verts2[i + 1];
            final float x2 = verts2[(i + 2) % count2];
            final float y2 = verts2[(i + 3) % count2];
            float axisX = y1 - y2;
            float axisY = -(x1 - x2);
            final float length = (float)Math.sqrt(axisX * axisX + axisY * axisY);
            axisX /= length;
            axisY /= length;
            int numInNormalDir = 0;
            float max1;
            float min1 = max1 = axisX * verts1[0] + axisY * verts1[1];
            for (int j = offset1; j < end1; j += 2) {
                final float p = axisX * verts1[j] + axisY * verts1[j + 1];
                numInNormalDir -= pointLineSide(x1, y1, x2, y2, verts1[j], verts1[j + 1]);
                if (p < min1) {
                    min1 = p;
                }
                else if (p > max1) {
                    max1 = p;
                }
            }
            float max2;
            float min2 = max2 = axisX * verts2[0] + axisY * verts2[1];
            for (int k = offset2; k < end2; k += 2) {
                final float p2 = axisX * verts2[k] + axisY * verts2[k + 1];
                if (p2 < min2) {
                    min2 = p2;
                }
                else if (p2 > max2) {
                    max2 = p2;
                }
            }
            if ((min1 > min2 || max1 < min2) && (min2 > min1 || max2 < min1)) {
                return false;
            }
            float o = Math.min(max1, max2) - Math.max(min1, min2);
            if ((min1 < min2 && max1 > max2) || (min2 < min1 && max2 > max1)) {
                final float mins = Math.abs(min1 - min2);
                final float maxs = Math.abs(max1 - max2);
                if (mins < maxs) {
                    o += mins;
                }
                else {
                    o += maxs;
                }
            }
            if (o < overlap) {
                overlap = o;
                smallestAxisX = ((numInNormalDir < 0) ? axisX : (-axisX));
                smallestAxisY = ((numInNormalDir < 0) ? axisY : (-axisY));
            }
        }
        if (mtv != null) {
            mtv.normal.set(smallestAxisX, smallestAxisY);
            mtv.depth = overlap;
        }
        return true;
    }
    
    public static void splitTriangle(final float[] triangle, final Plane plane, final SplitTriangle split) {
        final int stride = triangle.length / 3;
        final boolean r1 = plane.testPoint(triangle[0], triangle[1], triangle[2]) == Plane.PlaneSide.Back;
        final boolean r2 = plane.testPoint(triangle[0 + stride], triangle[1 + stride], triangle[2 + stride]) == Plane.PlaneSide.Back;
        final boolean r3 = plane.testPoint(triangle[0 + stride * 2], triangle[1 + stride * 2], triangle[2 + stride * 2]) == Plane.PlaneSide.Back;
        split.reset();
        if (r1 == r2 && r2 == r3) {
            split.total = 1;
            if (r1) {
                split.numBack = 1;
                System.arraycopy(triangle, 0, split.back, 0, triangle.length);
            }
            else {
                split.numFront = 1;
                System.arraycopy(triangle, 0, split.front, 0, triangle.length);
            }
            return;
        }
        split.total = 3;
        split.numFront = ((!r1 + !r2 + !r3) ? 1 : 0);
        split.numBack = split.total - split.numFront;
        split.setSide(!r1);
        int first = 0;
        int second = stride;
        if (r1 != r2) {
            splitEdge(triangle, first, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
        }
        else {
            split.add(triangle, first, stride);
        }
        first = stride;
        second = stride + stride;
        if (r2 != r3) {
            splitEdge(triangle, first, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
        }
        else {
            split.add(triangle, first, stride);
        }
        first = stride + stride;
        second = 0;
        if (r3 != r1) {
            splitEdge(triangle, first, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
        }
        else {
            split.add(triangle, first, stride);
        }
        if (split.numFront == 2) {
            System.arraycopy(split.front, stride * 2, split.front, stride * 3, stride * 2);
            System.arraycopy(split.front, 0, split.front, stride * 5, stride);
        }
        else {
            System.arraycopy(split.back, stride * 2, split.back, stride * 3, stride * 2);
            System.arraycopy(split.back, 0, split.back, stride * 5, stride);
        }
    }
    
    private static void splitEdge(final float[] vertices, final int s, final int e, final int stride, final Plane plane, final float[] split, final int offset) {
        final float t = intersectLinePlane(vertices[s], vertices[s + 1], vertices[s + 2], vertices[e], vertices[e + 1], vertices[e + 2], plane, Intersector.intersection);
        split[offset + 0] = Intersector.intersection.x;
        split[offset + 1] = Intersector.intersection.y;
        split[offset + 2] = Intersector.intersection.z;
        for (int i = 3; i < stride; ++i) {
            final float a = vertices[s + i];
            final float b = vertices[e + i];
            split[offset + i] = a + t * (b - a);
        }
    }
    
    public static class SplitTriangle
    {
        public float[] front;
        public float[] back;
        float[] edgeSplit;
        public int numFront;
        public int numBack;
        public int total;
        boolean frontCurrent;
        int frontOffset;
        int backOffset;
        
        public SplitTriangle(final int numAttributes) {
            this.frontCurrent = false;
            this.frontOffset = 0;
            this.backOffset = 0;
            this.front = new float[numAttributes * 3 * 2];
            this.back = new float[numAttributes * 3 * 2];
            this.edgeSplit = new float[numAttributes];
        }
        
        @Override
        public String toString() {
            return "SplitTriangle [front=" + Arrays.toString(this.front) + ", back=" + Arrays.toString(this.back) + ", numFront=" + this.numFront + ", numBack=" + this.numBack + ", total=" + this.total + "]";
        }
        
        void setSide(final boolean front) {
            this.frontCurrent = front;
        }
        
        boolean getSide() {
            return this.frontCurrent;
        }
        
        void add(final float[] vertex, final int offset, final int stride) {
            if (this.frontCurrent) {
                System.arraycopy(vertex, offset, this.front, this.frontOffset, stride);
                this.frontOffset += stride;
            }
            else {
                System.arraycopy(vertex, offset, this.back, this.backOffset, stride);
                this.backOffset += stride;
            }
        }
        
        void reset() {
            this.frontCurrent = false;
            this.frontOffset = 0;
            this.backOffset = 0;
            this.numFront = 0;
            this.numBack = 0;
            this.total = 0;
        }
    }
    
    public static class MinimumTranslationVector
    {
        public Vector2 normal;
        public float depth;
        
        public MinimumTranslationVector() {
            this.normal = new Vector2();
            this.depth = 0.0f;
        }
    }
}
