// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import java.util.Iterator;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SortedIntList;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.utils.Disposable;

public class DecalBatch implements Disposable
{
    private static final int DEFAULT_SIZE = 1000;
    private float[] vertices;
    private Mesh mesh;
    private final SortedIntList<Array<Decal>> groupList;
    private GroupStrategy groupStrategy;
    private final Pool<Array<Decal>> groupPool;
    private final Array<Array<Decal>> usedGroups;
    
    public DecalBatch(final GroupStrategy groupStrategy) {
        this(1000, groupStrategy);
    }
    
    public DecalBatch(final int size, final GroupStrategy groupStrategy) {
        this.groupList = new SortedIntList<Array<Decal>>();
        this.groupPool = new Pool<Array<Decal>>(16) {
            @Override
            protected Array<Decal> newObject() {
                return new Array<Decal>(false, 100);
            }
        };
        this.usedGroups = new Array<Array<Decal>>(16);
        this.initialize(size);
        this.setGroupStrategy(groupStrategy);
    }
    
    public void setGroupStrategy(final GroupStrategy groupStrategy) {
        this.groupStrategy = groupStrategy;
    }
    
    public void initialize(final int size) {
        this.vertices = new float[size * 24];
        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        }
        this.mesh = new Mesh(vertexDataType, false, size * 4, size * 6, new VertexAttribute[] { new VertexAttribute(1, 3, "a_position"), new VertexAttribute(4, 4, "a_color"), new VertexAttribute(16, 2, "a_texCoord0") });
        final short[] indices = new short[size * 6];
        for (int v = 0, i = 0; i < indices.length; i += 6, v += 4) {
            indices[i] = (short)v;
            indices[i + 1] = (short)(v + 2);
            indices[i + 2] = (short)(v + 1);
            indices[i + 3] = (short)(v + 1);
            indices[i + 4] = (short)(v + 2);
            indices[i + 5] = (short)(v + 3);
        }
        this.mesh.setIndices(indices);
    }
    
    public int getSize() {
        return this.vertices.length / 24;
    }
    
    public void add(final Decal decal) {
        final int groupIndex = this.groupStrategy.decideGroup(decal);
        Array<Decal> targetGroup = this.groupList.get(groupIndex);
        if (targetGroup == null) {
            targetGroup = this.groupPool.obtain();
            targetGroup.clear();
            this.usedGroups.add(targetGroup);
            this.groupList.insert(groupIndex, targetGroup);
        }
        targetGroup.add(decal);
    }
    
    public void flush() {
        this.render();
        this.clear();
    }
    
    protected void render() {
        this.groupStrategy.beforeGroups();
        for (final SortedIntList.Node<Array<Decal>> group : this.groupList) {
            this.groupStrategy.beforeGroup(group.index, group.value);
            final ShaderProgram shader = this.groupStrategy.getGroupShader(group.index);
            this.render(shader, group.value);
            this.groupStrategy.afterGroup(group.index);
        }
        this.groupStrategy.afterGroups();
    }
    
    private void render(final ShaderProgram shader, final Array<Decal> decals) {
        DecalMaterial lastMaterial = null;
        int idx = 0;
        for (final Decal decal : decals) {
            if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
                if (idx > 0) {
                    this.flush(shader, idx);
                    idx = 0;
                }
                decal.material.set();
                lastMaterial = decal.material;
            }
            decal.update();
            System.arraycopy(decal.vertices, 0, this.vertices, idx, decal.vertices.length);
            idx += decal.vertices.length;
            if (idx == this.vertices.length) {
                this.flush(shader, idx);
                idx = 0;
            }
        }
        if (idx > 0) {
            this.flush(shader, idx);
        }
    }
    
    protected void flush(final ShaderProgram shader, final int verticesPosition) {
        this.mesh.setVertices(this.vertices, 0, verticesPosition);
        this.mesh.render(shader, 4, 0, verticesPosition / 4);
    }
    
    protected void clear() {
        this.groupList.clear();
        this.groupPool.freeAll(this.usedGroups);
        this.usedGroups.clear();
    }
    
    @Override
    public void dispose() {
        this.clear();
        this.vertices = null;
        this.mesh.dispose();
    }
}
