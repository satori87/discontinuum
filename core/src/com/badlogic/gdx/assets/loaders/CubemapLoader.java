// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cubemap;

public class CubemapLoader extends AsynchronousAssetLoader<Cubemap, CubemapParameter>
{
    CubemapLoaderInfo info;
    
    public CubemapLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.info = new CubemapLoaderInfo();
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final CubemapParameter parameter) {
        this.info.filename = fileName;
        if (parameter == null || parameter.cubemapData == null) {
            final Pixmap pixmap = null;
            Pixmap.Format format = null;
            final boolean genMipMaps = false;
            this.info.cubemap = null;
            if (parameter != null) {
                format = parameter.format;
                this.info.cubemap = parameter.cubemap;
            }
            if (fileName.contains(".ktx") || fileName.contains(".zktx")) {
                this.info.data = new KTXTextureData(file, genMipMaps);
            }
        }
        else {
            this.info.data = parameter.cubemapData;
            this.info.cubemap = parameter.cubemap;
        }
        if (!this.info.data.isPrepared()) {
            this.info.data.prepare();
        }
    }
    
    @Override
    public Cubemap loadSync(final AssetManager manager, final String fileName, final FileHandle file, final CubemapParameter parameter) {
        if (this.info == null) {
            return null;
        }
        Cubemap cubemap = this.info.cubemap;
        if (cubemap != null) {
            cubemap.load(this.info.data);
        }
        else {
            cubemap = new Cubemap(this.info.data);
        }
        if (parameter != null) {
            cubemap.setFilter(parameter.minFilter, parameter.magFilter);
            cubemap.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return cubemap;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final CubemapParameter parameter) {
        return null;
    }
    
    public static class CubemapLoaderInfo
    {
        String filename;
        CubemapData data;
        Cubemap cubemap;
    }
    
    public static class CubemapParameter extends AssetLoaderParameters<Cubemap>
    {
        public Pixmap.Format format;
        public Cubemap cubemap;
        public CubemapData cubemapData;
        public Texture.TextureFilter minFilter;
        public Texture.TextureFilter magFilter;
        public Texture.TextureWrap wrapU;
        public Texture.TextureWrap wrapV;
        
        public CubemapParameter() {
            this.format = null;
            this.cubemap = null;
            this.cubemapData = null;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
            this.wrapU = Texture.TextureWrap.ClampToEdge;
            this.wrapV = Texture.TextureWrap.ClampToEdge;
        }
    }
}
