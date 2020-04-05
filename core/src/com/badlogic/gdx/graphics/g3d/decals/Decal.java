// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Decal
{
    private static final int VERTEX_SIZE = 6;
    public static final int SIZE = 24;
    private static Vector3 tmp;
    private static Vector3 tmp2;
    public int value;
    protected float[] vertices;
    protected Vector3 position;
    protected Quaternion rotation;
    protected Vector2 scale;
    protected Color color;
    public Vector2 transformationOffset;
    protected Vector2 dimensions;
    protected DecalMaterial material;
    protected boolean updated;
    static final Vector3 dir;
    public static final int X1 = 0;
    public static final int Y1 = 1;
    public static final int Z1 = 2;
    public static final int C1 = 3;
    public static final int U1 = 4;
    public static final int V1 = 5;
    public static final int X2 = 6;
    public static final int Y2 = 7;
    public static final int Z2 = 8;
    public static final int C2 = 9;
    public static final int U2 = 10;
    public static final int V2 = 11;
    public static final int X3 = 12;
    public static final int Y3 = 13;
    public static final int Z3 = 14;
    public static final int C3 = 15;
    public static final int U3 = 16;
    public static final int V3 = 17;
    public static final int X4 = 18;
    public static final int Y4 = 19;
    public static final int Z4 = 20;
    public static final int C4 = 21;
    public static final int U4 = 22;
    public static final int V4 = 23;
    protected static Quaternion rotator;
    
    static {
        Decal.tmp = new Vector3();
        Decal.tmp2 = new Vector3();
        dir = new Vector3();
        Decal.rotator = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Decal() {
        this.vertices = new float[24];
        this.position = new Vector3();
        this.rotation = new Quaternion();
        this.scale = new Vector2(1.0f, 1.0f);
        this.color = new Color();
        this.transformationOffset = null;
        this.dimensions = new Vector2();
        this.updated = false;
        this.material = new DecalMaterial();
    }
    
    public Decal(final DecalMaterial material) {
        this.vertices = new float[24];
        this.position = new Vector3();
        this.rotation = new Quaternion();
        this.scale = new Vector2(1.0f, 1.0f);
        this.color = new Color();
        this.transformationOffset = null;
        this.dimensions = new Vector2();
        this.updated = false;
        this.material = material;
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        final int intBits = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        final float color = NumberUtils.intToFloatColor(intBits);
        this.vertices[3] = color;
        this.vertices[9] = color;
        this.vertices[15] = color;
        this.vertices[21] = color;
    }
    
    public void setColor(final Color tint) {
        this.color.set(tint);
        final float color = tint.toFloatBits();
        this.vertices[3] = color;
        this.vertices[9] = color;
        this.vertices[15] = color;
        this.vertices[21] = color;
    }
    
    public void setPackedColor(final float color) {
        Color.abgr8888ToColor(this.color, color);
        this.vertices[3] = color;
        this.vertices[9] = color;
        this.vertices[15] = color;
        this.vertices[21] = color;
    }
    
    public void setRotationX(final float angle) {
        this.rotation.set(Vector3.X, angle);
        this.updated = false;
    }
    
    public void setRotationY(final float angle) {
        this.rotation.set(Vector3.Y, angle);
        this.updated = false;
    }
    
    public void setRotationZ(final float angle) {
        this.rotation.set(Vector3.Z, angle);
        this.updated = false;
    }
    
    public void rotateX(final float angle) {
        Decal.rotator.set(Vector3.X, angle);
        this.rotation.mul(Decal.rotator);
        this.updated = false;
    }
    
    public void rotateY(final float angle) {
        Decal.rotator.set(Vector3.Y, angle);
        this.rotation.mul(Decal.rotator);
        this.updated = false;
    }
    
    public void rotateZ(final float angle) {
        Decal.rotator.set(Vector3.Z, angle);
        this.rotation.mul(Decal.rotator);
        this.updated = false;
    }
    
    public void setRotation(final float yaw, final float pitch, final float roll) {
        this.rotation.setEulerAngles(yaw, pitch, roll);
        this.updated = false;
    }
    
    public void setRotation(final Vector3 dir, final Vector3 up) {
        Decal.tmp.set(up).crs(dir).nor();
        Decal.tmp2.set(dir).crs(Decal.tmp).nor();
        this.rotation.setFromAxes(Decal.tmp.x, Decal.tmp2.x, dir.x, Decal.tmp.y, Decal.tmp2.y, dir.y, Decal.tmp.z, Decal.tmp2.z, dir.z);
        this.updated = false;
    }
    
    public void setRotation(final Quaternion q) {
        this.rotation.set(q);
        this.updated = false;
    }
    
    public Quaternion getRotation() {
        return this.rotation;
    }
    
    public void translateX(final float units) {
        final Vector3 position = this.position;
        position.x += units;
        this.updated = false;
    }
    
    public void setX(final float x) {
        this.position.x = x;
        this.updated = false;
    }
    
    public float getX() {
        return this.position.x;
    }
    
    public void translateY(final float units) {
        final Vector3 position = this.position;
        position.y += units;
        this.updated = false;
    }
    
    public void setY(final float y) {
        this.position.y = y;
        this.updated = false;
    }
    
    public float getY() {
        return this.position.y;
    }
    
    public void translateZ(final float units) {
        final Vector3 position = this.position;
        position.z += units;
        this.updated = false;
    }
    
    public void setZ(final float z) {
        this.position.z = z;
        this.updated = false;
    }
    
    public float getZ() {
        return this.position.z;
    }
    
    public void translate(final float x, final float y, final float z) {
        this.position.add(x, y, z);
        this.updated = false;
    }
    
    public void translate(final Vector3 trans) {
        this.position.add(trans);
        this.updated = false;
    }
    
    public void setPosition(final float x, final float y, final float z) {
        this.position.set(x, y, z);
        this.updated = false;
    }
    
    public void setPosition(final Vector3 pos) {
        this.position.set(pos);
        this.updated = false;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public Vector3 getPosition() {
        return this.position;
    }
    
    public void setScaleX(final float scale) {
        this.scale.x = scale;
        this.updated = false;
    }
    
    public float getScaleX() {
        return this.scale.x;
    }
    
    public void setScaleY(final float scale) {
        this.scale.y = scale;
        this.updated = false;
    }
    
    public float getScaleY() {
        return this.scale.y;
    }
    
    public void setScale(final float scaleX, final float scaleY) {
        this.scale.set(scaleX, scaleY);
        this.updated = false;
    }
    
    public void setScale(final float scale) {
        this.scale.set(scale, scale);
        this.updated = false;
    }
    
    public void setWidth(final float width) {
        this.dimensions.x = width;
        this.updated = false;
    }
    
    public float getWidth() {
        return this.dimensions.x;
    }
    
    public void setHeight(final float height) {
        this.dimensions.y = height;
        this.updated = false;
    }
    
    public float getHeight() {
        return this.dimensions.y;
    }
    
    public void setDimensions(final float width, final float height) {
        this.dimensions.set(width, height);
        this.updated = false;
    }
    
    public float[] getVertices() {
        return this.vertices;
    }
    
    protected void update() {
        if (!this.updated) {
            this.resetVertices();
            this.transformVertices();
        }
    }
    
    protected void transformVertices() {
        float tx;
        float ty;
        if (this.transformationOffset != null) {
            tx = -this.transformationOffset.x;
            ty = -this.transformationOffset.y;
        }
        else {
            ty = (tx = 0.0f);
        }
        float x = (this.vertices[0] + tx) * this.scale.x;
        float y = (this.vertices[1] + ty) * this.scale.y;
        float z = this.vertices[2];
        this.vertices[0] = this.rotation.w * x + this.rotation.y * z - this.rotation.z * y;
        this.vertices[1] = this.rotation.w * y + this.rotation.z * x - this.rotation.x * z;
        this.vertices[2] = this.rotation.w * z + this.rotation.x * y - this.rotation.y * x;
        float w = -this.rotation.x * x - this.rotation.y * y - this.rotation.z * z;
        this.rotation.conjugate();
        x = this.vertices[0];
        y = this.vertices[1];
        z = this.vertices[2];
        this.vertices[0] = w * this.rotation.x + x * this.rotation.w + y * this.rotation.z - z * this.rotation.y;
        this.vertices[1] = w * this.rotation.y + y * this.rotation.w + z * this.rotation.x - x * this.rotation.z;
        this.vertices[2] = w * this.rotation.z + z * this.rotation.w + x * this.rotation.y - y * this.rotation.x;
        this.rotation.conjugate();
        final float[] vertices = this.vertices;
        final int n = 0;
        vertices[n] += this.position.x - tx;
        final float[] vertices2 = this.vertices;
        final int n2 = 1;
        vertices2[n2] += this.position.y - ty;
        final float[] vertices3 = this.vertices;
        final int n3 = 2;
        vertices3[n3] += this.position.z;
        x = (this.vertices[6] + tx) * this.scale.x;
        y = (this.vertices[7] + ty) * this.scale.y;
        z = this.vertices[8];
        this.vertices[6] = this.rotation.w * x + this.rotation.y * z - this.rotation.z * y;
        this.vertices[7] = this.rotation.w * y + this.rotation.z * x - this.rotation.x * z;
        this.vertices[8] = this.rotation.w * z + this.rotation.x * y - this.rotation.y * x;
        w = -this.rotation.x * x - this.rotation.y * y - this.rotation.z * z;
        this.rotation.conjugate();
        x = this.vertices[6];
        y = this.vertices[7];
        z = this.vertices[8];
        this.vertices[6] = w * this.rotation.x + x * this.rotation.w + y * this.rotation.z - z * this.rotation.y;
        this.vertices[7] = w * this.rotation.y + y * this.rotation.w + z * this.rotation.x - x * this.rotation.z;
        this.vertices[8] = w * this.rotation.z + z * this.rotation.w + x * this.rotation.y - y * this.rotation.x;
        this.rotation.conjugate();
        final float[] vertices4 = this.vertices;
        final int n4 = 6;
        vertices4[n4] += this.position.x - tx;
        final float[] vertices5 = this.vertices;
        final int n5 = 7;
        vertices5[n5] += this.position.y - ty;
        final float[] vertices6 = this.vertices;
        final int n6 = 8;
        vertices6[n6] += this.position.z;
        x = (this.vertices[12] + tx) * this.scale.x;
        y = (this.vertices[13] + ty) * this.scale.y;
        z = this.vertices[14];
        this.vertices[12] = this.rotation.w * x + this.rotation.y * z - this.rotation.z * y;
        this.vertices[13] = this.rotation.w * y + this.rotation.z * x - this.rotation.x * z;
        this.vertices[14] = this.rotation.w * z + this.rotation.x * y - this.rotation.y * x;
        w = -this.rotation.x * x - this.rotation.y * y - this.rotation.z * z;
        this.rotation.conjugate();
        x = this.vertices[12];
        y = this.vertices[13];
        z = this.vertices[14];
        this.vertices[12] = w * this.rotation.x + x * this.rotation.w + y * this.rotation.z - z * this.rotation.y;
        this.vertices[13] = w * this.rotation.y + y * this.rotation.w + z * this.rotation.x - x * this.rotation.z;
        this.vertices[14] = w * this.rotation.z + z * this.rotation.w + x * this.rotation.y - y * this.rotation.x;
        this.rotation.conjugate();
        final float[] vertices7 = this.vertices;
        final int n7 = 12;
        vertices7[n7] += this.position.x - tx;
        final float[] vertices8 = this.vertices;
        final int n8 = 13;
        vertices8[n8] += this.position.y - ty;
        final float[] vertices9 = this.vertices;
        final int n9 = 14;
        vertices9[n9] += this.position.z;
        x = (this.vertices[18] + tx) * this.scale.x;
        y = (this.vertices[19] + ty) * this.scale.y;
        z = this.vertices[20];
        this.vertices[18] = this.rotation.w * x + this.rotation.y * z - this.rotation.z * y;
        this.vertices[19] = this.rotation.w * y + this.rotation.z * x - this.rotation.x * z;
        this.vertices[20] = this.rotation.w * z + this.rotation.x * y - this.rotation.y * x;
        w = -this.rotation.x * x - this.rotation.y * y - this.rotation.z * z;
        this.rotation.conjugate();
        x = this.vertices[18];
        y = this.vertices[19];
        z = this.vertices[20];
        this.vertices[18] = w * this.rotation.x + x * this.rotation.w + y * this.rotation.z - z * this.rotation.y;
        this.vertices[19] = w * this.rotation.y + y * this.rotation.w + z * this.rotation.x - x * this.rotation.z;
        this.vertices[20] = w * this.rotation.z + z * this.rotation.w + x * this.rotation.y - y * this.rotation.x;
        this.rotation.conjugate();
        final float[] vertices10 = this.vertices;
        final int n10 = 18;
        vertices10[n10] += this.position.x - tx;
        final float[] vertices11 = this.vertices;
        final int n11 = 19;
        vertices11[n11] += this.position.y - ty;
        final float[] vertices12 = this.vertices;
        final int n12 = 20;
        vertices12[n12] += this.position.z;
        this.updated = true;
    }
    
    protected void resetVertices() {
        final float left = -this.dimensions.x / 2.0f;
        final float right = left + this.dimensions.x;
        final float top = this.dimensions.y / 2.0f;
        final float bottom = top - this.dimensions.y;
        this.vertices[0] = left;
        this.vertices[1] = top;
        this.vertices[2] = 0.0f;
        this.vertices[6] = right;
        this.vertices[7] = top;
        this.vertices[8] = 0.0f;
        this.vertices[12] = left;
        this.vertices[13] = bottom;
        this.vertices[14] = 0.0f;
        this.vertices[18] = right;
        this.vertices[19] = bottom;
        this.vertices[20] = 0.0f;
        this.updated = false;
    }
    
    protected void updateUVs() {
        final TextureRegion tr = this.material.textureRegion;
        this.vertices[4] = tr.getU();
        this.vertices[5] = tr.getV();
        this.vertices[10] = tr.getU2();
        this.vertices[11] = tr.getV();
        this.vertices[16] = tr.getU();
        this.vertices[17] = tr.getV2();
        this.vertices[22] = tr.getU2();
        this.vertices[23] = tr.getV2();
    }
    
    public void setTextureRegion(final TextureRegion textureRegion) {
        this.material.textureRegion = textureRegion;
        this.updateUVs();
    }
    
    public TextureRegion getTextureRegion() {
        return this.material.textureRegion;
    }
    
    public void setBlending(final int srcBlendFactor, final int dstBlendFactor) {
        this.material.srcBlendFactor = srcBlendFactor;
        this.material.dstBlendFactor = dstBlendFactor;
    }
    
    public DecalMaterial getMaterial() {
        return this.material;
    }
    
    public void setMaterial(final DecalMaterial material) {
        this.material = material;
    }
    
    public void lookAt(final Vector3 position, final Vector3 up) {
        Decal.dir.set(position).sub(this.position).nor();
        this.setRotation(Decal.dir, up);
    }
    
    public static Decal newDecal(final TextureRegion textureRegion) {
        return newDecal((float)textureRegion.getRegionWidth(), (float)textureRegion.getRegionHeight(), textureRegion, -1, -1);
    }
    
    public static Decal newDecal(final TextureRegion textureRegion, final boolean hasTransparency) {
        return newDecal((float)textureRegion.getRegionWidth(), (float)textureRegion.getRegionHeight(), textureRegion, hasTransparency ? 770 : -1, hasTransparency ? 771 : -1);
    }
    
    public static Decal newDecal(final float width, final float height, final TextureRegion textureRegion) {
        return newDecal(width, height, textureRegion, -1, -1);
    }
    
    public static Decal newDecal(final float width, final float height, final TextureRegion textureRegion, final boolean hasTransparency) {
        return newDecal(width, height, textureRegion, hasTransparency ? 770 : -1, hasTransparency ? 771 : -1);
    }
    
    public static Decal newDecal(final float width, final float height, final TextureRegion textureRegion, final int srcBlendFactor, final int dstBlendFactor) {
        final Decal decal = new Decal();
        decal.setTextureRegion(textureRegion);
        decal.setBlending(srcBlendFactor, dstBlendFactor);
        decal.dimensions.x = width;
        decal.dimensions.y = height;
        decal.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        return decal;
    }
    
    public static Decal newDecal(final float width, final float height, final TextureRegion textureRegion, final int srcBlendFactor, final int dstBlendFactor, final DecalMaterial material) {
        final Decal decal = new Decal(material);
        decal.setTextureRegion(textureRegion);
        decal.setBlending(srcBlendFactor, dstBlendFactor);
        decal.dimensions.x = width;
        decal.dimensions.y = height;
        decal.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        return decal;
    }
}
