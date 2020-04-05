// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Cubemap;

public class FrameBufferCubemap extends GLFrameBuffer<Cubemap>
{
    private int currentSide;
    private static final Cubemap.CubemapSide[] cubemapSides;
    
    static {
        cubemapSides = Cubemap.CubemapSide.values();
    }
    
    FrameBufferCubemap() {
    }
    
    protected FrameBufferCubemap(final GLFrameBufferBuilder<? extends GLFrameBuffer<Cubemap>> bufferBuilder) {
        super(bufferBuilder);
    }
    
    public FrameBufferCubemap(final Pixmap.Format format, final int width, final int height, final boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }
    
    public FrameBufferCubemap(final Pixmap.Format format, final int width, final int height, final boolean hasDepth, final boolean hasStencil) {
        final FrameBufferCubemapBuilder frameBufferBuilder = new FrameBufferCubemapBuilder(width, height);
        frameBufferBuilder.addBasicColorTextureAttachment(format);
        if (hasDepth) {
            frameBufferBuilder.addBasicDepthRenderBuffer();
        }
        if (hasStencil) {
            frameBufferBuilder.addBasicStencilRenderBuffer();
        }
        this.bufferBuilder = (GLFrameBufferBuilder<? extends GLFrameBuffer<T>>)frameBufferBuilder;
        this.build();
    }
    
    @Override
    protected Cubemap createTexture(final FrameBufferTextureAttachmentSpec attachmentSpec) {
        final GLOnlyTextureData data = new GLOnlyTextureData(this.bufferBuilder.width, this.bufferBuilder.height, 0, attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type);
        final Cubemap result = new Cubemap(data, data, data, data, data, data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }
    
    @Override
    protected void disposeColorTexture(final Cubemap colorTexture) {
        colorTexture.dispose();
    }
    
    @Override
    protected void attachFrameBufferColorTexture(final Cubemap texture) {
        final GL20 gl = Gdx.gl20;
        final int glHandle = texture.getTextureObjectHandle();
        final Cubemap.CubemapSide[] sides = Cubemap.CubemapSide.values();
        Cubemap.CubemapSide[] array;
        for (int length = (array = sides).length, i = 0; i < length; ++i) {
            final Cubemap.CubemapSide side = array[i];
            gl.glFramebufferTexture2D(36160, 36064, side.glEnum, glHandle, 0);
        }
    }
    
    @Override
    public void bind() {
        this.currentSide = -1;
        super.bind();
    }
    
    public boolean nextSide() {
        if (this.currentSide > 5) {
            throw new GdxRuntimeException("No remaining sides.");
        }
        if (this.currentSide == 5) {
            return false;
        }
        ++this.currentSide;
        this.bindSide(this.getSide());
        return true;
    }
    
    protected void bindSide(final Cubemap.CubemapSide side) {
        Gdx.gl20.glFramebufferTexture2D(36160, 36064, side.glEnum, this.getColorBufferTexture().getTextureObjectHandle(), 0);
    }
    
    public Cubemap.CubemapSide getSide() {
        return (this.currentSide < 0) ? null : FrameBufferCubemap.cubemapSides[this.currentSide];
    }
}
