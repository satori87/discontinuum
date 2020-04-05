// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.utils.FlushablePool;
import java.util.Iterator;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.TextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;

public class ModelBatch implements Disposable
{
    protected Camera camera;
    protected final RenderablePool renderablesPool;
    protected final Array<Renderable> renderables;
    protected final RenderContext context;
    private final boolean ownContext;
    protected final ShaderProvider shaderProvider;
    protected final RenderableSorter sorter;
    
    public ModelBatch(final RenderContext context, final ShaderProvider shaderProvider, final RenderableSorter sorter) {
        this.renderablesPool = new RenderablePool();
        this.renderables = new Array<Renderable>();
        this.sorter = ((sorter == null) ? new DefaultRenderableSorter() : sorter);
        this.ownContext = (context == null);
        this.context = ((context == null) ? new RenderContext(new DefaultTextureBinder(1, 1)) : context);
        this.shaderProvider = ((shaderProvider == null) ? new DefaultShaderProvider() : shaderProvider);
    }
    
    public ModelBatch(final RenderContext context, final ShaderProvider shaderProvider) {
        this(context, shaderProvider, null);
    }
    
    public ModelBatch(final RenderContext context, final RenderableSorter sorter) {
        this(context, null, sorter);
    }
    
    public ModelBatch(final RenderContext context) {
        this(context, null, null);
    }
    
    public ModelBatch(final ShaderProvider shaderProvider, final RenderableSorter sorter) {
        this(null, shaderProvider, sorter);
    }
    
    public ModelBatch(final RenderableSorter sorter) {
        this(null, null, sorter);
    }
    
    public ModelBatch(final ShaderProvider shaderProvider) {
        this(null, shaderProvider, null);
    }
    
    public ModelBatch(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(null, new DefaultShaderProvider(vertexShader, fragmentShader), null);
    }
    
    public ModelBatch(final String vertexShader, final String fragmentShader) {
        this(null, new DefaultShaderProvider(vertexShader, fragmentShader), null);
    }
    
    public ModelBatch() {
        this(null, null, null);
    }
    
    public void begin(final Camera cam) {
        if (this.camera != null) {
            throw new GdxRuntimeException("Call end() first.");
        }
        this.camera = cam;
        if (this.ownContext) {
            this.context.begin();
        }
    }
    
    public void setCamera(final Camera cam) {
        if (this.camera == null) {
            throw new GdxRuntimeException("Call begin() first.");
        }
        if (this.renderables.size > 0) {
            this.flush();
        }
        this.camera = cam;
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    public boolean ownsRenderContext() {
        return this.ownContext;
    }
    
    public RenderContext getRenderContext() {
        return this.context;
    }
    
    public ShaderProvider getShaderProvider() {
        return this.shaderProvider;
    }
    
    public RenderableSorter getRenderableSorter() {
        return this.sorter;
    }
    
    public void flush() {
        this.sorter.sort(this.camera, this.renderables);
        Shader currentShader = null;
        for (int i = 0; i < this.renderables.size; ++i) {
            final Renderable renderable = this.renderables.get(i);
            if (currentShader != renderable.shader) {
                if (currentShader != null) {
                    currentShader.end();
                }
                currentShader = renderable.shader;
                currentShader.begin(this.camera, this.context);
            }
            currentShader.render(renderable);
        }
        if (currentShader != null) {
            currentShader.end();
        }
        this.renderablesPool.flush();
        this.renderables.clear();
    }
    
    public void end() {
        this.flush();
        if (this.ownContext) {
            this.context.end();
        }
        this.camera = null;
    }
    
    public void render(final Renderable renderable) {
        renderable.shader = this.shaderProvider.getShader(renderable);
        renderable.meshPart.mesh.setAutoBind(false);
        this.renderables.add(renderable);
    }
    
    public void render(final RenderableProvider renderableProvider) {
        final int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; ++i) {
            final Renderable renderable = this.renderables.get(i);
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }
    
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            this.render(renderableProvider);
        }
    }
    
    public void render(final RenderableProvider renderableProvider, final Environment environment) {
        final int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; ++i) {
            final Renderable renderable = this.renderables.get(i);
            renderable.environment = environment;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }
    
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Environment environment) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            this.render(renderableProvider, environment);
        }
    }
    
    public void render(final RenderableProvider renderableProvider, final Shader shader) {
        final int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; ++i) {
            final Renderable renderable = this.renderables.get(i);
            renderable.shader = shader;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }
    
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Shader shader) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            this.render(renderableProvider, shader);
        }
    }
    
    public void render(final RenderableProvider renderableProvider, final Environment environment, final Shader shader) {
        final int offset = this.renderables.size;
        renderableProvider.getRenderables(this.renderables, this.renderablesPool);
        for (int i = offset; i < this.renderables.size; ++i) {
            final Renderable renderable = this.renderables.get(i);
            renderable.environment = environment;
            renderable.shader = shader;
            renderable.shader = this.shaderProvider.getShader(renderable);
        }
    }
    
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, final Environment environment, final Shader shader) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            this.render(renderableProvider, environment, shader);
        }
    }
    
    @Override
    public void dispose() {
        this.shaderProvider.dispose();
    }
    
    protected static class RenderablePool extends FlushablePool<Renderable>
    {
        @Override
        protected Renderable newObject() {
            return new Renderable();
        }
        
        @Override
        public Renderable obtain() {
            final Renderable renderable = super.obtain();
            renderable.environment = null;
            renderable.material = null;
            renderable.meshPart.set("", null, 0, 0, 0);
            renderable.shader = null;
            renderable.userData = null;
            return renderable;
        }
    }
}
