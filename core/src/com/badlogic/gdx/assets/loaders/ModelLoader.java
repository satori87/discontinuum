// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;
import java.util.Iterator;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Model;

public abstract class ModelLoader<P extends ModelParameters> extends AsynchronousAssetLoader<Model, P>
{
    protected Array<ObjectMap.Entry<String, ModelData>> items;
    protected ModelParameters defaultParameters;
    
    public ModelLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.items = new Array<ObjectMap.Entry<String, ModelData>>();
        this.defaultParameters = new ModelParameters();
    }
    
    public abstract ModelData loadModelData(final FileHandle p0, final P p1);
    
    public ModelData loadModelData(final FileHandle fileHandle) {
        return this.loadModelData(fileHandle, null);
    }
    
    public Model loadModel(final FileHandle fileHandle, final TextureProvider textureProvider, final P parameters) {
        final ModelData data = this.loadModelData(fileHandle, parameters);
        return (data == null) ? null : new Model(data, textureProvider);
    }
    
    public Model loadModel(final FileHandle fileHandle, final P parameters) {
        return this.loadModel(fileHandle, new TextureProvider.FileTextureProvider(), parameters);
    }
    
    public Model loadModel(final FileHandle fileHandle, final TextureProvider textureProvider) {
        return this.loadModel(fileHandle, textureProvider, null);
    }
    
    public Model loadModel(final FileHandle fileHandle) {
        return this.loadModel(fileHandle, new TextureProvider.FileTextureProvider(), null);
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final P parameters) {
        final Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        final ModelData data = this.loadModelData(file, parameters);
        if (data == null) {
            return deps;
        }
        final ObjectMap.Entry<String, ModelData> item = new ObjectMap.Entry<String, ModelData>();
        item.key = fileName;
        item.value = data;
        synchronized (this.items) {
            this.items.add(item);
        }
        // monitorexit(this.items)
        final TextureLoader.TextureParameter textureParameter = (parameters != null) ? parameters.textureParameter : this.defaultParameters.textureParameter;
        for (final ModelMaterial modelMaterial : data.materials) {
            if (modelMaterial.textures != null) {
                for (final ModelTexture modelTexture : modelMaterial.textures) {
                    deps.add(new AssetDescriptor(modelTexture.fileName, Texture.class, textureParameter));
                }
            }
        }
        return deps;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final P parameters) {
    }
    
    @Override
    public Model loadSync(final AssetManager manager, final String fileName, final FileHandle file, final P parameters) {
        ModelData data = null;
        synchronized (this.items) {
            for (int i = 0; i < this.items.size; ++i) {
                if (this.items.get(i).key.equals(fileName)) {
                    data = this.items.get(i).value;
                    this.items.removeIndex(i);
                }
            }
        }
        // monitorexit(this.items)
        if (data == null) {
            return null;
        }
        final Model result = new Model(data, new TextureProvider.AssetTextureProvider(manager));
        final Iterator<Disposable> disposables = result.getManagedDisposables().iterator();
        while (disposables.hasNext()) {
            final Disposable disposable = disposables.next();
            if (disposable instanceof Texture) {
                disposables.remove();
            }
        }
        data = null;
        return result;
    }
    
    public static class ModelParameters extends AssetLoaderParameters<Model>
    {
        public TextureLoader.TextureParameter textureParameter;
        
        public ModelParameters() {
            this.textureParameter = new TextureLoader.TextureParameter();
            final TextureLoader.TextureParameter textureParameter = this.textureParameter;
            final TextureLoader.TextureParameter textureParameter2 = this.textureParameter;
            final Texture.TextureFilter linear = Texture.TextureFilter.Linear;
            textureParameter2.magFilter = linear;
            textureParameter.minFilter = linear;
            final TextureLoader.TextureParameter textureParameter3 = this.textureParameter;
            final TextureLoader.TextureParameter textureParameter4 = this.textureParameter;
            final Texture.TextureWrap repeat = Texture.TextureWrap.Repeat;
            textureParameter4.wrapV = repeat;
            textureParameter3.wrapU = repeat;
        }
    }
}
