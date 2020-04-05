// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen.test;

import java.nio.ByteBuffer;
import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;
import com.badlogic.gdx.jnigen.BuildExecutor;
import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import java.nio.Buffer;

public class MyJniClass
{
    public static native void test(final boolean p0, final byte p1, final char p2, final short p3, final int p4, final long p5, final float p6, final double p7, final Buffer p8, final boolean[] p9, final char[] p10, final short[] p11, final int[] p12, final long[] p13, final float[] p14, final double[] p15, final double[][] p16, final String p17, final Class p18, final Throwable p19, final Object p20);
    
    public static void main(final String[] args) throws Exception {
        new NativeCodeGenerator().generate("src", "bin", "jni", new String[] { "**/MyJniClass.java" }, null);
        final BuildConfig buildConfig = new BuildConfig("test");
        final BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32.compilerPrefix = "";
        final BuildTarget buildTarget = win32;
        buildTarget.cppFlags = String.valueOf(buildTarget.cppFlags) + " -g";
        final BuildTarget lin64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        new AntScriptGenerator().generate(buildConfig, win32, lin64);
        BuildExecutor.executeAnt("jni/build-linux64.xml", "-v -Dhas-compiler=true clean postcompile");
        BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");
        new JniGenSharedLibraryLoader("libs/test-natives.jar").load("test");
        final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
        buffer.put(0, (byte)8);
        test(true, (byte)1, '\u0002', (short)3, 4, 5L, 6.0f, 7.0, buffer, new boolean[1], new char[] { '\t' }, new short[] { 10 }, new int[] { 11 }, new long[] { 12L }, new float[] { 13.0f }, new double[] { 14.0 }, null, "Hurray", MyJniClass.class, new RuntimeException(), new MyJniClass());
    }
}
