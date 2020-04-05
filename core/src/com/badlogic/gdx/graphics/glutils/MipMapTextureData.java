// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;

public class MipMapTextureData implements TextureData
{
    TextureData[] mips;
    
    public MipMapTextureData(final TextureData... mipMapData) {
        System.arraycopy(mipMapData, 0, this.mips = new TextureData[mipMapData.length], 0, mipMapData.length);
    }
    
    @Override
    public TextureDataType getType() {
        return TextureDataType.Custom;
    }
    
    @Override
    public boolean isPrepared() {
        return true;
    }
    
    @Override
    public void prepare() {
    }
    
    @Override
    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("It's compressed, use the compressed method");
    }
    
    @Override
    public boolean disposePixmap() {
        return false;
    }
    
    @Override
    public void consumeCustomData(final int target) {
        for (int i = 0; i < this.mips.length; ++i) {
            GLTexture.uploadImageData(target, this.mips[i], i);
        }
    }
    
    @Override
    public int getWidth() {
        return this.mips[0].getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.mips[0].getHeight();
    }
    
    @Override
    public Pixmap.Format getFormat() {
        return this.mips[0].getFormat();
    }
    
    @Override
    public boolean useMipMaps() {
        return false;
    }
    
    @Override
    public boolean isManaged() {
        return true;
    }
}
