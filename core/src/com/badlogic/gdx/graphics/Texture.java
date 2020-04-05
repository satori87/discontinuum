// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import java.util.Iterator;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import java.nio.Buffer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import java.util.Map;
import com.badlogic.gdx.assets.AssetManager;

public class Texture extends GLTexture
{
    private static AssetManager assetManager;
    static final Map<Application, Array<Texture>> managedTextures;
    TextureData data;
    
    static {
        managedTextures = new HashMap<Application, Array<Texture>>();
    }
    
    public Texture(final String internalPath) {
        this(Gdx.files.internal(internalPath));
    }
    
    public Texture(final FileHandle file) {
        this(file, null, false);
    }
    
    public Texture(final FileHandle file, final boolean useMipMaps) {
        this(file, null, useMipMaps);
    }
    
    public Texture(final FileHandle file, final Pixmap.Format format, final boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(file, format, useMipMaps));
    }
    
    public Texture(final Pixmap pixmap) {
        this(new PixmapTextureData(pixmap, null, false, false));
    }
    
    public Texture(final Pixmap pixmap, final boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, null, useMipMaps, false));
    }
    
    public Texture(final Pixmap pixmap, final Pixmap.Format format, final boolean useMipMaps) {
        this(new PixmapTextureData(pixmap, format, useMipMaps, false));
    }
    
    public Texture(final int width, final int height, final Pixmap.Format format) {
        this(new PixmapTextureData(new Pixmap(width, height, format), null, false, true));
    }
    
    public Texture(final TextureData data) {
        this(3553, Gdx.gl.glGenTexture(), data);
    }
    
    protected Texture(final int glTarget, final int glHandle, final TextureData data) {
        super(glTarget, glHandle);
        this.load(data);
        if (data.isManaged()) {
            addManagedTexture(Gdx.app, this);
        }
    }
    
    public void load(final TextureData data) {
        if (this.data != null && data.isManaged() != this.data.isManaged()) {
            throw new GdxRuntimeException("New data must have the same managed status as the old data");
        }
        this.data = data;
        if (!data.isPrepared()) {
            data.prepare();
        }
        this.bind();
        GLTexture.uploadImageData(3553, data);
        this.unsafeSetFilter(this.minFilter, this.magFilter, true);
        this.unsafeSetWrap(this.uWrap, this.vWrap, true);
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }
    
    @Override
    protected void reload() {
        if (!this.isManaged()) {
            throw new GdxRuntimeException("Tried to reload unmanaged Texture");
        }
        this.glHandle = Gdx.gl.glGenTexture();
        this.load(this.data);
    }
    
    public void draw(final Pixmap pixmap, final int x, final int y) {
        if (this.data.isManaged()) {
            throw new GdxRuntimeException("can't draw to a managed texture");
        }
        this.bind();
        Gdx.gl.glTexSubImage2D(this.glTarget, 0, x, y, pixmap.getWidth(), pixmap.getHeight(), pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
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
        return 0;
    }
    
    public TextureData getTextureData() {
        return this.data;
    }
    
    @Override
    public boolean isManaged() {
        return this.data.isManaged();
    }
    
    @Override
    public void dispose() {
        if (this.glHandle == 0) {
            return;
        }
        this.delete();
        if (this.data.isManaged() && Texture.managedTextures.get(Gdx.app) != null) {
            Texture.managedTextures.get(Gdx.app).removeValue(this, true);
        }
    }
    
    @Override
    public String toString() {
        if (this.data instanceof FileTextureData) {
            return this.data.toString();
        }
        return super.toString();
    }
    
    private static void addManagedTexture(final Application app, final Texture texture) {
        Array<Texture> managedTextureArray = Texture.managedTextures.get(app);
        if (managedTextureArray == null) {
            managedTextureArray = new Array<Texture>();
        }
        managedTextureArray.add(texture);
        Texture.managedTextures.put(app, managedTextureArray);
    }
    
    public static void clearAllTextures(final Application app) {
        Texture.managedTextures.remove(app);
    }
    
    public static void invalidateAllTextures(final Application app) {
        final Array<Texture> managedTextureArray = Texture.managedTextures.get(app);
        if (managedTextureArray == null) {
            return;
        }
        if (Texture.assetManager == null) {
            for (int i = 0; i < managedTextureArray.size; ++i) {
                final Texture texture = managedTextureArray.get(i);
                texture.reload();
            }
        }
        else {
            Texture.assetManager.finishLoading();
            final Array<Texture> textures = new Array<Texture>(managedTextureArray);
            for (final Texture texture : textures) {
                final String fileName = Texture.assetManager.getAssetFileName(texture);
                if (fileName == null) {
                    texture.reload();
                }
                else {
                    final int refCount = Texture.assetManager.getReferenceCount(fileName);
                    Texture.assetManager.setReferenceCount(fileName, 0);
                    texture.glHandle = 0;
                    final TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
                    params.textureData = texture.getTextureData();
                    params.minFilter = texture.getMinFilter();
                    params.magFilter = texture.getMagFilter();
                    params.wrapU = texture.getUWrap();
                    params.wrapV = texture.getVWrap();
                    params.genMipMaps = texture.data.useMipMaps();
                    params.texture = texture;
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        @Override
                        public void finishedLoading(final AssetManager assetManager, final String fileName, final Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };
                    Texture.assetManager.unload(fileName);
                    texture.glHandle = Gdx.gl.glGenTexture();
                    Texture.assetManager.load(fileName, Texture.class, params);
                }
            }
            managedTextureArray.clear();
            managedTextureArray.addAll(textures);
        }
    }
    
    public static void setAssetManager(final AssetManager manager) {
        Texture.assetManager = manager;
    }
    
    public static String getManagedStatus() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Managed textures/app: { ");
        for (final Application app : Texture.managedTextures.keySet()) {
            builder.append(Texture.managedTextures.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
    
    public static int getNumManagedTextures() {
        return Texture.managedTextures.get(Gdx.app).size;
    }
    
    public enum TextureFilter
    {
        Nearest("Nearest", 0, 9728), 
        Linear("Linear", 1, 9729), 
        MipMap("MipMap", 2, 9987), 
        MipMapNearestNearest("MipMapNearestNearest", 3, 9984), 
        MipMapLinearNearest("MipMapLinearNearest", 4, 9985), 
        MipMapNearestLinear("MipMapNearestLinear", 5, 9986), 
        MipMapLinearLinear("MipMapLinearLinear", 6, 9987);
        
        final int glEnum;
        
        private TextureFilter(final String name, final int ordinal, final int glEnum) {
            this.glEnum = glEnum;
        }
        
        public boolean isMipMap() {
            return this.glEnum != 9728 && this.glEnum != 9729;
        }
        
        public int getGLEnum() {
            return this.glEnum;
        }
    }
    
    public enum TextureWrap
    {
        MirroredRepeat("MirroredRepeat", 0, 33648), 
        ClampToEdge("ClampToEdge", 1, 33071), 
        Repeat("Repeat", 2, 10497);
        
        final int glEnum;
        
        private TextureWrap(final String name, final int ordinal, final int glEnum) {
            this.glEnum = glEnum;
        }
        
        public int getGLEnum() {
            return this.glEnum;
        }
    }
}
