// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.badlogic.gdx.graphics.glutils.ETC1TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.files.FileHandle;

public interface TextureData
{
    TextureDataType getType();
    
    boolean isPrepared();
    
    void prepare();
    
    Pixmap consumePixmap();
    
    boolean disposePixmap();
    
    void consumeCustomData(final int p0);
    
    int getWidth();
    
    int getHeight();
    
    Pixmap.Format getFormat();
    
    boolean useMipMaps();
    
    boolean isManaged();
    
    public enum TextureDataType
    {
        Pixmap("Pixmap", 0), 
        Custom("Custom", 1);
        
        private TextureDataType(final String name, final int ordinal) {
        }
    }
    
    public static class Factory
    {
        public static TextureData loadFromFile(final FileHandle file, final boolean useMipMaps) {
            return loadFromFile(file, null, useMipMaps);
        }
        
        public static TextureData loadFromFile(final FileHandle file, final Pixmap.Format format, final boolean useMipMaps) {
            if (file == null) {
                return null;
            }
            if (file.name().endsWith(".cim")) {
                return new FileTextureData(file, PixmapIO.readCIM(file), format, useMipMaps);
            }
            if (file.name().endsWith(".etc1")) {
                return new ETC1TextureData(file, useMipMaps);
            }
            if (file.name().endsWith(".ktx") || file.name().endsWith(".zktx")) {
                return new KTXTextureData(file, useMipMaps);
            }
            return new FileTextureData(file, new Pixmap(file), format, useMipMaps);
        }
    }
}
