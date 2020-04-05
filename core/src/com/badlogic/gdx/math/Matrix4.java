// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import java.io.Serializable;

public class Matrix4 implements Serializable
{
    private static final long serialVersionUID = -2717655254359579617L;
    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;
    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;
    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;
    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;
    private static final float[] tmp;
    public final float[] val;
    static Quaternion quat;
    static Quaternion quat2;
    static final Vector3 l_vez;
    static final Vector3 l_vex;
    static final Vector3 l_vey;
    static final Vector3 tmpVec;
    static final Matrix4 tmpMat;
    static final Vector3 right;
    static final Vector3 tmpForward;
    static final Vector3 tmpUp;
    
    static {
        tmp = new float[16];
        Matrix4.quat = new Quaternion();
        Matrix4.quat2 = new Quaternion();
        l_vez = new Vector3();
        l_vex = new Vector3();
        l_vey = new Vector3();
        tmpVec = new Vector3();
        tmpMat = new Matrix4();
        right = new Vector3();
        tmpForward = new Vector3();
        tmpUp = new Vector3();
    }
    
    public Matrix4() {
        (this.val = new float[16])[0] = 1.0f;
        this.val[5] = 1.0f;
        this.val[10] = 1.0f;
        this.val[15] = 1.0f;
    }
    
    public Matrix4(final Matrix4 matrix) {
        this.val = new float[16];
        this.set(matrix);
    }
    
    public Matrix4(final float[] values) {
        this.val = new float[16];
        this.set(values);
    }
    
    public Matrix4(final Quaternion quaternion) {
        this.val = new float[16];
        this.set(quaternion);
    }
    
    public Matrix4(final Vector3 position, final Quaternion rotation, final Vector3 scale) {
        this.val = new float[16];
        this.set(position, rotation, scale);
    }
    
    public Matrix4 set(final Matrix4 matrix) {
        return this.set(matrix.val);
    }
    
    public Matrix4 set(final float[] values) {
        System.arraycopy(values, 0, this.val, 0, this.val.length);
        return this;
    }
    
    public Matrix4 set(final Quaternion quaternion) {
        return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }
    
    public Matrix4 set(final float quaternionX, final float quaternionY, final float quaternionZ, final float quaternionW) {
        return this.set(0.0f, 0.0f, 0.0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }
    
    public Matrix4 set(final Vector3 position, final Quaternion orientation) {
        return this.set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);
    }
    
    public Matrix4 set(final float translationX, final float translationY, final float translationZ, final float quaternionX, final float quaternionY, final float quaternionZ, final float quaternionW) {
        final float xs = quaternionX * 2.0f;
        final float ys = quaternionY * 2.0f;
        final float zs = quaternionZ * 2.0f;
        final float wx = quaternionW * xs;
        final float wy = quaternionW * ys;
        final float wz = quaternionW * zs;
        final float xx = quaternionX * xs;
        final float xy = quaternionX * ys;
        final float xz = quaternionX * zs;
        final float yy = quaternionY * ys;
        final float yz = quaternionY * zs;
        final float zz = quaternionZ * zs;
        this.val[0] = 1.0f - (yy + zz);
        this.val[4] = xy - wz;
        this.val[8] = xz + wy;
        this.val[12] = translationX;
        this.val[1] = xy + wz;
        this.val[5] = 1.0f - (xx + zz);
        this.val[9] = yz - wx;
        this.val[13] = translationY;
        this.val[2] = xz - wy;
        this.val[6] = yz + wx;
        this.val[10] = 1.0f - (xx + yy);
        this.val[14] = translationZ;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 set(final Vector3 position, final Quaternion orientation, final Vector3 scale) {
        return this.set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w, scale.x, scale.y, scale.z);
    }
    
    public Matrix4 set(final float translationX, final float translationY, final float translationZ, final float quaternionX, final float quaternionY, final float quaternionZ, final float quaternionW, final float scaleX, final float scaleY, final float scaleZ) {
        final float xs = quaternionX * 2.0f;
        final float ys = quaternionY * 2.0f;
        final float zs = quaternionZ * 2.0f;
        final float wx = quaternionW * xs;
        final float wy = quaternionW * ys;
        final float wz = quaternionW * zs;
        final float xx = quaternionX * xs;
        final float xy = quaternionX * ys;
        final float xz = quaternionX * zs;
        final float yy = quaternionY * ys;
        final float yz = quaternionY * zs;
        final float zz = quaternionZ * zs;
        this.val[0] = scaleX * (1.0f - (yy + zz));
        this.val[4] = scaleY * (xy - wz);
        this.val[8] = scaleZ * (xz + wy);
        this.val[12] = translationX;
        this.val[1] = scaleX * (xy + wz);
        this.val[5] = scaleY * (1.0f - (xx + zz));
        this.val[9] = scaleZ * (yz - wx);
        this.val[13] = translationY;
        this.val[2] = scaleX * (xz - wy);
        this.val[6] = scaleY * (yz + wx);
        this.val[10] = scaleZ * (1.0f - (xx + yy));
        this.val[14] = translationZ;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 set(final Vector3 xAxis, final Vector3 yAxis, final Vector3 zAxis, final Vector3 pos) {
        this.val[0] = xAxis.x;
        this.val[4] = xAxis.y;
        this.val[8] = xAxis.z;
        this.val[1] = yAxis.x;
        this.val[5] = yAxis.y;
        this.val[9] = yAxis.z;
        this.val[2] = zAxis.x;
        this.val[6] = zAxis.y;
        this.val[10] = zAxis.z;
        this.val[12] = pos.x;
        this.val[13] = pos.y;
        this.val[14] = pos.z;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 cpy() {
        return new Matrix4(this);
    }
    
    public Matrix4 trn(final Vector3 vector) {
        final float[] val = this.val;
        final int n = 12;
        val[n] += vector.x;
        final float[] val2 = this.val;
        final int n2 = 13;
        val2[n2] += vector.y;
        final float[] val3 = this.val;
        final int n3 = 14;
        val3[n3] += vector.z;
        return this;
    }
    
    public Matrix4 trn(final float x, final float y, final float z) {
        final float[] val = this.val;
        final int n = 12;
        val[n] += x;
        final float[] val2 = this.val;
        final int n2 = 13;
        val2[n2] += y;
        final float[] val3 = this.val;
        final int n3 = 14;
        val3[n3] += z;
        return this;
    }
    
    public float[] getValues() {
        return this.val;
    }
    
    public Matrix4 mul(final Matrix4 matrix) {
        mul(this.val, matrix.val);
        return this;
    }
    
    public Matrix4 mulLeft(final Matrix4 matrix) {
        Matrix4.tmpMat.set(matrix);
        mul(Matrix4.tmpMat.val, this.val);
        return this.set(Matrix4.tmpMat);
    }
    
    public Matrix4 tra() {
        Matrix4.tmp[0] = this.val[0];
        Matrix4.tmp[4] = this.val[1];
        Matrix4.tmp[8] = this.val[2];
        Matrix4.tmp[12] = this.val[3];
        Matrix4.tmp[1] = this.val[4];
        Matrix4.tmp[5] = this.val[5];
        Matrix4.tmp[9] = this.val[6];
        Matrix4.tmp[13] = this.val[7];
        Matrix4.tmp[2] = this.val[8];
        Matrix4.tmp[6] = this.val[9];
        Matrix4.tmp[10] = this.val[10];
        Matrix4.tmp[14] = this.val[11];
        Matrix4.tmp[3] = this.val[12];
        Matrix4.tmp[7] = this.val[13];
        Matrix4.tmp[11] = this.val[14];
        Matrix4.tmp[15] = this.val[15];
        return this.set(Matrix4.tmp);
    }
    
    public Matrix4 idt() {
        this.val[0] = 1.0f;
        this.val[4] = 0.0f;
        this.val[8] = 0.0f;
        this.val[12] = 0.0f;
        this.val[1] = 0.0f;
        this.val[5] = 1.0f;
        this.val[9] = 0.0f;
        this.val[13] = 0.0f;
        this.val[2] = 0.0f;
        this.val[6] = 0.0f;
        this.val[10] = 1.0f;
        this.val[14] = 0.0f;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 inv() {
        final float l_det = this.val[3] * this.val[6] * this.val[9] * this.val[12] - this.val[2] * this.val[7] * this.val[9] * this.val[12] - this.val[3] * this.val[5] * this.val[10] * this.val[12] + this.val[1] * this.val[7] * this.val[10] * this.val[12] + this.val[2] * this.val[5] * this.val[11] * this.val[12] - this.val[1] * this.val[6] * this.val[11] * this.val[12] - this.val[3] * this.val[6] * this.val[8] * this.val[13] + this.val[2] * this.val[7] * this.val[8] * this.val[13] + this.val[3] * this.val[4] * this.val[10] * this.val[13] - this.val[0] * this.val[7] * this.val[10] * this.val[13] - this.val[2] * this.val[4] * this.val[11] * this.val[13] + this.val[0] * this.val[6] * this.val[11] * this.val[13] + this.val[3] * this.val[5] * this.val[8] * this.val[14] - this.val[1] * this.val[7] * this.val[8] * this.val[14] - this.val[3] * this.val[4] * this.val[9] * this.val[14] + this.val[0] * this.val[7] * this.val[9] * this.val[14] + this.val[1] * this.val[4] * this.val[11] * this.val[14] - this.val[0] * this.val[5] * this.val[11] * this.val[14] - this.val[2] * this.val[5] * this.val[8] * this.val[15] + this.val[1] * this.val[6] * this.val[8] * this.val[15] + this.val[2] * this.val[4] * this.val[9] * this.val[15] - this.val[0] * this.val[6] * this.val[9] * this.val[15] - this.val[1] * this.val[4] * this.val[10] * this.val[15] + this.val[0] * this.val[5] * this.val[10] * this.val[15];
        if (l_det == 0.0f) {
            throw new RuntimeException("non-invertible matrix");
        }
        final float inv_det = 1.0f / l_det;
        Matrix4.tmp[0] = this.val[9] * this.val[14] * this.val[7] - this.val[13] * this.val[10] * this.val[7] + this.val[13] * this.val[6] * this.val[11] - this.val[5] * this.val[14] * this.val[11] - this.val[9] * this.val[6] * this.val[15] + this.val[5] * this.val[10] * this.val[15];
        Matrix4.tmp[4] = this.val[12] * this.val[10] * this.val[7] - this.val[8] * this.val[14] * this.val[7] - this.val[12] * this.val[6] * this.val[11] + this.val[4] * this.val[14] * this.val[11] + this.val[8] * this.val[6] * this.val[15] - this.val[4] * this.val[10] * this.val[15];
        Matrix4.tmp[8] = this.val[8] * this.val[13] * this.val[7] - this.val[12] * this.val[9] * this.val[7] + this.val[12] * this.val[5] * this.val[11] - this.val[4] * this.val[13] * this.val[11] - this.val[8] * this.val[5] * this.val[15] + this.val[4] * this.val[9] * this.val[15];
        Matrix4.tmp[12] = this.val[12] * this.val[9] * this.val[6] - this.val[8] * this.val[13] * this.val[6] - this.val[12] * this.val[5] * this.val[10] + this.val[4] * this.val[13] * this.val[10] + this.val[8] * this.val[5] * this.val[14] - this.val[4] * this.val[9] * this.val[14];
        Matrix4.tmp[1] = this.val[13] * this.val[10] * this.val[3] - this.val[9] * this.val[14] * this.val[3] - this.val[13] * this.val[2] * this.val[11] + this.val[1] * this.val[14] * this.val[11] + this.val[9] * this.val[2] * this.val[15] - this.val[1] * this.val[10] * this.val[15];
        Matrix4.tmp[5] = this.val[8] * this.val[14] * this.val[3] - this.val[12] * this.val[10] * this.val[3] + this.val[12] * this.val[2] * this.val[11] - this.val[0] * this.val[14] * this.val[11] - this.val[8] * this.val[2] * this.val[15] + this.val[0] * this.val[10] * this.val[15];
        Matrix4.tmp[9] = this.val[12] * this.val[9] * this.val[3] - this.val[8] * this.val[13] * this.val[3] - this.val[12] * this.val[1] * this.val[11] + this.val[0] * this.val[13] * this.val[11] + this.val[8] * this.val[1] * this.val[15] - this.val[0] * this.val[9] * this.val[15];
        Matrix4.tmp[13] = this.val[8] * this.val[13] * this.val[2] - this.val[12] * this.val[9] * this.val[2] + this.val[12] * this.val[1] * this.val[10] - this.val[0] * this.val[13] * this.val[10] - this.val[8] * this.val[1] * this.val[14] + this.val[0] * this.val[9] * this.val[14];
        Matrix4.tmp[2] = this.val[5] * this.val[14] * this.val[3] - this.val[13] * this.val[6] * this.val[3] + this.val[13] * this.val[2] * this.val[7] - this.val[1] * this.val[14] * this.val[7] - this.val[5] * this.val[2] * this.val[15] + this.val[1] * this.val[6] * this.val[15];
        Matrix4.tmp[6] = this.val[12] * this.val[6] * this.val[3] - this.val[4] * this.val[14] * this.val[3] - this.val[12] * this.val[2] * this.val[7] + this.val[0] * this.val[14] * this.val[7] + this.val[4] * this.val[2] * this.val[15] - this.val[0] * this.val[6] * this.val[15];
        Matrix4.tmp[10] = this.val[4] * this.val[13] * this.val[3] - this.val[12] * this.val[5] * this.val[3] + this.val[12] * this.val[1] * this.val[7] - this.val[0] * this.val[13] * this.val[7] - this.val[4] * this.val[1] * this.val[15] + this.val[0] * this.val[5] * this.val[15];
        Matrix4.tmp[14] = this.val[12] * this.val[5] * this.val[2] - this.val[4] * this.val[13] * this.val[2] - this.val[12] * this.val[1] * this.val[6] + this.val[0] * this.val[13] * this.val[6] + this.val[4] * this.val[1] * this.val[14] - this.val[0] * this.val[5] * this.val[14];
        Matrix4.tmp[3] = this.val[9] * this.val[6] * this.val[3] - this.val[5] * this.val[10] * this.val[3] - this.val[9] * this.val[2] * this.val[7] + this.val[1] * this.val[10] * this.val[7] + this.val[5] * this.val[2] * this.val[11] - this.val[1] * this.val[6] * this.val[11];
        Matrix4.tmp[7] = this.val[4] * this.val[10] * this.val[3] - this.val[8] * this.val[6] * this.val[3] + this.val[8] * this.val[2] * this.val[7] - this.val[0] * this.val[10] * this.val[7] - this.val[4] * this.val[2] * this.val[11] + this.val[0] * this.val[6] * this.val[11];
        Matrix4.tmp[11] = this.val[8] * this.val[5] * this.val[3] - this.val[4] * this.val[9] * this.val[3] - this.val[8] * this.val[1] * this.val[7] + this.val[0] * this.val[9] * this.val[7] + this.val[4] * this.val[1] * this.val[11] - this.val[0] * this.val[5] * this.val[11];
        Matrix4.tmp[15] = this.val[4] * this.val[9] * this.val[2] - this.val[8] * this.val[5] * this.val[2] + this.val[8] * this.val[1] * this.val[6] - this.val[0] * this.val[9] * this.val[6] - this.val[4] * this.val[1] * this.val[10] + this.val[0] * this.val[5] * this.val[10];
        this.val[0] = Matrix4.tmp[0] * inv_det;
        this.val[4] = Matrix4.tmp[4] * inv_det;
        this.val[8] = Matrix4.tmp[8] * inv_det;
        this.val[12] = Matrix4.tmp[12] * inv_det;
        this.val[1] = Matrix4.tmp[1] * inv_det;
        this.val[5] = Matrix4.tmp[5] * inv_det;
        this.val[9] = Matrix4.tmp[9] * inv_det;
        this.val[13] = Matrix4.tmp[13] * inv_det;
        this.val[2] = Matrix4.tmp[2] * inv_det;
        this.val[6] = Matrix4.tmp[6] * inv_det;
        this.val[10] = Matrix4.tmp[10] * inv_det;
        this.val[14] = Matrix4.tmp[14] * inv_det;
        this.val[3] = Matrix4.tmp[3] * inv_det;
        this.val[7] = Matrix4.tmp[7] * inv_det;
        this.val[11] = Matrix4.tmp[11] * inv_det;
        this.val[15] = Matrix4.tmp[15] * inv_det;
        return this;
    }
    
    public float det() {
        return this.val[3] * this.val[6] * this.val[9] * this.val[12] - this.val[2] * this.val[7] * this.val[9] * this.val[12] - this.val[3] * this.val[5] * this.val[10] * this.val[12] + this.val[1] * this.val[7] * this.val[10] * this.val[12] + this.val[2] * this.val[5] * this.val[11] * this.val[12] - this.val[1] * this.val[6] * this.val[11] * this.val[12] - this.val[3] * this.val[6] * this.val[8] * this.val[13] + this.val[2] * this.val[7] * this.val[8] * this.val[13] + this.val[3] * this.val[4] * this.val[10] * this.val[13] - this.val[0] * this.val[7] * this.val[10] * this.val[13] - this.val[2] * this.val[4] * this.val[11] * this.val[13] + this.val[0] * this.val[6] * this.val[11] * this.val[13] + this.val[3] * this.val[5] * this.val[8] * this.val[14] - this.val[1] * this.val[7] * this.val[8] * this.val[14] - this.val[3] * this.val[4] * this.val[9] * this.val[14] + this.val[0] * this.val[7] * this.val[9] * this.val[14] + this.val[1] * this.val[4] * this.val[11] * this.val[14] - this.val[0] * this.val[5] * this.val[11] * this.val[14] - this.val[2] * this.val[5] * this.val[8] * this.val[15] + this.val[1] * this.val[6] * this.val[8] * this.val[15] + this.val[2] * this.val[4] * this.val[9] * this.val[15] - this.val[0] * this.val[6] * this.val[9] * this.val[15] - this.val[1] * this.val[4] * this.val[10] * this.val[15] + this.val[0] * this.val[5] * this.val[10] * this.val[15];
    }
    
    public float det3x3() {
        return this.val[0] * this.val[5] * this.val[10] + this.val[4] * this.val[9] * this.val[2] + this.val[8] * this.val[1] * this.val[6] - this.val[0] * this.val[9] * this.val[6] - this.val[4] * this.val[1] * this.val[10] - this.val[8] * this.val[5] * this.val[2];
    }
    
    public Matrix4 setToProjection(final float near, final float far, final float fovy, final float aspectRatio) {
        this.idt();
        final float l_fd = (float)(1.0 / Math.tan(fovy * 0.017453292519943295 / 2.0));
        final float l_a1 = (far + near) / (near - far);
        final float l_a2 = 2.0f * far * near / (near - far);
        this.val[0] = l_fd / aspectRatio;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 0.0f;
        this.val[5] = l_fd;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = l_a1;
        this.val[11] = -1.0f;
        this.val[12] = 0.0f;
        this.val[13] = 0.0f;
        this.val[14] = l_a2;
        this.val[15] = 0.0f;
        return this;
    }
    
    public Matrix4 setToProjection(final float left, final float right, final float bottom, final float top, final float near, final float far) {
        final float x = 2.0f * near / (right - left);
        final float y = 2.0f * near / (top - bottom);
        final float a = (right + left) / (right - left);
        final float b = (top + bottom) / (top - bottom);
        final float l_a1 = (far + near) / (near - far);
        final float l_a2 = 2.0f * far * near / (near - far);
        this.val[0] = x;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 0.0f;
        this.val[5] = y;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = a;
        this.val[9] = b;
        this.val[10] = l_a1;
        this.val[11] = -1.0f;
        this.val[12] = 0.0f;
        this.val[13] = 0.0f;
        this.val[14] = l_a2;
        this.val[15] = 0.0f;
        return this;
    }
    
    public Matrix4 setToOrtho2D(final float x, final float y, final float width, final float height) {
        this.setToOrtho(x, x + width, y, y + height, 0.0f, 1.0f);
        return this;
    }
    
    public Matrix4 setToOrtho2D(final float x, final float y, final float width, final float height, final float near, final float far) {
        this.setToOrtho(x, x + width, y, y + height, near, far);
        return this;
    }
    
    public Matrix4 setToOrtho(final float left, final float right, final float bottom, final float top, final float near, final float far) {
        this.idt();
        final float x_orth = 2.0f / (right - left);
        final float y_orth = 2.0f / (top - bottom);
        final float z_orth = -2.0f / (far - near);
        final float tx = -(right + left) / (right - left);
        final float ty = -(top + bottom) / (top - bottom);
        final float tz = -(far + near) / (far - near);
        this.val[0] = x_orth;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 0.0f;
        this.val[5] = y_orth;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = z_orth;
        this.val[11] = 0.0f;
        this.val[12] = tx;
        this.val[13] = ty;
        this.val[14] = tz;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 setTranslation(final Vector3 vector) {
        this.val[12] = vector.x;
        this.val[13] = vector.y;
        this.val[14] = vector.z;
        return this;
    }
    
    public Matrix4 setTranslation(final float x, final float y, final float z) {
        this.val[12] = x;
        this.val[13] = y;
        this.val[14] = z;
        return this;
    }
    
    public Matrix4 setToTranslation(final Vector3 vector) {
        this.idt();
        this.val[12] = vector.x;
        this.val[13] = vector.y;
        this.val[14] = vector.z;
        return this;
    }
    
    public Matrix4 setToTranslation(final float x, final float y, final float z) {
        this.idt();
        this.val[12] = x;
        this.val[13] = y;
        this.val[14] = z;
        return this;
    }
    
    public Matrix4 setToTranslationAndScaling(final Vector3 translation, final Vector3 scaling) {
        this.idt();
        this.val[12] = translation.x;
        this.val[13] = translation.y;
        this.val[14] = translation.z;
        this.val[0] = scaling.x;
        this.val[5] = scaling.y;
        this.val[10] = scaling.z;
        return this;
    }
    
    public Matrix4 setToTranslationAndScaling(final float translationX, final float translationY, final float translationZ, final float scalingX, final float scalingY, final float scalingZ) {
        this.idt();
        this.val[12] = translationX;
        this.val[13] = translationY;
        this.val[14] = translationZ;
        this.val[0] = scalingX;
        this.val[5] = scalingY;
        this.val[10] = scalingZ;
        return this;
    }
    
    public Matrix4 setToRotation(final Vector3 axis, final float degrees) {
        if (degrees == 0.0f) {
            this.idt();
            return this;
        }
        return this.set(Matrix4.quat.set(axis, degrees));
    }
    
    public Matrix4 setToRotationRad(final Vector3 axis, final float radians) {
        if (radians == 0.0f) {
            this.idt();
            return this;
        }
        return this.set(Matrix4.quat.setFromAxisRad(axis, radians));
    }
    
    public Matrix4 setToRotation(final float axisX, final float axisY, final float axisZ, final float degrees) {
        if (degrees == 0.0f) {
            this.idt();
            return this;
        }
        return this.set(Matrix4.quat.setFromAxis(axisX, axisY, axisZ, degrees));
    }
    
    public Matrix4 setToRotationRad(final float axisX, final float axisY, final float axisZ, final float radians) {
        if (radians == 0.0f) {
            this.idt();
            return this;
        }
        return this.set(Matrix4.quat.setFromAxisRad(axisX, axisY, axisZ, radians));
    }
    
    public Matrix4 setToRotation(final Vector3 v1, final Vector3 v2) {
        return this.set(Matrix4.quat.setFromCross(v1, v2));
    }
    
    public Matrix4 setToRotation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return this.set(Matrix4.quat.setFromCross(x1, y1, z1, x2, y2, z2));
    }
    
    public Matrix4 setFromEulerAngles(final float yaw, final float pitch, final float roll) {
        Matrix4.quat.setEulerAngles(yaw, pitch, roll);
        return this.set(Matrix4.quat);
    }
    
    public Matrix4 setFromEulerAnglesRad(final float yaw, final float pitch, final float roll) {
        Matrix4.quat.setEulerAnglesRad(yaw, pitch, roll);
        return this.set(Matrix4.quat);
    }
    
    public Matrix4 setToScaling(final Vector3 vector) {
        this.idt();
        this.val[0] = vector.x;
        this.val[5] = vector.y;
        this.val[10] = vector.z;
        return this;
    }
    
    public Matrix4 setToScaling(final float x, final float y, final float z) {
        this.idt();
        this.val[0] = x;
        this.val[5] = y;
        this.val[10] = z;
        return this;
    }
    
    public Matrix4 setToLookAt(final Vector3 direction, final Vector3 up) {
        Matrix4.l_vez.set(direction).nor();
        Matrix4.l_vex.set(direction).nor();
        Matrix4.l_vex.crs(up).nor();
        Matrix4.l_vey.set(Matrix4.l_vex).crs(Matrix4.l_vez).nor();
        this.idt();
        this.val[0] = Matrix4.l_vex.x;
        this.val[4] = Matrix4.l_vex.y;
        this.val[8] = Matrix4.l_vex.z;
        this.val[1] = Matrix4.l_vey.x;
        this.val[5] = Matrix4.l_vey.y;
        this.val[9] = Matrix4.l_vey.z;
        this.val[2] = -Matrix4.l_vez.x;
        this.val[6] = -Matrix4.l_vez.y;
        this.val[10] = -Matrix4.l_vez.z;
        return this;
    }
    
    public Matrix4 setToLookAt(final Vector3 position, final Vector3 target, final Vector3 up) {
        Matrix4.tmpVec.set(target).sub(position);
        this.setToLookAt(Matrix4.tmpVec, up);
        this.mul(Matrix4.tmpMat.setToTranslation(-position.x, -position.y, -position.z));
        return this;
    }
    
    public Matrix4 setToWorld(final Vector3 position, final Vector3 forward, final Vector3 up) {
        Matrix4.tmpForward.set(forward).nor();
        Matrix4.right.set(Matrix4.tmpForward).crs(up).nor();
        Matrix4.tmpUp.set(Matrix4.right).crs(Matrix4.tmpForward).nor();
        this.set(Matrix4.right, Matrix4.tmpUp, Matrix4.tmpForward.scl(-1.0f), position);
        return this;
    }
    
    @Override
    public String toString() {
        return "[" + this.val[0] + "|" + this.val[4] + "|" + this.val[8] + "|" + this.val[12] + "]\n" + "[" + this.val[1] + "|" + this.val[5] + "|" + this.val[9] + "|" + this.val[13] + "]\n" + "[" + this.val[2] + "|" + this.val[6] + "|" + this.val[10] + "|" + this.val[14] + "]\n" + "[" + this.val[3] + "|" + this.val[7] + "|" + this.val[11] + "|" + this.val[15] + "]\n";
    }
    
    public Matrix4 lerp(final Matrix4 matrix, final float alpha) {
        for (int i = 0; i < 16; ++i) {
            this.val[i] = this.val[i] * (1.0f - alpha) + matrix.val[i] * alpha;
        }
        return this;
    }
    
    public Matrix4 avg(final Matrix4 other, final float w) {
        this.getScale(Matrix4.tmpVec);
        other.getScale(Matrix4.tmpForward);
        this.getRotation(Matrix4.quat);
        other.getRotation(Matrix4.quat2);
        this.getTranslation(Matrix4.tmpUp);
        other.getTranslation(Matrix4.right);
        this.setToScaling(Matrix4.tmpVec.scl(w).add(Matrix4.tmpForward.scl(1.0f - w)));
        this.rotate(Matrix4.quat.slerp(Matrix4.quat2, 1.0f - w));
        this.setTranslation(Matrix4.tmpUp.scl(w).add(Matrix4.right.scl(1.0f - w)));
        return this;
    }
    
    public Matrix4 avg(final Matrix4[] t) {
        final float w = 1.0f / t.length;
        Matrix4.tmpVec.set(t[0].getScale(Matrix4.tmpUp).scl(w));
        Matrix4.quat.set(t[0].getRotation(Matrix4.quat2).exp(w));
        Matrix4.tmpForward.set(t[0].getTranslation(Matrix4.tmpUp).scl(w));
        for (int i = 1; i < t.length; ++i) {
            Matrix4.tmpVec.add(t[i].getScale(Matrix4.tmpUp).scl(w));
            Matrix4.quat.mul(t[i].getRotation(Matrix4.quat2).exp(w));
            Matrix4.tmpForward.add(t[i].getTranslation(Matrix4.tmpUp).scl(w));
        }
        Matrix4.quat.nor();
        this.setToScaling(Matrix4.tmpVec);
        this.rotate(Matrix4.quat);
        this.setTranslation(Matrix4.tmpForward);
        return this;
    }
    
    public Matrix4 avg(final Matrix4[] t, final float[] w) {
        Matrix4.tmpVec.set(t[0].getScale(Matrix4.tmpUp).scl(w[0]));
        Matrix4.quat.set(t[0].getRotation(Matrix4.quat2).exp(w[0]));
        Matrix4.tmpForward.set(t[0].getTranslation(Matrix4.tmpUp).scl(w[0]));
        for (int i = 1; i < t.length; ++i) {
            Matrix4.tmpVec.add(t[i].getScale(Matrix4.tmpUp).scl(w[i]));
            Matrix4.quat.mul(t[i].getRotation(Matrix4.quat2).exp(w[i]));
            Matrix4.tmpForward.add(t[i].getTranslation(Matrix4.tmpUp).scl(w[i]));
        }
        Matrix4.quat.nor();
        this.setToScaling(Matrix4.tmpVec);
        this.rotate(Matrix4.quat);
        this.setTranslation(Matrix4.tmpForward);
        return this;
    }
    
    public Matrix4 set(final Matrix3 mat) {
        this.val[0] = mat.val[0];
        this.val[1] = mat.val[1];
        this.val[2] = mat.val[2];
        this.val[3] = 0.0f;
        this.val[4] = mat.val[3];
        this.val[5] = mat.val[4];
        this.val[6] = mat.val[5];
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = 1.0f;
        this.val[11] = 0.0f;
        this.val[12] = mat.val[6];
        this.val[13] = mat.val[7];
        this.val[14] = 0.0f;
        this.val[15] = mat.val[8];
        return this;
    }
    
    public Matrix4 set(final Affine2 affine) {
        this.val[0] = affine.m00;
        this.val[1] = affine.m10;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = affine.m01;
        this.val[5] = affine.m11;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = 1.0f;
        this.val[11] = 0.0f;
        this.val[12] = affine.m02;
        this.val[13] = affine.m12;
        this.val[14] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }
    
    public Matrix4 setAsAffine(final Affine2 affine) {
        this.val[0] = affine.m00;
        this.val[1] = affine.m10;
        this.val[4] = affine.m01;
        this.val[5] = affine.m11;
        this.val[12] = affine.m02;
        this.val[13] = affine.m12;
        return this;
    }
    
    public Matrix4 setAsAffine(final Matrix4 mat) {
        this.val[0] = mat.val[0];
        this.val[1] = mat.val[1];
        this.val[4] = mat.val[4];
        this.val[5] = mat.val[5];
        this.val[12] = mat.val[12];
        this.val[13] = mat.val[13];
        return this;
    }
    
    public Matrix4 scl(final Vector3 scale) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= scale.x;
        final float[] val2 = this.val;
        final int n2 = 5;
        val2[n2] *= scale.y;
        final float[] val3 = this.val;
        final int n3 = 10;
        val3[n3] *= scale.z;
        return this;
    }
    
    public Matrix4 scl(final float x, final float y, final float z) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= x;
        final float[] val2 = this.val;
        final int n2 = 5;
        val2[n2] *= y;
        final float[] val3 = this.val;
        final int n3 = 10;
        val3[n3] *= z;
        return this;
    }
    
    public Matrix4 scl(final float scale) {
        final float[] val = this.val;
        final int n = 0;
        val[n] *= scale;
        final float[] val2 = this.val;
        final int n2 = 5;
        val2[n2] *= scale;
        final float[] val3 = this.val;
        final int n3 = 10;
        val3[n3] *= scale;
        return this;
    }
    
    public Vector3 getTranslation(final Vector3 position) {
        position.x = this.val[12];
        position.y = this.val[13];
        position.z = this.val[14];
        return position;
    }
    
    public Quaternion getRotation(final Quaternion rotation, final boolean normalizeAxes) {
        return rotation.setFromMatrix(normalizeAxes, this);
    }
    
    public Quaternion getRotation(final Quaternion rotation) {
        return rotation.setFromMatrix(this);
    }
    
    public float getScaleXSquared() {
        return this.val[0] * this.val[0] + this.val[4] * this.val[4] + this.val[8] * this.val[8];
    }
    
    public float getScaleYSquared() {
        return this.val[1] * this.val[1] + this.val[5] * this.val[5] + this.val[9] * this.val[9];
    }
    
    public float getScaleZSquared() {
        return this.val[2] * this.val[2] + this.val[6] * this.val[6] + this.val[10] * this.val[10];
    }
    
    public float getScaleX() {
        return (MathUtils.isZero(this.val[4]) && MathUtils.isZero(this.val[8])) ? Math.abs(this.val[0]) : ((float)Math.sqrt(this.getScaleXSquared()));
    }
    
    public float getScaleY() {
        return (MathUtils.isZero(this.val[1]) && MathUtils.isZero(this.val[9])) ? Math.abs(this.val[5]) : ((float)Math.sqrt(this.getScaleYSquared()));
    }
    
    public float getScaleZ() {
        return (MathUtils.isZero(this.val[2]) && MathUtils.isZero(this.val[6])) ? Math.abs(this.val[10]) : ((float)Math.sqrt(this.getScaleZSquared()));
    }
    
    public Vector3 getScale(final Vector3 scale) {
        return scale.set(this.getScaleX(), this.getScaleY(), this.getScaleZ());
    }
    
    public Matrix4 toNormalMatrix() {
        this.val[12] = 0.0f;
        this.val[13] = 0.0f;
        this.val[14] = 0.0f;
        return this.inv().tra();
    }
    
    public static native void mul(final float[] p0, final float[] p1);
    
    public static native void mulVec(final float[] p0, final float[] p1);
    
    public static native void mulVec(final float[] p0, final float[] p1, final int p2, final int p3, final int p4);
    
    public static native void prj(final float[] p0, final float[] p1);
    
    public static native void prj(final float[] p0, final float[] p1, final int p2, final int p3, final int p4);
    
    public static native void rot(final float[] p0, final float[] p1);
    
    public static native void rot(final float[] p0, final float[] p1, final int p2, final int p3, final int p4);
    
    public static native boolean inv(final float[] p0);
    
    public static native float det(final float[] p0);
    
    public Matrix4 translate(final Vector3 translation) {
        return this.translate(translation.x, translation.y, translation.z);
    }
    
    public Matrix4 translate(final float x, final float y, final float z) {
        Matrix4.tmp[0] = 1.0f;
        Matrix4.tmp[4] = 0.0f;
        Matrix4.tmp[8] = 0.0f;
        Matrix4.tmp[12] = x;
        Matrix4.tmp[1] = 0.0f;
        Matrix4.tmp[5] = 1.0f;
        Matrix4.tmp[9] = 0.0f;
        Matrix4.tmp[13] = y;
        Matrix4.tmp[2] = 0.0f;
        Matrix4.tmp[6] = 0.0f;
        Matrix4.tmp[10] = 1.0f;
        Matrix4.tmp[14] = z;
        Matrix4.tmp[3] = 0.0f;
        Matrix4.tmp[7] = 0.0f;
        Matrix4.tmp[11] = 0.0f;
        Matrix4.tmp[15] = 1.0f;
        mul(this.val, Matrix4.tmp);
        return this;
    }
    
    public Matrix4 rotate(final Vector3 axis, final float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        Matrix4.quat.set(axis, degrees);
        return this.rotate(Matrix4.quat);
    }
    
    public Matrix4 rotateRad(final Vector3 axis, final float radians) {
        if (radians == 0.0f) {
            return this;
        }
        Matrix4.quat.setFromAxisRad(axis, radians);
        return this.rotate(Matrix4.quat);
    }
    
    public Matrix4 rotate(final float axisX, final float axisY, final float axisZ, final float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        Matrix4.quat.setFromAxis(axisX, axisY, axisZ, degrees);
        return this.rotate(Matrix4.quat);
    }
    
    public Matrix4 rotateRad(final float axisX, final float axisY, final float axisZ, final float radians) {
        if (radians == 0.0f) {
            return this;
        }
        Matrix4.quat.setFromAxisRad(axisX, axisY, axisZ, radians);
        return this.rotate(Matrix4.quat);
    }
    
    public Matrix4 rotate(final Quaternion rotation) {
        rotation.toMatrix(Matrix4.tmp);
        mul(this.val, Matrix4.tmp);
        return this;
    }
    
    public Matrix4 rotate(final Vector3 v1, final Vector3 v2) {
        return this.rotate(Matrix4.quat.setFromCross(v1, v2));
    }
    
    public Matrix4 scale(final float scaleX, final float scaleY, final float scaleZ) {
        Matrix4.tmp[0] = scaleX;
        Matrix4.tmp[4] = 0.0f;
        Matrix4.tmp[8] = 0.0f;
        Matrix4.tmp[12] = 0.0f;
        Matrix4.tmp[1] = 0.0f;
        Matrix4.tmp[5] = scaleY;
        Matrix4.tmp[9] = 0.0f;
        Matrix4.tmp[13] = 0.0f;
        Matrix4.tmp[2] = 0.0f;
        Matrix4.tmp[6] = 0.0f;
        Matrix4.tmp[10] = scaleZ;
        Matrix4.tmp[14] = 0.0f;
        Matrix4.tmp[3] = 0.0f;
        Matrix4.tmp[7] = 0.0f;
        Matrix4.tmp[11] = 0.0f;
        Matrix4.tmp[15] = 1.0f;
        mul(this.val, Matrix4.tmp);
        return this;
    }
    
    public void extract4x3Matrix(final float[] dst) {
        dst[0] = this.val[0];
        dst[1] = this.val[1];
        dst[2] = this.val[2];
        dst[3] = this.val[4];
        dst[4] = this.val[5];
        dst[5] = this.val[6];
        dst[6] = this.val[8];
        dst[7] = this.val[9];
        dst[8] = this.val[10];
        dst[9] = this.val[12];
        dst[10] = this.val[13];
        dst[11] = this.val[14];
    }
    
    public boolean hasRotationOrScaling() {
        return !MathUtils.isEqual(this.val[0], 1.0f) || !MathUtils.isEqual(this.val[5], 1.0f) || !MathUtils.isEqual(this.val[10], 1.0f) || !MathUtils.isZero(this.val[4]) || !MathUtils.isZero(this.val[8]) || !MathUtils.isZero(this.val[1]) || !MathUtils.isZero(this.val[9]) || !MathUtils.isZero(this.val[2]) || !MathUtils.isZero(this.val[6]);
    }
}
