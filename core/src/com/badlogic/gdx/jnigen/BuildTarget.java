// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

public class BuildTarget
{
    public TargetOs os;
    public boolean is64Bit;
    public String[] cIncludes;
    public String[] cExcludes;
    public String[] cppIncludes;
    public String[] cppExcludes;
    public String[] headerDirs;
    public String compilerPrefix;
    public String cFlags;
    public String cppFlags;
    public String linkerFlags;
    public String buildFileName;
    public boolean excludeFromMasterBuildFile;
    public String preCompileTask;
    public String postCompileTask;
    public String libraries;
    public String osFileName;
    public String libName;
    
    public BuildTarget(final TargetOs targetType, final boolean is64Bit, String[] cIncludes, String[] cExcludes, String[] cppIncludes, String[] cppExcludes, String[] headerDirs, String compilerPrefix, String cFlags, String cppFlags, String linkerFlags) {
        this.excludeFromMasterBuildFile = false;
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }
        if (cIncludes == null) {
            cIncludes = new String[0];
        }
        if (cExcludes == null) {
            cExcludes = new String[0];
        }
        if (cppIncludes == null) {
            cppIncludes = new String[0];
        }
        if (cppExcludes == null) {
            cppExcludes = new String[0];
        }
        if (headerDirs == null) {
            headerDirs = new String[0];
        }
        if (compilerPrefix == null) {
            compilerPrefix = "";
        }
        if (cFlags == null) {
            cFlags = "";
        }
        if (cppFlags == null) {
            cppFlags = "";
        }
        if (linkerFlags == null) {
            linkerFlags = "";
        }
        this.os = targetType;
        this.is64Bit = is64Bit;
        this.cIncludes = cIncludes;
        this.cExcludes = cExcludes;
        this.cppIncludes = cppIncludes;
        this.cppExcludes = cppExcludes;
        this.headerDirs = headerDirs;
        this.compilerPrefix = compilerPrefix;
        this.cFlags = cFlags;
        this.cppFlags = cppFlags;
        this.linkerFlags = linkerFlags;
        this.libraries = "";
    }
    
    public static BuildTarget newDefaultTarget(final TargetOs type, final boolean is64Bit) {
        if (type == TargetOs.Windows && !is64Bit) {
            return new BuildTarget(TargetOs.Windows, false, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "i686-w64-mingw32-", "-c -Wall -O2 -mfpmath=sse -msse2 -fmessage-length=0 -m32", "-c -Wall -O2 -mfpmath=sse -msse2 -fmessage-length=0 -m32", "-Wl,--kill-at -shared -m32 -static -static-libgcc -static-libstdc++");
        }
        if (type == TargetOs.Windows && is64Bit) {
            return new BuildTarget(TargetOs.Windows, true, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "x86_64-w64-mingw32-", "-c -Wall -O2 -mfpmath=sse -msse2 -fmessage-length=0 -m64", "-c -Wall -O2 -mfpmath=sse -msse2 -fmessage-length=0 -m64", "-Wl,--kill-at -shared -static -static-libgcc -static-libstdc++ -m64");
        }
        if (type == TargetOs.Linux && !is64Bit) {
            return new BuildTarget(TargetOs.Linux, false, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m32 -fPIC", "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m32 -fPIC", "-shared -m32");
        }
        if (type == TargetOs.Linux && is64Bit) {
            return new BuildTarget(TargetOs.Linux, true, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m64 -fPIC", "-c -Wall -O2 -mfpmath=sse -msse -fmessage-length=0 -m64 -fPIC", "-shared -m64 -Wl,-wrap,memcpy");
        }
        if (type == TargetOs.MacOsX && !is64Bit) {
            final BuildTarget mac = new BuildTarget(TargetOs.MacOsX, false, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-c -Wall -O2 -arch i386 -DFIXED_POINT -fmessage-length=0 -fPIC -mmacosx-version-min=10.5", "-c -Wall -O2 -arch i386 -DFIXED_POINT -fmessage-length=0 -fPIC -mmacosx-version-min=10.5", "-shared -arch i386 -mmacosx-version-min=10.5");
            return mac;
        }
        if (type == TargetOs.MacOsX && is64Bit) {
            final BuildTarget mac = new BuildTarget(TargetOs.MacOsX, true, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-c -Wall -O2 -arch x86_64 -DFIXED_POINT -fmessage-length=0 -fPIC -mmacosx-version-min=10.5", "-c -Wall -O2 -arch x86_64 -DFIXED_POINT -fmessage-length=0 -fPIC -mmacosx-version-min=10.5", "-shared -arch x86_64 -mmacosx-version-min=10.5");
            return mac;
        }
        if (type == TargetOs.Android) {
            final BuildTarget android = new BuildTarget(TargetOs.Android, false, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-O2 -Wall -D__ANDROID__", "-O2 -Wall -D__ANDROID__", "-lm");
            return android;
        }
        if (type == TargetOs.IOS) {
            final BuildTarget ios = new BuildTarget(TargetOs.IOS, false, new String[] { "**/*.c" }, new String[0], new String[] { "**/*.cpp" }, new String[0], new String[0], "", "-c -Wall -O2", "-c -Wall -O2", "rcs");
            return ios;
        }
        throw new RuntimeException("Unknown target type");
    }
    
    public enum TargetOs
    {
        Windows("Windows", 0), 
        Linux("Linux", 1), 
        MacOsX("MacOsX", 2), 
        Android("Android", 3), 
        IOS("IOS", 4);
        
        private TargetOs(final String name, final int ordinal) {
        }
    }
}
