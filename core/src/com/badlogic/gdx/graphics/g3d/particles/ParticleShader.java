// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.particles;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.Attributes;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

public class ParticleShader extends BaseShader
{
    private static String defaultVertexShader;
    private static String defaultFragmentShader;
    protected static long implementedFlags;
    static final Vector3 TMP_VECTOR3;
    private Renderable renderable;
    private long materialMask;
    private long vertexMask;
    protected final Config config;
    private static final long optionalAttributes;
    Material currentMaterial;
    
    static {
        ParticleShader.defaultVertexShader = null;
        ParticleShader.defaultFragmentShader = null;
        ParticleShader.implementedFlags = (BlendingAttribute.Type | TextureAttribute.Diffuse);
        TMP_VECTOR3 = new Vector3();
        optionalAttributes = (IntAttribute.CullFace | DepthTestAttribute.Type);
    }
    
    public static String getDefaultVertexShader() {
        if (ParticleShader.defaultVertexShader == null) {
            ParticleShader.defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.vertex.glsl").readString();
        }
        return ParticleShader.defaultVertexShader;
    }
    
    public static String getDefaultFragmentShader() {
        if (ParticleShader.defaultFragmentShader == null) {
            ParticleShader.defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/particles/particles.fragment.glsl").readString();
        }
        return ParticleShader.defaultFragmentShader;
    }
    
    public ParticleShader(final Renderable renderable) {
        this(renderable, new Config());
    }
    
    public ParticleShader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }
    
    public ParticleShader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, (config.vertexShader != null) ? config.vertexShader : getDefaultVertexShader(), (config.fragmentShader != null) ? config.fragmentShader : getDefaultFragmentShader());
    }
    
    public ParticleShader(final Renderable renderable, final Config config, final String prefix, final String vertexShader, final String fragmentShader) {
        this(renderable, config, new ShaderProgram(String.valueOf(prefix) + vertexShader, String.valueOf(prefix) + fragmentShader));
    }
    
    public ParticleShader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        this.config = config;
        this.program = shaderProgram;
        this.renderable = renderable;
        this.materialMask = (renderable.material.getMask() | ParticleShader.optionalAttributes);
        this.vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        if (!config.ignoreUnimplemented && (ParticleShader.implementedFlags & this.materialMask) != this.materialMask) {
            throw new GdxRuntimeException("Some attributes not implemented yet (" + this.materialMask + ")");
        }
        this.register(DefaultShader.Inputs.viewTrans, DefaultShader.Setters.viewTrans);
        this.register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
        this.register(DefaultShader.Inputs.projTrans, DefaultShader.Setters.projTrans);
        this.register(Inputs.screenWidth, Setters.screenWidth);
        this.register(DefaultShader.Inputs.cameraUp, Setters.cameraUp);
        this.register(Inputs.cameraRight, Setters.cameraRight);
        this.register(Inputs.cameraInvDirection, Setters.cameraInvDirection);
        this.register(DefaultShader.Inputs.cameraPosition, Setters.cameraPosition);
        this.register(DefaultShader.Inputs.diffuseTexture, DefaultShader.Setters.diffuseTexture);
    }
    
    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        this.init(program, this.renderable);
        this.renderable = null;
    }
    
    public static String createPrefix(final Renderable renderable, final Config config) {
        String prefix = "";
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            prefix = String.valueOf(prefix) + "#version 120\n";
        }
        else {
            prefix = String.valueOf(prefix) + "#version 100\n";
        }
        if (config.type == ParticleType.Billboard) {
            prefix = String.valueOf(prefix) + "#define billboard\n";
            if (config.align == AlignMode.Screen) {
                prefix = String.valueOf(prefix) + "#define screenFacing\n";
            }
            else if (config.align == AlignMode.ViewPoint) {
                prefix = String.valueOf(prefix) + "#define viewPointFacing\n";
            }
        }
        return prefix;
    }
    
    @Override
    public boolean canRender(final Renderable renderable) {
        return this.materialMask == (renderable.material.getMask() | ParticleShader.optionalAttributes) && this.vertexMask == renderable.meshPart.mesh.getVertexAttributes().getMask();
    }
    
    @Override
    public int compareTo(final Shader other) {
        if (other == null) {
            return -1;
        }
        if (other == this) {
            return 0;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ParticleShader && this.equals((ParticleShader)obj);
    }
    
    public boolean equals(final ParticleShader obj) {
        return obj == this;
    }
    
    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
    }
    
    @Override
    public void render(final Renderable renderable) {
        if (!renderable.material.has(BlendingAttribute.Type)) {
            this.context.setBlending(false, 770, 771);
        }
        this.bindMaterial(renderable);
        super.render(renderable);
    }
    
    @Override
    public void end() {
        this.currentMaterial = null;
        super.end();
    }
    
    protected void bindMaterial(final Renderable renderable) {
        if (this.currentMaterial == renderable.material) {
            return;
        }
        final int cullFace = (this.config.defaultCullFace == -1) ? 1029 : this.config.defaultCullFace;
        int depthFunc = (this.config.defaultDepthFunc == -1) ? 515 : this.config.defaultDepthFunc;
        float depthRangeNear = 0.0f;
        float depthRangeFar = 1.0f;
        boolean depthMask = true;
        this.currentMaterial = renderable.material;
        for (final Attribute attr : this.currentMaterial) {
            final long t = attr.type;
            if (BlendingAttribute.is(t)) {
                this.context.setBlending(true, ((BlendingAttribute)attr).sourceFunction, ((BlendingAttribute)attr).destFunction);
            }
            else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                final DepthTestAttribute dta = (DepthTestAttribute)attr;
                depthFunc = dta.depthFunc;
                depthRangeNear = dta.depthRangeNear;
                depthRangeFar = dta.depthRangeFar;
                depthMask = dta.depthMask;
            }
            else {
                if (!this.config.ignoreUnimplemented) {
                    throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
                }
                continue;
            }
        }
        this.context.setCullFace(cullFace);
        this.context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
        this.context.setDepthMask(depthMask);
    }
    
    @Override
    public void dispose() {
        this.program.dispose();
        super.dispose();
    }
    
    public int getDefaultCullFace() {
        return (this.config.defaultCullFace == -1) ? 1029 : this.config.defaultCullFace;
    }
    
    public void setDefaultCullFace(final int cullFace) {
        this.config.defaultCullFace = cullFace;
    }
    
    public int getDefaultDepthFunc() {
        return (this.config.defaultDepthFunc == -1) ? 515 : this.config.defaultDepthFunc;
    }
    
    public void setDefaultDepthFunc(final int depthFunc) {
        this.config.defaultDepthFunc = depthFunc;
    }
    
    public enum AlignMode
    {
        Screen("Screen", 0), 
        ViewPoint("ViewPoint", 1);
        
        private AlignMode(final String name, final int ordinal) {
        }
    }
    
    public enum ParticleType
    {
        Billboard("Billboard", 0), 
        Point("Point", 1);
        
        private ParticleType(final String name, final int ordinal) {
        }
    }
    
    public static class Config
    {
        public String vertexShader;
        public String fragmentShader;
        public boolean ignoreUnimplemented;
        public int defaultCullFace;
        public int defaultDepthFunc;
        public AlignMode align;
        public ParticleType type;
        
        public Config() {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.align = AlignMode.Screen;
            this.type = ParticleType.Billboard;
        }
        
        public Config(final AlignMode align, final ParticleType type) {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.align = AlignMode.Screen;
            this.type = ParticleType.Billboard;
            this.align = align;
            this.type = type;
        }
        
        public Config(final AlignMode align) {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.align = AlignMode.Screen;
            this.type = ParticleType.Billboard;
            this.align = align;
        }
        
        public Config(final ParticleType type) {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.align = AlignMode.Screen;
            this.type = ParticleType.Billboard;
            this.type = type;
        }
        
        public Config(final String vertexShader, final String fragmentShader) {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.align = AlignMode.Screen;
            this.type = ParticleType.Billboard;
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
        }
    }
    
    public static class Inputs
    {
        public static final Uniform cameraRight;
        public static final Uniform cameraInvDirection;
        public static final Uniform screenWidth;
        public static final Uniform regionSize;
        
        static {
            cameraRight = new Uniform("u_cameraRight");
            cameraInvDirection = new Uniform("u_cameraInvDirection");
            screenWidth = new Uniform("u_screenWidth");
            regionSize = new Uniform("u_regionSize");
        }
    }
    
    public static class Setters
    {
        public static final Setter cameraRight;
        public static final Setter cameraUp;
        public static final Setter cameraInvDirection;
        public static final Setter cameraPosition;
        public static final Setter screenWidth;
        public static final Setter worldViewTrans;
        
        static {
            cameraRight = new Setter() {
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return true;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ParticleShader.TMP_VECTOR3.set(shader.camera.direction).crs(shader.camera.up).nor());
                }
            };
            cameraUp = new Setter() {
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return true;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ParticleShader.TMP_VECTOR3.set(shader.camera.up).nor());
                }
            };
            cameraInvDirection = new Setter() {
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return true;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ParticleShader.TMP_VECTOR3.set(-shader.camera.direction.x, -shader.camera.direction.y, -shader.camera.direction.z).nor());
                }
            };
            cameraPosition = new Setter() {
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return true;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.position);
                }
            };
            screenWidth = new Setter() {
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return true;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, (float)Gdx.graphics.getWidth());
                }
            };
            worldViewTrans = new Setter() {
                final Matrix4 temp = new Matrix4();
                
                @Override
                public boolean isGlobal(final BaseShader shader, final int inputID) {
                    return false;
                }
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, this.temp.set(shader.camera.view).mul(renderable.worldTransform));
                }
            };
        }
    }
}
