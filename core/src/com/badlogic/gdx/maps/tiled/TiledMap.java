// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import java.util.Iterator;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.Map;

public class TiledMap extends Map
{
    private TiledMapTileSets tilesets;
    private Array<? extends Disposable> ownedResources;
    
    public TiledMapTileSets getTileSets() {
        return this.tilesets;
    }
    
    public TiledMap() {
        this.tilesets = new TiledMapTileSets();
    }
    
    public void setOwnedResources(final Array<? extends Disposable> resources) {
        this.ownedResources = resources;
    }
    
    @Override
    public void dispose() {
        if (this.ownedResources != null) {
            for (final Disposable resource : this.ownedResources) {
                resource.dispose();
            }
        }
    }
}
