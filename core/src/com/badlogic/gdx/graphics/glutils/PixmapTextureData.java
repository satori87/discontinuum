// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;

public class PixmapTextureData implements TextureData
{
    final Pixmap pixmap;
    final Pixmap.Format format;
    final boolean useMipMaps;
    final boolean disposePixmap;
    final boolean managed;
    
    public PixmapTextureData(final Pixmap pixmap, final Pixmap.Format format, final boolean useMipMaps, final boolean disposePixmap) {
        this(pixmap, format, useMipMaps, disposePixmap, false);
    }
    
    public PixmapTextureData(final Pixmap pixmap, final Pixmap.Format format, final boolean useMipMaps, final boolean disposePixmap, final boolean managed) {
        this.pixmap = pixmap;
        this.format = ((format == null) ? pixmap.getFormat() : format);
        this.useMipMaps = useMipMaps;
        this.disposePixmap = disposePixmap;
        this.managed = managed;
    }
    
    @Override
    public boolean disposePixmap() {
        return this.disposePixmap;
    }
    
    @Override
    public Pixmap consumePixmap() {
        return this.pixmap;
    }
    
    @Override
    public int getWidth() {
        return this.pixmap.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.pixmap.getHeight();
    }
    
    @Override
    public Pixmap.Format getFormat() {
        return this.format;
    }
    
    @Override
    public boolean useMipMaps() {
        return this.useMipMaps;
    }
    
    @Override
    public boolean isManaged() {
        return this.managed;
    }
    
    @Override
    public TextureDataType getType() {
        return TextureDataType.Pixmap;
    }
    
    @Override
    public void consumeCustomData(final int target) {
        throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
    }
    
    @Override
    public boolean isPrepared() {
        return true;
    }
    
    @Override
    public void prepare() {
        throw new GdxRuntimeException("prepare() must not be called on a PixmapTextureData instance as it is already prepared.");
    }
}
