// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.math.collision.BoundingBox;

public class Frustum
{
    protected static final Vector3[] clipSpacePlanePoints;
    protected static final float[] clipSpacePlanePointsArray;
    private static final Vector3 tmpV;
    public final Plane[] planes;
    public final Vector3[] planePoints;
    protected final float[] planePointsArray;
    
    static {
        clipSpacePlanePoints = new Vector3[] { new Vector3(-1.0f, -1.0f, -1.0f), new Vector3(1.0f, -1.0f, -1.0f), new Vector3(1.0f, 1.0f, -1.0f), new Vector3(-1.0f, 1.0f, -1.0f), new Vector3(-1.0f, -1.0f, 1.0f), new Vector3(1.0f, -1.0f, 1.0f), new Vector3(1.0f, 1.0f, 1.0f), new Vector3(-1.0f, 1.0f, 1.0f) };
        clipSpacePlanePointsArray = new float[24];
        int j = 0;
        Vector3[] clipSpacePlanePoints2;
        for (int length = (clipSpacePlanePoints2 = Frustum.clipSpacePlanePoints).length, i = 0; i < length; ++i) {
            final Vector3 v = clipSpacePlanePoints2[i];
            Frustum.clipSpacePlanePointsArray[j++] = v.x;
            Frustum.clipSpacePlanePointsArray[j++] = v.y;
            Frustum.clipSpacePlanePointsArray[j++] = v.z;
        }
        tmpV = new Vector3();
    }
    
    public Frustum() {
        this.planes = new Plane[6];
        this.planePoints = new Vector3[] { new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3() };
        this.planePointsArray = new float[24];
        for (int i = 0; i < 6; ++i) {
            this.planes[i] = new Plane(new Vector3(), 0.0f);
        }
    }
    
    public void update(final Matrix4 inverseProjectionView) {
        System.arraycopy(Frustum.clipSpacePlanePointsArray, 0, this.planePointsArray, 0, Frustum.clipSpacePlanePointsArray.length);
        Matrix4.prj(inverseProjectionView.val, this.planePointsArray, 0, 8, 3);
        int i = 0;
        int j = 0;
        while (i < 8) {
            final Vector3 v = this.planePoints[i];
            v.x = this.planePointsArray[j++];
            v.y = this.planePointsArray[j++];
            v.z = this.planePointsArray[j++];
            ++i;
        }
        this.planes[0].set(this.planePoints[1], this.planePoints[0], this.planePoints[2]);
        this.planes[1].set(this.planePoints[4], this.planePoints[5], this.planePoints[7]);
        this.planes[2].set(this.planePoints[0], this.planePoints[4], this.planePoints[3]);
        this.planes[3].set(this.planePoints[5], this.planePoints[1], this.planePoints[6]);
        this.planes[4].set(this.planePoints[2], this.planePoints[3], this.planePoints[6]);
        this.planes[5].set(this.planePoints[4], this.planePoints[0], this.planePoints[1]);
    }
    
    public boolean pointInFrustum(final Vector3 point) {
        for (int i = 0; i < this.planes.length; ++i) {
            final Plane.PlaneSide result = this.planes[i].testPoint(point);
            if (result == Plane.PlaneSide.Back) {
                return false;
            }
        }
        return true;
    }
    
    public boolean pointInFrustum(final float x, final float y, final float z) {
        for (int i = 0; i < this.planes.length; ++i) {
            final Plane.PlaneSide result = this.planes[i].testPoint(x, y, z);
            if (result == Plane.PlaneSide.Back) {
                return false;
            }
        }
        return true;
    }
    
    public boolean sphereInFrustum(final Vector3 center, final float radius) {
        for (int i = 0; i < 6; ++i) {
            if (this.planes[i].normal.x * center.x + this.planes[i].normal.y * center.y + this.planes[i].normal.z * center.z < -radius - this.planes[i].d) {
                return false;
            }
        }
        return true;
    }
    
    public boolean sphereInFrustum(final float x, final float y, final float z, final float radius) {
        for (int i = 0; i < 6; ++i) {
            if (this.planes[i].normal.x * x + this.planes[i].normal.y * y + this.planes[i].normal.z * z < -radius - this.planes[i].d) {
                return false;
            }
        }
        return true;
    }
    
    public boolean sphereInFrustumWithoutNearFar(final Vector3 center, final float radius) {
        for (int i = 2; i < 6; ++i) {
            if (this.planes[i].normal.x * center.x + this.planes[i].normal.y * center.y + this.planes[i].normal.z * center.z < -radius - this.planes[i].d) {
                return false;
            }
        }
        return true;
    }
    
    public boolean sphereInFrustumWithoutNearFar(final float x, final float y, final float z, final float radius) {
        for (int i = 2; i < 6; ++i) {
            if (this.planes[i].normal.x * x + this.planes[i].normal.y * y + this.planes[i].normal.z * z < -radius - this.planes[i].d) {
                return false;
            }
        }
        return true;
    }
    
    public boolean boundsInFrustum(final BoundingBox bounds) {
        for (int i = 0, len2 = this.planes.length; i < len2; ++i) {
            if (this.planes[i].testPoint(bounds.getCorner000(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                if (this.planes[i].testPoint(bounds.getCorner001(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                    if (this.planes[i].testPoint(bounds.getCorner010(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                        if (this.planes[i].testPoint(bounds.getCorner011(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                            if (this.planes[i].testPoint(bounds.getCorner100(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                                if (this.planes[i].testPoint(bounds.getCorner101(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                                    if (this.planes[i].testPoint(bounds.getCorner110(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                                        if (this.planes[i].testPoint(bounds.getCorner111(Frustum.tmpV)) == Plane.PlaneSide.Back) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public boolean boundsInFrustum(final Vector3 center, final Vector3 dimensions) {
        return this.boundsInFrustum(center.x, center.y, center.z, dimensions.x / 2.0f, dimensions.y / 2.0f, dimensions.z / 2.0f);
    }
    
    public boolean boundsInFrustum(final float x, final float y, final float z, final float halfWidth, final float halfHeight, final float halfDepth) {
        for (int i = 0, len2 = this.planes.length; i < len2; ++i) {
            if (this.planes[i].testPoint(x + halfWidth, y + halfHeight, z + halfDepth) == Plane.PlaneSide.Back) {
                if (this.planes[i].testPoint(x + halfWidth, y + halfHeight, z - halfDepth) == Plane.PlaneSide.Back) {
                    if (this.planes[i].testPoint(x + halfWidth, y - halfHeight, z + halfDepth) == Plane.PlaneSide.Back) {
                        if (this.planes[i].testPoint(x + halfWidth, y - halfHeight, z - halfDepth) == Plane.PlaneSide.Back) {
                            if (this.planes[i].testPoint(x - halfWidth, y + halfHeight, z + halfDepth) == Plane.PlaneSide.Back) {
                                if (this.planes[i].testPoint(x - halfWidth, y + halfHeight, z - halfDepth) == Plane.PlaneSide.Back) {
                                    if (this.planes[i].testPoint(x - halfWidth, y - halfHeight, z + halfDepth) == Plane.PlaneSide.Back) {
                                        if (this.planes[i].testPoint(x - halfWidth, y - halfHeight, z - halfDepth) == Plane.PlaneSide.Back) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
