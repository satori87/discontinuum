// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.files.FileHandle;

public interface Files
{
    FileHandle getFileHandle(final String p0, final FileType p1);
    
    FileHandle classpath(final String p0);
    
    FileHandle internal(final String p0);
    
    FileHandle external(final String p0);
    
    FileHandle absolute(final String p0);
    
    FileHandle local(final String p0);
    
    String getExternalStoragePath();
    
    boolean isExternalStorageAvailable();
    
    String getLocalStoragePath();
    
    boolean isLocalStorageAvailable();
    
    public enum FileType
    {
        Classpath("Classpath", 0), 
        Internal("Internal", 1), 
        External("External", 2), 
        Absolute("Absolute", 3), 
        Local("Local", 4);
        
        private FileType(final String name, final int ordinal) {
        }
    }
}
