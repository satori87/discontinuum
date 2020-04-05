// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

public interface ImmediateModeRenderer
{
    void begin(final Matrix4 p0, final int p1);
    
    void flush();
    
    void color(final Color p0);
    
    void color(final float p0, final float p1, final float p2, final float p3);
    
    void color(final float p0);
    
    void texCoord(final float p0, final float p1);
    
    void normal(final float p0, final float p1, final float p2);
    
    void vertex(final float p0, final float p1, final float p2);
    
    void end();
    
    int getNumVertices();
    
    int getMaxVertices();
    
    void dispose();
}
