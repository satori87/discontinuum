// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.Serializable;

public class Rectangle implements Serializable, Shape2D
{
    public static final Rectangle tmp;
    public static final Rectangle tmp2;
    private static final long serialVersionUID = 5733252015138115702L;
    public float x;
    public float y;
    public float width;
    public float height;
    
    static {
        tmp = new Rectangle();
        tmp2 = new Rectangle();
    }
    
    public Rectangle() {
    }
    
    public Rectangle(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Rectangle(final Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }
    
    public Rectangle set(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }
    
    public float getX() {
        return this.x;
    }
    
    public Rectangle setX(final float x) {
        this.x = x;
        return this;
    }
    
    public float getY() {
        return this.y;
    }
    
    public Rectangle setY(final float y) {
        this.y = y;
        return this;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public Rectangle setWidth(final float width) {
        this.width = width;
        return this;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public Rectangle setHeight(final float height) {
        this.height = height;
        return this;
    }
    
    public Vector2 getPosition(final Vector2 position) {
        return position.set(this.x, this.y);
    }
    
    public Rectangle setPosition(final Vector2 position) {
        this.x = position.x;
        this.y = position.y;
        return this;
    }
    
    public Rectangle setPosition(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Rectangle setSize(final float width, final float height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    public Rectangle setSize(final float sizeXY) {
        this.width = sizeXY;
        this.height = sizeXY;
        return this;
    }
    
    public Vector2 getSize(final Vector2 size) {
        return size.set(this.width, this.height);
    }
    
    @Override
    public boolean contains(final float x, final float y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }
    
    @Override
    public boolean contains(final Vector2 point) {
        return this.contains(point.x, point.y);
    }
    
    public boolean contains(final Circle circle) {
        return circle.x - circle.radius >= this.x && circle.x + circle.radius <= this.x + this.width && circle.y - circle.radius >= this.y && circle.y + circle.radius <= this.y + this.height;
    }
    
    public boolean contains(final Rectangle rectangle) {
        final float xmin = rectangle.x;
        final float xmax = xmin + rectangle.width;
        final float ymin = rectangle.y;
        final float ymax = ymin + rectangle.height;
        return xmin > this.x && xmin < this.x + this.width && xmax > this.x && xmax < this.x + this.width && ymin > this.y && ymin < this.y + this.height && ymax > this.y && ymax < this.y + this.height;
    }
    
    public boolean overlaps(final Rectangle r) {
        return this.x < r.x + r.width && this.x + this.width > r.x && this.y < r.y + r.height && this.y + this.height > r.y;
    }
    
    public Rectangle set(final Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
        return this;
    }
    
    public Rectangle merge(final Rectangle rect) {
        final float minX = Math.min(this.x, rect.x);
        final float maxX = Math.max(this.x + this.width, rect.x + rect.width);
        this.x = minX;
        this.width = maxX - minX;
        final float minY = Math.min(this.y, rect.y);
        final float maxY = Math.max(this.y + this.height, rect.y + rect.height);
        this.y = minY;
        this.height = maxY - minY;
        return this;
    }
    
    public Rectangle merge(final float x, final float y) {
        final float minX = Math.min(this.x, x);
        final float maxX = Math.max(this.x + this.width, x);
        this.x = minX;
        this.width = maxX - minX;
        final float minY = Math.min(this.y, y);
        final float maxY = Math.max(this.y + this.height, y);
        this.y = minY;
        this.height = maxY - minY;
        return this;
    }
    
    public Rectangle merge(final Vector2 vec) {
        return this.merge(vec.x, vec.y);
    }
    
    public Rectangle merge(final Vector2[] vecs) {
        float minX = this.x;
        float maxX = this.x + this.width;
        float minY = this.y;
        float maxY = this.y + this.height;
        for (int i = 0; i < vecs.length; ++i) {
            final Vector2 v = vecs[i];
            minX = Math.min(minX, v.x);
            maxX = Math.max(maxX, v.x);
            minY = Math.min(minY, v.y);
            maxY = Math.max(maxY, v.y);
        }
        this.x = minX;
        this.width = maxX - minX;
        this.y = minY;
        this.height = maxY - minY;
        return this;
    }
    
    public float getAspectRatio() {
        return (this.height == 0.0f) ? Float.NaN : (this.width / this.height);
    }
    
    public Vector2 getCenter(final Vector2 vector) {
        vector.x = this.x + this.width / 2.0f;
        vector.y = this.y + this.height / 2.0f;
        return vector;
    }
    
    public Rectangle setCenter(final float x, final float y) {
        this.setPosition(x - this.width / 2.0f, y - this.height / 2.0f);
        return this;
    }
    
    public Rectangle setCenter(final Vector2 position) {
        this.setPosition(position.x - this.width / 2.0f, position.y - this.height / 2.0f);
        return this;
    }
    
    public Rectangle fitOutside(final Rectangle rect) {
        final float ratio = this.getAspectRatio();
        if (ratio > rect.getAspectRatio()) {
            this.setSize(rect.height * ratio, rect.height);
        }
        else {
            this.setSize(rect.width, rect.width / ratio);
        }
        this.setPosition(rect.x + rect.width / 2.0f - this.width / 2.0f, rect.y + rect.height / 2.0f - this.height / 2.0f);
        return this;
    }
    
    public Rectangle fitInside(final Rectangle rect) {
        final float ratio = this.getAspectRatio();
        if (ratio < rect.getAspectRatio()) {
            this.setSize(rect.height * ratio, rect.height);
        }
        else {
            this.setSize(rect.width, rect.width / ratio);
        }
        this.setPosition(rect.x + rect.width / 2.0f - this.width / 2.0f, rect.y + rect.height / 2.0f - this.height / 2.0f);
        return this;
    }
    
    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "," + this.width + "," + this.height + "]";
    }
    
    public Rectangle fromString(final String v) {
        final int s0 = v.indexOf(44, 1);
        final int s2 = v.indexOf(44, s0 + 1);
        final int s3 = v.indexOf(44, s2 + 1);
        if (s0 != -1 && s2 != -1 && s3 != -1 && v.charAt(0) == '[' && v.charAt(v.length() - 1) == ']') {
            try {
                final float x = Float.parseFloat(v.substring(1, s0));
                final float y = Float.parseFloat(v.substring(s0 + 1, s2));
                final float width = Float.parseFloat(v.substring(s2 + 1, s3));
                final float height = Float.parseFloat(v.substring(s3 + 1, v.length() - 1));
                return this.set(x, y, width, height);
            }
            catch (NumberFormatException ex) {}
        }
        throw new GdxRuntimeException("Malformed Rectangle: " + v);
    }
    
    public float area() {
        return this.width * this.height;
    }
    
    public float perimeter() {
        return 2.0f * (this.width + this.height);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + NumberUtils.floatToRawIntBits(this.height);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.width);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.x);
        result = 31 * result + NumberUtils.floatToRawIntBits(this.y);
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
        final Rectangle other = (Rectangle)obj;
        return NumberUtils.floatToRawIntBits(this.height) == NumberUtils.floatToRawIntBits(other.height) && NumberUtils.floatToRawIntBits(this.width) == NumberUtils.floatToRawIntBits(other.width) && NumberUtils.floatToRawIntBits(this.x) == NumberUtils.floatToRawIntBits(other.x) && NumberUtils.floatToRawIntBits(this.y) == NumberUtils.floatToRawIntBits(other.y);
    }
}
