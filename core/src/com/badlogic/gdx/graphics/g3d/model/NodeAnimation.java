// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class NodeAnimation
{
    public Node node;
    public Array<NodeKeyframe<Vector3>> translation;
    public Array<NodeKeyframe<Quaternion>> rotation;
    public Array<NodeKeyframe<Vector3>> scaling;
    
    public NodeAnimation() {
        this.translation = null;
        this.rotation = null;
        this.scaling = null;
    }
}
