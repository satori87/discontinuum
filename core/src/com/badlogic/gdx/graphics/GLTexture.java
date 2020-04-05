// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.nio.Buffer;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public abstract class GLTexture implements Disposable
{
    public final int glTarget;
    protected int glHandle;
    protected Texture.TextureFilter minFilter;
    protected Texture.TextureFilter magFilter;
    protected Texture.TextureWrap uWrap;
    protected Texture.TextureWrap vWrap;
    
    public abstract int getWidth();
    
    public abstract int getHeight();
    
    public abstract int getDepth();
    
    public GLTexture(final int glTarget) {
        this(glTarget, Gdx.gl.glGenTexture());
    }
    
    public GLTexture(final int glTarget, final int glHandle) {
        this.minFilter = Texture.TextureFilter.Nearest;
        this.magFilter = Texture.TextureFilter.Nearest;
        this.uWrap = Texture.TextureWrap.ClampToEdge;
        this.vWrap = Texture.TextureWrap.ClampToEdge;
        this.glTarget = glTarget;
        this.glHandle = glHandle;
    }
    
    public abstract boolean isManaged();
    
    protected abstract void reload();
    
    public void bind() {
        Gdx.gl.glBindTexture(this.glTarget, this.glHandle);
    }
    
    public void bind(final int unit) {
        Gdx.gl.glActiveTexture(33984 + unit);
        Gdx.gl.glBindTexture(this.glTarget, this.glHandle);
    }
    
    public Texture.TextureFilter getMinFilter() {
        return this.minFilter;
    }
    
    public Texture.TextureFilter getMagFilter() {
        return this.magFilter;
    }
    
    public Texture.TextureWrap getUWrap() {
        return this.uWrap;
    }
    
    public Texture.TextureWrap getVWrap() {
        return this.vWrap;
    }
    
    public int getTextureObjectHandle() {
        return this.glHandle;
    }
    
    public void unsafeSetWrap(final Texture.TextureWrap u, final Texture.TextureWrap v) {
        this.unsafeSetWrap(u, v, false);
    }
    
    public void unsafeSetWrap(final Texture.TextureWrap u, final Texture.TextureWrap v, final boolean force) {
        if (u != null && (force || this.uWrap != u)) {
            Gdx.gl.glTexParameteri(this.glTarget, 10242, u.getGLEnum());
            this.uWrap = u;
        }
        if (v != null && (force || this.vWrap != v)) {
            Gdx.gl.glTexParameteri(this.glTarget, 10243, v.getGLEnum());
            this.vWrap = v;
        }
    }
    
    public void setWrap(final Texture.TextureWrap u, final Texture.TextureWrap v) {
        this.uWrap = u;
        this.vWrap = v;
        this.bind();
        Gdx.gl.glTexParameteri(this.glTarget, 10242, u.getGLEnum());
        Gdx.gl.glTexParameteri(this.glTarget, 10243, v.getGLEnum());
    }
    
    public void unsafeSetFilter(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter) {
        this.unsafeSetFilter(minFilter, magFilter, false);
    }
    
    public void unsafeSetFilter(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter, final boolean force) {
        if (minFilter != null && (force || this.minFilter != minFilter)) {
            Gdx.gl.glTexParameteri(this.glTarget, 10241, minFilter.getGLEnum());
            this.minFilter = minFilter;
        }
        if (magFilter != null && (force || this.magFilter != magFilter)) {
            Gdx.gl.glTexParameteri(this.glTarget, 10240, magFilter.getGLEnum());
            this.magFilter = magFilter;
        }
    }
    
    public void setFilter(final Texture.TextureFilter minFilter, final Texture.TextureFilter magFilter) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.bind();
        Gdx.gl.glTexParameteri(this.glTarget, 10241, minFilter.getGLEnum());
        Gdx.gl.glTexParameteri(this.glTarget, 10240, magFilter.getGLEnum());
    }
    
    protected void delete() {
        if (this.glHandle != 0) {
            Gdx.gl.glDeleteTexture(this.glHandle);
            this.glHandle = 0;
        }
    }
    
    @Override
    public void dispose() {
        this.delete();
    }
    
    protected static void uploadImageData(final int target, final TextureData data) {
        uploadImageData(target, data, 0);
    }
    
    public static void uploadImageData(final int target, final TextureData data, final int miplevel) {
        if (data == null) {
            return;
        }
        if (!data.isPrepared()) {
            data.prepare();
        }
        final TextureData.TextureDataType type = data.getType();
        if (type == TextureData.TextureDataType.Custom) {
            data.consumeCustomData(target);
            return;
        }
        Pixmap pixmap = data.consumePixmap();
        boolean disposePixmap = data.disposePixmap();
        if (data.getFormat() != pixmap.getFormat()) {
            final Pixmap tmp = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), data.getFormat());
            tmp.setBlending(Pixmap.Blending.None);
            tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
            if (data.disposePixmap()) {
                pixmap.dispose();
            }
            pixmap = tmp;
            disposePixmap = true;
        }
        Gdx.gl.glPixelStorei(3317, 1);
        if (data.useMipMaps()) {
            MipMapGenerator.generateMipMap(target, pixmap, pixmap.getWidth(), pixmap.getHeight());
        }
        else {
            Gdx.gl.glTexImage2D(target, miplevel, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
        }
        if (disposePixmap) {
            pixmap.dispose();
        }
    }
}
