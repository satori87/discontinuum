// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.TextureData;

public class FileTextureData implements TextureData
{
    final FileHandle file;
    int width;
    int height;
    Pixmap.Format format;
    Pixmap pixmap;
    boolean useMipMaps;
    boolean isPrepared;
    
    public FileTextureData(final FileHandle file, final Pixmap preloadedPixmap, final Pixmap.Format format, final boolean useMipMaps) {
        this.width = 0;
        this.height = 0;
        this.isPrepared = false;
        this.file = file;
        this.pixmap = preloadedPixmap;
        this.format = format;
        this.useMipMaps = useMipMaps;
        if (this.pixmap != null) {
            this.width = this.pixmap.getWidth();
            this.height = this.pixmap.getHeight();
            if (format == null) {
                this.format = this.pixmap.getFormat();
            }
        }
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
        if (this.pixmap == null) {
            if (this.file.extension().equals("cim")) {
                this.pixmap = PixmapIO.readCIM(this.file);
            }
            else {
                this.pixmap = new Pixmap(this.file);
            }
            this.width = this.pixmap.getWidth();
            this.height = this.pixmap.getHeight();
            if (this.format == null) {
                this.format = this.pixmap.getFormat();
            }
        }
        this.isPrepared = true;
    }
    
    @Override
    public Pixmap consumePixmap() {
        if (!this.isPrepared) {
            throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
        }
        this.isPrepared = false;
        final Pixmap pixmap = this.pixmap;
        this.pixmap = null;
        return pixmap;
    }
    
    @Override
    public boolean disposePixmap() {
        return true;
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
        return this.format;
    }
    
    @Override
    public boolean useMipMaps() {
        return this.useMipMaps;
    }
    
    @Override
    public boolean isManaged() {
        return true;
    }
    
    public FileHandle getFileHandle() {
        return this.file;
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
    public String toString() {
        return this.file.toString();
    }
}
