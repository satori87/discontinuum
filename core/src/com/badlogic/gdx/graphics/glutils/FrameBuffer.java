// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class FrameBuffer extends GLFrameBuffer<Texture>
{
    FrameBuffer() {
    }
    
    protected FrameBuffer(final GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
    }
    
    public FrameBuffer(final Pixmap.Format format, final int width, final int height, final boolean hasDepth) {
        this(format, width, height, hasDepth, false);
    }
    
    public FrameBuffer(final Pixmap.Format format, final int width, final int height, final boolean hasDepth, final boolean hasStencil) {
        final FrameBufferBuilder frameBufferBuilder = new FrameBufferBuilder(width, height);
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
    protected Texture createTexture(final FrameBufferTextureAttachmentSpec attachmentSpec) {
        final GLOnlyTextureData data = new GLOnlyTextureData(this.bufferBuilder.width, this.bufferBuilder.height, 0, attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type);
        final Texture result = new Texture(data);
        result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }
    
    @Override
    protected void disposeColorTexture(final Texture colorTexture) {
        colorTexture.dispose();
    }
    
    @Override
    protected void attachFrameBufferColorTexture(final Texture texture) {
        Gdx.gl20.glFramebufferTexture2D(36160, 36064, 3553, texture.getTextureObjectHandle(), 0);
    }
    
    public static void unbind() {
        GLFrameBuffer.unbind();
    }
}
