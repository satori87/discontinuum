// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;

public class Sprite extends TextureRegion
{
    static final int VERTEX_SIZE = 9;
    static final int SPRITE_SIZE = 36;
    final float[] vertices;
    private final Color color;
    private float x;
    private float y;
    float width;
    float height;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    private boolean dirty;
    private Rectangle bounds;
    
    public Sprite() {
        this.vertices = new float[36];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public Sprite(final Texture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }
    
    public Sprite(final Texture texture, final int srcWidth, final int srcHeight) {
        this(texture, 0, 0, srcWidth, srcHeight);
    }
    
    public Sprite(final Texture texture, final int srcX, final int srcY, final int srcWidth, final int srcHeight) {
        this.vertices = new float[36];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        if (texture == null) {
            throw new IllegalArgumentException("texture cannot be null.");
        }
        this.texture = texture;
        this.setRegion(srcX, srcY, srcWidth, srcHeight);
        this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.setSize((float)Math.abs(srcWidth), (float)Math.abs(srcHeight));
        this.setOrigin(this.width / 2.0f, this.height / 2.0f);
    }
    
    public Sprite(final TextureRegion region) {
        this.vertices = new float[36];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.setRegion(region);
        this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.setSize((float)region.getRegionWidth(), (float)region.getRegionHeight());
        this.setOrigin(this.width / 2.0f, this.height / 2.0f);
    }
    
    public Sprite(final TextureRegion region, final int srcX, final int srcY, final int srcWidth, final int srcHeight) {
        this.vertices = new float[36];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.setRegion(region, srcX, srcY, srcWidth, srcHeight);
        this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.setSize((float)Math.abs(srcWidth), (float)Math.abs(srcHeight));
        this.setOrigin(this.width / 2.0f, this.height / 2.0f);
    }
    
    public Sprite(final Sprite sprite) {
        this.vertices = new float[36];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        this.set(sprite);
    }
    
    public void set(final Sprite sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException("sprite cannot be null.");
        }
        System.arraycopy(sprite.vertices, 0, this.vertices, 0, 36);
        this.texture = sprite.texture;
        this.u = sprite.u;
        this.v = sprite.v;
        this.u2 = sprite.u2;
        this.v2 = sprite.v2;
        this.x = sprite.x;
        this.y = sprite.y;
        this.width = sprite.width;
        this.height = sprite.height;
        this.regionWidth = sprite.regionWidth;
        this.regionHeight = sprite.regionHeight;
        this.originX = sprite.originX;
        this.originY = sprite.originY;
        this.rotation = sprite.rotation;
        this.scaleX = sprite.scaleX;
        this.scaleY = sprite.scaleY;
        this.color.set(sprite.color);
        this.dirty = sprite.dirty;
    }
    
    public void setBounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (this.dirty) {
            return;
        }
        final float x2 = x + width;
        final float y2 = y + height;
        final float[] vertices = this.vertices;
        vertices[0] = x;
        vertices[1] = y;
        vertices[9] = x;
        vertices[10] = y2;
        vertices[18] = x2;
        vertices[19] = y2;
        vertices[27] = x2;
        vertices[28] = y;
        if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
            this.dirty = true;
        }
    }
    
    public void setSize(final float width, final float height) {
        this.width = width;
        this.height = height;
        if (this.dirty) {
            return;
        }
        final float x2 = this.x + width;
        final float y2 = this.y + height;
        final float[] vertices = this.vertices;
        vertices[0] = this.x;
        vertices[1] = this.y;
        vertices[9] = this.x;
        vertices[10] = y2;
        vertices[18] = x2;
        vertices[19] = y2;
        vertices[27] = x2;
        vertices[28] = this.y;
        if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
            this.dirty = true;
        }
    }
    
    public void setPosition(final float x, final float y) {
        this.translate(x - this.x, y - this.y);
    }
    
    public void setOriginBasedPosition(final float x, final float y) {
        this.setPosition(x - this.originX, y - this.originY);
    }
    
    public void setX(final float x) {
        this.translateX(x - this.x);
    }
    
    public void setY(final float y) {
        this.translateY(y - this.y);
    }
    
    public void setCenterX(final float x) {
        this.setX(x - this.width / 2.0f);
    }
    
    public void setCenterY(final float y) {
        this.setY(y - this.height / 2.0f);
    }
    
    public void setCenter(final float x, final float y) {
        this.setCenterX(x);
        this.setCenterY(y);
    }
    
    public void translateX(final float xAmount) {
        this.x += xAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices2;
        final float[] vertices = vertices2 = this.vertices;
        final int n = 0;
        vertices2[n] += xAmount;
        final float[] array = vertices;
        final int n2 = 9;
        array[n2] += xAmount;
        final float[] array2 = vertices;
        final int n3 = 18;
        array2[n3] += xAmount;
        final float[] array3 = vertices;
        final int n4 = 27;
        array3[n4] += xAmount;
    }
    
    public void translateY(final float yAmount) {
        this.y += yAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices2;
        final float[] vertices = vertices2 = this.vertices;
        final int n = 1;
        vertices2[n] += yAmount;
        final float[] array = vertices;
        final int n2 = 10;
        array[n2] += yAmount;
        final float[] array2 = vertices;
        final int n3 = 19;
        array2[n3] += yAmount;
        final float[] array3 = vertices;
        final int n4 = 28;
        array3[n4] += yAmount;
    }
    
    public void translate(final float xAmount, final float yAmount) {
        this.x += xAmount;
        this.y += yAmount;
        if (this.dirty) {
            return;
        }
        final float[] vertices2;
        final float[] vertices = vertices2 = this.vertices;
        final int n = 0;
        vertices2[n] += xAmount;
        final float[] array = vertices;
        final int n2 = 1;
        array[n2] += yAmount;
        final float[] array2 = vertices;
        final int n3 = 9;
        array2[n3] += xAmount;
        final float[] array3 = vertices;
        final int n4 = 10;
        array3[n4] += yAmount;
        final float[] array4 = vertices;
        final int n5 = 18;
        array4[n5] += xAmount;
        final float[] array5 = vertices;
        final int n6 = 19;
        array5[n6] += yAmount;
        final float[] array6 = vertices;
        final int n7 = 27;
        array6[n7] += xAmount;
        final float[] array7 = vertices;
        final int n8 = 28;
        array7[n8] += yAmount;
    }
    
    public void setColor(final Color tint) {
        this.color.set(tint);
        final float color = tint.toFloatBits();
        final float[] vertices = this.vertices;
        vertices[11] = (vertices[2] = color);
        vertices[29] = (vertices[20] = color);
    }
    
    public void setAlpha(final float a) {
        this.color.a = a;
        final float color = this.color.toFloatBits();
        this.vertices[2] = color;
        this.vertices[11] = color;
        this.vertices[20] = color;
        this.vertices[29] = color;
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        final float color = this.color.toFloatBits();
        final float[] vertices = this.vertices;
        vertices[11] = (vertices[2] = color);
        vertices[29] = (vertices[20] = color);
    }
    
    public void setPackedColor(final float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        final float[] vertices = this.vertices;
        vertices[11] = (vertices[2] = packedColor);
        vertices[29] = (vertices[20] = packedColor);
    }
    
    public void setOrigin(final float originX, final float originY) {
        this.originX = originX;
        this.originY = originY;
        this.dirty = true;
    }
    
    public void setOriginCenter() {
        this.originX = this.width / 2.0f;
        this.originY = this.height / 2.0f;
        this.dirty = true;
    }
    
    public void setRotation(final float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void rotate(final float degrees) {
        if (degrees == 0.0f) {
            return;
        }
        this.rotation += degrees;
        this.dirty = true;
    }
    
    public void rotate90(final boolean clockwise) {
        final float[] vertices = this.vertices;
        if (clockwise) {
            float temp = vertices[4];
            vertices[4] = vertices[31];
            vertices[31] = vertices[22];
            vertices[22] = vertices[13];
            vertices[13] = temp;
            temp = vertices[3];
            vertices[3] = vertices[30];
            vertices[30] = vertices[21];
            vertices[21] = vertices[12];
            vertices[12] = temp;
        }
        else {
            float temp = vertices[4];
            vertices[4] = vertices[13];
            vertices[13] = vertices[22];
            vertices[22] = vertices[31];
            vertices[31] = temp;
            temp = vertices[3];
            vertices[3] = vertices[12];
            vertices[12] = vertices[21];
            vertices[21] = vertices[30];
            vertices[30] = temp;
        }
    }
    
    public void setScale(final float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
        this.dirty = true;
    }
    
    public void setScale(final float scaleX, final float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.dirty = true;
    }
    
    public void scale(final float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }
    
    public float[] getVertices() {
        if (this.dirty) {
            this.dirty = false;
            final float[] vertices = this.vertices;
            float localX = -this.originX;
            float localY = -this.originY;
            float localX2 = localX + this.width;
            float localY2 = localY + this.height;
            final float worldOriginX = this.x - localX;
            final float worldOriginY = this.y - localY;
            if (this.scaleX != 1.0f || this.scaleY != 1.0f) {
                localX *= this.scaleX;
                localY *= this.scaleY;
                localX2 *= this.scaleX;
                localY2 *= this.scaleY;
            }
            if (this.rotation != 0.0f) {
                final float cos = MathUtils.cosDeg(this.rotation);
                final float sin = MathUtils.sinDeg(this.rotation);
                final float localXCos = localX * cos;
                final float localXSin = localX * sin;
                final float localYCos = localY * cos;
                final float localYSin = localY * sin;
                final float localX2Cos = localX2 * cos;
                final float localX2Sin = localX2 * sin;
                final float localY2Cos = localY2 * cos;
                final float localY2Sin = localY2 * sin;
                final float x1 = localXCos - localYSin + worldOriginX;
                final float y1 = localYCos + localXSin + worldOriginY;
                vertices[0] = x1;
                vertices[1] = y1;
                final float x2 = localXCos - localY2Sin + worldOriginX;
                final float y2 = localY2Cos + localXSin + worldOriginY;
                vertices[9] = x2;
                vertices[10] = y2;
                final float x3 = localX2Cos - localY2Sin + worldOriginX;
                final float y3 = localY2Cos + localX2Sin + worldOriginY;
                vertices[18] = x3;
                vertices[19] = y3;
                vertices[27] = x1 + (x3 - x2);
                vertices[28] = y3 - (y2 - y1);
            }
            else {
                final float x4 = localX + worldOriginX;
                final float y4 = localY + worldOriginY;
                final float x5 = localX2 + worldOriginX;
                final float y5 = localY2 + worldOriginY;
                vertices[0] = x4;
                vertices[1] = y4;
                vertices[9] = x4;
                vertices[10] = y5;
                vertices[18] = x5;
                vertices[19] = y5;
                vertices[27] = x5;
                vertices[28] = y4;
            }
        }
        this.vertices[5] = 0.0f;
        this.vertices[6] = 15.0f;
        this.vertices[14] = 0.0f;
        this.vertices[15] = 15.0f;
        this.vertices[23] = 0.0f;
        this.vertices[24] = 15.0f;
        this.vertices[32] = 0.0f;
        this.vertices[33] = 15.0f;
        return this.vertices;
    }
    
    public Rectangle getBoundingRectangle() {
        final float[] vertices = this.getVertices();
        float minx = vertices[0];
        float miny = vertices[1];
        float maxx = vertices[0];
        float maxy = vertices[1];
        minx = ((minx > vertices[9]) ? vertices[9] : minx);
        minx = ((minx > vertices[18]) ? vertices[18] : minx);
        minx = ((minx > vertices[27]) ? vertices[27] : minx);
        maxx = ((maxx < vertices[9]) ? vertices[9] : maxx);
        maxx = ((maxx < vertices[18]) ? vertices[18] : maxx);
        maxx = ((maxx < vertices[27]) ? vertices[27] : maxx);
        miny = ((miny > vertices[10]) ? vertices[10] : miny);
        miny = ((miny > vertices[19]) ? vertices[19] : miny);
        miny = ((miny > vertices[28]) ? vertices[28] : miny);
        maxy = ((maxy < vertices[10]) ? vertices[10] : maxy);
        maxy = ((maxy < vertices[19]) ? vertices[19] : maxy);
        maxy = ((maxy < vertices[28]) ? vertices[28] : maxy);
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        this.bounds.x = minx;
        this.bounds.y = miny;
        this.bounds.width = maxx - minx;
        this.bounds.height = maxy - miny;
        return this.bounds;
    }
    
    public void draw(final Batch batch) {
        final SpriteBatch b = (SpriteBatch)batch;
        this.setTexture(b.uTex[14]);
        System.out.println(String.valueOf((int)SpriteBatch.particleX) + "," + (int)SpriteBatch.particleY);
        this.setRegion((int)SpriteBatch.particleX, (int)SpriteBatch.particleY, 32, 32);
        batch.draw(this.texture, this.getVertices(), 0, 36);
    }
    
    public void draw(final Batch batch, final float alphaModulation) {
        final float oldAlpha = this.getColor().a;
        this.setAlpha(oldAlpha * alphaModulation);
        this.draw(batch);
        this.setAlpha(oldAlpha);
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public float getOriginX() {
        return this.originX;
    }
    
    public float getOriginY() {
        return this.originY;
    }
    
    public float getScaleX() {
        return this.scaleX;
    }
    
    public float getScaleY() {
        return this.scaleY;
    }
    
    public Color getColor() {
        final int intBits = NumberUtils.floatToIntColor(this.vertices[2]);
        final Color color = this.color;
        color.r = (intBits & 0xFF) / 255.0f;
        color.g = (intBits >>> 8 & 0xFF) / 255.0f;
        color.b = (intBits >>> 16 & 0xFF) / 255.0f;
        color.a = (intBits >>> 24 & 0xFF) / 255.0f;
        return color;
    }
    
    @Override
    public void setRegion(final float u, final float v, final float u2, final float v2) {
        super.setRegion(u, v, u2, v2);
        final float[] vertices = this.vertices;
        vertices[3] = u;
        vertices[4] = v2;
        vertices[12] = u;
        vertices[13] = v;
        vertices[21] = u2;
        vertices[22] = v;
        vertices[30] = u2;
        vertices[31] = v2;
    }
    
    @Override
    public void setU(final float u) {
        super.setU(u);
        this.vertices[3] = u;
        this.vertices[12] = u;
    }
    
    @Override
    public void setV(final float v) {
        super.setV(v);
        this.vertices[13] = v;
        this.vertices[22] = v;
    }
    
    @Override
    public void setU2(final float u2) {
        super.setU2(u2);
        this.vertices[21] = u2;
        this.vertices[30] = u2;
    }
    
    @Override
    public void setV2(final float v2) {
        super.setV2(v2);
        this.vertices[4] = v2;
        this.vertices[31] = v2;
    }
    
    public void setFlip(final boolean x, final boolean y) {
        boolean performX = false;
        boolean performY = false;
        if (this.isFlipX() != x) {
            performX = true;
        }
        if (this.isFlipY() != y) {
            performY = true;
        }
        this.flip(performX, performY);
    }
    
    @Override
    public void flip(final boolean x, final boolean y) {
        super.flip(x, y);
        final float[] vertices = this.vertices;
        if (x) {
            float temp = vertices[3];
            vertices[3] = vertices[21];
            vertices[21] = temp;
            temp = vertices[12];
            vertices[12] = vertices[30];
            vertices[30] = temp;
        }
        if (y) {
            float temp = vertices[4];
            vertices[4] = vertices[22];
            vertices[22] = temp;
            temp = vertices[13];
            vertices[13] = vertices[31];
            vertices[31] = temp;
        }
    }
    
    @Override
    public void scroll(final float xAmount, final float yAmount) {
        final float[] vertices = this.vertices;
        if (xAmount != 0.0f) {
            final float u = (vertices[3] + xAmount) % 1.0f;
            final float u2 = u + this.width / this.texture.getWidth();
            this.u = u;
            this.u2 = u2;
            vertices[12] = (vertices[3] = u);
            vertices[30] = (vertices[21] = u2);
        }
        if (yAmount != 0.0f) {
            final float v = (vertices[13] + yAmount) % 1.0f;
            final float v2 = v + this.height / this.texture.getHeight();
            this.v = v;
            vertices[4] = (this.v2 = v2);
            vertices[22] = (vertices[13] = v);
            vertices[31] = v2;
        }
    }
}
