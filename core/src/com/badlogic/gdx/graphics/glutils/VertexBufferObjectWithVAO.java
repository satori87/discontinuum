// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL20;
import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.graphics.VertexAttributes;
import java.nio.IntBuffer;

public class VertexBufferObjectWithVAO implements VertexData
{
    static final IntBuffer tmpHandle;
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty;
    boolean isBound;
    int vaoHandle;
    IntArray cachedLocations;
    
    static {
        tmpHandle = BufferUtils.newIntBuffer(1);
    }
    
    public VertexBufferObjectWithVAO(final boolean isStatic, final int numVertices, final VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }
    
    public VertexBufferObjectWithVAO(final boolean isStatic, final int numVertices, final VertexAttributes attributes) {
        this.isDirty = false;
        this.isBound = false;
        this.vaoHandle = -1;
        this.cachedLocations = new IntArray();
        this.isStatic = isStatic;
        this.attributes = attributes;
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        (this.buffer = this.byteBuffer.asFloatBuffer()).flip();
        this.byteBuffer.flip();
        this.bufferHandle = Gdx.gl20.glGenBuffer();
        this.usage = (isStatic ? 35044 : 35048);
        this.createVAO();
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
    
    @Override
    public void bind(final ShaderProgram shader) {
        this.bind(shader, null);
    }
    
    @Override
    public void bind(final ShaderProgram shader, final int[] locations) {
        final GL30 gl = Gdx.gl30;
        gl.glBindVertexArray(this.vaoHandle);
        this.bindAttributes(shader, locations);
        this.bindData(gl);
        this.isBound = true;
    }
    
    private void bindAttributes(final ShaderProgram shader, final int[] locations) {
        boolean stillValid = this.cachedLocations.size != 0;
        final int numAttributes = this.attributes.size();
        if (stillValid) {
            if (locations == null) {
                int location;
                for (int i = 0; stillValid; stillValid = (location == this.cachedLocations.get(i)), ++i) {
                    if (i >= numAttributes) {
                        break;
                    }
                    final VertexAttribute attribute = this.attributes.get(i);
                    location = shader.getAttributeLocation(attribute.alias);
                }
            }
            else {
                stillValid = (locations.length == this.cachedLocations.size);
                for (int i = 0; stillValid && i < numAttributes; stillValid = (locations[i] == this.cachedLocations.get(i)), ++i) {}
            }
        }
        if (!stillValid) {
            Gdx.gl.glBindBuffer(34962, this.bufferHandle);
            this.unbindAttributes(shader);
            this.cachedLocations.clear();
            for (int i = 0; i < numAttributes; ++i) {
                final VertexAttribute attribute = this.attributes.get(i);
                if (locations == null) {
                    this.cachedLocations.add(shader.getAttributeLocation(attribute.alias));
                }
                else {
                    this.cachedLocations.add(locations[i]);
                }
                final int location = this.cachedLocations.get(i);
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, attribute.offset);
                }
            }
        }
    }
    
    private void unbindAttributes(final ShaderProgram shaderProgram) {
        if (this.cachedLocations.size == 0) {
            return;
        }
        for (int numAttributes = this.attributes.size(), i = 0; i < numAttributes; ++i) {
            final int location = this.cachedLocations.get(i);
            if (location >= 0) {
                shaderProgram.disableVertexAttribute(location);
            }
        }
    }
    
    private void bindData(final GL20 gl) {
        if (this.isDirty) {
            gl.glBindBuffer(34962, this.bufferHandle);
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
    }
    
    @Override
    public void unbind(final ShaderProgram shader) {
        this.unbind(shader, null);
    }
    
    @Override
    public void unbind(final ShaderProgram shader, final int[] locations) {
        final GL30 gl = Gdx.gl30;
        gl.glBindVertexArray(0);
        this.isBound = false;
    }
    
    @Override
    public void invalidate() {
        this.bufferHandle = Gdx.gl30.glGenBuffer();
        this.createVAO();
        this.isDirty = true;
    }
    
    @Override
    public void dispose() {
        final GL30 gl = Gdx.gl30;
        gl.glBindBuffer(34962, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
        BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
        this.deleteVAO();
    }
    
    private void createVAO() {
        VertexBufferObjectWithVAO.tmpHandle.clear();
        Gdx.gl30.glGenVertexArrays(1, VertexBufferObjectWithVAO.tmpHandle);
        this.vaoHandle = VertexBufferObjectWithVAO.tmpHandle.get();
    }
    
    private void deleteVAO() {
        if (this.vaoHandle != -1) {
            VertexBufferObjectWithVAO.tmpHandle.clear();
            VertexBufferObjectWithVAO.tmpHandle.put(this.vaoHandle);
            VertexBufferObjectWithVAO.tmpHandle.flip();
            Gdx.gl30.glDeleteVertexArrays(1, VertexBufferObjectWithVAO.tmpHandle);
            this.vaoHandle = -1;
        }
    }
}
