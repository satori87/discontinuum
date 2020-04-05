// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class ParticleEffectLoader extends SynchronousAssetLoader<ParticleEffect, ParticleEffectParameter>
{
    public ParticleEffectLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public ParticleEffect load(final AssetManager am, final String fileName, final FileHandle file, final ParticleEffectParameter param) {
        final ParticleEffect effect = new ParticleEffect();
        if (param != null && param.atlasFile != null) {
            effect.load(file, am.get(param.atlasFile, TextureAtlas.class), param.atlasPrefix);
        }
        else if (param != null && param.imagesDir != null) {
            effect.load(file, param.imagesDir);
        }
        else {
            effect.load(file, file.parent());
        }
        return effect;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final ParticleEffectParameter param) {
        Array<AssetDescriptor> deps = null;
        if (param != null && param.atlasFile != null) {
            deps = new Array<AssetDescriptor>();
            deps.add(new AssetDescriptor(param.atlasFile, TextureAtlas.class));
        }
        return deps;
    }
    
    public static class ParticleEffectParameter extends AssetLoaderParameters<ParticleEffect>
    {
        public String atlasFile;
        public String atlasPrefix;
        public FileHandle imagesDir;
    }
}
