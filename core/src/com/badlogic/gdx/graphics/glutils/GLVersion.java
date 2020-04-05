// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.graphics.glutils;

import java.util.regex.Matcher;
import com.badlogic.gdx.Gdx;
import java.util.regex.Pattern;
import com.badlogic.gdx.Application;

public class GLVersion
{
    private int majorVersion;
    private int minorVersion;
    private int releaseVersion;
    private final String vendorString;
    private final String rendererString;
    private final Type type;
    private final String TAG = "GLVersion";
    
    public GLVersion(final Application.ApplicationType appType, final String versionString, String vendorString, String rendererString) {
        if (appType == Application.ApplicationType.Android) {
            this.type = Type.GLES;
        }
        else if (appType == Application.ApplicationType.iOS) {
            this.type = Type.GLES;
        }
        else if (appType == Application.ApplicationType.Desktop) {
            this.type = Type.OpenGL;
        }
        else if (appType == Application.ApplicationType.Applet) {
            this.type = Type.OpenGL;
        }
        else if (appType == Application.ApplicationType.WebGL) {
            this.type = Type.WebGL;
        }
        else {
            this.type = Type.NONE;
        }
        if (this.type == Type.GLES) {
            this.extractVersion("OpenGL ES (\\d(\\.\\d){0,2})", versionString);
        }
        else if (this.type == Type.WebGL) {
            this.extractVersion("WebGL (\\d(\\.\\d){0,2})", versionString);
        }
        else if (this.type == Type.OpenGL) {
            this.extractVersion("(\\d(\\.\\d){0,2})", versionString);
        }
        else {
            this.majorVersion = -1;
            this.minorVersion = -1;
            this.releaseVersion = -1;
            vendorString = "";
            rendererString = "";
        }
        this.vendorString = vendorString;
        this.rendererString = rendererString;
    }
    
    private void extractVersion(final String patternString, final String versionString) {
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(versionString);
        final boolean found = matcher.find();
        if (found) {
            final String result = matcher.group(1);
            final String[] resultSplit = result.split("\\.");
            this.majorVersion = this.parseInt(resultSplit[0], 2);
            this.minorVersion = ((resultSplit.length < 2) ? 0 : this.parseInt(resultSplit[1], 0));
            this.releaseVersion = ((resultSplit.length < 3) ? 0 : this.parseInt(resultSplit[2], 0));
        }
        else {
            Gdx.app.log("GLVersion", "Invalid version string: " + versionString);
            this.majorVersion = 2;
            this.minorVersion = 0;
            this.releaseVersion = 0;
        }
    }
    
    private int parseInt(final String v, final int defaultValue) {
        try {
            return Integer.parseInt(v);
        }
        catch (NumberFormatException nfe) {
            Gdx.app.error("LibGDX GL", "Error parsing number: " + v + ", assuming: " + defaultValue);
            return defaultValue;
        }
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int getMajorVersion() {
        return this.majorVersion;
    }
    
    public int getMinorVersion() {
        return this.minorVersion;
    }
    
    public int getReleaseVersion() {
        return this.releaseVersion;
    }
    
    public String getVendorString() {
        return this.vendorString;
    }
    
    public String getRendererString() {
        return this.rendererString;
    }
    
    public boolean isVersionEqualToOrHigher(final int testMajorVersion, final int testMinorVersion) {
        return this.majorVersion > testMajorVersion || (this.majorVersion == testMajorVersion && this.minorVersion >= testMinorVersion);
    }
    
    public String getDebugVersionString() {
        return "Type: " + this.type + "\n" + "Version: " + this.majorVersion + ":" + this.minorVersion + ":" + this.releaseVersion + "\n" + "Vendor: " + this.vendorString + "\n" + "Renderer: " + this.rendererString;
    }
    
    public enum Type
    {
        OpenGL("OpenGL", 0), 
        GLES("GLES", 1), 
        WebGL("WebGL", 2), 
        NONE("NONE", 3);
        
        private Type(final String name, final int ordinal) {
        }
    }
}
