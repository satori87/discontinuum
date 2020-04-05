// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.Gdx;
import java.util.Comparator;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.Array;

public class SimpleOrthoGroupStrategy implements GroupStrategy
{
    private Comparator comparator;
    private static final int GROUP_OPAQUE = 0;
    private static final int GROUP_BLEND = 1;
    
    public SimpleOrthoGroupStrategy() {
        this.comparator = new Comparator();
    }
    
    @Override
    public int decideGroup(final Decal decal) {
        return decal.getMaterial().isOpaque() ? 0 : 1;
    }
    
    @Override
    public void beforeGroup(final int group, final Array<Decal> contents) {
        if (group == 1) {
            Sort.instance().sort(contents, this.comparator);
            Gdx.gl.glEnable(3042);
            Gdx.gl.glDepthMask(false);
        }
    }
    
    @Override
    public void afterGroup(final int group) {
        if (group == 1) {
            Gdx.gl.glDepthMask(true);
            Gdx.gl.glDisable(3042);
        }
    }
    
    @Override
    public void beforeGroups() {
        Gdx.gl.glEnable(3553);
    }
    
    @Override
    public void afterGroups() {
        Gdx.gl.glDisable(3553);
    }
    
    @Override
    public ShaderProgram getGroupShader(final int group) {
        return null;
    }
    
    class Comparator implements java.util.Comparator<Decal>
    {
        @Override
        public int compare(final Decal a, final Decal b) {
            if (a.getZ() == b.getZ()) {
                return 0;
            }
            return (a.getZ() - b.getZ() < 0.0f) ? -1 : 1;
        }
    }
}
