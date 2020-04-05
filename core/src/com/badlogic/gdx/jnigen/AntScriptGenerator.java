// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.ArrayList;

public class AntScriptGenerator
{
    public void generate(final BuildConfig config, final BuildTarget... targets) {
        if (!config.libsDir.exists() && !config.libsDir.mkdirs()) {
            throw new RuntimeException("Couldn't create directory for shared library files in '" + config.libsDir + "'");
        }
        if (!config.jniDir.exists() && !config.jniDir.mkdirs()) {
            throw new RuntimeException("Couldn't create native code directory '" + config.jniDir + "'");
        }
        this.copyJniHeaders(config.jniDir.path());
        if (!config.jniDir.child("memcpy_wrap.c").exists()) {
            new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/memcpy_wrap.c", FileDescriptor.FileType.Classpath).copyTo(config.jniDir.child("memcpy_wrap.c"));
        }
        final ArrayList<String> buildFiles = new ArrayList<String>();
        final ArrayList<String> libsDirs = new ArrayList<String>();
        final ArrayList<String> sharedLibFiles = new ArrayList<String>();
        for (final BuildTarget target : targets) {
            final String buildFile = this.generateBuildTargetTemplate(config, target);
            final FileDescriptor libsDir = new FileDescriptor(this.getLibsDirectory(config, target));
            if (!libsDir.exists() && !libsDir.mkdirs()) {
                throw new RuntimeException("Couldn't create libs directory '" + libsDir + "'");
            }
            String buildFileName = "build-" + target.os.toString().toLowerCase() + (target.is64Bit ? "64" : "32") + ".xml";
            if (target.buildFileName != null) {
                buildFileName = target.buildFileName;
            }
            config.jniDir.child(buildFileName).writeString(buildFile, false);
            System.out.println("Wrote target '" + target.os + (target.is64Bit ? "64" : "") + "' build script '" + config.jniDir.child(buildFileName) + "'");
            if (!target.excludeFromMasterBuildFile) {
                if (target.os != BuildTarget.TargetOs.MacOsX && target.os != BuildTarget.TargetOs.IOS) {
                    buildFiles.add(buildFileName);
                }
                String sharedLibFilename = target.libName;
                if (sharedLibFilename == null) {
                    sharedLibFilename = this.getSharedLibFilename(target.os, target.is64Bit, config.sharedLibName);
                }
                sharedLibFiles.add(sharedLibFilename);
                if (target.os != BuildTarget.TargetOs.Android && target.os != BuildTarget.TargetOs.IOS) {
                    libsDirs.add("../" + libsDir.path().replace('\\', '/'));
                }
            }
        }
        String template = new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/build.xml.template", FileDescriptor.FileType.Classpath).readString();
        final StringBuilder clean = new StringBuilder();
        final StringBuilder compile = new StringBuilder();
        final StringBuilder pack = new StringBuilder();
        for (int i = 0; i < buildFiles.size(); ++i) {
            clean.append("\t\t<ant antfile=\"" + buildFiles.get(i) + "\" target=\"clean\"/>\n");
            compile.append("\t\t<ant antfile=\"" + buildFiles.get(i) + "\"/>\n");
        }
        for (int i = 0; i < libsDirs.size(); ++i) {
            pack.append("\t\t\t<fileset dir=\"" + libsDirs.get(i) + "\" includes=\"" + sharedLibFiles.get(i) + "\"/>\n");
        }
        if (config.sharedLibs != null) {
            String[] sharedLibs;
            for (int length2 = (sharedLibs = config.sharedLibs).length, k = 0; k < length2; ++k) {
                final String sharedLib = sharedLibs[k];
                pack.append("\t\t\t<fileset dir=\"" + sharedLib + "\"/>\n");
            }
        }
        template = template.replace("%projectName%", String.valueOf(config.sharedLibName) + "-natives");
        template = template.replace("<clean/>", clean.toString());
        template = template.replace("<compile/>", compile.toString());
        template = template.replace("%packFile%", "../" + config.libsDir.path().replace('\\', '/') + "/" + config.sharedLibName + "-natives.jar");
        template = template.replace("<pack/>", pack);
        config.jniDir.child("build.xml").writeString(template, false);
        System.out.println("Wrote master build script '" + config.jniDir.child("build.xml") + "'");
    }
    
    private void copyJniHeaders(final String jniDir) {
        final String pack = "com/badlogic/gdx/jnigen/resources/headers";
        final String[] files = { "classfile_constants.h", "jawt.h", "jdwpTransport.h", "jni.h", "linux/jawt_md.h", "linux/jni_md.h", "mac/jni_md.h", "win32/jawt_md.h", "win32/jni_md.h" };
        String[] array;
        for (int length = (array = files).length, i = 0; i < length; ++i) {
            final String file = array[i];
            new FileDescriptor("com/badlogic/gdx/jnigen/resources/headers", FileDescriptor.FileType.Classpath).child(file).copyTo(new FileDescriptor(jniDir).child("jni-headers").child(file));
        }
    }
    
    private String getSharedLibFilename(final BuildTarget.TargetOs os, final boolean is64Bit, final String sharedLibName) {
        String libPrefix = "";
        String libSuffix = "";
        if (os == BuildTarget.TargetOs.Windows) {
            libSuffix = String.valueOf(is64Bit ? "64" : "") + ".dll";
        }
        if (os == BuildTarget.TargetOs.Linux || os == BuildTarget.TargetOs.Android) {
            libPrefix = "lib";
            libSuffix = String.valueOf(is64Bit ? "64" : "") + ".so";
        }
        if (os == BuildTarget.TargetOs.MacOsX) {
            libPrefix = "lib";
            libSuffix = String.valueOf(is64Bit ? "64" : "") + ".dylib";
        }
        if (os == BuildTarget.TargetOs.IOS) {
            libPrefix = "lib";
            libSuffix = ".a";
        }
        return String.valueOf(libPrefix) + sharedLibName + libSuffix;
    }
    
    private String getJniPlatform(final BuildTarget.TargetOs os) {
        if (os == BuildTarget.TargetOs.Windows) {
            return "win32";
        }
        if (os == BuildTarget.TargetOs.Linux) {
            return "linux";
        }
        if (os == BuildTarget.TargetOs.MacOsX) {
            return "mac";
        }
        return "";
    }
    
    private String getLibsDirectory(final BuildConfig config, final BuildTarget target) {
        String targetName = target.osFileName;
        if (targetName == null) {
            targetName = String.valueOf(target.os.toString().toLowerCase()) + (target.is64Bit ? "64" : "32");
        }
        return config.libsDir.child(targetName).path().replace('\\', '/');
    }
    
    private String generateBuildTargetTemplate(final BuildConfig config, final BuildTarget target) {
        if (target.os == BuildTarget.TargetOs.Android) {
            new AndroidNdkScriptGenerator().generate(config, target);
            String template = new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/build-android.xml.template", FileDescriptor.FileType.Classpath).readString();
            template = template.replace("%precompile%", (target.preCompileTask == null) ? "" : target.preCompileTask);
            template = template.replace("%postcompile%", (target.postCompileTask == null) ? "" : target.postCompileTask);
            return template;
        }
        String template = null;
        if (target.os == BuildTarget.TargetOs.IOS) {
            template = new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/build-ios.xml.template", FileDescriptor.FileType.Classpath).readString();
        }
        else {
            template = new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/build-target.xml.template", FileDescriptor.FileType.Classpath).readString();
        }
        String libName = target.libName;
        if (libName == null) {
            libName = this.getSharedLibFilename(target.os, target.is64Bit, config.sharedLibName);
        }
        final String jniPlatform = this.getJniPlatform(target.os);
        final StringBuilder cIncludes = new StringBuilder();
        cIncludes.append("\t\t<include name=\"memcpy_wrap.c\"/>\n");
        String[] cIncludes2;
        for (int length = (cIncludes2 = target.cIncludes).length, i = 0; i < length; ++i) {
            final String cInclude = cIncludes2[i];
            cIncludes.append("\t\t<include name=\"" + cInclude + "\"/>\n");
        }
        final StringBuilder cppIncludes = new StringBuilder();
        String[] cppIncludes2;
        for (int length2 = (cppIncludes2 = target.cppIncludes).length, j = 0; j < length2; ++j) {
            final String cppInclude = cppIncludes2[j];
            cppIncludes.append("\t\t<include name=\"" + cppInclude + "\"/>\n");
        }
        final StringBuilder cExcludes = new StringBuilder();
        String[] cExcludes2;
        for (int length3 = (cExcludes2 = target.cExcludes).length, k = 0; k < length3; ++k) {
            final String cExclude = cExcludes2[k];
            cExcludes.append("\t\t<exclude name=\"" + cExclude + "\"/>\n");
        }
        final StringBuilder cppExcludes = new StringBuilder();
        String[] cppExcludes2;
        for (int length4 = (cppExcludes2 = target.cppExcludes).length, l = 0; l < length4; ++l) {
            final String cppExclude = cppExcludes2[l];
            cppExcludes.append("\t\t<exclude name=\"" + cppExclude + "\"/>\n");
        }
        final StringBuilder headerDirs = new StringBuilder();
        String[] headerDirs2;
        for (int length5 = (headerDirs2 = target.headerDirs).length, n = 0; n < length5; ++n) {
            final String headerDir = headerDirs2[n];
            headerDirs.append("\t\t\t<arg value=\"-I" + headerDir + "\"/>\n");
        }
        String targetFolder = target.osFileName;
        if (targetFolder == null) {
            targetFolder = String.valueOf(target.os.toString().toLowerCase()) + (target.is64Bit ? "64" : "32");
        }
        template = template.replace("%projectName%", String.valueOf(config.sharedLibName) + "-" + target.os + "-" + (target.is64Bit ? "64" : "32"));
        template = template.replace("%buildDir%", config.buildDir.child(targetFolder).path().replace('\\', '/'));
        template = template.replace("%libsDir%", "../" + this.getLibsDirectory(config, target));
        template = template.replace("%libName%", libName);
        template = template.replace("%jniPlatform%", jniPlatform);
        template = template.replace("%compilerPrefix%", target.compilerPrefix);
        template = template.replace("%cFlags%", target.cFlags);
        template = template.replace("%cppFlags%", target.cppFlags);
        template = template.replace("%linkerFlags%", target.linkerFlags);
        template = template.replace("%libraries%", target.libraries);
        template = template.replace("%cIncludes%", cIncludes);
        template = template.replace("%cExcludes%", cExcludes);
        template = template.replace("%cppIncludes%", cppIncludes);
        template = template.replace("%cppExcludes%", cppExcludes);
        template = template.replace("%headerDirs%", headerDirs);
        template = template.replace("%precompile%", (target.preCompileTask == null) ? "" : target.preCompileTask);
        template = template.replace("%postcompile%", (target.postCompileTask == null) ? "" : target.postCompileTask);
        return template;
    }
}
