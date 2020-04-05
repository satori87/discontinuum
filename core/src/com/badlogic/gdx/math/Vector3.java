// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Vector3 implements Serializable, Vector<Vector3>
{
    private static final long serialVersionUID = 3840054589595372522L;
    public float x;
    public float y;
    public float z;
    public static final Vector3 X;
    public static final Vector3 Y;
    public static final Vector3 Z;
    public static final Vector3 Zero;
    private static final Matrix4 tmpMat;
    
    static {
        X = new Vector3(1.0f, 0.0f, 0.0f);
        Y = new Vector3(0.0f, 1.0f, 0.0f);
        Z = new Vector3(0.0f, 0.0f, 1.0f);
        Zero = new Vector3(0.0f, 0.0f, 0.0f);
        tmpMat = new Matrix4();
    }
    
    public Vector3() {
    }
    
    public Vector3(final float x, final float y, final float z) {
        this.set(x, y, z);
    }
    
    public Vector3(final Vector3 vector) {
        this.set(vector);
    }
    
    public Vector3(final float[] values) {
        this.set(values[0], values[1], values[2]);
    }
    
    public Vector3(final Vector2 vector, final float z) {
        this.set(vector.x, vector.y, z);
    }
    
    public Vector3 set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    @Override
    public Vector3 set(final Vector3 vector) {
        return this.set(vector.x, vector.y, vector.z);
    }
    
    public Vector3 set(final float[] values) {
        return this.set(values[0], values[1], values[2]);
    }
    
    public Vector3 set(final Vector2 vector, final float z) {
        return this.set(vector.x, vector.y, z);
    }
    
    public Vector3 setFromSpherical(final float azimuthalAngle, final float polarAngle) {
        final float cosPolar = MathUtils.cos(polarAngle);
        final float sinPolar = MathUtils.sin(polarAngle);
        final float cosAzim = MathUtils.cos(azimuthalAngle);
        final float sinAzim = MathUtils.sin(azimuthalAngle);
        return this.set(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
    }
    
    @Override
    public Vector3 setToRandomDirection() {
        final float u = MathUtils.random();
        final float v = MathUtils.random();
        final float theta = 6.2831855f * u;
        final float phi = (float)Math.acos(2.0f * v - 1.0f);
        return this.setFromSpherical(theta, phi);
    }
    
    @Override
    public Vector3 cpy() {
        return new Vector3(this);
    }
    
    @Override
    public Vector3 add(final Vector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }
    
    public Vector3 add(final float x, final float y, final float z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }
    
    public Vector3 add(final float values) {
        return this.set(this.x + values, this.y + values, this.z + values);
    }
    
    @Override
    public Vector3 sub(final Vector3 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }
    
    public Vector3 sub(final float x, final float y, final float z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }
    
    public Vector3 sub(final float value) {
        return this.set(this.x - value, this.y - value, this.z - value);
    }
    
    @Override
    public Vector3 scl(final float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
    @Override
    public Vector3 scl(final Vector3 other) {
        return this.set(this.x * other.x, this.y * other.y, this.z * other.z);
    }
    
    public Vector3 scl(final float vx, final float vy, final float vz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz);
    }
    
    @Override
    public Vector3 mulAdd(final Vector3 vec, final float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        return this;
    }
    
    @Override
    public Vector3 mulAdd(final Vector3 vec, final Vector3 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        return this;
    }
    
    public static float len(final float x, final float y, final float z) {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }
    
    @Override
    public float len() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public static float len2(final float x, final float y, final float z) {
        return x * x + y * y + z * z;
    }
    
    @Override
    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    public boolean idt(final Vector3 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }
    
    public static float dst(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final float a = x2 - x1;
        final float b = y2 - y1;
        final float c = z2 - z1;
        return (float)Math.sqrt(a * a + b * b + c * c);
    }
    
    @Override
    public float dst(final Vector3 vector) {
        final float a = vector.x - this.x;
        final float b = vector.y - this.y;
        final float c = vector.z - this.z;
        return (float)Math.sqrt(a * a + b * b + c * c);
    }
    
    public float dst(final float x, final float y, final float z) {
        final float a = x - this.x;
        final float b = y - this.y;
        final float c = z - this.z;
        return (float)Math.sqrt(a * a + b * b + c * c);
    }
    
    public static float dst2(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final float a = x2 - x1;
        final float b = y2 - y1;
        final float c = z2 - z1;
        return a * a + b * b + c * c;
    }
    
    @Override
    public float dst2(final Vector3 point) {
        final float a = point.x - this.x;
        final float b = point.y - this.y;
        final float c = point.z - this.z;
        return a * a + b * b + c * c;
    }
    
    public float dst2(final float x, final float y, final float z) {
        final float a = x - this.x;
        final float b = y - this.y;
        final float c = z - this.z;
        return a * a + b * b + c * c;
    }
    
    @Override
    public Vector3 nor() {
        final float len2 = this.len2();
        if (len2 == 0.0f || len2 == 1.0f) {
            return this;
        }
        return this.scl(1.0f / (float)Math.sqrt(len2));
    }
    
    public static float dot(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }
    
    @Override
    public float dot(final Vector3 vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }
    
    public float dot(final float x, final float y, final float z) {
        return this.x * x + this.y * y + this.z * z;
    }
    
    public Vector3 crs(final Vector3 vector) {
        return this.set(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z, this.x * vector.y - this.y * vector.x);
    }
    
    public Vector3 crs(final float x, final float y, final float z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }
    
    public Vector3 mul4x3(final float[] matrix) {
        return this.set(this.x * matrix[0] + this.y * matrix[3] + this.z * matrix[6] + matrix[9], this.x * matrix[1] + this.y * matrix[4] + this.z * matrix[7] + matrix[10], this.x * matrix[2] + this.y * matrix[5] + this.z * matrix[8] + matrix[11]);
    }
    
    public Vector3 mul(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]);
    }
    
    public Vector3 traMul(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2] + l_mat[3], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6] + l_mat[7], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10] + l_mat[11]);
    }
    
    public Vector3 mul(final Matrix3 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[3] + this.z * l_mat[6], this.x * l_mat[1] + this.y * l_mat[4] + this.z * l_mat[7], this.x * l_mat[2] + this.y * l_mat[5] + this.z * l_mat[8]);
    }
    
    public Vector3 traMul(final Matrix3 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[3] + this.y * l_mat[4] + this.z * l_mat[5], this.x * l_mat[6] + this.y * l_mat[7] + this.z * l_mat[8]);
    }
    
    public Vector3 mul(final Quaternion quat) {
        return quat.transform(this);
    }
    
    public Vector3 prj(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        final float l_w = 1.0f / (this.x * l_mat[3] + this.y * l_mat[7] + this.z * l_mat[11] + l_mat[15]);
        return this.set((this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12]) * l_w, (this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13]) * l_w, (this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]) * l_w);
    }
    
    public Vector3 rot(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10]);
    }
    
    public Vector3 unrotate(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }
    
    public Vector3 untransform(final Matrix4 matrix) {
        final float[] l_mat = matrix.val;
        this.x -= l_mat[12];
        this.y -= l_mat[12];
        this.z -= l_mat[12];
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }
    
    public Vector3 rotate(final float degrees, final float axisX, final float axisY, final float axisZ) {
        return this.mul(Vector3.tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
    }
    
    public Vector3 rotateRad(final float radians, final float axisX, final float axisY, final float axisZ) {
        return this.mul(Vector3.tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
    }
    
    public Vector3 rotate(final Vector3 axis, final float degrees) {
        Vector3.tmpMat.setToRotation(axis, degrees);
        return this.mul(Vector3.tmpMat);
    }
    
    public Vector3 rotateRad(final Vector3 axis, final float radians) {
        Vector3.tmpMat.setToRotationRad(axis, radians);
        return this.mul(Vector3.tmpMat);
    }
    
    @Override
    public boolean isUnit() {
        return this.isUnit(1.0E-9f);
    }
    
    @Override
    public boolean isUnit(final float margin) {
        return Math.abs(this.len2() - 1.0f) < margin;
    }
    
    @Override
    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }
    
    @Override
    public boolean isZero(final float margin) {
        return this.len2() < margin;
    }
    
    @Override
    public boolean isOnLine(final Vector3 other, final float epsilon) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= epsilon;
    }
    
    @Override
    public boolean isOnLine(final Vector3 other) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= 1.0E-6f;
    }
    
    @Override
    public boolean isCollinear(final Vector3 other, final float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasSameDirection(other);
    }
    
    @Override
    public boolean isCollinear(final Vector3 other) {
        return this.isOnLine(other) && this.hasSameDirection(other);
    }
    
    @Override
    public boolean isCollinearOpposite(final Vector3 other, final float epsilon) {
        return this.isOnLine(other, epsilon) && this.hasOppositeDirection(other);
    }
    
    @Override
    public boolean isCollinearOpposite(final Vector3 other) {
        return this.isOnLine(other) && this.hasOppositeDirection(other);
    }
    
    @Override
    public boolean isPerpendicular(final Vector3 vector) {
        return MathUtils.isZero(this.dot(vector));
    }
    
    @Override
    public boolean isPerpendicular(final Vector3 vector, final float epsilon) {
        return MathUtils.isZero(this.dot(vector), epsilon);
    }
    
    @Override
    public boolean hasSameDirection(final Vector3 vector) {
        return this.dot(vector) > 0.0f;
    }
    
    @Override
    public boolean hasOppositeDirection(final Vector3 vector) {
        return this.dot(vector) < 0.0f;
    }
    
    @Override
    public Vector3 lerp(final Vector3 target, final float alpha) {
        this.x += alpha * (target.x - this.x);
        this.y += alpha * (target.y - this.y);
        this.z += alpha * (target.z - this.z);
        return this;
    }
    
    @Override
    public Vector3 interpolate(final Vector3 target, final float alpha, final Interpolation interpolator) {
        return this.lerp(target, interpolator.apply(0.0f, 1.0f, alpha));
    }
    
    public Vector3 slerp(final Vector3 target, final float alpha) {
        final float dot = this.dot(target);
        if (dot > 0.9995 || dot < -0.9995) {
            return this.lerp(target, alpha);
        }
        final float theta0 = (float)Math.acos(dot);
        final float theta2 = theta0 * alpha;
        final float st = (float)Math.sin(theta2);
        final float tx = target.x - this.x * dot;
        final float ty = target.y - this.y * dot;
        final float tz = target.z - this.z * dot;
        final float l2 = tx * tx + ty * ty + tz * tz;
        final float dl = st * ((l2 < 1.0E-4f) ? 1.0f : (1.0f / (float)Math.sqrt(l2)));
        return this.scl((float)Math.cos(theta2)).add(tx * dl, ty * dl, tz * dl).nor();
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }
    
    public Vector3 fromString(final String v) {
        final int s0 = v.indexOf(44, 1);
        final int s2 = v.indexOf(44, s0 + 1);
        if (s0 != -1 && s2 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                final float x = Float.parseFloat(v.substring(1, s0));
                final float y = Float.parseFloat(v.substring(s0 + 1, s2));
                final float z = Float.parseFloat(v.substring(s2 + 1, v.length() - 1));
                return this.set(x, y, z);
            }
            catch (NumberFormatException ex) {}
        }
        throw new GdxRuntimeException("Malformed Vector3: " + v);
    }
    
    @Override
    public Vector3 limit(final float limit) {
        return this.limit2(limit * limit);
    }
    
    @Override
    public Vector3 limit2(final float limit2) {
        final float len2 = this.len2();
        if (len2 > limit2) {
            this.scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }
    
    @Override
    public Vector3 setLength(final float len) {
        return this.setLength2(len * len);
    }
    
    @Override
    public Vector3 setLength2(final float len2) {
        final float oldLen2 = this.len2();
        return (oldLen2 == 0.0f || oldLen2 == len2) ? this : this.scl((float)Math.sqrt(len2 / oldLen2));
    }
    
    @Override
    public Vector3 clamp(final float min, final float max) {
        final float len2 = this.len2();
        if (len2 == 0.0f) {
            return this;
        }
        final float max2 = max * max;
        if (len2 > max2) {
            return this.scl((float)Math.sqrt(max2 / len2));
        }
        final float min2 = min * min;
        if (len2 < min2) {
            return this.scl((float)Math.sqrt(min2 / len2));
        }
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + NumberUtils.floatToIntBits(this.x);
        result = 31 * result + NumberUtils.floatToIntBits(this.y);
        result = 31 * result + NumberUtils.floatToIntBits(this.z);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Vector3 other = (Vector3)obj;
        return NumberUtils.floatToIntBits(this.x) == NumberUtils.floatToIntBits(other.x) && NumberUtils.floatToIntBits(this.y) == NumberUtils.floatToIntBits(other.y) && NumberUtils.floatToIntBits(this.z) == NumberUtils.floatToIntBits(other.z);
    }
    
    @Override
    public boolean epsilonEquals(final Vector3 other, final float epsilon) {
        return other != null && Math.abs(other.x - this.x) <= epsilon && Math.abs(other.y - this.y) <= epsilon && Math.abs(other.z - this.z) <= epsilon;
    }
    
    public boolean epsilonEquals(final float x, final float y, final float z, final float epsilon) {
        return Math.abs(x - this.x) <= epsilon && Math.abs(y - this.y) <= epsilon && Math.abs(z - this.z) <= epsilon;
    }
    
    public boolean epsilonEquals(final Vector3 other) {
        return this.epsilonEquals(other, 1.0E-6f);
    }
    
    public boolean epsilonEquals(final float x, final float y, final float z) {
        return this.epsilonEquals(x, y, z, 1.0E-6f);
    }
    
    @Override
    public Vector3 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        return this;
    }
}
