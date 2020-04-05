// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Texture;

public class FloatFrameBuffer extends FrameBuffer
{
    FloatFrameBuffer() {
    }
    
    protected FloatFrameBuffer(final GLFrameBufferBuilder<? extends GLFrameBuffer<Texture>> bufferBuilder) {
        super(bufferBuilder);
    }
    
    public FloatFrameBuffer(final int width, final int height, final boolean hasDepth) {
        final FloatFrameBufferBuilder bufferBuilder = new FloatFrameBufferBuilder(width, height);
        bufferBuilder.addFloatAttachment(34836, 6408, 5126, false);
        if (hasDepth) {
            bufferBuilder.addBasicDepthRenderBuffer();
        }
        this.bufferBuilder = (GLFrameBufferBuilder<? extends GLFrameBuffer<T>>)bufferBuilder;
        this.build();
    }
    
    @Override
    protected Texture createTexture(final FrameBufferTextureAttachmentSpec attachmentSpec) {
        final FloatTextureData data = new FloatTextureData(this.bufferBuilder.width, this.bufferBuilder.height, attachmentSpec.internalFormat, attachmentSpec.format, attachmentSpec.type, attachmentSpec.isGpuOnly);
        final Texture result = new Texture(data);
        if (Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.Applet) {
            result.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        else {
            result.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        result.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return result;
    }
}
