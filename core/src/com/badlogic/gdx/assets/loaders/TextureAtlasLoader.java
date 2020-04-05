// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureAtlasLoader extends SynchronousAssetLoader<TextureAtlas, TextureAtlasParameter>
{
    TextureAtlas.TextureAtlasData data;
    
    public TextureAtlasLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public TextureAtlas load(final AssetManager assetManager, final String fileName, final FileHandle file, final TextureAtlasParameter parameter) {
        for (final TextureAtlas.TextureAtlasData.Page page : this.data.getPages()) {
            final Texture texture = assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
            page.texture = texture;
        }
        final TextureAtlas atlas = new TextureAtlas(this.data);
        this.data = null;
        return atlas;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle atlasFile, final TextureAtlasParameter parameter) {
        final FileHandle imgDir = atlasFile.parent();
        if (parameter != null) {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, parameter.flip);
        }
        else {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, false);
        }
        final Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        for (final TextureAtlas.TextureAtlasData.Page page : this.data.getPages()) {
            final TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor(page.textureFile, Texture.class, params));
        }
        return dependencies;
    }
    
    public static class TextureAtlasParameter extends AssetLoaderParameters<TextureAtlas>
    {
        public boolean flip;
        
        public TextureAtlasParameter() {
            this.flip = false;
        }
        
        public TextureAtlasParameter(final boolean flip) {
            this.flip = false;
            this.flip = flip;
        }
    }
}
