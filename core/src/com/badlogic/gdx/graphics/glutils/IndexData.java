// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.nio.ShortBuffer;
import com.badlogic.gdx.utils.Disposable;

public interface IndexData extends Disposable
{
    int getNumIndices();
    
    int getNumMaxIndices();
    
    void setIndices(final short[] p0, final int p1, final int p2);
    
    void setIndices(final ShortBuffer p0);
    
    void updateIndices(final int p0, final short[] p1, final int p2, final int p3);
    
    ShortBuffer getBuffer();
    
    void bind();
    
    void unbind();
    
    void invalidate();
    
    void dispose();
}
