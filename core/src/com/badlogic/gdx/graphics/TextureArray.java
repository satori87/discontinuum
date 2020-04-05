// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.util.Iterator;
import java.nio.Buffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import java.util.Map;

public class TextureArray extends GLTexture
{
    static final Map<Application, Array<TextureArray>> managedTextureArrays;
    private TextureArrayData data;
    
    static {
        managedTextureArrays = new HashMap<Application, Array<TextureArray>>();
    }
    
    public TextureArray(final String... internalPaths) {
        this(getInternalHandles(internalPaths));
    }
    
    public TextureArray(final FileHandle... files) {
        this(false, files);
    }
    
    public TextureArray(final boolean useMipMaps, final FileHandle... files) {
        this(useMipMaps, Pixmap.Format.RGBA8888, files);
    }
    
    public TextureArray(final boolean useMipMaps, final Pixmap.Format format, final FileHandle... files) {
        this(TextureArrayData.Factory.loadFromFiles(format, useMipMaps, files));
    }
    
    public TextureArray(final TextureArrayData data) {
        super(35866, Gdx.gl.glGenTexture());
        if (Gdx.gl30 == null) {
            throw new GdxRuntimeException("TextureArray requires a device running with GLES 3.0 compatibilty");
        }
        this.load(data);
        if (data.isManaged()) {
            addManagedTexture(Gdx.app, this);
        }
    }
    
    private static FileHandle[] getInternalHandles(final String... internalPaths) {
        final FileHandle[] handles = new FileHandle[internalPaths.length];
        for (int i = 0; i < internalPaths.length; ++i) {
            handles[i] = Gdx.files.internal(internalPaths[i]);
        }
        return handles;
    }
    
    private void load(final TextureArrayData data) {
        if (this.data != null && data.isManaged() != this.data.isManaged()) {
            throw new GdxRuntimeException("New data must have the same managed status as the old data");
        }
        this.data = data;
        this.bind();
        Gdx.gl30.glTexImage3D(35866, 0, data.getInternalFormat(), data.getWidth(), data.getHeight(), data.getDepth(), 0, data.getInternalFormat(), data.getGLType(), null);
        if (!data.isPrepared()) {
            data.prepare();
        }
        data.consumeTextureArrayData();
        this.setFilter(this.minFilter, this.magFilter);
        this.setWrap(this.uWrap, this.vWrap);
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }
    
    @Override
    public int getWidth() {
        return this.data.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.data.getHeight();
    }
    
    @Override
    public int getDepth() {
        return this.data.getDepth();
    }
    
    @Override
    public boolean isManaged() {
        return this.data.isManaged();
    }
    
    @Override
    protected void reload() {
        if (!this.isManaged()) {
            throw new GdxRuntimeException("Tried to reload an unmanaged TextureArray");
        }
        this.glHandle = Gdx.gl.glGenTexture();
        this.load(this.data);
    }
    
    private static void addManagedTexture(final Application app, final TextureArray texture) {
        Array<TextureArray> managedTextureArray = TextureArray.managedTextureArrays.get(app);
        if (managedTextureArray == null) {
            managedTextureArray = new Array<TextureArray>();
        }
        managedTextureArray.add(texture);
        TextureArray.managedTextureArrays.put(app, managedTextureArray);
    }
    
    public static void clearAllTextureArrays(final Application app) {
        TextureArray.managedTextureArrays.remove(app);
    }
    
    public static void invalidateAllTextureArrays(final Application app) {
        final Array<TextureArray> managedTextureArray = TextureArray.managedTextureArrays.get(app);
        if (managedTextureArray == null) {
            return;
        }
        for (int i = 0; i < managedTextureArray.size; ++i) {
            final TextureArray textureArray = managedTextureArray.get(i);
            textureArray.reload();
        }
    }
    
    public static String getManagedStatus() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Managed TextureArrays/app: { ");
        for (final Application app : TextureArray.managedTextureArrays.keySet()) {
            builder.append(TextureArray.managedTextureArrays.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
    
    public static int getNumManagedTextureArrays() {
        return TextureArray.managedTextureArrays.get(Gdx.app).size;
    }
}
