// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.CubemapData;

public class FacedCubemapData implements CubemapData
{
    protected final TextureData[] data;
    
    public FacedCubemapData() {
        this(null, null, null, null, null, (TextureData)null);
    }
    
    public FacedCubemapData(final FileHandle positiveX, final FileHandle negativeX, final FileHandle positiveY, final FileHandle negativeY, final FileHandle positiveZ, final FileHandle negativeZ) {
        this(TextureData.Factory.loadFromFile(positiveX, false), TextureData.Factory.loadFromFile(negativeX, false), TextureData.Factory.loadFromFile(positiveY, false), TextureData.Factory.loadFromFile(negativeY, false), TextureData.Factory.loadFromFile(positiveZ, false), TextureData.Factory.loadFromFile(negativeZ, false));
    }
    
    public FacedCubemapData(final FileHandle positiveX, final FileHandle negativeX, final FileHandle positiveY, final FileHandle negativeY, final FileHandle positiveZ, final FileHandle negativeZ, final boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(positiveX, useMipMaps), TextureData.Factory.loadFromFile(negativeX, useMipMaps), TextureData.Factory.loadFromFile(positiveY, useMipMaps), TextureData.Factory.loadFromFile(negativeY, useMipMaps), TextureData.Factory.loadFromFile(positiveZ, useMipMaps), TextureData.Factory.loadFromFile(negativeZ, useMipMaps));
    }
    
    public FacedCubemapData(final Pixmap positiveX, final Pixmap negativeX, final Pixmap positiveY, final Pixmap negativeY, final Pixmap positiveZ, final Pixmap negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }
    
    public FacedCubemapData(final Pixmap positiveX, final Pixmap negativeX, final Pixmap positiveY, final Pixmap negativeY, final Pixmap positiveZ, final Pixmap negativeZ, final boolean useMipMaps) {
        this((positiveX == null) ? null : new PixmapTextureData(positiveX, null, useMipMaps, false), (negativeX == null) ? null : new PixmapTextureData(negativeX, null, useMipMaps, false), (positiveY == null) ? null : new PixmapTextureData(positiveY, null, useMipMaps, false), (negativeY == null) ? null : new PixmapTextureData(negativeY, null, useMipMaps, false), (positiveZ == null) ? null : new PixmapTextureData(positiveZ, null, useMipMaps, false), (negativeZ == null) ? null : new PixmapTextureData(negativeZ, null, useMipMaps, false));
    }
    
    public FacedCubemapData(final int width, final int height, final int depth, final Pixmap.Format format) {
        this(new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true));
    }
    
    public FacedCubemapData(final TextureData positiveX, final TextureData negativeX, final TextureData positiveY, final TextureData negativeY, final TextureData positiveZ, final TextureData negativeZ) {
        (this.data = new TextureData[6])[0] = positiveX;
        this.data[1] = negativeX;
        this.data[2] = positiveY;
        this.data[3] = negativeY;
        this.data[4] = positiveZ;
        this.data[5] = negativeZ;
    }
    
    @Override
    public boolean isManaged() {
        TextureData[] data2;
        for (int length = (data2 = this.data).length, i = 0; i < length; ++i) {
            final TextureData data = data2[i];
            if (!data.isManaged()) {
                return false;
            }
        }
        return true;
    }
    
    public void load(final Cubemap.CubemapSide side, final FileHandle file) {
        this.data[side.index] = TextureData.Factory.loadFromFile(file, false);
    }
    
    public void load(final Cubemap.CubemapSide side, final Pixmap pixmap) {
        this.data[side.index] = ((pixmap == null) ? null : new PixmapTextureData(pixmap, null, false, false));
    }
    
    public boolean isComplete() {
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    public TextureData getTextureData(final Cubemap.CubemapSide side) {
        return this.data[side.index];
    }
    
    @Override
    public int getWidth() {
        int width = 0;
        int tmp;
        if (this.data[Cubemap.CubemapSide.PositiveZ.index] != null && (tmp = this.data[Cubemap.CubemapSide.PositiveZ.index].getWidth()) > width) {
            width = tmp;
        }
        if (this.data[Cubemap.CubemapSide.NegativeZ.index] != null && (tmp = this.data[Cubemap.CubemapSide.NegativeZ.index].getWidth()) > width) {
            width = tmp;
        }
        if (this.data[Cubemap.CubemapSide.PositiveY.index] != null && (tmp = this.data[Cubemap.CubemapSide.PositiveY.index].getWidth()) > width) {
            width = tmp;
        }
        if (this.data[Cubemap.CubemapSide.NegativeY.index] != null && (tmp = this.data[Cubemap.CubemapSide.NegativeY.index].getWidth()) > width) {
            width = tmp;
        }
        return width;
    }
    
    @Override
    public int getHeight() {
        int height = 0;
        int tmp;
        if (this.data[Cubemap.CubemapSide.PositiveZ.index] != null && (tmp = this.data[Cubemap.CubemapSide.PositiveZ.index].getHeight()) > height) {
            height = tmp;
        }
        if (this.data[Cubemap.CubemapSide.NegativeZ.index] != null && (tmp = this.data[Cubemap.CubemapSide.NegativeZ.index].getHeight()) > height) {
            height = tmp;
        }
        if (this.data[Cubemap.CubemapSide.PositiveX.index] != null && (tmp = this.data[Cubemap.CubemapSide.PositiveX.index].getHeight()) > height) {
            height = tmp;
        }
        if (this.data[Cubemap.CubemapSide.NegativeX.index] != null && (tmp = this.data[Cubemap.CubemapSide.NegativeX.index].getHeight()) > height) {
            height = tmp;
        }
        return height;
    }
    
    @Override
    public boolean isPrepared() {
        return false;
    }
    
    @Override
    public void prepare() {
        if (!this.isComplete()) {
            throw new GdxRuntimeException("You need to complete your cubemap data before using it");
        }
        for (int i = 0; i < this.data.length; ++i) {
            if (!this.data[i].isPrepared()) {
                this.data[i].prepare();
            }
        }
    }
    
    @Override
    public void consumeCubemapData() {
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i].getType() == TextureData.TextureDataType.Custom) {
                this.data[i].consumeCustomData(34069 + i);
            }
            else {
                Pixmap pixmap = this.data[i].consumePixmap();
                boolean disposePixmap = this.data[i].disposePixmap();
                if (this.data[i].getFormat() != pixmap.getFormat()) {
                    final Pixmap tmp = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), this.data[i].getFormat());
                    tmp.setBlending(Pixmap.Blending.None);
                    tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                    if (this.data[i].disposePixmap()) {
                        pixmap.dispose();
                    }
                    pixmap = tmp;
                    disposePixmap = true;
                }
                Gdx.gl.glPixelStorei(3317, 1);
                Gdx.gl.glTexImage2D(34069 + i, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
                if (disposePixmap) {
                    pixmap.dispose();
                }
            }
        }
    }
}
