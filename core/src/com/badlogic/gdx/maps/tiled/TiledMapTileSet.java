// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import java.util.Iterator;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.IntMap;

public class TiledMapTileSet implements Iterable<TiledMapTile>
{
    private String name;
    private IntMap<TiledMapTile> tiles;
    private MapProperties properties;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public MapProperties getProperties() {
        return this.properties;
    }
    
    public TiledMapTileSet() {
        this.tiles = new IntMap<TiledMapTile>();
        this.properties = new MapProperties();
    }
    
    public TiledMapTile getTile(final int id) {
        return this.tiles.get(id);
    }
    
    @Override
    public Iterator<TiledMapTile> iterator() {
        return this.tiles.values().iterator();
    }
    
    public void putTile(final int id, final TiledMapTile tile) {
        this.tiles.put(id, tile);
    }
    
    public void removeTile(final int id) {
        this.tiles.remove(id);
    }
    
    public int size() {
        return this.tiles.size;
    }
}
