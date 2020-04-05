// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.shaders;

import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;

public class DefaultShader extends BaseShader
{
    private static String defaultVertexShader;
    private static String defaultFragmentShader;
    protected static long implementedFlags;
    @Deprecated
    public static int defaultCullFace;
    @Deprecated
    public static int defaultDepthFunc;
    public final int u_projTrans;
    public final int u_viewTrans;
    public final int u_projViewTrans;
    public final int u_cameraPosition;
    public final int u_cameraDirection;
    public final int u_cameraUp;
    public final int u_cameraNearFar;
    public final int u_time;
    public final int u_worldTrans;
    public final int u_viewWorldTrans;
    public final int u_projViewWorldTrans;
    public final int u_normalMatrix;
    public final int u_bones;
    public final int u_shininess;
    public final int u_opacity;
    public final int u_diffuseColor;
    public final int u_diffuseTexture;
    public final int u_diffuseUVTransform;
    public final int u_specularColor;
    public final int u_specularTexture;
    public final int u_specularUVTransform;
    public final int u_emissiveColor;
    public final int u_emissiveTexture;
    public final int u_emissiveUVTransform;
    public final int u_reflectionColor;
    public final int u_reflectionTexture;
    public final int u_reflectionUVTransform;
    public final int u_normalTexture;
    public final int u_normalUVTransform;
    public final int u_ambientTexture;
    public final int u_ambientUVTransform;
    public final int u_alphaTest;
    protected final int u_ambientCubemap;
    protected final int u_environmentCubemap;
    protected final int u_dirLights0color;
    protected final int u_dirLights0direction;
    protected final int u_dirLights1color;
    protected final int u_pointLights0color;
    protected final int u_pointLights0position;
    protected final int u_pointLights0intensity;
    protected final int u_pointLights1color;
    protected final int u_spotLights0color;
    protected final int u_spotLights0position;
    protected final int u_spotLights0intensity;
    protected final int u_spotLights0direction;
    protected final int u_spotLights0cutoffAngle;
    protected final int u_spotLights0exponent;
    protected final int u_spotLights1color;
    protected final int u_fogColor;
    protected final int u_shadowMapProjViewTrans;
    protected final int u_shadowTexture;
    protected final int u_shadowPCFOffset;
    protected int dirLightsLoc;
    protected int dirLightsColorOffset;
    protected int dirLightsDirectionOffset;
    protected int dirLightsSize;
    protected int pointLightsLoc;
    protected int pointLightsColorOffset;
    protected int pointLightsPositionOffset;
    protected int pointLightsIntensityOffset;
    protected int pointLightsSize;
    protected int spotLightsLoc;
    protected int spotLightsColorOffset;
    protected int spotLightsPositionOffset;
    protected int spotLightsDirectionOffset;
    protected int spotLightsIntensityOffset;
    protected int spotLightsCutoffAngleOffset;
    protected int spotLightsExponentOffset;
    protected int spotLightsSize;
    protected final boolean lighting;
    protected final boolean environmentCubemap;
    protected final boolean shadowMap;
    protected final AmbientCubemap ambientCubemap;
    protected final DirectionalLight[] directionalLights;
    protected final PointLight[] pointLights;
    protected final SpotLight[] spotLights;
    private Renderable renderable;
    protected final long attributesMask;
    private final long vertexMask;
    protected final Config config;
    private static final long optionalAttributes;
    private static final Attributes tmpAttributes;
    private final Matrix3 normalMatrix;
    private float time;
    private boolean lightsSet;
    private final Vector3 tmpV1;
    
    static {
        DefaultShader.defaultVertexShader = null;
        DefaultShader.defaultFragmentShader = null;
        DefaultShader.implementedFlags = (BlendingAttribute.Type | TextureAttribute.Diffuse | ColorAttribute.Diffuse | ColorAttribute.Specular | FloatAttribute.Shininess);
        DefaultShader.defaultCullFace = 1029;
        DefaultShader.defaultDepthFunc = 515;
        optionalAttributes = (IntAttribute.CullFace | DepthTestAttribute.Type);
        tmpAttributes = new Attributes();
    }
    
    public static String getDefaultVertexShader() {
        if (DefaultShader.defaultVertexShader == null) {
            DefaultShader.defaultVertexShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl").readString();
        }
        return DefaultShader.defaultVertexShader;
    }
    
    public static String getDefaultFragmentShader() {
        if (DefaultShader.defaultFragmentShader == null) {
            DefaultShader.defaultFragmentShader = Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl").readString();
        }
        return DefaultShader.defaultFragmentShader;
    }
    
    public DefaultShader(final Renderable renderable) {
        this(renderable, new Config());
    }
    
    public DefaultShader(final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }
    
    public DefaultShader(final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, (config.vertexShader != null) ? config.vertexShader : getDefaultVertexShader(), (config.fragmentShader != null) ? config.fragmentShader : getDefaultFragmentShader());
    }
    
    public DefaultShader(final Renderable renderable, final Config config, final String prefix, final String vertexShader, final String fragmentShader) {
        this(renderable, config, new ShaderProgram(String.valueOf(prefix) + vertexShader, String.valueOf(prefix) + fragmentShader));
    }
    
    public DefaultShader(final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        this.u_dirLights0color = this.register(new Uniform("u_dirLights[0].color"));
        this.u_dirLights0direction = this.register(new Uniform("u_dirLights[0].direction"));
        this.u_dirLights1color = this.register(new Uniform("u_dirLights[1].color"));
        this.u_pointLights0color = this.register(new Uniform("u_pointLights[0].color"));
        this.u_pointLights0position = this.register(new Uniform("u_pointLights[0].position"));
        this.u_pointLights0intensity = this.register(new Uniform("u_pointLights[0].intensity"));
        this.u_pointLights1color = this.register(new Uniform("u_pointLights[1].color"));
        this.u_spotLights0color = this.register(new Uniform("u_spotLights[0].color"));
        this.u_spotLights0position = this.register(new Uniform("u_spotLights[0].position"));
        this.u_spotLights0intensity = this.register(new Uniform("u_spotLights[0].intensity"));
        this.u_spotLights0direction = this.register(new Uniform("u_spotLights[0].direction"));
        this.u_spotLights0cutoffAngle = this.register(new Uniform("u_spotLights[0].cutoffAngle"));
        this.u_spotLights0exponent = this.register(new Uniform("u_spotLights[0].exponent"));
        this.u_spotLights1color = this.register(new Uniform("u_spotLights[1].color"));
        this.u_fogColor = this.register(new Uniform("u_fogColor"));
        this.u_shadowMapProjViewTrans = this.register(new Uniform("u_shadowMapProjViewTrans"));
        this.u_shadowTexture = this.register(new Uniform("u_shadowTexture"));
        this.u_shadowPCFOffset = this.register(new Uniform("u_shadowPCFOffset"));
        this.ambientCubemap = new AmbientCubemap();
        this.normalMatrix = new Matrix3();
        this.tmpV1 = new Vector3();
        final Attributes attributes = combineAttributes(renderable);
        this.config = config;
        this.program = shaderProgram;
        this.lighting = (renderable.environment != null);
        this.environmentCubemap = (attributes.has(CubemapAttribute.EnvironmentMap) || (this.lighting && attributes.has(CubemapAttribute.EnvironmentMap)));
        this.shadowMap = (this.lighting && renderable.environment.shadowMap != null);
        this.renderable = renderable;
        this.attributesMask = (attributes.getMask() | DefaultShader.optionalAttributes);
        this.vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked();
        this.directionalLights = new DirectionalLight[(this.lighting && config.numDirectionalLights > 0) ? config.numDirectionalLights : 0];
        for (int i = 0; i < this.directionalLights.length; ++i) {
            this.directionalLights[i] = new DirectionalLight();
        }
        this.pointLights = new PointLight[(this.lighting && config.numPointLights > 0) ? config.numPointLights : 0];
        for (int i = 0; i < this.pointLights.length; ++i) {
            this.pointLights[i] = new PointLight();
        }
        this.spotLights = new SpotLight[(this.lighting && config.numSpotLights > 0) ? config.numSpotLights : 0];
        for (int i = 0; i < this.spotLights.length; ++i) {
            this.spotLights[i] = new SpotLight();
        }
        if (!config.ignoreUnimplemented && (DefaultShader.implementedFlags & this.attributesMask) != this.attributesMask) {
            throw new GdxRuntimeException("Some attributes not implemented yet (" + this.attributesMask + ")");
        }
        this.u_projTrans = this.register(Inputs.projTrans, Setters.projTrans);
        this.u_viewTrans = this.register(Inputs.viewTrans, Setters.viewTrans);
        this.u_projViewTrans = this.register(Inputs.projViewTrans, Setters.projViewTrans);
        this.u_cameraPosition = this.register(Inputs.cameraPosition, Setters.cameraPosition);
        this.u_cameraDirection = this.register(Inputs.cameraDirection, Setters.cameraDirection);
        this.u_cameraUp = this.register(Inputs.cameraUp, Setters.cameraUp);
        this.u_cameraNearFar = this.register(Inputs.cameraNearFar, Setters.cameraNearFar);
        this.u_time = this.register(new Uniform("u_time"));
        this.u_worldTrans = this.register(Inputs.worldTrans, Setters.worldTrans);
        this.u_viewWorldTrans = this.register(Inputs.viewWorldTrans, Setters.viewWorldTrans);
        this.u_projViewWorldTrans = this.register(Inputs.projViewWorldTrans, Setters.projViewWorldTrans);
        this.u_normalMatrix = this.register(Inputs.normalMatrix, Setters.normalMatrix);
        this.u_bones = ((renderable.bones != null && config.numBones > 0) ? this.register(Inputs.bones, new Setters.Bones(config.numBones)) : -1);
        this.u_shininess = this.register(Inputs.shininess, Setters.shininess);
        this.u_opacity = this.register(Inputs.opacity);
        this.u_diffuseColor = this.register(Inputs.diffuseColor, Setters.diffuseColor);
        this.u_diffuseTexture = this.register(Inputs.diffuseTexture, Setters.diffuseTexture);
        this.u_diffuseUVTransform = this.register(Inputs.diffuseUVTransform, Setters.diffuseUVTransform);
        this.u_specularColor = this.register(Inputs.specularColor, Setters.specularColor);
        this.u_specularTexture = this.register(Inputs.specularTexture, Setters.specularTexture);
        this.u_specularUVTransform = this.register(Inputs.specularUVTransform, Setters.specularUVTransform);
        this.u_emissiveColor = this.register(Inputs.emissiveColor, Setters.emissiveColor);
        this.u_emissiveTexture = this.register(Inputs.emissiveTexture, Setters.emissiveTexture);
        this.u_emissiveUVTransform = this.register(Inputs.emissiveUVTransform, Setters.emissiveUVTransform);
        this.u_reflectionColor = this.register(Inputs.reflectionColor, Setters.reflectionColor);
        this.u_reflectionTexture = this.register(Inputs.reflectionTexture, Setters.reflectionTexture);
        this.u_reflectionUVTransform = this.register(Inputs.reflectionUVTransform, Setters.reflectionUVTransform);
        this.u_normalTexture = this.register(Inputs.normalTexture, Setters.normalTexture);
        this.u_normalUVTransform = this.register(Inputs.normalUVTransform, Setters.normalUVTransform);
        this.u_ambientTexture = this.register(Inputs.ambientTexture, Setters.ambientTexture);
        this.u_ambientUVTransform = this.register(Inputs.ambientUVTransform, Setters.ambientUVTransform);
        this.u_alphaTest = this.register(Inputs.alphaTest);
        this.u_ambientCubemap = (this.lighting ? this.register(Inputs.ambientCube, new Setters.ACubemap(config.numDirectionalLights, config.numPointLights)) : -1);
        this.u_environmentCubemap = (this.environmentCubemap ? this.register(Inputs.environmentCubemap, Setters.environmentCubemap) : -1);
    }
    
    @Override
    public void init() {
        final ShaderProgram program = this.program;
        this.program = null;
        this.init(program, this.renderable);
        this.renderable = null;
        this.dirLightsLoc = this.loc(this.u_dirLights0color);
        this.dirLightsColorOffset = this.loc(this.u_dirLights0color) - this.dirLightsLoc;
        this.dirLightsDirectionOffset = this.loc(this.u_dirLights0direction) - this.dirLightsLoc;
        this.dirLightsSize = this.loc(this.u_dirLights1color) - this.dirLightsLoc;
        if (this.dirLightsSize < 0) {
            this.dirLightsSize = 0;
        }
        this.pointLightsLoc = this.loc(this.u_pointLights0color);
        this.pointLightsColorOffset = this.loc(this.u_pointLights0color) - this.pointLightsLoc;
        this.pointLightsPositionOffset = this.loc(this.u_pointLights0position) - this.pointLightsLoc;
        this.pointLightsIntensityOffset = (this.has(this.u_pointLights0intensity) ? (this.loc(this.u_pointLights0intensity) - this.pointLightsLoc) : -1);
        this.pointLightsSize = this.loc(this.u_pointLights1color) - this.pointLightsLoc;
        if (this.pointLightsSize < 0) {
            this.pointLightsSize = 0;
        }
        this.spotLightsLoc = this.loc(this.u_spotLights0color);
        this.spotLightsColorOffset = this.loc(this.u_spotLights0color) - this.spotLightsLoc;
        this.spotLightsPositionOffset = this.loc(this.u_spotLights0position) - this.spotLightsLoc;
        this.spotLightsDirectionOffset = this.loc(this.u_spotLights0direction) - this.spotLightsLoc;
        this.spotLightsIntensityOffset = (this.has(this.u_spotLights0intensity) ? (this.loc(this.u_spotLights0intensity) - this.spotLightsLoc) : -1);
        this.spotLightsCutoffAngleOffset = this.loc(this.u_spotLights0cutoffAngle) - this.spotLightsLoc;
        this.spotLightsExponentOffset = this.loc(this.u_spotLights0exponent) - this.spotLightsLoc;
        this.spotLightsSize = this.loc(this.u_spotLights1color) - this.spotLightsLoc;
        if (this.spotLightsSize < 0) {
            this.spotLightsSize = 0;
        }
    }
    
    private static final boolean and(final long mask, final long flag) {
        return (mask & flag) == flag;
    }
    
    private static final boolean or(final long mask, final long flag) {
        return (mask & flag) != 0x0L;
    }
    
    private static final Attributes combineAttributes(final Renderable renderable) {
        DefaultShader.tmpAttributes.clear();
        if (renderable.environment != null) {
            DefaultShader.tmpAttributes.set(renderable.environment);
        }
        if (renderable.material != null) {
            DefaultShader.tmpAttributes.set(renderable.material);
        }
        return DefaultShader.tmpAttributes;
    }
    
    private static final long combineAttributeMasks(final Renderable renderable) {
        long mask = 0L;
        if (renderable.environment != null) {
            mask |= renderable.environment.getMask();
        }
        if (renderable.material != null) {
            mask |= renderable.material.getMask();
        }
        return mask;
    }
    
    public static String createPrefix(final Renderable renderable, final Config config) {
        final Attributes attributes = combineAttributes(renderable);
        String prefix = "";
        final long attributesMask = attributes.getMask();
        final long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        if (and(vertexMask, 1L)) {
            prefix = String.valueOf(prefix) + "#define positionFlag\n";
        }
        if (or(vertexMask, 6L)) {
            prefix = String.valueOf(prefix) + "#define colorFlag\n";
        }
        if (and(vertexMask, 256L)) {
            prefix = String.valueOf(prefix) + "#define binormalFlag\n";
        }
        if (and(vertexMask, 128L)) {
            prefix = String.valueOf(prefix) + "#define tangentFlag\n";
        }
        if (and(vertexMask, 8L)) {
            prefix = String.valueOf(prefix) + "#define normalFlag\n";
        }
        if ((and(vertexMask, 8L) || and(vertexMask, 384L)) && renderable.environment != null) {
            prefix = String.valueOf(prefix) + "#define lightingFlag\n";
            prefix = String.valueOf(prefix) + "#define ambientCubemapFlag\n";
            prefix = String.valueOf(prefix) + "#define numDirectionalLights " + config.numDirectionalLights + "\n";
            prefix = String.valueOf(prefix) + "#define numPointLights " + config.numPointLights + "\n";
            prefix = String.valueOf(prefix) + "#define numSpotLights " + config.numSpotLights + "\n";
            if (attributes.has(ColorAttribute.Fog)) {
                prefix = String.valueOf(prefix) + "#define fogFlag\n";
            }
            if (renderable.environment.shadowMap != null) {
                prefix = String.valueOf(prefix) + "#define shadowMapFlag\n";
            }
            if (attributes.has(CubemapAttribute.EnvironmentMap)) {
                prefix = String.valueOf(prefix) + "#define environmentCubemapFlag\n";
            }
        }
        for (int n = renderable.meshPart.mesh.getVertexAttributes().size(), i = 0; i < n; ++i) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == 64) {
                prefix = String.valueOf(prefix) + "#define boneWeight" + attr.unit + "Flag\n";
            }
            else if (attr.usage == 16) {
                prefix = String.valueOf(prefix) + "#define texCoord" + attr.unit + "Flag\n";
            }
        }
        if ((attributesMask & BlendingAttribute.Type) == BlendingAttribute.Type) {
            prefix = String.valueOf(prefix) + "#define blendedFlag\n";
        }
        if ((attributesMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse) {
            prefix = String.valueOf(prefix) + "#define diffuseTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define diffuseTextureCoord texCoord0\n";
        }
        if ((attributesMask & TextureAttribute.Specular) == TextureAttribute.Specular) {
            prefix = String.valueOf(prefix) + "#define specularTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define specularTextureCoord texCoord0\n";
        }
        if ((attributesMask & TextureAttribute.Normal) == TextureAttribute.Normal) {
            prefix = String.valueOf(prefix) + "#define normalTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define normalTextureCoord texCoord0\n";
        }
        if ((attributesMask & TextureAttribute.Emissive) == TextureAttribute.Emissive) {
            prefix = String.valueOf(prefix) + "#define emissiveTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define emissiveTextureCoord texCoord0\n";
        }
        if ((attributesMask & TextureAttribute.Reflection) == TextureAttribute.Reflection) {
            prefix = String.valueOf(prefix) + "#define reflectionTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define reflectionTextureCoord texCoord0\n";
        }
        if ((attributesMask & TextureAttribute.Ambient) == TextureAttribute.Ambient) {
            prefix = String.valueOf(prefix) + "#define ambientTextureFlag\n";
            prefix = String.valueOf(prefix) + "#define ambientTextureCoord texCoord0\n";
        }
        if ((attributesMask & ColorAttribute.Diffuse) == ColorAttribute.Diffuse) {
            prefix = String.valueOf(prefix) + "#define diffuseColorFlag\n";
        }
        if ((attributesMask & ColorAttribute.Specular) == ColorAttribute.Specular) {
            prefix = String.valueOf(prefix) + "#define specularColorFlag\n";
        }
        if ((attributesMask & ColorAttribute.Emissive) == ColorAttribute.Emissive) {
            prefix = String.valueOf(prefix) + "#define emissiveColorFlag\n";
        }
        if ((attributesMask & ColorAttribute.Reflection) == ColorAttribute.Reflection) {
            prefix = String.valueOf(prefix) + "#define reflectionColorFlag\n";
        }
        if ((attributesMask & FloatAttribute.Shininess) == FloatAttribute.Shininess) {
            prefix = String.valueOf(prefix) + "#define shininessFlag\n";
        }
        if ((attributesMask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
            prefix = String.valueOf(prefix) + "#define alphaTestFlag\n";
        }
        if (renderable.bones != null && config.numBones > 0) {
            prefix = String.valueOf(prefix) + "#define numBones " + config.numBones + "\n";
        }
        return prefix;
    }
    
    @Override
    public boolean canRender(final Renderable renderable) {
        final long renderableMask = combineAttributeMasks(renderable);
        return this.attributesMask == (renderableMask | DefaultShader.optionalAttributes) && this.vertexMask == renderable.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked() && renderable.environment != null == this.lighting;
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
        return obj instanceof DefaultShader && this.equals((DefaultShader)obj);
    }
    
    public boolean equals(final DefaultShader obj) {
        return obj == this;
    }
    
    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);
        DirectionalLight[] directionalLights;
        for (int length = (directionalLights = this.directionalLights).length, i = 0; i < length; ++i) {
            final DirectionalLight dirLight = directionalLights[i];
            dirLight.set(0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f);
        }
        PointLight[] pointLights;
        for (int length2 = (pointLights = this.pointLights).length, j = 0; j < length2; ++j) {
            final PointLight pointLight = pointLights[j];
            pointLight.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
        SpotLight[] spotLights;
        for (int length3 = (spotLights = this.spotLights).length, k = 0; k < length3; ++k) {
            final SpotLight spotLight = spotLights[k];
            spotLight.set(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        }
        this.lightsSet = false;
        if (this.has(this.u_time)) {
            this.set(this.u_time, this.time += Gdx.graphics.getDeltaTime());
        }
    }
    
    @Override
    public void render(final Renderable renderable, final Attributes combinedAttributes) {
        if (!combinedAttributes.has(BlendingAttribute.Type)) {
            this.context.setBlending(false, 770, 771);
        }
        this.bindMaterial(combinedAttributes);
        if (this.lighting) {
            this.bindLights(renderable, combinedAttributes);
        }
        super.render(renderable, combinedAttributes);
    }
    
    @Override
    public void end() {
        super.end();
    }
    
    protected void bindMaterial(final Attributes attributes) {
        int cullFace = (this.config.defaultCullFace == -1) ? DefaultShader.defaultCullFace : this.config.defaultCullFace;
        int depthFunc = (this.config.defaultDepthFunc == -1) ? DefaultShader.defaultDepthFunc : this.config.defaultDepthFunc;
        float depthRangeNear = 0.0f;
        float depthRangeFar = 1.0f;
        boolean depthMask = true;
        for (final Attribute attr : attributes) {
            final long t = attr.type;
            if (BlendingAttribute.is(t)) {
                this.context.setBlending(true, ((BlendingAttribute)attr).sourceFunction, ((BlendingAttribute)attr).destFunction);
                this.set(this.u_opacity, ((BlendingAttribute)attr).opacity);
            }
            else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace) {
                cullFace = ((IntAttribute)attr).value;
            }
            else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
                this.set(this.u_alphaTest, ((FloatAttribute)attr).value);
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
    
    protected void bindLights(final Renderable renderable, final Attributes attributes) {
        final Environment lights = renderable.environment;
        final DirectionalLightsAttribute dla = attributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirs = (dla == null) ? null : dla.lights;
        final PointLightsAttribute pla = attributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> points = (pla == null) ? null : pla.lights;
        final SpotLightsAttribute sla = attributes.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        final Array<SpotLight> spots = (sla == null) ? null : sla.lights;
        if (this.dirLightsLoc >= 0) {
            for (int i = 0; i < this.directionalLights.length; ++i) {
                if (dirs == null || i >= dirs.size) {
                    if (this.lightsSet && this.directionalLights[i].color.r == 0.0f && this.directionalLights[i].color.g == 0.0f && this.directionalLights[i].color.b == 0.0f) {
                        continue;
                    }
                    this.directionalLights[i].color.set(0.0f, 0.0f, 0.0f, 1.0f);
                }
                else {
                    if (this.lightsSet && this.directionalLights[i].equals(dirs.get(i))) {
                        continue;
                    }
                    this.directionalLights[i].set(dirs.get(i));
                }
                final int idx = this.dirLightsLoc + i * this.dirLightsSize;
                this.program.setUniformf(idx + this.dirLightsColorOffset, this.directionalLights[i].color.r, this.directionalLights[i].color.g, this.directionalLights[i].color.b);
                this.program.setUniformf(idx + this.dirLightsDirectionOffset, this.directionalLights[i].direction.x, this.directionalLights[i].direction.y, this.directionalLights[i].direction.z);
                if (this.dirLightsSize <= 0) {
                    break;
                }
            }
        }
        if (this.pointLightsLoc >= 0) {
            for (int i = 0; i < this.pointLights.length; ++i) {
                if (points == null || i >= points.size) {
                    if (this.lightsSet && this.pointLights[i].intensity == 0.0f) {
                        continue;
                    }
                    this.pointLights[i].intensity = 0.0f;
                }
                else {
                    if (this.lightsSet && this.pointLights[i].equals(points.get(i))) {
                        continue;
                    }
                    this.pointLights[i].set(points.get(i));
                }
                final int idx = this.pointLightsLoc + i * this.pointLightsSize;
                this.program.setUniformf(idx + this.pointLightsColorOffset, this.pointLights[i].color.r * this.pointLights[i].intensity, this.pointLights[i].color.g * this.pointLights[i].intensity, this.pointLights[i].color.b * this.pointLights[i].intensity);
                this.program.setUniformf(idx + this.pointLightsPositionOffset, this.pointLights[i].position.x, this.pointLights[i].position.y, this.pointLights[i].position.z);
                if (this.pointLightsIntensityOffset >= 0) {
                    this.program.setUniformf(idx + this.pointLightsIntensityOffset, this.pointLights[i].intensity);
                }
                if (this.pointLightsSize <= 0) {
                    break;
                }
            }
        }
        if (this.spotLightsLoc >= 0) {
            for (int i = 0; i < this.spotLights.length; ++i) {
                if (spots == null || i >= spots.size) {
                    if (this.lightsSet && this.spotLights[i].intensity == 0.0f) {
                        continue;
                    }
                    this.spotLights[i].intensity = 0.0f;
                }
                else {
                    if (this.lightsSet && this.spotLights[i].equals(spots.get(i))) {
                        continue;
                    }
                    this.spotLights[i].set(spots.get(i));
                }
                final int idx = this.spotLightsLoc + i * this.spotLightsSize;
                this.program.setUniformf(idx + this.spotLightsColorOffset, this.spotLights[i].color.r * this.spotLights[i].intensity, this.spotLights[i].color.g * this.spotLights[i].intensity, this.spotLights[i].color.b * this.spotLights[i].intensity);
                this.program.setUniformf(idx + this.spotLightsPositionOffset, this.spotLights[i].position);
                this.program.setUniformf(idx + this.spotLightsDirectionOffset, this.spotLights[i].direction);
                this.program.setUniformf(idx + this.spotLightsCutoffAngleOffset, this.spotLights[i].cutoffAngle);
                this.program.setUniformf(idx + this.spotLightsExponentOffset, this.spotLights[i].exponent);
                if (this.spotLightsIntensityOffset >= 0) {
                    this.program.setUniformf(idx + this.spotLightsIntensityOffset, this.spotLights[i].intensity);
                }
                if (this.spotLightsSize <= 0) {
                    break;
                }
            }
        }
        if (attributes.has(ColorAttribute.Fog)) {
            this.set(this.u_fogColor, ((ColorAttribute)attributes.get(ColorAttribute.Fog)).color);
        }
        if (lights != null && lights.shadowMap != null) {
            this.set(this.u_shadowMapProjViewTrans, lights.shadowMap.getProjViewTrans());
            this.set(this.u_shadowTexture, lights.shadowMap.getDepthMap());
            this.set(this.u_shadowPCFOffset, 1.0f / (2.0f * lights.shadowMap.getDepthMap().texture.getWidth()));
        }
        this.lightsSet = true;
    }
    
    @Override
    public void dispose() {
        this.program.dispose();
        super.dispose();
    }
    
    public int getDefaultCullFace() {
        return (this.config.defaultCullFace == -1) ? DefaultShader.defaultCullFace : this.config.defaultCullFace;
    }
    
    public void setDefaultCullFace(final int cullFace) {
        this.config.defaultCullFace = cullFace;
    }
    
    public int getDefaultDepthFunc() {
        return (this.config.defaultDepthFunc == -1) ? DefaultShader.defaultDepthFunc : this.config.defaultDepthFunc;
    }
    
    public void setDefaultDepthFunc(final int depthFunc) {
        this.config.defaultDepthFunc = depthFunc;
    }
    
    public static class Config
    {
        public String vertexShader;
        public String fragmentShader;
        public int numDirectionalLights;
        public int numPointLights;
        public int numSpotLights;
        public int numBones;
        public boolean ignoreUnimplemented;
        public int defaultCullFace;
        public int defaultDepthFunc;
        
        public Config() {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.numDirectionalLights = 2;
            this.numPointLights = 5;
            this.numSpotLights = 0;
            this.numBones = 12;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
        }
        
        public Config(final String vertexShader, final String fragmentShader) {
            this.vertexShader = null;
            this.fragmentShader = null;
            this.numDirectionalLights = 2;
            this.numPointLights = 5;
            this.numSpotLights = 0;
            this.numBones = 12;
            this.ignoreUnimplemented = true;
            this.defaultCullFace = -1;
            this.defaultDepthFunc = -1;
            this.vertexShader = vertexShader;
            this.fragmentShader = fragmentShader;
        }
    }
    
    public static class Inputs
    {
        public static final Uniform projTrans;
        public static final Uniform viewTrans;
        public static final Uniform projViewTrans;
        public static final Uniform cameraPosition;
        public static final Uniform cameraDirection;
        public static final Uniform cameraUp;
        public static final Uniform cameraNearFar;
        public static final Uniform worldTrans;
        public static final Uniform viewWorldTrans;
        public static final Uniform projViewWorldTrans;
        public static final Uniform normalMatrix;
        public static final Uniform bones;
        public static final Uniform shininess;
        public static final Uniform opacity;
        public static final Uniform diffuseColor;
        public static final Uniform diffuseTexture;
        public static final Uniform diffuseUVTransform;
        public static final Uniform specularColor;
        public static final Uniform specularTexture;
        public static final Uniform specularUVTransform;
        public static final Uniform emissiveColor;
        public static final Uniform emissiveTexture;
        public static final Uniform emissiveUVTransform;
        public static final Uniform reflectionColor;
        public static final Uniform reflectionTexture;
        public static final Uniform reflectionUVTransform;
        public static final Uniform normalTexture;
        public static final Uniform normalUVTransform;
        public static final Uniform ambientTexture;
        public static final Uniform ambientUVTransform;
        public static final Uniform alphaTest;
        public static final Uniform ambientCube;
        public static final Uniform dirLights;
        public static final Uniform pointLights;
        public static final Uniform spotLights;
        public static final Uniform environmentCubemap;
        
        static {
            projTrans = new Uniform("u_projTrans");
            viewTrans = new Uniform("u_viewTrans");
            projViewTrans = new Uniform("u_projViewTrans");
            cameraPosition = new Uniform("u_cameraPosition");
            cameraDirection = new Uniform("u_cameraDirection");
            cameraUp = new Uniform("u_cameraUp");
            cameraNearFar = new Uniform("u_cameraNearFar");
            worldTrans = new Uniform("u_worldTrans");
            viewWorldTrans = new Uniform("u_viewWorldTrans");
            projViewWorldTrans = new Uniform("u_projViewWorldTrans");
            normalMatrix = new Uniform("u_normalMatrix");
            bones = new Uniform("u_bones");
            shininess = new Uniform("u_shininess", FloatAttribute.Shininess);
            opacity = new Uniform("u_opacity", BlendingAttribute.Type);
            diffuseColor = new Uniform("u_diffuseColor", ColorAttribute.Diffuse);
            diffuseTexture = new Uniform("u_diffuseTexture", TextureAttribute.Diffuse);
            diffuseUVTransform = new Uniform("u_diffuseUVTransform", TextureAttribute.Diffuse);
            specularColor = new Uniform("u_specularColor", ColorAttribute.Specular);
            specularTexture = new Uniform("u_specularTexture", TextureAttribute.Specular);
            specularUVTransform = new Uniform("u_specularUVTransform", TextureAttribute.Specular);
            emissiveColor = new Uniform("u_emissiveColor", ColorAttribute.Emissive);
            emissiveTexture = new Uniform("u_emissiveTexture", TextureAttribute.Emissive);
            emissiveUVTransform = new Uniform("u_emissiveUVTransform", TextureAttribute.Emissive);
            reflectionColor = new Uniform("u_reflectionColor", ColorAttribute.Reflection);
            reflectionTexture = new Uniform("u_reflectionTexture", TextureAttribute.Reflection);
            reflectionUVTransform = new Uniform("u_reflectionUVTransform", TextureAttribute.Reflection);
            normalTexture = new Uniform("u_normalTexture", TextureAttribute.Normal);
            normalUVTransform = new Uniform("u_normalUVTransform", TextureAttribute.Normal);
            ambientTexture = new Uniform("u_ambientTexture", TextureAttribute.Ambient);
            ambientUVTransform = new Uniform("u_ambientUVTransform", TextureAttribute.Ambient);
            alphaTest = new Uniform("u_alphaTest");
            ambientCube = new Uniform("u_ambientCubemap");
            dirLights = new Uniform("u_dirLights");
            pointLights = new Uniform("u_pointLights");
            spotLights = new Uniform("u_spotLights");
            environmentCubemap = new Uniform("u_environmentCubemap");
        }
    }
    
    public static class Setters
    {
        public static final Setter projTrans;
        public static final Setter viewTrans;
        public static final Setter projViewTrans;
        public static final Setter cameraPosition;
        public static final Setter cameraDirection;
        public static final Setter cameraUp;
        public static final Setter cameraNearFar;
        public static final Setter worldTrans;
        public static final Setter viewWorldTrans;
        public static final Setter projViewWorldTrans;
        public static final Setter normalMatrix;
        public static final Setter shininess;
        public static final Setter diffuseColor;
        public static final Setter diffuseTexture;
        public static final Setter diffuseUVTransform;
        public static final Setter specularColor;
        public static final Setter specularTexture;
        public static final Setter specularUVTransform;
        public static final Setter emissiveColor;
        public static final Setter emissiveTexture;
        public static final Setter emissiveUVTransform;
        public static final Setter reflectionColor;
        public static final Setter reflectionTexture;
        public static final Setter reflectionUVTransform;
        public static final Setter normalTexture;
        public static final Setter normalUVTransform;
        public static final Setter ambientTexture;
        public static final Setter ambientUVTransform;
        public static final Setter environmentCubemap;
        
        static {
            projTrans = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.projection);
                }
            };
            viewTrans = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.view);
                }
            };
            projViewTrans = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.combined);
                }
            };
            cameraPosition = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.position.x, shader.camera.position.y, shader.camera.position.z, 1.1881f / (shader.camera.far * shader.camera.far));
                }
            };
            cameraDirection = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.direction);
                }
            };
            cameraUp = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.up);
                }
            };
            cameraNearFar = new GlobalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, shader.camera.near, shader.camera.far);
                }
            };
            worldTrans = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, renderable.worldTransform);
                }
            };
            viewWorldTrans = new LocalSetter() {
                final Matrix4 temp = new Matrix4();
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, this.temp.set(shader.camera.view).mul(renderable.worldTransform));
                }
            };
            projViewWorldTrans = new LocalSetter() {
                final Matrix4 temp = new Matrix4();
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, this.temp.set(shader.camera.combined).mul(renderable.worldTransform));
                }
            };
            normalMatrix = new LocalSetter() {
                private final Matrix3 tmpM = new Matrix3();
                
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, this.tmpM.set(renderable.worldTransform).inv().transpose());
                }
            };
            shininess = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ((FloatAttribute)combinedAttributes.get(FloatAttribute.Shininess)).value);
                }
            };
            diffuseColor = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Diffuse)).color);
                }
            };
            diffuseTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Diffuse)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            diffuseUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Diffuse);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            specularColor = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Specular)).color);
                }
            };
            specularTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Specular)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            specularUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Specular);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            emissiveColor = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Emissive)).color);
                }
            };
            emissiveTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Emissive)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            emissiveUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Emissive);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            reflectionColor = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    shader.set(inputID, ((ColorAttribute)combinedAttributes.get(ColorAttribute.Reflection)).color);
                }
            };
            reflectionTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Reflection)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            reflectionUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Reflection);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            normalTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Normal)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            normalUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Normal);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            ambientTexture = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final int unit = shader.context.textureBinder.bind(((TextureAttribute)combinedAttributes.get(TextureAttribute.Ambient)).textureDescription);
                    shader.set(inputID, unit);
                }
            };
            ambientUVTransform = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    final TextureAttribute ta = (TextureAttribute)combinedAttributes.get(TextureAttribute.Ambient);
                    shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                }
            };
            environmentCubemap = new LocalSetter() {
                @Override
                public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                    if (combinedAttributes.has(CubemapAttribute.EnvironmentMap)) {
                        shader.set(inputID, shader.context.textureBinder.bind(((CubemapAttribute)combinedAttributes.get(CubemapAttribute.EnvironmentMap)).textureDescription));
                    }
                }
            };
        }
        
        public static class Bones extends LocalSetter
        {
            private static final Matrix4 idtMatrix;
            public final float[] bones;
            
            static {
                idtMatrix = new Matrix4();
            }
            
            public Bones(final int numBones) {
                this.bones = new float[numBones * 16];
            }
            
            @Override
            public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                for (int i = 0; i < this.bones.length; ++i) {
                    final int idx = i / 16;
                    this.bones[i] = ((renderable.bones == null || idx >= renderable.bones.length || renderable.bones[idx] == null) ? Bones.idtMatrix.val[i % 16] : renderable.bones[idx].val[i % 16]);
                }
                shader.program.setUniformMatrix4fv(shader.loc(inputID), this.bones, 0, this.bones.length);
            }
        }
        
        public static class ACubemap extends LocalSetter
        {
            private static final float[] ones;
            private final AmbientCubemap cacheAmbientCubemap;
            private static final Vector3 tmpV1;
            public final int dirLightsOffset;
            public final int pointLightsOffset;
            
            static {
                ones = new float[] { 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f };
                tmpV1 = new Vector3();
            }
            
            public ACubemap(final int dirLightsOffset, final int pointLightsOffset) {
                this.cacheAmbientCubemap = new AmbientCubemap();
                this.dirLightsOffset = dirLightsOffset;
                this.pointLightsOffset = pointLightsOffset;
            }
            
            @Override
            public void set(final BaseShader shader, final int inputID, final Renderable renderable, final Attributes combinedAttributes) {
                if (renderable.environment == null) {
                    shader.program.setUniform3fv(shader.loc(inputID), ACubemap.ones, 0, ACubemap.ones.length);
                }
                else {
                    renderable.worldTransform.getTranslation(ACubemap.tmpV1);
                    if (combinedAttributes.has(ColorAttribute.AmbientLight)) {
                        this.cacheAmbientCubemap.set(((ColorAttribute)combinedAttributes.get(ColorAttribute.AmbientLight)).color);
                    }
                    if (combinedAttributes.has(DirectionalLightsAttribute.Type)) {
                        final Array<DirectionalLight> lights = ((DirectionalLightsAttribute)combinedAttributes.get(DirectionalLightsAttribute.Type)).lights;
                        for (int i = this.dirLightsOffset; i < lights.size; ++i) {
                            this.cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).direction);
                        }
                    }
                    if (combinedAttributes.has(PointLightsAttribute.Type)) {
                        final Array<PointLight> lights2 = ((PointLightsAttribute)combinedAttributes.get(PointLightsAttribute.Type)).lights;
                        for (int i = this.pointLightsOffset; i < lights2.size; ++i) {
                            this.cacheAmbientCubemap.add(lights2.get(i).color, lights2.get(i).position, ACubemap.tmpV1, lights2.get(i).intensity);
                        }
                    }
                    this.cacheAmbientCubemap.clamp();
                    shader.program.setUniform3fv(shader.loc(inputID), this.cacheAmbientCubemap.data, 0, this.cacheAmbientCubemap.data.length);
                }
            }
        }
    }
}
