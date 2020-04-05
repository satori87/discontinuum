// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.io.IOException;
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch;
import java.util.Iterator;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;

public class ParticleEffectLoader extends AsynchronousAssetLoader<ParticleEffect, ParticleEffectLoadParameter>
{
    protected Array<ObjectMap.Entry<String, ResourceData<ParticleEffect>>> items;
    
    public ParticleEffectLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.items = new Array<ObjectMap.Entry<String, ResourceData<ParticleEffect>>>();
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final ParticleEffectLoadParameter parameter) {
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final ParticleEffectLoadParameter parameter) {
        final Json json = new Json();
        final ResourceData<ParticleEffect> data = json.fromJson((Class<ResourceData<ParticleEffect>>)ResourceData.class, file);
        Array<ResourceData.AssetData> assets = null;
        synchronized (this.items) {
            final ObjectMap.Entry<String, ResourceData<ParticleEffect>> entry = new ObjectMap.Entry<String, ResourceData<ParticleEffect>>();
            entry.key = fileName;
            entry.value = data;
            this.items.add(entry);
            assets = data.getAssets();
        }
        // monitorexit(this.items)
        final Array<AssetDescriptor> descriptors = new Array<AssetDescriptor>();
        for (final ResourceData.AssetData<?> assetData : assets) {
            if (!this.resolve(assetData.filename).exists()) {
                assetData.filename = file.parent().child(Gdx.files.internal(assetData.filename).name()).path();
            }
            if (assetData.type == ParticleEffect.class) {
                descriptors.add(new AssetDescriptor(assetData.filename, assetData.type, parameter));
            }
            else {
                descriptors.add(new AssetDescriptor(assetData.filename, assetData.type));
            }
        }
        return descriptors;
    }
    
    public void save(final ParticleEffect effect, final ParticleEffectSaveParameter parameter) throws IOException {
        final ResourceData<ParticleEffect> data = new ResourceData<ParticleEffect>(effect);
        effect.save(parameter.manager, data);
        if (parameter.batches != null) {
            for (final ParticleBatch<?> batch : parameter.batches) {
                boolean save = false;
                for (final ParticleController controller : effect.getControllers()) {
                    if (controller.renderer.isCompatible(batch)) {
                        save = true;
                        break;
                    }
                }
                if (save) {
                    batch.save(parameter.manager, data);
                }
            }
        }
        final Json json = new Json();
        json.toJson(data, parameter.file);
    }
    
    @Override
    public ParticleEffect loadSync(final AssetManager manager, final String fileName, final FileHandle file, final ParticleEffectLoadParameter parameter) {
        ResourceData<ParticleEffect> effectData = null;
        synchronized (this.items) {
            for (int i = 0; i < this.items.size; ++i) {
                final ObjectMap.Entry<String, ResourceData<ParticleEffect>> entry = this.items.get(i);
                if (entry.key.equals(fileName)) {
                    effectData = entry.value;
                    this.items.removeIndex(i);
                    break;
                }
            }
        }
        // monitorexit(this.items)
        effectData.resource.load(manager, effectData);
        if (parameter != null) {
            if (parameter.batches != null) {
                for (final ParticleBatch<?> batch : parameter.batches) {
                    batch.load(manager, effectData);
                }
            }
            effectData.resource.setBatch(parameter.batches);
        }
        return effectData.resource;
    }
    
    private <T> T find(final Array<?> array, final Class<T> type) {
        for (final Object object : array) {
            if (ClassReflection.isAssignableFrom(type, object.getClass())) {
                return (T)object;
            }
        }
        return null;
    }
    
    public static class ParticleEffectLoadParameter extends AssetLoaderParameters<ParticleEffect>
    {
        Array<ParticleBatch<?>> batches;
        
        public ParticleEffectLoadParameter(final Array<ParticleBatch<?>> batches) {
            this.batches = batches;
        }
    }
    
    public static class ParticleEffectSaveParameter extends AssetLoaderParameters<ParticleEffect>
    {
        Array<ParticleBatch<?>> batches;
        FileHandle file;
        AssetManager manager;
        
        public ParticleEffectSaveParameter(final FileHandle file, final AssetManager manager, final Array<ParticleBatch<?>> batches) {
            this.batches = batches;
            this.file = file;
            this.manager = manager;
        }
    }
}
