// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class TextureLoader extends AsynchronousAssetLoader<Texture, TextureParameter>
{
    TextureLoaderInfo info;
    
    public TextureLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.info = new TextureLoaderInfo();
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final TextureParameter parameter) {
        this.info.filename = fileName;
        if (parameter == null || parameter.textureData == null) {
            final Pixmap pixmap = null;
            Pixmap.Format format = null;
            boolean genMipMaps = false;
            this.info.texture = null;
            if (parameter != null) {
                format = parameter.format;
                genMipMaps = parameter.genMipMaps;
                this.info.texture = parameter.texture;
            }
            this.info.data = TextureData.Factory.loadFromFile(file, format, genMipMaps);
        }
        else {
            this.info.data = parameter.textureData;
            this.info.texture = parameter.texture;
        }
        if (!this.info.data.isPrepared()) {
            this.info.data.prepare();
        }
    }
    
    @Override
    public Texture loadSync(final AssetManager manager, final String fileName, final FileHandle file, final TextureParameter parameter) {
        if (this.info == null) {
            return null;
        }
        Texture texture = this.info.texture;
        if (texture != null) {
            texture.load(this.info.data);
        }
        else {
            texture = new Texture(this.info.data);
        }
        if (parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return texture;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final TextureParameter parameter) {
        return null;
    }
    
    public static class TextureLoaderInfo
    {
        String filename;
        TextureData data;
        Texture texture;
    }
    
    public static class TextureParameter extends AssetLoaderParameters<Texture>
    {
        public Pixmap.Format format;
        public boolean genMipMaps;
        public Texture texture;
        public TextureData textureData;
        public Texture.TextureFilter minFilter;
        public Texture.TextureFilter magFilter;
        public Texture.TextureWrap wrapU;
        public Texture.TextureWrap wrapV;
        
        public TextureParameter() {
            this.format = null;
            this.genMipMaps = false;
            this.texture = null;
            this.textureData = null;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
            this.wrapU = Texture.TextureWrap.ClampToEdge;
            this.wrapV = Texture.TextureWrap.ClampToEdge;
        }
    }
}
