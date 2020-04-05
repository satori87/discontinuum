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

public class PolygonSpriteBatch implements PolygonBatch
{
    private Mesh mesh;
    private final float[] vertices;
    private final short[] triangles;
    private int vertexIndex;
    private int triangleIndex;
    private Texture lastTexture;
    private float invTexWidth;
    private float invTexHeight;
    private boolean drawing;
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
    public int maxTrianglesInBatch;
    
    public PolygonSpriteBatch() {
        this(2000, null);
    }
    
    public PolygonSpriteBatch(final int size) {
        this(size, size * 2, null);
    }
    
    public PolygonSpriteBatch(final int size, final ShaderProgram defaultShader) {
        this(size, size * 2, defaultShader);
    }
    
    public PolygonSpriteBatch(final int maxVertices, final int maxTriangles, final ShaderProgram defaultShader) {
        this.invTexWidth = 0.0f;
        this.invTexHeight = 0.0f;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.blendSrcFunc = 770;
        this.blendDstFunc = 771;
        this.blendSrcFuncAlpha = 770;
        this.blendDstFuncAlpha = 771;
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.colorPacked = Color.WHITE_FLOAT_BITS;
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.maxTrianglesInBatch = 0;
        if (maxVertices > 32767) {
            throw new IllegalArgumentException("Can't have more than 32767 vertices per batch: " + maxVertices);
        }
        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        }
        this.mesh = new Mesh(vertexDataType, false, maxVertices, maxTriangles * 3, new VertexAttribute[] { new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0") });
        this.vertices = new float[maxVertices * 9];
        this.triangles = new short[maxTriangles * 3];
        if (defaultShader == null) {
            this.shader = SpriteBatch.createDefaultShader();
            this.ownsShader = true;
        }
        else {
            this.shader = defaultShader;
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
    }
    
    @Override
    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.end must be called before begin.");
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
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before end.");
        }
        if (this.vertexIndex > 0) {
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
    public void setPackedColor(final float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        this.colorPacked = packedColor;
    }
    
    @Override
    public Color getColor() {
        return this.color;
    }
    
    @Override
    public float getPackedColor() {
        return this.colorPacked;
    }
    
    @Override
    public void draw(final PolygonRegion region, final float x, final float y) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;
        final Texture texture = region.region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + regionTrianglesLength > triangles.length || this.vertexIndex + regionVerticesLength * 9 / 2 > this.vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / 9;
        for (int i = 0; i < regionTrianglesLength; ++i) {
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        }
        this.triangleIndex = triangleIndex;
        final float[] vertices = this.vertices;
        final float color = this.colorPacked;
        final float[] textureCoords = region.textureCoords;
        for (int j = 0; j < regionVerticesLength; j += 2) {
            vertices[vertexIndex++] = regionVertices[j] + x;
            vertices[vertexIndex++] = regionVertices[j + 1] + y;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[j];
            vertices[vertexIndex++] = textureCoords[j + 1];
        }
        this.vertexIndex = vertexIndex;
    }
    
    @Override
    public void draw(final PolygonRegion region, final float x, final float y, final float width, final float height) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;
        final TextureRegion textureRegion = region.region;
        final Texture texture = textureRegion.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + regionTrianglesLength > triangles.length || this.vertexIndex + regionVerticesLength * 9 / 2 > this.vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / 9;
        for (int i = 0, n = regionTriangles.length; i < n; ++i) {
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        }
        this.triangleIndex = triangleIndex;
        final float[] vertices = this.vertices;
        final float color = this.colorPacked;
        final float[] textureCoords = region.textureCoords;
        final float sX = width / textureRegion.regionWidth;
        final float sY = height / textureRegion.regionHeight;
        for (int j = 0; j < regionVerticesLength; j += 2) {
            vertices[vertexIndex++] = regionVertices[j] * sX + x;
            vertices[vertexIndex++] = regionVertices[j + 1] * sY + y;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[j];
            vertices[vertexIndex++] = textureCoords[j + 1];
        }
        this.vertexIndex = vertexIndex;
    }
    
    @Override
    public void draw(final PolygonRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final short[] regionTriangles = region.triangles;
        final int regionTrianglesLength = regionTriangles.length;
        final float[] regionVertices = region.vertices;
        final int regionVerticesLength = regionVertices.length;
        final TextureRegion textureRegion = region.region;
        final Texture texture = textureRegion.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + regionTrianglesLength > triangles.length || this.vertexIndex + regionVerticesLength * 9 / 2 > this.vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / 9;
        for (int i = 0; i < regionTrianglesLength; ++i) {
            triangles[triangleIndex++] = (short)(regionTriangles[i] + startVertex);
        }
        this.triangleIndex = triangleIndex;
        final float[] vertices = this.vertices;
        final float color = this.colorPacked;
        final float[] textureCoords = region.textureCoords;
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        final float sX = width / textureRegion.regionWidth;
        final float sY = height / textureRegion.regionHeight;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);
        for (int j = 0; j < regionVerticesLength; j += 2) {
            final float fx = (regionVertices[j] * sX - originX) * scaleX;
            final float fy = (regionVertices[j + 1] * sY - originY) * scaleY;
            vertices[vertexIndex++] = cos * fx - sin * fy + worldOriginX;
            vertices[vertexIndex++] = sin * fx + cos * fy + worldOriginY;
            vertices[vertexIndex++] = color;
            vertices[vertexIndex++] = textureCoords[j];
            vertices[vertexIndex++] = textureCoords[j + 1];
        }
        this.vertexIndex = vertexIndex;
    }
    
    @Override
    public void draw(final Texture texture, final float[] polygonVertices, final int verticesOffset, final int verticesCount, final short[] polygonTriangles, final int trianglesOffset, final int trianglesCount) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + trianglesCount > triangles.length || this.vertexIndex + verticesCount > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int vertexIndex = this.vertexIndex;
        final int startVertex = vertexIndex / 9;
        for (int i = trianglesOffset, n = i + trianglesCount; i < n; ++i) {
            triangles[triangleIndex++] = (short)(polygonTriangles[i] + startVertex);
        }
        this.triangleIndex = triangleIndex;
        System.arraycopy(polygonVertices, verticesOffset, vertices, vertexIndex, verticesCount);
        this.vertexIndex += verticesCount;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
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
        int idx = this.vertexIndex;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x5;
        vertices[idx++] = y5;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
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
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final int srcX, final int srcY, final int srcWidth, final int srcHeight) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
        final float u = srcX * this.invTexWidth;
        final float v = (srcY + srcHeight) * this.invTexHeight;
        final float u2 = (srcX + srcWidth) * this.invTexWidth;
        final float v2 = srcY * this.invTexHeight;
        final float fx2 = x + srcWidth;
        final float fy2 = y + srcHeight;
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height, final float u, final float v, final float u2, final float v2) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y) {
        this.draw(texture, x, y, (float)texture.getWidth(), (float)texture.getHeight());
    }
    
    @Override
    public void draw(final Texture texture, final float x, final float y, final float width, final float height) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = 0.0f;
        final float v = 1.0f;
        final float u2 = 1.0f;
        final float v2 = 0.0f;
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = 0.0f;
        vertices[idx++] = 1.0f;
        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = 0.0f;
        vertices[idx++] = 0.0f;
        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = 1.0f;
        vertices[idx++] = 0.0f;
        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = 1.0f;
        vertices[idx++] = 1.0f;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final Texture texture, final float[] spriteVertices, final int offset, final int count) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        final int triangleCount = count / 36 * 6;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + triangleCount > triangles.length || this.vertexIndex + count > vertices.length) {
            this.flush();
        }
        final int vertexIndex = this.vertexIndex;
        int triangleIndex = this.triangleIndex;
        short vertex = (short)(vertexIndex / 9);
        for (int n = triangleIndex + triangleCount; triangleIndex < n; triangleIndex += 6, vertex += 4) {
            triangles[triangleIndex] = vertex;
            triangles[triangleIndex + 1] = (short)(vertex + 1);
            triangles[triangleIndex + 2] = (short)(vertex + 2);
            triangles[triangleIndex + 3] = (short)(vertex + 2);
            triangles[triangleIndex + 4] = (short)(vertex + 3);
            triangles[triangleIndex + 5] = vertex;
        }
        this.triangleIndex = triangleIndex;
        System.arraycopy(spriteVertices, offset, vertices, vertexIndex, count);
        this.vertexIndex += count;
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y) {
        this.draw(region, x, y, (float)region.getRegionWidth(), (float)region.getRegionHeight());
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float width, final float height) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = fy2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = fx2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
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
        final float color = this.colorPacked;
        int idx = this.vertexIndex;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x5;
        vertices[idx++] = y5;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final boolean clockwise) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
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
        int idx = this.vertexIndex;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u1;
        vertices[idx++] = v1;
        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u3;
        vertices[idx++] = v3;
        vertices[idx++] = x5;
        vertices[idx++] = y5;
        vertices[idx++] = color;
        vertices[idx++] = u4;
        vertices[idx++] = v4;
        this.vertexIndex = idx;
    }
    
    @Override
    public void draw(final TextureRegion region, final float width, final float height, final Affine2 transform) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        final short[] triangles = this.triangles;
        final float[] vertices = this.vertices;
        final Texture texture = region.texture;
        if (texture != this.lastTexture) {
            this.switchTexture(texture);
        }
        else if (this.triangleIndex + 6 > triangles.length || this.vertexIndex + 36 > vertices.length) {
            this.flush();
        }
        int triangleIndex = this.triangleIndex;
        final int startVertex = this.vertexIndex / 9;
        triangles[triangleIndex++] = (short)startVertex;
        triangles[triangleIndex++] = (short)(startVertex + 1);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 2);
        triangles[triangleIndex++] = (short)(startVertex + 3);
        triangles[triangleIndex++] = (short)startVertex;
        this.triangleIndex = triangleIndex;
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
        int idx = this.vertexIndex;
        vertices[idx++] = x1;
        vertices[idx++] = y1;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x3;
        vertices[idx++] = y3;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x4;
        vertices[idx++] = y4;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v;
        this.vertexIndex = idx;
    }
    
    @Override
    public void flush() {
        if (this.vertexIndex == 0) {
            return;
        }
        ++this.renderCalls;
        ++this.totalRenderCalls;
        final int trianglesInBatch = this.triangleIndex;
        if (trianglesInBatch > this.maxTrianglesInBatch) {
            this.maxTrianglesInBatch = trianglesInBatch;
        }
        this.lastTexture.bind();
        final Mesh mesh = this.mesh;
        mesh.setVertices(this.vertices, 0, this.vertexIndex);
        mesh.setIndices(this.triangles, 0, this.triangleIndex);
        if (this.blendingDisabled) {
            Gdx.gl.glDisable(3042);
        }
        else {
            Gdx.gl.glEnable(3042);
            if (this.blendSrcFunc != -1) {
                Gdx.gl.glBlendFuncSeparate(this.blendSrcFunc, this.blendDstFunc, this.blendSrcFuncAlpha, this.blendDstFuncAlpha);
            }
        }
        mesh.render((this.customShader != null) ? this.customShader : this.shader, 4, 0, trianglesInBatch);
        this.vertexIndex = 0;
        this.triangleIndex = 0;
    }
    
    @Override
    public void disableBlending() {
        this.flush();
        this.blendingDisabled = true;
    }
    
    @Override
    public void enableBlending() {
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
        if (this.customShader != null) {
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
        }
        else {
            this.shader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.shader.setUniformi("u_texture", 0);
        }
    }
    
    private void switchTexture(final Texture texture) {
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
