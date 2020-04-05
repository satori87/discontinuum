// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import java.io.Reader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import com.badlogic.gdx.files.FileHandle;
import java.util.Locale;

public class I18NBundle
{
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Locale ROOT_LOCALE;
    private static boolean simpleFormatter;
    private static boolean exceptionOnMissingKey;
    private I18NBundle parent;
    private Locale locale;
    private ObjectMap<String, String> properties;
    private TextFormatter formatter;
    
    static {
        ROOT_LOCALE = new Locale("", "", "");
        I18NBundle.simpleFormatter = false;
        I18NBundle.exceptionOnMissingKey = true;
    }
    
    public static boolean getSimpleFormatter() {
        return I18NBundle.simpleFormatter;
    }
    
    public static void setSimpleFormatter(final boolean enabled) {
        I18NBundle.simpleFormatter = enabled;
    }
    
    public static boolean getExceptionOnMissingKey() {
        return I18NBundle.exceptionOnMissingKey;
    }
    
    public static void setExceptionOnMissingKey(final boolean enabled) {
        I18NBundle.exceptionOnMissingKey = enabled;
    }
    
    public static I18NBundle createBundle(final FileHandle baseFileHandle) {
        return createBundleImpl(baseFileHandle, Locale.getDefault(), "UTF-8");
    }
    
    public static I18NBundle createBundle(final FileHandle baseFileHandle, final Locale locale) {
        return createBundleImpl(baseFileHandle, locale, "UTF-8");
    }
    
    public static I18NBundle createBundle(final FileHandle baseFileHandle, final String encoding) {
        return createBundleImpl(baseFileHandle, Locale.getDefault(), encoding);
    }
    
    public static I18NBundle createBundle(final FileHandle baseFileHandle, final Locale locale, final String encoding) {
        return createBundleImpl(baseFileHandle, locale, encoding);
    }
    
    private static I18NBundle createBundleImpl(final FileHandle baseFileHandle, final Locale locale, final String encoding) {
        if (baseFileHandle == null || locale == null || encoding == null) {
            throw new NullPointerException();
        }
        I18NBundle bundle = null;
        I18NBundle baseBundle = null;
        Locale targetLocale = locale;
        do {
            final List<Locale> candidateLocales = getCandidateLocales(targetLocale);
            bundle = loadBundleChain(baseFileHandle, encoding, candidateLocales, 0, baseBundle);
            if (bundle != null) {
                final Locale bundleLocale = bundle.getLocale();
                final boolean isBaseBundle = bundleLocale.equals(I18NBundle.ROOT_LOCALE);
                if (!isBaseBundle) {
                    break;
                }
                if (bundleLocale.equals(locale)) {
                    break;
                }
                if (candidateLocales.size() == 1 && bundleLocale.equals(candidateLocales.get(0))) {
                    break;
                }
                if (isBaseBundle && baseBundle == null) {
                    baseBundle = bundle;
                }
            }
            targetLocale = getFallbackLocale(targetLocale);
        } while (targetLocale != null);
        if (bundle == null) {
            if (baseBundle == null) {
                throw new MissingResourceException("Can't find bundle for base file handle " + baseFileHandle.path() + ", locale " + locale, baseFileHandle + "_" + locale, "");
            }
            bundle = baseBundle;
        }
        return bundle;
    }
    
    private static List<Locale> getCandidateLocales(final Locale locale) {
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        final List<Locale> locales = new ArrayList<Locale>(4);
        if (variant.length() > 0) {
            locales.add(locale);
        }
        if (country.length() > 0) {
            locales.add(locales.isEmpty() ? locale : new Locale(language, country));
        }
        if (language.length() > 0) {
            locales.add(locales.isEmpty() ? locale : new Locale(language));
        }
        locales.add(I18NBundle.ROOT_LOCALE);
        return locales;
    }
    
    private static Locale getFallbackLocale(final Locale locale) {
        final Locale defaultLocale = Locale.getDefault();
        return locale.equals(defaultLocale) ? null : defaultLocale;
    }
    
    private static I18NBundle loadBundleChain(final FileHandle baseFileHandle, final String encoding, final List<Locale> candidateLocales, final int candidateIndex, final I18NBundle baseBundle) {
        final Locale targetLocale = candidateLocales.get(candidateIndex);
        I18NBundle parent = null;
        if (candidateIndex != candidateLocales.size() - 1) {
            parent = loadBundleChain(baseFileHandle, encoding, candidateLocales, candidateIndex + 1, baseBundle);
        }
        else if (baseBundle != null && targetLocale.equals(I18NBundle.ROOT_LOCALE)) {
            return baseBundle;
        }
        final I18NBundle bundle = loadBundle(baseFileHandle, encoding, targetLocale);
        if (bundle != null) {
            bundle.parent = parent;
            return bundle;
        }
        return parent;
    }
    
    private static I18NBundle loadBundle(final FileHandle baseFileHandle, final String encoding, final Locale targetLocale) {
        I18NBundle bundle = null;
        Reader reader = null;
        try {
            final FileHandle fileHandle = toFileHandle(baseFileHandle, targetLocale);
            if (checkFileExistence(fileHandle)) {
                bundle = new I18NBundle();
                reader = fileHandle.reader(encoding);
                bundle.load(reader);
            }
        }
        catch (IOException e) {
            throw new GdxRuntimeException(e);
        }
        finally {
            StreamUtils.closeQuietly(reader);
        }
        StreamUtils.closeQuietly(reader);
        if (bundle != null) {
            bundle.setLocale(targetLocale);
        }
        return bundle;
    }
    
    private static boolean checkFileExistence(final FileHandle fh) {
        try {
            fh.read().close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    protected void load(final Reader reader) throws IOException {
        PropertiesUtils.load(this.properties = new ObjectMap<String, String>(), reader);
    }
    
    private static FileHandle toFileHandle(final FileHandle baseFileHandle, final Locale locale) {
        final com.badlogic.gdx.utils.StringBuilder sb = new com.badlogic.gdx.utils.StringBuilder(baseFileHandle.name());
        if (!locale.equals(I18NBundle.ROOT_LOCALE)) {
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            final String variant = locale.getVariant();
            final boolean emptyLanguage = "".equals(language);
            final boolean emptyCountry = "".equals(country);
            final boolean emptyVariant = "".equals(variant);
            if (!emptyLanguage || !emptyCountry || !emptyVariant) {
                sb.append('_');
                if (!emptyVariant) {
                    sb.append(language).append('_').append(country).append('_').append(variant);
                }
                else if (!emptyCountry) {
                    sb.append(language).append('_').append(country);
                }
                else {
                    sb.append(language);
                }
            }
        }
        return baseFileHandle.sibling(sb.append(".properties").toString());
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    private void setLocale(final Locale locale) {
        this.locale = locale;
        this.formatter = new TextFormatter(locale, !I18NBundle.simpleFormatter);
    }
    
    public final String get(final String key) {
        String result = this.properties.get(key);
        if (result == null) {
            if (this.parent != null) {
                result = this.parent.get(key);
            }
            if (result == null) {
                if (I18NBundle.exceptionOnMissingKey) {
                    throw new MissingResourceException("Can't find bundle key " + key, this.getClass().getName(), key);
                }
                return "???" + key + "???";
            }
        }
        return result;
    }
    
    public String format(final String key, final Object... args) {
        return this.formatter.format(this.get(key), args);
    }
}
