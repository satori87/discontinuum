// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.assets.AssetDescriptor;
import java.util.Iterator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Json;

public class ResourceData<T> implements Json.Serializable
{
    private ObjectMap<String, SaveData> uniqueData;
    private Array<SaveData> data;
    Array<AssetData> sharedAssets;
    private int currentLoadIndex;
    public T resource;
    
    public ResourceData() {
        this.uniqueData = new ObjectMap<String, SaveData>();
        this.data = new Array<SaveData>(true, 3, SaveData.class);
        this.sharedAssets = new Array<AssetData>();
        this.currentLoadIndex = 0;
    }
    
    public ResourceData(final T resource) {
        this();
        this.resource = resource;
    }
    
     <K> int getAssetData(final String filename, final Class<K> type) {
        int i = 0;
        for (final AssetData data : this.sharedAssets) {
            if (data.filename.equals(filename) && data.type.equals(type)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public Array<AssetDescriptor> getAssetDescriptors() {
        final Array<AssetDescriptor> descriptors = new Array<AssetDescriptor>();
        for (final AssetData data : this.sharedAssets) {
            descriptors.add(new AssetDescriptor(data.filename, data.type));
        }
        return descriptors;
    }
    
    public Array<AssetData> getAssets() {
        return this.sharedAssets;
    }
    
    public SaveData createSaveData() {
        final SaveData saveData = new SaveData(this);
        this.data.add(saveData);
        return saveData;
    }
    
    public SaveData createSaveData(final String key) {
        final SaveData saveData = new SaveData(this);
        if (this.uniqueData.containsKey(key)) {
            throw new RuntimeException("Key already used, data must be unique, use a different key");
        }
        this.uniqueData.put(key, saveData);
        return saveData;
    }
    
    public SaveData getSaveData() {
        return this.data.get(this.currentLoadIndex++);
    }
    
    public SaveData getSaveData(final String key) {
        return this.uniqueData.get(key);
    }
    
    @Override
    public void write(final Json json) {
        json.writeValue("unique", this.uniqueData, ObjectMap.class);
        json.writeValue("data", this.data, Array.class, SaveData.class);
        json.writeValue("assets", this.sharedAssets.toArray(AssetData.class), AssetData[].class);
        json.writeValue("resource", this.resource, null);
    }
    
    @Override
    public void read(final Json json, final JsonValue jsonData) {
        this.uniqueData = json.readValue("unique", (Class<ObjectMap<String, SaveData>>)ObjectMap.class, jsonData);
        for (final ObjectMap.Entry<String, SaveData> entry : this.uniqueData.entries()) {
            entry.value.resources = this;
        }
        this.data = json.readValue("data", (Class<Array<SaveData>>)Array.class, SaveData.class, jsonData);
        for (final SaveData saveData : this.data) {
            saveData.resources = this;
        }
        this.sharedAssets.addAll(json.readValue("assets", (Class<Array<? extends AssetData>>)Array.class, AssetData.class, jsonData));
        this.resource = json.readValue("resource", (Class<T>)null, jsonData);
    }
    
    public static class SaveData implements Json.Serializable
    {
        ObjectMap<String, Object> data;
        IntArray assets;
        private int loadIndex;
        protected ResourceData resources;
        
        public SaveData() {
            this.data = new ObjectMap<String, Object>();
            this.assets = new IntArray();
            this.loadIndex = 0;
        }
        
        public SaveData(final ResourceData resources) {
            this.data = new ObjectMap<String, Object>();
            this.assets = new IntArray();
            this.loadIndex = 0;
            this.resources = resources;
        }
        
        public <K> void saveAsset(final String filename, final Class<K> type) {
            int i = this.resources.getAssetData(filename, type);
            if (i == -1) {
                this.resources.sharedAssets.add(new AssetData(filename, type));
                i = this.resources.sharedAssets.size - 1;
            }
            this.assets.add(i);
        }
        
        public void save(final String key, final Object value) {
            this.data.put(key, value);
        }
        
        public AssetDescriptor loadAsset() {
            if (this.loadIndex == this.assets.size) {
                return null;
            }
            final AssetData data = this.resources.sharedAssets.get(this.assets.get(this.loadIndex++));
            return new AssetDescriptor(data.filename, (Class<T>)data.type);
        }
        
        public <K> K load(final String key) {
            return (K)this.data.get(key);
        }
        
        @Override
        public void write(final Json json) {
            json.writeValue("data", this.data, ObjectMap.class);
            json.writeValue("indices", this.assets.toArray(), int[].class);
        }
        
        @Override
        public void read(final Json json, final JsonValue jsonData) {
            this.data = json.readValue("data", (Class<ObjectMap<String, Object>>)ObjectMap.class, jsonData);
            this.assets.addAll((int[])json.readValue("indices", int[].class, jsonData));
        }
    }
    
    public static class AssetData<T> implements Json.Serializable
    {
        public String filename;
        public Class<T> type;
        
        public AssetData() {
        }
        
        public AssetData(final String filename, final Class<T> type) {
            this.filename = filename;
            this.type = type;
        }
        
        @Override
        public void write(final Json json) {
            json.writeValue("filename", this.filename);
            json.writeValue("type", this.type.getName());
        }
        
        @Override
        public void read(final Json json, final JsonValue jsonData) {
            this.filename = json.readValue("filename", String.class, jsonData);
            final String className = json.readValue("type", String.class, jsonData);
            try {
                this.type = (Class<T>)ClassReflection.forName(className);
            }
            catch (ReflectionException e) {
                throw new GdxRuntimeException("Class not found: " + className, e);
            }
        }
    }
    
    public interface Configurable<T>
    {
        void save(final AssetManager p0, final ResourceData<T> p1);
        
        void load(final AssetManager p0, final ResourceData<T> p1);
    }
}
