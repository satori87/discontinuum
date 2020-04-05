// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public abstract class PluggableGroupStrategy implements GroupStrategy
{
    private IntMap<GroupPlug> plugs;
    
    public PluggableGroupStrategy() {
        this.plugs = new IntMap<GroupPlug>();
    }
    
    @Override
    public void beforeGroup(final int group, final Array<Decal> contents) {
        this.plugs.get(group).beforeGroup(contents);
    }
    
    @Override
    public void afterGroup(final int group) {
        this.plugs.get(group).afterGroup();
    }
    
    public void plugIn(final GroupPlug plug, final int group) {
        this.plugs.put(group, plug);
    }
    
    public GroupPlug unPlug(final int group) {
        return this.plugs.remove(group);
    }
}
