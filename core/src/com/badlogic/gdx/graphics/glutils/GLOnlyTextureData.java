// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Pixmap;
import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.TextureData;

public class GLOnlyTextureData implements TextureData
{
    int width;
    int height;
    boolean isPrepared;
    int mipLevel;
    int internalFormat;
    int format;
    int type;
    
    public GLOnlyTextureData(final int width, final int height, final int mipMapLevel, final int internalFormat, final int format, final int type) {
        this.width = 0;
        this.height = 0;
        this.isPrepared = false;
        this.mipLevel = 0;
        this.width = width;
        this.height = height;
        this.mipLevel = mipMapLevel;
        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
    }
    
    @Override
    public TextureDataType getType() {
        return TextureDataType.Custom;
    }
    
    @Override
    public boolean isPrepared() {
        return this.isPrepared;
    }
    
    @Override
    public void prepare() {
        if (this.isPrepared) {
            throw new GdxRuntimeException("Already prepared");
        }
        this.isPrepared = true;
    }
    
    @Override
    public void consumeCustomData(final int target) {
        Gdx.gl.glTexImage2D(target, this.mipLevel, this.internalFormat, this.width, this.height, 0, this.format, this.type, null);
    }
    
    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }
    
    @Override
    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public Pixmap.Format getFormat() {
        return Pixmap.Format.RGBA8888;
    }
    
    @Override
    public boolean useMipMaps() {
        return false;
    }
    
    @Override
    public boolean isManaged() {
        return false;
    }
}
