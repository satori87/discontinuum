// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class IsometricStaggeredTiledMapRenderer extends BatchTiledMapRenderer
{
    public IsometricStaggeredTiledMapRenderer(final TiledMap map) {
        super(map);
    }
    
    public IsometricStaggeredTiledMapRenderer(final TiledMap map, final Batch batch) {
        super(map, batch);
    }
    
    public IsometricStaggeredTiledMapRenderer(final TiledMap map, final float unitScale) {
        super(map, unitScale);
    }
    
    public IsometricStaggeredTiledMapRenderer(final TiledMap map, final float unitScale, final Batch batch) {
        super(map, unitScale, batch);
    }
    
    @Override
    public void renderTileLayer(final TiledMapTileLayer layer) {
        final Color batchColor = this.batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();
        final float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        final float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale;
        final float layerTileWidth = layer.getTileWidth() * this.unitScale;
        final float layerTileHeight = layer.getTileHeight() * this.unitScale;
        final float layerTileWidth2 = layerTileWidth * 0.5f;
        final float layerTileHeight2 = layerTileHeight * 0.5f;
        final int minX = Math.max(0, (int)((this.viewBounds.x - layerTileWidth2 - layerOffsetX) / layerTileWidth));
        final int maxX = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + layerTileWidth + layerTileWidth2 - layerOffsetX) / layerTileWidth));
        final int minY = Math.max(0, (int)((this.viewBounds.y - layerTileHeight - layerOffsetY) / layerTileHeight));
        final int maxY = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight2));
        for (int y = maxY - 1; y >= minY; --y) {
            final float offsetX = (y % 2 == 1) ? layerTileWidth2 : 0.0f;
            for (int x = maxX - 1; x >= minX; --x) {
                final TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    final TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        final boolean flipX = cell.getFlipHorizontally();
                        final boolean flipY = cell.getFlipVertically();
                        final int rotations = cell.getRotation();
                        final TextureRegion region = tile.getTextureRegion();
                        final float x2 = x * layerTileWidth - offsetX + tile.getOffsetX() * this.unitScale + layerOffsetX;
                        final float y2 = y * layerTileHeight2 + tile.getOffsetY() * this.unitScale + layerOffsetY;
                        final float x3 = x2 + region.getRegionWidth() * this.unitScale;
                        final float y3 = y2 + region.getRegionHeight() * this.unitScale;
                        final float u1 = region.getU();
                        final float v1 = region.getV2();
                        final float u2 = region.getU2();
                        final float v2 = region.getV();
                        this.vertices[0] = x2;
                        this.vertices[1] = y2;
                        this.vertices[2] = color;
                        this.vertices[3] = u1;
                        this.vertices[4] = v1;
                        this.vertices[9] = x2;
                        this.vertices[10] = y3;
                        this.vertices[11] = color;
                        this.vertices[12] = u1;
                        this.vertices[13] = v2;
                        this.vertices[18] = x3;
                        this.vertices[19] = y3;
                        this.vertices[20] = color;
                        this.vertices[21] = u2;
                        this.vertices[22] = v2;
                        this.vertices[27] = x3;
                        this.vertices[28] = y2;
                        this.vertices[29] = color;
                        this.vertices[30] = u2;
                        this.vertices[31] = v1;
                        if (flipX) {
                            float temp = this.vertices[3];
                            this.vertices[3] = this.vertices[21];
                            this.vertices[21] = temp;
                            temp = this.vertices[12];
                            this.vertices[12] = this.vertices[30];
                            this.vertices[30] = temp;
                        }
                        if (flipY) {
                            float temp = this.vertices[4];
                            this.vertices[4] = this.vertices[22];
                            this.vertices[22] = temp;
                            temp = this.vertices[13];
                            this.vertices[13] = this.vertices[31];
                            this.vertices[31] = temp;
                        }
                        if (rotations != 0) {
                            switch (rotations) {
                                case 1: {
                                    final float tempV = this.vertices[4];
                                    this.vertices[4] = this.vertices[13];
                                    this.vertices[13] = this.vertices[22];
                                    this.vertices[22] = this.vertices[31];
                                    this.vertices[31] = tempV;
                                    final float tempU = this.vertices[3];
                                    this.vertices[3] = this.vertices[12];
                                    this.vertices[12] = this.vertices[21];
                                    this.vertices[21] = this.vertices[30];
                                    this.vertices[30] = tempU;
                                    break;
                                }
                                case 2: {
                                    float tempU2 = this.vertices[3];
                                    this.vertices[3] = this.vertices[21];
                                    this.vertices[21] = tempU2;
                                    tempU2 = this.vertices[12];
                                    this.vertices[12] = this.vertices[30];
                                    this.vertices[30] = tempU2;
                                    float tempV2 = this.vertices[4];
                                    this.vertices[4] = this.vertices[22];
                                    this.vertices[22] = tempV2;
                                    tempV2 = this.vertices[13];
                                    this.vertices[13] = this.vertices[31];
                                    this.vertices[31] = tempV2;
                                    break;
                                }
                                case 3: {
                                    final float tempV = this.vertices[4];
                                    this.vertices[4] = this.vertices[31];
                                    this.vertices[31] = this.vertices[22];
                                    this.vertices[22] = this.vertices[13];
                                    this.vertices[13] = tempV;
                                    final float tempU = this.vertices[3];
                                    this.vertices[3] = this.vertices[30];
                                    this.vertices[30] = this.vertices[21];
                                    this.vertices[21] = this.vertices[12];
                                    this.vertices[12] = tempU;
                                    break;
                                }
                            }
                        }
                        this.batch.draw(region.getTexture(), this.vertices, 0, 20);
                    }
                }
            }
        }
    }
}
