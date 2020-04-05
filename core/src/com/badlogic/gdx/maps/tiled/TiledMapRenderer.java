// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapRenderer;

public interface TiledMapRenderer extends MapRenderer
{
    void renderObjects(final MapLayer p0);
    
    void renderObject(final MapObject p0);
    
    void renderTileLayer(final TiledMapTileLayer p0);
    
    void renderImageLayer(final TiledMapImageLayer p0);
}
