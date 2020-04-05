// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;

public class PixmapLoader extends AsynchronousAssetLoader<Pixmap, PixmapParameter>
{
    Pixmap pixmap;
    
    public PixmapLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final PixmapParameter parameter) {
        this.pixmap = null;
        this.pixmap = new Pixmap(file);
    }
    
    @Override
    public Pixmap loadSync(final AssetManager manager, final String fileName, final FileHandle file, final PixmapParameter parameter) {
        final Pixmap pixmap = this.pixmap;
        this.pixmap = null;
        return pixmap;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final PixmapParameter parameter) {
        return null;
    }
    
    public static class PixmapParameter extends AssetLoaderParameters<Pixmap>
    {
    }
}
