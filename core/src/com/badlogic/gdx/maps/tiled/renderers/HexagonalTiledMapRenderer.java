// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class HexagonalTiledMapRenderer extends BatchTiledMapRenderer
{
    private boolean staggerAxisX;
    private boolean staggerIndexEven;
    private float hexSideLength;
    
    public HexagonalTiledMapRenderer(final TiledMap map) {
        super(map);
        this.staggerAxisX = true;
        this.staggerIndexEven = false;
        this.hexSideLength = 0.0f;
        this.init(map);
    }
    
    public HexagonalTiledMapRenderer(final TiledMap map, final float unitScale) {
        super(map, unitScale);
        this.staggerAxisX = true;
        this.staggerIndexEven = false;
        this.hexSideLength = 0.0f;
        this.init(map);
    }
    
    public HexagonalTiledMapRenderer(final TiledMap map, final Batch batch) {
        super(map, batch);
        this.staggerAxisX = true;
        this.staggerIndexEven = false;
        this.hexSideLength = 0.0f;
        this.init(map);
    }
    
    public HexagonalTiledMapRenderer(final TiledMap map, final float unitScale, final Batch batch) {
        super(map, unitScale, batch);
        this.staggerAxisX = true;
        this.staggerIndexEven = false;
        this.hexSideLength = 0.0f;
        this.init(map);
    }
    
    private void init(final TiledMap map) {
        final String axis = map.getProperties().get("staggeraxis", String.class);
        if (axis != null) {
            if (axis.equals("x")) {
                this.staggerAxisX = true;
            }
            else {
                this.staggerAxisX = false;
            }
        }
        final String index = map.getProperties().get("staggerindex", String.class);
        if (index != null) {
            if (index.equals("even")) {
                this.staggerIndexEven = true;
            }
            else {
                this.staggerIndexEven = false;
            }
        }
        Integer length = map.getProperties().get("hexsidelength", Integer.class);
        if (length != null) {
            this.hexSideLength = length;
        }
        else if (this.staggerAxisX) {
            length = map.getProperties().get("tilewidth", Integer.class);
            if (length != null) {
                this.hexSideLength = 0.5f * length;
            }
            else {
                final TiledMapTileLayer tmtl = (TiledMapTileLayer)map.getLayers().get(0);
                this.hexSideLength = 0.5f * tmtl.getTileWidth();
            }
        }
        else {
            length = map.getProperties().get("tileheight", Integer.class);
            if (length != null) {
                this.hexSideLength = 0.5f * length;
            }
            else {
                final TiledMapTileLayer tmtl = (TiledMapTileLayer)map.getLayers().get(0);
                this.hexSideLength = 0.5f * tmtl.getTileHeight();
            }
        }
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
        final float layerHexLength = this.hexSideLength * this.unitScale;
        if (this.staggerAxisX) {
            final float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2.0f;
            final float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2.0f;
            final float layerTileHeight2 = layerTileHeight * 0.5f;
            final int row1 = Math.max(0, (int)((this.viewBounds.y - layerTileHeight2 - layerOffsetX) / layerTileHeight));
            final int row2 = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + layerTileHeight - layerOffsetX) / layerTileHeight));
            final int col1 = Math.max(0, (int)((this.viewBounds.x - tileWidthLowerCorner - layerOffsetY) / tileWidthUpperCorner));
            final int col2 = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + tileWidthUpperCorner - layerOffsetY) / tileWidthUpperCorner));
            final int colA = (this.staggerIndexEven == (col1 % 2 == 0)) ? (col1 + 1) : col1;
            final int colB = (this.staggerIndexEven == (col1 % 2 == 0)) ? col1 : (col1 + 1);
            for (int row3 = row2 - 1; row3 >= row1; --row3) {
                for (int col3 = colA; col3 < col2; col3 += 2) {
                    this.renderCell(layer.getCell(col3, row3), tileWidthUpperCorner * col3 + layerOffsetX, layerTileHeight2 + layerTileHeight * row3 + layerOffsetY, color);
                }
                for (int col3 = colB; col3 < col2; col3 += 2) {
                    this.renderCell(layer.getCell(col3, row3), tileWidthUpperCorner * col3 + layerOffsetX, layerTileHeight * row3 + layerOffsetY, color);
                }
            }
        }
        else {
            final float tileHeightLowerCorner = (layerTileHeight - layerHexLength) / 2.0f;
            final float tileHeightUpperCorner = (layerTileHeight + layerHexLength) / 2.0f;
            final float layerTileWidth2 = layerTileWidth * 0.5f;
            final int row1 = Math.max(0, (int)((this.viewBounds.y - tileHeightLowerCorner - layerOffsetX) / tileHeightUpperCorner));
            final int row2 = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + tileHeightUpperCorner - layerOffsetX) / tileHeightUpperCorner));
            final int col1 = Math.max(0, (int)((this.viewBounds.x - layerTileWidth2 - layerOffsetY) / layerTileWidth));
            final int col2 = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + layerTileWidth - layerOffsetY) / layerTileWidth));
            float shiftX = 0.0f;
            for (int row4 = row2 - 1; row4 >= row1; --row4) {
                if (row4 % 2 == 0 == this.staggerIndexEven) {
                    shiftX = layerTileWidth2;
                }
                else {
                    shiftX = 0.0f;
                }
                for (int col4 = col1; col4 < col2; ++col4) {
                    this.renderCell(layer.getCell(col4, row4), layerTileWidth * col4 + shiftX + layerOffsetX, tileHeightUpperCorner * row4 + layerOffsetY, color);
                }
            }
        }
    }
    
    private void renderCell(final TiledMapTileLayer.Cell cell, final float x, final float y, final float color) {
        if (cell != null) {
            final TiledMapTile tile = cell.getTile();
            if (tile != null) {
                if (tile instanceof AnimatedTiledMapTile) {
                    return;
                }
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
                if (rotations == 2) {
                    float tempU = this.vertices[3];
                    this.vertices[3] = this.vertices[21];
                    this.vertices[21] = tempU;
                    tempU = this.vertices[12];
                    this.vertices[12] = this.vertices[30];
                    this.vertices[30] = tempU;
                    float tempV = this.vertices[4];
                    this.vertices[4] = this.vertices[22];
                    this.vertices[22] = tempV;
                    tempV = this.vertices[13];
                    this.vertices[13] = this.vertices[31];
                    this.vertices[31] = tempV;
                }
                this.batch.draw(region.getTexture(), this.vertices, 0, 20);
            }
        }
    }
}
