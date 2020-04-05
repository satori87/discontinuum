// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;

public interface Shader extends Disposable
{
    void init();
    
    int compareTo(final Shader p0);
    
    boolean canRender(final Renderable p0);
    
    void begin(final Camera p0, final RenderContext p1);
    
    void render(final Renderable p0);
    
    void end();
}
