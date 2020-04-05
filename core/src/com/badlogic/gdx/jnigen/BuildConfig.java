// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

public class BuildConfig
{
    public final String sharedLibName;
    public final FileDescriptor buildDir;
    public final FileDescriptor libsDir;
    public final FileDescriptor jniDir;
    public String[] sharedLibs;
    
    public BuildConfig(final String sharedLibName) {
        this.sharedLibName = sharedLibName;
        this.buildDir = new FileDescriptor("target");
        this.libsDir = new FileDescriptor("libs");
        this.jniDir = new FileDescriptor("jni");
    }
    
    public BuildConfig(final String sharedLibName, final String temporaryDir, final String libsDir, final String jniDir) {
        this.sharedLibName = sharedLibName;
        this.buildDir = new FileDescriptor(temporaryDir);
        this.libsDir = new FileDescriptor(libsDir);
        this.jniDir = new FileDescriptor(jniDir);
    }
}
