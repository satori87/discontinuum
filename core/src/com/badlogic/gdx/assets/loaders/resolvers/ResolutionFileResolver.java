// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders.resolvers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public class ResolutionFileResolver implements FileHandleResolver
{
    protected final FileHandleResolver baseResolver;
    protected final Resolution[] descriptors;
    
    public ResolutionFileResolver(final FileHandleResolver baseResolver, final Resolution... descriptors) {
        if (descriptors.length == 0) {
            throw new IllegalArgumentException("At least one Resolution needs to be supplied.");
        }
        this.baseResolver = baseResolver;
        this.descriptors = descriptors;
    }
    
    @Override
    public FileHandle resolve(final String fileName) {
        final Resolution bestResolution = choose(this.descriptors);
        final FileHandle originalHandle = new FileHandle(fileName);
        FileHandle handle = this.baseResolver.resolve(this.resolve(originalHandle, bestResolution.folder));
        if (!handle.exists()) {
            handle = this.baseResolver.resolve(fileName);
        }
        return handle;
    }
    
    protected String resolve(final FileHandle originalHandle, final String suffix) {
        String parentString = "";
        final FileHandle parent = originalHandle.parent();
        if (parent != null && !parent.name().equals("")) {
            parentString = parent + "/";
        }
        return String.valueOf(parentString) + suffix + "/" + originalHandle.name();
    }
    
    public static Resolution choose(final Resolution... descriptors) {
        final int w = Gdx.graphics.getWidth();
        final int h = Gdx.graphics.getHeight();
        Resolution best = descriptors[0];
        if (w < h) {
            for (int i = 0, n = descriptors.length; i < n; ++i) {
                final Resolution other = descriptors[i];
                if (w >= other.portraitWidth && other.portraitWidth >= best.portraitWidth && h >= other.portraitHeight && other.portraitHeight >= best.portraitHeight) {
                    best = descriptors[i];
                }
            }
        }
        else {
            for (int i = 0, n = descriptors.length; i < n; ++i) {
                final Resolution other = descriptors[i];
                if (w >= other.portraitHeight && other.portraitHeight >= best.portraitHeight && h >= other.portraitWidth && other.portraitWidth >= best.portraitWidth) {
                    best = descriptors[i];
                }
            }
        }
        return best;
    }
    
    public static class Resolution
    {
        public final int portraitWidth;
        public final int portraitHeight;
        public final String folder;
        
        public Resolution(final int portraitWidth, final int portraitHeight, final String folder) {
            this.portraitWidth = portraitWidth;
            this.portraitHeight = portraitHeight;
            this.folder = folder;
        }
    }
}
