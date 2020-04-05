// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Mesh;

public class SpriteBatch implements Batch
{
    @Deprecated
    public static Mesh.VertexDataType defaultVertexDataType;
    private Mesh mesh;
    final float[] vertices;
    int idx;
    public Texture lastTexture;
    public Texture[] uTex;
    float invTexWidth;
    float invTexHeight;
    public static float particleX;
    public static float particleY;
    public static int particleTexNum;
    boolean drawing;
    private final Matrix4 transformMatrix;
    private final Matrix4 projectionMatrix;
    private final Matrix4 combinedMatrix;
    private boolean blendingDisabled;
    private int blendSrcFunc;
    private int blendDstFunc;
    private int blendSrcFuncAlpha;
    private int blendDstFuncAlpha;
    private final ShaderProgram shader;
    private ShaderProgram customShader;
    private boolean ownsShader;
    private final Color color;
    float colorPacked;
    public int renderCalls;
    public int totalRenderCalls;
    public int maxSpritesInBatch;
    
    static {
        SpriteBatch.defaultVertexDataType = Mesh.VertexDataType.VertexArray;
        SpriteBatch.particleX = 0.0f;
        SpriteBatch.particleY = 0.0f;
        SpriteBatch.particleTexNum = 2;
    }
    
    public void setTexture(final int i, final Texture t) {
        this.uTex[i] = t;
    }
    
    public SpriteBatch() {
        this(1000, null);
    }
    
    public SpriteBatch(final int size) {
        this(size, null);
    }
    
    public SpriteBatch(final int size, final ShaderProgram defaultShader) {
        this.idx = 0;
        this.lastTexture = null;
        this.uTex = new Texture[15];
        this.invTexWidth = 0.0f;
        this.invTexHeight = 0.0f;
        this.drawing = false;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.blendingDisabled = false;
        this.blendSrcFunc = 770;
        this.blendDstFunc = 771;
        this.blendSrcFuncAlpha = 770;
        this.blendDstFuncAlpha = 771;
        this.customShader = null;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.colorPacked = Color.WHITE_FLOAT_BITS;
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.maxSpritesInBatch = 0;
        if (size > 8191) {
            throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);
        }
        final Mesh.VertexDataType vertexDataType = (Gdx.gl30 != null) ? Mesh.VertexDataType.VertexBufferObjectWithVAO : SpriteBatch.defaultVertexDataType;
        this.mesh = new Mesh(vertexDataType, false, size * 4, size * 6, new VertexAttribute[] { new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0"), new VertexAttribute(32, 1, "a_data0"), new VertexAttribute(32, 1, "a_data1"), new VertexAttribute(16, 2, "a_texCoord1") });
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        this.vertices = new float[size * 36];
        final int len = size * 6;
        final short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        this.mesh.setIndices(indices);
        if (defaultShader == null) {
            this.shader = createDefaultShader();
            this.ownsShader = true;
        }
        else {
            this.shader = defaultShader;
        }
    }
    
    public static ShaderProgram createDefaultShader() {
        final String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projTrans;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_color.a = v_color.a * (255.0/254.0);\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projTrans * a_position;\n}\n";
        final String fragmentShader = "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}";
        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        return shader;
    }
    
    @Override
    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("SpriteBatch.end must be called before begin.");
        }
        this.renderCalls = 0;
        Gdx.gl.glDepthMask(false);
        if (this.customShader != null) {
            this.customShader.begin();
        }
        else {
            this.shader.begin();
        }
        this.setupMatrices();
        this.drawing = true;
    }
    
    @Override
    public void end() {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before end.");
        }
        if (this.idx > 0) {
            this.flush();
        }
        this.lastTexture = null;
        this.drawing = false;
        final GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        if (this.isBlendingEnabled()) {
            gl.glDisable(3042);
        }
        if (this.customShader != null) {
            this.customShader.end();
        }
        else {
            this.shader.end();
        }
    }
    
    @Override
    public void setColor(final Color tint) {
        this.color.set(tint);
        this.colorPacked = tint.toFloatBits();
    }
    
    @Override
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        this.colorPacked = this.color.toFloatBits();
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public void setPackedColor(final float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        this.colorPacked = packedColor;
    }
    
    @Override
    public float getPackedColor() {
        return this.colorPacked;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (scaleX != 1.0f || scaleY != 1.0f) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        float x5;
        float y5;
        if (rotation != 0.0f) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);
            x2 = cos * p1x - sin * p1y;
            y2 = sin * p1x + cos * p1y;
            x3 = cos * p2x - sin * p2y;
            y3 = sin * p2x + cos * p2y;
            x4 = cos * p3x - sin * p3y;
            y4 = sin * p3x + cos * p3y;
            x5 = x2 + (x4 - x3);
            y5 = y4 - (y3 - y2);
        }
        else {
            x2 = p1x;
            y2 = p1y;
            x3 = p2x;
            y3 = p2y;
            x4 = p3x;
            y4 = p3y;
            x5 = p4x;
            y5 = p4y;
        }
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;
        x5 += worldOriginX;
        y5 += worldOriginY;
        float u = srcX * this.invTexWidth;
        float v = (srcY + srcHeight) * this.invTexHeight;
        float u2 = (srcX + srcWidth) * this.invTexWidth;
        float v2 = srcY * this.invTexHeight;
        if (flipX) {
            final float tmp = u;
            u = u2;
            u2 = tmp;
        }
        if (flipY) {
            final float tmp = v;
            v = v2;
            v2 = tmp;
        }
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x2;
        vertices[idx + 1] = y2;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = x3;
        vertices[idx + 6] = y3;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = x4;
        vertices[idx + 11] = y4;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u2;
        vertices[idx + 14] = v2;
        vertices[idx + 15] = x5;
        vertices[idx + 16] = y5;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u2;
        vertices[idx + 19] = v;
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
        this.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, flipX, flipY, new float[] { 0.0f, 0.0f }, null);
    }
    
    public void draw(final Texture texture, final float x, final float y, final float width, final float height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY, final float[] data, final TextureRegion aregion) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture && data[1] == 0.0f) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        float u = srcX * this.invTexWidth;
        float v = (srcY + srcHeight) * this.invTexHeight;
        float u2 = (srcX + srcWidth) * this.invTexWidth;
        float v2 = srcY * this.invTexHeight;
        final float fx2 = x + width;
        final float fy2 = y + height;
        if (flipX) {
            final float tmp = u;
            u = u2;
            u2 = tmp;
        }
        if (flipY) {
            final float tmp = v;
            v = v2;
            v2 = tmp;
        }
        float au = 0.0f;
        float av = 0.0f;
        float au2 = 0.0f;
        float av2 = 0.0f;
        if (aregion != null) {
            au = aregion.u;
            av = aregion.v2;
            au2 = aregion.u2;
            av2 = aregion.v;
        }
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx + 0] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = data[0];
        vertices[idx + 6] = data[1];
        vertices[idx + 7] = au;
        vertices[idx + 8] = av;
        vertices[idx + 9] = x;
        vertices[idx + 10] = fy2;
        vertices[idx + 11] = color;
        vertices[idx + 12] = u;
        vertices[idx + 13] = v2;
        vertices[idx + 14] = data[0];
        vertices[idx + 15] = data[1];
        vertices[idx + 16] = au;
        vertices[idx + 17] = av;
        vertices[idx + 18] = fx2;
        vertices[idx + 19] = fy2;
        vertices[idx + 20] = color;
        vertices[idx + 21] = u2;
        vertices[idx + 22] = v2;
        vertices[idx + 23] = data[0];
        vertices[idx + 24] = data[1];
        vertices[idx + 25] = au;
        vertices[idx + 26] = av;
        vertices[idx + 27] = fx2;
        vertices[idx + 28] = y;
        vertices[idx + 29] = color;
        vertices[idx + 30] = u2;
        vertices[idx + 31] = v;
        vertices[idx + 32] = data[0];
        vertices[idx + 33] = data[1];
        vertices[idx + 34] = au;
        vertices[idx + 35] = av;
        this.idx = idx + 36;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final int srcX, final int srcY, final int srcWidth, final int srcHeight) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float u = srcX * this.invTexWidth;
        final float v = (srcY + srcHeight) * this.invTexHeight;
        final float u2 = (srcX + srcWidth) * this.invTexWidth;
        final float v2 = srcY * this.invTexHeight;
        final float fx2 = x + srcWidth;
        final float fy2 = y + srcHeight;
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = x;
        vertices[idx + 6] = fy2;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = fx2;
        vertices[idx + 11] = fy2;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u2;
        vertices[idx + 14] = v2;
        vertices[idx + 15] = fx2;
        vertices[idx + 16] = y;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u2;
        vertices[idx + 19] = v;
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height, final float u, final float v, final float u2, final float v2) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = x;
        vertices[idx + 6] = fy2;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = fx2;
        vertices[idx + 11] = fy2;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u2;
        vertices[idx + 14] = v2;
        vertices[idx + 15] = fx2;
        vertices[idx + 16] = y;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u2;
        vertices[idx + 19] = v;
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y) {
        this.draw(texture, x, y, (float)texture.getWidth(), (float)texture.getHeight());
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = 0.0f;
        final float v = 1.0f;
        final float u2 = 1.0f;
        final float v2 = 0.0f;
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = color;
        vertices[idx + 3] = 0.0f;
        vertices[idx + 4] = 1.0f;
        vertices[idx + 5] = x;
        vertices[idx + 6] = fy2;
        vertices[idx + 7] = color;
        vertices[idx + 9] = (vertices[idx + 8] = 0.0f);
        vertices[idx + 10] = fx2;
        vertices[idx + 11] = fy2;
        vertices[idx + 12] = color;
        vertices[idx + 13] = 1.0f;
        vertices[idx + 14] = 0.0f;
        vertices[idx + 15] = fx2;
        vertices[idx + 16] = y;
        vertices[idx + 17] = color;
        vertices[idx + 19] = (vertices[idx + 18] = 1.0f);
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final Texture texture, final float[] spriteVertices, int offset, int count) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        int remainingVertices;
        final int verticesLength = remainingVertices = this.vertices.length;
        remainingVertices -= this.idx;
        if (remainingVertices == 0) {
            this.flush();
            remainingVertices = verticesLength;
        }
        int copyCount = Math.min(remainingVertices, count);
        System.arraycopy(spriteVertices, offset, this.vertices, this.idx, copyCount);
        this.idx += copyCount;
        for (count -= copyCount; count > 0; count -= copyCount) {
            offset += copyCount;
            this.flush();
            copyCount = Math.min(verticesLength, count);
            System.arraycopy(spriteVertices, offset, this.vertices, 0, copyCount);
            this.idx += copyCount;
        }
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y) {
        this.draw(region, x, y, (float)region.getRegionWidth(), (float)region.getRegionHeight());
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float width, final float height) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x;
        vertices[idx + 1] = y;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = x;
        vertices[idx + 6] = fy2;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = fx2;
        vertices[idx + 11] = fy2;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u2;
        vertices[idx + 14] = v2;
        vertices[idx + 15] = fx2;
        vertices[idx + 16] = y;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u2;
        vertices[idx + 19] = v;
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        this.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation, new float[] { 0.0f, 0.0f }, null);
    }
    
    public void draw(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final float[] data, final TextureRegion aregion) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture && data[1] == 0.0f) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (scaleX != 1.0f || scaleY != 1.0f) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        float x5;
        float y5;
        if (rotation != 0.0f) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);
            x2 = cos * p1x - sin * p1y;
            y2 = sin * p1x + cos * p1y;
            x3 = cos * p2x - sin * p2y;
            y3 = sin * p2x + cos * p2y;
            x4 = cos * p3x - sin * p3y;
            y4 = sin * p3x + cos * p3y;
            x5 = x2 + (x4 - x3);
            y5 = y4 - (y3 - y2);
        }
        else {
            x2 = p1x;
            y2 = p1y;
            x3 = p2x;
            y3 = p2y;
            x4 = p3x;
            y4 = p3y;
            x5 = p4x;
            y5 = p4y;
        }
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;
        x5 += worldOriginX;
        y5 += worldOriginY;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;
        float au = 0.0f;
        float av = 0.0f;
        float au2 = 0.0f;
        float av2 = 0.0f;
        if (aregion != null) {
            au = aregion.u;
            av = aregion.v2;
            au2 = aregion.u2;
            av2 = aregion.v;
        }
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx + 0] = x2;
        vertices[idx + 1] = y2;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = data[0];
        vertices[idx + 6] = data[1];
        vertices[idx + 7] = au;
        vertices[idx + 8] = av;
        vertices[idx + 9] = x3;
        vertices[idx + 10] = y3;
        vertices[idx + 11] = color;
        vertices[idx + 12] = u;
        vertices[idx + 13] = v2;
        vertices[idx + 14] = data[0];
        vertices[idx + 15] = data[1];
        vertices[idx + 16] = au;
        vertices[idx + 17] = av2;
        vertices[idx + 18] = x4;
        vertices[idx + 19] = y4;
        vertices[idx + 20] = color;
        vertices[idx + 21] = u2;
        vertices[idx + 22] = v2;
        vertices[idx + 23] = data[0];
        vertices[idx + 24] = data[1];
        vertices[idx + 25] = au2;
        vertices[idx + 26] = av2;
        vertices[idx + 27] = x5;
        vertices[idx + 28] = y5;
        vertices[idx + 29] = color;
        vertices[idx + 30] = u2;
        vertices[idx + 31] = v;
        vertices[idx + 32] = data[0];
        vertices[idx + 33] = data[1];
        vertices[idx + 34] = au2;
        vertices[idx + 35] = av;
        this.idx = idx + 36;
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final boolean clockwise) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (scaleX != 1.0f || scaleY != 1.0f) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        final float p1x = fx;
        final float p1y = fy;
        final float p2x = fx;
        final float p2y = fy2;
        final float p3x = fx2;
        final float p3y = fy2;
        final float p4x = fx2;
        final float p4y = fy;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        float x5;
        float y5;
        if (rotation != 0.0f) {
            final float cos = MathUtils.cosDeg(rotation);
            final float sin = MathUtils.sinDeg(rotation);
            x2 = cos * p1x - sin * p1y;
            y2 = sin * p1x + cos * p1y;
            x3 = cos * p2x - sin * p2y;
            y3 = sin * p2x + cos * p2y;
            x4 = cos * p3x - sin * p3y;
            y4 = sin * p3x + cos * p3y;
            x5 = x2 + (x4 - x3);
            y5 = y4 - (y3 - y2);
        }
        else {
            x2 = p1x;
            y2 = p1y;
            x3 = p2x;
            y3 = p2y;
            x4 = p3x;
            y4 = p3y;
            x5 = p4x;
            y5 = p4y;
        }
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;
        x5 += worldOriginX;
        y5 += worldOriginY;
        float u1;
        float v1;
        float u2;
        float v2;
        float u3;
        float v3;
        float u4;
        float v4;
        if (clockwise) {
            u1 = region.u2;
            v1 = region.v2;
            u2 = region.u;
            v2 = region.v2;
            u3 = region.u;
            v3 = region.v;
            u4 = region.u2;
            v4 = region.v;
        }
        else {
            u1 = region.u;
            v1 = region.v;
            u2 = region.u2;
            v2 = region.v;
            u3 = region.u2;
            v3 = region.v2;
            u4 = region.u;
            v4 = region.v2;
        }
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x2;
        vertices[idx + 1] = y2;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u1;
        vertices[idx + 4] = v1;
        vertices[idx + 5] = x3;
        vertices[idx + 6] = y3;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u2;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = x4;
        vertices[idx + 11] = y4;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u3;
        vertices[idx + 14] = v3;
        vertices[idx + 15] = x5;
        vertices[idx + 16] = y5;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u4;
        vertices[idx + 19] = v4;
        this.idx = idx + 20;
    }
    
    @Override
    public void draw(final TextureRegion region, final float width, final float height, final Affine2 transform) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.idx == vertices.length) {
            this.flush();
        }
        final float x1 = transform.m02;
        final float y1 = transform.m12;
        final float x2 = transform.m01 * height + transform.m02;
        final float y2 = transform.m11 * height + transform.m12;
        final float x3 = transform.m00 * width + transform.m01 * height + transform.m02;
        final float y3 = transform.m10 * width + transform.m11 * height + transform.m12;
        final float x4 = transform.m00 * width + transform.m02;
        final float y4 = transform.m10 * width + transform.m12;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;
        final float color = this.colorPacked;
        final int idx = this.idx;
        vertices[idx] = x1;
        vertices[idx + 1] = y1;
        vertices[idx + 2] = color;
        vertices[idx + 3] = u;
        vertices[idx + 4] = v;
        vertices[idx + 5] = x2;
        vertices[idx + 6] = y2;
        vertices[idx + 7] = color;
        vertices[idx + 8] = u;
        vertices[idx + 9] = v2;
        vertices[idx + 10] = x3;
        vertices[idx + 11] = y3;
        vertices[idx + 12] = color;
        vertices[idx + 13] = u2;
        vertices[idx + 14] = v2;
        vertices[idx + 15] = x4;
        vertices[idx + 16] = y4;
        vertices[idx + 17] = color;
        vertices[idx + 18] = u2;
        vertices[idx + 19] = v;
        this.idx = idx + 20;
    }
    
    @Override
    public void flush() {
        if (this.idx == 0) {
            return;
        }
        ++this.renderCalls;
        ++this.totalRenderCalls;
        final int spritesInBatch = this.idx / 36;
        if (spritesInBatch > this.maxSpritesInBatch) {
            this.maxSpritesInBatch = spritesInBatch;
        }
        final int count = spritesInBatch * 6;
        int i = 0;
        if (this.uTex[0] != null) {
            this.uTex[0].bind(1);
        }
        ++i;
        if (this.uTex[1] != null) {
            this.uTex[1].bind(2);
        }
        ++i;
        if (this.uTex[2] != null) {
            this.uTex[2].bind(3);
        }
        ++i;
        if (this.uTex[3] != null) {
            this.uTex[3].bind(4);
        }
        ++i;
        if (this.uTex[4] != null) {
            this.uTex[4].bind(5);
        }
        ++i;
        if (this.uTex[5] != null) {
            this.uTex[5].bind(6);
        }
        ++i;
        if (this.uTex[6] != null) {
            this.uTex[6].bind(7);
        }
        ++i;
        if (this.uTex[7] != null) {
            this.uTex[7].bind(8);
        }
        ++i;
        if (this.uTex[8] != null) {
            this.uTex[8].bind(9);
        }
        ++i;
        if (this.uTex[9] != null) {
            this.uTex[9].bind(10);
        }
        ++i;
        if (this.uTex[10] != null) {
            this.uTex[10].bind(11);
        }
        ++i;
        if (this.uTex[11] != null) {
            this.uTex[11].bind(12);
        }
        ++i;
        if (this.uTex[12] != null) {
            this.uTex[12].bind(13);
        }
        ++i;
        if (this.uTex[13] != null) {
            this.uTex[13].bind(14);
        }
        ++i;
        if (this.uTex[14] != null) {
            this.uTex[14].bind(15);
        }
        ++i;
        if (this.lastTexture != null) {
            this.lastTexture.bind(0);
        }
        final Mesh mesh = this.mesh;
        mesh.setVertices(this.vertices, 0, this.idx);
        mesh.getIndicesBuffer().position(0);
        mesh.getIndicesBuffer().limit(count);
        if (this.blendingDisabled) {
            Gdx.gl.glDisable(3042);
        }
        else {
            Gdx.gl.glEnable(3042);
            if (this.blendSrcFunc != -1) {
                Gdx.gl.glBlendFuncSeparate(this.blendSrcFunc, this.blendDstFunc, this.blendSrcFuncAlpha, this.blendDstFuncAlpha);
            }
        }
        mesh.render((this.customShader != null) ? this.customShader : this.shader, 4, 0, count);
        this.idx = 0;
    }
    
    @Override
    public void disableBlending() {
        if (this.blendingDisabled) {
            return;
        }
        this.flush();
        this.blendingDisabled = true;
    }
    
    @Override
    public void enableBlending() {
        if (!this.blendingDisabled) {
            return;
        }
        this.flush();
        this.blendingDisabled = false;
    }
    
    @Override
    public void setBlendFunction(final int srcFunc, final int dstFunc) {
        this.setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }
    
    @Override
    public void setBlendFunctionSeparate(final int srcFuncColor, final int dstFuncColor, final int srcFuncAlpha, final int dstFuncAlpha) {
        if (this.blendSrcFunc == srcFuncColor && this.blendDstFunc == dstFuncColor && this.blendSrcFuncAlpha == srcFuncAlpha && this.blendDstFuncAlpha == dstFuncAlpha) {
            return;
        }
        this.flush();
        this.blendSrcFunc = srcFuncColor;
        this.blendDstFunc = dstFuncColor;
        this.blendSrcFuncAlpha = srcFuncAlpha;
        this.blendDstFuncAlpha = dstFuncAlpha;
    }
    
    @Override
    public int getBlendSrcFunc() {
        return this.blendSrcFunc;
    }
    
    @Override
    public int getBlendDstFunc() {
        return this.blendDstFunc;
    }
    
    @Override
    public int getBlendSrcFuncAlpha() {
        return this.blendSrcFuncAlpha;
    }
    
    @Override
    public int getBlendDstFuncAlpha() {
        return this.blendDstFuncAlpha;
    }
    
    @Override
    public void dispose() {
        this.mesh.dispose();
        if (this.ownsShader && this.shader != null) {
            this.shader.dispose();
        }
    }
    
    @Override
    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }
    
    @Override
    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }
    
    @Override
    public void setProjectionMatrix(final Matrix4 projection) {
        if (this.drawing) {
            this.flush();
        }
        this.projectionMatrix.set(projection);
        if (this.drawing) {
            this.setupMatrices();
        }
    }
    
    @Override
    public void setTransformMatrix(final Matrix4 transform) {
        if (this.drawing) {
            this.flush();
        }
        this.transformMatrix.set(transform);
        if (this.drawing) {
            this.setupMatrices();
        }
    }
    
    private void setupMatrices() {
        this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
        this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
        this.customShader.setUniformi("u_texture", 0);
        this.customShader.setUniformi("u_texture2", 1);
        this.customShader.setUniformi("u_texture3", 2);
        this.customShader.setUniformi("u_texture4", 3);
        this.customShader.setUniformi("u_texture5", 4);
        this.customShader.setUniformi("u_texture6", 5);
        this.customShader.setUniformi("u_texture7", 6);
        this.customShader.setUniformi("u_texture8", 7);
        this.customShader.setUniformi("u_texture9", 8);
        this.customShader.setUniformi("u_texture10", 9);
        this.customShader.setUniformi("u_texture11", 10);
        this.customShader.setUniformi("u_texture12", 11);
        this.customShader.setUniformi("u_texture13", 12);
        this.customShader.setUniformi("u_texture14", 13);
        this.customShader.setUniformi("u_texture15", 14);
        this.customShader.setUniformi("u_texture16", 15);
        this.shader.setUniformMatrix("u_projTrans", this.combinedMatrix);
        this.shader.setUniformi("u_texture", 0);
        this.shader.setUniformi("u_texture2", 1);
        this.shader.setUniformi("u_texture3", 2);
        this.shader.setUniformi("u_texture4", 3);
        this.shader.setUniformi("u_texture5", 4);
        this.shader.setUniformi("u_texture6", 5);
        this.shader.setUniformi("u_texture7", 6);
        this.shader.setUniformi("u_texture8", 7);
        this.shader.setUniformi("u_texture9", 8);
        this.shader.setUniformi("u_texture10", 9);
        this.shader.setUniformi("u_texture11", 10);
        this.shader.setUniformi("u_texture12", 11);
        this.shader.setUniformi("u_texture13", 12);
        this.shader.setUniformi("u_texture14", 13);
        this.shader.setUniformi("u_texture15", 14);
        this.shader.setUniformi("u_texture16", 15);
    }
    
    protected void switchTexture(final Texture texture) {
        this.flush();
        this.lastTexture = texture;
        this.invTexWidth = 1.0f / texture.getWidth();
        this.invTexHeight = 1.0f / texture.getHeight();
    }
    
    @Override
    public void setShader(final ShaderProgram shader) {
        if (this.drawing) {
            this.flush();
            if (this.customShader != null) {
                this.customShader.end();
            }
            else {
                this.shader.end();
            }
        }
        this.customShader = shader;
        if (this.drawing) {
            if (this.customShader != null) {
                this.customShader.begin();
            }
            else {
                this.shader.begin();
            }
            this.setupMatrices();
        }
    }
    
    @Override
    public ShaderProgram getShader() {
        if (this.customShader == null) {
            return this.shader;
        }
        return this.customShader;
    }
    
    @Override
    public boolean isBlendingEnabled() {
        return !this.blendingDisabled;
    }
    
    @Override
    public boolean isDrawing() {
        return this.drawing;
    }
}
