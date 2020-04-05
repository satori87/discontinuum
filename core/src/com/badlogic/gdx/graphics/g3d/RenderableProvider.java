// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;

public interface RenderableProvider
{
    void getRenderables(final Array<Renderable> p0, final Pool<Renderable> p1);
}
