// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.objects.TextureMapObject;

public class TiledMapTileMapObject extends TextureMapObject
{
    private boolean flipHorizontally;
    private boolean flipVertically;
    private TiledMapTile tile;
    
    public TiledMapTileMapObject(final TiledMapTile tile, final boolean flipHorizontally, final boolean flipVertically) {
        this.flipHorizontally = flipHorizontally;
        this.flipVertically = flipVertically;
        this.tile = tile;
        final TextureRegion textureRegion = new TextureRegion(tile.getTextureRegion());
        textureRegion.flip(flipHorizontally, flipVertically);
        this.setTextureRegion(textureRegion);
    }
    
    public boolean isFlipHorizontally() {
        return this.flipHorizontally;
    }
    
    public void setFlipHorizontally(final boolean flipHorizontally) {
        this.flipHorizontally = flipHorizontally;
    }
    
    public boolean isFlipVertically() {
        return this.flipVertically;
    }
    
    public void setFlipVertically(final boolean flipVertically) {
        this.flipVertically = flipVertically;
    }
    
    public TiledMapTile getTile() {
        return this.tile;
    }
    
    public void setTile(final TiledMapTile tile) {
        this.tile = tile;
    }
}
