// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.model.data;

import java.util.Iterator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Array;

public class ModelData
{
    public String id;
    public final short[] version;
    public final Array<ModelMesh> meshes;
    public final Array<ModelMaterial> materials;
    public final Array<ModelNode> nodes;
    public final Array<ModelAnimation> animations;
    
    public ModelData() {
        this.version = new short[2];
        this.meshes = new Array<ModelMesh>();
        this.materials = new Array<ModelMaterial>();
        this.nodes = new Array<ModelNode>();
        this.animations = new Array<ModelAnimation>();
    }
    
    public void addMesh(final ModelMesh mesh) {
        for (final ModelMesh other : this.meshes) {
            if (other.id.equals(mesh.id)) {
                throw new GdxRuntimeException("Mesh with id '" + other.id + "' already in model");
            }
        }
        this.meshes.add(mesh);
    }
}
