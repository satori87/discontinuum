// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Iterator;
import java.nio.IntBuffer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import java.util.Map;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.GLTexture;

public abstract class GLFrameBuffer<T extends GLTexture> implements Disposable
{
    protected static final Map<Application, Array<GLFrameBuffer>> buffers;
    protected static final int GL_DEPTH24_STENCIL8_OES = 35056;
    protected Array<T> textureAttachments;
    protected static int defaultFramebufferHandle;
    protected static boolean defaultFramebufferHandleInitialized;
    protected int framebufferHandle;
    protected int depthbufferHandle;
    protected int stencilbufferHandle;
    protected int depthStencilPackedBufferHandle;
    protected boolean hasDepthStencilPackedBuffer;
    protected boolean isMRT;
    protected GLFrameBufferBuilder<? extends GLFrameBuffer<T>> bufferBuilder;
    
    static {
        buffers = new HashMap<Application, Array<GLFrameBuffer>>();
        GLFrameBuffer.defaultFramebufferHandleInitialized = false;
    }
    
    GLFrameBuffer() {
        this.textureAttachments = new Array<T>();
    }
    
    protected GLFrameBuffer(final GLFrameBufferBuilder<? extends GLFrameBuffer<T>> bufferBuilder) {
        this.textureAttachments = new Array<T>();
        this.bufferBuilder = bufferBuilder;
        this.build();
    }
    
    public T getColorBufferTexture() {
        return this.textureAttachments.first();
    }
    
    public Array<T> getTextureAttachments() {
        return this.textureAttachments;
    }
    
    protected abstract T createTexture(final FrameBufferTextureAttachmentSpec p0);
    
    protected abstract void disposeColorTexture(final T p0);
    
    protected abstract void attachFrameBufferColorTexture(final T p0);
    
    protected void build() {
        final GL20 gl = Gdx.gl20;
        this.checkValidBuilder();
        if (!GLFrameBuffer.defaultFramebufferHandleInitialized) {
            GLFrameBuffer.defaultFramebufferHandleInitialized = true;
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                final IntBuffer intbuf = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
                gl.glGetIntegerv(36006, intbuf);
                GLFrameBuffer.defaultFramebufferHandle = intbuf.get(0);
            }
            else {
                GLFrameBuffer.defaultFramebufferHandle = 0;
            }
        }
        gl.glBindFramebuffer(36160, this.framebufferHandle = gl.glGenFramebuffer());
        final int width = this.bufferBuilder.width;
        final int height = this.bufferBuilder.height;
        if (this.bufferBuilder.hasDepthRenderBuffer) {
            gl.glBindRenderbuffer(36161, this.depthbufferHandle = gl.glGenRenderbuffer());
            gl.glRenderbufferStorage(36161, this.bufferBuilder.depthRenderBufferSpec.internalFormat, width, height);
        }
        if (this.bufferBuilder.hasStencilRenderBuffer) {
            gl.glBindRenderbuffer(36161, this.stencilbufferHandle = gl.glGenRenderbuffer());
            gl.glRenderbufferStorage(36161, this.bufferBuilder.stencilRenderBufferSpec.internalFormat, width, height);
        }
        if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
            gl.glBindRenderbuffer(36161, this.depthStencilPackedBufferHandle = gl.glGenRenderbuffer());
            gl.glRenderbufferStorage(36161, this.bufferBuilder.packedStencilDepthRenderBufferSpec.internalFormat, width, height);
        }
        this.isMRT = (this.bufferBuilder.textureAttachmentSpecs.size > 1);
        int colorTextureCounter = 0;
        if (this.isMRT) {
            for (final FrameBufferTextureAttachmentSpec attachmentSpec : this.bufferBuilder.textureAttachmentSpecs) {
                final T texture = this.createTexture(attachmentSpec);
                this.textureAttachments.add(texture);
                if (attachmentSpec.isColorTexture()) {
                    gl.glFramebufferTexture2D(36160, 36064 + colorTextureCounter, 3553, texture.getTextureObjectHandle(), 0);
                    ++colorTextureCounter;
                }
                else if (attachmentSpec.isDepth) {
                    gl.glFramebufferTexture2D(36160, 36096, 3553, texture.getTextureObjectHandle(), 0);
                }
                else {
                    if (!attachmentSpec.isStencil) {
                        continue;
                    }
                    gl.glFramebufferTexture2D(36160, 36128, 3553, texture.getTextureObjectHandle(), 0);
                }
            }
        }
        else {
            final T texture2 = this.createTexture(this.bufferBuilder.textureAttachmentSpecs.first());
            this.textureAttachments.add(texture2);
            gl.glBindTexture(texture2.glTarget, texture2.getTextureObjectHandle());
        }
        if (this.isMRT) {
            final IntBuffer buffer = BufferUtils.newIntBuffer(colorTextureCounter);
            for (int i = 0; i < colorTextureCounter; ++i) {
                buffer.put(36064 + i);
            }
            buffer.position(0);
            Gdx.gl30.glDrawBuffers(colorTextureCounter, buffer);
        }
        else {
            this.attachFrameBufferColorTexture(this.textureAttachments.first());
        }
        if (this.bufferBuilder.hasDepthRenderBuffer) {
            gl.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthbufferHandle);
        }
        if (this.bufferBuilder.hasStencilRenderBuffer) {
            gl.glFramebufferRenderbuffer(36160, 36128, 36161, this.stencilbufferHandle);
        }
        if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
            gl.glFramebufferRenderbuffer(36160, 33306, 36161, this.depthStencilPackedBufferHandle);
        }
        gl.glBindRenderbuffer(36161, 0);
        final Iterator<T> iterator2 = this.textureAttachments.iterator();
        while (iterator2.hasNext()) {
            final T texture2 = iterator2.next();
            gl.glBindTexture(texture2.glTarget, 0);
        }
        int result = gl.glCheckFramebufferStatus(36160);
        if (result == 36061 && this.bufferBuilder.hasDepthRenderBuffer && this.bufferBuilder.hasStencilRenderBuffer && (Gdx.graphics.supportsExtension("GL_OES_packed_depth_stencil") || Gdx.graphics.supportsExtension("GL_EXT_packed_depth_stencil"))) {
            if (this.bufferBuilder.hasDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
                this.depthbufferHandle = 0;
            }
            if (this.bufferBuilder.hasStencilRenderBuffer) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
                this.stencilbufferHandle = 0;
            }
            if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthStencilPackedBufferHandle);
                this.depthStencilPackedBufferHandle = 0;
            }
            this.depthStencilPackedBufferHandle = gl.glGenRenderbuffer();
            this.hasDepthStencilPackedBuffer = true;
            gl.glBindRenderbuffer(36161, this.depthStencilPackedBufferHandle);
            gl.glRenderbufferStorage(36161, 35056, width, height);
            gl.glBindRenderbuffer(36161, 0);
            gl.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthStencilPackedBufferHandle);
            gl.glFramebufferRenderbuffer(36160, 36128, 36161, this.depthStencilPackedBufferHandle);
            result = gl.glCheckFramebufferStatus(36160);
        }
        gl.glBindFramebuffer(36160, GLFrameBuffer.defaultFramebufferHandle);
        if (result == 36053) {
            addManagedFrameBuffer(Gdx.app, this);
            return;
        }
        for (final T texture3 : this.textureAttachments) {
            this.disposeColorTexture(texture3);
        }
        if (this.hasDepthStencilPackedBuffer) {
            gl.glDeleteBuffer(this.depthStencilPackedBufferHandle);
        }
        else {
            if (this.bufferBuilder.hasDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
            }
            if (this.bufferBuilder.hasStencilRenderBuffer) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
            }
        }
        gl.glDeleteFramebuffer(this.framebufferHandle);
        if (result == 36054) {
            throw new IllegalStateException("Frame buffer couldn't be constructed: incomplete attachment");
        }
        if (result == 36057) {
            throw new IllegalStateException("Frame buffer couldn't be constructed: incomplete dimensions");
        }
        if (result == 36055) {
            throw new IllegalStateException("Frame buffer couldn't be constructed: missing attachment");
        }
        if (result == 36061) {
            throw new IllegalStateException("Frame buffer couldn't be constructed: unsupported combination of formats");
        }
        throw new IllegalStateException("Frame buffer couldn't be constructed: unknown error " + result);
    }
    
    private void checkValidBuilder() {
        final boolean runningGL30 = Gdx.graphics.isGL30Available();
        if (!runningGL30) {
            if (this.bufferBuilder.hasPackedStencilDepthRenderBuffer) {
                throw new GdxRuntimeException("Packed Stencil/Render render buffers are not available on GLES 2.0");
            }
            if (this.bufferBuilder.textureAttachmentSpecs.size > 1) {
                throw new GdxRuntimeException("Multiple render targets not available on GLES 2.0");
            }
            for (final FrameBufferTextureAttachmentSpec spec : this.bufferBuilder.textureAttachmentSpecs) {
                if (spec.isDepth) {
                    throw new GdxRuntimeException("Depth texture FrameBuffer Attachment not available on GLES 2.0");
                }
                if (spec.isStencil) {
                    throw new GdxRuntimeException("Stencil texture FrameBuffer Attachment not available on GLES 2.0");
                }
                if (spec.isFloat && !Gdx.graphics.supportsExtension("OES_texture_float")) {
                    throw new GdxRuntimeException("Float texture FrameBuffer Attachment not available on GLES 2.0");
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        final GL20 gl = Gdx.gl20;
        for (final T texture : this.textureAttachments) {
            this.disposeColorTexture(texture);
        }
        if (this.hasDepthStencilPackedBuffer) {
            gl.glDeleteRenderbuffer(this.depthStencilPackedBufferHandle);
        }
        else {
            if (this.bufferBuilder.hasDepthRenderBuffer) {
                gl.glDeleteRenderbuffer(this.depthbufferHandle);
            }
            if (this.bufferBuilder.hasStencilRenderBuffer) {
                gl.glDeleteRenderbuffer(this.stencilbufferHandle);
            }
        }
        gl.glDeleteFramebuffer(this.framebufferHandle);
        if (GLFrameBuffer.buffers.get(Gdx.app) != null) {
            GLFrameBuffer.buffers.get(Gdx.app).removeValue(this, true);
        }
    }
    
    public void bind() {
        Gdx.gl20.glBindFramebuffer(36160, this.framebufferHandle);
    }
    
    public static void unbind() {
        Gdx.gl20.glBindFramebuffer(36160, GLFrameBuffer.defaultFramebufferHandle);
    }
    
    public void begin() {
        this.bind();
        this.setFrameBufferViewport();
    }
    
    protected void setFrameBufferViewport() {
        Gdx.gl20.glViewport(0, 0, this.bufferBuilder.width, this.bufferBuilder.height);
    }
    
    public void end() {
        this.end(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }
    
    public void end(final int x, final int y, final int width, final int height) {
        unbind();
        Gdx.gl20.glViewport(x, y, width, height);
    }
    
    public int getFramebufferHandle() {
        return this.framebufferHandle;
    }
    
    public int getDepthBufferHandle() {
        return this.depthbufferHandle;
    }
    
    public int getStencilBufferHandle() {
        return this.stencilbufferHandle;
    }
    
    protected int getDepthStencilPackedBuffer() {
        return this.depthStencilPackedBufferHandle;
    }
    
    public int getHeight() {
        return this.bufferBuilder.height;
    }
    
    public int getWidth() {
        return this.bufferBuilder.width;
    }
    
    private static void addManagedFrameBuffer(final Application app, final GLFrameBuffer frameBuffer) {
        Array<GLFrameBuffer> managedResources = GLFrameBuffer.buffers.get(app);
        if (managedResources == null) {
            managedResources = new Array<GLFrameBuffer>();
        }
        managedResources.add(frameBuffer);
        GLFrameBuffer.buffers.put(app, managedResources);
    }
    
    public static void invalidateAllFrameBuffers(final Application app) {
        if (Gdx.gl20 == null) {
            return;
        }
        final Array<GLFrameBuffer> bufferArray = GLFrameBuffer.buffers.get(app);
        if (bufferArray == null) {
            return;
        }
        for (int i = 0; i < bufferArray.size; ++i) {
            bufferArray.get(i).build();
        }
    }
    
    public static void clearAllFrameBuffers(final Application app) {
        GLFrameBuffer.buffers.remove(app);
    }
    
    public static StringBuilder getManagedStatus(final StringBuilder builder) {
        builder.append("Managed buffers/app: { ");
        for (final Application app : GLFrameBuffer.buffers.keySet()) {
            builder.append(GLFrameBuffer.buffers.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder;
    }
    
    public static String getManagedStatus() {
        return getManagedStatus(new StringBuilder()).toString();
    }
    
    public static class FloatFrameBufferBuilder extends GLFrameBufferBuilder<FloatFrameBuffer>
    {
        public FloatFrameBufferBuilder(final int width, final int height) {
            super(width, height);
        }
        
        @Override
        public FloatFrameBuffer build() {
            return new FloatFrameBuffer(this);
        }
    }
    
    public static class FrameBufferBuilder extends GLFrameBufferBuilder<FrameBuffer>
    {
        public FrameBufferBuilder(final int width, final int height) {
            super(width, height);
        }
        
        @Override
        public FrameBuffer build() {
            return new FrameBuffer(this);
        }
    }
    
    public static class FrameBufferCubemapBuilder extends GLFrameBufferBuilder<FrameBufferCubemap>
    {
        public FrameBufferCubemapBuilder(final int width, final int height) {
            super(width, height);
        }
        
        @Override
        public FrameBufferCubemap build() {
            return new FrameBufferCubemap(this);
        }
    }
    
    protected static class FrameBufferTextureAttachmentSpec
    {
        int internalFormat;
        int format;
        int type;
        boolean isFloat;
        boolean isGpuOnly;
        boolean isDepth;
        boolean isStencil;
        
        public FrameBufferTextureAttachmentSpec(final int internalformat, final int format, final int type) {
            this.internalFormat = internalformat;
            this.format = format;
            this.type = type;
        }
        
        public boolean isColorTexture() {
            return !this.isDepth && !this.isStencil;
        }
    }
    
    protected static class FrameBufferRenderBufferAttachmentSpec
    {
        int internalFormat;
        
        public FrameBufferRenderBufferAttachmentSpec(final int internalFormat) {
            this.internalFormat = internalFormat;
        }
    }
    
    protected abstract static class GLFrameBufferBuilder<U extends GLFrameBuffer<? extends GLTexture>>
    {
        protected int width;
        protected int height;
        protected Array<FrameBufferTextureAttachmentSpec> textureAttachmentSpecs;
        protected FrameBufferRenderBufferAttachmentSpec stencilRenderBufferSpec;
        protected FrameBufferRenderBufferAttachmentSpec depthRenderBufferSpec;
        protected FrameBufferRenderBufferAttachmentSpec packedStencilDepthRenderBufferSpec;
        protected boolean hasStencilRenderBuffer;
        protected boolean hasDepthRenderBuffer;
        protected boolean hasPackedStencilDepthRenderBuffer;
        
        public GLFrameBufferBuilder(final int width, final int height) {
            this.textureAttachmentSpecs = new Array<FrameBufferTextureAttachmentSpec>();
            this.width = width;
            this.height = height;
        }
        
        public GLFrameBufferBuilder<U> addColorTextureAttachment(final int internalFormat, final int format, final int type) {
            this.textureAttachmentSpecs.add(new FrameBufferTextureAttachmentSpec(internalFormat, format, type));
            return this;
        }
        
        public GLFrameBufferBuilder<U> addBasicColorTextureAttachment(final Pixmap.Format format) {
            final int glFormat = Pixmap.Format.toGlFormat(format);
            final int glType = Pixmap.Format.toGlType(format);
            return this.addColorTextureAttachment(glFormat, glFormat, glType);
        }
        
        public GLFrameBufferBuilder<U> addFloatAttachment(final int internalFormat, final int format, final int type, final boolean gpuOnly) {
            final FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, format, type);
            spec.isFloat = true;
            spec.isGpuOnly = gpuOnly;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }
        
        public GLFrameBufferBuilder<U> addDepthTextureAttachment(final int internalFormat, final int type) {
            final FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, 6402, type);
            spec.isDepth = true;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }
        
        public GLFrameBufferBuilder<U> addStencilTextureAttachment(final int internalFormat, final int type) {
            final FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, 36128, type);
            spec.isStencil = true;
            this.textureAttachmentSpecs.add(spec);
            return this;
        }
        
        public GLFrameBufferBuilder<U> addDepthRenderBuffer(final int internalFormat) {
            this.depthRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasDepthRenderBuffer = true;
            return this;
        }
        
        public GLFrameBufferBuilder<U> addStencilRenderBuffer(final int internalFormat) {
            this.stencilRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasStencilRenderBuffer = true;
            return this;
        }
        
        public GLFrameBufferBuilder<U> addStencilDepthPackedRenderBuffer(final int internalFormat) {
            this.packedStencilDepthRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
            this.hasPackedStencilDepthRenderBuffer = true;
            return this;
        }
        
        public GLFrameBufferBuilder<U> addBasicDepthRenderBuffer() {
            return this.addDepthRenderBuffer(33189);
        }
        
        public GLFrameBufferBuilder<U> addBasicStencilRenderBuffer() {
            return this.addStencilRenderBuffer(36168);
        }
        
        public GLFrameBufferBuilder<U> addBasicStencilDepthPackedRenderBuffer() {
            return this.addStencilDepthPackedRenderBuffer(35056);
        }
        
        public abstract U build();
    }
}
