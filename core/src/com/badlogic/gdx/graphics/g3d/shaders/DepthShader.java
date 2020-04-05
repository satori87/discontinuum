// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class DepthShader extends DefaultShader
{
    private static String defaultVertexShader;
    private static String defaultFragmentShader;
    public final int numBones;
    public final int weights;
    private final FloatAttribute alphaTestAttribute;
    private static final Attributes tmpAttributes;
    
    static {
        DepthShader.defaultVertexShader = null;
        DepthShader.defaultFragmentShader = null;
        tmpAttributes = new Attributes();
    }
    
    public static final String getDefaultVertexShader() {
        if (DepthShader.defaultVertexShader == null) {
            DepthShader.defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/depth.vertex.glsl").readString();
        }
        return DepthShader.defaultVertexShader;
    }
    
    public static final String getDefaultFragmentShader() {
        if (DepthShader.defaultFragmentShader == null) {
            DepthShader.defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/depth.fragment.glsl").readString();
        }
        return DepthShader.defaultFragmentShader;
    }
    
    public static String createPrefix(final Renderable renderable, final Config config) {
        String prefix = DefaultShader.createPrefix(renderable, config);
        if (!config.depthBufferOnly) {
            prefix = String.valueOf(prefix) + "#define PackedDepthFlag\n";
        }
        return prefix;
    }
    
    public DepthShader(final Renderable renderable) {
        this(renderable, new Config());
    }
    
    public DepthShader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }
    
    public DepthShader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, (config.vertexShader != null) ? config.vertexShader : getDefaultVertexShader(), (config.fragmentShader != null) ? config.fragmentShader : getDefaultFragmentShader());
    }
    
    public DepthShader(final Renderable renderable, final Config config, final String prefix, final String vertexShader, final String fragmentShader) {
        this(renderable, config, new ShaderProgram(String.valueOf(prefix) + vertexShader, String.valueOf(prefix) + fragmentShader));
    }
    
    public DepthShader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        super(renderable, config, shaderProgram);
        final Attributes attributes = combineAttributes(renderable);
        this.numBones = ((renderable.bones == null) ? 0 : config.numBones);
        int w = 0;
        for (int n = renderable.meshPart.mesh.getVertexAttributes().size(), i = 0; i < n; ++i) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == 64) {
                w |= 1 << attr.unit;
            }
        }
        this.weights = w;
        this.alphaTestAttribute = new FloatAttribute(FloatAttribute.AlphaTest, config.defaultAlphaTest);
    }
    
    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
    }
    
    @Override
    public void end() {
        super.end();
    }
    
    @Override
    public boolean canRender(final Renderable renderable) {
        final Attributes attributes = combineAttributes(renderable);
        if (attributes.has(BlendingAttribute.Type)) {
            if ((this.attributesMask & BlendingAttribute.Type) != BlendingAttribute.Type) {
                return false;
            }
            if (attributes.has(TextureAttribute.Diffuse) != ((this.attributesMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse)) {
                return false;
            }
        }
        final boolean skinned = (renderable.meshPart.mesh.getVertexAttributes().getMask() & 0x40L) == 0x40L;
        if (skinned != this.numBones > 0) {
            return false;
        }
        if (!skinned) {
            return true;
        }
        int w = 0;
        for (int n = renderable.meshPart.mesh.getVertexAttributes().size(), i = 0; i < n; ++i) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == 64) {
                w |= 1 << attr.unit;
            }
        }
        return w == this.weights;
    }
    
    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes) {
        if (combinedAttributes.has(BlendingAttribute.Type)) {
            final BlendingAttribute blending = (BlendingAttribute)combinedAttributes.get(BlendingAttribute.Type);
            combinedAttributes.remove(BlendingAttribute.Type);
            final boolean hasAlphaTest = combinedAttributes.has(FloatAttribute.AlphaTest);
            if (!hasAlphaTest) {
                combinedAttributes.set(this.alphaTestAttribute);
            }
            if (blending.opacity >= ((FloatAttribute)combinedAttributes.get(FloatAttribute.AlphaTest)).value) {
                super.render(renderable, combinedAttributes);
            }
            if (!hasAlphaTest) {
                combinedAttributes.remove(FloatAttribute.AlphaTest);
            }
            combinedAttributes.set(blending);
        }
        else {
            super.render(renderable, combinedAttributes);
        }
    }
    
    private static final Attributes combineAttributes(final Renderable renderable) {
        DepthShader.tmpAttributes.clear();
        if (renderable.environment != null) {
            DepthShader.tmpAttributes.set(renderable.environment);
        }
        if (renderable.material != null) {
            DepthShader.tmpAttributes.set(renderable.material);
        }
        return DepthShader.tmpAttributes;
    }
    
    public static class Config extends DefaultShader.Config
    {
        public boolean depthBufferOnly;
        public float defaultAlphaTest;
        
        public Config() {
            this.depthBufferOnly = false;
            this.defaultAlphaTest = 0.5f;
            this.defaultCullFace = 1028;
        }
        
        public Config(final String vertexShader, final String fragmentShader) {
            super(vertexShader, fragmentShader);
            this.depthBufferOnly = false;
            this.defaultAlphaTest = 0.5f;
        }
    }
}
