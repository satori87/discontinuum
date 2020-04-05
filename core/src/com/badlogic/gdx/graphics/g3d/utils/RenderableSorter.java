// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Camera;

public interface RenderableSorter
{
    void sort(final Camera p0, final Array<Renderable> p1);
}
