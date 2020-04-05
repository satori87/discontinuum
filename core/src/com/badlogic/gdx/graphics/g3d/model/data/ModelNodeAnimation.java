// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ModelNodeAnimation
{
    public String nodeId;
    public Array<ModelNodeKeyframe<Vector3>> translation;
    public Array<ModelNodeKeyframe<Quaternion>> rotation;
    public Array<ModelNodeKeyframe<Vector3>> scaling;
}
