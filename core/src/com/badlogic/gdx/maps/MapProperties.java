// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import java.util.Iterator;
import com.badlogic.gdx.utils.ObjectMap;

public class MapProperties
{
    private ObjectMap<String, Object> properties;
    
    public MapProperties() {
        this.properties = new ObjectMap<String, Object>();
    }
    
    public boolean containsKey(final String key) {
        return this.properties.containsKey(key);
    }
    
    public Object get(final String key) {
        return this.properties.get(key);
    }
    
    public <T> T get(final String key, final Class<T> clazz) {
        return (T)this.get(key);
    }
    
    public <T> T get(final String key, final T defaultValue, final Class<T> clazz) {
        final Object object = this.get(key);
        return (T)((object == null) ? defaultValue : object);
    }
    
    public void put(final String key, final Object value) {
        this.properties.put(key, value);
    }
    
    public void putAll(final MapProperties properties) {
        this.properties.putAll(properties.properties);
    }
    
    public void remove(final String key) {
        this.properties.remove(key);
    }
    
    public void clear() {
        this.properties.clear();
    }
    
    public Iterator<String> getKeys() {
        return (Iterator<String>)this.properties.keys();
    }
    
    public Iterator<Object> getValues() {
        return (Iterator<Object>)this.properties.values();
    }
}
