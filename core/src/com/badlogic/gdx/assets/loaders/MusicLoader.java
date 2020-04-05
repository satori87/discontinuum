// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class MusicLoader extends AsynchronousAssetLoader<Music, MusicParameter>
{
    private Music music;
    
    public MusicLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    protected Music getLoadedMusic() {
        return this.music;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final MusicParameter parameter) {
        this.music = Gdx.audio.newMusic(file);
    }
    
    @Override
    public Music loadSync(final AssetManager manager, final String fileName, final FileHandle file, final MusicParameter parameter) {
        final Music music = this.music;
        this.music = null;
        return music;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final MusicParameter parameter) {
        return null;
    }
    
    public static class MusicParameter extends AssetLoaderParameters<Music>
    {
    }
}
