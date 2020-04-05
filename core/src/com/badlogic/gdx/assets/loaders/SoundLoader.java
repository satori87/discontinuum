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
import com.badlogic.gdx.audio.Sound;

public class SoundLoader extends AsynchronousAssetLoader<Sound, SoundParameter>
{
    private Sound sound;
    
    public SoundLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    protected Sound getLoadedSound() {
        return this.sound;
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final SoundParameter parameter) {
        this.sound = Gdx.audio.newSound(file);
    }
    
    @Override
    public Sound loadSync(final AssetManager manager, final String fileName, final FileHandle file, final SoundParameter parameter) {
        final Sound sound = this.sound;
        this.sound = null;
        return sound;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final SoundParameter parameter) {
        return null;
    }
    
    public static class SoundParameter extends AssetLoaderParameters<Sound>
    {
    }
}
