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

public class OrthogonalTiledMapRenderer extends BatchTiledMapRenderer
{
    public OrthogonalTiledMapRenderer(final TiledMap map) {
        super(map);
    }
    
    public OrthogonalTiledMapRenderer(final TiledMap map, final Batch batch) {
        super(map, batch);
    }
    
    public OrthogonalTiledMapRenderer(final TiledMap map, final float unitScale) {
        super(map, unitScale);
    }
    
    public OrthogonalTiledMapRenderer(final TiledMap map, final float unitScale, final Batch batch) {
        super(map, unitScale, batch);
    }
    
    @Override
    public void renderTileLayer(final TiledMapTileLayer layer) {
        final Color batchColor = this.batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
        final int layerWidth = layer.getWidth();
        final int layerHeight = layer.getHeight();
        final float layerTileWidth = layer.getTileWidth() * this.unitScale;
        final float layerTileHeight = layer.getTileHeight() * this.unitScale;
        final float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        final float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale;
        final int col1 = Math.max(0, (int)((this.viewBounds.x - layerOffsetX) / layerTileWidth));
        final int col2 = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));
        final int row1 = Math.max(0, (int)((this.viewBounds.y - layerOffsetY) / layerTileHeight));
        final int row2 = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));
        float y = row2 * layerTileHeight + layerOffsetY;
        final float xStart = col1 * layerTileWidth + layerOffsetX;
        final float[] vertices = this.vertices;
        for (int row3 = row2; row3 >= row1; --row3) {
            float x = xStart;
            for (int col3 = col1; col3 < col2; ++col3) {
                final TiledMapTileLayer.Cell cell = layer.getCell(col3, row3);
                if (cell == null) {
                    x += layerTileWidth;
                }
                else {
                    final TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        final boolean flipX = cell.getFlipHorizontally();
                        final boolean flipY = cell.getFlipVertically();
                        final int rotations = cell.getRotation();
                        final TextureRegion region = tile.getTextureRegion();
                        final float x2 = x + tile.getOffsetX() * this.unitScale;
                        final float y2 = y + tile.getOffsetY() * this.unitScale;
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
                        this.batch.draw(region.getTexture(), vertices, 0, 20);
                    }
                    x += layerTileWidth;
                }
            }
            y -= layerTileHeight;
        }
    }
}
