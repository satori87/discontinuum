// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import java.util.Iterator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.assets.loaders.CubemapLoader;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.graphics.g2d.PolygonRegionLoader;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import java.util.Stack;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Disposable;

public class AssetManager implements Disposable
{
    final ObjectMap<Class, ObjectMap<String, RefCountedContainer>> assets;
    final ObjectMap<String, Class> assetTypes;
    final ObjectMap<String, Array<String>> assetDependencies;
    final ObjectSet<String> injected;
    final ObjectMap<Class, ObjectMap<String, AssetLoader>> loaders;
    final Array<AssetDescriptor> loadQueue;
    final AsyncExecutor executor;
    final Stack<AssetLoadingTask> tasks;
    AssetErrorListener listener;
    int loaded;
    int toLoad;
    int peakTasks;
    final FileHandleResolver resolver;
    Logger log;
    
    public AssetManager() {
        this(new InternalFileHandleResolver());
    }
    
    public AssetManager(final FileHandleResolver resolver) {
        this(resolver, true);
    }
    
    public AssetManager(final FileHandleResolver resolver, final boolean defaultLoaders) {
        this.assets = new ObjectMap<Class, ObjectMap<String, RefCountedContainer>>();
        this.assetTypes = new ObjectMap<String, Class>();
        this.assetDependencies = new ObjectMap<String, Array<String>>();
        this.injected = new ObjectSet<String>();
        this.loaders = new ObjectMap<Class, ObjectMap<String, AssetLoader>>();
        this.loadQueue = new Array<AssetDescriptor>();
        this.tasks = new Stack<AssetLoadingTask>();
        this.listener = null;
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.log = new Logger("AssetManager", 0);
        this.resolver = resolver;
        if (defaultLoaders) {
            this.setLoader(BitmapFont.class, (AssetLoader<BitmapFont, AssetLoaderParameters>)new BitmapFontLoader(resolver));
            this.setLoader(Music.class, (AssetLoader<Music, AssetLoaderParameters>)new MusicLoader(resolver));
            this.setLoader(Pixmap.class, (AssetLoader<Pixmap, AssetLoaderParameters>)new PixmapLoader(resolver));
            this.setLoader(Sound.class, (AssetLoader<Sound, AssetLoaderParameters>)new SoundLoader(resolver));
            this.setLoader(TextureAtlas.class, (AssetLoader<TextureAtlas, AssetLoaderParameters>)new TextureAtlasLoader(resolver));
            this.setLoader(Texture.class, (AssetLoader<Texture, AssetLoaderParameters>)new TextureLoader(resolver));
            this.setLoader(Skin.class, (AssetLoader<Skin, AssetLoaderParameters>)new SkinLoader(resolver));
            this.setLoader(ParticleEffect.class, (AssetLoader<ParticleEffect, AssetLoaderParameters>)new ParticleEffectLoader(resolver));
            this.setLoader(com.badlogic.gdx.graphics.g3d.particles.ParticleEffect.class, (AssetLoader<com.badlogic.gdx.graphics.g3d.particles.ParticleEffect, AssetLoaderParameters>)new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader(resolver));
            this.setLoader(PolygonRegion.class, (AssetLoader<PolygonRegion, AssetLoaderParameters>)new PolygonRegionLoader(resolver));
            this.setLoader(I18NBundle.class, (AssetLoader<I18NBundle, AssetLoaderParameters>)new I18NBundleLoader(resolver));
            this.setLoader(Model.class, ".g3dj", (AssetLoader<Model, AssetLoaderParameters>)new G3dModelLoader(new JsonReader(), resolver));
            this.setLoader(Model.class, ".g3db", (AssetLoader<Model, AssetLoaderParameters>)new G3dModelLoader(new UBJsonReader(), resolver));
            this.setLoader(Model.class, ".obj", (AssetLoader<Model, AssetLoaderParameters>)new ObjLoader(resolver));
            this.setLoader(ShaderProgram.class, (AssetLoader<ShaderProgram, AssetLoaderParameters>)new ShaderProgramLoader(resolver));
            this.setLoader(Cubemap.class, (AssetLoader<Cubemap, AssetLoaderParameters>)new CubemapLoader(resolver));
        }
        this.executor = new AsyncExecutor(1);
    }
    
    public FileHandleResolver getFileHandleResolver() {
        return this.resolver;
    }
    
    public synchronized <T> T get(final String fileName) {
        final Class<T> type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final T asset = assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }
    
    public synchronized <T> T get(final String fileName, final Class<T> type) {
        final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final T asset = assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }
    
    public synchronized <T> Array<T> getAll(final Class<T> type, final Array<T> out) {
        final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType != null) {
            for (final ObjectMap.Entry<String, RefCountedContainer> asset : assetsByType.entries()) {
                out.add(asset.value.getObject(type));
            }
        }
        return out;
    }
    
    public synchronized <T> T get(final AssetDescriptor<T> assetDescriptor) {
        return this.get(assetDescriptor.fileName, assetDescriptor.type);
    }
    
    public synchronized boolean contains(final String fileName) {
        if (this.tasks.size() > 0 && this.tasks.firstElement().assetDesc.fileName.equals(fileName)) {
            return true;
        }
        for (int i = 0; i < this.loadQueue.size; ++i) {
            if (this.loadQueue.get(i).fileName.equals(fileName)) {
                return true;
            }
        }
        return this.isLoaded(fileName);
    }
    
    public synchronized boolean contains(final String fileName, final Class type) {
        if (this.tasks.size() > 0) {
            final AssetDescriptor assetDesc = this.tasks.firstElement().assetDesc;
            if (assetDesc.type == type && assetDesc.fileName.equals(fileName)) {
                return true;
            }
        }
        for (int i = 0; i < this.loadQueue.size; ++i) {
            final AssetDescriptor assetDesc2 = this.loadQueue.get(i);
            if (assetDesc2.type == type && assetDesc2.fileName.equals(fileName)) {
                return true;
            }
        }
        return this.isLoaded(fileName, type);
    }
    
    public synchronized void unload(final String fileName) {
        if (this.tasks.size() > 0) {
            final AssetLoadingTask currAsset = this.tasks.firstElement();
            if (currAsset.assetDesc.fileName.equals(fileName)) {
                currAsset.cancel = true;
                this.log.info("Unload (from tasks): " + fileName);
                return;
            }
        }
        int foundIndex = -1;
        for (int i = 0; i < this.loadQueue.size; ++i) {
            if (this.loadQueue.get(i).fileName.equals(fileName)) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            --this.toLoad;
            this.loadQueue.removeIndex(foundIndex);
            this.log.info("Unload (from queue): " + fileName);
            return;
        }
        final Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        final RefCountedContainer assetRef = this.assets.get(type).get(fileName);
        assetRef.decRefCount();
        if (assetRef.getRefCount() <= 0) {
            this.log.info("Unload (dispose): " + fileName);
            if (assetRef.getObject(Object.class) instanceof Disposable) {
                assetRef.getObject((Class<Disposable>)Object.class).dispose();
            }
            this.assetTypes.remove(fileName);
            this.assets.get(type).remove(fileName);
        }
        else {
            this.log.info("Unload (decrement): " + fileName);
        }
        final Array<String> dependencies = this.assetDependencies.get(fileName);
        if (dependencies != null) {
            for (final String dependency : dependencies) {
                if (this.isLoaded(dependency)) {
                    this.unload(dependency);
                }
            }
        }
        if (assetRef.getRefCount() <= 0) {
            this.assetDependencies.remove(fileName);
        }
    }
    
    public synchronized <T> boolean containsAsset(final T asset) {
        final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(asset.getClass());
        if (assetsByType == null) {
            return false;
        }
        for (final String fileName : assetsByType.keys()) {
            final T otherAsset = assetsByType.get(fileName).getObject((Class<T>)Object.class);
            if (otherAsset == asset || asset.equals(otherAsset)) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized <T> String getAssetFileName(final T asset) {
        for (final Class assetType : this.assets.keys()) {
            final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(assetType);
            for (final String fileName : assetsByType.keys()) {
                final T otherAsset = assetsByType.get(fileName).getObject((Class<T>)Object.class);
                if (otherAsset == asset || asset.equals(otherAsset)) {
                    return fileName;
                }
            }
        }
        return null;
    }
    
    public synchronized boolean isLoaded(final AssetDescriptor assetDesc) {
        return this.isLoaded(assetDesc.fileName);
    }
    
    public synchronized boolean isLoaded(final String fileName) {
        return fileName != null && this.assetTypes.containsKey(fileName);
    }
    
    public synchronized boolean isLoaded(final String fileName, final Class type) {
        final ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            return false;
        }
        final RefCountedContainer assetContainer = assetsByType.get(fileName);
        return assetContainer != null && assetContainer.getObject((Class<Object>)type) != null;
    }
    
    public <T> AssetLoader getLoader(final Class<T> type) {
        return this.getLoader(type, null);
    }
    
    public <T> AssetLoader getLoader(final Class<T> type, final String fileName) {
        final ObjectMap<String, AssetLoader> loaders = this.loaders.get(type);
        if (loaders == null || loaders.size < 1) {
            return null;
        }
        if (fileName == null) {
            return loaders.get("");
        }
        AssetLoader result = null;
        int l = -1;
        for (final ObjectMap.Entry<String, AssetLoader> entry : loaders.entries()) {
            if (entry.key.length() > l && fileName.endsWith(entry.key)) {
                result = entry.value;
                l = entry.key.length();
            }
        }
        return result;
    }
    
    public synchronized <T> void load(final String fileName, final Class<T> type) {
        this.load(fileName, type, null);
    }
    
    public synchronized <T> void load(final String fileName, final Class<T> type, final AssetLoaderParameters<T> parameter) {
        final AssetLoader loader = this.getLoader(type, fileName);
        if (loader == null) {
            throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(type));
        }
        if (this.loadQueue.size == 0) {
            this.loaded = 0;
            this.toLoad = 0;
            this.peakTasks = 0;
        }
        for (int i = 0; i < this.loadQueue.size; ++i) {
            final AssetDescriptor desc = this.loadQueue.get(i);
            if (desc.fileName.equals(fileName) && !desc.type.equals(type)) {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in preload queue, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc.type) + ")");
            }
        }
        for (int i = 0; i < this.tasks.size(); ++i) {
            final AssetDescriptor desc = this.tasks.get(i).assetDesc;
            if (desc.fileName.equals(fileName) && !desc.type.equals(type)) {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in task list, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(desc.type) + ")");
            }
        }
        final Class otherType = this.assetTypes.get(fileName);
        if (otherType != null && !otherType.equals(type)) {
            throw new GdxRuntimeException("Asset with name '" + fileName + "' already loaded, but has different type (expected: " + ClassReflection.getSimpleName(type) + ", found: " + ClassReflection.getSimpleName(otherType) + ")");
        }
        ++this.toLoad;
        final AssetDescriptor assetDesc = new AssetDescriptor(fileName, (Class<T>)type, (AssetLoaderParameters<T>)parameter);
        this.loadQueue.add(assetDesc);
        this.log.debug("Queued: " + assetDesc);
    }
    
    public synchronized void load(final AssetDescriptor desc) {
        this.load(desc.fileName, desc.type, desc.params);
    }
    
    public synchronized boolean update() {
        try {
            if (this.tasks.size() == 0) {
                while (this.loadQueue.size != 0 && this.tasks.size() == 0) {
                    this.nextTask();
                }
                if (this.tasks.size() == 0) {
                    return true;
                }
            }
            return this.updateTask() && this.loadQueue.size == 0 && this.tasks.size() == 0;
        }
        catch (Throwable t) {
            this.handleTaskError(t);
            return this.loadQueue.size == 0;
        }
    }
    
    public boolean update(final int millis) {
        final long endTime = TimeUtils.millis() + millis;
        boolean done;
        while (true) {
            done = this.update();
            if (done || TimeUtils.millis() > endTime) {
                break;
            }
            ThreadUtils.yield();
        }
        return done;
    }
    
    public synchronized boolean isFinished() {
        return this.loadQueue.size == 0 && this.tasks.size() == 0;
    }
    
    public void finishLoading() {
        this.log.debug("Waiting for loading to complete...");
        while (!this.update()) {
            ThreadUtils.yield();
        }
        this.log.debug("Loading complete.");
    }
    
    public void finishLoadingAsset(final AssetDescriptor assetDesc) {
        this.finishLoadingAsset(assetDesc.fileName);
    }
    
    public void finishLoadingAsset(final String fileName) {
        this.log.debug("Waiting for asset to be loaded: " + fileName);
        while (!this.isLoaded(fileName)) {
            this.update();
            ThreadUtils.yield();
        }
        this.log.debug("Asset loaded: " + fileName);
    }
    
    synchronized void injectDependencies(final String parentAssetFilename, final Array<AssetDescriptor> dependendAssetDescs) {
        final ObjectSet<String> injected = this.injected;
        for (final AssetDescriptor desc : dependendAssetDescs) {
            if (injected.contains(desc.fileName)) {
                continue;
            }
            injected.add(desc.fileName);
            this.injectDependency(parentAssetFilename, desc);
        }
        injected.clear();
    }
    
    private synchronized void injectDependency(final String parentAssetFilename, final AssetDescriptor dependendAssetDesc) {
        Array<String> dependencies = this.assetDependencies.get(parentAssetFilename);
        if (dependencies == null) {
            dependencies = new Array<String>();
            this.assetDependencies.put(parentAssetFilename, dependencies);
        }
        dependencies.add(dependendAssetDesc.fileName);
        if (this.isLoaded(dependendAssetDesc.fileName)) {
            this.log.debug("Dependency already loaded: " + dependendAssetDesc);
            final Class type = this.assetTypes.get(dependendAssetDesc.fileName);
            final RefCountedContainer assetRef = this.assets.get(type).get(dependendAssetDesc.fileName);
            assetRef.incRefCount();
            this.incrementRefCountedDependencies(dependendAssetDesc.fileName);
        }
        else {
            this.log.info("Loading dependency: " + dependendAssetDesc);
            this.addTask(dependendAssetDesc);
        }
    }
    
    private void nextTask() {
        final AssetDescriptor assetDesc = this.loadQueue.removeIndex(0);
        if (this.isLoaded(assetDesc.fileName)) {
            this.log.debug("Already loaded: " + assetDesc);
            final Class type = this.assetTypes.get(assetDesc.fileName);
            final RefCountedContainer assetRef = this.assets.get(type).get(assetDesc.fileName);
            assetRef.incRefCount();
            this.incrementRefCountedDependencies(assetDesc.fileName);
            if (assetDesc.params != null && assetDesc.params.loadedCallback != null) {
                assetDesc.params.loadedCallback.finishedLoading(this, assetDesc.fileName, assetDesc.type);
            }
            ++this.loaded;
        }
        else {
            this.log.info("Loading: " + assetDesc);
            this.addTask(assetDesc);
        }
    }
    
    private void addTask(final AssetDescriptor assetDesc) {
        final AssetLoader loader = this.getLoader(assetDesc.type, assetDesc.fileName);
        if (loader == null) {
            throw new GdxRuntimeException("No loader for type: " + ClassReflection.getSimpleName(assetDesc.type));
        }
        this.tasks.push(new AssetLoadingTask(this, assetDesc, loader, this.executor));
        ++this.peakTasks;
    }
    
    protected <T> void addAsset(final String fileName, final Class<T> type, final T asset) {
        this.assetTypes.put(fileName, type);
        ObjectMap<String, RefCountedContainer> typeToAssets = this.assets.get(type);
        if (typeToAssets == null) {
            typeToAssets = new ObjectMap<String, RefCountedContainer>();
            this.assets.put(type, typeToAssets);
        }
        typeToAssets.put(fileName, new RefCountedContainer(asset));
    }
    
    private boolean updateTask() {
        final AssetLoadingTask task = this.tasks.peek();
        boolean complete = true;
        try {
            complete = (task.cancel || task.update());
        }
        catch (RuntimeException ex) {
            task.cancel = true;
            this.taskFailed(task.assetDesc, ex);
        }
        if (!complete) {
            return false;
        }
        if (this.tasks.size() == 1) {
            ++this.loaded;
            this.peakTasks = 0;
        }
        this.tasks.pop();
        if (task.cancel) {
            return true;
        }
        this.addAsset(task.assetDesc.fileName, (Class<Object>)task.assetDesc.type, task.getAsset());
        if (task.assetDesc.params != null && task.assetDesc.params.loadedCallback != null) {
            task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName, task.assetDesc.type);
        }
        final long endTime = TimeUtils.nanoTime();
        this.log.debug("Loaded: " + (endTime - task.startTime) / 1000000.0f + "ms " + task.assetDesc);
        return true;
    }
    
    protected void taskFailed(final AssetDescriptor assetDesc, final RuntimeException ex) {
        throw ex;
    }
    
    private void incrementRefCountedDependencies(final String parent) {
        final Array<String> dependencies = this.assetDependencies.get(parent);
        if (dependencies == null) {
            return;
        }
        for (final String dependency : dependencies) {
            final Class type = this.assetTypes.get(dependency);
            final RefCountedContainer assetRef = this.assets.get(type).get(dependency);
            assetRef.incRefCount();
            this.incrementRefCountedDependencies(dependency);
        }
    }
    
    private void handleTaskError(final Throwable t) {
        this.log.error("Error loading asset.", t);
        if (this.tasks.isEmpty()) {
            throw new GdxRuntimeException(t);
        }
        final AssetLoadingTask task = this.tasks.pop();
        final AssetDescriptor assetDesc = task.assetDesc;
        if (task.dependenciesLoaded && task.dependencies != null) {
            for (final AssetDescriptor desc : task.dependencies) {
                this.unload(desc.fileName);
            }
        }
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.error(assetDesc, t);
            return;
        }
        throw new GdxRuntimeException(t);
    }
    
    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(final Class<T> type, final AssetLoader<T, P> loader) {
        this.setLoader(type, null, loader);
    }
    
    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(final Class<T> type, final String suffix, final AssetLoader<T, P> loader) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        if (loader == null) {
            throw new IllegalArgumentException("loader cannot be null.");
        }
        this.log.debug("Loader set: " + ClassReflection.getSimpleName(type) + " -> " + ClassReflection.getSimpleName(loader.getClass()));
        ObjectMap<String, AssetLoader> loaders = this.loaders.get(type);
        if (loaders == null) {
            this.loaders.put(type, loaders = new ObjectMap<String, AssetLoader>());
        }
        loaders.put((suffix == null) ? "" : suffix, loader);
    }
    
    public synchronized int getLoadedAssets() {
        return this.assetTypes.size;
    }
    
    public synchronized int getQueuedAssets() {
        return this.loadQueue.size + this.tasks.size();
    }
    
    public synchronized float getProgress() {
        if (this.toLoad == 0) {
            return 1.0f;
        }
        float fractionalLoaded = (float)this.loaded;
        if (this.peakTasks > 0) {
            fractionalLoaded += (this.peakTasks - this.tasks.size()) / (float)this.peakTasks;
        }
        return Math.min(1.0f, fractionalLoaded / this.toLoad);
    }
    
    public synchronized void setErrorListener(final AssetErrorListener listener) {
        this.listener = listener;
    }
    
    @Override
    public synchronized void dispose() {
        this.log.debug("Disposing.");
        this.clear();
        this.executor.dispose();
    }
    
    public synchronized void clear() {
        this.loadQueue.clear();
        while (!this.update()) {}
        final ObjectIntMap<String> dependencyCount = new ObjectIntMap<String>();
        while (this.assetTypes.size > 0) {
            dependencyCount.clear();
            final Array<String> assets = this.assetTypes.keys().toArray();
            for (final String asset : assets) {
                dependencyCount.put(asset, 0);
            }
            for (final String asset : assets) {
                final Array<String> dependencies = this.assetDependencies.get(asset);
                if (dependencies == null) {
                    continue;
                }
                for (final String dependency : dependencies) {
                    int count = dependencyCount.get(dependency, 0);
                    ++count;
                    dependencyCount.put(dependency, count);
                }
            }
            for (final String asset : assets) {
                if (dependencyCount.get(asset, 0) == 0) {
                    this.unload(asset);
                }
            }
        }
        this.assets.clear();
        this.assetTypes.clear();
        this.assetDependencies.clear();
        this.loaded = 0;
        this.toLoad = 0;
        this.peakTasks = 0;
        this.loadQueue.clear();
        this.tasks.clear();
    }
    
    public Logger getLogger() {
        return this.log;
    }
    
    public void setLogger(final Logger logger) {
        this.log = logger;
    }
    
    public synchronized int getReferenceCount(final String fileName) {
        final Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return this.assets.get(type).get(fileName).getRefCount();
    }
    
    public synchronized void setReferenceCount(final String fileName, final int refCount) {
        final Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        this.assets.get(type).get(fileName).setRefCount(refCount);
    }
    
    public synchronized String getDiagnostics() {
        final StringBuilder sb = new StringBuilder();
        for (final String fileName : this.assetTypes.keys()) {
            sb.append(fileName);
            sb.append(", ");
            final Class type = this.assetTypes.get(fileName);
            final RefCountedContainer assetRef = this.assets.get(type).get(fileName);
            final Array<String> dependencies = this.assetDependencies.get(fileName);
            sb.append(ClassReflection.getSimpleName(type));
            sb.append(", refs: ");
            sb.append(assetRef.getRefCount());
            if (dependencies != null) {
                sb.append(", deps: [");
                for (final String dep : dependencies) {
                    sb.append(dep);
                    sb.append(",");
                }
                sb.append("]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public synchronized Array<String> getAssetNames() {
        return this.assetTypes.keys().toArray();
    }
    
    public synchronized Array<String> getDependencies(final String fileName) {
        return this.assetDependencies.get(fileName);
    }
    
    public synchronized Class getAssetType(final String fileName) {
        return this.assetTypes.get(fileName);
    }
}
