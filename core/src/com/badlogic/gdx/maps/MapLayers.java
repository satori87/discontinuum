// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import java.util.Iterator;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.Array;

public class MapLayers implements Iterable<MapLayer>
{
    private Array<MapLayer> layers;
    
    public MapLayers() {
        this.layers = new Array<MapLayer>();
    }
    
    public MapLayer get(final int index) {
        return this.layers.get(index);
    }
    
    public MapLayer get(final String name) {
        for (int i = 0, n = this.layers.size; i < n; ++i) {
            final MapLayer layer = this.layers.get(i);
            if (name.equals(layer.getName())) {
                return layer;
            }
        }
        return null;
    }
    
    public int getIndex(final String name) {
        return this.getIndex(this.get(name));
    }
    
    public int getIndex(final MapLayer layer) {
        return this.layers.indexOf(layer, true);
    }
    
    public int getCount() {
        return this.layers.size;
    }
    
    public void add(final MapLayer layer) {
        this.layers.add(layer);
    }
    
    public void remove(final int index) {
        this.layers.removeIndex(index);
    }
    
    public void remove(final MapLayer layer) {
        this.layers.removeValue(layer, true);
    }
    
    public int size() {
        return this.layers.size;
    }
    
    public <T extends MapLayer> Array<T> getByType(final Class<T> type) {
        return this.getByType(type, new Array<T>());
    }
    
    public <T extends MapLayer> Array<T> getByType(final Class<T> type, final Array<T> fill) {
        fill.clear();
        for (int i = 0, n = this.layers.size; i < n; ++i) {
            final MapLayer layer = this.layers.get(i);
            if (ClassReflection.isInstance(type, layer)) {
                fill.add((T)layer);
            }
        }
        return fill;
    }
    
    @Override
    public Iterator<MapLayer> iterator() {
        return this.layers.iterator();
    }
}
