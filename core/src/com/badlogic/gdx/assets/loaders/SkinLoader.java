// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import java.util.Iterator;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinLoader extends AsynchronousAssetLoader<Skin, SkinParameter>
{
    public SkinLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final SkinParameter parameter) {
        final Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        if (parameter == null || parameter.textureAtlasPath == null) {
            deps.add(new AssetDescriptor(String.valueOf(file.pathWithoutExtension()) + ".atlas", TextureAtlas.class));
        }
        else if (parameter.textureAtlasPath != null) {
            deps.add(new AssetDescriptor(parameter.textureAtlasPath, TextureAtlas.class));
        }
        return deps;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final SkinParameter parameter) {
    }
    
    @Override
    public Skin loadSync(final AssetManager manager, final String fileName, final FileHandle file, final SkinParameter parameter) {
        String textureAtlasPath = String.valueOf(file.pathWithoutExtension()) + ".atlas";
        ObjectMap<String, Object> resources = null;
        if (parameter != null) {
            if (parameter.textureAtlasPath != null) {
                textureAtlasPath = parameter.textureAtlasPath;
            }
            if (parameter.resources != null) {
                resources = parameter.resources;
            }
        }
        final TextureAtlas atlas = manager.get(textureAtlasPath, TextureAtlas.class);
        final Skin skin = this.newSkin(atlas);
        if (resources != null) {
            for (final ObjectMap.Entry<String, Object> entry : resources.entries()) {
                skin.add(entry.key, entry.value);
            }
        }
        skin.load(file);
        return skin;
    }
    
    protected Skin newSkin(final TextureAtlas atlas) {
        return new Skin(atlas);
    }
    
    public static class SkinParameter extends AssetLoaderParameters<Skin>
    {
        public final String textureAtlasPath;
        public final ObjectMap<String, Object> resources;
        
        public SkinParameter() {
            this(null, null);
        }
        
        public SkinParameter(final ObjectMap<String, Object> resources) {
            this(null, resources);
        }
        
        public SkinParameter(final String textureAtlasPath) {
            this(textureAtlasPath, null);
        }
        
        public SkinParameter(final String textureAtlasPath, final ObjectMap<String, Object> resources) {
            this.textureAtlasPath = textureAtlasPath;
            this.resources = resources;
        }
    }
}
