// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

public class GdxNativesLoader
{
    public static boolean disableNativesLoading;
    private static boolean nativesLoaded;
    
    static {
        GdxNativesLoader.disableNativesLoading = false;
    }
    
    public static synchronized void load() {
        if (GdxNativesLoader.nativesLoaded) {
            return;
        }
        GdxNativesLoader.nativesLoaded = true;
        if (GdxNativesLoader.disableNativesLoading) {
            return;
        }
        new SharedLibraryLoader().load("gdx");
    }
}
