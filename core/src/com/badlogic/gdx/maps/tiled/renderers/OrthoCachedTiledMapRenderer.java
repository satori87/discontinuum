// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapLayers;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;

public class OrthoCachedTiledMapRenderer implements TiledMapRenderer, Disposable
{
    private static final float tolerance = 1.0E-5f;
    protected static final int NUM_VERTICES = 20;
    protected final TiledMap map;
    protected final SpriteCache spriteCache;
    protected final float[] vertices;
    protected boolean blending;
    protected float unitScale;
    protected final Rectangle viewBounds;
    protected final Rectangle cacheBounds;
    protected float overCache;
    protected float maxTileWidth;
    protected float maxTileHeight;
    protected boolean cached;
    protected int count;
    protected boolean canCacheMoreN;
    protected boolean canCacheMoreE;
    protected boolean canCacheMoreW;
    protected boolean canCacheMoreS;
    
    public OrthoCachedTiledMapRenderer(final TiledMap map) {
        this(map, 1.0f, 2000);
    }
    
    public OrthoCachedTiledMapRenderer(final TiledMap map, final float unitScale) {
        this(map, unitScale, 2000);
    }
    
    public OrthoCachedTiledMapRenderer(final TiledMap map, final float unitScale, final int cacheSize) {
        this.vertices = new float[20];
        this.viewBounds = new Rectangle();
        this.cacheBounds = new Rectangle();
        this.overCache = 0.5f;
        this.map = map;
        this.unitScale = unitScale;
        this.spriteCache = new SpriteCache(cacheSize, true);
    }
    
    @Override
    public void setView(final OrthographicCamera camera) {
        this.spriteCache.setProjectionMatrix(camera.combined);
        final float width = camera.viewportWidth * camera.zoom + this.maxTileWidth * 2.0f * this.unitScale;
        final float height = camera.viewportHeight * camera.zoom + this.maxTileHeight * 2.0f * this.unitScale;
        this.viewBounds.set(camera.position.x - width / 2.0f, camera.position.y - height / 2.0f, width, height);
        if ((this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - 1.0E-5f) || (this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - 1.0E-5f) || (this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + 1.0E-5f) || (this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + 1.0E-5f)) {
            this.cached = false;
        }
    }
    
    @Override
    public void setView(final Matrix4 projection, float x, float y, float width, float height) {
        this.spriteCache.setProjectionMatrix(projection);
        x -= this.maxTileWidth * this.unitScale;
        y -= this.maxTileHeight * this.unitScale;
        width += this.maxTileWidth * 2.0f * this.unitScale;
        height += this.maxTileHeight * 2.0f * this.unitScale;
        this.viewBounds.set(x, y, width, height);
        if ((this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - 1.0E-5f) || (this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - 1.0E-5f) || (this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + 1.0E-5f) || (this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + 1.0E-5f)) {
            this.cached = false;
        }
    }
    
    @Override
    public void render() {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            final float extraWidth = this.viewBounds.width * this.overCache;
            final float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + extraWidth * 2.0f;
            this.cacheBounds.height = this.viewBounds.height + extraHeight * 2.0f;
            for (final MapLayer layer : this.map.getLayers()) {
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    this.renderTileLayer((TiledMapTileLayer)layer);
                }
                else if (layer instanceof TiledMapImageLayer) {
                    this.renderImageLayer((TiledMapImageLayer)layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
        }
        this.spriteCache.begin();
        final MapLayers mapLayers = this.map.getLayers();
        for (int i = 0, j = mapLayers.getCount(); i < j; ++i) {
            final MapLayer layer2 = mapLayers.get(i);
            if (layer2.isVisible()) {
                this.spriteCache.draw(i);
                this.renderObjects(layer2);
            }
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(3042);
        }
    }
    
    @Override
    public void render(final int[] layers) {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            final float extraWidth = this.viewBounds.width * this.overCache;
            final float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + extraWidth * 2.0f;
            this.cacheBounds.height = this.viewBounds.height + extraHeight * 2.0f;
            for (final MapLayer layer : this.map.getLayers()) {
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    this.renderTileLayer((TiledMapTileLayer)layer);
                }
                else if (layer instanceof TiledMapImageLayer) {
                    this.renderImageLayer((TiledMapImageLayer)layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
        }
        this.spriteCache.begin();
        final MapLayers mapLayers = this.map.getLayers();
        for (final int i : layers) {
            final MapLayer layer2 = mapLayers.get(i);
            if (layer2.isVisible()) {
                this.spriteCache.draw(i);
                this.renderObjects(layer2);
            }
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(3042);
        }
    }
    
    @Override
    public void renderObjects(final MapLayer layer) {
        for (final MapObject object : layer.getObjects()) {
            this.renderObject(object);
        }
    }
    
    @Override
    public void renderObject(final MapObject object) {
    }
    
    @Override
    public void renderTileLayer(final TiledMapTileLayer layer) {
        final float color = Color.toFloatBits(1.0f, 1.0f, 1.0f, layer.getOpacity());
        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();
        final float layerTileWidth = layer.getTileWidth() * this.unitScale;
        final float layerTileHeight = layer.getTileHeight() * this.unitScale;
        final float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        final float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale;
        final int col1 = Math.max(0, (int)((this.cacheBounds.x - layerOffsetX) / layerTileWidth));
        final int col2 = Math.min(layerWidth, (int)((this.cacheBounds.x + this.cacheBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));
        final int row1 = Math.max(0, (int)((this.cacheBounds.y - layerOffsetY) / layerTileHeight));
        final int row2 = Math.min(layerHeight, (int)((this.cacheBounds.y + this.cacheBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));
        this.canCacheMoreN = (row2 < layerHeight);
        this.canCacheMoreE = (col2 < layerWidth);
        this.canCacheMoreW = (col1 > 0);
        this.canCacheMoreS = (row1 > 0);
        final float[] vertices = this.vertices;
        for (int row3 = row2; row3 >= row1; --row3) {
            for (int col3 = col1; col3 < col2; ++col3) {
                final TiledMapTileLayer.Cell cell = layer.getCell(col3, row3);
                if (cell != null) {
                    final TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        ++this.count;
                        final boolean flipX = cell.getFlipHorizontally();
                        final boolean flipY = cell.getFlipVertically();
                        final int rotations = cell.getRotation();
                        final TextureRegion region = tile.getTextureRegion();
                        final Texture texture = region.getTexture();
                        final float x1 = col3 * layerTileWidth + tile.getOffsetX() * this.unitScale + layerOffsetX;
                        final float y1 = row3 * layerTileHeight + tile.getOffsetY() * this.unitScale + layerOffsetY;
                        final float x2 = x1 + region.getRegionWidth() * this.unitScale;
                        final float y2 = y1 + region.getRegionHeight() * this.unitScale;
                        final float adjustX = 0.5f / texture.getWidth();
                        final float adjustY = 0.5f / texture.getHeight();
                        final float u1 = region.getU() + adjustX;
                        final float v1 = region.getV2() - adjustY;
                        final float u2 = region.getU2() - adjustX;
                        final float v2 = region.getV() + adjustY;
                        vertices[0] = x1;
                        vertices[1] = y1;
                        vertices[2] = color;
                        vertices[3] = u1;
                        vertices[4] = v1;
                        vertices[9] = x1;
                        vertices[10] = y2;
                        vertices[11] = color;
                        vertices[12] = u1;
                        vertices[13] = v2;
                        vertices[18] = x2;
                        vertices[19] = y2;
                        vertices[20] = color;
                        vertices[21] = u2;
                        vertices[22] = v2;
                        vertices[27] = x2;
                        vertices[28] = y1;
                        vertices[29] = color;
                        vertices[30] = u2;
                        vertices[31] = v1;
                        if (flipX) {
                            float temp = vertices[3];
                            vertices[3] = vertices[21];
                            vertices[21] = temp;
                            temp = vertices[12];
                            vertices[12] = vertices[30];
                            vertices[30] = temp;
                        }
                        if (flipY) {
                            float temp = vertices[4];
                            vertices[4] = vertices[22];
                            vertices[22] = temp;
                            temp = vertices[13];
                            vertices[13] = vertices[31];
                            vertices[31] = temp;
                        }
                        if (rotations != 0) {
                            switch (rotations) {
                                case 1: {
                                    final float tempV = vertices[4];
                                    vertices[4] = vertices[13];
                                    vertices[13] = vertices[22];
                                    vertices[22] = vertices[31];
                                    vertices[31] = tempV;
                                    final float tempU = vertices[3];
                                    vertices[3] = vertices[12];
                                    vertices[12] = vertices[21];
                                    vertices[21] = vertices[30];
                                    vertices[30] = tempU;
                                    break;
                                }
                                case 2: {
                                    float tempU2 = vertices[3];
                                    vertices[3] = vertices[21];
                                    vertices[21] = tempU2;
                                    tempU2 = vertices[12];
                                    vertices[12] = vertices[30];
                                    vertices[30] = tempU2;
                                    float tempV2 = vertices[4];
                                    vertices[4] = vertices[22];
                                    vertices[22] = tempV2;
                                    tempV2 = vertices[13];
                                    vertices[13] = vertices[31];
                                    vertices[31] = tempV2;
                                    break;
                                }
                                case 3: {
                                    final float tempV = vertices[4];
                                    vertices[4] = vertices[31];
                                    vertices[31] = vertices[22];
                                    vertices[22] = vertices[13];
                                    vertices[13] = tempV;
                                    final float tempU = vertices[3];
                                    vertices[3] = vertices[30];
                                    vertices[30] = vertices[21];
                                    vertices[21] = vertices[12];
                                    vertices[12] = tempU;
                                    break;
                                }
                            }
                        }
                        this.spriteCache.add(texture, vertices, 0, 20);
                    }
                }
            }
        }
    }
    
    @Override
    public void renderImageLayer(final TiledMapImageLayer layer) {
        final float color = Color.toFloatBits(1.0f, 1.0f, 1.0f, layer.getOpacity());
        final float[] vertices = this.vertices;
        final TextureRegion region = layer.getTextureRegion();
        if (region == null) {
            return;
        }
        final float x = layer.getX();
        final float y = layer.getY();
        final float x2 = x * this.unitScale;
        final float y2 = y * this.unitScale;
        final float x3 = x2 + region.getRegionWidth() * this.unitScale;
        final float y3 = y2 + region.getRegionHeight() * this.unitScale;
        final float u1 = region.getU();
        final float v1 = region.getV2();
        final float u2 = region.getU2();
        final float v2 = region.getV();
        vertices[0] = x2;
        vertices[1] = y2;
        vertices[2] = color;
        vertices[3] = u1;
        vertices[4] = v1;
        vertices[9] = x2;
        vertices[10] = y3;
        vertices[11] = color;
        vertices[12] = u1;
        vertices[13] = v2;
        vertices[18] = x3;
        vertices[19] = y3;
        vertices[20] = color;
        vertices[21] = u2;
        vertices[22] = v2;
        vertices[27] = x3;
        vertices[28] = y2;
        vertices[29] = color;
        vertices[30] = u2;
        vertices[31] = v1;
        this.spriteCache.add(region.getTexture(), vertices, 0, 20);
    }
    
    public void invalidateCache() {
        this.cached = false;
    }
    
    public boolean isCached() {
        return this.cached;
    }
    
    public void setOverCache(final float overCache) {
        this.overCache = overCache;
    }
    
    public void setMaxTileSize(final float maxPixelWidth, final float maxPixelHeight) {
        this.maxTileWidth = maxPixelWidth;
        this.maxTileHeight = maxPixelHeight;
    }
    
    public void setBlending(final boolean blending) {
        this.blending = blending;
    }
    
    public SpriteCache getSpriteCache() {
        return this.spriteCache;
    }
    
    @Override
    public void dispose() {
        this.spriteCache.dispose();
    }
}
