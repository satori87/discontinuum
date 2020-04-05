// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public interface TextureProvider
{
    Texture load(final String p0);
    
    public static class FileTextureProvider implements TextureProvider
    {
        private Texture.TextureFilter minFilter;
        private Texture.TextureFilter magFilter;
        private Texture.TextureWrap uWrap;
        private Texture.TextureWrap vWrap;
        private boolean useMipMaps;
        
        public FileTextureProvider() {
            final Texture.TextureFilter linear = Texture.TextureFilter.Linear;
            this.magFilter = linear;
            this.minFilter = linear;
            final Texture.TextureWrap repeat = Texture.TextureWrap.Repeat;
            this.vWrap = repeat;
            this.uWrap = repeat;
            this.useMipMaps = false;
        }
        
        public FileTextureProvider(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final Texture.TextureWrap uWrap, final Texture.TextureWrap vWrap, final boolean useMipMaps) {
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.uWrap = uWrap;
            this.vWrap = vWrap;
            this.useMipMaps = useMipMaps;
        }
        
        @Override
        public Texture load(final String fileName) {
            final Texture result = new Texture(Gdx.files.internal(fileName), this.useMipMaps);
            result.setFilter(this.minFilter, this.magFilter);
            result.setWrap(this.uWrap, this.vWrap);
            return result;
        }
    }
    
    public static class AssetTextureProvider implements TextureProvider
    {
        public final AssetManager assetManager;
        
        public AssetTextureProvider(final AssetManager assetManager) {
            this.assetManager = assetManager;
        }
        
        @Override
        public Texture load(final String fileName) {
            return this.assetManager.get(fileName, Texture.class);
        }
    }
}
