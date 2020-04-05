// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Shader;

public abstract class BaseShader implements Shader
{
    private final Array<String> uniforms;
    private final Array<Validator> validators;
    private final Array<Setter> setters;
    private int[] locations;
    private final IntArray globalUniforms;
    private final IntArray localUniforms;
    private final IntIntMap attributes;
    public ShaderProgram program;
    public RenderContext context;
    public Camera camera;
    private Mesh currentMesh;
    private final IntArray tempArray;
    private Attributes combinedAttributes;
    
    public BaseShader() {
        this.uniforms = new Array<String>();
        this.validators = new Array<Validator>();
        this.setters = new Array<Setter>();
        this.globalUniforms = new IntArray();
        this.localUniforms = new IntArray();
        this.attributes = new IntIntMap();
        this.tempArray = new IntArray();
        this.combinedAttributes = new Attributes();
    }
    
    public int register(final String alias, final Validator validator, final Setter setter) {
        if (this.locations != null) {
            throw new GdxRuntimeException("Cannot register an uniform after initialization");
        }
        final int existing = this.getUniformID(alias);
        if (existing >= 0) {
            this.validators.set(existing, validator);
            this.setters.set(existing, setter);
            return existing;
        }
        this.uniforms.add(alias);
        this.validators.add(validator);
        this.setters.add(setter);
        return this.uniforms.size - 1;
    }
    
    public int register(final String alias, final Validator validator) {
        return this.register(alias, validator, null);
    }
    
    public int register(final String alias, final Setter setter) {
        return this.register(alias, null, setter);
    }
    
    public int register(final String alias) {
        return this.register(alias, null, null);
    }
    
    public int register(final Uniform uniform, final Setter setter) {
        return this.register(uniform.alias, uniform, setter);
    }
    
    public int register(final Uniform uniform) {
        return this.register(uniform, null);
    }
    
    public int getUniformID(final String alias) {
        for (int n = this.uniforms.size, i = 0; i < n; ++i) {
            if (this.uniforms.get(i).equals(alias)) {
                return i;
            }
        }
        return -1;
    }
    
    public String getUniformAlias(final int id) {
        return this.uniforms.get(id);
    }
    
    public void init(final ShaderProgram program, final Renderable renderable) {
        if (this.locations != null) {
            throw new GdxRuntimeException("Already initialized");
        }
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
        this.program = program;
        final int n = this.uniforms.size;
        this.locations = new int[n];
        for (int i = 0; i < n; ++i) {
            final String input = this.uniforms.get(i);
            final Validator validator = this.validators.get(i);
            final Setter setter = this.setters.get(i);
            if (validator != null && !validator.validate(this, i, renderable)) {
                this.locations[i] = -1;
            }
            else {
                this.locations[i] = program.fetchUniformLocation(input, false);
                if (this.locations[i] >= 0 && setter != null) {
                    if (setter.isGlobal(this, i)) {
                        this.globalUniforms.add(i);
                    }
                    else {
                        this.localUniforms.add(i);
                    }
                }
            }
            if (this.locations[i] < 0) {
                this.validators.set(i, null);
                this.setters.set(i, null);
            }
        }
        if (renderable != null) {
            final VertexAttributes attrs = renderable.meshPart.mesh.getVertexAttributes();
            for (int c = attrs.size(), j = 0; j < c; ++j) {
                final VertexAttribute attr = attrs.get(j);
                final int location = program.getAttributeLocation(attr.alias);
                if (location >= 0) {
                    this.attributes.put(attr.getKey(), location);
                }
            }
        }
    }
    
    @Override
    public void begin(final Camera camera, final RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.currentMesh = null;
        for (int i = 0; i < this.globalUniforms.size; ++i) {
            final int u;
            if (this.setters.get(u = this.globalUniforms.get(i)) != null) {
                this.setters.get(u).set(this, u, null, null);
            }
        }
    }
    
    private final int[] getAttributeLocations(final VertexAttributes attrs) {
        this.tempArray.clear();
        for (int n = attrs.size(), i = 0; i < n; ++i) {
            this.tempArray.add(this.attributes.get(attrs.get(i).getKey(), -1));
        }
        this.tempArray.shrink();
        return this.tempArray.items;
    }
    
    @Override
    public void render(final Renderable renderable) {
        if (renderable.worldTransform.det3x3() == 0.0f) {
            return;
        }
        this.combinedAttributes.clear();
        if (renderable.environment != null) {
            this.combinedAttributes.set(renderable.environment);
        }
        if (renderable.material != null) {
            this.combinedAttributes.set(renderable.material);
        }
        this.render(renderable, this.combinedAttributes);
    }
    
    public void render(final Renderable renderable, final Attributes combinedAttributes) {
        for (int i = 0; i < this.localUniforms.size; ++i) {
            final int u;
            if (this.setters.get(u = this.localUniforms.get(i)) != null) {
                this.setters.get(u).set(this, u, renderable, combinedAttributes);
            }
        }
        if (this.currentMesh != renderable.meshPart.mesh) {
            if (this.currentMesh != null) {
                this.currentMesh.unbind(this.program, this.tempArray.items);
            }
            (this.currentMesh = renderable.meshPart.mesh).bind(this.program, this.getAttributeLocations(renderable.meshPart.mesh.getVertexAttributes()));
        }
        renderable.meshPart.render(this.program, false);
    }
    
    @Override
    public void end() {
        if (this.currentMesh != null) {
            this.currentMesh.unbind(this.program, this.tempArray.items);
            this.currentMesh = null;
        }
        this.program.end();
    }
    
    @Override
    public void dispose() {
        this.program = null;
        this.uniforms.clear();
        this.validators.clear();
        this.setters.clear();
        this.localUniforms.clear();
        this.globalUniforms.clear();
        this.locations = null;
    }
    
    public final boolean has(final int inputID) {
        return inputID >= 0 && inputID < this.locations.length && this.locations[inputID] >= 0;
    }
    
    public final int loc(final int inputID) {
        return (inputID >= 0 && inputID < this.locations.length) ? this.locations[inputID] : -1;
    }
    
    public final boolean set(final int uniform, final Matrix4 value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformMatrix(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final Matrix3 value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformMatrix(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final Vector3 value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final Vector2 value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final Color value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final float value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final float v1, final float v2) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], v1, v2);
        return true;
    }
    
    public final boolean set(final int uniform, final float v1, final float v2, final float v3) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], v1, v2, v3);
        return true;
    }
    
    public final boolean set(final int uniform, final float v1, final float v2, final float v3, final float v4) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformf(this.locations[uniform], v1, v2, v3, v4);
        return true;
    }
    
    public final boolean set(final int uniform, final int value) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], value);
        return true;
    }
    
    public final boolean set(final int uniform, final int v1, final int v2) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], v1, v2);
        return true;
    }
    
    public final boolean set(final int uniform, final int v1, final int v2, final int v3) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], v1, v2, v3);
        return true;
    }
    
    public final boolean set(final int uniform, final int v1, final int v2, final int v3, final int v4) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], v1, v2, v3, v4);
        return true;
    }
    
    public final boolean set(final int uniform, final TextureDescriptor textureDesc) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], this.context.textureBinder.bind(textureDesc));
        return true;
    }
    
    public final boolean set(final int uniform, final GLTexture texture) {
        if (this.locations[uniform] < 0) {
            return false;
        }
        this.program.setUniformi(this.locations[uniform], this.context.textureBinder.bind(texture));
        return true;
    }
    
    public abstract static class GlobalSetter implements Setter
    {
        @Override
        public boolean isGlobal(final BaseShader shader, final int inputID) {
            return true;
        }
    }
    
    public abstract static class LocalSetter implements Setter
    {
        @Override
        public boolean isGlobal(final BaseShader shader, final int inputID) {
            return false;
        }
    }
    
    public static class Uniform implements Validator
    {
        public final String alias;
        public final long materialMask;
        public final long environmentMask;
        public final long overallMask;
        
        public Uniform(final String alias, final long materialMask, final long environmentMask, final long overallMask) {
            this.alias = alias;
            this.materialMask = materialMask;
            this.environmentMask = environmentMask;
            this.overallMask = overallMask;
        }
        
        public Uniform(final String alias, final long materialMask, final long environmentMask) {
            this(alias, materialMask, environmentMask, 0L);
        }
        
        public Uniform(final String alias, final long overallMask) {
            this(alias, 0L, 0L, overallMask);
        }
        
        public Uniform(final String alias) {
            this(alias, 0L, 0L);
        }
        
        @Override
        public boolean validate(final BaseShader shader, final int inputID, final Renderable renderable) {
            final long matFlags = (renderable != null && renderable.material != null) ? renderable.material.getMask() : 0L;
            final long envFlags = (renderable != null && renderable.environment != null) ? renderable.environment.getMask() : 0L;
            return (matFlags & this.materialMask) == this.materialMask && (envFlags & this.environmentMask) == this.environmentMask && ((matFlags | envFlags) & this.overallMask) == this.overallMask;
        }
    }
    
    public interface Setter
    {
        boolean isGlobal(final BaseShader p0, final int p1);
        
        void set(final BaseShader p0, final int p1, final Renderable p2, final Attributes p3);
    }
    
    public interface Validator
    {
        boolean validate(final BaseShader p0, final int p1, final Renderable p2);
    }
}
