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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;

public class IsometricTiledMapRenderer extends BatchTiledMapRenderer
{
    private Matrix4 isoTransform;
    private Matrix4 invIsotransform;
    private Vector3 screenPos;
    private Vector2 topRight;
    private Vector2 bottomLeft;
    private Vector2 topLeft;
    private Vector2 bottomRight;
    
    public IsometricTiledMapRenderer(final TiledMap map) {
        super(map);
        this.screenPos = new Vector3();
        this.topRight = new Vector2();
        this.bottomLeft = new Vector2();
        this.topLeft = new Vector2();
        this.bottomRight = new Vector2();
        this.init();
    }
    
    public IsometricTiledMapRenderer(final TiledMap map, final Batch batch) {
        super(map, batch);
        this.screenPos = new Vector3();
        this.topRight = new Vector2();
        this.bottomLeft = new Vector2();
        this.topLeft = new Vector2();
        this.bottomRight = new Vector2();
        this.init();
    }
    
    public IsometricTiledMapRenderer(final TiledMap map, final float unitScale) {
        super(map, unitScale);
        this.screenPos = new Vector3();
        this.topRight = new Vector2();
        this.bottomLeft = new Vector2();
        this.topLeft = new Vector2();
        this.bottomRight = new Vector2();
        this.init();
    }
    
    public IsometricTiledMapRenderer(final TiledMap map, final float unitScale, final Batch batch) {
        super(map, unitScale, batch);
        this.screenPos = new Vector3();
        this.topRight = new Vector2();
        this.bottomLeft = new Vector2();
        this.topLeft = new Vector2();
        this.bottomRight = new Vector2();
        this.init();
    }
    
    private void init() {
        (this.isoTransform = new Matrix4()).idt();
        this.isoTransform.scale((float)(Math.sqrt(2.0) / 2.0), (float)(Math.sqrt(2.0) / 4.0), 1.0f);
        this.isoTransform.rotate(0.0f, 0.0f, 1.0f, -45.0f);
        (this.invIsotransform = new Matrix4(this.isoTransform)).inv();
    }
    
    private Vector3 translateScreenToIso(final Vector2 vec) {
        this.screenPos.set(vec.x, vec.y, 0.0f);
        this.screenPos.mul(this.invIsotransform);
        return this.screenPos;
    }
    
    @Override
    public void renderTileLayer(final TiledMapTileLayer layer) {
        final Color batchColor = this.batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());
        final float tileWidth = layer.getTileWidth() * this.unitScale;
        final float tileHeight = layer.getTileHeight() * this.unitScale;
        final float layerOffsetX = layer.getRenderOffsetX() * this.unitScale;
        final float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale;
        final float halfTileWidth = tileWidth * 0.5f;
        final float halfTileHeight = tileHeight * 0.5f;
        this.topRight.set(this.viewBounds.x + this.viewBounds.width - layerOffsetX, this.viewBounds.y - layerOffsetY);
        this.bottomLeft.set(this.viewBounds.x - layerOffsetX, this.viewBounds.y + this.viewBounds.height - layerOffsetY);
        this.topLeft.set(this.viewBounds.x - layerOffsetX, this.viewBounds.y - layerOffsetY);
        this.bottomRight.set(this.viewBounds.x + this.viewBounds.width - layerOffsetX, this.viewBounds.y + this.viewBounds.height - layerOffsetY);
        final int row1 = (int)(this.translateScreenToIso(this.topLeft).y / tileWidth) - 2;
        final int row2 = (int)(this.translateScreenToIso(this.bottomRight).y / tileWidth) + 2;
        final int col1 = (int)(this.translateScreenToIso(this.bottomLeft).x / tileWidth) - 2;
        final int col2 = (int)(this.translateScreenToIso(this.topRight).x / tileWidth) + 2;
        for (int row3 = row2; row3 >= row1; --row3) {
            for (int col3 = col1; col3 <= col2; ++col3) {
                final float x = col3 * halfTileWidth + row3 * halfTileWidth;
                final float y = row3 * halfTileHeight - col3 * halfTileHeight;
                final TiledMapTileLayer.Cell cell = layer.getCell(col3, row3);
                if (cell != null) {
                    final TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        final boolean flipX = cell.getFlipHorizontally();
                        final boolean flipY = cell.getFlipVertically();
                        final int rotations = cell.getRotation();
                        final TextureRegion region = tile.getTextureRegion();
                        final float x2 = x + tile.getOffsetX() * this.unitScale + layerOffsetX;
                        final float y2 = y + tile.getOffsetY() * this.unitScale + layerOffsetY;
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
