// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.glutils.FileTextureArrayData;
import com.badlogic.gdx.files.FileHandle;

public interface TextureArrayData
{
    boolean isPrepared();
    
    void prepare();
    
    void consumeTextureArrayData();
    
    int getWidth();
    
    int getHeight();
    
    int getDepth();
    
    boolean isManaged();
    
    int getInternalFormat();
    
    int getGLType();
    
    public static class Factory
    {
        public static TextureArrayData loadFromFiles(final Pixmap.Format format, final boolean useMipMaps, final FileHandle... files) {
            return new FileTextureArrayData(format, useMipMaps, files);
        }
    }
}
