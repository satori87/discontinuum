// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.utils.async.AsyncTask;

class AssetLoadingTask implements AsyncTask<Void>
{
    AssetManager manager;
    final AssetDescriptor assetDesc;
    final AssetLoader loader;
    final AsyncExecutor executor;
    final long startTime;
    volatile boolean asyncDone;
    volatile boolean dependenciesLoaded;
    volatile Array<AssetDescriptor> dependencies;
    volatile AsyncResult<Void> depsFuture;
    volatile AsyncResult<Void> loadFuture;
    volatile Object asset;
    int ticks;
    volatile boolean cancel;
    
    public AssetLoadingTask(final AssetManager manager, final AssetDescriptor assetDesc, final AssetLoader loader, final AsyncExecutor threadPool) {
        this.asyncDone = false;
        this.dependenciesLoaded = false;
        this.depsFuture = null;
        this.loadFuture = null;
        this.asset = null;
        this.ticks = 0;
        this.cancel = false;
        this.manager = manager;
        this.assetDesc = assetDesc;
        this.loader = loader;
        this.executor = threadPool;
        this.startTime = ((manager.log.getLevel() == 3) ? TimeUtils.nanoTime() : 0L);
    }
    
    @Override
    public Void call() throws Exception {
        final AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)this.loader;
        if (!this.dependenciesLoaded) {
            this.dependencies = (Array<AssetDescriptor>)asyncLoader.getDependencies(this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
            if (this.dependencies != null) {
                this.removeDuplicates(this.dependencies);
                this.manager.injectDependencies(this.assetDesc.fileName, this.dependencies);
            }
            else {
                asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
                this.asyncDone = true;
            }
        }
        else {
            asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
        }
        return null;
    }
    
    public boolean update() {
        ++this.ticks;
        if (this.loader instanceof SynchronousAssetLoader) {
            this.handleSyncLoader();
        }
        else {
            this.handleAsyncLoader();
        }
        return this.asset != null;
    }
    
    private void handleSyncLoader() {
        final SynchronousAssetLoader syncLoader = (SynchronousAssetLoader)this.loader;
        if (!this.dependenciesLoaded) {
            this.dependenciesLoaded = true;
            this.dependencies = (Array<AssetDescriptor>)syncLoader.getDependencies(this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
            if (this.dependencies == null) {
                this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
                return;
            }
            this.removeDuplicates(this.dependencies);
            this.manager.injectDependencies(this.assetDesc.fileName, this.dependencies);
        }
        else {
            this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
        }
    }
    
    private void handleAsyncLoader() {
        final AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader)this.loader;
        if (!this.dependenciesLoaded) {
            if (this.depsFuture == null) {
                this.depsFuture = this.executor.submit((AsyncTask<Void>)this);
            }
            else if (this.depsFuture.isDone()) {
                try {
                    this.depsFuture.get();
                }
                catch (Exception e) {
                    throw new GdxRuntimeException("Couldn't load dependencies of asset: " + this.assetDesc.fileName, e);
                }
                this.dependenciesLoaded = true;
                if (this.asyncDone) {
                    this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
                }
            }
        }
        else if (this.loadFuture == null && !this.asyncDone) {
            this.loadFuture = this.executor.submit((AsyncTask<Void>)this);
        }
        else if (this.asyncDone) {
            this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
        }
        else if (this.loadFuture.isDone()) {
            try {
                this.loadFuture.get();
            }
            catch (Exception e) {
                throw new GdxRuntimeException("Couldn't load asset: " + this.assetDesc.fileName, e);
            }
            this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.resolve(this.loader, this.assetDesc), this.assetDesc.params);
        }
    }
    
    private FileHandle resolve(final AssetLoader loader, final AssetDescriptor assetDesc) {
        if (assetDesc.file == null) {
            assetDesc.file = loader.resolve(assetDesc.fileName);
        }
        return assetDesc.file;
    }
    
    public Object getAsset() {
        return this.asset;
    }
    
    private void removeDuplicates(final Array<AssetDescriptor> array) {
        final boolean ordered = array.ordered;
        array.ordered = true;
        for (int i = 0; i < array.size; ++i) {
            final String fn = array.get(i).fileName;
            final Class type = array.get(i).type;
            for (int j = array.size - 1; j > i; --j) {
                if (type == array.get(j).type && fn.equals(array.get(j).fileName)) {
                    array.removeIndex(j);
                }
            }
        }
        array.ordered = ordered;
    }
}
