// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public class LocalFileHandleResolver implements FileHandleResolver
{
    @Override
    public FileHandle resolve(final String fileName) {
        return Gdx.files.local(fileName);
    }
}
