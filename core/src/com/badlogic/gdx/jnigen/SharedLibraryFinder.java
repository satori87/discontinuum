// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.zip.ZipFile;

public interface SharedLibraryFinder
{
    String getSharedLibraryNameWindows(final String p0, final boolean p1, final ZipFile p2);
    
    String getSharedLibraryNameLinux(final String p0, final boolean p1, final boolean p2, final ZipFile p3);
    
    String getSharedLibraryNameMac(final String p0, final boolean p1, final ZipFile p2);
    
    String getSharedLibraryNameAndroid(final String p0, final ZipFile p1);
}
