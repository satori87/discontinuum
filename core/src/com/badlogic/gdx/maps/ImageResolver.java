// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface ImageResolver
{
    TextureRegion getImage(final String p0);
    
    public static class DirectImageResolver implements ImageResolver
    {
        private final ObjectMap<String, Texture> images;
        
        public DirectImageResolver(final ObjectMap<String, Texture> images) {
            this.images = images;
        }
        
        @Override
        public TextureRegion getImage(final String name) {
            return new TextureRegion(this.images.get(name));
        }
    }
    
    public static class AssetManagerImageResolver implements ImageResolver
    {
        private final AssetManager assetManager;
        
        public AssetManagerImageResolver(final AssetManager assetManager) {
            this.assetManager = assetManager;
        }
        
        @Override
        public TextureRegion getImage(final String name) {
            return new TextureRegion(this.assetManager.get(name, Texture.class));
        }
    }
    
    public static class TextureAtlasImageResolver implements ImageResolver
    {
        private final TextureAtlas atlas;
        
        public TextureAtlasImageResolver(final TextureAtlas atlas) {
            this.atlas = atlas;
        }
        
        @Override
        public TextureRegion getImage(final String name) {
            return this.atlas.findRegion(name);
        }
    }
}
