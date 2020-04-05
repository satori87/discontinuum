// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets;

import com.badlogic.gdx.files.FileHandle;

public class AssetDescriptor<T>
{
    public final String fileName;
    public final Class<T> type;
    public final AssetLoaderParameters params;
    public FileHandle file;
    
    public AssetDescriptor(final String fileName, final Class<T> assetType) {
        this(fileName, assetType, null);
    }
    
    public AssetDescriptor(final FileHandle file, final Class<T> assetType) {
        this(file, assetType, null);
    }
    
    public AssetDescriptor(final String fileName, final Class<T> assetType, final AssetLoaderParameters<T> params) {
        this.fileName = fileName.replaceAll("\\\\", "/");
        this.type = assetType;
        this.params = params;
    }
    
    public AssetDescriptor(final FileHandle file, final Class<T> assetType, final AssetLoaderParameters<T> params) {
        this.fileName = file.path().replaceAll("\\\\", "/");
        this.file = file;
        this.type = assetType;
        this.params = params;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.fileName);
        sb.append(", ");
        sb.append(this.type.getName());
        return sb.toString();
    }
}
