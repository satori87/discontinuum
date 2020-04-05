// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.nio.Buffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.graphics.VertexAttribute;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.graphics.VertexAttributes;

public class VertexArray implements VertexData
{
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    boolean isBound;
    
    public VertexArray(final int numVertices, final VertexAttribute... attributes) {
        this(numVertices, new VertexAttributes(attributes));
    }
    
    public VertexArray(final int numVertices, final VertexAttributes attributes) {
        this.isBound = false;
        this.attributes = attributes;
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        (this.buffer = this.byteBuffer.asFloatBuffer()).flip();
        this.byteBuffer.flip();
    }
    
    @Override
    public void dispose() {
        BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
    }
    
    @Override
    public FloatBuffer getBuffer() {
        return this.buffer;
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
    public void setVertices(final float[] vertices, final int offset, final int count) {
        BufferUtils.copy(vertices, this.byteBuffer, count, offset);
        this.buffer.position(0);
        this.buffer.limit(count);
    }
    
    @Override
    public void updateVertices(final int targetOffset, final float[] vertices, final int sourceOffset, final int count) {
        final int pos = this.byteBuffer.position();
        this.byteBuffer.position(targetOffset * 4);
        BufferUtils.copy(vertices, sourceOffset, count, this.byteBuffer);
        this.byteBuffer.position(pos);
    }
    
    @Override
    public void bind(final ShaderProgram shader) {
        this.bind(shader, null);
    }
    
    @Override
    public void bind(final ShaderProgram shader, final int[] locations) {
        final int numAttributes = this.attributes.size();
        this.byteBuffer.limit(this.buffer.limit() * 4);
        if (locations == null) {
            for (int i = 0; i < numAttributes; ++i) {
                final VertexAttribute attribute = this.attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    if (attribute.type == 5126) {
                        this.buffer.position(attribute.offset / 4);
                        shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, this.buffer);
                    }
                    else {
                        this.byteBuffer.position(attribute.offset);
                        shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, this.byteBuffer);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < numAttributes; ++i) {
                final VertexAttribute attribute = this.attributes.get(i);
                final int location = locations[i];
                if (location >= 0) {
                    shader.enableVertexAttribute(location);
                    if (attribute.type == 5126) {
                        this.buffer.position(attribute.offset / 4);
                        shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, this.buffer);
                    }
                    else {
                        this.byteBuffer.position(attribute.offset);
                        shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, this.attributes.vertexSize, this.byteBuffer);
                    }
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
        this.isBound = false;
    }
    
    @Override
    public VertexAttributes getAttributes() {
        return this.attributes;
    }
    
    @Override
    public void invalidate() {
    }
}
