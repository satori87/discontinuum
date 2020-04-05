// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class BitmapFontLoader extends AsynchronousAssetLoader<BitmapFont, BitmapFontParameter>
{
    BitmapFont.BitmapFontData data;
    
    public BitmapFontLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final BitmapFontParameter parameter) {
        final Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        if (parameter != null && parameter.bitmapFontData != null) {
            this.data = parameter.bitmapFontData;
            return deps;
        }
        this.data = new BitmapFont.BitmapFontData(file, parameter != null && parameter.flip);
        if (parameter != null && parameter.atlasName != null) {
            deps.add(new AssetDescriptor(parameter.atlasName, TextureAtlas.class));
        }
        else {
            for (int i = 0; i < this.data.getImagePaths().length; ++i) {
                final String path = this.data.getImagePath(i);
                final FileHandle resolved = this.resolve(path);
                final TextureLoader.TextureParameter textureParams = new TextureLoader.TextureParameter();
                if (parameter != null) {
                    textureParams.genMipMaps = parameter.genMipMaps;
                    textureParams.minFilter = parameter.minFilter;
                    textureParams.magFilter = parameter.magFilter;
                }
                final AssetDescriptor descriptor = new AssetDescriptor(resolved, (Class<T>)Texture.class, (AssetLoaderParameters<T>)textureParams);
                deps.add(descriptor);
            }
        }
        return deps;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final BitmapFontParameter parameter) {
    }
    
    @Override
    public BitmapFont loadSync(final AssetManager manager, final String fileName, final FileHandle file, final BitmapFontParameter parameter) {
        if (parameter == null || parameter.atlasName == null) {
            final int n = this.data.getImagePaths().length;
            final Array<TextureRegion> regs = new Array<TextureRegion>(n);
            for (int i = 0; i < n; ++i) {
                regs.add(new TextureRegion(manager.get(this.data.getImagePath(i), Texture.class)));
            }
            return new BitmapFont(this.data, regs, true);
        }
        final TextureAtlas atlas = manager.get(parameter.atlasName, TextureAtlas.class);
        final String name = file.sibling(this.data.imagePaths[0]).nameWithoutExtension().toString();
        final TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        if (region == null) {
            throw new GdxRuntimeException("Could not find font region " + name + " in atlas " + parameter.atlasName);
        }
        return new BitmapFont(file, region);
    }
    
    public static class BitmapFontParameter extends AssetLoaderParameters<BitmapFont>
    {
        public boolean flip;
        public boolean genMipMaps;
        public Texture.TextureFilter minFilter;
        public Texture.TextureFilter magFilter;
        public BitmapFont.BitmapFontData bitmapFontData;
        public String atlasName;
        
        public BitmapFontParameter() {
            this.flip = false;
            this.genMipMaps = false;
            this.minFilter = Texture.TextureFilter.Nearest;
            this.magFilter = Texture.TextureFilter.Nearest;
            this.bitmapFontData = null;
            this.atlasName = null;
        }
    }
}
