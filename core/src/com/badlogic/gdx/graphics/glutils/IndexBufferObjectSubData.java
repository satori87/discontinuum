// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class IndexBufferObjectSubData implements IndexData
{
    final ShortBuffer buffer;
    final ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isDirect;
    boolean isDirty;
    boolean isBound;
    final int usage;
    
    public IndexBufferObjectSubData(final boolean isStatic, final int maxIndices) {
        this.isDirty = true;
        this.isBound = false;
        this.byteBuffer = BufferUtils.newByteBuffer(maxIndices * 2);
        this.isDirect = true;
        this.usage = (isStatic ? 35044 : 35048);
        (this.buffer = this.byteBuffer.asShortBuffer()).flip();
        this.byteBuffer.flip();
        this.bufferHandle = this.createBufferObject();
    }
    
    public IndexBufferObjectSubData(final int maxIndices) {
        this.isDirty = true;
        this.isBound = false;
        this.byteBuffer = BufferUtils.newByteBuffer(maxIndices * 2);
        this.isDirect = true;
        this.usage = 35044;
        (this.buffer = this.byteBuffer.asShortBuffer()).flip();
        this.byteBuffer.flip();
        this.bufferHandle = this.createBufferObject();
    }
    
    private int createBufferObject() {
        final int result = Gdx.gl20.glGenBuffer();
        Gdx.gl20.glBindBuffer(34963, result);
        Gdx.gl20.glBufferData(34963, this.byteBuffer.capacity(), null, this.usage);
        Gdx.gl20.glBindBuffer(34963, 0);
        return result;
    }
    
    @Override
    public int getNumIndices() {
        return this.buffer.limit();
    }
    
    @Override
    public int getNumMaxIndices() {
        return this.buffer.capacity();
    }
    
    @Override
    public void setIndices(final short[] indices, final int offset, final int count) {
        this.isDirty = true;
        this.buffer.clear();
        this.buffer.put(indices, offset, count);
        this.buffer.flip();
        this.byteBuffer.position(0);
        this.byteBuffer.limit(count << 1);
        if (this.isBound) {
            Gdx.gl20.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
    }
    
    @Override
    public void setIndices(final ShortBuffer indices) {
        final int pos = indices.position();
        this.isDirty = true;
        this.buffer.clear();
        this.buffer.put(indices);
        this.buffer.flip();
        indices.position(pos);
        this.byteBuffer.position(0);
        this.byteBuffer.limit(this.buffer.limit() << 1);
        if (this.isBound) {
            Gdx.gl20.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
    }
    
    @Override
    public void updateIndices(final int targetOffset, final short[] indices, final int offset, final int count) {
        this.isDirty = true;
        final int pos = this.byteBuffer.position();
        this.byteBuffer.position(targetOffset * 2);
        BufferUtils.copy(indices, offset, this.byteBuffer, count);
        this.byteBuffer.position(pos);
        this.buffer.position(0);
        if (this.isBound) {
            Gdx.gl20.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
    }
    
    @Override
    public ShortBuffer getBuffer() {
        this.isDirty = true;
        return this.buffer;
    }
    
    @Override
    public void bind() {
        if (this.bufferHandle == 0) {
            throw new GdxRuntimeException("IndexBufferObject cannot be used after it has been disposed.");
        }
        Gdx.gl20.glBindBuffer(34963, this.bufferHandle);
        if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 2);
            Gdx.gl20.glBufferSubData(34963, 0, this.byteBuffer.limit(), this.byteBuffer);
            this.isDirty = false;
        }
        this.isBound = true;
    }
    
    @Override
    public void unbind() {
        Gdx.gl20.glBindBuffer(34963, 0);
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
        gl.glBindBuffer(34963, 0);
        gl.glDeleteBuffer(this.bufferHandle);
        this.bufferHandle = 0;
    }
}
