// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets;

public class AssetLoaderParameters<T>
{
    public LoadedCallback loadedCallback;
    
    public interface LoadedCallback
    {
        void finishedLoading(final AssetManager p0, final String p1, final Class p2);
    }
}
