// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.utils;

import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;

public class GdxBuild
{
    public static void main(final String[] args) throws Exception {
        final String JNI_DIR = "jni";
        final String LIBS_DIR = "libs";
        new NativeCodeGenerator().generate("src", "bin", JNI_DIR, new String[] { "**/*" }, null);
        final String[] excludeCpp = { "android/**", "iosgl/**" };
        final BuildTarget win32home = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32home.compilerPrefix = "";
        win32home.buildFileName = "build-windows32home.xml";
        win32home.excludeFromMasterBuildFile = true;
        win32home.cppExcludes = excludeCpp;
        final BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32.cppExcludes = excludeCpp;
        final BuildTarget win33 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        win33.cppExcludes = excludeCpp;
        final BuildTarget lin32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
        lin32.cppExcludes = excludeCpp;
        final BuildTarget lin33 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        lin33.cppExcludes = excludeCpp;
        final BuildTarget defaultTarget;
        final BuildTarget android = defaultTarget = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, false);
        defaultTarget.linkerFlags = String.valueOf(defaultTarget.linkerFlags) + " -lGLESv2 -llog";
        android.cppExcludes = new String[] { "iosgl/**" };
        final BuildTarget mac = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, false);
        mac.cppExcludes = excludeCpp;
        final BuildTarget mac2 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
        mac2.cppExcludes = excludeCpp;
        final BuildTarget ios = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
        ios.cppExcludes = new String[] { "android/**" };
        ios.headerDirs = new String[] { "iosgl" };
        new AntScriptGenerator().generate(new BuildConfig("gdx", "../target/native", LIBS_DIR, JNI_DIR), mac, mac2, win32home, win32, win33, lin32, lin33, android, ios);
    }
}
