// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.graphics.VertexAttributes;

public class VertexBufferObject implements VertexData
{
    private VertexAttributes attributes;
    private FloatBuffer buffer;
    private ByteBuffer byteBuffer;
    private boolean ownsBuffer;
    private int bufferHandle;
    private int usage;
    boolean isDirty;
    boolean isBound;
    
    public VertexBufferObject(final boolean isStatic, final int numVertices, final VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }
    
    public VertexBufferObject(final boolean isStatic, final int numVertices, final VertexAttributes attributes) {
        this.isDirty = false;
        this.isBound = false;
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        final ByteBuffer data = BufferUtils.newUnsafeByteBuffer(attributes.vertexSize * numVertices);
        data.limit(0);
        this.setBuffer(data, true, attributes);
        this.setUsage(isStatic ? 35044 : 35048);
    }
    
    protected VertexBufferObject(final int usage, final ByteBuffer data, final boolean ownsBuffer, final VertexAttributes attributes) {
        this.isDirty = false;
        this.isBound = false;
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.setBuffer(data, ownsBuffer, attributes);
        this.setUsage(usage);
    }
    
    @Override
    public VertexAttributes getAttributes() {
        return this.attributes;
    }
    
    @Override
    public int getNumVertices() {
        return this.buffer.limit() * 4 / this.attributes.vertexSize;
    }
    
    @Override
    public int getNumMaxVertices() {
        return this.byteBuffer.capacity() / this.attributes.vertexSize;
    }
    
    @Override
    public FloatBuffer getBuffer() {
        this.isDirty = true;
        return this.buffer;
    }
    
    protected void setBuffer(final Buffer data, final boolean ownsBuffer, final VertexAttributes value) {
        if (this.isBound) {
            throw new GdxRuntimeException("Cannot change attributes while VBO is bound");
        }
        if (this.ownsBuffer && this.byteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
        }
        this.attributes = value;
        if (data instanceof ByteBuffer) {
            this.byteBuffer = (ByteBuffer)data;
            this.ownsBuffer = ownsBuffer;
            final int l = this.byteBuffer.limit();
            this.byteBuffer.limit(this.byteBuffer.capacity());
            this.buffer = this.byteBuffer.asFloatBuffer();
            this.byteBuffer.limit(l);
            this.buffer.limit(l / 4);
            return;
        }
        throw new GdxRuntimeException("Only ByteBuffer is currently supported");
    }
    
    private void bufferChanged() {
        if (this.isBound) {
            Gdx.gl20.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
    }
    
    @Override
    public void setVertices(final float[] vertices, final int offset, final int count) {
        this.isDirty = true;
        BufferUtils.copy(vertices, this.byteBuffer, count, offset);
        this.buffer.position(0);
        this.buffer.limit(count);
        this.bufferChanged();
    }
    
    @Override
    public void updateVertices(final int targetOffset, final float[] vertices, final int sourceOffset, final int count) {
        this.isDirty = true;
        final int pos = this.byteBuffer.position();
        this.byteBuffer.position(targetOffset * 4);
        BufferUtils.copy(vertices, sourceOffset, count, this.byteBuffer);
        this.byteBuffer.position(pos);
        this.buffer.position(0);
        this.bufferChanged();
    }
    
    protected int getUsage() {
        return this.usage;
    }
    
    protected void setUsage(final int value) {
        if (this.isBound) {
            throw new GdxRuntimeException("Cannot change usage while VBO is bound");
        }
        this.usage = value;
    }
    
    @Override
    public void bind(final ShaderProgram shader) {
        this.bind(shader, null);
    }
    
    @Override
    public void bind(final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;
        gl.glBindBuffer(34962, this.bufferHandle);
        if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
        final int numAttributes = this.attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; ++i) {
                final VertexAttribute attribute = this.attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                }
            }
        }
        else {
            for (int i = 0; i < numAttributes; ++i) {
                final VertexAttribute attribute = this.attributes.get(i);
                final int location = locations[i];
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                }
            }
        }
        this.isBound = true;
    }
    
    @Override
    public void unbind(final ShaderProgram shader) {
        this.unbind(shader, null);
    }
    
    @Override
    public void unbind(final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;
        final int numAttributes = this.attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; ++i) {
                shader.disableVertexAttribute(this.attributes.get(i).alias);
            }
        }
        else {
            for (final int location : locations) {
                if (location >= 0) {
                    shader.disableVertexAttribute(location);
                }
            }
        }
        gl.glBindBuffer(34962, 0);
        this.isBound = false;
    }
    
    @Override
    public void invalidate() {
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.isDirty = true;
    }
    
    @Override
    public void dispose() {
        final GL20 gl = Gdx.gl20;
        gl.glBindBuffer(34962, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
        if (this.ownsBuffer) {
            BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
        }
    }
}
