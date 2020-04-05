// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.GLTexture;

public interface TextureBinder
{
    void begin();
    
    void end();
    
    int bind(final TextureDescriptor p0);
    
    int bind(final GLTexture p0);
    
    int getBindCount();
    
    int getReuseCount();
    
    void resetCounts();
}
