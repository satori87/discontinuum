// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.graphics.VertexAttribute;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.graphics.VertexAttributes;

public class VertexBufferObjectSubData implements VertexData
{
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isDirect;
    final boolean isStatic;
    final int usage;
    boolean isDirty;
    boolean isBound;
    
    public VertexBufferObjectSubData(final boolean isStatic, final int numVertices, final VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }
    
    public VertexBufferObjectSubData(final boolean isStatic, final int numVertices, final VertexAttributes attributes) {
        this.isDirty = false;
        this.isBound = false;
        this.isStatic = isStatic;
        this.attributes = attributes;
        this.byteBuffer = BufferUtils.newByteBuffer(this.attributes.vertexSize * numVertices);
        this.isDirect = true;
        this.usage = (isStatic ? 35044 : 35048);
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.bufferHandle = this.createBufferObject();
        this.buffer.flip();
        this.byteBuffer.flip();
    }
    
    private int createBufferObject() {
        final int result = Gdx.gl20.glGenBuffer();
        Gdx.gl20.glBindBuffer(34962, result);
        Gdx.gl20.glBufferData(34962, this.byteBuffer.capacity(), null, this.usage);
        Gdx.gl20.glBindBuffer(34962, 0);
        return result;
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
    
    private void bufferChanged() {
        if (this.isBound) {
            Gdx.gl20.glBufferSubData(34962, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
    }
    
    @Override
    public void setVertices(final float[] vertices, final int offset, final int count) {
        this.isDirty = true;
        if (this.isDirect) {
            BufferUtils.copy(vertices, this.byteBuffer, count, offset);
            this.buffer.position(0);
            this.buffer.limit(count);
        }
        else {
            this.buffer.clear();
            this.buffer.put(vertices, offset, count);
            this.buffer.flip();
            this.byteBuffer.position(0);
            this.byteBuffer.limit(this.buffer.limit() << 2);
        }
        this.bufferChanged();
    }
    
    @Override
    public void updateVertices(final int targetOffset, final float[] vertices, final int sourceOffset, final int count) {
        this.isDirty = true;
        if (this.isDirect) {
            final int pos = this.byteBuffer.position();
            this.byteBuffer.position(targetOffset * 4);
            BufferUtils.copy(vertices, sourceOffset, count, this.byteBuffer);
            this.byteBuffer.position(pos);
            this.bufferChanged();
            return;
        }
        throw new GdxRuntimeException("Buffer must be allocated direct.");
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
        this.bufferHandle = this.createBufferObject();
        this.isDirty = true;
    }
    
    @Override
    public void dispose() {
        final GL20 gl = Gdx.gl20;
        gl.glBindBuffer(34962, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
    }
    
    public int getBufferHandle() {
        return this.bufferHandle;
    }
}
