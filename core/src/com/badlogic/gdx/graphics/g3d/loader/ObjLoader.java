// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.graphics.g3d.Material;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.VertexAttribute;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.assets.loaders.ModelLoader;

public class ObjLoader extends ModelLoader<ObjLoaderParameters>
{
    public static boolean logWarning;
    final FloatArray verts;
    final FloatArray norms;
    final FloatArray uvs;
    final Array<Group> groups;
    
    static {
        ObjLoader.logWarning = false;
    }
    
    public ObjLoader() {
        this(null);
    }
    
    public ObjLoader(final FileHandleResolver resolver) {
        super(resolver);
        this.verts = new FloatArray(300);
        this.norms = new FloatArray(300);
        this.uvs = new FloatArray(200);
        this.groups = new Array<Group>(10);
    }
    
    public Model loadModel(final FileHandle fileHandle, final boolean flipV) {
        return this.loadModel(fileHandle, new ObjLoaderParameters(flipV));
    }
    
    @Override
    public ModelData loadModelData(final FileHandle file, final ObjLoaderParameters parameters) {
        return this.loadModelData(file, parameters != null && parameters.flipV);
    }
    
    protected ModelData loadModelData(final FileHandle file, final boolean flipV) {
        if (ObjLoader.logWarning) {
            Gdx.app.error("ObjLoader", "Wavefront (OBJ) is not fully supported, consult the documentation for more information");
        }
        final MtlLoader mtl = new MtlLoader();
        Group activeGroup = new Group("default");
        this.groups.add(activeGroup);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
        int id = 0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] tokens = line.split("\\s+");
                if (tokens.length < 1) {
                    break;
                }
                if (tokens[0].length() == 0) {
                    continue;
                }
                final char firstChar;
                if ((firstChar = tokens[0].toLowerCase().charAt(0)) == '#') {
                    continue;
                }
                if (firstChar == 'v') {
                    if (tokens[0].length() == 1) {
                        this.verts.add(Float.parseFloat(tokens[1]));
                        this.verts.add(Float.parseFloat(tokens[2]));
                        this.verts.add(Float.parseFloat(tokens[3]));
                    }
                    else if (tokens[0].charAt(1) == 'n') {
                        this.norms.add(Float.parseFloat(tokens[1]));
                        this.norms.add(Float.parseFloat(tokens[2]));
                        this.norms.add(Float.parseFloat(tokens[3]));
                    }
                    else {
                        if (tokens[0].charAt(1) != 't') {
                            continue;
                        }
                        this.uvs.add(Float.parseFloat(tokens[1]));
                        this.uvs.add(flipV ? (1.0f - Float.parseFloat(tokens[2])) : Float.parseFloat(tokens[2]));
                    }
                }
                else if (firstChar == 'f') {
                    final Array<Integer> faces = activeGroup.faces;
                    for (int i = 1; i < tokens.length - 2; --i) {
                        String[] parts = tokens[1].split("/");
                        faces.add(this.getIndex(parts[0], this.verts.size));
                        if (parts.length > 2) {
                            if (i == 1) {
                                activeGroup.hasNorms = true;
                            }
                            faces.add(this.getIndex(parts[2], this.norms.size));
                        }
                        if (parts.length > 1 && parts[1].length() > 0) {
                            if (i == 1) {
                                activeGroup.hasUVs = true;
                            }
                            faces.add(this.getIndex(parts[1], this.uvs.size));
                        }
                        parts = tokens[++i].split("/");
                        faces.add(this.getIndex(parts[0], this.verts.size));
                        if (parts.length > 2) {
                            faces.add(this.getIndex(parts[2], this.norms.size));
                        }
                        if (parts.length > 1 && parts[1].length() > 0) {
                            faces.add(this.getIndex(parts[1], this.uvs.size));
                        }
                        parts = tokens[++i].split("/");
                        faces.add(this.getIndex(parts[0], this.verts.size));
                        if (parts.length > 2) {
                            faces.add(this.getIndex(parts[2], this.norms.size));
                        }
                        if (parts.length > 1 && parts[1].length() > 0) {
                            faces.add(this.getIndex(parts[1], this.uvs.size));
                        }
                        final Group group2 = activeGroup;
                        ++group2.numFaces;
                    }
                }
                else if (firstChar == 'o' || firstChar == 'g') {
                    if (tokens.length > 1) {
                        activeGroup = this.setActiveGroup(tokens[1]);
                    }
                    else {
                        activeGroup = this.setActiveGroup("default");
                    }
                }
                else if (tokens[0].equals("mtllib")) {
                    mtl.load(file.parent().child(tokens[1]));
                }
                else {
                    if (!tokens[0].equals("usemtl")) {
                        continue;
                    }
                    if (tokens.length == 1) {
                        activeGroup.materialName = "default";
                    }
                    else {
                        activeGroup.materialName = tokens[1].replace('.', '_');
                    }
                }
            }
            reader.close();
        }
        catch (IOException e) {
            return null;
        }
        for (int j = 0; j < this.groups.size; ++j) {
            if (this.groups.get(j).numFaces < 1) {
                this.groups.removeIndex(j);
                --j;
            }
        }
        if (this.groups.size < 1) {
            return null;
        }
        final int numGroups = this.groups.size;
        final ModelData data = new ModelData();
        for (int g = 0; g < numGroups; ++g) {
            final Group group = this.groups.get(g);
            final Array<Integer> faces2 = group.faces;
            final int numElements = faces2.size;
            final int numFaces = group.numFaces;
            final boolean hasNorms = group.hasNorms;
            final boolean hasUVs = group.hasUVs;
            final float[] finalVerts = new float[numFaces * 3 * (3 + (hasNorms ? 3 : 0) + (hasUVs ? 2 : 0))];
            int uvIndex;
            for (int k = 0, vi = 0; k < numElements; uvIndex = faces2.get(k++) * 2, finalVerts[vi++] = this.uvs.get(uvIndex++), finalVerts[vi++] = this.uvs.get(uvIndex)) {
                int vertIndex = faces2.get(k++) * 3;
                finalVerts[vi++] = this.verts.get(vertIndex++);
                finalVerts[vi++] = this.verts.get(vertIndex++);
                finalVerts[vi++] = this.verts.get(vertIndex);
                if (hasNorms) {
                    int normIndex = faces2.get(k++) * 3;
                    finalVerts[vi++] = this.norms.get(normIndex++);
                    finalVerts[vi++] = this.norms.get(normIndex++);
                    finalVerts[vi++] = this.norms.get(normIndex);
                }
                if (hasUVs) {}
            }
            final int numIndices = (numFaces * 3 >= 32767) ? 0 : (numFaces * 3);
            final short[] finalIndices = new short[numIndices];
            if (numIndices > 0) {
                for (int l = 0; l < numIndices; ++l) {
                    finalIndices[l] = (short)l;
                }
            }
            final Array<VertexAttribute> attributes = new Array<VertexAttribute>();
            attributes.add(new VertexAttribute(1, 3, "a_position"));
            if (hasNorms) {
                attributes.add(new VertexAttribute(8, 3, "a_normal"));
            }
            if (hasUVs) {
                attributes.add(new VertexAttribute(16, 2, "a_texCoord0"));
            }
            final String stringId = Integer.toString(++id);
            final String nodeId = "default".equals(group.name) ? ("node" + stringId) : group.name;
            final String meshId = "default".equals(group.name) ? ("mesh" + stringId) : group.name;
            final String partId = "default".equals(group.name) ? ("part" + stringId) : group.name;
            final ModelNode node = new ModelNode();
            node.id = nodeId;
            node.meshId = meshId;
            node.scale = new Vector3(1.0f, 1.0f, 1.0f);
            node.translation = new Vector3();
            node.rotation = new Quaternion();
            final ModelNodePart pm = new ModelNodePart();
            pm.meshPartId = partId;
            pm.materialId = group.materialName;
            node.parts = new ModelNodePart[] { pm };
            final ModelMeshPart part = new ModelMeshPart();
            part.id = partId;
            part.indices = finalIndices;
            part.primitiveType = 4;
            final ModelMesh mesh = new ModelMesh();
            mesh.id = meshId;
            mesh.attributes = attributes.toArray(VertexAttribute.class);
            mesh.vertices = finalVerts;
            mesh.parts = new ModelMeshPart[] { part };
            data.nodes.add(node);
            data.meshes.add(mesh);
            final ModelMaterial mm = mtl.getMaterial(group.materialName);
            data.materials.add(mm);
        }
        if (this.verts.size > 0) {
            this.verts.clear();
        }
        if (this.norms.size > 0) {
            this.norms.clear();
        }
        if (this.uvs.size > 0) {
            this.uvs.clear();
        }
        if (this.groups.size > 0) {
            this.groups.clear();
        }
        return data;
    }
    
    private Group setActiveGroup(final String name) {
        for (final Group group : this.groups) {
            if (group.name.equals(name)) {
                return group;
            }
        }
        Group group = new Group(name);
        this.groups.add(group);
        return group;
    }
    
    private int getIndex(final String index, final int size) {
        if (index == null || index.length() == 0) {
            return 0;
        }
        final int idx = Integer.parseInt(index);
        if (idx < 0) {
            return size + idx;
        }
        return idx - 1;
    }
    
    public static class ObjLoaderParameters extends ModelParameters
    {
        public boolean flipV;
        
        public ObjLoaderParameters() {
        }
        
        public ObjLoaderParameters(final boolean flipV) {
            this.flipV = flipV;
        }
    }
    
    private class Group
    {
        final String name;
        String materialName;
        Array<Integer> faces;
        int numFaces;
        boolean hasNorms;
        boolean hasUVs;
        Material mat;
        
        Group(final String name) {
            this.name = name;
            this.faces = new Array<Integer>(200);
            this.numFaces = 0;
            this.mat = new Material("");
            this.materialName = "default";
        }
    }
}
