// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;

public class BaseShapeBuilder
{
    protected static final Color tmpColor0;
    protected static final Color tmpColor1;
    protected static final Color tmpColor2;
    protected static final Color tmpColor3;
    protected static final Color tmpColor4;
    protected static final Vector3 tmpV0;
    protected static final Vector3 tmpV1;
    protected static final Vector3 tmpV2;
    protected static final Vector3 tmpV3;
    protected static final Vector3 tmpV4;
    protected static final Vector3 tmpV5;
    protected static final Vector3 tmpV6;
    protected static final Vector3 tmpV7;
    protected static final MeshPartBuilder.VertexInfo vertTmp0;
    protected static final MeshPartBuilder.VertexInfo vertTmp1;
    protected static final MeshPartBuilder.VertexInfo vertTmp2;
    protected static final MeshPartBuilder.VertexInfo vertTmp3;
    protected static final MeshPartBuilder.VertexInfo vertTmp4;
    protected static final MeshPartBuilder.VertexInfo vertTmp5;
    protected static final MeshPartBuilder.VertexInfo vertTmp6;
    protected static final MeshPartBuilder.VertexInfo vertTmp7;
    protected static final MeshPartBuilder.VertexInfo vertTmp8;
    protected static final Matrix4 matTmp1;
    private static final FlushablePool<Vector3> vectorPool;
    private static final FlushablePool<Matrix4> matrices4Pool;
    
    static {
        tmpColor0 = new Color();
        tmpColor1 = new Color();
        tmpColor2 = new Color();
        tmpColor3 = new Color();
        tmpColor4 = new Color();
        tmpV0 = new Vector3();
        tmpV1 = new Vector3();
        tmpV2 = new Vector3();
        tmpV3 = new Vector3();
        tmpV4 = new Vector3();
        tmpV5 = new Vector3();
        tmpV6 = new Vector3();
        tmpV7 = new Vector3();
        vertTmp0 = new MeshPartBuilder.VertexInfo();
        vertTmp1 = new MeshPartBuilder.VertexInfo();
        vertTmp2 = new MeshPartBuilder.VertexInfo();
        vertTmp3 = new MeshPartBuilder.VertexInfo();
        vertTmp4 = new MeshPartBuilder.VertexInfo();
        vertTmp5 = new MeshPartBuilder.VertexInfo();
        vertTmp6 = new MeshPartBuilder.VertexInfo();
        vertTmp7 = new MeshPartBuilder.VertexInfo();
        vertTmp8 = new MeshPartBuilder.VertexInfo();
        matTmp1 = new Matrix4();
        vectorPool = new FlushablePool<Vector3>() {
            @Override
            protected Vector3 newObject() {
                return new Vector3();
            }
        };
        matrices4Pool = new FlushablePool<Matrix4>() {
            @Override
            protected Matrix4 newObject() {
                return new Matrix4();
            }
        };
    }
    
    protected static Vector3 obtainV3() {
        return BaseShapeBuilder.vectorPool.obtain();
    }
    
    protected static Matrix4 obtainM4() {
        final Matrix4 result = BaseShapeBuilder.matrices4Pool.obtain();
        return result;
    }
    
    protected static void freeAll() {
        BaseShapeBuilder.vectorPool.flush();
        BaseShapeBuilder.matrices4Pool.flush();
    }
}
