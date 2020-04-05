// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d.utils;

import java.util.Iterator;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.utils.Array;

public abstract class BaseShaderProvider implements ShaderProvider
{
    protected Array<Shader> shaders;
    
    public BaseShaderProvider() {
        this.shaders = new Array<Shader>();
    }
    
    @Override
    public Shader getShader(final Renderable renderable) {
        final Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) {
            return suggestedShader;
        }
        for (final Shader shader : this.shaders) {
            if (shader.canRender(renderable)) {
                return shader;
            }
        }
        Shader shader = this.createShader(renderable);
        shader.init();
        this.shaders.add(shader);
        return shader;
    }
    
    protected abstract Shader createShader(final Renderable p0);
    
    @Override
    public void dispose() {
        for (final Shader shader : this.shaders) {
            shader.dispose();
        }
        this.shaders.clear();
    }
}
