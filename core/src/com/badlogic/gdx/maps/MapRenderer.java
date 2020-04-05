// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.maps;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.OrthographicCamera;

public interface MapRenderer
{
    void setView(final OrthographicCamera p0);
    
    void setView(final Matrix4 p0, final float p1, final float p2, final float p3, final float p4);
    
    void render();
    
    void render(final int[] p0);
}
