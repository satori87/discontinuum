// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.decals;

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import java.util.Comparator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

public class CameraGroupStrategy implements GroupStrategy, Disposable
{
    private static final int GROUP_OPAQUE = 0;
    private static final int GROUP_BLEND = 1;
    Pool<Array<Decal>> arrayPool;
    Array<Array<Decal>> usedArrays;
    ObjectMap<DecalMaterial, Array<Decal>> materialGroups;
    Camera camera;
    ShaderProgram shader;
    private final Comparator<Decal> cameraSorter;
    
    public CameraGroupStrategy(final Camera camera) {
        this(camera, new Comparator<Decal>() {
            @Override
            public int compare(final Decal o1, final Decal o2) {
                final float dist1 = camera.position.dst(o1.position);
                final float dist2 = camera.position.dst(o2.position);
                return (int)Math.signum(dist2 - dist1);
            }
        });
    }
    
    public CameraGroupStrategy(final Camera camera, final Comparator<Decal> sorter) {
        this.arrayPool = new Pool<Array<Decal>>(16) {
            @Override
            protected Array<Decal> newObject() {
                return new Array<Decal>();
            }
        };
        this.usedArrays = new Array<Array<Decal>>();
        this.materialGroups = new ObjectMap<DecalMaterial, Array<Decal>>();
        this.camera = camera;
        this.cameraSorter = sorter;
        this.createDefaultShader();
    }
    
    public void setCamera(final Camera camera) {
        this.camera = camera;
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    @Override
    public int decideGroup(final Decal decal) {
        return decal.getMaterial().isOpaque() ? 0 : 1;
    }
    
    @Override
    public void beforeGroup(final int group, final Array<Decal> contents) {
        if (group == 1) {
            Gdx.gl.glEnable(3042);
            contents.sort(this.cameraSorter);
        }
        else {
            for (int i = 0, n = contents.size; i < n; ++i) {
                final Decal decal = contents.get(i);
                Array<Decal> materialGroup = this.materialGroups.get(decal.material);
                if (materialGroup == null) {
                    materialGroup = this.arrayPool.obtain();
                    materialGroup.clear();
                    this.usedArrays.add(materialGroup);
                    this.materialGroups.put(decal.material, materialGroup);
                }
                materialGroup.add(decal);
            }
            contents.clear();
            for (final Array<Decal> materialGroup2 : this.materialGroups.values()) {
                contents.addAll(materialGroup2);
            }
            this.materialGroups.clear();
            this.arrayPool.freeAll(this.usedArrays);
            this.usedArrays.clear();
        }
    }
    
    @Override
    public void afterGroup(final int group) {
        if (group == 1) {
            Gdx.gl.glDisable(3042);
        }
    }
    
    @Override
    public void beforeGroups() {
        Gdx.gl.glEnable(2929);
        this.shader.begin();
        this.shader.setUniformMatrix("u_projectionViewMatrix", this.camera.combined);
        this.shader.setUniformi("u_texture", 0);
    }
    
    @Override
    public void afterGroups() {
        this.shader.end();
        Gdx.gl.glDisable(2929);
    }
    
    private void createDefaultShader() {
        final String vertexShader = "attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_color.a = v_color.a * (255.0/254.0);\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n";
        final String fragmentShader = "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}";
        this.shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!this.shader.isCompiled()) {
            throw new IllegalArgumentException("couldn't compile shader: " + this.shader.getLog());
        }
    }
    
    @Override
    public ShaderProgram getGroupShader(final int group) {
        return this.shader;
    }
    
    @Override
    public void dispose() {
        if (this.shader != null) {
            this.shader.dispose();
        }
    }
}
