// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Version
{
    public static final String VERSION = "1.9.9";
    public static final int MAJOR;
    public static final int MINOR;
    public static final int REVISION;
    
    static {
        try {
            final String[] v = "1.9.9".split("\\.");
            MAJOR = ((v.length < 1) ? 0 : Integer.valueOf(v[0]));
            MINOR = ((v.length < 2) ? 0 : Integer.valueOf(v[1]));
            REVISION = ((v.length < 3) ? 0 : Integer.valueOf(v[2]));
        }
        catch (Throwable t) {
            throw new GdxRuntimeException("Invalid version 1.9.9", t);
        }
    }
    
    public static boolean isHigher(final int major, final int minor, final int revision) {
        return isHigherEqual(major, minor, revision + 1);
    }
    
    public static boolean isHigherEqual(final int major, final int minor, final int revision) {
        if (Version.MAJOR != major) {
            return Version.MAJOR > major;
        }
        if (Version.MINOR != minor) {
            return Version.MINOR > minor;
        }
        return Version.REVISION >= revision;
    }
    
    public static boolean isLower(final int major, final int minor, final int revision) {
        return isLowerEqual(major, minor, revision - 1);
    }
    
    public static boolean isLowerEqual(final int major, final int minor, final int revision) {
        if (Version.MAJOR != major) {
            return Version.MAJOR < major;
        }
        if (Version.MINOR != minor) {
            return Version.MINOR < minor;
        }
        return Version.REVISION <= revision;
    }
}
