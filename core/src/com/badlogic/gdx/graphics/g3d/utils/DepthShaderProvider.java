// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;

public class DepthShaderProvider extends BaseShaderProvider
{
    public final DepthShader.Config config;
    
    public DepthShaderProvider(final DepthShader.Config config) {
        this.config = ((config == null) ? new DepthShader.Config() : config);
    }
    
    public DepthShaderProvider(final String vertexShader, final String fragmentShader) {
        this(new DepthShader.Config(vertexShader, fragmentShader));
    }
    
    public DepthShaderProvider(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }
    
    public DepthShaderProvider() {
        this(null);
    }
    
    @Override
    protected Shader createShader(final Renderable renderable) {
        return new DepthShader(renderable, this.config);
    }
}
