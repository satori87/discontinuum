// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.I18NBundle;

public class I18NBundleLoader extends AsynchronousAssetLoader<I18NBundle, I18NBundleParameter>
{
    I18NBundle bundle;
    
    public I18NBundleLoader(final FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final I18NBundleParameter parameter) {
        this.bundle = null;
        Locale locale;
        String encoding;
        if (parameter == null) {
            locale = Locale.getDefault();
            encoding = null;
        }
        else {
            locale = ((parameter.locale == null) ? Locale.getDefault() : parameter.locale);
            encoding = parameter.encoding;
        }
        if (encoding == null) {
            this.bundle = I18NBundle.createBundle(file, locale);
        }
        else {
            this.bundle = I18NBundle.createBundle(file, locale, encoding);
        }
    }
    
    @Override
    public I18NBundle loadSync(final AssetManager manager, final String fileName, final FileHandle file, final I18NBundleParameter parameter) {
        final I18NBundle bundle = this.bundle;
        this.bundle = null;
        return bundle;
    }
    
    @Override
    public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final I18NBundleParameter parameter) {
        return null;
    }
    
    public static class I18NBundleParameter extends AssetLoaderParameters<I18NBundle>
    {
        public final Locale locale;
        public final String encoding;
        
        public I18NBundleParameter() {
            this(null, null);
        }
        
        public I18NBundleParameter(final Locale locale) {
            this(locale, null);
        }
        
        public I18NBundleParameter(final Locale locale, final String encoding) {
            this.locale = locale;
            this.encoding = encoding;
        }
    }
}
