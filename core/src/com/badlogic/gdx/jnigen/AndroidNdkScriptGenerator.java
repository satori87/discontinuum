// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.ArrayList;

public class AndroidNdkScriptGenerator
{
    public void generate(final BuildConfig config, final BuildTarget target) {
        if (target.os != BuildTarget.TargetOs.Android) {
            throw new IllegalArgumentException("target os must be Android");
        }
        if (!config.libsDir.exists() && !config.libsDir.mkdirs()) {
            throw new RuntimeException("Couldn't create directory for shared library files in '" + config.libsDir + "'");
        }
        if (!config.jniDir.exists() && !config.jniDir.mkdirs()) {
            throw new RuntimeException("Couldn't create native code directory '" + config.jniDir + "'");
        }
        final ArrayList<FileDescriptor> files = new ArrayList<FileDescriptor>();
        int idx = 0;
        final String[] includes = new String[target.cIncludes.length + target.cppIncludes.length];
        String[] cIncludes;
        for (int length = (cIncludes = target.cIncludes).length, j = 0; j < length; ++j) {
            final String include = cIncludes[j];
            includes[idx++] = config.jniDir + "/" + include;
        }
        String[] cppIncludes;
        for (int length2 = (cppIncludes = target.cppIncludes).length, k = 0; k < length2; ++k) {
            final String include = cppIncludes[k];
            includes[idx++] = config.jniDir + "/" + include;
        }
        idx = 0;
        final String[] excludes = new String[target.cExcludes.length + target.cppExcludes.length + 1];
        String[] cExcludes;
        for (int length3 = (cExcludes = target.cExcludes).length, l = 0; l < length3; ++l) {
            final String exclude = cExcludes[l];
            excludes[idx++] = config.jniDir + "/" + exclude;
        }
        String[] cppExcludes;
        for (int length4 = (cppExcludes = target.cppExcludes).length, n = 0; n < length4; ++n) {
            final String exclude = cppExcludes[n];
            excludes[idx++] = config.jniDir + "/" + exclude;
        }
        excludes[idx] = "**/target/*";
        this.gatherSourceFiles(config.jniDir, includes, excludes, files);
        final FileDescriptor application = config.jniDir.child("Application.mk");
        application.writeString(new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/Application.mk.template", FileDescriptor.FileType.Classpath).readString(), false);
        String template = new FileDescriptor("com/badlogic/gdx/jnigen/resources/scripts/Android.mk.template", FileDescriptor.FileType.Classpath).readString();
        final StringBuilder srcFiles = new StringBuilder();
        for (int i = 0; i < files.size(); ++i) {
            if (i > 0) {
                srcFiles.append("\t");
            }
            srcFiles.append(files.get(i).path().replace('\\', '/').replace(String.valueOf(config.jniDir.toString()) + "/", ""));
            if (i < files.size() - 1) {
                srcFiles.append("\\\n");
            }
            else {
                srcFiles.append("\n");
            }
        }
        final StringBuilder headerDirs = new StringBuilder();
        String[] headerDirs2;
        for (int length5 = (headerDirs2 = target.headerDirs).length, n2 = 0; n2 < length5; ++n2) {
            final String headerDir = headerDirs2[n2];
            headerDirs.append(headerDir);
            headerDirs.append(" ");
        }
        template = template.replace("%sharedLibName%", config.sharedLibName);
        template = template.replace("%headerDirs%", headerDirs);
        template = template.replace("%cFlags%", target.cFlags);
        template = template.replace("%cppFlags%", target.cppFlags);
        template = template.replace("%linkerFlags%", target.linkerFlags);
        template = template.replace("%srcFiles%", srcFiles);
        config.jniDir.child("Android.mk").writeString(template, false);
    }
    
    private void gatherSourceFiles(final FileDescriptor file, final String[] includes, final String[] excludes, final ArrayList<FileDescriptor> files) {
        final String fileName = file.path().replace('\\', '/');
        if (file.isDirectory()) {
            if (this.match(fileName, excludes)) {
                return;
            }
            FileDescriptor[] list;
            for (int length = (list = file.list()).length, i = 0; i < length; ++i) {
                final FileDescriptor child = list[i];
                this.gatherSourceFiles(child, includes, excludes, files);
            }
        }
        else if (this.match(fileName, includes) && !this.match(fileName, excludes)) {
            files.add(file);
        }
    }
    
    private boolean match(final String file, final String[] patterns) {
        return new AntPathMatcher().match(file, patterns);
    }
}
