// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Vector2 implements Serializable, Vector<Vector2>
{
    private static final long serialVersionUID = 913902788239530931L;
    public static final Vector2 X;
    public static final Vector2 Y;
    public static final Vector2 Zero;
    public float x;
    public float y;
    
    static {
        X = new Vector2(1.0f, 0.0f);
        Y = new Vector2(0.0f, 1.0f);
        Zero = new Vector2(0.0f, 0.0f);
    }
    
    public Vector2() {
    }
    
    public Vector2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(final Vector2 v) {
        this.set(v);
    }
    
    @Override
    public Vector2 cpy() {
        return new Vector2(this);
    }
    
    public static float len(final float x, final float y) {
        return (float)Math.sqrt(x * x + y * y);
    }
    
    @Override
    public float len() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public static float len2(final float x, final float y) {
        return x * x + y * y;
    }
    
    @Override
    public float len2() {
        return this.x * this.x + this.y * this.y;
    }
    
    @Override
    public Vector2 set(final Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    
    public Vector2 set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    @Override
    public Vector2 sub(final Vector2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    
    public Vector2 sub(final float x, final float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    
    @Override
    public Vector2 nor() {
        final float len = this.len();
        if (len != 0.0f) {
            this.x /= len;
            this.y /= len;
        }
        return this;
    }
    
    @Override
    public Vector2 add(final Vector2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }
    
    public Vector2 add(final float x, final float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public static float dot(final float x1, final float y1, final float x2, final float y2) {
        return x1 * x2 + y1 * y2;
    }
    
    @Override
    public float dot(final Vector2 v) {
        return this.x * v.x + this.y * v.y;
    }
    
    public float dot(final float ox, final float oy) {
        return this.x * ox + this.y * oy;
    }
    
    @Override
    public Vector2 scl(final float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    
    public Vector2 scl(final float x, final float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }
    
    @Override
    public Vector2 scl(final Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }
    
    @Override
    public Vector2 mulAdd(final Vector2 vec, final float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }
    
    @Override
    public Vector2 mulAdd(final Vector2 vec, final Vector2 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        return this;
    }
    
    public static float dst(final float x1, final float y1, final float x2, final float y2) {
        final float x_d = x2 - x1;
        final float y_d = y2 - y1;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }
    
    @Override
    public float dst(final Vector2 v) {
        final float x_d = v.x - this.x;
        final float y_d = v.y - this.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }
    
    public float dst(final float x, final float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }
    
    public static float dst2(final float x1, final float y1, final float x2, final float y2) {
        final float x_d = x2 - x1;
        final float y_d = y2 - y1;
        return x_d * x_d + y_d * y_d;
    }
    
    @Override
    public float dst2(final Vector2 v) {
        final float x_d = v.x - this.x;
        final float y_d = v.y - this.y;
        return x_d * x_d + y_d * y_d;
    }
    
    public float dst2(final float x, final float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return x_d * x_d + y_d * y_d;
    }
    
    @Override
    public Vector2 limit(final float limit) {
        return this.limit2(limit * limit);
    }
    
    @Override
    public Vector2 limit2(final float limit2) {
        final float len2 = this.len2();
        if (len2 > limit2) {
            return this.scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }
    
    @Override
    public Vector2 clamp(final float min, final float max) {
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
    public Vector2 setLength(final float len) {
        return this.setLength2(len * len);
    }
    
    @Override
    public Vector2 setLength2(final float len2) {
        final float oldLen2 = this.len2();
        return (oldLen2 == 0.0f || oldLen2 == len2) ? this : this.scl((float)Math.sqrt(len2 / oldLen2));
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
    
    public Vector2 fromString(final String v) {
        final int s = v.indexOf(44, 1);
        if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                final float x = Float.parseFloat(v.substring(1, s));
                final float y = Float.parseFloat(v.substring(s + 1, v.length() - 1));
                return this.set(x, y);
            }
            catch (NumberFormatException ex) {}
        }
        throw new GdxRuntimeException("Malformed Vector2: " + v);
    }
    
    public Vector2 mul(final Matrix3 mat) {
        final float x = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
        final float y = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
        this.x = x;
        this.y = y;
        return this;
    }
    
    public float crs(final Vector2 v) {
        return this.x * v.y - this.y * v.x;
    }
    
    public float crs(final float x, final float y) {
        return this.x * y - this.y * x;
    }
    
    public float angle() {
        float angle = (float)Math.atan2(this.y, this.x) * 57.295776f;
        if (angle < 0.0f) {
            angle += 360.0f;
        }
        return angle;
    }
    
    public float angle(final Vector2 reference) {
        return (float)Math.atan2(this.crs(reference), this.dot(reference)) * 57.295776f;
    }
    
    public float angleRad() {
        return (float)Math.atan2(this.y, this.x);
    }
    
    public float angleRad(final Vector2 reference) {
        return (float)Math.atan2(this.crs(reference), this.dot(reference));
    }
    
    public Vector2 setAngle(final float degrees) {
        return this.setAngleRad(degrees * 0.017453292f);
    }
    
    public Vector2 setAngleRad(final float radians) {
        this.set(this.len(), 0.0f);
        this.rotateRad(radians);
        return this;
    }
    
    public Vector2 rotate(final float degrees) {
        return this.rotateRad(degrees * 0.017453292f);
    }
    
    public Vector2 rotateAround(final Vector2 reference, final float degrees) {
        return this.sub(reference).rotate(degrees).add(reference);
    }
    
    public Vector2 rotateRad(final float radians) {
        final float cos = (float)Math.cos(radians);
        final float sin = (float)Math.sin(radians);
        final float newX = this.x * cos - this.y * sin;
        final float newY = this.x * sin + this.y * cos;
        this.x = newX;
        this.y = newY;
        return this;
    }
    
    public Vector2 rotateAroundRad(final Vector2 reference, final float radians) {
        return this.sub(reference).rotateRad(radians).add(reference);
    }
    
    public Vector2 rotate90(final int dir) {
        final float x = this.x;
        if (dir >= 0) {
            this.x = -this.y;
            this.y = x;
        }
        else {
            this.x = this.y;
            this.y = -x;
        }
        return this;
    }
    
    @Override
    public Vector2 lerp(final Vector2 target, final float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = this.x * invAlpha + target.x * alpha;
        this.y = this.y * invAlpha + target.y * alpha;
        return this;
    }
    
    @Override
    public Vector2 interpolate(final Vector2 target, final float alpha, final Interpolation interpolation) {
        return this.lerp(target, interpolation.apply(alpha));
    }
    
    @Override
    public Vector2 setToRandomDirection() {
        final float theta = MathUtils.random(0.0f, 6.2831855f);
        return this.set(MathUtils.cos(theta), MathUtils.sin(theta));
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + NumberUtils.floatToIntBits(this.x);
        result = 31 * result + NumberUtils.floatToIntBits(this.y);
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
        final Vector2 other = (Vector2)obj;
        return NumberUtils.floatToIntBits(this.x) == NumberUtils.floatToIntBits(other.x) && NumberUtils.floatToIntBits(this.y) == NumberUtils.floatToIntBits(other.y);
    }
    
    @Override
    public boolean epsilonEquals(final Vector2 other, final float epsilon) {
        return other != null && Math.abs(other.x - this.x) <= epsilon && Math.abs(other.y - this.y) <= epsilon;
    }
    
    public boolean epsilonEquals(final float x, final float y, final float epsilon) {
        return Math.abs(x - this.x) <= epsilon && Math.abs(y - this.y) <= epsilon;
    }
    
    public boolean epsilonEquals(final Vector2 other) {
        return this.epsilonEquals(other, 1.0E-6f);
    }
    
    public boolean epsilonEquals(final float x, final float y) {
        return this.epsilonEquals(x, y, 1.0E-6f);
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
        return this.x == 0.0f && this.y == 0.0f;
    }
    
    @Override
    public boolean isZero(final float margin) {
        return this.len2() < margin;
    }
    
    @Override
    public boolean isOnLine(final Vector2 other) {
        return MathUtils.isZero(this.x * other.y - this.y * other.x);
    }
    
    @Override
    public boolean isOnLine(final Vector2 other, final float epsilon) {
        return MathUtils.isZero(this.x * other.y - this.y * other.x, epsilon);
    }
    
    @Override
    public boolean isCollinear(final Vector2 other, final float epsilon) {
        return this.isOnLine(other, epsilon) && this.dot(other) > 0.0f;
    }
    
    @Override
    public boolean isCollinear(final Vector2 other) {
        return this.isOnLine(other) && this.dot(other) > 0.0f;
    }
    
    @Override
    public boolean isCollinearOpposite(final Vector2 other, final float epsilon) {
        return this.isOnLine(other, epsilon) && this.dot(other) < 0.0f;
    }
    
    @Override
    public boolean isCollinearOpposite(final Vector2 other) {
        return this.isOnLine(other) && this.dot(other) < 0.0f;
    }
    
    @Override
    public boolean isPerpendicular(final Vector2 vector) {
        return MathUtils.isZero(this.dot(vector));
    }
    
    @Override
    public boolean isPerpendicular(final Vector2 vector, final float epsilon) {
        return MathUtils.isZero(this.dot(vector), epsilon);
    }
    
    @Override
    public boolean hasSameDirection(final Vector2 vector) {
        return this.dot(vector) > 0.0f;
    }
    
    @Override
    public boolean hasOppositeDirection(final Vector2 vector) {
        return this.dot(vector) < 0.0f;
    }
    
    @Override
    public Vector2 setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        return this;
    }
}
