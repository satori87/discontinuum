// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Quaternion implements Serializable
{
    private static final long serialVersionUID = -7661875440774897168L;
    private static Quaternion tmp1;
    private static Quaternion tmp2;
    public float x;
    public float y;
    public float z;
    public float w;
    
    static {
        Quaternion.tmp1 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
        Quaternion.tmp2 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Quaternion(final float x, final float y, final float z, final float w) {
        this.set(x, y, z, w);
    }
    
    public Quaternion() {
        this.idt();
    }
    
    public Quaternion(final Quaternion quaternion) {
        this.set(quaternion);
    }
    
    public Quaternion(final Vector3 axis, final float angle) {
        this.set(axis, angle);
    }
    
    public Quaternion set(final float x, final float y, final float z, final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    
    public Quaternion set(final Quaternion quaternion) {
        return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }
    
    public Quaternion set(final Vector3 axis, final float angle) {
        return this.setFromAxis(axis.x, axis.y, axis.z, angle);
    }
    
    public Quaternion cpy() {
        return new Quaternion(this);
    }
    
    public static final float len(final float x, final float y, final float z, final float w) {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }
    
    public float len() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }
    
    @Override
    public String toString() {
        return "[" + this.x + "|" + this.y + "|" + this.z + "|" + this.w + "]";
    }
    
    public Quaternion setEulerAngles(final float yaw, final float pitch, final float roll) {
        return this.setEulerAnglesRad(yaw * 0.017453292f, pitch * 0.017453292f, roll * 0.017453292f);
    }
    
    public Quaternion setEulerAnglesRad(final float yaw, final float pitch, final float roll) {
        final float hr = roll * 0.5f;
        final float shr = (float)Math.sin(hr);
        final float chr = (float)Math.cos(hr);
        final float hp = pitch * 0.5f;
        final float shp = (float)Math.sin(hp);
        final float chp = (float)Math.cos(hp);
        final float hy = yaw * 0.5f;
        final float shy = (float)Math.sin(hy);
        final float chy = (float)Math.cos(hy);
        final float chy_shp = chy * shp;
        final float shy_chp = shy * chp;
        final float chy_chp = chy * chp;
        final float shy_shp = shy * shp;
        this.x = chy_shp * chr + shy_chp * shr;
        this.y = shy_chp * chr - chy_shp * shr;
        this.z = chy_chp * shr - shy_shp * chr;
        this.w = chy_chp * chr + shy_shp * shr;
        return this;
    }
    
    public int getGimbalPole() {
        final float t = this.y * this.x + this.z * this.w;
        return (t > 0.499f) ? 1 : ((t < -0.499f) ? -1 : 0);
    }
    
    public float getRollRad() {
        final int pole = this.getGimbalPole();
        return (pole == 0) ? MathUtils.atan2(2.0f * (this.w * this.z + this.y * this.x), 1.0f - 2.0f * (this.x * this.x + this.z * this.z)) : (pole * 2.0f * MathUtils.atan2(this.y, this.w));
    }
    
    public float getRoll() {
        return this.getRollRad() * 57.295776f;
    }
    
    public float getPitchRad() {
        final int pole = this.getGimbalPole();
        return (pole == 0) ? ((float)Math.asin(MathUtils.clamp(2.0f * (this.w * this.x - this.z * this.y), -1.0f, 1.0f))) : (pole * 3.1415927f * 0.5f);
    }
    
    public float getPitch() {
        return this.getPitchRad() * 57.295776f;
    }
    
    public float getYawRad() {
        return (this.getGimbalPole() == 0) ? MathUtils.atan2(2.0f * (this.y * this.w + this.x * this.z), 1.0f - 2.0f * (this.y * this.y + this.x * this.x)) : 0.0f;
    }
    
    public float getYaw() {
        return this.getYawRad() * 57.295776f;
    }
    
    public static final float len2(final float x, final float y, final float z, final float w) {
        return x * x + y * y + z * z + w * w;
    }
    
    public float len2() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }
    
    public Quaternion nor() {
        float len = this.len2();
        if (len != 0.0f && !MathUtils.isEqual(len, 1.0f)) {
            len = (float)Math.sqrt(len);
            this.w /= len;
            this.x /= len;
            this.y /= len;
            this.z /= len;
        }
        return this;
    }
    
    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    
    public Vector3 transform(final Vector3 v) {
        Quaternion.tmp2.set(this);
        Quaternion.tmp2.conjugate();
        Quaternion.tmp2.mulLeft(Quaternion.tmp1.set(v.x, v.y, v.z, 0.0f)).mulLeft(this);
        v.x = Quaternion.tmp2.x;
        v.y = Quaternion.tmp2.y;
        v.z = Quaternion.tmp2.z;
        return v;
    }
    
    public Quaternion mul(final Quaternion other) {
        final float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
        final float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
        final float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
        final float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }
    
    public Quaternion mul(final float x, final float y, final float z, final float w) {
        final float newX = this.w * x + this.x * w + this.y * z - this.z * y;
        final float newY = this.w * y + this.y * w + this.z * x - this.x * z;
        final float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
        final float newW = this.w * w - this.x * x - this.y * y - this.z * z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }
    
    public Quaternion mulLeft(final Quaternion other) {
        final float newX = other.w * this.x + other.x * this.w + other.y * this.z - other.z * this.y;
        final float newY = other.w * this.y + other.y * this.w + other.z * this.x - other.x * this.z;
        final float newZ = other.w * this.z + other.z * this.w + other.x * this.y - other.y * this.x;
        final float newW = other.w * this.w - other.x * this.x - other.y * this.y - other.z * this.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }
    
    public Quaternion mulLeft(final float x, final float y, final float z, final float w) {
        final float newX = w * this.x + x * this.w + y * this.z - z * this.y;
        final float newY = w * this.y + y * this.w + z * this.x - x * this.z;
        final float newZ = w * this.z + z * this.w + x * this.y - y * this.x;
        final float newW = w * this.w - x * this.x - y * this.y - z * this.z;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
        return this;
    }
    
    public Quaternion add(final Quaternion quaternion) {
        this.x += quaternion.x;
        this.y += quaternion.y;
        this.z += quaternion.z;
        this.w += quaternion.w;
        return this;
    }
    
    public Quaternion add(final float qx, final float qy, final float qz, final float qw) {
        this.x += qx;
        this.y += qy;
        this.z += qz;
        this.w += qw;
        return this;
    }
    
    public void toMatrix(final float[] matrix) {
        final float xx = this.x * this.x;
        final float xy = this.x * this.y;
        final float xz = this.x * this.z;
        final float xw = this.x * this.w;
        final float yy = this.y * this.y;
        final float yz = this.y * this.z;
        final float yw = this.y * this.w;
        final float zz = this.z * this.z;
        final float zw = this.z * this.w;
        matrix[0] = 1.0f - 2.0f * (yy + zz);
        matrix[4] = 2.0f * (xy - zw);
        matrix[8] = 2.0f * (xz + yw);
        matrix[12] = 0.0f;
        matrix[1] = 2.0f * (xy + zw);
        matrix[5] = 1.0f - 2.0f * (xx + zz);
        matrix[9] = 2.0f * (yz - xw);
        matrix[13] = 0.0f;
        matrix[2] = 2.0f * (xz - yw);
        matrix[6] = 2.0f * (yz + xw);
        matrix[10] = 1.0f - 2.0f * (xx + yy);
        matrix[3] = (matrix[14] = 0.0f);
        matrix[11] = (matrix[7] = 0.0f);
        matrix[15] = 1.0f;
    }
    
    public Quaternion idt() {
        return this.set(0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    public boolean isIdentity() {
        return MathUtils.isZero(this.x) && MathUtils.isZero(this.y) && MathUtils.isZero(this.z) && MathUtils.isEqual(this.w, 1.0f);
    }
    
    public boolean isIdentity(final float tolerance) {
        return MathUtils.isZero(this.x, tolerance) && MathUtils.isZero(this.y, tolerance) && MathUtils.isZero(this.z, tolerance) && MathUtils.isEqual(this.w, 1.0f, tolerance);
    }
    
    public Quaternion setFromAxis(final Vector3 axis, final float degrees) {
        return this.setFromAxis(axis.x, axis.y, axis.z, degrees);
    }
    
    public Quaternion setFromAxisRad(final Vector3 axis, final float radians) {
        return this.setFromAxisRad(axis.x, axis.y, axis.z, radians);
    }
    
    public Quaternion setFromAxis(final float x, final float y, final float z, final float degrees) {
        return this.setFromAxisRad(x, y, z, degrees * 0.017453292f);
    }
    
    public Quaternion setFromAxisRad(final float x, final float y, final float z, final float radians) {
        float d = Vector3.len(x, y, z);
        if (d == 0.0f) {
            return this.idt();
        }
        d = 1.0f / d;
        final float l_ang = (radians < 0.0f) ? (6.2831855f - -radians % 6.2831855f) : (radians % 6.2831855f);
        final float l_sin = (float)Math.sin(l_ang / 2.0f);
        final float l_cos = (float)Math.cos(l_ang / 2.0f);
        return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).nor();
    }
    
    public Quaternion setFromMatrix(final boolean normalizeAxes, final Matrix4 matrix) {
        return this.setFromAxes(normalizeAxes, matrix.val[0], matrix.val[4], matrix.val[8], matrix.val[1], matrix.val[5], matrix.val[9], matrix.val[2], matrix.val[6], matrix.val[10]);
    }
    
    public Quaternion setFromMatrix(final Matrix4 matrix) {
        return this.setFromMatrix(false, matrix);
    }
    
    public Quaternion setFromMatrix(final boolean normalizeAxes, final Matrix3 matrix) {
        return this.setFromAxes(normalizeAxes, matrix.val[0], matrix.val[3], matrix.val[6], matrix.val[1], matrix.val[4], matrix.val[7], matrix.val[2], matrix.val[5], matrix.val[8]);
    }
    
    public Quaternion setFromMatrix(final Matrix3 matrix) {
        return this.setFromMatrix(false, matrix);
    }
    
    public Quaternion setFromAxes(final float xx, final float xy, final float xz, final float yx, final float yy, final float yz, final float zx, final float zy, final float zz) {
        return this.setFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
    }
    
    public Quaternion setFromAxes(final boolean normalizeAxes, float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
        if (normalizeAxes) {
            final float lx = 1.0f / Vector3.len(xx, xy, xz);
            final float ly = 1.0f / Vector3.len(yx, yy, yz);
            final float lz = 1.0f / Vector3.len(zx, zy, zz);
            xx *= lx;
            xy *= lx;
            xz *= lx;
            yx *= ly;
            yy *= ly;
            yz *= ly;
            zx *= lz;
            zy *= lz;
            zz *= lz;
        }
        final float t = xx + yy + zz;
        if (t >= 0.0f) {
            float s = (float)Math.sqrt(t + 1.0f);
            this.w = 0.5f * s;
            s = 0.5f / s;
            this.x = (zy - yz) * s;
            this.y = (xz - zx) * s;
            this.z = (yx - xy) * s;
        }
        else if (xx > yy && xx > zz) {
            float s = (float)Math.sqrt(1.0 + xx - yy - zz);
            this.x = s * 0.5f;
            s = 0.5f / s;
            this.y = (yx + xy) * s;
            this.z = (xz + zx) * s;
            this.w = (zy - yz) * s;
        }
        else if (yy > zz) {
            float s = (float)Math.sqrt(1.0 + yy - xx - zz);
            this.y = s * 0.5f;
            s = 0.5f / s;
            this.x = (yx + xy) * s;
            this.z = (zy + yz) * s;
            this.w = (xz - zx) * s;
        }
        else {
            float s = (float)Math.sqrt(1.0 + zz - xx - yy);
            this.z = s * 0.5f;
            s = 0.5f / s;
            this.x = (xz + zx) * s;
            this.y = (zy + yz) * s;
            this.w = (yx - xy) * s;
        }
        return this;
    }
    
    public Quaternion setFromCross(final Vector3 v1, final Vector3 v2) {
        final float dot = MathUtils.clamp(v1.dot(v2), -1.0f, 1.0f);
        final float angle = (float)Math.acos(dot);
        return this.setFromAxisRad(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, angle);
    }
    
    public Quaternion setFromCross(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        final float dot = MathUtils.clamp(Vector3.dot(x1, y1, z1, x2, y2, z2), -1.0f, 1.0f);
        final float angle = (float)Math.acos(dot);
        return this.setFromAxisRad(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2 - y1 * x2, angle);
    }
    
    public Quaternion slerp(final Quaternion end, final float alpha) {
        final float d = this.x * end.x + this.y * end.y + this.z * end.z + this.w * end.w;
        final float absDot = (d < 0.0f) ? (-d) : d;
        float scale0 = 1.0f - alpha;
        float scale2 = alpha;
        if (1.0f - absDot > 0.1) {
            final float angle = (float)Math.acos(absDot);
            final float invSinTheta = 1.0f / (float)Math.sin(angle);
            scale0 = (float)Math.sin((1.0f - alpha) * angle) * invSinTheta;
            scale2 = (float)Math.sin(alpha * angle) * invSinTheta;
        }
        if (d < 0.0f) {
            scale2 = -scale2;
        }
        this.x = scale0 * this.x + scale2 * end.x;
        this.y = scale0 * this.y + scale2 * end.y;
        this.z = scale0 * this.z + scale2 * end.z;
        this.w = scale0 * this.w + scale2 * end.w;
        return this;
    }
    
    public Quaternion slerp(final Quaternion[] q) {
        final float w = 1.0f / q.length;
        this.set(q[0]).exp(w);
        for (int i = 1; i < q.length; ++i) {
            this.mul(Quaternion.tmp1.set(q[i]).exp(w));
        }
        this.nor();
        return this;
    }
    
    public Quaternion slerp(final Quaternion[] q, final float[] w) {
        this.set(q[0]).exp(w[0]);
        for (int i = 1; i < q.length; ++i) {
            this.mul(Quaternion.tmp1.set(q[i]).exp(w[i]));
        }
        this.nor();
        return this;
    }
    
    public Quaternion exp(final float alpha) {
        final float norm = this.len();
        final float normExp = (float)Math.pow(norm, alpha);
        final float theta = (float)Math.acos(this.w / norm);
        float coeff = 0.0f;
        if (Math.abs(theta) < 0.001) {
            coeff = normExp * alpha / norm;
        }
        else {
            coeff = (float)(normExp * Math.sin(alpha * theta) / (norm * Math.sin(theta)));
        }
        this.w = (float)(normExp * Math.cos(alpha * theta));
        this.x *= coeff;
        this.y *= coeff;
        this.z *= coeff;
        this.nor();
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + NumberUtils.floatToRawIntBits(this.w);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.x);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.y);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.z);
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
        if (!(obj instanceof Quaternion)) {
            return false;
        }
        final Quaternion other = (Quaternion)obj;
        return NumberUtils.floatToRawIntBits(this.w) == NumberUtils.floatToRawIntBits(other.w) && NumberUtils.floatToRawIntBits(this.x) == NumberUtils.floatToRawIntBits(other.x) && NumberUtils.floatToRawIntBits(this.y) == NumberUtils.floatToRawIntBits(other.y) && NumberUtils.floatToRawIntBits(this.z) == NumberUtils.floatToRawIntBits(other.z);
    }
    
    public static final float dot(final float x1, final float y1, final float z1, final float w1, final float x2, final float y2, final float z2, final float w2) {
        return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
    }
    
    public float dot(final Quaternion other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }
    
    public float dot(final float x, final float y, final float z, final float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }
    
    public Quaternion mul(final float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }
    
    public float getAxisAngle(final Vector3 axis) {
        return this.getAxisAngleRad(axis) * 57.295776f;
    }
    
    public float getAxisAngleRad(final Vector3 axis) {
        if (this.w > 1.0f) {
            this.nor();
        }
        final float angle = (float)(2.0 * Math.acos(this.w));
        final double s = Math.sqrt(1.0f - this.w * this.w);
        if (s < 9.999999974752427E-7) {
            axis.x = this.x;
            axis.y = this.y;
            axis.z = this.z;
        }
        else {
            axis.x = (float)(this.x / s);
            axis.y = (float)(this.y / s);
            axis.z = (float)(this.z / s);
        }
        return angle;
    }
    
    public float getAngleRad() {
        return (float)(2.0 * Math.acos((this.w > 1.0f) ? (this.w / this.len()) : this.w));
    }
    
    public float getAngle() {
        return this.getAngleRad() * 57.295776f;
    }
    
    public void getSwingTwist(final float axisX, final float axisY, final float axisZ, final Quaternion swing, final Quaternion twist) {
        final float d = Vector3.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
        twist.set(axisX * d, axisY * d, axisZ * d, this.w).nor();
        if (d < 0.0f) {
            twist.mul(-1.0f);
        }
        swing.set(twist).conjugate().mulLeft(this);
    }
    
    public void getSwingTwist(final Vector3 axis, final Quaternion swing, final Quaternion twist) {
        this.getSwingTwist(axis.x, axis.y, axis.z, swing, twist);
    }
    
    public float getAngleAroundRad(final float axisX, final float axisY, final float axisZ) {
        final float d = Vector3.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
        final float l2 = len2(axisX * d, axisY * d, axisZ * d, this.w);
        return MathUtils.isZero(l2) ? 0.0f : ((float)(2.0 * Math.acos(MathUtils.clamp((float)(((d < 0.0f) ? (-this.w) : this.w) / Math.sqrt(l2)), -1.0f, 1.0f))));
    }
    
    public float getAngleAroundRad(final Vector3 axis) {
        return this.getAngleAroundRad(axis.x, axis.y, axis.z);
    }
    
    public float getAngleAround(final float axisX, final float axisY, final float axisZ) {
        return this.getAngleAroundRad(axisX, axisY, axisZ) * 57.295776f;
    }
    
    public float getAngleAround(final Vector3 axis) {
        return this.getAngleAround(axis.x, axis.y, axis.z);
    }
}
