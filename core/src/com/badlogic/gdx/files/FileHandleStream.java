// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.files;

import java.io.OutputStream;
import java.io.InputStream;
import com.badlogic.gdx.Files;
import java.io.File;

public abstract class FileHandleStream extends FileHandle
{
    public FileHandleStream(final String path) {
        super(new File(path), Files.FileType.Absolute);
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public long length() {
        return 0L;
    }
    
    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public FileHandle child(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileHandle sibling(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileHandle parent() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream read() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public OutputStream write(final boolean overwrite) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileHandle[] list() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void mkdirs() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean deleteDirectory() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void copyTo(final FileHandle dest) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void moveTo(final FileHandle dest) {
        throw new UnsupportedOperationException();
    }
}
