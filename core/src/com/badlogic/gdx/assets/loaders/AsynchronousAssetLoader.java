// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetLoaderParameters;

public abstract class AsynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P>
{
    public AsynchronousAssetLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    public abstract void loadAsync(final AssetManager p0, final String p1, final FileHandle p2, final P p3);
    
    public abstract T loadSync(final AssetManager p0, final String p1, final FileHandle p2, final P p3);
}
