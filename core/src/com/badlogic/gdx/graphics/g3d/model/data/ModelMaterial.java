// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;

public class ModelMaterial
{
    public String id;
    public MaterialType type;
    public Color ambient;
    public Color diffuse;
    public Color specular;
    public Color emissive;
    public Color reflection;
    public float shininess;
    public float opacity;
    public Array<ModelTexture> textures;
    
    public ModelMaterial() {
        this.opacity = 1.0f;
    }
    
    public enum MaterialType
    {
        Lambert("Lambert", 0), 
        Phong("Phong", 1);
        
        private MaterialType(final String name, final int ordinal) {
        }
    }
}
