// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface GroupStrategy
{
    ShaderProgram getGroupShader(final int p0);
    
    int decideGroup(final Decal p0);
    
    void beforeGroup(final int p0, final Array<Decal> p1);
    
    void afterGroup(final int p0);
    
    void beforeGroups();
    
    void afterGroups();
}
