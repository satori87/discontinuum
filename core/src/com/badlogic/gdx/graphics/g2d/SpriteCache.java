// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import java.nio.FloatBuffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.utils.Disposable;

public class SpriteCache implements Disposable
{
    private static final float[] tempVertices;
    private final Mesh mesh;
    private boolean drawing;
    private final Matrix4 transformMatrix;
    private final Matrix4 projectionMatrix;
    private Array<Cache> caches;
    private final Matrix4 combinedMatrix;
    private final ShaderProgram shader;
    private Cache currentCache;
    private final Array<Texture> textures;
    private final IntArray counts;
    private final Color color;
    private float colorPacked;
    private ShaderProgram customShader;
    public int renderCalls;
    public int totalRenderCalls;
    
    static {
        tempVertices = new float[54];
    }
    
    public SpriteCache() {
        this(1000, false);
    }
    
    public SpriteCache(final int size, final boolean useIndices) {
        this(size, createDefaultShader(), useIndices);
    }
    
    public SpriteCache(final int size, final ShaderProgram shader, final boolean useIndices) {
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.caches = new Array<Cache>();
        this.combinedMatrix = new Matrix4();
        this.textures = new Array<Texture>(8);
        this.counts = new IntArray(8);
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.colorPacked = Color.WHITE_FLOAT_BITS;
        this.customShader = null;
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.shader = shader;
        if (useIndices && size > 8191) {
            throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);
        }
        (this.mesh = new Mesh(true, size * (useIndices ? 4 : 6), useIndices ? (size * 6) : 0, new VertexAttribute[] { new VertexAttribute(1, 2, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0") })).setAutoBind(false);
        if (useIndices) {
            final int length = size * 6;
            final short[] indices = new short[length];
            short j = 0;
            for (int i = 0; i < length; i += 6, j += 4) {
                indices[i + 0] = j;
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = j;
            }
            this.mesh.setIndices(indices);
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
    }
    
    public void setColor(final Color tint) {
        this.color.set(tint);
        this.colorPacked = tint.toFloatBits();
    }
    
    public void setColor(final float r, final float g, final float b, final float a) {
        this.color.set(r, g, b, a);
        this.colorPacked = this.color.toFloatBits();
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setPackedColor(final float packedColor) {
        Color.abgr8888ToColor(this.color, packedColor);
        this.colorPacked = packedColor;
    }
    
    public float getPackedColor() {
        return this.colorPacked;
    }
    
    public void beginCache() {
        if (this.drawing) {
            throw new IllegalStateException("end must be called before beginCache");
        }
        if (this.currentCache != null) {
            throw new IllegalStateException("endCache must be called before begin.");
        }
        final int verticesPerImage = (this.mesh.getNumIndices() > 0) ? 4 : 6;
        this.currentCache = new Cache(this.caches.size, this.mesh.getVerticesBuffer().limit());
        this.caches.add(this.currentCache);
        this.mesh.getVerticesBuffer().compact();
    }
    
    public void beginCache(final int cacheID) {
        if (this.drawing) {
            throw new IllegalStateException("end must be called before beginCache");
        }
        if (this.currentCache != null) {
            throw new IllegalStateException("endCache must be called before begin.");
        }
        if (cacheID == this.caches.size - 1) {
            final Cache oldCache = this.caches.removeIndex(cacheID);
            this.mesh.getVerticesBuffer().limit(oldCache.offset);
            this.beginCache();
            return;
        }
        this.currentCache = this.caches.get(cacheID);
        this.mesh.getVerticesBuffer().position(this.currentCache.offset);
    }
    
    public int endCache() {
        if (this.currentCache == null) {
            throw new IllegalStateException("beginCache must be called before endCache.");
        }
        final Cache cache = this.currentCache;
        final int cacheCount = this.mesh.getVerticesBuffer().position() - cache.offset;
        if (cache.textures == null) {
            cache.maxCount = cacheCount;
            cache.textureCount = this.textures.size;
            cache.textures = this.textures.toArray(Texture.class);
            cache.counts = new int[cache.textureCount];
            for (int i = 0, n = this.counts.size; i < n; ++i) {
                cache.counts[i] = this.counts.get(i);
            }
            this.mesh.getVerticesBuffer().flip();
        }
        else {
            if (cacheCount > cache.maxCount) {
                throw new GdxRuntimeException("If a cache is not the last created, it cannot be redefined with more entries than when it was first created: " + cacheCount + " (" + cache.maxCount + " max)");
            }
            cache.textureCount = this.textures.size;
            if (cache.textures.length < cache.textureCount) {
                cache.textures = new Texture[cache.textureCount];
            }
            for (int i = 0, n = cache.textureCount; i < n; ++i) {
                cache.textures[i] = this.textures.get(i);
            }
            if (cache.counts.length < cache.textureCount) {
                cache.counts = new int[cache.textureCount];
            }
            for (int i = 0, n = cache.textureCount; i < n; ++i) {
                cache.counts[i] = this.counts.get(i);
            }
            final FloatBuffer vertices = this.mesh.getVerticesBuffer();
            vertices.position(0);
            final Cache lastCache = this.caches.get(this.caches.size - 1);
            vertices.limit(lastCache.offset + lastCache.maxCount);
        }
        this.currentCache = null;
        this.textures.clear();
        this.counts.clear();
        return cache.id;
    }
    
    public void clear() {
        this.caches.clear();
        this.mesh.getVerticesBuffer().clear().flip();
    }
    
    public void add(final Texture texture, final float[] vertices, final int offset, final int length) {
        if (this.currentCache == null) {
            throw new IllegalStateException("beginCache must be called before add.");
        }
        final int verticesPerImage = (this.mesh.getNumIndices() > 0) ? 4 : 6;
        final int count = length / (verticesPerImage * 9) * 6;
        final int lastIndex = this.textures.size - 1;
        if (lastIndex < 0 || this.textures.get(lastIndex) != texture) {
            this.textures.add(texture);
            this.counts.add(count);
        }
        else {
            this.counts.incr(lastIndex, count);
        }
        this.mesh.getVerticesBuffer().put(vertices, offset, length);
    }
    
    public void add(final Texture texture, final float x, final float y) {
        final float fx2 = x + texture.getWidth();
        final float fy2 = y + texture.getHeight();
        SpriteCache.tempVertices[0] = x;
        SpriteCache.tempVertices[1] = y;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = 0.0f;
        SpriteCache.tempVertices[4] = 1.0f;
        SpriteCache.tempVertices[5] = x;
        SpriteCache.tempVertices[6] = fy2;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = 0.0f;
        SpriteCache.tempVertices[9] = 0.0f;
        SpriteCache.tempVertices[10] = fx2;
        SpriteCache.tempVertices[11] = fy2;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = 1.0f;
        SpriteCache.tempVertices[14] = 0.0f;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = y;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = 1.0f;
            SpriteCache.tempVertices[19] = 1.0f;
            this.add(texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = fy2;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = 1.0f;
            SpriteCache.tempVertices[19] = 0.0f;
            SpriteCache.tempVertices[20] = fx2;
            SpriteCache.tempVertices[21] = y;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = 1.0f;
            SpriteCache.tempVertices[24] = 1.0f;
            SpriteCache.tempVertices[25] = x;
            SpriteCache.tempVertices[26] = y;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = 0.0f;
            SpriteCache.tempVertices[29] = 1.0f;
            this.add(texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final Texture texture, final float x, final float y, final int srcWidth, final int srcHeight, final float u, final float v, final float u2, final float v2, final float color) {
        final float fx2 = x + srcWidth;
        final float fy2 = y + srcHeight;
        SpriteCache.tempVertices[0] = x;
        SpriteCache.tempVertices[1] = y;
        SpriteCache.tempVertices[2] = color;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x;
        SpriteCache.tempVertices[6] = fy2;
        SpriteCache.tempVertices[7] = color;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = fx2;
        SpriteCache.tempVertices[11] = fy2;
        SpriteCache.tempVertices[12] = color;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = y;
            SpriteCache.tempVertices[17] = color;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = fy2;
            SpriteCache.tempVertices[17] = color;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = fx2;
            SpriteCache.tempVertices[21] = y;
            SpriteCache.tempVertices[22] = color;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x;
            SpriteCache.tempVertices[26] = y;
            SpriteCache.tempVertices[27] = color;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final Texture texture, final float x, final float y, final int srcX, final int srcY, final int srcWidth, final int srcHeight) {
        final float invTexWidth = 1.0f / texture.getWidth();
        final float invTexHeight = 1.0f / texture.getHeight();
        final float u = srcX * invTexWidth;
        final float v = (srcY + srcHeight) * invTexHeight;
        final float u2 = (srcX + srcWidth) * invTexWidth;
        final float v2 = srcY * invTexHeight;
        final float fx2 = x + srcWidth;
        final float fy2 = y + srcHeight;
        SpriteCache.tempVertices[0] = x;
        SpriteCache.tempVertices[1] = y;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x;
        SpriteCache.tempVertices[6] = fy2;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = fx2;
        SpriteCache.tempVertices[11] = fy2;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = y;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = fy2;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = fx2;
            SpriteCache.tempVertices[21] = y;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x;
            SpriteCache.tempVertices[26] = y;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final Texture texture, final float x, final float y, final float width, final float height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
        final float invTexWidth = 1.0f / texture.getWidth();
        final float invTexHeight = 1.0f / texture.getHeight();
        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;
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
        SpriteCache.tempVertices[0] = x;
        SpriteCache.tempVertices[1] = y;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x;
        SpriteCache.tempVertices[6] = fy2;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = fx2;
        SpriteCache.tempVertices[11] = fy2;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = y;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = fy2;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = fx2;
            SpriteCache.tempVertices[21] = y;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x;
            SpriteCache.tempVertices[26] = y;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final Texture texture, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation, final int srcX, final int srcY, final int srcWidth, final int srcHeight, final boolean flipX, final boolean flipY) {
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
        final float invTexWidth = 1.0f / texture.getWidth();
        final float invTexHeight = 1.0f / texture.getHeight();
        float u = srcX * invTexWidth;
        float v = (srcY + srcHeight) * invTexHeight;
        float u2 = (srcX + srcWidth) * invTexWidth;
        float v2 = srcY * invTexHeight;
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
        SpriteCache.tempVertices[0] = x2;
        SpriteCache.tempVertices[1] = y2;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x3;
        SpriteCache.tempVertices[6] = y3;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = x4;
        SpriteCache.tempVertices[11] = y4;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = x5;
            SpriteCache.tempVertices[16] = y5;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = x4;
            SpriteCache.tempVertices[16] = y4;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = x5;
            SpriteCache.tempVertices[21] = y5;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x2;
            SpriteCache.tempVertices[26] = y2;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final TextureRegion region, final float x, final float y) {
        this.add(region, x, y, (float)region.getRegionWidth(), (float)region.getRegionHeight());
    }
    
    public void add(final TextureRegion region, final float x, final float y, final float width, final float height) {
        final float fx2 = x + width;
        final float fy2 = y + height;
        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;
        SpriteCache.tempVertices[0] = x;
        SpriteCache.tempVertices[1] = y;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x;
        SpriteCache.tempVertices[6] = fy2;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = fx2;
        SpriteCache.tempVertices[11] = fy2;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = y;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(region.texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = fx2;
            SpriteCache.tempVertices[16] = fy2;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = fx2;
            SpriteCache.tempVertices[21] = y;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x;
            SpriteCache.tempVertices[26] = y;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(region.texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final TextureRegion region, final float x, final float y, final float originX, final float originY, final float width, final float height, final float scaleX, final float scaleY, final float rotation) {
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
        SpriteCache.tempVertices[0] = x2;
        SpriteCache.tempVertices[1] = y2;
        SpriteCache.tempVertices[2] = this.colorPacked;
        SpriteCache.tempVertices[3] = u;
        SpriteCache.tempVertices[4] = v;
        SpriteCache.tempVertices[5] = x3;
        SpriteCache.tempVertices[6] = y3;
        SpriteCache.tempVertices[7] = this.colorPacked;
        SpriteCache.tempVertices[8] = u;
        SpriteCache.tempVertices[9] = v2;
        SpriteCache.tempVertices[10] = x4;
        SpriteCache.tempVertices[11] = y4;
        SpriteCache.tempVertices[12] = this.colorPacked;
        SpriteCache.tempVertices[13] = u2;
        SpriteCache.tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            SpriteCache.tempVertices[15] = x5;
            SpriteCache.tempVertices[16] = y5;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v;
            this.add(region.texture, SpriteCache.tempVertices, 0, 20);
        }
        else {
            SpriteCache.tempVertices[15] = x4;
            SpriteCache.tempVertices[16] = y4;
            SpriteCache.tempVertices[17] = this.colorPacked;
            SpriteCache.tempVertices[18] = u2;
            SpriteCache.tempVertices[19] = v2;
            SpriteCache.tempVertices[20] = x5;
            SpriteCache.tempVertices[21] = y5;
            SpriteCache.tempVertices[22] = this.colorPacked;
            SpriteCache.tempVertices[23] = u2;
            SpriteCache.tempVertices[24] = v;
            SpriteCache.tempVertices[25] = x2;
            SpriteCache.tempVertices[26] = y2;
            SpriteCache.tempVertices[27] = this.colorPacked;
            SpriteCache.tempVertices[28] = u;
            SpriteCache.tempVertices[29] = v;
            this.add(region.texture, SpriteCache.tempVertices, 0, 30);
        }
    }
    
    public void add(final Sprite sprite) {
        if (this.mesh.getNumIndices() > 0) {
            this.add(sprite.getTexture(), sprite.getVertices(), 0, 36);
            return;
        }
        final float[] spriteVertices = sprite.getVertices();
        System.arraycopy(spriteVertices, 0, SpriteCache.tempVertices, 0, 27);
        System.arraycopy(spriteVertices, 18, SpriteCache.tempVertices, 27, 9);
        System.arraycopy(spriteVertices, 27, SpriteCache.tempVertices, 36, 9);
        System.arraycopy(spriteVertices, 0, SpriteCache.tempVertices, 45, 9);
        this.add(sprite.getTexture(), SpriteCache.tempVertices, 0, 30);
    }
    
    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("end must be called before begin.");
        }
        if (this.currentCache != null) {
            throw new IllegalStateException("endCache must be called before begin");
        }
        this.renderCalls = 0;
        this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
        Gdx.gl20.glDepthMask(false);
        if (this.customShader != null) {
            this.customShader.begin();
            this.customShader.setUniformMatrix("u_proj", this.projectionMatrix);
            this.customShader.setUniformMatrix("u_trans", this.transformMatrix);
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
            this.mesh.bind(this.customShader);
        }
        else {
            this.shader.begin();
            this.shader.setUniformMatrix("u_projectionViewMatrix", this.combinedMatrix);
            this.shader.setUniformi("u_texture", 0);
            this.mesh.bind(this.shader);
        }
        this.drawing = true;
    }
    
    public void end() {
        if (!this.drawing) {
            throw new IllegalStateException("begin must be called before end.");
        }
        this.drawing = false;
        this.shader.end();
        final GL20 gl = Gdx.gl20;
        gl.glDepthMask(true);
        if (this.customShader != null) {
            this.mesh.unbind(this.customShader);
        }
        else {
            this.mesh.unbind(this.shader);
        }
    }
    
    public void draw(final int cacheID) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteCache.begin must be called before draw.");
        }
        final Cache cache = this.caches.get(cacheID);
        final int verticesPerImage = (this.mesh.getNumIndices() > 0) ? 4 : 6;
        int offset = cache.offset / (verticesPerImage * 9) * 6;
        final Texture[] textures = cache.textures;
        final int[] counts = cache.counts;
        final int textureCount = cache.textureCount;
        for (int i = 0; i < textureCount; ++i) {
            final int count = counts[i];
            textures[i].bind();
            if (this.customShader != null) {
                this.mesh.render(this.customShader, 4, offset, count);
            }
            else {
                this.mesh.render(this.shader, 4, offset, count);
            }
            offset += count;
        }
        this.renderCalls += textureCount;
        this.totalRenderCalls += textureCount;
    }
    
    public void draw(final int cacheID, int offset, int length) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteCache.begin must be called before draw.");
        }
        final Cache cache = this.caches.get(cacheID);
        offset = offset * 6 + cache.offset;
        length *= 6;
        final Texture[] textures = cache.textures;
        final int[] counts = cache.counts;
        final int textureCount = cache.textureCount;
        for (int i = 0; i < textureCount; ++i) {
            textures[i].bind();
            int count = counts[i];
            if (count > length) {
                i = textureCount;
                count = length;
            }
            else {
                length -= count;
            }
            if (this.customShader != null) {
                this.mesh.render(this.customShader, 4, offset, count);
            }
            else {
                this.mesh.render(this.shader, 4, offset, count);
            }
            offset += count;
        }
        this.renderCalls += cache.textureCount;
        this.totalRenderCalls += textureCount;
    }
    
    @Override
    public void dispose() {
        this.mesh.dispose();
        if (this.shader != null) {
            this.shader.dispose();
        }
    }
    
    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }
    
    public void setProjectionMatrix(final Matrix4 projection) {
        if (this.drawing) {
            throw new IllegalStateException("Can't set the matrix within begin/end.");
        }
        this.projectionMatrix.set(projection);
    }
    
    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }
    
    public void setTransformMatrix(final Matrix4 transform) {
        if (this.drawing) {
            throw new IllegalStateException("Can't set the matrix within begin/end.");
        }
        this.transformMatrix.set(transform);
    }
    
    static ShaderProgram createDefaultShader() {
        final String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_color.a = v_color.a * (255.0/254.0);\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n";
        final String fragmentShader = "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}";
        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        return shader;
    }
    
    public void setShader(final ShaderProgram shader) {
        this.customShader = shader;
    }
    
    private static class Cache
    {
        final int id;
        final int offset;
        int maxCount;
        int textureCount;
        Texture[] textures;
        int[] counts;
        
        public Cache(final int id, final int offset) {
            this.id = id;
            this.offset = offset;
        }
    }
}
