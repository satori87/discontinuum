// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public abstract class AssetLoader<T, P extends AssetLoaderParameters<T>>
{
    private FileHandleResolver resolver;
    
    public AssetLoader(final FileHandleResolver resolver) {
        this.resolver = resolver;
    }
    
    public FileHandle resolve(final String fileName) {
        return this.resolver.resolve(fileName);
    }
    
    public abstract Array<AssetDescriptor> getDependencies(final String p0, final FileHandle p1, final P p2);
}
