// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.jnigen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class BuildExecutor
{
    public static boolean executeAnt(final String buildFile, final String params) {
        final FileDescriptor build = new FileDescriptor(buildFile);
        final String ant = System.getProperty("os.name").contains("Windows") ? "ant.bat" : "ant";
        final String command = String.valueOf(ant) + " -f \"" + build.file().getAbsolutePath() + "\" " + params;
        System.out.println("Executing '" + command + "'");
        return startProcess(command, build.parent().file());
    }
    
    public static void executeNdk(final String directory) {
        final FileDescriptor build = new FileDescriptor(directory);
        final String command = "ndk-build";
        startProcess(command, build.file());
    }
    
    private static boolean startProcess(final String command, final File directory) {
        try {
            final Process process = new ProcessBuilder(command.split(" ")).redirectErrorStream(true).start();
            final Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            this.printFileLineNumber(line);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                private void printFileLineNumber(final String line) {
                    if (!line.contains("warning")) {
                        if (!line.contains("error")) {
                            System.out.println(line);
                            return;
                        }
                    }
                    try {
                        final String fileName = this.getFileName(line);
                        final String error = this.getError(line);
                        final int lineNumber = this.getLineNumber(line) - 1;
                        if (fileName != null && lineNumber >= 0) {
                            final FileDescriptor file = new FileDescriptor(fileName);
                            if (file.exists()) {
                                final String[] content = file.readString().split("\n");
                                if (lineNumber < content.length) {
                                    for (int i = lineNumber; i >= 0; --i) {
                                        final String contentLine = content[i];
                                        if (contentLine.startsWith("//@line:")) {
                                            final int javaLineNumber = Integer.parseInt(contentLine.split(":")[1].trim());
                                            System.out.flush();
                                            if (line.contains("warning")) {
                                                System.out.println("(" + file.nameWithoutExtension() + ".java:" + (javaLineNumber + (lineNumber - i) - 1) + "): " + error + ", original: " + line);
                                                System.out.flush();
                                            }
                                            else {
                                                System.err.println("(" + file.nameWithoutExtension() + ".java:" + (javaLineNumber + (lineNumber - i) - 1) + "): " + error + ", original: " + line);
                                                System.err.flush();
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                            else {
                                System.out.println(line);
                            }
                        }
                    }
                    catch (Throwable t) {
                        System.out.println(line);
                    }
                }
                
                private String getFileName(final String line) {
                    final Pattern pattern = Pattern.compile("(.*):([0-9])+:[0-9]+:");
                    final Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    final String fileName = (matcher.groupCount() >= 2) ? matcher.group(1).trim() : null;
                    if (fileName == null) {
                        return null;
                    }
                    final int index = fileName.indexOf(" ");
                    if (index != -1) {
                        return fileName.substring(index).trim();
                    }
                    return fileName;
                }
                
                private String getError(final String line) {
                    final Pattern pattern = Pattern.compile(":[0-9]+:[0-9]+:(.+)");
                    final Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    return (matcher.groupCount() >= 1) ? matcher.group(1).trim() : null;
                }
                
                private int getLineNumber(final String line) {
                    final Pattern pattern = Pattern.compile(":([0-9]+):[0-9]+:");
                    final Matcher matcher = pattern.matcher(line);
                    matcher.find();
                    return (matcher.groupCount() >= 1) ? Integer.parseInt(matcher.group(1)) : -1;
                }
            });
            t.setDaemon(true);
            t.start();
            process.waitFor();
            return process.exitValue() == 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
