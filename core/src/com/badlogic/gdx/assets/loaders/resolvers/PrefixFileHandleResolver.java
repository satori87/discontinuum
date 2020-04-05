// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public class PrefixFileHandleResolver implements FileHandleResolver
{
    private String prefix;
    private FileHandleResolver baseResolver;
    
    public PrefixFileHandleResolver(final FileHandleResolver baseResolver, final String prefix) {
        this.baseResolver = baseResolver;
        this.prefix = prefix;
    }
    
    public void setBaseResolver(final FileHandleResolver baseResolver) {
        this.baseResolver = baseResolver;
    }
    
    public FileHandleResolver getBaseResolver() {
        return this.baseResolver;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    @Override
    public FileHandle resolve(final String fileName) {
        return this.baseResolver.resolve(String.valueOf(this.prefix) + fileName);
    }
}
