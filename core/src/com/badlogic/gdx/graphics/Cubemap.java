// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.math.Vector3;
import java.util.Iterator;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Application;
import java.util.Map;
import com.badlogic.gdx.assets.AssetManager;

public class Cubemap extends GLTexture
{
    private static AssetManager assetManager;
    static final Map<Application, Array<Cubemap>> managedCubemaps;
    protected CubemapData data;
    
    static {
        managedCubemaps = new HashMap<Application, Array<Cubemap>>();
    }
    
    public Cubemap(final CubemapData data) {
        super(34067);
        this.load(this.data = data);
    }
    
    public Cubemap(final FileHandle positiveX, final FileHandle negativeX, final FileHandle positiveY, final FileHandle negativeY, final FileHandle positiveZ, final FileHandle negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }
    
    public Cubemap(final FileHandle positiveX, final FileHandle negativeX, final FileHandle positiveY, final FileHandle negativeY, final FileHandle positiveZ, final FileHandle negativeZ, final boolean useMipMaps) {
        this(TextureData.Factory.loadFromFile(positiveX, useMipMaps), TextureData.Factory.loadFromFile(negativeX, useMipMaps), TextureData.Factory.loadFromFile(positiveY, useMipMaps), TextureData.Factory.loadFromFile(negativeY, useMipMaps), TextureData.Factory.loadFromFile(positiveZ, useMipMaps), TextureData.Factory.loadFromFile(negativeZ, useMipMaps));
    }
    
    public Cubemap(final Pixmap positiveX, final Pixmap negativeX, final Pixmap positiveY, final Pixmap negativeY, final Pixmap positiveZ, final Pixmap negativeZ) {
        this(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ, false);
    }
    
    public Cubemap(final Pixmap positiveX, final Pixmap negativeX, final Pixmap positiveY, final Pixmap negativeY, final Pixmap positiveZ, final Pixmap negativeZ, final boolean useMipMaps) {
        this((positiveX == null) ? null : new PixmapTextureData(positiveX, null, useMipMaps, false), (negativeX == null) ? null : new PixmapTextureData(negativeX, null, useMipMaps, false), (positiveY == null) ? null : new PixmapTextureData(positiveY, null, useMipMaps, false), (negativeY == null) ? null : new PixmapTextureData(negativeY, null, useMipMaps, false), (positiveZ == null) ? null : new PixmapTextureData(positiveZ, null, useMipMaps, false), (negativeZ == null) ? null : new PixmapTextureData(negativeZ, null, useMipMaps, false));
    }
    
    public Cubemap(final int width, final int height, final int depth, final Pixmap.Format format) {
        this(new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(depth, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, depth, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true), new PixmapTextureData(new Pixmap(width, height, format), null, false, true));
    }
    
    public Cubemap(final TextureData positiveX, final TextureData negativeX, final TextureData positiveY, final TextureData negativeY, final TextureData positiveZ, final TextureData negativeZ) {
        super(34067);
        this.minFilter = Texture.TextureFilter.Nearest;
        this.magFilter = Texture.TextureFilter.Nearest;
        this.uWrap = Texture.TextureWrap.ClampToEdge;
        this.vWrap = Texture.TextureWrap.ClampToEdge;
        this.load(this.data = new FacedCubemapData(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ));
    }
    
    public void load(final CubemapData data) {
        if (!data.isPrepared()) {
            data.prepare();
        }
        this.bind();
        this.unsafeSetFilter(this.minFilter, this.magFilter, true);
        this.unsafeSetWrap(this.uWrap, this.vWrap, true);
        data.consumeCubemapData();
        Gdx.gl.glBindTexture(this.glTarget, 0);
    }
    
    public CubemapData getCubemapData() {
        return this.data;
    }
    
    @Override
    public boolean isManaged() {
        return this.data.isManaged();
    }
    
    @Override
    protected void reload() {
        if (!this.isManaged()) {
            throw new GdxRuntimeException("Tried to reload an unmanaged Cubemap");
        }
        this.glHandle = Gdx.gl.glGenTexture();
        this.load(this.data);
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
    
    @Override
    public void dispose() {
        if (this.glHandle == 0) {
            return;
        }
        this.delete();
        if (this.data.isManaged() && Cubemap.managedCubemaps.get(Gdx.app) != null) {
            Cubemap.managedCubemaps.get(Gdx.app).removeValue(this, true);
        }
    }
    
    private static void addManagedCubemap(final Application app, final Cubemap cubemap) {
        Array<Cubemap> managedCubemapArray = Cubemap.managedCubemaps.get(app);
        if (managedCubemapArray == null) {
            managedCubemapArray = new Array<Cubemap>();
        }
        managedCubemapArray.add(cubemap);
        Cubemap.managedCubemaps.put(app, managedCubemapArray);
    }
    
    public static void clearAllCubemaps(final Application app) {
        Cubemap.managedCubemaps.remove(app);
    }
    
    public static void invalidateAllCubemaps(final Application app) {
        final Array<Cubemap> managedCubemapArray = Cubemap.managedCubemaps.get(app);
        if (managedCubemapArray == null) {
            return;
        }
        if (Cubemap.assetManager == null) {
            for (int i = 0; i < managedCubemapArray.size; ++i) {
                final Cubemap cubemap = managedCubemapArray.get(i);
                cubemap.reload();
            }
        }
        else {
            Cubemap.assetManager.finishLoading();
            final Array<Cubemap> cubemaps = new Array<Cubemap>(managedCubemapArray);
            for (final Cubemap cubemap : cubemaps) {
                final String fileName = Cubemap.assetManager.getAssetFileName(cubemap);
                if (fileName == null) {
                    cubemap.reload();
                }
                else {
                    final int refCount = Cubemap.assetManager.getReferenceCount(fileName);
                    Cubemap.assetManager.setReferenceCount(fileName, 0);
                    cubemap.glHandle = 0;
                    final CubemapLoader.CubemapParameter params = new CubemapLoader.CubemapParameter();
                    params.cubemapData = cubemap.getCubemapData();
                    params.minFilter = cubemap.getMinFilter();
                    params.magFilter = cubemap.getMagFilter();
                    params.wrapU = cubemap.getUWrap();
                    params.wrapV = cubemap.getVWrap();
                    params.cubemap = cubemap;
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        @Override
                        public void finishedLoading(final AssetManager assetManager, final String fileName, final Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };
                    Cubemap.assetManager.unload(fileName);
                    cubemap.glHandle = Gdx.gl.glGenTexture();
                    Cubemap.assetManager.load(fileName, Cubemap.class, params);
                }
            }
            managedCubemapArray.clear();
            managedCubemapArray.addAll(cubemaps);
        }
    }
    
    public static void setAssetManager(final AssetManager manager) {
        Cubemap.assetManager = manager;
    }
    
    public static String getManagedStatus() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Managed cubemap/app: { ");
        for (final Application app : Cubemap.managedCubemaps.keySet()) {
            builder.append(Cubemap.managedCubemaps.get(app).size);
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }
    
    public static int getNumManagedCubemaps() {
        return Cubemap.managedCubemaps.get(Gdx.app).size;
    }
    
    public enum CubemapSide
    {
        PositiveX("PositiveX", 0, 0, 34069, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f), 
        NegativeX("NegativeX", 1, 1, 34070, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f), 
        PositiveY("PositiveY", 2, 2, 34071, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f), 
        NegativeY("NegativeY", 3, 3, 34072, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f), 
        PositiveZ("PositiveZ", 4, 4, 34073, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f), 
        NegativeZ("NegativeZ", 5, 5, 34074, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f);
        
        public final int index;
        public final int glEnum;
        public final Vector3 up;
        public final Vector3 direction;
        
        private CubemapSide(final String name, final int ordinal, final int index, final int glEnum, final float upX, final float upY, final float upZ, final float directionX, final float directionY, final float directionZ) {
            this.index = index;
            this.glEnum = glEnum;
            this.up = new Vector3(upX, upY, upZ);
            this.direction = new Vector3(directionX, directionY, directionZ);
        }
        
        public int getGLEnum() {
            return this.glEnum;
        }
        
        public Vector3 getUp(final Vector3 out) {
            return out.set(this.up);
        }
        
        public Vector3 getDirection(final Vector3 out) {
            return out.set(this.direction);
        }
    }
}
