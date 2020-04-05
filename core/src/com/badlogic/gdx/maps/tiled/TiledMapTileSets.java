// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import java.util.Iterator;
import com.badlogic.gdx.utils.Array;

public class TiledMapTileSets implements Iterable<TiledMapTileSet>
{
    private Array<TiledMapTileSet> tilesets;
    
    public TiledMapTileSets() {
        this.tilesets = new Array<TiledMapTileSet>();
    }
    
    public TiledMapTileSet getTileSet(final int index) {
        return this.tilesets.get(index);
    }
    
    public TiledMapTileSet getTileSet(final String name) {
        for (final TiledMapTileSet tileset : this.tilesets) {
            if (name.equals(tileset.getName())) {
                return tileset;
            }
        }
        return null;
    }
    
    public void addTileSet(final TiledMapTileSet tileset) {
        this.tilesets.add(tileset);
    }
    
    public void removeTileSet(final int index) {
        this.tilesets.removeIndex(index);
    }
    
    public void removeTileSet(final TiledMapTileSet tileset) {
        this.tilesets.removeValue(tileset, true);
    }
    
    public TiledMapTile getTile(final int id) {
        for (int i = this.tilesets.size - 1; i >= 0; --i) {
            final TiledMapTileSet tileset = this.tilesets.get(i);
            final TiledMapTile tile = tileset.getTile(id);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }
    
    @Override
    public Iterator<TiledMapTileSet> iterator() {
        return this.tilesets.iterator();
    }
}
