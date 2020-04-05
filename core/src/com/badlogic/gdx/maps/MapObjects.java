// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import java.util.Iterator;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.Array;

public class MapObjects implements Iterable<MapObject>
{
    private Array<MapObject> objects;
    
    public MapObjects() {
        this.objects = new Array<MapObject>();
    }
    
    public MapObject get(final int index) {
        return this.objects.get(index);
    }
    
    public MapObject get(final String name) {
        for (int i = 0, n = this.objects.size; i < n; ++i) {
            final MapObject object = this.objects.get(i);
            if (name.equals(object.getName())) {
                return object;
            }
        }
        return null;
    }
    
    public int getIndex(final String name) {
        return this.getIndex(this.get(name));
    }
    
    public int getIndex(final MapObject object) {
        return this.objects.indexOf(object, true);
    }
    
    public int getCount() {
        return this.objects.size;
    }
    
    public void add(final MapObject object) {
        this.objects.add(object);
    }
    
    public void remove(final int index) {
        this.objects.removeIndex(index);
    }
    
    public void remove(final MapObject object) {
        this.objects.removeValue(object, true);
    }
    
    public <T extends MapObject> Array<T> getByType(final Class<T> type) {
        return this.getByType(type, new Array<T>());
    }
    
    public <T extends MapObject> Array<T> getByType(final Class<T> type, final Array<T> fill) {
        fill.clear();
        for (int i = 0, n = this.objects.size; i < n; ++i) {
            final MapObject object = this.objects.get(i);
            if (ClassReflection.isInstance(type, object)) {
                fill.add((T)object);
            }
        }
        return fill;
    }
    
    @Override
    public Iterator<MapObject> iterator() {
        return this.objects.iterator();
    }
}
