// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.nio.FloatBuffer;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Disposable;

public interface VertexData extends Disposable
{
    int getNumVertices();
    
    int getNumMaxVertices();
    
    VertexAttributes getAttributes();
    
    void setVertices(final float[] p0, final int p1, final int p2);
    
    void updateVertices(final int p0, final float[] p1, final int p2, final int p3);
    
    FloatBuffer getBuffer();
    
    void bind(final ShaderProgram p0);
    
    void bind(final ShaderProgram p0, final int[] p1);
    
    void unbind(final ShaderProgram p0);
    
    void unbind(final ShaderProgram p0, final int[] p1);
    
    void invalidate();
    
    void dispose();
}
