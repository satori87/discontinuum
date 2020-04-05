// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Matrix3 implements Serializable
{
    private static final long serialVersionUID = 7907569533774959788L;
    public static final int M00 = 0;
    public static final int M01 = 3;
    public static final int M02 = 6;
    public static final int M10 = 1;
    public static final int M11 = 4;
    public static final int M12 = 7;
    public static final int M20 = 2;
    public static final int M21 = 5;
    public static final int M22 = 8;
    public float[] val;
    private float[] tmp;
    
    public Matrix3() {
        this.val = new float[9];
        this.tmp = new float[9];
        this.idt();
    }
    
    public Matrix3(final Matrix3 matrix) {
        this.val = new float[9];
        this.tmp = new float[9];
        this.set(matrix);
    }
    
    public Matrix3(final float[] values) {
        this.val = new float[9];
        this.tmp = new float[9];
        this.set(values);
    }
    
    public Matrix3 idt() {
        final float[] val = this.val;
        val[0] = 1.0f;
        val[1] = 0.0f;
        val[3] = (val[2] = 0.0f);
        val[4] = 1.0f;
        val[5] = 0.0f;
        val[7] = (val[6] = 0.0f);
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 mul(final Matrix3 m) {
        final float[] val = this.val;
        final float v00 = val[0] * m.val[0] + val[3] * m.val[1] + val[6] * m.val[2];
        final float v2 = val[0] * m.val[3] + val[3] * m.val[4] + val[6] * m.val[5];
        final float v3 = val[0] * m.val[6] + val[3] * m.val[7] + val[6] * m.val[8];
        final float v4 = val[1] * m.val[0] + val[4] * m.val[1] + val[7] * m.val[2];
        final float v5 = val[1] * m.val[3] + val[4] * m.val[4] + val[7] * m.val[5];
        final float v6 = val[1] * m.val[6] + val[4] * m.val[7] + val[7] * m.val[8];
        final float v7 = val[2] * m.val[0] + val[5] * m.val[1] + val[8] * m.val[2];
        final float v8 = val[2] * m.val[3] + val[5] * m.val[4] + val[8] * m.val[5];
        final float v9 = val[2] * m.val[6] + val[5] * m.val[7] + val[8] * m.val[8];
        val[0] = v00;
        val[1] = v4;
        val[2] = v7;
        val[3] = v2;
        val[4] = v5;
        val[5] = v8;
        val[6] = v3;
        val[7] = v6;
        val[8] = v9;
        return this;
    }
    
    public Matrix3 mulLeft(final Matrix3 m) {
        final float[] val = this.val;
        final float v00 = m.val[0] * val[0] + m.val[3] * val[1] + m.val[6] * val[2];
        final float v2 = m.val[0] * val[3] + m.val[3] * val[4] + m.val[6] * val[5];
        final float v3 = m.val[0] * val[6] + m.val[3] * val[7] + m.val[6] * val[8];
        final float v4 = m.val[1] * val[0] + m.val[4] * val[1] + m.val[7] * val[2];
        final float v5 = m.val[1] * val[3] + m.val[4] * val[4] + m.val[7] * val[5];
        final float v6 = m.val[1] * val[6] + m.val[4] * val[7] + m.val[7] * val[8];
        final float v7 = m.val[2] * val[0] + m.val[5] * val[1] + m.val[8] * val[2];
        final float v8 = m.val[2] * val[3] + m.val[5] * val[4] + m.val[8] * val[5];
        final float v9 = m.val[2] * val[6] + m.val[5] * val[7] + m.val[8] * val[8];
        val[0] = v00;
        val[1] = v4;
        val[2] = v7;
        val[3] = v2;
        val[4] = v5;
        val[5] = v8;
        val[6] = v3;
        val[7] = v6;
        val[8] = v9;
        return this;
    }
    
    public Matrix3 setToRotation(final float degrees) {
        return this.setToRotationRad(0.017453292f * degrees);
    }
    
    public Matrix3 setToRotationRad(final float radians) {
        final float cos = (float)Math.cos(radians);
        final float sin = (float)Math.sin(radians);
        final float[] val = this.val;
        val[0] = cos;
        val[1] = sin;
        val[2] = 0.0f;
        val[3] = -sin;
        val[4] = cos;
        val[5] = 0.0f;
        val[7] = (val[6] = 0.0f);
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 setToRotation(final Vector3 axis, final float degrees) {
        return this.setToRotation(axis, MathUtils.cosDeg(degrees), MathUtils.sinDeg(degrees));
    }
    
    public Matrix3 setToRotation(final Vector3 axis, final float cos, final float sin) {
        final float[] val = this.val;
        final float oc = 1.0f - cos;
        val[0] = oc * axis.x * axis.x + cos;
        val[1] = oc * axis.x * axis.y - axis.z * sin;
        val[2] = oc * axis.z * axis.x + axis.y * sin;
        val[3] = oc * axis.x * axis.y + axis.z * sin;
        val[4] = oc * axis.y * axis.y + cos;
        val[5] = oc * axis.y * axis.z - axis.x * sin;
        val[6] = oc * axis.z * axis.x - axis.y * sin;
        val[7] = oc * axis.y * axis.z + axis.x * sin;
        val[8] = oc * axis.z * axis.z + cos;
        return this;
    }
    
    public Matrix3 setToTranslation(final float x, final float y) {
        final float[] val = this.val;
        val[0] = 1.0f;
        val[1] = 0.0f;
        val[3] = (val[2] = 0.0f);
        val[4] = 1.0f;
        val[5] = 0.0f;
        val[6] = x;
        val[7] = y;
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 setToTranslation(final Vector2 translation) {
        final float[] val = this.val;
        val[0] = 1.0f;
        val[1] = 0.0f;
        val[3] = (val[2] = 0.0f);
        val[4] = 1.0f;
        val[5] = 0.0f;
        val[6] = translation.x;
        val[7] = translation.y;
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 setToScaling(final float scaleX, final float scaleY) {
        final float[] val = this.val;
        val[0] = scaleX;
        val[1] = 0.0f;
        val[3] = (val[2] = 0.0f);
        val[4] = scaleY;
        val[5] = 0.0f;
        val[7] = (val[6] = 0.0f);
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 setToScaling(final Vector2 scale) {
        final float[] val = this.val;
        val[0] = scale.x;
        val[1] = 0.0f;
        val[3] = (val[2] = 0.0f);
        val[4] = scale.y;
        val[5] = 0.0f;
        val[7] = (val[6] = 0.0f);
        val[8] = 1.0f;
        return this;
    }
    
    @Override
    public String toString() {
        final float[] val = this.val;
        return "[" + val[0] + "|" + val[3] + "|" + val[6] + "]\n" + "[" + val[1] + "|" + val[4] + "|" + val[7] + "]\n" + "[" + val[2] + "|" + val[5] + "|" + val[8] + "]";
    }
    
    public float det() {
        final float[] val = this.val;
        return val[0] * val[4] * val[8] + val[3] * val[7] * val[2] + val[6] * val[1] * val[5] - val[0] * val[7] * val[5] - val[3] * val[1] * val[8] - val[6] * val[4] * val[2];
    }
    
    public Matrix3 inv() {
        final float det = this.det();
        if (det == 0.0f) {
            throw new GdxRuntimeException("Can't invert a singular matrix");
        }
        final float inv_det = 1.0f / det;
        final float[] tmp = this.tmp;
        final float[] val = this.val;
        tmp[0] = val[4] * val[8] - val[5] * val[7];
        tmp[1] = val[2] * val[7] - val[1] * val[8];
        tmp[2] = val[1] * val[5] - val[2] * val[4];
        tmp[3] = val[5] * val[6] - val[3] * val[8];
        tmp[4] = val[0] * val[8] - val[2] * val[6];
        tmp[5] = val[2] * val[3] - val[0] * val[5];
        tmp[6] = val[3] * val[7] - val[4] * val[6];
        tmp[7] = val[1] * val[6] - val[0] * val[7];
        tmp[8] = val[0] * val[4] - val[1] * val[3];
        val[0] = inv_det * tmp[0];
        val[1] = inv_det * tmp[1];
        val[2] = inv_det * tmp[2];
        val[3] = inv_det * tmp[3];
        val[4] = inv_det * tmp[4];
        val[5] = inv_det * tmp[5];
        val[6] = inv_det * tmp[6];
        val[7] = inv_det * tmp[7];
        val[8] = inv_det * tmp[8];
        return this;
    }
    
    public Matrix3 set(final Matrix3 mat) {
        System.arraycopy(mat.val, 0, this.val, 0, this.val.length);
        return this;
    }
    
    public Matrix3 set(final Affine2 affine) {
        final float[] val = this.val;
        val[0] = affine.m00;
        val[1] = affine.m10;
        val[2] = 0.0f;
        val[3] = affine.m01;
        val[4] = affine.m11;
        val[5] = 0.0f;
        val[6] = affine.m02;
        val[7] = affine.m12;
        val[8] = 1.0f;
        return this;
    }
    
    public Matrix3 set(final Matrix4 mat) {
        final float[] val = this.val;
        val[0] = mat.val[0];
        val[1] = mat.val[1];
        val[2] = mat.val[2];
        val[3] = mat.val[4];
        val[4] = mat.val[5];
        val[5] = mat.val[6];
        val[6] = mat.val[8];
        val[7] = mat.val[9];
        val[8] = mat.val[10];
        return this;
    }
    
    public Matrix3 set(final float[] values) {
        System.arraycopy(values, 0, this.val, 0, this.val.length);
        return this;
    }
    
    public Matrix3 trn(final Vector2 vector) {
        final float[] val = this.val;
        final int n = 6;
        val[n] += vector.x;
        final float[] val2 = this.val;
        final int n2 = 7;
        val2[n2] += vector.y;
        return this;
    }
    
    public Matrix3 trn(final float x, final float y) {
        final float[] val = this.val;
        final int n = 6;
        val[n] += x;
        final float[] val2 = this.val;
        final int n2 = 7;
        val2[n2] += y;
        return this;
    }
    
    public Matrix3 trn(final Vector3 vector) {
        final float[] val = this.val;
        final int n = 6;
        val[n] += vector.x;
        final float[] val2 = this.val;
        final int n2 = 7;
        val2[n2] += vector.y;
        return this;
    }
    
    public Matrix3 translate(final float x, final float y) {
        final float[] val = this.val;
        this.tmp[0] = 1.0f;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = 1.0f;
        this.tmp[5] = 0.0f;
        this.tmp[6] = x;
        this.tmp[7] = y;
        this.tmp[8] = 1.0f;
        mul(val, this.tmp);
        return this;
    }
    
    public Matrix3 translate(final Vector2 translation) {
        final float[] val = this.val;
        this.tmp[0] = 1.0f;
        this.tmp[1] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[4] = 1.0f;
        this.tmp[5] = 0.0f;
        this.tmp[6] = translation.x;
        this.tmp[7] = translation.y;
        this.tmp[8] = 1.0f;
        mul(val, this.tmp);
        return this;
    }
    
    public Matrix3 rotate(final float degrees) {
        return this.rotateRad(0.017453292f * degrees);
    }
    
    public Matrix3 rotateRad(final float radians) {
        if (radians == 0.0f) {
            return this;
        }
        final float cos = (float)Math.cos(radians);
        final float sin = (float)Math.sin(radians);
        final float[] tmp = this.tmp;
        tmp[0] = cos;
        tmp[1] = sin;
        tmp[2] = 0.0f;
        tmp[3] = -sin;
        tmp[4] = cos;
        tmp[5] = 0.0f;
        tmp[7] = (tmp[6] = 0.0f);
        tmp[8] = 1.0f;
        mul(this.val, tmp);
        return this;
    }
    
    public Matrix3 scale(final float scaleX, final float scaleY) {
        final float[] tmp = this.tmp;
        tmp[0] = scaleX;
        tmp[1] = 0.0f;
        tmp[3] = (tmp[2] = 0.0f);
        tmp[4] = scaleY;
        tmp[5] = 0.0f;
        tmp[7] = (tmp[6] = 0.0f);
        tmp[8] = 1.0f;
        mul(this.val, tmp);
        return this;
    }
    
    public Matrix3 scale(final Vector2 scale) {
        final float[] tmp = this.tmp;
        tmp[0] = scale.x;
        tmp[1] = 0.0f;
        tmp[3] = (tmp[2] = 0.0f);
        tmp[4] = scale.y;
        tmp[5] = 0.0f;
        tmp[7] = (tmp[6] = 0.0f);
        tmp[8] = 1.0f;
        mul(this.val, tmp);
        return this;
    }
    
    public float[] getValues() {
        return this.val;
    }
    
    public Vector2 getTranslation(final Vector2 position) {
        position.x = this.val[6];
        position.y = this.val[7];
        return position;
    }
    
    public Vector2 getScale(final Vector2 scale) {
        final float[] val = this.val;
        scale.x = (float)Math.sqrt(val[0] * val[0] + val[3] * val[3]);
        scale.y = (float)Math.sqrt(val[1] * val[1] + val[4] * val[4]);
        return scale;
    }
    
    public float getRotation() {
        return 57.295776f * (float)Math.atan2(this.val[1], this.val[0]);
    }
    
    public float getRotationRad() {
        return (float)Math.atan2(this.val[1], this.val[0]);
    }
    
    public Matrix3 scl(final float scale) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= scale;
        final float[] val2 = this.val;
        final int n2 = 4;
        val2[n2] *= scale;
        return this;
    }
    
    public Matrix3 scl(final Vector2 scale) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= scale.x;
        final float[] val2 = this.val;
        final int n2 = 4;
        val2[n2] *= scale.y;
        return this;
    }
    
    public Matrix3 scl(final Vector3 scale) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= scale.x;
        final float[] val2 = this.val;
        final int n2 = 4;
        val2[n2] *= scale.y;
        return this;
    }
    
    public Matrix3 transpose() {
        final float[] val = this.val;
        final float v01 = val[1];
        final float v2 = val[2];
        final float v3 = val[3];
        final float v4 = val[5];
        final float v5 = val[6];
        final float v6 = val[7];
        val[3] = v01;
        val[6] = v2;
        val[1] = v3;
        val[7] = v4;
        val[2] = v5;
        val[5] = v6;
        return this;
    }
    
    private static void mul(final float[] mata, final float[] matb) {
        final float v00 = mata[0] * matb[0] + mata[3] * matb[1] + mata[6] * matb[2];
        final float v2 = mata[0] * matb[3] + mata[3] * matb[4] + mata[6] * matb[5];
        final float v3 = mata[0] * matb[6] + mata[3] * matb[7] + mata[6] * matb[8];
        final float v4 = mata[1] * matb[0] + mata[4] * matb[1] + mata[7] * matb[2];
        final float v5 = mata[1] * matb[3] + mata[4] * matb[4] + mata[7] * matb[5];
        final float v6 = mata[1] * matb[6] + mata[4] * matb[7] + mata[7] * matb[8];
        final float v7 = mata[2] * matb[0] + mata[5] * matb[1] + mata[8] * matb[2];
        final float v8 = mata[2] * matb[3] + mata[5] * matb[4] + mata[8] * matb[5];
        final float v9 = mata[2] * matb[6] + mata[5] * matb[7] + mata[8] * matb[8];
        mata[0] = v00;
        mata[1] = v4;
        mata[2] = v7;
        mata[3] = v2;
        mata[4] = v5;
        mata[5] = v8;
        mata[6] = v3;
        mata[7] = v6;
        mata[8] = v9;
    }
}
