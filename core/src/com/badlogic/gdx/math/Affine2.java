// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public final class Affine2 implements Serializable
{
    private static final long serialVersionUID = 1524569123485049187L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    
    public Affine2() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
    }
    
    public Affine2(final Affine2 other) {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.set(other);
    }
    
    public Affine2 idt() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 set(final Affine2 other) {
        this.m00 = other.m00;
        this.m01 = other.m01;
        this.m02 = other.m02;
        this.m10 = other.m10;
        this.m11 = other.m11;
        this.m12 = other.m12;
        return this;
    }
    
    public Affine2 set(final Matrix3 matrix) {
        final float[] other = matrix.val;
        this.m00 = other[0];
        this.m01 = other[3];
        this.m02 = other[6];
        this.m10 = other[1];
        this.m11 = other[4];
        this.m12 = other[7];
        return this;
    }
    
    public Affine2 set(final Matrix4 matrix) {
        final float[] other = matrix.val;
        this.m00 = other[0];
        this.m01 = other[4];
        this.m02 = other[12];
        this.m10 = other[1];
        this.m11 = other[5];
        this.m12 = other[13];
        return this;
    }
    
    public Affine2 setToTranslation(final float x, final float y) {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = x;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = y;
        return this;
    }
    
    public Affine2 setToTranslation(final Vector2 trn) {
        return this.setToTranslation(trn.x, trn.y);
    }
    
    public Affine2 setToScaling(final float scaleX, final float scaleY) {
        this.m00 = scaleX;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = scaleY;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 setToScaling(final Vector2 scale) {
        return this.setToScaling(scale.x, scale.y);
    }
    
    public Affine2 setToRotation(final float degrees) {
        final float cos = MathUtils.cosDeg(degrees);
        final float sin = MathUtils.sinDeg(degrees);
        this.m00 = cos;
        this.m01 = -sin;
        this.m02 = 0.0f;
        this.m10 = sin;
        this.m11 = cos;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 setToRotationRad(final float radians) {
        final float cos = MathUtils.cos(radians);
        final float sin = MathUtils.sin(radians);
        this.m00 = cos;
        this.m01 = -sin;
        this.m02 = 0.0f;
        this.m10 = sin;
        this.m11 = cos;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 setToRotation(final float cos, final float sin) {
        this.m00 = cos;
        this.m01 = -sin;
        this.m02 = 0.0f;
        this.m10 = sin;
        this.m11 = cos;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 setToShearing(final float shearX, final float shearY) {
        this.m00 = 1.0f;
        this.m01 = shearX;
        this.m02 = 0.0f;
        this.m10 = shearY;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        return this;
    }
    
    public Affine2 setToShearing(final Vector2 shear) {
        return this.setToShearing(shear.x, shear.y);
    }
    
    public Affine2 setToTrnRotScl(final float x, final float y, final float degrees, final float scaleX, final float scaleY) {
        this.m02 = x;
        this.m12 = y;
        if (degrees == 0.0f) {
            this.m00 = scaleX;
            this.m01 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = scaleY;
        }
        else {
            final float sin = MathUtils.sinDeg(degrees);
            final float cos = MathUtils.cosDeg(degrees);
            this.m00 = cos * scaleX;
            this.m01 = -sin * scaleY;
            this.m10 = sin * scaleX;
            this.m11 = cos * scaleY;
        }
        return this;
    }
    
    public Affine2 setToTrnRotScl(final Vector2 trn, final float degrees, final Vector2 scale) {
        return this.setToTrnRotScl(trn.x, trn.y, degrees, scale.x, scale.y);
    }
    
    public Affine2 setToTrnRotRadScl(final float x, final float y, final float radians, final float scaleX, final float scaleY) {
        this.m02 = x;
        this.m12 = y;
        if (radians == 0.0f) {
            this.m00 = scaleX;
            this.m01 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = scaleY;
        }
        else {
            final float sin = MathUtils.sin(radians);
            final float cos = MathUtils.cos(radians);
            this.m00 = cos * scaleX;
            this.m01 = -sin * scaleY;
            this.m10 = sin * scaleX;
            this.m11 = cos * scaleY;
        }
        return this;
    }
    
    public Affine2 setToTrnRotRadScl(final Vector2 trn, final float radians, final Vector2 scale) {
        return this.setToTrnRotRadScl(trn.x, trn.y, radians, scale.x, scale.y);
    }
    
    public Affine2 setToTrnScl(final float x, final float y, final float scaleX, final float scaleY) {
        this.m00 = scaleX;
        this.m01 = 0.0f;
        this.m02 = x;
        this.m10 = 0.0f;
        this.m11 = scaleY;
        this.m12 = y;
        return this;
    }
    
    public Affine2 setToTrnScl(final Vector2 trn, final Vector2 scale) {
        return this.setToTrnScl(trn.x, trn.y, scale.x, scale.y);
    }
    
    public Affine2 setToProduct(final Affine2 l, final Affine2 r) {
        this.m00 = l.m00 * r.m00 + l.m01 * r.m10;
        this.m01 = l.m00 * r.m01 + l.m01 * r.m11;
        this.m02 = l.m00 * r.m02 + l.m01 * r.m12 + l.m02;
        this.m10 = l.m10 * r.m00 + l.m11 * r.m10;
        this.m11 = l.m10 * r.m01 + l.m11 * r.m11;
        this.m12 = l.m10 * r.m02 + l.m11 * r.m12 + l.m12;
        return this;
    }
    
    public Affine2 inv() {
        final float det = this.det();
        if (det == 0.0f) {
            throw new GdxRuntimeException("Can't invert a singular affine matrix");
        }
        final float invDet = 1.0f / det;
        final float tmp00 = this.m11;
        final float tmp2 = -this.m01;
        final float tmp3 = this.m01 * this.m12 - this.m11 * this.m02;
        final float tmp4 = -this.m10;
        final float tmp5 = this.m00;
        final float tmp6 = this.m10 * this.m02 - this.m00 * this.m12;
        this.m00 = invDet * tmp00;
        this.m01 = invDet * tmp2;
        this.m02 = invDet * tmp3;
        this.m10 = invDet * tmp4;
        this.m11 = invDet * tmp5;
        this.m12 = invDet * tmp6;
        return this;
    }
    
    public Affine2 mul(final Affine2 other) {
        final float tmp00 = this.m00 * other.m00 + this.m01 * other.m10;
        final float tmp2 = this.m00 * other.m01 + this.m01 * other.m11;
        final float tmp3 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02;
        final float tmp4 = this.m10 * other.m00 + this.m11 * other.m10;
        final float tmp5 = this.m10 * other.m01 + this.m11 * other.m11;
        final float tmp6 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m02 = tmp3;
        this.m10 = tmp4;
        this.m11 = tmp5;
        this.m12 = tmp6;
        return this;
    }
    
    public Affine2 preMul(final Affine2 other) {
        final float tmp00 = other.m00 * this.m00 + other.m01 * this.m10;
        final float tmp2 = other.m00 * this.m01 + other.m01 * this.m11;
        final float tmp3 = other.m00 * this.m02 + other.m01 * this.m12 + other.m02;
        final float tmp4 = other.m10 * this.m00 + other.m11 * this.m10;
        final float tmp5 = other.m10 * this.m01 + other.m11 * this.m11;
        final float tmp6 = other.m10 * this.m02 + other.m11 * this.m12 + other.m12;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m02 = tmp3;
        this.m10 = tmp4;
        this.m11 = tmp5;
        this.m12 = tmp6;
        return this;
    }
    
    public Affine2 translate(final float x, final float y) {
        this.m02 += this.m00 * x + this.m01 * y;
        this.m12 += this.m10 * x + this.m11 * y;
        return this;
    }
    
    public Affine2 translate(final Vector2 trn) {
        return this.translate(trn.x, trn.y);
    }
    
    public Affine2 preTranslate(final float x, final float y) {
        this.m02 += x;
        this.m12 += y;
        return this;
    }
    
    public Affine2 preTranslate(final Vector2 trn) {
        return this.preTranslate(trn.x, trn.y);
    }
    
    public Affine2 scale(final float scaleX, final float scaleY) {
        this.m00 *= scaleX;
        this.m01 *= scaleY;
        this.m10 *= scaleX;
        this.m11 *= scaleY;
        return this;
    }
    
    public Affine2 scale(final Vector2 scale) {
        return this.scale(scale.x, scale.y);
    }
    
    public Affine2 preScale(final float scaleX, final float scaleY) {
        this.m00 *= scaleX;
        this.m01 *= scaleX;
        this.m02 *= scaleX;
        this.m10 *= scaleY;
        this.m11 *= scaleY;
        this.m12 *= scaleY;
        return this;
    }
    
    public Affine2 preScale(final Vector2 scale) {
        return this.preScale(scale.x, scale.y);
    }
    
    public Affine2 rotate(final float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        final float cos = MathUtils.cosDeg(degrees);
        final float sin = MathUtils.sinDeg(degrees);
        final float tmp00 = this.m00 * cos + this.m01 * sin;
        final float tmp2 = this.m00 * -sin + this.m01 * cos;
        final float tmp3 = this.m10 * cos + this.m11 * sin;
        final float tmp4 = this.m10 * -sin + this.m11 * cos;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m10 = tmp3;
        this.m11 = tmp4;
        return this;
    }
    
    public Affine2 rotateRad(final float radians) {
        if (radians == 0.0f) {
            return this;
        }
        final float cos = MathUtils.cos(radians);
        final float sin = MathUtils.sin(radians);
        final float tmp00 = this.m00 * cos + this.m01 * sin;
        final float tmp2 = this.m00 * -sin + this.m01 * cos;
        final float tmp3 = this.m10 * cos + this.m11 * sin;
        final float tmp4 = this.m10 * -sin + this.m11 * cos;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m10 = tmp3;
        this.m11 = tmp4;
        return this;
    }
    
    public Affine2 preRotate(final float degrees) {
        if (degrees == 0.0f) {
            return this;
        }
        final float cos = MathUtils.cosDeg(degrees);
        final float sin = MathUtils.sinDeg(degrees);
        final float tmp00 = cos * this.m00 - sin * this.m10;
        final float tmp2 = cos * this.m01 - sin * this.m11;
        final float tmp3 = cos * this.m02 - sin * this.m12;
        final float tmp4 = sin * this.m00 + cos * this.m10;
        final float tmp5 = sin * this.m01 + cos * this.m11;
        final float tmp6 = sin * this.m02 + cos * this.m12;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m02 = tmp3;
        this.m10 = tmp4;
        this.m11 = tmp5;
        this.m12 = tmp6;
        return this;
    }
    
    public Affine2 preRotateRad(final float radians) {
        if (radians == 0.0f) {
            return this;
        }
        final float cos = MathUtils.cos(radians);
        final float sin = MathUtils.sin(radians);
        final float tmp00 = cos * this.m00 - sin * this.m10;
        final float tmp2 = cos * this.m01 - sin * this.m11;
        final float tmp3 = cos * this.m02 - sin * this.m12;
        final float tmp4 = sin * this.m00 + cos * this.m10;
        final float tmp5 = sin * this.m01 + cos * this.m11;
        final float tmp6 = sin * this.m02 + cos * this.m12;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m02 = tmp3;
        this.m10 = tmp4;
        this.m11 = tmp5;
        this.m12 = tmp6;
        return this;
    }
    
    public Affine2 shear(final float shearX, final float shearY) {
        float tmp0 = this.m00 + shearY * this.m01;
        float tmp2 = this.m01 + shearX * this.m00;
        this.m00 = tmp0;
        this.m01 = tmp2;
        tmp0 = this.m10 + shearY * this.m11;
        tmp2 = this.m11 + shearX * this.m10;
        this.m10 = tmp0;
        this.m11 = tmp2;
        return this;
    }
    
    public Affine2 shear(final Vector2 shear) {
        return this.shear(shear.x, shear.y);
    }
    
    public Affine2 preShear(final float shearX, final float shearY) {
        final float tmp00 = this.m00 + shearX * this.m10;
        final float tmp2 = this.m01 + shearX * this.m11;
        final float tmp3 = this.m02 + shearX * this.m12;
        final float tmp4 = this.m10 + shearY * this.m00;
        final float tmp5 = this.m11 + shearY * this.m01;
        final float tmp6 = this.m12 + shearY * this.m02;
        this.m00 = tmp00;
        this.m01 = tmp2;
        this.m02 = tmp3;
        this.m10 = tmp4;
        this.m11 = tmp5;
        this.m12 = tmp6;
        return this;
    }
    
    public Affine2 preShear(final Vector2 shear) {
        return this.preShear(shear.x, shear.y);
    }
    
    public float det() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }
    
    public Vector2 getTranslation(final Vector2 position) {
        position.x = this.m02;
        position.y = this.m12;
        return position;
    }
    
    public boolean isTranslation() {
        return this.m00 == 1.0f && this.m11 == 1.0f && this.m01 == 0.0f && this.m10 == 0.0f;
    }
    
    public boolean isIdt() {
        return this.m00 == 1.0f && this.m02 == 0.0f && this.m12 == 0.0f && this.m11 == 1.0f && this.m01 == 0.0f && this.m10 == 0.0f;
    }
    
    public void applyTo(final Vector2 point) {
        final float x = point.x;
        final float y = point.y;
        point.x = this.m00 * x + this.m01 * y + this.m02;
        point.y = this.m10 * x + this.m11 * y + this.m12;
    }
    
    @Override
    public String toString() {
        return "[" + this.m00 + "|" + this.m01 + "|" + this.m02 + "]\n[" + this.m10 + "|" + this.m11 + "|" + this.m12 + "]\n[0.0|0.0|0.1]";
    }
}
