// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.MapGroupLayer;
import java.util.Iterator;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;

public abstract class BatchTiledMapRenderer implements TiledMapRenderer, Disposable
{
    protected static final int NUM_VERTICES = 20;
    protected TiledMap map;
    protected float unitScale;
    protected Batch batch;
    protected Rectangle viewBounds;
    protected Rectangle imageBounds;
    protected boolean ownsBatch;
    protected float[] vertices;
    
    public TiledMap getMap() {
        return this.map;
    }
    
    public void setMap(final TiledMap map) {
        this.map = map;
    }
    
    public float getUnitScale() {
        return this.unitScale;
    }
    
    public Batch getBatch() {
        return this.batch;
    }
    
    public Rectangle getViewBounds() {
        return this.viewBounds;
    }
    
    public BatchTiledMapRenderer(final TiledMap map) {
        this(map, 1.0f);
    }
    
    public BatchTiledMapRenderer(final TiledMap map, final float unitScale) {
        this.imageBounds = new Rectangle();
        this.vertices = new float[20];
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = new SpriteBatch();
        this.ownsBatch = true;
    }
    
    public BatchTiledMapRenderer(final TiledMap map, final Batch batch) {
        this(map, 1.0f, batch);
    }
    
    public BatchTiledMapRenderer(final TiledMap map, final float unitScale, final Batch batch) {
        this.imageBounds = new Rectangle();
        this.vertices = new float[20];
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = batch;
        this.ownsBatch = false;
    }
    
    @Override
    public void setView(final OrthographicCamera camera) {
        this.batch.setProjectionMatrix(camera.combined);
        final float width = camera.viewportWidth * camera.zoom;
        final float height = camera.viewportHeight * camera.zoom;
        final float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
        final float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        this.viewBounds.set(camera.position.x - w / 2.0f, camera.position.y - h / 2.0f, w, h);
    }
    
    @Override
    public void setView(final Matrix4 projection, final float x, final float y, final float width, final float height) {
        this.batch.setProjectionMatrix(projection);
        this.viewBounds.set(x, y, width, height);
    }
    
    @Override
    public void render() {
        this.beginRender();
        for (final MapLayer layer : this.map.getLayers()) {
            this.renderMapLayer(layer);
        }
        this.endRender();
    }
    
    @Override
    public void render(final int[] layers) {
        this.beginRender();
        for (final int layerIdx : layers) {
            final MapLayer layer = this.map.getLayers().get(layerIdx);
            this.renderMapLayer(layer);
        }
        this.endRender();
    }
    
    protected void renderMapLayer(final MapLayer layer) {
        if (!layer.isVisible()) {
            return;
        }
        if (layer instanceof MapGroupLayer) {
            final MapLayers childLayers = ((MapGroupLayer)layer).getLayers();
            for (int i = 0; i < childLayers.size(); ++i) {
                final MapLayer childLayer = childLayers.get(i);
                if (childLayer.isVisible()) {
                    this.renderMapLayer(childLayer);
                }
            }
        }
        else if (layer instanceof TiledMapTileLayer) {
            this.renderTileLayer((TiledMapTileLayer)layer);
        }
        else if (layer instanceof TiledMapImageLayer) {
            this.renderImageLayer((TiledMapImageLayer)layer);
        }
        else {
            this.renderObjects(layer);
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
    public void renderImageLayer(final TiledMapImageLayer layer) {
        final Color batchColor = this.batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
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
        this.imageBounds.set(x2, y2, x3 - x2, y3 - y2);
        if (this.viewBounds.contains(this.imageBounds) || this.viewBounds.overlaps(this.imageBounds)) {
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
            this.batch.draw(region.getTexture(), vertices, 0, 20);
        }
    }
    
    protected void beginRender() {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        this.batch.begin();
    }
    
    protected void endRender() {
        this.batch.end();
    }
    
    @Override
    public void dispose() {
        if (this.ownsBatch) {
            this.batch.dispose();
        }
    }
}
